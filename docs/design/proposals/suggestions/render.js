// Proposals (0.156): the six "approved in principle" ideas. Run: FONT_DIR=<roboto woff2 dir> node render.js
const fs = require('fs'), path = require('path'), { chromium } = require('playwright');
const out = __dirname, fontDir = process.env.FONT_DIR;
const font = w => `@font-face{font-family:R;font-weight:${w};src:url(data:font/woff2;base64,${fs.readFileSync(path.join(fontDir, `roboto-latin-${w}-normal.woff2`)).toString('base64')}) format('woff2')}`;
const P = { play: '<path d="M8 5v14l11-7z"/>', restart: '<path d="M4 12a8 8 0 1 0 2.5-5.8"/><path d="M4 4v4h4"/>', eye: '<path d="M2 12s4-7 10-7 10 7 10 7-4 7-10 7S2 12 2 12z"/><circle cx="12" cy="12" r="3"/>',
  plus: '<path d="M12 5v14M5 12h14"/>', hide: '<path d="M3 3l18 18M10.6 6.1A10 10 0 0 1 12 6c6 0 10 6 10 6a17 17 0 0 1-3 3.6M6.6 6.6C3.8 8.3 2 12 2 12s4 7 10 7a9.6 9.6 0 0 0 4.4-1"/>', trash: '<path d="M4 7h16M9 7V4h6v3M6 7l1 13h10l1-13"/>',
  info: '<circle cx="12" cy="12" r="8.5"/><path d="M12 11v5M12 8h.01"/>', speed: '<path d="M5 18a8 8 0 1 1 14 0"/><path d="M12 13l4-4"/>', moon: '<path d="M19 14.5A7.5 7.5 0 0 1 9.5 5a7.5 7.5 0 1 0 9.5 9.5z"/>',
  ask: '<circle cx="12" cy="12" r="8.5"/><path d="M9.5 9.5a2.5 2.5 0 1 1 3.5 2.3c-.6.3-1 .8-1 1.5v.7M12 17h.01"/>', search: '<circle cx="11" cy="11" r="6.5"/><path d="M16 16l4.5 4.5"/>', chev: '<path d="M9 6l6 6-6 6"/>',
  check: '<path d="M5.5 12.5l4 4L18.5 8"/>', audio: '<path d="M4 9h4l5-4v14l-5-4H4z"/><path d="M16 9a4 4 0 0 1 0 6"/>', cc: '<rect x="3" y="5" width="18" height="14" rx="2"/><path d="M10 10.2a2.2 2.2 0 1 0 0 3.6M16.5 10.2a2.2 2.2 0 1 0 0 3.6"/>',
  series: '<rect x="3" y="6" width="18" height="12" rx="2"/><path d="M7 3h10M10 10l4 2-4 2z"/>', folder: '<path d="M3 6.5A1.5 1.5 0 0 1 4.5 5H9l2 2.5h8.5A1.5 1.5 0 0 1 21 9v8.5a1.5 1.5 0 0 1-1.5 1.5h-15A1.5 1.5 0 0 1 3 17.5z"/>',
  net: '<rect x="3" y="4" width="18" height="6" rx="2"/><rect x="3" y="14" width="18" height="6" rx="2"/>', addc: '<circle cx="12" cy="12" r="8.5"/><path d="M12 8v8M8 12h8"/>', health: '<path d="M12 20s-7-4.5-7-10a4 4 0 0 1 7-2.6A4 4 0 0 1 19 10c0 5.5-7 10-7 10z"/><path d="M8 11h2l1-2 2 4 1-2h2"/>',
  scan: '<path d="M4 8V5a1 1 0 0 1 1-1h3M16 4h3a1 1 0 0 1 1 1v3M20 16v3a1 1 0 0 1-1 1h-3M8 20H5a1 1 0 0 1-1-1v-3M4 12h16"/>', sched: '<circle cx="12" cy="12" r="8.5"/><path d="M12 7v5l3 2"/>', usb: '<path d="M12 3v14M8 7l4-4 4 4M7 13v2a5 5 0 0 0 10 0v-2"/>',
  empty: '<rect x="4" y="5" width="16" height="12" rx="2"/><path d="M8 21h8M9 10l6 3M15 10l-6 3"/>', pen: '<path d="M4 20h4L19 9l-4-4L4 16z"/>', gear: '<circle cx="12" cy="12" r="3.2"/><path d="M12 2.5v3M12 18.5v3M2.5 12h3M18.5 12h3M5.3 5.3l2.1 2.1M16.6 16.6l2.1 2.1M5.3 18.7l2.1-2.1M16.6 7.4l2.1-2.1"/>' };
