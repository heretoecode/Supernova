# Supernova 0.151 — owner Shield QA corrections (2 November 2026)

**Status:** implemented and committed on `claude/supernova-github-access-yfhe89`, built on the delivered 0.150 source (`8d3c46ab`). Automated build, tests, signing and Google Drive delivery run in GitHub Actions. **Physical Shield verification pending — nothing here is accepted until the owner confirms it on the Shield.**

## Corrections (each traced to an owner report or photo)

| # | Owner report | Change |
|---|---|---|
| 1 | Preparing playback: filter over the artwork | Removed the full-screen navy gradient; artwork is untinted. Text keeps a soft shadow. |
| 2 | HUD: runtime and Ends at need a separator | `01:02:26 · Ends at 12:05`; the dot shows/hides with Ends at. |
| 3 | HUD: blue band over letterbox bars | Transport scrim changed from navy `#071420` to neutral black. |
| 4 | Language flags missing (recurring) | Root cause: policy and tests only allowed flags for explicit regions (`en-GB`); real media/Settings use `eng`/`en`. New policy: explicit region wins, otherwise a per-language default (`en`/`eng` → GB), ambiguous languages keep the globe. |
| 5 | Documentaries: no visible focus on open | On a fresh open, focus stays on the page's own top-nav tab, like Movies/TV. Movies/TV untouched. |
| 6 | Settings toggles too wide | Shared 44×26dp (~1.7:1) face for every switch (was 70×26dp, 2.7:1). |
| 7 | Reception empty; votes at the panel floor | TMDb is a full row with votes beneath the score; order IMDb · Rotten Tomatoes · Metacritic · TMDb · Awards; empty rows omitted. |
| 8 | Wants Rotten Tomatoes / Metacritic / awards | OMDb enabled with the owner's own key: **Settings → Library & Home → Library & metadata → Ratings key (OMDb)**. Stored on the device only; 900 requests/day cap. |

## Decisions recorded (supersede earlier records where they conflict)
- **Flags:** language-only codes now take a default flag (owner, 2 Nov 2026). Supersedes "flag only for an explicit region" in `docs/design/SETTINGS.md` and `GLOBAL_VISUAL_SYSTEM.md`.
- **Reception:** TMDb is shown alongside OMDb sources; ratings only, no reviews.
- **Delivery:** signed QA APKs are uploaded automatically to the owner's Google Drive folder **"Supernova APKs"** and verified by a complete download (`.github/delivery/drive_upload.py`, `drive.file` scope only).

## Owner Shield checklist
1. Preparing playback: artwork crystal clear, text readable.
2. HUD: dot between runtime and Ends at; no blue band over black bars.
3. Settings → Audio & Subtitles language pickers: English shows 🇬🇧; Arabic/Catalan show the globe. Audio/subtitle tracks in a film show flags.
4. Open Documentaries: focus visibly on its tab; Down enters the page; statistics visible.
5. Settings toggles: narrower, states clear.
6. Enter the OMDb key, then Details → Reception on a well-known film: IMDb, Rotten Tomatoes, Metacritic, TMDb, awards; captions beneath scores.
