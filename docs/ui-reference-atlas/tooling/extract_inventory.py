#!/usr/bin/env python3
"""Read-only source/resource catalogue. Does not certify runtime reachability."""
import argparse,csv,hashlib,html,json,re,shutil,xml.etree.ElementTree as ET
from pathlib import Path
p=argparse.ArgumentParser();p.add_argument('--source',required=True);p.add_argument('--output',required=True);p.add_argument('--medialib');p.add_argument('--filecore');a=p.parse_args()
src=Path(a.source);out=Path(a.output);out.mkdir(parents=True,exist_ok=True)
SHA='b3504c2c4b1725f3070416d14b63e53ae1db3d74'
BASE='https://github.com/heretoecode/Supernova/blob/'+SHA+'/'
def area(path):
 s=str(path)
 if any(x in s for x in ['PreviewHome','PreviewFeatured','HomePlaceholder','PreviewSeries','MainFragment','TopNavigation','EntryActivity','Startup']):return '01-home-navigation'
 if any(x in s for x in ['CustomLibrary','LibraryPageHeader','PreviewLibrary','PreviewPages','PreviewGenres','collections/']):return '02-libraries'
 if any(x in s for x in ['leanback/details','leanback/tvshow','PreviewRemoteDetails','PreviewStreamingActions']):return '03-details'
 if any(x in s for x in ['search/','Keyboard','TextInput','MatchSearch']):return '04-search-keyboard'
 if any(x in s for x in ['filebrowsing/','network/','PreviewNetwork','PreviewSources','LibraryHealth','putio/']):return '05-network-health'
 if any(x in s for x in ['settings/','foundation/FoundationAbout','FoundationTechnical','BundledNotice','PreviewAccent']):return '06-settings-about'
 if 'diagnostics/' in s or 'Backup' in s or 'Restore' in s:return '07-diagnostics-backup'
 if 'player/' in s or 'Subtitle' in s:return '08-playback-subtitles'
 if 'PreviewDialog' in s or 'SharedThreePanel' in s or 'Focus' in s or 'TvLayout' in s or 'TvButton' in s:return '09-shared-focus'
 return '10-inherited-conditional'
strings={};arrays={};resource_rows=[]
external_rows=[];external_sites=[]
for module,root,repo,commit in [('MediaLib',a.medialib,'heretoecode/aos-MediaLib','5758074049bc153ac4967b4ecd21886469270076'),('FileCore',a.filecore,'nova-video-player/aos-FileCoreLibrary','c4b760c55102c72d45f68b0d16999fc1f4f013a7')]:
 if not root:continue
 root=Path(root);base='https://github.com/'+repo+'/blob/'+commit+'/'
 for file in sorted((root/'res/values').glob('*.xml')):
  try:r=ET.parse(file).getroot()
  except ET.ParseError:continue
  for node in r:
   name=node.get('name','');value=''.join(node.itertext()).strip()
   if re.search(r'(api_key|client_secret|access_token|oauth_secret)$',name):value='[non-UI configuration value omitted]'
   if node.tag=='string':strings.setdefault(name,value)
   elif node.tag in ['string-array','array']:arrays.setdefault(name,[''.join(x.itertext()).strip() for x in node])
   else:continue
   line=next((i+1 for i,x in enumerate(file.read_text().splitlines()) if 'name="'+name+'"' in x),1)
   external_rows.append({'id':module+'-RES-'+name,'module':module,'key':name,'text_or_order':arrays.get(name,value),'source':base+file.relative_to(root).as_posix()+'#L'+str(line),'evidence':'Pinned sibling resource; effective preparation/overlays not inflated'})
 for file in sorted((root/'src').rglob('*.java')):
  for i,line in enumerate(file.read_text().splitlines(),1):
   if re.search(r'\.(setText|setTitle|setHint|setMessage|setEntries|setContentDescription)\(|new (AlertDialog|TVMenuItem)|String\[\].*\{',line) and not line.strip().startswith(('*','//')):
    external_sites.append({'id':module+'-SITE-'+hashlib.sha1((file.relative_to(root).as_posix()+str(i)).encode()).hexdigest()[:12],'module':module,'owner':file.stem,'source_expression':line.strip(),'source':base+file.relative_to(root).as_posix()+'#L'+str(i),'evidence':'Pinned sibling display site; reachability/effective overlay/runtime unknown'})
