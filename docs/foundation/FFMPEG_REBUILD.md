# Foundation FFmpeg n8.0.1 corresponding-source rebuild

Status: all four isolated candidate architectures passed source/ABI/licence and native runtime comparisons. Final Foundation APK verification is pending. No signed APK has been built and release signing remains disabled.

## Source and toolchain provenance

Official FFmpeg annotated tag `n8.0.1` is tag object **d22ecc4f6f3fca77b3e71b18641ceddb25973e97**, pointing to source commit **894da5ca7d742e4429ffb2af534fcda0103ef593**. GitHub's tag verification reports a valid Michael Niedermayer signature; the exact API response/payload is preserved in [FFMPEG_SOURCE_PROVENANCE.json](FFMPEG_SOURCE_PROVENANCE.json). This is service-reported signature verification, not a claim of a separate local PGP check. The source archive is pinned by SHA-256 **9dad3557314f1b7923b61aff15e30079e964c3b41276bbd43dd8bb15b8028b5f**. The candidate cannot use the pinned builder's newer n9.0.1 default or the prebuilt commit subject's 8.0.3 claim.

The old ELF configuration records **NDK 30.0.14904198**, Android **API 21**, four architectures and the complete flags. Google's exact **r30-beta1 / 30.0.14904198-beta1** Linux toolchain was downloaded, matched against the official repository checksum, and pinned additionally by SHA-256 in [.github/build/ffmpeg-foundation.lock.json](../../.github/build/ffmpeg-foundation.lock.json). Compiler version/API/features match; the build host changes Darwin to Linux. It is the existing prebuilt's toolchain, not an unrelated dependency upgrade. App/AVOS builds retain their existing NDK 29 setup. Both NDKs' full notices are bundled conservatively.

## Reproduced functionality

Each architecture's command is reconstructed from the actual existing ELF configuration. Only build-root/toolchain/header paths change. Existing ARMv7 NEON/softfp/thumb flags, ARM64, x86 `--disable-asm` and SSE flags, x86_64 SSE4.2/POPCNT, PIC/PIE and 16 KiB linker alignment remain. Shared libraries, broad existing decoder/demuxer/parser coverage, AC3 encoding, SPDIF muxing, swscale/swresample, dav1d, Opus, OpenSSL and mysofa remain. Original disabled programs/devices/other encoders/muxers/bitstream filters/V4L2/Vulkan/bzlib remain disabled. No GPL, GPLv3/LGPLv3 or nonfree configuration is enabled.

Existing dav1d/Opus/mysofa shared libraries and OpenSSL static archives are reused unchanged. Headers come from the observed Opus `2d862ea14b233e5a3f3afaf74d96050691af3cd5` and dav1d `54706fc6bc0cdecab7e9593974a4039cc038fca7` sources, pinned by archive hash. Original wrapper pkg-config metadata and OpenSSL headers remain. No independent codec/crypto version upgrade is introduced.

Two inherited changes are preserved: the Opus configure check links libm, and pinned `atempo.patch` retains both AVOS diagnostic accessors and the output-frontier tracking. The Opus patch is rebased onto exact n8.0.1 surrounding context; its single changed line is identical in effect. Patches apply with zero fuzz. Every original source file is checked against the hashed archive after building, except the two explicitly reviewed patched files, whose exact resulting hashes are locked. No additional source fixes are included.

The source release generates `8.0.1`; the old git build generated `n8.0.1`. All other public headers are byte-identical. Six libraries on each ABI have identical exported symbols (including symbol versions and both atempo accessors), SONAME dependencies and public ABI. [FFMPEG_REBUILD_REVIEWED.json](FFMPEG_REBUILD_REVIEWED.json) locks the baseline hashes/exports/dependencies and every rebuilt feature/component configuration macro. Absolute configure/data-directory paths and three source-traced host documentation/test-transfer probes (`HAVE_MAKEINFO`, `HAVE_MAKEINFO_HTML`, `HAVE_RSYNC_CONTIMEOUT`) are excluded from macro hashing. These probes appear only in configure/doc/tests Makefile logic and have no C/header/assembly library references; all runtime/component/licence macros remain strict. Runtime registries independently compare against the original binaries.

## Regression evidence and limits

