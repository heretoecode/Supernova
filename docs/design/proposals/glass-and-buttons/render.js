// Proposal (2 Nov 2026): one "glass" material and one button style across Supernova.
// Renders 1920x1080 mock-ups with Playwright. Run: node render.js
const fs = require('fs');
const path = require('path');
const { chromium } = require('playwright');
const out = __dirname;
const flag = c => `data:image/png;base64,${fs.readFileSync(path.join(__dirname, '../../../../res/drawable-nodpi/region_' + c + '.png')).toString('base64')}`;

// ---- tokens under proposal ----
const GLASS = 'rgba(26,31,92,0.88)';        // panels, pop-ups and resting buttons
const SOLID = 'rgb(26,31,92)';              // option B: panels over text-heavy pages
const EDGE = 'rgba(255,255,255,0.40)';      // resting outline
const ACCENT = '#2f6df0';                   // focused / selected fill
const RADIUS = 12;

const icon = {
  gear: '<circle cx="12" cy="12" r="3.2"/><path d="M12 2.5v3M12 18.5v3M2.5 12h3M18.5 12h3M5.3 5.3l2.1 2.1M16.6 16.6l2.1 2.1M5.3 18.7l2.1-2.1M16.6 7.4l2.1-2.1"/>',
  pause: '<rect x="6" y="5" width="4" height="14" rx="1" fill="currentColor"/><rect x="14" y="5" width="4" height="14" rx="1" fill="currentColor"/>',
  info: '<circle cx="12" cy="12" r="9"/><path d="M12 11v6M12 7.5v.5"/>',
  filter: '<path d="M4 5h16l-6 7v6l-4 2v-8z"/>',
  sort: '<path d="M7 4v16M3 16l4 4 4-4M17 20V4M13 8l4-4 4 4"/>',
  list: '<path d="M9 6h11M9 12h11M9 18h11"/><circle cx="4.5" cy="6" r="1"/><circle cx="4.5" cy="12" r="1"/><circle cx="4.5" cy="18" r="1"/>',
  cc: '<rect x="3" y="5" width="18" height="14" rx="2"/><path d="M10 10.2a2.2 2.2 0 1 0 0 3.6M16.5 10.2a2.2 2.2 0 1 0 0 3.6"/>',
  speaker: '<path d="M4 9h4l5-4v14l-5-4H4z"/><path d="M16 9a4 4 0 0 1 0 6"/>',
  play: '<circle cx="12" cy="12" r="9"/><path d="M10 8.5l5 3.5-5 3.5z" fill="currentColor"/>',
  down: '<path d="M12 4v11M7 10.5l5 5 5-5M4 20h16"/>',
  aa: '<text x="2" y="18" font-size="15" font-family="Roboto" stroke="none" fill="currentColor">Aa</text>',
  palette: '<circle cx="12" cy="12" r="9"/><circle cx="8" cy="10" r="1.2"/><circle cx="12" cy="7.5" r="1.2"/><circle cx="16" cy="10" r="1.2"/>',
  vpos: '<path d="M12 3v18M8 7l4-4 4 4M8 17l4 4 4-4"/>',
  outline: '<rect x="4" y="3" width="16" height="18" stroke-dasharray="2 2"/>',
  bg: '<rect x="4" y="4" width="16" height="16"/><path d="M4 12l8-8M4 18l14-14M10 20l10-10"/>',
  drop: '<path d="M12 3c4 5 6 8 6 11a6 6 0 0 1-12 0c0-3 2-6 6-11z"/>',
  timer: '<circle cx="12" cy="13" r="8"/><path d="M12 13V9M9 2h6"/>',
  edit: '<path d="M4 20l4-1 11-11-3-3L5 16z"/>',
  film: '<rect x="3" y="4" width="18" height="16" rx="1.5"/><path d="M7 4v16M17 4v16M3 9h4M3 15h4M17 9h4M17 15h4"/>',
  tag: '<path d="M3 12V4h8l10 10-8 8z"/><circle cx="7.5" cy="8.5" r="1.3"/>',
  eye: '<path d="M2 12s4-7 10-7 10 7 10 7-4 7-10 7S2 12 2 12z"/><circle cx="12" cy="12" r="3"/>',
  star: '<path d="M12 3l2.7 5.6 6.1.9-4.4 4.3 1 6.1L12 17l-5.4 2.9 1-6.1-4.4-4.3 6.1-.9z"/>',
  grid: '<rect x="4" y="4" width="7" height="7"/><rect x="13" y="4" width="7" height="7"/><rect x="4" y="13" width="7" height="7"/><rect x="13" y="13" width="7" height="7"/>',
};
const svg = (n, s = 28) => `<svg width="${s}" height="${s}" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.7" stroke-linecap="round" stroke-linejoin="round">${icon[n]}</svg>`;

