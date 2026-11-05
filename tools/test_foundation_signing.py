#!/usr/bin/env python3
"""Fail-closed tests using dummy bytes/empty stores; never create a signing key."""
import base64
import importlib.util
import os
from pathlib import Path
import subprocess
import tempfile
import unittest
from unittest.mock import patch
import zipfile

ROOT = Path(__file__).resolve().parents[1]

def load(name):
    spec = importlib.util.spec_from_file_location(name, ROOT / '.github/signing' / (name + '.py'))
    module = importlib.util.module_from_spec(spec)
    spec.loader.exec_module(module)
    return module

signing = load('foundation_signing')
apkcheck = load('verify_foundation_apk')
FINGERPRINT = 'a' * 64

class SigningTests(unittest.TestCase):
    def setUp(self):
        self.temp = tempfile.TemporaryDirectory()
        self.addCleanup(self.temp.cleanup)
        self.env = dict(os.environ, RUNNER_TEMP=self.temp.name,
                        SUPERNOVA_P12_BASE64=base64.b64encode(b'not a private key').decode(),
                        SUPERNOVA_STORE_PASSWORD='dummy $ ` \\ : = password',
                        SUPERNOVA_KEY_ALIAS='dummy-alias', SUPERNOVA_KEY_PASSWORD='dummy-other-password',
                        SUPERNOVA_CERT_SHA256=FINGERPRINT,
                        GITHUB_STEP_SUMMARY=self.temp.name + '/summary', GITHUB_ENV=self.temp.name + '/env')
        self.env.pop('RUNNER_DEBUG', None)
        self.env.pop('ACTIONS_STEP_DEBUG', None)

    def test_each_missing_secret_stops_before_write_or_verification(self):
        for name in signing.SECRET_NAMES:
            env = dict(self.env)
            del env[name]
            with self.subTest(name=name), patch.object(signing, 'verify') as verify:
                with self.assertRaisesRegex(signing.SigningError, name):
                    signing.execute('validate', env)
                verify.assert_not_called()
                self.assertFalse(signing.store_path(env).exists())

    def test_inspection_requires_only_store_secrets(self):
        env = dict(self.env)
        del env['SUPERNOVA_KEY_ALIAS'], env['SUPERNOVA_KEY_PASSWORD']
        signing.preflight('inspect', env)
        del env['SUPERNOVA_STORE_PASSWORD']
        with self.assertRaises(signing.SigningError): signing.preflight('inspect', env)

    def test_decode_strict_standard_base64_with_ios_wrapping(self):
        self.assertEqual(signing.decode_keystore('bm90\r\nIGEgcHJpdmF0ZSBrZXk=\t '), b'not a private key')
        for value in ('', ' ', 'not-base64!', '\u2603', 'Zg', 'a' * (48 * 1024 + 1)):
            with self.subTest(value=value[:20]), self.assertRaises(signing.SigningError):
                signing.decode_keystore(value)

    def test_readiness_pin_and_debug_guards(self):
        for updates in ({}, {'FOUNDATION_RELEASE_READY':'true', 'SUPERNOVA_CERT_SHA256':''},
                        {'FOUNDATION_RELEASE_READY':'true', 'SUPERNOVA_CERT_SHA256':signing.PREVIEW_CERT_SHA256}):
            with self.assertRaises(signing.SigningError): signing.preflight('prepare', dict(self.env, **updates))
        signing.preflight('prepare', dict(self.env, FOUNDATION_RELEASE_READY='true'))
        for updates in ({'RUNNER_DEBUG':'1'}, {'ACTIONS_STEP_DEBUG':'true'}):
            with self.assertRaises(signing.SigningError): signing.preflight('validate', dict(self.env, **updates))
        with self.assertRaises(signing.SigningError): signing.normalize_fingerprint(signing.PREVIEW_CERT_SHA256)

    def test_permissions_password_preservation_and_validation_cleanup(self):
        def verify(mode, path, env):
            self.assertEqual(path.stat().st_mode & 0o777, 0o600)
            self.assertEqual(path.parent.stat().st_mode & 0o777, 0o700)
            self.assertEqual(path.read_bytes(), b'not a private key')
            self.assertEqual(env['SUPERNOVA_STORE_PASSWORD'], self.env['SUPERNOVA_STORE_PASSWORD'])
            return FINGERPRINT
        with patch.object(signing, 'verify', side_effect=verify):
            signing.execute('validate', self.env)
        self.assertFalse(signing.store_path(self.env).exists())
        summary = Path(self.env['GITHUB_STEP_SUMMARY']).read_text()
        self.assertIn(FINGERPRINT, summary)
        for name in signing.SECRET_NAMES: self.assertNotIn(self.env[name], summary)

    def test_prepare_exports_only_path_then_cleanup(self):
        env = dict(self.env, FOUNDATION_RELEASE_READY='true')
        with patch.object(signing, 'verify', return_value=FINGERPRINT): signing.execute('prepare', env)
        self.assertTrue((signing.store_path(env) / 'foundation.p12').is_file())
        self.assertEqual(Path(env['GITHUB_ENV']).read_text(), 'SUPERNOVA_SIGNING_STORE_FILE=' + str(signing.store_path(env) / 'foundation.p12') + '\n')
        signing.remove_store(signing.store_path(env))
        self.assertFalse(signing.store_path(env).exists())

    def test_failure_cleanup_and_raw_java_output_suppression(self):
        raw = self.env['SUPERNOVA_STORE_PASSWORD']
        with patch.object(signing.subprocess, 'run', return_value=subprocess.CompletedProcess([], 1, raw, raw)) as run:
            with self.assertRaises(signing.SigningError) as caught: signing.execute('validate', self.env)
            self.assertNotIn(raw, str(caught.exception))
            self.assertFalse(signing.store_path(self.env).exists())
            args, kwargs = run.call_args
            self.assertNotIn(raw, str(args))
            self.assertNotIn('SUPERNOVA_P12_BASE64', kwargs['env'])

    def test_symlink_refused_without_touching_target(self):
        target = Path(self.temp.name) / 'keep'
        target.mkdir()
        (target / 'keep').write_text('keep')
        directory = signing.store_path(self.env)
        directory.symlink_to(target, target_is_directory=True)
        with self.assertRaises(signing.SigningError): signing.execute('validate', self.env)
        with self.assertRaises(signing.SigningError): signing.remove_store(directory)
        self.assertEqual((target / 'keep').read_text(), 'keep')

    def test_inspect_alias_escaped_and_no_key_password_needed(self):
        env = dict(self.env)
        del env['SUPERNOVA_KEY_ALIAS'], env['SUPERNOVA_KEY_PASSWORD']
        with patch.object(signing, 'verify', return_value=[{'alias':'<b>& public alias', 'certificate_sha256':FINGERPRINT}]):
            signing.execute('inspect', env)
        self.assertIn('&lt;b&gt;&amp;', Path(env['GITHUB_STEP_SUMMARY']).read_text())
        self.assertFalse(signing.store_path(env).exists())

    def test_java_rejects_corruption_wrong_password_empty_pkcs12_and_jks(self):
        # Empty containers contain NO generated keys or certificates.
        helper = Path(self.temp.name) / 'EmptyStore.java'
        helper.write_text('''import java.security.KeyStore; import java.nio.file.*;
class EmptyStore { public static void main(String[] a) throws Exception {
char[] p = System.getenv("SUPERNOVA_STORE_PASSWORD").toCharArray();
KeyStore k = KeyStore.getInstance(a[0]); k.load(null,p);
try(var out=Files.newOutputStream(Path.of(a[1]))) { k.store(out,p); }
}}''')
        cases = [b'corrupt']
        for kind in ('PKCS12', 'JKS'):
            path = Path(self.temp.name) / kind
            subprocess.run(['java', str(helper), kind, str(path)], env=self.env, capture_output=True, check=True)
            cases.append(path.read_bytes())
        for data in cases:
            for password in (self.env['SUPERNOVA_STORE_PASSWORD'], 'wrong-password'):
                with self.subTest(length=len(data), password=password):
                    env = dict(self.env, SUPERNOVA_P12_BASE64=base64.b64encode(data).decode(), SUPERNOVA_STORE_PASSWORD=password)
                    with self.assertRaises(signing.SigningError): signing.execute('validate', env)
                    self.assertFalse(signing.store_path(env).exists())

