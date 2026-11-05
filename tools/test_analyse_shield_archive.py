import json
from pathlib import Path
import tempfile
import unittest
import zipfile
from analyse_shield_archive import analyse

class ShieldArchiveAnalysisTest(unittest.TestCase):
    def run_archive(self,rows,copies=()):
        with tempfile.TemporaryDirectory() as directory:
            path=Path(directory)/'private.zip'
            with zipfile.ZipFile(path,'w') as archive:
                archive.writestr('events.jsonl','\n'.join(json.dumps(e) if isinstance(e,dict) else e for e in rows))
                archive.writestr('flight.jsonl','\n'.join(json.dumps(e) for e in copies))
            return analyse(path)

    def event(self,process,sequence,event,time,**payload):
        return dict(process=process,sequence=sequence,event=event,utc_ms=time,**payload)

    def test_copies_and_partial_lines_do_not_multiply_incidents(self):
        crash=self.event('private-main',2,'uncaught_exception',2000,trace='java.lang.IllegalArgumentException\nPRIVATE_PATH_CANARY')
        result=self.run_archive([self.event('private-main',1,'startup',1000,version='0.149'),crash,'{"unfinished":'],[crash])
        self.assertEqual(2,result['distinct_events']);self.assertEqual(1,result['duplicate_copies']);self.assertEqual(1,result['malformed_lines']);self.assertEqual(1,result['events']['uncaught_exception'])
        public=json.dumps(result);self.assertNotIn('PRIVATE_PATH_CANARY',public);self.assertNotIn('private-main',public);self.assertFalse(result['raw_event_payloads_exported'])

    def test_other_process_can_be_active_after_an_unclean_warning(self):
        rows=[self.event('main',1,'startup',1000,version='0.148'),self.event('scanner',1,'startup',2000,version='0.149'),self.event('scanner',2,'PREVIOUS_SESSION_UNCLEAN_EXIT',2500,previous_process='main'),self.event('main',2,'heartbeat',5000)]
        result=self.run_archive(rows);self.assertEqual(1,result['unclean_warnings_naming_process_with_later_events']);self.assertEqual({'0.148':1,'0.149':1},result['startup_versions']);self.assertNotIn('crashes',result)

    def test_distinct_identities_and_conflicting_copies_are_reported(self):
        one=self.event('a',1,'scan_failed',1000);two=self.event('b',1,'scan_failed',1000)
        conflict=dict(one,utc_ms=1001);result=self.run_archive([one,two],[conflict]);self.assertEqual(2,result['events']['scan_failed']);self.assertEqual(1,result['conflicting_identities']);self.assertEqual(1,result['duplicate_copies'])
