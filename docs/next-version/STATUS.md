# Next-version implementation — 30 October 2026

## Active continuation after PR #29 review

Current source `f5e2924` uses the attached global focus observer for real media-page escape/return and includes the corrected HUD indentation. Focused/full/library runs **41137713734 / 41137713737 / 41137713712** are pending. Local 134 tooling tests and 1,103-file Java parse pass. Prior 83aede9 UI/library tests pass; its full tests pass but release lint failed. Preceding results are historical, not current-source acceptance. See CONTINUATION.md.

The owner has authorised HUD engineering details within the approved visual design and confirmed strict ChaptersDB matching: **unverified sets stay hidden**. The new source checkpoint implements the one-row HUD, four-section editor using retained shared geometry, core-cache-first Details delivery and settled-focus prefetch, single-boundary Settings rail, and bounded ChaptersDB acquisition with strict suppression, explicitly focusable HUD targets, and a single selected/check versus focus boundary in Settings. The completed validation below describes the **previous** checkpoint `7011e87`, not these new changes. See CONTINUATION.md.

Dedicated branch: `codex/supernova-next-implementation-2026-10-30`.
Base: verified PR #27 tip `b4a6ee7a010c9b5b6af8731108f3973477bfc7f5`; application frozen at `f4abf7cb880243ba00481f82beee19d20bc72e33`. GitHub branch/PR audit found no newer implementation. Main and all existing development/documentation branches are untouched. Documentation branch `83a600f` contains older application source and was therefore not used as the application base. Its new design/requirement documents and PR #28 audit are carried here without replacing newer baseline documents.

Authority: complete consolidated handover in `docs/handovers/`, latest locked HUD composition, all-four-tab inline interaction decision, strict draft Edit Library blueprint and PR #28 source audit. Existing global design system, playback behaviour, release reports, build architecture and retained source contracts govern unchanged capabilities. Physical evidence attachments are not available locally.

## Requirement → source → validation / status

| Requirement | Source / regression coverage | Current status |
|---|---|---|
| HUD single row, title, persistent bubble | PlayerController, PlayerActivity, PreviewHudTiming, PreviewSeekPolicy, player_controller_experimental / PlayerControlsRelativeLayout | Implemented approved one-row composition, seek-first/source-convention graph, measured tooltip and constrained title fitting; no further engineering approval gate; current CI/device acceptance separate |
| Compact settings Audio/Subtitles/Video/More | PreviewPlaybackMenus, PlayerActivity native menus, PreviewPlaybackMenusTest | Implemented inline expansion under parent, collapse/focus restore and direct toggle state; native capability owners preserved; visual/Shield acceptance pending |
| Movie/episode ChaptersDB | Official site /api, published JS, retained live probes | Movie/episode acquisition, normalisation, cache/backoff and strict ID paths wired. Official API explicitly permits integration. Owner chose unverified sets hidden; current v1 lacks cut/runtime proof and therefore supplies no visible external markers |
| Embedded chapters / IntroDB | PlayerActivity/Controller, native avos chapters patch, existing chapter and intro tests | Already present; preserve, no chapter auto-skip added |
| Edit Library four sections, posters, stationary frame | CustomLibraryActivity, CustomLibraryPage, SharedThreePanel | Four sections and actual indexed poster/item preview implemented with stable shell and guarded semantic/scroll restoration. Existing shared metrics, Both, Delete location and in-panel Save retained; only a new visual geometry/type revision remains optional |
| Edit Library dirty destination | CustomLibraryActivity.requestExit/save, NextQaWizardTest | Implemented: successful Save and Discard honour requested continuation; failure retains editor |
| Edit Library stale preview return | CustomLibraryActivity dispatchKeyEvent/back | Implemented: store semantic tag and resolve current control with valid fallback |
| Retain unavailable saved year | CustomLibraryActivity year choices | Implemented; no filtering/schema change |
| Static library background | PreviewPages / PreviewBackdrop, PreviewBackdropTest | Implemented; library loads suppressed and late callbacks ignored, static existing utility scene; Home preserved |
| Keyboard caret / Caps | KeyboardInput / PreviewKeyboard | Implemented: vertical offset follows centred text; explicit ON/OFF label |
| Custom statistics first entry / tab highlight | PreviewPages.requestChildFocus / TopNavigation / custom activity | Implemented displayed-set statistics invalidation and nav-only focus transfer; physical reproduction/acceptance open |
| Missing regional certification | PlaybackAgeOverlay, existing 0.147 tests | Added missing-cache classification lookup through existing TMDb gateway; output/identity/deadline guards preserved; physical failure still open |
| Details hero wash / progressive load / Reception | details package, OmdbSupplement | Reception and local text scrim implemented; same-frame coalescing, settled-focus priority, core-cache-first delivery and stage/first-paint timing implemented; physical loading acceptance remains open; production OMDb access is disabled, so no fake provider rows |
| Settings focus ghost / toggles / integrations grouping | SettingsWorkspace / PreviewFocusRail / PreviewSettings / registry | Single Settings rail boundary with explicit selected check implemented; existing switch styling retained; reported hardware defects require device acceptance |
| Subtitle language icons | PreviewLanguageIcon, SettingsChoiceDialog | Explicit-region PNG flags and neutral generic languages already shipped; hardware exhaustive visibility remains open |
| Network & Files | 0.147 TRACEABILITY and preserved tests | Already shipped six-tile redesign; do not repeat; physical regressions remain open |
| Home clipping and row controls | PreviewPages, retained refinement register | Existing controls preserved; physical evidence still needed |
| Preparation artwork flashes | PreviewPlaybackLoading, 0.144 release evidence | Neutral official fallback already shipped; physical reproduction outstanding |

