// Proposal (0.157, owner 5 Nov 2026): the four Details tabs redrawn; the hero card stays as it is.
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

// Stand-in artwork: gradients only, never real posters.
const art = [
  'linear-gradient(135deg,#6b8fb5,#2b3d55 70%)', 'linear-gradient(135deg,#c9a46a,#4a3420 70%)',
  'linear-gradient(135deg,#5f7d5a,#1f3020 70%)', 'linear-gradient(135deg,#a86a5a,#3b1d17 70%)',
  'linear-gradient(135deg,#8a7ab0,#2a2140 70%)', 'linear-gradient(135deg,#d0b070,#4a3a1a 70%)',
  'linear-gradient(135deg,#4f8a9a,#173038 70%)', 'linear-gradient(135deg,#b07a8a,#3a1f28 70%)',
];

const tabsFor = ['Seasons & Episodes', 'Details', 'Extras', 'More Like This'];

function header(open) {
  const tabs = tabsFor.map((name, i) => `<span class="tab${i === open ? ' open' : ''}">${name}</span>`).join('');
  return `<div class="top"><div class="logo">MARSHALS</div><div class="tabs">${tabs}</div></div>`;
}

function episode(i, number, title, minutes, date, state, focused) {
  const tick = state === 'watched' ? '<i class="tick">✓</i>' : '';
  const bar = state === 'partial' ? '<i class="prog"><b style="width:38%"></b></i>' : '';
  const status = state === 'watched' ? 'Watched' : state === 'partial' ? '16 min left' : '';
  const line = [`${minutes} min`, date, status].filter(Boolean).join(' · ');
  return `<div class="card${focused ? ' f' : ''}"><div class="pic" style="background:${art[i % art.length]}">${tick}${bar}</div>
    <b>${number} · ${title}</b><span>${line}</span></div>`;
}

const synopsis = 'Kayce and the team track a fugitive into the Wind River Range, where an old feud with the reservation '
  + 'police threatens the whole operation.';

const episodes = header(0) + `<div class="content"><div class="seasons"><span class="pill open">Season 1</span><span class="pill">Specials</span>
  <em>13 episodes · 4 in your library</em></div>
  <div class="row">${[
    episode(0, 1, 'Piya Wiconi', 42, '1 Mar 2026', 'partial', false),
    episode(1, 2, 'Zone of Death', 44, '8 Mar 2026', '', true),
    episode(2, 3, 'Road to Nowhere', 43, '15 Mar 2026', '', false),
    episode(3, 4, 'The Gathering Storm', 47, '22 Mar 2026', '', false),
  ].join('')}</div>
  <div class="about"><b>2 · Zone of Death</b><p>${synopsis}</p></div></div>`;

function rows(pairs) {
  return pairs.map(([label, value]) => {
    const unknown = value === 'Unknown' ? ' class="unk"' : '';
    return `<div class="kv"><span>${label}</span><b${unknown}>${value}</b></div>`;
  }).join('');
}

const description = 'With the Yellowstone Ranch behind him, Kayce Dutton joins an elite unit of U.S. Marshals, '
  + 'combining his skills as a cowboy and Navy SEAL to bring range justice to Montana, where he and his teammates must '
  + 'balance family, duty and the high psychological cost that comes with serving as the last line of defence in the '
  + 'region’s war on violence.';

