# About, licences and third-party attribution audit — 8 October 2026

Status: **initial repository inventory / gaps identified; NOT legal-compliance certification**. No app code modifications authorised. User approves three-column Settings layout, with selected licence/acknowledgement details in third panel and scannable QR links; full licence text must be accessible in-app. No in-app update checker.

## Verified source evidence
- `res/xml/preferences_licences.xml`: 13 existing entries: FFmpeg, libcurl, AudioCompress, libyuv, Boost, libtorrent, jcifs-ng, trakt-java, tmdb-java, jsch, smbj, sshj, sardine-android. Entries currently provide summaries and ACTION_VIEW web links; do not presume listed licences are accurate for exact shipped binary or build configuration.
- `src/main/java/com/archos/mediacenter/video/leanback/settings/VideoSettingsLicencesFragment.java`: Leanback preferences overlay path, delegates clicks to `WebUtils.openWebLink`; existing third-column architecture is not used.
- `src/main/java/com/archos/mediacenter/video/utils/WebUtils.java`: Android TV web links are explicitly routed to `WebViewActivity`; the user's recorded 'No application can handle this action' must be reproduced/diagnosed, not attributed with certainty to a missing browser.
- `build.gradle`: direct runtime dependencies include ZXing 3.5.3, tmdb-java 2.13.0, Bouncy Castle 1.85, AndroidX lifecycle, desugar_jdk_libs, ThreeTenABP, Picasso, AndroidX TV provider/media/core/legacy/fragment/recyclerview/cardview/palette/appcompat/browser/preference/leanback, material, android-observablescrollview, OkHttp logging-interceptor, logback-android, Sentry; also MediaLib and FileCoreLibrary sibling modules. Build/test dependencies need inventory separately, distinguishing redistributed runtime binaries from build-time/test-only dependencies.
- `.github/build/nova-ci.xml`: pinned Nova/Archos-derived modules and native build/prebuilt modules: AVP, Video, MediaLib fork, FileCoreLibrary, avos, AudioCompress, FFmpeg, dav1d, Opus, libmysofa, OpenSSL, libyuv, libnativehelper, Boost, libtorrent, torrentd and native prebuilt components. Inventory actual packaged binaries, licences and any FFmpeg LGPL/GPL/configuration/source-distribution obligations.
- `README.md`: Supernova derived from NOVA Video Player, itself derived from Archos Video Player Community Edition. The manifest pins upstream `aos-Video` at `0651a3e0b60ada996a9774ab527d7c1ef34b7503`; this is an upstream source commit, **not proof of a named Nova release version**. Display exact verified upstream version if established, otherwise 'Based on NOVA Video Player — upstream source revision ...' rather than inventing a version.
- `docs/audits/PROVIDERS_DATA_SOURCES_AUDIT.md` and `POST_AUDIT_PROVIDER_RECONCILIATION.md`: TMDb, Trakt (existing inherited integration only), OpenSubtitles, streaming availability, put.io, SMB/WebDAV/SFTP/FTP/UPnP; other planned providers must not be represented as shipped. Additional sources SkipDB, TheIntroDB, MDBList are approved/planned in broader roadmap but require verification of actual integration before listing as shipped.

## Required follow-up audit gates
1. Generate full runtime dependency tree / SBOM from reproducible build including MediaLib and FileCoreLibrary, transitive dependencies and exact versions. Identify packaged native .so files and corresponding build flags, upstream licences and bundled notices.
2. For each component, record licence SPDX identifier, copyright holders, original notice/NOTICE requirements, licence text, source/offer obligations where relevant, distribution method, attribution location, and authoritative project URL. Confirm FFmpeg LGPL vs GPL and whether any optional components trigger different conditions.
3. Audit each *actually enabled* API/provider separately for terms-of-use, attribution wording, logos/trademarks, mandatory linking, account/credential constraints, data caching and regional rules. Separate service acknowledgements from software licences and avoid implying partnership/endorsement.
4. Confirm exact upstream NOVA version or commit corresponding to Supernova Foundation baseline; preserve NOVA and Archos attribution, modified-source notices and relevant licences.
5. Build third-column in-app licence details and offline-readable full legal texts; use verified QR code for official project/terms/source URLs, with remote-friendly focus, scrolling and graceful handling of unavailable external intents.
6. Reproduce reported 'No application can handle this action' on Shield and trace precise activity/intent failure; existing WebUtils attempts WebViewActivity on Android TV, so missing external browser alone is not proven.
7. Validate packaged APK against final notices and credits, including any build variants; complete a pre-release compliance review before claiming completion.

