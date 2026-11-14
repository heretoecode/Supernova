// Settings as a full page with tabs (owner, 13 Nov 2026), from docs/0.157/SETTINGS_INVENTORY.md and RULES.md.
// Batch 2 (owner, 14 Nov): Subtitles with the Experimental row; Library, Services, System, About.
// v2 (owner, 13 Nov): Seek previews marked Experimental; Open in another player moved to Advanced; online skip
// times row reworded; local-audio row reworded; flags on several languages; Appearance as the defaults; the
// OpenSubtitles panel, Font list and Text encoding list.
// Batch 1: Playback, Audio, Subtitles — each tab drawn whole. Light frame = picture edge.
// Run: FONT_DIR=<roboto woff2 dir> node render.js
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
  play: '<path d="M7 4l13 8-13 8z"/>',
  audio: '<path d="M4 9v6h4l5 4V5L8 9z"/><path d="M16 9a4 4 0 0 1 0 6"/>',
  subs: '<rect x="3" y="5" width="18" height="14" rx="2"/><path d="M7 14h4M13 14h4M7 10h10"/>',
  lib: '<rect x="3" y="4" width="5" height="16" rx="1"/><rect x="10" y="4" width="5" height="16" rx="1"/><path d="M17 5l4 14"/>',
  cloud: '<path d="M7 18h10a4 4 0 0 0 .6-8A6 6 0 0 0 6 9.5 4.3 4.3 0 0 0 7 18z"/>',
  sys: '<rect x="3" y="5" width="18" height="12" rx="2"/><path d="M8 21h8M12 17v4"/>',
  info: '<circle cx="12" cy="12" r="9"/><path d="M12 11v6M12 7.5h.1"/>',
  gen: '<path d="M4 6h10M18 6h2M4 12h4M12 12h8M4 18h12M20 18h0"/><circle cx="16" cy="6" r="2"/><circle cx="10" cy="12" r="2"/><circle cx="18" cy="18" r="2"/>',
  pause: '<path d="M9 5v14M15 5v14"/>',
  ext: '<path d="M14 4h6v6M20 4l-9 9"/><path d="M18 14v5a1 1 0 0 1-1 1H5a1 1 0 0 1-1-1V7a1 1 0 0 1 1-1h5"/>',
  prev: '<rect x="3" y="5" width="18" height="11" rx="2"/><path d="M3 20h18M9 20v-3"/>',
  resume: '<path d="M4 12a8 8 0 1 0 2.3-5.7"/><path d="M4 4v4h4"/>',
  mark: '<path d="M6 3h12v18l-6-4-6 4z"/>',
  skip: '<path d="M5 5l8 7-8 7zM14 5l8 7-8 7z"/>',
  db: '<ellipse cx="12" cy="6" rx="8" ry="3"/><path d="M4 6v12c0 1.7 3.6 3 8 3s8-1.3 8-3V6M4 12c0 1.7 3.6 3 8 3s8-1.3 8-3"/>',
  intro: '<path d="M9 18V5l11-2v13"/><circle cx="6" cy="18" r="3"/><circle cx="17" cy="16" r="3"/>',
  recap: '<path d="M3 12a9 9 0 1 0 3-6.7L3 8"/><path d="M3 3v5h5"/>',
  preview: '<path d="M2 12s4-7 10-7 10 7 10 7-4 7-10 7S2 12 2 12z"/><circle cx="12" cy="12" r="3"/>',
  outro: '<path d="M4 12h12M12 6l6 6-6 6M20 5v14"/>',
  credits: '<path d="M4 6h16M4 10h16M4 14h10M4 18h7"/>',
  pic: '<rect x="3" y="5" width="18" height="14" rx="2"/><path d="M3 15l5-5 4 4 3-3 6 6"/>',
  hz: '<path d="M2 12h3l2-6 4 12 3-9 2 3h6"/>',
  dv: '<rect x="3" y="6" width="18" height="12" rx="2"/><path d="M7 9v6h2a3 3 0 0 0 0-6zM14 9l1.5 6L17 9"/>',
  lang: '<circle cx="12" cy="12" r="9"/><path d="M3 12h18M12 3a14 14 0 0 1 0 18M12 3a14 14 0 0 0 0 18"/>',
  orig: '<path d="M4 4v16M4 5h11l-2 4 2 4H4"/>',
  sur: '<circle cx="12" cy="12" r="3"/><path d="M6 6a8.5 8.5 0 0 0 0 12M18 6a8.5 8.5 0 0 1 0 12"/>',
  head: '<path d="M4 15v-3a8 8 0 0 1 16 0v3"/><rect x="3" y="14" width="4" height="6" rx="1.5"/><rect x="17" y="14" width="4" height="6" rx="1.5"/>',
  chip: '<rect x="6" y="6" width="12" height="12" rx="2"/><path d="M9 2v4M15 2v4M9 18v4M15 18v4M2 9h4M2 15h4M18 9h4M18 15h4"/>',
  eye: '<path d="M2 12s4-7 10-7 10 7 10 7-4 7-10 7S2 12 2 12z"/><circle cx="12" cy="12" r="3"/>',
  person: '<circle cx="12" cy="8" r="4"/><path d="M4 21a8 8 0 0 1 16 0"/>',
  dl: '<path d="M12 4v11M7 10l5 5 5-5M5 20h14"/>',
  next: '<path d="M5 5l10 7-10 7zM19 5v14"/>',
  find: '<circle cx="11" cy="11" r="7"/><path d="M20 20l-4-4M11 8v6M8 11h6"/>',
  key: '<circle cx="8" cy="15" r="4"/><path d="M11 12l9-9M17 6l3 3"/>',
  size: '<path d="M4 18L9 6l5 12M6 14h6M15 18l3-7 3 7M16 16h4"/>',
  tint: '<path d="M12 3l6 8a6 6 0 1 1-12 0z"/>',
  pos: '<path d="M12 4v16M8 8l4-4 4 4M8 16l4 4 4-4"/>',
  outline: '<path d="M5 19L10 5h4l5 14M7.5 13h9"/>',
  bg: '<rect x="3" y="8" width="18" height="9" rx="2"/>',
  opac: '<circle cx="12" cy="12" r="9"/><path d="M12 3a9 9 0 0 1 0 18z" fill="currentColor"/>',
  font: '<path d="M4 20L10 4l6 16M6.5 14h7M17 20v-6a3 3 0 0 1 3-3"/>',
  folder: '<path d="M3 7a2 2 0 0 1 2-2h4l2 2h8a2 2 0 0 1 2 2v8a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2z"/>',
  bars: '<rect x="3" y="4" width="18" height="16" rx="2"/><path d="M3 8h18M3 16h18"/>',
  enc: '<path d="M4 7V5h16v2M9 19h6M12 5v14"/><path d="M17 12l3 3-3 3"/>',
  delay: '<circle cx="12" cy="13" r="8"/><path d="M12 9v4l2 2M10 2h4"/>',
  rewind: '<path d="M11 5L4 12l7 7zM20 5l-7 7 7 7z"/>',
  cloudf: '<path d="M7 18h10a4 4 0 0 0 .6-8A6 6 0 0 0 6 9.5 4.3 4.3 0 0 0 7 18z"/><path d="M12 10v6M9.5 13.5L12 16l2.5-2.5"/>',
  flag: '<path d="M4 21V4M4 5h11l-2 4 2 4H4"/>',
  chev: '<path d="M9 6l6 6-6 6"/>',
  minus: '<path d="M6 12h12"/>',
  plus: '<path d="M12 6v12M6 12h12"/>',
};
const TABS = [['play', 'Playback'], ['audio', 'Audio'], ['subs', 'Subtitles'], ['lib', 'Library'], ['cloud', 'Services'],
  ['sys', 'System'], ['info', 'About']];
