// Proposal (0.154 walk-through): path bar inside the panel; actions inline on the focused row; metadata as a second line on every row; no strip.
// Run: FONT_DIR=<roboto woff2 dir> node inline-actions.js
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
  sort: '<path d="M7 4v16M3 16l4 4 4-4M17 20V4M13 8l4-4 4 4"/>', filter: '<path d="M4 5h16l-6 7v6l-4 2v-8z"/>', save: '<path d="M6 3h12v18l-6-4-6 4z"/>',
};
const ic = (k, s = 32) => `<svg width="${s}" height="${s}" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.7" stroke-linecap="round" stroke-linejoin="round">${P[k]}</svg>`;
const btn = (k, t, f) => `<div class="btn${f ? ' focus' : ''}">${ic(k, 28)}<span>${t}</span></div>`;
const nav = `<div class="nav"><span class="brand">SUPERNOVA</span><span>Home</span><span>Movies</span><span>TV Shows</span><span>Documentaries</span><span class="sp"></span><span class="on">Network &amp; Files</span><span class="clock">17:35</span></div>`;
const head = `<div class="head"><div class="crumb"><span>Local Storage</span><b>›</b><span>Backup #2</span><b>›</b><span>TV</span><b>›</b><span class="last">1883 – The Bass Reeves Story</span></div>
  <div class="tools">${btn('grid', 'Grid')}${btn('sort', 'Sort: Name')}${btn('filter', 'Videos only')}</div></div>`;
const eps = [['S01E01 - PART I', '52m', 'watched'], ['S01E02 - PART II', '48m', 'progress'], ['S01E03 - PART III', '51m'], ['S01E04 - PART IV', '47m'], ['S01E05 - PART V', '50m'], ['S01E06 - PART VI', '49m']];
const mark = st => st === 'watched' ? `<span class="st ok">${ic('check', 26)}</span>` : st === 'progress' ? '<span class="st"><span class="prog"><i></i></span></span>' : '<span class="st"></span>';
const rowBtns = state => `<span class="ibtns">${btn('play', 'Resume', state === 'actions')}${btn('info', 'Details')}${btn('lib', 'Exclude')}${btn('save', 'Save')}</span>`;
const sizes = ['335 MB', '312 MB', '330 MB', '301 MB', '322 MB', '316 MB'];
const line2 = (i, d, extra) => `<span class="l2">Lawmen: Bass Reeves · S1 E${i + 1} · ${d.replace('m', ' min')} · 1080p · English 5.1 · ${sizes[i]}${extra || ''}</span>`;
const list = state => eps.map(([n, d, st], i) => `<div class="row${i === 1 ? (state === 'list' ? ' focus' : ' sel') : ''}">${ic('video')}
  <span class="two"><span class="name1">Lawmen - Bass Reeves - ${n}</span>${line2(i, d, i === 1 ? ' · <span class="res">Resume at 19:42</span>' : st === 'watched' ? ' · Watched' : '')}</span>
  ${i === 1 ? rowBtns(state) : ''}${mark(st)}</div>`).join('');
const poster = `<div class="poster"><div>LAWMEN<br><small>BASS REEVES</small></div></div>`;
const strip = state => `<div class="strip">${poster}<div class="si"><div class="t">Lawmen: Bass Reeves · Season 1 · Episode 2 · Part II</div>
  <div class="m">48 min · 1080p · English AAC 5.1 · 312 MB · In your library · Resume at 19:42</div></div></div>`;
const page = state => `${nav}<div class="sheet">${head}<div class="count">6 videos · in your library</div>${list(state)}</div>
  <div class="label">${state === 'list' ? 'Two-line rows, no strip · focused row shows its buttons · OK plays · Right → buttons' : 'After pressing Right: Resume focused; Left/Back returns to the row'}</div>`;
