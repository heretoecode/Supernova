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
const ch = [['Recap', 0, '0:00'], ['Intro', 0.87, '0:52'], ['Chapter 1', 1.67, '1:40'], ['Chapter 2', 11.67, '11:40'], ['Chapter 3', 22.08, '22:05'], ['Chapter 4', 33.8, '33:48'], ['Chapter 5', 44.2, '44:12'], ['Credits', 48.4, '48:24']];
const PLAY = 2;
const eps = [['Episode 8', '43 min', 'w'], ['Episode 9', '43 min', 'w'], ['Episode 10', '50 min', 'p'], ['Episode 11', '44 min', ''], ['Episode 12', '43 min', ''], ['Episode 13', '44 min', '']];
const LEN = 50.1, NOW = 4.53;
const zones = '';
const segs = (focus) => { const pct = v => v / LEN * 100;
  let h = `<div class="track"></div><div class="playedc" style="width:${pct(NOW)}%"></div>`;
  if (focus >= 0) { const st = ch[focus][1], e = focus + 1 < ch.length ? ch[focus + 1][1] : LEN; h += `<div class="span" style="left:${pct(st)}%;width:${pct(e - st)}%"></div>`; }
  return h + ch.slice(1).map(([n, st]) => `<i class="tick" style="left:${pct(st)}%"></i>`).join(''); };
const hud = (focus, strip, tabFocus = -1) => `<div class="htitle${strip ? ' up' : ''}">Dark Matter</div><div class="hsub${strip ? ' up' : ''}">Season 2 · Episode 10</div>
  <div class="hud${strip ? ' up' : ''}"><div class="hb">${ic('pause', 34)}</div><div class="bar">${segs(focus)}${zones}<div class="thumb${strip ? ' off' : ''}" style="left:${NOW / LEN * 100}%"></div>
  ${strip ? '' : `<div class="bubble" style="left:${NOW / LEN * 100}%"><b>00:04:32</b><span>Chapter 1</span></div>`}</div><div class="hb">${ic('gear', 30)}<span>Settings</span></div></div>` +
  (!strip ? tabsRow(-1, -1, true) : '') + (strip === 'ch' ? tabsRow(0, tabFocus) + `<div class="strip">${ch.map(([n, st, t], i) => card(`k${i}`, n, i === PLAY ? 'Playing · from ' + t : 'From ' + t, i === PLAY ? (NOW - st) / (ch[PLAY + 1][1] - st) : (i < PLAY ? 1 : 0), i === focus, i === PLAY)).join('')}</div>` : '') +
  (strip === 'ep' ? tabsRow(1, tabFocus) + `<div class="strip">${eps.map(([n, t, st], i) => card(`s${i}`, n, st === 'p' ? 'Playing · ' + t : st === 'w' ? 'Watched · ' + t : t, st === 'p' ? .09 : st === 'w' ? 1 : 0, i === 2, st === 'p')).join('')}</div>` : '');
