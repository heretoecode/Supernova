// Proposal (0.154 walk-through): Network & Files › Local Storage — drive list, folder, selected video.
// Approved 0.154 look (charcoal, More-menu rows, no dividers, Medium labels). Sizes/durations are illustrative.
// Run: FONT_DIR=<roboto woff2 dir> node render.js
const fs = require('fs');
const path = require('path');
const { chromium } = require('playwright');
const out = __dirname;
const fontDir = process.env.FONT_DIR;
const font = w => `@font-face{font-family:R;font-weight:${w};src:url(data:font/woff2;base64,${fs.readFileSync(path.join(fontDir, `roboto-latin-${w}-normal.woff2`)).toString('base64')}) format('woff2')}`;

const P = {
  drive: '<path d="M6 4h12l2 11H4z"/><path d="M4 15h16v5H4zM8 17.5h.1"/>',
  usb: '<rect x="8" y="2" width="8" height="7" rx="1"/><path d="M6 9h12v9a4 4 0 0 1-4 4h-4a4 4 0 0 1-4-4z"/><path d="M10 5h.1M14 5h.1"/>',
  folder: '<path d="M3 6.5A1.5 1.5 0 0 1 4.5 5H9l2 2.5h8.5A1.5 1.5 0 0 1 21 9v9.5a1.5 1.5 0 0 1-1.5 1.5h-15A1.5 1.5 0 0 1 3 18.5z"/>',
  video: '<rect x="3" y="5" width="18" height="14" rx="2"/><path d="M10 9.5v5l4.5-2.5z" fill="currentColor"/>',
  play: '<path d="M8 5l11 7-11 7z" fill="currentColor"/>',
  info: '<circle cx="12" cy="12" r="9"/><path d="M12 11v6M12 7.5v.5"/>',
  lib: '<path d="M5 4v16M9 4v16M13 4l5 15"/>',
  open: '<path d="M4 12h14M13 6l6 6-6 6"/>',
  grid: '<rect x="4" y="4" width="7" height="7"/><rect x="13" y="4" width="7" height="7"/><rect x="4" y="13" width="7" height="7"/><rect x="13" y="13" width="7" height="7"/>',
  sort: '<path d="M7 4v16M3 16l4 4 4-4M17 20V4M13 8l4-4 4 4"/>',
  filter: '<path d="M4 5h16l-6 7v6l-4 2v-8z"/>',
  check: '<circle cx="12" cy="12" r="9"/><path d="M8 12.5l2.7 2.7L16 9.7"/>',
  save: '<path d="M6 3h12v18l-6-4-6 4z"/>',
};
const ic = (k, s = 32) => `<svg width="${s}" height="${s}" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.7" stroke-linecap="round" stroke-linejoin="round">${P[k]}</svg>`;
const bar = (used, total) => `<div class="bar"><i style="width:${Math.round(used / total * 100)}%"></i></div>`;
const btn = (k, t, focus) => `<div class="btn${focus ? ' focus' : ''}">${ic(k, 30)}<span>${t}</span></div>`;
const nav = `<div class="nav"><span class="brand">SUPERNOVA</span><span>Home</span><span>Movies</span><span>TV Shows</span><span>Documentaries</span><span class="sp"></span><span class="on">Network &amp; Files</span><span class="clock">17:35</span></div>`;
const crumb = parts => `<div class="crumb">${parts.map((p, i) => `<span class="${i === parts.length - 1 ? 'last' : ''}">${p}</span>`).join('<b>›</b>')}</div>`;
const fact = (k, v) => `<div class="fact"><span>${k}</span><span>${v}</span></div>`;

// 1 — drive list
const drives = [
  ['drive', 'Internal storage', 'This Shield', 7.2, 11.4, false],
  ['usb', 'Backup #2', 'USB drive · Micron', 1188, 1800, true],
  ['usb', 'Backup #1', 'USB drive · SanDisk', 730, 931, false],
];
const fmt = gb => gb >= 1000 ? (gb / 1000).toFixed(1) + ' TB' : (gb >= 100 ? Math.round(gb) : gb.toFixed(1)) + ' GB';
const driveRows = drives.map(([k, n, sub, used, total, f]) => `<div class="row tall${f ? ' focus' : ''}">${ic(k, 38)}<div class="two">
  <div class="name">${n}<small>${sub}</small></div>${bar(used, total)}<div class="sub">${fmt(total - used)} free of ${fmt(total)}</div></div></div>`).join('');
