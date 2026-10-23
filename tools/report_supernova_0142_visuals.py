#!/usr/bin/env python3
"""Index existing native-view screenshots and preserved concept references; never fabricate a pass."""
from pathlib import Path
import argparse,hashlib,html,json,struct

parser=argparse.ArgumentParser(description=__doc__)
parser.add_argument('--captures',type=Path,required=True)
parser.add_argument('--source',required=True)
args=parser.parse_args()
root=Path(__file__).resolve().parents[1]
output=root/'docs/0.142/visual-evidence';(output/'network').mkdir(parents=True,exist_ok=True)
shots=[]
for file in sorted(args.captures.glob('0142-shell-*.png')):
 data=file.read_bytes()
 if data[:8]!=b'\x89PNG\r\n\x1a\n':raise ValueError('Invalid PNG')
 width,height=struct.unpack('>II',data[16:24])
 if (width,height)!=(1920,1080):raise ValueError('Unexpected screenshot viewport')
 target=output/'network'/file.name;target.write_bytes(data)
 shots.append(dict(file='network/'+file.name,width=width,height=height,sha256=hashlib.sha256(data).hexdigest()))
references=[('R1','2C2FCA2A-7A91-487A-8837-DD106F93AAD2.png','Monochrome Overview'),('R2','75E72DD0-4878-4FF8-A4EE-8448F228A48E.png','WebDAV storyboard'),('R3','52D674B6-1502-4657-A583-551556A51FE5.png','Put.io storyboard')]
index=dict(application_source=args.source,renderer='Native Android views / controlled fixtures',physical_shield_verified=False,visual_acceptance='PENDING',captures=shots,references=[])
for ref,name,title in references:
 file=root/'docs/design/assets/network-files'/name;data=file.read_bytes();index['references'].append(dict(id=ref,file='../../design/assets/network-files/'+name,title=title,bytes=len(data),sha256=hashlib.sha256(data).hexdigest(),git_blob_sha=hashlib.sha1(b'blob '+str(len(data)).encode()+b'\0'+data).hexdigest()))
(output/'index.json').write_text(json.dumps(index,indent=2)+'\n')
parts=['<!doctype html><meta charset="utf-8"><title>Supernova 0.142 visual evidence</title><style>body{background:#09111a;color:#fff;font:16px sans-serif;margin:28px}a{color:#9bd2f5}.pair{display:grid;grid-template-columns:1fr 1fr;gap:20px}img{width:100%;height:auto}figure{margin:12px 0;padding:12px;border:1px solid #344350}figcaption{margin-bottom:10px;color:#c2c9d0}button{padding:8px;margin:8px;color:inherit;background:#182331;border:1px solid #7394ab}.overlay{position:relative}.overlay img+img{position:absolute;top:0;left:0;opacity:.5}.note{max-width:1000px;line-height:1.6}</style><h1>Supernova 0.142 visual evidence</h1><p class="note">Actual 1920×1080 native Android view renders with controlled fixture data. Physical Shield acceptance and the full live-state atlas are pending. References are owner-selected concept art; no pixel-perfect pass is claimed. Existing global navigation/font and explicit blueprint geometry supersede invented mock-up details.</p>']
pairs=[('R1','0142-shell-overview.png'),('R2','0142-shell-webdav-form.png'),('R3','0142-shell-cloud_services.png')]
for ref,shot in pairs:
 r=next(r for r in index['references'] if r['id']==ref)
 parts.append('<h2>'+ref+' comparison</h2><div class="pair"><figure><figcaption>Original reference · '+html.escape(r['title'])+'</figcaption><img src="'+r['file']+'"></figure><figure><figcaption>Implemented native-view fixture · acceptance pending</figcaption><img src="network/'+shot+'"></figure></div>')
r1=index['references'][0]
parts.append('<h2>R1 overlay comparison</h2><p>Uniform contain scaling only; concept art is not pixel ground truth. Global navigation and actual dataset differ as documented.</p><label>Reference opacity <input type="range" min="0" max="1" step="0.05" value="0.5" oninput="document.getElementById(&quot;r1-reference&quot;).style.opacity=this.value"></label><div class="overlay" style="aspect-ratio:16/9"><img style="position:absolute;inset:0;height:100%;object-fit:contain" src="network/0142-shell-overview.png"><img id="r1-reference" style="height:100%;object-fit:contain" src="'+r1['file']+'"></div>')
parts.append('<h2>All captured sections</h2><div class="pair">')
for shot in shots:parts.append('<figure><figcaption>'+html.escape(shot['file'])+'</figcaption><img src="'+shot['file']+'"></figure>')
parts.append('</div><h2>Edit Library</h2><p>Same fixture, original editor source versus corrected editor. Baseline background-channel interference:25; corrected:1. Software measurement, not physical TV luminance certification.</p>')
for step in range(5):parts.append('<h3>Step '+str(step+1)+'</h3><div class="pair"><figure><figcaption>Original 0.141 editor source</figcaption><img src="editor-before/0142-editor-step-'+str(step)+'.png"></figure><figure><figcaption>Corrected editor</figcaption><img src="editor-after/0142-editor-step-'+str(step)+'.png"></figure></div>')
parts.append('<p><a href="README.md">Method and limitations</a> · <a href="index.json">Hashes and source record</a></p>')
(output/'index.html').write_text('\n'.join(parts)+'\n')
print(json.dumps(dict(captures=len(shots),references=len(references),physical_verified=False)))
