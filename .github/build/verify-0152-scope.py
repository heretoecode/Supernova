#!/usr/bin/env python3
"""Scope gate for the 0.152 Shield QA build (owner-approved designs, 2 November 2026).

Mirrors verify-0151-scope.py: the build must descend from the delivered 0.151 source,
must not modify any pre-existing protected signing/build/native input, must record the
0.151 delivery exactly, and must freeze the application trees that were tested.
"""
import argparse, json, subprocess
from pathlib import Path

BASE = 'dfa3fafd8ef7581f10fed054ba15fc34ef2cb82a'  # exact delivered 0.151 source
PREVIOUS_APK_SHA256 = '0c62a04a12e82613449cc9c545cd9c4b8e727c15e7c762568190f30ca700be56'
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
    if previous['version'] != '0.151' or previous.get('version_code') != 151 or previous.get('source_sha') != BASE:
        raise ValueError('0.151 must be recorded as the immediately previous release')
    if previous['apks'][0]['sha256'] != PREVIOUS_APK_SHA256 or previous.get('archive_digest_verified') is not True:
        raise ValueError('Previous delivered APK evidence changed')
    if current['current']['version'] != '0.152' or current['current']['version_code'] != 152:
        raise ValueError('0.152 version identity is required')
    actual = trees(video)
    return {'implementation_base': BASE, 'preserved_ten_fixes': True, 'implementation_trees': actual, 'corrections_baseline': BASE}


if __name__ == '__main__':
    p = argparse.ArgumentParser()
    p.add_argument('--video', type=Path, required=True)
    print(json.dumps(verify(p.parse_args().video), indent=2))
