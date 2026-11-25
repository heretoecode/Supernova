// Search v6 (25 Nov 2026): Search (search-flat v5, approved 12 Nov) redrawn on today's rules: the speckle, the 0.158
// top bar (Search's grey box while focus is on the page), the one side margin (52 px), the one small-box shape for
// pills (52 px, 20 px corners, outline = information), the 1 px white artwork edge, the corner badge (RULES §14c),
// Movies / TV Shows wording, the keyboard's Phone key, scroll arrows in the right margin, the Undo notice, the
// library long-press menu, the loading ring, no lone word on a line. 1920 x 1080 for the TV. Made-up titles.
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
  search: '<circle cx="11" cy="11" r="7"/><path d="M20 20l-4-4"/>',
  mic: '<rect x="9" y="3" width="6" height="11" rx="3"/><path d="M5 11a7 7 0 0 0 14 0M12 18v3"/>',
  phone: '<rect x="7" y="2.5" width="10" height="19" rx="2"/><path d="M11 18.5h2"/>',
  del: '<path d="M9 5h11a1 1 0 0 1 1 1v12a1 1 0 0 1-1 1H9l-6-7z"/><path d="M12 9.5l5 5M17 9.5l-5 5"/>',
  shift: '<path d="M12 4l8 8h-4.5v7h-7v-7H4z"/>',
  clock: '<circle cx="12" cy="12" r="9"/><path d="M12 7v5l3 2"/>',
  film: '<rect x="3" y="4" width="18" height="16" rx="2"/><path d="M7 4v16M17 4v16M3 9h4M3 15h4M17 9h4M17 15h4"/>',
  person: '<circle cx="12" cy="8" r="4"/><path d="M4 21a8 8 0 0 1 16 0"/>',
  tag: '<path d="M3 12V4h8l10 10-8 8z"/><circle cx="7.5" cy="8" r="1.5"/>',
  cal: '<rect x="3" y="5" width="18" height="16" rx="2"/><path d="M3 10h18M8 3v4M16 3v4"/>',
  cloud: '<path d="M7 18h10a4 4 0 0 0 .6-8A6 6 0 0 0 6 9.5 4.3 4.3 0 0 0 7 18z"/>',
  x: '<path d="M6 6l12 12M18 6L6 18"/>',
  ext: '<path d="M14 4h6v6M20 4l-9 9"/><path d="M18 14v5a1 1 0 0 1-1 1H5a1 1 0 0 1-1-1V7a1 1 0 0 1 1-1h5"/>',
  lib: '<rect x="3" y="4" width="4" height="16" rx="1"/><rect x="9" y="4" width="4" height="16" rx="1"/>'
    + '<path d="M15.5 5.2l3.8-1 3.2 15.4-3.8 1z"/>',
  bulb: '<path d="M9 18h6M10 21h4M12 3a6 6 0 0 0-3.5 10.9c.6.5 1 1.2 1 2.1h5c0-.9.4-1.6 1-2.1A6 6 0 0 0 12 3z"/>',
  play: '<circle cx="12" cy="12" r="9"/><path d="M10 8.5v7l5.5-3.5z"/>',
  play2: '<path d="M7 4l13 8-13 8z"/>',
  restart: '<path d="M4 12a8 8 0 1 0 2.3-5.7"/><path d="M4 4v4h4"/>',
  tick: '<circle cx="12" cy="12" r="9"/><path d="M8 12.5l2.8 2.8L16.5 9.5"/>',
  unwatch: '<circle cx="12" cy="12" r="9"/><path d="M8 12.5l2.8 2.8L16.5 9.5"/><path d="M4 4l16 16"/>',
  fix: '<path d="M14.5 5.5l4 4L8 20H4v-4z"/><path d="M12.5 7.5l4 4"/>',
  remove: '<circle cx="12" cy="12" r="9"/><path d="M8 12h8"/>',
  trash: '<path d="M4 7h16M10 11v6M14 11v6M6 7l1 13h10l1-13M9 7V4h6v3"/>',
  down: '<path d="M6 9l6 6 6-6"/>',
  up: '<path d="M6 15l6-6 6 6"/>',
  ring: '<circle cx="12" cy="12" r="9"/>',
  nothing: '<circle cx="11" cy="11" r="7"/><path d="M20 20l-4-4M8.5 11h5"/>',
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
.cap2{position:absolute;left:52px;bottom:24px;color:#e8ebf2;font-size:24px;z-index:41;background:rgba(17,18,21,.85);
 padding:4px 12px;border-radius:8px}
