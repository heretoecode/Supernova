#!/usr/bin/env python3
"""Reuse verified 0.146 third-party bytes; build pinned 0.156 avos/JNI for arm64."""
import argparse
import json
import os
import shutil
import tarfile
from pathlib import Path

# Filenames intentionally keep the release label; import without renaming them.
import importlib.util
spec = importlib.util.spec_from_file_location("native156", Path(__file__).with_name("native-supernova156.py"))
native = importlib.util.module_from_spec(spec)
spec.loader.exec_module(native)


def acquire_stack(destination, lock, source):
    archive = native.load_module("native-archive-supernova156")
    review = json.loads((native.VIDEO / "docs/0.146/NATIVE_CI_ARTIFACT_VERIFICATION.json").read_text())
    rows = archive.verification_rows(review, native.BUILD / "native-supernova146.lock.json")
    row = next(item for item in rows if item["abi"] == native.ABI)
    zip_path = destination / ("supernova-0146-native-" + native.ABI + ".zip")
    if source == "branch":
        branch_files = destination / "archive-branch"
        archive.read_branch(branch_files, native.BUILD / "native-supernova146.lock.json",
                            native.VIDEO / "docs/0.146/NATIVE_CI_ARTIFACT_VERIFICATION.json")
        shutil.copyfile(branch_files / zip_path.name, zip_path)
    elif source == "actions":
        selected = archive.select_artifacts(archive.api(f"actions/runs/{archive.RUN}/artifacts?per_page=100"), rows)
        artifact = next(item for candidate, item in selected if candidate["abi"] == native.ABI)
        archive.download(f"actions/artifacts/{artifact['id']}/zip", zip_path)
    else:
        raise ValueError("Explicit third-party source required")
    old = json.loads((native.BUILD / "native-supernova146.lock.json").read_text())
    tar_path = archive.verify_archive(zip_path, destination, row, old)
    # Verified prebuilt archives contain internal symlinks used by pkg-config.
    # The old verifier hashes every installed byte before this data-filter extraction.
    with tarfile.open(tar_path) as packed:
        packed.extractall(destination / "verified-stack", filter="data")
    return destination / "verified-stack/prebuilt", row


def build(root, ndk, jobs):
    native.require("Pkg.BaseRevision = 30.0.14904198" in (ndk / "source.properties").read_text(),
                   "Pinned NDK revision required")
    native.require((ndk / "toolchains/llvm/prebuilt/linux-x86_64/bin/clang").is_symlink(),
                   "NDK symlinks required")
    command = [ndk / "ndk-build", "NDK_APP_ABI=" + native.ABI, "APP_PLATFORM=android-23",
               "LIBAV_CONFIG=full", "BUILD=DEBUG", "-j" + str(jobs)]
    for module in ("native/libnativehelper", "FileCoreLibrary", "native/avos"):
        native.run(command, cwd=root / module, env=dict(os.environ, LIBAV_CONFIG="full", BUILD="DEBUG"))


