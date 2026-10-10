# Supernova — NOVA 6.5.5 integration decision matrix and validation plan
**2026-10-11 · Research-only deliverable · No source changes authorised**

Companions: [initial audit](NOVA_UPSTREAM_COMPATIBILITY_AUDIT_6.4.64_TO_6.5.5_2026-10-11.md), [phase 2 source findings](NOVA_UPSTREAM_SOURCE_AUDIT_PHASE2_2026-10-11.md).

## Critical FFmpeg correction
**The v6.5.5 release-tag manifest `aos-AVP/v6_4.xml` references `ffmpeg-android-builder-nobin@v8_0` and `prebuilt-ffmpeg@v8_0`, not v9.x.** The Supernova main manifest pins the builder at `49b63bcec9125f96b327d055bfc49875c07f3ce4` and prebuilt binaries at `6ba0e9340cf725bede2e2f29d30c290a2ab8130e`. Comparing builder pin to v8_0 shows four commits (build.sh and three patches). Comparing prebuilt pin to v8_0 shows 1 ahead/1 behind, indicating divergent binary histories. **Neither branch label establishes the precise FFmpeg patch version inside an APK.** Read build.sh source and binary av_version_info()/ffmpeg -version from reproducible build before asserting it. Previous conversational statement that NOVA 6.5.5 uses FFmpeg 9.0.1 is unsupported by this manifest and must not be used as an implementation premise. Upgrading independently to 9.x is **not** authorised.

Sources:
- https://github.com/nova-video-player/aos-AVP/blob/v6.5.5/v6_4.xml
- https://github.com/heretoecode/Supernova/blob/main/.github/build/nova-ci.xml
- https://github.com/nova-video-player/ffmpeg-android-builder-nobin/compare/49b63bcec9125f96b327d055bfc49875c07f3ce4...v8_0
- https://github.com/nova-video-player/prebuilt-ffmpeg/compare/6ba0e9340cf725bede2e2f29d30c290a2ab8130e...v8_0

## Five-step audit status
| Requested step | Status | Finding / next evidence |
|---|---|---|
| 1. Commit-by-commit history | **Partially complete** | Four component ancestry comparisons and 16 candidate search hits documented; exhaustive ordered history, reverts and full SHAs not yet certified |
| 2. Supernova comparison | **Partially complete** | Main manifest pinned; custom MediaLib and current refinement/QA provenance not yet diffed |
| 3. FFmpeg/dependencies | **Manifest verified, binary pending** | NOVA v6.5.5 explicitly references v8_0 branches; builder and prebuilt histories differ; precise compiled patch version unresolved |
| 4. Integration matrix | **Provisional** | See below; decisions are recommendations only |
| 5. Staged testing plan | **Drafted** | Tests and exit criteria below; no builds or device validation |

## Candidate integration matrix
| Group | Candidate / provenance | Benefit | Risk | Proposed decision |
|---|---|---|---|---|
| P1 native deadlocks and stream safety | AVOS native fixes; e.g. `2fd1e0d220ba`, `cf0b003ea825`; buffer limits | Avoid hangs/crashes | High: native concurrency and ABI | **Investigate isolated backports first**, verify v6.5.5 ancestry |
| P2 playback sync and seeks | `0c3a50b36e80`, `fe57ba1adf62`, `b4142fb88515`, `f3e2deebd253`, `b8d7b800338a` | Improve pause/resume/PCM/sync | High: tightly coupled native state | **Adapt as coherent patch group** after dependency graph |
| N1 SMB freezes and readiness | `ad49a7916e40`, `673f381428ac`, `4cdc574b7b66`, `7cd2eb4bd703` | Improve NAS/SMB stability | Medium/high: library and credential changes | **Investigate then stage network-only tests** |
| N2 SMB URI/credential correctness | `78076e738640`, `476b444c28ee` | Correct unusual paths and usernames | Medium: URL semantics | **Candidate isolated fixes** with tests |
| N3 HTTP proxy/FTP(S)/WebDAV | FileCoreLibrary changes | Streaming reliability | Medium/high: network cancellation/auth | **Adapt after exact diff/test review** |
| M1 TMDB genre IDs, DB v62 | `552d52b83514`, `3d3226d99468` | Stable genre metadata across locales | **High: custom MediaLib schema and backups** | **Defer pending schema migration audit and user approval** |
| F1 FFmpeg and colour conversion | v8_0 builder/prebuilt branch, FFmpeg swscale/libyuv removal | Compatible newer native pipeline | **High: ABI/native dependencies** | **Treat as coherent native migration; no independent FFmpeg bump** |
| U1 NOVA UI and in-app updater | All UI resources/screens and updater | None required for Supernova | Unacceptable product regression | **EXCLUDE categorically** |

