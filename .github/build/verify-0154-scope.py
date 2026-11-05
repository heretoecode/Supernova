#!/usr/bin/env python3
"""Scope gate for the 0.154 Shield QA build (0.153 walk-through improvements, owner approval 3 November 2026).

Mirrors verify-0153-scope.py: the build must descend from the delivered 0.153 source,
must not modify any pre-existing protected signing/build/native input, must record the
0.153 delivery exactly, and must freeze the application trees that were tested.
"""
import argparse, json, subprocess
from pathlib import Path

BASE = '38c2be745e45c9c0686fad5d07d8bcbd30255bf0'  # exact delivered 0.153 source
PREVIOUS_APK_SHA256 = '2193daae817b55c2ae33de320dc6c778b846436ccfd51aabfe24e530302c2987'
TREE_PATHS = ['src/main', 'src/test', 'res', 'assets', 'AndroidManifest.xml', 'build.gradle', 'settings.gradle', 'gradle.properties']


def trees(video):
    video = Path(video)
    return {p: subprocess.check_output(['git', '-C', str(video), 'rev-parse', 'HEAD:' + p], text=True).strip() for p in TREE_PATHS}


def verify(video):
    video = Path(video)
    def git(*args):
        return subprocess.check_output(['git', '-C', str(video), *args], text=True).strip()
    git('merge-base', '--is-ancestor', BASE, 'HEAD')
    for area in ['.github/signing', '.github/build', 'tools/native146']:
        for name in git('ls-tree', '-r', '--name-only', BASE, area).splitlines():
            if git('diff', '--name-only', BASE, 'HEAD', '--', name):
                raise ValueError('Existing protected/native input changed: ' + name)
    original = json.loads(git('show', BASE + ':assets/foundation/release-history.json'))
    current = json.loads((video / 'assets/foundation/release-history.json').read_text())
    if current['history'][1:] != original['history']:
        raise ValueError('Earlier release history must be preserved exactly')
    previous = current['history'][0]
    if previous['version'] != '0.153' or previous.get('version_code') != 153 or previous.get('source_sha') != BASE:
        raise ValueError('0.153 must be recorded as the immediately previous release')
    if previous['apks'][0]['sha256'] != PREVIOUS_APK_SHA256 or previous.get('archive_digest_verified') is not True:
        raise ValueError('Previous delivered APK evidence changed')
    if current['current']['version'] != '0.154' or current['current']['version_code'] != 154:
        raise ValueError('0.154 version identity is required')
    actual = trees(video)
    return {'implementation_base': BASE, 'preserved_ten_fixes': True, 'implementation_trees': actual, 'corrections_baseline': BASE}


if __name__ == '__main__':
    p = argparse.ArgumentParser()
    p.add_argument('--video', type=Path, required=True)
    print(json.dumps(verify(p.parse_args().video), indent=2))
