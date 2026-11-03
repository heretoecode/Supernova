// Proposal (after 0.154): Network & Files with a live Scan now tile; Library Scanning and Library Health pages.
// Real 1080p sizes (Settings tile sizes as approved for 0.153). Run: FONT_DIR=<roboto woff2 dir> node render.js
const fs = require('fs');
const path = require('path');
const { chromium } = require('playwright');
const out = __dirname;
const fontDir = process.env.FONT_DIR;
const font = w => `@font-face{font-family:R;font-weight:${w};src:url(data:font/woff2;base64,${fs.readFileSync(path.join(fontDir, `roboto-latin-${w}-normal.woff2`)).toString('base64')}) format('woff2')}`;

const P = {
  play: '<path d="M8 5l11 7-11 7z"/>',
  speaker: '<path d="M4 9h4l5-4v14l-5-4H4z"/><path d="M16 9a4 4 0 0 1 0 6"/>',
  film: '<rect x="3" y="4" width="18" height="16" rx="1.5"/><path d="M7 4v16M17 4v16M3 9h4M3 15h4M17 9h4M17 15h4"/>',
  cloud: '<path d="M7 18h10a4 4 0 0 0 .5-8A6 6 0 0 0 6 9.5 4.3 4.3 0 0 0 7 18z"/>',
  monitor: '<rect x="3" y="4" width="18" height="12" rx="1.5"/><path d="M8 20h8M12 16v4"/>',
  nodes: '<circle cx="6" cy="5" r="2"/><circle cx="18" cy="5" r="2"/><circle cx="12" cy="19" r="2"/><path d="M6 7l5 10M18 7l-5 10M8 5h8"/>',
  gear: '<circle cx="12" cy="12" r="3.2"/><path d="M12 2.5v3M12 18.5v3M2.5 12h3M18.5 12h3M5.3 5.3l2.1 2.1M16.6 16.6l2.1 2.1M5.3 18.7l2.1-2.1M16.6 7.4l2.1-2.1"/>',
  info: '<circle cx="12" cy="12" r="9"/><path d="M12 11v6M12 7.5v.5"/>',
  drive: '<path d="M6 4h12l2 11H4z"/><path d="M4 15h16v5H4zM8 17.5h.1"/>',
  servers: '<rect x="4" y="3" width="16" height="7" rx="1.5"/><rect x="4" y="14" width="16" height="7" rx="1.5"/><path d="M8 6.5h.1M8 17.5h.1"/>',
  plus: '<path d="M12 4v16M4 12h16"/>',
  links: '<circle cx="6" cy="12" r="2.5"/><circle cx="18" cy="6" r="2.5"/><circle cx="18" cy="18" r="2.5"/><path d="M8.3 11l7.4-3.8M8.3 13l7.4 3.8"/>',
  scan: '<path d="M4 12a8 8 0 0 1 14-5.3M20 12a8 8 0 0 1-14 5.3"/><path d="M18 3v4h-4M6 21v-4h4"/>',
  health: '<path d="M3 12h4l2-5 4 10 2-5h6"/>',
  warn: '<path d="M12 3l10 18H2z"/><path d="M12 10v5M12 18v.1"/>',
  sliders: '<path d="M4 7h16M4 12h16M4 17h16"/><circle cx="9" cy="7" r="2" fill="currentColor"/><circle cx="15" cy="12" r="2" fill="currentColor"/><circle cx="7" cy="17" r="2" fill="currentColor"/>',
};
const ic = (k, s = 40) => `<svg width="${s}" height="${s}" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.7" stroke-linecap="round" stroke-linejoin="round">${P[k]}</svg>`;
const nav = active => `<div class="nav"><span class="brand">SUPERNOVA</span><span>Home</span><span>Movies</span><span>TV Shows</span><span>Documentaries</span>
  <span class="sp"></span><span class="${active === 'net' ? 'on' : ''}">Network &amp; Files</span>${ic('info', 0)}<span class="icon">🔍</span><span class="icon ${active === 'set' ? 'on' : ''}">⚙</span><span class="clock">16:50</span></div>`;

