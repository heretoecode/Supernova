import copy
import hashlib
import importlib
import json
from pathlib import Path
import sys
import tempfile
import unittest
import zipfile
from unittest.mock import patch

ROOT = Path(__file__).resolve().parents[1]
sys.path.insert(0, str(ROOT / '.github/signing'))
qa = importlib.import_module('supernova_0141_qa_signing')
artifact = importlib.import_module('verify_supernova_0141_qa_apk')
REQUIRED = ['Supernova138HomeTest', 'Supernova138PlaybackTest', 'SubtitleDiscoveryTest',
            'Supernova138UiTest', 'BundledNoticePagerTest', 'PreviewSeasonTest', 'Supernova139HomeEditorTest',
            'CanonicalMatchProviderTest', 'MediaMatchJournalTest', 'MediaMatchReconciliationTest',
            'CombinedCurrentRestoreTest', 'TransportReadDiagnosticsTest', 'LogicalBackupExporterTest',
            'BackupSettingsControllerTest','Details139AcceptanceTest','ProductionBackupSettingsTest','LibraryStatisticsTest','LibraryStatisticsLayoutTest','BackupDirectOptionsTest','PlaybackAgeOverlayTest','DiagnosticCaptureReuseTest','PutioOAuthClientTest','PutioGrantRevokeTest','StreamingProviderFocusTest','PlayerControllerLaunchTest','OmdbSupplementTest','ReceptionSupplementTest','DetailsReferenceRenderTest','Supernova141UiTest','PutioConnectionFocusTest','PlaybackChaptersTest','Playback141RenderTest','PreviewPlaybackMenusTest','SubtitleReplacementTest']


