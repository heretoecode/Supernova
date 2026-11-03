// Proposal (0.154 walk-through): pop-up menu background — today's navy glass vs the buttons' black.
// Real 1080p sizes; simulated busy artwork with text behind. Run: FONT_DIR=<roboto woff2 dir> node render.js
const fs = require('fs');
const path = require('path');
const { chromium } = require('playwright');
const out = __dirname;
const fontDir = process.env.FONT_DIR;
const font = w => `@font-face{font-family:R;font-weight:${w};src:url(data:font/woff2;base64,${fs.readFileSync(path.join(fontDir, `roboto-latin-${w}-normal.woff2`)).toString('base64')}) format('woff2')}`;

const icons = {
  film: '<rect x="3" y="4" width="18" height="16" rx="1.5"/><path d="M7 4v16M17 4v16M3 9h4M3 15h4M17 9h4M17 15h4"/>',
  minus: '<circle cx="12" cy="12" r="9"/><path d="M8 12h8"/>',
  row: '<path d="M4 6h12M4 11h12M4 16h7M17 14v6M14 17h6"/>',
  remove: '<path d="M5 4v16M9 4v16M13 4v12M15 10h6"/>',
  trash: '<path d="M4 7h16M9 7V4h6v3M6 7l1 13h10l1-13M10 11v6M14 11v6"/>',
  subs: '<rect x="3" y="5" width="18" height="14" rx="2"/><path d="M6 13h5M13 13h5M6 16h8"/>',
};
const ic = k => `<svg width="34" height="34" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.7" stroke-linecap="round" stroke-linejoin="round">${icons[k]}</svg>`;
const heading = t => `<div class="h"><span>${t}</span><i></i></div>`;
const row = (k, t, f) => `<div class="r${f ? ' f' : ''}">${ic(k)}<span>${t}</span></div>`;
const menu = `${heading('Playback')}${row('film', 'Restart Movie', true)}${heading('Library')}${row('minus', 'Mark Watched')}${row('row', 'Add to Row')}${row('minus', 'Report Incorrect Match')}${row('remove', 'Remove From Library')}${heading('Files &amp; Media')}${row('trash', 'Delete')}${row('subs', 'Subtitles and Artwork')}`;

const variants = [
  ['Today — navy glass 97%', 'rgba(26,31,92,.97)', 'rgba(255,255,255,.40)', '#8fb4e8', 'rgba(143,180,232,.55)', ''],
  ['Button black, exactly — 72%', 'rgba(0,0,0,.72)', 'rgba(255,255,255,.18)', '#b7bfcc', 'rgba(255,255,255,.22)', 'Artwork and text behind show through.'],
  ['Black 97%', 'rgba(0,0,0,.97)', 'rgba(255,255,255,.18)', '#b7bfcc', 'rgba(255,255,255,.22)', 'Same black as the buttons, as solid as today.'],
  ['Charcoal 97%', 'rgba(22,24,28,.97)', 'rgba(255,255,255,.22)', '#b7bfcc', 'rgba(255,255,255,.22)', 'A softer black with a faint edge.'],
];
const art = `background:
  radial-gradient(ellipse 30% 40% at 85% 30%, rgba(80,200,230,.9), rgba(0,0,0,0) 70%),
  radial-gradient(ellipse 35% 50% at 15% 70%, rgba(140,80,40,.95), rgba(0,0,0,0) 70%),
  repeating-linear-gradient(35deg, #6b4a2a 0 22px, #8a6236 22px 44px, #4e3520 44px 66px),
  #3b2a1a`;
const cell = ([name, fill, edge, hcol, line, note]) => `<div class="cell"><div class="cap">${name}<small>${note}</small></div>
  <div class="scene" style="${art}"><div class="behind">and his friends set off on a quest to find Pirate One-Eyed Willy's
  treasure, in the hope of saving their homes from demolition. However, they run into a family of escaped
  criminals who chase the kids to reach the treasure first.</div>
  <div class="menu" style="background:${fill};border-color:${edge};--h:${hcol};--l:${line}">${menu}</div></div></div>`;
const css = `${font(400)}${font(500)}
*{box-sizing:border-box;margin:0}
body{width:2400px;height:1350px;overflow:hidden;background:#000;color:#fff;font-family:R;padding:34px 48px}
h1{font-weight:500;font-size:38px} p.sub{font-size:21px;color:#c9d3e6;margin:6px 0 16px}
.grid{display:grid;grid-template-columns:repeat(4,1fr);gap:22px}
.cap{font-weight:500;font-size:22px;margin-bottom:8px;min-height:62px}
.cap small{display:block;font-weight:400;font-size:17px;color:#b9c3d6;margin-top:2px}
.scene{position:relative;height:1080px;border-radius:18px;overflow:hidden}
.behind{position:absolute;left:14px;right:14px;top:160px;font-size:27px;line-height:1.45;color:#fff;font-weight:500}
.menu{position:absolute;left:20px;right:20px;top:40px;border:2px solid;border-radius:26px;padding:22px 20px 20px 20px}
.h{display:flex;align-items:center;gap:14px;color:var(--h);font-size:22px;margin:14px 6px 6px}
.h i{flex:1;height:2px;background:var(--l)}
.r{display:flex;align-items:center;gap:18px;height:68px;padding:0 18px;border-radius:24px;font-size:26px;font-weight:500;color:#fff}
.r.f{box-shadow:inset 0 0 0 3px #fff}`;
const html = `<html><head><style>${css}</style></head><body><h1>More menu background — navy vs black</h1>
<p class="sub">Same menu, focus and sizes as 0.153 (Medium labels as approved). Busy artwork and the synopsis behind, to show what bleeds through.</p>
<div class="grid">${variants.map(cell).join('')}</div></body></html>`;

(async () => {
  const browser = await chromium.launch();
  const tab = await browser.newPage({ viewport: { width: 2400, height: 1350 } });
  await tab.setContent(html, { waitUntil: 'load' });
  await tab.evaluate(() => document.fonts.ready);
  await tab.screenshot({ path: path.join(out, '01-menu-background.png') });
  await browser.close();
})();
