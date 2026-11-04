// Proposal (0.156): Edit Page panel fits without scrolling; Year becomes a range; Save Changes keeps its tick on focus.
// Run: FONT_DIR=<roboto woff2 dir> node render.js
const fs = require('fs'), path = require('path'), { chromium } = require('playwright');
const out = __dirname, fontDir = process.env.FONT_DIR;
const font = w => `@font-face{font-family:R;font-weight:${w};src:url(data:font/woff2;base64,${fs.readFileSync(path.join(fontDir, `roboto-latin-${w}-normal.woff2`)).toString('base64')}) format('woff2')}`;
const P = { pen: '<path d="M4 20h4L19 9l-4-4L4 16z"/>', film: '<rect x="4" y="3" width="16" height="18" rx="2"/><path d="M8 3v18M16 3v18M4 8h4M4 16h4M16 8h4M16 16h4"/>',
  filter: '<path d="M4 5h16l-6 8v6l-4-2v-4z"/>', date: '<rect x="4" y="5" width="16" height="15" rx="2"/><path d="M4 10h16M9 3v4M15 3v4"/>',
  eye: '<path d="M2 12s4-7 10-7 10 7 10 7-4 7-10 7S2 12 2 12z"/><circle cx="12" cy="12" r="3"/>', star: '<path d="M12 3l2.8 5.8 6.2.9-4.5 4.4 1 6.3-5.5-3-5.5 3 1-6.3L3 9.7l6.2-.9z"/>',
  globe: '<circle cx="12" cy="12" r="9"/><path d="M3 12h18M12 3a14 14 0 0 1 0 18M12 3a14 14 0 0 0 0 18"/>', studio: '<rect x="4" y="4" width="16" height="7" rx="2"/><rect x="4" y="13" width="16" height="7" rx="2"/>',
  grid: '<rect x="4" y="4" width="7" height="7" rx="1"/><rect x="13" y="4" width="7" height="7" rx="1"/><rect x="4" y="13" width="7" height="7" rx="1"/><rect x="13" y="13" width="7" height="7" rx="1"/>',
  sort: '<path d="M4 6h10M4 12h7M4 18h4M18 5v14M15 16l3 3 3-3"/>', order: '<path d="M8 4v16M5 7l3-3 3 3M16 20V4M13 17l3 3 3-3"/>', trash: '<path d="M4 7h16M9 7V4h6v3M6 7l1 13h10l1-13"/>',
  check: '<path d="M5.5 12.5l4 4L18.5 8"/>', chev: '<path d="M9 6l6 6-6 6"/>', left: '<path d="M15 6l-6 6 6 6"/>', back: '<path d="M15 6l-6 6 6 6"/>' };
const ic = (k, s = 26) => `<svg width="${s}" height="${s}" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round">${P[k]}</svg>`;
const segs = (opts, on) => `<span class="val">${opts.map(o => `<b class="${o === on ? 'sel' : ''}">${o}</b>`).join('<i>|</i>')}</span>`;
const step = v => `<span class="val">${ic('left', 20)}<b class="sel w">${v}</b>${ic('chev', 20)}</span>`;
const more = v => `<span class="val"><b>${v}</b>${ic('chev', 20)}</span>`;
const row = (k, t, v, f, cls = '') => `<div class="row${f ? ' focus' : ''} ${cls}">${ic(k)}<span class="lbl">${t}</span>${v}</div>`;
const head = (saveFocus) => `<div class="hdr"><div><div class="ttl">Edit Page</div><div class="mt">41 titles match · 123 GB</div></div><span class="save${saveFocus ? ' f' : ''}">${ic('check', 24)}Save Changes</span></div><div class="rule"></div>`;
const main = (focus) => `<div class="panel">${head(focus === 'save')}` +
  row('pen', 'Page Name', more('Documentaries')) + row('film', 'Includes', segs(['Movies', 'TV Shows', 'Both'], 'Both')) +
  `<div class="h"><span>What to show</span><i></i></div>` +
  row('filter', 'Genres', more('Documentary')) + row('filter', 'Match', segs(['Any', 'All'], 'Any')) +
  row('date', 'Year', more('2003 – 2006'), focus === 'year') + row('eye', 'Watched', step('All')) + row('star', 'Minimum Rating', step('Any')) +
  row('globe', 'Original Language', more('Any')) + row('studio', 'Studio / Network', more('Any')) +
  `<div class="h"><span>How to show it</span><i></i></div>` +
  row('grid', 'View', segs(['Grid', 'List'], 'Grid')) + row('sort', 'Sort By', segs(['Title', 'Year', 'Date Added'], 'Year')) + row('order', 'Order', segs(['Newest', 'Oldest'], 'Newest')) +
  `<div class="rule low"></div>` + row('trash', 'Delete Page', '<span class="dn">Removes this page only. Your media is never deleted.</span>', false, 'del') + `</div>`;
