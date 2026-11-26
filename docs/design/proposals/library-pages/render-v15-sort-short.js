// v15 (owner, 20 Nov 2026): Sort trimmed so it never scrolls. Order General · Trakt · Series · Files (Files last; Series
// only where TV shows are). Removed: Container, Path, Bitrate, Codec (owner); Date modified, HDR, Audio (Claude's
// proposal, to fit without scrolling). Shown on the own page, the longest case (11 sorts).
// Run: FONT_DIR=<dir with roboto-latin-*-normal.woff2> node render-v15-sort-short.js
const fs = require('fs');
const src = fs.readFileSync(require.resolve('./render-v14-sort.js'), 'utf8');
// reuse v14's pieces without running its render
const body = src.replace(/render\(\[\['44[\s\S]*$/, '');
const mod = new Function('require', '__dirname', `${body}
return { S, group, toolbar, grid, DOCS, extraCss };`)(require, __dirname);
const { svg, I, render } = require('./render-v11-filters.js');
const { S, group, toolbar, grid, DOCS, extraCss } = mod;

const GENERAL = [[S.added, 'Date added'], [S.title, 'Title'], [S.rel, 'Release date']];
const TRAKT = [[S.trend, 'Trending'], [S.pop, 'Popular']];
const SERIES = [[S.seasons, 'Seasons'], [S.eps, 'Episodes'], [S.per, 'Size per episode']];
const FILES = [[S.run, 'Running time'], [S.res, 'Resolution'], [S.size, 'File size']];

const css = `${extraCss}
.sortm .i.one{height:58px}
.sortm .h{padding:8px 24px 6px;margin-right:0}
.sortm .i{margin-right:10px}
`;

function frame() {
  const groups = group('General', GENERAL, 'Date added', 'Date added') + group('Trakt', TRAKT)
    + group('Series', SERIES) + group('Files', FILES);
  return toolbar('Documentaries', 'Date added', 'Newest first') + grid(DOCS)
    + `<div class="menu sortm">${groups}</div>`
    + '<div class="cap2">12 · Sort trimmed: no scrolling, Files last</div>';
}

render([['46-v15-sort-short', frame]], css);
