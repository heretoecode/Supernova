import hashlib
import importlib.util
import json
from pathlib import Path
import subprocess
import tempfile
import unittest
from unittest.mock import patch
import zipfile

ROOT=Path(__file__).resolve().parents[1]
def load(name):
    spec=importlib.util.spec_from_file_location(name,ROOT/'.github/build'/name)
    module=importlib.util.module_from_spec(spec);spec.loader.exec_module(module);return module
conformance=load('verify-foundation.py')
runtime=load('verify-foundation-runtime.py')

class FoundationConformanceTests(unittest.TestCase):
    def setUp(self):
        self.temp=tempfile.TemporaryDirectory();self.addCleanup(self.temp.cleanup);self.root=Path(self.temp.name)
        self.video=self.root/'Video';(self.video/'assets/foundation/legal').mkdir(parents=True);(self.root/'MediaLib').mkdir()
        (self.video/'docs/foundation').mkdir(parents=True)
        self.manifest='<manifest xmlns:android="http://schemas.android.com/apk/res/android"><application android:label="Supernova">'+''.join('<provider android:authorities="app.supernova.player.'+suffix+'"/>' for suffix in ['media','scraper','browser','provider'])+'</application></manifest>'
        (self.video/'AndroidManifest.xml').write_text(self.manifest)
        (self.video/'assets/foundation/release-history.json').write_text(json.dumps({'current':{'version':'0.2','version_code':2},'history':[{'version':'0.1','archive_digest_verified':True,'apks':[{'sha256':'a'*64}]}]}))
        (self.video/'assets/foundation/licences.json').write_text(json.dumps({'components':[{'text_asset':'foundation/legal/notice.txt','url':'https://example.org/'}]}))
        (self.video/'assets/foundation/legal/notice.txt').write_text('Test legal text. '*100)
        self.apk=self.root/'candidate.apk';self.package()
    def package(self):
        with zipfile.ZipFile(self.apk,'w')as z:
            for source in (self.video/'assets').rglob('*'):
                if source.is_file():z.writestr('assets/'+str(source.relative_to(self.video/'assets')),source.read_bytes())
            for abi in ['arm64-v8a','armeabi-v7a','x86','x86_64']:z.writestr('lib/'+abi+'/library.so','fixture; not an ELF binary')
    def inspect(self,command,**kwargs):
        outputs={'application-id':'app.supernova.player','version-name':'0.2','version-code':'2','print':self.manifest}
        return subprocess.CompletedProcess(command,0,outputs[command[2]],'')
    def test_actual_package_version_providers_and_offline_text_are_checked(self):
        conformance.sources(self.root)
        with patch.object(conformance.subprocess,'run',side_effect=self.inspect):
            self.assertEqual(conformance.binary(self.apk,'analyzer',self.video)['version_name'],'0.2')
            (self.video/'assets/foundation/legal/notice.txt').write_text('Different reviewed legal text '*100)
            with self.assertRaises(ValueError):conformance.binary(self.apk,'analyzer',self.video)
    def test_binary_legacy_provider_and_wrong_version_are_refused(self):
        self.manifest=self.manifest.replace('app.supernova.player.media','com.archos.media.videocommunity')
        with patch.object(conformance.subprocess,'run',side_effect=self.inspect),self.assertRaises(ValueError):conformance.binary(self.apk,'analyzer',self.video)
        self.manifest=(self.video/'AndroidManifest.xml').read_text()
        def wrong(command,**kwargs):
            result=self.inspect(command,**kwargs)
            if command[2]=='version-name':result.stdout='0.1.0'
            return result
        with patch.object(conformance.subprocess,'run',side_effect=wrong),self.assertRaises(ValueError):conformance.binary(self.apk,'analyzer',self.video)
    def test_readiness_requires_named_gates_and_no_blockers(self):
        p=self.video/'docs/foundation/READINESS.json'
        p.write_text(json.dumps({'release_ready':True,'gates':{}}))
        with self.assertRaises(ValueError):conformance.sources(self.root,True)
        p.write_text(json.dumps({'release_ready':True,'blockers':['unresolved source'],'gates':dict.fromkeys(['unit_tests','binary_conformance','native_source_compliance','protected_key_validation','foundation_review'],True)}))
        with self.assertRaises(ValueError):conformance.sources(self.root,True)
    def test_resolved_hash_drift_is_refused_before_signing(self):
        archive=self.root/'library.jar';archive.write_bytes(b'fixture')
        inventory=self.root/'inventory.json';reviewed=self.root/'reviewed.json'
        inventory.write_text(json.dumps([{'coordinate':'example:library:1','file':str(archive)}]));reviewed.write_text(json.dumps({'artifacts':[{'coordinate':'example:library:1','filename':archive.name,'sha256':hashlib.sha256(archive.read_bytes()).hexdigest()}]}))
        self.assertEqual(1,runtime.verify(inventory,reviewed));archive.write_bytes(b'changed')
        with self.assertRaises(ValueError):runtime.verify(inventory,reviewed)

if __name__=='__main__':unittest.main()
