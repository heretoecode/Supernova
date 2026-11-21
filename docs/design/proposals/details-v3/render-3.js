// Details v3, round 3 (owner, 21 Nov 2026): the Details tab for the other three kinds (the movie is tv/02). Hero foot
// peeks above the tabs (no title). 13 a TV show; 14 a season; 15 an episode. Groups show average and total; mixed
// technical values show the most common and how many differ.
// Run: FONT_DIR=<dir with roboto-latin-*-normal.woff2> node render-3.js
const fs = require('fs');
const path = require('path');
let src = fs.readFileSync(path.join(__dirname, 'render.js'), 'utf8');
src = src.slice(0, src.lastIndexOf("const FRAMES = [['"));
src = src.replace("const { chromium } = require('playwright');", '');
const m = new Function('require', '__dirname', `${src}
return { extra, svg, D, bar, speckle, tabs, kv, people };`)(require, __dirname);
const { chromium } = require('playwright');
const { extra, svg, D, bar, speckle, tabs, kv, people } = m;

function rec(rows) {
  const brand = {
    imdb: '<span class="brand" style="color:#f5c518">IMDb</span>',
    rt: '<span class="brand"><svg width="34" height="34" viewBox="0 0 24 24"><circle cx="12" cy="13" r="9" fill="#fa320a"/></svg></span>',
    mc: '<span class="brand" style="color:#ffcc33">◯</span>',
    tmdb: '<span class="brand" style="color:#01b4e4">TMDB</span>',
    award: `<span class="brand">${svg(D.trophy, 26)}</span>`,
  };
  return rows.map(([b, v, sub, q]) => `<div class="rec">${brand[b]}<span style="${q ? 'color:#8f98a8;font-weight:400' : ''}">${v}
    </span>${sub ? `<small>${sub}</small>` : ''}</div>`).join('');
}

function page(topBar, recRows, key, tech, cast, crew, label, castTitle) {
  return bar(topBar) + '<div class="mini-logo"></div>' + tabs(170, 'Details')
    + `<div class="cols3"><div><div class="ch3">${svg(D.star, 28)}Reception</div>${rec(recRows)}</div>
      <div><div class="ch3">${svg(D.list, 28)}Key information</div>${kv(key)}</div>
      <div><div class="ch3">${svg(D.chip, 28)}Technical information</div>${kv(tech)}</div></div>
      <div class="people"><div><div class="ch3">${svg(D.cast, 28)}${castTitle || 'Cast'}</div>${people(cast)}</div>
      <div><div class="ch3">${svg(D.crew, 28)}Crew</div>${people(crew)}</div></div><div class="cap2">${label}</div>`;
}

const CAST = [['Mara Lind', 'Herself'], ['Tom Oyelaran', 'Narrator'], ['Ilse Berg', 'Herself'], ['Kai Nolan', 'Himself'],
  ['Ruth Maddox', 'Herself'], ['Jon Aske', 'Himself'], ['Pia Sund', 'Herself']];

function show() {
  return page('TV Shows',
    [['imdb', '8.2/10', '18K votes'], ['rt', '94%'], ['mc', '81'], ['tmdb', '8.0/10', '1,240 votes'],
      ['award', '1 BAFTA win']],
    [['First aired', '3 March 2024'], ['Status', 'Returning · 2 seasons'], ['Episodes', '18 · 16 in your library'],
      ['Episode length', 'Average 49 min'], ['Total running time', '13 h 4 min'],
      ['Genres', 'Documentary, Nature, History, Travel'], ['Created by', 'Ruth Kane'], ['Network', 'BBC Two'],
      ['Country', 'United Kingdom'], ['Language', 'English']],
    [['Resolution', '1080p · 2 in 2160p'], ['Video', 'HEVC (H.265)'], ['HDR', 'SDR · 2 in HDR10'], ['Frame rate', '25 fps'],
      ['Audio', 'EAC3 · 5.1'], ['Audio languages', 'English'], ['Subtitles', 'English, English SDH'],
      ['Size', 'Average 2.2 GB · total 35.2 GB'], ['Files', '16 episodes'], ['Location', 'Media server › TV Shows › Tidelands']],
    CAST, [['Ruth Kane', 'Creator'], ['Ewan Hale', 'Executive producer'], ['Sara Quist', 'Producer'],
      ['Lars Moe', 'Cinematography'], ['Ana Rhys', 'Music'], ['Will Tarn', 'Editor'], ['Dee Walsh', 'Sound']],
    '13 · A TV show: averages and totals; mixed values show the most common and the odd ones out', 'Main cast');
}

