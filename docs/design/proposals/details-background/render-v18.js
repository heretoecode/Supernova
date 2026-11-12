// Details v18 (owner, 12 Nov 2026): the approved Details design with docs/design/RULES.md applied.
// 1 A movie's Details tab: headings with icons and gaps (no underlines), no row lines on read-only information,
//   gaps instead of column dividers. 2 An episode's hero with the More menu open: title line, age-rating badge,
//   metadata row (Age rating · Year · Length · Genres), final menu contents. 3 The artwork picker.
// Made-up film and names. Run: FONT_DIR=<roboto woff2 dir> node render-v18.js
const fs = require('fs');
const path = require('path');
const { chromium } = require('playwright');
const fontDir = process.env.FONT_DIR;
const font = w => `@font-face{font-family:R;font-weight:${w};src:url(data:font/woff2;base64,${fs.readFileSync(
  path.join(fontDir, `roboto-latin-${w}-normal.woff2`)).toString('base64')}) format('woff2')}`;
const svg = (d, s = 30) => `<svg width="${s}" height="${s}" viewBox="0 0 24 24" fill="none" stroke="currentColor"
  stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round">${d}</svg>`;
const I = {
  search: '<circle cx="11" cy="11" r="7"/><path d="M20 20l-4-4"/>',
  cog: '<circle cx="12" cy="12" r="3"/><path d="M12 2v3M12 19v3M4.2 4.2l2.1 2.1M17.7 17.7l2.1 2.1M2 12h3M19 12h3'
    + 'M4.2 19.8l2.1-2.1M17.7 6.3l2.1-2.1"/>',
  info: '<circle cx="12" cy="12" r="9"/><path d="M12 11v6M12 7.5h.1"/>',
  eps: '<rect x="3" y="4" width="18" height="12" rx="2"/><path d="M7 20h10"/><path d="M10 8l5 2.5-5 2.5z"/>',
  extras: '<rect x="3" y="7" width="18" height="13" rx="2"/><path d="M3 7l3-4h3l-3 4M10 7l3-4h3l-3 4"/>',
  more: '<path d="M12 3l1.8 4.6L18 9l-4.2 1.4L12 15l-1.8-4.6L6 9l4.2-1.4z"/>',
  star: '<path d="M12 3l2.7 5.6 6.1.9-4.4 4.3 1 6.1L12 17l-5.4 2.9 1-6.1L3.2 9.5l6.1-.9z"/>',
  key: '<path d="M4 6h16M4 12h16M4 18h10"/>',
  chip: '<rect x="6" y="6" width="12" height="12" rx="2"/><path d="M9 2v4M15 2v4M9 18v4M15 18v4M2 9h4M2 15h4'
    + 'M18 9h4M18 15h4"/>',
  people: '<circle cx="9" cy="8" r="3.5"/><path d="M2.5 20a6.5 6.5 0 0 1 13 0"/><circle cx="17" cy="9" r="2.5"/>'
    + '<path d="M16 14.2a5 5 0 0 1 5.5 4.8"/>',
  crew: '<path d="M4 20V9l8-5 8 5v11"/><path d="M9 20v-6h6v6"/>',
  play: '<path d="M7 4l13 8-13 8z"/>',
  dots: '<path d="M5 12h.1M12 12h.1M19 12h.1"/>',
  restart: '<path d="M4 12a8 8 0 1 0 2.3-5.7"/><path d="M4 4v4h4"/>',
  watched: '<circle cx="12" cy="12" r="9"/><path d="M8 12.5l2.8 2.8L16.5 9.5"/>',
  fix: '<circle cx="11" cy="11" r="7"/><path d="M20 20l-4-4M8 11l2 2 4-4"/>',
  remove: '<rect x="3" y="4" width="18" height="16" rx="2"/><path d="M8 12h8"/>',
  subs: '<rect x="3" y="5" width="18" height="14" rx="2"/><path d="M7 14h4M13 14h4M7 10h10"/>',
  art: '<rect x="3" y="4" width="18" height="16" rx="2"/><circle cx="9" cy="10" r="2"/><path d="M21 16l-5-5-9 9"/>',
  page: '<path d="M6 3h9l4 4v14H6z"/><path d="M14 3v5h5M9 12h7M9 16h7"/>',
  bin: '<path d="M4 7h16M10 11v6M14 11v6M6 7l1 13h10l1-13M9 7V4h6v3"/>',
  auto: '<path d="M12 3l1.8 4.6L18 9l-4.2 1.4L12 15l-1.8-4.6L6 9l4.2-1.4z"/><path d="M19 15l.8 2 2 .8-2 .8-.8 2'
    + '-.8-2-2-.8 2-.8z"/>',
  tick: '<path d="M5 12.5l4.5 4.5L19 7.5"/>',
};
const nav = (place, mark) => `<div class="nav"><span class="brand">SUPERNOVA</span>${['Home', 'Movies',
  'TV Shows', 'Documentaries'].map(t => `<span class="ni ${t === place ? mark : ''}">${t}</span>`).join('')}
  <span class="ni files">Files</span><span class="ni icon">${svg(I.search, 34)}</span>
  <span class="ni icon">${svg(I.cog, 34)}</span><span class="clock">21:40</span></div>`;
