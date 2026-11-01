# 1 November agreed fixes — implementation and validation

Scope: [authoritative agreed fixes](2026-11-01-agreed-fixes.md). Continuation base:
`fc1f07b832af14274455d81b654a9631440e7add` on PR #29's 0.148 branch.
Work branch: `codex/agreed-fixes-2026-11-01`. Existing branches and PR bases remain unchanged; no merges or resets.

## Implementation

| # | Change and underlying cause | Regression coverage |
|---|---|---|
| 1 | Library layout manager now handles focus scrolling for the controls row, retaining the statistics above it. RecyclerView's internal focus scroll goes directly through the layout manager, bypassing the former RecyclerView rectangle override. | LibraryStatisticsLayoutTest: Movies/TV, entry focus, no-op refresh, 720p/1080p/4K. |
| 2 | Page diff decides whether the focused cell actually changes before transferring focus. No-op/unrelated refreshes retain the view. Carousel-only updates no longer transfer focus from Home rows. | Home global-focus event regression; Filters no-op refresh checks actual view and absence of transient focus events. |
| 3 | Remove selected-section tick prefix from Settings navigation; retain option selection marks and radio controls. | SettingsWorkspaceTest rapid rail navigation; existing option/persistence tests. |
| 4 | Boolean rows show On/Off/Unavailable, disabled switch track/alpha are distinct, and the parent row stays fully opaque so its shared focus outline is consistent. Disabled rows still reject changes. | SettingsWorkspaceTest state labels, disabled activation and focus; existing persistence/dependency tests. |
| 5 | Explicitly retain dynamically named regional flag drawables in release resource shrinking. Keep existing region-aware binding and neutral fallback for generic/unknown languages; no arbitrary country assignment. | Resource-retention tooling test; Audio/Subtitle picker binding tests for en-GB, en-US, en and unknown; existing native flag rendering tests. |
| 6 | Provider picker binds by known ID/name instead of requiring cached logo-path lookup. Skip group/Done rows so binding cannot replace Done's action glyph with a provider fallback. | Picker regression without catalogue cache, provider fallback tests, Done drawable identity. |
| 7 | Always create Reception and explicitly show Ratings unavailable when empty. Wire the existing review reader for supported title identities even when ratings are absent; unsupported contexts show Reviews unavailable. | ReceptionSupplementTest for empty, primary, supplementary and reviews-only data; existing review loading/error tests. |
| 8 | Clear only Network & Files landing workspace background; restore that surface when entering its existing browser. Preserve padding, grid, six cards and their focus backgrounds. | Network142Test checks six focusable cards with retained backgrounds and transparent landing workspace. |
| 9 | Known duration, rather than isPlaying, controls finish prediction visibility. While paused, Ends at means the finish if resumed now and updates with the clock; unknown duration stays hidden. | NextHudTest paused and unknown-duration transitions. |
| 10 | Increase finishing-time start margin from 12dp to 24dp in existing transport row. | NextHudTest verifies rendered separation, usable seek width and text fit across viewport/text-scale cases. |

## Validation status

Implementation and tests committed; Android validation in progress. Do not treat this document as physical acceptance.

- Local tooling: **141 tests pass**; `git diff --check` passes.
- GitHub Actions: [first implementation run](https://github.com/heretoecode/Supernova/actions/runs/41313256939), [additional regression run](https://github.com/heretoecode/Supernova/actions/runs/41313338811).
- The dedicated workflow runs full app/library JVM tests, release lint and Java/resource compilation using the existing pinned dependency/native inputs. It does **not** assemble, sign, publish or upload an APK.
- Explicit flag keep rules and pre-shrink rendered assets are tested; final resource-shrunk package/device rendering must be verified during a separately authorised future APK cycle.

## Outstanding physical Nvidia Shield QA

All ten items require device acceptance: enter TV library and confirm complete statistics without scrolling; hold/repeat remote navigation through Home/Filters; verify no Settings rail ticks while genuine option selection remains; compare On/Off/Unavailable focused/unfocused; inspect regional and neutral language icons; inspect provider logos/fallback and Done; load cached/live/unavailable Reception and review errors; navigate all six network cards and return from the browser; pause/resume/seek/speed-change the HUD; check timestamp separation at supported resolutions and enlarged text. JVM/native graphics tests cannot establish device remote timing, TV font rendering, overscan or real service availability.

Playback settings redesigns, standalone Subtitles window and subtitle appearance grouping were **not implemented**. No version bump, new release, APK production, signing, device installation, data reset or migration.
