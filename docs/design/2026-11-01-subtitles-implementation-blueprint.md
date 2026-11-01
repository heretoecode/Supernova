# Supernova 0.148 — Subtitles Playback Settings Blueprint
**Status: Subtitles design approved in principle; NOT authorised for implementation.** Keep separate from active Codex fix batch.

## Source of truth and visual references
The user approved the latest simplified main Subtitles layout: one outer rounded floating Playback Settings window, **no inner section boxes**, section headings and fine row separators. The reference image is currently in the downloadable design handover package, **not yet verified in GitHub**. When uploaded, verify and link the exact image here.

The earlier composite image shows **Subtitle Track** and **Download Subtitles** pop-ups. Its older main-panel inner boxes are superseded by the final simplified image. The language-flag composite shows flag placement in track/download lists. Do not copy unrelated video/player controls or example filenames from mock-ups.

## Exact scope
Only **Subtitles** within one Playback Settings window. Horizontal tabs: **Subtitles → Audio → Video → More**, monochrome icon immediately left of each label. Do not redesign the other tabs or playback HUD.

## Main tab rows
### Subtitle Selection
1. **Subtitles:** direct On/Off toggle, first row.
2. **Subtitle Track:** current selected language/track, open track selection pop-up; selected mark, CC/SDH information, optional region flag and language text. Back closes pop-up.
3. **Download Subtitles:** invoke existing subtitle search/download workflow, not a new backend. Searchable results, download action, language text and appropriate flag/fallback. Back returns to settings.
### Subtitle Appearance
4. **Text Size:** inline text-only **Small | Medium | Large**, selected value blue; no pill buttons or submenu.
5. **Text Colour:** horizontal preset colour swatches, selected swatch highlighted. Exact swatch palette not yet finalised; preserve existing colour capabilities.
6. **Vertical Position:** inline text-only **Bottom | Middle | Top**, selected blue.
7. **Outline:** direct On/Off toggle.
8. **Background:** direct On/Off toggle.
9. **Background Opacity:** inline text-only **0% | 25% | 50% | 75% | 100%**, selected blue.
10. **Subtitle Delay:** inline minus / signed millisecond offset / plus; adjust **100 ms** per activation.

## Layout
- One dark floating rounded outer window, no nested section outlines. Thin separators, generous row heights, icons on left, controls on right.
- Blue active selection and a distinct, navigable D-pad focus state. Keep title and tabs visible while scrolling if necessary.
- Approx **40% screen width** was discussed, but latest concept imagery uses a larger panel; **responsive fit/actual final dimensions remain to be confirmed**. Do not sacrifice legibility or crowd the 0% opacity choice. Validate 1920×1080 on Shield.
- Android TV remote D-pad/Select/Back throughout; no extra X/back control on the main window.
- Flags supplement language names; do not infer country from language alone. Use neutral fallback when region unknown.
- Image sample values (e.g. English SDH, 50%) are examples, not defaults.

## Future Codex acceptance
- Before implementation, check existing 0.148 in-player behaviour against the functional audit and preserve all supported functionality, including sizing/position/opacity mappings.
- Screenshot actual implemented window and both pop-ups on device or emulator and compare to the **exact uploaded approved** references; correct discrepancies rather than claiming success from compilation.
- Functional tests: toggle, track choice, download, text size/colour, position, outline/background, opacity, +/-100ms delay, state persistence, focus and Back navigation.
- Physical Shield QA must be marked pending until actually performed.
- **Do not implement this blueprint as part of the current unrelated Codex fixes.** Wait for explicit user go-ahead and verified image assets.

## Asset upload
Upload approved reference PNGs to `docs/design/approved/` using clear names:
- `supernova-subtitles-main-approved.png`
- `supernova-subtitle-track-popup-reference.png`
- `supernova-subtitle-download-popup-reference.png`
Once uploaded, verify each exists and update this file with direct links. The pop-up mock-ups are reference proposals until individually confirmed.

## GitHub asset verification — 1 November 2026
Two PNG files have been verified as present in `docs/design/approved/` on `main`:
- [A31E82C8-CC81-4E6C-AC1D-6AE60370E152.png](https://github.com/heretoecode/Supernova/blob/main/docs/design/approved/A31E82C8-CC81-4E6C-AC1D-6AE60370E152.png) (1,561,700 bytes)
- [AA59180F-F8E0-4697-9423-78621273D28B.png](https://github.com/heretoecode/Supernova/blob/main/docs/design/approved/AA59180F-F8E0-4697-9423-78621273D28B.png) (1,472,382 bytes)

**Unresolved:** Three reference views were requested; only two images are present. Their visual contents have not been inspected through the GitHub connector (binary image access unavailable), so do not label either as main, track or download reference or rename them until verified. The reference set is **incomplete/unverified**, and Codex must not treat it as an implementation-ready approved set.
