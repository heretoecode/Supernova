// Details v3, round 5 (owner, 21 Nov 2026): the season card. 24 Episodes on a show: each season row starts with a
// season card (the season's artwork, "Season 1", "10 episodes · 7 watched", the badge for the season), focused;
// OK opens the season page. 25 the season page it opens: the shared hero with "Season 1" as its title line, Resume
// and More, the tabs below.
// Run: FONT_DIR=<dir with roboto-latin-*-normal.woff2> node render-5.js
const fs = require('fs');
const path = require('path');
let src = fs.readFileSync(path.join(__dirname, 'render-4.js'), 'utf8');
src = src.slice(0, src.lastIndexOf("const FRAMES = [['"));
src = src.replace("const { chromium } = require('playwright');", '');
const m = new Function('require', '__dirname', `${src}
return { css2, svg, I, D, bar, hero, speckle, tabs, inner, BTNS };`)(require, __dirname);
const { chromium } = require('playwright');
const { css2, svg, I, D, bar, hero, speckle, tabs, inner } = m;

const css = `${css2}
.scard .art{background:radial-gradient(ellipse 60% 70% at 50% 35%,rgba(160,190,210,.45),transparent 65%),
 linear-gradient(160deg,#2c4656,#0e1820)!important}
.scard .snum{position:absolute;left:0;right:0;top:22%;text-align:center;font-weight:900;font-size:64px;letter-spacing:2px;
 color:rgba(244,246,251,.92);z-index:2}
`;

function seasonRow(y, n, eps, watched, pie, focusSeason, cards) {
  const SEASON = '<path d="M4 6h1.5v4M4 10h3M4 14.5c0-1 2.8-1 2.8.3 0 1.2-2.8 2-2.8 3.2h3M11 7h9M11 12h9M11 17h9"/>';
  let h = inner.row(52 + 440, y, { icon: SEASON, title: `Season ${n}` }, cards, -1, { shift: n });
  h = h.replace(/left:492px;top:\d+px">/, m0 => m0);
  // heading back at the margin
  h = h.replace(`style="left:492px;top:${y}px"`, `style="left:52px;top:${y}px"`);
  const top = y + 70;
  const f = focusSeason;
  const badge = pie === 'w' ? '<path d="M15 24.5l6 6 12-13" fill="none" stroke="#000" stroke-width="4" stroke-linecap="round" stroke-linejoin="round"/>'
    : (() => { const a = 2 * Math.PI * pie - Math.PI / 2; return `<path d="M24 24 L24 3 A21 21 0 ${pie > 0.5 ? 1 : 0} 1 ${24 + 21 * Math.cos(a)} ${24 + 21 * Math.sin(a)} Z" fill="#000"/>`; })();
  const card = `<div class="card scard${f ? ' focus' : ''}" style="left:${52 - (f ? 16 : 0)}px;top:${top - (f ? 9 : 0)}px">
    <div class="art"></div><div class="snum">S${n}</div><div class="fade"></div>
    <div class="badge"><svg width="48" height="48" viewBox="0 0 48 48">${badge}</svg></div>
    <div class="ct"><div class="c1">Season ${n}</div><div class="c2">${eps} episodes · ${watched}</div></div>
    <div class="cedge"></div></div>`;
  return h + card;
}

function frame24() {
  const S1 = [['Arrival', 'S1 E1 · 52 min', 'w'], ['Low Water', 'S1 E2 · 49 min', 'w'],
    ['The Long Tide', 'S1 E3 · 31 min left', 0.4], ['Harbour Lights', 'S1 E4 · 51 min', null]];
  const S2 = [['Return', 'S2 E1 · 55 min', null], ['Driftwood', 'S2 E2 · 48 min', null],
    ['Fog', 'S2 E3 · 52 min', null], ['Wreck', 'S2 E4 · 50 min', null]];
  const FILM = { logo: 'Tidelands', age: '12', facts: '2024–2025 · 2 seasons · Documentary, Nature, History, Travel',
    desc: 'x' };
  return bar('TV Shows') + hero(FILM, m.BTNS, -680) + '<div class="barblur"></div>' + tabs(170, 'Episodes')
    + seasonRow(262, 1, 10, '2 watched', 0.25, true, S1)
    + seasonRow(600, 2, 8, 'not started', 0, false, S2).replace(/<div class="badge">[\s\S]*?<\/svg><\/div>(?=\s*<div class="ct"><div class="c1">Season 2)/, '')
    + '<div class="cap2">24 · Episodes: a season card starts each row; OK opens that season\'s page</div>';
}

function frame25() {
  const SEASON = { logo: 'Tidelands', age: '12',
    facts: '<span><b style="font-weight:500">Season 1</b> · 2024 · 10 episodes · Documentary, Nature, History, Travel</span>',
    desc: 'The first spring on the outer islands: seal hunters, ferrymen and a lighthouse family face the year the '
      + 'mainland finally comes for their children.' };
  const btns = `<span class="btn f">${svg(D.play, 24)}Resume S1 E3 · 31 min left</span><span class="btn">
    ${svg(I.more, 26)}More</span>`;
  return bar('TV Shows') + hero(SEASON, btns) + tabs(972, 'Details')
    + '<div class="cap2">25 · The season page: "Season 1" leads the facts line, no line of its own</div>';
}

(async () => {
  const browser = await chromium.launch();
  const pg = await browser.newPage({ viewport: { width: 1920, height: 1080 }, deviceScaleFactor: 1 });
  for (const [name, fn] of [['24-season-card', frame24], ['25-season-page', frame25]]) {
    const html = `<!doctype html><html><head><meta charset="utf-8"><style>${css}</style></head><body>
      <div class="screen">${fn()}<div class="border"></div></div>${speckle()}</body></html>`;
    await pg.setContent(html);
    await pg.evaluate(() => document.fonts.ready);
    await pg.screenshot({ path: path.join(__dirname, 'tv', `${name}.png`) });
  }
  await browser.close();
})();
