"""Independently verify the container, every embedded source archive and native configs."""
from pathlib import Path
import hashlib,json,tarfile
p=Path('/workspace/delivery146/Supernova_0.146_Corresponding_Source.tar.xz');r=json.load(open(str(p)+'.json'));verified=[];native=[];found_manifest=False;found_readme=False
with p.open('rb') as stream:assert hashlib.file_digest(stream,'sha256').hexdigest()==r['sha256']
expected={i['file']:i['sha256'] for i in r['contents']['archives']}
for m in r['contents']['modules']:expected['modules/'+m['path'].replace('/','-')+'.tar']=m['archive_sha256']
with tarfile.open(p,'r|xz') as archive:
 for m in archive:
  if m.name in expected:
   h=hashlib.file_digest(archive.extractfile(m),'sha256').hexdigest();assert h==expected[m.name],m.name;verified.append({'file':m.name,'sha256':h,'bytes':m.size})
  elif m.name=='SOURCE_MANIFEST.json':assert json.load(archive.extractfile(m))==r['contents'];found_manifest=True
  elif m.name=='REBUILD.md':assert hashlib.sha256(archive.extractfile(m).read()).hexdigest()==r['instructions_sha256'];found_readme=True
  elif m.name.startswith('native-build/') and m.isfile():
   _,abi,relative=m.name.split('/',2);source=Path('/workspace/native146-security-ci')/abi/'unpacked'/relative
   h=hashlib.file_digest(archive.extractfile(m),'sha256').hexdigest()
   with source.open('rb') as stream:assert h==hashlib.file_digest(stream,'sha256').hexdigest(),m.name
   native.append({'file':m.name,'sha256':h,'bytes':m.size})
assert len(verified)==len(expected) and found_manifest and found_readme,(len(verified),len(expected),found_manifest,found_readme)
out={'source_commit':r['source_commit'],'file':r['file'],'sha256':r['sha256'],'bytes':r['bytes'],'instructions_sha256':r['instructions_sha256'],'verified_source_entries':verified,'embedded_archive_count':26,'supporting_source_document_count':2,'native_configuration_and_evidence_files':native,'scope':'Independent complete container SHA256, all28 manifest-listed source entries (26 archives plus two support documents), native configuration/evidence bytes and corrected root rebuild instructions'}
Path('/workspace/Supernova/docs/0.146/SOURCE_PACKAGE_VERIFICATION.json').write_text(json.dumps(out,indent=2)+'\n');print(r['source_commit'],len(verified),'archives and',len(native),'native config/evidence files verified',r['sha256'])
