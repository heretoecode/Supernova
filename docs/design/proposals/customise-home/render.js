// Proposal (0.156, owner 4 Nov 2026): Customise Home rebuilt with the approved patterns — one charcoal panel, two-line
// rows with status tags, settings rows with the value on the right, solid side-panel choice list.
// Run: FONT_DIR=<roboto woff2 dir> node render.js
const fs = require('fs'), path = require('path'), { chromium } = require('playwright');
const out = __dirname, fontDir = process.env.FONT_DIR;
const font = w => `@font-face{font-family:R;font-weight:${w};src:url(data:font/woff2;base64,${fs.readFileSync(path.join(fontDir, `roboto-latin-${w}-normal.woff2`)).toString('base64')}) format('woff2')}`;
const P = { hist: '<path d="M3.5 12a8.5 8.5 0 1 0 2.5-6"/><path d="M3 4v4h4M12 8v4l3 2"/>', cal: '<rect x="4" y="5" width="16" height="15" rx="2"/><path d="M4 10h16M9 3v4M15 3v4"/>',
  list: '<path d="M8 6h12M8 12h12M8 18h12M4 6h.01M4 12h.01M4 18h.01"/>', star: '<path d="M12 3l2.7 5.6 6.1.9-4.4 4.3 1 6.1L12 17l-5.4 2.9 1-6.1-4.4-4.3 6.1-.9z"/>',
  spark: '<path d="M12 3v5M12 16v5M3 12h5M16 12h5M6 6l3 3M15 15l3 3M18 6l-3 3M9 15l-3 3"/>', plus: '<path d="M12 5v14M5 12h14"/>', move: '<path d="M12 3v18M8 7l4-4 4 4M8 17l4 4 4-4"/>',
  edit: '<path d="M4 20h4L19 9l-4-4L4 16z"/>', trash: '<path d="M4 7h16M9 7V4h6v3M6 7l1 13h10l1-13"/>', chev: '<path d="M9 6l6 6-6 6"/>', check: '<path d="M5.5 12.5l4 4L18.5 8"/>',
  film: '<rect x="4" y="3" width="16" height="18" rx="2"/><path d="M8 3v18M16 3v18"/>', home: '<path d="M4 11l8-7 8 7v9H4z"/>' };
const ic = (k, s = 28) => `<svg width="${s}" height="${s}" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round">${P[k]}</svg>`;
const nav = `<div class="nav"><span class="brand">SUPERNOVA</span><span class="on">Home</span><span>Movies</span><span>TV Shows</span><span>Documentaries</span><span class="sp"></span><span>Network &amp; Files</span><span class="clock">14:46</span></div>`;
const rowsData = [
  ['hist', 'Continue Watching', 'Built-in · unfinished films and next episodes', true],
  ['cal', 'Recently Added', 'Built-in · newest in your library', true],
  ['list', 'Watch Next', 'Built-in · titles you saved for later', true],
  ['star', 'Trending on Trakt', 'Built-in · in your library', true],
  ['star', 'Popular on Trakt', 'Built-in · in your library', true],
  ['hist', 'Recently Watched', 'Built-in · what you finished last', false],
  ['spark', 'Because You Watched', 'Built-in · similar titles in your library', false],
  ['list', 'Crime Nights', 'Your row · Films · Crime · 15 · Date added', true],
];
const tag = on => on ? '<span class="tag in"><i></i>Shown</span>' : '<span class="tag no"><i></i>Hidden</span>';
const list = (focus, mode) => `<div class="lcol"><div class="head"><div><div class="title">Customise Home</div><div class="sub">8 rows · 6 shown</div></div>
  <div class="btn${focus === 'add' ? ' f' : ''}">${ic('plus', 24)}<span>Add row</span></div></div>
  ${rowsData.map(([k, n, d, on], i) => `<div class="r${focus === i ? (mode === 'moving' ? ' lift' : ' f') : ''}${on ? '' : ' dim'}">${ic(k, 30)}<span class="two"><span class="l1"><span>${n}</span>${tag(on)}</span><span class="l2">${d}</span></span>
  ${focus === i && mode === 'list' ? `<span class="ib"><span class="btn sm">${ic('move', 22)}Move</span><span class="btn sm">${on ? 'Hide' : 'Show'}</span></span>` : ''}${focus === i && mode === 'moving' ? '<span class="mv">↑ ↓ to move · OK when done</span>' : ''}</div>`).join('')}</div>`;
