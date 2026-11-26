// v13 (owner, 20 Nov 2026): the Year list cut to Any year · Recent · Your Own Range (From / To step with Left / Right,
// stopping only on years that hold titles). Shown with the chosen icons: Filters as sliders with the count when on
// ("Filters · 2"), Genre as shapes.
// Run: FONT_DIR=<dir with roboto-latin-*-normal.woff2> node render-v13-year.js
const { svg, I, page, filtersMenu, heading, row, render, UNW } = require('./render-v11-filters.js');

const SLIDERS = '<path d="M4 6h8M16 6h4M4 12h2M10 12h10M4 18h10M18 18h2"/><circle cx="14" cy="6" r="2"/>'
  + '<circle cx="8" cy="12" r="2"/><circle cx="16" cy="18" r="2"/>';
I.genre = '<circle cx="7" cy="7" r="3.5"/><rect x="13.5" y="3.5" width="7" height="7" rx="1.2"/>'
  + '<path d="M7 13.5l4 7H3z"/><path d="M17 13.5l3.5 3.5-3.5 3.5-3.5-3.5z"/>';
const RECENT = '<path d="M12 3l1.8 5.2L19 10l-5.2 1.8L12 17l-1.8-5.2L5 10l5.2-1.8z"/>';
const ANY = '<rect x="3" y="5" width="18" height="16" rx="2"/><path d="M3 10h18M8 3v4M16 3v4"/>';
const FROM = '<path d="M4 12h12M11 7l5 5-5 5M20 5v14"/>';
const TO = '<path d="M20 12H8M13 7l-5 5 5 5M4 5v14"/>';

function step(icon, label, value, focused, last) {
  const l = focused ? svg(I.chevL, 26) : '';
  const r = focused ? svg(I.chev, 26) : '';
  return `<div class="i one${focused ? ' on' : ''}${last ? ' nosep' : ''}"><span class="ic">${svg(icon)}</span>
    <span class="tx">${label}</span><span class="fill" style="min-width:120px"></span>
    <span class="step">${l}<span>${value}</span>${r}</span><i class="sep"></i></div>`;
}

function frame() {
  const vals = [['Unwatched', false], ['Comedy, Drama', false], ['Any year', true], ['Any', true]];
  const list = `<div class="menu front" style="left:300px;top:196px">
    ${row(`<span class="ic">${svg(ANY)}</span>`, 'Any year', '1994 – 2026 · 96 titles', true)}
    ${row(`<span class="ic">${svg(RECENT)}</span>`, 'Recent', '2022 – 2026 · 41 titles', false, false, true)}
    ${heading('Your Own Range')}
    ${step(FROM, 'From', '1994', true)}${step(TO, 'To', '2026', false, true)}</div>`;
  return page(UNW, svg(SLIDERS, 26), 'Filters · 2') + filtersMenu(vals, 2, 'back') + list
    + '<div class="cap2">9 · Year: Any year · Recent · your own range</div>';
}

render([['43-v13-filters-year', frame]]);
