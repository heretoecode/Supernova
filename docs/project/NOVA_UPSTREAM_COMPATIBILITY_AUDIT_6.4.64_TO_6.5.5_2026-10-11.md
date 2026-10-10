# Supernova — NOVA upstream compatibility audit (6.4.64 → 6.5.5)

**Recorded:** 2026-10-11 (review of release published 2026-10-09)  
**Status:** READ-ONLY INITIAL AUDIT / PROPOSAL — NO INTEGRATION AUTHORISED  
**Scope:** NOVA upstream updates since Supernova's pinned 6.4.64-era foundation. Do not alter the active refinement PR #10, signing, application identity, UI, or current QA baseline.

## Executive conclusion

**Do not merge or wholesale replace Supernova with NOVA 6.5.5.** Establish a separate post-refinement upstream-maintenance workstream. Selectively cherry-pick or backport security, playback, networking, scanning and stability fixes after verifying prerequisites and testing on physical NVIDIA Shield. Preserve Supernova's approved visual designs, D-pad/focus model, settings, library semantics, navigation, playback HUD and Details page. Never treat upstream UI changes as automatic requirements.

**Evidence distinction:** The manifest/branch comparisons below are verified. Feature lists are upstream release-note claims, not proof that the fixes are absent from Supernova, directly transplantable, or already tested. No build, source integration, ABI comparison, dependency audit or physical-device validation was performed as part of this document.

## Verified source topology

- Supernova's `.github/build/nova-ci.xml` on `main` pins `aos-AVP` to `9c5842b74ab180c99441ed23fdd5d508448388a7` (the upstream v6.4.64 tag commit); `aos-Video` to `0651a3e0b60ada996a9774ab527d7c1ef34b7503`; `aos-FileCoreLibrary` to `c4b760c55102c72d45f68b0d16999fc1f4f013a7`; and `aos-avos` to `2cf21c486c7bf244c49b78d2220197a6d75396dd`. Its customised MediaLib is pinned to `heretoecode/aos-MediaLib@5758074049bc153ac4967b4ecd21886469270076`. These are the **main-branch manifest pins observed during audit**, not a claim about the current draft PR's eventual built APK.
- Upstream `aos-AVP` comparison `v6.4.64...v6.5.5`: 4 commits, only `core.mk` and `v6_4.xml` changed. **This does not represent the full product diff** because the manifest switches component branch revisions.
- Upstream v6.4.64 manifest: Video/MediaLib/FileCoreLibrary `v6.4`, AVOS `android_sync_back`. Upstream v6.5.5 manifest: all four use `v6.4-lint`. `native/libyuv` is removed from the latter manifest; upstream 6.5.2 notes switching colour conversion to FFmpeg swscale. This requires native build and ABI compatibility investigation.
- Branch comparisons: Video `v6.4...v6.4-lint`: 78 ahead; MediaLib: 22 ahead; FileCoreLibrary: 39 ahead; AVOS `android_sync_back...v6.4-lint`: 60 ahead and 1 behind (diverged). GitHub's compare file list for Video was capped at 300, so it must not be interpreted as an exhaustive file inventory.
- Relative to the specific pinned **upstream** commits in Supernova's main manifest: Video → `v6.4-lint` 86 ahead; FileCoreLibrary 46 ahead; AVOS 60 ahead. Supernova's custom MediaLib fork must be diffed separately against both upstream and the active refinement implementation before proposing patches.

Sources:
- https://github.com/nova-video-player/aos-AVP/releases/tag/v6.5.5
- https://github.com/nova-video-player/aos-AVP/releases/tag/v6.5.3
- https://github.com/nova-video-player/aos-AVP/releases/tag/v6.5.2
- https://github.com/nova-video-player/aos-AVP/releases/tag/v6.4.71
- https://github.com/nova-video-player/aos-AVP/releases/tag/v6.4.72
- https://github.com/nova-video-player/aos-AVP/compare/v6.4.64...v6.5.5
- https://github.com/nova-video-player/aos-AVP/blob/v6.4.64/v6_4.xml
- https://github.com/nova-video-player/aos-AVP/blob/v6.5.5/v6_4.xml
- https://github.com/heretoecode/Supernova/blob/main/.github/build/nova-ci.xml

## Upstream change register — initial triage

