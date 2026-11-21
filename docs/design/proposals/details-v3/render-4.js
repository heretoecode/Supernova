// Details v3, round 3 (owner, 21 Nov 2026): 16 a tab page scrolled: the hero runs on behind the top bar, blurred and
// darkened, the bar's words on top (never a solid band). 17 long-press on an episode. 18 Extras with the YouTube mark
// on each card. 19 the Extras player. 20 long-press on an In Your Library card. 21 Delete file reworded (no word ever
// wraps alone).
// Run: FONT_DIR=<dir with roboto-latin-*-normal.woff2> node render-4.js
const fs = require('fs');
const path = require('path');
let src = fs.readFileSync(path.join(__dirname, 'render-2.js'), 'utf8');
src = src.slice(0, src.lastIndexOf("const FRAMES = [['"));
src = src.replace("const { chromium } = require('playwright');", '');
src = src.replace("return { extra, svg, I, J, K, D, bar, hero, item, speckle, tabs };",
  "return { extra, svg, I, J, K, D, bar, hero, item, speckle, tabs, row, frame2, frame6, people, kv };");
const m = new Function('require', '__dirname', `${src}
return { css, svg, I, J, K, D, P, bar, hero, item, speckle, tabs, details, FILM, BTNS, m };`)(require, __dirname);
const { chromium } = require('playwright');
const { css, svg, I, J, K, D, P, bar, hero, item, speckle, tabs, FILM, BTNS } = m;
const inner = m.m;

const css2 = `${css}
.barblur{position:absolute;left:0;right:0;top:0;height:96px;z-index:4;backdrop-filter:blur(16px);
 background:linear-gradient(180deg,rgba(17,18,21,.78),rgba(17,18,21,.55) 80%,rgba(17,18,21,0))}
.yt{position:absolute;left:50%;top:32%;transform:translate(-50%,-50%);width:76px;height:54px;border-radius:14px;
 background:#ff0033;display:flex;align-items:center;justify-content:center;z-index:2}
.player{position:absolute;left:96px;top:54px;width:1728px;height:972px;border-radius:16px;overflow:hidden;z-index:10;
 background:radial-gradient(ellipse 50% 60% at 60% 45%,rgba(160,190,210,.45),transparent 70%),
 linear-gradient(160deg,#25404f,#0b141a);box-shadow:0 0 0 1.5px rgba(255,255,255,.7),0 18px 48px 12px rgba(0,0,0,.85)}
.player .seek{position:absolute;left:40px;right:40px;bottom:36px;height:4px;border-radius:2px;background:rgba(255,255,255,.25)}
.player .seek b{position:absolute;left:0;top:0;bottom:0;width:22%;background:#fff;border-radius:2px}
`;

function tabPage() {
  return bar('Movies') + hero(FILM, BTNS, -680) + '<div class="barblur"></div>' + tabs(170, 'Details');
}

function frame16() {
  // reuse the Details tab body from tv/02 by rendering its columns again
  return inner.frame2().replace('<div class="mini-logo"></div>', hero(FILM, BTNS, -680) + '<div class="barblur"></div>')
    .replace(/<div class="cap2">[\s\S]*?<\/div>/, '')
    + '<div class="cap2">16 · Scrolled to a tab: the hero runs on behind the top bar, blurred; never a solid band</div>';
}

function frame17() {
  const base = inner.frame6().replace('<div class="mini-logo"></div>', hero(FILM, BTNS, -680) + '<div class="barblur"></div>').replace(/<div class="cap2">[\s\S]*?<\/div>/, '');
  const menu = `<div class="menu" style="left:1392px;top:250px">
    <div class="h">Playback</div>${item(J.resume, 'Resume · 31 min left', { nosep: true, on: true })}
    ${item(J.restart, 'Play from the start', { nosep: true })}
    <div class="h">Library</div>${item(J.watched, 'Mark as watched', { nosep: true })}
    ${item(K.fixA, 'Mark earlier episodes as watched', { nosep: true })}
    <div class="h">Files &amp; Media</div>${item(J.subs, 'Subtitles', { nosep: true })}
    ${item(J.bin, 'Delete episode file', { nosep: true })}</div>`;
  return base + menu + '<div class="cap2">17 · Long-press an episode: beside the card (OK plays it)</div>';
}

function ytRow(x, y, title, cards, focus) {
  let h = `<div class="rowh" style="left:${x}px;top:${y}px">${svg(D.trailer, 32)}${title}</div>`;
  const A = [['#7a3b2e', '#26120d'], ['#2f5c4a', '#0e1d17'], ['#5a4a7a', '#1a1426'], ['#8a6a2a', '#2a1f0a']];
  cards.forEach((c, k) => {
    const f = k === focus;
    h += `<div class="card${f ? ' focus' : ''}" style="left:${x + k * 440 - (f ? 16 : 0)}px;top:${y + 70 - (f ? 9 : 0)}px">
      <div class="art" style="background:linear-gradient(155deg,${A[k][0]},${A[k][1]})"></div>
      <div class="yt"><svg width="26" height="26" viewBox="0 0 24 24"><path d="M8 5l12 7-12 7z" fill="#fff"/></svg></div>
      <div class="fade"></div><div class="ct"><div class="c1">${c[0]}</div><div class="c2">${c[1]}</div></div>
      <div class="cedge"></div></div>`;
  });
  return h;
}