const tabs = (list, open, mark) => `<div class="tabs">${list.map(([ic, t]) => `<span class="tab ${t === open ? mark
  : ''}">${svg(I[ic])}${t}</span>`).join('')}</div>`;
const MOVIE_TABS = [['info', 'Details'], ['extras', 'Extras'], ['more', 'More Like This']];
const ALL_TABS = [['info', 'Details'], ['eps', 'Episodes'], ['extras', 'Extras'], ['more', 'More Like This']];
const hd = (ic, t) => `<div class="hd">${svg(I[ic], 32)}${t}</div>`;
const kv = (k, v) => `<div class="kv"><span>${k}</span><b class="${v === 'Unknown' ? 'unk' : ''}">${v}</b></div>`;
const rec = (mark, value, votes) => `<div class="rec"><span class="rm">${mark}</span><b class="${value === 'Unknown'
  ? 'unk' : ''}">${value}${votes ? `<small>${votes}</small>` : ''}</b></div>`;
const tomato = '<svg width="54" height="54" viewBox="0 0 24 24"><circle cx="12" cy="13.5" r="8" fill="#E8352B"/>'
  + '<path d="M9 5.5c1.5.8 2.4.8 3 2 .6-1.2 1.6-1.4 3-2-1 1.6-1.6 2.2-3 2.4-1.4-.2-2-.8-3-2.4z" fill="#2E9E3A"/></svg>';
const meta = '<svg width="54" height="54" viewBox="0 0 24 24"><circle cx="12" cy="12" r="10" fill="none" '
  + 'stroke="#F2C230" stroke-width="2"/><path d="M8 15.5V9.5l4 4 4-4v6" fill="none" stroke="#F2C230" '
  + 'stroke-width="2"/></svg>';
const cols = ['#4a6b4a', '#8a4a3a', '#5a4a8a', '#8a7a3a', '#2e6a6a', '#7a3a5a', '#3a5a8a', '#8a6a3a'];
const person = (i, name, role) => `<div class="pp"><span class="face" style="background:radial-gradient(circle at
  50% 30%,${cols[i % 8]},#121418 85%)">${name.split(' ').map(w => w[0]).join('').slice(0, 2)}</span>
  <b>${name}</b><small>${role}</small></div>`;
const CAST = [['Nora Hale', 'Ellen Marsh'], ['Tomas Vey', 'Daniel Crane'], ['Ada Okafor', 'Ruth Abara'],
  ['Leon Brandt', 'Sheriff Cole'], ['Mina Sato', 'Kiko'], ['Rafe Dunmore', 'Pike'], ['Iris Penrose', 'Grace'],
  ['Owen Tull', 'The ferryman']];
const CREW = [['Anna Reyes', 'Director'], ['Jon Feld', 'Writer'], ['Sam Ortiz', 'Writer'], ['Lena Kroll',
  'Producer'], ['Max Ibsen', 'Cinematography'], ['Ola Grey', 'Music'], ['Bea Holm', 'Editor'], ['Kit Vance',
  'Production design']];
