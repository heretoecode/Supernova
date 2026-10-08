# Parked & Future Product/Design Work

Status: **PARKED / NEEDS DECISION — NOT CURRENT IMPLEMENTATION SCOPE**

This file preserves future work so it is resumable rather than reduced to feature names. It must be read together with `docs/project/DECISIONS_AND_ROADMAP.md`.

## Future identity transition — APPROVED direction, mechanism NEEDS DECISION
Later fresh Supernova identity:
- fresh-install model;
- new application/package ID;
- new signing key;
- complete Supernova branding;
- clean app data;
- no requirement to upgrade from the old Preview identity;
- ideally coexist with old/upstream Nova on Shield;
- validate from first launch.
Do not assume Mark can generate/manage a desktop keystore; development workflow must accommodate iOS-only project ownership.
Exact package/version/signing mechanism remains a future decision.

## About/versioning/branding — PARKED
Clean fresh-baseline versioning later. About should present Supernova cleanly while retaining required legal/upstream attribution and useful build/version identity. Remove obsolete AOS/Nova-facing branding where legally/technically appropriate; do not erase attribution.

## Profiles — PARKED
Profiles are a future product feature, not part of the fixes-only release. Before activation define library/history/watch-state/provider/settings scope per profile, switching UX, migration/default profile and interaction with external services.

## Smart Collections — NEEDS DECISION
Concept is retained but not approved for implementation. Before activation define rules versus manual collections, editable criteria, membership refresh, interaction with existing Collections and Home rows, empty/error states and performance on large libraries.

## Discovery / custom pages — PARKED
Future custom-page/Discovery concepts remain outside current scope. They must not silently turn Home Featured or More Like This into an advertising/recommendation feed. Define data sources, local-vs-streaming boundaries, provider-country behaviour, page customisation and navigation before implementation.

## Library Health / broader library audit — PARKED
Broader Unscraped Media / Library Health work is parked beyond the first-class Unmatched workflow already implemented. Future design should distinguish actionable problems (unmatched metadata, missing artwork, inaccessible file/source, duplicate/version issues, stale provider data) and avoid destructive “repair” without explicit review.

## Broader audits — PARKED
Movies/TV controls, List View columns, sorting, Settings, metadata, providers, artwork, Trakt, OpenSubtitles, TMDB and inherited library architecture may receive broader audits later. Current fixes do not authorise redesign outside identified defects.

## Network/provider futures — PARKED
Do not add NFS unless genuine implementation support exists. Put.io native API/OAuth work is separate from generic WebDAV playback; production OAuth configuration remains an external dependency. WebDAV replacement is not approved. Do not infer speculative transfer/sync capabilities.

## Person/cast discovery — PARKED
Person pages/cast discovery are future work. Current Cast/Crew rows do not authorise person-profile navigation.

## Design preservation rule
When any parked item is activated, create/update its design authority with final behaviour, geometry and mock-ups before implementation. A feature name is not an implementation specification.

## Current strict non-goals
The current fixes-only release must not introduce identity migration, Smart Collections, Profiles, Discovery/custom pages, broad audits, put.io transfer/sync expansion, WebDAV replacement, NFS, speculative capabilities, branding redesign or dependency-pin movement.


## User-reviewed discussion backlog — 8 October 2026
**Authority:** user decisions below supersede older generic parked descriptions in this file where they conflict. This is a decision/discussion register, **not authorisation to implement**. No application code changes or release scope expansion. The Codex app-wide functional audit is ongoing; await its findings before revisiting Settings/Network & Files and legacy cleanup.

