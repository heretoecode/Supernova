// v11 (owner, 20 Nov 2026): the Filters menu and its four lists as approved (23, 24), redrawn with today's rules:
// speckled background, the 0.158 top bar, 52 px toolbar boxes over the 35% tapered line, menus at 99% with the 70%
// white outline and dark shadow, centred Title Case group headings, the corner progress pie, three-line captions.
// A list opened from the menu sits on top of it; the menu behind is darkened and pushed back (RULES §11).
// 1920 x 1080 for viewing on the TV. Made-up titles.
// Run: FONT_DIR=<dir with roboto-latin-*-normal.woff2> node render-v11-filters.js
const fs = require('fs');
const path = require('path');
const { chromium } = require('playwright');

const fontDir = process.env.FONT_DIR;
const OUT = path.join(__dirname, 'tv');
const RES = path.join(__dirname, '../../../../res/drawable');

function font(w) {
  const data = fs.readFileSync(path.join(fontDir, `roboto-latin-${w}-normal.woff2`)).toString('base64');
  return `@font-face{font-family:R;font-weight:${w};src:url(data:font/woff2;base64,${data}) format('woff2')}`;
}

function svg(d, s = 28) {
  return `<svg width="${s}" height="${s}" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8"
    stroke-linecap="round" stroke-linejoin="round">${d}</svg>`;
}

function logo(name, colour) {
  const xml = fs.readFileSync(path.join(RES, `provider_${name}.xml`), 'utf8');
  const vw = xml.match(/viewportWidth="([\d.]+)"/)[1];
  const vh = xml.match(/viewportHeight="([\d.]+)"/)[1];
  const paths = [...xml.matchAll(/pathData="([^"]+)"/g)].map(m => `<path d="${m[1]}" fill="${colour}"/>`).join('');
  return `<svg viewBox="0 0 ${vw} ${vh}" preserveAspectRatio="xMidYMid meet">${paths}</svg>`;
}

const I = {
  filter: '<path d="M3 5h18l-7 8v6l-4 2v-8z"/>',
  sort: '<path d="M7 4v16M3 16l4 4 4-4M17 20V4M13 8l4-4 4 4"/>',
  order: '<path d="M7 4v16M3 16l4 4 4-4M14 6h7M14 12h5M14 18h3"/>',
  list: '<path d="M8 6h13M8 12h13M8 18h13M3 6h.1M3 12h.1M3 18h.1"/>',
  chev: '<path d="M9 6l6 6-6 6"/>',
  chevL: '<path d="M15 6l-6 6 6 6"/>',
  tick: '<path d="M5 12.5l4.5 4.5L19 7.5"/>',
  x: '<path d="M6 6l12 12M18 6L6 18"/>',
  watched: '<circle cx="12" cy="12" r="9"/><path d="M8 12.5l2.8 2.8L16.5 9.5"/>',
  genre: '<path d="M3 12V4h8l10 10-8 8z"/><circle cx="7.5" cy="8" r="1.5"/>',
  cal: '<rect x="3" y="5" width="18" height="16" rx="2"/><path d="M3 10h18M8 3v4M16 3v4"/>',
  cloud: '<path d="M7 18h10a4 4 0 0 0 .6-8A6 6 0 0 0 6 9.5 4.3 4.3 0 0 0 7 18z"/>',
  all: '<circle cx="12" cy="12" r="9"/>',
  unw: '<path d="M2 12s4-7 10-7 10 7 10 7-4 7-10 7S2 12 2 12z"/><circle cx="12" cy="12" r="3"/>',
  prog: '<circle cx="12" cy="12" r="9"/><path d="M12 3a9 9 0 0 1 0 18z" fill="currentColor"/>',
  match: '<circle cx="9" cy="12" r="5"/><circle cx="15" cy="12" r="5"/>',
  // genres, no two alike
  action: '<path d="M13 3L5 14h6l-1 7 8-11h-6z"/>',
  adventure: '<circle cx="12" cy="12" r="9"/><path d="M15.5 8.5l-2 5-5 2 2-5z"/>',
  animation: '<path d="M12 3l1.8 5.2L19 10l-5.2 1.8L12 17l-1.8-5.2L5 10l5.2-1.8z"/><path d="M19 16l.7 2 2 .7-2 .7-.7 2-.7-2'
    + '-2-.7 2-.7z"/>',
  comedy: '<circle cx="12" cy="12" r="9"/><path d="M8 14a4.5 4.5 0 0 0 8 0M9 9.5h.1M15 9.5h.1"/>',
  crime: '<circle cx="10.5" cy="10.5" r="6"/><path d="M15 15l6 6"/>',
  drama: '<path d="M4 4h10v6a5 5 0 0 1-10 0z"/><path d="M7 8h.1M11 8h.1M7 12.5a2.5 2.5 0 0 1 4 0"/>'
    + '<path d="M14 9h6v6a5 5 0 0 1-8.6 3.5"/>',
  family: '<path d="M3 11l9-7 9 7"/><path d="M5 10v10h14V10"/><path d="M10 20v-5h4v5"/>',
  romance: '<path d="M12 20s-7-4.5-7-10a4 4 0 0 1 7-2.6A4 4 0 0 1 19 10c0 5.5-7 10-7 10z"/>',
  scifi: '<path d="M12 2c3 2 5 6 5 10l-2 4H9l-2-4c0-4 2-8 5-10z"/><circle cx="12" cy="10" r="1.6"/>'
    + '<path d="M9 16l-2 4M15 16l2 4M12 16v5"/>',
};

