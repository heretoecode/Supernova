// Proposal (0.155, owner 4 Nov 2026): Home row controls as buttons to the left of the row (like the Shield home
// screen) instead of a Move / Hide pop-up. Left on the first card reveals Hide and Move; Move then moves the row
// live with up/down. Run: FONT_DIR=<roboto woff2 dir> node render.js
const fs = require('fs');
const path = require('path');
const { chromium } = require('playwright');
const out = __dirname;
const fontDir = process.env.FONT_DIR;
const font = w => `@font-face{font-family:R;font-weight:${w};src:url(data:font/woff2;base64,${fs.readFileSync(path.join(fontDir, `roboto-latin-${w}-normal.woff2`)).toString('base64')}) format('woff2')}`;
const P = {
  minus: '<circle cx="12" cy="12" r="9"/><path d="M8 12h8"/>', hide: '<path d="M3 3l18 18"/><path d="M10.6 5.2A10 10 0 0 1 12 5c6 0 9.5 7 9.5 7a17 17 0 0 1-3 3.8M6.6 6.6A17 17 0 0 0 2.5 12S6 19 12 19a9.6 9.6 0 0 0 4.4-1"/><path d="M9.9 9.9a3 3 0 0 0 4.2 4.2"/>',
  move: '<path d="M12 3v18M8 7l4-4 4 4M8 17l4 4 4-4"/>', up: '<path d="M7 14l5-5 5 5"/>', down: '<path d="M7 10l5 5 5-5"/>',
  hist: '<path d="M3.5 12a8.5 8.5 0 1 0 2.5-6"/><path d="M3 4v4h4M12 8v4l3 2"/>', cal: '<rect x="4" y="5" width="16" height="15" rx="2"/><path d="M4 10h16M9 3v4M15 3v4"/>',
  next: '<path d="M9 6l6 6-6 6"/>', info: '<circle cx="12" cy="12" r="9"/><path d="M12 11v6M12 7.5v.5"/>',
};
const ic = (k, s = 32, w = 1.8) => `<svg width="${s}" height="${s}" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="${w}" stroke-linecap="round" stroke-linejoin="round">${P[k]}</svg>`;
const nav = `<div class="nav"><span class="brand">SUPERNOVA</span><span class="on">Home</span><span>Movies</span><span>TV Shows</span><span>Documentaries</span><span class="sp"></span><span>Network &amp; Files</span><span class="clock">12:52</span></div>`;
const hero = `<div class="hero"><div class="hcopy"><div class="logo">THE HERO TITLE</div><div class="meta">2026 · Romance, Fantasy, Comedy</div><div class="more">${ic('info', 30)}More Info</div></div></div>`;
const cards = {
  continue: [['Nocturne', 'Resume · S1 E1', .15, '#33506f'], ['The Night Manager', 'Resume · S2 E3', .35, '#4a3a2a'], ['MobLand', 'Resume · S2 E7', .1, '#3e4f45'], ['Slow Horses', 'Resume · S5 E2', .6, '#5a4a36'], ['Andor', 'Resume · S2 E9', .8, '#2b3550']],
  recent: [['Bugonia', '2025 · Film', 0, '#566233'], ['Civil War', '2024 · Film', 0, '#6b3a2a'], ['Blitz', '2024 · Film', 0, '#5c2a1c'], ['Coneheads', '1993 · Film', 0, '#2f5a6b'], ['Aftersun', '2022 · Film', 0, '#7a6a52']],
  next: [['Kingdom', 'Up next · S2 E1', 0, '#3b2f28'], ['Mr. Robot', 'Up next · S3 E4', 0, '#2a2a2f'], ['Mayday', 'Film', 0, '#3e5470'], ['Tulsa King', 'Up next · S3 E1', 0, '#5b4630'], ['War', 'Film', 0, '#40403a']],
};
const rowDefs = { continue: ['hist', 'Continue Watching'], recent: ['cal', 'Recently Added'], next: ['next', 'Watch Next'] };
const card = ([t, s, p, c], focus) => `<div class="card${focus ? ' focus' : ''}" style="background:linear-gradient(160deg,${c},#0d1017 85%)"><div class="ct">${t}</div><div class="cs">${s}</div>${p ? `<div class="pb"><i style="width:${p * 100}%"></i></div>` : ''}</div>`;
// mode: 'hide' (Hide focused) | 'move' (Move focused) | 'moving' (row being moved, now in slot 2)
const controls = mode => mode === 'moving'
  ? `<div class="ctl"><div class="rb big on moving">${ic('up', 30, 2.2)}${ic('move', 34, 2)}${ic('down', 30, 2.2)}</div><div class="rl">Moving · row 2 of 7</div></div>`
  : `<div class="ctl"><div class="rbs"><div class="rb ${mode === 'hide' ? 'big on' : 'small'}">${ic('hide', mode === 'hide' ? 40 : 26)}</div><div class="rb ${mode === 'move' ? 'big on' : 'small'}">${ic('move', mode === 'move' ? 40 : 26)}</div></div><div class="rl">${mode === 'hide' ? 'Hide row' : 'Move row'}</div></div>`;
