// Exploring (owner, 6 Nov 2026): Settings on the flat-page principles worked out for Files. Not tied to a version.
// Run: FONT_DIR=<roboto woff2 dir> node render.js
const fs = require('fs');
const path = require('path');
const { chromium } = require('playwright');
const fontDir = process.env.FONT_DIR;
const font = w => `@font-face{font-family:R;font-weight:${w};src:url(data:font/woff2;base64,${fs.readFileSync(
  path.join(fontDir, `roboto-latin-${w}-normal.woff2`)).toString('base64')}) format('woff2')}`;
const icon = (d, s = 34) => `<svg width="${s}" height="${s}" viewBox="0 0 24 24" fill="none" stroke="currentColor"
  stroke-width="1.7" stroke-linecap="round" stroke-linejoin="round">${d}</svg>`;
const I = {
  play: '<path d="M7 4l13 8-13 8z"/>',
  audio: '<path d="M4 9v6h4l5 4V5L8 9z"/><path d="M16 9a4 4 0 0 1 0 6M18.5 6.5a8 8 0 0 1 0 11"/>',
  library: '<rect x="3" y="4" width="5" height="16" rx="1"/><rect x="10" y="4" width="5" height="16" rx="1"/><path d="M17 5l4 14"/>',
  person: '<circle cx="12" cy="8" r="4"/><path d="M4 21a8 8 0 0 1 16 0"/>',
  system: '<rect x="3" y="5" width="18" height="12" rx="2"/><path d="M8 21h8M12 17v4"/>',
  speed: '<path d="M4 16a8 8 0 1 1 16 0"/><path d="M12 16l4-5"/>',
  pause: '<rect x="6" y="5" width="4" height="14" rx="1"/><rect x="14" y="5" width="4" height="14" rx="1"/>',
  resume: '<path d="M4 12a8 8 0 1 0 2.3-5.7L4 8.5"/><path d="M4 4v4.5h4.5"/>',
  skip: '<path d="M5 5l8 7-8 7zM14 5l6 7-6 7"/>',
  credits: '<path d="M4 6h16M4 10h16M4 14h10M4 18h7"/>',
  picture: '<rect x="3" y="5" width="18" height="14" rx="2"/><path d="M3 15l5-5 4 4 3-3 6 6"/>',
  hdr: '<circle cx="12" cy="12" r="4"/><path d="M12 2v3M12 19v3M2 12h3M19 12h3M5 5l2 2M17 17l2 2M5 19l2-2M17 7l2-2"/>',
  lang: '<path d="M4 5h9M8.5 3v2M6 5c1 4 4 7 7 8M11 5c-1 4-4 7-7 8"/><path d="M13 21l4-9 4 9M14.5 18h5"/>',
  subs: '<rect x="3" y="5" width="18" height="14" rx="2"/><path d="M7 14h4M13 14h4M7 10h10"/>',
  size: '<path d="M4 20l6-16 6 16M6.5 14h7M17 20l2.5-7 2.5 7"/>',
  surround: '<circle cx="12" cy="12" r="3"/><path d="M5 8a9 9 0 0 0 0 8M19 8a9 9 0 0 1 0 8"/>',
  eye: '<path d="M2 12s4-7 10-7 10 7 10 7-4 7-10 7S2 12 2 12z"/><circle cx="12" cy="12" r="3"/>',
  sort: '<path d="M7 4v16M3 16l4 4 4-4M17 20V4M13 8l4-4 4 4"/>',
  details: '<circle cx="12" cy="12" r="9"/><path d="M12 11v6M12 7.5h.1"/>',
  home: '<path d="M3 11l9-7 9 7v9H3z"/><path d="M10 20v-6h4v6"/>',
  history: '<circle cx="12" cy="12" r="9"/><path d="M12 7v5l3 2"/>',
  tv: '<rect x="3" y="5" width="18" height="12" rx="2"/><path d="M8 21h8M12 17v4"/>',
  trakt: '<circle cx="12" cy="12" r="9"/><path d="M8 12l3 3 5-6"/>',
  cloud: '<path d="M7 18h10a4 4 0 0 0 .6-8A6 6 0 0 0 6 9.5 4.3 4.3 0 0 0 7 18z"/>',
  key: '<circle cx="8" cy="15" r="4"/><path d="M11 12l9-9M17 6l3 3"/>',
  globe: '<circle cx="12" cy="12" r="9"/><path d="M3 12h18M12 3c3 3.5 3 14.5 0 18M12 3c-3 3.5-3 14.5 0 18"/>',
  backup: '<path d="M12 3v12M7 10l5 5 5-5"/><path d="M4 17v3h16v-3"/>',
  restore: '<path d="M12 21V9M7 14l5-5 5 5"/><path d="M4 7V4h16v3"/>',
  diag: '<path d="M3 12h4l2-5 4 10 2-5h6"/>',
  wrench: '<path d="M14 6a4 4 0 0 0 5 5l-9 9-3-3 9-9a4 4 0 0 0-2-2z"/>',
  star: '<path d="M12 3l2.7 5.6 6.1.9-4.4 4.3 1 6.1L12 17l-5.4 2.9 1-6.1-4.4-4.3 6.1-.9z"/>',
  check: '<path d="M5 12l5 5 9-10"/>',
  signin: '<path d="M10 17l5-5-5-5M15 12H3M14 4h5a2 2 0 0 1 2 2v12a2 2 0 0 1-2 2h-5"/>',
  signout: '<path d="M14 17l5-5-5-5M19 12H7M10 4H5a2 2 0 0 0-2 2v12a2 2 0 0 0 2 2h5"/>',
  sync: '<path d="M4 12a8 8 0 0 1 14-5.3M20 12a8 8 0 0 1-14 5.3"/><path d="M18 3v4h-4M6 21v-4h4"/>',
  chev: '<path d="M9 6l6 6-6 6"/>',
  search: '<circle cx="11" cy="11" r="7"/><path d="M20 20l-4-4"/>',
  cog: '<circle cx="12" cy="12" r="3"/><path d="M12 2v3M12 19v3M4.2 4.2l2.1 2.1M17.7 17.7l2.1 2.1M2 12h3M19 12h3M4.2 19.8l2.1-2.1M17.7 6.3l2.1-2.1"/>',
  flagUK: '<rect x="3" y="6" width="18" height="12" rx="1.5"/><path d="M3 6l18 12M21 6L3 18M12 6v12M3 12h18"/>',
  flagFR: '<rect x="3" y="6" width="18" height="12" rx="1.5"/><path d="M9 6v12M15 6v12"/>',
  flagDE: '<rect x="3" y="6" width="18" height="12" rx="1.5"/><path d="M3 10h18M3 14h18"/>',
  flagES: '<rect x="3" y="6" width="18" height="12" rx="1.5"/><path d="M3 9.5h18M3 14.5h18"/>',
  flagJP: '<rect x="3" y="6" width="18" height="12" rx="1.5"/><circle cx="12" cy="12" r="2.5"/>',
  orig: '<path d="M12 3l2 6h6l-5 4 2 6-5-4-5 4 2-6-5-4h6z"/>',
};
const nav = `<div class="nav"><span class="brand">SUPERNOVA</span><span>Home</span><span>Movies</span><span>TV Shows</span>
  <span>Documentaries</span><span class="sp"></span><span>Files</span><span class="ico">${icon(I.search)}</span>
  <span class="ico on">${icon(I.cog)}</span><span class="clock">15:02</span></div>`;
