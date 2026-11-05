"""Refresh exact app and FileCore snapshots while retaining verified original archives."""
from pathlib import Path
import argparse,hashlib,io,json,subprocess,tarfile
p=argparse.ArgumentParser(description=__doc__);p.add_argument('--source',required=True);a=p.parse_args()
root=Path('/workspace/Supernova');delivery=Path('/workspace/delivery146');path=delivery/'Supernova_0.146_Corresponding_Source.tar.xz';source=subprocess.check_output(['git','-C',str(root),'rev-parse',a.source],text=True).strip();receipt=json.load(open(str(path)+'.json'));application=delivery/'application-final.tar';filecore=delivery/'FileCoreLibrary-final.tar';fc=Path('/workspace/source146-filecore-final-2')
with application.open('wb') as out:subprocess.run(['git','-C',str(root),'archive','--format=tar',source],stdout=out,check=True)
def sha(p):
 with p.open('rb') as f:return hashlib.file_digest(f,'sha256').hexdigest()
paths=sorted(set(filter(None,subprocess.check_output(['git','-C',str(fc),'ls-files','--cached','--others','--exclude-standard','-z']).decode().split('\0'))))
with tarfile.open(filecore,'w') as archive:
 for name in paths:
  f=fc/name
  if f.exists() or f.is_symlink():archive.add(f,arcname=name,recursive=False)
input_source=receipt['source_commit'];manifest=receipt['contents'];manifest['source_commit']=source
for item in manifest['archives']:
 if item['file']=='modules/application.tar':item['sha256']=sha(application)
for item in manifest['modules']:
 if item['path']=='FileCoreLibrary':item.update(archive_sha256=sha(filecore),source_files=len(paths))
readme=(root/'docs/0.146/SOURCE_REBUILD.md').read_bytes();target=path.with_suffix('.finalizing.xz')
with tarfile.open(path,'r:xz') as old,tarfile.open(target,'w:xz',preset=4) as new:
 for item in old:
  replacement={'modules/application.tar':application,'modules/FileCoreLibrary.tar':filecore}.get(item.name)
  if replacement:new.add(replacement,arcname=item.name)
  elif item.name in ['REBUILD.md','SOURCE_MANIFEST.json']:
   data=readme if item.name=='REBUILD.md' else (json.dumps(manifest,indent=2)+'\n').encode();item.size=len(data);new.addfile(item,io.BytesIO(data))
  else:new.addfile(item,old.extractfile(item) if item.isfile() else None)
receipt.update(source_commit=source,contents=manifest,sha256=sha(target),bytes=target.stat().st_size,instructions_sha256=hashlib.sha256(readme).hexdigest())
old_receipt=delivery/('intermediate/source-receipt-'+input_source[:9]+'.json');old_receipt.write_text(json.dumps(json.load(open(str(path)+'.json')),indent=2)+'\n');target.replace(path);Path(str(path)+'.json').write_text(json.dumps(receipt,indent=2)+'\n');application.unlink();filecore.unlink();print('Final source package',source,receipt['sha256'],receipt['bytes'])
