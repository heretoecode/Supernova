#!/usr/bin/env python3
"""Category C documentation illustrations, never Android/device captures.
Reads and verifies selected source literals; transcribes selected Android geometry
to HTML/SVG. Chromium rasterisation/font metrics are explicitly approximations.
No application code, tests or build settings are changed.
"""
import argparse,hashlib,html,json,math,re
from pathlib import Path
from PIL import ImageFont
from playwright.sync_api import sync_playwright
p=argparse.ArgumentParser();p.add_argument('--source',required=True);p.add_argument('--output',required=True);a=p.parse_args()
src=Path(a.source);out=Path(a.output).resolve();out.mkdir(parents=True,exist_ok=True)
SHA='b3504c2c4b1725f3070416d14b63e53ae1db3d74';prefix='src/main/java/com/archos/mediacenter/video/leanback/'
sources={n:(src/prefix/(n+'.java')).read_text() for n in ['PreviewHomeRows','PreviewDialog','PreviewGenres','PreviewTextInput','PreviewKeyboard','LibraryPageHeader','PreviewLibrarySummary','PreviewPages','PreviewIcon','PreviewFocusGlow','TvButton','TvLayout','SharedThreePanel']}
for owner,values in {'PreviewHomeRows':['Customise Home','Create New Row','Rename Row','Row sort','No Limit','Select…','Add to Row','Move row to','Enter a whole number from 0 to 2147483647. Zero means No Limit.'],'LibraryPageHeader':['Total Size','Local Storage','Network','30','13','17'],'PreviewKeyboard':['1234567890','QWERTYUIOP','ASDFGHJKL','ZXCVBNM']}.items():
 for value in values:assert value in sources[owner],(owner,value)
e=html.escape;records=[];font=ImageFont.truetype('/usr/share/fonts/truetype/dejavu/DejaVuSans.ttf',14)
def ref(owner,needle):
 lines=sources[owner].splitlines();line=next(i+1 for i,x in enumerate(lines) if needle in x)
 return {'owner':owner,'method_or_token':needle,'source_url':'https://github.com/heretoecode/Supernova/blob/'+SHA+'/'+prefix+owner+'.java#L'+str(line),'source_sha256':hashlib.sha256(sources[owner].encode()).hexdigest()}
