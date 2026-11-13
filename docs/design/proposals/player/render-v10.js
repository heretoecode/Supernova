// v10 (owner, 13 Nov 2026): the age rating with its reasons on its own line under the metadata row (the whole line
// fades, nothing slides); Preparing playback with the block at the top left; chapter row centred between the tab line
// and the bottom; Playback tab as one plain list plus the For This Series save row.
// v9 (owner, 12 Nov 2026): title block at the top left, level with the clock (top right); at the start the age
// rating's reasons sit beside the badge in the metadata row, fading after 8 s.
// v8 (owner, 12 Nov 2026): twice the gap between the bar and the tab row; with Chapters open the whole group sits
// higher so the focused card clears the bottom edge (preview a little smaller to keep clear of the title block).
// v7 (owner, 12 Nov 2026): one loading ring in the centre of the screen (the time box never has one); time box and
// preview card share one layout; panel section headings Title Case, centred, a little larger; a light frame round
// each picture so its edge shows on dark screens (not part of the design).
// v6 (owner, 12 Nov 2026): tabs always shown (strip only when focused); seek info laid out the same with or without
// a preview picture, centred over the dot; dot 50% larger; strip a little lower; panel section headings centred on
// their line.
// v5 (owner, 12 Nov 2026): timestamps on one line with a dot; preview shows the ring without the word "loading";
// controls lower, more room between the tab line and the focused card, half the gap below; controls with the strip
// closed; panel: room between the tab line and the first heading, larger headings, even gaps, straight row lines;
// focus on Subtitle delay (real subtitles stay on).
// v4 (owner, 12 Nov 2026): controls lower; seek preview laid out like a row card (time and chapter inside on the
// shade); running time and end time back beside Settings; panel section headings as words plus a line (menu group
// style); Play mode Stop after this · Play next · Repeat this · Repeat all (Folder and Binge merged).
// v3 (owner, 12 Nov 2026): Preparing playback with the same title block and the app's ring spinner; controls moved
// down so the seek bubble can hold a preview picture (picture beside time and chapter); Playback Settings with every
// current setting in sections (Subtitles: selection incl. delay, appearance with the sample line, for this series;
// Audio: selection, for this series, adjustment; Playback: picture, playback).
// v2: Chapters/Episodes tab row has the line under it; Playback Settings in use order Subtitles · Audio · Playback,
// all three tabs shown (stacked), with a sample subtitle line on the Subtitles tab.
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
  minus: '<path d="M6 12h12"/>',
  plus: '<path d="M12 6v12M6 12h12"/>',
  chev: '<path d="M9 6l6 6-6 6"/>',
  dl: '<path d="M12 4v11M7 10l5 5 5-5M5 20h14"/>',
  size: '<path d="M4 18L9 6l5 12M6 14h6M15 18l3-7 3 7M16 16h4"/>',
  pos: '<path d="M12 4v16M8 8l4-4 4 4M8 16l4 4 4-4"/>',
  delay: '<circle cx="12" cy="13" r="8"/><path d="M12 9v4l2 2M10 2h4"/>',
  series: '<rect x="3" y="5" width="18" height="12" rx="2"/><path d="M8 21h8M9 9l2 2 4-4"/>',
  night: '<path d="M20 14.5A8 8 0 1 1 9.5 4a6.5 6.5 0 0 0 10.5 10.5z"/><path d="M4 20h6"/>',
  tint: '<path d="M12 3l6 8a6 6 0 1 1-12 0z"/>',
  outline: '<path d="M5 19L10 5h4l5 14M7.5 13h9"/>',
  bg: '<rect x="3" y="8" width="18" height="9" rx="2"/>',
  opacity: '<circle cx="12" cy="12" r="9"/><path d="M12 3v18"/>',
  eq: '<path d="M4 21v-7M4 10V3M12 21v-9M12 8V3M20 21v-5M20 12V3M2 14h4M10 8h4M18 16h4"/>',
  boost: '<path d="M4 9v6h4l5 4V5L8 9z"/><path d="M16 8a6 6 0 0 1 0 8M19 5a10 10 0 0 1 0 14"/>',
  spatial: '<circle cx="12" cy="12" r="3"/><path d="M6 6a8.5 8.5 0 0 0 0 12M18 6a8.5 8.5 0 0 1 0 12"/>',
  pic: '<rect x="3" y="5" width="18" height="14" rx="2"/><path d="M3 15l5-5 4 4 3-3 6 6"/>',
  skip: '<path d="M5 5l8 7-8 7zM14 5l8 7-8 7z"/>',
  ask: '<circle cx="12" cy="12" r="9"/><path d="M9.5 9.5a2.5 2.5 0 1 1 3.5 2.3c-.6.3-1 .9-1 1.6v.6M12 17h.1"/>',
};
const scene = 'radial-gradient(ellipse at 55% 40%,#7a6a58 0%,#3e3a3a 30%,#1d1f26 65%,#111216 100%)';
const art = ['#7a5a3a', '#3a5a7a', '#7a3a3a', '#3a6a4a', '#5a4a7a', '#6a6a3a'];
const card = (i, name, line, o) => `<div class="card ${o.focus ? 'focus' : ''}" style="background:radial-gradient(
  ellipse at 50% 35%,${art[i]} 0%,#16181c 80%)"><div class="cs"><b>${name}</b><small>${line}</small>${o.played
  !== undefined ? `<i class="cp"><b style="width:${o.played}%"></b></i>` : ''}</div></div>`;
