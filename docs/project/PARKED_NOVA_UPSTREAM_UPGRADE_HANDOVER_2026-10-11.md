# PARKED — NOVA upstream upgrade research and Supernova handover
**Recorded:** 2026-10-11  
**Status:** PARKED BY USER — RESEARCH INCOMPLETE — NO IMPLEMENTATION AUTHORISED  
**Project:** `heretoecode/Supernova`  
**Upstream comparison:** NOVA Video Player 6.4.64 → 6.5.5, including intervening pre-releases

> This is the durable restart point for a later discussion or Codex research task. It is **not** an instruction to implement changes, merge a branch, alter CI, or interrupt active refinement development.

## 1. Objective and decisions already made

Supernova derives from NOVA Video Player, but has its own customised interface and application behaviour. The objective is to selectively adopt **upstream bug fixes, stability/security improvements, native playback and networking enhancements** without overwriting Supernova's product identity.

User-approved boundaries:
1. **Exclude every upstream NOVA UI/presentation change**, including home, Details, settings, navigation, D-pad focus, playback HUD, resources/layouts and visual interaction. A useful underlying nonvisual fix in a UI-bearing file must be isolated/adapted and independently approved.
2. **Exclude NOVA's in-app updater** completely, including permissions and hidden dependencies.
3. For the initial native integration, use the **coherent FFmpeg and native dependency combination proven in NOVA 6.5.5**, rather than independently selecting a newer FFmpeg patch at the same time.
4. Audit and documentation work were approved; **implementation, cherry-picks, new builds, merges and PR handover were not**.
5. Do not interfere with Codex's active refinement branch `codex/supernova-next-refinement-2026-10-10`, PR #10, signing, branding, APK build or `main`.

## 2. Existing research documents — read all before resuming

- [Initial upstream compatibility audit](NOVA_UPSTREAM_COMPATIBILITY_AUDIT_6.4.64_TO_6.5.5_2026-10-11.md) — high-level change categories, baseline pins, UI boundaries.
- [Phase 2 source audit](NOVA_UPSTREAM_SOURCE_AUDIT_PHASE2_2026-10-11.md) — four component comparisons, candidate commit messages, unresolved dependencies.
- [Integration matrix and Shield QA plan](NOVA_6_5_5_INTEGRATION_MATRIX_AND_QA_PLAN_2026-10-11.md) — **provisional** Adopt/Adapt/Defer/Exclude matrix, staged validation.
- [Verification addendum](NOVA_UPSTREAM_VERIFICATION_ADDENDUM_2026-10-11.md) — authoritative correction of misleading FFmpeg branch names, active refinement manifest parity.

**Interpretation rule:** Where older documents imply the branch label `v8_0` proves FFmpeg 8, the verification addendum supersedes them. No document claims that all source commits, custom forks, and APK binaries have been fully audited.

## 3. Confirmed technical findings

### Manifest and component scope
Supernova main `.github/build/nova-ci.xml` pins:
- `aos-AVP@9c5842b74ab180c99441ed23fdd5d508448388a7` (NOVA 6.4.64 era)
- `aos-Video@0651a3e0b60ada996a9774ab527d7c1ef34b7503`
- `aos-FileCoreLibrary@c4b760c55102c72d45f68b0d16999fc1f4f013a7`
- `aos-avos@2cf21c486c7bf244c49b78d2220197a6d75396dd`
- **Custom fork** `heretoecode/aos-MediaLib@5758074049bc153ac4967b4ecd21886469270076`.

Direct read of the same manifest on `codex/supernova-next-refinement-2026-10-10` showed **the same component pins**. This is manifest parity only, not proof of the contents of the currently installed QA APK.

NOVA v6.5.5 `aos-AVP/v6_4.xml` selects `v6.4-lint` branches for Video, FileCoreLibrary, AVOS and MediaLib. GitHub comparisons from Supernova's pinned revisions to upstream `v6.4-lint` showed **86 Video, 46 FileCoreLibrary and 60 AVOS commits ahead**, zero behind for those exact comparison bases. Upstream MediaLib `v6.4..v6.4-lint` had **22 ahead**, but this is **not** a comparison against Supernova's custom fork. Counts are time/ref-dependent; freeze exact target SHAs when resuming.

