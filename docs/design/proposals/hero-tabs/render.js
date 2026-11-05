// Proposal (0.157, owner 5 Nov 2026): Home and Details heroes at the same 2.4:1 size (1776x740 at 1080p), Home with
// carousel dots; Details tabs with no tab focused and with a tab focused (white underline on the open tab).
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

const art = ['linear-gradient(160deg,#c48b4f,#3a2a1a)', 'linear-gradient(160deg,#6fa7b8,#1f3640)',
  'linear-gradient(160deg,#7fb36a,#2a4020)', 'linear-gradient(160deg,#e8c7c0,#7a3a3a)',
  'linear-gradient(160deg,#9d8cc4,#231c38)'];
const heroArt = 'radial-gradient(ellipse 12% 40% at 62% 70%,rgba(30,34,44,.95),rgba(30,34,44,0)),'
  + 'radial-gradient(ellipse 5% 9% at 62% 34%,rgba(30,34,44,.95),rgba(30,34,44,0)),'
  + 'linear-gradient(180deg,#5d8fc6 0%,#9ab7d8 38%,#e2b178 62%,#7a4a3a 82%,#2b2a33 100%)';
const description = 'With the Yellowstone Ranch behind him, Kayce Dutton joins an elite unit of U.S. Marshals, combining '
  + 'his skills as a cowboy and Navy SEAL to bring range justice to Montana.';

const nav = `<div class="nav"><b>SUPERNOVA</b><span>Home</span><span>Movies</span><span>TV Shows</span>
  <span>Documentaries</span><i></i><span>Network &amp; Files</span><span>14:48</span></div>`;
const tabNames = ['Seasons &amp; Episodes', 'Details', 'Extras', 'More Like This'];

function tabs(open, focused, top) {
  return `<div class="tabs" style="top:${top}px">${tabNames.map((t, i) => {
    const cls = [i === open ? 'open' : '', i === focused ? 'f' : ''].join(' ');
    return `<span class="${cls}">${t}</span>`;
  }).join('')}</div>`;
}

function hero(page) {
  const dots = page === 'home'
    ? `<div class="dots">${[0, 1, 2, 3, 4, 5].map(i => `<b class="${i === 1 ? 'on' : ''}"></b>`).join('')}</div>` : '';
  const text = page === 'home' ? '' : `<p class="tag">A new era of Yellowstone.</p><p class="desc">${description}</p>`;
  const buttons = page === 'home' ? '<span class="btn f">ⓘ&nbsp; More Info</span>'
    : '<span class="btn f">▶&nbsp; Resume S1 E1 · 16 min left</span><span class="btn">···&nbsp; More</span>';
  return `<div class="hero" style="background:${heroArt}"><div class="hp"></div><div class="hc"><div class="logo">MARSHALS</div>
    <div class="meta">2026 · 1 Season · 13 Episodes · Drama, Crime</div>${text}<div class="acts">${buttons}</div></div>
    ${dots}</div>`;
}

function card(i, title, sub, focused) {
  return `<div class="card${focused ? ' f' : ''}" style="background:${art[i % art.length]}"><div class="g"></div>
    <div class="t"><b>${title}</b><span>${sub}</span></div></div>`;
}

const rowHome = `<div class="rowh" style="top:884px">Continue Watching</div><div class="rail" style="top:930px">${[
  card(0, 'Nocturne', 'Resume · S1 E1'), card(1, 'Marshals', 'Resume · S1 E1'), card(2, 'MobLand', 'Resume · S2 E7'),
  card(3, 'Forgotten Island', '2026'), card(4, 'Kingdom', 'Up Next · S1 E3')].join('')}</div>`;

function compact(focusTab) {
  const episodes = [card(0, '1 · Piya Wiconi', '42 min · 16 min left'), card(1, '2 · Zone of Death', '44 min · 8 Mar 2026',
    !focusTab), card(2, '3 · Road to Nowhere', '43 min · 15 Mar 2026'), card(3, '4 · The Gathering Storm', '47 min'),
    card(4, '5 · Blood Ties', '45 min')];
  return `<div class="clogo">MARSHALS</div>${tabs(0, focusTab ? 0 : -1, 120)}
    <div class="rowh" style="top:222px">Season 1 <em>›</em> <small>13 episodes</small></div>
    <div class="rail" style="top:268px">${episodes.join('')}</div>`;
}

