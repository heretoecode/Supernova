# 01 — Source baseline and Git history

## Preconditions and immutable references

**VERIFIED, high confidence:** signed Shield QA delivery is documented in PR #13 and the 0.138 QA branch, with signed artifact 11744230421 from successful workflow 38239064325. This establishes delivery evidence, not hardware installation or acceptance. No APK was built or published by this investigation.

| Reference | Start SHA / state |
|---|---|
| main | `75d9f012fa4f65869eef5d972ad4725df504bd9c` |
| 0.137 QA | `45521c370563c8c176b0c3d9435fbf08fcd115e0` |
| 0.138 QA / PR #13 head | `16a60cb11a5dd20ebe374873db360e32b617d7b0` |
| PR #13 | OPEN, DRAFT, UNMERGED; base 0.137 QA |
| Signed 0.138 source / investigated source | `b3504c2c4b1725f3070416d14b63e53ae1db3d74` |
| Existing upstream documentation base | `0a30dbf8f91fd652812677bc4472565f390ab24a` |

The difference between signed source and 0.138 branch head is six files under `docs/0.138/`, including signed receipts. No application-source difference was found. The APK source SHA must remain distinct from the receipt commit.

**VERIFIED:** GitHub identifies Supernova as a fork of `nova-video-player/aos-Video`. Full Git clones, not shallow downloads, were used. Video history is available back to the original open-source commit `66b9788c`; current documentation-base history has 4077 commits. The current source and upstream default-branch common ancestor is `f53a1fc055d1d550a2edad4992bf82f9d12a7872`. The first Supernova-only commit is `636c75c5731c91c1a4a5b972fc943abe5a055e1d`, directly parented by that ancestor. The upstream manifest Video pin `0651a3e0...` is a later upstream descendant; it is NOT the fork's original divergence commit.

## Checkout and branch protection

The workspace initially contained no application checkout. An independent clone at `/workspace/supernova-maintenance` holds the docs branch. A detached worktree at `/workspace/supernova-audit-source` holds the frozen APK source. Independent upstream clones hold component history. No source checkout was edited. Dependency overlays were reconstructed in memory with exact old-context matching for every hunk, not applied to application checkouts.

`docs/maintenance-investigation-2026-10` did not exist in the initial remote inventory. It starts from `docs/nova-upstream-deep-audit-2026-10-11`, preserving previous research unchanged and permitting a documentation-only draft PR against that documentation branch. Basing the PR on main would unnecessarily include earlier research; basing on QA would mix unrelated source histories. Protected refs were read, never updated.

## Requirements read and interpretation

At existing documentation base `0a30dbf8...`, read all three owner planning documents:

- [Maintenance milestone](https://github.com/heretoecode/Supernova/blob/0a30dbf8f91fd652812677bc4472565f390ab24a/docs/project/FUTURE_MAINTENANCE_MILESTONE_CODEBASE_UI_ATLAS_DESIGN_SYSTEM_2026-10-12.md)
- [Diagnostics baseline](https://github.com/heretoecode/Supernova/blob/0a30dbf8f91fd652812677bc4472565f390ab24a/docs/project/FUTURE_RELEASE_DIAGNOSTICS_PRIVACY_SAFE_PERFORMANCE_BASELINE_2026-10-12.md)
- [Independent review brief](https://github.com/heretoecode/Supernova/blob/0a30dbf8f91fd652812677bc4472565f390ab24a/docs/project/CLAUDE_INITIAL_INDEPENDENT_REVIEW_BRIEF_MAINTENANCE_AND_DIAGNOSTICS_2026-10-12.md)

Also read the initial compatibility audit, phase-2 source audit, verification addendum, parked upstream handover, integration matrix, 0.137 diagnostic reminder and 0.138 BUILD_QA, SHIELD_QA, CONFORMANCE and application-render provenance/index. Earlier proposals and release-note leads are not treated as feature proof. The old FFmpeg mismatch hypothesis is rechecked against the newer signed build's lock and rebuild receipts.

No AGENTS.md exists in the inspected source tree. No independent Claude consultation or subagent review occurred.

## Reproduction and evidence

See [start repository snapshot](evidence/repository-state-start.json), [fork metadata](evidence/github-fork-relationship.json), [component ancestry](evidence/component-comparisons.json), [effective overlays](evidence/effective-component-overlays.tsv), and [source inventory](evidence/video-tree-inventory.tsv).

Executed checks: `git merge-base`, `git rev-list`, `git diff`, Git object inventories, API release/fork reads and exact-context in-memory dependency reconstruction. Android unit tests, native decoder tests and Shield measurements were NOT rerun here. Previously recorded QA results remain evidence attributed to their original receipts.
