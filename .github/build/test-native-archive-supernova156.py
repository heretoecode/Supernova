"""Offline tests only: no workflow dispatch, artifact download or remote write."""
import copy
import hashlib
import importlib.util
import io
import json
import tarfile
import tempfile
import subprocess
import sys
import unittest
import zipfile
from pathlib import Path
from unittest.mock import patch

SPEC = importlib.util.spec_from_file_location(
    "native_archive", Path(__file__).with_name("native-archive-supernova156.py"))
ARCHIVE = importlib.util.module_from_spec(SPEC)
SPEC.loader.exec_module(ARCHIVE)


class NativeArchiveTest(unittest.TestCase):
    def setUp(self):
        self.directory = tempfile.TemporaryDirectory()
        self.addCleanup(self.directory.cleanup)
        self.root = Path(self.directory.name)
        self.destination = self.root / "verified"
        self.destination.mkdir()
        self.lock = {"recipes": {"recipe": "digest"}, "sources": {"source": "pinned"}}
        self.row = {"abi": "arm64-v8a", "native_source_commit": "c" * 40,
                    "lock_sha256": "a" * 64, "files": 1}
        self.receipt = {"abi": self.row["abi"], "source_commit": self.row["native_source_commit"],
                        "native_lock_sha256": self.row["lock_sha256"], **self.lock,
                        "files": [{"path": "lib/example.so", "bytes": 6,
                                   "sha256": hashlib.sha256(b"native").hexdigest()}]}
        # Any unintended online operation fails the test.
        self.no_network = patch.object(ARCHIVE, "gh", side_effect=AssertionError("Online operation"))
        self.no_network.start()
        self.addCleanup(self.no_network.stop)

    def fixture(self, duplicate=False, unsafe=False):
        native = self.root / ("Supernova_0.146_native_" + self.row["abi"] + ".tar.gz")
        with tarfile.open(native, "w:gz") as packed:
            for name, value in [("native-stack.json", json.dumps(self.receipt).encode()),
                                ("prebuilt/lib/example.so", b"native")]:
                member = tarfile.TarInfo(name)
                member.size = len(value)
                packed.addfile(member, io.BytesIO(value))
            if unsafe:
                packed.addfile(tarfile.TarInfo("../outside"))
        self.row["archive_sha256"] = ARCHIVE.digest(native)
        artifact = self.root / "artifact.zip"
        with zipfile.ZipFile(artifact, "w") as packed:
            packed.write(native, native.name)
            if duplicate:
                packed.write(native, "other/" + native.name)
        self.row["artifact_sha256"] = ARCHIVE.digest(artifact)
        return artifact

    def test_verifies_zip_tar_receipt_and_installed_file(self):
        result = ARCHIVE.verify_archive(self.fixture(), self.destination, self.row, self.lock)
        self.assertEqual(self.row["archive_sha256"], ARCHIVE.digest(result))
        self.assertEqual([result], list(self.destination.iterdir()))

    def test_corrupt_zip_is_rejected_before_extraction(self):
        artifact = self.fixture()
        artifact.write_bytes(artifact.read_bytes() + b"tampered")
        with self.assertRaisesRegex(ValueError, "ZIP SHA-256"):
            ARCHIVE.verify_archive(artifact, self.destination, self.row, self.lock)
        self.assertEqual([], list(self.destination.iterdir()))

    def test_tar_digest_is_independently_checked(self):
        artifact = self.fixture()
        self.row["archive_sha256"] = "0" * 64
        with self.assertRaisesRegex(ValueError, "tarball SHA-256"):
            ARCHIVE.verify_archive(artifact, self.destination, self.row, self.lock)

    def test_duplicate_tarball_and_unsafe_paths_are_rejected(self):
        with self.assertRaisesRegex(ValueError, "duplicate native tarball"):
            ARCHIVE.verify_archive(self.fixture(duplicate=True), self.destination, self.row, self.lock)
        with self.assertRaisesRegex(ValueError, "Unsafe archive path"):
            ARCHIVE.verify_archive(self.fixture(unsafe=True), self.destination, self.row, self.lock)
        self.assertFalse((self.root / "outside").exists())

    def test_receipt_provenance_and_file_hash_are_checked(self):
        original = copy.deepcopy(self.receipt)
        for key in ["abi", "source_commit", "native_lock_sha256", "recipes", "sources"]:
            with self.subTest(key=key):
                self.receipt = copy.deepcopy(original)
                self.receipt[key] = "wrong"
                with self.assertRaisesRegex(ValueError, "receipt differs"):
                    ARCHIVE.verify_archive(self.fixture(), self.destination, self.row, self.lock)
        self.receipt = original
        self.receipt["files"][0]["sha256"] = "0" * 64
        with self.assertRaisesRegex(ValueError, "installed file differs"):
            ARCHIVE.verify_archive(self.fixture(), self.destination, self.row, self.lock)

    def metadata(self):
        rows = [{**self.row, "abi": abi, "artifact_sha256": "b" * 64} for abi in ARCHIVE.ABIS]
        items = [{"id": index, "name": "supernova-0146-native-" + row["abi"],
                  "expired": False, "digest": "sha256:" + row["artifact_sha256"],
                  "workflow_run": {"id": ARCHIVE.RUN, "head_sha": row["native_source_commit"]}}
                 for index, row in enumerate(rows)]
        return rows, {"total_count": 4, "artifacts": items}

    def test_missing_expired_duplicate_or_unreviewed_artifacts_fail_closed(self):
        rows, metadata = self.metadata()
        self.assertEqual(4, len(ARCHIVE.select_artifacts(metadata, rows)))
        for key, value in [("expired", True), ("digest", "sha256:wrong"),
                           ("name", "supernova-0146-native-x86"),
                           ("workflow_run", {"id": 1, "head_sha": "wrong"})]:
            with self.subTest(key=key):
                modified = copy.deepcopy(metadata)
                modified["artifacts"][0][key] = value
                with self.assertRaises(ValueError):
                    ARCHIVE.select_artifacts(modified, rows)
        metadata["artifacts"].pop()
        with self.assertRaises(ValueError):
            ARCHIVE.select_artifacts(metadata, rows)

    def branch_fixture(self):
        lock = self.destination / "native-supernova146.lock.json"
        lock.write_text(json.dumps(self.lock))
        rows = []
        for abi in ARCHIVE.ABIS:
            path = self.destination / ("supernova-0146-native-" + abi + ".zip")
            path.write_bytes(("synthetic ZIP " + abi).encode())
            rows.append({**self.row, "abi": abi, "artifact_sha256": ARCHIVE.digest(path),
                         "lock_sha256": ARCHIVE.digest(lock)})
        review = self.destination / "NATIVE_CI_ARTIFACT_VERIFICATION.json"
        review.write_text(json.dumps({"run_id": ARCHIVE.RUN, "artifacts": rows}))
        files = sorted(self.destination.iterdir())
        manifest = self.destination / ARCHIVE.MANIFEST
        manifest.write_text(json.dumps({"schema": 2, "repository": ARCHIVE.REPOSITORY,
            "run_id": ARCHIVE.RUN, "branch": ARCHIVE.BRANCH,
            "source_commit": self.row["native_source_commit"],
            "files": [{"name": path.name, "bytes": path.stat().st_size, "sha256": ARCHIVE.digest(path)}
                      for path in files]}))
        files.append(manifest)
        remote = self.root / "remote.git"
        subprocess.run(["git", "init", "--bare", "--quiet", str(remote)], check=True)
        return files, remote, lock, review

    def test_oversize_fails_before_remote_access(self):
        files, remote, _, _ = self.branch_fixture()
        with patch.object(ARCHIVE, "MAX_FILE_BYTES", files[0].stat().st_size), \
                patch.object(ARCHIVE, "git", side_effect=AssertionError("must fail before git")):
            with self.assertRaisesRegex(ValueError, "below 100 MB"):
                ARCHIVE.publish(files, self.destination, str(remote))

    def test_publish_and_existing_identical_branch_are_immutable(self):
        files, remote, lock, review = self.branch_fixture()
        ARCHIVE.publish(files, self.destination, str(remote))
        head = ARCHIVE.git(remote, "rev-parse", "refs/heads/" + ARCHIVE.BRANCH)
        # Existing branch must never be pushed, even with identical bytes.
        original = ARCHIVE.git
        def no_push(repo, *args, **kwargs):
            self.assertNotIn("push", args)
            return original(repo, *args, **kwargs)
        with patch.object(ARCHIVE, "git", side_effect=no_push):
            ARCHIVE.publish(files, self.destination, str(remote))
        self.assertEqual(head, ARCHIVE.git(remote, "rev-parse", "refs/heads/" + ARCHIVE.BRANCH))
        self.assertEqual(b"1\n", ARCHIVE.git(remote, "rev-list", "--count", head.decode().strip()))
        downloaded = self.root / "downloaded"
        ARCHIVE.read_branch(downloaded, lock, review, str(remote))
        ARCHIVE.verify_same(files, downloaded)

    def test_existing_different_branch_is_never_rewritten(self):
        files, remote, _, _ = self.branch_fixture()
        ARCHIVE.publish(files, self.destination, str(remote))
        head = ARCHIVE.git(remote, "rev-parse", "refs/heads/" + ARCHIVE.BRANCH)
        files[0].write_bytes(b"different bytes")
        with self.assertRaisesRegex(ValueError, "branch bytes differ"):
            ARCHIVE.publish(files, self.destination, str(remote))
        self.assertEqual(head, ARCHIVE.git(remote, "rev-parse", "refs/heads/" + ARCHIVE.BRANCH))

    def test_manifest_hash_mismatch_and_changed_trust_anchor_fail(self):
        files, _, lock, review = self.branch_fixture()
        ARCHIVE.verify_manifest(self.destination, lock, review)
        artifact = self.destination / "supernova-0146-native-x86.zip"
        original = artifact.read_bytes()
        artifact.write_bytes(b"tampered")
        with self.assertRaisesRegex(ValueError, "hash/size mismatch"):
            ARCHIVE.verify_manifest(self.destination, lock, review)
        # Even an attacker-updated manifest cannot replace the locally reviewed ZIP hash.
        manifest_path = self.destination / ARCHIVE.MANIFEST
        manifest = json.loads(manifest_path.read_text())
        for item in manifest["files"]:
            if item["name"] == artifact.name:
                item.update(bytes=artifact.stat().st_size, sha256=ARCHIVE.digest(artifact))
        manifest_path.write_text(json.dumps(manifest))
        with self.assertRaisesRegex(ValueError, "reviewed hash"):
            ARCHIVE.verify_manifest(self.destination, lock, review)

    def test_fresh_fetch_detects_post_push_corruption(self):
        files, remote, _, _ = self.branch_fixture()
        original = ARCHIVE.fetch_branch
        def corrupt(repo, destination, source):
            commit = original(repo, destination, source)
            (destination / ARCHIVE.MANIFEST).write_bytes(b"corrupt")
            return commit
        with patch.object(ARCHIVE, "fetch_branch", side_effect=corrupt):
            with self.assertRaisesRegex(ValueError, "branch bytes differ"):
                ARCHIVE.publish(files, self.destination, str(remote))

    def test_branch_with_history_is_rejected(self):
        files, remote, lock, review = self.branch_fixture()
        ARCHIVE.publish(files, self.destination, str(remote))
        first = ARCHIVE.git(remote, "rev-parse", "refs/heads/" + ARCHIVE.BRANCH).decode().strip()
        tree = ARCHIVE.git(remote, "rev-parse", first + "^{tree}").decode().strip()
        second = ARCHIVE.git(remote, "-c", "user.name=Test", "-c", "user.email=test@example.org",
                             "commit-tree", tree, "-p", first, data=b"extra commit").decode().strip()
        ARCHIVE.git(remote, "update-ref", "refs/heads/" + ARCHIVE.BRANCH, second)
        with self.assertRaisesRegex(ValueError, "one orphan commit"):
            ARCHIVE.read_branch(self.root / "read", lock, review, str(remote))

    def test_branch_with_extra_directory_is_rejected(self):
        files, remote, lock, review = self.branch_fixture()
        ARCHIVE.publish(files, self.destination, str(remote))
        head = ARCHIVE.git(remote, "rev-parse", "refs/heads/" + ARCHIVE.BRANCH).decode().strip()
        entries = ARCHIVE.git(remote, "ls-tree", head)
        empty = ARCHIVE.git(remote, "mktree", data=b"").decode().strip()
        tree = ARCHIVE.git(remote, "mktree", data=entries + ("040000 tree " + empty + "\textra\n").encode()).decode().strip()
        commit = ARCHIVE.git(remote, "-c", "user.name=Test", "-c", "user.email=test@example.org",
                             "commit-tree", tree, data=b"unexpected tree").decode().strip()
        ARCHIVE.git(remote, "update-ref", "refs/heads/" + ARCHIVE.BRANCH, commit)
        with self.assertRaisesRegex(ValueError, "inventory differs"):
            ARCHIVE.read_branch(self.root / "read", lock, review, str(remote))

    def test_downloads_mode_verifies_all_abis_without_online_access(self):
        # Exercise the actual CLI preparation path with complete synthetic ZIP/tar receipts.
        source = self.root / "source"
        build = source / ".github/build"
        review_path = source / "docs/0.146/NATIVE_CI_ARTIFACT_VERIFICATION.json"
        build.mkdir(parents=True)
        review_path.parent.mkdir(parents=True)
        (build / "recipe").write_bytes(b"pinned recipe")
        self.lock["recipes"] = {"recipe": ARCHIVE.digest(build / "recipe")}
        lock_path = build / "native-supernova146.lock.json"
        lock_path.write_text(json.dumps(self.lock))
        self.row["lock_sha256"] = ARCHIVE.digest(lock_path)
        self.receipt.update(self.lock, native_lock_sha256=self.row["lock_sha256"])
        downloads = self.root / "downloads"
        downloads.mkdir()
        rows = []
        for abi in ARCHIVE.ABIS:
            self.row["abi"] = self.receipt["abi"] = abi
            zip_path = self.fixture()
            (downloads / ("supernova-0146-native-" + abi + ".zip")).write_bytes(zip_path.read_bytes())
            rows.append(dict(self.row))
        review_path.write_text(json.dumps({"run_id": ARCHIVE.RUN, "artifacts": rows}))
        destination = self.root / "cli-output"
        with patch.object(ARCHIVE, "ROOT", source), patch.object(sys, "argv", ["archive",
                "--downloads", str(downloads), "--destination", str(destination)]):
            ARCHIVE.main()
        self.assertEqual(7, len(list(destination.iterdir())))
        ARCHIVE.verify_manifest(destination, lock_path, review_path)

    def test_artifact_download_keeps_claude_json_accept_fix(self):
        with patch.object(ARCHIVE, "gh") as gh:
            ARCHIVE.download("actions/artifacts/1/zip", self.root / "download")
        self.assertIn("Accept: application/json", gh.call_args.args)

    def test_workflow_triggers_are_limited(self):
        workflow = (ARCHIVE.ROOT / ".github/workflows/supernova-0156-native-archive.yml").read_text()
        trigger = workflow.split("on:\n", 1)[1].split("permissions:", 1)[0]
        self.assertIn("workflow_dispatch:", trigger)
        self.assertIn("branches: [claude/supernova-github-access-yfhe89]", trigger)
        self.assertIn("paths: [.github/workflows/supernova-0156-native-archive.yml]", trigger)


if __name__ == "__main__":
    unittest.main()