const css = `${[400, 500, 700].map(font).join('')}
*{box-sizing:border-box;margin:0;padding:0}
body{background:#111215;font-family:R,sans-serif;width:1920px;height:1080px;overflow:hidden}
.screen{position:relative;width:1920px;height:1080px;overflow:hidden}
.screen>canvas{position:absolute;left:0;top:0}
.border{position:absolute;inset:0;border:2px solid #c9ccd3;z-index:40}
.bar{position:absolute;top:0;left:0;right:0;height:84px;display:flex;align-items:center;padding:0 30px;color:#fff;
 font-size:30px}
.bar span{height:52px;padding:0 22px;border-radius:20px;display:flex;align-items:center}
.bar .open{background:rgba(255,255,255,.15);color:rgba(255,255,255,.72)}
.bar .sp{flex:1}
.tb{position:absolute;left:52px;top:102px;height:52px;display:flex;gap:8px;color:#fff;font-size:24px}
.tb span{height:52px;padding:0 22px;border-radius:20px;display:flex;align-items:center;gap:10px;white-space:nowrap}
.poster{position:absolute;width:236px;border-radius:12px;background:#141619}
.art{position:relative;height:354px;border-radius:12px 12px 0 0;overflow:hidden}
.logo{position:absolute;left:8px;right:8px;bottom:40px;text-align:center;font-size:24px;color:#f4f6fa;font-weight:700;
 letter-spacing:2px;text-shadow:0 2px 10px rgba(0,0,0,.5)}
.cap{height:100px;padding:9px 8px 0 14px}
.t1{color:#fff;font-size:24px;font-weight:500;line-height:28px;white-space:nowrap;overflow:hidden;text-overflow:ellipsis}
.t2,.t3{color:#c9d3e6;font-size:18px;line-height:22px;margin-top:6px;white-space:nowrap;overflow:hidden;
 text-overflow:ellipsis}
.pedge{position:absolute;inset:0;border-radius:12px;box-shadow:inset 0 0 0 1px #fff;pointer-events:none;z-index:2}
.badge{position:absolute;top:8px;right:8px;width:48px;height:48px;border-radius:50%;background:#fff;
 box-shadow:0 1px 6px rgba(0,0,0,.45);overflow:hidden}
.badge svg{position:absolute;left:0;top:0}
.menu{position:absolute;border-radius:16px;background:rgba(22,24,28,.99);padding:12px 0;z-index:8;
 box-shadow:0 0 0 1.5px rgba(255,255,255,.7),0 18px 48px 12px rgba(0,0,0,.85);color:#fff}
.menu.back{filter:brightness(.45);transform:scale(.96);transform-origin:top left}
.menu.front{z-index:10;min-width:360px;box-shadow:0 0 0 1.5px rgba(255,255,255,.7),0 24px 64px 16px rgba(0,0,0,.9)}
.h{display:flex;align-items:center;gap:14px;font-size:28px;padding:12px 24px 10px;white-space:nowrap}
.h:before,.h:after{content:"";flex:1;height:1px;background:rgba(255,255,255,.3)}
.i{display:flex;align-items:center;gap:18px;padding:0 24px;font-size:26px;white-space:nowrap;margin:0 10px;
 border-radius:12px;position:relative}
.i.one{height:62px}
.i.two{height:70px}
.i .sep{position:absolute;left:14px;right:14px;bottom:0;height:1px;background:rgba(255,255,255,.08)}
.i.on .sep,.i.nosep .sep{display:none}
.i .ic{width:30px;display:flex;justify-content:center}
.i .lg{width:56px;height:30px;display:flex;align-items:center;justify-content:center}
.i .lg svg{width:56px;height:30px}
.i .tx{display:flex;flex-direction:column;justify-content:center}
.i .tx small{font-size:20px;color:#c9d3e6;margin-top:3px}
.i .fill{flex:1;min-width:56px}
.i .val{font-size:26px;font-weight:500}
.i .val.quiet{color:#c9d3e6;font-weight:400}
.i .ch{display:flex;color:#c9d3e6}
.i.on{background:#f4f6fb;color:#0b0d12}
.i.on .tx small,.i.on .val,.i.on .val.quiet,.i.on .ch{color:#0b0d12}
.i.dis{opacity:.4}
.chips{display:flex;gap:6px}
.chips b{height:46px;padding:0 20px;border-radius:20px;display:flex;align-items:center;font-weight:400}
.chips b.sel{background:rgba(255,255,255,.2)}
.i.on .chips b.sel{background:#0b0d12;color:#fff}
.step{display:flex;align-items:center;gap:14px;font-weight:500}
.rule{height:1px;background:rgba(255,255,255,.3);margin:10px 24px}
.cap2{position:absolute;right:52px;bottom:24px;color:#e8ebf2;font-size:24px;z-index:41;background:rgba(17,18,21,.85);
 padding:4px 12px;border-radius:8px}
`;

