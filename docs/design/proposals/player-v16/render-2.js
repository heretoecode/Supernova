// Player v16, round 2 (owner, 22 Nov 2026): the lists opened from Playback Settings. Each opens on top of the panel
// (panel behind darkened ~45% and pushed back to 96%, the row you came from still visible; RULES §11), no title,
// Back returns to the row. Every row: flag (real locales) or globe, language in words (never "chi"), pills, the tick
// on the right; all on the row's centre line.
// 11 Subtitle track. 12 Download subtitles. 13 Audio track.
// Run: FONT_DIR=<dir with roboto-latin-*-normal.woff2> node render-2.js
const fs = require('fs');
const path = require('path');
let src = fs.readFileSync(path.join(__dirname, 'render.js'), 'utf8');
src = src.slice(0, src.lastIndexOf('(async () => {'));
src = src.replace("const { chromium } = require('playwright');", '');
const m = new Function('require', '__dirname', `${src}
return { css, svg, I, row, step, pm, sw, chev, ptabs };`)(require, __dirname);
const { chromium } = require('playwright');
const { css, svg, I, row, step, pm, sw, chev, ptabs } = m;

const FLAG = {
  gb: '<svg width="36" height="24" viewBox="0 0 60 40"><rect width="60" height="40" fill="#012169"/><path d="M0 0l60 40M60 0L0 40" stroke="#fff" stroke-width="8"/><path d="M0 0l60 40M60 0L0 40" stroke="#C8102E" stroke-width="3"/><path d="M30 0v40M0 20h60" stroke="#fff" stroke-width="12"/><path d="M30 0v40M0 20h60" stroke="#C8102E" stroke-width="7"/></svg>',
  fr: '<svg width="36" height="24" viewBox="0 0 3 2"><rect width="1" height="2" fill="#002395"/><rect x="1" width="1" height="2" fill="#fff"/><rect x="2" width="1" height="2" fill="#ED2939"/></svg>',
  cn: '<svg width="36" height="24" viewBox="0 0 30 20"><rect width="30" height="20" fill="#DE2910"/><circle cx="6" cy="6" r="3" fill="#FFDE00"/></svg>',
  es: '<svg width="36" height="24" viewBox="0 0 3 2"><rect width="3" height="2" fill="#AA151B"/><rect y=".5" width="3" height="1" fill="#F1BF00"/></svg>',
  de: '<svg width="36" height="24" viewBox="0 0 5 3"><rect width="5" height="1" fill="#000"/><rect y="1" width="5" height="1" fill="#DD0000"/><rect y="2" width="5" height="1" fill="#FFCE00"/></svg>',
};
const GLOBE = '<circle cx="12" cy="12" r="9"/><path d="M3 12h18M12 3a14 14 0 0 1 0 18M12 3a14 14 0 0 0 0 18"/>';
const TICK = '<path d="M5 12.5l4.5 4.5L19 7.5"/>';

const extra = `${css}
.panel.back{filter:brightness(.45);transform:scale(.96);transform-origin:top right}
.list{position:absolute;right:120px;top:120px;width:760px;border-radius:20px;background:rgba(22,24,28,.99);padding:14px 0 16px;
 z-index:10;box-shadow:0 0 0 1.5px rgba(255,255,255,.7),0 24px 64px 16px rgba(0,0,0,.9)}
.lr{height:66px;display:flex;align-items:center;gap:16px;padding:0 18px;margin:0 10px;border-radius:12px;font-size:25px}
.lr.two{height:84px}
.lr .fl{width:36px;height:24px;display:flex;align-items:center;justify-content:center;flex:none;border-radius:3px;overflow:hidden}
.lr .nm{display:flex;flex-direction:column;justify-content:center;gap:4px;min-width:0}
.lr .nm small{font-size:19px;color:#c9d3e6;white-space:nowrap;overflow:hidden;text-overflow:ellipsis;max-width:470px}
.lr .pills{display:flex;gap:8px;align-items:center}
.lr .fill{flex:1}
.lr .tk{width:28px;display:flex}
.lr.on{background:#f4f6fb;color:#0b0d12}
.lr.on .nm small{color:#3a3f4a}
.lr.on .pill{border-color:#0b0d12}
.lr .q{color:#c9d3e6}
.lr.on .q{color:#3a3f4a}
.lr.off .fl{color:#c9d3e6}
.sub{position:absolute;left:92px;bottom:120px;font-size:44px;font-weight:500;text-shadow:0 0 6px #000,0 2px 4px #000}
`;

function lr(flag, name, sub, pills, ticked, on, two) {
  const fl = flag ? (FLAG[flag] || svg(flag, 24)) : '';
  return `<div class="lr${on ? ' on' : ''}${two ? ' two' : ''}"><span class="fl">${fl}</span><span class="nm"><span>${name}</span>
    ${sub ? `<small>${sub}</small>` : ''}</span><span class="pills">${pills.map(p => `<span class="pill">${p}</span>`).join('')}</span>
    <span class="fill"></span><span class="tk">${ticked ? svg(TICK, 26) : ''}</span></div>`;
}

