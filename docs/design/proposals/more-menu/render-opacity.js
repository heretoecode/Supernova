// Proposal (0.157, owner 5 Nov 2026): the More menu panel at four opacities, all with no outline, over bright artwork.
// (Round 1, 01-02: a proper icon for every entry; Restart only when there is somewhere to resume from.)
// Run: FONT_DIR=<folder with roboto-latin-{400,500,700}-normal.woff2> node render-opacity.js
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

function panel(alpha, label) {
  const items = [['restart', 'Restart Episode', true], ['watched', 'Mark Watched'], ['addrow', 'Add to Row'],
    ['fixmatch', 'Fix Match'], ['removelib', 'Remove From Library'], ['artwork', 'Artwork'], ['delete', 'Delete']];
  return `<div class="col"><div class="pan" style="background:rgba(22,24,28,${alpha})">
    <div class="group"><span>Playback</span><i></i></div>${items.slice(0, 1).map(row).join('')}
    <div class="group"><span>Library</span><i></i></div>${items.slice(1, 5).map(row).join('')}
    <div class="group"><span>Files &amp; Media</span><i></i></div>${items.slice(5).map(row).join('')}</div>
    <p>${label}</p></div>`;
}

function row([name, label, focused]) {
  return `<div class="item${focused ? ' f' : ''}">${icon(name)}<b>${label}</b></div>`;
}

const css = `${font(400)}${font(500)}${font(700)}
*{box-sizing:border-box;margin:0}
body{width:1920px;height:1080px;overflow:hidden;font-family:R;color:#fff;position:relative;
  background:radial-gradient(ellipse 30% 40% at 20% 30%,#f2c94c,rgba(242,201,76,0) 70%),
    radial-gradient(ellipse 35% 45% at 70% 65%,#4fc3f7,rgba(79,195,247,0) 70%),
    radial-gradient(ellipse 30% 40% at 45% 80%,#ef6c5a,rgba(239,108,90,0) 70%),
    linear-gradient(120deg,#d98a6a,#7fb2d9 50%,#e8d6a0)}
.wrap{position:absolute;inset:0;display:flex;gap:40px;padding:60px 60px 0}
.col{flex:1}.pan{border-radius:26px;padding:16px 16px 20px;box-shadow:0 18px 50px rgba(0,0,0,.45)}
.col p{margin-top:18px;text-align:center;font-size:26px;font-weight:500;color:#0b0d12;
  background:rgba(255,255,255,.8);border-radius:14px;padding:8px 0}
.group{display:flex;align-items:center;gap:12px;padding:14px 14px 6px}.group span{font-size:17px;color:#aeb7c8}
.group i{flex:1;height:1px;background:rgba(255,255,255,.24)}
.item{height:60px;border-radius:16px;padding:0 16px;display:flex;align-items:center;gap:16px;font-size:22px;color:#eef1f6}
.item svg{width:26px;height:26px;flex:none}.item b{font-weight:400;white-space:nowrap}
.item.f{background:#F4F6FB;color:#0B0D12}.item.f b{font-weight:500}
`;

const shots = [
  ['03-opacity-100-97-90-80.png', `<div class="wrap">${panel(1, '100% · solid')}${panel(.97, '97%')}${panel(.9, '90%')}
    ${panel(.8, '80%')}</div>`],
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