const ticks = [0, 2.6, 15, 17.5, 47.4, 100];
const hud = `<div class="video"></div><div class="shade"></div>
  <div class="block"><div class="logo">MARSHALS</div><div class="tline">Season 1 · Episode 5 · Wind River</div>
  <div class="meta"><span class="age">15</span>2026 · 46 min · Drama, Crime, Action &amp; Adventure</div></div>
  <div class="clock">21:40</div>
  <div class="pcard"><div class="cs"><b>11:52</b><small>Chapter 2</small></div></div>
  <div class="bar"><span class="gb">${svg(I.pause, 34)}</span><div class="track">
  ${ticks.slice(1, -1).map(t => `<i class="tk" style="left:${t}%"></i>`).join('')}
  <i class="played" style="width:26%"></i><i class="lit" style="left:17.5%;width:29.9%"></i>
  <i class="dot" style="left:26%"></i></div><span class="times">46:12 · Ends 18:14</span>
  <span class="gb wide">${svg(I.cog, 30)}Settings</span></div>
  <div class="tabs prow2"><span class="tab line">${svg(I.chapters, 28)}Chapters</span><span class="tab">${svg(I.eps, 28)}
  Episodes</span></div>
  <div class="strip">${card(0, 'Recap', 'From 0:00', { played: 100 })}${card(1, 'Chapter 1', 'From 1:06',
    { played: 100 })}${card(2, 'Intro', 'From 6:13', { played: 100 })}${card(3, 'Chapter 2',
    'Playing · from 7:13', { played: 40, focus: true })}${card(4, 'Chapter 3', 'From 19:40', {})}
  ${card(5, 'Credits', 'From 41:48', {})}</div>`;

const preparing = `<div class="frame"><div class="video" style="background:radial-gradient(ellipse at 72% 40%,
  #5a6a78 0%,#2c343c 40%,#15181c 80%)"></div><div class="shade"></div>
  <div class="block"><div class="logo">MARSHALS</div><div class="tline">Season 1 · Episode 5 · Wind River</div>
  <div class="meta"><span class="age">15</span>2026 · 46 min · Drama, Crime, Action &amp; Adventure</div>
  <div class="rline">Strong language, violence</div>
  <div class="prep"><i class="ring big"></i>Preparing playback…</div></div><div class="clock">21:40</div></div>`;
const chips = (list, on) => `<span class="chips">${list.map(t => `<i class="${t === on ? 'on' : ''}">${t}</i>`)
  .join('')}</span>`;
const prow = (ic, name, list, on, focus) => `<div class="prow ${focus ? 'focus' : ''}">${svg(I[ic], 32)}<span
  class="pn">${name}</span>${chips(list, on)}</div>`;
const vrow = (ic, name, value, focus, pills) => `<div class="prow ${focus ? 'focus' : ''}">${svg(I[ic], 32)}<span
  class="pn">${name}</span><span class="val">${value}${(pills || []).map(p => `<em>${p}</em>`).join('')}</span>
  <span class="chv">${svg(I.chev, 28)}</span></div>`;
const step = (ic, name, value, focus) => `<div class="prow ${focus ? 'focus' : ''}">${svg(I[ic], 32)}<span
  class="pn">${name}</span><span class="stp">${svg(I.minus, 26)}<b>${value}</b>${svg(I.plus, 26)}</span></div>`;
const sw = (ic, name, on, focus) => `<div class="prow ${focus ? 'focus' : ''}">${svg(I[ic], 32)}<span
  class="pn wide">${name}</span><span class="sw ${on ? 'on' : ''}"><i></i></span></div>`;
const TABS = [['subs', 'Subtitles'], ['audio', 'Audio'], ['play', 'Playback']];
const ptabs = open => `<div class="ptabs">${TABS.map(([ic, t]) => `<span class="tab ${t === open ? 'line' : ''}">
  ${svg(I[ic], 28)}${t}</span>`).join('')}</div>`;
const frame = (open, rows, sample, h) => `<div class="frame" style="height:${h || 1080}px"><div class="video dimv"></div>${sample
  ? '<div class="sample">I grew up on this land. I know every road out of here.</div>' : ''}
  <div class="panel">${ptabs(open)}${rows}</div></div>`;
