const fs=require('fs'),path=require('path');const {chromium}=require('playwright');const dir=__dirname;
const gb=fs.readFileSync(path.join(dir,'gb.b64'),'utf8').trim();
const icon = {
  cc: '<rect x="3" y="5" width="18" height="14" rx="2"/><path d="M10 10.2a2.2 2.2 0 1 0 0 3.6M16.5 10.2a2.2 2.2 0 1 0 0 3.6"/>',
  list: '<path d="M9 6h11M9 12h11M9 18h11"/><circle cx="4.5" cy="6" r="1"/><circle cx="4.5" cy="12" r="1"/><circle cx="4.5" cy="18" r="1"/>',
  down: '<path d="M12 4v11M7 10.5l5 5 5-5M4 20h16"/>',
  aa: '<text x="2" y="18" font-size="15" font-family="Roboto" stroke="none" fill="currentColor">Aa</text>',
  palette: '<circle cx="12" cy="12" r="9"/><circle cx="8" cy="10" r="1.2"/><circle cx="12" cy="7.5" r="1.2"/><circle cx="16" cy="10" r="1.2"/><circle cx="9" cy="15" r="1.2"/>',
  vpos: '<path d="M12 3v18M8 7l4-4 4 4M8 17l4 4 4-4"/>',
  outline: '<rect x="4" y="3" width="16" height="18" stroke-dasharray="2 2"/><text x="8" y="17" font-size="12" font-family="Roboto" stroke="none" fill="currentColor">A</text>',
  bg: '<rect x="4" y="4" width="16" height="16"/><path d="M4 12l8-8M4 18l14-14M10 20l10-10M16 20l4-4"/>',
  drop: '<path d="M12 3c4 5 6 8 6 11a6 6 0 0 1-12 0c0-3 2-6 6-11z"/>',
  timer: '<circle cx="12" cy="13" r="8"/><path d="M12 13V9M9 2h6"/>',
  note: '<path d="M9 18V5l11-2v13"/><circle cx="6" cy="18" r="3"/><circle cx="17" cy="16" r="3"/>',
  boost: '<path d="M4 9h4l5-4v14l-5-4H4z"/><path d="M17 8v8M13.5 12h7"/>',
  moon: '<path d="M20 14.5A8 8 0 1 1 9.5 4a6.5 6.5 0 0 0 10.5 10.5z"/>',
  surround: '<circle cx="12" cy="12" r="2.5"/><path d="M6.5 6.5a7.8 7.8 0 0 0 0 11M17.5 6.5a7.8 7.8 0 0 1 0 11M3.7 3.7a11.7 11.7 0 0 0 0 16.6M20.3 3.7a11.7 11.7 0 0 1 0 16.6"/>',
  aspect: '<rect x="2.5" y="6" width="19" height="12" rx="1.5"/><path d="M6 9.5V9h3M18 14.5v.5h-3"/>',
  cube: '<path d="M12 3l8 4.5v9L12 21l-8-4.5v-9z"/><path d="M12 12l8-4.5M12 12v9M12 12L4 7.5"/>',
  pip: '<rect x="2.5" y="5" width="19" height="14" rx="1.5"/><rect x="12" y="11" width="7.5" height="6" rx="1" fill="currentColor"/>',
  speed: '<path d="M4 17a8 8 0 1 1 16 0"/><path d="M12 17l4-5"/>',
  repeat: '<path d="M17 2l3 3-3 3"/><path d="M4 11V9a4 4 0 0 1 4-4h12M7 22l-3-3 3-3"/><path d="M20 13v2a4 4 0 0 1-4 4H4"/>',
  skip: '<path d="M5 5l9 7-9 7z"/><path d="M18 5v14"/>',
  zzz: '<path d="M4 6h6l-6 8h6M13 4h5l-5 6h5"/><path d="M15 15h4l-4 5h4"/>',
  bell: '<path d="M6 17V11a6 6 0 0 1 12 0v6l2 2H4z"/><path d="M10 21h4"/>',
  playtab: '<circle cx="12" cy="12" r="9"/><path d="M10 8.5l5 3.5-5 3.5z" fill="currentColor"/>',
  search: '<circle cx="11" cy="11" r="6.5"/><path d="M16 16l4.5 4.5"/>',
  gear: '<circle cx="12" cy="12" r="3.2"/><path d="M12 2.5v3M12 18.5v3M2.5 12h3M18.5 12h3M5.3 5.3l2.1 2.1M16.6 16.6l2.1 2.1M5.3 18.7l2.1-2.1M16.6 7.4l2.1-2.1"/>',
  speaker: '<path d="M4 9h4l5-4v14l-5-4H4z"/><path d="M16 9a4 4 0 0 1 0 6M18.5 6.5a7.5 7.5 0 0 1 0 11"/>',
  monitor: '<rect x="3" y="4" width="18" height="12" rx="1.5"/><path d="M9 20h6M12 16v4"/>',
  sliders: '<path d="M4 6h16M4 12h16M4 18h16"/><circle cx="9" cy="6" r="2" fill="#141a52"/><circle cx="15" cy="12" r="2" fill="#141a52"/><circle cx="8" cy="18" r="2" fill="#141a52"/>',
};
const svg = (name, size = 30) => `<svg width="${size}" height="${size}" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.6" stroke-linecap="round" stroke-linejoin="round">${icon[name]}</svg>`;
const toggle = on => `<span class="toggle ${on ? 'on' : ''}"><span class="thumb"></span></span>`;
const choice = (opts, sel) => `<span class="choices">${opts.map((o, i) => `${i ? '<span class="bar">|</span>' : ''}<span class="${i === sel ? 'sel' : ''}">${o}</span>`).join('')}</span>`;
const row = (ic, label, value, cls = '') => `<div class="row ${cls}"><span class="ic">${svg(ic)}</span><span class="label">${label}</span><span class="value">${value}</span></div>`;


