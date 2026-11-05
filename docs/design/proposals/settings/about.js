// Proposal (0.156): Settings › System in the approved single-panel pattern. Run: FONT_DIR=... node audio.js
const fs = require('fs'), path = require('path'), { chromium } = require('playwright');
const out = __dirname, fontDir = process.env.FONT_DIR;
const font = w => `@font-face{font-family:R;font-weight:${w};src:url(data:font/woff2;base64,${fs.readFileSync(path.join(fontDir, `roboto-latin-${w}-normal.woff2`)).toString('base64')}) format('woff2')}`;
const P = { audio: '<path d="M4 9h4l5-4v14l-5-4H4z"/><path d="M16 9a4 4 0 0 1 0 6"/>', cc: '<rect x="3" y="5" width="18" height="14" rx="2"/><path d="M10 10.2a2.2 2.2 0 1 0 0 3.6M16.5 10.2a2.2 2.2 0 1 0 0 3.6"/>',
  style: '<path d="M4 20h4L19 9l-4-4L4 16z"/>', font: '<path d="M5 19L11 5h2l6 14M8 14h8"/>', chev: '<path d="M9 6l6 6-6 6"/>', check: '<path d="M5.5 12.5l4 4L18.5 8"/>',
  search: '<circle cx="11" cy="11" r="6.5"/><path d="M16 16l4.5 4.5"/>', gear: '<circle cx="12" cy="12" r="3.2"/><path d="M12 2.5v3M12 18.5v3M2.5 12h3M18.5 12h3M5.3 5.3l2.1 2.1M16.6 16.6l2.1 2.1M5.3 18.7l2.1-2.1M16.6 7.4l2.1-2.1"/>',
  folder: '<path d="M3 6.5A1.5 1.5 0 0 1 4.5 5H9l2 2.5h8.5A1.5 1.5 0 0 1 21 9v8.5a1.5 1.5 0 0 1-1.5 1.5h-15A1.5 1.5 0 0 1 3 17.5z"/>', x: '<path d="M6 6l12 12M18 6L6 18"/>' };
const ic = (k, s = 28) => `<svg width="${s}" height="${s}" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round">${P[k]}</svg>`;
const nav = `<div class="nav"><span class="brand">SUPERNOVA</span><span>Home</span><span>Movies</span><span>TV Shows</span><span>Documentaries</span><span class="sp"></span><span>Network &amp; Files</span><span class="ic">${ic('search', 34)}</span><span class="ic on">${ic('gear', 34)}</span><span class="clock">17:40</span></div>`;
// simple flag drawings (stand-ins for the app's flag set)
const flag = c => ({ gb: '<rect width="36" height="24" fill="#1f3a93"/><path d="M0 0L36 24M36 0L0 24" stroke="#fff" stroke-width="5"/><path d="M0 0L36 24M36 0L0 24" stroke="#c8102e" stroke-width="2"/><path d="M18 0v24M0 12h36" stroke="#fff" stroke-width="7"/><path d="M18 0v24M0 12h36" stroke="#c8102e" stroke-width="4"/>',
  fr: '<rect width="12" height="24" fill="#0055a4"/><rect x="12" width="12" height="24" fill="#fff"/><rect x="24" width="12" height="24" fill="#ef4135"/>',
  de: '<rect width="36" height="8" fill="#000"/><rect y="8" width="36" height="8" fill="#dd0000"/><rect y="16" width="36" height="8" fill="#ffce00"/>',
  es: '<rect width="36" height="24" fill="#aa151b"/><rect y="6" width="36" height="12" fill="#f1bf00"/>',
  it: '<rect width="12" height="24" fill="#009246"/><rect x="12" width="12" height="24" fill="#fff"/><rect x="24" width="12" height="24" fill="#ce2b37"/>',
  jp: '<rect width="36" height="24" fill="#fff"/><circle cx="18" cy="12" r="7" fill="#bc002d"/>',
  kr: '<rect width="36" height="24" fill="#fff"/><circle cx="18" cy="12" r="6" fill="#cd2e3a"/><path d="M12 12a6 6 0 0 0 12 0z" fill="#0047a0"/>',
  la: '<rect width="36" height="24" rx="3" fill="#3a3f48"/><text x="18" y="17" font-size="12" font-family="R" font-weight="500" fill="#fff" text-anchor="middle">LA</text>',
  orig: '<rect width="36" height="24" rx="3" fill="#3a3f48"/><text x="18" y="17" font-size="11" font-family="R" font-weight="500" fill="#fff" text-anchor="middle">ORIG</text>' })[c];
