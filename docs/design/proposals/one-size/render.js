// Proposal (0.157, owner 5 Nov 2026): one row card size for the whole app (16:9, 400x225 px at 1080p, focused 440x248)
// shown on Home, Details Extras and the player strip; one hero size for Home and Details with the carousel cue as
// dots (A) or slim side peeks (B).
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
  'linear-gradient(160deg,#9d8cc4,#231c38)', 'linear-gradient(160deg,#d8be7a,#3b3016)'];

function card(i, title, sub, focused, extra) {
  return `<div class="card${focused ? ' f' : ''}" style="background:${art[i % art.length]}"><div class="g"></div>${extra || ''}
    <div class="t"><b>${title}</b><span>${sub}</span></div></div>`;
}

function rail(cards) {
  return `<div class="rail">${cards.join('')}</div>`;
}

const home = `<div class="lbl">Home · Continue Watching</div>${rail([
  card(0, 'Nocturne', 'Resume · S1 E1'), card(1, 'Marshals', 'Resume · S1 E1', true), card(2, 'MobLand', 'Resume · S2 E7'),
  card(3, 'Forgotten Island', '2026'), card(4, 'Kingdom', 'Up Next · S1 E3')])}`;
const len = l => `<i class="len">${l}</i>`;
const extras = `<div class="lbl">Details · Extras › Trailers</div>${rail([
  card(5, 'Official Trailer', 'YouTube', false, len('2:31')), card(0, 'Teaser Trailer', 'YouTube', false, len('0:45')),
  card(1, 'Final Trailer', 'YouTube', true, len('2:12')), card(2, 'In Cinemas Now', 'YouTube', false, len('0:30')),
  card(3, 'Featurette', 'YouTube', false, len('3:05'))])}`;
const player = `<div class="lbl">Player · Chapters</div>${rail([
  card(4, 'Recap', 'From 0:00'), card(5, 'Intro', 'From 0:52'), card(0, 'Chapter 1', 'Playing · from 1:40', true),
  card(1, 'Chapter 2', 'From 11:40'), card(2, 'Chapter 3', 'From 22:05')])}`;

function hero(kind, page) {
  const tabs = page === 'details'
    ? `<div class="tabs"><span class="open">Seasons &amp; Episodes</span><span>Details</span><span>Extras</span>
       <span>More Like This</span></div>` : '';
  const dots = kind === 'a' && page === 'home'
    ? `<div class="dots">${[0, 1, 2, 3, 4, 5].map(i => `<b class="${i === 1 ? 'on' : ''}"></b>`).join('')}</div>` : '';
  const peeks = kind === 'b' && page === 'home'
    ? `<div class="peek l" style="background:${art[3]}"></div><div class="peek r" style="background:${art[2]}"></div>` : '';
  const button = page === 'home' ? '<span class="btn f">ⓘ&nbsp; More Info</span>'
    : '<span class="btn f">▶&nbsp; Resume S1 E1 · 16 min left</span><span class="btn">···&nbsp; More</span>';
  return `<div class="nav"><b>SUPERNOVA</b><span>Home</span><span>Movies</span><span>TV Shows</span>
    <span>Documentaries</span><i></i><span>Network &amp; Files</span><span>14:48</span></div>${peeks}
    <div class="hero"><div class="hp"></div><div class="hc"><div class="logo">MARSHALS</div>
    <div class="meta">2026 · 1 Season · Drama, Crime</div><div class="acts">${button}</div></div>${dots}</div>${tabs}`;
}

const css = `${font(400)}${font(500)}${font(700)}
*{box-sizing:border-box;margin:0}
body{width:1920px;height:1080px;overflow:hidden;font-family:R;color:#fff;position:relative;
  background:#09111a url(${SPACE}) top center/1920px 1080px no-repeat}
.rows{position:absolute;left:72px;top:40px;right:0}
.lbl{font-size:24px;font-weight:500;margin:22px 0 10px;color:#e6ebf2}
.rail{display:flex;gap:24px;align-items:center;height:262px}
.card{position:relative;flex:none;width:400px;height:225px;border-radius:16px;overflow:hidden;
  box-shadow:inset 0 0 0 1.5px rgba(255,255,255,.28)}
.card.f{width:440px;height:248px;box-shadow:0 0 0 4px #fff,0 12px 30px rgba(0,0,0,.55)}
.g{position:absolute;inset:0;background:linear-gradient(0deg,rgba(0,0,0,.8),rgba(0,0,0,0) 62%)}
.t{position:absolute;left:18px;bottom:20px}.t b{display:block;font-size:24px;font-weight:700}.t span{font-size:18px;color:#e6ebf5}
.len{position:absolute;right:12px;top:12px;font-style:normal;font-size:17px;font-weight:500;padding:4px 12px;border-radius:10px;
  background:rgba(0,0,0,.6)}
.nav{position:absolute;left:72px;right:72px;top:34px;height:60px;display:flex;align-items:center;gap:34px;font-size:27px;color:#c3ccdc}
.nav b{font-weight:500;letter-spacing:1px;margin-right:30px;color:#fff}.nav i{flex:1}
.hero{position:absolute;left:72px;right:72px;top:118px;height:700px;border-radius:24px;overflow:hidden;
  background:linear-gradient(180deg,#5d8fc6 0%,#9ab7d8 38%,#e2b178 62%,#7a4a3a 82%,#2b2a33 100%);
  box-shadow:inset 0 0 0 1.5px rgba(255,255,255,.28)}
.hp{position:absolute;inset:0;background:radial-gradient(ellipse 50% 75% at 18% 52%,rgba(0,0,0,.6),rgba(0,0,0,0))}
.hc{position:absolute;left:56px;top:70px}.logo{font-size:84px;font-weight:700;letter-spacing:10px;color:#f1ead8}
.meta{font-size:25px;font-weight:500;margin-top:18px}.acts{display:flex;gap:18px;margin-top:300px}
.btn{height:64px;padding:0 30px;border-radius:20px;display:flex;align-items:center;font-size:24px;font-weight:500;
  background:rgba(22,24,28,.72);border:2px solid rgba(255,255,255,.22)}.btn.f{background:#F4F6FB;color:#0B0D12;border-color:#F4F6FB}
.dots{position:absolute;left:0;right:0;bottom:26px;display:flex;justify-content:center;gap:12px}
.dots b{width:12px;height:12px;border-radius:6px;background:rgba(255,255,255,.45)}.dots b.on{width:36px;background:#fff}
.peek{position:absolute;top:150px;width:40px;height:636px;border-radius:18px}.peek.l{left:-16px}.peek.r{right:-16px}
.b .hero{left:56px;right:56px}
.tabs{position:absolute;left:72px;right:72px;top:860px;display:flex;gap:46px;font-size:26px;font-weight:500;color:#b9c3d6;
  border-bottom:2px solid rgba(255,255,255,.28);padding-bottom:16px}.tabs .open{color:#fff}
`;

const shots = [
  ['01-one-row-card-everywhere.png', `<div class="rows">${home}${extras}${player}</div>`, ''],
  ['02-hero-home-a-dots.png', hero('a', 'home'), ''],
  ['03-hero-home-b-peeks.png', hero('b', 'home'), 'b'],
  ['04-hero-details-same-size.png', hero('a', 'details'), ''],
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
