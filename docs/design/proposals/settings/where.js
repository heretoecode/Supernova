// Proposal (0.156): Settings › Where to Watch in the approved single-panel pattern. Run: FONT_DIR=... node audio.js
const fs = require('fs'), path = require('path'), { chromium } = require('playwright');
const out = __dirname, fontDir = process.env.FONT_DIR;
const font = w => `@font-face{font-family:R;font-weight:${w};src:url(data:font/woff2;base64,${fs.readFileSync(path.join(fontDir, `roboto-latin-${w}-normal.woff2`)).toString('base64')}) format('woff2')}`;
const P = { audio: '<path d="M4 9h4l5-4v14l-5-4H4z"/><path d="M16 9a4 4 0 0 1 0 6"/>', cc: '<rect x="3" y="5" width="18" height="14" rx="2"/><path d="M10 10.2a2.2 2.2 0 1 0 0 3.6M16.5 10.2a2.2 2.2 0 1 0 0 3.6"/>',
  style: '<path d="M4 20h4L19 9l-4-4L4 16z"/>', font: '<path d="M5 19L11 5h2l6 14M8 14h8"/>', chev: '<path d="M9 6l6 6-6 6"/>', check: '<path d="M5.5 12.5l4 4L18.5 8"/>',
  search: '<circle cx="11" cy="11" r="6.5"/><path d="M16 16l4.5 4.5"/>', gear: '<circle cx="12" cy="12" r="3.2"/><path d="M12 2.5v3M12 18.5v3M2.5 12h3M18.5 12h3M5.3 5.3l2.1 2.1M16.6 16.6l2.1 2.1M5.3 18.7l2.1-2.1M16.6 7.4l2.1-2.1"/>',
  folder: '<path d="M3 6.5A1.5 1.5 0 0 1 4.5 5H9l2 2.5h8.5A1.5 1.5 0 0 1 21 9v8.5a1.5 1.5 0 0 1-1.5 1.5h-15A1.5 1.5 0 0 1 3 17.5z"/>', x: '<path d="M6 6l12 12M18 6L6 18"/>' };
const ic = (k, s = 28) => `<svg width="${s}" height="${s}" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round">${P[k]}</svg>`;
const nav = `<div class="nav"><span class="brand">SUPERNOVA</span><span>Home</span><span>Movies</span><span>TV Shows</span><span>Documentaries</span><span class="sp"></span><span>Network &amp; Files</span><span class="ic">${ic('search', 34)}</span><span class="ic on">${ic('gear', 34)}</span><span class="clock">17:40</span></div>`;
// simple flag drawings (stand-ins for the app's flag set)
const flag = c => ({ gb: '<rect width="36" height="24" fill="#1f3a93"/><path d="M0 0L36 24M36 0L0 24" stroke="#fff" stroke-width="5"/><path d="M0 0L36 24M36 0L0 24" stroke="#c8102e" stroke-width="2"/><path d="M18 0v24M0 12h36" stroke="#fff" stroke-width="7"/><path d="M18 0v24M0 12h36" stroke="#c8102e" stroke-width="4"/>',
  fr: '<rect width="12" height="24" fill="#0055a4"/><rect x="12" width="12" height="24" fill="#fff"/><rect x="24" width="12" height="24" fill="#ef4135"/>',
  de: '<rect width="36" height="8" fill="#000"/><rect y="8" width="36" height="8" fill="#dd0000"/><rect y="16" width="36" height="8" fill="#ffce00"/>',
  es: '<rect width="36" height="24" fill="#aa151b"/><rect y="6" width="36" height="12" fill="#f1bf00"/>',
  it: '<rect width="12" height="24" fill="#009246"/><rect x="12" width="12" height="24" fill="#fff"/><rect x="24" width="12" height="24" fill="#ce2b37"/>',
  jp: '<rect width="36" height="24" fill="#fff"/><circle cx="18" cy="12" r="7" fill="#bc002d"/>',
  kr: '<rect width="36" height="24" fill="#fff"/><circle cx="18" cy="12" r="6" fill="#cd2e3a"/><path d="M12 12a6 6 0 0 0 12 0z" fill="#0047a0"/>',
  la: '<rect width="36" height="24" rx="3" fill="#3a3f48"/><text x="18" y="17" font-size="12" font-family="R" font-weight="500" fill="#fff" text-anchor="middle">LA</text>',
  orig: '<rect width="36" height="24" rx="3" fill="#3a3f48"/><text x="18" y="17" font-size="11" font-family="R" font-weight="500" fill="#fff" text-anchor="middle">ORIG</text>' })[c];