const sec = (ic, t) => `<div class="psec"><span>${t}</span><i></i></div>`;
const dots = (list, on) => `<span class="dots">${list.map(c => `<i class="${c === on ? 'on' : ''}"
  style="background:${c}"></i>`).join('')}</span>`;
const drow = (ic, name, focus) => `<div class="prow ${focus ? 'focus' : ''}">${svg(I[ic], 32)}<span class="pn">${name}
  </span>${dots(['#ffffff', '#f5d547', '#7fd6f0', '#8fe39a', '#f2a2c8'], '#f5d547')}</div>`;
const panel = frame('Subtitles', `${sec('subs', 'Subtitle Selection')}${sw('subs', 'Subtitles', true)}
  ${vrow('subs', 'Subtitle track', 'English', false, ['Original', 'SDH'])}
  ${vrow('dl', 'Download subtitles', 'OpenSubtitles')}
  ${step('delay', 'Subtitle delay', '0.0 s', true)}
  ${sec('size', 'Subtitle Appearance')}
  ${prow('size', 'Text size', ['Small', 'Medium', 'Large', 'Extra large'], 'Medium')}
  ${drow('tint', 'Text colour')}
  ${step('pos', 'Vertical position', '10% up')}
  ${sw('outline', 'Outline', true)}
  ${sw('bg', 'Background', false)}
  ${prow('opacity', 'Background opacity', ['25%', '50%', '75%', '100%'], '50%')}
  ${sec('series', 'For This Series')}${sw('series', 'Use these subtitles for all of Marshals', true)}`, true, 1640)
const playbackTab = frame('Playback', `<div class="gap"></div>
  ${prow('pic', 'Format', ['Fit', 'Fill', 'Zoom', 'Stretch'], 'Fit')}
  ${prow('speed', 'Speed', ['0.75×', '1×', '1.25×', '1.5×', '2×'], '1×')}
  ${prow('moon', 'Stop playing at', ['Off', '17:32', '18:02', '18:32', 'End of episode'], 'Off', true)}
  ${prow('ask', 'Still watching?', ['Off', '2 episodes', '3 episodes', '2 hours', '3 hours'], '3 episodes')}
  ${prow('mode', 'Play mode', ['Stop after this', 'Play next', 'Repeat this', 'Repeat all'], 'Play next')}
  ${prow('skip', 'Skip intros and credits', ['Off', 'Show button', 'Automatic'], 'Show button')}
  ${sec('series', 'For This Series')}${sw('series', 'Use this format and skipping for all of Marshals', true)}`);

