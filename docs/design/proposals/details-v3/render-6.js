// Details v3, round 6 (owner, 21 Nov 2026): two ways to reach a season page, and the episode delete with a choice.
// 26 A: the season card redrawn: the season's own poster (TMDB's 2:3 season poster) whole on the left, "Season 1",
//   "10 episodes · 2 watched", "Open season ›" on the right; same 16:9 size as the episodes.
// 27 B: a season picker under the tabs (‹ Season 1 › steps with Left / Right, the stepper rule) with "Season page"
//   beside it; the episodes of that season in the row(s) below; no season cards.
// 28 the episode delete: Earlier episodes steps None · Watched only (6) · All earlier (10).
// Run: FONT_DIR=<dir with roboto-latin-*-normal.woff2> node render-6.js
const fs = require('fs');
const path = require('path');
let src = fs.readFileSync(path.join(__dirname, 'render-4.js'), 'utf8');
src = src.slice(0, src.lastIndexOf("const FRAMES = [['"));
src = src.replace("const { chromium } = require('playwright');", '');
const m = new Function('require', '__dirname', `${src}
return { css2, svg, I, D, J, K, P, bar, hero, speckle, tabs, inner, BTNS, item };`)(require, __dirname);
const { chromium } = require('playwright');
const { css2, svg, I, D, J, K, P, bar, hero, speckle, tabs, inner, BTNS, item } = m;

const css = `${css2}
.scard{position:absolute;width:400px;height:225px;border-radius:14px;overflow:hidden;background:#141619;display:flex}
.scard.focus{width:432px;height:243px;box-shadow:0 0 0 4px #fff;z-index:3}
.scard .spost{width:150px;flex:none;background:radial-gradient(ellipse 80% 60% at 50% 30%,rgba(170,200,220,.55),transparent 70%),
 linear-gradient(170deg,#2c4656,#0e1820);display:flex;align-items:center;justify-content:center;font-weight:900;
 font-size:20px;letter-spacing:1px;color:rgba(244,246,251,.9);text-align:center;line-height:1.1}
.scard.focus .spost{width:162px}
.scard .tx{flex:1;padding:22px 20px;display:flex;flex-direction:column;justify-content:center;gap:8px}
.scard .tx b{font-size:28px;font-weight:500}
.scard .tx small{font-size:20px;color:#c9d3e6}
.scard .tx em{font-style:normal;font-size:20px;margin-top:10px;display:flex;align-items:center;gap:6px}
.scard .e{position:absolute;inset:0;border-radius:14px;box-shadow:inset 0 0 0 1px #fff}
.scard.focus .e{display:none}
.picker{position:absolute;left:52px;top:262px;display:flex;gap:10px;align-items:center;font-size:28px;z-index:3}
.picker span{height:52px;padding:0 22px;border-radius:20px;display:flex;align-items:center;gap:12px}
.picker .f{background:#f4f6fb;color:#0b0d12;font-weight:500}
.picker small{font-size:22px;color:#c9d3e6;margin-left:8px}
`;

const S1 = [['Arrival', 'S1 E1 · 52 min', 'w'], ['Low Water', 'S1 E2 · 49 min', 'w'],
  ['The Long Tide', 'S1 E3 · 31 min left', 0.4], ['Harbour Lights', 'S1 E4 · 51 min', null], ['Salt', 'S1 E5 · 50 min', null]];
const S1b = [['Undertow', 'S1 E6 · 47 min', null], ['Fog Bell', 'S1 E7 · 53 min', null], ['Neap Tide', 'S1 E8 · 50 min', null],
  ['Ebb', 'S1 E9 · 49 min', null], ['Spring Tide', 'S1 E10 · 58 min', null]];
const S2 = [['Return', 'S2 E1 · 55 min', null], ['Driftwood', 'S2 E2 · 48 min', null], ['Fog', 'S2 E3 · 52 min', null],
  ['Wreck', 'S2 E4 · 50 min', null]];
const SEASON = '<path d="M4 6h1.5v4M4 10h3M4 14.5c0-1 2.8-1 2.8.3 0 1.2-2.8 2-2.8 3.2h3M11 7h9M11 12h9M11 17h9"/>';
const SHOW = { logo: 'Tidelands', age: '12', facts: 'x', desc: 'x' };

function seasonCard(x, y, n, eps, line, focus) {
  return `<div class="scard${focus ? ' focus' : ''}" style="left:${x - (focus ? 16 : 0)}px;top:${y - (focus ? 9 : 0)}px">
    <div class="spost">TIDELANDS<br>SEASON ${n}</div><div class="tx"><b>Season ${n}</b><small>${eps} episodes · ${line}</small>
    <em>Open season ${svg('<path d="M9 6l6 6-6 6"/>', 22)}</em></div><div class="e"></div></div>`;
}

