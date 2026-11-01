# 0.149 — PR #30 Subtitles continuation

Continues from `d84ab3ae37330d8eb65018f68fca53f55756c33a` on draft PR #30, including all ten completed fixes. Authority is the [owner handover](../handovers/2026-11-01-subtitles-and-test-apk-handover.md) and [consolidated blueprint v2](https://github.com/heretoecode/Supernova/blob/main/docs/design/2026-11-01-subtitles-implementation-blueprint.md). Image B controls the main panel; only Image A's right-hand Track and Download panels control the pop-ups. The superseded main panel in Image A was not used.

## Implementation and preservation

- `PlaybackSubtitlePanel` implements the one-border main panel, fixed title/four tabs, two unboxed sections and ten ordered rows. Inline choices have text/separators; five colour swatches have a selected ring; toggles separate their value from row focus. Track and Download are independent surfaces.
- `SubtitlePanelStyle` shares only the new subtitle surfaces' geometry, colours, rounded panel/focus treatment, thin semantic marks and scrollbars. Existing Android/Supernova sans-serif fonts are used. No stock background image or new font is packaged.
- `SubtitlePlaybackBackend` bridges the real `TVMenuItem.previewClick()` track/download actions and existing `SubtitleManager` getters/setters and preference keys. Track enumeration is live, with selected ticks and current callbacks refreshed in the existing popup. No artificial tracks enter production.
- The new Download presentation is explicitly selected by a playback-only intent extra. Existing non-playback subtitle download entry points retain `PreviewSubtitleResults.show()` and their existing appearance. Provider lookup, result selection, confirmation, quota/error handling, download and file association remain the existing implementation.
- `PreviewLanguageIcon` supplies bundled regional assets only for explicit regional metadata, with neutral fallbacks for generic/unknown language codes. No emoji flags or arbitrary country mappings.
- A single stable dialog content host swaps the approved subtitle surface and retained legacy surface. The new subtitle strip uses Left/Right to focus tabs, OK to activate, and Down to activate and enter the first content row. This avoids the different tab orders trapping remote focus.
- Audio/Video/More retain their existing content, appearance and callbacks when selected. Their legacy render/navigation checks remain. Playback HUD source/resources and every source file implementing the ten fixes are frozen by `verify-pr30-scope.py`.

## Compatibility mappings and deviations

- Size presets use existing legacy 0–100 values: Small 25, Medium 50, Large 75. Existing custom sizes remain unchanged and are announced as custom until a preset is activated.
- The existing renderer accepts arbitrary ARGB colours, so all five approved colours use the existing colour preference without a new model or migration. Existing non-palette colours remain intact.
- Vertical position retains values 0 / 128 / 255. The renderer's existing 0–255 range covers the **bottom third** of the subtitle canvas. Bottom/Middle/Top therefore refer to positions within that retained adjustment range, not full-canvas anchors. Extending the engine range is outside this presentation task.
- Opacity maps percentages to the existing 0–255 storage values: 0 / 64 / 128 / 191 / 255. Custom values survive opening/closing unchanged.
- The HUD uses 100 ms steps; the legacy TV delay picker uses 1 ms. The approved new control uses **100 ms per Left/Right/OK activation**, with the existing millisecond engine/storage unit and per-video persistence retained.
- The existing provider result model has no trustworthy byte-size field. Result rows show **Size unavailable**, never illustrative file sizes. The search field filters the existing provider result set locally; it does not introduce another search/download backend or change provider queries.
- The existing explicit download confirmation is retained. When available, the existing local-file chooser is reachable from a separate action in the Track popup; it is not fabricated as a track.
- Missing data, real track names and selection states take precedence over the mock-ups' sample values. Legacy language/preparation/default preferences remain in their existing global Settings locations and are not rewritten by this panel.

## Validation status

**Implementation and automated conformance pass; signed APK delivery is BLOCKED by GitHub environment access. Emulator/Shield visual parity is NOT VERIFIED.** Tested source is `cb28b1b936891374bc801f0b58b3397926fc3086`; subsequent commits update documentation only.

