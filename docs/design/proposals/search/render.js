// Proposal (0.156, owner 4 Nov 2026): Search page — charcoal keyboard panel with a TV-friendly A–Z grid, voice key,
// grouped results with clean two-line rows and a "matched on" line. Run: FONT_DIR=<roboto woff2 dir> node render.js
const fs = require('fs'), path = require('path'), { chromium } = require('playwright');
const out = __dirname, fontDir = process.env.FONT_DIR;
const font = w => `@font-face{font-family:R;font-weight:${w};src:url(data:font/woff2;base64,${fs.readFileSync(path.join(fontDir, `roboto-latin-${w}-normal.woff2`)).toString('base64')}) format('woff2')}`;
const P = { search: '<circle cx="11" cy="11" r="6.5"/><path d="M16 16l4.5 4.5"/>', mic: '<rect x="9" y="3" width="6" height="11" rx="3"/><path d="M5.5 11a6.5 6.5 0 0 0 13 0M12 17.5V21"/>',
  del: '<path d="M9 5h11v14H9l-6-7z"/><path d="M12.5 9.5l5 5M17.5 9.5l-5 5"/>', space: '<path d="M4 10v4h16v-4"/>', clear: '<path d="M6 6l12 12M18 6L6 18"/>',
  clock: '<circle cx="12" cy="12" r="9"/><path d="M12 7v5l3 2"/>', film: '<rect x="4" y="3" width="16" height="18" rx="2"/><path d="M8 3v18M16 3v18M4 8h4M4 12h4M4 16h4M16 8h4M16 12h4M16 16h4"/>',
  tv: '<rect x="3" y="5" width="18" height="12" rx="2"/><path d="M8 21h8"/>', people: '<circle cx="9" cy="8" r="3.2"/><path d="M3 20a6 6 0 0 1 12 0"/><circle cx="17" cy="9" r="2.6"/><path d="M15.5 14.2A5 5 0 0 1 21 19"/>' };
const ic = (k, s = 28) => `<svg width="${s}" height="${s}" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round">${P[k]}</svg>`;
const nav = `<div class="nav"><span class="brand">SUPERNOVA</span><span>Home</span><span>Movies</span><span>TV Shows</span><span>Documentaries</span><span class="sp"></span><span>Network &amp; Files</span><span class="on">${ic('search', 34)}</span><span class="clock">13:27</span></div>`;
const LAYERS = {
  abc: [['1','2','3','4','5','6','7','8','9','0'], ['q','w','e','r','t','y','u','i','o','p'], ['a','s','d','f','g','h','j','k','l','/'], ['⇧','z','x','c','v','b','n','m','.','@']],
  sym: [['1','2','3','4','5','6','7','8','9','0'], ['!','#','$','%','&','*','(',')','-','_'], ['=','+','[',']','{','}','\\','|','~','`'], ['ABC',':',';',',','?','\'','"','<','>','^']],
};
const key = (l, focus, cls = '') => `<div class="key${cls}${focus === l ? ' f' : ''}">${l}</div>`;
const kb = (text, focus, layer = 'abc', shift = false, label = '') => `<div class="kpanel">${label ? `<div class="flabel">${label}</div>` : ''}<div class="field">${label ? '' : ic('search', 30)}<span class="${text ? 'typed' : 'ph'}">${text || 'Films, shows, episodes, people'}</span><i class="caret"></i></div>
  <div class="kb">
  ${LAYERS[layer].map((r, i) => `<div class="krow">${r.map(l => l === '⇧' ? `<div class="key shift${shift ? ' on' : ''}${focus === l ? ' f' : ''}">⇧<small>${shift ? 'ON' : ''}</small></div>`
      : l === 'ABC' ? `<div class="key shift${focus === l ? ' f' : ''}">ABC</div>` : key(shift && /[a-z]/.test(l) ? l.toUpperCase() : l, focus)).join('')}</div>`).join('')}
  <div class="krow"><div class="key mid${focus === 'sym' ? ' f' : ''}"><span>${layer === 'abc' ? '#+=' : 'ABC'}</span></div><div class="key mid${focus === 'Voice' ? ' f' : ''}">${ic('mic', 28)}</div><div class="key space${focus === 'Space' ? ' f' : ''}">${ic('space', 28)}<span>Space</span></div><div class="key mid${focus === 'Delete' ? ' f' : ''}">${ic('del', 30)}</div><div class="key mid${focus === 'Clear' ? ' f' : ''}"><span>Clear</span></div></div>
  </div><div class="khint">${layer === 'abc' ? '/ . @ always on the letters page · #+= for every other symbol · ⇧ for capitals' : 'Symbols page · ABC goes back to letters'}</div></div>`;
