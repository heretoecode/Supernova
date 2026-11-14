// Two checks before 0.157 coding (owner, 14 Nov 2026):
// 1 Details › Cast and Crew with portrait cards (TMDB's photos are 2:3; the round crop cut heads off). Cards are like
//   small posters: rounded corners, the artwork edge, name and role underneath; the focused card grows 8% with the
//   thick ring; initials on a plain card when there is no photo. Silhouettes stand in for photos.
// 2 Settings › About › 0.157 opens the release notes pop-up (approved style, settings-v2/24), written properly:
//   grouped, one change per line, plain words. A draft: the final notes are written when 0.157 is signed.
// Made-up names. Light frame = picture edge. Run: FONT_DIR=<roboto woff2 dir> node render.js
const fs = require('fs');
const path = require('path');
const { chromium } = require('playwright');
const fontDir = process.env.FONT_DIR;
const font = w => `@font-face{font-family:R;font-weight:${w};src:url(data:font/woff2;base64,${fs.readFileSync(
  path.join(fontDir, `roboto-latin-${w}-normal.woff2`)).toString('base64')}) format('woff2')}`;
const svg = (d, s = 30) => `<svg width="${s}" height="${s}" viewBox="0 0 24 24" fill="none" stroke="currentColor"
  stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round">${d}</svg>`;
const I = {
  people: '<circle cx="9" cy="8" r="3.5"/><path d="M2.5 20a6.5 6.5 0 0 1 13 0"/><circle cx="17" cy="9" r="2.5"/>'
    + '<path d="M16 14.5a5 5 0 0 1 6 5"/>',
  crew: '<path d="M4 20V10l8-6 8 6v10"/><path d="M9 20v-6h6v6"/>',
  search: '<circle cx="11" cy="11" r="7"/><path d="M20 20l-4-4"/>',
  cog: '<circle cx="12" cy="12" r="3"/><path d="M12 2v3M12 19v3M4.2 4.2l2.1 2.1M17.7 17.7l2.1 2.1M2 12h3M19 12h3'
    + 'M4.2 19.8l2.1-2.1M17.7 6.3l2.1-2.1"/>',
  info: '<circle cx="12" cy="12" r="9"/><path d="M12 11v6M12 7.5h.1"/>',
  extras: '<rect x="3" y="7" width="18" height="13" rx="2"/><path d="M3 7l3-4h3l-3 4M10 7l3-4h3l-3 4"/>',
  more: '<path d="M12 3l1.8 4.6L18 9l-4.2 1.4L12 15l-1.8-4.6L6 9l4.2-1.4z"/>',
  notes: '<path d="M6 3h9l4 4v14H6z"/><path d="M9 11h7M9 15h7"/>',
  chev: '<path d="M9 6l6 6-6 6"/>',
};
const tones = ['#4b5a4e', '#6b4a42', '#4d4766', '#6a6040', '#3f5f62', '#663f52', '#3f4d6c', '#6a5640'];
const sil = `<svg viewBox="0 0 120 180" width="100%" height="100%" preserveAspectRatio="none"><circle cx="60" cy="66" r="27"
  fill="rgba(255,255,255,.22)"/><path d="M14 180c2-44 20-66 46-66s44 22 46 66z" fill="rgba(255,255,255,.18)"/></svg>`;
const card = (i, name, role, o = {}) => `<div class="pc ${o.focus ? 'focus' : ''}"><div class="ph" style="background:
  linear-gradient(160deg,${tones[i % tones.length]},#202329)">${o.initials ? `<i>${o.initials}</i>` : sil}</div>
  <b>${name}</b><small>${role}</small></div>`;
const nav = `<div class="nav"><span class="brand">SUPERNOVA</span><span class="ni">Home</span><span class="ni dull">Movies
  </span><span class="ni">TV Shows</span><span class="ni">Documentaries</span><span class="ni files">Files</span>
  <span class="ni">${svg(I.search, 34)}</span><span class="ni">${svg(I.cog, 34)}</span><span class="clock">21:40</span></div>`;
const cast = [['Nora Hale', 'Ellen Marsh'], ['Tomas Vey', 'Daniel Crane'], ['Ada Okafor', 'Ruth Abara'],
  ['Leon Brandt', 'Sheriff Cole'], ['Mina Sato', 'Kiko'], ['Rafe Dunmore', 'Pike'], ['Iris Penrose', 'Grace'],
  ['Owen Tull', 'The ferryman']];
