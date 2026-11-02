// Proposal (2 Nov 2026): the Settings category grid under the approved global style. Run: node render.js
const path = require('path');
const { chromium } = require('playwright');
const ic = {
  play: '<path d="M7 4.5v15l12-7.5z"/>',
  speaker: '<path d="M4 9h4l5-4v14l-5-4H4z"/><path d="M16 9a4 4 0 0 1 0 6M18.5 6.5a7.5 7.5 0 0 1 0 11"/>',
  film: '<rect x="3" y="4" width="18" height="16" rx="1.5"/><path d="M7 4v16M17 4v16M3 9h4M3 15h4M17 9h4M17 15h4"/>',
  cloud: '<path d="M7 18.5h10.5a4 4 0 0 0 .6-7.95A6 6 0 0 0 6.6 9.2 4.7 4.7 0 0 0 7 18.5z"/>',
  monitor: '<rect x="3" y="4" width="18" height="12" rx="1.5"/><path d="M9 20h6M12 16v4"/>',
  nodes: '<circle cx="6" cy="5" r="2.2"/><circle cx="18" cy="5" r="2.2"/><circle cx="12" cy="19" r="2.2"/><path d="M7.2 6.9l3.7 10M16.8 6.9l-3.7 10"/>',
  gear: '<circle cx="12" cy="12" r="3.2"/><path d="M12 2.5v3M12 18.5v3M2.5 12h3M18.5 12h3M5.3 5.3l2.1 2.1M16.6 16.6l2.1 2.1M5.3 18.7l2.1-2.1M16.6 7.4l2.1-2.1"/>',
  info: '<circle cx="12" cy="12" r="9"/><path d="M12 11v6M12 7.5v.5"/>',
};
const tiles = [
  ['play', 'Playback &amp; Video', 'Control how content plays, resumes and looks on screen.'],
  ['speaker', 'Audio &amp; Subtitles', 'Configure audio, subtitles and related playback options.'],
  ['film', 'Library &amp; Home', 'Manage your libraries, metadata and Home screen displays.'],
  ['cloud', 'Streaming &amp; Providers', 'Set up and manage streaming providers and availability preferences.'],
  ['monitor', 'Appearance', 'Customise the look and feel of Supernova.'],
  ['nodes', 'Connections &amp; Integrations', 'Manage external connections, accounts and integrations.'],
  ['gear', 'System &amp; Maintenance', 'Diagnostics, backup and other system options.'],
  ['info', 'About Supernova', 'Version information, credits and legal details.'],
];
const svg = d => `<svg width="44" height="44" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.6" stroke-linecap="round" stroke-linejoin="round">${d}</svg>`;
const styles = {
  black: { rest: 'background:rgba(0,0,0,.72);color:#fff', focus: 'background:#f4f6fb;color:#0b0d12;box-shadow:0 10px 28px rgba(0,0,0,.5)', sub: ['#c9d3e6', '#3a4256'] },
  outline: { rest: 'background:rgba(26,31,92,.97);border:2px solid rgba(255,255,255,.18);color:#fff', focus: 'background:rgba(26,31,92,.97);border:4px solid #fff;color:#fff', sub: ['#c9d3e6', '#c9d3e6'] },
  glasswhite: { rest: 'background:rgba(26,31,92,.97);border:2px solid rgba(255,255,255,.18);color:#fff', focus: 'background:#f4f6fb;color:#0b0d12;box-shadow:0 10px 28px rgba(0,0,0,.5)', sub: ['#c9d3e6', '#3a4256'] },
};
const page = (key, caption, left = false, header = false) => {
  const s = styles[key];
  return `<!doctype html><html><head><meta charset="utf-8"><style>
*{box-sizing:border-box;margin:0;padding:0}
body{width:1920px;height:1080px;overflow:hidden;font-family:Roboto,"Noto Sans",Arial,sans-serif;color:#fff;
 background:radial-gradient(ellipse 1300px 900px at 65% 25%,#1d3b8a,#0a1638 55%,#050914)}
.nav{position:absolute;left:56px;right:56px;top:30px;display:flex;align-items:center;gap:40px;font-size:30px;color:rgba(255,255,255,.88)}
.grid{position:absolute;left:56px;right:56px;top:190px;display:grid;grid-template-columns:repeat(4,1fr);gap:28px}
.tile{height:380px;border-radius:22px;padding:40px 34px;display:flex;flex-direction:column;justify-content:center;gap:14px;border:2px solid transparent}
.t{font-size:32px;font-weight:700;line-height:1.15}.d{font-size:22px;line-height:1.4}
.cap{position:absolute;left:56px;bottom:26px;font-size:20px;color:#e8eeff;background:rgba(0,0,0,.55);padding:8px 14px;border-radius:10px}
</style></head><body>
<div class="nav"><b style="font-weight:500">SUPERNOVA</b><span>Home</span><span>Movies</span><span>TV Shows</span><span>Documentaries</span><span style="flex:1"></span><span>Network &amp; Files</span>
 <svg width="34" height="34" viewBox="0 0 24 24" fill="none" stroke="#fff" stroke-width="2"><circle cx="11" cy="11" r="6.5"/><path d="M16 16l4.5 4.5"/></svg>
 <span style="border-bottom:3px solid #fff;padding-bottom:4px">${svg(ic.gear).replace('44', '36').replace('44', '36')}</span><span>23:01</span></div>
<div style="position:absolute;left:56px;top:122px;font-size:36px;font-weight:500">Settings</div>
<div class="grid">${tiles.map(([i, t, d], n) => { const f = n === 2; return `<div class="tile" style="${f ? s.focus : s.rest}${left ? ';flex-direction:row;align-items:center;gap:24px;justify-content:flex-start' : ''}">${header ? `<div style="display:flex;align-items:flex-start;gap:18px"><div style="flex:none;height:37px;display:flex;align-items:center">${svg(ic[i]).replace(/44/g, '38')}</div><div class="t">${t}</div></div><div class="d" style="color:${f ? s.sub[1] : s.sub[0]}">${d}</div>` : left ? `<div style="flex:none">${svg(ic[i])}</div><div style="display:flex;flex-direction:column;gap:14px"><div class="t">${t}</div><div class="d" style="color:${f ? s.sub[1] : s.sub[0]}">${d}</div></div>` : `${svg(ic[i])}<div class="t">${t}</div><div class="d" style="color:${f ? s.sub[1] : s.sub[0]}">${d}</div>`}</div>`; }).join('')}</div>
<div class="cap">${caption}</div></body></html>`;
};
(async () => {
  const b = await chromium.launch(); const p = await b.newPage({ viewport: { width: 1920, height: 1080 } });
  const shots = [
    ['01-button-style-black-white.png', page('black', 'Option 1 · Same as buttons: black at rest, white box with black text on focus')],
    ['02-row-style-outline.png', page('outline', 'Option 2 · Same as rows: navy glass at rest, white outline on focus (no fill change)')],
    ['03-hybrid-glass-white.png', page('glasswhite', 'Option 3 · Hybrid: navy glass at rest, white box with black text on focus')],
    ['04-hybrid-icon-left.png', page('glasswhite', 'Option 3 with the icon to the left of the text', true)],
    ['05-hybrid-icon-beside-title.png', page('glasswhite', 'Option 3 · icon beside the title, description full width underneath', false, true)],
  ];
  for (const [n, h] of shots) { await p.setContent(h); await p.screenshot({ path: path.join(__dirname, n) }); }
  await b.close(); console.log('rendered', shots.length);
})();
