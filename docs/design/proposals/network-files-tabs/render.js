// Exploring (owner, 5 Nov 2026), not tied to any version: Network & Files as one page with tabs, no panels.
// Run: FONT_DIR=<roboto woff2 dir> node render.js
const fs = require('fs');
const path = require('path');
const { chromium } = require('playwright');
const fontDir = process.env.FONT_DIR;
const font = w => `@font-face{font-family:R;font-weight:${w};src:url(data:font/woff2;base64,${fs.readFileSync(path.join(fontDir, `roboto-latin-${w}-normal.woff2`)).toString('base64')}) format('woff2')}`;
const nav = `<div class="nav"><span class="brand">SUPERNOVA</span><span>Home</span><span>Movies</span><span>TV Shows</span>
  <span>Documentaries</span><span class="sp"></span><span class="on">Network &amp; Files</span><span class="ico">⌕</span>
  <span class="ico">⚙</span><span class="clock">23:40</span></div>`;
const icon = (d, s = 34) => `<svg width="${s}" height="${s}" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.7" stroke-linecap="round" stroke-linejoin="round">${d}</svg>`;
const I = {
  local: '<path d="M4 15l2-9h12l2 9"/><rect x="3" y="15" width="18" height="5" rx="1.5"/>',
  shares: '<rect x="4" y="3" width="16" height="7" rx="1.5"/><rect x="4" y="14" width="16" height="7" rx="1.5"/><path d="M8 6.5h.1M8 17.5h.1"/>',
  library: '<path d="M3 12h4l2-5 4 10 2-5h6"/>',
  plus: '<path d="M12 5v14M5 12h14"/>',
  scan: '<path d="M4 12a8 8 0 0 1 14-5.3M20 12a8 8 0 0 1-14 5.3"/><path d="M18 3v4h-4M6 21v-4h4"/>',
  usb: '<rect x="7" y="9" width="10" height="12" rx="2"/><path d="M9 9V3h6v6M11 5.5h.1M13 5.5h.1"/>',
  tv: '<rect x="3" y="5" width="18" height="12" rx="2"/><path d="M8 21h8M12 17v4"/>',
  cloud: '<path d="M7 18h10a4 4 0 0 0 .6-8A6 6 0 0 0 6 9.5 4.3 4.3 0 0 0 7 18z"/>',
  folder: '<path d="M3 7a2 2 0 0 1 2-2h4l2 2h8a2 2 0 0 1 2 2v8a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2z"/>',
  film: '<rect x="3" y="4" width="18" height="16" rx="2"/><path d="M7 4v16M17 4v16M3 9h4M3 15h4M17 9h4M17 15h4"/>',
  pc: '<rect x="3" y="4" width="18" height="12" rx="2"/><path d="M7 20h10"/>',
  chev: '<path d="M9 6l6 6-6 6"/>',
  grid: '<rect x="4" y="4" width="7" height="7" rx="1"/><rect x="13" y="4" width="7" height="7" rx="1"/><rect x="4" y="13" width="7" height="7" rx="1"/><rect x="13" y="13" width="7" height="7" rx="1"/>',
  sort: '<path d="M7 4v16M3 16l4 4 4-4M17 20V4M13 8l4-4 4 4"/>',
  filter: '<path d="M4 5h16l-6 8v6l-4-2v-4z"/>',
  play: '<path d="M7 4l13 8-13 8z"/>',
  info: '<circle cx="12" cy="12" r="9"/><path d="M12 11v6M12 7.5h.1"/>',
  check: '<path d="M5 12l5 5 9-10"/>',
  edit: '<path d="M4 20h4L19 9l-4-4L4 16z"/>',
  test: '<circle cx="12" cy="12" r="9"/><path d="M8 12l3 3 5-6"/>',
  remove: '<path d="M5 7h14M10 7V4h4v3M7 7l1 13h8l1-13"/>',
  open: '<path d="M9 6l6 6-6 6"/>',
  search: '<circle cx="11" cy="11" r="7"/><path d="M20 20l-4-4"/>',
  globe: '<circle cx="12" cy="12" r="9"/><path d="M3 12h18M12 3c3 3.5 3 14.5 0 18M12 3c-3 3.5-3 14.5 0 18"/>',
  lock: '<rect x="5" y="11" width="14" height="10" rx="2"/><path d="M8 11V7a4 4 0 0 1 8 0v4"/>',
  more: '<path d="M5 12h.1M12 12h.1M19 12h.1"/>',
  eye: '<path d="M2 12s4-7 10-7 10 7 10 7-4 7-10 7S2 12 2 12z"/><circle cx="12" cy="12" r="3"/>',
};
const tabs = (open, focusTab) => `<div class="tabs">${[['local', 'Local Storage'], ['shares', 'Network Shares'], ['library', 'Library']]
  .map(([k, t]) => `<span class="tab ${k === open ? 'open' : ''} ${k === focusTab ? 'focus' : ''}">${icon(I[k], 30)}${t}</span>`).join('')}</div>`;