## Prior completed validation and delivery — before continuation

Full unsigned conformance [run 41118855808](https://github.com/heretoecode/Supernova/actions/runs/41118855808) **PASS**, completed 30 October 2026 at 20:29 UTC, exact source `7011e877d026e745a079846827ababe03c2af798`. At that checkpoint, application source last changed in `53eb9e0`. This historical receipt predates the current continuation source; do not apply its results or tree IDs to the new code.

| Validation | Read-back result |
|---|---|
| Application suite | 1,233 passed; zero failures/errors/skips |
| MediaLib suite | 198 passed, two retained database-fixture skips |
| FileCoreLibrary suite | 102 passed, two retained transfer/PROPFIND benchmark skips |
| Focused UI/playback run 41118855716 | 131 passed; zero failures/skips (subset, not additive to full suite) |
| Isolated scraper/FTPS run 41118855851 | Six passed (subset) |
| Release lint | Zero errors; 1,495 warnings remain |
| Tooling / Java parser | 134 tests passed; 1,099 Java files, zero syntax errors |
| Runtime/native conformance | 139 reviewed runtime hashes; 84 packaged native hashes; 24 source-built FFmpeg inputs matched |
| Independent downloads | Complete archive SHA-256/CRC, 310 JUnit XML files, lint XML, 139 runtime archives/POMs and reviewed SBOM, all 84 APK native hashes and APK/receipt/input digests verified |
| Android 23 transport proof | Existing 28-case proof reused for unchanged production inputs; not rerun on physical hardware in this task |

Before changes: 134 tooling tests passed and 1,097 baseline Java files parsed. The workspace has no Android SDK or pinned sibling modules; all Android compilation/tests/lint/assembly above ran in the existing pinned GitHub build architecture. Test-only overlays fix expired public synthetic TLS certificates and disambiguate an exact adaptation expectation without altering production TLS/scraper behaviour or original native/build/signing inputs; see TEST_INPUT_REVIEW.md. Intermediate failed/cancelled runs are diagnostic, not accepted evidence.

Receipts: FINAL_VALIDATION.json, FOCUSED_VALIDATION.json, LIBRARY_FIXTURE_VALIDATION.json, UNSIGNED_CONFORMANCE.json and INDEPENDENT_UNSIGNED_APK_VERIFICATION.json. Selected unmodified software captures and provenance: [software-captures/](software-captures/README.md). Original Concept A4 images were not found in the limited read-only Drive check (REFERENCE_ASSET_CHECK.md). Software captures do not establish visual parity or hardware acceptance.

An **unsigned development APK** is available from the successful run, retaining baseline identity **0.147 / 147**. It is not a signed new-version QA release. See [APK_DELIVERY.md](APK_DELIVERY.md) for filename, exact digest and expiry. No signed APK or Drive upload, main merge, tag or public release.

## Current decisions and acceptance boundaries

HUD visual approval is retained, and the owner delegated its measurements/D-pad engineering. Graph A and paused-finish hiding are resolved engineering choices. All-four-tab inline Settings remains approved. The owner explicitly confirmed strict ChaptersDB matching: unverified sets remain hidden. There is no renewed HUD or ChaptersDB approval request.

The editor implements the approved structure/parity using the existing shared geometry. Adopting the proposed new region proportions/type/card/grid treatment or moving Delete would be a new visual choice; retaining current conventions needs no decision. No generated settings, runtime values, posters or facet expansion were introduced.

Current ChaptersDB API data lacks authoritative cut/runtime proof; this is an external data constraint, not an unanswered personal preference. Acquisition does not imply usable verified chapter coverage. Embedded chapters remain available and take precedence. API integration permission and actual ID/schema probes are documented in CHAPTERSDB_CONTINUATION_RESEARCH.json.

Physical Nvidia Shield, original Concept A4 comparison and installed signed upgrade/retention: **NOT RUN**. Reported hardware symptoms and all changed playback/focus/layout behaviour need device acceptance. Procedures: SHIELD_ACCEPTANCE.md. The current first-paint/core/artwork timing events support measured device diagnosis; source tests do not establish latency or visual parity.

Draft PR: https://github.com/heretoecode/Supernova/pull/29, unchanged branch and stacked base, unmerged. Existing main, development and design/audit branches remain untouched. Current scope: CONTINUATION.md; previous changes: CHANGELOG.md.