function season() {
  return page('TV Shows',
    [['imdb', 'Unknown', '', 1], ['rt', '96%', 'Season 2'], ['mc', 'Unknown', '', 1], ['tmdb', '8.3/10', 'Season 2 · 210 votes'],
      ['award', 'Unknown', '', 1]],
    [['Aired', '2 March – 20 April 2025'], ['Season', '2 of 2'], ['Episodes', '8 · 8 in your library'],
      ['Episode length', 'Average 50 min'], ['Total running time', '6 h 40 min'],
      ['Genres', 'Documentary, Nature, History, Travel'], ['Directed by', 'Ruth Kane and 2 others'],
      ['Written by', 'Ewan Hale and 1 other'], ['Network', 'BBC Two'], ['Language', 'English']],
    [['Resolution', '2160p · 1 in 1080p'], ['Video', 'HEVC (H.265) · 10-bit'], ['HDR', 'HDR10 · 1 in SDR'],
      ['Frame rate', '25 fps'], ['Audio', 'EAC3 · 5.1'], ['Audio languages', 'English'], ['Subtitles', 'English, English SDH'],
      ['Size', 'Average 2.9 GB · total 23.2 GB'], ['Files', '8 episodes'],
      ['Location', 'Media server › TV Shows › Tidelands › Season 2']],
    CAST, [['Ruth Kane', 'Director'], ['Ewan Hale', 'Writer'], ['Sara Quist', 'Producer'], ['Lars Moe', 'Cinematography'],
      ['Ana Rhys', 'Music'], ['Will Tarn', 'Editor'], ['Dee Walsh', 'Sound']],
    '14 · A season: its own dates, scores where they exist, averages and totals', 'Season cast');
}

function episode() {
  return page('TV Shows',
    [['imdb', '8.4/10', '2.1K votes'], ['rt', 'Unknown', '', 1], ['mc', 'Unknown', '', 1], ['tmdb', '8.0/10', '64 votes'],
      ['award', 'Unknown', '', 1]],
    [['Aired', '16 March 2025'], ['Episode', 'Season 2 · Episode 3'], ['Runtime', '49 min'],
      ['Genres', 'Documentary, Nature, History, Travel'], ['Directed by', 'Ruth Kane'], ['Written by', 'Ewan Hale'],
      ['Guest stars', 'Ola Brekke, Siv Arne'], ['Network', 'BBC Two'], ['Country', 'United Kingdom'], ['Language', 'English']],
    [['Resolution', '2160p (3840 × 2160)'], ['Video', 'HEVC (H.265) · 10-bit'], ['HDR', 'HDR10'], ['Frame rate', '25 fps'],
      ['Audio', 'EAC3 · 5.1'], ['Audio languages', 'English'], ['Subtitles', 'English, English SDH'], ['Size', '2.8 GB'],
      ['File', 'Tidelands.S02E03.The.Long.Tide.2160p.mkv'], ['Location', 'Media server › TV Shows › Tidelands › Season 2']],
    [['Mara Lind', 'Herself'], ['Tom Oyelaran', 'Narrator'], ['Ola Brekke', 'Guest'], ['Siv Arne', 'Guest'],
      ['Ilse Berg', 'Herself'], ['Kai Nolan', 'Himself'], ['Ruth Maddox', 'Herself']],
    [['Ruth Kane', 'Director'], ['Ewan Hale', 'Writer'], ['Sara Quist', 'Producer'], ['Lars Moe', 'Cinematography'],
      ['Ana Rhys', 'Music'], ['Will Tarn', 'Editor'], ['Dee Walsh', 'Sound']],
    '15 · An episode: like a movie (one file), with its season and guest stars');
}

const FRAMES = [['13-kind-show', show], ['14-kind-season', season], ['15-kind-episode', episode]];

(async () => {
  const browser = await chromium.launch();
  const pg = await browser.newPage({ viewport: { width: 1920, height: 1080 }, deviceScaleFactor: 1 });
  for (const [name, fn] of FRAMES) {
    const html = `<!doctype html><html><head><meta charset="utf-8"><style>${extra}</style></head><body>
      <div class="screen">${fn()}<div class="border"></div></div>${speckle()}</body></html>`;
    await pg.setContent(html);
    await pg.evaluate(() => document.fonts.ready);
    await pg.screenshot({ path: path.join(__dirname, 'tv', `${name}.png`) });
  }
  await browser.close();
})();
