// Proposal (2 Nov 2026): provider icons in the "My providers" picker, three ways. Uses the app's own vector
// logos (res/drawable/provider_*.xml). Follows the approved global rules: no title, inline group headings,
// 97% glass, white row focus, fitted width. Run: node render.js
const fs = require('fs');
const path = require('path');
const { chromium } = require('playwright');
const RES = path.join(__dirname, '../../../../res/drawable');

// Android vector → SVG (paths + viewport); colour applied by caller.
function logo(name, colour) {
  const xml = fs.readFileSync(path.join(RES, `provider_${name}.xml`), 'utf8');
  const vw = xml.match(/viewportWidth="([\d.]+)"/)[1], vh = xml.match(/viewportHeight="([\d.]+)"/)[1];
  const paths = [...xml.matchAll(/pathData="([^"]+)"/g)].map(m => `<path d="${m[1]}" fill="${colour}"/>`).join('');
  return `<svg viewBox="0 0 ${vw} ${vh}" preserveAspectRatio="xMidYMid meet" style="width:100%;height:100%">${paths}</svg>`;
}
// Official brand colours (full-colour option). Disney+ and Apple are single-colour brands.
const BRAND = { netflix: '#E50914', primevideo: '#1A98FF', disneyplus: '#2E6BFF', appletv: '#FFFFFF' };
const providers = [
  ['Amazon Prime Video', 'primevideo', true, 'Pv'],
  ['Disney Plus', 'disneyplus', true, 'D+'],
  ['Netflix', 'netflix', true, 'N'],
  ['Apple TV', 'appletv', false, 'tv'],
  ['Apple TV Store', 'appletv', false, 'tv'],
  ['Hayu', null, false, 'Ha'],
  ['JustWatch TV Free', null, false, 'JW'],
  ['JustWatch TV Plus', null, false, 'JW'],
  ['JustWatch TV Store', null, false, 'JW'],
];

const css = `*{box-sizing:border-box;margin:0;padding:0}
body{width:1920px;height:1080px;overflow:hidden;font-family:Roboto,"Noto Sans",Arial,sans-serif;color:#fff;
 background:radial-gradient(ellipse 1200px 800px at 70% 30%,#16264a,#070b14 70%)}
.cap{font-size:20px;color:#e8eeff;background:rgba(0,0,0,.55);display:inline-block;padding:8px 14px;border-radius:10px;margin-bottom:14px}
.panel{display:inline-block;border-radius:26px;background:rgba(26,31,92,.97);border:2px solid rgba(255,255,255,.4);box-shadow:0 20px 60px rgba(0,0,0,.45);padding:20px 22px}
.head{display:flex;align-items:center;gap:14px;color:#9cc9f0;font-size:19px;padding:8px 16px 6px}
.head i{flex:1;height:1.5px;background:rgba(156,201,240,.35)}
.row{display:flex;align-items:center;gap:18px;height:68px;padding:0 20px 0 16px;border:3px solid transparent;border-radius:12px;font-size:23px}
.row.focus{border-color:#fff}
.row .name{flex:1;white-space:nowrap}
.tick{width:28px;text-align:right;font-size:26px}
.tile{width:56px;height:34px;display:flex;align-items:center;justify-content:center;flex:none}
.mono{width:40px;height:34px;border:1.8px solid rgba(255,255,255,.75);border-radius:8px;display:flex;align-items:center;justify-content:center;font-size:15px;font-weight:700;letter-spacing:.3px}
.done{display:flex;align-items:center;gap:14px;height:64px;padding:0 16px;font-size:23px;border-top:1.5px solid rgba(255,255,255,.14);margin-top:10px}
.bg-settings{position:absolute;left:1000px;top:120px;width:860px;font-size:34px;line-height:64px;color:rgba(255,255,255,.85)}`;

function picker(mode, focusIndex = 0) {
  const icon = ([, key, , mono]) => {
    if (mode === 'names') return '';
    if (!key) return `<div class="tile"><div class="mono">${mono}</div></div>`;
    const colour = mode === 'colour' ? BRAND[key] : '#FFFFFF';
    return `<div class="tile">${logo(key, colour)}</div>`;
  };
  const rows = list => list.map((p, i) => `<div class="row ${providers.indexOf(p) === focusIndex ? 'focus' : ''}">${icon(p)}<span class="name">${p[0]}</span><span class="tick">${p[2] ? '✓' : ''}</span></div>`).join('');
  return `<div class="panel">
    <div class="head">Selected providers<i></i></div>${rows(providers.filter(p => p[2]))}
    <div class="head" style="padding-top:16px">Other providers<i></i></div>${rows(providers.filter(p => !p[2]))}
    <div class="done"><span style="font-size:26px">✓</span>Done</div></div>`;
}
const settingsBehind = `<div class="bg-settings"><b style="font-size:46px">Streaming services</b><br>Show all providers<br>My providers — Netflix, Amazon Prime Video…<br>Preferred provider<br>Refresh provider list<br>JustWatch</div>`;
const page = (mode, caption) => `<!doctype html><html><head><meta charset="utf-8"><style>${css}</style></head><body>${settingsBehind}
<div style="position:absolute;left:300px;top:30px"><div class="cap">${caption}</div><br>${picker(mode)}</div></body></html>`;

(async () => {
  const browser = await chromium.launch();
  const p = await browser.newPage({ viewport: { width: 1920, height: 1080 } });
  const shots = [
    ['01-monochrome-tiles.png', page('mono', 'A · Official single-colour logos in a uniform tile · monogram tile for providers without a logo')],
    ['02-full-colour.png', page('colour', 'B · Full-colour logos, transparent background · monogram tile for the rest')],
    ['03-names-only.png', page('names', 'C · Names only · ticks show selection')],
  ];
  for (const [n, h] of shots) { await p.setContent(h); await p.screenshot({ path: path.join(__dirname, n) }); }
  await browser.close();
  console.log('rendered', shots.length);
})();
