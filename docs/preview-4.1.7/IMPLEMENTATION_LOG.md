# Preview 4.1.7 implementation and verification log

This is an active implementation record, not a completion report. The 115-entry
requirements register remains the scope authority alongside the handover.

## Checkpoint 1 — shared focus, keyboard and Settings

Local commit 3ade199f; remote commit 4f965cd91893048483adafb204fe4afde1d251f9.
Identical tree abb55a7b5cc1804f099bda86cacd64ff911f132e.

- Shared travelling top-navigation boundary (160 ms), explicit visual-order keys,
  Home LEFT/Settings RIGHT locks, white contents.
- Shared grid horizontal row boundaries and final-row DOWN containment.
- Shared staggered keyboard, permanent numbers, initial T, Clear/Space/Backspace.
- Search non-focusable query display, no X/duplicate introduction, result/key return.
- Settings fixed twelve-category rail; explicit entry; nested sections in middle;
  existing Preference objects, values and handlers retained.
- Landscape whole-card 1.08 focus and artwork-only rounded clipping.
- Added three Robolectric navigation regression tests; not yet executed at checkpoint.

Verified locally: Java syntax and XML/resource-structure/whitespace checks.
AWAITING PHYSICAL QA: focus travel, rapid input, Settings hierarchy and Search return.

## Checkpoint 2 — Home, library controls, source shell and scanner tracing

- Home membership toggles remain open and update ticks without toast; new row is
  selected on return. Row-rule toggles update labels without recreating the menu.
- Genre chooser footer stays fixed; maximum choices follow the specified list.
- Home display caps: 30 Continue Watching, 50 Recently Added. Underlying records,
  histories and row membership are preserved. More Info cycles the feature on LEFT/RIGHT.
- Shared toolbar uses the existing divider segment, not a boxed focus control.
- Unmatched classification separates filename hints from identity. Unknown files
  are available from either media page. Matching workflow still in progress.
- Year and provider multi-selection with persistent state; actual-library genres.
- Five-section Network & Files shell, inline scan controls, source context actions,
  storage capacity, explicit Saved Locations, shared folder actions.
- Manual scan entry uses actual scanner/batch activity rather than broad import
  activity; source lifecycle telemetry added to the pinned MediaLib patch.
- Scheduler protocol classification now includes HTTP/HTTPS as WebDAV aliases.
  This is a confirmed code inconsistency, not proof of the physical QA failure's
  sole cause. Manual-versus-relaunch behaviour still requires Shield validation.
- Supplied approved 320×180 banner used unchanged; preview label becomes Supernova;
  package identity and provider-authority conversion remain unchanged.
- Source validation workflow has no signing step or APK build. Delivery workflow
  remains separate and is not enabled for this branch yet.

Verified locally: Java syntax; all XML parses; HUD/internal-activity/signing-gate
source checks; whitespace; new scanner patch applies after pinned existing patch.
Not yet verified: Android compilation, Robolectric tests, emulator, physical Shield.

## Checkpoint 3 — enrichment, Details, technical overlay and diagnostic retention

- Persistent, deduplicated priority queue and locale-aware metadata package cache.
  Failed refreshes retain usable data but cannot mark a stale package complete;
  queued retries wake without needing another page visit.
- Details lower navigation shares the toolbar divider treatment, retains focus
  across rebinds and hides empty Extras/recommendation entries. Hero action edges
  are held; duplicate section headings and obsolete More actions are removed.
- Playback Info goes directly to Video/Audio/File/Source panels. No playback
  engine or signing changes were made.
- Important diagnostic events have a separate bounded queue and seven-day daily
  retention stream. Incident capture no longer depends on the routine queue.
  Export summaries include retained playback streams and deduplicate events;
  historical unclean exits remain evidence, not confirmed crash claims.
- Added archive and metadata-cache regression tests; updated technical overlay
  and empty recommendation expectations to match written requirements.

CI evidence: run 36216150455 exposed misplaced scanner completion telemetry;
corrected on the candidate branch. Run 36237617854 then compiled MediaLib and
exposed a blank-final navigation listener initialisation error, corrected here.
Neither run reached unit tests. These are compile failures, not signing failures.
This checkpoint is not full handover completion or a successful Android build.

## Checkpoint 4 — verified compile, association safety policy and regression fixes

Checkpoint 3 local commit 5909cb1e and remote f675ca47 have the identical tree
e29e8d7474ee6f757ab45b9e7edd1a5c489b03bb. CI run 36238345377 compiled Android
source successfully, then ran 31 targeted tests: 28 passed, 3 navigation tests
failed. Later test steps did not run. Navigation fixture attachment/touch-mode
handling is corrected for the next run; no test or assertion was suppressed.

- Backdrop replacement failures preserve the displayed artwork instead of clearing
  it. Same-bitmap rebinds avoid another crossfade. Added three regression tests and
  privacy-safe request/result/fallback diagnostics. Physical flashing cause remains
  investigative; this change is not a claim that Shield QA has passed.
- More Like This now follows TMDb recommendation order, reconciles local titles,
  then configured providers, with local genre fallback last. Card row edges hold.
- Settings child LEFT uses the same parent-restoration path as Back, rather than
  trying to focus a hidden child opener.
- Added pure put.io reconciliation policy and 13 regression cases. Every page and
  descendant must complete; failures yield no changes; stable IDs survive moves;
  ambiguous paths/sizes/multiple claims require review. Missing files are review
  candidates only, never unconditional delete instructions. This policy is not yet
  connected to production account linking or library writes.
- Requirements register now distinguishes tested components, pending integration,
  physical QA, deferred scope and the external OAuth configuration blocker.

Local XML/source safeguards pass. This resumed environment has no javac or
javalang; Android compilation and unit testing therefore use the isolated CI
route. No signing material has been accessed by source-validation CI.

## Checkpoint 5 — Details panels, Extras and bounded remote seeking

Checkpoint 4 local 3073dc48 / remote 8b8f28c1 source trees match exactly:
0a3d521bbd449176fe4ebef14356e03e59fc02d5. CI run 36239377263 compiled and
ran 47 targeted tests: 44 passed, including all 13 put.io safety tests and all
three backdrop tests. Three navigation tests still failed because the fixture's
Activity.getCurrentFocus() was null despite a successful component focus request.
The next run checks the actual component focus tree with the same exact target
assertions; none are removed or disabled.

- Extras groups actual playable videos into populated category rows, four across,
  with focus-only Play indicator and held horizontal edges. Unknown durations are
  omitted. Episode cards now measure four across and show the local Play indicator.
- Details adds real release/country/tagline/collection/budget/revenue/vote fields,
  available bitrate/frame-rate/subtitle facts, TV Library Information and remote
  Streaming Availability. Empty Reception panels are omitted and columns reflow.
  Added tests for empty remote panels and Extras categories.
- Added deterministic 10/30/60/120-second remote seek policy, reset on direction
  change or 1.25-second pause. Absolute-position remote hold previews the target
  and commits through the existing player on release; legacy relative-position,
  joystick and touch paths are retained. Six policy tests added. Decoder callback,
  pause/resume and physical Shield behaviour still require validation.

## Checkpoint 6 — API reader, Home row actions and broader regression validation

Checkpoint 5 local 59a21e4d / remote ae4eea8d have identical tree
8ff3f7044c08e4e643ebf069bf0358df31a92609. Run 36239715389 compiled and passed
all 55 targeted tests, including navigation, Details categories/panels and seek
policy. Its wider regression step ran 76 tests: 74 passed; two old expectations
failed (clearing displayed artwork on failure; old Add to Watch Next label).
These expectations are updated to the approved retention/keep-open membership
behaviour, retaining library-state assertions. WebDAV tests did not run because
the earlier test step failed. No emulator, APK or physical Shield validation yet.

- put.io read-only adapter and recursive cursor reader use provider-maintained
  API contracts, fixed HTTPS host, header authorisation, no redirects, bounded
  responses and sanitised errors. Added seven reader/parser safety tests.
  No live token/account access; native integration remains incomplete.
- Home leftmost-row LEFT exposes only Move/Hide; moving and hiding keep row
  membership and playback history intact.
- Shared file options expose Movies/TV classification when the native folder
  capability offers indexing, and independent Saved Locations. Existing native
  local/network handlers remain responsible for indexing. Network indexing no
  longer removes the independent saved bookmark.

## Checkpoint 7 — enrichment, classification and identity evidence

Checkpoint 6 local 25693fce / remote 4b3a4add have identical source tree
165d3697dc13be8aa5d46379d95e2ff8894ffaee. CI run 36240119933 succeeded:
Android compilation; 62 targeted tests; 76 wider regression tests; and 17
FileCore WebDAV tests. These step counts overlap and are not a unique-test total.
https://github.com/heretoecode/Supernova/actions/runs/36240119933

- Persistent enrichment now covers TV season packages and classification data,
  yielding between seasons. Schema migration preserves pending jobs. Locale is
  included in newly enqueued identities. Completed packages become eligible for
  provider refresh after six hours; section caches retain their own freshness.
- Cache network coalescing uses separate locks from atomic disk access so a
  cache read does not wait for a slow network request. Added a concurrent test.
- Unmatched classification honours explicitly classified source folders, using
  the deepest matching folder and strict path boundaries without inventing a
  metadata identity. Added four classification tests and a queue migration test.
- Added a read-only merged-manifest identity audit and four passing local Python
  tests. Documented inherited shared-user-ID evidence without changing it or
  claiming a confirmed cause of upstream Nova installation failure.
- Validation now requests the unfiltered Video unit suite as well as targeted
  and WebDAV checks. New Android tests and merged-manifest audit await CI.

Local safeguards: 430 XML files parsed; HUD/internal-Details/signing-gate checks
and diff whitespace passed. Complete feature integration and final three-pass
conformance reviews remain unfinished. This is not a release-complete candidate.

## Checkpoint 8 — diagnostic evidence and full-suite fixture repair

Checkpoint 7 local 87cb8690 / remote 1b581a58 have identical source tree
aeda927b88348bd24db41c53d39a34f79ca772c0. Run 36240788783 compiled and passed
the expanded targeted tests, including cache concurrency, source classification
and queue migration. Its merged-manifest audit passed. The unfiltered Video
suite ran 191 tests: 190 passed; SortUtilsTest failed during Robolectric SDK
initialisation before its five assertions/tests ran. That legacy fixture now
explicitly uses SDK 28 and Application, consistent with the other pure Android
utility fixtures; all test methods and assertions remain intact. This correction
awaits CI. Later WebDAV/regression steps were skipped after the suite failure.

- Diagnostic records now carry INFO/WARNING/ERROR/FATAL severity; suspected
  unclean exit remains WARNING, not a confirmed crash.
- Machine-readable manifest schema 2 includes event counts, retained per-process
  time spans, launch/exit counts, significant-event correlation references and
  historical drop counts using each process's maximum, not a sum of repeated
  cumulative counters. Human summaries distinguish retained spans from complete
  session duration.
- Incident context includes native heap and available/low-memory state only at
  capture. Separate manual/automatic daily streams preserve window copies across
  replacement of the current flight file; roughly 60 seconds before/after,
  bounded to four 256 KiB files per category/day and seven-day age retention.
  Size/queue limits still make completeness PARTIAL; physical pressure/soak
  testing remains necessary. Routine-event rotation cannot replace these files.
- Advanced settings exposes Show Latest Reference without requiring logging to
  be enabled. QR presentation is still not implemented; do not mark DIA-003 done.
- Three new archive tests cover incident allow-list/deduplication, evidence spans
  and drop-counter aggregation, and conservative severity classification.

Local XML/source/whitespace checks pass. Latest diagnostic changes await Android
compilation/tests. No Preview 4.1.7 APK or physical/runtime validation is claimed.

## Checkpoint 9 — safe reference QR and live provider filtering

Checkpoint 8 local 7b3bf24c / remote e8176a9c have identical source tree
79b512ac3d15f17af619173853b06c34d1d1fd1b. CI run 36241111976 succeeded:
compilation, 71 targeted tests, the full 198-test Video suite, 76 regression-step
tests and 17 FileCore WebDAV tests. Step counts overlap. Merged identity audit
also passed. This validates checkpoint 8, not subsequent changes.
https://github.com/heretoecode/Supernova/actions/runs/36241111976

- Manual-report confirmation and Show Latest Reference now display an offline QR
  beside the readable reference/category/millisecond UTC timestamp. Payload
  construction accepts only the generated reference format, four fixed categories
  and a positive timestamp: no URL, credentials, arbitrary text or report payload.
  Uses pinned ZXing core 3.5.3; encoder contract checked against upstream source:
  https://github.com/zxing/zxing/blob/zxing-3.5.3/core/src/main/java/com/google/zxing/qrcode/QRCodeWriter.java
  Three tests cover QR encode/decode round-trip and rejection of unsafe fields.
- Provider cache updates now trigger a debounced DiffUtil refresh only for active
  Movies/TV provider-filtered pages with the matching country/title scope. No
  media-library requery and no unfiltered-page rebuild. Country/enabled state is
  included in enrichment identity, so changes cannot be blocked by completion of
  an earlier country's package. Two policy tests added; live-service/Shield
  integration remains unverified.

New source and tests await CI. XML/source/whitespace safeguards pass. Remaining
workstreams and final conformance reviews are still in progress; no APK yet.

## Checkpoint 10 — Home exact limits and persistent carousel indicators

Checkpoint 9 local 2546fc93 / remote e4147f29 have identical source tree
dd3f845df7fc76f5844bf6d3b84d779cb1e200a5. CI run 36241450416 succeeded:
compilation, 74 targeted tests, the full 203-test Video suite, 76 regression-step
tests and 17 WebDAV tests. The QR round-trip/privacy and provider-refresh tests
passed. Counts between steps overlap.
https://github.com/heretoecode/Supernova/actions/runs/36241450416

- A further source audit found the exact Maximum Items option was absent despite
  an earlier continuation summary describing it as present. The actual code now
  offers Select alongside No Limit and the approved presets, using the shared
  full QWERTY keyboard. Invalid input stays in the dialog; no saved limit changes.
  Added boundary/overflow tests and a real dialog validation/correction test.
- Featured indicators now retain their view/state while the Hero is rebound.
  A 200 ms ease-out animation morphs pill widths/colour, retargeting from the
  current visible state during rapid navigation. Indicators do not take focus
  away from More Info. Two animation/wrap tests added. Logo/synopsis geometry
  remains under review; this does not mark all of UI-006 complete.

