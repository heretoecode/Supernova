// Tags and views, first look (owner, 15 Nov 2026; parked, for discussion). 1 Home with the view switch at the start of
// the top bar (left of Home, because it decides what Home shows), its menu open under it: Everything, Me, Alex,
// Together, then Edit views. 2 A library page's Filters with Tags: each tag toggles Off › Hide › Only, like the order
// button flips. Made-up names and titles. Light frame = picture edge. Run: FONT_DIR=<roboto woff2 dir> node render.js
const fs = require('fs');
const path = require('path');
const { chromium } = require('playwright');
const fontDir = process.env.FONT_DIR;
const font = w => `@font-face{font-family:R;font-weight:${w};src:url(data:font/woff2;base64,${fs.readFileSync(
  path.join(fontDir, `roboto-latin-${w}-normal.woff2`)).toString('base64')}) format('woff2')}`;
const svg = (d, s = 30) => `<svg width="${s}" height="${s}" viewBox="0 0 24 24" fill="none" stroke="currentColor"
  stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round">${d}</svg>`;
const I = {
  search: '<circle cx="11" cy="11" r="7"/><path d="M20 20l-4-4"/>',
  cog: '<circle cx="12" cy="12" r="3"/><path d="M12 2v3M12 19v3M4.2 4.2l2.1 2.1M17.7 17.7l2.1 2.1M2 12h3M19 12h3'
    + 'M4.2 19.8l2.1-2.1M17.7 6.3l2.1-2.1"/>',
  eye: '<path d="M2 12s3.6-7 10-7 10 7 10 7-3.6 7-10 7S2 12 2 12z"/><circle cx="12" cy="12" r="3"/>',
  all: '<rect x="3" y="3" width="7" height="7" rx="1.5"/><rect x="14" y="3" width="7" height="7" rx="1.5"/>'
    + '<rect x="3" y="14" width="7" height="7" rx="1.5"/><rect x="14" y="14" width="7" height="7" rx="1.5"/>',
  me: '<circle cx="12" cy="8" r="4"/><path d="M4 21a8 8 0 0 1 16 0"/>',
  them: '<circle cx="9" cy="8" r="3.5"/><path d="M2.5 20a6.5 6.5 0 0 1 13 0"/><path d="M17 4v6M14 7h6"/>',
  both: '<circle cx="8" cy="9" r="3"/><circle cx="16" cy="9" r="3"/><path d="M2 20a6 6 0 0 1 12 0M10 20a6 6 0 0 1 12 0"/>',
  edit: '<path d="M4 20h4L19 9l-4-4L4 16z"/><path d="M14 6l4 4"/>',
  tick: '<path d="M5 12.5l4.5 4.5L19 7.5"/>',
  down: '<path d="M6 9l6 6 6-6"/>',
  tag: '<path d="M3 12V4h8l10 10-8 8z"/><circle cx="7.5" cy="8.5" r="1.5"/>',
  star: '<path d="M12 3l2.7 5.6 6.1.9-4.4 4.3 1 6.1L12 17l-5.4 2.9 1-6.1L3.2 9.5l6.1-.9z"/>',
  filter: '<path d="M3 5h18l-7 8v6l-4 2v-8z"/>',
  watched: '<circle cx="12" cy="12" r="9"/><path d="M8 12.5l3 3 5-6"/>',
};
const nav = (chip, opts = {}) => `<div class="nav"><span class="brand">SUPERNOVA</span><span class="view ${opts.focus ?
  'focus' : ''}">${svg(I[opts.icon || 'me'], 28)}${chip}${svg(I.down, 24)}</span><span class="ni ${opts.home || ''}">Home</span>
  <span class="ni ${opts.movies || ''}">Movies</span><span class="ni">TV Shows</span><span class="ni">Documentaries</span>
  <span class="ni files">Files</span><span class="ni">${svg(I.search, 34)}</span><span class="ni">${svg(I.cog, 34)}</span>
  <span class="clock">21:40</span></div>`;
