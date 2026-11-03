// Alternatives to the two-panel browser (0.154 walk-through): A expanding row, B poster grid, C list + strip + menu.
// Run: FONT_DIR=<roboto woff2 dir> node alt.js
const fs = require('fs');
const path = require('path');
const { chromium } = require('playwright');
const out = __dirname;
const fontDir = process.env.FONT_DIR;
const font = w => `@font-face{font-family:R;font-weight:${w};src:url(data:font/woff2;base64,${fs.readFileSync(path.join(fontDir, `roboto-latin-${w}-normal.woff2`)).toString('base64')}) format('woff2')}`;
const P = {
  video: '<rect x="3" y="5" width="18" height="14" rx="2"/><path d="M10 9.5v5l4.5-2.5z" fill="currentColor"/>',
  play: '<path d="M8 5l11 7-11 7z" fill="currentColor"/>', info: '<circle cx="12" cy="12" r="9"/><path d="M12 11v6M12 7.5v.5"/>',
  lib: '<path d="M5 4v16M9 4v16M13 4l5 15"/>', check: '<circle cx="12" cy="12" r="9"/><path d="M8 12.5l2.7 2.7L16 9.7"/>',
  grid: '<rect x="4" y="4" width="7" height="7"/><rect x="13" y="4" width="7" height="7"/><rect x="4" y="13" width="7" height="7"/><rect x="13" y="13" width="7" height="7"/>',
  sort: '<path d="M7 4v16M3 16l4 4 4-4M17 20V4M13 8l4-4 4 4"/>', filter: '<path d="M4 5h16l-6 7v6l-4 2v-8z"/>',
  dots: '<circle cx="5" cy="12" r="1.6" fill="currentColor"/><circle cx="12" cy="12" r="1.6" fill="currentColor"/><circle cx="19" cy="12" r="1.6" fill="currentColor"/>',
  save: '<path d="M6 3h12v18l-6-4-6 4z"/>', restart: '<path d="M4 12a8 8 0 1 0 3-6.2M4 4v4h4"/>',
};
const ic = (k, s = 32) => `<svg width="${s}" height="${s}" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.7" stroke-linecap="round" stroke-linejoin="round">${P[k]}</svg>`;
const btn = (k, t, cls = '') => `<div class="btn ${cls}">${ic(k, 30)}<span>${t}</span></div>`;
const nav = `<div class="nav"><span class="brand">SUPERNOVA</span><span>Home</span><span>Movies</span><span>TV Shows</span><span>Documentaries</span><span class="sp"></span><span class="on">Network &amp; Files</span><span class="clock">17:35</span></div>`;
const crumb = `<div class="crumb"><span>Local Storage</span><b>›</b><span>Backup #2</span><b>›</b><span>TV</span><b>›</b><span class="last">1883 – The Bass Reeves Story</span></div>`;
const tools = `<div class="tools">${btn('grid', 'Grid')}${btn('sort', 'Sort: Name')}${btn('filter', 'Showing: Videos only')}<span class="count">8 videos</span></div>`;
const eps = [['S01E01 - PART I', '52m', 'watched'], ['S01E02 - PART II', '48m', 'progress'], ['S01E03 - PART III', '51m'], ['S01E04 - PART IV', '47m'], ['S01E05 - PART V', '50m'], ['S01E06 - PART VI', '49m']];
const mark = st => st === 'watched' ? `<span class="st ok">${ic('check', 26)}</span>` : st === 'progress' ? '<span class="st"><span class="prog"><i></i></span></span>' : '<span class="st"></span>';
const poster = (w, h, f = 26) => `<div class="poster" style="width:${w}px;height:${h}px;font-size:${f}px"><div>LAWMEN<br><small>BASS REEVES</small></div></div>`;
const still = (i, w, h) => `<div class="still" style="width:${w}px;height:${h}px;background:linear-gradient(${120 + i * 25}deg,#5b3d24,#1f2a33 70%)"></div>`;