const empty = `<div class="right"><div class="sec">${ic('clock', 26)}Recent searches<span class="spc"></span><span class="mini">${ic('clear', 20)}Clear recent</span></div>
  <div class="chips">${['dune', 'mr robot', 'slow horses', 'kingdom', 'tulsa king'].map(c => `<span class="chip">${c}</span>`).join('')}</div>
  <div class="sec gap">What you can search</div>
  ${[['film', 'Films, TV shows and episodes', 'by title, original title or file name'], ['people', 'People and studios', 'cast, crew and studios in your library'], ['search', 'Forgiving', 'punctuation, “the” and small typos don’t matter']].map(([k, t, s]) => `<div class="tip">${ic(k, 30)}<div><b>${t}</b><span>${s}</span></div></div>`).join('')}</div>`;
const poster = (c, wide) => `<div class="po${wide ? ' w' : ''}" style="background:linear-gradient(160deg,${c},#0d1017 85%)"></div>`;
const res = (c, t, y, l2, why, focus, wide) => `<div class="r${focus ? ' f' : ''}">${poster(c, wide)}<div class="rt"><div class="t1"><b>${t}</b>${y ? `<span class="yr">${y}</span>` : ''}</div><div class="t2">${l2}</div>${why ? `<div class="why">${why}</div>` : ''}</div></div>`;
const results = `<div class="right"><div class="count">5 results for “dune” · in your library</div>
  <div class="sec">${ic('film', 26)}Films · 2</div>
  ${res('#c7a46a', 'Dune', '2021', 'Film · 155 min · 4K HDR · Science Fiction, Adventure', '', true)}
  ${res('#e3a33a', 'Dune: Part Two', '2024', 'Film · 165 min · 4K HDR · Science Fiction, Adventure', '')}
  <div class="sec">${ic('tv', 26)}Episodes · 1</div>
  ${res('#5f7a8c', 'Frontier · Dunes', 'S2 E4', 'Episode · 48 min · 1080p', 'Matched episode title', false, true)}
  <div class="sec">${ic('people', 26)}People · 2</div>
  ${res('#6b5a4a', 'Denis Villeneuve', '', 'Director · 2 titles in your library', 'Matched: directed Dune, Dune: Part Two', false, true)}</div>`;

const hl = (t, q) => t.replace(new RegExp('(' + q + ')', 'ig'), '<mark>$1</mark>');
const pill = w => `<span class="wp ${w === 1 ? 'done' : w > 0 ? 'part' : 'un'}">${w > 0 && w < 1 ? `<i style="width:${w * 100}%"></i>` : ''}<b>${w === 1 ? '✓ Watched' : w > 0 ? Math.round(w * 100) + '% watched' : 'Unwatched'}</b></span>`;
const filt = on => `<div class="filters">${['All', 'Films', 'TV shows', 'Episodes', 'People'].map(f => `<span class="fl${on === f ? ' on' : ''}">${f}</span>`).join('')}</div>`;
const r2 = (c, t, y, l2, w, focus, q, online) => `<div class="r r2${focus ? ' f' : ''}">${poster(c)}<div class="rt"><div class="t1"><b>${hl(t, q)}</b>${y ? `<span class="yr">${y}</span>` : ''}</div><div class="t2">${l2}</div></div><span class="sp2"></span>${online ? '<span class="add">Not in your library</span>' : pill(w)}</div>`;
const discover = (on) => `<div class="right"><div class="count">${on === 'All' ? '3' : '2'} results for “dune” · in your library</div>${filt(on)}
  <div class="sec">${ic('film', 26)}Films · 2</div>
  ${r2('#c7a46a', 'Dune', '2021', 'Film · 155 min · 4K HDR · Science Fiction, Adventure', 1, true, 'dune')}
  ${r2('#e3a33a', 'Dune: Part Two', '2024', 'Film · 165 min · 4K HDR · Science Fiction, Adventure', .4, false, 'dune')}
  ${on === 'All' ? `<div class="sec">${ic('tv', 26)}Episodes · 1</div>${r2('#5f7a8c', 'Frontier · Dunes', 'S2 E4', 'Episode · 48 min · 1080p', 0, false, 'dune')}` : ''}</div>`;
const browse = `<div class="right"><div class="sec">${ic('clock', 26)}Recent searches<span class="spc"></span><span class="mini">${ic('clear', 20)}Clear recent</span></div>
  <div class="chips">${['dune', 'mr robot', 'slow horses', 'kingdom', 'tulsa king'].map(c => `<span class="chip">${c}</span>`).join('')}</div>
  <div class="sec gap">What you can search</div><div class="tipline">Titles, original titles, episodes, cast, crew and studios — punctuation, “the” and small typos don’t matter</div></div>`;
