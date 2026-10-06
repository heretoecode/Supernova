# Supernova

Supernova is a custom Android TV / NVIDIA Shield video-player build derived from NOVA Video Player.

## Start here

**For the current project state, read [PROJECT_STATUS.md](PROJECT_STATUS.md) first.**

The repository is intended to be the durable source of truth for the project. Historical handovers, audits and checkpoint documents are retained for traceability but must not override the current status and linked authoritative records.

## Current baseline

The accepted development baseline is **Preview 4.1.7 Final**.

- Authoritative application/test source: `7efb4195639205a6526281491af40c0dab1776b7`
- Final APK: `Supernova-4.1.7-FINAL-7efb4195.apk`
- Full build, hash, certificate and validation evidence: `docs/preview-4.1.7/IMPLEMENTATION_REPORT.md`
- Current physical Shield QA / fixes-only scope: `docs/qa/4.1.7-final-shield-qa.md`
- Project decisions and parked roadmap: `docs/project/DECISIONS_AND_ROADMAP.md`

The cleaned Preview 4.1.7 lineage was promoted on 6 October 2026, and `main` is now the authoritative repository branch. The exact source used for the accepted 4.1.7 Final APK remains the frozen commit listed above.

## Repository and build

The application uses supporting NOVA-derived modules including MediaLib, FileCoreLibrary and native components. CI pins the supporting dependencies in `.github/build/nova-ci.xml` for reproducible builds.

The customised MediaLib fork is maintained separately at:
https://github.com/heretoecode/aos-MediaLib

The upstream NOVA `aos-AVP` repository is used only as a bootstrap manifest source by the current CI process. The personal `heretoecode/aos-AVP` fork is not required by the current Supernova build.

## Upstream and attribution

Supernova is derived from NOVA Video Player:
https://github.com/nova-video-player

NOVA itself derives from the open-source Archos Video Player Community Edition.

See the application's licence/acknowledgement material and repository licence files for applicable attribution and licensing.