const css = `${font(400)}${font(500)}
*{box-sizing:border-box;margin:0}
body{width:1920px;height:1080px;overflow:hidden;font-family:R;color:#fff;position:relative;
  background:radial-gradient(ellipse 70% 60% at 70% 75%,#1d4fd8 0%,rgba(29,79,216,0) 70%),linear-gradient(180deg,#05081a 0%,#0a1650 45%,#10248a 100%)}
.nav{position:absolute;left:56px;right:56px;top:28px;height:56px;display:flex;align-items:center;gap:30px;font-size:34px;color:#e8edf8}
.nav .brand{font-size:36px;margin-right:40px;letter-spacing:1px}.nav .sp{flex:1}.nav .on{color:#fff;font-weight:500}
.nav .icon{font-size:34px;filter:grayscale(1) brightness(3)}.nav .clock{margin-left:30px}
.grid{position:absolute;left:56px;right:56px;top:150px;bottom:56px;display:grid;gap:26px}
.tile{border-radius:22px;border:2px solid rgba(255,255,255,.22);display:flex;flex-direction:column;padding:0 34px;justify-content:flex-start;padding-top:30%}
.tile.focus{background:#F4F6FB;border-color:#F4F6FB;color:#0B0D12}
.head{display:flex;align-items:center;gap:16px;font-size:35px;font-weight:500;white-space:nowrap}
.desc{font-size:25px;line-height:1.35;margin-top:14px;color:#C9D3E6}
.tile.focus .desc{color:#3A4256}
.desc .live{display:flex;align-items:center;gap:12px}.bar{display:block;width:100%;height:8px;border-radius:4px;background:rgba(255,255,255,.18);overflow:hidden;margin-top:16px}.bar i{display:block;height:8px;background:#5aa9ff}
.tile.focus .bar{background:rgba(11,13,18,.15)}.tile.focus .bar i{background:#2f6df0}.ok{color:#5ad18a}.tile.focus .ok{color:#1d8a4e}.wa{color:#f0b44c}.tile.focus .wa{color:#a86400}
.label{position:absolute;right:56px;bottom:14px;font-size:20px;color:#c9d3e6}`;

const tile = ([k, t, d, f]) => `<div class="tile${f ? ' focus' : ''}" style="${f ? '' : 'background:rgba(22,24,28,.97)'}">
  <div class="head">${ic(k)}<span>${t}</span></div><div class="desc">${d}</div></div>`;
const grid = items => `<div class="grid" style="grid-template-columns:repeat(3,1fr);grid-template-rows:1fr 1fr">${items.map(tile).join('')}</div>`;
const base = [
  ['drive', 'Local Storage', "Browse this device's storage and any USB drives."],
  ['servers', 'Network Shares', 'Browse and manage Put.io and any other shares.'],
  ['plus', 'Add Connection', 'Connect to a computer, NAS or server.'],
  ['health', 'Library Health', '2 missing files to review.'],
  ['sliders', 'Library Scanning', 'Every 15 minutes and when Supernova opens.'],
];
const scan = {
  rest: ['scan', 'Scan now', 'Last scan 4 hours ago<br>1,808 videos in your library'],
  running: ['scan', 'Scanning…', 'Put.io › Films · 312 of 1,808<span class="bar"><i style="width:17%"></i></span>'],
  done: ['scan', 'Scan complete', '<span class="ok">3 new videos</span> · 1 removed<br>Finished just now'],
  error: ['scan', 'Scan now', '<span class="wa">Put.io not reachable</span><br>Press to try again'],
};
const page = body => `<html><head><style>${css}</style></head><body>${body}</body></html>`;
const landing = (state, focusScan) => page(nav('net') + grid([...base, [...scan[state], focusScan]]) +
  `<div class="label">Scan now tile · ${state === 'rest' ? 'at rest' : state === 'running' ? 'scanning (OK again offers Stop)' : state === 'done' ? 'just finished, returns to rest after a few seconds' : 'a source failed'}</div>`);
const pages = [
  ['01-landing-scan-rest.png', landing('rest', true)],
  ['02-landing-scan-running.png', landing('running', false)],
  ['03-landing-scan-done.png', landing('done', false)],
  ['04-landing-scan-error.png', landing('error', true)],
];
(async () => {
  const browser = await chromium.launch();
  const tab = await browser.newPage({ viewport: { width: 1920, height: 1080 } });
  for (const [f, html] of pages) {
    await tab.setContent(html, { waitUntil: 'load' });
    await tab.evaluate(() => document.fonts.ready);
    await tab.screenshot({ path: path.join(out, f) });
  }
  await browser.close();
})();