const nav = () => `<div class="nav"><span class="brand">SUPERNOVA</span>${['Home', 'Movies', 'TV Shows', 'Documentaries']
  .map(t => `<span class="ni">${t}</span>`).join('')}<span class="ni files">Files</span><span class="ni">${svg(I.search, 34)}
  </span><span class="ni dull">${svg(I.cog, 34)}</span><span class="clock">21:40</span></div>`;
const tabs = open => `<div class="tabs">${TABS.map(([ic, t]) => `<span class="tab ${t === open ? 'line' : ''}">
  ${svg(I[ic], 28)}${t}</span>`).join('')}</div>`;
const hd = (ic, t) => `<div class="hd">${svg(I[ic], 30)}${t}</div>`;
const chips = (l, on) => `<span class="chips">${l.map(t => `<i class="${t === on ? 'on' : ''}">${t}</i>`).join('')}</span>`;
const sw = on => `<span class="sw ${on ? 'on' : ''}"><i></i></span>`;
const val = v => `<span class="v">${v}${svg(I.chev, 26)}</span>`;
const step = v => `<span class="v">${svg(I.minus, 24)}<b>${v}</b>${svg(I.plus, 24)}</span>`;
const row = (ic, name, line, right, f) => `<div class="row ${f ? 'focus' : ''}">${svg(I[ic], 34)}<span class="rt"><b>${name}
  </b>${line ? `<small>${line}</small>` : ''}</span>${right}</div>`;
