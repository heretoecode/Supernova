// Settings review (26 Nov 2026): every tab and everything it opens, on today's rules.
// FONT_DIR=<@fontsource/roboto files> node render.js  → tv/*.png (1920 wide, speckled, framed)
const fs = require('fs');
const path = require('path');
const { chromium } = require('playwright');
const F = require('../files-v2/render.js');

const { svg, speckle, hd, keyboard } = F;
const I = Object.assign({}, F.I, {
  playback: '<path d="M7 4l13 8-13 8z"/>',
  audio: '<path d="M4 9v6h4l5 4V5L8 9z"/><path d="M16 8.5a5 5 0 0 1 0 7M18.5 6a8.5 8.5 0 0 1 0 12"/>',
  subs: '<rect x="3" y="5" width="18" height="14" rx="2"/><path d="M7 14h4M13 14h4M7 10h10"/>',
  services: '<rect x="3" y="4" width="18" height="12" rx="2"/><path d="M8 20h8M12 16v4"/>',
  system: '<circle cx="12" cy="12" r="3"/><path d="M12 2v3M12 19v3M2 12h3M19 12h3M4.9 4.9l2.1 2.1M17 17l2.1 2.1M4.9 19.1L7 17M17 7l2.1-2.1"/>',
  about: '<circle cx="12" cy="12" r="9"/><path d="M12 11v6M12 7.5v.5"/>',
  general: '<path d="M4 6h10M18 6h2M4 12h4M12 12h8M4 18h12M20 18h0"/><circle cx="16" cy="6" r="2"/><circle cx="10" cy="12" r="2"/><circle cx="18" cy="18" r="2"/>',
  pause: '<path d="M8 5v14M16 5v14"/>',
  seek: '<rect x="3" y="5" width="8" height="6" rx="1"/><path d="M3 17h18M14 17v-3"/>',
  other: '<path d="M14 4h6v6M20 4l-9 9"/><path d="M18 14v5a1 1 0 0 1-1 1H5a1 1 0 0 1-1-1V7a1 1 0 0 1 1-1h5"/>',
  resume: '<path d="M4 12a8 8 0 1 0 2.3-5.7"/><path d="M4 4v4h4"/><path d="M11 9l4 3-4 3z"/>',
  bookmark: '<path d="M6 3h12v18l-6-4-6 4z"/>',
  rewind: '<path d="M11 6l-7 6 7 6zM20 6l-7 6 7 6z"/>',
  skip: '<path d="M5 5l9 7-9 7zM17 5v14"/>',
  intro: '<path d="M4 5h16v14H4z"/><path d="M10 9l5 3-5 3z"/>',
  recap: '<path d="M4 12a8 8 0 1 0 2.3-5.7"/><path d="M4 4v4h4"/>',
  preview: '<circle cx="12" cy="12" r="9"/><path d="M12 7v5l4 2"/>',
  outro: '<path d="M4 19h16M7 15l5-9 5 9"/>',
  credits: '<path d="M6 4h12M6 9h12M8 14h8M9 19h6"/>',
  picture: '<rect x="3" y="5" width="18" height="12" rx="2"/><path d="M8 21h8"/>',
  refresh: '<path d="M3 12h4l2-5 3 10 2-5h7"/>',
  hdr: '<rect x="3" y="6" width="18" height="12" rx="2"/><path d="M7 10v4M10 10v4M7 12h3M13 10v4h2a2 2 0 0 0 0-4zM18 10v4"/>',
  lang: '<circle cx="12" cy="12" r="9"/><path d="M3 12h18M12 3a14 14 0 0 1 0 18M12 3a14 14 0 0 0 0 18"/>',
  original: '<path d="M12 3l2.6 5.6 6.1.7-4.5 4.2 1.2 6L12 16.5 6.6 19.5l1.2-6-4.5-4.2 6.1-.7z"/>',
  surround: '<circle cx="12" cy="12" r="2"/><path d="M7.8 7.8a6 6 0 0 0 0 8.4M16.2 7.8a6 6 0 0 1 0 8.4M5 5a10 10 0 0 0 0 14M19 5a10 10 0 0 1 0 14"/>',
  virtual: '<path d="M4 14v-2a8 8 0 0 1 16 0v2"/><rect x="3" y="14" width="4" height="6" rx="1"/><rect x="17" y="14" width="4" height="6" rx="1"/>',
  chip: '<rect x="6" y="6" width="12" height="12" rx="2"/><path d="M9 2v4M15 2v4M9 18v4M15 18v4M2 9h4M2 15h4M18 9h4M18 15h4"/>',
  cc: '<rect x="3" y="5" width="18" height="14" rx="2"/><path d="M10 10a2 2 0 1 0 0 4M17 10a2 2 0 1 0 0 4"/>',
  full: '<rect x="3" y="5" width="18" height="14" rx="2"/><path d="M7 15h10"/>',
  translate: '<path d="M4 5h8M8 3v2M6 5c0 4 3 7 6 8M10 5c0 4-3 7-6 8M13 20l4-9 4 9M14.5 17h5"/>',
  ear: '<path d="M7 9a5 5 0 0 1 10 0c0 3-3 4-3 7a3 3 0 0 1-6 0"/><path d="M10 9a2 2 0 0 1 4 0"/>',
  download: '<path d="M12 4v11M7 10l5 5 5-5M5 20h14"/>',
  find: '<circle cx="11" cy="11" r="6"/><path d="M20 20l-4.5-4.5"/>',
  ready: '<circle cx="12" cy="12" r="9"/><path d="M8 12.5l3 3 5-6"/>',
  size: '<path d="M4 7V5h10v2M9 5v14M7 19h4M14 12v-1h6v1M17 11v8M15.5 19h3"/>',
  colour: '<circle cx="12" cy="12" r="9"/><circle cx="8.5" cy="10" r="1.2"/><circle cx="12" cy="7.5" r="1.2"/><circle cx="15.5" cy="10" r="1.2"/>',
  updown: '<path d="M12 3v18M8 7l4-4 4 4M8 17l4 4 4-4"/>',
  outline: '<path d="M6 19L11 5h2l5 14M8.2 13h7.6"/>',
  bg: '<rect x="3" y="8" width="18" height="9" rx="2"/><path d="M7 12.5h10"/>',
  opacity: '<circle cx="12" cy="12" r="9"/><path d="M12 3v18"/>',
  font: '<path d="M5 19L10 5h1l5 14M7 14h7M17 19v-6M15 13h4"/>',
  bars: '<rect x="3" y="4" width="18" height="16" rx="2"/><path d="M3 8h18M3 16h18"/>',
  enc: '<path d="M8 6l-5 6 5 6M16 6l5 6-5 6M14 4l-4 16"/>',
  thumbs: '<rect x="3" y="5" width="8" height="6" rx="1"/><rect x="13" y="5" width="8" height="6" rx="1"/><rect x="3" y="13" width="8" height="6" rx="1"/><rect x="13" y="13" width="8" height="6" rx="1"/>',
  spoiler: '<path d="M3 3l18 18"/><path d="M10.6 6.1A10 10 0 0 1 22 12s-1.3 2.3-3.6 4.2M6.4 7.5C3.6 9.4 2 12 2 12s4 7 10 7c1.4 0 2.7-.4 3.9-1"/>',
  newv: '<circle cx="11" cy="11" r="6"/><path d="M20 20l-4.5-4.5M11 8.5v5M8.5 11h5"/>',
  nfo: '<path d="M14 3H7a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h10a2 2 0 0 0 2-2V8z"/><path d="M14 3v5h5M9 13h6M9 17h4"/>',
  write: '<path d="M14.5 5.5l4 4L8 20H4v-4z"/>',
  details: '<rect x="3" y="4" width="18" height="16" rx="2"/><path d="M7 9h10M7 13h10M7 17h6"/>',
  star: '<path d="M12 3l2.6 5.6 6.1.7-4.5 4.2 1.2 6L12 16.5 6.6 19.5l1.2-6-4.5-4.2 6.1-.7z"/>',
  trend: '<path d="M3 17l6-6 4 4 8-8M15 7h6v6"/>',
  fire: '<path d="M12 3c1 4 5 5 5 10a5 5 0 0 1-10 0c0-3 2-4 2-7 1 1 2 2 3 3 0-2 0-4 0-6z"/>',
  because: '<path d="M12 20s-7-4.4-7-10a4 4 0 0 1 7-2.6A4 4 0 0 1 19 10c0 5.6-7 10-7 10z"/>',
  timer: '<circle cx="12" cy="13" r="8"/><path d="M12 9v4l2 2M9 2h6"/>',
  eyeH: '<path d="M2 12s4-7 10-7 10 7 10 7-4 7-10 7S2 12 2 12z"/><circle cx="12" cy="12" r="3"/>',
  clear: '<path d="M4 7h16M10 11v6M14 11v6M6 7l1 13h10l1-13M9 7V4h6v3"/>',
  searchH: '<circle cx="11" cy="11" r="6"/><path d="M20 20l-4.5-4.5M8 11h6"/>',
  tvw: '<rect x="3" y="5" width="18" height="12" rx="2"/><path d="M10 9l4 2-4 2z"/><path d="M8 21h8"/>',
  flag: '<path d="M5 21V4M5 4h12l-2 4 2 4H5"/>',
  trakt: '<circle cx="12" cy="12" r="9"/><path d="M8 12l3 3 6-6"/>',
  scrobble: '<path d="M4 12h3l2-6 4 12 2-6h5"/>',
  sync: '<path d="M20 12a8 8 0 0 1-14.3 4.9M4 12a8 8 0 0 1 14.3-4.9"/><path d="M20 4v4h-4M4 20v-4h4"/>',
  send: '<path d="M4 12l16-8-6 16-3-7z"/>',
  fetch: '<path d="M12 4v11M7 10l5 5 5-5M5 20h14"/>',
  remove: '<circle cx="12" cy="12" r="9"/><path d="M8 12h8"/>',
  ratings: '<path d="M4 20V10M10 20V4M16 20v-7M22 20H2"/>',
  key: '<circle cx="8" cy="15" r="4"/><path d="M11 12l9-9M17 6l3 3"/>',
  backup: '<path d="M4 7h16v12a1 1 0 0 1-1 1H5a1 1 0 0 1-1-1z"/><path d="M3 4h18v3H3zM10 11h4"/>',
  choose: '<path d="M4 6h2M4 12h2M4 18h2M9 6h11M9 12h11M9 18h11"/>',
  restore: '<path d="M4 12a8 8 0 1 0 2.3-5.7"/><path d="M4 4v4h4M12 8v4l3 2"/>',
  diag: '<path d="M3 12h4l3-7 4 14 3-7h4"/>',
  log: '<path d="M14 3H7a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h10a2 2 0 0 0 2-2V8z"/><path d="M14 3v5h5M9 13h6M9 17h6"/>',
  save: '<path d="M5 3h11l3 3v13a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2z"/><path d="M8 3v5h7M8 21v-7h8v7"/>',
  adv: '<path d="M12 2l3 6 7 1-5 5 1 7-6-3-6 3 1-7-5-5 7-1z"/>',
  projector: '<rect x="2" y="8" width="20" height="9" rx="2"/><circle cx="8" cy="12.5" r="2.5"/><path d="M14 11h5M14 14h5"/>',
  cpu: '<rect x="6" y="6" width="12" height="12" rx="2"/><path d="M9 2v4M15 2v4M9 18v4M15 18v4M2 9h4M2 15h4M18 9h4M18 15h4"/>',
  parser: '<path d="M4 6h16M4 12h10M4 18h16"/>',
  buffer: '<rect x="3" y="9" width="18" height="6" rx="3"/><path d="M3 12h10"/>',
  frame: '<rect x="4" y="4" width="16" height="16" rx="2"/><path d="M4 9h16M9 4v16"/>',
  speaker: '<rect x="6" y="3" width="12" height="18" rx="2"/><circle cx="12" cy="14" r="3"/><circle cx="12" cy="7.5" r="1"/>',
  speed: '<path d="M4 16a8 8 0 1 1 16 0"/><path d="M12 16l4-5"/>',
  downmix: '<path d="M4 6h6l4 6h6M4 18h6l4-6"/>',
  delay: '<circle cx="12" cy="12" r="9"/><path d="M12 7v5h4"/>',
  news: '<path d="M4 5h13v14H6a2 2 0 0 1-2-2z"/><path d="M17 9h3v8a2 2 0 0 1-2 2M8 9h5M8 13h5"/>',
  version: '<circle cx="6" cy="6" r="2"/><circle cx="6" cy="18" r="2"/><circle cx="18" cy="8" r="2"/><path d="M6 8v8M18 10c0 4-6 3-10 6"/>',
  all: '<path d="M4 6h16M4 12h16M4 18h10"/>',
  heartc: '<path d="M12 20s-7-4.4-7-10a4 4 0 0 1 7-2.6A4 4 0 0 1 19 10c0 5.6-7 10-7 10z"/>',
  licence: '<path d="M14 3H7a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h10a2 2 0 0 0 2-2V8z"/><path d="M14 3v5h5"/><circle cx="12" cy="14" r="2.5"/>',
  more: '<circle cx="5" cy="12" r="1.2"/><circle cx="12" cy="12" r="1.2"/><circle cx="19" cy="12" r="1.2"/>',
  person: '<circle cx="12" cy="8" r="4"/><path d="M4 21a8 8 0 0 1 16 0"/>',
  swap: '<path d="M4 8h14l-4-4M20 16H6l4 4"/>',
  out: '<path d="M10 4H5a1 1 0 0 0-1 1v14a1 1 0 0 0 1 1h5M15 8l4 4-4 4M19 12H9"/>',
  qr: '<rect x="3" y="3" width="7" height="7"/><rect x="14" y="3" width="7" height="7"/><rect x="3" y="14" width="7" height="7"/><path d="M14 14h3v3M21 14v7h-4M14 21v-4"/>',
  restart: '<path d="M20 12a8 8 0 1 1-2.3-5.7"/><path d="M20 4v4h-4"/>',
  stop: '<rect x="6" y="6" width="12" height="12" rx="2"/>',
  move: '<path d="M12 4v16M8 8l4-4 4 4M8 16l4 4 4-4"/>',
  hearing: '<path d="M7 9a5 5 0 0 1 10 0c0 3-3 4-3 7a3 3 0 0 1-6 0"/><path d="M10 9a2 2 0 0 1 4 0M3 3l18 18"/>',
});

