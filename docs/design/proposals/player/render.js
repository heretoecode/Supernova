// Player re-tick (owner, 12 Nov 2026), built from docs/design/RULES.md.
// 1 The HUD in the first seconds of an episode, Chapters strip open: age rating with reasons alone in the top left
//   (fades after 6-8 s); title block in its fixed place (logo, title line, metadata row as on Details); the seek
//   bubble with the loading ring; chapters renumbered after merging IntroDB parts, each with a picture.
// 2 Playback Settings, Playback tab: Speed, Play mode, Stop playing at, Still watching? as one-row chip choices.
// Made-up episode. Run: FONT_DIR=<roboto woff2 dir> node render.js
const fs = require('fs');
const path = require('path');
const { chromium } = require('playwright');
const fontDir = process.env.FONT_DIR;
const font = w => `@font-face{font-family:R;font-weight:${w};src:url(data:font/woff2;base64,${fs.readFileSync(
  path.join(fontDir, `roboto-latin-${w}-normal.woff2`)).toString('base64')}) format('woff2')}`;
const svg = (d, s = 30) => `<svg width="${s}" height="${s}" viewBox="0 0 24 24" fill="none" stroke="currentColor"
  stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round">${d}</svg>`;
const I = {
  pause: '<path d="M9 5v14M15 5v14"/>',
  cog: '<circle cx="12" cy="12" r="3"/><path d="M12 2v3M12 19v3M4.2 4.2l2.1 2.1M17.7 17.7l2.1 2.1M2 12h3M19 12h3'
    + 'M4.2 19.8l2.1-2.1M17.7 6.3l2.1-2.1"/>',
  chapters: '<path d="M4 6h16M4 12h16M4 18h10"/>',
  eps: '<rect x="3" y="4" width="18" height="12" rx="2"/><path d="M7 20h10"/><path d="M10 8l5 2.5-5 2.5z"/>',
  play: '<path d="M7 4l13 8-13 8z"/>',
  audio: '<path d="M4 9v6h4l5 4V5L8 9z"/><path d="M16 9a4 4 0 0 1 0 6"/>',
  subs: '<rect x="3" y="5" width="18" height="14" rx="2"/><path d="M7 14h4M13 14h4M7 10h10"/>',
  speed: '<path d="M4 16a8 8 0 1 1 16 0"/><path d="M12 16l4-5"/>',
  mode: '<path d="M4 9h13l-3-3M20 15H7l3 3"/>',
  moon: '<path d="M20 14.5A8 8 0 1 1 9.5 4a6.5 6.5 0 0 0 10.5 10.5z"/>',
  ask: '<circle cx="12" cy="12" r="9"/><path d="M9.5 9.5a2.5 2.5 0 1 1 3.5 2.3c-.6.3-1 .9-1 1.6v.6M12 17h.1"/>',
};
const scene = 'radial-gradient(ellipse at 55% 40%,#7a6a58 0%,#3e3a3a 30%,#1d1f26 65%,#111216 100%)';
const art = ['#7a5a3a', '#3a5a7a', '#7a3a3a', '#3a6a4a', '#5a4a7a', '#6a6a3a'];
const card = (i, name, line, o) => `<div class="card ${o.focus ? 'focus' : ''}" style="background:radial-gradient(
  ellipse at 50% 35%,${art[i]} 0%,#16181c 80%)"><div class="cs"><b>${name}</b><small>${line}</small>${o.played
  !== undefined ? `<i class="cp"><b style="width:${o.played}%"></b></i>` : ''}</div></div>`;
