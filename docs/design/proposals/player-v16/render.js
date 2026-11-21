// Player v16 (owner, 21 Nov 2026): the player on today's rules, for the full player review.
// 1 controls with Chapters open, focus on the seek bar. 2 Chapters tab focused. 3 Episodes open, an episode focused.
// 4-6 Playback Settings: Subtitles, Audio, Playback (v15 content). 7 the skip button alone.
// Title block = Preparing playback's (logo, title line, facts with the age badge first, no reasons line); clock in the
// bar's regular weight; cards with the artwork edge and the corner badge (no bars); tabs = one tab behaviour; menus
// and panels 99% with the 70% outline and shadow (proposed over video too).
// Run: FONT_DIR=<dir with roboto-latin-*-normal.woff2> node render.js
const fs = require('fs');
const path = require('path');
const { chromium } = require('playwright');
const fontDir = process.env.FONT_DIR;
const OUT = path.join(__dirname, 'tv');
const font = w => `@font-face{font-family:R;font-weight:${w};src:url(data:font/woff2;base64,${fs.readFileSync(
  path.join(fontDir, `roboto-latin-${w}-normal.woff2`)).toString('base64')}) format('woff2')}`;
function svg(d, s = 28) {
  return `<svg width="${s}" height="${s}" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8"
    stroke-linecap="round" stroke-linejoin="round">${d}</svg>`;
}
const I = {
  pause: '<path d="M8 5v14M16 5v14"/>', cog: '<circle cx="12" cy="12" r="3"/><path d="M12 2v3M12 19v3M4.2 4.2l2.1 2.1M17.7 17.7l2.1 2.1M2 12h3M19 12h3M4.2 19.8l2.1-2.1M17.7 6.3l2.1-2.1"/>',
  chap: '<path d="M4 6h16M4 12h16M4 18h10"/>', eps: '<rect x="3" y="4" width="18" height="13" rx="2"/><path d="M10 8l4 2.5-4 2.5zM8 21h8"/>',
  subs: '<rect x="3" y="5" width="18" height="14" rx="2"/><path d="M7 12h4M13 12h4M7 15.5h7"/>',
  audio: '<path d="M4 9v6h4l5 4V5L8 9z"/><path d="M16.5 8.5a5 5 0 0 1 0 7"/>', play: '<path d="M7 4l13 8-13 8z"/>',
  list: '<path d="M8 6h13M8 12h13M8 18h13M3 6h.1M3 12h.1M3 18h.1"/>', dl: '<path d="M12 4v11M7 10l5 5 5-5M5 20h14"/>',
  clock: '<circle cx="12" cy="13" r="8"/><path d="M12 9v4l3 2M10 2h4"/>', aa: '<path d="M3 19l5-14h1l5 14M5 14h7M15 19v-5a3 3 0 0 1 6 0v5"/>',
  palette: '<circle cx="12" cy="12" r="9"/><circle cx="8" cy="10" r="1.2"/><circle cx="12" cy="7.5" r="1.2"/><circle cx="16" cy="10" r="1.2"/>',
  vpos: '<path d="M12 3v18M8 7l4-4 4 4M8 17l4 4 4-4"/>', outline: '<rect x="4" y="4" width="16" height="16" rx="2"/><path d="M9 16l3-8 3 8M10 13h4"/>',
  bg: '<rect x="4" y="4" width="16" height="16" rx="2"/><path d="M4 14l6-6 10 10"/>', drop: '<path d="M12 3s6 7 6 11a6 6 0 0 1-12 0c0-4 6-11 6-11z"/>',
  remember: '<path d="M4 7h12l-3-3M20 17H8l3 3"/>', boost: '<path d="M4 9v6h4l5 4V5L8 9z"/><path d="M17 9v6M20 7v10"/>',
  moon: '<path d="M20 15A8 8 0 1 1 9 4a6.5 6.5 0 0 0 11 11z"/>', spatial: '<circle cx="12" cy="12" r="2"/><path d="M7 7a7 7 0 0 0 0 10M17 7a7 7 0 0 1 0 10"/>',
  format: '<rect x="3" y="6" width="18" height="12" rx="2"/><path d="M7 10v4M17 10v4"/>', speed: '<path d="M4 18a8 8 0 1 1 16 0"/><path d="M12 18l4-6"/>',
  ask: '<circle cx="12" cy="12" r="9"/><path d="M9.5 9a2.5 2.5 0 1 1 3.5 2.3c-.6.3-1 .8-1 1.5V14M12 17v.1"/>',
  mode: '<path d="M4 7h12l-3-3M20 17H8l3 3"/>', skip: '<path d="M5 5l9 7-9 7zM18 5v14"/>',
  chevL: '<path d="M15 6l-6 6 6 6"/>', chev: '<path d="M9 6l6 6-6 6"/>',
};
const css = `${[400, 500, 700, 900].map(font).join('')}
*{box-sizing:border-box;margin:0;padding:0}
body{width:1920px;height:1080px;overflow:hidden;font-family:R,sans-serif;color:#fff;background:#000}
.screen{position:relative;width:1920px;height:1080px;overflow:hidden;
 background:radial-gradient(ellipse 45% 55% at 58% 40%,rgba(150,120,90,.75),transparent 70%),
 radial-gradient(ellipse 30% 40% at 25% 70%,rgba(60,90,80,.6),transparent 70%),linear-gradient(160deg,#2a2622,#0c0b0a)}
.border{position:absolute;inset:0;border:2px solid #c9ccd3;z-index:40}
.dim{position:absolute;inset:0;background:linear-gradient(180deg,rgba(0,0,0,.55),rgba(0,0,0,0) 32%,rgba(0,0,0,0) 45%,rgba(0,0,0,.82) 100%)}
.tblock{position:absolute;left:72px;top:56px}
.tblock .logo{font-weight:900;font-size:72px;line-height:.9;letter-spacing:-1px;text-transform:uppercase}
.tblock .tl{font-size:28px;font-weight:500;margin-top:18px}
.tblock .facts{display:flex;align-items:center;gap:12px;font-size:26px;margin-top:10px}
.age{border:1.5px solid #fff;border-radius:6px;padding:1px 8px;font-size:22px;font-weight:500}
.clock{position:absolute;right:72px;top:52px;font-size:30px}
.ctrl{position:absolute;left:72px;right:72px;display:flex;align-items:center;gap:22px}
.pbtn{width:52px;height:52px;border-radius:20px;display:flex;align-items:center;justify-content:center;background:rgba(10,11,13,.6)}
.pbtn.f,.sbtn.f{background:#f4f6fb;color:#0b0d12}
.seek{flex:1;height:6px;border-radius:3px;background:rgba(255,255,255,.3);position:relative}
.seek b{position:absolute;left:0;top:0;bottom:0;width:26%;background:#fff;border-radius:3px}
.seek .buf{position:absolute;left:0;top:0;bottom:0;width:48%;background:rgba(255,255,255,.5);border-radius:3px}
.seek i{position:absolute;top:-6px;width:2px;height:18px;background:#fff}
.seek .knob{position:absolute;left:26%;top:50%;width:30px;height:30px;margin:-15px 0 0 -15px;border-radius:15px;background:#fff}
.seek.f .knob{width:38px;height:38px;margin:-19px 0 0 -19px;box-shadow:0 0 0 6px rgba(255,255,255,.25)}
.time{font-size:26px;white-space:nowrap}
.sbtn{height:52px;padding:0 22px;border-radius:20px;display:flex;align-items:center;gap:10px;font-size:26px;background:rgba(10,11,13,.6)}
.preview{position:absolute;width:320px;height:180px;border-radius:12px;overflow:hidden;box-shadow:inset 0 0 0 1px #fff;
 background:radial-gradient(ellipse 60% 60% at 50% 40%,rgba(80,140,100,.6),transparent 70%),linear-gradient(160deg,#20302a,#0b120f)}
.preview div{position:absolute;left:16px;bottom:12px;font-size:28px;font-weight:500}
.preview small{display:block;font-size:20px;color:#c9d3e6;font-weight:400}
.tabs{position:absolute;left:72px;display:flex;gap:10px;font-size:28px}
.tabs span{height:52px;padding:0 22px;border-radius:20px;display:flex;align-items:center;gap:12px;position:relative}
.tabs .f{background:#f4f6fb;color:#0b0d12}
.tabs .u:after{content:"";position:absolute;left:22px;right:22px;bottom:2px;height:3px;border-radius:2px;background:#fff}
.tline{position:absolute;left:72px;right:72px;height:2px;background:linear-gradient(90deg,rgba(255,255,255,.04),rgba(255,255,255,.35) 22%,rgba(255,255,255,.35) 78%,rgba(255,255,255,.04))}
.card{position:absolute;width:400px;height:225px;border-radius:14px;overflow:hidden}
.card.focus{width:432px;height:243px;box-shadow:0 0 0 4px #fff;z-index:3}
.card .art{position:absolute;inset:0}
.card .fade{position:absolute;left:0;right:0;bottom:0;height:60%;background:linear-gradient(0deg,rgba(8,9,11,.92),rgba(8,9,11,0))}
.card .edge{position:absolute;inset:0;border-radius:14px;box-shadow:inset 0 0 0 1px #fff}
.card.focus .edge{display:none}
.card .ct{position:absolute;left:18px;right:18px;bottom:14px}
.card .c1{font-size:24px;font-weight:500;white-space:nowrap;overflow:hidden;text-overflow:ellipsis}
.card .c2{font-size:19px;color:#c9d3e6;margin-top:4px}
.badge{position:absolute;top:8px;right:8px;width:48px;height:48px;border-radius:50%;background:#fff;box-shadow:0 1px 6px rgba(0,0,0,.45)}
.badge svg{position:absolute;left:0;top:0}
.panel{position:absolute;right:72px;top:56px;width:700px;border-radius:20px;background:rgba(22,24,28,.99);padding:16px 0 18px;
 box-shadow:0 0 0 1.5px rgba(255,255,255,.7),0 18px 48px 12px rgba(0,0,0,.85)}
.ptabs{display:flex;gap:8px;padding:0 20px 14px;font-size:26px}
.ptabs span{height:52px;padding:0 20px;border-radius:20px;display:flex;align-items:center;gap:10px;position:relative}
.ptabs .f{background:#f4f6fb;color:#0b0d12}
.ptabs .u:after{content:"";position:absolute;left:20px;right:20px;bottom:2px;height:3px;border-radius:2px;background:#fff}
.h{display:flex;align-items:center;gap:14px;font-size:24px;padding:10px 26px 6px;color:#fff}
.h:before,.h:after{content:"";flex:1;height:1px;background:rgba(255,255,255,.3)}
.r{height:62px;display:flex;align-items:center;gap:18px;padding:0 16px;margin:0 10px;border-radius:12px;font-size:25px;position:relative}
.r .ic{width:28px;display:flex}
.r .fill{flex:1}
.r .v{font-weight:500;display:flex;align-items:center;gap:10px}
.r .q{color:#c9d3e6;font-weight:400}
.r.on{background:#f4f6fb;color:#0b0d12}
.r.on .q{color:#3a3f4a}
.pill{height:30px;padding:0 12px;border-radius:15px;border:1.5px solid rgba(255,255,255,.65);font-size:17px;display:flex;align-items:center;font-weight:400}
.r.on .pill{border-color:#0b0d12}
.sw{width:60px;height:32px;border-radius:16px;position:relative;background:rgba(255,255,255,.25)}
.sw:after{content:"";position:absolute;left:4px;top:4px;width:24px;height:24px;border-radius:12px;background:#fff}
.sw.on{background:#f4f6fb}.sw.on:after{left:auto;right:4px;background:#0b0d12}
.r.on .sw.on{background:#0b0d12}.r.on .sw.on:after{background:#fff}
.r.on .sw{background:#c9ccd3}.r.on .sw:after{background:#0b0d12}
.chips{display:flex;gap:4px}.chips b{height:42px;padding:0 16px;border-radius:20px;display:flex;align-items:center;font-weight:400}
.chips b.sel{background:rgba(255,255,255,.2)}
.dots{display:flex;gap:10px}.dots i{width:26px;height:26px;border-radius:13px}
.dots i.sel{box-shadow:0 0 0 2px #16181c,0 0 0 4px #fff}
.subline{position:absolute;left:92px;bottom:120px;font-size:44px;font-weight:500;text-shadow:0 0 6px #000,0 2px 4px #000}
.cap2{position:absolute;right:52px;bottom:24px;color:#e8ebf2;font-size:24px;z-index:41;background:rgba(17,18,21,.85);padding:4px 12px;border-radius:8px}
`;
const ART = [['#7a5a3a', '#26180d'], ['#2e4f78', '#0d1726'], ['#7a2e2e', '#260d0d'], ['#2f5c4a', '#0e1d17'], ['#5a4a7a', '#1a1426']];
function pie(p) { const a = 2 * Math.PI * p - Math.PI / 2; return `<path d="M24 24 L24 3 A21 21 0 ${p > 0.5 ? 1 : 0} 1 ${24 + 21 * Math.cos(a)} ${24 + 21 * Math.sin(a)} Z" fill="#000"/>`; }
function badge(st) {
  if (st == null) return '';
  const m = st === 'w' ? '<path d="M15 24.5l6 6 12-13" fill="none" stroke="#000" stroke-width="4" stroke-linecap="round" stroke-linejoin="round"/>' : pie(st);
  return `<div class="badge"><svg width="48" height="48" viewBox="0 0 48 48">${m}</svg></div>`;
}
function cards(y, list, focus) {
  return list.map((c, k) => { const f = k === focus; const a = ART[k % 5];
    return `<div class="card${f ? ' focus' : ''}" style="left:${72 + k * 440 - (f ? 16 : 0)}px;top:${y - (f ? 9 : 0)}px"><div class="art" style="background:radial-gradient(ellipse 60% 60% at 50% 40%,rgba(255,255,255,.18),transparent 60%),linear-gradient(155deg,${a[0]},${a[1]})"></div><div class="fade"></div>${badge(c[2])}<div class="ct"><div class="c1">${c[0]}</div><div class="c2">${c[1]}</div></div><div class="edge"></div></div>`; }).join('');
}
const top = () => `<div class="dim"></div><div class="tblock"><div class="logo">Marshals</div><div class="tl">Season 1 · Episode 5 · Wind River</div>
  <div class="facts"><span class="age">15</span>2026 · 46 min · Drama, Crime, Action</div></div><div class="clock">21:40</div>`;
