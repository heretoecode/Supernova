#!/usr/bin/env python3
"""Retain PR30's ten fixes and permit only the authorised subtitles/version continuation."""
import json,subprocess
from pathlib import Path
BASE='d84ab3ae'
PRESERVED=['res/layout/player_controller_experimental.xml','res/raw/keep.xml','src/main/java/com/archos/mediacenter/video/leanback/PreviewPages.java','src/main/java/com/archos/mediacenter/video/leanback/details/DetailsBlueprintPanel.java','src/main/java/com/archos/mediacenter/video/leanback/details/PreviewMoviePage.java','src/main/java/com/archos/mediacenter/video/leanback/filebrowsing/NetworkFilesLayout.java','src/main/java/com/archos/mediacenter/video/leanback/settings/SettingsWorkspace.java','src/main/java/com/archos/mediacenter/video/leanback/settings/PreviewPreferenceDialogs.java','src/main/java/com/archos/mediacenter/video/leanback/settings/PreviewSettings.java','src/main/java/com/archos/mediacenter/video/player/PlayerController.java','src/main/java/com/archos/mediacenter/video/player/PreviewHudTiming.java','AndroidManifest.xml','build.gradle','settings.gradle','gradle.properties']
def verify(video):
 video=Path(video)
 def git(*args):return subprocess.check_output(['git','-C',str(video),*args],text=True).strip()
 git('merge-base','--is-ancestor',BASE,'HEAD')
 for name in PRESERVED:
  if git('diff','--name-only',BASE,'HEAD','--',name):raise ValueError('Preserved PR30 source changed: '+name)
 original=json.loads(git('show',BASE+':assets/foundation/release-history.json'));current=json.loads((video/'assets/foundation/release-history.json').read_text())
 if current['history'][1:]!=original['history'] or current['history'][0]['version']!='0.148' or current['current']['version']!='0.149' or current['current']['version_code']!=149:raise ValueError('QA version/history preservation failed')
 # Original native and signing inputs stay immutable. New QA-specific wrappers are allowed.
 for area in ['.github/signing','.github/build','tools/native146']:
  for name in git('ls-tree','-r','--name-only',BASE,area).splitlines():
   if git('diff','--name-only',BASE,'HEAD','--',name):raise ValueError('Original native/signing input changed: '+name)
 return {'implementation_base':git('rev-parse',BASE),'preserved_ten_fixes':True,'implementation_trees':{p:git('rev-parse','HEAD:'+p) for p in ['src/main','src/test','res','assets','AndroidManifest.xml','build.gradle','settings.gradle','gradle.properties']}}
if __name__=='__main__':
 import argparse
 p=argparse.ArgumentParser();p.add_argument('--video',type=Path,required=True);a=p.parse_args();print(json.dumps(verify(a.video),indent=2))
