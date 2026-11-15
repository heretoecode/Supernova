// Users and tags v3, the whole flow (owner, 15 Nov 2026; pinned for 0.158, in discussion). Thirteen full 1080p screens
// shown at half size, two by seven, read left to right:
// 1 Home, user switcher open · 2 Edit users and views · 3 Edit a user (name, icon, what it hides, default, delete)
// 4 Icon picker (the one app-wide picker, with a new People and Fun group) · 5 Name with the one keyboard
// 6 Delete a user (warning) · 7 Default user on this device · 8 Details hero with its tags on the card
// 9 Details › More › Tags (Users and Tags) · 10 New tag with the keyboard · 11 Library select mode (many titles)
// 12 Tag the selected titles · 13 Library Filters with Users and Tags (Off › Hide › Only).
// Made-up names and titles. Light frame = picture edge. Run: FONT_DIR=<roboto woff2 dir> node render-v3.js
const fs = require('fs');
const path = require('path');
const { chromium } = require('playwright');
const fontDir = process.env.FONT_DIR;
const font = w => `@font-face{font-family:R;font-weight:${w};src:url(data:font/woff2;base64,${fs.readFileSync(
  path.join(fontDir, `roboto-latin-${w}-normal.woff2`)).toString('base64')}) format('woff2')}`;
const svg = (d, s = 30, w = 1.8) => `<svg width="${s}" height="${s}" viewBox="0 0 24 24" fill="none" stroke="currentColor"
  stroke-width="${w}" stroke-linecap="round" stroke-linejoin="round">${d}</svg>`;
