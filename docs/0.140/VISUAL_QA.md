# 0.140 visual verification register

Native Robolectric renders are software evidence, not NVIDIA Shield screenshots or final device acceptance. The implementation was calibrated against the recovered JPEG and approved written v3 corrections; final full-suite/native CI results are recorded separately.

## Authoritative reference and evidence

- [Exact owner-attached JPEG](references/FD-033-approved-goonies.jpg): received **1280×720**, 191,790 bytes, SHA-256 `8b7ce64f4ef1cb2608081ecbc20a15d1fef061ad54533b235f4e7eeb033b3a15`. Copied without resizing/recompression. Earlier planning describes a 1536×864 copy; this is the exact received attachment, not a claim to have recovered that master.
- [Provenance and reference interpretation](references/README.md). The separately annotated v3 PNG and FD-007 reference images remain missing; this JPEG does not replace them.
- [Side-by-side comparison](visual-evidence/FD033-reference-comparison.png) and [geometry overlay](visual-evidence/FD033-geometry-overlay.png). The overlay is a diagnostic comparison, not a pixel-pass metric: content, artwork, background and written margin overrides differ.
- Actual populated fixture renders and measured JSON: [720p](visual-evidence/0140-reference-fixture-1280-1.0.png), [1080p](visual-evidence/0140-reference-fixture-1920-1.0.png), [4K](visual-evidence/0140-reference-fixture-3840-1.0.png), [1080p at 150% text](visual-evidence/0140-reference-fixture-1920-1.5.png). Ratings/names in these fixtures come from illustrative reference content, not live API responses. Neutral silhouettes are intentional; no likenesses are fabricated.

## Conformance results and final measurements

| Requirement | Software result |
|---|---|
| Six fixed, read-only regions | Description, Key Information, Reception / Cast, Crew, Technical Information; all region bounds fit the same viewport. |
| Retained title / no app bar | Scrolled lower view retains the selected title/available official artwork. Application navigation disappears; Up restores hero/actions/navigation. The reference's Supernova wordmark is not copied as a movie logo. |
| Placement | Tab top = one sixth of viewport height (120 / 180 / 360px). Tab band = 5/72 of height (50 / 75 / 150px), with compact minimum. Left/right/bottom = 4%, as written; these intentionally differ from the raster's narrower margins. |
| Border | One compound tab/panel fill and perimeter, 1dp stroke and 4dp radius; white default and user accent override. No base line under active tab, duplicate outline or boxed inactive tab. Icon and label form a centred inline group; Details minimum width 14.3% of viewport. |
| Internal proportions | Upper/lower weights 56/44; upper columns 37.8/32.8/29.4, lower 34.4/35.3/30.3. Offset dividers preserved. Three Reception rules. |
| Typography and padding | At 960×540 logical reference: heading 15sp bold, facts 8–10sp autosize, description 11–14sp with 3dp added leading, score up to 16sp with smaller unit/support. Region padding 6×4dp, panel 10×8dp. Lower-panel metrics scale by the available logical reference ratio, including compact 720p. Long descriptions reduce then ellipsize; long metadata/path use bounded ellipsis. |
| Portraits | 4×2 each, up to eight cast and eight distinct crew. Circular clipping works on software and hardware paths. Nominal 34 reference dp; reduced to available cell height after actual caption ascent/descent. Name/role 9 reference sp with measured caption height. Useful portrait size and whole caption bounds verified at 720p, 1080p, 4K and 150% text. |
| Reception | Stable four slots. Score and vote captions measured separately to prevent clipping; supplementary attribution visible once. Missing values remain “No rating available” / “Unavailable”. OMDb production access denied. |
| Technical | Seven aligned rows; video/audio aggregates preserve available track details. Single-line middle-ellipsized safe path. Series unknown sizes remain lower bounds; averages appear only when complete. |

