// Home v3, round 4 (owner, 21 Nov 2026): Exclude from hero. 14 the Home More menu with "Exclude from hero". 15 the Undo
// notice with where to manage it. 16 icon choices. 17 Files › Library Care › Excluded from hero (A–Z, search above 12,
// Include again on the focused row, Include all again). 18 the row icon picker opening under the Icon row.
// Run: FONT_DIR=<dir with roboto-latin-*-normal.woff2> node render-4.js
const fs = require('fs');
const path = require('path');
let src = fs.readFileSync(path.join(__dirname, 'render-3.js'), 'utf8');
src = src.slice(0, src.lastIndexOf('const FRAMES = ['));
src = src.replace("const { chromium } = require('playwright');", '');
const m = new Function('require', '__dirname', `${src}
return { css, svg, I, J, K, GEN, bar, hero, STARTED, row, CONT, item, homeBehind, speckle, rowSettings };`)(require,
  __dirname);
const { chromium } = require('playwright');
const { css, svg, I, J, K, GEN, bar, hero, STARTED, row, CONT, item, homeBehind, speckle, rowSettings } = m;

const X = {
  a: '<rect x="3" y="5" width="18" height="11" rx="2"/><path d="M8 20h8"/><circle cx="18" cy="17" r="4.2" '
    + 'fill="#16181C"/><path d="M16 17h4"/>',
  b: '<path d="M12 3l2.3 5.2 5.7.5-4.3 3.8 1.3 5.5L12 15.2 7 18l1.3-5.5L4 8.7l5.7-.5z"/><path d="M3 21L21 3"/>',
  c: '<rect x="2" y="7" width="20" height="10" rx="2"/><path d="M7 12h10"/>',
  search: '<circle cx="11" cy="11" r="7"/><path d="M20 20l-4-4"/>',
  up: '<path d="M6 15l6-6 6 6"/>',
  down: '<path d="M6 9l6 6 6-6"/>',
  back: '<path d="M4 12a8 8 0 1 0 2.3-5.7"/><path d="M4 4v4h4"/>',
  film: '<rect x="3" y="4" width="18" height="16" rx="2"/><path d="M7 4v16M17 4v16M3 9h4M3 15h4M17 9h4M17 15h4"/>',
};

const extra = `${css}
.toast{position:absolute;left:50%;bottom:44px;transform:translateX(-50%);display:flex;align-items:center;gap:22px;
 padding:20px 20px 30px 26px;border-radius:20px;background:rgba(22,24,28,.99);z-index:12;
 box-shadow:0 0 0 1.5px rgba(255,255,255,.7),0 18px 48px 12px rgba(0,0,0,.85);white-space:nowrap}
.toast .tx{display:flex;flex-direction:column;gap:4px;font-size:24px}
.toast .tx small{font-size:20px;color:#c9d3e6}
.toast .u{height:52px;padding:0 24px;border-radius:20px;display:flex;align-items:center;font-size:24px}
.toast .u.f{background:#f4f6fb;color:#0b0d12}
.toast .bar6{position:absolute;left:24px;right:24px;bottom:10px;height:3px;border-radius:2px;background:rgba(255,255,255,.18)}
.toast .bar6 b{position:absolute;left:0;top:0;bottom:0;width:70%;border-radius:2px;background:rgba(255,255,255,.7)}
.ftabs{position:absolute;left:52px;top:104px;display:flex;gap:8px;font-size:28px}
.ftabs span{height:52px;padding:0 22px;display:flex;align-items:center;gap:12px}
.ftabs .open{box-shadow:inset 0 -3px 0 #fff}
.fline{position:absolute;left:52px;right:52px;top:164px;height:1px;background:rgba(255,255,255,.18)}
.crumb{position:absolute;left:52px;top:212px;display:flex;align-items:center;gap:14px;font-size:32px;font-weight:500}
.crumb small{font-size:24px;color:#c9d3e6;font-weight:400}
.fact{position:absolute;right:52px;top:208px;display:flex;gap:8px;font-size:24px}
.fact span{height:52px;padding:0 22px;border-radius:20px;display:flex;align-items:center;gap:10px}
.list{position:absolute;left:52px;right:110px;top:290px}
.fr{height:96px;display:flex;align-items:center;gap:22px;padding:0 20px;border-radius:14px;position:relative}
.fr .th{width:44px;height:66px;border-radius:5px;box-shadow:inset 0 0 0 1px #fff}
.fr .t{display:flex;flex-direction:column;gap:4px;font-size:30px}
.fr .t small{font-size:22px;color:#c9d3e6}
.fr .ln{position:absolute;left:20px;right:20px;bottom:0;height:1px;background:linear-gradient(90deg,
 rgba(255,255,255,0),rgba(255,255,255,.2) 18%,rgba(255,255,255,.2) 82%,rgba(255,255,255,0))}
.fr.on{background:#f4f6fb;color:#0b0d12}
.fr.on .t small{color:#3a3f4a}
.fr.on .ln,.fr.last .ln{display:none}
.fr .fill{flex:1}
.fr .b{height:52px;padding:0 22px;border-radius:20px;display:flex;align-items:center;gap:10px;font-size:24px;
 background:#0b0d12;color:#fff}
.sarrow{position:absolute;right:56px;color:#fff}
.pick2{position:absolute;right:68px;top:204px;width:640px;padding:20px;z-index:12;display:grid;
 grid-template-columns:repeat(4,1fr);gap:6px;box-shadow:0 0 0 1.5px rgba(255,255,255,.7),0 24px 64px 16px rgba(0,0,0,.9)}
.pick2 .tile{height:108px;font-size:20px;gap:10px}
`;

