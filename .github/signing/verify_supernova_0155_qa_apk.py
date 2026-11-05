#!/usr/bin/env python3
"""Verify exact APK payload parity and existing signature before QA artifact upload."""
import argparse
import hashlib
import importlib.util
import json
import re
from pathlib import Path
import shutil
import subprocess
import sys
import zipfile
from verify_foundation_apk import verify
from supernova_0155_qa_signing import CERTIFICATE, gate


def payload_parity(unsigned, signed):
    def entries(path):
        with zipfile.ZipFile(path) as archive:
            names = archive.namelist()
            if len(names) != len(set(names)) or archive.testzip() is not None:
                raise ValueError('Duplicate or corrupt APK entries refused.')
            return {name: hashlib.sha256(archive.read(name)).hexdigest() for name in names
                    if not re.fullmatch(r'META-INF/(?:MANIFEST\.MF|[^/]+\.(?:SF|RSA|DSA|EC))', name, re.IGNORECASE)}
    if entries(unsigned) != entries(signed):
        raise ValueError('Signed APK payload differs from the conformance-tested unsigned APK.')


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    for name in ('apk', 'unsigned', 'evidence', 'source', 'publish'):
        parser.add_argument('--' + name, type=Path, required=True)
    for name in ('apksigner', 'apkanalyzer', 'aapt2'):
        parser.add_argument('--' + name, required=True)
    args = parser.parse_args()
    try:
        source = subprocess.check_output(['git', '-C', str(args.source), 'rev-parse', 'HEAD'], text=True).strip()
        unsigned_proof = json.loads(args.evidence.read_text())
        gate(unsigned_proof, args.unsigned, source)
        evidence = verify(args.apk, args.apksigner, args.apkanalyzer, CERTIFICATE)
        identity = {part: subprocess.run([args.apkanalyzer, 'manifest', part, str(args.apk)], capture_output=True,
                                         text=True, check=True, timeout=120).stdout.strip()
                    for part in ('application-id', 'version-name', 'version-code')}
        if identity != {'application-id': 'app.supernova.player', 'version-name': '0.155', 'version-code': '155'}:
            raise ValueError('Signed APK identity/version differs from 0.155.')
        evidence.update(application_id=identity['application-id'], version_name='0.155', version_code=155)
        payload_parity(args.unsigned, args.apk)
        evidence.update(source_commit=source, signed=True, qa_only=True, final_release_published=False,
                        unsigned_apk_sha256=unsigned_proof['apk_sha256'], payload_matches_tested_unsigned=True,
                        unsigned_conformance=unsigned_proof, physical_shield_verified=False,
                        signed_upgrade_verified=False)
        args.publish.mkdir(exist_ok=False)
        shutil.copyfile(args.apk, args.publish / 'Supernova_0.155.apk')
        (args.publish / 'public-qa-evidence.json').write_text(json.dumps(evidence, indent=2) + '\n')
        print('QA APK signature, identity, version and tested payload parity verified.')
        return 0
    except (ValueError, OSError, zipfile.BadZipFile, subprocess.SubprocessError):
        print('QA APK verification failed safely; no artifact authorized for upload.', file=sys.stderr)
        return 1


if __name__ == '__main__':
    sys.exit(main())
