// Owner photos 5 Nov 2026: NOVA's seek spinner sat half off screen. Two replacements in the approved HUD style.
// Run: FONT_DIR=<roboto woff2 dir> node render.js
const fs = require('fs');
const path = require('path');
const { chromium } = require('playwright');
const fontDir = process.env.FONT_DIR;
const font = w => `@font-face{font-family:R;font-weight:${w};src:url(data:font/woff2;base64,${fs.readFileSync(path.join(fontDir, `roboto-latin-${w}-normal.woff2`)).toString('base64')}) format('woff2')}`;
const ring = s => `<svg width="${s}" height="${s}" viewBox="0 0 24 24"><circle cx="12" cy="12" r="9" fill="none" stroke="rgba(255,255,255,.25)" stroke-width="2.6"/><path d="M12 3a9 9 0 0 1 9 9" fill="none" stroke="#fff" stroke-width="2.6" stroke-linecap="round"/></svg>`;
const hud = (bubble, dot) => `
<div class="title"><div class="t">Dark Matter</div><div class="e">Season 2 · Episode 10 · No Quiet Life</div></div>
<div class="bar">
  <div class="btn"><svg width="34" height="34" viewBox="0 0 24 24"><path d="M8 5v14M16 5v14" stroke="#fff" stroke-width="2.4" stroke-linecap="round"/></svg></div>
  <div class="track"><div class="played"></div><div class="buf"></div>${dot}${bubble}</div>
  <div class="btn wide"><span>Settings</span></div>
</div>`;
const plainDot = `<div class="dot"></div>`;
const ringDot = `<div class="ringdot">${ring(40)}</div>`;
const bubble = inner => `<div class="bubble">${inner}</div>`;
const a = hud(bubble(`<div class="time">${ring(30)}<span>00:25:57</span></div><div class="ch">Chapter 2 · loading</div>`), plainDot);
const b = hud(bubble(`<div class="time"><span>00:25:57</span></div><div class="ch">Chapter 2</div>`), ringDot);
const css = `${font(400)}${font(500)}*{box-sizing:border-box;margin:0}
body{width:1920px;height:1080px;overflow:hidden;font-family:R;color:#fff;position:relative;
 background:radial-gradient(ellipse 60% 70% at 65% 40%,#8a7a72 0%,#4a3c36 45%,#1c1614 100%)}
.shade{position:absolute;left:0;right:0;bottom:0;height:460px;background:linear-gradient(0deg,rgba(0,0,0,.85),rgba(0,0,0,0))}
.title{position:absolute;left:80px;bottom:300px}.t{font-size:64px;font-weight:500}.e{font-size:28px;color:#dfe5f1;margin-top:8px}
.bar{position:absolute;left:70px;right:70px;bottom:110px;height:76px;display:flex;align-items:center;gap:26px}
.btn{width:78px;height:72px;border-radius:24px;background:rgba(22,24,28,.97);border:2px solid rgba(255,255,255,.18);display:flex;align-items:center;justify-content:center;flex:none}
.btn.wide{width:164px;font-size:26px;font-weight:500}
.track{flex:1;height:10px;border-radius:5px;background:rgba(255,255,255,.35);position:relative}
.played{position:absolute;left:0;top:0;bottom:0;width:58%;background:#fff;border-radius:5px}
.buf{position:absolute;left:58%;top:0;bottom:0;width:6%;background:rgba(255,255,255,.65)}
.dot{position:absolute;left:58%;top:50%;width:30px;height:30px;margin:-15px 0 0 -15px;border-radius:50%;background:#fff;box-shadow:0 0 0 12px rgba(255,255,255,.25)}
.ringdot{position:absolute;left:58%;top:50%;width:40px;height:40px;margin:-20px 0 0 -20px;border-radius:50%;background:rgba(22,24,28,.9)}
.bubble{position:absolute;left:58%;bottom:44px;transform:translateX(-50%);background:rgba(22,24,28,.97);border:2px solid rgba(255,255,255,.18);border-radius:16px;padding:12px 24px;text-align:center;white-space:nowrap}
.time{display:flex;align-items:center;gap:12px;justify-content:center;font-size:44px;font-weight:500}.ch{font-size:26px;color:#C9D3E6;margin-top:4px}`;
(async () => {
  const browser = await chromium.launch();
  const tab = await browser.newPage({ viewport: { width: 1920, height: 1080 } });
  for (const [f, body] of [['01-ring-in-time-bubble.png', a], ['02-ring-replaces-dot.png', b]]) {
    await tab.setContent(`<html><head><style>${css}</style></head><body><div class="shade"></div>${body}</body></html>`, { waitUntil: 'load' });
    await tab.evaluate(() => document.fonts.ready);
    await tab.screenshot({ path: path.join(__dirname, f) });
  }
  await browser.close();
})();
