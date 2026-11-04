# To revisit later

Ideas and decisions the owner wants to come back to. Check this list when planning each release.
Gathered 4 Nov 2026 from this session and the older registers (`docs/Future Development/`, `docs/design/PARKED_FUTURE.md`,
`docs/project-decisions/POST_0139_NON_MAINTENANCE_FEATURE_BACKLOG.md`, `docs/design/CODEX_WORK_QUEUE_2026-10-27.md`).
Items already built or scheduled are left out (see the bottom of this file).

## Ratings without a personal OMDb key — added 4 Nov 2026
- **Decision (owner):** do not build an OMDb key into the app for now. Reception's IMDb, Rotten Tomatoes and
  Metacritic scores keep using the key typed into Settings (`omdb_api_key`, `metadata/OmdbSupplement.java`,
  900 lookups/day budget).
- TMDB "just works" because Supernova uses NOVA's own TMDB key shipped in the app
  (`aos-MediaLib/res/values/donottranslate.xml`).
- Agreed for 0.156: OMDb ratings are saved per title and refreshed at most weekly
  (`leanback/details/PreviewDetailsData.java`).
- **Options:** (1) one key built in at build time from a GitHub secret, so ratings need no user action (free tier
  1,000/day, paid tier more; a key inside an APK can be extracted); (2) MDBList (adds Letterboxd/Trakt; also a key);
  (3) keyless scores only (TMDB, Trakt) without IMDb/RT/Metacritic.
- **Revisit when:** Supernova is shared with anyone else, or the daily limit is reached.
- Notes: `docs/0.155/OWNER_NOTES.md` item 34, `docs/0.155/DATA_PROVIDERS.md`.

## Diagnostics privacy — added 4 Nov 2026
- Diagnostics record everything in full detail because the owner is the only user. Cut back (or make opt-in) before
  Supernova is ever shared. Notes: `docs/0.155/OWNER_NOTES.md` item 29, `docs/codex/BRIEF_0156_THUMBS_DIAGNOSTICS.md`.

## From this session (4 Nov 2026)
- **Discovery in Search** — owner: "maybe in the future but not for now". Search stays library-only in 0.156
  (`docs/0.155/OWNER_NOTES.md` item 20).
- **Rectangular QR codes** — not used because most phone cameras can't read them; square kept (item 32).
- **Streaming launch from Where to Watch** — code says launching a service is "paused for this testing edition"
  (`streaming/StreamingRepository.java:32`). Decide whether Where to Watch should open the service's app.

## Parked ideas from the older registers
| ID | Idea | Where it stands |
|---|---|---|
| FD-004 | Shield Home / Android TV launcher channels (rows on the Shield home screen) | Parked; which rows, consent and on/off controls undecided |
| FD-011 | Profiles (separate history, Continue Watching, settings per person) | Parked |
| FD-013 | Discovery / Coming Soon page | Parked; Home must not become advertising |
| FD-016 | Person pages (cast and crew filmographies) | Parked |
| FD-017 | More than one custom Library Page | Parked; check the current limit when revisiting |
| FD-018 | Anime-specific metadata and seasons | Concept only |
| FD-020 | Subtitle presets (saved appearance sets) | Concept only |
| FD-021 | Playback intelligence (smarter version/track choices) | Concept only; per-series audio/subtitle memory is in 0.156 (in principle) |
| FD-022 | Expanded ratings | Partly built (OMDb with the owner's key) — see the OMDb entry above |
| FD-023 | Trakt community reviews and social features | Concept only |
| FD-024 | AI features (matching, recommendations, natural-language search) | Concept only; cost and privacy concerns |
| FD-025 | Personalisation (layouts, themes) | Concept only; note Accent & Colour was removed in the Settings review |
| FD-026 | Other integrations (more metadata/media services) | Concept only; avoid duplicating current providers |
| FD-027 | Downloads (copy WebDAV files to the Shield) | Parked |
| FD-028 | Detailed content warnings at playback start | Parked; no reliable source found (TMDB descriptors empty) |
| FD-032 | Fully automatic Google Drive release publishing | Parked; today the signed APK is a GitHub artifact because Drive secrets aren't set |
| — | Smart Collections | Needs a decision |
| — | Put.io improvements (account, linking, file workflow) | Needs product discussion |
| — | Less manual GitHub work at the end of Codex sessions | Parked by the owner (workflow/permission automation) |

## Moved out of "parked" (built or scheduled)
- Seek thumbnails (FD-019) — experimental in 0.156. Library Health (FD-015) — built. Network & Files and Settings
  redesigns, playback HUD (FD-002/005/006) — built or in 0.156. Broader screen audits (FD-029) — done through the
  0.154–0.156 reviews. Age rating overlay (FD-001) — built.
