// Owner request (5 Nov 2026): Library Health with no panel, using the full page (bigger text, more space).
// Run: FONT_DIR=<roboto woff2 dir> node render-v2.js
const fs = require('fs');
const path = require('path');
const { chromium } = require('playwright');
const fontDir = process.env.FONT_DIR;
const font = w => `@font-face{font-family:R;font-weight:${w};src:url(data:font/woff2;base64,${fs.readFileSync(path.join(fontDir, `roboto-latin-${w}-normal.woff2`)).toString('base64')}) format('woff2')}`;
const svg = (d, s) => `<svg width="${s}" height="${s}" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.6" stroke-linecap="round" stroke-linejoin="round">${d}</svg>`;
const I = {
  health: '<path d="M3 12h4l2-5 4 10 2-5h6"/>',
  warn: '<path d="M12 3l9.5 17h-19z"/><path d="M12 10v4.5M12 17.5v.3"/>',
  video: '<rect x="3" y="5" width="18" height="14" rx="2"/><path d="M10 9.5v5l4.5-2.5z" fill="currentColor"/>',
  match: '<rect x="3" y="5" width="18" height="14" rx="2"/><path d="M9 10a3 3 0 1 1 3 3v1.5M12 17v.3"/>',
  art: '<rect x="3" y="4" width="18" height="16" rx="2"/><circle cx="9" cy="10" r="2"/><path d="M4 18l5-5 4 4 3-3 4 4"/>',
  cloud: '<path d="M7 18h10a4 4 0 0 0 .5-8A6 6 0 0 0 6 9.5 4.3 4.3 0 0 0 7 18z"/>',
  check: '<circle cx="12" cy="12" r="9"/><path d="M8 12.5l2.7 2.7L16 9.7"/>',
  open: '<path d="M9 6l6 6-6 6"/>',
};
const row = (icon, name, line, end, cls = '') => `<div class="row ${cls}"><span class="ic">${svg(I[icon], 44)}</span>
  <div class="two"><div class="n">${name}</div><div class="l">${line}</div></div>${end}</div>`;
const ok = `<span class="ok">${svg(I.check, 40)}<b>All clear</b></span>`;
const count = n => `<span class="cnt">${n}<i>${svg(I.open, 30)}</i></span>`;
const body = `
<div class="nav"><span class="brand">SUPERNOVA</span><span>Home</span><span>Movies</span><span>TV Shows</span>
<span>Documentaries</span><span class="sp"></span><span class="on">Network &amp; Files</span><span class="clock">20:51</span></div>
<div class="page">
  <div class="head">
    <div class="crumb">Network &amp; Files</div>
    <div class="title"><span class="tile">${svg(I.health, 52)}</span>Library Health</div>
    <div class="facts"><span class="warnpill"><i></i>2 to review</span><span>1,808 videos checked</span>
      <span>Last checked 12 minutes ago</span></div>
  </div>
  <div class="rows">
    ${row('warn', 'Missing files', '2 videos in your library whose file has gone', count(2), 'focus')}
    ${row('video', 'Not matched', 'Videos Supernova could not identify', ok)}
    ${row('match', 'Possibly wrong match', 'Matches you reported as wrong', ok)}
    ${row('art', 'Missing artwork or details', 'Matched, but without a poster or description', ok)}
    ${row('cloud', 'Sources offline', 'Drives or shares that could not be reached', ok)}
  </div>
</div>`;
const css = `${font(400)}${font(500)}
*{box-sizing:border-box;margin:0}
body{width:1920px;height:1080px;overflow:hidden;font-family:R;color:#fff;background:#16181C;position:relative}
.nav{position:absolute;left:56px;right:56px;top:28px;height:56px;display:flex;align-items:center;gap:30px;font-size:34px;color:#e8edf8}
.nav .brand{font-size:36px;margin-right:40px;letter-spacing:1px}.nav .sp{flex:1}.nav .on{font-weight:500}.nav .clock{margin-left:30px}
.page{position:absolute;left:96px;right:96px;top:132px;bottom:56px;display:flex;flex-direction:column}
.crumb{font-size:24px;color:#9aa3b2;margin-bottom:10px}
.title{display:flex;align-items:center;gap:26px;font-size:60px;font-weight:500}
.tile{width:92px;height:92px;border-radius:24px;background:#2a2d33;display:flex;align-items:center;justify-content:center;color:#e8edf8}
.facts{display:flex;gap:14px;margin-top:22px;font-size:24px;color:#dfe5f1}
.facts span{background:rgba(255,255,255,.08);border-radius:14px;padding:8px 16px;display:inline-flex;align-items:center;gap:10px}
.warnpill i{width:12px;height:12px;border-radius:50%;background:#f0b44c}
.rows{flex:1;display:flex;flex-direction:column;justify-content:space-evenly;margin-top:34px}
.row{display:flex;align-items:center;gap:32px;height:128px;padding:0 36px;border-radius:28px;background:rgba(255,255,255,.035)}
.row.focus{background:#F4F6FB;color:#0B0D12}.row.focus .l{color:#3A4256}
.ic{width:56px;display:flex;justify-content:center;flex:none}
.two{flex:1;display:flex;flex-direction:column;gap:8px}.n{font-size:36px;font-weight:500}.l{font-size:26px;color:#C9D3E6}
.ok{display:flex;align-items:center;gap:14px;color:#5ad18a;font-size:26px}.ok b{font-weight:400;color:#C9D3E6}
.cnt{display:flex;align-items:center;gap:10px;font-size:40px;font-weight:500}.cnt i{display:flex}`;
(async () => {
  const browser = await chromium.launch();
  const tab = await browser.newPage({ viewport: { width: 1920, height: 1080 } });
  await tab.setContent(`<html><head><style>${css}</style></head><body>${body}</body></html>`, { waitUntil: 'load' });
  await tab.evaluate(() => document.fonts.ready);
  await tab.screenshot({ path: path.join(__dirname, '02-library-health-full-page.png') });
  await browser.close();
})();
