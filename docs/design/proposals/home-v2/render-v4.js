// v4: no 'This Row' heading (a plain line sets Delete row apart); focus on Add new titles automatically; the icon
// picker as a grid on top of Add a row.
// v3: Add a row with today's real options (Icon, Maximum items added); Years and Minimum rating kept as proposals;
// Add new titles automatically moved beside Maximum items.
// v2: Customise Home with Your Rows first; Add a row open on top (Customise Home darkened behind).
// Home redesign, first pass (owner, 13 Nov 2026), built from docs/design/RULES.md. Made-up titles. Light frame = edge.
// 1 Home, whole page: hero; Continue Watching with the caption box under each card; Recently Added with the title
//   inside the picture, to compare.
// 2 A row heading with Move and Hide beside it; below, move mode in progress.
// 3 Customise Home, whole panel.
// Run: FONT_DIR=<roboto woff2 dir> node render.js
const fs = require('fs');
const path = require('path');
const { chromium } = require('playwright');
const fontDir = process.env.FONT_DIR;
const font = w => `@font-face{font-family:R;font-weight:${w};src:url(data:font/woff2;base64,${fs.readFileSync(
  path.join(fontDir, `roboto-latin-${w}-normal.woff2`)).toString('base64')}) format('woff2')}`;
const svg = (d, s = 30) => `<svg width="${s}" height="${s}" viewBox="0 0 24 24" fill="none" stroke="currentColor"
  stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round">${d}</svg>`;
const I = {
  search: '<circle cx="11" cy="11" r="7"/><path d="M20 20l-4-4"/>',
  cog: '<circle cx="12" cy="12" r="3"/><path d="M12 2v3M12 19v3M4.2 4.2l2.1 2.1M17.7 17.7l2.1 2.1M2 12h3M19 12h3'
    + 'M4.2 19.8l2.1-2.1M17.7 6.3l2.1-2.1"/>',
  play: '<path d="M7 4l13 8-13 8z"/>',
  info: '<circle cx="12" cy="12" r="9"/><path d="M12 11v6M12 7.5h.1"/>',
  cont: '<circle cx="12" cy="12" r="9"/><path d="M10 8.5v7l5.5-3.5z"/>',
  recent: '<rect x="3" y="5" width="18" height="16" rx="2"/><path d="M3 10h18M12 13v5M9.5 15.5h5"/>',
  later: '<path d="M6 3h12v18l-6-4-6 4z"/>',
  trend: '<path d="M3 17l6-6 4 4 8-8M15 7h6v6"/>',
  pop: '<path d="M12 20s-7-4.5-7-10a4 4 0 0 1 7-2.6A4 4 0 0 1 19 10c0 5.5-7 10-7 10z"/>',
  watched: '<circle cx="12" cy="12" r="9"/><path d="M8 12.5l2.8 2.8L16.5 9.5"/>',
  similar: '<circle cx="9" cy="12" r="5"/><circle cx="15" cy="12" r="5"/>',
  leaf: '<path d="M5 19c0-8 6-14 15-14 0 9-6 15-14 15"/><path d="M5 19l8-8"/>',
  star: '<path d="M12 3l2.7 5.6 6.1.9-4.4 4.3 1 6.1L12 17l-5.4 2.9 1-6.1L3.2 9.5l6.1-.9z"/>',
  name: '<path d="M4 7V5h16v2M12 5v14M9 19h6"/>',
  shows: '<rect x="3" y="5" width="18" height="12" rx="2"/><path d="M8 21h8M12 17v4"/>',
  genre: '<path d="M3 12V4h8l10 10-8 8z"/><circle cx="7.5" cy="8" r="1.5"/>',
  cal: '<rect x="3" y="5" width="18" height="16" rx="2"/><path d="M3 10h18M8 3v4M16 3v4"/>',
  auto: '<path d="M4 12a8 8 0 0 1 14-5.3M20 12a8 8 0 0 1-14 5.3"/><path d="M18 3v4h-4M6 21v-4h4"/>',
  sort: '<path d="M7 4v16M3 16l4 4 4-4M17 20V4M13 8l4-4 4 4"/>',
  order: '<path d="M7 4v16M3 16l4 4 4-4M14 6h7M14 12h5M14 18h3"/>',
  bin: '<path d="M4 7h16M10 11v6M14 11v6M6 7l1 13h10l1-13M9 7V4h6v3"/>',
  max: '<path d="M4 6h16M4 12h16M4 18h16"/><path d="M20 3v18"/>',
  own: '<path d="M4 6h16M4 12h10M4 18h7"/><path d="M18 15v6M15 18h6"/>',
  move: '<path d="M8 9l4-4 4 4M8 15l4 4 4-4M12 5v14"/>',
  hide: '<circle cx="12" cy="12" r="9"/><path d="M8 12h8"/>',
  updn: '<path d="M8 9l4-4 4 4M8 15l4 4 4-4"/>',
  chev: '<path d="M9 6l6 6-6 6"/>',
  plus: '<path d="M12 5v14M5 12h14"/>',
  left: '<path d="M15 6l-6 6 6 6"/>',
};
const nav = mark => `<div class="nav"><span class="brand">SUPERNOVA</span><span class="ni ${mark}">Home</span>
  ${['Movies', 'TV Shows', 'Documentaries'].map(t => `<span class="ni">${t}</span>`).join('')}<span class="ni files">Files
  </span><span class="ni">${svg(I.search, 34)}</span><span class="ni">${svg(I.cog, 34)}</span><span class="clock">21:40</span>
  </div>`;
