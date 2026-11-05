# Supernova 0.145 / 145 — signed QA candidate

[Download verified signed APK](https://drive.google.com/file/d/10HmVAOilIRsT8iRcyLyOktz_vgS7uCeM/view?usp=drivesdk) · [Protected QA run](https://github.com/heretoecode/Supernova/actions/runs/40762577939) · [Draft PR #25](https://github.com/heretoecode/Supernova/pull/25).

Preserves Supernova's custom interface, stored libraries/settings, application identity and existing signing certificate on the independently verified 0.144 baseline. NOVA stable target: v6.6.2; seven compatible ports improve failed-network reopening/latest seek, paused subtitle timing, HDMI registration, private suggestions, cache fallback, MediaStore write guards and native buffer overflow safety. Gradle download checksum pinned.

Safe UPnP mitigations reject ambiguous framing, hide private error details and validate discovered endpoint scheme/host before legacy Jetty. All nine Jetty advisories are reviewed individually; the old library remains version-affected and compatible transport migration/backports remain deferred. [Security dispositions](JETTY_SECURITY_DISPOSITION.md).

Final-source validation: 1,158 Android tests, zero failures/errors/skips; 128 tooling checks; 11 Jetty integration cases; sanitized native buffer regression; zero lint errors/1,795 warnings; native/runtime/hash conformance and exact tested-payload signing parity. Full Drive APK readback, certificate, identity/version, CRC, native hashes and alignment independently verified.

Source `d6e106ffa27fce88cbfb4ebbfe305954c7e73f60`. APK SHA-256 `1cd4d3c6dcbe9c8c02c3abae26ba4fb37b99d73e0fc68e6a0cf0ca43c2b0c2c8`. Certificate SHA-256 `79ed34c52c3e359756ade0634d7bbb6f8e94e42a059020c1220bd8c7092a9f5e`.

Physical Shield, real providers/storage/AV-chain, signed upgrade/rollback, exact unavailable reference comparisons and performance/memory profiling remain pending. Native subtitle/libass/FFmpeg9/OpenSSL4.1 and unported upstream changes remain explicitly deferred in the [matrix](UPSTREAM_MATRIX.md). [Full release report](RELEASE_REPORT.md). This is a private QA delivery; main is unmerged and no public release was published.
