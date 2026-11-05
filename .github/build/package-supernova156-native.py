#!/usr/bin/env python3
"""Verify a reviewed engine artifact SHA and stage its DSOs into the app workspace."""
import argparse
import importlib.util
import json
import shutil
import tempfile
from pathlib import Path

spec = importlib.util.spec_from_file_location("native156", Path(__file__).with_name("native-supernova156.py"))
native = importlib.util.module_from_spec(spec)
spec.loader.exec_module(native)


def install(archive, expected_sha, source_commit, root, evidence):
    native.require(len(expected_sha) == 64 and native.sha(archive) == expected_sha,
                   "Native archive differs from reviewed SHA-256")
    lock = native.load_lock()
    with tempfile.TemporaryDirectory(prefix="native156-package-") as temporary:
        payload = Path(temporary)
        native.unpack(archive, payload)
        receipt = json.loads((payload / "native-stack.json").read_text())
        native.verify_payload(payload, receipt, lock, source_commit)
        # Never merge into stale ABI directories; old DSOs could otherwise survive.
        for module in ("MediaLib", "FileCoreLibrary"):
            destination = root / module / "libs" / native.ABI
            native.require(not destination.exists() or not any(destination.iterdir()),
                           "Empty destination ABI directory required: " + str(destination))
        for module in ("MediaLib", "FileCoreLibrary"):
            shutil.copytree(payload / "libraries" / module / "libs" / native.ABI,
                            root / module / "libs" / native.ABI, dirs_exist_ok=True)
        evidence.parent.mkdir(parents=True, exist_ok=True)
        evidence.write_text(json.dumps({"archive_sha256": expected_sha, "engine": receipt}, indent=2) + "\n")


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--root", required=True, type=Path)
    parser.add_argument("--archive", required=True, type=Path)
    parser.add_argument("--archive-sha256", required=True, help="Reviewed first-build archive.json SHA; never pending")
    parser.add_argument("--source-commit", required=True, help="Exact native workflow head SHA reviewed by Claude")
    parser.add_argument("--evidence", required=True, type=Path)
    args = parser.parse_args()
    install(args.archive, args.archive_sha256, args.source_commit, args.root.resolve(), args.evidence)


if __name__ == "__main__":
    main()
