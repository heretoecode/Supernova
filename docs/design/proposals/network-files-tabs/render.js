// Exploring (owner, 5–6 Nov 2026), not tied to any version: Network & Files as one page with tabs, no panels.
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
  list: '<path d="M8 6h12M8 12h12M8 18h12M4 6h.1M4 12h.1M4 18h.1"/>',
  sort: '<path d="M7 4v16M3 16l4 4 4-4M17 20V4M13 8l4-4 4 4"/>',
  filter: '<path d="M4 5h16l-6 8v6l-4-2v-4z"/>',
  play: '<path d="M7 4l13 8-13 8z"/>',
  info: '<circle cx="12" cy="12" r="9"/><path d="M12 11v6M12 7.5h.1"/>',
  check: '<path d="M5 12l5 5 9-10"/>',
  edit: '<path d="M4 20h4L19 9l-4-4L4 16z"/>',
  test: '<circle cx="12" cy="12" r="9"/><path d="M8 12l3 3 5-6"/>',
  remove: '<path d="M5 7h14M10 7V4h4v3M7 7l1 13h8l1-13"/>',
  exclude: '<circle cx="12" cy="12" r="9"/><path d="M5.6 5.6l12.8 12.8"/>',
  open: '<path d="M9 6l6 6-6 6"/>',
  search: '<circle cx="11" cy="11" r="7"/><path d="M20 20l-4-4"/>',
  globe: '<circle cx="12" cy="12" r="9"/><path d="M3 12h18M12 3c3 3.5 3 14.5 0 18M12 3c-3 3.5-3 14.5 0 18"/>',
  lock: '<rect x="5" y="11" width="14" height="10" rx="2"/><path d="M8 11V7a4 4 0 0 1 8 0v4"/>',
  more: '<path d="M5 12h.1M12 12h.1M19 12h.1"/>',
  disk: '<ellipse cx="12" cy="6" rx="8" ry="3"/><path d="M4 6v12c0 1.7 3.6 3 8 3s8-1.3 8-3V6M4 12c0 1.7 3.6 3 8 3s8-1.3 8-3"/>',
  eye: '<path d="M2 12s4-7 10-7 10 7 10 7-4 7-10 7S2 12 2 12z"/><circle cx="12" cy="12" r="3"/>',
};
const head = (open, focusTab, summary) => `<div class="tabrow"><div class="tray">${[['local', 'Local Storage'], ['shares', 'Network Shares'], ['library', 'Library']]
  .map(([k, t]) => `<span class="tab ${k === open ? 'open' : ''} ${k === focusTab ? 'focus' : ''}">${icon(I[k], 30)}${t}</span>`).join('')}</div>
  <div class="summary">${summary.map(([v, l]) => `<span>${icon(I[SUMMARY_ICON[l]] || I.info, 32)}<b>${v}</b> ${title(l)}</span>`).join('<i class="dot">·</i>')}</div></div>`;
const title = l => l.split(' ').map(w => w === 'TB' ? w : w.charAt(0).toUpperCase() + w.slice(1)).join(' ');
const SUMMARY_ICON = { devices: 'local', videos: 'film', 'videos here': 'film', free: 'disk', 'in library': 'check',
  'not added': 'plus', connections: 'shares', reachable: 'test', 'need a look': 'library', '12 min ago': 'scan' };
const bar = (used, total) => `<div class="bar"><i style="width:${Math.round(used / total * 100)}%"></i></div>`;
// Library / reachability states: one column, same place on every row.
const S = {
  in: ['#5fd38d', 'In library'], out: ['#8a93a3', 'Excluded'], none: ['', 'Not added'],
  up: ['#5fd38d', 'Reachable'], down: ['#f2b544', 'Not reachable'], newf: ['', 'Not added yet'], ok: ['#5fd38d', 'Connected'],
};
const state = k => k ? `<span class="state">${S[k][1]}</span>` : '';
const acts = list => `<span class="acts">${list.map(([ic, t]) => `<span class="ab">${icon(I[ic], 26)}${t}</span>`).join('')}</span>`;
const row = (ic, name, line, st, actions, focus, extra = '') => `<div class="row ${focus ? 'focus' : ''}">
  <span class="ri">${icon(I[ic], 40)}</span><div class="rt"><div class="rn">${name}</div><div class="rl">${line}</div>${extra}</div>
  <div class="rs">${state(st)}</div><div class="ra">${focus && actions ? acts(actions) : `<span class="rc">${icon(I.chev, 30)}</span>`}</div></div>`;
