// Proposal (0.155, owner 4 Nov 2026): watched state in words; library + watched status carried into the Grid view
// with real artwork; long-press menu as an opaque panel attached to the card.
// Run: FONT_DIR=<roboto woff2 dir> node status-v2.js
const fs = require('fs');
const path = require('path');
const { chromium } = require('playwright');
const out = __dirname;
const fontDir = process.env.FONT_DIR;
const font = w => `@font-face{font-family:R;font-weight:${w};src:url(data:font/woff2;base64,${fs.readFileSync(path.join(fontDir, `roboto-latin-${w}-normal.woff2`)).toString('base64')}) format('woff2')}`;
const P = {
  video: '<path d="M14 3H6.5A1.5 1.5 0 0 0 5 4.5v15A1.5 1.5 0 0 0 6.5 21h11a1.5 1.5 0 0 0 1.5-1.5V8z"/><path d="M14 3v5h5"/><path d="M10 11.5v5l4-2.5z" fill="currentColor"/>',
  play: '<path d="M8 5l11 7-11 7z"/>', x: '<path d="M6 6l12 12M18 6L6 18"/>', info: '<circle cx="12" cy="12" r="9"/><path d="M12 11v6M12 7.5v.5"/>',
  ban: '<circle cx="12" cy="12" r="8.5"/><path d="M6 6l12 12"/>', tick: '<path d="M5.5 12.5l4 4L18.5 8"/>',
  eye: '<path d="M2.5 12s3.5-6.5 9.5-6.5S21.5 12 21.5 12s-3.5 6.5-9.5 6.5S2.5 12 2.5 12z"/><circle cx="12" cy="12" r="2.8"/>',
  grid: '<rect x="4" y="4" width="7" height="7"/><rect x="13" y="4" width="7" height="7"/><rect x="4" y="13" width="7" height="7"/><rect x="13" y="13" width="7" height="7"/>',
  list: '<path d="M8 6h12M8 12h12M8 18h12M4 6h.01M4 12h.01M4 18h.01"/>',
  sort: '<path d="M4 6h10M4 12h7M4 18h4M18 5v14M15 16l3 3 3-3"/>', filter: '<path d="M4 5h16l-6 7v6l-4 2v-8z"/>',
  folder: '<path d="M3 6.5A1.5 1.5 0 0 1 4.5 5H9l2 2.5h8.5A1.5 1.5 0 0 1 21 9v8.5a1.5 1.5 0 0 1-1.5 1.5h-15A1.5 1.5 0 0 1 3 17.5z"/>',
  drive: '<rect x="3" y="7" width="18" height="10" rx="2"/><path d="M7 12h.01M11 12h6"/>',
};
const ic = (k, s = 32) => `<svg width="${s}" height="${s}" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round">${P[k]}</svg>`;
const btn = (k, t, f) => `<div class="btn${f ? ' focus' : ''}">${ic(k, 26)}<span>${t}</span></div>`;
const nav = `<div class="nav"><span class="brand">SUPERNOVA</span><span>Home</span><span>Movies</span><span>TV Shows</span><span>Documentaries</span><span class="sp"></span><span class="on">Network &amp; Files</span><span class="clock">12:36</span></div>`;
const head = grid => `<div class="head"><div class="ftile">${ic('folder', 44)}</div>
  <div class="hcol"><div class="crumb"><span class="chip">${ic('drive', 24)}Local Storage</span><b>›</b><span class="chip">Backup #1 (SanDisk USB drive)</span><b>›</b><span class="chip">NVIDIA_SHIELD</span></div>
  <div class="title">Movies</div><div class="pills"><span class="pill">66 videos</span><span class="pill">146 GB</span><span class="pill lib"><i></i>In your library</span><span class="pill">2 excluded</span><span class="pill">3 watched</span></div></div>
  <div class="tools">${grid ? btn('list', 'List') : btn('grid', 'Grid')}${btn('sort', 'Sort: Name')}${btn('filter', 'Videos only')}</div></div><div class="rule"></div>`;
