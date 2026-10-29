#!/usr/bin/env python3
"""Verify pinned four-ABI CI inputs, build current JNI sources and stage APK DSOs."""
import argparse,hashlib,importlib.util,json,os,shutil,subprocess,tarfile
from pathlib import Path
ABIS=('arm64-v8a','armeabi-v7a','x86','x86_64')
def sha(p):
 with p.open('rb') as f:return hashlib.file_digest(f,'sha256').hexdigest()
def main():
 p=argparse.ArgumentParser(description=__doc__);p.add_argument('--root',type=Path,required=True);p.add_argument('--artifacts',type=Path,required=True);p.add_argument('--workspace',type=Path,required=True);p.add_argument('--ndk',type=Path);a=p.parse_args();root=a.root.resolve();video=root/'Video';here=video/'.github/build';work=a.workspace.resolve();work.mkdir(parents=True,exist_ok=True)
 lock=json.loads((here/'native-supernova146.lock.json').read_text());review=json.loads((video/'docs/0.146/NATIVE_CI_ARTIFACT_VERIFICATION.json').read_text());expected={x['abi']:x for x in review['artifacts']};pre=root/'native/prebuilt';pre.mkdir(parents=True,exist_ok=True);native={}
 for abi in ABIS:
  matches=list(a.artifacts.rglob('Supernova_0.146_native_'+abi+'.tar.gz'))
  if len(matches)!=1 or sha(matches[0])!=expected[abi]['archive_sha256']:raise ValueError('Pinned native archive differs: '+abi)
  unpack=work/abi;unpack.mkdir(exist_ok=True)
  with tarfile.open(matches[0]) as archive:archive.extractall(unpack,filter='data')
  info=json.loads((unpack/'native-stack.json').read_text())
  if info['abi']!=abi or info['native_lock_sha256']!=sha(here/'native-supernova146.lock.json') or info['recipes']!=lock['recipes'] or info['sources']!=lock['sources']:raise ValueError('Native source/recipe lock differs: '+abi)
  if len(info['files'])!=expected[abi]['files']:raise ValueError('Native inventory incomplete')
  for item in info['files']:
   source=unpack/'prebuilt'/item['path'];relative=Path(item['path'])
   if relative.is_absolute() or '..' in relative.parts or sha(source)!=item['sha256']:raise ValueError('Native installed file differs')
   # CLI test tools vary across ABI and are neither linked nor packaged.
   if 'bin' in relative.parts:continue
   dest=pre/relative;dest.parent.mkdir(parents=True,exist_ok=True)
   if dest.exists() and sha(dest)!=item['sha256']:raise ValueError('Cross-ABI shared include conflict: '+str(relative))
   if not dest.exists():shutil.copyfile(source,dest)
  native[abi]=info
 if a.ndk:ndk=a.ndk.resolve()
 else:
  spec=importlib.util.spec_from_file_location('native_acquisition',here/'acquire-supernova146-native.py');mod=importlib.util.module_from_spec(spec);spec.loader.exec_module(mod)
  archive=work/'ndk.zip';mod.download(lock['ndk'],archive);tool=work/'toolchain';tool.mkdir(exist_ok=True);ndk=tool/'android-ndk-r30-beta1'
  if not ndk.exists():subprocess.run(['unzip','-q',str(archive),'-d',str(tool)],check=True)
 if 'Pkg.BaseRevision = 30.0.14904198' not in (ndk/'source.properties').read_text() or not (ndk/'toolchains/llvm/prebuilt/linux-x86_64/bin/clang').is_symlink():raise ValueError('Pinned NDK/symlinks required')
 env=dict(os.environ,LIBAV_CONFIG='full',BUILD='DEBUG');command=[str(ndk/'ndk-build'),'NDK_APP_ABI='+' '.join(ABIS),'APP_PLATFORM=android-23','LIBAV_CONFIG=full','BUILD=DEBUG','-j4']
 for module in ['native/libnativehelper','FileCoreLibrary','native/avos']:
  subprocess.run(command,cwd=root/module,env=env,check=True)
 libraries=[]
 for abi in ABIS:
  dest=root/'MediaLib/libs'/abi;dest.mkdir(parents=True,exist_ok=True)
  sources=[root/'native/avos/libs'/abi,root/'native/libnativehelper/libs'/abi,pre/'ffmpeg'/('dist-full-'+abi)/'lib']
  for source in sources:
   for f in source.glob('*.so'):shutil.copyfile(f,dest/f.name)
  for name in ['dav1d','opus','libmysofa']:
   filename='lib'+name.removeprefix('lib')+'.so';shutil.copyfile(pre/name/'lib'/abi/filename,dest/filename)
  for module in ['MediaLib','FileCoreLibrary']:
   for f in sorted((root/module/'libs'/abi).glob('*.so')):libraries.append({'path':str(f.relative_to(root)),'sha256':sha(f),'bytes':f.stat().st_size})
 receipt={'schema':1,'native_artifact_run':review['run_id'],'source_commit':subprocess.check_output(['git','-c','safe.directory='+str(video),'-C',str(video),'rev-parse','HEAD'],text=True).strip(),'ndk':lock['ndk'],'android_api':23,'build':'DEBUG','native_lock_sha256':sha(here/'native-supernova146.lock.json'),'avos_patch_sha256':sha(here/'avos-supernova146-upstream.patch'),'abis':native,'libraries':libraries}
 (work/'packaging-inputs.json').write_text(json.dumps(receipt,indent=2)+'\n');print('Verified four-ABI native archives and staged current JNI packaging inputs')
if __name__=='__main__':main()
