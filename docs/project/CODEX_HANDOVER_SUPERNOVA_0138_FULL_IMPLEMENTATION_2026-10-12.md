# CODEX HANDOVER — Supernova 0.138 complete approved implementation

**Prepared:** 2026-10-12. **Status:** READY FOR CODEX INTAKE; no implementation started by preparing this document. User explicitly approved the entire previously proposed 0.138 scope and requested preparation for Codex, including subsequent user-approved fixes. This is one consolidated development release, not isolated tickets.

## 0. Mandatory safety and repository intake
- Repository: `heretoecode/Supernova`. Current signed user-testing baseline: **0.137** on NVIDIA Shield. Existing draft PR **#12** and working branch `codex/supernova-0.137-qa-2026-10-11` were the last recorded development state; **verify current state live**, do not assume branch/PR/commit has not moved.
- Source of approved design/QA documentation: `docs/nova-upstream-deep-audit-2026-10-11` (documentation-only branch). **Read all linked docs and the current development source**. Documentation branch has not necessarily been merged; do not assume docs are on the implementation branch. This handover supersedes older provisional “not authorised” status wording in individual exploratory documents: the user has now approved 0.138 implementation for the explicitly agreed items.
- First inspect branch, draft PR, recent commits, existing build/tests, outstanding conformance notes and any active coding work. Preserve 0.137 source and history, build identity, signing, existing UX and user data. Establish a safe 0.138 development continuation/branch without overwriting, resetting or discarding work. **Do not merge to main, publish an APK, upload user-provided photos/videos/logs, or perform destructive repository operations without user approval.** If existing work is active/conflicting, report and resolve before modifying it.
- Produce source audit, implementation plan, then code/tests and evidence. Do not substitute a mockup for running app behaviour; user tests on NVIDIA Shield with D-pad. Do not claim complete from compile or Robolectric-only renders. Preserve working navigation and settings functionality.
- This is the **entire agreed scope**. Implement all approved items; investigate unknowns and disclose any real API/platform constraints. Do not silently defer difficult items.

## 1. Home / hero / Continue Watching
- Remove Home **Review Library Health** banner/button and any reserved gap; preserve Library Health elsewhere.
- Investigate missing Continue Watching row; verify in-progress resume, completed episode → next unstarted episode, and completed season → next season first available episode, with correct ordering, missing episodes, end-of-series and row visibility. Don't presume banner caused it.
- Automatic Home rows display maximum **15 items**; **Watch Next uncapped**, custom rows honour user-selected limits. Correct mixed Movies/TV custom row eligibility without arbitrary 50/50 ratio. Trakt headings: **Trending on Trakt · In Your Library**, **Popular on Trakt · In Your Library**.
- Hero: approved full-card visual focus outline while button remains sole focusable target; improve metadata size/contrast; remove unwanted faded artwork effects after confirming cause; verify eligible movies enter hero rotation. Preserve existing smooth animation, More Info and D-pad navigation.
- Shared hero metadata: age badge → year → genres → `TMDB x.x/10` → muted votes; dynamic separators/compaction. Separate verified technical badges. No runtime or series counts on Home hero.

## 2. Global Details pages — upper card, tabs, lower Details
- Shared **Details Card** for movies/shows/seasons/episodes, based on Home hero visual language with official proportionally scaled title-logo artwork, background/scrim, metadata, synopsis and applicable Play/Streaming/More buttons. Comfortable outer margins; lower tabs **visible on initial viewport**. Card itself not focusable; focus only on actual buttons, with visual-only card outline while inside.
- Lower tabs **Details / Extras / More Like This** as applicable: text sits on underline location, **white focus underline like top nav**, **no continuous separator line**. Correct focus and tab navigation.
- Lower **Details** view: show Cast, Crew, Key Information, Reception and Technical Information **all together without vertical scrolling**, at readable size. **Cast max 8, Crew max 8**, no show-more/expand. No focusable informational cells; focus stays on Details tab, left/right changes tabs. Label and value on **same row** for compact information cards; consistent comfortable gap (not cramped or stretched across huge empty container); minimal wasted space. Handle missing/long values.
- Global metadata/technical styling across hero and all relevant details, with correct contextual fields: movie runtime; show Season(s) and Episode(s) counts; season Episode(s) count; episode runtime; no irrelevant fields or unverified inherited rating/file capabilities. Proper singular/plural capitalisation, middle-dot separators and no empty slots. Technical badges reflect verified playable local media only.
- Apply shared resting/focused button design to Play/Streaming/More, Home More Info, Customise Home and other appropriate controls. Preserve app-specific control semantics.
- Preserve existing functionality in Extras/More Like This; broad lower-page redesign beyond the explicit above remains parked.

