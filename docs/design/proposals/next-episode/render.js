// Next episode on the hero (owner, 25 Nov 2026): a running TV show's hero says when its next episode airs (idea seen
// in Plozz). One small outlined label (the small-box shape, outline = information, RULES §3), only when an episode
// airs within 7 days; "every Sunday" only once the last three aired on that weekday. Built on the one shared hero
// (RULES §14d), so Home and Details match. 01 A: label above the logo (Details). 02 B: label at the end of the facts
// line (Details). 03 A on Home's hero, "New episode today".
// Run: FONT_DIR=<dir with roboto-latin-*-normal.woff2> node render.js
const fs = require('fs');
const path = require('path');
let src = fs.readFileSync(path.join(__dirname, '..', 'home-v3', 'render.js'), 'utf8');
src = src.slice(0, src.lastIndexOf('const FRAMES = ['));
src = src.replace("const { chromium } = require('playwright');", '');
const m = new Function('require', '__dirname', `${src}
return { css, svg, I, speckle, row, ADDED };`)(require, path.join(__dirname, '..', 'home-v3'));
const { chromium } = require('playwright');
const { css, svg, I, speckle, row, ADDED } = m;

const CAL = '<rect x="3" y="5" width="18" height="16" rx="2"/><path d="M3 10h18M8 3v4M16 3v4"/>'
  + '<circle cx="12" cy="15.5" r="1.6" fill="currentColor"/>';
const PLAY = '<path d="M7 4l13 8-13 8z"/>';

const extra = `${css}
.newep{display:inline-flex;align-items:center;gap:10px;height:52px;padding:0 22px;border-radius:20px;font-size:24px;
 font-weight:500;background:rgba(10,11,13,.55);box-shadow:inset 0 0 0 1.5px rgba(255,255,255,.7);align-self:flex-start}
.newep.above{margin-bottom:22px}
.facts .newep{height:44px;padding:0 18px;font-size:22px;margin-left:8px}
.dtabs{position:absolute;left:52px;top:974px;display:flex;gap:8px;font-size:28px}
.dtabs span{height:52px;padding:0 22px;border-radius:20px;display:flex;align-items:center;gap:12px}
.dline{position:absolute;left:52px;right:52px;top:1036px;height:2px;background:linear-gradient(90deg,
 rgba(255,255,255,0),rgba(255,255,255,.35) 10%,rgba(255,255,255,.35) 90%,rgba(255,255,255,0))}

`;

const ART = 'radial-gradient(ellipse 55% 75% at 70% 38%,rgba(120,170,200,.6),transparent 62%),'
  + 'radial-gradient(ellipse 40% 50% at 88% 80%,rgba(40,80,110,.7),transparent 70%),'
  + 'linear-gradient(160deg,#20384a,#0a1219 70%)';

function bar(open) {
  const words = ['Home', 'Movies', 'TV Shows', 'Documentaries'];
  return `<div class="bar">${words.map(w => `<span class="${w === open ? 'open' : ''}">${w}</span>`).join('')}
    <span class="sp"></span><span>Files</span><span>Search</span><span>Settings</span><span>18:40</span></div>`;
}

const SHOW = {
  logo: 'Tidelands', age: '15', facts: '2024– · 2 seasons · Crime, Drama, Mystery',
  desc: 'A harbour detective with a buried past is pulled back to the island where she grew up, as a string of '
    + 'drownings stirs up the town\'s oldest secrets.',
};

function label(text, where) {
  return `<span class="newep ${where}">${svg(CAL, where === 'above' ? 26 : 22)}${text}</span>`;
}

// place: 'above' (A, own line above the logo) or 'facts' (B, end of the facts line)
function hero(t, buttons, place, text) {
  const above = place === 'above' ? label(text, 'above') : '';
  const inFacts = place === 'facts' ? label(text, 'facts') : '';
  return `<div class="hero"><div class="art" style="background:${ART}"></div><div class="shade"></div>
    <div class="text">${above}<div class="logo">${t.logo}</div><div class="facts"><span class="age">${t.age}</span>
    ${t.facts}${inFacts}</div><div class="desc">${t.desc}</div></div><div class="btns">${buttons}</div>
    <div class="edge"></div></div>`;
}

const DBTNS = `<span class="btn f">${svg(PLAY, 24)}Resume S2 E3 · 31 min left</span>
  <span class="btn">${svg(I.more, 26)}More</span>`;

function dtabs() {
  return `<div class="dtabs"><span>${svg(I.info, 26)}Details</span><span>${svg(I.eps, 26)}Episodes</span>
    <span>${svg(I.extras, 26)}Extras</span><span>${svg(I.like, 26)}More Like This</span></div><div class="dline"></div>`;
}

function f01() {
  return bar('TV Shows') + hero(SHOW, DBTNS, 'above', 'New episode every Sunday') + dtabs()
    + '<div class="cap2">1 · A: above the logo</div>';
}

function f02() {
  return bar('TV Shows') + hero(SHOW, DBTNS, 'facts', 'New episode every Sunday') + dtabs()
    + '<div class="cap2">2 · B: end of the facts line</div>';
}

function f03() {
  const btn = `<span class="btn f">${svg(I.more, 26)}More</span>`;
  const dots = '<div class="dots"><i></i><i></i><i class="on"></i><i></i><i></i></div>';
  return bar('Home') + hero(SHOW, btn, 'above', 'New episode today') + dots
    + row(52, 990, { icon: I.added, title: 'Recently Added' }, ADDED, -1)
    + '<div class="cap2">3 · A on Home\'s hero</div>';
}

const FRAMES = [['01-details-above-logo', f01], ['02-details-facts-line', f02], ['03-home-today', f03]];

(async () => {
  fs.mkdirSync(path.join(__dirname, 'tv'), { recursive: true });
  const browser = await chromium.launch();
  const pg = await browser.newPage({ viewport: { width: 1920, height: 1080 }, deviceScaleFactor: 1 });
  for (const [name, fn] of FRAMES) {
    const html = `<!doctype html><html><head><meta charset="utf-8"><style>${extra}</style></head><body>
      <div class="screen">${fn()}<div class="border"></div></div>${speckle()}</body></html>`;
    await pg.setContent(html);
    await pg.evaluate(() => document.fonts.ready);
    await pg.screenshot({ path: path.join(__dirname, 'tv', `${name}.png`) });
  }
  await browser.close();
})();
