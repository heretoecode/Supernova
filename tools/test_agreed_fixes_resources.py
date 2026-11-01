"""Release resource contracts for dynamic flag lookup; no APK assembly required."""
import unittest
import xml.etree.ElementTree as ET
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]

class AgreedFixResourcesTest(unittest.TestCase):
    def test_dynamic_drawables_survive_resource_shrinking(self):
        keep = ET.parse(ROOT / 'res/raw/keep.xml').getroot().attrib['{http://schemas.android.com/tools}keep'].split(',')
        self.assertIn('@drawable/region_*', keep)
        self.assertIn('@drawable/provider_*', keep)
        for region in ('gb', 'us'):
            self.assertTrue((ROOT / f'res/drawable-nodpi/region_{region}.png').is_file())