def path(x):return '<path d="'+x+'"/>'
def icon(label):
 # Only shapes used in these fixtures. Branch choice/order and coordinates match
 # PreviewIcon.draw; genre Drama/Science Fiction use PreviewGenres.Icon.draw.
 k=label.lower();q=''
 if k=='caps lock':q=path('M4 13 L12 4 L20 13 L16 13 L16 17 L8 17 L8 13 Z')+'<rect x="8" y="20" width="8" height="2"/>'
 elif k=='space':q=path('M4 10 L4 17 L20 17 L20 10')
 elif k=='backspace':q=path('M9 5 L22 5 L22 19 L9 19 L2 12 Z')+path('M12 9 L18 15 M18 9 L12 15')
 elif k in ['select','done'] or 'check' in k:q=path('M5 12 L10 17 L20 6')
 elif k=='plus':q=path('M5 12 L19 12 M12 5 L12 19')
 elif k.startswith(('edit','rename')):q=path('M4 16 L16 4 L20 8 L8 20 L4 20 Z M14 6 L18 10')
 elif k=='cancel' or any(x in k for x in ['clear','close','remove']):q=path('M6 6 L18 18 M18 6 L6 18')
 elif 'movie' in k:q='<rect x="3" y="3" width="18" height="18"/>'+path('M7 3 L7 21 M17 3 L17 21 M3 7 L7 7 M17 7 L21 7 M3 12 L7 12 M17 12 L21 12 M3 17 L7 17 M17 17 L21 17')
 elif 'tv' in k:q='<rect x="2" y="4" width="20" height="14" rx="2"/>'+path('M8 22 L16 22 M12 18 L12 22')
 elif 'total size' in k:q='<ellipse cx="12" cy="6" rx="9" ry="3"/>'+path('M3 6 L3 19 M21 6 L21 19 M21 18.5 A9 3.5 0 0 1 3 18.5 M21 12.5 A9 3.5 0 0 1 3 12.5')
 elif 'local' in k:q=path('M6 3 L18 3 L22 19 L2 19 Z M3 14 L21 14')+'<circle cx="17" cy="17" r=".5"/>'
 elif 'delete' in k:q=path('M5 7 L6 21 L18 21 L19 7 M3 5 L21 5 M9 5 L9 2 L15 2 L15 5 M10 10 L10 17 M14 10 L14 17')
 elif any(x in k for x in ['year','date','added']):q='<rect x="3" y="5" width="18" height="16" rx="2"/>'+path('M3 10 L21 10 M8 3 L8 7 M16 3 L16 7')
 elif any(x in k for x in ['order','ascending','descending','newest','oldest']):q=path('M7 3 L7 21 M3 7 L7 3 L11 7 M17 3 L17 21 M13 17 L17 21 L21 17')
 elif 'filter' in k or 'genre' in k:q=path('M3 5 L21 5 L14 13 L14 20 L10 18 L10 13 Z')
 elif 'library' in k:q='<rect x="3" y="4" width="5" height="17"/><rect x="11" y="4" width="5" height="17"/>'+path('M19 4 L22 20')
 elif any(x in k for x in ['network','source','storage']):q='<rect x="3" y="3" width="18" height="7" rx="2"/><rect x="3" y="14" width="18" height="7" rx="2"/><circle cx="7" cy="7" r=".5"/><circle cx="7" cy="18" r=".5"/>'
 elif 'sort' in k:q=path('M3 5 L14 5 M3 11 L11 11 M3 17 L8 17 M18 3 L18 21 M14 17 L18 21 L22 17')
 elif 'list' in k or 'title' in k:q=path('M8 5 L21 5 M8 12 L21 12 M8 19 L21 19')+'<circle cx="3" cy="5" r=".5"/><circle cx="3" cy="12" r=".5"/><circle cx="3" cy="19" r=".5"/>'
 elif k=='drama':q=path('M3 4 L21 4 L20 15 L12 22 L4 15 Z')+'<circle cx="8" cy="10" r="1"/><circle cx="16" cy="10" r="1"/>'+path('M7 18 A5 3 0 0 1 17 18')
 elif k=='science fiction':q='<circle cx="12" cy="12" r="7"/><ellipse cx="12" cy="12" rx="11" ry="3" transform="rotate(-30 12 12)"/><circle cx="3" cy="3" r=".5"/>'
 else:q='<circle cx="12" cy="12" r="8"/>'+path('M8 12 L16 12')
 return '<svg viewBox="0 0 24 24" fill="none" stroke="#e1e9ef" stroke-width="1.6" stroke-linecap="round" stroke-linejoin="round">'+q+'</svg>'
