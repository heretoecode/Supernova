#!/usr/bin/env python3
"""Rebuild seven Jetty9.4 modules from pinned source with explicit API23/security backports.

Upstream Maven coordinates are retained so advisory scanners still report the
upstream version. Source patch/evidence, not a version relabel, establishes fixes.
"""
import argparse,hashlib,json,os,shutil,subprocess,tarfile,urllib.request,zipfile
from pathlib import Path
MODULES=['util','io','http','server','client','security','servlet']
def sha(p):return hashlib.sha256(p.read_bytes()).hexdigest()
def main():
 p=argparse.ArgumentParser(description=__doc__);p.add_argument('--workspace',type=Path,required=True);a=p.parse_args();work=a.workspace.resolve();work.mkdir(parents=True,exist_ok=True)
 here=Path(__file__).resolve().parent;lock=json.loads((here/'jetty-supernova146.lock.json').read_text());inputs=work/'inputs';inputs.mkdir(exist_ok=True)
 for item in lock['inputs']:
  f=inputs/item['file']
  if not f.exists():
   temporary=f.with_suffix(f.suffix+'.download');urllib.request.urlretrieve(item['url'],temporary)
   if sha(temporary)!=item['sha256']:raise ValueError('Download hash differs: '+item['file'])
   temporary.replace(f)
  if sha(f)!=item['sha256']:raise ValueError('Input hash differs: '+item['file'])
 patch=here/lock['source_patch']
 if sha(patch)!=lock['source_patch_sha256']:raise ValueError('Reviewed Jetty source patch changed')
 source=work/'source'
 if not source.exists():
  source.mkdir()
  with tarfile.open(inputs/'source.tar.gz') as archive:
   for member in archive.getmembers():
    member.name='/'.join(member.name.split('/')[1:])
    if member.name:archive.extract(member,source,filter='data')
  subprocess.run(['patch','--dry-run','--batch','--fuzz=0','-p1','-i',str(patch)],cwd=source,check=True)
  subprocess.run(['patch','--batch','--fuzz=0','-p1','-i',str(patch)],cwd=source,check=True)
 else:
  # Refuse stale source rather than silently using an older patch on rerun.
  if not (work/'source-patch.sha256').exists() or (work/'source-patch.sha256').read_text().strip()!=sha(patch):raise ValueError('Use a fresh workspace for a changed patch')
 # Refuse a no-op/skipped patch before compilation or packaging.
 checks={
  'jetty-http/src/main/java/org/eclipse/jetty/http/HttpParser.java':'private enum ChunkSizeState',
  'jetty-server/src/main/java/org/eclipse/jetty/server/HttpChannel.java':'Conflicting authority',
  'jetty-security/src/main/java/org/eclipse/jetty/security/authentication/DigestAuthenticator.java':'Digest value is not ISO-8859-1',
  'jetty-util/src/main/java/org/eclipse/jetty/util/thread/api23/LongAdder.java':'class LongAdder',
 }
 for name,expected in checks.items():
  if not (source/name).is_file() or expected not in (source/name).read_text():raise ValueError('Jetty source backport missing: '+name)
 (work/'source-patch.sha256').write_text(sha(patch)+'\n')
 jre=work/'boot-jre8'
 if not jre.exists():
  jre.mkdir()
  with tarfile.open(inputs/'jre8.tar.gz') as archive:archive.extractall(jre,filter='data')
 boot=list(jre.rglob('rt.jar'))
 if len(boot)!=1:raise ValueError('Pinned JRE8 boot classes missing')
 classes=work/'classes'
 if classes.exists():shutil.rmtree(classes)
 classes.mkdir();version=lock['upstream_version'];jars=sorted(inputs.glob('*.jar'));jars.remove(inputs/'ecj.jar')
 sources=sorted(f for module in MODULES for f in (source/('jetty-'+module+'/src/main/java')).rglob('*.java'))
 args=['-8','-proc:none','-bootclasspath',str(boot[0]),'-cp',os.pathsep.join(map(str,jars)),'-d',str(classes)]+list(map(str,sources))
 # An argument file avoids command-length limits. Paths are controlled workspace paths.
 argfile=work/'compile.args';argfile.write_text('\n'.join('"'+arg.replace('\\','\\\\').replace('"','\\"')+'"' for arg in args)+'\n')
 with (work/'compile.log').open('w') as log:subprocess.run(['java','-jar',str(inputs/'ecj.jar'),'@'+str(argfile)],stdout=log,stderr=subprocess.STDOUT,check=True)
 records=[];maven=work/'maven/org/eclipse/jetty'
 for module in MODULES:
  name='jetty-'+module;filename=name+'-'+version+'.jar';directory=maven/name/version;directory.mkdir(parents=True,exist_ok=True);dest=directory/filename;entries={}
  for f in (source/(name+'/src/main/java')).rglob('*.java'):
   if str(f.relative_to(source)) in lock.get('api23_excluded_optional_sources',[]):continue
   text=f.read_text();package=text.split('package ',1)[1].split(';',1)[0].strip().replace('.','/')
   for compiled in [classes/package/(f.stem+'.class'),*(classes/package).glob(f.stem+'$*.class')]:
    if compiled.is_file():entries[str(compiled.relative_to(classes))]=compiled.read_bytes()
  with zipfile.ZipFile(inputs/filename) as archive:
   for path in archive.namelist():
    if not path.endswith('/') and not path.endswith('.class') and not path.upper().endswith(('.SF','.RSA','.DSA')):entries[path]=archive.read(path)
  entries['META-INF/SUPERNOVA-BACKPORT/'+name+'.json']=(json.dumps({'upstream_coordinate':'org.eclipse.jetty:'+name+':'+version,'source_commit':lock['source_commit'],'patch_sha256':sha(patch),'android_api_floor':23,'excluded_optional_sources':lock.get('api23_excluded_optional_sources',[])},sort_keys=True)+'\n').encode()
  with zipfile.ZipFile(dest,'w',zipfile.ZIP_DEFLATED) as archive:
   for path,data in sorted(entries.items()):
    info=zipfile.ZipInfo(path,(2026,10,28,0,0,0));info.compress_type=zipfile.ZIP_DEFLATED;info.external_attr=0o644<<16;archive.writestr(info,data)
  shutil.copyfile(inputs/(name+'-'+version+'.pom'),directory/(name+'-'+version+'.pom'))
  records.append({'module':name,'sha256':sha(dest),'classes':sum(path.endswith('.class') for path in entries),'bytes':dest.stat().st_size})
 (work/'evidence.json').write_text(json.dumps({'schema':1,'source_commit':lock['source_commit'],'patch_sha256':sha(patch),'libraries':records,'lock_sha256':sha(here/'jetty-supernova146.lock.json'),'api23_excluded_optional_sources':lock.get('api23_excluded_optional_sources',[])},indent=2)+'\n')
 print('Built seven Jetty modules from exact pinned source and patch; Android runtime validation is separate.')
if __name__=='__main__':main()
