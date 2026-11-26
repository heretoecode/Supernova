// Home v3, round 2 (owner, 21 Nov 2026): the hero's More menu and Customise Home with row ordering (no Move / Hide
// on Home). Cards 40 px apart, focus grows 8%; the first row heading 66 px under the hero.
// 6 More open beside the button (above it, the button being at the foot). 7 Customise Home: Shown on Home (in Home's
// order; a focused row shows Move · Hide, own rows also Edit), Hidden (A–Z, Show), Add a row. 8 Move: the row lifts,
// Up / Down moves it, Home behind follows live.
// Run: FONT_DIR=<dir with roboto-latin-*-normal.woff2> node render-2.js
const fs = require('fs');
const path = require('path');
let src = fs.readFileSync(path.join(__dirname, 'render.js'), 'utf8');
src = src.replace(/const FRAMES = [\s\S]*$/, '');
src = src.replace('cx += 424;', 'cx += 440;');
src = src.replace('const yy = f ? top - 11 : top;', 'const yy = f ? top - 9 : top;');
src = src.replace('const xx = f ? cx - 20 : cx;', 'const xx = f ? cx - 16 : cx;');
src = src.replace('.card.focus{width:440px;height:248px;', '.card.focus{width:432px;height:243px;');
const mod = new Function('require', '__dirname', `${src}
return { css, svg, I, bar, hero, FILM, row, CONT, ADDED, TREND, speckle, ARTS };`)(require, __dirname);
const { chromium } = require('playwright');
const { css, svg, I, bar, hero, FILM, row, CONT, ADDED, TREND, speckle } = mod;

const J = {
  details: '<circle cx="12" cy="12" r="9"/><path d="M12 11v6M12 7.5v.1"/>',
  resume: '<path d="M7 4l13 8-13 8z"/>',
  restart: '<path d="M4 12a8 8 0 1 0 2.3-5.7"/><path d="M4 4v4h4"/>',
  watched: '<circle cx="12" cy="12" r="9"/><path d="M8 12.5l2.8 2.8L16.5 9.5"/>',
  fix: '<circle cx="11" cy="11" r="7"/><path d="M20 20l-4-4M8 11l2 2 4-4"/>',
  remove: '<rect x="3" y="4" width="18" height="16" rx="2"/><path d="M8 12h8"/>',
  subs: '<rect x="3" y="5" width="18" height="14" rx="2"/><path d="M7 12h4M13 12h4M7 15.5h7"/>',
  art: '<rect x="3" y="5" width="18" height="14" rx="2"/><circle cx="9" cy="10" r="1.6"/><path d="M21 16l-5-5-9 8"/>',
  desc: '<path d="M5 6h14M5 10h14M5 14h14M5 18h9"/>',
  bin: '<path d="M4 7h16M10 11v6M14 11v6M6 7l1 13h10l1-13M9 7V4h6v3"/>',
  qr: '<rect x="3" y="3" width="7" height="7"/><rect x="14" y="3" width="7" height="7"/><rect x="3" y="14" width="7" '
    + 'height="7"/><path d="M14 14h3v3M21 14v7h-4M17 21v-3"/>',
  edit: '<path d="M4 20h4L19 9l-4-4L4 16z"/><path d="M14 6l4 4"/>',
  move: '<path d="M8 9l4-4 4 4M8 15l4 4 4-4"/>',
  hide: '<path d="M3 3l18 18M10.6 6.1A10 10 0 0 1 22 12s-1.2 2.2-3.3 4M6.3 6.6C3.7 8.3 2 12 2 12s4 7 10 7c1.7 0 '
    + '3.2-.5 4.5-1.2"/>',
  show: '<path d="M2 12s4-7 10-7 10 7 10 7-4 7-10 7S2 12 2 12z"/><circle cx="12" cy="12" r="3"/>',
  plus: '<circle cx="12" cy="12" r="9"/><path d="M12 8v8M8 12h8"/>',
  next: '<rect x="3" y="5" width="15" height="12" rx="2"/><path d="M8.5 21h6M21 3v6M18 6h6"/>'
    + '<path d="M9 8.5l4 2.5-4 2.5z"/>',
  watched2: '<path d="M12 8v4l3 2"/><path d="M3.1 11a9 9 0 1 1 .9 4.5M3 20v-5h5"/>',
  because: '<path d="M12 3l1.8 5.2L19 10l-5.2 1.8L12 17l-1.8-5.2L5 10l5.2-1.8z"/>',
  pop: '<path d="M12 20s-7-4.5-7-10a4 4 0 0 1 7-2.6A4 4 0 0 1 19 10c0 5.5-7 10-7 10z"/>',
  nature: '<path d="M5 19c0-8 6-14 15-14 0 9-6 15-14 15"/><path d="M5 19l8-8"/>',
  laugh: '<circle cx="12" cy="12" r="9"/><path d="M8 14a4.5 4.5 0 0 0 8 0M9 9.5h.1M15 9.5h.1"/>',
};