const ART = [['#7a3b2e', '#26120d'], ['#2f5c4a', '#0e1d17'], ['#5a4a7a', '#1a1426'], ['#8a6a2a', '#2a1f0a'],
  ['#2e4f78', '#0d1726'], ['#6d2f52', '#1f0c18'], ['#3f6b72', '#0f1e21']];
const MOV = [
  ['The Long Frost', '2025 · 1 h 52 min', 'Added 16 Nov 2026', null],
  ['Golden Mile', '42 min left', 'Added 15 Nov 2026', 0.65],
  ['Quiet Harbour', '2024 · 1 h 38 min', 'Added 14 Nov 2026', 'w'],
  ['Paper Lanterns', '2023 · 2 h 4 min', 'Added 12 Nov 2026', null],
  ['North Road', '2025 · 1 h 41 min', 'Added 10 Nov 2026', null],
  ['Salt and Stone', '2022 · 1 h 55 min', 'Added 9 Nov 2026', 'w'],
  ['Low Tide', '2024 · 1 h 30 min', 'Added 2 Nov 2026', null],
  ['The Clockmaker', '2019 · 2 h 11 min', 'Added 1 Nov 2026', null],
  ['Summer Static', '1 h 5 min left', 'Added 30 Oct 2026', 0.3],
  ['A Field of Kites', '2021 · 1 h 47 min', 'Added 28 Oct 2026', null],
  ['Second Breakfast Club', '2018 · 1 h 36 min', 'Added 27 Oct 2026', 'w'],
  ['Westward', '2016 · 2 h 22 min', 'Added 25 Oct 2026', null],
  ['Lighthouse Keepers', '2020 · 1 h 44 min', 'Added 20 Oct 2026', null],
  ['Cold Open', '2023 · 1 h 29 min', 'Added 18 Oct 2026', null],
];
const UNW = MOV.filter(m => m[3] == null).concat([
  ['Harbour Lights', '2017 · 1 h 49 min', 'Added 15 Oct 2026', null],
  ['The Last Orchard', '2014 · 1 h 58 min', 'Added 12 Oct 2026', null],
  ['Kettle and Crow', '2021 · 1 h 33 min', 'Added 9 Oct 2026', null],
  ['Brightwater', '2012 · 2 h 1 min', 'Added 5 Oct 2026', null],
  ['Rain on Tuesday', '2020 · 1 h 41 min', 'Added 2 Oct 2026', null],
]);