icon.film='<rect x="3" y="4" width="18" height="16" rx="2"/><path d="M7 4v16M17 4v16M3 9h4M3 15h4M17 9h4M17 15h4"/>';
icon.tag='<path d="M3 12V4h8l10 10-8 8z"/><circle cx="7.5" cy="8.5" r="1.3"/>';
icon.match='<path d="M4 7h16M4 12h10M4 17h6"/>';
icon.cal='<rect x="3.5" y="5" width="17" height="15" rx="2"/><path d="M3.5 10h17M8 3v4M16 3v4"/>';
icon.eye='<path d="M2 12s3.6-7 10-7 10 7 10 7-3.6 7-10 7S2 12 2 12z"/><circle cx="12" cy="12" r="3"/>';
icon.star='<path d="M12 3.5l2.6 5.4 5.9.8-4.3 4.1 1 5.9L12 17l-5.2 2.7 1-5.9-4.3-4.1 5.9-.8z"/>';
icon.globe='<circle cx="12" cy="12" r="9"/><path d="M3 12h18M12 3c2.8 3 2.8 15 0 18M12 3c-2.8 3-2.8 15 0 18"/>';
icon.studio='<path d="M4 20V9l8-5 8 5v11M9 20v-6h6v6"/>';
icon.grid='<rect x="4" y="4" width="7" height="7" rx="1"/><rect x="13" y="4" width="7" height="7" rx="1"/><rect x="4" y="13" width="7" height="7" rx="1"/><rect x="13" y="13" width="7" height="7" rx="1"/>';
icon.sort='<path d="M7 4v16M3.5 16.5 7 20l3.5-3.5M17 20V4M13.5 7.5 17 4l3.5 3.5"/>';
icon.pen='<path d="M4 20h4L19 9l-4-4L4 16z"/>';
icon.check='<path d="M4 12.5l5 5L20 6.5"/>';
const step=v=>`<span class="step"><b>‹</b><span class="sv">${v}</span><b>›</b></span>`;
const head=(t,f)=>`<div class="head" ${f?'':'style="margin-top:10px"'}>${t}</div>`;
const tile=(k,v,s)=>`<div class="tile"><div class="tk">${k}</div><div class="tv">${v}</div><div class="ts">${s}</div></div>`;
const hues=[[200,40],[30,60],[150,35],[260,40],[10,55],[190,45],[45,55],[280,35],[120,30],[220,50],[340,45],[170,40]];
const posters=hues.map(([h,l],i)=>`<div class="poster" style="background:linear-gradient(160deg,hsl(${h} 45% ${l}%),hsl(${h+30} 50% ${Math.max(12,l-28)}%))"><span>Title ${i+1}</span></div>`).join('');
const libpage=`<div class="lib"><div class="lnav"><b>SUPERNOVA</b><span>Home</span><span>Movies</span><span>TV Shows</span><span class="on">Documentaries</span><i></i><span>Network &amp; Files</span></div>
<div class="lstats">${['32 items','119 GB total','2 in progress','0 added this month'].map(t=>`<div>${t}</div>`).join('')}</div>
<div class="ltool"><div>Filters</div><div>Sort: Date Added</div><div>Newest First</div><div>List View</div></div><div class="lline"></div>
<div class="lgrid">${hues.concat(hues).slice(0,18).map(([h,l],i)=>`<div class="poster" style="background:linear-gradient(160deg,hsl(${h} 45% ${l}%),hsl(${h+30} 50% ${Math.max(12,l-28)}%))"><span>Title ${i+1}</span></div>`).join('')}</div></div>`;
const preview=`<div class="panel preview"><div class="ptitle">Preview</div><div class="pname">What this page will show</div>
 <div class="tiles">${tile('Items','32','matching titles')}${tile('Total size','119 GB','indexed media')}${tile('Local storage','31 · 115 GB','titles · size')}${tile('Network / WebDAV','1 · 3.8 GB','title · size')}</div>
 <div class="phead">Newest first · showing the first 20 of 32</div><div class="posters">${posters}</div></div>`;