css='''*{box-sizing:border-box}body{margin:0;font-family:"DejaVu Sans",sans-serif;background:#070d14;color:white}.plate{padding:16px}.provenance{font:12px monospace;color:#cbd8e1;min-height:55px;line-height:18px;padding-bottom:10px;overflow-wrap:anywhere}.ui{background:#0b1b29;border:1px solid #426a8050;border-radius:6px;overflow:hidden}.row{display:flex;align-items:center;gap:0;height:37px;padding:0 10px;font-size:14px}.row svg{width:18px;height:18px;flex-shrink:0}.label{flex:1;margin-left:10px;overflow:hidden;text-overflow:ellipsis;white-space:nowrap}.focused{border:1px solid #62bbf3bf;border-radius:6px;box-shadow:0 0 0 1px #62bbf32a,0 0 0 2px #62bbf30c,0 0 0 3px #62bbf30c,0 0 0 4px #62bbf30c}.selected{background:#0b1b29}.check{width:18px;flex-shrink:0}.menu{padding:12px 14px 12px 12px}.menu h1{font-size:17px;font-weight:400;margin:2px 0 12px 6px;line-height:22px}.divider{height:1px;background:#426a8050}.scroll{overflow:hidden}.control{background:#0b1b29c8;border:1px solid #7f899688;border-radius:10px}.control.focused{background:#203b50e0;border-color:#62bbf3;box-shadow:none}'''
def save(id,area,state,fragment,w,h,fixture,refs,extra_limits=[]):
 path=out/area;path.mkdir(exist_ok=True)
 banner=f'C · CODE-DERIVED APPROXIMATION · {id}<br>0.138 {SHA[:12]} · mdpi / font scale 1 · controlled fixture<br>HTML/SVG, not Android / Shield. Native metrics and focus execution unverified.'
 doc='<html><meta charset="utf-8"><style>'+css+'</style><body><div class="plate"><div class="provenance">'+banner+'</div>'+fragment+'</div></body></html>'
 name=id+'.html';(path/name).write_text(doc)
 records.append({'id':id,'area':area,'state':state,'category':'C — code-derived approximation','source_branch':'codex/supernova-0.138-qa-2026-10-12','source_commit':SHA,'date':'2026-10-12','render_method':'Source-literal assertions + selected geometry/icon transcription to HTML/SVG; system Chromium screenshot','resolution':[w+32,h+87],'ui_crop_resolution':[w,h],'viewport_fixture':{'screenWidthDp':960,'screenHeightDp':540,'density':1,'scaledDensity':1,'fontScale':1,'TvLayout.scale':1},'fixture':fixture,'source_references':refs,'known_deviations':['DejaVu Sans substitutes Android sans-serif/sans-serif-light; line breaks/ellipses can differ','HTML layout/rasterisation is not Android; panel measurements are transcribed estimates','Crops omit underlying app/window placement and dimming; controlled focus state is illustrated, not executed','No physical Shield evidence; no real library values or assets'], 'state_specific_limits':extra_limits,'html':'visuals/'+area+'/'+name,'file':'visuals/'+area+'/'+id+'.png','verified':'Labels/order/selected dimensions checked against source; raster inspected separately'})
def menu(id,title,labels,focus=0,checks=(),fixture=None,membership=False,scroll=0,maxheight=492,refs=None):
 w=min(520,max(240,math.ceil(max(font.getlength(x) for x in labels))+88));chrome=61;h=min(maxheight,chrome+37*len(labels));items=''
 for i,label in enumerate(labels):
  mark=icon('check' if i in checks else 'plus') if membership and i<len(labels)-1 else icon('check') if i in checks else ''
  lead='' if membership and i<len(labels)-1 else icon(label)
  items+='<div class="row '+('focused' if i==focus else '')+'">'+lead+'<span class="label">'+e(label)+'</span><span class="check">'+mark+'</span></div>'
 fragment=f'<div class="ui menu" style="width:{w}px;height:{h}px"><h1>{e(title)}</h1><div class="divider"></div><div class="scroll" style="height:{h-chrome}px"><div style="transform:translateY(-{scroll}px)">'+items+'</div></div></div>'
 save(id,'customise-home',title+' / '+labels[focus],fragment,w,h,fixture or {'labels':labels,'checked_indices':list(checks),'focus_index':focus,'scrollOffset':scroll},refs or [ref('PreviewHomeRows','private void rule'),ref('PreviewDialog','public static Dialog choose')])