## 3. Library page parity, sorting, and custom-page action regressions
- Movies and TV Shows existing code and approved layout are **the reference** for the already-created, media-filled custom Library page (e.g. Documentaries). Consolidate/reuse shared components rather than independently matching screenshots. Align title/stats text baselines, data counts/storage sizes, toolbar geometry/positions, poster sizing/gaps, focus/zoom/animation, scroll behaviour, D-pad navigation, visual treatment, dividers. Preserve contextual **Edit Library** feature.
- Custom page **Filters** currently opens five-step Edit Library guide (same as Edit Library). Audit and fix misrouting against intended filtering semantics. Back/Cancel/completion must return focus to **the invoking control** (Filters vs Edit Library) if still available. Regression-test both.
- TV Shows **Air Date** sort broken in both directions. With filters clear, Newest First observed Father Ted 1995, Only Fools and Horses 1981, DTF St. Louis 2026, Onar 2026, All Her Fault 2025; Oldest First starts Horizon 1964, The O.C. 2003, The Office 2005, Orwell 2007, Wild Scandinavia 2011 but ends with Father Ted 1995. Audit full collection sort key, date semantics, missing dates, comparator, paging, direction and refresh. Verify top/middle/bottom both ways and avoid sorting only visible batch.

## 4. Search polish
- Single-line search field with **vertically centred** text/placeholder, appropriate compact height; no phantom second line.
- Compact numeric, letter and action key heights/gaps without harming D-pad focus; Caps and Backspace **icon only**, make Space visibly wider, retain Clear/Done/Cancel labels. Preserve typing/actions and keyboard-specific focus states.
- Right-hand informational explanations: consistent **left-aligned** heading/description text, aligned icon column, **subtle separators**, no heavy nested boxes; five **distinct, meaningful monochrome icons** for Your Library, Flexible Formatting, Original Titles, Minor Typos, People & Studios (two-person icon, not generic information symbol).
- Editorially improve wording, capitalisation, punctuation and British English **only when verified against real implemented search behaviour**; do not advertise unsupported search capabilities.

## 5. Settings
- Preserve successful initial left-panel focus, Up → Home, Down through all categories; diagnostics ON/working and removed diagnostic-level selector.
- Audit ~13 categories with only ~8 visible; compact vertical button heights/padding/gaps to show all where feasible on standard TV **without sacrificing readability**; allow correct scroll on smaller displays/text scaling. Fix clipped bottom **About** item AND nested **Technical Information** item independently (bottom padding/scroll-to-visible/overscan).
- Remove extra **centre-panel dark inner inlay** behind settings options; shared single translucent dark blue-black panel background across columns, without losing necessary control focus/contrast.
- Preserve Technical Information navigation; Credits & Acknowledgements and Release Notes functioning, polish their formatting; release notes correctly on right, not wrongly indented in centre.
- **Open Source Licenses crash**: left item focus is safe, OK opens locally bundled notices, first **Android X Runtime Software** entry appears/focus traps then app exits. User reported **02:36 local time**; correlate permitted diagnostics and stack traces, reproduce, fix navigation/focus and crash. Do not assert cause from video alone.

## 6. Network & Files
- Severe focus bug: Down through left-panel local storage devices repeatedly jumps to **centre breadcrumb/address row**; correct Up/Down containment and explicit Left/Right panel transitions, focus restore.
- Grid View, Sort, All Files controls use approved global button rest/focus styling. **All Files** must actually toggle state and applicable file filtering, show clear ON/OFF visual indication; inspect semantics and folder contents before concluding expected items.
- Overview read-only scan/status text appears outside/cut off its container and has an unwanted focus target. Fix clipping/layout/overflow and make informational centre content **non-focusable**, while retaining actionable controls.
- User videos support observed defects but **are not authorised for external upload**. Validate on device and with targeted automated tests.

## 7. Preparing Playback
- Keep official title-logo artwork. Show at most one **real provider-sourced tagline** beneath it, no season/episode counts, duplicate Episode 1, runtime or other metadata.
- Movie → movie tagline; TV episode → episode tagline → season tagline → show tagline; if none, **logo artwork only**. Audit availability of episode/season taglines and do not fabricate or substitute synopsis. No blank reserved space.
- Preserve playback initiation, cancellation/error/progress behaviour and visual readability.

## 8. Playback HUD — latest approved layout
- Remove large blurred white outer focus box around entire bottom HUD when seek bar focused; focus treatment local to seek control.
- Timestamp pill **always visible while HUD is visible**, follows current/preview position; **small/compact**, avoid clipping/overlap; remove duplicate elapsed time under left end.
- **Subtitles → Audio → More** buttons grouped **on the left, above the seek bar**, raised enough to give pill clearance.
- Main row on **one horizontal centreline**: **Play/Pause icon WITH TEXT LABEL** to left → seek bar → total duration `52:52` **·** contextual `Episode Ends 03:58` (or **Movie Ends**). End time updates sensibly with seeking/resuming/speed; assess paused-state semantics.
- **Top right clock only, no icon**, no episode end time there.
- All HUD labels, clock, pill, duration and end time use the **existing official Supernova UI font**, cohesive similar apparent sizing, readable contrast and aligned numerals; media title-logo artwork remains untouched. Preserve HUD functionality, D-pad focus, auto-hide.
- User wants broader HUD discussion later and has **a subtitle-specific point to raise**; remind them at the start of that later discussion. Do not expand scope to unspecified HUD changes.

