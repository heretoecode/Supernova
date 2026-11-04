// Proposal (0.156): subtitle Vertical Position as a step control with live preview. Run: FONT_DIR=<roboto woff2 dir> node render.js
const fs = require('fs'), path = require('path'), { chromium } = require('playwright');
const out = __dirname, fontDir = process.env.FONT_DIR;
const font = w => `@font-face{font-family:R;font-weight:${w};src:url(data:font/woff2;base64,${fs.readFileSync(path.join(fontDir, `roboto-latin-${w}-normal.woff2`)).toString('base64')}) format('woff2')}`;
const P = { cc: '<rect x="3" y="5" width="18" height="14" rx="2"/><path d="M10 10.2a2.2 2.2 0 1 0 0 3.6M16.5 10.2a2.2 2.2 0 1 0 0 3.6"/>',
  audio: '<path d="M4 9h4l5-4v14l-5-4H4z"/><path d="M16 9a4 4 0 0 1 0 6"/>', play: '<path d="M8 5v14l11-7z"/>',
  chap: '<path d="M4 6h16M4 12h16M4 18h10"/><path d="M18 16l3 2-3 2z"/>', speed: '<path d="M5 18a8 8 0 1 1 14 0"/><path d="M12 13l4-4"/>',
  ratio: '<rect x="3" y="6" width="18" height="12" rx="2"/><path d="M7 10v4M17 10v4"/>', bell: '<path d="M6 16V11a6 6 0 0 1 12 0v5l2 2H4z"/><path d="M10 20a2 2 0 0 0 4 0"/>', moon: '<path d="M19 14.5A7.5 7.5 0 0 1 9.5 5a7.5 7.5 0 1 0 9.5 9.5z"/>',
  up: '<path d="M6 15l6-6 6 6"/>', down: '<path d="M6 9l6 6 6-6"/>', size: '<path d="M4 18L9 6h1l5 12M6 14h7M16 18l2.5-6h.5l2.5 6"/>', vpos: '<path d="M12 4v16M8 8l4-4 4 4M8 16l4 4 4-4"/>', outline: '<rect x="4" y="4" width="16" height="16" rx="2" stroke-dasharray="2 2"/><path d="M9 16l3-8 3 8M10 13h4"/>', bg: '<rect x="4" y="4" width="16" height="16" rx="2"/><path d="M4 12l8-8M4 20L20 4M12 20l8-8"/>', chev: '<path d="M9 6l6 6-6 6"/>', back: '<path d="M15 6l-6 6 6 6"/>', now: '<path d="M8 5v14l11-7z"/>' };
const ic = (k, s = 28) => `<svg width="${s}" height="${s}" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round">${P[k]}</svg>`;
const tabsUnused = `<div class="tabs"><div class="tab">Audio</div><div class="tab">Subtitles</div><div class="tab">Video</div><div class="tab open">More</div></div>`;
const row = (k, t, v, f) => `<div class="row${f ? ' focus' : ''}">${ic(k)}<span class="lbl">${t}</span><span class="val">${v}</span></div>`;
const scene = `<div class="scene"></div><div class="bars"></div>`;
const tabs2 = `<div class="tabs"><div class="tab">Audio</div><div class="tab open">Subtitles</div><div class="tab">Video</div><div class="tab">More</div></div>`;
const stepper = (v, f) => `<span class="stp${f ? ' f' : ''}">${ic('down', 24)}<b>${v}</b>${ic('up', 24)}</span>`;
const panel = (v) => `<div class="panel">${tabs2}<div class="h"><span>Subtitle appearance</span><i></i></div>` +
  row('size', 'Text Size', 'Small | <span class="sel">Medium</span> | Large') +
  `<div class="row focus">${ic('vpos')}<span class="lbl">Vertical Position</span>${stepper(v, true)}</div>` +
  row('outline', 'Outline', '<span class="tg on"><i></i></span>') + row('bg', 'Background', '<span class="tg"><i></i></span>') + `</div>`;
const sub = pct => `<div class="sub" style="bottom:${pct * 10.8}px">They knew we were coming.</div>`;
const shots = [['01-default.png', scene + sub(5) + panel('Bottom'), 'Default: just above the bottom edge (inside the black bar on wide films)'],
  ['02-raised.png', scene + sub(30) + panel('Raised 30%'), 'Right raises the subtitles a step, up to the top of the screen; Left lowers them; they move as you press']];
