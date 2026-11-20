// v19 (owner, 20 Nov 2026): list view (26, 28) redrawn on today's rules: own page, the sheet's default columns
// (Release date, Episodes, Running time, Resolution, File size, Size per episode, Date added), Title first with a small
// poster, faint lines between rows, the focused row the white box, column headings 24 px under the toolbar line.
// Toolbar: Grid view, then Columns beside it.
// Run: FONT_DIR=<dir with roboto-latin-*-normal.woff2> node render-v19-list.js
const fs = require('fs');
const src = fs.readFileSync(require.resolve('./render-v14-sort.js'), 'utf8');
const body = src.replace(/render\(\[\['44[\s\S]*$/, '');
const mod = new Function('require', '__dirname', `${body}
return { SLIDERS, extraCss };`)(require, __dirname);
const { svg, I, render } = require('./render-v11-filters.js');
const { SLIDERS, extraCss } = mod;

const GRID = '<rect x="3" y="3" width="7" height="7" rx="1"/><rect x="14" y="3" width="7" height="7" rx="1"/>'
  + '<rect x="3" y="14" width="7" height="7" rx="1"/><rect x="14" y="14" width="7" height="7" rx="1"/>';
const COLS = '<rect x="3" y="4" width="18" height="16" rx="2"/><path d="M9 4v16M15 4v16"/>';
const ART = [['#7a3b2e', '#26120d'], ['#2f5c4a', '#0e1d17'], ['#5a4a7a', '#1a1426'], ['#8a6a2a', '#2a1f0a'],
  ['#2e4f78', '#0d1726'], ['#6d2f52', '#1f0c18'], ['#3f6b72', '#0f1e21']];

// title, release, episodes, running time, resolution, file size, size per episode, added, state
// v20 columns (owner, 20 Nov): title, release, episodes, average running time, average size, total size, added, state
const ROWS = [
  ['Frozen Frontier', '2021', '18', '52 min', '3.1 GB', '55.8 GB', '16 Nov 2026', null],
  ['Deep Blue Planet', '2025', '6', '49 min', '2.5 GB', '15.0 GB', '15 Nov 2026', 0.4],
  ['Inside the Hive', '2023', '—', '1 h 24 min', '2.4 GB', '2.4 GB', '14 Nov 2026', 'w'],
  ['The Lost Rivers', '2022', '4', '58 min', '2.2 GB', '8.8 GB', '12 Nov 2026', null],
  ['The Green Valley', '2023', '5', '44 min', '1.0 GB', '5.0 GB', '10 Nov 2026', null],
  ['Tidelands: Voices of the Northern Coast', '2024', '32', '26 min', '1.4 GB', '44.8 GB', '9 Nov 2026', 0.1],
  ['Mountain Light', '2023', '—', '1 h 47 min', '24.6 GB', '24.6 GB', '2 Nov 2026', null],
  ['Wild Isles of the North', '2024', '5', '51 min', '1.2 GB', '6.0 GB', '1 Nov 2026', 'w'],
  ['Rails Across Asia', '2020', '3', '52 min', '0.8 GB', '2.4 GB', '30 Oct 2026', null],
  ['City of Birds', '2019', '—', '1 h 28 min', '2.1 GB', '2.1 GB', '27 Oct 2026', null],
];
const HEAD = ['Title', 'Release date', 'Episodes', 'Average running time', 'Average size', 'Total size', 'Date added'];
const W = [560, 180, 140, 270, 190, 180, 180];

const css = `${extraCss}
.lv{position:absolute;left:52px;top:203px;width:1816px;color:#fff}
.lh,.lr{display:flex;align-items:center}
.lh{height:36px;color:#c9d3e6;font-size:22px;padding:0 16px}
.lr{height:76px;font-size:24px;padding:0 16px;position:relative;border-radius:12px}
.lr .ln{position:absolute;left:16px;right:16px;bottom:0;height:1px;background:rgba(255,255,255,.08)}
.lr.on{background:#f4f6fb;color:#0b0d12}
.lr.on .ln,.lr.last .ln{display:none}
.c{white-space:nowrap;overflow:hidden;text-overflow:ellipsis;padding-right:20px}
.c.t{display:flex;align-items:center;gap:18px;font-weight:500}
.thumb{width:40px;height:60px;border-radius:5px;flex:none;box-shadow:inset 0 0 0 1px #fff;position:relative}
.tt{overflow:hidden;text-overflow:ellipsis;flex:1;white-space:nowrap}
.mini{width:30px;height:30px;flex:none}
`;

function pie(p) {
  const a = 2 * Math.PI * p - Math.PI / 2;
  const x = 24 + 21 * Math.cos(a);
  const y = 24 + 21 * Math.sin(a);
  return `<path d="M24 24 L24 3 A21 21 0 ${p > 0.5 ? 1 : 0} 1 ${x} ${y} Z" fill="#000"/>`;
}

function state(st, focused) {
  if (st == null) {
    return '<span class="mini"></span>';
  }
  const rim = focused ? ' stroke="#0b0d12" stroke-width="3"' : '';
  const mark = st === 'w' ? '<path d="M14.5 24.5l6.5 6.5 13-14" fill="none" stroke="#000" stroke-width="4.5" '
    + 'stroke-linecap="round" stroke-linejoin="round"/>' : pie(st);
  return `<svg class="mini" viewBox="0 0 48 48"><circle cx="24" cy="24" r="22" fill="#fff"${rim}/>${mark}</svg>`;
}

function list(focus) {
  let h = '<div class="lv"><div class="lh">';
  HEAD.forEach((t, i) => {
    h += `<span class="c" style="width:${W[i]}px${i === 0 ? ';padding-left:58px' : ''}">${t}</span>`;
  });
  h += '</div><div style="height:6px"></div>';
  ROWS.forEach((r, k) => {
    const a = ART[k % 7];
    const cls = `lr${k === focus ? ' on' : ''}${k === ROWS.length - 1 ? ' last' : ''}`;
    h += `<div class="${cls}"><span class="c t" style="width:${W[0]}px"><span class="thumb" style="background:
      linear-gradient(155deg,${a[0]},${a[1]})"></span><span class="tt">${r[0]}</span>${state(r[7], k === focus)}</span>`;
    for (let i = 1; i < 7; i++) {
      h += `<span class="c" style="width:${W[i]}px">${r[i]}</span>`;
    }
    h += '<i class="ln"></i></div>';
  });
  return h + '</div>';
}

function bar() {
  const words = ['Home', 'Movies', 'TV Shows', 'Documentaries'];
  return `<div class="bar">${words.map(w => `<span class="${w === 'Documentaries' ? 'open' : ''}">${w}</span>`).join('')}
    <span class="sp"></span><span>Files</span><span>Search</span><span>Settings</span><span>18:40</span></div>
    <div class="tb"><span>${svg(SLIDERS, 26)}Filters</span><span>${svg(I.sort, 26)}Sort: Date added</span>
    <span class="fixed" style="width:210px">${svg(I.order, 26)}Newest first</span>
    <span>${svg(GRID, 26)}Grid view</span><span>${svg(COLS, 26)}Columns</span></div>`;
}

function taper() {
  const n = 120;
  const top = [];
  const bot = [];
  for (let i = 0; i <= n; i++) {
    const t = i / n;
    const x = 52 + 1816 * t;
    const e = Math.min(t, 1 - t);
    const k = e >= 0.22 ? 1 : 0.18 + 0.82 * Math.sin(Math.PI / 2 * e / 0.22);
    top.push([x, 179 - k]);
    bot.unshift([x, 179 + k]);
  }
  return `<svg style="position:absolute;left:0;top:0" width="1920" height="1080"><polygon points="${
    top.concat(bot).map(p => p.join(',')).join(' ')}" fill="rgba(255,255,255,.35)"/></svg>`;
}

const UP = '<path d="M6 15l6-6 6 6"/>';
const DOWN = '<path d="M6 9l6 6 6-6"/>';

// scroll arrows in the right-hand margin (RULES §16): up faint at the top (nothing above), down at the foot
function arrows() {
  return `<div style="position:absolute;left:1873px;top:246px;color:#fff;opacity:.35">${svg(UP, 34)}</div>
    <div style="position:absolute;left:1873px;top:968px;color:#fff">${svg(DOWN, 34)}</div>`;
}

function lineCss(alpha, tapered) {
  const c = `rgba(255,255,255,${alpha})`;
  const bg = tapered ? `linear-gradient(90deg,rgba(255,255,255,0),${c} 18%,${c} 82%,rgba(255,255,255,0))` : c;
  return `<style>.lr .ln{background:${bg}}</style>`;
}

function frame(n, alpha, tapered, label) {
  return () => lineCss(alpha, tapered) + bar() + taper() + list(3) + arrows()
    + `<div class="cap2">${n} · Row lines ${label}</div>`;
}

render([
  ['52-v20-list-lines-14', frame(18, 0.14, false, '14%, square ends')],
  ['53-v20-list-lines-14-taper', frame(19, 0.14, true, '14%, fading at both ends')],
  ['54-v20-list-lines-20-taper', frame(20, 0.20, true, '20%, fading at both ends')],
], css);