const css = `
*{box-sizing:border-box;margin:0;padding:0}
body{width:1920px;height:1080px;overflow:hidden;font-family:Roboto,"Noto Sans",Arial,sans-serif;color:#fff;background:#000;position:relative}
.film{position:absolute;inset:0;background:
  radial-gradient(ellipse 500px 360px at 30% 40%,rgba(255,226,180,.9),transparent 70%),
  linear-gradient(180deg,#7fb2d8 0%,#a9c9de 35%,#d6a77e 60%,#5a3b30 61%,#2a1d22 100%)}
.film .figure{position:absolute;left:700px;bottom:200px;width:90px;height:300px;border-radius:45px 45px 10px 10px;background:#1a1012}
.tag{position:absolute;left:40px;top:34px;font-size:20px;color:rgba(255,255,255,.92);background:rgba(0,0,0,.55);padding:8px 14px;border-radius:10px;z-index:10}
/* the one button style */
.btn{display:inline-flex;align-items:center;gap:12px;height:64px;padding:0 26px;border-radius:${RADIUS}px;font-size:24px;font-weight:500;
  background:${GLASS};border:2px solid ${EDGE};color:#fff}
.btn.focus{background:${ACCENT};border:3px solid #fff;box-shadow:0 0 0 1px rgba(0,0,0,.25),0 6px 18px rgba(0,0,0,.35)}
.btn.selected{background:${ACCENT};border:2px solid rgba(255,255,255,.25)}
.btn.disabled{opacity:.4}
.btn.icon{width:64px;padding:0;justify-content:center}
.btn.big{height:72px;font-size:26px}
/* glass panels */
.panel{position:absolute;border-radius:26px;background:${GLASS};border:2px solid ${EDGE};box-shadow:0 20px 60px rgba(0,0,0,.45);padding:30px 30px 26px}
.panel.solid{background:${SOLID}}
.title{display:flex;align-items:center;gap:14px;font-size:30px;font-weight:700;margin-bottom:22px}
.tabs{display:grid;grid-template-columns:repeat(3,1fr);gap:12px;margin-bottom:16px}
.tabs .btn{height:56px;font-size:19px;font-weight:600;justify-content:center;padding:0}
.head{font-size:16px;font-weight:700;color:#9cc9f0;letter-spacing:.9px;padding:14px 4px 8px;border-bottom:1.5px solid rgba(156,201,240,.35)}
.row{display:flex;align-items:center;height:62px;padding:0 16px;position:relative;border-radius:${RADIUS}px;border:3px solid transparent;margin:2px 0}
.row:after{content:'';position:absolute;left:16px;right:16px;bottom:-3px;height:1.5px;background:rgba(255,255,255,.14)}
.row.focus{border-color:#fff}
.row.focus:after,.row.last:after{display:none}
.row .ic{width:46px;display:flex;color:#e8eeff}
.row .label{flex:1;font-size:21px;font-weight:600}
.row .value{display:flex;align-items:center;gap:12px;font-size:20px}
.row.dim{opacity:.42}
.sel{color:#6cc4ff}.bar{color:rgba(255,255,255,.5);margin:0 4px}
.toggle{width:58px;height:34px;border-radius:17px;background:#3a4a77;position:relative;display:inline-block}
.toggle.on{background:${ACCENT}}
.toggle i{position:absolute;top:3px;left:3px;width:28px;height:28px;border-radius:50%;background:#fff}
.toggle.on i{left:27px}
.dot{width:28px;height:28px;border-radius:50%;display:inline-block}
.dot.sel{outline:3px solid #fff;outline-offset:3px}
.step b{font-weight:400;font-size:26px;margin:0 16px}
.flag{width:32px;height:24px;object-fit:cover;border-radius:3px}
`;
const doc = body => `<!doctype html><html><head><meta charset="utf-8"><style>${css}</style></head><body>${body}</body></html>`;
const toggle = on => `<span class="toggle ${on ? 'on' : ''}"><i></i></span>`;
const choices = (o, s) => o.map((x, i) => `${i ? '<span class="bar">|</span>' : ''}<span class="${i === s ? 'sel' : ''}">${x}</span>`).join('');
const row = (ic, label, value, cls = '') => `<div class="row ${cls}"><span class="ic">${svg(ic)}</span><span class="label">${label}</span><span class="value">${value}</span></div>`;

