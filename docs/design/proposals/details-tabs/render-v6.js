// Proposal round 6 (0.157, owner 5 Nov 2026): season headings aligned with the cards in focus; Key and Technical
// information as vertical lists; More Like This with centred service logos only and one title line.
// Run: FONT_DIR=<folder with roboto-latin-{400,500,700}-normal.woff2> node render-v6.js
const fs = require('fs');
const path = require('path');
const { chromium } = require('playwright');

const out = __dirname;
const RES = path.join(__dirname, '../../../../res/drawable');

// The app's own service logos (res/drawable/provider_*.xml), as used in the Where to Watch service list.
function logo(name, colour) {
  const xml = fs.readFileSync(path.join(RES, `provider_${name}.xml`), 'utf8');
  const vw = xml.match(/viewportWidth="([\d.]+)"/)[1];
  const vh = xml.match(/viewportHeight="([\d.]+)"/)[1];
  const paths = [...xml.matchAll(/pathData="([^"]+)"/g)].map(m => `<path d="${m[1]}" fill="${colour}"/>`).join('');
  return `<svg viewBox="0 0 ${vw} ${vh}" preserveAspectRatio="xMidYMid meet">${paths}</svg>`;
}
const BRAND = { netflix: '#E50914', primevideo: '#1A98FF', disneyplus: '#2E6BFF' };
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
  const colour = o.colour ? BRAND[o.service] : '#FFFFFF';
  const mark = o.service ? `<i class="svc ${o.pos}">${logo(o.service, colour)}</i>` : '';
  const play = o.local ? `<i class="lplay ${o.pos}"><svg viewBox="0 0 24 24"><path d="M8 5l11 7-11 7z" fill="#fff"/></svg></i>` : '';
  const len = o.length ? `<i class="len">${o.length}</i>` : '';
  const prog = o.progress ? `<i class="hp"><b style="width:${o.progress}%"></b></i>` : '';
  return `<div class="hc${o.focused ? ' f' : ''}" style="background:${art[i % art.length]}"><div class="hcg"></div>
    ${tick}${mark}${play}${len}<div class="hct">${o.oneLine ? `<b>${title}<small> · ${sub}</small></b>` : `<b>${title}</b><span>${sub}</span>`}</div>${prog}</div>`;
}

function row(heading, cards, note, link) {
  const extra = note ? `<em>${note}</em>` : '';
  const more = link ? '<i class="chev">›</i>' : '';
  return `<div class="rowblock"><h4${link === 'focus' ? ' class="hf"' : ''}>${heading}${more}${extra}</h4><div class="rail">${cards.join('')}</div></div>`;
}

const synopsis = 'The team track a fugitive into the Wind River Range, where an old feud with the reservation police '
  + 'threatens the whole operation.';

const episodes = header(0) + `<div class="content">${row('Season 1', [
  card(0, '1 · Piya Wiconi', '42 min · 16 min left', { progress: 62 }),
  card(1, '2 · Zone of Death', '44 min · 8 Mar 2026'),
  card(2, '3 · Road to Nowhere', '43 min · 15 Mar 2026'),
  card(3, '4 · The Gathering Storm', '47 min · 22 Mar 2026'),
  card(4, '5 · Blood Ties', '45 min · 29 Mar 2026'),
], '13 episodes', true)}
  ${row('Specials', [
    card(5, 'Inside Marshals', '22 min · 1 Mar 2026'), card(6, 'Season 1 Recap', '12 min · 3 May 2026'),
  ], '2 episodes', 'focus')}</div>`;

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

function fact(label, value) {
  const unknown = value === 'Unknown' ? ' unk' : '';
  return `<div class="fact"><span>${label}</span><b class="${unknown}">${value}</b></div>`;
}

function person(i, name, role) {
  const initials = name.split(' ').map(w => w[0]).slice(0, 2).join('');
  return `<div class="pp"><div class="face" style="background:${art[(i + 2) % art.length]}"><i>${initials}</i></div>
    <b>${name}</b><span>${role}</span></div>`;
}

function grid(pairs) {
  return `<div class="facts">${pairs.map(([label, value]) => fact(label, value)).join('')}</div>`;
}

function list(pairs) {
  return `<div class="vlist">${rows(pairs)}</div>`;
}

const details = header(1) + `<div class="content page">
  <div class="band"><div class="descr"><h3>About</h3><p class="tag">A new era of Yellowstone.</p><p class="body">${description}</p></div>
    <div class="recep"><h3>Reception</h3><div class="scores">
      ${score('IMDb', 'imdb', '6.1/10', '18.5K votes')}${score('RT', 'rt', 'Unknown', 'Rotten Tomatoes')}
      ${score('MC', 'mc', 'Unknown', 'Metacritic')}${score('TMDB', 'tmdb', '8.0/10', '419 votes')}</div></div></div>
  <div class="pair"><div class="sec"><h3>Key information</h3>${list([
    ['First aired', '1 March 2026'], ['Runtime', '42–48 min per episode'], ['Genres', 'Drama, Crime, Action & Adventure'],
    ['Seasons', '1 · 13 episodes'], ['Created by', 'Spencer Hudnut'], ['Network', 'CBS'], ['Studio', '101 Studios'],
    ['Country', 'United States'], ['Language', 'English'], ['Original title', 'Marshals'],
    ['Budget', 'Unknown'], ['Revenue', 'Unknown'],
  ])}</div>
  <div class="sec"><h3>Technical information</h3>${list([
    ['Video', '1080p · HEVC (H.265)'], ['Audio', 'Dolby Digital Plus · 5.1'], ['Subtitles', 'English, English SDH'],
    ['Container', 'MKV'], ['Files', '13 episodes · 15 GB'], ['Average size', '1.2 GB per episode'], ['Source', 'Put.io'],
    ['Folder', 'TV / Marshals (2026)'], ['Added', '24 October 2026'],
  ])}</div></div>
  <div class="sec"><h3>Cast <em>8 of 32</em></h3><div class="people">${cast.map(([n, r], i) => person(i, n, r)).join('')}</div></div>
  <div class="sec"><h3>Crew <em>8 of 41</em></h3><div class="people">${crew.map(([n, r], i) => person(i + 3, n, r)).join('')}</div></div>
  </div>`;

