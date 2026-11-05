# Supernova 0.139 — signed QA build delivered

15 October 2026. **Supernova_0.139.apk**, package **app.supernova.player**, version **0.139**, version code **139**.

[Download the signed QA APK](https://drive.google.com/file/d/1rWpKsPTJ564oOQsvnkJSJASaRN6UbwZX/view?usp=drivesdk) · [Successful signing workflow](https://github.com/heretoecode/Supernova/actions/runs/38773230129)

This is a development/QA build. Physical Shield testing and the signed upgrade from 0.138 have **not been run**. Delivery does not close the outstanding acceptance matrices or declare a public release.

## Changes

- Customise Home retains the cinematic two-column editor, built-in content locks, independent reorder/visibility, incomplete-draft confirmation, canonical keyboard, custom icons, type/genre matching, supported sorting/limits and durable row intent.
- Home and Libraries retain ten-title hero eligibility/idle rotation, successful-output versus failed-start handling, artwork retry/shared metadata, semantic headings/icons and Library focus/poster/scroll return.
- The owner-approved compact Backup layout fits all 26 categories and four actions within 640×360 without a scrolling category list. The Settings menu remains permanent with readable compact labels; normal-TV sidebar geometry is preserved.
- Original Details D03 uses a connected selected-tab cap/frame, separate actual focus emphasis and stable 4% usable side/bottom insets. Existing cards/actions, tab order and indexed-season work are preserved.
- Original Details D04 retains the complete synopsis, useful facts and up to eight cast/eight crew. Read-only information wraps through vertical overflow. Down/Up reads with heading focus retained; Back follows the existing Activity/fragment exit route. Content remains below the fixed tab strip, and growing media focus outlines retain clearance.
- Backup & Restore includes all 26 production logical codecs, including canonical Media matches, coordinated recovery and protection for later manual choices. Selected present categories replace owned state; missing/unselected categories remain untouched. Legacy 1.0 keeps its separate whole-replacement/authentication-disconnect contract.
- New backups exclude media/artwork/subtitle content, credentials, Put.io associations, legacy Collections and private journals/epochs. Locations/bookmarks default off; history defaults on. Archives are unencrypted, and arbitrary user text may contain private information.
- Existing privacy-safe diagnostics, offline notices, language/provider assets and audited torrent retirement are preserved. Synthetic tests do not diagnose the owner's actual playback incident.

## Verification and exact provenance

The existing signing workflow [38773230129](https://github.com/heretoecode/Supernova/actions/runs/38773230129) completed successfully on **`4e4d2710435ddd9058923a3d9fbd6a2fc27e0464`**. That is the APK's exact source commit. Approved implementation **`23f75b38b18f6b34f5ceb94c001260f08bafac09`** is its ancestor; intervening changes and later delivery records modify documentation/evidence only. Application, assets, resources, tests, build/workflow configuration, dependency patches and signing recipes remain unchanged.

The signing run repeated **1,007 application tests with zero failures, errors or skips**, release lint **0 errors / 1,771 warnings**, unsigned R8 assembly, **81 Python guards**, 138 reviewed runtime artifact hashes, 24 rebuilt FFmpeg library checks and 64 paired software decode cases across four ABIs. The completed signing log independently records 1,007 passing test lines across 209 distinct class labels. The preceding exact-implementation CI38746280548 also supplied independent JUnit XML for **209 classes**; its artifact and historical failures remain preserved separately.

The downloaded signed APK independently passed certificate verification over Android API23–36, package/version parsing, branding/assets/provider isolation, non-debuggable status, CRC/duplicate-entry checks, 16 KiB alignment, all 88 native hashes and torrent retirement. Its certificate matches the downloaded signed 0.138 baseline. Exact current unsigned-to-signed payload parity passed inside the protected signing workflow; the independent receipt records any additional comparison with the preceding unsigned build.

- APK filename: **Supernova_0.139.apk**
- APK size: **87874512 bytes**
- APK SHA-256: **`bc8d45eea2fe17c49048b48e97eaf5da228c021647675968d725237314344827`**
- Signing certificate SHA-256: **`79ed34c52c3e359756ade0634d7bbb6f8e94e42a059020c1220bd8c7092a9f5e`**
- Application identity: **app.supernova.player / 0.139 / code 139 / minSdk23 / targetSdk36 / non-debuggable**
- Drive delivery: [verified file](https://drive.google.com/file/d/1rWpKsPTJ564oOQsvnkJSJASaRN6UbwZX/view?usp=drivesdk) in the existing [Supernova/APKs folder](https://drive.google.com/drive/folders/1mfHrFgLdJJSJYtTPys1N4Y-_KuE_84mM). The uploaded copy was downloaded again and its complete bytes, size and SHA-256 matched.

Public corresponding FFmpeg source is retained in the same workflow's `supernova-0139-qa-ffmpeg-source` artifact. Artifact archives were checked against their GitHub SHA-256 digests. Signing used the existing protected environment and certificate; no signing secrets or security rules were replaced.

Receipts: SIGNED_QA_VERIFICATION_2026-10-15.json, SIGNED_QA_CI_EVIDENCE_2026-10-15.json, DRIVE_DELIVERY_VERIFICATION_2026-10-15.json and SIGNING_AUTHORIZATION_2026-10-15.json. Historical unsigned CI evidence remains in CI_APPROVED_*_2026-10-15 files.

## Remaining acceptance

**NOT RUN:** installation on an actual device, physical Shield focus/output/passthrough/HDR, actual OS-force-stop recovery, the owner's incident reproduction and the 0.138→0.139 signed upgrade. Static installability/signature checks are not a device installation or upgrade test. No physical endpoint or incident media/ZIP was supplied.

All 43 whole-workstream results remain **BLOCKED** for their outstanding state/render/hardware matrices; automated software passes and successful QA delivery are recorded separately. Use the exact APK/checksum above with SHIELD_ACCEPTANCE.md. No PR merge into main, tag, public GitHub release or 0.140 work was performed. The future Details redesign remains excluded.
