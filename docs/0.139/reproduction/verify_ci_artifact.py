#!/usr/bin/env python3
from pathlib import Path
import argparse,hashlib,importlib.util,json,re,subprocess,zipfile,xml.etree.ElementTree as ET

parser=argparse.ArgumentParser(description='Verify an extracted unsigned CI artifact, not whole release acceptance.')
for flag in ['artifact-directory','video','sdk','output','baseline-apk']:parser.add_argument('--'+flag,type=Path,required=True)
parser.add_argument('--source-sha',required=True,help='Exact40-character source SHA from the verified GitHub run metadata')
args=parser.parse_args();unpacked=args.artifact_directory;video=args.video;sdk=args.sdk;out=args.output
out.mkdir(parents=True,exist_ok=True)
assert re.fullmatch('[0-9a-f]{40}',args.source_sha),'Exact run source SHA required'
subprocess.run(['git','-C',str(video),'diff','--quiet',args.source_sha,'HEAD','--','src/main','assets','res','AndroidManifest.xml','build.gradle','gradle.properties'],check=True)
def sha(p):
    with p.open('rb') as f:return hashlib.file_digest(f,'sha256').hexdigest()
def module(name,path):
    spec=importlib.util.spec_from_file_location(name,path);m=importlib.util.module_from_spec(spec);spec.loader.exec_module(m);return m
apk=next(unpacked.rglob('*.apk'));native=unpacked/'_temp/supernova-foundation-ffmpeg/verified-native-build.json';wrappers=unpacked/'_temp/supernova-foundation-native-wrappers.json';runtime=unpacked/'_temp/supernova-foundation-ffmpeg/native-runtime-regression.json'
identity=module('identity',video/'.github/build/verify-next-qa.py');identity.sources(video.parent,False)
proof=identity.binary(apk,str(sdk/'cmdline-tools/latest/bin/apkanalyzer'),video,str(sdk/'build-tools/36.0.0/aapt2'))
assert proof['version_name']=='0.139' and proof['version_code']==139
assert subprocess.run([str(sdk/'build-tools/36.0.0/apksigner'),'verify',str(apk)],capture_output=True).returncode!=0
subprocess.run([str(sdk/'build-tools/36.0.0/zipalign'),'-c','-P','16','4',str(apk)],check=True,capture_output=True)
count=module('native',video/'.github/build/verify-foundation-native-apk.py').verify(apk,native);assert count==24
w=json.loads(wrappers.read_text());assert w['native_evidence_sha256']==sha(native)
r=json.loads(runtime.read_text());assert r['all_comparisons_passed'] and r['total_decode_cases']==64
n=json.loads(native.read_text());assert w['ffmpeg_source_commit']==r['source_commit']==n['source_commit']
assert r['candidate_libraries']=={abi:data['libraries'] for abi,data in n['abis'].items()},'Runtime proof must cover the exact rebuilt package inputs'
with zipfile.ZipFile(apk) as z:
    assert z.testzip() is None
    libs={n:hashlib.sha256(z.read(n)).hexdigest() for n in z.namelist() if n.startswith('lib/') and n.endswith('.so')};assert len(libs)==88
    matched=[]
    for entry in w['libraries']:
        path=entry['path'];key='lib/'+path.split('/libs/')[1]
        if key.endswith('/libtorrentd.so'):continue
        assert libs[key]==entry['sha256'],key
        matched.append(key)
    # The four nvpnativehelper paths occur in both dependency manifests.
    assert len(matched)==84 and len(set(matched))==80
    baseline_sha=sha(args.baseline_apk)
    assert baseline_sha=='6f46a40c9698e9219b6e26c2f09277de93462d3b9721a9b0b2bfd64814cd04ee'
    remaining=set(libs)-set(matched);assert len(remaining)==8
    with zipfile.ZipFile(args.baseline_apk) as baseline:
        assert all(libs[name]==hashlib.sha256(baseline.read(name)).hexdigest() for name in remaining)
    for n in z.namelist():
        if n.endswith('.dex'):assert all(s not in z.read(n) for s in [b'TorrentLoaderActivity',b'TorrentObserverService',b'Lcom/archos/mediacenter/video/utils/Torrent;'])
    assert all(not n.endswith('/libtorrentd.so') for n in libs)
manifest=subprocess.check_output([str(sdk/'cmdline-tools/latest/bin/apkanalyzer'),'manifest','print',str(apk)],text=True)
assert all(s not in manifest for s in ['magnet','application/x-bittorrent','application/*'])
android='{http://schemas.android.com/apk/res/android}';m=ET.fromstring(manifest)
assert m.find('application').get(android+'debuggable','false')=='false'
uses=m.find('uses-sdk');assert uses.get(android+'minSdkVersion')=='23' and uses.get(android+'targetSdkVersion')=='36'
proof.update(debuggable=False,min_sdk=23,target_sdk=36)
proof.update(result='PASS',source_sha=args.source_sha,signed=False,apk_bytes=apk.stat().st_size,apk_path=str(apk),zip_crc=True,zipalign_16k=True,signature_rejected=True,rebuilt_ffmpeg_libraries_matched=count,wrapper_manifest_entries_matched=len(matched),wrapper_unique_libraries_matched=len(set(matched)),remaining_libraries_matching_signed138=len(remaining),signed138_baseline_sha256=baseline_sha,native_libraries=libs,native_build_evidence_sha256=sha(native),native_wrapper_evidence_sha256=sha(wrappers),native_runtime_evidence_sha256=sha(runtime),software_decode_pairs=64,limits=['Unsigned CI package; no signed upgrade/installability or Shield acceptance.','This binary-only proof does not establish overall CI or release acceptance; consult the run receipt.','Source debug native wrappers follow existing Foundation build arrangement; this does not assert Java debuggable status.'])
(out/'CI_CURRENT_PACKAGING_VERIFICATION.json').write_text(json.dumps(proof,indent=2)+'\n')
print(json.dumps({k:v for k,v in proof.items() if k!='native_libraries'},indent=2))
