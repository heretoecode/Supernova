// Files v18 (owner, 13 Nov 2026): the tab is called Your Library; no heading repeating it and no facts on the right;
// big figures with icons and words beside them; more stats (watched, hours, formats, biggest per episode).
// Files v17 (owner, 13 Nov 2026): a fifth tab, Statistics: the whole library, then one row per library page (named
// as the page is), then the biggest titles per episode. The library pages no longer show stats.
// Files v16 (owner, 12 Nov 2026): one signal slot left of Files (ring while scanning, else the attention dot); the
// tab row's right shows the tab's facts plus the scan control when idle; Scan one source = buttons on the focused row;
// numbers add up (badge = everything waiting); the shared folder picker; delete with two history switches.
// Files v15 (owner, 12 Nov 2026): Files back on the right of the top bar with the scan ring and the attention dot
// together; one scan line at the right of the tab row on every Files tab (OK = Scan now / Stop); delete warning with
// "Also remove from watched history" (Supernova and Trakt), left-aligned.
// Files v14 (owner, 12 Nov 2026): the approved Files page with every rule in docs/design/RULES.md applied, plus the
// new pieces: attention mark on Files, Check matches (re-check), the Transfers tab, the one-step delete warning.
// Run: FONT_DIR=<roboto woff2 dir> node render-v14.js
const fs = require('fs');
const path = require('path');
const { chromium } = require('playwright');
const fontDir = process.env.FONT_DIR;
const font = w => `@font-face{font-family:R;font-weight:${w};src:url(data:font/woff2;base64,${fs.readFileSync(
  path.join(fontDir, `roboto-latin-${w}-normal.woff2`)).toString('base64')}) format('woff2')}`;
const svg = (d, s = 30) => `<svg width="${s}" height="${s}" viewBox="0 0 24 24" fill="none" stroke="currentColor"
  stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round">${d}</svg>`;
