# Details tab forensic investigation — signed Supernova 0.138

Date: 2026-10-13. **Documentation only. Current behavior is reconstructed from immutable source, not tested on a Shield.**

## Result

The implemented lower Details view has five parallel sections when data permits: Cast, Crew, Key Information, Reception and Technical Information. Cast/Crew each contain at most eight single-line people records. Fact labels and values are single-line, end-ellipsized. Missing values usually remove their row. TV shows replace selected-file technical facts with indexed episode/season/storage aggregates; streaming-only titles replace Technical Information with Streaming Availability.

The information itself does not accept D-pad focus. However, `PreviewMoviePage` is a **page-level ScrollView**, and Down/OK on Details explicitly moves the page to its lower content. The current source therefore does **not** implement the owner's fixed-screen, no-scroll contract. Synopsis is in the upper card, ellipsized to one, two or four lines according to viewport/card size. **There is no synopsis field in the lower Details tab.** A private reader method has no call site and is not evidence of accessible full synopsis.

Current TV order is **Seasons & Episodes → Details → Extras (conditional) → More Like This (conditional)**. Tabs have no icons. The selected underline remains at reduced opacity after tab focus leaves. The approved future order, passive canvas, connected active-tab cap and 4% frame margins must be treated as future requirements.

New static concerns: Versions is created/configured but never added to the hierarchy; fresh season credits/videos fail the metadata gateway allowlist while their disk caches can be read; an Episode's completed technical-metadata task does not directly rebind the preview page, unlike Movie. These are source findings, not reproduced owner incidents. Detailed qualifications appear in [assembly](01-SOURCE-AND-ASSEMBLY.md) and [lifecycle/navigation](04-NAVIGATION-AND-LIFECYCLE.md).

## Read the investigation

| Deliverable | Contents |
|---|---|
| [01 — Source and assembly](01-SOURCE-AND-ASSEMBLY.md) | Entry routes, activity/fragment/view tree, IDs, models/provider/DB trace, omitted supported metadata and source quality |
| [02 — Complete field inventory](02-FIELD-INVENTORY.md) | Every lower field, exact spelling, value precedence, movie/show/episode/season/remote applicability, formatting, empty behavior and people ordering |
| [03 — Layout reconstruction](03-LAYOUT-RECONSTRUCTION.md) | dp/sp/color/weight specification, source formulas, annotated 1920×1080 diagrams, initial versus lower page, readability pressure |
| [04 — Navigation and lifecycle](04-NAVIGATION-AND-LIFECYCLE.md) | Current focus/scroll graph versus target, selection, file variants, asynchronous updates/cache/scan overlay |
| [05 — Edges, decisions and validation](05-EDGES-DECISIONS-VALIDATION.md) | Variation matrix, useful-content preservation, owner decisions, existing tests, proposed non-device acceptance and design handover |
| [Evidence ledger](evidence/SOURCE_LEDGER.md) | Immutable links, exact source excerpts and hashes; primary and pinned MediaLib sources |
| [Baseline receipt](evidence/baseline.json) | Ref SHAs, comparisons, reference provenance, scope limits |
| [Documentation validation](evidence/documentation-validation.json) | 88 exact excerpts,81 file hashes,44 distinct lower fact labels,290 immutable links,4 existing render hashes; no app tests |
| [Brief coverage](evidence/BRIEF_COVERAGE.md) | All ten audit questions and each required deliverable mapped to report/evidence |
| [1920×1080 annotated lower layout](evidence/current-lower-layout.svg) | **C: source-derived approximation**, no media imagery, fixture values, redesign or owner approval |

## Exact baseline and authority

| Reference | Inspected commit |
|---|---|
| Signed 0.138 application source, detached read-only worktree | `b3504c2c4b1725f3070416d14b63e53ae1db3d74` |
| 0.138 QA receipt head | `16a60cb11a5dd20ebe374873db360e32b617d7b0` |
| Investigation brief/decisions branch at intake; documentation branch base | `d75024fed0747453ea2cf774506beb66f22f9773` |
| Completed maintenance investigation reference | `eac3e504490a92dd9f43252649264657e485982c` |
| Codex UI Reference Atlas reference | `7b6413a641139efe4d355727980f12f9729d1bbc` |
| Main at intake | `75d9f012fa4f65869eef5d972ad4725df504bd9c` |
| Pinned native AVOS source for bitrate/fps | `2cf21c486c7bf244c49b78d2220197a6d75396dd` |
| Pinned sibling MediaLib source | `5758074049bc153ac4967b4ecd21886469270076` |

