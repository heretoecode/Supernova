import argparse,hashlib,json,subprocess
from pathlib import Path
parser=argparse.ArgumentParser(description='Generate synthetic test-only native codec regression media')
parser.add_argument('--output',type=Path,required=True)
root=parser.parse_args().output
root.mkdir(parents=True,exist_ok=True)
base=['ffmpeg','-hide_banner','-loglevel','error','-y','-threads','1']
video=['-f','lavfi','-i','testsrc2=size=96x64:rate=12:duration=1']
audio=['-f','lavfi','-i','sine=frequency=997:sample_rate=48000:duration=1']
commands=[('h264-aac.mp4',video+audio+['-c:v','libx264','-threads','1','-pix_fmt','yuv420p','-c:a','aac']),('hevc.mkv',video+['-c:v','libx265','-threads','1','-x265-params','pools=none:frame-threads=1:log-level=error']),('av1.mkv',video+['-c:v','libaom-av1','-threads','1','-cpu-used','8','-crf','40']),('vp9-opus.webm',video+audio+['-c:v','libvpx-vp9','-threads','1','-deadline','realtime','-cpu-used','8','-c:a','libopus']),('mpeg2-ac3.ts',video+audio+['-c:v','mpeg2video','-threads','1','-c:a','ac3']),('opus.ogg',audio+['-c:a','libopus']),('vorbis.ogg',audio+['-c:a','libvorbis']),('flac.flac',audio+['-c:a','flac']),('pcm.wav',audio+['-c:a','pcm_s16le']),('mp3.mp3',audio+['-c:a','libmp3lame']),('alac.m4a',audio+['-c:a','alac']),('eac3.eac3',audio+['-c:a','eac3']),('dts.dts',audio+['-c:a','dca','-strict','-2']),('ac3-51.ac3',['-f','lavfi','-i','aevalsrc=0.1*sin(997*2*PI*t)|0.1*sin(500*2*PI*t)|0.1*sin(250*2*PI*t)|0.1*sin(100*2*PI*t)|0.1*sin(750*2*PI*t)|0.1*sin(1250*2*PI*t):s=48000:d=1','-channel_layout','5.1','-c:a','ac3'])]
results=[]
for name,args in commands:
 command=base+args+[str(root/name)];subprocess.run(command,check=True,stdout=subprocess.DEVNULL)
 results.append({'file':name,'sha256':hashlib.sha256((root/name).read_bytes()).hexdigest(),'generation_arguments':args})
(root/'corpus.json').write_text(json.dumps(results,indent=2)+'\n')
print('Generated',len(results),'test-only media files')