class Supernova141QAGates(unittest.TestCase):
    def setUp(self):
        self.temp = tempfile.TemporaryDirectory(); self.addCleanup(self.temp.cleanup)
        self.apk = Path(self.temp.name) / 'unsigned.apk'; self.apk.write_bytes(b'unsigned test payload')
        self.source = 'a' * 40
        self.proof = dict(source_commit=self.source, application_id='app.supernova.player', version_name='0.141', version_code=141,
                          signed=False, unsigned_signature_rejected=True, runtime_artifact_hashes_matched=True,
                          rebuilt_ffmpeg_libraries_matched=24, branding_resource_checks=True, build_result='BUILD SUCCESSFUL',
                          unit_tests=qa.MINIMUM_TESTS, unit_failures=0, unit_errors=0, unit_skips=0,
                          lint={'errors': 0}, native_hashes_matched=88, offline_licence_entries=60,
                          required_0141_test_classes=REQUIRED,
                          apk_bytes=self.apk.stat().st_size, apk_sha256=hashlib.sha256(self.apk.read_bytes()).hexdigest())

    def test_exact_unsigned_candidate_passes_gate(self):
        qa.gate(self.proof, self.apk, self.source)

    def test_missing_141_regressions_failed_tests_and_old_identity_refuse_signing(self):
        for field, value in [('required_0141_test_classes', REQUIRED[:-1]), ('unit_tests', qa.MINIMUM_TESTS - 1),
                             ('unit_failures', 1), ('unit_errors', 1), ('unit_skips', 1), ('version_name', '0.138'), ('version_code', 138),
                             ('application_id', 'org.courville.nova'), ('signed', True), ('lint', {'errors': 1}), ('rebuilt_ffmpeg_libraries_matched', True),
                             ('native_hashes_matched', 87), ('offline_licence_entries', 59)]:
            with self.subTest(field=field):
                proof = copy.deepcopy(self.proof); proof[field] = value
                with self.assertRaises(qa.key.SigningError): qa.gate(proof, self.apk, self.source)

    def test_main_and_previous_qa_branch_rejected_before_key_decode(self):
        evidence = Path(self.temp.name) / 'proof.json'; evidence.write_text(json.dumps(self.proof))
        for ref in ['refs/heads/main', 'refs/heads/codex/supernova-0.137-qa-2026-10-11',
                    'refs/heads/codex/supernova-0.138-qa-2026-10-12']:
            with patch.object(qa.key, 'decode_keystore') as decode:
                with self.assertRaises(qa.key.SigningError):
                    qa.prepare(evidence, self.apk, dict(SUPERNOVA_QA_SOURCE=self.source, GITHUB_REF=ref, SUPERNOVA_CERT_SHA256=qa.CERTIFICATE))
                decode.assert_not_called()

    def test_changed_payload_and_source_are_rejected(self):
        with self.assertRaises(qa.key.SigningError): qa.gate(self.proof, self.apk, 'b' * 40)
        self.apk.write_bytes(b'changed payload')
        with self.assertRaises(qa.key.SigningError): qa.gate(self.proof, self.apk, self.source)

    def test_failed_suite_or_wrong_certificate_cannot_reach_key_decode_on_correct_branch(self):
        env = dict(SUPERNOVA_QA_SOURCE=self.source,
                   GITHUB_REF='refs/heads/codex/supernova-0.141-implementation-2026-10-22',
                   SUPERNOVA_CERT_SHA256=qa.CERTIFICATE)
        evidence = Path(self.temp.name) / 'proof.json'
        for failed, certificate in [(1, qa.CERTIFICATE), (0, '0' * 64)]:
            proof = copy.deepcopy(self.proof); proof['unit_failures'] = failed
            evidence.write_text(json.dumps(proof)); env['SUPERNOVA_CERT_SHA256'] = certificate
            with patch.object(qa.key, 'decode_keystore') as decode:
                with self.assertRaises(qa.key.SigningError): qa.prepare(evidence, self.apk, env)
                decode.assert_not_called()

    def test_signature_metadata_only_parity_accepts_signature_and_refuses_changed_code(self):
        signed = Path(self.temp.name) / 'signed.apk'
        with zipfile.ZipFile(self.apk, 'w') as packed: packed.writestr('classes.dex', b'tested code')
        with zipfile.ZipFile(signed, 'w') as packed:
            packed.writestr('classes.dex', b'tested code'); packed.writestr('META-INF/CERT.RSA', b'signature fixture')
        artifact.payload_parity(self.apk, signed)
        with zipfile.ZipFile(signed, 'w') as packed: packed.writestr('classes.dex', b'changed code')
        with self.assertRaises(ValueError): artifact.payload_parity(self.apk, signed)

    def test_distribution_approval_protected_environment_and_no_auto_signing(self):
        import yaml
        workflow = yaml.safe_load((ROOT / '.github/workflows/supernova-0141-qa.yml').read_text())
        trigger = workflow.get('on', workflow.get(True)); self.assertEqual({'workflow_dispatch', 'push'}, set(trigger))
        self.assertEqual({'branches': ['codex/supernova-0.141-implementation-2026-10-22'], 'paths': ['.github/workflows/supernova-0141-qa.yml']}, trigger['push'])
        self.assertFalse(trigger['workflow_dispatch']['inputs']['approved_qa_distribution']['default'])
        job = workflow['jobs']['qa']; self.assertEqual('supernova-foundation-signing', job['environment'])
        self.assertIn("github.event_name == 'workflow_dispatch'", job['if'])
        self.assertIn('inputs.approved_qa_distribution == true', job['if'])
        self.assertIn('refs/heads/codex/supernova-0.141-implementation-2026-10-22', job['if'])
        self.assertNotIn('secrets.', json.dumps(job.get('env', {})))
        frozen = '4f0937f1fe8826f50d2c0e844b0837e8a2360a46'
        self.assertEqual(frozen, job['env']['SUPERNOVA_QA_SOURCE'])
        self.assertEqual(frozen, job['steps'][0]['with']['ref'])
        self.assertIn('merge-base --is-ancestor', job['steps'][1]['run'])
        self.assertIn('rev-parse HEAD', job['steps'][1]['run'])
        steps = job['steps']; gates = [i for i, step in enumerate(steps) if 'record-supernova-0141-conformance.py' in step.get('run', '')]
        secrets = [i for i, step in enumerate(steps) if 'secrets.' in json.dumps(step)]
        self.assertEqual(1, len(secrets)); self.assertGreater(secrets[0], gates[0])
        self.assertNotIn('actions/create-release', json.dumps(workflow))


if __name__ == '__main__': unittest.main()
