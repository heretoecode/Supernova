# Preview 4.1.5 — all 23 audit findings

Implementation dispositions below do not imply physical acceptance. Test execution results are recorded separately in the return handover. FIXED means the identified source defect was corrected; PHYSICAL SHIELD QA REQUIRED still applies where listed.

| ID | Severity | Disposition | Change / reason | Validation | Shield QA required |
|---|---|---|---|---|---|
| SNA-001 | HIGH | FIXED | Explicit automatic resume transfer is resolved at PlayUtils launch boundary; explicit positions and restart/remote modes retain priority. Manual Versions requery retains physical identity. | Preview415Test automatic-version/launch-policy tests; real engine seek remains untested. | Yes |
| SNA-002 | HIGH | FIXED | Adjacent episode compares season/episode, ignores current encodes, selects deterministic best next version. Cached Preview binge selection shares this policy. | Duplicate current/next encodes, season boundary and end-of-series tests. | Yes |
| SNA-003 | HIGH | MITIGATED | Both SFTP engines persist first-use SSH wire-key fingerprints and reject changes before authentication. Explicit per-server reset; trust records excluded from portable archives. | Persistence, normalised host, port separation, changed key and explicit reset tests. Real SSH handshakes/key rotation not tested. First-use interception remains possible. | Yes |
| SNA-004 | HIGH | FIXED | Both internal bridge constructors bind 127.0.0.1 and publish matching URLs. Inspected consumers are on-device playback/subtitle/proxy paths. | Actual bound-address and local socket connection test. External-player/protocol matrix remains physical QA. | Yes |
| SNA-005 | HIGH | MITIGATED | Durable pre-swap rollback journal, committed marker and recovery before providers. Incomplete restore rolls back files and preferences; committed cleanup is idempotent. | Interrupted-swap and committed-cleanup recovery tests on disposable files/preferences. Actual kill/power loss, storage failure and concurrent non-media database writers remain unproven. | Yes |
| SNA-006 | HIGH | MITIGATED | Targeted production-boundary tests and existing full-process emulator startup/playback smoke workflow. | New tests plus existing suites run in both CI modes. Not full device end-to-end regression coverage. | Yes |
| SNA-007 | MEDIUM | MITIGATED | Consumed explicit watched markers are cleared for every physical encode, not only the preferred encode. | Existing journey suites; manual Unwatched-to-committed-series reconciliation remains deferred: needs an explicit logical/file-history policy and migration tests to avoid overwriting other real history. | Yes |
| SNA-008 | MEDIUM | DEFERRED WITH REASON | Physical-ID row membership remains. Stable logical IDs require migration of saved memberships/dismissals and recovery when the old physical record has vanished. Avoid silent row reassignment without that migration evidence. | Source review; no correction claimed. | Yes |
| SNA-009 | MEDIUM | MITIGATED | Clear preserved refresh order when a Sort/Order selection is accepted. Reinspection corrects the audit: ordinary button clicks already clear it; remaining race is a loader refresh while the chooser is open. | Source tracing and existing sort suites; refresh-during-dialog needs runtime validation. | Yes |
| SNA-010 | MEDIUM | FIXED | Apply Hide Watched to modern library listing; use actual database article-aware sort titles for grid/list title sorting. History and Versions remain available. | Preference-to-filter/title policy test and existing library tests. | Yes |
| SNA-011 | MEDIUM | FIXED | Update source rescan flag by database ID, not non-unique name. Existing scheduler preserved. | Real shortcut database with two identically named sources. | Yes |
| SNA-012 | MEDIUM | FIXED | Let focus move horizontally inside browser dock before leaving for contextual/source panels. | Compile and browser smoke; real D-pad edge geometry remains untested. | Yes |
| SNA-013 | MEDIUM | FIXED | Help registry values are weak references, removing value→Activity→weak-key retention. | Ownership-path source review; no heap-dump claim. | Yes, repeated Settings/Back |
| SNA-014 | MEDIUM | FIXED | Do not permanently cache null/empty metadata failures. Later explicit requests can retry; bounded worker retained. | Source review; live network failure/recovery not tested. | Yes |
| SNA-015 | MEDIUM | DEFERRED WITH REASON | Search still scans/normalises the library in Java. A new index/cache and invalidation policy would broaden this pass; no measured Shield latency justifies that change yet. | Source review; large-library profiling required. | Yes |
| SNA-016 | MEDIUM | FIXED | Launch-cover release depends on laid-out content, not remote image completion. Existing fallback can be used immediately. | Startup/recreation and emulator launch checks; offline large-library start remains physical QA. | Yes |
| SNA-017 | MEDIUM | MITIGATED | Backup confirmation explicitly states no password encryption and recoverable passwords/tokens. Trust pins cannot be imported. Archive encryption deferred: requires passphrase/recovery UX and compatible format design; confidentiality weakness remains. | Source review; no encrypted-backup claim. | Yes |
| SNA-018 | MEDIUM | FIXED | Decode replacement settings using a clear-first editor; parse failure never commits it. Applies to included named stores too. | Real SharedPreferences replacement/malformed-input and restore-journal tests. Absent optional named stores retain legacy compatibility. | Yes |
| SNA-019 | MEDIUM | FIXED | Targeted newer suites run regardless of preview/full mode. | Workflow inspection and actual preview CI. Full-mode run itself not performed. | No; CI |
| SNA-020 | MEDIUM | DEFERRED WITH REASON | Approved browser contextual composition differences retained; this pass explicitly excludes redesign. | No visual conformance fix claimed. | Yes |
| SNA-021 | MEDIUM | MITIGATED | Provider auto-skip rejects end/out-of-range/unknown-duration targets and never calls completion. Only backend completion commits watched/advances through that route. | Seek-target policy tests plus source trace. Historical long WebDAV false EOF cause still unknown; no causal linkage established. | Yes |
| SNA-022 | LOW | FIXED | Invalid/non-numeric/out-of-range custom port stops credential submission and retains field focus. | Source trace; manual input/connection check required. | Yes |
| SNA-023 | LOW | FIXED | Failed or portrait artwork clears both current/previous backdrop bitmaps. | Bitmap failure-state test. Visual transition acceptance remains physical QA. | Yes |

## Diagnostic findings outside SNA index

**DIA-001 — 02:35 startup crash: MITIGATED.** The exact field and uncaught propagation are established; the exact underlying bad value/window state is not. Per-record containment, database-error containment, redacted record/type coordinates and mixed-row tests added. No database repair or corruption dismissal.

**DIA-002 — browser Details shared transition: FIXED.** Same-build physical logs show reachable null legacy image view. Guard absent/detached source views and finishing activity. Details opens without shared-element animation. Null/detached transition regression test added.

**REL-001 — stale About identity: FIXED.** Existing About text still said Preview 4.1.3 despite 4.1.4 BuildConfig. Updated to 4.1.5 and correct preceding baseline; runtime version/SHA fields remain generated.

## Upstream provenance

No additional upstream Nova change was ported during 4.1.5. The pinned 4.1.4 AVOS/WebDAV corrections remain. New FileCore/MediaLib patches are local stability corrections applied after their existing patches; no dependency versions changed.