const I = {
  search: '<circle cx="11" cy="11" r="7"/><path d="M20 20l-4-4"/>',
  cog: '<circle cx="12" cy="12" r="3"/><path d="M12 2v3M12 19v3M4.2 4.2l2.1 2.1M17.7 17.7l2.1 2.1M2 12h3M19 12h3'
    + 'M4.2 19.8l2.1-2.1M17.7 6.3l2.1-2.1"/>',
  all: '<rect x="3.5" y="3.5" width="7" height="7" rx="1.5"/><rect x="13.5" y="3.5" width="7" height="7" rx="1.5"/>'
    + '<rect x="3.5" y="13.5" width="7" height="7" rx="1.5"/><rect x="13.5" y="13.5" width="7" height="7" rx="1.5"/>',
  both: '<circle cx="9" cy="8" r="3.5"/><path d="M2.5 20a6.5 6.5 0 0 1 13 0"/><path d="M15 4.6a3.5 3.5 0 1 1 0 6.8"/>'
    + '<path d="M17.5 14.2A6.5 6.5 0 0 1 21.5 20"/>',
  rocket: '<path d="M12 2.5c3.5 2.5 5 6 4.5 10L14.5 16h-5l-2-3.5C7 8.5 8.5 5 12 2.5z"/><circle cx="12" cy="9.5" r="2"/>'
    + '<path d="M9.5 16L7 21l3.5-2M14.5 16l2.5 5-3.5-2"/>',
  flower: '<circle cx="12" cy="8" r="2"/><circle cx="12" cy="4" r="2"/><circle cx="15.5" cy="6.5" r="2"/>'
    + '<circle cx="14.5" cy="10.5" r="2"/><circle cx="9.5" cy="10.5" r="2"/><circle cx="8.5" cy="6.5" r="2"/>'
    + '<path d="M12 12.5V22M12 18c-2.5 0-4-1.5-4.5-3.5M12 16.5c2.5 0 4-1.5 4.5-3.5"/>',
  cat: '<path d="M5 20V8l3 3h8l3-3v12z"/><circle cx="9.5" cy="15" r=".8"/><circle cx="14.5" cy="15" r=".8"/>',
  heart: '<path d="M12 20s-8-4.5-8-10.5A4.5 4.5 0 0 1 12 7a4.5 4.5 0 0 1 8 2.5C20 15.5 12 20 12 20z"/>',
  music: '<path d="M9 18V5l11-2v13"/><circle cx="6.5" cy="18" r="2.5"/><circle cx="17.5" cy="16" r="2.5"/>',
  game: '<rect x="2.5" y="7" width="19" height="11" rx="5"/><path d="M7 10.5v4M5 12.5h4"/><circle cx="16" cy="11.5"'
    + ' r=".9"/><circle cx="18" cy="13.5" r=".9"/>',
  sun: '<circle cx="12" cy="12" r="4"/><path d="M12 2v2.5M12 19.5V22M2 12h2.5M19.5 12H22M4.9 4.9l1.8 1.8M17.3 17.3l1.8'
    + ' 1.8M4.9 19.1l1.8-1.8M17.3 6.7l1.8-1.8"/>',
  moon: '<path d="M20 14.5A8 8 0 1 1 9.5 4a6.5 6.5 0 0 0 10.5 10.5z"/>',
  leaf: '<path d="M5 19c0-9 6-14 15-14 0 9-5 15-14 15"/><path d="M5 19l8-8"/>',
  ghost: '<path d="M5 21V10a7 7 0 0 1 14 0v11l-2.5-2-2.5 2-2-2-2 2-2.5-2z"/><circle cx="9.5" cy="10.5" r=".9"/>'
    + '<circle cx="14.5" cy="10.5" r=".9"/>',
  crown: '<path d="M3 8l4.5 4L12 5l4.5 7L21 8l-2 11H5z"/>',
  ball: '<circle cx="12" cy="12" r="9"/><path d="M12 7l4 3-1.5 4.5h-5L8 10z"/>',
  anchor: '<circle cx="12" cy="5" r="2"/><path d="M12 7v14M5 13a7 7 0 0 0 14 0M9 10h6"/>',
  paw: '<circle cx="7" cy="10" r="1.8"/><circle cx="11" cy="6.5" r="1.8"/><circle cx="15.5" cy="7.5" r="1.8"/>'
    + '<circle cx="18" cy="11.5" r="1.8"/><path d="M8.5 18c0-3 2-5.5 4.5-5.5s4 2 4 4.5-2 2.5-4 1.5-4 1.5-4.5-.5z"/>',
  edit: '<path d="M4 20h4L19 9l-4-4L4 16z"/><path d="M14 6l4 4"/>',
  tick: '<path d="M5 12.5l4.5 4.5L19 7.5"/>',
  chev: '<path d="M9 6l6 6-6 6"/>',
  tag: '<path d="M3 12V4h8l10 10-8 8z"/><circle cx="7.5" cy="8.5" r="1.5"/>',
  tree: '<path d="M12 2.5l5.5 6.5h-3.5l4.5 5.5h-3.5l3.5 4.5H5.5L9 14.5H5.5L10 9H6.5z"/><path d="M12 19v3"/>',
  star: '<path d="M12 3l2.7 5.6 6.1.9-4.4 4.3 1 6.1L12 17l-5.4 2.9 1-6.1L3.2 9.5l6.1-.9z"/>',
  plus: '<path d="M12 5v14M5 12h14"/>',
  play: '<path d="M7 4l13 8-13 8z"/>',
  dots: '<circle cx="5" cy="12" r="1.3"/><circle cx="12" cy="12" r="1.3"/><circle cx="19" cy="12" r="1.3"/>',
  restart: '<path d="M4 12a8 8 0 1 0 2.4-5.7"/><path d="M4 4v4h4"/>',
  watched: '<circle cx="12" cy="12" r="9"/><path d="M8 12.5l3 3 5-6"/>',
  fix: '<circle cx="11" cy="11" r="7"/><path d="M20 20l-4-4M8.5 11h5M11 8.5v5"/>',
  remove: '<path d="M5 7h14M10 7V4h4v3M7 7l1 13h8l1-13"/>',
  art: '<rect x="3" y="5" width="18" height="14" rx="2"/><circle cx="9" cy="10" r="2"/><path d="M21 16l-5-5-8 8"/>',
  name: '<path d="M4 7h16M4 12h10M4 17h7"/>',
  hide: '<path d="M3 3l18 18M10.6 6.2A9.6 9.6 0 0 1 12 6c6.4 0 10 6 10 6a17 17 0 0 1-3.3 3.9M6.6 7.6C3.8 9.3 2 12 2'
    + ' 12s3.6 6 10 6c1.7 0 3.2-.4 4.5-1"/>',
  home: '<path d="M4 11l8-7 8 7v9H4z"/><path d="M10 20v-5h4v5"/>',
  warn: '<path d="M12 4l9 16H3z"/><path d="M12 10v4M12 17h0"/>',
  select: '<rect x="4" y="4" width="16" height="16" rx="3"/><path d="M8 12.5l3 3 5-6"/>',
  filter: '<path d="M3 5h18l-7 8v6l-4 2v-8z"/>',
  back: '<path d="M10 6l-6 6 6 6M4 12h16"/>',
  space: '<path d="M4 10v4h16v-4"/>',
};
const nav = (o = {}) => `<div class="nav"><span class="user ${o.focus ? 'focus' : o.dull ? 'dull' : ''}">${svg(I[o.icon ||
  'rocket'], 36, 2)}</span><span class="ni ${o.home || ''}">Home</span><span class="ni ${o.movies || ''}">Movies</span>
  <span class="ni">TV Shows</span><span class="ni">Documentaries</span><span class="ni files">Files</span>
  <span class="ni">${svg(I.search, 34)}</span><span class="ni">${svg(I.cog, 34)}</span><span class="clock">21:40</span></div>`;
