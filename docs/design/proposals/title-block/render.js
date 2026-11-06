// Proposal (owner review, 6 Nov 2026): where the title logo, information line and age rating sit on Preparing
// playback and the HUD. A: all stacked top left. B: rating alone top left; logo and information bottom left.
// Run: FONT_DIR=<roboto woff2 dir> node render.js
const fs = require('fs');
const path = require('path');
const { chromium } = require('playwright');
const fontDir = process.env.FONT_DIR;
const font = w => `@font-face{font-family:R;font-weight:${w};src:url(data:font/woff2;base64,${fs.readFileSync(
  path.join(fontDir, `roboto-latin-${w}-normal.woff2`)).toString('base64')}) format('woff2')}`;
const logo = '<div class="logo">MOBLAND</div>';
const info = '<div class="info">Season 2 · Episode 8 · We Don\'t Talk Anymore</div>';
const rating = '<div class="rating"><b>15</b><span>Strong language, violence</span></div>';
const spin = '<div class="spin"><i></i>Preparing playback…</div>';
const bar = `<div class="hud"><div class="bubble">00:17:46<br><small>Chapter 05</small></div>
  <div class="track"><i></i><b></b></div><div class="times">00:42:37 · Ends at 17:14</div></div>`;
const screen = (kind, body) => `<div class="scr ${kind}"><div class="shade"></div>${body}</div>`;
const optionA = `
${screen('art', `<div class="tl">${logo}${info}${rating}${spin}</div>`)}
${screen('video', `<div class="tl">${logo}${info}${rating}</div>${bar}`)}`;
const optionB = `
${screen('art', `<div class="tlr">${rating}</div><div class="bl">${logo}${info}${spin}</div>`)}
${screen('video', `<div class="tlr">${rating}</div><div class="bl hudup">${logo}${info}</div>${bar}`)}`;
const css = `${font(400)}${font(500)}*{box-sizing:border-box;margin:0}
body{width:1920px;height:1080px;overflow:hidden;font-family:R;color:#fff;background:#0b0d12;display:flex;
flex-direction:column;gap:20px;padding:20px 0}
.scr{position:relative;width:1920px;height:520px;overflow:hidden;transform-origin:top left}
.scr.art{background:linear-gradient(120deg,#3c5a63,#22333a 55%,#141c20)}
.scr.video{background:linear-gradient(120deg,#5e8a95,#2c4a52 60%,#182a2f)}
.shade{position:absolute;inset:0;background:linear-gradient(90deg,rgba(0,0,0,.55),transparent 55%)}
.tl{position:absolute;left:72px;top:44px}.tlr{position:absolute;left:72px;top:44px}
.bl{position:absolute;left:72px;bottom:56px}.bl.hudup{bottom:170px}
.logo{font-family:Georgia,serif;font-size:76px;letter-spacing:2px;color:#e8344e;line-height:1}
.info{font-size:26px;margin-top:12px;color:#e8edf6}
.rating{display:flex;align-items:center;gap:16px;margin-top:22px;font-size:24px;color:#e8edf6}
.tlr .rating{margin-top:0}
.rating b{font-size:26px;font-weight:500;padding:6px 14px;border-radius:10px;box-shadow:inset 0 0 0 2px #fff}
.spin{display:flex;align-items:center;gap:16px;margin-top:26px;font-size:24px;color:#dfe5f1}
.spin i{width:30px;height:30px;border-radius:50%;border:3px solid rgba(255,255,255,.3);border-top-color:#fff}
.hud{position:absolute;left:72px;right:72px;bottom:44px}
.bubble{position:absolute;left:520px;bottom:46px;background:rgba(22,24,28,.9);border-radius:14px;padding:8px 18px;
font-size:24px;text-align:center}.bubble small{font-size:18px;color:#c9d3e6}
.track{position:relative;height:8px;border-radius:4px;background:rgba(255,255,255,.35);margin-right:380px}
.track i{position:absolute;left:0;top:0;bottom:0;width:42%;background:#fff;border-radius:4px}
.track b{position:absolute;left:42%;top:-9px;width:26px;height:26px;margin-left:-13px;border-radius:50%;background:#fff}
.times{position:absolute;right:0;bottom:-10px;font-size:24px}`;
(async () => {
  const browser = await chromium.launch();
  const tab = await browser.newPage({ viewport: { width: 1920, height: 1080 } });
  for (const [f, body] of [['01-a-top-left.png', optionA], ['02-b-rating-alone.png', optionB]]) {
    await tab.setContent(`<html><head><style>${css}</style></head><body>${body}</body></html>`, { waitUntil: 'load' });
    await tab.evaluate(() => document.fonts.ready);
    await tab.screenshot({ path: path.join(__dirname, f) });
  }
  await browser.close();
})();
