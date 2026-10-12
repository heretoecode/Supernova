# Supernova 0.138 — user-approved consolidated Codex implementation scope

**Approval:** On 2026-10-12 the user explicitly authorised **all** areas in the proposed 0.138 Codex scope for implementation in the next version, rather than deferring individual areas. This is a scope approval, **not** a claim implementation has begun, passed or shipped.

**Repository:** heretoecode/Supernova. **Current testing baseline:** signed 0.137 on NVIDIA Shield; draft PR #12 on codex/supernova-0.137-qa-2026-10-11. **Documentation branch:** docs/nova-upstream-deep-audit-2026-10-11. Do not overwrite/revert existing work, merge to main, alter signing/application identity, or upload private Shield evidence without permission. First audit the exact active branch and reconcile all documented requirements; implement as 0.138 only after establishing safe branch/release transition. Use actual GitHub docs as source of truth; this index is not a substitute for full specifications.

## Required implementation work — all in scope

### 1. Home and Continue Watching
- Remove Home Library Health banner/alert **and reserved space**, retain Library Health elsewhere. Retest Home Continue Watching; investigate if still absent, but respect the previously agreed diagnostic sequence and do not invent banner causality.
- Verify/fix resume-in-progress, completed episode → next unstarted episode, last episode of season → first available next-season episode; no next item beyond available series content. Preserve correct row location and hide/show controls, avoid unintended duplicates.
- Apply **15 displayed-item cap to automatic Home rows** including Continue Watching, Recently Added, Trending on Trakt, Popular on Trakt, Because You Watched. **Watch Next uncapped**; custom rows respect user-selected max.
- Investigate/fix custom Home row combining eligible Movies and TV Shows; do not force 50/50 mix.
- Use approved Trakt headings **“Trending on Trakt · In Your Library”** and **“Popular on Trakt · In Your Library”** (confirm exact latest approved wording before changing copy).

### 2. Hero cards
- Improve hero metadata readability/size and contrast, using global metadata standard. Age classification → year → genres → labelled TMDB rating → muted vote count, middle dots, dynamic compaction; verified separate technical badges only. No season/episode counts or runtime on Home hero.
- Add visual-only whole-card focus outline (white or user accent) when More Info is focused, without changing actual D-pad focus/Select behaviour.
- Audit washed-out/faded Home poster artwork and remove unintended overlays/opacity if confirmed. Audit hero content eligibility to ensure eligible movies are not incorrectly excluded. Preserve smooth hero animation; polish only if justified by actual inspection.

### 3. Global Details Card and lower Details page
- Implement shared global upper **Details Card** across applicable Movies, TV Shows, Seasons, Episodes, visually based on Home hero design, over approved Supernova background, with comfortable margins and lower tabs visible in initial viewport.
- Official title artwork/logo scales **proportionately** with card (maintain native aspect ratio and relative composition). Shared title-artwork positioning and metadata/technical presentation standards, with content-specific fields. Movie runtime and episode runtime where appropriate; TV show Season/Episode counts and season Episode counts, capitalise Season/Seasons/Episode/Episodes and handle singular/plural. No irrelevant fields or misleading technical badges.
- Synopsis and Play/Streaming if applicable/More actions within card. Card has subtle visual-only white/user-accent active-region highlight when its buttons hold focus; **card itself never focusable/clickable**. Button focus remains individually visible.
- Lower Details/Extras/More Like This tabs: **white focus underline mirroring top navigation, NO full-width separator**, visible on first viewport; selected vs focused distinguishable.
- Lower **Details** tab must show **Cast, Crew, Key Information, Reception, Technical Information** together **without vertical scrolling** at readable sizes. Cast <=8, Crew <=8, no See All/Show More/expansion. Compact Key Information/Reception/Technical label-and-value pairs on **same row**, with comfortable (neither huge nor tiny) gap and compact containers without excessive empty space. Handle long strings and TV scaling gracefully; do not silently drop essential content. Nothing inside lower Details content focusable; D-pad focus stays on Details tab, Left/Right navigates tabs.
- Apply global button design to upper Play, Streaming, More **in resting and focused states**, not just when focused; do not defer until later redesign.
- Wider Extras/More Like This redesign beyond above is not specified; preserve functionality unless needed for shared layout.