// ---------- 1. Playback Settings over a bright film frame ----------
const playback = doc(`<div class="film"><div class="figure"></div></div>
<div class="tag">Proposal · glass 88% · white focus with padding · tabs never change size</div>
<div class="panel" style="top:40px;right:40px;width:640px">
  <div class="title">${svg('gear', 32)}Playback Settings</div>
  <div class="tabs"><span class="btn selected">${svg('cc', 22)}Subtitles</span><span class="btn">${svg('speaker', 22)}Audio</span><span class="btn">${svg('play', 22)}Playback</span></div>
  <div class="head">SUBTITLE SELECTION</div>
  ${row('cc', 'Subtitles', toggle(true))}
  ${row('list', 'Subtitle Track', `<span class="step"><b>‹</b></span><img class="flag" src="${flag('gb')}"><span class="sel">English</span><span class="step"><b>›</b></span>`, 'focus')}
  ${row('down', 'Download Subtitles', 'Find &amp; Download <span class="step"><b style="margin-right:0">›</b></span>', 'last')}
  <div class="head">SUBTITLE APPEARANCE</div>
  ${row('aa', 'Text Size', choices(['Small', 'Medium', 'Large'], 1))}
  ${row('palette', 'Text Colour', ['#fff', '#ffc93c', '#19e6e6', '#4cc554', '#f06a9b'].map((c, i) => `<span class="dot ${i === 0 ? 'sel' : ''}" style="background:${c}"></span>`).join(''))}
  ${row('vpos', 'Vertical Position', choices(['Bottom', 'Middle', 'Top'], 0))}
  ${row('outline', 'Outline', toggle(true))}
  ${row('bg', 'Background', toggle(false))}
  ${row('drop', 'Background Opacity', '<span class="step"><b>‹</b></span><span class="sel">100%</span><span class="step"><b>›</b></span>', 'dim')}
  ${row('timer', 'Subtitle Delay', '<span class="step"><b>−</b></span>0 ms<span class="step"><b>+</b></span>', 'last')}
</div>`);

// ---------- 2. HUD over the same frame ----------
const hud = doc(`<div class="film"><div class="figure"></div></div>
<div class="tag">Proposal · HUD buttons in the same glass · Settings focused (solid accent + white outline)</div>
<div style="position:absolute;left:0;right:0;bottom:0;height:260px;background:linear-gradient(180deg,transparent,rgba(0,0,0,.65))"></div>
<div style="position:absolute;left:56px;right:56px;bottom:60px;display:flex;align-items:center;gap:28px">
  <span class="btn icon big">${svg('pause', 30)}</span>
  <div style="flex:1;height:6px;border-radius:3px;background:rgba(255,255,255,.35);position:relative"><div style="width:28%;height:100%;border-radius:3px;background:${ACCENT}"></div></div>
  <span style="font-size:26px">02:00:00 · Ends at 22:38</span>
  <span class="btn big focus">${svg('gear', 30)}Settings</span>
</div>`);

// ---------- 3. Button sheet: every place, every state ----------
const library = `background:linear-gradient(135deg,#0b1830,#06101f 55%,#123a6b)`;
const hero = `background:linear-gradient(100deg,#0d1b2a 0%,#173a5e 45%,#c98a52 75%,#f2d29b 100%)`;
const cell = (bg, inner) => `<div style="${bg};height:150px;border-radius:14px;display:flex;align-items:center;justify-content:center;gap:16px;overflow:hidden">${inner}</div>`;
// Built by rows for readability:
const rows = [
  ['Playback HUD', 'over the film', 'background:linear-gradient(180deg,#9fc3dc,#d6a77e)', l => `<span class="btn big ${l}">${svg('gear', 28)}Settings</span>`],
  ['Library toolbar', 'over the library page', library, l => `<span class="btn ${l}">${svg('sort', 24)}Newest First</span>`],
  ['Home hero', 'over artwork', hero, l => `<span class="btn big ${l}">${svg('info', 28)}More Info</span>`],
  ['Panel tabs', 'inside a glass panel', `background:${GLASS};border:2px solid ${EDGE}`, l => `<span class="btn ${l}" style="height:56px;font-size:19px;width:220px;justify-content:center">${svg('cc', 22)}Subtitles</span>`],
];
const sheetHtml = doc(`<div style="position:absolute;inset:0;background:#05080d;padding:40px 44px">
<div style="font-size:32px;font-weight:700;margin-bottom:8px">One button style — every place, every state</div>
<div style="font-size:19px;color:#b9c7e8;margin-bottom:26px">Resting: navy glass 88% + soft white outline · Focused: solid accent + white outline, <b>same size</b> · Selected: accent, no white outline · Disabled: 40%</div>
<div style="display:grid;grid-template-columns:250px repeat(4,1fr);gap:18px;align-items:center">
  <div></div>${['RESTING', 'FOCUSED', 'SELECTED · not focused', 'DISABLED'].map(t => `<div style="color:#9cc9f0;font-size:17px;font-weight:700;letter-spacing:.8px">${t}</div>`).join('')}
  ${rows.map(([name, sub, bg, b]) => `<div style="font-size:22px;font-weight:600">${name}<div style="color:#b9c7e8;font-size:16px;font-weight:400;margin-top:4px">${sub}</div></div>
    ${['', 'focus', 'selected', 'disabled'].map(s => cell(bg, b(s))).join('')}`).join('')}
</div></div>`);

