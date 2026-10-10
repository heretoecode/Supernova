# Supernova — NOVA upstream source audit, phase 2 (6.4.64 → 6.5.5)
Date: 2026-10-11
Status: **RESEARCH ONLY / INCOMPLETE COMMIT-LEVEL VERIFICATION / NO INTEGRATION AUTHORISED**
Companion: [Initial compatibility audit](NOVA_UPSTREAM_COMPATIBILITY_AUDIT_6.4.64_TO_6.5.5_2026-10-11.md)

## User-approved guardrails
- **Absolutely no upstream UI import**, including layouts, resources, navigation, focus, settings, Details, HUD, or presentation logic. Review technical changes in UI-bearing files only as evidence; rewrite isolated nonvisual fixes if indispensable, subject to separate approval.
- **Exclude NOVA's in-app updater entirely.** No hidden updater dependencies or permissions.
- Initially follow **the exact FFmpeg revision and native dependency set used by NOVA's selected stable baseline**, rather than independently bumping to a newer FFmpeg patch release during the same integration.
- Do not touch Codex's active refinement branch, PR #10, its APK build, signing, app ID, or main. This report is on a separate docs-only branch.
- User must approve an itemised implementation plan later; do not infer implementation approval from audit authorisation.

## Verified component topology and comparison (GitHub compare API)
The pinned starting points below were documented from Supernova main's `.github/build/nova-ci.xml` in the initial audit, **not** verified as the exact current in-progress QA APK manifest. The NOVA v6.5.5 manifest selects `v6.4-lint` for four component repositories.

| Component | Supernova main pin / upstream reference | Compared to upstream v6.4-lint | Ahead | Key files surfaced |
|---|---|---|---:|---|
| aos-Video | 0651a3e0b60ada996a9774ab527d7c1ef34b7503 | pinned commit | 86 | AndroidManifest.xml, build.gradle, resources, Java playback/UI files (300-file API cap possible) |
| aos-FileCoreLibrary | c4b760c55102c72d45f68b0d16999fc1f4f013a7 | pinned commit | 46 | StreamOverHttp, NetworkInitialization, ConnectionLocks, FTPFile2, tests |
| aos-avos | 2cf21c486c7bf244c49b78d2220197a6d75396dd | pinned commit | 60 | native stream/sync/audio/video headers and implementations |
| upstream aos-MediaLib | upstream v6.4 branch (not Supernova fork) | branch comparison | 22 | AvosMediaPlayer, SmbProxy, scanner, XmlDb, ScraperStore, tests |
| Supernova custom MediaLib | heretoecode/aos-MediaLib@5758074049bc153ac4967b4ecd21886469270076 | **not yet compared** | unknown | preserve custom features and database |

All four API comparisons above reported zero behind for the **specified starting refs**. This does not negate the older branch-to-branch AVOS comparison showing divergence: those are different comparison bases. Compare results describe ancestry and changed files, not patch portability.

NOVA aos-AVP v6.4.64 → v6.5.5 is only four manifest/build commits; using this count as a measure of application changes would be incorrect. Upstream v6.5.5 drops libyuv in favour of FFmpeg swscale; this may necessitate a coordinated native build migration.

## Commit candidates discovered in upstream search
**Important:** These are exact upstream search hits, **not yet certified as ancestors of the v6.5.5 component snapshot, nor safe cherry-picks**. Verify full SHA, ancestry, diff, tests and cross-module dependencies before selecting them.