function controls(y, focus) {
  return `<div class="ctrl" style="top:${y}px"><span class="pbtn${focus === 'play' ? ' f' : ''}">${svg(I.pause, 26)}</span>
    <div class="seek${focus === 'seek' ? ' f' : ''}"><div class="buf"></div><b></b><i style="left:3%"></i><i style="left:15%"></i><i style="left:18%"></i><i style="left:47%"></i><span class="knob"></span></div>
    <span class="time">12:06 / 46:12 · Ends 22:14</span><span class="sbtn${focus === 'set' ? ' f' : ''}">${svg(I.cog, 26)}Settings</span></div>`;
}
function tabs(y, open, focus) {
  return `<div class="tabs" style="top:${y}px">${[['Chapters', I.chap], ['Episodes', I.eps]].map(([n, ic]) =>
    `<span class="${n === focus ? 'f' : n === open ? 'u' : ''}">${svg(ic, 26)}${n}</span>`).join('')}</div><div class="tline" style="top:${y + 66}px"></div>`;
}
const CH = [['Recap', 'From 0:02', 'w'], ['Chapter 1', 'From 1:06', 'w'], ['Intro', 'From 6:13', 'w'], ['Chapter 2', 'Playing · from 7:13', 0.4], ['Chapter 3', 'From 19:40', null]];
const EP = [['Long Way Down', 'S1 E2 · 45 min', 'w'], ['Dry Creek', 'S1 E3 · 44 min', 'w'], ['Black Hills', 'S1 E4 · 46 min', 'w'], ['Wind River', 'Playing · 34 min left', 0.26], ['Sundown', 'S1 E6 · 47 min', null]];
function f1() { return top() + `<div class="preview" style="left:400px;top:366px"><div>12:06<small>Chapter 2</small></div></div>` + controls(596, 'seek') + tabs(688, 'Chapters') + cards(784, CH, -1) + '<div class="cap2">1 · Controls, Chapters open, focus on the seek bar</div>'; }
function f2() { return top() + controls(596, null) + tabs(688, 'Chapters', 'Chapters') + cards(784, CH, -1) + '<div class="cap2">2 · Chapters tab focused: the white box, clear of the line</div>'; }
function f3() { return top() + controls(596, null) + tabs(688, 'Episodes') + cards(783, EP, 3) + '<div class="cap2">3 · Episodes open, an episode focused: the tab keeps its underline</div>'; }
function row(ic, label, right, on) { return `<div class="r${on ? ' on' : ''}"><span class="ic">${svg(ic, 26)}</span><span>${label}</span><span class="fill"></span>${right}</div>`; }
function step(v, on) { return `<span class="v">${on ? svg(I.chevL, 22) : ''}${v}${on ? svg(I.chev, 22) : '<span style="width:22px"></span>'}</span>`; }
function pm(v) { return `<span class="v">−&nbsp;&nbsp;${v}&nbsp;&nbsp;+</span>`; }
const sw = on => `<span class="sw${on ? ' on' : ''}"></span>`;
const chev = `<span style="display:flex;color:#c9d3e6">${svg(I.chev, 22)}</span>`;
function ptabs(open, focus) { return `<div class="ptabs">${[['Subtitles', I.subs], ['Audio', I.audio], ['Playback', I.play]].map(([n, ic]) => `<span class="${n === focus ? 'f' : n === open ? 'u' : ''}">${svg(ic, 24)}${n}</span>`).join('')}</div>`; }
function f4() {
  return '<div class="subline">Who you really are, and why you use it.</div>' + `<div class="panel">${ptabs('Subtitles')}
    <div class="h">Subtitle Selection</div>${row(I.subs, 'Subtitles', sw(true))}
    ${row(I.list, 'Subtitle track', '<span class="v">English <span class="pill">Original</span><span class="pill">SDH</span></span>' + chev)}
    ${row(I.dl, 'Download subtitles', chev)}${row(I.clock, 'Subtitle delay', pm('0 ms'))}
    <div class="h">Subtitle Appearance</div>${row(I.aa, 'Text size', step('Medium', true), true)}
    ${row(I.palette, 'Text colour', '<span class="dots"><i class="sel" style="background:#fff"></i><i style="background:#f5d33a"></i><i style="background:#3ad0d6"></i><i style="background:#5fd068"></i><i style="background:#f07aa0"></i></span>')}
    ${row(I.vpos, 'Vertical position', pm('Raised 5%'))}${row(I.outline, 'Outline', sw(true))}${row(I.bg, 'Background', sw(false))}
    ${row(I.drop, 'Background opacity', step('75%'))}<div class="h">This Show</div>${row(I.remember, 'Remember for this show', sw(false))}</div>`
    + '<div class="cap2">4 · Playback Settings › Subtitles (Text size focused)</div>';
}
function f5() {
  return '<div class="subline">Who you really are, and why you use it.</div>' + `<div class="panel">${ptabs('Audio')}
    <div class="h">Audio Selection</div>${row(I.audio, 'Audio track', '<span class="v">English <span class="pill">Dolby Digital Plus</span><span class="pill">5.1</span></span>' + chev)}
    <div class="h">Audio Adjustments</div>${row(I.clock, 'Audio delay', pm('0 ms'))}${row(I.boost, 'Audio boost', step('Off', true), true)}
    ${row(I.moon, 'Night mode', sw(false))}${row(I.spatial, 'Spatial audio', sw(false))}
    <div class="h">This Show</div>${row(I.remember, 'Remember for this show', sw(false))}</div>`
    + '<div class="cap2">5 · Playback Settings › Audio (Audio boost focused)</div>';
}
function f6() {
  return '<div class="subline">Who you really are, and why you use it.</div>' + `<div class="panel">${ptabs('Playback')}
    <div class="h">Picture and Speed</div>${row(I.format, 'Format', step('Fit'))}${row(I.speed, 'Speed', step('1×'))}
    <div class="h">Playing On</div>${row(I.moon, 'Stop playing at', step('22:40', true), true)}${row(I.ask, 'Still watching?', step('Off'))}
    ${row(I.mode, 'Play mode', step('Play next'))}${row(I.skip, 'Skipping', step('Show button'))}
    <div class="h">This Show</div>${row(I.remember, 'Remember for this show', sw(false))}</div>`
    + '<div class="cap2">6 · Playback (Skipping now steps; panel narrowed to about 700 px)</div>';
}
function f8() {
  return top() + `<div class="ctrl" style="top:596px"><span class="pbtn">${svg(I.pause, 26)}</span>
    <div class="seek"><div class="buf"></div><b style="width:8%"></b><i style="left:3%"></i><i style="left:15%"></i><span class="knob" style="left:8%"></span></div>
    <span class="time">3:40 / 46:12 · Ends 22:48</span><span class="sbtn">${svg(I.cog, 26)}Settings</span><span class="sbtn f">${svg(I.skip, 26)}Skip intro</span></div>`
    + tabs(688, 'Chapters') + cards(784, CH, -1) + '<div class="cap2">8 · Skip with the controls: it takes the far right, Settings moves in, the seek bar shortens</div>';
}
function f7() { return `<div class="ctrl" style="top:960px;justify-content:flex-end"><span class="sbtn f">${svg(I.skip, 26)}Skip intro</span></div>` + '<div class="cap2" style="right:auto;left:52px">7 · Skip button alone, in its place on the controls\' line</div>'; }
(async () => {
  fs.mkdirSync(OUT, { recursive: true });
  const b = await chromium.launch(); const pg = await b.newPage({ viewport: { width: 1920, height: 1080 } });
  for (const [n, fn] of [['01-controls-chapters', f1], ['02-chapters-tab-focused', f2], ['03-episodes', f3], ['04-settings-subtitles', f4], ['05-settings-audio', f5], ['06-settings-playback', f6], ['07-skip-alone', f7], ['08-skip-with-controls', f8]]) {
    await pg.setContent(`<!doctype html><html><head><meta charset="utf-8"><style>${css}</style></head><body><div class="screen">${fn()}<div class="border"></div></div></body></html>`);
    await pg.evaluate(() => document.fonts.ready); await pg.screenshot({ path: path.join(OUT, `${n}.png`) });
  }
  await b.close();
})();
