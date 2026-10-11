# Development HTML release notes

The reviewable source is [Supernova Development.html](<Supernova Development.html>). It was imported from the existing Drive file for the 0.137 documentation correction; previously no HTML release-notes source was tracked in this repository.

The published copy remains [Supernova Development.html on Drive](https://drive.google.com/file/d/17P05N7Q8c0TQdeZIEjUKYoQ-4xZIz-DW/view?usp=drivesdk), in the existing Supernova folder. Preserve that file's ID, name, parent and sharing settings. It is a standalone HTML document intended for browser viewing after download; Drive may show a preview instead of rendering it as a website. Publication currently means replacing its bytes manually through the connected Drive file-update action, then downloading it again and comparing its hash with this source. No new folder, replacement file, public sharing or OAuth setup is needed.

## Maintenance

Follow the existing [delivery policy](../PREVIEW-DELIVERY.md): latest delivered QA entry first, preserved history, exact source/version/hash, actual checks and explicit physical-test limitations. Keep the existing stylesheet, page structure and prior release articles. Do not label a delivered APK as installed, physically accepted or publicly released without corresponding evidence. Update the source in the development PR, publish it to the same Drive file and verify the readback when delivering each APK.

## How Supernova displays release notes

This HTML is outside the Android assets directory and is not referenced by application code. Inspection of the delivered `Supernova_0.137.apk` found **no `.html` entries**. The app's About → Release Notes view instead reads `foundation/release-history.json` in `FoundationAboutWorkspace.releases()` and renders native text views. The delivered APK's `assets/foundation/release-history.json` already has the correct 0.137 current entry and its functional/UI summary, matching the source asset at the build commit.

Consequently, updating the Drive HTML does not update the native About text, and the delivered APK does **not** contain the obsolete 0.136 HTML. No rebuild is necessary for this correction. Changing the separate bundled About notes would require a new APK; that asset and the already delivered binary are untouched.

## 0.137 correction evidence — 11 October 2026

- APK source: `68caa2c0ac3c42073b8ed2168995c2316625b29a`, from the existing development branch and draft [PR #12](https://github.com/heretoecode/Supernova/pull/12).
- Changes: [source-pinned changelog](https://github.com/heretoecode/Supernova/blob/68caa2c0ac3c42073b8ed2168995c2316625b29a/docs/next-qa/RELEASE_NOTES.md), [implementation findings](../next-qa/PROGRESS.md), the actual changes relative to PR #10's `91a502a7355c637b2630990e15a2582aa96a0329` baseline, and the `NextQaReliabilityTest`, `NextQaBrowserFocusTest`, `NextQaUiTest` and `NextQaWizardTest` regressions. The progress report predates completed CI/signing; its pending-build statements are superseded by the successful runs below, not by assumed device acceptance.
- Exact-source unsigned validation: [run 38109927407](https://github.com/heretoecode/Supernova/actions/runs/38109927407). Successful signing and repeated conformance: [run 38134479237](https://github.com/heretoecode/Supernova/actions/runs/38134479237), completed 11 October 2026. Its public evidence reports 528 application tests, no failures/errors/skips, zero lint errors with 1,496 warnings, 138 reviewed dependency hashes, 88 native-library hashes and 24 rebuilt FFmpeg libraries matched. The tooling suite passed 67 checks; native regression evidence records 64 decode cases across four ABIs, not device playback.
- Delivered `Supernova_0.137.apk`: `app.supernova.player`, version `0.137`, version code `137`, SHA-256 `d999db6387ec380de22818d3324c1acf4ffa6d956a3961f594878048644325fe`. Signing certificate SHA-256 `79ed34c52c3e359756ade0634d7bbb6f8e94e42a059020c1220bd8c7092a9f5e`, independently matched to 0.136. Signed payload matches the tested unsigned build. The [existing Drive APK](https://drive.google.com/file/d/1Jc1UnpDufFQdUAMHBSvbSOIeMQojRLpd/view?usp=drivesdk) was downloaded again and its bytes/hash verified after upload.
- Physical NVIDIA Shield acceptance and an actual signed upgrade remain untested; use the [0.137 checklist](../next-qa/SHIELD_QA.md). The HTML distinguishes reproduced automated regressions from device-dependent scanning, playback, layout and hero timing.
- Correction checks: original stylesheet and all four earlier release articles preserved byte-for-byte; 0.137 is the first release; document tags and unique headings checked; APK version, build SHA and checksum cross-checked with signed evidence. This is documentation only, with no APK rebuild/replacement or merge.
- Published the corrected HTML into the same Drive file on 11 October 2026 and downloaded it again: all 16,801 bytes match the repository source, SHA-256 `c956b09853415372fef73356e095b77aaf185cad92323ad7349e76f52a3146e5`. File ID, name, parent and private sharing state were preserved.

## Release-process validation finding

The current release gates validate the bundled JSON version counter, historical evidence and APK asset parity (`.github/build/verify-next-qa.py`). They do **not** validate the external HTML or its Drive publication. The manual delivery policy requires an update, but there was no tracked HTML for CI to check. This explains how the 0.137 APK could pass its gates while the external page still showed 0.136.

Automatic validation **should** be added to the release process: a lightweight, secret-free check before signing should compare the first HTML release heading with `assets/foundation/release-history.json`'s current version, require a version-specific changelog and explicit QA limitations, and reject a missing/stale entry. Source entries can say “QA candidate” before signing; mark delivery only after successful artifact verification. The post-delivery check should compare the published Drive bytes/hash with the committed source. This does not require an APK rebuild or automated OAuth/publishing setup.

This correction records that recommendation; it does not change signing/build workflows or enable an automatic gate. All changed files are under `docs/`, which the existing 0.137 unsigned-conformance push workflow excludes; signed QA builds require explicit dispatch. Future workflow work can add the check without broadening this documentation-only correction into another build or release.