function more(colour, pos) {
  return header(3) + `<div class="content">${row('In your library', [
    card(5, 'Justified', '2010', { watched: true, oneLine: true }),
    card(6, 'Justified: City Primeval', '2023', { focused: true, oneLine: true }),
    card(7, 'Big Sky', '2020', { oneLine: true }),
  ], '3 titles')}${row('On your services', [
    card(0, 'Legends', '2026', { service: 'netflix', colour, pos, oneLine: true }),
    card(3, 'Reacher', '2022', { service: 'primevideo', colour, pos, oneLine: true }),
    card(1, 'Faraway Downs', '2023', { service: 'disneyplus', colour, pos, oneLine: true }),
  ], '3 titles')}</div>`;
}

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
.svc,.lplay{position:absolute;display:flex;filter:drop-shadow(0 2px 6px rgba(0,0,0,.75))}
.svc svg,.lplay svg{width:100%;height:100%}
.svc.centre{left:50%;top:40%;width:150px;height:64px;transform:translate(-50%,-50%)}
.lplay.centre{left:50%;top:40%;width:64px;height:64px;transform:translate(-50%,-50%)}
.svc.corner{right:18px;bottom:24px;width:104px;height:40px}.svc.corner svg{object-position:right}
.lplay.corner{right:18px;bottom:24px;width:40px;height:40px}
.chev{font-style:normal;margin-left:10px;color:#c3ccdc}
.rowblock h4{display:inline-block;border-radius:14px;padding:4px 16px}
h4.hf{background:#F4F6FB;color:#0B0D12}
.rail{padding-left:0}
h4.hf em,h4.hf .chev{color:#3a3f48}
.page{gap:56px;right:72px}
.page h3{font-size:20px;margin-bottom:18px}
.page .facts{row-gap:26px}.page .fact span{font-size:18px}.page .fact b{font-size:23px;margin-top:4px}
.page .descr .body{font-size:24px;line-height:1.55}.page .scores .score{height:92px}
.people{display:grid;grid-template-columns:repeat(8,1fr);column-gap:24px}
.pp{text-align:center}.face{width:150px;height:150px;border-radius:50%;margin:0 auto 14px;display:flex;align-items:center;
  justify-content:center;box-shadow:0 6px 16px rgba(0,0,0,.4)}.face i{font-style:normal;font-size:40px;font-weight:500;opacity:.55}
.pair{display:flex;gap:80px}.pair .sec{flex:1}
.vlist .kv{height:44px;font-size:22px;border-top:1px solid rgba(255,255,255,.08)}.vlist .kv:first-child{border-top:none}
.hct small{font-size:19px;font-weight:400;color:#e6ebf5}
.pp b{display:block;font-size:20px;font-weight:500}.pp span{display:block;font-size:17px;color:#aeb7c8;margin-top:4px}
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
h3{font-size:19px;font-weight:500;letter-spacing:1.5px;text-transform:uppercase;color:#aeb7c8;margin-bottom:10px}
h3 em{font-style:normal;text-transform:none;letter-spacing:0;color:#8f98a8;margin-left:10px}
.band{display:flex;gap:60px;align-items:flex-start}.descr{flex:1.35}.recep{flex:1}
.descr .body{font-size:22px;line-height:1.45}.descr .tag{font-size:22px}
.scores{display:grid;grid-template-columns:1fr 1fr;column-gap:30px}.scores .score{height:76px}
.facts{display:grid;grid-template-columns:repeat(4,1fr);column-gap:40px;row-gap:12px}
.fact span{display:block;font-size:17px;color:#9aa4b6}.fact b{display:block;font-size:21px;font-weight:500;margin-top:2px;
  white-space:nowrap;overflow:hidden;text-overflow:ellipsis}.fact b.unk{color:#8f98a8;font-weight:400}
.kv{height:31px}
`;

const shots = [
  ['v6-01-seasons-episodes.png', episodes, 1080],
  ['v6-02-details-whole-page.png', details, 2000],
  ['v6-03-more-like-this.png', more(true, 'centre'), 1080],
];

(async () => {
  const browser = await chromium.launch();
  const page = await browser.newPage({ viewport: { width: 1920, height: 1080 } });
  for (const [file, body, height] of shots) {
    await page.setViewportSize({ width: 1920, height });
    await page.setContent(`<style>${css}body{height:${height}px}</style><body>${body}</body>`);
    await page.screenshot({ path: path.join(out, file) });
  }
  await browser.close();
})();