const css = `${font(400)}${font(500)}*{box-sizing:border-box;margin:0}body{width:1920px;height:1080px;overflow:hidden;font-family:R;color:#F4F6FB;background:#000;position:relative}
.scene{position:absolute;inset:0;background:radial-gradient(ellipse 40% 50% at 35% 45%,rgba(120,150,190,.55),transparent 70%),linear-gradient(120deg,#1c2a3a 0%,#0d141c 55%,#05070a 100%)}
.shade{position:absolute;inset:0;background:linear-gradient(270deg,rgba(0,0,0,.55) 0%,rgba(0,0,0,0) 45%)}
.hud{position:absolute;left:80px;right:740px;bottom:70px}.hud .title{font-size:30px;font-weight:500;margin-bottom:18px}
.bar{position:relative;height:6px;border-radius:3px;background:rgba(255,255,255,.3)}.fill{height:100%;border-radius:3px;background:#2fb7d8}.bar i{position:absolute;top:-3px;width:3px;height:12px;background:#fff;border-radius:1px}
.times{display:flex;justify-content:space-between;font-size:22px;margin-top:10px;color:#dfe5f1}
.panel{position:absolute;right:48px;top:48px;width:640px;border-radius:26px;padding:26px 26px 22px;background:rgba(22,24,28,.97);border:2px solid rgba(255,255,255,.22)}
.tabs{display:flex;gap:16px;margin-bottom:6px}.tab{flex:1;height:72px;padding:0 6px;display:flex;align-items:center;justify-content:center;gap:10px;font-size:24px;font-weight:500;border-radius:24px;background:#2a2d33}.tab.open{background:#3a3e46}
.h{display:flex;align-items:center;gap:14px;font-size:20px;margin:18px 6px 4px;color:#b7bfcc}.h i{flex:1;height:1px;background:rgba(255,255,255,.18)}.h.back svg{color:#b7bfcc}
.row,.crow{display:flex;align-items:center;gap:16px;height:74px;border-radius:24px;padding:0 16px;font-size:24px;font-weight:500}.focus{box-shadow:inset 0 0 0 3px #fff}
.lbl{flex:1;white-space:nowrap}.val{display:flex;align-items:center;gap:6px;font-weight:400;font-size:23px;color:#dfe5f1}.val svg{color:#8f98a8}
.cn{width:24px;display:flex;color:#5aa9ff}.crow.cur .lbl{color:#5aa9ff}.ct{font-weight:400;font-size:23px;color:#c9d3e6;font-variant-numeric:tabular-nums}
.gap{height:14px}.bars{position:absolute;inset:0;background:linear-gradient(180deg,#000 0,#000 12.3%,transparent 12.3%,transparent 87.7%,#000 87.7%)}.sub{position:absolute;left:0;right:0;text-align:center;font-size:46px;font-weight:500;color:#fff;text-shadow:0 0 4px #000,0 0 4px #000,0 2px 6px #000}.sel{color:#5aa9ff}.tg{width:68px;height:36px;border-radius:18px;background:#3a3f48;position:relative;display:inline-block}.tg.on{background:#2fb7d8}.tg i{position:absolute;top:4px;left:4px;width:28px;height:28px;border-radius:50%;background:#fff}.tg.on i{left:36px}.stp{display:flex;align-items:center;gap:14px;font-size:23px;font-weight:500}.stp svg{color:#dfe5f1}.stp b{font-weight:500;min-width:130px;text-align:center}.label{position:absolute;right:48px;bottom:10px;font-size:18px;color:#c9d3e6}`;
(async () => { const b = await chromium.launch(); const t = await b.newPage({ viewport: { width: 1920, height: 1080 } });
  for (const [f, body, label] of shots) {
    await t.setContent(`<html><head><style>${css}</style></head><body>${body}<div class="label">${label}</div></body></html>`, { waitUntil: 'load' });
    await t.evaluate(() => document.fonts.ready); await t.screenshot({ path: path.join(out, f) }); }
  await b.close(); })();
