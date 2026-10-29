"""Run rendering/loading probes asUID10000 against native bytes taken from the exact APK."""
from pathlib import Path
import argparse,hashlib,json,subprocess,tarfile,zipfile
p=argparse.ArgumentParser(description=__doc__);p.add_argument('--apk',type=Path,required=True);p.add_argument('--receipt',type=Path,required=True);a=p.parse_args();r=json.loads(a.receipt.read_text());digest=hashlib.file_digest(a.apk.open('rb'),'sha256').hexdigest();assert digest==r['apk_sha256'];video=Path('/workspace/Supernova');root=Path('/workspace/security146/nova');work=Path('/workspace/final146-native');work.mkdir(exist_ok=True);tc=Path('/workspace/native146-toolchain/android-ndk-r30-beta1/toolchains/llvm/prebuilt/linux-x86_64/bin');rows=[];expected={x['path']:x['sha256'] for x in r['native_libraries']}
for abi,triple in [('arm64-v8a','aarch64-linux-android'),('armeabi-v7a','armv7a-linux-androideabi'),('x86','i686-linux-android'),('x86_64','x86_64-linux-android')]:
 android='/android-'+abi;lib='lib64' if abi in ['arm64-v8a','x86_64'] else 'lib';qemu='' if abi=='x86_64' else '/usr/bin/qemu-static ';dest=work/abi;dest.mkdir(exist_ok=True);pre=root/'native/prebuilt';include=[root/'native/avos/Include',pre/'ffmpeg'/('dist-full-'+abi)/'include'];subprocess.run(['docker','exec','supernova146-runtime','rm','-rf',android+'/test/lib'],check=True);subprocess.run(['docker','exec','supernova146-runtime','mkdir','-p',android+'/test/lib'],check=True)
 process=subprocess.Popen(['docker','exec','-i','supernova146-runtime','tar','-xf','-','-C',android+'/test/lib'],stdin=subprocess.PIPE)
 with zipfile.ZipFile(a.apk) as packed,tarfile.open(fileobj=process.stdin,mode='w|') as stream:
  for name in sorted(n for n in expected if n.startswith('lib/'+abi+'/')):
   data=packed.read(name);assert hashlib.sha256(data).hexdigest()==expected[name];info=tarfile.TarInfo(name.split('/')[-1]);info.size=len(data);info.mode=0o644;info.uid=info.gid=10000;import io;stream.addfile(info,io.BytesIO(data))
   if name.endswith('/libavos.so'):(dest/'libavos.so').write_bytes(data)
 process.stdin.close();assert process.wait()==0
 probes={'format':video/'docs/0.146/qa/native_subtitle_probe.c','default':video/'docs/0.146/qa/default_font_probe.c','font':video/'tools/native146/font_stack.c','load':video/'docs/0.146/qa/final_native_load_probe.c'}
 for kind,source in probes.items():
  binary=dest/(kind+'-probe');command=[str(tc/(triple+'23-clang')),str(source),'-o',str(binary),'-ldl']
  if kind in ['format','default']:command += ['-I'+str(i) for i in include]+['-L'+str(root/'native/avos/libs'/abi),'-lavformat','-lavcodec','-lavutil','-Wl,--allow-shlib-undefined']
  if kind=='font':command += ['-I'+str(pre/'fontconfig/include'),'-I'+str(pre/'libass/include'),'-I'+str(pre/'freetype/include/freetype2')]
  subprocess.run(command,check=True);subprocess.run(['docker','cp',str(binary),'supernova146-runtime:'+android+'/test/'+binary.name],check=True)
 subprocess.run(['docker','exec','supernova146-runtime','chown','-R','10000:10000',android+'/test',android+'/corpus'],check=True)
 cases=[('format',n) for n in ['qa.srt','qa.vtt','qa.ass','qa.sup']]+[('default',n) for n in ['qa.srt','qa.vtt','qa.ass']]+[('font',''),('load','')]
 for kind,name in cases:
  env='env -u HOME -u XDG_CACHE_HOME -u XDG_CONFIG_HOME -u FONTCONFIG_FILE -u FONTCONFIG_PATH '
  if kind!='default':env+='FONTCONFIG_FILE=/test/fonts.conf FONTCONFIG_PATH=/test '
  command=env+f'LD_LIBRARY_PATH=/test/lib:/system/{lib} chroot --userspec=10000:10000 {android} {qemu}/test/{kind}-probe'+(' /corpus/'+name if name else '')
  result=subprocess.run(['docker','exec','supernova146-runtime','sh','-c',command],capture_output=True,text=True,timeout=60);rows.append({'abi':abi,'case':kind,'fixture':name,'returncode':result.returncode,'output':result.stdout,'stderr':result.stderr});print(abi,kind,name,result.returncode,result.stdout[-200:],flush=True);assert result.returncode==0,(abi,kind,result.stderr)
 symbols=subprocess.check_output([str(tc/'llvm-nm'),'-D','--defined-only',str(dest/'libavos.so')],text=True);assert not any(line.split()[-1] in ['xmlXIncludeProcess','xmlXIncludeCopyRange','xmlXIncludeCopyXPointer','htmlSaveFileFormat'] for line in symbols.splitlines() if line.split())
proof={'source_commit':r['source_commit'],'apk_sha256':digest,'tested_uid':10000,'case_count':len(rows),'scope':'Exact APK native bytes, official Android23 bionic, four ABIs (non-x86_64 under QEMU); actual AVOS subtitle rendering/expiry/flush, system/custom fonts, FreeType variation fix, restricted XML symbols and14-DSO strict loading. No ART/JNI/EGL/physical-device acceptance implied.','native_libraries':r['native_libraries'],'results':rows};assert len(rows)==36;Path('/workspace/Supernova/docs/0.146/FINAL_APK_NATIVE_RUNTIME.json').write_text(json.dumps(proof,indent=2)+'\n');print('Exact final APK native runtime passed',len(rows),'cases',digest)