const lib = st => st === 'in' ? '<span class="tag in"><i></i>In library</span>'
  : st === 'ex' ? `<span class="tag ex">${ic('ban', 20)}Excluded</span>` : '<span class="tag no"><i></i>Not added</span>';
const seen = (w, small) => `<span class="wcell${small ? ' sm' : ''}"><span class="wp ${w === 1 ? 'done' : w > 0 ? 'part' : 'un'}">${w > 0 && w < 1 ? `<i style="width:${w * 100}%"></i>` : ''}<b>${w === 1 ? ic('tick', small ? 18 : 22) + 'Watched' : w > 0 ? Math.round(w * 100) + '% watched' : 'Unwatched'}</b></span></span>`;
const films = [
  { n: '28 Weeks Later', y: 2007, d: '2007 · 100 min · 1080p · English 5.1 · 2.0 GB', st: 'in', w: 1, c: ['#5b1d12', '#140806'] },
  { n: '99 Homes', y: 2015, d: '2015 · 112 min · 1080p · English 5.1 · 1.8 GB', st: 'in', w: 1, c: ['#3a4f63', '#0e151d'] },
  { n: 'Aftersun.2022.1080p.WEBRip.x264-RARBG', y: '', d: 'Not matched yet · 2.1 GB', st: 'in', w: 0, c: null },
  { n: 'All of Us Strangers', y: 2023, d: '2023 · 105 min · 1080p · English 5.1 · 2.0 GB', st: 'in', w: .4, c: ['#7a4a2f', '#1c1210'] },
  { n: 'Argylle', y: 2024, d: '2024 · 139 min · 1080p · English 5.1 · 2.8 GB', st: 'ex', w: 0, c: ['#29408a', '#0b1230'] },
  { n: 'Asteroid City', y: 2023, d: '2023 · 105 min · 1080p · English 5.1 · 1.1 GB', st: 'in', w: 1, c: ['#d9a35b', '#6b3f22'] },
  { n: 'Bad Genius', y: 2017, d: '2017 · 130 min · 1080p · Thai 5.1 · 1.9 GB', st: 'ex', w: 0, c: ['#24302a', '#0a0f0c'] },
  { n: 'Blitz', y: 2024, d: '2024 · 120 min · 1080p · English 5.1 · 2.4 GB', st: 'in', w: 0, c: ['#6d2a1a', '#170a07'] },
  { n: 'Blue Valentine', y: 2010, d: '2010 · 112 min · 1080p · English 5.1 · 1.7 GB', st: 'in', w: 0, c: ['#2a5470', '#0b1823'] },
  { n: 'Bugonia', y: 2025, d: '2025 · 118 min · 1080p · English 5.1 · 2.2 GB', st: 'no', w: 0, c: ['#4f5e2a', '#12160a'] },
];
const row = (r, focus) => `<div class="row${focus ? ' focus' : ''}${r.st === 'ex' ? ' dim' : ''}">${ic('video')}
  <span class="two"><span class="l1"><span class="name1">${r.n}</span>${lib(r.st)}</span><span class="l2">${r.d}</span></span>
  ${focus ? `<span class="ibtns">${btn('play', r.w > 0 && r.w < 1 ? 'Resume' : 'Play')}${btn('x', 'Exclude')}</span>` : ''}${seen(r.w)}</div>`;
const poster = r => r.c ? `<div class="art" style="background:linear-gradient(165deg,${r.c[0]},${r.c[1]} 75%)"><span>${r.n.toUpperCase()}</span></div>`
  : `<div class="art frame"><span class="fr"></span><em>Frame from the file</em></div>`;