const css = `
.tabs span{gap:12px}
.ctl{flex:none;display:flex;align-items:center;justify-content:flex-end;gap:10px;min-width:360px;font-size:28px;
 white-space:nowrap}
.ctl .val{font-weight:500}
.ctl .mute{color:#c9d3e6;font-weight:400;font-size:24px}
.r.on .ctl .mute{color:#3a4256}
.ctl .chips{display:flex;gap:6px}
.ctl .chips span{height:52px;padding:0 22px;border-radius:20px;display:flex;align-items:center;font-size:26px}
.ctl .chips span.c{background:rgba(255,255,255,.18)}
.r.on .ctl .chips span.c{background:#0b0d12;color:#fff}
.ctl .step{display:flex;align-items:center;gap:14px;font-weight:500}
.ctl .step i{font-style:normal;width:28px;display:flex;justify-content:center;opacity:.45}
.r.on .ctl .step i{opacity:1}
.r.on .sw{background:#0b0d12}
.r.on .sw:after{background:#fff}
.r.on .sw.off{background:rgba(11,13,18,.25)}
.r.on .sw.off:after{background:#fff}
.ctl .verb{font-weight:500;padding:0 6px}
.xp{display:inline-flex;align-items:center;height:34px;padding:0 14px;border-radius:17px;font-size:20px;font-weight:500;
 margin-left:14px;vertical-align:middle;box-shadow:inset 0 0 0 1.5px rgba(255,255,255,.55)}
.r.on .xp{box-shadow:inset 0 0 0 1.5px rgba(11,13,18,.55)}
.dots{display:flex;gap:12px}
.dots b{width:34px;height:34px;border-radius:17px;box-shadow:inset 0 0 0 1.5px rgba(255,255,255,.4)}
.dots b.c{box-shadow:0 0 0 3px #111215,0 0 0 6px #fff}
.r.on .dots b.c{box-shadow:0 0 0 3px #f4f6fb,0 0 0 6px #0b0d12}
.plain{font-size:24px;color:#c9d3e6;padding:20px 24px 0}
.pane{position:absolute;z-index:12;border-radius:20px;background:rgba(22,24,28,.99);
 box-shadow:0 0 0 1.5px rgba(255,255,255,.7),0 18px 48px 12px rgba(0,0,0,.85)}
.cl{padding:12px 0}
.cl .it{display:flex;align-items:center;gap:18px;height:64px;margin:0 10px;padding:0 18px;border-radius:14px;font-size:27px;
 white-space:nowrap}
.cl .it.f{background:#f4f6fb;color:#0b0d12}
.cl .it .tk{margin-left:auto;padding-left:28px;display:flex;width:56px;justify-content:flex-end}
.cl .it .pl{height:34px;padding:0 12px;border-radius:17px;font-size:19px;box-shadow:inset 0 0 0 1.5px rgba(255,255,255,.5);
 display:flex;align-items:center}
.cl .it.f .pl{box-shadow:inset 0 0 0 1.5px rgba(11,13,18,.5)}
.cl .it .sm{font-size:22px;color:#c9d3e6;margin-left:4px}
.cl .it.f .sm{color:#3a4256}
.cl .g{display:flex;align-items:center;gap:14px;padding:14px 28px 8px;font-size:24px;color:#c9d3e6;white-space:nowrap}
.cl .g:before,.cl .g:after{content:'';flex:1;height:1px;background:rgba(255,255,255,.25)}
.cl .hint{padding:8px 28px 6px;font-size:22px;color:#c9d3e6;white-space:normal;line-height:32px}
.cl .srch{display:flex;align-items:center;gap:16px;height:64px;margin:0 10px 6px;padding:0 18px;border-radius:14px;
 font-size:26px;color:#c9d3e6;background:#24272d}
.flag{width:40px;height:28px;border-radius:4px;overflow:hidden;flex:none;display:flex;box-shadow:0 0 0 1px rgba(255,255,255,.25)}
.flag i{flex:1}
.flag.v{flex-direction:column}
.logo{width:44px;height:44px;border-radius:10px;flex:none;display:flex;align-items:center;justify-content:center;
 font-size:20px;font-weight:900;color:#fff}
.pnl{padding:34px 36px 32px}
.pnl .pt{font-size:32px;font-weight:500}
.pnl .pl2{font-size:25px;color:#c9d3e6;margin-top:10px;line-height:36px}
.pnl .btns{display:flex;gap:10px;margin-top:28px}
.pnl .btns span{height:52px;padding:0 24px;border-radius:20px;display:flex;align-items:center;gap:10px;font-size:24px;
 box-shadow:inset 0 0 0 1.5px rgba(255,255,255,.45)}
.pnl .btns span.f{background:#f4f6fb;color:#0b0d12;box-shadow:none}
.pnl .qrw{display:flex;align-items:center;gap:26px;margin-top:30px}
.pnl .qrw .qt{font-size:23px;color:#c9d3e6;line-height:34px}
.pnl .qrw .qt b{color:#fff;font-weight:500}
.kv{display:flex;justify-content:space-between;font-size:26px;padding:16px 0;border-bottom:1px solid rgba(255,255,255,.1)}
.kv:last-child{border-bottom:none}
.kv span{color:#c9d3e6}
.kv b{font-weight:500}
.dim{position:absolute;inset:0;background:rgba(10,11,13,.55);z-index:11}
.prog{display:flex;align-items:center;gap:14px}
.prog .pb{width:200px;height:8px;border-radius:4px;background:rgba(11,13,18,.2);position:relative}
.prog .pb b{position:absolute;left:0;top:0;bottom:0;border-radius:4px;background:#0b0d12}
.abouthd{display:flex;align-items:center;gap:30px;margin-bottom:10px}
.abouthd .mark{width:96px;height:96px;border-radius:24px;background:radial-gradient(circle at 50% 50%,#fff 0 8%,
 rgba(255,255,255,.0) 9%),conic-gradient(from 0deg,#fff 0 4%,transparent 4% 25%,#fff 25% 29%,transparent 29% 50%,
 #fff 50% 54%,transparent 54% 75%,#fff 75% 79%,transparent 79%);box-shadow:inset 0 0 0 1.5px rgba(255,255,255,.4)}
.abouthd .an{font-size:44px;font-weight:500}
.abouthd .ab{font-size:25px;color:#c9d3e6;margin-top:8px}
.notes{padding:36px 40px;font-size:26px;line-height:40px}
.notes h4{font-size:24px;font-weight:500;color:#c9d3e6;margin:22px 0 6px}
.notes h4:first-child{margin-top:0}
.notes .vt{font-size:34px;font-weight:500;margin-bottom:4px}
.notes .vd{font-size:24px;color:#c9d3e6;margin-bottom:18px}
`;

