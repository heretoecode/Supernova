// v10 (owner, 14 Nov 2026): library pages before anything is in them, now that onboarding never blocks the app.
// 1 Movies with nothing added yet: no toolbar (nothing to filter or sort), one message and "Add your videos".
// 2 TV Shows while the first scan runs: the app's loading ring (the mark, spinning) and a count; posters appear as
//   they are found.
// 3 The third page before it is set up: the "+" in the top bar opens it; the page explains itself and offers the same
//   "Customise page" button the page has once it is set up (the same panel opens), plus one line pointing to Files
//   and Settings. Made-up counts. Light frame = picture edge.
// Run: FONT_DIR=<roboto woff2 dir> node render-v10-empty.js
const fs = require('fs');
const path = require('path');
const { chromium } = require('playwright');
const fontDir = process.env.FONT_DIR;
const font = w => `@font-face{font-family:R;font-weight:${w};src:url(data:font/woff2;base64,${fs.readFileSync(
  path.join(fontDir, `roboto-latin-${w}-normal.woff2`)).toString('base64')}) format('woff2')}`;
const svg = (d, s = 34) => `<svg width="${s}" height="${s}" viewBox="0 0 24 24" fill="none" stroke="currentColor"
  stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round">${d}</svg>`;
const I = {
  search: '<circle cx="11" cy="11" r="7"/><path d="M20 20l-4-4"/>',
  cog: '<circle cx="12" cy="12" r="3"/><path d="M12 2v3M12 19v3M4.2 4.2l2.1 2.1M17.7 17.7l2.1 2.1M2 12h3M19 12h3'
    + 'M4.2 19.8l2.1-2.1M17.7 6.3l2.1-2.1"/>',
  plus: '<path d="M12 5v14M5 12h14"/>',
  film: '<rect x="3" y="4" width="18" height="16" rx="2"/><path d="M7 4v16M17 4v16M3 9h4M3 15h4M17 9h4M17 15h4"/>',
  edit: '<path d="M4 20h4L19 9l-4-4L4 16z"/><path d="M14 6l4 4"/>',
  folderplus: '<path d="M3 7a2 2 0 0 1 2-2h4l2 2h8a2 2 0 0 1 2 2v8a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2z"/><path d="M12 10v6M9 13h6"/>',
  page: '<rect x="4" y="3" width="16" height="18" rx="2"/><path d="M8 8h8M8 12h8M8 16h5"/>',
};
const pt = (r, a) => [50 + r * Math.cos((a - 90) * Math.PI / 180), 50 + r * Math.sin((a - 90) * Math.PI / 180)];
const arc = (r, a0, a1) => {
  const [x0, y0] = pt(r, a0);
  const [x1, y1] = pt(r, a1);
  return `M${x0} ${y0}A${r} ${r} 0 1 1 ${x1} ${y1}`;
};
const mark = `<svg viewBox="0 0 100 100" width="100%" height="100%"><path d="${arc(40, 165, 445)}" stroke="#fff"
  stroke-width="7" fill="none" stroke-linecap="round"/><path d="${arc(24, 185, 465)}" stroke="#fff" stroke-width="6"
  fill="none" stroke-linecap="round"/></svg>`;

const nav = (place, mark2) => `<div class="nav"><span class="brand">SUPERNOVA</span>${['Home', 'Movies', 'TV Shows']
  .map(t => `<span class="ni ${t === place ? mark2 : ''}">${t}</span>`).join('')}<span class="ni ${place === '+' ? mark2
  : ''}">${svg(I.plus, 34)}</span><span class="ni files">Files</span><span class="ni">${svg(I.search, 34)}</span>
  <span class="ni">${svg(I.cog, 34)}</span><span class="clock">21:40</span></div>`;
const btn = (ic, t, f) => `<span class="btn ${f ? 'focus' : ''}">${svg(I[ic], 32)}${t}</span>`;
const empty = (icon, h, p, action, note) => `<div class="empty"><div class="ei">${icon}</div><h2>${h}</h2><p>${p}</p>
  ${action || ''}${note ? `<p class="note">${note}</p>` : ''}</div>`;

const movies = `<div class="frame">${nav('Movies', 'line')}${empty(svg(I.film, 60), 'No films yet',
  'Add a folder, a drive or a network share and your films appear here.',
  btn('folderplus', 'Add your videos', true))}</div>`;
const shows = `<div class="frame">${nav('TV Shows', 'line')}${empty(`<div class="ring">${mark}</div>`,
  'Finding your TV shows', '12 found so far. They appear here as they’re found.')}</div>`;
const third = `<div class="frame">${nav('+', 'line')}${empty(svg(I.page, 60), 'Make this page your own',
  'Choose what it shows: a genre, a service, what you haven’t watched yet, anything you like. Give it a name and it '
  + 'takes the place of the + in the top bar.', btn('edit', 'Customise page', true),
  'More options for your library are in Files and Settings.')}</div>`;

const css = `${font(400)}${font(500)}*{box-sizing:border-box;margin:0}
body{width:1920px;font-family:R;color:#fff;background:#16181C;position:relative}
body:after{content:"";position:fixed;inset:0;border:6px solid #8a919c;z-index:99;pointer-events:none}
.frame{width:1920px;height:1080px;position:relative;overflow:hidden;background:#16181C}.frame+.frame{border-top:8px solid
#8a919c}
.nav{position:absolute;left:56px;right:56px;top:24px;height:76px;display:flex;align-items:center;gap:14px;
font-size:33px;color:#8f98a8;z-index:5}.nav .brand{color:#f1ead8;letter-spacing:2px;margin-right:36px;font-weight:500}
.ni{position:relative;display:inline-flex;align-items:center;height:60px;padding:0 18px}.ni.files{margin-left:auto}
.nav .clock{margin-left:18px;color:#C9D3E6}
.line{color:#fff}.line:after{content:'';position:absolute;left:18px;right:18px;bottom:-2px;height:4px;border-radius:2px;
background:#fff}
.empty{position:absolute;left:50%;top:52%;transform:translate(-50%,-50%);width:900px;text-align:center;display:flex;
flex-direction:column;align-items:center}
.ei{width:120px;height:120px;border-radius:50%;background:rgba(255,255,255,.07);display:flex;align-items:center;
justify-content:center;color:#C9D3E6;margin-bottom:36px}
.ring{width:84px;height:84px}
h2{font-size:48px;font-weight:400}p{font-size:30px;color:#C9D3E6;line-height:1.45;margin-top:18px;max-width:820px}
.btn{margin-top:46px;height:84px;padding:0 40px;border-radius:42px;display:inline-flex;align-items:center;gap:16px;
font-size:30px;font-weight:500;background:rgba(255,255,255,.1)}.btn.focus{background:#F4F6FB;color:#0B0D12}
.note{font-size:25px;color:#9aa3b4;margin-top:34px}`;

(async () => {
  const browser = await chromium.launch();
  const tab = await browser.newPage({ viewport: { width: 1920, height: 3256 } });
  await tab.setContent(`<html><head><style>${css}</style></head><body>${movies}${shows}${third}</body></html>`,
    { waitUntil: 'load' });
  await tab.evaluate(() => document.fonts.ready);
  await tab.screenshot({ path: path.join(__dirname, '34-v10-empty-pages.png'), fullPage: true });
  await browser.close();
})();