function frame18() {
  return bar('Movies') + hero(FILM, BTNS, -680) + '<div class="barblur"></div>' + tabs(170, 'Extras')
    + ytRow(72, 262, 'Trailers', [['Official Trailer', '2 min 31 s'], ['Teaser', '1 min 2 s'], ['Trailer 2', '2 min 10 s']], 0)
    + ytRow(72, 600, 'Behind the Scenes', [['Making The Last Crossing', '12 min'], ['The Ferry', '6 min'],
      ['Scoring the Strait', '8 min']], -1)
    + '<div class="cap2">18 · Extras: the YouTube mark on each card, as services carry theirs</div>';
}

function frame19() {
  return bar('Movies') + hero(FILM, BTNS, -680) + '<div class="barblur"></div>' + tabs(170, 'Extras')
    + '<div class="blur"></div><div class="player"><div class="seek"><b></b></div></div>'
    + '<div class="cap2">19 · Extras player: nearly full screen, page blurred behind, no title or controls; Back closes</div>';
}

function frame20() {
  const base = bar('Movies') + hero(FILM, BTNS, -680) + '<div class="barblur"></div>' + tabs(170, 'More Like This')
    + inner.row(72, 262, { icon: D.lib, title: 'In Your Library' }, [['Cold Iron', '2024 · 1 h 52 min', null],
      ['North Road', '42 min left', 0.6], ['Quiet Harbour', '2024 · 1 h 38 min', null],
      ['Salt and Stone', '2022 · 1 h 55 min', null], ['Low Tide', '2024 · 1 h 30 min', null]], 1);
  const menu = `<div class="menu" style="left:948px;top:250px">
    <div class="h">Playback</div>${item(J.resume, 'Resume · 42 min left', { nosep: true, on: true })}
    ${item(J.restart, 'Play from the start', { nosep: true })}
    <div class="h">Library</div>${item('<path d="M5 4h14v17l-7-4-7 4z"/>', 'Add to Watch Next', { nosep: true })}
    ${item(J.watched, 'Mark as watched', { nosep: true })}</div>`;
  return base + menu + '<div class="cap2">20 · Long-press a library title on More Like This (OK opens its Details)</div>';
}

function frame21() {
  const sw = on => `<span class="sw${on ? ' on' : ''}"></span>`;
  return bar('Movies') + hero(FILM, BTNS) + tabs(972, 'Details')
    + `<div class="scrim"></div><div class="pop dlg"><div class="ti">Delete The Last Crossing?</div>
    <div class="l1"><b>The.Last.Crossing.2025.2160p.mkv</b></div>
    <div class="l2" style="font-size:24px">2160p · HDR10 · Dolby Atmos · 18.7 GB · Media server</div>
    <div class="l2">Its own subtitle and .nfo files go with it; anything else in the folder stays.</div>
    <div class="h">Clean Up</div>
    ${item(P.hist, 'Also clear viewing history in Supernova', { nosep: true, right: sw(false) })}
    ${item(K.fixA, 'Also remove it from your Trakt history', { nosep: true, right: sw(false) })}
    ${item(P.folder, "Delete the folder if it's left empty", { nosep: true, right: sw(true) })}
    <div class="bt"><span class="f">Keep file</span><span>${svg(J.bin, 24)}Delete file</span></div></div>`
    + '<div class="cap2">21 · Delete file: the file on its own line, its facts under it; no word wraps alone</div>';
}

const FRAMES = [['16-tab-behind-bar', frame16], ['17-episode-long-press', frame17], ['18-extras-youtube', frame18],
  ['19-extras-player', frame19], ['20-library-long-press', frame20], ['21-delete-file-v2', frame21]];

(async () => {
  const browser = await chromium.launch();
  const pg = await browser.newPage({ viewport: { width: 1920, height: 1080 }, deviceScaleFactor: 1 });
  for (const [name, fn] of FRAMES) {
    const html = `<!doctype html><html><head><meta charset="utf-8"><style>${css2}</style></head><body>
      <div class="screen">${fn()}<div class="border"></div></div>${speckle()}</body></html>`;
    await pg.setContent(html);
    await pg.evaluate(() => document.fonts.ready);
    await pg.screenshot({ path: path.join(__dirname, 'tv', `${name}.png`) });
  }
  await browser.close();
})();
