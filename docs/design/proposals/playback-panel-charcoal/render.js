// Proposal (0.154 walk-through): Playback Settings panel — charcoal glass, More-menu row and tab sizes, no title.
// Real 1080p sizes (panel ≈ one third of the screen). Run: FONT_DIR=<roboto woff2 dir> node render.js
const fs = require('fs');
const path = require('path');
const { chromium } = require('playwright');
const out = __dirname;
const fontDir = process.env.FONT_DIR;
const font = w => `@font-face{font-family:R;font-weight:${w};src:url(data:font/woff2;base64,${fs.readFileSync(path.join(fontDir, `roboto-latin-${w}-normal.woff2`)).toString('base64')}) format('woff2')}`;

const P = {
  cc: '<rect x="3" y="5" width="18" height="14" rx="2"/><path d="M10 10.2a2.2 2.2 0 1 0 0 3.6M16.5 10.2a2.2 2.2 0 1 0 0 3.6"/>',
  list: '<path d="M9 6h11M9 12h11M9 18h11"/><circle cx="4.5" cy="6" r="1"/><circle cx="4.5" cy="12" r="1"/><circle cx="4.5" cy="18" r="1"/>',
  down: '<path d="M12 4v11M7 10.5l5 5 5-5M4 20h16"/>',
  aa: '<text x="1" y="17" font-size="13" font-family="R" stroke="none" fill="currentColor">Aa</text>',
  palette: '<circle cx="12" cy="12" r="9"/><circle cx="8" cy="10" r="1.2"/><circle cx="12" cy="7.5" r="1.2"/><circle cx="16" cy="10" r="1.2"/>',
  vpos: '<path d="M12 3v18M8 7l4-4 4 4M8 17l4 4 4-4"/>',
  outline: '<rect x="4" y="3" width="16" height="18" stroke-dasharray="2 2"/><text x="8" y="16" font-size="10" font-family="R" stroke="none" fill="currentColor">A</text>',
  bg: '<rect x="4" y="4" width="16" height="16"/><path d="M4 12l8-8M4 18l14-14M10 20l10-10"/>',
  drop: '<path d="M12 3c4 5 6 8 6 11a6 6 0 0 1-12 0c0-3 2-6 6-11z"/>',
  timer: '<circle cx="12" cy="13" r="8"/><path d="M12 13V9M9 2h6"/>',
  gear: '<circle cx="12" cy="12" r="3.2"/><path d="M12 2.5v3M12 18.5v3M2.5 12h3M18.5 12h3M5.3 5.3l2.1 2.1M16.6 16.6l2.1 2.1M5.3 18.7l2.1-2.1M16.6 7.4l2.1-2.1"/>',
  speaker: '<path d="M4 9h4l5-4v14l-5-4H4z"/><path d="M16 9a4 4 0 0 1 0 6"/>',
  play: '<path d="M8 5l11 7-11 7z"/>',
  globe: '<circle cx="12" cy="12" r="9"/><path d="M3 12h18M12 3c3 3 3 15 0 18M12 3c-3 3-3 15 0 18"/>',
};
const ic = (k, s = 30) => `<svg width="${s}" height="${s}" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.7" stroke-linecap="round" stroke-linejoin="round">${P[k]}</svg>`;
const toggle = on => `<span class="tg ${on ? 'on' : ''}"><i></i></span>`;
const choices = (opts, sel) => opts.map(o => `<span class="${o === sel ? 'sel' : ''}">${o}</span>`).join('<b>|</b>');
const swatches = ['#fff8e1', '#f5c518', '#3fd8d0', '#3fc23f', '#f06aa3'].map((c, i) => `<span class="sw${i === 0 ? ' cur' : ''}" style="background:${c}"></span>`).join('');
const rows = [
  ['h', 'Subtitle selection'],
  ['cc', 'Subtitles', toggle(true)],
  ['list', 'Subtitle Track', `‹ ${ic('globe', 28)} <span class="sel">English</span> ›`],
  ['down', 'Download Subtitles', 'Find &amp; Download ›'],
  ['h', 'Subtitle appearance'],
  ['aa', 'Text Size', choices(['Small', 'Medium', 'Large'], 'Medium')],
  ['palette', 'Text Colour', swatches, 'focus'],
  ['vpos', 'Vertical Position', choices(['Bottom', 'Middle', 'Top'], 'Bottom')],
  ['outline', 'Outline', toggle(true)],
  ['bg', 'Background', toggle(false)],
  ['drop', 'Background Opacity', '‹ <span class="sel">100%</span> ›', 'dim'],
  ['timer', 'Subtitle Delay', '−&nbsp;&nbsp;&nbsp;0 ms&nbsp;&nbsp;&nbsp;+'],
];