const detailsTab = `${nav('Movies', 'dull')}<div class="dtop"><div class="clogo">THE LAST CROSSING</div>
  ${tabs(MOVIE_TABS, 'Details', 'box')}</div>
  <div class="cols"><div>${hd('star', 'Reception')}
    ${rec('<b class="imdb">IMDb</b>', '7.4/10', '212K votes')}${rec(tomato, '91%')}${rec(meta, '78')}
    ${rec('<b class="tmdb">TMDB</b>', '7.6/10', '3,104 votes')}${rec(svg('<path d="M8 21h8M12 17v4M7 4h10v5a5 5 0 0 1'
      + '-10 0zM7 6H4a3 3 0 0 0 3 4M17 6h3a3 3 0 0 1-3 4"/>', 48), '2 BAFTA nominations')}</div>
  <div>${hd('key', 'Key information')}${kv('Released', '14 March 2025')}${kv('Runtime', '2 h 11 min')}
    ${kv('Genres', 'Drama, Thriller')}${kv('Directed by', 'Anna Reyes')}${kv('Written by', 'Jon Feld, Sam Ortiz')}
    ${kv('Studio', 'Northlight Pictures')}${kv('Country', 'United Kingdom')}${kv('Language', 'English')}
    ${kv('Original title', 'The Last Crossing')}${kv('Budget', 'Unknown')}${kv('Revenue', 'Unknown')}</div>
  <div>${hd('chip', 'Technical information')}${kv('Resolution', '2160p (3840 × 2160)')}
    ${kv('Video', 'HEVC (H.265) · 10-bit')}${kv('HDR', 'HDR10')}${kv('Frame rate', '23.976 fps')}
    ${kv('Bitrate', 'Unknown')}${kv('Audio', 'Dolby Atmos · 7.1')}${kv('Audio languages', 'English')}
    ${kv('Subtitles', 'English, English SDH')}${kv('Container', 'MKV')}${kv('Size', '18.7 GB')}
    ${kv('Added', '28 October 2026')}</div></div>
  <div class="people">${hd('people', 'Cast')}<div class="prow">${CAST.map(([n, r], i) => person(i, n, r)).join('')}
  </div>${hd('crew', 'Crew')}<div class="prow">${CREW.map(([n, r], i) => person(i + 3, n, r)).join('')}</div></div>`;

const heroArt = 'linear-gradient(100deg,rgba(14,16,20,.92) 0%,rgba(14,16,20,.55) 38%,rgba(14,16,20,0) 62%),'
  + 'radial-gradient(ellipse at 72% 45%,#6d7f8f 0%,#3d4a56 35%,#232a31 70%),#1d2228';
const hero = (inner, art) => `<div class="hero" style="background:${art || heroArt}">${inner}</div>`;
const menuItem = (ic, t, focus) => `<div class="mi ${focus ? 'focus' : ''}">${svg(I[ic], 30)}<span>${t}</span></div>`;
const group = (t, items) => `<div class="mg"><span>${t}</span><i></i></div>${items}`;
const episode = `${nav('TV Shows', 'dull')}${hero(`<div class="hcopy"><div class="hlogo">MARSHALS</div>
  <div class="tline">Season 1 · Episode 5 · Wind River</div>
  <div class="hmeta"><span class="age">15</span>2026 · 46 min · Drama, Crime, Action &amp; Adventure</div>
  <div class="hdesc">The team track a fugitive into the Wind River Range, where an old feud with the reservation
  police puts Kayce between the law and the people he grew up with.</div>
  <div class="hbtns"><span class="btn">${svg(I.play, 26)}Resume S1 E5 · 16 min left</span>
  <span class="btn bline">${svg(I.dots, 26)}More</span></div></div>`)}
  <div class="menu">${group('Playback', menuItem('restart', 'Restart episode'))}
  ${group('Library', menuItem('watched', 'Mark as watched', true) + menuItem('fix', 'Fix match')
    + menuItem('remove', 'Remove from library'))}
  ${group('Files &amp; media', menuItem('subs', 'Subtitles') + menuItem('art', 'Artwork')
    + menuItem('page', 'Full description') + menuItem('bin', 'Delete file'))}</div>
  <div class="peek">${tabs(ALL_TABS, 'Details', 'dull')}</div>`;

const shots = ['#3d4a56', '#4b3a2e', '#2e3b4a', '#1f2a24', '#5a4632', '#262230'];
const pick = (i, label, opts) => `<div class="pk ${opts.focus ? 'focus' : ''}"><span class="pimg" style="background:
  radial-gradient(ellipse at ${opts.x || 70}% 45%,${shots[i]} 0%,#15181c 80%)">${opts.auto ? `<span class="autoi">
  ${svg(I.auto, 48)}</span>` : ''}${opts.on ? `<i class="ptick">${svg(I.tick, 26)}</i>` : ''}</span>
  <span class="pl">${label}</span></div>`;