Local XML/source/whitespace checks pass; these latest Home changes await CI.
This checkpoint remains an implementation candidate, not final delivery.

## Checkpoint 11 — protected OAuth persistence and explicit metadata acceptance

Checkpoint 10 local 96f188b6 / remote 4f68f2f8 have identical source tree
1ecdc2819b2ef6bfe7b93a50ebb18ab1cac1b4fa. CI run 36243112200 succeeded:
compilation, 74 targeted tests, the full 208-test Video suite, 76 regression-step
tests and 17 WebDAV tests. Counts overlap between steps. The exact-limit dialog
and persistent indicator tests passed.
https://github.com/heretoecode/Supernova/actions/runs/36243112200

- Added PutioTokenStore: Android Keystore AES-GCM encryption, atomic no-backup
  envelope, no plaintext fallback, no replacement key on read and sanitised
  exceptions. No live credential is used or stored. Four tests exercise encryption
  and failure behaviour with a test-only symmetric key. OAuth UI/configuration,
  credential lifecycle integration and actual-device verification remain pending.
- New-UI manual movie/show/episode results now open shared Match Preview with
  title/available year or episode coordinates and real synopsis. Only Use This
  Match/Use This Episode invokes the existing save path; Back makes no changes.
  Series correction avoids a duplicate confirmation after explicit acceptance.
  Classic UI is unchanged. Added an acceptance/Back/double-activation test.
  Unified matching search/ID/current-match presentation and full reconciliation
  conformance are still unfinished; this is not all of UI-039.

Local XML/source/whitespace safeguards pass. New source/tests await CI. No APK,
emulator or Shield behaviour has been validated for this checkpoint.

## Checkpoint 12 — shared navigation shade and Details state

Checkpoint 11 local 935dc4b1 / remote 23fa4f4b have identical source tree
502a77b8c3be7d9a67e9e7639c85dfccaeead836. CI run 36243449105 succeeded:
compilation, 78 targeted tests, the full 213-test Video suite, 76 regression-step
tests and 17 WebDAV tests. Counts overlap. Token envelope tests and explicit
metadata acceptance tests passed; live OAuth and library migration are not implied.
https://github.com/heretoecode/Supernova/actions/runs/36243449105

- TopNavigation.setScrolled no longer does nothing. A shared, cached low-resolution
  artwork sample receives a two-pass blur and a dark gradient which fades below
  navigation. The bar itself remains transparent, with no separator or permanent
  rectangle. Strength retargets over 180 ms. Sample width is capped at 240 pixels
  and refresh rate at 10 Hz; it requires no Android 12 RenderEffect. Three tests
  cover blur and rendered alpha fade. Real-device visual/performance QA remains.
- TV Hero Resume Sx Ex is derived from the same committed journey/available episode
  inputs used by existing playback. No seek/resume policy change. Snapshot refresh
  now updates Details library information and restores semantic panel focus.
- Shared Details focus restoration records requested/result/fallback and success,
  skips hidden/missing targets and uses a visible section fallback for remote
  titles. Added panel-focus and series-label tests.

Local XML/source/whitespace safeguards pass. New Android tests await CI. Remaining
Details scope includes sticky compact title, complete provider/non-local episode
presentation and exact Hero/provider focus conformance; these are not silently
marked complete. Final three-pass review and signed APK production remain pending.

### Checkpoint 13 — Versions presentation and retained selection

Checkpoint 12 was preserved remotely at 9f57d45ac07e1bce256b8e2163f374b00d4ef281
with tree a5049985143eb7827613046cba49f5afd7181e76 matching local a1c4ba43.
Run 36243903397 passed compilation, 79 targeted tests, the full 218-test Video
suite, 78 regression checks and 17 FileCore WebDAV tests. Counts overlap.

Versions now uses compact horizontal rows with cached resolution, known HDR,
codec/audio/channels, size and source/location. Unknown metadata is omitted;
opening the menu does not probe files. URI authentication/query/fragment material
is omitted from location text. A separate white check and Current label remain
independent of focus. Selecting a version keeps the menu open, updates Details
through the retained handler and carries the selected title's in-memory resume
position to the chosen encode. No toast or database migration is introduced.

Two new regressions cover persistent selection/focus state and credential-free
URI display. Local XML/source checks and four identity-audit tests pass. Android
tests await CI for this checkpoint. Full title-history persistence/reload and
different-duration encode playback still require further verification; UI-036 is
partial, not complete. Artwork selection remains a separate unfinished workflow.

### Checkpoint 14 — Scrolling Details title and selected backdrop

Checkpoint 13 is remotely preserved at 7767b4a51e5105d69453879b482c9a30650eb092,
tree f9bc42bd7b70cf36ddc45b42c62e3e53e74affb4 matching local e705d88b.
CI run 36244361818 was still in progress at this checkpoint.

The shared Details page now reuses its existing text/logo rendering as a compact
sticky title below global navigation once the Hero title leaves view. It does not
issue a second artwork request or introduce another focus target. The transition
reverses with scroll; lower-section entry and automatic focused-child scrolling
reserve space for it. Scroll changes now drive the shared navigation shade on
movie, episode, TV and remote Details wherever hosted by TopNavigation.

Metadata refresh now prefers the saved default backdrop, falling back to the first
available only if no default exists; the chosen local or remote URI is retained
for the current video. This fixes a concrete preference-preservation defect found
while tracing the unfinished artwork picker.

Added transition and rendered focus-retention regression coverage, including a CI
image fixture. Local XML/structure/whitespace checks pass. Android tests and visual
inspection of the new fixture remain pending; physical scroll/focus conformance
is AWAITING PHYSICAL QA. This does not complete the remaining artwork workflow.

### Checkpoint 15 — Durable association boundaries and visual correction

Checkpoint 14 is preserved remotely at 46b1445d7dc9f188ce623e7a651650eb89dc444d,
tree 08a0e10db7774c6dc102e08e0a7a9323a1f9e8b2 matching local 2dfd447f.
Run 36244575086 compiled successfully but failed one of 80 targeted tests: the
new rendering fixture did not initialise Picasso before host teardown. Fixed the
fixture consistently with existing rendering tests; no assertion was suppressed.
Checkpoint 13's separate run was superseded/cancelled by checkpoint 14.

Downloaded and inspected the actual CI rendering. This exposed Hero controls
overlapping the compact title and empty Cast/Crew headings before metadata arrival.
Scrolling content now clips beneath the compact title, and empty people sections
remain hidden. Revised rendering awaits the next CI artefact and physical QA.

Added a transactional put.io association sidecar with stable account/file/media IDs,
source scope, generation fencing, complete-snapshot checks and missing-review flags.
No existing library rows are changed or deleted. Identity conflict rolls back all
attachments. New/ambiguous files prevent activation; disconnect requires an explicit
discovery choice, invalidates in-flight work and preserves links. Credential-bearing
source URIs are rejected. Six regression tests cover these data-safety boundaries.
The store is not yet connected to source selection, new-file indexing or scanner
exclusion. Native put.io is still partial; OAuth configuration remains external.

Local checks pass. New database tests and revised rendering need Android CI.

### Checkpoint 16 — Shared child-window focus restoration

Found a shared navigation defect: menu anchoring used Activity.getCurrentFocus even
when the opener was inside another dialog. Children could return focus to the Hero
instead of the More row. PreviewDialog.create now tracks weak dialog references,
captures the actual parent opener, restores semantic replacements after a rebuild,
and records requested/result/fallback diagnostics. Closing a covered parent does
not steal focus from a remaining child. Choose/read/review, Versions and the shared
keyboard use this lifecycle. Three tests cover nested Back, replaced opener and
covered-parent dismissal. Retained native dialogs still need route-by-route review.

Requirements UI-033/UI-036/PUT-003/PUT-004 now reflect the traced implementation
and distinguish component completion from integration/physical QA. Local safeguards
pass; new Android stack tests await CI. The full pass remains in progress.

### Checkpoint 17 — Artwork selection grid

Checkpoint 15 passed run 36244922476: compile, 86 targeted tests, full 228-test
Video suite, 79 regression tests and 17 FileCore WebDAV tests. Counts overlap.
Checkpoint 16 is preserved remotely at 8fdfc0467599ebbc3b934187ef718c7cc5d819a2,
tree 4a18d0aa4ba861b9d59c55620095956a00ea4017 matching local 79af6771; CI pending.

Added PreviewArtworkPicker, a reusable poster/backdrop grid using shared boundary
focus and whole-card enlargement. Selection is a plain white top-right check,
independent of focus. The grid remains open and serialises save requests; only a
confirmed save moves the check. Failure retains the previous selection with inline
feedback. Images are released when the dialog closes. Two tests cover failure,
success, single-flight writes and grid-edge focus containment.

Movie/episode artwork now routes through the grid and existing native saver tasks.
Preview backdrop downloads precede default selection; preview success/failure
toasts are replaced by picker state. A successful backdrop immediately refreshes
the current Hero. Native non-Preview paths retain their handlers. Current choices
are kept for reopening the picker and refreshed by normal metadata callbacks.

Local safeguards pass. Android compilation/tests for this change remain pending.
TV-overview artwork routing and cross-surface cache propagation still require work
and physical QA; UI-038 is partial. No signed 4.1.7 APK exists yet.

### Checkpoint 18 — Locale-safe More actions and TV artwork route

Checkpoint 17 plus its fixture correction is preserved remotely at
54421aec4cab58d369f08a028cd4ba28979e86aa, tree
8916c01ce72c987db5bbdb7736c32b8829f55a7a matching local 61e136a8.
Checkpoint 16 compiled but two dialog-stack tests failed on unlaid-out fixture
views (null window focus). The fixtures now wait for layout and explicitly assert
initial focus before testing restoration; the same final assertions remain.
Run 36248451832 is validating the correction and artwork picker.

More action filtering now uses the separate native Movie/TV action ID namespaces,
not English label substrings. This keeps the specified exclusions consistent in
other languages. Empty streaming-only More controls are omitted. Local subtitle/
artwork tools no longer depend on a delete/file action being present, and duplicate
File Information is removed. Two tests cover translated labels and action namespaces.

TV overview now opens the same shared artwork grid. Choices and saves run off the
UI thread; cancellation is checked before changing the default. The existing
scraper default setters and normal TV refresh path are retained. Successful
backdrops refresh immediately. Callbacks are guarded against destroyed/replaced
Details views. No new remote transport or library migration is involved.

Local XML/source/whitespace safeguards pass. Android compilation and tests for this
checkpoint remain pending. Cross-surface artwork propagation, UI visuals and real
Shield navigation remain AWAITING PHYSICAL QA; this is not a final conformance sign-off.

#### Checkpoint 18 validation follow-up