const art = [['#5b3a2e', '#1f1512'], ['#2e4a5b', '#121c22'], ['#4b5b2e', '#1a2012'], ['#5b2e4a', '#20121a'], ['#2e5b4f', '#12201d']];

const local = `${nav}<div class="page">${head('local', 'local', [['2', 'devices'], ['1,513', 'videos'], ['1.1 TB', 'free']])}
${row('tv', 'This Shield', '310 videos · 14.2 GB free of 32 GB', '', null, false, bar(17.8, 32))}
${row('usb', 'Backup #1', 'SanDisk USB drive · 1,203 videos · 1.1 TB free of 2 TB', '', null, false, bar(0.9, 2))}
</div>`;

const localPage = local.split('<div class="rt">').join('<div class="rt grow">');
const crumbrow = (trail, tools) => `<div class="crumbrow"><div class="crumbs">${trail.map((t, i) => i === trail.length - 1 ? `<span>${t}</span>` : `${t} <b>›</b> `).join('')}</div>${tools || ''}</div>`;
const tools = gridOn => `<div class="tools"><span class="b">${icon(gridOn ? I.list : I.grid, 26)}${gridOn ? 'List' : 'Grid'}</span><span class="b">${icon(I.sort, 26)}Sort: Name</span><span class="b">${icon(I.filter, 26)}Videos only</span></div>`;
const fileacts = [['play', 'Play'], ['info', 'Details'], ['check', 'Mark as watched'], ['exclude', 'Exclude']];
const files = [['folder', '4K', 'Folder · 86 videos', 'in'], ['folder', 'Old rips', 'Folder · 31 videos', 'out'],
  ['film', 'Conclave (2024).mkv', '2024 · 14.2 GB', 'in'], ['film', 'home-video-2019.mp4', '1.1 GB', 'none'],
  ['film', 'Nosferatu (2024).mkv', '2024 · 18.9 GB', 'in']];
const folder = `${nav}<div class="page">${head('local', '', [['412', 'videos'], ['398', 'in library'], ['14', 'not added']])}
${crumbrow(['Backup #1', 'Films'], tools(false))}
${files.map(([ic, n, l, st], i) => row(ic, n, l, st, fileacts, i === 2)).join('')}
</div>`;

const cards = [['4K', '86 videos', 'in', 'mosaic'], ['Old rips', '31 videos', 'out', 'plain'], ['Conclave', '2024', 'in', 'art'],
  ['home-video-2019', '1.1 GB', 'none', 'still'], ['Nosferatu', '2024', 'in', 'art'], ['Flow', '2024', 'in', 'art'],
  ['Anora', '2024', 'in', 'art'], ['The Brutalist', '2024', 'in', 'art']];
const grad = i => `linear-gradient(160deg,${art[i % 5][0]},${art[i % 5][1]})`;
const still = 'linear-gradient(180deg,#5d6066 0%,#3c3f45 45%,#2a2b2e 46%,#4a4d52 100%)';
const cardBg = (kind, i) => kind === 'art' ? `background:${grad(i)}` : kind === 'still' ? `background:${still}` : '';
const gridv = `${nav}<div class="page">${head('local', '', [['412', 'videos'], ['398', 'in library'], ['14', 'not added']])}
${crumbrow(['Backup #1', 'Films'], tools(true))}
<div class="g4">${cards.map(([t, l, st, kind], i) => `<div class="rc4 ${i === 2 ? 'focus' : ''}" style="${cardBg(kind, i)}">
${kind === 'mosaic' ? `<div class="mos">${[0, 1, 3, 4].map(k => `<i style="background:${grad(k)}"></i>`).join('')}</div>` : ''}
${kind === 'plain' || kind === 'mosaic' ? `<span class="fic">${icon(I.folder, 30)}</span>` : ''}<span class="cst">${state(st)}</span>
<div class="shade"><div class="ct">${t}</div><div class="cl">${l}</div></div></div>`).join('')}</div>
</div>`;

