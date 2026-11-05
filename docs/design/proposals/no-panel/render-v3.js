// Exploring (owner, 5 Nov 2026): Home, Settings and a library page on the panel charcoal, no panel.
// Run: FONT_DIR=<roboto woff2 dir> node render-v3.js
const fs = require('fs');
const path = require('path');
const { chromium } = require('playwright');
const fontDir = process.env.FONT_DIR;
const font = w => `@font-face{font-family:R;font-weight:${w};src:url(data:font/woff2;base64,${fs.readFileSync(path.join(fontDir, `roboto-latin-${w}-normal.woff2`)).toString('base64')}) format('woff2')}`;
const nav = on => `<div class="nav"><span class="brand">SUPERNOVA</span>${['Home', 'Movies', 'TV Shows', 'Documentaries']
  .map(n => `<span class="${n === on ? 'on' : ''}">${n}</span>`).join('')}<span class="sp"></span>
  <span class="${on === 'Network' ? 'on' : ''}">Network &amp; Files</span><span class="ico">⌕</span>
  <span class="ico ${on === 'Settings' ? 'on' : ''}">⚙</span><span class="clock">20:51</span></div>`;
const art = [['#5b3a2e', '#1f1512'], ['#2e4a5b', '#121c22'], ['#4b5b2e', '#1a2012'], ['#5b2e4a', '#20121a'],
  ['#2e5b4f', '#12201d'], ['#5b4f2e', '#201c12'], ['#3a2e5b', '#151220'], ['#5b2e2e', '#201212']];
const card = (i, w, h, label, focus) => `<div class="card ${focus ? 'focus' : ''}" style="width:${w}px;height:${h}px;
  background:linear-gradient(160deg,${art[i % 8][0]},${art[i % 8][1]})"><span>${label}</span></div>`;
