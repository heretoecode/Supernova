# Software render comparison

Controlled illustrative fixture rendered with Robolectric native software canvas. These are **not Shield captures**, real portraits, fetched ratings or production OMDb evidence. Existing production OMDb denial is unchanged. Source before: `48e9cd6f` (frozen application `b347707d`); after: `bd36ca73`. Same pinned/prepared dependency and SDK setup. Four baseline native tests pass; the candidate native tests are included in the 68-test passing run. Candidate source files were restored and byte-verified after baseline rendering.

The historical renderer writes `0140-reference-fixture-*` filenames; copies here are renamed before/after. Raw images and metrics are preserved with [hashes](VISUAL_EVIDENCE.json). The six regions remain; Language has no globe, Cast/Crew have a shared explicit row gap, and Read reviews sits beside Reception without taking a rating row.

| Viewport / text scale | Before | After | Portrait diameter before → after |
|---|---|---|---|
| 1280×720 / 1 | [Before](visuals/before/before-1280-1.0.png) | [After](visuals/after/after-1280-1.0.png) | 40 → 40 px |
| 1920×1080 / 1 | [Before](visuals/before/before-1920-1.0.png) | [After](visuals/after/after-1920-1.0.png) | 64 → 63 px |
| 1920×1080 / 1.5 | [Before](visuals/before/before-1920-1.5.png) | [After](visuals/after/after-1920-1.5.png) | 48 → 50 px |
| 3840×2160 / 1 | [Before](visuals/before/before-3840-1.0.png) | [After](visuals/after/after-3840-1.0.png) | 128 → 127 px |

Default-size portrait differences of one physical pixel reflect weighted-layout rounding; enlarged-text portraits retain the useful-size assertion. All original visibility, text-fit and non-clipping assertions remain enabled. See [physical Shield QA](SHIELD_QA.md) for real artwork, timing, review content and D-pad acceptance. No user recording reproduction or device performance gain is inferred from static renders.
