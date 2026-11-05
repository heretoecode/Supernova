// Proposal (0.157, owner 5 Nov 2026): library page statistics in the new look. A: one row straight on Space Black.
// B: the same row in a 90% charcoal strip. Toolbar in the new button style; poster captions without blue bars.
// Run: SPACE_JPG=<1080p Space Black jpg> FONT_DIR=<folder with roboto-latin-{400,500,700}-normal.woff2> node render.js
const fs = require('fs');
const path = require('path');
const { chromium } = require('playwright');

const out = __dirname;
const fontDir = process.env.FONT_DIR;
const SPACE = 'data:image/jpeg;base64,' + fs.readFileSync(process.env.SPACE_JPG).toString('base64');

function font(weight) {
  const file = path.join(fontDir, `roboto-latin-${weight}-normal.woff2`);
  const data = fs.readFileSync(file).toString('base64');
  return `@font-face{font-family:R;font-weight:${weight};src:url(data:font/woff2;base64,${data}) format('woff2')}`;
}

const ICON = {
  titles: '<rect x="3" y="4" width="18" height="16" rx="2"/><path d="M8 4v16M16 4v16"/>',
  unwatched: '<path d="M2 12s3.5-7 10-7 10 7 10 7-3.5 7-10 7S2 12 2 12z"/><path d="M4 4l16 16"/>',
  progress: '<circle cx="12" cy="12" r="9"/><path d="M12 7v5l3 3"/>',
  size: '<ellipse cx="12" cy="6" rx="8" ry="3"/><path d="M4 6v12c0 1.7 3.6 3 8 3s8-1.3 8-3V6M4 12c0 1.7 3.6 3 8 3s8-1.3 8-3"/>',
  added: '<rect x="3.5" y="5" width="17" height="15" rx="2"/><path d="M3.5 10h17M8 3v4M16 3v4M12 13v4M10 15h4"/>',
  local: '<rect x="3" y="13" width="18" height="7" rx="2"/><path d="M5 13l2-8h10l2 8M17 16.5h.01"/>',
  network: '<rect x="3" y="4" width="18" height="6" rx="1.5"/><rect x="3" y="14" width="18" height="6" rx="1.5"/><path d="M7 7h.01M7 17h.01"/>',
  updated: '<path d="M4 12a8 8 0 1 0 2.4-5.7"/><path d="M4 4v4.5h4.5"/><path d="M12 8v4l3 2"/>',
  alert: '<circle cx="12" cy="12" r="9"/><path d="M12 7v6M12 16.5v.01"/>',
  filter: '<path d="M3 5h18l-7 8v6l-4 2v-8z"/>',
  sort: '<path d="M7 4v16M3 16l4 4 4-4M17 20V4M13 8l4-4 4 4"/>',
  list: '<path d="M8 6h13M8 12h13M8 18h13M3.5 6h.01M3.5 12h.01M3.5 18h.01"/>',
  pencil: '<path d="M4 20l4-1 11-11-3-3L5 16z"/>',
};

function icon(name) {
  return `<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.9" stroke-linecap="round"
    stroke-linejoin="round">${ICON[name]}</svg>`;
}

const stats = [
  ['titles', '41', 'Titles'], ['unwatched', '39', 'Unwatched'], ['progress', '2', 'In progress'],
  ['size', '123 GB', 'Total size'], ['added', '0', 'Added this month'], ['local', '40', 'On this Shield · 119 GB'],
  ['network', '1', 'On the network · 3.8 GB'], ['updated', '5 Nov', 'Last updated · 14:40'],
];

function statRow() {
  return `<div class="stats">${stats.map(([ic, value, label]) => `<div class="st"><i>${icon(ic)}</i><b>${value}</b>
    <span>${label}</span></div>`).join('')}</div>`;
}

const status = `<div class="status">${icon('alert')}<b>Scan needs attention</b><span>2 files couldn't be read ·
  see Library Health</span></div>`;

const toolbar = `<div class="tools"><span class="tb">${icon('filter')}Filters</span>
  <span class="tb">${icon('sort')}Sort: Release / Air date</span><span class="tb">Newest first</span>
  <span class="tb">${icon('list')}List view</span><i></i><span class="tb f">${icon('pencil')}Edit Library</span></div>`;

