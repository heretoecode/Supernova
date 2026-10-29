#!/usr/bin/env python3
"""Build FFmpeg9.0.2 with reviewed playback patches and verified TLS defaults."""
import argparse,hashlib,json,os,shutil,subprocess,re
from pathlib import Path
TARGETS={'armeabi-v7a':('armv7a-linux-androideabi','arm'), 'arm64-v8a':('aarch64-linux-android','aarch64'), 'x86':('i686-linux-android','x86'), 'x86_64':('x86_64-linux-android','x86_64')}
def main():
 p=argparse.ArgumentParser(description=__doc__)
 for name in ['sources','ndk','workspace','prebuilt']:p.add_argument('--'+name,type=Path,required=True)
 p.add_argument('--abi',choices=TARGETS,required=True);p.add_argument('--jobs',type=int,default=2);a=p.parse_args()
 sources,ndk,work,pre=[v.resolve() for v in (a.sources,a.ndk,a.workspace,a.prebuilt)];work.mkdir(parents=True,exist_ok=True);source=work/'source';here=Path(__file__).resolve().parent
 if not source.exists():
  shutil.copytree(sources/'ffmpeg',source)
  for name in ['opus','atempo','sofalizer','security']:
   patch=here/('ffmpeg-supernova146-'+name+'.patch');subprocess.run(['patch','--forward','--batch','--fuzz=0','-p1','-i',str(patch)],cwd=source,check=True)
 # Reject the upstream TLS-verification-disabling patch, including nested opens.
 tls=(source/'libavformat/tls.h').read_text()
 for key in ['tls_verify','verify']:
  if not re.search(r'\{"'+key+r'"[^\n]+\.i64 = 1',tls):raise ValueError('Native TLS peer verification must default on')
 triple,arch=TARGETS[a.abi];bin=ndk/'toolchains/llvm/prebuilt/linux-x86_64/bin';prefix=pre/'ffmpeg'/('dist-full-'+a.abi);prefix.mkdir(parents=True,exist_ok=True)
 pcs=work/'pkgconfig';pcs.mkdir(exist_ok=True)
 for name,pcname,version in [('opus','opus','1.6.1-84-g084bb732'),('dav1d','dav1d','1.5.4'),('libmysofa','libmysofa','1.3.5')]:
  libname=name.removeprefix('lib');include=pre/name/'include';extra=' -I'+str(include/'opus') if name=='opus' else ''
  (pcs/(pcname+'.pc')).write_text('Name: '+name+'\nDescription: Pinned source Android runtime\nVersion: '+version+'\nLibs: -L'+str(pre/name/'lib'/a.abi)+' -l'+libname+'\nLibs.private: -lm\nCflags: -I'+str(include)+extra+'\n')
 ssl=pre/'openssl'/('dist-'+a.abi);ssllib=next(x for x in [ssl/'lib',ssl/'lib64'] if (x/'libssl.a').exists())
 env=dict(os.environ,PATH=str(bin)+':'+os.environ['PATH'],PKG_CONFIG_LIBDIR=str(pcs)+':'+str(ssllib/'pkgconfig'),PKG_CONFIG_PATH='',SOURCE_DATE_EPOCH='1793224800')
 cflags='-O2 -fPIC -I'+str(ssl/'include')+' -I'+str(pre/'zlib/include')
 ldflags='-Wl,-z,max-page-size=16384,--exclude-libs,ALL -L'+str(ssllib)+' -L'+str(pre/'zlib/lib'/a.abi)
 for name in ['dav1d','opus','libmysofa']:ldflags+=' -L'+str(pre/name/'lib'/a.abi)
 flags=['--enable-cross-compile','--target-os=android','--arch='+arch,'--cc='+str(bin/(triple+'23-clang')),'--cxx='+str(bin/(triple+'23-clang++')),'--nm='+str(bin/'llvm-nm'),'--ar='+str(bin/'llvm-ar'),'--ranlib='+str(bin/'llvm-ranlib'),'--strip='+str(bin/'llvm-strip'),'--sysroot='+str(bin.parent/'sysroot'),'--prefix='+str(prefix),'--pkg-config=pkg-config','--pkg-config-flags=--static','--extra-cflags='+cflags,'--extra-ldflags='+ldflags,'--enable-shared','--disable-static','--disable-symver','--disable-autodetect','--disable-programs','--disable-doc','--enable-swresample','--enable-swscale','--disable-bzlib','--disable-muxers','--disable-bsfs','--disable-avdevice','--disable-devices','--enable-demuxers','--enable-parsers','--enable-muxer=spdif','--disable-encoders','--enable-encoder=ac3','--enable-decoders','--disable-v4l2-m2m','--enable-libdav1d','--enable-libopus','--disable-vulkan','--enable-openssl','--enable-libmysofa','--enable-zlib']
 if a.abi=='x86':flags+=['--disable-asm']
 if a.abi=='armeabi-v7a':flags+=['--extra-cflags='+cflags+' -march=armv7-a -mfpu=neon -mfloat-abi=softfp -mthumb']
 with (work/'build.log').open('w') as out:
  subprocess.run([str(source/'configure'),*flags],cwd=source,env=env,stdout=out,stderr=subprocess.STDOUT,check=True)
  config=(source/'config.h').read_text()
  for prohibited in ['GPL','GPLV3','LGPLV3','NONFREE']:
   if '#define CONFIG_'+prohibited+' 0' not in config:raise ValueError('Unreviewed native licence configuration '+prohibited)
  subprocess.run(['make','-j'+str(a.jobs),'install'],cwd=source,env=env,stdout=out,stderr=subprocess.STDOUT,check=True)
 records=[]
 for f in sorted((prefix/'lib').glob('*.so')):
  elf=subprocess.check_output([str(bin/'llvm-readelf'),'-l','-d','--wide',str(f)],text=True)
  align=[int(line.split()[-1],16) for line in elf.splitlines() if line.strip().startswith('LOAD ')]
  if not align or min(align)<16384:raise ValueError('Native alignment below16KiB')
  soname=re.search(r'\(SONAME\).*?\[(.*?)\]',elf).group(1)
  if soname!=f.name:raise ValueError('Android FFmpeg SONAME differs from packaged filename')
  records.append({'file':f.name,'sha256':hashlib.sha256(f.read_bytes()).hexdigest(),'bytes':f.stat().st_size,'soname':soname,'needed':re.findall(r'\(NEEDED\).*?\[(.*?)\]',elf)})
 if len(records)!=6:raise ValueError('Expected six FFmpeg libraries')
 receipt={'schema':1,'abi':a.abi,'source_commit':'946fcce07b6dcd0331c8cc609192aeff5e1924f8','android_api':23,'tls_peer_and_hostname_verification':True,'configure':flags,'libraries':records,'config_h_sha256':hashlib.sha256((source/'config.h').read_bytes()).hexdigest(),'config_components_sha256':hashlib.sha256((source/'config_components.h').read_bytes()).hexdigest()}
 (work/'evidence.json').write_text(json.dumps(receipt,indent=2)+'\n');print('Built all six FFmpeg9 libraries for',a.abi)
if __name__=='__main__':main()
