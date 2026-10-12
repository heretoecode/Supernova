#!/usr/bin/env python3
"""Standalone searchable documentation report. No hosting/application changes."""
import base64,csv,html,json,re
from pathlib import Path
ROOT=Path(__file__).resolve().parents[1];e=html.escape
receipts=json.loads((ROOT/'evidence/drive-upload-receipts.json').read_text())
urls={}
for r in receipts:
 file=r.get('file','');prefix=str(ROOT)+'/'
 if file.startswith(prefix):file=file[len(prefix):]
 urls[file]=r['url']
def linked_markdown(file):
 text=file.read_text();parts=[];at=0
 for m in re.finditer(r'\[([^\]]+)\]\(([^)]+)\)',text):
  parts.append(e(text[at:m.start()]));label,url=m.groups()
  if not url.startswith('http'):
   resolved=(file.parent/url.split('#')[0]).resolve()
   try:key=resolved.relative_to(ROOT).as_posix();url=urls.get(key,'https://github.com/heretoecode/Supernova/blob/docs/ui-reference-atlas-2026-10-12/docs/ui-reference-atlas/'+key)
   except ValueError:url='https://github.com/heretoecode/Supernova/blob/docs/ui-reference-atlas-2026-10-12/'+resolved.relative_to(ROOT.parents[1]).as_posix()
  parts.append('<a href="'+e(url)+'">'+e(label)+'</a>');at=m.end()
 parts.append(e(text[at:]));return ''.join(parts)
counts=json.loads((ROOT/'evidence/inventory-counts.json').read_text())
sites=list(csv.DictReader((ROOT/'evidence/display-sites.tsv').open(),delimiter='\t'))
focus=list(csv.DictReader((ROOT/'evidence/focus-branches.tsv').open(),delimiter='\t'))
prefs=list(csv.DictReader((ROOT/'evidence/preferences-effective.tsv').open(),delimiter='\t'))
screens=list(csv.DictReader((ROOT/'evidence/manifest-screens.tsv').open(),delimiter='\t'))
siblings=list(csv.DictReader((ROOT/'evidence/sibling-display-sites.tsv').open(),delimiter='\t'))
dataset=[]
for kind,rows in [('Display source',sites),('Focus branch',focus),('Preference',prefs),('Manifest screen',screens),('Sibling display',siblings)]:
 for r in rows:dataset.append({'kind':kind,'area':r.get('area',r.get('effective_category_or_move',r.get('module','Conditional'))),'id':r.get('id',r.get('stable_id','')),'source':r.get('source',r.get('declaration','')),'record':r})
images=json.loads((ROOT/'evidence/image-manifest.json').read_text());gallery=''
for r in images:
 picture=''
 if r.get('file'):
  picture='<img loading="lazy" alt="'+e(r['id']+' '+r['state'])+'" src="data:image/png;base64,'+base64.b64encode((ROOT/r['file']).read_bytes()).decode()+'">'
 gallery+='<article class="image" data-search="'+e(r['id']+' '+r['area']+' '+r['state']+' '+r['category']).lower()+'"><a href="'+e(r.get('file_link',''))+'">'+picture+'</a><h3>'+e(r['id'])+'</h3><p><b>'+e(r['category'])+'</b><br>'+e(r['state'])+'<br>'+e(str(r['resolution']))+' · '+e(r['source_commit'][:12])+'</p><details><summary>Fixture / source / limitations</summary><pre>'+e(json.dumps({k:r[k] for k in ['fixture','source_references','known_deviations']},indent=2,ensure_ascii=False))+'</pre></details></article>'
reports=''
for file in list(sorted((ROOT/'areas').glob('*.md')))+[ROOT/x for x in ['CUSTOMISE_HOME_VISUAL_PACKAGE.md','LIBRARY_STATISTICS_VISUAL_PACKAGE.md','ON_DEMAND_REFERENCES.md','ACCEPTANCE_AND_GAPS.md']]:
 reports+='<details class="report"><summary>'+e(file.read_text().splitlines()[0].lstrip('# '))+'</summary><pre>'+linked_markdown(file)+'</pre></details>'
