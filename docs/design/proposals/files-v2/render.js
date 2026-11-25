// Files v2, round 1 (25 Nov 2026): This Device and browsing a drive, redrawn on today's rules (FILES.md; RULES §1–4,
// §5, §7, §7b, §10–12, §16): speckle, the 0.158 top bar (Files grey, the signal dot on its left), the one tab
// behaviour (open tab underlined while focus is below), the one small-box shape (52 px, 20 px corners; outline =
// information), the artwork edge and 8% focus, arrows in the right margin, menus with outline and shadow, the Undo
// notice. 1920 x 1080 for the TV. Made-up drives, folders and titles.
// Run: FONT_DIR=<dir with roboto-latin-*-normal.woff2> node render.js
const fs = require('fs');
const path = require('path');
const { chromium } = require('playwright');

const fontDir = process.env.FONT_DIR;
const OUT = path.join(__dirname, 'tv');

function font(w) {
  const data = fs.readFileSync(path.join(fontDir, `roboto-latin-${w}-normal.woff2`)).toString('base64');
  return `@font-face{font-family:R;font-weight:${w};src:url(data:font/woff2;base64,${data}) format('woff2')}`;
}

function svg(d, s = 28) {
  return `<svg width="${s}" height="${s}" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8"
    stroke-linecap="round" stroke-linejoin="round">${d}</svg>`;
}

const I = {
  device: '<rect x="3" y="4" width="18" height="12" rx="2"/><path d="M8 20h8M12 16v4"/>',
  conn: '<rect x="3" y="4" width="18" height="6" rx="1.5"/><rect x="3" y="14" width="18" height="6" rx="1.5"/>'
    + '<path d="M7 7h.01M7 17h.01"/>',
  care: '<path d="M3 12h4l3-7 4 14 3-7h4"/>',
  lib: '<rect x="3" y="4" width="4" height="16" rx="1"/><rect x="9" y="4" width="4" height="16" rx="1"/>'
    + '<path d="M15.5 5.2l3.8-1 3.2 15.4-3.8 1z"/>',
  scan: '<path d="M20 12a8 8 0 1 1-2.3-5.7"/><path d="M20 4v4h-4"/>',
  usb: '<rect x="7" y="9" width="10" height="13" rx="2"/><path d="M9 9V3h6v6M10.5 5.5h.01M13.5 5.5h.01"/>',
  internal: '<rect x="4" y="3" width="16" height="18" rx="2"/><path d="M8 17h8M8 7h8"/>',
  folder: '<path d="M3 6a1 1 0 0 1 1-1h5l2 2h9a1 1 0 0 1 1 1v10a1 1 0 0 1-1 1H4a1 1 0 0 1-1-1z"/>',
  film: '<rect x="3" y="4" width="18" height="16" rx="2"/><path d="M7 4v16M17 4v16M3 9h4M3 15h4M17 9h4M17 15h4"/>',
  file: '<path d="M14 3H7a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h10a2 2 0 0 0 2-2V8z"/><path d="M14 3v5h5"/>',
  chev: '<path d="M9 6l6 6-6 6"/>',
  up: '<path d="M6 15l6-6 6 6"/>',
  down: '<path d="M6 9l6 6 6-6"/>',
  grid: '<rect x="4" y="4" width="7" height="7" rx="1"/><rect x="13" y="4" width="7" height="7" rx="1"/>'
    + '<rect x="4" y="13" width="7" height="7" rx="1"/><rect x="13" y="13" width="7" height="7" rx="1"/>',
  list: '<path d="M8 6h12M8 12h12M8 18h12M4 6h.01M4 12h.01M4 18h.01"/>',
  sort: '<path d="M7 4v16M4 17l3 3 3-3M14 6h7M14 12h5M14 18h3"/>',
  az: '<path d="M4 18l3-12 3 12M5 14h4M14 6h6l-6 12h6"/>',
  vonly: '<path d="M3 5h18l-7 8v6l-4-2v-4z"/>',
  name: '<path d="M4 7V5h16v2M12 5v14M9 19h6"/>',
  date: '<rect x="3" y="5" width="18" height="16" rx="2"/><path d="M3 10h18M8 3v4M16 3v4"/>',
  size: '<rect x="4" y="13" width="4" height="7"/><rect x="10" y="8" width="4" height="12"/><rect x="16" y="3" width="4" height="17"/>',
  tick: '<path d="M5 12.5l4.5 4.5L19 7.5"/>',
  open: '<path d="M3 6a1 1 0 0 1 1-1h5l2 2h9a1 1 0 0 1 1 1v3M3 6v12a1 1 0 0 0 1 1h13l4-8H8l-4 8"/>',
  add: '<circle cx="12" cy="12" r="9"/><path d="M12 8v8M8 12h8"/>',
  exclude: '<circle cx="12" cy="12" r="9"/><path d="M5.6 5.6l12.8 12.8"/>',
  play: '<path d="M7 4l13 8-13 8z"/>',
  info: '<circle cx="12" cy="12" r="9"/><path d="M12 11v6M12 7.5v.1"/>',
  watched: '<circle cx="12" cy="12" r="9"/><path d="M8 12.5l2.8 2.8L16.5 9.5"/>',
  fix: '<path d="M14.5 5.5l4 4L8 20H4v-4z"/><path d="M12.5 7.5l4 4"/>',
  empty: '<path d="M3 6a1 1 0 0 1 1-1h5l2 2h9a1 1 0 0 1 1 1v10a1 1 0 0 1-1 1H4a1 1 0 0 1-1-1z"/><path d="M9 13h6"/>',
};

