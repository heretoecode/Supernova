// Next episode, Details tab (owner, 25 Nov 2026): the TV show's Details tab (details-v3/tv/13) with the next episode's
// date folded into Status ("Returning · S2 E4 on Sun 30 Nov"), so no extra row and the columns stay level.
// Run: FONT_DIR=<dir with roboto-latin-*-normal.woff2> node render-2.js
const fs = require('fs');
const path = require('path');
const dir = path.join(__dirname, '..', 'details-v3');
let src = fs.readFileSync(path.join(dir, 'render-3.js'), 'utf8');
src = src.replace("['Status', 'Returning · 2 seasons']", "['Status', 'Returning · S2 E4 on Sun 30 Nov']");
src = src.replace("'13 · A TV show: averages and totals; mixed values show the most common and the odd ones out'",
  "'4 · Details tab: the next episode in Status'");
src = src.replace(/const FRAMES = \[.*\];/, "const FRAMES = [['04-details-tab-status', show]];");
src = src.replace("path.join(__dirname, 'tv', `${name}.png`)", "path.join(OUT, `${name}.png`)");
new Function('require', '__dirname', 'OUT', src)(require, dir, path.join(__dirname, 'tv'));