const I = {
  search: '<circle cx="11" cy="11" r="7"/><path d="M20 20l-4-4"/>',
  cog: '<circle cx="12" cy="12" r="3"/><path d="M12 2v3M12 19v3M4.2 4.2l2.1 2.1M17.7 17.7l2.1 2.1M2 12h3M19 12h3'
    + 'M4.2 19.8l2.1-2.1M17.7 6.3l2.1-2.1"/>',
  shield: '<rect x="3" y="5" width="18" height="12" rx="2"/><path d="M8 21h8M12 17v4"/>',
  server: '<rect x="4" y="4" width="16" height="7" rx="1.5"/><rect x="4" y="13" width="16" height="7" rx="1.5"/>'
    + '<path d="M8 7.5h.1M8 16.5h.1"/>',
  care: '<path d="M3 12h4l2-5 4 10 2-5h6"/>',
  transfer: '<path d="M12 3v12M7 10l5 5 5-5"/><path d="M4 17v2a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2v-2"/>',
  sync: '<path d="M4 12a8 8 0 0 1 14-5.3M20 12a8 8 0 0 1-14 5.3"/><path d="M18 3v4h-4M6 21v-4h4"/>',
  folder: '<path d="M3 7a2 2 0 0 1 2-2h4l2 2h8a2 2 0 0 1 2 2v8a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2z"/>',
  film: '<rect x="3" y="4" width="18" height="16" rx="2"/><path d="M7 4v16M17 4v16M3 9h4M3 15h4M17 9h4M17 15h4"/>',
  match: '<circle cx="11" cy="11" r="7"/><path d="M20 20l-4-4M8 11l2 2 4-4"/>',
  recheck: '<path d="M4 12a8 8 0 0 1 14-5.3"/><path d="M18 3v4h-4"/><path d="M9 13l2 2 4-4"/>'
    + '<path d="M20 12a8 8 0 0 1-14 5.3"/>',
  clock: '<circle cx="12" cy="12" r="9"/><path d="M12 7v5l3 2"/>',
  heart: '<path d="M12 20s-7-4.5-7-10a4 4 0 0 1 7-2.6A4 4 0 0 1 19 10c0 5.5-7 10-7 10z"/>',
  power: '<path d="M12 3v8"/><path d="M6.3 6.3a8 8 0 1 0 11.4 0"/>',
  check: '<path d="M5 12.5l4.5 4.5L19 7.5"/>',
  checkall: '<path d="M2 12.5l4.5 4.5L16 7.5M10 16l1 1L20.5 7.5"/>',
  hand: '<path d="M8 13V5.5a1.5 1.5 0 0 1 3 0V12M11 11V4.5a1.5 1.5 0 0 1 3 0V12M14 11V6a1.5 1.5 0 0 1 3 0v8'
    + 'a6 6 0 0 1-6 6h-1a6 6 0 0 1-5-2.7L3 13.6a1.5 1.5 0 0 1 2.4-1.8L8 15"/>',
  down: '<path d="M12 4v13M6 11l6 6 6-6"/>',
  wait: '<path d="M6 3h12M6 21h12M7 3c0 5 10 5 10 9s-10 4-10 9M17 3c0 5-10 5-10 9"/>',
  done: '<circle cx="12" cy="12" r="9"/><path d="M8 12.5l2.8 2.8L16.5 9.5"/>',
  pause: '<path d="M9 5v14M15 5v14"/>',
  x: '<path d="M6 6l12 12M18 6L6 18"/>',
  bin: '<path d="M4 7h16M10 11v6M14 11v6M6 7l1 13h10l1-13M9 7V4h6v3"/>',
  keep: '<path d="M9 14L4 9l5-5"/><path d="M4 9h11a5 5 0 0 1 0 10h-2"/>',
  usb: '<path d="M12 3v13M8 7l4-4 4 4M7 12v2l5 3 5-3v-2"/><circle cx="12" cy="19" r="2"/>',
  chev: '<path d="M9 6l6 6-6 6"/>',
  arrow: '<path d="M5 12h14M13 6l6 6-6 6"/>',
  plus: '<path d="M3 7a2 2 0 0 1 2-2h4l2 2h8a2 2 0 0 1 2 2v8a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2z"/><path d="M12 10v6M9 13h6"/>',
  use: '<path d="M3 7a2 2 0 0 1 2-2h4l2 2h8a2 2 0 0 1 2 2v8a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2z"/><path d="M9 13l2 2 4-4"/>',
  open: '<path d="M3 7a2 2 0 0 1 2-2h4l2 2h8a2 2 0 0 1 2 2v8a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2z"/><path d="M10 10l3 3-3 3"/>',
  cloud: '<path d="M7 18h10a4 4 0 0 0 .6-8A6 6 0 0 0 6 9.5 4.3 4.3 0 0 0 7 18z"/>',
  stats: '<path d="M4 20V10M10 20V4M16 20v-7M22 20H2"/>',
  tv: '<rect x="3" y="5" width="18" height="12" rx="2"/><path d="M8 21h8M12 17v4"/>',
  doc: '<circle cx="12" cy="12" r="9"/><path d="M3 12h18M12 3a14 14 0 0 1 0 18M12 3a14 14 0 0 0 0 18"/>',
  hd: '<rect x="3" y="5" width="18" height="14" rx="2"/><path d="M7 9v6M7 12h3M10 9v6M14 9h1.5a3 3 0 0 1 0 6H14z"/>',
  hdr: '<circle cx="12" cy="12" r="4"/><path d="M12 2v3M12 19v3M2 12h3M19 12h3M5 5l2 2M17 17l2 2M5 19l2-2M17 7l2-2"/>',
  disk: '<rect x="3" y="6" width="18" height="12" rx="2"/><path d="M7 14h.1M11 14h6"/>',
  fixmatch: '<path d="M4 20l7-7"/><path d="M14 4l6 6-5 5-6-6z"/>',
};
const nav = scanning => `<div class="nav"><span class="brand">SUPERNOVA</span>${['Home', 'Movies', 'TV Shows',
  'Documentaries'].map(t => `<span class="ni">${t}</span>`).join('')}<span class="ni dull att tools">${scanning
  ? '<span class="ring"></span>' : '<i class="slotdot"></i>'}Files</span><span class="ni icon">${svg(I.search, 34)}</span><span class="ni icon">${svg(I.cog, 34)}</span>
  <span class="clock">21:40</span></div>`;