const css = `${[400, 500, 700, 900].map(font).join('')}
*{box-sizing:border-box;margin:0;padding:0}
body{background:#111215;font-family:R,sans-serif;width:1920px;height:1080px;overflow:hidden;color:#fff}
.screen{position:relative;width:1920px;height:1080px;overflow:hidden}
.screen>canvas{position:absolute;left:0;top:0}
.border{position:absolute;inset:0;border:2px solid #c9ccd3;z-index:40}
.bar{position:absolute;top:0;left:0;right:0;height:84px;display:flex;align-items:center;padding:0 30px;font-size:30px;
 z-index:5}
.bar span{height:52px;padding:0 22px;border-radius:20px;display:flex;align-items:center}
.bar .open{background:rgba(255,255,255,.15);color:rgba(255,255,255,.72)}
.bar .sp{flex:1}
.bar .dot{width:12px;height:12px;padding:0;border-radius:6px;background:#fff;margin-right:-6px}
.cap2{position:absolute;right:52px;bottom:24px;color:#e8ebf2;font-size:24px;z-index:41;background:rgba(17,18,21,.85);
 padding:4px 12px;border-radius:8px}
.tabs{position:absolute;left:52px;top:104px;display:flex;gap:8px;font-size:28px}
.tabs span{position:relative;height:52px;padding:0 22px;border-radius:20px;display:flex;align-items:center;gap:12px}
.tabs .f{background:#f4f6fb;color:#0b0d12}
.tabs .open:after{content:'';position:absolute;left:22px;right:22px;bottom:-6px;height:3px;border-radius:2px;
 background:#fff}
.tabs i{font-style:normal;min-width:30px;height:30px;border-radius:15px;background:#fff;color:#0b0d12;font-size:19px;
 font-weight:700;display:flex;align-items:center;justify-content:center;padding:0 8px}
.tabs .f i{background:#0b0d12;color:#fff}
.scanb{position:absolute;right:52px;top:104px;height:52px;padding:0 22px;border-radius:20px;display:flex;align-items:center;
 gap:12px;font-size:26px}
.tline{position:absolute;left:52px;right:52px;top:170px;height:2px;background:linear-gradient(90deg,rgba(255,255,255,0),
 rgba(255,255,255,.35) 8%,rgba(255,255,255,.35) 92%,rgba(255,255,255,0))}
.crumb{position:absolute;left:52px;top:206px;display:flex;align-items:center;gap:14px;font-size:30px;font-weight:500}
.crumb em{font-style:normal;color:#c9d3e6;font-weight:400}
.tools{position:absolute;right:52px;top:198px;display:flex;gap:8px;font-size:24px}
.tools span{height:52px;padding:0 22px;border-radius:20px;display:flex;align-items:center;gap:10px}
.tools .f{background:#f4f6fb;color:#0b0d12}
.list{position:absolute;left:52px;right:110px}
.r{position:relative;display:flex;align-items:center;gap:24px;padding:0 24px;border-radius:14px}
.r.on{background:#f4f6fb;color:#0b0d12}
.r .ic{display:flex;color:#c9d3e6;flex:none}
.r.on .ic{color:#0b0d12}
.r .t{flex:1;min-width:0;display:flex;flex-direction:column;gap:4px}
.r .t b{font-weight:400;white-space:nowrap;overflow:hidden;text-overflow:ellipsis}
.r .t small{color:#c9d3e6;white-space:nowrap}
.r.on .t small{color:#3a4256}
.r .pill{flex:none;width:200px;height:52px;border-radius:20px;display:flex;align-items:center;justify-content:center;
 font-size:23px;box-shadow:inset 0 0 0 1.5px rgba(255,255,255,.45)}
.r .pill.none{box-shadow:none}
.r.on .pill{box-shadow:inset 0 0 0 1.5px rgba(11,13,18,.5)}
.r .ch{flex:none;width:28px;display:flex;color:#fff}
.r.on .ch{color:#0b0d12}
.r .ln{position:absolute;left:24px;right:24px;bottom:0;height:1px;background:linear-gradient(90deg,rgba(255,255,255,0),
 rgba(255,255,255,.14) 12%,rgba(255,255,255,.14) 88%,rgba(255,255,255,0))}
.r.on .ln,.r.last .ln{display:none}
.big{height:128px;font-size:36px}
.big .t small{font-size:26px}
.std{height:104px;font-size:32px}
.std .t small{font-size:24px}
.bar2{position:relative;height:8px;border-radius:4px;background:rgba(255,255,255,.18);width:560px;margin-top:8px}
.bar2 b{position:absolute;left:0;top:0;bottom:0;border-radius:4px;background:#fff}
.r.on .bar2{background:rgba(11,13,18,.18)}
.r.on .bar2 b{background:#0b0d12}
.arrow{position:absolute;right:56px;color:#fff}
.arrow.faint{color:rgba(255,255,255,.28)}
.menu{position:absolute;z-index:12;padding:12px 0;border-radius:20px;background:rgba(22,24,28,.99);
 box-shadow:0 0 0 1.5px rgba(255,255,255,.7),0 18px 48px 12px rgba(0,0,0,.85)}
.menu .g{display:flex;align-items:center;gap:14px;padding:10px 24px 6px;font-size:22px;color:#c9d3e6;white-space:nowrap}
.menu .g:before,.menu .g:after{content:'';flex:1;height:1px;background:rgba(255,255,255,.25)}
.menu .it{display:flex;align-items:center;gap:16px;height:60px;margin:0 10px;padding:0 16px;border-radius:14px;
 font-size:26px;white-space:nowrap}
.menu .it .tk{margin-left:auto;padding-left:24px;display:flex}
.menu .it.f{background:#f4f6fb;color:#0b0d12}
.toast{position:absolute;left:50%;bottom:44px;transform:translateX(-50%);display:flex;align-items:center;gap:22px;
 padding:20px 20px 30px 26px;border-radius:20px;background:rgba(22,24,28,.99);z-index:12;
 box-shadow:0 0 0 1.5px rgba(255,255,255,.7),0 18px 48px 12px rgba(0,0,0,.85);white-space:nowrap}
.toast .tx{display:flex;flex-direction:column;gap:4px;font-size:26px}
.toast .tx small{font-size:22px;color:#c9d3e6}
.toast .u{height:52px;padding:0 24px;border-radius:20px;display:flex;align-items:center;font-size:24px}
.toast .u.f{background:#f4f6fb;color:#0b0d12}
.toast .bar6{position:absolute;left:24px;right:24px;bottom:10px;height:3px;border-radius:2px;
 background:rgba(255,255,255,.18)}
.toast .bar6 b{position:absolute;left:0;top:0;bottom:0;width:70%;border-radius:2px;background:rgba(255,255,255,.7)}
.grid{position:absolute;left:52px;top:286px;display:grid;grid-template-columns:repeat(4,400px);column-gap:40px;
 row-gap:40px}
.card{position:relative;width:400px;height:225px;border-radius:14px;overflow:hidden}
.card.focus{transform:scale(1.08);z-index:3;box-shadow:0 0 0 4px #fff,0 12px 30px rgba(0,0,0,.5)}
.card .art{position:absolute;inset:0}
.card .mos{position:absolute;inset:0;display:grid;grid-template-columns:1fr 1fr;grid-template-rows:1fr 1fr;gap:2px}
.card .fade{position:absolute;left:0;right:0;bottom:0;height:62%;background:linear-gradient(0deg,rgba(8,9,11,.92),
 rgba(8,9,11,0))}
.card .e{position:absolute;inset:0;border-radius:14px;box-shadow:inset 0 0 0 1px #fff,inset 0 0 14px rgba(0,0,0,.4)}
.card.focus .e{box-shadow:none}
.card .ct{position:absolute;left:18px;right:18px;bottom:14px;display:flex;align-items:center;gap:12px}
.card .ct div{min-width:0}
.card .c1{font-size:24px;font-weight:500;white-space:nowrap;overflow:hidden;text-overflow:ellipsis}
.card .c2{font-size:19px;color:#c9d3e6;margin-top:4px;white-space:nowrap}
.card .cpill{position:absolute;left:10px;top:10px;height:40px;padding:0 16px;border-radius:16px;display:flex;
 align-items:center;font-size:19px;background:rgba(10,11,13,.72);box-shadow:inset 0 0 0 1.5px rgba(255,255,255,.6)}
.card .plain{position:absolute;inset:0;background:#1d2026;display:flex;align-items:center;justify-content:center;
 color:#8f98a8}
.empty{position:absolute;left:52px;right:52px;top:400px;display:flex;flex-direction:column;align-items:center;
 text-align:center}
.empty svg{color:#c9d3e6;margin-bottom:22px}
.empty .et{font-size:34px;font-weight:500}
.empty .ed{font-size:26px;color:#c9d3e6;margin-top:12px}
.empty .bt{margin-top:30px;height:52px;padding:0 24px;border-radius:20px;display:flex;align-items:center;gap:10px;
 font-size:24px;background:#f4f6fb;color:#0b0d12}
.ring{display:inline-flex}
`;