const TABS = [['play', 'Playback'], ['audio', 'Audio & Subtitles'], ['library', 'Library'], ['person', 'Accounts'],
  ['system', 'System']];
const head = (open, summary, badge = {}) => `<div class="tabrow"><div class="tray">${TABS.map(([k, t]) =>
  `<span class="tab ${k === open ? 'open' : ''}">${icon(I[k], 30)}${t}${badge[k] ? `<b class="tbadge">${badge[k]}</b>`
  : ''}</span>`).join('')}</div><div class="summary">${summary.map(([ic, v, l]) =>
  `<span>${icon(I[ic], 32)}<b>${v}</b> ${l}</span>`).join('<i class="dot">·</i>')}</div></div>`;
const acts = list => `<span class="acts">${list.map(([ic, t]) => `<span class="ab">${icon(I[ic], 26)}${t}</span>`)
  .join('')}</span>`;
const lead = (ic, name, line) => `<span class="ri">${icon(I[ic], 40)}</span><div class="rt grow"><div class="rn">${name}
  </div><div class="rl">${line}</div></div>`;
const chips = (ic, name, line, options, chosen, focus) => `<div class="row ${focus ? 'focus' : ''}">${lead(ic, name, line)}
  <span class="chips">${options.map(o => `<i class="${o === chosen ? 'on' : ''}">${o}</i>`).join('')}</span></div>`;
