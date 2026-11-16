// Fix match from Details v2 (owner, 16 Nov 2026): on Files › Library Care, remembering the title you came from;
// the focused match offers Match and go back (first) or Match only.
// The same page either way; only the top bar and the path differ. Made-up titles. Run: FONT_DIR=<dir> node render-fixmatch.js
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
  pulse: '<path d="M3 12h4l2-5 4 10 2-5h6"/>',
  chart: '<path d="M4 20V10M10 20V4M16 20v-7M22 20H2"/>',
  globe: '<circle cx="12" cy="12" r="9"/><path d="M3 12h18M12 3c3 3.5 3 14.5 0 18M12 3c-3 3.5-3 14.5 0 18"/>',
  pen: '<path d="M4 20h4L19 9l-4-4L4 16z"/>',
  info: '<circle cx="12" cy="12" r="9"/><path d="M12 11v6M12 7.5h0"/>',
  shift: '<path d="M12 4l7 8h-4v7H9v-7H5z"/>',
  phone: '<rect x="7" y="2.5" width="10" height="19" rx="2"/><path d="M11 18.5h2"/>',
};
const nav = open => `<div class="nav"><span class="brand">SUPERNOVA</span>${['Home', 'Movies', 'TV Shows',
  'Documentaries'].map(t => `<span class="ni ${t === open ? 'open' : ''}">${t}</span>`).join('')}<span class="ni files
  ${open === 'Files' ? 'open' : ''}">Files</span><span class="ni">${svg(I.search, 32)}</span><span class="ni">
  ${svg(I.cog, 32)}</span><span class="clock">21:40</span></div>`;
const tabs = `<div class="tabs"><span class="tab">${svg(I.device)}This Device</span><span class="tab">${svg(I.server)}
  Connections</span><span class="tab on">${svg(I.pulse)}Library Care<i>9</i></span><span class="tab">${svg(I.chart)}
  Your Library</span></div>`;
const keys = '1234567890qwertyuiopasdfghjkl/'.split('');
const kb = top => `<div class="sbox" style="top:${top}px">${svg(I.search, 30)}snowed in<i></i></div><div class="kg"
  style="top:${top + 94}px">${keys.map(k => `<span class="k">${k}</span>`).join('')}<span class="k">${svg(I.shift, 26)}
  </span>${'zxcvbnm.@'.split('').map(k => `<span class="k">${k}</span>`).join('')}<span class="k
  w2">#+=</span><span class="k w2">${svg(I.phone, 26)} Phone</span><span class="k w3">Space</span><span class="k">⌫
  </span><span class="k w2">Clear</span></div><div class="tips" style="top:${top + 520}px"><p>${svg(I.search, 24)}
  <b>Any style</b> capitals, dots and dashes and a leading "The" are ignored</p><p>${svg(I.globe, 24)}<b>Original
  titles</b> search in the film's own language if you like</p><p>${svg(I.pen, 24)}<b>Near misses</b> a letter out of
  place is forgiven</p><p>${svg(I.info, 24)}<b>ID numbers</b> type a TMDB, IMDb, TVDB or Trakt number</p></div>`;
const res = (c, t, s, o = {}) => `<div class="r ${o.focus ? 'f' : ''}"><span class="ra" style="background:linear-gradient(
  150deg,${c},#1f2228)"></span><div><b>${t}</b><small>${s}</small></div>${o.btns ? `<span class="bt on">Match and go back</span>
  <span class="bt">Match only</span>` : o.pill ? `<span class="pill">${o.pill}</span>` : ''}</div>`;
const matches = top => `<div class="mh" style="top:${top}px">Matches on TMDB</div><div class="rs" style="top:${top + 46}px">
  ${res('#6a5a48', 'Snowed In', '2024 · Movie', { pill: 'Best match', focus: true, btns: true })}
  ${res('#3f5f62', 'Snowed In', '2011 · Movie · 1 h 28 min')}${res('#4d4766', 'Snowed Under', '2019 · TV show · 2 seasons')}</div>`;
const pathLine = (top, parts) => `<div class="path" style="top:${top}px">${parts.slice(0, -1).map(p => `<span>${p}</span>
  <em>›</em>`).join('')}<b>${parts[parts.length - 1]}</b></div>`;
const f1 = `${nav('Files')}${tabs}${pathLine(250, ['Library Care', 'Fix match · Snowed In'])}<div class="from">From Snowed In ·
  Back returns there</div>${kb(320)}${matches(320)}`;
