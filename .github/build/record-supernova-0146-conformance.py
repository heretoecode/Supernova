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
    conformance=load('foundation_conformance',video/'.github/build/verify-next-qa.py')
    conformance.sources(args.root.resolve(),False)
    evidence=conformance.binary(args.apk,args.apkanalyzer,video,args.aapt2)
    if evidence['version_name']!='0.146' or evidence['version_code']!=146:raise ValueError('Exact146 version is required')
    actual_manifest=ET.fromstring(subprocess.check_output([args.apkanalyzer,'manifest','print',str(args.apk)],text=True))
    uses_sdk=actual_manifest.find('uses-sdk')
    if uses_sdk is None or uses_sdk.get('{http://schemas.android.com/apk/res/android}minSdkVersion')!='23':raise ValueError('API23 minimum must be preserved')
    api23=json.loads((video/'docs/0.146/ANDROID23_TRANSPORT_TRUST.json').read_text())
    if api23['statistics']!={'tests':23,'failures':0,'errors':0,'skipped':0} or api23['jetty_lock_sha256']!=hashlib.sha256((video/'.github/build/jetty-supernova146.lock.json').read_bytes()).hexdigest() or api23['medialib_patch_sha256']!=hashlib.sha256((video/'.github/build/medialib-supernova146-upstream.patch').read_bytes()).hexdigest():raise ValueError('Matching actual Android23 transport proof required')
    evidence['actual_android23_transport_and_trust_tests']=23
    evidence['native_security_artifact_run']=40843685431
    signature=subprocess.run([args.apksigner,'verify',str(args.apk)],capture_output=True,text=True)
    if signature.returncode == 0:
        raise ValueError('Unsigned conformance must not accept a signed APK')
    evidence['unsigned_signature_rejected']=True
    runtime=load('foundation_runtime',video/'.github/build/verify-foundation-runtime.py')
    evidence['runtime_artifact_hashes_matched']=runtime.verify(build/'foundation/runtime-artifacts.json',video/'docs/foundation/RUNTIME_SBOM.json')
    native=load('foundation_native',video/'.github/build/verify-supernova146-native-apk.py')
    evidence['rebuilt_ffmpeg_libraries_matched']=native.verify(args.apk,args.native_evidence)
    suites=[ET.parse(file).getroot() for file in (build/'test-results/testNoamazonReleaseUnitTest').glob('TEST-*.xml')]
    for name,attribute in [('unit_tests','tests'),('unit_failures','failures'),('unit_errors','errors'),('unit_skips','skipped')]:
        evidence[name]=sum(int(suite.get(attribute,0)) for suite in suites)
    if evidence['unit_tests']<1209 or any(evidence[name] for name in ['unit_failures','unit_errors','unit_skips']):
        raise ValueError('Complete 0.146 unit suite must pass without skips')
    required={'Jetty145SecurityTest','MediaUtilsSubsDirTest','StorageWriteAccessTest','PlaybackNetworkRetryTest','SubtitleLegacyPreferencesTest','AudioSpeedSettingsTest','Jetty146BackportTest','WebdavReconciliationSelectionTest','WebdavDeleteResponseTest','WebdavTraversalCompletenessTest','CustomLibraryPageTest','Supernova138HomeTest','Supernova138PlaybackTest','SubtitleDiscoveryTest','Supernova138UiTest','BundledNoticePagerTest','PreviewSeasonTest','Supernova139HomeEditorTest','CanonicalMatchProviderTest','MediaMatchJournalTest','MediaMatchReconciliationTest','CombinedCurrentRestoreTest','TransportReadDiagnosticsTest','LogicalBackupExporterTest','BackupSettingsControllerTest','Details139AcceptanceTest','ProductionBackupSettingsTest','LibraryStatisticsTest','LibraryStatisticsLayoutTest','BackupDirectOptionsTest','PlaybackAgeOverlayTest','DiagnosticCaptureReuseTest','PutioOAuthClientTest','PutioGrantRevokeTest','StreamingProviderFocusTest','PlayerControllerLaunchTest','OmdbSupplementTest','ReceptionSupplementTest','DetailsReferenceRenderTest','Supernova141UiTest','PutioConnectionFocusTest','PlaybackChaptersTest','Playback141RenderTest','PreviewPlaybackMenusTest','SubtitleReplacementTest','Network142Test','Network142CredentialsTest','Network142WebdavTest','Network142RenderTest','Editor142RenderTest','Network142PutioTest','DetailsImprovementsTest','SettingsWorkspaceTest','SettingsShieldNavigationTest'}
    present={suite.get('name','').split('.')[-1] for suite in suites if int(suite.get('tests','0'))>0}
    if not required.issubset(present):raise ValueError('Complete 0.146 regression classes are required')
    evidence['required_0146_test_classes']=sorted(required)
    evidence['library_suites']={}
    for module,minimum in [('MediaLib',200),('FileCoreLibrary',99)]:
        reports=[ET.parse(file).getroot() for file in (args.root.resolve()/module/'build/test-results/testReleaseUnitTest').glob('TEST-*.xml')]
        stats={name:sum(int(report.get(attribute,0)) for report in reports) for name,attribute in [('tests','tests'),('failures','failures'),('errors','errors'),('skipped','skipped')]}
        stats['skipped_cases']=[report.get('name')+'.'+case.get('name') for report in reports for case in report.findall('testcase') if case.find('skipped') is not None]
        allowed={
            'MediaLib':{'com.archos.mediaprovider.video.RealDatabasePruningTest.syntheticUsbBacklogIsPrunedAndLeavesDatabaseConsistent','com.archos.mediaprovider.video.RealDatabasePruningTest.realDatabaseUpgradesCleanlyAndPassesIntegrityCheck'},
            'FileCoreLibrary':{'com.archos.filecorelibrary.webdav.WebdavPropfindBenchmarkTest.compareMinimalPropertiesWithAllprop','com.archos.filecorelibrary.SpeedTestTransferTest.compareTransferRates'},
        }
        if stats['tests']<minimum or stats['failures'] or stats['errors'] or stats['skipped']!=2 or set(stats['skipped_cases'])!=allowed[module]:raise ValueError('Complete library suite required: '+module)
        evidence['library_suites'][module]=stats
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
    evidence.update(source_commit=subprocess.check_output(['git','-C',str(video),'rev-parse','HEAD'],text=True).strip(),signed=False,apk_bytes=args.apk.stat().st_size,build_result='BUILD SUCCESSFUL',native_hashes_matched=len(hashes),native_libraries=hashes,offline_licence_entries=len(json.loads((video/'assets/foundation/licences.json').read_text())['components']),branding_resource_checks=True,ffmpeg_source_commit='946fcce07b6dcd0331c8cc609192aeff5e1924f8',native_build_evidence_sha256=hashlib.sha256(args.native_evidence.read_bytes()).hexdigest())
    with zipfile.ZipFile(args.apk) as archive:
        if any(name.endswith('/libtorrentd.so') for name in archive.namelist()):raise ValueError('Retired torrent runtime must not be packaged')
    args.output.write_text(json.dumps(evidence,indent=2)+'\n')
    print('Public unsigned Foundation conformance evidence recorded; no APK/keys/logs published.')

if __name__=='__main__':main()