const shares = (focusIdx) => `${nav}<div class="page">${head('shares', '', [['3', 'connections'], ['2', 'reachable'], ['1,416', 'videos']])}
<div class="sec">Your connections</div>
${row('cloud', 'Put.io · heretostream', '98.9 of 107 GB used · synced 3 hours ago', 'up', [['open', 'Open'], ['scan', 'Sync now'], ['test', 'Test']], focusIdx === 0)}
${row('shares', 'Living-room NAS', 'SMB · 512 videos in your library', 'up', [['open', 'Open'], ['edit', 'Edit'], ['test', 'Test'], ['remove', 'Remove']], focusIdx === 1)}
${row('pc', 'Office PC', 'SFTP · last seen yesterday', 'down', [['test', 'Try again'], ['edit', 'Edit'], ['remove', 'Remove']], focusIdx === 2)}
<div class="row add ${focusIdx === 3 ? 'focus' : ''}"><span class="ri">${icon(I.plus, 40)}</span><div class="rt"><div class="rn">Add a connection</div>
<div class="rl">${focusIdx === 3 ? 'Choose what you are connecting to' : 'Computer, NAS, WebDAV, SFTP or FTP'}</div></div><div class="rs"></div>
<div class="ra wide">${focusIdx === 3 ? acts([['pc', 'Computer or NAS'], ['globe', 'WebDAV'], ['lock', 'SFTP'], ['more', 'More']]) : `<span class="rc">${icon(I.chev, 30)}</span>`}</div></div>
<div class="sec">Found on your network</div>
${row('pc', 'DESKTOP-7Q', 'SMB · Windows computer', 'newf', [['plus', 'Add']], focusIdx === 4)}
</div>`;

const types = `${nav}<div class="page">${head('shares', '', [['3', 'connections'], ['2', 'reachable'], ['1,416', 'videos']])}
${crumbrow(['Network Shares', 'Add a connection'])}
${row('cloud', 'Put.io', 'Sign in with your Put.io account', 'ok', null, false)}
${row('pc', 'A computer or NAS', 'Windows, Mac, Linux or a NAS · Supernova finds them for you', '', [['search', 'Find on my network'], ['plus', 'Enter address']], true)}
${row('globe', 'A WebDAV server', 'Cloud storage and servers that offer WebDAV', '', null, false)}
${row('lock', 'An SFTP server', 'A server you reach over SSH', '', null, false)}
${row('more', 'More types', 'FTP, FTP with TLS, and plain (unencrypted) WebDAV', '', null, false)}
</div>`;

const field = (l, v, dim, eye) => `<div class="fld"><span class="fl">${l}</span><span class="fv ${dim ? 'dim' : ''}">${v}</span>${eye ? `<span class="fe">${icon(I.eye, 30)}</span>` : ''}</div>`;
const fieldF = (l, v, dim, eye, f) => `<div class="fld ${f ? 'sel' : ''}"><span class="fl">${l}</span><span class="fv ${dim ? 'dim' : ''}">${v}</span>${eye ? `<span class="fe">${icon(I.eye, 30)}</span>` : ''}</div>`;
const keyrows = [['q', 'w', 'e', 'r', 't', 'y', 'u', 'i', 'o', 'p'], ['a', 's', 'd', 'f', 'g', 'h', 'j', 'k', 'l', '@'],
  ['⇧', 'z', 'x', 'c', 'v', 'b', 'n', 'm', '.', '-'], ['123', 'Space', '⌫', 'Next field']];
const form = `${nav}<div class="page">${head('shares', '', [['3', 'connections'], ['2', 'reachable'], ['1,416', 'videos']])}
${crumbrow(['New WebDAV connection'])}
<div class="formgrid"><div class="col">
${fieldF('Name', 'Put.io Films')}${fieldF('Address', 'webdav.put.io')}${fieldF('Port', '443 · default for HTTPS', true)}
${fieldF('Folder', '/Films')}${fieldF('Username', 'heretostream<i class="caret"></i>', false, false, true)}${fieldF('Password', '••••••••••••', false, true)}
<div class="kv"><span>Remember password</span><span class="sw on"><i></i></span></div>
<div class="btnrow"><span class="b">${icon(I.test, 28)}Test connection</span><span class="b">${icon(I.chev, 28)}Save and browse</span></div>
</div><div class="col"><div class="kb">${keyrows.map(row => `<div class="krow">${row.map(k => `<span class="key ${k.length > 1 ? 'wide' : ''} ${k === 'n' ? 'focus' : ''}">${k}</span>`).join('')}</div>`).join('')}</div>
<div class="rl p">Port and Folder are optional. For Put.io use <b>webdav.put.io</b> and your Put.io username.
Test connection checks it without saving anything.</div></div>
</div></div>`;
const menuHtml = '<div class="cmenu"><div class="ch">More connection types</div>' +
  [['pc', 'FTP', true], ['lock', 'FTP with TLS', false], ['globe', 'WebDAV without encryption', false]]
    .map(([ic, n, f]) => `<div class="ci ${f ? 'focus' : ''}">${icon(I[ic], 30)}<span>${n}</span></div>`).join('') + '</div>';
