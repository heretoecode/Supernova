# Preview 4.1.7 conformance review

Authority: 26 September handover. This record supplements, not replaces, the 115-entry REQUIREMENTS.md register. Current closeout: all three development reviews are complete with explicit physical/live limitations. FINAL artifact evidence is recorded in IMPLEMENTATION_REPORT.md. Dated earlier pending statements below are retained history, superseded only by the later evidence and delivery interpretation.

## 5 October continuation evidence update (not final acceptance)

- Current-source CI 37341405225 passed at 81efbf6d, including checkpoint 100 source-removal/About corrections and the follow-on provider, immutable pre-failure recorder and Info tests.
- Strict native run 37338324041 at fbcbc20d passed exact Info return before packaging and signed release upgrade/signature checks. The authorised earlier interim exception was not used. This supersedes the pending Info-fix statements below for that source tree, not physical Shield acceptance.
- Checkpoint 101 corrects worker-time post-failure routing and adds a delayed-writer regression. Its Android validation is pending. No final conformance pass is declared complete.

## 4 October interim evidence update (not final acceptance)

- Frozen interim build commit ff955d90d7a76d157776b07cfb116fd7c67ea924 has fresh successful source CI 37192141281: 137 targeted, all 391 Video, 92 regression and 17 WebDAV tests, with compilation and identity/log-safety gates passing. Test sets overlap.
- Re-inspected that run's `movie-details.png`, `details-information-next.png` and `playback-hud.png` against written requirements and VISUAL_AUTHORITY. The hero retains a lower Cast teaser; the scrolled Details fixture shows complete cast cards and Key/Reception/Technical panels; HUD composition retains five controls and time/seek elements. Synthetic artwork, absent HUD focus in the fixture, and static positions prevent claims about real crops, focus glow, continuous scrolling or physical rendering. This is limited visual evidence, not completion of Pass 2.
- Also re-inspected `customise-home.png`, `page-1.png`, `library-list.png` and `search-empty-next.png`: inline Shown/Hidden controls, six-column Grid/header/divider, List column headers, and the single Search heading with T-focused required keyboard are visible. Poster cyan frames/text belong to the synthetic geometry fixture, not actual artwork or focus styling. These captures do not prove focused-card enlargement, numeric keyboard/delete flows, populated Search return or physical repeat behaviour.
- Native emulator run 37156506482 exposed a real Back-from-Info HUD visibility/focus defect despite prior unit tests. UI-046 has been corrected from the stale physical-only status. Mark authorised delivery of the frozen interim with this known defect; the assertion remains active after this interim's APK upload and must pass before any final release. The local correction and new hidden-parent test are outside the interim snapshot and awaiting Android/native verification.
- DIA-011 pre-failure capture was also found vulnerable to delayed writer execution after ring rotation. Local immutable failure-time capture and new tests are awaiting Android execution; post-failure timing under queue delay still needs reconciliation. Do not infer completion from the older passing coverage.
- No final conformance pass is complete. Interim signing/build results do not waive remaining final-source or physical Shield acceptance.

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

## 5 October checkpoint-100 render review and continued source tracing

Source CI 37341405225 at 81efbf6d, artifact 11359475136. Twenty retained images were inspected against the written authority and selected references. This is intermediate evidence, not final validation.

- Home, Movies/TV six-column grid, List, Search keyboard, matching surface, compact Details, three information panels, populated cast and shared put.io browser show the intended basic composition. Synthetic posters and empty live data do not prove real artwork, provider completeness, all focused states or Shield readability.
- `playback-info.png` was rejected as current evidence: its render fixture invoked obsolete PreviewPlaybackInfo directly even though production PlayerActivity opens PreviewTechnicalInfo. The fixture now invokes the production technical overlay and asserts its four sections and excluded actions/private filename. The corrected image still needs CI and inspection. Existing production Info-return verification remains valid and independent.
- `page-3.png` shows the retained TV page, not a Network workspace, so it is not Network visual acceptance. `episode-details.png` lacks the full shell and remains inadequate whole-screen evidence. Network/episode visual acceptance is still open.
- Source tracing reconciled filters, episode identity/availability, Extras, recommendations, conditional Details facts, source-removal semantics, shared scan scheduling and rebuild/network diagnostics with existing successful tests. Detailed limitations are retained in REQUIREMENTS.md. Checkpoint 102 addresses the newly found Details logo-mask defect.

### Checkpoint 104 corrected surface evidence

CI 37350349198 at 798669e6 passed all source gates. Artifact 11362870687 was inspected: Network now shows Overview focus, five categories and independent Scan Library/Network Scanning items; episode Details is inside the canonical shell with episode-specific metadata and lower divider/teaser; active technical Info shows Video/Audio/File/Source without legacy actions or private filename. This resolves those three inadequate captures. Synthetic/absent artwork and empty decoder metadata do not establish physical rendering or full scenario coverage. Final conformance remains open as listed in CONTINUATION_2026-10-05.md.

## 5 October checkpoint 107 — register reconciliation and current gates

Reconciled 39 stale entries against source and existing evidence, including eleven aggregate QA entries and the visual/scope rules. All 115 original authority paragraphs are byte-for-byte unchanged. No entry remains an unexplained `PENDING IMPLEMENTATION REVIEW`; aggregate acceptance is explicitly in progress rather than converted to a pass. Earlier dated pending statements above remain historical records and are superseded only by cited later evidence.

