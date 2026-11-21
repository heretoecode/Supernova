// Details v3 (owner, 21 Nov 2026): Details on today's rules, built from the shared hero and row (RULES §14d).
// 1 an episode at rest: the shared hero with its title line, Resume and More, the tabs at the same height as Home's
// first row heading. 2 the Details tab on one screen (B2, files-review-0157-1/06 idea 2) with File and Location added
// to Technical information. 3 Extras. 4 More Like This (in your library; on your services, one row per service, the
// service's colour logo on its cards). 5 the More menu (new Fix match and Remove icons).
// Run: FONT_DIR=<dir with roboto-latin-*-normal.woff2> node render.js
const fs = require('fs');
const path = require('path');
const HV = path.join(__dirname, '../home-v3');
let src = fs.readFileSync(path.join(HV, 'render-3.js'), 'utf8');
src = src.slice(0, src.lastIndexOf('const FRAMES = ['));
src = src.replace("const { chromium } = require('playwright');", '');
src = src.split("path.join(__dirname, 'render-2.js')").join("path.join(HVDIR, 'render-2.js')");
const m = new Function('require', '__dirname', 'HVDIR', `${src}
return { css, svg, I, J, K, bar, hero, row, item, speckle };`)(require, HV, HV);
const { chromium } = require('playwright');
const { css, svg, I, J, K, bar, hero, row, item, speckle } = m;
const RES = path.join(__dirname, '../../../../res/drawable');

function logo(name, colour) {
  const xml = fs.readFileSync(path.join(RES, `provider_${name}.xml`), 'utf8');
  const vw = xml.match(/viewportWidth="([\d.]+)"/)[1];
  const vh = xml.match(/viewportHeight="([\d.]+)"/)[1];
  const p = [...xml.matchAll(/pathData="([^"]+)"/g)].map(x => `<path d="${x[1]}" fill="${colour}"/>`).join('');
  return `<svg viewBox="0 0 ${vw} ${vh}" preserveAspectRatio="xMidYMid meet">${p}</svg>`;
}

const D = {
  info: '<circle cx="12" cy="12" r="9"/><path d="M12 11v6M12 7.5v.1"/>',
  eps: '<rect x="3" y="4" width="18" height="13" rx="2"/><path d="M10 8l4 2.5-4 2.5zM8 21h8"/>',
  extras: '<rect x="3" y="7" width="18" height="13" rx="2"/><path d="M3 11h18M7 7l2-4M12 7l2-4M17 7l2-4"/>',
  like: '<path d="M12 3l1.8 5.2L19 10l-5.2 1.8L12 17l-1.8-5.2L5 10l5.2-1.8z"/>',
  star: '<path d="M12 3l2.7 5.6 6.1.9-4.4 4.3 1 6.1L12 17l-5.4 2.9 1-6.1L3.2 9.5l6.1-.9z"/>',
  list: '<path d="M8 6h13M8 12h13M8 18h13M3 6h.1M3 12h.1M3 18h.1"/>',
  chip: '<rect x="6" y="6" width="12" height="12" rx="1.5"/><path d="M9 2v4M15 2v4M9 18v4M15 18v4M2 9h4M2 15h4M18 9h4'
    + 'M18 15h4"/>',
  cast: '<circle cx="9" cy="8" r="3.5"/><path d="M2.5 20a6.5 6.5 0 0 1 13 0"/><circle cx="17" cy="9" r="2.5"/>'
    + '<path d="M16 14.2a5 5 0 0 1 5.5 5.8"/>',
  crew: '<path d="M3 11l9-7 9 7"/><path d="M5 10v10h14V10"/>',
  trophy: '<path d="M8 4h8v5a4 4 0 0 1-8 0zM5 5h3v2a3 3 0 0 1-3-2zM19 5h-3v2a3 3 0 0 0 3-2zM12 13v4M8 20h8"/>',
  trailer: '<rect x="3" y="5" width="18" height="14" rx="2"/><path d="M10 9l5 3-5 3z"/>',
  behind: '<path d="M4 7l4-3 4 3 4-3 4 3v12H4z"/><path d="M4 11h16"/>',
  lib: '<path d="M4 4h4v16H4zM10 4h4v16h-4zM16 5l3.5-.8 3 15-3.5.8z"/>',
  play: '<path d="M7 4l13 8-13 8z"/>',
};