const TABS = [['Playback', I.playback], ['Audio', I.audio], ['Subtitles', I.subs], ['Library', I.lib],
  ['Services', I.services], ['System', I.system], ['About', I.about]];

function bar() {
  return `<div class="bar"><span>Home</span><span>Movies</span><span>TV Shows</span><span>Documentaries</span>
    <span class="sp"></span><span class="dot"></span><span>Files</span><span>Search</span><span class="open">Settings</span>
    <span>18:40</span></div>`;
}

function tabs(open, focus) {
  return `<div class="tabs">${TABS.map(([n, ic]) => {
    const cls = n === focus ? 'f' : (n === open && !focus ? 'open' : '');
    return `<span class="${cls}">${svg(ic, 26)}${n}</span>`;
  }).join('')}</div><div class="tline"></div>`;
}

// control kinds: sw (v true/false) · chips (v: [a, b], c: index) · step (v) · go (v) · act (v verb, s status) ·
// dots (c index) · none
function ctl(o) {
  if (o.k === 'sw') {
    return `<span class="sw${o.v ? '' : ' off'}"></span>`;
  }
  if (o.k === 'chips') {
    return `<span class="chips">${o.v.map((x, k) => `<span class="${k === o.c ? 'c' : ''}">${x}</span>`).join('')}</span>`;
  }
  if (o.k === 'step') {
    return `<span class="step"><i>${svg(I.chev, 26).replace('M9 6l6 6-6 6', 'M15 6l-6 6 6 6')}</i>${o.v}<i>${svg(I.chev, 26)}
      </i></span>`;
  }
  if (o.k === 'go') {
    return `<span class="val">${o.v}</span>`;
  }
  if (o.k === 'act') {
    return `${o.s ? `<span class="mute">${o.s}</span>` : ''}<span class="verb">${o.v}</span>`;
  }
  if (o.k === 'prog') {
    return `<span class="prog"><span class="mute">${o.s}</span><span class="pb"><b style="width:${o.p}%"></b></span></span>
      <span class="verb">${o.v}</span>`;
  }
  if (o.k === 'dots') {
    const C = ['#ffffff', '#f6e05e', '#7ee0ff', '#9cf29c', '#ffb3c7'];
    return `<span class="dots">${C.map((c, k) => `<b class="${k === o.c ? 'c' : ''}" style="background:${c}"></b>`).join('')}
      </span>`;
  }
  return '';
}

function srow(o) {
  const cls = ['r', 'std'];
  if (o.on) {
    cls.push('on');
  }
  if (o.last) {
    cls.push('last');
  }
  return `<div class="${cls.join(' ')}"><span class="ic">${svg(o.ic, 34)}</span><span class="t"><b>${o.n}${o.x
    ? `<span class="xp">${o.x}</span>` : ''}</b>${o.l ? `<small>${o.l}</small>` : ''}</span><span class="ctl">${ctl(o)}
    </span><span class="ch">${o.k === 'go' ? svg(I.chev, 28) : ''}</span><i class="ln"></i></div>`;
}

function section(ic, title, rows, first) {
  rows[rows.length - 1].last = true;
  return hd(ic, title, first) + rows.map(srow).join('');
}

function page(open, sections, opts = {}) {
  const body = sections.map((s, k) => section(s[0], s[1], s[2], k === 0)).join('');
  return bar() + tabs(open, opts.tabFocus) + `<div class="list" style="top:${opts.top || 212}px">${opts.pre || ''}${body}
    ${opts.post || ''}</div>` + (opts.arrows === false ? '' : `<div class="arrow faint" style="top:214px">${svg(I.up, 32)}</div>`)
    + (opts.extra || '') + `<div class="cap2">${opts.cap}</div>`;
}

const LTR = '<path d="M15 6l-6 6 6 6"/>';

// ---------- flags (simple, for mock-ups only) ----------
const FL = {
  gb: '<svg width="40" height="28" viewBox="0 0 60 40"><rect width="60" height="40" fill="#1f3b8a"/><path d="M0 0l60 40M60 0L0 40" stroke="#fff" stroke-width="8"/><path d="M0 0l60 40M60 0L0 40" stroke="#c8102e" stroke-width="3"/><path d="M30 0v40M0 20h60" stroke="#fff" stroke-width="12"/><path d="M30 0v40M0 20h60" stroke="#c8102e" stroke-width="7"/></svg>',
  fr: 'h:#1f4bb5,#fff,#e03a3e', de: 'v:#111,#d00,#ffce00', it: 'h:#0a8f3c,#fff,#d22', es: 'v:#c60b1e,#ffc400,#c60b1e',
  nl: 'v:#ae1c28,#fff,#21468b', jp: 'jp', se: 'se', ca: 'ca', pt: 'h:#046a38,#da291c,#da291c', pl: 'v:#fff,#dc143c',
  dk: 'dk', ie: 'h:#169b62,#fff,#ff883e', br: 'br', kr: 'kr', no: 'no', fi: 'fi',
};

