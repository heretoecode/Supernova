// Trakt list as a Home row (owner, 16 Nov 2026): add a public list, paste its link, preview, the row on Home.
// Made-up names; service logos are stand-ins. Run: FONT_DIR=<roboto woff2 dir> node render-trakt.js
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

const key = (k, o = {}) => `<span class="k ${o.focus ? 'focus' : ''} ${o.wide ? 'wide' : ''}">${k}</span>`;
const keyboard = (typed, cls) => `<div class="panel kb ${cls}"><div class="typed">${typed}<i></i></div><div class="keys">
  ${'abcdefghijklmnopqrstuvwxyz'.split('').map(c => key(c, { focus: c === 't' })).join('')}${key(svg(I.space, 30), {
  wide: true })}${key('⌫')}${key('Done', { wide: true })}</div></div>`;
const crow = (t, top, n, o = {}) => `<div class="rowh" style="top:${top}px">${o.icon ? svg(I[o.icon], 30, 2) : ''}${t}
  </div><div class="row" style="top:${top + (o.focus === undefined ? 52 : 76)}px">${Array.from({ length: n }, (_, i) => `<div class="card ${o.focus === i ?
  'cf' : ''}" style="width:400px;height:225px;background:linear-gradient(150deg,${['#6b4a42', '#3f5f62', '#5a3f52',
  '#6a6040', '#4b5a4e'][(i + (o.shift || 0)) % 5]},#1f2228)"></div>`).join('')}</div>`;
const chips = (opts, on) => `<span class="chips">${opts.map(o => `<span class="${o === on ? 'chip' : ''}">${o}</span>`)
  .join('')}</span>`;
const step = v => `<span class="stepv">‹ <b>${v}</b> ›</span>`;

I.link = '<path d="M10 14a4 4 0 0 0 5.7 0l3-3a4 4 0 0 0-5.7-5.7l-1 1"/><path d="M14 10a4 4 0 0 0-5.7 0l-3 3a4 4 0 0 0 5.7 5.7l1-1"/>';
I.phone = '<rect x="7" y="2.5" width="10" height="19" rx="2"/><path d="M11 18.5h2"/>';
I.listic = '<path d="M9 6h11M9 12h11M9 18h11"/><circle cx="4.5" cy="6" r="1"/><circle cx="4.5" cy="12" r="1"/><circle cx="4.5" cy="18" r="1"/>';
I.trakt = '<circle cx="12" cy="12" r="9"/><path d="M8 12.5l3 3 5-6"/>';
const svc = (n, c) => `<span class="svc" style="background:${c}">${n}</span>`;
const N = svc('N', '#b8141c'), P = svc('P', '#1a6fb0'), D = svc('D+', '#14306e');
const homeBase = (dim) => `${nav('Home')}<div class="hero" style="background:linear-gradient(100deg,rgba(14,16,20,.92),
  rgba(14,16,20,.3) 60%),radial-gradient(ellipse at 72% 45%,#6a5a48,#2a2620 75%)"><div class="hl">SNOWED IN</div>
  <div class="hm">PG · 2024 · 1 h 41 min · Comedy, Family</div></div>${crow('Continue Watching', 800, 5)}
  ${dim ? '<div class="dim"></div>' : ''}`;
const custom = (dim, focusAdd) => `<div class="panel side tall2 ${dim ? 'behind' : ''}">${grp('Your Rows')}
  ${mi('leaf', 'Nature Nights', 'Documentaries · newest first', { chev: true })}
  ${mi('tree', 'Christmas Films', 'Movies · Christmas · A–Z', { chev: true })}
  ${mi('plus', 'Add a row', '', { focus: focusAdd })}${grp('Built-In Rows')}
  ${mi('play', 'Continue Watching', 'Unfinished movies and next episodes', { end: '<span class="sw on"></span>' })}
  ${mi('scan', 'Recently Added', 'Newest in your library', { end: '<span class="sw on"></span>' })}</div>`;
const kindList = `<div class="menu kind">${mi('lib', 'From your library', 'Pick genres, years and more')}
  ${mi('trakt', 'A Trakt list', 'What you can stream from a public list', { focus: true })}</div>`;
const f1 = `${homeBase(true)}${custom(true)}${kindList}`;
const traktPanel = (o = {}) => `<div class="panel side2 tp ${o.dim ? 'behind' : ''}">${grp('The List')}
  ${mi('link', 'List link', o.found ? 'Films to Watch · by moviefan · 20 titles' : 'Paste the link of a public Trakt list',
  { end: o.found ? '<span class="val">Found</span>' : '<span class="val soft">Not set</span>', chev: true,
  focus: o.focus === 'link' })}${o.found ? `<div class="prev"><b>12 of 20 on your services</b><span>${N}5${P}4${D}3
  </span><small>Titles in your library are left out. Checked again every day.</small></div>` : ''}
  ${grp('The Row')}${mi('name', 'Row name', '', { end: `<span class="val">${o.found ? 'Films to Watch' : '—'}</span>`,
  chev: true, focus: o.focus === 'name' })}${mi('leaf', 'Icon', '', { end: '<span class="val">Automatic</span>',
  chev: true })}${mi('listic', 'Maximum items', '', { end: chips(['10', '15', '20', '25', '30'], '20') })}
  <div class="apply ${o.focus === 'add' ? 'focus' : ''} ${o.found ? '' : 'off'}">Add row</div></div>`;
const kbx = `<div class="panel kb kbt"><div class="typed sm">trakt.tv/users/moviefan/lists/films-to-watch<i></i></div>
  <div class="keys">${'abcdefghijklmnopqrstuvwxyz'.split('').map(c => key(c)).join('')}${key(svg(I.space, 30), {
  wide: true })}${key('⌫')}${key(`${svg(I.phone, 28)} Phone`, { wide: true, focus: true })}${`<span class="k d2">Done</span>`}
  </div><p class="kbh">Pasted from your phone</p></div>`;
const f2 = `${homeBase(true)}${custom(true)}${traktPanel({ dim: true })}${kbx}`;
const f3 = `${homeBase(true)}${custom(true)}${traktPanel({ found: true, focus: 'add' })}`;
const names = ['Glass Garden', 'North Window', 'Copper Coast', 'Late Trains', 'Blue Hour', 'Field Notes'];
const tcard = (i, s, o = {}) => `<div class="tc ${o.focus ? 'tf' : ''}"><div class="ta" style="background:linear-gradient(
  150deg,${['#5a3f52', '#3f5f62', '#6b4a42', '#4d4766', '#6a6040', '#4b5a4e'][i]},#1f2228)">${s}</div>
  <div class="tcap"><b>${names[i]}</b><small>${2019 + i} · ${i % 2 ? 'TV show' : 'Movie'}</small></div></div>`;
const f4 = `${nav('Home')}<div class="hero" style="background:linear-gradient(100deg,rgba(14,16,20,.92),rgba(14,16,20,.3)
  60%),radial-gradient(ellipse at 72% 45%,#6a5a48,#2a2620 75%)"><div class="hl">SNOWED IN</div><div class="hm">PG · 2024
  · 1 h 41 min · Comedy, Family</div></div><div class="rowh" style="top:800px">${svg(I.listic, 30, 2)}Films to Watch
  <span class="rmeta">12 on your services</span></div><div class="trow">${[N, P, D, N, P, N].map((s, i) => tcard(i, s,
  { focus: i === 0 })).join('')}</div>`;
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
.sr.focus .obtn{box-shadow:inset 0 0 0 2px rgba(11,13,18,.5)}
*{box-sizing:border-box;margin:0}
body{width:1920px;font-family:R;color:#fff;background:#8a919c;position:relative}
body:after{content:"";position:absolute;inset:0;border:6px solid #8a919c;z-index:99;pointer-events:none}
.grid{display:grid;grid-template-columns:960px 960px}.cell{width:960px;height:540px;overflow:hidden;border:4px solid #8a919c}
.frame{width:1920px;height:1080px;position:relative;overflow:hidden;background:#16181C;}
.nav{position:absolute;left:56px;right:56px;top:24px;height:76px;display:flex;align-items:center;gap:14px;
font-size:33px;color:#8f98a8;z-index:5}
.user{width:68px;height:68px;border-radius:50%;display:inline-flex;align-items:center;justify-content:center;color:#fff;
box-shadow:inset 0 0 0 2px rgba(255,255,255,.3);margin-right:26px}.user.focus{background:#F4F6FB;color:#0B0D12;
box-shadow:none}.user.dull{box-shadow:inset 0 0 0 2px rgba(255,255,255,.3),0 6px 0 -2px rgba(255,255,255,.3)}
.ni{position:relative;display:inline-flex;align-items:center;height:60px;padding:0 18px}.ni.files{margin-left:auto}
.nav .clock{margin-left:18px;color:#C9D3E6}.dull{color:#C9D3E6}.ni.dull:after{content:'';position:absolute;left:18px;
right:18px;bottom:-2px;height:4px;border-radius:2px;background:rgba(255,255,255,.3)}
.hero{position:absolute;left:72px;top:118px;width:1776px;height:640px;border-radius:26px;box-shadow:inset 0 0 0 2px
rgba(255,255,255,.6)}.hero.tall{height:820px}
.hl{position:absolute;left:60px;top:200px;font-size:76px;letter-spacing:8px;font-weight:500;color:#f1ead8}
.hm{position:absolute;left:62px;top:300px;font-size:28px;color:#e8edf6}
.tagsl{position:absolute;left:62px;top:350px;display:flex;gap:14px}.tagsl span{display:inline-flex;align-items:center;
gap:10px;font-size:25px;padding:6px 16px;border-radius:16px;background:rgba(22,24,28,.7);box-shadow:inset 0 0 0 2px
rgba(255,255,255,.3)}
.hd{position:absolute;left:62px;top:416px;width:900px;font-size:27px;line-height:1.45;color:#e8edf6}
.hb{position:absolute;left:60px;bottom:60px;display:flex;gap:16px}.btn{display:inline-flex;align-items:center;gap:12px;
height:68px;padding:0 30px;border-radius:34px;font-size:28px;font-weight:500;background:rgba(22,24,28,.72)}
.btn.focus{background:#F4F6FB;color:#0B0D12}
.rowh{position:absolute;left:96px;top:800px;font-size:30px;font-weight:500}.row{position:absolute;left:96px;top:852px;
display:flex;gap:24px}.card{border-radius:14px;box-shadow:inset 0 0 0 2px rgba(255,255,255,.6)}
.menu,.panel{position:absolute;width:max-content;padding:14px;border-radius:24px;background:rgba(22,24,28,.95);
box-shadow:0 0 0 2px rgba(255,255,255,.14),0 30px 80px rgba(0,0,0,.7);z-index:6}
.um{left:44px;top:100px}.side{right:56px;top:110px;width:640px}.side2{right:120px;top:170px;width:640px;z-index:7}
.side3{right:150px;top:470px;z-index:7}.picker{right:150px;top:150px;z-index:7;width:700px}
.kbuser{right:740px;top:130px;z-index:8}.kbtag{left:860px;top:300px;z-index:8}
.more{left:300px;top:250px}.tags{left:560px;top:300px;z-index:7}.side4{right:56px;top:150px;width:600px;z-index:7}
.filt{left:96px;top:196px;width:640px}.warn{left:50%;top:50%;transform:translate(-50%,-50%);width:760px;padding:40px;
z-index:8}
.behind{transform:scale(.96);transform-origin:right top;filter:brightness(.45)}.um.behind{transform-origin:left top}
.more.behind{transform-origin:left top}.tags.behind{transform-origin:left top}
.mi{display:flex;align-items:center;gap:20px;height:84px;padding:0 24px;border-radius:18px;min-width:480px}
.mi svg{color:#C9D3E6;flex:none}.mt b{display:block;font-size:29px;font-weight:500}.mt small{font-size:22px;color:#C9D3E6}
.mi .end{margin-left:auto;padding-left:24px;display:flex;align-items:center;gap:12px}.val{font-size:27px;font-weight:500}
.mi.focus{background:#F4F6FB;color:#0B0D12}.mi.focus svg,.mi.focus small{color:#0B0D12}
.sep{height:2px;background:rgba(255,255,255,.12);margin:8px 24px}
.mg{display:flex;align-items:center;gap:18px;margin:12px 24px 6px;font-size:26px;font-weight:500}.mg:before,.mg:after{
content:'';flex:1;height:2px;background:rgba(255,255,255,.16)}
.st{font-size:24px;padding:4px 14px;border-radius:14px;box-shadow:inset 0 0 0 2px rgba(255,255,255,.35)}
.st.off{box-shadow:none;color:#C9D3E6;padding:4px 0}.mi.focus .st{box-shadow:inset 0 0 0 2px rgba(11,13,18,.4)}
.icg{display:grid;grid-template-columns:repeat(8,72px);gap:12px;padding:8px 24px 14px}.ic{width:72px;height:72px;
border-radius:16px;display:flex;align-items:center;justify-content:center;color:#e8edf6;box-shadow:inset 0 0 0 2px
rgba(255,255,255,.12)}.ic.on{box-shadow:inset 0 0 0 3px rgba(255,255,255,.7)}.ic.focus{background:#F4F6FB;color:#0B0D12}
.kb{width:820px;padding:22px}.typed{height:72px;border-radius:14px;background:rgba(255,255,255,.08);font-size:32px;
display:flex;align-items:center;padding:0 22px}.typed i{width:3px;height:36px;background:#fff;margin-left:2px}
.keys{display:grid;grid-template-columns:repeat(9,1fr);gap:10px;margin-top:16px}.k{height:66px;border-radius:12px;
display:flex;align-items:center;justify-content:center;font-size:28px;background:rgba(255,255,255,.06)}
.k.wide{grid-column:span 3}.k.focus{background:#F4F6FB;color:#0B0D12}
.dim{position:absolute;inset:0;background:rgba(10,11,14,.6);z-index:7}
.wt{display:flex;align-items:center;gap:16px;font-size:34px;font-weight:500}.wt svg{color:#fff}.warn p{font-size:27px;
line-height:1.45;color:#C9D3E6;margin:18px 0 30px}.wb{display:flex;gap:16px}.wbtn{height:72px;padding:0 34px;
border-radius:36px;display:inline-flex;align-items:center;font-size:28px;font-weight:500;background:rgba(255,255,255,.1)}
.wbtn.focus{background:#F4F6FB;color:#0B0D12}
.selbar{position:absolute;left:96px;right:96px;top:128px;display:flex;align-items:center;gap:26px;font-size:28px}
.tool{display:inline-flex;align-items:center;gap:10px;font-size:27px;padding:10px 18px;border-radius:14px}
.tool svg{color:#C9D3E6}.tf{background:rgba(255,255,255,.14)}.toolbar{position:absolute;left:96px;top:128px}
.pgrid{position:absolute;left:96px;top:210px;display:grid;grid-template-columns:repeat(6,250px);gap:30px}
.pst{position:relative;width:250px;height:375px;border-radius:14px;box-shadow:inset 0 0 0 2px rgba(255,255,255,.6)}
.pst.focus{box-shadow:0 0 0 6px #fff}.pst i{position:absolute;right:12px;top:12px;width:48px;height:48px;border-radius:50%;
background:#F4F6FB;color:#0B0D12;display:flex;align-items:center;justify-content:center}
.pst:not(.sel){opacity:.55}.apply{margin:14px 24px 6px;height:76px;border-radius:38px;background:rgba(255,255,255,.12);
display:flex;align-items:center;justify-content:center;font-size:28px;font-weight:500}
.hint{font-size:22px;color:#9aa3b4;margin:12px 24px 0}
.del{left:520px;top:60px;width:880px;padding:30px 26px;z-index:8}.del .wt{margin:0 24px 6px}
.del.behind{transform-origin:left top}.chl{left:1240px;top:300px;z-index:9;min-width:560px}
.safe{font-size:24px;color:#C9D3E6;margin:16px 24px 18px}.del .wb{margin:0 24px}
*{box-sizing:border-box;margin:0}
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
.sr.focus .obtn{box-shadow:inset 0 0 0 2px rgba(11,13,18,.5)}
.warn2{left:50%;top:50%;transform:translate(-50%,-50%);width:900px;padding:40px;z-index:8}
.warn2 .wb{display:flex;gap:16px}.warn2 p{font-size:27px;line-height:1.45;color:#C9D3E6;margin:18px 0 30px}.warn2 p.small{font-size:23px;color:#9aa3b4;margin:22px 0 0}
.tall2{top:110px}.tagp{right:120px;top:170px;width:760px}.chips{display:flex;gap:6px;font-size:26px;color:#C9D3E6}
.chips span{padding:6px 16px;border-radius:16px}.chips .chip{background:#F4F6FB;color:#0B0D12;font-weight:500}
.mi.focus .chips{color:#0B0D12}.mi.focus .chips .chip{background:#0B0D12;color:#fff}.stepv{font-size:27px;color:#C9D3E6}
.stepv b{color:#fff;font-weight:500;margin:0 8px}.rowh{display:flex;align-items:center;gap:14px}.rowh svg{color:#C9D3E6}
.card.cf{transform:scale(1.1);transform-origin:left center;box-shadow:0 0 0 6px #fff;margin-right:40px}
.sugp{right:56px;top:560px;width:1200px;z-index:8;padding:20px}.srs2 .sr{height:110px}
.sp{width:70px;height:105px}.tagc{margin-left:auto;display:inline-flex;align-items:center;gap:8px;font-size:26px;
padding:6px 16px;border-radius:16px;box-shadow:inset 0 0 0 2px rgba(255,255,255,.35)}.sr.focus .tagc{box-shadow:inset 0
0 0 2px rgba(11,13,18,.4)}.sr .rbtn{margin-left:20px}.sr .obtn.dk{margin-left:12px;box-shadow:inset 0 0 0 2px
rgba(11,13,18,.5)}.sr.focus .rbtn svg{color:#fff}.sugp .apply{margin:16px 20px 6px}
.panel,.menu{z-index:8!important}
.mi.focus{background:#F4F6FB!important;color:#0B0D12!important;filter:none!important}.mi.focus svg,.mi.focus small,.mi.focus .val,.mi.focus .stepv,.mi.focus .stepv b{color:#0B0D12!important}
.kind{left:900px;top:330px}.tp{right:120px;top:120px;width:820px}.val.soft{color:#9aa3b4;font-weight:400}
.prev{margin:4px 24px 10px 92px;padding:16px 20px;border-radius:16px;background:rgba(255,255,255,.06)}
.prev b{display:block;font-size:28px;font-weight:500}.prev span{display:flex;align-items:center;gap:10px;margin:10px 0;
font-size:26px}.prev span .svc{margin-left:12px}.prev span .svc:first-child{margin-left:0}.prev small{font-size:22px;
color:#C9D3E6}.apply.off{opacity:.4}
.svc{display:inline-flex;align-items:center;justify-content:center;min-width:44px;height:44px;padding:0 8px;
border-radius:10px;color:#fff;font-weight:500;font-size:22px;box-shadow:0 0 0 2px rgba(255,255,255,.5)}
.kbt{left:150px;top:300px;z-index:9}.typed.sm{font-size:26px}.k.d2{grid-column:span 2}.kbh{font-size:22px;color:#C9D3E6;margin:14px 4px 0}
.sw{width:64px;height:36px;border-radius:18px;background:#0B0D12;position:relative;display:inline-block}
.sw:after{content:'';position:absolute;right:4px;top:4px;width:28px;height:28px;border-radius:50%;background:#fff}
.rmeta{font-size:24px;color:#C9D3E6;font-weight:400;margin-left:10px}
.trow{position:absolute;left:96px;top:876px;display:flex;gap:24px}.tc{width:400px;border-radius:14px;overflow:hidden;
background:#22252c;box-shadow:inset 0 0 0 2px rgba(255,255,255,.6)}.ta{height:225px;position:relative}
.ta .svc{position:absolute;left:14px;bottom:14px}.tcap{padding:10px 16px 14px}.tcap b{display:block;font-size:25px;
font-weight:500}.tcap small{font-size:21px;color:#C9D3E6}.tc.tf{transform:scale(1.1);transform-origin:left top;
box-shadow:0 0 0 6px #fff;margin-right:40px}`;
(async () => {
  const browser = await chromium.launch();
  const out = { '01-add-a-row-kind': [f1, 1080], '02-paste-list-link': [f2, 1080], '03-preview-and-add': [f3, 1080],
    '04-home-row': [f4, 1240] };
  for (const [name, [f, h]] of Object.entries(out)) {
    const tab = await browser.newPage({ viewport: { width: 1920, height: h } });
    await tab.setContent(`<html><head><style>${css.replaceAll('height:1080px', `height:${h}px`)}</style></head><body>
      ${f}</body></html>`, { waitUntil: 'load' });
    await tab.evaluate(() => document.fonts.ready);
    await tab.screenshot({ path: path.join(__dirname, `trakt-${name}.png`) });
    await tab.close();
  }
  await browser.close();
})();
