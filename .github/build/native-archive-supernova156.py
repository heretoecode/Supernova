#!/usr/bin/env python3
"""Verify pinned 0.146 artifacts; optionally preserve them on an immutable orphan branch.

No builds, workflow dispatches, or overwrites. Requires gh and GH_TOKEN when used
online. Tests exercise verification with synthetic archives without GitHub access.
"""
import argparse
import hashlib
import json
import subprocess
import shutil
import tempfile
import sys
import tarfile
import zipfile
from pathlib import Path, PurePosixPath

REPOSITORY = "heretoecode/Supernova"
RUN = 40843685431
BRANCH = "native-archive-0146"
MANIFEST = "archive-supernova156-manifest.json"
# Strictly below 100 MB (also below GitHub's 100 MiB limit).
MAX_FILE_BYTES = 100_000_000
REMOTE = "https://github.com/" + REPOSITORY + ".git"
ABIS = ("arm64-v8a", "armeabi-v7a", "x86", "x86_64")
ROOT = Path(__file__).resolve().parents[2]


def digest(path):
    with path.open("rb") as stream:
        return hashlib.file_digest(stream, "sha256").hexdigest()


def require(condition, message):
    if not condition:
        raise ValueError(message)


def gh(*arguments, output=None):
    result = subprocess.run(["gh", *arguments], stdout=output or subprocess.PIPE,
                            stderr=subprocess.PIPE, check=False)
    if result.returncode != 0:
        # gh never echoes the token; show which command failed and GitHub's reason.
        sys.stderr.write("gh " + " ".join(a for a in arguments if not a.startswith("--notes")) + " failed:\n"
                         + result.stderr.decode(errors="replace")[-2000:] + "\n")
    require(result.returncode == 0, "GitHub command failed; archive not complete")
    return result.stdout


def api(path):
    return json.loads(gh("api", f"repos/{REPOSITORY}/{path}"))


def download(path, target):
    # Artifact ZIPs only accept the JSON media type (GitHub answers 415 to octet-stream and redirects to the ZIP);
    # Release assets need octet-stream to return bytes instead of metadata.
    accept = "application/json" if path.startswith("actions/artifacts/") else "application/octet-stream"
    with target.open("wb") as stream:
        gh("api", "-H", f"Accept: {accept}", f"repos/{REPOSITORY}/{path}", output=stream)


def safe_path(value):
    path = PurePosixPath(value)
    require(bool(value) and not path.is_absolute() and ".." not in path.parts,
            "Unsafe archive path")
    return path


def verification_rows(review, lock_path):
    require(review["run_id"] == RUN, "Unexpected native run")
    rows = review["artifacts"]
    require(len(rows) == len(ABIS) and {row["abi"] for row in rows} == set(ABIS),
            "Expected exactly the four reviewed ABIs")
    require(all(row["lock_sha256"] == digest(lock_path) for row in rows),
            "Native lock differs from reviewed lock")
    require(len({row["native_source_commit"] for row in rows}) == 1,
            "Inconsistent native source provenance")
    return rows


def select_artifacts(metadata, rows):
    require(metadata["total_count"] == len(rows), "Unexpected artifact count")
    selected = []
    for row in rows:
        name = "supernova-0146-native-" + row["abi"]
        matches = [item for item in metadata["artifacts"] if item["name"] == name]
        require(len(matches) == 1, "Missing or duplicate ABI artifact")
        item = matches[0]
        require(not item["expired"], "Pinned artifact expired")
        require(item["workflow_run"]["id"] == RUN
                and item["workflow_run"]["head_sha"] == row["native_source_commit"],
                "Unexpected artifact provenance")
        require(item["digest"] == "sha256:" + row["artifact_sha256"],
                "Artifact API digest differs from review")
        selected.append((row, item))
    return selected


