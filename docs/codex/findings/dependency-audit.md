# Dependency and upstream audit — 0.157, 6 November 2026

Read-only inventory after the registered source preparation stack. Maven Central/Google metadata and OSV were
queried for all 63 distinct direct coordinates, including tests and conditional branches. This is a declaration
inventory, not an authoritative Gradle resolved graph or a new APK SBOM. No Gradle/pin changes were made.
Fork coordinates require inherited-source review even when OSV returns no indexed advisory. Latest stable is a
dated observation, not a recommendation to jump major versions. JSON evidence retains usages and metadata URLs.

## Direct Java dependencies

| Current coordinate | Latest stable observed | OSV IDs |
| --- | --- | --- |
| `com.google.zxing:core:3.5.3` | 3.5.4 | None indexed |
| `com.uwetrottmann.tmdb2:tmdb-java:2.13.0` | 2.13.0 | None indexed |
| `org.bouncycastle:bcprov-jdk18on:1.86` | 1.86 | None indexed |
| `androidx.lifecycle:lifecycle-process:2.11.0` | 2.11.0 | None indexed |
| `com.android.tools:desugar_jdk_libs_nio:2.1.5` | 2.1.5 | None indexed |
| `com.jakewharton.threetenabp:threetenabp:1.4.9` | 1.4.9 | None indexed |
| `com.squareup.picasso:picasso:2.8` | 2.8 | None indexed |
| `androidx.tvprovider:tvprovider:1.1.0` | 1.1.0 | None indexed |
| `androidx.media:media:1.8.0` | 1.8.0 | None indexed |
| `androidx.core:core:1.19.1` | 1.19.1 | None indexed |
| `androidx.legacy:legacy-support-core-utils:1.0.0` | 1.0.0 | None indexed |
| `androidx.legacy:legacy-support-core-ui:1.0.0` | 1.0.0 | None indexed |
| `androidx.fragment:fragment:1.9.1` | 1.9.1 | None indexed |
| `androidx.recyclerview:recyclerview:1.4.0` | 1.4.0 | None indexed |
| `androidx.cardview:cardview:1.0.0` | 1.0.0 | None indexed |
| `androidx.palette:palette:1.0.0` | 1.1.0 | None indexed |
| `androidx.appcompat:appcompat:1.8.0` | 1.8.0 | None indexed |
| `androidx.browser:browser:1.10.0` | 1.10.0 | None indexed |
| `androidx.preference:preference:1.2.1` | 1.2.1 | None indexed |
| `com.github.ksoichiro:android-observablescrollview:1.6.0` | Fork: compare upstream source/tags separately | None indexed |
| `com.google.android.material:material:1.14.0` | 1.14.0 | None indexed |
| `androidx.leanback:leanback:1.2.0` | 1.2.0 | None indexed |
| `androidx.leanback:leanback-preference:1.2.0` | 1.2.0 | None indexed |
| `com.squareup.okhttp3:logging-interceptor:5.5.1` | 5.5.1 | None indexed |
| `com.github.tony19:logback-android:3.0.0` | Fork: compare upstream source/tags separately | None indexed |
| `io.sentry:sentry-android:8.61.1` | 8.63.0 | None indexed |
| `io.sentry:sentry:8.61.1` | 8.63.0 | None indexed |
| `junit:junit:4.13.2` | 4.13.2 | None indexed |
| `org.mockito:mockito-core:5.24.0` | 5.24.0 | None indexed |
| `org.robolectric:robolectric:4.17` | 4.17 | None indexed |
| `com.squareup.okhttp3:mockwebserver:5.5.1` | 5.5.1 | None indexed |
| `androidx.test:core:1.7.0` | 1.7.0 | None indexed |
| `org.slf4j:slf4j-api:2.0.20` | 2.0.20 | None indexed |
| `ch.qos.logback:logback-classic:1.6.6` | 1.6.6 | None indexed |
| `javax.servlet:javax.servlet-api:4.0.1` | 4.0.1 | None indexed |
| `org.eclipse.jetty:jetty-client:9.4.58.v20250814` | 12.1.14 | None indexed |
| `org.eclipse.jetty:jetty-server:9.4.58.v20250814` | 12.1.14 | GHSA-7p3p-8qv8-m2vh |
| `org.eclipse.jetty:jetty-servlet:9.4.58.v20250814` | 11.0.26 | None indexed |
| `org.eclipse.jetty:jetty-security:9.4.58.v20250814` | 12.1.14 | GHSA-2fvj-hgj9-j2gr |
| `org.apache.httpcomponents.core5:httpcore5:5.4.4` | 5.4.4 | None indexed |
| `org.json:json:20260814` | 20260814 | None indexed |
| `com.uwetrottmann.trakt5:trakt-java:6.21.0` | 6.22.0 | None indexed |
| `org.apache.oltu.oauth2:org.apache.oltu.oauth2.client:1.0.2` | 1.0.2 | None indexed |
| `commons-codec:commons-codec:1.23.0` | 1.23.0 | None indexed |
| `org.apache.commons:commons-text:1.15.0` | 1.15.0 | None indexed |
| `com.github.nova-video-player.jupnp:org.jupnp:3.0.5-nova3` | Fork: compare upstream source/tags separately | None indexed |
| `com.github.nova-video-player.jupnp:org.jupnp.support:3.0.5-nova3` | Fork: compare upstream source/tags separately | None indexed |
| `com.github.nova-video-player.jupnp:org.jupnp.android:3.0.5-nova3` | Fork: compare upstream source/tags separately | None indexed |
| `io.sentry:sentry:8.61.0` | 8.63.0 | None indexed |
| `androidx.test:runner:1.7.0` | 1.7.0 | None indexed |
| `androidx.test.ext:junit:1.3.0` | 1.3.0 | None indexed |
| `com.github.nova-video-player:sardine-android:v0.9-nova` | Fork: compare upstream source/tags separately | None indexed |
| `ch.qos.logback:logback-classic:1.6.5` | 1.6.6 | None indexed |
| `org.slf4j:slf4j-api:1.7.36` | 2.0.20 | None indexed |
| `org.bouncycastle:bcprov-jdk18on:1.81` | 1.86 | GHSA-574f-3g2m-x479, GHSA-9pwp-9qqc-pr26, GHSA-c3fc-8qff-9hwx, GHSA-p93r-85wp-75v3, GHSA-qp49-qgx5-5m26 |
| `com.github.nova-video-player:jcifs-ng:v2.1.11-nova12` | Fork: compare upstream source/tags separately | None indexed |
| `com.github.nova-video-player:jsch-mwiede:jsch-2.28.7-readahead3` | Fork: compare upstream source/tags separately | None indexed |
| `commons-net:commons-net:3.13.0` | 3.13.0 | None indexed |
| `com.github.nova-video-player:smbj:v0.15.0-nova2` | Fork: compare upstream source/tags separately | None indexed |
| `com.hierynomus:sshj:0.41.2` | 0.41.2 | None indexed |
| `org.bouncycastle:bcpkix-jdk18on:1.86` | 1.86 | None indexed |
| `oro:oro:2.0.8` | 2.0.8 | None indexed |
| `com.squareup.okhttp3:okhttp-tls:5.5.1` | 5.5.1 | None indexed |