// ---------- 4/5. Edit Page over a text-heavy library page: option A (88%) and B (solid) ----------
const libraryPage = `<div style="position:absolute;inset:0;background:linear-gradient(135deg,#0a1526,#05090f 60%,#0f2f57)">
  <div style="position:absolute;left:56px;top:28px;display:flex;gap:44px;font-size:34px;color:rgba(255,255,255,.85)"><b style="font-weight:500">SUPERNOVA</b><span>Home</span><span>Movies</span><span>TV Shows</span><span style="border-bottom:3px solid #fff">Documentaries</span></div>
  <div style="position:absolute;left:56px;right:56px;top:112px;height:210px;border-radius:14px;background:rgba(40,70,120,.45);display:grid;grid-template-columns:repeat(4,1fr);padding:22px 26px;gap:12px;font-size:22px;color:#cfd8ef">
    ${['Total Library|32 Titles', 'Total Size|119 GB', 'Local Storage|28 · 101 GB', 'Network / WebDAV|4 · 18 GB', 'Unwatched|21', 'In Progress|2', 'Added This Month|3', 'Library Status|No scan in progress'].map(s => { const [a, b] = s.split('|'); return `<div>${a}<div style="font-size:30px;font-weight:700;color:#fff">${b}</div></div>`; }).join('')}
  </div>
  <div style="position:absolute;left:56px;top:350px;display:flex;gap:16px"><span class="btn">${svg('filter', 24)}Filters</span><span class="btn">${svg('sort', 24)}Sort: Date Added</span><span class="btn">Newest First</span><span class="btn">${svg('list', 24)}List View</span></div>
  <div style="position:absolute;left:56px;right:56px;top:450px;display:grid;grid-template-columns:repeat(6,1fr);gap:18px">
    ${['Planet Earth III', 'Free Solo', 'My Octopus Teacher', 'The Last Dance', 'Apollo 11', 'Fire of Love', 'Our Planet', 'Senna', 'Man on Wire', '13th', 'Icarus', 'Jiro Dreams of Sushi'].map((t, i) => `<div style="height:270px;border-radius:12px;background:linear-gradient(160deg,hsl(${i * 37 % 360},45%,42%),hsl(${(i * 37 + 40) % 360},45%,18%));display:flex;align-items:flex-end;padding:14px;font-size:21px;font-weight:600">${t}</div>`).join('')}
  </div></div>`;
const editRows = `
  <div class="head">PAGE</div>
  ${row('edit', 'Page Name', 'Documentaries <span class="step"><b style="margin-right:0">›</b></span>', 'focus')}
  ${row('film', 'Includes', choices(['Movies', 'TV Shows', 'Both'], 2), 'last')}
  <div class="head">WHAT TO SHOW</div>
  ${row('tag', 'Genres', 'Documentary, Nature +1 <span class="step"><b style="margin-right:0">›</b></span>')}
  ${row('filter', 'Match', choices(['Any', 'All'], 0))}
  ${row('eye', 'Watched', '<span class="step"><b>‹</b></span><span class="sel">All</span><span class="step"><b>›</b></span>')}
  ${row('star', 'Minimum Rating', '<span class="step"><b>‹</b></span><span class="sel">Any</span><span class="step"><b>›</b></span>', 'last')}
  <div class="head">HOW TO SHOW IT</div>
  ${row('grid', 'View', choices(['Grid', 'List'], 0))}
  ${row('sort', 'Sort By', choices(['Title', 'Year', 'Date Added'], 2), 'last')}`;
const editPage = (solid, label) => doc(`${libraryPage}<div class="tag" style="top:auto;bottom:24px">${label}</div>
<div class="panel ${solid ? 'solid' : ''}" style="top:96px;right:40px;width:690px">
  <div style="display:flex;align-items:center;margin-bottom:8px"><div style="flex:1"><div style="font-size:32px;font-weight:700">Edit Page</div><div style="color:#4cd3a0;font-size:18px;margin-top:6px">32 titles match · 119 GB</div></div><span class="btn" style="height:50px;font-size:19px">✓ Save Changes</span></div>
  ${editRows}
</div>`);


// ---------- 6/7. Context menus (owner photos, 2 Nov): keep their structure and focus, apply the shared glass ----------
const details = `<div style="position:absolute;inset:0;background:radial-gradient(ellipse 900px 700px at 80% 30%,#6c8db5,#14233a 60%,#05080d)">
  <div style="position:absolute;left:80px;top:70px;width:1100px;font-size:92px;font-weight:900;letter-spacing:4px;color:#e9eef6">TERMINATOR 2</div>
  <div style="position:absolute;left:84px;top:180px;font-size:44px;letter-spacing:22px;color:#e9eef6">JUDGMENT DAY</div>
  <div style="position:absolute;left:84px;top:290px;font-size:26px;color:#dfe7f2">15 · 1991 · Thriller, Action, Science Fiction · 2h 35m · TMDb 8.2/10</div>
  <div style="position:absolute;left:84px;top:380px;width:1150px;font-size:28px;line-height:44px;color:#e9eef6">Ten years after the events of the original, a reprogrammed T-800 is sent back in time to protect young John Connor from the shape-shifting T-1000. Together with his mother Sarah, he fights to stop Skynet from triggering a nuclear apocalypse.</div>
  <div style="position:absolute;left:84px;top:600px;display:flex;gap:20px"><span class="btn big">${svg('play', 28)}Play</span><span class="btn big">··· More</span></div>
  <div style="position:absolute;left:84px;top:760px;font-size:30px;font-weight:700">Description</div>
  <div style="position:absolute;left:84px;top:810px;width:1500px;font-size:26px;line-height:40px;color:#cfd8ef">Key Information · Directed by James Cameron · Starring Arnold Schwarzenegger, Linda Hamilton, Edward Furlong, Robert Patrick · Carolco Pictures · 137 minutes · English</div>
</div>`;
const menuRow = (ic, label, focus) => `<div class="row ${focus ? 'focus' : ''} last" style="height:72px;padding-right:40px"><span class="ic">${svg(ic, 30)}</span><span class="label" style="font-weight:500;font-size:24px">${label}</span></div>`;
const menuHead = t => `<div style="color:#9cc9f0;font-size:19px;padding:16px 16px 6px">${t}</div>`;
const actions = (glass, label) => doc(`${details}<div class="tag" style="top:auto;bottom:24px">${label}</div>
<div class="panel" style="top:150px;left:820px;width:620px;padding:26px 22px;background:${glass}">
  <div style="font-size:30px;font-weight:500;padding:4px 16px 14px">Actions</div>
  ${menuRow('play', 'Restart Movie', true)}${menuRow('info', 'Report Incorrect Match')}
  ${menuHead('Library')}${menuRow('eye', 'Mark Watched')}${menuRow('list', 'Remove From Library')}${menuRow('list', 'Add to Row')}
  ${menuHead('Files &amp; Media')}${menuRow('down', 'Delete')}${menuRow('cc', 'Subtitles and Artwork')}
</div>`);


