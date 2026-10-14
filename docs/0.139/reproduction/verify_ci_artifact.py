#!/usr/bin/env python3
from pathlib import Path
import argparse,hashlib,importlib.util,json,subprocess,zipfile,xml.etree.ElementTree as ET

parser=argparse.ArgumentParser(description='Verify an extracted unsigned CI artifact, not whole release acceptance.')
for flag in ['artifact-directory','video','sdk','output']:parser.add_argument('--'+flag,type=Path,required=True)
args=parser.parse_args();unpacked=args.artifact_directory;video=args.video;sdk=args.sdk;out=args.output
out.mkdir(parents=True,exist_ok=True)
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
with zipfile.ZipFile(apk) as z:
    assert z.testzip() is None
    libs={n:hashlib.sha256(z.read(n)).hexdigest() for n in z.namelist() if n.startswith('lib/') and n.endswith('.so')};assert len(libs)==88
    matched=[]
    for entry in w['libraries']:
        path=entry['path'];key='lib/'+path.split('/libs/')[1]
        if key.endswith('/libtorrentd.so'):continue
        assert libs[key]==entry['sha256'],key
        matched.append(key)
    assert len(matched)==84
    for n in z.namelist():
        if n.endswith('.dex'):assert all(s not in z.read(n) for s in [b'TorrentLoaderActivity',b'TorrentObserverService',b'Lcom/archos/mediacenter/video/utils/Torrent;'])
    assert all(not n.endswith('/libtorrentd.so') for n in libs)
manifest=subprocess.check_output([str(sdk/'cmdline-tools/latest/bin/apkanalyzer'),'manifest','print',str(apk)],text=True)
assert all(s not in manifest for s in ['magnet','application/x-bittorrent','application/*'])
android='{http://schemas.android.com/apk/res/android}';m=ET.fromstring(manifest)
assert m.find('application').get(android+'debuggable','false')=='false'
uses=m.find('uses-sdk');assert uses.get(android+'minSdkVersion')=='23' and uses.get(android+'targetSdkVersion')=='36'
proof.update(debuggable=False,min_sdk=23,target_sdk=36)
proof.update(result='PASS',source_sha='5177cb3aa6fb3b7f94bb6fb134df348e9fffb7a0',signed=False,apk_bytes=apk.stat().st_size,apk_path=str(apk),zip_crc=True,zipalign_16k=True,signature_rejected=True,rebuilt_ffmpeg_libraries_matched=count,wrapper_manifest_libraries_matched=len(matched),native_libraries=libs,native_build_evidence_sha256=sha(native),native_wrapper_evidence_sha256=sha(wrappers),native_runtime_evidence_sha256=sha(runtime),software_decode_pairs=64,limits=['Unsigned CI package; no signed upgrade/installability or Shield acceptance.','CI fails its small Backup layout test; this binary proof is not release acceptance.','Source debug native wrappers follow existing Foundation build arrangement; this does not assert Java debuggable status.'])
(out/'CI_CURRENT_PACKAGING_VERIFICATION.json').write_text(json.dumps(proof,indent=2)+'\n')
print(json.dumps({k:v for k,v in proof.items() if k!='native_libraries'},indent=2))
