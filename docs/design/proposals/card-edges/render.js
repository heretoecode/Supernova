// Proposal (0.157, owner 5 Nov 2026): one edge rule for every artwork card (hero, Home rows, library posters, Details
// card). A: no edge at rest. B: a faint edge on every card. C: a white outline on every card. In all three the focused
// card grows and gets a solid white ring.
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

const art = ['linear-gradient(160deg,#4a7fc0,#10203c)', 'linear-gradient(160deg,#c48b4f,#3a2a1a)',
  'linear-gradient(160deg,#6fa7b8,#1f3640)', 'linear-gradient(160deg,#7fb36a,#2a4020)',
  'linear-gradient(160deg,#e8c7c0,#7a3a3a)', 'linear-gradient(160deg,#9d8cc4,#231c38)',
  'linear-gradient(160deg,#d8be7a,#3b3016)'];

const row = [['Nocturne', 'Resume · S1 E1', 30], ['Marshals', 'Resume · S1 E1', 62], ['MobLand', 'Resume · S2 E7', 12],
  ['Forgotten Island', '', 0], ['Kingdom', 'Up Next · S1 E3', 0]];
const posters = ['Louis Theroux', 'The Green Planet', 'Car Workshop', 'Exterminate All the Brutes',
  'I’ll Be Gone in the Dark', 'McMillion$', 'Dynasties'];

function page(focusRow) {
  const cards = row.map(([t, s, p], i) => `<div class="card${i === focusRow ? ' f' : ''}" style="background:${art[i + 1]}">
    <div class="g"></div><div class="t"><b>${t}</b>${s ? `<span>${s}</span>` : ''}</div>
    ${p ? `<i class="pr"><b style="width:${p}%"></b></i>` : ''}</div>`).join('');
  const grid = posters.map((t, i) => `<div class="po" style="background:${art[(i + 3) % art.length]}"></div>`).join('');
  return `<div class="hero" style="background:${art[0]}"><div class="hp"></div><div class="hc"><div class="logo">PRIMETIME</div>
    <div class="meta">2026 · Thriller, Drama</div><span class="btn">ⓘ&nbsp; More Info</span></div></div>
    <div class="rh">Continue Watching</div><div class="rail">${cards}</div>
    <div class="rh">Documentaries</div><div class="grid">${grid}</div>`;
}

const css = `${font(400)}${font(500)}${font(700)}
*{box-sizing:border-box;margin:0}
body{width:1920px;height:1080px;overflow:hidden;font-family:R;color:#fff;position:relative;
  background:#09111a url(${SPACE}) top center/1920px 1080px no-repeat;padding:40px 72px}
.hero{position:relative;height:430px;border-radius:24px;overflow:hidden}
.hp{position:absolute;inset:0;background:radial-gradient(ellipse 50% 80% at 15% 55%,rgba(0,0,0,.55),rgba(0,0,0,0))}
.hc{position:absolute;left:50px;top:60px}.logo{font-size:76px;font-weight:700;letter-spacing:4px;color:#f1ead8}
.meta{font-size:22px;font-weight:500;margin:14px 0 120px}
.btn{display:inline-flex;height:56px;padding:0 24px;border-radius:18px;align-items:center;font-size:21px;font-weight:500;
  background:rgba(22,24,28,.72);border:2px solid rgba(255,255,255,.18)}
.rh{font-size:24px;font-weight:500;margin:28px 0 14px}
.rail{display:flex;gap:24px;align-items:center;height:210px}
.card{position:relative;flex:none;width:320px;height:180px;border-radius:16px;overflow:hidden}
.card.f{width:356px;height:200px}
.g{position:absolute;inset:0;background:linear-gradient(0deg,rgba(0,0,0,.78),rgba(0,0,0,0) 60%)}
.t{position:absolute;left:16px;bottom:20px}.t b{display:block;font-size:22px;font-weight:700}.t span{font-size:17px;color:#e6ebf5}
.pr{position:absolute;left:14px;right:14px;bottom:9px;height:5px;border-radius:3px;background:rgba(255,255,255,.3)}
.pr b{display:block;height:100%;border-radius:3px;background:#fff}
.grid{display:flex;gap:24px}.po{flex:none;width:226px;height:330px;border-radius:14px}
/* the three edge rules */
.a .card.f{box-shadow:0 0 0 4px #fff,0 12px 30px rgba(0,0,0,.55)}
.b .hero,.b .card,.b .po{box-shadow:inset 0 0 0 1.5px rgba(255,255,255,.28)}
.b .card.f{box-shadow:0 0 0 4px #fff,0 12px 30px rgba(0,0,0,.55)}
.c .hero,.c .card,.c .po{box-shadow:inset 0 0 0 2px rgba(255,255,255,.85)}
.c .card.f{box-shadow:0 0 0 4px #fff,0 12px 30px rgba(0,0,0,.55)}
`;

(async () => {
  const browser = await chromium.launch();
  const p = await browser.newPage({ viewport: { width: 1920, height: 1080 } });
  for (const [file, cls] of [['01-a-no-edge.png', 'a'], ['02-b-faint-edge.png', 'b'], ['03-c-white-outline.png', 'c']]) {
    await p.setContent(`<style>${css}</style><body class="${cls}">${page(2)}</body>`);
    await p.screenshot({ path: path.join(out, file) });
  }
  await browser.close();
})();