function flag(code) {
  const f = FL[code];
  if (!f) {
    return `<span style="width:40px;display:flex;justify-content:center;color:#c9d3e6;flex:none">${svg(I.lang, 28)}</span>`;
  }
  if (f.startsWith('<svg')) {
    return `<span class="flag">${f}</span>`;
  }
  if (f.startsWith('h:') || f.startsWith('v:')) {
    const cols = f.slice(2).split(',');
    return `<span class="flag${f[0] === 'v' ? ' v' : ''}">${cols.map(c => `<i style="background:${c}"></i>`).join('')}</span>`;
  }
  const S = {
    jp: '<rect width="60" height="40" fill="#fff"/><circle cx="30" cy="20" r="11" fill="#bc002d"/>',
    se: '<rect width="60" height="40" fill="#006aa7"/><path d="M0 20h60M21 0v40" stroke="#fecc00" stroke-width="8"/>',
    dk: '<rect width="60" height="40" fill="#c8102e"/><path d="M0 20h60M21 0v40" stroke="#fff" stroke-width="7"/>',
    no: '<rect width="60" height="40" fill="#ba0c2f"/><path d="M0 20h60M21 0v40" stroke="#fff" stroke-width="10"/><path d="M0 20h60M21 0v40" stroke="#00205b" stroke-width="5"/>',
    fi: '<rect width="60" height="40" fill="#fff"/><path d="M0 20h60M21 0v40" stroke="#002f6c" stroke-width="9"/>',
    ca: '<rect width="60" height="40" fill="#fff"/><rect width="15" height="40" fill="#d52b1e"/><rect x="45" width="15" height="40" fill="#d52b1e"/><path d="M30 9l3 7 5-2-2 8 3 1-9 6-9-6 3-1-2-8 5 2z" fill="#d52b1e"/>',
    br: '<rect width="60" height="40" fill="#009c3b"/><path d="M30 5l24 15-24 15L6 20z" fill="#ffdf00"/><circle cx="30" cy="20" r="8" fill="#002776"/>',
    kr: '<rect width="60" height="40" fill="#fff"/><circle cx="30" cy="20" r="9" fill="#cd2e3a"/><path d="M21 20a9 9 0 0 0 18 0z" fill="#0047a0"/>',
  };
  return `<span class="flag"><svg width="40" height="28" viewBox="0 0 60 40">${S[f]}</svg></span>`;
}

// choice list item: [mark html, name, extra html, ticked, focused]
function cl(items, opts = {}) {
  const rows = items.map(x => {
    if (typeof x === 'string') {
      return x;
    }
    const [mark, name, extra, tick, f] = x;
    return `<div class="it${f ? ' f' : ''}">${mark}<span>${name}</span>${extra || ''}<span class="tk">${tick ? svg(I.tick, 28)
      : ''}</span></div>`;
  }).join('');
  return `<div class="pane cl" style="right:${opts.right || 52}px;top:${opts.top}px;${opts.width ? `width:${opts.width}px;` : ''}
    ${opts.pr ? 'padding-right:56px;' : ''}">${opts.arrows ? `<div style="position:absolute;right:14px;top:22px;color:#fff;
    opacity:.3">${svg(I.up, 30)}</div><div style="position:absolute;right:14px;bottom:22px;color:#fff">${svg(I.down, 30)}
    </div>` : ''}${rows}</div>`;
}

const grp = t => `<div class="g">${t}</div>`;
const srch = t => `<div class="srch">${svg(I.find, 28)}${t}</div>`;

function qr(size, seed) {
  let s = seed;
  const rnd = () => {
    s = (s * 1103515245 + 12345) & 0x7fffffff;
    return s / 0x7fffffff;
  };
  const n = 29;
  const c = size / n;
  let r = `<rect width="${size}" height="${size}" fill="#fff"/>`;
  const fin = (x, y) => `<rect x="${x * c}" y="${y * c}" width="${7 * c}" height="${7 * c}" fill="#000"/>
    <rect x="${(x + 1) * c}" y="${(y + 1) * c}" width="${5 * c}" height="${5 * c}" fill="#fff"/>
    <rect x="${(x + 2) * c}" y="${(y + 2) * c}" width="${3 * c}" height="${3 * c}" fill="#000"/>`;
  for (let y = 1; y < n - 1; y++) {
    for (let x = 1; x < n - 1; x++) {
      const inF = (x < 9 && y < 9) || (x > n - 10 && y < 9) || (x < 9 && y > n - 10);
      if (!inF && rnd() < 0.48) {
        r += `<rect x="${x * c}" y="${y * c}" width="${c + 0.3}" height="${c + 0.3}" fill="#000"/>`;
      }
    }
  }
  r += fin(1, 1) + fin(n - 8, 1) + fin(1, n - 8);
  return `<svg width="${size}" height="${size}" style="border-radius:12px;flex:none">${r}</svg>`;
}

// ---------- Round 1: Playback, Audio ----------
const SKIP = ['Intro', 'Recap', 'Preview', 'Outro', 'Credits'];
const SKIPIC = [I.intro, I.recap, I.preview, I.outro, I.credits];

function s01() {
  return page('Playback', [
    [I.general, 'General', [
      { ic: I.pause, n: 'Hide controls when paused', l: 'Only the picture shows while you pause', k: 'sw', v: true },
      { ic: I.seek, n: 'Seek previews', x: 'Experimental', l: 'Pictures above the progress bar as you seek', k: 'sw', v: true },
      { ic: I.other, n: 'Open videos in another player', l: 'Play shows your other players to choose from', k: 'sw', v: false },
    ]],
    [I.resume, 'Resume', [
      { ic: I.resume, n: 'When a video was started', l: 'What Play does on a video you have started', k: 'step', v: 'Ask',
        on: true },
      { ic: I.bookmark, n: 'Remember where you stopped', l: 'On this device and on network files', k: 'sw', v: true },
      { ic: I.rewind, n: 'Start a little earlier', l: 'Goes back a few seconds when you resume', k: 'step', v: '5 seconds' },
    ]],
    [I.skip, 'Skipping', [
      { ic: I.find, n: 'Look up skip times online', l: 'TheIntroDB and IntroDB, when the file doesn\'t mark its parts',
        k: 'sw', v: true },
    ].concat(SKIP.map((n, k) => ({ ic: SKIPIC[k], n, l: k === 4 ? 'The end credits of a film or episode' : '',
      k: 'step', v: ['Skip when binge-watching', 'Show button', 'Show button', 'Off', 'Show button'][k] })))],
    [I.picture, 'Picture', [
      { ic: I.refresh, n: 'Automatic refresh rate', l: 'The TV follows the video\'s frame rate', k: 'chips',
        v: ['Off', 'Match the video'], c: 1 },
      { ic: I.hdr, n: 'Dolby Vision', l: 'When the TV and the file both have it', k: 'step', v: 'Automatic' },
    ]],
  ], { tabFocus: null, cap: '01 · Playback (whole tab)' });
}

const LANGS = [['gb', 'English'], ['fr', 'French'], ['de', 'German'], ['it', 'Italian'], ['es', 'Spanish'],
  ['nl', 'Dutch'], ['jp', 'Japanese'], ['se', 'Swedish'], ['dk', 'Danish'], ['no', 'Norwegian'],
  ['fi', 'Finnish'], ['pt', 'Portuguese'], ['pl', 'Polish']];

function s02() {
  const list = cl([srch('Search languages'), [flag(null), 'The film\'s original language', '', false],
    ...LANGS.slice(0, 7).map(([c, n], k) => [flag(c), n, '', k === 0, k === 0])],
  { top: 250, width: 600, arrows: true, pr: true });
  return page('Audio', [
    [I.lang, 'Language', [
      { ic: I.lang, n: 'Preferred audio language', l: 'Chosen first when a file has several', k: 'go', v: 'English',
        on: true },
      { ic: I.original, n: 'Prefer the original language', l: 'Original audio first when there is one', k: 'sw', v: true },
    ]],
    [I.surround, 'Sound', [
      { ic: I.surround, n: 'Surround sound', l: 'Passthrough sends it to your amplifier as it is', k: 'step',
        v: 'Automatic' },
      { ic: I.virtual, n: 'Virtual surround', l: 'Surround on stereo speakers or headphones', k: 'sw', v: false },
      { ic: I.chip, n: 'Audio decoder', l: 'Leave on Automatic unless a file has no sound', k: 'step', v: 'Automatic' },
    ]],
  ], { arrows: false, extra: list, cap: '02 · Audio, with Preferred audio language open' });
}

// ---------- Round 2: Subtitles ----------
function s03() {
  return page('Subtitles', [
    [I.cc, 'When To Show', [
      { ic: I.cc, n: 'Show subtitles', l: 'Turn on when a video starts', k: 'sw', v: true },
      { ic: I.full, n: 'Full subtitles in your own language', l: 'When the audio is in your language', k: 'sw', v: true },
      { ic: I.translate, n: 'Also translate other languages', x: 'Experimental', l: 'When no subtitles in your language exist',
        k: 'sw', v: true },
      { ic: I.hearing, n: 'Subtitles for the hard of hearing', l: 'SDH: with sounds and speaker names', k: 'step',
        v: 'Either', on: true },
    ]],
    [I.lang, 'Languages', [
      { ic: I.lang, n: 'Preferred subtitle language', l: 'Chosen first when a file has several', k: 'go', v: 'English' },
      { ic: I.find, n: 'Languages to search online', l: 'Asked for when you download', k: 'go', v: 'English, French' },
    ]],
    [I.download, 'Downloading', [
      { ic: I.download, n: 'OpenSubtitles', l: 'Where subtitles are downloaded from', k: 'go', v: 'Not signed in' },
      { ic: I.find, n: 'Find subtitles when none are included', l: 'Looks online when the file has none', k: 'sw', v: true },
      { ic: I.ready, n: 'Have subtitles ready', l: 'Fetched while a video prepares, and for the next two episodes', k: 'sw',
        v: true },
    ]],
    [I.subs, 'Default Appearance', [
      { ic: I.size, n: 'Text size', l: '', k: 'step', v: 'Medium' },
      { ic: I.colour, n: 'Text colour', l: '', k: 'dots', c: 0 },
      { ic: I.updown, n: 'Vertical position', l: 'Higher moves them up from the bottom edge', k: 'step', v: '8 %' },
      { ic: I.outline, n: 'Outline', l: 'A thin dark edge round each letter', k: 'sw', v: true },
      { ic: I.bg, n: 'Background', l: 'A dark box behind the text', k: 'sw', v: false },
      { ic: I.opacity, n: 'Background opacity', l: 'Only with Background on', k: 'step', v: '50 %' },
      { ic: I.font, n: 'Font', l: '', k: 'go', v: 'Roboto' },
      { ic: I.folder, n: 'Custom fonts folder', l: 'Your own fonts for subtitles', k: 'go', v: 'Not set' },
      { ic: I.bars, n: 'Use the black bars', l: 'Subtitles sit in the bars under a wide film', k: 'sw', v: true },
      { ic: I.enc, n: 'Text encoding', l: 'Only for old files whose letters look wrong', k: 'go', v: 'Automatic' },
    ]],
  ], { cap: '03 · Subtitles (whole tab)' });
}

