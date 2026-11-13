// v4 (owner, 13 Nov 2026): a keyboard opened from a menu or side panel sits in a panel of its own with the same
// background as the menu; on a full page it stays on the page.
// v3 (owner, 13 Nov 2026): Country with focus on the search row, then with the shared keyboard open to the left of
// the panel (never covering it) and the list filtered live by what is typed.
// v2: each list as wide as its longest item (in the whole list) plus the standard padding, as the menu rule says;
// search row only on long lists (countries, languages), not on Your services.
// The shared choice list (owner, 13 Nov 2026), built from docs/design/RULES.md: one design for every long list.
// Left: Country (one choice: OK ticks and closes) with Search, Suggested and All. Right: Your services (several:
// ticked items gather under Selected). Group headings centred on their line, Title Case. Light frame = picture edge.
// Run: FONT_DIR=<roboto woff2 dir> node render.js
const fs = require('fs');
const path = require('path');
const { chromium } = require('playwright');
const fontDir = process.env.FONT_DIR;
const RES = path.join(__dirname, '../../../../res/drawable');
const font = w => `@font-face{font-family:R;font-weight:${w};src:url(data:font/woff2;base64,${fs.readFileSync(
  path.join(fontDir, `roboto-latin-${w}-normal.woff2`)).toString('base64')}) format('woff2')}`;
const svg = (d, s = 30) => `<svg width="${s}" height="${s}" viewBox="0 0 24 24" fill="none" stroke="currentColor"
  stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round">${d}</svg>`;
function logo(name, colour) {
  const xml = fs.readFileSync(path.join(RES, `provider_${name}.xml`), 'utf8');
  const vw = xml.match(/viewportWidth="([\d.]+)"/)[1];
  const vh = xml.match(/viewportHeight="([\d.]+)"/)[1];
  const paths = [...xml.matchAll(/pathData="([^"]+)"/g)].map(m => `<path d="${m[1]}" fill="${colour}"/>`).join('');
  return `<svg viewBox="0 0 ${vw} ${vh}" preserveAspectRatio="xMidYMid meet">${paths}</svg>`;
}
const I = {
  search: '<circle cx="11" cy="11" r="7"/><path d="M20 20l-4-4"/>',
  tick: '<path d="M5 12.5l4.5 4.5L19 7.5"/>',
  mic: '<rect x="9" y="3" width="6" height="11" rx="3"/><path d="M5 11a7 7 0 0 0 14 0M12 18v3"/>',
};
// Simple stand-in flags (made-up rendering, not official artwork).
const stripes = (dir, cols) => `<span class="flag" style="background:linear-gradient(${dir},${cols.map((c, i) =>
  `${c} ${i * 100 / cols.length}% ${(i + 1) * 100 / cols.length}%`).join(',')})"></span>`;
const uk = '<span class="flag"><svg viewBox="0 0 60 30" width="48" height="32" preserveAspectRatio="none"><rect width="60"'
  + ' height="30" fill="#012169"/><path d="M0 0L60 30M60 0L0 30" stroke="#fff" stroke-width="6"/><path d="M0 0L60 30M60 0'
  + 'L0 30" stroke="#C8102E" stroke-width="2"/><path d="M30 0v30M0 15h60" stroke="#fff" stroke-width="10"/><path d="M30 0'
  + 'v30M0 15h60" stroke="#C8102E" stroke-width="6"/></svg></span>';
const FLAGS = {
  uk,
  af: stripes('90deg', ['#000', '#B9131E', '#0B7B3E']),
  al: stripes('90deg', ['#E41E20', '#E41E20']),
  dz: stripes('90deg', ['#006233', '#fff']),
  ad: stripes('90deg', ['#10069F', '#FEDF00', '#D50032']),
  ar: stripes('180deg', ['#74ACDF', '#fff', '#74ACDF']),
  au: stripes('90deg', ['#00008B', '#00008B']),
  at: stripes('180deg', ['#ED2939', '#fff', '#ED2939']),
};
const row = (lead, name, line, o) => `<div class="row ${o.focus ? 'focus' : ''}"><span class="lead">${lead}</span>
  <span class="rt"><b>${name}</b>${line ? `<small>${line}</small>` : ''}</span>${o.on
  ? `<span class="tk">${svg(I.tick, 30)}</span>` : ''}</div>`;
