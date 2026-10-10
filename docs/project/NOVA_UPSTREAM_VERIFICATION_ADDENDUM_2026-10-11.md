# Supernova — source verification addendum (2026-10-11)
**Research only. No application changes or implementation approval.**

## Important correction to earlier FFmpeg interpretation
The NOVA v6.5.5 manifest selects branches named `v8_0` for the FFmpeg builder and prebuilt repo. **These branch names do not describe the actual FFmpeg version.** Direct inspection of both the v8_0 builder's `build.sh` and Supernova's pinned builder's `build.sh` shows the active clone argument `-b n9.0.1`. The v8_0 prebuilt's `dist-full-arm64-v8a/include/libavutil/version.h` reports **LIBAVUTIL 61.1.101**, consistent with FFmpeg 9.x; Supernova's pinned prebuilt header reports **LIBAVUTIL 60.8.100**, consistent with FFmpeg 8.x. Therefore:
- The **builder scripts target FFmpeg 9.0.1** on both refs.
- The **prebuilt header generations differ**: NOVA v8_0 is FFmpeg 9-era; Supernova's pinned prebuilt is FFmpeg 8-era.
- The *actual packaged APK native libraries* have not been inspected; do not equate a builder script with the installed binary.
- Prior document's suggestion that v8_0 branches prove FFmpeg 8 is **incorrect and superseded by this addendum**. Prior conversation's assertion that Supernova definitely runs FFmpeg 8.0.3 is also **not binary-verified**.
- Initial recommendation remains: use the coherent NOVA 6.5.5 native library set as the reference, no independent 9.0.2 upgrade at the same time.

Sources:
- https://github.com/nova-video-player/ffmpeg-android-builder-nobin/blob/v8_0/build.sh
- https://github.com/nova-video-player/ffmpeg-android-builder-nobin/blob/49b63bcec9125f96b327d055bfc49875c07f3ce4/build.sh
- https://github.com/nova-video-player/prebuilt-ffmpeg/blob/v8_0/dist-full-arm64-v8a/include/libavutil/version.h
- https://github.com/nova-video-player/prebuilt-ffmpeg/blob/6ba0e9340cf725bede2e2f29d30c290a2ab8130e/dist-full-arm64-v8a/include/libavutil/version.h

## Active refinement manifest provenance
Direct read of `heretoecode/Supernova:.github/build/nova-ci.xml` at `codex/supernova-next-refinement-2026-10-10` matches the component revision pins in `main` (including the customised MediaLib, AVOS, Video, FileCoreLibrary, FFmpeg builder and FFmpeg prebuilt). This verifies **manifest pin parity**, not necessarily the exact native binaries installed in a generated QA APK or local modifications in other repositories.

## Custom MediaLib inspection
The custom MediaLib at `5758074049bc153ac4967b4ecd21886469270076` is accessible. Its `XmlDb.java` and upstream `v6.4-lint` `XmlDb.java` have distinct blob SHAs. Upstream `v6.4..v6.4-lint` has 22 commits, including changes to `XmlDb.java`, `AvosMediaPlayer.java`, `SmbProxy.java`, `NetworkScannerServiceVideo.java` and `ScraperTables.java`. These are potential collision surfaces, not yet proof of conflicts. The database-v62 migration claim comes from an upstream commit message and must be checked against actual custom schema definitions and upgrade logic before adoption.

## Updated decision gates
1. Determine actual FFmpeg version from the built APK's `libavutil.so` (or runtime `av_version_info()`) and compare against the precise NOVA v6.5.5 prebuilt binary; inspect all ABIs and hashes.
2. Diff custom MediaLib against its true upstream merge base; review DB schema/migrations, resume, watched state, SMB proxy and scanner.
3. Enumerate ordered per-component commits and ancestry/reverts; release-note and search-result lists alone are not exhaustive.
4. Map each proposed fix to exact full SHA and dependent files before classifying Adopt/Adapt/Defer/Exclude.
5. Keep upstream UI and updater categorically excluded; keep Codex branch, build, signing, main and PR untouched.

**Status:** These checks improve provenance and expose a material FFmpeg version mismatch risk, but do **not** complete exhaustive commit analysis, custom fork diff or binary validation. No safe-to-merge claim is made.
