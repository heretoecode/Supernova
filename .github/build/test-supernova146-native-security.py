#!/usr/bin/env python3
"""Reproduce native security regressions with original-source negative controls."""
import argparse,hashlib,json,os,shutil,socket,subprocess,tarfile,threading,urllib.request
from pathlib import Path
FONT_URL='https://raw.githubusercontent.com/google/fonts/8b0a1d0f5983c89bc2b93f1b5fb55f9e252744b5/ofl/robotoflex/RobotoFlex%5BGRAD,XOPQ,XTRA,YOPQ,YTAS,YTDE,YTFI,YTLC,YTUC,opsz,slnt,wdth,wght%5D.ttf'
FONT_SHA='9b523f7d82593df0107173849ebb8c817471a1df4b4fb2c3cbf40cfd810c8281'
def digest(p):return hashlib.sha256(p.read_bytes()).hexdigest()
def main():
 parser=argparse.ArgumentParser(description=__doc__);parser.add_argument('--workspace',type=Path,required=True);args=parser.parse_args();work=args.workspace.resolve();work.mkdir(parents=True,exist_ok=True);video=Path(__file__).resolve().parents[2];fixtures=video/'tools/native146';here=video/'.github/build';lock=json.loads((here/'native-supernova146.lock.json').read_text());inputs=work/'inputs';inputs.mkdir(exist_ok=True)
 def get(url,path,sha):
  if not path.exists():urllib.request.urlretrieve(url,path)
  if digest(path)!=sha:raise ValueError('Input hash differs: '+path.name)
 font=inputs/'RobotoFlex.ttf';get(FONT_URL,font,FONT_SHA)
 for item in lock['sources']:
  if item['name'] not in ['ffmpeg','freetype','zlib']:continue
  arc=inputs/(item['name']+'.tar.gz');get(item['url'],arc,item['sha256']);dest=inputs/item['name']
  if not dest.exists():
   dest.mkdir()
   with tarfile.open(arc) as archive:
    for member in archive.getmembers():
     member.name='/'.join(member.name.split('/')[1:])
     if member.name:archive.extract(member,dest,filter='data')
 records=[]
 for variant in ['original','patched']:
  root=work/variant;root.mkdir(exist_ok=True);sources=root/'source';sources.mkdir(exist_ok=True)
  for name in ['ffmpeg','freetype','zlib']:
   dest=sources/name
   if not dest.exists():
    shutil.copytree(inputs/name,dest)
    if variant=='patched':
     patch=here/(name+'-supernova146-security.patch')
     if digest(patch)!=lock['recipes'][patch.name]:raise ValueError('Security patch changed')
     subprocess.run(['patch','--forward','--batch','--fuzz=0','-p1','-i',str(patch)],cwd=dest,check=True)
  flags='-O1 -g -fsanitize=address,undefined -fno-omit-frame-pointer';common=['clang',*flags.split()]
  with (root/'build.log').open('w') as log:
   def run(command,cwd=root,env=None):subprocess.run(command,cwd=cwd,env=env,stdout=log,stderr=subprocess.STDOUT,check=True)
   zlib=sources/'zlib';run(['./configure','--static'],zlib,dict(os.environ,CC='clang',CFLAGS=flags,LDFLAGS='-fsanitize=address,undefined'));run(['make','-j2'],zlib)
   run(['cmake','-S',str(sources/'freetype'),'-B',str(root/'freetype'),'-DBUILD_SHARED_LIBS=OFF','-DFT_DISABLE_HARFBUZZ=ON','-DFT_DISABLE_BZIP2=ON','-DFT_DISABLE_BROTLI=ON','-DFT_DISABLE_PNG=ON','-DFT_DISABLE_ZLIB=ON','-DCMAKE_C_COMPILER=clang','-DCMAKE_C_FLAGS='+flags]);run(['cmake','--build',str(root/'freetype'),'-j2'])
   ff=root/'ffmpeg';ff.mkdir(exist_ok=True);run([str(sources/'ffmpeg/configure'),'--cc=clang','--disable-everything','--disable-autodetect','--disable-x86asm','--disable-programs','--disable-doc','--disable-avdevice','--disable-swscale','--disable-swresample','--disable-avfilter','--enable-static','--disable-shared','--enable-demuxer=rtsp','--enable-protocol=tcp,udp,rtp,http,file','--extra-cflags='+flags,'--extra-ldflags=-fsanitize=address,undefined'],ff);run(['make','-j2','libavutil/libavutil.a','libavformat/libavformat.a','libavcodec/libavcodec.a'],ff)
   run(common+['-I'+str(sources/'ffmpeg'),'-I'+str(ff),str(fixtures/'security_checks.c'),str(ff/'libavutil/libavutil.a'),'-lm','-pthread','-o',str(root/'security-checks')])
   run(common+['-I'+str(sources/'freetype/include'),str(fixtures/'freetype_coords.c'),str(root/'freetype/libfreetype.a'),'-lm','-o',str(root/'freetype-coords')])
   run(common+['-I'+str(zlib),str(fixtures/'zlib_socket.c'),str(zlib/'libz.a'),'-o',str(root/'zlib-socket')])
   run(common+['-I'+str(sources/'ffmpeg'),'-I'+str(ff),str(fixtures/'rtsp_redirect.c'),'-Wl,--start-group',*[str(ff/x/(x+'.a')) for x in ['libavformat','libavcodec','libavutil']],'-Wl,--end-group','-lm','-pthread','-lz','-o',str(root/'rtsp-redirect')])
  cases=[('empty-needle',[str(root/'security-checks'),'empty-needle']),('hdr-roundtrip',[str(root/'security-checks'),'hdr-roundtrip']),('deep-free',[str(root/'security-checks'),'deep-free']),('freetype-var-coordinates',[str(root/'freetype-coords'),str(font)]),('zlib-nonblocking-write',[str(root/'zlib-socket')])]
  def execute(name,command,timeout=8):
   try:p=subprocess.run(command,stdout=subprocess.PIPE,stderr=subprocess.STDOUT,timeout=timeout);rc=p.returncode;output=p.stdout
   except subprocess.TimeoutExpired as failure:rc=124;output=failure.stdout or b''
   (root/(name+'.log')).write_bytes(output);record={'variant':variant,'case':name,'exit_code':rc,'log_sha256':hashlib.sha256(output).hexdigest()};records.append(record);print(record,flush=True)
   if (variant=='patched')!=(rc==0):raise ValueError('Unexpected security regression outcome: '+name)
   return rc
  for name,command in cases:execute(name,command)
  listener=socket.socket();listener.bind(('127.0.0.1',0));listener.listen(16);listener.settimeout(.25);port=listener.getsockname()[1];requests=[];stop=threading.Event()
  def server():
   while not stop.is_set():
    try:client,_=listener.accept()
    except socket.timeout:continue
    except OSError:return
    with client:
     client.settimeout(.3);data=b''
     try:
      while b'\r\n\r\n' not in data:
       chunk=client.recv(8192)
       if not chunk:break
       data+=chunk
     except OSError:continue
     requests.append(data.split(b'\r\n',1)[0].decode())
     if len(requests)>32:stop.wait(6);continue
     seq=next((line.split(b':',1)[1].strip() for line in data.split(b'\r\n') if line.lower().startswith(b'cseq:')),b'1')
     client.sendall(b'RTSP/1.0 302 Moved\r\nCSeq: '+seq+b'\r\nLocation: rtsp://127.0.0.1:'+str(port).encode()+b'/loop\r\nContent-Length: 0\r\n\r\n')
  thread=threading.Thread(target=server,daemon=True);thread.start()
  try:rc=execute('rtsp-redirect',[str(root/'rtsp-redirect'),'rtsp://127.0.0.1:'+str(port)+'/loop'],5)
  finally:stop.set();listener.close();thread.join(2)
  records[-1]['requests']=len(requests)
  if variant=='patched' and len(requests)!=9 or variant=='original' and (rc!=124 or len(requests)<=9):raise ValueError('RTSP limit negative/control mismatch')
 (work/'security-regressions.json').write_text(json.dumps({'sanitizers':'AddressSanitizer+UndefinedBehaviorSanitizer','cases':records,'variable_font_sha256':FONT_SHA,'source_lock_sha256':digest(here/'native-supernova146.lock.json')},indent=2)+'\n')
if __name__=='__main__':main()
