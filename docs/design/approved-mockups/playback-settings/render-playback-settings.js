// Renders the Option B side-panel mock-up at 1920x1080 (Shield output resolution).
const fs = require('fs');
const path = require('path');
const { chromium } = require('playwright');
const dir = __dirname;
const gb = fs.readFileSync(path.join(dir, 'gb.b64'), 'utf8').trim();

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

function page(opacityRow, activeTab, body, tag, sub){ return `<!doctype html><html><head><meta charset="utf-8"><style>
*{box-sizing:border-box;margin:0;padding:0}
body{width:1920px;height:1080px;overflow:hidden;font-family:Roboto,"Noto Sans",Arial,sans-serif;color:#fff;background:#000}
.film{position:absolute;inset:0;background:
  radial-gradient(ellipse 900px 380px at 30% 78%, rgba(255,170,90,.55), transparent 70%),
  linear-gradient(180deg,#1b2a4a 0%,#3d4f7a 32%,#c77b52 58%,#2a1d22 62%,#140f14 100%)}
.film .sun{position:absolute;left:480px;top:520px;width:170px;height:170px;border-radius:50%;background:radial-gradient(circle,#ffe2a6,#ff9a52 60%,transparent 72%)}
.film .hills{position:absolute;left:0;right:0;bottom:0;height:470px;background:
  radial-gradient(ellipse 700px 200px at 15% 30%,#1d1418 98%,transparent 100%),
  radial-gradient(ellipse 900px 240px at 70% 40%,#22171c 98%,transparent 100%),#140f14}
.film .figure{position:absolute;left:760px;bottom:300px;width:46px;height:150px;border-radius:23px 23px 6px 6px;background:#0b0809}
.film .figure:before{content:"";position:absolute;left:5px;top:-40px;width:36px;height:36px;border-radius:50%;background:#0b0809}
.dim{position:absolute;top:0;height:1000px;right:0;width:760px;background:linear-gradient(90deg,rgba(0,0,0,0),rgba(0,0,0,.45) 90px)}
.subtitle{position:absolute;left:0;width:1920px;bottom:70px;text-align:center;font-size:44px;font-weight:500;color:#ffc93c;
  text-shadow:-2px -2px 0 #000,2px -2px 0 #000,-2px 2px 0 #000,2px 2px 0 #000,0 3px 8px #000}
.preview-note{position:absolute;left:0;width:1240px;bottom:1000px;display:none;text-align:center;font-size:18px;color:rgba(255,255,255,.75);letter-spacing:.3px}
.panel{position:absolute;top:40px;right:40px;width:640px;border-radius:26px;
  background:linear-gradient(180deg,rgba(26,31,92,.96),rgba(20,24,72,.96));border:2px solid #5fd0ff;
  box-shadow:0 0 22px rgba(95,208,255,.35),0 20px 60px rgba(0,0,0,.55);padding:30px 30px 26px;display:flex;flex-direction:column}
.panel .row:last-child:after{display:none}
.title{display:flex;align-items:center;gap:14px;font-size:30px;font-weight:700;margin-bottom:22px}
.tabs{display:grid;grid-template-columns:repeat(4,1fr);gap:10px;margin-bottom:22px}
.tab{height:56px;border-radius:14px;border:2px solid #5a7bd8;display:flex;align-items:center;justify-content:center;gap:8px;font-size:19px;font-weight:600}
.tab.active{background:#2f6df0;border-color:#7fd6ff;box-shadow:0 0 16px rgba(95,208,255,.55)}
.head{font-size:17px;font-weight:700;color:#9cc9f0;letter-spacing:.6px;padding:10px 4px 8px;border-bottom:1.5px solid #3e5aa8}
.row{display:flex;align-items:center;height:60px;padding:0 6px;position:relative}
.row:after{content:'';position:absolute;left:0;right:0;bottom:0;height:1.5px;background:#34479a}
.row.focus:after{display:none}
.row .ic{width:44px;display:flex;color:#e8eeff}
.row .label{flex:1;font-size:21px;font-weight:600}
.row .value{display:flex;align-items:center;gap:10px;font-size:20px}
.row.dimmed{opacity:.42}
.row.focus{border-radius:10px;border:2.5px solid #7fd6ff;box-shadow:0 0 14px rgba(95,208,255,.6);background:rgba(95,208,255,.08)}
.chev{font-size:24px;margin-left:2px}
.flag{width:30px;height:30px;border-radius:4px}
.choices{display:flex;gap:9px;align-items:center}.choices .sel{color:#3fb6ff}.choices .bar{color:#cfd8ff}
.dots{display:flex;gap:11px}.dot{width:30px;height:30px;border-radius:50%}.dot.sel{outline:3px solid #fff;outline-offset:2px}
.toggle{width:58px;height:34px;border-radius:17px;background:#3a4a77;position:relative;display:inline-block}
.toggle.on{background:linear-gradient(90deg,#00aff0,#2559ee)}
.toggle .thumb{position:absolute;top:3px;left:3px;width:28px;height:28px;border-radius:50%;background:#fff}
.toggle.on .thumb{left:27px}
.search{display:flex;align-items:center;gap:12px;height:56px;border:2px solid #5a7bd8;border-radius:14px;padding:0 16px;font-size:19px;color:#b9c7e8;margin-bottom:12px}
.res{height:74px}.res .label{display:flex;flex-direction:column;gap:4px}.res .meta{font-size:16px;font-weight:400;color:#b9c7e8}
.fname{font-size:19px;font-weight:600;white-space:nowrap;overflow:hidden;text-overflow:ellipsis;max-width:430px}
.tick{color:#3fb6ff;font-size:24px;width:28px;text-align:right}
.subhead{display:flex;align-items:center;gap:12px;font-size:28px;font-weight:700;margin-bottom:18px}.subhead .back{font-size:30px;color:#9cc9f0}
.delay{display:flex;gap:22px;align-items:center;font-size:21px}.delay b{font-size:26px;font-weight:400}
.label-tag{position:absolute;left:40px;top:34px;font-size:20px;color:rgba(255,255,255,.85);background:rgba(0,0,0,.45);padding:8px 14px;border-radius:10px}
</style></head><body>
<div class="film"><div class="sun"></div><div class="hills"></div><div class="figure"></div></div>
<div class="dim"></div>
<div class="label-tag">${tag}</div>
<div class="subtitle">I told you we'd make it back before dark.</div>
<div class="preview-note">Subtitle preview stays visible while you adjust it</div>
<div class="panel">
  ${sub ? sub : `  <div class="title">${svg('gear', 34)}Playback Settings</div>
  <div class="tabs" style="grid-template-columns:repeat(3,1fr)">
    <div class="tab ${activeTab==='Subtitles'?'active':''}">${svg('cc', 24)}Subtitles</div>
    <div class="tab ${activeTab==='Audio'?'active':''}">${svg('speaker', 24)}Audio</div>
    <div class="tab ${activeTab==='Playback'?'active':''}">${svg('playtab', 24)}Playback</div>
  </div>
`}
  ${body}
</div>
</body></html>`; }

