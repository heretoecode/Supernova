"""Trace the executable Settings map to the approved audit and unchanged assets."""
import hashlib
import json
from pathlib import Path
import re
import unittest

ROOT=Path(__file__).resolve().parents[1]
class Settings143Mapping(unittest.TestCase):
    def test_every_audited_definition_has_its_exact_approved_home_and_key(self):
        audit=json.loads((ROOT/'docs/audits/settings-2026-10-26/inventory.json').read_text())['rows']
        runtime=json.loads((ROOT/'assets/foundation/settings-map.json').read_text())['rows']
        mapping={line.split('|')[1].strip():[part.strip() for part in line.split('|')[1:-1]] for line in (ROOT/'docs/design/SETTINGS_AUDIT_MAPPING.md').read_text().splitlines() if re.match(r'\| S\d{3} ',line)}
        self.assertEqual(165,len(runtime));self.assertEqual({r['id'] for r in audit},{r['id'] for r in runtime})
        self.assertEqual(165,len({r['id'] for r in runtime}))
        for row in runtime:
            expected=mapping[row['id']]
            self.assertEqual(expected[5].strip('`'),row['key']);self.assertEqual(expected[2],row['destination']);self.assertEqual(expected[3],row['subsection']);self.assertTrue(row['disposition'])
            self.assertNotIn('default',row,'View registry must not duplicate stored preference values')
        self.assertEqual(23,sum(r['destination']=='REVIEW / NON-EXPOSED' for r in runtime))
        self.assertEqual(26,sum(r['id']>='S140' for r in runtime))
    def test_official_background_bytes_remain_canonical(self):
        expected='09c25a2f1a4419f0ed0c73a1b0f664d3936ee93b12d69a9e18a960fd786c96d4'
        for path in ['assets/branding/Supernova_Space_Black_Blend_4K_UHD.png','res/drawable-nodpi/foundation_space_black.png']:
            self.assertEqual(expected,hashlib.sha256((ROOT/path).read_bytes()).hexdigest())
    def test_release_history_preserves_previous_delivered_identity(self):
        history=json.loads((ROOT/'assets/foundation/release-history.json').read_text())
        self.assertEqual(('0.147',147),(history['current']['version'],history['current']['version_code']))
        delivered=next(item for item in history['history'] if item['version']=='0.146')
        receipt=json.loads((ROOT/'docs/0.146/SIGNED_ARTIFACT_VERIFICATION.json').read_text())
        binary=json.loads((ROOT/'docs/0.146/INDEPENDENT_SIGNED_APK_VERIFICATION.json').read_text())
        self.assertEqual(receipt['artifact_sha256'],delivered['archive_sha256']);self.assertTrue(delivered['archive_digest_verified'])
        self.assertEqual(binary['apk_sha256'],delivered['apks'][0]['sha256']);self.assertEqual(binary['source_commit'],delivered['source_sha'])
        prior=next(item for item in history['history'] if item['version']=='0.142');self.assertEqual('0.142',prior['version'])
        self.assertEqual('b347707defdbf2ab3ae83d920ba9fa7af3936220',prior['source_sha'])
        self.assertEqual('af03d26719b00601ca08da5ad562c1f196ec536c6397ac5b4603c2337d7c8514',prior['apks'][0]['sha256'])
if __name__=='__main__':unittest.main()
