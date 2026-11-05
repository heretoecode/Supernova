// Proposal (walk-through after 0.154): Network Shares, Put.io first. Same header, rows and focus as Local Storage.
// Run: FONT_DIR=<roboto woff2 dir> node render.js
const fs = require('fs');
const path = require('path');
const { chromium } = require('playwright');
const out = __dirname;
const fontDir = process.env.FONT_DIR;
const font = w => `@font-face{font-family:R;font-weight:${w};src:url(data:font/woff2;base64,${fs.readFileSync(path.join(fontDir, `roboto-latin-${w}-normal.woff2`)).toString('base64')}) format('woff2')}`;
const P = {
  servers: '<rect x="4" y="3" width="16" height="7" rx="1.5"/><rect x="4" y="14" width="16" height="7" rx="1.5"/><path d="M8 6.5h.1M8 17.5h.1"/>',
  cloud: '<path d="M7 18h10a4 4 0 0 0 .5-8A6 6 0 0 0 6 9.5 4.3 4.3 0 0 0 7 18z"/>',
  folder: '<path d="M3 6.5A1.5 1.5 0 0 1 4.5 5H9l2 2.5h8.5A1.5 1.5 0 0 1 21 9v8.5a1.5 1.5 0 0 1-1.5 1.5h-15A1.5 1.5 0 0 1 3 17.5z"/>',
  video: '<rect x="3" y="5" width="18" height="14" rx="2"/><path d="M10 9.5v5l4.5-2.5z" fill="currentColor"/>',
  play: '<path d="M8 5l11 7-11 7z" fill="currentColor"/>', info: '<circle cx="12" cy="12" r="9"/><path d="M12 11v6M12 7.5v.5"/>',
  lib: '<path d="M5 4v16M9 4v16M13 4l5 15"/>', check: '<circle cx="12" cy="12" r="9"/><path d="M8 12.5l2.7 2.7L16 9.7"/>',
  grid: '<rect x="4" y="4" width="7" height="7"/><rect x="13" y="4" width="7" height="7"/><rect x="4" y="13" width="7" height="7"/><rect x="13" y="13" width="7" height="7"/>',
  sort: '<path d="M7 4v16M3 16l4 4 4-4M17 20V4M13 8l4-4 4 4"/>', filter: '<path d="M4 5h16l-6 7v6l-4 2v-8z"/>',
  sync: '<path d="M4 12a8 8 0 0 1 14-5.3M20 12a8 8 0 0 1-14 5.3"/><path d="M18 3v4h-4M6 21v-4h4"/>',
  open: '<path d="M9 6l6 6-6 6"/>', plus: '<path d="M12 4v16M4 12h16"/>', search: '<circle cx="11" cy="11" r="6.5"/><path d="M16 16l4.5 4.5"/>',
  retry: '<path d="M20 12a8 8 0 1 1-2.3-5.7M20 4v5h-5"/>',
};
const ic = (k, s = 32) => `<svg width="${s}" height="${s}" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.7" stroke-linecap="round" stroke-linejoin="round">${P[k]}</svg>`;
const btn = (k, t, f) => `<div class="btn${f ? ' focus' : ''}">${ic(k, 28)}<span>${t}</span></div>`;
const nav = `<div class="nav"><span class="brand">SUPERNOVA</span><span>Home</span><span>Movies</span><span>TV Shows</span><span>Documentaries</span><span class="sp"></span><span class="on">Network &amp; Files</span><span class="clock">20:51</span></div>`;
const chip = (t, k) => `<span class="chip">${k ? ic(k, 24) : ''}${t}</span>`;
const pill = (t, dot) => `<span class="pill${dot ? ' ' + dot : ''}">${dot ? '<i></i>' : ''}${t}</span>`;
const head = (icon, crumbs, title, pills, tools) => `<div class="head"><div class="ftile">${ic(icon, 44)}</div>
  <div class="hcol">${crumbs ? `<div class="crumb">${crumbs}</div>` : ''}<div class="title">${title}</div><div class="pills">${pills}</div></div>
  ${tools ? `<div class="tools">${tools}</div>` : ''}</div><div class="rule"></div>`;