const page = (open, body) => `${nav()}<div class="page">${tabs(open)}${body}</div>`;
const SK = ['Off', 'Show button', 'Automatic'];

const playback = page('Playback', `${hd('gen', 'General')}
  ${row('pause', 'Hide controls when paused', 'Shows the paused picture with nothing on top', sw(false), true)}
  ${row('prev', 'Seek previews<em class="exp">Experimental</em>', 'A small picture above the bar while you skip through a video', sw(true))}
  ${hd('resume', 'Resume')}
  ${row('resume', 'When a video was started', 'What OK does on something you have started', chips(['Ask', 'Resume', 'Start over'], 'Resume'))}
  ${row('mark', 'Remember where you stopped', 'On this Shield and on network files', sw(true))}
  ${hd('skip', 'Skipping')}
  ${row('db', 'Look up skip times online', 'TheIntroDB and IntroDB, when the file doesn’t mark its intro and credits', sw(true))}
  ${row('intro', 'Intro', 'Opening titles and theme tune', chips(SK, 'Show button'))}
  ${row('rewind', 'Recap', '“Previously on…”', chips(SK, 'Automatic'))}
  ${row('preview', 'Preview', 'Scenes from the next episode', chips(SK, 'Off'))}
  ${row('outro', 'Outro', 'Closing scene after the story ends', chips(SK, 'Off'))}
  ${row('credits', 'Credits', 'End credits; never cuts a scene after them', chips(SK, 'Show button'))}
  ${hd('pic', 'Picture')}
  ${row('hz', 'Automatic refresh rate', 'Switches the TV to match the video', chips(['Off', 'Match the video'], 'Match the video'))}
  ${row('dv', 'Dolby Vision', 'How Dolby Vision videos are sent to the TV', chips(['Automatic', 'Always', 'Off'], 'Automatic'))}`);

const audio = page('Audio', `${hd('lang', 'Language')}
  ${row('flag', 'Preferred audio language', 'Chosen first when a video has more than one', val('🇬🇧 English'), true)}
  ${row('lang', 'Prefer the original language', 'Plays the film’s own language when that track exists', sw(false))}
  ${hd('sur', 'Sound')}
  ${row('sur', 'Surround sound', 'How 5.1 and 7.1 sound reaches your TV or receiver', chips(['Automatic', 'Passthrough', 'PCM', 'Stereo'], 'Automatic'))}
  ${row('head', 'Virtual surround', 'Spreads stereo sound for headphones and TV speakers', sw(false))}
  ${row('chip', 'Audio decoder', 'Which part of the Shield turns the file into sound', chips(['Automatic', 'Hardware', 'Software'], 'Automatic'))}`);

const subtitles = page('Subtitles', `${hd('subs', 'When To Show')}
  ${row('eye', 'Show subtitles', 'When a video starts', sw(true), true)}
  ${row('person', 'Full subtitles when the audio is in your language', 'Off: only signs and foreign-language lines', sw(true))}
  ${row('lang', 'Also translate other languages<em class="exp">Experimental</em>', 'Adds lines spoken in other languages to your subtitles', sw(true))}
  ${hd('lang', 'Languages')}
  ${row('flag', 'Preferred subtitle language', 'Chosen first when there is more than one', val('🇬🇧 English'))}
  ${row('find', 'Languages to search online', 'Used by Download subtitles', val('🇬🇧 English, 🇪🇸 Spanish'))}
  ${hd('dl', 'Downloading')}
  ${row('key', 'OpenSubtitles', 'Signed in as yourname', val('Signed in'))}
  ${row('cloudf', 'Find subtitles when none are included', 'Searches OpenSubtitles automatically', sw(true))}
  ${row('next', 'Have subtitles ready', 'Fetches them while a video prepares, and for the next two episodes', sw(true))}
  ${hd('size', 'Default Appearance')}
  ${row('size', 'Text size', '', chips(['Small', 'Medium', 'Large', 'Extra large'], 'Medium'))}
  ${row('tint', 'Text colour', '', '<span class="dots"><i class="on" style="background:#fff"></i><i style="background:#f5d547"></i><i style="background:#7fd6f0"></i><i style="background:#8fe39a"></i><i style="background:#f2a2c8"></i></span>')}
  ${row('pos', 'Vertical position', '', step('10% up'))}
  ${row('outline', 'Outline', '', sw(true))}
  ${row('bg', 'Background', '', sw(false))}
  ${row('opac', 'Background opacity', '', chips(['25%', '50%', '75%', '100%'], '50%'))}
  ${row('font', 'Font', 'Used when the subtitles name none', val('Roboto'))}
  ${row('folder', 'Custom fonts folder', 'For styled subtitles that ask for other fonts', val('Choose a folder'))}
  ${row('bars', 'Use the black bars', 'Puts subtitles below the picture on wide films', sw(true))}
  ${row('enc', 'Text encoding', 'For old subtitle files that show odd letters', val('Automatic'))}`);