function speckle() {
  return `<script>{
  const rng=function(seed){return()=>{seed|=0;seed=seed+0x6D2B79F5|0;let t=Math.imul(seed^seed>>>15,1|seed);
    t=t+Math.imul(t^t>>>7,61|t)^t;return((t^t>>>14)>>>0)/4294967296}};
  const sc=document.querySelector('.screen');const cv=document.createElement('canvas');cv.width=1920;cv.height=1080;
  sc.prepend(cv);const g=cv.getContext('2d'),r=rng(20261118),n=1920*1080*20/1000;g.fillStyle='#111215';
  g.fillRect(0,0,1920,1080);for(let i=0;i<n;i++){const x=r()*1920|0,y=r()*1080|0,a=r()<1/7?.45:.07+r()*.15;
  g.fillStyle='rgba(255,255,255,'+a+')';g.fillRect(x,y,1,1)}}</script>`;
}

function ring(s = 28) {
  return `<span class="ring"><svg width="${s}" height="${s}" viewBox="0 0 36 36" fill="none" stroke="currentColor"
    stroke-width="4" stroke-linecap="round"><path d="M18 3a15 15 0 1 1-13.5 8.4" transform="rotate(30 18 18)"/>
    <path d="M18 9.5a8.5 8.5 0 1 1-7.6 4.7" transform="rotate(210 18 18)"/></svg></span>`;
}

