// Users and tags v5: the three gentle nudges (owner asked how they look, 15 Nov 2026). One full-size picture each.
// 1 Files › Library Care: a row for new titles without a user tag, Tag them on the focused row
// 2 What Tag them opens: Movies in select mode, only those titles, all ticked · 3 The tag list: Tag 8 titles
// 4 A library page with "12 hidden by Me" among its facts · 5 Search finds a hidden title; its owner is in the line.
// Made-up names and titles. Run: FONT_DIR=<roboto woff2 dir> node render-v5.js
const fs = require('fs');
const path = require('path');
const { chromium } = require('playwright');
const fontDir = process.env.FONT_DIR;
const font = w => `@font-face{font-family:R;font-weight:${w};src:url(data:font/woff2;base64,${fs.readFileSync(
  path.join(fontDir, `roboto-latin-${w}-normal.woff2`)).toString('base64')}) format('woff2')}`;
const svg = (d, s = 30, w = 1.8) => `<svg width="${s}" height="${s}" viewBox="0 0 24 24" fill="none" stroke="currentColor"
  stroke-width="${w}" stroke-linecap="round" stroke-linejoin="round">${d}</svg>`;
const I = {
  search: '<circle cx="11" cy="11" r="7"/><path d="M20 20l-4-4"/>',
  cog: '<circle cx="12" cy="12" r="3"/><path d="M12 2v3M12 19v3M4.2 4.2l2.1 2.1M17.7 17.7l2.1 2.1M2 12h3M19 12h3'
    + 'M4.2 19.8l2.1-2.1M17.7 6.3l2.1-2.1"/>',
  all: '<rect x="3.5" y="3.5" width="7" height="7" rx="1.5"/><rect x="13.5" y="3.5" width="7" height="7" rx="1.5"/>'
    + '<rect x="3.5" y="13.5" width="7" height="7" rx="1.5"/><rect x="13.5" y="13.5" width="7" height="7" rx="1.5"/>',
  both: '<circle cx="9" cy="8" r="3.5"/><path d="M2.5 20a6.5 6.5 0 0 1 13 0"/><path d="M15 4.6a3.5 3.5 0 1 1 0 6.8"/>'
    + '<path d="M17.5 14.2A6.5 6.5 0 0 1 21.5 20"/>',
  rocket: '<path d="M12 2.5c3.5 2.5 5 6 4.5 10L14.5 16h-5l-2-3.5C7 8.5 8.5 5 12 2.5z"/><circle cx="12" cy="9.5" r="2"/>'
    + '<path d="M9.5 16L7 21l3.5-2M14.5 16l2.5 5-3.5-2"/>',
  flower: '<circle cx="12" cy="8" r="2"/><circle cx="12" cy="4" r="2"/><circle cx="15.5" cy="6.5" r="2"/>'
    + '<circle cx="14.5" cy="10.5" r="2"/><circle cx="9.5" cy="10.5" r="2"/><circle cx="8.5" cy="6.5" r="2"/>'
    + '<path d="M12 12.5V22M12 18c-2.5 0-4-1.5-4.5-3.5M12 16.5c2.5 0 4-1.5 4.5-3.5"/>',
  cat: '<path d="M5 20V8l3 3h8l3-3v12z"/><circle cx="9.5" cy="15" r=".8"/><circle cx="14.5" cy="15" r=".8"/>',
  heart: '<path d="M12 20s-8-4.5-8-10.5A4.5 4.5 0 0 1 12 7a4.5 4.5 0 0 1 8 2.5C20 15.5 12 20 12 20z"/>',
  music: '<path d="M9 18V5l11-2v13"/><circle cx="6.5" cy="18" r="2.5"/><circle cx="17.5" cy="16" r="2.5"/>',
  game: '<rect x="2.5" y="7" width="19" height="11" rx="5"/><path d="M7 10.5v4M5 12.5h4"/><circle cx="16" cy="11.5"'
    + ' r=".9"/><circle cx="18" cy="13.5" r=".9"/>',
  sun: '<circle cx="12" cy="12" r="4"/><path d="M12 2v2.5M12 19.5V22M2 12h2.5M19.5 12H22M4.9 4.9l1.8 1.8M17.3 17.3l1.8'
    + ' 1.8M4.9 19.1l1.8-1.8M17.3 6.7l1.8-1.8"/>',
  moon: '<path d="M20 14.5A8 8 0 1 1 9.5 4a6.5 6.5 0 0 0 10.5 10.5z"/>',
  leaf: '<path d="M5 19c0-9 6-14 15-14 0 9-5 15-14 15"/><path d="M5 19l8-8"/>',
  ghost: '<path d="M5 21V10a7 7 0 0 1 14 0v11l-2.5-2-2.5 2-2-2-2 2-2.5-2z"/><circle cx="9.5" cy="10.5" r=".9"/>'
    + '<circle cx="14.5" cy="10.5" r=".9"/>',
  crown: '<path d="M3 8l4.5 4L12 5l4.5 7L21 8l-2 11H5z"/>',
  ball: '<circle cx="12" cy="12" r="9"/><path d="M12 7l4 3-1.5 4.5h-5L8 10z"/>',
  anchor: '<circle cx="12" cy="5" r="2"/><path d="M12 7v14M5 13a7 7 0 0 0 14 0M9 10h6"/>',
  paw: '<circle cx="7" cy="10" r="1.8"/><circle cx="11" cy="6.5" r="1.8"/><circle cx="15.5" cy="7.5" r="1.8"/>'
    + '<circle cx="18" cy="11.5" r="1.8"/><path d="M8.5 18c0-3 2-5.5 4.5-5.5s4 2 4 4.5-2 2.5-4 1.5-4 1.5-4.5-.5z"/>',
  edit: '<path d="M4 20h4L19 9l-4-4L4 16z"/><path d="M14 6l4 4"/>',
  tick: '<path d="M5 12.5l4.5 4.5L19 7.5"/>',
  chev: '<path d="M9 6l6 6-6 6"/>',
  tag: '<path d="M3 12V4h8l10 10-8 8z"/><circle cx="7.5" cy="8.5" r="1.5"/>',
  tree: '<path d="M12 2.5l5.5 6.5h-3.5l4.5 5.5h-3.5l3.5 4.5H5.5L9 14.5H5.5L10 9H6.5z"/><path d="M12 19v3"/>',
  star: '<path d="M12 3l2.7 5.6 6.1.9-4.4 4.3 1 6.1L12 17l-5.4 2.9 1-6.1L3.2 9.5l6.1-.9z"/>',
  plus: '<path d="M12 5v14M5 12h14"/>',
  play: '<path d="M7 4l13 8-13 8z"/>',
  dots: '<circle cx="5" cy="12" r="1.3"/><circle cx="12" cy="12" r="1.3"/><circle cx="19" cy="12" r="1.3"/>',
  restart: '<path d="M4 12a8 8 0 1 0 2.4-5.7"/><path d="M4 4v4h4"/>',
  watched: '<circle cx="12" cy="12" r="9"/><path d="M8 12.5l3 3 5-6"/>',
  fix: '<circle cx="11" cy="11" r="7"/><path d="M20 20l-4-4M8.5 11h5M11 8.5v5"/>',
  remove: '<path d="M5 7h14M10 7V4h4v3M7 7l1 13h8l1-13"/>',
  art: '<rect x="3" y="5" width="18" height="14" rx="2"/><circle cx="9" cy="10" r="2"/><path d="M21 16l-5-5-8 8"/>',
  name: '<path d="M4 7h16M4 12h10M4 17h7"/>',
  hide: '<path d="M3 3l18 18M10.6 6.2A9.6 9.6 0 0 1 12 6c6.4 0 10 6 10 6a17 17 0 0 1-3.3 3.9M6.6 7.6C3.8 9.3 2 12 2'
    + ' 12s3.6 6 10 6c1.7 0 3.2-.4 4.5-1"/>',
  home: '<path d="M4 11l8-7 8 7v9H4z"/><path d="M10 20v-5h4v5"/>',
  warn: '<path d="M12 4l9 16H3z"/><path d="M12 10v4M12 17h0"/>',
  select: '<rect x="4" y="4" width="16" height="16" rx="3"/><path d="M8 12.5l3 3 5-6"/>',
  filter: '<path d="M3 5h18l-7 8v6l-4 2v-8z"/>',
  back: '<path d="M10 6l-6 6 6 6M4 12h16"/>',
  space: '<path d="M4 10v4h16v-4"/>',
};
I.heartline = '<path d="M12 20s-8-4.5-8-10.5A4.5 4.5 0 0 1 12 7a4.5 4.5 0 0 1 8 2.5C20 15.5 12 20 12 20z"/>';
I.folder = '<path d="M3 6h6l2 2h10v11H3z"/>';
I.film = '<rect x="3" y="4" width="18" height="16" rx="2"/><path d="M7 4v16M17 4v16M3 9h4M3 15h4M17 9h4M17 15h4"/>';
I.match = '<circle cx="11" cy="11" r="7"/><path d="M20 20l-4-4M8 11l2 2 4-4"/>';
I.recheck = '<path d="M20 12a8 8 0 1 1-2.4-5.7"/><path d="M20 4v4h-4M8.5 12l2.5 2.5 4.5-5"/>';
I.scan = '<path d="M20 12a8 8 0 1 1-2.4-5.7"/><path d="M20 4v4h-4"/>';
I.server = '<rect x="4" y="4" width="16" height="7" rx="2"/><rect x="4" y="13" width="16" height="7" rx="2"/>';
I.device = '<rect x="3" y="4" width="18" height="12" rx="2"/><path d="M8 20h8M12 16v4"/>';
I.cloud = '<path d="M7 18a4.5 4.5 0 0 1-.5-9A6 6 0 0 1 18 10a4 4 0 0 1 0 8z"/>';
I.pulse = '<path d="M3 12h4l2-5 4 10 2-5h6"/>';
I.down = '<path d="M12 4v11M7 10l5 5 5-5M5 20h14"/>';
I.sort = '<path d="M7 4v16M4 7l3-3 3 3M17 20V4M14 17l3 3 3-3"/>';
I.az = '<path d="M4 6h9M4 12h6M4 18h3M17 5v14M14 16l3 3 3-3"/>';
I.list = '<path d="M9 6h11M9 12h11M9 18h11M4 6h.5M4 12h.5M4 18h.5"/>';
I.lib = '<path d="M5 4v16M9 4v16M13 5l4 15M19 4v16"/>';
const nav = (open, o = {}) => `<div class="nav"><span class="user">${svg(I.rocket, 36, 2)}</span>${['Home', 'Movies',
  'TV Shows', 'Documentaries'].map(t => `<span class="ni ${open === t ? (o.inBar ? 'dull' : 'open') : ''}">${t}</span>`)
  .join('')}<span class="ni files ${open === 'Files' ? 'open' : ''}">Files</span><span class="ni ${open === 'Search' ?
  'open' : ''}">${svg(I.search, 34)}</span><span class="ni">${svg(I.cog, 34)}</span><span class="clock">21:40</span></div>`;
