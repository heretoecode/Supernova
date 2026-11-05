// Proposal (0.154 walk-through): clickable path bar and library include/exclude markers in the file browser
// (approved layout C: list + details strip + pop-up menu). Run: FONT_DIR=<roboto woff2 dir> node library-marks.js
const fs = require('fs');
const path = require('path');
const { chromium } = require('playwright');
const out = __dirname;
const fontDir = process.env.FONT_DIR;
const font = w => `@font-face{font-family:R;font-weight:${w};src:url(data:font/woff2;base64,${fs.readFileSync(path.join(fontDir, `roboto-latin-${w}-normal.woff2`)).toString('base64')}) format('woff2')}`;
const P = {
  folder: '<path d="M3 6.5A1.5 1.5 0 0 1 4.5 5H9l2 2.5h8.5A1.5 1.5 0 0 1 21 9v9.5a1.5 1.5 0 0 1-1.5 1.5h-15A1.5 1.5 0 0 1 3 18.5z"/>',
  lib: '<path d="M5 4v16M9 4v16M13 4l5 15"/>', ban: '<circle cx="12" cy="12" r="9"/><path d="M6 6l12 12"/>',
  grid: '<rect x="4" y="4" width="7" height="7"/><rect x="13" y="4" width="7" height="7"/><rect x="4" y="13" width="7" height="7"/><rect x="13" y="13" width="7" height="7"/>',
  sort: '<path d="M7 4v16M3 16l4 4 4-4M17 20V4M13 8l4-4 4 4"/>', filter: '<path d="M4 5h16l-6 7v6l-4 2v-8z"/>',
  dots: '<circle cx="5" cy="12" r="1.6" fill="currentColor"/><circle cx="12" cy="12" r="1.6" fill="currentColor"/><circle cx="19" cy="12" r="1.6" fill="currentColor"/>',
};
const ic = (k, s = 32) => `<svg width="${s}" height="${s}" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.7" stroke-linecap="round" stroke-linejoin="round">${P[k]}</svg>`;
const btn = (k, t) => `<div class="btn">${ic(k, 30)}<span>${t}</span></div>`;
const nav = `<div class="nav"><span class="brand">SUPERNOVA</span><span>Home</span><span>Movies</span><span>TV Shows</span><span>Documentaries</span><span class="sp"></span><span class="on">Network &amp; Files</span><span class="clock">17:35</span></div>`;
const crumb = (parts, focusIdx) => `<div class="crumb">${parts.map((p, i) => `<span class="c${i === focusIdx ? ' f' : ''}${i === parts.length - 1 ? ' last' : ''}">${p}</span>`).join('<b>›</b>')}</div>`;
const tools = n => `<div class="tools">${btn('grid', 'Grid')}${btn('sort', 'Sort: Name')}${btn('filter', 'Showing: Videos only')}<span class="count">${n}</span></div>`;
const tag = st => st === 'root' ? `<span class="tag lib">${ic('lib', 24)}Library folder</span>` : st === 'ex' ? `<span class="tag ex">${ic('ban', 22)}Excluded</span>` : '';
const row = ([n, meta, st], f) => `<div class="row${f ? ' focus' : ''}${st === 'ex' ? ' dim' : ''}">${ic('folder')}<span class="name1">${n}</span>${tag(st)}<span class="meta">${meta}</span></div>`;
const strip = (t, m, s) => `<div class="strip"><div><div class="t">${t}</div><div class="m">${m}</div></div><div class="status ${s[0]}">${s[1]}</div><div class="hint"><b>OK</b> Open &nbsp;&nbsp; ${ic('dots', 24)} More</div></div>`;
const page1 = `${nav}${crumb(['Network &amp; Files', 'Local Storage', 'Backup #2'], -1)}<div class="sheet">${tools('5 folders')}
  ${[['TV', '73 folders', 'root'], ['Films', '412 videos', 'root'], ['Documentaries', '28 videos', 'root'], ['Downloads', '6 videos', ''], ['Camera Imports', '19 videos', '']].map((r, i) => row(r, i === 3)).join('')}
  ${strip('Downloads', 'Folder · 6 videos · 4.1 GB', ['no', 'Not in your library'])}</div>`;
const tvRows = f => [['1883 – The Bass Reeves Story', '8 videos', ''], ['Behind the Scenes', '14 videos', 'ex'], ['Mr. Robot', '45 videos', ''], ['Tulsa King', '19 videos', ''], ['War', '6 videos', ''], ['Kingdom', '3 videos', '']].map((r, i) => row(r, i === f)).join('');
const page3 = `${nav}${crumb(['Network &amp; Files', 'Local Storage', 'Backup #2', 'TV'], 2)}<div class="sheet">${tools('6 folders')}${tvRows(-1)}
  <div class="strip"><div><div class="t">Backup #2</div><div class="m">USB drive · Micron · 612 GB free of 1.8 TB</div></div><div class="hint"><b>OK</b> Go to Backup #2</div></div></div>
  <div class="note">Path bar focused (press Up from the toolbar): <b>OK</b> jumps straight to “Backup #2”</div>`;
