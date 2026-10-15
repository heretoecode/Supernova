# Supernova 0.139 — authorised signing workflow active

The owner added the exact development branch to the existing signing environment. All five previous branch policies and the other security rules are preserved; the agent changed no secrets, certificate or environment rules. The previous403 blocker is resolved. Readback: SIGNING_AUTHORIZATION_2026-10-15.json.

**Active signing workflow:** [38773230129](https://github.com/heretoecode/Supernova/actions/runs/38773230129), job116554234003, exact source **4e4d2710435ddd9058923a3d9fbd6a2fc27e0464**, existing `.github/workflows/supernova-0139-qa.yml`, `approved_qa_distribution=true`. Do not dispatch a duplicate run. Provider identity and dependency preparation pass; pinned native rebuilding is active as of17:03UTC. Inspect actual run status before proceeding.

The source is unchanged from approved implementation23f75b38b18f6b34f5ceb94c001260f08bafac09 outside documentation/evidence. Its preceding CI38746280548 passed1007tests/209classes, zero failures/errors/skips; all receipts remain preserved. Current signing run repeats full conformance before protected key access, signature validation and tested-payload parity.

Existing branch `codex/supernova-0.139-implementation-2026-10-13`, draftPR17, no merge/main/tag/publicrelease/0.140/design change. This checkpoint is documentation only and does not change the already checked-out workflow source.

## Next actions

1. Wait for the existing run to complete; investigate actual failures if any. Do not sign or deliver a failing candidate.
2. Download `supernova-0139-signed-qa` and `supernova-0139-qa-ffmpeg-source`. Verify archive digests/CRC, actual APK certificate79ed34c52c3e359756ade0634d7bbb6f8e94e42a059020c1220bd8c7092a9f5e, packageapp.supernova.player, version0.139/code139, SDK23–36/non-debuggable, branding/assets/native hashes/16KiB alignment and protectedCI payload parity. No physical install/upgrade is implied by static checks.
3. Upload verified **Supernova_0.139.apk** to existing folder **1mfHrFgLdJJSJYtTPys1N4Y-_KuE_84mM**; verify metadata and downloaded complete bytes/SHA256. Do not create a duplicate folder or upload unsigned evidence. Existing known signed138file1_KdbMEUyTDPEYjmKD-UDG4hud-EBCJlO confirms the destination.
4. Finalise Markdown and HTML release notes, update existing published DriveHTML file **17P05N7Q8c0TQdeZIEjUKYoQ-4xZIz-DW** and read back its exact bytes. Preserve CSS and all prior release history/sharing. Source0.137-and-older history and CSS match the existing publishedHTML; local source also includes0.138.
5. Record exact APK source/run/file/checksum/Drive link and update draftPR17. Keep all43whole rowsBLOCKED for their remaining state/render/hardware matrices while recording software/signing/delivery passes separately.

**NOTRUN:** actual device installation, physicalShield focus/output/passthrough/HDR,138→139signedupgrade, actualOS-force-stop recovery and ownerincident reproduction. No physical endpoint or incident media/ZIP supplied. No signedAPK/Drive139delivery is claimed while the run is active.

Workspace `/workspace/Supernova`; run data and preserved publishedHTML `/workspace/release-evidence/signing-139/`; monitor session70459; AndroidSDK `/workspace/android-sdk`; verified signed138baseline `/workspace/release-evidence/Supernova_0.138.apk`; preceding unsigned CI artifact `/workspace/release-evidence/ci-38746280548/`. Prepared local scripts `/workspace/scratch/verify_signed_139.py` and `/workspace/scratch/finalize_delivered_139.py` require actual signed/delivery proofs before recording success. Connected GitHub artifact download succeeded previously where direct CLI redirected storage returned403.

Previous full checkpoint is preserved in CONTINUATION_BEFORE_SIGNING_STARTED_2026-10-15.md. Continue independently; no new permission or design approval is needed.
