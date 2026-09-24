"""Offline source/resource checks, NOT Android inflation or runtime validation."""
from pathlib import Path
import subprocess
import xml.etree.ElementTree as ET

ROOT = Path(__file__).resolve().parents[1]
ANDROID = '{http://schemas.android.com/apk/res/android}'
xml_files = list((ROOT / 'res').rglob('*.xml')) + [ROOT / 'AndroidManifest.xml']
for path in xml_files:
    ET.parse(path)
hud = ET.parse(ROOT / 'res/layout/player_controller_experimental.xml').getroot()
nodes = {node.get(ANDROID+'id', '').split('/')[-1]: node for node in hud.iter() if node.get(ANDROID+'id')}
assert len(nodes) == sum(bool(node.get(ANDROID+'id')) for node in hud.iter()), 'Duplicate HUD ids'
transport = nodes['preview_transport']
expected = ['preview_subtitles', 'preview_audio', 'pause', 'preview_info', 'preview_more']
assert len(transport) == 5
for group, identity in zip(transport, expected):
    assert group[0] is nodes[identity]
    assert group[0].get(ANDROID+'visibility', 'visible') == 'visible'
    assert group[1].get(ANDROID+'singleLine') == 'true'
for identity in ['preview_previous', 'preview_next', 'backward', 'forward', 'preview_speed']:
    assert nodes[identity].get(ANDROID+'visibility') == 'gone'
    assert nodes[identity].get(ANDROID+'focusable') == 'false'
assert 'preview_scrub_time' in nodes
manifest = ET.parse(ROOT / 'AndroidManifest.xml').getroot()
remote = [node for node in manifest.iter('activity') if node.get(ANDROID+'name', '').endswith('.PreviewRemoteDetailsActivity')]
assert len(remote) == 1 and remote[0].get(ANDROID+'exported') == 'false'
# Existing identity remains a hard delivery gate, never a generated substitute.
workflow = (ROOT / '.github/workflows/build-preview-apk.yml').read_text()
assert '89ac087ed6f989c90482d4a999f80511fe6ceee26ef1b9c37a142a9f00d39a5a' in workflow
assert 'refusing to generate a different identity' in workflow
subprocess.run(['git', 'diff', '--check'], cwd=ROOT, check=True)
print(f'PASS: {len(xml_files)} XML files parsed; HUD structure, internal Details activity, signing gate and diff whitespace checked')
