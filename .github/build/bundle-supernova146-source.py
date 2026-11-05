#!/usr/bin/env python3
"""Bundle exact application/sibling/native source, patches and rebuild evidence."""
import argparse,hashlib,json,os,subprocess,tarfile,tempfile
from pathlib import Path

def sha(p):
 with p.open('rb') as f:return hashlib.file_digest(f,'sha256').hexdigest()
def main():
 p=argparse.ArgumentParser(description=__doc__)
 for name in ['root','native-sources','native-evidence','jetty-source','output']:p.add_argument('--'+name,type=Path,required=True)
 p.add_argument('--additional-sources',type=Path);a=p.parse_args();root=a.root.resolve();video=root/'Video';source=subprocess.check_output(['git','-C',str(video),'rev-parse','HEAD'],text=True).strip();here=video/'.github/build';lock=json.loads((here/'native-supernova146.lock.json').read_text());record={'schema':1,'source_commit':source,'source_lock_sha256':sha(here/'native-supernova146.lock.json'),'archives':[],'modules':[]};a.output.parent.mkdir(parents=True,exist_ok=True)
 with tempfile.TemporaryDirectory(prefix='supernova146-source-') as tmp:
  work=Path(tmp);readme=work/'REBUILD.md';readme.write_text('''# Supernova0.146 corresponding source

The manifest records the exact application commit, source archives, sibling trees,
patches and native configuration used for this QA candidate. Original copyrights
and full licence texts remain in each source archive and the application catalogue.

1. Extract application.tar and the sibling module archives under a common nova/
   workspace, with the application at nova/Video and native modules at nova/native/.
   Sibling archives already include the exact reviewed patches; do not apply twice.
   Alternatively start from immutable nova-ci.xml pins and run
   .github/build/prepare-supernova146-dependencies.py for a fresh source checkout.
2. Install Java21, Python3, GNU build tools, autoconf/automake/libtool, CMake, Meson,
   Ninja, pkg-config, nasm, gettext/autopoint and gperf. Acquire the exact NDK/source
   inputs with acquire-supernova146-native.py. The lock pins every download/commit.
3. Run build-supernova146-native-stack.py for each of the four ABIs. This builds
   OpenSSL, the static subtitle/font chain, codecs and all six FFmpeg libraries.
   Its source patches include the security, audio-tempo, Opus and libmysofa fixes.
4. Use the same per-ABI installed files and current AVOS/AudioCompress/nativehelper
   and FileCore NDK sources to rebuild JNI. package-supernova146-native.py documents
   staging/receipt verification; regenerate the reviewed native archive receipt for
   a modified local build rather than falsely claiming its hashes are the original.
5. Build the locked Jetty source fork with build-supernova146-jetty.py. Its four
   optional MethodHandle-based server/module classes are explicitly omitted for the
   API23 UPnP transport; source is complete and the omissions are recorded in the lock.
6. Build the unsigned universal app with Gradle using -PfoundationVerify -PsupernovaQa
   -Puniversal and the noamazonRelease variant. The workflows document complete
   application/library tests, lint, API23 and binary conformance. For modified builds,
   update the reviewed runtime/native hashes and validate all software/device gates.
7. Install a modified APK using your own developer key on a separate test install.
   Android requires the existing installation certificate for an in-place upgrade.
   Private signing material is not corresponding source and is never distributed.

No restriction is imposed on reverse engineering for debugging modifications to LGPL
libraries. A signed QA APK does not constitute physical Shield/HDMI/server acceptance.
''')
  with tarfile.open(a.output,'w:xz',preset=4) as bundle:
   bundle.add(readme,arcname='REBUILD.md')
   app=work/'application.tar'
   with app.open('wb') as stream:subprocess.run(['git','-C',str(video),'archive','--format=tar',source],stdout=stream,check=True)
   bundle.add(app,arcname='modules/application.tar');record['archives'].append({'file':'modules/application.tar','sha256':sha(app),'kind':'exact committed application source'})
   for module in ['MediaLib','FileCoreLibrary','native/avos','native/audiocompress','native/libnativehelper']:
    path=root/module;paths=subprocess.check_output(['git','-C',str(path),'ls-files','--cached','--others','--exclude-standard','-z']).decode().split('\0');paths=sorted(set(x for x in paths if x and '__pycache__' not in x.split('/') and not x.endswith('.pyc')));target=work/(module.replace('/','-')+'.tar')
    with tarfile.open(target,'w') as archive:
     for name in paths:
      f=path/name
      if f.exists() or f.is_symlink():archive.add(f,arcname=name,recursive=False)
    base=subprocess.check_output(['git','-C',str(path),'rev-parse','HEAD'],text=True).strip();bundle.add(target,arcname='modules/'+target.name);record['modules'].append({'path':module,'base_commit':base,'archive_sha256':sha(target),'source_files':len(paths),'scope':'Complete patched source tree; original Git metadata and generated build outputs excluded'})
   for item in lock['sources']:
    if item.get('git_repository'):
     archive=work/(item['name']+'.tar');actual=subprocess.check_output(['git','-C',str(a.native_sources/item['name']),'rev-parse','HEAD'],text=True).strip()
     if actual!=item['commit']:raise ValueError('Source Git pin differs')
     with archive.open('wb') as stream:subprocess.run(['git','-C',str(a.native_sources/item['name']),'archive','--format=tar',actual],stdout=stream,check=True)
    else:
     archive=a.native_sources/(item['name']+'.tar.gz')
     if sha(archive)!=item['sha256']:raise ValueError('Source archive differs: '+item['name'])
    filename='native-inputs/'+archive.name;bundle.add(archive,arcname=filename);record['archives'].append({'file':filename,'source_commit':item['commit'],'sha256':sha(archive),'input':item})
   jetty=json.loads((here/'jetty-supernova146.lock.json').read_text());expected=next(x['sha256'] for x in jetty['inputs'] if x['file']=='source.tar.gz')
   if sha(a.jetty_source)!=expected:raise ValueError('Jetty source archive differs')
   bundle.add(a.jetty_source,arcname='jetty/jetty-source.tar.gz');record['archives'].append({'file':'jetty/jetty-source.tar.gz','sha256':expected,'source_commit':jetty['source_commit']})
   for abi in lock['abis']:
    base=a.native_evidence/abi/'unpacked'
    for folder in ['configuration','evidence']:
     bundle.add(base/folder,arcname='native-build/'+abi+'/'+folder)
    bundle.add(base/'native-stack.json',arcname='native-build/'+abi+'/native-stack.json')
   if a.additional_sources:
    for f in sorted(a.additional_sources.iterdir()):
     if f.is_file():bundle.add(f,arcname='runtime-copyleft-sources/'+f.name);record['archives'].append({'file':'runtime-copyleft-sources/'+f.name,'sha256':sha(f)})
   manifest=work/'SOURCE_MANIFEST.json';manifest.write_text(json.dumps(record,indent=2)+'\n');bundle.add(manifest,arcname='SOURCE_MANIFEST.json')
 receipt={'file':a.output.name,'source_commit':source,'sha256':sha(a.output),'bytes':a.output.stat().st_size,'contents':record};a.output.with_suffix(a.output.suffix+'.json').write_text(json.dumps(receipt,indent=2)+'\n');print('Corresponding-source bundle:',a.output.name,receipt['bytes'],receipt['sha256'])
if __name__=='__main__':main()
