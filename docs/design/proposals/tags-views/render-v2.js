// Tags and views v2 (owner, 15 Nov 2026; parked, for discussion). No "SUPERNOVA" in the top bar: the user switcher
// takes its place at the far left as one fixed-size icon, showing the current user's own icon (each user picks one,
// like a profile picture but a monochrome icon; Together shows two overlapping people).
// 1 Home, Together on, the user switching menu open. 2 Details › More with Tags in the Library group.
// 3 The Tags list on top of More: two groups, Users and Tags, ticks, New tag. Made-up names and titles.
// Light frame = picture edge. Run: FONT_DIR=<roboto woff2 dir> node render-v2.js
const fs = require('fs');
const path = require('path');
const { chromium } = require('playwright');
const fontDir = process.env.FONT_DIR;
const font = w => `@font-face{font-family:R;font-weight:${w};src:url(data:font/woff2;base64,${fs.readFileSync(
  path.join(fontDir, `roboto-latin-${w}-normal.woff2`)).toString('base64')}) format('woff2')}`;
const svg = (d, s = 30) => `<svg width="${s}" height="${s}" viewBox="0 0 24 24" fill="none" stroke="currentColor"
  stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round">${d}</svg>`;
const I = {
  search: '<circle cx="11" cy="11" r="7"/><path d="M20 20l-4-4"/>',
  cog: '<circle cx="12" cy="12" r="3"/><path d="M12 2v3M12 19v3M4.2 4.2l2.1 2.1M17.7 17.7l2.1 2.1M2 12h3M19 12h3'
    + 'M4.2 19.8l2.1-2.1M17.7 6.3l2.1-2.1"/>',
  all: '<rect x="3" y="3" width="7" height="7" rx="1.5"/><rect x="14" y="3" width="7" height="7" rx="1.5"/>'
    + '<rect x="3" y="14" width="7" height="7" rx="1.5"/><rect x="14" y="14" width="7" height="7" rx="1.5"/>',
  rocket: '<path d="M12 3c3 2 5 5.5 5 9.5L15 16H9l-2-3.5C7 8.5 9 5 12 3z"/><circle cx="12" cy="10" r="1.8"/>'
    + '<path d="M9 16l-2 4 3-1.5M15 16l2 4-3-1.5"/>',
  flower: '<circle cx="12" cy="9" r="2.2"/><path d="M12 6.8a2.6 2.6 0 1 1 2.6 2.6M14.6 9.4a2.6 2.6 0 1 1-2 3.8M12.6'
    + ' 13.2a2.6 2.6 0 1 1-3.2-2M9.4 11.2A2.6 2.6 0 1 1 12 6.8M12 13v8M12 18c-2 0-3.5-1-4-3M12 17c2 0 3.5-1 4-3"/>',
  both: '<circle cx="9" cy="8" r="3.5"/><path d="M2.5 20a6.5 6.5 0 0 1 13 0"/><path d="M15 4.6a3.5 3.5 0 1 1 0 6.8"/>'
    + '<path d="M17.5 14.2A6.5 6.5 0 0 1 21.5 20"/>',
  edit: '<path d="M4 20h4L19 9l-4-4L4 16z"/><path d="M14 6l4 4"/>',
  tick: '<path d="M5 12.5l4.5 4.5L19 7.5"/>',
  tag: '<path d="M3 12V4h8l10 10-8 8z"/><circle cx="7.5" cy="8.5" r="1.5"/>',
  tree: '<path d="M12 3l5 6h-3l4 5h-3l3 4H6l3-4H6l4-5H7z"/><path d="M12 18v3"/>',
  star: '<path d="M12 3l2.7 5.6 6.1.9-4.4 4.3 1 6.1L12 17l-5.4 2.9 1-6.1L3.2 9.5l6.1-.9z"/>',
  plus: '<path d="M12 5v14M5 12h14"/>',
  play: '<path d="M7 4l13 8-13 8z"/>',
  dots: '<circle cx="5" cy="12" r="1.3"/><circle cx="12" cy="12" r="1.3"/><circle cx="19" cy="12" r="1.3"/>',
  restart: '<path d="M4 12a8 8 0 1 0 2.4-5.7"/><path d="M4 4v4h4"/>',
  watched: '<circle cx="12" cy="12" r="9"/><path d="M8 12.5l3 3 5-6"/>',
  fix: '<circle cx="11" cy="11" r="7"/><path d="M20 20l-4-4M8.5 11h5M11 8.5v5"/>',
  remove: '<path d="M5 7h14M10 7V4h4v3M7 7l1 13h8l1-13"/>',
  art: '<rect x="3" y="5" width="18" height="14" rx="2"/><circle cx="9" cy="10" r="2"/><path d="M21 16l-5-5-8 8"/>',
  page: '<rect x="4" y="3" width="16" height="18" rx="2"/><path d="M8 8h8M8 12h8M8 16h5"/>',
};
const nav = (opts = {}) => `<div class="nav"><span class="user ${opts.focus ? 'focus' : 'line'}">${svg(I.both, 34)}</span>
  <span class="ni">Home</span><span class="ni ${opts.movies || ''}">Movies</span><span class="ni">TV Shows</span>
  <span class="ni">Documentaries</span><span class="ni files">Files</span><span class="ni">${svg(I.search, 34)}</span>
  <span class="ni">${svg(I.cog, 34)}</span><span class="clock">21:40</span></div>`;
