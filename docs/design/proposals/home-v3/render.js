// Home v3 (owner, 21 Nov 2026): Home on today's rules, built from ONE hero and ONE row, the same pieces Details uses.
// Hero (global): 1776 x 820 at x 72, y 112; artwork with the 1 px white edge and inner shade; a dark shade from the
// left; the text block anchored to the bottom so the buttons never move: buttons 56 px from the foot, description 40
// above them (two lines), the facts line 16 above that, an optional title line above it, the logo (inside 900 x 240)
// 24 px above. Home: one button, More, and carousel dots under the hero. Details: Play / Resume and More, the tabs
// under the hero. Row (global): heading with icon (32 px), 26 px to the cards; cards 400 x 225, 24 px apart, design A
// (title and one line over the artwork's foot), corner badge, focus grows to 440 x 248 with the 4 px ring.
// 1920 x 1080 for the TV. Made-up titles.
// Run: FONT_DIR=<dir with roboto-latin-*-normal.woff2> node render.js
const fs = require('fs');
const path = require('path');
const { chromium } = require('playwright');

const fontDir = process.env.FONT_DIR;
const OUT = path.join(__dirname, 'tv');

function font(w) {
  const data = fs.readFileSync(path.join(fontDir, `roboto-latin-${w}-normal.woff2`)).toString('base64');
  return `@font-face{font-family:R;font-weight:${w};src:url(data:font/woff2;base64,${data}) format('woff2')}`;
}

function svg(d, s = 28) {
  return `<svg width="${s}" height="${s}" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8"
    stroke-linecap="round" stroke-linejoin="round">${d}</svg>`;
}

const I = {
  more: '<circle cx="5" cy="12" r="1.3" fill="currentColor"/><circle cx="12" cy="12" r="1.3" fill="currentColor"/>'
    + '<circle cx="19" cy="12" r="1.3" fill="currentColor"/>',
  play: '<path d="M7 4l13 8-13 8z"/>',
  cont: '<circle cx="12" cy="12" r="9"/><path d="M10 8.5l5 3.5-5 3.5z"/>',
  added: '<path d="M3 13l3-8h12l3 8v6a1 1 0 0 1-1 1H4a1 1 0 0 1-1-1z"/><path d="M3 13h5l1.5 2.5h5L16 13h5"/>'
    + '<path d="M12 3.5v7M9 8l3 3 3-3"/>',
  trend: '<path d="M3 17l6-6 4 4 8-8M15 7h6v6"/>',
  move: '<path d="M8 9l4-4 4 4M8 15l4 4 4-4"/>',
  hide: '<circle cx="12" cy="12" r="9"/><path d="M8 12h8"/>',
  info: '<circle cx="12" cy="12" r="9"/><path d="M12 11v6M12 7.5v.1"/>',
  eps: '<rect x="3" y="4" width="18" height="13" rx="2"/><path d="M10 8l4 2.5-4 2.5zM8 21h8"/>',
  extras: '<rect x="3" y="7" width="18" height="13" rx="2"/><path d="M3 11h18M7 7l2-4M12 7l2-4M17 7l2-4"/>',
  like: '<path d="M12 3l1.8 5.2L19 10l-5.2 1.8L12 17l-1.8-5.2L5 10l5.2-1.8z"/>',
  season: '<path d="M4 6h1.5v4M4 10h3M4 14.5c0-1 2.8-1 2.8.3 0 1.2-2.8 2-2.8 3.2h3M11 7h9M11 12h9M11 17h9"/>',
};