const mi = (ic, t, sub, o = {}) => `<div class="mi ${o.focus ? 'focus' : ''}">${ic ? svg(I[ic], 32, 2) : ''}<div class="mt">
  <b>${t}</b>${sub ? `<small>${sub}</small>` : ''}</div><span class="end">${o.end || ''}${o.tick ? svg(I.tick, 28) : ''}
  ${o.chev ? svg(I.chev, 26) : ''}</span></div>`;
const grp = t => `<div class="mg"><span>${t}</span></div>`;
const foot = (ic, t, o = {}) => `<div class="sep"></div>${mi(ic, t, o.sub || '', o)}`;
const chip = t => `<span class="chip">${t}</span>`;
const st = t => `<span class="st ${t.toLowerCase()}">${t}</span>`;
const cards = (n, w = 400, h = 225) => Array.from({ length: n }, (_, i) => `<div class="card" style="width:${w}px;
  height:${h}px;background:linear-gradient(150deg,${['#4b5a4e', '#6b4a42', '#4d4766', '#6a6040', '#3f5f62'][i % 5]},
  #1f2228)"></div>`).join('');
const heroArt = 'linear-gradient(100deg,rgba(14,16,20,.92),rgba(14,16,20,.3) 60%),radial-gradient(ellipse at 72% 45%,'
  + '#6a5a48,#2a2620 75%)';
const homeBg = (navo) => `${nav(navo)}<div class="hero" style="background:${heroArt}"><div class="hl">SNOWED IN</div>
  <div class="hm">2024 · 1 h 41 min · Comedy, Family</div></div><div class="rowh">Continue Watching</div>
  <div class="row">${cards(5)}</div>`;
const frame = (inner) => `<div class="frame">${inner}</div>`;
const userMenu = (dim) => `<div class="menu um ${dim ? 'behind' : ''}">${mi('all', 'Everything', 'Nothing hidden')}
  ${mi('rocket', 'Me', 'Hides Alex’s titles and Classics', { tick: true })}${mi('flower', 'Alex', 'Hides Me’s titles')}
  ${mi('both', 'Together', 'Only titles tagged Together')}${foot('edit', 'Edit users and views', { focus: !dim })}</div>`;

const f1 = frame(`${homeBg({ focus: true })}${userMenu(false)}`);
const usersPanel = (dim, focusAdd) => `<div class="panel side ${dim ? 'behind' : ''}">${grp('Users')}
  ${mi('rocket', 'Me', 'Default on this device', { chev: true, focus: !dim && !focusAdd })}
  ${mi('flower', 'Alex', '', { chev: true })}${mi('both', 'Together', '', { chev: true })}
  ${mi('all', 'Everything', 'Always there; can’t be changed', {})}${mi('plus', 'Add a user', '', { focus: focusAdd })}
  ${grp('This Device')}${mi('home', 'Open Supernova as', '', { end: '<span class="val">Me</span>', chev: true })}</div>`;
