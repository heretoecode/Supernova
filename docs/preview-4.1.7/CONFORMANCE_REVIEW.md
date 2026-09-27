# Preview 4.1.7 conformance review — in progress

Authority: 26 September handover. This record supplements, not replaces, the 115-entry REQUIREMENTS.md register. No review pass is declared complete.

## Pass 1: source and requirement tracing

In progress. Recent reconciliation covers early global/Home/library requirements and the twelve put.io entries. Diagnostics coverage is being traced. Pending register entries are not completion claims; earlier implementation checkpoints and test results must be reconciled individually.

## Pass 2: rendered visual comparison

Initial inspection uses CI 36349549771, remote source e961c51ef85bd35b1ce8dde157c697c93fc03b33. That run has one full-suite browser-focus failure, so it is **not** an accepted candidate. Reference characteristics were checked against VISUAL_AUTHORITY.md and IMPLEMENTATION_SPEC.md, not copied from generated navigation.

| Fixture | Observation | Outcome |
| --- | --- | --- |
| movie-details.png vs Movie_Details_Dune_Reference.png | Canonical written top navigation is preserved instead of the generated reference's incorrect navigation. Hero/title/actions remain left, backdrop right. Lower divider reaches screen bottom and hides required teaser content. | Defect found: move hero sizing from layout-time callback to measurement; add 44dp teaser assertion. Correction awaits CI/render review. |
| page-1.png and library-list.png vs Movies_TV_Library_Header_Icon_Divider_Reference.png | Summary icons/counts, compact toolbar/divider, six grid columns and distinct List headers are visible. New Unmatched control is present as explicitly required. | Basic layout inspected; focussed glow, clipping, terminal edges and real artwork still need review. |
| page-0.png | Featured summary is left of backdrop; row caps/indicator structure visible. Fixture uses synthetic geometry artwork and no real title logo. | Cannot prove real-logo bounds, face crop or artwork quality from this fixture. These require a populated fixture or physical QA. |
| customise-home.png | Inline Shown/Hidden controls and bounded scroll viewport visible. | Does not exercise delete confirmation, genre footer or exact numeric keyboard. Those scenarios remain pending. |
| playback-hud.png | Five low controls, neutral remaining seekbar, played segment, time endpoints, title/episode and clock/end-time positions visible. | Basic composition inspected only. Fixture does not demonstrate focused-control glow, live seeking or physical playback. No ground-up redesign authorised. |

Synthetic test images are diagnostic fixtures, not replacement artwork and not evidence of physical Shield rendering. The generated reference's thicker underline and obsolete navigation are not specifications; written same-stroke divider and canonical navigation take priority.

## Pass 3: regression, identity and signed delivery

Not complete. CI 36349549771: application/test compilation and targeted suite passed; identity audit passed; full Video suite 312 tests, 1 failure (shared provider browser focus); backend/WebDAV gates skipped. The focus fix and diagnostics checkpoint are submitted in remote ab0c9eaea404b1f39b7a88b742dd7dc774b9b8f1, CI 36349938513 pending.

No signed Preview 4.1.7 APK has been produced or validated by this continuation. Signing fingerprint, installed-data upgrade, smoke checks, final complete register reconciliation and physical Shield QA remain delivery gates. Only live production put.io OAuth configuration/account validation is an acknowledged external credential dependency; unfinished implementation/review work must not be labelled OAuth-blocked.