const more = shares(3).replace('class="row add focus"', 'class="row add open"')
  .replace('</div></div>\n<div class="sec">Found on your network</div>', `</div>${menuHtml}</div>\n<div class="sec">Found on your network</div>`);

const library = `${nav}<div class="page">${head('library', '', [['1,808', 'videos'], ['5', 'need a look'], ['scanned', '12 min ago']])}
<div class="two"><div class="col"><div class="sec">Scanning</div>
<div class="scanbox"><span class="b focus big">${icon(I.scan, 32)}Scan now</span>
<div class="sl"><div class="rn">Last scan 12 minutes ago</div><div class="rl">Put.io › Films · 2 min 14 s · 3 new, 1 updated</div></div></div>
<div class="kv"><span>Automatic scan</span><b>Every hour</b></div><div class="kv"><span>When Supernova opens</span><b>On</b></div>
<div class="sec small">Recent scans</div>
<div class="mini"><span>Today 23:28</span><span>Put.io › Films</span><span>3 new · 1 updated</span></div>
<div class="mini"><span>Today 22:28</span><span>Living-room NAS</span><span>No changes</span></div>
<div class="mini"><span>Today 21:28</span><span>Backup #1</span><span class="warnc">Stopped: drive removed</span></div></div>
<div class="col"><div class="sec">Library Health</div>
<div class="hrow"><span class="ri">${icon(I.folder, 40)}</span><div class="rt"><div class="rn">Missing files</div><div class="rl">In your library but no longer found</div></div><span class="count amber">3</span></div>
<div class="hrow"><span class="ri">${icon(I.film, 40)}</span><div class="rt"><div class="rn">Couldn't be read</div><div class="rl">Files Supernova could not open</div></div><span class="count amber">2</span></div>
<div class="hrow"><span class="ri">${icon(I.library, 40)}</span><div class="rt"><div class="rn">Not matched</div><div class="rl">Videos without a film or show</div></div><span class="count">14</span></div>
</div></div></div>`;