- [Full conformance run 41328625056](https://github.com/heretoecode/Supernova/actions/runs/41328625056) passes **1,276 application tests** with zero failures/errors/skips; **300 library tests pass** with **four existing skips** (200 MediaLib cases including two skips; 104 FileCoreLibrary cases including two skips); **147 tooling tests pass**. Release compilation and unsigned APK assembly pass. Release lint: **zero errors / 1,252 warnings**. Downloaded JUnit/lint XML and both artifact archives were independently read back; [full validation receipt](FINAL_VALIDATION.json).
- [Focused run 41328625028](https://github.com/heretoecode/Supernova/actions/runs/41328625028) passes **33 application tests**, zero failures/errors/skips, release Java/resource compilation and **147 tooling tests**. Its six selected classes are not another full application/library suite; no lint or APK is produced by that focused job. [Archive hash and class counts](FOCUSED_VALIDATION.json).
- Initial regressions caught a retained old dialog scene, reentrant focus transitions and queued popup dismissal consuming rapid Back twice. One stable content host and synchronous popup-state clearing address these causes. The actual-window regression traverses all four tabs, enters retained Video content and activates More; tests also assert that detached subtitle content is absent. Key fixtures lay out ViewGroups before dispatching keys.
- Controls are covered for exact row order/full default visibility, toggles, all presets, custom-value preservation, 100 ms delay, focus retention, live track callbacks/count/availability refresh, scrolling, regional/neutral drawables, download confirmation callbacks and one-level Back restoration. Existing subtitle preference/replacement and all ten-fix regression classes are included in the full suite.
- Source scope checks preserve the ten fixes, manifest/package identity, original native inputs and original signing helpers. The shrunk APK retains regional flags/provider resources. **139 runtime archives, 24 source-built FFmpeg inputs and 84 packaged native libraries match**. The prior 28 actual API23 transport/trust cases are reused only because their exact dependency patch/lock inputs remain identical; no new API23 emulator run is claimed.
- The five additional lint warnings are reviewed in [SUBTITLE_LINT_WARNINGS.json](SUBTITLE_LINT_WARNINGS.json): three session-owned static view references, a deliberately programmatic-only view constructor and explicit legacy RIGHT gravity. Close releases the additional references (regression asserted), and the existing player stop path invokes that close. No warnings were suppressed. Physical lifecycle/soak acceptance remains pending.
- Actual native-graphics Android view renders use labelled synthetic fixtures. They are **software renders, not emulator/Shield screenshots or live provider evidence**. [Final comparisons](VISUAL_REVIEW.md) include exact [render provenance](software-renders/RENDER_PROVENANCE.json). [Audio/Video/More captures](LEGACY_TAB_RENDER_COMPARISON.json) are byte-identical to the preserved ten-fix fixtures.
- This workspace has no `adb`, Android emulator or `/dev/kvm`. An installed signed upgrade, actual subtitle rendering/provider download, and physical Shield acceptance remain **NOT RUN**. See [Shield QA](SHIELD_QA.md).

## Signing and delivery

Version **0.149 / code 149**, package **app.supernova.player**. Existing certificate pin:
`79ed34c52c3e359756ade0634d7bbb6f8e94e42a059020c1220bd8c7092a9f5e`.

The new version-specific wrappers reuse the existing protected `supernova-foundation-signing` environment and private-key validation/cleanup implementation. They require exact-source complete tests, lint, binary/native/resource proof and unsigned payload hash before key access. Existing 0.148 signing rules are unmodified. No alternate key or unprotected signing path is used.

Actual signing attempt [41331247034](https://github.com/heretoecode/Supernova/actions/runs/41331247034) **failed before any steps**. GitHub explicitly rejected the branch because it is not allowed to deploy to `supernova-foundation-signing`. No private key was accessed or deployment protection changed. The owner must add only `codex/agreed-fixes-2026-11-01` to the existing permitted branches; [exact rejection and access record](SIGNING_ACCESS.json), [ready-candidate resume steps](SIGNING_RESUME.md). Local secret metadata listing returned HTTP 403; this does not prove secrets are missing. The complete existing Drive 0.148 APK was independently downloaded and verified with official Android build-tools 36: the pinned certificate, package, version 148, and published SHA-256 match ([baseline receipt](BASELINE_APK_READBACK.json)).

The verified **unsigned, non-installable** candidate is [GitHub artifact 13093101711](https://github.com/heretoecode/Supernova/actions/runs/41328625056/artifacts/13093101711), file `Supernova_0.149_unsigned.apk`, **95,398,440 bytes**, SHA-256 `ec453ae4a9cc4e9d34d4668270c8a243d52163d746741c70f862c24c0370437f`. [Independent APK verification](INDEPENDENT_UNSIGNED_APK_VERIFICATION.json) confirms manifest, ZIP integrity, 4-byte/16 KB alignment, four ABIs, all native hashes, 64 offline licence entries and retained flag/provider resources; signature verification correctly rejects it. The exact-source pre-key conformance gate passes locally.

Drive discovery/readback succeeded: existing APK folder `1mfHrFgLdJJSJYtTPys1N4Y-_KuE_84mM` still contains 16 retained entries and no 0.149 APK. **No signed APK, signed checksum, Drive upload or 0.149 Drive download verification is claimed.** No file, sharing permission or folder organization changed. [Actual Drive status](DRIVE_STATUS.json). Delivery remains unfinished until the existing environment permits signing; then verify and upload the signed candidate and download its complete bytes to recheck SHA-256, manifest and signature.

[Physical Shield acceptance checklist](SHIELD_QA.md) records the remaining device checks.

PR #30 remains draft and unmerged. No production release or tag.
