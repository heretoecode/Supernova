# Supernova handover investigation — 27 October 2026

Documentation-only continuation of [work queue](../../design/CODEX_WORK_QUEUE_2026-10-27.md). No application fix, Settings implementation, APK, signing change, release, main merge or workflow automation is included.

## State established before edits

Workspace `/workspace` initially contained no checkout. Cloned `heretoecode/Supernova`; initial `main` working tree was clean, no AGENTS.md found. All remote branches were enumerated and recent/open PRs inspected through GitHub. Current main `75d9f012fa4f65869eef5d972ad4725df504bd9c` is older than the development line. Latest development is `codex/supernova-0.142-implementation-2026-10-23`, tip `48e9cd6fbff47c120c013d5017f30cb3f3219030`. PR #20 is open/draft/unmerged, based on the 0.141 branch; GitHub readback confirms exact head. Its claimed signed QA delivery and 73 pending physical checks are historical release evidence, not rerun here. Recent PRs #19/#18/#17 preserve the preceding 0.141/0.140/0.139 implementation history. Open documentation/automation PRs are separate work; none were modified.

Authoritative handover branch `docs/settings-from-scratch-blueprint-2026-10-26` was at `e509ff083a109d82961059a5ea96d766546f97ba`. New isolated branch `docs/handover-investigation-2026-10-27` starts there, preserving its complete blueprint/audit. Its app tree matches frozen 0.142 source `b347707defdbf2ab3ae83d920ba9fa7af3936220`; confirmed no differences in `src`, `res`, `assets`. The design branch predates later delivery/workflow/tooling evidence at 0.142 tip, so do not merge its old release files over development. A future authorised implementation must branch from then-current 0.142 development tip (or its successor), carrying only the relevant specifications. No new implementation/release branch is needed now.

Google Drive was searched via the Google Drive skill. The existing Supernova folder and Development.html (updated 24 October) plus older project/audit documents were found. No newer Settings authority appeared in that result; the supplied repository handover controls this task. No Drive files or sharing were changed.

## Reviewable deliverables

- [Details investigation](DETAILS_INVESTIGATION.md): exact globe/grid sources, ratings-vs-reviews finding, async/artwork instability mechanisms, proposed corrections, instrumentation, regression and Shield cases.
- [Settings technical specification](SETTINGS_TECHNICAL_SPEC.md): reusable model/view contract, components, persistence/listeners, dependencies, exceptional flows, known issues, implementation order and acceptance matrix.
- [165-row disposition ledger](SETTINGS_DISPOSITION.md) and [machine-readable evidence](SETTINGS_VERIFICATION.json): original audit fields preserved with destination, disposition, declaration/default/reference checks and runtime test status.
- [Asset manifest](ASSET_MANIFEST.md): canonical background/resource equality and hashes, real typography/palette/icon sources, exact icon gaps and missing capture evidence.
- [Reproducible verification](verify.py): offline, no build. `python docs/investigations/handover-2026-10-27/verify.py` regenerates both ledgers.

## Verified versus outstanding

Verified: all 165 IDs/keys map exactly once; XML declarations and declared defaults resolve (including pinned MediaLib NFO key/bool); all audit consumer reference paths/line bounds exist; all 26 backup enum defaults/constructed keys match; five official/package branding hashes match historic export evidence; app tree unchanged. Explicit disposition covers 23 non-exposed definitions and Network & Files ownership. Controller contracts and F01/F02 source behaviours were reviewed; no full runtime certification is implied by a matching literal/reference.

Evidence dependency: `evidence/medialib-nfo_import_export.xml` is a byte-for-byte read of `heretoecode/aos-MediaLib` commit `5758074049bc153ac4967b4ecd21886469270076`, path `res/values/nfo_import_export.xml`, pinned in `.github/build/nova-ci.xml`. Inspection clone `/workspace/MediaLib-inspect` is separate, no changes to app dependencies. Broader native/FileCore behaviour is preserved from owners and not rebuilt here. All audit reference/default checks are source-level; live language/provider catalogues, account access, per-device option gates and actual saved user state remain runtime inputs.

Outstanding: device reproduction/timings/captures and before/after fixes, written-review endpoint/terms validation and UX scope approval, matching node/gear assets during authorised implementation, unavailable original mock-up attachments and physical dimension/font/contrast calibration. No Android/Shield tests were run. There is no adb/physical Shield attached or prepared Android sibling build environment. Documentation verification is not release conformance.

Ready for implementation planning: targeted globe/spacing changes and state/artwork correction design; complete Settings mapping/disposition, architecture and exceptional-flow/test requirements. Settings is **not yet authorised for implementation**; exact provisional placements remain a specification candidate under the master blueprint. Details source fixes likewise remain prepared under this documentation-only handover.

## Genuine decisions when implementation is commissioned

No routine engineering questions are pending. The only substantive product-scope decision identified is the written-review proposal: TMDb-backed read-only review reader launched from Reception, subject to endpoint/terms validation, versus retaining ratings until a review design is approved. This affects an intentionally read-only six-region panel. Do not add the action silently. Explicit application/Settings implementation go-ahead is required by the [work queue](../../design/CODEX_WORK_QUEUE_2026-10-27.md): “Do not assume priority order is permission to start either implementation.” This report does not request a release or signing authorisation.
