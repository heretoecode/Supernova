// v17 (owner, 20 Nov 2026): the final Sort. General (Date added · Title · Release date) · Trakt (Trending · Popular) ·
// Series (Last episode added · Number of seasons · Number of episodes) · Files (Running time · Size). One Size sort:
// films by file size, shows by size per episode; its name follows the page (Movies "File size", TV Shows "Size per
// episode", mixed pages "Size"). Shown on the own page sorted by Size, largest first, so films and shows mix.
// Run: FONT_DIR=<dir with roboto-latin-*-normal.woff2> node render-v17-sort-final.js
const fs = require('fs');
const src = fs.readFileSync(require.resolve('./render-v14-sort.js'), 'utf8');
const body = src.replace(/render\(\[\['44[\s\S]*$/, '');
const mod = new Function('require', '__dirname', `${body}
return { S, group, toolbar, grid, extraCss };`)(require, __dirname);
const { render } = require('./render-v11-filters.js');
const { S, group, toolbar, grid, extraCss } = mod;

const N = {
  tray: '<path d="M3 13l3-8h12l3 8v6a1 1 0 0 1-1 1H4a1 1 0 0 1-1-1z"/><path d="M3 13h5l1.5 2.5h5L16 13h5"/>'
    + '<path d="M12 3.5v7M9 8l3 3 3-3"/>',
  az: '<path d="M3 10l3-7 3 7M4 8h4M3 14h6l-6 7h6"/><path d="M17 4v16M13.5 16.5L17 20l3.5-3.5"/>',
  numbered: '<path d="M4 6h1.5v4M4 10h3M4 14.5c0-1 2.8-1 2.8.3 0 1.2-2.8 2-2.8 3.2h3M11 7h9M11 12h9M11 17h9"/>',
  newEp: '<rect x="3" y="5" width="15" height="12" rx="2"/><path d="M8.5 21h6M21 3v6M18 6h6"/>'
    + '<path d="M9 8.5l4 2.5-4 2.5z"/>',
};

const GENERAL = [[N.tray, 'Date added'], [N.az, 'Title'], [S.rel, 'Release date']];
const TRAKT = [[S.trend, 'Trending'], [S.pop, 'Popular']];
const SERIES = [[N.newEp, 'Last episode added'], [N.numbered, 'Number of seasons'], [S.eps, 'Number of episodes']];
const FILES = [[S.run, 'Running time'], [S.size, 'Size']];

const SIZED = [
  ['Mountain Light', '2023 · 1 h 47 min', '24.6 GB', null],
  ['Frozen Frontier', '3 seasons · 18 episodes', '3.1 GB each · 55.8 GB', null],
  ['Deep Blue Planet', 'S1 E3 · 4 of 6 left', '2.5 GB each · 15.0 GB', 0.4],
  ['Inside the Hive', '2023 · 1 h 24 min', '2.4 GB', 'w'],
  ['The Lost Rivers', '4 episodes', '2.2 GB each · 8.8 GB', null],
  ['City of Birds', '2019 · 1 h 28 min', '2.1 GB', null],
  ['Tidelands', 'S1 E2 · 31 of 32 left', '1.4 GB each · 44.8 GB', 0.1],
  ['Wild Isles of the North', '5 episodes', '1.2 GB each · 6.0 GB', 'w'],
  ['Desert Bloom', '2020 · 1 h 22 min', '1.1 GB', null],
  ['The Green Valley', '5 episodes', '1.0 GB each · 5.0 GB', null],
  ['The Salt Road', '42 min left', '0.9 GB', 0.6],
  ['Rails Across Asia', '3 episodes', '0.8 GB each · 2.4 GB', null],
  ['The Coral Clock', '2019 · 1 h 41 min', '0.7 GB', 'w'],
  ['Night Shift Ocean', '15 episodes', '0.5 GB each · 7.5 GB', null],
];

const css = `${extraCss}
.sortm .i.one{height:58px}
.sortm .h{padding:8px 24px 6px;margin-right:0}
.sortm .i{margin-right:10px}
`;

function frame() {
  const groups = group('General', GENERAL) + group('Trakt', TRAKT) + group('Series', SERIES)
    + group('Files', FILES, 'Size', 'Size');
  const bar = toolbar('Documentaries', 'Size', 'Largest first').replace('width:210px', 'width:226px');
  return bar + grid(SIZED)
    + '<div class="cap2">15 · Size: a show gives each episode, then its total</div>';
}

render([['49-v17-size-caption', frame]], css);
