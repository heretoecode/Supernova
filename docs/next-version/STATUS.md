# Next-version implementation — 30 October 2026

## Active continuation after PR #29 review

The owner has authorised HUD engineering details within the approved visual design and confirmed strict ChaptersDB matching: **unverified sets stay hidden**. The new source checkpoint implements the one-row HUD, four-section editor using retained shared geometry, core-cache-first Details delivery and settled-focus prefetch, single-boundary Settings rail, and bounded ChaptersDB acquisition with strict suppression. The completed validation below describes the **previous** checkpoint `7011e87`, not these new changes. New focused/full runs are pending. See CONTINUATION.md.

Dedicated branch: `codex/supernova-next-implementation-2026-10-30`.
Base: verified PR #27 tip `b4a6ee7a010c9b5b6af8731108f3973477bfc7f5`; application frozen at `f4abf7cb880243ba00481f82beee19d20bc72e33`. GitHub branch/PR audit found no newer implementation. Main and all existing development/documentation branches are untouched. Documentation branch `83a600f` contains older application source and was therefore not used as the application base. Its new design/requirement documents and PR #28 audit are carried here without replacing newer baseline documents.

Authority: complete consolidated handover in `docs/handovers/`, latest locked HUD composition, all-four-tab inline interaction decision, strict draft Edit Library blueprint and PR #28 source audit. Existing global design system, playback behaviour, release reports, build architecture and retained source contracts govern unchanged capabilities. Physical evidence attachments are not available locally.

## Requirement → source → validation / status

| Requirement | Source / regression coverage | Current status |
|---|---|---|
| HUD single row, title, persistent bubble | PlayerController, PlayerActivity, PreviewHudTiming, PreviewSeekPolicy, player_controller_inside variants | Composition approved; exact geometry/focus graph requires owner approval; not implemented |
| Compact settings Audio/Subtitles/Video/More | PreviewPlaybackMenus, PlayerActivity native menus, PreviewPlaybackMenusTest | Implemented inline expansion under parent, collapse/focus restore and direct toggle state; native capability owners preserved; visual/Shield acceptance pending |
| Movie/episode ChaptersDB | Official site /api, published JS, retained live probes | Blocked: documented examples fail but real movie/TV sets respond; no verified data licence/usage policy; no structured runtime/edition matching. No provider requests added to application |
| Embedded chapters / IntroDB | PlayerActivity/Controller, native avos chapters patch, existing chapter and intro tests | Already present; preserve, no chapter auto-skip added |
| Edit Library four sections, posters, stationary frame | CustomLibraryActivity, CustomLibraryPage, SharedThreePanel | Stationary preview frame, semantic return and loading/error/empty distinctions implemented; four-section/poster redesign still gated on geometry, pages 3/4, stats and Save/Delete decisions |
| Edit Library dirty destination | CustomLibraryActivity.requestExit/save, NextQaWizardTest | Implemented: successful Save and Discard honour requested continuation; failure retains editor |
| Edit Library stale preview return | CustomLibraryActivity dispatchKeyEvent/back | Implemented: store semantic tag and resolve current control with valid fallback |
| Retain unavailable saved year | CustomLibraryActivity year choices | Implemented; no filtering/schema change |
| Static library background | PreviewPages / PreviewBackdrop, PreviewBackdropTest | Implemented; library loads suppressed and late callbacks ignored, static existing utility scene; Home preserved |
| Keyboard caret / Caps | KeyboardInput / PreviewKeyboard | Implemented: vertical offset follows centred text; explicit ON/OFF label |
| Custom statistics first entry / tab highlight | PreviewPages.requestChildFocus / TopNavigation / custom activity | Implemented displayed-set statistics invalidation and nav-only focus transfer; physical reproduction/acceptance open |
| Missing regional certification | PlaybackAgeOverlay, existing 0.147 tests | Added missing-cache classification lookup through existing TMDb gateway; output/identity/deadline guards preserved; physical failure still open |
| Details hero wash / progressive load / Reception | details package, OmdbSupplement | Reception and local text scrim implemented; same-frame enrichment coalescing implemented; additional preloading/coordinated entry work remains open pending measured device-stage timing; physical loading acceptance remains open; production OMDb access is disabled, so no fake provider rows |
| Settings focus ghost / toggles / integrations grouping | SettingsWorkspace / PreviewFocusRail / PreviewSettings / registry | Existing shared switches shipped; reported hardware defects still open |
| Subtitle language icons | PreviewLanguageIcon, SettingsChoiceDialog | Explicit-region PNG flags and neutral generic languages already shipped; hardware exhaustive visibility remains open |
| Network & Files | 0.147 TRACEABILITY and preserved tests | Already shipped six-tile redesign; do not repeat; physical regressions remain open |
| Home clipping and row controls | PreviewPages, retained refinement register | Existing controls preserved; physical evidence still needed |
| Preparation artwork flashes | PreviewPlaybackLoading, 0.144 release evidence | Neutral official fallback already shipped; physical reproduction outstanding |

## Validation and delivery

Full unsigned conformance [run 41118855808](https://github.com/heretoecode/Supernova/actions/runs/41118855808) **PASS**, completed 30 October 2026 at 20:29 UTC, exact source `7011e877d026e745a079846827ababe03c2af798`. Application source last changed in `53eb9e0`; subsequent changes to current branch HEAD are documentation/evidence only. Tested Git tree IDs are retained in PRESERVATION.json.

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

## Remaining work / decisions

- HUD: composition approved; exact geometry, remote graph and paused Ends at presentation still require the locked blueprint's owner sign-off. [OWNER_DECISIONS.md](OWNER_DECISIONS.md) includes measured tokens and a diagram; application HUD remains unchanged.
- Edit Library: independent behavioural fixes are implemented; full four-section geometry, poster preview, Pages 3/4, Save/Delete placement, naming and stats await the recorded exact design decisions. Eight annotated diagrams are proposals, not approved screenshots.
- ChaptersDB: live movie and TV sets verified. Production integration remains unimplemented pending authoritative usage terms and reliable edition/runtime/episode matching; retained probes demonstrate the schema limitations. Existing embedded chapters/IntroDB remain preserved.
- Details entry: same-frame enrichment coalescing is implemented. Additional preloading/coordinated-entry engineering remains open pending measured device-stage timings; no complete loading-performance fix is claimed.
- Reported hardware defects and regression acceptance: Settings focus ghosting/toggle visibility, exhaustive picker rendering, Home clipping/controls, preparation flashes, first-entry statistics and all changed playback/focus behaviour still require the original/device scenarios.

**Physical Nvidia Shield and installed upgrade/retention tests: NOT RUN.** Exact device procedure, source evidence and limitations are recorded in [SHIELD_ACCEPTANCE.md](SHIELD_ACCEPTANCE.md).

Draft PR: https://github.com/heretoecode/Supernova/pull/29, stacked on 0.147 and unmerged. Existing main, implementation and design/audit branches remain untouched. Full changes: [CHANGELOG.md](CHANGELOG.md).
