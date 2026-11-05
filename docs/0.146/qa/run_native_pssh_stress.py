from pathlib import Path
import subprocess,json,time
r=Path('/workspace/native146-runtime');rows=[]
for abi in ['arm64-v8a','armeabi-v7a','x86','x86_64']:
 android='/android-'+abi;lib='lib64' if abi in ['arm64-v8a','x86_64'] else 'lib';qemu='' if abi=='x86_64' else '/usr/bin/qemu-static '
 subprocess.run(['docker','cp',str(r/'corpus/many-pssh.mp4'),'supernova146-runtime:'+android+'/corpus/many-pssh.mp4'],check=True)
 start=time.monotonic();p=subprocess.run(['docker','exec','supernova146-runtime','sh','-c',f'LD_LIBRARY_PATH=/test/lib:/system/{lib} chroot {android} {qemu}/test/hls-probe /corpus/many-pssh.mp4'],capture_output=True,text=True,timeout=90);rows.append({'abi':abi,'pssh_records':10000,'returncode':p.returncode,'elapsed_seconds':time.monotonic()-start,'output':p.stdout,'stderr':p.stderr});print(abi,p.returncode,flush=True);assert p.returncode==0
Path('/workspace/Supernova/docs/0.146/NATIVE_API23_PSSH_STRESS.json').write_text(json.dumps({'scope':'Actual security-patched four-ABI FFmpeg9 DSOs on Android23bionic; full decode+cleanup with10000 MP4PSSH boxes; no Shield/device benchmark claim','native_artifact_run':40843685431,'results':rows},indent=2)+'\n')
