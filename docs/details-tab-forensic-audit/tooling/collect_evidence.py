#!/usr/bin/env python3
"""Documentation-only evidence extraction via immutable git objects; never builds/edits app."""
import argparse, hashlib, json, re, subprocess
from pathlib import Path
p=argparse.ArgumentParser()
p.add_argument('--video', required=True); p.add_argument('--medialib', required=True);p.add_argument('--avos',required=True)
p.add_argument('--output', default=str(Path(__file__).resolve().parents[1]/'evidence'))
a=p.parse_args();out=Path(a.output);out.mkdir(parents=True,exist_ok=True)
V='b3504c2c4b1725f3070416d14b63e53ae1db3d74'; M='5758074049bc153ac4967b4ecd21886469270076'; A='2cf21c486c7bf244c49b78d2220197a6d75396dd'
repos={'video':(a.video,V,'heretoecode/Supernova'),'medialib':(a.medialib,M,'heretoecode/aos-MediaLib'),'avos':(a.avos,A,'nova-video-player/aos-avos')}
base='src/main/java/com/archos/mediacenter/video/'
def j(path):return base+path+'.java'
def git(root,*args):return subprocess.check_output(['git','-C',root,*args])
def read(repo,path):root,sha,_=repos[repo];return git(root,'show',sha+':'+path)
items=[]
def add(eid,topic,path,start=1,end=None,repo='video'):
    raw=read(repo,path);lines=raw.decode().splitlines();end=end or len(lines)
    assert 1<=start<=end<=len(lines),(eid,path,start,end,len(lines))
    root,sha,name=repos[repo];items.append(dict(id=eid,topic=topic,repository=name,commit=sha,path=path,start=start,end=end,url=f'https://github.com/{name}/blob/{sha}/{path}#L{start}',sha256=hashlib.sha256(raw).hexdigest(),excerpt='\n'.join(f'{n}: {lines[n-1]}' for n in range(start,end+1))))
P=j('leanback/details/PreviewMoviePage')
for eid,topic,s,e in [('E04','View tree and measurement',52,89),('E05','Tabs and D-pad',120,174),('E06','Text and sections',176,215),('E07','Key values and precedence',222,253),('E08','Reception and enriched facts',254,273),('E09','Selected-file technical facts',274,298),('E10','Show/season aggregates',299,318),('E11','Remote fields and summary',319,335),('E12','Panel and fact styling',337,352),('E13','People and TV binding',375,403),('E14','Identity/enrichment',511,542),('E15','Unreachable reader',561,576),('E16','Season rows/source labels',405,429),('E17','Artwork/adapter updates',354,372),('E18','Compact scroll title',91,118),('E19','Release on detach',605,612)]:
    # Allow final range to follow actual method lines; release excerpt resolved below.
    if eid=='E19':s=next(i for i,l in enumerate(read('video',P).decode().splitlines(),1) if 'onDetachedFromWindow()' in l);e=s
    add(eid,topic,P,s,e)