const picker = `${nav('TV Shows', 'dull')}${hero(`<div class="hcopy"><div class="hlogo">MARSHALS</div>
  <div class="hmeta"><span class="age">15</span>2026 · 1 Season · Drama, Crime, Action &amp; Adventure</div>
  <div class="hdesc">With the Yellowstone Ranch behind him, Kayce Dutton joins an elite unit of U.S. Marshals,
  bringing range justice to Montana.</div>
  <div class="hbtns"><span class="btn">${svg(I.play, 26)}Resume S1 E5 · 16 min left</span>
  <span class="btn">${svg(I.dots, 26)}More</span></div></div>`,
  'linear-gradient(100deg,rgba(14,16,20,.92) 0%,rgba(14,16,20,.5) 38%,rgba(14,16,20,0) 60%),'
  + 'radial-gradient(ellipse at 80% 45%,#5a4632 0%,#33291f 40%,#1c1a18 75%)')}
  <div class="side"><div class="sh">${svg(I.art, 32)}Artwork</div>
  <div class="sc"><span class="cl">Choose for</span><span class="chips"><i class="on">Hero</i><i>Row card</i></span></div>
  <div class="grid">${pick(0, 'Automatic', { auto: true, on: true })}${pick(1, 'Backdrop 2', { x: 30 })}
  ${pick(4, 'Backdrop 3', { x: 80, focus: true })}${pick(2, 'Backdrop 4', { x: 50 })}
  ${pick(3, 'Backdrop 5', { x: 75 })}${pick(5, 'Backdrop 6', { x: 40 })}</div>
  <div class="note">The hero shows the picture you are on. Your choice is kept; Automatic never replaces it.</div>
  </div>`;

