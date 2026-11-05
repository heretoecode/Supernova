// Proposal round 3 (0.157, owner 5 Nov 2026): Details tabs using the Home row cards; Details on one screen;
// More Like This in two honest rows. The hero card stays as it is.
// Run: FONT_DIR=<folder with roboto-latin-{400,500,700}-normal.woff2> node render-v3.js
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
  'radial-gradient(ellipse at 30% 35%,#7fa3c9,#22324a 72%)', 'radial-gradient(ellipse at 60% 30%,#d2ad72,#3e2b18 72%)',
  'radial-gradient(ellipse at 40% 55%,#7fa078,#1b2a1c 72%)', 'radial-gradient(ellipse at 65% 45%,#c07b68,#341a15 72%)',
  'radial-gradient(ellipse at 35% 30%,#9d8cc4,#231c38 72%)', 'radial-gradient(ellipse at 50% 50%,#d8be7a,#3b3016 72%)',
  'radial-gradient(ellipse at 45% 40%,#5f9aaa,#132a30 72%)', 'radial-gradient(ellipse at 55% 35%,#c48c9c,#341b24 72%)',
];

const tabNames = ['Seasons & Episodes', 'Details', 'Extras', 'More Like This'];

function header(open) {
  const tabs = tabNames.map((name, i) => `<span class="tab${i === open ? ' open' : ''}">${name}</span>`).join('');
  return `<div class="top"><div class="logo">MARSHALS</div><div class="tabs">${tabs}</div></div>`;
}

// The Home row card: picture with the title and one line inside it, a progress line, an optional corner mark.
function card(i, title, sub, opts) {
  const o = opts || {};
  const tick = o.watched ? '<i class="tick">✓</i>' : '';
  const mark = o.mark ? `<i class="mark">${o.mark}</i>` : '';
  const len = o.length ? `<i class="len">${o.length}</i>` : '';
  const prog = o.progress ? `<i class="hp"><b style="width:${o.progress}%"></b></i>` : '';
  return `<div class="hc${o.focused ? ' f' : ''}" style="background:${art[i % art.length]}"><div class="hcg"></div>
    ${tick}${mark}${len}<div class="hct"><b>${title}</b><span>${sub}</span></div>${prog}</div>`;
}

function row(heading, cards, note) {
  const extra = note ? `<em>${note}</em>` : '';
  return `<div class="rowblock"><h4>${heading}${extra}</h4><div class="rail">${cards.join('')}</div></div>`;
}

const synopsis = 'The team track a fugitive into the Wind River Range, where an old feud with the reservation police '
  + 'threatens the whole operation.';

const episodes = header(0) + `<div class="content">${row('Season 1', [
  card(0, '1 · Piya Wiconi', '42 min · 16 min left', { progress: 62 }),
  card(1, '2 · Zone of Death', '44 min · 8 Mar 2026', { focused: true }),
  card(2, '3 · Road to Nowhere', '43 min · 15 Mar 2026'),
  card(3, '4 · The Gathering Storm', '47 min · 22 Mar 2026'),
  card(4, '5 · Blood Ties', '45 min · 29 Mar 2026'),
], '13 episodes')}
  <div class="syn"><b>2 · Zone of Death</b><p>${synopsis}</p></div>
  ${row('Specials', [
    card(5, 'Inside Marshals', '22 min · 1 Mar 2026'), card(6, 'Season 1 Recap', '12 min · 3 May 2026'),
  ], '2 episodes')}</div>`;

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

function people(list) {
  return list.map(([name, role]) => `<div class="kv person"><span>${name}</span><b>${role}</b></div>`).join('');
}

const description = 'With the Yellowstone Ranch behind him, Kayce Dutton joins an elite unit of U.S. Marshals, '
  + 'combining his skills as a cowboy and Navy SEAL to bring range justice to Montana, where he and his teammates must '
  + 'balance family, duty and the high psychological cost that comes with serving as the last line of defence in the '
  + 'region’s war on violence.';

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

const details = header(1) + `<div class="content tight"><div class="cols">
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
  </div></div><div class="hr"></div><div class="cols">
  <div class="col"><h3>Cast <em>8 of 32</em></h3>${people(cast)}</div>
  <div class="col"><h3>Crew <em>8 of 41</em></h3>${people(crew)}</div>
  <div class="col"><h3>Technical information</h3>${rows([
    ['Video', '1080p · HEVC (H.265)'], ['Audio', 'Dolby Digital Plus · 5.1'], ['Subtitles', 'English, English SDH'],
    ['Container', 'MKV'], ['Files', '13 episodes · 15 GB (about 1.2 GB each)'], ['Source', 'Put.io'],
    ['Folder', 'TV / Marshals (2026)'], ['Added', '24 October 2026'],
  ])}</div></div></div>`;