const ic = (k, s = 26) => `<svg width="${s}" height="${s}" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round">${P[k]}</svg>`;
const nav = (on = '') => `<div class="nav"><span class="brand">SUPERNOVA</span>${['Home', 'Movies', 'TV Shows', 'Documentaries'].map(t => `<span class="${t === on ? 'on' : ''}">${t}</span>`).join('')}<span class="sp"></span><span class="${on === 'NF' ? 'on' : ''}">Network &amp; Files</span><span class="ic">${ic('search', 32)}</span><span class="ic">${ic('gear', 32)}</span><span class="clock">21:40</span></div>`;
const sec = t => `<div class="sec"><span>${t}</span><i></i></div>`;
const row = (k, t, v = '', f) => `<div class="row${f ? ' f' : ''}">${k ? ic(k) : ''}<span class="lbl">${t}</span>${v}</div>`;
const seg = (o, on) => `<span class="val">${o.map(x => `<b class="${x === on ? 'sel' : ''}">${x}</b>`).join('<i>|</i>')}</span>`;
const step = v => `<span class="val"><span class="ch">‹</span><b class="sel">${v}</b><span class="ch">›</span></span>`;
const tg = on => `<span class="tg${on ? ' on' : ''}"><i></i></span>`;
const frame = `<div class="video"></div>`;
const tabs = open => `<div class="tabs">${['Audio', 'Subtitles', 'Video', 'More'].map(t => `<div class="tab${t === open ? ' open' : ''}">${t}</div>`).join('')}</div>`;
const poster = (c, w = 84) => `<span class="pst" style="width:${w}px;height:${w * 1.5}px;background:${c}"></span>`;
const libBg = `<div class="lib">${['#5d4a8a', '#8a4a4a', '#3a6a8a', '#4a8a5a', '#8a7a4a', '#4a4a8a'].map(c => `<div class="lc" style="background:linear-gradient(160deg,${c},#141820)"></div>`).join('')}</div>`;

// 01 shared choice list (Local Storage long-press)
const choice = `${nav('NF')}<div class="page"><div class="ph">${ic('usb', 30)}<div><div class="crumb">Local Storage › Backup #1 (SanDisk USB drive) › TV</div><div class="pt">Dark Matter</div></div></div>
  ${[1, 2, 3, 4, 5, 6].map(n => `<div class="frow${n === 3 ? ' held' : ''}">${ic('play', 22)}<span>Dark.Matter.S02E0${n}.1080p.mkv</span><em>${n < 3 ? 'Watched' : n === 3 ? 'In progress' : ''}</em></div>`).join('')}</div>
  <div class="dim"></div><div class="side"><div class="ctx">Dark.Matter.S02E03.1080p.mkv</div>${sec('Play')}${row('play', 'Resume from 21:14', '', true)}${row('restart', 'Play from the start')}
  ${sec('Library')}${row('eye', 'Mark as watched')}${row('info', 'Show details')}${row('hide', 'Hide from library')}${sec('File')}${row('trash', 'Delete file…')}</div>`;