Scale is `min(screenWidthDp/960, screenHeightDp/540)` with a lower unsupported-viewport guard; Android density and user font scale still apply. Geometry JSON records actual measured frame/region rectangles, font pixels and portrait diameter. At 720p a populated fixture exposed portrait shrinkage and Reception clipping; responsive metrics, actual caption measurement, separate score/support blocks and regression checks corrected them before final validation.

## Explicit visual differences / acceptance still open

- Existing bundled IMDb wordmark has a different aspect ratio from the reference's square mark; it is preserved without distortion. Awards use a generic gold trophy. Rotten Tomatoes and Metacritic use text labels pending approved/provider-permitted artwork. No new logo rights are inferred from a screenshot or OMDb access.
- Existing source-labelled indexed/TMDb ratings and votes remain visible, as required by the approved content-retention decision. This adds a compact line absent from the illustrative mock-up. Supplementary attribution is explicit.
- The actual approved Supernova page background is retained. The mock-up's illustrative nebula, faces, scores, money/date formats and “Remux” sample do not replace actual metadata or project assets. Technical Source reflects the actual storage transport; missing Added remains Unknown.
- Real portraits, provider app launch, overscan, hardware font rendering and Shield remote/decoder behaviour still need device QA. No pixel-perfect or physical acceptance is claimed. The annotated v3 and FD-007 image comparisons remain blocked by missing assets.

## Details / Extras corrections

Genre fallback from indexed metadata; redundant resolution removed from human metadata; canonical Supernova background; single compound border; vertically balanced hero synopsis. Extras use the actual shared Home CONTINUE card renderer, horizontal rails, 172dp width with 97dp 16:9 artwork and 125dp rail height. Focus reveals headings and resets to the active tab; Up/Down remember cards between rails. Provider buttons align with measured Play height, have at least 44dp targets and use the existing functional callback. A callback fixture does not establish a real external provider launch.

## Available test boundary

Native viewport/focus fixtures and enlarged text checks are available. No emulator or physical device is connected (`adb devices` empty; no KVM device). No instrumentation/device playback, signed upgrade, physical Shield screenshot or real Put.io account round trip is claimed.

## Library and navigation review

The original FD-007 raster is still unavailable. A current 720p native render exposed clipped Library Status text, early label truncation and offscreen navigation icons painting into the clock area. Metric labels, values and captions now auto-size against real cell widths without horizontal scrolling; fixed line boxes use measured maximum font metrics. The navigation scroll viewport clips overflow and retains focus-driven reveal. Regression checks require all normal-size metric text to fit without ellipsis at 720p/1080p/4K, and compact focused navigation actions to remain whole and clear of the clock. The focused 19-test group passes, including a native pixel comparison of the clock with the navigation rail visible/hidden. That pixel check failed before the parent drawing boundary correction; geometry checks alone had missed paint overflow. Fresh normal-text renders show complete metric labels, values and captions. Final full-suite results follow in release evidence. This does not claim reference-asset or device acceptance.

## Additional saved native captures

[Statistics 720p](visual-evidence/0140-library-statistics-1280.png), [1080p](visual-evidence/0140-library-statistics-1920.png), [4K](visual-evidence/0140-library-statistics-3840.png). [Backup main page 720p](visual-evidence/0140-backup-1280.png), [1080p](visual-evidence/0140-backup-1920.png), [4K](visual-evidence/0140-backup-3840.png). [Extras focused card 720p](visual-evidence/0140-extras-card-1280-1.0.png), [4K](visual-evidence/0140-extras-card-3840-1.0.png), [tab/heading 1080p](visual-evidence/0140-extras-tab-1920-1.0.png), [150% text](visual-evidence/0140-extras-tab-1920-1.5.png). The trailer image is an intentional offline placeholder; aspect, caption, focus outline and heading come from the actual shared renderer.

[Image dimensions/checksums and source provenance](visual-evidence/INDEX.json). The AppCompat portrait compatibility fix preserves all four populated reference PNGs byte-for-byte; it resolves lint without changing their visual output. [Local validation receipt](LOCAL_VALIDATION.json). All captures remain software evidence.
