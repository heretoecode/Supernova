// v24 (owner, 21 Nov 2026): Show as one Left / Right choice (panel narrower); Delete this page warns beside itself
// while focused and is undone from the Undo notice (no pop-up); the keyboard opens over the panel, straight under
// Page name; Last episode added icon choices.
// v23 (owner, 21 Nov 2026): Customise page (32, 29) redrawn on today's rules, on the own page (six a row behind, live).
// 25 the panel; 26 Page name with the one keyboard in its own panel beside it; 27 Sort by opens the Sort list on top
// (ten sorts now, so it is a list, not chips); 28 Delete page, the delete warning (Keep page focused).
// Changes from 32: no "This Page" heading (a plain line sets Delete page apart, RULES §10); Genres icon shapes; Years
// uses the Year list (Any year · Recent · Your Own Range); Order words follow the sort.
// Run: FONT_DIR=<dir with roboto-latin-*-normal.woff2> node render-v23-customise.js
const fs = require('fs');
const src = fs.readFileSync(require.resolve('./render-v14-sort.js'), 'utf8');
const body = src.replace(/render\(\[\['44[\s\S]*$/, '');
const mod = new Function('require', '__dirname', `${body}
return { S, group, SLIDERS, extraCss };`)(require, __dirname);
const v11 = require('./render-v11-filters.js');
const { svg, I, render } = v11;
const { S, group, SLIDERS, extraCss } = mod;

const IC = {
  name: '<path d="M4 7V5h16v2M12 5v14M9 19h6"/>',
  incl: '<rect x="3" y="5" width="18" height="12" rx="2"/><path d="M8 21h8M12 17v4"/>',
  shapes: '<circle cx="7" cy="7" r="3.5"/><rect x="13.5" y="3.5" width="7" height="7" rx="1.2"/>'
    + '<path d="M7 13.5l4 7H3z"/><path d="M17 13.5l3.5 3.5-3.5 3.5-3.5-3.5z"/>',
  cal: '<rect x="3" y="5" width="18" height="16" rx="2"/><path d="M3 10h18M8 3v4M16 3v4"/>',
  star: '<path d="M12 3l2.7 5.6 6.1.9-4.4 4.3 1 6.1L12 17l-5.4 2.9 1-6.1L3.2 9.5l6.1-.9z"/>',
  lang: '<circle cx="12" cy="12" r="9"/><path d="M3 12h18M12 3a14 14 0 0 1 0 18M12 3a14 14 0 0 0 0 18"/>',
  studio: '<rect x="4" y="7" width="16" height="12" rx="2"/><path d="M9 3l3 4 3-4"/>',
  show: '<circle cx="12" cy="12" r="9"/><path d="M8 12.5l2.8 2.8L16.5 9.5"/>',
  grid: '<rect x="3" y="3" width="7" height="7" rx="1"/><rect x="14" y="3" width="7" height="7" rx="1"/>'
    + '<rect x="3" y="14" width="7" height="7" rx="1"/><rect x="14" y="14" width="7" height="7" rx="1"/>',
  bin: '<path d="M4 7h16M10 11v6M14 11v6M6 7l1 13h10l1-13M9 7V4h6v3"/>',
  edit: '<path d="M4 20h4L19 9l-4-4L4 16z"/><path d="M14 6l4 4"/>',
  back: '<path d="M21 7H8l-5 5 5 5h13zM12 10l4 4M16 10l-4 4"/>',
  mic: '<rect x="9" y="3" width="6" height="11" rx="3"/><path d="M5 11a7 7 0 0 0 14 0M12 18v3"/>',
  shift: '<path d="M12 4l8 8h-4v7H8v-7H4z"/>',
  tray: '<path d="M3 13l3-8h12l3 8v6a1 1 0 0 1-1 1H4a1 1 0 0 1-1-1z"/><path d="M3 13h5l1.5 2.5h5L16 13h5"/>'
    + '<path d="M12 3.5v7M9 8l3 3 3-3"/>',
  az: '<path d="M3 10l3-7 3 7M4 8h4M3 14h6l-6 7h6"/><path d="M17 4v16M13.5 16.5L17 20l3.5-3.5"/>',
  numbered: '<path d="M4 6h1.5v4M4 10h3M4 14.5c0-1 2.8-1 2.8.3 0 1.2-2.8 2-2.8 3.2h3M11 7h9M11 12h9M11 17h9"/>',
  newEp: '<rect x="3" y="5" width="15" height="12" rx="2"/><path d="M8.5 21h6M21 3v6M18 6h6"/>'
    + '<path d="M9 8.5l4 2.5-4 2.5z"/>',
};

const css = `${extraCss}
.panel{position:absolute;right:52px;top:24px;width:800px;padding:14px 0 16px;z-index:8}
.panel .i.one{height:62px}
.panel.back{filter:brightness(.45);transform:scale(.96);transform-origin:top right}
.val{white-space:nowrap}
.warn{font-size:22px;color:#0b0d12;white-space:nowrap}
.kb.over{left:auto;right:68px;top:160px;grid-template-columns:repeat(10,61px);z-index:11;box-shadow:0 0 0 1.5px rgba(255,255,255,.7),0 24px 64px 16px rgba(0,0,0,.9)}
.toast{position:absolute;left:50%;bottom:44px;transform:translateX(-50%);display:flex;align-items:center;gap:22px;
 padding:20px 20px 30px 26px;border-radius:20px;background:rgba(22,24,28,.99);z-index:12;color:#fff;font-size:24px;
 white-space:nowrap;box-shadow:0 0 0 1.5px rgba(255,255,255,.7),0 18px 48px 12px rgba(0,0,0,.85)}
.toast .u{height:52px;padding:0 24px;border-radius:20px;display:flex;align-items:center}
.toast .u.f{background:#f4f6fb;color:#0b0d12}
.toast .bar6{position:absolute;left:24px;right:24px;bottom:10px;height:3px;border-radius:2px;background:rgba(255,255,255,.18)}
.toast .bar6 b{position:absolute;left:0;top:0;bottom:0;width:70%;border-radius:2px;background:rgba(255,255,255,.7)}
.empty{position:absolute;left:52px;right:52px;top:330px;text-align:center;color:#fff}
.empty .t{font-size:40px;font-weight:500;margin-top:24px}
.empty .s{font-size:26px;color:#c9d3e6;margin-top:14px}
.empty .b{display:inline-flex;height:52px;padding:0 26px;border-radius:20px;align-items:center;font-size:24px;margin-top:30px;border:1.5px solid rgba(255,255,255,.7)}
.board{position:absolute;left:72px;top:60px;color:#fff}
.board .cols{display:flex;gap:60px;margin-top:20px}
.board .lbl{font-size:24px;color:#c9d3e6;margin-bottom:8px}
.board .big{height:90px;display:flex;align-items:center;padding-left:14px}
.board .mini{position:relative;border-radius:16px;background:rgba(22,24,28,.99);padding:10px 0;width:470px;box-shadow:0 0 0 1.5px rgba(255,255,255,.7),0 18px 48px 12px rgba(0,0,0,.85)}
.board .mini .i.one{height:58px}
.caret{display:inline-block;width:2px;height:30px;background:#fff;margin-left:4px;vertical-align:middle}
.kb{position:absolute;left:52px;top:166px;padding:20px;z-index:9;display:grid;grid-template-columns:repeat(10,64px);
 gap:12px}
.kb b{height:62px;border-radius:14px;background:rgba(255,255,255,.1);color:#fff;font-size:26px;font-weight:400;
 display:flex;align-items:center;justify-content:center}
.kb b.f{background:#f4f6fb;color:#0b0d12}
.kb b.w2{grid-column:span 2}
.kb b.w3{grid-column:span 3}
.sortlist{position:absolute;left:600px;top:150px;z-index:10}
.sortlist .i.one{height:58px}
.sortlist .h{padding:8px 24px 6px}
.scrim{position:absolute;inset:0;background:rgba(0,0,0,.55);z-index:12}
.dialog{position:absolute;left:50%;top:50%;transform:translate(-50%,-50%);width:820px;border-radius:20px;
 background:rgba(22,24,28,.99);z-index:13;padding:40px 44px 36px;color:#fff;
 box-shadow:0 0 0 1.5px rgba(255,255,255,.7),0 18px 48px 12px rgba(0,0,0,.85)}
.dialog .ti{font-size:34px;font-weight:500}
.dialog .l1{font-size:26px;margin-top:20px}
.dialog .l2{font-size:24px;color:#c9d3e6;margin-top:8px}
.dialog .bt{display:flex;gap:12px;margin-top:34px}
.dialog .bt span{height:52px;padding:0 24px;border-radius:20px;display:flex;align-items:center;gap:10px;font-size:24px}
.dialog .bt .f{background:#f4f6fb;color:#0b0d12}
`;

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
];

function page() {
  const words = ['Home', 'Movies', 'TV Shows', 'Documentaries'];
  const grid = v11.page(DOCS, undefined, 'Filters', 6).replace(/^[\s\S]*?<\/div>\s*<div class="tb">[\s\S]*?<\/div>/, '');
  return `<div class="bar">${words.map(w => `<span class="${w === 'Documentaries' ? 'open' : ''}">${w}</span>`).join('')}
    <span class="sp"></span><span>Files</span><span>Search</span><span>Settings</span><span>18:40</span></div>
    <div class="tb" style="right:52px"><span>${svg(SLIDERS, 26)}Filters</span><span>${svg(I.sort, 26)}Sort: Date added
    </span><span class="fixed" style="width:210px">${svg(I.order, 26)}Newest first</span>
    <span>${svg(I.list, 26)}List view</span><span style="margin-left:auto">${svg(IC.edit, 26)}Customise page</span></div>`
    + grid;
}

function chips(list, sel) {
  return `<span class="chips">${list.map(c => `<b class="${c === sel ? 'sel' : ''}">${c}</b>`).join('')}</span>`;
}

function prow(ic, label, right, focus, last) {
  return `<div class="i one${focus ? ' on' : ''}${last ? ' nosep' : ''}"><span class="ic">${svg(ic)}</span>
    <span class="tx">${label}</span><span class="fill"></span>${right}<i class="sep"></i></div>`;
}

function stepper(v, focused, quiet) {
  const l = focused ? svg(I.chevL, 26) : '';
  const r = focused ? svg(I.chev, 26) : '<span style="width:26px;display:inline-block"></span>';
  return `<span class="step">${l}<span class="val${quiet && !focused ? ' quiet' : ''}">${v}</span>${r}</span>`;
}

function more(v) {
  const quiet = v === 'Any' ? ' quiet' : '';
  return `<span class="val${quiet}">${v}</span><span class="ch">${svg(I.chev, 26)}</span>`;
}

function panel(focus, opts = {}) {
  const f = k => focus === k;
  const nameVal = opts.typing ? `<span class="val">Documentar<i class="caret"></i></span>` : more('Documentaries');
  const w = opts.step3 ? ' style="width:700px"' : '';
  return `<div class="menu panel${opts.back ? ' back' : ''}"${w}>
    <div class="h">What To Show</div>
    ${prow(IC.name, 'Page name', nameVal, f('name'))}
    ${prow(IC.incl, 'Includes', opts.step3 ? stepper('Movies and TV shows', f('incl')) : chips(['Movies', 'TV Shows', 'Both'], 'Both'), f('incl'))}
    ${prow(IC.shapes, 'Genres', more('Documentary, Nature'), f('genres'))}
    ${prow(IC.cal, 'Years', more('From 1990'), f('years'))}
    ${prow(IC.star, 'Minimum rating', opts.step3 ? stepper('Any', f('rating'), true) : chips(['Any', '6+', '7+', '8+', '9+'], 'Any'), f('rating'))}
    ${prow(IC.lang, 'Original language', more('Any'), f('lang'))}
    ${prow(IC.studio, 'Studio / Network', more('Any'), f('studio'), true)}
    <div class="h" style="margin-top:14px">How To Show It</div>
    ${prow(IC.show, 'Show', stepper('All', f('show')), f('show'))}
    ${prow(IC.grid, 'View', chips(['Grid', 'List'], 'Grid'), f('view'))}
    ${prow(I.sort, 'Sort by', more('Date added'), f('sort'))}
    ${prow(I.order, 'Order', chips(['Newest first', 'Oldest first'], 'Newest first'), f('order'), true)}
    <div class="rule" style="margin-top:14px"></div>
    ${prow(IC.bin, 'Delete this page', f('delete') ? '<span class="warn">Your videos stay in your library.</span>'
      : '', f('delete'), true)}
  </div>`;
}

function keyboard() {
  const keys = '1234567890qwertyuiopasdfghjkl/'.split('');
  let h = '<div class="menu kb over">';
  keys.forEach(k => {
    h += `<b class="${k === 'i' ? 'f' : ''}">${k}</b>`;
  });
  h += `<b>${svg(IC.shift, 26)}</b>${'zxcvbnm.@'.split('').map(k => `<b>${k}</b>`).join('')}`;
  h += `<b class="w2">#+=</b><b>${svg(IC.mic, 26)}</b><b class="w2">Space</b><b>${svg(IC.back, 26)}</b>`
    + '<b class="w2">Clear</b><b class="w2">Done</b></div>';
  return h;
}

function sortList() {
  const G = [[IC.tray, 'Date added'], [IC.az, 'Title'], [S.rel, 'Release date']];
  const T = [[S.trend, 'Trending'], [S.pop, 'Popular']];
  const R = [[IC.newEp, 'Last episode added'], [IC.numbered, 'Number of seasons'], [S.eps, 'Number of episodes']];
  const F = [[S.run, 'Running time'], [S.size, 'Size']];
  return `<div class="menu sortlist">${group('General', G, 'Date added', 'Date added')}${group('Trakt', T)}
    ${group('Series', R)}${group('Files', F)}</div>`;
}

function deleteWarning() {
  return `<div class="scrim"></div><div class="dialog"><div class="ti">Delete Documentaries?</div>
    <div class="l1">Removes the page from the top bar.</div>
    <div class="l2">Your movies and TV shows stay in your library.</div>
    <div class="bt"><span class="f">Keep page</span><span>${svg(IC.bin, 24)}Delete page</span></div></div>`;
}

const LE = {
  a: '<rect x="3" y="6" width="18" height="13" rx="2"/><path d="M8 22h8M10 10l4 2.5-4 2.5z"/>'
    + '<circle cx="19.5" cy="5.5" r="3.5" fill="currentColor" stroke="none"/>',
  b: '<rect x="3" y="9" width="18" height="12" rx="2"/><path d="M12 2v8M8.5 6.5L12 10l3.5-3.5"/>',
  c: '<path d="M3 6h12M3 11h12M3 16h7"/><path d="M18 12v8M14 16h8"/>',
};

function iconBoard() {
  const col = (letter, name, d) => `<div><div class="lbl">${letter} · ${name}</div><div class="big">${svg(d, 72)}</div>
    <div class="mini"><div class="i one nosep"><span class="ic">${svg(d)}</span><span class="tx">Last episode added</span>
    <span class="fill"></span></div><div class="i one nosep on"><span class="ic">${svg(d)}</span>
    <span class="tx">Last episode added</span><span class="fill"></span></div></div></div>`;
  return `<div class="board"><div class="sec" style="font-size:30px;font-weight:500">Last episode added</div>
    <div class="cols">${col('A', 'Screen with a "new" dot', LE.a)}${col('B', 'Arriving on the screen', LE.b)}
    ${col('C', 'A new one on the list', LE.c)}</div></div>`;
}

function deleted() {
  const words = ['Home', 'Movies', 'TV Shows', '+'];
  return `<div class="bar">${words.map(w => `<span class="${w === '+' ? 'open' : ''}">${w}</span>`).join('')}
    <span class="sp"></span><span>Files</span><span>Search</span><span>Settings</span><span>18:40</span></div>
    <div class="empty">${svg(IC.edit, 88)}<div class="t">Make this page your own</div>
    <div class="s">More options for your library are in Files and Settings.</div><div class="b">Customise page</div></div>
    <div class="toast">${svg(IC.grid, 30)}<span>Documentaries page deleted</span><span style="display:flex;gap:8px;margin-left:10px">
    <span class="u f">Understood</span><span class="u">Undo</span></span><i class="bar6"><b></b></i></div>`;
}

render([
  ['63-v24-customise-show-step', () => page() + panel('show')
    + '<div class="cap2">29 · Show steps with Left / Right; the panel is narrower</div>'],
  ['64-v24-customise-delete-focused', () => page() + panel('delete')
    + '<div class="cap2">30 · Delete this page warns beside itself while focused</div>'],
  ['65-v24-customise-deleted-undo', () => deleted()
    + '<div class="cap2">31 · OK: the page goes, the Undo notice offers it back</div>'],
  ['66-v24-customise-keyboard-over', () => page() + panel(null, { typing: true }) + keyboard()
    + '<div class="cap2">32 · The keyboard opens over the panel, under Page name</div>'],
  ['68-v24-customise-steppers', () => page() + panel('incl', { step3: true })
    + '<div class="cap2">34 · More than two choices step with Left / Right</div>'],
  ['67-v24-last-episode-icons', () => iconBoard() + '<div class="cap2">33 · Last episode added: icon choices</div>'],
], css);
