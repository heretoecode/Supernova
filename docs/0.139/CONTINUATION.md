# Supernova 0.139 — signed QA APK delivered; physical acceptance open

15 October2026. Existing branch `codex/supernova-0.139-implementation-2026-10-13`, draftPR17. Do not reset/restart, change the approved design, merge main, tag, publish a public release or begin0.140.

## Completed signing and delivery

- Exact APK source: **4e4d2710435ddd9058923a3d9fbd6a2fc27e0464**; approved implementation **23f75b38b18f6b34f5ceb94c001260f08bafac09** is preserved unchanged. All later delivery changes are documentation/evidence only. Resolve the latest branch HEAD for the documentation commit; it is distinct from the APK source.
- Owner added exact139branch policy63180517 to existing `supernova-foundation-signing`. All five earlier branch policies and other security rules were preserved. The agent changed no environment rules, secrets or certificate. Readback: SIGNING_AUTHORIZATION_2026-10-15.json. Previous403 receipts remain historical, no longer active blockers.
- Existing manual signing workflow **38773230129**, [successful run](https://github.com/heretoecode/Supernova/actions/runs/38773230129), rebuilt and tested its exact HEAD before protected signing. Conformance:1007tests/0failures/errors/skips, lint0errors/1771warnings,81Python guards,138runtime hashes,24rebuiltFFmpeg and64pairedsoftware decode cases. Prior implementation CI38746280548 retains independent1007/209 XML evidence.
- **Supernova_0.139.apk**, **87874512bytes**, SHA256 **bc8d45eea2fe17c49048b48e97eaf5da228c021647675968d725237314344827**. Certificate **79ed34c52c3e359756ade0634d7bbb6f8e94e42a059020c1220bd8c7092a9f5e**, unchanged from signed138. Actual package app.supernova.player/version0.139/code139/minSdk23/targetSdk36/non-debuggable. Independent signed verification, native/branding/assets/CRC/16KiB checksPASS. Exact unsigned-to-signed payload parity passed in protectedCI. Static signature/installability checks are recorded separately from unrun device install/upgrade.
- Delivered to existing Supernova/APKs folderID **1mfHrFgLdJJSJYtTPys1N4Y-_KuE_84mM**. FileID **1rWpKsPTJ564oOQsvnkJSJASaRN6UbwZX**, observed direct file link **https://drive.google.com/file/d/1rWpKsPTJ564oOQsvnkJSJASaRN6UbwZX/view?usp=drivesdk**. Metadata parent/name verified; downloaded Drive copy size/complete bytes/SHA256 match. No duplicate folder or unrelated file replaced. No sharing change.
- CorrespondingFFmpeg source remains in workflow artifact `supernova-0139-qa-ffmpeg-source`; both signed and source archives match GitHub digests and CRC. Raw artifacts/native source/public proof are retained locally under `/workspace/release-evidence/signing-139/`.

## Records and remaining acceptance

RELEASE_NOTES.md and source HTML are finalised for deliveredQA, preserving source CSS and all138/olderhistory. Published DriveHTML update/readback is recorded in HTML_RELEASE_NOTES_VERIFICATION_2026-10-15.json with a complete matching download. SIGNED_QA_VERIFICATION_2026-10-15.json, SIGNED_QA_CI_EVIDENCE_2026-10-15.json and DRIVE_DELIVERY_VERIFICATION_2026-10-15.json contain exact proof. Historical failing/unsigned/denied-policy receipts are preserved and remain source-pinned. Previous checkpoint is archived in CONTINUATION_BEFORE_SIGNED_DELIVERY_2026-10-15.md.

**NOTRUN:** physicalShield focus/output/passthrough/HDR, actualdevice installation,138→139signed upgrade, actualOS-force-stop recovery and ownerincident reproduction. No physicalendpoint/mediaZIP supplied. All43whole rows remainBLOCKED for their outstanding state/render/hardware matrices; successful softwareCI and QAdelivery are separate. Use the deliveredAPK/checksum with SHIELD_ACCEPTANCE.md. Do not uninstall/reset to manufacture an upgrade pass.

No app/test/asset/resource/build/workflow/dependency/signing change was needed for delivery. PR17 remainsdraft/open; no merge/main/tag/publicrelease/0.140 action. Further development is not required to complete this signedQA delivery; physical acceptance requires the actualdevice and ownerfixtures.