const css = `${[400, 500, 700, 900].map(font).join('')}
*{box-sizing:border-box;margin:0;padding:0}
body{background:#111215;font-family:R,sans-serif;width:1920px;height:1080px;overflow:hidden;color:#fff}
.screen{position:relative;width:1920px;height:1080px;overflow:hidden}
.screen>canvas{position:absolute;left:0;top:0}
.border{position:absolute;inset:0;border:2px solid #c9ccd3;z-index:40}
.bar{position:absolute;top:0;left:0;right:0;height:84px;display:flex;align-items:center;padding:0 30px;font-size:30px;
 z-index:5}
.bar span{height:52px;padding:0 22px;border-radius:20px;display:flex;align-items:center}
.bar .open{background:rgba(255,255,255,.15);color:rgba(255,255,255,.72)}
.bar .sp{flex:1}
.hero{position:absolute;left:72px;top:112px;width:1776px;height:820px;border-radius:24px;overflow:hidden}
.hero .art{position:absolute;inset:0}
.hero .shade{position:absolute;inset:0;background:linear-gradient(90deg,rgba(10,11,13,.92) 0%,rgba(10,11,13,.75) 30%,
 rgba(10,11,13,.25) 58%,rgba(10,11,13,0) 75%),linear-gradient(0deg,rgba(10,11,13,.55),rgba(10,11,13,0) 45%)}
.hero .edge{position:absolute;inset:0;border-radius:24px;box-shadow:inset 0 0 0 1px #fff,inset 0 0 40px rgba(0,0,0,.35)}
.hero .text{position:absolute;left:64px;bottom:148px;width:900px;display:flex;flex-direction:column}
.logo{font-weight:900;font-size:104px;line-height:.9;letter-spacing:-1px;text-transform:uppercase;color:#f4f6fb;
 margin-bottom:24px;max-height:240px}
.tline{font-size:26px;font-weight:500;margin-bottom:12px}
.facts{display:flex;align-items:center;gap:12px;font-size:26px;margin-bottom:16px}
.age{border:1.5px solid #fff;border-radius:6px;padding:1px 8px;font-size:22px;font-weight:500}
.desc{font-size:24px;line-height:34px;color:#e8ecf4;max-width:860px;display:-webkit-box;-webkit-line-clamp:2;
 -webkit-box-orient:vertical;overflow:hidden}
.hero .btns{position:absolute;left:64px;bottom:56px;display:flex;gap:12px}
.btn{height:52px;padding:0 24px;border-radius:20px;display:flex;align-items:center;gap:10px;font-size:24px;
 background:rgba(10,11,13,.72)}
.btn.f{background:#f4f6fb;color:#0b0d12}
.dots{position:absolute;left:0;right:0;top:952px;display:flex;justify-content:center;gap:10px}
.dots i{width:10px;height:10px;border-radius:5px;background:rgba(255,255,255,.4)}
.dots i.on{width:32px;background:#fff}
.tabs{position:absolute;left:72px;top:960px;display:flex;gap:8px;font-size:28px}
.tabs span{height:52px;padding:0 22px;border-radius:20px;display:flex;align-items:center;gap:12px}
.tabs .open{box-shadow:inset 0 -3px 0 rgba(255,255,255,.45);border-radius:0}
.tabline{position:absolute;left:72px;right:72px;top:1020px;height:1px;background:rgba(255,255,255,.18)}
.rowh{position:absolute;display:flex;align-items:center;gap:14px;font-size:32px;font-weight:500}
.rowh .rb{display:flex;gap:8px;margin-left:24px;font-size:24px;font-weight:400}
.rowh .rb span{height:52px;padding:0 22px;border-radius:20px;display:flex;align-items:center;gap:10px}
.rowh .rb .f{background:#f4f6fb;color:#0b0d12}
.card{position:absolute;width:400px;height:225px;border-radius:14px;overflow:hidden}
.card.focus{width:440px;height:248px;z-index:3;box-shadow:0 0 0 4px #fff,0 12px 30px rgba(0,0,0,.5)}
.card .art{position:absolute;inset:0}
.card .fade{position:absolute;left:0;right:0;bottom:0;height:60%;background:linear-gradient(0deg,rgba(8,9,11,.92),
 rgba(8,9,11,0))}
.card .cedge{position:absolute;inset:0;border-radius:14px;box-shadow:inset 0 0 0 1px #fff}
.card.focus .cedge{display:none}
.card .ct{position:absolute;left:18px;right:18px;bottom:14px}
.card .c1{font-size:24px;font-weight:500;white-space:nowrap;overflow:hidden;text-overflow:ellipsis}
.card .c2{font-size:19px;color:#c9d3e6;margin-top:4px;white-space:nowrap;overflow:hidden;text-overflow:ellipsis}
.card.focus .c1{font-size:26px}
.card.focus .c2{font-size:21px}
.card .logo2{position:absolute;left:0;right:0;top:34%;text-align:center;font-weight:900;font-size:30px;
 letter-spacing:1px;color:rgba(244,246,251,.9)}
.badge{position:absolute;top:8px;right:8px;width:48px;height:48px;border-radius:50%;background:#fff;
 box-shadow:0 1px 6px rgba(0,0,0,.45)}
.badge svg{position:absolute;left:0;top:0}
.cap2{position:absolute;right:52px;bottom:24px;color:#e8ebf2;font-size:24px;z-index:41;background:rgba(17,18,21,.85);
 padding:4px 12px;border-radius:8px}
.arrowL{position:absolute;color:#fff}
`;

function speckle() {
  return `<script>{
  const rng=function(seed){return()=>{seed|=0;seed=seed+0x6D2B79F5|0;let t=Math.imul(seed^seed>>>15,1|seed);
    t=t+Math.imul(t^t>>>7,61|t)^t;return((t^t>>>14)>>>0)/4294967296}};
  const sc=document.querySelector('.screen');const cv=document.createElement('canvas');cv.width=1920;cv.height=1080;
  sc.prepend(cv);const g=cv.getContext('2d'),r=rng(20261118),n=1920*1080*20/1000;g.fillStyle='#111215';
  g.fillRect(0,0,1920,1080);for(let i=0;i<n;i++){const x=r()*1920|0,y=r()*1080|0,a=r()<1/7?.45:.07+r()*.15;
  g.fillStyle='rgba(255,255,255,'+a+')';g.fillRect(x,y,1,1)}}</script>`;
}