for file in sorted((src/'res/values').glob('*.xml')):
 try:root=ET.parse(file).getroot()
 except ET.ParseError:continue
 for node in root:
  name=node.get('name','');value=''.join(node.itertext()).strip()
  if re.search(r'(api_key|client_secret|access_token|oauth_secret)$',name):value='[non-UI configuration value omitted]'

  if node.tag=='string':strings[name]=value
  elif node.tag in ['string-array','integer-array','array']:arrays[name]=[''.join(x.itertext()).strip() for x in node]
  else:continue
  rel=file.relative_to(src).as_posix();line=next((i+1 for i,x in enumerate(file.read_text().splitlines()) if 'name="'+name+'"' in x),1)
  resource_rows.append({'id':'RES-'+name,'type':node.tag,'key':name,'text_or_order':arrays.get(name,value),'source':BASE+rel+'#L'+str(line)})
def resolve(v):
 if not v:return ''
 if v.startswith('@string/'):return strings.get(v[8:],v+' [external/unresolved]')
 if v.startswith('@array/'):return arrays.get(v[7:],v+' [external/unresolved]')
 return v
def tsv(name,rows):
 if not rows:return
 with (out/name).open('w') as f:
  w=csv.DictWriter(f,fieldnames=list(rows[0]),delimiter='\t');w.writeheader()
  for r in rows:w.writerow({k:json.dumps(v,ensure_ascii=False) if isinstance(v,(list,dict)) else v for k,v in r.items()})
tsv('english-resources.tsv',resource_rows)
tsv('sibling-resources.tsv',external_rows);tsv('sibling-display-sites.tsv',external_sites)
prefs=[];texts=[];manifest=[]
ns='{http://schemas.android.com/apk/res/android}';ap='{http://schemas.android.com/apk/res-auto}'
for file in sorted(list((src/'res/xml').glob('*.xml'))+list((src/'res/layout').glob('*.xml'))):
 try:root=ET.parse(file).getroot()
 except ET.ParseError:continue
 rel=file.relative_to(src).as_posix();lines=file.read_text().splitlines()
 def walk(node,parent='',sequence=[0]):
  sequence[0]+=1;attrs={k.split('}')[-1]:v for k,v in node.attrib.items()};key=resolve(attrs.get('key',''));title=attrs.get('title','')
  locator='android:key="'+attrs.get('key','')+'"' if key else 'android:text="'+attrs.get('text','')+'"'
  line=next((i+1 for i,x in enumerate(lines) if locator in x),1)
  ref=BASE+rel+'#L'+str(line)
  if key or 'Preference' in node.tag:
   prefs.append({'id':'PREF-'+key+'-'+hashlib.sha1((rel+str(sequence[0])).encode()).hexdigest()[:6],'xml':rel,'xml_order':sequence[0],'parent':parent,'class':node.tag,'key':key,'title':resolve(title),'summary':resolve(attrs.get('summary','')),'choices_in_order':resolve(attrs.get('entries','')),'stored_values':resolve(attrs.get('entryValues','')),'declared_default':resolve(attrs.get('defaultValue','')),'dependency':attrs.get('dependency',''),'visibility_status':'XML declaration; PreviewSettings/device/provider overrides require effective map','source':ref})
  if any(k in attrs for k in ['text','hint','contentDescription']):
   texts.append({'id':'XML-'+hashlib.sha1((rel+str(sequence[0])).encode()).hexdigest()[:10],'xml':rel,'order':sequence[0],'widget':node.tag,'view_id':attrs.get('id',''),'text':resolve(attrs.get('text','')),'hint':resolve(attrs.get('hint','')),'content_description':resolve(attrs.get('contentDescription','')),'visibility':attrs.get('visibility','default visible if inflated'),'attributes':attrs,'source':ref})
  for child in node:walk(child,key or parent,sequence)
 walk(root,sequence=[0])
tsv('preferences-declared.tsv',prefs);tsv('layout-text.tsv',texts)
root=ET.parse(src/'AndroidManifest.xml').getroot()
for i,n in enumerate(root.findall('.//activity')+root.findall('.//activity-alias')):
 attrs={k.split('}')[-1]:v for k,v in n.attrib.items()};intent=[]
 for intentfilter in n.findall('intent-filter'):intent.append([dict(x.attrib) for x in intentfilter])
 manifest.append({'id':'SCREEN-'+hashlib.sha1(attrs.get('name','').encode()).hexdigest()[:10],'component':attrs.get('name',''),'kind':n.tag,'label':resolve(attrs.get('label','')),'exported':attrs.get('exported','implicit; inspect filters/API'),'target':attrs.get('targetActivity',''),'intents':intent,'source':BASE+'AndroidManifest.xml','runtime_status':'Declared entrypoint; primary TV/legacy/external route described in area inventory, runtime unknown'})