// ---------- 10. Focused-button options (owner: the solid blue is not elegant enough) ----------
const focusCss = `
.f-a{background:#f4f6fb;color:#0d1240;border:3px solid #fff}
.f-b{background:rgba(26,31,92,0.97);border:3px solid #fff;box-shadow:0 0 0 1px rgba(0,0,0,.2)}
.f-c{background:rgba(255,255,255,.18);border:3px solid #fff;backdrop-filter:none}
.f-d{background:#3d4f86;border:3px solid #fff}
.f-a svg{color:#0d1240}`;
const options = [
  ['A · White', 'white fill, navy text', 'f-a'],
  ['B · Outline only', 'resting glass + white outline', 'f-b'],
  ['C · Frosted', 'soft white tint + white outline', 'f-c'],
  ['D · Muted navy', 'lighter navy + white outline', 'f-d'],
];
const places = [
  ['Playback HUD', 'background:linear-gradient(180deg,#9fc3dc,#d6a77e)', c => `<span class="btn big ${c}">${svg('gear', 28)}Settings</span>`],
  ['Library toolbar', library, c => `<span class="btn ${c}">${svg('sort', 24)}Newest First</span>`],
  ['Home hero', hero, c => `<span class="btn big ${c}">${svg('info', 28)}More Info</span>`],
  ['Panel tabs', `background:rgba(26,31,92,0.97);border:2px solid ${EDGE}`, c => `<span class="btn ${c}" style="height:56px;font-size:19px;width:220px;justify-content:center">${svg('cc', 22)}Subtitles</span>`],
];
const focusSheet = doc(`<style>${focusCss}</style><div style="position:absolute;inset:0;background:#05080d;padding:36px 44px">
<div style="font-size:32px;font-weight:700;margin-bottom:6px">Focused button — four calmer options</div>
<div style="font-size:19px;color:#b9c7e8;margin-bottom:22px">Every option keeps the same size as resting and a white outline. Left column is the resting button for reference.</div>
<div style="display:grid;grid-template-columns:200px repeat(5,1fr);gap:14px;align-items:center">
  <div></div><div style="color:#9cc9f0;font-size:16px;font-weight:700">RESTING</div>${options.map(([t, d]) => `<div style="color:#9cc9f0;font-size:16px;font-weight:700">${t}<div style="color:#b9c7e8;font-weight:400;font-size:14px">${d}</div></div>`).join('')}
  ${places.map(([name, bg, b]) => `<div style="font-size:20px;font-weight:600">${name}</div>${cell(bg, b(''))}${options.map(([, , c]) => cell(bg, b(c))).join('')}`).join('')}
</div></div>`);