const ticks = [0, 2.6, 15, 17.5, 47.4, 100];
const hud = `<div class="video"></div><div class="shade"></div>
  <div class="rating"><span class="age big">15</span><span>Strong language, violence</span></div>
  <div class="block"><div class="logo">MARSHALS</div><div class="tline">Season 1 · Episode 5 · Wind River</div>
  <div class="meta"><span class="age">15</span>2026 · 46 min · Drama, Crime, Action &amp; Adventure</div></div>
  <div class="bubble"><b>11:52</b><span><i class="ring"></i>Chapter 2 · loading</span></div>
  <div class="bar"><span class="gb">${svg(I.pause, 34)}</span><div class="track">
  ${ticks.slice(1, -1).map(t => `<i class="tk" style="left:${t}%"></i>`).join('')}
  <i class="played" style="width:26%"></i><i class="lit" style="left:17.5%;width:29.9%"></i>
  <i class="dot" style="left:26%"></i></div><span class="gb wide">${svg(I.cog, 30)}Settings</span></div>
  <div class="tabs"><span class="tab line">${svg(I.chapters, 28)}Chapters</span><span class="tab">${svg(I.eps, 28)}
  Episodes</span></div>
  <div class="strip">${card(0, 'Recap', 'From 0:00', { played: 100 })}${card(1, 'Chapter 1', 'From 1:06',
    { played: 100 })}${card(2, 'Intro', 'From 6:13', { played: 100 })}${card(3, 'Chapter 2',
    'Playing · from 7:13', { played: 40, focus: true })}${card(4, 'Chapter 3', 'From 19:40', {})}
  ${card(5, 'Credits', 'From 41:48', {})}</div>`;

const chips = (list, on) => `<span class="chips">${list.map(t => `<i class="${t === on ? 'on' : ''}">${t}</i>`)
  .join('')}</span>`;
const prow = (ic, name, list, on, focus) => `<div class="prow ${focus ? 'focus' : ''}">${svg(I[ic], 32)}<span
  class="pn">${name}</span>${chips(list, on)}</div>`;
const panel = `<div class="video dimv"></div>
  <div class="panel"><div class="ptabs"><span class="tab line">${svg(I.play, 28)}Playback</span><span class="tab">
  ${svg(I.audio, 28)}Audio</span><span class="tab">${svg(I.subs, 28)}Subtitles</span></div>
  ${prow('speed', 'Speed', ['0.75×', '1×', '1.25×', '1.5×', '2×'], '1×')}
  ${prow('mode', 'Play mode', ['Off', 'Repeat this', 'Play next', 'Shuffle'], 'Play next')}
  ${prow('moon', 'Stop playing at', ['Off', '17:32', '18:02', '18:32', 'End of episode'], 'Off', true)}
  ${prow('ask', 'Still watching?', ['Off', '2 episodes', '3 episodes', '2 hours', '3 hours'], '3 episodes')}
  </div>`;

