# Supernova — Foundation Release credits and licence ledger
Date: 8 October 2026
Scope: repository source and current QA build configuration; NOT a legal compliance certificate.
Source branch inspected: codex/post-4.1.7-shield-fixes. Documentation only, on main.

## Evidence and release identification
- Main Gradle build declares direct dependencies below; nested :MediaLib and :FileCoreLibrary are separate modules, and their full runtime dependency trees have NOT been resolved.
- Build manifest .github/build/nova-ci.xml pins NOVA source 0651a3e0 (source version 6.4.64), AVP, MediaLib, FileCoreLibrary, and native builder/prebuilt repositories. A source manifest does NOT prove which .so libraries ship.
- GitHub Actions build run 37614061018 completed successfully on 7 October 2026 at SHA f97f62942f6b4f1a90e2e15258db452384d6b39c. It published artifact nova-preview-full-apk (artifact ID 11480830105, size 81,358,210 bytes, archive SHA-256 2078a2c2ef5d6aee056b888d7a847569fdc00f148dd317564431cdb15ac7feb7) and gradle-build-log (artifact ID 11480480180). **Neither artifact's contents were downloaded or inspected in this audit.** This is a QA Preview APK, NOT a verified Foundation Release.
- Repository GitHub Releases API currently returned no releases. No final Foundation Release APK is identified for binary compliance validation.

## A. Origins and upstream
| Credit | Evidence | User-facing placement | Licence/QR destination | Status |
|---|---|---|---|---|
| Supernova | This repository | Main About version | Local app identity | Final version TBD |
| NOVA Video Player | Pinned upstream aos-Video 0651a3e0, source version 6.4.64 | Main About: **Based on Nova Video Player 6.4.64**; detailed credits | https://github.com/nova-video-player/aos-Video | Version verified, original notices still need binary/revision verification |
| Archos Video Player Community Edition / Archos SA | Derived upstream source/copyright notices | Credits & acknowledgements | https://github.com/nova-video-player/aos-Video | Preserve applicable copyright and origin notices; exact source obligations pending |

## B. Existing legacy licence entries (13)
These are the 13 entries in res/xml/preferences_licences.xml. **Licence labels here are unverified inherited claims, not compliance sign-off.** Verify exact shipped revision and licence files.
| Project | Inherited label | QR candidate / official project | Evidence status |
|---|---|---|---|
| FFmpeg | LGPL v2.1 | https://ffmpeg.org/legal.html | Native manifest; build flags, GPL/nonfree selection, source/relinking duties BLOCKED |
| libcurl | curl licence | https://curl.se/docs/copyright.html | Legacy entry; inclusion in final binaries UNCONFIRMED |
| AudioCompress | LGPL v2.1 | https://github.com/fluffy-critter/AudioCompress | Native manifest; verify fork/licence |
| libyuv | BSD | https://chromium.googlesource.com/libyuv/libyuv/ | Native manifest; verify notice |
| Boost | Boost 1.0 | https://www.boost.org/users/license.html | Native manifest; verify notices |
| libtorrent | BSD | https://www.libtorrent.org/ | Native manifest; verify licence/source |
| jcifs-ng | LGPL v2.1 | https://github.com/AgNO3/jcifs-ng | Legacy entry; actual module/version pending |
| trakt-java | Unlicense | https://github.com/UweTrottmann/trakt-java | Legacy entry; actual module/version pending |
| tmdb-java | Unlicense | https://github.com/UweTrottmann/tmdb-java | Direct Gradle 2.13.0; verify exact licence |
| jsch | LGPL and BSD-style | https://github.com/mwiede/jsch | Legacy entry; actual module/version pending |
| smbj | Apache 2.0 | https://github.com/hierynomus/smbj | Legacy entry; actual module/version pending |
| sshj | Apache 2.0 | https://github.com/hierynomus/sshj | Legacy entry; actual module/version pending |
| sardine-android | Apache 2.0 | https://github.com/thegrizzlylabs/sardine-android | Legacy entry; actual module/version pending |

