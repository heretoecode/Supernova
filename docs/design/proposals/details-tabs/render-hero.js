// Proposal (0.157, owner 5 Nov 2026): the Details hero card carries the description (up to five lines, never repeated
// in the Details tab); a longer one is read in full from More › Full description in the shared side panel.
// Run: FONT_DIR=<folder with roboto-latin-{400,500,700}-normal.woff2> node render-hero.js
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

const shortText = 'With the Yellowstone Ranch behind him, Kayce Dutton joins an elite unit of U.S. Marshals, combining his '
  + 'skills as a cowboy and Navy SEAL to bring range justice to Montana, where he and his teammates must balance '
  + 'family, duty and the high psychological cost that comes with serving as the last line of defence in the '
  + 'region’s war on violence.';

const longText = shortText + ' As old enemies close in on the reservation and a new threat rises from the '
  + 'mountains, Kayce must decide how far he is willing to go to protect his family, his badge and the land his '
  + 'father fought for, while the team learns that some fugitives are running from something worse than the law.';

const nav = `<div class="nav"><b>SUPERNOVA</b><span>Home</span><span>Movies</span><span>TV Shows</span>
  <span>Documentaries</span><i></i><span>Network &amp; Files</span><span class="clock">10:12</span></div>`;

function hero(text, options) {
  const o = options || {};
  const buttons = `<div class="acts"><span class="btn${o.focus === 'play' ? ' f' : ''}">▶&nbsp; Play</span>
    <span class="btn${o.focus === 'more' ? ' f' : ''}">···&nbsp; More</span></div>`;
  return `<div class="hero"><div class="art"></div><div class="patch"></div><div class="copy">
    <div class="logo">MARSHALS</div>
    <div class="meta">2026 · 1 Season · 13 Episodes · Drama, Crime, Action &amp; Adventure</div>
    <p class="tag">A new era of Yellowstone.</p>
    <p class="desc">${text}</p>${buttons}</div></div>
    <div class="tabs"><span class="open">Seasons &amp; Episodes</span><span>Details</span><span>Extras</span>
    <span>More Like This</span></div>`;
}

const moreList = `<div class="side"><div class="item f">Full description</div><div class="item">Mark as watched</div>
  <div class="item">Add to Watch Next</div><div class="item">Fix match</div><div class="item">Remove from library</div></div>`;

const fullText = `<div class="side wide"><h3>Description</h3><p class="tag">A new era of Yellowstone.</p>
  <p class="full">${longText}</p><div class="hint">Back to close</div></div>`;

const css = `${font(400)}${font(500)}${font(700)}
*{box-sizing:border-box;margin:0}
body{width:1920px;height:1080px;overflow:hidden;font-family:R;color:#fff;position:relative;
  background:linear-gradient(180deg,#05091a 0%,#0b1b5e 60%,#1636b8 100%)}
.nav{position:absolute;left:72px;right:72px;top:34px;height:60px;display:flex;align-items:center;gap:34px;font-size:27px;
  color:#e6ebf2}.nav b{font-weight:500;letter-spacing:1px;margin-right:30px}.nav i{flex:1}
.hero{position:absolute;left:72px;right:72px;top:118px;height:780px;border-radius:26px;overflow:hidden;
  box-shadow:0 0 0 2px rgba(255,255,255,.55),0 18px 50px rgba(0,0,0,.45)}
.art{position:absolute;inset:0;background:
  radial-gradient(ellipse 14% 42% at 60% 66%,rgba(30,34,44,.95) 0%,rgba(30,34,44,.85) 55%,rgba(30,34,44,0) 100%),
  radial-gradient(ellipse 6% 9% at 60% 30%,rgba(30,34,44,.95) 0%,rgba(30,34,44,0) 100%),
  linear-gradient(180deg,#5d8fc6 0%,#9ab7d8 38%,#e2b178 62%,#7a4a3a 78%,#2b2a33 100%)}
.patch{position:absolute;inset:0;
  background:radial-gradient(ellipse 52% 72% at 18% 50%,rgba(0,0,0,.62) 0%,rgba(0,0,0,.38) 55%,rgba(0,0,0,0) 100%)}
.copy{position:absolute;left:56px;top:60px;width:820px}
.logo{font-size:84px;font-weight:700;letter-spacing:10px;color:#f1ead8;text-shadow:0 3px 10px rgba(0,0,0,.5)}
.meta{font-size:25px;font-weight:500;margin-top:20px;text-shadow:0 2px 6px rgba(0,0,0,.8)}
.tag{font-size:24px;font-style:italic;color:#e8e2d4;margin-top:22px;text-shadow:0 2px 6px rgba(0,0,0,.8)}
.desc{font-size:24px;line-height:1.5;margin-top:10px;color:#f4f6fa;text-shadow:0 2px 6px rgba(0,0,0,.85);
  display:-webkit-box;-webkit-line-clamp:5;-webkit-box-orient:vertical;overflow:hidden}
.acts{display:flex;gap:18px;margin-top:40px}
.btn{height:64px;padding:0 30px;border-radius:20px;display:flex;align-items:center;font-size:24px;font-weight:500;
  background:rgba(22,24,28,.72);border:2px solid rgba(255,255,255,.22)}
.btn.f{background:#F4F6FB;color:#0B0D12;border-color:#F4F6FB}
.tabs{position:absolute;left:72px;right:72px;top:940px;display:flex;gap:46px;font-size:26px;font-weight:500;
  color:#b9c3d6;border-bottom:2px solid rgba(255,255,255,.28);padding-bottom:16px}
.tabs .open{color:#fff}
.dim{position:absolute;inset:0;background:rgba(0,0,0,.35)}
.side{position:absolute;right:56px;top:112px;width:520px;border-radius:26px;background:#16181C;padding:22px;
  box-shadow:0 18px 50px rgba(0,0,0,.5)}
.item{height:70px;border-radius:18px;padding:0 24px;display:flex;align-items:center;font-size:25px;color:#e6ebf2}
.item.f{background:#F4F6FB;color:#0B0D12;font-weight:500}
.side.wide{width:760px;padding:40px 44px;bottom:112px}
.side h3{font-size:20px;letter-spacing:1.5px;text-transform:uppercase;color:#aeb7c8;font-weight:500}
.side .tag{color:#c3ccdc;text-shadow:none;margin-top:16px}
.full{font-size:25px;line-height:1.6;margin-top:12px;color:#eef1f6}
.hint{position:absolute;left:44px;bottom:32px;font-size:20px;color:#8f98a8}
`;

const shots = [
  ['hero-01-description.png', nav + hero(shortText, { focus: 'play' })],
  ['hero-02-long-description.png', nav + hero(longText, { focus: 'more' })],
  ['hero-03-more-menu.png', nav + hero(longText, {}) + '<div class="dim"></div>' + moreList],
  ['hero-04-full-description.png', nav + hero(longText, {}) + '<div class="dim"></div>' + fullText],
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