function s04() {
  const list = cl([srch('Search languages'), grp('Selected, A–Z'),
    [flag('gb'), 'English', '', true], [flag('fr'), 'French', '', true], grp('All'),
    [flag('nl'), 'Dutch', '', false], [flag('de'), 'German', '', false, true], [flag('it'), 'Italian', '', false],
    [flag('jp'), 'Japanese', '', false], [flag('es'), 'Spanish', '', false]],
  { top: 300, width: 560, arrows: true, pr: true });
  return page('Subtitles', [
    [I.lang, 'Languages', [
      { ic: I.lang, n: 'Preferred subtitle language', l: 'Chosen first when a file has several', k: 'go', v: 'English' },
      { ic: I.find, n: 'Languages to search online', l: 'Asked for when you download', k: 'go', v: 'English, French',
        on: true },
    ]],
  ], { arrows: false, extra: list, cap: '04 · Languages to search online: several, ticked ones under Selected' });
}

function osPanel(inner, w = 700) {
  return `<div class="pane pnl" style="right:52px;top:300px;width:${w}px">${inner}</div>`;
}

const downRows = (on) => [
  { ic: I.download, n: 'OpenSubtitles', l: 'Where subtitles are downloaded from', k: 'go', v: on ? 'cinephile_42' : 'Not signed in',
    on: true },
  { ic: I.find, n: 'Find subtitles when none are included', l: 'Looks online when the file has none', k: 'sw', v: true },
  { ic: I.ready, n: 'Have subtitles ready', l: 'Fetched while a video prepares, and for the next two episodes', k: 'sw',
    v: true }];

function s05() {
  const p = osPanel(`<div class="pt">Not signed in</div><div class="pl2">Sign in to download subtitles. A free account
    gives you 20 a day.</div><div class="btns"><span class="f">${svg(I.person, 24)}Sign in</span></div>
    <div class="qrw">${qr(170, 7)}<div class="qt"><b>No account yet?</b><br>Scan to make a free one at
    opensubtitles.com, then sign in here.</div></div>`);
  return page('Subtitles', [[I.download, 'Downloading', downRows(false)]], { arrows: false, extra: p,
    cap: '05 · OpenSubtitles, signed out' });
}

function s05b() {
  const fields = [['Username', 'cinephile_42', 'typing'], ['Password', '••••••••••', 'pw']];
  const f = fields.map(([lb, v, kind]) => `<div class="fr2${kind === 'typing' ? ' typing' : ''}"><span class="fic">${svg(
    lb === 'Username' ? I.person : I.key, 28)}</span><span class="lb" style="width:200px">${lb}</span><span class="vl">${v}${kind
    === 'typing' ? '<i class="caret" style="margin-left:4px"></i>' : `<span style="flex:1"></span>${svg(I.eye, 30)}`}</span>
    <i class="ln"></i></div>`).join('');
  return bar() + tabs('Subtitles', null) + `<div class="crumb"><em>Subtitles › OpenSubtitles ›</em>Sign in</div>
    <div class="form" style="width:900px"><div style="font-size:25px;color:#c9d3e6;margin:0 0 18px 24px">A free account
    downloads 20 subtitles a day.</div>${f}<div class="fbtns"><span>${svg(I.person, 22)}Sign in</span></div>
    <div class="pnl" style="padding:34px 0 0 24px"><div class="qrw" style="margin-top:0">${qr(170, 7)}<div class="qt">
    <b>No account yet?</b><br>Scan to make a free one at opensubtitles.com,<br>then sign in here.</div></div></div></div>`
    + keyboard('2', '<b>OpenSubtitles:</b> the username and password of your account.<br>Back leaves without signing in.')
    + '<div class="cap2">05b · OK on OpenSubtitles (signed out) opens this page; no panel first</div>';
}

function s05c() {
  const p = osPanel(`<div class="pt">cinephile_42</div><div class="pl2">Signed in to OpenSubtitles</div>
    <div style="margin-top:22px"><div class="kv"><span>Downloads left today</span><b>17 of 20</b></div>
    <div class="kv"><span>Next 20 from</span><b>Tomorrow 01:00</b></div></div>
    <div class="btns"><span class="f">${svg(I.swap, 24)}Change account</span><span>${svg(I.out, 24)}Sign out</span></div>`);
  return page('Subtitles', [[I.download, 'Downloading', downRows(true)]], { arrows: false, extra: p,
    cap: '05c · OpenSubtitles, signed in' });
}

function s06() {
  const fonts = [['Roboto', 'R,sans-serif', true], ['Noto Serif', 'Georgia,serif'], ['Atkinson Hyperlegible', 'Verdana,sans-serif'],
    ['Courier Prime', 'Courier New,monospace'], ['Comic Neue', 'Comic Sans MS,cursive'], ['Cinema Sans (yours)', 'Trebuchet MS,sans-serif']];
  const list = cl(fonts.map(([n, ff, t], k) => [`<span style="width:40px;display:flex;justify-content:center;color:#c9d3e6;
    flex:none">${svg(I.font, 28)}</span>`, `<span style="font-family:${ff}">${n}</span>`, k === 5
    ? '<span class="sm">from your fonts folder</span>' : '', !!t, k === 1]), { top: 520, width: 620 });
  return page('Subtitles', [[I.subs, 'Default Appearance', [
    { ic: I.size, n: 'Text size', l: '', k: 'step', v: 'Medium' },
    { ic: I.colour, n: 'Text colour', l: '', k: 'dots', c: 0 },
    { ic: I.font, n: 'Font', l: 'Each shown in its own letters', k: 'go', v: 'Roboto', on: true },
    { ic: I.folder, n: 'Custom fonts folder', l: 'Your own fonts for subtitles', k: 'go', v: 'Not set' },
  ]]], { arrows: false, extra: list, cap: '06 · Font: each name previewed in its own letters' });
}

// ---------- Round 3: Library, Services ----------
function libSections(focus) {
  const S = [
    [I.lib, 'Your Library', [
      { ic: I.az, n: 'Ignore "The" and "A" when sorting', l: 'The Wire sorts under W', k: 'sw', v: true },
      { ic: I.thumbs, n: 'Make thumbnails for network videos', l: 'A still from the video when there is no artwork', k: 'sw',
        v: true },
      { ic: I.newv, n: 'Look for new videos', l: 'Plugging in a drive or Scan library always looks at once', k: 'step',
        v: 'Every 6 hours' },
      { ic: I.spoiler, n: 'Hide spoilers', l: 'Blur pictures and hide descriptions of episodes you haven\'t seen', k: 'sw',
        v: false },
    ]],
    [I.details, 'Details', [
      { ic: I.find, n: 'Find details automatically', l: 'Titles, pictures and cast from TMDB', k: 'sw', v: true },
      { ic: I.lang, n: 'Details language', l: 'Titles and descriptions in this language', k: 'go', v: 'English' },
      { ic: I.nfo, n: 'Use .nfo files', l: 'Details saved beside your videos come first', k: 'sw', v: true },
      { ic: I.write, n: 'Write .nfo files for new titles', l: 'Saves details beside each new video', k: 'sw', v: false },
      { ic: I.nfo, n: 'Write .nfo files for the whole library', l: 'Once, for every title you have', k: 'act', v: 'Write' },
    ]],
    [I.hero, 'Home Hero', [
      { ic: I.added, n: 'Recently added', l: '', k: 'sw', v: true },
      { ic: I.trend, n: 'Trending on Trakt', l: '', k: 'sw', v: true },
      { ic: I.fire, n: 'Popular on Trakt', l: '', k: 'sw', v: false },
      { ic: I.because, n: 'Because you watched', l: '', k: 'sw', v: true },
      { ic: I.timer, n: 'Change every', l: 'Titles you have started leave the hero', k: 'step', v: '12 seconds' },
    ]],
    [I.history, 'Viewing History', [
      { ic: I.history, n: 'Keep viewing history', l: 'Where you stopped and what you have watched', k: 'sw', v: true },
      { ic: I.clear, n: 'Clear viewing history', l: 'Your library and files are kept', k: 'act', v: 'Clear' },
      { ic: I.search, n: 'Keep search history', l: 'Off: nothing is kept and Recent searches is hidden', k: 'sw', v: true },
      { ic: I.searchH, n: 'Clear search history', l: '', k: 'act', v: 'Clear' },
    ]],
  ];
  S.forEach(s => s[2].forEach(r => {
    r.on = r.n === focus;
  }));
  return S;
}