const fl = c => `<svg class="fl" width="36" height="24" viewBox="0 0 36 24">${flag(c)}</svg>`;
Object.assign(P, { tv: '<rect x="3" y="5" width="18" height="12" rx="2"/><path d="M8 21h8"/>', search: '<circle cx="11" cy="11" r="6.5"/><path d="M16 16l4.5 4.5"/>' });
const tg = on => `<span class="tg${on ? ' on' : ''}"><i></i></span>`;
const val = v => `<span class="sv">${v}<span class="chev">${ic('chev', 22)}</span></span>`;
const row = (t, d, right, f) => `<div class="r${f ? ' f' : ''}"><div class="rt"><b>${t}</b><span>${d}</span></div>${right}</div>`;
const seg = (opts, on, f) => `<span class="seg${f ? ' f' : ''}">${opts.map(o => `<b class="${o === on ? 'on' : ''}">${o}</b>`).join('')}</span>`;
const btn1 = (t, f) => `<span class="ib"><span class="btn sm${f ? ' f' : ''}">${t}</span></span>`;
Object.assign(P, { box: '<path d="M4 7h16v13H4z"/><path d="M4 7l2-3h12l2 3M9 12h6"/>', diag: '<path d="M3 12h4l3-7 4 14 3-7h4"/>', adv: '<path d="M4 6h10M18 6h2M4 12h4M12 12h8M4 18h12M20 18h0"/><circle cx="16" cy="6" r="2"/><circle cx="10" cy="12" r="2"/><circle cx="18" cy="18" r="2"/>' });
Object.assign(P, { info: '<circle cx="12" cy="12" r="8.5"/><path d="M12 11v5M12 8h.01"/>', news: '<path d="M5 4h14v16H5z"/><path d="M8 8h8M8 12h8M8 16h5"/>', lic: '<path d="M6 3h9l3 3v15H6z"/><path d="M9 10h6M9 14h6M9 18h3"/>', dev: '<rect x="3" y="5" width="18" height="12" rx="2"/><path d="M8 21h8"/>', heart: '<path d="M12 20s-7-4.5-7-10a4 4 0 0 1 7-2.6A4 4 0 0 1 19 10c0 5.5-7 10-7 10z"/>' });
const tabs = (open) => `<div class="tabs">${[['info', 'About'], ['news', 'What’s new'], ['lic', 'Licences'], ['dev', 'Device']].map(([k, t], i) => `<div class="tb${i === open ? ' open' : ''}">${ic(k, 26)}<span>${t}</span></div>`).join('')}</div>`;
const shell = (open, body, foot) => `<div class="one"><div class="ohead"><div><div class="crumb">Settings</div><div class="stitle">About Supernova</div></div>${tabs(open)}</div><div class="rule"></div><div class="inner">${body}<div class="more">${foot}</div></div></div>`;
const info = (t, v) => `<div class="kv"><span>${t}</span><b>${v}</b></div>`;
const esc = t => t.replace(/&/g, '&amp;');
const histTop = [['November 2026', [['0.155', '4 Nov'], ['0.154', '3 Nov'], ['0.153', '3 Nov'], ['0.152', '2 Nov'], ['0.151', '2 Nov'], ['0.150', '2 Nov'], ['0.149', '1 Nov']]],
  ['October 2026', [['0.148', '31 Oct'], ['0.147', '29 Oct'], ['0.146', '29 Oct']]]];
const histEnd = [['September 2026', [['0.19', '16 Sep'], ['0.17', '15 Sep'], ['0.16', '15 Sep'], ['0.15', '15 Sep'], ['0.14', '15 Sep', 1], ['0.13', '15 Sep'], ['0.12', '15 Sep'], ['0.11', '14 Sep'], ['0.10', '14 Sep'], ['0.9', '14 Sep'], ['0.8', '14 Sep'], ['0.7', '14 Sep', 1], ['0.6', '14 Sep'], ['0.5', '14 Sep'], ['0.1 – 0.4', '13–14 Sep', 4]]]];
const detail = {
  '0.155': ['4 November 2026', ['Playback engine updated to NOVA 6.6.4.', 'Better Dolby Vision and HDR playback, including Dolby Vision in AV1.', 'If a hardware decoder fails, Supernova tries another one.', 'MPEG-TS files show the right length and resume correctly.', 'Styled subtitles stay on screen in Fit width and zoom.']],
  '0.8': ['14 September 2026', ['A new Home screen: compact rows, a slate look and real tabs for each section.']] };
