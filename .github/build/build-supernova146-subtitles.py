#!/usr/bin/env python3
"""Cross-build the pinned static libass dependency chain, without host libraries."""
import argparse, hashlib, json, os, shutil, subprocess
from pathlib import Path
TARGETS={'armeabi-v7a':('armv7a-linux-androideabi','arm'), 'arm64-v8a':('aarch64-linux-android','aarch64'), 'x86':('i686-linux-android','x86'), 'x86_64':('x86_64-linux-android','x86_64')}
ORDER=['zlib','libpng','freetype','fribidi','harfbuzz','libxml2','fontconfig','libunibreak','libass']
def main():
 p=argparse.ArgumentParser(description=__doc__)
 for name in ['sources','ndk','workspace','prebuilt']:p.add_argument('--'+name,type=Path,required=True)
 p.add_argument('--abi',choices=TARGETS,required=True);p.add_argument('--jobs',type=int,default=2);a=p.parse_args()
 src,ndk,work,pre=[x.resolve() for x in (a.sources,a.ndk,a.workspace,a.prebuilt)];work.mkdir(parents=True,exist_ok=True)
 triple,cpu=TARGETS[a.abi];bin=ndk/'toolchains/llvm/prebuilt/linux-x86_64/bin'
 env=dict(os.environ,PATH=str(bin)+':'+os.environ['PATH'],CC=str(bin/(triple+'21-clang')),CXX=str(bin/(triple+'21-clang++')),AR=str(bin/'llvm-ar'),RANLIB=str(bin/'llvm-ranlib'),STRIP=str(bin/'llvm-strip'))
 prefixes={n:pre/n for n in ORDER};pcs=[str(x/'lib'/a.abi/'pkgconfig') for x in prefixes.values()]
 env.update(PKG_CONFIG_PATH=':'.join(pcs),PKG_CONFIG_LIBDIR=':'.join(pcs),CFLAGS='-O2 -fPIC',CXXFLAGS='-O2 -fPIC',LDFLAGS='-Wl,-z,max-page-size=16384')
 includes=[]
 for n,suffix in [('freetype','freetype2'),('libpng','libpng16'),('harfbuzz','harfbuzz'),('libxml2','libxml2')]:includes.append('-I'+str(prefixes[n]/'include'/suffix))
 for x in prefixes.values():includes.append('-I'+str(x/'include'))
 env['CPPFLAGS']=' '.join(includes);env['LDFLAGS']+=' '+' '.join('-L'+str(x/'lib'/a.abi) for x in prefixes.values())
 cross=work/'meson-cross.ini'
 cross.write_text('[binaries]\n'+''.join(k+" = '"+env[v]+"'\n" for k,v in [('c','CC'),('cpp','CXX'),('ar','AR'),('strip','STRIP')])+"pkg-config = 'pkg-config'\n[properties]\nneeds_exe_wrapper = true\npkg_config_libdir = "+repr(pcs)+"\n[host_machine]\nsystem = 'android'\ncpu_family = '"+cpu+"'\ncpu = '"+cpu+"'\nendian = 'little'\n[built-in options]\nc_args = ['-O2', '-fPIC']\ncpp_args = ['-O2', '-fPIC']\n")
 records=[]
 for name in ORDER:
  prefix=prefixes[name];prefix.mkdir(parents=True,exist_ok=True)
  s=work/(name+'-source');build=work/(name+'-build');build.mkdir(exist_ok=True)
  if not s.exists():shutil.copytree(src/name,s,ignore=shutil.ignore_patterns('.git'))
  if name=='freetype' and not (s/'subprojects/dlg/include/dlg/dlg.h').exists():
   shutil.copytree(src/'dlg',s/'subprojects/dlg',dirs_exist_ok=True)
  log=work/(name+'.log')
  with log.open('w') as out:
   def run(cmd,cwd=build,extra=None):
    out.write('COMMAND '+repr(cmd)+'\n');out.flush();subprocess.run(cmd,cwd=cwd,env=dict(env,**(extra or {})),stdout=out,stderr=subprocess.STDOUT,check=True)
   flags=['--host='+triple,'--prefix='+str(prefix),'--libdir='+str(prefix/'lib'/a.abi),'--enable-static','--disable-shared']
   if name in ('fribidi','harfbuzz'):
    options=['-Dtests=false','-Ddocs=false','-Dbin=false'] if name=='fribidi' else ['-Dtests=disabled','-Ddocs=disabled','-Dfreetype=enabled','-Dsubset=disabled','-Draster=disabled','-Dvector=disabled','-Dgpu=disabled','-Dutilities=disabled','-Dglib=disabled','-Dgobject=disabled','-Dcairo=disabled','-Dicu=disabled']
    if not (build/'build.ninja').exists():run(['meson','setup',str(build),str(s),'--cross-file',str(cross),'--prefix='+str(prefix),'--libdir=lib/'+a.abi,'--default-library=static']+options)
    run(['ninja','-j'+str(a.jobs)]);run(['ninja','install'])
   else:
    if not (s/'configure').exists() or (name=='freetype' and not (s/'builds/unix/configure').exists()):
     # libxml2 autogen invokes configure; defer it to the out-of-tree phase.
     if name=='libxml2':run(['sh','autogen.sh'],s,{'NOCONFIGURE':'1'})
     elif name in ('libass','libunibreak','freetype'):run(['sh','autogen.sh'],s,{'NOCONFIGURE':'1'})
     else:run(['autoreconf','-fiv','-Wno-obsolete'],s)
    if name=='zlib':flags=['--prefix='+str(prefix),'--libdir='+str(prefix/'lib'/a.abi),'--static']
    if name=='freetype':flags+=['--without-harfbuzz','--with-png=yes','--with-zlib=yes','--without-bzip2','--without-brotli']
    if name=='libxml2':flags+=['--without-python','--without-icu','--without-iconv','--with-zlib']
    if name=='libass':
     flags+=['--enable-fontconfig','--enable-libunibreak']+(['--disable-asm'] if a.abi=='x86' else [])
     env['LIBUNIBREAK_CFLAGS']='-I'+str(prefixes['libunibreak']/'include');env['LIBUNIBREAK_LIBS']='-L'+str(prefixes['libunibreak']/'lib'/a.abi)+' -lunibreak'
    if name=='fontconfig':
     # Stable device fallback paths, not build-machine absolute directories.
     flags=['--host='+triple,'--prefix=/usr/local','--libdir=/usr/local/lib/'+a.abi,'--enable-static','--disable-shared','--disable-docs','--with-arch='+cpu,'--enable-libxml2','ac_cv_va_copy=C99']
     for link in ['usr','local']:
      if not (prefix/link).exists():(prefix/link).symlink_to('.')
    run([str(s/'configure')]+flags)
    run(['make','-j'+str(a.jobs)]+(['ARFLAGS=rc'] if name=='zlib' else []))
    run(['make','install']+(['ARFLAGS=rc'] if name=='zlib' else []),extra={'DESTDIR':str(prefix)} if name=='fontconfig' else None)
  archives=list((prefix/'lib'/a.abi).glob('*.a'))
  if not archives:raise ValueError('No static archive installed for '+name)
  records.append({'component':name,'abi':a.abi,'android_api':21,'archives':[{'path':str(f.relative_to(pre)),'sha256':hashlib.sha256(f.read_bytes()).hexdigest(),'bytes':f.stat().st_size} for f in archives]})
  (work/'evidence.json').write_text(json.dumps(records,indent=2)+'\n');print(name,a.abi,'built',flush=True)
if __name__=='__main__':main()
