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
I.cloud = '<path d="M7 18h10a4 4 0 0 0 .6-8A6 6 0 0 0 6 9.5 4.3 4.3 0 0 0 7 18z"/>';
I.x = '<path d="M6 6l12 12M18 6L6 18"/>';
I.ext = '<path d="M14 4h6v6M20 4l-9 9"/><path d="M18 14v5a1 1 0 0 1-1 1H5a1 1 0 0 1-1-1V7a1 1 0 0 1 1-1h5"/>';
I.lib = '<rect x="3" y="4" width="4" height="16" rx="1"/><rect x="9" y="4" width="4" height="16" rx="1"/>'
  + '<path d="M15.5 5.2l3.8-1 3.2 15.4-3.8 1z"/>';
I.bulb = '<path d="M9 18h6M10 21h4M12 3a6 6 0 0 0-3.5 10.9c.6.5 1 1.2 1 2.1h5c0-.9.4-1.6 1-2.1A6 6 0 0 0 12 3z"/>';
I.play = '<circle cx="12" cy="12" r="9"/><path d="M10 8.5v7l5.5-3.5z"/>';
I.tick = '<circle cx="12" cy="12" r="9"/><path d="M8 12.5l2.8 2.8L16.5 9.5"/>';
I.ring = '<circle cx="12" cy="12" r="9"/>';

// v5 (12 Nov 2026, owner): bigger gap between sections, no line under a list's last row, no lines under tips;
// headings "Stream more from …"; library pills use the Details wording ("Resume S5 E3").
// v4 (12 Nov 2026, owner): no box on Clear history until focused; one services heading line; streaming pills use
// the Details open-in-app icon; section headings get icons; library pills show the title's state, not "In library".
// v3 (12 Nov 2026): final pass. Focus rule (white box = remote; search icon in the top bar keeps a bright underline
// while you are in the page); "From the world of …" / "Related to …" / "More from …" under an "On your services"
// label; person matches listed title by title; separators; Clear on Recent searches; streaming line in the tips.
const nav = `<div class="nav"><span class="brand">SUPERNOVA</span><span>Home</span><span>Movies</span><span>TV Shows</span>
  <span>Documentaries</span><span class="sp"></span><span>Files</span><span class="ico line">${icon(I.search, 34)}</span>
  <span class="ico">${icon(I.cog, 34)}</span><span class="clock">00:52</span></div>`;
const keys = [['1', '2', '3', '4', '5', '6', '7', '8', '9', '0'], ['q', 'w', 'e', 'r', 't', 'y', 'u', 'i', 'o', 'p'],
  ['a', 's', 'd', 'f', 'g', 'h', 'j', 'k', 'l', '-'], ['⇧', 'z', 'x', 'c', 'v', 'b', 'n', 'm', "'", '.'],
  ['#+=', 'mic', 'Space', '⌫', 'Clear']];
const kb = focus => `<div class="kb">${keys.map(r => `<div class="krow">${r.map(k => `<span class="key
  ${k.length > 1 ? 'wide' : ''} ${k === 'Space' ? 'space' : ''} ${k === focus ? 'focus' : ''}">${k === 'mic'
  ? icon(I.mic, 28) : k}</span>`).join('')}</div>`).join('')}</div>`;
const field = text => `<div class="pin">${icon(I.search, 32)}<span>${text || '<i class="ph">Films, series, people…</i>'}
  </span>${text ? '<i class="caret"></i>' : ''}</div>`;
const art = ['#5b3a2e', '#2e4a5b', '#4b5b2e', '#5b2e4a', '#2e5b4f', '#3a2e5b'];
const res = (i, name, line, pill, focus, round) => `<div class="res ${focus ? 'focus' : ''}"><span
  class="thumb${round ? ' round' : ''}" style="background:linear-gradient(160deg,${art[i % 6]},#121418)"></span>
  <div class="rt"><div class="rn">${name}</div><div class="rl">${line}</div></div>${pill
  ? `<span class="state">${icon(I[pill[0]], 24)}${pill[1]}</span>` : ''}</div>`;
const hd = (t, extra, ic) => `<div class="hd">${ic ? icon(I[ic], 30) : ''}${t}${extra || ''}</div>`;
const svc = t => `<div class="hd svch">${icon(I.cloud, 30)}${t}</div>`;
const tip = (ic, t, l) => `<div class="tip">${icon(I[ic], 28)}<b>${t}</b><span>${l}</span></div>`;
const page = (left, right) => `${nav}<div class="page"><div class="col left">${left}</div>
  <div class="col right">${right}</div></div>`;
const b = (t, n) => `<b>${t.slice(0, n)}</b>${t.slice(n)}`;
const start = page(`${field('')}${kb('m')}`, `${hd('Recent searches', `<span class="clr">${icon(I.x, 22)}Clear history</span>`, 'clock')}
  ${['Marshals', 'Slow Horses', 'Denis Villeneuve', 'Christmas films'].map(t => `<div class="rec">${icon(I.search, 28)}
  <span>${t}</span></div>`).join('')}
  ${hd('What you can search', '', 'bulb').replace('class="hd"', 'class="hd gap"')}
  ${tip('film', 'Titles', 'films and series in your library')}
  ${tip('person', 'People', 'actors, directors and writers')}
  ${tip('tag', 'Genres', 'e.g. "comedy" or "crime drama"')}
  ${tip('cal', 'Years', 'e.g. "1994" or "1990s"')}
  ${tip('cloud', 'Your services', 'titles you can stream, related to what you own')}`);