## Decisions and boundaries
User approves three-panel Settings presentation and credits with QR links. Technical details behind separate entry. No in-app update mechanism. **No edits to current fixes-only app code** as part of this investigation. This document records audit findings, not legal advice or a certification of licence compliance.

## Upstream NOVA version verified from pinned source — 8 October 2026
The CI manifest pins upstream `nova-video-player/aos-Video` revision `0651a3e0b60ada996a9774ab527d7c1ef34b7503`. Reading that **exact upstream revision's** `build.gradle` confirms `versionName = '6.4.64'` and `versionCode = 6040064`. Its commit date is 13 September 2026. Therefore display **'Based on NOVA Video Player 6.4.64'** with optional source revision `0651a3e0` in Foundation About/version information. This is a verified upstream **source-declared version**, not independent confirmation of an identically sourced publicly published APK or release tag. Distinguish Supernova's own release version. Existing local Supernova build.gradle version strings are independently modified and are not the authoritative upstream source version.

## User direction — legacy web-link failure deferred
User explicitly does **not** want investigation of the existing 'No application can handle this action' error. Flag legacy clickable URL/intent handling in licences and acknowledgements for **removal/replacement during future About UI implementation**, rather than spending time debugging it now. The approved design has no clickable external links: show licence/attribution information within the third Settings panel and a **real QR code** per applicable entry pointing to its verified official site. QR links supplement, but do not replace, locally accessible legal notices and licence texts. No app code changes authorised now.

## User-approved display simplification
Main About screen must display **only** `Based on Nova Video Player 6.4.64` (no upstream SHA, commit link, or source-revision label). Derive this from the pinned upstream version during release preparation and update whenever the upstream basis changes. Preserve the pinned commit internally in release provenance only. This supersedes the earlier suggestion to show an optional SHA in the UI.

## Attribution reconciliation — initial matrix
| Group | Verified presence | User-facing treatment | Remaining evidence |
|---|---|---|---|
| Nova Video Player / Archos Video Player CE | Repo README, upstream build manifest and source copyrights | Main About lineage line for Nova; distinct credits for upstream Nova and Archos and their contributors | Exact original copyright/NOTICE and modified-file obligations; release-specific version pin |
| Existing 13 licence entries | `res/xml/preferences_licences.xml` | Preserve entries; third-panel locally readable licence and notice text, official project QR | Validate exact versions, SPDX IDs, native build options, transitive licences |
| AndroidX / Google libraries | Direct Gradle dependencies | Grouped open-source software licences and applicable copyright notices; avoid 20 redundant high-level rows if grouping remains legally complete | Resolved runtime dependency graph, individual NOTICE obligations |
| Picasso / OkHttp / Bouncy Castle / ZXing / ThreeTenABP / observablescrollview / logback / Sentry | Direct Gradle dependencies | Add entries where applicable, full licence/NOTICE text locally available | Confirm precise version and each package's actual licence at shipped revision |
| FFmpeg, dav1d, Opus, libmysofa, OpenSSL, libyuv, Boost, libtorrent and native stack | Pinned native build/prebuilt manifest | Dedicated third-party software licences and notices, QR project sites | Packaged binaries, build flags, LGPL/GPL applicability, source distribution / relinking duties |
| TMDb | Existing catalogue integration and tmdb-java | Distinct **service/data provider** credit, separate from tmdb-java library licence | Check current official attribution text/logo and usage terms for actual API |
| Trakt / OpenSubtitles | Existing inherited integrations | Provider credits when functionality ships; not conflated with software libraries | Current provider branding/terms; whether account integration is enabled |
| Streaming availability and put.io | Documented integration scope, put.io OAuth externally blocked | Attribute only actually shipped/activated service, not speculative integrations | Identify exact upstream availability data provider, brand rules, production enablement |
| SMB, WebDAV, SFTP, FTP, DLNA/UPnP | Transport capabilities, not necessarily provider businesses | Credit actual software implementations under library licences; **do not** treat protocols as commercial providers | Resolve module-specific transitive dependency tree |
| SkipDB, TheIntroDB, MDBList | Approved in broader roadmap, not verified in current shipped binary | Do not mark as active service partners unless actual integration is confirmed | Future release integration evidence and terms |