Object.assign(I, {
  az: '<path d="M4 18L8 6l4 12M5.5 14h5M14 6h6l-6 12h6"/>',
  det: '<rect x="4" y="3" width="16" height="18" rx="2"/><path d="M8 8h8M8 12h8M8 16h5"/>',
  nfo: '<path d="M6 3h9l4 4v14H6z"/><path d="M14 3v5h5"/>',
  save: '<path d="M5 3h11l3 3v15H5z"/><path d="M8 3v6h8V3M8 21v-7h8v7"/>',
  exp2: '<path d="M12 4v11M7 10l5 5 5-5M5 20h14"/>',
  thumb: '<rect x="3" y="5" width="18" height="14" rx="2"/><circle cx="9" cy="10" r="2"/><path d="M21 16l-5-5-9 9"/>',
  hero: '<rect x="2" y="5" width="20" height="12" rx="2"/><path d="M6 21h12"/>',
  recent: '<rect x="3" y="5" width="18" height="16" rx="2"/><path d="M3 10h18M12 13v5M9.5 15.5h5"/>',
  trend: '<path d="M3 17l6-6 4 4 8-8M15 7h6v6"/>',
  pop: '<path d="M12 20s-7-4.5-7-10a4 4 0 0 1 7-2.6A4 4 0 0 1 19 10c0 5.5-7 10-7 10z"/>',
  hist: '<circle cx="12" cy="12" r="9"/><path d="M12 7v5l3 2"/>',
  bin: '<path d="M4 7h16M10 11v6M14 11v6M6 7l1 13h10l1-13M9 7V4h6v3"/>',
  tv: '<rect x="3" y="5" width="18" height="12" rx="2"/><path d="M8 21h8M12 17v4"/>',
  country: '<path d="M4 21V4M4 5h11l-2 4 2 4H4"/>',
  first: '<path d="M12 3l2.7 5.6 6.1.9-4.4 4.3 1 6.1L12 17l-5.4 2.9 1-6.1L3.2 9.5l6.1-.9z"/>',
  trakt: '<circle cx="12" cy="12" r="9"/><path d="M8 12l3 3 5-6"/>',
  scrob: '<path d="M3 12h4l3-7 4 14 3-7h4"/>',
  sync: '<path d="M4 12a8 8 0 0 1 14-5.3M20 12a8 8 0 0 1-14 5.3"/><path d="M18 3v4h-4M6 21v-4h4"/>',
  up: '<path d="M12 20V8M7 13l5-5 5 5M5 4h14"/>',
  down: '<path d="M12 4v12M7 11l5 5 5-5M5 20h14"/>',
  wipe: '<path d="M6 6l12 12M18 6L6 18"/>',
  star: '<path d="M12 3l2.7 5.6 6.1.9-4.4 4.3 1 6.1L12 17l-5.4 2.9 1-6.1L3.2 9.5l6.1-.9z"/>',
  ui: '<circle cx="12" cy="12" r="9"/><path d="M3 12h18M12 3a14 14 0 0 1 0 18M12 3a14 14 0 0 0 0 18"/>',
  backup: '<path d="M7 18h10a4 4 0 0 0 .6-8A6 6 0 0 0 6 9.5 4.3 4.3 0 0 0 7 18z"/><path d="M12 16v-6M9.5 12.5L12 10l2.5 2.5"/>',
  restore: '<path d="M4 12a8 8 0 1 0 2.3-5.7"/><path d="M4 4v4h4"/>',
  box: '<path d="M3 7l9-4 9 4v10l-9 4-9-4z"/><path d="M3 7l9 4 9-4M12 11v10"/>',
  log: '<path d="M4 6h16M4 10h16M4 14h10M4 18h7"/>',
  rep: '<path d="M6 3h9l4 4v14H6z"/><path d="M9 13h6M9 17h4"/>',
  clr: '<path d="M4 7h16M6 7l1 13h10l1-13"/>',
  keep: '<rect x="3" y="5" width="18" height="16" rx="2"/><path d="M3 10h18"/>',
  adv: '<path d="M4 6h10M18 6h2M4 12h4M12 12h8M4 18h12"/><circle cx="16" cy="6" r="2"/><circle cx="10" cy="12" r="2"/>',
  proj: '<path d="M3 8h18v8H3z"/><circle cx="15" cy="12" r="2"/><path d="M6 16v3M18 16v3"/>',
  dec: '<rect x="6" y="6" width="12" height="12" rx="2"/><path d="M9 2v4M15 2v4M9 18v4M15 18v4"/>',
  speed: '<path d="M4 16a8 8 0 1 1 16 0"/><path d="M12 16l4-5"/>',
  mix: '<path d="M4 21v-7M4 10V3M12 21v-9M12 8V3M20 21v-5M20 12V3"/>',
  net: '<rect x="4" y="4" width="16" height="7" rx="1.5"/><rect x="4" y="13" width="16" height="7" rx="1.5"/>',
  notes: '<path d="M6 3h9l4 4v14H6z"/><path d="M9 9h6M9 13h6M9 17h4"/>',
  cred: '<circle cx="9" cy="8" r="3.5"/><path d="M2.5 20a6.5 6.5 0 0 1 13 0"/><circle cx="17" cy="9" r="2.5"/>',
  lic: '<path d="M7 3h10v18l-5-3-5 3z"/>',
  app: '<rect x="6" y="2" width="12" height="20" rx="2"/><path d="M11 18h2"/>',
});
const action = t => `<span class="v">${t}${svg(I.chev, 26)}</span>`;
const library = page('Library', `${hd('lib', 'Your Library')}
  ${row('az', 'Ignore “The” and “A” when sorting', '“The Batman” sorts under B', sw(true), true)}
  ${row('thumb', 'Make thumbnails for network videos', 'A frame from videos with no artwork; uses some network time', sw(true))}
  ${hd('det', 'Details')}
  ${row('det', 'Find details automatically', 'Posters, synopsis and cast for new files', sw(true))}
  ${row('ui', 'Details language', 'For titles and synopses where available', val('🇬🇧 English'))}
  ${row('nfo', 'Use .nfo files', 'Reads details saved next to your files', sw(true))}
  ${row('save', 'Save details as .nfo files', 'Writes them next to your files for other apps', sw(false))}
  ${row('exp2', 'Export .nfo files now', 'For everything already in your library', action('Export'))}
  ${hd('hero', 'Home Hero')}
  ${row('recent', 'Recently added', 'Newest titles take turns on the hero', sw(true))}
  ${row('trend', 'Trending on Trakt', 'What’s trending that you own', sw(true))}
  ${row('pop', 'Popular on Trakt', 'What’s popular that you own', sw(false))}
  ${hd('hist', 'Viewing History')}
  ${row('hist', 'Keep viewing history', 'For Continue Watching, Recently Watched and suggestions', sw(true))}
  ${row('bin', 'Clear viewing history', 'Your library and files are kept', action('Clear'))}`);