const hrow = ([v, d, quiet], sel) => quiet
  ? `<div class="hv q"><b>${v}</b><span>${quiet > 1 ? quiet + ' test builds · no visible changes' : 'Test build · no visible changes'}</span></div>`
  : `<div class="hv${sel ? ' f' : ''}"><b>${v}</b><span>${d}</span></div>`;
const histPage = (groups, pick, fadeTop) => { const [date, items] = detail[pick];
  const list = groups.map(([m, rows]) => `<div class="csec2"><span>${m}</span><i></i></div>` + rows.map(r => hrow(r, r[0] === pick)).join('')).join('');
  return shell(1, `<div class="hist"><div class="hl${fadeTop ? ' end' : ''}"><div class="hls">${list}</div>${fadeTop ? '<div class="hfade top"></div>' : '<div class="hfade"></div>'}</div>` +
    `<div class="hd"><div class="hdv">${pick}</div><div class="hdd">${date}</div>${items.map(t => `<div class="ri">${t}</div>`).join('')}${pick === '0.8' ? '' : '<div class="hcount">155 versions since 13 September 2026</div>'}</div></div>`, ''); };
const news = histPage(histTop, '0.155', false);
const newsEnd = histPage(histEnd, '0.8', true);
const credits = [['Where Supernova comes from', [['NOVA Video Player', 'The app Supernova is built on', 'Supernova is based on NOVA Video Player 6.6.4. Thank you to the NOVA project and everyone who has worked on it. Supernova changes the app in many places; the original notices are kept under Licences.'],
  ['Archos Video Player Community Edition', 'Where NOVA itself began', 'NOVA grew out of Archos Video Player Community Edition. Thank you to Archos and its original contributors.']]],
  ['Information and services', [['TMDB', 'Film and TV details and artwork', 'This product uses the TMDB API but is not endorsed or certified by TMDB.'],
  ['JustWatch, via TMDB', 'Where to Watch', 'Where a title is available comes from JustWatch through TMDB. Availability can change.'],
  ['Trakt', 'Optional watch history sync', 'Signing in is optional and uses your own Trakt account.'],
  ['OpenSubtitles.com', 'Optional subtitle downloads', 'Uses your own OpenSubtitles.com account and its limits.']]]];
const svc = [['TMDB', 'Film and TV details, artwork, trailers and reviews', 'This product uses the TMDB API but is not endorsed or certified by TMDB.'],
  ['JustWatch, via TMDB', 'Where to Watch'], ['OMDb', 'IMDb, Rotten Tomatoes and Metacritic scores, and awards'],
  ['IntroDB and TheIntroDB', 'Intro, recap and credits times for Skip'], ['ChaptersDB', 'Chapters for files that have none'],
  ['OpenSubtitles.com', 'Subtitle search and download'], ['Trakt', 'Optional watch history sync; Trending and Popular rows'],
  ['YouTube', 'Trailers and extras play in YouTube’s player']];
const about = shell(0,
  `<div class="hero sm"><div class="logo">S</div><div><div class="an">Supernova</div><div class="av">Version 0.155 · 4 November 2026</div><div class="ab">A video player for Android TV, based on NOVA Video Player 6.6.4, which grew out of Archos Video Player Community Edition. Thank you to both projects.</div></div></div>` +
  `<div class="csec2"><span>Information and services</span><i></i></div>` +
  svc.map(([n, r, t], i) => `<div class="sr${i === 0 ? ' f' : ''}"><b>${n}</b><span>${r}</span>${t ? `<div class="srt">${t}</div>` : ''}</div>`).join('') +
  `<div class="disc">No endorsement by any of these projects or services is implied.</div>`, '');
const creditsPage = shell(0, `<div class="crumb2">About › Credits &amp; acknowledgements</div>` + credits.map(([g, items]) => `<div class="csec2"><span>${g}</span><i></i></div>` +
  items.map(([n, r, t], i) => `<div class="cr${g.startsWith('Where') && i === 0 ? ' f' : ''}"><div class="crn"><b>${n}</b><span>${r}</span></div><div class="crt">${t}</div></div>`).join('')).join('') +
  `<div class="disc">No endorsement by any of these projects or services is implied.</div>`, '');
