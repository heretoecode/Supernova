// Proposal (0.157, owner 5 Nov 2026): three ways to show resume progress on the Details hero card.
// Run: FONT_DIR=<folder with roboto-latin-{400,500,700}-normal.woff2> node render-resume.js
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

const description = 'With the Yellowstone Ranch behind him, Kayce Dutton joins an elite unit of U.S. Marshals, combining '
  + 'his skills as a cowboy and Navy SEAL to bring range justice to Montana, where he and his teammates must balance '
  + 'family, duty and the high psychological cost that comes with serving as the last line of defence.';

function panel(letter, buttons, above) {
  return `<div class="panel"><div class="art"></div><div class="patch"></div><div class="copy">
    <div class="logo">MARSHALS</div><div class="meta">2026 · 1 Season · 13 Episodes · Drama, Crime</div>
    <p class="desc">${description}</p>${above || ''}<div class="acts">${buttons}</div></div></div>`;
}

const more = '<span class="btn">···&nbsp; More</span>';
const a = panel('A', `<span class="btn f">▶&nbsp; Resume S1 E1</span>${more}`,
  '<div class="prog"><i><b style="width:62%"></b></i><span>16 min left</span></div>');
const b = panel('B', `<span class="btn f inbar">▶&nbsp; Resume S1 E1<i><b style="width:62%"></b></i></span>${more}`);
const c = panel('C', `<span class="btn f">▶&nbsp; Resume S1 E1 · 16 min left</span>${more}`);

const css = `${font(400)}${font(500)}${font(700)}
*{box-sizing:border-box;margin:0}
body{width:1920px;height:640px;overflow:hidden;font-family:R;color:#fff;background:#05070c;display:flex;gap:24px;padding:24px}
.panel{position:relative;flex:1;border-radius:22px;overflow:hidden;box-shadow:0 0 0 2px rgba(255,255,255,.4)}
.art{position:absolute;inset:0;background:linear-gradient(180deg,#5d8fc6 0%,#9ab7d8 38%,#e2b178 62%,#7a4a3a 78%,#2b2a33 100%)}
.patch{position:absolute;inset:0;background:radial-gradient(ellipse 80% 80% at 30% 55%,rgba(0,0,0,.62),rgba(0,0,0,.3) 60%,rgba(0,0,0,0))}
.copy{position:absolute;left:36px;right:36px;top:40px}
.logo{font-size:50px;font-weight:700;letter-spacing:6px;color:#f1ead8}
.meta{font-size:18px;font-weight:500;margin-top:12px}
.desc{font-size:18px;line-height:1.5;margin-top:14px;color:#f4f6fa;display:-webkit-box;-webkit-line-clamp:5;-webkit-box-orient:vertical;overflow:hidden}
.prog{display:flex;align-items:center;gap:14px;margin-top:22px}.prog i{width:220px;height:5px;border-radius:3px;background:rgba(255,255,255,.3)}
.prog b{display:block;height:100%;border-radius:3px;background:#fff}.prog span{font-size:17px}
.acts{display:flex;gap:14px;margin-top:22px}
.btn{position:relative;height:54px;padding:0 24px;border-radius:17px;display:flex;align-items:center;font-size:20px;font-weight:500;
  background:rgba(22,24,28,.72);border:2px solid rgba(255,255,255,.22);white-space:nowrap}
.btn.f{background:#F4F6FB;color:#0B0D12;border-color:#F4F6FB}
.inbar i{position:absolute;left:18px;right:18px;bottom:7px;height:4px;border-radius:2px;background:rgba(11,13,18,.18)}
.inbar i b{display:block;height:100%;border-radius:2px;background:#0B0D12}
.letter{position:absolute;right:22px;top:18px;font-size:30px;font-weight:700;color:rgba(255,255,255,.9)}
`;

(async () => {
  const browser = await chromium.launch();
  const page = await browser.newPage({ viewport: { width: 1920, height: 640 } });
  await page.setContent(`<style>${css}</style><body>${a}${b}${c}</body>`);
  await page.screenshot({ path: path.join(out, 'resume-options-a-b-c.png') });
  await browser.close();
})();