// 02 sleep timer + still watching settings (player More tab)
const sleep = `${frame}<div class="side tall">${tabs('More')}${sec('Playback')}${row('speed', 'Playback Speed', step('1.0×'))}
  ${row('moon', 'Sleep timer', seg(['Off', '30', '60', '90', 'End of episode'], 'End of episode'), true)}<div class="note">Stops at the end of this episode (23:32)</div>
  ${row('ask', 'Still watching?', step('After 3 episodes'))}${sec('Other')}${row('', 'Play mode', `<span class="val">${ic('chev', 20)}</span>`)}${row('', 'Notifications', `<span class="val">${ic('chev', 20)}</span>`)}</div>`;
// 03 still watching prompt
const still = `${frame}<div class="dimv"></div><div class="card"><div class="cq">Still watching Dark Matter?</div><div class="cs">You've watched 3 episodes in a row. Next: Season 2 · Episode 13</div>
  <div class="cb"><span class="btn f">${ic('play', 24)}Keep watching</span><span class="btn">Stop</span></div><div class="cn">If nothing is pressed, playback stops in 60 seconds</div></div>`;
// 04 fix match
const fix = `${nav('Movies')}<div class="det"><div class="dt">Pressure</div><div class="ds">2026 · 1 h 52 min · Drama</div></div><div class="dim"></div>
  <div class="side tall"><div class="ctx">Fix match · File: Pressure.2026.2160p.mkv</div><div class="srch">${ic('search', 24)}<span>Pressure</span><i class="cur"></i></div>
  ${sec('Matches on TMDB')}${[['Pressure', '2026 · Film', '#6a8aa0', 1], ['Pressure', '2015 · Film', '#8a6a4a'], ['Pressure', '2024 · TV series', '#5a5a8a'], ['Under Pressure', '2022 · Documentary', '#4a7a5a']].map(([t, s, c, cur], i) =>
    `<div class="mr${i === 1 ? ' f' : ''}">${poster(`linear-gradient(160deg,${c},#141820)`, 54)}<div class="mt"><b>${t}</b><span>${s}</span></div>${cur ? '<em>Current</em>' : ''}</div>`).join('')}
  <div class="note">OK picks the right title; details and artwork refresh in place</div></div>`;
// 05 library health badge
const tile = (k, t, d, badge, f) => `<div class="tile${f ? ' f' : ''}">${ic(k, 40)}<b>${t}</b><span>${d}</span>${badge ? `<em class="badge">${badge}</em>` : ''}</div>`;
const health = `${nav('NF')}<div class="tiles">${tile('usb', 'Local Storage', 'USB drives and the Shield')}${tile('net', 'Network Shares', '1 share · Put.io')}${tile('addc', 'Add Connection', 'SMB, SFTP, WebDAV, Put.io')}
  ${tile('health', 'Library Health', '3 new files couldn’t be identified', '3', true)}${tile('sched', 'Library Scanning', 'Every hour · last 21:07')}${tile('scan', 'Scan now', '1,811 titles · 9.3 TB')}</div>`;
// 06 per-series track memory (Audio tab)
const tracks = `${frame}<div class="side tall">${tabs('Audio')}${sec('Audio track')}${row('audio', 'English · Dolby Digital 5.1', `<span class="val sel">${ic('check', 22)}</span>`, true)}${row('audio', 'German · AAC 2.0')}${row('audio', 'English · Commentary')}
  ${sec('For this series')}${row('series', 'Use these choices for Dark Matter', tg(true))}<div class="note">Every Dark Matter episode starts with English 5.1 audio and English subtitles</div></div>`;
