from pathlib import Path
import subprocess,json
work=Path('/workspace/native146-runtime');tc=Path('/workspace/native146-toolchain/android-ndk-r30-beta1/toolchains/llvm/prebuilt/linux-x86_64/bin');root=Path('/workspace/security146/nova');rows=[]
for abi,triple in [('arm64-v8a','aarch64-linux-android'),('armeabi-v7a','armv7a-linux-androideabi'),('x86','i686-linux-android'),('x86_64','x86_64-linux-android')]:
 probe=work/abi/'native-load-probe';subprocess.run([str(tc/(triple+'23-clang')),'/workspace/native146-load-probe.c','-ldl','-o',str(probe)],check=True)
 android='/android-'+abi;lib='lib64' if abi in ('arm64-v8a','x86_64') else 'lib';qemu='' if abi=='x86_64' else '/usr/bin/qemu-static '
 for source in [root/'native/avos/libs'/abi,root/'FileCoreLibrary/libs'/abi]:subprocess.run(['docker','cp',str(source)+'/.','supernova146-runtime:'+android+'/test/lib'],check=True,stdout=subprocess.DEVNULL)
 subprocess.run(['docker','cp',str(probe),'supernova146-runtime:'+android+'/test/native-load-probe'],check=True,stdout=subprocess.DEVNULL)
 result=subprocess.run(['docker','exec','supernova146-runtime','sh','-c',f'LD_LIBRARY_PATH=/test/lib:/system/{lib} chroot {android} {qemu}/test/native-load-probe'],capture_output=True,text=True,timeout=60);rows.append({'abi':abi,'returncode':result.returncode,'output':result.stdout,'stderr':result.stderr});print(abi,result.returncode,result.stdout[-250:],flush=True)
Path('/workspace/Supernova/docs/0.146/NATIVE_API23_LIBRARY_LOADING.json').write_text(json.dumps({'scope':'Android23 bionic RTLD_NOW loading; no JVM/GL/real-device acceptance implied','results':rows},indent=2)+'\n')