const f2 = frame(`${homeBg({ dull: true })}${userMenu(true)}${usersPanel(false)}`);
const userEdit = (dim, focus) => `<div class="panel side2 ${dim ? 'behind' : ''}">${mi('name', 'Name', '', { end:
  '<span class="val">Me</span>', chev: true, focus: focus === 'name' })}${mi('rocket', 'Icon', '', { chev: true, focus:
  focus === 'icon' })}${grp('Hidden When Me Is On')}${mi('flower', 'Alex’s titles', '', { end: st('Hide') })}
  ${mi('both', 'Together', '', { end: st('Off') })}${mi('star', 'Classics', '', { end: st('Hide') })}
  ${mi('tree', 'Christmas', '', { end: st('Off') })}${foot('remove', 'Delete Me', { focus: focus === 'delete' })}</div>`;
const f3 = frame(`${homeBg({ dull: true })}${usersPanel(true)}${userEdit(false, 'icon')}`);
const pick = (ic, o = {}) => `<span class="ic ${o.focus ? 'focus' : ''} ${o.on ? 'on' : ''}">${svg(I[ic], 46, 2)}</span>`;
const picker = `<div class="panel picker">${grp('People And Fun')}<div class="icg">${['both', 'rocket', 'flower', 'cat',
  'paw', 'heart', 'music', 'game', 'sun', 'moon', 'leaf', 'ghost', 'crown', 'ball', 'anchor', 'star'].map(i => pick(i, {
  focus: i === 'cat', on: i === 'rocket' })).join('')}</div>${grp('Genres')}<div class="icg">${['tree', 'heart', 'star',
  'ghost', 'music', 'ball', 'sun', 'leaf'].map(i => pick(i)).join('')}</div></div>`;
const f4 = frame(`${homeBg({ dull: true })}${userEdit(true)}${picker}`);
const key = (k, o = {}) => `<span class="k ${o.focus ? 'focus' : ''} ${o.wide ? 'wide' : ''}">${k}</span>`;
const keyboard = (typed, cls) => `<div class="panel kb ${cls}"><div class="typed">${typed}<i></i></div><div class="keys">
  ${'abcdefghijklmnopqrstuvwxyz'.split('').map(c => key(c, { focus: c === 't' })).join('')}${key(svg(I.space, 30), {
  wide: true })}${key('⌫')}${key('Done', { wide: true })}</div></div>`;
const f5 = frame(`${homeBg({ dull: true })}${userEdit(true)}${keyboard('Me', 'kbuser')}`);
const warn = `<div class="dim"></div><div class="panel warn"><div class="wt">${svg(I.warn, 34)}<b>Delete Me?</b></div>
  <p>The user and their view go. Their tag comes off every title; the titles stay in your library.</p>
  <div class="wb"><span class="wbtn focus">Keep Me</span><span class="wbtn">Delete</span></div></div>`;
const f6 = frame(`${homeBg({ dull: true })}${userEdit(true)}${warn}`);
const defList = `<div class="panel side3">${mi('all', 'Everything', '')}${mi('rocket', 'Me', '', { tick: true, focus:
  true })}${mi('flower', 'Alex', '')}${mi('both', 'Together', '')}</div>`;
const f7 = frame(`${homeBg({ dull: true })}${usersPanel(true)}${defList}`);
const detailsHero = (navo) => `${nav(navo)}<div class="hero tall" style="background:${heroArt}"><div class="hl">SNOWED IN
  </div><div class="hm">PG · 2024 · 1 h 41 min · Comedy, Family</div><div class="tagsl"><span>${svg(I.both, 26, 2)}
  Together</span><span>${svg(I.tree, 26, 2)}Christmas</span></div><div class="hd">A family snowed in at a hotel on
  Christmas Eve sets out to save the staff party.</div><div class="hb"><span class="btn focus">${svg(I.play, 26)}Play
  </span><span class="btn">${svg(I.dots, 26)}More</span></div></div>`;
