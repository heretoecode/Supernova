// Details v3, round 2 (owner, 21 Nov 2026): where the More menu leads. 7 Remove from library: back to the page you came
// from, the poster gone, the Undo notice. 8 Subtitles side panel. 9 Artwork side panel (Hero and rows · Library page),
// the hero previewing. 10 Full description: the text alone. 11 Delete file warning (approved 20 Nov). 12 TMDB page.
// Run: FONT_DIR=<dir with roboto-latin-*-normal.woff2> node render-2.js
const fs = require('fs');
const path = require('path');
let src = fs.readFileSync(path.join(__dirname, 'render.js'), 'utf8');
src = src.slice(0, src.lastIndexOf("const FRAMES = [['"));
src = src.replace("const { chromium } = require('playwright');", '');
const m = new Function('require', '__dirname', `${src}
return { extra, svg, I, J, K, D, bar, hero, item, speckle, tabs };`)(require, __dirname);
const { chromium } = require('playwright');
const { extra, svg, I, J, K, D, bar, hero, item, speckle, tabs } = m;

const FILM = { logo: 'The Last<br>Crossing', age: '15', facts: '2025 · 2 h 11 min · Drama, Thriller, Mystery, Crime',
  desc: 'A ferryman who knows every current of the strait agrees to carry one last passenger across, and finds the '
    + 'whole town waiting on the other side.' };
const BTNS = `<span class="btn f">${svg(D.play, 24)}Resume · 42 min left</span><span class="btn">${svg(I.more, 26)}More</span>`;
const P = {
  film: '<rect x="3" y="4" width="18" height="16" rx="2"/><path d="M7 4v16M17 4v16M3 9h4M3 15h4M17 9h4M17 15h4"/>',
  dl: '<path d="M12 4v11M7 10l5 5 5-5M5 20h14"/>',
  file: '<path d="M6 3h8l4 4v14H6z"/><path d="M14 3v4h4"/>',
  off: '<circle cx="12" cy="12" r="9"/><path d="M6 6l12 12"/>',
  folder: '<path d="M3 7a2 2 0 0 1 2-2h4l2 2h8a2 2 0 0 1 2 2v8a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2z"/>',
  hist: '<path d="M12 8v4l3 2"/><path d="M3.1 11a9 9 0 1 1 .9 4.5M3 20v-5h5"/>',
  tick: '<path d="M5 12.5l4.5 4.5L19 7.5"/>',
};

