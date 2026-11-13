// v2: every picture on the owner's own page (Documentaries), the page with everything; Movies and TV Shows are the
// same minus what does not apply.
// Library pages (owner, 13 Nov 2026): one design and one piece of code for Movies, TV Shows and the owner's own page;
// only which titles differ. Built from docs/design/RULES.md. Made-up titles. Light frame = picture edge.
// 1 Movies: toolbar (Watched filter chips, Sort, Genres, Edit page) with the page's facts on the right; poster grid
//   with title and second line under each poster, progress on started titles, a tick on watched ones.
// 2 The long-press menu beside the poster (movie).
// 3 Edit page for the owner's own page (Documentaries): the full master list; Movies / TV Shows hide what doesn't apply.
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
  sort: '<path d="M7 4v16M3 16l4 4 4-4M17 20V4M13 8l4-4 4 4"/>',
  genre: '<path d="M3 12V4h8l10 10-8 8z"/><circle cx="7.5" cy="8" r="1.5"/>',
  edit: '<path d="M4 20h4L19 9l-4-4L4 16z"/><path d="M14 6l4 4"/>',
  film: '<rect x="3" y="4" width="18" height="16" rx="2"/><path d="M7 4v16M17 4v16M3 9h4M3 15h4M17 9h4M17 15h4"/>',
  ring: '<circle cx="12" cy="12" r="9"/>',
  disk: '<rect x="3" y="6" width="18" height="12" rx="2"/><path d="M7 14h.1M11 14h6"/>',
  tick: '<path d="M5 12.5l4.5 4.5L19 7.5"/>',
  play: '<path d="M7 4l13 8-13 8z"/>',
  restart: '<path d="M4 12a8 8 0 1 0 2.3-5.7"/><path d="M4 4v4h4"/>',
  watched: '<circle cx="12" cy="12" r="9"/><path d="M8 12.5l2.8 2.8L16.5 9.5"/>',
  fix: '<circle cx="11" cy="11" r="7"/><path d="M20 20l-4-4M8 11l2 2 4-4"/>',
  remove: '<rect x="3" y="4" width="18" height="16" rx="2"/><path d="M8 12h8"/>',
  bin: '<path d="M4 7h16M10 11v6M14 11v6M6 7l1 13h10l1-13M9 7V4h6v3"/>',
  name: '<path d="M4 7V5h16v2M12 5v14M9 19h6"/>',
  shows: '<rect x="3" y="5" width="18" height="12" rx="2"/><path d="M8 21h8M12 17v4"/>',
  cal: '<rect x="3" y="5" width="18" height="16" rx="2"/><path d="M3 10h18M8 3v4M16 3v4"/>',
  folder: '<path d="M3 7a2 2 0 0 1 2-2h4l2 2h8a2 2 0 0 1 2 2v8a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2z"/>',
  chev: '<path d="M9 6l6 6-6 6"/>',
  minus: '<path d="M6 12h12"/>',
  plus: '<path d="M12 6v12M6 12h12"/>',
};
const nav = (place, mark) => `<div class="nav"><span class="brand">SUPERNOVA</span>${['Home', 'Movies', 'TV Shows',
  'Documentaries'].map(t => `<span class="ni ${t === place ? mark : ''}">${t}</span>`).join('')}
  <span class="ni files">Files</span><span class="ni">${svg(I.search, 34)}</span><span class="ni">${svg(I.cog, 34)}</span>
  <span class="clock">21:40</span></div>`;
const chips = (list, on) => `<span class="chips">${list.map(t => `<i class="${t === on ? 'on' : ''}">${t}</i>`)
  .join('')}</span>`;
const tool = (ic, t) => `<span class="tool">${svg(I[ic], 28)}${t}</span>`;
const facts = list => `<span class="facts">${list.map(([ic, v, l]) => `<span>${svg(I[ic], 26)}<b>${v}</b> ${l}</span>`)
  .join('<i>·</i>')}</span>`;
const toolbar = `<div class="toolbar">${chips(['All', 'Unwatched', 'In progress', 'Watched'], 'All')}
  ${tool('sort', 'Release date')}${tool('genre', 'All genres')}${tool('edit', 'Edit page')}
  ${facts([['film', '96', 'Titles'], ['ring', '12', 'Unwatched']])}</div>`;