const mi = (ic, t, sub, o = {}) => `<div class="mi ${o.focus ? 'focus' : ''}">${ic ? svg(I[ic], 32, 2) : ''}<div class="mt">
  <b>${t}</b>${sub ? `<small>${sub}</small>` : ''}</div><span class="end">${o.end || ''}${o.tick ? svg(I.tick, 28) : ''}
  ${o.chev ? svg(I.chev, 26) : ''}</span></div>`;
const grp = t => `<div class="mg"><span>${t}</span></div>`;
const foot = (ic, t, o = {}) => `<div class="sep"></div>${mi(ic, t, o.sub || '', o)}`;
const pill = t => `<span class="pill">${t}</span>`;
const careRow = (ic, t, sub, p, o = {}) => `<div class="crow ${o.focus ? 'focus' : ''}">${svg(I[ic], 34)}<div class="ct">
  <b>${t}</b><small>${sub}</small></div><div class="cp">${pill(p)}</div>${o.btn ? `<span class="rbtn">${svg(I.tag, 26)}
  ${o.btn}</span>` : svg(I.chev, 28)}</div>`;
const f1 = `${nav('Files')}<div class="tabs"><span class="tab">${svg(I.device, 30)}This Device</span><span class="tab">
  ${svg(I.server, 30)}Connections</span><span class="tab on">${svg(I.pulse, 30)}Library Care<i>9</i></span>
  <span class="tab">${svg(I.down, 30)}Transfers</span><span class="facts">${svg(I.film, 30)}<b>1,808</b> Videos ·
  ${svg(I.pulse, 30)}<b>9</b> To Check · ${svg(I.scan, 30)}12 min ago</span></div>
  <div class="sec" style="top:240px">${svg(I.heartline, 30)}Library health</div><div class="crows" style="top:300px">
  ${careRow('folder', 'Missing files', 'In your library but no longer found', '3 files')}
  ${careRow('film', 'Couldn’t be read', 'Files Supernova could not open', '2 files')}
  ${careRow('match', 'Not matched', 'Videos without a movie or TV show', '4 videos')}
  ${careRow('recheck', 'Check matches', 'Look every title up again; you see the changes first', 'Last: 2 Nov')}
  ${careRow('tag', 'New titles without a user tag', 'Added since 12 Nov', '8 titles', { focus: true, btn: 'Tag them' })}
  </div><div class="sec" style="top:830px">${svg(I.scan, 30)}Scanning</div><div class="crows" style="top:890px">
  <div class="crow">${svg(I.server, 34)}<div class="ct"><b>Scan one source</b><small>Look for new videos in one place only
  </small></div><span class="obtn" style="margin-left:auto">${svg(I.device, 26)}This Device</span><span class="obtn">
  ${svg(I.cloud, 26)}Put.io</span><span class="obtn">${svg(I.server, 26)}Backup #1</span></div></div>`;