const art = ['linear-gradient(160deg,#c48b4f,#3a2a1a)', 'linear-gradient(160deg,#6fa7b8,#1f3640)',
  'linear-gradient(160deg,#c9b3a0,#4a3a3a)', 'linear-gradient(160deg,#7fb36a,#2a4020)',
  'linear-gradient(160deg,#e8c7c0,#7a3a3a)', 'linear-gradient(160deg,#4a6ab0,#101a40)'];
const names = [['Kingdom', '2025 · 1 season · 6 episodes'], ['The Wild Ones', '2025 · 1 season · 6 episodes'],
  ['Pee-wee as Himself', '2025 · 1 season · 1 episode'], ['Long Way Home', '2025 · 1 season · 1 episode'],
  ['Devil in the Family', '2025 · 1 season · 1 episode'], ['Solar System', '2024 · 1 season · 5 episodes']];
const posters = `<div class="posters">${names.map(([t, s], i) => `<div class="po"><div class="pic" style="background:${art[i]}">
  </div><b>${t}</b><span>${s}</span></div>`).join('')}</div>`;

const nav = `<div class="nav"><b>SUPERNOVA</b><span>Home</span><span>Movies</span><span>TV Shows</span>
  <span class="open">Documentaries</span><i></i><span>Network &amp; Files</span><span>14:46</span></div>`;

const page = cls => nav + `<div class="lib ${cls}"><div class="band">${statRow()}${status}</div>${toolbar}${posters}</div>`;

const css = `${font(400)}${font(500)}${font(700)}
*{box-sizing:border-box;margin:0}
body{width:1920px;height:1080px;overflow:hidden;font-family:R;color:#fff;position:relative;
  background:#09111a url(${SPACE}) top center/1920px 1080px no-repeat}
.nav{position:absolute;left:72px;right:72px;top:34px;height:60px;display:flex;align-items:center;gap:34px;font-size:27px;color:#c3ccdc}
.nav b{font-weight:500;letter-spacing:1px;margin-right:30px;color:#fff}.nav i{flex:1}.nav .open{color:#fff}
.lib{position:absolute;left:72px;right:72px;top:130px}
.band{padding:26px 0 20px}
.b .band{padding:26px 30px 22px;border-radius:24px;background:rgba(22,24,28,.9)}
.stats{display:flex}
.st{flex:1;padding:0 22px;border-left:1px solid rgba(255,255,255,.32);display:flex;flex-direction:column}
.st:first-child{padding-left:0;border-left:none}
.st i{width:28px;height:28px;color:#aeb7c8}.st i svg{width:100%;height:100%}
.st b{font-size:40px;font-weight:500;margin-top:10px;white-space:nowrap}
.st span{font-size:18px;color:#aeb7c8;margin-top:4px;white-space:nowrap}
.status{display:flex;align-items:center;gap:14px;margin-top:22px;padding-top:18px;border-top:1px solid rgba(255,255,255,.24)}
.status svg{width:26px;height:26px;color:#ffc04d}.status b{font-size:22px;font-weight:500}
.status span{font-size:19px;color:#aeb7c8}
.tools{display:flex;gap:16px;align-items:center;margin-top:24px}.tools i{flex:1}
.tb{height:56px;padding:0 22px;border-radius:18px;display:flex;align-items:center;gap:10px;font-size:21px;font-weight:500;
  background:rgba(22,24,28,.72);border:2px solid rgba(255,255,255,.18)}
.tb svg{width:22px;height:22px}.tb.f{background:#F4F6FB;color:#0B0D12;border-color:#F4F6FB}
.posters{display:flex;gap:24px;margin-top:30px}
.po{width:274px;flex:none}.po .pic{height:400px;border-radius:16px;box-shadow:0 8px 22px rgba(0,0,0,.5)}
.po b{display:block;font-size:22px;font-weight:500;margin-top:12px}.po span{display:block;font-size:17px;color:#aeb7c8;margin-top:3px}
`;

(async () => {
  const browser = await chromium.launch();
  const p = await browser.newPage({ viewport: { width: 1920, height: 1080 } });
  for (const [file, cls] of [['01-stats-on-space-black.png', 'a'], ['02-stats-in-charcoal-strip.png', 'b']]) {
    await p.setContent(`<style>${css}</style><body>${page(cls)}</body>`);
    await p.screenshot({ path: path.join(out, file) });
  }
  await browser.close();
})();
