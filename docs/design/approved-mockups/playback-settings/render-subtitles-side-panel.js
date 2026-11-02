// Renders the Option B side-panel mock-up at 1920x1080 (Shield output resolution).
const fs = require('fs');
const path = require('path');
const { chromium } = require('playwright');
const dir = __dirname;
const gb = fs.readFileSync(path.join(dir, 'gb.b64'), 'utf8').trim();

const icon = {
  cc: '<rect x="3" y="5" width="18" height="14" rx="2"/><path d="M10 10.2a2.2 2.2 0 1 0 0 3.6M16.5 10.2a2.2 2.2 0 1 0 0 3.6"/>',
  list: '<path d="M9 6h11M9 12h11M9 18h11"/><circle cx="4.5" cy="6" r="1"/><circle cx="4.5" cy="12" r="1"/><circle cx="4.5" cy="18" r="1"/>',
  down: '<path d="M12 4v11M7 10.5l5 5 5-5M4 20h16"/>',
  aa: '<text x="2" y="18" font-size="15" font-family="Roboto" stroke="none" fill="currentColor">Aa</text>',
  palette: '<circle cx="12" cy="12" r="9"/><circle cx="8" cy="10" r="1.2"/><circle cx="12" cy="7.5" r="1.2"/><circle cx="16" cy="10" r="1.2"/><circle cx="9" cy="15" r="1.2"/>',
  vpos: '<path d="M12 3v18M8 7l4-4 4 4M8 17l4 4 4-4"/>',
  outline: '<rect x="4" y="3" width="16" height="18" stroke-dasharray="2 2"/><text x="8" y="17" font-size="12" font-family="Roboto" stroke="none" fill="currentColor">A</text>',
  bg: '<rect x="4" y="4" width="16" height="16"/><path d="M4 12l8-8M4 18l14-14M10 20l10-10M16 20l4-4"/>',
  drop: '<path d="M12 3c4 5 6 8 6 11a6 6 0 0 1-12 0c0-3 2-6 6-11z"/>',
  timer: '<circle cx="12" cy="13" r="8"/><path d="M12 13V9M9 2h6"/>',
  gear: '<circle cx="12" cy="12" r="3.2"/><path d="M12 2.5v3M12 18.5v3M2.5 12h3M18.5 12h3M5.3 5.3l2.1 2.1M16.6 16.6l2.1 2.1M5.3 18.7l2.1-2.1M16.6 7.4l2.1-2.1"/>',
  speaker: '<path d="M4 9h4l5-4v14l-5-4H4z"/><path d="M16 9a4 4 0 0 1 0 6M18.5 6.5a7.5 7.5 0 0 1 0 11"/>',
  monitor: '<rect x="3" y="4" width="18" height="12" rx="1.5"/><path d="M9 20h6M12 16v4"/>',
  sliders: '<path d="M4 6h16M4 12h16M4 18h16"/><circle cx="9" cy="6" r="2" fill="#141a52"/><circle cx="15" cy="12" r="2" fill="#141a52"/><circle cx="8" cy="18" r="2" fill="#141a52"/>',
};
const svg = (name, size = 30) => `<svg width="${size}" height="${size}" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.6" stroke-linecap="round" stroke-linejoin="round">${icon[name]}</svg>`;
const toggle = on => `<span class="toggle ${on ? 'on' : ''}"><span class="thumb"></span></span>`;
const choice = (opts, sel) => `<span class="choices">${opts.map((o, i) => `${i ? '<span class="bar">|</span>' : ''}<span class="${i === sel ? 'sel' : ''}">${o}</span>`).join('')}</span>`;
const row = (ic, label, value, cls = '') => `<div class="row ${cls}"><span class="ic">${svg(ic)}</span><span class="label">${label}</span><span class="value">${value}</span></div>`;