function pie(p) {
  const r = 21;
  const a = 2 * Math.PI * p - Math.PI / 2;
  const x = 24 + r * Math.cos(a);
  const y = 24 + r * Math.sin(a);
  const large = p > 0.5 ? 1 : 0;
  return `<path d="M24 24 L24 3 A21 21 0 ${large} 1 ${x} ${y} Z" fill="#000"/>`;
}

function badge(state) {
  if (state == null) {
    return '';
  }
  const inner = state === 'w'
    ? '<path d="M15 24.5l6 6 12-13" fill="none" stroke="#000" stroke-width="4" stroke-linecap="round" '
      + 'stroke-linejoin="round"/>'
    : pie(state);
  return `<div class="badge"><svg width="48" height="48" viewBox="0 0 48 48">${inner}</svg></div>`;
}

function taper(x0, x1, y, w, alpha) {
  const n = 120;
  const top = [];
  const bot = [];
  for (let i = 0; i <= n; i++) {
    const t = i / n;
    const x = x0 + (x1 - x0) * t;
    const e = Math.min(t, 1 - t);
    const k = e >= 0.22 ? 1 : 0.18 + 0.82 * Math.sin(Math.PI / 2 * e / 0.22);
    top.push([x, y - w * k / 2]);
    bot.unshift([x, y + w * k / 2]);
  }
  const pts = top.concat(bot).map(p => p.join(',')).join(' ');
  return `<polygon points="${pts}" fill="rgba(255,255,255,${alpha})"/>`;
}

function page(items = MOV, filterIcon = svg(I.filter, 26), filterWord = 'Filters', cols = 7) {
  const w = cols === 7 ? 236 : Math.floor((1816 - (cols - 1) * 32) / cols);
  const step = cols === 7 ? 263 : w + 32;
  const artH = Math.round(w * 1.5);
  const words = ['Home', 'Movies', 'TV Shows', 'Documentaries'];
  let h = `<div class="bar">${words.map(w => `<span class="${w === 'Movies' ? 'open' : ''}">${w}</span>`).join('')}
    <span class="sp"></span><span>Files</span><span>Search</span><span>Settings</span><span>18:40</span></div>`;
  h += `<div class="tb"><span>${filterIcon}${filterWord}</span><span>${svg(I.sort, 26)}Sort: Date added</span>
    <span>${svg(I.order, 26)}Newest first</span><span>${svg(I.list, 26)}List view</span></div>`;
  h += `<svg style="position:absolute;left:0;top:0" width="1920" height="1080">${taper(52, 1868, 179, 2, 0.35)}</svg>`;
  for (let r = 0; r < 2; r++) {
    for (let c = 0; c < cols; c++) {
      const it = items[r * cols + c];
      const a = ART[(c + r * 3) % 7];
      const x = 52 + c * step;
      const y = 204 + r * (artH + 100 + 44);
      const bg = `radial-gradient(ellipse 70% 60% at 70% 30%,rgba(255,255,255,.18),transparent 60%),`
        + `linear-gradient(155deg,${a[0]},${a[1]})`;
      h += `<div class="poster" style="left:${x}px;top:${y}px;width:${w}px"><div class="art" style="background:${bg};height:${artH}px">
        <div class="logo">${it[0].toUpperCase()}</div>${badge(it[3])}</div><div class="cap"><div class="t1">${it[0]}</div>
        <div class="t2">${it[1]}</div><div class="t3">${it[2]}</div></div><div class="pedge"></div></div>`;
    }
  }
  return h;
}

const tickSvg = svg(I.tick, 28);
const chevSvg = svg(I.chev, 26);