const css = `${font(400)}${font(500)}${font(700)}*{box-sizing:border-box;margin:0}
body{width:1920px;overflow:hidden;font-family:R;color:#fff;background:#000;position:relative}
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
.dot{position:absolute;top:50%;width:39px;height:39px;margin:-19.5px 0 0 -19.5px;border-radius:50%;background:#fff}
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
.frame{position:relative;width:1920px;height:1080px;overflow:hidden}.frame+.frame{border-top:8px solid #000}
.prow2{border-bottom:2px solid rgba(255,255,255,.14);right:72px;padding-bottom:6px}.prow2 .tab.line:after{bottom:-8px}
.val{display:flex;align-items:center;gap:10px;font-size:27px;margin-left:auto}.val em{font-style:normal;font-size:21px;
padding:4px 12px;border-radius:12px;box-shadow:inset 0 0 0 2px rgba(255,255,255,.3)}
.prow.focus .val em{box-shadow:inset 0 0 0 2px rgba(11,13,18,.35)}.chv{display:flex;color:#C9D3E6}
.prow .chv svg{color:inherit}.stp{margin-left:auto;display:flex;align-items:center;gap:22px}.stp b{font-size:27px;
font-weight:500;min-width:110px;text-align:center}.pn.wide{width:auto;flex:1}
.sw{width:76px;height:42px;border-radius:21px;background:#454B55;position:relative;margin-left:auto;flex:none}
.sw i{position:absolute;top:5px;left:5px;width:32px;height:32px;border-radius:50%;background:#C9D3E6}
.sw.on{background:#F4F6FB}.sw.on i{left:39px;background:#0B0D12}
.sample{position:absolute;left:0;right:900px;bottom:120px;text-align:center;font-size:40px;font-weight:500;
text-shadow:0 2px 6px rgba(0,0,0,.9)}
.panel{min-width:1060px}
.chips{display:flex;margin-left:auto;gap:6px;font-size:26px}.chips i{font-style:normal;padding:10px 18px;border-radius:14px;
white-space:nowrap}.chips i.on{background:#454B55}.prow.focus .chips i.on{background:#0B0D12;color:#fff}`;
const CSS2 = `
.rline{font-size:28px;margin-top:10px;color:#fff;font-weight:500;padding-left:61px}
.gap{height:12px}.frame{border-top:none !important}

.bar{top:600px !important}.tabs{top:702px !important}.strip{top:799px !important}
.centre{position:absolute;left:50%;top:50%;transform:translate(-50%,-50%)}
.ring.huge{display:block;width:64px;height:64px;border-width:5px}
body:after{content:"";position:fixed;inset:0;border:6px solid #8a919c;z-index:99;pointer-events:none}
.tab.dullt{color:#C9D3E6;position:relative}.tab.dullt:after{content:'';position:absolute;left:20px;right:20px;
bottom:-8px;height:4px;border-radius:2px;background:rgba(255,255,255,.3)}
.bar.low{top:874px !important}.tabs.low{top:976px !important}
.pcard.nothumb{width:auto;height:auto;background:rgba(22,24,28,.9);box-shadow:none;transform:translateX(-50%);
left:487px}.pcard.nothumb .cs{position:static;background:none;padding:14px 22px}.pcard.low{top:768px !important}
.pcard{position:absolute;left:327px;top:406px;width:320px;height:180px;border-radius:14px;
background:radial-gradient(ellipse at 60% 40%,#4f7a5a 0%,#1a2420 85%);
box-shadow:inset 0 0 0 2px rgba(255,255,255,.6),inset 0 0 14px 3px rgba(0,0,0,.45)}
.pcard .cs b{font-size:30px}.pcard .cs small{display:flex;align-items:center;gap:8px}
.times{font-size:26px;font-weight:500;white-space:nowrap}
.psec{display:flex !important;align-items:center;gap:16px;font-size:26px !important;color:#fff !important;
padding:24px 24px 10px !important}.ptabs+.psec{padding-top:30px !important}
.prow{position:relative;box-shadow:none !important}.prow:after{content:'';position:absolute;left:24px;right:24px;
bottom:0;height:2px;background:rgba(255,255,255,.08)}.prow.focus:after,.prow:last-child:after,
.prow:has(+ .psec):after,.prow:has(+ .prow.focus):after{display:none}
.psec i{flex:1;height:2px;background:rgba(255,255,255,.18)}
.psec{justify-content:center;font-size:28px !important}.psec:before{content:'';flex:1;height:2px;background:rgba(255,255,255,.18)}

.logo{font-size:60px}.block{top:52px}
.clock{position:absolute;right:72px;top:58px;font-size:34px;font-weight:500;color:#e8edf6}
.why{color:#fff;font-weight:500;margin-right:-2px}
.bubble{left:600px;top:420px;transform:translateX(-50%);display:flex;align-items:center;gap:18px;padding:12px;
text-align:left}.bubble b{font-size:34px}.prev{width:256px;height:144px;border-radius:10px;flex:none;
background:radial-gradient(ellipse at 60% 40%,#4f7a5a 0%,#1a2420 85%);
box-shadow:inset 0 0 0 2px rgba(255,255,255,.6),inset 0 0 14px 3px rgba(0,0,0,.45)}.bubble div{padding-right:14px}
.bar{top:604px}.tabs{top:700px}.strip{top:790px}
.prep{display:flex;align-items:center;gap:18px;font-size:28px;color:#C9D3E6;margin-top:46px}
.ring.big{width:34px;height:34px;border-width:4px}
.pn{width:330px;white-space:nowrap}
.psec{display:flex;align-items:center;gap:14px;font-size:25px;font-weight:500;color:#C9D3E6;padding:30px 24px 12px}
.ptabs+.psec{padding-top:8px}.psec svg{color:#C9D3E6}
.dots{display:flex;gap:16px;margin-left:auto}.dots i{width:36px;height:36px;border-radius:50%;
box-shadow:inset 0 0 0 2px rgba(0,0,0,.25)}.dots i.on{box-shadow:0 0 0 3px #16181C,0 0 0 6px #fff}
.prow.focus .dots i.on{box-shadow:0 0 0 3px #F4F6FB,0 0 0 6px #0B0D12}
.prow:has(+ .psec){box-shadow:none}
`;
(async () => {
  const browser = await chromium.launch();
  const tab = await browser.newPage({ viewport: { width: 1920, height: 1080 } });
  for (const [f, body, h] of [['23-v10-preparing.png', preparing, 1080],
    ['24-v10-chapters-open.png', hud, 1080], ['25-v10-playback-tab.png', playbackTab, 1080]]) {
    await tab.setViewportSize({ width: 1920, height: h });
    await tab.setContent(`<html><head><style>${css}${CSS2}body{height:${h}px}</style></head><body>${body}</body></html>`, { waitUntil: 'load' });
    await tab.evaluate(() => document.fonts.ready);
    await tab.screenshot({ path: path.join(__dirname, f) });
  }
  await browser.close();
})();
