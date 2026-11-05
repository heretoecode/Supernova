#!/usr/bin/env python3
"""Verify delivered signed bytes against frozen CI payload; never access a signing key."""
import argparse
import hashlib
import json
from pathlib import Path
import re
import struct
import subprocess
import zipfile
from apkutils2 import APK

p = argparse.ArgumentParser(description=__doc__)
for name in ('apk', 'receipt', 'unsigned-apk', 'unsigned-receipt', 'apksigner-jar', 'output'):
    p.add_argument('--' + name, type=Path, required=True)
a = p.parse_args()
source = '30d4961e4e5b0e412ec35fe8d27cd6197cea8b26'
certificate = '79ed34c52c3e359756ade0634d7bbb6f8e94e42a059020c1220bd8c7092a9f5e'
r = json.loads(a.receipt.read_text())
u = json.loads(a.unsigned_receipt.read_text())
assert r['source_commit'] == u['source_commit'] == source
assert r['signed'] and r['payload_matches_tested_unsigned']
assert not r['physical_shield_verified'] and not r['signed_upgrade_verified']
assert r['unsigned_conformance'] == u
digest = hashlib.file_digest(a.apk.open('rb'), 'sha256').hexdigest()
assert digest == r['apk_sha256']
unsigned_digest = hashlib.file_digest(a.unsigned_apk.open('rb'), 'sha256').hexdigest()
assert unsigned_digest == r['unsigned_apk_sha256'] == u['apk_sha256']
assert a.unsigned_apk.stat().st_size == u['apk_bytes']
m = APK(str(a.apk)).get_manifest()
assert m['@package'] == 'app.supernova.player'
assert m['@android:versionName'] == '0.148' and m['@android:versionCode'] == '148'
assert m['uses-sdk']['@android:minSdkVersion'] == '23'
assert m['uses-sdk']['@android:targetSdkVersion'] == '36'
assert '@android:sharedUserId' not in m
assert m['application'].get('@android:debuggable', 'false') == 'false'
services = m['application']['service']
services = [services] if isinstance(services, dict) else services
services = {s['@android:name']: s for s in services}
private = ['com.archos.mediacenter.video.player.PlayerService', 'com.archos.mediaprovider.MediaRetrieverService']
for name in private:
    assert services[name]['@android:exported'] == 'false', name
signature = subprocess.run(['java', '-jar', str(a.apksigner_jar), 'verify', '--verbose',
                            '--print-certs', '--min-sdk-version', '23', '--max-sdk-version', '36',
                            str(a.apk)], capture_output=True, text=True)
assert signature.returncode == 0, signature.stderr
certificates = re.findall(r'Signer #\d+ certificate SHA-256 digest: ([0-9a-f]{64})', signature.stdout)
assert certificates == [certificate] and r['certificate_sha256'] == certificate
with zipfile.ZipFile(a.unsigned_apk) as unsigned, zipfile.ZipFile(a.apk) as signed:
    def payload(archive):
        names = archive.namelist()
        assert len(names) == len(set(names)) and archive.testzip() is None
        return {n: hashlib.sha256(archive.read(n)).hexdigest() for n in names
                if not re.fullmatch(r'META-INF/(?:MANIFEST\.MF|[^/]+\.(?:SF|RSA|DSA|EC))', n, re.I)}
    tested = payload(unsigned)
    assert tested == payload(signed), 'Every non-signature entry must retain its tested bytes'
    payload_entries = len(tested)
expected = {x['path']: x['sha256'] for x in u['native_libraries']}
baseline = json.loads((Path(__file__).resolve().parents[2] / 'next-version/continuation/UNSIGNED_CONFORMANCE.json').read_text())
assert expected == {x['path']: x['sha256'] for x in baseline['native_libraries']}
native = []
compressed = aligned = 0
with a.apk.open('rb') as raw, zipfile.ZipFile(a.apk) as packed:
    names = {n for n in packed.namelist() if n.startswith('lib/') and n.endswith('.so')}
    assert len(names) == 84 and names == expected.keys()
    assert {n.split('/')[1] for n in names} == {'armeabi-v7a', 'arm64-v8a', 'x86', 'x86_64'}
    assert any(source.encode() in packed.read(n) for n in packed.namelist()
               if n.startswith('classes') and n.endswith('.dex'))
    for name in sorted(names):
        data = packed.read(name)
        assert hashlib.sha256(data).hexdigest() == expected[name]
        entry = packed.getinfo(name)
        raw.seek(entry.header_offset + 26)
        size, extra = struct.unpack('<HH', raw.read(4))
        offset = entry.header_offset + 30 + size + extra
        if entry.compress_type == 0:
            assert offset % 16384 == 0, (name, offset)
            aligned += 1
        else:
            assert entry.compress_type == 8 and m['application']['@android:extractNativeLibs'] == 'true'
            compressed += 1
        assert data[:4] == b'\x7fELF'
        bit = data[4]
        assert bit in (1, 2) and data[5] in (1, 2)
        endian = '<' if data[5] == 1 else '>'
        if bit == 2:
            phoff = struct.unpack_from(endian + 'Q', data, 32)[0]
            entsize, count = struct.unpack_from(endian + 'HH', data, 54)
        else:
            phoff = struct.unpack_from(endian + 'I', data, 28)[0]
            entsize, count = struct.unpack_from(endian + 'HH', data, 42)
        for i in range(count):
            base = phoff + i * entsize
            if struct.unpack_from(endian + 'I', data, base)[0] == 1:
                assert struct.unpack_from(endian + ('Q' if bit == 2 else 'I'), data,
                                          base + (48 if bit == 2 else 28))[0] >= 16384, name
        native.append({'path': name, 'sha256': expected[name]})
proof = {'source_commit': source, 'application_id': m['@package'], 'version_name': '0.148',
         'version_code': 148, 'min_sdk': 23, 'target_sdk': 36, 'apk_sha256': digest,
         'apk_bytes': a.apk.stat().st_size, 'certificate_sha256': certificate,
         'signature_cryptographically_verified': True, 'apksigner_sdk_range': [23, 36],
         'apksigner_jar_sha256': hashlib.file_digest(a.apksigner_jar.open('rb'), 'sha256').hexdigest(),
         'apksigner_output': signature.stdout.strip().splitlines(),
         'tested_unsigned_payload_parity_verified': True, 'payload_entries_compared': payload_entries,
         'unsigned_apk_sha256': unsigned_digest, 'unsigned_conformance_receipt_matched': True,
         'zip_crc_verified': True, 'embedded_source_verified': True, 'shared_uid_absent': True,
         'private_internal_services': private, 'native_hashes_matched_to_ci_and_validated_pr29': 84,
         'native_elf_load_alignment_minimum': 16384, 'compressed_native_entries': compressed,
         'uncompressed_native_entries_16kb_aligned': aligned, 'native_libraries': native,
         'local_signing_or_application_rebuild': False, 'physical_shield_verified': False,
         'signed_upgrade_verified': False,
         'scope': 'Independent signed bytes, binary manifest, certificate/signatures, complete tested-payload parity, exact dex source, ZIP CRC and native ELF verification. No installed upgrade or physical launch claim.'}
a.output.write_text(json.dumps(proof, indent=2) + '\n')
print('Independent signed APK verification passed:', digest, a.apk.stat().st_size)
