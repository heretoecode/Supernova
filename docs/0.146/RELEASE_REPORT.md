# Supernova0.146 upstream completion — final candidate validation

Application source is frozen at `938885ac2a84a5efecb1674b3e9f1032a56740ed` on `codex/supernova-0.146-upstream-completion-2026-10-28`. Final complete conformance [40886034550](https://github.com/heretoecode/Supernova/actions/runs/40886034550) is running. The earlier corrected-source conformance40876969463 passed; its unsigned APK is superseded by the documented security/lifecycle fixes. A signed0.146 APK, certificate verification or delivery is not yet claimed.

## Baseline and upstream

The completed0.145 source `d6e106ffa27fce88cbfb4ebbfe305954c7e73f60` and documentation tip `b04938a804ba192e51a6707cbffc1b46cc97bcff` are retained. Its protected40762577939 success, full Drive APK checksum `1cd4d3c6dcbe9c8c02c3abae26ba4fb37b99d73e0fc68e6a0cf0ca43c2b0c2c8` and certificate `79ed34c52c3e359756ade0634d7bbb6f8e94e42a059020c1220bd8c7092a9f5e` were independently reverified. That certificate is the required0.146 installation identity; it is not represented as a new0.146 signature result.

Latest stable NOVA remains v6.6.2, published27October2026. AVP release commit is `5c5d4ec81b47ba59be8f91c9d877b49b0ab1c01e`; Video’s reviewed release-time snapshot is `842ac7592ea9188b60a34acd08a450b88d0c4545`. Upstream does not publish a frozen component manifest proving the exact released APK’s sources, so mutable branch heads are not substituted for that unavailable evidence.

[UPSTREAM_MATRIX.md](UPSTREAM_MATRIX.md) accounts for every438 prior entry:413 integrated/adapted,17 not applicable and8 already equivalent. Full first-parent file lists, source blobs and hashes support each disposition; there is no300-file API truncation. The exclusions identify reverted changes, unrelated/replacement UI, store/updater identity and retired torrent/build infrastructure. The separate Sonic compressed-codec review demonstrates that the old ordinary decoder path already rejects the research codec; retained Sonic PCM speed is a different feature. [SOURCE_COMMITS.json](SOURCE_COMMITS.json) records all28 Supernova first-parent commits from the verified baseline to the frozen candidate.

## Resulting behavior

The source integrates NOVA’s native subtitle/JNI lifecycle, source readiness/recovery, audio routing, network ownership, metadata, storage/focus and compatible translations without replacing Supernova’s screens. The existing subtitle controls and saved sizing/preferences remain authoritative. Five additive settings expose SAF fonts, default font, letterbox margins, full subtitles for local-language audio and PCM Sonic/atempo. Original165 settings IDs, library/credential contracts, custom segment skipping and chapter/HDR hooks remain.

Schema63 preserves Supernova collections and match isolation; genre fields/views extend it transactionally. SFTP identity checks, loopback binding, numeric diagnostics, confirmed404 handling and complete-traversal deletion safeguards remain. Meaningful final lint/security corrections release closed playback-dialog views, preserve credential-fragment dismissal ownership, make two internal services private and replace permissive Java FTPS defaults with verified CA/control/data identities.

The complete four-ABI native chain uses FFmpeg9.0.2, OpenSSL4.1 and libass0.17.5 with source-locked font/codec dependencies and reviewed new security patches. Default font loading works asUID10000 without a selected folder, HOME/XDG or writable system cache. Raw-video/HLS registry changes have working compatibility paths. AAC padding and ARM64 rounding differences are quantified; warmed timings separate decoding from QEMU/process startup.

## Security disposition

[JETTY_SECURITY_BACKPORT_REVIEW.md](JETTY_SECURITY_BACKPORT_REVIEW.md) gives an individual disposition for all nine legacy advisories. Eight disappear from the9.4.58 exact-version query; CVE2024-6763 and three newer parser/authority/Digest findings have source backports and original-version negative controls. The seven rebuilt modules retain their upstream coordinates with explicit source/patch markers. Four unused optional MethodHandle-based features are omitted; NIO desugaring and atomic counters preserve API23. This is a scoped UPnP transport fork, not an arbitrary-feature Jetty server distribution.22 host Jetty cases and28 actual Android23 transport/trust/FTPS cases pass.

[NATIVE_SECURITY_BACKPORT_REVIEW.md](NATIVE_SECURITY_BACKPORT_REVIEW.md) describes all11 native commit-query findings. FFmpeg/FreeType/zlib fixes, bounded RTSP redirects and feature-restricted libxml2 are source reviewed and built on every ABI. Six original-failure/six patched sanitizer outcomes pass. The disabled FFmpeg libssh backend is separate from retained application SFTP. Scanner-visible version findings remain recorded; no advisory is suppressed or declared globally fixed by relabelling a version.

[FTPS_TLS_REVIEW.md](FTPS_TLS_REVIEW.md) records five actual TLS cases, the permissive-default negative control and the reproduced unsupported SDK setter. Its replacement keeps strict CA and original-host checks on both channels at API23. Untrusted/self-signed FTPS peers now fail. [SECURITY_REVIEW.md](SECURITY_REVIEW.md) and [LINT_REVIEW.md](LINT_REVIEW.md) separate implemented protections from remaining library warnings and device/provider scope.

## Validation

| Gate | Evidence/result |
|---|---|
| Earlier corrected complete source |40876969463 success;1209 application/200MediaLib/99FileCore, zero failures/errors; four exact library fixture/benchmark skips;131 tooling; lint0errors/1492warnings |
| Final exact-source APK |40886034550 running; requires all new activity/service/FTPS assertions,1209 application/200MediaLib/104FileCore minimums and exact five FTPS cases |
| Actual Android23 framework |40884866451:28 tests,0failures/errors/skips; exact MediaLib/FileCore/Jetty hashes and20 selected runtime archives independently verified |
| Jetty host/reproduction |22 host cases; five vanilla9.4.58 negative controls; seven module hashes identical across independent builds |
| Four-ABI source stack |40843685431 success;647 installed/configuration/evidence files perABI verified against complete artifacts/source recipes |
| JNI/strict bionic loading | All four current wrappers built and14 native DSOs perABI loaded RTLD_NOW/GLOBAL on official Android23 bionic |
| Native runtime |64 representative decode cases;36 TLS/HLS/raw-video checks; four10000-PSSH stress runs |
| Subtitles/fonts |16 SRT/WebVTT/ASS/PGS decode-to-backend render/expiry/clear/flush cases; four font-stack cases;12 default-font cases asUID10000 |
| Native lifetime/concurrency |13 ASan/UBSan,7TSan and180 regressions; corrected host CI40842168320 |
| New native security |40848631560: six patched/six original negative-control outcomes under sanitizers; artifact/XML/digests verified |
| FTPS host | Five real TLS cases pass in three repeated trials after fixing rejected-peer fixture close timing |
| Runtime/legal inventory |139 resolved archive/POM hashes and64 full offline entries; corresponding copyleft/native source and complete notices retained |
| UI software renders |168 compared across semantic changes and corrected About text;165 identical, three bounded metadata/text/temporary-path changes inspected |
| Performance |16 codec/ABI medians from three20-decode warm-process trials; startup-inclusive results retained separately; no physical throughput claim |

The four library skips are exactly the two RealDatabasePruningTest fixtures, WebdavPropfindBenchmarkTest.compareMinimalPropertiesWithAllprop and SpeedTestTransferTest.compareTransferRates. Other skips/failures are refused. Earlier failed/cancelled runs remain historical evidence, not successful release gates.

## Source delivery and signing

The complete corresponding-source archive contains the exact frozen application, five patched sibling modules, all15 native originals, Jetty, copyleft runtime sources, recipes and per-ABI generated configurations. All28 manifest-listed source entries (26 archives plus two support documents) and32 native configuration/evidence files were independently verified. SHA256: `6579930f7f5699c8b9699e3f8fea6dc71c1103d8cff0a4380acf6f1ecd0b089b`;507860792bytes.

[Drive source folder](https://drive.google.com/drive/folders/1KwxezR3qdquBXun9MD3PMMenEG0jTk1h) uses six numbered binary parts because the connector rejects files above100MiB. Reassembly instructions, checksums and full readback evidence accompany the parts. Every uploaded part and the complete ordered507860792-byte concatenation have been independently streamed back and SHA256-verified. SOURCE_DELIVERY.json and SOURCE_DRIVE_READBACK.json record the exact result. No signing keys are corresponding source.

The existing `supernova-foundation-signing` environment enforces selected branch policies. Its exact0.146 branch is absent;0.145 authorization does not allow this new branch. [SIGNING_POLICY.json](SIGNING_POLICY.json) records the live read. The handover’s safety rule says: “Do not … change signing protections without approval.” No protection was changed and no protected signing attempted.

The remaining owner action is narrowly defined: authorize `codex/supernova-0.146-upstream-completion-2026-10-28` as a branch in that existing environment. The prepared signing workflow will then consume the successful conformance artifact, check source/report-only ancestry, sign the exact tested payload with the pinned certificate, verify payload parity/certificate/checksum and deliver the signed APK to the existing APK folder. It does not rebuild another payload or publish a public release.

## Outstanding QA limits

[QA_CHECKLIST.md](QA_CHECKLIST.md) retains physical Shield/ART/JNI/EGL/3D subtitle visuals, HDMI/ARC/SPDIF/private listening, remote resolution/large text, real NAS/UPnP/FTPS/VPN and IPv6 interfaces, signed upgrade/rollback, representative real-database fixtures and hardware CPU/memory/thermal/performance as unverified. The original October27 diagnostic ZIP and A4/hero attachments are unavailable. The malicious CID exploit-font fixture was unavailable; that fix has upstream patch/build/valid-font evidence rather than an independent exploit-font reproduction. An independent second full native-stack rebuild and external security assessment are not claimed.

[Draft PR26](https://github.com/heretoecode/Supernova/pull/26) remains stacked on the preserved0.145 branch. Main is unmerged; no public release. HTML history is updated in place after final validation while preserving all14 previous article byte strings. Full maintenance implementation and scoped software evidence do not establish physical/general-release acceptance.