const css = `${font(400)}${font(500)}${font(700)}*{box-sizing:border-box;margin:0}
body{width:1920px;height:1080px;overflow:hidden;font-family:R;color:#fff;background:#000;position:relative}
.video{position:absolute;inset:0;background:${scene}}.dimv{filter:brightness(.55)}
.shade{position:absolute;inset:0;background:linear-gradient(0deg,rgba(8,9,12,.92) 0%,rgba(8,9,12,.75) 38%,
rgba(8,9,12,0) 62%),linear-gradient(90deg,rgba(8,9,12,.55) 0%,rgba(8,9,12,0) 45%)}
.rating{position:absolute;left:72px;top:56px;display:flex;align-items:center;gap:18px;font-size:28px;color:#e8edf6}
.age{display:inline-flex;align-items:center;justify-content:center;min-width:44px;height:38px;padding:0 10px;
border-radius:8px;font-size:24px;font-weight:700;box-shadow:inset 0 0 0 2px rgba(255,255,255,.75)}
.age.big{min-width:60px;height:52px;font-size:32px;border-radius:10px}
.block{position:absolute;left:72px;top:230px}.logo{font-size:76px;font-weight:500;letter-spacing:8px;color:#f1ead8}
.tline{font-size:32px;font-weight:500;margin-top:12px}
.meta{display:flex;align-items:center;gap:14px;font-size:28px;margin-top:12px;color:#e8edf6}
.bubble{position:absolute;left:592px;top:478px;transform:translateX(-50%);padding:12px 22px;border-radius:16px;
background:rgba(22,24,28,.9);text-align:center}.bubble b{display:block;font-size:34px;font-weight:500}
.bubble span{display:flex;align-items:center;gap:10px;font-size:22px;color:#C9D3E6;margin-top:2px}
.ring{width:20px;height:20px;border-radius:50%;border:3px solid rgba(255,255,255,.25);border-top-color:#fff}
.bar{position:absolute;left:72px;right:72px;top:572px;display:flex;align-items:center;gap:26px}
.gb{display:inline-flex;align-items:center;justify-content:center;gap:12px;width:76px;height:72px;border-radius:20px;
background:rgba(22,24,28,.72);font-size:28px;font-weight:500}.gb.wide{width:auto;padding:0 28px}
.track{position:relative;flex:1;height:8px;border-radius:4px;background:rgba(255,255,255,.25)}
.played{position:absolute;left:0;top:0;bottom:0;border-radius:4px;background:#fff}
.lit{position:absolute;top:-3px;height:14px;border-radius:7px;background:rgba(255,255,255,.35)}
.tk{position:absolute;top:-10px;width:3px;height:28px;background:rgba(255,255,255,.8);border-radius:2px}
.dot{position:absolute;top:50%;width:26px;height:26px;margin:-13px 0 0 -13px;border-radius:50%;background:#fff}
.tabs{position:absolute;left:72px;top:672px;display:flex;gap:10px}
.tab{position:relative;display:inline-flex;align-items:center;gap:12px;height:56px;padding:0 20px;font-size:28px;
font-weight:500;color:#8f98a8}.tab.line{color:#fff}.tab.line:after{content:'';position:absolute;left:20px;right:20px;
bottom:-4px;height:4px;border-radius:2px;background:#fff}
.strip{position:absolute;left:72px;top:766px;display:flex;gap:24px;align-items:center}
.card{position:relative;flex:none;width:400px;height:225px;border-radius:16px;
box-shadow:inset 0 0 0 2px rgba(255,255,255,.6),inset 0 0 18px 4px rgba(0,0,0,.45)}
.card.focus{width:440px;height:248px;box-shadow:0 0 0 5px #fff,inset 0 0 18px 4px rgba(0,0,0,.45),
0 14px 34px rgba(0,0,0,.55)}
.cs{position:absolute;left:0;right:0;bottom:0;padding:16px 18px 16px;border-radius:0 0 16px 16px;
background:linear-gradient(0deg,rgba(10,11,14,.85),rgba(10,11,14,0))}.cs b{display:block;font-size:27px;
font-weight:500}.cs small{display:block;font-size:21px;color:#C9D3E6;margin-top:2px}
.cp{display:block;height:5px;border-radius:3px;background:rgba(255,255,255,.25);margin-top:10px}
.cp b{display:block;height:5px;border-radius:3px;background:#fff}
.panel{position:absolute;right:56px;top:56px;padding:22px 22px 26px;border-radius:26px;background:rgba(22,24,28,.9);
box-shadow:0 30px 80px rgba(0,0,0,.6)}
.ptabs{display:flex;gap:10px;border-bottom:2px solid rgba(255,255,255,.14);padding:4px 8px 12px;margin-bottom:16px}
.ptabs .tab.line:after{bottom:-14px}
.prow{display:flex;align-items:center;gap:20px;height:88px;padding:0 24px;border-radius:18px;
box-shadow:inset 0 -2px 0 rgba(255,255,255,.08)}.prow:last-child{box-shadow:none}
.prow svg{color:#C9D3E6;flex:none}.pn{width:250px;font-size:29px;font-weight:500;flex:none}
.prow.focus{background:#F4F6FB;color:#0B0D12;box-shadow:none}.prow.focus svg{color:#0B0D12}
.chips{display:flex;gap:6px;font-size:26px}.chips i{font-style:normal;padding:10px 18px;border-radius:14px;
white-space:nowrap}.chips i.on{background:#454B55}.prow.focus .chips i.on{background:#0B0D12;color:#fff}`;
(async () => {
  const browser = await chromium.launch();
  const tab = await browser.newPage({ viewport: { width: 1920, height: 1080 } });
  for (const [f, body] of [['01-hud-start-chapters.png', hud], ['02-playback-settings.png', panel]]) {
    await tab.setContent(`<html><head><style>${css}</style></head><body>${body}</body></html>`, { waitUntil: 'load' });
    await tab.evaluate(() => document.fonts.ready);
    await tab.screenshot({ path: path.join(__dirname, f) });
  }
  await browser.close();
})();