function s07() {
  return page('Library', libSections('Look for new videos'), { cap: '07 · Library (whole tab)' });
}

function s08() {
  const sw = (n, l, on, f) => `<div class="r std${f ? ' on' : ''}" style="height:92px;margin:0 -12px"><span class="t"><b>${n}
    </b><small>${l}</small></span><span class="sw${on ? '' : ' off'}"></span></div>`;
  const dlg = `<div class="dim"></div><div class="pane pnl" style="left:50%;top:50%;transform:translate(-50%,-50%);width:860px">
    <div class="pt">Clear viewing history?</div><div class="pl2">Where you stopped and what you have watched are forgotten.
    Your library and files are kept.</div><div style="margin-top:22px">${sw('On this device', 'Supernova\'s own history', true,
    false)}${sw('On Trakt too', 'Also removes your watched history on Trakt', false, false)}</div>
    <div class="btns"><span class="f">${svg(I.back, 24)}Keep it</span><span>${svg(I.clear, 24)}Clear</span></div></div>`;
  return page('Library', libSections('Clear viewing history').slice(3), { arrows: false, extra: dlg,
    cap: '08 · Clear viewing history: asks first, Keep it focused' });
}

function svcSections(focus, signed = true) {
  const S = [
    [I.tvw, 'Where To Watch', [
      { ic: I.tvw, n: 'Show where to watch', l: 'Off removes every streaming button and row', k: 'sw', v: true },
      { ic: I.flag, n: 'Country', l: 'Which services are offered where you are', k: 'go', v: 'Canada' },
      { ic: I.services, n: 'Your services', l: 'Shown first, in your order', k: 'go', v: '5 services' },
    ]],
    [I.trakt, 'Trakt', [
      { ic: I.trakt, n: 'Trakt', l: 'Your watched history and lists', k: 'go', v: signed ? 'cinephile_42' : 'Not signed in' },
      { ic: I.scrobble, n: 'Scrobble while watching', l: 'Trakt shows what you are watching now', k: 'sw', v: true },
      { ic: I.sync, n: 'Sync resume points', l: 'Carry on where you stopped on another device', k: 'sw', v: true },
      { ic: I.sync, n: 'Sync your library to Trakt', l: 'Your films and shows appear in your Trakt collection', k: 'sw',
        v: false },
      { ic: I.send, n: 'Send everything to Trakt again', l: 'Fixes Trakt when it has missed something', k: 'act', v: 'Send' },
      { ic: I.fetch, n: 'Fetch everything from Trakt again', l: 'Fixes Supernova when it has missed something', k: 'act',
        v: 'Fetch' },
      { ic: I.remove, n: 'Remove Supernova\'s data from Trakt', l: 'Asks first', k: 'act', v: 'Remove' },
    ]],
    [I.ratings, 'Ratings', [
      { ic: I.key, n: 'OMDb ratings key', l: 'For Rotten Tomatoes and Metacritic scores', k: 'go', v: 'Key saved' },
    ]],
  ];
  S.forEach(s => s[2].forEach(r => {
    r.on = r.n === focus;
  }));
  return S;
}

function s09() {
  return page('Services', svcSections('Send everything to Trakt again'), {
    post: '<div class="plain">Streaming availability from JustWatch</div>', cap: '09 · Services (whole tab)' });
}

function s10() {
  const L = (bg, t, fg = '#fff') => `<span class="logo" style="background:${bg};color:${fg}">${t}</span>`;
  const list = cl([`<div class="hint">Your first choice goes at the top. Press OK on a ticked service to move it.</div>`,
    srch('Search services'), grp('Selected'),
    [L('#e50914', 'N'), 'Netflix', '<span class="sm">1</span>', true], [L('#113ccf', 'D+'), 'Disney+', '<span class="sm">2</span>', true, true],
    [L('#00a8e1', 'P'), 'Prime Video', '<span class="sm">3</span>', true], [L('#222', 'tv'), 'Apple TV+', '<span class="sm">4</span>', true],
    [L('#7b2cbf', 'M'), 'Max', '<span class="sm">5</span>', true], grp('All, A–Z'),
    [L('#ffcc00', 'C', '#000'), 'Crunchyroll', '', false], [L('#1ce783', 'H', '#000'), 'Hulu', '', false],
    [L('#0064ff', 'P+'), 'Paramount+', '', false]], { top: 230, width: 640, arrows: true, pr: true });
  return page('Services', svcSections('Your services').slice(0, 1), { arrows: false, extra: list,
    cap: '10 · Your services: Selected in your order, then All' });
}

function s11() {
  const p = `<div class="pane pnl" style="right:52px;top:330px;width:860px;white-space:nowrap"><div class="pt">cinephile_42</div>
    <div class="pl2">Signed in to Trakt</div><div style="margin-top:22px"><div class="kv"><span>Last synced</span>
    <b>Today 18:32</b></div><div class="kv"><span>Watched on Trakt</span><b>412 films · 3,108 episodes</b></div></div>
    <div class="btns"><span class="f">${svg(I.sync, 24)}Sync now</span><span>${svg(I.swap, 24)}Change account</span>
    <span>${svg(I.out, 24)}Sign out</span></div></div>`;
  return page('Services', svcSections('Trakt').slice(0, 2), { arrows: false, extra: p,
    cap: '11 · Trakt, signed in (signed out: a QR code and a short code to enter on trakt.tv)' });
}


function s11a() {
  const step = (n, t) => `<div style="display:flex;gap:22px;align-items:baseline;margin-top:26px;font-size:30px">
    <span style="width:44px;height:44px;border-radius:22px;background:#24272d;display:flex;align-items:center;
    justify-content:center;font-size:24px;font-weight:500;flex:none">${n}</span><span>${t}</span></div>`;
  return bar() + tabs('Services', null) + `<div class="crumb"><em>Services ›</em>Trakt</div>
    <div style="position:absolute;left:76px;top:300px;width:980px">
    <div style="font-size:40px;font-weight:500">Sign in to Trakt</div>
    <div style="font-size:26px;color:#c9d3e6;margin-top:10px">Trakt keeps what you watch, your lists and your ratings.</div>
    ${step(1, 'On your phone or computer, go to <b style="font-weight:500">trakt.tv/activate</b>')}
    ${step(2, 'Enter this code')}
    <div style="margin:26px 0 0 66px;display:inline-flex;padding:22px 40px;border-radius:20px;background:#24272d;
    font-size:64px;font-weight:500;letter-spacing:8px;font-variant-numeric:tabular-nums">AB12-CD34</div>
    <div style="margin:30px 0 0 66px;display:flex;align-items:center;gap:14px;font-size:26px;color:#c9d3e6">
    ${F.ring(28)}Waiting for you to confirm · this page carries on by itself</div>
    <div style="margin:14px 0 0 66px;font-size:24px;color:#c9d3e6">The code works for 10 minutes; a new one appears after
    that.</div></div>
    <div style="position:absolute;left:1180px;top:330px;display:flex;flex-direction:column;align-items:flex-start;gap:26px">
    ${qr(300, 31)}<div style="font-size:26px;color:#c9d3e6;line-height:38px"><b style="color:#fff;font-weight:500">Or scan
    this</b> to open it with the code<br>filled in. New to Trakt? Join there too.</div></div>
    <div class="cap2">11a · Services › Trakt, signed out: Trakt's own TV sign-in (no password on the TV)</div>`;
}

function s11b() {
  const head = `<div style="display:flex;align-items:center;gap:28px;margin-bottom:8px"><span style="width:96px;height:96px;
    border-radius:48px;background:linear-gradient(135deg,#ed1c24,#8a1016);display:flex;align-items:center;
    justify-content:center;font-size:40px;font-weight:700">C</span><div><div style="font-size:40px;font-weight:500">
    cinephile_42</div><div style="font-size:25px;color:#c9d3e6;margin-top:6px">Signed in to Trakt · last synced Today
    18:32</div></div></div><div style="height:58px"></div>`;
  const st = (ic, n, v, last) => `<div class="stat${last ? ' last' : ''}" style="height:84px"><span class="ic">${svg(ic, 32)}
    </span><span class="t">${n}</span><span class="v">${v}</span><i class="ln"></i></div>`;
  const facts = `<style>${F.css4}</style>${hd(I.trakt, 'On Trakt', true)}${st(I.film, 'Films watched', '412')}
    ${st(I.tv, 'Episodes watched', '3,108 · 96 shows')}${st(I.list, 'Lists', '7')}${st(I.star, 'Ratings', '240')}
    ${st(I.clock, 'Time watched', '2,904 hours', true)}`;
  const acct = [
    { ic: I.sync, n: 'Sync now', l: 'Last: Today 18:32 · also every 6 hours', k: 'act', v: 'Sync now', on: true },
    { ic: I.swap, n: 'Change account', l: 'Sign in as someone else', k: 'go', v: '' },
    { ic: I.out, n: 'Sign out', l: 'Your history stays on Trakt', k: 'act', v: 'Sign out', last: true },
  ];
  return bar() + tabs('Services', null) + `<div class="crumb"><em>Services ›</em>Trakt</div>
    <div class="list" style="top:290px">${head}${facts}${hd(I.person, 'Account')}${acct.map(srow).join('')}</div>
    <div class="cap2">11b · Services › Trakt, signed in: what Trakt holds, then the account</div>`;
}