def stage(root, output):
    abi = native.ABI
    media = output / "MediaLib/libs" / abi
    files = output / "FileCoreLibrary/libs" / abi
    media.mkdir(parents=True)
    files.mkdir(parents=True)
    for source in (root / "native/avos/libs" / abi, root / "native/libnativehelper/libs" / abi,
                   root / "native/prebuilt/ffmpeg" / ("dist-full-" + abi) / "lib"):
        for path in source.glob("*.so"):
            shutil.copyfile(path, media / path.name)
    for name in ("dav1d", "opus", "libmysofa"):
        filename = "lib" + name.removeprefix("lib") + ".so"
        shutil.copyfile(root / "native/prebuilt" / name / "lib" / abi / filename, media / filename)
    for path in (root / "FileCoreLibrary/libs" / abi).glob("*.so"):
        shutil.copyfile(path, files / path.name)
    native.require(any(files.glob("*.so")), "Missing FileCore JNI output")


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--workspace", type=Path, required=True)
    parser.add_argument("--third-party-source", choices=("branch", "actions"), default="branch")
    parser.add_argument("--jobs", type=int, default=4)
    parser.add_argument("--ndk", type=Path)
    args = parser.parse_args()
    native.require(args.jobs > 0, "Positive job count required")
    lock = native.load_lock()
    work = args.workspace.resolve()
    work.mkdir(parents=True, exist_ok=False)
    prebuilt, old_row = acquire_stack(work, lock, args.third_party_source)
    root = work / "root"
    root.mkdir()
    (root / "native").mkdir()
    shutil.move(str(prebuilt), root / "native/prebuilt")
    for module, source in lock["wrapper_sources"].items():
        native.checkout(source, root / module)
    native.checkout(lock["sources"]["avos"], root / "native/avos")
    native.apply_avos(root / "native/avos", lock)
    for item in lock["filecore_operations"]:
        patch = native.VIDEO / item["path"]
        native.require(native.sha(patch) == item["sha256"], "FileCore patch differs")
        native.run(["git", "apply", "--check", patch], cwd=root / "FileCoreLibrary")
        native.run(["git", "apply", "--index", patch], cwd=root / "FileCoreLibrary")
    if args.ndk:
        ndk = args.ndk.resolve()
    else:
        acquisition = native.load_module("acquire-supernova146-native")
        acquisition.download(lock["sources"]["third_party_native_stack"]["ndk"], work / "ndk.zip")
        native.run(["unzip", "-q", work / "ndk.zip", "-d", work / "toolchain"])
        ndk = work / "toolchain/android-ndk-r30-beta1"
    build(root, ndk, args.jobs)
    payload = work / "payload"
    stage(root, payload / "libraries")
    receipt = {"schema": 1, "abi": native.ABI, "source_commit": native.git(native.VIDEO, "rev-parse", "HEAD"),
               "native_lock_sha256": native.sha(native.BUILD / "native-supernova156.lock.json"),
               "avos_commit": lock["sources"]["avos"]["commit"], "avos_tree": lock["expected_patched_avos_tree"],
               "patches": lock["patches"], "recipe_hashes": lock["recipe_hashes"],
               "wrapper_sources": lock["wrapper_sources"], "filecore_operations": lock["filecore_operations"],
               "filecore_tree": native.git(root / "FileCoreLibrary", "write-tree"),
               "third_party_verification": old_row, "third_party_source": args.third_party_source,
               "ndk": lock["sources"]["third_party_native_stack"]["ndk"],
               "android_api": 23, "build": "DEBUG", "libraries": native.inventory(payload / "libraries"),
               "validation": "cross-built; APK/JNI/runtime/conformance/device acceptance pending"}
    native.verify_payload(payload, receipt, lock, receipt["source_commit"])
    (payload / "native-stack.json").write_text(json.dumps(receipt, indent=2) + "\n")
    delivery = work / "delivery"
    delivery.mkdir()
    archive = delivery / "Supernova_0.156_native_arm64-v8a.tar.gz"
    with tarfile.open(archive, "w:gz", compresslevel=1) as packed:
        for path in sorted(payload.rglob("*")):
            if path.is_file():
                packed.add(path, arcname=str(path.relative_to(payload)), recursive=False)
    shutil.copyfile(payload / "native-stack.json", delivery / "native-stack.json")
    shutil.copyfile(native.BUILD / "native-supernova156.lock.json", delivery / "native-supernova156.lock.json")
    (delivery / "archive.json").write_text(json.dumps({"file": archive.name, "sha256": native.sha(archive),
        "bytes": archive.stat().st_size, "source_commit": receipt["source_commit"]}, indent=2) + "\n")
    (delivery / "SHA256SUMS").write_text("".join(native.sha(path) + "  " + path.name + "\n"
        for path in sorted(delivery.iterdir()) if path.is_file()))
    print("Built new engine; third-party bytes verified against run 40843685431; see delivery receipts")


if __name__ == "__main__":
    main()