const f8 = frame(detailsHero({ movies: 'dull' }));
const more = (dim) => `<div class="menu more ${dim ? 'behind' : ''}">${grp('Playback')}${mi('restart',
  'Play from the start', '')}${grp('Library')}${mi('watched', 'Mark as watched', '')}${mi('tag', 'Tags',
  'Together · Christmas', { focus: !dim })}${mi('fix', 'Fix match', '')}${mi('remove', 'Remove from library', '')}
  ${grp('Files &amp; Media')}${mi('art', 'Artwork', '')}</div>`;
const tagList = (dim, extra) => `<div class="menu tags ${dim ? 'behind' : ''}">${grp('Users')}${mi('rocket', 'Me', '')}
  ${mi('flower', 'Alex', '')}${mi('both', 'Together', '', { tick: true })}${grp('Tags')}${mi('tree', 'Christmas', '',
  { tick: true })}${mi('star', 'Classics', '', { tick: !!extra })}${extra ? '' : ''}${foot('plus', 'New tag', { focus:
  !dim })}</div>`;
const f9 = frame(`${detailsHero({ movies: 'dull' })}${more(true)}${tagList(false)}`);
const f10 = frame(`${detailsHero({ movies: 'dull' })}${more(true)}${tagList(true)}${keyboard('Comfort fi', 'kbtag')}`);
const poster = (i, on) => `<div class="pst ${on ? 'sel' : ''} ${i === 2 ? 'focus' : ''}" style="background:linear-gradient(
  150deg,${['#4b5a4e', '#6b4a42', '#4d4766', '#6a6040', '#3f5f62'][i % 5]},#1f2228)">${on ? `<i>${svg(I.tick, 30, 2.6)}
  </i>` : ''}</div>`;
const selectBar = `<div class="selbar"><b>12 selected</b><span class="tool">${svg(I.select, 28)}Select all</span>
  <span class="tool">${svg(I.tag, 28)}Tag selected</span><span class="tool">${svg(I.tick, 28)}Done</span></div>`;
const libGrid = (sel) => `<div class="pgrid">${Array.from({ length: 12 }, (_, i) => poster(i, sel && i % 3 !== 1))
  .join('')}</div>`;
const f11 = frame(`${nav({ movies: 'dull' })}${selectBar}${libGrid(true)}`);
const tagMany = `<div class="panel side4">${grp('Users')}${mi('rocket', 'Me', '', { tick: true, focus: true })}
  ${mi('flower', 'Alex', '')}${mi('both', 'Together', '')}${grp('Tags')}${mi('tree', 'Christmas', '')}
  ${mi('star', 'Classics', '')}${foot('plus', 'New tag')}<div class="apply">Tag 12 titles</div></div>`;
const f12 = frame(`${nav({ movies: 'dull' })}${selectBar}${libGrid(true)}<div class="dim"></div>${tagMany}`);
const filters = `<div class="panel filt">${grp('Watched')}${mi('watched', 'Watched', '', { end: '<span class="val">All'
  + '</span>' })}${grp('Users')}${mi('flower', 'Alex', '', { end: st('Hide'), focus: true })}${mi('both', 'Together', '',
  { end: st('Off') })}${grp('Tags')}${mi('tree', 'Christmas', '', { end: st('Off') })}${mi('star', 'Classics', '',
  { end: st('Only') })}<p class="hint">OK changes Off › Hide › Only</p></div>`;
const f13 = frame(`${nav({ movies: 'dull' })}<div class="toolbar"><span class="tool tf">${svg(I.filter, 28)}Filters</span>
  </div>${libGrid(false)}${filters}`);