const sw = (ic, name, line, on, focus) => `<div class="row ${focus ? 'focus' : ''}">${lead(ic, name, line)}
  <span class="sw ${on ? 'on' : ''}"><i></i></span></div>`;
const value = (ic, name, line, v, focus, extra = '') => `<div class="row ${focus ? 'focus' : ''} ${extra}">
  ${lead(ic, name, line)}<span class="val">${v}</span><span class="rc">${icon(I.chev, 30)}</span></div>`;
const pillRow = (ic, name, line, pill, actions, focus) => `<div class="row ${focus ? 'focus' : ''}">
  <span class="ri">${icon(I[ic], 40)}</span><div class="rt"><div class="rn">${name}</div><div class="rl">${line}</div></div>
  <div class="rs">${pill ? `<span class="state">${pill}</span>` : ''}</div><div class="ra">${focus && actions
  ? acts(actions) : `<span class="rc">${icon(I.chev, 30)}</span>`}</div></div>`;
const sec = (t, first) => `<div class="sec ${first ? 'first' : ''}">${t}</div>`;
const page = (tab, summary, body, badge) => `${nav}<div class="page">${head(tab, summary, badge)}
  <div class="clip">${body}</div></div>`;

const playback = page('play', [['skip', '4 of 5', 'Skipped']], `
${sec('Playing', true)}
${chips('speed', 'Playback speed', 'For every video until you change it', ['0.75×', '1×', '1.25×', '1.5×'], '1×', false)}
${sw('pause', 'Hide controls when paused', 'Shows the picture without the bar over it', true, false)}
${chips('resume', 'Resume where you left off', 'When you start something you have half watched',
  ['Ask', 'Always', 'Never'], 'Ask', true)}
${sec('Skipping')}
${chips('skip', 'Intros', 'Opening titles and theme tune', ['Off', 'Button', 'Automatic'], 'Button', false)}
${chips('credits', 'Credits', 'End credits · never cuts a scene after them', ['Off', 'Button', 'Automatic'],
  'Automatic', false)}`);

const audio = page('audio', [['lang', 'English', 'Audio']], `
${sec('Audio', true)}
${value('lang', 'Preferred audio language', 'Chosen first when a video has more than one', 'English', false)}
${sw('orig', 'Prefer the original language', 'Play a film in the language it was made in', false, false)}
${chips('surround', 'Surround sound', 'Send it to your amplifier or soundbar untouched',
  ['Off', 'Passthrough', 'Virtual'], 'Passthrough', false)}
${sec('Subtitles')}
${sw('subs', 'Show subtitles', 'When a video starts', false, false)}
${value('lang', 'Preferred subtitle language', 'Chosen first when there is more than one', 'English', false,
  'open')}`.replace('</span><span class="rc">', '</span><span class="rc">') + langMenu());
function langMenu() {
  const items = [['EN', 'English', true, false], ['FR', 'French', false, true], ['DE', 'German', false, false],
    ['ES', 'Spanish', false, false], ['JA', 'Japanese', false, false], ['globe', 'More languages…', false, false]];
  const mark = c => I[c] ? icon(I[c], 30) : `<b class="code">${c}</b>`;
  return `<div class="cmenu">${items.map(([ic, n, tick, f]) => `<div class="ci ${f ? 'focus' : ''}">${mark(ic)}
    <span>${n}</span><span class="tick">${tick ? icon(I.check, 30) : ''}</span></div>`).join('')}</div>`;
}