tsv('manifest-screens.tsv',manifest)
sites=[];keys=[];files=[];byarea={}
# All Java production sources, including inherited/classic/player utilities. Each
# display site carries the whole physical source line; no label hierarchy is inferred.
display=re.compile(r'\.(?:setText|setTitle|setSummary|setHint|setEntries|setEntryValues|setContentDescription|setDefaultValue|setMessage|addAction|addPreference|setVisible|setEnabled)\s*\(|\b(?:row|action|button|control|heading|centreAction|addAction|location|footerAction|facet|fact|text)\s*\(|PreviewDialog\.(?:choose|read|message|review|confirm)|new (?:Action|TVMenuItem|TVCardView)|String\[\].*\{|labels\.add|NAMES\s*=|LABELS\s*=|ROWS\s*=')
for file in sorted((src/'src/main/java').rglob('*.java')):
 rel=file.relative_to(src).as_posix();lines=file.read_text().splitlines();cat=area(rel);method='class scope';count=0
 for i,line in enumerate(lines,1):
  m=re.search(r'(?:public|private|protected|static)\s+(?:static\s+|final\s+|synchronized\s+)*(?:[\w<>\[\].?]+\s+)(\w+)\s*\([^;]*\)\s*(?:throws [\w., ]+)?\{',line)
  if m:method=m.group(1)
  if display.search(line) and not line.strip().startswith(('*','//')):
   literals=re.findall(r'"(?:\\.|[^"\\])*"',line);refs=re.findall(r'R\.string\.(\w+)',line)
   row={'id':'SITE-'+hashlib.sha1((rel+':'+str(i)).encode()).hexdigest()[:12],'area':cat,'owner':file.stem,'method_hint':method,'line':i,'literal_tokens_in_source_order':literals,'resolved_english_resources':{x:strings.get(x,'[external/unresolved]') for x in refs},'source_expression_and_handler_context':line.strip(),'evidence':'VERIFIED source site; visibility/ordering only as written; runtime UNKNOWN','source':BASE+rel+'#L'+str(i)}
   sites.append(row);byarea.setdefault(cat,[]).append(row);count+=1
  if re.search(r'KEYCODE_DPAD_|KEYCODE_BACK|focusSearch\(|requestFocus\(|setNextFocus|OnBackPressed|handleBack|restoreFocus',line) and not line.strip().startswith(('*','//')):
   keys.append({'id':'FOCUS-'+hashlib.sha1((rel+':'+str(i)).encode()).hexdigest()[:12],'area':cat,'owner':file.stem,'method_hint':method,'source_expression':line.strip(),'source':BASE+rel+'#L'+str(i),'evidence':'VERIFIED source branch; runtime outcome UNKNOWN'})
 if count:files.append({'area':cat,'owner':file.stem,'path':rel,'sha256':hashlib.sha256(file.read_bytes()).hexdigest(),'display_sites':count,'source':BASE+rel})
tsv('display-sites.tsv',sites);tsv('focus-branches.tsv',keys);tsv('ui-owners.tsv',files)
for cat,rows in byarea.items():
 name=cat+'-source-options.md';body=['# '+cat+' — exact source expressions','', 'Generated read-only from signed 0.138 source `'+SHA+'`. This appendix is exhaustive for the documented matcher, not a runtime hierarchy. Physical lines can contain several calls and conditions; literal tokens can include non-visible keys. Method hints are syntactic. Area narratives distinguish confirmed surfaces from unresolved reachability. English resource resolution only; external resources are marked.','']
 for r in rows:
  body+=['## '+r['id']+' · '+r['owner']+'.'+r['method_hint'],'', '[Source line '+str(r['line'])+']('+r['source']+')','', '```java',r['source_expression_and_handler_context'],'```','']
  if r['resolved_english_resources']:body+=['Resolved English: `'+json.dumps(r['resolved_english_resources'],ensure_ascii=False)+'`','']
 (out/name).write_text('\n'.join(body))
counts={'source_commit':SHA,'java_files_scanned':len(list((src/'src/main/java').rglob('*.java'))),'ui_owners':len(files),'display_sites':len(sites),'focus_branches':len(keys),'manifest_entries':len(manifest),'preference_declarations':len(prefs),'layout_text_sites':len(texts),'english_resources':len(resource_rows),'per_area':{k:len(v) for k,v in byarea.items()},'limits':['Syntactic display-site extraction is not semantic reachability proof','Android/dependency generated/dynamic labels can require runtime data','English resource set only','Method hints can refer to surrounding rather than lambda method']}
(out/'inventory-counts.json').write_text(json.dumps(counts,indent=2));print(json.dumps(counts,indent=2))