const services = page('Services', `${hd('tv', 'Where To Watch')}
  ${row('tv', 'Show where to watch', 'Off turns every streaming feature off', sw(true), true)}
  ${row('country', 'Country', 'Services differ by country', val('🇬🇧 United Kingdom'))}
  ${row('cloud', 'Your services', 'The ones you subscribe to', val('Netflix, Prime Video, Disney+'))}
  ${row('first', 'Show first', 'When a title is on several of your services', val('Netflix'))}
  ${hd('trakt', 'Trakt')}
  ${row('trakt', 'Trakt', 'Signed in as yourname', val('Signed in'))}
  ${row('scrob', 'Scrobble while watching', 'Tells Trakt what you’re watching as you watch', sw(true))}
  ${row('sync', 'Sync resume points', 'Carry on where you stopped on another device', sw(true))}
  ${row('box', 'Sync your library to Trakt', 'Adds the titles you own to your Trakt collection', sw(false))}
  ${row('up', 'Send everything to Trakt again', 'If Trakt is missing things', action('Send'))}
  ${row('down', 'Fetch everything from Trakt again', 'If Supernova is missing things', action('Fetch'))}
  ${row('wipe', 'Remove Supernova’s data from Trakt', 'Watched history Supernova sent; your library is kept', action('Remove'))}
  ${hd('star', 'Ratings')}
  ${row('key', 'OMDb ratings key', 'For IMDb, Rotten Tomatoes and Metacritic scores', val('Key saved'))}
  <div class="credit">Streaming availability from JustWatch</div>`);
