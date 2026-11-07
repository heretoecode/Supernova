// Details background decision (owner, 7 Nov 2026): the approved layout on Space Black and on plain charcoal.
// Proposal (0.157, owner 5 Nov 2026): the remaining Details mock-ups for approval: Extras on Space Black, the polished
// hero card, a film's Details tab, a season's own page and a title with no file (Where to watch instead of Technical).
// Run: SPACE_JPG=<1080p Space Black jpg> FONT_DIR=<folder with roboto-latin-{400,500,700}-normal.woff2> node render-set2.js
const fs = require('fs');
const path = require('path');
const { chromium } = require('playwright');

const out = __dirname;
const fontDir = process.env.FONT_DIR;
const RES = path.join(__dirname, '../../../../res/drawable');
const SPACE = 'data:image/jpeg;base64,' + fs.readFileSync(process.env.SPACE_JPG).toString('base64');

function font(weight) {
  const file = path.join(fontDir, `roboto-latin-${weight}-normal.woff2`);
  const data = fs.readFileSync(file).toString('base64');
  return `@font-face{font-family:R;font-weight:${weight};src:url(data:font/woff2;base64,${data}) format('woff2')}`;
}

// Android vector → SVG, keeping fills, strokes and group rotations.
function vector(file, colour) {
  const xml = fs.readFileSync(path.join(RES, file), 'utf8');
  const vw = xml.match(/viewportWidth="([\d.]+)"/)[1];
  const vh = xml.match(/viewportHeight="([\d.]+)"/)[1];
  const parts = [];
  const re = /(<group[^>]*rotation="([-\d.]+)"[^>]*>)?\s*<path([^>]*)\/>/g;
  let m;
  while ((m = re.exec(xml)) !== null) {
    const get = name => (m[3].match(new RegExp(`android:${name}="([^"]+)"`)) || [])[1];
    const fill = colour || get('fillColor') || 'none';
    const p = `<path d="${get('pathData')}" fill="${fill === '#00000000' ? 'none' : fill}"
      stroke="${get('strokeColor') || 'none'}" stroke-width="${get('strokeWidth') || 0}" stroke-linecap="round"/>`;
    parts.push(m[2] ? `<g transform="rotate(${m[2]} 32 32)">${p}</g>` : p);
  }
  return `<svg viewBox="0 0 ${vw} ${vh}">${parts.join('')}</svg>`;
}

const art = [
  'radial-gradient(ellipse at 30% 35%,#7fa3c9,#22324a 72%)', 'radial-gradient(ellipse at 60% 30%,#d2ad72,#3e2b18 72%)',
  'radial-gradient(ellipse at 40% 55%,#7fa078,#1b2a1c 72%)', 'radial-gradient(ellipse at 65% 45%,#c07b68,#341a15 72%)',
  'radial-gradient(ellipse at 35% 30%,#9d8cc4,#231c38 72%)', 'radial-gradient(ellipse at 50% 50%,#d8be7a,#3b3016 72%)',
  'radial-gradient(ellipse at 45% 40%,#5f9aaa,#132a30 72%)', 'radial-gradient(ellipse at 55% 35%,#c48c9c,#341b24 72%)',
];

function header(logo, tabs, open) {
  const t = tabs.map((name, i) => `<span class="tab${i === open ? ' open' : ''}">${name}</span>`).join('');
  return `<div class="top"><div class="logo">${logo}</div><div class="tabs">${t}</div></div>`;
}

function card(i, title, sub, o) {
  const opt = o || {};
  const len = opt.length ? `<i class="len">${opt.length}</i>` : '';
  return `<div class="hc${opt.focused ? ' f' : ''}" style="background:${art[i % art.length]}"><div class="hcg"></div>${len}
    <div class="hct"><b>${title}</b><span>${sub}</span></div></div>`;
}

function row(title, cards) {
  return `<div class="rowblock"><h4>${title}</h4><div class="rail">${cards.join('')}</div></div>`;
}

