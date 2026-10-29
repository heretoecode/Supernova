# Supernova 0.146 upstream completion — signed QA APK delivered; device QA pending

Application source is frozen at `938885ac2a84a5efecb1674b3e9f1032a56740ed` on `codex/supernova-0.146-upstream-completion-2026-10-28`. Final complete conformance [40886034550](https://github.com/heretoecode/Supernova/actions/runs/40886034550) passed. Independent artifact/JUnit/lint, binary manifest, dex source, ZIP/ELF and native byte checks pass. All 36 final-payload native rendering/font/loading cases pass as UID 10000 on four-ABI Android 23 bionic. The earlier corrected-source conformance40876969463 passed; its unsigned APK is superseded by the documented security/lifecycle fixes. Protected signing [40896722823](https://github.com/heretoecode/Supernova/actions/runs/40896722823) succeeded for exact application source `938885ac2a84a5efecb1674b3e9f1032a56740ed`. [Signed Supernova 0.146 APK](https://drive.google.com/file/d/100G08JITx7byXjN4BvfLSm2imB7FOXin/view?usp=drivesdk) is 95,569,591 bytes; SHA-256 `79829e5181d22d69400157b6da234c7327bfb4b7dfe36a4997a24db29dc86ca7`. Independent signature verification confirms the existing certificate `79ed34c52c3e359756ade0634d7bbb6f8e94e42a059020c1220bd8c7092a9f5e`. Every tested unsigned payload entry is unchanged, embedded source identity and all 84 native hashes match, and the complete Drive download passes the same checks. Private signing-material cleanup passed in the protected runner. Physical/device and installed signed upgrade/rollback acceptance remain pending.

## Baseline and upstream

The completed 0.145 source `d6e106ffa27fce88cbfb4ebbfe305954c7e73f60` and documentation tip `b04938a804ba192e51a6707cbffc1b46cc97bcff` are retained. Its protected40762577939 success, full Drive APK checksum `1cd4d3c6dcbe9c8c02c3abae26ba4fb37b99d73e0fc68e6a0cf0ca43c2b0c2c8` and certificate `79ed34c52c3e359756ade0634d7bbb6f8e94e42a059020c1220bd8c7092a9f5e` were independently reverified. The signed0.146 APK independently verifies against that same installation identity.

Latest stable NOVA remains v6.6.2, published27October2026. AVP release commit is `5c5d4ec81b47ba59be8f91c9d877b49b0ab1c01e`; Video’s reviewed release-time snapshot is `842ac7592ea9188b60a34acd08a450b88d0c4545`. Upstream does not publish a frozen component manifest proving the exact released APK’s sources, so mutable branch heads are not substituted for that unavailable evidence.

[UPSTREAM_MATRIX.md](UPSTREAM_MATRIX.md) accounts for every 438 prior entry:413 integrated/adapted,17 not applicable and8 already equivalent. Full first-parent file lists, source blobs and hashes support each disposition; there is no300-file API truncation. The exclusions identify reverted changes, unrelated/replacement UI, store/updater identity and retired torrent/build infrastructure. The separate Sonic compressed-codec review demonstrates that the old ordinary decoder path already rejects the research codec; retained Sonic PCM speed is a different feature. [SOURCE_COMMITS.json](SOURCE_COMMITS.json) records all 28 Supernova first-parent commits from the verified baseline to the frozen candidate.

## Resulting behavior

The source integrates NOVA’s native subtitle/JNI lifecycle, source readiness/recovery, audio routing, network ownership, metadata, storage/focus and compatible translations without replacing Supernova’s screens. The existing subtitle controls and saved sizing/preferences remain authoritative. Five additive settings expose SAF fonts, default font, letterbox margins, full subtitles for local-language audio and PCM Sonic/atempo. Original 165 settings IDs, library/credential contracts, custom segment skipping and chapter/HDR hooks remain.

Schema63 preserves Supernova collections and match isolation; genre fields/views extend it transactionally. SFTP identity checks, loopback binding, numeric diagnostics, confirmed404 handling and complete-traversal deletion safeguards remain. Meaningful final lint/security corrections release closed playback-dialog views, preserve credential-fragment dismissal ownership, make two internal services private and replace permissive Java FTPS defaults with verified CA/control/data identities.

The complete four-ABI native chain uses FFmpeg 9.0.2, OpenSSL 4.1 and libass 0.17.5 with source-locked font/codec dependencies and reviewed new security patches. Default font loading works as UID 10000 without a selected folder, HOME/XDG or writable system cache. Raw-video/HLS registry changes have working compatibility paths. AAC padding and ARM64 rounding differences are quantified; warmed timings separate decoding from QEMU/process startup.

## Security disposition

[JETTY_SECURITY_BACKPORT_REVIEW.md](JETTY_SECURITY_BACKPORT_REVIEW.md) gives an individual disposition for all nine legacy advisories. Eight disappear from the9.4.58 exact-version query; CVE2024-6763 and three newer parser/authority/Digest findings have source backports and original-version negative controls. The seven rebuilt modules retain their upstream coordinates with explicit source/patch markers. Four unused optional MethodHandle-based features are omitted; NIO desugaring and atomic counters preserve API 23. This is a scoped UPnP transport fork, not an arbitrary-feature Jetty server distribution.22 host Jetty cases and28 actual Android 23 transport/trust/FTPS cases pass.

[NATIVE_SECURITY_BACKPORT_REVIEW.md](NATIVE_SECURITY_BACKPORT_REVIEW.md) describes all 11 native commit-query findings. FFmpeg/FreeType/zlib fixes, bounded RTSP redirects and feature-restricted libxml2 are source reviewed and built on every ABI. Six original-failure/six patched sanitizer outcomes pass. The disabled FFmpeg libssh backend is separate from retained application SFTP. Scanner-visible version findings remain recorded; no advisory is suppressed or declared globally fixed by relabelling a version.

[FTPS_TLS_REVIEW.md](FTPS_TLS_REVIEW.md) records five actual TLS cases, the permissive-default negative control and the reproduced unsupported SDK setter. Its replacement keeps strict CA and original-host checks on both channels at API 23. Untrusted/self-signed FTPS peers now fail. [SECURITY_REVIEW.md](SECURITY_REVIEW.md) and [LINT_REVIEW.md](LINT_REVIEW.md) separate implemented protections from remaining library warnings and device/provider scope.

## Validation

| Gate | Evidence/result |
|---|---|
| Earlier corrected complete source |40876969463 success;1209 application/200MediaLib/99FileCore, zero failures/errors; four exact library fixture/benchmark skips;131 tooling; lint0errors/1492warnings |
| Final exact-source APK |40886034550 success:1209 application/200MediaLib/104FileCore; zero failures/errors, four exact library skips;131 tooling; lint 0 errors/1,489 warnings; actual service/lifecycle/FTPS assertions pass |
| Actual Android 23 framework |40884866451:28 tests,0failures/errors/skips; exact MediaLib/FileCore/Jetty hashes and20 selected runtime archives independently verified |
| Jetty host/reproduction |22 host cases; five vanilla9.4.58 negative controls; seven module hashes identical across independent builds |
| Four-ABI source stack |40843685431 success;647 installed/configuration/evidence files perABI verified against complete artifacts/source recipes |
| Exact APK native runtime |36 cases as UID 10000 on four-ABI Android 23 bionic:16 subtitle formats,12 default fonts,4 font-stack and4 strict14-DSO loading cases; actual extracted APK bytes, with prohibited XML symbols absent |
| Native runtime |64 representative decode cases;36 TLS/HLS/raw-video checks; four10000-PSSH stress runs |
| Subtitles/fonts |16 SRT/WebVTT/ASS/PGS decode-to-backend render/expiry/clear/flush cases; four font-stack cases;12 default-font cases as UID 10000 |
| Native lifetime/concurrency |13 ASan/UBSan,7TSan and180 regressions; corrected host CI40842168320 |
| New native security |40848631560: six patched/six original negative-control outcomes under sanitizers; artifact/XML/digests verified |
| FTPS host | Five real TLS cases pass in three repeated trials after fixing rejected-peer fixture close timing |
| Runtime/legal inventory |139 resolved archive/POM hashes and64 full offline entries; corresponding copyleft/native source and complete notices retained |
| UI software renders |168 compared across semantic changes and corrected About text;165 identical, three bounded metadata/text/temporary-path changes inspected |
| Performance |16 codec/ABI medians from three20-decode warm-process trials; startup-inclusive results retained separately; no physical throughput claim |

The four library skips are exactly the two RealDatabasePruningTest fixtures, WebdavPropfindBenchmarkTest.compareMinimalPropertiesWithAllprop and SpeedTestTransferTest.compareTransferRates. Other skips/failures are refused. Earlier failed/cancelled runs remain historical evidence, not successful release gates.

## Source delivery and signing

The complete corresponding-source archive contains the exact frozen application, five patched sibling modules, all15 native originals, Jetty, copyleft runtime sources, recipes and per-ABI generated configurations. All 28 manifest-listed source entries (26 archives plus two support documents) and32 native configuration/evidence files were independently verified. SHA256: `6579930f7f5699c8b9699e3f8fea6dc71c1103d8cff0a4380acf6f1ecd0b089b`;507860792bytes.

[Drive source folder](https://drive.google.com/drive/folders/1KwxezR3qdquBXun9MD3PMMenEG0jTk1h) uses six numbered binary parts because the connector rejects files above100MiB. Reassembly instructions, checksums and full readback evidence accompany the parts. Every uploaded part and the complete ordered507860792-byte concatenation have been independently streamed back and SHA256-verified. SOURCE_DELIVERY.json and SOURCE_DRIVE_READBACK.json record the exact result. No signing keys are corresponding source.

The owner authorized the exact0.146 branch in the existing `supernova-foundation-signing` selected-branch environment. Live policy64803247 was verified. The assistant did not change protections or signing identity. [SIGNING_POLICY.json](SIGNING_POLICY.json) records the read. Protected signing consumed the successful conformance artifact, checked source/report-only ancestry and signed the exact tested payload without rebuilding it.

## Verified signed APK delivery

Protected signing [40896722823](https://github.com/heretoecode/Supernova/actions/runs/40896722823) succeeded for exact application source `938885ac2a84a5efecb1674b3e9f1032a56740ed`. [Signed Supernova 0.146 APK](https://drive.google.com/file/d/100G08JITx7byXjN4BvfLSm2imB7FOXin/view?usp=drivesdk) is 95,569,591 bytes; SHA-256 `79829e5181d22d69400157b6da234c7327bfb4b7dfe36a4997a24db29dc86ca7`. Independent signature verification confirms the existing certificate `79ed34c52c3e359756ade0634d7bbb6f8e94e42a059020c1220bd8c7092a9f5e`. Every tested unsigned payload entry is unchanged, embedded source identity and all 84 native hashes match, and the complete Drive download passes the same checks. Private signing-material cleanup passed in the protected runner. Physical/device and installed signed upgrade/rollback acceptance remain pending.

- [Public signed artifact](https://github.com/heretoecode/Supernova/actions/runs/40896722823/artifacts/12906343972), with only the signed APK and public evidence receipt.
- Tested unsigned SHA-256: `c8812d47a022391390177d107022d21b8b240c21ec578172383d6d399e558937`;95,366,288bytes. [Preserved conformance artifact](https://github.com/heretoecode/Supernova/actions/runs/40886034550/artifacts/12903599884).
- Complete final tests:1513 cases,1509 passed/four explicitly skipped;28 actual Android23 framework cases and36 exact-APK native cases additionally pass. Signed payload parity preserves the applicability of these results.
- [Independent signed verification](INDEPENDENT_SIGNED_APK_VERIFICATION.json), [protected public receipt](SIGNED_QA_EVIDENCE.json), [complete Drive readback](SIGNED_APK_DRIVE_DELIVERY.json) and [cleanup/workflow result](SIGNED_WORKFLOW_RUN.json).
- Source archive and complete Drive readback: `6579930f7f5699c8b9699e3f8fea6dc71c1103d8cff0a4380acf6f1ecd0b089b`.

## Outstanding QA limits

[QA_CHECKLIST.md](QA_CHECKLIST.md) retains physical Shield/ART/JNI/EGL/3D subtitle visuals, HDMI/ARC/SPDIF/private listening, remote resolution/large text, real NAS/UPnP/FTPS/VPN and IPv6 interfaces, signed upgrade/rollback, representative real-database fixtures and hardware CPU/memory/thermal/performance as unverified. The original October27 diagnostic ZIP and A4/hero attachments are unavailable. The malicious CID exploit-font fixture was unavailable; that fix has upstream patch/build/valid-font evidence rather than an independent exploit-font reproduction. An independent second full native-stack rebuild and external security assessment are not claimed.

[Draft PR26](https://github.com/heretoecode/Supernova/pull/26) remains stacked on the preserved0.145 branch. Main is unmerged; no public release. HTML history now links the signed0.146 QA APK with its checksum, certificate and protected signing evidence; all 14 earlier article byte strings remain unchanged. Independent full readback is recorded in HTML_RELEASE_NOTES.json. Full maintenance implementation and scoped software evidence do not establish physical/general-release acceptance.