const sec = t => `<div class="sec"><i></i><span>${t}</span><i></i></div>`;
const searchRow = t => `<div class="row search">${svg(I.search, 30)}<span class="ph">${t}</span></div>`;
const country = `<div class="panel">${searchRow('Search countries')}${sec('Suggested')}
  ${row(FLAGS.uk, 'United Kingdom', '', { on: true })}
  ${sec('All')}
  ${row(FLAGS.af, 'Afghanistan', '', {})}${row(FLAGS.al, 'Albania', '', {})}${row(FLAGS.dz, 'Algeria', '', {})}
  ${row(FLAGS.ad, 'Andorra', '', {})}${row(FLAGS.ar, 'Argentina', '', {})}${row(FLAGS.au, 'Australia', '',
    { focus: true })}${row(FLAGS.at, 'Austria', '', {})}
  <div class="row ghost"><span class="lead"></span><span class="rt"><b>Bosnia and Herzegovina</b></span>
  <span class="tk"></span></div></div>`;
const svc = (name, colour) => `<span class="logo">${logo(name, colour)}</span>`;
const services = `<div class="panel">${sec('Selected')}
  ${row(svc('netflix', '#E50914'), 'Netflix', '', { on: true })}
  ${row(svc('primevideo', '#1A98FF'), 'Prime Video', '', { on: true, focus: true })}
  ${row(svc('disneyplus', '#2E6BFF'), 'Disney+', '', { on: true })}
  ${sec('All')}
  ${row(svc('appletv', '#FFFFFF'), 'Apple TV+', '', {})}
  ${row(svc('crunchyroll', '#F47521'), 'Crunchyroll', '', {})}${row(svc('hbomax', '#9B4DFF'), 'HBO Max', '', {})}
  ${row(svc('mubi', '#FFFFFF'), 'MUBI', '', {})}${row(svc('paramountplus', '#0064FF'), 'Paramount+', '', {})}</div>`;
const keys = [['1', '2', '3', '4', '5', '6', '7', '8', '9', '0'], ['q', 'w', 'e', 'r', 't', 'y', 'u', 'i', 'o', 'p'],
  ['a', 's', 'd', 'f', 'g', 'h', 'j', 'k', 'l', '/'], ['⇧', 'z', 'x', 'c', 'v', 'b', 'n', 'm', '.', '@'],
  ['#+=', 'mic', 'Space', '⌫', 'Clear']];
const kb = focus => `<div class="kb">${keys.map(r => `<div class="krow">${r.map(k => `<span class="key
  ${k.length > 1 ? 'wide' : ''} ${k === 'Space' ? 'space' : ''} ${k === focus ? 'focus' : ''}">${k === 'mic'
  ? svg(I.mic, 28) : k}</span>`).join('')}</div>`).join('')}</div>`;
const ghost = `<div class="row ghost"><span class="lead"></span><span class="rt"><b>Bosnia and Herzegovina</b></span>
  <span class="tk"></span></div>`;
const se = stripes('180deg', ['#006AA7', '#006AA7']);
const ch = stripes('180deg', ['#D52B1E', '#D52B1E']);
const sz = stripes('180deg', ['#3E5EB9', '#FFD900', '#B10C0C', '#FFD900', '#3E5EB9']);
const countryFocus = `<div class="panel">${searchRow('Search countries').replace('class="row search"',
  'class="row search focus"')}${sec('Suggested')}
  ${row(FLAGS.uk, 'United Kingdom', '', { on: true })}${sec('All')}
  ${row(FLAGS.af, 'Afghanistan', '', {})}${row(FLAGS.al, 'Albania', '', {})}${row(FLAGS.dz, 'Algeria', '', {})}
  ${row(FLAGS.ad, 'Andorra', '', {})}${ghost}</div>`;
const countryTyping = `<div class="panel"><div class="row search typed">${svg(I.search, 30)}<span>sw<i class="caret"></i>
  </span></div>${sec('Results')}
  ${row(se, 'Sweden', '', {})}${row(ch, 'Switzerland', '', {})}${row(sz, 'Eswatini', '', {})}${ghost}</div>`;
const frame = (inner, k) => `<div class="frame"><div class="page"></div><div class="lay">${k ? kb('w') : ''}${inner}
  </div></div>`;
