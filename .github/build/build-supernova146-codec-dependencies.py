#!/usr/bin/env python3
"""Source-build the pinned dav1d, Opus and libmysofa Android runtime dependencies."""
import argparse,hashlib,json,os,shutil,subprocess
from pathlib import Path
TARGETS={'armeabi-v7a':('armv7a-linux-androideabi','arm'), 'arm64-v8a':('aarch64-linux-android','aarch64'), 'x86':('i686-linux-android','x86'), 'x86_64':('x86_64-linux-android','x86_64')}
def main():
 p=argparse.ArgumentParser(description=__doc__)
 for key in ['sources','ndk','workspace','prebuilt']:p.add_argument('--'+key,type=Path,required=True)
 p.add_argument('--abi',choices=TARGETS,required=True);p.add_argument('--jobs',type=int,default=2);a=p.parse_args()
 sources,ndk,work,pre=[v.resolve() for v in (a.sources,a.ndk,a.workspace,a.prebuilt)];work.mkdir(parents=True,exist_ok=True);triple,cpu=TARGETS[a.abi];bin=ndk/'toolchains/llvm/prebuilt/linux-x86_64/bin';env=dict(os.environ,PATH=str(bin)+':'+os.environ['PATH']);records=[]
 for name in ['dav1d','opus','libmysofa']:
  source=sources/name;build=work/name;build.mkdir(exist_ok=True);prefix=pre/name;prefix.mkdir(parents=True,exist_ok=True);lib=prefix/'lib'/a.abi;lib.mkdir(parents=True,exist_ok=True)
  with (work/(name+'.log')).open('w') as log:
   def run(cmd,cwd=build):
    log.write('COMMAND '+repr(cmd)+'\n');log.flush();subprocess.run(cmd,cwd=cwd,env=env,check=True,stdout=log,stderr=subprocess.STDOUT)
   if name=='dav1d':
    cross=work/'dav1d-cross.ini';cross.write_text("[binaries]\nc = '"+str(bin/(triple+'21-clang'))+"'\ncpp = '"+str(bin/(triple+'21-clang++'))+"'\nar = '"+str(bin/'llvm-ar')+"'\nstrip = '"+str(bin/'llvm-strip')+"'\n[properties]\nneeds_exe_wrapper = true\n[host_machine]\nsystem = 'android'\ncpu_family = '"+cpu+"'\ncpu = '"+cpu+"'\nendian = 'little'\n[built-in options]\nc_link_args = ['-Wl,-z,max-page-size=16384']\n")
    if not (build/'build.ninja').exists():run(['meson','setup',str(build),str(source),'--cross-file',str(cross),'--prefix='+str(prefix),'--libdir=lib/'+a.abi,'--buildtype=release','-Denable_tools=false','-Denable_tests=false'])
    run(['ninja','-j'+str(a.jobs)]);run(['ninja','install'])
   else:
    if name=='opus':
     source=work/'opus-source'
     if not source.exists():
      shutil.copytree(sources/'opus',source)
      patch=Path(__file__).resolve().parent/'opus-supernova146-cmake.patch';run(['git','apply','--check',str(patch)],source);run(['git','apply',str(patch)],source)
    flags=['-DANDROID_PLATFORM='+('23' if name=='libmysofa' else '21'),'-DANDROID_ABI='+a.abi,'-DANDROID_NDK='+str(ndk),'-DCMAKE_TOOLCHAIN_FILE='+str(ndk/'build/cmake/android.toolchain.cmake'),'-DCMAKE_BUILD_TYPE=Release','-DBUILD_SHARED_LIBS=ON','-DCMAKE_LIBRARY_OUTPUT_DIRECTORY='+str(lib),'-DCMAKE_SHARED_LINKER_FLAGS=-Wl,-z,max-page-size=16384','-DCMAKE_EXE_LINKER_FLAGS=-Wl,-z,max-page-size=16384','-DCMAKE_POLICY_VERSION_MINIMUM=3.5']
    if name=='opus':flags+=['-DOPUS_BUILD_TESTING=OFF','-DOPUS_BUILD_PROGRAMS=OFF','-DOPUS_DNN=OFF','-DOPUS_PACKAGE_VERSION=1.6.1-84-g084bb732']
    else:flags+=['-DBUILD_TESTS=OFF','-DBUILD_STATIC_LIBS=OFF','-DZLIB_LIBRARY='+str(pre/'zlib/lib'/a.abi/'libz.a'),'-DZLIB_INCLUDE_DIR='+str(pre/'zlib/include')]
    run(['cmake','-G','Ninja','-S',str(source),'-B',str(build)]+flags);run(['cmake','--build',str(build),'--parallel',str(a.jobs)])
    include=prefix/'include';include.mkdir(exist_ok=True)
    if name=='opus':
     (include/'opus').mkdir(exist_ok=True)
     for header in ['opus.h','opus_defines.h','opus_multistream.h','opus_projection.h','opus_types.h']:shutil.copyfile(source/'include'/header,include/'opus'/header)
    else:
     shutil.copyfile(source/'src/hrtf/mysofa.h',include/'mysofa.h');shutil.copyfile(build/'src/mysofa_export.h',include/'mysofa_export.h')
   library=lib/('lib'+name.removeprefix('lib')+'.so')
   if not library.exists():raise ValueError('Missing shared codec dependency: '+name)
   readelf=subprocess.check_output([str(bin/'llvm-readelf'),'-d','-l','--wide',str(library)],text=True)
   if not ('0x4000' in readelf or '0x10000' in readelf):raise ValueError('16KiB native alignment missing')
   records.append({'name':name,'abi':a.abi,'path':str(library.relative_to(pre)),'sha256':hashlib.sha256(library.read_bytes()).hexdigest(),'bytes':library.stat().st_size,'elf':readelf})
   (work/'evidence.json').write_text(json.dumps(records,indent=2)+'\n');print(name,a.abi,'built',flush=True)
if __name__=='__main__':main()
