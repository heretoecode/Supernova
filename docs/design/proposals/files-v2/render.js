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
body{background:#111215;font-family:R,sans-serif;width:1920px;overflow:hidden;color:#fff}
.screen{position:relative;width:1920px;overflow:hidden}
.screen>canvas{position:absolute;left:0;top:0}
.border{position:absolute;inset:0;border:2px solid #c9ccd3;z-index:40}
.bar{position:absolute;top:0;left:0;right:0;height:84px;display:flex;align-items:center;padding:0 30px;font-size:30px;
 z-index:5}
.bar span{height:52px;padding:0 22px;border-radius:20px;display:flex;align-items:center}
.bar .open{background:rgba(255,255,255,.15);color:rgba(255,255,255,.72)}
.bar .sp{flex:1}
.bar .dot{width:12px;height:12px;padding:0;border-radius:6px;background:#fff;margin-right:14px}
.bar .ring{margin-right:14px}
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
.empty>svg{color:#c9d3e6;margin-bottom:22px}
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
  const sc=document.querySelector('.screen');const H=sc.offsetHeight;const cv=document.createElement('canvas');cv.width=1920;cv.height=H;
  sc.prepend(cv);const g=cv.getContext('2d'),r=rng(20261118),n=1920*H*20/1000;g.fillStyle='#111215';
  g.fillRect(0,0,1920,H);for(let i=0;i<n;i++){const x=r()*1920|0,y=r()*H|0,a=r()<1/7?.45:.07+r()*.15;
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
function tabs(open, focus, scan = 'Scan library') {
  const T = [['This Device', I.device], ['Connections', I.conn], ['Library Care', I.care, 5], ['Your Library', I.lib]];
  return `<div class="tabs">${T.map(([n, ic, badge]) => {
    const cls = n === focus ? 'f' : (n === open && !focus ? 'open' : '');
    return `<span class="${cls}">${svg(ic, 26)}${n}${badge ? `<i>${badge}</i>` : ''}</span>`;
  }).join('')}</div><div class="scanb"><span style="font-variant-numeric:tabular-nums">${scan}</span>${svg(I.scan, 26)}</div>
    <div class="tline"></div>`;
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
const FOLDERS = [['4K', 'Folder · 86 videos · 1.1 TB', 'In library'], ['Classics', 'Folder · 31 videos · 48.6 GB', 'In library'],
  ['New arrivals', 'Folder · 12 videos · 96.3 GB', 'Not added'], ['Tidelands (2024)', 'Folder · TV show · 16 episodes · 35.2 GB',
  'In library']];
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
      <div class="it">${svg(I.date, 26)}Date modified<span class="tk" style="visibility:hidden">${svg(I.tick, 24)}</span></div>
      <div class="it">${svg(I.size, 26)}Size<span class="tk" style="visibility:hidden">${svg(I.tick, 24)}</span></div></div>`
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
  } else if (o.kind === 'folderart') {
    pic = `<div class="art" style="background:${art(k + 5)}"></div>`;
  } else if (o.kind === 'other') {
    pic = `<div class="plain">${svg(I.film, 64)}</div>`;
  } else {
    pic = `<div class="art" style="background:${art(k + 3)}"></div>`;
  }
  const icon = o.kind.startsWith('folder') ? svg(I.folder, 26) : '';
  return `<div class="card${o.focus ? ' focus' : ''}">${pic}<div class="fade"></div><span class="cpill">${o.pill}</span>
    <div class="ct">${icon}<div><div class="c1">${o.name}</div><div class="c2">${o.line}</div></div></div>
    <div class="e"></div></div>`;
}

function f04() {
  const cards = [
    { kind: 'folder', mosaic: true, name: '4K', line: '86 videos · 1.1 TB', pill: 'In library' },
    { kind: 'folder', mosaic: true, name: 'Classics', line: '31 videos · 48.6 GB', pill: 'In library' },
    { kind: 'folder', name: 'New arrivals', line: '12 videos · 96.3 GB', pill: 'Not added' },
    { kind: 'folderart', name: 'Tidelands (2024)', line: 'TV show · 16 episodes', pill: 'In library', focus: true },
    { kind: 'lib', name: 'The Last Crossing', line: '2025 · 18.7 GB', pill: 'In library' },
    { kind: 'lib', name: 'Northern Lights · Directors Cut · Remastered', line: '2021 · 62.4 GB', pill: 'In library' },
    { kind: 'lib', name: 'Paper Harbour', line: '2024 · 9.8 GB', pill: 'In library' },
    { kind: 'other', name: 'family-holiday-2019.mp4', line: '3.1 GB', pill: 'Not added' },
  ];
  const menu = `<div class="menu" style="right:72px;top:530px">
    <div class="it f">${svg(I.open, 26)}Open</div>
    <div class="it">${svg(I.scan, 26)}Scan this folder</div><div class="it">${svg(I.fix, 26)}Fix match</div>
    <div class="it">${svg(I.exclude, 26)}Exclude</div></div>`;
  return bar() + tabs('This Device', null) + crumb('Movies', 'grid')
    + `<div class="grid">${cards.map(card).join('')}</div>` + menu
    + arrows(286) + '<div class="cap2">4 · Grid, and the menu on a one-title folder (Fix match offered)</div>';
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
  return bar('ring') + tabs('This Device', null, 'Scanning New arrivals · 40%') + crumb('Movies', 'list')
    + `<div class="list" style="top:270px">${listRows('New arrivals', { 'New arrivals': { pill: `${ring(24)}&nbsp;Adding · 40%`,
      line: 'Folder · 12 videos · 96.3 GB · 5 added so far' } })}</div>`
    + arrows() + '<div class="cap2">7 · Add to library (or Scan this folder): progress in the row</div>';
}

function f08() {
  return bar() + tabs('This Device', null) + crumb('Movies › Home videos', 'list')
    + `<div class="empty">${svg(I.empty, 64)}<div class="et">No videos in this folder</div>
    <div class="ed">4 other files are hidden while Videos only is on.</div>
    <div class="bt">${svg(I.file, 24)}Show all files</div></div>`
    + '<div class="cap2">8 · A folder with no videos</div>';
}

// ================= Round 2: Connections · Round 3: Library Care =================
Object.assign(I, {
  cloud: '<path d="M7 18h10a4 4 0 0 0 .6-8A6 6 0 0 0 6 9.5 4.3 4.3 0 0 0 7 18z"/>',
  server: '<rect x="3" y="4" width="18" height="7" rx="1.5"/><rect x="3" y="13" width="18" height="7" rx="1.5"/>'
    + '<path d="M7 7.5h.01M7 16.5h.01"/>',
  nas: '<rect x="5" y="3" width="14" height="18" rx="2"/><path d="M9 7h6M9 11h6M12 17h.01"/>',
  pc: '<rect x="3" y="4" width="18" height="12" rx="2"/><path d="M8 20h8M12 16v4"/>',
  plus: '<path d="M12 5v14M5 12h14"/>',
  radar: '<circle cx="12" cy="12" r="9"/><circle cx="12" cy="12" r="5"/><path d="M12 12l6-6"/>',
  sliders: '<path d="M4 6h10M18 6h2M4 12h4M12 12h8M4 18h12M20 18h0"/><circle cx="16" cy="6" r="2"/>'
    + '<circle cx="10" cy="12" r="2"/><circle cx="18" cy="18" r="2"/>',
  edit: '<path d="M14.5 5.5l4 4L8 20H4v-4z"/>',
  test: '<path d="M4 12h3l2-5 4 10 2-5h5"/>',
  trash: '<path d="M4 7h16M10 11v6M14 11v6M6 7l1 13h10l1-13M9 7V4h6v3"/>',
  eye: '<path d="M2 12s4-7 10-7 10 7 10 7-4 7-10 7S2 12 2 12z"/><circle cx="12" cy="12" r="3"/>',
  lock: '<rect x="5" y="11" width="14" height="10" rx="2"/><path d="M8 11V7a4 4 0 0 1 8 0v4"/>',
  smb: '<rect x="3" y="4" width="18" height="12" rx="2"/><path d="M8 20h8M12 16v4"/>',
  key: '<circle cx="8" cy="15" r="4"/><path d="M11 12l9-9M17 6l3 3"/>',
  heart: '<path d="M12 20s-7-4.4-7-10a4 4 0 0 1 7-2.6A4 4 0 0 1 19 10c0 5.6-7 10-7 10z"/>',
  missing: '<path d="M14 3H7a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h10a2 2 0 0 0 2-2V8z"/><path d="M14 3v5h5M10 13l4 4M14 13l-4 4"/>',
  unread: '<rect x="3" y="4" width="18" height="16" rx="2"/><path d="M3 9h18M9 13l6 4M15 13l-6 4"/>',
  question: '<circle cx="11" cy="11" r="7"/><path d="M20 20l-4-4M9 9a2 2 0 1 1 2.5 2c-.6.3-.5.8-.5 1.5M11 14.5v.1"/>',
  tofix: '<path d="M14.5 5.5l4 4L8 20H4v-4z"/><circle cx="18" cy="18" r="3.2"/>',
  hero: '<rect x="3" y="5" width="18" height="11" rx="2"/><path d="M8 20h8"/><circle cx="18" cy="17" r="4.2" '
    + 'fill="#111215"/><path d="M16 17h4"/>',
  check: '<path d="M20 12a8 8 0 1 1-2.3-5.7"/><path d="M20 4v4h-4M8.5 12.5l2.5 2.5 4.5-5"/>',
  history: '<circle cx="12" cy="12" r="9"/><path d="M12 7v5l3 2"/>',
  search: '<circle cx="11" cy="11" r="7"/><path d="M20 20l-4-4"/>',
  back: '<path d="M4 12a8 8 0 1 0 2.3-5.7"/><path d="M4 4v4h4"/>',
  ok: '<path d="M5 12.5l4.5 4.5L19 7.5"/>',
  mic: '<rect x="9" y="3" width="6" height="11" rx="3"/><path d="M5 11a7 7 0 0 0 14 0M12 18v3"/>',
  del: '<path d="M9 5h11a1 1 0 0 1 1 1v12a1 1 0 0 1-1 1H9l-6-7z"/><path d="M12 9.5l5 5M17 9.5l-5 5"/>',
  shift: '<path d="M12 4l8 8h-4.5v7h-7v-7H4z"/>',
});

const css2 = `
.hd{display:flex;align-items:center;gap:16px;font-size:32px;font-weight:500;height:44px;margin:64px 0 26px}
.hd.first{margin-top:0}
.hd svg{color:#c9d3e6;flex:none}
.rb{display:flex;gap:8px;flex:none;justify-content:flex-end;width:620px}
.rb.wide{width:1180px}
.rb span{height:52px;padding:0 20px;border-radius:20px;display:flex;align-items:center;gap:10px;font-size:24px;
 white-space:nowrap;flex:none;box-shadow:inset 0 0 0 1.5px rgba(11,13,18,.45)}
.rb span.f{background:#0b0d12;color:#fff;box-shadow:none}
.r .t small.bad{color:#ffb4a8}
.r.on .t small.bad{color:#8a1d10}
.form{position:absolute;left:52px;top:270px;width:900px}
.fr2{position:relative;display:flex;align-items:center;height:74px;padding:0 24px;border-radius:14px;font-size:29px}
.fr2 .lb{width:300px;color:#c9d3e6;flex:none}
.fr2 .vl{flex:1;min-width:0;display:flex;align-items:center;gap:12px;white-space:nowrap;overflow:hidden}
.fr2 .vl.ph{color:#8a93a3}
.fr2.typing{box-shadow:inset 0 0 0 2px rgba(255,255,255,.6)}
.fr2.on{background:#f4f6fb;color:#0b0d12}
.fr2.on .lb{color:#3a4256}
.fr2 .ln{position:absolute;left:24px;right:24px;bottom:0;height:1px;background:rgba(255,255,255,.1)}
.fr2.on .ln,.fr2.typing .ln{display:none}
.caret{display:inline-block;width:3px;height:36px;background:#fff;margin-left:-8px}
.sw{width:64px;height:36px;border-radius:18px;background:#f4f6fb;position:relative;flex:none}
.sw:after{content:'';position:absolute;right:4px;top:4px;width:28px;height:28px;border-radius:14px;background:#111215}
.sw.off{background:rgba(255,255,255,.22)}
.sw.off:after{left:4px;right:auto;background:#c9d3e6}
.fr2.on .sw,.sw.swf{background:#0b0d12}
.fr2.on .sw:after,.sw.swf:after{background:#fff}
.step{display:flex;align-items:center;gap:14px;font-weight:500}
.step i{font-style:normal;width:28px;display:flex;justify-content:center;opacity:0}
.fr2.on .step i{opacity:1}
.fbtns{display:flex;gap:12px;margin-top:30px;padding-left:24px}
.fbtns span{height:52px;padding:0 24px;border-radius:20px;display:flex;align-items:center;gap:10px;font-size:24px}
.fbtns .f{background:#f4f6fb;color:#0b0d12}
.fres{margin-top:18px;padding-left:24px;font-size:24px;color:#c9d3e6;display:flex;align-items:center;gap:10px}
.kbd{position:absolute;left:1016px;right:52px;top:270px}
.kbd .krow{display:flex;gap:10px;margin-bottom:12px}
.kbd .key{flex:1;height:64px;border-radius:16px;background:#24272d;display:flex;align-items:center;justify-content:center;
 font-size:28px;font-weight:500}
.kbd .key.w{flex:1.4;font-size:23px}
.kbd .key.sp{flex:2.6}
.kbd .key.f{background:#f4f6fb;color:#0b0d12}
.kbd .help{margin-top:30px;font-size:24px;color:#c9d3e6;line-height:36px}
.kbd .help b{color:#fff;font-weight:500}
.adv{position:absolute;left:52px;right:110px;top:270px}
.fm{position:absolute;left:52px;top:270px;width:900px}
.fbox{padding:0 0 0 4px}
.fbox .fn{font-size:30px;font-weight:500;white-space:nowrap;overflow:hidden;text-overflow:ellipsis}
.fbox .fl{font-size:24px;color:#c9d3e6;margin-top:6px}
.fbox .fl b{color:#fff;font-weight:500}
.sfield{height:76px;border-radius:20px;background:#24272d;display:flex;align-items:center;gap:16px;padding:0 24px;
 font-size:32px;margin-top:30px}
.sfield svg{color:#c9d3e6}
.kb2 .krow{display:flex;gap:10px;margin-top:12px}
.kb2 .key{flex:1;height:60px;border-radius:16px;background:#24272d;display:flex;align-items:center;justify-content:center;
 font-size:28px;font-weight:500}
.kb2 .key.w{flex:1.4;font-size:23px}
.kb2 .key.sp{flex:2.6}
.mres{position:absolute;left:1016px;right:110px;top:270px}
.thumb{width:128px;height:72px;border-radius:8px;flex:none;box-shadow:inset 0 0 0 1px #fff,inset 0 0 12px 3px rgba(0,0,0,.45)}
.r.on .thumb{box-shadow:inset 0 0 12px 3px rgba(0,0,0,.45)}
.r.mt{height:104px;font-size:32px}
`;

function hd(icon, title, first, extra = '') {
  return `<div class="hd${first ? ' first' : ''}">${svg(icon, 32)}${title}${extra}</div>`;
}

// buttons on a focused row: [label, icon, focused]
function rbtns(list, wide) {
  if (!list) {
    return `<span class="rb${wide ? ' wide' : ''}"></span>`;
  }
  return `<span class="rb${wide ? ' wide' : ''}">${list.map(([l, ic, f]) => `<span class="${f ? 'f' : ''}">${svg(ic, 22)}${l}</span>`).join('')}</span>`;
}

function crow(o) {
  const cls = ['r', 'std'];
  if (o.on) {
    cls.push('on');
  }
  if (o.last) {
    cls.push('last');
  }
  const pill = o.pill === undefined ? '' : `<span class="pill${o.pill ? '' : ' none'}">${o.pill || ''}</span>`;
  return `<div class="${cls.join(' ')}"><span class="ic">${svg(o.icon, 34)}</span><span class="t"><b>${o.name}</b>
    <small class="${o.bad ? 'bad' : ''}">${o.line}</small></span>${pill}${o.noArea ? '' : rbtns(o.btns, o.wide)}
    <span class="ch">${o.chev ? svg(I.chev, 28) : ''}</span><i class="ln"></i></div>`;
}

function connPage(opts = {}) {
  const C = [
    { icon: I.cloud, name: 'Put.io · alex.morgan', line: 'WebDAV · 1,204 videos', pill: 'Reachable', chev: true },
    { icon: I.server, name: 'Home server · alex', line: 'SFTP · 2 folders · 512 videos', pill: 'Reachable', chev: true },
    { icon: I.nas, name: 'Living-room NAS (Synology DS920+ in the cupboard) · media', line: 'SMB · 3 folders · 2,140 videos',
      pill: 'Not reachable', chev: true },
    { icon: I.plus, name: 'Add a connection', line: 'Computer or NAS, WebDAV, SFTP or FTP', chev: true, last: true },
  ];
  const F = [
    { icon: I.pc, name: 'MEDIA-PC', line: 'Windows computer · SMB', pill: 'New', chev: true },
    { icon: I.nas, name: 'backup-nas.local', line: 'NAS · SMB and SFTP', pill: 'New', chev: true, last: true },
  ];
  (opts.patch || []).forEach(([k, v]) => Object.assign(k < 10 ? C[k] : F[k - 10], v));
  const rows = (opts.remove != null ? C.filter((_, k) => k !== opts.remove) : C).map(crow).join('');
  let h = `<div class="list" style="top:212px">${hd(I.conn, 'Your connections', true)}${rows}
    ${hd(I.radar, 'Found on your network')}${F.map(crow).join('')}`;
  if (opts.tall) {
    h += `${hd(I.sliders, 'Advanced')}${crow({ icon: I.sliders, name: 'Network options',
      line: 'Windows shares, finding devices and SFTP', chev: true, last: true })}`;
  }
  return h + '</div>';
}

function f09() {
  return bar() + tabs('Connections', null) + connPage({ tall: true, patch: [[1, { on: true,
    btns: [['Open', I.open, true], ['Edit', I.edit], ['Test', I.test], ['Remove', I.trash]] }]] })
    + '<div class="cap2">9 · Connections (whole page)</div>';
}

function f10() {
  return bar() + tabs('Connections', null) + connPage({ patch: [[2, { on: true, bad: true,
    line: 'Can\'t reach it · check it\'s switched on and on this network',
    btns: [['Open', I.open], ['Edit', I.edit], ['Test', I.test, true], ['Remove', I.trash]] }]] })
    + '<div class="arrow" style="top:1010px">' + svg(I.down, 32) + '</div>'
    + '<div class="cap2">10 · Test: the answer in the row</div>';
}

function f11() {
  const toast = `<div class="toast">${svg(I.cloud, 32)}<span class="tx">Put.io · alex.morgan removed
    <small>Its videos leave your library. Nothing is deleted on Put.io.</small></span>
    <span style="display:flex;gap:8px;margin-left:10px"><span class="u f">Understood</span><span class="u">Undo</span></span>
    <i class="bar6"><b></b></i></div>`;
  return bar() + tabs('Connections', null) + connPage({ remove: 0, patch: [[1, { on: true,
    btns: [['Open', I.open], ['Edit', I.edit], ['Test', I.test], ['Remove', I.trash]] }]] }) + toast
    + '<div class="cap2">11 · Remove: at once, with Undo</div>';
}

const KROWS = [['1', '2', '3', '4', '5', '6', '7', '8', '9', '0'], ['q', 'w', 'e', 'r', 't', 'y', 'u', 'i', 'o', 'p'],
  ['a', 's', 'd', 'f', 'g', 'h', 'j', 'k', 'l', '/'], ['shift', 'z', 'x', 'c', 'v', 'b', 'n', 'm', '.', '@'],
  ['#+=', 'mic', 'Space', 'del', 'Clear'], ['Done', 'Cancel']];
const KIC = { shift: I.shift, mic: I.mic, del: I.del };

function keyboard(focus, help) {
  return `<div class="kbd">${KROWS.map((r, ri) => `<div class="krow">${r.map(k => {
    const c = ['key'];
    if (ri >= 4) {
      c.push(k === 'Space' ? 'sp' : 'w');
    }
    if (k === focus) {
      c.push('f');
    }
    return `<span class="${c.join(' ')}">${KIC[k] ? svg(KIC[k], 28) : k}</span>`;
  }).join('')}</div>`).join('')}<div class="help">${help}</div></div>`;
}

// field: [label, value, kind] kind: 'typing' | 'on' | 'ph' | 'sw' | 'swoff' | 'step' | 'pw'
function field([lb, vl, kind, on]) {
  const cls = ['fr2'];
  if (kind === 'typing') {
    cls.push('typing');
  }
  if (on) {
    cls.push('on');
  }
  let v = `<span style="overflow:hidden;text-overflow:ellipsis">${vl}</span>`;
  if (kind === 'typing') {
    v = `${vl}<i class="caret"></i>`;
  } else if (kind === 'sw' || kind === 'swoff') {
    v = `<span style="flex:1"></span><span class="sw${kind === 'swoff' ? ' off' : ''}"></span>`;
  } else if (kind === 'step') {
    v = `<span class="step"><i>‹</i>${vl}<i>›</i></span>`;
  } else if (kind === 'pw') {
    v = `<span style="flex:1">${vl}</span>${svg(I.eye, 30)}`;
  }
  return `<div class="${cls.join(' ')}"><span class="lb">${lb}</span><span class="vl${kind === 'ph' ? ' ph' : ''}">${v}</span>
    <i class="ln"></i></div>`;
}

function formCrumb(t) {
  return `<div class="crumb"><em>Connections ›</em>${t}</div>`;
}

function f12() {
  const fields = [['Type', 'WebDAV', 'step'], ['Name', 'Put.io', ''], ['Address', 'webdav.put.io', 'typing'],
    ['Encryption (HTTPS)', 'On', 'sw'], ['Port', '443 · the usual port with encryption', ''],
    ['Folder', '/ (everything)', 'ph'], ['Username', 'alex.morgan', ''], ['Password', '••••••••••', 'pw'],
    ['Remember password', 'On', 'sw']];
  return bar() + tabs('Connections', null) + formCrumb('New connection')
    + `<div class="form">${fields.map(field).join('')}<div class="fbtns"><span>${svg(I.test, 22)}Test connection</span>
    <span>${svg(I.ok, 22)}Save and browse</span></div></div>`
    + keyboard('o', '<b>WebDAV:</b> cloud storage and web servers (Put.io, Nextcloud).<br>Name fills in from the address; '
      + 'change it any time.<br>Back leaves without saving.')
    + '<div class="cap2">12 · New connection: the form and the one keyboard</div>';
}

function f13() {
  const fields = [['Type', 'SMB', 'step'], ['Name', 'Living-room NAS (Synology DS920+ in the cupboard)', ''],
    ['Address', 'nas.local', ''], ['Folders', 'Movies · TV Shows · Documentaries', ''],
    ['Username', 'media', ''], ['Password', '••••••••', 'pw'], ['Remember password', 'On', 'sw']];
  return bar() + tabs('Connections', null) + formCrumb('Edit · Living-room NAS')
    + `<div class="form">${fields.map(field).join('')}<div class="fbtns"><span class="f">${svg(I.test, 22)}Test connection
    </span><span>${svg(I.ok, 22)}Save</span></div><div class="fres">${svg(I.ok, 24)}Connected · 3 shared folders found
    </div></div>`
    + keyboard('', '<b>SMB:</b> shared folders on a Windows PC, Mac or NAS.<br>Back leaves without saving.')
    + '<div class="cap2">13 · Edit: the same form, filled in; Test answers under the buttons</div>';
}

function f14() {
  const fields = [['Username', 'alex', 'typing'], ['Password', '', 'pw'], ['Remember password', 'On', 'sw']];
  return bar() + tabs('Connections', null) + formCrumb('Found on your network › MEDIA-PC')
    + `<div class="form"><div class="fbox" style="margin-bottom:26px"><div class="fn">MEDIA-PC asks you to sign in</div>
    <div class="fl">Windows computer · SMB · it keeps its shared folders private</div></div>${fields.map(field).join('')}
    <div class="fbtns"><span>${svg(I.lock, 22)}Sign in and browse</span><span>${svg(I.plus, 22)}Add as a connection
    </span></div></div>`
    + keyboard('x', '<b>Only when needed:</b> a device that shares openly opens straight away.<br>Back leaves without '
      + 'signing in.')
    + '<div class="cap2">14 · Open on a found device that needs a sign-in</div>';
}

function f15() {
  const sw = (n, l, on, last, f) => `<div class="r std${f ? ' on' : ''}${last ? ' last' : ''}"><span class="t"><b>${n}</b>
    <small>${l}</small></span><span class="fr2" style="padding:0;height:auto;background:none"><span class="sw${on ? ''
    : ' off'}${f ? ' swf' : ''}"></span></span><i class="ln"></i></div>`;
  const chips = `<div class="r std last"><span class="t"><b>Name lookup order</b><small>How share names are found</small>
    </span><span style="display:flex;gap:6px;font-size:24px"><span style="height:52px;padding:0 22px;border-radius:20px;
    display:flex;align-items:center;background:rgba(255,255,255,.18)">Network first</span><span style="height:52px;
    padding:0 22px;display:flex;align-items:center">Local first</span></span><i class="ln"></i></div>`;
  return bar() + tabs('Connections', null) + formCrumb('Network options')
    + `<div class="adv">${hd(I.smb, 'Windows Shares (SMB)', true)}
    ${sw('Newer SMB method', 'SMB2 and later; turn off if an old share won\'t connect', true, false, true)}
    ${sw('Allow SMBv1', 'For very old shares only', false)}${chips}
    ${hd(I.radar, 'Finding Devices')}
    ${sw('Look over TCP', 'Finds most computers and NAS boxes', true)}
    ${sw('Look over UDP', 'Finds older Windows computers', true)}
    ${sw('Look over mDNS (Bonjour)', 'Finds Macs and many NAS boxes', true, true)}
    ${hd(I.key, 'SFTP')}${sw('Newer SFTP method', 'Turn off if an SFTP server won\'t connect', true, true)}</div>`
    + '<div class="cap2">15 · Connections › Network options (moved from Settings)</div>';
}

// ---------- Round 3: Library Care ----------
function careRows(focus) {
  const R = [
    { icon: I.missing, name: 'Missing files', line: 'In your library but no longer found', pill: '3 files', chev: true },
    { icon: I.unread, name: 'Couldn\'t be read', line: 'Files Supernova could not open', pill: 'None' },
    { icon: I.question, name: 'Not matched', line: 'Videos without a movie or TV show', pill: '4 videos', chev: true },
    { icon: I.tofix, name: 'To fix', line: 'Opened in Fix match and left as they were', pill: '2 titles', chev: true },
    { icon: I.check, name: 'Check matches', line: 'Look every title up again; you see the changes first',
      pill: 'Last: 17 Nov', chev: true, last: true },
  ];
  const K = [
    { icon: I.hero, name: 'Excluded from hero', line: 'Titles kept off Home\'s hero', pill: '46 titles', chev: true,
      last: true },
  ];
  R.forEach(r => {
    r.on = r.name === focus;
  });
  K.forEach(r => {
    r.on = r.name === focus;
  });
  return `${hd(I.heart, 'Library health', true)}${R.map(crow).join('')}${hd(I.lib, 'Kept by you')}${K.map(crow).join('')}`;
}

function scanPart(focusScan) {
  const btns = rbtns(focusScan ? [['This device', I.device], ['Backup #1', I.usb, true], ['Put.io', I.cloud],
    ['Home server', I.server], ['Living-room NAS', I.nas]] : null, true);
  const scan = `<div class="r std${focusScan ? ' on' : ''} last"><span class="ic">${svg(I.scan, 34)}</span><span class="t">
    <b>Scan one source</b><small>${focusScan ? 'Put.io: Scanning · 42% · Backup #1: Waiting' : 'Look for new videos in one place only'}</small>
    </span>${btns}<span class="ch"></span><i class="ln"></i></div>`;
  const rec = [
    { icon: I.cloud, name: 'Put.io', line: 'Today 18:28 · 3 new, 1 updated', pill: 'Finished' },
    { icon: I.nas, name: 'Living-room NAS', line: 'Today 17:28 · could not connect', pill: 'Failed', bad: true },
    { icon: I.usb, name: 'Backup #1', line: 'Today 16:02 · the drive was removed', pill: 'Stopped' },
    { icon: I.history, name: 'Show all scans', line: 'The last ten', chev: true, last: true },
  ];
  return `${hd(I.scan, 'Scanning')}${scan}${hd(I.history, 'Recent scans')}${rec.map(crow).join('')}`;
}

function f16() {
  return bar() + tabs('Library Care', null).replace('<i>5</i>', '<i>9</i>')
    + `<div class="list" style="top:212px">${careRows('To fix')}${scanPart(false)}</div>`
    + '<div class="cap2">16 · Library Care (whole page): To fix and Excluded from hero added</div>';
}

function subCrumb(t, sub) {
  return `<div class="crumb"><em>Library Care ›</em>${t}<em style="font-size:24px;margin-left:6px">${sub}</em></div>`;
}

function f17() {
  const rows = [
    { icon: I.film, name: 'Dine Out', line: 'TV show · 1 episode · opened in Fix match on 20 Nov from TV Shows', on: true,
      btns: [['Fix match', I.fix, true], ['It\'s right', I.ok]] },
    { icon: I.film, name: 'Kingdom of Glass and Long Shadows: The Second Part of the Saga', line:
      'Movie · opened in Fix match on 22 Nov from Details', last: true },
  ];
  return bar() + tabs('Library Care', null).replace('<i>5</i>', '<i>9</i>') + subCrumb('To fix', '2 titles')
    + `<div class="list" style="top:270px">${rows.map(crow).join('')}</div>`
    + '<div class="cap2">17 · To fix: kept until matched or marked "It\'s right"</div>';
}

function f18() {
  const keys = [['1', '2', '3', '4', '5', '6', '7', '8', '9', '0'], ['q', 'w', 'e', 'r', 't', 'y', 'u', 'i', 'o', 'p'],
    ['a', 's', 'd', 'f', 'g', 'h', 'j', 'k', 'l', '/'], ['shift', 'z', 'x', 'c', 'v', 'b', 'n', 'm', '.', '@'],
    ['#+=', 'mic', 'Space', 'del', 'Clear']];
  const kb = `<div class="kb2">${keys.map((r, ri) => `<div class="krow">${r.map(k => `<span class="key${ri === 4
    ? (k === 'Space' ? ' sp' : ' w') : ''}">${KIC[k] ? svg(KIC[k], 26) : k}</span>`).join('')}</div>`).join('')}</div>`;
  const file = `<div class="fbox">${hd(I.film, 'This file', true)}
    <div class="fn">Harbour.Wives.S01E02.Dinner.Party.1080p.mkv</div>
    <div class="fl">Backup #2 › TV Shows › Harbour Wives › Season 1 · 1.4 GB</div>
    <div class="fl">Matched now as <b>Dine Out (2023)</b> · TV show · 1 episode</div></div>`;
  const m = [
    { name: 'Harbour Wives', line: '2020 · TV show · 3 seasons · <b style="font-weight:500;color:inherit">Best match</b>', on: true,
      btns: [['Match and return', I.back, true], ['Match and stay', I.ok]] },
    { name: 'Harbour Wives: Reunion', line: '2023 · TV show · 1 season' },
    { name: 'Dine Out', line: '2023 · TV show · 1 season', last: true },
  ];
  const mrows = m.map((o, k) => `<div class="r mt${o.on ? ' on' : ''}${o.last ? ' last' : ''}"${o.on
    ? ' style="height:172px;align-items:flex-start;padding-top:16px"' : ''}><span class="thumb"
    style="background:${art(k)}"></span><span class="t"><b>${o.name}</b><small>${o.line}</small>${o.btns
    ? rbtns(o.btns).replace('class="rb"', 'class="rb" style="width:auto;justify-content:flex-start;margin-top:14px"')
    : ''}</span><i class="ln"></i></div>`)
    .join('');
  return bar() + tabs('Library Care', null).replace('<i>5</i>', '<i>9</i>')
    + `<div class="crumb">Fix match<em style="font-size:24px;margin-left:6px">from Dine Out · Details</em></div>`
    + `<div class="fm">${file}<div class="sfield">${svg(I.search, 30)}harbour wives<i class="caret"></i></div>${kb}</div>
    <div class="mres">${hd(I.question, 'Matches', true)}${mrows}</div>`
    + '<div class="cap2">18 · Fix match shows the file, where it is and what it\'s matched as now</div>';
}

function f19() {
  const EX = [['A Field of Kites', 'Movie · 2021 · excluded 2 Nov 2026'],
    ['Brightwater', 'TV show · 3 seasons · excluded 19 Nov 2026'], ['City of Birds', 'Movie · 2019 · excluded 14 Oct 2026'],
    ['Cold Open', 'Movie · 2023 · excluded 8 Nov 2026'], ['Desert Bloom', 'Movie · 2020 · excluded 30 Oct 2026'],
    ['Golden Mile', 'TV show · 1 season · excluded 12 Nov 2026']];
  const rows = [crow({ icon: I.search, name: '<span style="color:#c9d3e6">Search excluded titles</span>', line: '' })]
    .concat(EX.map(([n, l], k) => crow({ icon: I.film, name: n, line: l, on: k === 2, last: k === EX.length - 1,
      btns: k === 2 ? [['Include again', I.back, true]] : null })));
  return bar() + tabs('Library Care', null).replace('<i>5</i>', '<i>9</i>') + subCrumb('Excluded from hero', '46 titles · A–Z')
    + `<div class="tools"><span>${svg(I.back, 24)}Include all again</span></div>`
    + `<div class="list" style="top:270px">${rows.join('')}</div>` + arrows()
    + '<div class="cap2">19 · Excluded from hero (approved in principle 21 Nov)</div>';
}

function f20() {
  const rows = [
    { icon: I.film, name: 'The Salt Road (2023)', line: 'Living-room NAS › Movies · since 3 Nov', on: true,
      btns: [['Find it again', I.search, true], ['Remove', I.trash]] },
    { icon: I.film, name: 'Tidelands · S2 E4', line: 'Backup #1 › TV Shows · since 5 Nov' },
    { icon: I.film, name: 'Low Tide (2021)', line: 'Backup #1 › Movies · since 5 Nov', last: true },
  ];
  return bar() + tabs('Library Care', null).replace('<i>5</i>', '<i>9</i>') + subCrumb('Missing files', '3 files')
    + `<div class="list" style="top:270px">${rows.map(crow).join('')}<div style="margin-top:30px;padding-left:24px;
    font-size:24px;color:#c9d3e6;line-height:36px">Find it again looks for the same file and links it back, keeping where
    you stopped.<br>Remove takes it out of your library only.</div></div>`
    + '<div class="cap2">20 · Missing files (Couldn\'t be read and Not matched work the same way)</div>';
}

function f21() {
  return bar('ring') + tabs('Library Care', null).replace('<i>5</i>', '<i>9</i>')
    + `<div class="list" style="top:212px">${careRows('')}${scanPart(true)}</div>`
    + '<div class="cap2">21 · Scan one source: one button per source; pressed ones wait their turn</div>';
}

const ROUND23 = [['09-connections', f09, 1380], ['10-test-failed', f10], ['11-remove-undo', f11],
  ['12-new-connection', f12], ['13-edit-connection', f13], ['14-sign-in-found', f14], ['15-network-options', f15, 1380],
  ['16-library-care', f16, 1900], ['17-to-fix', f17], ['18-fix-match-file', f18], ['19-excluded-from-hero', f19],
  ['20-missing-files', f20], ['21-scan-one-source', f21, 1900]];


// ---------- Round 4: Your Library (read-only, nothing focusable) ----------
Object.assign(I, {
  total: '<path d="M4 20V10M10 20V4M16 20v-7M22 20H2"/>',
  pages: '<rect x="3" y="4" width="7" height="16" rx="1"/><rect x="14" y="4" width="7" height="16" rx="1"/>',
  space: '<rect x="3" y="6" width="18" height="12" rx="2"/><path d="M7 10h4M7 14h7"/>',
  pic: '<rect x="3" y="5" width="18" height="14" rx="2"/><path d="M3 15l5-5 4 4 3-3 6 6"/>',
  clock: '<circle cx="12" cy="12" r="9"/><path d="M12 7v5l3 2"/>',
  added: '<path d="M12 5v14M5 12h14"/>',
  tv: '<rect x="3" y="5" width="18" height="12" rx="2"/><path d="M8 21h8M12 17v4"/>',
  globe: '<circle cx="12" cy="12" r="9"/><path d="M3 12h18M12 3a14 14 0 0 1 0 18M12 3a14 14 0 0 0 0 18"/>',
});

const css4 = `
.stat{position:relative;display:flex;align-items:center;gap:24px;height:92px;padding:0 24px}
.stat .ic{display:flex;color:#c9d3e6;flex:none}
.stat .t{flex:1;min-width:0;display:flex;flex-direction:column;gap:4px;font-size:30px}
.stat .t small{font-size:23px;color:#c9d3e6}
.stat .v{font-size:30px;font-weight:500;white-space:nowrap;text-align:right}
.stat .v small{display:block;font-size:22px;font-weight:400;color:#c9d3e6;margin-top:4px}
.stat .ln{position:absolute;left:24px;right:24px;bottom:0;height:1px;background:linear-gradient(90deg,
 rgba(255,255,255,0),rgba(255,255,255,.09) 12%,rgba(255,255,255,.09) 88%,rgba(255,255,255,0))}
.stat.last .ln{display:none}
.note{font-size:23px;color:#c9d3e6;margin:-12px 0 14px 4px}
.qual{display:flex;gap:28px;font-size:28px;font-weight:500}
.qual span small{font-weight:400;color:#c9d3e6;font-size:22px;margin-right:8px}
`;

function stat(icon, name, sub, value, vsub, last) {
  return `<div class="stat${last ? ' last' : ''}"><span class="ic">${svg(icon, 32)}</span><span class="t">${name}
    ${sub ? `<small>${sub}</small>` : ''}</span><span class="v">${value}${vsub ? `<small>${vsub}</small>` : ''}</span>
    <i class="ln"></i></div>`;
}

function f22() {
  const qual = `<span class="qual"><span><small>4K</small>406</span><span><small>1080p</small>1,120</span>
    <span><small>720p</small>210</span><span><small>SD</small>72</span><span><small>HDR</small>229</span></span>`;
  const h = `<style>${css4}</style><div class="list" style="top:212px">
    ${hd(I.total, 'Total Library', true)}
    ${stat(I.lib, 'All library pages', '520 titles · 1,808 videos', '3.4 TB')}
    ${stat(I.watched, 'Watched', '318 of 520 titles', '61%')}
    ${stat(I.clock, 'Time to watch everything', 'Every unwatched title and episode', '2,140 hours')}
    ${stat(I.pic, 'Picture', 'Videos by quality', qual)}
    ${stat(I.added, 'Added this month', 'Since 1 Nov 2026', '23 titles', '', true)}
    ${hd(I.pages, 'By Library Page')}
    ${stat(I.film, 'Movies', '214 titles', '1.4 TB')}
    ${stat(I.tv, 'TV Shows', '210 titles · 1,412 episodes', '1.6 TB')}
    ${stat(I.globe, 'Documentaries', '96 titles', '0.4 TB', '', true)}
    ${hd(I.conn, 'By Source')}
    <div class="note">The videos in your library on each source, not the space left on it.</div>
    ${stat(I.internal, 'Internal storage', 'This device', '0.1 TB', '92 videos')}
    ${stat(I.usb, 'Backup #1 · Extreme Pro portable drive (exFAT)', 'USB drive', '0.9 TB', '412 videos')}
    ${stat(I.usb, 'Backup #2', 'USB drive', '1.2 TB', '268 videos')}
    ${stat(I.nas, 'Living-room NAS', 'SMB · 3 folders', '0.8 TB', '610 videos')}
    ${stat(I.cloud, 'Put.io', 'WebDAV', '0.3 TB', '302 videos')}
    ${stat(I.server, 'Home server', 'SFTP · 2 folders', '0.1 TB', '124 videos')}
    ${stat(I.conn, 'By connection type', 'USB 2.1 TB · SMB 0.8 TB · WebDAV 0.3 TB · SFTP 0.1 TB · this device 0.1 TB', '', '', true)}
    ${hd(I.space, 'Most Space Per Episode')}
    ${stat(I.tv, 'Frozen Frontier', '6 episodes · 14.4 GB', '2.4 GB', 'per episode')}
    ${stat(I.tv, 'Tidelands', '16 episodes · 35.2 GB', '2.2 GB', 'per episode')}
    ${stat(I.tv, 'Deep Blue Planet', '6 episodes · 12.6 GB', '2.1 GB', 'per episode', true)}
    </div>`;
  return bar() + tabs('Your Library', null) + h
    + `<div class="arrow faint" style="top:260px">${svg(I.up, 32)}</div>`
    + '<div class="cap2">22 · Your Library (whole page): read-only, Down / Up scroll; By Source added</div>';
}
ROUND23.push(['22-your-library', f22, 2460]);

const FRAMES = [['01-this-device', f01], ['02-drive-list', f02], ['03-sort-menu', f03], ['04-grid-folder-menu', f04],
  ['05-file-menu', f05], ['06-exclude-undo', f06], ['07-add-progress', f07], ['08-no-videos', f08]].concat(ROUND23);

(async () => {
  fs.mkdirSync(OUT, { recursive: true });
  const browser = await chromium.launch();
  const pg = await browser.newPage({ viewport: { width: 1920, height: 1080 }, deviceScaleFactor: 1 });
  for (const [name, fn, h = 1080] of FRAMES) {
    await pg.setViewportSize({ width: 1920, height: h });
    const html = `<!doctype html><html><head><meta charset="utf-8"><style>${css}${css2}</style></head><body>
      <div class="screen" style="height:${h}px">${fn()}<div class="border"></div></div>${speckle()}</body></html>`;
    await pg.setContent(html);
    await pg.evaluate(() => document.fonts.ready);
    await pg.screenshot({ path: path.join(OUT, `${name}.png`) });
  }
  await browser.close();
})();