const names = ['Harbour Lights', 'Paper Moons', 'The Quiet Field', 'Kite Season', 'North Window', 'Glass Garden',
  'Small Hours', 'Copper Coast', 'Late Trains', 'Snowed In', 'The Long Table', 'Blue Hour', 'Field Notes', 'Night Ferry'];
const tints = ['#6b4a42', '#3f5f62', '#4d4766', '#6a6040', '#4b5a4e', '#5a3f52', '#40506a'];
const poster = (i, o = {}) => `<div class="pc ${o.focus ? 'focus' : ''} ${o.dim ? 'dim' : ''}"><div class="pa" style=
  "background:radial-gradient(ellipse at 50% 40%,${tints[i % 7]},#1c1f25 80%)">${o.tick ? `<i>${svg(I.tick, 26, 2.6)}</i>`
  : ''}</div><div class="pcap"><b>${names[i % 14]}</b><small>${2018 + (i % 7)} · 1 h ${10 + (i * 7) % 49} min</small></div></div>`;
const selbar = (n, focusTag) => `<div class="tbar"><b class="selc">${n} selected</b><span class="tool">${svg(I.select, 28)}
  Select all</span><span class="tool ${focusTag ? 'focus' : ''}">${svg(I.tag, 28)}Tag selected</span><span class="tool">
  ${svg(I.tick, 28)}Done</span><span class="tfact">${svg(I.tag, 28)}New titles without a user tag</span></div>`;