const card = (bg, title, sub, prog, f, now) => `<div class="hc ${bg}${f ? ' f' : ''}"><div class="hcg"></div><div class="hct"><b>${title}</b><span class="${now ? 'now' : ''}">${sub}</span></div>${prog > 0 ? `<i class="hp"><b style="width:${Math.max(4, prog * 100)}%"></b></i>` : ''}</div>`;
const tabsRow = (open, tf = -1, rest = false) => `<div class="ptabs${rest ? ' rest' : ''}">${['Chapters', 'Episodes'].map((t, i) => `<span class="${i === open ? 'open' : ''}${i === tf ? ' tf' : ''}">${t}</span>`).join('')}</div>`;
const tip = `<div class="tip"><div class="tr"><span class="k">‹ ›</span>Seek<span class="sep">·</span><span class="k">OK</span>Play / pause</div><div class="tr"><span class="k">↑</span>Play/pause and Settings buttons</div><div class="tr"><span class="k">↓</span>Chapters and Episodes</div></div>`;
const shots = [
  ['01-controls-resting.png', frame + `<div class="shade"></div>` + hud(-1, false), 'Controls raised a little so Chapters and Episodes fit comfortably below; a thin line crosses the bar at each part: Recap and Intro (IntroDB), the chapters, then Credits'],
  ['02-down-to-chapters.png', frame + `<div class="shade tall"></div>` + hud(-1, 'ch', 0), 'Down from the bar: the controls rise and the Chapters row slides in below; Left/Right switches to Episodes'],
  ['03-chapter-strip.png', frame + `<div class="shade tall"></div>` + hud(3, 'ch'), 'Down again into the row: the focused card grows and its stretch of the bar lights up between its lines; OK jumps there'],
  ['04-episode-strip.png', frame + `<div class="shade tall"></div>` + hud(-1, 'ep'), 'Episodes: opened at the one playing, watched ones marked; OK plays the chosen episode; Up, Up returns to the bar'],
  ['05-first-play-tip.png', frame + `<div class="shade"></div>` + hud(-1, false) + tip, 'First video only: a small tip on how the remote works; gone after 8 seconds or any press'],
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
.htitle{position:absolute;left:80px;top:630px;font-size:60px;font-weight:700}.hsub{position:absolute;left:84px;top:708px;font-size:26px}
.hud{position:absolute;left:70px;right:70px;bottom:150px;height:80px;display:flex;align-items:center;gap:26px}
.hb{height:72px;min-width:72px;padding:0 20px;border-radius:22px;background:rgba(22,24,28,.62);border:2px solid rgba(255,255,255,.22);display:flex;align-items:center;justify-content:center;gap:10px;font-size:22px;font-weight:500}
.bar{flex:1;height:10px;border-radius:5px;background:rgba(255,255,255,.22);position:relative}
.buf{position:absolute;left:0;top:0;bottom:0;width:20%;border-radius:5px;background:rgba(255,255,255,.4)}
.played{position:absolute;left:0;top:0;bottom:0;width:2.4%;border-radius:5px;background:#fff}
.thumb{position:absolute;left:2.4%;top:50%;width:30px;height:30px;margin:-15px 0 0 -15px;border-radius:50%;background:#fff;box-shadow:0 0 0 4px rgba(255,255,255,.25)}
.bubble{position:absolute;left:2.4%;bottom:40px;transform:translateX(-30%);background:rgba(22,24,28,.92);border:2px solid rgba(255,255,255,.22);border-radius:16px;padding:10px 20px;text-align:center}
.bubble b{display:block;font-size:30px;font-weight:500}.bubble span{display:block;font-size:19px;color:#c9d3e6;margin-top:2px}
.seg{position:absolute;top:0;bottom:0;border-radius:5px;background:rgba(255,255,255,.28);overflow:hidden}.seg b{display:block;height:100%;background:#fff}.seg.hi{background:rgba(255,255,255,.6);top:-4px;bottom:-4px}
.bar{background:none!important}.thumb.off{display:none}
.hud.up{bottom:350px}.htitle.up{top:430px}.hsub.up{top:508px}.shade.tall{background:linear-gradient(0deg,rgba(0,0,0,.85) 0%,rgba(0,0,0,0) 60%)}
.strip{position:absolute;left:70px;right:-200px;bottom:52px;display:flex;gap:26px;align-items:center;height:200px}
.hc{position:relative;flex:none;width:300px;height:169px;border-radius:16px;overflow:hidden;box-shadow:0 6px 18px rgba(0,0,0,.45)}.hc.f{width:336px;height:189px;box-shadow:0 0 0 3px #fff,0 10px 26px rgba(0,0,0,.55)}
.hcg{position:absolute;inset:0;background:linear-gradient(0deg,rgba(0,0,0,.78) 0%,rgba(0,0,0,0) 60%)}.hct{position:absolute;left:16px;right:16px;bottom:20px}.hct b{display:block;font-size:23px;font-weight:700}.hct span{display:block;font-size:18px;color:#e6ebf5;margin-top:2px}.hct span.now{color:#9fd0ff}
.hp{position:absolute;left:14px;right:14px;bottom:9px;height:5px;border-radius:3px;background:rgba(255,255,255,.3)}.hp b{display:block;height:100%;border-radius:3px;background:#fff}
.k0{background:radial-gradient(ellipse at 30% 40%,#c9a46a,#3b2a1a 70%)}.k1{background:radial-gradient(ellipse at 60% 30%,#6aa0c9,#17263b 70%)}.k2{background:radial-gradient(ellipse at 40% 60%,#c96a5a,#3b1a17 70%)}.k3{background:radial-gradient(ellipse at 70% 50%,#7ac98a,#173b22 70%)}.k4{background:radial-gradient(ellipse at 30% 30%,#a08ad0,#221a3b 70%)}.k5{background:radial-gradient(ellipse at 50% 50%,#d0c06a,#3b331a 70%)}
.ptabs{position:absolute;left:70px;bottom:262px;display:flex;gap:14px}.ptabs.rest{bottom:70px}.ptabs.rest span{height:46px;font-size:20px;padding:0 20px;border-radius:15px}.ptabs span.tf,.ptabs span.open.tf{background:#F4F6FB!important;color:#0B0D12!important;border-color:#F4F6FB!important}.ptabs span{height:56px;padding:0 26px;border-radius:18px;background:rgba(22,24,28,.7);border:2px solid rgba(255,255,255,.18);display:flex;align-items:center;font-size:23px;font-weight:500;color:#dfe5f1}.ptabs span.open{background:#3a3e46;color:#fff}
.ec{flex:1;min-width:0;border-radius:22px;padding:10px;background:rgba(22,24,28,.78);border:2px solid rgba(255,255,255,.18)}.ec.f{background:#F4F6FB;border-color:#F4F6FB}.ec.f b{color:#0B0D12}.ec.f span{color:#3a3f48}
.ec b{display:block;font-size:22px;font-weight:500;margin:8px 6px 0}.ec span{display:block;font-size:19px;color:#c9d3e6;margin:2px 6px 4px}.ec span.now{color:#5aa9ff}
.still{position:relative;height:64px;border-radius:14px;overflow:hidden}.still em{position:absolute;right:8px;top:8px;font-style:normal;font-size:16px;font-weight:500;background:rgba(0,0,0,.6);padding:3px 10px;border-radius:9px}
.still .pg{position:absolute;left:8px;right:8px;bottom:8px;height:5px;border-radius:3px;background:rgba(255,255,255,.35)}.still .pg b{display:block;height:100%;margin:0;background:#2fb7d8;border-radius:3px}
.s0{background:linear-gradient(135deg,#3b4a63,#1b2230)}.s1{background:linear-gradient(135deg,#5a3d34,#22191a)}.s2{background:linear-gradient(135deg,#2f5560,#162428)}.s3{background:linear-gradient(135deg,#4c3e66,#1d1828)}.s4{background:linear-gradient(135deg,#355c3f,#17231a)}.s5{background:linear-gradient(135deg,#66503a,#241c15)}
.cc{flex:1;height:110px;border-radius:22px;background:rgba(22,24,28,.78);border:2px solid rgba(255,255,255,.18);padding:18px 22px;display:flex;flex-direction:column;justify-content:center}
.cc b{font-size:26px;font-weight:500}.cc span{font-size:21px;color:#c9d3e6;margin-top:6px}.cc.now span{color:#5aa9ff}
.cc.f{background:#F4F6FB;border-color:#F4F6FB}.cc.f b{color:#0B0D12}.cc.f span{color:#3a3f48}
.tip{position:absolute;left:50%;top:300px;transform:translateX(-50%);background:rgba(22,24,28,.95);border:2px solid rgba(255,255,255,.22);border-radius:24px;padding:24px 34px;box-shadow:0 12px 40px rgba(0,0,0,.5)}
.tr{display:flex;align-items:center;gap:14px;font-size:24px;height:52px}.k{min-width:56px;height:40px;padding:0 10px;border-radius:12px;background:#3a3e46;display:flex;align-items:center;justify-content:center;font-size:21px;font-weight:500}.sep{color:#8f98a8;margin:0 6px}
.zone{position:absolute;top:-5px;bottom:-5px;border-radius:3px;background:repeating-linear-gradient(135deg,rgba(255,211,138,.95) 0 4px,rgba(255,211,138,.35) 4px 8px);pointer-events:none}
.track{position:absolute;inset:0;border-radius:5px;background:rgba(255,255,255,.28)}.playedc{position:absolute;left:0;top:0;bottom:0;border-radius:5px;background:#fff}
.span{position:absolute;top:-3px;bottom:-3px;border-radius:6px;background:rgba(255,255,255,.55)}.tick{position:absolute;top:-6px;width:3px;height:22px;margin-left:-1.5px;border-radius:2px;background:rgba(255,255,255,.9);box-shadow:0 0 0 1.5px rgba(0,0,0,.55)}
.label{position:absolute;left:56px;top:28px;font-size:20px;background:rgba(0,0,0,.6);padding:6px 14px;border-radius:10px}`;
(async () => { const b = await chromium.launch(); const t = await b.newPage({ viewport: { width: 1920, height: 1080 } });
  for (const [f, body, label] of shots) {
    await t.setContent(`<html><head><style>${css}</style></head><body>${body}<div class="label">${label}</div></body></html>`, { waitUntil: 'load' });
    await t.evaluate(() => document.fonts.ready);
    await t.screenshot({ path: path.join(out, f) }); }
  await b.close(); })();
