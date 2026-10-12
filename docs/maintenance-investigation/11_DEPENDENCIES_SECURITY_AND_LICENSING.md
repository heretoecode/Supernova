# 11 — Dependencies, security, native compatibility and licensing

## Verified dependencies and reproducibility

The frozen source has an existing 138-artifact resolved-runtime SBOM with coordinates, actual hashes, declared/licence-family provenance and notice sources. Direct Gradle declarations do not equal the resolved graph. Duplicate `androidx.tvprovider` declaration is visible in build.gradle, but the SBOM resolves one artifact; this is declaration redundancy, not proof of duplicate DEX. The independent modules have their own dependencies and preparation rewrites. No Gradle dependency-resolution/build task was executed here because this workspace has no JDK/Android SDK/sibling build assembly.

Source: [RUNTIME_SBOM.json:1](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/docs/foundation/RUNTIME_SBOM.json#L1), [build.gradle:443](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/build.gradle#L443), [prepare-foundation.py:1](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/.github/build/prepare-foundation.py#L1), [nova-ci.xml:1](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/.github/build/nova-ci.xml#L1).

**VERIFIED static/attributed evidence:** QA source uses AGP9.3.2, Gradle wrapper, minSDK23, compileSDK37/target36, four ABIs, R8/resource shrink. QA native wrapper build uses NDK29.0.14206865 and existing native DEBUG configuration; app is release/non-debuggable. Native RELEASE wrapper attempt is recorded as failing inherited undefined stream-buffer functions; that remains a build-risk unknown, not something fixed here. Previous successful CI/tests/lint native checks are attributed to BUILD_QA; release lint reports 1500 warnings, not clean lint.

## Native manifest and builder histories

[Native manifest/history evidence](evidence/native-manifest-and-history.json) resolves every v6.5.5 manifest component ref to a current SHA, compares available native/build histories and records API caps. Most auxiliary pins match current selected refs: audiocompress, dav1d builder/prebuilt, mysofa builder/prebuilt, OpenSSL builder/prebuilt, nativehelper, Boost, libtorrent builder, torrentd builder/prebuilt. Equality of commit SHAs is source identity; it does not prove byte-identical rebuilt binaries or every security issue fixed.

Changed source groups:

| Component | Verified difference | Recommendation / verification |
|---|---|---|
| AVP orchestration | Four commits since 9c5842b7; changes core.mk and manifest, removes libyuv and cleans Opus includes | ADAPT only after native group approved; no source pin inference from moving refs |
| FFmpeg builder | 49b63bce→`fd7c9f9c3f63befedafc8a36b2da722f81b64aad`, four commits | Updated atempo frontier publication, Opus includes, SOFAlizer ring/resampling, TLS default patch; review each independently |
| Opus builder | `c0251c36a4524110bea6b52047c918d11d087102` adds includes to prebuilts | ADAPT rebuild packaging if needed; no codec improvement implied by headers |
| FFmpeg prebuilt | pin6ba0e934 vs current `7bc9a0860ba400a103d5e88f1706fb0435dcb676` diverged one each | Major generation/provenance change; API file list reaches 300-file cap, not exhaustive; compare coherent actual ABI exports/build/source |
| Opus prebuilt | current `2a04bae78e56281bf57d4116b1e2545b5de2abdb`; exact pinned SHA fetched by Git | API compare404 explained by unrelated histories: pin has two commits, current main one root. Direct snapshot diff changes four ABI .so binaries; semantic codec change UNKNOWN |
| libyuv | Still pinned/consumed in Supernova; removed from upstream manifest | DEFER removal until swscale migration and consumer/colour/JNI validation |

[Effective overlays](evidence/effective-component-overlays.tsv) add Supernova behavior beyond pins. FFmpeg Foundation lock/rebuild, rather than generic builder branch name, governs shipped source. Previously recorded native legal provenance lists FFmpeg8.0.1/LGPL2.1+, dav1d header1.5.4, Opus ELF1.6.1, OpenSSL3.5.7, Boost1.92 and libtorrent2.0.14; libmysofa wrapper1.3.5 vs exported1.3.3 is already disclosed. These version labels are attributed to existing receipts, not freshly executed ELF inspection.

## Security evidence and priority

**VERIFIED public advisory query:** OSV exact Maven coordinate/version batch for all 138 SBOM artifacts returned nine distinct advisory IDs on Jetty9.2.30: three for jetty-http and six for jetty-server. [Query](evidence/dependency-advisory-query.json) and [full details](evidence/dependency-advisory-details.json) retain affected-version metadata and references. Zero matches on other coordinates is not a comprehensive security clearance; fork versions/native artifacts and unavailable advisories are outside this query.

| Advisory / CVE | Upstream-described condition | Supernova reachability assessment |
|---|---|---|
| GHSA-26vr-8j45-3r4w / CVE-2021-28165 | Large malformed TLS frame CPU exhaustion | TLS Jetty server use not established; current UPnP connector uses plain ServerConnector. Condition-specific reachability UNKNOWN |
| GHSA-84q7-p226-4x5w / CVE-2017-7656 | Request parsing/cache poisoning/smuggling | HTTP server exists; intermediaries/cache interpretation and crafted request prerequisites need controlled test |
| GHSA-9rgv-h7x4-qw8g / CVE-2018-12536 | Sensitive error content | Server exists; handler/error response fields and actual request path need review |
| GHSA-cj7v-27pg-wf7q / CVE-2022-2047 | Invalid URI authority parsing | Client/server URI handling present; malicious input route not certified |
| GHSA-hmr7-m48g-48f6 / CVE-2023-40167 | + prefixed Content-Length | HTTP parser exists; exploit chain requires framing peer/intermediary conditions |
| GHSA-m6cp-vxjx-65j6 / CVE-2021-34428 | SessionListener prevents invalidation | Current UPnP ServletContextHandler uses NO_SESSIONS; that path does not establish vulnerable session use |
| GHSA-p26g-97m4-6q7c / CVE-2023-26049 | Quoted cookie parsing/exfiltration | Relevant parser library version; application cookie/session use and trust boundary UNKNOWN |
| GHSA-qh8g-58pp-2wxh / CVE-2024-6763 | Invalid URI authority parsing | Version match; endpoint/input reachability needs tracing |
| GHSA-qw69-rqj8-6qw8 / CVE-2023-26048 | Large multipart without filename memory exhaustion | UPnP handler multipart parsing not demonstrated; do not claim exploitable upload endpoint |

**VERIFIED source relevance:** MediaLib depends on jUPnP3.0.5 and Jetty9.2.30 for Android API23 compatibility. NovaUpnpService creates Jetty client and server, NovaJettyServletContainer binds host/port provided by UPnP network factory and registers `/*` with NO_SESSIONS. Jetty is therefore not a removable unused dependency based on UI absence. **PROPOSED P0:** investigate supported secure replacement/backport in isolation, including minSDK/desugaring/jUPnP binary API and controlled request tests. No CVSS severity or exploit claim is invented.

Source: [MediaLib/build.gradle:185](https://github.com/heretoecode/aos-MediaLib/blob/5758074049bc153ac4967b4ecd21886469270076/build.gradle#L185), [MediaLib/NovaUpnpService.java:58](https://github.com/heretoecode/aos-MediaLib/blob/5758074049bc153ac4967b4ecd21886469270076/src/com/archos/mediacenter/filecoreextension/upnp2/NovaUpnpService.java#L58), [MediaLib/NovaJettyServletContainer.java:125](https://github.com/heretoecode/aos-MediaLib/blob/5758074049bc153ac4967b4ecd21886469270076/src/com/archos/mediacenter/filecoreextension/upnp2/NovaJettyServletContainer.java#L125).

Other priority surfaces:

- **VERIFIED:** StreamOverHttp has implicit-interface ServerSocket(0), despite localhost returned URL; actual LAN exposure UNKNOWN. Test and consider explicit loopback binding (report06).
- **VERIFIED:** generic diagnostic string redaction/denylist is insufficient by source rules and synthetic translated probes; no real leak observed (report05).
- **VERIFIED:** credential DB stores username/password/domain in ordinary SQLite columns. Device app sandbox protects access; at-rest encryption is not shown for this store. Do not export credentials or sweep them into shared migration dumps. Existing provider token envelope is a separate design.
- **VERIFIED:** broad cleartext policy and SFTP TOFU are explicit current behaviors. Threat model must distinguish selected local protocols from protected account API traffic.
- **VERIFIED upstream source:** FFmpeg builder commit `fd7c9f9c...` adds tls_verify_default.patch changing verify default1→0. **PROPOSED REJECT default-off patch as an automatic adoption**; separately decide compatibility for self-signed/local endpoints without bypassing trusted account endpoints.
- **VERIFIED locked FFmpeg source:** TLS options use TLS_VERIFY_DEFAULT controlled by FF_API_NO_DEFAULT_TLS_VERIFY; [source evidence](evidence/ffmpeg-tls-default-source.json). AVOS pinned parser passes HTTP options without an explicit tls_verify setting in inspected Source/Include. **INFERRED existing risk:** native direct HTTPS may inherit a default-off legacy policy. Compiled macro/backend/runtime CA behavior remains UNKNOWN; Android network-security XML does not automatically govern native OpenSSL. Require valid/invalid/self-signed certificate tests against local controlled endpoints before claiming secure verification.
- **UNKNOWN:** native CVE reachability/version-patch coverage across FFmpeg/OpenSSL/dav1d/Opus/libtorrent. No exhaustive native vulnerability certification was performed; Source lock and recorded versions enable that follow-up.

## Licensing and distribution obligations

**VERIFIED:** app/source headers and NOVA/Archos modules retain Apache2.0 notices; packaged legal catalog/runtime/native licence entries and Foundation reader already exist. FFmpeg and AudioCompress LGPL2.1+; VideoLAN routines LGPL/MIT, dav1d/Opus/libyuv/mysofa/libtorrent BSD families, Boost BSL and OpenSSL Apache2.0. Runtime inventory also includes notices with CDDL/EPL and GPL2+Classpath exception; do not collapse all code into an Apache label.

**PROPOSED compliance gates:** preserve notices/attribution and actual copyright headers, ship matching corresponding source/patches/configuration for LGPL components, maintain relinkability obligations applicable to dynamic/static linkage and source distribution, verify no GPL/nonfree FFmpeg configuration drift, synchronize About/license catalog with resolved artifacts and rebuilt native source. The existing companion FFmpeg QA source artifact expires2026-11-11; artifact expiry is a distribution-availability question for the owner, not a reason to publish a release here. Detailed legal interpretation is outside this technical audit; do not delete licences for size optimization.

## APK/DEX/resource footprint evidence

**VERIFIED attributed receipt:** signed 0.138 APK103518717 bytes; previous CI verified138 runtime hashes,92 native hashes and24 rebuilt FFmpeg libraries. Local investigation did not download/measure the signed APK, DEX/resource compressed shares or installed footprint. Existing Foundation native inventory refers to its own older APK/source and must not be relabelled as 0.138.

**VERIFIED source-only sizes:** tracked assets15857232 bytes, res9858740 bytes; nine byte-identical resource/asset groups. These do not predict APK savings because compression, R8 and resource shrink alter packaging. [Source sizes](evidence/source-resource-footprint.json), [duplicate groups](evidence/resource-byte-duplicates.json) identify candidates only. **UNKNOWN:** like-for-like NOVA APK ratio, installed disk/PSS and actual retained DEX/resources per feature. Required future checks in reports12–13.