def verify_archive(artifact, destination, row, lock):
    require(digest(artifact) == row["artifact_sha256"], "Artifact ZIP SHA-256 mismatch")
    name = "Supernova_0.146_native_" + row["abi"] + ".tar.gz"
    with zipfile.ZipFile(artifact) as packed:
        members = [item for item in packed.infolist()
                   if PurePosixPath(item.filename).name == name]
        require(len(members) == 1, "Missing or duplicate native tarball")
        member = members[0]
        safe_path(member.filename)
        # Stream just the selected member; never extract ZIP paths to the host.
        with packed.open(member) as source, (destination / name).open("wb") as target:
            while chunk := source.read(1024 * 1024):
                target.write(chunk)
    archive = destination / name
    require(digest(archive) == row["archive_sha256"], "Native tarball SHA-256 mismatch")
    with tarfile.open(archive, "r:gz") as packed:
        names = [member.name for member in packed.getmembers()]
        require(len(names) == len(set(names)), "Duplicate tar member")
        for name in names:
            safe_path(name)
        receipt = json.load(packed.extractfile("native-stack.json"))
        require(receipt["abi"] == row["abi"]
                and receipt["source_commit"] == row["native_source_commit"]
                and receipt["native_lock_sha256"] == row["lock_sha256"]
                and receipt["recipes"] == lock["recipes"]
                and receipt["sources"] == lock["sources"], "Native receipt differs from lock/review")
        files = receipt["files"]
        require(len(files) == row["files"] and len({item["path"] for item in files}) == len(files),
                "Unexpected native file inventory")
        # tarfile follows archive links internally; nothing is unpacked or executed.
        for item in files:
            path = safe_path(item["path"])
            with packed.extractfile("prebuilt/" + str(path)) as source:
                contents = source.read()
            require(len(contents) == item["bytes"]
                    and hashlib.sha256(contents).hexdigest() == item["sha256"],
                    "Native installed file differs from receipt")
    return archive


def git(repo, *arguments, output=None, data=None):
    # gh obtains the job token from GH_TOKEN, without embedding it in URLs/config/logs.
    command = ["git", "-c", "credential.helper=", "-c", "credential.helper=!gh auth git-credential",
               "-C", str(repo), *map(str, arguments)]
    result = subprocess.run(command, input=data, stdout=output or subprocess.PIPE,
                            stderr=subprocess.PIPE, check=False)
    require(result.returncode == 0, "Git archive command failed: " + " ".join(map(str, arguments))
            + "\n" + result.stderr.decode(errors="replace")[-2000:])
    return result.stdout


def archive_names():
    return {"supernova-0146-native-" + abi + ".zip" for abi in ABIS} | {
        "native-supernova146.lock.json", "NATIVE_CI_ARTIFACT_VERIFICATION.json"}


def verify_manifest(directory, lock_path, review_path):
    manifest = json.loads((directory / MANIFEST).read_text())
    review = json.loads(review_path.read_text())
    rows = verification_rows(review, lock_path)
    require(manifest["schema"] == 2 and manifest["repository"] == REPOSITORY
            and manifest["run_id"] == RUN and manifest["branch"] == BRANCH
            and manifest["source_commit"] == rows[0]["native_source_commit"],
            "Archive manifest provenance differs")
    files = manifest["files"]
    require(len(files) == len(archive_names())
            and {item["name"] for item in files} == archive_names(), "Archive manifest inventory differs")
    require({path.name for path in directory.iterdir()} == archive_names() | {MANIFEST},
            "Archive branch file inventory differs")
    for item in files:
        path = directory / item["name"]
        require(path.is_file() and not path.is_symlink()
                and path.stat().st_size == item["bytes"] and digest(path) == item["sha256"],
                "Archive manifest hash/size mismatch")
    # The manifest is not its own trust anchor. Pin receipts and ZIPs to reviewed local inputs.
    for local in (lock_path, review_path):
        require((directory / local.name).read_bytes() == local.read_bytes(), "Archived lock/review differs")
    for row in rows:
        require(digest(directory / ("supernova-0146-native-" + row["abi"] + ".zip"))
                == row["artifact_sha256"], "Archived ZIP differs from reviewed hash")
    return manifest


def check_sizes(files):
    require(all(path.is_file() and not path.is_symlink()
                and path.stat().st_size < MAX_FILE_BYTES for path in files),
            "Archive files must be regular files strictly below 100 MB")


def fetch_branch(repo, destination, remote):
    # Fetch anew even after a push; verify only the fetched commit and its complete tree.
    git(repo, "fetch", "--no-tags", remote, "refs/heads/" + BRANCH)
    commit = git(repo, "rev-parse", "FETCH_HEAD").decode().strip()
    require(len(git(repo, "rev-list", "--parents", "-n", "1", commit).split()) == 1,
            "Archive must be one orphan commit")
    entries = git(repo, "ls-tree", "-rz", commit).split(b"\0")
    for entry in filter(None, entries):
        metadata, name = entry.split(b"\t", 1)
        path = safe_path(name.decode())
        require(len(path.parts) == 1 and metadata.split()[:2] == [b"100644", b"blob"],
                "Archive tree must contain only regular root files")
        with (destination / str(path)).open("wb") as stream:
            git(repo, "cat-file", "blob", metadata.split()[2].decode(), output=stream)
    return commit


def verify_same(files, downloaded):
    require({path.name for path in files} == {path.name for path in downloaded.iterdir()},
            "Existing archive branch inventory differs")
    for path in files:
        other = downloaded / path.name
        require(path.stat().st_size == other.stat().st_size and digest(path) == digest(other),
                "Existing archive branch bytes differ")


