// Onboarding, first pass (owner, 14 Nov 2026). Six screens at full 1080p, laid out two by three:
// row 1 Welcome (Start fresh / Restore from a backup) and Access to files (asked once, so later deletes and folder
// pickers never show Android screens); row 2 Add your videos (Start fresh) and Find your backup (Restore);
// row 3 Languages (Start fresh) and Restored: sign in again (backups keep no passwords).
// Built from the rulebook: charcoal page, the new mark, rows as in Settings, choice rows with chips, step dots like
// Home's carousel dots. Made-up names and sizes. Light frame = picture edge.
// Run: FONT_DIR=<roboto woff2 dir> node render.js
const fs = require('fs');
const path = require('path');
const { chromium } = require('playwright');
const fontDir = process.env.FONT_DIR;
const font = w => `@font-face{font-family:R;font-weight:${w};src:url(data:font/woff2;base64,${fs.readFileSync(
  path.join(fontDir, `roboto-latin-${w}-normal.woff2`)).toString('base64')}) format('woff2')}`;
const svg = (d, s = 34) => `<svg width="${s}" height="${s}" viewBox="0 0 24 24" fill="none" stroke="currentColor"
  stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round">${d}</svg>`;
const I = {
  fresh: '<path d="M12 3v4M12 17v4M3 12h4M17 12h4M6 6l2.5 2.5M15.5 15.5L18 18M6 18l2.5-2.5M15.5 8.5L18 6"/>',
  restore: '<path d="M4 12a8 8 0 1 0 2.4-5.7"/><path d="M4 4v4h4"/>',
  folder: '<path d="M3 7a2 2 0 0 1 2-2h4l2 2h8a2 2 0 0 1 2 2v8a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2z"/>',
  usb: '<rect x="7" y="9" width="10" height="12" rx="2"/><path d="M9 9V4h6v5M10.5 6h0M13.5 6h0"/>',
  net: '<rect x="3" y="4" width="18" height="6" rx="1.5"/><rect x="3" y="14" width="18" height="6" rx="1.5"/>'
    + '<path d="M7 7h0M7 17h0"/>',
  cloud: '<path d="M7 18a4 4 0 0 1-.6-8A6 6 0 0 1 18 9a4.5 4.5 0 0 1-.5 9z"/>',
  audio: '<path d="M4 9v6h4l5 4V5L8 9z"/><path d="M16 9a4 4 0 0 1 0 6"/>',
  subs: '<rect x="3" y="5" width="18" height="14" rx="2"/><path d="M7 14h4M13 14h4M7 10h10"/>',
  globe: '<circle cx="12" cy="12" r="9"/><path d="M3 12h18M12 3a14 14 0 0 1 0 18M12 3a14 14 0 0 0 0 18"/>',
  chev: '<path d="M9 6l6 6-6 6"/>',
  tick: '<path d="M5 12.5l4.5 4.5L19 7.5"/>',
  key: '<circle cx="8" cy="15" r="4"/><path d="M11 12l8-8M16 7l2 2"/>',
  shield: '<path d="M12 3l8 3v6c0 4.5-3.4 8-8 9-4.6-1-8-4.5-8-9V6z"/>',
  eye: '<path d="M2 12s3.6-7 10-7 10 7 10 7-3.6 7-10 7S2 12 2 12z"/><circle cx="12" cy="12" r="3"/>',
};
const pt = (r, a) => [50 + r * Math.cos((a - 90) * Math.PI / 180), 50 + r * Math.sin((a - 90) * Math.PI / 180)];
const arc = (r, a0, a1) => {
  const [x0, y0] = pt(r, a0);
  const [x1, y1] = pt(r, a1);
  return `M${x0} ${y0}A${r} ${r} 0 1 1 ${x1} ${y1}`;
};
const mark = `<svg viewBox="0 0 100 100" width="100%" height="100%"><path d="${arc(40, 125, 405)}" stroke="#fff"
  stroke-width="6" fill="none" stroke-linecap="round"/><path d="${arc(24, 225, 505)}" stroke="#fff" stroke-width="5"
  fill="none" stroke-linecap="round"/></svg>`;

