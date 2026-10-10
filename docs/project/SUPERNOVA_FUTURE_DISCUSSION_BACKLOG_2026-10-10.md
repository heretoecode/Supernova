# Supernova — Future Discussion Backlog
Date: 2026-10-10
Status: Discussion only / not approved for implementation

This document records subjects to discuss after 0.135. It explicitly excludes the 0.135 APK installation, walkthrough, testing, defect triage and related follow-up. Items below are proposals, not commitments, approved requirements, or instructions to Codex. Do not start implementation or alter release branches based on this document.

## Technical improvements and optimisation
- **APK file size investigation (new):** Compare original Nova Video Player (~30 MB) with Supernova (>100 MB). Read-only analysis of actual APK contents: native libraries and ABI duplication, packaged images and mockups/assets, unused/duplicate resources, debug symbols, DEX/dependencies, compression, code/resource shrinking and build configuration. Compare like-for-like APK variants. Report safe options without changing the current build, signing, package identity or functionality.
- **Kotlin / Jetpack Compose:** Discuss whether partial or broader migration from Java is justified, and what practical improvements it offers.
- **AI integration:** Evaluate worthwhile AI-assisted metadata matching, recommendations or natural-language search; weigh cost, privacy and complexity.
- **Performance optimisation:** Discuss startup, responsiveness, memory and library scaling as a longer-term initiative.

## Library and discovery
- **Multiple custom Library Pages:** Revisit current one-page limitation and management UX.
- **Discovery / Coming Soon:** Revisit parked dedicated discovery concept.
- **Anime section:** Consider specialised metadata, episode/season organisation and navigation.
- **Smart Collections:** Assess whether separate collections add value beyond Smart Home Rows and custom Library Page.

## Profiles and personalisation
- **Multiple user profiles:** Separate histories, Continue Watching, preferences and possibly libraries.
- **Additional personalisation:** Consider layout, theme and navigation choices without overcomplicating Settings.

## Playback and subtitles
- **Seek-bar thumbnail previews:** Evaluate preview generation, performance and storage.
- **Advanced subtitle presets:** Discuss saved subtitle appearances and granular controls.
- **Expanded playback intelligence:** Future compatible-version selection and playback decision improvements beyond the approved 0.135 scope.

## Metadata, ratings and integrations
- **Expanded ratings:** IMDb, Rotten Tomatoes and other sources; check licensing and APIs.
- **Trakt community reviews:** Assess community ratings, reviews and social features.
- **Cast & Crew profile pages:** Revisit dedicated people pages and filmographies.
- **Additional integrations:** Consider other valuable metadata/media services without duplicating current functionality.

## Suggested discussion order (not a release schedule)
1. APK file size investigation
2. Performance optimisation
3. Expanded ratings and metadata
4. Multiple custom Library Pages
5. Seek-bar thumbnail previews
6. Multiple user profiles
7. Discovery and Anime pages
8. Kotlin / Compose and AI

## Governance
- Every entry remains **for discussion** until explicitly approved by the user.
- Do not assume any entry is in the next APK or future release.
- Keep this backlog separate from the 0.135 QA walkthrough, test results, defects and delivery work.
- For APK size, the first step is read-only measurement; do not optimise or rebuild without approval.