// signal: 'dot' | 'ring' | ''
function bar(signal = 'dot') {
  const sig = signal === 'dot' ? '<span class="dot"></span>' : signal === 'ring' ? ring(28) : '';
  return `<div class="bar"><span>Home</span><span>Movies</span><span>TV Shows</span><span>Documentaries</span>
    <span class="sp"></span>${sig}<span class="open">Files</span><span>Search</span><span>Settings</span>
    <span>18:40</span></div>`;
}

// focus: tab name focused, or null (then the open tab is underlined)
function tabs(open, focus) {
  const T = [['This Device', I.device], ['Connections', I.conn], ['Library Care', I.care, 5], ['Your Library', I.lib]];
  return `<div class="tabs">${T.map(([n, ic, badge]) => {
    const cls = n === focus ? 'f' : (n === open && !focus ? 'open' : '');
    return `<span class="${cls}">${svg(ic, 26)}${n}${badge ? `<i>${badge}</i>` : ''}</span>`;
  }).join('')}</div><div class="scanb">${svg(I.scan, 26)}Scan library</div><div class="tline"></div>`;
}

function row(o) {
  const cls = ['r', o.size || 'std'];
  if (o.on) {
    cls.push('on');
  }
  if (o.last) {
    cls.push('last');
  }
  const bar2 = o.used != null ? `<span class="bar2"><b style="width:${o.used}%"></b></span>` : '';
  const pill = o.pill === undefined ? '' : `<span class="pill${o.pill ? '' : ' none'}">${o.pill || ''}</span>`;
  return `<div class="${cls.join(' ')}"><span class="ic">${svg(o.icon, o.size === 'big' ? 40 : 34)}</span>
    <span class="t"><b>${o.name}</b><small>${o.line}</small>${bar2}</span>${pill}
    <span class="ch">${o.chev ? svg(I.chev, 28) : ''}</span><i class="ln"></i></div>`;
}

