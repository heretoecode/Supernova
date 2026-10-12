# Supernova maintenance investigation — 2026-10-12

**Read-only application audit; documentation-only delivery.** Frozen signed 0.138 source: `b3504c2c4b1725f3070416d14b63e53ae1db3d74`. Branch: `docs/maintenance-investigation-2026-10`, based on the existing upstream documentation branch. No implementation or release authority is implied. Recheck findings against final validated 0.139 before maintenance.

[Draft PR #15](https://github.com/heretoecode/Supernova/pull/15) · [Delivery provenance](evidence/delivery.json).

The package covers all major source areas. Static investigation and planning are delivered; specific runtime, every-option atlas, release-binary and portability/security work remains **INCOMPLETE**, identified precisely in report15. Do not treat the presence of reports as runtime acceptance.

Evidence vocabulary: **VERIFIED** directly supported by source/Git/configuration or a specifically named executed check; **OBSERVED** available runtime/user report with provenance; **INFERRED** reasoned interpretation needing confirmation; **UNKNOWN** insufficient evidence; **PROPOSED** future action. “Verified static” does not mean tested on Shield. Previous QA results are attributed to original receipts.

## Master report index

- [00 — Executive summary](00_EXECUTIVE_SUMMARY.md)
- [01 — Source baseline and Git history](01_SOURCE_BASELINE_AND_GIT_HISTORY.md)
- [02 — Codebase architecture and ownership](02_CODEBASE_ARCHITECTURE.md)
- [03 — Upstream NOVA divergence and comparison method](03_UPSTREAM_NOVA_DIVERGENCE.md)
- [04 — Upstream change ledger](04_UPSTREAM_CHANGE_LEDGER.md)
- [05 — Playback, diagnostics and the smallest useful expansion](05_PLAYBACK_AND_DIAGNOSTICS.md)
- [06 — Network & Files, discovery and transport ownership](06_NETWORK_AND_FILES.md)
- [07 — Movies, TV, custom libraries, scanning, metadata and artwork](07_LIBRARIES_AND_METADATA.md)
- [08 — Settings, persistence, backup and migration safety](08_SETTINGS_BACKUP_AND_DATA.md)
- [09 — Source UI atlas and navigation contracts](09_UI_ATLAS_AND_NAVIGATION.md)
- [10 — Existing shared components and proposed design system](10_DESIGN_SYSTEM_AND_SHARED_COMPONENTS.md)
- [11 — Dependencies, security, native compatibility and licensing](11_DEPENDENCIES_SECURITY_AND_LICENSING.md)
- [12 — Cleanup and optimization register](12_CLEANUP_AND_OPTIMISATION_REGISTER.md)
- [13 — Reproducible regression and benchmark plan](13_REGRESSION_AND_BENCHMARK_PLAN.md)
- [14 — Proposed maintenance implementation roadmap](14_MAINTENANCE_IMPLEMENTATION_ROADMAP.md)
- [15 — Open questions, limitations and incomplete work](15_OPEN_QUESTIONS_AND_UNVERIFIED_ITEMS.md)

## Evidence and reproducibility

Start with [evidence index](evidence/README.md), [source references](evidence/source-references.json), [219-commit inventory](evidence/upstream-commit-inventory.tsv), [51-group ledger](evidence/upstream-selected-ledger.json), [three-way paths](evidence/three-way-file-ownership.tsv), [effective overlays](evidence/effective-component-overlays.tsv), [native histories](evidence/native-manifest-and-history.json), [UI consumers/navigation](evidence/shared-component-consumers.tsv), and [advisories](evidence/dependency-advisory-details.json). All evidence files are linked in the evidence index.

No private media, credentials, device databases/logs/recordings, APKs or signing material are included. No Android build/application/device benchmark was executed in this environment. Source reconstruction occurred only in memory; application checkouts were not patched. Documentation commits are incremental and the draft PR is not to be merged by this task.
