// New Supernova mark (owner, 14 Nov 2026): the glowing space-blue rings retire. Two open rings, white on charcoal;
// the outer turns clockwise and the inner anticlockwise, and the same mark is the app's one loading ring.
// Rows: 1 plain arcs; 2 arcs with a bright point at each leading end (echo of the old glowing points);
// 3 comet arcs that fade from head to tail. Each row: icon, Shield banner, splash, Preparing playback with the ring,
// then four moments of the spin (outer +40° and inner -40° per step). Made-up title. Light frame = picture edge.
// Run: FONT_DIR=<roboto woff2 dir> node render-marks.js
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
  return `M${x0.toFixed(2)} ${y0.toFixed(2)}A${r} ${r} 0 ${a1 - a0 > 180 ? 1 : 0} 1 ${x1.toFixed(2)} ${y1.toFixed(2)}`;
};
// One ring: an arc of `len` degrees whose head is at `head`; `dir` 1 = clockwise travel, -1 = anticlockwise.
const ringPath = (style, r, w, head, len, dir) => {
  const a0 = dir > 0 ? head - len : head;
  const a1 = dir > 0 ? head : head + len;
  if (style === 3) {
    let out = '';
    const n = 72;
    for (let i = 0; i < n; i++) {
      const s = a0 + len * i / n;
      const e = a0 + len * (i + 1) / n + 0.6;
      const t = dir > 0 ? (i + 1) / n : 1 - i / n;
      out += `<path d="${arc(r, s, e)}" stroke="#fff" stroke-opacity="${(0.04 + 0.96 * t * t).toFixed(3)}"
        stroke-width="${w}" fill="none" stroke-linecap="butt"/>`;
    }
    const [hx, hy] = pt(r, head);
    return out + `<circle cx="${hx}" cy="${hy}" r="${w / 2}" fill="#fff"/>`;
  }
  let out = `<path d="${arc(r, a0, a1)}" stroke="#fff" stroke-width="${w}" fill="none" stroke-linecap="round"/>`;
  if (style === 2) {
    const [hx, hy] = pt(r, head);
    out += `<circle cx="${hx}" cy="${hy}" r="${w * 1.25}" fill="#fff"/>
      <circle cx="${hx}" cy="${hy}" r="${w * 2.6}" fill="#fff" opacity=".16"/>`;
  }
  return out;
};
const mark = (style, step = 0) => `<svg viewBox="0 0 100 100" width="100%" height="100%" overflow="visible">
  ${ringPath(style, 40, 6, 45 + step * 40, style === 3 ? 250 : 280, 1)}
  ${ringPath(style, 24, 5, 225 - step * 40, style === 3 ? 250 : 280, -1)}</svg>`;

const icon = s => `<div class="icon"><div class="im">${mark(s)}</div></div>`;
const banner = s => `<div class="banner"><div class="bm">${mark(s)}</div><div class="wm">SUPERNOVA</div></div>`;
const splash = s => `<div class="splash"><div class="sm">${mark(s)}</div><div class="swm">SUPERNOVA</div></div>`;
const prep = s => `<div class="prep"><div class="title">The Long Field</div><div class="pm">${mark(s, 1)}</div></div>`;
const frames = s => `<div class="frames">${[0, 1, 2, 3].map(i => `<div class="fr">${mark(s, i)}</div>`).join('')}</div>`;
const row = s => `<section>${icon(s)}${banner(s)}${splash(s)}${prep(s)}${frames(s)}</section>`;

const css = `${font(300)}${font(400)}${font(700)}*{box-sizing:border-box;margin:0}
body{width:1920px;font-family:R;color:#fff;background:#16181C;position:relative}
body:after{content:"";position:fixed;inset:0;border:6px solid #8a919c;z-index:99;pointer-events:none}
section{display:flex;align-items:center;gap:34px;padding:56px 50px;height:380px}
section+section{border-top:8px solid #8a919c}
.icon{width:200px;height:200px;border-radius:22%;background:#16181C;display:flex;align-items:center;
justify-content:center;box-shadow:0 0 0 2px rgba(255,255,255,.12);flex:none}.im{width:68%;height:68%}
.banner,.splash,.prep{width:400px;height:225px;border-radius:14px;flex:none;overflow:hidden;position:relative;
box-shadow:0 0 0 2px rgba(255,255,255,.12)}
.banner{background:#16181C;display:flex;align-items:center;justify-content:center;gap:22px}
.bm{width:96px;height:96px}.wm{font-size:34px;letter-spacing:.1em;margin-right:-3px}
.splash{background:#16181C;display:flex;flex-direction:column;align-items:center;justify-content:center}
.sm{width:64px;height:64px}.swm{margin-top:14px;font-size:16px;font-weight:300;letter-spacing:.14em}
.prep{background:radial-gradient(ellipse at 70% 40%,#5d5a52 0%,#2c2b2e 45%,#121316 90%)}
.prep:before{content:"";position:absolute;inset:0;background:rgba(0,0,0,.45)}
.title{position:absolute;left:20px;top:18px;font-weight:700;font-size:22px;letter-spacing:.02em}
.pm{position:absolute;left:50%;top:50%;width:56px;height:56px;margin:-28px 0 0 -28px}
.frames{display:grid;grid-template-columns:repeat(2,100px);gap:18px}.fr{width:100px;height:100px}`;

(async () => {
  const browser = await chromium.launch();
  const tab = await browser.newPage({ viewport: { width: 1920, height: 1156 } });
  await tab.setContent(`<html><head><style>${css}</style></head><body>${row(1)}${row(2)}${row(3)}</body></html>`,
    { waitUntil: 'load' });
  await tab.evaluate(() => document.fonts.ready);
  await tab.screenshot({ path: path.join(__dirname, '03-new-mark-three-versions.png'), fullPage: true });
  await browser.close();
})();