const bar = (used, total) => `<div class="bar"><i style="width:${Math.round(used / total * 100)}%"></i></div>`;
const row = (ic, name, line, right, focus, extra = '') => `<div class="row ${focus ? 'focus' : ''}">
  <span class="ri">${icon(I[ic], 40)}</span><div class="rt"><div class="rn">${name}</div><div class="rl">${line}</div>${extra}</div>
  <div class="rr">${right}</div>${right.includes('class="acts"') ? '' : `<span class="rc">${icon(I.chev, 30)}</span>`}</div>`;
const acts = list => `<span class="acts">${list.map(([ic, t]) => `<span class="ab">${icon(I[ic], 26)}${t}</span>`).join('')}</span>`;
const dot = (c, t) => `<span class="st"><i style="background:${c}"></i>${t}</span>`;
const art = [['#5b3a2e', '#1f1512'], ['#2e4a5b', '#121c22'], ['#4b5b2e', '#1a2012'], ['#5b2e4a', '#20121a'], ['#2e5b4f', '#12201d']];
const titles = ['Conclave', 'Flow', 'The Brutalist', 'Anora', 'Dune: Part Two'];

const local = `${nav}<div class="page">${tabs('local', 'local')}
<div class="sec">Devices</div>
${row('tv', 'This Shield', '310 videos · 14.2 GB free of 32 GB', '', false, bar(17.8, 32))}
${row('usb', 'Backup #1', 'SanDisk USB drive · 1,203 videos · 1.1 TB free of 2 TB', '', false, bar(0.9, 2))}
</div>`;

const shares = `${nav}<div class="page">${tabs('shares', '')}
<div class="sech"><div class="sec">Your connections</div><span class="b">${icon(I.plus, 28)}Add connection</span></div>
${row('cloud', 'Put.io · heretostream', '98.9 GB of 107 GB used · library sync automatic, last 3 hours ago', dot('#5fd38d', 'Reachable'), false)}
${row('shares', 'Living-room NAS', 'SMB · 3 folders in your library · 512 videos', acts([['open', 'Open'], ['edit', 'Edit'], ['test', 'Test'], ['remove', 'Remove']]), true)}
${row('pc', 'Office PC', 'SFTP · not in your library', dot('#f2b544', 'Not reachable · last seen yesterday'), false)}
<div class="sec">Found on your network</div>
${row('pc', 'DESKTOP-7Q', 'SMB · not added yet', '<span class="hint">Add</span>', false)}
</div>`;

const types = `${nav}<div class="page">${tabs('shares', '')}
<div class="crumbs">Network Shares <b>›</b> <span>Add a connection</span></div>
<div class="sec first">Recommended for you</div>
${row('cloud', 'Put.io', 'Sign in with your Put.io account · browsing, library sync and search in one', '<span class="ok">Already connected</span>', false)}
<div class="sec">On your home network</div>
${row('pc', 'A computer or NAS', 'Windows, Mac, Linux or a NAS (Synology, QNAP…) · Supernova finds them for you', acts([['search', 'Find on my network'], ['plus', 'Enter address']]), true)}
<div class="sec">On the internet</div>
${row('globe', 'A WebDAV server', 'Cloud storage and servers that offer WebDAV (secure HTTPS)', '', false)}
${row('lock', 'An SFTP server', 'A server you reach over SSH', '', false)}
${row('more', 'More types', 'FTP, FTP with TLS, and plain (unencrypted) WebDAV', '', false)}
</div>`;