function bar(open) {
  const words = ['Home', 'Movies', 'TV Shows', 'Documentaries'];
  return `<div class="bar">${words.map(w => `<span class="${w === open ? 'open' : ''}">${w}</span>`).join('')}
    <span class="sp"></span><span>Files</span><span>Search</span><span>Settings</span><span>18:40</span></div>`;
}

// ---- the ONE hero ----
const HERO_ART = 'radial-gradient(ellipse 60% 70% at 72% 40%,rgba(150,180,200,.55),transparent 60%),'
  + 'radial-gradient(ellipse 40% 50% at 85% 75%,rgba(70,110,140,.6),transparent 70%),'
  + 'linear-gradient(160deg,#2c4656,#0e1820 70%)';

function hero(t, buttons, y = 112) {
  const tline = t.titleLine ? `<div class="tline">${t.titleLine}</div>` : '';
  return `<div class="hero" style="top:${y}px"><div class="art" style="background:${HERO_ART}"></div><div class="shade">
    </div><div class="text"><div class="logo">${t.logo}</div>${tline}<div class="facts"><span class="age">${t.age}</span>
    ${t.facts}</div><div class="desc">${t.desc}</div></div><div class="btns">${buttons}</div><div class="edge"></div></div>`;
}

const FILM = {
  logo: 'The Last<br>Crossing', age: '15', facts: '2025 · 2 h 11 min · Drama, Thriller',
  desc: 'A ferryman who knows every current of the strait agrees to carry one last passenger across, and finds the '
    + 'whole town waiting on the other side.',
};

// ---- the ONE row ----
const ARTS = [['#7a3b2e', '#26120d'], ['#2f5c4a', '#0e1d17'], ['#5a4a7a', '#1a1426'], ['#8a6a2a', '#2a1f0a'],
  ['#2e4f78', '#0d1726'], ['#6d2f52', '#1f0c18'], ['#3f6b72', '#0f1e21']];

function pie(p) {
  const a = 2 * Math.PI * p - Math.PI / 2;
  const x = 24 + 21 * Math.cos(a);
  const y = 24 + 21 * Math.sin(a);
  return `<path d="M24 24 L24 3 A21 21 0 ${p > 0.5 ? 1 : 0} 1 ${x} ${y} Z" fill="#000"/>`;
}

function badge(st) {
  if (st == null) {
    return '';
  }
  const m = st === 'w' ? '<path d="M15 24.5l6 6 12-13" fill="none" stroke="#000" stroke-width="4" '
    + 'stroke-linecap="round" stroke-linejoin="round"/>' : pie(st);
  return `<div class="badge"><svg width="48" height="48" viewBox="0 0 48 48">${m}</svg></div>`;
}

// heading: {icon, title, buttons?}; cards: [title, line, state]; focus index (-1 none)
function row(x, y, heading, cards, focus, opts = {}) {
  let h = `<div class="rowh" style="left:${x}px;top:${y}px">${svg(heading.icon, 32)}${heading.title}`;
  if (opts.headingButtons) {
    h += `<span class="rb"><span class="${opts.headingFocus === 'move' ? 'f' : ''}">${svg(I.move, 24)}Move</span>
      <span>${svg(I.hide, 24)}Hide</span></span>`;
  }
  h += '</div>';
  const top = y + 44 + 26;
  let cx = x;
  cards.forEach((c, k) => {
    const a = ARTS[(k + (opts.shift || 0)) % 7];
    const f = k === focus;
    const yy = f ? top - 11 : top;
    const xx = f ? cx - 20 : cx;
    h += `<div class="card${f ? ' focus' : ''}" style="left:${xx}px;top:${yy}px"><div class="art" style="background:
      radial-gradient(ellipse 60% 60% at 60% 35%,rgba(255,255,255,.16),transparent 60%),linear-gradient(155deg,${a[0]},
      ${a[1]})"></div><div class="fade"></div>${badge(c[2])}
      <div class="ct"><div class="c1">${c[0]}</div><div class="c2">${c[1]}</div></div><div class="cedge"></div></div>`;
    cx += 424;
  });
  return h;
}

const CONT = [['Tidelands', 'S1 E2 · 31 min left', 0.1], ['The Salt Road', '42 min left', 0.6],
  ['Northern Lights', 'S2 E5 · 18 min left', 0.7], ['Cold Iron', '1 h 2 min left', 0.25], ['Glass Bay', 'S1 E4', null]];