const extras = header(2) + `<div class="content">${row('Trailers', [
  card(3, 'Official Trailer', 'YouTube · CBS', { focused: true, length: '2:31' }),
  card(4, 'A Yellowstone Story', 'YouTube · CBS', { length: '1:58' }),
  card(5, 'Official Trailer (4K)', 'YouTube', { length: '2:31' }),
  card(6, 'Teaser Trailer', 'YouTube · CBS', { length: '0:45' }),
])}${row('Behind the scenes', [
  card(7, 'Inside the Marshals', 'YouTube · CBS', { length: '4:12' }),
  card(0, 'Meet the Team', 'YouTube · CBS', { length: '3:05' }),
])}</div>`;

const more = header(3) + `<div class="content">${row('In your library', [
  card(5, 'Justified', '2010 · Series', { watched: true }),
  card(6, 'Justified: City Primeval', '2023 · Series', { focused: true }),
  card(7, 'Big Sky', '2020 · Series'),
], '3 titles')}${row('On your services', [
  card(0, 'Legends', '2026 · Series', { mark: 'N' }),
  card(3, 'Reacher', '2022 · Series', { mark: 'prime' }),
], '2 titles')}</div>`;

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
.content{position:absolute;left:72px;right:0;top:206px;display:flex;flex-direction:column;gap:26px}
.content.tight{right:72px;top:200px;gap:18px}
.rowblock h4{font-size:27px;font-weight:500;margin-bottom:16px}
.rowblock h4 em{font-style:normal;font-size:20px;color:#9aa4b6;font-weight:400;margin-left:14px}
.rail{display:flex;gap:24px;align-items:center;height:250px}
.hc{position:relative;flex:none;width:400px;height:225px;border-radius:16px;overflow:hidden;
  box-shadow:0 6px 18px rgba(0,0,0,.45)}
.hc.f{width:440px;height:248px;box-shadow:0 0 0 4px #fff,0 12px 30px rgba(0,0,0,.55)}
.hcg{position:absolute;inset:0;background:linear-gradient(0deg,rgba(0,0,0,.8) 0%,rgba(0,0,0,0) 62%)}
.hct{position:absolute;left:18px;right:18px;bottom:22px}
.hct b{display:block;font-size:25px;font-weight:700;white-space:nowrap;overflow:hidden;text-overflow:ellipsis}
.hct span{display:block;font-size:19px;color:#e6ebf5;margin-top:3px}
.hp{position:absolute;left:16px;right:16px;bottom:10px;height:5px;border-radius:3px;background:rgba(255,255,255,.3)}
.hp b{display:block;height:100%;border-radius:3px;background:#fff}
.tick{position:absolute;right:12px;top:12px;width:36px;height:36px;border-radius:50%;background:#fff;color:#0B0D12;
  font-style:normal;font-weight:700;font-size:21px;display:flex;align-items:center;justify-content:center}
.len,.mark{position:absolute;right:12px;top:12px;font-style:normal;font-size:18px;font-weight:500;padding:4px 12px;
  border-radius:10px;background:rgba(0,0,0,.6)}
.mark{font-weight:700;letter-spacing:.5px}
.syn{margin:-6px 72px 0 0;max-width:1500px}.syn b{font-size:23px;font-weight:500}
.syn p{font-size:22px;line-height:1.45;color:#dfe5f1;margin-top:6px}
.cols{display:flex}
.col{flex:1;padding:0 40px;border-left:1px solid rgba(255,255,255,.16)}
.col:first-child{padding-left:0;border-left:none}.col:last-child{padding-right:0}.col.narrow{flex:.78}
.col h3{font-size:19px;font-weight:500;letter-spacing:1.5px;text-transform:uppercase;color:#aeb7c8;margin-bottom:8px}
.col h3 em{font-style:normal;text-transform:none;letter-spacing:0;color:#8f98a8;margin-left:10px}
.body{font-size:21px;line-height:1.45;color:#e4e9f2}.tag{font-size:21px;font-style:italic;color:#c3ccdc;margin-bottom:10px}
.kv{display:flex;justify-content:space-between;gap:18px;height:33px;align-items:center;font-size:20px}
.kv span{color:#aeb7c8;flex:none}.kv b{font-weight:500;text-align:right;white-space:nowrap;overflow:hidden;text-overflow:ellipsis}
.kv b.unk,.score b.unk{color:#8f98a8;font-weight:400}
.person span{color:#fff;font-weight:500}.person b{color:#aeb7c8;font-weight:400}
.score{display:flex;align-items:center;gap:18px;height:84px}
.score i{width:68px;height:44px;border-radius:10px;font-style:normal;font-weight:700;font-size:18px;color:#111;display:flex;
  align-items:center;justify-content:center;flex:none}
.imdb{background:#f5c518}.tmdb{background:#36c4d6}.rt{background:#fa320a;color:#fff!important}.mc{background:#ffcc34}
.score b{display:block;font-size:27px;font-weight:500}.score span{font-size:17px;color:#aeb7c8}
.hr{height:1px;background:rgba(255,255,255,.16)}
`;

const shots = [
  ['v3-01-seasons-episodes.png', episodes],
  ['v3-02-details.png', details],
  ['v3-03-extras.png', extras],
  ['v3-04-more-like-this.png', more],
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