const extra = `${css}
.tabs{position:absolute;left:72px;display:flex;gap:8px;font-size:28px;z-index:3}
.tabs span{height:52px;padding:0 22px;border-radius:20px;display:flex;align-items:center;gap:12px}
.tabs .open{box-shadow:inset 0 -3px 0 rgba(255,255,255,.95);border-radius:0}
.tabs .f{background:#f4f6fb;color:#0b0d12}
.tline2{position:absolute;left:72px;right:72px;height:2px;border-radius:1px;background:linear-gradient(90deg,
 rgba(255,255,255,.04),rgba(255,255,255,.35) 22%,rgba(255,255,255,.35) 78%,rgba(255,255,255,.04))}
.mini-logo{position:absolute;left:72px;top:84px;width:1776px;height:56px;border-radius:0 0 24px 24px;overflow:hidden;
 font-size:0;background:radial-gradient(ellipse 60% 400% at 72% -300%,rgba(150,180,200,.5),transparent 60%),
 linear-gradient(90deg,#0b1116 0%,#16242e 45%,#2c4656 75%,#1a2c38 100%);
 box-shadow:inset 1px 0 0 #fff,inset -1px 0 0 #fff,inset 0 -1px 0 #fff}
.cols3{position:absolute;left:72px;right:72px;top:262px;display:grid;grid-template-columns:1fr 1.15fr 1.15fr;gap:56px}
.ch3{display:flex;align-items:center;gap:12px;font-size:26px;font-weight:500;margin-bottom:10px}
.kv{display:flex;justify-content:space-between;align-items:center;height:38px;font-size:21px;position:relative}
.kv span:first-child{color:#c9d3e6}
.kv b{font-weight:500;white-space:nowrap;overflow:hidden;text-overflow:ellipsis;max-width:62%;text-align:right}
.kv b.q{font-weight:400;color:#8f98a8}
.kv:after{content:"";position:absolute;left:0;right:0;bottom:0;height:1px;background:linear-gradient(90deg,
 rgba(255,255,255,0),rgba(255,255,255,.16) 18%,rgba(255,255,255,.16) 82%,rgba(255,255,255,0))}
.kv.last:after{display:none}
.kv.new b{color:#fff}
.rec{display:flex;align-items:center;gap:22px;height:76px;font-size:28px;font-weight:500;position:relative}
.rec:after{content:"";position:absolute;left:0;right:0;bottom:0;height:1px;background:linear-gradient(90deg,
 rgba(255,255,255,0),rgba(255,255,255,.16) 18%,rgba(255,255,255,.16) 82%,rgba(255,255,255,0))}
.rec:last-child:after{display:none}
.rec .brand{width:96px;font-weight:700;font-size:30px;display:flex}
.rec small{font-size:20px;color:#c9d3e6;font-weight:400}
.people{position:absolute;left:72px;right:72px;top:724px;display:grid;grid-template-columns:1fr 1fr;gap:56px}
.prow{display:flex;gap:12px}
.pc{width:112px;text-align:center}
.pc .ph{width:112px;height:168px;border-radius:10px;box-shadow:inset 0 0 0 1px #fff;position:relative;overflow:hidden}
.pc .ph i{position:absolute;left:34px;top:30px;width:44px;height:44px;border-radius:22px;background:rgba(255,255,255,.25)}
.pc .ph em{position:absolute;left:16px;top:86px;width:80px;height:90px;border-radius:40px 40px 0 0;
 background:rgba(255,255,255,.2)}
.pc b{display:block;font-size:18px;font-weight:500;margin-top:6px;white-space:nowrap;overflow:hidden;
 text-overflow:ellipsis}
.pc small{font-size:15px;color:#c9d3e6;white-space:nowrap}
.svc{position:absolute;left:0;right:0;top:30%;height:56px;display:flex;justify-content:center;z-index:2}
.svc svg{height:56px;width:160px}
.rowlogo{height:30px;width:90px;display:flex}
.rowlogo svg{height:30px;width:90px}
.menu{z-index:9}
`;

const EP = {
  logo: 'Tidelands', age: '12', titleLine: 'Season 1 · Episode 3 · The Long Tide',
  facts: '2024 · 49 min · Documentary, Nature, History, Travel',
  desc: 'As the spring tides return, a family of seal hunters on the outer islands must decide whether to stay '
    + 'for one more season or follow their children to the mainland.',
};

function tabs(top, open, focus) {
  const T = [['Details', D.info], ['Episodes', D.eps], ['Extras', D.extras], ['More Like This', D.like]];
  return `<div class="tabs" style="top:${top}px">${T.map(([n, ic]) => `<span class="${n === focus ? 'f' : n === open
    ? 'open' : ''}">${svg(ic, 26)}${n}</span>`).join('')}</div><div class="tline2" style="top:${top + 58}px"></div>`;
}

function frame1() {
  return bar('TV Shows') + hero(EP, `<span class="btn f">${svg(D.play, 24)}Resume · 31 min left</span>
    <span class="btn">${svg(I.more, 26)}More</span>`) + tabs(972, 'Details')
    + '<div class="cap2">1 · An episode: the shared hero, its title line, tabs in Home\'s first-row place</div>';
}