function kv(pairs) {
  return pairs.map(([label, value]) => {
    const unk = value === 'Unknown' ? ' class="unk"' : '';
    return `<div class="kv"><span>${label}</span><b${unk}>${value}</b></div>`;
  }).join('');
}

function rating(markHtml, value, note) {
  const unk = value === 'Unknown' ? ' class="unk"' : '';
  return `<div class="rate"><i>${markHtml}</i><div><b${unk}>${value}</b><span>${note}</span></div></div>`;
}

function reception(imdb, rt, mc, tmdb, awards) {
  return `<div class="col3 recepcol"><h3>Reception</h3>
    ${rating('<em class="imdbw">IMDb</em>', imdb[0], imdb[1])}
    ${rating(vector('reception_rotten_tomatoes.xml'), rt, 'Rotten Tomatoes')}
    ${rating(vector('reception_metacritic.xml'), mc, 'Metacritic')}
    ${rating('<em class="tmdbw">TMDB</em>', tmdb[0], tmdb[1])}
    ${rating('<em class="award">🏆</em>', awards, 'Awards')}</div>`;
}

function columns(recep, key, third, thirdTitle) {
  return `<div class="three">${recep}<div class="col3"><h3>Key information</h3>${kv(key)}</div>
    <div class="col3"><h3>${thirdTitle}</h3>${third}</div></div>`;
}

// 1. Extras on Space Black
const extras = header('MARSHALS', ['Seasons & Episodes', 'Details', 'Extras', 'More Like This'], 2)
  + `<div class="content">${row('Trailers', [
    card(3, 'Official Trailer', 'YouTube · CBS', { focused: true, length: '2:31' }),
    card(4, 'A Yellowstone Story', 'YouTube · CBS', { length: '1:58' }),
    card(5, 'Official Trailer (4K)', 'YouTube', { length: '2:31' }),
    card(6, 'Teaser Trailer', 'YouTube · CBS', { length: '0:45' }),
  ])}${row('Behind the scenes', [
    card(7, 'Inside the Marshals', 'YouTube · CBS', { length: '4:12' }),
    card(0, 'Meet the Team', 'YouTube · CBS', { length: '3:05' }),
  ])}</div>`;

// 2. The polished hero card
const description = 'With the Yellowstone Ranch behind him, Kayce Dutton joins an elite unit of U.S. Marshals, combining '
  + 'his skills as a cowboy and Navy SEAL to bring range justice to Montana, where he and his teammates must balance '
  + 'family, duty and the high psychological cost that comes with serving as the last line of defence in the '
  + 'region’s war on violence.';

function hero(o) {
  const nav = `<div class="nav"><b>SUPERNOVA</b><span>Home</span><span>Movies</span><span>TV Shows</span>
    <span>Documentaries</span><i></i><span>Files</span><span>10:12</span></div>`;
  const tabs = o.tabs.map((name, i) => `<span class="${i === 0 ? 'open' : ''}">${name}</span>`).join('');
  return nav + `<div class="hero"><div class="hart" style="background:${o.art}"></div><div class="patch"></div>
    <div class="copy"><div class="hlogo">${o.logo}</div><div class="meta">${o.meta}</div>
    ${o.tag ? `<p class="htag">${o.tag}</p>` : ''}<p class="desc">${o.text}</p>
    ${o.progress ? `<div class="hprog"><i><b style="width:${o.progress}%"></b></i><span>${o.left}</span></div>` : ''}
    <div class="acts"><span class="btn f">▶&nbsp; ${o.play}</span><span class="btn">···&nbsp; More</span></div></div></div>
    <div class="htabs">${tabs}</div>`;
}

const heroArt = 'radial-gradient(ellipse 14% 42% at 60% 66%,rgba(30,34,44,.95) 0%,rgba(30,34,44,.85) 55%,rgba(30,34,44,0) 100%),'
  + 'radial-gradient(ellipse 6% 9% at 60% 30%,rgba(30,34,44,.95) 0%,rgba(30,34,44,0) 100%),'
  + 'linear-gradient(180deg,#5d8fc6 0%,#9ab7d8 38%,#e2b178 62%,#7a4a3a 78%,#2b2a33 100%)';

