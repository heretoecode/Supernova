#!/usr/bin/env python3
"""Verify current resolved runtime archives against the existing offline legal catalogue.

This is a source/runtime-artifact gate. It cannot establish final APK/native/R8
coverage; run the established binary gates against the actual candidate as well.
No cache paths, provider credentials or source archive contents enter the report.
"""
import argparse
from collections import Counter
import hashlib
import json
from pathlib import Path
import re
import zipfile

ROOT = Path(__file__).resolve().parents[1]


def digest(path):
    return hashlib.sha256(path.read_bytes()).hexdigest()


def verify(inventory):
    resolved = json.loads(Path(inventory).read_text())
    sbom = json.loads((ROOT / 'docs/foundation/RUNTIME_SBOM.json').read_text())
    catalogue = json.loads((ROOT / 'assets/foundation/licences.json').read_text())['components']
    names = [item['name'] for item in catalogue]
    if len(names) != len(set(names)):
        raise ValueError('Duplicate legal catalogue component')
    texts = {}
    assets = []
    for item in catalogue:
        if not item['url'].startswith('https://') or not item['licence'] or not item['role']:
            raise ValueError('Incomplete attribution: ' + item['name'])
        asset = item['text_asset']
        if not re.fullmatch(r'foundation/legal/[a-z0-9-]+\.txt', asset):
            raise ValueError('Invalid notice asset')
        path = ROOT / 'assets' / asset
        text = path.read_text(encoding='utf-8')
        if len(text.strip()) < 500:
            raise ValueError('Incomplete offline notice: ' + item['name'])
        texts[item['name']] = text
        assets.append({'component': item['name'], 'asset': asset, 'bytes': path.stat().st_size, 'sha256': digest(path)})
    expected = {item['coordinate']: item for item in sbom['artifacts']}
    actual = {item['coordinate']: item for item in resolved}
    # Gradle can expose the same AAR through multiple variants. Preserve occurrence
    # counts and refuse divergent bytes rather than silently dropping duplicates.
    if Counter(item['coordinate'] for item in sbom['artifacts']) != Counter(item['coordinate'] for item in resolved):
        raise ValueError('Runtime artifact occurrence counts differ')
    for item in sbom['artifacts']:
        if item != expected[item['coordinate']]:
            raise ValueError('Divergent duplicate SBOM coordinate: ' + item['coordinate'])
    for item in resolved:
        other = actual[item['coordinate']]
        if item['configuration'] != other['configuration'] or digest(Path(item['file'])) != digest(Path(other['file'])):
            raise ValueError('Divergent duplicate runtime coordinate: ' + item['coordinate'])
    if expected.keys() != actual.keys():
        raise ValueError('Runtime SBOM differs: added=' + str(sorted(actual.keys() - expected.keys())) + ', missing=' + str(sorted(expected.keys() - actual.keys())))
    artifacts = []
    notice_count = 0
    for coordinate, current in sorted(actual.items()):
        previous = expected[coordinate]
        archive = Path(current['file'])
        if digest(archive) != previous['sha256'] or archive.name != previous['filename'] or current['configuration'] != previous['configuration']:
            raise ValueError('Runtime archive changed: ' + coordinate)
        body = texts[previous['legal_family']]
        if coordinate not in body or previous['sha256'] not in body:
            raise ValueError('Missing artifact attribution: ' + coordinate)
        poms = sorted(archive.parent.parent.glob('*/*.pom'))
        if len(poms) != 1 or digest(poms[0]) != previous['pom_sha256']:
            raise ValueError('Runtime POM changed: ' + coordinate)
        found = []
        with zipfile.ZipFile(archive) as zipped:
            for entry in sorted(zipped.namelist()):
                leaf = entry.rsplit('/', 1)[-1].lower()
                if not entry.endswith(('/', '.class')) and re.match(r'^(license|licence|notice|copying|sentry_third_party_notices)([._-]|$)', leaf):
                    text = zipped.read(entry).decode('utf-8').replace('\r\n', '\n').replace('\r', '\n').strip()
                    if text and text not in body:
                        raise ValueError('Original archive notice missing: ' + coordinate + '/' + entry)
                    found.append(entry)
        if found != previous['archive_notices']:
            raise ValueError('Archive notice inventory changed: ' + coordinate)
        notice_count += len(found)
        artifacts.append({'coordinate': coordinate, 'sha256': previous['sha256'], 'family': previous['legal_family'], 'notices': found})
    for component in json.loads((ROOT / 'docs/foundation/NATIVE_LICENCE_ENTRIES.json').read_text()):
        if component not in catalogue:
            raise ValueError('Native/source availability catalogue entry missing: ' + component['name'])
    return {'result': 'PASS', 'runtime_artifacts': len(resolved), 'unique_runtime_artifacts': len(artifacts), 'archive_notices': notice_count,
            'catalogue_entries': len(assets), 'assets': assets, 'artifacts': artifacts,
            'limits': 'Exact current Java/runtime archive and existing native notice-entry coverage only. Final candidate native dependency/source attribution, R8 reachability and APK bytes remain separate gates. No Shield/UI memory claim.'}


if __name__ == '__main__':
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument('--inventory', required=True)
    parser.add_argument('--output', required=True)
    args = parser.parse_args()
    result = verify(args.inventory)
    Path(args.output).write_text(json.dumps(result, indent=2) + '\n')
    print(json.dumps({key: value for key, value in result.items() if key not in ('assets', 'artifacts')}))