## 9. Subtitle intelligence
- Audit actual provider/API terms, rate limits, cache, embedded/sidecar detection and subtitle vs CC/SDH/AD availability. Implement **quota-aware, persistent, deduplicated, intent-triggered** subtitle discovery rather than provider requests for all library titles during import.
- Selected hero low priority, Watch Next/Continue Watching high, details scoped, playback highest/manual first. On episode playback progressively prepare **remaining episodes of current season only**, paced with 429/Retry-After/backoff and language preference; next-season first episode high priority after season completion, **no remaining next-season prefetch until that season starts playing**. Persist positive/negative caches, avoid repeat import bursts, allow user preference/provider constraints. Never show false capability badges.

## 10. QA, evidence, acceptance and deliverables
- Read all referenced detailed documents below, reconcile latest approved wording, inspect actual code and regressions. Build exact source, run unit/integration/UI tests, exercise TV D-pad focus, clipping, screen sizes, focus restore, scrolling, animations, sorting, crash reproduction and subtitle throttling.
- Review first-run onboarding/welcome Home and custom Library walkthrough **for preservation/regression only**. **Full onboarding redesign and broader custom Library walkthrough redesign are NOT yet agreed; do not invent or implement unapproved designs.** User plans separate discussions later. Likewise do not silently launch separate Claude Customise Home editor redesign or seek-bar feature redesign.
- Check user-approved diagnostics including previously identified **23:22** event and new **02:36** licence crash, only with permitted access to logs. Never upload raw user photos/videos/logs to GitHub, Claude or Codex without explicit permission.
- Produce conformance matrix **for every item**: requirement, code path, tests, screenshots/render evidence if possible, PASS/FAIL/UNKNOWN and any blocker; exact commit SHA, build/QA APK provenance and regression summary. Do not mark implemented without real tests. Preserve user approval gates for merge/release/distribution.

## Required project source documents — read on documentation branch
- `docs/project/APPROVED_SUPERNOVA_0138_CONSOLIDATED_CODEX_IMPLEMENTATION_SCOPE_2026-10-12.md`
- `docs/project/GLOBAL_DETAILS_CARD_UPPER_PAGE_DESIGN_2026-10-12.md`
- `docs/project/NEXT_RELEASE_LOWER_DETAILS_COMPACT_NONSCROLLABLE_AND_GLOBAL_BUTTONS_2026-10-12.md`
- `docs/project/NEXT_RELEASE_CAST_CREW_EIGHT_ITEM_CAP_2026-10-12.md`
- `docs/project/GLOBAL_HERO_METADATA_STANDARD_AND_SUBTITLE_DISCOVERY_DISCUSSION_2026-10-12.md`
- `docs/project/QA_CONTINUE_WATCHING_NEXT_EPISODE_AND_SEASON_SUBTITLE_PREFETCH_2026-10-12.md`
- `docs/project/QA_0137_HOME_CONTINUE_WATCHING_LIBRARY_HEALTH_BANNER_2026-10-11.md`
- `docs/project/QA_0137_HERO_HOME_ROWS_CUSTOM_MIXED_CONTENT_2026-10-11.md`
- `docs/project/QA_0137_HERO_ANIMATION_METADATA_AND_CONTENT_MIX_DISCUSSION_2026-10-12.md`
- `docs/project/QA_0137_GLOBAL_BUTTON_DESIGN_STANDARD_2026-10-11.md`
- `docs/project/GLOBAL_LIBRARY_PAGE_SHARED_DESIGN_RULE_2026-10-12.md`
- `docs/project/QA_0137_SEARCH_KEYBOARD_DENSITY_AND_INFORMATION_LAYOUT_2026-10-12.md`
- `docs/project/QA_0137_SETTINGS_CATEGORY_DENSITY_CLIPPING_AND_CENTRE_PANEL_SURFACE_2026-10-12.md`
- `docs/project/QA_0137_SETTINGS_LICENSES_AND_NETWORK_FILES_FOCUS_2026-10-12.md`
- `docs/project/QA_0137_TV_AIR_DATE_SORT_AND_PREPARING_PLAYBACK_TAGLINE_2026-10-12.md`
- `docs/project/QA_0137_PLAYBACK_HUD_SEEKBAR_FOCUS_AND_LAYOUT_2026-10-12.md`
- `docs/project/QA_0137_DIAGNOSTICS_1122_PRE_HANDOVER_2026-10-11.md`

## Codex execution instruction
**Continue from verified repository state. Plan and implement the full 0.138 scope in one coordinated release, with checkpoints and evidence. Do not restart or overwrite 0.137 work; do not merge to main.** Where a technical constraint makes a requirement impossible as stated, document the specific blocker and seek a decision instead of quietly dropping the item.