const yearPage = `<div class="panel">${head(false)}<div class="h back">${ic('back', 20)}<span>Year</span><i></i></div>` +
  row('date', 'From', step('2003'), true) + row('date', 'To', step('2006')) +
  `<div class="h"><span>Quick choices</span><i></i></div>` +
  ['Any year', 'This year', 'Last 5 years', '2020s', '2010s', '2000s', '1990s', 'Before 1990'].map((t, i) => `<div class="row opt"><span class="rad${i === 0 ? '' : ''}"></span><span class="lbl">${t}</span></div>`).join('') + `</div>`;
const bg = `<div class="bg"></div>${[0, 1, 2].map(i => `<div class="card c${i}"></div>`).join('')}`;
const shots = [['01-fits-on-screen.png', bg + main('save'), 'Every option fits without scrolling (rows 56 px); Save Changes keeps its tick when focused'],
  ['02-year-range.png', bg + main('year'), 'Year shows a range; OK opens it'],
  ['03-year-picker.png', bg + yearPage, 'Year opens inside the panel: From and To (Left/Right), or a quick choice; Back returns']];
const css = `${font(400)}${font(500)}*{box-sizing:border-box;margin:0}body{width:1920px;height:1080px;overflow:hidden;font-family:R;color:#F4F6FB;background:#0b1430;position:relative}
.bg{position:absolute;inset:0;background:linear-gradient(160deg,#16255a,#0a1230 60%,#1636a8)}.card{position:absolute;top:300px;width:300px;height:450px;border-radius:20px;opacity:.55}
.c0{left:80px;background:linear-gradient(160deg,#5d8a4a,#2b4a2a)}.c1{left:420px;background:linear-gradient(160deg,#6a4a4a,#2a1a1a)}.c2{left:760px;background:linear-gradient(160deg,#3a4a6a,#1a2230)}
.panel{position:absolute;right:48px;top:48px;width:760px;border-radius:26px;padding:24px 26px;background:rgba(22,24,28,.97);border:2px solid rgba(255,255,255,.22)}
.hdr{display:flex;align-items:center;justify-content:space-between;margin:0 6px 14px}.rule{height:1px;background:rgba(255,255,255,.22);margin:0 6px 10px}.rule.low{margin:12px 6px 6px}.ttl{font-size:32px;font-weight:500}.mt{font-size:19px;color:#7fe0a8;margin-top:4px}
.save{display:flex;align-items:center;gap:10px;height:58px;padding:0 24px;border-radius:20px;background:#2a2d33;font-size:21px;font-weight:500}.save.f{background:#F4F6FB;color:#0B0D12}
.h{display:flex;align-items:center;gap:14px;font-size:19px;margin:8px 8px 0;height:36px;color:#b7bfcc}.h i{flex:1;height:1px;background:rgba(255,255,255,.18)}
.row{display:flex;align-items:center;gap:16px;height:56px;border-radius:20px;padding:0 16px;font-size:23px;font-weight:500}.row svg{color:#dfe5ef;flex:none}.focus{box-shadow:inset 0 0 0 3px #fff}
.lbl{flex:1;white-space:nowrap}.val{display:flex;align-items:center;gap:8px;font-weight:400;font-size:22px;color:#dfe5f1;white-space:nowrap}.val i{font-style:normal;color:#8f98a8}.val b{font-weight:400}.val b.sel{color:#5aa9ff}.val b.w{min-width:70px;text-align:center}.val svg{color:#8f98a8}
.del .lbl{color:#ff8fb0;flex:none}.del svg{color:#ff8fb0}.dn{font-size:19px;color:#aab3c4;font-weight:400}
.opt{height:52px}.rad{width:26px;height:26px;border-radius:50%;border:2px solid #aab3c4;flex:none}
.label{position:absolute;left:80px;bottom:20px;font-size:20px;color:#c9d3e6;max-width:900px}`;
(async () => { const b = await chromium.launch(); const t = await b.newPage({ viewport: { width: 1920, height: 1080 } });
  for (const [f, body, label] of shots) {
    await t.setContent(`<html><head><style>${css}</style></head><body>${body}<div class="label">${label}</div></body></html>`, { waitUntil: 'load' });
    await t.evaluate(() => document.fonts.ready);
    const over = await t.evaluate(() => document.querySelector(".panel").getBoundingClientRect().bottom > 1032);
    if (over) console.log(f, 'OVERFLOW');
    await t.screenshot({ path: path.join(out, f) }); }
  await b.close(); })();