const card = (r, focus) => `<div class="card${focus ? ' focus' : ''}${r.st === 'ex' ? ' dim' : ''}"><div class="pw">${poster(r)}
  <span class="corner ${r.st}">${r.st === 'ex' ? ic('ban', 22) : '<i></i>'}</span>
  ${V2 ? `<span class="wbadge2">${seen(r.w, true)}</span>` : `${r.w === 1 ? `<span class="wbadge">${ic('tick', 20)}Watched</span>` : ''}${r.w > 0 && r.w < 1 ? `<span class="pbar"><i style="width:${r.w * 100}%"></i></span>` : ''}`}</div>
  <div class="cn">${r.y ? r.n : 'Aftersun.2022.1080p…'}</div>
  <div class="cm">${r.y ? r.y + ' · ' : ''}${r.st === 'ex' ? 'Excluded' : r.st === 'no' ? 'Not added' : 'In library'}${V2 ? '' : r.w === 1 ? ' · Watched' : r.w > 0 ? ' · ' + Math.round(r.w * 100) + '%' : ''}</div></div>`;
const menu = `<div class="menu"><div class="mt">99 Homes</div><div class="ms">2015 · 112 min · In library · Watched</div>
  ${btn('play', 'Play', true)}${btn('info', 'Details')}${btn('eye', 'Mark as unwatched')}${btn('x', 'Exclude')}</div>`;
