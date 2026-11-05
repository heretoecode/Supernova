// Proposal (0.157, owner 5 Nov 2026): the global panel look (#16181C at 90%, no outline, focused row filled white)
// applied to Playback Settings and Edit Page, showing how in-row controls look on the white focus.
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

const ICON = {
  cc: '<rect x="2.5" y="5" width="19" height="14" rx="2.5"/><path d="M10 10.5a2 2 0 1 0 0 3M17 10.5a2 2 0 1 0 0 3"/>',
  list: '<path d="M8 6h13M8 12h13M8 18h13M3.5 6h.01M3.5 12h.01M3.5 18h.01"/>',
  download: '<path d="M12 3v12M7 10l5 5 5-5M4 20h16"/>',
  size: '<path d="M3 19l5-13 5 13M5 14h6M15 19l3-8 3 8M16 16.5h4"/>',
  palette: '<circle cx="12" cy="12" r="9"/><circle cx="8" cy="10" r="1.2"/><circle cx="12" cy="7.5" r="1.2"/><circle cx="16" cy="10" r="1.2"/>',
  updown: '<path d="M12 3v18M8 7l4-4 4 4M8 17l4 4 4-4"/>',
  outline: '<rect x="4" y="4" width="16" height="16" rx="2"/><path d="M9 16l3-8 3 8M10 13.5h4"/>',
  bg: '<rect x="4" y="4" width="16" height="16" rx="2"/><path d="M4 12l8-8M4 20L20 4M12 20l8-8"/>',
  drop: '<path d="M12 3s6 6.5 6 11a6 6 0 0 1-12 0c0-4.5 6-11 6-11z"/>',
  timer: '<circle cx="12" cy="13" r="8"/><path d="M12 9v4l2.5 2.5M10 2h4"/>',
  pencil: '<path d="M4 20l4-1 11-11-3-3L5 16z"/>',
  film: '<rect x="4" y="3" width="16" height="18" rx="2"/><path d="M8 3v18M16 3v18M4 8h4M4 13h4M4 18h4M16 8h4M16 13h4M16 18h4"/>',
  filter: '<path d="M3 5h18l-7 8v6l-4 2v-8z"/>',
  calendar: '<rect x="3.5" y="5" width="17" height="15" rx="2"/><path d="M3.5 10h17M8 3v4M16 3v4"/>',
  eye: '<path d="M2 12s3.5-7 10-7 10 7 10 7-3.5 7-10 7S2 12 2 12z"/><circle cx="12" cy="12" r="3"/>',
  star: '<path d="M12 3l2.7 5.6 6.1.9-4.4 4.3 1 6.1L12 17l-5.4 2.9 1-6.1-4.4-4.3 6.1-.9z"/>',
};

function icon(name) {
  return `<svg class="ic" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.9" stroke-linecap="round"
    stroke-linejoin="round">${ICON[name]}</svg>`;
}

const seg = (items, on) => `<span class="seg">${items.map((t, i) => `<em class="${i === on ? 'on' : ''}">${t}</em>`)
  .join('<i>|</i>')}</span>`;
const sw = on => `<span class="sw${on ? ' on' : ''}"><b></b></span>`;
const step = v => `<span class="step"><i>‹</i><em class="on">${v}</em><i>›</i></span>`;
const chev = v => `<span class="chev"><em>${v}</em><i>›</i></span>`;
const swatches = chosen => `<span class="dots">${['#ffffff', '#ffd400', '#19e6e6', '#4cc554', '#f06a9b']
  .map((c, i) => `<b class="${i === chosen ? 'chosen' : ''}" style="background:${c}"></b>`).join('')}</span>`;
const plusminus = v => `<span class="step"><i>−</i><em>${v}</em><i>+</i></span>`;

function row(ic, label, control, focused) {
  return `<div class="row${focused ? ' f' : ''}">${icon(ic)}<b>${label}</b>${control}</div>`;
}
const head = t => `<div class="head"><span>${t}</span><i></i></div>`;

function playback(focus) {
  const rows = [
    ['cc', 'Subtitles', sw(true)], ['list', 'Subtitle Track', step('🇬🇧 English (TEXT)')],
    ['download', 'Download Subtitles', chev('Find &amp; Download')],
  ];
  const look = [
    ['size', 'Text Size', seg(['Small', 'Medium', 'Large'], 1)], ['palette', 'Text Colour', swatches(0)],
    ['updown', 'Vertical Position', step('5%')], ['outline', 'Outline', sw(true)], ['bg', 'Background', sw(false)],
    ['drop', 'Background Opacity', step('100%')], ['timer', 'Subtitle Delay', plusminus('0 ms')],
  ];
  const r = (x, i) => row(x[0], x[1], x[2], x[1] === focus);
  return `<div class="pan"><div class="tabs"><span class="open">${icon('cc')}Subtitles</span><span>Audio</span>
    <span>Playback</span></div>${head('Subtitle selection')}${rows.map(r).join('')}
    ${head('Subtitle appearance')}${look.map(r).join('')}</div>`;
}

