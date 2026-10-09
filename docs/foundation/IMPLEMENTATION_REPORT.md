# Foundation implementation and APK delivery

**In progress. No Foundation APK has been signed or delivered. `FOUNDATION_RELEASE_READY` has not been changed.** Authorised Foundation source starts at `56864849`; signing implementation and all later handover commits are retained. Work is confined to `codex/foundation-release`.

## Preflight

Latest handover, signing setup, release sequence, technical readiness, approved About specification, branding decisions/assets and licence/credits audits were reviewed. Full Foundation implementation is authorised; signed `prepare` dispatch requires a separate user approval after readiness evidence. Physical Shield QA is deferred until delivery.

GitHub independently confirms successful signing setup runs [37877953965](https://github.com/heretoecode/Supernova/actions/runs/37877953965) and [37877971212](https://github.com/heretoecode/Supernova/actions/runs/37877971212). A subsequent [37878964877](https://github.com/heretoecode/Supernova/actions/runs/37878964877) failed certificate-pin format validation; its fixed diagnostic was inspected without displaying credentials/raw logs. The checkpoint's public pin has 63 hex characters; user asked to correct the public environment variable from the original trusted inspection, without supplying any key/password. The integration cannot read environment variables (403). The environment's branch policy exists; no required-reviewer rule was returned, so the explicit conversational readiness approval remains essential. No environment policy was altered.

## Planned deliverables

Permanent application/package/provider identity with isolated sibling contracts and no shared UID; approved resource exports and utility backgrounds; two lifecycle-bound counter-rotating double-ring indicators; approved five-section About navigation and offline licences/QR/credits; literal `0.N` history and monotonic internal versionCode; unsigned conformance build, runtime/native dependency evidence, tests and source review; then user-approved signing, actual APK identity/certificate/checksum verification and Actions artifact delivery. Existing playback/library/network behavior, Preview signing and maintenance branch stay outside this change.

## Verified APK history

All **132 retained APK artifacts** were downloaded through the connected GitHub artifact service and curl, archive hashes matched GitHub digests, and Android aapt2 inspected each embedded package/version. The earlier Forbidden download finding was resolved by using the supported artifact URL with curl. [APK_HISTORY.json](APK_HISTORY.json) contains full binary hashes and original embedded versions, workflow/run/source/date evidence and literal retrospective labels `0.1` through `0.132`. Foundation candidate is **`0.133` / versionCode 133**. These are independent APK builds, not 132 feature releases. Two separate successful optimised build jobs at one source produced byte-identical APKs (131 distinct hashes); both actual builds remain recorded.

The oldest retained APK is the first *verifiable* Supernova-lineage build, not independently proven to be the first APK ever created. Deleted/unrecorded history cannot be recovered from retained artifacts. Notes identify actual source checkpoints and explicitly distinguish documentation/build-only checkpoints; they do not invent feature changes or device acceptance. Original APK binaries and embedded versions are unchanged. Unsigned conformance candidates are not installable releases and do not consume additional published release counters.

## Public signing pin correction evidence

The additional read-only [validation run 37880448924](https://github.com/heretoecode/Supernova/actions/runs/37880448924), source `ca0a8df6`, independently verified the private key/password/certificate match and emitted only the public SHA-256 before rejecting the still-truncated configured pin. Verified public fingerprint: **`79ed34c52c3e359756ade0634d7bbb6f8e94e42a059020c1220bd8c7092a9f5e`**. A missing `6` in the original checkpoint explains the 63-character transcription. The public value was supplied to the user for the exact protected-environment variable correction; no credentials changed or exposed. Do not infer pin acceptance until a fresh validation succeeds.

## Native provenance finding

Pinned FFmpeg prebuilt binaries identify **FFmpeg n8.0.1** and **LGPL version 2.1 or later**, although the newer pinned builder script clones n9.0.1 when rebuilding. Therefore the builder script's current version is **not** a valid version claim for the shipped prebuilt. Actual ELF configuration strings, component versions and licences are being traced; final binary notices must describe the actual prebuilt, not its wrapper repository's Apache licence. No library version upgrade is included in Foundation.

## Implementation checkpoint

- Permanent identity is opt-in through `foundationRelease` (protected signing) or `foundationVerify` (unsigned release verification). Java namespace stays Archos-derived. Pinned build-copy preparation removes shared UID, isolates media/scraper/browser/FileProvider contracts and task affinity, and preserves registered external OAuth schemes and standard protocols. Preview configuration remains separate.
- Approved original Space Black and alpha ring files are retained unchanged. Platform exports provide five launcher densities, adaptive-icon safe foreground, TV banner 320×180, static launch composition and utility base/background. Home keeps its existing hero artwork and behavior over the approved base. Roboto Light raster typography comes from the official Google Fonts source; its hash is recorded; the font itself is not redistributed.
- Both existing Preparing Playback and Home floating scan-status pill use stationary concentric rings with constant opposite highlight/trail motion, governed by existing loading visibility. Hidden/detached/destroyed indicators stop. Existing playback preparation, scanning and first-frame timing are retained.
- About alone has five child sections in the left rail, locally readable release notes/licences, provider QR/credits and read-only technical details. Current release stays expanded; older releases expand independently and retain state. No updater or clickable external URLs are added. NOVA lineage wording remains exactly “Based on Nova Video Player 6.4.64”.
- Foundation literal version state is handled separately from NOVA's semantic/date parser. Legacy source-era NOVA migration comparisons must not interpret `0.N` as an old NOVA release and repeatedly reset user preferences or clear caches. Existing NOVA/Preview paths are preserved.

## Automated evidence so far

The first unsigned optimised release APK built successfully (`assembleNoamazonRelease -PfoundationVerify -Puniversal`), package **app.supernova.player**, version **0.133**, code **133**, 101,475,372 bytes, SHA-256 **d69875be7159b7d9004ad1b6cd690ad7813485c06169b709075f8cf5e59989f0**. Its 88 native ELF hashes match merged build inputs. Binary providers all use the permanent identity; no shared UID or legacy authorities remain. This checkpoint APK precedes the final native-notice/icon/version-state additions; final conformance must be rerun. It is unsigned and has not been delivered as installable.

The full unsigned release Java unit suite passed **412 tests, zero failures/errors/skips** before later targeted additions. New About release expansion/Back/QR tests passed separately. Python tools suite passed **24 tests**; actionlint passed the Foundation workflow. Release lint completed with **zero errors** and 1,439 warnings across the inherited app/modules (not a claim that all warnings were fixed). Later code/assets require another full verification.

The resolved graph contains **138 external artifacts**, including core-library desugaring and its configuration; runtime SBOM retains exact coordinates, hashes, POM licence evidence, inherited parents and original archive notices. Offline legal entries additionally cover native/upstream code. Actual packages corrected inherited labels: tmdb-java/trakt-java Apache-2.0, JUPnP CDDL-1.0, JSch BSD/ISC; OpenJDK desugaring GPL with Classpath exception. AVOS includes separately attributed LGPL VideoLAN deinterlacing and MIT NEON routines. Wrapper/prebuilt repository licences are never used as codec-library licences.

## Unresolved native source gate

Actual FFmpeg ELF markers on four ABIs report **n8.0.1**, with LGPL-2.1-or-later and no GPL/nonfree/version3 enablement, while prebuilt commit subject says 8.0.3 and pinned builder targets 9.0.1. The exact matching corresponding source is not independently established. Verified upstream n8.0.1 commit **894da5ca7d742e4429ffb2af534fcda0103ef593** accepts both pinned `config_opus.patch` and `atempo.patch`; this is a candidate source reconstruction, not proof those exact sources produced the prebuilt. User decision requested: rebuild the same observed version/features/four ABIs from explicit source, or retain bytes and hold signing until matching source provenance is supplied. No native replacement is authorised by an unanswered question.

Other wrappers also differ from actual binary evidence: Opus reports `libopus 1.6.1-9-g2d862ea1`; libtorrent reports `2.0.14.0` despite commit subject 2.0.24; `mysofa_getversion()` disassembly reports **1.3.3**. Follow-up checked the actual v1.3.5 upstream CMakeLists.txt: it still defines patch version 3, resolving this as upstream version metadata retained in tag v1.3.5, rather than proof of a different source release. Their permissive licences/notices are retained conservatively with evidence limitations; actual version evidence controls display. FFmpeg corresponding-source duties remain a blocking release gate, not a licence certification.

Latest protected validation [37882848273](https://github.com/heretoecode/Supernova/actions/runs/37882848273) still received the truncated 63-character public pin and failed safely. Exact 64-character correction was requested for the protected environment variable. No Foundation signed APK exists; readiness is false and `prepare` has never been dispatched.

The v1.3.5 libmysofa tag uses `CPACK_PACKAGE_VERSION_PATCH "3"`; exact upstream file was inspected at https://github.com/hoene/libmysofa/blob/v1.3.5/CMakeLists.txt. Runtime function values and source tag are reported separately.

## Latest source-test checkpoint

Full unsigned release unit results after identity/migration/Classic About additions: **419 tests across 106 classes, zero failures/errors/skips**. Focused host recreation, literal version/runtime identity and user preference preservation passed. Python source/runtime/binary/readiness guards: **28 tests passed**. An earlier reflection-only identity fixture failed because it had not initialised the application logger; the fixture now supplies the same logger/package state established by real startup, and the full suite was rerun successfully. No product code was changed to suppress the test failure.

Classic/mobile existing Build/licence/provider-credit entry points now route to the same approved Foundation offline About host. This prevents stale Preview/upstream version text and external licence-link launches while leaving other Settings and interface choices intact. Android 12's platform colour-only splash first phase uses the approved 4K asset's top-centre pixel `#000206` and a static safe-area ring bitmap; the app launch surface uses the complete approved background/ring/wordmark composition with no added timer.
