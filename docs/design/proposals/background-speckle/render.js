// Background speckle (owner, 18 Nov 2026): Home on charcoal with fixed fine grains, three strengths.
const { chromium } = require('playwright');
const path = require('path');
(async () => {
  const b = await chromium.launch();
  const p = await b.newPage({ viewport: { width: 1940, height: 1100 }, deviceScaleFactor: 1 });
  const url = 'file://' + path.join(__dirname, 'mock.html');
  const out = [['faint', '01-faint'], ['medium', '02-medium-darker'], ['strong', '03-strong-darkest']];
  for (const [o, name] of out) {
    await p.goto(url + '#' + o);
    await p.reload();
    await p.waitForTimeout(500);
    await p.screenshot({ path: path.join(__dirname, name + '.png'), fullPage: true });
  }
  await b.close();
})();