const f2 = `${nav('Movies')}${pathLine(150, ['Movies', 'Snowed In', 'Fix match'])}${kb(230)}${matches(230)}`;
const css = `${font(400)}${font(500)}*{box-sizing:border-box;margin:0}body{width:1920px;height:1080px;font-family:R;
color:#fff;background:#16181C;position:relative;overflow:hidden}body:after{content:"";position:absolute;inset:0;
border:6px solid #8a919c;pointer-events:none}
.nav{position:absolute;left:56px;right:56px;top:24px;height:76px;display:flex;align-items:center;gap:14px;font-size:33px;
color:#8f98a8}.brand{color:#f1ead8;letter-spacing:3px;font-weight:500;margin-right:40px}.ni{position:relative;
display:inline-flex;align-items:center;height:60px;padding:0 18px}.ni.files{margin-left:auto}.ni.open{color:#fff}
.ni.open:after{content:'';position:absolute;left:18px;right:18px;bottom:-2px;height:4px;border-radius:2px;
background:rgba(255,255,255,.3)}.clock{margin-left:18px;color:#C9D3E6}
.tabs{position:absolute;left:96px;right:96px;top:128px;height:86px;display:flex;align-items:center;gap:52px;font-size:31px;
color:#C9D3E6;border-bottom:2px solid rgba(255,255,255,.1)}.tab{display:inline-flex;align-items:center;gap:12px;
height:86px;position:relative}.tab.on{color:#fff}.tab.on:after{content:'';position:absolute;left:0;right:0;bottom:-2px;
height:4px;border-radius:2px;background:rgba(255,255,255,.3)}.tab i{font-style:normal;font-size:22px;font-weight:500;
width:34px;height:34px;border-radius:50%;background:#fff;color:#0B0D12;display:inline-flex;align-items:center;
justify-content:center}
.path{position:absolute;left:96px;font-size:32px;color:#C9D3E6;display:flex;gap:18px;align-items:center}.path b{color:#fff;
font-weight:500}.path em{font-style:normal}
.sbox{position:absolute;left:96px;width:760px;height:80px;border-radius:18px;background:rgba(255,255,255,.08);display:flex;
align-items:center;gap:16px;padding:0 28px;font-size:34px}.sbox svg{color:#C9D3E6}.sbox i{width:3px;height:40px;
background:#fff;margin-left:-12px}.kg{position:absolute;left:96px;width:760px;display:grid;grid-template-columns:
repeat(10,1fr);gap:10px}.k{height:64px;border-radius:12px;background:rgba(255,255,255,.06);display:flex;align-items:center;
justify-content:center;gap:8px;font-size:28px}.k.f{background:#F4F6FB;color:#0B0D12}.k.w2{grid-column:span 2}.k.w3{grid-column:
span 3}.tips{position:absolute;left:96px;font-size:23px;color:#C9D3E6}.tips p{display:flex;align-items:center;gap:12px;
margin-bottom:12px}.tips b{color:#fff;font-weight:500}
.mh{position:absolute;left:930px;font-size:28px;color:#C9D3E6}.rs{position:absolute;left:930px;right:96px}
.r{display:flex;align-items:center;gap:26px;height:128px;padding:0 20px;border-radius:18px}.r+.r{border-top:2px solid
rgba(255,255,255,.08)}.ra{width:66px;height:98px;border-radius:8px;box-shadow:inset 0 0 0 2px rgba(255,255,255,.6);
flex:none}.r b{display:block;font-size:34px;font-weight:500}.r small{font-size:27px;color:#C9D3E6}.r .pill{margin-left:auto;
font-size:26px;font-weight:500;padding:8px 26px;border-radius:26px;box-shadow:inset 0 0 0 2px rgba(255,255,255,.45)}
.bt{margin-left:16px;font-size:25px;font-weight:500;padding:12px 22px;border-radius:14px;box-shadow:inset 0 0 0 2px
rgba(11,13,18,.45);white-space:nowrap}.bt.on{margin-left:auto;background:#0B0D12;color:#fff;box-shadow:none}
.from{position:absolute;right:96px;top:256px;font-size:25px;color:#C9D3E6}
.r.f{background:#F4F6FB;color:#0B0D12}.r.f small{color:#0B0D12}.r.f .pill{box-shadow:inset 0 0 0 2px rgba(11,13,18,.5)}`;
(async () => {
  const browser = await chromium.launch();
  const tab = await browser.newPage({ viewport: { width: 1920, height: 1080 } });
  for (const [name, f] of Object.entries({ 'v2-on-files-from-a-title': f1 })) {
    await tab.setContent(`<html><head><style>${css}</style></head><body>${f}</body></html>`, { waitUntil: 'load' });
    await tab.evaluate(() => document.fonts.ready);
    await tab.screenshot({ path: path.join(__dirname, `fix-match-${name}.png`) });
  }
  await browser.close();
})();