| ID | Change / evidence | Priority | Disposition / Supernova guardrail |
|---|---|---|---|
| U-01 | A/V sync across seek, pause, resume, playback speed; MediaCodec flush/async seek timing (6.5.5) | P0 | **Investigate for backport**; compare AVOS, Video and JNI dependencies; Shield PCM/passthrough tests. |
| U-02 | Passthrough freezes after repeated seeks; long-uptime timing overflow; IEC nonblocking output (6.5.3) | P0 | **Investigate** native fix isolation and regression tests; preserve user audio settings. |
| U-03 | Stream buffer allocation overflow hardening (6.5.3) | P0 | **Security/stability review**; assess exploitability and backportability, do not claim a confirmed vulnerability without evidence. |
| U-04 | SMB jcifs-ng freeze, 4x HTTP proxy buffer improvement claim, HTTP range/unknown-length/cancellation, SMB/SFTP/WebDAV stability (6.5.2) | P0 | **Investigate** network stack and streaming performance; benchmark rather than assume 4x in Supernova. |
| U-05 | SMB readiness before metadata/playback and SMB/FTP reliability (6.5.5) | P0 | **Investigate** startup/seek/disconnect/reconnect, NAS sleep/wake, credentials; protect custom Network & Files browser. |
| U-06 | SMB playback interruption and limited-concurrency servers (6.4.72), SMB playback and WebDAV authentication (6.4.71) | P0 | **Investigate** whether fixes are already included in 6.5.x branch; avoid duplicate/conflicting patches. |
| U-07 | FFmpeg swscale replacing libyuv for colour conversion (6.5.2) | P1 | **Architecture-dependent**; investigate performance, colour accuracy, native ABI and removal of libyuv before adoption. |
| U-08 | Audio speed sonic filter / speech clarity (6.5.1/6.5.2) | P2 | **Optional feature**, not a mandatory fix; assess CPU cost, output quality and settings/UI impact. |
| U-09 | ARC speaker-route passthrough capabilities and external Bluetooth media buttons (6.5.5) | P1 | **Investigate** Shield audio routing and remote/media-key handling; avoid focus/HUD regressions. |
| U-10 | TMDB-ID genre filtering (6.5.5) | P1 | **Review only**: may benefit metadata consistency but must not replace approved Supernova library filters or cause schema/data migration without explicit approval. |
| U-11 | TV Lists duplicate rows, wrong target list, back-key handling (6.5.3) | P1 | **Applicability check** against Supernova's own Lists and focus/navigation implementation; import only underlying fix if relevant. |
| U-12 | WebDAV OPTIONS redirect validation (6.5.3), FTP(S) robustness (6.5.2) | P1 | **Investigate** network security, interoperability and regression coverage. |
| U-13 | In-app updater for GitHub/Play builds (6.5.5) | Exclude by default | **Do not adopt automatically**: risks wrong update channel, app identity, signature, user consent and Supernova release controls. Separate product decision required. |
| U-14 | Upstream Android TV/phone UI, RTL, OSD clipping and presentation changes (6.5.x) | Exclude UI | **Protect Supernova visual and focus contracts**. Review isolated accessibility/fix logic only where applicable. |
| U-15 | Broad lint refactor and build/dependency changes on `v6.4-lint` branches | P1 | **Stage separately**; high change surface, risk of subtle runtime regression. No bulk source replacement. |
| U-16 | Trakt, OpenSubtitles, metadata/scraping and library persistence interactions | P1 | **Dependency and schema review** before any import; preserve Supernova's custom MediaLib fork, watch/resume history, provider integrations and library data. |

## Risk boundaries and acceptance gates

1. **Freeze current refinement QA first.** Draft PR #10 and its signing/Shield acceptance remain independent. No upstream code changes or mass dependency bumps on that branch.
2. **Verify actual baseline** from the signed QA APK's source commit and CI manifest before computing final diffs; `main` pins alone are not sufficient if the active branch has advanced.
3. **Inventory commits by module**, not only the `aos-AVP` manifest tag; map release-note claims to exact code commits, dependencies and test cases. Identify already-applied fixes in Supernova before proposing a cherry-pick.
4. **Separate fixes from UI**. Review any Video frontend patch for Supernova-specific layout, typography, hero, Settings, Details, HUD, D-pad and back navigation impacts. Do not overwrite custom resources, navigation or feature logic.
5. **MediaLib schema and data preservation:** inspect migrations, database compatibility, resume/watched state, episode mapping, TMDB IDs, scraping, collections, library backup/restore and custom fork changes. No destructive or silent migration.
6. **Native/ABI:** validate four supported ABIs where relevant, JNI contracts, FFmpeg/swscale vs libyuv, build flags, symbol compatibility, codec coverage, PCM, AC3/EAC3/DTS/TrueHD passthrough, Dolby Vision/HDR and long-uptime behaviour.
7. **Network:** test SMB auth, browsing, share reconnection, seek, cancellation, streaming under throttling, NAS sleep, SFTP, FTP and WebDAV, including credential privacy and correct path encoding.
8. **Security:** review dependency/CVE advisories and actual affected versions independently; upstream release notes alone are not a security assessment. No secrets in logs or test artifacts.
9. **Validation:** Android/unit/native tests, build/lint, repeatable benchmarks, installation over existing signed Supernova, backup/restore, physical Shield remote/focus and playback acceptance; record baseline and after metrics.
10. **Approval:** produce an itemised candidate patch list and review results before implementing on a **new separate branch**. No merge to `main` without user approval.

## Proposed integration waves (future, not authorised)

- **Wave A — critical:** isolated security/stability fixes, buffer safety, serious playback hangs and SMB lockups.
- **Wave B — performance:** SMB/proxy throughput, stream cancellation, A/V synchronisation and decoder improvements; benchmark and regression test.
- **Wave C — optional:** subtitle/audio-speed features, TMDB genre-ID mapping and broader library enhancements only with explicit UX/schema approval.
- **Deferred/excluded:** in-app updater, upstream UI redesigns, bulk `v6.4-lint` replacement, new app identity or signing changes.

## Open questions before an implementation proposal

- Which upstream commits implement each release-note item, and what are their cross-module dependencies?
- Which of those fixes are already present in Supernova's current draft branch or customised MediaLib fork?
- Are there changes to storage schema, native ABI, media codecs, security dependencies or app permissions?
- Which fixes are safe to backport individually, and which require a staged subsystem upgrade?
- What exact playback and network scenarios should be measured on the user's NVIDIA Shield?

**Next deliverable:** a commit-by-commit candidate matrix with exact SHAs, dependency graph, already-present checks, estimated conflict risk and test plan. This document is an initial audit, not a completed code-level backport analysis.
