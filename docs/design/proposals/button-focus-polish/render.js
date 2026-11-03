// Proposal (0.154 walk-through): focused-button polish — label weight and focus shadow.
// Real Android sizes on a 1080p Shield (1dp = 2px). Roboto from @fontsource/roboto (FONT_DIR).
// Run: FONT_DIR=<dir with roboto-latin-{300,400,500}-normal.woff2> node render.js
const fs = require('fs');
const path = require('path');
const { chromium } = require('playwright');
const out = __dirname;
const fontDir = process.env.FONT_DIR;
const font = w => `@font-face{font-family:R;font-weight:${w};src:url(data:font/woff2;base64,${fs.readFileSync(path.join(fontDir, `roboto-latin-${w}-normal.woff2`)).toString('base64')}) format('woff2')}`;

// Tokens from SupernovaStyle (0.153)
const FOCUS_FILL = '#F4F6FB', FOCUS_TEXT = '#0B0D12', REST = 'rgba(0,0,0,0.72)';
const info = c => `<svg width="36" height="36" viewBox="0 0 24 24" fill="none" stroke="${c}" stroke-width="1.8" stroke-linecap="round"><circle cx="12" cy="12" r="9"/><path d="M12 11v6M12 7.5v.5"/></svg>`;

// shadow: 'stepped' (0.153), 'none', 'soft', 'outline'
function button({ focused, weight = 300, iconColour, shadow = 'none' }) {
  const fill = focused ? FOCUS_FILL : REST;
  const text = focused ? FOCUS_TEXT : '#fff';
  const ic = iconColour || text;
  let deco = '';
  if (focused && shadow === 'stepped')
    deco = `box-shadow:0 2px 0 0 rgba(0,0,0,.15),0 4px 0 1px rgba(0,0,0,.15),-2px 5px 0 2px rgba(0,0,0,.15),-3px 6px 0 3px rgba(0,0,0,.15);`;
  if (focused && shadow === 'soft') deco = `box-shadow:0 0 14px rgba(0,0,0,.18);`;
  if (focused && shadow === 'outline') deco = `box-shadow:0 0 0 2px rgba(11,13,18,.55);`;
  return `<div class="btn" style="background:${fill};color:${text};font-weight:${weight};${deco}">${info(ic)}<span>More Info</span></div>`;
}

const bgLight = `background:linear-gradient(160deg,#e3e0dc,#cfcac4 60%,#d8d4cf)`;
const bgDark = `background:linear-gradient(160deg,#1d2433,#0b0e16 70%)`;
const css = `${font(300)}${font(400)}${font(500)}
*{box-sizing:border-box;margin:0}
body{width:1920px;height:1080px;overflow:hidden;font-family:R;background:#000;color:#fff;padding:56px 72px}
h1{font-weight:500;font-size:44px;margin-bottom:10px}
p.sub{font-weight:400;font-size:24px;color:#c9d3e6;margin-bottom:34px}
.grid{display:grid;gap:26px}
.cell{border-radius:22px;padding:30px 34px;display:flex;flex-direction:column;gap:22px;min-height:250px}
.label{font-weight:500;font-size:26px}
.note{font-weight:400;font-size:21px;opacity:.85;line-height:1.35}
.light .label,.light .note{color:#1b1f27}
.dark .label,.dark .note{color:#fff}
.row{display:flex;gap:24px;align-items:center}
.btn{height:88px;border-radius:24px;padding:0 34px 0 30px;display:inline-flex;align-items:center;gap:16px;font-size:28px;letter-spacing:.2px;white-space:nowrap;flex:none}
.tag{display:inline-block;padding:4px 14px;border-radius:999px;background:#2f6df0;color:#fff;font-weight:500;font-size:20px;margin-left:12px}`;

const page = (title, sub, body) => `<html><head><style>${css}</style></head><body><h1>${title}</h1><p class="sub">${sub}</p>${body}</body></html>`;

// 1 — label weight (icon fixed: black on focus)
const weights = [
  [300, 'A — Light (today)', 'Thin strokes: reads grey from the sofa.'],
  [400, 'B — Regular', 'Solid black, still slim.'],
  [500, 'C — Medium', 'Bold enough to read across the room, like Netflix / Google TV.'],
];
const weightCells = bg => weights.map(([w, name, note], i) => `
  <div class="cell ${bg}" style="${bg === 'light' ? bgLight : bgDark}">
    <div class="label">${name}${i === 2 ? '<span class="tag">Recommended</span>' : ''}</div>
    <div class="row">${button({ focused: true, weight: w })}${button({ focused: false, weight: w })}</div>
    <div class="note">${note} Left: focused · right: at rest.</div>
  </div>`).join('');
const weightPage = page('Button label weight', 'Icon fixed to turn black on focus in every option. Pale background like the Home hero (top) and dark (bottom).',
  `<div class="grid" style="grid-template-columns:repeat(3,1fr)">${weightCells('light')}${weightCells('dark')}</div>`);

// 2 — focus shadow (Medium weight, icon fixed)
const shadows = [
  ['stepped', 'Today (0.153)', 'Stepped, lopsided shadow — the grey "bleed" on pale images.'],
  ['none', '1 — No shadow', 'Clean on any background. The white box already stands out.'],
  ['soft', '2 — Faint, even glow', 'A hint of lift; can still show faintly on pale images.'],
  ['outline', '3 — Thin dark outline', 'Crisp edge on any background.'],
];
const shadowCells = bg => shadows.map(([s, name, note]) => `
  <div class="cell ${bg}" style="${bg === 'light' ? bgLight : bgDark};min-height:230px">
    <div class="label">${name}${s === 'none' ? '<span class="tag">Recommended</span>' : ''}</div>
    <div class="row">${button({ focused: true, weight: 500, shadow: s })}</div>
    <div class="note">${note}</div>
  </div>`).join('');
const shadowPage = page('Focus shadow', 'Focused button, Medium label, black icon. Pale background (top) and dark (bottom).',
  `<div class="grid" style="grid-template-columns:repeat(4,1fr)">${shadowCells('light')}${shadowCells('dark')}</div>`);

// 3 — today vs proposed, on the hero
const heroPage = page('Home hero — today vs proposed', 'The More Info button on a pale hero image, focused.',
  `<div class="grid" style="grid-template-columns:1fr 1fr">
    <div class="cell light" style="${bgLight};min-height:560px;justify-content:flex-end"><div class="label">Today (0.153)</div>
      <div class="row">${button({ focused: true, weight: 300, iconColour: '#ffffff', shadow: 'stepped' })}</div>
      <div class="note">White icon on the white box, thin grey-looking label, stepped shadow.</div></div>
    <div class="cell light" style="${bgLight};min-height:560px;justify-content:flex-end"><div class="label">Proposed</div>
      <div class="row">${button({ focused: true, weight: 500, shadow: 'none' })}</div>
      <div class="note">Black icon, Medium label, no shadow.</div></div>
  </div>`);

(async () => {
  const browser = await chromium.launch();
  const tab = await browser.newPage({ viewport: { width: 1920, height: 1080 } });
  for (const [name, html] of [['01-label-weight.png', weightPage], ['02-focus-shadow.png', shadowPage], ['03-hero-today-vs-proposed.png', heroPage]]) {
    await tab.setContent(html, { waitUntil: 'load' });
    await tab.evaluate(() => document.fonts.ready);
    await tab.screenshot({ path: path.join(out, name) });
  }
  await browser.close();
})();