// ---------- Round 4: System, About ----------
function sysSections(focus, over = {}) {
  const S = [
    [I.general, 'General', [
      { ic: I.lang, n: 'Interface language', l: 'The words in Supernova', k: 'go', v: 'English' },
    ]],
    [I.backup, 'Backup', [
      { ic: I.choose, n: 'What to back up', l: 'Choose picks the parts', k: 'step', v: 'Everything' },
      { ic: I.folder, n: 'Backups folder', l: 'Restore lists the backups in this folder', k: 'go', v: 'Download › Supernova › Backups' },
      { ic: I.backup, n: 'Back up now', l: 'Last: 24 Nov 21:10', k: 'act', v: 'Back up' },
      { ic: I.timer, n: 'Back up automatically', l: 'Keeps the last five; your own backups are never removed', k: 'step',
        v: 'Every week' },
      { ic: I.restore, n: 'Restore', l: 'Puts a backup back in place', k: 'go', v: '3 backups' },
    ]],
    [I.diag, 'Diagnostics', [
      { ic: I.diag, n: 'Diagnostic logging', l: 'Records what happens so problems can be found', k: 'sw', v: true },
      { ic: I.timer, n: 'Keep diagnostics for', l: '', k: 'step', v: '2 weeks' },
      { ic: I.save, n: 'Save a diagnostic report', l: 'To Download › Supernova; never includes passwords', k: 'act',
        v: 'Save' },
      { ic: I.clear, n: 'Clear diagnostics', l: 'Asks first', k: 'act', v: 'Clear' },
    ]],
    [I.adv, 'Advanced', [
      { ic: I.projector, n: 'Projector mode', l: 'Mirrors the picture for rear projection', k: 'sw', v: false },
      { ic: I.cpu, n: 'Force software decoding', l: 'Only if a video shows the wrong colours or freezes', k: 'sw', v: false },
      { ic: I.chip, n: 'Video decoder', l: 'Leave on Automatic', k: 'step', v: 'Automatic' },
      { ic: I.parser, n: 'Parser synchronisation', l: 'Relaxed helps damaged files keep playing', k: 'step', v: 'Automatic' },
      { ic: I.buffer, n: 'Stream buffer', l: 'Larger rides out slow networks, starts later', k: 'step', v: 'Medium' },
      { ic: I.frame, n: 'Max I-frame size', l: 'Only for very high bit-rate files', k: 'step', v: '8 MB' },
      { ic: I.speaker, n: 'Audio interface', l: 'Change only if sound drops out', k: 'step', v: 'Automatic' },
      { ic: I.speed, n: 'Sound when speed changes', l: 'Sonic keeps voices natural', k: 'chips', v: ['FFmpeg', 'Sonic'], c: 1 },
      { ic: I.downmix, n: 'Stereo downmix', l: 'Surround squeezed into two speakers', k: 'step', v: 'Automatic' },
      { ic: I.delay, n: 'Automatic audio delay', l: 'Lines sound up with the picture on this TV', k: 'sw', v: true },
    ]],
  ];
  S.forEach(s => s[2].forEach(r => {
    r.on = r.n === focus;
    Object.assign(r, over[r.n] || {});
  }));
  return S;
}

function s12() {
  return page('System', sysSections('What to back up'), { cap: '12 · System (whole tab)' });
}

function s13() {
  const P = [[I.general, 'Settings', 'Every choice on these seven tabs', true],
    [I.hero, 'Home rows and library pages', 'Your rows, their order and your own pages', true, true],
    [I.history, 'Viewing history', 'What you have watched and where you stopped', true],
    [I.fix, 'Fixed matches and hidden titles', 'Matches you corrected, titles kept off the hero', false],
    [I.conn, 'Connections', 'Servers and their folders; no usernames or passwords', true],
    [I.font, 'Subtitle fonts', 'Fonts you added for subtitles', false]];
  const it = ([ic, n, l, t, f]) => `<div class="it${f ? ' f' : ''}" style="height:92px"><span style="width:40px;display:flex;
    justify-content:center;color:${f ? '#0b0d12' : '#c9d3e6'}">${svg(ic, 28)}</span><span style="display:flex;flex-direction:column;
    gap:4px"><span>${n}</span><span class="sm" style="margin:0">${l}</span></span><span class="tk">${t ? svg(I.tick, 28) : ''}
    </span></div>`;
  const list = `<div class="pane cl" style="right:52px;top:330px;width:820px">${P.map(it).join('')}</div>`;
  return page('System', sysSections('What to back up', { 'What to back up': { v: 'Choose' } }).slice(1, 2), {
    arrows: false, extra: list, cap: '13 · What to back up › Choose: the six parts, each with what it holds' });
}

function s14() {
  const it = (n, l, f) => `<div class="it${f ? ' f' : ''}" style="height:84px"><span style="width:40px;display:flex;
    justify-content:center">${svg(I.backup, 28)}</span><span style="display:flex;flex-direction:column;gap:2px"><span>${n}
    </span><span class="sm" style="margin:0">${l}</span></span><span class="tk">${svg(I.chev, 26)}</span></div>`;
  const list = `<div class="pane cl" style="right:52px;top:470px;width:700px">${it('Today 21:10', 'Everything · 4.2 MB', true)}
    ${it('24 Nov 21:10', 'Everything · 4.1 MB')}${it('17 Nov 09:44', 'Settings only · 96 KB')}</div>`;
  return page('System', sysSections('Restore').slice(1, 2), { arrows: false, extra: list,
    cap: '14 · Restore ›: the backups, newest first' });
}

function s14b() {
  const kv = [['Settings', 'All tabs'], ['Home rows and library pages', '9 rows · 4 pages'], ['Viewing history',
    '1,206 titles'], ['Fixed matches and hidden titles', '38 · 46'], ['Connections', '4, without passwords'],
    ['Subtitle fonts', 'Not in this backup']];
  const p = `<div class="pane pnl" style="right:52px;top:330px;width:760px"><div class="pt">Today 21:10</div>
    <div class="pl2">Everything · 4.2 MB · made on Supernova 0.158.9</div><div style="margin-top:18px">${kv.map(([a, b]) =>
    `<div class="kv"><span>${a}</span><b>${b}</b></div>`).join('')}</div><div class="pl2" style="margin-top:20px">Restoring
    replaces what is here now with what is in this backup.</div><div class="btns"><span class="f">${svg(I.restore, 24)}
    Restore this backup</span></div></div>`;
  return page('System', sysSections('Restore').slice(1, 2), { arrows: false, extra: p,
    cap: '14b · In This Backup: what it holds, then Restore this backup' });
}

function s15() {
  const a = sysSections('Restore', { Restore: { k: 'prog', s: 'Restoring viewing history', p: 60, v: 'Stop' } }).slice(1, 2);
  const b = sysSections('Restore', { Restore: { k: 'act', s: 'Done · restart to finish', v: 'Restart now',
    l: 'Today 21:10 restored' } }).slice(1, 2);
  return bar() + tabs('System', null) + `<div class="list" style="top:212px">${section(a[0][0], a[0][1], a[0][2], true)}</div>
    <div class="list" style="top:820px">${section(b[0][0], 'Backup', b[0][2], true)}</div>
    <div class="cap2">15 · Restore runs in its row (top), then Restart now (bottom)</div>`;
}