const row = (icon, title, sub, o = {}) => `<div class="row ${o.focus ? 'focus' : ''}">${svg(icon)}<div class="rt">
  <b>${title}</b>${sub ? `<small>${sub}</small>` : ''}</div><span class="end">${o.end || ''}${o.chev ? svg(I.chev, 30)
  : ''}${o.tick ? svg(I.tick, 30) : ''}</span></div>`;
const chip = t => `<span class="chip">${t}</span>`;
const flag = c => `<span class="flag" style="background:${c}"></span>`;
const GB = 'linear-gradient(90deg,#012169 0 100%)';
const dots = (n, on) => `<div class="dots">${[0, 1, 2, 3].map(i => `<i class="${i === on ? 'on' : ''}"></i>`)
  .join('').slice(0, 9999)}</div>`.replace(/<i[^>]*><\/i>/g, (m, off, s) => m) + '';
const btn = (t, focus) => `<span class="btn ${focus ? 'focus' : ''}">${t}</span>`;
const screen = (body, step) => `<div class="screen"><div class="col">${body}</div>${step === null ? '' :
  dots(4, step)}</div>`;

const welcome = screen(`<div class="hero"><div class="mk">${mark}</div><h1>Welcome to Supernova</h1>
  <p>Your films and TV shows, from this Shield, drives and network shares, in one place.</p></div>
  <div class="list">${row(I.fresh, 'Start fresh', 'Choose your folders and languages', { focus: true, chev: true })}
  ${row(I.restore, 'Restore from a backup', 'Bring back your settings, rows, history and matches', { chev: true })}
  </div>`, 0);
const access = screen(`<div class="head"><div class="hi">${svg(I.shield, 56)}</div><h2>Access to your files</h2>
  <p>Supernova needs this once, so it can play, copy and delete your videos without Android stopping to ask each
  time. Android shows one screen: switch Supernova on, then press Back.</p></div>
  <div class="actions">${btn('Allow access', true)}${btn('Not now')}</div>`, 1);
const videos = screen(`<div class="head"><h2>Add your videos</h2><p>Pick the folders that hold your films and TV shows.
  Nothing is scanned until you press Continue.</p></div><div class="list">
  ${row(I.folder, 'Folders on this Shield', 'Movies, TV Shows', { end: chip('2 folders'), chev: true })}
  ${row(I.usb, 'USB drive', 'Media Drive', { chev: true })}
  ${row(I.net, 'Network share', 'A computer or NAS on your network', { focus: true, chev: true })}
  ${row(I.cloud, 'Put.io', 'Sign in with a code on your phone', { chev: true })}</div>
  <div class="actions">${btn('Continue')}</div>`, 2);
const languages = screen(`<div class="head"><h2>Languages</h2><p>Change these any time in Settings.</p></div>
  <div class="list">${row(I.audio, 'Audio language', '', { end: `${flag(GB)}<span class="val">English</span>`,
    chev: true, focus: true })}
  ${row(I.subs, 'Subtitles', 'When the audio is in another language', { end: `${flag(GB)}<span class="val">English</span>`, chev: true })}
  ${row(I.globe, 'Where to Watch region', 'From this Shield', { end: '<span class="val">United States</span>',
    chev: true })}</div>
  <div class="actions">${btn('Finish and start scanning')}</div>`, 3);
const find = screen(`<div class="head"><h2>Find your backup</h2><p>Where did you save it?</p></div>
  <div class="list">${row(I.folder, 'This Shield', 'Nothing found', {})}
  ${row(I.usb, 'USB drive', 'Media Drive · 3 backups', { focus: true, chev: true })}
  ${row(I.net, 'Network share', 'Browse a computer or NAS', { chev: true })}</div>`, 2);
const signin = screen(`<div class="head"><div class="hi">${svg(I.tick, 56)}</div><h2>Restored</h2><p>Your settings,
  rows, history and matches are back. Backups never keep passwords, so sign in to these again.</p></div>
  <div class="list">${row(I.net, 'Living Room NAS', 'Password needed', { focus: true, chev: true })}
  ${row(I.cloud, 'Put.io', 'Sign in with a code', { chev: true })}
  ${row(I.net, 'Office PC', 'Signed in', { tick: true })}</div>
  <div class="actions">${btn('Restart Supernova')}</div>`, 3);