// ---------- 11. Boxless resting buttons, white focus (owner idea, Netflix-like) ----------
const bl = `
.bx{display:inline-flex;align-items:center;gap:12px;height:64px;padding:0 26px;border-radius:${RADIUS}px;font-size:24px;font-weight:500;color:#fff;border:3px solid transparent;
  text-shadow:0 1px 3px rgba(0,0,0,.75),0 0 12px rgba(0,0,0,.45)}
.bx svg{filter:drop-shadow(0 1px 2px rgba(0,0,0,.7))}
.bx.big{height:72px;font-size:26px}
.bx.focus{background:#f4f6fb;color:#0d1240;text-shadow:none;border-color:#fff}
.bx.focus svg{filter:none}`;
const boxless = [
  ['Playback HUD', 'background:linear-gradient(180deg,#9fc3dc 0%,#d6a77e 55%,rgba(20,12,14,.9) 100%)', (f1, f2) => `<span class="bx big ${f1}">${svg('pause', 28)}</span><span class="bx big ${f2}">${svg('gear', 28)}Settings</span>`],
  ['Library toolbar', library, (f1, f2) => `<span class="bx ${f1}">${svg('sort', 24)}Newest First</span><span class="bx ${f2}">${svg('list', 24)}List View</span>`],
  ['Home hero', hero, (f1, f2) => `<span class="bx big ${f1}">${svg('play', 28)}Play</span><span class="bx big ${f2}">${svg('info', 28)}More Info</span>`],
  ['Panel tabs', `background:rgba(26,31,92,0.97);border:2px solid ${EDGE}`, (f1, f2) => `<span class="bx ${f1}" style="height:56px;font-size:19px">${svg('cc', 22)}Subtitles</span><span class="bx ${f2}" style="height:56px;font-size:19px">${svg('speaker', 22)}Audio</span>`],
];
const boxlessSheet = doc(`<style>${bl}</style><div style="position:absolute;inset:0;background:#05080d;padding:36px 44px">
<div style="font-size:32px;font-weight:700;margin-bottom:6px">Boxless resting buttons · white box only on focus</div>
<div style="font-size:19px;color:#b9c7e8;margin-bottom:22px">Resting: icon + text with a soft shadow, no box · Focused: white box, navy text, same footprint — nothing moves</div>
<div style="display:grid;grid-template-columns:200px 1fr 1fr;gap:16px;align-items:center">
  <div></div><div style="color:#9cc9f0;font-size:16px;font-weight:700">NOTHING FOCUSED</div><div style="color:#9cc9f0;font-size:16px;font-weight:700">SECOND BUTTON FOCUSED</div>
  ${boxless.map(([name, bg, b]) => `<div style="font-size:20px;font-weight:600">${name}</div>${cell(bg, b('', ''))}${cell(bg, b('', 'focus'))}`).join('')}
</div></div>`);

// ---------- 12. Context menu fitted to its longest item (97% glass) ----------
const fitted = doc(`${details}<div class="tag" style="top:auto;bottom:24px">Menu width = longest item + 24px each side (min 360px, max 40% of the screen) · glass 97%</div>
<div class="panel" style="top:150px;left:820px;width:auto;display:inline-block;padding:26px 22px;background:rgba(26,31,92,0.97)">
  <div style="font-size:30px;font-weight:500;padding:4px 16px 14px">Actions</div>
  ${menuRow('play', 'Restart Movie', true)}${menuRow('info', 'Report Incorrect Match')}
  ${menuHead('Library')}${menuRow('eye', 'Mark Watched')}${menuRow('list', 'Remove From Library')}${menuRow('list', 'Add to Row')}
  ${menuHead('Files &amp; Media')}${menuRow('down', 'Delete')}${menuRow('cc', 'Subtitles and Artwork')}
</div>`);


// ---------- 13. Menu groups: every item under a heading; headings floating vs. divider between groups ----------
const groupMenu = (dividers, x, caption) => {
  const head = (t, first) => `${dividers && !first ? '<div style="height:1.5px;background:rgba(255,255,255,.18);margin:12px 16px 4px"></div>' : ''}<div style="color:#9cc9f0;font-size:19px;padding:${first ? 6 : (dividers ? 10 : 22)}px 16px 6px">${t}</div>`;
  return `<div style="position:absolute;top:110px;left:${x}px">
  <div style="font-size:18px;color:#b9c7e8;margin-bottom:12px">${caption}</div>
  <div class="panel" style="position:relative;display:inline-block;width:auto;padding:26px 22px;background:rgba(26,31,92,0.97)">
  <div style="font-size:30px;font-weight:500;padding:4px 16px 10px">Actions</div>
  ${head('Playback', true)}${menuRow('play', 'Restart Movie', true)}
  ${head('Library')}${menuRow('eye', 'Mark Watched')}${menuRow('list', 'Add to Row')}${menuRow('info', 'Report Incorrect Match')}${menuRow('list', 'Remove From Library')}
  ${head('Files &amp; Media')}${menuRow('down', 'Delete')}${menuRow('cc', 'Subtitles and Artwork')}
  </div></div>`;
};
const groups = doc(`${details}<div style="position:absolute;inset:0;background:rgba(0,0,0,.35)"></div>
<div class="tag">Every item sits under a heading · Report Incorrect Match moves to Library · left: floating headings · right: thin line between groups</div>
${groupMenu(false, 300, 'A · Floating headings (extra space above)')}${groupMenu(true, 1040, 'B · Thin line between groups')}`);