Run 36248451832 compiled and passed 86 targeted tests, then ran 233 Video tests
with two fixture failures. Rebuilt-opener restoration now passed. The remaining
Hero assertion was traced to Robolectric ShadowActivity.getCurrentFocus, which
returns a separately supplied field rather than the real focused view (confirmed
against the provider's 4.16.1 source). The fixture now supplies that initial field,
clears actual Hero focus while the parent opens, then asserts real View.hasFocus
after dismissal. Grid navigation was tested before layout (0×0 cards); the fixture
now lays out the dialog before sending DPAD input. No application failure was
established by these two assertions, and neither test has been removed or disabled.

### Checkpoint 19 — Watched scope, live menu state and distinct episode counts

Checkpoint 18 is preserved remotely at fa9b6d0abd123aa985d565c5ae75865b518e53c9,
tree d4fc9ddd2b40c2c49b215ec175ebe63a396c6dea matching local 81d59525.
Run 36248882548 compiled and passed the targeted suite; full Video ran 235 tests
with one remaining fixture failure. Artwork tests and semantic replacement passed.
The last fixture used clearFocus on the host's sole control, which Android
automatically reselected. It now moves focus to a second real control before
asserting restoration to the captured Hero opener. Assertions remain enabled.

TV watched state now opens a shared Entire Series / season scope menu with real
watched counts and explicit Mark watched/unwatched labels. Existing SeasonsLoader
and DbUtils writes/Trakt integration are retained; query and write work is off the
UI thread. Failed/partial operations refresh the actual library state and report
failure without claiming success. Two scope tests cover Back, single-season and
mixed-series choices.

Open More menus now resolve watched actions against the current native adapter,
update the visible label after state changes and stop using stale action objects.
A regression tests watched-to-unwatched replacement. Remote Details without any
primary action fall back to the visible section tab for focus.

Found and corrected a shared SQL counting defect: watched physical encodes were
counted against distinct episode totals, so two watched copies of one episode
could falsely complete a two-episode season. All relevant TV/season loaders now
use one distinct watched-episode expression (season+episode for whole shows).
Two real SQLite aggregate regressions cover duplicate encodes and season identity.
No watched/resume rows are rewritten by this query correction.

Local safeguards pass. Compilation and the five new tests await CI. TV watched
state, Trakt behaviour and live refresh remain AWAITING PHYSICAL QA. Full delivery
and the three final conformance reviews are not complete.

## Checkpoint 20 — shared Find a Match surface and movie identifiers

Checkpoint 19 is now verified by run 36249337871 (remote eab995faee54948377da772ab19f40c98986e9ec): compilation passed, 86 targeted tests, all 240 Video unit tests, 79 overlapping regression tests and 17 FileCore WebDAV tests passed. The identity audit passed. These are CI results, not device QA.

Movie and series correction now share a Preview Find a Match presentation with the specified subtitle, shared keyboard, compact results and explicit Match Preview acceptance. Results append without stealing existing result focus. Typing is debounced and detached views cancel queued searches. Movie numeric TMDB/IMDb identifiers use the native details/save route, with strict provider-host parsing and a bounded response. Uppercase IMDb input from the TV keyboard works. Search clearing invalidates old tasks; legitimate no-match results remain distinct from network failure. Search hardware keys retain Preview keyboard focus.

Added identifier, result-focus, debounce, detach and retained-fragment lifecycle regressions. Local XML/source safeguards and four identity-audit tests pass; this checkpoint's Java compilation/new tests await CI. Metadata correction remains PARTIAL: parent-series episode correction/escape, full database preservation audit and runtime evidence remain outstanding. No metadata is deliberately removed before correction. This is not final conformance approval.

## Checkpoint 21 — metadata state preservation and membership child workflow

Checkpoint 20 compiled and passed its targeted suite in run 36250172546. The full suite ran 246 tests with one failure in matching-result focus preservation. The fixture now declares the intended TV display density/size and asserts visible width and successful initial focus separately; this still requires a CI rerun, not a presumed pass. The real retained-fragment lifecycle regression passed.

Series correction now transfers explicit Home/Watch Next membership to the corrected series ID without changing row order, visibility or unrelated members. Continue Watching dismissal is retained. A failed episode batch no longer reports a successful correction or proceeds to Trakt/row remapping. Added real-schema/provider tests for retaining two physical files, their identifiers, bookmarks and last-played values through movie and series correction. These tests do not yet verify every artwork-selection or cross-type correction case.

Add to Row now displays plain white plus/check state, retains its menu during toggles, and returns from Create New Row with the new membership selected and focused. The shared text-entry dialog dismisses before invoking its accepted action so a replacement menu cannot capture a disappearing keyboard as its parent. Customise Home uses the shared dialog lifetime. Added an integrated create/toggle regression and membership reconciliation coverage. Local XML/source safeguards pass; new tests await CI. Main and signing configuration remain unchanged.

## Checkpoint 22 — playback opener return and adjustment values

Corrected a concrete navigation defect: direct HUD Audio/Subtitles menus used to return to More on Back. Direct entry now returns to the original HUD control; nested child paths retain their parent callbacks. The presentation helper accepts an Activity rather than depending on PlayerActivity internals, enabling a real dialog/track callback test without starting native decoding. The test checks that choosing a track keeps the menu open, moves the tick and Back closes without opening More.

Preview speed and timing pickers now display human-readable values such as 1.00×, 0 ms, +250 ms, −500 ms and +1.5 s. Existing native limits, increments, persistence and engine callbacks are unchanged. Non-timing reuse of the subtitle picker (size/position/opacity) retains its numeric values. Nested native picker windows now use the shared dialog lifetime. Added formatting edge-case coverage. Compilation/tests for this checkpoint are pending; real playback and exact HUD focus remain AWAITING PHYSICAL QA. Subtitle download redesign and complete adjustment-panel conformance remain outstanding.

## Checkpoint 23 — loading artwork cache eligibility

Checkpoint 21 passed run 36256002931 (remote b466cf8199475566bd940525ee86a41ab97b3e0f): compilation, 86 targeted tests, all 250 Video unit tests, 79 overlapping regression checks, 17 WebDAV tests and the identity audit passed. The previously failing matching focus test passes with the intended TV fixture. Both real-provider history tests and Create New Row navigation pass.

The Preparing Playback surface previously ignored a remote backdrop URI even when Picasso had already cached its image. It now accepts HTTP(S) artwork using NetworkPolicy.OFFLINE, as well as existing file/content artwork. Missing cache data retains the dark neutral transition, without starting an artwork network request. Unsupported URI schemes are rejected. Added a regression verifying offline enforcement and local-file handling. This resolves a code-path limitation; absence of flashing on Shield still requires physical QA. Checkpoint 22 and this checkpoint await their own validation.

## Checkpoint 24 — full Subtitle Settings entry

Details subtitle/artwork tools and the HUD Subtitles menu now expose the required full Subtitle Settings shortcut. The existing three-panel Settings shell accepts a one-shot category entry and focuses the Subtitles rail item without toggling an option or bypassing explicit category entry. Existing category contents are retained. Details tools remain open beneath child workflows so Back has a real parent to return to. Added a shell lifecycle regression for category targeting, explicit entry and Left return; compilation and that regression await CI. Subtitle search/download presentation remains a separate outstanding requirement.

## Validation checkpoint after playback changes

Run 36256319349, remote 4ae48ad72ed0f378fbe9575e9d2c4d5e5c6aad10, passed compilation, 86 targeted tests, all 252 Video tests, 79 overlapping regressions, 17 WebDAV tests and the identity audit. The direct HUD track/Back regression and adjustment-value tests passed. Checkpoints 23–24 are remotely preserved at af25e8e8905e6aada0d52dd4601a60063de6893e, tree 032cd89d627f2aff7f0bc5fcfcb29ce48a110379, identical to local d5cd8bf845c10c245f30f34289935830b9924e40. Their validation run is 36256717952 and was still running when recorded.

Enabled native graphics for the Find a Match PNG fixture, so the next CI image supports a real visual inspection rather than an unrendered legacy canvas. This is fixture-only and not a claim of emulator or Shield validation.

## Checkpoint 25 — Settings lifecycle crash correction

Run 36256717952 compiled and passed the targeted suite, but the full suite exposed a genuine Settings shell crash. Its focus-return callback was stored using android.R.id.custom; View.setTag requires an application-specific resource ID. Replaced both tag access sites with a declared R.id.preview_settings_return. A source-wide search found no other framework-ID keyed tags. The existing Settings entry/lifecycle regression is retained unchanged to validate the correction. The cache-only loading regression passed in that run. Full rerun pending.

## Checkpoint 26 — subtitle result review and credential logging safety

The Settings resource-ID crash is gone in run 36259864259. The remaining Settings test assertion used direct RecyclerView key dispatch, which does not perform ViewRoot's final unhandled-direction traversal in Robolectric. The fixture now exercises the actual overridden focusSearch with the focused preference, verifies the exact returned category view and requests it; this remains pending CI rather than presumed passed.

Preview subtitle search now presents the shared result menu and explicit Download review with language, source and available file-hash/release information. Even one result requires acceptance. Back from review leaves the chooser open, and simply searching no longer clears the previous subtitle cache or reports a completed download. Actual download completion now reports RESULT_OK only when the existing transfer method succeeds; failures report cancellation. Native search, account/quota handling, destination selection and playback refresh mechanisms remain in use. Classic UI keeps its prior single-result behaviour. Added a no-download-until-confirmed regression. Progress/error presentation and local-file chooser conformance remain outstanding.

Removed raw OpenSubtitles token/API-key/signed download URL logging, including login JSON exception output that can contain response content. No credentials are changed. Added two source-safety guards and wired them into CI; both pass locally. This is a targeted correction, not a claim that every legacy logging path has been fully audited.

## Checkpoint 27 — subtitle transfer staging

Checkpoint 26 is verified remotely at 2ac73f006cca2db306f4e874d6480c060db62eac, with tree 26881c16f11c76423743410cb7d2794ca2d6892f identical to local 598ed56d2d4743c469c3d7568280273bd19e9c79. Run 36260242226 passed compilation, 86 targeted tests, all 255 Video tests, 79 overlapping regression tests, 17 WebDAV tests and the identity/safety checks. Settings category return and explicit subtitle review regressions now pass. This is not runtime/Shield validation.

Inspection found that the inherited destination write probe could truncate an existing subtitle before the HTTP transfer succeeded. Responses are now staged in app-private cache before any destination probe/write, with connection/read timeouts, cancellation, empty/truncated-response rejection and a 32 MiB transfer bound. Existing subtitle files are no longer probed by writing a zero byte. Temporary payloads are removed on completion/failure; native destination selection is retained. Transfer/save failure logs omit URL-bearing exception messages. Four unit tests cover complete, empty/cancelled, interrupted and oversized staging; pending CI. Local XML/source checks and six Python safety/identity tests pass. Final destination write failures are not yet transactional on every remote protocol; this change does not claim that guarantee. Subtitle progress/error presentation and local chooser conformance remain outstanding.

## Checkpoint 27 preservation and visual inspection

Remote 9ddd6b9227bb1035f3729e2de0ca4b293076b23a is verified against local 9aaa937f9b2fa6a11d282b6478bbbee94e5209c8: both have tree fccd9177ed0a8baa5c3e2ee9c75c45107fa31be2. Validation run 36260670035 is in progress at this record.

Downloaded checkpoint 26's retained validation artifact and visually inspected its native-rendered Find a Match PNG. The heading/instruction, query, full keyboard, T focus and compact result column render visibly without the prior blank-canvas issue. This fixture uses sample results, not a live metadata-provider or physical Shield session.

Started updating stale register mappings from actual source rather than assuming pending means unimplemented. UI-001/002/004 now distinguish existing top-nav structure/animation/shade from outstanding global conformance. In particular, the current blur samples background artwork and the content stage sits below the navigation bar; scrolling-content layering still requires investigation. These are intermediate findings, not any of the three final conformance sign-offs.

## Checkpoint 28 — shared subtitle operation states

Checkpoint 27 run 36260670035 passed compilation, 86 targeted tests, all 259 Video tests, 79 overlapping regression tests and 17 WebDAV tests. The four staged-transfer regressions passed.

Preview subtitle search/download now uses PreviewOperationDialog for indeterminate progress and explicit Cancel/Back; normal completion dismissal does not cancel the operation. No-network, empty results, login/quota and transfer errors remain visible in a shared Close notice rather than toast messages or an immediate Activity exit. Completion is dispatched on the main thread, and cancelled/destroyed activities do not show late results. Search completion dismisses progress before opening the chooser. Classic UI keeps its legacy progress/toast presentation. HUD labels are now Download Subtitles and Choose Subtitles while invoking the existing native actions.

Added three real-dialog regressions for completion versus cancellation, Back/Cancel equivalence and focused Close without Copy/toast, plus a native-action menu-label regression. Compilation and these four tests await CI. Local 430-XML/source safeguards and six Python checks pass. The local/downloaded chooser and active-track synchronisation remain unfinished; physical download/playback and remote-write failure behaviour remain AWAITING PHYSICAL QA. No APK built, main merge, signing change or OAuth credential change.

## Checkpoint 29 — shared language / locale icons

PreviewLanguageIcon is shared by UI Language, Subtitle Reading Language, subtitle download language selections and the HUD subtitle track menu. Only an explicitly regional stored locale (such as en-GB, pt_BR or zh-Hant-TW) supplies a country flag; generic language codes/names do not infer a nationality. If the device font cannot render the real flag glyph, a neutral outline language globe is shown instead of broken indicator letters. Existing native language values, preferred-language filtering and track selection callbacks remain unchanged. Added locale classification tests and a real track-menu icon/selection test; CI pending. Actual flag/font appearance on Shield remains AWAITING PHYSICAL QA.

Checkpoint 28 source is remotely preserved at dae788013f2aa23879748ae4e62a718f4aac4f07 with tree 4da797817fea5cb269b2bcd494b054b7c9389003 identical to local b9f44c1029f266f38eb0df192a37afe137596f43. Run 36277264981 is in progress at this record.

## Checkpoint 30 — live playback track menu reconciliation

Native refreshSubtitleTVMenu/refreshAudioTracksTVMenu replace their TVMenuItem instances, while an already open PreviewPlaybackMenus dialog previously retained callbacks to the old instances. Shared refresh now rebinds those callbacks and checked states in the existing dialog when the structure is unchanged. Structural changes (such as a newly discovered subtitle) rebuild the choices and restore the focused existing item's title where possible; hidden menus are not opened. Subtitle metadata refresh no longer replaces an already open Preview child with the legacy More route. Original player track-switch/persistence callbacks are retained. Two regressions cover live callback/tick replacement without closing the dialog and focus restoration after insertion; CI pending. The separate local-file chooser is still unfinished, and physical track/download/resume behaviour still needs Shield QA.

Checkpoint 28 run 36277264981 passed compilation, 86 targeted tests, all 263 Video tests, 79 overlapping regression tests and 17 WebDAV tests, including shared operation-dialog and subtitle-action regressions. Checkpoint 29 is verified remotely at 99e345b2f3592e7f670d2fd61838591d8c581d55, tree 32d07c5a1fdad093286b7c0548da50e6414432cd identical to local eaea84b99a33a5670ac8927c3f9238ca4dcffeaf. Its validation run 36277635634 is in progress at this record.

## Dependencies and remaining implementation

Production put.io OAuth configuration is absent. Do not supply invented or borrowed
credentials. API/migration safety and all other workstreams remain in progress.
No 4.1.7 APK has been built. No main merge or signing identity change has occurred.

## Checkpoint 31 — recovered local subtitle chooser

Recovered continuation worktree and matching remote source tree; see
RECOVERY_2026-09-27.md. Completed retained chooser wiring and added six tests.
Run 36313890161 compiled and passed targeted checks; 274 full Video tests ran
with one failure. Five chooser tests passed. The association acceptance test
used a substring text finder and clicked the heading instead of the button;
changed it to exact text with an explicit clickable assertion. No assertion
was removed. Full rerun pending. No native playback or Shield validation.

## Checkpoint 32 — scrolling navigation and adjustment geometry

The scroll viewport now extends behind the navigation controls, with the
52dp top inset inside the scrolling content rather than a separate clipped
stage. Non-scrolling utility content retains its existing layout. The
shared shade samples the background and actual content (bounded to a small
cached bitmap), then renders between content and the foreground navigation.
Details compact title accounts for the inset and retains its initial hero
height. Clock text is white. Added a native-rendered pixel regression for
content behind the shade and its darkening/fade.

Playback Speed and Audio Delay now use the same compact 330×180dp panel
bounds; larger subtitle controls retain measured height. Native pickers,
limits, callbacks and persistence are retained. Added real inflated panel
size and dismissal-callback coverage. Compilation and new regressions
await CI; visual performance and physical focus remain unverified.

## Checkpoint 33 — parent-constrained episode correction

Episode Find a Match now searches the current series by S/E coordinates,
episode title, TMDB episode ID or IMDb episode ID. IMDb results cannot silently
switch the parent. Change Series Match explicitly opens the retained complete
series correction workflow. On return, the physical media record resolves the
current parent instead of reusing a stale Episode object's scraper ID. Failed
episode fetches cannot offer a title-only replacement; explicit accepted saves
recheck parent identity, retain the physical video row, report failure and keep
the chooser available. The legacy refetch now also supplies the chosen episode
number. Added coordinate/ID/parent-boundary tests. Native schema preservation
coverage already exists; live metadata-provider/physical Shield QA is pending.
Provider contracts checked against TMDB's own find-by-id and tv-season-details
documentation; no additional provider is introduced.

Checkpoint 32 run 36314258924 compiled and passed targeted source checks, then
ran 276 Video tests with two new-test failures. All six subtitle chooser tests
now passed. Adjustment dismissal coverage needed to drain Android's posted
OnCancel listener. The navigation pixel fixture now completes initial Activity
layout before scrolling, waits for the bounded sample cache and retains its PNG
and pixel values for investigation. A pending shade sample schedules a redraw
so the final scroll frame cannot retain a stale cached strip indefinitely.
These changes require a full CI rerun; no failures are waived.

## Continuation checkpoint 34 — complete-series card reconciliation

- Home synopsis now uses visible title-logo ink width clamped to the handover's 25–32% viewport range (UI006).
- Series enrichment fetches cached season episode metadata and configured season provider availability. The episode rails merge local preferred versions first, then remote metadata, retaining unavailable episodes without making library rows. Recycled provider indicators reset to the local Play glyph. Unreleased/unknown-date episodes do not inherit a season's playable indication.
- Provider episode actions currently resolve the specific series title and record an explicit episode-to-series fallback. Provider-supported exact episode routing still needs conformance review; this checkpoint does not claim it complete.
- Added model regressions for local precedence, duplicate remote episodes, invalid season coordinates, specials and release-date gating.
- Source CI 36330790505 compiled successfully; 278/279 unit tests passed. The failing navigation fixture used a zero-minimum-height plain View inside ScrollView; the expected 160px scroll stayed at zero. Added a minimum height, keeping the rendered-pixel assertions intact. New CI pending.
- Local Java syntax, 430 XML checks and six Python regressions passed. No physical Shield claim, signed APK or final conformance claim.

## Continuation checkpoint 35 — put.io account and discovery ownership

- Checkpoint 34 CI 36331412791 passed compilation, the complete Video suite, targeted backend regressions and WebDAV checks. Navigation pixel regression now passes with a genuinely scrollable fixture.
- Added the provider-documented account/info reader, selecting only identity/status/storage fields. Cloud Services opens native account controls and a paginated read-only file browser. OAuth production connection is still explicitly unavailable, not simulated.
- Disconnect requires inactive versus generic discovery selection, invalidates all account scope generations and retains identity/history. New account parsing and account-wide disconnect regressions cover the boundary.
- The pinned MediaLib patch adds a shared discovery gate. Generic network scans and ownership changes share a lock (NetworkScannerServiceVideo runs in the app process). Owned roots are skipped; owned descendants of ancestor scans are excluded from traversal and retained in stale-record reconciliation. Unrelated sibling sources remain discoverable. Activation persists exclusion before recording API ownership.
- Patch applicability was checked against the recovered pinned scanner source. New source compilation and regression run still required. Association selection, new-file indexing, review UI, source reassignment and production OAuth remain outstanding; this is not a native put.io completion claim.

## Continuation checkpoint 36 — folder association and native ingestion

- Checkpoint 35 CI 36333349103 passed all compilation, Video, backend and WebDAV gates.
- Connected Movies/TV API-folder selection to an explicitly chosen existing WebDAV playback source. Save & Continue explains preservation and zero-duplicate intent. Complete snapshots feed native files_scanned ingestion; its existing database trigger creates canonical Video IDs, not synthetic scraper matches.
- Sync applies under the discovery lock, validates current generation, keeps ambiguous matches untouched until Same File/Keep Separate, persists stable associations and marks missing files without deletion. Finish Later retains completed work. Rename updates the existing raw scanner row's URI, preserving its canonical media ID and history. New imports invoke the existing enrichment service.
- Added explicit resume of retained inactive/generic scopes after reconnect. Source overlap is rejected pending deliberate reassignment support. Native OAuth setup, source reassignment, connected-overview polish and live-device tests remain outstanding.
- Added fixture-backed coordinator tests for stable-ID rename, one-time new-file insertion, incomplete/disconnected snapshots, explicit ambiguity decisions, Keep Separate and safe URI encoding. Native ContentProvider integration still requires verification; fake-library tests alone do not prove it.
- Excluded device-specific provider scanner ownership from portable settings restore so a restored device cannot silently suppress discovery before reconnection.

## Continuation checkpoint 37 — OAuth boundary, scheduling and native safeguards

- Checkpoint 36 CI 36333943160 passed compilation and all source/backend/WebDAV suites, including five new sync coordinator fixtures.
- Added provider-documented OOB request/poll parsing and TV QR/short-code flow. Both registered client ID and validated linking URL remain empty; production connection cannot start. QR contains only the link/code; token goes directly to encrypted storage. Cancellation, timeout and reconnection paths preserve the library. Parser fixtures use test-only values, with no live account requests.
- Native sync joins the existing manual and foreground automatic scan schedule, coalesces concurrent requests and records sanitised operation outcomes. It processes API-owned scopes only and leaves ambiguous matches for explicit review.
- Guarded generic WebDAV deletion (including ancestor folders), associated-file deletion and subtitle rename/delete for API-managed scopes. These operations require provider API support; generic unassociated sources retain their original behaviour.
- Added real native database-schema/trigger tests for new-file insertion, duplicate-path refusal and rename with preserved ID, bookmark and movie match. Added account display-path/status persistence and explicit previous-source handling when reconnecting to a different account.
- Local syntax and XML checks passed before this checkpoint; new CI is required. Source reassignment, remaining UI/register reconciliation, three conformance passes and signed delivery are still open. No physical Shield verification claimed.

## Continuation checkpoint 38 — resumable source reassignment

- Checkpoint 37 CI 36336048449 passed all gates, including the native import/rename/schema tests, guarded deletion and OOB parser tests. Production OAuth values remain empty.
- Change Library Folders now obtains a complete new snapshot before mutation, requests explicit inactive/generic retirement of the previous source and journals the change before updating native paths. Stable provider IDs transfer the same media IDs. A pending change suspends ordinary sync and can be resumed from either affected folder.
- Added a non-destructive association database v1→v2 migration for reassignment journals and retained source ownership. Same-folder WebDAV-root changes preserve absent file identities as missing, not deletions. Account disconnect handles retained sources explicitly.
- Native relocation reads the current canonical URI by media ID, making a retry safe when interruption occurs between the native path write and sidecar commit. Source collisions remain review failures, never overwrites.
- Added native-schema tests for interruption after URI commit, incomplete snapshots and same-folder root changes; new CI pending. Remaining conformance includes complete put.io feature/visual audit, full 115-entry reconciliation, three review passes and signed delivery.

## Continuation checkpoint 39 — read-only put.io search and media information

- Checkpoint 38 CI 36336639069 passed all compilation, Video, backend and WebDAV gates, including resumable source-reassignment fixtures.
- Added account-level Search Files using the shared TV keyboard, cursor pagination and repeated-cursor rejection. Search results accept different parent folders without weakening folder-list parent checks. Search never changes the library.
- File information selects provider metadata, container, duration, bit rate and stream codec/dimensions/channels. No API playback URL is requested or exposed; existing WebDAV playback is unchanged. Back returns to the originating browser/search page.
- Added parsing regressions for cross-folder search, missing totals, wrong file identity, invalid technical values and exclusion of arbitrary response/transport fields. Local syntax/XML checks passed; new tests require CI. Production OAuth and physical Shield behaviour remain unverified.

## Continuation checkpoint 40 — selected-folder moves and honest sync counts

- The sync review retains its original media-ID set across repeated ambiguity choices, so its completion summary separately reports matched existing items and new imports. Duplicate-path prevention is reported specifically as playback-path duplicates, not a claim about semantic title similarity.
- Selected folder ancestry is read by stable IDs before and after a complete listing. A later root rename/move rebases the existing WebDAV path only where the recorded API path is an exact suffix; server and mount prefix remain unchanged. Custom mappings that cannot be proved require review. The existing reassignment journal preserves identity and keeps previous discovery inactive.
- Ancestor cycles and movement during the snapshot invalidate the operation. Added tests for path ancestry/cycles, URI encoding, account/source binding, uncertain mounts and counts across multiple review steps. These tests and native integration require the new CI checkpoint; no physical validation is claimed.
- Ongoing requirements review found that legacy technical-column hydration is still list-triggered. Background technical enrichment remains implementation work, not an OAuth blocker.

## Continuation checkpoint 41 — background technical enrichment

- Checkpoint 40 CI 36337372113 passed compilation, complete Video tests, backend checks and WebDAV tests. This includes checkpoint 39's search/media-info additions.
- Replaced List scroll/view-triggered extraction with a single-file background queue offered from loaded snapshots. Continue Watching/recent items precede the current library category and remaining physical files, including alternate versions. Completion and failure backoff persist by media ID/size/modification fingerprint; cold restart resumes unfinished indexed work.
- Native codec/bitrate/dimensions/audio persist through the existing metadata save path. Cached HDR is loaded without extraction; retriever media metadata is consulted for colour transfer when present, with unknown values left unknown. No playback engine or transport change.
- Removed extraction triggers from list scrolling and Grid/List controls. Added regressions for background coverage of physical variants and fingerprint-specific persisted HDR. The derived completion cache is excluded from portable preference restores.
- Java syntax (797 files), XML/identity/HUD checks (431 files) and six Python tests pass locally. New Android tests and compilation require CI; physical extraction performance and HDR coverage still require Shield QA.

## Continuation checkpoint 42 — first conformance review in progress

- Re-read the delivery gate and began reconciling the register against current source rather than its stale early-checkpoint descriptions. Updated fourteen early UI entries with actual implementations, CI evidence and remaining physical/visual checks. All 115 original entries and requirement text are retained; this is not a completed conformance pass.
- Found the travelling boundary covered top navigation but not persistent category rails. Settings and Network categories now use the same 160ms retargetable rail, without a second per-item background. Top-navigation and Network telemetry now use named semantic controls instead of numeric/generated identities.
- Existing navigation/Settings fixtures now assert the rail/semantic-tag integration. Local syntax/XML checks pass; new CI required. Details information completeness, remaining register entries and normative visual comparison are still under review.

## Continuation checkpoint 43 — Details and shared provider-browser conformance

- Checkpoint 41 CI 36338099673 and checkpoint 42 CI 36338484525 both passed all compilation, Video, backend and WebDAV gates.
- Details Play/More horizontal routing now follows the written explicit mapping. Added original title/TV date-status fields when supplied; local file container, credential-free location and audio sample rates; TV library average size, physical specials, cached technical counts and storage locations. Unsupported awards/quotes/quality claims remain omitted.
- Divider focus adds a restrained gradient halo while retaining exactly the original one-pixel divider stroke. Network frequency now preselects/ticks the persisted interval.
- Verified native bitrate units against pinned aos-avos Source/avos_mr.c: per-track values are bytesPerSec/125 (kilobits/s). Normalised native-index and background values to bits/s for the existing Mb/s List formatter; no playback-unit change.
- Reused PreviewBrowserSurface for native put.io browsing via an internal, non-exported Activity. It provides source/content/context panels, paginated read-only API items, explicit information, parent return and generation-fenced cancellation. Tokens are read from the encrypted store, not intents. Added shared-browser routing/render fixture; actual provider/device behaviour remains untested.
- Connected account overview now includes Movies/TV paths, current ownership/sync status and last-sync age alongside account/storage. Reconciled all twelve put.io register entries with current source and actual CI evidence, separating production OAuth from implementation/physical checks.
- Local 799-file Java syntax, 431 XML/identity/HUD checks and six Python tests pass. New CI is required. This does not complete any conformance pass or the signed delivery gate.

## Continuation checkpoint 44 — diagnostics recovery and test compilation repair

- Recovered branch/worktree without altering the pre-existing missing symlinks or touching main. Checkpoint 43 CI 36338977521 compiled application code but failed unit-test compilation because PreviewProviderBrowserTest did not declare its screenshot helper's checked exception. Fixed in local 73f717bf / remote e961c51ef85bd35b1ce8dde157c697c93fc03b33; rerun 36349549771 is still in progress at this checkpoint.
- Completed the recovered diagnostic UI-state additions for library, Details, Settings, Network and modal depth. Focused cards supply anonymous media IDs. Added snapshot/privacy regression; active filter types are recorded, not user-entered text or private paths.
- Repeated failures retain individual evidence and now include a shared incident ID plus bounded cumulative burst summaries. Added a regression for three retained repeats and cumulative count evidence.
- Card artwork events now carry operation/media/surface/type/cache-layer/failure/latency fields without URIs or exception messages. Artwork operation starts remain verbose flight-recorder traffic rather than generating normal-stream preference churn.
- put.io read requests now record safe service/operation/status/category/duration/retry-count/connectivity fields. Transport completion does not imply schema validation or successful library reconciliation. No credentials or request contents are logged.
- Local Java syntax, XML/identity/HUD checks and six Python tests pass. Android tests for this diagnostic checkpoint remain pending. The complete 115-entry reconciliation, remaining diagnostic coverage, visual/regression passes and signed APK delivery are still outstanding; no physical Shield validation is claimed.

## Continuation checkpoint 45 — shared browser focus hand-off

- CI 36349549771 passed compilation/targeted tests and package identity audit. Full Video suite: 312 tests, one failure; backend/WebDAV gates were consequently skipped. Failure was PreviewProviderBrowserTest: RIGHT retained Example.mkv instead of reaching File Information.
- Shared browser controls now accept programmatic focus in touch mode, consistently with its supplied provider rows and other Preview controls. Explicit context LEFT / source RIGHT return to the prior attached content control, with normal dock fallback if the row was replaced. Extended the fixture to cover content→context→content→source→content.
- This is a source correction, not yet a passing Android regression result. Main and the unrelated missing symlinks remain untouched.

## Continuation checkpoint 46 — initial visual comparison

- Downloaded CI 36349549771 evidence and inspected Details, Home, Grid/List, Customise Home and HUD renders against the applicable written/reference characteristics. Recorded observations and limits in CONFORMANCE_REVIEW.md; this is not a completed visual pass.
- The Details image exposed a hidden lower-content teaser: layout-time hero resizing interacted with the navigation overlay's two measurements. Hero height now resolves before measurement, with a regression requiring 44dp of lower content in the initial viewport. New Android CI/render verification is required.
- Synthetic fixture artwork cannot prove real-logo/crop conformance or physical Shield rendering. The review retains these as explicit pending checks rather than extrapolating success.

- CI 36349938513 compiled the diagnostics checkpoint but failed the old routine-rotation fixture (110 targeted tests, one failure): it counted protected daily/incident streams against the routine stream's seven-file budget. The new incident test legitimately creates those separate streams. The assertion now counts only events/playback rotations; the existing per-file byte-bound assertion still covers every file. Retention limits were not increased. A new complete CI run is required.

## Continuation checkpoint 47 — title-level resume after version reload

- Source review found version switching carried title resume in memory, but a later loader refresh selected the physical file's persisted position. PreviewVariants.restoreTitleResume now derives the selected version's automatic position from the latest persisted title history on every Preview Details loader refresh. It does not rewrite media records or change classic UI behaviour.
- Added regressions for a fresh selected-file reload and a later restart at zero overriding older progress. These require CI; different-duration files and physical playback remain unverified.
- Reconciled further Unmatched, Details and diagnostic entries with explicit evidence and unresolved visual/physical conditions. The 115-entry pass remains in progress.

## Continuation checkpoint 48 — scan batch identity and honest source totals

- CI 36350179013 passed every compilation, Video, identity, backend and WebDAV gate. This verifies the shared browser focus, incident snapshots/burst summaries, corrected rotation assertion and measured Details teaser. The later title-resume change is running separately in CI 36367506423.
- Backend lifecycle broadcasts now carry native batch identity (unique standalone-scan identity), zero initial source counts, and a host/path-only UI location. Locations are not passed into diagnostic events. Existing traversal, discovery gates, reconciliation and scrape scheduling remain unchanged.
- PreviewScanProgress accumulates per-source counts within a batch, deduplicates terminal notifications and resets between batches. PreviewLibraryScan now uses fresh operation IDs for new native batches instead of indefinitely reusing the previous manual scan ID. Live status includes phase/source and aggregate counts, without guessed percentages.
- Existing Preview foreground scheduling now goes through the same request wrapper as Manual, retaining its existing due/connectivity/busy checks and native scheduler. Owned triggers distinguish startup/resume/scheduled/manual and propagate to put.io sync. Unowned native scheduler requests are labelled native_scheduler rather than inventing a trigger.
- Added counter regressions for duplicate completion, failed terminal notification, live source totals, negative input and subsequent batch reset. Local 801-file Java syntax, 431 XML/identity/HUD checks, six Python tests and backend patch applicability pass. New Android/backend CI remains required; complete requested→queued→metadata lifecycle coverage is still under review.

## Continuation checkpoint 49 — unmatched Details action

- CI 36367506423 passed all compilation, complete Video, identity, backend and WebDAV gates, including title-resume reconstruction tests. Scan reporting changes remain awaiting their CI checkpoint.
- Reinspected the corrected Details screenshot from CI 36350179013: divider now leaves visible lower content. This confirms the specific teaser correction, not complete visual conformance or physical Shield behaviour.
- Found UI-021's prominent Match Metadata action absent from the current hero. Added an unmatched-only action through the existing native ACTION_SCRAP callback, honest metadata/synopsis placeholders and explicit Play UP / Match DOWN routing. The strict Play/More horizontal mapping remains untouched. Added regression for callback, placeholders, focus and removal when the native action becomes unavailable.
- Java syntax/XML checks pass; new Android tests and the scan backend patch require CI. Full register reconciliation, remaining diagnostic lifecycle coverage, all three conformance passes and signed delivery are still incomplete.

## Continuation checkpoint 50 — mixed scan outcomes and Search presentation coverage

- Scan progress now retains partial/failed-source flags across subsequent successful sources. The persisted summary cannot report clean completion just because the final source succeeded; raw events include failed-source counts. Extended the batch regression accordingly.
- Added actual Search-surface tests for cursorless/non-focusable query, required placeholder, initial T focus, absence of Caps/Shift, retained keyboard key and an empty Search render fixture. These do not claim populated-result return or physical keyboard QA.
- Reconciled previously stale subtitle/metadata correction/loading/Search entries with their implemented routes and real passing evidence through CI 36367506423. Remaining live/visual/preservation checks stay explicit.

## Post-audit checkpoint 51 — cached technical facts, enrichment and people

- Resumed from the accepted audit without touching the thirteen pre-existing missing symlinks or main. Preserved and incorporated the three pending Java edits and two audio-channel tests.
- CI 36368502479 passed the prior committed tree: 111 targeted, 321 complete Video, 82 selected regression and 17 WebDAV test invocations, with zero failures. These invocations overlap; they are not additive unique-test counts.
- Details and Versions now present measured audio channels without inventing a speaker layout. Both read HDR from the current indexed ID/size/modification fingerprint when runtime metadata is unavailable, without file probing; changed fingerprints and unknown values are rejected.
- Background package offers now include Continue Watching, visible entries and nearby entries; previous page priority is reset when a library scope is offered. Completed foreground packages relinquish their priority. Episode Details queues the known parent series rather than a movie request or an episode ID in the TV namespace.
- Existing cached TMDb credits now populate individual principal-crew and cast cards on local and remote Details. Portrait rails reserve whole columns and use edge fades. No person discovery pages or new data provider were added.
- Added tests for channel unknowns, HDR fingerprint changes, parent-series queue identity/progress preservation and separate principal-crew/remote cast presentation. Java syntax (805 files), XML/identity/HUD/whitespace checks pass; Android compilation and these new regressions still require CI. No requirement verification count or completed conformance pass is claimed from syntax checks.

## Post-audit checkpoint 52 — failure-time state and linked diagnostic evidence

- Checkpoint 51 CI 36371077000 passed compilation, identity, 112 targeted tests, 326 complete Video tests, 82 selected regressions and 17 WebDAV tests (overlapping invocations, zero failures). Physical presentation is not inferred from these results.
- Captured immutable UI state before queuing an incident, including failure UTC time. Added regression demonstrating subsequent navigation does not rewrite the captured page/media/modal state.
- Shared dialogs and library-to-Details return now retain entry/return correlation IDs. Added dedicated diagnostic semantic tags without overwriting application tags, covering shared menu choices and preference keys. Added token/privacy regression; remaining native/navigation surfaces still require tracing.
- Backdrops and official title logos now use correlated artwork traces with safe media/surface/type/source/cache/outcome fields. Expected missing-logo text fallback is not a failure incident. Terminal events are single-shot; the trace privacy/lifecycle regression covers this distinction. Other artwork paths remain to be reconciled.
- Export schema 3 provides linked operation/playback/app-usage summaries. Playback and foreground-use durations require both monotonic boundaries; incomplete evidence stays unknown, not an invented complete lifetime. Both machine-readable and human summaries reference process/sequence/UTC evidence. Added complete, carried-in and foreground-duration fixtures.
- Library diff telemetry now reports reason, old/new counts, inserted/removed/moved/rebound counts and adapter reuse, without changing DiffUtil updates. Source snapshot, discovery, provider refresh, tab and column changes have distinct reasons.
- Corrected an audit finding: the existing generic choice helper already anchors finite choices to the current opener. Network Frequency uses that helper and persisted ticks. Added an actual-window anchor/tick/return test rather than replacing working code. This does not establish physical geometry.
- Android compilation/new regression validation for this checkpoint remain required. No signed APK or completed conformance pass is claimed.

## Post-audit checkpoint 53 — viewport priorities and cache-first Details

- Checkpoint 52 CI 36489423042 passed every gate: 117 targeted, 332 complete Video, 83 selected regression and 17 WebDAV test invocations, zero failures. Counts overlap and must not be added as unique tests. Compilation and packaged/merged identity source checks passed; this was not an APK build or physical test.
- Scrolling now replaces temporary visible priorities instead of accumulating them. The current library-page baseline is restored without resetting persistent package progress or active foreground requests. Horizontal rails inspect actual visible children and schedule reprioritisation when scrolled, rather than assuming the first six titles are visible.
- Details now publishes disk-cached core metadata, credits, seasons/episodes, Extras and locally reconciled recommendations before waiting for network refreshes. Unknown remote availability is not manufactured. Local tag refreshes retain cached individual principal-crew cards.
- Added viewport/baseline regressions, extended the crew refresh regression, and added a disk-only package-consumption regression. New Android tests require CI. The scheduler request now emits an explicit Queued diagnostic; native per-source queuing and Metadata queued evidence remain open.
- The 13 pre-existing missing symlinks remain untouched. Implementation, full 115-entry reconciliation, conformance review and signed delivery are not complete.

## Post-audit checkpoint 54 — measured scan batch and metadata enqueue outcomes

- Checkpoint 53 CI 36490240467 passed compilation/identity, 119 targeted, 335 complete Video, 83 selected regression and 17 WebDAV test invocations (overlapping sets, zero failures).
- Native scheduling now emits per-source Queued events with the actual eligible batch size. Source completion remains distinct from batch completion, and pending sources remain visible between staggered dispatches. Unstarted/coalesced requests release their progress slots without invented checked-item counts.
- Traced the existing metadata start helper and found it swallowed service-start failures. A result-returning companion now reports accepted, null and restricted starts while preserving the existing void entry points and scheduling semantics. Batch diagnostics record Metadata queued, skipped or failed from that outcome, including alternate completion paths.
- Added receiver/accounting and service-enqueue regressions plus a terminal-slot regression. Patch applicability, 809-file Video syntax, four backend-source syntax checks, 431 XML/identity/whitespace checks and six Python tests pass. Complete Android/backend CI remains required. Local-library scan lifecycle/progress still needs completion, so the scan requirements are not marked fully implemented.

### Checkpoint 54 validation correction

- CI 36491115615 failed while applying the backend patch, before Java compilation or tests. The generated diff included three unintended end-of-file blank-line removals; the locally retrieved comparison copies had an extra final blank line absent from the pinned CI checkout. Removed only those non-functional hunks. The scan changes and tests are retained and require a fresh CI run. No tests from the failed run are reported as passed.

## Post-audit checkpoint 55 — correlated artwork routes

- Corrected checkpoint 54 CI 36491589158 passed every gate: 119 targeted, 338 complete Video, 83 selected regression and 17 WebDAV test invocations, zero failures. This validates the actual metadata-enqueue outcome and batch/source accounting regressions. Sets overlap; no APK or physical QA is implied.
- Shared correlated artwork traces now cover library cards, episode stills, recommendations, Extras thumbnails, provider marks, individual cast/crew portraits and the artwork picker, alongside the existing backdrop/title-logo routes. Replaced/recycled requests terminate once; stale callbacks cannot turn cancelled requests into successful outcomes. Portrait failures retain an explicit neutral silhouette.
- Records carry numeric media context, structural surface/type, coarse source, elapsed time and fallback outcome. Picasso callbacks that do not expose the actual cache layer report picasso_unspecified rather than guessing. No URI, path, title, person name or exception message is recorded by the adapter.
- Added a regression for cancellation/rebinding, late callback suppression, fallback reporting and privacy. Java syntax (811 files), 431 XML/identity/whitespace checks and six Python tests pass locally; new Android validation remains required. Full diagnostic route reconciliation and physical artwork/soak QA remain open.

## Post-audit checkpoint 56 — exact safe filter state and modal kinds

- Checkpoint 55 CI 36617861582 compiled the source but stopped at targeted tests: 119 passed, one failed. Archived XML confirms ArtworkRequestTest failed with Picasso context == null because the fixture did not initialise its singleton. Added the same explicit application-backed Picasso setup used by existing artwork tests; no assertion was removed or weakened. The complete Video/WebDAV gates did not run in that failed build.
- Incident snapshots now retain numeric selected years/provider IDs, unmatched state, genre count/selection digest, and the exact active sort criterion/direction. Raw genre metadata is never logged. Page/filter state is captured together before asynchronous incident processing; navigating elsewhere clears the current filter details without changing a captured incident.
- Shared dialogs report structural kinds (choice, reader, review, artwork picker, or generic dialog), with nested-stack restoration. Snapshot regressions cover privacy, delayed-state retention and nested modal dismissal. Native dialog routes still require final reconciliation.
- Local syntax/XML/identity/whitespace and six Python tests pass. Corrected artwork assertions and the new snapshot tests require CI. Requirement counts and conformance-pass completion are not advanced solely on these local checks.

## Post-audit checkpoint 57 — metadata response validation

- Checkpoint 56 CI 36618615484 passed: 122 targeted, 341 complete Video, 85 selected regression and 17 WebDAV test invocations, zero failures. Sets overlap; this is not APK or physical Shield validation.
- Metadata transport operations now link to their parent request. Successful HTTP responses must also supply the expected identity and section structure before new data enters the enrichment cache. Recommendation pages correctly accept their results-based envelope without requiring a source-title ID. Empty valid lists and empty availability regions remain valid; no availability is invented.
- Added semantic rejection, optional-empty-list and invalid-package cache regressions. Local parsing of 811 Java files, 431 XML/identity/whitespace checks and six Python tests pass. The new Android tests still require CI.
- Preserved an unrelated modified store screenshot and the 13 previously missing tracked symlinks. Implementation gaps, full requirements reconciliation, three-pass conformance and signed delivery remain open.

## Post-audit checkpoint 58 — concurrent library progress presentation

- Home's Scan Library card previously used mutually exclusive network/local/metadata branches, hiding concurrent local work. It now presents all observed active phases alongside the shared source/batch network status, and retains the recorded network outcome after activity stops.
- Removed the progress text's fixed 36dp height so multiline source/count details are not clipped. Native remaining-item counters are explicitly labelled remaining; negative/unknown values are indeterminate, never converted into processed totals or percentages.
- Added a regression covering simultaneous phases, unknown counts and retained outcomes. Local 811-file Java syntax, 431 XML/identity/whitespace checks and six Python tests pass. Android validation remains pending; checkpoint 57 CI 36649306709 was still running when this checkpoint was recorded.
- This closes a presentation gap, not the outstanding native local-import lifecycle/accounting requirement. No requirement count or conformance-pass completion is advanced.

### Details focus follow-up

- Enriched cast/crew cards now expose structural diagnostic identifiers using the provider's numeric person ID, with a position fallback only when no valid ID exists. Human names/character text remain outside the diagnostic identifier. Added assertions to the existing individual-crew regression. Local syntax and identity checks pass; Android execution remains pending.

## Post-audit checkpoint 59 — native local-import evidence

- Checkpoint 57 CI 36649306709 passed all gates: 122 targeted, 344 complete Video, 85 selected regression and 17 WebDAV invocations, zero failures (overlapping sets). Checkpoint 58 is in CI.
- Added a separate additive backend patch for worker-local import traces: native start, reconciliation counts, bounded checked-row progress, actual metadata enqueue outcome, and complete/partial/failed/cancelled termination. Paths and media names never enter these events. Existing caught import errors mark the operation partial rather than falsely reporting clean completion. Counters identify technical rows checked, not an invented whole-library percentage or source total.
- Native metadata starts retain their original behaviour; a result-returning companion observes null/restricted/accepted starts. A null scan cursor is now handled before accessing its count. The UI retains local results independently of network results, across page navigation.
- Added backend-to-receiver accounting/partial-outcome and local metadata-start regressions. Four backend files and 812 Video Java files parse; patch applicability, 431 XML/identity/whitespace checks and six Python tests pass. Android compilation/tests remain required. Local request-to-queue correlation and complete source accounting still need reconciliation; this checkpoint does not declare those requirements complete.

## Post-audit checkpoint 60 — published Extra durations

- Extras now accepts bounded ISO duration metadata from the existing YouTube video's public page, only when its published video identity exactly matches the requested Extra. No API credentials, replacement provider, feature-runtime estimate or media probing is used. Missing/changed public metadata remains unknown; live-source availability is not asserted by fixture tests.
- Cached durations display immediately. Focus requests optional metadata with deduplication, a bounded pending set and daily retry throttling, retaining known durations on refresh failure. The existing focus animation and playback action remain intact.
- Added identity/malformed/unknown duration parser regressions and duration-format coverage. Local 814-file syntax, 431 XML/identity/whitespace checks and six Python tests pass. Android CI and real artwork/geometry/remote interaction validation remain outstanding.

## Post-audit checkpoint 61 — search/settings evidence and episode honesty

- Checkpoint 58 CI 36649874668 passed all gates: 122 targeted, 345 complete Video, 85 selected regression and 17 WebDAV invocations (overlapping sets).
- Checkpoints 59/60 CI 36650468103 compiled and passed 122 targeted checks, then completed 351 Video tests with 349 passes and two scan-lifecycle failures. Archived XML shows the later tests did not receive broadcasts (network source total stayed -1). Robolectric replaces the application/receivers while retaining the static installation guard. The fixture now resets that guard before each test; no assertions are weakened. Remaining gates were skipped and must rerun.
- Search cancellation now closes the correlated operation; Search artwork uses the shared safe adapter, result focus uses numeric semantic identifiers, and incidents get Search page context. Search/Settings/Network record structural rebuild reasons and counts without search text or source names.
- Settings child return uses semantic identity rather than matching display text, retaining entry/return tokens. Added a duplicate-label child regression.
- Season/series streaming offers no longer claim availability for an exact episode. Unknown episodes remain visible and non-playable; the series hero retains its own provider route. Conditional handling accepts only an explicitly supplied, matching episode-scoped TMDb watch URL and does not construct speculative provider URLs. No live exact-episode source has been verified, so this remains a provider-capability verification gap, not a claimed live integration. Added scope and non-playability regressions.
- Local syntax/XML/identity/Python checks pass. Full Android revalidation remains required; all three conformance passes and signed delivery remain incomplete.

## Post-audit checkpoint 62 — cached availability consumption and queue scope

- Successful availability responses now persist the selected country's package, keyed by media and season. Disk-only reads distinguish fresh known-empty results from unknown/stale data. Memory promotion retains the original timestamp rather than extending freshness.
- Cached Details consumes season availability and previously validated provider recommendations without waiting for a refresh. The background series package now fetches each season's availability alongside its episode package when streaming is enabled.
- Enrichment selection/retry now respects the current language/country/streaming scope; old-scope jobs remain preserved, not accidentally completed using a new scope. Mid-request scope changes do not advance the old package cursor.
- Added scope isolation, persisted-availability states and cached Details consumption regressions. Local 814-file Java syntax, 431 XML/identity/whitespace checks and six Python tests pass. Android CI remains pending.

## Post-audit checkpoint 63 — applicable remote Details facts

- Checkpoint 61 CI 36682204794 compiled and passed targeted checks; the full Video suite ran 354 tests, 353 passed and one failed. The scan fixture corrections passed. The new Settings duplicate-label regression exposed a real return-path rebuild: refocusing the already-selected rail could clear the newly recreated children. Same-category focus no longer triggers that destructive presentation rebuild; the regression remains unchanged.
- Details now consumes cached/refreshed classification packages, using only the selected country's rating. Streaming-only titles gain published release year, TV episode runtimes where provided, and the same official-title logo cache/render path as local titles. No awards, distributor, locations or technical claims are invented where the existing sources lack them.
- Added country isolation, applicable published fact and shared remote-logo-cache tests. Local 817-file syntax and 431 XML/identity/whitespace checks pass. New tests and the Settings correction still require CI.

## Post-audit checkpoint 64 — modal lifetime and measured operation summaries

- Checkpoints 62/63 CI 36682974483 passed compilation, 123 targeted tests, all 360 Video tests, 85 selected regression checks and 17 WebDAV tests (overlapping sets). This verifies the Settings return correction and new cached/remote Details tests.
- Trailer dialogs now use the shared modal/focus lifetime, including the external-player fallback. Added incident modal-state and opener-restoration coverage.
- Artwork terminal outcomes close their operation exactly once; put.io read transport closes operations on success, failure and cancellation. Exported operation summaries retain parent correlation and report measured monotonic begin/end durations only when both boundaries survive retention. Missing boundaries remain explicitly unknown; stage-relative elapsed values and wall-clock changes are not used to invent durations.
- Added operation-duration/clock-jump/incomplete-retention tests and artwork terminal idempotence coverage. Local 817-file Java parsing, 431 XML/identity/whitespace checks and six Python tests pass. Android CI for this checkpoint remains pending. Requirement counts and conformance/release readiness are not promoted by these source changes alone.

## Post-audit checkpoint 65 — provider semantic-validation correlation

- put.io account/list/search/file parsing now remains inside the request operation. A successful HTTP/JSON transport is recorded separately from a validated provider result; malformed schemas produce a correlated INVALID_PAGE failure instead of a misleading completed request.
- OAuth begin/poll records only fixed operation labels, status, safe outcome, duration, retry count and connectivity. Pending authorisation is not an error. Semantic failures and operation ends are captured without request URLs, link codes, credentials, response bodies or exception messages. Production configuration and live linking remain externally blocked.
- Added intercepted, no-network tests for malformed account and OAuth responses, correlated terminal events and sensitive-fixture exclusion. Local Java/XML/identity checks and six Python tests pass; Android CI remains pending.

## Post-audit checkpoint 66 — complete people-card viewport and shared credits cache

- People rails distribute integer-pixel remainder across repeated visible-card groups and align focus-driven scrolling to complete card boundaries. Added an awkward-width geometry/focus regression without removing individual principal-crew, semantic-identity or remote-cast coverage. Physical glow/fade and rapid D-pad QA remain required.
- The legacy portrait-name mapping now reads credits through the existing shared metadata cache/gateway instead of making a separate untraced TMDb credits request. This reuses enrichment packages and their bounded, semantically validated network diagnostics; no new provider is introduced.
- Local syntax validation passes. Android tests and visual conformance remain pending.

## Post-audit checkpoint 67 — title-logo network context

- Checkpoint 64 CI 36715503044 passed compilation/identity, 125 targeted, 362 full Video, 86 selected regression and 17 WebDAV tests (overlapping sets). Trailer modal lifetime and operation-duration regressions passed.
- Title-logo metadata/image transports now have safe provider/type/status/outcome/duration/retry/connectivity records linked to the parent artwork operation. URLs, API keys and response bodies are never recorded. Metadata must match the requested title and contain a logo list; invalid dimensions/decode are failures rather than an unexplained absence.
- Added an intercepted bounded-response/privacy test. Checkpoints 65–67 Android CI remains pending; no live provider or physical Shield validation is claimed.

## Post-audit checkpoint 68 — network batch operation closure

- Network scan operations now close once at the batch terminal, not at each source terminal. Superseded and never-started requests close explicitly, and a later actual start creates a new operation rather than stretching an already-ended request. Added duplicate-batch-terminal and source-versus-batch lifetime coverage.
- Reconciled DIA-006/007/013/014 register evidence against actual recent CI, retaining route/lifecycle/physical gaps. These edits do not declare Pass 1 complete or change the mutually exclusive audit totals.
- Local Java/XML/identity checks pass; new Android regression pending.

## Post-audit checkpoint 69 — native local request/queue correlation

- Checkpoints 65–67 CI 36716475104 passed compilation/identity, 127 targeted tests, all 366 Video tests, 86 selected regression checks and 17 WebDAV tests (overlapping sets).
- Native local-import messages now emit Requested and Queued before execution, retaining their batch through Started/reconciliation/metadata/terminal states. Removed/rejected queued requests receive cancellation evidence. Service startup/resume, content-change and Android-scan triggers are recorded where established; other origins remain native_import rather than being guessed.
- Import scheduling/delays, reconciliation and authentication behaviour are unchanged. App-side local tracing tracks overlapping batches separately, bounds retained state and suppresses repeated terminal broadcasts.
- Added actual queued/start/cancel state and same-batch/trigger sequence tests. Backend patch applies cleanly to the pinned baseline; local 818 Java files parse, 431 XML/identity checks and six Python tests pass. Android/backend validation remains pending.

## Post-audit checkpoint 70 — enrichment cross-layer correlation

- Synchronous worker operation scopes link background package and foreground Details enrichment to metadata request/transport children. Nested scopes restore their previous parent even after exceptions and do not use inheritable thread state.
- Package and Details failure/terminal records carry their operation ID and safe numeric media identity/category. No title, URL, credential or exception message is introduced. Foreground Details now has an explicit begin/end lifetime.
- Added nested-scope restoration regression. Local syntax/identity checks pass; Android CI remains pending.

## Post-audit checkpoint 71 — Details semantic controls and reader lifetime

- The Details reader now participates in shared modal-state capture and retained opener restoration while preserving its existing layout/frost treatment. Actions, recommendation/Extras cards and fallback people slots receive structural/numeric semantic identifiers rather than generated view positions or names.
- Added reader-modal/opener regression. Local 818-file Java parsing, 431 XML/identity checks and six Python tests pass. Checkpoints 70/71 still need Android CI.
- Checkpoints 68/69 CI 36717304246 completed successfully, including native local queue/backend compilation and the full Video suite. This clears the previously pending scan-lifecycle run, not subsequent local changes.

## Post-audit checkpoint 72 — Scan Library surface reconciliation

- Source review found that Network & Files → Scan Library still displayed network-only status, unlike Home. It now displays combined local/network/metadata status; Network Scanning remains network-only. Added a regression for the distinction.
- Added an explicit toolbar fixture for first-item LEFT, Columns RIGHT retention and DOWN to the library header. This validates the shared toolbar behaviour without claiming full physical navigation coverage.
- Checkpoint 69 exact CI counts: 127 targeted, 369 complete Video, 86 selected regression and 17 WebDAV tests, all passed (overlapping sets). Local Java/XML checks pass; newest tests await CI.

## Post-audit checkpoint 73 — meaningful Details panel evidence

- Reinspected intermediate rendered fixtures and recorded their limitations in CONFORMANCE_REVIEW.md. The old information-panel screenshot did not show its subject; its test now asserts real panel visibility and captures the complete shell after focus/scroll/layout settle.
- Existing field assertions remain intact. This is a test-evidence correction, not a claim that the normative visual pass is finished. Corrected output and latest tests await CI.

## Post-audit checkpoint 74 — provider redirect evidence and navigation fixture correction

- Provider-link redirect requests now record bounded, correlated status/outcome/duration events without URLs, query values or response bodies. Existing redirect safety checks and fallback behaviour are retained. Added a fake-response regression for unsafe redirect rejection and private-query exclusion.
- CI 36753786357 compiled and ran 130 targeted tests: 129 passed, one failed. The new toolbar fixture dispatched DOWN directly to a View, bypassing Android ViewRootImpl's fallback navigation. The corrected fixture asserts that DOWN is unconsumed, the framework focus-search target is the header, and directional focus succeeds. Existing LEFT/RIGHT edge assertions remain unchanged. Full Video, identity and backend stages were skipped after this failure, so this run is not a full validation pass.
- Checkpoints 70/71 CI 36752959935 passed 129 targeted, 371 complete Video, 87 selected regression and 17 WebDAV tests (overlapping sets). Checkpoints 72–74 still require successful complete CI.

## Post-audit checkpoint 75 — direct Speed opener restoration

- Traced UI-044 through PlayerController's actual Preview HUD callback and TVCardDialog bridge. Equal compact Speed/Audio Delay dimensions and a native-picker regression already existed; the requirements-register dimension note was stale.
- Found and corrected a real direct-HUD Speed return defect: the targeted menu-item route installed More as its parent. Direct entry now has no intermediate parent, so Back restores the captured HUD opener. Entry from More retains its existing More return callback; track-menu nesting remains unchanged.
- Added a real nested-dialog regression asserting dismissal, no replacement More dialog and exact Speed opener focus. Updated UI-044 to implemented/verification pending; physical geometry and remote behaviour are not claimed verified. Automated validation remains pending for this correction.

## Post-audit checkpoint 76 — honest accepted-match persistence result

- Inspection of pinned native MovieTags/EpisodeTags revealed that save returns a negative ID on persistence failure. The Preview episode acceptance path ignored that return and could report success. It now requires a nonnegative native save result before closing the chooser, exporting NFO or notifying Trakt.
- Preview movie acceptance uses the same bounded failure presentation and application-context save. Classic matching and unmatched-to-episode enrichment retain their existing paths; the known-parent episode guard is unchanged.
- Added negative-result/valid-result regressions for both native tag types. These are save-result checks, not proof of live metadata-provider or full artwork preservation; UI-039 remains under audit. Tests await CI.

## Post-audit checkpoint 77 — file-association and corrected visual evidence

- Reconciled the pinned native schema: replacement metadata is linked to physical files by remote video ID; insert triggers update each file's scraper ID/type without replacing the physical file row. Extended real-provider movie/series correction tests to assert those associations point to the newly inserted metadata, in addition to existing two-version/bookmark/last-played preservation assertions. This extension awaits CI.
- CI through checkpoint 74 passed: 130 targeted, 374 complete Video, 87 selected regression and 17 WebDAV tests, plus compilation and identity validation. Counts overlap. Corrected Details screenshots were inspected and their limited conclusions recorded in CONFORMANCE_REVIEW.md; no final visual-pass completion is claimed.

## Post-audit checkpoint 78 — individual native crew and refresh focus

- UI-030 review found the enriched-credit path produced individual principal cards, but native tags still put a formatted list of directors/writers into one card. Native structured director/writer lists now produce separate cards without splitting names on punctuation. Missing structured names are not fabricated from an ambiguous formatted string; enriched credits remain the fuller principal-crew source.
- Local tag refresh captures and restores the crew opener as well as cast focus. Empty native crew is cleared before applying any cached enriched credits, avoiding stale people on a later tag update.
- Added a native two-director/one-writer regression, including a comma inside a real name and focused-person restoration. Android validation remains pending.

## Post-audit checkpoint 79 — retained native dialog diagnostics

- Preview-styled native credential/delete dialogs now participate in modal-state capture using window attachment observation. Their original action, cancel and dismiss listeners are retained. Only the structural `native_dialog` kind is recorded; no title, input or credential text is read.
- Entry/return diagnostic tokens retain the opener; dismissal restores it only if the same parent remains active. A newly opened modal is not overridden. Added confirmation cancellation, original-dismiss-callback and safe-modal-context coverage.
- CI 36787680412 (through checkpoint 76) passed compilation/identity, 130 targeted, 376 complete Video, 87 selected regression and 17 WebDAV tests (overlapping sets), including direct Speed return and native-save failure regressions. Checkpoints 77–79 need their own CI.

## Post-audit checkpoint 80 — consistent rebind evidence and activity-state return

- Home/Movie/TV dataset updates now also emit the shared per-view rebuild counter and reason/item accounting alongside the existing detailed diff event. Details information reconstruction uses the same bounded counter with a distinct indexed-snapshot reason where applicable.
- Incident-state review found that a child activity could inherit the preceding page, and returning without a rebuild could retain the child's state. Activity pause now retains its own structural state and clears the active page to unknown; resume restores that state only when no fresh surface state has been supplied. Playback explicitly identifies its page and anonymous native media ID. Modal state remains tied to actual window lifetime, not restored stale activity snapshots.
- Added Details rebind/count/privacy and activity-return/filter-state regressions. Latest completed CI 36788311924 passed 130 targeted, 377 complete Video, 87 selected regression and 17 WebDAV tests (overlapping sets), plus compile/identity. This checkpoint's new changes still require CI.

## Post-audit checkpoint 81 — toolbar semantic identities and register evidence

- Library toolbar controls now carry fixed semantic identifiers independent of translated text, active filters or generated view positions. Existing structural restoration tags remain unchanged.
- Strengthened the actual library-page render fixture: List mode must really be entered, Columns must retain RIGHT, and framework DOWN must target a list header. This complements the isolated toolbar test rather than silently accepting a missing control. New assertions await CI.
- Updated stale evidence paragraphs for enrichment, artwork, scan lifecycle and provider diagnostics to the latest completed run. Final route/package reconciliation remains explicitly open; requirement counts were not inflated from test/commit activity.

## Post-audit checkpoint 82 — validation evidence reconciliation (in progress)

- CI 36829209419, remote revision 6d35a4c9a36315daf2645c92fe9447b66424bd6d (through checkpoint 79), passed compilation and identity checks, 131 targeted tests, all 378 Video tests, 88 selected regression checks and 17 WebDAV tests. These sets overlap and must not be added as a unique-test total.
- Checkpoints 80–81 were submitted as remote a57f8dfa376872d2ab83c548e2457bd43fd5c955, with the same tree as local a001a18c. CI 37077962022 passed compilation/identity and 133 targeted tests; the complete Video suite passed 379 of 380 tests, failing the new actual-page toolbar DOWN-to-header assertion. Later regression/WebDAV stages were skipped.
- The actual headers were focusable but, unlike toolbar controls and list rows, not focusable in touch mode. They now retain eligibility when remote input follows touch input, and receive stable column semantic identities. The render fixture now attaches a visible activity and checks DOWN from every toolbar control, requiring an actual focused column heading. This correction needs CI; it does not weaken the original assertion or establish physical Shield behaviour.

## Post-audit checkpoint 83 — library and browser artwork route parity

- DIA-006 route tracing found direct Picasso bindings in the actual library table and browser context poster which bypassed the shared artwork evidence. Both now use ArtworkRequest with anonymous numeric media identity, structural surface/type and source category, without recording titles or paths.
- List recycling/rebinding cancels the corresponding trace; browser selection changes, provider transitions and detach terminate the prior poster request. Existing image geometry is unchanged.
- Added actual list-binding and browser-selection regressions for request/cancellation correlation, single terminal operation and private-path/title exclusion. These tests await CI. Local 818-file Java parsing and 431 XML/source checks pass.

## Post-audit checkpoint 84 — exact library return coverage

- Added Grid and List integration fixtures exercising the real library adapter, a scrolled non-leftmost item, its Details callback and a refreshed snapshot while a child stand-in holds focus. Return must restore the exact media tag, scroll offset, first visible position and view mode without stealing the child's focus during refresh.
- The stand-in exercises the page's retained-anchor mechanism, not the complete native Details activity lifecycle or physical remote behaviour. Both tests await CI; UI-016 is not yet promoted on their existence alone.
- Checkpoint 82 CI 37078654379 passed compilation/identity, 133 targeted tests, all 380 Video tests, 90 selected regression checks and 17 WebDAV tests (overlapping sets). The actual toolbar DOWN assertion now passes. Updated UI-014, UI-022 and UI-030 with traced behaviour and completed evidence; their physical/visual limitations remain explicit. Checkpoints 83–84 still need validation.

## Post-audit checkpoint 85 — failure-time session correlation

- Further incident tracing found the immutable UI state was captured at failure time, but serialization still inherited the worker-time playback/foreground session and last operation. Delayed incident records now retain those failure-time identifiers, including repeat records.
- Resource measurements remain bounded to major-incident processing, avoiding heavy work in the event producer. Their sampling timestamp and monotonic delay from failure are now explicit, so later resource samples are not presented as instantaneous failure-time measurements.
- Added a delayed-capture regression spanning two actual diagnostic playback sessions. It asserts original-session association, retained failure time, explicit resource-sampling delay and private-path exclusion. Validation is pending.

## Post-audit checkpoint 86 — episode information package consumption

- UI-010/UI-029 tracing found that local episode Key Information could inherit a series premiere year/date while omitting a known native episode runtime. It now uses the selected episode's native date/runtime first, then that exact episode's already-cached season record. Episode rating/counts cannot borrow the series totals; missing values remain absent.
- Inherited series original-title/tagline fields are explicitly labelled as series facts. No provider or new feature was introduced; the existing series/season package supplies the data.
- Added a conflicting-series/episode fixture for year, date, runtime, rating and vote count, plus missing-episode fallback checks. It remains unverified until CI passes.

## Post-audit checkpoint 87 — metadata transport outcome parity

- DIA-013 adapter reconciliation found the shared TMDb/YouTube read transport logged HTTP status/body size but lacked a single outcome carrying service, operation type, retry count and connectivity. It now supplies these fixed categories, correlated parent/operation IDs and monotonic duration; oversized, cancelled and HTTP-error outcomes remain distinct.
- Response URLs, query values, headers, bodies and exception messages are not recorded. Added intercepted oversized-200 and HTTP-404 coverage with private query/body sentinels; no live requests are used by that regression.
- CI through checkpoint 84 (37079271165) passed compilation/identity, 135 targeted, all 384 Video, 90 selected regression and 17 WebDAV tests (overlapping sets), including actual library return and both artwork routes. Checkpoints 85–87 still need successful CI.

## Post-audit checkpoint 88 — honest TV library size accounting

- Details Library Information now labels a partially known byte total as a lower bound and omits average file size until every physical file has a known size. Previously unknown files were implicitly treated as zero in that average. This aligns Details with the existing library-table unknown-size policy.
- Added an actual TV panel regression transitioning from partial to complete size knowledge. It awaits CI; physical library/duplicate-version QA remains separate.
- Checkpoints 85–86 passed CI 37112603608: compilation/identity, 136 targeted, all 386 Video, 91 selected regression and 17 WebDAV tests (overlapping sets). This verifies the episode-package and delayed-session fixes, not the subsequent transport/size-accounting changes.

## Post-audit checkpoint 89 — Search Details return correlation

- Search result entry to Details now retains a safe entry token and weak opener reference until its window loses and regains focus. It records the actual restored semantic control and fallback/success once. Search queries, refresh scheduling and focus routing are unchanged.
- Added an actual result-click/Details-intent regression with window return, retained exact focus, matching entry/return ID and private-title/path exclusion. This covers retained-surface return, not process recreation or physical Shield activity transitions. CI remains pending.
- Checkpoints 87–88 passed CI 37113071931: compilation/identity, 137 targeted, all 388 Video, 92 selected regression and 17 WebDAV tests (overlapping sets). Local checkpoint 89 validation parsed 820 Java and 431 XML files and passed six Python checks; its Android regression still requires CI.

## Post-audit checkpoint 90 — Network workspace semantic controls

- DIA-008 reconciliation found semantic category identifiers but positional identifiers for middle-panel locations and right-panel actions. Existing controls now use fixed action names, numeric source IDs with separate indexed/saved namespaces, and volume type/position without names or paths.
- Keyed diagnostic tags preserve the existing Runnable context-population tag and all navigation/actions. Added an actual-workspace regression covering initial context, scanning controls, indexed/saved sources and private volume labels. Android validation is pending; no live source operation is performed by this fixture.

## Post-audit checkpoint 91 — Network category UP routing

- UI-050 tracing found PreviewPages treated the entire Network workspace as its top row. TopNavigation therefore intercepted UP from any category/control before the workspace could handle it. The page now delegates top-edge eligibility to the actual workspace: only its Overview category qualifies.
- Added actual PreviewPages/TopNavigation coverage for Network Shares → Local Storage → Overview, middle-panel LEFT return and Overview → global Network & Files. Validation remains pending; no physical remote behaviour is claimed.
- Checkpoint 89 passed CI 37113656058: compilation/identity, 137 targeted, all 389 Video, 92 selected regression and 17 WebDAV tests (overlapping sets). Checkpoint 90 has been submitted separately and is not yet verified.

## Post-audit checkpoint 92 — distinguish background refresh reasons

- DIA-009 source reconciliation found initial construction, source-list loading, storage updates and Home preference changes still using the generic control-change reason. These existing rebuild paths now have fixed, distinct diagnostic reasons; scheduling, diffing, adapter lifetime and focus behaviour are unchanged.
- The existing rebuild counters/item counts and indexed-library operation/count records remain in place. This small telemetry correction is covered by source checks and the next complete regression run; it is not evidence of physical flicker or soak performance.
- Reconciled DIA-010/011/012 with failure-time snapshot/resource/burst regressions that passed CI 37120075931 (137 targeted, all 390 Video, 92 selected regression and 17 WebDAV tests). Physical pressure/incident windows remain unverified. Extended the existing actual Details test to cover both hero edges, Resume label and DOWN to the selected lower tab; these added assertions await CI.

## Post-audit checkpoint 93 — artwork chooser rounded containment

- UI-038/global visual tracing found the chooser still placed square artwork directly in its rounded-focus card. It now uses the same rounded inner artwork body as the existing media cards, keeping the outer focus glow unclipped and the established 1.08 whole-card scale. Check marks remain independent in their existing safe corner position.
- Added fixed poster/backdrop-index semantic IDs without image URLs or titles. Save/selection callbacks and single-flight behaviour are unchanged. Existing chooser save/failure/edge tests must pass on the corrected tree; actual image corners/glow remain part of visual and physical QA.
- Source-register integrity check found all 115 unique requirement paragraphs still match the controlling handover. A preliminary added-code search found no named Fanart/SubDL/StevenLu/MDBList/SkipDB, trick-play, transfer-management, position-sync or NFS additions; this does not replace the final complete scope review. Remote main remains 77b2617ad1e48b7a7a85ba28463de0961af5ba4d.
- Network UP correction passed CI 37120377842: compilation/identity, 137 targeted, all 391 Video, 92 selected regression and 17 WebDAV tests (overlapping sets). Checkpoint 92 remains pending separately.

## Post-audit checkpoint 94 — explicit Network entry target

- Further UI-050 source tracing found selecting Network changed the page but left global navigation focused. PreviewPages now fulfils that explicit entry request when the workspace binds, focusing Overview only; reselecting an already visible Network page also returns to Overview. Switching to another tab cancels the pending entry request.
- Extended the actual page/shell regression to assert initial entry and re-entry as well as the verified UP sequence. No action is clicked and no middle-panel control is activated by entry. Validation is pending.

## Post-audit checkpoint 95 — verified source/register reconciliation

- Traced UI-017 technical indexing/cache and retained library adapter, UI-025 actual hero edges/DOWN/Resume and series playback target, and UI-032 More action filtering/direct correction entry. Updated stale register entries against actual behaviour and passed CI 37120690556 (137 targeted, all 391 Video, 92 selected regression, 17 WebDAV; overlapping sets).
- Updated the conformance record with current evidence, newly found defects and explicit incomplete-pass status. Physical rendering/native/live checks are not inferred from unit tests. Checkpoints 93–94 still require their own successful validation.

## Interim Shield QA snapshot requested — 3 October 2026

- The user explicitly requested an interim APK now, followed by continued development towards a separate final APK. This overrides the earlier timing gate for this interim snapshot only, not final acceptance or remaining conformance.
- Application source is unchanged from remote 07aef20b29b85aebb6cf2364f98af4bd9fbb5dd1 (local 8dd4ee6fa0c4b5eecfde44481ffa2b61609586eb; tree cee8a4843d28b2c739156868cebadc1ad6efa83c). Source CI 37121048586 passed all gates, including the corrected artwork picker and explicit Network Overview entry.
- The existing APK workflow is enabled on this branch only for an explicit [interim-shield-qa] commit marker (or its existing manual dispatch). Build commands, signing identity, application identity and branding are unchanged. The workflow records the exact triggering build SHA in its validation artifact.
- Deliver as **Preview 4.1.7 — INTERIM SHIELD QA BUILD**, never final. Signing, packaging and startup results are pending until that workflow finishes. The three-pass conformance review remains incomplete; production put.io OAuth and physical/coexistence evidence remain separate dependencies.

## Post-audit checkpoint 96 — interim validation recovery and continued tracing

- Snapshot source CI 37137362694 passed compilation/identity, 137 targeted, all 391 Video, 92 selected regression and 17 WebDAV tests (overlapping sets). APK run 37137362573 verified the authorised certificate, built/installed the signed debug APK, and passed Preview cold start, reinstall and restart. It stopped at an obsolete Settings smoke sequence that pressed DOWN twice from Integrations, landing on About. No release APK was delivered from that failed run.
- Corrected test navigation to explicit Integrations RIGHT entry, added no-expansion-on-focus and credentials-option assertions, and reconciled the remaining smoke routes with the existing Browse action, non-focusable Search query/actual keyboard and technical-only Info overlay with exact Back focus assertion. Application source, identity, branding and signing remained unchanged.
- Retry 37138307038 stopped because the test driver tapped an already-focused OpenSubtitles child and then pressed centre, activating twice. Captured XML shows the real username/password credentials dialog, not a missing setting. The driver now activates an already-focused target once. Current retry uses remote efa20c21f3ac0b7e18c6636b7bf8c3e870baf1f3; local equivalent 0ced003313125dd3be8ac939e74896deade18b1e, tree f8dc1c10afaefbd87c272fc6b93e2e4bc3067dec. Validation is pending; no pass is inferred from those corrections.
- Re-read all 115 register entries. Reconciled UI-049/050/059/060 against inspected Search/Network/Settings behaviour and successful source-test evidence, with native/physical limitations explicit. UI-018 still has a confirmed missing provider-logo binding in Streaming Service filters; the shared provider-logo helper also lacks monochrome presentation and safe artwork correlation. This is remaining final-development work, not part of the frozen interim application snapshot.

## Post-audit checkpoint 97 — provider-filter icons and persistence regression

- Prepared locally while the interim source remains frozen in CI: Streaming Service filter rows now bind their existing selected-catalogue logos through the shared provider helper. Desaturation preserves internal shapes even for opaque-background logos; neutral fallback remains when artwork is absent/invalid/failed. No provider was added.
- Shared provider choices (including retained Settings/provider menus) use the same monochrome treatment and safe ArtworkRequest lifecycle, fixed surface/type context and detach cancellation. URLs, labels and credentials are not diagnostic identifiers.
- Added an actual PreviewPages filter-dialog regression for multi-select ticks, Done, reopen, surface recreation, separate Movies/TV selections, Clear selection and Clear Filters resetting genre/year/provider state. It also asserts the monochrome transform retains alpha. Java parsing passes (822 files); Android test execution is pending until the interim build finishes, to avoid cancelling that build with a branch push.
- Further DIA-011 review found that the recorder snapshot is still taken on the important writer, not at failure time. Under a long queued write delay, pre-failure rows may already have rotated. This is an open final-development conformance defect, separate from the already-corrected failure-time UI-state capture.

## Interim delivery gate — confirmed native focus failure, 4 October 2026

- Frozen application snapshot run 37156506482, remote bf8af15cd58733439b3da3a8e7e4d57726bd4210, failed the actual remote Info-return assertion. Unlike the previous touch-based check, playback-info-focused.xml confirms preview_info was focused before activation. After Back, playback-info-return.xml/screenshot show the HUD absent. No FATAL EXCEPTION was found in the captured playback log. This is an actual runtime conformance defect, not an obsolete test expectation.
- Compilation, authorised signing, debug packaging/install, Preview startup/reinstall/restart, Settings, Movies/TV, browser and Search checks passed before this failure. The workflow did not reach optimised release packaging or APK upload. No interim APK has been delivered and no final-completion claim is made.
- The exact-snapshot request and the strict failed-test delivery gate now require an explicit delivery choice: permit the unchanged interim snapshot with this known defect, or fix it before producing a changed interim snapshot. Neither source identity nor signing has been changed, and no assertion has been removed or bypassed.
- Subsequent local work is preserved separately: provider filters are committed at 65a346d72e2f352866e56b1493483520100e516f but not pushed into the frozen interim candidate. The diagnostics pre-failure capture correction and its immutable-window/blocked-writer tests remain uncommitted and Android-unverified. Local Java parsing passed for 823 files; all nine Python tooling tests passed. They are not evidence of Android test success.

## Authorised unchanged interim delivery — 4 October 2026

- Mark answered “Deliver” to the explicit known-defect delivery choice. Build commit ff955d90d7a76d157776b07cfb116fd7c67ea924 changes only the APK workflow relative to bf8af15cd58733439b3da3a8e7e4d57726bd4210; application source remains the frozen 07aef20b snapshot. Later local provider/diagnostics work is excluded.
- The explicit interim-known-info-return marker moves the unchanged playback assertion after signed release validation/upload for this preview-mode run only. It is not disabled or converted into a pass: any failure leaves a red job and separately uploaded playback evidence. Full/final builds retain the pre-packaging gate. Startup, release upgrade and required certificate checks remain blocking.
- Run 37192141331 is producing the authorised interim. Delivery, release signature and upgrade results remain pending until observed. This is not a final APK or completion of any conformance pass.

## Post-audit checkpoint 98 — failure-time recorder and technical Info return

- Local-only subsequent development, excluded from the interim: anomalous events capture immutable bounded recorder references before queuing their writer. Rotation/clear while the writer is delayed cannot replace the pre-failure window with later events. Joining remains on the worker; snapshot metadata records capture time and writer delay. Added rotation/age/byte-budget and blocked-writer tests. Post-failure-window semantics still require separate reconciliation.
- The native Info failure is consistent with the normal HUD inactivity fade while the technical dialog is open. PreviewTechnicalInfo now invokes an owning-player HUD restoration callback before requesting the exact captured opener. Classic behaviour is unchanged. Added an actual-dialog hidden-parent regression; closing a finishing/destroyed activity does not reopen its HUD.
- Local validation passed: 823 Java files parsed, 431 XML files parsed, structural/signing-source checks and nine Python tooling tests. These are not Android compilation or runtime verification. New Android tests and the real playback smoke must pass before claiming the defect fixed.

## Interim APK delivered — 4 October 2026

- Delivered `Supernova-4.1.7-INTERIM-SHIELD-QA-ff955d90.apk` from ff955d90d7a76d157776b07cfb116fd7c67ea924; SHA-256 6636e08f48406df22be046241f560f95ff8e9fd084632afebf31a7f47cd9e079, 82,736,779 bytes. The authorised certificate and signed release upgrade/restart passed. Extracted bytes match CI, and packaged Shield banner pixels match the approved asset.
- Run 37192141331 intentionally remains red for the accepted unchanged-snapshot defect: `Back did not restore HUD Info focus`. Compilation, startup, source tests, release packaging/signature and upload passed. No other playback failure is substituted for that known defect. Full details and physical limitations are in INTERIM_SHIELD_QA.md.
- Continuing with provider-filter, failure-time recorder and Info-return corrections separately; subsequent validation must use the strict pre-packaging playback assertion, not the interim-known-info-return exception. No final APK/completion claim is made. Main remains 77b2617ad1e48b7a7a85ba28463de0961af5ba4d.

## Post-audit checkpoint 99 — continued validation and adapter creation evidence

- After runtime replacement, the prior checkout directory was unavailable. Recovered a fresh checkout of remote ed7a3b19ccbe381c32e4630b024212fddce77737 into the current workspace; its tree matches the already-committed local follow-on work. No existing checkout was reset/cleaned. The earlier protected uncommitted screenshot/deleted-symlink state was not available in this runtime and was not silently represented as preserved by the clean clone.
- Source run 37209527222 compiled successfully and passed the new immutable flight-recorder tests, but the Info-return fixture failed at its initial focus assertion before opening the dialog. The fixture now uses a visible attached activity window; its initial and final exact-focus assertions remain intact. No runtime fix is claimed verified from this failure.
- Further DIA-009 tracing found that rebuild events only reported `adapter_recreated=false`. Added explicit creation events at actual Library/Search adapter installation and Home rail replacement, with safe fixed surface/reason and item count. Existing per-view rebind counts remain. Added a real Library/Search recreation fixture distinguishing new view identities from later rebinds.
- Local parsing/structural validation: 824 Java files, 431 XML files, nine Python tooling tests passed. Android execution of this checkpoint is pending.

## Post-audit checkpoint 100 — removal honesty and branding reconciliation

- The visible-window change alone did not resolve the Info test's initial assertion: Robolectric's ShadowActivity stubs getCurrentFocus independently of view-tree focus. The fixture now seeds only that initial shadow field, verifies the actual initial view focus, and checks the actual final HUD focus rather than the stub. Both initial and return behaviours remain asserted; strict native playback validation remains required.
- Network & Files previously removed a displayed source/bookmark even when its database delete returned no success. It now retains the row, reports an unconfirmed removal and does not start indexed-media cleanup on that path. Added actual confirmation-dialog tests using absent database rows for both source and bookmark cases; Android execution pending.
- UI-061/062/063 reconciled against the delivered signed APK, source/manifest evidence and original branding assets. About's descriptive implementation-baseline line still named 4.1.5; corrected to the verified 4.1.6 baseline and added a content regression. No application/version/signing identity changed. Packaged banner pixels are already verified; physical launcher/coexistence evidence remains explicitly outstanding.
- Local validation: 826 Java files parsed and 431 XML/structural checks passed. New source-removal/About tests are not yet run; source/native checks of the earlier Info/provider/diagnostics corrections are running on fbcbc20d5528d21ea0ef6d92704c7e9b6568a7b0.

## Post-audit checkpoint 101 — continuation recovery and event-time post-failure window

- Recovered remote branch HEAD 81efbf6d5e1b6424e52e9fd883258502d643e257 in a separate worktree. The previous checkout's local commit has the identical tree; its thirteen missing tracked links and untracked Python cache remain untouched. No reset, cleanup, replacement implementation or main change was performed.
- Source CI 37341405225 passed compilation, identity, complete Video, selected regression and WebDAV gates at checkpoint 100. Earlier strict native run 37338324041 at fbcbc20d passed playback and exact Info return before packaging, signed release upgrade and signature validation. The known-interim exception was skipped. This verifies the Info correction on the emulator; it does not establish physical Shield QA or final completion.
- DIA-011 tracing confirmed post-failure routing was decided by worker execution time, and the deadline started when the important writer ran. Events immediately following a queued failure could be omitted, while unrelated later events were retained. Routing and deadline now use producer/failure time, with the destination retained per queued event. Post-context appends to its protected daily stream even if written before the delayed pre-failure snapshot or after another incident starts. The latest flight snapshot remains pre-failure evidence; the daily incident stream is authoritative for post-context. Existing bounded queues and dropped-event counters remain explicit limits.
- Added a regression delaying both writers beyond sixty seconds, requiring immediate post-failure context and excluding events outside the window. Local 431 XML/structural checks and nine Python tooling tests pass; the new Android regression awaits CI. Remaining source/register reconciliation and all final conformance passes are still open.

## Post-audit checkpoint 102 — Details provider marks and delayed snapshot metadata

- Source CI 37347960718 compiled checkpoint 101 but the new delayed-writer regression failed: the snapshot envelope still inherited the worker-time last operation even though incident state and captured records used failure time. The envelope now explicitly retains failure-time playback/app-session/operation fields as well. The original delayed-writer assertions remain unchanged and must pass.
- Details episode/recommendation provider marks still used a solid white alpha mask, unlike the corrected filter/helper route. Opaque catalogue logos could lose all internal brand shapes. Both now use the shared desaturation/fallback/artwork-lifecycle helper with translucent focus-only presentation. Added an actual episode-card regression checking the monochrome transform and local rebind reset. No availability is inferred from a series/season-only offer.
- Traced Details package/episode/Extras/recommendation code and existing fixtures. Episode reconciliation is local-first by season/episode; unknown remote availability remains visible and non-playable. Extras omit empty categories and invalid/duplicate video IDs. Recommendation source ordering is metadata relevance then local genre fallback, bounded at twelve. Final register/visual/physical review remains outstanding.

## Post-audit checkpoint 103 — current Info visual fixture and scoped register reconciliation

- Reviewed checkpoint-100 source artifacts, including all twenty retained render images. The Info capture used an obsolete component; changed the fixture to invoke the actual technical overlay and assert Video/Audio/File/Source plus absence of legacy playback actions/private filename. This correction requires fresh CI/render inspection; no runtime feature was changed.
- Reconciled fifteen stale requirement entries with inspected source and existing successful evidence. Explicit live-data, physical and incomplete visual coverage remains recorded. Rejected page-3/episode-only screenshots as proof of Network/full episode-page appearance; no conformance pass is declared complete.

Checkpoint-102 validation: CI 37348732185 at 7503e70f46527ba43a60364b5fa063bf18c24501 passed compilation, identity, complete Video, selected regression and WebDAV checks, including the unchanged delayed-writer assertions and new provider-card/rebind regression. This validates those source corrections, not final APK/native/physical acceptance.

## Post-audit checkpoint 104 — Network and episode visual evidence repair

- The old page render loop clicked children by position; after the canonical navigation spacer was introduced, its fourth click hit that spacer and captured TV again. The fixture now clicks semantic Home/Movies/TV/Network controls and requires actual Overview/Scan Library content before taking the Network image. It supplies an empty source inventory rather than starting asynchronous database discovery in a geometry fixture.
- Episode metadata rendering now includes the actual TopNavigation shell, retaining the original episode-versus-series fact assertions. These are evidence repairs, not a new UI implementation. Android rendering/CI and image inspection are pending.
- Read the pinned MediaLib MovieTags/ShowTags save paths during metadata-preservation review. Existing provider tests establish physical rows/bookmarks/associations, not every live correction/version/artwork scenario; UI-039 remains explicitly under review rather than promoting that evidence beyond what it proves.

Checkpoint-104 validation: CI 37350349198 at 798669e6f3dd7168dd478a5fb102f8f18a41fb86 passed every source gate. Corrected Network, full-shell episode and active technical-Info images from artifact 11362870687 were inspected; each now shows its intended surface. Fixture artwork/empty metadata and physical limitations remain. CONTINUATION_2026-10-05.md records exact commits, preserved workspace state, verified evidence and unfinished work. No final conformance/APK claim is made.

## Checkpoint 105 — explicit full/native validation (5 October)

Continued from remote 67c82487, checkpoint 104; preserved both worktrees and all existing work. Added an explicit [full-validation] commit trigger to the existing APK workflow, selecting full mode without any interim exception. Full mode now runs the complete Video suite instead of the earlier selected subset. Existing strict pre-packaging playback/Info return, pinned certificate, native startup, release upgrade and WebDAV checks remain mandatory. Candidate artefacts are explicitly not final acceptance. This run is required for the current post-interim source; execution is pending. No application identity, signing key, main branch or release scope changed.