const css = `${extra}
.scrim{position:absolute;inset:0;background:rgba(0,0,0,.55);z-index:9}
.blur{position:absolute;inset:0;backdrop-filter:blur(10px);background:rgba(0,0,0,.45);z-index:9}
.pop{position:absolute;left:50%;top:50%;transform:translate(-50%,-50%);border-radius:20px;background:rgba(22,24,28,.99);
 z-index:10;box-shadow:0 0 0 1.5px rgba(255,255,255,.7),0 18px 48px 12px rgba(0,0,0,.85)}
.toast{position:absolute;left:50%;bottom:44px;transform:translateX(-50%);display:flex;align-items:center;gap:22px;
 padding:20px 20px 30px 26px;border-radius:20px;background:rgba(22,24,28,.99);z-index:12;font-size:24px;white-space:nowrap;
 box-shadow:0 0 0 1.5px rgba(255,255,255,.7),0 18px 48px 12px rgba(0,0,0,.85)}
.toast .u{height:52px;padding:0 24px;border-radius:20px;display:flex;align-items:center}
.toast .u.f{background:#f4f6fb;color:#0b0d12}
.toast .bar6{position:absolute;left:24px;right:24px;bottom:10px;height:3px;border-radius:2px;background:rgba(255,255,255,.18)}
.toast .bar6 b{position:absolute;left:0;top:0;bottom:0;width:70%;border-radius:2px;background:rgba(255,255,255,.7)}
.tb{position:absolute;left:52px;top:102px;display:flex;gap:8px;font-size:24px}
.tb span{height:52px;padding:0 22px;border-radius:20px;display:flex;align-items:center;gap:10px}
.tbline{position:absolute;left:52px;right:52px;top:178px;height:2px;background:linear-gradient(90deg,
 rgba(255,255,255,.04),rgba(255,255,255,.35) 22%,rgba(255,255,255,.35) 78%,rgba(255,255,255,.04))}
.post{position:absolute;width:276px;border-radius:12px;background:#141619;overflow:hidden}
.post .a{height:414px}
.post .c{height:100px;padding:10px 14px}
.post .c b{display:block;font-size:24px;font-weight:500}
.post .c small{display:block;font-size:18px;color:#c9d3e6;margin-top:6px}
.post .e{position:absolute;inset:0;border-radius:12px;box-shadow:inset 0 0 0 1px #fff}
.post.on{box-shadow:0 0 0 4px #fff;transform:scale(1.08);z-index:3}
.side{position:absolute;right:52px;top:24px;width:700px;padding:14px 0 16px;z-index:10;border-radius:16px;
 background:rgba(22,24,28,.99);box-shadow:0 0 0 1.5px rgba(255,255,255,.7),0 18px 48px 12px rgba(0,0,0,.85)}
.i small{margin-left:6px}
.pill{height:34px;padding:0 14px;border-radius:17px;border:1.5px solid rgba(255,255,255,.6);font-size:18px;
 display:flex;align-items:center}
.i.on .pill{border-color:#0b0d12}
.thumbs{display:grid;grid-template-columns:1fr 1fr;gap:16px;padding:8px 24px 6px}
.th{height:180px;border-radius:12px;position:relative;box-shadow:inset 0 0 0 1px #fff}
.th.on{box-shadow:0 0 0 4px #fff}
.th .sug{position:absolute;left:8px;top:8px;height:32px;padding:0 12px;border-radius:16px;background:rgba(10,11,13,.8);
 display:flex;align-items:center;gap:6px;font-size:17px}
.th .tk{position:absolute;right:8px;top:8px;width:36px;height:36px;border-radius:18px;background:#fff;color:#000;
 display:flex;align-items:center;justify-content:center}
.chips{display:flex;gap:6px}
.chips b{height:46px;padding:0 20px;border-radius:20px;display:flex;align-items:center;font-weight:400}
.chips b.sel{background:rgba(255,255,255,.2)}
.sw{width:64px;height:34px;border-radius:17px;position:relative;background:rgba(255,255,255,.25)}
.sw:after{content:"";position:absolute;left:4px;top:4px;width:26px;height:26px;border-radius:13px;background:#fff}
.sw.on{background:#f4f6fb}
.sw.on:after{left:auto;right:4px;background:#0b0d12}
.dlg{width:860px;padding:40px 44px 36px}
.dlg .ti{font-size:34px;font-weight:500}
.dlg .l1{font-size:26px;margin-top:20px;color:#c9d3e6}
.dlg .l1 b{color:#fff;font-weight:500}
.dlg .l2{font-size:22px;color:#c9d3e6;margin-top:8px}
.dlg .h{padding:22px 0 8px;font-size:26px}
.dlg .i{margin:0;padding:0 4px;height:62px}
.dlg .bt{display:flex;gap:12px;margin-top:28px;align-items:center}
.dlg .bt span{height:52px;padding:0 24px;border-radius:20px;display:flex;align-items:center;gap:10px;font-size:24px}
.dlg .bt .f{background:#f4f6fb;color:#0b0d12}
`;

const ART = [['#7a3b2e', '#26120d'], ['#2f5c4a', '#0e1d17'], ['#5a4a7a', '#1a1426'], ['#8a6a2a', '#2a1f0a'],
  ['#2e4f78', '#0d1726'], ['#6d2f52', '#1f0c18']];

function frame7() {
  const items = [['The Long Frost', '2025 · 1 h 52 min'], ['Golden Mile', '42 min left'], ['Paper Lanterns', '2023 · 2 h 4 min'],
    ['North Road', '2025 · 1 h 41 min'], ['Salt and Stone', '2022 · 1 h 55 min'], ['Low Tide', '2024 · 1 h 30 min']];
  let h = bar('Movies') + `<div class="tb"><span>Filters</span><span>Sort: Date added</span><span>Newest first</span>
    <span>List view</span></div><div class="tbline"></div>`;
  items.forEach(([t, l], k) => {
    h += `<div class="post${k === 2 ? ' on' : ''}" style="left:${52 + k * 308}px;top:204px"><div class="a" style="background:
      linear-gradient(155deg,${ART[k][0]},${ART[k][1]})"></div><div class="c"><b>${t}</b><small>${l}</small></div>
      ${k === 2 ? '' : '<div class="e"></div>'}</div>`;
  });
  return h + `<div class="toast">${svg(P.film, 30)}<span>The Last Crossing removed from your library</span>
    <span style="display:flex;gap:8px;margin-left:10px"><span class="u f">Understood</span><span class="u">Undo</span></span>
    <i class="bar6"><b></b></i></div>`
    + '<div class="cap2">7 · Remove from library: back where you came from, the poster gone, Undo</div>';
}

function details(extraHtml) {
  return bar('Movies') + hero(FILM, BTNS) + tabs(972, 'Details') + extraHtml;
}