// ---------- 14. Boxless buttons over the worst backgrounds ----------
const worst = [
  ['Pure white', 'background:#ffffff'],
  ['Bright yellow', 'background:#ffd84a'],
  ['Pale sky', 'background:linear-gradient(180deg,#cfe6f7,#eaf3fa)'],
  ['Busy artwork', 'background:repeating-linear-gradient(115deg,#f4e2c4 0 22px,#2b6cb0 22px 30px,#ffffff 30px 46px,#e2583e 46px 52px)'],
  ['Dark film', 'background:#0b0d12'],
];
const band = 'background:linear-gradient(180deg,rgba(5,8,13,0) 0%,rgba(5,8,13,.72) 30%,rgba(5,8,13,.78) 70%,rgba(5,8,13,0) 100%)';
const worstSheet = doc(`<style>${bl}</style><div style="position:absolute;inset:0;background:#05080d;padding:36px 44px">
<div style="font-size:32px;font-weight:700;margin-bottom:6px">Boxless buttons — worst-case backgrounds</div>
<div style="font-size:19px;color:#b9c7e8;margin-bottom:22px">Left: soft shadow only (fails on bright backgrounds) · Right: the button row sits on a dark gradient band, as Netflix does</div>
<div style="display:grid;grid-template-columns:200px 1fr 1fr;gap:14px;align-items:center">
  <div></div><div style="color:#9cc9f0;font-size:16px;font-weight:700">SHADOW ONLY</div><div style="color:#9cc9f0;font-size:16px;font-weight:700">ON A DARK GRADIENT BAND</div>
  ${worst.map(([n, bg]) => `<div style="font-size:20px;font-weight:600">${n}</div>
    ${cell(bg, `<span class="bx big">${svg('play', 28)}Play</span><span class="bx big">${svg('info', 28)}More Info</span><span class="bx big focus">${svg('gear', 28)}Settings</span>`)}
    ${cell(bg, `<div style="${band};display:flex;gap:16px;align-items:center;justify-content:center;width:100%;height:120px"><span class="bx big">${svg('play', 28)}Play</span><span class="bx big">${svg('info', 28)}More Info</span><span class="bx big focus">${svg('gear', 28)}Settings</span></div>`)}`).join('')}
</div></div>`);


// ---------- 15. Group heading with the line beside it, spanning the rest of the menu ----------
const inlineMenu = (() => {
  const head = (t, first) => `<div style="display:flex;align-items:center;gap:14px;color:#9cc9f0;font-size:19px;padding:${first ? 6 : 18}px 16px 6px">${t}<span style="flex:1;height:1.5px;background:rgba(156,201,240,.35)"></span></div>`;
  return doc(`${details}<div style="position:absolute;inset:0;background:rgba(0,0,0,.35)"></div>
<div class="tag">Group headings with the line beside the text, spanning the rest of the menu</div>
<div class="panel" style="top:130px;left:820px;display:inline-block;width:auto;padding:26px 22px;background:rgba(26,31,92,0.97)">
  <div style="font-size:30px;font-weight:500;padding:4px 16px 10px">Actions</div>
  ${head('Playback', true)}${menuRow('play', 'Restart Movie', true)}
  ${head('Library')}${menuRow('eye', 'Mark Watched')}${menuRow('list', 'Add to Row')}${menuRow('info', 'Report Incorrect Match')}${menuRow('list', 'Remove From Library')}
  ${head('Files &amp; Media')}${menuRow('down', 'Delete')}${menuRow('cc', 'Subtitles and Artwork')}
</div>`);
})();

// ---------- 16. Boxed resting buttons: black box + white text; focus: white box + black text ----------
const bk = `
.bk{display:inline-flex;align-items:center;gap:12px;height:64px;padding:0 26px;border-radius:${RADIUS}px;font-size:24px;font-weight:500;color:#fff;border:2px solid transparent}
.bk.big{height:72px;font-size:26px}
.bk.r1{background:rgba(0,0,0,.72)}
.bk.r2{background:rgba(0,0,0,.72);border-color:rgba(255,255,255,.28)}
.bk.focus{background:#f4f6fb;color:#0b0d12;border-color:#fff}`;
const bkPlaces = [
  ['Playback HUD', 'background:linear-gradient(180deg,#9fc3dc 0%,#d6a77e 55%,rgba(20,12,14,.9) 100%)', (r, f) => `<span class="bk big ${r}">${svg('pause', 28)}</span><span class="bk big ${f}">${svg('gear', 28)}Settings</span>`],
  ['Library toolbar', library, (r, f) => `<span class="bk ${r}">${svg('sort', 24)}Newest First</span><span class="bk ${f}">${svg('list', 24)}List View</span>`],
  ['Home hero', hero, (r, f) => `<span class="bk big ${r}">${svg('play', 28)}Play</span><span class="bk big ${f}">${svg('info', 28)}More Info</span>`],
  ['Panel tabs', `background:rgba(26,31,92,0.97);border:2px solid ${EDGE}`, (r, f) => `<span class="bk ${r}" style="height:56px;font-size:19px">${svg('cc', 22)}Subtitles</span><span class="bk ${f}" style="height:56px;font-size:19px">${svg('speaker', 22)}Audio</span>`],
  ['Pure white (worst)', 'background:#ffffff', (r, f) => `<span class="bk big ${r}">${svg('play', 28)}Play</span><span class="bk big ${f}">${svg('info', 28)}More Info</span>`],
  ['Bright yellow (worst)', 'background:#ffd84a', (r, f) => `<span class="bk big ${r}">${svg('play', 28)}Play</span><span class="bk big ${f}">${svg('info', 28)}More Info</span>`],
];
const boxedSheet = doc(`<style>${bk}</style><div style="position:absolute;inset:0;background:#05080d;padding:30px 44px">
<div style="font-size:32px;font-weight:700;margin-bottom:6px">Boxed buttons · black at rest, white on focus</div>
<div style="font-size:19px;color:#b9c7e8;margin-bottom:18px">In each cell the first button is resting and the second is focused · same size in both states</div>
<div style="display:grid;grid-template-columns:230px 1fr 1fr;gap:12px;align-items:center">
  <div></div><div style="color:#9cc9f0;font-size:16px;font-weight:700">RESTING: BLACK 72%, NO OUTLINE</div><div style="color:#9cc9f0;font-size:16px;font-weight:700">RESTING: BLACK 72% + FAINT OUTLINE</div>
  ${bkPlaces.map(([n, bg, b]) => `<div style="font-size:19px;font-weight:600">${n}</div>${cell(bg, b('r1', 'focus')).replace('height:150px', 'height:128px')}${cell(bg, b('r2', 'focus')).replace('height:150px', 'height:128px')}`).join('')}
</div></div>`);