AndroidX/Material/Leanback updates need API-23/resource/focus acceptance; HTTP updates need aligned OkHttp/Okio
variants and server fixtures; TMDB/Trakt updates need parser, auth and allow-list tests; SSH/SMB/Sardine forks need
range/credential/cancellation tests; logging/Sentry changes need startup, privacy and packaging checks. Test tooling
needs Java/Robolectric compatibility. Desugaring must stay aligned across all three modules.

Jetty server/security advisories are CVE-2026-6790 (Host/:authority) and CVE-2026-10050 (Digest character conversion).
Existing jetty-supernova146-api23-security.patch changes HttpChannel/HttpParser and client/server Digest handling;
the rebuild embeds a SUPERNOVA-BACKPORT receipt. Same-coordinate version scanning cannot recognise those patched
bytes. Verify the receipt, never silently substitute ordinary Maven JARs. Jetty 12 is not an API-23 drop-in update.

BCprov 1.81 is declared only in the optional local-JCIFS-JAR branch. Its five findings concern GOST CTR, name
constraints, LDAP, FrodoKEM timing and ASN.1 depth. App bcprov and FileCore bcpkix directly declare 1.86; confirm the
resolved graph before claiming 1.81 ships. Reject/align the old optional branch during a separately authorised update.
No indexed advisory is a limited database result, not a certificate of safety.

Picasso metadata incorrectly lists 2.71828 as latest/release. The published POM Last-Modified dates are
7 March 2018 for 2.71828 and 10 August 2020 for 2.8; this audit uses the newer chronological release, 2.8.

## Native components

Exact locked commits were queried in OSV. Stable tags were checked at public upstream repositories, excluding
pre-release/development tags. Full native pin/URL/hash authority remains the existing lock, unchanged.