const mi = (ic, t, sub, o = {}) => `<div class="mi ${o.focus ? 'focus' : ''}">${svg(I[ic], 30)}<div class="mt"><b>${t}</b>
  ${sub ? `<small>${sub}</small>` : ''}</div><span class="end">${o.tick ? svg(I.tick, 28) : ''}</span></div>`;
const menu = `<div class="menu">${mi('all', 'Everything', 'Nothing hidden')}
  ${mi('me', 'Me', 'Hides Alex and Classics', { tick: true })}
  ${mi('them', 'Alex', 'Only Alex and Together', { focus: true })}
  ${mi('both', 'Together', 'Only Together')}<div class="sep"></div>${mi('edit', 'Edit views', '')}</div>`;
const cards = n => Array.from({ length: n }, (_, i) => `<div class="card" style="background:linear-gradient(150deg,
  ${['#4b5a4e', '#6b4a42', '#4d4766', '#6a6040', '#3f5f62'][i % 5]},#1f2228)"></div>`).join('');
const home = `<div class="frame">${nav('Me', { focus: true, home: 'dull' })}<div class="hero"><div class="hl">THE SALT ROAD</div>
  <div class="hm">2025 · 1 h 52 min · Drama</div></div><div class="rowh">Continue Watching</div><div class="row">${cards(5)}
  </div>${menu}</div>`;
const tagRow = (ic, t, state, focus) => `<div class="fr ${focus ? 'focus' : ''}">${svg(I[ic], 30)}<b>${t}</b><span class="st
  ${state.toLowerCase()}">${state}</span></div>`;
const filters = `<div class="frame">${nav('Everything', { movies: 'dull', icon: 'all' })}<div class="toolbar"><span class="tool tf">
  ${svg(I.filter, 28)}Filters</span></div><div class="grid">${cards(10)}</div>
  <div class="panel"><div class="mg"><span>Watched</span></div>${tagRow('watched', 'Watched', 'All')}
  <div class="mg"><span>Tags</span></div>${tagRow('me', 'Alex', 'Hide', true)}${tagRow('both', 'Together', 'Off')}
  ${tagRow('star', 'Classics', 'Only')}<p class="hint">OK changes Off › Hide › Only</p></div></div>`;
