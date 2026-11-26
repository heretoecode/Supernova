// v14 (owner, 20 Nov 2026): the Sort menu as approved (25), redrawn with today's rules for the TV and with the sheet's
// groups: General · Trakt · Files · Series (Series only where TV shows are). Taller than the screen, so it scrolls
// inside with arrows beside its foot / top (RULES §16). Order keeps one width (the wider of its two words).
// 10 Movies, opened: focus on the current sort (Date added). 11 Documentaries (mixed), scrolled to the foot.
// Run: FONT_DIR=<dir with roboto-latin-*-normal.woff2> node render-v14-sort.js
const { svg, I, render, MOV } = require('./render-v11-filters.js');
const v11 = require('./render-v11-filters.js');

const SLIDERS = '<path d="M4 6h8M16 6h4M4 12h2M10 12h10M4 18h10M18 18h2"/><circle cx="14" cy="6" r="2"/>'
  + '<circle cx="8" cy="12" r="2"/><circle cx="16" cy="18" r="2"/>';
const S = {
  added: '<rect x="3" y="5" width="18" height="16" rx="2"/><path d="M3 10h18M12 13v5M9.5 15.5h5"/>',
  title: '<path d="M5 20L10 4h4l5 16M7.5 14h9"/>',
  rel: '<rect x="3" y="5" width="18" height="16" rx="2"/><path d="M3 10h18M8 3v4M16 3v4"/>',
  trend: '<path d="M3 17l6-6 4 4 8-8M15 7h6v6"/>',
  pop: '<path d="M12 20s-7-4.5-7-10a4 4 0 0 1 7-2.6A4 4 0 0 1 19 10c0 5.5-7 10-7 10z"/>',
  run: '<circle cx="12" cy="13" r="8"/><path d="M12 9v4l3 2M10 2h4"/>',
  res: '<rect x="3" y="5" width="18" height="14" rx="2"/><path d="M7 9v6M7 12h3M10 9v6M14 9h1.5a3 3 0 0 1 0 6H14z"/>',
  hdr: '<circle cx="12" cy="12" r="4"/><path d="M12 2v2M12 20v2M4.9 4.9l1.4 1.4M17.7 17.7l1.4 1.4M2 12h2M20 12h2'
    + 'M4.9 19.1l1.4-1.4M17.7 6.3l1.4-1.4"/>',
  audio: '<path d="M4 9v6h4l5 4V5L8 9z"/><path d="M16.5 8.5a5 5 0 0 1 0 7M19 6a8.5 8.5 0 0 1 0 12"/>',
  size: '<rect x="3" y="6" width="18" height="12" rx="2"/><path d="M7 14h.1M11 14h6"/>',
  mod: '<path d="M4 20h4L19 9l-4-4L4 16z"/><path d="M14 6l4 4"/>',
  codec: '<rect x="6" y="6" width="12" height="12" rx="1.5"/><path d="M9 2v4M15 2v4M9 18v4M15 18v4M2 9h4M2 15h4M18 9h4'
    + 'M18 15h4"/>',
  bit: '<path d="M4 18a8 8 0 1 1 16 0"/><path d="M12 18l4-6"/>',
  cont: '<path d="M12 3l8 4.5v9L12 21l-8-4.5v-9z"/><path d="M4 7.5l8 4.5 8-4.5M12 12v9"/>',
  path: '<path d="M3 7a2 2 0 0 1 2-2h4l2 2h8a2 2 0 0 1 2 2v8a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2z"/>',
  seasons: '<path d="M12 3l9 5-9 5-9-5z"/><path d="M3 13l9 5 9-5"/>',
  eps: '<rect x="3" y="4" width="18" height="13" rx="2"/><path d="M10 8l4 2.5-4 2.5zM8 21h8"/>',
  per: '<rect x="3" y="4" width="18" height="12" rx="2"/><path d="M7 20h10M9 8v4M12 8v4M15 8v4"/>',
  up: '<path d="M6 15l6-6 6 6"/>',
  down: '<path d="M6 9l6 6 6-6"/>',
};

const extraCss = `
.tb .fixed{justify-content:center}
.sortm{position:absolute;left:206px;top:166px;width:auto}
.clip{overflow:hidden}
.sortm .i,.sortm .h{margin-right:44px}
.arrow{position:absolute;color:#fff;z-index:9}
.arrow.dim{opacity:.35}
`;

function group(name, rows, current, focus) {
  let h = `<div class="h">${name}</div>`;
  rows.forEach(([ic, label]) => {
    const on = label === focus ? ' on' : '';
    const tick = label === current ? `<span class="ch" style="color:inherit">${svg(I.tick)}</span>`
      : '<span style="width:28px"></span>';
    h += `<div class="i one nosep${on}"><span class="ic">${svg(ic)}</span><span class="tx">${label}</span>
      <span class="fill"></span>${tick}</div>`;
  });
  return h;
}

