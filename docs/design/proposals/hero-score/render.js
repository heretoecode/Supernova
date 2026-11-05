// Proposal (0.154 walk-through): how the score appears in the hero metadata row.
// Real 1080p sizes; simulated artwork. Run: FONT_DIR=<roboto woff2 dir> node render.js
const fs = require('fs');
const path = require('path');
const { chromium } = require('playwright');
const out = __dirname;
const fontDir = process.env.FONT_DIR;
const font = w => `@font-face{font-family:R;font-weight:${w};src:url(data:font/woff2;base64,${fs.readFileSync(path.join(fontDir, `roboto-latin-${w}-normal.woff2`)).toString('base64')}) format('woff2')}`;

const score = 6.4;
const star = (fill, size = 34) => `<svg width="${size}" height="${size}" viewBox="0 0 24 24" style="vertical-align:-4px">
  <defs><linearGradient id="g${Math.round(fill * 100)}"><stop offset="${fill}" stop-color="#f5c518"/><stop offset="${fill}" stop-color="rgba(255,255,255,.28)"/></linearGradient></defs>
  <path d="M12 2.6l2.9 5.9 6.5.9-4.7 4.6 1.1 6.5L12 17.4l-5.8 3.1 1.1-6.5L2.6 9.4l6.5-.9z" fill="url(#g${Math.round(fill * 100)})"/></svg>`;
const fiveStars = s => { const v = s / 2; return [0, 1, 2, 3, 4].map(i => star(Math.max(0, Math.min(1, v - i)), 34)).join(''); };
const ring = s => { const p = s * 10, r = 15, c = 2 * Math.PI * r; const col = p >= 70 ? '#21d07a' : p >= 40 ? '#d2d531' : '#db2360';
  return `<svg width="52" height="52" viewBox="0 0 40 40" style="vertical-align:-14px"><circle cx="20" cy="20" r="19" fill="#081c22"/><circle cx="20" cy="20" r="${r}" fill="none" stroke="rgba(255,255,255,.18)" stroke-width="3"/>
  <circle cx="20" cy="20" r="${r}" fill="none" stroke="${col}" stroke-width="3" stroke-linecap="round" stroke-dasharray="${c * p / 100} ${c}" transform="rotate(-90 20 20)"/>
  <text x="20" y="24.5" text-anchor="middle" font-family="R" font-weight="500" font-size="12" fill="#fff">${Math.round(p)}<tspan font-size="6" dy="-5">%</tspan></text></svg>`; };

const options = [
  ['A — Text', `TMDB ${score}`, 'Plain and honest; matches the rest of the row.'],
  ['B — Star + number', `${star(1)} ${score}`, 'Instantly reads as a score. Source shown in Reception.', true],
  ['C — Five stars', `${fiveStars(score)}`, 'Familiar, but partial stars are hard to judge from the sofa.'],
  ['D — Score ring', `${ring(score)}`, "TMDB's own style; colour does the talking."],
];
const art = `background:
  radial-gradient(ellipse 40% 70% at 82% 50%, rgba(255,214,140,.35), rgba(0,0,0,0) 70%),
  linear-gradient(115deg,#1aa3d6 0%,#1592c4 55%,#e9c27a 56%,#f2d39a 70%,#13a0d4 71%)`;
const card = ([name, value, note, rec]) => `<div class="wrap"><div class="cap">${name}${rec ? '<span class="pill">Recommended</span>' : ''}<small>${note}</small></div>
  <div class="card" style="${art}"><div class="scrim"></div><div class="copy">
    <div class="title">BLACK BAG</div>
    <div class="meta">2025 · 1h 34m · Mystery, Thriller · <span class="score">${value}</span></div>
    <div class="tag">It takes a spy to hunt a spy.</div></div></div></div>`;
const css = `${font(400)}${font(500)}
*{box-sizing:border-box;margin:0}
body{width:1920px;height:1080px;overflow:hidden;background:#000;color:#fff;font-family:R;padding:40px 56px}
h1{font-weight:500;font-size:40px} p.sub{font-size:22px;color:#c9d3e6;margin:6px 0 22px}
.grid{display:grid;grid-template-columns:1fr 1fr;gap:22px 30px}
.cap{font-weight:500;font-size:24px;margin-bottom:10px;display:flex;align-items:baseline;gap:12px;flex-wrap:wrap}
.cap small{font-weight:400;font-size:19px;color:#b9c3d6}
.pill{background:#2f6df0;border-radius:999px;padding:2px 12px;font-size:18px}
.card{position:relative;height:380px;border-radius:24px;overflow:hidden}
.scrim{position:absolute;inset:0;background:linear-gradient(90deg,rgba(0,0,0,.42) 0%,rgba(0,0,0,.28) 30%,rgba(0,0,0,0) 58%)}
.copy{position:absolute;left:44px;top:40px;right:30%;text-shadow:0 1px 2px rgba(0,0,0,.5),0 2px 10px rgba(0,0,0,.4)}
.title{font-size:84px;line-height:.95;letter-spacing:2px;font-weight:400;color:#fff;-webkit-text-stroke:2px #fff;color:transparent;text-shadow:none;width:440px}
.meta{font-size:30px;margin-top:28px;white-space:nowrap}
.score{display:inline-flex;align-items:center;gap:6px}
.tag{font-size:30px;margin-top:16px}`;
const html = `<html><head><style>${css}</style></head><body><h1>Hero score — how should 6.4 look?</h1>
<p class="sub">Details hero for Black Bag (Home is the same without the runtime). Approved readability treatment applied. Simulated artwork.</p>
<div class="grid">${options.map(card).join('')}</div></body></html>`;

(async () => {
  const browser = await chromium.launch();
  const tab = await browser.newPage({ viewport: { width: 1920, height: 1080 } });
  await tab.setContent(html, { waitUntil: 'load' });
  await tab.evaluate(() => document.fonts.ready);
  await tab.screenshot({ path: path.join(out, '01-score-styles.png') });
  await browser.close();
})();