const heading = t => `<div class="grp">${t}<i></i></div>`;
const row = (icon, l1, l2, opts = {}) => `<div class="row${opts.focus ? ' focus' : ''}${opts.sel ? ' sel' : ''}${opts.dim ? ' dim' : ''}">${ic(icon)}
  <span class="two"><span class="name1">${l1}</span><span class="l2">${l2}</span></span>${opts.right || ''}${opts.buttons || ''}${opts.mark || '<span class="st"></span>'}</div>`;
const bar = (used, total) => `<span class="space"><span class="bar"><i style="width:${Math.round(100 * used / total)}%"></i></span></span>`;
const tick = `<span class="st ok">${ic('check', 26)}</span>`;

// 01 — the Network Shares page: Put.io as one service, its library folders, then other shares
const landing = focus => `${nav}<div class="sheet">${head('servers', '', 'Network Shares', pill('1 service') + pill('Put.io reachable', 'ok') + pill('Library synced 3 hours ago'),
  btn('plus', 'Add share'))}
  ${heading('Put.io')}
  ${row('cloud', 'Put.io · heretostream', '98.9 GB of 107 GB used · Library sync: automatic, last 3 hours ago', {
    focus: focus === 'service', right: bar(98.9, 107),
    buttons: focus === 'service' ? `<span class="ibtns">${btn('sync', 'Sync now')}${btn('search', 'Search')}${btn('info', 'Account')}</span>` : ''})}
  ${row('folder', 'Films', 'Put.io · 9 videos · in your library', { focus: focus === 'films',
    buttons: focus === 'films' ? `<span class="ibtns">${btn('open', 'Open')}${btn('lib', 'Exclude')}</span>` : '' })}
  ${row('folder', 'TV', 'Put.io · 12 shows · in your library')}
  ${heading('Other shares')}
  <div class="empty">No other shares. A NAS or computer added under <b>Add Connection</b> appears here.</div></div>`;

// 02 — inside Put.io › Films: same page as Local Storage; matched titles first, release names out of sight
const films = [
  ['Black Bag', '2025 · 1h 34m · 1080p · AAC 5.1 · 1.7 GB', 'watched'],
  ["Don't Say Good Luck", '2026 · 1h 51m · 1080p · AAC 5.1 · 2.1 GB · <span class="res">Resume at 41:10 (37%)</span>'],
  ['Forgotten Island', '2026 · 1h 39m · 1080p · 5.1 · 1.8 GB'],
  ['Mayday', '2026 · 1h 52m · 1080p · HEVC · 5.1 · 2.6 GB', 'watched'],
  ['Passport to Paris', '1999 · 1h 30m · 720p · AAC · 0.8 GB'],
  ['The.Quiet.Coast.2024.720p.WEBRip.x264', 'Video · not matched yet · 1.1 GB'],
];
const folder = state => `${nav}<div class="sheet">${head('folder', `${chip('Network Shares', 'servers')}<b>›</b>${chip('Put.io', 'cloud')}`, 'Films',
  pill('9 videos') + pill('15.2 GB') + pill('In your library', 'lib') + pill('2 watched · 1 in progress'),
  btn('grid', 'Grid') + btn('sort', 'Sort: Title') + btn('filter', 'Videos only'))}
  ${films.map(([t, l2, st], i) => row('video', t, l2, { focus: i === 0 && state === 'list', mark: st ? tick : '',
    buttons: i === 0 ? `<span class="ibtns">${btn('play', 'Play')}${btn('info', 'Details')}${btn('lib', 'Exclude')}</span>` : '' })).join('')}</div>`;

