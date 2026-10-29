#!/usr/bin/env python3
"""Verify the packaged FFmpeg9/codec DSOs against exact four-ABI source artifacts."""
import argparse,hashlib,json,zipfile
from pathlib import Path
ABIS={'arm64-v8a','armeabi-v7a','x86','x86_64'}
FFMPEG={'libavcodec.so','libavformat.so','libavfilter.so','libavutil.so','libswresample.so','libswscale.so'}
def verify(apk,evidence):
 record=json.loads(Path(evidence).read_text());count=0
 if set(record['abis'])!=ABIS or record['android_api']!=23:raise ValueError('Four-ABI API23 packaging receipt required')
 with zipfile.ZipFile(apk) as archive:
  names=archive.namelist()
  if len(names)!=len(set(names)) or archive.testzip() is not None:raise ValueError('Corrupt or duplicate APK entries')
  if any(n.endswith(('/libtorrentd.so','/libyuv.so')) for n in names):raise ValueError('Retired native dependencies must not be packaged')
  for abi,info in record['abis'].items():
   if not any(x['name']=='ffmpeg' and x['commit']=='946fcce07b6dcd0331c8cc609192aeff5e1924f8' for x in info['sources']):raise ValueError('FFmpeg9 source identity differs')
   expected={Path(x['path']).name:x['sha256'] for x in info['files'] if x['path'].startswith('ffmpeg/dist-full-'+abi+'/lib/') and Path(x['path']).name in FFMPEG}
   if set(expected)!=FFMPEG:raise ValueError('Incomplete FFmpeg9 source inventory')
   for name,sha in expected.items():
    if hashlib.sha256(archive.read('lib/'+abi+'/'+name)).hexdigest()!=sha:raise ValueError('APK FFmpeg differs from verified source build')
    count+=1
   for component in ['dav1d','opus','libmysofa']:
    filename='lib'+component.removeprefix('lib')+'.so';path=component+'/lib/'+abi+'/'+filename;entries=[x for x in info['files'] if x['path']==path]
    if len(entries)!=1 or hashlib.sha256(archive.read('lib/'+abi+'/'+filename)).hexdigest()!=entries[0]['sha256']:raise ValueError('APK codec dependency differs from verified source build')
 return count
if __name__=='__main__':
 p=argparse.ArgumentParser(description=__doc__);p.add_argument('--apk',type=Path,required=True);p.add_argument('--evidence',type=Path,required=True);a=p.parse_args();print('Matched FFmpeg9 libraries:',verify(a.apk,a.evidence))