const QR = 'M0 0.5h7m1 0h1m1 0h1m1 0h1m1 0h1m3 0h1m3 0h7m-29 1h1m5 0h1m1 0h1m3 0h1m1 0h2m6 0h1m5 0h1m-29 1h1m1 0h3m1 0h1m5 0h4m4 0h1m1 0h1m1 0h3m1 0h1m-29 1h1m1 0h3m1 0h1m2 0h3m1 0h2m1 0h2m2 0h1m1 0h1m1 0h3m1 0h1m-29 1h1m1 0h3m1 0h1m2 0h1m1 0h1m5 0h1m1 0h2m1 0h1m1 0h3m1 0h1m-29 1h1m5 0h1m3 0h3m1 0h3m5 0h1m5 0h1m-29 1h7m1 0h1m1 0h1m1 0h1m1 0h1m1 0h1m1 0h1m1 0h1m1 0h7m-18 1h4m1 0h1m2 0h2m-20 1h1m4 0h3m1 0h2m2 0h1m2 0h1m2 0h2m5 0h2m-29 1h1m1 0h3m5 0h1m1 0h4m2 0h2m3 0h5m-24 1h5m1 0h2m2 0h1m3 0h1m2 0h3m1 0h2m-25 1h4m2 0h1m1 0h1m7 0h1m3 0h3m-25 1h1m1 0h1m1 0h1m1 0h3m5 0h3m3 0h3m2 0h1m1 0h1m-27 1h3m1 0h1m1 0h1m1 0h1m2 0h1m2 0h1m1 0h4m1 0h1m1 0h2m2 0h1m-29 1h1m3 0h1m1 0h1m1 0h2m1 0h1m1 0h1m1 0h1m5 0h1m1 0h1m-24 1h4m1 0h1m1 0h2m1 0h1m1 0h1m1 0h3m1 0h2m1 0h2m1 0h3m-27 1h1m1 0h5m1 0h1m7 0h1m1 0h1m6 0h3m-28 1h1m2 0h1m1 0h1m1 0h2m1 0h2m1 0h2m3 0h3m1 0h5m1 0h1m-29 1h3m1 0h4m3 0h5m1 0h1m1 0h2m3 0h1m3 0h1m-29 1h1m2 0h1m1 0h1m2 0h1m4 0h2m4 0h7m1 0h1m-28 1h1m4 0h3m2 0h2m2 0h2m1 0h1m1 0h6m1 0h1m1 0h1m-21 1h2m3 0h1m1 0h1m2 0h1m1 0h1m3 0h4m-28 1h7m1 0h2m1 0h5m2 0h3m1 0h1m1 0h1m-25 1h1m5 0h1m5 0h1m1 0h3m1 0h3m3 0h2m1 0h2m-29 1h1m1 0h3m1 0h1m4 0h3m2 0h9m-25 1h1m1 0h3m1 0h1m3 0h1m1 0h2m2 0h1m1 0h3m2 0h1m1 0h3m-28 1h1m1 0h3m1 0h1m2 0h1m1 0h1m1 0h2m2 0h1m1 0h6m2 0h1m-28 1h1m5 0h1m1 0h4m2 0h2m5 0h1m2 0h3m1 0h1m-29 1h7m2 0h4m4 0h2m2 0h1m2 0h1m1 0h1';
const notice = `FFmpeg n9.0.2

# License

Most files in FFmpeg are under the GNU Lesser General Public License version 2.1
or later (LGPL v2.1+). Read the file COPYING.LGPLv2.1 for details. Some other
files have MIT/X11/BSD-style licenses. In combination the LGPL v2.1+ applies to
FFmpeg.

Some optional parts of FFmpeg are licensed under the GNU General Public License
version 2 or later (GPL v2+). See the file COPYING.GPLv2 for details. None of
these parts are used by default, you have to explicitly pass --enable-gpl to
configure to activate them. In this case, FFmpeg's license changes to GPL v2+.`;
const licPage = shell(2, `<div class="crumb2 i">Licences › Playback engine</div><div class="lc"><div class="lcl"><div class="lcn"><b>FFmpeg</b><span>Reads and decodes almost every video and audio format</span></div>` +
  `<div class="lcf"><span><i>Version</i>9.0.2, with security fixes</span><span><i>Licence</i>LGPL 2.1 or later</span></div></div>` +
  `<div class="lcq"><svg width="76" height="76" viewBox="0 0 29 29" shape-rendering="crispEdges"><path stroke="#000" d="${QR}"/></svg><span>Website<br>Scan with your phone</span></div></div>` +
  `<div class="ltext f"><pre>${notice}</pre><div class="hfade"></div></div>`, '');