.left{position:absolute;left:52px;top:132px;width:760px}
.right{position:absolute;left:884px;right:110px;top:132px}
.field{height:84px;border-radius:20px;background:#24272d;display:flex;align-items:center;gap:18px;padding:0 28px;
 font-size:36px}
.field .ph{color:#8a93a3}
.field .ph.live{color:#fff}
.field svg{color:#c9d3e6;flex:none}
.caret{display:inline-block;width:3px;height:42px;background:#fff;margin-left:-16px}
.field b{font-weight:700}
.kb{display:flex;flex-direction:column;gap:12px;margin-top:22px}
.krow{display:flex;gap:10px}
.key{flex:1;height:64px;border-radius:16px;background:#24272d;display:flex;align-items:center;justify-content:center;
 font-size:30px;font-weight:500}
.key.wide{flex:1.35;font-size:24px}
.key.space{flex:2.6}
.key.focus{background:#f4f6fb;color:#0b0d12}
.hd{display:flex;align-items:center;gap:16px;font-size:32px;font-weight:500;height:44px;margin:64px 0 26px}
.hd:first-child{margin-top:0}
.hd svg{color:#c9d3e6;flex:none}
.clr{margin-left:auto;display:flex;align-items:center;gap:10px;height:52px;padding:0 22px;border-radius:20px;
 font-size:24px;font-weight:400;color:#c9d3e6}
.clr.f{background:#f4f6fb;color:#0b0d12}
.clr.f svg{color:#0b0d12}
.rec{position:relative;display:flex;align-items:center;gap:20px;height:72px;padding:0 24px;font-size:30px;
 border-radius:14px}
.rec svg{color:#c9d3e6}
.ln{position:absolute;left:20px;right:20px;bottom:0;height:1px;background:linear-gradient(90deg,
 rgba(255,255,255,0),rgba(255,255,255,.14) 15%,rgba(255,255,255,.14) 85%,rgba(255,255,255,0))}
.last .ln,.on .ln{display:none}
.tip{display:flex;align-items:center;gap:18px;height:60px;padding:0 24px;font-size:26px;color:#c9d3e6}
.tip svg{flex:none}
.tip b{color:#fff;font-weight:500;width:200px;flex:none}
.res{position:relative;display:flex;align-items:center;gap:24px;height:96px;padding:0 20px;border-radius:14px}
.res.on{background:#f4f6fb;color:#0b0d12}
.thumb{position:relative;width:128px;height:72px;border-radius:8px;flex:none;
 box-shadow:inset 0 0 0 1px #fff,inset 0 0 12px 3px rgba(0,0,0,.45)}
.res.on .thumb{box-shadow:inset 0 0 12px 3px rgba(0,0,0,.45)}
.mini{position:absolute;top:4px;right:4px;width:30px;height:30px;border-radius:50%;background:#fff;
 box-shadow:0 1px 4px rgba(0,0,0,.5)}
.mini svg{position:absolute;left:0;top:0}
.rt{flex:1;min-width:0}
.rn{font-size:32px;white-space:nowrap;overflow:hidden;text-overflow:ellipsis}
.rn b{font-weight:700}
.rl{font-size:24px;color:#c9d3e6;margin-top:4px;white-space:nowrap}
.res.on .rl{color:#3a4256}
.pill{flex:none;width:250px;height:52px;border-radius:20px;display:flex;align-items:center;justify-content:center;
 gap:10px;font-size:24px;box-shadow:inset 0 0 0 1.5px rgba(255,255,255,.4)}
.pill.none{box-shadow:none}
.res.on .pill{box-shadow:inset 0 0 0 1.5px rgba(11,13,18,.45)}
.res.on .pill.none{box-shadow:none}
.arrow{position:absolute;right:56px;color:#fff}
.arrow.faint{color:rgba(255,255,255,.28)}
.load{display:flex;align-items:center;gap:18px;height:96px;padding:0 20px;font-size:26px;color:#c9d3e6}
.empty{position:absolute;left:884px;right:52px;top:300px;display:flex;flex-direction:column;align-items:center;
 text-align:center}
.empty svg{color:#c9d3e6;margin-bottom:24px}
.empty .et{font-size:34px;font-weight:500}
.empty .ed{font-size:26px;color:#c9d3e6;margin-top:14px;line-height:38px}
.menu{position:absolute;z-index:12;padding:12px 0;border-radius:20px;background:rgba(22,24,28,.99);
 box-shadow:0 0 0 1.5px rgba(255,255,255,.7),0 18px 48px 12px rgba(0,0,0,.85)}
.menu .g{display:flex;align-items:center;gap:14px;padding:10px 24px 6px;font-size:22px;color:#c9d3e6;white-space:nowrap}
.menu .g:before,.menu .g:after{content:'';flex:1;height:1px;background:rgba(255,255,255,.25)}
.menu .it{display:flex;align-items:center;gap:16px;height:60px;margin:0 10px;padding:0 16px;border-radius:14px;
 font-size:26px;white-space:nowrap}
.menu .it small{font-size:22px;color:#c9d3e6;margin-left:4px}
.menu .it.f{background:#f4f6fb;color:#0b0d12}
.menu .it.f small{color:#3a4256}
.toast{position:absolute;left:50%;bottom:44px;transform:translateX(-50%);display:flex;align-items:center;gap:22px;
 padding:20px 20px 30px 26px;border-radius:20px;background:rgba(22,24,28,.99);z-index:12;
 box-shadow:0 0 0 1.5px rgba(255,255,255,.7),0 18px 48px 12px rgba(0,0,0,.85);white-space:nowrap;font-size:26px}
.toast .u{height:52px;padding:0 24px;border-radius:20px;display:flex;align-items:center;font-size:24px}
.toast .u.f{background:#f4f6fb;color:#0b0d12}
.toast .bar6{position:absolute;left:24px;right:24px;bottom:10px;height:3px;border-radius:2px;
 background:rgba(255,255,255,.18)}
.toast .bar6 b{position:absolute;left:0;top:0;bottom:0;width:70%;border-radius:2px;background:rgba(255,255,255,.7)}
.pulse{position:relative;width:40px;height:40px;flex:none;display:flex;align-items:center;justify-content:center}
.pulse:before,.pulse:after{content:'';position:absolute;border-radius:50%;border:2px solid rgba(255,255,255,.35)}
.pulse:before{inset:-8px}
.pulse:after{inset:-13px;border-color:rgba(255,255,255,.15)}
.ab{position:absolute;left:52px;right:52px;top:132px;display:grid;grid-template-columns:1fr 1fr;gap:72px}
.ab .lab{font-size:28px;font-weight:500;margin-bottom:26px}
.ab .thumb2{position:relative;width:128px;height:72px;border-radius:8px;flex:none;
 box-shadow:inset 0 0 0 1px #fff,inset 0 0 12px 3px rgba(0,0,0,.45)}
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

// the loading ring at small size: the mark (two open rings), strokes drawn thicker
function ring(s = 36) {
  return `<svg width="${s}" height="${s}" viewBox="0 0 36 36" fill="none" stroke="#fff" stroke-width="3.2"
    stroke-linecap="round"><path d="M18 3a15 15 0 1 1-13.5 8.4" transform="rotate(30 18 18)"/>
    <path d="M18 9.5a8.5 8.5 0 1 1-7.6 4.7" transform="rotate(210 18 18)"/></svg>`;
}

function bar() {
  return `<div class="bar"><span>Home</span><span>Movies</span><span>TV Shows</span><span>Documentaries</span>
    <span class="sp"></span><span>Files</span><span class="open">Search</span><span>Settings</span><span>18:40</span></div>`;
}

const KEYS = [['1', '2', '3', '4', '5', '6', '7', '8', '9', '0'], ['q', 'w', 'e', 'r', 't', 'y', 'u', 'i', 'o', 'p'],
  ['a', 's', 'd', 'f', 'g', 'h', 'j', 'k', 'l', '/'], ['shift', 'z', 'x', 'c', 'v', 'b', 'n', 'm', '.', '@'],
  ['#+=', 'mic', 'Space', 'del', 'Clear']];
const KEY_ICON = { shift: I.shift, mic: I.mic, del: I.del, phone: I.phone };

function kb(focus) {
  return `<div class="kb">${KEYS.map((r, ri) => `<div class="krow">${r.map(k => {
    const cls = ['key'];
    if (ri === 4) {
      cls.push(k === 'Space' ? 'space' : 'wide');
    }
    if (k === focus) {
      cls.push('focus');
    }
    const label = KEY_ICON[k] ? svg(KEY_ICON[k], 30) : k;
    return `<span class="${cls.join(' ')}">${label}</span>`;
  }).join('')}</div>`).join('')}</div>`;
}

function field(text, opts = {}) {
  if (opts.listening) {
    return `<div class="field"><span class="pulse">${svg(I.mic, 32)}</span><span class="ph live">Listening…</span></div>`;
  }
  const inner = text ? `<span>${text}</span><i class="caret"></i>` : '<span class="ph">Movies, TV shows, people…</span>';
  return `<div class="field">${svg(I.search, 32)}${inner}</div>`;
}

const ART = ['#5b3a2e', '#2e4a5b', '#4b5b2e', '#5b2e4a', '#2e5b4f', '#3a2e5b', '#5b512e'];

function pie(p) {
  const a = 2 * Math.PI * p - Math.PI / 2;
  const x = 15 + 13 * Math.cos(a);
  const y = 15 + 13 * Math.sin(a);
  return `<path d="M15 15 L15 2 A13 13 0 ${p > 0.5 ? 1 : 0} 1 ${x} ${y} Z" fill="#000"/>`;
}

function mini(st) {
  if (st == null) {
    return '';
  }
  const m = st === 'w' ? '<path d="M9.5 15.5l3.8 3.8 7.5-8.3" fill="none" stroke="#000" stroke-width="2.6" '
    + 'stroke-linecap="round" stroke-linejoin="round"/>' : pie(st);
  return `<span class="mini"><svg width="30" height="30" viewBox="0 0 30 30">${m}</svg></span>`;
}

// r: {i, name, line, pill: [icon, text] | null, state, on, last}
function res(r, opts = {}) {
  const cls = ['res'];
  if (r.on) {
    cls.push('on');
  }
  if (r.last) {
    cls.push('last');
  }
  const badge = opts.noBadge ? '' : mini(r.state);
  const pill = r.pill ? `<span class="pill">${svg(I[r.pill[0]], 24)}${r.pill[1]}</span>`
    : '<span class="pill none"></span>';
  return `<div class="${cls.join(' ')}"><span class="thumb" style="background:linear-gradient(160deg,
    ${ART[r.i % 7]},#121418)">${badge}</span><div class="rt"><div class="rn">${r.name}</div><div class="rl">${r.line}
    </div></div>${pill}<div class="ln"></div></div>`;
}

function hd(icon, title, extra = '') {
  return `<div class="hd">${svg(I[icon], 32)}${title}${extra}</div>`;
}

function bold(t, n) {
  return `<b>${t.slice(0, n)}</b>${t.slice(n)}`;
}

function page(left, right) {
  return `${bar()}<div class="left">${left}</div><div class="right">${right}</div>`;
}

function tips(withServices) {
  let h = hd('bulb', 'What you can search');
  h += `<div class="tip">${svg(I.film, 28)}<b>Titles</b><span>movies and TV shows in your library</span></div>`;
  h += `<div class="tip">${svg(I.person, 28)}<b>People</b><span>actors, directors and writers</span></div>`;
  h += `<div class="tip">${svg(I.tag, 28)}<b>Genres</b><span>e.g. "comedy" or "crime drama"</span></div>`;
  h += `<div class="tip">${svg(I.cal, 28)}<b>Years</b><span>e.g. "1994" or "1990s"</span></div>`;
  if (withServices) {
    h += `<div class="tip">${svg(I.cloud, 28)}<b>Your services</b><span>related titles you can stream</span></div>`;
  }
  return h;
}

function recent(clearFocus) {
  const words = ['Tidelands', 'Cold Iron', 'Mara Ellison', 'Christmas movies'];
  const clr = `<span class="clr${clearFocus ? ' f' : ''}">${svg(I.x, 22)}Clear history</span>`;
  return hd('clock', 'Recent searches', clr) + words.map((w, k) => `<div class="rec${k === 3 ? ' last' : ''}">
    ${svg(I.search, 28)}<span>${w}</span><div class="ln"></div></div>`).join('');
}

// 01 before typing
function f01() {
  return page(field('') + kb('t'), recent(false) + tips(true)) + '<div class="cap2">1 · Before typing</div>';
}

// 02 a show and its world
function f02() {
  const right = hd('lib', 'In your library')
    + res({ i: 0, name: bold('Tidelands', 8), line: '2019 · TV show · 4 seasons', pill: ['play', 'Resume S2 E3'],
      state: 0.4, on: true, last: true })
    + hd('cloud', 'Stream more from the world of Tidelands')
    + res({ i: 1, name: 'Tidelands: 1899', line: '2022 · TV show · prequel', pill: ['ext', 'Paramount+'] })
    + res({ i: 2, name: 'The Keeper of Gull Rock', line: '2024 · Movie · prequel', pill: ['ext', 'Paramount+'] })
    + res({ i: 3, name: 'Tidelands: North', line: '2026 · TV show · spin-off', pill: ['ext', 'Prime Video'],
      last: true });
  return page(field('tideland') + kb(''), right) + '<div class="cap2">2 · A show and its world</div>';
}

// 03 a person, more than fits, Menu on a library title
function f03() {
  const right = hd('lib', 'In your library')
    + res({ i: 0, name: 'Tidelands', line: 'Mara Ellison · creator', pill: ['play', 'Resume S2 E3'], state: 0.4 })
    + res({ i: 3, name: 'Cold Iron', line: 'Mara Ellison · director', pill: null, state: 'w', on: true })
    + res({ i: 4, name: 'The Salt Road', line: 'Mara Ellison · writer', pill: null })
    + res({ i: 5, name: 'Northern Lights', line: 'Mara Ellison · actor', pill: null, state: 0.7 })
    + res({ i: 6, name: 'Glass Bay', line: 'Mara Ellison · executive producer', pill: null, last: true })
    + hd('cloud', 'Stream more from Mara Ellison')
    + res({ i: 1, name: 'Tidelands: 1899', line: '2022 · TV show · creator', pill: ['ext', 'Paramount+'] })
    + res({ i: 2, name: 'Harbour Wives', line: '2020 · TV show · writer', pill: ['ext', 'Netflix'] });
  const menu = `<div class="menu" style="right:1056px;top:330px">
    <div class="g">Playback</div>
    <div class="it">${svg(I.play2, 26)}Play</div>
    <div class="g">Library</div>
    <div class="it f">${svg(I.unwatch, 26)}Mark as unwatched</div>
    <div class="it">${svg(I.fix, 26)}Fix match</div>
    <div class="it">${svg(I.remove, 26)}Remove from library</div>
    <div class="g">Files &amp; Media</div>
    <div class="it">${svg(I.trash, 26)}Delete file</div></div>`;
  const arrows = `<div class="arrow faint" style="top:236px">${svg(I.up, 32)}</div>
    <div class="arrow" style="top:960px">${svg(I.down, 32)}</div>`;
  return page(field('mara ellison') + kb(''), right) + menu + arrows
    + '<div class="cap2">3 · A person, more than fits · Menu on a library title</div>';
}

// 04 streaming still being fetched
function f04() {
  const right = hd('lib', 'In your library')
    + res({ i: 4, name: `Our Blue ${bold('Planet', 6)} II`, line: '2017 · TV show · 7 episodes', pill: null,
      state: 'w', on: true, last: true })
    + hd('cloud', 'Stream more related to Our Blue Planet II')
    + `<div class="load">${ring(36)}Looking on your services</div>`;
  return page(field('planet') + kb(''), right) + '<div class="cap2">4 · Streaming still being fetched</div>';
}

// 05 after Clear history
function f05() {
  const toast = `<div class="toast">${svg(I.clock, 32)}<span>Recent searches cleared</span>
    <span class="u f">Understood</span><span class="u">Undo</span><span class="bar6"><b></b></span></div>`;
  const right = genres(8) + `<div style="margin-top:64px">${people()}</div>`;
  return page(field('') + kb('') + leftTips(), right) + toast + '<div class="cap2">5 · After Clear history</div>';
}

// 06 nothing in the library
function f06() {
  const empty = `<div class="empty">${svg(I.nothing, 64)}<div class="et">Nothing in your library matches
    "terminus"</div><div class="ed">Search looks only in your library.<br>Titles you can stream show up under what it
    finds.</div></div>`;
  return `${bar()}<div class="left">${field('terminus') + kb('s')}</div>${empty}`
    + '<div class="cap2">6 · Nothing in the library</div>';
}

// 07 the Voice key
function f07() {
  const right = recent3() + `<div style="margin-top:64px">${genres(4)}</div><div style="margin-top:64px">${people()}</div>`;
  return page(field('', { listening: true }) + kb('mic') + leftTips(), right)
    + '<div class="cap2">7 · Voice key pressed: listening</div>';
}

// 08 badge choice: A badge on the thumbnail, words only where they add something; B pills only
function f08() {
  const rowsA = [
    { i: 0, name: 'Tidelands', line: 'Mara Ellison · creator', pill: ['play', 'Resume S2 E3'], state: 0.4 },
    { i: 3, name: 'Cold Iron', line: 'Mara Ellison · director', pill: null, state: 'w' },
    { i: 4, name: 'The Salt Road', line: 'Mara Ellison · writer', pill: null, last: true },
  ];
  const rowsB = [
    { i: 0, name: 'Tidelands', line: 'Mara Ellison · creator', pill: ['play', 'Resume S2 E3'] },
    { i: 3, name: 'Cold Iron', line: 'Mara Ellison · director', pill: ['tick', 'Watched'] },
    { i: 4, name: 'The Salt Road', line: 'Mara Ellison · writer', pill: ['ring', 'Unwatched'], last: true },
  ];
  const colA = `<div><div class="lab">A</div>${hd('lib', 'In your library')}${rowsA.map(r => res(r)).join('')}</div>`;
  const colB = `<div><div class="lab">B</div>${hd('lib', 'In your library')}
    ${rowsB.map(r => res(r, { noBadge: true })).join('')}</div>`;
  return `${bar()}<div class="ab">${colA}${colB}</div><div class="cap2">8 · Progress on results: A or B</div>`;
}

// ---- round 2 (owner, 25 Nov): the landing page looked empty. Tips move under the keyboard (RULES §1: help under
// the keyboard); the right holds Recent searches, then Genres and People from the library (artwork cards, OK fills
// the field and shows the results).
const GEN = [['Crime', 42, '#6b2f2f'], ['Drama', 118, '#2f4f6b'], ['Documentary', 37, '#2f6b55'],
  ['Comedy', 51, '#6b5a2f'], ['Thriller', 33, '#4a2f6b'], ['Mystery', 19, '#2f5f6b'], ['Science Fiction', 24, '#3a3f78'],
  ['History', 16, '#6b4a2f']];
const PEOPLE = [['Mara Ellison', 6], ['Tom Oyelaran', 5], ['Ruth Kane', 5], ['Ilse Berg', 4], ['Kai Nolan', 4],
  ['Pia Sund', 3]];

const css3 = `
.ltips{margin-top:64px}
.ltips .hd{margin-top:0}
.ltips .tip{height:50px;font-size:24px;padding:0 4px}
.ltips .tip b{width:170px}
.gcards{display:grid;grid-template-columns:repeat(4,214px);gap:24px}
.gc{position:relative;width:214px;height:120px;border-radius:12px;overflow:hidden}
.gc .e{position:absolute;inset:0;border-radius:12px;box-shadow:inset 0 0 0 1px #fff,inset 0 0 14px rgba(0,0,0,.4)}
.gc .f{position:absolute;left:0;right:0;bottom:0;height:70%;background:linear-gradient(0deg,rgba(8,9,11,.9),
 rgba(8,9,11,0))}
.gc b{position:absolute;left:14px;bottom:30px;font-size:24px;font-weight:500;white-space:nowrap}
.gc small{position:absolute;left:14px;bottom:8px;font-size:19px;color:#c9d3e6}
.pcards{display:flex;gap:24px}
.pcard{width:112px;text-align:center}
.pcard .ph{position:relative;width:112px;height:168px;border-radius:10px;overflow:hidden;
 box-shadow:inset 0 0 0 1px #fff}
.pcard .ph i{position:absolute;left:31px;top:30px;width:50px;height:50px;border-radius:50%;background:rgba(255,255,255,.3)}
.pcard .ph em{position:absolute;left:16px;top:92px;width:80px;height:90px;border-radius:40px 40px 0 0;
 background:rgba(255,255,255,.3)}
.pcard b{display:block;font-size:20px;font-weight:500;margin-top:8px;white-space:nowrap}
.pcard small{font-size:17px;color:#c9d3e6}
`;

function genres(n) {
  return hd('tag', 'Genres in your library') + `<div class="gcards">${GEN.slice(0, n).map(([g, c, a]) => `<div class="gc"
    style="background:radial-gradient(ellipse 70% 70% at 60% 30%,rgba(255,255,255,.18),transparent 60%),
    linear-gradient(155deg,${a},#101215)"><div class="f"></div><b>${g}</b><small>${c} titles</small><div class="e"></div>
    </div>`).join('')}</div>`;
}

function people() {
  const ART = ['#3f6b52', '#7a4a3a', '#4a4a7a', '#7a6a3a', '#2f6672', '#6d3a5a'];
  return hd('person', 'People in your library') + `<div class="pcards">${PEOPLE.map(([n, c], k) => `<div class="pcard">
    <div class="ph" style="background:linear-gradient(160deg,${ART[k]},#14161a)"><i></i><em></em></div><b>${n}</b>
    <small>${c} titles</small></div>`).join('')}</div>`;
}

function leftTips() {
  return `<div class="ltips">${tips(true)}</div>`;
}

function recent3() {
  const words = ['Tidelands', 'Cold Iron', 'Mara Ellison'];
  const clr = `<span class="clr">${svg(I.x, 22)}Clear history</span>`;
  return hd('clock', 'Recent searches', clr) + words.map((w, k) => `<div class="rec${k === 2 ? ' last' : ''}">
    ${svg(I.search, 28)}<span>${w}</span><div class="ln"></div></div>`).join('');
}

// 09 landing with recent searches
function f09() {
  const right = recent3() + `<div style="margin-top:64px">${genres(4)}</div><div style="margin-top:64px">${people()}</div>`;
  return page(field('') + kb('t') + leftTips(), right) + '<div class="cap2">9 · Landing, with recent searches</div>';
}

// 10 landing with no history (first visit, or search history off in Settings)
function f10() {
  const right = genres(8) + `<div style="margin-top:64px">${people()}</div>`;
  return page(field('') + kb('t') + leftTips(), right)
    + '<div class="cap2">10 · Landing, no history (or search history off)</div>';
}

// 11 OK on a genre card: the field fills and the results show, as if typed
function f11() {
  const right = hd('lib', 'In your library')
    + res({ i: 0, name: 'Tidelands', line: '2024 · TV show · 2 seasons', pill: ['play', 'Resume S2 E3'], state: 0.4 })
    + res({ i: 3, name: 'Cold Iron', line: '2025 · Movie · 1 h 52 min', pill: null, state: 'w', on: true })
    + res({ i: 4, name: 'The Salt Road', line: '2023 · Movie · 2 h 4 min', pill: null })
    + res({ i: 5, name: 'Harbour Wives', line: '2020 · TV show · 3 seasons', pill: null, state: 0.7 })
    + res({ i: 6, name: 'Glass Bay', line: '2022 · TV show · 1 season', pill: null })
    + res({ i: 1, name: 'Northern Lights', line: '2021 · Movie · 1 h 47 min', pill: null })
    + res({ i: 2, name: 'Low Tide', line: '2021 · Movie · 1 h 30 min', pill: null, last: true });
  const arrows = `<div class="arrow faint" style="top:236px">${svg(I.up, 32)}</div>
    <div class="arrow" style="top:960px">${svg(I.down, 32)}</div>`;
  return page(field('Crime') + kb(''), right) + arrows + '<div class="cap2">11 · OK on Crime: its titles, as if typed</div>';
}

const FRAMES = [['01-start', f01], ['02-show-and-world', f02], ['03-person-menu', f03], ['04-streaming-loading', f04],
  ['05-clear-history-undo', f05], ['06-nothing-in-library', f06], ['07-voice', f07], ['08-badge-a-or-b', f08],
  ['09-landing-recent', f09], ['10-landing-no-history', f10], ['11-genre-results', f11]];

(async () => {
  fs.mkdirSync(OUT, { recursive: true });
  const browser = await chromium.launch();
  const pg = await browser.newPage({ viewport: { width: 1920, height: 1080 }, deviceScaleFactor: 1 });
  for (const [name, fn] of FRAMES) {
    const html = `<!doctype html><html><head><meta charset="utf-8"><style>${css}${css3}</style></head><body>
      <div class="screen">${fn()}<div class="border"></div></div>${speckle()}</body></html>`;
    await pg.setContent(html);
    await pg.evaluate(() => document.fonts.ready);
    await pg.screenshot({ path: path.join(OUT, `${name}.png`) });
  }
  await browser.close();
})();