### FFmpeg — important version clarification
- NOVA v6.5.5 manifest references FFmpeg builder and prebuilt branches named **`v8_0`**.
- **Both** NOVA's `v8_0` builder script and Supernova's pinned builder script actually clone FFmpeg tag **`n9.0.1`**.
- NOVA's `v8_0` prebuilt arm64 `libavutil/version.h` reports **61.1.101**, an FFmpeg 9-era library version.
- Supernova's pinned prebuilt arm64 header reports **60.8.100**, an FFmpeg 8-era library version.
- The builder script and prebuilt header therefore describe **different source/binary generations within Supernova's manifest**. This is a compatibility investigation lead, **not proof that a broken binary is installed**.
- The precise FFmpeg binary packaged in the actual Supernova QA APK **has not been inspected**. Inspect `libavutil.so`, `av_version_info()`, binary hashes, build provenance, all shipped ABIs and JNI contracts before making version or safety claims.
- Do **not** automatically upgrade to FFmpeg 9.0.2 or copy individual FFmpeg files without the compatible AVOS/colour conversion dependency set.

### Upstream technical opportunities
- AVOS native playback: PCM/bitstream synchronisation, rapid pause/resume, seek deadlocks, long-running playback, buffer hardening, passthrough freezes, native ANRs.
- FileCoreLibrary: SMB credentials/crypto readiness, jcifs-ng changes, dialect selection, unusual path characters, HTTP proxy/cancellation, FTP(S), WebDAV and streaming resilience.
- MediaLib: scanner/proxy/player changes and TMDB genre-ID storage, with a potential **database v62 migration** affecting watched/resume/library and backup compatibility.
- Native colour conversion: upstream manifest removes `aos-libyuv` while using FFmpeg swscale; treat this as a **coordinated migration**, not an isolated deletion.
- Upstream UI and in-app updater are **excluded regardless of upstream release notes**.

## 4. Exact commits examined at source level

The following were fetched from upstream GitHub with full SHAs, message, touched-file list and source diff. **Their ancestry in the final 6.5.5 component snapshot and suitability for Supernova have NOT been certified.**