// Filters menu rows: [icon, label, value, quiet]
function filtersMenu(vals, focus, cls = '') {
  const rows = [
    [I.watched, 'Watched', vals[0]],
    [I.genre, 'Genre', vals[1]],
    [I.cal, 'Year', vals[2]],
    [I.cloud, 'Streaming services', vals[3]],
  ];
  const anySet = vals.some(v => !v[1]);
  let h = `<div class="menu ${cls}" style="left:52px;top:166px">`;
  rows.forEach(([ic, label, [value, quiet]], k) => {
    const on = focus === k ? ' on' : '';
    const last = k === rows.length - 1 ? ' nosep' : '';
    h += `<div class="i one${on}${last}"><span class="ic">${svg(ic)}</span><span class="tx">${label}</span>
      <span class="fill"></span><span class="val${quiet ? ' quiet' : ''}">${value}</span>
      <span class="ch">${chevSvg}</span><i class="sep"></i></div>`;
  });
  const clear = focus === 4 ? ' on' : (anySet ? '' : ' dis');
  h += `<div class="rule"></div><div class="i one nosep${clear}"><span class="ic">${svg(I.x)}</span>
    <span class="tx">Clear filters</span></div></div>`;
  return h;
}

function heading(t) {
  return `<div class="h">${t}</div>`;
}

// list row: lead html, title, second line, ticked, focused, last-in-group
function row(lead, title, sub, ticked, focused, last) {
  const cls = `i two${focused ? ' on' : ''}${last ? ' nosep' : ''}`;
  const second = sub ? `<small>${sub}</small>` : '';
  const tick = ticked ? `<span class="ch" style="color:inherit">${tickSvg}</span>` : '<span style="width:28px"></span>';
  return `<div class="${cls}">${lead}<span class="tx">${title}${second}</span><span class="fill"></span>${tick}
    <i class="sep"></i></div>`;
}

function icon(d) {
  return `<span class="ic">${svg(d)}</span>`;
}

function brand(name, colour) {
  return `<span class="lg">${logo(name, colour)}</span>`;
}

const NONE = [['All', true], ['Any', true], ['Any year', true], ['Any', true]];

function frame1() {
  return page() + filtersMenu(NONE, 0)
    + '<div class="cap2">1 · Filters (nothing set yet): Clear filters dimmed</div>';
}

function frame2() {
  const list = `<div class="menu front" style="left:300px;top:196px">
    ${row(icon(I.all), 'All', '142 titles', true, true)}
    ${row(icon(I.unw), 'Unwatched', '96 titles')}
    ${row(icon(I.prog), 'In progress', '7 titles')}
    ${row(icon(I.watched), 'Watched', '39 titles', false, false, true)}</div>`;
  return page() + filtersMenu(NONE, 0, 'back') + list
    + '<div class="cap2">2 · Watched: one choice, OK ticks and closes</div>';
}

function frame3() {
  const vals = [['Unwatched', false], ['Any', true], ['Any year', true], ['Any', true]];
  const g = (d, name, n, t, f, l) => row(icon(d), name, `${n} titles`, t, f, l);
  const list = `<div class="menu front" style="left:300px;top:196px">
    <div class="i one nosep"><span class="ic">${svg(I.match)}</span><span class="tx">Match</span>
      <span class="fill"></span><span class="chips"><b class="sel">Any</b><b>All</b></span></div>
    ${heading('Selected')}
    ${g(I.comedy, 'Comedy', 28, true)}${g(I.drama, 'Drama', 41, true, false, true)}
    ${heading('All')}
    ${g(I.action, 'Action', 22)}${g(I.adventure, 'Adventure', 19, false, true)}${g(I.animation, 'Animation', 9)}
    ${g(I.crime, 'Crime', 17)}${g(I.romance, 'Romance', 14)}
    ${g(I.scifi, 'Science Fiction', 11, false, false, true)}</div>`;
  return page(UNW) + filtersMenu(vals, 1, 'back') + list
    + '<div class="cap2">3 · Genre: several, Match Any · All, ticked ones under Selected</div>';
}