const TABS = [['shield', 'This Shield'], ['server', 'Connections'], ['care', 'Library Care', 9],
  ['transfer', 'Transfers'], ['stats', 'Your Library']];
const tabs = (open, facts, scan) => `<div class="tabs">${TABS.map(([ic, t, n]) => `<span class="tab
  ${t === open ? 'line' : ''}">${svg(I[ic])}${t}${n ? `<b class="badge">${n}</b>` : ''}</span>`).join('')}
  <span class="sum">${facts.map(([ic, v, l]) => `<span>${svg(I[ic], 28)}<b>${v}</b> ${l}</span>`).join('<i>·</i>')}
  ${facts.length ? '<i>·</i>' : ''}<span class="scanc">${svg(I.sync, 28)}${scan}</span></span></div>`;
const hd = (ic, t, extra, first) => `<div class="hd ${first ? 'first' : ''}">${svg(I[ic], 30)}${t}${extra || ''}</div>`;
const art = ['#5b3a2e', '#2e4a5b', '#4b5b2e', '#5b2e4a', '#2e5b4f', '#3a2e5b'];
const thumb = i => `<span class="thumb" style="background:linear-gradient(160deg,${art[i % 6]},#121418)"></span>`;
const btns = list => `<span class="btns">${list.map(([ic, t], k) => `<span class="b ${k === 0 ? 'on' : ''}">
  ${svg(I[ic], 26)}${t}</span>`).join('')}</span>`;
// a row: lead (icon key or thumb index), name, line, pill, focus buttons, extra classes
const row = o => `<div class="row ${o.focus ? 'focus' : ''} ${o.cls || ''}"><span class="ri">${typeof o.lead === 'number'
  ? thumb(o.lead) : svg(I[o.lead], 38)}</span><div class="rt"><div class="rn">${o.name}</div><div class="rl">${o.line}
  </div>${o.bar !== undefined ? `<div class="bar"><i style="width:${o.bar}%"></i></div>` : ''}</div>
  ${o.pill ? `<span class="pill">${o.pill}</span>` : '<span class="nopill"></span>'}
  ${o.focus && o.buttons ? btns(o.buttons) : `<span class="end">${o.end || (o.chev ? svg(I.chev, 30) : '')}</span>`}
  </div>`;
const chips = (list, on) => `<span class="chips">${list.map(t => `<i class="${t === on ? 'on' : ''}">${t}</i>`)
  .join('')}</span>`;
const sw = on => `<span class="sw ${on ? 'on' : ''}"><i></i></span>`;
const page = (body, bw, iw, scanning) => `${nav(scanning)}<div class="page" style="--bw:${bw}px;--iw:${iw || 128}px">${body}</div>`;

// 1. Library Care, every rule applied; attention mark on Files; count badge; the new Check matches row (focused).
const care = page(`${tabs('Library Care', [['film', '1,808', 'Videos'], ['care', '9', 'To Check']], '12 min ago')}
  ${hd('heart', 'Library health', '', true)}
  ${row({ lead: 'folder', name: 'Missing files', line: 'In your library but no longer found', pill: '3 files',
    chev: true })}
  ${row({ lead: 'film', name: 'Couldn\'t be read', line: 'Files Supernova could not open', pill: '2 files',
    chev: true })}
  ${row({ lead: 'match', name: 'Not matched', line: 'Videos without a movie or TV show', pill: '4 videos',
    chev: true })}
  ${row({ lead: 'recheck', name: 'Check matches', line: 'Look every title up again; you see the changes first',
    pill: 'Last: 2 Nov', chev: true })}
  ${hd('sync', 'Scanning')}
  ${row({ lead: 'server', name: 'Scan one source', line: 'Look for new videos in one place only', focus: true,
    buttons: [['shield', 'This Shield'], ['cloud', 'Put.io'], ['server', 'Backup #1']] })}
  `, 640, 56);

// 2. Check matches: results, nothing changed until accepted; hand-made fixes left alone.
const pair = (a, b) => `${a}<span class="arr">${svg(I.arrow, 26)}</span><b>${b}</b>`;
const results = page(`${tabs('Library Care', [['film', '1,808', 'Videos'], ['recheck', '4', 'Changes']])}
  <div class="crumb">Check matches</div>
  ${hd('recheck', '4 suggested changes', `<span class="act">${svg(I.checkall, 24)}Accept all</span>`, true)}
  ${row({ lead: 1, name: pair('Dune (1984)', 'Dune (2021)'), line: 'File: Dune.2021.2160p.mkv · year and length match',
    pill: 'Film', focus: true, buttons: [['check', 'Accept'], ['keep', 'Keep current'], ['fixmatch', 'Fix match']] })}
  ${row({ lead: 2, name: pair('The Office (US)', 'The Office (UK)'), line: 'Folder: The Office (2001) · 12 episodes',
    pill: 'Series' })}
  ${row({ lead: 3, name: pair('Untitled', 'Conclave (2024)'), line: 'File: conclave_final.mp4 · was not matched',
    pill: 'Film' })}
  ${row({ lead: 4, name: pair('Planet Earth', 'Planet Earth II'), line: 'Folder: Planet Earth II · 6 episodes',
    pill: 'Series' })}
  ${hd('hand', 'Left alone')}
  ${row({ lead: 'fixmatch', name: '12 titles you fixed by hand', line: 'Checks never change a match you chose',
    chev: true })}`, 640);

// 3. Transfers tab: copying, waiting, finished; folders, space and the move switch (off by default).
const transfers = page(`${tabs('Transfers', [['transfer', '1', 'Copying'], ['wait', '1', 'Waiting'],
  ['shield', '412 GB', 'Free']])}
  ${hd('down', 'Copying now', '', true)}
  ${row({ lead: 1, name: 'Marshals · S1 E5', line: 'From Put.io · 1.4 of 2.1 GB · 3 min left', bar: 67,
    pill: '67%', focus: true, buttons: [['pause', 'Pause'], ['x', 'Cancel']] })}
  ${hd('wait', 'Waiting')}
  ${row({ lead: 3, name: 'Conclave', line: 'From Backup #1 · 4.2 GB · waits while you watch from the network',
    pill: 'Waiting' })}
  ${hd('done', 'Finished')}
  ${row({ lead: 4, name: 'Slow Horses · S4 E1', line: 'Copied and checked · 14:02 · plays from this Shield now',
    pill: 'Done' })}
  ${hd('folder', 'Where copies go')}
  ${row({ lead: 'folder', name: 'Films and series', line: 'This Shield › Films · This Shield › TV Shows',
    chev: true })}
  ${row({ lead: 'bin', name: 'Remove the network copy', line: 'Only after the copy is checked and matched',
    end: sw(false) })}`, 380);

// 5. The shared folder picker (Transfers › Where copies go › Movies; also the fonts and diagnostics folders).
const picker = page(`${tabs('Transfers', [['shield', '412 GB', 'Free']], '12 min ago')}
  <div class="crumb">Where copies go › Movies</div>
  ${hd('use', 'Choose a folder for Movies', '', true)}
  <div class="path">Internal storage › Media</div>
  ${row({ lead: 'folder', name: 'Movies', line: '212 videos · 1.9 TB', pill: 'Current' })}
  ${row({ lead: 'folder', name: 'Movies 4K', line: '38 videos · 1.1 TB', focus: true,
    buttons: [['use', 'Use this folder'], ['open', 'Open']] })}
  ${row({ lead: 'folder', name: 'TV Shows', line: '64 shows · 3.2 TB' })}
  ${row({ lead: 'folder', name: 'Downloads', line: '9 videos · 41 GB' })}
  ${row({ lead: 'plus', name: 'New folder', line: 'Make a folder here and use it' })}`, 460, 56);

// 4. One-step delete with two history switches; focus on the safe choice.
const shieldList = page(`${tabs('This Shield', [['folder', '2', 'Drives'], ['shield', '412 GB', 'Free']],
  '12 min ago')}
  <div class="crumb">Internal storage › Movies</div>
  ${row({ lead: 1, name: 'Conclave (2024).mkv', line: '4.2 GB · added 3 Nov', pill: 'Watched' })}
  ${row({ lead: 2, name: 'Dune (2021).mkv', line: '18.7 GB · added 28 Oct', pill: 'In library' })}
  ${row({ lead: 3, name: 'The Holdovers (2023).mkv', line: '6.1 GB · added 21 Oct', pill: 'In library' })}`, 200)
  + `<div class="dim"></div><div class="dlg"><div class="dh">${svg(I.bin, 34)}Delete this file?</div>
  <div class="dn">Conclave (2024).mkv</div>
  <div class="dt">4.2 GB will be removed from this Shield. This can't be undone.</div>
  <div class="dsw"><div><div class="dsn">Remove from Supernova history</div><div class="dsl">Watched and progress
  on this Shield</div></div>${sw(false)}</div>
  <div class="dsw two"><div><div class="dsn">Remove from Trakt history</div><div class="dsl">Your Trakt account</div>
  </div>${sw(false)}</div>
  <div class="dbtn"><span class="b on">${svg(I.keep, 26)}Keep file</span><span class="b">
  ${svg(I.bin, 26)}Delete</span></div></div>`;

const big = (ic, v, l) => `<div class="big">${svg(I[ic], 44)}<b>${v}</b><span>${l}</span></div>`;
const libRow = (ic, name, line, shield, net) => `<div class="row lib"><span class="ri">${svg(I[ic], 38)}
  </span><div class="rt"><div class="rn">${name}</div><div class="rl">${line}</div></div>
  <div class="split"><span>${svg(I.shield, 24)}${shield}</span><span>${svg(I.server, 24)}${net}</span></div></div>`;
const perEp = (name, line, v) => `<div class="row lib"><span class="ri">${svg(I.tv, 38)}</span><div class="rt">
  <div class="rn">${name}</div><div class="rl">${line}</div></div><div class="split"><span>${v} per episode</span></div>
  </div>`;
const statsTab = page(`${tabs('Your Library', [], '12 min ago')}
  <div class="bigs">${big('film', '520', 'Titles')}${big('tv', '1,808', 'Videos')}${big('disk', '3.4 TB', 'In total')}
  ${big('shield', '2.1 TB', 'On this Shield')}${big('server', '1.3 TB', 'On the network')}</div>
  <div class="bigs b2">${big('done', '61%', 'Watched')}${big('clock', '2,140 h', 'To watch everything')}
  ${big('hd', '78%', 'In 4K')}${big('hdr', '44%', 'In HDR')}${big('plus', '23', 'Added this month')}</div>
  ${hd('film', 'By Library Page')}
  ${libRow('film', 'Movies', '214 titles · 1.4 TB', '0.9 TB', '0.5 TB')}
  ${libRow('tv', 'TV Shows', '210 titles · 1,412 episodes · 1.6 TB', '1.0 TB', '0.6 TB')}
  ${libRow('doc', 'Documentaries', '96 titles · 0.4 TB', '0.2 TB', '0.2 TB')}
  ${hd('down', 'Most Space Per Episode')}
  ${perEp('Frozen Frontier', '6 episodes · 14.4 GB', '2.4 GB')}${perEp('Deep Blue Planet', '6 episodes · 12.6 GB', '2.1 GB')}
  ${perEp('Planet Earth III', '9 episodes · 9.2 GB', '1.0 GB')}`, 0, 56);

const css = `${font(400)}${font(500)}${font(700)}*{box-sizing:border-box;margin:0}
body{width:1920px;height:1440px;overflow:hidden;font-family:R;color:#fff;background:#16181C;position:relative}
body:after{content:"";position:fixed;inset:0;border:6px solid #8a919c;z-index:99;pointer-events:none}
.nav{position:absolute;left:56px;right:56px;top:24px;height:76px;display:flex;align-items:center;gap:14px;
font-size:33px;color:#8f98a8;z-index:5}.nav .brand{color:#f1ead8;letter-spacing:2px;margin-right:36px;font-weight:500}
.ni{position:relative;display:inline-flex;align-items:center;height:60px;padding:0 18px;border-radius:16px}
.ni.tools{margin-left:auto}.nav .clock{margin-left:18px;color:#C9D3E6}
.dull{color:#C9D3E6}.dull:after,.line:after{content:'';position:absolute;left:18px;right:18px;bottom:-2px;height:4px;
border-radius:2px}.dull:after{background:rgba(255,255,255,.3)}.line{color:#fff}.line:after{background:#fff}
.mark{position:absolute;top:8px;right:4px;width:13px;height:13px;border-radius:50%;background:#fff;
box-shadow:0 0 0 3px #16181C}
.page{position:absolute;left:96px;right:96px;top:140px}
.tabs{display:flex;align-items:center;gap:10px;border-bottom:2px solid rgba(255,255,255,.14);padding-bottom:12px}
.tab{position:relative;display:inline-flex;align-items:center;gap:12px;height:60px;padding:0 20px;border-radius:16px;
font-size:30px;font-weight:500;color:#8f98a8}.tab.line:after{left:20px;right:20px;bottom:-14px}
.badge{display:inline-flex;align-items:center;justify-content:center;min-width:34px;height:34px;border-radius:17px;
background:#fff;color:#0B0D12;font-size:21px;font-weight:700;padding:0 8px}
.sum{margin-left:auto;display:flex;align-items:center;gap:16px;font-size:30px;color:#C9D3E6}
.sum span{display:inline-flex;align-items:center;gap:10px}.sum b{color:#fff;font-weight:500}.sum i{font-style:normal}
.hd{display:flex;align-items:center;gap:16px;font-size:32px;font-weight:500;margin:44px 0 16px}
.hd.first{margin-top:30px}.hd svg{color:#C9D3E6}
.act{margin-left:auto;display:inline-flex;align-items:center;gap:10px;font-size:25px;color:#C9D3E6;padding-right:24px}
.crumb{font-size:26px;color:#C9D3E6;margin-top:26px}
.row{display:flex;align-items:center;gap:28px;height:96px;padding:0 30px;box-shadow:inset 0 -2px 0 rgba(255,255,255,.08)}
.row:has(+ .hd),.row:last-child,.row:has(+ .dim){box-shadow:none}
.row.focus{background:#F4F6FB;color:#0B0D12;border-radius:22px;box-shadow:none}
.ri{color:#C9D3E6;display:flex;width:var(--iw);justify-content:center;flex:none}.row.focus .ri{color:#0B0D12}
.rt{flex:1;min-width:0}.rn{font-size:33px;font-weight:500;white-space:nowrap;display:flex;align-items:center;gap:6px}
.rn b{font-weight:500}.arr{display:inline-flex;color:#C9D3E6;margin:0 8px}.row.focus .arr{color:#3A4256}
.rl{font-size:25px;color:#C9D3E6;margin-top:4px;white-space:nowrap;overflow:hidden;text-overflow:ellipsis}
.row.focus .rl{color:#3A4256}
.bar{height:6px;border-radius:3px;background:rgba(255,255,255,.18);margin-top:10px;width:560px}
.bar i{display:block;height:6px;border-radius:3px;background:#fff}.row.focus .bar{background:rgba(11,13,18,.15)}
.row.focus .bar i{background:#0B0D12}
.pill{flex:none;width:190px;text-align:center;padding:8px 0;border-radius:22px;font-size:25px;font-weight:500;
box-shadow:inset 0 0 0 2px rgba(255,255,255,.3)}.row.focus .pill{box-shadow:inset 0 0 0 2px rgba(11,13,18,.35)}
.nopill{width:190px;flex:none}.row.wide .nopill{display:none}
.end{flex:none;width:var(--bw);display:flex;justify-content:flex-end;color:#C9D3E6}
.btns{display:flex;gap:12px;flex:none;width:var(--bw);justify-content:flex-end}
.b{display:inline-flex;align-items:center;gap:10px;height:62px;padding:0 26px;border-radius:16px;font-size:27px;
font-weight:500;white-space:nowrap;box-shadow:inset 0 0 0 2px rgba(11,13,18,.3)}.b.on{background:#0B0D12;color:#fff;box-shadow:none}
.thumb{width:128px;height:72px;border-radius:10px;
box-shadow:inset 0 0 0 2px rgba(255,255,255,.6),inset 0 0 12px 3px rgba(0,0,0,.45)}
.chips{display:flex;gap:6px;font-size:27px}.chips i{font-style:normal;padding:10px 20px;border-radius:14px;color:#fff}
.chips i.on{background:#454B55}
.sw{width:76px;height:42px;border-radius:21px;background:#454B55;position:relative}.sw i{position:absolute;top:5px;
left:5px;width:32px;height:32px;border-radius:50%;background:#C9D3E6}.sw.on{background:#F4F6FB}
.sw.on i{left:39px;background:#0B0D12}
.dim{position:absolute;inset:0;background:rgba(10,11,13,.6)}
.dlg{position:absolute;left:50%;top:50%;transform:translate(-50%,-50%);width:900px;padding:44px 48px 40px;
border-radius:28px;background:rgba(22,24,28,.9);box-shadow:0 0 0 2px rgba(255,255,255,.28),0 30px 80px rgba(0,0,0,.6)}
.dh{display:flex;align-items:center;gap:16px;font-size:38px;font-weight:500}.dh svg{color:#C9D3E6}
.dn{font-size:28px;margin-top:22px;font-weight:500}
.dt{font-size:26px;color:#C9D3E6;line-height:1.5;margin-top:10px}
.scan{margin-left:auto;display:flex;align-items:center;gap:12px;font-size:30px;color:#C9D3E6;height:60px;
padding:0 20px;border-radius:16px}.scan b{color:#fff;font-weight:500}.scan i{font-style:normal}
.ring{width:26px;height:26px;border-radius:50%;border:3px solid rgba(255,255,255,.25);border-top-color:#fff;
margin-right:12px;display:inline-block}.ring.big{margin-right:0}
.dsw{display:flex;align-items:center;justify-content:space-between;margin-top:28px;padding:18px 0;
border-top:2px solid rgba(255,255,255,.08);border-bottom:2px solid rgba(255,255,255,.08)}
.dsn{font-size:28px;font-weight:500}.dsl{font-size:24px;color:#C9D3E6;margin-top:4px}
.slotdot{width:13px;height:13px;border-radius:50%;background:#fff;margin-right:14px;display:inline-block}
.scanc{display:inline-flex;align-items:center;gap:10px}.dsw.two{margin-top:0;border-top:none}
.path{font-size:25px;color:#C9D3E6;margin:-6px 0 10px}
.bigs{display:grid;grid-template-columns:repeat(5,1fr);gap:20px;margin:44px 0 0}.bigs.b2{margin-top:30px}
.big{display:flex;align-items:center;gap:16px}.big svg{color:#C9D3E6;flex:none}.big b{font-size:38px;font-weight:500;white-space:nowrap}
.big span{font-size:24px;color:#C9D3E6;line-height:1.2}
.row.lib{box-shadow:inset 0 -2px 0 rgba(255,255,255,.08)}.row.lib:last-child{box-shadow:none}
.split{display:flex;gap:40px;font-size:27px;font-weight:500}.split span{display:inline-flex;align-items:center;gap:10px}
.split svg{color:#C9D3E6}
.dbtn{display:flex;gap:14px;margin-top:34px}.dbtn .b{box-shadow:none;color:#fff;background:none}
.dbtn .b.on{background:#F4F6FB;color:#0B0D12}`;
(async () => {
  const browser = await chromium.launch();
  const tab = await browser.newPage({ viewport: { width: 1920, height: 1440 } });
  const shots = [['43-v18-your-library.png', statsTab]];
  for (const [f, body] of shots) {
    await tab.setContent(`<html><head><style>${css}</style></head><body>${body}</body></html>`, { waitUntil: 'load' });
    await tab.evaluate(() => document.fonts.ready);
    await tab.screenshot({ path: path.join(__dirname, f) });
  }
  await browser.close();
})();
