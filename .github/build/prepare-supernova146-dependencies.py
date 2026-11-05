#!/usr/bin/env python3
"""Reproduce the preserved 0.145 tree plus reviewed 0.146 source integrations."""
import argparse
import hashlib
import json
from pathlib import Path
import subprocess
import xml.etree.ElementTree as ET


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument('--root', type=Path, required=True)
    args = parser.parse_args()
    root = args.root.resolve()
    video = root / 'Video'
    lock = json.loads((video / '.github/build/supernova146-dependency-patches.json').read_text())
    pins = {p.get('path'): p.get('revision') for p in
            ET.parse(video / '.github/build/nova-ci.xml').getroot().findall('project')}
    modules = {op['module'] for op in lock['operations'] if op['kind'] == 'patch'}
    for module in sorted(modules):
        sha = subprocess.check_output(['git', '-C', str(root / module), 'rev-parse', 'HEAD'], text=True).strip()
        if sha != pins[module]:
            raise ValueError('Unexpected dependency source revision: ' + module)
        if subprocess.check_output(['git', '-C', str(root / module), 'status', '--porcelain'], text=True).strip():
            raise ValueError('Dependency workspace must be fresh: ' + module)
    receipts = []
    for op in lock['operations']:
        if op['kind'] == 'foundation_identity':
            subprocess.run(['python3', str(video / '.github/build/prepare-foundation.py'), str(root)], check=True)
            continue
        patch = video / op['path']
        if hashlib.sha256(patch.read_bytes()).hexdigest() != op['sha256']:
            raise ValueError('Source patch changed without an updated review lock: ' + op['path'])
        subprocess.run(['git', '-C', str(root / op['module']), 'apply', '--check', str(patch)], check=True)
        subprocess.run(['git', '-C', str(root / op['module']), 'apply', str(patch)], check=True)
        receipts.append(op)
    report = {'schema': 1, 'patches': receipts,
              'video_source': subprocess.check_output(['git', '-C', str(video), 'rev-parse', 'HEAD'], text=True).strip()}
    output = root / 'supernova146-source-preparation.json'
    output.write_text(json.dumps(report, indent=2) + '\n')
    print('Preserved baseline and reviewed upstream dependency patches applied with exact hashes.')


if __name__ == '__main__':
    main()