builtins=['Continue Watching','Watch Next','Recently Added','Trending on Trakt · In Your Library','Popular on Trakt · In Your Library','Recently Watched','Because You Watched']
for state,offset,moving in [('top',0,False),('custom-row',109,False),('movement',109,True)]:
 labels=builtins+['Atlas Fixture'];rows='<div class="editor-action">Create New Row</div>'
 for i,label in enumerate(labels):
  custom=i==7;rows+='<div style="display:flex;height:39px"><div class="editor-action '+('focused' if (i==0 if state=='top' else custom) else '')+'" style="flex:1">'+e(('↕  ' if custom and moving else '')+label)+'</div><div class="editor-action" style="width:95px">'+('Hidden' if i==1 else 'Shown')+'</div>'
  if custom:
   for x in ['Rename','Genre Rule','Delete']:rows+='<div class="editor-action" style="width:88px;margin-left:6px">'+x+'</div>'
  rows+='</div>'
 frag='<style>.editor-action{height:36px;padding:0 12px;font-size:14px;color:#d6e5ef;display:flex;align-items:center}.editor h1{margin:0;font-size:21px;line-height:28px;font-weight:300}.help{padding:8px 0 16px;line-height:16px;font-size:13px;color:#d6e5ef}</style><div class="ui editor" style="width:700px;height:350px;padding:12px 16px"><h1>Customise Home</h1><div class="divider" style="margin-top:8px;margin-bottom:10px"></div><div class="help">Select to edit · Up/Down to reorder</div><div class="scroll" style="height:239px"><div style="transform:translateY(-'+str(offset)+'px)">'+rows+'</div></div></div>'
 save('CH-C-EDITOR-'+state.upper(),'customise-home','Main editor '+state,frag,700,350,{'rows':labels,'hidden':'watchnext','customRow':'custom:fixture','dynamic':True,'moving':moving,'scrollOffset':offset},[ref('PreviewHomeRows','private void editor')],['Scroll offsets are illustrative fixture offsets, not measured Android scroll positions'])
root=['Dynamic genre rule: On','Genres: All','Movies: On','TV Shows: On','Sort: added','Maximum items: No Limit','Direction: Descending']
for focus in range(7):menu('CH-C-RULE-FOCUS-'+str(focus),'Atlas Fixture',root,focus)
menu('CH-C-RULE-CHANGED','Atlas Fixture',['Dynamic genre rule: Off','Genres: Drama, Science Fiction','Movies: Off','TV Shows: On','Sort: title','Maximum items: 37','Direction: Ascending'],0)
for i in range(3):menu('CH-C-SORT-'+str(i),'Row sort',['Date Added','Title','Year'],i,(i,))
maxlabels=['No Limit','10','20','30','40','50','75','100','Select…']
menu('CH-C-MAX-PRESETS','Maximum items',maxlabels,0,(0,));menu('CH-C-MAX-SELECT','Maximum items',maxlabels,8,(2,));menu('CH-C-MAX-CUSTOM','Maximum items',maxlabels,8,())
menu('CH-C-GENRES-EMPTY','Genres · Match Any Selected',['All Genres'],0,(0,),{'memoryCache':'null / no available genres','selected':[]},refs=[ref('PreviewHomeRows','Set<String> values=new TreeSet'),ref('PreviewGenres','public static void choose')])
menu('CH-C-GENRES-FIXTURE','Genres · Match Any Selected',['All Genres','Drama','Science Fiction'],2,(1,2),{'memoryCache':'controlled movie/show genres Drama|Science Fiction','selected':['Drama','Science Fiction']},refs=[ref('PreviewGenres','public static void choose')])
menu('CH-C-MEMBERSHIP','Add to Row',['Watch Next','Atlas Fixture','Hidden Fixture','Create New Row…'],1,(0,2),{'entry':'v1','hiddenRowSelectable':True,'membership':['watchnext','custom:hidden']},membership=True,refs=[ref('PreviewHomeRows','private void membership'),ref('PreviewDialog','public static void updateMembership')])
menu('CH-C-MOVE-HIDE','Atlas Fixture',['Move','Hide'],0,refs=[ref('PreviewHomeRows','public static void rowControls')])
menu('CH-C-MOVE-TO','Move row to',[str(i+1)+' · '+x for i,x in enumerate(builtins+['Atlas Fixture'])],7,(7,),refs=[ref('PreviewHomeRows','public static void rowControls')])
def keyboard(id,title,value,focus='T',error=None):
 keys=['1234567890','QWERTYUIOP','ASDFGHJKL','ZXCVBNM'];inner=''
 for i,line in enumerate(keys):
  margin=12 if i==2 else 36 if i==3 else 0
  inner+=f'<div style="display:flex;margin:3px {margin}px 0;height:32px">'
  for key in line:inner+='<div class="key control '+('focused' if key==focus else '')+'">'+key+'</div>'
  inner+='</div>'
 for line in [['Caps Lock','Space','Backspace'],['Clear','Done','Cancel']]:
  inner+='<div style="display:flex;margin-top:3px;height:32px">'
  for key in line:inner+='<div class="key control '+('focused' if key==focus else '')+'" style="flex:'+('3' if key=='Space' else '1')+';font-size:12px">'+icon(key)+('' if key in ['Caps Lock','Backspace'] else e(key))+'</div>'
  inner+='</div>'
 height=312;fragment='<style>.key{flex:1;display:flex;align-items:center;justify-content:center;gap:4px;margin:0 2px;font-size:14px}.key svg{width:14px;height:14px}</style><div class="ui" style="width:560px;height:312px;border-radius:16px;padding:16px 20px"><div style="font-size:20px;text-align:center;line-height:26px">'+title+'</div><div class="control" style="height:44px;font-size:16px;display:flex;align-items:center;padding:0 12px">'+e(value)+(' <span style="margin-left:auto;color:#ffaaaa">!</span>' if error else '')+'</div>'+inner+'</div>'
 if error:
  # Android setError popup is system-owned. Do not fabricate its shape/position.
  fragment+='<div style="width:560px;padding:10px 0;font:12px monospace;color:#ffbaba">SOURCE VALIDATION TEXT (Android popup appearance unverified):<br>'+e(error)+'</div>';height+=70
 save(id,'customise-home',title+' / '+focus,fragment,560,height,{'input':value,'focusKey':focus,'caps':True,'validation_error':error},[ref('PreviewTextInput','private static void showInput'),ref('PreviewKeyboard','public PreviewKeyboard'),ref('PreviewHomeRows','private void name')],['Input error icon uses a marked approximation; native setError popup omitted and exact text transcribed outside UI crop'] if error else [])
