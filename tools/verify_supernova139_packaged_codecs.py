#!/usr/bin/env python3
"""Compare actual packaged FFmpeg decode/filter/registry behavior in isolated Android23 userspace.

Requires the existing pinned native-runtime Docker image, system roots, NDK and
prebuilt headers prepared using the Foundation lock files. No app/signing data or
network is supplied to the running test container. This is not Shield output.
"""
import argparse,hashlib,json,subprocess,zipfile,shutil
from pathlib import Path

ABIS={'x86_64':'x86_64-linux-android','arm64-v8a':'aarch64-linux-android','armeabi-v7a':'armv7a-linux-androideabi','x86':'i686-linux-android'}
def sha(path):
    with path.open('rb') as stream:return hashlib.file_digest(stream,'sha256').hexdigest()
def run(args):return subprocess.run([str(a) for a in args],check=True,capture_output=True,text=True).stdout

def main():
    p=argparse.ArgumentParser(description=__doc__);p.add_argument('--apk',type=Path,required=True);p.add_argument('--baseline',type=Path,required=True);p.add_argument('--workspace',type=Path,required=True);p.add_argument('--container',required=True);p.add_argument('--report',type=Path,required=True);args=p.parse_args()
    repo=Path(__file__).resolve().parents[1];work=args.workspace.resolve();tc=work/'toolchain/android-ndk-r30-beta1/toolchains/llvm/prebuilt/linux-x86_64/bin';headers=work/'ffmpeg-prebuilt';corpus=work/'corpus'
    lock=json.loads((repo/'.github/build/ffmpeg-foundation.lock.json').read_text());images=json.loads((repo/'.github/build/foundation-native-runtimes.json').read_text())['images']
    assert sha(work/'downloads/ndk.zip')==lock['inputs']['ndk']['sha256']
    for entry in images:assert sha(work/'downloads'/(entry['abi']+'.zip'))==entry['sha256']
    assert run(['git','-C',headers,'rev-parse','HEAD']).strip()=='6ba0e9340cf725bede2e2f29d30c290a2ab8130e'
    samples=json.loads((corpus/'corpus.json').read_text())
    for sample in samples:assert sha(corpus/sample['file'])==sample['sha256']
    cases=[(entry['file'],'') for entry in samples]+[('av1.mkv','libdav1d'),('opus.ogg','libopus')]
    report={'schema':1,'result':'RUNNING','tool_source':run(['git','-C',repo,'rev-parse','HEAD']).strip(),'apk':{'sha256':sha(args.apk),'bytes':args.apk.stat().st_size},'baseline':{'sha256':sha(args.baseline),'bytes':args.baseline.stat().st_size},'runtime_api':23,'probe_api':21,'ndk_sha256':lock['inputs']['ndk']['sha256'],'header_commit':'6ba0e9340cf725bede2e2f29d30c290a2ab8130e','corpus':samples,'abis':{},'limits':['Userspace software decode/filter/registry only; no application installation, hardware decoding, passthrough, HDR or Shield navigation.','Packaged libraries are reused verified138 bytes; no fresh native-source rebuild is claimed.','APK application source and final signing acceptance are tracked separately.']}
    args.report.parent.mkdir(parents=True,exist_ok=True)
    def save():args.report.write_text(json.dumps(report,indent=2)+'\n')
    save()
    try:
        for abi,triple in ABIS.items():
            root='/android-'+abi;target=work/'comparison'/abi;target.mkdir(parents=True,exist_ok=True)
            for label,apk in [('baseline',args.baseline),('candidate',args.apk)]:
                lib=target/label/'lib';lib.mkdir(parents=True,exist_ok=True)
                with zipfile.ZipFile(apk) as packed:
                    for name in packed.namelist():
                        if name.startswith('lib/'+abi+'/') and name.endswith('.so'):(lib/Path(name).name).write_bytes(packed.read(name))
            base=target/'baseline/lib';candidate=target/'candidate/lib';old={x.name:sha(x) for x in base.glob('*.so')};new={x.name:sha(x) for x in candidate.glob('*.so')};assert set(old)-set(new)=={'libtorrentd.so'};assert not(set(new)-set(old));assert all(old[n]==digest for n,digest in new.items())
            probe=target/'native-probe';run([tc/(triple+'21-clang'),repo/'tools/foundation_native_probe.c','-I'+str(headers/('dist-full-'+abi)/'include'),'-L'+str(base),'-Wl,-rpath,/test/lib','-lavformat','-lavcodec','-lavfilter','-lavutil','-o',probe])
            run(['docker','exec',args.container,'mkdir','-p',root+'/test']);run(['docker','cp',probe,args.container+':'+root+'/test/native-probe']);run(['docker','cp',str(corpus),args.container+':'+root+'/corpus'])
            def suite(label):
                run(['docker','cp',str(target/label/'lib')+'/.',args.container+':'+root+'/test/lib'])
                libname='lib64' if abi in ['x86_64','arm64-v8a'] else 'lib';qemu='' if abi=='x86_64' else '/usr/bin/qemu-static '
                def invoke(arguments):return run(['docker','exec',args.container,'sh','-c',f'LD_LIBRARY_PATH=/test/lib:/system/{libname} chroot {root} {qemu}/test/native-probe '+arguments])
                return {'registry':invoke(''),'atempo':invoke('--atempo'),'decodes':[{'file':name,'decoder':decoder,'output':invoke('/corpus/'+name+(' '+decoder if decoder else ''))} for name,decoder in cases]}
            before=suite('baseline');after=suite('candidate');assert before==after;assert before['registry'].splitlines()[0]=='version\t8.0.1';assert 'atempo-output\t' in after['atempo'];assert all(case['output'].strip() for case in after['decodes'])
            report['abis'][abi]={'native_hashes':new,'removed_exclusive_library':'libtorrentd.so','probe_sha256':sha(probe),'matched_cases':len(cases),'baseline_equals_candidate':True,'outputs':after};save();print('PASS',abi,len(cases),'decode pairs, registry and dynamic atempo',flush=True)
        report['result']='PASS';report['decode_pairs']=sum(x['matched_cases'] for x in report['abis'].values());save()
    except Exception:
        report['result']='FAIL';save();raise
if __name__=='__main__':main()