const body = frame(countryTyping, true).replace('<div class="kb">', '<div class="kbp"><div class="kb">')
  .replace(/(<\/div><\/div>)(<div class="panel">)/, '$1</div>$2');
const css = `${font(400)}${font(500)}*{box-sizing:border-box;margin:0}
body{width:1920px;overflow:hidden;font-family:R;color:#fff;background:#16181C;position:relative}
body:after{content:"";position:fixed;inset:0;border:6px solid #8a919c;z-index:99;pointer-events:none}
.frame .page{position:absolute;inset:0;background:radial-gradient(ellipse at 30% 40%,#23262c 0%,#16181C 70%)}
.frame{position:relative;width:1920px;height:1080px;overflow:hidden}.frame+.frame{border-top:8px solid #8a919c}
.lay{position:absolute;right:96px;top:56px;display:flex;gap:40px;align-items:flex-start}
.kb{width:760px;display:flex;flex-direction:column;gap:12px;margin-top:6px}.krow{display:flex;gap:10px}
.key{flex:1;height:64px;border-radius:16px;background:#2a2d33;display:flex;align-items:center;justify-content:center;
font-size:30px;font-weight:500}.key.wide{flex:1.6;font-size:24px}.key.space{flex:3}.key.focus{background:#F4F6FB;color:#0B0D12}
.kbp{padding:22px;border-radius:26px;background:rgba(22,24,28,.9);box-shadow:0 0 0 2px rgba(255,255,255,.12),
0 30px 80px rgba(0,0,0,.6)}.kbp .kb{margin-top:0}
.typed{color:#fff}.typed span{font-size:30px;display:flex;align-items:center}.caret{display:inline-block;width:3px;height:36px;
background:#fff;margin-left:4px}.row.search.focus{color:#0B0D12}
.pair{position:absolute;inset:48px 56px;display:flex;justify-content:space-around;align-items:flex-start}
.row.ghost{height:0;overflow:hidden;visibility:hidden}.row:has(+ .row.ghost):after{display:none}.tk{width:30px}
.panel{width:max-content;min-width:360px;max-width:768px;padding:18px;border-radius:26px;background:rgba(22,24,28,.9);
box-shadow:0 0 0 2px rgba(255,255,255,.12),0 30px 80px rgba(0,0,0,.6)}
.row{position:relative;display:flex;align-items:center;gap:24px;height:84px;padding:0 26px;border-radius:18px}
.row:after{content:'';position:absolute;left:26px;right:26px;bottom:0;height:2px;background:rgba(255,255,255,.08)}
.row.focus:after,.row:has(+ .sec):after,.row:last-child:after,.row:has(+ .row.focus):after{display:none}
.row.focus{background:#F4F6FB;color:#0B0D12}
.lead{width:64px;display:flex;justify-content:center;flex:none}
.flag{display:block;width:48px;height:32px;border-radius:4px;overflow:hidden;box-shadow:0 0 0 1px rgba(255,255,255,.25)}
.flag svg{display:block}
.logo{display:flex;width:64px;height:36px}.logo svg{width:100%;height:100%}
.row.focus .logo svg path[fill="#FFFFFF"]{fill:#0B0D12}
.rt{flex:1;display:flex;flex-direction:column}.rt b{font-size:30px;font-weight:500}
.rt small{font-size:24px;color:#C9D3E6;margin-top:2px}.row.focus .rt small{color:#3A4256}
.tk{display:flex;color:#fff}.row.focus .tk{color:#0B0D12}
.search{color:#C9D3E6;gap:20px}.search .ph{font-size:29px}.search:after{display:none}
.sec{display:flex;align-items:center;gap:16px;padding:22px 26px 8px;font-size:28px;font-weight:500}
.sec i{flex:1;height:2px;background:rgba(255,255,255,.18)}`;
(async () => {
  const browser = await chromium.launch();
  const tab = await browser.newPage({ viewport: { width: 1920, height: 1080 } });
  await tab.setContent(`<html><head><style>${css}</style></head><body>${body}</body></html>`, { waitUntil: 'load' });
  await tab.evaluate(() => document.fonts.ready);
  await tab.screenshot({ path: path.join(__dirname, '04-v4-keyboard-in-a-panel.png') });
  await browser.close();
})();
