// Tab style choice (owner, 12 Nov 2026): A grey chip for the open tab; B thin line with a short underline under the
// open tab. Focused tab filled white in both. Shown on Details, Files and Settings.
// Run: FONT_DIR=<roboto woff2 dir> node render.js
const fs = require('fs');
const path = require('path');
const { chromium } = require('playwright');
const fontDir = process.env.FONT_DIR;
const font = w => `@font-face{font-family:R;font-weight:${w};src:url(data:font/woff2;base64,${fs.readFileSync(
  path.join(fontDir, `roboto-latin-${w}-normal.woff2`)).toString('base64')}) format('woff2')}`;
const svg = d => `<svg width="28" height="28" viewBox="0 0 24 24" fill="none" stroke="currentColor"
  stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round">${d}</svg>`;
const I = {
  eps: '<rect x="3" y="4" width="18" height="12" rx="2"/><path d="M7 20h10"/><path d="M10 8l5 2.5-5 2.5z"/>',
  info: '<circle cx="12" cy="12" r="9"/><path d="M12 11v6M12 7.5h.1"/>',
  extras: '<rect x="3" y="7" width="18" height="13" rx="2"/><path d="M3 7l3-4h3l-3 4M10 7l3-4h3l-3 4"/>',
  more: '<path d="M12 3l1.8 4.6L18 9l-4.2 1.4L12 15l-1.8-4.6L6 9l4.2-1.4z"/>',
  tv: '<rect x="3" y="5" width="18" height="12" rx="2"/><path d="M8 21h8M12 17v4"/>',
  conn: '<rect x="4" y="3" width="16" height="7" rx="1.5"/><rect x="4" y="14" width="16" height="7" rx="1.5"/>',
  care: '<path d="M3 12h4l2-5 4 10 2-5h6"/>',
  play: '<path d="M7 4l13 8-13 8z"/>',
  audio: '<path d="M4 9v6h4l5 4V5L8 9z"/><path d="M16 9a4 4 0 0 1 0 6"/>',
  lib: '<rect x="3" y="4" width="5" height="16" rx="1"/><rect x="10" y="4" width="5" height="16" rx="1"/>'
    + '<path d="M17 5l4 14"/>',
  user: '<circle cx="12" cy="8" r="4"/><path d="M4 21a8 8 0 0 1 16 0"/>',
  sys: '<rect x="3" y="5" width="18" height="12" rx="2"/><path d="M8 21h8"/>',
};
const tabs = (list, open, focus, badge) => `<div class="tabs">${list.map(([ic, t], i) => `<span class="tab
  ${i === open ? 'open' : ''} ${i === focus ? 'focus' : ''}">${svg(I[ic])}${t}${badge && i === 2
  ? '<b class="badge">5</b>' : ''}</span>`).join('')}</div>`;
const art = ['#5b3a2e', '#2e4a5b', '#4b5b2e', '#5b2e4a', '#2e5b4f'];
const cards = n => `<div class="cards">${Array.from({ length: n }, (_, i) => `<span class="card"
  style="background:linear-gradient(160deg,${art[i % 5]},#121418)"></span>`).join('')}</div>`;
const rows = n => Array.from({ length: n }, () => '<div class="row"><i></i><b></b></div>').join('');
const band = (title, body, extra) => `<div class="band"><div class="ttl">${title}</div>${body}${extra}</div>`;
const page = (style) => `<div class="${style}">
${band('MARSHALS', tabs([['eps', 'Seasons &amp; Episodes'], ['info', 'Details'], ['extras', 'Extras'],
    ['more', 'More Like This']], 0, 1), cards(4))}
${band('Files', tabs([['tv', 'This Shield'], ['conn', 'Connections'], ['care', 'Library Care']], 1, 0, true),
    rows(2))}
${band('Settings', tabs([['play', 'Playback'], ['audio', 'Audio &amp; Subtitles'], ['lib', 'Library'],
    ['user', 'Accounts'], ['sys', 'System']], 3, 4), rows(2))}</div>`;
const css = `${font(400)}${font(500)}*{box-sizing:border-box;margin:0}
body{width:1920px;height:1080px;overflow:hidden;font-family:R;color:#fff;background:#16181C}
.band{height:340px;padding:34px 72px 0;border-bottom:2px solid #0d0e11;overflow:hidden}
.ttl{font-size:30px;font-weight:500;letter-spacing:4px;color:#f1ead8;margin-bottom:22px}
.tabs{display:flex;gap:10px;align-items:center;position:relative;padding-bottom:12px}
.tab{display:inline-flex;align-items:center;gap:12px;height:62px;padding:0 20px;border-radius:16px;font-size:28px;
font-weight:500;color:#C9D3E6;white-space:nowrap;position:relative}
.tab.focus{background:#F4F6FB;color:#0B0D12}
.badge{margin-left:6px;min-width:30px;height:30px;border-radius:15px;background:#F4F6FB;color:#0B0D12;font-size:19px;
display:inline-flex;align-items:center;justify-content:center;padding:0 8px}.tab.focus .badge{background:#0B0D12;color:#fff}
.A .tabs{border-bottom:2px solid rgba(255,255,255,.18)}
.A .tab.open:not(.focus){background:#454B55;color:#fff}
.B .tabs{border-bottom:2px solid rgba(255,255,255,.18)}
.B .tab.open{color:#fff}
.B .tab.open:after{content:'';position:absolute;left:20px;right:20px;bottom:-14px;height:4px;border-radius:2px;
background:#fff}
.cards{display:flex;gap:24px;margin-top:34px}.card{width:400px;height:225px;border-radius:16px;
box-shadow:inset 0 0 0 2px rgba(255,255,255,.6),inset 0 0 18px 4px rgba(0,0,0,.45)}
.row{display:flex;align-items:center;gap:24px;height:86px;margin-top:14px;padding:0 24px;
box-shadow:inset 0 -2px 0 rgba(255,255,255,.08)}.row i{width:36px;height:36px;border-radius:8px;
background:rgba(255,255,255,.14)}.row b{width:520px;height:22px;border-radius:6px;background:rgba(255,255,255,.14)}`;
(async () => {
  const browser = await chromium.launch();
  const tab = await browser.newPage({ viewport: { width: 1920, height: 1080 } });
  for (const [f, s] of [['01-grey-chip.png', 'A'], ['02-underline.png', 'B']]) {
    await tab.setContent(`<html><head><style>${css}</style></head><body>${page(s)}</body></html>`,
      { waitUntil: 'load' });
    await tab.evaluate(() => document.fonts.ready);
    await tab.screenshot({ path: path.join(__dirname, f) });
  }
  await browser.close();
})();