const hero = btnFocus => `<div class="hero"><div class="hcopy"><div class="hlogo">THE SALT ROAD</div>
  <div class="hmeta"><span class="age">12</span>2024 · 1 h 38 min · Documentary, Travel</div>
  <div class="hdesc">Two friends walk the ancient salt routes of the Sahara, following the caravans that still cross it
  each winter, and the families whose lives depend on them.</div></div>
  <div class="hbtns"><span class="btn ${btnFocus ? 'focus' : ''}">${svg(I.play, 26)}Resume · 42 min left</span>
  <span class="btn">${svg(I.info, 26)}More info</span></div>
  <div class="dots"><i class="on"></i><i></i><i></i><i></i><i></i></div></div>`;
const art = ['#5b3a2e', '#2e4a5b', '#4b5b2e', '#5b2e4a', '#2e5b4f', '#3a2e5b', '#5b4a2e'];
const capCard = (i, t, l, o) => `<div class="cc ${o.focus ? 'focus' : ''}"><span class="art" style="background:
  radial-gradient(ellipse at 50% 40%,${art[i % 7]} 0%,#121418 85%)"></span><div class="cap"><b>${t}</b><small>${l}</small>
  ${o.p ? `<i class="cpb"><b style="width:${o.p}%"></b></i>` : ''}</div></div>`;
const inCard = (i, t, l, o) => `<div class="ic ${o.focus ? 'focus' : ''}" style="background:radial-gradient(ellipse at 50% 40%,
  ${art[(i + 3) % 7]} 0%,#121418 85%)"><div class="shade"><b>${t}</b><small>${l}</small></div></div>`;
const rowHead = (ic, t, extra) => `<div class="rh">${svg(I[ic], 30)}<span>${t}</span>${extra || ''}</div>`;
const contRow = f => `${rowHead('cont', 'Continue Watching')}<div class="cards">${capCard(0, 'Tidelands',
  'S1 E2 · 31 min left', { p: 30, focus: f })}${capCard(1, 'The Salt Road', '42 min left', { p: 60 })}${capCard(2,
  'Northern Lights', 'S2 E5 · 18 min left', { p: 75 })}${capCard(3, 'Cold Iron', '1 h 2 min left', { p: 20 })}
  ${capCard(4, 'Glass Orchard', 'S1 E1 · 40 min left', { p: 10 })}</div>`;
const recentRow = () => `${rowHead('recent', 'Recently Added')}<div class="cards">${inCard(0, 'Frozen Frontier',
  '2021 · 6 episodes', {})}${inCard(1, 'Inside the Hive', '2023 · 1 h 24 min', {})}${inCard(2, 'Mountain Light',
  '2023 · 1 h 47 min', {})}${inCard(3, 'Paper Harbour', '2024 · 1 h 38 min', {})}${inCard(4, 'Low Tide',
  '2021 · 1 h 44 min', {})}</div>`;