| Repo | Commit | Verified source behaviour | Caution |
|---|---|---|---|
| `aos-avos` | [`cf0b003ea825aa20dfdd8c9ecbe693d893570364`](https://github.com/nova-video-player/aos-avos/commit/cf0b003ea825aa20dfdd8c9ecbe693d893570364) | Changes `Source/stream_sync.c` to admit forward video while PCM audio startup is waiting after a seek, breaking a circular wait | Depends on surrounding `put_time_mode`, `audio_start_pending`, target timestamp and other sync refactors |
| `aos-FileCoreLibrary` | [`4cdc574b7b66850d93153752a9a1e5e5fa353782`](https://github.com/nova-video-player/aos-FileCoreLibrary/commit/4cdc574b7b66850d93153752a9a1e5e5fa353782) | Adds shared background SMB crypto/credential initialisation, bounded waits, retries and concurrency tests across seven files | Must verify dependencies, Android threading, credentials privacy and SMB lifecycle |
| `aos-MediaLib` | [`552d52b835144da8a3102740450c2c50f4377373`](https://github.com/nova-video-player/aos-MediaLib/commit/552d52b835144da8a3102740450c2c50f4377373) | Adds `m_genre_ids`/`s_genre_ids`, v62 schema migration and locale-independent genre backfill, plus tests | High migration risk for custom MediaLib, user data, backups and rollback |

Other **commit-message search leads, not source-verified or inclusion-certified**:
- AVOS: `0c3a50b36e80`, `fe57ba1adf62`, `ae006e5f980d`, `2fd1e0d220ba`, `b4142fb88515`, `f3e2deebd253`, `b8d7b800338a`.
- FileCoreLibrary: `ad49a7916e40`, `673f381428ac`, `7cd2eb4bd703`, `78076e738640`, `476b444c28ee`.
- MediaLib: `3d3226d99468`.

## 5. Proposed prioritisation — not approved implementation

| Workstream | Provisional direction | Principal risk |
|---|---|---|
| Native deadlock, ANR and buffer hardening | Investigate isolated backports first | Concurrency, native ABI and regression |
| Playback sync, seek, pause/resume and passthrough | Adapt as coherent, version-matched group | Interdependent AVOS/audio state |
| SMB credential, connection and unusual-path fixes | Prefer narrow changes with NAS tests | Auth, dialect and networking regressions |
| HTTP proxy, FTP(S), WebDAV and streaming | Stage separately after exact diff | Network cancellation/stream semantics |
| FFmpeg, swscale and native codecs | Coherent NOVA 6.5.5 baseline, not independent patch bump | Prebuilt/builder mismatch, native JNI/ABI |
| MediaLib genre IDs / database v62 | **Defer** until schema and backup proof | Data loss, migration, rollback |
| NOVA UI, in-app updater | **Exclude** | Violates user decisions |

## 6. Work remaining before declaring audit COMPLETE

**These are hard blockers, not optional polish:**

1. **Freeze actual source snapshots:** resolve immutable SHAs for all `v6.4-lint` component heads used by the NOVA 6.5.5 build, not merely moving branch names. Confirm release tag-to-component provenance.
2. **Exhaustive commit history:** enumerate ordered commits from each Supernova baseline to target across Video, FileCoreLibrary, AVOS, MediaLib and native builder/prebuilts; read actual diffs, group dependencies, identify reverts/superseding changes, and mark inclusion in target snapshot. Existing counts/search hits are not exhaustive.
3. **Supernova fork comparison:** find true common ancestor of `heretoecode/aos-MediaLib` and upstream; diff custom database schema/migrations, scraper, scanner, SMB proxy, playback bridge, collections and backup behaviour. Compare Supernova-specific Video changes too, not just manifests.
4. **Binary and build verification:** retrieve actual signed Shield QA APK or exact CI artifact and its commit/run ID; inspect native `.so` version strings, ABI, checksums, build flags and JNI interfaces. Compare with reproducible NOVA 6.5.5 artifacts; resolve FFmpeg builder/prebuilt discrepancy without assumptions.
5. **Precise patch matrix:** for every proposed change record full SHA, touched files, dependencies, inclusion/revert status, whether already present, Adopt/Adapt/Defer/Exclude recommendation, rollback approach and test evidence.
6. **Tests:** baseline vs candidate native builds, static checks, all supported ABIs, real NVIDIA Shield PCM/passthrough/seek/resume/long-play, SMB/NAS credentials/reconnect, HTTP/WebDAV, subtitle/HDR where supported, database migration/backup restore, app signing/identity and **zero upstream UI changes**.
7. **Decision gate:** present the final report to the user and obtain explicit authorisation before any integration branch, Codex implementation brief, code edits, APK build or merge.

## 7. How to resume this parked investigation

Start by reading this document and its four companion files on the **separate docs-only branch** `docs/nova-upstream-deep-audit-2026-10-11`. Use repository history and build artifacts as authoritative, not previous assistant claims. Perform **research/documentation only** until the user explicitly un-parks the investigation and authorises implementation. Keep the ongoing refinement release independent.

A complete result should consist of a frozen source/ABI baseline, a verified per-commit patch dependency matrix, custom-fork conflict assessment, FFmpeg binary provenance, a Shield test plan with measured evidence, and a user-facing recommendation. If any source or artifact is unavailable, label that limitation; **never report the audit as complete merely because a plan has been written**.

## 8. Repository preservation and scope

Research commits already recorded on the separate docs-only branch include:
- Initial audit on main: [`75d9f012fa4f65869eef5d972ad4725df504bd9c`](https://github.com/heretoecode/Supernova/commit/75d9f012fa4f65869eef5d972ad4725df504bd9c).
- Phase 2 checkpoint: [`a9a7fececf4109d5ef5dc7f29bdf0a48e5faf037`](https://github.com/heretoecode/Supernova/commit/a9a7fececf4109d5ef5dc7f29bdf0a48e5faf037).
- Integration matrix: [`3a54a7bdc476872a9e2547f4174b0b68c948c4bb`](https://github.com/heretoecode/Supernova/commit/3a54a7bdc476872a9e2547f4174b0b68c948c4bb).
- FFmpeg verification addendum: [`244fd5cf38119e1c5336bf664fde5d988ae7b61c`](https://github.com/heretoecode/Supernova/commit/244fd5cf38119e1c5336bf664fde5d988ae7b61c).

**Final status: PARKED; documentation preserved; no integration authorised; current Codex refinement unaffected.**