const libs = [['App libraries', [['AndroidX', 'Apache 2.0'], ['OkHttp', 'Apache 2.0'], ['Retrofit', 'Apache 2.0'], ['jcifs-ng (Windows shares)', 'LGPL 2.1'], ['sshj (SFTP)', 'Apache 2.0'], ['sardine-android (WebDAV)', 'Apache 2.0']]],
  ['Playback engine', [['FFmpeg', 'LGPL 2.1'], ['libass (styled subtitles)', 'ISC'], ['dav1d (AV1)', 'BSD 2-Clause'], ['HarfBuzz', 'MIT']]]];
const lic = shell(2, `<div class="srch2">${ic('search', 26)}<span>Search 66 licences</span></div>` + libs.map(([g, items]) => `<div class="csec2"><span>${g}</span><i></i></div>` + items.map(([n, l], i) => `<div class="lr${g === 'App libraries' && i === 0 ? ' f' : ''}"><span>${n}</span><span class="lv">${l}</span>${ic('chev', 22)}</div>`).join('')).join(''), '');
const dev = shell(3,
  `<div class="csec2"><span>This device</span><i></i></div>` + info('Device', 'NVIDIA SHIELD Android TV') + info('Android', '11') + info('Free space', '6.2 GB') +
  `<div class="csec2"><span>Playback</span><i></i></div>` + info('Hardware video', 'H.264 · HEVC · VP9 · Dolby Vision · VP8 · MPEG-2 · VC-1') +
  `<div class="csec2"><span>Library</span><i></i></div>` + info('In your library', '85 films · 324 shows · 1,726 episodes') + info('Sources', '2 · USB drive and Put.io'), '');
