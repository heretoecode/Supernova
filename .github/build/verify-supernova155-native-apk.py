#!/usr/bin/env python3
"""0.155: every engine library in the reviewed arm64 engine receipt is packaged byte-for-byte; only arm64-v8a is packaged."""
import argparse, hashlib, json, zipfile
from pathlib import Path


def verify(apk, evidence):
    record = json.loads(Path(evidence).read_text())
    libraries = record["engine"]["libraries"]
    expected = {}
    for item in libraries:
        name = Path(item["path"]).name
        # The same DSO may ship in both MediaLib and FileCoreLibrary; Gradle packages one copy, so both must agree.
        if expected.setdefault(name, item["sha256"]) != item["sha256"]:
            raise ValueError("Engine receipt has two different builds of " + name)
    if not expected:
        raise ValueError("Engine receipt lists no libraries")
    with zipfile.ZipFile(apk) as archive:
        names = archive.namelist()
        if len(names) != len(set(names)) or archive.testzip() is not None:
            raise ValueError("Corrupt or duplicate APK entries")
        native = [n for n in names if n.startswith("lib/") and n.endswith(".so")]
        if {n.split("/")[1] for n in native} != {"arm64-v8a"}:
            raise ValueError("Only arm64-v8a engines may be packaged")
        packaged = {Path(n).name: n for n in native}
        for name, sha in expected.items():
            if name not in packaged:
                raise ValueError("Engine library missing from APK: " + name)
            if hashlib.sha256(archive.read(packaged[name])).hexdigest() != sha:
                raise ValueError("APK library differs from the reviewed engine build: " + name)
        others = sorted(set(packaged) - set(expected))
    return len(expected), others


if __name__ == "__main__":
    p = argparse.ArgumentParser(description=__doc__)
    p.add_argument("--apk", type=Path, required=True)
    p.add_argument("--evidence", type=Path, required=True)
    a = p.parse_args()
    count, others = verify(a.apk, a.evidence)
    print("Engine libraries matched byte-for-byte:", count)
    print("Other packaged libraries (from Java dependencies, not the engine):", ", ".join(others) or "none")
