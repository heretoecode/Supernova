// TV set (owner, 18 Nov 2026): full-screen 1920 x 1080, no frame, to view on the TV itself.
const { chromium } = require('playwright');
const path = require('path');
(async () => {
  const b = await chromium.launch();
  const p = await b.newPage({ viewport: { width: 1920, height: 1080 }, deviceScaleFactor: 1 });
  const url = 'file://' + path.join(__dirname, 'tv.html');
  const out = [['plain-medium', 'tv-1-plain'], ['files-medium', 'tv-2-files-speckle'],
    ['files-charcoal', 'tv-3-files-charcoal-today'], ['settings-medium', 'tv-4-settings-speckle'],
    ['home-medium', 'tv-5-home-speckle']];
  for (const [h, name] of out) {
    await p.goto(url + '#' + h);
    await p.reload();
    await p.waitForTimeout(500);
    await p.screenshot({ path: path.join(__dirname, name + '.png') });
  }
  await b.close();
})();
