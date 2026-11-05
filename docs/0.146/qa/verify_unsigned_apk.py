"""Independent final unsigned payload/manifest/native checks, without any signing key."""
from pathlib import Path
import argparse,hashlib,json,struct,subprocess,zipfile
from apkutils2 import APK
p=argparse.ArgumentParser(description=__doc__);p.add_argument('--apk',type=Path,required=True);p.add_argument('--receipt',type=Path,required=True);p.add_argument('--output',type=Path,required=True);a=p.parse_args();r=json.loads(a.receipt.read_text());source=r['source_commit'];digest=hashlib.file_digest(a.apk.open('rb'),'sha256').hexdigest();assert digest==r['apk_sha256'] and a.apk.stat().st_size==r['apk_bytes']
m=APK(str(a.apk)).get_manifest();assert m['@package']=='app.supernova.player' and m['@android:versionName']=='0.146' and m['@android:versionCode']=='146';assert m['uses-sdk']['@android:minSdkVersion']=='23' and m['uses-sdk']['@android:targetSdkVersion']=='36';assert '@android:sharedUserId' not in m
services=m['application']['service'];services=[services] if isinstance(services,dict) else services;services={s['@android:name']:s for s in services};private=['com.archos.mediacenter.video.player.PlayerService','com.archos.mediaprovider.MediaRetrieverService']
for name in private:assert services[name]['@android:exported']=='false',name
signature=subprocess.run(['java','-cp','/workspace/apksig.jar:/workspace','VerifyApk',str(a.apk)],capture_output=True,text=True);assert signature.returncode!=0 and 'Signature invalid:' in signature.stderr,'Unsigned payload must have no verified signature'
expected={x['path']:x['sha256'] for x in r['native_libraries']};native=[]
with a.apk.open('rb') as raw,zipfile.ZipFile(a.apk) as packed:
 assert packed.testzip() is None;names={n for n in packed.namelist() if n.startswith('lib/') and n.endswith('.so')};assert len(names)==84 and names==expected.keys();assert {n.split('/')[1] for n in names}=={'armeabi-v7a','arm64-v8a','x86','x86_64'}
 assert any(source.encode() in packed.read(n) for n in packed.namelist() if n.startswith('classes') and n.endswith('.dex')),'Exact source SHA must be embedded in dex'
 for name in sorted(names):
  data=packed.read(name);assert hashlib.sha256(data).hexdigest()==expected[name];entry=packed.getinfo(name);raw.seek(entry.header_offset+26);size,extra=struct.unpack('<HH',raw.read(4));offset=entry.header_offset+30+size+extra
  if entry.compress_type==0:assert offset%16384==0,(name,offset)
  else:assert entry.compress_type==8 and m['application']['@android:extractNativeLibs']=='true'
  assert data[:4]==b'\x7fELF';bit=data[4];endian='<' if data[5]==1 else '>'
  if bit==2:phoff=struct.unpack_from(endian+'Q',data,32)[0];entsize,count=struct.unpack_from(endian+'HH',data,54)
  else:phoff=struct.unpack_from(endian+'I',data,28)[0];entsize,count=struct.unpack_from(endian+'HH',data,42)
  for i in range(count):
   base=phoff+i*entsize
   if struct.unpack_from(endian+'I',data,base)[0]==1:assert struct.unpack_from(endian+('Q' if bit==2 else 'I'),data,base+(48 if bit==2 else 28))[0]>=16384,name
  native.append({'path':name,'sha256':expected[name]})
proof={'source_commit':source,'apk_sha256':digest,'apk_bytes':a.apk.stat().st_size,'application_id':m['@package'],'version_name':'0.146','version_code':146,'min_sdk':23,'target_sdk':36,'shared_uid_absent':True,'private_internal_services':private,'embedded_source_sha_verified':True,'unsigned_signature_rejected':True,'signed_certificate_verified':False,'zip_crc_verified':True,'native_hashes_independently_matched':84,'zip_and_elf_16kb_alignment_verified':True,'native_libraries':native,'scope':'Independent local bytes, binary manifest, dex source identity, signature rejection, ZIP/ELF and native checks; no signed delivery or device launch claim'};a.output.write_text(json.dumps(proof,indent=2)+'\n');print('Independent unsigned APK verification passed',digest,a.apk.stat().st_size)