function editPage(focus) {
  const a = [['pencil', 'Page Name', chev('Documentaries')], ['film', 'Includes', seg(['Movies', 'TV Shows', 'Both'], 2)]];
  const b = [['filter', 'Genres', chev('Documentary')], ['filter', 'Match', seg(['Any', 'All'], 0)],
    ['calendar', 'Year', step('Any')], ['eye', 'Watched', step('All')], ['star', 'Minimum Rating', step('Any')]];
  const r = x => row(x[0], x[1], x[2], x[1] === focus);
  return `<div class="pan"><div class="ttl"><div><b>Edit Page</b><span>41 titles match · 123 GB</span></div>
    <em class="save">✓ Save Changes</em></div>${head('Page')}${a.map(r).join('')}${head('What to show')}
    ${b.map(r).join('')}</div>`;
}

const css = `${font(400)}${font(500)}${font(700)}
*{box-sizing:border-box;margin:0}
body{width:1920px;height:1080px;overflow:hidden;font-family:R;color:#fff;position:relative;
  background:radial-gradient(ellipse 40% 50% at 25% 40%,#7a8a6a,rgba(0,0,0,0) 70%),
    radial-gradient(ellipse 40% 50% at 75% 60%,#5a6a8a,rgba(0,0,0,0) 70%),linear-gradient(120deg,#3a3430,#20262e)}
.wrap{position:absolute;inset:0;display:flex;gap:60px;justify-content:center;align-items:flex-start;padding-top:80px}
.pan{width:760px;border-radius:26px;padding:20px 22px 24px;background:rgba(22,24,28,.9);box-shadow:0 18px 50px rgba(0,0,0,.45)}
.tabs{display:flex;gap:12px;margin-bottom:6px}.tabs span{flex:1;height:58px;border-radius:18px;display:flex;align-items:center;
  justify-content:center;gap:10px;font-size:22px;font-weight:500;background:rgba(255,255,255,.08)}
.tabs span.open{background:rgba(255,255,255,.16)}.tabs .ic{width:24px;height:24px}
.ttl{display:flex;justify-content:space-between;align-items:flex-start;padding:4px 10px 6px}
.ttl b{display:block;font-size:30px;font-weight:700}.ttl span{display:block;font-size:18px;color:#4cd3a0;margin-top:4px}
.save{font-style:normal;font-size:20px;font-weight:500;padding:12px 20px;border-radius:16px;background:rgba(255,255,255,.12)}
.head{display:flex;align-items:center;gap:12px;padding:16px 12px 6px}.head span{font-size:17px;color:#aeb7c8}
.head i{flex:1;height:1px;background:rgba(255,255,255,.24)}
.row{height:62px;border-radius:16px;padding:0 14px;display:flex;align-items:center;gap:16px;font-size:22px;color:#eef1f6}
.row .ic{width:26px;height:26px;flex:none}.row>b{font-weight:400;flex:1;white-space:nowrap}
.seg,.step,.chev{display:flex;align-items:center;gap:10px;font-size:21px}
.seg em,.step em,.chev em{font-style:normal}.seg em{color:#c9d3e6}.seg em.on,.step em.on{color:#4fb8ff;font-weight:500}
.seg i,.step i,.chev i{font-style:normal;color:#8f98a8}.step i{font-size:26px;padding:0 4px}
.sw{width:58px;height:32px;border-radius:16px;background:#4a505c;position:relative}
.sw b{position:absolute;left:4px;top:4px;width:24px;height:24px;border-radius:50%;background:#f4f6fb}
.sw.on{background:#2f8cff}.sw.on b{left:30px}
.dots{display:flex;gap:10px}.dots b{width:30px;height:30px;border-radius:50%}
.dots b.chosen{box-shadow:0 0 0 3px #16181C,0 0 0 6px #fff}
/* Focused row: white fill; every part of the row switches to dark ink so nothing is white on white. */
.row.f{background:#F4F6FB;color:#0B0D12}.row.f>b{font-weight:500}
.row.f .seg em{color:#4a5262}.row.f .seg em.on,.row.f .step em.on{color:#0B57D0;font-weight:700}
.row.f .seg i,.row.f .step i,.row.f .chev i{color:#0B0D12}.row.f .chev em,.row.f .step em{color:#0B0D12}
.row.f .step em.on{color:#0B57D0}
.row.f .sw{background:#c3c9d4}.row.f .sw b{background:#0B0D12}.row.f .sw.on{background:#0B57D0}.row.f .sw.on b{background:#fff}
.row.f .dots b{box-shadow:0 0 0 1.5px rgba(11,13,18,.35)}.row.f .dots b.chosen{box-shadow:0 0 0 3px #F4F6FB,0 0 0 6px #0B0D12}
`;

const shots = [
  ['01-switch-and-stepper.png', `<div class="wrap">${playback('Outline')}${editPage('Year')}</div>`],
  ['02-choices-and-colours.png', `<div class="wrap">${playback('Text Colour')}${editPage('Includes')}</div>`],
  ['03-switch-off-and-link.png', `<div class="wrap">${playback('Background')}${editPage('Genres')}</div>`],
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
