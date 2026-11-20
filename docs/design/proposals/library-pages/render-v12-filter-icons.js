// v12 (owner, 20 Nov 2026): Filters icons, round two.
// 7  The Watched list with the poster badge as its icons: an empty ring (unwatched), the pie (in progress) and the
//    tick (watched), white discs with black marks as on the posters; All gets a grid of posters.
// 8  Choices: the Filters button at rest and with filters on (A funnel, B sliders, C narrowing lines); the Genre icon
//    (A masks, B shapes, C clapperboard); the Match icon (A overlapping circles, B ticked list, C link).
// Run: FONT_DIR=<dir with roboto-latin-*-normal.woff2> node render-v12-filter-icons.js
const v11 = require('./render-v11-filters.js');
const { svg, I, page, filtersMenu, row, render, NONE } = v11;

const ICON = {
  funnel: '<path d="M3 5h18l-7 8v6l-4 2v-8z"/>',
  funnelOn: '<path d="M3 5h18l-7 8v6l-4 2v-8z" fill="currentColor"/>',
  sliders: '<path d="M4 6h8M16 6h4M4 12h2M10 12h10M4 18h10M18 18h2"/><circle cx="14" cy="6" r="2"/>'
    + '<circle cx="8" cy="12" r="2"/><circle cx="16" cy="18" r="2"/>',
  slidersOn: '<path d="M4 6h8M16 6h4M4 12h2M10 12h10M4 18h10M18 18h2"/><circle cx="14" cy="6" r="2" fill="currentColor"/>'
    + '<circle cx="8" cy="12" r="2" fill="currentColor"/><circle cx="16" cy="18" r="2" fill="currentColor"/>',
  slidersDot: '<path d="M4 6h8M16 6h4M4 12h2M10 12h10M4 18h10M18 18h2"/><circle cx="14" cy="6" r="2"/>'
    + '<circle cx="8" cy="12" r="2"/><circle cx="16" cy="18" r="2"/><circle cx="21" cy="3" r="3" fill="currentColor" stroke="none"/>',
  lines: '<path d="M3 6h18M6 12h12M9 18h6"/>',
  linesOn: '<path d="M3 6h18M6 12h12M9 18h6"/><circle cx="20" cy="18" r="3" fill="currentColor" stroke="none"/>',
  masks: '<path d="M2 3.5h11v6.5a5.5 5.5 0 0 1-11 0z"/><path d="M5 7.5h1.5M8.5 7.5H10M5.2 11.5c1.3 1.3 3.3 1.3 4.6 0"/>'
    + '<path d="M13 9.5h9v6a4.5 4.5 0 0 1-9 0"/><path d="M15.3 13h1.2M18.5 13h1.2M15.6 17.8c.9-.9 2.9-.9 3.8 0"/>',
  shapes: '<circle cx="7" cy="7" r="3.5"/><rect x="13.5" y="3.5" width="7" height="7" rx="1.2"/>'
    + '<path d="M7 13.5l4 7H3z"/><path d="M17 13.5l3.5 3.5-3.5 3.5-3.5-3.5z"/>',
  clapper: '<rect x="3" y="9" width="18" height="12" rx="1.5"/><path d="M3 9V5.5A1.5 1.5 0 0 1 4.5 4h15A1.5 1.5 0 0 1 21 5.5V9"/>'
    + '<path d="M8 4l-3 5M13 4l-3 5M18 4l-3 5"/>',
  venn: '<circle cx="9" cy="12" r="6"/><circle cx="15" cy="12" r="6"/>'
    + '<path d="M12 6.8A6 6 0 0 1 12 17.2A6 6 0 0 1 12 6.8z" fill="currentColor"/>',
  ticks: '<path d="M3.5 7l2 2 3.5-3.5M3.5 16l2 2 3.5-3.5M12 7.5h8.5M12 16.5h8.5"/>',
  link: '<path d="M10 14a4 4 0 0 0 5.7 0l3-3a4 4 0 0 0-5.7-5.7l-1 1"/><path d="M14 10a4 4 0 0 0-5.7 0l-3 3a4 4 0 0 0 5.7 5.7l1-1"/>',
  posters: '<rect x="3" y="3" width="7.5" height="7.5" rx="1.2"/><rect x="13.5" y="3" width="7.5" height="7.5" rx="1.2"/>'
    + '<rect x="3" y="13.5" width="7.5" height="7.5" rx="1.2"/><rect x="13.5" y="13.5" width="7.5" height="7.5" rx="1.2"/>',
};

// Badge-style icons, as on the posters: white disc, black mark. On a focused (white) row the disc keeps a dark rim.
function disc(mark) {
  return `<span class="ic"><svg width="30" height="30" viewBox="0 0 48 48"><circle class="dsc" cx="24" cy="24" r="22"/>
    ${mark}</svg></span>`;
}

const RING = '<circle cx="24" cy="24" r="21" fill="none" stroke="#fff" stroke-width="3.5" class="rng"/>';
const PIE = '<path d="M24 24 L24 5 A19 19 0 0 1 37.4 37.4 Z" fill="#000"/>';
const TICK = '<path d="M14.5 24.5l6.5 6.5 13-14" fill="none" stroke="#000" stroke-width="4.5" stroke-linecap="round" '
  + 'stroke-linejoin="round"/>';

