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

## Dependencies and remaining implementation

Production put.io OAuth configuration is absent. Do not supply invented or borrowed
credentials. API/migration safety and all other workstreams remain in progress.
No 4.1.7 APK has been built. No main merge or signing identity change has occurred.
