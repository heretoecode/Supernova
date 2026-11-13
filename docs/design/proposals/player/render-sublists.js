// Player sub-lists (owner, 13 Nov 2026), two ways to compare:
// 1 Subtitle track replacing Playback Settings, with a Back row at the top (focused) and no "None" (the Subtitles
//   switch already turns them off).
// 2 Download subtitles sitting on top of Playback Settings: the panel behind darkened and pushed back, no back header.
// Made-up release names. Light frame = picture edge. Run: FONT_DIR=<roboto woff2 dir> node render-sublists.js
const fs = require('fs');
const path = require('path');
const { chromium } = require('playwright');
const fontDir = process.env.FONT_DIR;
const font = w => `@font-face{font-family:R;font-weight:${w};src:url(data:font/woff2;base64,${fs.readFileSync(
  path.join(fontDir, `roboto-latin-${w}-normal.woff2`)).toString('base64')}) format('woff2')}`;
const svg = (d, s = 30) => `<svg width="${s}" height="${s}" viewBox="0 0 24 24" fill="none" stroke="currentColor"
  stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round">${d}</svg>`;
const I = {
  back: '<path d="M10 6l-6 6 6 6M4 12h16"/>',
  tick: '<path d="M5 12.5l4.5 4.5L19 7.5"/>',
  dl: '<path d="M12 4v11M7 10l5 5 5-5M5 20h14"/>',
  search: '<circle cx="11" cy="11" r="7"/><path d="M20 20l-4-4"/>',
  subs: '<rect x="3" y="5" width="18" height="14" rx="2"/><path d="M7 14h4M13 14h4M7 10h10"/>',
  audio: '<path d="M4 9v6h4l5 4V5L8 9z"/><path d="M16 9a4 4 0 0 1 0 6"/>',
  play: '<path d="M7 4l13 8-13 8z"/>',
  delay: '<circle cx="12" cy="13" r="8"/><path d="M12 9v4l2 2M10 2h4"/>',
  chev: '<path d="M9 6l6 6-6 6"/>',
  minus: '<path d="M6 12h12"/>',
  plus: '<path d="M12 6v12M6 12h12"/>',
};
const flag = c => `<span class="flag" style="background:${c}"></span>`;
const US = 'linear-gradient(180deg,#B22234 0 14%,#fff 14% 28%,#B22234 28% 42%,#fff 42% 56%,#B22234 56% 70%,#fff 70% 84%,'
  + '#B22234 84%),#B22234';
const ES = 'linear-gradient(180deg,#AA151B 0 25%,#F1BF00 25% 75%,#AA151B 75%)';
const FR = 'linear-gradient(90deg,#002395 0 33%,#fff 33% 66%,#ED2939 66%)';
const pills = list => list.map(p => `<em>${p}</em>`).join('');
const track = (f, name, p, o) => `<div class="row ${o.focus ? 'focus' : ''}">${flag(f)}<b>${name}</b>${pills(p)}
  <span class="end">${o.on ? svg(I.tick, 30) : ''}${o.dl ? svg(I.dl, 30) : ''}</span></div>`;
const scene = '<div class="video"></div>';
const one = `<div class="frame">${scene}<div class="panel right">
  <div class="row back focus">${svg(I.back, 30)}<b>Back</b></div>
  ${track(US, 'English', ['Original'], { on: true })}
  ${track(US, 'English', ['Original', 'SDH'], {})}
  ${track(ES, 'Spanish', [], {})}
  ${track(FR, 'French', ['Forced'], {})}</div></div>`;
const behindRows = `<div class="ptabs"><span class="tab line">${svg(I.subs, 28)}Subtitles</span><span class="tab">
  ${svg(I.audio, 28)}Audio</span><span class="tab">${svg(I.play, 28)}Playback</span></div>
  <div class="prow">${svg(I.subs, 30)}<span>Subtitles</span><i class="sw on"><i></i></i></div>
  <div class="prow">${svg(I.subs, 30)}<span>Subtitle track</span><span class="v">English ${svg(I.chev, 26)}</span></div>
  <div class="prow focus">${svg(I.dl, 30)}<span>Download subtitles</span><span class="v">OpenSubtitles ${svg(I.chev, 26)}
  </span></div>
  <div class="prow">${svg(I.delay, 30)}<span>Subtitle delay</span><span class="v">${svg(I.minus, 24)} 0.0 s
  ${svg(I.plus, 24)}</span></div>`;