`git diff` between signed source and QA head yields only six files under `docs/0.138/`. Main is an older, different source tree, not the signed baseline: source/resources differ in 213 files. The documentation branch inherits the decisions branch's tree; **its application files are not a replacement baseline**. Every code citation points to signed source or the pinned sibling. No APK was fetched/built, no CI/test workflow invoked, no merge performed, no source/tests/assets/build/signing files changed. No AGENTS.md was found in the investigated repository tree. Work uses its own clone/worktree; the ongoing backup branch is only included in the immutable intake ref receipt and is not edited.

### Required decisions read

At the pinned decisions commit, read the [investigation brief](https://github.com/heretoecode/Supernova/blob/d75024fed0747453ea2cf774506beb66f22f9773/docs/project-decisions/2026-10-13-details-tab-forensic-source-and-ui-audit-brief.md), [consolidated 0.139 scope](https://github.com/heretoecode/Supernova/blob/d75024fed0747453ea2cf774506beb66f22f9773/docs/project-decisions/2026-10-13-supernova-0139-consolidated-handover-scope.md), [decision-gap review](https://github.com/heretoecode/Supernova/blob/d75024fed0747453ea2cf774506beb66f22f9773/docs/project-decisions/2026-10-13-preimplementation-decision-gap-review.md), [roadmap](https://github.com/heretoecode/Supernova/blob/d75024fed0747453ea2cf774506beb66f22f9773/docs/project-decisions/CURRENT_ROADMAP_AND_DECISIONS.md), [chronological Shield QA](https://github.com/heretoecode/Supernova/blob/d75024fed0747453ea2cf774506beb66f22f9773/docs/project-decisions/2026-10-12-supernova-0138-shield-qa-and-library-foundation.md), and [connected-tab frame decision](https://github.com/heretoecode/Supernova/blob/d75024fed0747453ea2cf774506beb66f22f9773/docs/project-decisions/2026-10-13-details-connected-tabs-panel-design-decision.md). Latest appended decisions supersede older proposals. The newer connected cap supersedes the earlier proposed underline/divider treatment where they conflict. This audit has no coding authority regardless of 0.139 release-scope approvals in those records.

### Prior evidence reused

[Maintenance master](https://github.com/heretoecode/Supernova/blob/eac3e504490a92dd9f43252649264657e485982c/docs/maintenance-investigation/README.md), especially 01 baseline, 02 ownership, 05 playback, 06 transports, 07 libraries/metadata, 08 storage/backup, 09 UI/navigation, 10 shared design, 13 regression and 15 limitations. Its scanner/native overlays and durable-storage findings are reused with attribution, not reinterpreted as runtime diagnosis.

[UI Atlas master](https://github.com/heretoecode/Supernova/blob/7b6413a641139efe4d355727980f12f9729d1bbc/docs/ui-reference-atlas/README.md), [Details area](https://github.com/heretoecode/Supernova/blob/7b6413a641139efe4d355727980f12f9729d1bbc/docs/ui-reference-atlas/areas/03-details.md), exact expression appendix, provenance and acceptance/gaps. Stable identifiers: **DET-001** movie/episode, **DET-002** TV/season/remote, **DET-003** tabs, **DET-004** facts, **DET-005–007** menus/mutations, **DET-008** extras, **DET-009** people/recommendations/files/episodes. These IDs identify surfaces, not executed navigation coverage. This audit adds precise lower-field conditions and geometry; it does not overwrite Atlas history.

## Evidence classification and limits

**S (source-verified):** directly read code/resources/Git objects. **B (existing native evidence):** committed Robolectric renders and original test fixtures, inspected visually here but not regenerated. **O (owner-reported):** photograph transcription and Shield observations in decisions. **I (inferred):** layout arithmetic/readability implications. **U (unknown):** device pixel geometry, OEM fonts/overscan/input, real metadata/file population, the installed binary's independently verified identity and incident causes. **T (target):** approved future direction, not existing behavior.

The brief transcribes `IMG_4941.jpeg`; its image bytes were **not supplied to this investigation**. No claim of inspecting that photograph, its pixels or private media is made. Repository 0.138 PNGs are synthetic Android captures, not that photograph. No Google Drive lookup/upload was needed: the authoritative Atlas and required references were available in Git, with their existing Drive links preserved in the Atlas. No private paths, logs, credentials or databases are published. Example source URIs in tests describe synthetic fixtures only.
