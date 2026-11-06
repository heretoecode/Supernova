// Exploring (owner, 6 Nov 2026): the top bar — where the scan ring goes, and what Network & Files is called.
const fs = require('fs');
const path = require('path');
const { chromium } = require('playwright');
const fontDir = process.env.FONT_DIR;
const font = w => `@font-face{font-family:R;font-weight:${w};src:url(data:font/woff2;base64,${fs.readFileSync(path.join(fontDir, `roboto-latin-${w}-normal.woff2`)).toString('base64')}) format('woff2')}`;
const svg = (d, s = 34) => `<svg width="${s}" height="${s}" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round">${d}</svg>`;
const ring = `<svg width="34" height="34" viewBox="0 0 24 24" fill="none" stroke-width="1.8" stroke-linecap="round"><circle cx="12" cy="12" r="8.5" stroke="rgba(255,255,255,.3)"/><path d="M12 3.5a8.5 8.5 0 0 1 8.5 8.5" stroke="#fff"/></svg>`;
const search = svg('<circle cx="11" cy="11" r="7"/><path d="M20 20l-4-4"/>');
const cog = svg('<circle cx="12" cy="12" r="3"/><path d="M12 2v3M12 19v3M4.2 4.2l2.1 2.1M17.7 17.7l2.1 2.1M2 12h3M19 12h3M4.2 19.8l2.1-2.1M17.7 6.3l2.1-2.1"/>');
const folder = svg('<path d="M3 7a2 2 0 0 1 2-2h4l2 2h8a2 2 0 0 1 2 2v8a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2z"/>');
const bar = (label, right) => `<div class="nav"><span class="brand">SUPERNOVA</span><span class="on">Home</span><span>Movies</span><span>TV Shows</span><span>Documentaries</span>
<span class="sp"></span>${right}<span class="ico">${search}</span><span class="ico">${cog}</span><span class="clock">23:40</span></div><div class="cap"></div>`;
const rows = [
  bar('Today', '<span>Network &amp; Files</span>'),
  bar('A · ring on the left of the words', `<span class="ring">${ring}</span><span>Network &amp; Files</span>`),
  bar('B · renamed “Files”, ring on the left', `<span class="ring">${ring}</span><span>Files</span>`),
  bar('C · a folder icon beside search and settings, ring round the icon', `<span class="ico wrap">${folder}<span class="halo">${ring.replace('width="34" height="34"', 'width="58" height="58"')}</span></span>`)];
const css = `${font(400)}${font(500)}*{box-sizing:border-box;margin:0}body{width:1920px;height:1080px;background:#16181C;font-family:R;color:#fff;padding-top:20px}
.nav{height:96px;display:flex;align-items:center;gap:30px;padding:0 56px;font-size:34px;color:#e8edf8}.nav .brand{font-size:36px;margin-right:40px;letter-spacing:1px}
.nav .sp{flex:1}.nav .on{font-weight:500;color:#fff}.nav .clock{margin-left:20px}.ico{display:inline-flex}.ring{display:inline-flex;margin-right:-16px}
.wrap{position:relative;width:34px;height:34px}.halo{position:absolute;left:-12px;top:-12px}
.cap{padding:0 56px 30px;border-bottom:2px solid rgba(255,255,255,.08);margin-bottom:26px}`;
(async () => {
  const browser = await chromium.launch();
  const tab = await browser.newPage({ viewport: { width: 1920, height: 1080 } });
  await tab.setContent(`<html><head><style>${css}</style></head><body>${rows.join('')}</body></html>`, { waitUntil: 'load' });
  await tab.evaluate(() => document.fonts.ready);
  await tab.screenshot({ path: path.join(__dirname, '31-top-bar-options.png') });
  await browser.close();
})();