const extra = `${css}
.menu{position:absolute;border-radius:16px;background:rgba(22,24,28,.99);padding:12px 0;z-index:8;
 box-shadow:0 0 0 1.5px rgba(255,255,255,.7),0 18px 48px 12px rgba(0,0,0,.85);color:#fff}
.h{display:flex;align-items:center;gap:14px;font-size:28px;padding:12px 24px 10px;white-space:nowrap}
.h:before,.h:after{content:"";flex:1;height:1px;background:rgba(255,255,255,.3)}
.i{display:flex;align-items:center;gap:18px;padding:0 24px;font-size:26px;white-space:nowrap;margin:0 10px;
 border-radius:12px;height:60px;position:relative}
.i.on{background:#f4f6fb;color:#0b0d12}
.i small{font-size:20px;color:#c9d3e6}
.i.on small{color:#3a3f4a}
.i .ic{width:30px;display:flex;justify-content:center}
.i .fill{flex:1;min-width:40px}
.i .sep{position:absolute;left:14px;right:14px;bottom:0;height:1px;background:rgba(255,255,255,.1)}
.i.on .sep,.i.nosep .sep{display:none}
.rule{height:1px;background:rgba(255,255,255,.3);margin:10px 24px}
.ib{display:flex;gap:6px}
.ib span{height:46px;padding:0 18px;border-radius:20px;display:flex;align-items:center;gap:8px;font-size:22px}
.ib .f{background:#0b0d12;color:#fff}
.lift{display:flex;color:inherit}
.dim{opacity:.4}
.panel{position:absolute;right:52px;top:24px;width:700px;padding:14px 0 16px;z-index:9}
.panel .i{height:62px}
.pagedim{position:absolute;inset:0;background:rgba(0,0,0,.25);z-index:4}
.cust{position:absolute;left:72px;top:900px;height:52px;padding:0 24px;border-radius:20px;display:flex;
 align-items:center;gap:10px;font-size:24px;border:1.5px solid rgba(255,255,255,.7)}
.hint{position:absolute;left:72px;top:126px;font-size:22px;color:#c9d3e6}
`;

function item(ic, label, opts = {}) {
  const sub = opts.sub ? `<small>${opts.sub}</small>` : '';
  return `<div class="i${opts.on ? ' on' : ''}${opts.nosep ? ' nosep' : ''}${opts.dim ? ' dim' : ''}">
    <span class="ic">${svg(ic)}</span><span>${label}</span>${sub}<span class="fill"></span>${opts.right || ''}
    <i class="sep"></i></div>`;
}

function moreMenu() {
  return `<div class="menu" style="left:300px;top:112px">
    ${item(J.details, 'Details', { on: true, nosep: true })}
    <div class="h">Playback</div>
    ${item(J.resume, 'Resume · 42 min left', { nosep: true })}${item(J.restart, 'Play from the start', { nosep: true })}
    <div class="h">Library</div>
    ${item(J.watched, 'Mark as watched', { nosep: true })}${item(J.fix, 'Fix match', { nosep: true })}
    ${item(J.remove, 'Remove from library', { nosep: true })}
    <div class="h">Files &amp; Media</div>
    ${item(J.subs, 'Subtitles', { nosep: true })}${item(J.art, 'Artwork', { nosep: true })}
    ${item(J.bin, 'Delete file', { nosep: true })}
    <div class="rule"></div>${item(J.qr, 'TMDB page', { nosep: true })}</div>`;
}

