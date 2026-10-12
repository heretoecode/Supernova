# 14 — Proposed maintenance implementation roadmap

This is **PROPOSED**, with no implementation authority. The source baseline is signed0.138; recheck all source/overlap/dependency/security recommendations against final validated0.139 before creating a maintenance implementation branch. Source investigation does not authorize updates, cleanup, schema migration, CI changes, signing or release.

## Sequencing decisions

| Boundary | Work and dependencies | Measurable checkpoint / approval gate | Reversal |
|---|---|---|---|
| R0: owner0.138 walkthrough | Signed candidate already delivered; install/upgrade/Shield observations still unknown | Owner walkthrough and prioritized bug list against exact APK; private logs only with separate authorization | Preserve existing signed install/data; no branch merge implied |
| R1: intervening diagnostics release, provisionally0.139 | Selected approved0.138 fixes + typed safe recorder additions, reliable playback/seek/startup pairing and sampler deltas | Privacy tests, startup stability, recorder A/A overhead/retention/export proof, owner acceptance of exact fields | Revert event additions independently; keep historical schema/readability and safe export |
| R2: maintenance rebaseline and compatibility specification | Exact validated0.139 + upstream stable/current refs, actual native build provenance, schema fingerprints and transport/shrinker/role maps | Updated ledger with already-present/changed findings and owner-selected work packages; corpus hashes frozen | Docs/spec only |
| M1: isolated critical safety/stability | Jetty/advisory prerequisite review, relay/native HTTPS policy, credential readiness/repair, native buffer bound candidates | Per-candidate owner approval, exact diff/dependencies, controlled security/privacy tests and device smoke | Separate commits/dependency lock changes; credentials migrations require protected recovery, never dump secrets |
| M2a: network/files | Read-purpose/stream ownership/SMB readiness and URI/NFO reconciliation; protect SFTP trust and staged browser selection | NAS/protocol/encoded-path/integrity/reconnect tests, FD/memory/startup distributions, no UI change | Reversible non-schema patches; retain original source identity and progress fixture |
| M2b: coherent playback/native group | Rebuilt FFmpeg/AVOS/MediaLib/Video APIs, surface/route/seek/pause/subtitle behavior; no updater/TLS-default downgrade | Four-ABI/JNI/enum/native build proof, corpus/Shield output, measured start/seek/recovery and stable requested state | Whole coherent group boundary, not mixing native generations; retain previous artifact/source set |
| M3: library/schema/backup | Dual-v62 successor migration, genre mapping if selected, scan/refresh/cache cleanup | Every preserved row/membership/history invariant; all upgrade/backup/process-death cases; no auto DB deletion rollback | Explicit data recovery and backward backup policy; APK downgrade alone forbidden |
| M4: semantic UI consolidation | Static/running atlas + existing SharedThreePanel/Library/Keyboard; pilot Overview then Settings/library | Source-role-consumer matrix, screenshot/style parity, every-key focus/back/dirty-exit/recreate checks | Role wrapper migration by screen; preserve originals until equivalence demonstrated |
| M5: proven cleanup/optimization | R8/retained class/resource/native inventories, direct/indirect reachability proof and prior subsystem stability | Approved removal register; actual APK/installed/DEX/resource deltas plus noise-controlled runtime comparison | Small deletions/rule changes and rollback commit; protect migration/backup/readers/legal assets |
| M6: release sign-off | Complete cross-area regression, exact signed build and owner Shield review | Stable startup plus corpus/data/navigation/privacy/license/performance evidence and owner release approval | Preserve prior signed artifact + matching source, with explicit schema recovery strategy |

M1 can precede broad architecture work because measured security/data integrity is more important than reducing duplication. M2a and M2b share read/proxy/player interfaces: agree the interface/buffer contract first and sequence implementation commits accordingly; they are not independent cherry-pick tracks. M3 schema may remain deferred if genre-ID benefit is modest or rollback cannot be proven. M4 token documentation can start read-only while native verification is underway, but player HUD migration waits for playback stabilization. M5 follows actual reachability and R8 evidence; do not force deletion to achieve a predetermined APK target.

## Functional work packages and minimum scope

Each selected area should address related verified issues together where sharing a source/contract makes narrow fragments unsafe. For example, seek work includes request generation, native flush/frame ownership, achieved position, durable checkpoint, subtitle replay and pause intent; network work includes readiness/auth, range cancellation, read purpose, path identity and scanning reconciliation. This is a comprehensive proposal, not open-ended feature permission.

Use one problem/contract per reviewable commit where possible, frozen dependency locks, before/after tests and an explicit rollback boundary. Avoid simultaneous broad file rewrites with upstream adaptations. Do not import all upstream native modes or optional Sonic functionality solely because the upstream branch contains them. Do not combine genre schema migration with UI reorganization or broad keep-rule narrowing.

## Owner decisions before implementation

1. Confirm0.138 walkthrough and selected0.139 fixes; approve smallest diagnostic field/retention/export contract after privacy review.
2. Approve security triage priorities and endpoint trust policy, including LAN relay scope, self-signed native HTTPS and Jetty/jUPnP minimum-Android compatibility.
3. Choose native source target from reproducible release/component evidence, preserving Supernova custom metadata/JNI; exclude upstream visuals/updater.
4. Decide genre-ID benefit versus schema/backup/rollback cost and maximum supported old archive compatibility.
5. Review semantic role taxonomy/pilot and intended visual invariance; approve exact removal candidates only after evidence.
6. Set hardware/test corpus/performance acceptance thresholds and authorize any subsequent builds/implementation/releases separately.

Suggested release boundaries are diagnostics first, then one or more focused safety/subsystem maintenance candidates, then comprehensive UI/cleanup candidate. Version numbers beyond provisional0.139 remain owner decisions. Nothing in this package establishes release readiness or authorizes any of these changes.