css='''body{font-family:Arial,sans-serif;margin:0;background:#f4f6f8;color:#172431}header,main{max-width:1400px;margin:auto;padding:24px}header{background:#172431;color:white}h1{margin-top:0}a{color:#176ea3}header a{color:#93d8ff}input{width:100%;padding:14px;font-size:16px;border:1px solid #879baa;border-radius:6px}nav{display:flex;gap:18px;margin:20px 0;flex-wrap:wrap}.note{background:#fff4d7;padding:16px;border-left:4px solid #bc8800}.report{background:white;margin:12px 0;padding:16px;border-radius:6px}summary{font-size:18px;font-weight:bold;cursor:pointer}pre{white-space:pre-wrap;overflow-wrap:anywhere;font:14px/1.55 monospace}.gallery{display:grid;grid-template-columns:repeat(auto-fit,minmax(300px,1fr));gap:18px}.image{background:white;padding:14px;border-radius:6px}.image img{width:100%;height:240px;object-fit:contain;background:#070d14}.image h3{font-size:14px}.image p{font-size:13px;line-height:1.45}.image summary{font-size:13px}.result{padding:14px;background:white;margin:10px 0;border-left:4px solid #7b9eac}.result pre{font-size:12px}button{padding:10px 18px;margin:8px 0}.hidden{display:none}'''
data=json.dumps(dataset,ensure_ascii=False).replace('<','\\u003c')
js='''const data=DATA;const box=document.getElementById('query'),results=document.getElementById('results'),count=document.getElementById('count');let cap=60;function filter(){let q=box.value.toLowerCase().trim();document.querySelectorAll('.image').forEach(x=>x.classList.toggle('hidden',q&&!x.dataset.search.includes(q)));results.replaceChildren();if(!q){count.textContent=data.length+' searchable source/focus/preference/manifest records. Enter a label, class, key or ID.';return;}let hits=data.filter(x=>JSON.stringify(x).toLowerCase().includes(q));count.textContent=hits.length+' matches (showing '+Math.min(cap,hits.length)+')';for(let x of hits.slice(0,cap)){let item=document.createElement('article');item.className='result';let link=document.createElement('a');link.href=x.source;link.textContent=x.id+' · '+x.kind+' · '+x.area;item.append(link);let pre=document.createElement('pre');pre.textContent=JSON.stringify(x.record,null,2);item.append(pre);results.append(item);}}box.addEventListener('input',()=>{cap=60;filter()});document.getElementById('more').onclick=()=>{cap+=100;filter()};filter();'''.replace('DATA',data)
doc='''<!doctype html><html lang="en"><meta charset="utf-8"><title>Supernova UI Reference Atlas — source and visuals</title><style><!--ATLAS_CSS--></style><header><h1>Supernova UI Reference Atlas</h1><p>Read-only documentation · signed0.138 b3504c2c4b1725f3070416d14b63e53ae1db3d74 · 12 October2026</p><p>78 declared screens · 10 reviewed area inventories · 1,782 display sites · 437 focus branches · 51 image records</p><nav><a href="https://github.com/heretoecode/Supernova/pull/16">GitHub draft #16</a><a href="#reports">Area reports</a><a href="#source">Search source/options</a><a href="#images">Visual references</a><a href="https://drive.google.com/drive/folders/1kzYXFIvuxs5eGrvGD-c-2cgHBKpPkLGc">Drive atlas</a><a href="https://drive.google.com/drive/folders/1AJJ4_S5FS0u87Hk8mh6SathtWiGHMLKN">Original Home folder</a></nav></header><main><p class="note"><b>Acceptance remains qualified.</b> Source catalogue and priority packages delivered; conditional every-option runtime reachability and physical Shield proof remain incomplete. A: no Shield screenshots; B: 6 historical +10 unchanged native CI references; C: 35 visibly labelled approximations; D: no concepts. Synthetic values are not owner library data.</p><h2 id="source">Search labels, options, owner classes and stable IDs</h2><input id="query" aria-label="Search atlas" placeholder="Try Maximum items, Edit Library, Network, OPT-, LibraryPageHeader"><p id="count"></p><div id="results"></div><button id="more">Show more matches</button><h2 id="reports">Reviewed reports</h2><!--ATLAS_REPORTS--><h2 id="images">Visual references with provenance</h2><p>Native B pixels unchanged. C carries its own visible approximation banner. Historical references link their preserved Drive originals. Select an image to open the observed Drive file.</p><div class="gallery"><!--ATLAS_GALLERY--></div><p>Use the on-demand protocol and existing surface IDs for additional packages; no app code, APK build or release authority is implied.</p></main><script><!--ATLAS_JS--></script></html>'''.replace('<!--ATLAS_CSS-->',css).replace('<!--ATLAS_REPORTS-->',reports).replace('<!--ATLAS_GALLERY-->',gallery).replace('<!--ATLAS_JS-->',js)
(ROOT/'MASTER_INDEX.html').write_text(doc);print('Standalone master HTML:',len(doc.encode()),'bytes;',len(dataset),'searchable records;',len(images),'image records')
