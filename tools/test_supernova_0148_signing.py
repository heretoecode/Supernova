"""The0.148 key gate rejects source, payload and validation drift without secrets."""
import copy,hashlib,importlib.util,json,sys,tempfile,unittest
from pathlib import Path
ROOT=Path(__file__).resolve().parents[1];sys.path.insert(0,str(ROOT/'.github/signing'))
spec=importlib.util.spec_from_file_location('qa146',ROOT/'.github/signing/supernova_0148_qa_signing.py');qa=importlib.util.module_from_spec(spec);spec.loader.exec_module(qa)
class Signing148Test(unittest.TestCase):
 def setUp(self):
  self.directory=tempfile.TemporaryDirectory();self.addCleanup(self.directory.cleanup);self.apk=Path(self.directory.name)/'candidate.apk';self.apk.write_bytes(b'unsigned gate fixture');self.source='a'*40
  self.evidence={'implementation_base':qa.IMPLEMENTATION_BASE,'implementation_trees':copy.deepcopy(qa.IMPLEMENTATION_TREES),'implementation_source_unchanged':True,'source_commit':self.source,'application_id':'app.supernova.player','version_name':'0.148','version_code':148,'signed':False,'unsigned_signature_rejected':True,'runtime_artifact_hashes_matched':139,'rebuilt_ffmpeg_libraries_matched':24,'branding_resource_checks':True,'build_result':'BUILD SUCCESSFUL','unit_tests':1258,'unit_failures':0,'unit_errors':0,'unit_skips':0,'lint':{'errors':0},'required_0148_test_classes':sorted(qa.REQUIRED_TEST_CLASSES),'native_hashes_matched':84,'offline_licence_entries':64,'actual_android23_transport_and_trust_tests':28,'native_security_artifact_run':40843685431,'library_suites':{'MediaLib':{'tests':200,'failures':0,'errors':0,'skipped':2},'FileCoreLibrary':{'tests':104,'failures':0,'errors':0,'skipped':2}},'apk_sha256':hashlib.sha256(self.apk.read_bytes()).hexdigest(),'apk_bytes':self.apk.stat().st_size}
 def testCompleteEvidencePassesButEveryCriticalBoundaryFailsClosed(self):
  qa.gate(self.evidence,self.apk,self.source)
  for key,value in [('source_commit','b'*40),('version_name','0.145'),('version_code',145),('application_id','org.courville.nova'),('signed',True),('unsigned_signature_rejected',False),('unit_tests',1257),('unit_failures',1),('unit_errors',1),('unit_skips',1),('native_hashes_matched',88),('rebuilt_ffmpeg_libraries_matched',23),('actual_android23_transport_and_trust_tests',22),('native_security_artifact_run',40780058727),('runtime_artifact_hashes_matched',0),('apk_sha256','0'*64),('apk_bytes',0),('required_0148_test_classes',[])]:
   with self.subTest(key=key):
    evidence=copy.deepcopy(self.evidence);evidence[key]=value
    with self.assertRaises(qa.key.SigningError):qa.gate(evidence,self.apk,self.source)
 def testLibraryFailuresAndPostValidationPayloadChangesAreRejected(self):
  for module in ['MediaLib','FileCoreLibrary']:
   evidence=copy.deepcopy(self.evidence);evidence['library_suites'][module]['failures']=1
   with self.assertRaises(qa.key.SigningError):qa.gate(evidence,self.apk,self.source)
  self.apk.write_bytes(b'changed payload')
  with self.assertRaises(qa.key.SigningError):qa.gate(self.evidence,self.apk,self.source)
 def testUnapprovedBranchIsRejectedBeforeAnyKeystoreAccess(self):
  receipt=Path(self.directory.name)/'proof.json';receipt.write_text(json.dumps(self.evidence))
  for branch in ['refs/heads/codex/supernova-0.146-upstream-completion-2026-10-28','refs/heads/main','refs/heads/codex/supernova-0.145-maintenance-2026-10-28','refs/heads/codex/supernova-0.148-maintenance-2026-10-28']:
   with self.subTest(branch=branch),self.assertRaises(qa.key.SigningError):qa.prepare(receipt,self.apk,{'SUPERNOVA_QA_SOURCE':self.source,'GITHUB_REF':branch})

 def testValidatedImplementationCannotBeChangedOrMerelyClaimed(self):
  for field,bad in [('implementation_base','0'*40),('implementation_trees',{}),('implementation_source_unchanged',False)]:
   altered=copy.deepcopy(self.evidence);altered[field]=bad
   with self.subTest(field=field),self.assertRaises(qa.key.SigningError):qa.gate(altered,self.apk,self.source)
  altered=copy.deepcopy(self.evidence);altered['implementation_trees']['src/main']='0'*40
  with self.assertRaises(qa.key.SigningError):qa.gate(altered,self.apk,self.source)
 def testWorkflowKeepsExistingProtectedDispatchOnlyProcess(self):
  import yaml
  workflow=yaml.safe_load((ROOT/'.github/workflows/supernova-0148-qa.yml').read_text())
  job=workflow['jobs']['qa'];self.assertEqual('supernova-foundation-signing',job['environment'])
  self.assertIn("github.event_name == 'workflow_dispatch'",job['if'])
  self.assertIn('refs/heads/codex/supernova-next-implementation-2026-10-30',job['if'])
  steps=job['steps'];secret_steps=[n for n,step in enumerate(steps) if 'secrets.' in json.dumps(step)]
  self.assertEqual(len(secret_steps),1)
  verification=next(n for n,step in enumerate(steps) if step['name'].startswith('Verify successful conformance'))
  self.assertLess(verification,secret_steps[0])
  self.assertIn('verify-supernova-0148-scope.py',steps[verification]['run'])
  self.assertIn('only subsequent report updates',steps[verification]['name'])
  self.assertNotIn('secrets.',json.dumps(job.get('env',{})))
  self.assertEqual(qa.CERTIFICATE,steps[secret_steps[0]]['env']['SUPERNOVA_CERT_SHA256'])
  self.assertTrue(any(step.get('if')=='always()' and 'cleanup' in step.get('run','') for step in steps))

 def testWrongCertificateCannotReachKeystoreOnTheAuthorisedBranch(self):
  from unittest.mock import patch
  receipt=Path(self.directory.name)/'proof.json';receipt.write_text(json.dumps(self.evidence))
  env={'SUPERNOVA_QA_SOURCE':self.source,'GITHUB_REF':'refs/heads/codex/supernova-next-implementation-2026-10-30','SUPERNOVA_CERT_SHA256':'0'*64}
  with patch.object(qa.key,'decode_keystore') as decode:
   with self.assertRaises(qa.key.SigningError):qa.prepare(receipt,self.apk,env)
   decode.assert_not_called()