const field = (l, v, dim, eye) => `<div class="fld"><span class="fl">${l}</span><span class="fv ${dim ? 'dim' : ''}">${v}</span>${eye ? `<span class="fe">${icon(I.eye, 30)}</span>` : ''}</div>`;
const form = `${nav}<div class="page">${tabs('shares', '')}
<div class="crumbs">Network Shares <b>›</b> Add a connection <b>›</b> <span>WebDAV server</span></div>
<div class="two form">
<div class="col">
${field('Name', 'Put.io Films')}${field('Address', 'webdav.put.io')}${field('Port', '443 · default for HTTPS', true)}
${field('Folder', '/Films')}${field('Username', 'heretostream')}${field('Password', '••••••••••••', false, true)}
<div class="kv"><span>Remember password</span><span class="sw on"><i></i></span></div>
<div class="btnrow"><span class="b focus">${icon(I.test, 28)}Test connection</span><span class="b">${icon(I.chev, 28)}Save and browse</span></div>
</div>
<div class="col"><div class="rn">Test connection</div><div class="rl p">Checks the address and password without saving anything.</div>
<div class="rn gap">Tips</div><div class="rl p">Port and Folder are optional. For Put.io use <b>webdav.put.io</b> and your Put.io username.</div></div>
</div></div>`;

const library = `${nav}<div class="page">${tabs('library', '')}
<div class="two">
<div class="col"><div class="sec">Scanning</div>
<div class="scanbox"><span class="b focus big">${icon(I.scan, 32)}Scan now</span>
<div class="sl"><div class="rn">Last scan 12 minutes ago</div><div class="rl">Put.io › Films · 2 min 14 s · 3 new, 1 updated</div></div></div>
<div class="kv"><span>Automatic scan</span><b>Every hour</b></div>
<div class="kv"><span>When Supernova opens</span><b>On</b></div>
<div class="sec small">Recent scans</div>
<div class="mini"><span>Today 23:28</span><span>Put.io › Films</span><span>3 new · 1 updated</span></div>
<div class="mini"><span>Today 22:28</span><span>Living-room NAS</span><span>No changes</span></div>
<div class="mini"><span>Today 21:28</span><span>Backup #1</span><span class="warnc">Stopped: drive removed</span></div>
</div>
<div class="col"><div class="sec">Library Health</div>
${row('folder', 'Missing files', 'In your library but no longer found', '<span class="count amber">3</span>', false)}
${row('film', "Couldn't be read", 'Files Supernova could not open', '<span class="count amber">2</span>', false)}
${row('library', 'Not matched', 'Videos without a film or show', '<span class="count">14</span>', false)}
</div></div></div>`;

const files = [['folder', '4K', 'Folder · in your library', ''], ['folder', 'Classics', 'Folder · in your library', ''],
  ['film', 'Conclave (2024).mkv', '2024 · in your library · 14.2 GB', ''], ['film', 'home-video-2019.mp4', 'Not added · 1.1 GB', ''],
  ['film', 'Nosferatu (2024).mkv', '2024 · in your library · 18.9 GB', '']];