const mi = (ic, t, sub, o = {}) => `<div class="mi ${o.focus ? 'focus' : ''}">${svg(I[ic], 30)}<div class="mt"><b>${t}</b>
  ${sub ? `<small>${sub}</small>` : ''}</div><span class="end">${o.tick ? svg(I.tick, 28) : ''}</span></div>`;
const userMenu = `<div class="menu um">${mi('all', 'Everything', 'Nothing hidden')}
  ${mi('rocket', 'Me', 'Hides Alex and Classics', { focus: true })}${mi('flower', 'Alex', 'Only Alex and Together')}
  ${mi('both', 'Together', 'Only Together', { tick: true })}<div class="sep"></div>${mi('edit', 'Edit users and views', '')}
  </div>`;
const cards = n => Array.from({ length: n }, (_, i) => `<div class="card" style="background:linear-gradient(150deg,
  ${['#4b5a4e', '#6b4a42', '#4d4766', '#6a6040', '#3f5f62'][i % 5]},#1f2228)"></div>`).join('');
const heroArt = 'linear-gradient(100deg,rgba(14,16,20,.92),rgba(14,16,20,.3) 60%),radial-gradient(ellipse at 72% 45%,'
  + '#6a5a48,#2a2620 75%)';
const home = `<div class="frame">${nav({ focus: true })}<div class="hero" style="background:${heroArt}"><div class="hl">
  SNOWED IN</div><div class="hm">2024 · 1 h 41 min · Comedy, Family</div></div><div class="rowh">Continue Watching
  </div><div class="row">${cards(5)}</div>${userMenu}</div>`;
const grp = t => `<div class="mg"><span>${t}</span></div>`;
const more = (dim) => `<div class="menu more ${dim ? 'behind' : ''}">${grp('Playback')}${mi('restart', 'Play from the start',
  '')}${grp('Library')}${mi('watched', 'Mark as watched', '')}${mi('tag', 'Tags', 'Together · Christmas', { focus: !dim })}
  ${mi('fix', 'Fix match', '')}${mi('remove', 'Remove from library', '')}${grp('Files &amp; Media')}
  ${mi('art', 'Artwork', '')}${mi('page', 'Full description', '')}</div>`;
const detailsHero = `${nav({ movies: 'dull' })}<div class="hero tall" style="background:${heroArt}"><div class="hl">SNOWED IN
  </div><div class="hm">PG · 2024 · 1 h 41 min · Comedy, Family</div><div class="hd">A family snowed in at a hotel on
  Christmas Eve sets out to save the staff party.</div><div class="hb"><span class="btn">${svg(I.play, 26)}Play</span>
  <span class="btn bline">${svg(I.dots, 26)}More</span></div></div>`;
const d1 = `<div class="frame">${detailsHero}${more(false)}</div>`;
const tagList = `<div class="menu tags">${grp('Users')}${mi('rocket', 'Me', '')}${mi('flower', 'Alex', '')}
  ${mi('both', 'Together', '', { tick: true })}${grp('Tags')}${mi('tree', 'Christmas', '', { tick: true, focus: true })}
  ${mi('star', 'Classics', '')}<div class="sep"></div>${mi('plus', 'New tag', '')}</div>`;
