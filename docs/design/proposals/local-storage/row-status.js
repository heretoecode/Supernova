// Proposal (0.155, owner 4 Nov 2026): show on every row whether it is in the library, excluded, or not added.
// Run: FONT_DIR=<roboto woff2 dir> node row-status.js
const fs = require('fs');
const path = require('path');
const { chromium } = require('playwright');
const out = __dirname;
const fontDir = process.env.FONT_DIR;
const font = w => `@font-face{font-family:R;font-weight:${w};src:url(data:font/woff2;base64,${fs.readFileSync(path.join(fontDir, `roboto-latin-${w}-normal.woff2`)).toString('base64')}) format('woff2')}`;
const P = {
  video: '<path d="M14 3H6.5A1.5 1.5 0 0 0 5 4.5v15A1.5 1.5 0 0 0 6.5 21h11a1.5 1.5 0 0 0 1.5-1.5V8z"/><path d="M14 3v5h5"/><path d="M10 11.5v5l4-2.5z" fill="currentColor"/>',
  play: '<path d="M8 5l11 7-11 7z"/>', x: '<path d="M6 6l12 12M18 6L6 18"/>', plus: '<path d="M12 5v14M5 12h14"/>',
  ban: '<circle cx="12" cy="12" r="8.5"/><path d="M6 6l12 12"/>',
  check: '<circle cx="12" cy="12" r="9"/><path d="M8 12.5l2.7 2.7L16 9.7"/>',
  grid: '<rect x="4" y="4" width="7" height="7"/><rect x="13" y="4" width="7" height="7"/><rect x="4" y="13" width="7" height="7"/><rect x="13" y="13" width="7" height="7"/>',
  sort: '<path d="M4 6h10M4 12h7M4 18h4M18 5v14M15 16l3 3 3-3"/>', filter: '<path d="M4 5h16l-6 7v6l-4 2v-8z"/>',
  folder: '<path d="M3 6.5A1.5 1.5 0 0 1 4.5 5H9l2 2.5h8.5A1.5 1.5 0 0 1 21 9v8.5a1.5 1.5 0 0 1-1.5 1.5h-15A1.5 1.5 0 0 1 3 17.5z"/>',
  drive: '<rect x="3" y="7" width="18" height="10" rx="2"/><path d="M7 12h.01M11 12h6"/>',
};
const ic = (k, s = 32) => `<svg width="${s}" height="${s}" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.7" stroke-linecap="round" stroke-linejoin="round">${P[k]}</svg>`;
const btn = (k, t, f) => `<div class="btn${f ? ' focus' : ''}">${ic(k, 26)}<span>${t}</span></div>`;
const nav = `<div class="nav"><span class="brand">SUPERNOVA</span><span>Home</span><span>Movies</span><span>TV Shows</span><span>Documentaries</span><span class="sp"></span><span class="on">Network &amp; Files</span><span class="clock">12:29</span></div>`;
const head = (title, pills) => `<div class="head"><div class="ftile">${ic('folder', 44)}</div>
  <div class="hcol"><div class="crumb"><span class="chip">${ic('drive', 24)}Local Storage</span><b>›</b><span class="chip">Backup #1 (SanDisk USB drive)</span><b>›</b><span class="chip">NVIDIA_SHIELD</span></div>
  <div class="title">${title}</div><div class="pills">${pills}</div></div>
  <div class="tools">${btn('grid', 'Grid')}${btn('sort', 'Sort: Name')}${btn('filter', 'Videos only')}</div></div><div class="rule"></div>`;
const tag = st => st === 'in' ? '<span class="tag in"><i></i>In library</span>'
  : st === 'ex' ? `<span class="tag ex">${ic('ban', 20)}Excluded</span>`
  : st === 'no' ? '<span class="tag no"><i></i>Not added</span>' : '';