const css = `${font(300)}${font(400)}${font(500)}*{box-sizing:border-box;margin:0}
body{width:1920px;font-family:R;color:#fff;background:#8a919c;position:relative}
body:after{content:"";position:fixed;inset:0;border:6px solid #8a919c;z-index:99;pointer-events:none}
.grid{display:grid;grid-template-columns:960px 960px;gap:0}
.cell{width:960px;height:540px;overflow:hidden;border:4px solid #8a919c}
.screen{width:1920px;height:1080px;transform:scale(.4958);transform-origin:0 0;background:#16181C;position:relative;
display:flex;justify-content:center}
.col{width:980px;padding-top:120px}
.hero{text-align:center}.mk{width:150px;height:150px;margin:0 auto 46px}
h1{font-size:60px;font-weight:400}h2{font-size:52px;font-weight:400}
p{font-size:30px;color:#C9D3E6;line-height:1.45;margin-top:22px}
.hero p{max-width:820px;margin:22px auto 0}
.head{margin-bottom:46px}.hi{width:96px;height:96px;border-radius:50%;background:rgba(255,255,255,.08);display:flex;
align-items:center;justify-content:center;margin-bottom:34px}
.hero+.list{margin-top:70px}
.row{position:relative;display:flex;align-items:center;gap:26px;height:104px;padding:0 30px;border-radius:20px}
.row svg{flex:none;color:#C9D3E6}.rt{display:flex;flex-direction:column}.rt b{font-size:32px;font-weight:500}
.rt small{font-size:24px;color:#C9D3E6;margin-top:4px}
.row:after{content:'';position:absolute;left:30px;right:30px;bottom:0;height:2px;background:rgba(255,255,255,.08)}
.row:last-child:after,.row.focus:after,.row:has(+ .row.focus):after{display:none}
.row.focus{background:#F4F6FB;color:#0B0D12}.row.focus svg,.row.focus small{color:#0B0D12}
.end{margin-left:auto;display:flex;align-items:center;gap:14px;font-size:28px}
.chip{font-size:24px;padding:6px 16px;border-radius:14px;box-shadow:inset 0 0 0 2px rgba(255,255,255,.35)}
.row.focus .chip{box-shadow:inset 0 0 0 2px rgba(11,13,18,.4)}
.val{font-weight:500}.flag{width:42px;height:28px;border-radius:4px;flex:none;
background:linear-gradient(90deg,#3C3B6E 0 40%,transparent 40%) top/100% 54% no-repeat,
repeating-linear-gradient(180deg,#B22234 0 2.15px,#fff 2.15px 4.3px)!important;box-shadow:0 0 0 1px rgba(255,255,255,.3)}
.actions{display:flex;gap:22px;margin-top:56px}
.btn{height:84px;padding:0 44px;border-radius:42px;display:inline-flex;align-items:center;font-size:30px;
font-weight:500;background:rgba(255,255,255,.1)}.btn.focus{background:#F4F6FB;color:#0B0D12}
.dots{position:absolute;bottom:64px;left:50%;transform:translateX(-50%);display:flex;gap:16px}
.dots i{width:14px;height:14px;border-radius:50%;background:rgba(255,255,255,.3)}.dots i.on{background:#fff;width:40px;
border-radius:7px}`;

(async () => {
  const browser = await chromium.launch();
  const tab = await browser.newPage({ viewport: { width: 1920, height: 1620 }, deviceScaleFactor: 2 });
  const cells = [welcome, access, videos, find, languages, signin].map(s => `<div class="cell">${s}</div>`).join('');
  await tab.setContent(`<html><head><style>${css}</style></head><body><div class="grid">${cells}</div></body></html>`,
    { waitUntil: 'load' });
  await tab.evaluate(() => document.fonts.ready);
  await tab.screenshot({ path: path.join(__dirname, '01-onboarding-first-pass.png') });
  await browser.close();
})();
