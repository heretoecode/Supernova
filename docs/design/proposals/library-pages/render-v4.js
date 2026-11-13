// v4 (owner, 13 Nov 2026): stats moved off the library pages (to Files › Statistics); the focused poster and its two
// lines are one card that grows with the ring round both; Customise page with the faint edge used over pages.
// v3 (owner, 13 Nov 2026): menus and panels at 95%; page sorted by size per episode with the value under each
// poster and the stats (titles, total size, on this Shield, on the network); list view with columns; Customise page as
// a floating panel with live changes (no Save).
// v2b: Edit page with the rows it really has today (no invented ones), in the rulebook look; tall picture because
// the panel scrolls.
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
  match: '<circle cx="9" cy="12" r="5"/><circle cx="15" cy="12" r="5"/>',
  star: '<path d="M12 3l2.7 5.6 6.1.9-4.4 4.3 1 6.1L12 17l-5.4 2.9 1-6.1L3.2 9.5l6.1-.9z"/>',
  lang: '<circle cx="12" cy="12" r="9"/><path d="M3 12h18M12 3a14 14 0 0 1 0 18M12 3a14 14 0 0 0 0 18"/>',
  studio: '<rect x="4" y="6" width="16" height="12" rx="2"/><path d="M9 3l3 3 3-3"/>',
  quick: '<path d="M13 3L5 14h6l-1 7 8-11h-6z"/>',
  order: '<path d="M7 4v16M3 16l4 4 4-4M14 6h7M14 12h5M14 18h3"/>',
  view: '<rect x="3" y="3" width="7" height="7" rx="1"/><rect x="14" y="3" width="7" height="7" rx="1"/><rect x="3" y="14" width="7" height="7" rx="1"/><rect x="14" y="14" width="7" height="7" rx="1"/>',
  save: '<path d="M5 3h11l3 3v15H5z"/><path d="M8 3v6h8V3M8 21v-7h8v7"/>',
  shield: '<rect x="3" y="5" width="18" height="12" rx="2"/><path d="M8 21h8M12 17v4"/>',
  net: '<rect x="4" y="4" width="16" height="7" rx="1.5"/><rect x="4" y="13" width="16" height="7" rx="1.5"/>',
  filter: '<path d="M3 5h18l-7 8v6l-4 2v-8z"/>',
  list: '<path d="M8 6h13M8 12h13M8 18h13M3 6h.1M3 12h.1M3 18h.1"/>',
  grid: '<rect x="3" y="3" width="7" height="7" rx="1"/><rect x="14" y="3" width="7" height="7" rx="1"/><rect x="3" y="14" width="7" height="7" rx="1"/><rect x="14" y="14" width="7" height="7" rx="1"/>',
  cols: '<rect x="3" y="4" width="18" height="16" rx="2"/><path d="M9 4v16M15 4v16"/>',
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
const toolbar = (sortLabel, list) => `<div class="toolbar">${chips(['All', 'Unwatched', 'In progress', 'Watched'], 'All')}
  ${tool('sort', sortLabel)}${tool('filter', 'Filters')}${tool(list ? 'grid' : 'list', list ? 'Grid view' : 'List view')}
  ${list ? tool('cols', 'Columns') : ''}${tool('edit', 'Customise page')}</div>`;
const art = ['#5b3a2e', '#2e4a5b', '#4b5b2e', '#5b2e4a', '#2e5b4f', '#3a2e5b', '#5b4a2e'];
const poster = (i, title, line, o) => `<div class="pc ${o.focus ? 'focus' : ''}"><span class="pimg" style="background:
  radial-gradient(ellipse at 50% 30%,${art[i % 7]} 0%,#121418 85%)">${o.watched ? `<i class="wt">${svg(I.tick, 22)}</i>`
  : ''}${o.progress ? `<i class="pb"><b style="width:${o.progress}%"></b></i>` : ''}</span><b>${title}</b>
  <small>${line}</small></div>`;
const TITLES = [['Frozen Frontier', '2.4 GB per episode', {}], ['Deep Blue Planet',
  '2.1 GB per episode', { watched: true }], ['Inside the Hive', '1.9 GB', { focus: true }],
['The Lost Rivers', '1.7 GB per episode', {}], ['The Green Valley', '1.6 GB per episode', {}],
['Tidelands', '1.4 GB per episode', { progress: 30 }], ['Mountain Light', '1.3 GB', {}],
['Wild Isles of the North', '1.2 GB per episode', { watched: true }], ['Rails Across Asia',
  '1.1 GB per episode', {}], ['The Salt Road', '1.0 GB', { progress: 40 }], ['City of Birds',
  '0.9 GB', {}], ['Desert Bloom', '0.9 GB', {}], ['The Coral Clock', '0.8 GB',
  { watched: true }], ['Night Shift Ocean', '0.7 GB', {}]];
