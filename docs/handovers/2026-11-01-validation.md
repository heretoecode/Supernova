# 1 November agreed fixes — implementation and validation

> Historical ten-fix validation record. The later owner-authorised Subtitles/0.149 continuation is tracked in [docs/0.149/IMPLEMENTATION.md](../0.149/IMPLEMENTATION.md). The exclusions and no-APK statements below describe this earlier completed task, not the subsequently authorised continuation.

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

All ten items are implemented and covered by passing automated regression checks. Physical Shield acceptance remains pending. Flag retention and picker binding are validated; final resource-shrunk package/device rendering remains pending as described below.

The first full application run compiled successfully and completed lint with zero errors / 1,247 warnings. It ran 1,264 application tests: 1,256 passed and eight legacy Reception assertions failed because they required the panel/review action to be hidden, contrary to the new handover. Those assertions were updated. All 300 library tests passed (four existing skips). The superseded follow-up run also exposed an intermittent programmatic-scroll/row visibility assertion; the layout-manager fix was narrowed to the controls row, preserving existing poster centring. Exact-source affected-class validation [41315819719](https://github.com/heretoecode/Supernova/actions/runs/41315819719) **passed**: 134 application regressions, 300 library tests, four existing library skips; release compilation and lint passed (zero errors / 1,247 warnings), and 141 tooling tests passed. XML reports and representative native-rendered HUD, Settings and statistics screenshots were downloaded and inspected.

The broader follow-up [41315305839](https://github.com/heretoecode/Supernova/actions/runs/41315305839) passed the revised Reception acceptance but failed two of 1,265 application tests during Robolectric application setup: a delayed production CustomApplication HDMI receiver callback reached a torn-down ActivityThread. No audio selection assertion failed. AudioTrackSelectionPolicyTest now uses a plain Application with an explicit Archos global test context, excluding production background bootstrap; audio selection implementation is untouched. The final affected-class rerun [41317089694](https://github.com/heretoecode/Supernova/actions/runs/41317089694) **passed**, including all 14 audio policy tests. No final whole-application green suite is claimed.

- Final validated source: **`3929e769fb69ce172de56ebe40fc07c238546bcc`**. Later completion commits change documentation only.
- Final [run 41317089694](https://github.com/heretoecode/Supernova/actions/runs/41317089694): **148 affected application tests pass**, **300 library tests pass**, **four existing library skips**, **141 tooling tests pass**. Release Java/resource compilation passes; release lint: **zero errors / 1,247 warnings**.
- Downloaded artifact **13087628882** and read back all JUnit XML and lint counts. [Machine-readable receipt](2026-11-01-validation.json) includes class counts, archive hash, exact source and limitations.
- Local tooling: **141 tests pass**; `git diff --check` passes.
- GitHub Actions: [first implementation run](https://github.com/heretoecode/Supernova/actions/runs/41313256939), [additional regression run](https://github.com/heretoecode/Supernova/actions/runs/41313338811).
- The dedicated workflow supports full application validation on manual dispatch and runs affected application regression classes on push, full library JVM tests, release lint and Java/resource compilation using the existing pinned dependency/native inputs. It does **not** assemble, sign, publish or upload an APK.
- Explicit flag keep rules and pre-shrink rendered assets are tested; final resource-shrunk package/device rendering must be verified during a separately authorised future APK cycle.

## Outstanding physical Nvidia Shield QA

All ten items require device acceptance: enter TV library and confirm complete statistics without scrolling; hold/repeat remote navigation through Home/Filters; verify no Settings rail ticks while genuine option selection remains; compare On/Off/Unavailable focused/unfocused; inspect regional and neutral language icons; inspect provider logos/fallback and Done; load cached/live/unavailable Reception and review errors; navigate all six network cards and return from the browser; pause/resume/seek/speed-change the HUD; check timestamp separation at supported resolutions and enlarged text. JVM/native graphics tests cannot establish device remote timing, TV font rendering, overscan or real service availability.

Playback settings redesigns, standalone Subtitles window and subtitle appearance grouping were **not implemented**. No version bump, new release, APK production, signing, device installation, data reset or migration.

Main independently advanced to `28484b9f069d09d248908506509f9720aa3c3c94` during this task with a proposed playback Settings document. That work was not modified or merged. The authoritative handover remained blob `f6f345055d37c02866947ce9465cea06d1597f2c`; PR #29 remains draft/open at its original tip. Completion PR #30 is draft against the 0.148 continuation branch.
