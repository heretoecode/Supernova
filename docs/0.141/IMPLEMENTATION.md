# Supernova 0.141 implementation and validation ledger

Status: all ten approved workstreams implemented; final duplicate-track focus correction undergoing repeat candidate validation. Software checks and device acceptance are tracked separately; no signed APK or physical acceptance claimed.

## Continuation baseline

Inspected 22 October 2026: fresh clean clone; no pre-existing local work. Main remains `75d9f012fa4f65869eef5d972ad4725df504bd9c`. Draft/unmerged PR 18 points to `cf257aaa59ad8d59b2ab7252d5beae174f5879c5`; its application, resources and build files equal delivered signed 0.140 source `866f21f11f27dea4846ddd3cac57d98b281eecbb`. Changes after that source are documentation/evidence only. No other open 0.140 fixes or newer implementation heads were found. Created isolated `codex/supernova-0.141-implementation-2026-10-22` at PR 18 tip; no reset, clean, revert, merge, previous-branch edit or maintenance implementation.

Authority: complete handover and scope copied byte-for-byte from planning tip `306e1b35689104c3307cfc06b4d3193ba343be31`; complete FD-002 audit copied from `663646c129ca43882827c2f42dd7e8aa7c936820`. Current user authorises implementation, validation and candidate preparation, overriding those documents' earlier documentation-only scheduling wording. Underlying branding, package and signing identity are retained.

## Requirements → implementation → evidence

Every UI row still requires physical Shield acceptance. Previous candidate local complete suite: **1,067 tests across 226 classes passed, zero failures/errors/skips**; final duplicate-label focus correction has nine targeted passing cases, full repeat pending, plus95 tooling tests and release lint with zero errors. Targeted and language/lifecycle passes are also retained. Exact-source full native/APK CI is pending; see RELEASE_REPORT.md. No compilation-only fix claim.

| Requirement | Code / investigation | Required evidence | Status |
|---|---|---|---|
| HUD transport left, slim blue seek, tooltip, remaining + finish right, no duplicate elapsed | PlayerController / experimental XML / PreviewHudTiming | seek/focus/time tests; software renders; physical playback | Implemented; HUD timing/cold-start and real-view checks pass; Shield pending |
| Accurate conditional chapters; no online fetching | inspect actual decoder metadata boundary | supported metadata proof or documented omission when unavailable | Implemented; Java validation and actual embedded MKV native demux/parcel probe pass; Shield pending |
| Right translucent popup; exact horizontal Subtitles / Audio / Display / Playback tabs; Subtitles first | PreviewPlaybackMenus | real callbacks, scroll, tab focus, Back, timer and lifecycle tests | Implemented; eight callback/focus/persistence tests and all four software tab renders pass; Shield pending |
| Preserve C01–C05, S01–S29, A01–A26 and I01–I07 with approved dispositions | FD-002 inventory; migrate existing handlers | per-ID migration register + regressions | Complete per-ID register in PLAYBACK_MIGRATION.md; conditional hardware/workflows pending |
| Remove Technical Information / Report a Problem from playback UI; preserve diagnostics | PlayerController / menus / activity | menu and key-path assertions | Implemented in dialog/native card/key paths; diagnostic infrastructure retained; Shield pending |
| Audit B01–B14 reconcile against 0.140 | activity/controller/pickers/downloader | finding-by-finding evidence and limits | All 14 findings reconciled; B04 retained sentinel semantics; backend/provider/hardware QA pending |
| Hero overlap + smoothness; rapid reversals, repeats, boundaries, focus, art loading | PreviewPages / PreviewFeaturedCard | deterministic interrupted transition tests; hardware frame timing pending | Implemented; 24 interrupted alternating inputs accepted without stale transforms; Shield frame timing pending |
| All licence pagination removed; dependency/transitive notices + required texts retained | FoundationAboutWorkspace / legal inventory / legacy entries | complete disclosure reachability; dependency inventory and hashes | Implemented; all 60 texts losslessly partitioned; 138 runtime artifacts/64 archive notices audited; API28 reader passes; Shield pending |
| Global language icons: reliable region only; neutral generic; unknown/nonlanguage exclusions | PreviewLanguageIcon + all renderers | language/region/unknown/mixed + accessibility tests | Implemented with shared mapping; explicit valid regions only; additional renderer checks recorded separately; Shield pending |
| Statistics values centred; entire panel matches toolbar opacity | LibraryPageHeader / MenuFoundation | bounds + rendered varying values / scaling | Implemented; existing native statistics geometry tests pass; Shield varied-value/text-scale acceptance pending |
| Shared keyboard one-line input; . , / \\ keys; bottom-left Caps Lock and all actions retained | PreviewKeyboard / KeyboardInput / consumers | punctuation, Caps, selection, path and D-pad tests | Implemented; punctuation, Caps active state/toggling, selection and one-line geometry pass; Shield consumers pending |
| Put.io connection focus; all controls, Back/re-entry/cancel/error/external return | PutioAccountController / AuthFlow / browser | focus tests; real account flow requires owner | Implemented; replacement/re-entry, simulated success/cancel/error and late-close tests pass; real authorisation and physical acceptance release-blocking |
| Edit Library far right, controls left; responsive and reachable | PreviewPages / PreviewToolbar | geometry + remote traversal + scaling | Implemented; pinned trailing action with separately scrolling left group; normal/narrow large-text checks pass; Shield pending |
| All Library rows centre in content viewport; ends/short/fast/data refresh | PreviewPages / focus recycler | grid/list/custom row centring + repeated input | Implemented; grid/list middle and final rows settle centred; restoration regression passes; Shield boundaries/refresh pending |
| Movie/TV card → tabs 20–24dp separation; surfaces and scroll retained | PreviewMoviePage (shared movie/TV) | both media types / sizes / text scale / D-pad | Implemented; section lowered 22dp without shrinking card; movie/TV and 720p/1080p/4K/large-text renders pass; Shield pending |
| Home/Search/Network/Library/Details/playback/backup/scan regressions | existing full test suite | build, lint, full suite + device checklist | Final local complete suite 1,067 passes without failures/errors/skips; full native/APK CI pending |
| Candidate / signing / APK / Drive / notes | existing protected procedures only | exact source, original cert, checksum, full readback | Protected workflow ready; GitHub exact-branch permission denied HTTP403; unsigned conformance pending |

