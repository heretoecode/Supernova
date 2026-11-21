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
const { css, svg, I, J, K, D, P, bar, hero, item, speckle, tabs, FILM } = m;
// scrolled to the tabs: focus is below, so the hero's buttons are at rest (no white box)
const BTNS = `<span class="btn">${svg(D.play, 24)}Resume · 42 min left</span><span class="btn">${svg(I.more, 26)}More</span>`;
const inner = m.m;

const css2 = `${css}
.hero .art{background:radial-gradient(ellipse 45% 120% at 68% 100%,rgba(180,205,225,.8),transparent 70%),
 radial-gradient(ellipse 30% 90% at 25% 100%,rgba(120,150,175,.5),transparent 70%),linear-gradient(160deg,#2c4656,#17303e)!important}
.barblur{position:absolute;left:0;right:0;top:0;height:130px;z-index:4;backdrop-filter:blur(18px) brightness(.7);
 -webkit-mask-image:linear-gradient(180deg,#000 0%,#000 55%,transparent 100%);
 mask-image:linear-gradient(180deg,#000 0%,#000 55%,transparent 100%)}
.yt{position:absolute;left:50%;top:50%;transform:translate(-50%,-50%);width:80px;height:56px;border-radius:14px;
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
  return inner.frame2().replace(/<div class="mini-logo">[^<]*<\/div>/, hero(FILM, BTNS, -680) + '<div class="barblur"></div>')
    .replace(/<div class="cap2">[\s\S]*?<\/div>/, '')
    + '<div class="cap2">16 · Scrolled to a tab: the hero runs on behind the top bar, blurred; never a solid band</div>';
}

function frame17() {
  const base = inner.frame6().replace(/<div class="mini-logo">[^<]*<\/div>/, hero(FILM, BTNS, -680) + '<div class="barblur"></div>').replace(/<div class="cap2">[\s\S]*?<\/div>/, '');
  const menu = `<div class="menu" style="left:390px;top:250px">
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
    + ytRow(52, 262, 'Trailers', [['Official Trailer', '2 min 31 s'], ['Teaser', '1 min 2 s'], ['Trailer 2', '2 min 10 s']], 0)
    + ytRow(52, 600, 'Behind the Scenes', [['Making The Last Crossing', '12 min'], ['The Ferry', '6 min'],
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
    + inner.row(52, 262, { icon: D.lib, title: 'In Your Library' }, [['Cold Iron', '2024 · 1 h 52 min', null],
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

function frame22() {
  const sw = on => `<span class="sw${on ? ' on' : ''}"></span>`;
  const base = inner.frame6().replace(/<div class="mini-logo">[^<]*<\/div>/, hero(FILM, BTNS, -680) + '<div class="barblur"></div>')
    .replace(/<div class="cap2">[\s\S]*?<\/div>/, '');
  return base + `<div class="scrim"></div><div class="pop dlg"><div class="ti">Delete The Long Tide?</div>
    <div class="l1"><b>Tidelands.S01E03.The.Long.Tide.1080p.mkv</b></div>
    <div class="l2" style="font-size:24px">S1 E3 · 1080p · 2.4 GB · Media server</div>
    <div class="h">Earlier Episodes</div>
    ${item(K.fixA, 'Also delete S1 E1 and S1 E2 (watched)', { nosep: true, right: sw(true) })}
    <div class="l2" style="margin:2px 0 0 52px">Deletes 3 files · 7.1 GB.</div>
    <div class="h">Clean Up</div>
    ${item(P.hist, 'Also clear viewing history in Supernova', { nosep: true, right: sw(false) })}
    ${item(P.folder, "Delete the folder if it's left empty", { nosep: true, right: sw(true) })}
    <div class="bt"><span class="f">Keep files</span><span>${svg(J.bin, 24)}Delete 3 files</span></div></div>`
    + '<div class="cap2">22 · Deleting an episode with earlier ones watched: one switch takes them too</div>';
}

function frame23() {
  const sw = on => `<span class="sw${on ? ' on' : ''}"></span>`;
  const base = inner.frame6().replace(/<div class="mini-logo">[^<]*<\/div>/, hero(FILM, BTNS, -680) + '<div class="barblur"></div>')
    .replace(/<div class="cap2">[\s\S]*?<\/div>/, '');
  const trakt = '<svg width="28" height="28" viewBox="0 0 24 24"><circle cx="12" cy="12" r="10" fill="none" stroke="currentColor" stroke-width="1.8"/><path d="M7 12.5l3 3 7-7" fill="none" stroke="currentColor" stroke-width="1.8"/></svg>';
  return base + `<div class="scrim"></div><div class="pop dlg"><div class="ti">Delete Harbour Lights?</div>
    <div class="l1"><b>Tidelands.S01E11.Harbour.Lights.1080p.mkv</b></div>
    <div class="l2" style="font-size:24px">S1 E11 · 1080p · 2.3 GB · Media server</div>
    <div class="h">Earlier Episodes</div>
    ${item(K.fixA, 'Also delete S1 E1 – E10 (all watched)', { nosep: true, right: sw(true) })}
    <div class="l2" style="margin:2px 0 0 52px">Deletes 11 files · 25.6 GB.</div>
    <div class="h">Clean Up</div>
    ${item(P.hist, 'Also clear viewing history in Supernova', { nosep: true, right: sw(false) })}
    <div class="i nosep" style="margin:0;padding:0 4px;height:62px"><span class="ic">${trakt}</span><span>Also remove them from your Trakt history</span><span class="fill"></span>${sw(false)}</div>
    ${item(P.folder, "Delete the folder if it's left empty", { nosep: true, right: sw(true) })}
    <div class="bt"><span class="f">Keep files</span><span>${svg(J.bin, 24)}Delete 11 files</span></div></div>`
    + '<div class="cap2">23 · Episode 11: earlier ones as a range, never a list; Trakt row when Trakt is connected</div>';
}

const FRAMES = [['16-tab-behind-bar', frame16], ['17-episode-long-press', frame17], ['18-extras-youtube', frame18],
  ['19-extras-player', frame19], ['20-library-long-press', frame20], ['21-delete-file-v2', frame21], ['22-delete-episode-earlier', frame22], ['23-delete-episode-11', frame23]];

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