function panel(v) {
  const body = rows.map(r => r[0] === 'h'
    ? `<div class="h ${v.caps ? 'caps' : ''}"><span>${v.caps ? r[1].toUpperCase() : r[1]}</span>${v.headLine ? '<i></i>' : ''}</div>`
    : `<div class="row ${r[3] || ''}">${ic(r[0])}<span class="lbl">${r[1]}</span><span class="val">${r[2]}</span></div>`).join('');
  const tabs = [['cc', 'Subtitles', 'selected'], ['speaker', 'Audio'], ['play', 'Playback']]
    .map(([k, t, s]) => `<div class="tab ${s || ''}">${ic(k, 26)}<span>${t}</span></div>`).join('');
  return `<div class="panel ${v.cls}">${v.title ? `<div class="title">${ic('gear', 34)}<span>Playback Settings</span></div>` : ''}<div class="tabs">${tabs}</div>${body}</div>`;
}
const today = { cls: 'today', title: true, caps: true, headLine: false };
const proposed = { cls: 'proposed', title: false, caps: false, headLine: true };

const css = `${font(400)}${font(500)}
*{box-sizing:border-box;margin:0}
body{width:1920px;height:1080px;overflow:hidden;font-family:R;color:#fff;
  background:radial-gradient(ellipse 50% 70% at 30% 45%,#2b5a5e,#0d2226 70%),#0a1a1d;position:relative}
.lab{position:absolute;top:60px;width:210px;font-size:28px;font-weight:500;text-shadow:0 2px 8px #000}
.lab small{display:block;font-size:20px;font-weight:400;color:#cfe0e6;margin-top:2px}
.panel{position:absolute;top:48px;width:640px;border-radius:26px;padding:26px 26px 22px}
.today{left:260px;background:rgba(26,31,92,.97);border:2px solid rgba(255,255,255,.40)}
.proposed{left:1010px;background:rgba(22,24,28,.97);border:2px solid rgba(255,255,255,.22)}
.title{display:flex;align-items:center;gap:14px;font-size:34px;font-weight:500;margin:2px 0 18px 4px}
.tabs{display:flex;gap:16px;margin-bottom:6px}
.tab{flex:1;display:flex;align-items:center;justify-content:center;gap:10px;font-size:24px;font-weight:500;border-radius:24px}
.today .tab{height:56px;background:#141848}
.today .tab.selected{box-shadow:inset 0 0 0 3px #fff}
.proposed .tab{height:72px;background:#2a2d33}
.proposed .tab.selected{box-shadow:inset 0 0 0 2px #fff}
.h{display:flex;align-items:center;gap:14px;font-size:20px;margin:18px 6px 4px}
.today .h{color:#8fb4e8;letter-spacing:1px;font-weight:500;padding-bottom:6px;border-bottom:2px solid rgba(143,180,232,.55)}
.proposed .h{color:#b7bfcc}.proposed .h i{flex:1;height:2px;background:rgba(255,255,255,.22)}
.row{display:flex;align-items:center;gap:16px;border-radius:24px;padding:0 16px;font-size:24px;font-weight:500}
.today .row{height:60px;border-bottom:2px solid rgba(143,180,232,.45);border-radius:0}
.today .row.focus{border-radius:24px;border-bottom:none;box-shadow:inset 0 0 0 3px #fff}
.proposed .row{height:74px}
.proposed .row.focus{box-shadow:inset 0 0 0 3px #fff}
.lbl{flex:1;white-space:nowrap}
.val{display:flex;align-items:center;gap:10px;font-weight:400;font-size:23px;white-space:nowrap}
.val b{font-weight:400;opacity:.7;margin:0 2px}
.sel{color:#5aa9ff}
.dim{opacity:.45}
.sw{width:34px;height:34px;border-radius:50%;display:inline-block}
.sw.cur{box-shadow:0 0 0 3px #fff}
.tg{width:68px;height:36px;border-radius:18px;background:#3a3f48;position:relative;display:inline-block}
.tg.on{background:#2fb7d8}.tg i{position:absolute;top:4px;left:4px;width:28px;height:28px;border-radius:50%;background:#fff}
.tg.on i{left:36px}`;
const html = `<html><head><style>${css}</style></head><body>
<div class="lab" style="left:24px">Today (0.153)<small>Navy, title, 56 px tabs, 60 px rows: same 24 px corners look curvier</small></div>
<div class="lab" style="left:1680px">Proposed<small>Charcoal, no title, More-menu sizes (72 px tabs, 74 px rows)</small></div>
${panel(today)}${panel(proposed)}</body></html>`;

(async () => {
  const browser = await chromium.launch();
  const tab = await browser.newPage({ viewport: { width: 1920, height: 1080 } });
  await tab.setContent(html, { waitUntil: 'load' });
  await tab.evaluate(() => document.fonts.ready);
  const h = await tab.evaluate(() => [...document.querySelectorAll('.panel')].map(p => Math.round(p.getBoundingClientRect().bottom)));
  console.log('panel bottoms', h);
  await tab.screenshot({ path: path.join(out, '01-panel-today-vs-proposed.png') });
  await browser.close();
})();
