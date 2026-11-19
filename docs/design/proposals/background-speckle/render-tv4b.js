// TV set 4b (owner, 19 Nov 2026): today's panel colour 16181C at 95%, 98%, 99% and 100% over the speckle.
const { chromium } = require('playwright');
const path = require('path');
(async () => {
  const b = await chromium.launch();
  const p = await b.newPage({ viewport: { width: 1920, height: 1080 }, deviceScaleFactor: 2 });
  const url = 'file://' + path.join(__dirname, 'tv4.html');
  for (const [c, name] of [['16181C_95', 'tv4-D-panel-95'], ['16181C_98', 'tv4-E-panel-98'], ['16181C_99', 'tv4-F-panel-99'], ['16181C_100', 'tv4-G-panel-100']]) {
    await p.goto(url + '#settings-medium-none-020-' + c);
    await p.reload();
    await p.waitForTimeout(500);
    await p.screenshot({ path: path.join(__dirname, name + '.png') });
  }
  await b.close();
})();
