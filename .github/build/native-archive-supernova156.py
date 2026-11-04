#!/usr/bin/env python3
"""Draft: verify the pinned 0.146 artifacts, optionally preserve them in a Release.

No builds, workflow dispatches, or overwrites. Requires gh and GH_TOKEN when used
online. Tests exercise verification with synthetic archives without GitHub access.
"""
import argparse
import hashlib
import json
import subprocess
import tarfile
import zipfile
from pathlib import Path, PurePosixPath

REPOSITORY = "heretoecode/Supernova"
RUN = 40843685431
TAG = "native-archive-0146"
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
    require(result.returncode == 0, "GitHub command failed; archive not complete")
    return result.stdout


def api(path):
    return json.loads(gh("api", f"repos/{REPOSITORY}/{path}"))


def download(path, target):
    with target.open("wb") as stream:
        gh("api", "-H", "Accept: application/octet-stream",
           f"repos/{REPOSITORY}/{path}", output=stream)


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


def release_or_none():
    # A failed request must not be mistaken for an absent Release.
    releases = json.loads(gh("api", "--paginate", "--slurp",
                            f"repos/{REPOSITORY}/releases?per_page=100"))
    matches = [release for page in releases for release in page if release["tag_name"] == TAG]
    require(len(matches) <= 1, "Duplicate archive Release")
    return matches[0] if matches else None


def verify_tag(commit, release):
    matches = [reference for reference in api("git/matching-refs/tags/" + TAG)
               if reference["ref"] == "refs/tags/" + TAG]
    require(len(matches) <= 1, "Duplicate archive tag")
    if not matches:
        require(release["draft"] and release["target_commitish"] == commit,
                "Missing archive tag or unreviewed draft target")
        return
    reference = matches[0]["object"]
    while reference["type"] == "tag":
        reference = api("git/tags/" + reference["sha"])["object"]
    require(reference["type"] == "commit" and reference["sha"] == commit,
            "Archive tag must point to the reviewed native source commit")


def publish(files, commit, destination):
    release = release_or_none()
    if release is None:
        gh("release", "create", TAG, "--repo", REPOSITORY, "--target", commit,
           "--draft", "--latest=false", "--title", "Pinned 0.146 native archive",
           "--notes", "Verified preservation of native run 40843685431; not a new engine build. "
           "See archive-supernova156-manifest.json, the native lock and verification receipts.")
        release = release_or_none()
        require(release is not None and release["draft"], "Archive draft was not created")
    verify_tag(commit, release)
    assets = {asset["name"]: asset for asset in release["assets"]}
    # Check existing bytes before uploading anything to an existing Release.
    for path in files:
        if path.name in assets:
            downloaded = destination / (path.name + ".release-check")
            download(f"releases/assets/{assets[path.name]['id']}", downloaded)
            require(digest(downloaded) == digest(path), "Existing Release asset differs")
            downloaded.unlink()
    for path in files:
        if path.name not in assets:
            # No --clobber: concurrent changes or differing assets fail closed.
            gh("release", "upload", TAG, str(path), "--repo", REPOSITORY)
    release = release_or_none()
    assets = {asset["name"]: asset for asset in release["assets"]}
    for path in files:
        require(path.name in assets, "Release missing a required asset")
        downloaded = destination / (path.name + ".release-check")
        download(f"releases/assets/{assets[path.name]['id']}", downloaded)
        require(digest(downloaded) == digest(path), "Existing or uploaded Release asset differs")
        downloaded.unlink()
    verify_tag(commit, release)
    if release["draft"]:
        gh("api", "--method", "PATCH", f"repos/{REPOSITORY}/releases/{release['id']}",
           "-F", "draft=false", "-f", "make_latest=false")
    release = release_or_none()
    require(release is not None and not release["draft"], "Archive Release is not published")
    verify_tag(commit, release)


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--destination", required=True, type=Path)
    parser.add_argument("--publish", action="store_true")
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
    run = api(f"actions/runs/{RUN}")
    require(run["id"] == RUN and run["status"] == "completed" and run["conclusion"] == "success"
            and run["head_sha"] == rows[0]["native_source_commit"]
            and run["repository"]["full_name"] == REPOSITORY, "Unexpected native run provenance")
    selected = select_artifacts(api(f"actions/runs/{RUN}/artifacts?per_page=100"), rows)
    files = []
    for row, artifact in selected:
        path = destination / (artifact["name"] + ".zip")
        download(f"actions/artifacts/{artifact['id']}/zip", path)
        files.extend([path, verify_archive(path, destination, row, lock)])
    for source in (lock_path, review_path):
        target = destination / source.name
        target.write_bytes(source.read_bytes())
        files.append(target)
    manifest = destination / "archive-supernova156-manifest.json"
    manifest.write_text(json.dumps({"schema": 1, "repository": REPOSITORY, "run_id": RUN,
        "source_commit": rows[0]["native_source_commit"], "release_tag": TAG,
        "artifacts": [{"id": item["id"], "name": item["name"], "expires_at": item["expires_at"]}
                      for _, item in selected],
        "files": [{"name": path.name, "bytes": path.stat().st_size, "sha256": digest(path)}
                  for path in files]}, indent=2) + "\n")
    files.append(manifest)
    if args.publish:
        publish(files, rows[0]["native_source_commit"], destination)
    print("Verified all four native ABIs" + (" and Release assets" if args.publish else " locally"))


if __name__ == "__main__":
    main()