const STARTED = { ...FILM, facts: '2025 · 2 h 11 min · Drama, Thriller, Mystery, Crime' };

function frame6() {
  return bar('Home') + hero(STARTED, `<span class="btn">${svg(I.more, 26)}More</span>`)
    + '<div class="dots"><i class="on"></i><i></i><i></i><i></i><i></i></div>'
    + row(72, 976, { icon: I.cont, title: 'Continue Watching' }, CONT, -1) + moreMenu()
    + '<div class="cap2">6 · More on the Home hero: Details first, then the Details menu</div>';
}

// Customise Home
const SHOWN = [[I.cont, 'Continue Watching'], [I.added, 'Recently Added'], [J.nature, 'Nature Nights', true],
  [J.next, 'Watch Next'], [I.trend, 'Trending on Trakt'], [J.laugh, 'Friday Comedy', true]];
const HIDDEN = [[J.because, 'Because You Watched'], [J.pop, 'Popular on Trakt'], [J.watched2, 'Recently Watched']];

function custom(focus, lifted) {
  let h = '<div class="menu panel"><div class="h">Shown on Home</div>';
  SHOWN.forEach(([ic, name, own], k) => {
    const on = k === focus;
    let right = '';
    if (on && lifted) {
      right = `<span class="lift">${svg(J.move, 28)}</span>`;
    } else if (on) {
      right = `<span class="ib"><span class="f">${svg(J.move, 22)}Move</span>${own ? `<span>${svg(J.edit, 22)}Edit
        </span>` : ''}<span>${svg(J.hide, 22)}Hide</span></span>`;
    } else if (own) {
      right = `<small>Your row</small>`;
    }
    h += item(ic, name, { on, right, nosep: k === SHOWN.length - 1, dim: lifted && !on });
  });
  h += '<div class="h">Hidden</div>';
  HIDDEN.forEach(([ic, name], k) => {
    h += item(ic, name, { nosep: k === HIDDEN.length - 1, dim: lifted });
  });
  h += `<div class="rule"></div>${item(J.plus, 'Add a row', { nosep: true, dim: lifted })}</div>`;
  return h;
}

const NAT = [['Deep Blue Planet', 'S1 E3 · 4 of 6 left', 0.4], ['Wild Isles of the North', '5 episodes', 'w'],
  ['Mountain Light', '2023 · 1 h 47 min', null], ['The Green Valley', '5 episodes', null],
  ['Frozen Frontier', '3 seasons', null]];

function homeBehind(order) {
  const rows = { 'Continue Watching': [I.cont, CONT], 'Recently Added': [I.added, ADDED],
    'Nature Nights': [J.nature, NAT] };
  let h = '';
  order.forEach((n, k) => {
    h += row(72, 120 + k * 330, { icon: rows[n][0], title: n }, rows[n][1], -1, { shift: k * 2 });
  });
  return h;
}

function frame7() {
  return bar('Home') + homeBehind(['Continue Watching', 'Recently Added', 'Nature Nights'])
    + '<div class="pagedim"></div>' + custom(2, false)
    + '<div class="cap2">7 · Customise Home: rows in Home\'s order</div>';
}

function frame8() {
  const moved = SHOWN.splice(2, 1)[0];
  SHOWN.splice(1, 0, moved);
  const out = bar('Home') + homeBehind(['Continue Watching', 'Nature Nights', 'Recently Added'])
    + '<div class="pagedim"></div>' + custom(1, true)
    + '<div class="cap2">8 · Move: Up / Down, Home follows; OK or Back puts it down</div>';
  return out;
}

const FRAMES = [['06-home-more-menu', frame6], ['07-customise-home', frame7], ['08-customise-home-move', frame8]];

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
