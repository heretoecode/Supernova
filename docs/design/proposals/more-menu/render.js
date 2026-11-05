// Proposal (0.156, owner 5 Nov 2026): the Details More menu with a proper icon for every entry (no fallback circles)
// and Restart only when there is somewhere to resume from.
// Run: FONT_DIR=<folder with roboto-latin-{400,500,700}-normal.woff2> node render.js
const fs = require('fs');
const path = require('path');
const { chromium } = require('playwright');

const out = __dirname;
const fontDir = process.env.FONT_DIR;

function font(weight) {
  const file = path.join(fontDir, `roboto-latin-${weight}-normal.woff2`);
  const data = fs.readFileSync(file).toString('base64');
  return `@font-face{font-family:R;font-weight:${weight};src:url(data:font/woff2;base64,${data}) format('woff2')}`;
}

// Line icons on a 24-unit grid, drawn in the row's content colour.
const ICON = {
  restart: '<path d="M4 12a8 8 0 1 0 2.4-5.7"/><path d="M4 4v4.5h4.5"/>',
  watched: '<circle cx="12" cy="12" r="9"/><path d="M7.5 12.3l3 3 6-6.3"/>',
  unwatched: '<circle cx="12" cy="12" r="9"/>',
  addrow: '<path d="M3 5h13M3 11h10M3 17h8M18 13v10M13 18h10"/>',
  fixmatch: '<circle cx="10.5" cy="10.5" r="6.5"/><path d="M15.5 15.5L21 21"/><path d="M8 10.5l2 2 3.5-4"/>',
  removelib: '<rect x="3" y="4" width="18" height="16" rx="2.5"/><path d="M8 12h8"/>',
  description: '<rect x="5" y="2.5" width="14" height="19" rx="2"/><path d="M8.5 8h7M8.5 12h7M8.5 16h4.5"/>',
  delete: '<path d="M5 7l1 14h12l1-14M3 5h18M9 5V2.5h6V5M10 10v7M14 10v7"/>',
  artwork: '<rect x="3" y="4" width="18" height="16" rx="2.5"/><circle cx="8.5" cy="9.5" r="1.8"/><path d="M3.5 18l5.5-5.5 4 4 3-3 4.5 4.5"/>',
  subtitles: '<rect x="2.5" y="5" width="19" height="14" rx="2.5"/><path d="M6 11h3M11 11h7M6 15h7M15 15h3"/>',
  info: '<circle cx="12" cy="12" r="9"/><path d="M12 11v6"/><path d="M12 7.5v.01"/>',
};

function icon(name) {
  return `<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.9" stroke-linecap="round"
    stroke-linejoin="round">${ICON[name]}</svg>`;
}

function menu(groups) {
  return `<div class="side">${groups.map(([title, items]) => `<div class="group"><span>${title}</span><i></i></div>`
    + items.map(([name, label, focused]) => `<div class="item${focused ? ' f' : ''}">${icon(name)}<b>${label}</b></div>`)
      .join('')).join('')}</div>`;
}

const film = menu([
  ['Library', [['watched', 'Mark Watched', true], ['addrow', 'Add to Row'], ['fixmatch', 'Fix Match'],
               ['removelib', 'Remove From Library']]],
  ['Files &amp; Media', [['description', 'Full Description'], ['subtitles', 'Subtitles'], ['artwork', 'Artwork'],
                         ['delete', 'Delete']]],
]);

const series = menu([
  ['Playback', [['restart', 'Restart Episode', true]]],
  ['Library', [['watched', 'Mark Watched'], ['info', 'Change Info of All Episodes'], ['addrow', 'Add to Row'],
               ['fixmatch', 'Fix Match'], ['removelib', 'Remove From Library']]],
  ['Files &amp; Media', [['description', 'Full Description'], ['artwork', 'Artwork'], ['delete', 'Delete']]],
]);

const hero = (title, meta, play) => `<div class="hero"><div class="copy"><div class="logo">${title}</div>
  <div class="meta">${meta}</div><div class="acts"><span class="btn">▶&nbsp; ${play}</span>
  <span class="btn f">···&nbsp; More</span></div></div></div><div class="dim"></div>`;

const css = `${font(400)}${font(500)}${font(700)}
*{box-sizing:border-box;margin:0}
body{width:1920px;height:1080px;overflow:hidden;font-family:R;color:#fff;position:relative;background:#0b0d12}
.hero{position:absolute;inset:0;background:linear-gradient(120deg,#3a2a2a 0%,#6b3b33 35%,#1f2a3a 100%)}
.copy{position:absolute;left:128px;top:240px}
.logo{font-size:84px;font-weight:700;letter-spacing:8px;color:#f6c2b8}
.meta{font-size:25px;font-weight:500;margin-top:18px}
.acts{display:flex;gap:18px;margin-top:300px}
.btn{height:64px;padding:0 30px;border-radius:20px;display:flex;align-items:center;font-size:24px;font-weight:500;
  background:rgba(22,24,28,.72);border:2px solid rgba(255,255,255,.22)}
.btn.f{background:#F4F6FB;color:#0B0D12;border-color:#F4F6FB}
.dim{position:absolute;inset:0;background:rgba(0,0,0,.35)}
.side{position:absolute;right:56px;top:112px;width:560px;border-radius:26px;background:#16181C;padding:22px 22px 26px;
  box-shadow:0 18px 50px rgba(0,0,0,.5)}
.group{display:flex;align-items:center;gap:14px;padding:16px 16px 8px}.group span{font-size:19px;color:#9aa4b6}
.group i{flex:1;height:1px;background:rgba(255,255,255,.24)}
.item{height:68px;border-radius:18px;padding:0 20px;display:flex;align-items:center;gap:20px;font-size:25px;color:#e6ebf2}
.item svg{width:30px;height:30px;flex:none}.item b{font-weight:400}
.item.f{background:#F4F6FB;color:#0B0D12}.item.f b{font-weight:500}
`;

const shots = [
  ['01-film-not-started.png', hero('YOUR MOTHER', '2026 · 1 h 52 min · Drama, Crime, Thriller', 'Play') + film],
  ['02-series-in-progress.png', hero('DARK MATTER', '2024 · 2 Seasons · Drama, Sci-Fi &amp; Fantasy',
    'Resume S2 E10 · 31 min left') + series],
];

(async () => {
  const browser = await chromium.launch();
  const page = await browser.newPage({ viewport: { width: 1920, height: 1080 } });
  for (const [file, body] of shots) {
    await page.setContent(`<style>${css}</style><body>${body}</body>`);
    await page.screenshot({ path: path.join(out, file) });
  }
  await browser.close();
})();