let V2 = false;
const shots = [
  ['26-list-watched-words.png', false, null, 'List: watched state in words on the right — ✓ Watched · 40% watched (with bar) · Unwatched'],
  ['27-grid-artwork-status.png', true, null, 'Grid: artwork (poster, or a frame from the file), library dot on the corner, Watched badge, status line under the title'],
  ['28-grid-long-press-menu.png', true, 'menu', 'Grid, long press / Menu: an opaque panel beside the card — title, status, then actions'],
  ['29-list-watched-one-pill.png', false, null, 'List: watched state as ONE pill — fills with progress (40% watched), solid when Watched, outline when Unwatched', 2],
  ['30-grid-watched-one-pill.png', true, null, 'Grid: the same single pill on the artwork; the line under the title keeps year and library state only', 2],
];
const css = `${font(400)}${font(500)}
*{box-sizing:border-box;margin:0}
body{width:1920px;height:1080px;overflow:hidden;font-family:R;color:#fff;position:relative;
  background:radial-gradient(ellipse 70% 60% at 70% 75%,#1d4fd8 0%,rgba(29,79,216,0) 70%),linear-gradient(180deg,#05081a 0%,#0a1650 45%,#10248a 100%)}
.nav{position:absolute;left:56px;right:56px;top:28px;height:56px;display:flex;align-items:center;gap:30px;font-size:34px;color:#e8edf8}
.nav .brand{font-size:36px;margin-right:40px;letter-spacing:1px}.nav .sp{flex:1}.nav .on{font-weight:500}.nav .clock{margin-left:30px}
.sheet{position:absolute;left:56px;right:56px;top:112px;bottom:40px;border-radius:26px;background:rgba(22,24,28,.97);border:2px solid rgba(255,255,255,.22);padding:22px;overflow:hidden}
.head{display:flex;align-items:center;gap:24px;padding:4px 6px 18px}
.ftile{width:96px;height:96px;border-radius:22px;background:#2a2d33;display:flex;align-items:center;justify-content:center;color:#e8edf8;flex:none}
.hcol{flex:1;min-width:0;display:flex;flex-direction:column;gap:6px}
.crumb{display:flex;gap:4px;align-items:center;white-space:nowrap;font-size:21px;color:#c9d3e6;margin-left:-12px}.crumb b{font-weight:400;opacity:.55}
.chip{display:inline-flex;align-items:center;gap:8px;padding:5px 12px;border-radius:14px}
.title{font-size:36px;font-weight:500}
.pills{display:flex;gap:10px;margin-top:4px}.pill{font-size:19px;color:#dfe5f1;background:rgba(255,255,255,.08);border-radius:12px;padding:5px 12px;display:inline-flex;align-items:center;gap:8px}
.pill.lib i{width:10px;height:10px;border-radius:50%;background:#5ad18a}
.rule{height:1px;background:rgba(255,255,255,.14);margin:0 6px 6px}
.tools{display:flex;gap:14px}
.btn{height:64px;padding:0 22px 0 18px;border-radius:22px;background:#2a2d33;display:inline-flex;align-items:center;gap:10px;font-size:22px;font-weight:500;white-space:nowrap}
.btn.focus{background:#F4F6FB;color:#0B0D12}
.row{display:flex;align-items:center;gap:20px;height:96px;padding:0 20px;border-radius:24px;font-size:26px;font-weight:500}
.row.focus{box-shadow:inset 0 0 0 3px #fff}
.row.dim .name1,.row.dim .l2,.row.dim>svg,.row.dim .wt,.row.dim .wcell{opacity:.55}
.two{flex:1;display:flex;flex-direction:column;gap:6px;min-width:0}.l1{display:flex;align-items:center;gap:14px;white-space:nowrap}
.l2{font-weight:400;font-size:21px;color:#C9D3E6;white-space:nowrap}
.tag{font-size:18px;font-weight:500;border-radius:11px;padding:3px 11px;display:inline-flex;align-items:center;gap:7px}
.tag i{width:9px;height:9px;border-radius:50%}
.tag.in{background:rgba(90,209,138,.13);color:#9fe3bb}.tag.in i{background:#5ad18a}
.tag.ex{background:rgba(255,255,255,.08);color:#c9d3e6}
.tag.no{background:rgba(255,255,255,.06);color:#aab3c4}.tag.no i{border:1.6px solid #aab3c4}
.wt{width:210px;flex:none;display:inline-flex;align-items:center;justify-content:flex-end;gap:8px;font-size:21px;font-weight:500}
.wt.done{color:#F4F6FB}.wt.un{color:#8f98a8;font-weight:400}.wt.part{color:#8fc3ff}
.wcell{width:230px;flex:none;display:flex;justify-content:flex-end}.wcell.sm{width:auto}
.wp{position:relative;overflow:hidden;display:inline-flex;align-items:center;height:42px;padding:0 16px;border-radius:21px;font-size:20px;font-weight:500;white-space:nowrap}
.wp b{position:relative;display:inline-flex;align-items:center;gap:7px;font-weight:500}
.wp.done{background:#F4F6FB;color:#0B0D12}
.wp.part{background:rgba(90,169,255,.16);color:#fff;box-shadow:inset 0 0 0 1.5px rgba(90,169,255,.7)}.wp.part i{position:absolute;left:0;top:0;bottom:0;background:rgba(90,169,255,.55)}
.wp.un{color:#9aa3b3;box-shadow:inset 0 0 0 1.5px rgba(255,255,255,.22);font-weight:400}.wp.un b{font-weight:400}
.sm .wp{height:30px;padding:0 10px;font-size:15px;border-radius:15px}
.wbadge2{position:absolute;right:8px;top:8px}.wbadge2 .wp.un{background:rgba(14,16,20,.75)}.wbadge2 .wp.part{background:rgba(14,16,20,.75)}
.bar{width:54px;height:6px;border-radius:3px;background:rgba(255,255,255,.2);overflow:hidden}.bar i,.pbar i{display:block;height:100%;background:#5aa9ff}
.ibtns{display:flex;gap:12px;margin-right:6px}.ibtns .btn{height:60px;font-size:21px;padding:0 18px 0 14px}
.grid{display:grid;grid-template-columns:repeat(8,minmax(0,1fr));gap:26px 22px;padding:14px 8px}
.card{border-radius:18px;padding:8px;position:relative}.card.focus{box-shadow:0 0 0 3px #fff;background:rgba(255,255,255,.06)}
.card.dim .pw,.card.dim .cn,.card.dim .cm{opacity:.5}
.pw{position:relative;aspect-ratio:2/3;border-radius:12px;overflow:hidden}
.art{position:absolute;inset:0;display:flex;align-items:flex-end;padding:12px;font-family:Georgia,serif;font-size:17px;line-height:1.1;color:#f2e6cf;letter-spacing:.5px}
.art.frame{background:linear-gradient(180deg,#5e7f9b,#c4a27a 55%,#3a2d24);align-items:flex-end;justify-content:flex-start}
.art.frame em{font-family:R;font-style:normal;font-size:13px;color:#fff;background:rgba(0,0,0,.45);padding:3px 7px;border-radius:7px}
.corner{position:absolute;top:8px;left:8px;width:30px;height:30px;border-radius:50%;background:rgba(14,16,20,.82);display:flex;align-items:center;justify-content:center;color:#dfe5f1}
.corner i{width:12px;height:12px;border-radius:50%}.corner.in i{background:#5ad18a}.corner.no i{border:2px solid #aab3c4}
.wbadge{position:absolute;right:8px;top:8px;display:inline-flex;align-items:center;gap:5px;background:#F4F6FB;color:#0B0D12;font-size:15px;font-weight:500;border-radius:10px;padding:4px 9px 4px 6px}
.pbar{position:absolute;left:8px;right:8px;bottom:8px;height:6px;border-radius:3px;background:rgba(0,0,0,.55);overflow:hidden}
.cn{font-size:19px;font-weight:500;margin-top:10px;white-space:nowrap;overflow:hidden;text-overflow:ellipsis}
.cm{font-size:16px;color:#C9D3E6;margin-top:4px;white-space:nowrap}
.menu{position:absolute;left:566px;top:445px;width:400px;border-radius:22px;background:#24272d;border:2px solid rgba(255,255,255,.2);padding:20px;display:flex;flex-direction:column;gap:10px;box-shadow:0 18px 50px rgba(0,0,0,.55)}
.menu .mt{font-size:26px;font-weight:500}.menu .ms{font-size:18px;color:#C9D3E6;margin-bottom:6px}.menu .btn{width:100%;height:58px}
.label{position:absolute;right:56px;bottom:8px;font-size:19px;color:#c9d3e6}`;
(async () => {
  const browser = await chromium.launch();
  const tab = await browser.newPage({ viewport: { width: 1920, height: 1080 } });
  for (const [f, grid, extra, label, v] of shots) {
    V2 = v === 2;
    const body = grid ? `<div class="grid">${films.slice(0, 8).map((r, i) => card(r, i === 1)).join('')}${films.slice(8).map(r => card(r)).join('')}</div>`
                      : films.slice(0, 7).map((r, i) => row(r, i === 3)).join('');
    const html = `${nav}<div class="sheet">${head(grid)}${body}</div>${extra ? menu : ''}<div class="label">${label}</div>`;
    await tab.setContent(`<html><head><style>${css}</style></head><body>${html}</body></html>`, { waitUntil: 'load' });
    await tab.evaluate(() => document.fonts.ready);
    const issues = await tab.evaluate(() => {
      const out = [], r = e => e.getBoundingClientRect();
      document.querySelectorAll('.card').forEach(c => { const a = c.querySelector('.corner'), b = c.querySelector('.wp');
        if (a && b && r(b).left < r(a).right + 4) out.push('pill hits corner dot: ' + c.querySelector('.cn').textContent);
        if (b && r(b).right > r(c.querySelector('.pw')).right) out.push('pill overflows art'); });
      document.querySelectorAll('.row').forEach(w => { const b = w.querySelector('.wp'), t = w.querySelector('.ibtns');
        if (b && r(b).right > r(w).right) out.push('pill overflows row');
        if (b && t && r(t).right > r(b).left) out.push('buttons hit pill'); });
      document.querySelectorAll('.cn,.name1,.l2').forEach(e => { if (e.scrollWidth > e.clientWidth + 1 && !e.textContent.includes('…')) out.push('clipped: ' + e.textContent); });
      return out;
    });
    if (issues.length) console.log(f, issues);
    await tab.screenshot({ path: path.join(out, f) });
  }
  await browser.close();
})();