const grid8 = (o = {}) => `<div class="pg">${Array.from({ length: 8 }, (_, i) => poster(i + 3, { tick: true, dim:
  o.dim })).join('')}</div>`;
const f2 = `${nav('Movies')}${selbar(8, true)}${grid8()}`;
const tagPanel = `<div class="panel side4">${grp('Users')}${mi('rocket', 'Me', '', { tick: true })}
  ${mi('flower', 'Alex', '')}${mi('both', 'Together', '')}${grp('Tags')}${mi('tree', 'Christmas', '')}
  ${mi('star', 'Classics', '')}${foot('plus', 'New tag')}<div class="apply focus">Tag 8 titles</div></div>`;
const f3 = `${nav('Movies')}${selbar(8, false)}${grid8({ dim: true })}${tagPanel}`;
const f4 = `${nav('Movies')}<div class="tbar"><span class="tool">${svg(I.filter, 28)}Filters</span><span class="tool">
  ${svg(I.sort, 28)}Title</span><span class="tool">${svg(I.az, 28)}A–Z</span><span class="tool">${svg(I.list, 28)}List view
  </span><span class="tfact">${svg(I.hide, 28)}12 hidden by Me</span><span class="tool">${svg(I.edit, 28)}Customise page
  </span></div><div class="pg">${Array.from({ length: 14 }, (_, i) => poster(i, { focus: i === 2 })).join('')}</div>`;