const details = header(1) + `<div class="content"><div class="grid">
  <div class="panel desc"><h3>Description</h3><p class="tag">A new era of Yellowstone.</p><p>${description}</p></div>
  <div class="panel"><h3>Key information</h3>${rows([
    ['Original title', 'Marshals'], ['First aired', '1 March 2026'], ['Runtime', '42–48 min per episode'],
    ['Genres', 'Drama, Crime, Action & Adventure'], ['Created by', 'Spencer Hudnut'], ['Network', 'CBS'],
    ['Country', 'United States'], ['Language', 'English'], ['Studio', '101 Studios'],
    ['Budget', 'Unknown'], ['Revenue', 'Unknown'],
  ])}</div>
  <div class="panel"><h3>Reception</h3>
    <div class="score"><i class="imdb">IMDb</i><div><b>6.1</b><span>18.5K votes</span></div></div>
    <div class="score"><i class="tmdb">TMDB</i><div><b>8.0</b><span>419 votes</span></div></div>
    <div class="score"><i class="rt">RT</i><div><b class="unk">Unknown</b><span>Rotten Tomatoes</span></div></div>
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

const details2 = header(1) + `<div class="content"><div class="grid two">
  <div class="panel"><h3>Cast <em>8 of 32</em></h3>${people(cast)}</div>
  <div class="panel"><h3>Crew <em>8 of 41</em></h3>${people(crew)}</div>
  <div class="panel"><h3>Technical information</h3>${rows([
    ['Video', '1080p · HEVC (H.265)'], ['Audio', 'Dolby Digital Plus · 5.1'], ['Subtitles', 'English, English SDH'],
    ['Container', 'MKV'], ['Files', '13 episodes · 15 GB (about 1.2 GB each)'], ['Source', 'Put.io'],
    ['Folder', 'TV / Marshals (2026)'], ['Added', '24 October 2026'],
  ])}</div></div></div>`;

function extra(i, title, minutes, source, focused) {
  return `<div class="card${focused ? ' f' : ''}"><div class="pic" style="background:${art[(i + 3) % art.length]}">
    <i class="len">${minutes}</i></div><b>${title}</b><span>${source}</span></div>`;
}

const extras = header(2) + `<div class="content"><h4>Trailers</h4><div class="row">${[
  extra(0, 'Official Trailer', '2:31', 'YouTube · CBS', true), extra(1, 'A Yellowstone Story', '1:58', 'YouTube · CBS', false),
  extra(2, 'Official Trailer (4K)', '2:31', 'YouTube', false), extra(3, 'Teaser Trailer', '0:45', 'YouTube · CBS', false),
].join('')}</div><h4>Behind the scenes</h4><div class="row">${[
  extra(4, 'Inside the Marshals', '4:12', 'YouTube · CBS', false), extra(5, 'Meet the Team', '3:05', 'YouTube · CBS', false),
].join('')}</div></div>`;

function similar(i, title, year, where, focused) {
  return `<div class="card${focused ? ' f' : ''}"><div class="pic" style="background:${art[(i + 5) % art.length]}"></div>
    <b>${title}</b><span>${year} · ${where}</span></div>`;
}

const more = header(3) + `<div class="content"><div class="row">${[
  similar(0, 'Justified', 2010, 'In your library', false), similar(1, 'Big Sky', 2020, 'In your library', false),
  similar(2, 'Justified: City Primeval', 2023, 'In your library', true), similar(3, 'Legends', 2026, 'Netflix', false),
].join('')}</div><div class="row">${[
  similar(4, 'Marvel’s The Punisher', 2017, 'In your library', false), similar(5, 'Faraway Downs', 2023, 'Disney+', false),
  similar(6, 'Reacher', 2022, 'Prime Video', false),
].join('')}</div></div>`;

const css = `${font(400)}${font(500)}${font(700)}
*{box-sizing:border-box;margin:0}
body{width:1920px;height:1080px;overflow:hidden;font-family:R;color:#fff;position:relative;
  background:linear-gradient(180deg,#05091a 0%,#0b1b5e 55%,#1636b8 100%)}
.top{position:absolute;left:72px;right:72px;top:44px;height:76px;display:flex;align-items:center;gap:56px}
.logo{font-size:40px;font-weight:700;letter-spacing:6px;color:#f1ead8}
.tabs{display:flex;gap:16px}
.tab{height:60px;padding:0 30px;border-radius:20px;display:flex;align-items:center;font-size:25px;font-weight:500;
  color:#dfe5f1;background:rgba(22,24,28,.55);border:2px solid rgba(255,255,255,.16)}
.tab.open{background:#3a3e46;color:#fff;border-color:rgba(255,255,255,.4)}
.seasons{display:flex;align-items:center;gap:14px}
.pill{height:48px;padding:0 24px;border-radius:16px;display:flex;align-items:center;font-size:22px;font-weight:500;
  background:rgba(22,24,28,.55);border:2px solid rgba(255,255,255,.14);color:#dfe5f1}
.pill.open{background:#3a3e46;color:#fff}
.seasons em{font-style:normal;font-size:21px;color:#b9c3d6;margin-left:14px}
h4{font-size:26px;font-weight:500;color:#dfe5f1;margin-bottom:-6px}

.row{display:flex;gap:28px}
.content{position:absolute;left:72px;right:72px;top:160px;display:flex;flex-direction:column;gap:30px}
.card{width:420px;flex:none;border-radius:20px;padding:12px;background:rgba(22,24,28,.62);border:2px solid rgba(255,255,255,.12)}
.card .pic{position:relative;height:236px;border-radius:14px;overflow:hidden}
.card b{display:block;font-size:24px;font-weight:500;margin:16px 6px 0;white-space:nowrap;overflow:hidden;text-overflow:ellipsis}
.card span{display:block;font-size:20px;color:#c3ccdc;margin:6px 6px 6px}
.card.f{background:#F4F6FB;border-color:#F4F6FB;transform:scale(1.04);box-shadow:0 14px 36px rgba(0,0,0,.45)}
.card.f b{color:#0B0D12}.card.f span{color:#3a3f48}
.tick{position:absolute;right:12px;top:12px;width:40px;height:40px;border-radius:50%;background:#fff;color:#0B0D12;
  font-style:normal;font-weight:700;font-size:24px;display:flex;align-items:center;justify-content:center}
.prog{position:absolute;left:12px;right:12px;bottom:12px;height:7px;border-radius:4px;background:rgba(255,255,255,.35)}
.prog b{display:block;height:100%;margin:0;border-radius:4px;background:#fff}
.len{position:absolute;right:12px;bottom:12px;font-style:normal;font-size:19px;font-weight:500;padding:4px 12px;
  border-radius:10px;background:rgba(0,0,0,.65)}
.about{padding:28px 34px;border-radius:22px;background:rgba(22,24,28,.62);
  border:2px solid rgba(255,255,255,.12)}
.about b{font-size:26px;font-weight:500}.about p{font-size:23px;line-height:1.45;color:#dfe5f1;margin-top:10px;max-width:1500px}
.grid{display:grid;grid-template-columns:1.15fr 1.1fr .8fr;gap:28px;align-items:start}
.grid.two{grid-template-columns:1fr 1fr 1.15fr}
.panel{border-radius:22px;padding:28px 32px;background:rgba(22,24,28,.62);border:2px solid rgba(255,255,255,.12)}
.panel h3{font-size:27px;font-weight:500;margin-bottom:18px}.panel h3 em{font-style:normal;font-size:19px;color:#9aa4b6;margin-left:10px}
.desc p{font-size:23px;line-height:1.5;color:#e4e9f2}.desc .tag{font-style:italic;color:#c3ccdc;margin-bottom:14px}
.kv{display:flex;justify-content:space-between;gap:20px;padding:11px 0;border-top:1px solid rgba(255,255,255,.08);font-size:21px}
.kv:first-of-type{border-top:none}
.kv span{color:#aeb7c8;flex:none}.kv b{font-weight:500;text-align:right;color:#fff}.kv b.unk,.score b.unk{color:#8f98a8;font-weight:400}
.person span{color:#fff;font-weight:500}.person b{color:#aeb7c8;font-weight:400}
.score{display:flex;align-items:center;gap:20px;padding:14px 0;border-top:1px solid rgba(255,255,255,.08)}
.score:first-of-type{border-top:none}
.score i{width:72px;height:48px;border-radius:10px;font-style:normal;font-weight:700;font-size:19px;color:#111;display:flex;
  align-items:center;justify-content:center}
.imdb{background:#f5c518}.tmdb{background:#36c4d6}.rt{background:#6b7280;color:#fff!important}
.score b{display:block;font-size:30px;font-weight:500}.score span{font-size:18px;color:#aeb7c8}
.more{font-size:21px;color:#9aa4b6}
.cap{position:absolute;left:0;right:0;bottom:0;padding:18px 72px;background:rgba(0,0,0,.72);font-size:22px;color:#e8edf6}
`;

const shots = [
  ['01-seasons-episodes.png', episodes, 'Series open on Seasons & Episodes. Readable tabs; season choice; titles under the pictures with runtime, air date and watched; the focused episode’s synopsis below.'],
  ['02-details.png', details, 'Details: description left-aligned with its tagline; aligned key information (Unknown kept, greyed); runtime from the episodes; reception. Down scrolls to the rest.'],
  ['03-details-cast-crew.png', details2, 'Further down Details: cast and crew as a tidy list, eight of each (creator first); technical information in plain words.'],
  ['04-extras.png', extras, 'Extras: one row per kind; clean titles under the pictures, length on the picture, where it comes from underneath.'],
  ['05-more-like-this.png', more, 'More Like This: every card the same size; title and year underneath, and whether it is in your library or on a service you use.'],
];

(async () => {
  const browser = await chromium.launch();
  const page = await browser.newPage({ viewport: { width: 1920, height: 1080 } });
  for (const [file, body, caption] of shots) {
    const cls = file.startsWith('04') ? 'extras2' : '';
    await page.setContent(`<style>${css}</style><body class="${cls}">${body}</body>`);
    await page.screenshot({ path: path.join(out, file) });
  }
  await browser.close();
})();