## Missing visual evidence

Exact approved 22 October HUD image and IMG_5004/IMG_5007 are absent from the repository. Connected Drive searches for HUD, 0.141 and the filenames returned no matches; Supernova image search returned older Statistics atlas images. The written approved contract is the implementation authority per handover. No substitute is claimed as the original image. Physical Shield screenshots/video and visual acceptance remain owner work.

## Validation boundaries

No physical Shield is attached. Real Put.io account authorisation is unavailable. Successful compilation alone will never close an owner-reported defect. Record test failures and corrections; preserve original 0.140 evidence. Do not merge main or create a public release.

## Investigation outcomes

- Hero navigation deferred the selected index until an outgoing animation completed and rejected inputs while animating. Cached inner cards also retained transforms. Selection now advances on each input and only the incoming container animates; cached transforms reset before reuse.
- Removing the focused Put.io inline content allowed focus to transfer to the enclosing navigation. Replacement keeps focus inside the account target, then requests the actual new control; closed generations and owned auth pollers cannot steal it back later. Linking owns its error/configuration dialogs and checks closure again before a queued authorisation callback; test credentials/provider responses are simulated, not a real account. No authentication bypass or fabricated successful login.
- Playback menu refresh removed a focused row before rebuilding it, allowing automatic focus to select the first tab. The selected tab now holds focus during replacement; row focus is restored afterwards. Native callbacks and persistence flushing remain attached.
- Licence pagination was a display-layer split, not separate legal obligations. Continuous bounded blocks preserve every character of all existing texts; consolidated entry points no longer require Previous/Next page actions. A local lint error caught an API33-only read and it was corrected to bounded API23-compatible streaming.
- Edit Library must stay visible even when the left group overflows. It has a separate fixed trailing group and explicit D-pad edge links. Details spacing moves the whole tab/content section down, retaining card dimensions.

## Evidence index

- `PLAYBACK_MIGRATION.md`: every audit control, action, indicator and B01–B14.
- `LOCAL_INITIAL_TEST_RESULTS.json`: retained initial full-suite failures and lint finding; superseded, not a pass receipt.
- `LOCAL_FULL_TEST_RESULTS.json`: complete final 1,067-case local suite, zero failures/errors/skips.
- `LOCAL_ASSEMBLY_PROBE.json`: local shrunken unsigned assembly and resource checks; explicitly incomplete native payload, not for distribution.
- `LOCAL_SOURCE_READBACK.json`: candidate application-source correspondence with the expected Foundation provider rewrite.
- `LOCAL_TARGETED_TEST_RESULTS.json`:58 targeted cases and95 tooling cases; overlapping follow-up groups in LOCAL_LANGUAGE_TEST_RESULTS.json and LOCAL_AUTH_TEST_RESULTS.json.
- `NATIVE_CHAPTER_PROBE.json`, `fixtures/`: four-ABI wrapper compile and real API28 x86 embedded-MKV demux/metadata evidence, with limitations.
- `LEGAL_INVENTORY.json`: complete current runtime/archive disclosure inventory.
- `visual-evidence/`: actual native Android view renders; playback scene synthetic, no Shield claim.
- `BASELINE_0140_BINARY_VERIFICATION.json`: independent whole-file verification of the preserved delivered APK.
- `PRESERVATION_READBACK.json`: earlier remote references remain unchanged.
- `SHIELD_QA.md`: full physical acceptance and real account/storage/hardware checklist.