const tabs=a=>`<div class="tabs">${['Details','Filters','Display','Review'].map(t=>`<div class="tab ${t===a?'active':''}">${t}</div>`).join('')}</div>`;
const one = head('PAGE',1)+row('pen','Page Name',`Documentaries<span class="chev">›</span>`)+row('film','Includes',choice(['Movies','TV Shows','Both'],2))+
 head('WHAT TO SHOW')+row('tag','Genres',`Documentary, Nature +1<span class="chev">›</span>`)+row('match','Match',choice(['Any','All'],0))+
 row('cal','Year',step('Any'))+row('eye','Watched',step('All'),'focus')+row('star','Minimum Rating',step('Any'))+row('globe','Original Language',`Any<span class="chev">›</span>`)+row('studio','Studio / Network',`Any<span class="chev">›</span>`)+
 head('HOW TO SHOW IT')+row('grid','View',choice(['Grid','List'],0))+row('sort','Sort By',choice(['Title','Year','Date Added'],2))+row('sort','Order',choice(['Newest','Oldest'],0))+`<div class="del">${svg('bg',22)}Delete Page<span>Removes this page only. Your media is never deleted.</span></div>`;
const screens={single:['',one]};
const page=(tab,body,tag)=>`<html><head><style>*{box-sizing:border-box;margin:0;padding:0}
body{width:1920px;height:1080px;overflow:hidden;font-family:Roboto,Arial,sans-serif;color:#fff;background:radial-gradient(ellipse 1400px 900px at 70% 0%,#121a3a,#05070f 70%)}
.nav{position:absolute;left:56px;right:56px;top:28px;height:56px;border:1.5px dashed rgba(255,255,255,.18);border-radius:12px;color:rgba(255,255,255,.4);font-size:18px;display:flex;align-items:center;justify-content:center}
.tagline{position:absolute;left:56px;bottom:8px;font-size:15px!important;font-size:18px;color:rgba(255,255,255,.55)}
.wrap{position:absolute;right:40px;top:96px;width:690px}
.panel{border-radius:22px;padding:24px 30px!important;background:linear-gradient(180deg,rgba(26,31,92,.92),rgba(18,22,66,.92));border:1.5px solid #3e5aa8;padding:28px 30px}
.editor{width:690px;background:linear-gradient(180deg,#1a1f5c,#141848)!important;box-shadow:0 20px 60px rgba(0,0,0,.6);border-color:#5fd0ff!important}.preview{flex:1;display:flex;flex-direction:column}
.title{display:flex;align-items:center;justify-content:space-between;margin-bottom:14px}.title h1{font-size:30px}.count{color:#7fe0a8!important;font-size:18px}.title span{font-size:18px;color:#9cc9f0}.hbtns{display:flex;gap:12px}.btn.ghost{border:2px solid #7a3b4f;color:#ff9a9a;font-weight:600;height:52px;font-size:19px;padding:0 20px}.hbtns .btn{height:46px!important;font-size:17px!important;padding:0 14px!important}.hbtns .btn.primary{height:52px;font-size:20px;padding:0 22px;box-shadow:none;border-color:#5a8cff}
.tabs{display:grid;grid-template-columns:repeat(4,1fr);gap:12px;margin-bottom:20px}
.tab{height:54px;border-radius:12px;border:2px solid #5a7bd8;display:flex;align-items:center;justify-content:center;font-size:20px;font-weight:600}
.tab.active{background:#2f6df0;border-color:#7fd6ff;box-shadow:0 0 16px rgba(95,208,255,.55)}
.head{font-size:15px;font-weight:700;color:#9cc9f0;letter-spacing:.6px;padding:6px 4px 6px;border-bottom:1.5px solid #3e5aa8}
.row{display:flex;align-items:center;height:54px;padding:0 8px;position:relative}
.row:after{content:'';position:absolute;left:0;right:0;bottom:0;height:1.5px;background:#34479a}
.row.focus{border:2.5px solid #7fd6ff;border-radius:12px;box-shadow:0 0 14px rgba(95,208,255,.6);background:rgba(95,208,255,.08)}.row.focus:after{display:none}
.ic{width:48px;display:flex}.label{flex:1;font-size:22px;font-weight:600}.value{display:flex;align-items:center;gap:10px;font-size:21px}
.chev{font-size:26px;margin-left:4px}.choices{display:flex;gap:10px}.choices .sel{color:#3fb6ff}.bar{color:#8fa4d8}
.step{display:flex;align-items:center;gap:22px}.step b{font-size:28px;font-weight:400}.sv{min-width:80px;text-align:center;color:#3fb6ff}
.ptitle{font-size:28px;font-weight:700}.pname{font-size:20px;color:#9cc9f0;margin:4px 0 18px}
.tiles{display:grid;grid-template-columns:1fr 1fr;gap:12px;margin-bottom:18px}.tile{background:rgba(255,255,255,.05);border:1.5px solid #34479a;border-radius:14px;padding:12px 16px}
.tk{font-size:16px;color:#9cc9f0}.tv{font-size:28px;font-weight:700;margin:2px 0}.ts{font-size:15px;color:#b9c7e8}
.phead{font-size:16px;color:#b9c7e8;margin-bottom:12px}.posters{display:grid;grid-template-columns:repeat(4,1fr);gap:12px}
.poster{aspect-ratio:2/3;border-radius:10px;display:flex;align-items:flex-end;padding:8px;font-size:14px;color:rgba(255,255,255,.75)}
.posters{flex:1;overflow:hidden;-webkit-mask-image:linear-gradient(180deg,#000 70%,transparent);mask-image:linear-gradient(180deg,#000 70%,transparent)}
.sum{display:grid;grid-template-columns:150px 1fr;row-gap:14px;column-gap:20px;font-size:20px;padding:16px 8px 22px;border-bottom:1.5px solid #34479a}.sum span{color:#9cc9f0}.sum b{font-weight:500}
.actions{padding:22px 0 10px}.btn{height:62px;border-radius:12px;display:inline-flex;align-items:center;gap:12px;padding:0 28px;font-size:22px;font-weight:700}
.btn.primary{background:#2f6df0;border:2.5px solid #7fd6ff;box-shadow:0 0 16px rgba(95,208,255,.6)}
.danger{margin-top:22px;padding-top:18px;border-top:1.5px solid #5a2a3a;display:flex;align-items:center;gap:12px;font-size:20px;color:#ff8a8a}.dnote{color:#b9a3b0;font-size:16px;margin-left:12px}
.lib{position:absolute;inset:0;padding:0 56px}.lnav{display:flex;gap:34px;align-items:center;height:84px;font-size:22px;color:#dfe6ff}.lnav b{font-weight:400;margin-right:40px}.lnav .on{color:#fff;border:2px solid #5fd0ff;border-radius:10px;padding:4px 12px}.lnav i{flex:1}
.lstats{display:grid;grid-template-columns:repeat(4,1fr);gap:0;background:rgba(60,100,160,.35);border-radius:14px;height:96px;align-items:center;font-size:22px;margin-top:8px}.lstats div{padding-left:24px;border-left:1px solid rgba(255,255,255,.12)}
.ltool{display:flex;gap:14px;margin:22px 0 22px}.ltool div{border:1.5px solid rgba(255,255,255,.25);background:rgba(60,100,160,.3);border-radius:12px;padding:12px 20px;font-size:19px}.lline{height:1.5px;background:rgba(127,214,255,.4);margin-bottom:22px}
.lgrid{display:grid;grid-template-columns:repeat(6,1fr);gap:18px}
.shade{position:absolute;inset:0;background:linear-gradient(90deg,rgba(0,0,0,0) 0%,rgba(0,0,0,.15) 45%,rgba(0,0,0,.55) 62%)}
.del{display:flex;align-items:center;gap:12px;margin-top:14px;padding:12px 8px 0;color:#ff8a8a;font-size:19px;font-weight:600}.del span{color:#a99ab0;font-size:15px;font-weight:400;margin-left:6px}.tagline{display:none}</style></head><body>${libpage}<div class="shade"></div><div class="wrap"><div class="panel editor"><div class="title"><div><h1>Edit Page</h1><span class="count">32 titles match · 119 GB</span></div><div class="hbtns"><div class="btn primary">${svg('check',24)}Save Changes</div></div></div>${body}</div></div>
<div class="tagline">${tag}</div></body></html>`;
(async()=>{const b=await chromium.launch();const p=await b.newPage({viewport:{width:1920,height:1080}});
for(const [n,[t,body]] of Object.entries(screens)){await p.setContent(page(t,body,`Proposal · Edit Page as a 690px side panel over the real library page (which updates live behind it) · posters are placeholders`));await p.screenshot({path:path.join(dir,'edit3-'+n+'.png')});}
await b.close();console.log('ok')})();