const css = `${font(400)}${font(500)}*{box-sizing:border-box;margin:0}
body{width:1920px;font-family:R;color:#fff;background:#16181C;position:relative}
body:after{content:"";position:absolute;inset:0;border:6px solid #8a919c;z-index:99;pointer-events:none}
.frame{width:1920px;height:1080px;position:relative;overflow:hidden;background:#16181C}.frame+.frame{border-top:8px solid
#8a919c}
.nav{position:absolute;left:56px;right:56px;top:24px;height:76px;display:flex;align-items:center;gap:14px;
font-size:33px;color:#8f98a8;z-index:5}.nav .brand{color:#f1ead8;letter-spacing:2px;margin-right:24px;font-weight:500}
.view{display:inline-flex;align-items:center;gap:10px;height:56px;padding:0 18px;border-radius:28px;font-size:29px;
color:#fff;box-shadow:inset 0 0 0 2px rgba(255,255,255,.28);margin-right:18px}.view svg{color:#C9D3E6}
.view.focus{background:#F4F6FB;color:#0B0D12;box-shadow:none}.view.focus svg{color:#0B0D12}
.ni{position:relative;display:inline-flex;align-items:center;height:60px;padding:0 18px}.ni.files{margin-left:auto}
.nav .clock{margin-left:18px;color:#C9D3E6}.dull{color:#C9D3E6}.dull:after{content:'';position:absolute;left:18px;
right:18px;bottom:-2px;height:4px;border-radius:2px;background:rgba(255,255,255,.3)}
.hero{position:absolute;left:72px;top:118px;width:1776px;height:640px;border-radius:26px;background:linear-gradient(100deg,
rgba(14,16,20,.9),rgba(14,16,20,.2) 60%),radial-gradient(ellipse at 72% 45%,#7a6a52,#2a2620 75%);box-shadow:inset 0 0 0
2px rgba(255,255,255,.6)}.hl{position:absolute;left:60px;top:260px;font-size:76px;letter-spacing:8px;font-weight:500;
color:#f1ead8}.hm{position:absolute;left:62px;top:360px;font-size:28px;color:#e8edf6}
.rowh{position:absolute;left:96px;top:800px;font-size:30px;font-weight:500}.row{position:absolute;left:96px;top:852px;
display:flex;gap:24px}.card{width:400px;height:225px;border-radius:14px;box-shadow:inset 0 0 0 2px rgba(255,255,255,.6)}
.menu{position:absolute;left:300px;top:96px;width:max-content;padding:14px;border-radius:24px;background:rgba(22,24,28,
.95);box-shadow:0 0 0 2px rgba(255,255,255,.14),0 30px 80px rgba(0,0,0,.7);z-index:6}
.mi{display:flex;align-items:center;gap:20px;height:88px;padding:0 24px;border-radius:18px;min-width:520px}
.mi svg{color:#C9D3E6;flex:none}.mt b{display:block;font-size:29px;font-weight:500}.mt small{font-size:22px;color:#C9D3E6}
.mi .end{margin-left:auto;padding-left:24px}.mi.focus{background:#F4F6FB;color:#0B0D12}.mi.focus svg,.mi.focus small{
color:#0B0D12}.sep{height:2px;background:rgba(255,255,255,.12);margin:8px 24px}
.toolbar{position:absolute;left:96px;top:128px}.tool{display:inline-flex;align-items:center;gap:10px;font-size:27px;
padding:10px 18px;border-radius:14px}.tf{background:rgba(255,255,255,.14)}
.grid{position:absolute;left:96px;top:210px;display:grid;grid-template-columns:repeat(5,250px);gap:30px}
.grid .card{width:250px;height:375px;opacity:.5}
.panel{position:absolute;left:96px;top:196px;width:640px;padding:18px 18px 22px;border-radius:24px;background:rgba(22,24,
28,.95);box-shadow:0 0 0 2px rgba(255,255,255,.14),0 30px 80px rgba(0,0,0,.7)}
.mg{display:flex;align-items:center;gap:18px;margin:14px 24px 8px;font-size:26px;font-weight:500}.mg:before,.mg:after{
content:'';flex:1;height:2px;background:rgba(255,255,255,.16)}
.fr{display:flex;align-items:center;gap:20px;height:84px;padding:0 24px;border-radius:18px;font-size:29px}
.fr svg{color:#C9D3E6}.fr b{font-weight:500}.fr.focus{background:#F4F6FB;color:#0B0D12}.fr.focus svg{color:#0B0D12}
.st{margin-left:auto;font-size:25px;padding:5px 16px;border-radius:14px;box-shadow:inset 0 0 0 2px rgba(255,255,255,.35)}
.st.off,.st.all{box-shadow:none;color:#C9D3E6;padding:5px 0}.fr.focus .st{box-shadow:inset 0 0 0 2px rgba(11,13,18,.4)}
.hint{font-size:22px;color:#9aa3b4;margin:12px 24px 0}`;
(async () => {
  const browser = await chromium.launch();
  const tab = await browser.newPage({ viewport: { width: 1920, height: 2168 } });
  await tab.setContent(`<html><head><style>${css}</style></head><body>${home}${filters}</body></html>`,
    { waitUntil: 'load' });
  await tab.evaluate(() => document.fonts.ready);
  await tab.screenshot({ path: path.join(__dirname, '01-view-switch-and-tag-filters.png') });
  await browser.close();
})();
