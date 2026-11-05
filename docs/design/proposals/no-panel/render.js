// Owner request (5 Nov 2026): Library Health with no panel; the panel colour fills the whole page.
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
  health: '<path d="M3 12h4l2-5 4 10 2-5h6"/>',
  scan: '<path d="M4 12a8 8 0 0 1 14-5.3M20 12a8 8 0 0 1-14 5.3"/><path d="M18 3v4h-4M6 21v-4h4"/>',
  clock: '<circle cx="12" cy="12" r="9"/><path d="M12 7v5l3 2"/>',
  drive: '<path d="M6 4h12l2 11H4z"/><path d="M4 15h16v5H4zM8 17.5h.1"/>',
  warn: '<path d="M12 3l10 18H2z"/><path d="M12 10v5M12 18v.1"/>',
  trash: '<path d="M4 7h16M9 7V4h6v3M6 7l1 13h10l1-13"/>',
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


const val = t => `<span class="val">${t}</span>`;
const sw = on => `<span class="tg${on ? ' on' : ''}"><i></i></span>`;
const ok = `<span class="st ok">${ic('check', 26)}</span>`, warnMark = `<span class="st wa">${ic('warn', 26)}</span>`;
const scanning = () => `${nav}<div class="sheet">${head('scan', `${chip('Network & Files')}`, 'Library Scanning',
  pill('Last full scan 4 hours ago') + pill('1,808 videos') + pill('Put.io part not finishing', 'warn'))}
  ${heading('Schedule')}
  ${row('clock', 'Scan automatically', 'Checks your drives and Put.io for new and removed videos', { focus: true, right: val('‹  Every hour  ›') })}
  ${row('scan', 'Also scan when Supernova opens', 'Only if the last scan was more than an hour ago', { right: sw(true) })}
  ${heading('What gets scanned')}
  ${row('drive', 'Backup #1 · Backup #2', 'USB drives · library folders you added in Local Storage', { mark: ok })}
  ${row('cloud', 'Put.io · Films · TV', 'Library sync through your Put.io account', { mark: ok })}
  ${heading('Recent scans')}
  ${row('drive', 'Today 21:44 · USB drives', 'Finished in 2 min · nothing new', { mark: ok })}
  ${row('cloud', 'Today 20:14 · Put.io', "Didn't finish: waiting on Put.io for 90 min · Scan now retries it", { mark: warnMark })}</div>`;
const health = () => `${nav}<div class="sheet">${head('health', `${chip('Network & Files')}`, 'Library Health', pill('2 to review', 'warn') + pill('1,808 videos checked'))}
  ${row('warn', 'Missing files', '2 videos in your library whose file has gone', { focus: true, right: val('2  ›') })}
  ${row('video', 'Not matched', 'Videos Supernova could not identify', { mark: ok })}
  ${row('video', 'Possibly wrong match', 'Matches with a low confidence score', { mark: ok })}
  ${row('video', 'Missing artwork or details', 'Matched, but without a poster or description', { mark: ok })}
  ${row('cloud', 'Sources offline', 'Drives or shares that could not be reached', { mark: ok })}</div>`;
const missing = () => `${nav}<div class="sheet">${head('warn', `${chip('Network & Files')}<b>›</b>${chip('Library Health')}`, 'Missing files', pill('2 videos') + pill('Watched state and progress are kept'))}
  ${row('video', 'Life Is Beautiful (1997)', 'Put.io › Films · gone since 2 Nov · was "Life Is Beautiful 1997 1080p BDRip…"', { focus: true,
     buttons: `<span class="ibtns">${btn('search', 'Find it again')}${btn('trash', 'Remove')}</span>` })}
  ${row('video', 'Tony (2026)', 'Put.io › Films · gone since 2 Nov · was "Tony.2026.1080p.WEBRip…"')}</div>`;
const labels = {
  '05-library-scanning.png': 'Library Scanning: schedule, what gets scanned, recent scans in plain words; the Scan now tile does the scanning',
  '06-library-health.png': 'Library Health: each check with its count; only problems open a list',
  '07-library-health-missing.png': 'Missing files: titles not addresses; Find it again or Remove; history is kept',
};
const css = `${font(400)}${font(500)}
*{box-sizing:border-box;margin:0}
body{width:1920px;height:1080px;overflow:hidden;font-family:R;color:#fff;position:relative;
  background:#16181C}
.nav{position:absolute;left:56px;right:56px;top:28px;height:56px;display:flex;align-items:center;gap:30px;font-size:34px;color:#e8edf8}
.nav .brand{font-size:36px;margin-right:40px;letter-spacing:1px}.nav .sp{flex:1}.nav .on{font-weight:500}.nav .clock{margin-left:30px}
.sheet{position:absolute;left:56px;right:56px;top:112px;bottom:40px;padding:22px 0;overflow:hidden}
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
.val{font-size:23px;color:#C9D3E6;white-space:nowrap;margin-right:6px}.st.wa{color:#f0b44c}
.tg{width:68px;height:36px;border-radius:18px;background:#3a3f48;position:relative;display:inline-block;flex:none;margin-right:8px}.tg.on{background:#2fb7d8}.tg i{position:absolute;top:4px;left:4px;width:28px;height:28px;border-radius:50%;background:#fff}.tg.on i{left:36px}
.row{height:92px}
.label{position:absolute;right:56px;bottom:8px;font-size:19px;color:#c9d3e6}`;
const pages = [['01-library-health-no-panel.png', health()]];
(async () => {
  const browser = await chromium.launch();
  const tab = await browser.newPage({ viewport: { width: 1920, height: 1080 } });
  for (const [f, body] of pages) {
    await tab.setContent(`<html><head><style>${css}</style></head><body>${body}</body></html>`, { waitUntil: 'load' });
    await tab.evaluate(() => document.fonts.ready);
    await tab.screenshot({ path: path.join(out, f) });
  }
  await browser.close();
})();
