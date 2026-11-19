// TV set 4 (owner, 19 Nov 2026): panel shade over the speckle: A today's 16181C at 90%, B 1D2025, C 24272D (solid).
const { chromium } = require('playwright');
const path = require('path');
(async () => {
  const b = await chromium.launch();
  const p = await b.newPage({ viewport: { width: 1920, height: 1080 }, deviceScaleFactor: 2 });
  const url = 'file://' + path.join(__dirname, 'tv4.html');
  const out = [['16181C', 'tv4-A-panel-today'], ['1D2025', 'tv4-B-panel-a-shade-lighter'],
    ['24272D', 'tv4-C-panel-lighter-still']];
  for (const [c, name] of out) {
    await p.goto(url + '#settings-medium-none-020-' + c);
    await p.reload();
    await p.waitForTimeout(500);
    await p.screenshot({ path: path.join(__dirname, name + '.png') });
  }
  await b.close();
})();