keyboard('CH-C-RENAME','Rename Row','Atlas Fixture');keyboard('CH-C-CREATE','Create New Row','');keyboard('CH-C-CREATE-ERROR','Create New Row','', 'Done','Enter a name')
keyboard('CH-C-MAX-INPUT','Maximum items','37','T');keyboard('CH-C-MAX-ERROR','Maximum items','2147483648','Done','Enter a whole number from 0 to 2147483647. Zero means No Limit.')
for focus in ['Cancel','Delete']:
 frag='<div class="ui" style="width:362px;height:205px;border-radius:16px;padding:18px"><div style="font-size:24px">Delete Row?</div><div style="font-size:16px;margin:18px 0">Titles remain in your library. Only this Home row is removed.</div><div style="display:flex;justify-content:flex-end;gap:12px">'+''.join('<div class="control '+('focused' if x==focus else '')+'" style="padding:10px;font-size:13px;color:'+('#ffa5a5' if x=='Delete' else 'white')+'">'+x+'</div>' for x in ['Cancel','Delete'])+'</div></div>'
 save('CH-C-DELETE-'+focus.upper(),'customise-home','Production Delete Row? / '+focus,frag,362,205,{'title':'Delete Row?','focus':focus},[ref('PreviewHomeRows','Delete Row?'),ref('PreviewDialog','public static Dialog confirmDelete')],['Native AlertDialog icon and framework geometry are omitted/approximate; use historical native captures for real framework rendering, whose test title has lowercase row'])
