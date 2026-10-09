#!/usr/bin/env python3
"""Only publish an APK whose package and certificate match approved Foundation identity."""
import argparse
import hashlib
import json
import os
from pathlib import Path
import re
import subprocess
import sys
import zipfile


def checked(command):
    result = subprocess.run(command, capture_output=True, text=True, check=False, timeout=120)
    if result.returncode:
        raise ValueError("APK verification tool failed; raw diagnostics suppressed.")
    return result.stdout


def verify(apk, apksigner, apkanalyzer, expected):
    if not re.fullmatch(r"[0-9a-f]{64}", expected):
        raise ValueError("Verified public Foundation certificate fingerprint required.")
    if expected == "89ac087ed6f989c90482d4a999f80511fe6ceee26ef1b9c37a142a9f00d39a5a":
        raise ValueError("Foundation must not reuse the Preview signing certificate.")
    output = checked([apksigner, "verify", "--verbose", "--print-certs", str(apk)])
    fingerprints = re.findall(r"Signer #\d+ certificate SHA-256 digest: ([0-9a-fA-F]{64})", output)
    if [s.lower() for s in fingerprints] != [expected]:
        raise ValueError("APK signer identity differs from verified Foundation certificate.")
    package = checked([apkanalyzer, "manifest", "application-id", str(apk)]).strip()
    if package != "app.supernova.player":
        raise ValueError("APK application ID is not app.supernova.player.")
    manifest = checked([apkanalyzer, "manifest", "print", str(apk)])
    old = ("browser.SearchProviderVideocommunity", "com.archos.media.videocommunity",
           "com.archos.media.scrapercommunity", "org.courville.nova.markpreview")
    if any(value in manifest for value in old):
        raise ValueError("Foundation APK still contains legacy/Preview manifest identities.")
    with zipfile.ZipFile(apk) as archive:
        for name in archive.namelist():
            if name.lower().endswith((".p12", ".pfx", ".jks", ".keystore", "keystore.properties")):
                raise ValueError("Unexpected signing material in APK; publication refused.")
    return {"application_id": package, "certificate_sha256": expected,
            "apk_sha256": hashlib.sha256(apk.read_bytes()).hexdigest()}


def main():
    parser = argparse.ArgumentParser()
    parser.add_argument("--directory", type=Path, required=True)
    parser.add_argument("--apksigner", required=True)
    parser.add_argument("--apkanalyzer", required=True)
    parser.add_argument("--publish", type=Path, required=True)
    parser.add_argument("--source", type=Path, required=True)
    args = parser.parse_args()
    try:
        apks = list(args.directory.glob("*.apk"))
        if len(apks) != 1:
            raise ValueError("Expected exactly one Foundation release APK.")
        evidence = verify(apks[0], args.apksigner, args.apkanalyzer,
                          os.environ.get("SUPERNOVA_CERT_SHA256", "").replace(":", "").strip().lower())
        import importlib.util
        spec = importlib.util.spec_from_file_location("foundation_conformance", args.source / ".github/build/verify-foundation.py")
        conformance = importlib.util.module_from_spec(spec)
        spec.loader.exec_module(conformance)
        evidence.update(conformance.binary(apks[0], args.apkanalyzer, args.source))
        evidence["source_commit"] = checked(["git", "-C", str(args.source), "rev-parse", "HEAD"]).strip()
        # Allowlisted artifacts only; no build/test logs, folders or signing config.
        args.publish.mkdir(exist_ok=False)
        import shutil
        shutil.copyfile(apks[0], args.publish / "Supernova-Foundation.apk")
        (args.publish / "public-build-evidence.json").write_text(json.dumps(evidence, indent=2) + "\n")
        print("Foundation APK package, signature and artifact allowlist verified.")
    except (ValueError, OSError, zipfile.BadZipFile, subprocess.TimeoutExpired) as failure:
        print(str(failure) if isinstance(failure, ValueError) else "APK verification failed safely.", file=sys.stderr)
        return 1
    return 0


if __name__ == "__main__":
    sys.exit(main())