const css = `${font(400)}${font(500)}*{box-sizing:border-box;margin:0}
body{width:1920px;height:1080px;overflow:hidden;font-family:R;color:#fff;background:#16181C;position:relative}
.nav{position:absolute;left:56px;right:56px;top:28px;height:56px;display:flex;align-items:center;gap:30px;font-size:34px;color:#e8edf8}
.nav .brand{font-size:36px;margin-right:40px;letter-spacing:1px}.nav .sp{flex:1}.nav .on{font-weight:500;color:#fff}.nav .clock{margin-left:20px}
.page{position:absolute;left:96px;right:96px;top:150px;bottom:40px}
.tabrow{display:flex;align-items:center;justify-content:space-between;padding-bottom:34px;border-bottom:2px solid rgba(255,255,255,.10);margin-bottom:26px}
.tray{display:inline-flex;gap:8px;padding:7px;border-radius:31px;background:#22252B;box-shadow:inset 0 0 0 2px rgba(255,255,255,.10)}
.tab{height:76px;padding:0 34px;border-radius:24px;display:inline-flex;align-items:center;gap:16px;font-size:32px;font-weight:500;color:#dfe5f1}
.tab.open{background:#454B55;color:#fff}.tab.focus{background:#F4F6FB;color:#0B0D12}
.summary{display:flex;align-items:center;gap:18px;font-size:32px;color:#C9D3E6}.summary span{display:inline-flex;align-items:center;gap:12px}.summary svg{color:#C9D3E6}.summary b{color:#fff;font-weight:500}.summary .dot{font-style:normal;color:#6b7383}
.sec{font-size:28px;font-weight:500;color:#B7BFCC;margin:36px 0 12px;letter-spacing:.3px}.sec.small{margin-top:30px;font-size:24px}
.row{display:flex;align-items:center;gap:30px;min-height:128px;padding:18px 34px;box-shadow:inset 0 -2px 0 rgba(255,255,255,.08)}
.row.focus{background:#F4F6FB;color:#0B0D12;border-radius:24px;box-shadow:none}.row.focus .rl{color:#3A4256}
.ri{color:#c9d3e6;width:44px}.row.focus .ri{color:#0B0D12}.rt{width:500px;min-width:0;flex-shrink:0}.rn{font-size:36px;font-weight:500;white-space:nowrap;overflow:hidden;text-overflow:ellipsis}.rl{font-size:27px;color:#C9D3E6;margin-top:6px;white-space:nowrap;overflow:hidden;text-overflow:ellipsis}
.rt.grow{width:auto;flex:1}.rs{width:220px;flex-shrink:0;display:flex;justify-content:center}.ra{flex:1;display:flex;justify-content:flex-end}.rc{color:rgba(255,255,255,.35)}.row.focus .rc{color:#0B0D12}
.state{white-space:nowrap;display:inline-flex;align-items:center;justify-content:center;width:210px;box-sizing:border-box;height:48px;padding:0 24px;border-radius:24px;font-size:26px;font-weight:500;color:#e8edf6;box-shadow:inset 0 0 0 2px rgba(255,255,255,.30)}
.row.focus .state{color:#0B0D12;box-shadow:inset 0 0 0 2px rgba(11,13,18,.35)}
.bar{margin-top:16px;width:620px;height:8px;border-radius:4px;background:rgba(255,255,255,.14)}.bar i{display:block;height:100%;border-radius:4px;background:#C9D3E6}
.acts{display:flex;gap:14px}.ab{white-space:nowrap;height:64px;padding:0 22px;border-radius:20px;background:#dde1ea;color:#0B0D12;display:inline-flex;align-items:center;gap:12px;font-size:26px;font-weight:500}
.ab:first-child{background:#0B0D12;color:#fff}
.row.add .ri{color:#fff}.row.add.focus .ri{color:#0B0D12}.row.add .rn{font-weight:500}
.crumbrow{display:flex;align-items:center;justify-content:space-between;margin:30px 0 14px;min-height:64px}
.crumbs{font-size:34px;color:#C9D3E6}.crumbs b{color:#7d8696;margin:0 10px;font-weight:400}.crumbs span{color:#fff;font-weight:500}
.tools{display:flex;gap:12px}.b{height:64px;padding:0 26px;border-radius:20px;background:#2a2d33;display:inline-flex;align-items:center;gap:12px;font-size:27px;font-weight:500}
.b.focus{background:#F4F6FB;color:#0B0D12}.b.big{height:76px;padding:0 34px;font-size:30px}
.g4{display:grid;grid-template-columns:repeat(4,400px);gap:40px 42px;margin-top:26px}
.rc4{position:relative;width:400px;height:225px;border-radius:16px;background:#22252B;box-shadow:0 0 0 2px rgba(255,255,255,.28);overflow:hidden}
.rc4.focus{transform:scale(1.1);box-shadow:0 0 0 4px #fff;z-index:2}
.fic{position:absolute;left:14px;top:12px;width:48px;height:42px;border-radius:12px;background:rgba(10,12,16,.72);display:flex;align-items:center;justify-content:center;color:#fff}
.mos{position:absolute;inset:0;display:grid;grid-template-columns:1fr 1fr;grid-template-rows:1fr 1fr;gap:3px}.mos i{display:block}
.cst{position:absolute;right:12px;top:12px}.cst .state{width:150px;height:40px;font-size:21px;padding:0 12px;background:rgba(10,12,16,.72);box-shadow:inset 0 0 0 2px rgba(255,255,255,.28)}
.shade{position:absolute;left:0;right:0;bottom:0;padding:44px 20px 16px;background:linear-gradient(transparent,rgba(0,0,0,.82))}
.ct{font-size:28px;font-weight:500;white-space:nowrap;overflow:hidden;text-overflow:ellipsis}.cl{font-size:20px;color:#d6dce8;margin-top:2px}
.two{display:grid;grid-template-columns:1fr 1fr;gap:80px}
.scanbox{display:flex;align-items:center;gap:30px;margin-bottom:22px}
.kv{display:flex;justify-content:space-between;align-items:center;font-size:28px;padding:20px 4px;border-bottom:2px solid rgba(255,255,255,.08)}.kv span{color:#C9D3E6}.kv b{font-weight:500}
.mini{display:grid;grid-template-columns:200px 1fr auto;font-size:24px;padding:12px 4px;color:#dfe5f1}.mini span:first-child{color:#9aa3b2}.warnc{color:#f2b544}
.hrow{display:flex;align-items:center;gap:28px;min-height:108px;padding:16px 30px;box-shadow:inset 0 -2px 0 rgba(255,255,255,.08)}
.count{min-width:56px;height:56px;padding:0 16px;border-radius:28px;background:rgba(255,255,255,.12);display:inline-flex;align-items:center;justify-content:center;font-size:28px;font-weight:500}
.count.amber{background:#F2B544;color:#111}
.form{margin-top:4px}.fld{display:flex;align-items:center;height:60px;padding:0 26px;border-radius:18px;background:rgba(255,255,255,.06);margin-bottom:12px;font-size:28px}
.fl{width:180px;color:#C9D3E6;font-size:24px}.fv{flex:1}.fv.dim{color:#8a93a3}.fe{color:#c9d3e6}
.btnrow{display:flex;gap:16px;margin-top:18px}.p{line-height:1.45;margin-top:22px;white-space:normal}.p b{color:#fff;font-weight:500}.gap{margin-top:40px}
.formgrid{display:grid;grid-template-columns:760px minmax(0,1fr);gap:60px}.formgrid .col{min-width:0}.caret{display:inline-block;width:3px;height:30px;background:#fff;margin-left:3px;vertical-align:middle}
.kb{display:flex;flex-direction:column;gap:12px}.kb .krow{display:flex;gap:10px}.kb .key{min-width:0;height:66px;font-size:28px}.fld.sel{box-shadow:inset 0 0 0 3px rgba(255,255,255,.7);background:rgba(255,255,255,.12)}
.spanel{position:absolute;top:0;right:0;bottom:0;width:820px;background:rgba(22,24,28,.985);padding:60px 48px;box-sizing:border-box}
.pt{font-size:40px;font-weight:500}.pd{font-size:26px;color:#C9D3E6;margin-top:8px}
.pin{margin-top:34px;height:76px;border-radius:18px;background:#2a2d33;display:flex;align-items:center;padding:0 26px;font-size:32px}
.pin i{display:inline-block;width:3px;height:40px;background:#fff;margin-left:4px}
.keys{margin-top:30px;display:flex;flex-direction:column;gap:12px}.krow{display:flex;gap:12px}
.key{flex:1;height:76px;border-radius:16px;background:#2a2d33;display:flex;align-items:center;justify-content:center;font-size:30px;font-weight:500}
.key.wide{flex:2.2;font-size:26px}.key.focus{background:#F4F6FB;color:#0B0D12}
.pnote{margin-top:28px;font-size:24px;color:#9aa3b2}
.prow{display:flex;align-items:center;gap:26px;padding:22px 26px;border-radius:22px;margin-top:14px}.prow.focus{background:#F4F6FB;color:#0B0D12}
.prow.focus .rl,.prow.focus .ri{color:#3A4256}.prow:first-of-type{margin-top:34px}
.row.open{position:relative;background:rgba(255,255,255,.06);border-radius:24px;box-shadow:none}
.row.open .ab{background:#2a2d33;color:#fff}.row.open .ab:last-child{background:#454B55}
.cmenu{position:absolute;right:30px;bottom:calc(100% + 10px);width:520px;background:rgba(22,24,28,.97);border-radius:26px;padding:20px 18px 16px;z-index:5;box-shadow:0 20px 60px rgba(0,0,0,.5)}
.ch{font-size:24px;color:#B7BFCC;padding:0 14px 10px}
.ci{display:flex;align-items:center;gap:18px;height:68px;padding:0 18px;border-radius:18px;font-size:29px;font-weight:500}
.ci.focus{background:#F4F6FB;color:#0B0D12}
.sw{width:76px;height:40px;border-radius:20px;background:#3a3f48;position:relative}.sw i{position:absolute;top:4px;left:4px;width:32px;height:32px;border-radius:50%;background:#fff}.sw.on{background:#cfd6e2}.sw.on i{left:40px;background:#16181C}`;
(async () => {
  const browser = await chromium.launch();
  const tab = await browser.newPage({ viewport: { width: 1920, height: 1080 } });
  const pages = [['01-local-storage.png', localPage], ['02-inside-a-folder.png', folder], ['03-folder-grid.png', gridv],
    ['04-network-shares.png', shares(1)], ['05-network-shares-unreachable.png', shares(2)], ['06-network-shares-add.png', shares(3)],
    ['07-add-connection.png', types], ['08-add-connection-form.png', form], ['09-library.png', library],
    ['11-add-connection-more.png', more]];
  for (const [f, body] of pages) {
    await tab.setContent(`<html><head><style>${css}</style></head><body>${body}</body></html>`, { waitUntil: 'load' });
    await tab.evaluate(() => document.fonts.ready);
    await tab.screenshot({ path: path.join(__dirname, f) });
  }
  await browser.close();
})();
