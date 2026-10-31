#!/usr/bin/env python3
"""Prepare the existing key only after exact-source unsigned QA conformance."""
import argparse
import hashlib
import json
import os
from pathlib import Path
import re
import sys
import foundation_signing as key

CERTIFICATE = '79ed34c52c3e359756ade0634d7bbb6f8e94e42a059020c1220bd8c7092a9f5e'
MINIMUM_TESTS = 1258
REQUIRED_TEST_CLASSES = {'Jetty145SecurityTest','MediaUtilsSubsDirTest','StorageWriteAccessTest','PlaybackNetworkRetryTest','SubtitleLegacyPreferencesTest','AudioSpeedSettingsTest','Jetty146BackportTest','WebdavReconciliationSelectionTest','WebdavDeleteResponseTest','WebdavTraversalCompletenessTest','CustomLibraryPageTest','Supernova138HomeTest','Supernova138PlaybackTest','SubtitleDiscoveryTest','Supernova138UiTest','BundledNoticePagerTest','PreviewSeasonTest','Supernova139HomeEditorTest','CanonicalMatchProviderTest','MediaMatchJournalTest','MediaMatchReconciliationTest','CombinedCurrentRestoreTest','TransportReadDiagnosticsTest','LogicalBackupExporterTest','BackupSettingsControllerTest','Details139AcceptanceTest','ProductionBackupSettingsTest','LibraryStatisticsTest','LibraryStatisticsLayoutTest','BackupDirectOptionsTest','PlaybackAgeOverlayTest','DiagnosticCaptureReuseTest','PutioOAuthClientTest','PutioGrantRevokeTest','StreamingProviderFocusTest','PlayerControllerLaunchTest','OmdbSupplementTest','ReceptionSupplementTest','DetailsReferenceRenderTest','Supernova141UiTest','PutioConnectionFocusTest','PlaybackChaptersTest','Playback141RenderTest','PreviewPlaybackMenusTest','SubtitleReplacementTest','Network142Test','Network142CredentialsTest','Network142WebdavTest','Network142RenderTest','Editor142RenderTest','Network142PutioTest','DetailsImprovementsTest','SettingsWorkspaceTest','SettingsShieldNavigationTest'}

IMPLEMENTATION_BASE = 'b0bce99a41b8321606059f4bfe5b5c16da741c6b'
IMPLEMENTATION_TREES = {'src/main': 'd09043e23d18148d0f1651ca76c23ea1872e9909', 'src/test': 'd0a7771d368eb1813eae5bd738631cc8bcea4876', 'res': 'df320ea71beb115264cbca30a50e8587ce86cbdc', 'AndroidManifest.xml': '0054e3359347da4fada1ff9eae75c93486b4829c', 'build.gradle': '3a516bfe54c6f75dad901501c78d5be2c52fe7e1', 'settings.gradle': '7becd5859bacb0e0c4f9bf94f24069e81795a499', 'gradle.properties': '1c947b0dba9f10623846e845dbca1796213eb3b7'}
REQUIRED_TEST_CLASSES.update({'NextHudTest','ChaptersDbClientTest','NextQaWizardTest','PreviewEnrichmentQueueTest','DetailsEnrichmentDeliveryTest','NextVersionUiTest'})

