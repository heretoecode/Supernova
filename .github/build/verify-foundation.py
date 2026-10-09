#!/usr/bin/env python3
"""Secret-free Foundation source and binary conformance gates."""
import argparse
import hashlib
import json
from pathlib import Path
import re
import subprocess
import xml.etree.ElementTree as ET
import zipfile

APP = 'app.supernova.player'
ANDROID = '{http://schemas.android.com/apk/res/android}'
LEGACY = ('browser.SearchProviderVideocommunity', 'com.archos.media.videocommunity', 'com.archos.media.scrapercommunity', 'org.courville.nova.markpreview')

def sources(root, ready=False):
    video = root / 'Video'
    manifest = ET.parse(video / 'AndroidManifest.xml').getroot()
    text = (video / 'AndroidManifest.xml').read_text()
    if any(x in text for x in LEGACY) or any(ANDROID+x in manifest.attrib for x in ('sharedUserId','sharedUserMaxSdkVersion')):
        raise ValueError('Foundation provider/shared-UID source isolation incomplete')
    for path in (root/'MediaLib').rglob('*'):
        if path.suffix in ('.java','.xml') and 'build' not in path.relative_to(root/'MediaLib').parts and any(x in path.read_text() for x in LEGACY):
            raise ValueError('Foundation sibling authority mismatch')
    history=json.loads((video/'assets/foundation/release-history.json').read_text())
    expected=[f'0.{i}' for i in range(1,len(history['history'])+1)]
    if [x['version'] for x in reversed(history['history'])] != expected or history['current']['version'] != f'0.{len(expected)+1}' or history['current']['version_code'] != len(expected)+1:
        raise ValueError('Literal Foundation version counter is inconsistent')
    for entry in history['history']:
        if not entry.get('archive_digest_verified') or not entry.get('apks') or not all(re.fullmatch('[0-9a-f]{64}',a['sha256']) for a in entry['apks']):
            raise ValueError('Historical APK binary evidence is incomplete')
    licences=json.loads((video/'assets/foundation/licences.json').read_text())
    for entry in licences['components']:
        path=entry['text_asset']
        if not path.startswith('foundation/legal/') or '..' in path or len((video/'assets'/path).read_bytes()) < 500:
            raise ValueError('Missing full offline legal text')
        if not entry['url'].startswith('https://') or any(x in entry['url'] for x in ('?','#')):
            raise ValueError('Invalid public official QR destination')
    if ready:
        evidence=json.loads((video/'docs/foundation/READINESS.json').read_text())
        required=('unit_tests','binary_conformance','native_source_compliance','protected_key_validation','foundation_review')
        if evidence.get('release_ready') is not True or evidence.get('blockers') or not all(evidence.get('gates',{}).get(name) is True for name in required):
            raise ValueError('Foundation readiness evidence contains unresolved gates')
    return history['current']

def binary(apk, analyzer, video):
    def inspect(part):
        result=subprocess.run([analyzer,'manifest',part,str(apk)],capture_output=True,text=True,check=True,timeout=120)
        return result.stdout.strip()
    history=json.loads((video/'assets/foundation/release-history.json').read_text())
    version=history['current']
    if inspect('application-id') != APP or inspect('version-name') != version['version'] or int(inspect('version-code')) != version['version_code']:
        raise ValueError('Actual APK identity/version differs from approved Foundation source')
    manifest_text=inspect('print');manifest=ET.fromstring(manifest_text)
    if any(x in manifest_text for x in LEGACY) or any(ANDROID+x in manifest.attrib for x in ('sharedUserId','sharedUserMaxSdkVersion')):
        raise ValueError('Actual APK manifest isolation failed')
    authorities=[p.get(ANDROID+'authorities','') for p in manifest.iter('provider')]
    if not all(value.startswith(APP+'.') for group in authorities for value in group.split(';')):
        raise ValueError('APK provider authority is outside permanent identity')
    required={APP+'.media',APP+'.scraper',APP+'.browser',APP+'.provider'}
    if not required.issubset({value for group in authorities for value in group.split(';')}):
        raise ValueError('Expected isolated providers absent from APK')
    with zipfile.ZipFile(apk) as archive:
        names=archive.namelist()
        if any(n.lower().endswith(('.p12','.pfx','.jks','.keystore','keystore.properties')) for n in names):
            raise ValueError('Signing material must never be packaged')
        for source in (video/'assets/foundation').rglob('*'):
            if source.is_file() and archive.read('assets/'+str(source.relative_to(video/'assets'))) != source.read_bytes():
                raise ValueError('APK offline Foundation legal/history asset differs from source')
        native=[n for n in names if n.startswith('lib/') and n.endswith('.so')]
        if not native or {n.split('/')[1] for n in native} != {'arm64-v8a','armeabi-v7a','x86','x86_64'}:
            raise ValueError('Universal APK is missing approved native ABIs')
    return {'application_id':APP,'version_name':version['version'],'version_code':version['version_code'],
            'provider_authorities':authorities,'native_library_count':len(native),
            'apk_sha256':hashlib.sha256(apk.read_bytes()).hexdigest()}

if __name__=='__main__':
    parser=argparse.ArgumentParser(description=__doc__)
    parser.add_argument('--root',type=Path,required=True)
    parser.add_argument('--readiness',action='store_true')
    parser.add_argument('--apk',type=Path)
    parser.add_argument('--apkanalyzer')
    args=parser.parse_args()
    try:
        sources(args.root,args.readiness)
        if args.apk:
            if not args.apkanalyzer:raise ValueError('Android APK analyzer required')
            print(json.dumps(binary(args.apk,args.apkanalyzer,args.root/'Video'),indent=2))
        else:print('Foundation source conformance passed')
    except (ValueError,OSError,KeyError,ET.ParseError,subprocess.SubprocessError,zipfile.BadZipFile):
        raise SystemExit('Foundation conformance failed; release refused. Inspect secret-free readiness evidence.')