def stats(id,title,lines,kind='Movies',custom=False,listmode=False):
 icons=[kind,'Total Size','Local Storage','Network'];rows=''
 for i,line in enumerate(lines):rows+='<div style="display:flex;white-space:pre-wrap;align-items:center;padding:2px 0;min-height:23px;font-size:13px;color:#e1e9ef;gap:9px">'+('<span style="width:17px;height:17px">'+icon(icons[i])+'</span>' if i<4 else '')+e(line)+'</div>'
 controls=['Filters  ▾','Sort: '+('Title' if custom else 'Date Added')+'  ▾','A → Z' if custom else 'Newest First','Grid View' if listmode else 'List View']+(['Columns'] if listmode else [])+(['Edit Library'] if custom else [])
 toolbar='<div style="display:flex;gap:8px;margin-top:19px;margin-bottom:12px">'+''.join('<div class="control '+('focused' if i==0 else '')+'" style="display:flex;align-items:center;gap:8px;padding:8px 16px;height:44px;font-size:13px;'+('margin-left:auto;' if x=='Edit Library' else '')+'">'+'<span style="width:18px;height:18px;flex-shrink:0">'+icon(x)+'</span><span style="white-space:nowrap">'+e(x)+'</span></div>' for i,x in enumerate(controls))+'</div><div class="divider"></div>'
 h=100+23*len(lines)+88;frag='<div style="width:904px;height:'+str(h)+'px;padding:12px 0"><div style="font-size:30px;font-weight:bold;line-height:38px">'+e(title)+'</div>'+rows+toolbar+'</div>'
 save(id,'library-statistics',title+' header and toolbar',frag,904,h,{'summary_lines':lines,'custom':custom,'listMode':listmode,'focus':'Filters','values':'controlled indexed fixtures; not real owner data'},[ref('LibraryPageHeader','private static void add'),ref('PreviewLibrarySummary','public static String describe'),ref('PreviewPages','else if(c.type==HEADER)')],['Header/toolbar crop only; background artwork omitted','File sizes use explicitly supplied en-US display strings; Android Formatter/locale rounding unexecuted','First four icon positions only, matching source even for mixed blocks; blank line row retained'])
stats('LS-C-MOVIES-PARTIAL','Movies',['3 Movies','Total Size    ≥ 3.0 GB','Local Storage    2 movies · 1.0 GB','Network / WebDAV    1 movies · 2.0 GB','Indexed sizes only; some file sizes are unknown'])
stats('LS-C-TV-SPANNING','TV Shows',['2 Shows','Total Size    ≥ 3.0 GB','Local Storage    1 shows · 1.0 GB','Network / WebDAV    2 shows · 2.0 GB','Indexed sizes only; some file sizes are unknown','Shows spanning sources appear in both source counts'],kind='TV Shows')
movies=['2 Movies','Total Size    1.0 GB','Local Storage    1 movies · 500 MB','Network / WebDAV    1 movies · 500 MB']
tv=['1 Shows','Total Size    2.0 GB','Local Storage    1 shows · 1.0 GB','Network / WebDAV    1 shows · 1.0 GB','Shows spanning sources appear in both source counts']
stats('LS-C-CUSTOM-MIXED','Atlas Library',movies+['']+tv,kind='Library',custom=True)
stats('LS-C-CUSTOM-MOVIES','Atlas Movies',movies,kind='Library',custom=True,listmode=True)
stats('LS-C-CUSTOM-TV','Atlas TV',tv,kind='Library',custom=True)
stats('LS-C-CUSTOM-EMPTY','Atlas Empty',['0 Movies','Total Size    0 B','Local Storage    0 movies · 0 B','Network / WebDAV    0 movies · 0 B'],kind='Library',custom=True)
with sync_playwright() as pw:
 browser=pw.chromium.launch(executable_path='/usr/bin/chromium',headless=True,args=['--no-sandbox'])
 for r in records:
  file=out.parent/r['html'];png=out.parent/r['file'];page=browser.new_page(viewport={'width':r['resolution'][0],'height':r['resolution'][1]},device_scale_factor=1)
  page.set_content(file.read_text());actual_height=math.ceil(page.locator('.plate').bounding_box()['height']);page.set_viewport_size({'width':r['resolution'][0],'height':actual_height});page.screenshot(path=str(png),full_page=True);r['resolution']=list(__import__('PIL.Image',fromlist=['Image']).open(png).size);r['sha256']=hashlib.sha256(png.read_bytes()).hexdigest();page.close()
 browser.close()
(out/'reconstruction-manifest.json').write_text(json.dumps(records,indent=2,ensure_ascii=False));print('Rendered',len(records),'labelled category C illustrations')
