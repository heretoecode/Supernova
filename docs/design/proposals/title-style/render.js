// Title house style (owner, 18 Nov 2026): the four styles the owner liked, on Home, Details and the player.
const { chromium } = require('playwright');
const path = require('path');
(async () => {
  const b = await chromium.launch();
  const p = await b.newPage({ viewport: { width: 3968, height: 1000 }, deviceScaleFactor: 1 });
  const url = 'file://' + path.join(__dirname, 'mock.html');
  const out = [['home', '01-home-hero'], ['details', '02-details-hero'], ['player', '03-player']];
  for (const [page, name] of out) {
    await p.goto(url + '#' + page);
    await p.reload();
    await p.waitForTimeout(300);
    await p.screenshot({ path: path.join(__dirname, name + '.png'), fullPage: true });
  }
  await b.close();
})();
