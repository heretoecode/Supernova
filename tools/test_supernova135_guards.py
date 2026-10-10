import copy
import importlib.util
import json
from pathlib import Path
import shutil
import tempfile
import unittest

ROOT = Path(__file__).resolve().parents[1]
spec = importlib.util.spec_from_file_location('supernova135_conformance', ROOT / '.github/build/verify-supernova135.py')
conformance = importlib.util.module_from_spec(spec)
spec.loader.exec_module(conformance)


class Supernova135GuardTests(unittest.TestCase):
    def setUp(self):
        self.temp = tempfile.TemporaryDirectory()
        self.addCleanup(self.temp.cleanup)
        self.video = Path(self.temp.name)
        (self.video / 'docs/foundation').mkdir(parents=True)
        self.proof = 'docs/foundation/UI_CORRECTED_SIGNED_APK_VERIFICATION.json'
        shutil.copyfile(ROOT / self.proof, self.video / self.proof)
        self.entry = copy.deepcopy(next(entry for entry in json.loads((ROOT / 'assets/foundation/release-history.json').read_text())['history'] if entry['version'] == '0.134'))

    def test_preserved_binary_record_is_distinct_from_fresh_archive_verification(self):
        self.assertIs(self.entry['archive_digest_verified'], False)
        self.assertTrue(conformance.verified_baseline_record(self.video, self.entry))

    def test_record_cannot_authorize_a_different_source_or_apk(self):
        for field in ['source_sha', 'version']:
            altered = copy.deepcopy(self.entry)
            altered[field] = 'unapproved'
            self.assertFalse(conformance.verified_baseline_record(self.video, altered))
        altered = copy.deepcopy(self.entry)
        altered['apks'][0]['sha256'] = '0' * 64
        self.assertFalse(conformance.verified_baseline_record(self.video, altered))

    def test_certificate_or_signature_failure_refuses_the_baseline_record(self):
        for field, value in [('certificate_sha256', '0' * 64), ('signature_verified', False), ('apk_zip_crc_verified', False)]:
            proof = json.loads((ROOT / self.proof).read_text())
            proof[field] = value
            (self.video / self.proof).write_text(json.dumps(proof))
            self.assertFalse(conformance.verified_baseline_record(self.video, self.entry))

    def test_unsigned_workflow_has_no_signing_secrets_or_apk_upload(self):
        import yaml
        workflow = yaml.safe_load((ROOT / '.github/workflows/supernova135-conformance.yml').read_text())
        text = (ROOT / '.github/workflows/supernova135-conformance.yml').read_text()
        self.assertNotIn('secrets.', text)
        self.assertNotIn('-x :MediaLib:ndkBuild', text)
        self.assertNotIn('-x :FileCoreLibrary:ndkBuild', text)
        uploads = [step for step in workflow['jobs']['conformance']['steps'] if step.get('uses', '').startswith('actions/upload-artifact@')]
        self.assertEqual(len(uploads), 1)
        self.assertNotIn('.apk', uploads[0]['with']['path'])


if __name__ == '__main__':
    unittest.main()