## C. Direct runtime dependency inventory absent from the legacy 13-entry list
Exact declared versions; verify SPDX/licence/NOTICE from exact package and resolve transitive runtime dependencies before creating final local legal text.
| Group | Declared dependency/version | Official project / QR candidate |
|---|---|---|
| QR | com.google.zxing:core:3.5.3 | https://github.com/zxing/zxing |
| Crypto | org.bouncycastle:bcprov-jdk18on:1.85 | https://www.bouncycastle.org/ |
| Time | com.jakewharton.threetenabp:threetenabp:1.4.9 | https://github.com/JakeWharton/ThreeTenABP |
| Images | com.squareup.picasso:picasso:2.8 | https://github.com/square/picasso |
| Scrolling | com.github.ksoichiro:android-observablescrollview:1.6.0 | https://github.com/ksoichiro/Android-ObservableScrollView |
| HTTP logging | com.squareup.okhttp3:logging-interceptor:5.5.0 | https://square.github.io/okhttp/ |
| Logging | com.github.tony19:logback-android:3.0.0 | https://github.com/tony19/logback-android |
| Diagnostics | io.sentry:sentry-android:8.53.0; io.sentry:sentry:8.53.0 | https://github.com/getsentry/sentry-java |
| Android lifecycle | androidx.lifecycle:lifecycle-process:2.11.0 | https://developer.android.com/jetpack/androidx |
| Android TV | androidx.tvprovider:tvprovider:1.1.0; androidx.leanback:leanback:1.2.0; androidx.leanback:leanback-preference:1.2.0 | https://developer.android.com/jetpack/androidx |
| Android support/UI | androidx.media:media:1.8.0; androidx.core:core:1.19.0; androidx.legacy:legacy-support-core-utils:1.0.0; androidx.legacy:legacy-support-core-ui:1.0.0; androidx.fragment:fragment:1.9.0; androidx.recyclerview:recyclerview:1.4.0; androidx.cardview:cardview:1.0.0; androidx.palette:palette:1.0.0; androidx.appcompat:appcompat:1.8.0; androidx.browser:browser:1.9.0; androidx.preference:preference:1.2.1 | https://developer.android.com/jetpack/androidx |
| Material | com.google.android.material:material:1.14.0 | https://github.com/material-components/material-components-android |
| External local modules | project(':MediaLib'); project(':FileCoreLibrary') | https://github.com/nova-video-player/aos-MediaLib ; https://github.com/nova-video-player/aos-FileCoreLibrary |

Note: build.gradle contains tvprovider declaration twice. Resolve once in actual runtime graph; not two credits. The direct build list is NOT the resolved runtime SBOM. Desugar/build tools and test dependencies require redistribution check rather than automatic inclusion.

## D. Native builder/prebuilt inventory (presence in manifest, packaging UNVERIFIED)
aos-AVP, aos-avos, FFmpeg, dav1d, Opus, libmysofa, OpenSSL, libyuv, libnativehelper, Boost, libtorrent, torrentd, AudioCompress; also native prebuilt ffmpeg, opus, dav1d, openssl, libmysofa, torrentd. Additional native transitive components may exist. Source/license/build flag audit for every shipped ELF .so is mandatory. The LGPL/GPL and corresponding-source obligations for FFmpeg, and licences for linked libraries, cannot be certified from the manifest alone.

## E. Service/provider credit ledger — separate from software libraries
| Provider | Evidence / condition | Attribution action | QR destination | Status |
|---|---|---|---|---|
| TMDb | Existing metadata and tmdb-java | Approved TMDb logo plus required notice: **This product uses the TMDB API but is not endorsed or certified by TMDB.** | https://www.themoviedb.org/ | Required; confirm branding/asset use at implementation: https://developer.themoviedb.org/docs/faq |
| Trakt | Existing account/scrobble integration | Follow Trakt official branding/API-use policy, avoid endorsement implication | https://trakt.tv/ | Integration documented; final credential/activation check |
| OpenSubtitles.com | Verified REST v1 API endpoint in OpenSubtitlesApiHelper.java | Credit actual provider; confirm exact logo/attribution terms | https://www.opensubtitles.com/ | API generation verified; terms and functional acceptance outstanding |
| put.io | Implementation documented; production OAuth configuration previously blocked | Credit only if shipped enabled | https://put.io/ | Activation pending |
| Streaming availability provider(s) | JustWatch-related designs and provider selection | Credit actual enabled upstream data provider(s) and comply with their terms; do not assume design equals live integration | Provider-specific official site TBD | Activation and contract pending |
| SkipDB / TheIntroDB / MDBList | Roadmap/approved ideas, not established shipped integration | Do not list as active providers | None until verified | Excluded from current active ledger |
| SMB/WebDAV/SFTP/FTP/UPnP | Network protocols | Credit underlying shipped libraries, NOT protocol names as companies | N/A | Resolve modules |

## F. Implementation and final sign-off gates
- Main About must not show upstream SHA. Separate Open-source licences, Credits & acknowledgements, Technical Information.
- Third panel contains complete locally readable legal text, copyright/NOTICE and source availability, plus a validated scannable QR for each applicable official site. No clickable external URLs. QR does not replace legal text.
- Resolve exact Foundation Release app SHA, APK SHA-256, variant, and build inputs; inspect artifact, manifest and all packaged .so files.
- Run Gradle releaseRuntimeClasspath dependency graph for Video/MediaLib/FileCoreLibrary and preserve lockfile/SBOM with exact versions and licence provenance.
- Check native build flags and source distribution/relinking requirements, particularly FFmpeg and LGPL/GPL-linked modules.
- Verify service attribution and approved branding with actual activated integrations; check OpenSubtitles account/API key functionality.
- Compare all bundled offline licence texts against corresponding exact library versions; check final UI focus, scrolling and QR scanability on Shield.
- Require human legal review for ambiguous/copyleft obligations if distributing publicly.

**FINAL RESULT: LEDGER PREPARED. RELEASE-SPECIFIC LICENCE VALIDATION NOT PASSED / NOT POSSIBLE YET.** A QA preview APK exists in GitHub Actions, but no final Foundation Release artifact was found and no packaged binary/runtime SBOM was inspected. This must remain an explicit release blocker, not be reported as a completed compliance validation. No app code or fixes-only branch modified.