function s16(focus = '0.158.9') {
  const V = [['0.158.9', '26 Nov 2026'], ['0.158.7', '24 Nov 2026'], ['0.158.6', '22 Nov 2026'], ['0.158.5', '21 Nov 2026'],
    ['0.158.4', '20 Nov 2026']];
  const btn = (ic, t, f) => `<span style="height:52px;padding:0 22px;border-radius:20px;display:flex;align-items:center;
    gap:12px;font-size:26px;${f ? 'background:#f4f6fb;color:#0b0d12' : 'box-shadow:inset 0 0 0 1.5px rgba(255,255,255,.45)'}">
    ${svg(ic, 24)}${t}${svg(I.chev, 22)}</span>`;
  const head = `<div class="abouthd"><div class="mark"></div><div><div class="an">Supernova 0.158.9</div><div class="ab">Built 26 Nov
    2026 · based on NOVA 6.6.4 · Android TV 11</div><div style="display:flex;gap:12px;margin-top:22px">
    ${btn(I.heartc, 'Services', focus === 'Credits')}${btn(I.licence, 'Open-source licences', focus === 'Licences')}</div>
    </div></div>`;
  const vrow = ([v, d], k) => `<div class="r std${v === focus ? ' on' : ''}${k === V.length - 1 ? ' last' : ''}"
    style="height:92px"><span class="ic">${svg(I.version, 32)}</span><span class="t"><b>${v}</b><small>${d}</small></span>
    <span class="ch">${v === focus ? '' : ''}</span><i class="ln"></i></div>`;
  const older = [['0.158.3', '19 Nov 2026'], ['0.158.2', '19 Nov 2026'], ['0.158.1', '18 Nov 2026']];
  const notes = `<div style="position:absolute;left:640px;right:0;top:0;bottom:0;padding:6px 40px 0 44px;
    border-left:1px solid rgba(255,255,255,.12)"><div style="font-size:34px;font-weight:500">0.158.9</div>
    <div style="font-size:24px;color:#c9d3e6;margin:4px 0 18px">26 Nov 2026</div><div class="notes" style="padding:0;
    font-size:26px;line-height:40px"><h4>Home</h4>The hero has one button, More: Details · Playback · This Title.<br>Exclude
    from hero takes the title off at once, with an Undo notice.<br>Row headings no longer stop the remote.
    <h4>Player</h4>Seek previews never show a grey box.<br>Track languages in words ("Chinese", not "chi").
    <h4>Search</h4>What you can search stays under the keyboard; the landing shows your last three searches.
    <h4>Library pages</h4>Six posters a row.<br>Mark as watched happens at once; the menu then offers Mark as unwatched.
    </div></div>`;
  const body = `<div style="position:relative;height:940px">${hd(I.news, 'What\'s New', true)}<div style="position:absolute;
    left:0;top:70px;width:600px">${V.concat(older).map(vrow).join('')}</div><div style="position:absolute;left:0;right:0;
    top:70px;bottom:0">${notes}</div><div style="position:absolute;right:-48px;bottom:30px;color:#fff">${svg(I.down, 32)}</div>
    </div>`;
  return bar() + tabs('About', null) + `<div class="list" style="top:212px">${head}<div style="height:58px"></div>${body}</div>`
    + `<div class="cap2">16 · About: versions on the left; the focused version's notes on the right (Right to read them)</div>`;

}

function s17() {
  const p = `<div class="dim"></div><div class="pane notes" style="left:50%;top:60px;transform:translateX(-50%);width:1120px">
    <div class="vt">0.158.9</div><div class="vd">26 Nov 2026</div>
    <h4>Home</h4>The hero has one button, More: Details · Playback · This Title.<br>Exclude from hero takes the title off at
    once, with an Undo notice.<br>Row headings no longer stop the remote.
    <h4>Player</h4>Seek previews never show a grey box.<br>Track languages in words ("Chinese", not "chi").<br>Audio boost
    always steps Off · Low · Medium · High.
    <h4>Search</h4>What you can search stays under the keyboard; the landing shows your last three searches.
    <h4>Library pages</h4>Six posters a row.<br>Mark as watched happens at once; the menu then offers Mark as unwatched.
    <h4>Fewer sudden closes</h4>No more closing while titles are matched, or while looking for network devices.</div>`;
  return s16().replace(/<div class="cap2">.*<\/div>$/, '') + p
    + '<div class="cap2">17 · A version opens its notes: text alone, no title bar, no Close</div>';
}

const mk = ic => `<span style="width:40px;display:flex;justify-content:center;color:#c9d3e6">${svg(ic, 26)}</span>`;

function creditsList(f) {
  const svc = [['TMDB', 'Details, pictures, cast'], ['Trakt', 'History, lists, trending'], ['OpenSubtitles', 'Subtitles'],
    ['JustWatch', 'Where to watch'], ['OMDb', 'Rotten Tomatoes, Metacritic'], ['TheIntroDB and IntroDB', 'Skip times']];
  return cl([grp('Services'), ...svc.map(([n, l], k) => [mk(I.heartc), n, `<span class="sm" style="margin-left:auto;
    padding-left:30px">${l}</span>`, false, f && k === 0]), grp('Built On'), [mk(I.heartc), 'NOVA Video Player',
    '<span class="sm" style="margin-left:auto;padding-left:30px">The player Supernova grew from</span>', false]],
  { top: 300, width: 860 });
}

function s18() {
  return s16('Credits').replace(/<div class="cap2">.*<\/div>$/, '') + creditsList(true)
    + '<div class="cap2">18 · OK on Services: the list floats beside the page</div>';
}

function s18b() {
  const pop = `<div class="pane pnl" style="right:110px;top:380px;width:860px;box-shadow:0 0 0 1.5px rgba(255,255,255,.7),
    0 24px 64px 16px rgba(0,0,0,.9)"><div class="pt">TMDB</div><div class="pl2">Film and TV details, pictures and cast come
    from TMDB.<br>This product uses the TMDB API but is not endorsed or certified by TMDB.</div><div class="qrw">${qr(170, 11)}
    <div class="qt"><b>themoviedb.org</b><br>Scan to visit. Back returns to the list.</div></div></div>`;
  return s16('Credits').replace(/<div class="cap2">.*<\/div>$/, '') + creditsList(true) + pop
    + '<div class="cap2">18b · OK on TMDB: its pop-up, a step down and left, in front of the list</div>';
}

function licList(f) {
  const comp = [['ExoPlayer (Media3)', 'Apache 2.0'], ['FFmpeg', 'LGPL 2.1'], ['jcifs-ng', 'LGPL 2.1'],
    ['OkHttp', 'Apache 2.0'], ['SSHJ', 'Apache 2.0']];
  const row = ([n, l], k) => [mk(I.licence), n, `<span class="sm" style="margin-left:auto;padding-left:30px">${l}</span>`, false,
    f && k === 1];
  return cl([srch('Search licences'), grp('Player'), ...comp.slice(0, 2).map(row), grp('Network'),
    ...comp.slice(2).map((c, k) => row(c, k + 9))], { top: 300, width: 760, arrows: true, pr: true });
}

function s19() {
  return s16('Licences').replace(/<div class="cap2">.*<\/div>$/, '') + licList(true)
    + '<div class="cap2">19 · OK on Open-source licences: grouped, with search</div>';
}

function s19b() {
  const pop = `<div class="pane pnl" style="right:110px;top:380px;width:820px;box-shadow:0 0 0 1.5px rgba(255,255,255,.7),
    0 24px 64px 16px rgba(0,0,0,.9)"><div class="pt">FFmpeg</div><div class="pl2">GNU Lesser General Public License 2.1<br>
    Decodes most audio and video formats.</div><div class="qrw">${qr(170, 23)}<div class="qt"><b>The licence's full text</b><br>
    Scan to read it on your phone. Back returns to the list.</div></div></div>`;
  return s16('Licences').replace(/<div class="cap2">.*<\/div>$/, '') + licList(true) + pop
    + '<div class="cap2">19b · OK on FFmpeg: its pop-up with one QR code</div>';
}

const ROUNDS = {
  'round-1': [['01-playback', s01, 2240], ['02-audio', s02]],
  'round-2': [['03-subtitles', s03, 2700], ['04-subtitle-search-languages', s04], ['05-opensubtitles-signed-out', s05],
    ['05b-opensubtitles-sign-in', s05b], ['05c-opensubtitles-signed-in', s05c], ['06-font', s06]],
  'round-3': [['07-library', s07, 2700], ['08-clear-viewing-history', s08], ['09-services', s09, 1820],
    ['10-your-services', s10], ['11a-trakt-signed-out', s11a], ['11b-trakt-signed-in', s11b, 1500]],
  'round-4': [['12-system', s12, 2900], ['13-what-to-back-up', s13], ['14-restore', s14], ['14b-in-this-backup', s14b],
    ['15-restore-progress', s15, 1500], ['16-about', s16, 1400], ['17-whats-new-notes', s17], ['18-credits', s18], ['18b-credits-tmdb', s18b],
    ['19-licences', s19], ['19b-licence-ffmpeg', s19b]],
};

(async () => {
  const OUT = path.join(__dirname, 'tv');
  fs.mkdirSync(OUT, { recursive: true });
  const browser = await chromium.launch();
  const pg = await browser.newPage({ viewport: { width: 1920, height: 1080 }, deviceScaleFactor: 1 });
  const only = process.argv[2];
  for (const [round, frames] of Object.entries(ROUNDS)) {
    for (const [name, fn, h = 1080] of frames) {
      if (only && !name.startsWith(only)) {
        continue;
      }
      await pg.setViewportSize({ width: 1920, height: h });
      const html = `<!doctype html><html><head><meta charset="utf-8"><style>${F.css}${F.css2}${css}</style></head><body>
        <div class="screen" style="height:${h}px">${fn()}<div class="border"></div></div>${speckle()}</body></html>`;
      await pg.setContent(html);
      await pg.evaluate(() => document.fonts.ready);
      await pg.screenshot({ path: path.join(OUT, `${name}.png`) });
      console.log(round, name);
    }
  }
  await browser.close();
})();
