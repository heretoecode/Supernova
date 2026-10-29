#!/usr/bin/env python3
"""Reproduce one ABI of the complete0.146 native stack from pinned source."""
import argparse,hashlib,json,os,subprocess,tarfile
from pathlib import Path

def digest(p):
 with p.open('rb') as f:return hashlib.file_digest(f,'sha256').hexdigest()
def main():
 p=argparse.ArgumentParser(description=__doc__);p.add_argument('--workspace',type=Path,required=True);p.add_argument('--abi',choices=['arm64-v8a','armeabi-v7a','x86','x86_64'],required=True);p.add_argument('--jobs',type=int,default=2);p.add_argument('--ndk',type=Path);a=p.parse_args();here=Path(__file__).resolve().parent;lock=json.loads((here/'native-supernova146.lock.json').read_text());work=a.workspace.resolve();work.mkdir(parents=True,exist_ok=True)
 for name,expected in lock['recipes'].items():
  if digest(here/name)!=expected:raise ValueError('Native recipe differs from reviewed source lock: '+name)
 acquisition=['python3',str(here/'acquire-supernova146-native.py'),'--workspace',str(work/'inputs')]
 if a.ndk:acquisition+=['--ndk',str(a.ndk.resolve())]
 subprocess.run(acquisition,check=True);info=json.loads((work/'inputs/acquisition.json').read_text());ndk=Path(info['ndk']);sources=work/'inputs/sources';pre=work/'prebuilt'
 for component in ['openssl','subtitles','codec-dependencies','ffmpeg']:
  script=here/('build-supernova146-'+component+'.py');build=work/component/a.abi;command=['python3',str(script),'--ndk',str(ndk),'--workspace',str(build),'--abi',a.abi,'--jobs',str(a.jobs)]
  if component=='openssl':command+=['--source',str(sources/'openssl'),'--prefix',str(pre/'openssl'/('dist-'+a.abi))]
  else:command+=['--sources',str(sources),'--prebuilt',str(pre)]
  subprocess.run(command,check=True)
 files=[]
 for f in sorted(pre.rglob('*')):
  if f.is_file():files.append({'path':str(f.relative_to(pre)),'sha256':digest(f),'bytes':f.stat().st_size})
 receipt={'schema':1,'abi':a.abi,'source_commit':os.environ.get('GITHUB_SHA'),'native_lock_sha256':digest(here/'native-supernova146.lock.json'),'recipes':lock['recipes'],'sources':lock['sources'],'files':files,'validation':'cross-built; independent runtime and APK tests remain separate'}
 (work/'native-stack.json').write_text(json.dumps(receipt,indent=2)+'\n');delivery=work/'delivery';delivery.mkdir(exist_ok=True);archive=delivery/('Supernova_0.146_native_'+a.abi+'.tar.gz')
 with tarfile.open(archive,'w:gz',compresslevel=1) as packed:
  packed.add(pre,arcname='prebuilt');packed.add(work/'native-stack.json',arcname='native-stack.json')
  for component in ['openssl','subtitles','codec-dependencies','ffmpeg']:
   for f in (work/component/a.abi).glob('*.json'):packed.add(f,arcname='evidence/'+component+'/'+f.name)
  for name in ['config.h','config_components.h','ffbuild/config.mak']:packed.add(work/'ffmpeg'/a.abi/'source'/name,arcname='configuration/'+Path(name).name)
 (delivery/'archive.json').write_text(json.dumps({'file':archive.name,'sha256':digest(archive),'bytes':archive.stat().st_size},indent=2)+'\n');print('Pinned native stack built and archived:',a.abi)
if __name__=='__main__':main()