const art = ['#5b3a2e', '#2e4a5b', '#4b5b2e', '#5b2e4a', '#2e5b4f', '#3a2e5b', '#5b4a2e'];
const poster = (i, title, line, o) => `<div class="pc ${o.focus ? 'focus' : ''}"><span class="pimg" style="background:
  radial-gradient(ellipse at 50% 30%,${art[i % 7]} 0%,#121418 85%)">${o.watched ? `<i class="wt">${svg(I.tick, 22)}</i>`
  : ''}${o.progress ? `<i class="pb"><b style="width:${o.progress}%"></b></i>` : ''}</span><b>${title}</b>
  <small>${line}</small></div>`;
const TITLES = [['Deep Blue Planet', '2025 · 6 episodes', {}], ['The Salt Road', '2024 · 1 h 38 min',
  { progress: 40, line: '42 min left' }], ['Wild Isles of the North', '2024 · 5 episodes', { watched: true }],
['Inside the Hive', '2023 · 1 h 24 min', { focus: true }], ['Mountain Light', '2023 · 1 h 47 min', {}],
['The Lost Rivers', '2022 · 4 episodes', { watched: true }], ['Night Shift Ocean', '2022 · 1 h 31 min', {}],
['Frozen Frontier', '2021 · 6 episodes', {}], ['A Year in the Forest', '2021 · 1 h 44 min',
  { progress: 75, line: '18 min left' }], ['Desert Bloom', '2020 · 1 h 22 min', {}], ['Rails Across Asia',
  '2020 · 3 episodes', {}], ['The Coral Clock', '2019 · 1 h 41 min', { watched: true }], ['City of Birds',
  '2019 · 1 h 28 min', {}], ['Tidelands', '2018 · 4 episodes', {}]];
const grid = (focusOff) => `<div class="grid">${TITLES.map(([t, l, o], i) => poster(i, t, o.line || l,
  Object.assign({}, o, focusOff ? { focus: false } : {}))).join('')}</div>`;
const page = `${nav('Documentaries', 'line')}${toolbar}${grid(false)}`;

const mi = (ic, t, f) => `<div class="mi ${f ? 'focus' : ''}">${svg(I[ic], 30)}<span>${t}</span></div>`;
const mg = t => `<div class="mg"><i></i><span>${t}</span><i></i></div>`;
const longPress = `${nav('Documentaries', 'line')}${toolbar}${grid(false)}<div class="menu">${mg('Playback')}
  ${mi('play', 'Play', true)}${mg('Library')}${mi('watched', 'Mark as watched')}${mi('fix', 'Fix match')}
  ${mi('remove', 'Remove from library')}${mg('Files &amp; Media')}${mi('bin', 'Delete file')}</div>`;

const prow = (ic, name, right, f) => `<div class="prow ${f ? 'focus' : ''}">${svg(I[ic], 30)}<span class="pn">${name}
  </span>${right}</div>`;
const val = (v, chev) => `<span class="v">${v}${chev ? svg(I.chev, 26) : ''}</span>`;
const yrs = `<span class="v">${svg(I.minus, 24)}<b>1990</b>to<b>Any</b>${svg(I.plus, 24)}</span>`;
const editPage = `${nav('Documentaries', 'line')}${toolbar}${grid(true)}<div class="dim"></div><div class="panel">
  ${prow('name', 'Page name', val('Documentaries', true))}
  ${prow('shows', 'Shows', chips(['Movies', 'TV Shows', 'Both'], 'Both'), true)}
  ${prow('genre', 'Genres', val('Documentary', true))}
  ${prow('cal', 'Years', yrs)}
  ${prow('folder', 'Sources', val('All sources', true))}
  ${prow('sort', 'Opens sorted by', val('Release date', true))}
  ${mg('This Page')}${prow('bin', 'Delete page', '')}</div>`;

