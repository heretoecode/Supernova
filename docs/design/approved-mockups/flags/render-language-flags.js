// Builds 72x72 language flags in the Twemoji frame (flag 36x26 at y=5, corner radius 4, in a 36x36 box),
// then a review sheet. Hand-drawn flags follow the official designs using simple geometry.
const fs = require('fs'), path = require('path');
const { chromium } = require('playwright');
const dir = __dirname, out = path.join(dir, 'out');
fs.mkdirSync(out, { recursive: true });

const W = 36, H = 26;
// 5-pointed star centred at (cx,cy) with outer radius r
const star = (cx, cy, r, n = 5, inner = 0.382) => {
  const pts = [];
  for (let i = 0; i < n * 2; i++) {
    const rad = i % 2 ? r * inner : r, a = -Math.PI / 2 + i * Math.PI / n;
    pts.push((cx + rad * Math.cos(a)).toFixed(3) + ',' + (cy + rad * Math.sin(a)).toFixed(3));
  }
  return pts.join(' ');
};
const ermine = (x, y) => `<g transform="translate(${x},${y}) scale(.42)" fill="#000"><path d="M0-3.2c.7 1.4.7 2.6 0 3.6-.7-1-.7-2.2 0-3.6z"/><circle cx="-1.6" cy="-3.6" r=".7"/><circle cx="0" cy="-4.4" r=".7"/><circle cx="1.6" cy="-3.6" r=".7"/><path d="M0 .2L-1.8 3.2h3.6z"/></g>`;

// Hand-drawn designs in a 36x26 coordinate space.
const drawn = {
  // Esperanto: green field, white canton (square, half height), green five-pointed star
  eo: `<rect width="36" height="26" fill="#009900"/><rect width="13" height="13" fill="#fff"/><polygon points="${star(6.5, 6.9, 5.3)}" fill="#009900"/>`,
  // Sami: red | green | yellow | blue verticals; ring centred on green/yellow, blue half over red, red half over blue
  se: `<rect width="36" height="26" fill="#0035AD"/><rect width="16" height="26" fill="#DC241F"/><rect x="14" width="2.6" height="26" fill="#007229"/><rect x="16.6" width="2.6" height="26" fill="#FFCE00"/>
       <path d="M16.6 5.6a7.4 7.4 0 0 0 0 14.8v-2.2a5.2 5.2 0 0 1 0-10.4z" fill="#0035AD"/><path d="M16.6 5.6a7.4 7.4 0 0 1 0 14.8v-2.2a5.2 5.2 0 0 0 0-10.4z" fill="#DC241F"/>`,
  // Tatarstan: green over red with a narrow white band (1/15 of height)
  tt: `<rect width="36" height="26" fill="#ED2E38"/><rect width="36" height="12.13" fill="#00A651"/><rect y="12.13" width="36" height="1.74" fill="#fff"/>`,
  // Kurdistan: red / white / green with a golden 21-ray sun
  ku: `<rect width="36" height="26" fill="#fff"/><rect width="36" height="8.67" fill="#ED2024"/><rect y="17.33" width="36" height="8.67" fill="#278E43"/>
       <polygon points="${star(18, 13, 6.6, 21, 0.62)}" fill="#FEBD11"/><circle cx="18" cy="13" r="3.2" fill="#FEBD11"/>`,
  // Brittany (Gwenn-ha-Du): 9 stripes black/white starting black; white canton with ermine spots
  br: `<rect width="36" height="26" fill="#fff"/>${[0, 2, 4, 6, 8].map(i => `<rect y="${(i * 26 / 9).toFixed(3)}" width="36" height="${(26 / 9).toFixed(3)}" fill="#000"/>`).join('')}
       <rect width="15.6" height="14.44" fill="#fff"/>${[[2.6, 4.4], [7.8, 4.4], [13, 4.4], [5.2, 8.6], [10.4, 8.6], [2.6, 12.8], [7.8, 12.8], [13, 12.8]].map(([x, y]) => ermine(x, y)).join('')}`,
  // Extremadura: green / white / black horizontal tricolour (civil flag, without arms)
  ext: `<rect width="36" height="26" fill="#000"/><rect width="36" height="17.33" fill="#fff"/><rect width="36" height="8.67" fill="#00A650"/>`,
  // Occitan: red field, golden cross of Toulouse (cross pattée, each arm ending in three balls)
  oc: `<rect width="36" height="26" fill="#D52B1E"/><g fill="#FCD116" transform="translate(18,13)">
       ${[0, 90, 180, 270].map(r => `<g transform="rotate(${r})"><path d="M0 0L-1.1-1.4-3.1-8.3Q0-7.4 3.1-8.3L1.1-1.4Z"/><circle cx="-3.3" cy="-8.9" r=".95"/><circle cx="0" cy="-9.6" r=".95"/><circle cx="3.3" cy="-8.9" r=".95"/></g>`).join('')}</g>`,
};
const iconsSvg = name => fs.readFileSync(path.join(dir, 'src', name + '.svg'), 'utf8');
const framed = inner => `<svg xmlns="http://www.w3.org/2000/svg" width="72" height="72" viewBox="0 0 36 36"><defs><clipPath id="c"><rect y="5" width="36" height="26" rx="4"/></clipPath></defs><g clip-path="url(#c)"><g transform="translate(0,5)">${inner}</g></g></svg>`;
const fromIcons = name => `<image href="data:image/svg+xml;base64,${Buffer.from(iconsSvg(name)).toString('base64')}" width="36" height="26" preserveAspectRatio="xMidYMid slice"/>`;

