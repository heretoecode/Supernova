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

Focused subtitle validation is complete; the whole-application suite, lint and APK build remain in progress. No signed delivery or device parity is claimed.

- [Focused run 41328625028](https://github.com/heretoecode/Supernova/actions/runs/41328625028) passes **33 application tests**, zero failures/errors/skips, release Java/resource compilation and **147 tooling tests**. Its six selected classes are not the full application/library suite; no lint or APK is produced by that focused job. Downloaded XML, archive hash and class counts are in [FOCUSED_VALIDATION.json](FOCUSED_VALIDATION.json).
- Local tooling: 147 tests pass, including current-source/payload/version/certificate and pre-key-access rejection boundaries.
- First combined run identified a reentrant presentation-focus transition and queued popup dismissal consuming rapid Back twice. Both paths were corrected and covered by regressions. The inline-control fixture now sends keys to an actually focused row.
- Source scope checks preserve the ten fixes, manifest/package identity, original native inputs and original signing helpers.
- Actual native-graphics Android view renders use labelled synthetic fixtures. They are **software renders, not emulator/Shield screenshots or live provider evidence**. [Final side-by-side comparisons](VISUAL_REVIEW.md) and exact [render source/hash provenance](software-renders/RENDER_PROVENANCE.json) are recorded separately. [Audio/Video/More PNG comparisons](LEGACY_TAB_RENDER_COMPARISON.json) are byte-identical to the preserved ten-fix fixtures.
- This cloud workspace has no `adb`, Android emulator or `/dev/kvm`. A final signed install is required before signed-APK emulator/Shield verification can proceed. Physical Shield acceptance remains **NOT RUN**.

## Signing and delivery

Version **0.149 / code 149**, package **app.supernova.player**. Existing certificate pin:
`79ed34c52c3e359756ade0634d7bbb6f8e94e42a059020c1220bd8c7092a9f5e`.

The new version-specific wrappers reuse the existing protected `supernova-foundation-signing` environment and private-key validation/cleanup implementation. They require exact-source complete tests, lint, binary/native/resource proof and unsigned payload hash before key access. Existing 0.148 signing rules are unmodified. No alternate key or unprotected signing path is used.

Current access issue: the environment's allowed deployment branches omit `codex/agreed-fixes-2026-11-01`. The owner has been asked to add only that branch while preserving other rules. Signing readiness is not established by the prior 0.148 delivery; a real protected signing attempt/readback is required for this candidate. Local secret metadata listing returned HTTP 403; this does not prove secrets are missing. The complete existing Drive 0.148 APK was independently downloaded and verified with official Android build-tools 36: the pinned certificate, package, version 148, and published SHA-256 match ([baseline receipt](BASELINE_APK_READBACK.json)).

Drive discovery succeeded: existing APK folder `1mfHrFgLdJJSJYtTPys1N4Y-_KuE_84mM` contains 16 retained entries and no 0.149 APK. No file, sharing permission or folder organization has been changed. Upload only a cryptographically verified signed candidate, then download the complete uploaded bytes and compare SHA-256, manifest and signature again.

[Physical Shield acceptance checklist](SHIELD_QA.md) records the remaining device checks.

PR #30 remains draft and unmerged. No production release or tag.
