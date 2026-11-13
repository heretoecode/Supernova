// v8 (owner, 13 Nov 2026): calmer caption (only the title bright; no 'Resume', the bar says it); list view with the
// Columns menu; Page name with the keyboard beside the panel; long-press menu for a TV show.
// v7 (owner, 13 Nov 2026): cards grow 8%; three-line caption (title, today's facts or resume, sort value) with the
// progress bar on the caption's bottom edge; menus open under the button pressed; Filters with the Watched list on top;
// the Genre (with icons), Year (live) and Streaming services lists; the whole Sort menu.
// v6 (owner, 13 Nov 2026): ring exactly on the card edge; toolbar Filters · Sort · Order · List view, Customise page
// right; toolbar focus = white box; Filters: Watched as a row, Clear filters at the bottom; Customise page whole panel
// (Match folded into Genres, Years opens a list); the Genres, Years and Studio / Network lists.
// v5 (owner, 13 Nov 2026): poster and caption as one card (caption on its own box, as today but not blue), the ring
// tight round both; the watched quick filters move into Filters; Customise page locked to the right, Columns appears
// beside Grid view without anything moving; Filters menu (Clear filters first, Show, Filter by); Minimum rating chips.
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
  x: '<path d="M6 6l12 12M18 6L6 18"/>',
  eye: '<path d="M2 12s4-7 10-7 10 7 10 7-4 7-10 7S2 12 2 12z"/><circle cx="12" cy="12" r="3"/>',
  half: '<circle cx="12" cy="12" r="9"/><path d="M12 3a9 9 0 0 1 0 18z" fill="currentColor"/>',
  cloud: '<path d="M7 18h10a4 4 0 0 0 .6-8A6 6 0 0 0 6 9.5 4.3 4.3 0 0 0 7 18z"/>',
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
const tool = (ic, t, f) => `<span class="tool ${f ? 'tf' : ''}">${svg(I[ic], 28)}${t}</span>`;
const facts = list => `<span class="facts">${list.map(([ic, v, l]) => `<span>${svg(I[ic], 26)}<b>${v}</b> ${l}</span>`)
  .join('<i>·</i>')}</span>`;
