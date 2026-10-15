# Supernova 0.139 — development candidate, not delivered

15 October 2026. Branch `codex/supernova-0.139-implementation-2026-10-13`; [draft PR17](https://github.com/heretoecode/Supernova/pull/17). Current test/build source: `b2a17cbe5559579f58347b6ad78c45674df85e79`. Version **0.139 / code139**, package `app.supernova.player`. Existing application sources, branding and signing identity are preserved; this follow-up changes tests and prepares the139 signing route. No main merge, public release or future0.140 Details redesign is included.

## Changes implemented or under validation

- Customise Home: two-column editor, built-in content locks, separate reorder/visibility controls, draft confirmation, canonical keyboard, custom icons, content type/genres/matching, supported sort/direction/limits and durable row intent. Shared heading/icon, language/provider and navigation refinements retain their source-pinned evidence.
- Hero and Libraries: ten-title eligibility/shortage policy and idle rotation; successful-output versus failed/cancelled-start handling; artwork retry and shared metadata; Library focus routes and poster/scroll return. Physical animation, playback and owner incident acceptance remain separate.
- Details: independently approved card/actions, tab order/navigation, Extras and indexed-season changes are preserved. **D03 connected frame remains unimplemented pending selected/focused approval; D04 full-information versus fixed passive viewport remains unresolved.** No future information-layout blueprint is substituted.
- Backup & Restore: all26 logical category codecs include production canonical Media matches, coordinated recovery, later manual-choice protection and pending indexing/provider reconciliation. Selected present categories replace owned state; missing/unselected domains remain untouched. The separate legacy1.0 reader has its own whole-replacement/authentication-disconnect contract.
- New backups exclude media/artwork/subtitle content, authentication material, Put.io associations, legacy Collections and private journals/manual epochs. Locations and bookmarks defaultOff; history defaultsOn. Archives are unencrypted; titles/history and arbitrary user text can still reveal private information. No anonymity or universal rematching promise is made.
- Privacy-safe diagnostics, offline notices and audited torrent retirement retain staged evidence; physical output/overhead/network and upgrade checks remain open.

## Confirmed verification and fixes

The first15October full rerun executed996tests and found two failures. One was a test synchronization race: an AlertDialog click was posted, but the helper assertedRESTORE_PREVIEW while the model was stillPREPARING_RESTORE. The corrected helper waits for the expected phase, worker completion and published state. It passed17focused tests/3classes and all older Settings cases inside the fresh unfiltered full run. Product restore behavior was not changed.

Corrected full run: **996tests /208classes;995PASS,1FAIL,0errors/skips**. Release lint: **PASS,0errors/1771warnings**. R8 unsigned assembly completed; batch20m43s/exit1 correctly reflects the retained Backup layout failure. See FULL_RECHECK_FIRST_2026-10-15.json, RESTORE_WAIT_FIX_2026-10-15.json and FULL_RECHECK_CORRECTED_2026-10-15.json.

Connected Backup measurement passes all26 physical/ancestor bounds, complete labels and existing target sizes at960×540/default font scale. At640×360 the inherited395px rail/672×223 virtual panel causes21off-screen controls,21ancestor clips,6undersized targets and1clipped label. The failing case remains active. **No sidebar, support-matrix or design exception has been implemented.** OWNER_DECISIONS_2026-10-15.md presents the smallest proposed choices for Backup and D03/D04; these await owner approval.

Python tooling: **81PASS**, including the prepared manual-only139 signing route's wrong-source/ref/version/certificate, failed-suite and payload-change guards. Existing signing helpers/workflows remain intact. The push registration job was actuallySKIPPED; no signing or real-key preparation occurred.

Four-ABI software evidence includes64paired decode cases, registry and dynamic tempo comparisons. Clean CIb2 run38725529541 completed996tests/994PASS/2FAIL: Backup plus an artwork pixel test that sampled before its UI callback. Its native step/lint/unsigned assembly passed their subchecks. A test-only readiness correction at6ded8d37 passed19focused tests/5classes; fresh full [CI38730716137](https://github.com/heretoecode/Supernova/actions/runs/38730716137) is running. No successful overallCI is inferred. Historical CI binaries were rechecked:84wrapper-manifest entries cover80unique libraries; four helper paths occur in both dependency manifests. Eight remaining libraries match the verified signed138baseline. These checks establish software/byte provenance, not Shield hardware decode, passthrough, HDR or installability.

## APK and delivery status

The current local **unsigned development** APK passed actual identity/version, non-debuggable SDK23/36, branding/assets/providers, ZIP CRC/16KiB alignment,88tested native byte matches and retired torrent manifest/native/class exclusion. It is87672309bytes, SHA256:

`5539f71c4b20c9558eabdde3ea5c1eb27422574e07fce22d4df0c0769c5f39ff`

Preserved filename: `Supernova_0139_unsigned_b2a17cbe.apk`; receipt LOCAL_PACKAGING_RECHECK_2026-10-15.json. Earlier unsigned artifacts and failed receipts remain preserved separately. This is not the signed final filename or a delivered QA candidate.

**No signed139 APK or139 Drive delivery has been produced.** The exact-branch signing-policy request was attempted under the owner's instruction and denied **HTTP403 Resource not accessible by integration**. Readback confirms existing policies unchanged. SIGNING_POLICY_ATTEMPT_2026-10-15.json gives the exact administrator request; no old-ref/key workaround was attempted. No connected Shield/ADB endpoint is available. Whole43 acceptance remains42BLOCKED/1FAIL, with passing subchecks recorded separately.

After software/specification gates and authorized environment access, the prepared workflow can produce the signed QA APK needed for physical upgrade testing; physical signed-upgradePASS is not a circular prerequisite to building that test APK. Keep certificate `79ed34c52c3e359756ade0634d7bbb6f8e94e42a059020c1220bd8c7092a9f5e`. Validate the signed payload before delivering `Supernova_0.139.apk` to the [existing Supernova/APKs folder](https://drive.google.com/drive/folders/1mfHrFgLdJJSJYtTPys1N4Y-_KuE_84mM), then download/read back, hash and record its actual file link. Drive metadata confirms the existing folder and138file parent; incomplete searches do not establish absence of existing files or a write-permission failure.

HTML release-note source preserves verified138 history and accurately labels139 unfinished. Published Drive HTML remains unchanged; no completed delivery/publication is claimed. CONTINUATION.md and SHIELD_ACCEPTANCE.md identify the exact continuation and43-row physical test procedure.
