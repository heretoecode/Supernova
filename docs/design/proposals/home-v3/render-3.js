// Home v3, round 3 (owner, 21 Nov 2026). 9 the hero's More menu trimmed (no library management on Home), with "Not on
// the hero", and the current dot's pill filling over the 8 s until the hero changes. 10 icon choices for Fix match and
// Remove from library (Details and long-press menus). 11 Customise Home with a Hero group (titles kept off the hero).
// 12 a row's settings on today's rules (steppers for 3+ choices, Sort by list, Delete row inline). 13 the icon picker.
// Run: FONT_DIR=<dir with roboto-latin-*-normal.woff2> node render-3.js
const fs = require('fs');
const path = require('path');
let src = fs.readFileSync(path.join(__dirname, 'render-2.js'), 'utf8');
src = src.slice(0, src.lastIndexOf('const FRAMES = ['));
src = src.replace("const { chromium } = require('playwright');", '');
const m = new Function('require', '__dirname', `${src}
return { extra, svg, I, J, bar, hero, STARTED, row, CONT, item, homeBehind, speckle, SHOWN, HIDDEN };`)(require,
  __dirname);
const { chromium } = require('playwright');
const { extra, svg, I, J, bar, hero, STARTED, row, CONT, item, homeBehind, speckle, SHOWN, HIDDEN } = m;