const crew = [['Anna Reyes', 'Director'], ['Jon Feld', 'Writer'], ['Sam Ortiz', 'Writer'], ['Lena Kroll', 'Producer'],
  ['Max Ibsen', 'Cinematography'], ['Ola Grey', 'Music'], ['Bea Holm', 'Editor'], ['Kit Vance', 'Production design']];
const details = `<div class="frame f1">${nav}<div class="title">THE LAST CROSSING</div><div class="tabs"><span class="tab
  line">${svg(I.info, 28)}Details</span><span class="tab">${svg(I.extras, 28)}Extras</span><span class="tab">
  ${svg(I.more, 28)}More Like This</span></div>
  <div class="sec s1"><h3>${svg(I.people, 30)}Cast</h3><div class="strip">${cast.map(([n, r], i) => card(i, n, r,
    { focus: i === 1, initials: i === 6 ? 'IP' : '' })).join('')}</div></div>
  <div class="sec s2"><h3>${svg(I.crew, 30)}Crew</h3><div class="strip">${crew.map(([n, r], i) => card(i + 3, n, r))
    .join('')}</div></div></div>`;

const groups = [
  ['New Look', ['A new Supernova mark: the app icon, the banner and the splash',
    'One loading ring, the same spinning mark, wherever Supernova is waiting',
    'Menus and panels share one look and open beside the button you pressed',
    'One keyboard everywhere: paste from your phone with your TV’s remote app',
    'Password boxes have an eye to show what you typed']],
  ['Files', ['Network & Files is now Files, rebuilt from top to bottom',
    'Add a network share with one simple form',
    'Copy films and episodes to this device, checked once copied',
    'Copies carry on after a restart or a dropped connection',
    'Fix match, Library Health and match re-checks live in Library Care',
    'Scan one source on its own and see your last ten scans']],
  ['Details', ['Cast and crew in portrait photos, so nobody loses the top of their head',
    'Films, series, seasons and episodes each get the right tabs',
    'Tabs arrive with the page instead of popping in']],
  ['Player', ['Preparing playback shows the title, episode and age rating',
    'Skip intro sits at the end of the control row, with a 10-second bar',
    'Seek previews now work on Put.io and local files',
    'Subtitles are ready when a video starts, and for the next two episodes']],
  ['Faster And Steadier', ['Uses far less memory, so it reopens where you left it',
    'No more stutter while a scan runs in the background',
    'Focus comes back to where you were after standby',
    'Old NOVA screens no longer run behind Supernova’s']],
  ['Fixed', ['Started titles move from Recently Added to Continue Watching',
    'The Episodes row shows for every series',
    'Files deleted elsewhere are tidied away, keeping what you watched',
    'The “Scanning…” label no longer gets stuck']],
];
const notes = `<div class="pop"><b class="pt">Supernova 0.157</b><small class="ps">14 Nov 2026</small>
  ${groups.map(([g, items]) => `<div class="mg"><span>${g}</span></div><ul>${items.map(t => `<li>${t}</li>`).join('')}
  </ul>`).join('')}</div>`;
const about = `<div class="frame f2">${nav.replace('ni dull">Movies', 'ni">Movies').replace(`<span class="ni">${svg(I.cog,
  34)}</span>`, `<span class="ni dull">${svg(I.cog, 34)}</span>`)}<div class="behind"><div class="ver"><b>Supernova
  0.157</b><small>Built 14 Nov 2026 · based on NOVA 6.6.4 · Android TV 11</small></div><h3>${svg(I.notes, 30)}What’s New
  </h3>${['0.157', '0.156', '0.155', '0.154'].map((v, i) => `<div class="vr ${i === 0 ? 'came' : ''}">${svg(I.notes,
  30)}<b>${v}</b>${svg(I.chev, 28)}</div>`).join('')}</div><div class="dim"></div>${notes}</div>`;

