#!/usr/bin/env python3
"""Preserve the completed corrections and approved designs for the authorised 0.150 QA build."""
import argparse,json,subprocess
from pathlib import Path
BASE='538cb869a4bd7e3b5a84c7997437dbc45c37a321'
TREES={'src/main': '66918e04eb59925911914be78edf857130aa46ca', 'src/test': '53ad7139b57400e269c82062a83f7174e9bd6c79', 'res': '7ea93584208b9ffca9c6392e69f74233f44ced50', 'assets': 'c17b80984077b1225d81f702276975739f5c5ea6', 'AndroidManifest.xml': '0054e3359347da4fada1ff9eae75c93486b4829c', 'build.gradle': '3a516bfe54c6f75dad901501c78d5be2c52fe7e1', 'settings.gradle': '7becd5859bacb0e0c4f9bf94f24069e81795a499', 'gradle.properties': '1c947b0dba9f10623846e845dbca1796213eb3b7'}

def verify(video):
 video=Path(video)
 def git(*args):return subprocess.check_output(['git','-C',str(video),*args],text=True).strip()
 git('merge-base','--is-ancestor',BASE,'HEAD')
 actual={p:git('rev-parse','HEAD:'+p) for p in TREES}
 if actual!=TREES:raise ValueError('Application/resources/build identity differs from the frozen 0.150 QA candidate')
 for area in ['.github/signing','.github/build','tools/native146']:
  for name in git('ls-tree','-r','--name-only',BASE,area).splitlines():
   if git('diff','--name-only',BASE,'HEAD','--',name):raise ValueError('Existing protected/native input changed: '+name)
 original=json.loads(git('show',BASE+':assets/foundation/release-history.json'));current=json.loads((video/'assets/foundation/release-history.json').read_text())
 if current['history'][1:]!=original['history'] or current['history'][0]['version']!='0.149' or current['current']['version']!='0.150' or current['current']['version_code']!=150:raise ValueError('QA version/history preservation failed')
 if current['history'][0]['apks'][0]['sha256']!='cf589e8b78a28267b76892325d628244afe932a3fc140f6f4443054f78cbaa6c':raise ValueError('Previous delivered APK evidence changed')
 return {'implementation_base':BASE,'preserved_ten_fixes':True,'implementation_trees':actual,'corrections_baseline':BASE}

if __name__=='__main__':
 p=argparse.ArgumentParser();p.add_argument('--video',type=Path,required=True);print(json.dumps(verify(p.parse_args().video),indent=2))