const flags = {
  lang_eo: framed(drawn.eo), lang_se: framed(drawn.se), lang_tt: framed(drawn.tt), lang_ku: framed(drawn.ku),
  lang_br: framed(drawn.br), lang_ext: framed(drawn.ext), lang_oc: framed(drawn.oc),
  region_es_ct: framed(fromIcons('es-ct')), region_es_ga: framed(fromIcons('es-ga')), region_es_pv: framed(fromIcons('es-pv')), region_arab: framed(fromIcons('arab')),
};

(async () => {
  const browser = await chromium.launch();
  const page = await browser.newPage({ viewport: { width: 72, height: 72 } });
  for (const [name, svg] of Object.entries(flags)) {
    await page.setContent(`<html><body style="margin:0;background:transparent">${svg}</body></html>`);
    await page.locator('svg').screenshot({ path: path.join(out, name + '.png'), omitBackground: true });
  }
  fs.copyFileSync(path.join(dir, 'src', 'wls.png'), path.join(out, 'region_gb_wls.png'));
  fs.copyFileSync(path.join(dir, 'src', 'sct.png'), path.join(out, 'region_gb_sct.png'));

  // Review sheet: large and at real on-screen size, next to existing Twemoji references.
  const b64 = f => 'data:image/png;base64,' + fs.readFileSync(f).toString('base64');
  const res = c => b64(`/home/user/Supernova/res/drawable-nodpi/region_${c}.png`);
  const items = [
    ['Welsh', 'region_gb_wls', 'Twemoji'], ['Scottish Gaelic', 'region_gb_sct', 'Twemoji'],
    ['Catalan', 'region_es_ct', 'flag-icons'], ['Galician', 'region_es_ga', 'flag-icons'], ['Basque', 'region_es_pv', 'flag-icons'], ['Arabic (Arab League)', 'region_arab', 'flag-icons'],
    ['Esperanto', 'lang_eo', 'drawn'], ['Northern Sami', 'lang_se', 'drawn'], ['Tatar (Tatarstan)', 'lang_tt', 'drawn'], ['Kurdish (Kurdistan)', 'lang_ku', 'drawn'],
    ['Breton (Brittany)', 'lang_br', 'drawn'], ['Extremaduran', 'lang_ext', 'drawn'], ['Occitan', 'lang_oc', 'drawn'],
  ];
  const card = (label, src, tag) => `<div class="card"><img class="big" src="${src}"><div class="row"><img class="small" src="${src}"><span>${label}</span></div><div class="tag ${tag}">${tag}</div></div>`;
  const sheet = `<html><head><style>body{margin:0;background:#141a52;font-family:Roboto,Arial,sans-serif;color:#fff;padding:28px;width:1240px}
    h1{font-size:28px;margin:0 0 6px}p{margin:0 0 20px;color:#c9d6ff;font-size:17px}
    .grid{display:grid;grid-template-columns:repeat(5,1fr);gap:16px}.card{background:#1d2466;border:1.5px solid #3e5aa8;border-radius:14px;padding:14px;display:flex;flex-direction:column;align-items:center;gap:10px}
    .big{width:144px;height:144px;image-rendering:auto}.row{display:flex;align-items:center;gap:8px;font-size:17px}.small{width:22px;height:22px}
    .tag{font-size:13px;padding:2px 8px;border-radius:8px}.Twemoji{background:#2f6df0}.flag-icons{background:#7a4fd8}.drawn{background:#c77b1e}.reference{background:#555}</style></head><body>
    <h1>New language flags — for approval</h1><p>Each shown large and at real size (22px, as in Settings). Existing Twemoji flags on the right for style comparison.</p>
    <div class="grid">${items.map(([l, f, t]) => card(l, b64(path.join(out, f + '.png')), t)).join('')}${card('English (existing)', res('gb'), 'reference')}${card('French (existing)', res('fr'), 'reference')}</div></body></html>`;
  const p2 = await browser.newPage({ viewport: { width: 1300, height: 900 } });
  await p2.setContent(sheet);
  await p2.screenshot({ path: path.join(dir, 'flag-review-sheet.png'), fullPage: true });
  await browser.close();
  console.log('ok');
})();
