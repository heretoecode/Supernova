// Proposals from the 0.154 Shield check (owner, 4 Nov 2026): hero readability + logo placement, live subtitle
// preview, in-panel subtitle search, colour ring, playback HUD. Run: FONT_DIR=<roboto woff2 dir> node render.js
const fs = require('fs'), path = require('path'), { chromium } = require('playwright');
const out = __dirname, fontDir = process.env.FONT_DIR;
const font = w => `@font-face{font-family:R;font-weight:${w};src:url(data:font/woff2;base64,${fs.readFileSync(path.join(fontDir, `roboto-latin-${w}-normal.woff2`)).toString('base64')}) format('woff2')}`;
const P = { info: '<circle cx="12" cy="12" r="9"/><path d="M12 11v6M12 7.5v.5"/>', play: '<path d="M8 5l11 7-11 7z" fill="currentColor"/>',
  pause: '<path d="M8 5v14M16 5v14"/>', gear: '<circle cx="12" cy="12" r="3.2"/><path d="M12 2.5v3M12 18.5v3M2.5 12h3M18.5 12h3M5.3 5.3l2.1 2.1M16.6 16.6l2.1 2.1M5.3 18.7l2.1-2.1M16.6 7.4l2.1-2.1"/>',
  cc: '<rect x="3" y="5" width="18" height="14" rx="2"/><path d="M10 10.2a2.2 2.2 0 1 0 0 3.6M16.5 10.2a2.2 2.2 0 1 0 0 3.6"/>',
  sound: '<path d="M4 9h4l5-4v14l-5-4H4z"/><path d="M16 9a4 4 0 0 1 0 6"/>', back: '<path d="M15 5l-7 7 7 7"/>', search: '<circle cx="11" cy="11" r="6.5"/><path d="M16 16l4.5 4.5"/>' };
const ic = (k, s = 32) => `<svg width="${s}" height="${s}" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round">${P[k]}</svg>`;
const nav = `<div class="nav"><span class="brand">SUPERNOVA</span><span class="on">Home</span><span>Movies</span><span>TV Shows</span><span>Documentaries</span><span class="sp"></span><span>Network &amp; Files</span><span class="clock">21:30</span></div>`;
const art = {
  kingdom: 'linear-gradient(170deg,#f3a6c8 0%,#f6c47a 38%,#f4d35e 55%,#c9a24a 75%,#6b5a2c 100%)',
  mayday: 'repeating-linear-gradient(120deg,#4f6b46 0 46px,#3c5236 46px 92px,#b5302e 92px 104px,#4a6342 104px 150px)',
  robot: 'linear-gradient(90deg,#e8355a 0%,#e02d55 60%,#2b1a22 100%)' };
const logo = { kingdom: `<div class="lg k" style="height:70px">K I N G D O M</div>`, mayday: `<div class="lg m" style="height:150px">MAYDAY</div>`,
  robot: `<div class="lg r" style="height:78px">MR. ROBOT</div>` };
const hero = (k, meta, tag, after) => `<div class="hero" style="background:${art[k]}">${after ? '<div class="patch"></div>' : '<div class="fade"></div>'}
  <div class="copy${after ? ' after' : ''}"><div class="logobox${after ? ' bottom' : ''}">${logo[k]}</div><div class="meta">${meta}</div><div class="tag">${tag}</div>
  <div class="btn focus">${ic('info', 26)}<span>More Info</span></div></div><div class="lab">${after ? 'Proposed' : 'Today (0.154)'}</div></div>`;
