# Next-version implementation — 30 October 2026

## Validated continuation after PR #29 review

Current application source `f5e2924` passes full conformance, focused regressions and isolated library checks with independently downloaded evidence. The approved HUD, four-tab inline Settings, source-backed four-section editor, core-first Details delivery and real global-focus preparation lifecycle are implemented. Settings has one immediate rail boundary with a separate selected check. ChaptersDB acquisition/cache/strict ID paths are wired; the owner chose **unverified sets stay hidden**, and current v1 data lacks cut/runtime proof, so no visible external coverage is claimed.

Dedicated branch: `codex/supernova-next-implementation-2026-10-30`.
Base: verified PR #27 tip `b4a6ee7a010c9b5b6af8731108f3973477bfc7f5`; delivered 0.147 application checkpoint `f4abf7cb880243ba00481f82beee19d20bc72e33`. The initial GitHub branch/PR audit found no newer implementation. Main and all existing development/documentation branches are untouched. Documentation branch `83a600f` contains older application source and was therefore not used as the application base. Its new design/requirement documents and PR #28 audit are carried here without replacing newer baseline documents.

Authority: complete consolidated handover in `docs/handovers/`, latest locked HUD composition, all-four-tab inline interaction decision, strict draft Edit Library blueprint and PR #28 source audit. Existing global design system, playback behaviour, release reports, build architecture and retained source contracts govern unchanged capabilities. Physical evidence attachments are not available locally.

## Requirement → source → validation / status

| Requirement | Source / regression coverage | Current status |
|---|---|---|
| HUD single row, title, persistent bubble | PlayerController, PlayerActivity, PreviewHudTiming, PreviewSeekPolicy, player_controller_experimental / PlayerControlsRelativeLayout | Implemented approved one-row composition, seek-first/source-convention graph, measured tooltip and constrained title fitting; no further engineering approval gate; focused/full automated validation passes; physical acceptance remains open |
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

## Completed continuation validation

Exact source `f5e2924cc8f7302032933f8aec8793030b6e1b0f`: full unsigned conformance [41137713737, attempt 2](https://github.com/heretoecode/Supernova/actions/runs/41137713737) **PASS**, completed 30 October 2026 at **23:02:45 UTC**. Later commits change documentation only; tested tree objects and preserved inputs are recorded in continuation/PRESERVATION.json.

| Check | Actual read-back result |
|---|---|
| Complete application suite | **1,258 passed**, zero failures/errors/skips |
| Complete library suites | **198 MediaLib + 102 FileCoreLibrary passed**; four existing database/benchmark skips retained |
| Focused UI/playback 41137713734 | **183 passed**, zero failures/skips; subset of the full suite |
| Isolated scraper/FTPS 41137713712 | **Six passed**; subset of the library suites |
| Release lint | **Zero errors; 1,496 warnings**. One extra SetTextI18n warning versus 7011e87; no new suppression or baseline |
| Local tooling / parser | **134 tests passed; 1,103 Java files parsed; zero syntax errors** |
| Independent evidence / payload | Both ZIP SHA-256 and CRC checks; 312 JUnit XML files and lint read back; all **84** packaged native libraries traced to pinned inputs/runtime AARs; **139 inventory records / 136 distinct runtime archives** and POM hashes match the reviewed SBOM |
| Android 23 evidence | Existing 28-case proof reused for unchanged transport/native inputs; not rerun on hardware here |

Receipts: [FINAL_VALIDATION](continuation/FINAL_VALIDATION.json), [FOCUSED_VALIDATION](continuation/FOCUSED_VALIDATION.json), [LIBRARY_FIXTURE_VALIDATION](continuation/LIBRARY_FIXTURE_VALIDATION.json), [UNSIGNED_CONFORMANCE](continuation/UNSIGNED_CONFORMANCE.json), [independent APK verification](continuation/INDEPENDENT_UNSIGNED_APK_VERIFICATION.json). Unmodified current-source software captures: [HUD/editor/global Settings/all four playback tabs](continuation/software-captures-f5e2924/README.md).

The independently verified **unsigned inspection APK** is 95,382,168 bytes, SHA-256 `ab335395cf25743e5506662feb187e77a99c9ceb8066d41d9f17df9b49d3ff5f`, identity **0.147 / 147**. Android requires signing before installation. [APK_DELIVERY.md](APK_DELIVERY.md) links the exact payload, expiry and receipts. No signed installable QA build or Drive delivery occurred.

Attempt 1 stopped before application compilation on HTTP 429 downloading a pinned Jetty input. Attempt 2 used the same source and dependency hashes and passed. Intermediate failed/cancelled runs remain diagnostic; no result is inferred from cancellation. Physical Nvidia Shield, original Concept A4 comparison and signed installed upgrade: **NOT RUN**.

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