const set = (label, value, focus, extra = '') => `<div class="s${focus ? ' f' : ''}"><span class="sl">${label}</span><span class="sv">${value}</span>${extra}</div>`;
const builtIn = `<div class="rcol"><div class="ptitle">${ic('hist', 34)}Continue Watching</div><div class="pdesc">Unfinished films and the next episode of each series you’re watching.</div>
  ${set('Shown on Home', '<span class="tg on"><i></i></span>', false)}
  ${set('Position', '1 of 8', false, `<span class="chev">${ic('chev', 22)}</span>`)}
  <div class="note">Built-in row — Supernova chooses what appears in it. You can show, hide or move it.</div></div>`;
const custom = (focus) => `<div class="pcol"><div class="ptitle">${ic('list', 34)}Crime Nights</div><div class="pdesc">Your own row. Changes appear on Home straight away.</div>
  ${set('Name', 'Crime Nights', focus === 'name', `<span class="chev">${ic('edit', 22)}</span>`)}
  ${set('Icon', ic('list', 26), focus === 'icon', `<span class="chev">${ic('chev', 22)}</span>`)}
  ${set('Shows', '<span class="seg"><b>Films</b><b class="on">TV shows</b><b>Both</b></span>'.replace('<b>Films</b><b class="on">TV shows</b>', '<b class="on">Films</b><b>TV shows</b>'), focus === 'shows')}
  ${set('Genres', 'Crime', focus === 'genres', `<span class="chev">${ic('chev', 22)}</span>`)}
  ${set('Sort by', 'Date added · newest first', focus === 'sort', `<span class="chev">${ic('chev', 22)}</span>`)}
  ${set('Maximum titles', '15', focus === 'max', `<span class="chev">${ic('chev', 22)}</span>`)}
  ${set('Add new matching titles automatically', '<span class="tg on"><i></i></span>', focus === 'auto')}
  <div class="dangers"><span class="btn danger${focus === 'delete' ? ' f' : ''}">${ic('trash', 24)}<span>Delete row</span></span></div></div>`;
const genres = [['All genres', 324, false], ['Action & Adventure', 16, false], ['Adventure', 13, false], ['Animation', 3, false], ['Comedy', 47, false], ['Crime', 115, true], ['Documentary', 22, false], ['Drama', 140, false]];
const picker = `<div class="pcol"><div class="ptitle">Genres</div><div class="pdesc">Pick one or more. Numbers are titles in your library.</div>
  ${genres.map(([g, n, on], i) => `<div class="opt${i === 5 ? ' f' : ''}"><span class="cb${on ? ' on' : ''}">${on ? ic('check', 22) : ''}</span><span class="on2">${g}</span><span class="cnt">${n}</span></div>`).join('')}
  <div class="pfoot">OK ticks · Back saves and closes</div></div>`;