// ---------- 01 This Device ----------
function f01() {
  const rows = [
    row({ size: 'big', icon: I.internal, name: 'Internal storage', line: 'This device · 310 videos · 14.2 GB free of 32 GB',
      used: 56, chev: true }),
    row({ size: 'big', icon: I.usb, name: 'Backup #1 · Extreme Pro portable drive (exFAT)',
      line: 'USB drive · 1,203 videos · 1.1 TB free of 2 TB', used: 45, chev: true, on: true }),
    row({ size: 'big', icon: I.usb, name: 'Backup #2', line: 'USB drive · 618 videos · 212 GB free of 4 TB', used: 95,
      chev: true, last: true }),
  ];
  return bar() + tabs('This Device', null) + `<div class="list" style="top:212px">${rows.join('')}</div>`
    + '<div class="cap2">1 · This Device</div>';
}

// ---------- browsing ----------
const FOLDERS = [['4K', 'Folder · 86 videos', 'In library'], ['Classics', 'Folder · 31 videos', 'In library'],
  ['New arrivals', 'Folder · 12 videos', 'Not added'], ['Home videos', 'Folder · no videos', 'Not added']];
const FILES = [['The Last Crossing (2025).mkv', '2025 · 18.7 GB', 'In library'],
  ['Northern Lights · Directors Cut · Remastered (2021) [2160p HDR10 DV].mkv', '2021 · 62.4 GB', 'In library'],
  ['Paper Harbour (2024).mkv', '2024 · 9.8 GB', 'In library'],
  ['family-holiday-2019.mp4', '3.1 GB', 'Not added']];

function listRows(focus, overrides = {}) {
  const all = FOLDERS.map(([n, l, p]) => ({ icon: I.folder, name: n, line: l, pill: p, chev: true }))
    .concat(FILES.map(([n, l, p]) => ({ icon: I.film, name: n, line: l, pill: p })));
  return all.map((o, k) => row({ ...o, ...(overrides[o.name] || {}), on: o.name === focus,
    last: k === all.length - 1 })).join('');
}