const step = v => `<span class="delay"><b>‹</b><span style="min-width:70px;text-align:center;color:#3fb6ff">${v}</span><b>›</b></span>`;
const pm = v => `<span class="delay"><b>−</b>${v}<b>+</b></span>`;
const head = (t, first) => `<div class="head" ${first ? '' : 'style="margin-top:12px"'}>${t}</div>`;
const trackStep = v => `<span class="delay"><b>‹</b><span style="display:flex;align-items:center;gap:10px;color:#3fb6ff"><img class="flag" src="data:image/png;base64,${gb}">${v}</span><b>›</b></span>`;
const F = c => `data:image/png;base64,${fs.readFileSync('/home/user/Supernova/res/drawable-nodpi/region_'+c+'.png').toString('base64')}`;
const flagImg = c => `<img class="flag" src="${F(c)}">`;
const res = (c, name, meta, focus) => `<div class="row res ${focus ? 'focus' : ''}"><span class="ic">${flagImg(c)}</span><span class="label" style="margin-left:6px"><span class="fname">${name}</span><span class="meta">${meta}</span></span><span class="value">${svg('down', 28)}</span></div>`;
const trk = (c, name, meta, sel, focus) => `<div class="row ${focus ? 'focus' : ''}"><span class="ic">${flagImg(c)}</span><span class="label" style="margin-left:6px">${name}<span style="font-weight:400;color:#b9c7e8;font-size:17px"> · ${meta}</span></span><span class="tick">${sel ? '✓' : ''}</span></div>`;
const screens = {
  download: [`<div class="subhead"><span class="back">‹</span>Download Subtitles</div><div class="search">${svg('search', 24)}Nocturne S01E01</div>`,
    res('gb', 'Nocturne.2026.S01E01.Lazarus.Come.Forth.1080p.HEVC.x265', 'English', true) +
    res('gb', 'Nocturne.S01E01.WEB.H264', 'English · Hearing impaired') +
    res('fr', 'Nocturne.2026.S01E01.1080p.WEB', 'French') +
    res('es', 'Nocturne.S01E01.WEBRip.x264', 'Spanish (EU)'),
    'Proposal · Download Subtitles opens inside the same side panel · fits its results'],
  tracks: [`<div class="subhead"><span class="back">‹</span>Subtitle Track</div>`,
    trk('gb', 'English', 'SDH', false) + trk('gb', 'English', 'Forced', true, true) + trk('fr', 'French', 'Full', false) +
    trk('de', 'German', 'Full', false) + trk('es', 'Spanish (EU)', 'Full', false) + trk('mx', 'Spanish (LA)', 'Full', false) +
    trk('it', 'Italian', 'Full', false) + `<div style="text-align:center;color:#9cc9f0;font-size:17px;padding-top:10px">▾ 13 more</div>`,
    'Proposal · OK on Subtitle Track opens the full list in the same panel · Left/Right still steps through tracks'],
};
(async () => {
  const browser = await chromium.launch();
  const p = await browser.newPage({ viewport: { width: 1920, height: 1080 } });
  for (const [name, [sub, body, tag]] of Object.entries(screens)) {
    await p.setContent(page('', 'Subtitles', body, tag, sub), { waitUntil: 'load' });
    await p.screenshot({ path: path.join(dir, 'sub-' + name + '.png') });
  }
  await browser.close();
  console.log('rendered');
})();