const home = `${nav('line')}${hero(false)}<div class="rows">${contRow(true)}${recentRow()}</div>`;

const headingFocus = `<div class="frame">${nav('dull')}<div class="rows top">${rowHead('cont', 'Continue Watching')}
  <div class="cards">${capCard(0, 'Tidelands', 'S1 E2 · 31 min left', { p: 30 })}${capCard(1, 'The Salt Road',
  '42 min left', { p: 60 })}${capCard(2, 'Northern Lights', 'S2 E5 · 18 min left', { p: 75 })}${capCard(3, 'Cold Iron',
  '1 h 2 min left', { p: 20 })}${capCard(4, 'Glass Orchard', 'S1 E1 · 40 min left', { p: 10 })}</div>
  ${rowHead('recent', 'Recently Added', `<span class="hb focus">${svg(I.move, 26)}Move</span><span class="hb">
  ${svg(I.hide, 26)}Hide</span>`)}<div class="cards">${[0, 1, 2, 3, 4].map(i => capCard(i + 2, ['Frozen Frontier',
  'Inside the Hive', 'Mountain Light', 'Paper Harbour', 'Low Tide'][i], ['2021 · 6 episodes', '2023 · 1 h 24 min',
  '2023 · 1 h 47 min', '2024 · 1 h 38 min', '2021 · 1 h 44 min'][i], {})).join('')}</div></div></div>`;
const moving = `<div class="frame">${nav('dull')}<div class="rows top"><div class="moving">${rowHead('recent',
  'Recently Added', `<span class="mv">${svg(I.updn, 26)}</span><span class="mhint">Row 1 of 6 · OK when done</span>`)}
  <div class="cards">${[0, 1, 2, 3, 4].map(i => capCard(i + 2, ['Frozen Frontier', 'Inside the Hive', 'Mountain Light',
  'Paper Harbour', 'Low Tide'][i], ['2021 · 6 episodes', '2023 · 1 h 24 min', '2023 · 1 h 47 min', '2024 · 1 h 38 min',
  '2021 · 1 h 44 min'][i], {})).join('')}</div></div><div class="dimmed">${rowHead('cont', 'Continue Watching')}
  <div class="cards">${capCard(0, 'Tidelands', 'S1 E2 · 31 min left', { p: 30 })}${capCard(1, 'The Salt Road',
  '42 min left', { p: 60 })}${capCard(2, 'Northern Lights', 'S2 E5 · 18 min left', { p: 75 })}${capCard(3, 'Cold Iron',
  '1 h 2 min left', { p: 20 })}${capCard(4, 'Glass Orchard', 'S1 E1 · 40 min left', { p: 10 })}</div></div></div></div>`;
const rowsPics = headingFocus + moving;

const mg = t => `<div class="mg"><i></i><span>${t}</span><i></i></div>`;
const sw = on => `<span class="sw ${on ? 'on' : ''}"><i></i></span>`;
const crow = (ic, name, line, on, f, chev) => `<div class="prow ${f ? 'focus' : ''}">${svg(I[ic], 30)}<span class="pt">
  <b>${name}</b><small>${line}</small></span>${chev ? svg(I.chev, 26) : ''}${sw(on)}</div>`;
const cust = `${nav('dull')}${hero(false)}<div class="rows">${contRow(false)}</div><div class="dim"></div><div class="panel">
  ${mg('Built-In Rows')}
  ${crow('cont', 'Continue Watching', 'Unfinished movies and next episodes', true, true)}
  ${crow('recent', 'Recently Added', 'Newest in your library', true)}
  ${crow('later', 'Watch Next', 'Titles you saved for later', true)}
  ${crow('trend', 'Trending on Trakt', 'In your library', true)}
  ${crow('pop', 'Popular on Trakt', 'In your library', false)}
  ${crow('watched', 'Recently Watched', 'What you finished last', false)}
  ${crow('similar', 'Because You Watched', 'Similar titles in your library', true)}
  ${mg('Your Rows')}
  ${crow('leaf', 'Nature Nights', 'Documentary, Nature · newest first', true, false, true)}
  ${crow('star', 'Christmas Films', 'Movies · Christmas · title A–Z', false, false, true)}
  <div class="prow add">${svg(I.plus, 30)}<span class="pt"><b>Add a row</b></span></div></div>`;


