// Proposal (owner review, 6 Nov 2026): track names as a plain name plus detail pills, in the 0.157 panel look.
// Run: FONT_DIR=<roboto woff2 dir> node render.js
const fs = require('fs');
const path = require('path');
const { chromium } = require('playwright');
const fontDir = process.env.FONT_DIR;
const font = w => `@font-face{font-family:R;font-weight:${w};src:url(data:font/woff2;base64,${fs.readFileSync(
  path.join(fontDir, `roboto-latin-${w}-normal.woff2`)).toString('base64')}) format('woff2')}`;
const flag = '<svg width="44" height="30" viewBox="0 0 60 40"><rect width="60" height="40" fill="#012169"/>'
  + '<path d="M0 0L60 40M60 0L0 40" stroke="#fff" stroke-width="8"/><path d="M0 0L60 40M60 0L0 40" stroke="#C8102E"'
  + ' stroke-width="3"/><path d="M30 0v40M0 20h60" stroke="#fff" stroke-width="12"/><path d="M30 0v40M0 20h60"'
  + ' stroke="#C8102E" stroke-width="7"/></svg>';
const none = '<svg width="44" height="30" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8">'
  + '<circle cx="12" cy="12" r="9"/><path d="M5.6 5.6l12.8 12.8"/></svg>';
const tick = '<svg width="30" height="30" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.2"'
  + ' stroke-linecap="round"><path d="M5 12l5 5 9-10"/></svg>';
const row = (mark, name, pills, chosen, focus) => `<div class="ci ${focus ? 'focus' : ''}">${mark}<span class="nm">${name}
  </span>${pills.map(p => `<i class="pill">${p}</i>`).join('')}<span class="tick">${chosen ? tick : ''}</span></div>`;
const panel = (title, rows) => `<div class="panel"><div class="ph">‹&nbsp; ${title}</div>${rows.join('')}</div>`;
const audio = panel('Audio Track', [
  row(flag, 'English', ['Original', 'Dolby Digital Plus', '5.1'], true, true),
  row(flag, 'English', ['Audio description', 'Dolby Digital', 'Stereo'], false, false)]);
const subs = panel('Subtitle Track', [
  row(none, 'None', [], false, false),
  row(flag, 'English', ['Original'], true, true),
  row(flag, 'English', ['Original', 'SDH'], false, false)]);
const css = `${font(400)}${font(500)}*{box-sizing:border-box;margin:0}
body{width:1920px;height:1080px;overflow:hidden;font-family:R;color:#fff;
background:linear-gradient(160deg,#6a7f86,#2f3a3f 60%,#1b2124)}
.panel{position:absolute;right:96px;top:96px;background:rgba(22,24,28,.9);border-radius:28px;padding:22px 18px 18px;
white-space:nowrap}
.ph{font-size:32px;font-weight:500;padding:6px 18px 18px}
.ci{display:flex;align-items:center;gap:18px;height:84px;padding:0 22px;border-radius:20px;font-size:30px;
font-weight:500}
.ci.focus{background:#F4F6FB;color:#0B0D12}.nm{margin-right:8px}
.pill{font-style:normal;font-size:22px;font-weight:500;padding:6px 14px;border-radius:14px;color:#dfe5f1;
box-shadow:inset 0 0 0 2px rgba(255,255,255,.30)}
.ci.focus .pill{color:#0B0D12;box-shadow:inset 0 0 0 2px rgba(11,13,18,.35)}
.tick{margin-left:auto;padding-left:40px;width:80px;display:inline-flex;justify-content:flex-end}`;
(async () => {
  const browser = await chromium.launch();
  const tab = await browser.newPage({ viewport: { width: 1920, height: 1080 } });
  for (const [f, body] of [['01-audio-track.png', audio], ['02-subtitle-track.png', subs]]) {
    await tab.setContent(`<html><head><style>${css}</style></head><body>${body}</body></html>`, { waitUntil: 'load' });
    await tab.evaluate(() => document.fonts.ready);
    await tab.screenshot({ path: path.join(__dirname, f) });
  }
  await browser.close();
})();
