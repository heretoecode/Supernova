// TV set 2 (owner, 18 Nov 2026): 4K, lighter speckle, soft pane behind the list (without / with), and one rounded
// shape for every small box (buttons, pills, chips, tab and top-bar focus).
const { chromium } = require('playwright');
const path = require('path');
(async () => {
  const b = await chromium.launch();
  const p = await b.newPage({ viewport: { width: 1920, height: 1080 }, deviceScaleFactor: 2 });
  const url = 'file://' + path.join(__dirname, 'tv2.html');
  const out = [['files-medium-none', 'tv2-1-files-no-pane'], ['files-medium-pane', 'tv2-2-files-pane'],
    ['settings-medium-none', 'tv2-3-settings-no-pane'], ['settings-medium-pane', 'tv2-4-settings-pane']];
  for (const [h, name] of out) {
    await p.goto(url + '#' + h);
    await p.reload();
    await p.waitForTimeout(500);
    await p.screenshot({ path: path.join(__dirname, name + '.png') });
  }
  await b.close();
})();