const css = `${font(400)}${font(500)}${font(700)}
*{box-sizing:border-box;margin:0}
body{width:1920px;height:1080px;overflow:hidden;font-family:R;color:#fff;position:relative;
  background:#09111a url(${SPACE}) top center/1920px 1080px no-repeat}
.nav{position:absolute;left:72px;right:72px;top:34px;height:60px;display:flex;align-items:center;gap:34px;font-size:27px;color:#c3ccdc}
.nav b{font-weight:500;letter-spacing:1px;margin-right:30px;color:#fff}.nav i{flex:1}
.hero{position:absolute;left:72px;top:118px;width:1776px;height:740px;border-radius:24px;overflow:hidden;
  box-shadow:inset 0 0 0 1.5px rgba(255,255,255,.28)}
.hp{position:absolute;inset:0;background:radial-gradient(ellipse 50% 75% at 18% 52%,rgba(0,0,0,.62),rgba(0,0,0,0))}
.hc{position:absolute;left:56px;top:64px;width:820px}.logo{font-size:84px;font-weight:700;letter-spacing:10px;color:#f1ead8}
.meta{font-size:25px;font-weight:500;margin-top:18px}
.tag{font-size:24px;font-style:italic;color:#e8e2d4;margin-top:22px}.desc{font-size:24px;line-height:1.5;margin-top:8px}
.acts{display:flex;gap:18px;margin-top:40px}
.home .acts{margin-top:330px}
.btn{height:64px;padding:0 30px;border-radius:20px;display:flex;align-items:center;font-size:24px;font-weight:500;
  background:rgba(22,24,28,.72);border:2px solid rgba(255,255,255,.22)}.btn.f{background:#F4F6FB;color:#0B0D12;border-color:#F4F6FB}
.dots{position:absolute;left:0;right:0;bottom:26px;display:flex;justify-content:center;gap:12px}
.dots b{width:12px;height:12px;border-radius:6px;background:rgba(255,255,255,.45)}.dots b.on{width:36px;background:#fff}
.tabs{position:absolute;left:72px;right:72px;display:flex;gap:16px;align-items:center;border-bottom:2px solid rgba(255,255,255,.28);
  padding-bottom:10px}
.tabs span{position:relative;padding:12px 22px;border-radius:16px;font-size:26px;font-weight:500;color:#9aa4b6}
.tabs span.open{color:#fff}
.tabs span.open:after{content:'';position:absolute;left:22px;right:22px;bottom:-12px;height:3px;border-radius:2px;background:#fff}
.tabs span.f{background:#F4F6FB;color:#0B0D12}.tabs span.f:after{display:none}
.clogo{position:absolute;left:72px;top:40px;font-size:44px;font-weight:700;letter-spacing:6px;color:#f1ead8}
.rowh{position:absolute;left:72px;font-size:26px;font-weight:500}.rowh em{font-style:normal;color:#c3ccdc;margin:0 6px}
.rowh small{font-size:20px;color:#9aa4b6;font-weight:400}
.rail{position:absolute;left:72px;right:0;display:flex;gap:24px;align-items:center;height:262px}
.card{position:relative;flex:none;width:400px;height:225px;border-radius:16px;overflow:hidden;box-shadow:inset 0 0 0 1.5px rgba(255,255,255,.28)}
.card.f{width:440px;height:248px;box-shadow:0 0 0 4px #fff,0 12px 30px rgba(0,0,0,.55)}
.g{position:absolute;inset:0;background:linear-gradient(0deg,rgba(0,0,0,.8),rgba(0,0,0,0) 62%)}
.t{position:absolute;left:18px;bottom:20px}.t b{display:block;font-size:24px;font-weight:700}.t span{font-size:18px;color:#e6ebf5}
`;

const shots = [
  ['01-home-hero-2-4.png', nav + hero('home') + rowHome, 'home'],
  ['02-details-hero-2-4.png', nav + hero('details') + tabs(0, -1, 880), ''],
  ['03-details-tabs-no-tab-focused.png', compact(false), ''],
  ['04-details-tabs-tab-focused.png', compact(true), ''],
];

(async () => {
  const browser = await chromium.launch();
  const p = await browser.newPage({ viewport: { width: 1920, height: 1080 } });
  for (const [file, body, cls] of shots) {
    await p.setContent(`<style>${css}</style><body class="${cls}">${body}</body>`);
    await p.screenshot({ path: path.join(out, file) });
  }
  await browser.close();
})();