function frame14() {
  const menu = `<div class="menu" style="left:300px;top:380px">
    ${item(J.details, 'Details', { nosep: true })}
    <div class="h">Playback</div>
    ${item(J.resume, 'Resume · 42 min left', { nosep: true })}${item(J.restart, 'Play from the start', { nosep: true })}
    <div class="h">This Title</div>
    ${item(J.watched, 'Mark as watched', { nosep: true })}${item(X.a, 'Exclude from hero', { nosep: true, on: true })}
    </div>`;
  return bar('Home') + hero(STARTED, `<span class="btn">${svg(I.more, 26)}More</span>`)
    + '<div class="dots"><i class="on"><b></b></i><i></i><i></i><i></i><i></i></div>'
    + row(72, 976, { icon: I.cont, title: 'Continue Watching' }, CONT, -1) + menu
    + '<div class="cap2">14 · Exclude from hero (icon A)</div>';
}

function frame15() {
  return bar('Home') + hero({ ...STARTED, logo: 'Cold<br>Harbour', age: '15',
    facts: '2022 · 3 seasons · Drama, Mystery, Crime',
    desc: 'A harbour master in a fading fishing town finds a stranger in the nets, and with him a secret the whole '
      + 'coast has kept for thirty years.' }, `<span class="btn f">${svg(I.more, 26)}More</span>`)
    + '<div class="dots"><i class="on"><b style="width:5%"></b></i><i></i><i></i><i></i></div>'
    + row(72, 976, { icon: I.cont, title: 'Continue Watching' }, CONT, -1)
    + `<div class="toast">${svg(X.film, 30)}<span class="tx">The Last Crossing excluded from hero<small>Manage in Files ›
      Library Care › Excluded from hero.</small></span><span style="display:flex;gap:8px;margin-left:10px">
      <span class="u f">Understood</span><span class="u">Undo</span></span><i class="bar6"><b></b></i></div>`
    + '<div class="cap2">15 · The Undo notice says where to manage it</div>';
}

function frame16() {
  const col = (letter, name, d) => `<div><div class="lbl">${letter} · ${name}</div><div class="big">${svg(d, 72)}
    </div><div class="mini">${item(d, 'Exclude from hero', { nosep: true })}
    ${item(d, 'Exclude from hero', { on: true, nosep: true })}</div></div>`;
  return `<div class="board"><div class="sec">Exclude from hero</div><div class="cols">
    ${col('A', 'The hero screen, minus', X.a)}${col('B', 'Struck-out star (featured)', X.b)}
    ${col('C', 'A banner, minus', X.c)}</div></div><div class="cap2">16 · Icon choices: Exclude from hero</div>`;
}