const css = `${font(400)}${font(500)}
*{box-sizing:border-box;margin:0}
body{width:1920px;height:1080px;overflow:hidden;font-family:R;color:#fff;position:relative;
  background:radial-gradient(ellipse 70% 60% at 70% 75%,#1d4fd8 0%,rgba(29,79,216,0) 70%),linear-gradient(180deg,#05081a 0%,#0a1650 45%,#10248a 100%)}
.nav{position:absolute;left:56px;right:56px;top:28px;height:56px;display:flex;align-items:center;gap:30px;font-size:34px;color:#e8edf8}
.nav .brand{font-size:36px;margin-right:40px;letter-spacing:1px}.nav .sp{flex:1}.nav .on{font-weight:500}.nav .clock{margin-left:30px}
.sheet{position:absolute;left:56px;right:56px;top:112px;bottom:40px;border-radius:26px;background:rgba(22,24,28,.97);border:2px solid rgba(255,255,255,.22);padding:22px}
.head{display:flex;align-items:center;gap:20px;margin-bottom:6px}
.crumb{flex:1;font-size:25px;color:#c9d3e6;display:flex;gap:6px;align-items:center;white-space:nowrap;overflow:hidden}.crumb span{padding:8px 10px;border-radius:14px}
.crumb b{font-weight:400;opacity:.6}.crumb .last{color:#fff;font-weight:500}
.tools{display:flex;gap:14px}
.btn{height:64px;padding:0 22px 0 18px;border-radius:22px;background:#2a2d33;display:inline-flex;align-items:center;gap:10px;font-size:22px;font-weight:500;white-space:nowrap}
.btn.focus{background:#F4F6FB;color:#0B0D12}
.count{font-size:21px;color:#C9D3E6;margin:6px 20px 6px}
.row{display:flex;align-items:center;gap:20px;height:100px;padding:0 20px;border-radius:24px;font-size:26px;font-weight:500}
.row.focus{box-shadow:inset 0 0 0 3px #fff}.row.sel{background:rgba(255,255,255,.1)}
.name1{white-space:nowrap}.two{flex:1;display:flex;flex-direction:column;gap:6px;min-width:0}.l2{font-weight:400;font-size:21px;color:#C9D3E6;white-space:nowrap}.res{color:#8fc3ff}.meta{font-weight:400;font-size:22px;color:#C9D3E6}
.st{width:34px;display:flex;justify-content:center}.st.ok{color:#5ad18a}
.prog{width:30px;height:6px;border-radius:3px;background:rgba(255,255,255,.25);overflow:hidden}.prog i{display:block;width:40%;height:100%;background:#5aa9ff}
.strip{position:absolute;left:22px;right:22px;bottom:22px;height:140px;border-radius:20px;background:#22252b;display:flex;align-items:center;gap:22px;padding:0 22px}
.si .t{font-size:25px;font-weight:500}.si .m{font-size:20px;color:#C9D3E6;margin-top:8px}.si{flex:1;min-width:0}
.acts{display:flex;gap:12px}.ibtns{display:flex;gap:12px;margin-right:6px}.ibtns .btn{height:62px;font-size:21px;padding:0 18px 0 14px}
.poster{width:80px;height:118px;border-radius:10px;flex:none;background:linear-gradient(160deg,#6b4a2a,#2b1d14 70%);display:flex;align-items:flex-end;padding:7px;font-family:Georgia,serif;font-size:13px;line-height:1;color:#f1d9a8}.poster small{font-size:8px}
.label{position:absolute;right:56px;bottom:8px;font-size:19px;color:#c9d3e6}`;
(async () => {
  const browser = await chromium.launch();
  const tab = await browser.newPage({ viewport: { width: 1920, height: 1080 } });
  for (const [f, st] of [['14-two-line-rows-browsing.png', 'list'], ['15-two-line-rows-focused.png', 'actions']]) {
    await tab.setContent(`<html><head><style>${css}</style></head><body>${page(st)}</body></html>`, { waitUntil: 'load' });
    await tab.evaluate(() => document.fonts.ready);
    await tab.screenshot({ path: path.join(out, f) });
  }
  await browser.close();
})();