const K = {
  nohero: '<rect x="3" y="5" width="18" height="12" rx="2"/><path d="M3 3l18 18"/>',
  fixA: '<path d="M4 7h11M4 12h7M4 17h5"/><path d="M14 17l2.5 2.5L21 14"/>',
  fixB: '<path d="M4 20l4-1 10-10-3-3L5 16z"/><path d="M14 5l3 3"/><path d="M19 15l.8 2 2 .8-2 .8-.8 2-.8-2-2-.8 2-.8z"/>',
  fixC: '<circle cx="9" cy="12" r="5"/><circle cx="15" cy="12" r="5" stroke-dasharray="2 2.2"/>',
  remA: '<path d="M5 8h14M6 12h12M8 16h8"/><path d="M15.5 3.5l5 5M20.5 3.5l-5 5"/>',
  remB: '<rect x="3" y="7" width="13" height="13" rx="2"/><path d="M6 4h12a2 2 0 0 1 2 2v12M7 13.5h5"/>',
  remC: '<path d="M4 6h16l-1.5 13a2 2 0 0 1-2 1.8h-9A2 2 0 0 1 5.5 19z"/><path d="M9 12h6"/>',
  name: '<path d="M4 7V5h16v2M12 5v14M9 19h6"/>',
  icon: '<circle cx="7" cy="7" r="3.5"/><rect x="13.5" y="3.5" width="7" height="7" rx="1.2"/>'
    + '<path d="M7 13.5l4 7H3z"/><path d="M17 13.5l3.5 3.5-3.5 3.5-3.5-3.5z"/>',
  incl: '<rect x="3" y="5" width="18" height="12" rx="2"/><path d="M8 21h8M12 17v4"/>',
  cal: '<rect x="3" y="5" width="18" height="16" rx="2"/><path d="M3 10h18M8 3v4M16 3v4"/>',
  star: '<path d="M12 3l2.7 5.6 6.1.9-4.4 4.3 1 6.1L12 17l-5.4 2.9 1-6.1L3.2 9.5l6.1-.9z"/>',
  sort: '<path d="M7 4v16M3 16l4 4 4-4M17 20V4M13 8l4-4 4 4"/>',
  order: '<path d="M7 4v16M3 16l4 4 4-4M14 6h7M14 12h5M14 18h3"/>',
  max: '<path d="M4 6h16M4 12h16M4 18h10"/>',
  auto: '<path d="M4 12a8 8 0 0 1 14-5.3L20 9M20 4v5h-5M20 12a8 8 0 0 1-14 5.3L4 15M4 20v-5h5"/>',
  chev: '<path d="M9 6l6 6-6 6"/>',
  chevL: '<path d="M15 6l-6 6 6 6"/>',
  tick: '<path d="M5 12.5l4.5 4.5L19 7.5"/>',
  hero: '<rect x="3" y="4" width="18" height="12" rx="2"/><path d="M8 20h8"/>',
};
const GEN = [['Automatic', '<path d="M12 3l1.8 5.2L19 10l-5.2 1.8L12 17l-1.8-5.2L5 10l5.2-1.8z"/>'],
  ['Action', '<path d="M13 3L5 14h6l-1 7 8-11h-6z"/>'],
  ['Adventure', '<circle cx="12" cy="12" r="9"/><path d="M15.5 8.5l-2 5-5 2 2-5z"/>'],
  ['Animation', '<path d="M12 3l1.8 5.2L19 10l-5.2 1.8L12 17l-1.8-5.2L5 10l5.2-1.8z"/><path d="M19 16v4M17 18h4"/>'],
  ['Comedy', '<circle cx="12" cy="12" r="9"/><path d="M8 14a4.5 4.5 0 0 0 8 0M9 9.5h.1M15 9.5h.1"/>'],
  ['Crime', '<circle cx="10.5" cy="10.5" r="6"/><path d="M15 15l6 6"/>'],
  ['Documentary', '<rect x="3" y="7" width="13" height="10" rx="2"/><path d="M16 11l5-3v8l-5-3"/>'],
  ['Drama', '<path d="M4 4h10v6a5 5 0 0 1-10 0z"/><path d="M14 9h6v6a5 5 0 0 1-8.6 3.5"/>'],
  ['Family', '<path d="M3 11l9-7 9 7"/><path d="M5 10v10h14V10"/><path d="M10 20v-5h4v5"/>'],
  ['Fantasy', '<path d="M5 19L17 7M15 3l1 2 2 1-2 1-1 2-1-2-2-1 2-1z"/>'],
  ['History', '<path d="M3 21h18M5 21V10M9 21V10M15 21V10M19 21V10M3 10l9-6 9 6z"/>'],
  ['Horror', '<path d="M6 20V10a6 6 0 0 1 12 0v10l-2-1.5-2 1.5-2-1.5-2 1.5-2-1.5z"/><path d="M10 10h.1M14 10h.1"/>'],
  ['Music', '<path d="M9 18V5l11-2v13"/><circle cx="6" cy="18" r="3"/><circle cx="17" cy="16" r="3"/>'],
  ['Mystery', '<circle cx="12" cy="12" r="9"/><path d="M9.5 9a2.5 2.5 0 1 1 3.5 2.3c-.6.3-1 .8-1 1.5V14M12 17v.1"/>'],
  ['Nature', '<path d="M5 19c0-8 6-14 15-14 0 9-6 15-14 15"/><path d="M5 19l8-8"/>'],
  ['Romance', '<path d="M12 20s-7-4.5-7-10a4 4 0 0 1 7-2.6A4 4 0 0 1 19 10c0 5.5-7 10-7 10z"/>'],
  ['Science fiction', '<ellipse cx="12" cy="12" rx="10" ry="4"/><circle cx="12" cy="12" r="2.5"/>'],
  ['Thriller', '<circle cx="12" cy="13" r="8"/><path d="M12 9v4l3 2M10 2h4"/>'],
  ['War', '<path d="M12 3l8 3v6c0 5-4 8-8 9-4-1-8-4-8-9V6z"/>'],
  ['Western', '<path d="M3 15c3 0 3-3 9-3s6 3 9 3M7 12l1-6h8l1 6"/>']];

