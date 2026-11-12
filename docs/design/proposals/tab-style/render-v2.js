// Tab style v2 (owner, 12 Nov 2026): one marker. The open page/tab = white text and icon with an underline; others
// dimmer; no white box. Moving along tabs or the top bar switches the page. Bright underline = the remote is in that
// row; soft grey underline = the remote is down in the page. Same rule for the top bar and the tabs.
// Run: FONT_DIR=<roboto woff2 dir> node render-v2.js
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
};
// state: 'bar' remote in the top bar, 'tabs' remote in the tabs, 'page' remote down in the page
const nav = (open, state) => {
  const items = [['Home'], ['Movies'], ['TV Shows'], ['Documentaries'], ['Files'], ['search', true], ['cog', true]];
  return `<div class="nav"><span class="brand">SUPERNOVA</span>${items.map(([t, ic]) => `<span class="ni
    ${t === open ? 'open ' + (state === 'bar' ? 'live' : 'rest') : ''} ${ic ? 'icon' : ''}">${ic ? svg(I[t], 34)
    : t}</span>`).join('')}<span class="clock">21:40</span></div>`;
};
const tabs = (open, state) => `<div class="tabs">${[['play', 'Playback'], ['audio', 'Audio &amp; Subtitles'],
  ['lib', 'Library'], ['user', 'Accounts'], ['sys', 'System']].map(([ic, t]) => `<span class="tab
  ${t === open ? 'open ' + (state === 'tabs' ? 'live' : 'rest') : ''}">${svg(I[ic])}${t}</span>`).join('')}</div>`;
const row = (ic, name, line, focus) => `<div class="row ${focus ? 'focus' : ''}"><span class="ri">${svg(I[ic], 38)}
  </span><div><div class="rn">${name}</div><div class="rl">${line}</div></div><span class="pill">Signed in</span></div>`;
const settings = (state) => `${nav('cog', state)}<div class="page">${tabs('Accounts', state)}
  ${row('cloud', 'Put.io', 'yourname · 42 of 100 GB used', state === 'page')}
  ${row('sync', 'Trakt', 'Watched and progress sync', false)}
  ${row('subs', 'OpenSubtitles', 'Download subtitles from inside the player', false)}</div>`;
const art = ['#5b3a2e', '#2e4a5b', '#4b5b2e', '#5b2e4a', '#2e5b4f', '#3a2e5b'];
const movies = `${nav('Movies', 'page')}<div class="page"><div class="grid">${Array.from({ length: 6 }, (_, i) =>
  `<span class="poster ${i === 1 ? 'focus' : ''}" style="background:linear-gradient(160deg,${art[i]},#121418)">
  </span>`).join('')}</div></div>`;
const moviesBar = movies.replace(nav('Movies', 'page'), nav('Movies', 'bar')).replace('poster focus', 'poster');
const css = `${font(400)}${font(500)}*{box-sizing:border-box;margin:0}
body{width:1920px;height:1080px;overflow:hidden;font-family:R;color:#fff;background:#16181C}
.nav{position:absolute;left:56px;right:56px;top:28px;height:70px;display:flex;align-items:center;gap:34px;
font-size:34px;color:#8f98a8}.nav .brand{color:#f1ead8;font-size:34px;letter-spacing:2px;margin-right:30px;font-weight:500}
.nav .ni{position:relative;display:inline-flex;align-items:center;height:56px}
.nav .ni:nth-child(6){margin-left:auto}.nav .clock{margin-left:18px;color:#C9D3E6}
.open{color:#fff !important}
.open:after{content:'';position:absolute;left:0;right:0;bottom:-4px;height:4px;border-radius:2px}
.open.live:after{background:#fff}.open.rest:after{background:rgba(255,255,255,.32)}
.nav .open.live{transform:scale(1.06)}
.page{position:absolute;left:96px;right:96px;top:150px}
.tabs{display:flex;gap:44px;border-bottom:2px solid rgba(255,255,255,.14);padding:0 4px 14px;margin-bottom:22px}
.tab{position:relative;display:inline-flex;align-items:center;gap:12px;font-size:30px;font-weight:500;color:#8f98a8}
.tab.open:after{bottom:-16px}.tab.open.live{transform:scale(1.06);transform-origin:left center}
.row{display:flex;align-items:center;gap:28px;height:120px;padding:0 30px;box-shadow:inset 0 -2px 0 rgba(255,255,255,.08)}
.row.focus{background:#F4F6FB;color:#0B0D12;border-radius:22px;box-shadow:none}
.ri{color:#C9D3E6}.row.focus .ri{color:#0B0D12}.rn{font-size:34px;font-weight:500}
.rl{font-size:26px;color:#C9D3E6;margin-top:4px}.row.focus .rl{color:#3A4256}
.pill{margin-left:auto;padding:8px 26px;border-radius:22px;font-size:25px;font-weight:500;
box-shadow:inset 0 0 0 2px rgba(255,255,255,.3)}.row.focus .pill{box-shadow:inset 0 0 0 2px rgba(11,13,18,.35)}
.grid{display:flex;gap:34px;margin-top:20px}
.poster{width:250px;height:375px;border-radius:14px;box-shadow:inset 0 0 0 2px rgba(255,255,255,.6),
inset 0 0 18px 4px rgba(0,0,0,.45)}.poster.focus{transform:scale(1.1);margin:0 12px}`;
(async () => {
  const browser = await chromium.launch();
  const tab = await browser.newPage({ viewport: { width: 1920, height: 1080 } });
  const shots = [['03-v2-remote-in-top-bar.png', settings('bar')], ['04-v2-remote-in-tabs.png', settings('tabs')],
    ['05-v2-remote-in-page.png', settings('page')], ['06-v2-movies-remote-on-poster.png', movies],
    ['07-v2-movies-remote-in-top-bar.png', moviesBar]];
  for (const [f, body] of shots) {
    await tab.setContent(`<html><head><style>${css}</style></head><body>${body}</body></html>`, { waitUntil: 'load' });
    await tab.evaluate(() => document.fonts.ready);
    await tab.screenshot({ path: path.join(__dirname, f) });
  }
  await browser.close();
})();