function page(opacityRow){ return `<!doctype html><html><head><meta charset="utf-8"><style>
*{box-sizing:border-box;margin:0;padding:0}
body{width:1920px;height:1080px;overflow:hidden;font-family:Roboto,"Noto Sans",Arial,sans-serif;color:#fff;background:#000}
.film{position:absolute;inset:0;background:
  radial-gradient(ellipse 900px 380px at 30% 78%, rgba(255,170,90,.55), transparent 70%),
  linear-gradient(180deg,#1b2a4a 0%,#3d4f7a 32%,#c77b52 58%,#2a1d22 62%,#140f14 100%)}
.film .sun{position:absolute;left:480px;top:520px;width:170px;height:170px;border-radius:50%;background:radial-gradient(circle,#ffe2a6,#ff9a52 60%,transparent 72%)}
.film .hills{position:absolute;left:0;right:0;bottom:0;height:470px;background:
  radial-gradient(ellipse 700px 200px at 15% 30%,#1d1418 98%,transparent 100%),
  radial-gradient(ellipse 900px 240px at 70% 40%,#22171c 98%,transparent 100%),#140f14}
.film .figure{position:absolute;left:760px;bottom:300px;width:46px;height:150px;border-radius:23px 23px 6px 6px;background:#0b0809}
.film .figure:before{content:"";position:absolute;left:5px;top:-40px;width:36px;height:36px;border-radius:50%;background:#0b0809}
.dim{position:absolute;top:0;bottom:0;right:0;width:760px;background:linear-gradient(90deg,rgba(0,0,0,0),rgba(0,0,0,.45) 90px)}
.subtitle{position:absolute;left:0;width:1240px;bottom:110px;text-align:center;font-size:44px;font-weight:500;color:#ffc93c;
  text-shadow:-2px -2px 0 #000,2px -2px 0 #000,-2px 2px 0 #000,2px 2px 0 #000,0 3px 8px #000}
.preview-note{position:absolute;left:0;width:1240px;bottom:62px;text-align:center;font-size:18px;color:rgba(255,255,255,.75);letter-spacing:.3px}
.panel{position:absolute;top:40px;bottom:40px;right:40px;width:640px;border-radius:26px;
  background:linear-gradient(180deg,rgba(26,31,92,.96),rgba(20,24,72,.96));border:2px solid #5fd0ff;
  box-shadow:0 0 22px rgba(95,208,255,.35),0 20px 60px rgba(0,0,0,.55);padding:30px 30px 24px;display:flex;flex-direction:column}
.title{display:flex;align-items:center;gap:14px;font-size:30px;font-weight:700;margin-bottom:22px}
.tabs{display:grid;grid-template-columns:repeat(4,1fr);gap:10px;margin-bottom:22px}
.tab{height:56px;border-radius:14px;border:2px solid #5a7bd8;display:flex;align-items:center;justify-content:center;gap:8px;font-size:19px;font-weight:600}
.tab.active{background:#2f6df0;border-color:#7fd6ff;box-shadow:0 0 16px rgba(95,208,255,.55)}
.head{font-size:17px;font-weight:700;color:#9cc9f0;letter-spacing:.6px;padding:10px 4px 8px;border-bottom:1.5px solid #3e5aa8}
.row{display:flex;align-items:center;height:60px;padding:0 6px;position:relative}
.row:after{content:'';position:absolute;left:0;right:0;bottom:0;height:1.5px;background:#34479a}
.row.focus:after{display:none}
.row .ic{width:44px;display:flex;color:#e8eeff}
.row .label{flex:1;font-size:21px;font-weight:600}
.row .value{display:flex;align-items:center;gap:10px;font-size:20px}
.row.dimmed{opacity:.42}
.row.focus{border-radius:10px;border:2.5px solid #7fd6ff;box-shadow:0 0 14px rgba(95,208,255,.6);background:rgba(95,208,255,.08)}
.chev{font-size:24px;margin-left:2px}
.flag{width:30px;height:30px;border-radius:4px}
.choices{display:flex;gap:9px;align-items:center}.choices .sel{color:#3fb6ff}.choices .bar{color:#cfd8ff}
.dots{display:flex;gap:11px}.dot{width:30px;height:30px;border-radius:50%}.dot.sel{outline:3px solid #fff;outline-offset:2px}
.toggle{width:58px;height:34px;border-radius:17px;background:#3a4a77;position:relative;display:inline-block}
.toggle.on{background:linear-gradient(90deg,#00aff0,#2559ee)}
.toggle .thumb{position:absolute;top:3px;left:3px;width:28px;height:28px;border-radius:50%;background:#fff}
.toggle.on .thumb{left:27px}
.delay{display:flex;gap:22px;align-items:center;font-size:21px}.delay b{font-size:26px;font-weight:400}
.label-tag{position:absolute;left:40px;top:34px;font-size:20px;color:rgba(255,255,255,.85);background:rgba(0,0,0,.45);padding:8px 14px;border-radius:10px}
</style></head><body>
<div class="film"><div class="sun"></div><div class="hills"></div><div class="figure"></div></div>
<div class="dim"></div>
<div class="label-tag">Approved direction · Option B side panel · Background Opacity stepper (dimmed while Background is off)</div>
<div class="subtitle">I told you we'd make it back before dark.</div>
<div class="preview-note">Subtitle preview stays visible while you adjust it</div>
<div class="panel">
  <div class="title">${svg('gear', 34)}Playback Settings</div>
  <div class="tabs">
    <div class="tab active">${svg('cc', 24)}Subtitles</div>
    <div class="tab">${svg('speaker', 24)}Audio</div>
    <div class="tab">${svg('monitor', 24)}Video</div>
    <div class="tab">${svg('sliders', 24)}More</div>
  </div>
  <div class="head">SUBTITLE SELECTION</div>
  ${row('cc', 'Subtitles', toggle(true))}
  ${row('list', 'Subtitle Track', `<img class="flag" src="data:image/png;base64,${gb}">English<span class="chev">›</span>`)}
  ${row('down', 'Download Subtitles', `Find &amp; Download<span class="chev">›</span>`)}
  <div class="head" style="margin-top:12px">SUBTITLE APPEARANCE</div>
  ${row('aa', 'Text Size', choice(['Small', 'Medium', 'Large'], 1))}
  ${row('palette', 'Text Colour', `<span class="dots">${['#ffffff', '#ffc93c', '#19e6e6', '#4cc554', '#f06a9b'].map((c, i) => `<span class="dot ${i === 1 ? 'sel' : ''}" style="background:${c}"></span>`).join('')}</span>`, 'focus')}
  ${row('vpos', 'Vertical Position', choice(['Bottom', 'Middle', 'Top'], 0))}
  ${row('outline', 'Outline', toggle(true))}
  ${row('bg', 'Background', toggle(false))}
  ${opacityRow}
  ${row('timer', 'Subtitle Delay', `<span class="delay"><b>−</b>0 ms<b>+</b></span>`)}
</div>
</body></html>`; }

