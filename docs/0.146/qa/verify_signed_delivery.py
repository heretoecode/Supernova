"""Independent signed delivery, tested-payload parity and native verification; no signing key access."""
from pathlib import Path
import argparse,hashlib,json,struct,subprocess,zipfile,re
from apkutils2 import APK
p=argparse.ArgumentParser(description=__doc__);p.add_argument('--apk',type=Path,required=True);p.add_argument('--receipt',type=Path,required=True);p.add_argument('--output',type=Path,required=True);p.add_argument('--unsigned-apk',type=Path,required=True);p.add_argument('--unsigned-receipt',type=Path,required=True);a=p.parse_args();r=json.loads(a.receipt.read_text());source=r['source_commit'];assert source=='938885ac2a84a5efecb1674b3e9f1032a56740ed';assert r['signed'] and r['payload_matches_tested_unsigned'];assert r['unsigned_conformance']==json.loads(a.unsigned_receipt.read_text());digest=hashlib.file_digest(a.apk.open('rb'),'sha256').hexdigest();assert digest==r['apk_sha256'] and a.apk.stat().st_size==95569591
m=APK(str(a.apk)).get_manifest();assert m['@package']=='app.supernova.player' and m['@android:versionName']=='0.146' and m['@android:versionCode']=='146';assert m['uses-sdk']['@android:minSdkVersion']=='23' and m['uses-sdk']['@android:targetSdkVersion']=='36';assert '@android:sharedUserId' not in m
services=m['application']['service'];services=[services] if isinstance(services,dict) else services;services={s['@android:name']:s for s in services};private=['com.archos.mediacenter.video.player.PlayerService','com.archos.mediaprovider.MediaRetrieverService']
for name in private:assert services[name]['@android:exported']=='false',name
signature=subprocess.run(['java','-cp','/workspace/apksig.jar:/workspace','VerifyApk',str(a.apk)],capture_output=True,text=True);assert signature.returncode==0,signature.stderr;certs=re.findall(r'Certificate SHA256: ([0-9a-f]{64})',signature.stdout);assert certs==['79ed34c52c3e359756ade0634d7bbb6f8e94e42a059020c1220bd8c7092a9f5e'];assert r['certificate_sha256']==certs[0]
with zipfile.ZipFile(a.unsigned_apk) as unsigned,zipfile.ZipFile(a.apk) as signed:
 def payload(z):return {x.filename:hashlib.sha256(z.read(x)).hexdigest() for x in z.infolist() if not re.fullmatch(r'META-INF/(?:MANIFEST\.MF|[^/]+\.(?:SF|RSA|DSA|EC))',x.filename,re.I)}
 assert payload(unsigned)==payload(signed),'Signed APK must preserve every tested payload byte'
 assert hashlib.file_digest(a.unsigned_apk.open('rb'),'sha256').hexdigest()==r['unsigned_conformance']['apk_sha256']==r['unsigned_apk_sha256']
expected={x['path']:x['sha256'] for x in r['unsigned_conformance']['native_libraries']};native=[]
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
proof={'source_commit':source,'apk_sha256':digest,'apk_bytes':a.apk.stat().st_size,'application_id':m['@package'],'version_name':'0.146','version_code':146,'min_sdk':23,'target_sdk':36,'shared_uid_absent':True,'private_internal_services':private,'embedded_source_sha_verified':True,'signed_certificate_verified':True,'certificate_sha256':certs[0],'tested_unsigned_payload_parity_verified':True,'unsigned_conformance_receipt_matched':True,'zip_crc_verified':True,'native_hashes_independently_matched':84,'zip_and_elf_16kb_alignment_verified':True,'native_libraries':native,'scope':'Independent local bytes, binary manifest, dex source identity, signature, complete tested-payload parity, ZIP/ELF and native checks; no device launch claim'};a.output.write_text(json.dumps(proof,indent=2)+'\n');print('Independent signed APK verification passed',digest,a.apk.stat().st_size)
