// Proposal (0.154 walk-through): Home hero — age rating removed from the metadata row, and text readability
// over bright or busy artwork. Real 1080p sizes. Artwork is simulated (gradients/patterns) to reproduce the
// owner's hard cases. Run: FONT_DIR=<dir with roboto-latin-{300,400,500}-normal.woff2> node render.js
const fs = require('fs');
const path = require('path');
const { chromium } = require('playwright');
const out = __dirname;
const fontDir = process.env.FONT_DIR;
const font = w => `@font-face{font-family:R;font-weight:${w};src:url(data:font/woff2;base64,${fs.readFileSync(path.join(fontDir, `roboto-latin-${w}-normal.woff2`)).toString('base64')}) format('woff2')}`;

const art = {
  bright: `background:
      radial-gradient(ellipse 60% 30% at 30% 92%, #5e7b2f 0%, rgba(94,123,47,0) 70%),
      radial-gradient(ellipse 70% 22% at 75% 88%, #6f8a3a 0%, rgba(111,138,58,0) 70%),
      linear-gradient(180deg,#f0c3d9 0%,#f4cf96 38%,#f5d64e 58%,#e9cf73 74%,#d7c08e 100%)`,
  pale: `background:
      radial-gradient(circle at 18% 55%, #ffffff 0 9%, rgba(255,255,255,0) 22%),
      radial-gradient(circle at 34% 48%, #f4f4f2 0 10%, rgba(255,255,255,0) 24%),
      radial-gradient(circle at 52% 58%, #fbfbfa 0 8%, rgba(255,255,255,0) 22%),
      radial-gradient(circle at 26% 70%, #eceae7 0 12%, rgba(255,255,255,0) 26%),
      linear-gradient(180deg,#b9bec6 0%,#d6d8dc 45%,#c8cacd 100%)`,
  busy: `background:
      repeating-linear-gradient(115deg, rgba(220,40,60,.85) 0 18px, rgba(0,0,0,0) 18px 70px),
      repeating-linear-gradient(25deg, #5b6b4a 0 40px, #7d8f62 40px 80px, #46553a 80px 120px)`,
  dark: `background:
      radial-gradient(ellipse 50% 60% at 78% 45%, #8a5a6a 0%, rgba(138,90,106,0) 70%),
      linear-gradient(180deg,#160c14,#0c0710)`,
};
const titles = {
  bright: ['KINGDOM', '#e7d6a2', 'Georgia,serif', '2025 · Documentary · TMDB 7.9/10', 'Filmed across five years in a single spectacular location.'],
  pale: ['TULSA KING', '#e32a2a', 'Impact,Arial Black,sans-serif', '2022 · Drama, Crime, Comedy · TMDB 8.3/10', "Storms break. Kings don't."],
  busy: ['MAYDAY', '#ff4e6a', 'Impact,Arial Black,sans-serif', '2026 · Action, Comedy · TMDB 7.9/10', 'A friendship with major red flags.'],
  dark: ['PRACTICAL MAGIC 2', '#f1c98a', 'Georgia,serif', "2026 · Romance, Fantasy, Comedy · TMDB 7.2/10", "Everything you've witched for."],
};
const treatments = {
  today: { name: 'Today (0.153)', note: 'No protection behind the text.', shadow: 'none', scrim: '' },
  shadow: { name: '1 — Soft text shadow', note: 'Invisible on dark art; lifts text off bright art.',
    shadow: '0 1px 2px rgba(0,0,0,.55), 0 2px 10px rgba(0,0,0,.45)', scrim: '' },
  both: { name: '2 — Shadow + gentle darkening', note: 'Faint shade behind the left third only.',
    shadow: '0 1px 2px rgba(0,0,0,.5), 0 2px 10px rgba(0,0,0,.4)',
    scrim: 'linear-gradient(90deg,rgba(0,0,0,.42) 0%,rgba(0,0,0,.28) 30%,rgba(0,0,0,0) 58%)' },
  strong: { name: '3 — Stronger darkening', note: 'Most legible; artwork dimmer on the left.',
    shadow: '0 1px 2px rgba(0,0,0,.45)',
    scrim: 'linear-gradient(90deg,rgba(0,0,0,.62) 0%,rgba(0,0,0,.45) 32%,rgba(0,0,0,0) 62%)' },
};