const heroes = (k, meta, tag) => `${nav}<div class="pair">${hero(k, meta, tag, false)}${hero(k, meta, tag, true)}</div>`;
const sw = (c, sel) => `<span class="sw${sel ? ' sel' : ''}"><i style="background:${c}"></i></span>`;
const row = (icon, label, val, focus) => `<div class="prow${focus ? ' focus' : ''}">${ic(icon, 28)}<span class="pl">${label}</span><span class="pv">${val}</span></div>`;
const tabs = f => `<div class="tabs"><div class="tab${f === 0 ? ' focus' : ''}">${ic('cc', 26)}Subtitles</div><div class="tab${f === 1 ? ' focus' : ''}">${ic('sound', 26)}Audio</div><div class="tab${f === 2 ? ' focus' : ''}">${ic('play', 22)}Playback</div></div>`;
const video = 'linear-gradient(160deg,#3a3f52,#1b1d26 55%,#0d0e13)';
const previewPage = () => `<div class="video" style="background:${video}"></div>
  <div class="sample">This is how your subtitles will look</div><div class="hint">Preview · shown while you change subtitle appearance</div>
  <div class="panel">${tabs(-1)}<div class="grp">Subtitle appearance<i></i></div>
  ${row('cc', 'Text Size', 'Small  |  <b>Medium</b>  |  Large')}
  <div class="prow focus">${ic('cc', 28)}<span class="pl">Text Colour</span><span class="pv sws">${sw('#fff')}${sw('#ffc93c', true)}${sw('#19e6e6')}${sw('#4cc554')}${sw('#f06a9b')}</span></div>
  ${row('cc', 'Vertical Position', '<b>Bottom</b>  |  Middle  |  Top')}${row('cc', 'Outline', '<span class="tg on"><i></i></span>')}
  ${row('cc', 'Background', '<span class="tg"><i></i></span>')}${row('cc', 'Background Opacity', '‹  <b>100%</b>  ›')}</div>`;
const searchPage = () => `<div class="video" style="background:${video}"></div>
  <div class="panel"><div class="ptitle">${ic('back', 26)}Download subtitles</div>
  <div class="searching"><span class="spin"></span><div><div class="st">Searching OpenSubtitles…</div><div class="ss">Dark Matter · S2 E10 · English</div></div></div>
  ${[1, 2, 3, 4].map(() => '<div class="skel"><i></i><b></b></div>').join('')}<div class="foot">Back cancels the search</div></div>`;
const hud = after => `<div class="video" style="background:linear-gradient(170deg,#c9d6f0 0%,#7f93c4 30%,#2c3550 70%,#0b0d14 100%)"></div>
  <div class="htitle">Dark Matter</div><div class="hsub">Season 2 · Episode 10 · No Quiet Life</div>
  <div class="hud${after ? ' after' : ''}"><div class="hb">${ic('pause', 34)}</div>
  <div class="bar"><div class="buf"></div><div class="played"></div><span class="tick" style="left:14%"></span><span class="tick" style="left:15.5%"></span>
  <div class="thumb"></div><div class="bubble"><b>00:17:16</b><span>Chapter 2</span></div></div>
  <div class="hb set">${ic('gear', 30)}<span>Settings</span></div></div><div class="hlab">${after ? 'Proposed: glass buttons, readable time above the dot, white played / grey remaining' : 'Today (0.154)'}</div>`;
