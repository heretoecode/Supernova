#!/usr/bin/env python3
"""Keep the approved Subtitles contract; permit its shared switch face and final icon bounds."""
import subprocess
from pathlib import Path
import argparse

def verify(video):
    video=Path(video)
    def git(*args):return subprocess.check_output(['git','-C',str(video),*args],text=True)
    path='src/main/java/com/archos/mediacenter/video/player/PlaybackSubtitlePanel.java'
    approved=git('show','75c17bed:'+path).replace('new Switch(on)','StandardSwitchDrawable.snapshot(owner,on)')
    approved=approved.replace('PreviewLanguageIcon.apply(active,selected==null?null:selected.language);Drawable flag=active.getCompoundDrawables()[0];if(flag!=null)flag.setBounds(0,0,style.px(46),style.px(46));','PreviewLanguageIcon.apply(active,selected==null?null:selected.language,style.px(46));')
    start=approved.index('    private final class Switch extends Drawable')
    approved=approved[:start]+approved[approved.index('\n}',start):]
    if git('show','HEAD:'+path)!=approved:raise ValueError('Unrelated approved Subtitles implementation changed')
    git('diff','--exit-code','75c17bed','HEAD','--','src/main/java/com/archos/mediacenter/video/leanback/CustomLibraryActivity.java','src/main/java/com/archos/mediacenter/video/leanback/PreviewPages.java')
    print('Approved Subtitles layout/backend and existing Custom Library/Movies/TV sources preserved')

if __name__=='__main__':
    p=argparse.ArgumentParser();p.add_argument('--video',type=Path,required=True);verify(p.parse_args().video)
