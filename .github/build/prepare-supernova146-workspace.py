#!/usr/bin/env python3
"""Populate a fresh sibling dependency workspace for exact-source0.146 validation."""
import argparse,subprocess
from pathlib import Path
p=argparse.ArgumentParser(description=__doc__);p.add_argument('--root',type=Path,required=True);a=p.parse_args();root=a.root.resolve()
repos={'MediaLib':('heretoecode/aos-MediaLib','5758074049bc153ac4967b4ecd21886469270076'),'FileCoreLibrary':('nova-video-player/aos-FileCoreLibrary','c4b760c55102c72d45f68b0d16999fc1f4f013a7'),'native/avos':('nova-video-player/aos-avos','2cf21c486c7bf244c49b78d2220197a6d75396dd'),'native/libnativehelper':('nova-video-player/aos-libnativehelper','ab46fb536b594b25f93611ad7ef0f27b8fc24edb'),'native/audiocompress':('nova-video-player/aos-audiocompress','3a3f9324d2550bd305060e1c52b803121ea09aad')}
for name,(repo,sha) in repos.items():
 dest=root/name;dest.parent.mkdir(parents=True,exist_ok=True)
 subprocess.run(['git','clone','--filter=blob:none','--no-checkout','https://github.com/'+repo+'.git',str(dest)],check=True);subprocess.run(['git','-C',str(dest),'checkout','--detach',sha],check=True)
subprocess.run(['python3',str(root/'Video/.github/build/prepare-supernova146-dependencies.py'),'--root',str(root)],check=True)
