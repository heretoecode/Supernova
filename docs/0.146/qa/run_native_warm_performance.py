from pathlib import Path
import subprocess,json,statistics,hashlib
root=Path('/workspace/Supernova');work=Path('/workspace/native146-runtime');tc=Path('/workspace/native146-toolchain/android-ndk-r30-beta1/toolchains/llvm/prebuilt/linux-x86_64/bin');rows=[]
p=(root/'tools/foundation_native_probe.c').read_text().replace('int main(int argc, char **argv) {','int original_main(int argc, char **argv) {')
p+='''\n#include <time.h>\nint main(int argc,char **argv){assert(argc==2);decode(argv[1],NULL);struct timespec a,b;clock_gettime(CLOCK_MONOTONIC,&a);for(int i=0;i<20;i++)if(decode(argv[1],NULL))return 42;clock_gettime(CLOCK_MONOTONIC,&b);fprintf(stderr,"WARM_SECONDS=%.9f\\n",(b.tv_sec-a.tv_sec)+(b.tv_nsec-a.tv_nsec)/1e9);return 0;}\n''';p='#include <assert.h>\n'+p;source=work/'warm-performance.c';source.write_text(p)
for abi,triple in [('arm64-v8a','aarch64-linux-android'),('armeabi-v7a','armv7a-linux-androideabi'),('x86','i686-linux-android'),('x86_64','x86_64-linux-android')]:
 android='/android-'+abi;lib='lib64' if abi in ['arm64-v8a','x86_64'] else 'lib';qemu='' if abi=='x86_64' else '/usr/bin/qemu-static ';pre=Path('/workspace/native146-security-ci')/abi/'unpacked/prebuilt';results={}
 for label,headers in [('baseline',work/'ffmpeg8-headers'/('dist-full-'+abi)/'include'),('candidate',pre/'ffmpeg'/('dist-full-'+abi)/'include')]:
  path=work/abi/label;binary=path/'warm-performance';subprocess.run([str(tc/(triple+'23-clang')),str(source),'-I'+str(headers),'-L'+str(path/'lib'),'-lavformat','-lavcodec','-lavfilter','-lavutil','-o',str(binary)],check=True,stdout=subprocess.DEVNULL,stderr=subprocess.DEVNULL)
  subprocess.run(['docker','exec','supernova146-runtime','mkdir','-p',android+'/performance-'+label],check=True);subprocess.run(['docker','cp',str(path)+'/.','supernova146-runtime:'+android+'/performance-'+label],check=True,stdout=subprocess.DEVNULL)
  data={}
  for name in ['h264-aac.mp4','hevc.mkv','av1.mkv','ac3-51.ac3']:
   values=[]
   for repeat in range(3):
    command=f'LD_LIBRARY_PATH=/performance-{label}/lib:/system/{lib} chroot {android} {qemu}/performance-{label}/warm-performance /corpus/{name}'
    r=subprocess.run(['docker','exec','supernova146-runtime','sh','-c',command],capture_output=True,text=True,timeout=90);assert r.returncode==0,(abi,label,r.stderr)
    values.append(float(r.stderr.rsplit('WARM_SECONDS=',1)[1].strip()))
   data[name]={'twenty_decodes_seconds':values,'median_per_decode_seconds':statistics.median(values)/20}
  results[label]=data;print(abi,label,data,flush=True)
 rows.append({'abi':abi,'measurements':results})
(root/'docs/0.146/NATIVE_WARM_PERFORMANCE.json').write_text(json.dumps({'scope':'Wall clock inside an already running Android23 process; one warm-up decode then twenty measured decodes, three trials. QEMU remains for non-x86_64 ABIs; software only, no Shield or hardware performance acceptance.','source_sha256':hashlib.sha256(source.read_bytes()).hexdigest(),'native_artifact_run':40843685431,'results':rows},indent=2)+'\n')