const keys = '1234567890qwertyuiopasdfghjkl/⇧zxcvbnm.@'.split('');
const sres = (art, t, sub, btn, o = {}) => `<div class="sr ${o.focus ? 'focus' : ''}"><span class="sa" style="background:
  ${art}"></span><div class="st2"><b>${t}</b><small>${sub}</small></div><span class="obtn">${svg(I.play, 24)}${btn}</span>
  </div>`;
const f5 = `${nav('Search')}<div class="sbox">${svg(I.search, 32)}harbour<i></i></div><div class="kgrid">${keys.map(k =>
  `<span class="key">${k}</span>`).join('')}<span class="key w2">#+=</span><span class="key w2">Space</span><span class="key w2">⌫</span><span class="key w2">Clear</span></div><div class="sec2">${svg(I.lib, 32)}In your library</div>
  <div class="srs">${sres('linear-gradient(150deg,#6b4a42,#1f2228)', 'Harbour Lights', '2019 · Movie', 'Play')}
  ${sres('linear-gradient(150deg,#3f5f62,#1f2228)', 'The Long Harbour', `2021 · Movie · <span class="own">${svg(I.flower,
  24, 2)}Alex</span>`, 'Play', { focus: true })}${sres('linear-gradient(150deg,#4d4766,#1f2228)', 'Harbour Street',
  '2016 · TV show · 2 seasons', 'Resume S1 E4')}</div>`;
