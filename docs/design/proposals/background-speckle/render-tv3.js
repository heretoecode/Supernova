// TV set 3 (owner, 18 Nov 2026): Settings, no pane, corners B (20 px), three speckle densities around tv2's.
const { chromium } = require('playwright');
const path = require('path');
(async () => {
  const b = await chromium.launch();
  const p = await b.newPage({ viewport: { width: 1920, height: 1080 }, deviceScaleFactor: 2 });
  const url = 'file://' + path.join(__dirname, 'tv3.html');
  const out = [['settings-medium-none-013', 'tv3-1-lighter'], ['settings-medium-none-020', 'tv3-2-current'],
    ['settings-medium-none-026', 'tv3-3-a-little-more']];
  for (const [h, name] of out) {
    await p.goto(url + '#' + h);
    await p.reload();
    await p.waitForTimeout(500);
    await p.screenshot({ path: path.join(__dirname, name + '.png') });
  }
  await b.close();
})();
