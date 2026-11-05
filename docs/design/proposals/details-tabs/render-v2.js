// Proposal round 2 (0.157, owner 5 Nov 2026): Details tabs with no boxed panels, list-style episodes and a fixed
// Reception set. The hero card stays as it is.
// Run: FONT_DIR=<folder with roboto-latin-{400,500,700}-normal.woff2> node render-v2.js
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

// Stand-in artwork: gradients only, never real posters.
const art = [
  'linear-gradient(135deg,#6b8fb5,#2b3d55 70%)', 'linear-gradient(135deg,#c9a46a,#4a3420 70%)',
  'linear-gradient(135deg,#5f7d5a,#1f3020 70%)', 'linear-gradient(135deg,#a86a5a,#3b1d17 70%)',
  'linear-gradient(135deg,#8a7ab0,#2a2140 70%)', 'linear-gradient(135deg,#d0b070,#4a3a1a 70%)',
  'linear-gradient(135deg,#4f8a9a,#173038 70%)', 'linear-gradient(135deg,#b07a8a,#3a1f28 70%)',
];

const tabNames = ['Seasons & Episodes', 'Details', 'Extras', 'More Like This'];

// Tabs are plain words on one thin line; the open tab's stretch of the line is Supernova blue.
// A focused tab would be the usual white box; here focus is in the content, so no tab is white.
function header(open) {
  const tabs = tabNames.map((name, i) => `<span class="tab${i === open ? ' open' : ''}">${name}</span>`).join('');
  return `<div class="top"><div class="logo">MARSHALS</div><div class="tabs">${tabs}</div></div>`;
}

const seasonList = [
  ['Season 1', '13 episodes', true], ['Specials', '2 episodes', false],
];

function episodeRow(i, number, title, minutes, date, state, focused, text) {
  const tick = state === 'watched' ? '<i class="tick">✓</i>' : '';
  const bar = state === 'partial' ? '<i class="prog"><b style="width:38%"></b></i>' : '';
  const status = state === 'watched' ? 'Watched' : state === 'partial' ? '16 min left' : '';
  const line = [date, status].filter(Boolean).join(' · ');
  return `<div class="ep${focused ? ' f' : ''}"><div class="still" style="background:${art[i % art.length]}">${tick}${bar}</div>
    <div class="ept"><div class="eph"><b>${number} · ${title}</b><em>${minutes} min</em></div>
    <p>${text}</p><span>${line}</span></div></div>`;
}

const episodes = header(0) + `<div class="content eplayout">
  <div class="seasons">${seasonList.map(([name, count, open]) => `<div class="season${open ? ' open' : ''}"><b>${name}</b><span>${count}</span></div>`).join('')}</div>
  <div class="eps">${[
    episodeRow(0, 1, 'Piya Wiconi', 42, '1 March 2026', 'partial', false,
      'Kayce joins the U.S. Marshals and is thrown straight into a manhunt on the reservation.'),
    episodeRow(1, 2, 'Zone of Death', 44, '8 March 2026', '', true,
      'The team track a fugitive into the Wind River Range, where an old feud with the reservation police threatens the whole operation.'),
    episodeRow(2, 3, 'Road to Nowhere', 43, '15 March 2026', '', false,
      'A routine prisoner transfer goes wrong on a remote mountain pass.'),
  ].join('')}</div></div>`;

function rows(pairs) {
  return pairs.map(([label, value]) => {
    const unknown = value === 'Unknown' ? ' class="unk"' : '';
    return `<div class="kv"><span>${label}</span><b${unknown}>${value}</b></div>`;
  }).join('');
}

function score(mark, cls, value, note) {
  const unknown = value === 'Unknown' ? ' class="unk"' : '';
  return `<div class="score"><i class="${cls}">${mark}</i><div><b${unknown}>${value}</b><span>${note}</span></div></div>`;
}

const description = 'With the Yellowstone Ranch behind him, Kayce Dutton joins an elite unit of U.S. Marshals, '
  + 'combining his skills as a cowboy and Navy SEAL to bring range justice to Montana, where he and his teammates must '
  + 'balance family, duty and the high psychological cost that comes with serving as the last line of defence in the '
  + 'region’s war on violence.';

const details = header(1) + `<div class="content"><div class="cols">
  <div class="col"><h3>Description</h3><p class="tag">A new era of Yellowstone.</p><p class="body">${description}</p></div>
  <div class="col"><h3>Key information</h3>${rows([
    ['Original title', 'Marshals'], ['First aired', '1 March 2026'], ['Runtime', '42–48 min per episode'],
    ['Genres', 'Drama, Crime, Action & Adventure'], ['Created by', 'Spencer Hudnut'], ['Network', 'CBS'],
    ['Country', 'United States'], ['Language', 'English'], ['Studio', '101 Studios'],
    ['Budget', 'Unknown'], ['Revenue', 'Unknown'],
  ])}</div>
  <div class="col narrow"><h3>Reception</h3>
    ${score('IMDb', 'imdb', '6.1/10', '18.5K votes')}
    ${score('RT', 'rt', 'Unknown', 'Rotten Tomatoes')}
    ${score('MC', 'mc', 'Unknown', 'Metacritic')}
    ${score('TMDB', 'tmdb', '8.0/10', '419 votes')}
  </div></div><div class="more">↓ Cast & crew · Technical information</div></div>`;