// ---------- 17. Menu title: none, or the item's name ----------
const titleMenus = (() => {
  const head = (t, first) => `<div style="display:flex;align-items:center;gap:14px;color:#9cc9f0;font-size:19px;padding:${first ? 6 : 18}px 16px 6px">${t}<span style="flex:1;height:1.5px;background:rgba(156,201,240,.35)"></span></div>`;
  const body = `${head('Playback', true)}${menuRow('play', 'Restart Movie', true)}
    ${head('Library')}${menuRow('eye', 'Mark Watched')}${menuRow('list', 'Add to Row')}${menuRow('info', 'Report Incorrect Match')}${menuRow('list', 'Remove From Library')}
    ${head('Files &amp; Media')}${menuRow('down', 'Delete')}${menuRow('cc', 'Subtitles and Artwork')}`;
  const menu = (x, caption, title) => `<div style="position:absolute;top:96px;left:${x}px">
    <div style="font-size:18px;color:#e8eeff;background:rgba(0,0,0,.55);display:inline-block;padding:6px 12px;border-radius:8px;margin-bottom:12px">${caption}</div><br>
    <div class="panel" style="position:relative;display:inline-block;width:auto;padding:${title ? 24 : 18}px 22px 22px;background:rgba(26,31,92,0.97)">${title}${body}</div></div>`;
  const itemTitle = `<div style="display:flex;align-items:center;gap:16px;padding:2px 16px 14px">
      <div style="width:44px;height:64px;border-radius:6px;background:linear-gradient(160deg,#3a5f8f,#0d1626);flex:none"></div>
      <div><div style="font-size:24px;font-weight:600;max-width:380px;white-space:nowrap;overflow:hidden;text-overflow:ellipsis">Terminator 2: Judgment Day</div>
      <div style="font-size:17px;color:#b9c7e8;margin-top:4px">1991 · Movie</div></div></div>`;
  return doc(`${details}<div style="position:absolute;inset:0;background:rgba(0,0,0,.35)"></div>
  ${menu(240, 'A · No title — the group headings carry the menu', '')}
  ${menu(1000, 'B · The item it acts on, small poster + name + year', itemTitle)}`);
})();

(async () => {
  const browser = await chromium.launch();
  const page = await browser.newPage({ viewport: { width: 1920, height: 1080 } });
  const shots = [
    ['01-button-states.png', sheetHtml],
    ['02-playback-settings-glass.png', playback],
    ['03-hud-glass.png', hud],
    ['04-edit-page-option-A-glass-88.png', editPage(false, 'Option A · Edit Page in the same 88% glass (page text shows through faintly)')],
    ['05-edit-page-option-B-solid.png', editPage(true, 'Option B · same colour, solid over text-heavy pages')],
    ['06-context-menu-glass-88.png', actions('rgba(26,31,92,0.88)', 'Actions menu over the details page · shared glass at 88% (synopsis faintly visible)')],
    ['07-context-menu-glass-94.png', actions('rgba(26,31,92,0.94)', 'Actions menu over the details page · shared glass at 94%')],
    ['08-context-menu-glass-97.png', actions('rgba(26,31,92,0.97)', 'Actions menu over the details page · shared glass at 97%')],
    ['09-context-menu-glass-98.png', actions('rgba(26,31,92,0.98)', 'Actions menu over the details page · shared glass at 98%')],
    ['10-focus-options.png', focusSheet],
    ['11-boxless-buttons.png', boxlessSheet],
    ['12-context-menu-fitted-97.png', fitted],
    ['13-menu-groups.png', groups],
    ['14-boxless-worst-case.png', worstSheet],
    ['15-menu-heading-inline-line.png', inlineMenu],
    ['16-boxed-black-buttons.png', boxedSheet],
    ['17-menu-title-options.png', titleMenus],
  ];
  for (const [name, html] of shots) {
    await page.setContent(html);
    await page.screenshot({ path: path.join(out, name) });
  }
  await browser.close();
  console.log('rendered', shots.length);
})();
