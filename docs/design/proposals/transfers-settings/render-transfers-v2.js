// Transfers settings v2 (owner, 16 Nov 2026): keep free as a percentage; Needs your OK on the tab; full speed hours
// and where copies go panels; one-line warning. Was v1: How Copies Run on Files › Transfers, and the "Copy anyway?" question when
// a copy you start would dip well into the space you keep free. Made-up titles. Run: FONT_DIR=<dir> node render-transfers.js
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
  cog: '<circle cx="12" cy="12" r="3"/><path d="M12 2v3M12 19v3M4.2 4.2l2.1 2.1M17.7 17.7l2.1 2.1M2 12h3M19 12h3M4.2 19.8l2.1-2.1M17.7 6.3l2.1-2.1"/>',
  device: '<rect x="3" y="4" width="18" height="12" rx="2"/><path d="M8 20h8M12 16v4"/>',
  server: '<rect x="4" y="4" width="16" height="7" rx="2"/><rect x="4" y="13" width="16" height="7" rx="2"/>',
  pulse: '<path d="M3 12h4l2-5 4 10 2-5h6"/>', chart: '<path d="M4 20V10M10 20V4M16 20v-7M22 20H2"/>',
  down: '<path d="M12 4v11M7 10l5 5 5-5M5 20h14"/>', glass: '<path d="M6 3h12M6 21h12M7 3c0 6 10 6 10 9s-10 3-10 9"/>',
  tick: '<circle cx="12" cy="12" r="9"/><path d="M8 12.5l3 3 5-6"/>', folder: '<path d="M3 6h6l2 2h10v11H3z"/>',
  bin: '<path d="M5 7h14M10 7V4h4v3M7 7l1 13h8l1-13"/>', gauge: '<path d="M4 16a8 8 0 1 1 16 0"/><path d="M12 16l4-5"/>',
  disk: '<rect x="3" y="6" width="18" height="12" rx="2"/><path d="M7 12h6M17 12h0"/>',
  play: '<path d="M7 4l13 8-13 8z"/>', moon: '<path d="M20 14.5A8 8 0 1 1 9.5 4a6.5 6.5 0 0 0 10.5 10.5z"/>',
  stack: '<path d="M12 3l9 5-9 5-9-5z"/><path d="M3 13l9 5 9-5"/>', warn: '<path d="M12 4l9 16H3z"/><path d="M12 10v4M12 17h0"/>',
  pause: '<path d="M9 5v14M15 5v14"/>', x: '<path d="M6 6l12 12M18 6L6 18"/>', chev: '<path d="M9 6l6 6-6 6"/>',
};
I.clock = '<circle cx="12" cy="12" r="9"/><path d="M12 7v5l3 2"/>';
I.sun = '<circle cx="12" cy="12" r="4"/><path d="M12 2v2.5M12 19.5V22M2 12h2.5M19.5 12H22M4.9 4.9l1.8 1.8M17.3 17.3l1.8 1.8M4.9 19.1l1.8-1.8M17.3 6.7l1.8-1.8"/>';
I.film = '<rect x="3" y="4" width="18" height="16" rx="2"/><path d="M7 4v16M17 4v16M3 9h4M3 15h4M17 9h4M17 15h4"/>';
I.tv = '<rect x="3" y="6" width="18" height="12" rx="2"/><path d="M8 21h8M9 3l3 3 3-3"/>';
const nav = `<div class="nav"><span class="brand">SUPERNOVA</span>${['Home', 'Movies', 'TV Shows', 'Documentaries']
  .map(t => `<span class="ni">${t}</span>`).join('')}<span class="ni files open">Files<i></i></span><span class="ni">
  ${svg(I.search, 32)}</span><span class="ni">${svg(I.cog, 32)}</span><span class="clock">21:40</span></div>`;
const tabs = `<div class="tabs"><span class="tab">${svg(I.device)}This Device</span><span class="tab">${svg(I.server)}
  Connections</span><span class="tab">${svg(I.pulse)}Library Care<i class="bd">3</i></span><span class="tab on">${svg(I.down)}Transfers<i class="bd">1</i></span>
  <span class="tab">${svg(I.chart)}Your Library</span><span class="facts">${svg(I.down)}<b>1</b> Copying ·
  ${svg(I.glass)}<b>1</b> Waiting · ${svg(I.device)}<b>412 GB</b> Free</span></div>`;