const shots = [
  ['26-about.png', about, 'About: version, origins and every outside service Supernova uses'],
  ['27-whats-new.png', news, 'What’s new: every version since 0.1, newest first; the selected one is described on the right'],
  ['30-whats-new-start.png', newsEnd, 'The start of the list: test builds with no visible change are shown as one quiet line'],
  ['32-licence.png', licPage, 'One licence opened: a small QR code for the website sits with the facts'],
  ['28-licences.png', lic, 'Licences: search, grouped into app libraries and playback engine'],
  ['29-device.png', dev, 'Device: device, decoders and library in plain words'],
];
const css = `${font(400)}${font(500)}*{box-sizing:border-box;margin:0}
body{width:1920px;height:1080px;overflow:hidden;font-family:R;color:#fff;position:relative;background:radial-gradient(ellipse 70% 60% at 70% 75%,#1d4fd8 0%,rgba(29,79,216,0) 70%),linear-gradient(180deg,#05081a 0%,#0a1650 45%,#10248a 100%)}
.nav{position:absolute;left:56px;right:56px;top:28px;height:56px;display:flex;align-items:center;gap:30px;font-size:34px;color:#e8edf8}.nav .brand{font-size:36px;margin-right:40px;letter-spacing:1px}.nav .sp{flex:1}.nav .ic{display:flex}.nav .ic.on{color:#fff;border-bottom:3px solid #fff;padding-bottom:4px}.nav .clock{margin-left:10px}
.one{position:absolute;left:56px;right:56px;top:112px;bottom:40px;border-radius:26px;background:rgba(22,24,28,.97);border:2px solid rgba(255,255,255,.22);padding:22px 28px}
.ohead{display:flex;align-items:center;justify-content:space-between;margin:0 10px 16px}.crumb{font-size:20px;color:#b7bfcc}.stitle{font-size:36px;font-weight:500;margin-top:2px}
.tabs{display:flex;gap:12px}.tb{height:64px;border-radius:20px;background:#2a2d33;color:#c9d3e6;display:flex;align-items:center;gap:10px;padding:0 22px;font-size:23px;font-weight:500}.tb.open{background:#454b55;color:#fff}.tb.f{background:#F4F6FB;color:#0B0D12}
.rule{height:1px;background:rgba(255,255,255,.14);margin:0 6px 8px}.inner{position:relative;height:calc(100% - 120px)}
.r{display:flex;align-items:center;gap:24px;min-height:96px;padding:10px 22px;border-radius:22px;border-bottom:1px solid rgba(255,255,255,.08)}.r.f{box-shadow:inset 0 0 0 3px #fff;border-bottom-color:transparent}
.rt{flex:1;min-width:0}.rt b{display:block;font-size:27px;font-weight:500}.rt span{display:block;font-size:21px;color:#c9d3e6;margin-top:6px}
.sv{display:flex;align-items:center;gap:10px;font-size:24px;color:#dfe5f1;white-space:nowrap}.chev{color:#8f98a8;display:flex}.fl{border-radius:3px;flex:none}
.tg{width:64px;height:34px;border-radius:17px;background:#3a3f48;position:relative;display:inline-block;flex:none}.tg.on{background:#2fb7d8}.tg i{position:absolute;top:4px;left:4px;width:26px;height:26px;border-radius:50%;background:#fff}.tg.on i{left:34px}
.seg{display:inline-flex;background:#1b1d22;border-radius:18px;padding:4px;flex:none}.seg b{font-weight:500;font-size:20px;padding:10px 16px;border-radius:14px;color:#aab3c4;white-space:nowrap}.seg b.on{background:#454b55;color:#fff}.seg.f b.on{background:#F4F6FB;color:#0B0D12}
.ib{display:flex;gap:10px;margin-left:16px}.btn{height:52px;padding:0 18px;border-radius:18px;background:#2a2d33;display:inline-flex;align-items:center;gap:8px;font-size:20px;font-weight:500;white-space:nowrap}.btn.f{background:#F4F6FB;color:#0B0D12}
.lg{display:inline-flex;align-items:center;height:36px;padding:0 12px;border-radius:9px;font-size:18px;font-weight:500;color:#fff;margin-left:8px;white-space:nowrap}
.opt .lg{margin-left:0}.srch{display:flex;align-items:center;gap:12px;height:60px;border-radius:18px;background:#2a2d33;padding:0 18px;font-size:22px;color:#8f98a8;margin:0 0 12px}
.cb{width:32px;height:32px;border-radius:9px;border:2px solid #aab3c4;display:flex;align-items:center;justify-content:center;flex:none}.cb.on{background:#F4F6FB;border-color:#F4F6FB;color:#0B0D12}
.grp{font-size:22px;font-weight:500;color:#b7bfcc;margin:22px 14px 6px}
.ss{position:absolute;inset:0;background:#000;z-index:10}.ssart{position:absolute;inset:-40px;background:radial-gradient(ellipse 50% 40% at 62% 40%,rgba(255,210,150,.55),transparent 70%),linear-gradient(160deg,#5b4a36 0%,#2b2420 45%,#0b0a0a 100%);transform:scale(1.04)}
.ssshade{position:absolute;inset:0;background:linear-gradient(0deg,rgba(0,0,0,.65) 0%,rgba(0,0,0,0) 35%)}
.sscopy{position:absolute;left:80px;bottom:70px}.sst{font-size:40px;font-weight:500}.sss{font-size:22px;color:#c9d3e6;margin-top:6px}.ssclock{position:absolute;right:80px;bottom:74px;font-size:40px;font-weight:400;color:#e8edf8}
.mk{width:56px;height:56px;border-radius:16px;display:flex;align-items:center;justify-content:center;font-size:20px;font-weight:700;flex:none}
.st{font-size:20px;font-weight:500;white-space:nowrap}.st.ok{color:#9fe3bb}.st.no{color:#aab3c4}.st.na{color:#8f98a8;font-weight:400}
.warn{margin:6px 14px 4px;font-size:21px;color:#ffd38a}
.pg{font-size:18px;font-weight:500;color:#b7bfcc;margin:6px 6px 0}.opt.sm{height:40px;font-size:20px}.opt.sm .cb{width:28px;height:28px}
.note{margin:18px 22px;font-size:20px;color:#aab3c4}
.more{position:absolute;left:14px;bottom:0;font-size:18px;color:#8f98a8}
.dim{position:absolute;inset:0;background:rgba(0,0,0,.35)}
.panel{position:absolute;right:56px;top:112px;width:max-content;min-width:320px;max-height:928px;border-radius:26px;background:#24272d;border:2px solid rgba(255,255,255,.24);padding:28px;box-shadow:-20px 0 60px rgba(0,0,0,.5)}
.ptitle{font-size:30px;font-weight:500;margin:0 6px 6px}.pdesc{font-size:20px;color:#c9d3e6;margin:0 6px 14px}
.opt{display:flex;align-items:center;gap:16px;height:62px;padding:0 20px;border-radius:20px;font-size:25px;font-weight:500}.opt.f{box-shadow:inset 0 0 0 3px #fff}
.rad{width:32px;height:32px;border-radius:50%;border:2px solid #aab3c4;display:flex;align-items:center;justify-content:center;flex:none}.rad.on{background:#F4F6FB;border-color:#F4F6FB;color:#0B0D12}
.cpanel{position:absolute;right:56px;top:112px;width:720px;height:928px;border-radius:26px;background:#1b1d22;border:2px solid rgba(255,255,255,.22);box-shadow:-20px 0 60px rgba(0,0,0,.5);overflow:hidden}
.cscroll{position:absolute;left:28px;right:28px;top:22px}.cscroll.end{top:auto;bottom:26px}
.csec{display:flex;align-items:center;gap:16px;height:50px;padding:0 8px;font-size:21px;color:#aab3c4}.csec i{flex:1;height:1px;background:rgba(255,255,255,.18)}
.crow{border-radius:20px;padding:0 18px}.crow.f{box-shadow:inset 0 0 0 3px #fff}.crow.ask{box-shadow:inset 0 0 0 3px #fff;background:#24272d;padding-bottom:18px}
.cline{display:flex;align-items:center;gap:20px;height:74px;font-size:25px;font-weight:500}.cline svg{color:#dfe5ef;flex:none}.ct{flex:1}
.cwarn{display:flex;gap:12px;margin:0 0 16px 48px;font-size:20px;line-height:1.4;color:#ffd38a}.cwarn svg{flex:none;margin-top:3px}
.cbtns{display:flex;gap:12px;margin-left:48px}
.cfade{position:absolute;left:0;right:0;bottom:0;height:90px;background:linear-gradient(0deg,#1b1d22 20%,rgba(27,29,34,0))}.cfade.top{top:0;bottom:auto;height:70px;background:linear-gradient(180deg,#1b1d22 25%,rgba(27,29,34,0))}
.hero{display:flex;gap:30px;align-items:center;margin:16px 14px 26px}.logo{width:120px;height:120px;border-radius:30px;background:linear-gradient(145deg,#3b6cff,#1b2c7a);display:flex;align-items:center;justify-content:center;font-size:64px;font-weight:500}
.an{font-size:40px;font-weight:500}.av{font-size:24px;color:#dfe5f1;margin-top:6px}.ab{font-size:21px;color:#c9d3e6;margin-top:8px}
.rel{border-radius:22px;padding:18px 22px;margin-bottom:8px}.rel.f{box-shadow:inset 0 0 0 3px #fff}.rh{display:flex;gap:16px;align-items:baseline;margin-bottom:8px}.rh b{font-size:28px;font-weight:500}.rh span{font-size:20px;color:#aab3c4}
.ri{font-size:22px;color:#dfe5f1;margin:6px 0 0 4px}.ri:before{content:'·';margin-right:12px;color:#8f98a8}
.srch2{display:flex;align-items:center;gap:12px;height:62px;border-radius:18px;background:#2a2d33;padding:0 20px;font-size:22px;color:#8f98a8;margin:8px 0 6px}
.csec2{display:flex;align-items:center;gap:16px;height:50px;padding:0 14px;font-size:21px;color:#aab3c4}.csec2 i{flex:1;height:1px;background:rgba(255,255,255,.18)}
.lr{display:flex;align-items:center;gap:20px;height:62px;padding:0 22px;border-radius:20px;font-size:24px;font-weight:500}.lr.f{box-shadow:inset 0 0 0 3px #fff}.lr span:first-child{flex:1}.lv{font-weight:400;color:#c9d3e6;font-size:21px}.lr svg{color:#8f98a8}
.kv{display:flex;align-items:center;height:58px;padding:0 22px;font-size:23px}.kv span{width:320px;color:#c9d3e6}.kv b{font-weight:500}
.hist{display:flex;gap:28px;height:780px}.hl{position:relative;width:560px;flex:none;overflow:hidden}.hls{position:absolute;left:0;right:0;top:0}.hl.end .hls{top:auto;bottom:0}
.hv{display:flex;align-items:center;gap:18px;height:58px;padding:0 20px;border-radius:18px}.hv b{font-size:24px;font-weight:500;width:120px}.hv span{font-size:20px;color:#aab3c4}.hv.f{box-shadow:inset 0 0 0 3px #fff}
.hv.q b{color:#8f98a8;font-weight:400}.hv.q span{color:#7d8696}
.hfade{position:absolute;left:0;right:0;bottom:0;height:90px;background:linear-gradient(0deg,#16181d 15%,rgba(22,24,29,0))}.hfade.top{top:0;bottom:auto;background:linear-gradient(180deg,#16181d 15%,rgba(22,24,29,0))}
.hd{flex:1;border-left:1px solid rgba(255,255,255,.12);padding:14px 0 0 32px;position:relative}.hdv{font-size:40px;font-weight:500}.hdd{font-size:21px;color:#aab3c4;margin:4px 0 18px}.hd .ri{font-size:23px;margin:10px 0 0}
.hcount{position:absolute;left:32px;bottom:10px;font-size:19px;color:#8f98a8}
.lc{display:flex;align-items:center;gap:30px;margin:0 14px 30px}.lcl{flex:1}.crumb2.i{margin:6px 14px 12px}.lcn{display:flex;align-items:baseline;gap:20px}.lcn b{font-size:34px;font-weight:500}.lcn span{font-size:21px;color:#c9d3e6}.lcf{display:flex;gap:44px;font-size:21px;margin-top:20px}.lcf i{font-style:normal;color:#aab3c4;margin-right:10px}.lcq{display:flex;align-items:center;gap:14px}.lcq svg{background:#fff;padding:6px;border-radius:8px;box-sizing:content-box}.lcq span{font-size:19px;color:#aab3c4;line-height:1.35}
.crumb2{font-size:20px;color:#aab3c4;margin:6px 14px 4px}
.cr{border-radius:20px;padding:14px 22px;margin-bottom:4px}.cr.f{box-shadow:inset 0 0 0 3px #fff}.crn{display:flex;align-items:baseline;gap:16px}.crn b{font-size:25px;font-weight:500}.crn span{font-size:20px;color:#aab3c4}.crt{font-size:21px;color:#dfe5f1;margin-top:6px;line-height:1.4;max-width:1500px}
.disc{font-size:19px;color:#8f98a8;margin:14px 22px}
.lh{margin:10px 14px 16px}.qr{position:absolute;right:0;top:-6px;display:flex;flex-direction:column;align-items:center;gap:8px}.qr svg{background:#fff;padding:12px;border-radius:14px;box-sizing:content-box}.qr span{font-size:18px;color:#aab3c4}.hero.sm{margin:6px 14px 10px}.hero.sm .logo{width:96px;height:96px;font-size:52px;border-radius:24px}.hero.sm .an{font-size:34px}.cr.sm{padding:10px 22px;margin-bottom:0}.lhn{font-size:38px;font-weight:500}.lhs{font-size:22px;color:#c9d3e6;margin-top:4px}
.lfacts{display:flex;gap:48px;align-items:center;margin:18px 0 0;font-size:22px}.qrw{display:flex;align-items:center}.qrw svg{background:#fff;padding:6px;border-radius:8px;box-sizing:content-box;flex:none}.qrw em{font-style:normal;font-size:19px;color:#aab3c4;margin-left:14px}.sr{display:flex;flex-wrap:wrap;align-items:baseline;gap:6px 18px;padding:12px 22px;border-radius:18px}.sr.f{box-shadow:inset 0 0 0 3px #fff}.sr b{font-size:24px;font-weight:500}.sr span{font-size:21px;color:#c9d3e6}.srt{width:100%;font-size:20px;color:#aab3c4}.lfacts i{font-style:normal;color:#aab3c4;margin-right:12px}
.ltext{position:relative;border-radius:20px;background:#121418;margin:8px 0 0;height:570px;overflow:hidden;padding:20px 26px}.ltext.f{box-shadow:inset 0 0 0 3px #fff}.ltext pre{font-family:R;font-size:20px;line-height:1.5;color:#dfe5f1;white-space:pre-wrap;margin:0}.ltext .hfade{background:linear-gradient(0deg,#121418 15%,rgba(18,20,24,0))}
.label{z-index:20;position:absolute;right:56px;bottom:8px;font-size:18px;color:#c9d3e6}`;
(async () => { const b = await chromium.launch(); const t = await b.newPage({ viewport: { width: 1920, height: 1080 } });
  for (const [f, body, label] of shots) {
    await t.setContent(`<html><head><style>${css}</style></head><body>${nav}${body}<div class="label">${label}</div></body></html>`, { waitUntil: 'load' });
    await t.evaluate(() => document.fonts.ready);
    const issues = await t.evaluate(() => { const o = [], r = e => e.getBoundingClientRect(); const mo = document.querySelector('.more');
      document.querySelectorAll('.inner .r, .note').forEach(e => { if (r(e).bottom > r(mo).top - 4) o.push('hits footer: ' + e.textContent.slice(0, 30)); });
      document.querySelectorAll('.inner .r').forEach(e => { const a = e.querySelector('.rt'), z = a.nextElementSibling; if (z && r(a).right > r(z).left) o.push('text hits value: ' + e.textContent.slice(0, 30)); });
      const p = document.querySelector('.panel'); if (p) p.querySelectorAll('.opt').forEach(e => { if (r(e).bottom > r(p).bottom) o.push('picker overflow'); });
      return o; });
    if (issues.length) console.log(f, issues);
    await t.screenshot({ path: path.join(out, f) }); }
  await b.close(); })();