### Parked — definitely wanted, not soon
- **Smart Home Rows** — future discussion/implementation, not soon. Distinguish advanced intelligence/automatic row logic from **existing custom Home rows** (user can already name a row and filter by genres/media type). Do not rebuild existing row functionality as if absent.
- **Multiple versions of media** — discuss library grouping, file choice, playback resume, matching, versions and presentation. No settled design.
- **End-of-playback / Next Episode** — investigate whether a pill/next-episode prompt already exists in current Supernova; user recalls seeing one but is unsure. Audit first, then discuss presentation, timing, focus and interaction with skipping/outro.
- **Intro/recap/outro skipping controls** — discuss expanding current on/off segment-skipping integration (IntroDB and existing skip-marker sources) into user-selectable rules: always skip intro, recap, outro or combinations, with safe fallback and per-type availability. Exact choices, precedence and service support not yet agreed.
- **Backup and Restore** — future discussion on scope, integrity, settings, library/watch-state and safe restore.
- **put.io** — finish existing integration and discuss account, playback, transfer/download and sync enhancements. Respect unresolved production OAuth configuration; no credentials invented.
- **AI-assisted features** — exploratory future discussion only; no feature commitment.
- **Performance/architecture modernisation** — parked, including possible Kotlin/Compose and other modernisation; not part of fixes-only or audit.
- **Further metadata-provider expansion** — parked, subject to quality, availability, licensing and realistic API access.
- **Anime-specialist metadata provider** — investigate *whether* TMDb leaves meaningful gaps and whether a specialist provider has viable terms, identifiers and coverage. No provider selected; user does not personally watch anime.
- **Cloud storage beyond put.io** — parked for possible future discussion; put.io is the only currently intended cloud integration.

### Combined concept — custom library page + future Discovery
The **plus sign after Home / Movies / TV Shows** opens a custom-page landing screen with two conceptual paths:
1. **Apply filters / create custom library page** — the functional path to develop and refine. User chooses a page name and dynamic rules (e.g. documentary, anime, multiple genres, movie and/or TV type). A saved page appears in top navigation, e.g. **Home · Movies · TV Shows · Documentaries**. Its content and layout behave like the existing Movies/TV library views but include only matching *local library* media. New indexed matching items automatically appear. Allow subsequent editing/renaming and reuse existing grid/list, filter, sort and navigation behaviour where applicable.
2. **Discovery** — visible on landing screen as **Coming soon**, not active yet. Later discussion: discover content from *user-selected streaming providers*, separate from local library browsing, with country/provider constraints. Do not implement speculative provider data or rental/buy content.

**Open design questions (not yet approved):** option to exclude media matching the custom page from the main Movies and/or TV pages; whether exclusion is independent per media type; multi-genre AND/OR semantics; multiple custom pages; mixed movie/TV layout; handling metadata changes and empty states. Do not silently remove media from primary library pages. This combined direction supersedes treating Smart Collections and Discovery as two unrelated pages.

### Search — next discussion and requested enhancements
Keep Search **local-library focused**, not a general online discovery search. On the initial screen show the keyboard at left and a tasteful, non-intrusive **instructional panel at right**, with relevant icons, before results replace that area. Explain useful searches including TMDb/IMDb **identifier** lookup where supported, and forgiving titles (e.g. `OC` → `The O.C.`, `Terminator 2` → `The Terminator 2`). Identifier lookup is a desired capability to verify in audit, not a claim that it currently works. Preserve approved keyboard focus/navigation.
Discuss searching library catalogue **cast and crew**, including partial names/surnames, across all credited roles (actor, writer, producer, director, etc.), with each result showing the **matching role/credit** (e.g. Steven Spielberg — story credit on *The Goonies*, director on *Saving Private Ryan* where present in the local library). Also consider studio/production-company search. Only search available indexed/local metadata; check provider coverage and data storage before promising complete credits. Year is already filterable and not requested as a free-text search field. Genre search is undecided/optional, not approved.
**No cast/crew person-detail pages** are wanted at present; search by people replaces the earlier person-page proposal.

### Existing features supersede separate proposals
- **Playlists:** no standalone playlist feature is currently wanted; existing user-created and filtered Home rows substantially cover this need. Park the standalone idea, not the existing rows.
- **Companion app / phone remote:** no current plans. Park without implementation.
- **Cast/crew person-profile pages:** not wanted currently; retain local-library cast/crew search concept instead.