const fl = c => `<svg class="fl" width="36" height="24" viewBox="0 0 36 24">${flag(c)}</svg>`;
Object.assign(P, { tv: '<rect x="3" y="5" width="18" height="12" rx="2"/><path d="M8 21h8"/>', search: '<circle cx="11" cy="11" r="6.5"/><path d="M16 16l4.5 4.5"/>' });
const tg = on => `<span class="tg${on ? ' on' : ''}"><i></i></span>`;
const val = v => `<span class="sv">${v}<span class="chev">${ic('chev', 22)}</span></span>`;
const row = (t, d, right, f) => `<div class="r${f ? ' f' : ''}"><div class="rt"><b>${t}</b><span>${d}</span></div>${right}</div>`;
const logo = (n, c) => `<span class="lg" style="background:${c}">${n}</span>`;
const SVC = [['Netflix', '#b20710'], ['Prime Video', '#1a98ff'], ['Disney+', '#113ccf'], ['Apple TV+', '#2b2b2b'], ['NOW', '#00818a'], ['BBC iPlayer', '#e6007e'], ['ITVX', '#102c3c'], ['Channel 4', '#7b3fa0'], ['Paramount+', '#0064ff'], ['MUBI', '#1b1b1b']];
const chosen = ['Netflix', 'Prime Video', 'Disney+', 'BBC iPlayer'];
const page = f => `<div class="one"><div class="ohead"><div><div class="crumb">Settings</div><div class="stitle">Where to Watch</div></div></div><div class="rule"></div><div class="inner">
  ${row('Show where to watch', 'On a title’s Details page, shows which of your services carry it.', tg(true))}
  ${row('Country', 'Services and availability differ by country.', val(`${fl('gb')}<span>United Kingdom</span>`))}
  ${row('Your services', 'The ones you subscribe to.', val(`${SVC.filter(x => chosen.includes(x[0])).map(x => logo(x[0], x[1])).join('')}`), f === 'svc')}
  ${row('Show first', 'Listed first when a title is on several of your services.', val(logo('Netflix', '#b20710')))}
  <div class="more">Availability from JustWatch via TMDB · service list updated automatically (today)</div></div></div>`;
const picker = `<div class="dim"></div><div class="panel"><div class="ptitle">Your services</div>
  <div class="srch">${ic('search', 24)}<span>Search services</span></div>
  ${SVC.map(([n, c], i) => `<div class="opt${i === 1 ? ' f' : ''}"><span class="cb${chosen.includes(n) ? ' on' : ''}">${chosen.includes(n) ? ic('check', 20) : ''}</span>${logo(n, c)}</div>`).join('')}</div>`;
