// Proposal (0.156, owner 4 Nov 2026): chapters built into the playback progress bar, with a chapter strip below it.
// Run: FONT_DIR=<roboto woff2 dir> node render.js
const fs = require('fs'), path = require('path'), { chromium } = require('playwright');
const out = __dirname, fontDir = process.env.FONT_DIR;
const font = w => `@font-face{font-family:R;font-weight:${w};src:url(data:font/woff2;base64,${fs.readFileSync(path.join(fontDir, `roboto-latin-${w}-normal.woff2`)).toString('base64')}) format('woff2')}`;
const P = { skip: '<path d="M5 5l9 7-9 7z" fill="currentColor"/><path d="M18 5v14"/>', pause: '<path d="M8 5v14M16 5v14"/>',
  gear: '<circle cx="12" cy="12" r="3.2"/><path d="M12 2.5v3M12 18.5v3M2.5 12h3M18.5 12h3M5.3 5.3l2.1 2.1M16.6 16.6l2.1 2.1M5.3 18.7l2.1-2.1M16.6 7.4l2.1-2.1"/>' };
const ic = (k, s = 30) => `<svg width="${s}" height="${s}" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">${P[k]}</svg>`;
// A stand-in film frame (no real artwork): dusky street scene in gradients.
const frame = `<div class="video"><i class="sky"></i><i class="glow"></i><i class="b1"></i><i class="b2"></i><i class="b3"></i><i class="road"></i><i class="vig"></i></div>`;
const ch = [['Chapter 1', 0, '0:00'], ['Chapter 2', 2.23, '2:14'], ['Chapter 3', 11.67, '11:40'], ['Chapter 4', 22.08, '22:05'], ['Chapter 5', 33.8, '33:48'], ['Chapter 6', 44.2, '44:12']];
const LEN = 50.1, NOW = 4.53;
const segs = (focus) => ch.map(([n, s], i) => { const e = i + 1 < ch.length ? ch[i + 1][1] : LEN; const w = (e - s) / LEN * 100, l = s / LEN * 100;
  const played = Math.max(0, Math.min(1, (NOW - s) / (e - s)));
  return `<div class="seg${focus === i ? ' hi' : ''}" style="left:${l}%;width:calc(${w}% - 6px)"><b style="width:${played * 100}%"></b></div>`; }).join('');
const hud = (focus, strip) => `<div class="htitle${strip ? ' up' : ''}">Dark Matter</div><div class="hsub${strip ? ' up' : ''}">Season 2 · Episode 10</div>
  <div class="hud${strip ? ' up' : ''}"><div class="hb">${ic('pause', 34)}</div><div class="bar">${segs(focus)}<div class="thumb${strip ? ' off' : ''}" style="left:${NOW / LEN * 100}%"></div>
  ${strip ? '' : `<div class="bubble" style="left:${NOW / LEN * 100}%"><b>00:04:32</b><span>Chapter 2</span></div>`}</div><div class="hb">${ic('gear', 30)}<span>Settings</span></div></div>` +
  (strip ? `<div class="strip">${ch.map(([n, s, t], i) => `<div class="cc${i === focus ? ' f' : ''}${i === 1 ? ' now' : ''}"><b>${n}</b><span>${i === 1 ? 'Playing · ' : ''}${t}</span></div>`).join('')}</div>` : '');