const grid = (focusOff) => `<div class="grid">${TITLES.map(([t, l, o], i) => poster(i, t, o.line || l,
  Object.assign({}, o, focusOff ? { focus: false } : {}))).join('')}</div>`;
const page = `${nav('Documentaries', 'line')}${toolbar('Size per episode · Largest first')}${grid(false)}`;
const LROWS = [['Frozen Frontier', '1', '6', '14.4 GB', '2.4 GB', '2 Nov 2026'],
  ['Deep Blue Planet', '1', '6', '12.6 GB', '2.1 GB', '28 Oct 2026'], ['Inside the Hive', '—', '—', '1.9 GB', '1.9 GB',
    '25 Oct 2026'], ['The Lost Rivers', '1', '4', '6.8 GB', '1.7 GB', '20 Oct 2026'], ['The Green Valley', '1', '5',
    '8.1 GB', '1.6 GB', '18 Oct 2026'], ['Tidelands', '1', '4', '5.6 GB', '1.4 GB', '11 Oct 2026'], ['Mountain Light',
    '—', '—', '1.3 GB', '1.3 GB', '9 Oct 2026'], ['Wild Isles of the North', '1', '5', '6.0 GB', '1.2 GB',
    '2 Oct 2026']];
const listView = `${nav('Documentaries', 'line')}${toolbar('Size per episode · Largest first', true)}<div class="table">
  <div class="th"><span class="c1">Title</span><span>Seasons</span><span>Episodes</span><span>File size</span>
  <span class="sorted">Size per episode ${svg('<path d="M6 9l6 6 6-6"/>', 22)}</span><span>Date added</span></div>
  ${LROWS.map((r, i) => `<div class="tr ${i === 2 ? 'focus' : ''}"><span class="c1"><i class="th-art" style="background:
  radial-gradient(ellipse at 50% 30%,${art[i % 7]} 0%,#121418 85%)"></i>${r[0]}</span><span>${r[1]}</span>
  <span>${r[2]}</span><span>${r[3]}</span><span class="b">${r[4]}</span><span>${r[5]}</span></div>`).join('')}</div>`;
const mi = (ic, t, f) => `<div class="mi ${f ? 'focus' : ''}">${svg(I[ic], 30)}<span>${t}</span></div>`;
const mg = t => `<div class="mg"><i></i><span>${t}</span><i></i></div>`;
const longPress = `${nav('Documentaries', 'line')}${toolbar('Size per episode · Largest first')}${grid(false)}<div class="menu">${mg('Playback')}
  ${mi('play', 'Play', true)}${mg('Library')}${mi('watched', 'Mark as watched')}${mi('fix', 'Fix match')}
  ${mi('remove', 'Remove from library')}${mg('Files &amp; Media')}${mi('bin', 'Delete file')}</div>`;

const prow = (ic, name, right, f) => `<div class="prow ${f ? 'focus' : ''}">${svg(I[ic], 30)}<span class="pn">${name}
  </span>${right}</div>`;
const val = (v, chev) => `<span class="v">${v}${chev ? svg(I.chev, 26) : ''}</span>`;
const yrs = `<span class="v">${svg(I.minus, 24)}<b>1990</b>to<b>Any</b>${svg(I.plus, 24)}</span>`;
const editPage = `${nav('Documentaries', 'line')}${toolbar('Size per episode · Largest first')}${grid(true)}<div class="dim"></div><div class="panel">
  ${mg('What To Show')}
  ${prow('name', 'Page name', val('Documentaries', true))}
  ${prow('shows', 'Includes', chips(['Movies', 'TV Shows', 'Both'], 'Both'), true)}
  ${prow('genre', 'Genres', val('Documentary', true))}
  ${prow('match', 'Match', chips(['Any genre', 'All genres'], 'Any genre'))}
  ${prow('cal', 'Years', yrs)}
  ${prow('star', 'Minimum rating', `<span class="v">${svg(I.minus, 24)}<b>Any</b>${svg(I.plus, 24)}</span>`)}
  ${prow('lang', 'Original language', val('Any', true))}
  ${prow('studio', 'Studio / Network', val('Any', true))}
  ${mg('How To Show It')}
  ${prow('ring', 'Show', chips(['All', 'Unwatched', 'In progress', 'Watched'], 'All'))}
  ${prow('sort', 'Sort by', chips(['Title', 'Release date', 'Date added'], 'Release date'))}
  ${prow('order', 'Order', chips(['Newest', 'Oldest'], 'Newest'))}
  ${prow('view', 'View', chips(['Grid', 'List'], 'Grid'))}
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
.pc{border-radius:20px}.pc.focus{transform:scale(1.03);box-shadow:0 0 0 7px #16181C,0 0 0 12px #fff,0 18px 40px rgba(0,0,0,.6);
background:#16181C;position:relative;z-index:2}
.pc b{font-size:25px;font-weight:500;margin-top:14px;white-space:nowrap;overflow:hidden;text-overflow:ellipsis}
.pc small{font-size:21px;color:#C9D3E6;margin-top:2px;white-space:nowrap;overflow:hidden;text-overflow:ellipsis}
.wt{position:absolute;right:10px;top:10px;width:36px;height:36px;border-radius:50%;background:#F4F6FB;color:#0B0D12;
display:flex;align-items:center;justify-content:center}
.pb{position:absolute;left:12px;right:12px;bottom:12px;height:6px;border-radius:3px;background:rgba(255,255,255,.3)}
.pb b{display:block;height:6px;border-radius:3px;background:#fff;margin:0}
.menu{position:absolute;left:1050px;top:290px;padding:18px;border-radius:24px;background:rgba(22,24,28,.95);
box-shadow:0 0 0 2px rgba(255,255,255,.22),0 30px 80px rgba(0,0,0,.6);z-index:6}
.mg{display:flex;align-items:center;gap:14px;padding:14px 16px 8px;font-size:24px;font-weight:500}
.mg i{flex:1;height:2px;background:rgba(255,255,255,.18);min-width:30px}
.mi{display:flex;align-items:center;gap:20px;height:60px;padding:0 24px;border-radius:14px;font-size:28px;
white-space:nowrap}.mi svg{color:#C9D3E6}.mi.focus{background:#F4F6FB;color:#0B0D12}.mi.focus svg{color:#0B0D12}
.stats{position:absolute;left:96px;right:96px;top:122px;display:flex;font-size:25px}.stats .facts{margin-left:0}
.toolbar{top:130px !important}.grid{top:222px !important}
.table{position:absolute;left:96px;right:96px;top:216px;font-size:26px}
.th,.tr{display:grid;grid-template-columns:3fr 1fr 1fr 1.2fr 1.5fr 1.4fr;align-items:center;gap:20px;padding:0 24px}
.th{height:52px;color:#C9D3E6;font-size:23px;font-weight:500}.th .sorted{color:#fff;display:flex;align-items:center;gap:6px}
.tr{position:relative;height:84px;border-radius:16px}.tr:after{content:'';position:absolute;left:24px;right:24px;bottom:0;
height:2px;background:rgba(255,255,255,.08)}.tr.focus{background:#F4F6FB;color:#0B0D12}.tr.focus:after,
.tr:has(+ .tr.focus):after,.tr:last-child:after{display:none}.tr .b{font-weight:500}
.c1{display:flex;align-items:center;gap:18px;font-weight:500;white-space:nowrap;overflow:hidden}.th-art{width:44px;height:66px;
border-radius:6px;flex:none;box-shadow:inset 0 0 0 2px rgba(255,255,255,.6)}
.dim{position:absolute;inset:0;background:rgba(10,11,13,.55);z-index:5}
.panel .mg{margin-top:18px}
.panel{box-shadow:0 0 0 2px rgba(255,255,255,.22),0 30px 80px rgba(0,0,0,.6) !important;position:absolute;right:56px;top:48px;bottom:48px;overflow:hidden;border-radius:26px;width:max-content;min-width:760px;padding:16px 24px;
background:rgba(22,24,28,.95);z-index:6}
.prow{position:relative;display:flex;align-items:center;gap:20px;height:72px;padding:0 24px;border-radius:18px;
font-size:28px}.prow:after{content:'';position:absolute;left:24px;right:24px;bottom:0;height:2px;
background:rgba(255,255,255,.08)}.prow.focus:after,.prow:has(+ .mg):after,.prow:last-child:after,
.prow:has(+ .prow.focus):after{display:none}.prow svg{color:#C9D3E6;flex:none}.pn{font-weight:500;margin-right:40px}
.prow.focus{background:#F4F6FB;color:#0B0D12}.prow.focus svg{color:#0B0D12}
.prow .chips{margin-left:auto}.prow.focus .chips i.on{background:#0B0D12;color:#fff}
.v{margin-left:auto;display:flex;align-items:center;gap:12px}.v b{font-weight:500}`;
(async () => {
  const browser = await chromium.launch();
  const tab = await browser.newPage({ viewport: { width: 1920, height: 1080 } });
  for (const [f, body] of [['11-v4-page.png', page], ['12-v4-list-view.png', listView], ['13-v4-customise-page.png', editPage]]) {
    await tab.setContent(`<html><head><style>${css}</style></head><body>${body}</body></html>`, { waitUntil: 'load' });
    await tab.evaluate(() => document.fonts.ready);
    await tab.screenshot({ path: path.join(__dirname, f) });
  }
  await browser.close();
})();