const home = `${nav('Home')}
<div class="hero"><div class="heroart"></div><div class="herotext"><div class="logo">THE ELECTRIC STATE</div>
<div class="meta">2025 · Science Fiction · 2 h 8 min</div>
<div class="plot">In an alternate 1990s, an orphaned teenager crosses the American West with a robot and a drifter.</div>
<div class="btns"><span class="b focus">▶ Play</span><span class="b">Details</span></div></div>
<div class="dots"><i class="on"></i><i></i><i></i><i></i><i></i></div></div>
<div class="rowtitle">Continue Watching</div>
<div class="row">${[0, 1, 2, 3, 4].map(i => card(i + 2, 400, 225, '', false)).join('')}</div>`;
const setrow = (n, l, v) => `<div class="srow"><div class="two"><div class="n">${n}</div><div class="l">${l}</div></div>${v}</div>`;
const chips = (list, on) => `<span class="chips">${list.map(x => `<i class="${x === on ? 'on' : ''}">${x}</i>`).join('')}</span>`;
const sw = on => `<span class="sw ${on ? 'on' : ''}"><i></i></span>`;
const settings = `${nav('Settings')}
<div class="page"><div class="shead"><div><div class="crumb">Settings</div><div class="st">Playback</div></div>
<div class="tabs"><span class="tab open">General</span><span class="tab">Resume</span><span class="tab">Skipping</span>
<span class="tab">Picture</span></div></div>
<div class="srows">
<div class="srow focus"><div class="two"><div class="n">Default playback speed</div><div class="l">Used when a video starts</div></div>${chips(['0.75×', '1.0×', '1.25×', '1.5×', '2.0×'], '1.0×')}</div>
${setrow('Still watching?', 'Ask after a run of auto-played episodes', chips(['Off', '2', '3', '5'], '3'))}
${setrow('Hide controls when paused', 'The picture stays clear while paused', sw(false))}
${setrow('Play next episode automatically', 'Starts five seconds before the end', sw(true))}
${setrow('Open videos in another player', 'Only for formats Supernova cannot play', sw(false))}
</div></div>`;
const library = `${nav('Movies')}
<div class="page"><div class="lhead"><div><div class="st">Movies</div><div class="facts"><span>1,214 films</span>
<span>86 unwatched</span></div></div><div class="tools"><span class="b">Filters ▾</span><span class="b">Sort: Date added</span>
<span class="b">Grid</span></div></div>
<div class="grid">${Array.from({ length: 16 }, (_, i) => `<div class="poster"><div class="pa" style="background:linear-gradient(160deg,${art[i % 8][0]},${art[i % 8][1]})${i === 1 ? ';box-shadow:0 0 0 4px #fff' : ''}"></div>
<div class="pt">${['Arrival', 'Blade Runner 2049', 'Conclave', 'Dune', 'Ex Machina', 'Flow', 'Gravity', 'Heat'][i % 8]}</div>
<div class="py">${2010 + i}</div></div>`).join('')}</div></div>`;
const css = `${font(400)}${font(500)}*{box-sizing:border-box;margin:0}
body{width:1920px;height:1080px;overflow:hidden;font-family:R;color:#fff;background:#16181C;position:relative}
.nav{position:absolute;left:56px;right:56px;top:28px;height:56px;display:flex;align-items:center;gap:30px;font-size:34px;color:#e8edf8;z-index:3}
.nav .brand{font-size:36px;margin-right:40px;letter-spacing:1px}.nav .sp{flex:1}.nav .on{font-weight:500;color:#fff}.nav .ico{font-size:34px}.nav .clock{margin-left:20px}
.hero{position:absolute;left:72px;top:112px;width:1776px;height:820px;border-radius:28px;overflow:hidden;box-shadow:0 0 0 2px rgba(255,255,255,.28)}
.heroart{position:absolute;inset:0;background:radial-gradient(ellipse 60% 80% at 70% 40%,#c47a4a 0%,#5a3020 45%,#1a0f0a 100%)}
.herotext{position:absolute;left:72px;bottom:110px;width:820px}.logo{font-size:76px;font-weight:500;letter-spacing:2px}
.meta{font-size:28px;color:#e2e6ee;margin-top:14px}.plot{font-size:26px;color:#d0d6e0;margin-top:14px;line-height:1.35}
.btns{display:flex;gap:16px;margin-top:28px}.b{height:64px;padding:0 26px;border-radius:20px;background:#2a2d33;display:inline-flex;align-items:center;font-size:24px;font-weight:500}
.b.focus{background:#F4F6FB;color:#0B0D12}
.dots{position:absolute;left:50%;bottom:34px;transform:translateX(-50%);display:flex;gap:12px}.dots i{width:12px;height:12px;border-radius:6px;background:rgba(255,255,255,.4)}.dots i.on{width:34px;background:#fff}
.rowtitle{position:absolute;left:72px;top:962px;font-size:30px;font-weight:500}
.row{position:absolute;left:72px;top:1012px;display:flex;gap:24px}
.card{border-radius:16px;box-shadow:0 0 0 2px rgba(255,255,255,.28)}
.page{position:absolute;left:96px;right:96px;top:132px;bottom:56px}
.shead,.lhead{display:flex;align-items:flex-end;justify-content:space-between}
.crumb{font-size:24px;color:#9aa3b2;margin-bottom:6px}.st{font-size:58px;font-weight:500}
.tabs,.tools{display:flex;gap:14px}.tab{height:64px;padding:0 28px;border-radius:20px;background:#2a2d33;display:inline-flex;align-items:center;font-size:24px;font-weight:500}.tab.open{background:#454B55}
.srows{margin-top:40px;display:flex;flex-direction:column;gap:14px}
.srow{display:flex;align-items:center;gap:30px;height:120px;padding:0 36px;border-radius:28px;background:rgba(255,255,255,.035)}
.srow.focus{box-shadow:inset 0 0 0 3px #fff}
.two{flex:1}.n{font-size:34px;font-weight:500}.l{font-size:24px;color:#C9D3E6;margin-top:6px}
.chips{display:flex;gap:6px;font-size:26px}.chips i{font-style:normal;padding:8px 16px;border-radius:12px;color:#dfe5f1}.chips i.on{background:#454B55;color:#fff}
.sw{width:76px;height:40px;border-radius:20px;background:#3a3f48;position:relative}.sw i{position:absolute;top:4px;left:4px;width:32px;height:32px;border-radius:50%;background:#fff}.sw.on{background:#cfd6e2}.sw.on i{left:40px;background:#16181C}
.facts{display:flex;gap:12px;margin-top:12px;font-size:24px;color:#dfe5f1}.facts span{background:rgba(255,255,255,.08);border-radius:14px;padding:6px 14px}
.grid{margin-top:36px;display:grid;grid-template-columns:repeat(8,1fr);gap:28px 26px}
.pa{height:300px;border-radius:14px;box-shadow:0 0 0 2px rgba(255,255,255,.28)}.pt{font-size:23px;font-weight:500;margin-top:12px;white-space:nowrap;overflow:hidden;text-overflow:ellipsis}.py{font-size:20px;color:#C9D3E6;margin-top:4px}`;
(async () => {
  const browser = await chromium.launch();
  const tab = await browser.newPage({ viewport: { width: 1920, height: 1080 } });
  for (const [f, body] of [['03-home.png', home], ['04-settings-playback.png', settings], ['05-movies-library.png', library]]) {
    await tab.setContent(`<html><head><style>${css}</style></head><body>${body}</body></html>`, { waitUntil: 'load' });
    await tab.evaluate(() => document.fonts.ready);
    await tab.screenshot({ path: path.join(__dirname, f) });
  }
  await browser.close();
})();
