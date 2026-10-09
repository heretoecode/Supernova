#!/usr/bin/env python3
"""Compare baseline/candidate native registries, decoded media and AVOS tempo state on four Android ABIs."""
import argparse
import hashlib
import importlib.util
import json
import os
from pathlib import Path
import shutil
import subprocess
import tempfile

ABIS = ['arm64-v8a', 'armeabi-v7a', 'x86', 'x86_64']

def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument('--root', type=Path, required=True)
    parser.add_argument('--workspace', type=Path, required=True)
    parser.add_argument('--test-workspace', type=Path, required=True)
    parser.add_argument('--reuse-container')
    parser.add_argument('--ca-bundle', type=Path, default=Path('/etc/ssl/certs/ca-certificates.crt'))
    args = parser.parse_args()
    root=args.root.resolve(); work=args.workspace.resolve(); tests=args.test_workspace.resolve(); video=root/'Video'
    tests.mkdir(parents=True,exist_ok=True)
    env=os.environ.copy()
    for key in ['DOCKER_HOST','DOCKER_CONTEXT','DOCKER_TLS','DOCKER_TLS_VERIFY','DOCKER_CERT_PATH']:
        env.pop(key,None)
    docker=['docker','--host=unix:///var/run/docker.sock']
    def run_docker(arguments,**kwargs):
        return subprocess.run(docker+arguments,env=env,check=True,**kwargs)
    spec=importlib.util.spec_from_file_location('native_builder',video/'.github/build/build-foundation-ffmpeg.py')
    builder=importlib.util.module_from_spec(spec);spec.loader.exec_module(builder)
    native_build=builder.check_candidate(root,work,json.loads((video/'docs/foundation/FFMPEG_REBUILD_REVIEWED.json').read_text())['abis'])
    container=args.reuse_container or 'foundation-native-regression-'+str(os.getpid())
    created=False
    tc=work/'toolchain/android-ndk-r30-beta1/toolchains/llvm/prebuilt/linux-x86_64/bin'
    try:
        if not args.reuse_container:
            image='foundation-native-regression:local'
            run_docker(['build','--secret','id=proxy_ca,src='+str(args.ca_bundle),'-f',str(video/'.github/build/foundation-native-runtime.Dockerfile'),'-t',image,str(video/'.github/build')],stdout=subprocess.DEVNULL)
            # Legacy 32-bit Android/QEMU needs personality(); this network-free test
            # container holds only public libraries and synthetic test media.
            run_docker(['run','-d','--name',container,'--network=none','--security-opt','seccomp=unconfined',image],stdout=subprocess.DEVNULL)
            created=True
            images=json.loads((video/'.github/build/foundation-native-runtimes.json').read_text())['images']
            for entry in images:
                abi=entry['abi'];archive=tests/(abi+'.zip')
                builder.download(entry,archive)
                run_docker(['cp',str(archive),container+':/tmp/image.zip'])
                android='/android' if abi=='x86_64' else '/android-'+abi
                bits=64 if abi in ['arm64-v8a','x86_64'] else 32
                linker='linker64' if bits==64 else 'linker';lib='lib64' if bits==64 else 'lib'
                qemu={'arm64-v8a':'aarch64','armeabi-v7a':'arm','x86':'i386','x86_64':'x86_64'}[abi]
                script=f'unzip -p /tmp/image.zip {abi}/system.img > /tmp/system.img && mkdir -p {android}/system/bin {android}/usr/bin {android}/tmp && debugfs -R "dump /bin/{linker} {android}/system/bin/{linker}" /tmp/system.img && debugfs -R "rdump /{lib} {android}/system" /tmp/system.img && chmod 755 {android}/system/bin/{linker} && cp /usr/bin/qemu-{qemu}-static {android}/usr/bin/qemu-static && rm /tmp/system.img /tmp/image.zip'
                run_docker(['exec',container,'sh','-c',script],stdout=subprocess.DEVNULL)
        corpus=tests/'corpus'
        subprocess.run(['python3',str(video/'tools/generate_foundation_native_corpus.py'),'--output',str(corpus)],check=True)
        samples=[(sample['file'],'') for sample in json.loads((corpus/'corpus.json').read_text())]+[('av1.mkv','libdav1d'),('opus.ogg','libopus')]
        reports=[]
        for abi in ABIS:
            android='/android' if abi=='x86_64' else '/android-'+abi
            triple={'x86_64':'x86_64-linux-android','arm64-v8a':'aarch64-linux-android','armeabi-v7a':'armv7a-linux-androideabi','x86':'i686-linux-android'}[abi]
            baseline=tests/abi/'baseline';(baseline/'lib').mkdir(parents=True,exist_ok=True)
            sources=[root/'native/prebuilt/ffmpeg'/('dist-full-'+abi)/'lib',root/'native/prebuilt/dav1d/lib'/abi,root/'native/prebuilt/opus/lib'/abi,root/'native/prebuilt/libmysofa/lib'/abi]
            for source in sources:
                for library in source.glob('*.so'):
                    shutil.copyfile(library,baseline/'lib'/library.name)
            expected={file.name for file in sources[0].glob('*.so')}
            candidate=work/'candidate'/('dist-full-'+abi)/'lib'
            if {file.name for file in candidate.glob('*.so')} != expected or len(expected)!=6:
                raise ValueError('Candidate must contain all six rebuilt libraries before runtime testing')
            subprocess.run([str(tc/(triple+'21-clang')),str(video/'tools/foundation_native_probe.c'),'-I'+str(root/'native/prebuilt/ffmpeg'/('dist-full-'+abi)/'include'),'-L'+str(baseline/'lib'),'-Wl,-rpath,/test/lib','-lavformat','-lavcodec','-lavfilter','-lavutil','-o',str(baseline/'native-probe')],check=True)
            run_docker(['exec',container,'mkdir','-p',android+'/test'])
            run_docker(['cp',str(baseline)+'/.',container+':'+android+'/test'])
            run_docker(['cp',str(corpus),container+':'+android+'/corpus'])
            lib='lib64' if abi in ['x86_64','arm64-v8a'] else 'lib'
            qemu='' if abi=='x86_64' else '/usr/bin/qemu-static '
            def probe(arguments):
                result=run_docker(['exec',container,'sh','-c',f'LD_LIBRARY_PATH=/test/lib:/system/{lib} chroot {android} {qemu}/test/native-probe '+arguments],capture_output=True,text=True)
                return result.stdout
            def suite():
                return {'catalogue':probe(''),'atempo':probe('--atempo'),'decodes':[{'file':name,'decoder':decoder,'output':probe('/corpus/'+name+(' '+decoder if decoder else ''))} for name,decoder in samples]}
            print('Comparing native baseline and candidate:',abi,flush=True)
            old=suite()
            run_docker(['cp',str(candidate)+'/.',container+':'+android+'/test/lib'])
            new=suite()
            if old['catalogue'].splitlines()[0]!='version\tn8.0.1' or new['catalogue'].splitlines()[0]!='version\t8.0.1':
                raise ValueError('Native runtime version differs from reviewed release source')
            if old['catalogue'].splitlines()[1:]!=new['catalogue'].splitlines()[1:] or old['atempo']!=new['atempo'] or old['decodes']!=new['decodes']:
                raise ValueError('Native registry, tempo or decoded-output regression; installation refused')
            reports.append({'abi':abi,'registry_rows':len(new['catalogue'].splitlines())-2,'registry_sha256':hashlib.sha256('\n'.join(new['catalogue'].splitlines()[1:]).encode()).hexdigest(),'atempo_sha256':hashlib.sha256(new['atempo'].encode()).hexdigest(),'decode_cases':len(samples),'decode_results':new['decodes'],'all_comparisons_passed':True})
        evidence={'schema':1,'source_commit':'894da5ca7d742e4429ffb2af534fcda0103ef593','runtime':'Android API 23 bionic; x86_64 native, other ABIs QEMU user; no framework/hardware playback claim','corpus':json.loads((corpus/'corpus.json').read_text()),'abis':reports,'total_decode_cases':sum(item['decode_cases'] for item in reports),'all_comparisons_passed':True,'candidate_libraries':{abi:native_build['abis'][abi]['libraries'] for abi in ABIS}}
        (work/'native-runtime-regression.json').write_text(json.dumps(evidence,indent=2)+'\n')
        print('Four ABI registries / tempo state / 64 decode comparisons passed; baseline remains untouched.')
    finally:
        if created:
            run_docker(['rm','-f',container],stdout=subprocess.DEVNULL)

if __name__=='__main__':
    main()