const extraCss = `
.dsc{fill:#fff}
.i.on .dsc{stroke:#0b0d12;stroke-width:3}
.ring .dsc{fill:none}
.i.on .rng{stroke:#0b0d12}
.board{position:absolute;inset:0;padding:40px 72px;color:#fff}
.sec{display:flex;align-items:center;gap:14px;font-size:30px;font-weight:500;margin:0 0 14px}
.cols{display:flex;gap:40px;margin-bottom:64px}
.col{display:flex;flex-direction:column;gap:10px}
.lbl{font-size:24px;color:#c9d3e6}
.btn{height:52px;padding:0 22px;border-radius:20px;display:inline-flex;align-items:center;gap:10px;font-size:24px;
 white-space:nowrap;align-self:flex-start}
.btn.f{background:#f4f6fb;color:#0b0d12}
.pair{display:flex;gap:8px}
.big{display:flex;gap:40px;height:72px;align-items:center;padding-left:14px}
.board .cols{margin-bottom:44px}
.board .mini{width:400px}
.mini{position:relative;border-radius:16px;background:rgba(22,24,28,.99);padding:10px 0;width:430px;
 box-shadow:0 0 0 1.5px rgba(255,255,255,.7),0 18px 48px 12px rgba(0,0,0,.85)}
.mini .i{margin:0 10px}
`;

function frame7() {
  const list = `<div class="menu front" style="left:300px;top:196px">
    ${row(`<span class="ic">${svg(ICON.posters)}</span>`, 'All', '142 titles', true)}
    ${row(disc(RING).replace('<span class="ic">', '<span class="ic ring">'), 'Unwatched', '96 titles')}
    ${row(disc(PIE), 'In progress', '7 titles', false, true)}
    ${row(disc(TICK), 'Watched', '39 titles', false, false, true)}</div>`;
  return page(undefined, svg(ICON.sliders, 26)) + filtersMenu(NONE, 0, 'back') + list
    + '<div class="cap2">7 · Watched: the poster badges as icons</div>';
}

function filterChoice(letter, name, rest, on, onWord = 'Filters') {
  return `<div class="col"><div class="lbl">${letter} · ${name}</div>
    <div class="big">${svg(rest, 64)}${svg(on, 64)}</div>
    <div class="pair"><span class="btn">${svg(rest, 26)}Filters</span><span class="btn">${svg(on, 26)}${onWord}</span></div>
    <div class="pair"><span class="btn f">${svg(rest, 26)}Filters</span><span class="btn f">${svg(on, 26)}${onWord}</span>
    </div></div>`;
}

function rowChoice(letter, name, d, label, right, focused) {
  return `<div class="col"><div class="lbl">${letter} · ${name}</div><div class="big">${svg(d, 64)}</div><div class="mini">
    <div class="i one nosep${focused ? ' on' : ''}"><span class="ic">${svg(d)}</span><span class="tx">${label}</span>
    <span class="fill"></span>${right}</div></div></div>`;
}

function frame8() {
  const chev = `<span class="val quiet">Any</span><span class="ch">${svg(I.chev, 26)}</span>`;
  const chips = '<span class="chips"><b class="sel">Any</b><b>All</b></span>';
  const board = `<div class="board">
    <div class="sec">${svg(ICON.sliders, 32)}Filters button: off, then on; at rest, then focused</div>
    <div class="cols">${filterChoice('A', 'Funnel, filled when on', ICON.funnel, ICON.funnelOn)}
      ${filterChoice('B', 'Sliders, knobs filled when on', ICON.sliders, ICON.slidersOn)}
      ${filterChoice('C', 'Sliders, a dot when on', ICON.sliders, ICON.slidersDot)}
      ${filterChoice('D', 'Sliders, a count when on', ICON.sliders, ICON.sliders, 'Filters · 4')}</div>
    <div class="sec">${svg(ICON.shapes, 32)}Genre row in the Filters menu</div>
    <div class="cols">${rowChoice('A', 'Masks', ICON.masks, 'Genre', chev)}
      ${rowChoice('B', 'Shapes', ICON.shapes, 'Genre', chev)}
      ${rowChoice('C', 'Clapperboard', ICON.clapper, 'Genre', chev)}</div>
    <div class="sec">${svg(ICON.venn, 32)}Match row at the top of Genre</div>
    <div class="cols">${rowChoice('A', 'Overlapping circles', ICON.venn, 'Match', chips)}
      ${rowChoice('B', 'Ticked list', ICON.ticks, 'Match', chips)}
      ${rowChoice('C', 'Link', ICON.link, 'Match', chips, true)}</div>
  </div>`;
  return board + '<div class="cap2">8 · Icon choices: Filters button, Genre, Match</div>';
}

render([
  ['41-v12-filters-watched-badges', frame7],
  ['42-v12-filter-icon-choices', frame8],
], extraCss);