const heroCard = hero({
  art: heroArt, logo: 'MARSHALS', meta: '2026 · 1 Season · 13 Episodes · Drama, Crime, Action &amp; Adventure',
  tag: 'A new era of Yellowstone.', text: description, progress: 62, left: 'S1 E1 · 16 min left', play: 'Resume S1 E1',
  tabs: ['Seasons &amp; Episodes', 'Details', 'Extras', 'More Like This'],
});

// 3. A film's Details tab (a made-up film; no real artwork or data)
const film = header('THE LAST CROSSING', ['Details', 'Extras', 'More Like This'], 0) + `<div class="content page">${columns(
  reception(['7.4/10', '212K votes'], '91%', '78', ['7.6/10', '3,104 votes'], '2 BAFTA nominations'),
  [['Released', '14 March 2025'], ['Runtime', '2 h 11 min'], ['Genres', 'Drama, Thriller'], ['Directed by', 'Anna Reyes'],
   ['Written by', 'Tom Hale, Anna Reyes'], ['Studio', 'Northlight Pictures'], ['Country', 'United Kingdom'],
   ['Language', 'English'], ['Age rating', '15'], ['Collection', 'Unknown'], ['Budget', '$42 million'],
   ['Box office', '$118 million']],
  kv([['Resolution', '2160p (3840 × 2160)'], ['Video', 'HEVC (H.265) · 10-bit'], ['HDR', 'Dolby Vision'],
      ['Frame rate', '23.976 fps'], ['Bitrate', 'about 24 Mb/s'], ['Audio', 'Dolby TrueHD Atmos · 7.1'],
      ['Audio languages', 'English, French'], ['Subtitles', 'English, French'], ['Container', 'MKV'], ['Size', '38 GB'],
      ['Source', 'Shield · Films / The Last Crossing'], ['Added', '2 November 2026']]),
  'Technical information')}</div>`;

// 4. A season's own page (OK on "Season 1")
const season = hero({
  art: heroArt, logo: 'MARSHALS', meta: 'Season 1 · 2026 · 13 Episodes · CBS',
  tag: '', text: 'Kayce joins the Marshals and is pulled into a war over land, water and old loyalties, as a string of '
    + 'killings on the reservation brings the team face to face with his family’s past.',
  progress: 8, left: '1 of 13 watched', play: 'Resume S1 E1', tabs: ['Episodes', 'Details', 'Extras'],
});

// 5. A title with no file: Where to watch replaces Technical information
const svc = name => `<span class="svcl">${vector(`provider_${name}.xml`)}</span>`;
const nofile = header('LEGENDS', ['Seasons & Episodes', 'Details', 'Extras', 'More Like This'], 1)
  + `<div class="content page">${columns(
  reception(['7.1/10', '24K votes'], '88%', 'Unknown', ['7.3/10', '612 votes'], 'Unknown'),
  [['First aired', '9 January 2026'], ['Runtime', '44–52 min per episode'], ['Genres', 'Crime, Drama'],
   ['Seasons', '1 · 8 episodes'], ['Created by', 'Unknown'], ['Network', 'Netflix'], ['Studio', 'Unknown'],
   ['Country', 'United Kingdom'], ['Language', 'English'], ['Original title', 'Legends'], ['Budget', 'Unknown'],
   ['Revenue', 'Unknown']],
  `<div class="kv"><span>Watch on</span><b>${svc('netflix')}</b></div>` + kv([['Included with', 'Your Netflix plan'],
    ['Country', 'United Kingdom'], ['Quality', '4K · HDR'], ['Audio', '5.1'], ['Subtitles', 'English, English SDH'],
    ['Audio languages', 'English'], ['Leaving', 'Unknown'], ['Also on', 'None of your services'],
    ['In your library', 'No'], ['Seasons available', '1 of 1']]),
  'Where to watch')}</div>`;

