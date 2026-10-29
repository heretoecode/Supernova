#!/usr/bin/env python3
"""Preserve exact resolved Gradle archives/POMs for independent SBOM review."""
import argparse,json,shutil,hashlib
from pathlib import Path
p=argparse.ArgumentParser(description=__doc__);p.add_argument('--video',type=Path,required=True);a=p.parse_args();root=a.video.resolve();inventory=root/'build/foundation/runtime-artifacts.json';target=root/'build/supernova146-runtime-review';target.mkdir(exist_ok=True)
if inventory.exists():
 records=[]
 for artifact in json.loads(inventory.read_text()):
  source=Path(artifact['file']);group,module,version=artifact['coordinate'].split(':');directory=target/group/module/version;directory.mkdir(parents=True,exist_ok=True);archive=directory/'archive'/source.name;archive.parent.mkdir(exist_ok=True);shutil.copyfile(source,archive)
  poms=list(source.parent.parent.glob('*/*.pom'))
  if not poms:poms=list(source.parent.glob('*.pom'))
  if len(poms)!=1:raise ValueError('Expected exact resolved POM: '+artifact['coordinate'])
  pom=directory/'pom'/poms[0].name;pom.parent.mkdir(exist_ok=True);shutil.copyfile(poms[0],pom)
  records.append(dict(artifact,file=str(archive.relative_to(target)),sha256=hashlib.sha256(archive.read_bytes()).hexdigest(),pom_sha256=hashlib.sha256(pom.read_bytes()).hexdigest()))
 (target/'inventory.json').write_text(json.dumps(records,indent=2)+'\n');print('Preserved exact resolved runtime artifacts/POMs:',len(records))
else:print('No runtime inventory yet; prior build failed before resolution')