def gate(evidence, apk, source):
    if not re.fullmatch(r'[0-9a-f]{40}', source or '') or evidence.get('source_commit') != source:
        raise key.SigningError('QA conformance source differs from the checked-out commit.')
    if evidence.get('implementation_base')!=IMPLEMENTATION_BASE or evidence.get('implementation_trees')!=IMPLEMENTATION_TREES or evidence.get('implementation_source_unchanged') is not True:
        raise key.SigningError('Validated PR29 implementation provenance is missing or changed.')
    if evidence.get('application_id') != 'app.supernova.player' or evidence.get('version_name') != '0.148' or evidence.get('version_code') != 148:
        raise key.SigningError('QA application identity/version differs from the approved scope.')
    if evidence.get('signed') is not False or evidence.get('unsigned_signature_rejected') is not True:
        raise key.SigningError('QA signing requires the verified unsigned APK.')
    for name in ('runtime_artifact_hashes_matched', 'rebuilt_ffmpeg_libraries_matched', 'branding_resource_checks'):
        if not evidence.get(name):
            raise key.SigningError('QA native/runtime/branding conformance is incomplete.')
    if evidence.get('build_result') != 'BUILD SUCCESSFUL' or evidence.get('unit_tests', 0) < MINIMUM_TESTS or any(evidence.get(name) != 0 for name in ('unit_failures', 'unit_errors', 'unit_skips')) or evidence.get('lint', {}).get('errors') != 0:
        raise key.SigningError('QA signing requires the complete passing suite and release lint.')
    if set(evidence.get('required_0148_test_classes', [])) != REQUIRED_TEST_CLASSES:
        raise key.SigningError('All required 0.148 regression classes must pass.')
    if evidence.get('rebuilt_ffmpeg_libraries_matched') != 24 or evidence.get('native_hashes_matched') != 84 or evidence.get('offline_licence_entries', 0) < 60:
        raise key.SigningError('QA compiled native and offline licence evidence is missing.')
    if evidence.get('actual_android23_transport_and_trust_tests') != 28 or evidence.get('native_security_artifact_run') != 40843685431:
        raise key.SigningError('API23 transport/native security validation is incomplete.')
    for module,minimum in [('MediaLib',200),('FileCoreLibrary',104)]:
        proof=evidence.get('library_suites',{}).get(module,{})
        if proof.get('tests',0)<minimum or proof.get('failures')!=0 or proof.get('errors')!=0 or proof.get('skipped')!=2:
            raise key.SigningError('Complete reviewed library suites are required.')
    if hashlib.sha256(apk.read_bytes()).hexdigest() != evidence.get('apk_sha256') or apk.stat().st_size != evidence.get('apk_bytes'):
        raise key.SigningError('Unsigned APK changed after conformance.')


def prepare(evidence_path, apk, env=None):
    env = dict(os.environ if env is None else env)
    gate(json.loads(evidence_path.read_text()), apk, env.get('SUPERNOVA_QA_SOURCE'))
    if env.get('GITHUB_REF') != 'refs/heads/codex/supernova-next-implementation-2026-10-30':
        raise key.SigningError('QA signing is restricted to the isolated 0.148 branch.')
    if env.get('SUPERNOVA_CERT_SHA256') != CERTIFICATE:
        raise key.SigningError('QA certificate pin differs from the verified installed-release baseline.')
    key.preflight('validate', env)
    data = key.decode_keystore(env['SUPERNOVA_P12_BASE64'])
    directory = key.store_path(env)
    if directory.exists() or directory.is_symlink():
        raise key.SigningError('Temporary signing directory already exists. Refusing reuse.')
    directory.mkdir(mode=0o700)
    keep = False
    try:
        keystore = directory / 'foundation.p12'
        fd = os.open(keystore, os.O_WRONLY | os.O_CREAT | os.O_EXCL | os.O_NOFOLLOW, 0o600)
        with os.fdopen(fd, 'wb') as stream:
            stream.write(data)
        key.verify('validate', keystore, env)
        keep = True
    finally:
        if not keep:
            key.remove_store(directory)


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument('--evidence', type=Path, required=True)
    parser.add_argument('--apk', type=Path, required=True)
    args = parser.parse_args()
    try:
        prepare(args.evidence, args.apk)
        print('Exact-source unsigned conformance and existing QA certificate verified; temporary key prepared.')
        return 0
    except (key.SigningError, OSError, ValueError, TypeError):
        print('QA key preparation refused; inspect the public conformance gates and existing secret configuration privately.', file=sys.stderr)
        return 1


if __name__ == '__main__':
    sys.exit(main())
