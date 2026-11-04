// Proposal (0.156, owner 4 Nov 2026): Settings › Playback rebuilt from SETTINGS_REVIEW.md — standard buttons on the left,
// one aligned value/switch per row, plain one-line explanations, solid choice list. Run: FONT_DIR=... node render.js
const fs = require('fs'), path = require('path'), { chromium } = require('playwright');
const out = __dirname, fontDir = process.env.FONT_DIR;
const font = w => `@font-face{font-family:R;font-weight:${w};src:url(data:font/woff2;base64,${fs.readFileSync(path.join(fontDir, `roboto-latin-${w}-normal.woff2`)).toString('base64')}) format('woff2')}`;
const P = { play: '<path d="M8 5l11 7-11 7z"/>', resume: '<path d="M3.5 12a8.5 8.5 0 1 0 2.5-6"/><path d="M3 4v4h4M12 8v4l3 2"/>',
  skip: '<path d="M5 5l9 7-9 7z"/><path d="M18 5v14"/>', pic: '<rect x="3" y="5" width="18" height="13" rx="2"/><path d="M8 21h8"/>',
  check: '<path d="M5.5 12.5l4 4L18.5 8"/>', chev: '<path d="M9 6l6 6-6 6"/>', search: '<circle cx="11" cy="11" r="6.5"/><path d="M16 16l4.5 4.5"/>',
  gear: '<circle cx="12" cy="12" r="3.2"/><path d="M12 2.5v3M12 18.5v3M2.5 12h3M18.5 12h3M5.3 5.3l2.1 2.1M16.6 16.6l2.1 2.1M5.3 18.7l2.1-2.1M16.6 7.4l2.1-2.1"/>' };
const ic = (k, s = 28) => `<svg width="${s}" height="${s}" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round">${P[k]}</svg>`;
const nav = `<div class="nav"><span class="brand">SUPERNOVA</span><span>Home</span><span>Movies</span><span>TV Shows</span><span>Documentaries</span><span class="sp"></span><span>Network &amp; Files</span><span class="ic">${ic('search', 34)}</span><span class="ic on">${ic('gear', 34)}</span><span class="clock">17:05</span></div>`;
const side = focus => `<div class="side"><div class="crumb">Settings</div><div class="stitle">Playback</div>
  ${[['play', 'Playback'], ['resume', 'Resume'], ['skip', 'Skipping'], ['pic', 'Picture']].map(([k, t], i) => `<div class="sb${i === 0 ? ' open' : ''}${focus === i ? ' f' : ''}">${ic(k, 28)}<span>${t}</span></div>`).join('')}</div>`;
const tg = on => `<span class="tg${on ? ' on' : ''}"><i></i></span>`;
const val = v => `<span class="sv">${v}<span class="chev">${ic('chev', 22)}</span></span>`;
const row = (t, d, right, focus) => `<div class="r${focus ? ' f' : ''}"><div class="rt"><b>${t}</b><span>${d}</span></div>${right}</div>`;
const page = focus => `<div class="main"><div class="grp">Playback</div>
  ${row('Default playback speed', 'Speed new videos start at. You can still change it while watching.', val('1.0×'), focus === 'speed')}
  ${row('Play next episode automatically', 'Starts the next episode when one finishes.', tg(true))}
  ${row('Ask “Still watching?”', 'After three episodes in a row with no button pressed.', val('After 3 episodes'))}
  ${row('Sleep timer', 'Default for the timer in the player menu.', val('Off'))}
  ${row('Hide controls when paused', 'Shows the paused picture without the controls on top.', tg(false))}
  ${row('Open videos in another player', 'Offers your other video apps when you press Play.', tg(false))}
  <div class="more">Resume, Skipping and Picture are the next pages on the left</div></div>`;