const row = (r, focus, show, big) => `<div class="row${focus ? ' focus' : ''}${r.st === 'ex' ? ' dim' : ''}">${ic(r.folder ? 'folder' : 'video')}
  <span class="two"><span class="l1"><span class="name1">${r.n}</span>${show(r.st) ? tag(r.st) : ''}</span><span class="l2">${r.d}</span></span>
  ${focus ? `<span class="ibtns">${btn('play', r.folder ? 'Open' : 'Play')}${r.st === 'ex' ? btn('plus', 'Include') : r.st === 'no' ? btn('plus', 'Add to library') : btn('x', 'Exclude')}</span>` : ''}
  <span class="st">${r.w ? (big ? `<span class="wbig"><svg width="26" height="26" viewBox="0 0 24 24" fill="none" stroke="#16181c" stroke-width="2.6" stroke-linecap="round" stroke-linejoin="round"><path d="M6 12.5l4 4L18 8"/></svg></span>` : `<span class="ok">${ic('check', 26)}</span>`) : ''}</span></div>`;
const films = [
  { n: '28 Weeks Later', d: '2007 · 100 min · 1080p · English 5.1 · 2.0 GB', st: 'in', w: 1 },
  { n: '99 Homes', d: '2015 · 112 min · 1080p · English 5.1 · 1.8 GB', st: 'in', w: 1 },
  { n: 'Aftersun.2022.1080p.WEBRip.x264-RARBG', d: 'Not matched yet · 2.1 GB', st: 'in' },
  { n: 'All of Us Strangers', d: '2023 · 105 min · 1080p · English 5.1 · 2.0 GB', st: 'in' },
  { n: 'Argylle', d: '2024 · 139 min · 1080p · English 5.1 · 2.8 GB', st: 'ex' },
  { n: 'Asteroid City', d: '2023 · 105 min · 1080p · English 5.1 · 1.1 GB', st: 'in', w: 1 },
  { n: 'Bad Genius', d: '2017 · 130 min · 1080p · Thai 5.1 · 1.9 GB', st: 'ex' },
];
const folders = [
  { n: 'Movies', d: 'Folder · 66 videos · 146 GB', st: 'in', folder: 1 },
  { n: 'TV', d: 'Folder · 212 videos · 388 GB', st: 'in', folder: 1 },
  { n: 'Home videos', d: 'Folder · 41 videos · 23 GB', st: 'no', folder: 1 },
  { n: 'Samples', d: 'Folder · 9 videos · 2.4 GB', st: 'ex', folder: 1 },
  { n: 'Downloads', d: 'Folder · 14 videos · 18 GB', st: 'no', folder: 1 },
];
const libPill = '<span class="pill lib"><i></i>In your library</span>';
const shots = [
  ['22-row-status-every-row.png', 'Movies', `<span class="pill">66 videos</span><span class="pill">146 GB</span>${libPill}<span class="pill">2 excluded</span>`, films, 3, () => true,
    'A — every row says where it stands: ● In library · ⦸ Excluded (row dimmed) · ○ Not added'],
  ['23-row-status-exceptions-only.png', 'Movies', `<span class="pill">66 videos</span><span class="pill">146 GB</span>${libPill}<span class="pill">2 excluded</span>`, films, 3, st => st === 'ex',
    'B — only rows that differ from the folder are tagged (here: the two excluded films)'],
  ['24-row-status-mixed-folder.png', 'NVIDIA_SHIELD', '<span class="pill">5 folders</span><span class="pill">578 GB</span><span class="pill">2 in your library · 1 excluded</span>', folders, 2, () => true,
    'A in a mixed folder — what is added, what is not, what is excluded, at a glance'],
  ['25-row-status-watched-big.png', 'Movies', `<span class="pill">66 videos</span><span class="pill">146 GB</span>${libPill}<span class="pill">2 excluded</span><span class="pill">3 watched</span>`, films, 3, () => true,
    'Chosen: A, plus a larger watched mark — solid light disc with a dark tick (green now means \u201cin library\u201d only)', true],
];
const css = `${font(400)}${font(500)}
*{box-sizing:border-box;margin:0}
body{width:1920px;height:1080px;overflow:hidden;font-family:R;color:#fff;position:relative;
  background:radial-gradient(ellipse 70% 60% at 70% 75%,#1d4fd8 0%,rgba(29,79,216,0) 70%),linear-gradient(180deg,#05081a 0%,#0a1650 45%,#10248a 100%)}
.nav{position:absolute;left:56px;right:56px;top:28px;height:56px;display:flex;align-items:center;gap:30px;font-size:34px;color:#e8edf8}
.nav .brand{font-size:36px;margin-right:40px;letter-spacing:1px}.nav .sp{flex:1}.nav .on{font-weight:500}.nav .clock{margin-left:30px}
.sheet{position:absolute;left:56px;right:56px;top:112px;bottom:40px;border-radius:26px;background:rgba(22,24,28,.97);border:2px solid rgba(255,255,255,.22);padding:22px;overflow:hidden}
.head{display:flex;align-items:center;gap:24px;padding:4px 6px 18px}
.ftile{width:96px;height:96px;border-radius:22px;background:#2a2d33;display:flex;align-items:center;justify-content:center;color:#e8edf8;flex:none}
.hcol{flex:1;min-width:0;display:flex;flex-direction:column;gap:6px}
.crumb{display:flex;gap:4px;align-items:center;white-space:nowrap;font-size:21px;color:#c9d3e6;margin-left:-12px}.crumb b{font-weight:400;opacity:.55}
.chip{display:inline-flex;align-items:center;gap:8px;padding:5px 12px;border-radius:14px}
.title{font-size:36px;font-weight:500;white-space:nowrap}
.pills{display:flex;gap:10px;margin-top:4px}.pill{font-size:19px;color:#dfe5f1;background:rgba(255,255,255,.08);border-radius:12px;padding:5px 12px;display:inline-flex;align-items:center;gap:8px}
.pill.lib i{width:10px;height:10px;border-radius:50%;background:#5ad18a}
.rule{height:1px;background:rgba(255,255,255,.14);margin:0 6px 6px}
.tools{display:flex;gap:14px}
.btn{height:64px;padding:0 22px 0 18px;border-radius:22px;background:#2a2d33;display:inline-flex;align-items:center;gap:10px;font-size:22px;font-weight:500;white-space:nowrap}
.btn.focus{background:#F4F6FB;color:#0B0D12}
.row{display:flex;align-items:center;gap:20px;height:96px;padding:0 20px;border-radius:24px;font-size:26px;font-weight:500}
.row.focus{box-shadow:inset 0 0 0 3px #fff}
.row.dim .name1,.row.dim .l2,.row.dim>svg{opacity:.55}
.two{flex:1;display:flex;flex-direction:column;gap:6px;min-width:0}.l1{display:flex;align-items:center;gap:14px;white-space:nowrap}
.l2{font-weight:400;font-size:21px;color:#C9D3E6;white-space:nowrap}
.tag{font-size:18px;font-weight:500;border-radius:11px;padding:3px 11px;display:inline-flex;align-items:center;gap:7px}
.tag i{width:9px;height:9px;border-radius:50%}
.tag.in{background:rgba(90,209,138,.13);color:#9fe3bb}.tag.in i{background:#5ad18a}
.tag.ex{background:rgba(255,255,255,.08);color:#c9d3e6}
.tag.no{background:rgba(255,255,255,.06);color:#aab3c4}.tag.no i{border:1.6px solid #aab3c4}
.wbig{width:44px;height:44px;border-radius:50%;background:#E6EAF2;display:flex;align-items:center;justify-content:center}
.st{width:48px;display:flex;justify-content:center}.ok{color:#5ad18a;display:flex}
.ibtns{display:flex;gap:12px;margin-right:6px}.ibtns .btn{height:60px;font-size:21px;padding:0 18px 0 14px}
.label{position:absolute;right:56px;bottom:8px;font-size:19px;color:#c9d3e6}`;
(async () => {
  const browser = await chromium.launch();
  const tab = await browser.newPage({ viewport: { width: 1920, height: 1080 } });
  for (const [f, title, pills, rows, focus, show, label, big] of shots) {
    const body = `${nav}<div class="sheet">${head(title, pills)}${rows.map((r, i) => row(r, i === focus, show, big)).join('')}</div><div class="label">${label}</div>`;
    await tab.setContent(`<html><head><style>${css}</style></head><body>${body}</body></html>`, { waitUntil: 'load' });
    await tab.evaluate(() => document.fonts.ready);
    await tab.screenshot({ path: path.join(out, f) });
  }
  await browser.close();
})();