const css = `${font(400)}${font(500)}*{box-sizing:border-box;margin:0}
body{width:1920px;font-family:R;color:#fff;background:#16181C;position:relative}
body:after{content:"";position:absolute;inset:0;border:6px solid #8a919c;z-index:99;pointer-events:none}
.frame{width:1920px;position:relative;overflow:hidden;background:#16181C}.f1{height:1200px}.f2{height:1830px;
border-top:8px solid #8a919c}
.nav{position:absolute;left:56px;right:56px;top:24px;height:76px;display:flex;align-items:center;gap:14px;
font-size:33px;color:#8f98a8;z-index:5}.nav .brand{color:#f1ead8;letter-spacing:2px;margin-right:36px;font-weight:500}
.ni{position:relative;display:inline-flex;align-items:center;height:60px;padding:0 18px}.ni.files{margin-left:auto}
.nav .clock{margin-left:18px;color:#C9D3E6}.dull{color:#C9D3E6}.dull:after{content:'';position:absolute;left:18px;
right:18px;bottom:-2px;height:4px;border-radius:2px;background:rgba(255,255,255,.3)}
.title{position:absolute;left:96px;top:130px;font-size:46px;letter-spacing:.12em;font-weight:500;color:#f1ead8}
.tabs{position:absolute;left:96px;right:96px;top:200px;display:flex;gap:10px;border-bottom:2px solid
rgba(255,255,255,.12);padding-bottom:14px}.tab{position:relative;display:inline-flex;align-items:center;gap:12px;
height:58px;padding:0 20px;font-size:29px;color:#8f98a8}.tab.line{color:#fff}.tab.line:after{content:'';
position:absolute;left:20px;right:20px;bottom:-16px;height:4px;border-radius:2px;background:#fff}
.sec{position:absolute;left:96px;right:96px}.s1{top:320px}.s2{top:770px}
h3{display:flex;align-items:center;gap:14px;font-size:30px;font-weight:500;margin-bottom:26px}h3 svg{color:#C9D3E6}
.strip{display:flex;gap:30px;align-items:flex-start}
.pc{width:180px;flex:none;text-align:center}.ph{position:relative;width:180px;height:270px;border-radius:14px;
overflow:hidden;box-shadow:inset 0 0 0 2px rgba(255,255,255,.6),inset 0 0 22px rgba(0,0,0,.45)}
.ph i{position:absolute;inset:0;display:flex;align-items:center;justify-content:center;font-style:normal;
font-size:52px;font-weight:500;color:rgba(255,255,255,.7)}
.pc b{display:block;font-size:24px;font-weight:500;margin-top:14px}.pc small{display:block;font-size:20px;
color:#C9D3E6;margin-top:4px}
.pc.focus{transform:scale(1.08);transform-origin:center top}.pc.focus .ph{box-shadow:0 0 0 6px #fff,
0 18px 40px rgba(0,0,0,.6)}
.behind{position:absolute;left:96px;right:96px;top:130px}.ver b{font-size:40px;font-weight:500;display:block}
.ver small{font-size:26px;color:#C9D3E6}.behind h3{margin-top:60px}
.vr{display:flex;align-items:center;gap:22px;height:96px;padding:0 30px;font-size:30px;border-bottom:2px solid
rgba(255,255,255,.08)}.vr svg{color:#C9D3E6}.vr svg:last-child{margin-left:auto}.vr.came{border-radius:18px;
border:none;box-shadow:inset 0 -4px 0 #fff}
.dim{position:absolute;inset:0;background:rgba(10,11,14,.6)}
.pop{position:absolute;left:50%;top:110px;transform:translateX(-50%);width:1180px;padding:44px 54px 40px;
border-radius:26px;background:rgba(22,24,28,.95);box-shadow:0 0 0 2px rgba(255,255,255,.14),0 40px 100px
rgba(0,0,0,.7)}
.pt{display:block;font-size:40px;font-weight:500}.ps{font-size:25px;color:#C9D3E6}
.mg{display:flex;align-items:center;gap:20px;margin:34px 0 10px;font-size:28px;font-weight:500}.mg:before,.mg:after{
content:'';flex:1;height:2px;background:rgba(255,255,255,.18)}
.pop ul{margin:0;padding-left:28px;font-size:27px;line-height:1.62;color:#eef1f6}`;

(async () => {
  const browser = await chromium.launch();
  const tab = await browser.newPage({ viewport: { width: 1920, height: 3038 } });
  await tab.setContent(`<html><head><style>${css}</style></head><body>${details}${about}</body></html>`,
    { waitUntil: 'load' });
  await tab.evaluate(() => document.fonts.ready);
  await tab.screenshot({ path: path.join(__dirname, '01-cast-portraits-and-0157-notes.png'), fullPage: true });
  await browser.close();
})();