const speeds = ['0.5×', '0.75×', '1.0×', '1.25×', '1.5×', '2.0×'];
const choice = `<div class="dim"></div><div class="panel"><div class="ptitle">Default playback speed</div><div class="pdesc">Speed new videos start at.</div>
  ${speeds.map((s, i) => `<div class="opt${i === 2 ? ' f' : ''}"><span class="rad${i === 2 ? ' on' : ''}">${i === 2 ? ic('check', 20) : ''}</span>${s}${i === 2 ? '<em>Normal</em>' : ''}</div>`).join('')}
  <div class="pfoot">OK chooses · Back closes</div></div>`;
const tabs = focus => `<div class="tabs">${[['play', 'Playback'], ['resume', 'Resume'], ['skip', 'Skipping'], ['pic', 'Picture']].map(([k, t], i) => `<div class="tb${i === 0 ? ' open' : ''}${focus === i ? ' f' : ''}">${ic(k, 26)}<span>${t}</span></div>`).join('')}</div>`;
const one = (focus, tabFocus) => `<div class="one"><div class="ohead"><div><div class="crumb">Settings</div><div class="stitle">Playback</div></div>${tabs(tabFocus)}</div><div class="rule"></div>
  ${page(focus).replace('<div class="main">', '<div class="inner">').replace('Resume, Skipping and Picture are the next pages on the left', 'Up from the list reaches the tabs · Left/Right switches tab')}</div>`;