[FFMPEG_NATIVE_REGRESSION.json](FFMPEG_NATIVE_REGRESSION.json) records **64 successful baseline/candidate decode comparisons**: 16 cases on each ABI. Synthetic media covers H.264/AAC MP4, HEVC MKV, AV1 MKV, VP9/Opus WebM, MPEG2/AC3 TS, Opus, Vorbis, FLAC, PCM, MP3, ALAC, EAC3, DTS and 5.1 AC3, plus explicit libdav1d and libopus paths. Decoder names, frame/sample counts and decoded-byte checksums match within each architecture. Every codec/filter/demuxer/muxer/protocol registry row matches. The runtime tempo change sequence 1.25 → 0.75 → 2.0 matches sample checksum/count and all 101 inherited AVOS diagnostic state snapshots.

Tests run with official checksum-verified Android API 23 bionic/linker files, x86_64 directly and other ABIs via QEMU user emulation. The test container has no network, signing environment or user media; legacy 32-bit Android emulation requires the personality syscall. This exercises Android native libraries, not an Android framework UI, NVIDIA hardware decoder, HDMI passthrough, live network integration or physical Shield playback. Those checks remain pending after the signed APK, as authorised. No device success is fabricated.

## Build, modify and relink

Use the repository's exact Foundation source commit and pinned module manifest, `.github/build/nova-ci.xml`. Recreate sibling repositories and inherited dependency patches using the protected workflow's **secret-free dependency preparation**. No signing key is needed for source rebuilding or an unsigned APK. Install build-essential, curl, nasm, pkg-config, unzip, ffmpeg and Docker. Existing pinned native dependencies must be present.

From the Video repository:

```sh
python3 .github/build/build-foundation-ffmpeg.py --root .. --workspace /absolute/fresh/ffmpeg-work
python3 tools/verify_foundation_native_runtime.py --root .. --workspace /absolute/fresh/ffmpeg-work \
  --test-workspace /absolute/fresh/native-tests
python3 .github/build/build-foundation-ffmpeg.py --root .. --workspace /absolute/fresh/ffmpeg-work \
  --verify-existing --install --source-bundle
./gradlew assembleNoamazonRelease -PfoundationVerify -Puniversal --no-daemon
```

The isolated candidate never overwrites the prebuilt tree until all four ABI/source/feature/licence guards pass and runtime evidence is bound to those exact candidate hashes. Missing/changed source, patch, public symbol, dependency, header, component or regression evidence refuses installation. The protected workflow repeats these checks before receiving any signing secrets. A further guard verifies all **24 actual APK FFmpeg libraries** against the rebuilt inputs before key preparation and again before publication.

For your own modified library build, start from the supplied source and recipes, deliberately update/review relevant comparison baselines, rebuild the app and sign your own installation with your own identity. The Foundation owner's private key is unnecessary and is never distributed. Preserve library notices and provide corresponding source under the applicable licences. Reverse engineering to debug library modifications is permitted under LGPL terms.

## Source availability

The protected workflow prepares `Supernova-Foundation-FFmpeg-Source.tar.xz`: complete patched FFmpeg source, both patches, source lock, build recipe, pinned module manifest, exact four-ABI configuration and provenance. A separate allowlisted source artifact accompanies the verified APK; source URLs, immutable commits and all patch/recipe files remain available in the repository after Actions artifacts expire. Existing static OpenSSL 3.5.7 source commit **8cf17aaeb4599f8af87fefd810b5b5fee90fe69e**, archive URL/hash and build provenance remain explicit in the lock/companion metadata; OpenSSL binaries are unchanged. Original source licences/full notices are retained, including both NDK versions. This records actual source availability and verification, not a legal certification or device acceptance claim.

## Keyless CI rehearsal

The existing manual Foundation workflow also accepts `operation=verify` on this branch. It runs the exact pinned dependency preparation, canonical source rebuild, four-ABI native regressions, unsigned app unit/lint/assembly and actual APK checks. Signing-secret preflight/inspection/preparation/signing steps do not execute for this mode; readiness is not enabled. It uploads public conformance/native evidence only, **no APK or private material**. This permits checking the complete CI path before the separate signing approval. The `prepare` mode retains protected secret, public pin, branch and readiness checks. No workflow registration/merge on main is required; dispatch against the existing registered workflow and this branch:

```sh
gh workflow run foundation-signing.yml --ref codex/foundation-release -f operation=verify
```

### CI host-tool difference diagnosis

Cold rehearsal 37926791331 failed closed on generated macro hashes before installation. Diagnostic rehearsal 37929001285 identified only the three host documentation/rsync probes above (local 0, runner 1). A second long diagnostic run was cancelled after early configuration checks were added. Source tracing showed these probes cannot change the compiled libraries; their values are retained separately in reviewed evidence. A regression test proves host-only changes are accepted while a codec-feature change remains refused. Python tools now pass 36 tests. The complete canonical keyless run must still pass before signing approval. No runtime feature/configuration baseline was relaxed.