function people(list) {
  return list.map(([name, role]) => `<div class="kv person"><span>${name}</span><b>${role}</b></div>`).join('');
}

const cast = [
  ['Luke Grimes', 'Kayce Dutton'], ['Logan Marshall-Green', 'Pete Calvin'], ['Arielle Kebbel', 'Belle Skinner'],
  ['Ash Santos', 'Andrea Cruz'], ['Tatanka Means', 'Miles Kittle'], ['Brecken Merrill', 'Tate Dutton'],
  ['Mo Brings Plenty', 'Mo'], ['Gil Birmingham', 'Thomas Rainwater'],
];
const crew = [
  ['Spencer Hudnut', 'Creator · Showrunner'], ['Taylor Sheridan', 'Executive producer'],
  ['David C. Glasser', 'Executive producer'], ['John Linson', 'Executive producer'],
  ['Art Linson', 'Executive producer'], ['Ron Burkle', 'Executive producer'],
  ['Luke Grimes', 'Executive producer'], ['David Hutkin', 'Executive producer'],
];

const details2 = header(1) + `<div class="content"><div class="cols">
  <div class="col"><h3>Cast <em>8 of 32</em></h3>${people(cast)}</div>
  <div class="col"><h3>Crew <em>8 of 41</em></h3>${people(crew)}</div>
  <div class="col"><h3>Technical information</h3>${rows([
    ['Video', '1080p · HEVC (H.265)'], ['Audio', 'Dolby Digital Plus · 5.1'], ['Subtitles', 'English, English SDH'],
    ['Container', 'MKV'], ['Files', '13 episodes · 15 GB (about 1.2 GB each)'], ['Source', 'Put.io'],
    ['Folder', 'TV / Marshals (2026)'], ['Added', '24 October 2026'],
  ])}</div></div></div>`;

function tile(i, title, sub, focused, badge) {
  const len = badge ? `<i class="len">${badge}</i>` : '';
  return `<div class="tile${focused ? ' f' : ''}"><div class="pic" style="background:${art[i % art.length]}">${len}</div>
    <b>${title}</b><span>${sub}</span></div>`;
}

const extras = header(2) + `<div class="content"><h4>Trailers</h4><div class="row">${[
  tile(3, 'Official Trailer', 'YouTube · CBS', true, '2:31'), tile(4, 'A Yellowstone Story', 'YouTube · CBS', false, '1:58'),
  tile(5, 'Official Trailer (4K)', 'YouTube', false, '2:31'), tile(6, 'Teaser Trailer', 'YouTube · CBS', false, '0:45'),
].join('')}</div><h4>Behind the scenes</h4><div class="row">${[
  tile(7, 'Inside the Marshals', 'YouTube · CBS', false, '4:12'), tile(0, 'Meet the Team', 'YouTube · CBS', false, '3:05'),
].join('')}</div></div>`;

const more = header(3) + `<div class="content"><div class="row">${[
  tile(5, 'Justified', '2010 · In your library', false), tile(6, 'Big Sky', '2020 · In your library', false),
  tile(7, 'Justified: City Primeval', '2023 · In your library', true), tile(0, 'Legends', '2026 · Netflix', false),
].join('')}</div><div class="row">${[
  tile(1, 'Marvel’s The Punisher', '2017 · In your library', false), tile(2, 'Faraway Downs', '2023 · Disney+', false),
  tile(3, 'Reacher', '2022 · Prime Video', false),
].join('')}</div></div>`;