const css = `${font(400)}${font(500)}${font(700)}
*{box-sizing:border-box;margin:0}
body{width:1920px;height:1080px;overflow:hidden;font-family:R;color:#fff;position:relative;
  background:#09111a url(${SPACE}) top center/1920px 1080px no-repeat}
.top{position:absolute;left:72px;right:72px;top:40px}
.logo{font-size:40px;font-weight:700;letter-spacing:6px;color:#f1ead8;height:64px}
.tabs{display:flex;gap:46px;border-bottom:2px solid rgba(255,255,255,.28);margin-top:18px}
.tab{position:relative;padding:0 0 16px;font-size:26px;font-weight:500;color:#b9c3d6}.tab.open{color:#fff}
.tab.open:after{content:'';position:absolute;left:0;right:0;bottom:-2px;height:2px;background:#4d8dff;box-shadow:0 0 10px rgba(77,141,255,.8)}
.content{position:absolute;left:72px;right:0;top:206px;display:flex;flex-direction:column;gap:26px}
.content.page{right:72px;top:250px}
.rowblock h4{display:inline-block;padding:4px 16px;font-size:27px;font-weight:500;margin-bottom:12px}
.rail{display:flex;gap:24px;align-items:center;height:250px}
.hc{position:relative;flex:none;width:400px;height:225px;border-radius:16px;overflow:hidden;box-shadow:0 6px 18px rgba(0,0,0,.45)}
.hc.f{width:440px;height:248px;box-shadow:0 0 0 4px #fff,0 12px 30px rgba(0,0,0,.55)}
.hcg{position:absolute;inset:0;background:linear-gradient(0deg,rgba(0,0,0,.8) 0%,rgba(0,0,0,0) 62%)}
.hct{position:absolute;left:18px;right:18px;bottom:22px}.hct b{display:block;font-size:25px;font-weight:700}
.hct span{display:block;font-size:19px;color:#e6ebf5;margin-top:3px}
.len{position:absolute;right:12px;top:12px;font-style:normal;font-size:18px;font-weight:500;padding:4px 12px;border-radius:10px;background:rgba(0,0,0,.6)}
.three{display:flex;height:780px}
.col3{flex:1.15;padding:0 44px;border-left:1px solid rgba(255,255,255,.32)}
.col3:first-child{padding-left:0;border-left:none;flex:.85}.col3:last-child{padding-right:0}
h3{display:inline-block;position:relative;font-size:25px;font-weight:500;letter-spacing:2px;text-transform:uppercase;margin-bottom:28px}
h3:after{content:'';position:absolute;left:0;right:2px;bottom:-10px;height:3px;border-radius:2px;background:rgba(255,255,255,.85)}
.kv{display:flex;justify-content:space-between;align-items:center;gap:18px;height:60px;font-size:23px;border-top:1px solid rgba(255,255,255,.24)}
h3 + .kv{border-top:none}
.kv span{color:#aeb7c8;flex:none}.kv b{font-weight:500;text-align:right;white-space:nowrap;overflow:hidden;text-overflow:ellipsis}
.kv b.unk,.rate b.unk{color:#8f98a8;font-weight:400}
.rate{display:flex;align-items:center;gap:22px;height:144px;border-top:1px solid rgba(255,255,255,.24)}h3 + .rate{border-top:none}
.rate i{width:110px;height:64px;flex:none;display:flex;align-items:center;justify-content:center;font-style:normal}
.rate i svg{width:64px;height:64px}.rate b{display:block;font-size:34px;font-weight:500}.rate span{display:block;font-size:21px;color:#aeb7c8}
.imdbw{font-style:normal;font-size:38px;font-weight:700;color:#F5C518}
.tmdbw{font-style:normal;font-size:34px;font-weight:700;background:linear-gradient(90deg,#90CEA1,#01B4E4);-webkit-background-clip:text;color:transparent}
.award{font-style:normal;font-size:44px;filter:grayscale(1) brightness(1.6)}
.svcl{display:inline-flex;height:40px;width:110px;justify-content:flex-end}.svcl svg{height:100%;width:100%}
.nav{position:absolute;left:72px;right:72px;top:34px;height:60px;display:flex;align-items:center;gap:34px;font-size:27px;color:#e6ebf2}
.nav b{font-weight:500;letter-spacing:1px;margin-right:30px}.nav i{flex:1}
.hero{position:absolute;left:72px;right:72px;top:118px;height:780px;border-radius:26px;overflow:hidden;
  box-shadow:0 0 0 2px rgba(255,255,255,.55),0 18px 50px rgba(0,0,0,.45)}
.hart{position:absolute;inset:0}
.patch{position:absolute;inset:0;background:radial-gradient(ellipse 52% 72% at 18% 50%,rgba(0,0,0,.62) 0%,rgba(0,0,0,.38) 55%,rgba(0,0,0,0) 100%)}
.copy{position:absolute;left:56px;top:60px;width:820px}
.hlogo{font-size:84px;font-weight:700;letter-spacing:10px;color:#f1ead8;text-shadow:0 3px 10px rgba(0,0,0,.5)}
.meta{font-size:25px;font-weight:500;margin-top:20px;text-shadow:0 2px 6px rgba(0,0,0,.8)}
.htag{font-size:24px;font-style:italic;color:#e8e2d4;margin-top:22px;text-shadow:0 2px 6px rgba(0,0,0,.8)}
.desc{font-size:24px;line-height:1.5;margin-top:10px;color:#f4f6fa;text-shadow:0 2px 6px rgba(0,0,0,.85);
  display:-webkit-box;-webkit-line-clamp:5;-webkit-box-orient:vertical;overflow:hidden}
.hprog{display:flex;align-items:center;gap:16px;margin-top:26px}.hprog i{width:300px;height:6px;border-radius:3px;background:rgba(255,255,255,.3)}
.hprog i b{display:block;height:100%;border-radius:3px;background:#fff}.hprog span{font-size:20px;color:#e6ebf2}
.acts{display:flex;gap:18px;margin-top:26px}
.btn{height:64px;padding:0 30px;border-radius:20px;display:flex;align-items:center;font-size:24px;font-weight:500;
  background:rgba(22,24,28,.72);border:2px solid rgba(255,255,255,.22);color:#fff}
.btn.f{background:#F4F6FB;color:#0B0D12;border-color:#F4F6FB}
.htabs{position:absolute;left:72px;right:72px;top:940px;display:flex;gap:46px;font-size:26px;font-weight:500;color:#b9c3d6;
  border-bottom:2px solid rgba(255,255,255,.28);padding-bottom:16px}.htabs .open{color:#fff}

body.flat{background:#16181C !important}
body.v16 .hero{box-shadow:0 0 0 2px #000,0 0 0 4px rgba(255,255,255,.55),0 18px 50px rgba(0,0,0,.45)}
.tab.open{background:#454B55;border-radius:16px;padding:8px 18px 10px;margin:-8px -18px 6px;color:#fff}
.tab.open:after{display:none}
.htabs span.open{background:#454B55;border-radius:16px;padding:8px 18px;margin:-8px -18px;color:#fff}
.htabs span.open:after{display:none}
`;

const shots = [
  ['01-top-space-black.png', heroCard, ''],
  ['03-top-charcoal.png', heroCard, 'flat'],
  ['05-v16-top.png', heroCard, 'flat v16'],
];

(async () => {
  const browser = await chromium.launch();
  const page = await browser.newPage({ viewport: { width: 1920, height: 1080 } });
  for (const [file, body, look] of shots) {
    await page.setContent(`<style>${css}</style><body class="${look}">${body}</body>`);
    await page.screenshot({ path: path.join(out, file) });
  }
  await browser.close();
})();