const sec = (ic, t) => `<div class="sec">${svg(I[ic])}${t}</div>`;
const item = (c, t, s, right, o = {}) => `<div class="row it ${o.focus ? 'f' : ''}"><span class="art" style="background:
  linear-gradient(150deg,${c},#1f2228)"></span><div class="tx"><b>${t}</b><small>${s}</small>${o.bar ? `<span class="bar">
  <span style="width:${o.bar}%"></span></span>` : ''}</div>${right}</div>`;
const pill = t => `<span class="pill">${t}</span>`;
const chips = (opts, on) => `<span class="chips">${opts.map(o => `<span class="${o === on ? 'chip' : ''}">${o}</span>`)
  .join('')}</span>`;
const set = (ic, t, s, end, o = {}) => `<div class="row st ${o.focus ? 'f' : ''}">${svg(I[ic], 32)}<div class="tx"><b>${t}</b>
  ${s ? `<small>${s}</small>` : ''}</div><span class="end">${end}</span></div>`;
const needs = (focus) => `${sec('warn', 'Needs your OK')}<div class="row it ${focus ? 'f' : ''}"><span class="art"
  style="background:linear-gradient(150deg,#6a5a48,#1f2228)"></span><div class="tx"><b>Dune: Part Two · 62 GB</b><small>
  Paused: leaves 31 GB free, under the 10% you keep free</small></div>${focus ? `<span class="btn dk">Copy anyway</span>
  <span class="btn">Choose another folder</span><span class="btn">Cancel</span>` : `<span class="pill">Needs your OK</span>`}
  </div>`;
const how = (focusRow) => `${sec('gauge', 'How copies run')}
  ${set('disk', 'Keep free on this device', 'Small overruns are fine; bigger ones wait for your OK',
    chips(['5%', '10% · 100 GB', '15%', '20%'], '10% · 100 GB'), { focus: focusRow === 'keep' })}
  ${set('play', 'While you watch', 'From the network or this device', chips(['Pause', 'Slow down', 'Carry on'], 'Slow down'))}
  ${set('moon', 'Full speed hours', 'Copies run flat out; outside these hours the row above applies',
    `<span class="val">02:00–07:00</span>${svg(I.chev, 26)}`, { focus: focusRow === 'hours' })}
  ${set('stack', 'Copies at once', '', chips(['1', '2', '3'], '1'))}
  ${sec('folder', 'Where copies go')}${set('folder', 'Movies and TV Shows', 'This device › Movies · This device › TV Shows',
    svg(I.chev, 26), { focus: focusRow === 'where' })}
  ${set('bin', 'Remove the network copy', 'Only after the copy is checked and matched; applies to every copy',
    '<span class="sw"></span>')}`;
const lists = `${sec('down', 'Copying now')}${item('#1f3a4a', 'Marshals · S1 E5',
  'From Put.io · 1.4 of 2.1 GB · slowed while you watch', pill('67%'), { bar: 67 })}
  ${sec('glass', 'Waiting')}${item('#4a2a44', 'Conclave', 'From Backup #1 · 4.2 GB · one copy at a time', pill('Waiting'))}`;
const page = `${nav}${tabs}<div class="body">${needs(true)}${lists}${how('')}</div>`;
const pageF = f => `${nav}${tabs}<div class="body">${needs(false)}${lists}${how(f)}</div>`;
const panel = rows => `<div class="dim"></div><div class="side">${rows}</div>`;
const prow = (ic, t, s, end, o = {}) => `<div class="pr ${o.focus ? 'f' : ''}">${svg(I[ic], 30)}<div class="tx"><b>${t}</b>
  ${s ? `<small>${s}</small>` : ''}</div><span class="end">${end}</span></div>`;
