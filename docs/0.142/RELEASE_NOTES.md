# Supernova 0.142 — signed QA candidate

Status: signed QA delivered and download-verified; physical/final acceptance pending. No main merge.

- Network & Files gains Overview, Local Storage, Network Shares, Cloud Services, All Files and Library Health in a fixed rail with one flexible workspace. Overview has four information cards and no Quick Actions strip. The existing global navigation, Space Black artwork, typeface and monochrome icon language are retained.
- Local/USB storage, six connection protocols, discovery, bookmarks, staged library membership, scan settings and Health remain accessible. File browsing keeps its toolbar, sort/filter modes, breadcrumbs and existing playback/Details handoffs.
- Put.io keeps device authorization, account actions, explicit WebDAV associations and sync protections. Its read-only API browser is adapted for the same workspace. No additional cloud provider, token-entry mode or file operation is added.
- Corrective work covers supported credential-recovery aliases, non-SMB email usernames and hostname-case lookup; stale file rows, repeated Retry actions, discovery callback ownership, inherited-exclusion feedback, scan preference defaults and disabled Put.io account probes. Verification and remaining limitations are tracked item by item; this list is not physical interoperability certification.
- Edit Library uses a darker backdrop and panels across all five steps, preserving layout, working controls and Live Preview.
- Playback HUD work is a source-grounded proposal only. Player/HUD application code is unchanged and final visuals remain deferred for owner approval.

Software results, reference comparisons and physical release gates are recorded in IMPLEMENTATION.md, PARITY_LEDGER.md and RELEASE_REPORT.md. Physical Shield, real Put.io/SMB/WebDAV, USB reconnect, signed upgrade and final screenshot acceptance must not be inferred from software renders.


Signed QA **delivered and fully download-verified**: [Supernova_0.142.apk](https://drive.google.com/file/d/1CxWm1pxf5K005ZzIO4RWKzQyPF0YQcu4/view?usp=drivesdk), 87,907,280 bytes, package `app.supernova.player`, version **0.142 / code 142**. Exact source: `b347707defdbf2ab3ae83d920ba9fa7af3936220`. APK SHA-256: `af03d26719b00601ca08da5ad562c1f196ec536c6397ac5b4603c2337d7c8514`. Existing certificate SHA-256: `79ed34c52c3e359756ade0634d7bbb6f8e94e42a059020c1220bd8c7092a9f5e`. [Signing workflow](https://github.com/heretoecode/Supernova/actions/runs/40080144690) passed the repeated tests/native/binary gates, existing-certificate signing, tested-payload parity and temporary-key cleanup. Independent signature, manifest/resources/assets, native hashes, ZIP CRC and 16 KiB alignment pass. The complete Drive copy matches the signed artifact. [Delivery receipt](SIGNED_QA_DELIVERY.json); [Shield quickcheck](QA_QUICKCHECK.md).

This is a QA candidate; final acceptance, physical Shield, real provider/storage interoperability and signed upgrade remain pending. Playback HUD redesign stays deferred pending final visual approval. No main merge.
