# 0.146 validation and remaining QA

Software evidence is separate from physical/device acceptance. No row is presented as Shield-verified.

| Area | Completed evidence | Remaining limitation |
|---|---|---|
| Preserved0.145 baseline | Delivered APK checksum/certificate and protected40762577939 reverified; source d6e106ffa27fce88cbfb4ebbfe305954c7e73f60 | Installed-device baseline not available |
| NOVA scope | Latest stable6.6.2 rechecked; all438 prior deltas have per-commit/file dispositions | Upstream published no frozen manifest proving exact released-binary component sources |
| Native toolchain/source | Source-locked OpenSSL4.1, FFmpeg9, libass/font chain and codecs; four-ABI40843685431;647 file hashes perABI | Independent second full native-stack reproduction is not claimed; original source/pins/configuration are supplied |
| JNI/native loading | Current wrappers compile on all four ABIs; strict Android23RTLD_NOW loads pass | Actual ART/JNI player launch and EGL/GPU surface still require device QA |
| Decoder/tempo behavior |64 actual Android23bionic decode cases; AAC padding/ARM64 Opus differences quantified; output tempo audio matches baseline | Representative synthetic media; no exhaustive media corpus or device benchmark |
| Native TLS/HLS |36 trusted/untrusted/wrong-host/nested-HLS/alias/raw-video cases pass | Real HTTPS providers, VPNs, server authentication and network fault campaigns pending |
| Native security | Six patched/six original negative-control cases pass CI;10000-PSSH stress passes each ABI | Malicious CID exploit-font fixture unavailable; no blanket fuzzing/security clearance |
| Subtitle formats |16 SRT/VTT/ASS/PGS decode-to-native-backend render/expiry/clear/flush cases pass; four font-stack cases and12 default-system-font cases | Actual GPU visual layout,3D/stereo, pause/seek/resume with real content pending |
| Subtitle concurrency |13 ASan/UBSan,7TSan and180 native regressions pass | Mock JavaVM and modelled STREAM worker tests do not establish real ART/GL behavior |
| API23 transport/trust |28 actual Android23 framework tests (22Jetty,1trust-root,5FTPS), zero failures/errors/skips | Real UPnP devices, SOAP/provider quirks and LAN IPv6 scope interfaces pending |
| Application/libraries | Successful40876969463:1209 application/200MediaLib/99FileCore cases; final security fixes require fresh1209/200/104 suite conformance | Four named fixture/benchmark skips stay explicit; no claim of live-user database/NAS benchmark execution |
| UI/focus |168 software snapshots compared across semantic layout changes;165 byte-identical;3 differences explained by build metadata/random fixture suffix | Original A4/hero reference attachments unavailable; real remote/720p/1080p/4K/large-text Shield checks pending |
| Settings/data | Original165 IDs preserved; five additive controls; schema63 additive genre extension; WAL FK/migration/restore/collection tests | Installed signed upgrade/rollback and real database/pruning fixtures pending |
| Security/legal |139 archives/POMs and64 offline notices verified; source backport/version alerts explicitly retained | No external independent security review or zero-vulnerability claim |
| Diagnostics | Numeric/sanitized export protections retained; default-enabled/persisted opt-out test corrected | Original October27 diagnostic ZIP unavailable; no original-incident reproduction claimed |
| APK/signing/delivery | Final conformance workflow and exact-payload signing gate prepared | Final unsigned gate running; signing environment lacks exact0.146 branch authorization |

The four explicit library skips are the two `RealDatabasePruningTest` cases requiring its missing real database setup, `WebdavPropfindBenchmarkTest.compareMinimalPropertiesWithAllprop`, and `SpeedTestTransferTest.compareTransferRates`. They are allowlisted by exact case name in the release gate; other skips/failures are refused.