New findings/corrections:
- Put.io sync lacked parent correlation for child transport and cancellation termination. Checkpoint 106 corrects this with three behavioural regressions; current-source Android CI must pass.
- Network's friendly protocol formatter omitted actual internal webdav/webdavs/smbj/sshj aliases; checkpoint 107 maps them to WebDAV HTTP/HTTPS, SMB and SFTP without changing connection implementations.
- Full APK mode already invokes lint through the assemble dependency configured in build.gradle. Checkpoint 107 additionally makes `lintNoamazonRelease` explicit, with its own retained log; this improves evidence visibility rather than correcting an absent lint dependency. Checkpoint 105 also replaces its selected full-mode unit subset with the complete Video suite. No existing strict playback, signing or upgrade gate is relaxed.

The source mapping inventory is now reconciled, but this does not close all three acceptance passes. The concrete remaining matrix is:

| Area | Remaining evidence/action |
| --- | --- |
| Home/customisation and focused visuals | Real logo/crop comparison; delete/genre/numeric chooser focused layouts; populated Search return; native child focus and retained artwork propagation scenarios. Existing synthetic images establish only their stated geometry. |
| Metadata correction | End-to-end version grouping and series/NFO failure behaviour. Provider-level tests preserve physical rows/history but do not prove every multi-version UI selection or external NFO side effect. |
| Current-source CI/native | Complete Video suite, source identity, WebDAV, explicit release lint, strict native Info return, signed release upgrade and certificate. Earlier native success cannot validate later source changes. |
| Physical Shield | Real remote/rapid focus and seeking; real library scan/startup/resume equivalence; artwork after scan/restart; installed 4.1.6 data preservation; launcher viewing-distance legibility; multi-day diagnostics. No attached Shield session or new physical report is available here. |
| Live put.io/provider | Production OAuth client/account configuration and real provider app/region handoff. OAuth is not a blocker for unrelated implementation or tests. |
| Final delivery | A separate FINAL APK only after acceptance is resolved, tied to the final source commit and pinned signing identity. Current full-build artefacts are validation candidates. |

## Delivery interpretation and final closeout — 5 October

The original 01_CODEX_PROMPT delivery gate requires build/tests, a signed and cryptographically verified APK, smoke/regression checks, release notes and an implementation report. PREFLIGHT explicitly keeps device checks AWAITING PHYSICAL QA. Therefore a final development APK may be delivered with those limitations; it does not constitute physical acceptance or permission to merge. Earlier wording in this review that treated every physical check as a pre-APK gate was too broad. Main remains outside this task.

Pass 1 source/completeness review: the 115-entry mapping is reconciled, with source/component coverage, external configuration and device-only evidence explicitly classified. Review findings were corrected centrally (including incident timing, provider logo reuse, sync correlation, Back routing and menu viewport clipping), rather than starting a replacement implementation. No unexplained pending implementation-review placeholders remain. Source conformance does not turn live provider, NFO or physical-library scenarios into proven tests.

Pass 2 visual/behavioural closeout and Pass 3 final signed regression acceptance are awaiting the checkpoint-111 results below. Release notes and the implementation/limitations report are prepared locally and will be committed with the final evidence.

### Checkpoint 111 visual and source acceptance

Source CI 37359092502 at 7efb4195639205a6526281491af40c0dab1776b7 passed compilation, identity, complete Video, selected regression and WebDAV gates. Artifact 11365583449 was inspected. Both initial and scrolled genre images now keep rows strictly in the middle viewport: header and Done footer remain unobscured; the focused footer boundary is contained. The new before/after header pixel test passed. Checkpoint-110 delete confirmation, exact-number keyboard, populated Search return and updated Details portrait/panel captures were already inspected and remain applicable outside the bounded viewport correction.

Pass 2 review is complete for available rendered/component evidence, with real artwork/crops, physical glow/readability and device/live scenarios explicitly retained as AWAITING PHYSICAL QA in the register/report. This is review completion with classified limitations, not a claim that every synthetic image proves physical conformance. Pass 3 remains open until current-source signed/native validation and final artifact verification succeed.

### Final signed regression and delivery acceptance

Pass 3 is complete. Full CI 37359092413 at 7efb4195639205a6526281491af40c0dab1776b7 passed every required step: full Video/regression/WebDAV, lint-enabled signed development packaging, emulator startup, strict native Info return before release packaging, optimised release, explicit release lint, signed upgrade/restart and exact pinned certificate verification. No interim exception was used. APK artifact 11367082061 was downloaded through the authorised file materialisation route; its archive digest and extracted APK SHA-256 matched recorded CI values. The final APK is 82,738,089 bytes, SHA-256 4070ee3d259b3cf57bb10a26fbc789363cf812e8b81fe7f3101522634705681a.

The artifact is accepted as the separate FINAL Preview 4.1.7 development APK. Release notes/report and classified physical/live limitations accompany it. Earlier dated incomplete statements are historical, not instructions to repeat closed development gates. This does not close physical Shield QA, supply missing production OAuth configuration, establish an unknown installer diagnosis, or authorise any main merge/Phase 1/next release.
