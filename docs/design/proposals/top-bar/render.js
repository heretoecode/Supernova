// Proposal (0.156, owner 4 Nov 2026): one top-bar scroll effect for Home, Movies, TV Shows and custom libraries.
// At the top: nothing. Scrolled: page content passes behind a charcoal fade under the bar. Run: FONT_DIR=... node render.js
const fs = require('fs'), path = require('path'), { chromium } = require('playwright');
const out = __dirname, fontDir = process.env.FONT_DIR;
const font = w => `@font-face{font-family:R;font-weight:${w};src:url(data:font/woff2;base64,${fs.readFileSync(path.join(fontDir, `roboto-latin-${w}-normal.woff2`)).toString('base64')}) format('woff2')}`;
const nav = on => `<div class="nav"><span class="brand">SUPERNOVA</span>${['Home', 'Movies', 'TV Shows', 'Documentaries'].map(t => `<span class="${t === on ? 'on' : ''}">${t}</span>`).join('')}<span class="sp"></span><span>Network &amp; Files</span><span class="clock">14:50</span></div>`;
const row = (title, cols, y) => `<div class="row" style="top:${y}px"><div class="rt">${title}</div><div class="cards">${cols.map(c => `<div class="card" style="background:linear-gradient(160deg,${c},#0d1017 85%)"></div>`).join('')}</div></div>`;
const rows = off => row('Continue Watching', ['#33506f', '#4a3a2a', '#3e4f45', '#5a4a36', '#2b3550'], 560 - off) + row('Recently Added', ['#566233', '#6b3a2a', '#5c2a1c', '#2f5a6b', '#7a6a52'], 860 - off) + row('Watch Next', ['#3b2f28', '#2a2a2f', '#3e5470', '#5b4630', '#40403a'], 1160 - off) + row('Trending', ['#4a2f5a', '#2f4a5a', '#5a4a2f', '#2f5a3a', '#5a2f3a'], 1460 - off);
const hero = off => `<div class="hero" style="top:${110 - off}px"><div class="hcopy"><div class="logo">THE HERO TITLE</div><div class="meta">2026 · Drama</div></div></div>`;
const art = (i) => { const h = [210, 25, 160, 340, 45, 280, 120, 0][i % 8];
  return `background:radial-gradient(circle at 70% 35%,hsl(${h} 70% 70%) 0 12%,transparent 13%),radial-gradient(ellipse 60% 40% at 30% 75%,hsl(${(h + 40) % 360} 50% 35%),transparent 70%),linear-gradient(160deg,hsl(${h} 45% 45%),hsl(${(h + 200) % 360} 40% 12%) 85%)`; };
const artRow = (title, y, k) => `<div class="row" style="top:${y}px"><div class="rt">${title}</div><div class="cards">${[0, 1, 2, 3, 4].map(i => `<div class="card art" style="${art(i + k)}"><span>Title ${i + 1}</span></div>`).join('')}</div></div>`;
const shots = [
  ['04-scrolled-artwork-behind.png', nav('Home') + artRow('Recently Added', 20, 0) + artRow('Watch Next', 320, 3) + artRow('Trending', 620, 5), 'soft', 'Scrolled with real artwork: posters stay visible as they slide under the bar, fading out towards the top; tabs stay readable'],
  ['01-at-top.png', nav('Home') + hero(0) + rows(0), false, 'At the top of the page: no effect at all — the bar sits on the page background'],
  ['02-scrolled.png', nav('Home') + hero(430) + rows(430), true, 'Scrolled: rows pass behind the bar; a charcoal fade (no blue, no box) keeps the tabs readable; it eases in over the first ~120 px'],
  ['03-custom-library-scrolled.png', nav('Documentaries') + rows(330), true, 'Custom libraries (e.g. Documentaries) behave exactly the same: the page header scrolls with the content, nothing fixed under the bar'],
];
const css = `${font(400)}${font(500)}*{box-sizing:border-box;margin:0}
body{width:1920px;height:1080px;overflow:hidden;font-family:R;color:#fff;position:relative;background:radial-gradient(ellipse 70% 60% at 70% 75%,#1d4fd8 0%,rgba(29,79,216,0) 70%),linear-gradient(180deg,#05081a 0%,#0a1650 45%,#10248a 100%)}
.nav{position:absolute;left:56px;right:56px;top:28px;height:56px;display:flex;align-items:center;gap:30px;font-size:34px;color:#e8edf8;z-index:5}.nav .brand{font-size:36px;margin-right:40px;letter-spacing:1px}.nav .sp{flex:1}.nav .on{font-weight:500;color:#fff;border-bottom:3px solid #fff;padding-bottom:4px}.nav .clock{margin-left:30px}
.fade{position:absolute;left:0;right:0;top:0;height:190px;z-index:4;background:linear-gradient(180deg,rgba(22,24,28,.97) 0%,rgba(22,24,28,.9) 45%,rgba(22,24,28,0) 100%)}
.fade.soft{height:230px;background:linear-gradient(180deg,rgba(22,24,28,.88) 0%,rgba(22,24,28,.62) 40%,rgba(22,24,28,0) 100%)}
.card.art{position:relative}.card.art span{position:absolute;left:16px;bottom:14px;font-size:22px;font-weight:500;text-shadow:0 1px 4px #000}
.hero{position:absolute;left:56px;right:56px;height:400px;border-radius:26px;background:linear-gradient(90deg,#0c0f16 30%,rgba(12,15,22,.2)),linear-gradient(140deg,#6d5a4a,#22283a)}
.hcopy{position:absolute;left:44px;top:60px}.logo{font-family:Georgia,serif;font-size:60px;color:#f0dfc0}.meta{font-size:24px;margin-top:14px}
.row{position:absolute;left:56px;right:0}.rt{font-size:30px;font-weight:500;margin-bottom:14px}.cards{display:flex;gap:22px}.card{width:360px;height:200px;border-radius:18px;flex:none}
.label{position:absolute;right:56px;bottom:14px;font-size:19px;color:#c9d3e6;background:rgba(5,8,26,.7);padding:4px 10px;border-radius:8px;z-index:6}`;
(async () => { const b = await chromium.launch(); const t = await b.newPage({ viewport: { width: 1920, height: 1080 } });
  for (const [f, body, faded, label] of shots) {
    await t.setContent(`<html><head><style>${css}</style></head><body>${faded ? `<div class="fade${faded === 'soft' ? ' soft' : ''}"></div>` : ''}${body}<div class="label">${label}</div></body></html>`, { waitUntil: 'load' });
    await t.evaluate(() => document.fonts.ready); await t.screenshot({ path: path.join(out, f) }); }
  await b.close(); })();