const css = `${font(400)}${font(500)}*{box-sizing:border-box;margin:0}
body{width:1920px;height:1080px;font-family:R;color:#fff;background:#16181C;position:relative;overflow:hidden}
body:after{content:"";position:absolute;inset:0;border:6px solid #8a919c;z-index:99;pointer-events:none}
.nav{position:absolute;left:56px;right:56px;top:24px;height:76px;display:flex;align-items:center;gap:14px;font-size:33px;
color:#8f98a8;z-index:5}.user{width:68px;height:68px;border-radius:50%;display:inline-flex;align-items:center;
justify-content:center;color:#fff;box-shadow:inset 0 0 0 2px rgba(255,255,255,.3);margin-right:26px}
.ni{position:relative;display:inline-flex;align-items:center;height:60px;padding:0 18px}.ni.files{margin-left:auto}
.ni.open{color:#fff}.ni.open:after,.ni.dull:after{content:'';position:absolute;left:18px;right:18px;bottom:-2px;height:4px;
border-radius:2px;background:rgba(255,255,255,.3)}.ni.dull{color:#C9D3E6}.clock{margin-left:18px;color:#C9D3E6}
.tabs{position:absolute;left:96px;right:96px;top:128px;height:86px;display:flex;align-items:center;gap:52px;
font-size:31px;color:#C9D3E6;border-bottom:2px solid rgba(255,255,255,.1)}.tab{display:inline-flex;align-items:center;
gap:12px;height:86px;position:relative}.tab.on{color:#fff}.tab.on:after{content:'';position:absolute;left:0;right:0;
bottom:-2px;height:4px;border-radius:2px;background:#fff}.tab i{font-style:normal;font-size:22px;font-weight:500;
width:34px;height:34px;border-radius:50%;background:#fff;color:#0B0D12;display:inline-flex;align-items:center;
justify-content:center}.facts{margin-left:auto;display:flex;align-items:center;gap:10px;font-size:31px;color:#fff}
.facts b{font-weight:500}
.sec{position:absolute;left:96px;display:flex;align-items:center;gap:16px;font-size:32px;font-weight:500}
.sec svg,.sec2 svg{color:#C9D3E6}.crows{position:absolute;left:96px;right:96px}
.crow{display:flex;align-items:center;gap:32px;height:96px;padding:0 36px;border-bottom:2px solid rgba(255,255,255,.08);
border-radius:18px}.crow:last-child{border-bottom:none}.crow svg{color:#C9D3E6;flex:none}.ct b{display:block;
font-size:33px;font-weight:500}.ct small{font-size:26px;color:#C9D3E6}.cp{position:absolute;left:840px}
.crow{position:relative}.pill{display:inline-block;min-width:190px;text-align:center;font-size:26px;font-weight:500;
padding:6px 26px;border-radius:24px;box-shadow:inset 0 0 0 2px rgba(255,255,255,.45)}.crow>svg:last-child{margin-left:auto}
.crow.focus{background:#F4F6FB;color:#0B0D12;border-bottom-color:transparent}.crow.focus svg,.crow.focus small{color:#0B0D12}
.crow.focus .pill{box-shadow:inset 0 0 0 2px rgba(11,13,18,.45)}.rbtn{margin-left:auto;display:inline-flex;
align-items:center;gap:10px;height:62px;padding:0 28px;border-radius:14px;background:#0B0D12;color:#fff;font-size:28px;
font-weight:500}.crow.focus .rbtn svg{color:#fff}.obtn{display:inline-flex;align-items:center;gap:10px;height:60px;
padding:0 26px;border-radius:14px;box-shadow:inset 0 0 0 2px rgba(255,255,255,.4);font-size:28px;font-weight:500}
.obtn svg{color:#fff}.crow .obtn+.obtn{margin-left:-18px}
.tbar{position:absolute;left:96px;right:96px;top:128px;height:64px;display:flex;align-items:center;gap:30px;font-size:29px}
.tool{display:inline-flex;align-items:center;gap:10px;padding:10px 18px;border-radius:14px}.tool svg{color:#C9D3E6}
.tool.focus{background:#F4F6FB;color:#0B0D12}.tool.focus svg{color:#0B0D12}.selc{font-weight:500}
.tfact{margin-left:auto;display:inline-flex;align-items:center;gap:10px;color:#C9D3E6;font-size:27px}
.tfact svg{color:#C9D3E6}
.pg{position:absolute;left:96px;top:222px;display:grid;grid-template-columns:repeat(7,218px);gap:34px 34px}
.pc{width:218px;border-radius:14px;overflow:hidden;background:#22252c;box-shadow:inset 0 0 0 2px rgba(255,255,255,.6)}
.pa{height:325px;position:relative;box-shadow:inset 0 -2px 0 rgba(255,255,255,.2)}.pa i{position:absolute;right:12px;
top:12px;width:44px;height:44px;border-radius:50%;background:#F4F6FB;color:#0B0D12;display:flex;align-items:center;
justify-content:center}.pcap{padding:12px 14px 16px}.pcap b{display:block;font-size:24px;font-weight:500;white-space:nowrap;
overflow:hidden;text-overflow:ellipsis}.pcap small{font-size:21px;color:#C9D3E6}
.pc.focus{transform:scale(1.08);box-shadow:0 0 0 6px #fff;position:relative;z-index:2}.pc.dim{filter:brightness(.45)}
.menu,.panel{position:absolute;width:max-content;padding:14px;border-radius:24px;background:rgba(22,24,28,.95);
box-shadow:0 0 0 2px rgba(255,255,255,.14),0 30px 80px rgba(0,0,0,.7);z-index:6}.side4{right:56px;top:150px;width:600px}
.mi{display:flex;align-items:center;gap:20px;height:84px;padding:0 24px;border-radius:18px;min-width:480px}
.mi svg{color:#C9D3E6;flex:none}.mt b{display:block;font-size:29px;font-weight:500}.mt small{font-size:22px;color:#C9D3E6}
.mi .end{margin-left:auto;padding-left:24px;display:flex;align-items:center;gap:12px}
.sep{height:2px;background:rgba(255,255,255,.12);margin:8px 24px}.mg{display:flex;align-items:center;gap:18px;
margin:12px 24px 6px;font-size:26px;font-weight:500}.mg:before,.mg:after{content:'';flex:1;height:2px;
background:rgba(255,255,255,.16)}.apply{margin:14px 24px 6px;height:76px;border-radius:38px;background:rgba(255,255,255,.12);
display:flex;align-items:center;justify-content:center;font-size:28px;font-weight:500}.apply.focus{background:#F4F6FB;
color:#0B0D12}
.sbox{position:absolute;left:96px;top:150px;width:760px;height:84px;border-radius:18px;background:rgba(255,255,255,.08);
display:flex;align-items:center;gap:18px;padding:0 30px;font-size:36px}.sbox svg{color:#C9D3E6}.sbox i{width:3px;
height:40px;background:#fff;margin-left:-14px}.kgrid{position:absolute;left:96px;top:256px;width:760px;display:grid;
grid-template-columns:repeat(10,1fr);gap:10px}.key{height:64px;border-radius:12px;background:rgba(255,255,255,.06);
display:flex;align-items:center;justify-content:center;font-size:29px}.key.w2{grid-column:span 2}.kgrid .key.w2:nth-last-child(3){grid-column:span 4}
.sec2{position:absolute;left:930px;top:150px;display:flex;align-items:center;gap:16px;font-size:32px;font-weight:500}
.srs{position:absolute;left:930px;right:96px;top:214px}.sr{display:flex;align-items:center;gap:26px;height:96px;
padding:0 20px;border-radius:18px;border-bottom:2px solid rgba(255,255,255,.08)}.sr:last-child{border-bottom:none}
.sa{width:126px;height:70px;border-radius:10px;box-shadow:inset 0 0 0 2px rgba(255,255,255,.6);flex:none}
.st2 b{display:block;font-size:32px;font-weight:500}.st2 small{font-size:25px;color:#C9D3E6;display:inline-flex;
align-items:center;gap:8px}.own{display:inline-flex;align-items:center;gap:6px}.sr .obtn{margin-left:auto}
.sr.focus{background:#F4F6FB;color:#0B0D12;border-bottom-color:transparent}.sr.focus small,.sr.focus svg{color:#0B0D12}
.sr.focus .obtn{box-shadow:inset 0 0 0 2px rgba(11,13,18,.5)}`;
(async () => {
  const browser = await chromium.launch();
  const tab = await browser.newPage({ viewport: { width: 1920, height: 1080 } });
  const out = { '01-library-care-untagged': f1, '02-tag-them-select-mode': f2, '03-tag-8-titles': f3,
    '04-library-hidden-by-me': f4, '05-search-hidden-title': f5 };
  for (const [name, f] of Object.entries(out)) {
    await tab.setContent(`<html><head><style>${css}</style></head><body>${f}</body></html>`, { waitUntil: 'load' });
    await tab.evaluate(() => document.fonts.ready);
    await tab.screenshot({ path: path.join(__dirname, `05-v5-${name}.png`) });
  }
  await browser.close();
})();
