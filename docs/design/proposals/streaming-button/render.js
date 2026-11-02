// Proposal (2 Nov 2026): one generic icon for "play on a streaming service" buttons on the details page.
// Uses the approved button style (black 72% at rest, white box + black text on focus). Run: node render.js
const path = require('path');
const { chromium } = require('playwright');

const icons = {
  A: ['Open externally', 'box with an arrow leaving it — "this opens another app"',
      '<path d="M14 4h6v6"/><path d="M20 4l-9 9"/><path d="M18 14v4.5A1.5 1.5 0 0 1 16.5 20h-11A1.5 1.5 0 0 1 4 18.5v-11A1.5 1.5 0 0 1 5.5 6H10"/>'],
  B: ['Play elsewhere', 'play triangle with a small outward arrow',
      '<path d="M5 4.5v13l10-6.5z"/><path d="M16 6h4v4"/><path d="M20 6l-4.5 4.5"/>'],
  C: ['Screen + play', 'a TV with a play mark — "watch on a service"',
      '<rect x="2.5" y="4" width="19" height="13" rx="2"/><path d="M10 8.2v5.6l4.6-2.8z"/><path d="M8 21h8"/>'],
  D: ['Stream', 'play mark with signal arcs — streaming',
      '<path d="M9 8.5v7l6-3.5z"/><path d="M5.6 6.6a8 8 0 0 0 0 10.8M18.4 6.6a8 8 0 0 1 0 10.8"/><path d="M3 4a11.5 11.5 0 0 0 0 16M21 4a11.5 11.5 0 0 1 0 16"/>'],
  E: ['Cloud play', 'a cloud with a play mark — online, not on your drive',
      '<path d="M7 18.5h10.5a4 4 0 0 0 .6-7.95A6 6 0 0 0 6.6 9.2 4.7 4.7 0 0 0 7 18.5z"/><path d="M10.6 11v5l4-2.5z"/>'],
  F: ['Launch', 'arrow out of a bracket — leaving Supernova',
      '<path d="M10 4H6a2 2 0 0 0-2 2v12a2 2 0 0 0 2 2h4"/><path d="M14 8l5 4-5 4"/><path d="M19 12H9"/>'],
};
const resume = '<path d="M7 4.5v15l12-7.5z"/>';
const more = '<circle cx="5" cy="12" r="1.6" fill="currentColor"/><circle cx="12" cy="12" r="1.6" fill="currentColor"/><circle cx="19" cy="12" r="1.6" fill="currentColor"/>';
const svg = (d, s = 30) => `<svg width="${s}" height="${s}" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round">${d}</svg>`;

const css = `*{box-sizing:border-box;margin:0;padding:0}
body{width:1920px;height:1080px;overflow:hidden;font-family:Roboto,"Noto Sans",Arial,sans-serif;color:#fff;background:#05080d;position:relative}
.bk{display:inline-flex;align-items:center;gap:14px;height:72px;padding:0 28px;border-radius:12px;font-size:26px;font-weight:500;color:#fff;background:rgba(0,0,0,.72);border:2px solid transparent}
.bk.focus{background:#f4f6fb;color:#0b0d12;box-shadow:0 6px 18px rgba(0,0,0,.45)}
.scene{background:linear-gradient(100deg,rgba(5,8,13,.92) 0%,rgba(5,8,13,.75) 40%,rgba(5,8,13,.2) 75%),radial-gradient(ellipse 900px 600px at 78% 40%,#c48a52,#5a3a22 55%,#140c08)}
.h{color:#9cc9f0;font-size:17px;font-weight:700;letter-spacing:.8px}`;
const doc = b => `<!doctype html><html><head><meta charset="utf-8"><style>${css}</style></head><body>${b}</body></html>`;

// 1. Icon designs, large, on both boxes.
const iconSheet = doc(`<div style="padding:40px 50px">
<div style="font-size:32px;font-weight:700;margin-bottom:6px">Streaming button icon — six designs</div>
<div style="font-size:19px;color:#b9c7e8;margin-bottom:28px">Each shown large, then in the button at rest (black) and focused (white). The label is the provider's name.</div>
<div style="display:grid;grid-template-columns:repeat(3,1fr);gap:26px">
${Object.entries(icons).map(([k, [name, note, d]]) => `<div style="background:#0d1424;border-radius:16px;padding:22px 24px">
  <div style="display:flex;align-items:center;gap:22px;margin-bottom:18px"><div style="width:96px;height:96px;border-radius:14px;background:#161f36;display:flex;align-items:center;justify-content:center">${svg(d, 64)}</div>
  <div><div style="font-size:26px;font-weight:700">${k} · ${name}</div><div style="font-size:17px;color:#b9c7e8;margin-top:6px;max-width:330px">${note}</div></div></div>
  <div style="display:flex;gap:14px"><span class="bk">${svg(d)}Netflix</span><span class="bk focus">${svg(d)}Netflix</span></div></div>`).join('')}
</div></div>`);

// 2. In context: Dune: Part Two details page, button row at rest and with the streaming button focused.
const row = (d, focusStream) => `<div style="display:flex;gap:18px"><span class="bk">${svg(resume)}Resume</span><span class="bk ${focusStream ? 'focus' : ''}">${svg(d)}Netflix</span><span class="bk">${svg(more)}More</span></div>`;
const page = (keys, title) => doc(`<div class="scene" style="position:absolute;inset:0"></div>
<div style="position:absolute;left:84px;top:60px;font-size:84px;font-weight:900;letter-spacing:6px">DUNE</div>
<div style="position:absolute;left:88px;top:160px;font-size:30px;letter-spacing:14px">PART TWO</div>
<div style="position:absolute;left:88px;top:225px;font-size:23px;color:#dfe7f2">12A · 2024 · Science Fiction, Adventure · 2h 47m · AAC · Stereo</div>
<div style="position:absolute;left:88px;top:20px;right:60px;text-align:right;font-size:20px;color:#b9c7e8">${title}</div>
<div style="position:absolute;left:88px;top:290px;display:grid;grid-template-columns:auto auto;column-gap:70px;row-gap:34px;align-items:center">
${keys.map(k => `<div class="h">${k} · ${icons[k][0].toUpperCase()}</div><div></div>
  <div>${row(icons[k][2], false)}</div><div>${row(icons[k][2], true)}</div>`).join('')}
</div>`);

(async () => {
  const browser = await chromium.launch();
  const p = await browser.newPage({ viewport: { width: 1920, height: 1080 } });
  const shots = [
    ['01-icon-designs.png', iconSheet],
    ['02-in-context-A-B-C.png', page(['A', 'B', 'C'], 'Details page · left: at rest · right: streaming button focused')],
    ['03-in-context-D-E-F.png', page(['D', 'E', 'F'], 'Details page · left: at rest · right: streaming button focused')],
  ];
  for (const [n, h] of shots) { await p.setContent(h); await p.screenshot({ path: path.join(__dirname, n) }); }
  await browser.close();
  console.log('rendered', shots.length);
})();