const css = `${font(400)}${font(500)}${font(700)}*{box-sizing:border-box;margin:0}body{width:1920px;height:1080px;overflow:hidden;font-family:R;color:#fff;position:relative;background:#05081a}
.nav{position:absolute;left:56px;right:56px;top:24px;height:56px;display:flex;align-items:center;gap:30px;font-size:30px;color:#e8edf8}.nav .brand{margin-right:30px}.nav .sp{flex:1}.nav .on{font-weight:500}
.pair{position:absolute;top:110px;left:40px;right:40px;bottom:40px;display:flex;gap:30px}
.hero{flex:1;border-radius:22px;position:relative;overflow:hidden}.fade{position:absolute;inset:0;background:linear-gradient(90deg,rgba(0,0,0,.42),rgba(0,0,0,0) 58%)}
.patch{position:absolute;inset:0;background:radial-gradient(ellipse 44% 24% at 24% 39%,rgba(0,0,0,.55),rgba(0,0,0,.38) 45%,rgba(0,0,0,.12) 75%,rgba(0,0,0,0) 100%)}
.copy{position:absolute;left:44px;top:40px;bottom:40px;width:60%;display:flex;flex-direction:column}
.logobox{height:240px;display:flex;align-items:flex-start}.logobox.bottom{align-items:flex-end}
.lg{font-weight:700;line-height:1;display:flex;align-items:center}.lg.k{font-size:60px;color:#f4ead0;font-family:Georgia,serif;letter-spacing:2px}.lg.m{font-size:118px;color:#ff3d6e;letter-spacing:-2px}.lg.r{font-size:70px;color:#ff4d6d;-webkit-text-stroke:2px #8b0f2a}
.meta{margin-top:22px;font-size:22px;color:#eef1f7;font-weight:400;text-shadow:0 1px 3px rgba(0,0,0,.6)}.after .meta{font-weight:500;color:#fff;text-shadow:0 1px 6px rgba(0,0,0,.85)}
.tag{margin-top:14px;font-size:22px;line-height:1.4;text-shadow:0 1px 3px rgba(0,0,0,.6);max-width:100%}.after .tag{max-width:78%;text-shadow:0 1px 6px rgba(0,0,0,.85)}
.btn{margin-top:auto;align-self:flex-start;height:58px;padding:0 22px;border-radius:20px;display:flex;align-items:center;gap:10px;font-size:22px;font-weight:500}.btn.focus{background:#F4F6FB;color:#0B0D12}
.lab{position:absolute;right:20px;top:16px;font-size:18px;background:rgba(0,0,0,.55);padding:4px 12px;border-radius:10px}
.video{position:absolute;inset:0}.panel{position:absolute;right:60px;top:70px;width:640px;bottom:70px;border-radius:26px;background:rgba(22,24,28,.97);border:2px solid rgba(255,255,255,.22);padding:26px}
.tabs{display:flex;gap:12px;margin-bottom:8px}.tab{flex:1;height:72px;border-radius:22px;background:#2a2d33;display:flex;align-items:center;justify-content:center;gap:10px;font-size:22px;font-weight:500}.tab.focus{background:#F4F6FB;color:#0B0D12}
.grp{display:flex;align-items:center;gap:14px;font-size:19px;color:#b7bfcc;margin:16px 6px 6px}.grp i{flex:1;height:2px;background:rgba(255,255,255,.22)}
.prow{display:flex;align-items:center;gap:14px;height:74px;padding:0 16px;border-radius:22px;font-size:22px;font-weight:500}.prow.focus{box-shadow:inset 0 0 0 3px #fff}.pl{flex:1}.pv{font-weight:400;font-size:21px;color:#c9d3e6;white-space:pre}.pv b{color:#5aa9ff;font-weight:500}
.sws{display:flex;gap:8px}.sw{width:46px;height:46px;border-radius:50%;display:flex;align-items:center;justify-content:center}.sw i{width:30px;height:30px;border-radius:50%}.sw.sel{box-shadow:inset 0 0 0 3px #fff}
.tg{width:64px;height:34px;border-radius:17px;background:#3a3f48;position:relative;display:inline-block}.tg.on{background:#2fb7d8}.tg i{position:absolute;top:4px;left:4px;width:26px;height:26px;border-radius:50%;background:#fff}.tg.on i{left:34px}
.sample{position:absolute;left:0;width:1220px;bottom:120px;text-align:center;font-size:44px;font-weight:500;color:#ffc93c;text-shadow:-2px -2px 0 #000,2px -2px 0 #000,-2px 2px 0 #000,2px 2px 0 #000}
.hint{position:absolute;left:0;width:1220px;bottom:80px;text-align:center;font-size:18px;color:#c9d3e6}
.ptitle{display:flex;align-items:center;gap:12px;font-size:26px;font-weight:500;margin:4px 6px 18px}
.searching{display:flex;align-items:center;gap:20px;padding:22px;border-radius:20px;background:#22252b;margin-bottom:14px}.st{font-size:23px;font-weight:500}.ss{font-size:19px;color:#c9d3e6;margin-top:6px}
.spin{width:40px;height:40px;border-radius:50%;border:4px solid rgba(255,255,255,.2);border-top-color:#fff;flex:none}
.skel{height:74px;display:flex;flex-direction:column;justify-content:center;gap:10px;padding:0 18px}.skel i{display:block;width:70%;height:16px;border-radius:8px;background:rgba(255,255,255,.1)}.skel b{display:block;width:35%;height:12px;border-radius:6px;background:rgba(255,255,255,.07)}
.foot{position:absolute;bottom:22px;left:32px;font-size:18px;color:#8a93a3}
.htitle{position:absolute;left:80px;top:640px;font-size:64px;font-weight:700}.hsub{position:absolute;left:84px;top:722px;font-size:26px}
.hud{position:absolute;left:70px;right:70px;bottom:90px;height:80px;display:flex;align-items:center;gap:26px}
.hb{height:72px;min-width:72px;padding:0 20px;border-radius:22px;background:rgba(0,0,0,.72);display:flex;align-items:center;justify-content:center;gap:10px;font-size:22px;font-weight:500}
.hud.after .hb{background:rgba(22,24,28,.62);border:2px solid rgba(255,255,255,.22)}
.bar{flex:1;height:8px;border-radius:4px;background:#8ec5ff;position:relative}.hud.after .bar{background:rgba(255,255,255,.22);height:10px;border-radius:5px}
.buf{display:none}.hud.after .buf{display:block;position:absolute;left:0;top:0;bottom:0;width:66%;border-radius:5px;background:rgba(255,255,255,.4)}
.played{position:absolute;left:0;top:0;bottom:0;width:52%;border-radius:4px;background:#fff}
.tick{position:absolute;top:-2px;width:2px;height:12px;background:#777}.hud.after .tick{background:rgba(0,0,0,.7);top:0;height:10px;width:4px}
.thumb{position:absolute;left:52%;top:50%;width:22px;height:22px;margin:-11px 0 0 -11px;border-radius:50%;background:#fff}.hud.after .thumb{width:30px;height:30px;margin:-15px 0 0 -15px;box-shadow:0 0 0 4px rgba(255,255,255,.25)}
.bubble{position:absolute;left:52%;bottom:16px;transform:translateX(-50%);background:#1677d2;border-radius:8px;padding:4px 10px;text-align:center;font-size:16px;line-height:1.25}.bubble b{display:block;font-weight:400}.bubble span{display:block}
.hud.after .bubble{bottom:40px;background:rgba(22,24,28,.92);border:2px solid rgba(255,255,255,.22);border-radius:16px;padding:10px 20px}.hud.after .bubble b{font-size:30px;font-weight:500}.hud.after .bubble span{font-size:19px;color:#c9d3e6;margin-top:2px}
.hlab{position:absolute;right:60px;top:40px;font-size:20px;background:rgba(0,0,0,.6);padding:6px 14px;border-radius:10px}`;
const pages = [
  ['01-hero-kingdom.png', heroes('kingdom', '2025 · Documentary', 'Filmed across five years in a single spectacular location, the lives and fates of four families — leopards, hyenas, wild dogs and lions.')],
  ['02-hero-mayday.png', heroes('mayday', '2026 · Action, Comedy', 'A friendship with major red flags.')],
  ['03-hero-mr-robot.png', heroes('robot', '2015 · Crime, Drama', 'Control is an illusion.')],
  ['04-subtitle-live-preview.png', previewPage()],
  ['05-subtitle-search-in-panel.png', searchPage()],
  ['06-hud-today.png', hud(false)],
  ['07-hud-proposed.png', hud(true)],
];
(async () => { const b = await chromium.launch(); const t = await b.newPage({ viewport: { width: 1920, height: 1080 } });
  for (const [f, body] of pages) { await t.setContent(`<html><head><style>${css}</style></head><body>${body}</body></html>`, { waitUntil: 'load' });
    await t.evaluate(() => document.fonts.ready); await t.screenshot({ path: path.join(out, f) }); } await b.close(); })();