const step = v => `<span class="stepv">‹ <b>${v}</b> ›</span>`;
const hours = `${pageF('hours')}${panel(`${prow('moon', 'Full speed hours', '', '<span class="sw on"></span>')}
  ${prow('clock', 'From', '', step('02:00'), { focus: true })}${prow('sun', 'Until', '', step('07:00'))}
  <div class="sep"></div>${prow('gauge', 'Full speed now', 'Until 07:00, or until you watch something', '')}
  <p class="ph">Left and Right change the time in half hours.</p>`)}`;
const where = `${pageF('where')}${panel(`${prow('film', 'Movies', 'This device › Movies', svg(I.chev, 26),
  { focus: true })}${prow('tv', 'TV Shows', 'This device › TV Shows', svg(I.chev, 26))}
  <p class="ph">Each opens the folder picker.</p>`)}`;
const ask = `${page}<div class="dim"></div><div class="warn"><div class="wt">${svg(I.warn, 34)}<b>Copy anyway?</b></div>
  <p class="nm">Dune: Part Two · 62 GB</p><p>Leaves 31 GB free, under the 10% (100 GB) you keep free.</p><div class="wb">
  <span class="wbtn f">Cancel</span><span class="wbtn">Copy anyway</span><span class="wbtn">Choose another folder</span>
  </div></div>`;
