#!/usr/bin/env python3
"""Prepare the existing key only after exact-source unsigned QA conformance."""
import argparse
import hashlib
import json
import os
from pathlib import Path
import re
import sys
import foundation_signing as key

CERTIFICATE = '79ed34c52c3e359756ade0634d7bbb6f8e94e42a059020c1220bd8c7092a9f5e'
MINIMUM_TESTS = 1300
# Engine-release regression classes that must have run (and, with zero failures overall, passed).
REQUIRED_TEST_CLASSES = {'PlayerHdrProducerContractTest', 'PlaybackHdrCapabilitiesTest', 'VideoCodecSelectionTest',
                         'SubtitlePaddingPlumbingTest', 'SurfaceControllerOverflowTest', 'SubtitleManagerDefaultsTest',
                         'SupernovaStyleTest', 'LocalStorageViewTest', 'Supernova154UiTest'}
ENGINE = {'run': 41848351713, 'source_commit': 'a8c82ae523533e8dc92cc30c203212f08a491148',
          'archive_sha256': '42e1990bc5e584747242ecffc8d83d1244f55b9babf72ebeaa3a4be272d2e738'}


def gate(evidence, apk, source):
    if not re.fullmatch(r'[0-9a-f]{40}', source or '') or evidence.get('source_commit') != source:
        raise key.SigningError('QA conformance source differs from the checked-out commit.')
    if evidence.get('application_id') != 'app.supernova.player' or evidence.get('version_name') != '0.155' or evidence.get('version_code') != 155:
        raise key.SigningError('QA application identity/version differs from the approved scope.')
    if evidence.get('signed') is not False:
        raise key.SigningError('QA signing requires the verified unsigned APK.')
    if evidence.get('engine') != ENGINE or 'Engine libraries matched byte-for-byte:' not in evidence.get('engine_apk_check', ''):
        raise key.SigningError('The reviewed engine and its byte-for-byte APK check are required.')
    unit = evidence.get('unit', {})
    if unit.get('tests', 0) < MINIMUM_TESTS or any(unit.get(k) != 0 for k in ('failures', 'errors')) or evidence.get('lint_errors') != 0:
        raise key.SigningError('QA signing requires the complete passing suite and release lint.')
    if not REQUIRED_TEST_CLASSES <= set(evidence.get('unit_classes', [])):
        raise key.SigningError('All required 0.155 regression classes must run.')
    tools = evidence.get('tooling', {})
    if tools.get('tests', 0) < 156 or any(tools.get(k) != 0 for k in ('failures', 'errors')):
        raise key.SigningError('Complete passing tooling suite is required.')
    for module, minimum in [('MediaLib', 200), ('FileCoreLibrary', 104)]:
        proof = evidence.get('library_suites', {}).get(module, {})
        if proof.get('tests', 0) < minimum or proof.get('failures') != 0 or proof.get('errors') != 0:
            raise key.SigningError('Complete reviewed library suites are required.')
    if hashlib.sha256(apk.read_bytes()).hexdigest() != evidence.get('apk_sha256') or apk.stat().st_size != evidence.get('apk_bytes'):
        raise key.SigningError('Unsigned APK changed after conformance.')


def prepare(evidence_path, apk, env=None):
    env = dict(os.environ if env is None else env)
    gate(json.loads(evidence_path.read_text()), apk, env.get('SUPERNOVA_QA_SOURCE'))
    if env.get('GITHUB_REF') != 'refs/heads/claude/supernova-github-access-yfhe89':
        raise key.SigningError('QA signing is restricted to the isolated 0.155 branch.')
    if env.get('SUPERNOVA_CERT_SHA256') != CERTIFICATE:
        raise key.SigningError('QA certificate pin differs from the verified installed-release baseline.')
    key.preflight('validate', env)
    data = key.decode_keystore(env['SUPERNOVA_P12_BASE64'])
    directory = key.store_path(env)
    if directory.exists() or directory.is_symlink():
        raise key.SigningError('Temporary signing directory already exists. Refusing reuse.')
    directory.mkdir(mode=0o700)
    keep = False
    try:
        keystore = directory / 'foundation.p12'
        fd = os.open(keystore, os.O_WRONLY | os.O_CREAT | os.O_EXCL | os.O_NOFOLLOW, 0o600)
        with os.fdopen(fd, 'wb') as stream:
            stream.write(data)
        key.verify('validate', keystore, env)
        keep = True
    finally:
        if not keep:
            key.remove_store(directory)


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument('--evidence', type=Path, required=True)
    parser.add_argument('--apk', type=Path, required=True)
    args = parser.parse_args()
    try:
        prepare(args.evidence, args.apk)
        print('Exact-source unsigned conformance and existing QA certificate verified; temporary key prepared.')
        return 0
    except (key.SigningError, OSError, ValueError, TypeError):
        print('QA key preparation refused; inspect the public conformance gates and existing secret configuration privately.', file=sys.stderr)
        return 1


if __name__ == '__main__':
    sys.exit(main())