const shots = [
  ['03-single-panel.png', one('speed', -1), 'One panel: title and section tabs across the top, the rows below — no second panel'],
  ['04-single-panel-tabs.png', one('', 1), 'Up from the rows reaches the tabs; Left/Right moves between Playback · Resume · Skipping · Picture'],
  ['01-playback.png', side(-1) + page('speed'), 'Settings › Playback: standard buttons on the left; each row says what it does in one line; one value or switch per row, level with its title'],
  ['02-choice.png', side(-1) + page('speed') + choice, 'Choosing a value: a solid side panel (no see-through pop-up); the current choice is ticked'],
];
const css = `${font(400)}${font(500)}*{box-sizing:border-box;margin:0}
body{width:1920px;height:1080px;overflow:hidden;font-family:R;color:#fff;position:relative;background:radial-gradient(ellipse 70% 60% at 70% 75%,#1d4fd8 0%,rgba(29,79,216,0) 70%),linear-gradient(180deg,#05081a 0%,#0a1650 45%,#10248a 100%)}
.nav{position:absolute;left:56px;right:56px;top:28px;height:56px;display:flex;align-items:center;gap:30px;font-size:34px;color:#e8edf8}.nav .brand{font-size:36px;margin-right:40px;letter-spacing:1px}.nav .sp{flex:1}.nav .ic{display:flex}.nav .ic.on{color:#fff;border-bottom:3px solid #fff;padding-bottom:4px}.nav .clock{margin-left:10px}
.side{position:absolute;left:56px;top:112px;width:420px;bottom:40px;border-radius:26px;background:rgba(22,24,28,.97);border:2px solid rgba(255,255,255,.22);padding:26px 20px}
.crumb{font-size:20px;color:#b7bfcc;margin:0 10px}.stitle{font-size:36px;font-weight:500;margin:4px 10px 24px}
.sb{position:relative;height:76px;border-radius:22px;background:#2a2d33;display:flex;align-items:center;gap:16px;padding:0 22px;font-size:25px;font-weight:500;margin-bottom:12px}
.sb.open::before{content:"";position:absolute;left:-12px;top:20px;bottom:20px;width:5px;border-radius:3px;background:#fff}.sb.f{background:#F4F6FB;color:#0B0D12}
.main{position:absolute;left:500px;right:56px;top:112px;bottom:40px;border-radius:26px;background:rgba(22,24,28,.97);border:2px solid rgba(255,255,255,.22);padding:24px 28px}
.grp{font-size:22px;font-weight:500;color:#b7bfcc;margin:2px 14px 10px}
.r{display:flex;align-items:center;gap:24px;min-height:104px;padding:10px 22px;border-radius:22px;border-bottom:1px solid rgba(255,255,255,.08)}.r.f{box-shadow:inset 0 0 0 3px #fff;border-bottom-color:transparent}
.rt{flex:1;min-width:0}.rt b{display:block;font-size:27px;font-weight:500}.rt span{display:block;font-size:21px;color:#c9d3e6;margin-top:6px}
.sv{display:flex;align-items:center;gap:8px;font-size:24px;color:#dfe5f1;white-space:nowrap}.chev{color:#8f98a8;display:flex}
.tg{width:64px;height:34px;border-radius:17px;background:#3a3f48;position:relative;display:inline-block;flex:none}.tg.on{background:#2fb7d8}.tg i{position:absolute;top:4px;left:4px;width:26px;height:26px;border-radius:50%;background:#fff}.tg.on i{left:34px}
.more{position:absolute;left:36px;bottom:22px;font-size:18px;color:#8f98a8}
.dim{position:absolute;inset:0;background:rgba(0,0,0,.35)}
.panel{position:absolute;right:56px;top:112px;bottom:40px;width:600px;border-radius:26px;background:#24272d;border:2px solid rgba(255,255,255,.24);padding:28px;box-shadow:-20px 0 60px rgba(0,0,0,.5)}
.ptitle{font-size:32px;font-weight:500;margin:0 6px 6px}.pdesc{font-size:21px;color:#c9d3e6;margin:0 6px 18px}
.opt{display:flex;align-items:center;gap:16px;height:76px;padding:0 20px;border-radius:20px;font-size:26px;font-weight:500}.opt.f{box-shadow:inset 0 0 0 3px #fff}.opt em{font-style:normal;font-size:20px;color:#c9d3e6;font-weight:400;margin-left:auto}
.rad{width:34px;height:34px;border-radius:50%;border:2px solid #aab3c4;display:flex;align-items:center;justify-content:center}.rad.on{background:#F4F6FB;border-color:#F4F6FB;color:#0B0D12}
.pfoot{position:absolute;left:28px;bottom:22px;font-size:18px;color:#8f98a8}
.one{position:absolute;left:56px;right:56px;top:112px;bottom:40px;border-radius:26px;background:rgba(22,24,28,.97);border:2px solid rgba(255,255,255,.22);padding:22px 28px}
.ohead{display:flex;align-items:center;justify-content:space-between;margin:0 10px 16px}.one .crumb{margin:0}.one .stitle{margin:2px 0 0}
.tabs{display:flex;gap:12px}.tb{position:relative;height:64px;border-radius:20px;background:#2a2d33;display:flex;align-items:center;gap:10px;padding:0 22px;font-size:23px;font-weight:500}
.tb.open::after{content:"";position:absolute;left:22px;right:22px;bottom:-12px;height:4px;border-radius:2px;background:#fff}.tb.f{background:#F4F6FB;color:#0B0D12}
.rule{height:1px;background:rgba(255,255,255,.14);margin:0 6px 8px}
.inner{position:relative;height:calc(100% - 120px)}.inner .more{left:14px;bottom:0}
.label{position:absolute;right:56px;bottom:8px;font-size:18px;color:#c9d3e6}`;
(async () => { const b = await chromium.launch(); const t = await b.newPage({ viewport: { width: 1920, height: 1080 } });
  for (const [f, body, label] of shots) {
    await t.setContent(`<html><head><style>${css}</style></head><body>${nav}${body}<div class="label">${label}</div></body></html>`, { waitUntil: 'load' });
    await t.evaluate(() => document.fonts.ready);
    const issues = await t.evaluate(() => { const o = [], r = e => e.getBoundingClientRect(); const mo = document.querySelector('.more');
      document.querySelectorAll('.main .r, .inner .r').forEach(e => { if (r(e).bottom > r(mo).top - 4) o.push('rows hit footer: ' + e.textContent.slice(0, 30)); const t2 = e.querySelector('.rt'), s = e.lastElementChild; if (t2 !== s && r(t2).right > r(s).left) o.push('text hits value'); });
      return o; });
    if (issues.length) console.log(f, issues);
    await t.screenshot({ path: path.join(out, f) }); }
  await b.close(); })();