const preview = (hl) => `<div class="pv">${['Continue Watching', 'Recently Added', 'Watch Next', 'Trending on Trakt', 'Popular on Trakt'].map((t, i) => `<div class="pr${hl === i ? ' hl' : ''}"><div class="prt">${t}</div><div class="prc">${[0, 1, 2, 3].map(j => `<i style="background:linear-gradient(160deg,hsl(${(i * 70 + j * 35) % 360} 40% 42%),#0d1017 85%)"></i>`).join('')}</div></div>`).join('')}</div>`;
const back = t => `<div class="crumb">‹ ${t}</div>`;
const panelList = (focus, mode) => `<div class="panel">${list(focus, mode)}</div>`;
const panelRow = (html, crumb) => `<div class="panel">${back(crumb)}${html}</div>`;
const shots2 = [
  ['01-built-in-row.png', preview(0) + panelList(0, 'list'), 'A side panel over Home (live behind it): the row list; Move and Hide on the focused row; OK opens the row'],
  ['02-your-row.png', preview(-1) + panelRow(custom('genres'), 'Customise Home'), 'OK on your own row: its settings in the same panel (‹ back to the list), values on the right'],
  ['03-genres-picker.png', preview(-1) + panelRow(picker, 'Crime Nights'), 'Genres opens as the next page of the same panel: tick boxes and counts; Back saves'],
  ['04-moving-a-row.png', preview(1) + panelList(3, 'moving'), 'Moving: the row lifts in the list and Home behind updates live as it moves'],
];
const shots = [
  ['01-built-in-row.png', list(0, 'list') + builtIn, false, 'One panel: rows on the left (two lines, Shown/Hidden tag, Move and Hide on the focused row); the selected row’s settings on the right'],
  ['02-your-row.png', list(7, '') + custom('genres'), false, 'Your own row: settings rows with the value on the right; Films / TV shows / Both as one segmented choice; Delete row at the bottom'],
  ['03-genres-picker.png', list(7, '') + custom('genres') + picker, true, 'Genres opens a solid side panel (no overlapping list): tick boxes, counts, Back saves'],
  ['04-moving-a-row.png', list(3, 'moving') + builtIn.replace('Continue Watching', 'Trending on Trakt').replace('Unfinished films and the next episode of each series you’re watching.', 'Titles trending on Trakt that are in your library.').replace('1 of 8', '4 of 8'), false, 'Move works like on Home: the row lifts, Up/Down moves it live, OK drops it'],
];
const css = `${font(400)}${font(500)}*{box-sizing:border-box;margin:0}
body{width:1920px;height:1080px;overflow:hidden;font-family:R;color:#fff;position:relative;background:radial-gradient(ellipse 70% 60% at 70% 75%,#1d4fd8 0%,rgba(29,79,216,0) 70%),linear-gradient(180deg,#05081a 0%,#0a1650 45%,#10248a 100%)}
.nav{position:absolute;left:56px;right:56px;top:28px;height:56px;display:flex;align-items:center;gap:30px;font-size:34px;color:#e8edf8}.nav .brand{font-size:36px;margin-right:40px;letter-spacing:1px}.nav .sp{flex:1}.nav .on{font-weight:500}.nav .clock{margin-left:30px}
.sheet{position:absolute;left:56px;right:56px;top:112px;bottom:40px;border-radius:26px;background:rgba(22,24,28,.97);border:2px solid rgba(255,255,255,.22)}
.divider{position:absolute;left:870px;top:140px;bottom:68px;width:1px;background:rgba(255,255,255,.14)}
.lcol{position:absolute;left:78px;top:134px;width:770px;bottom:60px}.rcol{position:absolute;left:900px;right:86px;top:134px;bottom:60px}
.head{display:flex;align-items:center;justify-content:space-between;margin:0 6px 14px}.title{font-size:36px;font-weight:500}.sub{font-size:20px;color:#c9d3e6;margin-top:4px}
.btn{height:60px;padding:0 22px 0 18px;border-radius:20px;background:#2a2d33;display:inline-flex;align-items:center;gap:10px;font-size:22px;font-weight:500;white-space:nowrap}.btn.f{background:#F4F6FB;color:#0B0D12}
.btn.sm{height:52px;font-size:20px;padding:0 16px}.btn.danger{color:#ffb4b4}.btn.danger.f{background:#F4F6FB;color:#a01818}
.r{display:flex;align-items:center;gap:18px;height:88px;padding:0 18px;border-radius:22px;font-size:25px;font-weight:500}.r.f{box-shadow:inset 0 0 0 3px #fff}.r.dim .two,.r.dim>svg{opacity:.55}
.r.lift{background:rgba(255,255,255,.08);box-shadow:0 14px 34px rgba(0,0,0,.5),inset 0 0 0 3px #fff;transform:translateY(-4px)}
.two{flex:1;min-width:0;display:flex;flex-direction:column;gap:5px}.l1{display:flex;align-items:center;gap:12px;white-space:nowrap}.l2{font-weight:400;font-size:19px;color:#C9D3E6;white-space:nowrap;overflow:hidden;text-overflow:ellipsis}
.ib{display:flex;gap:10px}.mv{font-size:19px;font-weight:400;color:#c9d3e6}
.tag{font-size:17px;font-weight:500;border-radius:11px;padding:3px 10px;display:inline-flex;align-items:center;gap:7px}.tag i{width:9px;height:9px;border-radius:50%}
.tag.in{background:rgba(90,209,138,.13);color:#9fe3bb}.tag.in i{background:#5ad18a}.tag.no{background:rgba(255,255,255,.06);color:#aab3c4}.tag.no i{border:1.6px solid #aab3c4}
.ptitle{display:flex;align-items:center;gap:14px;font-size:36px;font-weight:500;margin:4px 6px 8px}.pdesc{font-size:22px;color:#c9d3e6;margin:0 6px 22px}
.s{display:flex;align-items:center;gap:16px;height:80px;padding:0 20px;border-radius:22px;border-bottom:1px solid rgba(255,255,255,.08)}.s.f{box-shadow:inset 0 0 0 3px #fff;border-bottom-color:transparent}
.sl{flex:1;font-size:25px;font-weight:500}.sv{font-size:23px;color:#dfe5f1;display:flex;align-items:center}.chev{color:#8f98a8;display:flex}
.seg{display:inline-flex;background:#2a2d33;border-radius:18px;padding:4px}.seg b{font-weight:500;font-size:20px;padding:10px 18px;border-radius:14px}.seg b.on{background:#F4F6FB;color:#0B0D12}
.tg{width:64px;height:34px;border-radius:17px;background:#3a3f48;position:relative;display:inline-block}.tg.on{background:#2fb7d8}.tg i{position:absolute;top:4px;left:4px;width:26px;height:26px;border-radius:50%;background:#fff}.tg.on i{left:34px}
.note{margin:24px 6px 0;font-size:21px;color:#aab3c4;line-height:1.4}.dangers{margin-top:26px;padding:0 6px}
.side{position:absolute;right:56px;top:112px;bottom:40px;width:640px;border-radius:26px;background:#24272d;border:2px solid rgba(255,255,255,.24);padding:28px;box-shadow:-20px 0 60px rgba(0,0,0,.5)}
.opt{display:flex;align-items:center;gap:16px;height:72px;padding:0 18px;border-radius:20px;font-size:24px;font-weight:500}.opt.f{box-shadow:inset 0 0 0 3px #fff}.on2{flex:1}.cnt{font-size:22px;color:#c9d3e6;font-weight:400}
.cb{width:34px;height:34px;border-radius:9px;border:2px solid #aab3c4;display:flex;align-items:center;justify-content:center}.cb.on{background:#F4F6FB;border-color:#F4F6FB;color:#0B0D12}
.pfoot{position:absolute;left:28px;bottom:22px;font-size:18px;color:#8f98a8}
.pv{position:absolute;left:56px;top:120px;width:1100px;opacity:.55}.pr{margin-bottom:26px;border-radius:18px;padding:6px}.pr.hl{opacity:1;box-shadow:0 0 0 3px rgba(255,255,255,.7)}
.prt{font-size:26px;font-weight:500;margin-bottom:10px}.prc{display:flex;gap:16px}.prc i{display:block;width:250px;height:140px;border-radius:14px}
.panel{position:absolute;right:56px;top:112px;bottom:40px;width:760px;border-radius:26px;background:rgba(22,24,28,.97);border:2px solid rgba(255,255,255,.22);padding:26px 24px;box-shadow:-20px 0 60px rgba(0,0,0,.45)}
.panel .lcol,.panel .pcol{position:static;width:auto}.crumb{font-size:20px;color:#b7bfcc;margin:0 6px 8px}
.panel .r{height:80px}.panel .s{height:74px}.panel .pfoot{position:static;margin-top:10px}
.label{position:absolute;right:56px;bottom:8px;font-size:18px;color:#c9d3e6}`;
(async () => { const b = await chromium.launch(); const t = await b.newPage({ viewport: { width: 1920, height: 1080 } });
  for (const [f, body, label] of shots2) {
    await t.setContent(`<html><head><style>${css}</style></head><body>${nav}${body}<div class="label">${label}</div></body></html>`, { waitUntil: 'load' });
    await t.evaluate(() => document.fonts.ready);
    const issues = await t.evaluate(() => { const o = [], r = e => e.getBoundingClientRect(); const sh = document.querySelector('.panel');
      document.querySelectorAll('.panel > *, .panel .lcol > *, .panel .pcol > *').forEach(e => { if (r(e).bottom > r(sh).bottom - 8) o.push('overflow: ' + e.className + ' ' + e.textContent.slice(0, 30)); });
      document.querySelectorAll('.r').forEach(e => { if (e.scrollWidth > e.clientWidth + 1) o.push('row too wide: ' + e.textContent.slice(0, 30)); });
      
      return o; });
    if (issues.length) console.log(f, issues);
    await t.screenshot({ path: path.join(out, f) }); }
  await b.close(); })();
