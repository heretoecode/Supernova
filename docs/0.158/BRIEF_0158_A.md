# Brief: 0.158, part A (for Supernova Coding)

The owner is reviewing 0.157.2 (Player, Details, Files) outside Claude; those notes come later as part B. Part A
builds the 0.158 redesigns, which don't depend on that review. Approved by the owner, 18 Nov 2026.

## Read first
1. `CLAUDE.md`: Definition of done, Working unattended, standing signing approval.
2. Page sheets: `docs/design/pages/LIBRARY.md`, `HOME.md`, `SEARCH.md`. Build from these, not the diaries.
3. `docs/design/RULES.md` §1 (one side margin: the top bar's edges), §2 (0.158 top bar note), §16 (scroll arrows).
4. `docs/ROADMAP.md` › 0.158.

Merge `claude/design-planning-discussion-9poejm` into the working branch first.

## Order of work (finish each, with everything behind it, before the next)
1. **Top bar** (`files-review-0157-1/08`, `09` A): no SUPERNOVA wordmark; every item a word (Files · Search · Settings,
   no icons); media pages open as focus lands on them, tools on OK; plain / white box / soft grey box for the open page;
   no underlines in the bar; one font, weight and size for every word and the clock; words centred on their letters.
   Tabs on Details and Files stay as they are.
2. **One side margin:** library pages, Files and Search line up with the top bar's edges; Home has none.
3. **Library pages** (LIBRARY.md): every action in its table pressed and checked in a render.
4. **Home** (HOME.md).
5. **Search** (SEARCH.md).
6. If time remains: connections grouped by server (F52, `docs/0.157/REVIEW_0157_1.md`).

## Not in part A
The Player review fixes and anything from the owner's 0.157.2 notes (part B). Settings and onboarding (0.159).

## Version
Ship part A as **0.158** (code 1580) when its conformance run is green; part B follows as 0.158.1 unless the owner
says otherwise. Finish with a report in `docs/0.158/`: what was built, the "not built" list, judgement calls, the APK's
run link; update PROJECT_STATUS.md.
