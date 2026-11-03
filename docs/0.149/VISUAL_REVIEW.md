# Approved Subtitles comparison — device verification pending

**Emulator/Shield visual parity: NOT VERIFIED.** No device or emulator is available in this workspace (`adb`, emulator and `/dev/kvm` absent). These are actual Android views rendered by Robolectric native graphics using synthetic fixtures, not photographs, screenshot mock-ups, live provider data or installed-APK evidence. They cannot establish GPU glow, overscan, decoder interaction or remote timing on Shield.

[Reference provenance](APPROVED_REFERENCES.json) pins both approved GitHub images and the entire blueprint. [Render provenance](software-renders/RENDER_PROVENANCE.json) identifies exact test source, workflow, runtime, dimensions and file hashes. The comparisons independently fit each panel for inspection and do not assert pixel parity with generated reference artwork.

| Surface | Approved reference | Actual software render | Side-by-side |
|---|---|---|---|
| Main | Image B only | (image removed 2026-11; in git history: docs/0.149/software-renders/subtitles-main-1920x1080.png) | (image removed 2026-11; in git history: docs/0.149/software-renders/main-comparison.png) |
| Track | Image A right-hand top only | (image removed 2026-11; in git history: docs/0.149/software-renders/subtitles-track-1920x1080.png) | (image removed 2026-11; in git history: docs/0.149/software-renders/track-comparison.png) |
| Download | Image A right-hand bottom only | (image removed 2026-11; in git history: docs/0.149/software-renders/subtitles-download-1920x1080.png) | (image removed 2026-11; in git history: docs/0.149/software-renders/download-comparison.png) |

The historical (image removed 2026-11; in git history: docs/0.149/software-renders/before-playback-subtitles.png) is from the preserved ten-fix source, with its exact provenance recorded. Before popup device captures are unavailable; no replacement is fabricated. Actual before/after captures of all three surfaces on Shield or a 1920×1080 emulator remain pending.

## Inspection and differences

- Main uses one outer border, two unboxed headings and ten ordered rows with separators. At the default 1920×1080 context all ten rows fit completely; title/tabs remain outside the content scroll view. Inline choices use plain text/separators, selected blue values, and no choice-pill backgrounds. Toggles distinguish value from full-row focus; five swatches retain a visible selected ring.
- Header/row icons use retained Supernova marks or thin semantic Canvas marks. Existing sans-serif font metrics, rounded toggle drawing and software blur differ from the generated artwork. No new font or stock video background is packaged. GPU shadow/glow and final TV text appearance require actual-device comparison.
- Track and Download are independent dialogs with real model callbacks. Fixtures deliberately contain more entries than fit, showing overflow/scrollbars; the Track capture separates focused and selected rows to demonstrate the independent tick. Real titles, regional/neutral icons and selected state take precedence over illustrative sample rows.
- Download uses two-line filename/metadata rows and end ellipsis. **Size unavailable** replaces reference MB values because the provider model lacks a reliable byte size. The fixture's long filename demonstrates truncation; it is not production data.
- The approved 100 ms delay increment is implemented in the retained millisecond contract. Position labels apply within the retained renderer's bottom-third range. The search field filters already returned results. See [compatibility mappings](IMPLEMENTATION.md#compatibility-mappings-and-deviations).
- Audio/Video/More use their original compact shell, geometry and callbacks. Native-rendered regression captures are compared with the historical ten-fix captures separately; matching captures are evidence for those fixture views only.

Use the [Shield checklist](SHIELD_QA.md) for live track selection/rendering, provider download/association, persisted settings, key-repeat/Back timing and the ten preserved fixes. Software inspection does not close that acceptance.