const page1 = `${nav}${crumb(['Network &amp; Files', 'Local Storage'])}
<div class="panel left">${driveRows}</div>
<div class="panel right"><div class="ph">${ic('usb', 44)}<div><div class="pt">Backup #2</div><div class="ps">USB drive · Micron</div></div></div>
  ${bar(1188, 1800)}<div class="sub big">612 GB free of 1.8 TB</div>
  ${fact('In your library', 'Yes · 1,284 videos')}${fact('Last scanned', 'Today, 17:31')}
  <div class="actions">${btn('open', 'Open')}${btn('lib', 'Exclude from Library')}</div></div>`;

// 2 — folder
const folderRows = [
  ['folder', '1883 – The Bass Reeves Story', '8 videos', true],
  ['folder', 'Mr. Robot', '45 videos'], ['folder', 'Tulsa King', '19 videos'], ['folder', 'War', '6 videos'],
  ['folder', 'Kingdom', '3 videos'],
].map(([k, n, s, f]) => `<div class="row${f ? ' focus' : ''}">${ic(k)}<span class="name1">${n}</span><span class="meta">${s}</span></div>`).join('');
const toolbar = `<div class="tools">${btn('grid', 'Grid')}${btn('sort', 'Sort: Name')}${btn('filter', 'Showing: Videos only')}</div>`;
const page2 = `${nav}${crumb(['Local Storage', 'Backup #2', 'TV'])}
<div class="panel left">${toolbar}<div class="count">5 folders</div>${folderRows}</div>
<div class="panel right"><div class="ph">${ic('folder', 44)}<div><div class="pt">1883 – The Bass Reeves Story</div><div class="ps">Folder · Backup #2 › TV</div></div></div>
  ${fact('Contains', '8 videos · 2.6 GB')}${fact('In your library', 'Yes')}${fact('Matched to', 'Lawmen: Bass Reeves')}
  <div class="actions">${btn('open', 'Open')}${btn('lib', 'Exclude from Library')}${btn('save', 'Save Location')}</div></div>`;

// 3 — selected video
const eps = [
  ['Lawmen - Bass Reeves - S01E01 - PART I', '52m · 1080p', 'watched', true],
  ['Lawmen - Bass Reeves - S01E02 - PART II', '48m · 1080p', 'progress'],
  ['Lawmen - Bass Reeves - S01E03 - PART III', '51m · 1080p'],
  ['Lawmen - Bass Reeves - S01E04 - PART IV', '47m · 1080p'],
  ['Lawmen - Bass Reeves - S01E05 - PART V', '50m · 1080p'],
].map(([n, s, st, f]) => `<div class="row${f ? ' focus' : ''}">${ic('video')}<span class="name1">${n}</span><span class="meta">${s}</span>
  ${st === 'watched' ? `<span class="st">${ic('check', 26)}</span>` : st === 'progress' ? '<span class="st"><span class="prog"><i></i></span></span>' : '<span class="st"></span>'}</div>`).join('');
const poster = `<div class="poster"><div>LAWMEN<br><small>BASS REEVES</small></div></div>`;
const page3 = `${nav}${crumb(['Local Storage', 'Backup #2', 'TV', '1883 – The Bass Reeves Story'])}
<div class="panel left">${toolbar}<div class="count">8 videos</div>${eps}</div>
<div class="panel right"><div class="vh">${poster}<div><div class="pt">Lawmen: Bass Reeves</div><div class="ps">Season 1 · Episode 1 · Part I</div><div class="ps2">✓ Watched</div></div></div>
  ${fact('Duration', '52 min')}${fact('Video', '1080p · H.264')}${fact('Audio', 'English · AAC 5.1')}${fact('Size', '335 MB')}${fact('In your library', 'Yes')}
  <div class="actions">${btn('play', 'Play')}${btn('info', 'Details')}${btn('lib', 'Exclude')}</div></div>`;

