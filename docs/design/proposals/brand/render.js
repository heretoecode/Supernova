// App icon, Shield banner and splash on the charcoal look (owner, 14 Nov 2026; walkthrough 10 Nov asked for options
// that keep the recognisable rings and stay legible at launcher sizes). Two options, one picture each:
// A keeps today's glowing rings (cut from the current artwork, ring-glow.png) on charcoal instead of space blue.
// B redraws the rings as clean shapes: crisper at small sizes, no glow.
// Each picture: the banner in a launcher row (neighbours are blank made-up tiles), the banner large, the icon large
// and at real size, then the splash at full size. Light frame = picture edge.
// Run: FONT_DIR=<roboto woff2 dir> node render.js
const fs = require('fs');
const path = require('path');
const { chromium } = require('playwright');
const fontDir = process.env.FONT_DIR;
const font = w => `@font-face{font-family:R;font-weight:${w};src:url(data:font/woff2;base64,${fs.readFileSync(
  path.join(fontDir, `roboto-latin-${w}-normal.woff2`)).toString('base64')}) format('woff2')}`;
const glow = 'data:image/png;base64,' + fs.readFileSync(path.join(__dirname, 'ring-glow.png')).toString('base64');

const star = (x, y, s) => `<path d="M${x} ${y - s}L${x + s * .18} ${y - s * .18}L${x + s} ${y}L${x + s * .18}
  ${y + s * .18}L${x} ${y + s}L${x - s * .18} ${y + s * .18}L${x - s} ${y}L${x - s * .18} ${y - s * .18}Z" fill="#fff"/>`;
const flat = `<svg viewBox="0 0 100 100" width="100%" height="100%"><defs>
  <linearGradient id="o" x1="1" y1="0" x2="0" y2="1"><stop offset="0" stop-color="#fff"/>
  <stop offset=".55" stop-color="#5AA8FF"/><stop offset="1" stop-color="#2F6FE0"/></linearGradient>
  <linearGradient id="i" x1="0" y1="1" x2="1" y2="0"><stop offset="0" stop-color="#fff"/>
  <stop offset=".6" stop-color="#7FD3FF"/><stop offset="1" stop-color="#3A8CF0"/></linearGradient></defs>
  <circle cx="50" cy="50" r="38" fill="none" stroke="url(#o)" stroke-width="5.5"/>
  <circle cx="51" cy="51" r="24" fill="none" stroke="url(#i)" stroke-width="4"/>
  ${star(77, 23, 9)}${star(31, 62, 7)}</svg>`;
const ring = opt => opt === 'A' ? `<img src="${glow}" style="width:132%;height:132%;margin:-16%">` : flat;

const banner = (opt, w) => `<div class="banner" style="width:${w}px;height:${w * 9 / 16}px;--u:${w / 100}px">
  <div class="br">${ring(opt)}</div><div class="wm">SUPERNOVA</div></div>`;
const icon = (opt, s) => `<div class="icon" style="width:${s}px;height:${s}px"><div class="ir">${ring(opt)}</div></div>`;
const blank = (n) => `<div class="tile t${n}"></div>`;
const launcher = opt => `<section class="launch"><div class="row">${blank(1)}${blank(2)}
  <div class="focus">${banner(opt, 352)}</div>${blank(3)}${blank(4)}</div></section>`;
const big = opt => `<section class="big">${banner(opt, 960)}<div class="icons">${icon(opt, 420)}
  <div class="smalls">${icon(opt, 96)}${icon(opt, 48)}</div></div></section>`;
const splash = opt => `<section class="splash"><div class="sr">${ring(opt)}</div><div class="swm">SUPERNOVA</div>
  </section>`;

const css = `${font(300)}${font(400)}*{box-sizing:border-box;margin:0}
body{width:1920px;font-family:R;color:#fff;background:#16181C;position:relative}
body:after{content:"";position:fixed;inset:0;border:6px solid #8a919c;z-index:99;pointer-events:none}
section+section{border-top:8px solid #8a919c}
.launch{height:420px;background:linear-gradient(180deg,#0d0e11,#1b1d22);display:flex;align-items:center;
justify-content:center}
.row{display:flex;align-items:center;gap:36px}
.tile{width:320px;height:180px;border-radius:10px;opacity:.8}
.t1{background:linear-gradient(135deg,#3b3f47,#24272d)}.t2{background:linear-gradient(135deg,#2c3a33,#1e2622)}
.t3{background:linear-gradient(135deg,#40353a,#272225)}.t4{background:linear-gradient(135deg,#2f3442,#1e212a)}
.focus{border-radius:12px;box-shadow:0 0 0 5px #fff,0 24px 60px rgba(0,0,0,.7)}
.banner{position:relative;background:#16181C;border-radius:10px;overflow:hidden;display:flex;align-items:center;
box-shadow:inset 0 0 0 1px rgba(255,255,255,.06);justify-content:center}
.br{width:calc(var(--u)*30);height:calc(var(--u)*30);flex:none;display:flex}
.wm{margin-left:calc(var(--u)*5);margin-right:calc(var(--u)*-0.8);font-size:calc(var(--u)*7.4);font-weight:400;letter-spacing:.1em}
.big{display:flex;align-items:center;justify-content:center;gap:90px;height:720px}
.big .banner{border-radius:28px}
.icons{display:flex;align-items:flex-end;gap:40px}.smalls{display:flex;flex-direction:column;align-items:center;
gap:36px}
.icon{border-radius:22%;background:#16181C;display:flex;align-items:center;justify-content:center;overflow:hidden;
box-shadow:inset 0 0 0 1px rgba(255,255,255,.08)}
.ir{width:72%;height:72%;display:flex}
.splash{height:1080px;display:flex;flex-direction:column;align-items:center;justify-content:center}
.sr{width:260px;height:260px;display:flex}
.swm{margin-top:48px;font-size:64px;font-weight:300;letter-spacing:.14em}`;

(async () => {
  const browser = await chromium.launch();
  for (const [opt, file] of [['A', '01-option-a-glowing-rings.png'], ['B', '02-option-b-clean-rings.png']]) {
    const tab = await browser.newPage({ viewport: { width: 1920, height: 2236 } });
    await tab.setContent(`<html><head><style>${css}</style></head><body>${launcher(opt)}${big(opt)}${splash(opt)}
      </body></html>`, { waitUntil: 'load' });
    await tab.evaluate(() => document.fonts.ready);
    await tab.screenshot({ path: path.join(__dirname, file), fullPage: true });
    await tab.close();
  }
  await browser.close();
})();