// A — expanding row
const A = `${nav}${crumb}<div class="sheet">${tools}
  <div class="row exp"><div class="exp-top">${ic('video')}<span class="name1">Lawmen - Bass Reeves - S01E01 - PART I</span><span class="meta">52m · 1080p</span>${mark('watched')}</div>
    <div class="exp-body">${still(0, 300, 168)}<div class="exp-info"><div class="t">Lawmen: Bass Reeves · Season 1 · Episode 1</div>
      <div class="m">1080p · H.264 · English AAC 5.1 · 335 MB · In your library · ✓ Watched</div>
      <div class="acts">${btn('play', 'Play', 'focus')}${btn('info', 'Details')}${btn('lib', 'Exclude')}</div></div></div></div>
  ${eps.slice(1, 5).map(([n, d, st]) => `<div class="row">${ic('video')}<span class="name1">Lawmen - Bass Reeves - ${n}</span><span class="meta">${d} · 1080p</span>${mark(st)}</div>`).join('')}</div>`;

// B — poster grid
const B = `${nav}${crumb}<div class="sheet">${tools}<div class="pgrid">
  ${eps.map(([n, d, st], i) => `<div class="pcard${i === 0 ? ' focus' : ''}">${still(i, 400, 225)}${st === 'progress' ? '<div class="cprog"><i></i></div>' : ''}
    <div class="pc-t">Episode ${i + 1} · Part ${['I', 'II', 'III', 'IV', 'V', 'VI'][i]}</div><div class="pc-m">${d} · 1080p ${st === 'watched' ? '· ✓ Watched' : ''}</div></div>`).join('')}</div></div>`;

// C — list + strip + menu
const C = `${nav}${crumb}<div class="sheet c">${tools}
  ${eps.map(([n, d, st], i) => `<div class="row${i === 0 ? ' focus' : ''}">${ic('video')}<span class="name1">Lawmen - Bass Reeves - ${n}</span><span class="meta">${d} · 1080p</span>${mark(st)}</div>`).join('')}
  <div class="strip">${poster(78, 116, 13)}<div><div class="t">Lawmen: Bass Reeves · Season 1 · Episode 1 · Part I</div>
    <div class="m">52 min · 1080p · English AAC 5.1 · 335 MB · In your library · ✓ Watched</div></div>
    <div class="hint"><b>OK</b> Play &nbsp;&nbsp; ${ic('dots', 24)} More</div></div></div>
  <div class="menu"><div class="h"><span>Playback</span><i></i></div><div class="mr focus">${ic('play')}<span>Play</span></div><div class="mr">${ic('restart')}<span>Play from beginning</span></div>
  <div class="h"><span>Library</span><i></i></div><div class="mr">${ic('info')}<span>Details</span></div><div class="mr">${ic('lib')}<span>Exclude from Library</span></div>
  <div class="h"><span>Files</span><i></i></div><div class="mr">${ic('save')}<span>Save Location</span></div></div>`;