const chips = (list, on) => `<span class="chips">${list.map(t => `<i class="${t === on ? 'on' : ''}">${t}</i>`).join('')}
  </span>`;
const vrow = (ic, name, value, f) => `<div class="prow ${f ? 'focus' : ''}">${svg(I[ic], 30)}<span class="pt"><b>${name}</b>
  </span><span class="v">${value}${svg(I.chev, 26)}</span></div>`;
const crow2 = (ic, name, right, f) => `<div class="prow ${f ? 'focus' : ''}">${svg(I[ic], 30)}<span class="pt"><b>${name}</b>
  </span>${right}</div>`;
const custBehind = `<div class="panel behind2">${mg('Your Rows')}
  ${crow('leaf', 'Nature Nights', 'Documentary, Nature · newest first', true, false, true)}
  ${crow('star', 'Christmas Films', 'Movies · Christmas · title A–Z', false, false, true)}
  <div class="prow focus">${svg(I.plus, 30)}<span class="pt"><b>Add a row</b></span></div>
  ${mg('Built-In Rows')}
  ${crow('cont', 'Continue Watching', 'Unfinished movies and next episodes', true)}
  ${crow('recent', 'Recently Added', 'Newest in your library', true)}
  ${crow('later', 'Watch Next', 'Titles you saved for later', true)}
  ${crow('trend', 'Trending on Trakt', 'In your library', true)}
  ${crow('pop', 'Popular on Trakt', 'In your library', false)}
  ${crow('watched', 'Recently Watched', 'What you finished last', false)}
  ${crow('similar', 'Because You Watched', 'Similar titles in your library', true)}</div>`;
const addRow = `<div class="panel front2">${mg('What To Show')}
  ${vrow('name', 'Row name', 'Nature Nights')}${vrow('leaf', 'Icon', 'Automatic')}
  ${crow2('shows', 'Includes', chips(['Movies', 'TV Shows', 'Both'], 'Both'))}
  ${vrow('genre', 'Genres', 'Any')}${vrow('cal', 'Years', 'Any')}
  ${crow2('star', 'Minimum rating', chips(['Any', '6+', '7+', '8+', '9+'], 'Any'))}
  ${mg('How To Show It')}
  ${crow2('sort', 'Sort by', chips(['Title', 'Release date', 'Date added'], 'Date added'))}
  ${crow2('order', 'Order', chips(['Newest', 'Oldest'], 'Newest'))}
  ${crow2('max', 'Maximum items', chips(['10', '15', '20', '25', '30'], '20'))}
  ${crow2('auto', 'Add new titles automatically', '<span class="sw on"><i></i></span>', true)}
  <div class="mdiv"></div>${crow2('bin', 'Delete row', '')}</div>`;
const addPic = `${nav('dull')}${hero(false)}<div class="rows">${contRow(false)}</div><div class="dim"></div>${custBehind}
  ${addRow}`;


