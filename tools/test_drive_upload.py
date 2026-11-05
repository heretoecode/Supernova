"""Drive delivery: never blocks the signed build when unconfigured, never leaks credentials."""
import contextlib, hashlib, importlib.util, io, json, tempfile, unittest, urllib.error
from pathlib import Path
from unittest import mock

ROOT = Path(__file__).resolve().parents[1]
spec = importlib.util.spec_from_file_location('drive_upload', ROOT / '.github/delivery/drive_upload.py')
drive = importlib.util.module_from_spec(spec)
spec.loader.exec_module(drive)

SECRETS = {'GDRIVE_CLIENT_ID': 'client-id-SECRET', 'GDRIVE_CLIENT_SECRET': 'client-secret-SECRET', 'GDRIVE_REFRESH_TOKEN': 'refresh-SECRET'}


class DriveUploadTest(unittest.TestCase):
    def setUp(self):
        self.directory = tempfile.TemporaryDirectory()
        self.apk = Path(self.directory.name) / 'Supernova_0.151.apk'
        self.apk.write_bytes(b'signed apk payload')
        self.receipt = Path(self.directory.name) / 'receipt.json'

    def tearDown(self):
        self.directory.cleanup()

    def run_main(self, env):
        argv = ['drive_upload.py', '--apk', str(self.apk), '--receipt', str(self.receipt)]
        out, err = io.StringIO(), io.StringIO()
        with mock.patch('sys.argv', argv), mock.patch.dict('os.environ', env, clear=True), \
                contextlib.redirect_stdout(out), contextlib.redirect_stderr(err):
            code = drive.main()
        return code, out.getvalue() + err.getvalue()

    def testMissingCredentialsSkipDeliveryWithoutFailingTheBuild(self):
        code, output = self.run_main({})
        self.assertEqual(0, code)
        self.assertFalse(json.loads(self.receipt.read_text())['delivered'])
        self.assertIn('not configured', output)

    def testHttpFailureNeverEchoesCredentials(self):
        error = urllib.error.HTTPError('https://oauth2.googleapis.com/token', 400, 'Bad Request', {}, io.BytesIO(b'refresh-SECRET'))
        with mock.patch('urllib.request.urlopen', side_effect=error):
            code, output = self.run_main(SECRETS)
        self.assertEqual(1, code)
        for value in SECRETS.values():
            self.assertNotIn(value, output)

    def testSuccessfulDeliveryRequiresByteEqualReadback(self):
        digest = hashlib.sha256(self.apk.read_bytes()).hexdigest()
        with mock.patch.object(drive, 'access_token', return_value='t'), mock.patch.object(drive, 'folder', return_value='f'), \
                mock.patch.object(drive, 'upload', return_value='file123'), mock.patch.object(drive, 'readback_sha256', return_value=digest):
            code, _ = self.run_main(SECRETS)
        self.assertEqual(0, code)
        receipt = json.loads(self.receipt.read_text())
        self.assertTrue(receipt['delivered'] and receipt['complete_drive_readback_byte_equal'])
        self.assertIn('file123', receipt['url'])
        with mock.patch.object(drive, 'access_token', return_value='t'), mock.patch.object(drive, 'folder', return_value='f'), \
                mock.patch.object(drive, 'upload', return_value='file123'), mock.patch.object(drive, 'readback_sha256', return_value='0' * 64):
            code, output = self.run_main(SECRETS)
        self.assertEqual(1, code)
        self.assertIn('do not install', output)

    def testScopeIsLimitedToFilesTheWorkflowCreated(self):
        source = (ROOT / '.github/delivery/drive_upload.py').read_text()
        self.assertIn('drive.file', source)
        self.assertNotIn("auth/drive'", source)


if __name__ == '__main__':
    unittest.main()