// 07 empty page
const empty = `${nav('Documentaries')}<div class="empty">${ic('empty', 64)}<div class="et">Nothing on this page yet</div><div class="es">No titles in your library match this page’s choices (Documentary · 2003–2006).</div><span class="btn f">${ic('pen', 24)}Edit Page</span></div>`;
const shots = [['01-shared-choice-list.png', choice, 'Every old see-through pop-up becomes this side panel: no title, a small line saying what it is for, grouped choices'],
  ['02-sleep-timer.png', sleep, 'Player › More: Sleep timer inline (5 choices) and Still watching? after a set number of episodes'],
  ['03-still-watching.png', still, 'After 3 auto-played episodes: one question, Keep watching selected; playback stops if nothing is pressed'],
  ['04-fix-match.png', fix, 'Details › Fix match: a side panel searches TMDB with the title filled in; OK picks; Details refreshes in place'],
  ['05-library-health-badge.png', health, 'Network & Files: the Library Health tile shows a count when something needs a look'],
  ['06-series-choices.png', tracks, 'Player › Audio: remember this episode’s audio and subtitle choices for the whole series'],
  ['07-empty-page.png', empty, 'Empty pages everywhere: one calm line, why it is empty, one useful button']];
const css = `${font(400)}${font(500)}${font(700)}*{box-sizing:border-box;margin:0}body{width:1920px;height:1080px;overflow:hidden;font-family:R;color:#F4F6FB;background:linear-gradient(160deg,#101a40,#0a1230 60%,#1636a8);position:relative}
.nav{position:absolute;left:56px;right:56px;top:28px;height:56px;display:flex;align-items:center;gap:32px;font-size:34px;color:#e8edf8}.brand{font-size:34px;letter-spacing:1px;margin-right:40px}.nav .on{color:#fff;font-weight:500;text-decoration:underline;text-underline-offset:10px}.sp{flex:1}.nav .ic{display:flex}.clock{margin-left:10px}
.sec{display:flex;align-items:center;gap:14px;font-size:20px;color:#b7bfcc;margin:14px 8px 4px;height:32px}.sec i{flex:1;height:1px;background:rgba(255,255,255,.18)}
.row{display:flex;align-items:center;gap:16px;height:66px;border-radius:20px;padding:0 18px;font-size:24px;font-weight:500}.row svg{color:#dfe5ef;flex:none}.row.f{box-shadow:inset 0 0 0 3px #fff}.lbl{flex:1;white-space:nowrap}
.val{display:flex;align-items:center;gap:8px;font-size:22px;font-weight:400;color:#dfe5f1;white-space:nowrap}.val i{font-style:normal;color:#8f98a8}.val b{font-weight:400}.sel{color:#5aa9ff!important}.ch{color:#8f98a8;font-size:26px;padding:0 6px}
.tg{width:64px;height:34px;border-radius:17px;background:#3a3f48;position:relative;display:inline-block}.tg.on{background:#2fb7d8}.tg i{position:absolute;top:4px;left:4px;width:26px;height:26px;border-radius:50%;background:#fff}.tg.on i{left:34px}
.side{position:absolute;right:48px;top:112px;width:700px;border-radius:26px;padding:22px 24px;background:#16181c;border:2px solid rgba(255,255,255,.22);box-shadow:-20px 0 60px rgba(0,0,0,.5)}.side.tall{top:48px;width:760px}
.ctx{font-size:20px;color:#aab3c4;margin:2px 8px 2px}.note{font-size:19px;color:#aab3c4;margin:2px 22px 8px 60px}
.dim{position:absolute;inset:0;background:rgba(0,0,0,.4)}.dimv{position:absolute;inset:0;background:rgba(0,0,0,.55)}
.video{position:absolute;inset:0;background:radial-gradient(ellipse 40% 45% at 40% 45%,rgba(140,160,200,.55),transparent 70%),linear-gradient(120deg,#1c2a3a,#05070a)}
.tabs{display:flex;gap:14px;margin-bottom:4px}.tab{flex:1;height:68px;display:flex;align-items:center;justify-content:center;font-size:23px;font-weight:500;border-radius:22px;background:#2a2d33}.tab.open{background:#3a3e46}
.page{position:absolute;left:56px;right:56px;top:112px;bottom:40px;border-radius:26px;padding:26px 30px;background:rgba(22,24,28,.97);border:2px solid rgba(255,255,255,.22)}.ph{display:flex;gap:18px;align-items:center;margin-bottom:18px}.crumb{font-size:20px;color:#aab3c4}.pt{font-size:34px;font-weight:500}
.frow{display:flex;align-items:center;gap:16px;height:62px;padding:0 18px;border-radius:18px;font-size:23px}.frow em{margin-left:auto;font-style:normal;font-size:19px;color:#aab3c4}.frow.held{box-shadow:inset 0 0 0 3px rgba(255,255,255,.5)}
.card{position:absolute;left:50%;top:50%;transform:translate(-50%,-50%);width:820px;border-radius:28px;padding:40px 44px;background:rgba(22,24,28,.96);border:2px solid rgba(255,255,255,.22);text-align:center}
.cq{font-size:40px;font-weight:500}.cs{font-size:23px;color:#c9d3e6;margin-top:10px}.cb{display:flex;gap:18px;justify-content:center;margin-top:30px}.cn{font-size:19px;color:#8f98a8;margin-top:22px}
.btn{display:inline-flex;align-items:center;gap:12px;height:66px;padding:0 30px;border-radius:22px;background:#2a2d33;font-size:24px;font-weight:500}.btn.f{background:#F4F6FB;color:#0B0D12}
.det{position:absolute;left:80px;top:560px}.dt{font-size:72px;font-weight:700}.ds{font-size:26px;margin-top:10px;color:#dfe5f1}
.srch{display:flex;align-items:center;gap:12px;height:62px;border-radius:18px;background:#2a2d33;padding:0 20px;font-size:24px;margin:12px 0 6px}.srch .cur{width:2px;height:28px;background:#fff}
.mr{display:flex;align-items:center;gap:18px;padding:10px 14px;border-radius:20px}.mr.f{box-shadow:inset 0 0 0 3px #fff}.pst{border-radius:8px;flex:none;display:block}.mt b{display:block;font-size:24px;font-weight:500}.mt span{font-size:20px;color:#c9d3e6}.mr em{margin-left:auto;font-style:normal;font-size:18px;color:#7fe0a8}
.tiles{position:absolute;left:56px;right:56px;top:150px;display:grid;grid-template-columns:repeat(3,1fr);gap:28px}.tile{position:relative;height:380px;border-radius:26px;background:rgba(22,24,28,.85);border:2px solid rgba(255,255,255,.16);padding:40px;display:flex;flex-direction:column;justify-content:flex-end;gap:10px}
.tile svg{position:absolute;left:40px;top:40px;color:#dfe5ef}.tile b{font-size:34px;font-weight:500}.tile span{font-size:22px;color:#c9d3e6}.tile.f{background:#F4F6FB;color:#0B0D12}.tile.f svg{color:#0B0D12}.tile.f span{color:#3a3f48}
.badge{position:absolute;right:36px;top:36px;min-width:52px;height:52px;border-radius:26px;background:#f0b44c;color:#0B0D12;font-style:normal;font-size:26px;font-weight:700;display:flex;align-items:center;justify-content:center;padding:0 14px}
.empty{position:absolute;left:0;right:0;top:330px;display:flex;flex-direction:column;align-items:center;gap:14px;text-align:center}.empty>svg{color:#aab3c4}.et{font-size:40px;font-weight:500;margin-top:8px}.es{font-size:24px;color:#c9d3e6;margin-bottom:18px}
.label{position:absolute;right:56px;bottom:8px;font-size:18px;color:#c9d3e6;z-index:5}`;
(async () => { const b = await chromium.launch(); const t = await b.newPage({ viewport: { width: 1920, height: 1080 } });
  for (const [f, body, label] of shots) { await t.setContent(`<html><head><style>${css}</style></head><body>${body}<div class="label">${label}</div></body></html>`, { waitUntil: 'load' });
    await t.evaluate(() => document.fonts.ready); await t.screenshot({ path: path.join(out, f) }); }
  await b.close(); })();
