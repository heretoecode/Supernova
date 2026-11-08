// Proposal (owner, 7 Nov 2026): Search on the flat-page principles (plain charcoal, typing as a page with the global
// keyboard: field and keyboard left, results right). Run: FONT_DIR=<roboto woff2 dir> node render.js
const fs = require('fs');
const path = require('path');
const { chromium } = require('playwright');
const fontDir = process.env.FONT_DIR;
const font = w => `@font-face{font-family:R;font-weight:${w};src:url(data:font/woff2;base64,${fs.readFileSync(
  path.join(fontDir, `roboto-latin-${w}-normal.woff2`)).toString('base64')}) format('woff2')}`;
const icon = (d, s = 30) => `<svg width="${s}" height="${s}" viewBox="0 0 24 24" fill="none" stroke="currentColor"
  stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round">${d}</svg>`;
const I = {
  search: '<circle cx="11" cy="11" r="7"/><path d="M20 20l-4-4"/>',
  cog: '<circle cx="12" cy="12" r="3"/><path d="M12 2v3M12 19v3M4.2 4.2l2.1 2.1M17.7 17.7l2.1 2.1M2 12h3M19 12h3'
    + 'M4.2 19.8l2.1-2.1M17.7 6.3l2.1-2.1"/>',
  mic: '<rect x="9" y="3" width="6" height="11" rx="3"/><path d="M5 11a7 7 0 0 0 14 0M12 18v3"/>',
  back: '<path d="M9 7L4 12l5 5M4 12h16"/>',
  clock: '<circle cx="12" cy="12" r="9"/><path d="M12 7v5l3 2"/>',
  film: '<rect x="3" y="4" width="18" height="16" rx="2"/><path d="M7 4v16M17 4v16M3 9h4M3 15h4M17 9h4M17 15h4"/>',
  tv: '<rect x="3" y="5" width="18" height="12" rx="2"/><path d="M8 21h8M12 17v4"/>',
  person: '<circle cx="12" cy="8" r="4"/><path d="M4 21a8 8 0 0 1 16 0"/>',
  tag: '<path d="M3 12V4h8l10 10-8 8z"/><circle cx="7.5" cy="8" r="1.5"/>',
  cal: '<rect x="3" y="5" width="18" height="16" rx="2"/><path d="M3 10h18M8 3v4M16 3v4"/>',
};
const nav = `<div class="nav"><span class="brand">SUPERNOVA</span><span>Home</span><span>Movies</span><span>TV Shows</span>
  <span>Documentaries</span><span class="sp"></span><span>Files</span><span class="ico on">${icon(I.search, 34)}</span>
  <span class="ico">${icon(I.cog, 34)}</span><span class="clock">00:52</span></div>`;
const keys = [['1', '2', '3', '4', '5', '6', '7', '8', '9', '0'], ['q', 'w', 'e', 'r', 't', 'y', 'u', 'i', 'o', 'p'], ['a', 's', 'd', 'f', 'g', 'h', 'j', 'k', 'l', '-'],
  ['⇧', 'z', 'x', 'c', 'v', 'b', 'n', 'm', "'", '.'], ['#+=', 'mic', 'Space', '⌫', 'Clear']];
const kb = focus => `<div class="kb">${keys.map(r => `<div class="krow">${r.map(k => `<span class="key
  ${k.length > 1 ? 'wide' : ''} ${k === 'Space' ? 'space' : ''} ${k === focus ? 'focus' : ''}">${k === 'mic'
  ? icon(I.mic, 28) : k}</span>`).join('')}</div>`).join('')}</div>`;
const head = t => `<div class="hd">${t}</div>`;
const field = text => `<div class="pin">${icon(I.search, 32)}<span>${text || '<i class="ph">Films, series, people…</i>'}
  </span>${text ? '<i class="caret"></i>' : ''}</div>`;
