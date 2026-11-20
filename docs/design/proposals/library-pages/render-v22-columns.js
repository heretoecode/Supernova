// v22 (owner, 20 Nov 2026): Columns. 22 every column switched on (why there is a cap); 23 the Columns menu at the top:
// Shown "7 of 7" (in column order), Hidden A–Z dimmed while full; 24 scrolled to its foot (Reset to default).
// Column names are the menu's names, used as headings too.
// Run: FONT_DIR=<dir with roboto-latin-*-normal.woff2> node render-v22-columns.js
const fs = require('fs');
let src = fs.readFileSync(require.resolve('./render-v19-list.js'), 'utf8');
src = src.replace(/\/\/ v20 columns[\s\S]*?const W = \[[^\]]*\];/, 'let ROWS; let HEAD; let W;');
src = src.replace(/render\(\[[\s\S]*$/, '');
src = src.replace('state(r[7], k === focus)', 'state(r[r.length - 1], k === focus)');
src = src.replace('for (let i = 1; i < 7; i++)', 'for (let i = 1; i < HEAD.length; i++)');
const mod = new Function('require', '__dirname', `${src}
return { list, bar, taper, arrows, lineCss, css, set: (r, h, w) => { ROWS = r; HEAD = h; W = w; } };`)(require,
  __dirname);
const { svg, render } = require('./render-v11-filters.js');

// every column, menu names = headings
const ALL = ['Release date', 'Date added', 'Last episode added', 'Number of seasons', 'Number of episodes',
  'Average running time', 'Total running time', 'Average size', 'Total size', 'Resolution', 'HDR', 'Audio',
  'Video codec', 'Bitrate', 'Container', 'Location'];
const DATA = [
  ['Frozen Frontier', '2021', '16 Nov 2026', '16 Nov 2026', '3', '18', '52 min', '15 h 36 min', '3.1 GB', '55.8 GB',
    '2160p', 'HDR10', 'EAC3 5.1', 'HEVC', '14 Mb/s', 'MKV', 'Media server › Docs › Frozen Frontier', null],
  ['Deep Blue Planet', '2025', '15 Nov 2026', '15 Nov 2026', '1', '6', '49 min', '4 h 54 min', '2.5 GB', '15.0 GB',
    '2160p', 'Dolby Vision', 'Atmos', 'HEVC', '18 Mb/s', 'MKV', 'Media server › Docs › Deep Blue Planet', 0.4],
  ['Inside the Hive', '2023', '14 Nov 2026', '—', '—', '—', '1 h 24 min', '1 h 24 min', '2.4 GB', '2.4 GB', '1080p',
    'SDR', 'AAC 2.0', 'H.264', '4 Mb/s', 'MP4', 'This device › Movies', 'w'],
  ['The Lost Rivers', '2022', '12 Nov 2026', '3 Nov 2026', '1', '4', '58 min', '3 h 52 min', '2.2 GB', '8.8 GB',
    '1080p', 'SDR', 'AC3 5.1', 'H.264', '5 Mb/s', 'MKV', 'Media server › Docs › The Lost Rivers', null],
  ['The Green Valley', '2023', '10 Nov 2026', '10 Nov 2026', '1', '5', '44 min', '3 h 40 min', '1.0 GB', '5.0 GB',
    '1080p', 'SDR', 'AAC 2.0', 'H.264', '3 Mb/s', 'MKV', 'Media server › Docs › The Green Valley', null],
  ['Tidelands: Voices of the Northern Coast', '2024', '9 Nov 2026', '18 Nov 2026', '2', '32', '26 min',
    '13 h 52 min', '1.4 GB', '44.8 GB', '1080p', 'SDR', 'EAC3 5.1', 'HEVC', '7 Mb/s', 'MKV',
    'Media server › Docs › Tidelands', 0.1],
  ['Mountain Light', '2023', '2 Nov 2026', '—', '—', '—', '1 h 47 min', '1 h 47 min', '24.6 GB', '24.6 GB', '2160p',
    'HDR10+', 'TrueHD 7.1', 'HEVC', '31 Mb/s', 'MKV', 'Media server › Movies › Mountain Light', null],
  ['Wild Isles of the North', '2024', '1 Nov 2026', '1 Nov 2026', '1', '5', '51 min', '4 h 15 min', '1.2 GB',
    '6.0 GB', '1080p', 'SDR', 'AAC 2.0', 'H.264', '3 Mb/s', 'MP4', 'This device › TV Shows', 'w'],
  ['Rails Across Asia', '2020', '30 Oct 2026', '30 Oct 2026', '1', '3', '52 min', '2 h 36 min', '0.8 GB', '2.4 GB',
    '720p', 'SDR', 'AAC 2.0', 'H.264', '2 Mb/s', 'MKV', 'Media server › Docs › Rails Across Asia', null],
  ['City of Birds', '2019', '27 Oct 2026', '—', '—', '—', '1 h 28 min', '1 h 28 min', '2.1 GB', '2.1 GB', '1080p',
    'SDR', 'AC3 5.1', 'H.264', '3 Mb/s', 'MP4', 'Media server › Movies › City of Birds', null],
];

function pick(cols) {
  const idx = cols.map(c => ALL.indexOf(c) + 1);
  return DATA.map(d => [d[0], ...idx.map(i => d[i]), d[17]]);
}

const extra = `${mod.css}
.lv .c{font-size:inherit}
.squash .lh{font-size:15px}
.squash .lr{font-size:15px}
.cm{position:absolute;left:880px;top:166px;width:560px;z-index:9}
.cm .clip{overflow:hidden}
.cm .i.one{height:58px}
.cm .i.dim{opacity:.4}
.cm .h{padding:8px 24px 6px}
.cm .i,.cm .h{margin-right:44px}
.cm .arrow{position:absolute;right:8px;color:#fff}
.cm .arrow.dim{opacity:.35}
.lift{display:flex;color:inherit;margin-right:6px;width:26px}
`;

const IC = {
  rel: '<rect x="3" y="5" width="18" height="16" rx="2"/><path d="M3 10h18M8 3v4M16 3v4"/>',
  tray: '<path d="M3 13l3-8h12l3 8v6a1 1 0 0 1-1 1H4a1 1 0 0 1-1-1z"/><path d="M3 13h5l1.5 2.5h5L16 13h5"/>'
    + '<path d="M12 3.5v7M9 8l3 3 3-3"/>',
  newEp: '<rect x="3" y="5" width="15" height="12" rx="2"/><path d="M8.5 21h6M21 3v6M18 6h6"/>'
    + '<path d="M9 8.5l4 2.5-4 2.5z"/>',
  numbered: '<path d="M4 6h1.5v4M4 10h3M4 14.5c0-1 2.8-1 2.8.3 0 1.2-2.8 2-2.8 3.2h3M11 7h9M11 12h9M11 17h9"/>',
  eps: '<rect x="3" y="4" width="18" height="13" rx="2"/><path d="M10 8l4 2.5-4 2.5zM8 21h8"/>',
  run: '<circle cx="12" cy="13" r="8"/><path d="M12 9v4l3 2M10 2h4"/>',
  total: '<circle cx="12" cy="12" r="9"/><path d="M12 7v5l3.5 2M3 12h2M19 12h2"/>',
  size: '<rect x="3" y="6" width="18" height="12" rx="2"/><path d="M7 14h.1M11 14h6"/>',
  stack: '<rect x="3" y="9" width="18" height="11" rx="2"/><path d="M5 6h14M7 3h10M7 15h.1M11 15h6"/>',
  res: '<rect x="3" y="5" width="18" height="14" rx="2"/><path d="M7 9v6M7 12h3M10 9v6M14 9h1.5a3 3 0 0 1 0 6H14z"/>',
  hdr: '<circle cx="12" cy="12" r="4"/><path d="M12 2v2M12 20v2M4.9 4.9l1.4 1.4M17.7 17.7l1.4 1.4M2 12h2M20 12h2'
    + 'M4.9 19.1l1.4-1.4M17.7 6.3l1.4-1.4"/>',
  audio: '<path d="M4 9v6h4l5 4V5L8 9z"/><path d="M16.5 8.5a5 5 0 0 1 0 7M19 6a8.5 8.5 0 0 1 0 12"/>',
  codec: '<rect x="6" y="6" width="12" height="12" rx="1.5"/><path d="M9 2v4M15 2v4M9 18v4M15 18v4M2 9h4M2 15h4M18 9h4'
    + 'M18 15h4"/>',
  bit: '<path d="M4 18a8 8 0 1 1 16 0"/><path d="M12 18l4-6"/>',
  cont: '<path d="M12 3l8 4.5v9L12 21l-8-4.5v-9z"/><path d="M4 7.5l8 4.5 8-4.5M12 12v9"/>',
  loc: '<path d="M3 7a2 2 0 0 1 2-2h4l2 2h8a2 2 0 0 1 2 2v8a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2z"/>',
  reset: '<path d="M4 12a8 8 0 1 0 2.3-5.7"/><path d="M4 4v4h4"/>',
  tick: '<path d="M5 12.5l4.5 4.5L19 7.5"/>',
  updown: '<path d="M8 9l4-4 4 4M8 15l4 4 4-4"/>',
};
const ICON_OF = {
  'Release date': IC.rel, 'Date added': IC.tray, 'Last episode added': IC.newEp, 'Number of seasons': IC.numbered,
  'Number of episodes': IC.eps, 'Average running time': IC.run, 'Total running time': IC.total,
  'Average size': IC.size, 'Total size': IC.stack, Resolution: IC.res, HDR: IC.hdr, Audio: IC.audio,
  'Video codec': IC.codec, Bitrate: IC.bit, Container: IC.cont, Location: IC.loc,
};

const SHOWN = ['Release date', 'Number of episodes', 'Average running time', 'Average size', 'Total size',
  'Resolution', 'Date added'];

function columnsMenu(scroll, focusName) {
  const hidden = ALL.filter(c => !SHOWN.includes(c)).sort();
  const row = (name, shown, dim) => {
    const on = name === focusName ? ' on' : '';
    const right = shown ? `<span class="lift">${name === focusName ? svg(IC.updown, 26) : ''}</span>`
      + `<span class="ch" style="color:inherit">${svg(IC.tick)}</span>` : '<span style="width:28px"></span>';
    return `<div class="i one nosep${on}${dim ? ' dim' : ''}"><span class="ic">${svg(ICON_OF[name])}</span>
      <span class="tx">${name}</span><span class="fill"></span>${right}</div>`;
  };
  let inner = '<div class="h">Shown · 7 of 7</div>' + SHOWN.map(n => row(n, true)).join('')
    + '<div class="h">Hidden</div>' + hidden.map(n => row(n, false, true)).join('')
    + `<div class="rule" style="margin-right:68px"></div><div class="i one nosep${focusName === 'reset' ? ' on' : ''}">
      <span class="ic">${svg(IC.reset)}</span><span class="tx">Reset to default</span></div>`;
  const up = `<div class="arrow${scroll ? '' : ' dim'}" style="top:14px">${svg('<path d="M6 15l6-6 6 6"/>', 30)}</div>`;
  const down = `<div class="arrow${scroll ? ' dim' : ''}" style="bottom:14px">${svg('<path d="M6 9l6 6 6-6"/>', 30)}
    </div>`;
  return `<div class="menu cm"><div class="clip" style="height:836px"><div style="transform:translateY(-${scroll}px)">
    ${inner}</div></div>${up}${down}</div>`;
}

function allColumns() {
  const head = ['Title', ...ALL];
  const widths = [250, ...ALL.map(() => 98)];
  mod.set(pick(ALL), head, widths);
  return mod.lineCss(0.2, true) + mod.bar() + mod.taper() + `<div class="squash">${mod.list(3)}</div>` + mod.arrows()
    + '<div class="cap2">22 · Every column switched on: 16 squeezed in, nothing readable</div>';
}

function withMenu(scroll, focusName, n, label) {
  return () => {
    mod.set(pick(SHOWN), ['Title', ...SHOWN], [470, 160, 220, 250, 170, 150, 140, 170]);
    return mod.lineCss(0.2, true) + mod.bar() + mod.taper() + mod.list(-1) + mod.arrows()
      + columnsMenu(scroll, focusName) + `<div class="cap2">${n} · ${label}</div>`;
  };
}

render([
  ['56-v22-all-columns', allColumns],
  ['57-v22-columns-menu-top', withMenu(0, 'Average size', 23, 'Columns: seven at most; the rest wait, dimmed')],
  ['58-v22-columns-menu-foot', withMenu(290, 'reset', 24, 'Columns, scrolled to the foot')],
], extra);