function kv(rows) {
  return rows.map(([k, v, q, isNew], i) => `<div class="kv${i === rows.length - 1 ? ' last' : ''}${isNew ? ' new' : ''}">
    <span>${k}</span><b class="${q ? 'q' : ''}">${v}</b></div>`).join('');
}

const ARTP = [['#3f6b52', '#13221a'], ['#7a4a3a', '#26140f'], ['#4a4a7a', '#14142a'], ['#7a6a3a', '#28220f'],
  ['#2f6672', '#0f2024'], ['#6d3a5a', '#22111c'], ['#3a4f78', '#101726']];

function people(list) {
  return `<div class="prow">${list.map(([n, r], k) => `<div class="pc"><div class="ph" style="background:
    linear-gradient(160deg,${ARTP[k % 7][0]},${ARTP[k % 7][1]})"><i></i><em></em></div><b>${n}</b>
    <small>${r}</small></div>`).join('')}</div>`;
}

function frame2() {
  const rec = `<div class="rec"><span class="brand" style="color:#f5c518">IMDb</span>7.4/10<small>212K votes</small></div>
    <div class="rec"><span class="brand"><svg width="34" height="34" viewBox="0 0 24 24"><circle cx="12" cy="13" r="9" fill="#fa320a"/></svg></span>91%</div>
    <div class="rec"><span class="brand" style="color:#ffcc33">◯</span>78</div>
    <div class="rec"><span class="brand" style="color:#01b4e4">TMDB</span>7.6/10<small>3,104 votes</small></div>
    <div class="rec"><span class="brand">${svg(D.trophy, 26)}</span>2 BAFTA nominations</div>`;
  const key = kv([['Released', '14 March 2025'], ['Runtime', '2 h 11 min'], ['Genres', 'Drama, Thriller, Mystery, Crime'],
    ['Directed by', 'Anna Reyes'], ['Written by', 'Jon Feld, Sam Ortiz'], ['Studio', 'Northlight Pictures'],
    ['Country', 'Canada'], ['Language', 'English'], ['Original title', 'The Last Crossing'], ['Budget', 'Unknown', 1]]);
  const tech = kv([['Resolution', '2160p (3840 × 2160)'], ['Video', 'HEVC (H.265) · 10-bit'], ['HDR', 'HDR10'],
    ['Frame rate', '23.976 fps'], ['Audio', 'Dolby Atmos · 7.1'], ['Audio languages', 'English'],
    ['Subtitles', 'English, English SDH'], ['Size', '18.7 GB'],
    ['File', 'The.Last.Crossing.2025.2160p.mkv', 0, 1], ['Location', 'Media server › Movies › The Last Crossing', 0, 1]]);
  const cast = [['Nora Hale', 'Ellen Marsh'], ['Tomas Vey', 'Daniel Crane'], ['Ada Okafor', 'Ruth Abara'],
    ['Leon Brandt', 'Sheriff Cole'], ['Mina Sato', 'Kiko'], ['Rafe Dunmore', 'Pike'], ['Iris Penrose', 'Grace']];
  const crew = [['Anna Reyes', 'Director'], ['Jon Feld', 'Writer'], ['Sam Ortiz', 'Writer'], ['Lena Kroll', 'Producer'],
    ['Max Ibsen', 'Cinematography'], ['Ola Grey', 'Music'], ['Bea Holm', 'Editor']];
  return bar('Movies') + '<div class="mini-logo">The Last Crossing</div>'
    + tabs(170, 'Details')
    + `<div class="cols3"><div><div class="ch3">${svg(D.star, 28)}Reception</div>${rec}</div>
      <div><div class="ch3">${svg(D.list, 28)}Key information</div>${key}</div>
      <div><div class="ch3">${svg(D.chip, 28)}Technical information</div>${tech}</div></div>
      <div class="people"><div><div class="ch3">${svg(D.cast, 28)}Cast</div>${people(cast)}</div>
      <div><div class="ch3">${svg(D.crew, 28)}Crew</div>${people(crew)}</div></div>`
    + '<div class="cap2">2 · Details tab: the library line, bigger Reception and people, no Container or Revenue</div>';
}

const TRAIL = [['Official Trailer', '2 min 31 s', null], ['Teaser', '1 min 2 s', null], ['Trailer 2', '2 min 10 s', 'w']];
const BTS = [['Making The Last Crossing', '12 min', null], ['The Ferry', '6 min', null], ['Scoring the Strait', '8 min', null]];

function frame3() {
  return bar('Movies') + '<div class="mini-logo">The Last Crossing</div>'
    + tabs(170, 'Extras')
    + row(72, 262, { icon: D.trailer, title: 'Trailers' }, TRAIL, 0)
    + row(72, 600, { icon: D.behind, title: 'Behind the Scenes' }, BTS, -1, { shift: 3 })
    + '<div class="cap2">3 · Extras: the shared rows, one per kind, Trailers first</div>';
}

