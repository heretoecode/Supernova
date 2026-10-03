# Preview 4.1.7 conformance review — in progress

Authority: 26 September handover. This record supplements, not replaces, the 115-entry REQUIREMENTS.md register. No review pass is declared complete.

## Pass 1: source and requirement tracing

Post-audit correction: UI-052's frequency chooser already uses PreviewDialog.choose, which obtains the current opener and places the window using PreviewMenuPlacement. The audit's missing-anchor claim was not supported after tracing that helper. Checkpoint 52 adds an actual-window regression; no replacement UI was necessary. Other scan/presentation and physical checks remain open.

In progress. Recent reconciliation covers early global/Home/library requirements and the twelve put.io entries. Diagnostics coverage is being traced. Pending register entries are not completion claims; earlier implementation checkpoints and test results must be reconciled individually.

### Checkpoints 89–95 source reconciliation

- All 115 unique register paragraphs still match their named authoritative handover documents after status/evidence updates; this checks scope-text integrity, not implementation completion.
- Search retained-surface Details entry/return correlation passed CI 37113656058. Network item/action semantics passed CI 37120075931. Actual Network category UP routing passed CI 37120377842 after correcting the page-wide top-edge predicate.
- Rebuild reasons and stronger actual Details hero edge/DOWN assertions passed CI 37120690556: compilation/identity, 137 targeted, all 391 Video, 92 selected regression and 17 WebDAV tests (overlapping sets).
- UI-017/025/032/047/048/052 and DIA-010/011/012 have been reconciled with inspected behaviour and successful evidence. Physical/native/live limitations remain recorded per requirement; these are not blanket completion declarations.
- Further defects found: chooser artwork lacked rounded inner containment; selecting Network did not explicitly enter Overview. Corrections at checkpoints 93–94 are awaiting CI. No conformance pass is complete while these and the remaining register reconciliation are outstanding.

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

### Checkpoint 69 rendered-evidence reinspection

Source: remote `4c8234d8f805dcb3a377cc6e7624425169bb1189`, successful CI 36717304246, artifact 11096828194. This is an intermediate source tree, not the final candidate.

- `movie-details.png`: corrected hero now leaves lower content visible below the existing divider (Cast heading teaser). Canonical top navigation, left title/actions and right artwork region are visible. Synthetic artwork cannot establish real-logo/crop conformance.
- `search-empty-next.png`: one Search heading, required placeholder, number/QWERTY/staggered rows and Clear/Space/Backspace are visible; T is initially focused. Populated-results return/routing and real Shield appearance remain separate checks.
- `details-information-next.png`: rejected as adequate panel evidence. It captured the transparent page without its shell and did not actually expose the information panels. The fixture now requests the real Key Information panel, advances scrolling/layout, asserts panel visibility and captures the shell. Its corrected render must be inspected after CI; no panel conformance is inferred from the old image.
- `episode-details.png`: metadata assertions are useful, but the shell-less image is insufficient for whole-screen visual acceptance.

Pass 2 remains incomplete.

### Checkpoint 74 corrected Details evidence

CI 36787091119, remote d500fe78d27753baf177be6ceb095b04ad4393d7, artifact 11130596438 passed compilation/identity, 130 targeted, 374 complete Video, 87 selected regression and 17 WebDAV tests (overlapping sets).

- `details-information-next.png` now shows its actual subject inside the complete navigation shell: Key Information on the left, populated Reception in the middle, Technical Information on the right. The focused panel boundary, compact title and complete visible cast cards are inspectable. This resolves the earlier inadequate screenshot, not the full Details acceptance checklist.
- `details-compact-title.png` shows the remote fixture's compact title and Key/Streaming two-panel reflow with absent Reception/Technical content. Synthetic/absent artwork and fixed scroll positions do not establish physical scrolling, real crop or Shield legibility.
- The later direct Speed return and match-save corrections are outside this validated tree and need another complete run. All three final conformance passes remain incomplete.

## Pass 3: regression, identity and signed delivery

Not complete. CI 36349549771: application/test compilation and targeted suite passed; identity audit passed; full Video suite 312 tests, 1 failure (shared provider browser focus); backend/WebDAV gates skipped. The focus fix and diagnostics checkpoint are submitted in remote ab0c9eaea404b1f39b7a88b742dd7dc774b9b8f1, CI 36349938513 pending.

CI 36350179013 subsequently passed every gate, including the shared-browser repair, diagnostics regressions and Details teaser assertion. This clears the earlier failures for that source tree, not later changes. The corrected Details image still needs reinspection.

No signed Preview 4.1.7 APK has been produced or validated by this continuation. Signing fingerprint, installed-data upgrade, smoke checks, final complete register reconciliation and physical Shield QA remain delivery gates. Only live production put.io OAuth configuration/account validation is an acknowledged external credential dependency; unfinished implementation/review work must not be labelled OAuth-blocked.