const css = `${font(400)}${font(500)}
*{box-sizing:border-box;margin:0}
body{width:1920px;height:1080px;overflow:hidden;font-family:R;color:#fff;position:relative;
  background:radial-gradient(ellipse 70% 60% at 70% 75%,#1d4fd8 0%,rgba(29,79,216,0) 70%),linear-gradient(180deg,#05081a 0%,#0a1650 45%,#10248a 100%)}
.nav{position:absolute;left:56px;right:56px;top:28px;height:56px;display:flex;align-items:center;gap:30px;font-size:34px;color:#e8edf8}
.nav .brand{font-size:36px;margin-right:40px;letter-spacing:1px}.nav .sp{flex:1}.nav .on{font-weight:500}.nav .clock{margin-left:30px}
.crumb{position:absolute;left:64px;top:112px;font-size:26px;color:#c9d3e6;display:flex;gap:14px;align-items:center}
.crumb b{font-weight:400;opacity:.6}.crumb .last{color:#fff;font-weight:500}
.panel{position:absolute;top:166px;bottom:40px;border-radius:26px;background:rgba(22,24,28,.97);border:2px solid rgba(255,255,255,.22);padding:22px}
.left{left:56px;width:1060px}.right{left:1140px;right:56px;padding:34px 36px}
.row{display:flex;align-items:center;gap:20px;height:74px;padding:0 20px;border-radius:24px;font-size:26px;font-weight:500}
.row.tall{height:132px}
.row.focus{background:#F4F6FB;color:#0B0D12}
.row.focus .meta,.row.focus .sub,.row.focus small{color:#3A4256}
.two{flex:1;display:flex;flex-direction:column;gap:10px}
.name{font-size:28px}.name small{font-weight:400;font-size:22px;color:#C9D3E6;margin-left:14px}
.name1{flex:1;white-space:nowrap;overflow:hidden;text-overflow:ellipsis}
.meta{font-weight:400;font-size:22px;color:#C9D3E6;white-space:nowrap}
.st{width:34px;display:flex;justify-content:center;color:#5ad18a}
.prog{width:30px;height:6px;border-radius:3px;background:rgba(255,255,255,.25);overflow:hidden}.prog i{display:block;width:40%;height:100%;background:#5aa9ff}
.sub{font-weight:400;font-size:21px;color:#C9D3E6}.sub.big{font-size:24px;margin:10px 0 26px}
.bar{height:10px;border-radius:5px;background:rgba(255,255,255,.18);overflow:hidden}.bar i{display:block;height:100%;background:#5aa9ff}
.row.focus .bar{background:rgba(11,13,18,.15)}
.right .bar{height:14px;border-radius:7px}
.ph,.vh{display:flex;gap:22px;align-items:center;margin-bottom:26px}
.pt{font-size:34px;font-weight:500;line-height:1.15}.ps{font-size:22px;color:#C9D3E6;margin-top:6px}.ps2{font-size:22px;color:#5ad18a;margin-top:6px}
.fact{display:flex;justify-content:space-between;gap:20px;font-size:24px;padding:12px 0}
.fact span:first-child{color:#C9D3E6}.fact span:last-child{font-weight:500;text-align:right}
.actions{display:flex;flex-wrap:wrap;gap:16px;margin-top:30px}
.btn{height:72px;padding:0 26px 0 22px;border-radius:24px;background:#2a2d33;display:inline-flex;align-items:center;gap:12px;font-size:24px;font-weight:500;white-space:nowrap}
.btn.focus{background:#F4F6FB;color:#0B0D12}
.tools{display:flex;gap:16px;margin:2px 0 14px}
.count{font-size:21px;color:#C9D3E6;margin:4px 20px 8px}
.poster{width:150px;height:220px;border-radius:14px;flex:none;background:linear-gradient(160deg,#6b4a2a,#2b1d14 70%);display:flex;align-items:flex-end;padding:14px;font-family:Georgia,serif;font-size:26px;line-height:1;color:#f1d9a8;letter-spacing:2px}
.poster small{font-size:14px;letter-spacing:3px}
.label{position:absolute;right:56px;top:116px;font-size:20px;color:#c9d3e6}`;
const pages = [['01-drives.png', page1, 'Local Storage · 1 drive list'], ['02-folder.png', page2, 'Local Storage · 2 a folder'], ['03-video.png', page3, 'Local Storage · 3 selected video']];
(async () => {
  const browser = await chromium.launch();
  const tab = await browser.newPage({ viewport: { width: 1920, height: 1080 } });
  for (const [f, body, label] of pages) {
    await tab.setContent(`<html><head><style>${css}</style></head><body>${body}<div class="label">Proposed · ${label}</div></body></html>`, { waitUntil: 'load' });
    await tab.evaluate(() => document.fonts.ready);
    await tab.screenshot({ path: path.join(out, f) });
  }
  await browser.close();
})();
