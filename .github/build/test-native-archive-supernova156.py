"""Offline tests only: no workflow dispatch, artifact download or Release write."""
import copy
import hashlib
import importlib.util
import io
import json
import tarfile
import tempfile
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
        native = self.root / "Supernova_0.146_native_arm64-v8a.tar.gz"
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

    def test_existing_mismatched_release_asset_is_never_overwritten(self):
        asset = self.root / "asset.zip"
        asset.write_bytes(b"reviewed")
        missing = self.root / "missing.zip"
        missing.write_bytes(b"missing")
        release = {"assets": [{"name": asset.name, "id": 9}]}
        with patch.object(ARCHIVE, "release_or_none", return_value=release), \
                patch.object(ARCHIVE, "verify_tag"), \
                patch.object(ARCHIVE, "download", side_effect=lambda _, p: p.write_bytes(b"wrong")):
            with self.assertRaisesRegex(ValueError, "Existing Release asset differs"):
                ARCHIVE.publish([missing, asset], "c" * 40, self.destination)
        # The default gh mock raises if any upload/create/edit was attempted.

    def test_draft_missing_tag_needs_exact_reviewed_commit(self):
        with patch.object(ARCHIVE, "api", return_value=[]):
            ARCHIVE.verify_tag("c" * 40, {"draft": True, "target_commitish": "c" * 40})
            for release in [{"draft": True, "target_commitish": "main"}, {"draft": False}]:
                with self.assertRaises(ValueError):
                    ARCHIVE.verify_tag("c" * 40, release)

    def test_create_verify_publish_and_repeat_without_replacing_assets(self):
        asset = self.root / "asset.zip"
        asset.write_bytes(b"reviewed")
        remote = {}
        release = None
        calls = []

        def invoke(*arguments, **kwargs):
            nonlocal release
            calls.append(arguments)
            if arguments[:2] == ("release", "create"):
                release = {"id": 1, "draft": True, "target_commitish": "c" * 40, "assets": []}
            elif arguments[:2] == ("release", "upload"):
                source = Path(arguments[3])
                remote[9] = source.read_bytes()
                release["assets"].append({"id": 9, "name": source.name})
            elif arguments[:3] == ("api", "--method", "PATCH"):
                release["draft"] = False
            else:
                self.fail("Unexpected GitHub command")
            return b""

        def references(_):
            return [] if release["draft"] else [{"ref": "refs/tags/" + ARCHIVE.TAG,
                "object": {"type": "commit", "sha": "c" * 40}}]

        with patch.object(ARCHIVE, "gh", side_effect=invoke), \
                patch.object(ARCHIVE, "release_or_none", side_effect=lambda: copy.deepcopy(release)), \
                patch.object(ARCHIVE, "api", side_effect=references), \
                patch.object(ARCHIVE, "download", side_effect=lambda _, p: p.write_bytes(remote[9])):
            ARCHIVE.publish([asset], "c" * 40, self.destination)
            self.assertFalse(release["draft"])
            self.assertEqual(3, len(calls))
            ARCHIVE.publish([asset], "c" * 40, self.destination)
            self.assertEqual(3, len(calls))


if __name__ == "__main__":
    unittest.main()
