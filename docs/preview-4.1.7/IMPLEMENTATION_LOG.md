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

## Dependencies and remaining implementation

Production put.io OAuth configuration is absent. Do not supply invented or borrowed
credentials. API/migration safety and all other workstreams remain in progress.
No 4.1.7 APK has been built. No main merge or signing identity change has occurred.