const library = page('library', [['library', '1,808', 'Videos']], `
${sec('Library', true)}
${sw('eye', 'Hide watched titles', 'In Movies, TV Shows and your own library pages', false, false)}
${chips('sort', 'Sort titles', 'How A–Z treats “The” and “A”', ['As written', 'Ignore The and A'],
  'Ignore The and A', false)}
${sec('Details')}
${sw('details', 'Find details automatically', 'Posters, synopses and cast for new videos', true, true)}
${value('lang', 'Details language', 'Titles and synopses where available', 'English (UK)', false)}
${sec('Home and history')}
${value('home', 'Customise Home', 'Choose and order the rows on Home', '9 rows', false)}
${sw('history', 'Keep viewing history', 'Until you clear it', true, false)}`);

const accounts = page('person', [['check', '2', 'Signed In']], `
${sec('Your accounts', true)}
${pillRow('cloud', 'Put.io', 'heretostream · 98.9 of 107 GB used', 'Signed in', null, false)}
${pillRow('trakt', 'Trakt', 'Watched and progress sync · last synced 10 minutes ago', 'Signed in',
  [['sync', 'Sync now'], ['wrench', 'Options'], ['signout', 'Sign out']], true)}
${pillRow('subs', 'OpenSubtitles', 'Download subtitles from inside the player', 'Not signed in', null, false)}
${sec('Services')}
${pillRow('globe', 'Where to watch', 'Netflix, Disney+, Prime Video · United Kingdom', 'On', null, false)}
${pillRow('key', 'OMDb', 'Extra ratings on Details · needs your own key', 'Not set up', null, false)}`, { person: '1' });

const system = page('system', [['star', '0.156', 'Version']], `
${sec('Backup', true)}
${chips('credits', 'What to include', 'Everything except passwords, sources and bookmarks',
  ['Everything', 'Settings only', 'Custom'], 'Everything', true)}
${pillRow('backup', 'Back up now', 'Saves a ZIP using Android Files', '', null, false)}
${pillRow('restore', 'Restore from a backup', 'You check what it changes before anything is replaced', '', null, false)}
${sec('Look and help')}
${value('lang', 'Interface language', 'Language of Supernova’s menus and labels', 'English', false)}
${pillRow('diag', 'Diagnostics', 'Logs and a report to send when something goes wrong', 'Off', null, false)}
${pillRow('star', 'About Supernova', 'Version 0.156 · what’s new · credits and licences', '', null, false)}`);