function frame4() {
  const vals = [['Unwatched', false], ['Comedy, Drama', false], ['Any year', true], ['Any', true]];
  const q = (name, sub, t, l) => row('', name, sub, t, false, l);
  const step = (label, value, focused, last) => {
    const arrows = focused;
    const l = arrows ? svg(I.chevL, 26) : '';
    const r = arrows ? svg(I.chev, 26) : '';
    return `<div class="i one${focused ? ' on' : ''}${last ? ' nosep' : ''}"><span class="tx">${label}</span>
      <span class="fill"></span><span class="step">${l}<span>${value}</span>${r}</span><i class="sep"></i></div>`;
  };
  const list = `<div class="menu front" style="left:300px;top:196px">
    ${heading('Quick Choices')}
    ${q('Any year', '')}${q('This year', '2026 · 4 titles')}${q('Last 5 years', '2022 – 2026 · 41 titles')}
    ${q('2020s', '48 titles')}${q('2010s', '31 titles')}${q('2000s', '12 titles')}${q('1990s', '5 titles', false, true)}
    ${heading('Your Own Range')}
    ${step('From', '2010', true)}${step('To', '2026', false, true)}</div>`;
  return page(UNW) + filtersMenu(vals, 2, 'back') + list
    + '<div class="cap2">4 · Year: quick choices, or your own range with Left / Right</div>';
}

function frame5() {
  const vals = [['Unwatched', false], ['Comedy, Drama', false], ['2010 – 2026', false], ['Any', true]];
  const s = (name, colour, label, n, t, f, l) => row(brand(name, colour), label, `${n} titles`, t, f, l);
  const list = `<div class="menu front" style="left:300px;top:196px">
    ${heading('Selected')}
    ${s('netflix', '#E50914', 'Netflix', 9, true, false, true)}
    ${heading('All')}
    ${s('appletv', 'currentColor', 'Apple TV+', 3, false, true)}${s('disneyplus', '#2E6BFF', 'Disney+', 5)}
    ${s('paramountplus', '#0064FF', 'Paramount+', 2)}${s('primevideo', '#1A98FF', 'Prime Video', 6, false, false, true)}
    </div>`;
  return page(UNW) + filtersMenu(vals, 3, 'back') + list
    + '<div class="cap2">5 · Streaming services: several, ticked ones under Selected</div>';
}

function frame6() {
  const vals = [['Unwatched', false], ['Comedy, Drama', false], ['2010 – 2026', false], ['Netflix', false]];
  return page(UNW) + filtersMenu(vals, 4)
    + '<div class="cap2">6 · Back on Filters with all four set: Clear filters ready</div>';
}

function speckle() {
  return `<script>{
  const rng=function(seed){return()=>{seed|=0;seed=seed+0x6D2B79F5|0;let t=Math.imul(seed^seed>>>15,1|seed);
    t=t+Math.imul(t^t>>>7,61|t)^t;return((t^t>>>14)>>>0)/4294967296}}
  const sc=document.querySelector('.screen');const cv=document.createElement('canvas');cv.width=1920;cv.height=1080;
  sc.prepend(cv);const g=cv.getContext('2d'),r=rng(20261118),n=1920*1080*20/1000;g.fillStyle='#111215';
  g.fillRect(0,0,1920,1080);for(let i=0;i<n;i++){const x=r()*1920|0,y=r()*1080|0,a=r()<1/7?.45:.07+r()*.15;
  g.fillStyle='rgba(255,255,255,'+a+')';g.fillRect(x,y,1,1)}}</script>`;
}

const FRAMES = [
  ['35-v11-filters-menu', frame1],
  ['36-v11-filters-watched', frame2],
  ['37-v11-filters-genre', frame3],
  ['38-v11-filters-year', frame4],
  ['39-v11-filters-services', frame5],
  ['40-v11-filters-all-set', frame6],
];

async function render(frames, extraCss = '') {
  fs.mkdirSync(OUT, { recursive: true });
  const browser = await chromium.launch();
  const pg = await browser.newPage({ viewport: { width: 1920, height: 1080 }, deviceScaleFactor: 1 });
  for (const [name, fn] of frames) {
    const html = `<!doctype html><html><head><meta charset="utf-8"><style>${css}${extraCss}</style></head><body>
      <div class="screen">${fn()}<div class="border"></div></div>${speckle()}</body></html>`;
    await pg.setContent(html);
    await pg.evaluate(() => document.fonts.ready);
    await pg.screenshot({ path: path.join(OUT, `${name}.png`) });
  }
  await browser.close();
}

module.exports = { svg, I, page, filtersMenu, heading, row, icon, render, NONE, MOV, UNW, css };

if (require.main === module) {
  render(FRAMES);
}