const shots = [
  ['01-bar-with-chapters.png', frame + `<div class="shade"></div>` + hud(-1, false), 'The progress bar is split at each chapter; the time bubble names the chapter you are in'],
  ['02-chapter-strip.png', frame + `<div class="shade tall"></div>` + hud(2, true), 'Down from the bar opens the chapters below it; Left/Right picks one (its part of the bar lights up), OK jumps there, Up or Back closes'],
];
const css = `${font(400)}${font(500)}${font(700)}*{box-sizing:border-box;margin:0}
body{width:1920px;height:1080px;overflow:hidden;font-family:R;color:#fff;position:relative;background:#000}
.video{position:absolute;inset:0;overflow:hidden}.video i{position:absolute;display:block}
.sky{inset:0;background:linear-gradient(180deg,#1b2440 0%,#3b3550 38%,#a5654a 62%,#2a1d1c 100%)}
.glow{left:52%;top:46%;width:520px;height:260px;border-radius:50%;background:radial-gradient(closest-side,rgba(255,190,120,.55),rgba(255,190,120,0));transform:translate(-50%,-50%)}
.b1{left:0;bottom:22%;width:34%;height:52%;background:#120f14}.b2{right:0;bottom:22%;width:30%;height:60%;background:#0f0c11}
.b3{left:30%;bottom:22%;width:12%;height:30%;background:#1a1418}.road{left:0;right:0;bottom:0;height:22%;background:linear-gradient(180deg,#2a2224,#0c0a0b)}
.vig{inset:0;background:radial-gradient(ellipse 80% 70% at 50% 45%,rgba(0,0,0,0) 55%,rgba(0,0,0,.6))}
.shade{position:absolute;inset:0;background:linear-gradient(0deg,rgba(0,0,0,.75) 0%,rgba(0,0,0,0) 45%)}
.skip{position:absolute;height:120px;padding:0 52px 0 40px;border-radius:35px;background:#F4F6FB;color:#0B0D12;font-size:44px;font-weight:500;
  display:flex;align-items:center;box-shadow:0 10px 30px rgba(0,0,0,.45);overflow:hidden}
.skip span{display:flex;align-items:center;gap:20px}
.skip .cd{position:absolute;left:28px;right:28px;bottom:14px;height:7px;border-radius:3px;background:rgba(11,13,18,.15)}.skip .cd b{display:block;height:100%;border-radius:3px;background:#0B0D12}
.skip.a{padding-bottom:23px}.skip.a .cd{bottom:16px}
.skip.b{background:#AEB4BF}.skip.b .fill{position:absolute;left:0;top:0;bottom:0;background:#F4F6FB;display:block}.skip.b span{position:relative}
.skip.br{right:96px;bottom:110px}.skip.br.up{bottom:270px}.skip.bl{left:96px;bottom:110px}
.htitle{position:absolute;left:80px;top:690px;font-size:60px;font-weight:700}.hsub{position:absolute;left:84px;top:768px;font-size:26px}
.hud{position:absolute;left:70px;right:70px;bottom:90px;height:80px;display:flex;align-items:center;gap:26px}
.hb{height:72px;min-width:72px;padding:0 20px;border-radius:22px;background:rgba(22,24,28,.62);border:2px solid rgba(255,255,255,.22);display:flex;align-items:center;justify-content:center;gap:10px;font-size:22px;font-weight:500}
.bar{flex:1;height:10px;border-radius:5px;background:rgba(255,255,255,.22);position:relative}
.buf{position:absolute;left:0;top:0;bottom:0;width:20%;border-radius:5px;background:rgba(255,255,255,.4)}
.played{position:absolute;left:0;top:0;bottom:0;width:2.4%;border-radius:5px;background:#fff}
.thumb{position:absolute;left:2.4%;top:50%;width:30px;height:30px;margin:-15px 0 0 -15px;border-radius:50%;background:#fff;box-shadow:0 0 0 4px rgba(255,255,255,.25)}
.bubble{position:absolute;left:2.4%;bottom:40px;transform:translateX(-30%);background:rgba(22,24,28,.92);border:2px solid rgba(255,255,255,.22);border-radius:16px;padding:10px 20px;text-align:center}
.bubble b{display:block;font-size:30px;font-weight:500}.bubble span{display:block;font-size:19px;color:#c9d3e6;margin-top:2px}
.seg{position:absolute;top:0;bottom:0;border-radius:5px;background:rgba(255,255,255,.28);overflow:hidden}.seg b{display:block;height:100%;background:#fff}.seg.hi{background:rgba(255,255,255,.6);top:-4px;bottom:-4px}
.bar{background:none!important}.thumb.off{display:none}
.hud.up{bottom:230px}.htitle.up{top:550px}.hsub.up{top:628px}.shade.tall{background:linear-gradient(0deg,rgba(0,0,0,.85) 0%,rgba(0,0,0,0) 60%)}
.strip{position:absolute;left:70px;right:70px;bottom:70px;display:flex;gap:18px}
.cc{flex:1;height:110px;border-radius:22px;background:rgba(22,24,28,.78);border:2px solid rgba(255,255,255,.18);padding:18px 22px;display:flex;flex-direction:column;justify-content:center}
.cc b{font-size:26px;font-weight:500}.cc span{font-size:21px;color:#c9d3e6;margin-top:6px}.cc.now span{color:#5aa9ff}
.cc.f{background:#F4F6FB;border-color:#F4F6FB}.cc.f b{color:#0B0D12}.cc.f span{color:#3a3f48}
.label{position:absolute;left:56px;top:28px;font-size:20px;background:rgba(0,0,0,.6);padding:6px 14px;border-radius:10px}`;
(async () => { const b = await chromium.launch(); const t = await b.newPage({ viewport: { width: 1920, height: 1080 } });
  for (const [f, body, label] of shots) {
    await t.setContent(`<html><head><style>${css}</style></head><body>${body}<div class="label">${label}</div></body></html>`, { waitUntil: 'load' });
    await t.evaluate(() => document.fonts.ready);
    await t.screenshot({ path: path.join(out, f) }); }
  await b.close(); })();