const css = `${font(400)}${font(500)}${font(700)}*{box-sizing:border-box;margin:0}
body{width:1920px;font-family:R;color:#fff;background:#16181C;position:relative;overflow:hidden}
.nav{position:absolute;left:56px;right:56px;top:24px;height:76px;display:flex;align-items:center;gap:14px;
font-size:33px;color:#8f98a8;z-index:5}.nav .brand{color:#f1ead8;letter-spacing:2px;margin-right:36px;font-weight:500}
.ni{position:relative;display:inline-flex;align-items:center;height:60px;padding:0 18px;border-radius:16px}
.ni.files{margin-left:auto}.nav .clock{margin-left:18px;color:#C9D3E6}
.box{background:#F4F6FB;color:#0B0D12 !important}.dull{color:#C9D3E6}.line{position:relative}
.dull:after,.line:after{content:'';position:absolute;left:18px;right:18px;bottom:-2px;height:4px;border-radius:2px}
.dull:after{background:rgba(255,255,255,.3)}.line:after{background:#fff;left:14px;right:14px;bottom:-12px}
.dtop{position:absolute;left:96px;right:96px;top:130px}.clogo{font-size:46px;font-weight:500;letter-spacing:6px;
color:#f1ead8}
.tabs{display:flex;gap:10px;border-bottom:2px solid rgba(255,255,255,.14);padding-bottom:12px;margin-top:22px}
.tab{position:relative;display:inline-flex;align-items:center;gap:12px;height:60px;padding:0 20px;border-radius:16px;
font-size:30px;font-weight:500;color:#8f98a8}.tab.dull:after{left:20px;right:20px;bottom:-14px}
.hd{display:flex;align-items:center;gap:16px;font-size:32px;font-weight:500;margin-bottom:26px}.hd svg{color:#C9D3E6}
.cols{position:absolute;left:96px;right:96px;top:400px;display:grid;grid-template-columns:1fr 1.25fr 1.25fr;gap:110px}
.kv{display:flex;justify-content:space-between;gap:24px;height:58px;align-items:center;font-size:28px}
.kv span{color:#C9D3E6}.kv b{font-weight:500;text-align:right}.kv b.unk,.rec b.unk{color:#8f98a8;font-weight:400}
.rec{display:flex;align-items:center;height:116px;font-size:34px}.rm{width:150px;display:flex;
justify-content:center}.rec b{flex:1;text-align:center;font-weight:500;white-space:nowrap}.rec small{font-size:23px;color:#C9D3E6;
margin-left:12px;font-weight:400}.imdb{color:#F5C518;font-size:42px;font-weight:700}.tmdb{color:#36C2B4;
font-size:38px;font-weight:700}.rm svg{color:#e8edf6}
.people{position:absolute;left:96px;right:96px;top:1150px}.people .hd{margin-top:20px}
.prow{display:flex;justify-content:space-between;margin-bottom:84px}.pp{width:190px;text-align:center}
.face{display:flex;width:184px;height:184px;border-radius:50%;margin:0 auto 18px;align-items:center;
justify-content:center;font-size:52px;font-weight:500;color:rgba(255,255,255,.6);
box-shadow:inset 0 0 0 2px rgba(255,255,255,.6),inset 0 0 18px 4px rgba(0,0,0,.45)}
.pp b{display:block;font-size:24px;font-weight:500}.pp small{display:block;font-size:20px;color:#C9D3E6;margin-top:4px}
.hero{position:absolute;left:72px;top:118px;width:1776px;height:820px;border-radius:26px;
box-shadow:inset 0 0 0 2px rgba(255,255,255,.6),inset 0 0 26px 6px rgba(0,0,0,.45)}
.hcopy{position:absolute;left:60px;top:70px;width:960px}
.hlogo{font-size:84px;font-weight:500;letter-spacing:8px;color:#f1ead8}
.tline{font-size:32px;font-weight:500;margin-top:16px}
.hmeta{display:flex;align-items:center;gap:14px;font-size:28px;margin-top:14px;color:#e8edf6}
.age{display:inline-flex;align-items:center;justify-content:center;min-width:44px;height:38px;padding:0 10px;
border-radius:8px;font-size:24px;font-weight:700;box-shadow:inset 0 0 0 2px rgba(255,255,255,.7)}
.hdesc{font-size:27px;line-height:1.45;margin-top:22px;color:#e8edf6}
.hbtns{display:flex;gap:16px;margin-top:40px}
.btn{display:inline-flex;align-items:center;gap:12px;height:68px;padding:0 30px;border-radius:18px;font-size:28px;
font-weight:500;background:rgba(22,24,28,.72);color:#fff}
.menu{position:absolute;left:840px;top:262px;padding:18px;border-radius:24px;background:rgba(22,24,28,.9);
box-shadow:0 0 0 2px rgba(255,255,255,.28),0 30px 80px rgba(0,0,0,.6);z-index:4}
.mg{display:flex;align-items:center;gap:16px;padding:14px 18px 8px;font-size:22px;color:#C9D3E6;font-weight:500}
.mg i{flex:1;height:2px;background:rgba(255,255,255,.14)}
.mi{display:flex;align-items:center;gap:20px;height:58px;padding:0 24px;border-radius:14px;font-size:28px;
color:#fff;white-space:nowrap}.mi svg{color:#C9D3E6}.mi.focus{background:#F4F6FB;color:#0B0D12}
.mi.focus svg{color:#0B0D12}
.bline{position:relative}.bline:after{content:'';position:absolute;left:16px;right:16px;bottom:-12px;height:4px;
border-radius:2px;background:#fff}
.peek{position:absolute;left:72px;right:72px;top:962px}
.side{position:absolute;right:0;top:0;bottom:0;width:780px;background:rgba(22,24,28,.9);z-index:6;padding:56px 56px}
.sh{display:flex;align-items:center;gap:16px;font-size:34px;font-weight:500}.sh svg{color:#C9D3E6}
.sc{display:flex;align-items:center;gap:20px;margin-top:30px;font-size:27px}.cl{color:#C9D3E6}
.chips{display:flex;gap:6px}.chips i{font-style:normal;padding:10px 22px;border-radius:14px}
.chips i.on{background:#454B55}
.grid{display:grid;grid-template-columns:1fr 1fr;gap:26px 30px;margin-top:34px}
.pk{display:flex;flex-direction:column;gap:12px}.pimg{position:relative;display:block;width:100%;height:158px;
border-radius:14px;box-shadow:inset 0 0 0 2px rgba(255,255,255,.6),inset 0 0 18px 4px rgba(0,0,0,.45)}
.pk.focus .pimg{transform:scale(1.06);box-shadow:0 0 0 5px #fff,inset 0 0 18px 4px rgba(0,0,0,.45),
0 14px 34px rgba(0,0,0,.55)}.pk.focus .pl{margin-top:10px}
.pl{font-size:25px;color:#C9D3E6}.pk.focus .pl{color:#fff;font-weight:500}
.autoi{position:absolute;inset:0;display:flex;align-items:center;justify-content:center;color:#fff}
.ptick{position:absolute;right:12px;top:12px;width:42px;height:42px;border-radius:50%;background:#fff;color:#0B0D12;
display:flex;align-items:center;justify-content:center}
.note{font-size:24px;color:#C9D3E6;line-height:1.45;margin-top:30px}`;
(async () => {
  const browser = await chromium.launch();
  const tab = await browser.newPage({ viewport: { width: 1920, height: 1080 } });
  const list = [['15-v18-movie-details-tab.png', detailsTab, 2160], ['16-v18-episode-more.png', episode, 1080],
    ['17-v18-artwork-picker.png', picker, 1080]];
  for (const [f, body, h] of list) {
    await tab.setViewportSize({ width: 1920, height: h });
    await tab.setContent(`<html><head><style>${css}body{height:${h}px}</style></head><body>${body}</body></html>`,
      { waitUntil: 'load' });
    await tab.evaluate(() => document.fonts.ready);
    await tab.screenshot({ path: path.join(__dirname, f) });
  }
  await browser.close();
})();
