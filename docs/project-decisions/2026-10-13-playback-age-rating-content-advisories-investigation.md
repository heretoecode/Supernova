# Investigation brief — playback-start age rating and content advisory overlay
Date 2026-10-13. Status: new owner idea, **investigation / discussion only**, outside 0.139 and maintenance release. No mockup or implementation approved.

## Owner intent
When playback starts, show a **temporary hovering indicator in the top-left** containing an age classification and **tags explaining the classification for that specific movie or episode/file** (e.g., violence, strong language, sex, drugs, frightening scenes where supported by a legitimate source). This must reflect **actual title-specific classification evidence**, not inferred from genre, title or generic age category. No permanent player chrome or disruptive focus.

## Preliminary source/document investigation (limits)
- Reviewed owner 0.139 scope, 0.138 QA/roadmap and current planning documents. No existing approved implementation of a playback-start age/advisory overlay identified in those records. Broad HUD redesign is separately parked; 0.139 metadata/rating work is not an approval for this feature.
- Searched the connected GitHub repository's **default-branch indexed code** for `contentRating`, `certification`, `ageRating`, `parentalRating`, `mpaa` and `rating descriptors`: no matching indexed results. **This does NOT establish that the app lacks certificates, metadata APIs or provider fields**; code search may not index the working 0.139 branch or relevant files. A full source inspection is still required.
- Source evidence for availability of per-title *reasons* is currently **unverified**. Age certificate alone does not reliably encode reasons. External ratings systems differ by country, authority, medium and edition, and provider terms/API licences matter. Do not fabricate descriptors, scrape arbitrary websites, or assert all titles have them.

## Required read-only engineering/data investigation
1. Trace movie and TV metadata/scraper stack (NOVA/TMDB/Trakt/local NFO/embedded fields), existing certification and region fields, database schema, playback launch/title identity and any preloaded metadata. Inspect both current baseline and ongoing 0.139 work without touching Codex branches.
2. Identify trusted lawful sources of **region-specific certification** and **title-specific advisory descriptors**, coverage for movies vs TV series vs individual episodes, language/localisation, provenance, API/licence terms, offline/cache availability, cost/rate limits and fallback. Check differences between e.g. BBFC/IFCO/MPA/TV ratings without assuming equivalent labels.
3. Determine user's classification region (app setting/device locale versus explicit preference); prevent misleading cross-region mixes. If ratings differ across cuts/releases, state limits; never claim the specific local file's cut has been rated unless verified. A file's content is not automatically inferable from filename.
4. Define data confidence/fallback ladder: verified regional certificate + verified advisories -> certificate only -> concise 'Rating information unavailable' or no overlay (owner choice). Do **not** generate advisory tags from certificate category alone or language-model guesses. Show provenance and distinguish generic descriptions from title-specific advisory reasons.
5. Check runtime: playback-start hook after real playback begins (not click), overlay top-left TV safe area avoiding subtitles/player status, poster/backdrop overlays and system accessibility; display duration/fade/repeat/seek/resume behaviour; no D-pad focus capture; readability and scaling at 720p/1080p/4K; audio-described/TalkBack handling. Avoid slowing playback or network calls on critical path.
6. Assess privacy: title-level API lookups may reveal viewing activity to providers; prefer existing metadata/cache, explicit consent if new service needed, no credentials exported to backups, retention/bounds and diagnostics sanitisation.
7. Produce an evidence matrix of representative movie/series/episode records with certificate, advisory source and gaps, licensing/cost and UI feasible states. Only then discuss exact overlay design and owner approval.

## Provisional UX recommendation (not approved)
- Passive top-left soft-surface compact overlay; unboxed monochrome icon if needed, age badge and at most a few concise **verified** advisory tags. Short dwell/fade after playback actually starts; no focusable controls; never block content or permanently occupy screen.
- When source has only a certificate, display certificate only; if neither, omit overlay rather than invent labels. Consider user option to disable feature once design is approved; do not add to 0.139.
- Need owner decisions on display duration, whether show on resume/next episode, region/source preference, hide/skip when missing tags, and whether overlay should appear for all content.

## Investigative outcome
**Feasibility is plausible but not source-verified.** Largest unresolved issue is obtaining reliable title-specific content reasons, not drawing the overlay. This brief is a handoff for a separate read-only source/provider investigation after current 0.139 work, not permission to modify code.