add('E01','Video activity XML','res/layout/androidtv_details_activity.xml')
add('E02','Video preview entry/versions',j('leanback/details/VideoDetailsFragment'),199,229)
add('E03','Show preview entry',j('leanback/tvshow/TvshowFragment'),99,119)
add('E20','Toolbar traversal and selected underline',j('leanback/PreviewToolbar'))
add('E21','Shell navigation and stage',j('leanback/TopNavigation'),47,143)
add('E22','Shell drawing and input',j('leanback/TopNavigation'),154,209)
add('E23','Viewport scaling/insets',j('leanback/TvLayout'))
add('E24','Panel background',j('leanback/PreviewDialog'),181,187)
add('E25','Card/scrim',j('leanback/CinematicCard'))
add('E26','People font',j('leanback/SharedThreePanel'),62,70)
add('E27','Button defaults',j('leanback/TvButton'))
add('E28','Common metadata/format/certificate',j('leanback/SharedMetadata'))
add('E29','Codec/channel formatting',j('leanback/details/PreviewMediaInfo'))
add('E30','Variant choice and safe location',j('leanback/PreviewVariants'))
add('E31','Disk cache/freshness',j('leanback/PreviewMetadataCache'))
add('E32','TMDB gateway/country',j('streaming/StreamingRepository'),60,83)
add('E33','Enrichment package requests/cache',j('leanback/details/PreviewDetailsData'))
add('E34','Facts/date and unused helpers',j('leanback/details/PreviewDetailsFacts'))
add('E35','Native task and movie-only rebind',j('leanback/details/VideoDetailsFragment'),1769,1842)
add('E36','File selection authoritative reload',j('leanback/details/VideoDetailsFragment'),1113,1189)
add('E37','Snapshot worker',j('leanback/details/VideoDetailsFragment'),669,697)
add('E38','Tags callback',j('leanback/details/VideoDetailsFragment'),1861,1920)
add('E39','Show tags/update lifecycle',j('leanback/tvshow/TvshowFragment'),618,669)
add('E40','Show season loaders',j('leanback/tvshow/TvshowFragment'),675,755)
add('E41','Season activity',j('leanback/details/PreviewSeasonActivity'))
add('E42','Remote activity',j('streaming/PreviewRemoteDetailsActivity'))
add('E43','Video DB columns',j('browser/adapters/mappers/VideoCursorMapper'),64,112)
add('E44','Library Entry/Snapshot',j('leanback/PreviewLibraryLoader'),12,108)
add('E45','Native track fields',j('utils/VideoMetadata'),71,207)
add('E46','Native metadata persistence',j('utils/VideoMetadata'),375,412)
add('E47','Metadata retrieve transport',j('info/VideoInfoCommonClass'),87,132)
add('E48','Scan overlay attachment',j('leanback/overlay/Overlay'),86,120)
add('E49','Scan poll/visibility/status',j('leanback/overlay/ScannerAndScraperProgress'),67,None)
add('E50','Library opener restoration',j('leanback/PreviewPages'),65,110)
add('E51','Library opener token',j('leanback/PreviewPages'),248,248)
add('E52','Backdrop rendering',j('leanback/PreviewBackdrop'))
add('E53','Portrait helper unreachable with empty target',j('leanback/details/PreviewPeople'))
add('E54','Video measured/filename getters',j('browser/adapters/object/Video'),118,145)
add('E55','Video fact getters',j('browser/adapters/object/Video'),202,212)
add('E56','Filename and URI',j('browser/adapters/object/Video'),247,290)
add('E57','Calculated format getters',j('browser/adapters/object/Video'),377,391)
add('E58','Movie model',j('browser/adapters/object/Movie'))
add('E59','TV model',j('browser/adapters/object/Tvshow'))
add('E60','Episode model',j('browser/adapters/object/Episode'))
add('E61','Multiple-file DB query',j('info/MultipleVideoLoader'))
add('E62','Native versions modal',j('leanback/PreviewVersionsDialog'))
add('E63','Intent entry',j('leanback/VideoViewClickedListener'),95,182)
add('E64','Foundation preview gate',j('foundation/FoundationUiPolicy'))
add('E65','Official logo selection/cache',j('leanback/OfficialTitleArtwork'))
add('E66','Native activity Back dispatch',j('leanback/details/VideoDetailsActivity'),64,77)
add('E67','Video loader projections and model mapping',j('browser/loader/VideoLoader'))
add('E68','TV activity layout','res/layout/androidtv_tvshow_activity.xml')
add('E69','Nonindexed unknown model values',j('browser/adapters/object/NonIndexedVideo'))
add('E70','Available-offer predicate',j('streaming/StreamingActions'),81,81)
mb='src/com/archos/'
add('M01','Base tag support/insertion ordering',mb+'mediascraper/BaseTags.java',66,136,'medialib')
add('M02','Tag actor/director normalization',mb+'mediascraper/BaseTags.java',356,407,'medialib')
add('M03','Genres/studios support',mb+'mediascraper/VideoTags.java',23,97,'medialib')
add('M04','Movie supported metadata',mb+'mediascraper/MovieTags.java',52,182,'medialib')
add('M05','Show supported metadata',mb+'mediascraper/ShowTags.java',54,134,'medialib')
add('M06','Episode supported metadata',mb+'mediascraper/EpisodeTags.java',50,114,'medialib')
add('M07','Movie relationships unsorted',mb+'mediascraper/TagsFactory.java',844,907,'medialib')
add('M08','Episode relationships',mb+'mediascraper/TagsFactory.java',1006,1048,'medialib')
add('M09','Show relationships',mb+'mediascraper/TagsFactory.java',1136,1200,'medialib')
add('M10','Provider query ordering',mb+'mediaprovider/video/ScraperProvider.java',1147,1156,'medialib')
add('M11','Movie actor view no order',mb+'mediaprovider/video/ScraperTables.java',446,459,'medialib')
add('M12','TMDB local Movie parsing',mb+'mediascraper/themoviedb3/MovieIdParser2.java',65,135,'medialib')
add('M13','TMDB local Show parsing',mb+'mediascraper/themoviedb3/ShowIdParser.java',54,116,'medialib')
add('N01','Native retriever bitrate/fps units','Source/avos_mr.c',238,252,'avos')
add('N02','Native player bitrate/fps units','Source/avos_mp.c',281,295,'avos')
add('N03','FFmpeg bitrate to bytes/sec','Source/stream_parser_ffmpeg.c',498,504,'avos')
add('N04','Effective native HDR additions','.github/build/avos-supernova135.patch',1,40)
add('N05','Effective native enum additions','.github/build/medialib-supernova135.patch',226,257)
# File inventory includes each complete inspected file; excerpts remain bounded.
unique={}
for item in items:
    key=(item['repository'],item['commit'],item['path']);unique[key]={k:item[k] for k in ('repository','commit','path','sha256')}