function subPanel(focus) {
  return `<div class="panel back">${ptabs('Subtitles')}<div class="h">Subtitle Selection</div>${row(I.subs, 'Subtitles', sw(true))}
    ${row(I.list, 'Subtitle track', '<span class="v">English <span class="pill">Original</span><span class="pill">SDH</span></span>' + chev, focus === 'track')}
    ${row(I.dl, 'Download subtitles', chev, focus === 'dl')}${row(I.clock, 'Subtitle delay', pm('0 ms'))}
    <div class="h">Subtitle Appearance</div>${row(I.aa, 'Text size', step('Medium'))}</div>`;
}

function f11() {
  const list = `<div class="list">
    ${lr(I.subs ? '<path d="M5 5l14 14M4 12a8 8 0 1 0 16 0 8 8 0 1 0-16 0"/>' : '', 'Off', '', [], false, false)}
    <div class="h">In The File</div>
    ${lr('gb', 'English', '', ['Original', 'SDH'], true, true)}
    ${lr('gb', 'English', '', ['Forced'], false)}
    ${lr('fr', 'French', '', [], false)}
    ${lr('es', 'Spanish', '', ['Latin America'], false)}
    ${lr('cn', 'Chinese', '', ['Simplified'], false)}
    <div class="h">Subtitle Files</div>
    ${lr('gb', 'English', 'Dune.Part.Two.2024.en.srt', ['.srt'], false, false, true)}</div>`;
  return '<div class="sub">The spice must flow.</div>' + subPanel('track') + list
    + '<div class="cap2">11 · Subtitle track: on top of the panel; flag, language, pills and tick on one centre line</div>';
}

function f12() {
  const list = `<div class="list" style="width:860px">
    <div class="r" style="margin:0 10px"><span class="ic">${svg(GLOBE, 26)}</span><span>Language</span><span class="fill"></span>${step('English')}</div>
    <div class="h">Results</div>
    ${lr('gb', 'Dune.Part.Two.2024.2160p.WEB-DL', 'Matches your file · 12,400 downloads', ['Best match'], false, true, true)}
    ${lr('gb', 'Dune.Part.Two.2024.1080p.BluRay.x264', '8,210 downloads', [], false, false, true)}
    ${lr('gb', 'Dune.Part.Two.2024.1080p.BluRay', '3,050 downloads', ['SDH'], false, false, true)}
    ${lr('gb', 'Dune Part Two (2024) English', '1,120 downloads', [], false, false, true)}
    ${lr('gb', 'Dune.Part.Two.2024.WEBRip', '640 downloads', ['Forced'], false, false, true)}</div>`;
  return '<div class="sub">The spice must flow.</div>' + subPanel('dl') + list
    + '<div class="cap2">12 · Download subtitles: Language steps; OK downloads, then it is ticked in Subtitle track</div>';
}

function f13() {
  const panel = `<div class="panel back">${ptabs('Audio')}<div class="h">Audio Selection</div>
    ${row(I.audio, 'Audio track', '<span class="v">English <span class="pill">Dolby Atmos</span><span class="pill">7.1</span></span>' + chev, true)}
    <div class="h">Audio Adjustments</div>${row(I.clock, 'Audio delay', pm('0 ms'))}${row(I.boost, 'Audio boost', step('Off'))}
    ${row(I.moon, 'Night mode', sw(false))}${row(I.spatial, 'Spatial audio', sw(false))}</div>`;
  const list = `<div class="list">
    ${lr('gb', 'English', '', ['Original', 'Dolby Atmos', '7.1'], true, true)}
    ${lr('gb', 'English', '', ['AC3', '5.1'], false)}
    ${lr('gb', 'English', '', ['Commentary', 'AAC', 'Stereo'], false)}
    ${lr('fr', 'French', '', ['EAC3', '5.1'], false)}
    ${lr('de', 'German', '', ['EAC3', '5.1'], false)}
    ${lr('cn', 'Chinese', '', ['AAC', 'Stereo'], false)}</div>`;
  return '<div class="sub">The spice must flow.</div>' + panel + list
    + '<div class="cap2">13 · Audio track: language in words, then pills; tick on the current one</div>';
}

(async () => {
  const b = await chromium.launch(); const pg = await b.newPage({ viewport: { width: 1920, height: 1080 } });
  for (const [n, fn] of [['11-subtitle-track', f11], ['12-download-subtitles', f12], ['13-audio-track', f13]]) {
    await pg.setContent(`<!doctype html><html><head><meta charset="utf-8"><style>${extra}</style></head><body><div class="screen">${fn()}<div class="border"></div></div></body></html>`);
    await pg.evaluate(() => document.fonts.ready); await pg.screenshot({ path: path.join(__dirname, 'tv', `${n}.png`) });
  }
  await b.close();
})();