### Other discussion topics
- **Playback enhancements:** user requests specific proposals for worthwhile improvements, but none newly approved by this entry. Assess trick-play preview, resume/Up Next reliability, seek/HUD responsiveness, audio/subtitle handling and stability against existing code and approved HUD design before recommending changes. Do not automatically revive discarded HUD controls.
- **Advanced subtitles:** possible future provider/selection refinements only; no new provider chosen. Preserve approved existing subtitle design and current integrations.
- **Legacy NOVA cleanup:** **strictly after** the ongoing full app-wide functional audit. Maintain candidate/dependency register now, remove no code, and require separate later approval plus regression planning.

### Explicitly parked or excluded provider/data proposals — preserve decision history
- **Awards/nominations and Wikidata** — parked, no current Reception or provider work.
- **New Trakt ratings/reviews** — parked due to app-registration/VIP access constraints; inherited Trakt account/scrobbling and previously approved uses remain.
- **IMDb rating/vote counts** — excluded; IMDb IDs may still support matching/search.
- **Other previously rejected direct reception/provider options** (Rotten Tomatoes, Metacritic, Letterboxd, Watchmode, Plex, direct JustWatch data as ratings, FlixPatrol, Blu-ray.com, YouTube metadata, uNoGS, OMDb workaround) remain excluded absent explicit reopening. Parked exploratory sources include MyAnimeList, RogerEbert.com, A Good Movie to Watch and BestSimilar.
- **Library Health, Downloads, metadata/artwork fallbacks, Home Featured readiness and Details information layout** already have approved design directions elsewhere; do not present these as unresolved new proposals.
- **Profiles** remain parked; no implementation.


## User correction and playback/backup investigation — 8 October 2026 (latest authority)
**Smart Home Rows are ALREADY IMPLEMENTED**, not a new future feature. Existing user-observed editor supports custom row name, Dynamic Genre Rule toggle, genres/other filters, movie/TV inclusion, Show/Hidden, Sort (Date Added / Title / Year) and Maximum Items. Earlier references to building Smart Home Rows anew are superseded. The requested **existing-feature corrective investigation**:
- Add independent **Ascending/Descending** sort direction for each sort choice, e.g. Year newest/oldest, Title A–Z/Z–A, Date Added newest/oldest (final defaults/wording to confirm).
- User observed that Show/Hidden alone does not display a custom row when Dynamic Genre Rule is Off; switching it On *and* Show enabled makes it appear. Trace row eligibility, empty/manual rule state and whether the dynamic toggle wrongly gates visibility. **Show/Hide and content-rule mode should be independent concepts**; do not simply force visibility of an empty row without designing empty state. Verify root cause before any fix.
- Keep existing Home row implementation and filters; do not create a duplicate smart-row feature. Include in audit/corrective candidate register; implementation requires separate release authority.

**IntroDB / segment skip / Next Episode:** re-investigate actual existing integration and supported marker payloads/types; identify which source supplies intro, recap, credits/outro and which types are absent. Existing Integrations→IntroDB currently exposes user-observed single On/Off. Future discussion: independent preferences per supported segment (intro, recap, outro/credits) with **Auto skip / Show skip pill / Do not offer** modes as a candidate, not yet final. User suggests a small skip pill for non-auto-skipped segments, visible ~5 seconds with subtle progress/fill and fade on timeout; auto-skip segments should not show redundant pill. One user can auto-skip intro/credits yet choose optional pill for recap/outro. Clarify marker start/end accuracy, rewind, playback near end, end-of-episode Next Episode handoff and no interruption of deliberate playback. Existing Next Episode pill uncertain; audit code first. Keep IntroDB and other skip-marker source responsibilities distinct.

**Seek preview thumbnails:** desired if feasible without impairing Shield playback. Prior concern about generating every frame is not a requirement: investigate bounded-interval sampling, cached thumbnails, existing embedded trick-play/sprite tracks, extraction on demand, asynchronous low-priority generation, disk/memory budgets and unsupported-codec fallback. No frame-by-frame full-video image generation, main playback decoding disruption or speculative implementation; benchmark options before approval.