function serviceRow(x, y, name, colour, cards, label) {
  let h = `<div class="rowh" style="left:${x}px;top:${y}px"><span class="rowlogo">${logo(name, colour)}</span>${label}</div>`;
  const top = y + 70;
  cards.forEach((c, k) => {
    const a = ARTP[(k + 2) % 7];
    h += `<div class="card" style="left:${x + k * 440}px;top:${top}px"><div class="art" style="background:
      linear-gradient(155deg,${a[0]},${a[1]})"></div><div class="svc">${logo(name, colour)}</div><div class="fade"></div>
      <div class="ct"><div class="c1">${c[0]}</div><div class="c2">${c[1]}</div></div><div class="cedge"></div></div>`;
  });
  return h;
}

function frame4() {
  const LIB = [['Cold Iron', '2024 · 1 h 52 min', null], ['North Road', '2025 · 1 h 41 min', 'w'],
    ['Quiet Harbour', '2024 · 1 h 38 min', null], ['Salt and Stone', '2022 · 1 h 55 min', null],
    ['Low Tide', '2024 · 1 h 30 min', null]];
  const NF = [['The Tideline', '2023'], ['Breakwater', '2021'], ['Strait Lines', '2024'], ['Hollow Bay', '2022'],
    ['Ferryman\'s Daughter', '2025']];
  return bar('Movies') + '<div class="mini-logo">The Last Crossing</div>'
    + tabs(170, 'More Like This')
    + row(72, 262, { icon: D.lib, title: 'In Your Library' }, LIB, -1)
    + serviceRow(72, 600, 'netflix', '#E50914', NF, 'On Netflix')
    + '<div class="cap2">4 · More Like This: your library, then a row per service</div>';
}

function frame5() {
  const FILM = { logo: 'The Last<br>Crossing', age: '15', facts: '2025 · 2 h 11 min · Drama, Thriller, Mystery, Crime',
    desc: 'A ferryman who knows every current of the strait agrees to carry one last passenger across, and finds the '
      + 'whole town waiting on the other side.' };
  const menu = `<div class="menu" style="left:440px;top:300px">
    <div class="h">Playback</div>${item(J.restart, 'Restart movie', { nosep: true })}
    <div class="h">Library</div>${item(J.watched, 'Mark as watched', { nosep: true, on: true })}
    ${item(K.fixB, 'Fix match', { nosep: true })}${item(K.remA, 'Remove from library', { nosep: true })}
    <div class="h">Files &amp; Media</div>${item(J.subs, 'Subtitles', { nosep: true })}
    ${item(J.art, 'Artwork', { nosep: true })}${item(J.desc, 'Full description', { nosep: true })}
    ${item(J.bin, 'Delete file', { nosep: true })}<div class="rule"></div>${item(J.qr, 'TMDB page QR code', { nosep: true })}</div>`;
  return bar('Movies') + hero(FILM, `<span class="btn">${svg(D.play, 24)}Resume · 42 min left</span><span class="btn">
    ${svg(I.more, 26)}More</span>`) + tabs(972, 'Details')
    + menu + '<div class="cap2">5 · More on Details, with the new Fix match and Remove icons</div>';
}

function frame6() {
  const S1 = [['Arrival', 'S1 E1 · 52 min', 'w'], ['Low Water', 'S1 E2 · 49 min', 'w'],
    ['The Long Tide', 'S1 E3 · 31 min left', 0.4], ['Harbour Lights', 'S1 E4 · 51 min', null],
    ['Salt', 'S1 E5 · 50 min', null]];
  const S2 = [['Return', 'S2 E1 · 55 min', null], ['Driftwood', 'S2 E2 · 48 min', null],
    ['Fog', 'S2 E3 · 52 min', null], ['Wreck', 'S2 E4 · 50 min', null], ['Landfall', 'S2 E5 · 58 min', null]];
  const SEASON = '<path d="M4 6h1.5v4M4 10h3M4 14.5c0-1 2.8-1 2.8.3 0 1.2-2.8 2-2.8 3.2h3M11 7h9M11 12h9M11 17h9"/>';
  return bar('TV Shows') + '<div class="mini-logo">Tidelands</div>' + tabs(170, 'Episodes')
    + row(72, 262, { icon: SEASON, title: 'Season 1' }, S1, 2)
    + row(72, 600, { icon: SEASON, title: 'Season 2' }, S2, -1, { shift: 3 })
    + '<div class="cap2">6 · Episodes: a row per season (TV shows, seasons, episodes)</div>';
}

const FRAMES = [['01-episode-hero', frame1], ['02-details-tab', frame2], ['03-extras', frame3],
  ['04-more-like-this', frame4], ['05-more-menu', frame5], ['06-episodes', frame6]];

(async () => {
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