const toolbar = (sortLabel, list, ff) => `<div class="toolbar">${tool('filter', 'Filters', ff)}
  ${tool('sort', 'Size per episode')}${tool('order', 'Largest first')}
  ${tool(list ? 'grid' : 'list', list ? 'Grid view' : 'List view')}${list ? tool('cols', 'Columns') : ''}
  <span class="right">${tool('edit', 'Customise page')}</span></div>`;
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
const listView = `${nav('Documentaries', 'line')}${toolbar('Size per episode · Largest first', true)}${'FILTERS'}<div class="table">
  <div class="th"><span class="c1">Title</span><span>Seasons</span><span>Episodes</span><span>File size</span>
  <span class="sorted">Size per episode ${svg('<path d="M6 9l6 6 6-6"/>', 22)}</span><span>Date added</span></div>
  ${LROWS.map((r, i) => `<div class="tr ${i === 2 ? 'focus' : ''}"><span class="c1"><i class="th-art" style="background:
  radial-gradient(ellipse at 50% 30%,${art[i % 7]} 0%,#121418 85%)"></i>${r[0]}</span><span>${r[1]}</span>
  <span>${r[2]}</span><span>${r[3]}</span><span class="b">${r[4]}</span><span>${r[5]}</span></div>`).join('')}</div>`;
const mt = (ic, t, on) => `<div class="mi">${svg(I[ic], 30)}<span>${t}</span>${on ? `<i class="mtk">${svg(I.tick,
  28)}</i>` : ''}</div>`;
const mc = (ic, t, v) => `<div class="mi">${svg(I[ic], 30)}<span>${t}</span><em class="mv">${v}</em>${svg(I.chev, 26)}</div>`;
const filtersMenu = () => `<div class="menu fm">${mi('x', 'Clear filters', true)}${mg('Show')}${mt('ring', 'All', true)}
  ${mt('eye', 'Unwatched')}${mt('half', 'In progress')}${mt('watched', 'Watched')}${mg('Filter By')}
  ${mc('genre', 'Genre', 'Any')}${mc('cal', 'Year', 'Any')}${mc('cloud', 'Streaming services', 'Any')}</div>`;
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
  ${prow('star', 'Minimum rating', chips(['Any', '6+', '7+', '8+', '9+'], 'Any'))}
  ${prow('lang', 'Original language', val('Any', true))}
  ${prow('studio', 'Studio / Network', val('Any', true))}
  ${mg('How To Show It')}
  ${prow('ring', 'Show', chips(['All', 'Unwatched', 'In progress', 'Watched'], 'All'))}
  ${prow('sort', 'Sort by', chips(['Title', 'Release date', 'Date added'], 'Release date'))}
  ${prow('order', 'Order', chips(['Newest', 'Oldest'], 'Newest'))}
  ${prow('view', 'View', chips(['Grid', 'List'], 'Grid'))}
  ${mg('This Page')}${prow('bin', 'Delete page', '')}</div>`;


const N = nav('Documentaries', 'line');
const page6 = `${N}${toolbar('', false, false)}${grid(false)}`;
const tbFocus = `${N}${toolbar('', false, true)}${grid(true)}`;
const fr = (ic, t, v, f) => `<div class="mi ${f ? 'focus' : ''}">${svg(I[ic], 30)}<span>${t}</span><em class="mv">${v}</em>
  ${svg(I.chev, 26)}</div>`;
const filters6 = `${N}${toolbar('', false, false)}${grid(true)}<div class="menu fm">${fr('ring', 'Watched', 'All', true)}
  ${fr('genre', 'Genre', 'Any')}${fr('cal', 'Year', 'Any')}${fr('cloud', 'Streaming services', 'Any')}
  <div class="mdiv"></div>${mi('x', 'Clear filters')}</div>`;
const cust6 = `${N}${toolbar('', false, false)}${grid(true)}<div class="dim"></div><div class="panel full">
  ${mg('What To Show')}${prow('name', 'Page name', val('Documentaries', true))}
  ${prow('shows', 'Includes', chips(['Movies', 'TV Shows', 'Both'], 'Both'), true)}
  ${prow('genre', 'Genres', val('Documentary, Nature', true))}${prow('cal', 'Years', val('1990 – Any', true))}
  ${prow('star', 'Minimum rating', chips(['Any', '6+', '7+', '8+', '9+'], 'Any'))}
  ${prow('lang', 'Original language', val('Any', true))}${prow('studio', 'Studio / Network', val('Any', true))}
  ${mg('How To Show It')}${prow('ring', 'Show', chips(['All', 'Unwatched', 'In progress', 'Watched'], 'All'))}
  ${prow('sort', 'Sort by', chips(['Title', 'Release date', 'Date added'], 'Release date'))}
  ${prow('order', 'Order', chips(['Newest', 'Oldest'], 'Newest'))}${prow('view', 'View', chips(['Grid', 'List'], 'Grid'))}
  ${mg('This Page')}${prow('bin', 'Delete page', '')}</div>`;
const lrow = (name, line, on, f) => `<div class="lr ${f ? 'focus' : ''}"><span class="lt"><b>${name}</b>${line
  ? `<small>${line}</small>` : ''}</span>${on ? `<i class="ltk">${svg(I.tick, 26)}</i>` : ''}</div>`;
const step2 = (t, v, f) => `<div class="lr ${f ? 'focus' : ''}"><span class="lt"><b>${t}</b></span><span class="v">
  ${svg(I.minus, 24)}<b>${v}</b>${svg(I.plus, 24)}</span></div>`;
const genresL = `<div class="sl"><div class="lr"><span class="lt"><b>Match</b></span>${chips(['Any', 'All'], 'Any')}</div>
  ${mg('Selected')}${lrow('Documentary', '61 titles', true, true)}${lrow('Nature', '38 titles', true)}${mg('All')}
  ${lrow('History', '14 titles')}${lrow('Science', '11 titles')}${lrow('Travel', '6 titles')}${lrow('War', '3 titles')}</div>`;
const yearsL = `<div class="sl">${mg('Quick Choices')}${lrow('Any year')}${lrow('This year', '2026')}
  ${lrow('Last 5 years', '2022 – 2026')}${lrow('2020s')}${lrow('2010s')}${lrow('2000s')}${lrow('1990s')}${lrow('Before 1990')}
  ${mg('Your Own Range')}${step2('From', '1990', true)}${step2('To', 'Any')}</div>`;
const studioL = `<div class="sl"><div class="lr"><span class="lt" style="flex-direction:row;gap:14px;color:#C9D3E6">
  ${svg(I.search, 28)}Search studios and networks</span></div>${mg('All')}${lrow('Apple TV+', '3 titles')}
  ${lrow('BBC Studios', '31 titles', false, true)}${lrow('Discovery', '7 titles')}${lrow('National Geographic', '12 titles')}
  ${lrow('Netflix', '9 titles')}${lrow('PBS', '4 titles')}${lrow('Sky Nature', '2 titles')}</div>`;
const lists6 = `${N}${toolbar('', false, false)}${grid(true)}<div class="dim"></div><div class="trio">${genresL}${yearsL}
  ${studioL}</div>`;


const RES = path.join(__dirname, '../../../../res/drawable');
function logo(name, colour) {
  const xml = fs.readFileSync(path.join(RES, `provider_${name}.xml`), 'utf8');
  const vw = xml.match(/viewportWidth="([\d.]+)"/)[1];
  const vh = xml.match(/viewportHeight="([\d.]+)"/)[1];
  const p = [...xml.matchAll(/pathData="([^"]+)"/g)].map(m => `<path d="${m[1]}" fill="${colour}"/>`).join('');
  return `<svg viewBox="0 0 ${vw} ${vh}" preserveAspectRatio="xMidYMid meet">${p}</svg>`;
}
const S7 = {
  doc: '<rect x="3" y="7" width="13" height="10" rx="2"/><path d="M16 11l5-3v8l-5-3"/>',
  nature: '<path d="M5 19c0-8 6-14 15-14 0 9-6 15-14 15"/><path d="M5 19l8-8"/>',
  history: '<path d="M3 21h18M5 21V10M9 21V10M15 21V10M19 21V10M3 10l9-6 9 6z"/>',
  science: '<path d="M9 3h6M10 3v6L4 19a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2L14 9V3"/>',
  travel: '<path d="M2 16l20-8-6 12-3-5z"/><path d="M13 15l-3 5"/>',
  war: '<path d="M12 3l8 3v6c0 5-4 8-8 9-4-1-8-4-8-9V6z"/>',
  all: '<circle cx="12" cy="12" r="9"/>',
  unw: '<path d="M2 12s4-7 10-7 10 7 10 7-4 7-10 7S2 12 2 12z"/><circle cx="12" cy="12" r="3"/>',
  prog: '<circle cx="12" cy="12" r="9"/><path d="M12 3a9 9 0 0 1 0 18z" fill="currentColor"/>',
  wat: '<circle cx="12" cy="12" r="9"/><path d="M8 12.5l2.8 2.8L16.5 9.5"/>',
  added: '<rect x="3" y="5" width="18" height="16" rx="2"/><path d="M3 10h18M12 13v5M9.5 15.5h5"/>',
  title: '<path d="M5 20L10 4h4l5 16M7.5 14h9"/>',
  rel: '<rect x="3" y="5" width="18" height="16" rx="2"/><path d="M3 10h18M8 3v4M16 3v4"/>',
  run: '<circle cx="12" cy="13" r="8"/><path d="M12 9v4l3 2M10 2h4"/>',
  trend: '<path d="M3 17l6-6 4 4 8-8M15 7h6v6"/>',
  pop: '<path d="M12 20s-7-4.5-7-10a4 4 0 0 1 7-2.6A4 4 0 0 1 19 10c0 5.5-7 10-7 10z"/>',
  size: '<rect x="3" y="6" width="18" height="12" rx="2"/><path d="M7 14h.1M11 14h6"/>',
  per: '<rect x="3" y="4" width="18" height="12" rx="2"/><path d="M7 20h10M9 8v4M12 8v4M15 8v4"/>',
  res: '<rect x="3" y="5" width="18" height="14" rx="2"/><path d="M7 9v6M7 12h3M10 9v6M14 9h1.5a3 3 0 0 1 0 6H14z"/>',
  hdr: '<circle cx="12" cy="12" r="4"/><path d="M12 2v3M12 19v3M2 12h3M19 12h3M5 5l2 2M17 17l2 2M5 19l2-2M17 7l2-2"/>',
  audio: '<path d="M4 9v6h4l5 4V5L8 9z"/><path d="M16 9a4 4 0 0 1 0 6M19 6a8 8 0 0 1 0 12"/>',
  codec: '<rect x="6" y="6" width="12" height="12" rx="2"/><path d="M9 2v4M15 2v4M9 18v4M15 18v4M2 9h4M2 15h4M18 9h4M18 15h4"/>',
  bit: '<path d="M4 16a8 8 0 1 1 16 0"/><path d="M12 16l4-5"/>',
  box: '<path d="M3 7l9-4 9 4v10l-9 4-9-4z"/><path d="M3 7l9 4 9-4M12 11v10"/>',
  mod: '<path d="M4 20h4L19 9l-4-4L4 16z"/>',
  path: '<path d="M3 7a2 2 0 0 1 2-2h4l2 2h8a2 2 0 0 1 2 2v8a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2z"/>',
  seas: '<path d="M12 3l9 5-9 5-9-5z"/><path d="M3 13l9 5 9-5"/>',
  eps: '<rect x="3" y="4" width="18" height="12" rx="2"/><path d="M7 20h10"/><path d="M10 8l5 2.5-5 2.5z"/>',
};
const card7 = (i, title, l2, l3, o) => `<div class="pc7 ${o.focus ? 'focus' : ''}"><span class="pimg" style="background:
  radial-gradient(ellipse at 50% 30%,${art[i % 7]} 0%,#121418 85%)">${o.watched ? `<i class="wt">${svg(I.tick, 22)}</i>`
  : ''}</span><div class="cap"><b>${title}</b><small>${l2}</small>${l3 ? `<em>${l3}</em>` : ''}${o.progress
  ? `<i class="cpb"><b style="width:${o.progress}%"></b></i>` : ''}</div></div>`;
const T7 = [['Frozen Frontier', '2021 · 6 episodes', '2.4 GB per episode', {}],
  ['Deep Blue Planet', '2025 · 6 episodes', '2.1 GB per episode', { watched: true }],
  ['Inside the Hive', '2023 · 1 h 24 min', '1.9 GB', { focus: true }],
  ['The Lost Rivers', '2022 · 4 episodes', '1.7 GB per episode', {}],
  ['The Green Valley', '2023 · 5 episodes', '1.6 GB per episode', {}],
  ['Tidelands', 'S1 E2 · 31 min left', '1.4 GB per episode', { progress: 30 }],
  ['Mountain Light', '2023 · 1 h 47 min', '1.3 GB', {}],
  ['Wild Isles of the North', '2024 · 5 episodes', '1.2 GB per episode', { watched: true }],
  ['Rails Across Asia', '2020 · 3 episodes', '1.1 GB per episode', {}],
  ['The Salt Road', '42 min left', '1.0 GB', { progress: 40 }], ['City of Birds', '2019 · 1 h 28 min',
  '0.9 GB', {}], ['Desert Bloom', '2020 · 1 h 22 min', '0.9 GB', {}], ['The Coral Clock', '2019 · 1 h 41 min', '0.8 GB',
  { watched: true }], ['Night Shift Ocean', '2022 · 1 h 31 min', '0.7 GB', {}]];
const grid7 = f => `<div class="grid7">${T7.map(([t, a, b, o], i) => card7(i, t, a, b,
  Object.assign({}, o, f ? {} : { focus: false }))).join('')}</div>`;
const lrowI = (ic, name, line, on, f) => `<div class="lr ${f ? 'focus' : ''}"><span class="lead">${svg(S7[ic], 28)}</span>
  <span class="lt"><b>${name}</b>${line ? `<small>${line}</small>` : ''}</span>${on ? `<i class="ltk">${svg(I.tick, 26)}
  </i>` : ''}</div>`;
const lrowL = (lg, col, name, line, on) => `<div class="lr"><span class="lead lg">${logo(lg, col)}</span><span class="lt">
  <b>${name}</b><small>${line}</small></span>${on ? `<i class="ltk">${svg(I.tick, 26)}</i>` : ''}</div>`;
const page7 = `${N}${toolbar('', false, false)}${grid7(true)}`;
const filters7 = `${N}${toolbar('', false, false)}${grid7(false)}<div class="menu fm7 behind7">
  ${fr('ring', 'Watched', 'All', true)}${fr('genre', 'Genre', 'Any')}${fr('cal', 'Year', 'Any')}
  ${fr('cloud', 'Streaming services', 'Any')}<div class="mdiv"></div>${mi('x', 'Clear filters')}</div>
  <div class="sl front7">${lrowI('all', 'All', '', true, true)}${lrowI('unw', 'Unwatched', '35 titles')}
  ${lrowI('prog', 'In progress', '4 titles')}${lrowI('wat', 'Watched', '57 titles')}</div>`;
const genre7 = `<div class="sl"><div class="lr"><span class="lt"><b>Match</b></span>${chips(['Any', 'All'], 'Any')}</div>
  ${mg('Selected')}${lrowI('doc', 'Documentary', '61 titles', true, true)}${lrowI('nature', 'Nature', '38 titles', true)}
  ${mg('All')}${lrowI('history', 'History', '14 titles')}${lrowI('science', 'Science', '11 titles')}
  ${lrowI('travel', 'Travel', '6 titles')}${lrowI('war', 'War', '3 titles')}</div>`;
const year7 = `<div class="sl">${mg('Quick Choices')}${lrow('Any year', '', true)}${lrow('This year', '2026 · 4 titles')}
  ${lrow('Last 5 years', '2022 – 2026 · 41 titles')}${lrow('2020s', '48 titles')}${lrow('2010s', '31 titles')}
  ${lrow('2000s', '12 titles')}${lrow('1990s', '5 titles')}${mg('Your Own Range')}${step2('From', '1994', true)}
  ${step2('To', '2026')}</div>`;
const stream7 = `<div class="sl">${mg('Selected')}${lrowL('netflix', '#E50914', 'Netflix', '9 titles', true)}${mg('All')}
  ${lrowL('appletv', '#FFFFFF', 'Apple TV+', '3 titles')}${lrowL('disneyplus', '#2E6BFF', 'Disney+', '5 titles')}
  ${lrowL('paramountplus', '#0064FF', 'Paramount+', '2 titles')}${lrowL('primevideo', '#1A98FF', 'Prime Video', '6 titles')}
  </div>`;
const lists7 = `${N}${toolbar('', false, false)}${grid7(false)}<div class="dim"></div><div class="trio">${genre7}${year7}
  ${stream7}</div>`;
const st = (ic, t, on, f) => `<div class="mi ${f ? 'focus' : ''}">${svg(S7[ic], 30)}<span>${t}</span>${on
  ? `<i class="mtk">${svg(I.tick, 28)}</i>` : ''}</div>`;
const sort7 = `${N}${toolbar('', false, false)}${grid7(false)}<div class="menu sm7">${mg('General')}
  ${st('added', 'Date added')}${st('title', 'Title')}${st('rel', 'Release date')}${st('run', 'Running time')}
  ${mg('Trakt')}${st('trend', 'Trending')}${st('pop', 'Popular')}${mg('Files')}${st('size', 'File size')}
  ${st('per', 'Size per episode', true, true)}${st('res', 'Resolution')}${st('hdr', 'HDR')}${st('audio', 'Audio')}
  ${st('codec', 'Codec')}${st('bit', 'Bitrate')}${st('box', 'Container')}${st('mod', 'Modified')}${st('path', 'Path')}
  ${mg('Series')}${st('seas', 'Seasons')}${st('eps', 'Episodes')}</div>`;


const keys8 = [['1', '2', '3', '4', '5', '6', '7', '8', '9', '0'], ['q', 'w', 'e', 'r', 't', 'y', 'u', 'i', 'o', 'p'],
  ['a', 's', 'd', 'f', 'g', 'h', 'j', 'k', 'l', '/'], ['⇧', 'z', 'x', 'c', 'v', 'b', 'n', 'm', '.', '@'],
  ['#+=', 'mic', 'Space', '⌫', 'Clear']];
const MIC = '<rect x="9" y="3" width="6" height="11" rx="3"/><path d="M5 11a7 7 0 0 0 14 0M12 18v3"/>';
const kb8 = f => `<div class="kbp8">${keys8.map(r => `<div class="krow">${r.map(k => `<span class="key ${k.length > 1
  ? 'wide' : ''} ${k === 'Space' ? 'space' : ''} ${k === f ? 'kf' : ''}">${k === 'mic' ? svg(MIC, 28) : k}</span>`)
  .join('')}</div>`).join('')}</div>`;
const UPDN = '<path d="M8 9l4-4 4 4M8 15l4 4 4-4"/>';
const CI = { 'Seasons': 'seas', 'Episodes': 'eps', 'File size': 'size', 'Size per episode': 'per', 'Date added': 'added',
  'Audio': 'audio', 'Bitrate': 'bit', 'Codec': 'codec', 'Container': 'box', 'HDR': 'hdr', 'Modified': 'mod', 'Path': 'path',
  'Release date': 'rel', 'Resolution': 'res', 'Running time': 'run' };
const colRow = (name, on, f) => `<div class="mi ${f ? 'focus' : ''}">${svg(S7[CI[name]], 30)}<span>${name}</span>${f ? `<i class="mv2">
  ${svg(UPDN, 26)}</i>` : ''}${on ? `<i class="mtk">${svg(I.tick, 28)}</i>` : ''}</div>`;
const listCols = `${N}${toolbar('', true, false)}<div class="table">
  <div class="th"><span class="c1">Title</span><span>Seasons</span><span>Episodes</span><span>File size</span>
  <span class="sorted">Size per episode</span><span>Date added</span></div>
  ${LROWS.map((r, i) => `<div class="tr"><span class="c1"><i class="th-art" style="background:radial-gradient(ellipse
  at 50% 30%,${art[i % 7]} 0%,#121418 85%)"></i>${r[0]}</span><span>${r[1]}</span><span>${r[2]}</span><span>${r[3]}
  </span><span class="b">${r[4]}</span><span>${r[5]}</span></div>`).join('')}</div>
  <div class="menu cm8">${mg('Shown')}${colRow('Seasons', true)}${colRow('Episodes', true)}${colRow('File size', true)}
  ${colRow('Size per episode', true, true)}${colRow('Date added', true)}${mg('Hidden')}${colRow('Audio')}
  ${colRow('Bitrate')}${colRow('Codec')}${colRow('Container')}${colRow('HDR')}${colRow('Modified')}${colRow('Path')}
  ${colRow('Release date')}${colRow('Resolution')}${colRow('Running time')}<div class="mdiv"></div>
  ${mi('restart', 'Reset to default')}</div>`;
const custKb = `${N}${toolbar('', false, false)}${grid(true)}<div class="dim"></div>${kb8('i')}<div class="panel full">
  ${mg('What To Show')}${prow('name', 'Page name', `<span class="v fld">Documentar<i class="caret8"></i></span>`)}
  ${prow('shows', 'Includes', chips(['Movies', 'TV Shows', 'Both'], 'Both'))}
  ${prow('genre', 'Genres', val('Documentary, Nature', true))}${prow('cal', 'Years', val('1990 – Any', true))}
  ${prow('star', 'Minimum rating', chips(['Any', '6+', '7+', '8+', '9+'], 'Any'))}
  ${prow('lang', 'Original language', val('Any', true))}${prow('studio', 'Studio / Network', val('Any', true))}
  ${mg('How To Show It')}${prow('ring', 'Show', chips(['All', 'Unwatched', 'In progress', 'Watched'], 'All'))}
  ${prow('sort', 'Sort by', chips(['Title', 'Release date', 'Date added'], 'Release date'))}
  ${prow('order', 'Order', chips(['Newest', 'Oldest'], 'Newest'))}${prow('view', 'View', chips(['Grid', 'List'], 'Grid'))}
  ${mg('This Page')}${prow('bin', 'Delete page', '')}</div>`;
const T7b = T7.map((t, i) => [t[0], t[1], t[2], Object.assign({}, t[3], { focus: i === 5 })]);
const gridB = `<div class="grid7">${T7b.map(([t, a, b, o], i) => card7(i, t, a, b, o)).join('')}</div>`;
const PLAY = '<path d="M7 4l13 8-13 8z"/>';
const tvPress = `${N}${toolbar('', false, false)}${gridB}<div class="menu tvm">${mg('Playback')}
  ${mi('play', 'Resume S1 E2', true)}${mi('restart', 'Play from the start')}${mg('Library')}
  ${mi('watched', 'Mark show as watched')}${mi('fix', 'Fix match')}${mi('remove', 'Remove from library')}
  ${mg('Files &amp; Media')}${mi('bin', 'Delete all episode files')}</div>`;

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
.pc{border-radius:14px;background:#24272D;overflow:visible;box-shadow:inset 0 0 0 2px rgba(255,255,255,.12)}
.pc .pimg{border-radius:14px 14px 0 0}.pc b{margin:12px 14px 0}.pc small{margin:2px 14px 14px}
.pc.focus{transform:scale(1.03);box-shadow:0 0 0 5px #fff,0 18px 40px rgba(0,0,0,.6);position:relative;z-index:2}
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
.toolbar{top:130px !important}
.cm8{left:918px !important;top:196px !important;min-width:420px}.mv2{margin-left:auto;display:flex;padding-left:20px}
.mv2+.mtk{padding-left:14px}.cm8 .mi span{margin-right:30px}
.tvm{left:880px !important;top:240px !important}
.kbp8{position:absolute;left:96px;top:150px;z-index:7;width:820px;padding:22px;border-radius:26px;
background:rgba(22,24,28,.95);box-shadow:0 0 0 2px rgba(255,255,255,.22),0 30px 80px rgba(0,0,0,.6);display:flex;
flex-direction:column;gap:12px}.krow{display:flex;gap:10px}.key{flex:1;height:64px;border-radius:16px;background:#2a2d33;
display:flex;align-items:center;justify-content:center;font-size:30px;font-weight:500}.key.wide{flex:1.6;font-size:24px}
.key.space{flex:3}.key.kf{background:#F4F6FB;color:#0B0D12}
.fld{color:#fff;font-weight:500}.caret8{display:inline-block;width:3px;height:34px;background:#fff;margin-left:3px;vertical-align:middle}
.grid7{position:absolute;left:96px;right:96px;top:222px;display:grid;grid-template-columns:repeat(7,minmax(0,1fr));
gap:44px 34px}
.pc7{position:relative;border-radius:14px;background:#24272D;box-shadow:inset 0 0 0 2px rgba(255,255,255,.12)}
.pc7 .pimg{border-radius:14px 14px 0 0}
.pc7.focus{transform:scale(1.08);box-shadow:0 0 0 5px #fff,0 18px 40px rgba(0,0,0,.6);z-index:2}
.cap{position:relative;padding:12px 14px 16px;display:flex;flex-direction:column}
.cap b{font-size:24px;font-weight:500;white-space:nowrap;overflow:hidden;text-overflow:ellipsis}
.cap small{font-size:20px;color:#C9D3E6;margin-top:3px;white-space:nowrap;overflow:hidden;text-overflow:ellipsis}
.cap em{font-style:normal;font-size:20px;color:#C9D3E6;font-weight:400;margin-top:2px;white-space:nowrap}
.cpb{position:absolute;left:0;right:0;bottom:0;height:5px;background:rgba(255,255,255,.2);border-radius:0 0 14px 14px;
overflow:hidden}.cpb b{display:block;height:5px;background:#fff}
.fm7{left:96px !important;top:196px !important}.sm7{left:250px !important;top:196px !important}
.behind7{filter:brightness(.45);transform:scale(.96);transform-origin:left top}
.front7{position:absolute;left:400px;top:270px;z-index:8;width:auto !important;min-width:440px}
.lr .lead{display:flex;width:56px;justify-content:center;color:#C9D3E6;flex:none}.lr.focus .lead{color:#0B0D12}
.lead.lg svg{width:56px;height:30px}
.tool.tf{background:#F4F6FB;color:#0B0D12;border-radius:14px;padding:10px 18px}.tool.tf svg{color:#0B0D12}
.mdiv{height:2px;background:rgba(255,255,255,.18);margin:10px 16px}
.panel.full{top:24px !important;bottom:auto !important}.panel.full .prow{height:62px}
.trio{position:absolute;inset:40px 56px;display:flex;justify-content:space-between;align-items:flex-start;z-index:7}
.sl{width:570px;padding:16px;border-radius:24px;background:rgba(22,24,28,.95);
box-shadow:0 0 0 2px rgba(255,255,255,.22),0 30px 80px rgba(0,0,0,.6)}
.lr{position:relative;display:flex;align-items:center;gap:16px;height:68px;padding:0 20px;border-radius:14px;font-size:27px}
.lr:after{content:'';position:absolute;left:20px;right:20px;bottom:0;height:2px;background:rgba(255,255,255,.08)}
.lr.focus:after,.lr:has(+ .mg):after,.lr:last-child:after,.lr:has(+ .lr.focus):after{display:none}
.lr.focus{background:#F4F6FB;color:#0B0D12}.lt{flex:1;display:flex;flex-direction:column}.lt b{font-weight:500}
.lt small{font-size:21px;color:#C9D3E6}.lr.focus .lt small{color:#3A4256}.ltk{display:flex}
.lr .v{margin-left:auto;display:flex;align-items:center;gap:14px}.lr .v b{font-weight:500}.toolbar .right{margin-left:auto}
.fm{top:196px !important;left:520px !important}
.mtk{margin-left:auto;padding-left:30px;display:flex}.mi .mv{font-style:normal;margin-left:auto;padding-left:40px;color:#C9D3E6}
.mi.focus .mv{color:#3A4256}.grid{top:222px !important}
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
  for (const [f, body, h] of [['27-v8-page-calm-caption.png', page7], ['28-v8-list-columns.png', listCols, 1380],
    ['29-v8-page-name-keyboard.png', custKb], ['30-v8-tv-show-long-press.png', tvPress]]) {
    await tab.setViewportSize({ width: 1920, height: h || 1080 });
    await tab.setViewportSize({ width: 1920, height: h || 1080 });
    await tab.setContent(`<html><head><style>${css}body{height:${h || 1080}px}</style></head><body>${body}</body></html>`, { waitUntil: 'load' });
    await tab.evaluate(() => document.fonts.ready);
    await tab.screenshot({ path: path.join(__dirname, f) });
  }
  await browser.close();
})();
