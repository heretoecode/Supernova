// v18 (owner, 20 Nov 2026): six posters a row instead of seven (bigger artwork, wider captions), on the own page sorted
// by Size, largest first. A show's line 3 names both figures: "Average 2.2 GB · total 8.8 GB".
// Run: FONT_DIR=<dir with roboto-latin-*-normal.woff2> node render-v18-six.js
const fs = require('fs');
const src = fs.readFileSync(require.resolve('./render-v14-sort.js'), 'utf8');
const body = src.replace(/render\(\[\['44[\s\S]*$/, '');
const mod = new Function('require', '__dirname', `${body}
return { toolbar, extraCss };`)(require, __dirname);
const v11 = require('./render-v11-filters.js');
const { toolbar, extraCss } = mod;

const SIZED = [
  ['Mountain Light', '2023 · 1 h 47 min', '24.6 GB', null],
  ['Frozen Frontier', '3 seasons · 18 episodes', 'Average 3.1 GB · total 55.8 GB', null],
  ['Deep Blue Planet', 'S1 E3 · 4 of 6 left', 'Average 2.5 GB · total 15.0 GB', 0.4],
  ['Inside the Hive', '2023 · 1 h 24 min', '2.4 GB', 'w'],
  ['The Lost Rivers', '4 episodes', 'Average 2.2 GB · total 8.8 GB', null],
  ['City of Birds', '2019 · 1 h 28 min', '2.1 GB', null],
  ['Tidelands', 'S1 E2 · 31 of 32 left', 'Average 1.4 GB · total 44.8 GB', 0.1],
  ['Wild Isles of the North', '5 episodes', 'Average 1.2 GB · total 6.0 GB', 'w'],
  ['Desert Bloom', '2020 · 1 h 22 min', '1.1 GB', null],
  ['The Green Valley', '5 episodes', 'Average 1.0 GB · total 5.0 GB', null],
  ['The Salt Road', '42 min left', '0.9 GB', 0.6],
  ['Rails Across Asia', '3 episodes', 'Average 0.8 GB · total 2.4 GB', null],
];

function grid(items) {
  const html = v11.page(items, undefined, 'Filters', 6);
  return html.replace(/^[\s\S]*?<\/div>\s*<div class="tb">[\s\S]*?<\/div>/, '');
}

function frame() {
  const bar = toolbar('Documentaries', 'Size', 'Largest first').replace('width:210px', 'width:226px');
  return bar + grid(SIZED) + '<div class="cap2">16 · Six a row: bigger posters, room to say "Average"</div>';
}

v11.render([['50-v18-six-a-row', frame]], extraCss);
