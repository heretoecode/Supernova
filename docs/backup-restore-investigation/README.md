# Supernova backup and restore investigation

Read-only implementation investigation; documentation deliverables only. Owner brief dated 2026-10-13; repository inspection performed across 2026-10-12–13 UTC. The incident's actual date remains unconfirmed. No app changes, APK, build, merge or new maintenance scope.

| Report | Content |
|---|---|
| [00 — Findings](00_EXECUTIVE_SUMMARY.md) | Direct answers, important gaps and evidence limits |
| [01 — Archive inventory](01_CURRENT_ARCHIVE_INVENTORY.md) | Every emitted/accepted entry, nested stores, tables and fields |
| [02 — Settings matrix](02_SETTINGS_AND_DEFAULTS_MATRIX.md) | XML options, dynamic choices, defaults, storage and restore |
| [03 — Identity and viewing data](03_MEDIA_IDENTITY_PATHS_AND_HISTORY.md) | Paths, manual corrections, membership, sources and history |
| [04 — SAF and performance](04_SAF_EXPORT_IMPORT_AND_PERFORMANCE.md) | Publication, temporary files, lifecycle and feasible improvements |
| [05 — Incident evidence](05_INCIDENT_TIMELINE_AND_EVIDENCE_PLAN.md) | 12:41 / 12:33 observations and diagnostic plan |
| [06 — Proposed controls](06_PROPOSED_GRANULAR_CONTROLS.md) | Individual category switches and recommended defaults |
| [07 — Compatibility and safety](07_RESTORE_COMPATIBILITY_AND_SAFETY_PLAN.md) | Format evolution, artwork compatibility and future test gates |
| [08 — Owner decisions](08_PRIORITIES_AND_OWNER_DECISIONS.md) | 0.139 candidates versus later, approval choices |
| [Evidence index](evidence/README.md) | Source catalogs, field/key matrices and validation record |

## Provenance and authority

Read first, in the requested order: [audit brief](https://github.com/heretoecode/Supernova/blob/c7bbc2dd6d9f0b75ad3cb726d0c632914b43277b/docs/project-decisions/2026-10-13-backup-restore-audit-brief.md), [consolidated handover](https://github.com/heretoecode/Supernova/blob/c7bbc2dd6d9f0b75ad3cb726d0c632914b43277b/docs/project-decisions/2026-10-13-supernova-0139-consolidated-handover-scope.md), [roadmap](https://github.com/heretoecode/Supernova/blob/c7bbc2dd6d9f0b75ad3cb726d0c632914b43277b/docs/project-decisions/CURRENT_ROADMAP_AND_DECISIONS.md), then the chronological Shield QA record. Latest overrides take precedence over older headings. Backup redesign stays parked for 0.139; the 41 INCLUDE / 7 EXCLUDE scope is unchanged.

Existing verified work reused: maintenance [05 Playback/Diagnostics](https://github.com/heretoecode/Supernova/blob/eac3e504490a92dd9f43252649264657e485982c/docs/maintenance-investigation/05_PLAYBACK_AND_DIAGNOSTICS.md), [07 Libraries/Metadata](https://github.com/heretoecode/Supernova/blob/eac3e504490a92dd9f43252649264657e485982c/docs/maintenance-investigation/07_LIBRARIES_AND_METADATA.md), [08 Settings/Backup/Data](https://github.com/heretoecode/Supernova/blob/eac3e504490a92dd9f43252649264657e485982c/docs/maintenance-investigation/08_SETTINGS_BACKUP_AND_DATA.md), [09 UI Atlas](https://github.com/heretoecode/Supernova/blob/eac3e504490a92dd9f43252649264657e485982c/docs/maintenance-investigation/09_UI_ATLAS_AND_NAVIGATION.md) and their XML/consumer, diagnostic-producer, source-reference and effective-overlay maps. This investigation adds the entry/field/key coverage, omission semantics, SAF details and granular design missing from those reports; it does not redo upstream maintenance or diagnose runtime incidents from static code.

- **Source-verified** means executable code at signed 0.138 source `b3504c2c4b1725f3070416d14b63e53ae1db3d74`; it does not certify the owner's installed device or archive.
- MediaLib pin `5758074049bc153ac4967b4ecd21886469270076` and FileCore pin `c4b760c55102c72d45f68b0d16999fc1f4f013a7` come from [nova-ci.xml:16](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/.github/build/nova-ci.xml#L16). The existing maintenance overlay reconstruction is reused. Backup-related schema definitions at these pins are unaffected by ordered overlays; source policy/discovery behaviours additionally cite the Supernova patch. See [overlay map](https://github.com/heretoecode/Supernova/blob/eac3e504490a92dd9f43252649264657e485982c/docs/maintenance-investigation/evidence/effective-component-overlays.tsv) and [supernova-0138-qa.yml:63](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/.github/workflows/supernova-0138-qa.yml#L63).
- **Owner-observed** records the owner's report without independent device verification. **Inferred** is a source-based risk/hypothesis. **Proposed** requires future owner approval and implementation.
- Docs base `c7bbc2dd6d9f0b75ad3cb726d0c632914b43277b` contains older application code than signed 0.138. A separate detached worktree of `b3504c2c4b1725f3070416d14b63e53ae1db3d74` was read, without editing it. Do not infer installed behaviour from the docs branch's inherited source. Links pin the actual inspected source.
- No personal backup ZIP, diagnostic ZIP, device exit records, private DB or credentials were supplied or opened. No personal archive listing, bytes, restoration, timing or crash cause can be certified. Source-shaped catalogs below are exhaustive for the known writer/schema, not a measured listing of the reported 16.87 MB archive. Historic/custom database objects and directory filenames are runtime-dependent.

The owner has already decided: **no artwork in future exports, no artwork switch; optional media identification including necessary paths; privacy warnings; one switch per independently safe category**. Reports 06–08 propose defaults and implementation boundaries without reopening those decisions.