tests=[]
testroot='src/test/java/com/archos/mediacenter/video/'
for path in git(a.video,'ls-tree','-r','--name-only',V,'--','src/test/java').decode().splitlines():
    raw=read('video',path).decode()
    if any(x in raw for x in ('PreviewMoviePage','PreviewDetailsFacts','PreviewDetailsData','PreviewVariants','PreviewMediaInfo','SharedMetadata')) or path.endswith(('TopNavigationTest.java','Preview417NavigationTest.java','PreviewMetadataCacheTest.java','PreviewScanProgressTest.java','PreviewScanLifecycleTest.java')):
        methods=[]
        for match in re.finditer(r'@(?:org\.junit\.)?Test(?:\s*\([^)]*\))?(?:(?!@Test)[\s\S]){0,250}?public\s+void\s+(\w+)\s*\(',raw):
            line=raw.count('\n',0,match.start())+1;methods.append({'method':match.group(1),'line':line,'url':f'https://github.com/heretoecode/Supernova/blob/{V}/{path}#L{line}'})
        tests.append({'path':path,'sha256':hashlib.sha256(raw.encode()).hexdigest(),'methods':methods,'execution':'not run by this investigation'})
        unique[('heretoecode/Supernova',V,path)]={'repository':'heretoecode/Supernova','commit':V,'path':path,'sha256':hashlib.sha256(raw.encode()).hexdigest()}
(out/'source-ledger.json').write_text(json.dumps(items,indent=2)+'\n')
(out/'source-inventory.json').write_text(json.dumps(list(unique.values()),indent=2)+'\n')
(out/'test-inventory.json').write_text(json.dumps(tests,indent=2)+'\n')
md=['# Reproducible source evidence ledger','', 'Generated by [collect_evidence.py](../tooling/collect_evidence.py) from immutable Git objects. No app mutation/build/test. S: static source only. Each excerpt is line-numbered with an immutable URL; hashes cover entire file. Markdown presentation removes trailing whitespace; source-ledger.json retains exact source text. Native pins/overlays distinguish original native source from effective additions. Re-run with the three clone paths; no current branch names are substituted.','', '## Source inventory','', '| Repository / commit | File | Whole-file SHA-256 |','|---|---|---|']
for i in unique.values():md.append(f"| {i['repository']} / `{i['commit']}` | [{i['path']}](https://github.com/{i['repository']}/blob/{i['commit']}/{i['path']}) | `{i['sha256']}` |")
md.extend(['','## Direct evidence excerpts',''])
for i in sorted(items,key=lambda x:x['id']):md.extend([f"### {i['id']} — {i['topic']}",'',f"[{i['path']} lines {i['start']}–{i['end']}]({i['url']}); commit `{i['commit']}`.",'','```text','\n'.join(line.rstrip() for line in i['excerpt'].splitlines()),'```',''])
(out/'SOURCE_LEDGER.md').write_text('\n'.join(md))
# Every fact invocation, preserving dynamic label/source expressions and exact order.
rows=[]
for n,line in enumerate(read('video',P).decode().splitlines(),1):
    if n<348 and 'fact(' in line:rows.append({'line':n,'expression':line.strip(),'url':f'https://github.com/heretoecode/Supernova/blob/{V}/{P}#L{n}'})
(out/'field-expressions.json').write_text(json.dumps(rows,indent=2)+'\n')
# Baseline comparisons are reproduced against frozen intake refs.
refs={'decisions':'d75024fed0747453ea2cf774506beb66f22f9773','maintenance':'eac3e504490a92dd9f43252649264657e485982c','atlas':'7b6413a641139efe4d355727980f12f9729d1bbc','main':'75d9f012fa4f65869eef5d972ad4725df504bd9c','qa_receipts':'16a60cb11a5dd20ebe374873db360e32b617d7b0'}
receipt={'date':'2026-10-13','signed_source':V,'native_metadata_pin':A,'medialib_pin':M,'reference_commits':refs,'documentation_branch':'docs/details-tab-forensic-audit-2026-10-13','source_vs_qa_paths':git(a.video,'diff','--name-only',V,refs['qa_receipts']).decode().splitlines(),'main_vs_signed_src_res':git(a.video,'diff','--shortstat',V,refs['main'],'--','src','res').decode().strip(),'source_worktree_status':git(a.video,'status','--porcelain').decode(),'scope':{'app_changes':False,'apk_build':False,'app_tests_executed':False,'physical_ui_inspection':False,'owner_photo_bytes_available':False,'new_artifacts':'documentation/evidence/reconstruction only'},'image_evidence':[]}
images=json.loads(read('video','docs/0.138/ui/RENDER_PROVENANCE.json'))
for im in images:
    if im['file'].startswith('0138-details'):
        actual=hashlib.sha256(read('video','docs/0.138/ui/'+im['file'])).hexdigest();assert actual==im['sha256'];receipt['image_evidence'].append(dict(im,hash_verified=True,classification='B: existing synthetic native fixture, not Shield',new_render=False))
(out/'baseline.json').write_text(json.dumps(receipt,indent=2)+'\n')
print(json.dumps({'source_excerpts':len(items),'inventoried_files':len(unique),'test_files':len(tests),'field_expression_lines':len(rows),'image_hashes_verified':len(receipt['image_evidence'])}))