const ADDED = [['Frozen Frontier', '2021 · 6 episodes', null], ['Inside the Hive', '2023 · 1 h 24 min', 'w'],
  ['Mountain Light', '2023 · 1 h 47 min', null], ['Paper Harbour', '2024 · 1 h 38 min', null],
  ['Low Tide', '2021 · 1 h 30 min', null]];
const TREND = [['Night Shift', '3 seasons', null], ['Golden Mile', '2025 · 2 h 4 min', null],
  ['Velvet', '1 season · 8 episodes', 0.3], ['The Isle', '2024 · 1 h 52 min', null], ['Greenwood', '2 seasons', null]];

function home1() {
  return bar('Home') + hero(FILM, `<span class="btn f">${svg(I.more, 26)}More</span>`)
    + '<div class="dots"><i class="on"></i><i></i><i></i><i></i><i></i></div>'
    + row(72, 992, { icon: I.cont, title: 'Continue Watching' }, CONT, -1)
    + '<div class="cap2">1 · Home at rest: the hero, the dots, the first row peeking</div>';
}

function details1() {
  return bar('Movies') + hero(FILM, `<span class="btn f">${svg(I.play, 24)}Play</span><span class="btn">
    ${svg(I.more, 26)}More</span>`) + `<div class="tabs"><span class="open">${svg(I.info, 26)}Details</span>
    <span>${svg(I.extras, 26)}Extras</span><span>${svg(I.like, 26)}More Like This</span></div><div class="tabline"></div>`
    + '<div class="cap2">2 · Details: the same hero, its buttons, tabs below</div>';
}

function home2() {
  // scrolled: the hero has gone up; rows only
  return bar('Home') + row(72, 120, { icon: I.cont, title: 'Continue Watching' }, CONT, 0)
    + row(72, 480, { icon: I.added, title: 'Recently Added' }, ADDED, -1, { shift: 2 })
    + row(72, 840, { icon: I.trend, title: 'Trending on Trakt' }, TREND, -1, { shift: 4 })
    + '<div class="cap2">3 · The rows: one row design for the whole app</div>';
}

function home3() {
  return bar('Home') + row(72, 120, { icon: I.cont, title: 'Continue Watching' }, CONT, -1,
    { headingButtons: true, headingFocus: 'move' })
    + row(72, 480, { icon: I.added, title: 'Recently Added' }, ADDED, -1, { shift: 2 })
    + row(72, 840, { icon: I.trend, title: 'Trending on Trakt' }, TREND, -1, { shift: 4 })
    + '<div class="cap2">4 · Up from a card: Move and Hide beside the heading (Home only)</div>';
}

function details2() {
  const S1 = [['Arrival', 'S1 E1 · 52 min', 'w'], ['Low Water', 'S1 E2 · 49 min', 'w'],
    ['The Long Tide', 'S1 E3 · 31 min left', 0.4], ['Harbour Lights', 'S1 E4 · 51 min', null],
    ['Salt', 'S1 E5 · 50 min', null]];
  const S2 = [['Return', 'S2 E1 · 55 min', null], ['Driftwood', 'S2 E2 · 48 min', null],
    ['Fog', 'S2 E3 · 52 min', null], ['Wreck', 'S2 E4 · 50 min', null], ['Landfall', 'S2 E5 · 58 min', null]];
  return bar('TV Shows') + `<div class="tabs" style="top:110px"><span>${svg(I.info, 26)}Details</span>
    <span class="open">${svg(I.eps, 26)}Episodes</span><span>${svg(I.extras, 26)}Extras</span>
    <span>${svg(I.like, 26)}More Like This</span></div><div class="tabline" style="top:170px"></div>`
    + row(72, 220, { icon: I.season, title: 'Season 1' }, S1, 2)
    + row(72, 580, { icon: I.season, title: 'Season 2' }, S2, -1, { shift: 3 })
    + '<div class="cap2">5 · Details › Episodes: the same rows</div>';
}

const FRAMES = [['01-home-hero', home1], ['02-details-hero', details1], ['03-home-rows', home2],
  ['04-home-row-heading', home3], ['05-details-episodes', details2]];

(async () => {
  fs.mkdirSync(OUT, { recursive: true });
  const browser = await chromium.launch();
  const pg = await browser.newPage({ viewport: { width: 1920, height: 1080 }, deviceScaleFactor: 1 });
  for (const [name, fn] of FRAMES) {
    const html = `<!doctype html><html><head><meta charset="utf-8"><style>${css}</style></head><body>
      <div class="screen">${fn()}<div class="border"></div></div>${speckle()}</body></html>`;
    await pg.setContent(html);
    await pg.evaluate(() => document.fonts.ready);
    await pg.screenshot({ path: path.join(OUT, `${name}.png`) });
  }
  await browser.close();
})();