const css = `${font(400)}${font(500)}*{box-sizing:border-box;margin:0}
body{width:1920px;height:1080px;overflow:hidden;font-family:R;color:#fff;background:#16181C;position:relative}
.nav{position:absolute;left:56px;right:56px;top:28px;height:56px;display:flex;align-items:center;gap:30px;font-size:34px;
color:#e8edf8}.nav span{display:inline-flex;align-items:center}.nav .brand{font-size:36px;margin-right:40px;letter-spacing:1px}
.nav .sp{flex:1}.nav .clock{margin-left:20px}.nav .ico.on{background:#F4F6FB;color:#0B0D12;border-radius:50%;padding:8px;
margin:-8px}
.page{position:absolute;left:96px;right:96px;top:150px;bottom:0}
.clip{position:absolute;left:0;right:0;top:136px;bottom:0;overflow:hidden}
.tabrow{display:flex;align-items:center;justify-content:space-between;gap:56px;padding-bottom:34px;
border-bottom:2px solid rgba(255,255,255,.10)}
.tray{display:inline-flex;gap:8px;padding:7px;border-radius:31px;background:#22252B;
box-shadow:inset 0 0 0 2px rgba(255,255,255,.10)}
.tab{height:76px;padding:0 28px;border-radius:24px;display:inline-flex;align-items:center;gap:16px;font-size:32px;
font-weight:500;color:#dfe5f1;white-space:nowrap}.tab.open{background:#454B55;color:#fff}
.summary{display:flex;align-items:center;gap:14px;font-size:32px;color:#C9D3E6;white-space:nowrap}
.summary span{display:inline-flex;align-items:center;gap:12px}.summary b{color:#fff;font-weight:500}
.summary .dot{font-style:normal;color:#6b7383}
.tbadge{margin-left:10px;min-width:34px;height:34px;padding:0 10px;border-radius:17px;background:#F4F6FB;color:#0B0D12;
font-size:22px;display:inline-flex;align-items:center;justify-content:center}
.sec{font-size:28px;font-weight:500;color:#B7BFCC;margin:30px 0 8px;letter-spacing:.3px}.sec.first{margin-top:22px}
.row{position:relative;display:flex;align-items:center;gap:30px;min-height:120px;padding:16px 34px;
box-shadow:inset 0 -2px 0 rgba(255,255,255,.08)}
.row.focus{background:#F4F6FB;color:#0B0D12;border-radius:24px;box-shadow:none}.row.focus .rl{color:#3A4256}
.ri{color:#c9d3e6;width:44px}.row.focus .ri{color:#0B0D12}
.rt{width:620px;min-width:0;flex-shrink:0}.rt.grow{width:auto;flex:1}
.rn{font-size:36px;font-weight:500;white-space:nowrap;overflow:hidden;text-overflow:ellipsis}
.rl{font-size:27px;color:#C9D3E6;margin-top:6px;white-space:nowrap;overflow:hidden;text-overflow:ellipsis}
.rs{width:240px;flex-shrink:0;display:flex;justify-content:center}.ra{flex:1;display:flex;justify-content:flex-end}
.rc{color:rgba(255,255,255,.35)}.row.focus .rc{color:#0B0D12}
.val{font-size:30px;color:#fff}.row.focus .val{color:#0B0D12}
.state{white-space:nowrap;display:inline-flex;align-items:center;justify-content:center;width:230px;height:48px;
border-radius:24px;font-size:26px;font-weight:500;color:#e8edf6;box-shadow:inset 0 0 0 2px rgba(255,255,255,.30)}
.row.focus .state{color:#0B0D12;box-shadow:inset 0 0 0 2px rgba(11,13,18,.35)}
.acts{display:flex;gap:14px}.ab{white-space:nowrap;height:64px;padding:0 22px;border-radius:20px;background:#dde1ea;
color:#0B0D12;display:inline-flex;align-items:center;gap:12px;font-size:26px;font-weight:500}
.ab:first-child{background:#0B0D12;color:#fff}
.chips{display:flex;gap:6px;font-size:28px;white-space:nowrap}.chips i{font-style:normal;padding:10px 20px;border-radius:14px;
color:#dfe5f1}.chips i.on{background:#454B55;color:#fff}.row.focus .chips i{color:#3A4256}
.row.focus .chips i.on{background:#0B0D12;color:#fff}
.sw{width:76px;height:40px;border-radius:20px;background:#3a3f48;position:relative;flex-shrink:0}
.sw i{position:absolute;top:4px;left:4px;width:32px;height:32px;border-radius:50%;background:#fff}
.sw.on{background:#cfd6e2}.sw.on i{left:40px;background:#16181C}.row.focus .sw{background:#b9c0cc}
.row.focus .sw.on{background:#0B0D12}.row.focus .sw.on i{background:#fff}
.cmenu{position:absolute;right:96px;bottom:150px;border:2px solid rgba(255,255,255,.28);background:rgba(22,24,28,.97);
border-radius:26px;padding:16px;z-index:5;box-shadow:0 20px 60px rgba(0,0,0,.5);white-space:nowrap}
.ci{display:flex;align-items:center;gap:18px;height:68px;padding:0 18px;border-radius:18px;font-size:29px;font-weight:500}

.code{width:44px;height:30px;border-radius:7px;box-shadow:inset 0 0 0 2px currentColor;font-size:17px;display:inline-flex;
align-items:center;justify-content:center}.row.open{background:rgba(255,255,255,.08);border-radius:24px;box-shadow:none}
.ci.focus{background:#F4F6FB;color:#0B0D12}.ci .tick{margin-left:auto;padding-left:40px;width:70px;display:inline-flex;
justify-content:flex-end}`;
(async () => {
  const browser = await chromium.launch();
  const tab = await browser.newPage({ viewport: { width: 1920, height: 1080 } });
  const pages = [['01-playback.png', playback], ['02-audio-subtitles-menu.png', audio], ['03-library.png', library],
    ['04-accounts.png', accounts], ['05-system.png', system]];
  for (const [f, body] of pages) {
    await tab.setContent(`<html><head><style>${css}</style></head><body>${body}</body></html>`, { waitUntil: 'load' });
    await tab.evaluate(() => document.fonts.ready);
    await tab.screenshot({ path: path.join(__dirname, f) });
  }
  await browser.close();
})();
