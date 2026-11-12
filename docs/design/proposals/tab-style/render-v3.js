// Focus brightness scale (owner, 12 Nov 2026): white box = where the remote is (artwork grows instead); bright
// underline = one step back; dull underline = two steps back. Top bar and tabs alike.
// v3b: anything ahead of the remote gets the dull underline; hero focus = its first button boxed; focused
// artwork grows with a solid white ring (resting artwork keeps the thin soft edge).
// Run: FONT_DIR=<roboto woff2 dir> node render-v3.js
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
  play: '<path d="M7 4l13 8-13 8z"/>',
  audio: '<path d="M4 9v6h4l5 4V5L8 9z"/><path d="M16 9a4 4 0 0 1 0 6"/>',
  lib: '<rect x="3" y="4" width="5" height="16" rx="1"/><rect x="10" y="4" width="5" height="16" rx="1"/>'
    + '<path d="M17 5l4 14"/>',
  user: '<circle cx="12" cy="8" r="4"/><path d="M4 21a8 8 0 0 1 16 0"/>',
  sys: '<rect x="3" y="5" width="18" height="12" rx="2"/><path d="M8 21h8"/>',
  sync: '<path d="M4 12a8 8 0 0 1 14-5.3M20 12a8 8 0 0 1-14 5.3"/><path d="M18 3v4h-4M6 21v-4h4"/>',
  cloud: '<path d="M7 18h10a4 4 0 0 0 .6-8A6 6 0 0 0 6 9.5 4.3 4.3 0 0 0 7 18z"/>',
  subs: '<rect x="3" y="5" width="18" height="14" rx="2"/><path d="M7 14h4M13 14h4M7 10h10"/>',
  eps: '<rect x="3" y="4" width="18" height="12" rx="2"/><path d="M7 20h10"/><path d="M10 8l5 2.5-5 2.5z"/>',
  info: '<circle cx="12" cy="12" r="9"/><path d="M12 11v6M12 7.5h.1"/>',
  extras: '<rect x="3" y="7" width="18" height="13" rx="2"/><path d="M3 7l3-4h3l-3 4M10 7l3-4h3l-3 4"/>',
  more: '<path d="M12 3l1.8 4.6L18 9l-4.2 1.4L12 15l-1.8-4.6L6 9l4.2-1.4z"/>',
  dots: '<path d="M5 12h.1M12 12h.1M19 12h.1"/>',
};
// mark: 'box' (remote here), 'line' (one step back), 'dull' (two steps back)
const nav = (item, mark) => {
  const items = ['Home', 'Movies', 'TV Shows', 'Documentaries', 'Files', 'search', 'cog'];
  return `<div class="nav"><span class="brand">SUPERNOVA</span>${items.map(t => `<span class="ni
    ${t === item ? mark : ''} ${I[t] ? 'icon' : ''}">${I[t] ? svg(I[t], 34) : t}</span>`).join('')}
    <span class="clock">21:40</span></div>`;
};
const tabs = (list, item, mark) => `<div class="tabs">${list.map(([ic, t]) => `<span class="tab
  ${t === item ? mark : ''}">${svg(I[ic])}${t}</span>`).join('')}</div>`;
const SET = [['play', 'Playback'], ['audio', 'Audio &amp; Subtitles'], ['lib', 'Library'], ['user', 'Accounts'],
  ['sys', 'System']];
const DET = [['eps', 'Seasons &amp; Episodes'], ['info', 'Details'], ['extras', 'Extras'], ['more', 'More Like This']];
const row = (ic, name, line, focus) => `<div class="row ${focus ? 'focus' : ''}"><span class="ri">${svg(I[ic], 38)}
  </span><div><div class="rn">${name}</div><div class="rl">${line}</div></div><span class="pill">Signed in</span></div>`;
const settings = (level) => `${nav('cog', ['box', 'line', 'dull'][level])}<div class="page">
  ${tabs(SET, 'Accounts', ['dull', 'box', 'line'][level])}
  ${row('cloud', 'Put.io', 'yourname · 42 of 100 GB used', level === 2)}
  ${row('sync', 'Trakt', 'Watched and progress sync', false)}
  ${row('subs', 'OpenSubtitles', 'Download subtitles from inside the player', false)}</div>`;
const art = ['#5b3a2e', '#2e4a5b', '#4b5b2e', '#5b2e4a', '#2e5b4f', '#3a2e5b'];
const card = (i, big) => `<span class="card ${big ? 'grow' : ''}" style="background:linear-gradient(160deg,${art[i]},
  #121418)"></span>`;
const home = (onHero) => `${nav('Home', onHero ? 'line' : 'box')}<div class="hero">
  <div class="hcopy"><div class="hlogo">SLOW HORSES</div><div class="hmeta">2022 · Drama, Thriller</div>
  <div class="hdesc">A team of disgraced MI5 agents, banished to a dumping ground, find themselves drawn into one
  dangerous case after another.</div><span class="btn ${onHero ? 'focus' : ''}">${svg(I.info, 26)}More info</span></div>
  <div class="dots"><i class="on"></i><i></i><i></i><i></i></div></div>
  <div class="rowtitle">Continue Watching</div><div class="cards">${[0, 1, 2, 3].map(i => card(i, false)).join('')}</div>`;
const details = (level) => `${nav('TV Shows', ['line', 'line', 'dull'][level])}<div class="dhero">
  <div class="hcopy"><div class="hlogo">MARSHALS</div><div class="hmeta">2026 · Drama, Crime</div>
  <span class="btn ${level === 0 ? 'focus' : ''}">${svg(I.play, 26)}Resume S1 E1</span>
  <span class="btn ${''}">${svg(I.dots, 26)}More</span></div></div>
  <div class="dpage">${tabs(DET, 'Seasons &amp; Episodes', ['dull', 'box', 'line'][level])}
  <div class="cards">${[0, 1, 2, 3].map(i => card(i + 1, level === 2 && i === 0)).join('')}</div></div>`;
const css = `${font(400)}${font(500)}*{box-sizing:border-box;margin:0}
body{width:1920px;height:1080px;overflow:hidden;font-family:R;color:#fff;background:#16181C}
.nav{position:absolute;left:56px;right:56px;top:24px;height:76px;display:flex;align-items:center;gap:14px;
font-size:33px;color:#8f98a8;z-index:5}.nav .brand{color:#f1ead8;letter-spacing:2px;margin-right:36px;font-weight:500}
.ni{position:relative;display:inline-flex;align-items:center;height:60px;padding:0 18px;border-radius:16px}
.ni.icon:nth-of-type(7){margin-left:auto}.nav .clock{margin-left:18px;color:#C9D3E6}
.box{background:#F4F6FB;color:#0B0D12 !important}
.line,.dull{color:#fff}.dull{color:#C9D3E6}
.line:after,.dull:after{content:'';position:absolute;left:18px;right:18px;bottom:-2px;height:4px;border-radius:2px}
.line:after{background:#fff}.dull:after{background:rgba(255,255,255,.3)}
.page{position:absolute;left:96px;right:96px;top:150px}
.tabs{display:flex;gap:10px;border-bottom:2px solid rgba(255,255,255,.14);padding-bottom:12px;margin-bottom:22px}
.tab{position:relative;display:inline-flex;align-items:center;gap:12px;height:60px;padding:0 20px;border-radius:16px;
font-size:30px;font-weight:500;color:#8f98a8}.tab.line:after,.tab.dull:after{left:20px;right:20px;bottom:-14px}
.row{display:flex;align-items:center;gap:28px;height:120px;padding:0 30px;box-shadow:inset 0 -2px 0 rgba(255,255,255,.08)}
.row.focus{background:#F4F6FB;color:#0B0D12;border-radius:22px;box-shadow:none}
.ri{color:#C9D3E6}.row.focus .ri{color:#0B0D12}.rn{font-size:34px;font-weight:500}
.rl{font-size:26px;color:#C9D3E6;margin-top:4px}.row.focus .rl{color:#3A4256}
.pill{margin-left:auto;padding:8px 26px;border-radius:22px;font-size:25px;font-weight:500;
box-shadow:inset 0 0 0 2px rgba(255,255,255,.3)}.row.focus .pill{box-shadow:inset 0 0 0 2px rgba(11,13,18,.35)}
.edge{box-shadow:inset 0 0 0 2px rgba(255,255,255,.6),inset 0 0 26px 6px rgba(0,0,0,.45)}
.hero,.dhero{position:absolute;left:72px;right:72px;top:118px;height:640px;border-radius:26px;
background:linear-gradient(110deg,#20262c 0%,#2c3a44 40%,#6a7a84 100%);
box-shadow:inset 0 0 0 2px rgba(255,255,255,.6),inset 0 0 26px 6px rgba(0,0,0,.45);transition:none}
.hero.grow{transform:scale(1.025)}
.dhero{height:560px}
.hcopy{position:absolute;left:60px;top:70px;width:900px}
.hlogo{font-size:76px;font-weight:500;letter-spacing:6px;color:#f1ead8}
.hmeta{font-size:28px;margin-top:14px;color:#e8edf6}
.hdesc{font-size:26px;line-height:1.45;margin-top:18px;color:#e8edf6;max-height:113px;overflow:hidden}
.btn{display:inline-flex;align-items:center;gap:12px;height:66px;padding:0 30px;border-radius:18px;margin-top:30px;
margin-right:14px;font-size:28px;font-weight:500;background:rgba(22,24,28,.72);color:#fff}
.btn.focus{background:#F4F6FB;color:#0B0D12}
.dots{position:absolute;left:50%;bottom:26px;transform:translateX(-50%);display:flex;gap:12px}
.dots i{width:12px;height:12px;border-radius:50%;background:rgba(255,255,255,.35)}.dots i.on{background:#fff}
.rowtitle{position:absolute;left:96px;top:800px;font-size:30px;font-weight:500}
body>.cards{position:absolute;left:96px;top:856px}
.cards{display:flex;gap:26px}.card{width:400px;height:225px;border-radius:16px;flex:none;
box-shadow:inset 0 0 0 2px rgba(255,255,255,.6),inset 0 0 18px 4px rgba(0,0,0,.45)}
.card.grow{width:440px;height:248px;margin-top:-12px;box-shadow:0 0 0 5px #fff,inset 0 0 18px 4px rgba(0,0,0,.45),0 14px 34px rgba(0,0,0,.55)}
.dpage{position:absolute;left:96px;right:96px;top:700px}
.dpage .cards{margin-top:34px}`;
(async () => {
  const browser = await chromium.launch();
  const tab = await browser.newPage({ viewport: { width: 1920, height: 1080 } });
  const shots = [['08-v3-settings-1-top-bar.png', settings(0)], ['09-v3-settings-2-tabs.png', settings(1)],
    ['10-v3-settings-3-row.png', settings(2)], ['11-v3-home-1-top-bar.png', home(false)],
    ['12-v3-home-2-hero.png', home(true)], ['13-v3-details-1-button.png', details(0)],
    ['14-v3-details-2-tabs.png', details(1)], ['15-v3-details-3-episode.png', details(2)]];
  for (const [f, body] of shots) {
    await tab.setContent(`<html><head><style>${css}</style></head><body>${body}</body></html>`, { waitUntil: 'load' });
    await tab.evaluate(() => document.fonts.ready);
    await tab.screenshot({ path: path.join(__dirname, f) });
  }
  await browser.close();
})();