### 4. Global shared design systems
- Shared reusable button tokens/components, rest/focus/pressed/disabled states and appropriate context sizing. Home hero More Info and Home Customise Home share approved restrained translucent dark styling; migrate appropriate other app buttons; do not indiscriminately restyle unrelated specialised controls.
- Shared metadata/technical formatting and badges across hero and applicable details pages, with screen-specific content and verified data only. Missing fields collapse without gaps; vote counts not orphaned. Validate all consumers, not hero only.
- Shared Library-page design contract across Movies, TV Shows and custom third Library page; audit/fix actual divergences while preserving content/configuration differences.

### 5. Subtitle intelligence — audit then safe implementation within 0.138
- Audit actual provider(s), API terms/limits, request cache, availability vs download semantics, preferred language and local embedded/sidecar discovery before implementation. Do not invent provider quotas or issue bulk requests.
- Implement quota-aware, persistent, deduplicated **intent-triggered** checks: hero eligibility (low), Watch Next and Continue Watching (high), movie/show/episode detail visits (context-specific), playback (highest). At import detect local subtitles where feasible; **no automatic provider lookup for entire imported library**.
- On starting playback of an episode, progressively prepare **remaining episodes in that season only** in preferred language, with low-priority queue; no burst, honour 429/Retry-After, cache negative results, throttle and dedup repeated reimports. At season boundary, prioritise next-season first episode if Continue Watching eligible; **do not prepare rest of next season until playback starts there**. Respect manual requests and user settings/consent where relevant.
- Display subtitle/CC/SDH/AD badges only for verified capabilities; never equate unsearched with unavailable. Provider constraints may require adapted implementation, document limitations transparently rather than unsafe fetching.

### 6. QA and release readiness
- Revisit first-run onboarding/welcome Home before final handover, inspect fresh user-approved Shield diagnostics, including previously parked **23:22** event blind-investigation, respecting privacy/consent.
- Review existing QA issues and preservation requirements: Home row navigation/focus, hero visual consistency, shared library design, file/network focus and first-run behaviour. Produce actual screen captures/renders where feasible and test D-pad traversal.
- Automated tests plus physical Shield verification, exact-source build and conformance, signing/identity preservation. Explicit checklist per requirement with PASS/FAIL/UNKNOWN and evidence; do not claim done from compilation alone.

## Source documentation (same branch)
- docs/project/GLOBAL_HERO_METADATA_STANDARD_AND_SUBTITLE_DISCOVERY_DISCUSSION_2026-10-12.md
- docs/project/GLOBAL_DETAILS_CARD_UPPER_PAGE_DESIGN_2026-10-12.md
- docs/project/NEXT_RELEASE_LOWER_DETAILS_COMPACT_NONSCROLLABLE_AND_GLOBAL_BUTTONS_2026-10-12.md
- docs/project/NEXT_RELEASE_CAST_CREW_EIGHT_ITEM_CAP_2026-10-12.md
- docs/project/QA_CONTINUE_WATCHING_NEXT_EPISODE_AND_SEASON_SUBTITLE_PREFETCH_2026-10-12.md
- docs/project/QA_0137_HOME_CONTINUE_WATCHING_LIBRARY_HEALTH_BANNER_2026-10-11.md
- docs/project/QA_0137_HERO_HOME_ROWS_CUSTOM_MIXED_CONTENT_2026-10-11.md
- docs/project/QA_0137_HERO_ANIMATION_METADATA_AND_CONTENT_MIX_DISCUSSION_2026-10-12.md
- docs/project/QA_0137_GLOBAL_BUTTON_DESIGN_STANDARD_2026-10-11.md
- docs/project/GLOBAL_LIBRARY_PAGE_SHARED_DESIGN_RULE_2026-10-12.md
- docs/project/QA_0137_DIAGNOSTICS_1122_PRE_HANDOVER_2026-10-11.md

**Precedence:** User's latest approved decisions supersede provisional wording in earlier exploratory notes. This file is an implementation-scope approval; do not conflate with already completed implementation. User has not approved merge/release or exposure of private test materials.
