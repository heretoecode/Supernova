import copy
import hashlib
import importlib
import json
from pathlib import Path
import sys
import tempfile
import unittest
from unittest.mock import patch
import zipfile

ROOT = Path(__file__).resolve().parents[1]
sys.path.insert(0, str(ROOT / '.github/signing'))
qa = importlib.import_module('next_refinement_qa_signing')
apk_check = importlib.import_module('verify_next_refinement_qa_apk')


class NextRefinementQATests(unittest.TestCase):
    def setUp(self):
        self.temp = tempfile.TemporaryDirectory(); self.addCleanup(self.temp.cleanup)
        self.apk = Path(self.temp.name) / 'unsigned.apk'; self.apk.write_bytes(b'fixture APK, no credentials')
        self.source = 'a' * 40
        self.proof = dict(source_commit=self.source, application_id='app.supernova.player', version_name='0.136', version_code=136,
                          signed=False, unsigned_signature_rejected=True, runtime_artifact_hashes_matched=True,
                          rebuilt_ffmpeg_libraries_matched=True, branding_resource_checks=True, build_result='BUILD SUCCESSFUL',
                          unit_tests=qa.MINIMUM_TESTS, unit_failures=0, unit_errors=0, unit_skips=0, lint={'errors': 0},
                          native_hashes_matched=18, offline_licence_entries=5, apk_bytes=self.apk.stat().st_size,
                          apk_sha256=hashlib.sha256(self.apk.read_bytes()).hexdigest())

    def test_exact_source_unsigned_qa_gate(self):
        qa.gate(self.proof, self.apk, self.source)

    def test_failures_skips_incomplete_suite_and_lint_refuse_signing(self):
        for field, value in [('unit_tests', qa.MINIMUM_TESTS - 1), ('unit_failures', 1), ('unit_errors', 1), ('unit_skips', 1), ('lint', {'errors': 1}), ('build_result', 'BUILD FAILED')]:
            with self.subTest(field=field):
                altered = copy.deepcopy(self.proof); altered[field] = value
                with self.assertRaises(qa.key.SigningError): qa.gate(altered, self.apk, self.source)

    def test_source_identity_native_branding_and_signature_gates(self):
        for field, value in [('source_commit', 'b' * 40), ('application_id', 'org.courville.nova'), ('version_code', 134), ('version_name', '0.134'), ('signed', True), ('unsigned_signature_rejected', False), ('runtime_artifact_hashes_matched', False), ('rebuilt_ffmpeg_libraries_matched', False), ('branding_resource_checks', False), ('native_hashes_matched', 0), ('offline_licence_entries', 0)]:
            with self.subTest(field=field):
                altered = copy.deepcopy(self.proof); altered[field] = value
                with self.assertRaises(qa.key.SigningError): qa.gate(altered, self.apk, self.source)

    def test_changed_unsigned_apk_refused(self):
        self.apk.write_bytes(b'changed')
        with self.assertRaises(qa.key.SigningError): qa.gate(self.proof, self.apk, self.source)

    def test_no_key_decoded_before_gate(self):
        evidence = Path(self.temp.name) / 'proof.json'; evidence.write_text('{}')
        with patch.object(qa.key, 'decode_keystore') as decode:
            with self.assertRaises(qa.key.SigningError): qa.prepare(evidence, self.apk, {})
            decode.assert_not_called()

    def test_other_branch_or_certificate_refused_before_key_access(self):
        evidence = Path(self.temp.name) / 'proof.json'; evidence.write_text(json.dumps(self.proof))
        for ref, cert in [('refs/heads/main', qa.CERTIFICATE), ('refs/heads/codex/supernova-next-refinement-2026-10-10', '0' * 64)]:
            env = dict(SUPERNOVA_QA_SOURCE=self.source, GITHUB_REF=ref, SUPERNOVA_CERT_SHA256=cert)
            with patch.object(qa.key, 'decode_keystore') as decode:
                with self.assertRaises(qa.key.SigningError): qa.prepare(evidence, self.apk, env)
                decode.assert_not_called()

    def packed(self, name, data):
        path = Path(self.temp.name) / name
        with zipfile.ZipFile(path, 'w') as archive:
            for entry, value in data.items(): archive.writestr(entry, value)
        return path

    def test_signature_addition_preserves_payload(self):
        original = {'classes.dex': b'code', 'AndroidManifest.xml': b'manifest', 'META-INF/androidx.version': b'1'}
        unsigned = self.packed('u.apk', original)
        signed = self.packed('s.apk', dict(original, **{'META-INF/CERT.SF': b'public signature'}))
        apk_check.payload_parity(unsigned, signed)

    def test_any_tested_payload_mutation_refused(self):
        original = {'classes.dex': b'code', 'META-INF/androidx.version': b'1'}
        for entry in original:
            altered = dict(original); altered[entry] = b'changed'
            with self.assertRaises(ValueError): apk_check.payload_parity(self.packed('u.apk', original), self.packed('s.apk', altered))

    def test_signing_is_dispatch_only_and_secrets_scoped_after_conformance(self):
        import yaml
        text = (ROOT / '.github/workflows/next-refinement-qa.yml').read_text(); workflow = yaml.safe_load(text)
        trigger = workflow.get('on', workflow.get(True)); self.assertEqual(set(trigger), {'push', 'workflow_dispatch'}); self.assertEqual(trigger['push']['paths'], ['.github/workflows/next-refinement-qa.yml'])
        job = workflow['jobs']['qa']; self.assertEqual(job['environment'], 'supernova-foundation-signing'); self.assertIn("github.event_name == 'workflow_dispatch'", job['if']); self.assertIn("refs/heads/codex/supernova-next-refinement-2026-10-10", job['if'])
        self.assertNotIn('secrets.', json.dumps(job.get('env', {})))
        steps = job['steps']; secret_steps = [i for i, step in enumerate(steps) if 'secrets.' in json.dumps(step)]
        gates = [i for i, step in enumerate(steps) if 'record-next-refinement-conformance.py' in step.get('run', '')]
        self.assertEqual(len(secret_steps), 1); self.assertGreater(secret_steps[0], gates[0])
        self.assertNotIn('foundationRelease', text); self.assertNotIn('FOUNDATION_RELEASE_READY', text)
        self.assertIn('env:SUPERNOVA_STORE_PASSWORD', text); self.assertIn('payload parity', text)
        self.assertNotIn('actions/create-release', text); self.assertNotIn('gh release', text)


if __name__ == '__main__': unittest.main()
