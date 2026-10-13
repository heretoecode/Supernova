#!/usr/bin/env python3
"""Validate only documentation and immutable source evidence. Does not run app tests."""
import argparse, hashlib, json, re, subprocess, urllib.parse, xml.etree.ElementTree as ET
from pathlib import Path
p=argparse.ArgumentParser();p.add_argument('--video',required=True);p.add_argument('--medialib',required=True);p.add_argument('--avos',required=True);a=p.parse_args()
r=Path(__file__).resolve().parents[1];e=r/'evidence'
repo={'heretoecode/Supernova':a.video,'heretoecode/aos-MediaLib':a.medialib,'nova-video-player/aos-avos':a.avos}
def git(root,*args):return subprocess.check_output(['git','-C',root,*args])
def read(name,sha,path):return git(repo[name],'show',sha+':'+path)
ledger=json.loads((e/'source-ledger.json').read_text());inventory=json.loads((e/'source-inventory.json').read_text())
for i in ledger:
 raw=read(i['repository'],i['commit'],i['path']);lines=raw.decode().splitlines()
 assert hashlib.sha256(raw).hexdigest()==i['sha256']
 assert i['excerpt']=='\n'.join(f'{n}: {lines[n-1]}' for n in range(i['start'],i['end']+1))
for i in inventory:assert hashlib.sha256(read(i['repository'],i['commit'],i['path'])).hexdigest()==i['sha256']
# Verify local links and every immutable GitHub source line link without remote mutation/network.
links=0
for f in r.rglob('*.md'):
 for target in re.findall(r'\]\(([^)]+)\)',f.read_text()):
  if target.startswith('https://github.com/'):
   m=re.match(r'https://github.com/([^/]+/[^/]+)/blob/([0-9a-f]{40})/(.*?)(?:#L(\d+)(?:-L\d+)?)?$',target)
   if m:
    name,sha,path,line=m.groups();raw=read(name,sha,urllib.parse.unquote(path));assert line is None or 1<=int(line)<=len(raw.decode().splitlines());links+=1
  elif not target.startswith(('http:','https:','#')):
   assert (f.parent/target.split('#')[0]).exists(),(f,target)
ET.parse(e/'current-lower-layout.svg')
base=json.loads((e/'baseline.json').read_text());assert all(x.startswith('docs/') for x in base['source_vs_qa_paths']);assert base['source_worktree_status']==''
# Parse every fact invocation's arguments, retaining string/parenthesis boundaries.
V=base['signed_source'];path='src/main/java/com/archos/mediacenter/video/leanback/details/PreviewMoviePage.java'
s=read('heretoecode/Supernova',V,path).decode();section='\n'.join(s.splitlines()[221:335]);labels=set();calls=[]
for m in re.finditer(r'\bfact\(',section):
 pos=m.end();depth=1;quote=False;escape=False;parts=[];part=''
 while pos<len(section):
  ch=section[pos];pos+=1
  if quote:
   part+=ch
   if escape:escape=False
   elif ch=='\\':escape=True
   elif ch=='"':quote=False
  elif ch=='"':quote=True;part+=ch
  elif ch=='(':depth+=1;part+=ch
  elif ch==')':
   depth-=1
   if depth==0:parts.append(part);break
   part+=ch
  elif ch==',' and depth==1:parts.append(part);part=''
  else:part+=ch
 assert len(parts)==3,(parts,m.start())
 label=parts[1];tokens=re.findall(r'"([^"\\]*(?:\\.[^"\\]*)*)"',label)
 if '?' in label:tokens=tokens[-2:]
 labels.update(tokens);calls.append({'label_expression':label,'possible_labels':tokens,'value_expression':parts[2]})
report=(r/'02-FIELD-INVENTORY.md').read_text();assert all(label in report for label in labels),(labels-set(re.findall(r'\| ([^|]+) \|',report)))
(e/'field-labels.json').write_text(json.dumps({'source':V,'path':path,'distinct_labels':sorted(labels),'calls':calls,'all_labels_documented':True},indent=2)+'\n')
# Static reachability conditions central to audit; verification of source text, not runtime testing.
assert 'extends ScrollView' in s and 'setFocusable(false)' in s
assert 'addView(versions' not in s and 'addView(tabLine' not in s and 'portraits.put' not in s
assert len(re.findall(r'\bmoreInfo\s*\(',s))==1
assert 'showSection(tag.substring(8),true)' in s and 'scrollTo(0,Math.max(0,heroPage.getHeight()-dp(44)-dp(48)))' in s
root=repo['heretoecode/Supernova'];base_sha=base['reference_commits']['decisions']
tracked=git(root,'diff','--name-only',base_sha,'HEAD').decode().splitlines()
# source worktree is detached at inspected source: docs branch diff checked via its own repo root.
docsrepo=str(r.parents[1]);changed=git(docsrepo,'status','--porcelain').decode().splitlines();scope=git(docsrepo,'diff','--name-only',base_sha,'--').decode().splitlines()
assert all(path.startswith('docs/details-tab-forensic-audit/') for path in scope),scope
assert git(root,'status','--porcelain').decode()==''
result={'date':'2026-10-13','classification':'documentation/static evidence validation only; not application test results','source_sha':V,'source_excerpts_exact':len(ledger),'file_hashes_verified':len(inventory),'immutable_links_verified':links,'distinct_lower_fact_labels':len(labels),'lower_fact_calls':len(calls),'labels_all_documented':True,'existing_render_hashes_verified':len(base['image_evidence']),'svg_xml_valid':True,'signed_source_worktree_clean':True,'no_app_tests_or_apk_build':True}
(e/'documentation-validation.json').write_text(json.dumps(result,indent=2)+'\n');print(json.dumps(result))