The short commit IDs are upstream search results, not certified included in the release snapshot. This matrix must not be interpreted as an approved cherry-pick list.

## Staged future implementation proposal (requires user authorisation)
**Stage 0 — provenance and reproducibility:** freeze baseline APK SHA, manifest pins, native library hashes, actual FFmpeg version, test fixtures and regression metrics; inspect custom MediaLib branch and full diffs.
**Stage 1 — narrow reliability fixes:** verified buffer hardening, deadlocks, SMB credential/path bugs, each isolated where feasible; baseline/after tests and easy revert.
**Stage 2 — coherent native playback upgrade:** AVOS and matching FFmpeg native dependencies, swscale/libyuv transition only if necessary, validate JNI/ABI and Shield codec/output.
**Stage 3 — network streaming upgrade:** SMB, HTTP proxy, FTP(S), WebDAV with repeatable NAS/network tests.
**Stage 4 — optional library enhancements:** genre IDs and schema migrations only after explicit data preservation proof and approval.
No upstream UI, no in-app updater, no automatic merge.

## Acceptance criteria and physical NVIDIA Shield test plan
1. **Identity and upgrade:** retain app ID, signature, data, backup format; upgrade existing signed APK without losing sources, resume, watched, playlists/collections or settings.
2. **UI invariants:** automated resource/layout diff + human Shield D-pad sweep for home, navigation, settings, search, network/files, custom libraries, Details and playback HUD; zero NOVA UI import.
3. **Native audio:** PCM, AC3, EAC3, DTS, TrueHD passthrough where hardware supports; rapid pause/play, 20 repeated forward/back seeks, speed transitions, audio-track switching and long sessions.
4. **Video:** H.264, HEVC, AV1 where supported, HDR/Dolby Vision where available, seeking near end, subtitle sync and colour accuracy; no regressions versus baseline.
5. **Network:** SMB authentication and reconnection, NAS sleep/wake, '#'/ '@' filenames and usernames, SMB dialect selection, throttled seeks, HTTP range/unknown length, cancellation, WebDAV redirects, FTP(S)/SFTP.
6. **Data and schema:** old/new MediaLib DB versions, rollback strategy, scan/rescan, TMDB genres, resume/watched and backup/restore round-trip with unavailable USB/network sources.
7. **Reliability/security:** ANR and unclean-exit logs, no credential leakage, memory trends, long-duration playback, performance comparisons with same files/network conditions.
8. **Release gates:** native/build tests and ABI checks, review of every changed file, user approval of scope, Shield QA APK, user acceptance before merge.

## Explicit remaining blockers
- Cannot claim exhaustive commit-by-commit audit: full ordered history and per-commit ancestry/revert checks remain outstanding.
- Cannot claim FFmpeg 8.0.x patch version, or that any 9.x FFmpeg is bundled, until actual source/binary checks.
- Cannot claim current active Codex APK uses main's manifest without provenance.
- Cannot claim the custom MediaLib fork is compatible with upstream DB v62.
- Cannot claim a performance/security fix is present, absent or safe based only on release notes or commit messages.

**Decision:** Keep this plan as a non-invasive review artifact. No implementation, branch merge, CI edits or Codex handover is authorised by this audit.
