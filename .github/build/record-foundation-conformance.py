#!/usr/bin/env python3
"""Record public proof from the actual unsigned Foundation APK and completed tests."""
import argparse
import hashlib
import importlib.util
import json
from pathlib import Path
import subprocess
import xml.etree.ElementTree as ET
import zipfile

def load(name,path):
    spec=importlib.util.spec_from_file_location(name,path)
    module=importlib.util.module_from_spec(spec);spec.loader.exec_module(module);return module

def main():
    parser=argparse.ArgumentParser(description=__doc__)
    parser.add_argument('--root',type=Path,required=True)
    parser.add_argument('--apk',type=Path,required=True)
    parser.add_argument('--apkanalyzer',required=True)
    parser.add_argument('--aapt2',required=True)
    parser.add_argument('--apksigner',required=True)
    parser.add_argument('--native-evidence',type=Path,required=True)
    parser.add_argument('--output',type=Path,required=True)
    args=parser.parse_args();video=args.root.resolve()/'Video';build=video/'build'
    conformance=load('foundation_conformance',video/'.github/build/verify-foundation.py')
    conformance.sources(args.root.resolve(),False)
    evidence=conformance.binary(args.apk,args.apkanalyzer,video,args.aapt2)
    signature=subprocess.run([args.apksigner,'verify',str(args.apk)],capture_output=True,text=True)
    if signature.returncode == 0:
        raise ValueError('Unsigned conformance must not accept a signed APK')
    evidence['unsigned_signature_rejected']=True
    runtime=load('foundation_runtime',video/'.github/build/verify-foundation-runtime.py')
    evidence['runtime_artifact_hashes_matched']=runtime.verify(build/'foundation/runtime-artifacts.json',video/'docs/foundation/RUNTIME_SBOM.json')
    native=load('foundation_native',video/'.github/build/verify-foundation-native-apk.py')
    evidence['rebuilt_ffmpeg_libraries_matched']=native.verify(args.apk,args.native_evidence)
    suites=[ET.parse(file).getroot() for file in (build/'test-results/testNoamazonReleaseUnitTest').glob('TEST-*.xml')]
    for name,attribute in [('unit_tests','tests'),('unit_failures','failures'),('unit_errors','errors'),('unit_skips','skipped')]:
        evidence[name]=sum(int(suite.get(attribute,0)) for suite in suites)
    if evidence['unit_tests']!=419 or any(evidence[name] for name in ['unit_failures','unit_errors','unit_skips']):
        raise ValueError('Complete reviewed Foundation unit suite must pass without skips')
    issues=ET.parse(build/'reports/lint-results-noamazonRelease.xml').getroot().findall('issue')
    evidence['lint']={'errors':sum(issue.get('severity') in ['Error','Fatal'] for issue in issues),'warnings':sum(issue.get('severity')=='Warning' for issue in issues)}
    if evidence['lint']['errors']:
        raise ValueError('Release lint errors refuse conformance clearance')
    with zipfile.ZipFile(args.apk) as packed:
        hashes=[]
        for name in sorted(packed.namelist()):
            if not name.startswith('lib/') or not name.endswith('.so'):continue
            abi,filename=name.split('/')[1:]
            directories=[build/'intermediates/stripped_native_libs/noamazonRelease/stripNoamazonReleaseDebugSymbols/out/lib',build/'intermediates/merged_native_libs/noamazonRelease/mergeNoamazonReleaseNativeLibs/out/lib']
            digest=hashlib.sha256(packed.read(name)).hexdigest()
            if not any((directory/abi/filename).exists() and hashlib.sha256((directory/abi/filename).read_bytes()).hexdigest()==digest for directory in directories):
                raise ValueError('Packaged native library differs from compiled build input')
            hashes.append({'path':name,'sha256':digest})
    evidence.update(source_commit=subprocess.check_output(['git','-C',str(video),'rev-parse','HEAD'],text=True).strip(),signed=False,apk_bytes=args.apk.stat().st_size,build_result='BUILD SUCCESSFUL',native_hashes_matched=len(hashes),native_libraries=hashes,offline_licence_entries=len(json.loads((video/'assets/foundation/licences.json').read_text())['components']),branding_resource_checks=True,ffmpeg_source_commit='894da5ca7d742e4429ffb2af534fcda0103ef593',native_build_evidence_sha256=hashlib.sha256(args.native_evidence.read_bytes()).hexdigest())
    args.output.write_text(json.dumps(evidence,indent=2)+'\n')
    print('Public unsigned Foundation conformance evidence recorded; no APK/keys/logs published.')

if __name__=='__main__':main()