function frame8() {
  const r = (label, sub, pill, tick, on, ic) => item(ic || P.file, `${label}${sub ? `<small>${sub}</small>` : ''}`,
    { on, nosep: true, right: `${pill ? `<span class="pill">${pill}</span>` : ''}<span style="width:28px;display:flex">
    ${tick ? svg(P.tick) : ''}</span>` });
  const panel = `<div class="side" style="width:560px"><div class="h">Built In</div>
    ${r('Off', '', '', false, false, P.off)}${r('English', '', '', true, true)}${r('English', '', 'SDH', false)}
    ${r('French', '', 'Forced', false)}<div class="h">Subtitle Files</div>
    ${r('English', '', '.srt', false)}<div class="h">Online</div>
    ${item(P.dl, 'Find subtitles online', { nosep: true, right: `<span class="ch">${svg(K.chev, 26)}</span>` })}</div>`;
  return details(panel) + '<div class="cap2">8 · Subtitles: Built In · Subtitle Files · Online; OK ticks</div>';
}

function frame8b() {
  const back = `<div class="side" style="width:560px;filter:brightness(.45);transform:scale(.96);transform-origin:top right">
    <div class="h">Built In</div>${item(P.off, 'Off', { nosep: true })}${item(P.file, 'English', { nosep: true })}
    <div class="h">Subtitle Files</div>${item(P.file, 'English', { nosep: true })}<div class="h">Online</div>
    ${item(P.dl, 'Find subtitles online', { nosep: true })}</div>`;
  const res = (name, sub, pills, on) => `<div class="i${on ? ' on' : ''}" style="height:78px"><span style="display:flex;
    flex-direction:column;gap:4px;min-width:0"><span style="white-space:nowrap;overflow:hidden;text-overflow:ellipsis;
    max-width:520px">${name}</span><small style="margin:0">${sub}</small></span><span class="fill"></span>
    ${pills.map(x => `<span class="pill">${x}</span>`).join('')}${on ? `<span style="display:flex">${svg(P.dl)}</span>` : '<span style="width:28px"></span>'}</div>`;
  const list = `<div class="side" style="width:860px;top:60px;right:120px;z-index:11;
    box-shadow:0 0 0 1.5px rgba(255,255,255,.7),0 24px 64px 16px rgba(0,0,0,.9)">
    ${item('<circle cx="12" cy="12" r="9"/><path d="M3 12h18M12 3a14 14 0 0 1 0 18M12 3a14 14 0 0 0 0 18"/>', 'Language', { nosep: true, right: '<span class="step" style="display:flex;gap:10px;font-weight:500">English</span>' })}
    <div class="h">Results</div>
    ${res('The.Last.Crossing.2025.2160p.WEB-DL', 'Matches your file · 12,400 downloads', ['Best match'], true)}
    ${res('The.Last.Crossing.2025.1080p.BluRay', '8,210 downloads', [])}
    ${res('The.Last.Crossing.2025.1080p.BluRay', '3,050 downloads', ['SDH'])}
    ${res('The Last Crossing (2025) English', '1,120 downloads', [])}
    ${res('The.Last.Crossing.2025.WEBRip', '640 downloads', ['Forced'])}</div>`;
  return details(back + list) + '<div class="cap2">8b · Find subtitles online: on top; OK downloads, then it plays with it ticked</div>';
}

function frame9() {
  let thumbs = '';
  const cols = [['#2c4656', '#0e1820'], ['#5a3a2a', '#1a0e08'], ['#2f4a3a', '#0e1a12'], ['#4a3a5a', '#160e1e'],
    ['#5a5a3a', '#1c1c0e'], ['#3a4a5a', '#0e141c'], ['#4a2f2f', '#180e0e'], ['#2f3f4a', '#0e1418']];
  cols.forEach((a, k) => {
    thumbs += `<div class="th${k === 2 ? ' on' : ''}" style="background:linear-gradient(155deg,${a[0]},${a[1]})">
      ${k === 0 ? '<span class="sug">' + svg(K.star || '<path d="M12 3l2.7 5.6 6.1.9-4.4 4.3 1 6.1L12 17l-5.4 2.9 1-6.1L3.2 9.5l6.1-.9z"/>', 20) + 'Suggested</span>' : ''}
      ${k === 1 ? `<span class="tk">${svg(P.tick, 22)}</span>` : ''}</div>`;
  });
  const panel = `<div class="side" style="padding-bottom:0">${item(J.art, 'For', { nosep: true,
    right: '<span class="chips"><b class="sel">Hero and rows</b><b>Library page</b></span>' })}
    <div style="height:1px;margin:6px 24px 0;background:linear-gradient(90deg,rgba(255,255,255,0),rgba(255,255,255,.2) 18%,rgba(255,255,255,.2) 82%,rgba(255,255,255,0))"></div>
    <div style="height:610px;overflow:hidden;position:relative;margin-right:44px"><div>
    <div class="thumbs" style="padding-top:22px">${thumbs}</div></div></div>
    <div style="position:absolute;right:8px;top:96px;color:#fff;opacity:.35">${svg('<path d="M6 15l6-6 6 6"/>', 30)}</div>
    <div style="position:absolute;right:8px;bottom:14px;color:#fff">${svg('<path d="M6 9l6 6 6-6"/>', 30)}</div></div>`;
  return details(panel) + '<div class="cap2">9 · Artwork: chooser stays put, pictures scroll under it; Suggested marked</div>';
}