class ApkTests(unittest.TestCase):
    def test_package_cert_manifest_and_embedded_key_gate(self):
        with tempfile.TemporaryDirectory() as temp:
            apk = Path(temp) / 'dummy.apk'
            with zipfile.ZipFile(apk, 'w') as z: z.writestr('AndroidManifest.xml', 'dummy')
            good = ['Signer #1 certificate SHA-256 digest: ' + FINGERPRINT, 'app.supernova.player', '<manifest/>']
            with patch.object(apkcheck, 'checked', side_effect=good):
                self.assertEqual(apkcheck.verify(apk, 'apksigner', 'apkanalyzer', FINGERPRINT)['application_id'], 'app.supernova.player')
            for outputs in ([good[0].replace(FINGERPRINT, 'b'*64)], [good[0], 'org.courville.nova.markpreview'],
                            [*good[:2], '<provider android:authorities="com.archos.media.videocommunity"/>']):
                with patch.object(apkcheck, 'checked', side_effect=outputs), self.assertRaises(ValueError):
                    apkcheck.verify(apk, 'apksigner', 'apkanalyzer', FINGERPRINT)
            with zipfile.ZipFile(apk, 'a') as z: z.writestr('assets/foundation.p12', 'dummy')
            with patch.object(apkcheck, 'checked', side_effect=good), self.assertRaises(ValueError):
                apkcheck.verify(apk, 'apksigner', 'apkanalyzer', FINGERPRINT)
            with self.assertRaises(ValueError): apkcheck.verify(apk, 'apksigner', 'apkanalyzer', signing.PREVIEW_CERT_SHA256)

if __name__ == '__main__': unittest.main()