// 03 — Put.io unreachable: the service row says so, with Retry; the folders stay listed but dimmed
const down = () => `${nav}<div class="sheet">${head('servers', '', 'Network Shares', pill('1 service') + pill('Put.io not reachable', 'warn') + pill('Library kept as it is'),
  btn('plus', 'Add share'))}
  ${heading('Put.io')}
  ${row('cloud', 'Put.io · heretostream', "Couldn't reach Put.io at 20:51 · check the internet connection · your library and progress are safe", {
    focus: true, buttons: `<span class="ibtns">${btn('retry', 'Retry')}${btn('info', 'Account')}</span>` })}
  ${row('folder', 'Films', 'Put.io · not reachable right now', { dim: true })}
  ${row('folder', 'TV', 'Put.io · not reachable right now', { dim: true })}
  ${heading('Other shares')}
  <div class="empty">No other shares. A NAS or computer added under <b>Add Connection</b> appears here.</div></div>`;

const labels = {
  '01-network-shares.png': 'Network Shares: Put.io is one service (account + its folders); focused row shows its buttons',
  '02-network-shares-folder-focused.png': 'Same page, focus on the Films folder',
  '03-putio-films.png': 'Inside Put.io › Films: matched titles first; the release name only until a match is found',
  '04-putio-unreachable.png': 'Put.io unreachable: said once, on the service row, with Retry; folders dimmed',
};
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
.title{font-size:36px;font-weight:500;white-space:nowrap;overflow:hidden;text-overflow:ellipsis}
.pills{display:flex;gap:10px;margin-top:4px}.pill{font-size:19px;color:#dfe5f1;background:rgba(255,255,255,.08);border-radius:12px;padding:5px 12px;display:inline-flex;align-items:center;gap:8px}
.pill i{width:10px;height:10px;border-radius:50%}.pill.lib i,.pill.ok i{background:#5ad18a}.pill.warn i{background:#f0b44c}
.rule{height:1px;background:rgba(255,255,255,.14);margin:0 6px 6px}
.grp{display:flex;align-items:center;gap:16px;font-size:20px;color:#b7bfcc;margin:14px 12px 4px}.grp i{flex:1;height:2px;background:rgba(255,255,255,.22)}
.tools{display:flex;gap:14px}
.btn{height:64px;padding:0 22px 0 18px;border-radius:22px;background:#2a2d33;display:inline-flex;align-items:center;gap:10px;font-size:22px;font-weight:500;white-space:nowrap}
.btn.focus{background:#F4F6FB;color:#0B0D12}
.row{display:flex;align-items:center;gap:20px;height:100px;padding:0 20px;border-radius:24px;font-size:26px;font-weight:500}
.row.focus{box-shadow:inset 0 0 0 3px #fff}.row.sel{background:rgba(255,255,255,.1)}.row.dim{opacity:.5}
.two{flex:1;display:flex;flex-direction:column;gap:6px;min-width:0}.name1{white-space:nowrap;overflow:hidden;text-overflow:ellipsis}
.l2{font-weight:400;font-size:21px;color:#C9D3E6;white-space:nowrap;overflow:hidden;text-overflow:ellipsis}.res{color:#8fc3ff}
.st{width:34px;display:flex;justify-content:center;flex:none}.st.ok{color:#5ad18a}
.ibtns{display:flex;gap:12px;margin-right:6px}.ibtns .btn{height:62px;font-size:21px;padding:0 18px 0 14px}
.space{width:180px;flex:none}.bar{display:block;height:8px;border-radius:4px;background:rgba(255,255,255,.18);overflow:hidden}.bar i{display:block;height:100%;background:#f0b44c}
.empty{font-size:21px;color:#C9D3E6;margin:14px 20px}.empty b{font-weight:500;color:#fff}
.label{position:absolute;right:56px;bottom:8px;font-size:19px;color:#c9d3e6}`;
const pages = [['01-network-shares.png', landing('service')], ['02-network-shares-folder-focused.png', landing('films')],
               ['03-putio-films.png', folder('list')], ['04-putio-unreachable.png', down()]];
(async () => {
  const browser = await chromium.launch();
  const tab = await browser.newPage({ viewport: { width: 1920, height: 1080 } });
  for (const [f, body] of pages) {
    await tab.setContent(`<html><head><style>${css}</style></head><body>${body}<div class="label">${labels[f]}</div></body></html>`, { waitUntil: 'load' });
    await tab.evaluate(() => document.fonts.ready);
    await tab.screenshot({ path: path.join(out, f) });
  }
  await browser.close();
})();