function crumb(path2, view) {
  return `<div class="crumb"><em>Backup #2 ›</em>${path2}</div>
    <div class="tools"><span>${svg(view === 'list' ? I.grid : I.list, 24)}${view === 'list' ? 'Grid' : 'List'}</span>
    <span>${svg(I.sort, 24)}Sort: Name ▾</span><span>${svg(I.az, 24)}A → Z</span>
    <span>${svg(I.vonly, 24)}Videos only</span></div>`;
}

function arrows(top = 300) {
  return `<div class="arrow faint" style="top:${top}px">${svg(I.up, 32)}</div>
    <div class="arrow" style="top:1010px">${svg(I.down, 32)}</div>`;
}

function f02() {
  return bar() + tabs('This Device', null) + crumb('Movies', 'list')
    + `<div class="list" style="top:270px">${listRows('Northern Lights · Directors Cut · Remastered (2021) [2160p HDR10 DV].mkv')}</div>`
    + arrows() + '<div class="cap2">2 · Inside a drive: list (the default)</div>';
}

function f03() {
  return bar() + tabs('This Device', null)
    + crumb('Movies', 'list').replace('<span>' + svg(I.sort, 24) + 'Sort: Name ▾</span>',
      `<span class="f">${svg(I.sort, 24)}Sort: Name ▾</span>`)
    + `<div class="list" style="top:270px">${listRows('')}</div>`
    + `<div class="menu" style="left:1290px;top:262px">
      <div class="it f">${svg(I.name, 26)}Name<span class="tk">${svg(I.tick, 24)}</span></div>
      <div class="it">${svg(I.date, 26)}Date modified</div>
      <div class="it">${svg(I.size, 26)}Size</div></div>`
    + arrows() + '<div class="cap2">3 · Sort drops down under its button</div>';
}

const ARTS = [['#7a3b2e', '#26120d'], ['#2f5c4a', '#0e1d17'], ['#5a4a7a', '#1a1426'], ['#8a6a2a', '#2a1f0a'],
  ['#2e4f78', '#0d1726'], ['#6d2f52', '#1f0c18'], ['#3f6b72', '#0f1e21']];

function art(k) {
  const a = ARTS[k % 7];
  return `radial-gradient(ellipse 60% 60% at 60% 35%,rgba(255,255,255,.16),transparent 60%),linear-gradient(155deg,
    ${a[0]},${a[1]})`;
}

function card(o, k) {
  let pic;
  if (o.kind === 'folder' && o.mosaic) {
    pic = `<div class="mos">${[0, 1, 2, 3].map(j => `<div style="background:${art(k + j)}"></div>`).join('')}</div>`;
  } else if (o.kind === 'folder') {
    pic = `<div class="plain">${svg(I.folder, 64)}</div>`;
  } else if (o.kind === 'other') {
    pic = `<div class="plain">${svg(I.film, 64)}</div>`;
  } else {
    pic = `<div class="art" style="background:${art(k + 3)}"></div>`;
  }
  const icon = o.kind === 'folder' ? svg(I.folder, 26) : '';
  return `<div class="card${o.focus ? ' focus' : ''}">${pic}<div class="fade"></div><span class="cpill">${o.pill}</span>
    <div class="ct">${icon}<div><div class="c1">${o.name}</div><div class="c2">${o.line}</div></div></div>
    <div class="e"></div></div>`;
}

function f04() {
  const cards = [
    { kind: 'folder', mosaic: true, name: '4K', line: '86 videos', pill: 'In library' },
    { kind: 'folder', mosaic: true, name: 'Classics', line: '31 videos', pill: 'In library' },
    { kind: 'folder', name: 'New arrivals', line: '12 videos', pill: 'Not added', focus: true },
    { kind: 'folder', name: 'Home videos', line: 'No videos', pill: 'Not added' },
    { kind: 'lib', name: 'The Last Crossing', line: '2025 · 18.7 GB', pill: 'In library' },
    { kind: 'lib', name: 'Northern Lights · Directors Cut · Remastered', line: '2021 · 62.4 GB', pill: 'In library' },
    { kind: 'lib', name: 'Paper Harbour', line: '2024 · 9.8 GB', pill: 'In library' },
    { kind: 'other', name: 'family-holiday-2019.mp4', line: '3.1 GB', pill: 'Not added' },
  ];
  const menu = `<div class="menu" style="left:1356px;top:290px">
    <div class="it f">${svg(I.open, 26)}Open</div><div class="it">${svg(I.add, 26)}Add to library</div>
    <div class="it">${svg(I.scan, 26)}Scan this folder</div><div class="it">${svg(I.exclude, 26)}Exclude</div></div>`;
  return bar() + tabs('This Device', null) + crumb('Movies', 'grid')
    + `<div class="grid">${cards.map(card).join('')}</div>` + menu
    + arrows(286) + '<div class="cap2">4 · Grid, and the menu on a folder</div>';
}