const d2 = `<div class="frame">${detailsHero}${more(true)}${tagList}</div>`;
const css = `${font(400)}${font(500)}*{box-sizing:border-box;margin:0}
body{width:1920px;font-family:R;color:#fff;background:#16181C;position:relative}
body:after{content:"";position:absolute;inset:0;border:6px solid #8a919c;z-index:99;pointer-events:none}
.frame{width:1920px;height:1080px;position:relative;overflow:hidden;background:#16181C}.frame+.frame{border-top:8px solid
#8a919c}
.nav{position:absolute;left:56px;right:56px;top:24px;height:76px;display:flex;align-items:center;gap:14px;
font-size:33px;color:#8f98a8;z-index:5}
.user{position:relative;width:64px;height:64px;border-radius:50%;display:inline-flex;align-items:center;
justify-content:center;color:#fff;box-shadow:inset 0 0 0 2px rgba(255,255,255,.28);margin-right:26px}
.user.focus{background:#F4F6FB;color:#0B0D12;box-shadow:none}
.ni{position:relative;display:inline-flex;align-items:center;height:60px;padding:0 18px}.ni.files{margin-left:auto}
.nav .clock{margin-left:18px;color:#C9D3E6}.dull{color:#C9D3E6}.dull:after{content:'';position:absolute;left:18px;
right:18px;bottom:-2px;height:4px;border-radius:2px;background:rgba(255,255,255,.3)}
.hero{position:absolute;left:72px;top:118px;width:1776px;height:640px;border-radius:26px;box-shadow:inset 0 0 0 2px
rgba(255,255,255,.6)}.hero.tall{height:820px}
.hl{position:absolute;left:60px;top:200px;font-size:76px;letter-spacing:8px;font-weight:500;color:#f1ead8}
.hm{position:absolute;left:62px;top:300px;font-size:28px;color:#e8edf6}.hd{position:absolute;left:62px;top:356px;
width:900px;font-size:27px;line-height:1.45;color:#e8edf6}
.hb{position:absolute;left:60px;bottom:60px;display:flex;gap:16px}.btn{display:inline-flex;align-items:center;gap:12px;
height:68px;padding:0 30px;border-radius:34px;font-size:28px;font-weight:500;background:rgba(22,24,28,.72)}
.bline{box-shadow:inset 0 -4px 0 #fff}
.rowh{position:absolute;left:96px;top:800px;font-size:30px;font-weight:500}.row{position:absolute;left:96px;top:852px;
display:flex;gap:24px}.card{width:400px;height:225px;border-radius:14px;box-shadow:inset 0 0 0 2px rgba(255,255,255,.6)}
.menu{position:absolute;width:max-content;padding:14px;border-radius:24px;background:rgba(22,24,28,.95);
box-shadow:0 0 0 2px rgba(255,255,255,.14),0 30px 80px rgba(0,0,0,.7);z-index:6}
.um{left:44px;top:100px}.more{left:300px;top:250px}.tags{left:560px;top:330px;z-index:7}
.behind{transform:scale(.96);transform-origin:left top;filter:brightness(.45)}
.mi{display:flex;align-items:center;gap:20px;height:84px;padding:0 24px;border-radius:18px;min-width:480px}
.mi svg{color:#C9D3E6;flex:none}.mt b{display:block;font-size:29px;font-weight:500}.mt small{font-size:22px;color:#C9D3E6}
.mi .end{margin-left:auto;padding-left:24px}.mi.focus{background:#F4F6FB;color:#0B0D12}.mi.focus svg,.mi.focus small{
color:#0B0D12}.sep{height:2px;background:rgba(255,255,255,.12);margin:8px 24px}
.mg{display:flex;align-items:center;gap:18px;margin:12px 24px 6px;font-size:26px;font-weight:500}.mg:before,.mg:after{
content:'';flex:1;height:2px;background:rgba(255,255,255,.16)}`;
(async () => {
  const browser = await chromium.launch();
  const tab = await browser.newPage({ viewport: { width: 1920, height: 3256 } });
  await tab.setContent(`<html><head><style>${css}</style></head><body>${home}${d1}${d2}</body></html>`,
    { waitUntil: 'load' });
  await tab.evaluate(() => document.fonts.ready);
  await tab.screenshot({ path: path.join(__dirname, '02-v2-user-switcher-and-tags.png') });
  await browser.close();
})();
