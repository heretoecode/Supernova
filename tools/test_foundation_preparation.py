import importlib.util
import json
from pathlib import Path
import tempfile
import unittest
import xml.etree.ElementTree as ET

ROOT=Path(__file__).resolve().parents[1]
spec=importlib.util.spec_from_file_location('prepare',ROOT/'.github/build/prepare-foundation.py')
prepare=importlib.util.module_from_spec(spec);spec.loader.exec_module(prepare)

class FoundationPreparationTests(unittest.TestCase):
    def test_isolated_contracts_shared_uid_task_and_external_oauth(self):
        with tempfile.TemporaryDirectory() as tmp:
            root=Path(tmp);(root/'Video').mkdir();(root/'MediaLib').mkdir()
            manifest='''<manifest xmlns:android="http://schemas.android.com/apk/res/android" android:sharedUserId="archos.openmediacenter" android:sharedUserMaxSdkVersion="32"><application android:label="@string/nova"><activity android:taskAffinity="archos.task.video"><intent-filter><data android:scheme="nova.trakt"/></intent-filter></activity><provider android:authorities="browser.SearchProviderVideocommunity"/><provider android:authorities="com.archos.media.videocommunity;com.archos.media.scrapercommunity"/></application></manifest>'''
            (root/'Video/AndroidManifest.xml').write_text(manifest)
            (root/'MediaLib/Provider.java').write_text(' '.join(prepare.REPLACEMENTS))
            result=prepare.prepare(root)
            text=(root/'Video/AndroidManifest.xml').read_text()
            self.assertTrue(result['shared_uid_removed']);self.assertNotIn('sharedUser',text)
            self.assertIn('app.supernova.player.task.video',text);self.assertIn('nova.trakt',text)
            self.assertIn('${novaLabel}',text)
            for old,new in prepare.REPLACEMENTS.items():
                self.assertNotIn(old,text);self.assertIn(new,text)
                self.assertIn(new,(root/'MediaLib/Provider.java').read_text())
            ET.fromstring(text)
            with self.assertRaises(ValueError): prepare.prepare(root)

    def test_missing_pinned_module_refused(self):
        with tempfile.TemporaryDirectory() as tmp:
            (Path(tmp)/'Video').mkdir()
            with self.assertRaises(ValueError):prepare.prepare(tmp)

    def test_history_literal_counter_and_current_follow_verified_records(self):
        data=json.loads((ROOT/'assets/foundation/release-history.json').read_text())
        labels=[x['version'] for x in reversed(data['history'])]
        self.assertEqual(labels,[f'0.{i}' for i in range(1,len(labels)+1)])
        self.assertEqual(data['current']['version'],f'0.{len(labels)+1}')
        self.assertEqual(data['current']['version_code'],len(labels)+1)
        self.assertIn('0.10',labels)

    def test_disclaimers_and_no_parked_provider_credits(self):
        data=json.loads((ROOT/'assets/foundation/credits.json').read_text())
        tmdb=next(x for x in data['entries'] if x['name']=='TMDB')
        self.assertEqual(tmdb['text'],'This product uses the TMDB API but is not endorsed or certified by TMDB.')
        self.assertEqual(tmdb['logo'],'tmdb')
        names={x['name'] for x in data['entries']}
        self.assertFalse(names & {'SkipDB','TheIntroDB','MDBList','put.io'})
        for x in data['entries']:
            self.assertTrue(x['url'].startswith('https://'))
            self.assertNotIn('?',x['url'])

if __name__=='__main__':unittest.main()
