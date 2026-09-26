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

## Dependencies and remaining implementation

Production put.io OAuth configuration is absent. Do not supply invented or borrowed
credentials. API/migration safety and all other workstreams remain in progress.
No 4.1.7 APK has been built. No main merge or signing identity change has occurred.
