import argparse,collections,hashlib,io,json,subprocess,zipfile
from pathlib import Path
p=argparse.ArgumentParser();p.add_argument('full');p.add_argument('candidate');p.add_argument('--full-sha',required=True);p.add_argument('--candidate-sha',required=True);p.add_argument('--source',required=True);p.add_argument('--apk-output',required=True);p.add_argument('--apksigner-jar',required=True);p.add_argument('--out',required=True);a=p.parse_args()
def sha(data):return hashlib.sha256(data).hexdigest()
def checked_archive(path,expected):
 b=Path(path).read_bytes();assert sha(b)==expected,(path,sha(b),expected)
 z=zipfile.ZipFile(io.BytesIO(b));assert z.testzip() is None
 return z
full=checked_archive(a.full,a.full_sha);candidate=checked_archive(a.candidate,a.candidate_sha)
receipt=json.loads(candidate.read('unsigned-conformance.json'));assert receipt['source_commit']==a.source
assert receipt['signed'] is False and receipt['unsigned_signature_rejected'] is True
assert receipt['version_name']=='0.148' and receipt['version_code']==148
assert receipt['application_id']=='app.supernova.player'
apk_bytes=candidate.read('Supernova_0.148_unsigned.apk');assert sha(apk_bytes)==receipt['apk_sha256'] and len(apk_bytes)==receipt['apk_bytes']
apk=zipfile.ZipFile(io.BytesIO(apk_bytes));assert apk.testzip() is None
apk_output=Path(a.apk_output);apk_output.parent.mkdir(parents=True,exist_ok=True);apk_output.write_bytes(apk_bytes)
unsigned_signature=subprocess.run(['java','-jar',a.apksigner_jar,'verify','--verbose','--min-sdk-version','23','--max-sdk-version','36',str(apk_output)],capture_output=True,text=True)
assert unsigned_signature.returncode!=0,'Conformance payload must be unsigned'
native={n:sha(apk.read(n)) for n in apk.namelist() if n.startswith('lib/') and n.endswith('.so')}
assert native=={item['path']:item['sha256'] for item in receipt['native_libraries']}
assert len(native)==84==receipt['native_hashes_matched']
input_name=next(n for n in full.namelist() if n.endswith('/packaging-inputs.json'))
input_bytes=full.read(input_name);assert sha(input_bytes)==receipt['native_build_evidence_sha256']
inputs=json.loads(input_bytes);assert inputs['source_commit']==a.source
input_matches=0;input_paths=set()
for item in inputs['libraries']:
 path='lib/'+item['path'].split('/libs/',1)[1]
 assert native[path]==item['sha256'],path
 input_matches+=1;input_paths.add(path)
inventory_name=next(n for n in full.namelist() if n.endswith('/supernova146-runtime-review/inventory.json'))
prefix=inventory_name.removesuffix('inventory.json');inventory=json.loads(full.read(inventory_name))
sbom=json.loads(subprocess.check_output(['git','show',a.source+':docs/foundation/RUNTIME_SBOM.json']))['artifacts']
expected=collections.Counter((i['coordinate'],i['configuration'],i['filename'],i['sha256'],i['pom_sha256']) for i in sbom)
observed=collections.Counter()
for item in inventory:
 data=full.read(prefix+item['file']);assert sha(data)==item['sha256'],item['file']
 base=item['file'].rsplit('/archive/',1)[0]
 pom_names=[n for n in full.namelist() if n.startswith(prefix+base+'/pom/') and n.endswith('.pom')]
 assert len(pom_names)==1,(base,pom_names)
 assert sha(full.read(pom_names[0]))==item['pom_sha256'],pom_names
 observed[(item['coordinate'],item['configuration'],item['file'].rsplit('/',1)[1],item['sha256'],item['pom_sha256'])]+=1
assert observed==expected
remaining={path:digest for path,digest in native.items() if path not in input_paths};runtime_native={}
for item in inventory:
 if not item['file'].endswith('.aar'):continue
 with zipfile.ZipFile(io.BytesIO(full.read(prefix+item['file']))) as aar:
  for path,digest in remaining.items():
   member='jni/'+path.removeprefix('lib/')
   if member in aar.namelist() and sha(aar.read(member))==digest:runtime_native[path]=item['coordinate']
assert set(runtime_native)==set(remaining),(remaining,runtime_native)
assert len(input_paths)+len(runtime_native)==len(native)
assert len(inventory)==139==receipt['runtime_artifact_hashes_matched']
out=Path(a.out);out.mkdir(parents=True,exist_ok=True)
(out/'UNSIGNED_CONFORMANCE.json').write_text(json.dumps(receipt,indent=2)+'\n')
summary={'source_commit':a.source,'candidate_archive_sha256':a.candidate_sha,'candidate_archive_bytes':Path(a.candidate).stat().st_size,'conformance_archive_sha256':a.full_sha,'independent_archive_digest_and_crc':'pass','apk_file':'Supernova_0.148_unsigned.apk','apk_sha256':sha(apk_bytes),'apk_bytes':len(apk_bytes),'apk_crc':'pass','independent_packaged_native_hashes_verified':len(native),'independent_native_packaging_input_hash_records_matched':input_matches,'distinct_packaged_native_files_from_inputs':len(input_paths),'independent_native_files_matched_to_pinned_runtime_aars':len(runtime_native),'runtime_native_provenance':runtime_native,'all_packaged_native_files_have_independent_input_provenance':True,'native_build_evidence_digest_matches_receipt':True,'independent_runtime_archive_and_pom_hashes_verified':len(inventory),'distinct_runtime_archive_paths':len({i['file'] for i in inventory}),'independent_runtime_sbom_match':True,'version_name':receipt['version_name'],'version_code':receipt['version_code'],'unsigned_signature_status':'CI and independent local pinned apksigner JAR rejected the unsigned signature as required.','physical_shield':'NOT RUN','signed_upgrade':'NOT RUN','drive_upload':False}
(out/'INDEPENDENT_UNSIGNED_APK_VERIFICATION.json').write_text(json.dumps(summary,indent=2)+'\n')
print(json.dumps(summary,indent=2))