const css = `${extra}
.dots .on{position:relative;overflow:hidden;width:56px!important;background:rgba(255,255,255,.35)!important}
.dots .on b{position:absolute;left:0;top:0;bottom:0;width:60%;background:#fff;border-radius:5px}
.board{position:absolute;left:72px;top:56px}
.board .sec{font-size:30px;font-weight:500;margin-bottom:14px}
.board .cols{display:flex;gap:56px;margin-bottom:56px}
.board .lbl{font-size:24px;color:#c9d3e6;margin-bottom:8px}
.board .big{height:84px;display:flex;align-items:center;padding-left:14px}
.board .mini{position:relative;border-radius:16px;background:rgba(22,24,28,.99);padding:10px 0;width:470px;
 box-shadow:0 0 0 1.5px rgba(255,255,255,.7),0 18px 48px 12px rgba(0,0,0,.85)}
.step{display:flex;align-items:center;gap:12px;font-weight:500}
.val{font-weight:500}
.val.q{font-weight:400;color:#c9d3e6}
.i.on .val.q{color:#3a3f4a}
.ch{display:flex;color:#c9d3e6}
.i.on .ch{color:inherit}
.chips{display:flex;gap:6px}
.chips b{height:46px;padding:0 20px;border-radius:20px;display:flex;align-items:center;font-weight:400}
.chips b.sel{background:rgba(255,255,255,.2)}
.sw{width:64px;height:34px;border-radius:17px;background:#f4f6fb;position:relative}
.sw:after{content:"";position:absolute;right:4px;top:4px;width:26px;height:26px;border-radius:13px;background:#0b0d12}
.back2{filter:brightness(.45);transform:scale(.96);transform-origin:top right}
.picker{position:absolute;left:560px;top:180px;width:960px;padding:24px;z-index:12;display:grid;
 grid-template-columns:repeat(5,1fr);gap:8px}
.tile{height:132px;border-radius:16px;display:flex;flex-direction:column;align-items:center;justify-content:center;
 gap:12px;font-size:22px;position:relative}
.tile.on{background:#f4f6fb;color:#0b0d12}
.tile .tk{position:absolute;right:12px;top:10px}
`;

function frame9() {
  const menu = `<div class="menu" style="left:300px;top:380px">
    ${item(J.details, 'Details', { on: true, nosep: true })}
    <div class="h">Playback</div>
    ${item(J.resume, 'Resume · 42 min left', { nosep: true })}${item(J.restart, 'Play from the start', { nosep: true })}
    <div class="h">This Title</div>
    ${item(J.watched, 'Mark as watched', { nosep: true })}${item(K.nohero, 'Not on the hero', { nosep: true })}</div>`;
  return bar('Home') + hero(STARTED, `<span class="btn">${svg(I.more, 26)}More</span>`)
    + '<div class="dots"><i class="on"><b></b></i><i></i><i></i><i></i><i></i></div>'
    + row(72, 976, { icon: I.cont, title: 'Continue Watching' }, CONT, -1) + menu
    + '<div class="cap2">9 · More on Home, trimmed; the pill fills until the next hero</div>';
}

function frame10() {
  const col = (letter, name, d, label) => `<div><div class="lbl">${letter} · ${name}</div><div class="big">${svg(d, 72)}
    </div><div class="mini">${item(d, label, { nosep: true })}${item(d, label, { on: true, nosep: true })}</div></div>`;
  return `<div class="board"><div class="sec">Fix match</div><div class="cols">
    ${col('A', 'Checking a list', K.fixA, 'Fix match')}${col('B', 'A pencil putting it right', K.fixB, 'Fix match')}
    ${col('C', 'A match not lining up', K.fixC, 'Fix match')}</div>
    <div class="sec">Remove from library</div><div class="cols">
    ${col('A', 'Crossed off the list', K.remA, 'Remove from library')}
    ${col('B', 'Taken off the pile', K.remB, 'Remove from library')}
    ${col('C', 'Into the bin, not deleted', K.remC, 'Remove from library')}</div></div>`
    + '<div class="cap2">10 · Icon choices: Fix match, Remove from library</div>';
}

function frame11() {
  let h = '<div class="menu panel"><div class="h">Shown on Home</div>';
  SHOWN.forEach(([ic, name, own], k) => {
    h += item(ic, name, { right: own ? '<small>Your row</small>' : '', nosep: k === SHOWN.length - 1 });
  });
  h += '<div class="h">Hidden</div>';
  HIDDEN.forEach(([ic, name], k) => {
    h += item(ic, name, { nosep: k === HIDDEN.length - 1 });
  });
  h += `<div class="rule"></div>${item(J.plus, 'Add a row', { nosep: true })}
    ${item(K.hero, 'Kept off the hero', { on: true, nosep: true, right: `<span class="val">2</span><span class="ch">
    ${svg(K.chev, 26)}</span>` })}</div>`;
  return bar('Home') + homeBehind(['Continue Watching', 'Recently Added', 'Nature Nights'])
    + '<div class="pagedim"></div>' + h
    + '<div class="cap2">11 · Customise Home: titles kept off the hero can come back</div>';
}