const two = `<div class="frame">${scene}<div class="panel right behind">${behindRows}</div>
  <div class="panel front">
  <div class="row search">${svg(I.search, 30)}<span>Search subtitles</span></div>
  ${track(US, 'English', ['Matches your file', 'WEB'], { focus: true, dl: true })}
  ${track(US, 'English', ['SDH', 'WEB'], {})}
  ${track(US, 'English', ['BluRay'], {})}
  ${track(ES, 'Spanish', ['WEB'], {})}
  ${track(FR, 'French', ['WEB'], {})}</div></div>`;
const css = `${font(400)}${font(500)}*{box-sizing:border-box;margin:0}
body{width:1920px;font-family:R;color:#fff;background:#000;position:relative}
body:after{content:"";position:fixed;inset:0;border:6px solid #8a919c;z-index:99;pointer-events:none}
.frame{position:relative;width:1920px;height:1080px;overflow:hidden}.frame+.frame{border-top:8px solid #8a919c}
.video{position:absolute;inset:0;background:radial-gradient(ellipse at 35% 40%,#5f554a 0%,#2b2a2d 40%,#111216 85%);
filter:brightness(.6)}
.panel{position:absolute;top:56px;padding:18px;border-radius:26px;background:rgba(22,24,28,.9);
box-shadow:0 30px 80px rgba(0,0,0,.6);width:max-content;min-width:520px}
.panel.right{right:56px}
.behind{transform:scale(.96);transform-origin:right top;filter:brightness(.45)}
.front{right:200px;top:150px;box-shadow:0 0 0 2px rgba(255,255,255,.16),0 40px 100px rgba(0,0,0,.85)}
.row{position:relative;display:flex;align-items:center;gap:18px;height:80px;padding:0 24px;border-radius:18px;
font-size:29px}.row b{font-weight:500;margin-right:6px}
.row:after{content:'';position:absolute;left:24px;right:24px;bottom:0;height:2px;background:rgba(255,255,255,.08)}
.row.focus:after,.row:last-child:after,.row:has(+ .row.focus):after{display:none}
.row.focus{background:#F4F6FB;color:#0B0D12}
.back{color:#e8edf6}.back.focus{color:#0B0D12}
.flag{width:44px;height:30px;border-radius:4px;flex:none;box-shadow:0 0 0 1px rgba(255,255,255,.25)}
.row em{font-style:normal;font-size:21px;padding:4px 12px;border-radius:12px;box-shadow:inset 0 0 0 2px
rgba(255,255,255,.3)}.row.focus em{box-shadow:inset 0 0 0 2px rgba(11,13,18,.35)}
.end{margin-left:auto;padding-left:30px;display:flex}
.search{color:#C9D3E6}.search span{font-size:28px}
.ptabs{display:flex;gap:10px;border-bottom:2px solid rgba(255,255,255,.14);padding:4px 8px 12px;margin-bottom:16px}
.tab{position:relative;display:inline-flex;align-items:center;gap:12px;height:56px;padding:0 18px;font-size:28px;
font-weight:500;color:#8f98a8}.tab.line{color:#fff}.tab.line:after{content:'';position:absolute;left:18px;right:18px;
bottom:-14px;height:4px;border-radius:2px;background:#fff}
.prow{display:flex;align-items:center;gap:20px;height:84px;padding:0 24px;border-radius:18px;font-size:28px;
font-weight:500;width:900px}.prow svg{color:#C9D3E6;flex:none}.prow.focus{background:#F4F6FB;color:#0B0D12}
.prow.focus svg{color:#0B0D12}.prow .v{margin-left:auto;display:flex;align-items:center;gap:10px;font-weight:400}
.sw{margin-left:auto;width:76px;height:42px;border-radius:21px;background:#F4F6FB;position:relative}
.sw i{position:absolute;top:5px;left:39px;width:32px;height:32px;border-radius:50%;background:#0B0D12}`;
(async () => {
  const browser = await chromium.launch();
  const tab = await browser.newPage({ viewport: { width: 1920, height: 2168 } });
  await tab.setContent(`<html><head><style>${css}</style></head><body>${one}${two}</body></html>`,
    { waitUntil: 'load' });
  await tab.evaluate(() => document.fonts.ready);
  await tab.screenshot({ path: path.join(__dirname, '28-subtitle-track-back-and-download-on-top.png') });
  await browser.close();
})();
