// v16 (owner, 20 Nov 2026): Sort icon choices for Date added, Title and Number of seasons, each large and in a menu
// row (focused in the last column), with the reworded Number of seasons / Number of episodes.
// Run: FONT_DIR=<dir with roboto-latin-*-normal.woff2> node render-v16-sort-icons.js
const { svg, I, render } = require('./render-v11-filters.js');

const O = {
  inbox: '<path d="M3 13l3-8h12l3 8v6a1 1 0 0 1-1 1H4a1 1 0 0 1-1-1z"/><path d="M3 13h5l1.5 2.5h5L16 13h5"/>'
    + '<path d="M12 3.5v7M9 8l3 3 3-3"/>',
  clockPlus: '<path d="M20.5 12A8.5 8.5 0 1 1 12 3.5"/><path d="M12 7.5V12l3 2"/><path d="M19 2.5v6M16 5.5h6"/>',
  newStar: '<rect x="3" y="4" width="18" height="17" rx="2"/><path d="M3 9h18M8 2v4M16 2v4"/>'
    + '<path d="M12 11.5l1.2 2.3 2.5.4-1.8 1.7.4 2.5-2.3-1.2-2.3 1.2.4-2.5-1.8-1.7 2.5-.4z"/>',
  aa: '<path d="M2.5 19L7.5 5h1L13.5 19M4.5 14h7"/><path d="M21 19v-6.5a3 3 0 0 0-6 0M21 15.5c-3.5-.6-6.2.3-6.2 2'
    + 'c0 2.2 4.2 2.5 6.2-.4"/>',
  azArrow: '<path d="M3 10l3-7 3 7M4 8h4M3 14h6l-6 7h6"/><path d="M17 4v16M13.5 16.5L17 20l3.5-3.5"/>',
  textLines: '<path d="M4 5h16M12 5v14M8 19h8"/>',
  stack: '<rect x="3" y="9" width="14" height="11" rx="1.5"/><path d="M6 6h13.5a1.5 1.5 0 0 1 1.5 1.5V17"/>'
    + '<path d="M9 3h10"/>',
  numbered: '<path d="M4 6h1.5v4M4 10h3M4 14.5c0-1 2.8-1 2.8.3 0 1.2-2.8 2-2.8 3.2h3M11 7h9M11 12h9M11 17h9"/>',
  calendarStack: '<rect x="3" y="7" width="15" height="14" rx="1.5"/><path d="M3 11h15M7 5v3M14 5v3"/>'
    + '<path d="M21 9v10.5a1.5 1.5 0 0 1-1.5 1.5"/>',
  tvPlay: '<rect x="3" y="4" width="18" height="13" rx="2"/><path d="M10 8l4 2.5-4 2.5zM8 21h8"/>',
};

const css = `
.board{position:absolute;inset:0;padding:40px 72px;color:#fff}
.sec{display:flex;align-items:center;gap:14px;font-size:30px;font-weight:500;margin:0 0 12px}
.cols{display:flex;gap:40px;margin-bottom:40px}
.col{display:flex;flex-direction:column;gap:8px}
.lbl{font-size:24px;color:#c9d3e6}
.big{display:flex;gap:40px;height:72px;align-items:center;padding-left:14px}
.mini{position:relative;border-radius:16px;background:rgba(22,24,28,.99);padding:10px 0;width:440px;
 box-shadow:0 0 0 1.5px rgba(255,255,255,.7),0 18px 48px 12px rgba(0,0,0,.85)}
.mini .i.one{height:58px}
`;

function choice(letter, name, d, label, focused, extra = '') {
  return `<div class="col"><div class="lbl">${letter} · ${name}</div><div class="big">${svg(d, 64)}</div>
    <div class="mini"><div class="i one nosep${focused ? ' on' : ''}"><span class="ic">${svg(d)}</span>
    <span class="tx">${label}</span><span class="fill"></span></div>${extra}</div></div>`;
}

function eps(focused) {
  return `<div class="i one nosep${focused ? ' on' : ''}"><span class="ic">${svg(O.tvPlay)}</span>
    <span class="tx">Number of episodes</span><span class="fill"></span></div>`;
}

function frame() {
  return `<div class="board">
    <div class="sec">Date added</div>
    <div class="cols">${choice('A', 'Arriving in the tray', O.inbox, 'Date added')}
      ${choice('B', 'Clock with a plus', O.clockPlus, 'Date added')}
      ${choice('C', 'Calendar with a star', O.newStar, 'Date added', true)}</div>
    <div class="sec">Title</div>
    <div class="cols">${choice('A', 'Aa', O.aa, 'Title')}
      ${choice('B', 'A to Z with an arrow', O.azArrow, 'Title')}
      ${choice('C', 'Plain T', O.textLines, 'Title', true)}</div>
    <div class="sec">Number of seasons (shown above Number of episodes)</div>
    <div class="cols">${choice('A', 'Stacked screens', O.stack, 'Number of seasons', false, eps(false))}
      ${choice('B', 'Numbered list', O.numbered, 'Number of seasons', false, eps(false))}
      ${choice('C', 'Stacked calendars', O.calendarStack, 'Number of seasons', true, eps(false))}</div>
  </div><div class="cap2">13 · Sort icon choices and the new wording</div>`;
}

render([['47-v16-sort-icon-choices', frame]], css);