const page2 = `${nav}${crumb(['Network &amp; Files', 'Local Storage', 'Backup #2', 'TV'], -1)}<div class="sheet">${tools('6 folders')}
  ${[['1883 – The Bass Reeves Story', '8 videos', ''], ['Behind the Scenes', '14 videos', 'ex'], ['Mr. Robot', '45 videos', ''], ['Tulsa King', '19 videos', ''], ['War', '6 videos', ''], ['Kingdom', '3 videos', '']].map((r, i) => row(r, i === 1)).join('')}
  ${strip('Behind the Scenes', 'Folder · 14 videos · 9.8 GB', ['ex', 'Excluded by you · inside the TV library folder'])}</div>
  <div class="label">Excluded folder (focused): dimmed, tagged, and explained in the strip</div>`;
const css = `${font(400)}${font(500)}
*{box-sizing:border-box;margin:0}
body{width:1920px;height:1080px;overflow:hidden;font-family:R;color:#fff;position:relative;
  background:radial-gradient(ellipse 70% 60% at 70% 75%,#1d4fd8 0%,rgba(29,79,216,0) 70%),linear-gradient(180deg,#05081a 0%,#0a1650 45%,#10248a 100%)}
.nav{position:absolute;left:56px;right:56px;top:28px;height:56px;display:flex;align-items:center;gap:30px;font-size:34px;color:#e8edf8}
.nav .brand{font-size:36px;margin-right:40px;letter-spacing:1px}.nav .sp{flex:1}.nav .on{font-weight:500}.nav .clock{margin-left:30px}
.crumb{position:absolute;left:50px;top:100px;height:56px;font-size:26px;color:#c9d3e6;display:flex;gap:6px;align-items:center}
.crumb b{font-weight:400;opacity:.6;margin:0 4px}.crumb .c{padding:8px 14px;border-radius:16px}.crumb .last{color:#fff;font-weight:500}
.crumb .c.f{background:#F4F6FB;color:#0B0D12;font-weight:500}
.sheet{position:absolute;left:56px;right:56px;top:166px;bottom:40px;border-radius:26px;background:rgba(22,24,28,.97);border:2px solid rgba(255,255,255,.22);padding:22px}
.tools{display:flex;gap:16px;align-items:center;margin-bottom:12px}.count{margin-left:auto;font-size:22px;color:#C9D3E6;padding-right:12px}
.btn{height:72px;padding:0 26px 0 22px;border-radius:24px;background:#2a2d33;display:inline-flex;align-items:center;gap:12px;font-size:24px;font-weight:500;white-space:nowrap}
.row{display:flex;align-items:center;gap:20px;height:74px;padding:0 20px;border-radius:24px;font-size:26px;font-weight:500}
.row.focus{box-shadow:inset 0 0 0 3px #fff;background:transparent}
.row.dim .name1,.row.dim>svg,.row.dim .meta{opacity:.45}
.name1{white-space:nowrap}.meta{margin-left:auto;font-weight:400;font-size:22px;color:#C9D3E6}
.tag{display:inline-flex;align-items:center;gap:8px;font-size:20px;font-weight:500;padding:4px 14px 4px 10px;border-radius:999px}
.tag.lib{background:rgba(90,169,255,.18);color:#9fcbff}.tag.ex{background:rgba(255,120,120,.16);color:#ffb0b0}
.strip{position:absolute;left:22px;right:22px;bottom:22px;height:120px;border-radius:20px;background:#22252b;display:flex;align-items:center;gap:26px;padding:0 28px}
.strip .t{font-size:26px;font-weight:500}.strip .m{font-size:21px;color:#C9D3E6;margin-top:8px}
.status{font-size:22px;font-weight:500;padding:6px 16px;border-radius:999px;margin-left:30px}
.status.no{background:rgba(255,255,255,.1);color:#d6dbe5}.status.ex{background:rgba(255,120,120,.16);color:#ffb0b0}
.hint{margin-left:auto;font-size:21px;color:#C9D3E6;display:flex;align-items:center;gap:8px}.hint b{border:1px solid #888;border-radius:6px;padding:0 6px;font-weight:500}
.note{position:absolute;right:56px;top:116px;font-size:20px;color:#c9d3e6}.note b{border:1px solid #888;border-radius:6px;padding:0 6px}
.label{position:absolute;right:56px;top:116px;font-size:20px;color:#c9d3e6}`;
(async () => {
  const browser = await chromium.launch();
  const tab = await browser.newPage({ viewport: { width: 1920, height: 1080 } });
  for (const [f, body] of [['07-library-marks-drive.png', page1 + '<div class="label">Library folders tagged · Downloads (focused) not in library</div>'], ['08-library-marks-excluded.png', page2], ['09-path-bar-focused.png', page3]]) {
    await tab.setContent(`<html><head><style>${css}</style></head><body>${body}</body></html>`, { waitUntil: 'load' });
    await tab.evaluate(() => document.fonts.ready);
    await tab.screenshot({ path: path.join(out, f) });
  }
  await browser.close();
})();