const css = `${font(400)}${font(500)}${font(700)}
*{box-sizing:border-box;margin:0}
body{width:1920px;height:1080px;overflow:hidden;font-family:R;color:#fff;position:relative;
  background:linear-gradient(180deg,#05091a 0%,#0b1b5e 55%,#1636b8 100%)}
.top{position:absolute;left:72px;right:72px;top:40px}
.logo{font-size:40px;font-weight:700;letter-spacing:6px;color:#f1ead8;height:64px}
.tabs{display:flex;gap:46px;border-bottom:2px solid rgba(255,255,255,.28);margin-top:18px}
.tab{position:relative;padding:0 0 16px;font-size:26px;font-weight:500;color:#b9c3d6}
.tab.open{color:#fff}
.tab.open:after{content:'';position:absolute;left:0;right:0;bottom:-2px;height:2px;background:#4d8dff;
  box-shadow:0 0 10px rgba(77,141,255,.8)}
.content{position:absolute;left:72px;right:72px;top:220px;display:flex;flex-direction:column;gap:30px}
.eplayout{flex-direction:row;gap:56px}
.seasons{width:220px;flex:none;display:flex;flex-direction:column;gap:6px}
.season{padding:14px 18px;border-radius:14px;color:#b9c3d6}.season b{display:block;font-size:24px;font-weight:500}
.season span{display:block;font-size:18px;margin-top:2px}.season.open{color:#fff;background:rgba(255,255,255,.10)}
.eps{flex:1;display:flex;flex-direction:column;gap:30px}
.ep{display:flex;gap:34px;align-items:flex-start}
.still{position:relative;width:360px;height:202px;flex:none;border-radius:16px;overflow:hidden}
.ep.f .still{box-shadow:0 0 0 4px #fff,0 12px 30px rgba(0,0,0,.5)}
.ept{flex:1;padding-top:8px}.eph{display:flex;justify-content:space-between;align-items:baseline}
.eph b{font-size:27px;font-weight:500}.eph em{font-style:normal;font-size:22px;color:#c3ccdc}
.ept p{font-size:22px;line-height:1.45;color:#dfe5f1;margin-top:10px;max-width:1040px}
.ept span{display:block;font-size:19px;color:#9aa4b6;margin-top:10px}
.ep:not(.f) .eph b{color:#e6ebf2}
.tick{position:absolute;right:12px;top:12px;width:40px;height:40px;border-radius:50%;background:#fff;color:#0B0D12;
  font-style:normal;font-weight:700;font-size:24px;display:flex;align-items:center;justify-content:center}
.prog{position:absolute;left:12px;right:12px;bottom:12px;height:7px;border-radius:4px;background:rgba(255,255,255,.35)}
.prog b{display:block;height:100%;border-radius:4px;background:#fff}
.cols{display:flex;gap:0}
.col{flex:1;padding:0 44px;border-left:1px solid rgba(255,255,255,.16)}
.col:first-child{padding-left:0;border-left:none}.col:last-child{padding-right:0}.col.narrow{flex:.8}
.col h3{font-size:22px;font-weight:500;letter-spacing:1.5px;text-transform:uppercase;color:#aeb7c8;margin-bottom:18px}
.col h3 em{font-style:normal;text-transform:none;letter-spacing:0;color:#8f98a8;margin-left:10px}
.body{font-size:23px;line-height:1.5;color:#e4e9f2}.tag{font-size:23px;font-style:italic;color:#c3ccdc;margin-bottom:14px}
.kv{display:flex;justify-content:space-between;gap:20px;padding:10px 0;font-size:21px}
.kv span{color:#aeb7c8;flex:none}.kv b{font-weight:500;text-align:right}.kv b.unk,.score b.unk{color:#8f98a8;font-weight:400}
.person span{color:#fff;font-weight:500}.person b{color:#aeb7c8;font-weight:400}
.score{display:flex;align-items:center;gap:20px;padding:12px 0}
.score i{width:72px;height:46px;border-radius:10px;font-style:normal;font-weight:700;font-size:19px;color:#111;display:flex;
  align-items:center;justify-content:center;flex:none}
.imdb{background:#f5c518}.tmdb{background:#36c4d6}.rt{background:#fa320a;color:#fff!important}.mc{background:#ffcc34}
.score b{display:block;font-size:28px;font-weight:500}.score span{font-size:18px;color:#aeb7c8}
.more{font-size:21px;color:#9aa4b6;margin-top:20px}
h4{font-size:24px;font-weight:500;letter-spacing:1.5px;text-transform:uppercase;color:#aeb7c8;margin-bottom:-8px}
.row{display:flex;gap:36px}
.tile{width:414px;flex:none}
.tile .pic{position:relative;height:233px;border-radius:16px;overflow:hidden}
.tile.f .pic{box-shadow:0 0 0 4px #fff,0 12px 30px rgba(0,0,0,.5)}
.tile b{display:block;font-size:24px;font-weight:500;margin-top:14px;white-space:nowrap;overflow:hidden;text-overflow:ellipsis}
.tile span{display:block;font-size:20px;color:#c3ccdc;margin-top:4px}
.len{position:absolute;right:12px;bottom:12px;font-style:normal;font-size:19px;font-weight:500;padding:4px 12px;
  border-radius:10px;background:rgba(0,0,0,.65)}
`;

const shots = [
  ['v2-01-seasons-episodes.png', episodes],
  ['v2-02-details.png', details],
  ['v2-03-details-cast-crew.png', details2],
  ['v2-04-extras.png', extras],
  ['v2-05-more-like-this.png', more],
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
