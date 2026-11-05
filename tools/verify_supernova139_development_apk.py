#!/usr/bin/env python3
"""Verify an explicitly UNSIGNED139 development APK with reused, tested138 native inputs."""
import argparse,hashlib,importlib.util,json,shutil,subprocess,zipfile
from pathlib import Path

def sha(path):
    with path.open('rb') as stream:return hashlib.file_digest(stream,'sha256').hexdigest()
def main():
    p=argparse.ArgumentParser(description=__doc__);p.add_argument('--root',type=Path,required=True);p.add_argument('--apk',type=Path,required=True);p.add_argument('--baseline',type=Path,required=True);p.add_argument('--sdk',type=Path,required=True);p.add_argument('--codec-report',type=Path,required=True);p.add_argument('--output',type=Path,required=True);p.add_argument('--report',type=Path,required=True);args=p.parse_args();video=args.root.resolve()/'Video';sdk=args.sdk.resolve();apk=args.apk.resolve()
    spec=importlib.util.spec_from_file_location('identity',video/'.github/build/verify-next-qa.py');identity=importlib.util.module_from_spec(spec);spec.loader.exec_module(identity)
    identity.sources(args.root.resolve(),False);proof=identity.binary(apk,str(sdk/'cmdline-tools/latest/bin/apkanalyzer'),video,str(sdk/'build-tools/36.0.0/aapt2'));assert proof['version_name']=='0.139' and proof['version_code']==139
    signature=subprocess.run([str(sdk/'build-tools/36.0.0/apksigner'),'verify',str(apk)],capture_output=True);assert signature.returncode!=0,'Development packaging proof requires unsigned input'
    subprocess.run([str(sdk/'build-tools/36.0.0/zipalign'),'-c','-P','16','4',str(apk)],check=True,capture_output=True)
    codec=json.loads(args.codec_report.read_text());assert codec['result']=='PASS' and codec['decode_pairs']==64;assert sha(args.baseline)==codec['baseline']['sha256']
    def native(archive):return {n:hashlib.sha256(archive.read(n)).hexdigest() for n in archive.namelist() if n.startswith('lib/') and n.endswith('.so')}
    with zipfile.ZipFile(args.baseline) as baseline,zipfile.ZipFile(apk) as candidate:
        assert candidate.testzip() is None;old=native(baseline);new=native(candidate);assert len(new)==88 and len(old)==92
        assert set(old)-set(new)=={'lib/'+abi+'/libtorrentd.so' for abi in codec['abis']};assert not(set(new)-set(old));assert all(digest==old[name] for name,digest in new.items())
        assert all(new['lib/'+abi+'/'+name]==digest for abi,data in codec['abis'].items() for name,digest in data['native_hashes'].items())
        obsolete=[b'TorrentLoaderActivity',b'TorrentObserverService',b'Lcom/archos/mediacenter/video/utils/Torrent;']
        for name in candidate.namelist():
            if name.endswith('.dex'):assert not any(symbol in candidate.read(name) for symbol in obsolete),'Retired feature survived R8'
    manifest=subprocess.run([str(sdk/'cmdline-tools/latest/bin/apkanalyzer'),'manifest','print',str(apk)],check=True,capture_output=True,text=True).stdout
    assert 'magnet' not in manifest and 'application/x-bittorrent' not in manifest and 'application/*' not in manifest
    args.output.parent.mkdir(parents=True,exist_ok=True)
    if args.output.exists():assert sha(args.output)==sha(apk),'Refuse to overwrite a different preserved development APK'
    else:shutil.copy2(apk,args.output)
    proof.update(result='PASS',source_sha=subprocess.check_output(['git','-C',str(video),'rev-parse','HEAD'],text=True).strip(),apk_path=str(args.output.resolve()),apk_bytes=apk.stat().st_size,signed=False,signature_rejected=True,zip_crc=True,zipalign_16k=True,branding_and_offline_assets=True,native_hashes=new,native_codec_report_sha256=sha(args.codec_report),native_inputs='Byte-identical to signed138 except four excluded torrent executables;64 fresh software decode pairs are inherited through exact hashes',torrent_manifest_native_and_classes_excluded=True,limits=['Unsigned partial-development package; not installable as a signed upgrade and not a delivered candidate.','No source-native rebuild, Shield hardware/output/installability or complete43 acceptance is established here.'])
    args.report.parent.mkdir(parents=True,exist_ok=True);args.report.write_text(json.dumps(proof,indent=2)+'\n');print('PASS unsigned139 identity, ZIP/alignment,88 tested native hashes, branding/legal and torrent exclusion')
if __name__=='__main__':main()