function stepper(v, f, q) {
  const l = f ? svg(K.chevL, 26) : '';
  const r = f ? svg(K.chev, 26) : '<span style="width:26px"></span>';
  return `<span class="step">${l}<span class="val${q && !f ? ' q' : ''}">${v}</span>${r}</span>`;
}

function more(v, q) {
  return `<span class="val${q ? ' q' : ''}">${v}</span><span class="ch">${svg(K.chev, 26)}</span>`;
}

function rowSettings(focus, back) {
  const f = k => focus === k;
  return `<div class="menu panel${back ? ' back2' : ''}" style="width:700px;top:24px;z-index:11">
    <div class="h">What To Show</div>
    ${item(K.name, 'Row name', { right: more('Nature Nights'), on: f('name') })}
    ${item(K.icon, 'Icon', { right: more('Automatic', true), on: f('icon') })}
    ${item(K.incl, 'Includes', { right: stepper('Movies and TV shows', f('incl')), on: f('incl') })}
    ${item(GEN[14][1], 'Genres', { right: more('Documentary, Nature'), on: f('genres') })}
    ${item(K.cal, 'Years', { right: more('Any year', true), on: f('years') })}
    ${item(K.star, 'Minimum rating', { right: stepper('Any', f('rating'), true), on: f('rating'), nosep: true })}
    <div class="h">How To Show It</div>
    ${item(K.sort, 'Sort by', { right: more('Date added'), on: f('sort') })}
    ${item(K.order, 'Order', { right: '<span class="chips"><b class="sel">Newest first</b><b>Oldest first</b></span>',
      on: f('order') })}
    ${item(K.max, 'Maximum items', { right: stepper('20', f('max')), on: f('max') })}
    ${item(K.auto, 'Add new titles automatically', { right: '<span class="sw"></span>', on: f('auto'), nosep: true })}
    <div class="rule"></div>${item(J.bin, 'Delete this row', { on: f('del'), nosep: true })}</div>`;
}

function frame12() {
  return bar('Home') + homeBehind(['Continue Watching', 'Recently Added', 'Nature Nights'])
    + '<div class="pagedim"></div>' + rowSettings('max', false)
    + '<div class="cap2">12 · Edit a row (or Add a row): today\'s rules</div>';
}

function frame13() {
  let tiles = '';
  GEN.forEach(([n, d]) => {
    const on = n === 'Nature';
    tiles += `<div class="tile${on ? ' on' : ''}">${svg(d, 40)}<span>${n}</span>${on ? `<span class="tk">${svg(K.tick, 24)}
      </span>` : ''}</div>`;
  });
  return bar('Home') + homeBehind(['Continue Watching', 'Recently Added', 'Nature Nights'])
    + '<div class="pagedim"></div>' + rowSettings(null, true) + `<div class="menu picker">${tiles}</div>`
    + '<div class="cap2">13 · Icon: the picker on top; OK chooses and returns</div>';
}

const FRAMES = [['09-home-more-trimmed', frame9], ['10-fix-remove-icons', frame10],
  ['11-customise-home-hero', frame11], ['12-row-settings', frame12], ['13-icon-picker', frame13]];

(async () => {
  const browser = await chromium.launch();
  const pg = await browser.newPage({ viewport: { width: 1920, height: 1080 }, deviceScaleFactor: 1 });
  for (const [name, fn] of FRAMES) {
    const html = `<!doctype html><html><head><meta charset="utf-8"><style>${css}</style></head><body>
      <div class="screen">${fn()}<div class="border"></div></div>${speckle()}</body></html>`;
    await pg.setContent(html);
    await pg.evaluate(() => document.fonts.ready);
    await pg.screenshot({ path: path.join(__dirname, 'tv', `${name}.png`) });
  }
  await browser.close();
})();