function frame26() {
  let h = bar('TV Shows') + hero(SHOW, BTNS, -680) + '<div class="barblur"></div>' + tabs(170, 'Episodes');
  h += inner.row(492, 262, { icon: SEASON, title: 'Season 1' }, S1.slice(0, 4), -1, { shift: 1 })
    .replace('style="left:492px;top:262px"', 'style="left:52px;top:262px"');
  h += seasonCard(52, 332, 1, 10, '2 watched', true);
  h += inner.row(492, 600, { icon: SEASON, title: 'Season 2' }, S2, -1, { shift: 2 })
    .replace('style="left:492px;top:600px"', 'style="left:52px;top:600px"');
  h += seasonCard(52, 670, 2, 8, 'not started', false);
  return h + '<div class="cap2">26 · A: the season card with the season\'s own poster, "Open season ›"</div>';
}

function frame27() {
  let h = bar('TV Shows') + hero(SHOW, BTNS, -680) + '<div class="barblur"></div>' + tabs(170, 'Episodes');
  h += `<div class="picker"><span class="f">${svg('<path d="M15 6l-6 6 6 6"/>', 24)}Season 1${svg('<path d="M9 6l6 6-6 6"/>', 24)}</span>
    <small>10 episodes · 2 watched · of 2 seasons</small><span style="margin-left:24px">${svg(SEASON, 24)}Season page</span></div>`;
  h += inner.row(52, 330, { icon: '', title: '' }, S1, -1).replace(/<div class="rowh"[\s\S]*?<\/div>/, '');
  h += inner.row(52, 600, { icon: '', title: '' }, S1b, -1, { shift: 3 }).replace(/<div class="rowh"[\s\S]*?<\/div>/, '');
  return h + '<div class="cap2">27 · B: ‹ Season 1 › picker steps seasons; Season page beside it; episodes below</div>';
}

function frame28() {
  const sw = on => `<span class="sw${on ? ' on' : ''}"></span>`;
  const base = inner.frame6().replace(/<div class="mini-logo">[^<]*<\/div>/, hero(SHOW, BTNS, -680) + '<div class="barblur"></div>')
    .replace(/<div class="cap2">[\s\S]*?<\/div>/, '');
  const trakt = '<svg width="28" height="28" viewBox="0 0 24 24"><circle cx="12" cy="12" r="10" fill="none" stroke="currentColor" stroke-width="1.8"/><path d="M7 12.5l3 3 7-7" fill="none" stroke="currentColor" stroke-width="1.8"/></svg>';
  return base + `<div class="scrim"></div><div class="pop dlg"><div class="ti">Delete Harbour Lights?</div>
    <div class="l1"><b>Tidelands.S01E11.Harbour.Lights.1080p.mkv</b></div>
    <div class="l2" style="font-size:24px">S1 E11 · 1080p · 2.3 GB · Media server</div>
    <div class="h">Earlier Episodes</div>
    <div class="i on" style="margin:0;padding:0 14px;height:62px"><span class="ic">${svg(K.fixA)}</span><span>Also delete</span>
      <span class="fill"></span><span style="display:flex;align-items:center;gap:12px;font-weight:500">
      ${svg('<path d="M15 6l-6 6 6 6"/>', 24)}Watched only · E1 – E10 except E7${svg('<path d="M9 6l6 6-6 6"/>', 24)}</span></div>
    <div class="l2" style="margin:6px 0 0 52px">Deletes 10 files · 23.3 GB.</div>
    <div class="h">Clean Up</div>
    ${item(P.hist, 'Also clear viewing history in Supernova', { nosep: true, right: sw(false) })}
    <div class="i nosep" style="margin:0;padding:0 4px;height:62px"><span class="ic">${trakt}</span><span>Also remove them from your Trakt history</span><span class="fill"></span>${sw(false)}</div>
    ${item(P.folder, "Delete the folder if it's left empty", { nosep: true, right: sw(true) })}
    <div class="bt"><span class="f">Keep files</span><span>${svg(J.bin, 24)}Delete 10 files</span></div></div>`
    + '<div class="cap2">28 · Also delete steps: None · Watched only (9) · All earlier (10)</div>';
}

(async () => {
  const browser = await chromium.launch();
  const pg = await browser.newPage({ viewport: { width: 1920, height: 1080 }, deviceScaleFactor: 1 });
  for (const [name, fn] of [['26-season-card-a', frame26], ['27-season-picker-b', frame27], ['28-delete-earlier-choice', frame28]]) {
    const html = `<!doctype html><html><head><meta charset="utf-8"><style>${css}</style></head><body>
      <div class="screen">${fn()}<div class="border"></div></div>${speckle()}</body></html>`;
    await pg.setContent(html);
    await pg.evaluate(() => document.fonts.ready);
    await pg.screenshot({ path: path.join(__dirname, 'tv', `${name}.png`) });
  }
  await browser.close();
})();
