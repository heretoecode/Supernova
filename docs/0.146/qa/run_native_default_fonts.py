from pathlib import Path
import subprocess,json,hashlib
root=Path('/workspace/security146/nova');work=Path('/workspace/native146-runtime');tc=Path('/workspace/native146-toolchain/android-ndk-r30-beta1/toolchains/llvm/prebuilt/linux-x86_64/bin');results=[]
for abi,triple in [('arm64-v8a','aarch64-linux-android'),('armeabi-v7a','armv7a-linux-androideabi'),('x86','i686-linux-android'),('x86_64','x86_64-linux-android')]:
 pre=root/'native/prebuilt';android='/android-'+abi;lib='lib64' if abi in ['arm64-v8a','x86_64'] else 'lib';qemu='' if abi=='x86_64' else '/usr/bin/qemu-static ';binary=work/abi/'subtitle-format-probe'
 command=[str(tc/(triple+'23-clang')),'/workspace/Supernova/docs/0.146/qa/default_font_probe.c','-I'+str(root/'native/avos/Include'),'-I'+str(pre/'ffmpeg'/('dist-full-'+abi)/'include'),'-L'+str(root/'native/avos/libs'/abi),'-lavformat','-lavcodec','-lavutil','-ldl','-Wl,--allow-shlib-undefined','-o',str(binary)];subprocess.run(command,check=True)
 subprocess.run(['docker','cp',str(binary),'supernova146-runtime:'+android+'/test/subtitle-format-probe'],check=True)
 subprocess.run(['docker','exec','supernova146-runtime','mkdir','-p',android+'/system/fonts'],check=True)
 subprocess.run(['docker','cp','/workspace/native146-security-fixes/RobotoFlex.ttf','supernova146-runtime:'+android+'/system/fonts/RobotoFlex.ttf'],check=True)
 subprocess.run(['docker','exec','supernova146-runtime','chown','-R','10000:10000',android+'/test',android+'/corpus'],check=True)
 subprocess.run(['docker','exec','supernova146-runtime','chmod','0644',android+'/system/fonts/RobotoFlex.ttf'],check=True)
 for name in ['qa.srt','qa.vtt','qa.ass']:
  subprocess.run(['docker','cp',str(work/'corpus'/name),'supernova146-runtime:'+android+'/corpus/'+name],check=True)
  subprocess.run(['docker','exec','supernova146-runtime','chown','10000:10000',android+'/corpus/'+name],check=True)
  p=subprocess.run(['docker','exec','supernova146-runtime','sh','-c',f'env -u HOME -u XDG_CACHE_HOME -u XDG_CONFIG_HOME -u FONTCONFIG_FILE -u FONTCONFIG_PATH LD_LIBRARY_PATH=/test/lib:/system/{lib} chroot --userspec=10000:10000 {android} {qemu}/test/subtitle-format-probe /corpus/'+name],capture_output=True,text=True,timeout=60);results.append({'abi':abi,'file':name,'returncode':p.returncode,'output':p.stdout,'stderr':p.stderr});print(abi,name,p.returncode,p.stdout,p.stderr[-400:],flush=True);assert p.returncode==0
Path('/workspace/Supernova/docs/0.146/NATIVE_API23_DEFAULT_FONT.json').write_text(json.dumps({'native_artifact_run':40843685431,'scope':'UID10000 without HOME/XDG or writable system caches. Default native font loading with no fonts_dir, no default_font_name, no FONTCONFIG_FILE/PATH and a system-font Roboto Flex fixture; actual Android23bionic, no real EGL/ART or Shield acceptance implied','fixtures':[{ 'file':n,'sha256':hashlib.sha256((work/'corpus'/n).read_bytes()).hexdigest()} for n in ['qa.srt','qa.vtt','qa.ass','qa.sup']],'results':results},indent=2)+'\n')