const system = page('System', `${hd('ui', 'General')}
  ${row('ui', 'Interface language', 'Language of Supernova’s menus', val('🇬🇧 English'), true)}
  ${hd('backup', 'Backup')}
  ${row('box', 'What to back up', '', chips(['Everything', 'Settings only', 'Choose'], 'Everything'))}
  ${row('backup', 'Back up now', 'Last backup 12 Nov 2026 · Internal storage › Backups', action('Back up'))}
  ${row('restore', 'Restore', 'From a backup file', action('Choose'))}
  ${hd('log', 'Diagnostics')}
  ${row('log', 'Diagnostic logging', 'Records what the app does, for fixing problems', sw(true))}
  ${row('keep', 'Keep diagnostics for', '', chips(['1 week', '2 weeks', '4 weeks'], '2 weeks'))}
  ${row('rep', 'Save a diagnostic report', 'To Internal storage › Diagnostics; never includes passwords', action('Save'))}
  ${row('clr', 'Clear diagnostics', 'After you’ve saved a report', action('Clear'))}
  ${hd('adv', 'Advanced')}
  ${row('proj', 'Projector mode', 'Mirrors the picture for rear projection', sw(false))}
  ${row('dec', 'Force software decoding', 'Only if a video won’t play', sw(false))}
  ${row('chip', 'Video decoder', '', chips(['Automatic', 'Hardware', 'Software'], 'Automatic'))}
  ${row('sync', 'Parser synchronisation', 'How the file and decoder keep in step', chips(['Automatic', 'Strict', 'Relaxed'], 'Automatic'))}
  ${row('box', 'Stream buffer', 'Memory set aside for network playback', chips(['Small', 'Medium', 'Large'], 'Medium'))}
  ${row('audio', 'Audio interface', 'How sound leaves Supernova', chips(['Automatic', 'AudioTrack', 'OpenSL'], 'Automatic'))}
  ${row('speed', 'Sound when speed changes', '', chips(['FFmpeg', 'Sonic'], 'Sonic'))}
  ${row('mix', 'Stereo downmix', '', chips(['Automatic', 'Always', 'Never'], 'Automatic'))}
  ${row('delay', 'Automatic audio delay', 'Estimates the delay between sound and picture', sw(true))}
  ${row('net', 'Network shares', 'SMB, SFTP and discovery options', val('6 options'))}`);
const about = page('About', `<div class="ver"><b>Supernova 0.157</b><small>14 Nov 2026 · NOVA 6.6.4 engine</small></div>
  ${hd('notes', 'What’s New')}
  ${row('notes', '0.157', '14 Nov 2026', val(''), true)}${row('notes', '0.156', '6 Nov 2026', val(''))}
  ${row('notes', '0.155', '4 Nov 2026', val(''))}
  ${hd('info', 'More')}
  ${row('cred', 'Credits', 'Every service and component, with links', val(''))}
  ${row('lic', 'Open-source licences', 'Scan the code to read them on your phone', val(''))}
  ${row('app', 'App information', 'Version, device and storage', val(''))}`);

const mg = t => `<div class="mg"><i></i><span>${t}</span><i></i></div>`;
const li = (lead, t, line, on, f) => `<div class="lr ${f ? 'focus' : ''}">${lead}<span class="lt"><b>${t}</b>${line
  ? `<small>${line}</small>` : ''}</span>${on ? svg('<path d="M5 12.5l4.5 4.5L19 7.5"/>', 26) : ''}</div>`;