const G = {
  Automatic: '<path d="M12 3l1.8 4.6L18 9l-4.2 1.4L12 15l-1.8-4.6L6 9l4.2-1.4z"/><path d="M19 15l.8 2 2 .8-2 .8-.8 2-.8-2-2-.8 2-.8z"/>',
  Action: '<path d="M13 3L5 14h6l-1 7 8-11h-6z"/>',
  Adventure: '<path d="M3 20l6-12 4 7 3-4 5 9z"/>',
  Animation: '<circle cx="12" cy="12" r="8"/><circle cx="9" cy="10" r="1"/><circle cx="15" cy="10" r="1"/><path d="M9 15c1.5 1.5 4.5 1.5 6 0"/>',
  Comedy: '<circle cx="12" cy="12" r="9"/><path d="M8 14c2 3 6 3 8 0M9 9h.1M15 9h.1"/>',
  Crime: '<circle cx="11" cy="11" r="7"/><path d="M20 20l-4-4"/><path d="M8 11h6"/>',
  Documentary: '<rect x="3" y="7" width="13" height="10" rx="2"/><path d="M16 11l5-3v8l-5-3"/>',
  Drama: '<path d="M4 4h16v6a8 8 0 0 1-16 0z"/><path d="M8 9h.1M16 9h.1M9 14c2-1.5 4-1.5 6 0"/>',
  Family: '<circle cx="8" cy="7" r="3"/><circle cx="16" cy="9" r="2.5"/><path d="M3 20a5 5 0 0 1 10 0M12 20a4 4 0 0 1 8 0"/>',
  Fantasy: '<path d="M5 20L19 6M15 4l1 2 2 1-2 1-1 2-1-2-2-1 2-1z"/>',
  History: '<path d="M3 21h18M5 21V10M9 21V10M15 21V10M19 21V10M3 10l9-6 9 6z"/>',
  Horror: '<path d="M12 3a7 7 0 0 0-7 7v10l2.5-2 2.5 2 2-2 2 2 2.5-2 2.5 2V10a7 7 0 0 0-7-7z"/><path d="M9 11h.1M15 11h.1"/>',
  Music: '<path d="M9 18V5l11-2v13"/><circle cx="6" cy="18" r="3"/><circle cx="17" cy="16" r="3"/>',
  Mystery: '<circle cx="12" cy="12" r="9"/><path d="M9.5 9.5a2.5 2.5 0 1 1 3.5 2.3c-.6.3-1 .9-1 1.6v.6M12 17h.1"/>',
  Nature: '<path d="M5 19c0-8 6-14 15-14 0 9-6 15-14 15"/><path d="M5 19l8-8"/>',
  Romance: '<path d="M12 20s-7-4.5-7-10a4 4 0 0 1 7-2.6A4 4 0 0 1 19 10c0 5.5-7 10-7 10z"/>',
  'Science fiction': '<circle cx="12" cy="12" r="4"/><ellipse cx="12" cy="12" rx="10" ry="3.5"/>',
  Thriller: '<circle cx="12" cy="13" r="8"/><path d="M12 9v4l2 2M10 2h4"/>',
  War: '<path d="M12 3l8 3v6c0 5-4 8-8 9-4-1-8-4-8-9V6z"/>',
  Western: '<path d="M3 15c3 2 15 2 18 0M7 15l1-6h8l1 6M10 9V6h4v3"/>',
};
const tile = (name, on, f) => `<div class="gt ${f ? 'focus' : ''}">${svg(G[name], 44)}<span>${name}</span>${on
  ? `<i class="gtk">${svg(I.tick || '<path d="M5 12.5l4.5 4.5L19 7.5"/>', 22)}</i>` : ''}</div>`;
const iconGrid = `<div class="panel front3"><div class="ggrid">${Object.keys(G).map(n => tile(n, n === 'Nature',
  n === 'Nature')).join('')}</div></div>`;
const iconPic = addPic.replace('<div class="panel front2">', '<div class="panel front2 behind3">') + iconGrid;

