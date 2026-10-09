#!/usr/bin/env python3
"""Prepare isolated Foundation contracts on pinned build copies, never on Preview."""
from pathlib import Path
import json
import sys
import xml.etree.ElementTree as ET

APPLICATION_ID = 'app.supernova.player'
REPLACEMENTS = {
    'com.archos.media.videocommunity': APPLICATION_ID + '.media',
    'com.archos.media.scrapercommunity': APPLICATION_ID + '.scraper',
    'browser.SearchProviderVideocommunity': APPLICATION_ID + '.browser',
}
ANDROID = '{http://schemas.android.com/apk/res/android}'

def prepare(root):
    root = Path(root).resolve()
    if not all((root / project).is_dir() for project in ('Video', 'MediaLib')):
        raise ValueError('Expected pinned Video and MediaLib build directories')
    counts = dict.fromkeys(REPLACEMENTS, 0)
    for project in ('Video', 'MediaLib'):
        for path in (root / project).rglob('*'):
            if path.suffix not in ('.java', '.xml') or 'build' in path.relative_to(root / project).parts:
                continue
            original = path.read_text()
            updated = original
            for before, after in REPLACEMENTS.items():
                counts[before] += updated.count(before)
                updated = updated.replace(before, after)
            if original != updated: path.write_text(updated)
    if not all(counts.values()):
        raise ValueError('Missing legacy contract; refusing incomplete Foundation preparation')
    manifest = root / 'Video/AndroidManifest.xml'
    original = manifest.read_text()
    # Preserve comments/formatting; remove only the legacy shared-user identity.
    import re
    updated = re.sub(r'\s+android:sharedUser(?:Id|MaxSdkVersion)="[^"]*"', '', original)
    updated = updated.replace('android:label="@string/nova"', 'android:label="${novaLabel}"')
    updated = updated.replace('android:taskAffinity="archos.task.video"', 'android:taskAffinity="app.supernova.player.task.video"')
    manifest.write_text(updated)
    tree = ET.fromstring(updated)
    if any(ANDROID + name in tree.attrib for name in ('sharedUserId', 'sharedUserMaxSdkVersion')):
        raise ValueError('Foundation must not share a UID with NOVA/Preview')
    authorities = [node.get(ANDROID+'authorities', '') for node in tree.iter('provider')]
    if not any(APPLICATION_ID+'.media' in value for value in authorities):
        raise ValueError('Foundation media provider was not isolated')
    return {'application_id': APPLICATION_ID, 'replacements':counts, 'shared_uid_removed':True,
            'provider_authorities':authorities}

if __name__ == '__main__':
    if len(sys.argv) != 2: raise SystemExit('Usage: prepare-foundation.py PINNED_BUILD_ROOT')
    print(json.dumps(prepare(sys.argv[1]),indent=2))
