// 29 · the Extras player as a 1280 x 720 floating window (owner, 27 Nov), over frame 18 blurred and dimmed.
// node render-29.js <background png> <font dir>
const fs = require('fs');
const path = require('path');
const { chromium } = require('playwright');

const bg = fs.readFileSync(process.argv[2]).toString('base64');
const fontDir = process.argv[3];
const font = w => `@font-face{font-family:R;font-weight:${w};src:url(data:font/woff2;base64,${fs.readFileSync(
  path.join(fontDir, `roboto-latin-${w}-normal.woff2`)).toString('base64')}) format('woff2')}`;

const html = `<!doctype html><html><head><meta charset="utf-8"><style>${font(400)}${font(500)}
*{margin:0;padding:0;box-sizing:border-box}
body{width:1920px;height:1080px;overflow:hidden;background:#111215;font-family:R,sans-serif}
.page{position:absolute;inset:-30px;background:url(data:image/png;base64,${bg}) center/1920px 1080px no-repeat;
 filter:blur(14px)}
.dim{position:absolute;inset:0;background:rgba(10,11,13,.55)}
.win{position:absolute;left:320px;top:180px;width:1280px;height:720px;border-radius:20px;overflow:hidden;
 box-shadow:0 0 0 1px #fff,0 24px 70px 18px rgba(0,0,0,.8)}
.still{position:absolute;inset:0;background:
 radial-gradient(ellipse 40% 55% at 68% 42%,rgba(255,196,140,.85),rgba(255,196,140,0) 70%),
 radial-gradient(ellipse 70% 60% at 30% 70%,rgba(40,70,110,.9),rgba(40,70,110,0) 75%),
 linear-gradient(180deg,#2b3d5c 0%,#5c4a5e 45%,#1a1418 100%)}
.hill{position:absolute;left:-5%;right:-5%;bottom:0;height:38%;background:#120e12;border-radius:50% 50% 0 0/40% 40% 0 0}
.fig{position:absolute;left:58%;bottom:30%;width:46px;height:150px;background:#0c090c;border-radius:23px 23px 6px 6px}
.fig:before{content:'';position:absolute;left:7px;top:-38px;width:32px;height:32px;border-radius:16px;background:#0c090c}
.inner{position:absolute;inset:0;border-radius:20px;box-shadow:inset 0 0 24px rgba(0,0,0,.35)}
.prog{position:absolute;left:32px;right:32px;bottom:24px;height:4px;border-radius:2px;background:rgba(255,255,255,.3)}
.prog b{position:absolute;left:0;top:0;bottom:0;width:22%;border-radius:2px;background:#fff}
.cap{position:absolute;right:52px;bottom:24px;color:#e8ebf2;font-size:24px;background:rgba(17,18,21,.85);
 padding:4px 12px;border-radius:8px}
.border{position:absolute;inset:0;border:2px solid #c9ccd3}
</style></head><body><div class="page"></div><div class="dim"></div>
<div class="win"><div class="still"></div><div class="hill"></div><div class="fig"></div><div class="inner"></div>
<div class="prog"><b></b></div></div>
<div class="cap">29 · Extras player: a 1280 × 720 window floating over the blurred page; Back closes</div>
<div class="border"></div></body></html>`;

(async () => {
  const b = await chromium.launch();
  const p = await b.newPage({ viewport: { width: 1920, height: 1080 } });
  await p.setContent(html);
  await p.evaluate(() => document.fonts.ready);
  await p.screenshot({ path: path.join(__dirname, 'tv', '29-extras-window-1280.png') });
  await b.close();
})();
