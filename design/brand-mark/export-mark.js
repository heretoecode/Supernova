// Exports the app icon, Shield banner and splash with the new Supernova mark (owner, 14 Nov 2026; RULES §21;
// mock-up docs/design/proposals/brand/03, row 1). The mark and layouts are the mock-up's own (render-marks.js):
// two open white rings on charcoal #16181C. Run from the repository root:
//   FONT_DIR=<folder with roboto-latin-300/400-normal.woff2> node design/brand-mark/export-mark.js
// Writes 2x-size PNGs to design/brand-mark/out/; export-mark.py then sizes them into res/.
const fs = require('fs');
const path = require('path');
const { chromium } = require('playwright');

const fontDir = process.env.FONT_DIR;
const font = w => `@font-face{font-family:R;font-weight:${w};src:url(data:font/woff2;base64,${fs.readFileSync(
  path.join(fontDir, `roboto-latin-${w}-normal.woff2`)).toString('base64')}) format('woff2')}`;

const pt = (r, a) => [50 + r * Math.cos((a - 90) * Math.PI / 180), 50 + r * Math.sin((a - 90) * Math.PI / 180)];
const arc = (r, a0, a1) => {
  const [x0, y0] = pt(r, a0);
  const [x1, y1] = pt(r, a1);
  return `M${x0.toFixed(3)} ${y0.toFixed(3)}A${r} ${r} 0 ${a1 - a0 > 180 ? 1 : 0} 1 ${x1.toFixed(3)} ${y1.toFixed(3)}`;
};
const ring = (r, w, head, len, dir) => {
  const a0 = dir > 0 ? head - len : head;
  const a1 = dir > 0 ? head : head + len;
  return `<path d="${arc(r, a0, a1)}" stroke="#fff" stroke-width="${w}" fill="none" stroke-linecap="round"/>`;
};
const mark = `<svg viewBox="0 0 100 100" width="100%" height="100%" overflow="visible">
  ${ring(40, 6, 45, 280, 1)}${ring(24, 5, 225, 280, -1)}</svg>`;

const css = `${font(300)}${font(400)}*{box-sizing:border-box;margin:0}
body{font-family:R;color:#fff;background:transparent}
.icon{width:200px;height:200px;background:#16181C;display:flex;align-items:center;justify-content:center}
.im{width:68%;height:68%}
.fore{width:216px;height:216px;display:flex;align-items:center;justify-content:center}
.fm{width:45.3%;height:45.3%}
.banner,.splash{width:400px;height:225px;background:#16181C;display:flex;align-items:center;justify-content:center}
.banner{gap:22px}.bm{width:96px;height:96px}.wm{font-size:34px;letter-spacing:.1em;margin-right:-3px}
.splash{flex-direction:column}.sm{width:64px;height:64px}
.swm{margin-top:14px;font-size:16px;font-weight:300;letter-spacing:.14em}`;

const pieces = [
  ['icon', `<div class="icon"><div class="im">${mark}</div></div>`, 200, 200, 5.12],
  ['adaptive-foreground', `<div class="fore"><div class="fm">${mark}</div></div>`, 216, 216, 1024 / 216],
  ['banner', `<div class="banner"><div class="bm">${mark}</div><div class="wm">SUPERNOVA</div></div>`, 400, 225, 2],
  ['splash', `<div class="splash"><div class="sm">${mark}</div><div class="swm">SUPERNOVA</div></div>`, 400, 225, 4.8],
];

(async () => {
  const out = path.join(__dirname, 'out');
  fs.mkdirSync(out, { recursive: true });
  const browser = await chromium.launch();
  for (const [name, html, width, height, scale] of pieces) {
    const tab = await browser.newPage({ viewport: { width, height }, deviceScaleFactor: scale });
    await tab.setContent(`<html><head><style>${css}</style></head><body>${html}</body></html>`, { waitUntil: 'load' });
    await tab.evaluate(() => document.fonts.ready);
    await tab.screenshot({ path: path.join(out, `${name}.png`), omitBackground: true,
      clip: { x: 0, y: 0, width, height } });
    await tab.close();
  }
  await browser.close();
})();