def publish(files, destination, remote=REMOTE):
    check_sizes(files)  # Before creating any Git object or making any remote write.
    with tempfile.TemporaryDirectory(prefix="native-archive-git-") as temporary:
        repo = Path(temporary)
        git(repo, "init", "--bare", "--quiet")
        existing = git(repo, "ls-remote", "--heads", remote, "refs/heads/" + BRANCH)
        if existing.strip():
            before = repo / "before"
            before.mkdir()
            fetch_branch(repo, before, remote)
            verify_same(files, before)
        else:
            entries = []
            for path in sorted(files):
                blob = git(repo, "hash-object", "-w", path.resolve()).decode().strip()
                entries.append("100644 blob " + blob + "\t" + path.name + "\n")
            tree = git(repo, "mktree", data="".join(entries).encode()).decode().strip()
            commit = git(repo, "-c", "user.name=Supernova native archive",
                         "-c", "user.email=native-archive@users.noreply.github.com",
                         "commit-tree", tree, data=b"Preserve verified native run 40843685431\n").decode().strip()
            # No force, no parent: concurrent creation with other bytes cannot be overwritten.
            git(repo, "push", remote, commit + ":refs/heads/" + BRANCH)
        after = repo / "after"
        after.mkdir()
        fetch_branch(repo, after, remote)
        verify_same(files, after)  # Includes manifest hash, not only payload hashes.
        verify_manifest(after, destination / "native-supernova146.lock.json",
                        destination / "NATIVE_CI_ARTIFACT_VERIFICATION.json")


def read_branch(destination, lock_path, review_path, remote=REMOTE):
    destination.mkdir(parents=True, exist_ok=False)
    with tempfile.TemporaryDirectory(prefix="native-archive-read-") as temporary:
        repo = Path(temporary)
        git(repo, "init", "--bare", "--quiet")
        fetch_branch(repo, destination, remote)
    check_sizes(list(destination.iterdir()))
    return verify_manifest(destination, lock_path, review_path)


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--destination", required=True, type=Path)
    parser.add_argument("--publish", action="store_true")
    parser.add_argument("--downloads", type=Path, help="Verify local original artifact ZIPs instead of downloading")
    args = parser.parse_args()
    destination = args.destination.resolve()
    destination.mkdir(parents=True, exist_ok=False)
    lock_path = ROOT / ".github/build/native-supernova146.lock.json"
    review_path = ROOT / "docs/0.146/NATIVE_CI_ARTIFACT_VERIFICATION.json"
    review = json.loads(review_path.read_text())
    lock = json.loads(lock_path.read_text())
    rows = verification_rows(review, lock_path)
    for name, expected in lock["recipes"].items():
        safe_path(name)
        require(digest(lock_path.parent / name) == expected, "Recipe differs from native lock")
    if args.downloads:
        selected = [(row, {"name": "supernova-0146-native-" + row["abi"]}) for row in rows]
    else:
        run = api(f"actions/runs/{RUN}")
        require(run["id"] == RUN and run["status"] == "completed" and run["conclusion"] == "success"
                and run["head_sha"] == rows[0]["native_source_commit"]
                and run["repository"]["full_name"] == REPOSITORY, "Unexpected native run provenance")
        selected = select_artifacts(api(f"actions/runs/{RUN}/artifacts?per_page=100"), rows)
    files = []
    # Tarballs are verified in scratch space; the immutable branch stores the original ZIPs only.
    with tempfile.TemporaryDirectory(prefix="native-archive-verify-") as temporary:
        for row, artifact in selected:
            path = destination / (artifact["name"] + ".zip")
            if args.downloads:
                shutil.copyfile(args.downloads / path.name, path)
            else:
                download(f"actions/artifacts/{artifact['id']}/zip", path)
            check_sizes([path])
            verify_archive(path, Path(temporary), row, lock)
            files.append(path)
    for source in (lock_path, review_path):
        target = destination / source.name
        target.write_bytes(source.read_bytes())
        files.append(target)
    manifest = destination / MANIFEST
    manifest.write_text(json.dumps({"schema": 2, "repository": REPOSITORY, "run_id": RUN,
        "source_commit": rows[0]["native_source_commit"], "branch": BRANCH,
        "files": [{"name": path.name, "bytes": path.stat().st_size, "sha256": digest(path)}
                  for path in sorted(files)]}, indent=2) + "\n")
    files.append(manifest)
    check_sizes(files)
    verify_manifest(destination, lock_path, review_path)
    if args.publish:
        publish(files, destination)
    print("Verified all four native ABIs" + (" and freshly fetched archive branch" if args.publish else " locally"))


if __name__ == "__main__":
    main()