const shots = [
  ['05-discover-all.png', kb('dune', '') + discover('All'), 'All: library results only, grouped, with watched state and the typed letters in bold'],
  ['06-filter-films.png', kb('dune', '') + discover('Films'), 'Up from the results reaches the filters: Films shows only the films in your library'],
  ['07-browse-before-typing.png', kb('', 't') + browse, 'Before typing: recent searches (with Clear recent) and one line on what you can search; no Browse shortcuts'],
  ['03-keyboard-letters.png', kb('webdav.put.io/TV', '/', 'abc', false, 'Server address') + `<div class="ctx">The same keyboard is used everywhere — here, adding a network share</div>`, 'Letters page: number row, QWERTY, plus / . @ on the page; #+= opens all other symbols; Shift for capitals'],
  ['04-keyboard-symbols.png', kb('MyP@ss_2026!', '!', 'sym', false, 'Password') + `<div class="ctx">Symbols page: everything a password, URL or file path might need</div>`, 'Symbols page: ! # $ % & * ( ) - _ = + [ ] { } \\ | ~ ` : ; , ? \' " < > ^ — ABC returns to letters'],
  ['01-empty.png', kb('', 't') + empty, 'Before typing: one panel — keyboard on the left, recent searches (with Clear recent) and what you can search on the right'],
  ['02-results.png', kb('dune', '') + results, 'Typing: results grouped (Films · Episodes · People), two clean lines each, and why it matched when it isn’t the title'],
];
const css = `${font(400)}${font(500)}${font(700)}*{box-sizing:border-box;margin:0}
body{width:1920px;height:1080px;overflow:hidden;font-family:R;color:#fff;position:relative;background:radial-gradient(ellipse 70% 60% at 70% 75%,#1d4fd8 0%,rgba(29,79,216,0) 70%),linear-gradient(180deg,#05081a 0%,#0a1650 45%,#10248a 100%)}
.nav{position:absolute;left:56px;right:56px;top:28px;height:56px;display:flex;align-items:center;gap:30px;font-size:34px;color:#e8edf8}.nav .brand{font-size:36px;margin-right:40px;letter-spacing:1px}.nav .sp{flex:1}.nav .on{display:flex}.nav .clock{margin-left:10px}
.sheet{position:absolute;left:56px;right:56px;top:112px;bottom:40px;border-radius:26px;background:rgba(22,24,28,.97);border:2px solid rgba(255,255,255,.22)}
.divider{position:absolute;left:832px;top:140px;bottom:68px;width:1px;background:rgba(255,255,255,.14)}
.kpanel{position:absolute;left:58px;top:114px;width:760px;bottom:42px;padding:24px}
.flabel{font-size:20px;color:#b7bfcc;margin:0 4px 10px}.ctx{position:absolute;left:846px;top:140px;right:56px;font-size:26px;color:#c9d3e6}
.field{height:76px;border-radius:22px;background:#2a2d33;display:flex;align-items:center;gap:14px;padding:0 22px;font-size:30px}.ph{color:#8f98a8}.typed{font-weight:500}.caret{width:3px;height:36px;background:#fff;margin-left:-8px}
.kb{margin-top:24px;display:flex;flex-direction:column;gap:10px}
.krow{display:flex;gap:8px}
.key{width:62px;position:relative;height:74px;flex:none;border-radius:16px;background:#2a2d33;display:flex;align-items:center;justify-content:center;gap:8px;font-size:30px;font-weight:500}
.key.f{background:#F4F6FB;color:#0B0D12}.key.shift{width:62px;font-size:24px}.key.shift small{position:absolute;bottom:6px;font-size:12px}.key.shift.on{box-shadow:inset 0 0 0 2px #5aa9ff}.key.mid{width:112px;font-size:22px}.key.space{flex:1;width:auto;font-size:21px}
.khint{position:absolute;left:26px;right:26px;bottom:22px;font-size:18px;color:#8f98a8}
.right{position:absolute;left:846px;right:58px;top:114px;bottom:42px;padding:24px 30px;overflow:hidden}
.spc{flex:1}.mini{display:inline-flex;align-items:center;gap:8px;height:48px;padding:0 18px;border-radius:16px;background:#2a2d33;color:#fff;font-size:20px;font-weight:500}
.sec{display:flex;align-items:center;gap:12px;font-size:22px;font-weight:500;color:#b7bfcc;margin:6px 4px 14px}.sec.gap{margin-top:40px}
.chips{display:flex;flex-wrap:wrap;gap:14px}.chip{height:56px;padding:0 24px;border-radius:20px;background:#2a2d33;display:flex;align-items:center;font-size:24px;font-weight:500}
.tip{display:flex;align-items:center;gap:20px;padding:16px 8px;border-bottom:1px solid rgba(255,255,255,.1)}.tip b{display:block;font-size:26px;font-weight:500}.tip span{display:block;font-size:21px;color:#c9d3e6;margin-top:4px}
.count{position:absolute;right:30px;top:30px;font-size:20px;color:#c9d3e6}
.r{display:flex;align-items:center;gap:22px;height:120px;padding:0 16px;border-radius:22px;margin-bottom:6px}.r.f{box-shadow:inset 0 0 0 3px #fff;background:rgba(255,255,255,.06)}
.po{width:66px;height:98px;border-radius:10px;flex:none}.po.w{width:150px;height:84px}
.rt{min-width:0}.t1{display:flex;align-items:baseline;gap:14px;font-size:30px}.t1 b{font-weight:500}.yr{font-size:22px;color:#c9d3e6}
.t2{font-size:21px;color:#c9d3e6;margin-top:6px;white-space:nowrap}.why{font-size:19px;color:#8fc3ff;margin-top:4px}
.filters{display:flex;gap:12px;margin:0 0 18px}.fl{height:54px;padding:0 22px;border-radius:18px;background:#2a2d33;display:flex;align-items:center;font-size:22px;font-weight:500}.fl.on{background:#F4F6FB;color:#0B0D12}
.r2{height:110px}.sp2{flex:1}mark{background:none;color:#fff;font-weight:700;text-decoration:underline;text-decoration-thickness:3px;text-underline-offset:6px}
.wp{position:relative;overflow:hidden;display:inline-flex;align-items:center;height:40px;padding:0 16px;border-radius:20px;font-size:19px;font-weight:500;white-space:nowrap;flex:none}
.wp b{position:relative;font-weight:500}.wp.done{background:#F4F6FB;color:#0B0D12}.wp.part{background:rgba(90,169,255,.16);box-shadow:inset 0 0 0 1.5px rgba(90,169,255,.7)}.wp.part i{position:absolute;left:0;top:0;bottom:0;background:rgba(90,169,255,.55)}
.wp.un{color:#9aa3b3;box-shadow:inset 0 0 0 1.5px rgba(255,255,255,.22)}.wp.un b{font-weight:400}
.add{font-size:19px;color:#aab3c4;flex:none;height:40px;display:inline-flex;align-items:center;padding:0 16px;border-radius:20px;box-shadow:inset 0 0 0 1.5px rgba(255,255,255,.22)}
.tiles{display:grid;grid-template-columns:1fr 1fr;gap:14px}.tile{height:104px;border-radius:20px;background:#2a2d33;padding:18px 22px;display:flex;flex-direction:column;justify-content:center}.tile b{font-size:26px;font-weight:500}.tile span{font-size:19px;color:#c9d3e6;margin-top:6px}
.tipline{font-size:21px;color:#c9d3e6;line-height:1.45;margin:0 4px}
.label{position:absolute;right:56px;bottom:8px;font-size:18px;color:#c9d3e6}`;
(async () => { const b = await chromium.launch(); const t = await b.newPage({ viewport: { width: 1920, height: 1080 } });
  for (const [f, body, label] of shots) {
    await t.setContent(`<html><head><style>${css}</style></head><body>${nav}<div class="sheet"></div><div class="divider"></div>${body}<div class="label">${label}</div></body></html>`, { waitUntil: 'load' });
    await t.evaluate(() => document.fonts.ready);
    const issues = await t.evaluate(() => { const o = [], r = e => e.getBoundingClientRect();
      const k = document.querySelector('.kpanel'), sh = document.querySelector('.sheet'), g = document.querySelector('.kb'), h = document.querySelector('.khint');
      if (r(g).bottom > r(h).top) o.push('grid hits hint'); if (r(g).bottom > r(k).bottom) o.push('grid overflows panel');
      const rp = document.querySelector('.right'); document.querySelectorAll('.right > *').forEach(e => { if (r(e).bottom > r(rp).bottom - 10) o.push('right overflow: ' + e.className); });
      document.querySelectorAll('.t2,.tip span').forEach(e => { if (r(e).right > r(rp).right - 20) o.push('line too long: ' + e.textContent); });
      document.querySelectorAll('.krow').forEach(w => { if (r(w.lastElementChild).right > r(k).right - 24) o.push('key row overflows panel'); });
      return o; });
    if (issues.length) console.log(f, issues);
    await t.screenshot({ path: path.join(out, f) }); }
  await b.close(); })();