const toolbar = (gridOn) => `<div class="crumbrow"><div class="crumbs">Local Storage <b>›</b> Backup #1 <b>›</b> <span>Films</span></div>
<div class="tools"><span class="b">${icon(gridOn ? I.film : I.grid, 26)}${gridOn ? 'List' : 'Grid'}</span><span class="b">${icon(I.sort, 26)}Sort: Name</span><span class="b">${icon(I.filter, 26)}Videos only</span></div></div>`;
const folder = `${nav}<div class="page">${tabs('local', '')}${toolbar(false)}
${files.map(([ic, n, l], i) => row(ic, n, l, i === 2 ? acts([['play', 'Play'], ['info', 'Details'], ['check', 'Mark as watched'], ['remove', 'Exclude']]) : '', i === 2)).join('')}
</div>`;
const gtitles = ['4K', 'Classics', 'Conclave', 'home-video-2019', 'Nosferatu', 'Flow', 'Anora', 'The Brutalist', 'Dune: Part Two', 'Heat', 'Arrival', 'Gravity', 'Ex Machina', 'Sicario', 'Prisoners', 'Her'];
const gridv = `${nav}<div class="page">${tabs('local', '')}${toolbar(true)}
<div class="g8">${gtitles.map((t, i) => `<div class="gc"><div class="gp ${i === 2 ? 'focus' : ''}" style="${i < 2 ? '' : `background:linear-gradient(160deg,${art[i % 5][0]},${art[i % 5][1]})`}">${i < 2 ? icon(I.folder, 60) : ''}${i >= 2 && i !== 3 ? '<i class="sd"></i>' : ''}</div><div class="gt">${t}</div><div class="gl">${i < 2 ? 'Folder' : i === 3 ? 'Not added' : '2024'}</div></div>`).join('')}</div>
</div>`;
const css = `${font(400)}${font(500)}*{box-sizing:border-box;margin:0}
body{width:1920px;height:1080px;overflow:hidden;font-family:R;color:#fff;background:#16181C;position:relative}
.nav{position:absolute;left:56px;right:56px;top:28px;height:56px;display:flex;align-items:center;gap:30px;font-size:34px;color:#e8edf8}
.nav .brand{font-size:36px;margin-right:40px;letter-spacing:1px}.nav .sp{flex:1}.nav .on{font-weight:500;color:#fff}.nav .clock{margin-left:20px}
.page{position:absolute;left:96px;right:96px;top:124px;bottom:40px}
.tabs{display:flex;gap:14px}.tab{height:68px;padding:0 30px;border-radius:22px;display:inline-flex;align-items:center;gap:14px;font-size:28px;font-weight:500;color:#dfe5f1}
.tab.open{background:#454B55;color:#fff}.tab.focus{background:#F4F6FB;color:#0B0D12}
.sec{font-size:26px;font-weight:500;color:#B7BFCC;margin:46px 0 14px;letter-spacing:.3px}.sec.small{margin-top:34px;font-size:24px}
.sech{display:flex;align-items:flex-end;justify-content:space-between}.sech .b{margin-bottom:10px}
.row{display:flex;align-items:center;gap:28px;min-height:112px;padding:18px 30px;box-shadow:inset 0 -2px 0 rgba(255,255,255,.08)}
.row.focus{background:#F4F6FB;color:#0B0D12;border-radius:24px;box-shadow:none}.row.focus .rl,.row.focus .st{color:#3A4256}
.ri{color:#c9d3e6}.row.focus .ri{color:#0B0D12}.rt{flex:1}.rn{font-size:32px;font-weight:500}.rl{font-size:24px;color:#C9D3E6;margin-top:6px}
.rr{font-size:24px}.rc{color:rgba(255,255,255,.35)}.row.focus .rc{color:#0B0D12}
.bar{margin-top:14px;width:560px;height:8px;border-radius:4px;background:rgba(255,255,255,.14)}.bar i{display:block;height:100%;border-radius:4px;background:#C9D3E6}
.st{display:inline-flex;align-items:center;gap:12px;color:#dfe5f1}.st i{width:14px;height:14px;border-radius:50%}
.hint{font-size:24px;font-weight:500;color:#dfe5f1}
.cards{display:flex;gap:24px}.card{width:400px;height:225px;border-radius:16px;box-shadow:0 0 0 2px rgba(255,255,255,.28)}
.ct{font-size:26px;font-weight:500;margin-top:14px}.cl{font-size:22px;color:#C9D3E6;margin-top:4px}
.b{height:64px;padding:0 26px;border-radius:20px;background:#2a2d33;display:inline-flex;align-items:center;gap:12px;font-size:26px;font-weight:500}
.b.focus{background:#F4F6FB;color:#0B0D12}.b.big{height:76px;padding:0 34px;font-size:30px}
.two{display:grid;grid-template-columns:1fr 1fr;gap:80px}
.scanbox{display:flex;align-items:center;gap:30px;margin-bottom:22px}
.kv{display:flex;justify-content:space-between;font-size:28px;padding:20px 4px;border-bottom:2px solid rgba(255,255,255,.08)}.kv span{color:#C9D3E6}.kv b{font-weight:500}
.mini{display:grid;grid-template-columns:200px 1fr auto;font-size:24px;padding:12px 4px;color:#dfe5f1}.mini span:first-child{color:#9aa3b2}
.warnc{color:#f2b544}
.count{min-width:56px;height:56px;padding:0 16px;border-radius:28px;background:rgba(255,255,255,.12);display:inline-flex;align-items:center;justify-content:center;font-size:28px;font-weight:500}
.count.amber{background:#F2B544;color:#111}
.crumbs{font-size:30px;color:#C9D3E6;margin:44px 0 18px}.crumbs b{color:#7d8696;margin:0 10px;font-weight:400}.crumbs span{color:#fff;font-weight:500}
.acts{display:flex;gap:12px}.ab{height:56px;padding:0 22px;border-radius:18px;background:#2a2d33;color:#fff;display:inline-flex;align-items:center;gap:10px;font-size:24px;font-weight:500}
.row.focus .ab{background:#dde1ea;color:#0B0D12}.row.focus .ab:first-child{background:#0B0D12;color:#fff}
.ok{color:#5fd38d;font-size:24px}.sec.first{margin-top:10px}
.crumbrow{display:flex;align-items:center;justify-content:space-between;margin:34px 0 14px}.crumbrow .crumbs{margin:0}
.tools{display:flex;gap:12px}.tools .b{height:58px;font-size:24px}
.g8{display:grid;grid-template-columns:repeat(8,1fr);gap:26px 22px;margin-top:10px}
.gp{height:118px;border-radius:14px;box-shadow:0 0 0 2px rgba(255,255,255,.28);display:flex;align-items:center;justify-content:center;color:#c9d3e6;position:relative}
.gp.focus{box-shadow:0 0 0 4px #fff;transform:scale(1.06)}.sd{position:absolute;right:10px;top:10px;width:14px;height:14px;border-radius:50%;background:#5fd38d}
.gt{font-size:22px;font-weight:500;margin-top:10px;white-space:nowrap;overflow:hidden;text-overflow:ellipsis}.gl{font-size:19px;color:#C9D3E6;margin-top:2px}
.form{margin-top:6px}.fld{display:flex;align-items:center;height:74px;padding:0 26px;border-radius:18px;background:rgba(255,255,255,.06);margin-bottom:12px;font-size:28px}
.fl{width:180px;color:#C9D3E6;font-size:24px}.fv{flex:1}.fv.dim{color:#8a93a3}.fe{color:#c9d3e6}
.btnrow{display:flex;gap:16px;margin-top:20px}.p{line-height:1.45;margin-top:10px}.p b{color:#fff;font-weight:500}.gap{margin-top:40px}
.sw{width:76px;height:40px;border-radius:20px;background:#3a3f48;position:relative}.sw i{position:absolute;top:4px;left:4px;width:32px;height:32px;border-radius:50%;background:#fff}.sw.on{background:#cfd6e2}.sw.on i{left:40px;background:#16181C}`;
(async () => {
  const browser = await chromium.launch();
  const tab = await browser.newPage({ viewport: { width: 1920, height: 1080 } });
  for (const [f, body] of [['01-local-storage.png', local], ['02-network-shares.png', shares], ['03-library.png', library], ['04-inside-a-folder.png', folder],
    ['05-folder-grid.png', gridv], ['06-add-connection.png', types], ['07-add-connection-form.png', form]]) {
    await tab.setContent(`<html><head><style>${css}</style></head><body>${body}</body></html>`, { waitUntil: 'load' });
    await tab.evaluate(() => document.fonts.ready);
    await tab.screenshot({ path: path.join(__dirname, f) });
  }
  await browser.close();
})();