const osPanel = `<div class="sl"><div class="acct">${svg(I.key, 40)}<div><b>OpenSubtitles</b><small>Signed in as yourname ·
  12 of 20 downloads left today</small></div></div>${li(svg(I.person, 28), 'Change account', '', false, true)}
  ${li(svg('<path d="M10 17l5-5-5-5M15 12H3M14 4h5a1 1 0 0 1 1 1v14a1 1 0 0 1-1 1h-5"/>', 28), 'Sign out')}</div>`;
const fontPanel = `<div class="sl">${mg('Built In')}${li('<span class="fa" style="font-family:R">Aa</span>', 'Roboto', 'Supernova’s own', true, true)}
  ${li('<span class="fa" style="font-family:serif">Aa</span>', 'Noto Serif')}${li('<span class="fa" style="font-family:monospace">Aa</span>', 'Noto Mono')}
  ${mg('From Your Folder')}${li('<span class="fa" style="font-family:cursive">Aa</span>', 'Anime Ace', '3 subtitle files use it')}
  ${li('<span class="fa" style="font-family:fantasy">Aa</span>', 'Trajan Pro')}</div>`;
const encPanel = `<div class="sl">${li(svg(I.enc, 28), 'Automatic', 'Works for nearly every file', true, true)}${mg('By Language')}
  ${li('🇬🇧', 'Western European', 'English, French, Spanish, German…')}${li('🇵🇱', 'Central European', 'Polish, Czech, Hungarian…')}
  ${li('🇷🇺', 'Cyrillic', 'Russian, Ukrainian, Bulgarian…')}${li('🇬🇷', 'Greek')}${li('🇹🇷', 'Turkish')}
  ${li('🇸🇦', 'Arabic')}${li('🇮🇱', 'Hebrew')}${li('🇯🇵', 'Japanese')}${li('🇨🇳', 'Chinese (Simplified)')}
  ${li('🇰🇷', 'Korean')}</div>`;
const lists = `<div class="lists"><div class="col">${osPanel}${fontPanel}</div>${encPanel}</div>`;