const css = `${font(400)}${font(500)}${font(700)}*{box-sizing:border-box;margin:0}
body{width:1920px;height:1080px;overflow:hidden;font-family:R;color:#fff;background:#16181C;position:relative}
body:after{content:"";position:fixed;inset:0;border:6px solid #8a919c;z-index:99;pointer-events:none}
.nav{position:absolute;left:56px;right:56px;top:24px;height:76px;display:flex;align-items:center;gap:14px;
font-size:33px;color:#8f98a8;z-index:5}.nav .brand{color:#f1ead8;letter-spacing:2px;margin-right:36px;font-weight:500}
.ni{position:relative;display:inline-flex;align-items:center;height:60px;padding:0 18px}.ni.files{margin-left:auto}
.nav .clock{margin-left:18px;color:#C9D3E6}.line{color:#fff}.line:after{content:'';position:absolute;left:18px;
right:18px;bottom:-2px;height:4px;border-radius:2px;background:#fff}
.toolbar{position:absolute;left:96px;right:96px;top:128px;display:flex;align-items:center;gap:22px;font-size:27px}
.chips{display:flex;gap:4px}.chips i{font-style:normal;padding:10px 20px;border-radius:14px;white-space:nowrap}
.chips i.on{background:#454B55}
.tool{white-space:nowrap;display:inline-flex;align-items:center;gap:10px;color:#e8edf6;padding:10px 6px}.tool svg{color:#C9D3E6}
.facts{margin-left:auto;display:flex;align-items:center;gap:14px;color:#C9D3E6}.facts span{white-space:nowrap;display:inline-flex;
align-items:center;gap:8px}.facts b{color:#fff;font-weight:500}.facts i{font-style:normal}
.grid{position:absolute;left:96px;right:96px;top:224px;display:grid;grid-template-columns:repeat(7,minmax(0,1fr));gap:40px 28px}
.pc{display:flex;flex-direction:column}.pimg{position:relative;display:block;aspect-ratio:2/3;border-radius:14px;
box-shadow:inset 0 0 0 2px rgba(255,255,255,.6),inset 0 0 18px 4px rgba(0,0,0,.45)}
.pc.focus .pimg{transform:scale(1.07);box-shadow:0 0 0 5px #fff,inset 0 0 18px 4px rgba(0,0,0,.45),
0 14px 34px rgba(0,0,0,.55)}.pc.focus b{margin-top:22px}
.pc b{font-size:25px;font-weight:500;margin-top:14px;white-space:nowrap;overflow:hidden;text-overflow:ellipsis}
.pc small{font-size:21px;color:#C9D3E6;margin-top:2px}
.wt{position:absolute;right:10px;top:10px;width:36px;height:36px;border-radius:50%;background:#F4F6FB;color:#0B0D12;
display:flex;align-items:center;justify-content:center}
.pb{position:absolute;left:12px;right:12px;bottom:12px;height:6px;border-radius:3px;background:rgba(255,255,255,.3)}
.pb b{display:block;height:6px;border-radius:3px;background:#fff;margin:0}
.menu{position:absolute;left:1050px;top:290px;padding:18px;border-radius:24px;background:rgba(22,24,28,.9);
box-shadow:0 0 0 2px rgba(255,255,255,.22),0 30px 80px rgba(0,0,0,.6);z-index:6}
.mg{display:flex;align-items:center;gap:14px;padding:14px 16px 8px;font-size:24px;font-weight:500}
.mg i{flex:1;height:2px;background:rgba(255,255,255,.18);min-width:30px}
.mi{display:flex;align-items:center;gap:20px;height:60px;padding:0 24px;border-radius:14px;font-size:28px;
white-space:nowrap}.mi svg{color:#C9D3E6}.mi.focus{background:#F4F6FB;color:#0B0D12}.mi.focus svg{color:#0B0D12}
.dim{position:absolute;inset:0;background:rgba(10,11,13,.55);z-index:5}
.panel .mg{margin-top:18px}
.panel{position:absolute;right:0;top:0;bottom:0;width:max-content;min-width:760px;padding:56px 40px;
background:rgba(22,24,28,.9);z-index:6}
.prow{position:relative;display:flex;align-items:center;gap:20px;height:86px;padding:0 24px;border-radius:18px;
font-size:28px}.prow:after{content:'';position:absolute;left:24px;right:24px;bottom:0;height:2px;
background:rgba(255,255,255,.08)}.prow.focus:after,.prow:has(+ .mg):after,.prow:last-child:after,
.prow:has(+ .prow.focus):after{display:none}.prow svg{color:#C9D3E6;flex:none}.pn{font-weight:500;margin-right:40px}
.prow.focus{background:#F4F6FB;color:#0B0D12}.prow.focus svg{color:#0B0D12}
.prow .chips{margin-left:auto}.prow.focus .chips i.on{background:#0B0D12;color:#fff}
.v{margin-left:auto;display:flex;align-items:center;gap:12px}.v b{font-weight:500}`;
(async () => {
  const browser = await chromium.launch();
  const tab = await browser.newPage({ viewport: { width: 1920, height: 1080 } });
  for (const [f, body] of [['04-v2-own-page.png', page], ['05-v2-own-page-long-press.png', longPress]]) {
    await tab.setContent(`<html><head><style>${css}</style></head><body>${body}</body></html>`, { waitUntil: 'load' });
    await tab.evaluate(() => document.fonts.ready);
    await tab.screenshot({ path: path.join(__dirname, f) });
  }
  await browser.close();
})();
