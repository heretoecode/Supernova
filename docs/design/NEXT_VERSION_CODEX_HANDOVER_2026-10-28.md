# Supernova — Next Version Codex Handover (after 0.143)

**Prepared:** 2026-10-28. **Status:** READY FOR CODEX INVESTIGATION AND IMPLEMENTATION; no application implementation or APK is claimed by this document.

## Mandatory start / branch safety
Repository: `heretoecode/Supernova`. Last user-reported delivered APK: **0.143**, source commit `7b826443363af00b236e5b6af309046456d719df`, draft PR #23. **Verify the live repo, branches, PRs, current build and release/version status before choosing a base.** Do not blindly assume the reported 0.143 commit is the latest code.

- Preserve all existing code, uncommitted work, signing configuration and user data. Do not reset/revert/discard work.
- Do not merge to `main`, publish a release, or overwrite 0.143 without explicit authorisation.
- Create/use a dedicated next-version implementation branch after verifying the authoritative baseline; preserve this docs branch as requirements source. Do not implement on the documentation branch.
- Verify target version and version code before changing them; use the next appropriate version rather than inventing one.
- First compare prior approved mock-ups, audits, requirements, previous completion claims and existing source; record a concise conformance matrix with each item marked **not started / implemented / automated-tested / visually Shield-verified**. Never conflate these states.
- Preserve approved Supernova branding, Space Black blend, current application identity, signing and storage/account configuration.
- Ensure all source references and mock-up images are actually accessible to Codex. If an image exists only in chat and not GitHub, flag the missing asset rather than claiming visual parity.

## P0 — Functional reliability and navigation
1. **WebDAV stale records and deletion:** Files deleted independently from WebDAV days earlier still appear in Supernova; selecting Delete fails. Distinguish **remove library entry** from **delete remote file**. Handle remote 404/missing source safely and reconcile on confirmed complete scans. Never purge library records on timeout, auth/network failure, partial listing or unavailable storage. Preserve source data and avoid accidental remote deletion. [Requirements](NEXT_VERSION_WEBDAV_STALE_LIBRARY_FIX.md).
2. **Library page focus and artwork:** Third custom page **Documentaries** loses visible D-pad focus on entry/switching; Movies/TV Shows appear stable. Background sometimes retains first movie's artwork instead of the active Documentaries first item. Give each page independent focus/background state; first item is initial artwork; subsequent focus changes follow focused item; cancel/ignore stale async results. Test rapid page switching, empty/missing art and data refresh. [Requirements](NEXT_VERSION_LIBRARY_PAGE_FOCUS_AND_BACKGROUND.md).
3. **Scan Library button:** Prominent action in **Network & Files > Overview** to run existing scan pipeline immediately, with progress, result/error and no duplicate jobs. Coordinate safe WebDAV reconciliation. [Requirements](NEXT_VERSION_NETWORK_FILES_SCAN_ACTION.md).

## P1 — Approved UI conformance and playback
4. **In-player Playback Settings — Concept A4 is the visual baseline, not inspiration.** Restore compact dark translucent panel with tabs **Audio / Subtitles / Video / More**, aligned two-column groups and clear D-pad focus. **Video format choices inline**, no `Format` submenu. Audio tracks on left; Sync/Enhancements on right. Keep all real supported functions and don't invent modes from illustrative mock-up labels. Compare against user-supplied Concept A4 Audio and Video images. Rework Play/Pause control to fit the HUD. [Requirements](NEXT_VERSION_IN_PLAYER_SETTINGS_AND_PLAY_PAUSE.md).
5. **Global Settings across all eight categories:** Shared clear On/Off switches distinct from focus, clean spaced/separated rows instead of permanent individual-setting cards; preserve eight category cards and all preference keys/dependencies. [Requirements](NEXT_VERSION_GLOBAL_SETTINGS_ROW_AND_TOGGLE_DESIGN.md).
6. **Settings navigation/layout:** Open Settings as a full-screen destination without slide-in overlay; ensure eight-category 4×2 landing grid has equal horizontal and vertical gutters. Investigate subsection transition flicker/overlap. Reuse approved Settings blueprint and mapping: [Blueprint](SETTINGS_FROM_SCRATCH_BLUEPRINT.md), [Mapping](SETTINGS_AUDIT_MAPPING.md). Do not regress the 165 mapped preferences.
7. **Settings language flags and copy:** Restore *locale-specific* flags where region is explicit, neutral language icon otherwise; verify real Android TV assets render, not just emoji strings. Replace generic boilerplate text in settings; Audio Output & Tracks suggested description: `Configure audio output, playback compatibility and preferred audio tracks.` [Requirements](NEXT_VERSION_SETTINGS_LANGUAGE_FLAGS_AND_COPY.md).
8. **Playback preparation/HUD:** Eliminate unrelated/stale textured artwork while loading; correct cached art or neutral placeholder. Raise whole bottom HUD safely, keeping subtitles clear. Seek timestamp tooltip closer to bar with pointing notch and end-of-bar clamping. [Requirements](NEXT_VERSION_PLAYBACK_PREPARATION_AND_HUD_REFINEMENTS.md).
9. **Age rating on playback start:** Approved upper-left certification overlay, 10 seconds then gentle fade, white text/thin black outline/slim red accent, no panel, region-appropriate rating, no display when unknown. No content advisories yet. [Requirements](NEXT_VERSION_PLAYBACK_AGE_RATING_OVERLAY.md).
10. **Details pages:** Remove `Read Reviews`/written reviews and restore/display ratings. Preserve the approved details design and existing useful metadata. Correct hero vertical spacing, keep metadata on **one line** with graceful truncation; verify movie and TV variants, badges, synopsis and action positions. [Hero requirements](NEXT_VERSION_DETAILS_HERO_SPACING_METADATA.md). Inspect previous details QA and implementation history before editing.

## P2 — Diagnostic audit
Review the user-provided diagnostic report and existing recorded investigation for scan/player/metadata/network/focus/unclean-session events. Separate historical events from current reproducible defects. Fix substantiated issues without expanding scope indiscriminately. Do not claim the report was re-analysed unless the original artifact is accessible.

## Acceptance and evidence
- Maintain a traceability checklist for **every** item above, linked to actual source changes, automated tests and visual evidence. For previously missed work (flags, Concept A4), require **actual Shield screenshots/video** compared against approved references before marking visually verified.
- Test Android compilation, existing automated tests, lint, regressions, focus/navigation, screen scaling at 720p/1080p/4K and large text, playback/resume/seek, movie/TV/custom library pages, WebDAV offline/error/404/reconnect and scan progress.
- Distinguish **automated validation** from **physical Nvidia Shield QA**. If hardware inaccessible, mark physical QA pending, never passed.
- Preserve signing safety and exact-source APK reproducibility. For eventual delivery, record source commit, signed APK SHA256, version code, Drive upload and release notes; do not claim delivery until actually verified.
- Update human-readable release notes and QA checklist; include screenshots/visual comparisons where possible.
- Prefer one coherent QA/fix implementation cycle over piecemeal cosmetic patches; document deviations from approved mock-ups and their concrete technical reasons.

## Canonical references
All linked `NEXT_VERSION_*.md` files in `docs/design/` are required reading, not optional suggestions. Also review `docs/design/DETAILS_TAB_QA_CHANGES_2026-10-27.md`, `docs/design/CODEX_WORK_QUEUE_2026-10-27.md` and relevant previous release QA. In conflicts, preserve later explicit user-approved direction and flag unresolved contradictions for approval.
