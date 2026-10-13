# Priorities, 0.139 candidates and owner decisions

**The approved 0.139 41 INCLUDE / 7 EXCLUDE scope is unchanged.** This investigation authorises documentation only. No recommendation below is a coding start, feature re-inclusion, APK/build, dependency migration, merge or unrelated maintenance approval.

## Priority and release boundary

| Priority | Finding / proposed work | 0.139 versus later | Approval and evidence gate |
|---|---|---|---|
| P0 investigation | 12:41 apparent exit / 12:33 notification inconsistency, related playback incidents | Existing walkthrough/approved diagnostics context; incident **not diagnosed** | One near-end privacy-reviewed diagnostic ZIP, exact installed version/time/provider/process evidence; owner selects any required fix |
| P1 data safety | Default-settings omissions, category absence wipes stores, schema-v62 fingerprint gap and complete recovery limitations | Broader backup/restore remains **later/parked**, unless explicit reliability exception selected | Synthetic preservation/rollback/schema acceptance; no schema-number decision or migration in this investigation |
| P1 narrow candidate | Correct “+ credentials” wording, legacy Nova naming and missing-picker backup.zip discovery mismatch | **Candidate only** for separately approved 0.139 scope amendment | Confirm labels/locales/fallback and old archive compatibility; no format or broad UI change bundled |
| P1 narrow candidate | Prepare privately before picker; truthful export phases/timestamps, attempt correlation and interruption cleanup | **Candidate only**; size of lifecycle/permission change may require later phase | Provider tests and diagnostics; no universal invisibility/atomicity promise; owner explicitly selects scope |
| P2 design implementation | No-artwork export, logical categories, independently safe switches and new manifest/default intent | **Later**, owner no-art/granular direction already decided, implementation parked | Decide compatibility/defaults/dependencies, migration and signed-upgrade gates |
| P2 performance | Reduce snapshot lock time/VACUUM passes/repeated IO, compression, disk budget and retention | **Later**, measure before changing | Synthetic large-library baseline and correctness/privacy invariants; preserve existing verified safeguards |
| P2 settings completeness | Add omitted portable choices and controlled registry, keep device/auth/transient state excluded | **Later** unless separately selected | Every setter/default migration/end-to-end fixture; owner defaults/privacy intent approval |
| Out of scope | Broad NOVA/Jetty/FFmpeg/Android changes, launcher integration, 10-title hero implementation, app-wide menu replacement | Remain parked/unrelated | No action authorised by this investigation |

## Decisions requiring owner approval

| Decision | Concrete recommendation for review | Alternative / effect |
|---|---|---|
| D1 — Release placement | Keep granular backup/format work after 0.139; consider only diagnostic-evidenced narrow reliability/wording fixes by explicit amendment | Re-include a named bounded subset in 0.139 after evidence/acceptance gates; never infer approval from this report |
| D2 — Category defaults | Report 06 defaults: portable preferences/user organisation On; matching/sources/bookmarks/watch history Off; resume On; per-title adjustments Off | Owner may prefer matching/history/source convenience On with explicit privacy warning; this differs from current history+resume defaults On |
| D3 — Omitted categories on restore | New logical format: absent category leaves destination unchanged; explicit present-empty replacement reviewed separately | Full replacement mode can clear data but must be chosen and explained, not encoded by missing ZIP entries |
| D4 — Future format / legacy reader | New versioned logical format with per-category schemas/actual inclusion; keep strict compatible Supernova1.0 reader; no arbitrary DB successor number here | A constrained no-art1.0 writer could omit art bytes, but fixed “personal artwork” manifest label remains misleading and cannot support general independent categories |
| D5 — Old archive artwork | Continue accepting validated old artwork-bearing archives; default restore their legacy personal content, new exports always exclude all artwork | Explicitly skip legacy artwork after review and regenerate; acknowledge personal/non-provider art cannot be regenerated. Neither option rewrites existing backups or adds new-artwork switch |
| D6 — Matching/path dependence | Treat physical identification matches and necessary locators/source identities as one optional category with direct filenames/folders/network warning | Path-free logical title/history/collection identities remain separately useful, but cannot promise restoration of unmatched physical associations; unresolved members remain pending |
| D7 — Unmatched membership/variants | Preserve unresolved path-free references; display pending counts/dependencies. Physical preferred variants require selected matching | Require an explicit matching dependency where no portable reference is possible; do not auto-enable paths or silently drop user data |
| D8 — Settings/default intent | Introduce explicit-value versus follow-default representation; conservatively preserve legacy stored user values; add known omitted portable choices | Value-equals-default omission loses intent and is not recommended; new defaults must not silently override explicit user choices |
| D9 — Recovery/auth policy | Preserve private journal originals; define complete local recovery policy and retention; keep plaintext auth out of portable archives; partial new restore leaves unrelated live auth unchanged | Legacy full restore disconnects accounts today; retaining that behaviour requires clear warning. Pre-restore current ZIP is incomplete rollback |
| D10 — Export publication UX | Private prepare first, capability-aware save/readback/rename, durable owned-URI cleanup state; announce exact verified/final-name outcome | Directory-tree first may improve deferred creation but changes save UX/provider coverage; universal invisible/atomic copy is unavailable |
| D11 — Cancellation/retention | Cooperative export cancel before publish; preserve verified local recovery on provider failure; bounded storage/time retention and clear cleanup-pending notices | Exact retention cap/age, retry policy and restore cancel boundaries require product choice and future tests |
| D12 — Missing settings/diagnostic choice | Include genuine missing subtitle/search/network/NFO/player policies under proper categories; optional Diagnostic preferences saves On/Off but no logs | Device-specific/legacy player choices can remain excluded only with accurate disclosure, not “every setting saved” wording |

Already settled and **not needing reconsideration**: no artwork in new backups/no switch; user-selectable categories wherever safe; optional paths with privacy warning; no plaintext secrets recommended; source-read-only/docs-only delivery; no automatic broad backup changes in 0.139.

## Outstanding evidence rather than owner product decisions

- Exact installed APK/source/date/device/provider for incident and notification; diagnostic timeline and termination reason.
- Actual owner's ZIP entry counts/bytes/schema and restoration integrity; classification of retained art. Perform only authorised private review and publish safe aggregates, not private content.
- Runtime every-setting round trip, default/type migration, provider capability/invisibility limits, interruption cleanup and journal power-loss behaviour.
- Measured performance/space/lock hold and signed upgrade proof. These are future engineering acceptance gates; no build/restore/CI was run here.

## Documentation delivery contract

Isolated branch `docs/backup-restore-investigation-2026-10`, based on decisions branch snapshot `c7bbc2dd6d9f0b75ad3cb726d0c632914b43277b`; new files only under `docs/backup-restore-investigation/`. Pinned signed source and component checkouts remain unchanged. Evidence/link/scope verification is documentation validation, not app tests. Publication consists of the requested docs commit/branch; no merge or app source change.
