#!/usr/bin/env python3
"""Independently read the complete CI candidate archive without signing or rebuilding."""
import argparse
import hashlib
import importlib.util
import io
import json
from pathlib import Path
import sys
import tempfile
import zipfile

p=argparse.ArgumentParser(description=__doc__)
p.add_argument('--archive',type=Path,required=True)
p.add_argument('--source',required=True)
p.add_argument('--output',type=Path,required=True)
a=p.parse_args()
video=Path(__file__).resolve().parents[3]
with zipfile.ZipFile(a.archive) as package:
    assert package.testzip() is None
    receipt_name=next(n for n in package.namelist() if Path(n).name=='unsigned-conformance.json')
    apk_name=next(n for n in package.namelist() if Path(n).name=='Supernova_0.147_unsigned.apk')
    receipt=json.loads(package.read(receipt_name));payload=package.read(apk_name)
assert receipt['source_commit']==a.source
assert receipt['version_name']=='0.147' and receipt['version_code']==147
assert hashlib.sha256(payload).hexdigest()==receipt['apk_sha256'] and len(payload)==receipt['apk_bytes']
expected=json.loads((video/'docs/0.146/INDEPENDENT_SIGNED_APK_VERIFICATION.json').read_text())
old_native={r['path']:r['sha256'] for r in expected['native_libraries']}
with zipfile.ZipFile(io.BytesIO(payload)) as apk:
    assert apk.testzip() is None and len(apk.namelist())==len(set(apk.namelist()))
    native={n:hashlib.sha256(apk.read(n)).hexdigest() for n in apk.namelist() if n.startswith('lib/') and n.endswith('.so')}
    assert len(native)==84 and native==old_native
    assert native=={r['path']:r['sha256'] for r in receipt['native_libraries']}
    assert any(a.source.encode() in apk.read(n) for n in apk.namelist() if n.startswith('classes') and n.endswith('.dex'))
    assert not any(n.upper().startswith('META-INF/') and n.upper().endswith(('.RSA','.DSA','.EC','.SF')) for n in apk.namelist())
    alignment=[]
    for name in native:
        info=apk.getinfo(name)
        if info.compress_type==zipfile.ZIP_STORED:
            start=info.header_offset
            header=payload[start:start+30]
            name_len=int.from_bytes(header[26:28],'little');extra_len=int.from_bytes(header[28:30],'little')
            assert (start+30+name_len+extra_len)%16384==0
            alignment.append(name)
# Exercise the public conformance gate only; prepare() and key access are never called.
sys.path.insert(0,str(video/'.github/signing'))
spec=importlib.util.spec_from_file_location('qa147',video/'.github/signing/supernova_0147_qa_signing.py')
module=importlib.util.module_from_spec(spec);spec.loader.exec_module(module)
with tempfile.TemporaryDirectory() as directory:
    path=Path(directory)/'candidate.apk';path.write_bytes(payload);module.gate(receipt,path,a.source)
record={'source_commit':a.source,'archive_sha256':hashlib.file_digest(a.archive.open('rb'),'sha256').hexdigest(),
        'apk_sha256':receipt['apk_sha256'],'apk_bytes':len(payload),'archive_and_apk_crc_verified':True,
        'embedded_exact_source_verified':True,'native_hashes_matched_to_0146_and_ci':84,
        'uncompressed_native_entries_16kb_aligned':len(alignment),'unsigned_conformance_gate_accepted':True,
        'signing_performed':False,'physical_shield_verified':False,
        'scope':'Complete byte readback, source marker, native payload and alignment checks; CI independently runs manifest/resource analyzers and unsigned signature rejection.'}
a.output.write_text(json.dumps(record,indent=2)+'\n')
print(json.dumps(record,indent=2))