const css = `${font(400)}${font(500)}
*{box-sizing:border-box;margin:0}
body{width:1920px;height:1080px;overflow:hidden;font-family:R;color:#fff;position:relative;
  background:radial-gradient(ellipse 70% 60% at 70% 75%,#1d4fd8 0%,rgba(29,79,216,0) 70%),linear-gradient(180deg,#05081a 0%,#0a1650 45%,#10248a 100%)}
.nav{position:absolute;left:56px;right:56px;top:28px;height:56px;display:flex;align-items:center;gap:30px;font-size:34px;color:#e8edf8}
.nav .brand{font-size:36px;margin-right:40px;letter-spacing:1px}.nav .sp{flex:1}.nav .on{font-weight:500}.nav .clock{margin-left:30px}
.crumb{position:absolute;left:64px;top:112px;font-size:26px;color:#c9d3e6;display:flex;gap:14px}.crumb b{font-weight:400;opacity:.6}.crumb .last{color:#fff;font-weight:500}
.sheet{position:absolute;left:56px;right:56px;top:166px;bottom:40px;border-radius:26px;background:rgba(22,24,28,.97);border:2px solid rgba(255,255,255,.22);padding:22px}
.tools{display:flex;gap:16px;align-items:center;margin-bottom:12px}.count{margin-left:auto;font-size:22px;color:#C9D3E6;padding-right:12px}
.btn{height:72px;padding:0 26px 0 22px;border-radius:24px;background:#2a2d33;display:inline-flex;align-items:center;gap:12px;font-size:24px;font-weight:500;white-space:nowrap}
.btn.focus{background:#F4F6FB;color:#0B0D12}
.row{display:flex;align-items:center;gap:20px;min-height:74px;padding:0 20px;border-radius:24px;font-size:26px;font-weight:500}
.row.focus{background:#F4F6FB;color:#0B0D12}.row.focus .meta{color:#3A4256}
.name1{flex:1;white-space:nowrap}.meta{font-weight:400;font-size:22px;color:#C9D3E6}
.st{width:34px;display:flex;justify-content:center}.st.ok{color:#5ad18a}
.prog{width:30px;height:6px;border-radius:3px;background:rgba(255,255,255,.25);overflow:hidden}.prog i{display:block;width:40%;height:100%;background:#5aa9ff}
.row.exp{flex-direction:column;align-items:stretch;padding:14px 20px 20px;background:#2a2d33;box-shadow:inset 0 0 0 3px #fff}
.exp-top{display:flex;align-items:center;gap:20px;height:56px}.exp-body{display:flex;gap:26px;margin-top:10px}
.exp-info .t{font-size:28px}.exp-info .m{font-size:22px;font-weight:400;color:#C9D3E6;margin-top:8px}.acts{display:flex;gap:16px;margin-top:22px}
.still{border-radius:14px;flex:none}
.poster{border-radius:10px;flex:none;background:linear-gradient(160deg,#6b4a2a,#2b1d14 70%);display:flex;align-items:flex-end;padding:8px;font-family:Georgia,serif;line-height:1;color:#f1d9a8;letter-spacing:1px}.poster small{font-size:60%}
.pgrid{display:grid;grid-template-columns:repeat(4,1fr);gap:30px 26px;margin-top:10px}
.pcard{border-radius:18px;padding:12px;position:relative}.pcard .still{width:100%!important}
.pcard.focus{background:#F4F6FB;color:#0B0D12}.pcard.focus .pc-m{color:#3A4256}
.pc-t{font-size:24px;font-weight:500;margin-top:12px}.pc-m{font-size:20px;color:#C9D3E6;margin-top:4px}
.cprog{position:absolute;left:24px;right:24px;top:218px;height:6px;border-radius:3px;background:rgba(255,255,255,.3)}.cprog i{display:block;width:40%;height:100%;background:#5aa9ff;border-radius:3px}
.sheet.c .strip{position:absolute;left:22px;right:22px;bottom:22px;height:140px;border-radius:20px;background:#22252b;display:flex;align-items:center;gap:22px;padding:0 24px}
.strip .t{font-size:26px;font-weight:500}.strip .m{font-size:21px;color:#C9D3E6;margin-top:8px}.strip .hint{margin-left:auto;font-size:21px;color:#C9D3E6;display:flex;align-items:center;gap:8px}.strip .hint b{border:1px solid #888;border-radius:6px;padding:0 6px;font-weight:500}
.menu{position:absolute;left:1100px;top:330px;width:560px;border-radius:26px;background:rgba(22,24,28,.97);border:2px solid rgba(255,255,255,.22);padding:20px;box-shadow:0 20px 60px rgba(0,0,0,.6)}
.menu .h{display:flex;align-items:center;gap:14px;color:#b7bfcc;font-size:21px;margin:12px 6px 4px}.menu .h i{flex:1;height:2px;background:rgba(255,255,255,.22)}
.mr{display:flex;align-items:center;gap:18px;height:70px;padding:0 18px;border-radius:24px;font-size:25px;font-weight:500}.mr.focus{box-shadow:inset 0 0 0 3px #fff}
.label{position:absolute;right:56px;top:116px;font-size:20px;color:#c9d3e6}`;
const pages = [['04-alt-A-expanding-row.png', A, 'A · one list, focused row expands'], ['05-alt-B-poster-grid.png', B, 'B · poster grid (matched videos)'], ['06-alt-C-list-strip-menu.png', C, 'C · list + details strip; menu button opens actions']];
(async () => {
  const browser = await chromium.launch();
  const tab = await browser.newPage({ viewport: { width: 1920, height: 1080 } });
  for (const [f, body, label] of pages) {
    await tab.setContent(`<html><head><style>${css}</style></head><body>${body}<div class="label">${label}</div></body></html>`, { waitUntil: 'load' });
    await tab.evaluate(() => document.fonts.ready);
    await tab.screenshot({ path: path.join(out, f) });
  }
  await browser.close();
})();