const shots = [
  ['14-where-to-watch.png', page('svc'), 'Where to Watch (was Streaming): one page, no tabs; no Refresh button — the list updates itself'],
  ['15-your-services.png', page('svc') + picker, 'Your services: tick boxes with each service’s logo; search at the top; panel sized to its content'],
];
const css = `${font(400)}${font(500)}*{box-sizing:border-box;margin:0}
body{width:1920px;height:1080px;overflow:hidden;font-family:R;color:#fff;position:relative;background:radial-gradient(ellipse 70% 60% at 70% 75%,#1d4fd8 0%,rgba(29,79,216,0) 70%),linear-gradient(180deg,#05081a 0%,#0a1650 45%,#10248a 100%)}
.nav{position:absolute;left:56px;right:56px;top:28px;height:56px;display:flex;align-items:center;gap:30px;font-size:34px;color:#e8edf8}.nav .brand{font-size:36px;margin-right:40px;letter-spacing:1px}.nav .sp{flex:1}.nav .ic{display:flex}.nav .ic.on{color:#fff;border-bottom:3px solid #fff;padding-bottom:4px}.nav .clock{margin-left:10px}
.one{position:absolute;left:56px;right:56px;top:112px;bottom:40px;border-radius:26px;background:rgba(22,24,28,.97);border:2px solid rgba(255,255,255,.22);padding:22px 28px}
.ohead{display:flex;align-items:center;justify-content:space-between;margin:0 10px 16px}.crumb{font-size:20px;color:#b7bfcc}.stitle{font-size:36px;font-weight:500;margin-top:2px}
.tabs{display:flex;gap:12px}.tb{height:64px;border-radius:20px;background:#2a2d33;color:#c9d3e6;display:flex;align-items:center;gap:10px;padding:0 22px;font-size:23px;font-weight:500}.tb.open{background:#454b55;color:#fff}.tb.f{background:#F4F6FB;color:#0B0D12}
.rule{height:1px;background:rgba(255,255,255,.14);margin:0 6px 8px}.inner{position:relative;height:calc(100% - 120px)}
.r{display:flex;align-items:center;gap:24px;min-height:96px;padding:10px 22px;border-radius:22px;border-bottom:1px solid rgba(255,255,255,.08)}.r.f{box-shadow:inset 0 0 0 3px #fff;border-bottom-color:transparent}
.rt{flex:1;min-width:0}.rt b{display:block;font-size:27px;font-weight:500}.rt span{display:block;font-size:21px;color:#c9d3e6;margin-top:6px}
.sv{display:flex;align-items:center;gap:10px;font-size:24px;color:#dfe5f1;white-space:nowrap}.chev{color:#8f98a8;display:flex}.fl{border-radius:3px;flex:none}
.tg{width:64px;height:34px;border-radius:17px;background:#3a3f48;position:relative;display:inline-block;flex:none}.tg.on{background:#2fb7d8}.tg i{position:absolute;top:4px;left:4px;width:26px;height:26px;border-radius:50%;background:#fff}.tg.on i{left:34px}
.seg{display:inline-flex;background:#1b1d22;border-radius:18px;padding:4px;flex:none}.seg b{font-weight:500;font-size:20px;padding:10px 16px;border-radius:14px;color:#aab3c4;white-space:nowrap}.seg b.on{background:#454b55;color:#fff}.seg.f b.on{background:#F4F6FB;color:#0B0D12}
.ib{display:flex;gap:10px;margin-left:16px}.btn{height:52px;padding:0 18px;border-radius:18px;background:#2a2d33;display:inline-flex;align-items:center;gap:8px;font-size:20px;font-weight:500;white-space:nowrap}.btn.f{background:#F4F6FB;color:#0B0D12}
.lg{display:inline-flex;align-items:center;height:36px;padding:0 12px;border-radius:9px;font-size:18px;font-weight:500;color:#fff;margin-left:8px;white-space:nowrap}
.opt .lg{margin-left:0}.srch{display:flex;align-items:center;gap:12px;height:60px;border-radius:18px;background:#2a2d33;padding:0 18px;font-size:22px;color:#8f98a8;margin:0 0 12px}
.cb{width:32px;height:32px;border-radius:9px;border:2px solid #aab3c4;display:flex;align-items:center;justify-content:center;flex:none}.cb.on{background:#F4F6FB;border-color:#F4F6FB;color:#0B0D12}
.note{margin:18px 22px;font-size:20px;color:#aab3c4}
.more{position:absolute;left:14px;bottom:0;font-size:18px;color:#8f98a8}
.dim{position:absolute;inset:0;background:rgba(0,0,0,.35)}
.panel{position:absolute;right:56px;top:112px;width:max-content;min-width:320px;max-height:928px;border-radius:26px;background:#24272d;border:2px solid rgba(255,255,255,.24);padding:28px;box-shadow:-20px 0 60px rgba(0,0,0,.5)}
.ptitle{font-size:30px;font-weight:500;margin:0 6px 6px}.pdesc{font-size:20px;color:#c9d3e6;margin:0 6px 14px}
.opt{display:flex;align-items:center;gap:16px;height:62px;padding:0 20px;border-radius:20px;font-size:25px;font-weight:500}.opt.f{box-shadow:inset 0 0 0 3px #fff}
.rad{width:32px;height:32px;border-radius:50%;border:2px solid #aab3c4;display:flex;align-items:center;justify-content:center;flex:none}.rad.on{background:#F4F6FB;border-color:#F4F6FB;color:#0B0D12}
.label{position:absolute;right:56px;bottom:8px;font-size:18px;color:#c9d3e6}`;
(async () => { const b = await chromium.launch(); const t = await b.newPage({ viewport: { width: 1920, height: 1080 } });
  for (const [f, body, label] of shots) {
    await t.setContent(`<html><head><style>${css}</style></head><body>${nav}${body}<div class="label">${label}</div></body></html>`, { waitUntil: 'load' });
    await t.evaluate(() => document.fonts.ready);
    const issues = await t.evaluate(() => { const o = [], r = e => e.getBoundingClientRect(); const mo = document.querySelector('.more');
      document.querySelectorAll('.inner .r, .note').forEach(e => { if (r(e).bottom > r(mo).top - 4) o.push('hits footer: ' + e.textContent.slice(0, 30)); });
      document.querySelectorAll('.inner .r').forEach(e => { const a = e.querySelector('.rt'), z = e.children[1]; if (z && r(a).right > r(z).left) o.push('text hits value: ' + e.textContent.slice(0, 30)); });
      const p = document.querySelector('.panel'); if (p) p.querySelectorAll('.opt').forEach(e => { if (r(e).bottom > r(p).bottom) o.push('picker overflow'); });
      return o; });
    if (issues.length) console.log(f, issues);
    await t.screenshot({ path: path.join(out, f) }); }
  await b.close(); })();