const family = page(`${field('yellowstone')}${kb('')}`, `${hd('In your library', '', 'lib')}
  ${res(0, b('Yellowstone', 11), '2018 · TV series · 5 seasons', ['play', 'Resume S5 E3'], true)}
  ${svc('Stream more from the world of Yellowstone')}
  ${res(1, '1883', '2021 · TV series · prequel', ['ext', 'Paramount+'], false)}
  ${res(2, '1923', '2022 · TV series · prequel', ['ext', 'Paramount+'], false)}
  ${res(3, 'Marshals', '2026 · TV series · spin-off', ['ext', 'Paramount+'], false)}`);
const person = page(`${field('taylor sheridan')}${kb('')}`, `${hd('In your library', '', 'lib')}
  ${res(0, 'Yellowstone', 'Taylor Sheridan · creator', ['play', 'Resume S5 E3'], false)}
  ${res(3, 'Marshals', 'Taylor Sheridan · creator', ['ring', 'Unwatched'], false)}
  ${svc('Stream more from Taylor Sheridan')}
  ${res(1, '1883', '2021 · TV series', ['ext', 'Paramount+'], true)}
  ${res(4, 'Mayor of Kingstown', '2021 · TV series', ['ext', 'Paramount+'], false)}
  ${res(2, 'Sicario', '2015 · Film · writer', ['ext', 'Netflix'], false)}`);
const factual = page(`${field('planet earth')}${kb('')}`, `${hd('In your library', '', 'lib')}
  ${res(4, b('Planet Earth', 12) + ' II', '2016 · Documentary series', ['tick', 'Watched'], true)}
  ${svc('Stream more related to Planet Earth II')}
  ${res(1, b('Planet Earth', 12) + ' III', '2023 · Documentary series', ['ext', 'BBC iPlayer'], false)}
  ${res(2, 'Frozen Planet II', '2022 · Documentary series', ['ext', 'BBC iPlayer'], false)}`);
const none = page(`${field('terminator')}${kb('r')}`, `<div class="empty"><div class="et">Nothing in your library
  matches "terminator"</div><div class="ed">Search looks in your library, and shows titles related to it on your
  services.</div></div>`);
const css = `${font(400)}${font(700)}${font(500)}*{box-sizing:border-box;margin:0}
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
.res.focus .state{color:#0B0D12;box-shadow:inset 0 0 0 2px rgba(11,13,18,.35)}.nav .ico.line{position:relative}.nav .ico.line:after{content:'';position:absolute;left:-4px;right:-4px;bottom:-12px;
height:4px;border-radius:2px;background:#fff}.nav span{color:#8f98a8}.nav .brand{color:#f1ead8}.nav .ico.line{color:#fff}
.nav .clock{color:#C9D3E6}
.hd{display:flex;align-items:center;font-size:32px;font-weight:500;color:#fff;margin:28px 0 10px}.right .hd:first-child{margin-top:4px}
.clr{margin-left:auto;display:inline-flex;align-items:center;gap:10px;height:52px;padding:0 20px;border-radius:14px;
background:#2a2d33;font-size:24px;font-weight:500}
.tip b{min-width:210px;white-space:nowrap}.tip span{white-space:nowrap}
.svc{display:flex;align-items:center;gap:10px;margin-top:30px;font-size:22px;font-weight:500;color:#C9D3E6;
letter-spacing:.5px}.svc + .hd{margin-top:6px}
.res{height:96px;box-shadow:inset 0 -2px 0 rgba(255,255,255,.08);border-radius:0}.res.focus{border-radius:22px;box-shadow:none}
.rec{height:70px}
.hd svg{color:#C9D3E6;margin-right:16px;flex:none}.svch{margin-top:34px}.svch em{font-style:normal;font-weight:400;
color:#C9D3E6;white-space:pre}
.clr{background:none;color:#C9D3E6;padding:0 8px}.clr svg{margin-right:0}
.rec:has(+ .hd),.res:has(+ .hd),.res:last-child{box-shadow:none}
.hd.gap,.svch{margin-top:64px}
.state{width:auto;min-width:236px;padding:0 20px;gap:10px}
.empty{margin-top:120px;text-align:center}.et{font-size:36px;font-weight:500}.ed{font-size:27px;color:#C9D3E6;
margin:16px auto 0;max-width:780px;line-height:1.4}`;
(async () => {
  const browser = await chromium.launch();
  const tab = await browser.newPage({ viewport: { width: 1920, height: 1080 } });
  for (const [f, body] of [['17-v5-start.png', start], ['18-v5-family.png', family], ['19-v5-person.png', person],
    ['20-v5-non-fiction.png', factual], ['21-v5-not-in-library.png', none]]) {
    await tab.setContent(`<html><head><style>${css}</style></head><body>${body}</body></html>`, { waitUntil: 'load' });
    await tab.evaluate(() => document.fonts.ready);
    await tab.screenshot({ path: path.join(__dirname, f) });
  }
  await browser.close();
})();