const css = `${font(400)}${font(500)}*{box-sizing:border-box;margin:0}
body{width:1920px;font-family:R;color:#fff;background:#16181C;position:relative;overflow:hidden}
body:after{content:"";position:fixed;inset:0;border:6px solid #8a919c;z-index:99;pointer-events:none}
.nav{position:absolute;left:56px;right:56px;top:24px;height:76px;display:flex;align-items:center;gap:14px;
font-size:33px;color:#8f98a8}.nav .brand{color:#f1ead8;letter-spacing:2px;margin-right:36px;font-weight:500}
.ni{position:relative;display:inline-flex;align-items:center;height:60px;padding:0 18px}.ni.files{margin-left:auto}
.nav .clock{margin-left:18px;color:#C9D3E6}.dull{color:#C9D3E6}.dull:after{content:'';position:absolute;left:18px;
right:18px;bottom:-2px;height:4px;border-radius:2px;background:rgba(255,255,255,.3)}
.page{position:absolute;left:96px;right:96px;top:140px}
.tabs{display:flex;gap:10px;border-bottom:2px solid rgba(255,255,255,.14);padding-bottom:12px}
.tab{position:relative;display:inline-flex;align-items:center;gap:12px;height:60px;padding:0 20px;font-size:30px;
font-weight:500;color:#8f98a8}.tab.line{color:#fff}.tab.line:after{content:'';position:absolute;left:20px;right:20px;
bottom:-14px;height:4px;border-radius:2px;background:#fff}
.hd{display:flex;align-items:center;gap:16px;font-size:32px;font-weight:500;margin:64px 0 26px}.hd svg{color:#C9D3E6}
.tabs+.hd{margin-top:48px}
.row{position:relative;display:flex;align-items:center;gap:26px;min-height:104px;padding:0 30px;border-radius:22px}
.row:after{content:'';position:absolute;left:30px;right:30px;bottom:0;height:2px;background:rgba(255,255,255,.08)}
.row.focus:after,.row:has(+ .hd):after,.row:last-child:after,.row:has(+ .row.focus):after{display:none}
.row>svg{color:#C9D3E6;flex:none}.row.focus{background:#F4F6FB;color:#0B0D12}.row.focus>svg{color:#0B0D12}
.rt{flex:1;display:flex;flex-direction:column;gap:4px}.rt b{font-size:32px;font-weight:500}.rt small{font-size:25px;
color:#C9D3E6}.row.focus .rt small{color:#3A4256}
.chips{display:flex;gap:4px;font-size:27px}.chips i{font-style:normal;display:inline-flex;align-items:center;height:52px;
padding:0 22px;border-radius:14px;white-space:nowrap}.chips i.on{background:#454B55}
.row.focus .chips i.on{background:#0B0D12;color:#fff}
.sw{width:76px;height:42px;border-radius:21px;background:#454B55;position:relative;flex:none}.sw i{position:absolute;
top:5px;left:5px;width:32px;height:32px;border-radius:50%;background:#C9D3E6}.sw.on{background:#F4F6FB}
.sw.on i{left:39px;background:#0B0D12}.row.focus .sw.on{background:#0B0D12}.row.focus .sw.on i{background:#F4F6FB}
.row.focus .sw:not(.on){background:#C9D3E6}.row.focus .sw:not(.on) i{background:#0B0D12}
.v{display:flex;align-items:center;gap:12px;font-size:28px}.v b{font-weight:500;min-width:100px;text-align:center}
.row:has(+ .credit):after{display:none}
.credit{font-size:24px;color:#C9D3E6;margin:40px 30px 0}.ver{margin:48px 30px 0}.ver b{display:block;font-size:40px;font-weight:500}
.ver small{font-size:26px;color:#C9D3E6}
.exp{font-style:normal;font-size:21px;font-weight:500;margin-left:16px;padding:4px 12px;border-radius:12px;
vertical-align:middle;box-shadow:inset 0 0 0 2px rgba(255,255,255,.3)}
.lists{display:flex;gap:40px;padding:56px;align-items:flex-start}.col{display:flex;flex-direction:column;gap:40px}
.sl{width:700px;padding:18px;border-radius:26px;background:rgba(22,24,28,.95);box-shadow:0 0 0 2px rgba(255,255,255,.22)}
.acct{display:flex;gap:20px;align-items:center;padding:14px 20px 20px}.acct svg{color:#C9D3E6}.acct b{display:block;font-size:30px;
font-weight:500}.acct small{font-size:23px;color:#C9D3E6}
.mg{display:flex;align-items:center;gap:14px;padding:16px 16px 8px;font-size:26px;font-weight:500}.mg i{flex:1;height:2px;
background:rgba(255,255,255,.18)}
.lr{position:relative;display:flex;align-items:center;gap:20px;min-height:72px;padding:0 22px;border-radius:16px;font-size:28px}
.lr:after{content:'';position:absolute;left:22px;right:22px;bottom:0;height:2px;background:rgba(255,255,255,.08)}
.lr.focus:after,.lr:last-child:after,.lr:has(+ .mg):after,.lr:has(+ .lr.focus):after{display:none}
.lr.focus{background:#F4F6FB;color:#0B0D12}.lr>svg{color:#C9D3E6}.lr.focus>svg{color:#0B0D12}
.lt{flex:1;display:flex;flex-direction:column}.lt b{font-weight:500}.lt small{font-size:21px;color:#C9D3E6}
.lr.focus .lt small{color:#3A4256}.fa{width:40px;font-size:28px;text-align:center}
.dots{display:flex;gap:16px}.dots i{width:38px;height:38px;border-radius:50%}.dots i.on{box-shadow:0 0 0 3px #16181C,0 0 0 6px #fff}`;
(async () => {
  const browser = await chromium.launch();
  const tab = await browser.newPage({ viewport: { width: 1920, height: 1080 } });
  for (const [f, body] of [['09-b2-subtitles.png', subtitles], ['10-b2-library.png', library], ['11-b2-services.png', services],
    ['12-b2-system.png', system], ['13-b2-about.png', about]]) {
    await tab.setContent(`<html><head><style>${css}</style></head><body>${body}</body></html>`, { waitUntil: 'load' });
    await tab.evaluate(() => document.fonts.ready);
    const h = await tab.evaluate(() => Math.max(1080, (document.querySelector('.page') || document.querySelector('.lists')).getBoundingClientRect().bottom + 80));
    await tab.setViewportSize({ width: 1920, height: Math.ceil(h) });
    await tab.addStyleTag({ content: `body{height:${Math.ceil(h)}px}` });
    await tab.screenshot({ path: path.join(__dirname, f) });
    await tab.setViewportSize({ width: 1920, height: 1080 });
  }
  await browser.close();
})();