### Audit status and release gate
This is an evidence-based **inventory and gap assessment**, not yet a completed compliance certification. Existing 13-entry XML is insufficient to establish complete runtime/native attributions; transitive dependencies, source/binary licensing, FFmpeg configuration and provider terms remain to be verified against the actual release APK. Build/test-only dependencies must be distinguished from redistributed runtime code. QR codes are supplementary, not replacements for legally required licence texts and notices. No app code modified; legacy clickable links flagged for later removal only.


## Research closeout — 8 October 2026
**Status: repository and public-provider research complete to available evidence; binary-level licence compliance NOT certified.**

### Verified provider policies
- TMDB: https://developer.themoviedb.org/docs/faq requires approved logo and prominent About/Credits notice: “This product uses the TMDB API but is not endorsed or certified by TMDB.” Logo must not imply endorsement. QR: https://www.themoviedb.org .
- Trakt: https://developer.trakt.tv/docs/api-use-policy requires adherence to official branding guidance, approved unmodified assets, no implied endorsement, and API access rules. Existing inherited integration only; do not reopen parked metadata features.
- OpenSubtitles: https://forum.opensubtitles.com/t/opensubtitles-org-api-final-shutdown-notice-for-non-vip-users/5045 warns old .org API is being retired; verify whether Supernova uses .org XML-RPC or .com REST. Terms https://status.opensubtitles.com/en/tos/ . Exact API attribution remains to be established.
- FFmpeg: https://ffmpeg.org/legal.html and https://ffmpeg.org/doxygen/trunk/md_LICENSE.html explain LGPL baseline, optional GPL/nonfree components and distribution duties. Actual shipped build flags and corresponding source remain essential; existing XML 'LGPL v2.1' is not enough.
- NOVA: upstream pinned source declares 6.4.64. Main About wording only: **Based on Nova Video Player 6.4.64**; no revision shown to users; internal provenance retained and version updated on upstream rebase.

### Final UI register requirements
1. Main About: Supernova version, short Nova credit, Open-source licences, Credits & acknowledgements, Technical Information. No in-app updater.
2. Approved persistent three-column layout. Licence/credit details, full offline-readable texts, notices and real scannable QR for verified official URLs in third panel. No clickable external links.
3. Preserve and validate all existing 13 listed entries; add every actually distributed direct/transitive runtime and native dependency as required, with copyright, licence, NOTICE and source-availability obligations.
4. Distinguish actual software libraries from external service/provider credits; list only enabled integrations, not parked ideas; TMDB approved logo and exact mandatory notice are required.
5. Legacy clickable-link handling marked for removal in later implementation, not debugging now.

### Outstanding release-specific evidence — mandatory compliance gate
- Resolved releaseRuntimeClasspath dependency graph/SBOM for main app and external MediaLib/FileCoreLibrary modules, including transitive versions.
- Actual final APK native .so inventory, FFmpeg/native configure flags, source provenance and binary/linking obligations.
- Full matching licence texts and copyright/NOTICE requirements for every redistributed module, including upstream Nova/Archos changes.
- Confirm enabled provider endpoints, OpenSubtitles API generation, terms, branding and production credentials without exposing secrets.
- Validate final licence/credit screens, QR targets, offline text and APK distribution notices against the Foundation Release artifact.

**Conclusion:** The research phase is documented; no truthful full compliance sign-off is possible until the release build and its resolved dependencies/native binaries can be inspected. Block release sign-off if evidence is missing. No application code or fixes-only branch was changed.