| Component | Locked tag / commit | Latest stable observed | OSV IDs |
| --- | --- | --- | --- |
| ffmpeg | `n9.0.2` / `946fcce07b6dcd0331c8cc609192aeff5e1924f8` | n9.0.2 | CVE-2026-107675, CVE-2026-107676, CVE-2026-107677, CVE-2026-107678, CVE-2026-107696 |
| openssl | `openssl-4.1.0` / `2f4e3a5fec05394068cfa7379890ab552483a8a6` | openssl-4.1.0 | None indexed |
| libass | `0.17.5` / `4a05d8127f525943ebf45fdc6497c9e665947f0d` | 0.17.5 | None indexed |
| zlib | `v1.3.2` / `da607da739fa6047df13e66a2af6b8bec7c2a498` | v1.4 | CVE-2026-76844, CVE-2026-85091 |
| libpng | `v1.6.59` / `cd952f49f95bb27154ae77dbb103032d95f6e580` | v1.6.59 | None indexed |
| libxml2 | `v2.15.4` / `96498992efa48d52b0e8b83058bd88dbdaf153c1` | v2.15.4 | OSV-2021-777, OSV-2024-698 |
| freetype | `VER-2-14-3` / `0a0221a1347e2f1e07c395263540026e9a0aa7c7` | VER-2-15-0 | CVE-2026-50811, CVE-2026-95512 |
| fribidi | `v1.0.17` / `b93119f5fdc7ea47672cc304c1455ffa6dfe7536` | v1.0.18 | None indexed |
| harfbuzz | `15.0.0` / `35f656a1a2bab548ea7f54a6dd3fba2b09680d14` | 15.1.0 | None indexed |
| libunibreak | `libunibreak_8_0` / `28a2756b864c343f438cd22537d49d394d4666a5` | libunibreak_8_0 | None indexed |
| fontconfig | `2.18.3` / `0f236723a3c26bd9745e32ba2b3ee21e5f809295` | v1.3.5 | None indexed |
| opus | `084bb7327ce90171c4dcf2a58fe6ac58ae821dbf` / `084bb7327ce90171c4dcf2a58fe6ac58ae821dbf` | No stable release verified | None indexed |
| dav1d | `1.5.4` / `54706fc6bc0cdecab7e9593974a4039cc038fca7` | 1.5.4 | None indexed |
| libmysofa | `v1.3.5` / `6cc5b15a73e9bd97810d03767082edda7f315881` | v1.6.1 | None indexed |
| dlg | `untagged` / `395ccad2c1e0daae535c4d20bb0a3f2424648e17` | v0.4 (nyorain/dlg; 655b598bba43f9bf84d39dec2d4ba93ca327892b) | None indexed |

Existing FFmpeg backports cover HDR serialisation, the empty-needle template loop, iterative PSSH freeing and bounded
RTSP redirects. SFTP host verification remains a build-feature exposure question: establish whether libssh SFTP is
enabled in the reviewed binary; FileCore SSH is separate. Existing zlib failed-write input reset and its socket test
cover gzwrite stall → clearerr → gzprintf, the mechanisms named by both advisories. Existing FreeType patches cover
variable-coordinate bounds and excessive CID overlap. These are source observations: archive receipts/security
fixtures must prove the binary includes them. The two open-ended libxml2 OSS-Fuzz ranges require reproducer/fix
triage; no suppression or claim of safety is made. Rebuild/link/package all required ABIs and rerun security negative
controls for native updates. Do not mix new libraries into an old reviewed archive.

## Build tools

- `com.android.tools.build:gradle:9.3.2`: latest 9.5.1.
- `com.github.triplet.play:com.github.triplet.play.gradle.plugin:4.1.1`: latest 4.1.1.
- `io.sentry.android.gradle:io.sentry.android.gradle.gradle.plugin:6.19.0`: latest 6.26.0.

Gradle wrapper: distributionUrl=https\://services.gradle.org/distributions/gradle-9.5.0-all.zip

These are build inputs, not APK dependencies. Updating requires a full exact-source Gradle/lint/resource/R8 run,
including API-23 checks. Repository TLS failures are recorded as unverified, never treated as "latest".

## NOVA source survey

| Repository | Observed upstream | Assessment |
| --- | --- | --- |
| aos-Video | bd4ed0706ea6aff420c4213c38bbbc0d0a58a7f8 | PGS/fit-width/system-bar changes cross screen/subtitle-layout ownership. HDR ranking already backported; compare source equivalence. |
| aos-MediaLib | a07bc4f75cd9763c725c45949ebd052828e5667a | Pin 5758074 comparison diverges 44 ahead/4 behind. Producer lifecycle, UPnP, subtitle cache and IntroDB overlap local backports. Preserve local schema/ownership; no blanket merge. |
| aos-FileCoreLibrary | 78ca3812e0f291e4436e4f2c92722d07b4db99f9 | Pin c4b760c comparison 54 ahead/0 behind. Range/read/credential changes overlap local patches; thread priority/debug startup need fixtures and Shield measurements. |
| aos-AVP | 5c5d4ec | Upstream manifest provenance only; owner's archived fork is not a dependency. |
| aos-avos | 7634db295df74cf218e6901d649f875f75c3bfae | Two commits beyond 5cf3ae9, including 3df9542e viewport-independent PGS/VobSub. Item 21 still requires rebasing, building and a reviewed archive. |

Repositories: https://github.com/nova-video-player/ . Counts alone do not show missing fixes; compare raw source plus
the complete prepared patch stack. No fork push, active workflow or dependency registration change was made here.