function f05() {
  const menu = `<div class="menu" style="right:390px;bottom:194px">
    <div class="g">Playback</div>
    <div class="it f">${svg(I.play, 26)}Play</div>
    <div class="g">Library</div>
    <div class="it">${svg(I.info, 26)}Details</div>
    <div class="it">${svg(I.watched, 26)}Mark as watched</div>
    <div class="it">${svg(I.fix, 26)}Fix match</div>
    <div class="g">Files &amp; Media</div>
    <div class="it">${svg(I.exclude, 26)}Exclude</div></div>`;
  return bar() + tabs('This Device', null) + crumb('Movies', 'list')
    + `<div class="list" style="top:270px">${listRows('Paper Harbour (2024).mkv')}</div>` + menu
    + arrows() + '<div class="cap2">5 · The menu on a file</div>';
}

function f06() {
  const toast = `<div class="toast">${svg(I.folder, 32)}<span class="tx">Classics excluded from your library
    <small>Its 31 videos leave your library pages. The files stay on the drive.</small></span>
    <span style="display:flex;gap:8px;margin-left:10px"><span class="u f">Understood</span><span class="u">Undo</span></span>
    <i class="bar6"><b></b></i></div>`;
  return bar() + tabs('This Device', null) + crumb('Movies', 'list')
    + `<div class="list" style="top:270px">${listRows('Classics', { Classics: { pill: 'Excluded' } })}</div>` + toast
    + arrows() + '<div class="cap2">6 · Exclude: at once, with Undo</div>';
}

function f07() {
  return bar('ring') + tabs('This Device', null) + crumb('Movies', 'list')
    + `<div class="list" style="top:270px">${listRows('New arrivals', { 'New arrivals': { pill: `${ring(24)}&nbsp;Adding · 40%`,
      line: 'Folder · 12 videos · 5 added so far' } })}</div>`
    + arrows() + '<div class="cap2">7 · Add to library (or Scan this folder): progress in the row</div>';
}

function f08() {
  return bar() + tabs('This Device', null) + crumb('Movies › Home videos', 'list')
    + `<div class="empty">${svg(I.empty, 64)}<div class="et">No videos in this folder</div>
    <div class="ed">4 other files are hidden while Videos only is on.</div>
    <div class="bt">${svg(I.file, 24)}Show all files</div></div>`
    + '<div class="cap2">8 · A folder with no videos</div>';
}

const FRAMES = [['01-this-device', f01], ['02-drive-list', f02], ['03-sort-menu', f03], ['04-grid-folder-menu', f04],
  ['05-file-menu', f05], ['06-exclude-undo', f06], ['07-add-progress', f07], ['08-no-videos', f08]];

(async () => {
  fs.mkdirSync(OUT, { recursive: true });
  const browser = await chromium.launch();
  const pg = await browser.newPage({ viewport: { width: 1920, height: 1080 }, deviceScaleFactor: 1 });
  for (const [name, fn] of FRAMES) {
    const html = `<!doctype html><html><head><meta charset="utf-8"><style>${css}</style></head><body>
      <div class="screen">${fn()}<div class="border"></div></div>${speckle()}</body></html>`;
    await pg.setContent(html);
    await pg.evaluate(() => document.fonts.ready);
    await pg.screenshot({ path: path.join(OUT, `${name}.png`) });
  }
  await browser.close();
})();
