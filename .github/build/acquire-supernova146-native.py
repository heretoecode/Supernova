#!/usr/bin/env python3
"""Acquire exact official native sources/toolchain, preserving archive symlinks."""
import argparse,hashlib,json,subprocess,tarfile,urllib.request,time
from pathlib import Path

def digest(p):
 with p.open('rb') as f:return hashlib.file_digest(f,'sha256').hexdigest()
def download(item,p):
 if p.exists():
  if digest(p)!=item['sha256']:raise ValueError('Cached input hash differs: '+item['name'])
  return
 temporary=p.with_suffix(p.suffix+'.download')
 for attempt in range(4):
  try:
   with urllib.request.urlopen(item['url'],timeout=90) as source,temporary.open('wb') as out:
    import shutil;shutil.copyfileobj(source,out)
   if digest(temporary)!=item['sha256']:raise ValueError('Downloaded input hash differs: '+item['name'])
   temporary.replace(p);return
  except Exception:
   temporary.unlink(missing_ok=True)
   if attempt==3:raise
   time.sleep(2)
def main():
 p=argparse.ArgumentParser(description=__doc__);p.add_argument('--workspace',type=Path,required=True);p.add_argument('--ndk',type=Path);a=p.parse_args();work=a.workspace.resolve();work.mkdir(parents=True,exist_ok=True);here=Path(__file__).resolve().parent;lock=json.loads((here/'native-supernova146.lock.json').read_text());cache=work/'downloads';cache.mkdir(exist_ok=True);sources=work/'sources';sources.mkdir(exist_ok=True)
 for item in lock['sources']:
  dest=sources/item['name']
  if item.get('git_repository'):
   if not dest.exists():
    dest.mkdir();subprocess.run(['git','init',str(dest)],check=True,stdout=subprocess.DEVNULL);subprocess.run(['git','-C',str(dest),'fetch','--depth=1',item['git_repository'],item['commit']],check=True);subprocess.run(['git','-C',str(dest),'checkout','--detach','FETCH_HEAD'],check=True)
   actual=subprocess.check_output(['git','-C',str(dest),'rev-parse','HEAD'],text=True).strip()
   if actual!=item['commit'] or subprocess.check_output(['git','-C',str(dest),'status','--porcelain'],text=True).strip():raise ValueError('Native source checkout differs: '+item['name'])
  else:
   archive=cache/(item['name']+'.tar.gz');download(item,archive)
   if not dest.exists():
    dest.mkdir()
    with tarfile.open(archive) as packed:
     for member in packed.getmembers():
      member.name='/'.join(member.name.split('/')[1:])
      if member.name:packed.extract(member,dest,filter='data')
 if a.ndk:
  ndk=a.ndk.resolve()
 else:
  item=lock['ndk'];archive=cache/'ndk.zip';download(item,archive);tool=work/'toolchain';tool.mkdir(exist_ok=True);ndk=tool/'android-ndk-r30-beta1'
  if not ndk.exists():subprocess.run(['unzip','-q',str(archive),'-d',str(tool)],check=True)
 if 'Pkg.BaseRevision = 30.0.14904198' not in (ndk/'source.properties').read_text():raise ValueError('Wrong NDK revision')
 # unzip preserves NDK symlinks. An executable shell wrapper must not point to a
 # text file containing only the symlink target, as Python ZipFile.extractall does.
 compiler=ndk/'toolchains/llvm/prebuilt/linux-x86_64/bin/clang'
 if not compiler.is_symlink():raise ValueError('NDK symlinks were not preserved')
 receipt={'schema':1,'sources':lock['sources'],'ndk':str(ndk),'lock_sha256':digest(here/'native-supernova146.lock.json')}
 (work/'acquisition.json').write_text(json.dumps(receipt,indent=2)+'\n');print('Pinned native sources and NDK verified')
if __name__=='__main__':main()