const row = (id, mode) => {
  const [icon, name] = rowDefs[id];
  const active = mode && id === 'continue';
  return `<div class="row${active ? ' active' : ''}${active && mode === 'moving' ? ' lifted' : ''}"><div class="rh">${ic(icon, 30)}${name}${active && mode === 'moving' ? '<span class="hint">↑ ↓ to move · OK or Back when done</span>' : ''}</div>
    <div class="rc">${active ? controls(mode) : ''}<div class="cards">${cards[id].map((c, i) => card(c, false)).join('')}</div></div></div>`;
};
const shots = [
  ['01-left-reveals-hide.png', ['continue', 'recent', 'next'], 'hide', 'Left on the first card: the row slides right and two round buttons appear — Hide row is focused'],
  ['02-move-focused.png', ['continue', 'recent', 'next'], 'move', 'Down (or right) moves to Move row; Up goes back to Hide; Right returns to the first card'],
  ['03-moving-live.png', ['recent', 'continue', 'next'], 'moving', 'OK on Move: the row lifts; Up/Down moves it live (here it is now row 2); OK or Back drops it'],
];
const css = `${font(400)}${font(500)}
*{box-sizing:border-box;margin:0}
body{width:1920px;height:1080px;overflow:hidden;font-family:R;color:#fff;position:relative;background:linear-gradient(180deg,#05081a 0%,#0a1650 50%,#10248a 100%)}
.nav{position:absolute;left:56px;right:56px;top:28px;height:56px;display:flex;align-items:center;gap:30px;font-size:34px;color:#e8edf8;z-index:3}
.nav .brand{font-size:36px;margin-right:40px;letter-spacing:1px}.nav .sp{flex:1}.nav .on{font-weight:500}.nav .clock{margin-left:30px}
.hero{position:absolute;left:56px;right:56px;top:110px;height:300px;border-radius:26px;background:linear-gradient(90deg,#0c0f16 30%,rgba(12,15,22,.2)),linear-gradient(140deg,#6d5a4a,#22283a);overflow:hidden}
.hcopy{position:absolute;left:44px;top:40px}.logo{font-family:Georgia,serif;font-size:56px;color:#f0dfc0}.meta{font-size:24px;margin-top:14px;color:#e8edf8}
.more{display:inline-flex;gap:10px;align-items:center;font-size:24px;font-weight:500;margin-top:30px}
.rows{position:absolute;left:56px;right:0;top:440px}
.row{margin-bottom:26px;transition:none;border-radius:26px;padding:6px 0 10px}
.row.lifted{background:rgba(255,255,255,.07);box-shadow:0 0 0 3px #fff, 0 20px 50px rgba(0,0,0,.5);margin-left:-16px;padding-left:16px;margin-right:56px}
.rh{display:flex;align-items:center;gap:14px;font-size:30px;font-weight:500;height:46px;margin-bottom:12px}
.hint{margin-left:20px;font-size:21px;font-weight:400;color:#c9d3e6}
.rc{display:flex;align-items:center;gap:26px}
.cards{display:flex;gap:22px}
.card{width:360px;height:180px;border-radius:18px;position:relative;flex:none;padding:16px;display:flex;flex-direction:column;justify-content:flex-end}
.ct{font-size:24px;font-weight:500}.cs{font-size:18px;color:#dfe5f1;margin-top:4px}
.pb{height:6px;border-radius:3px;background:rgba(255,255,255,.25);margin-top:10px;overflow:hidden}.pb i{display:block;height:100%;background:#F4F6FB}
.ctl{width:210px;flex:none;display:flex;flex-direction:column;align-items:center;gap:12px}
.rbs{display:flex;flex-direction:column;align-items:center;gap:14px}
.rb{border-radius:50%;display:flex;align-items:center;justify-content:center;flex-direction:column}
.rb.small{width:52px;height:52px;background:rgba(42,45,51,.95);color:#e8edf8}
.rb.big{width:112px;height:112px;background:#F4F6FB;color:#0B0D12}
.rb.moving{width:112px;height:150px;border-radius:56px;gap:0}
.rl{font-size:21px;font-weight:500;color:#F4F6FB;white-space:nowrap}
.label{position:absolute;right:56px;bottom:12px;font-size:19px;color:#c9d3e6;background:rgba(5,8,26,.7);padding:4px 10px;border-radius:8px}`;
(async () => {
  const browser = await chromium.launch();
  const tab = await browser.newPage({ viewport: { width: 1920, height: 1080 } });
  for (const [f, order, mode, label] of shots) {
    await tab.setContent(`<html><head><style>${css}</style></head><body>${nav}${hero}<div class="rows">${order.map(id => row(id, mode)).join('')}</div><div class="label">${label}</div></body></html>`, { waitUntil: 'load' });
    await tab.evaluate(() => document.fonts.ready);
    const issues = await tab.evaluate(() => {
      const o = [], r = e => e.getBoundingClientRect();
      const c = document.querySelector('.ctl'), k = document.querySelector('.row.active .card');
      if (c && k && r(c).right > r(k).left) o.push('controls overlap first card');
      if (c && r(c).bottom > r(c.closest('.row')).bottom) o.push('controls taller than row');
      return o;
    });
    if (issues.length) console.log(f, issues);
    await tab.screenshot({ path: path.join(out, f) });
  }
  await browser.close();
})();
