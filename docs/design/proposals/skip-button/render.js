// Proposal (0.156, owner 4 Nov 2026): the IntroDB "Show Skip" button during playback — selected on arrival, one
// press of OK skips, a bar counts down the 5 seconds. Placement options for the owner to choose.
// Run: FONT_DIR=<roboto woff2 dir> node render.js
const fs = require('fs'), path = require('path'), { chromium } = require('playwright');
const out = __dirname, fontDir = process.env.FONT_DIR;
const font = w => `@font-face{font-family:R;font-weight:${w};src:url(data:font/woff2;base64,${fs.readFileSync(path.join(fontDir, `roboto-latin-${w}-normal.woff2`)).toString('base64')}) format('woff2')}`;
const P = { skip: '<path d="M5 5l9 7-9 7z" fill="currentColor"/><path d="M18 5v14"/>', pause: '<path d="M8 5v14M16 5v14"/>',
  gear: '<circle cx="12" cy="12" r="3.2"/><path d="M12 2.5v3M12 18.5v3M2.5 12h3M18.5 12h3M5.3 5.3l2.1 2.1M16.6 16.6l2.1 2.1M5.3 18.7l2.1-2.1M16.6 7.4l2.1-2.1"/>' };
const ic = (k, s = 30) => `<svg width="${s}" height="${s}" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">${P[k]}</svg>`;
// A stand-in film frame (no real artwork): dusky street scene in gradients.
const frame = `<div class="video"><i class="sky"></i><i class="glow"></i><i class="b1"></i><i class="b2"></i><i class="b3"></i><i class="road"></i><i class="vig"></i></div>`;
const skip = (left, where) => `<div class="skip ${where}"><span>${ic('skip', 46)}Skip Intro</span><i class="cd"><b style="width:${left * 100}%"></b></i></div>`;
const hud = `<div class="htitle">Slow Horses</div><div class="hsub">Season 5 · Episode 2 · Opening titles</div>
  <div class="hud"><div class="hb">${ic('pause', 34)}</div><div class="bar"><div class="buf"></div><div class="played"></div><div class="thumb"></div>
  <div class="bubble"><b>00:01:12</b><span>Intro</span></div></div><div class="hb">${ic('gear', 30)}<span>Settings</span></div></div>`;
const shots = [
  ['01-appears-bottom-right.png', frame + skip(1, 'br'), 'Intro starts: Skip Intro appears bottom right, already selected — OK skips. Controls stay hidden.'],
  ['02-countdown.png', frame + skip(.4, 'br'), 'About 4 of 10 seconds left: the bar runs down; at zero the button fades, but it comes back with the controls until the intro ends'],
  ['03-with-controls-open.png', frame + `<div class="shade"></div>` + hud + skip(.7, 'br up'), 'Controls open during the intro (even after the button faded): Skip Intro sits just above them, selected; Down goes to the controls'],
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
.label{position:absolute;left:56px;top:28px;font-size:20px;background:rgba(0,0,0,.6);padding:6px 14px;border-radius:10px}`;
(async () => { const b = await chromium.launch(); const t = await b.newPage({ viewport: { width: 1920, height: 1080 } });
  for (const [f, body, label] of shots) {
    await t.setContent(`<html><head><style>${css}</style></head><body>${body}<div class="label">${label}</div></body></html>`, { waitUntil: 'load' });
    await t.evaluate(() => document.fonts.ready);
    const issues = await t.evaluate(() => { const o = [], r = e => e.getBoundingClientRect(); const s = document.querySelector('.skip'), h = document.querySelector('.hud'), bb = document.querySelector('.bubble');
      if (s && h && r(s).bottom > r(h).top - 8) o.push('skip overlaps controls');
      if (s && bb && !(r(s).right < r(bb).left || r(s).left > r(bb).right || r(s).bottom < r(bb).top || r(s).top > r(bb).bottom)) o.push('skip overlaps time bubble');
      if (s && r(s).right > 1920 - 48) o.push('skip outside safe area'); return o; });
    if (issues.length) console.log(f, issues);
    await t.screenshot({ path: path.join(out, f) }); }
  await b.close(); })();