const card = (a, t, recommended) => {
  const [title, tc, tf, meta, tag] = titles[a]; const tr = treatments[t];
  return `<div class="wrap"><div class="cap">${tr.name}${recommended ? '<span class="pill">Recommended</span>' : ''}<small>${tr.note}</small></div>
  <div class="card" style="${art[a]}">${tr.scrim ? `<div class="scrim" style="background:${tr.scrim}"></div>` : ''}
    <div class="copy" style="text-shadow:${tr.shadow}">
      <div class="title" style="color:${tc};font-family:${tf};font-size:${title.length > 12 ? 56 : 82}px">${title}</div>
      <div class="meta">${meta}</div><div class="tag">${tag}</div>
      <div class="btn"><svg width="36" height="36" viewBox="0 0 24 24" fill="none" stroke="#fff" stroke-width="1.8" stroke-linecap="round"><circle cx="12" cy="12" r="9"/><path d="M12 11v6M12 7.5v.5"/></svg>More Info</div>
    </div></div></div>`;
};
const css = `${font(400)}${font(500)}
*{box-sizing:border-box;margin:0}
body{width:1920px;height:1080px;overflow:hidden;background:#000;color:#fff;font-family:R;padding:40px 56px}
h1{font-weight:500;font-size:40px} p.sub{font-size:22px;color:#c9d3e6;margin:6px 0 22px}
.grid{display:grid;grid-template-columns:1fr 1fr;gap:22px 30px}
.cap{font-weight:500;font-size:24px;margin-bottom:10px;display:flex;align-items:baseline;gap:12px;flex-wrap:wrap}
.cap small{font-weight:400;font-size:19px;color:#b9c3d6}
.pill{background:#2f6df0;border-radius:999px;padding:2px 12px;font-size:18px}
.card{position:relative;height:380px;border-radius:24px;overflow:hidden}
.scrim{position:absolute;inset:0}
.copy{position:absolute;left:44px;top:34px;right:40%;display:flex;flex-direction:column}
.title{font-size:82px;line-height:1;letter-spacing:4px;text-shadow:none;white-space:nowrap}
.meta{font-size:30px;margin-top:22px;font-weight:400;color:#fff;white-space:nowrap}
.tag{font-size:30px;margin-top:14px;font-weight:400;color:#fff}
.btn{margin-top:30px;height:72px;align-self:flex-start;border-radius:20px;background:rgba(0,0,0,.72);display:inline-flex;align-items:center;gap:14px;padding:0 28px 0 24px;font-size:26px;font-weight:500;text-shadow:none}`;
const page = (a, label) => `<html><head><style>${css}</style></head><body>
  <h1>Home hero readability — ${label}</h1><p class="sub">Age rating removed from the hero row (kept on Details and the playback overlay). Simulated artwork; real 1080p text sizes.</p>
  <div class="grid">${card(a, 'today')}${card(a, 'shadow')}${card(a, 'both', true)}${card(a, 'strong')}</div></body></html>`;

(async () => {
  const browser = await chromium.launch();
  const tab = await browser.newPage({ viewport: { width: 1920, height: 1080 } });
  for (const [file, a, label] of [['01-bright-sky.png', 'bright', 'bright warm sky (like Kingdom)'], ['02-pale-clouds.png', 'pale', 'pale cloudy grey (like Tulsa King)'],
                                  ['03-busy-pattern.png', 'busy', 'busy pattern (like Mayday)'], ['04-dark.png', 'dark', 'dark artwork (like Practical Magic)']]) {
    await tab.setContent(page(a, label), { waitUntil: 'load' });
    await tab.evaluate(() => document.fonts.ready);
    await tab.screenshot({ path: path.join(out, file) });
  }
  await browser.close();
})();
