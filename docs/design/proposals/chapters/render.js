// Proposal (0.156): Chapters list inside the Playback Settings side panel. Run: FONT_DIR=<roboto woff2 dir> node render.js
const fs = require('fs'), path = require('path'), { chromium } = require('playwright');
const out = __dirname, fontDir = process.env.FONT_DIR;
const font = w => `@font-face{font-family:R;font-weight:${w};src:url(data:font/woff2;base64,${fs.readFileSync(path.join(fontDir, `roboto-latin-${w}-normal.woff2`)).toString('base64')}) format('woff2')}`;
const P = { cc: '<rect x="3" y="5" width="18" height="14" rx="2"/><path d="M10 10.2a2.2 2.2 0 1 0 0 3.6M16.5 10.2a2.2 2.2 0 1 0 0 3.6"/>',
  audio: '<path d="M4 9h4l5-4v14l-5-4H4z"/><path d="M16 9a4 4 0 0 1 0 6"/>', play: '<path d="M8 5v14l11-7z"/>',
  chap: '<path d="M4 6h16M4 12h16M4 18h10"/><path d="M18 16l3 2-3 2z"/>', speed: '<path d="M5 18a8 8 0 1 1 14 0"/><path d="M12 13l4-4"/>',
  ratio: '<rect x="3" y="6" width="18" height="12" rx="2"/><path d="M7 10v4M17 10v4"/>', bell: '<path d="M6 16V11a6 6 0 0 1 12 0v5l2 2H4z"/><path d="M10 20a2 2 0 0 0 4 0"/>', moon: '<path d="M19 14.5A7.5 7.5 0 0 1 9.5 5a7.5 7.5 0 1 0 9.5 9.5z"/>',
  chev: '<path d="M9 6l6 6-6 6"/>', back: '<path d="M15 6l-6 6 6 6"/>', now: '<path d="M8 5v14l11-7z"/>' };
const ic = (k, s = 28) => `<svg width="${s}" height="${s}" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round">${P[k]}</svg>`;
const tabs = `<div class="tabs"><div class="tab">Audio</div><div class="tab">Subtitles</div><div class="tab">Video</div><div class="tab open">More</div></div>`;
const row = (k, t, v, f) => `<div class="row${f ? ' focus' : ''}">${ic(k)}<span class="lbl">${t}</span><span class="val">${v}</span></div>`;
const chapters = [['Chapter 1', '0:00', 0], ['Chapter 2', '2:14', 1], ['Chapter 3', '11:40', 0], ['Chapter 4', '22:05', 0], ['Chapter 5', '33:48', 0], ['Chapter 6', '44:12', 0]];
const scene = `<div class="scene"></div><div class="shade"></div>`;
const playbackTab = `<div class="panel">${tabs}<div class="gap"></div>` +
  row('chap', 'Chapters', `Chapter 2 of 6 ${ic('chev', 22)}`, true) + `<div class="h"><span>Playback</span><i></i></div>` + row('speed', 'Playback Speed', '1.0×') + row('play', 'Play mode', `${ic('chev', 22)}`) + row('bell', 'Notifications', `${ic('chev', 22)}`) + `</div>`;
const list = `<div class="panel">${tabs}<div class="h back">${ic('back', 22)}<span>Chapters</span><i></i></div>` +
  chapters.map(([n, t, cur], i) => `<div class="crow${i === 2 ? ' focus' : ''}${cur ? ' cur' : ''}"><span class="cn">${cur ? ic('now', 20) : ''}</span><span class="lbl">${n}</span><span class="ct">${t}</span></div>`).join('') +
  `</div>`;
const hud = `<div class="hud"><div class="title">Dark Matter · S2 E10</div><div class="bar"><div class="fill" style="width:9%"></div>${[2.14, 11.4, 22.05, 33.8, 44.2].map(m => `<i style="left:${m / 50 * 100}%"></i>`).join('')}</div><div class="times"><span>4:32</span><span>50:06</span></div></div>`;
const shots = [['01-playback-tab.png', playbackTab, 'More tab: a Chapters row at the top, only when the video has chapters; it shows where you are'],
  ['02-chapter-list.png', list, 'OK opens the list inside the panel: name and start time, the current chapter marked, OK jumps']];
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
.gap{height:14px}.label{position:absolute;right:48px;bottom:10px;font-size:18px;color:#c9d3e6}`;
(async () => { const b = await chromium.launch(); const t = await b.newPage({ viewport: { width: 1920, height: 1080 } });
  for (const [f, body, label] of shots) {
    await t.setContent(`<html><head><style>${css}</style></head><body>${scene}${hud}${body}<div class="label">${label}</div></body></html>`, { waitUntil: 'load' });
    await t.evaluate(() => document.fonts.ready); await t.screenshot({ path: path.join(out, f) }); }
  await b.close(); })();