const css = `${font(400)}${font(500)}*{box-sizing:border-box;margin:0}
body{width:1920px;font-family:R;color:#fff;background:#8a919c;position:relative}
body:after{content:"";position:absolute;inset:0;border:6px solid #8a919c;z-index:99;pointer-events:none}
.grid{display:grid;grid-template-columns:960px 960px}.cell{width:960px;height:540px;overflow:hidden;border:4px solid #8a919c}
.frame{width:1920px;height:1080px;position:relative;overflow:hidden;background:#16181C;transform:scale(.4958);
transform-origin:0 0}
.nav{position:absolute;left:56px;right:56px;top:24px;height:76px;display:flex;align-items:center;gap:14px;
font-size:33px;color:#8f98a8;z-index:5}
.user{width:68px;height:68px;border-radius:50%;display:inline-flex;align-items:center;justify-content:center;color:#fff;
box-shadow:inset 0 0 0 2px rgba(255,255,255,.3);margin-right:26px}.user.focus{background:#F4F6FB;color:#0B0D12;
box-shadow:none}.user.dull{box-shadow:inset 0 0 0 2px rgba(255,255,255,.3),0 6px 0 -2px rgba(255,255,255,.3)}
.ni{position:relative;display:inline-flex;align-items:center;height:60px;padding:0 18px}.ni.files{margin-left:auto}
.nav .clock{margin-left:18px;color:#C9D3E6}.dull{color:#C9D3E6}.ni.dull:after{content:'';position:absolute;left:18px;
right:18px;bottom:-2px;height:4px;border-radius:2px;background:rgba(255,255,255,.3)}
.hero{position:absolute;left:72px;top:118px;width:1776px;height:640px;border-radius:26px;box-shadow:inset 0 0 0 2px
rgba(255,255,255,.6)}.hero.tall{height:820px}
.hl{position:absolute;left:60px;top:200px;font-size:76px;letter-spacing:8px;font-weight:500;color:#f1ead8}
.hm{position:absolute;left:62px;top:300px;font-size:28px;color:#e8edf6}
.tagsl{position:absolute;left:62px;top:350px;display:flex;gap:14px}.tagsl span{display:inline-flex;align-items:center;
gap:10px;font-size:25px;padding:6px 16px;border-radius:16px;background:rgba(22,24,28,.7);box-shadow:inset 0 0 0 2px
rgba(255,255,255,.3)}
.hd{position:absolute;left:62px;top:416px;width:900px;font-size:27px;line-height:1.45;color:#e8edf6}
.hb{position:absolute;left:60px;bottom:60px;display:flex;gap:16px}.btn{display:inline-flex;align-items:center;gap:12px;
height:68px;padding:0 30px;border-radius:34px;font-size:28px;font-weight:500;background:rgba(22,24,28,.72)}
.btn.focus{background:#F4F6FB;color:#0B0D12}
.rowh{position:absolute;left:96px;top:800px;font-size:30px;font-weight:500}.row{position:absolute;left:96px;top:852px;
display:flex;gap:24px}.card{border-radius:14px;box-shadow:inset 0 0 0 2px rgba(255,255,255,.6)}
.menu,.panel{position:absolute;width:max-content;padding:14px;border-radius:24px;background:rgba(22,24,28,.95);
box-shadow:0 0 0 2px rgba(255,255,255,.14),0 30px 80px rgba(0,0,0,.7);z-index:6}
.um{left:44px;top:100px}.side{right:56px;top:110px;width:640px}.side2{right:120px;top:170px;width:640px;z-index:7}
.side3{right:150px;top:560px;z-index:7}.picker{right:150px;top:150px;z-index:7;width:700px}
.kbuser{right:740px;top:130px;z-index:8}.kbtag{left:860px;top:300px;z-index:8}
.more{left:300px;top:250px}.tags{left:560px;top:300px;z-index:7}.side4{right:56px;top:150px;width:600px;z-index:7}
.filt{left:96px;top:196px;width:640px}.warn{left:50%;top:50%;transform:translate(-50%,-50%);width:760px;padding:40px;
z-index:8}
.behind{transform:scale(.96);transform-origin:right top;filter:brightness(.45)}.um.behind{transform-origin:left top}
.more.behind{transform-origin:left top}.tags.behind{transform-origin:left top}
.mi{display:flex;align-items:center;gap:20px;height:84px;padding:0 24px;border-radius:18px;min-width:480px}
.mi svg{color:#C9D3E6;flex:none}.mt b{display:block;font-size:29px;font-weight:500}.mt small{font-size:22px;color:#C9D3E6}
.mi .end{margin-left:auto;padding-left:24px;display:flex;align-items:center;gap:12px}.val{font-size:27px;font-weight:500}
.mi.focus{background:#F4F6FB;color:#0B0D12}.mi.focus svg,.mi.focus small{color:#0B0D12}
.sep{height:2px;background:rgba(255,255,255,.12);margin:8px 24px}
.mg{display:flex;align-items:center;gap:18px;margin:12px 24px 6px;font-size:26px;font-weight:500}.mg:before,.mg:after{
content:'';flex:1;height:2px;background:rgba(255,255,255,.16)}
.st{font-size:24px;padding:4px 14px;border-radius:14px;box-shadow:inset 0 0 0 2px rgba(255,255,255,.35)}
.st.off{box-shadow:none;color:#C9D3E6;padding:4px 0}.mi.focus .st{box-shadow:inset 0 0 0 2px rgba(11,13,18,.4)}
.icg{display:grid;grid-template-columns:repeat(8,72px);gap:12px;padding:8px 24px 14px}.ic{width:72px;height:72px;
border-radius:16px;display:flex;align-items:center;justify-content:center;color:#e8edf6;box-shadow:inset 0 0 0 2px
rgba(255,255,255,.12)}.ic.on{box-shadow:inset 0 0 0 3px rgba(255,255,255,.7)}.ic.focus{background:#F4F6FB;color:#0B0D12}
.kb{width:820px;padding:22px}.typed{height:72px;border-radius:14px;background:rgba(255,255,255,.08);font-size:32px;
display:flex;align-items:center;padding:0 22px}.typed i{width:3px;height:36px;background:#fff;margin-left:2px}
.keys{display:grid;grid-template-columns:repeat(9,1fr);gap:10px;margin-top:16px}.k{height:66px;border-radius:12px;
display:flex;align-items:center;justify-content:center;font-size:28px;background:rgba(255,255,255,.06)}
.k.wide{grid-column:span 3}.k.focus{background:#F4F6FB;color:#0B0D12}
.dim{position:absolute;inset:0;background:rgba(10,11,14,.6);z-index:7}
.wt{display:flex;align-items:center;gap:16px;font-size:34px;font-weight:500}.wt svg{color:#fff}.warn p{font-size:27px;
line-height:1.45;color:#C9D3E6;margin:18px 0 30px}.wb{display:flex;gap:16px}.wbtn{height:72px;padding:0 34px;
border-radius:36px;display:inline-flex;align-items:center;font-size:28px;font-weight:500;background:rgba(255,255,255,.1)}
.wbtn.focus{background:#F4F6FB;color:#0B0D12}
.selbar{position:absolute;left:96px;right:96px;top:128px;display:flex;align-items:center;gap:26px;font-size:28px}
.tool{display:inline-flex;align-items:center;gap:10px;font-size:27px;padding:10px 18px;border-radius:14px}
.tool svg{color:#C9D3E6}.tf{background:rgba(255,255,255,.14)}.toolbar{position:absolute;left:96px;top:128px}
.pgrid{position:absolute;left:96px;top:210px;display:grid;grid-template-columns:repeat(6,250px);gap:30px}
.pst{position:relative;width:250px;height:375px;border-radius:14px;box-shadow:inset 0 0 0 2px rgba(255,255,255,.6)}
.pst.focus{box-shadow:0 0 0 6px #fff}.pst i{position:absolute;right:12px;top:12px;width:48px;height:48px;border-radius:50%;
background:#F4F6FB;color:#0B0D12;display:flex;align-items:center;justify-content:center}
.pst:not(.sel){opacity:.55}.apply{margin:14px 24px 6px;height:76px;border-radius:38px;background:rgba(255,255,255,.12);
display:flex;align-items:center;justify-content:center;font-size:28px;font-weight:500}
.hint{font-size:22px;color:#9aa3b4;margin:12px 24px 0}`;
(async () => {
  const browser = await chromium.launch();
  const tab = await browser.newPage({ viewport: { width: 1920, height: 3780 }, deviceScaleFactor: 2 });
  const cells = [f1, f2, f3, f4, f5, f6, f7, f8, f9, f10, f11, f12, f13].map(f => `<div class="cell">${f}</div>`).join('');
  await tab.setContent(`<html><head><style>${css}</style></head><body><div class="grid">${cells}</div></body></html>`,
    { waitUntil: 'load' });
  await tab.evaluate(() => document.fonts.ready);
  await tab.screenshot({ path: path.join(__dirname, '03-v3-whole-flow.png'), fullPage: true });
  await browser.close();
})();
