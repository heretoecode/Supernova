import copy
import importlib.util
import json
from pathlib import Path
import tempfile
import unittest

ROOT=Path(__file__).resolve().parents[1]
spec=importlib.util.spec_from_file_location('release_history_check',ROOT/'.github/build/verify-next-qa.py')
check=importlib.util.module_from_spec(spec);spec.loader.exec_module(check)

class HistoricalDeliveryProof(unittest.TestCase):
    def test_143_receipt_accepts_only_pinned_identity_signature_and_payload(self):
        history=json.loads((ROOT/'assets/foundation/release-history.json').read_text())
        entry=next(row for row in history['history'] if row['version']=='0.143')
        receipt=json.loads((ROOT/'docs/0.143/SIGNED_QA_DELIVERY.json').read_text())
        with tempfile.TemporaryDirectory() as directory:
            target=Path(directory)/entry['recorded_binary_verification'];target.parent.mkdir(parents=True)
            target.write_text(json.dumps(receipt));self.assertTrue(check.verified_143_delivery_record(Path(directory),entry))
            for field,bad in [('signature_verified',False),('zip_crc_verified',False),('payload_matches_tested_unsigned_verified_in_ci',False),('native_hashes_independently_matched',0),('source_commit','0'*40),('apk_sha256','0'*64),('certificate_sha256','0'*64),('version_code',144)]:
                altered=copy.deepcopy(receipt);altered[field]=bad;target.write_text(json.dumps(altered))
                self.assertFalse(check.verified_143_delivery_record(Path(directory),entry),field)

if __name__=='__main__':unittest.main()
