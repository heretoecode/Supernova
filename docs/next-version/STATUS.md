# Next-version implementation — 30 October 2026

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
| Custom statistics first entry / tab highlight | PreviewPages.requestChildFocus / TopNavigation / custom activity | Investigation open; Implemented displayed-set statistics invalidation and nav-only focus transfer; physical reproduction/acceptance open |
| Missing regional certification | PlaybackAgeOverlay, existing 0.147 tests | Added missing-cache classification lookup through existing TMDb gateway; output/identity/deadline guards preserved; physical failure still open |
| Details hero wash / progressive load / Reception | details package, OmdbSupplement | Reception and local text scrim implemented; same-frame enrichment coalescing implemented; physical loading acceptance remains open; production OMDb access is disabled, so no fake provider rows |
| Settings focus ghost / toggles / integrations grouping | SettingsWorkspace / PreviewFocusRail / PreviewSettings / registry | Existing shared switches shipped; reported hardware defects still open |
| Subtitle language icons | PreviewLanguageIcon, SettingsChoiceDialog | Explicit-region PNG flags and neutral generic languages already shipped; hardware exhaustive visibility remains open |
| Network & Files | 0.147 TRACEABILITY and preserved tests | Already shipped six-tile redesign; do not repeat; physical regressions remain open |
| Home clipping and row controls | PreviewPages, retained refinement register | Existing controls preserved; physical evidence still needed |
| Preparation artwork flashes | PreviewPlaybackLoading, 0.144 release evidence | Neutral official fallback already shipped; physical reproduction outstanding |

## Validation

Before changes: 134 Python tooling tests PASS; JDK parser reads 1,097 Java files with zero syntax errors. No Android SDK or pinned sibling modules are installed in this workspace. A dedicated unsigned GitHub workflow reuses the existing pinned native/dependency preparation and runs application/library tests, lint, APK assembly and conformance guards. It does not sign or publish. Version metadata stays at the baseline during development; any unsigned build is a development artefact, not a new QA release.

Physical Nvidia Shield testing: NOT RUN for every changed item. No physical device or original clips attached. No signed APK, Drive upload, main merge, tag or public release. Signing remains a later protected owner-authorised step.

## Progress checkpoint

Draft PR: https://github.com/heretoecode/Supernova/pull/29 (stacked on 0.147, unmerged). Latest application checkpoint `11c7cdd` (subsequent workflow/documentation only) includes independent fixes; newer docs-only commits must not be mistaken for a different tested application. Full unsigned run https://github.com/heretoecode/Supernova/actions/runs/41090453193 is pending. Superseded intermediate runs were cancelled to avoid redundant build use; the first run is retained for diagnostic evidence. No successful Android/build/visual result is claimed until its actual outcome is read back.

Local after changes: 134 tooling tests pass; JDK parses 1,099 Java files with zero syntax errors. Eight annotated Edit Library diagrams and measured source token/focus proposal are available in OWNER_DECISIONS.md; they are proposals, not approved screenshots. HUD focus approval was requested while independent work continues.

Focused regression/render run: https://github.com/heretoecode/Supernova/actions/runs/41090547508 . The comparable prior complete 0.147 workflow took approximately 35 minutes; current runs remain pending, not passing evidence. Full change log: CHANGELOG.md.