const EX = [['A Field of Kites', 'Movie · 2021 · excluded 2 Nov 2026'],
  ['Brightwater', 'TV show · 3 seasons · excluded 19 Nov 2026'],
  ['City of Birds', 'Movie · 2019 · excluded 14 Oct 2026'],
  ['Cold Open', 'Movie · 2023 · excluded 8 Nov 2026'],
  ['Desert Bloom', 'Movie · 2020 · excluded 30 Oct 2026'],
  ['Golden Mile', 'TV show · 1 season · excluded 12 Nov 2026'],
  ['Harbour Lights', 'Movie · 2017 · excluded 21 Nov 2026']];

function frame17() {
  const ARTS = m.ARTS || [['#7a3b2e', '#26120d'], ['#2f5c4a', '#0e1d17'], ['#5a4a7a', '#1a1426']];
  let list = '<div class="list">';
  list += `<div class="fr"><span style="display:flex;color:#c9d3e6">${svg(X.search, 30)}</span>
    <span class="t" style="color:#c9d3e6">Search excluded titles</span><i class="ln"></i></div>`;
  EX.forEach(([t, s], k) => {
    const a = ARTS[k % ARTS.length];
    const on = k === 2;
    list += `<div class="fr${on ? ' on' : ''}${k === EX.length - 1 ? ' last' : ''}"><span class="th" style="background:
      linear-gradient(155deg,${a[0]},${a[1]})"></span><span class="t">${t}<small>${s}</small></span>
      <span class="fill"></span>${on ? `<span class="b">${svg(X.back, 22)}Include again</span>` : ''}<i class="ln"></i></div>`;
  });
  list += '</div>';
  return bar('Files').replace('<span>Files</span>', '<span class="open">Files</span>')
    + `<div class="ftabs"><span>This Device</span><span>Connections</span><span class="open">Library Care</span>
    <span>Your Library</span></div><div class="fline"></div>
    <div class="crumb">${svg(X.a, 32)}Excluded from hero<small>46 titles · A–Z</small></div>
    <div class="fact"><span>${svg(X.back, 24)}Include all again</span></div>${list}
    <div class="sarrow" style="top:300px;opacity:.35">${svg(X.up, 34)}</div>
    <div class="sarrow" style="top:1000px">${svg(X.down, 34)}</div>`
    + '<div class="cap2">17 · Files › Library Care › Excluded from hero</div>';
}

function frame18() {
  let tiles = '';
  GEN.forEach(([n, d]) => {
    const on = n === 'Nature';
    tiles += `<div class="tile${on ? ' on' : ''}">${svg(d, 36)}<span>${n}</span>${on ? `<span class="tk">${svg(K.tick, 22)}
      </span>` : ''}</div>`;
  });
  return bar('Home') + homeBehind(['Continue Watching', 'Recently Added', 'Nature Nights'])
    + '<div class="pagedim"></div>' + rowSettings('icon', false) + `<div class="menu pick2">${tiles}</div>`
    + '<div class="cap2">18 · Icon: the picker opens under the Icon row</div>';
}

const FRAMES = [['14-exclude-from-hero', frame14], ['15-exclude-undo', frame15], ['16-exclude-icons', frame16],
  ['17-library-care-excluded', frame17], ['18-icon-picker-under', frame18]];

(async () => {
  const browser = await chromium.launch();
  const pg = await browser.newPage({ viewport: { width: 1920, height: 1080 }, deviceScaleFactor: 1 });
  for (const [name, fn] of FRAMES) {
    const html = `<!doctype html><html><head><meta charset="utf-8"><style>${extra}</style></head><body>
      <div class="screen">${fn()}<div class="border"></div></div>${speckle()}</body></html>`;
    await pg.setContent(html);
    await pg.evaluate(() => document.fonts.ready);
    await pg.screenshot({ path: path.join(__dirname, 'tv', `${name}.png`) });
  }
  await browser.close();
})();
