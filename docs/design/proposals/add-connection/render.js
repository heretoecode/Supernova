// Proposal (walk-through after 0.154): Add Connection — plain-English types, a real form, Test before Save.
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
  computer: '<rect x="3" y="4" width="18" height="12" rx="1.5"/><path d="M8 20h8M12 16v4"/>',
  globe: '<circle cx="12" cy="12" r="9"/><path d="M3 12h18M12 3a14 14 0 0 1 0 18M12 3a14 14 0 0 0 0 18"/>',
  lock: '<rect x="5" y="11" width="14" height="10" rx="2"/><path d="M8 11V8a4 4 0 0 1 8 0v3"/>',
  more: '<circle cx="6" cy="12" r="1.2" fill="currentColor"/><circle cx="12" cy="12" r="1.2" fill="currentColor"/><circle cx="18" cy="12" r="1.2" fill="currentColor"/>',
  eye: '<path d="M2 12s3.5-7 10-7 10 7 10 7-3.5 7-10 7S2 12 2 12z"/><circle cx="12" cy="12" r="3"/>',
  warn: '<path d="M12 3l10 18H2z"/><path d="M12 10v5M12 18v.1"/>',
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


const types = focus => `${nav}<div class="sheet">${head('plus', `${chip('Network & Files')}`, 'Add a connection', pill('Choose what you are connecting to'))}
  ${heading('Recommended for you')}
  ${row('cloud', 'Put.io', 'Sign in with your Put.io account · browsing, library sync and search in one', { focus: focus === 0, mark: '<span class="tag">Already connected</span>' })}
  ${heading('On your home network')}
  ${row('computer', 'A computer or NAS', 'Windows, Mac, Linux or a NAS (Synology, QNAP…) · Supernova finds them for you', { focus: focus === 1,
     buttons: focus === 1 ? `<span class="ibtns">${btn('search', 'Find on my network')}${btn('plus', 'Enter address')}</span>` : '' })}
  ${heading('On the internet')}
  ${row('globe', 'A WebDAV server', 'Cloud storage and servers that offer WebDAV (secure HTTPS)')}
  ${row('lock', 'An SFTP server', 'A server you reach over SSH')}
  ${row('more', 'More types', 'FTP, FTP with TLS, and plain (unencrypted) WebDAV')}</div>`;
const field = (label, value, opts = {}) => `<div class="field${opts.focus ? ' focus' : ''}${opts.err ? ' err' : ''}"><span class="fl">${label}</span>
  <span class="fv${opts.ph ? ' ph' : ''}">${value}</span>${opts.right || ''}</div>`;
const form = state => `${nav}<div class="sheet">${head('globe', `${chip('Network & Files')}<b>›</b>${chip('Add a connection')}`, 'WebDAV server',
  pill('Secure (HTTPS)', 'ok') + pill('Saved on this Shield only'))}
  <div class="formgrid"><div class="fields">
  ${field('Name', 'Put.io Films', { focus: false })}
  ${field('Address', state === 'error' ? 'webdav.put.oi' : 'webdav.put.io', { err: state === 'error', focus: state === 'error' })}
  ${field('Port', '443 · default for HTTPS', { ph: true })}
  ${field('Folder', '/Films', {})}
  ${field('Username', 'heretostream', {})}
  ${field('Password', '••••••••••••', { right: `<span class="eye">${ic('eye', 26)}</span>` })}
  <div class="sw">Remember password <span class="tg on"><i></i></span></div>
  <div class="actions">${btn('check', 'Test connection', state === 'form')}${btn('open', 'Save and browse', state === 'ok')}</div></div>
  <div class="help">${state === 'form' ? `<div class="ht">Test connection</div><div class="hb">Checks the address and password without saving anything.</div>
     <div class="ht" style="margin-top:28px">Tips</div><div class="hb">Port and Folder are optional. For Put.io use <b>webdav.put.io</b> and your Put.io username.</div>`
   : state === 'ok' ? `<div class="res ok">${ic('check', 40)}<div><div class="ht">Connected</div><div class="hb">webdav.put.io answered in 0.4 s · 2 folders, 21 videos found.</div></div></div>
     <div class="hb" style="margin-top:22px">Next: <b>Save and browse</b> opens it like Local Storage, where you can add folders to your library.</div>`
   : `<div class="res bad">${ic('warn', 40)}<div><div class="ht">Couldn't find that server</div><div class="hb">No server called <b>webdav.put.oi</b> exists. Check the spelling of the address.</div></div></div>
     <div class="hb" style="margin-top:22px">Nothing was saved. Fix the address, then <b>Test connection</b> again.</div>`}</div></div></div>`;
const labels = {
  '01-choose-type.png': 'Step 1: choose what you are connecting to, in plain English; focused row shows its buttons',
  '02-webdav-form.png': 'Step 2: real fields (label + value), defaults shown, Test before Save; help on the right',
  '03-test-ok.png': 'Test passed: what was found; Save and browse focused',
  '04-test-error.png': 'Test failed: the reason in plain English, the wrong field marked, nothing saved',
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
.tag{font-size:19px;color:#5ad18a;white-space:nowrap;margin-right:8px}
.formgrid{display:flex;gap:40px;padding:6px 10px}.fields{flex:1.1;display:flex;flex-direction:column;gap:12px}.help{flex:.9;padding-top:6px}
.field{display:flex;align-items:center;height:70px;padding:0 22px;border-radius:18px;background:#22252b;border:2px solid transparent;font-size:24px}
.field.focus{border-color:#fff}.field.err{border-color:#f0b44c}.fl{width:170px;color:#b7bfcc;font-size:21px}.fv{flex:1}.fv.ph{color:#8a93a3}.eye{color:#c9d3e6}
.sw{display:flex;align-items:center;justify-content:space-between;height:60px;padding:0 22px;font-size:23px}
.tg{width:68px;height:36px;border-radius:18px;background:#3a3f48;position:relative;display:inline-block}.tg.on{background:#2fb7d8}.tg i{position:absolute;top:4px;left:4px;width:28px;height:28px;border-radius:50%;background:#fff}.tg.on i{left:36px}
.actions{display:flex;gap:16px;margin-top:6px}
.ht{font-size:26px;font-weight:500}.hb{font-size:22px;color:#C9D3E6;line-height:1.45;margin-top:8px}.hb b{color:#fff;font-weight:500}
.res{display:flex;gap:18px;align-items:flex-start;padding:22px;border-radius:20px;background:#22252b}.res.ok{color:#5ad18a}.res.bad{color:#f0b44c}.res .ht{color:#fff}
.label{position:absolute;right:56px;bottom:8px;font-size:19px;color:#c9d3e6}`;
const pages = [['01-choose-type.png', types(1)], ['02-webdav-form.png', form('form')], ['03-test-ok.png', form('ok')], ['04-test-error.png', form('error')]];
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