const css = `${font(400)}${font(500)}${font(700)}*{box-sizing:border-box;margin:0}
body{width:1920px;font-family:R;color:#fff;background:#16181C;position:relative;overflow:hidden}
body:after{content:"";position:fixed;inset:0;border:6px solid #8a919c;z-index:99;pointer-events:none}
.frame{position:relative;height:1080px;overflow:hidden}.frame+.frame{border-top:8px solid #8a919c}
.nav{position:absolute;left:56px;right:56px;top:24px;height:76px;display:flex;align-items:center;gap:14px;
font-size:33px;color:#8f98a8;z-index:5}.nav .brand{color:#f1ead8;letter-spacing:2px;margin-right:36px;font-weight:500}
.ni{position:relative;display:inline-flex;align-items:center;height:60px;padding:0 18px}.ni.files{margin-left:auto}
.nav .clock{margin-left:18px;color:#C9D3E6}
.line,.dull{color:#fff}.dull{color:#C9D3E6}.line:after,.dull:after{content:'';position:absolute;left:18px;right:18px;
bottom:-2px;height:4px;border-radius:2px}.line:after{background:#fff}.dull:after{background:rgba(255,255,255,.3)}
.hero{position:absolute;left:72px;top:118px;width:1776px;height:820px;border-radius:26px;
background:linear-gradient(100deg,rgba(14,16,20,.92) 0%,rgba(14,16,20,.5) 38%,rgba(14,16,20,0) 60%),
radial-gradient(ellipse at 75% 45%,#8a6a46 0%,#4a3626 40%,#1c1814 78%);
box-shadow:inset 0 0 0 2px rgba(255,255,255,.6),inset 0 0 26px 6px rgba(0,0,0,.45)}
.hcopy{position:absolute;left:60px;top:70px;width:960px}.hlogo{font-size:80px;font-weight:500;letter-spacing:7px;
color:#f1ead8}.hmeta{display:flex;align-items:center;gap:14px;font-size:28px;margin-top:14px;color:#e8edf6}
.age{display:inline-flex;align-items:center;justify-content:center;min-width:44px;height:38px;padding:0 10px;
border-radius:8px;font-size:24px;font-weight:700;box-shadow:inset 0 0 0 2px rgba(255,255,255,.7)}
.hdesc{font-size:27px;line-height:1.45;margin-top:20px;color:#e8edf6;display:-webkit-box;-webkit-line-clamp:2;
-webkit-box-orient:vertical;overflow:hidden}
.hbtns{position:absolute;left:60px;bottom:60px;display:flex;gap:16px}
.btn{display:inline-flex;align-items:center;gap:12px;height:68px;padding:0 30px;border-radius:18px;font-size:28px;
font-weight:500;background:rgba(22,24,28,.72);color:#fff}.btn.focus{background:#F4F6FB;color:#0B0D12}
.dots{position:absolute;left:50%;bottom:28px;transform:translateX(-50%);display:flex;gap:12px}
.dots i{width:12px;height:12px;border-radius:50%;background:rgba(255,255,255,.35)}.dots i.on{background:#fff}
.rows{position:absolute;left:96px;right:0;top:990px}.rows.top{top:150px}
.rh{display:flex;align-items:center;gap:16px;font-size:32px;font-weight:500;margin:0 0 26px}.rh svg{color:#C9D3E6}
.rows .cards+.rh,.rows .cards+.dimmed .rh,.moving+.dimmed .rh{margin-top:64px}
.cards{display:flex;gap:30px}
.cc{position:relative;flex:none;width:400px;border-radius:16px;background:#24272D;
box-shadow:inset 0 0 0 2px rgba(255,255,255,.12)}
.cc .art{display:block;height:225px;border-radius:16px 16px 0 0;
box-shadow:inset 0 0 0 2px rgba(255,255,255,.6),inset 0 0 18px 4px rgba(0,0,0,.45)}
.cc.focus{transform:scale(1.1);transform-origin:left center;margin-right:40px;box-shadow:0 0 0 5px #fff,
0 18px 40px rgba(0,0,0,.6);z-index:2}
.cap{position:relative;padding:12px 16px 24px}.cap b{display:block;font-size:26px;font-weight:500}
.cap small{display:block;font-size:22px;color:#C9D3E6;margin-top:3px}
.cpb{position:absolute;left:16px;right:16px;bottom:10px;height:5px;border-radius:3px;background:rgba(255,255,255,.2);
overflow:hidden}.cpb b{display:block;height:5px;background:#fff}
.ic{position:relative;flex:none;width:400px;height:225px;border-radius:16px;
box-shadow:inset 0 0 0 2px rgba(255,255,255,.6),inset 0 0 18px 4px rgba(0,0,0,.45)}
.ic .shade{position:absolute;left:0;right:0;bottom:0;padding:14px 16px;border-radius:0 0 16px 16px;
background:linear-gradient(0deg,rgba(10,11,14,.9),rgba(10,11,14,0))}.ic b{display:block;font-size:26px;font-weight:500}
.ic small{display:block;font-size:22px;color:#C9D3E6;margin-top:2px}
.hb{display:inline-flex;align-items:center;gap:10px;height:56px;padding:0 20px;border-radius:14px;font-size:26px;
font-weight:500;margin-left:14px;color:#e8edf6}.hb svg{color:inherit}.hb.focus{background:#F4F6FB;color:#0B0D12}
.rh .hb:first-of-type{margin-left:30px}
.moving .rh{display:inline-flex;background:#F4F6FB;color:#0B0D12;padding:8px 22px;border-radius:16px}
.moving .rh svg{color:#0B0D12}.mv{display:flex;margin-left:10px}.mhint{font-size:24px;font-weight:400;margin-left:18px;
color:#3A4256}.moving .cards{transform:translateY(-4px)}.moving .cc{box-shadow:0 0 0 3px rgba(255,255,255,.5),
0 16px 36px rgba(0,0,0,.6)}
.dimmed{opacity:.35}
.dim{position:absolute;inset:0;background:rgba(10,11,13,.55);z-index:5}
.panel{position:absolute;right:56px;top:48px;z-index:6;width:820px;padding:18px 24px;border-radius:26px;
background:rgba(22,24,28,.95);box-shadow:0 0 0 2px rgba(255,255,255,.22),0 30px 80px rgba(0,0,0,.6)}
.mg{display:flex;align-items:center;gap:14px;padding:16px 16px 8px;font-size:28px;font-weight:500}
.mg i{flex:1;height:2px;background:rgba(255,255,255,.18)}
.prow{position:relative;display:flex;align-items:center;gap:20px;height:80px;padding:0 24px;border-radius:18px}
.prow:after{content:'';position:absolute;left:24px;right:24px;bottom:0;height:2px;background:rgba(255,255,255,.08)}
.prow.focus:after,.prow:has(+ .mg):after,.prow:last-child:after,.prow:has(+ .prow.focus):after{display:none}
.prow svg{color:#C9D3E6;flex:none}.prow.focus{background:#F4F6FB;color:#0B0D12}.prow.focus svg{color:#0B0D12}
.pt{flex:1;display:flex;flex-direction:column}.pt b{font-size:28px;font-weight:500}.pt small{font-size:22px;color:#C9D3E6}
.prow.focus .pt small{color:#3A4256}
.sw{width:76px;height:42px;border-radius:21px;background:#454B55;position:relative;flex:none}.sw i{position:absolute;
top:5px;left:5px;width:32px;height:32px;border-radius:50%;background:#C9D3E6}.sw.on{background:#F4F6FB}
.sw.on i{left:39px;background:#0B0D12}.prow.focus .sw.on{background:#0B0D12}.prow.focus .sw.on i{background:#F4F6FB}
.prow.add .pt b{color:#e8edf6}
.mdiv{height:2px;background:rgba(255,255,255,.18);margin:14px 16px}
.behind3{filter:brightness(.45);transform:scale(.96);transform-origin:right top}
.front3{right:420px !important;top:200px !important;width:auto;padding:24px}
.ggrid{display:grid;grid-template-columns:repeat(5,170px);gap:14px}
.gt{position:relative;display:flex;flex-direction:column;align-items:center;justify-content:center;gap:12px;height:140px;
border-radius:18px;font-size:22px;color:#e8edf6}.gt svg{color:#C9D3E6}
.gt.focus{background:#F4F6FB;color:#0B0D12;box-shadow:none}.gt.focus svg{color:#0B0D12}
.gtk{position:absolute;right:10px;top:10px;display:flex}
.behind2{filter:brightness(.45);transform:scale(.96);transform-origin:right top}
.front2{right:200px !important;top:40px !important;width:900px}
.chips{display:flex;gap:4px;font-size:26px;margin-left:auto}.chips i{font-style:normal;display:inline-flex;align-items:center;
height:48px;padding:0 18px;border-radius:14px;white-space:nowrap}.chips i.on{background:#454B55}
.prow.focus .chips i.on{background:#0B0D12;color:#fff}.v{margin-left:auto;display:flex;align-items:center;gap:10px;
font-size:26px}.prow .sw{margin-left:auto}.front2 .prow{height:72px}`;
(async () => {
  const browser = await chromium.launch();
  const tab = await browser.newPage({ viewport: { width: 1920, height: 1080 } });
  for (const [f, body, h] of [['06-v4-add-a-row-focus.png', addPic, 1080], ['07-v4-icon-picker.png', iconPic, 1080]]) {
    await tab.setViewportSize({ width: 1920, height: h });
    await tab.setContent(`<html><head><style>${css}body{height:${h}px}</style></head><body>${body}</body></html>`,
      { waitUntil: 'load' });
    await tab.evaluate(() => document.fonts.ready);
    await tab.screenshot({ path: path.join(__dirname, f) });
  }
  await browser.close();
})();