const css = `${font(400)}${font(500)}*{box-sizing:border-box;margin:0}body{width:1920px;font-family:R;color:#fff;
background:#16181C;position:relative}body:after{content:"";position:absolute;inset:0;border:6px solid #8a919c;
pointer-events:none;z-index:99}
.nav{position:absolute;left:56px;right:56px;top:24px;height:76px;display:flex;align-items:center;gap:14px;font-size:33px;
color:#8f98a8}.brand{color:#f1ead8;letter-spacing:3px;font-weight:500;margin-right:40px}.ni{position:relative;
display:inline-flex;align-items:center;height:60px;padding:0 18px}.ni.files{margin-left:auto}.ni.open{color:#fff}
.ni.open:after{content:'';position:absolute;left:18px;right:18px;bottom:-2px;height:4px;border-radius:2px;
background:rgba(255,255,255,.3)}.ni i{position:absolute;right:6px;top:8px;width:10px;height:10px;border-radius:50%;
background:#fff}.clock{margin-left:18px;color:#C9D3E6}
.tabs{position:absolute;left:96px;right:96px;top:128px;height:86px;display:flex;align-items:center;gap:36px;font-size:28px;white-space:nowrap;
color:#C9D3E6;border-bottom:2px solid rgba(255,255,255,.1)}.tab{display:inline-flex;align-items:center;gap:12px;
height:86px;position:relative}.tab.on{color:#fff}.tab.on:after{content:'';position:absolute;left:0;right:0;bottom:-2px;
height:4px;border-radius:2px;background:#fff}.facts{margin-left:auto;display:flex;align-items:center;gap:10px;color:#fff}
.facts b{font-weight:500}
.body{padding:240px 96px 60px}.sec{display:flex;align-items:center;gap:16px;font-size:32px;font-weight:500;margin:64px 0 26px}
.sec:first-child{margin-top:0}.sec svg{color:#C9D3E6}
.row{display:flex;align-items:center;gap:30px;min-height:110px;padding:14px 34px;border-radius:18px}.row+.row{border-top:2px
solid rgba(255,255,255,.08)}.row svg{color:#C9D3E6;flex:none}.tx b{display:block;font-size:34px;font-weight:500}.tx small{
font-size:26px;color:#C9D3E6}.end{margin-left:auto;display:flex;align-items:center;gap:12px}.val{font-size:29px;font-weight:500}
.art{width:128px;height:72px;border-radius:10px;box-shadow:inset 0 0 0 2px rgba(255,255,255,.6);flex:none}
.it .pill{margin-left:auto}.pill{min-width:190px;text-align:center;font-size:26px;font-weight:500;padding:6px 26px;
border-radius:24px;box-shadow:inset 0 0 0 2px rgba(255,255,255,.45)}.btn{display:inline-flex;align-items:center;gap:10px;
font-size:28px;font-weight:500;padding:12px 24px;border-radius:14px;box-shadow:inset 0 0 0 2px rgba(255,255,255,.4);
margin-left:16px}.btn.dk{background:#fff;color:#0B0D12;box-shadow:none}.btn svg{color:inherit}
.bar{display:block;width:560px;height:6px;border-radius:3px;background:rgba(255,255,255,.2);margin-top:10px}.bar span{
display:block;height:6px;border-radius:3px;background:#fff}
.chips{display:flex;gap:6px;font-size:28px;color:#C9D3E6}.chips span{padding:8px 18px;border-radius:16px}.chips .chip{
background:rgba(255,255,255,.14);color:#fff;font-weight:500}
.row.f{background:#F4F6FB;color:#0B0D12}.row.f small,.row.f svg,.row.f .chips{color:#0B0D12}.row.f .chips .chip{
background:#0B0D12;color:#fff}.row.f+.row{border-top-color:transparent}
.sw{width:72px;height:40px;border-radius:20px;background:rgba(255,255,255,.25);position:relative;display:inline-block}
.sw:after{content:'';position:absolute;left:5px;top:5px;width:30px;height:30px;border-radius:50%;background:#1f2228}
.tab i.bd{font-style:normal;font-size:20px;font-weight:500;width:32px;height:32px;border-radius:50%;background:#fff;
color:#0B0D12;display:inline-flex;align-items:center;justify-content:center}
.side{position:absolute;right:56px;top:110px;width:720px;padding:14px;border-radius:24px;background:rgba(22,24,28,.95);
box-shadow:0 0 0 2px rgba(255,255,255,.14),0 30px 80px rgba(0,0,0,.7);z-index:8}.pr{display:flex;align-items:center;gap:20px;
min-height:84px;padding:8px 24px;border-radius:18px}.pr svg{color:#C9D3E6;flex:none}.pr .tx b{font-size:29px}.pr .tx small{
font-size:22px}.pr.f{background:#F4F6FB;color:#0B0D12}.pr.f svg,.pr.f small,.pr.f .stepv,.pr.f .stepv b{color:#0B0D12}
.stepv{font-size:28px;color:#C9D3E6}.stepv b{color:#fff;font-weight:500;margin:0 8px}.sep{height:2px;
background:rgba(255,255,255,.12);margin:8px 24px}.ph{font-size:22px;color:#9aa3b4;margin:12px 24px 6px}
.sw.on{background:#fff}.sw.on:after{left:auto;right:5px;background:#0B0D12}
.row.f .btn{box-shadow:inset 0 0 0 2px rgba(11,13,18,.45);background:none}.row.f .btn.dk{background:#0B0D12;color:#fff;box-shadow:none}
.facts{font-size:26px}
.dim{position:absolute;inset:0;background:rgba(10,11,14,.65);z-index:7}.warn{position:absolute;left:50%;top:430px;
transform:translateX(-50%);width:920px;padding:40px;border-radius:24px;background:rgba(22,24,28,.95);box-shadow:0 0 0 2px
rgba(255,255,255,.14),0 30px 80px rgba(0,0,0,.7);z-index:8}.wt{display:flex;align-items:center;gap:16px;font-size:36px;
font-weight:500}.warn p{font-size:27px;line-height:1.45;color:#C9D3E6;margin:14px 0 0}.warn p.nm{color:#fff;font-weight:500}
.wb{display:flex;gap:16px;margin-top:32px}.wbtn{height:72px;padding:0 32px;border-radius:36px;display:inline-flex;
align-items:center;font-size:28px;font-weight:500;background:rgba(255,255,255,.1)}.wbtn.f{background:#F4F6FB;color:#0B0D12}`;
(async () => {
  const browser = await chromium.launch();
  for (const [name, f] of Object.entries({ 'v2-01-tab': page, 'v2-02-full-speed-hours': hours,
    'v2-03-where-copies-go': where, 'v2-04-copy-anyway': ask })) {
    const tab = await browser.newPage({ viewport: { width: 1920, height: 1080 } });
    await tab.setContent(`<html><head><style>${css}</style></head><body>${f}</body></html>`, { waitUntil: 'load' });
    await tab.evaluate(() => document.fonts.ready);
    await tab.screenshot({ path: path.join(__dirname, `transfers-${name}.png`), fullPage: true });
    await tab.close();
  }
  await browser.close();
})();