const art = ['#5b3a2e', '#2e4a5b', '#4b5b2e', '#5b2e4a', '#2e5b4f', '#3a2e5b'];
const res = (i, name, line, pill, focus, round) => `<div class="res ${focus ? 'focus' : ''}"><span class="thumb${round ? ' round' : ''}"
  style="background:linear-gradient(160deg,${art[i % 6]},#121418)"></span><div class="rt"><div class="rn">${name}</div>
  <div class="rl">${line}</div></div>${pill ? `<span class="state">${pill}</span>` : ''}</div>`;
const sec = (t, n) => `<div class="sec">${t}${n ? ` <em>${n}</em>` : ''}</div>`;
const tip = (ic, t, l) => `<div class="tip">${icon(I[ic], 28)}<b>${t}</b><span>${l}</span></div>`;
const chips = on => `<div class="chips">${['All', 'Movies', 'TV Shows', 'People', 'On your services']
  .map(c => `<i class="${c === on ? 'on' : ''}">${c}</i>`).join('')}</div>`;
const page = (left, right) => `${nav}<div class="page"><div class="col left">${left}</div>
  <div class="col right">${right}</div></div>`;
const empty = page(`${field('')}${kb('m')}`, `${head('Recent searches')}
  ${['Marshals', 'Slow Horses', 'Denis Villeneuve', 'Christmas films'].map(t => `<div class="rec">${icon(I.clock, 28)}
  <span>${t}</span></div>`).join('')}
  ${head('What you can search')}
  ${tip('film', 'Titles', 'films and series, in your library and on your services')}
  ${tip('person', 'People', 'actors, directors and writers')}
  ${tip('tag', 'Genres', 'e.g. "comedy" or "crime drama"')}
  ${tip('cal', 'Years', 'e.g. "1994" or "1990s"')}`);
const b = (t, n) => `<b>${t.slice(0, n)}</b>${t.slice(n)}`;
const results = page(`${field('yellowstone')}${kb('e')}`, `${head('In your library')}
  ${res(0, b('Yellowstone', 11), '2018 · TV series · 5 seasons', 'In library', true)}
  ${head('More Yellowstone on your services')}
  ${res(1, '1883', '2021 · TV series · prequel', 'Paramount+', false)}
  ${res(2, '1923', '2022 · TV series · prequel', 'Paramount+', false)}
  ${res(3, 'Marshals', '2026 · TV series · spin-off', 'Paramount+', false)}`);
const person = page(`${field('taylor sheridan')}${kb('n')}`, `${head('In your library')}
  ${res(5, b('Taylor Sheridan', 15), 'Creator · 2 titles in your library: Yellowstone, Marshals', '', true, true)}
  ${head('More from Taylor Sheridan on your services')}
  ${res(1, '1883', '2021 · TV series', 'Paramount+', false)}
  ${res(4, 'Mayor of Kingstown', '2021 · TV series', 'Paramount+', false)}
  ${res(2, 'Sicario', '2015 · Film · writer', 'Netflix', false)}
  ${res(0, 'Hell or High Water', '2016 · Film · writer', 'Prime Video', false)}`);
const none = page(`${field('terminator')}${kb('r')}`, `<div class="empty"><div class="et">Nothing in your library
  matches "terminator"</div><div class="ed">Search shows what is in your library, and titles related to it on your
  services.</div></div>`);