function frame10() {
  return details('') + `<div class="blur"></div><div class="pop" style="width:1000px;padding:44px 52px;font-size:28px;
    line-height:44px">A ferryman who knows every current of the strait agrees to carry one last passenger across, and
    finds the whole town waiting on the other side. Over one long night on the water, the passenger's past and the
    ferryman's own come to the surface, and by morning neither of them can go back to the shore they left.</div>`
    + '<div class="cap2">10 · Full description: the text alone; Back closes</div>';
}

function frame11() {
  const sw = on => `<span class="sw${on ? ' on' : ''}"></span>`;
  return details('') + `<div class="scrim"></div><div class="pop dlg"><div class="ti">Delete The Last Crossing?</div>
    <div class="l1"><b>The.Last.Crossing.2025.2160p.mkv</b> (2160p · 18.7 GB) on <b>Media server</b>.</div>
    <div class="l2">Its own subtitle and .nfo files go with it; anything else in the folder stays.</div>
    <div class="h">Clean Up</div>
    ${item(P.hist, 'Also clear viewing history in Supernova', { nosep: true, right: sw(false) })}
    ${item(K.fixA, 'Also remove it from your Trakt history', { nosep: true, right: sw(false) })}
    ${item(P.folder, "Delete the folder if it's left empty", { nosep: true, right: sw(true) })}
    <div class="bt"><span class="f">Keep file</span><span>${svg(J.bin, 24)}Delete file</span></div></div>`
    + '<div class="cap2">11 · Delete file: the approved warning, Keep file focused</div>';
}

function qr() {
  let h = '<svg width="200" height="200" viewBox="0 0 29 29"><rect width="29" height="29" fill="#fff"/>';
  let seed = 7;
  for (let y = 0; y < 29; y++) {
    for (let x = 0; x < 29; x++) {
      seed = (seed * 9301 + 49297) % 233280;
      const finder = (x < 8 && y < 8) || (x > 20 && y < 8) || (x < 8 && y > 20);
      if (!finder && seed / 233280 > 0.55 && x > 0 && y > 0 && x < 28 && y < 28) {
        h += `<rect x="${x}" y="${y}" width="1" height="1"/>`;
      }
    }
  }
  [[1, 1], [21, 1], [1, 21]].forEach(([x, y]) => {
    h += `<rect x="${x}" y="${y}" width="7" height="7"/><rect x="${x + 1}" y="${y + 1}" width="5" height="5" fill="#fff"/>
      <rect x="${x + 2}" y="${y + 2}" width="3" height="3"/>`;
  });
  return h + '</svg>';
}

function frame12() {
  return details('') + `<div class="blur"></div><div class="pop" style="padding:36px;text-align:center">${qr()}</div>`
    + '<div class="cap2">12 · TMDB page QR code: the code alone; Back closes</div>';
}

const FRAMES = [['07-removed-undo', frame7], ['08-subtitles', frame8], ['08b-find-online', frame8b], ['09-artwork', frame9],
  ['10-full-description', frame10], ['11-delete-file', frame11], ['12-tmdb-page', frame12]];

(async () => {
  const browser = await chromium.launch();
  const pg = await browser.newPage({ viewport: { width: 1920, height: 1080 }, deviceScaleFactor: 1 });
  for (const [name, fn] of FRAMES) {
    const html = `<!doctype html><html><head><meta charset="utf-8"><style>${css}</style></head><body>
      <div class="screen">${fn()}<div class="border"></div></div>${speckle()}</body></html>`;
    await pg.setContent(html);
    await pg.evaluate(() => document.fonts.ready);
    await pg.screenshot({ path: path.join(__dirname, 'tv', `${name}.png`) });
  }
  await browser.close();
})();