**Resume state in backup/restore:** backup should optionally include Continue Watching progress, linked to stable matched catalogue IDs (TMDb/IMDb where available) **plus local item/episode/version identity**, position, duration, completion status and timestamps as needed to avoid ambiguity. A lone IMDb tag + timestamp is insufficient for multiple episodes/editions; restore must reconcile against library, avoid overwriting newer progress, and preserve privacy. Investigate current watch-state storage and Trakt interactions; design export/import later. No code changes.

**Audio/subtitle preferences:** future discussion of language selection precedence, per-title/per-series override vs global defaults, forced/SDH flags, external subtitle fallback, remembered track identities and audio-sync/boost persistence. Not approved changes.

**Anime metadata:** inspect inherited NOVA anime-specific UI/library classification and the actual metadata lookup/provider path before proposing AniList or another service. Prefer existing TMDb or other already-integrated source if sufficient; no additional anime provider unless demonstrated coverage gap.

## Clarification — Dynamic Genre Rule and segment options (8 October 2026)
User's proposed interpretation (NOT YET CONFIRMED IN CODE): Dynamic Genre Rule On = saved filters continuously include future matching imports; Off = freeze/snapshot current row membership while retaining visible row. This requires persistent static membership and is not necessarily what current toggle does. Audit the implementation before adopting this behaviour. Show/Hide must remain conceptually independent; do not claim the toggle is a bug until its actual contract is established.

Proposed IntroDB UI model (NOT YET APPROVED): master Enable IntroDB; per *supported* segment type Intro, Recap, Outro/Credits choose one of Auto-skip, Show five-second skip pill, or Play normally. This avoids contradictory separate On/Off plus mode controls. Confirm actual source marker capabilities and source precedence before implementation. Playback seek-thumbnail feasibility: evaluate lazy/on-demand thumbnails, native embedded preview tracks and small bounded caches first; 720 images for a two-hour film is only a sample interval example, **not** a requirement or an assertion that generation is cheap. Avoid full-library pre-generation and playback disruption.

## User-approved direction and external segment API research — 8 October 2026
**Home rows:** User approves dynamic-to-static snapshot semantics: Dynamic Genre Rule ON adds existing/future matching indexed media; OFF freezes current membership without hiding row, and manual Add to Row continues to work. Re-enabling should reconcile matching media while preserving manual additions. Show/Hide remains independent. Add Ascending/Descending control for Date Added, Title and Year. **Investigate existing persistence and UI before implementation; not authorised in current fixes-only release.**
**Segment controls:** User likes one master integration switch plus **three modes per segment** (Auto skip / five-second skip pill / Play normally), pending code/provider validation. Distinguish **outro** from **credits** rather than treating them as identical; support only types actually provided or inferable safely. Do not conflate skip-outro with Next Episode. Source research:
- `https://introdb.app/docs/api` documents `GET /segments` (TV by IMDb ID/season/episode, movies by IMDb ID), segment submissions `intro`, `recap`, `outro`, `post-credits`, and legacy intro-only `/intro`. Read access no API key, rate-limited. Post-credits are **scenes**, not equivalent to credits skip.
- Separately, `https://theintrodb.org` and `https://theintrodb.github.io/theintrodb-npm/types.html` describe `intro`, `recap`, `credits`, `preview` types, and `/media` using TMDb preferred/IMDb fallback. **These are different domains/API schemas; do not assume interchangeable or the same backend.** Trace which endpoint Supernova actually uses before final design, and assess marker availability/version matching.
**Seek thumbnails:** explicitly PARKED for later discussion; no implementation now.

### Up Next and segment-source audit follow-up (8 October 2026)
- App-wide source audit confirms Up Next uses a 15-second countdown. User considers this too long; **5 seconds is proposed for review**, not yet final implementation authority. Preserve Play Now/Cancel, and coordinate the transition with IntroDB credits/outro skipping and post-credit safety.
- Source audit confirms `IntroDbManager` merges segment data from **theintrodb.org and introdb.app**. This is source integration, not a guarantee that both services provide markers for every title. Retain distinct outro/credits modes and investigate source-specific marker coverage before implementation.