const GENERAL = [[S.added, 'Date added'], [S.title, 'Title'], [S.rel, 'Release date']];
const TRAKT = [[S.trend, 'Trending'], [S.pop, 'Popular']];
const FILES = [[S.run, 'Running time'], [S.res, 'Resolution'], [S.hdr, 'HDR'], [S.audio, 'Audio'], [S.size, 'File size'],
  [S.mod, 'Date modified'], [S.codec, 'Codec'], [S.bit, 'Bitrate'], [S.cont, 'Container'], [S.path, 'Path']];
const SERIES = [[S.seasons, 'Seasons'], [S.eps, 'Episodes'], [S.per, 'Size per episode']];

// toolbar with the Order button fixed to the wider of its two words
function toolbar(open, sortWord, orderWord) {
  const words = ['Home', 'Movies', 'TV Shows', 'Documentaries'];
  return `<div class="bar">${words.map(w => `<span class="${w === open ? 'open' : ''}">${w}</span>`).join('')}
    <span class="sp"></span><span>Files</span><span>Search</span><span>Settings</span><span>18:40</span></div>
    <div class="tb"><span>${svg(SLIDERS, 26)}Filters</span><span>${svg(I.sort, 26)}Sort: ${sortWord}</span>
    <span class="fixed" style="width:210px">${svg(I.order, 26)}${orderWord}</span><span>${svg(I.list, 26)}List view</span></div>`;
}

// the page under the toolbar: reuse v11's grid, then replace its bar and toolbar with ours
function grid(items) {
  const html = v11.page(items);
  return html.replace(/^[\s\S]*?<\/div>\s*<div class="tb">[\s\S]*?<\/div>/, '');
}

function menu(groupsHtml, scrollPx, height, arrows) {
  let a = '';
  if (arrows.up !== undefined) {
    a += `<div class="arrow${arrows.up ? '' : ' dim'}" style="right:8px;top:14px">${svg(S.up, 30)}</div>`;
  }
  if (arrows.down !== undefined) {
    a += `<div class="arrow${arrows.down ? '' : ' dim'}" style="right:8px;bottom:14px">${svg(S.down, 30)}</div>`;
  }
  return `<div class="menu sortm"><div class="clip" style="height:${height}px">
    <div style="transform:translateY(-${scrollPx}px)">${groupsHtml}</div></div>${a}</div>`;
}

const DOCS = [
  ['Frozen Frontier', '3 seasons · 18 episodes', 'Added 16 Nov 2026', null],
  ['Deep Blue Planet', 'S1 E3 · 4 of 6 left', 'Added 15 Nov 2026', 0.4],
  ['Inside the Hive', '2023 · 1 h 24 min', 'Added 14 Nov 2026', 'w'],
  ['The Lost Rivers', '4 episodes', 'Added 12 Nov 2026', null],
  ['The Green Valley', '5 episodes', 'Added 10 Nov 2026', null],
  ['Tidelands', 'S1 E2 · 31 of 32 left', 'Added 9 Nov 2026', 0.1],
  ['Mountain Light', '2023 · 1 h 47 min', 'Added 2 Nov 2026', null],
  ['Wild Isles of the North', '5 episodes', 'Added 1 Nov 2026', 'w'],
  ['Rails Across Asia', '3 episodes', 'Added 30 Oct 2026', null],
  ['The Salt Road', '42 min left', 'Added 28 Oct 2026', 0.6],
  ['City of Birds', '2019 · 1 h 28 min', 'Added 27 Oct 2026', null],
  ['Desert Bloom', '2020 · 1 h 22 min', 'Added 25 Oct 2026', null],
  ['The Coral Clock', '2019 · 1 h 41 min', 'Added 20 Oct 2026', 'w'],
  ['Night Shift Ocean', '2022 · 1 h 31 min', 'Added 18 Oct 2026', null],
];

function frame10() {
  const groups = group('General', GENERAL, 'Date added', 'Date added') + group('Trakt', TRAKT)
    + group('Files', FILES);
  return toolbar('Movies', 'Date added', 'Newest first') + grid(MOV) + menu(groups, 0, 860, { up: false, down: true })
    + '<div class="cap2">10 · Sort on Movies, as it opens (more below)</div>';
}

function frame11() {
  const groups = group('General', GENERAL, 'Date added') + group('Trakt', TRAKT) + group('Files', FILES)
    + group('Series', SERIES, null, 'Size per episode');
  // whole list: 4 headings x 50 + 18 rows x 58 + padding; scroll so the foot shows
  return toolbar('Documentaries', 'Date added', 'Newest first') + grid(DOCS)
    + menu(groups, 1268 - 860 + 82, 860, { up: true, down: false })
    + '<div class="cap2">11 · Sort on your own page, scrolled to the foot: Series</div>';
}

render([['44-v14-sort-movies', frame10], ['45-v14-sort-own-page-foot', frame11]], extraCss);
