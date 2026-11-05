#!/usr/bin/env python3
"""Freeze validated PR29 implementation while permitting the authorised version/history update."""
import json,subprocess
from pathlib import Path
BASE = 'b0bce99a41b8321606059f4bfe5b5c16da741c6b'
EXPECTED_TREES = {'src/main': 'd09043e23d18148d0f1651ca76c23ea1872e9909', 'src/test': 'd0a7771d368eb1813eae5bd738631cc8bcea4876', 'res': 'df320ea71beb115264cbca30a50e8587ce86cbdc', 'AndroidManifest.xml': '0054e3359347da4fada1ff9eae75c93486b4829c', 'build.gradle': '3a516bfe54c6f75dad901501c78d5be2c52fe7e1', 'settings.gradle': '7becd5859bacb0e0c4f9bf94f24069e81795a499', 'gradle.properties': '1c947b0dba9f10623846e845dbca1796213eb3b7'}
def verify(video):
    video=Path(video)
    for path,expected in EXPECTED_TREES.items():
        actual=subprocess.check_output(['git','-C',str(video),'rev-parse','HEAD:'+path],text=True).strip()
        if actual!=expected:raise ValueError('Validated implementation changed: '+path)
    changed=subprocess.check_output(['git','-C',str(video),'diff','--name-only',BASE,'HEAD','--','assets'],text=True).splitlines()
    if changed!=['assets/foundation/release-history.json']:raise ValueError('Only authorised version/history asset may change')
    original=json.loads(subprocess.check_output(['git','-C',str(video),'show',BASE+':assets/foundation/release-history.json'],text=True))
    current=json.loads((video/'assets/foundation/release-history.json').read_text())
    if current['current']['version']!='0.148' or current['current']['version_code']!=148 or current['history'][1:]!=original['history'] or current['history'][0]['version']!='0.147':raise ValueError('Version/history preservation failed')
    inputs=subprocess.check_output(['git','-C',str(video),'ls-tree','-r','--name-only',BASE,'.github/build','tools/native146','.github/signing'],text=True).splitlines()
    if any(subprocess.check_output(['git','-C',str(video),'diff','--name-only',BASE,'HEAD','--',p],text=True).strip() for p in inputs):raise ValueError('Original build/native/signing inputs changed')
    return {'implementation_base':BASE,'implementation_trees':EXPECTED_TREES,'implementation_source_unchanged':True}
if __name__=='__main__':
    import argparse
    p=argparse.ArgumentParser(description=__doc__);p.add_argument('--video',type=Path,required=True);a=p.parse_args();print(json.dumps(verify(a.video),indent=2))