const stepper = `<span class="delay"><b>‹</b><span style="min-width:64px;text-align:center;color:#3fb6ff">100%</span><b>›</b></span>`;
const variants = {
  '1-stepper': row('drop', 'Background Opacity', stepper, 'dimmed'),
  '2-short-label': row('drop', 'Opacity', choice(['0%', '25%', '50%', '75%', '100%'], 4)),
  '3-two-line': `<div class="row" style="height:96px;flex-wrap:wrap;align-content:center;row-gap:8px"><span class="ic">${svg('drop')}</span><span class="label">Background Opacity</span><span class="value" style="width:100%;justify-content:flex-end;padding-right:2px">${choice(['0%', '25%', '50%', '75%', '100%'], 4)}</span></div>`,
};
(async () => {
  const browser = await chromium.launch();
  const p = await browser.newPage({ viewport: { width: 1920, height: 1080 } });
  for (const [name, r] of Object.entries(variants)) {
    await p.setContent(page(r), { waitUntil: 'load' });
    const box = await p.locator('.panel').boundingBox();
    await p.screenshot({ path: path.join(dir, 'opacity-' + name + '.png'), clip: { x: box.x - 20, y: box.y + 690, width: box.width + 40, height: 210 } });
  }
  await p.setContent(page(variants['1-stepper']), { waitUntil: 'load' });
  await p.screenshot({ path: path.join(dir, 'option-b-side-panel-v2.png') });
  const page2 = page(variants['1-stepper']).replace("row('bg'","x");
  await browser.close();
  console.log('rendered');
})();
