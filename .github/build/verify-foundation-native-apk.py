#!/usr/bin/env python3
"""Require all rebuilt FFmpeg bytes in the actual APK before signing/publication."""
import argparse
import hashlib
import json
from pathlib import Path
import zipfile

ABIS = {'arm64-v8a', 'armeabi-v7a', 'x86', 'x86_64'}
LIBRARIES = {'libavcodec.so', 'libavformat.so', 'libavfilter.so', 'libavutil.so', 'libswresample.so', 'libswscale.so'}

def verify(apk, evidence):
    record = json.loads(Path(evidence).read_text())
    if record.get('source_commit') != '894da5ca7d742e4429ffb2af534fcda0103ef593' or set(record.get('abis', {})) != ABIS:
        raise ValueError('Verified four-ABI FFmpeg corresponding-source evidence required')
    count = 0
    with zipfile.ZipFile(apk) as packed:
        for abi, data in record['abis'].items():
            entries = data['libraries']
            if len(entries) != 6 or {entry['file'] for entry in entries} != LIBRARIES:
                raise ValueError('Native build evidence must cover all six FFmpeg libraries')
            for entry in entries:
                if hashlib.sha256(packed.read('lib/' + abi + '/' + entry['file'])).hexdigest() != entry['sha256']:
                    raise ValueError('APK contains stale or changed FFmpeg native bytes')
                count += 1
    return count

if __name__ == '__main__':
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument('--apk', type=Path, required=True)
    parser.add_argument('--evidence', type=Path, required=True)
    args = parser.parse_args()
    print('Actual APK rebuilt FFmpeg libraries matched:', verify(args.apk, args.evidence))