const css = `${font(400)}${font(500)}*{box-sizing:border-box;margin:0}
body{width:1920px;height:1080px;overflow:hidden;font-family:R;color:#fff;background:#16181C;position:relative}
.nav{position:absolute;left:56px;right:56px;top:28px;height:56px;display:flex;align-items:center;gap:30px;font-size:34px;
color:#e8edf8}.nav span{display:inline-flex;align-items:center}.nav .brand{font-size:36px;margin-right:40px}
.nav .sp{flex:1}.nav .clock{margin-left:20px}
.nav .ico.on{background:#F4F6FB;color:#0B0D12;border-radius:50%;padding:8px;margin:-8px}
.page{position:absolute;left:96px;right:96px;top:150px;bottom:40px;display:grid;grid-template-columns:760px 1fr;gap:72px}
.col{min-width:0;overflow:hidden}
.pin{height:84px;border-radius:20px;background:#2a2d33;display:flex;align-items:center;gap:18px;padding:0 28px;
font-size:36px}.pin .ph{font-style:normal;color:#8a93a3}.caret{display:inline-block;width:3px;height:42px;
background:#fff;margin-left:-14px}
.kb{display:flex;flex-direction:column;gap:12px;margin-top:22px}.krow{display:flex;gap:10px}
.key{flex:1;height:64px;border-radius:16px;background:#2a2d33;display:flex;align-items:center;justify-content:center;
font-size:30px;font-weight:500}.key.wide{flex:1.6;font-size:24px}.key.space{flex:3}
.key.focus{background:#F4F6FB;color:#0B0D12}
.sec{font-size:26px;font-weight:500;color:#B7BFCC;margin:26px 0 10px}.sec em{font-style:normal;color:#7d8696;
margin-left:8px}.right .sec:first-child,.chips + .sec{margin-top:18px}
.rec{display:flex;align-items:center;gap:20px;height:74px;padding:0 24px;font-size:32px;color:#fff;
box-shadow:inset 0 -2px 0 rgba(255,255,255,.08)}.rec svg{color:#C9D3E6}
.hd{font-size:32px;font-weight:500;color:#fff;margin:30px 0 12px}.right .hd:first-child{margin-top:4px}
.empty{margin-top:120px;text-align:center}.et{font-size:36px;font-weight:500}.ed{font-size:27px;color:#C9D3E6;
margin:16px auto 0;max-width:780px;line-height:1.4}
.tip{display:flex;align-items:center;gap:18px;height:64px;padding:0 24px;font-size:28px;color:#C9D3E6}
.tip b{color:#fff;font-weight:500;min-width:120px}
.chips{display:flex;gap:8px;font-size:28px}.chips i{font-style:normal;padding:12px 22px;border-radius:16px;color:#dfe5f1}
.chips i.on{background:#454B55;color:#fff}
.res{display:flex;align-items:center;gap:24px;height:104px;padding:0 20px;border-radius:22px}
.res.focus{background:#F4F6FB;color:#0B0D12}
.thumb{position:relative;width:128px;height:72px;border-radius:10px;flex:none;
box-shadow:inset 0 0 0 2px rgba(255,255,255,.6),inset 0 0 12px 3px rgba(0,0,0,.45)}
.thumb.round{width:72px;height:72px;border-radius:50%;margin:0 28px}.rt{flex:1;min-width:0}.rn{font-size:32px;font-weight:400;white-space:nowrap;overflow:hidden;text-overflow:ellipsis}
.rn b{font-weight:700}.rl{font-size:24px;color:#C9D3E6;margin-top:4px}.res.focus .rl{color:#3A4256}
.state{white-space:nowrap;display:inline-flex;align-items:center;justify-content:center;width:180px;height:44px;
border-radius:22px;font-size:24px;font-weight:500;color:#e8edf6;box-shadow:inset 0 0 0 2px rgba(255,255,255,.30)}
.res.focus .state{color:#0B0D12;box-shadow:inset 0 0 0 2px rgba(11,13,18,.35)}`;
(async () => {
  const browser = await chromium.launch();
  const tab = await browser.newPage({ viewport: { width: 1920, height: 1080 } });
  for (const [f, body] of [['03-v2-start.png', empty], ['04-v2-title-family.png', results], ['05-v2-person.png', person], ['06-v2-not-in-library.png', none]]) {
    await tab.setContent(`<html><head><style>${css}</style></head><body>${body}</body></html>`, { waitUntil: 'load' });
    await tab.evaluate(() => document.fonts.ready);
    await tab.screenshot({ path: path.join(__dirname, f) });
  }
  await browser.close();
})();