| Area | Candidate upstream commit prefix | What its commit message claims | Required verification |
|---|---|---|---|
| Native sync | `0c3a50b36e80` (aos-avos) | Preserve Mode 1 sync recovery through rapid pauses | ancestry, playback-mode applicability, AVOS dependencies |
| Native sync | `fe57ba1adf62` (aos-avos) | Restore Android platform-timed sync engine | JNI/MediaCodec and output path |
| Seeking | `ae006e5f980d` (aos-avos) | Prevent backwards seek jumping forwards at altered speed | stream seek regression |
| Native ANR | `2fd1e0d220ba` (aos-avos) | Resolve videosink lock-order inversion | thread locking and build tests |
| PCM sync | `b4142fb88515` (aos-avos) | Preserve PCM sync through calibration/speed changes | passthrough and PCM test matrix |
| PCM sync | `f3e2deebd253` (aos-avos) | Preserve PCM correction through rapid pauses | mode-specific audio tests |
| PCM seek | `cf0b003ea825` (aos-avos) | Break PCM post-seek startup hold deadlock | seek/pause/resume |
| Resume seek | `b8d7b800338a` (aos-avos) | Align initial sync to achieved seek position | resume position tests |
| SMB init | `4cdc574b7b66` (FileCoreLibrary) | Coordinate SMB access with credentials and crypto init | credential privacy, boot/wake |
| SMB freeze | `ad49a7916e40` (FileCoreLibrary) | jcifs-ng update for multicredit SMB freeze | dependency version, SMB dialects |
| SMB freeze | `673f381428ac` (FileCoreLibrary) | jcifs-ng multicredit/locale regression fixes | overlap with preceding candidate |
| SMB protocols | `7cd2eb4bd703` (FileCoreLibrary) | Automatic SMB dialect selection | server compatibility |
| SMB paths | `78076e738640` (FileCoreLibrary) | Preserve '#' in SMB path segments | URI escaping and path round trips |
| Credentials | `476b444c28ee` (FileCoreLibrary) | Preserve '@' in usernames | credential parsing regressions |
| Genre schema | `552d52b83514` (MediaLib) | Persist TMDB genre IDs, database v62 | **schema migration, custom fork, backup compatibility** |
| Genre locale | `3d3226d99468` (MediaLib) | Translate genre via TMDB ID | requires schema/locale review |

Links: https://github.com/nova-video-player/aos-avos/commits/v6.4-lint ; https://github.com/nova-video-player/aos-FileCoreLibrary/commits/v6.4-lint ; https://github.com/nova-video-player/aos-MediaLib/commits/v6.4-lint

## Coverage and decisions
1. **Release timeline:** examine 6.4.66 pre-release, 6.4.71, 6.4.72, 6.5.1 pre-release, 6.5.2, 6.5.3, 6.5.5. Release labels alone do not prove that every earlier pre-release patch survived unchanged; verify ancestry and final snapshots.
2. **Native playback and audio:** prioritize A/V sync, passthrough hangs, seeking, pause/resume, buffer overflow hardening, speaker routes and long-running sessions.
3. **Networking:** SMB freezes/initialisation, proxy streaming, cancellation, FTP(S), SFTP, WebDAV, auth/path handling; test real Shield/NAS workloads and keep credentials out of diagnostics.
4. **MediaLib:** database v62 genre change is a potential migration, not an automatic patch; preserve user library, watched/resume history, custom collections, metadata and backup/restore compatibility.
5. **FFmpeg:** verify the actual compiled version from native build pins and binary, not just release notes. Use NOVA's compatible revision first; independently upgrading FFmpeg is a **separate future decision**.
6. **Security:** inspect actual dependency advisories, native buffer changes, threat reachability and affected version ranges before assigning CVE severity.
7. **No UI/updater:** enforce as explicit patch-selection gates, not merely visual QA goals.

## Proposed integration sequence — NOT AUTHORISED
- A: isolated critical security/native hang fixes after dependency and applicability checks.
- B: coherent playback/FFmpeg/native engine changes, keeping NOVA's own tested dependency combination.
- C: networking and SMB/proxy improvements with measured regressions.
- D: library/subtitle/metadata changes only after schema, backup and custom fork compatibility review.
- Excluded: all NOVA UI, in-app updater, wholesale component replacement.

## Tests to require before implementation acceptance
Clean and upgrade installation over signed Supernova; four ABI compile/packaging where supported; Shield PCM and passthrough formats, seeking and resume, repeated pause/play, subtitle timing, HDR/Dolby Vision where available; NAS SMB reconnect/sleep, large-file seeking, credentials and unusual filenames; database upgrade and backup restore, watched history; no UI diffs or changed focus routes; long-duration performance; local privacy-safe diagnostic capture.

## Remaining verification / no premature completion claim
- Resolve current signed QA APK provenance and compare **its** component pins, not only main.
- Enumerate **all** commits from each pinned SHA to the exact v6.5.5 snapshot; current compare API supplies counts/files but not the full ordered commit log. Search hits above are only candidate leads.
- Verify full commit SHAs and whether each candidate is included in v6.5.5; group reverts and superseding fixes.
- Diff Supernova's MediaLib fork and custom Video implementation; identify already present fixes.
- Determine exact FFmpeg source/version, native flags, ABI/JNI contracts and dependency compatibility.
- Produce final commit-by-commit dependency/patch matrix and quantified risks before any implementation authorisation.

**Outcome:** The component scope and promising patch candidates are now documented, but a full exhaustive source audit is **not yet complete**. This document is a research checkpoint, not a claim that upstream code can safely be integrated today.
