# Supernova — Future Development

**Planning register only.** An approved design is NOT permission to code, schedule, merge, build or release. The owner explicitly chooses when an item enters a release. Protect active 0.139 work and separate Stage D maintenance.

## Statuses
- **Under Discussion** — exploring, not approved.
- **Parked Ideas** — paused, preserve all discussion and references.
- **Approved - Awaiting Scheduling** — owner has signed off behaviour/design; no release or coding authorisation.
- **Scheduled / In Development** — owner explicitly selected a release; distinguish scheduled vs actual implementation.
- **Completed / Verified** — distinguish implemented, built, owner-tested and released; don't mark complete on developer claim alone.
- **Rejected / Withdrawn** — retain rationale, history and mock-ups, no active work.

## Index
| ID | Feature | Status | Record |
|---|---|---|---|
| FD-001 | Playback age classification overlay | **Approved - Awaiting Scheduling** | [Approved](03%20Approved%20-%20Awaiting%20Scheduling/FD-001-age-rating-overlay.md) |
| FD-002 | Playback HUD redesign | **Under Discussion — needs further discussion** | [Discussion record](01%20Under%20Discussion/FD-002-playback-hud-redesign.md) |
| FD-003 | Preparing Playback screen redesign | Parked | [Parked](02%20Parked%20Ideas/README.md) |
| FD-004 | Shield Home integration | Parked | [Parked](02%20Parked%20Ideas/README.md) |
| FD-005 | Network & Files redesign | Parked | [Parked](02%20Parked%20Ideas/README.md) |
| FD-006 | Remaining Settings/context menus | Parked | [Parked](02%20Parked%20Ideas/README.md) |
| FD-007 | Library statistics/header | Parked / unresolved design | [Parked](02%20Parked%20Ideas/README.md) |
| FD-008 | Put.io native integration activation | **Provisional 0.140 target; not authorised** | [Scheduled register](04%20Scheduled%20and%20In%20Development/README.md) |
| FD-009 | Additional Home row icons | Closed / no further action | [Rejected & withdrawn](06%20Rejected%20and%20Withdrawn/README.md) |
| FD-010 | Hero More Info -> Resume proposal | Rejected | [Rejected & withdrawn](06%20Rejected%20and%20Withdrawn/README.md) |
| FD-011 | Profiles, Smart Collections, Discovery, library health and related historical futures | Parked or needs decision; see canonical historical register | [Parked](02%20Parked%20Ideas/README.md) |

The full historical inventories remain at [parked future design](../design/PARKED_FUTURE.md) and [post-0.139 backlog](../project-decisions/POST_0139_NON_MAINTENANCE_FEATURE_BACKLOG.md). This new register does not erase or silently supersede their evidence. Each new decision should update this index and the relevant permanent feature record; keep mock-ups in GitHub when available, otherwise link existing approved assets and clearly note missing attachments. Never claim all prior chat imagery is archived unless verified.

## Historical cross-check — 8–13 October 2026

The following **additional historical discussion-only candidates** were found in the 10 October discussion backlog and the legacy parked design file. They are retained here pending individual records and any later owner decisions; **none is scheduled or implementation-approved by this migration**:

| ID | Subject | Status / caution |
|---|---|---|
| FD-012 | Smart Collections | **Under discussion / needs decision**, not approved |
| FD-013 | Dedicated Discovery / Coming Soon | Parked |
| FD-014 | One custom local Library Page | **Historically approved design**, check current release handover to avoid duplicate scheduling |
| FD-015 | Library Health / broader unmatched-media workflows | Parked |
| FD-016 | Cast and crew person profiles | Parked |
| FD-017 | Multiple custom Library Pages | Historical discussion-only; distinct from FD-014 |
| FD-018 | Anime section | Historical discussion-only |
| FD-019 | Seek-bar thumbnail previews | Historical discussion-only |
| FD-020 | Advanced subtitle presets | Historical discussion-only |
| FD-021 | Expanded playback intelligence / version choice | Historical discussion-only |
| FD-022 | Expanded IMDb / Rotten Tomatoes ratings | Historical discussion-only, separate from age classification |
| FD-023 | Trakt community reviews | Historical discussion-only |
| FD-024 | AI-assisted matching, search or recommendations | Historical discussion-only |
| FD-025 | Additional layout/theme/navigation personalisation | Historical discussion-only |
| FD-026 | Other service integrations | Historical discussion-only |
| FD-027 | Generic WebDAV downloads / transfer idea | Parked; not equivalent to existing playback |
| FD-028 | Detailed content warnings | Parked, removed from approved age-only v1 |
| FD-029 | Broader Movies/TV/metadata UI audits | Parked; distinguish from Stage D maintenance |

**Separate governance tracks, not to be duplicated as new feature requests:** the complete active 0.139 scope (including hero, Backup & Restore, Customise Home, Details and 43 workstreams); signed 0.138 Shield QA; Stage D upstream/dependency/Jetty/FFmpeg/security/performance/shared-UI/APK size and Kotlin/Compose investigation; existing completed branding and signing. Their source of truth remains the [roadmap](../project-decisions/CURRENT_ROADMAP_AND_DECISIONS.md) and [0.139 handover](../project-decisions/2026-10-13-supernova-0139-codex-implementation-handover.md).

**Evidence audit:** The register was cross-checked against [the 10 October future discussion backlog](../project/SUPERNOVA_FUTURE_DISCUSSION_BACKLOG_2026-10-10.md), [historical parked design](../design/PARKED_FUTURE.md), [post-0.139 backlog](../project-decisions/POST_0139_NON_MAINTENANCE_FEATURE_BACKLOG.md), and [current roadmap](../project-decisions/CURRENT_ROADMAP_AND_DECISIONS.md). Those documents contain chronological, sometimes superseded decisions; the latest explicit owner approval controls. Not every prior chat, external Claude design or video/mock-up has a verified GitHub asset. **Do not claim a complete image/conversation archive.**

**Follow-up reconciliation required:** the historically approved custom Library Page and About Settings designs need comparison with active 0.139 scope/current shipped behaviour before being categorised as unscheduled; branding was reported completed. Put.io is only *provisionally* proposed for 0.140. The age overlay is approved but has **no assigned release**.

## Repository-wide reconciliation update
See [13 October documentation audit and status corrections](REPOSITORY_WIDE_RECONCILIATION_2026-10-13.md). Important corrections:
- **FD-014** single custom Library Page: explicitly approved **for historical 0.135 scope**, **not** a fresh unscheduled item; check installed/active code before treating it as incomplete.
- **FD-031** About five subsections: **approved design**, not awaiting a fresh design discussion; implementation/release verification outstanding.
- **FD-030** seek-bar/end-time refinement: **parked**, with detailed preferred layout retained; separate from broader FD-002 HUD redesign and **previously approved 0.135 four-button HUD**.
- **FD-032** Google Drive release publishing: parked **operational** workflow.
- Branding/icon/banner/signing: owner-reported implemented, not new work.
- 0.139 authorised scope and Stage D maintenance stay in their own authoritative release tracks, not duplicated as new ideas.

The reconciliation inventory counted **170 documentation Markdown/text files**, with targeted reading of central planning/decision documents; it does not certify line-by-line review of every file or every conversation. Do not claim otherwise.

## Owner-facing decision readiness (13 October 2026)
For future feature discussions, report readiness based on **outstanding owner decisions**, not routine engineering work. Use: **Ready for implementation** (all owner decisions final; scheduling authorisation still required), **Needs your decision** (specific unresolved owner choice), or **Still under discussion** (design/behaviour not final). Record metadata audits, feasibility checks, coding prerequisites and QA in technical handovers for Codex; do not present these as questions for the owner unless they reveal a genuine product choice or blocker. FD-001 age-rating overlay is **ready for implementation from the owner's perspective**, but remains **approved — awaiting scheduling**; do not start it without explicit release authorisation.

## Owner-confirmed existing features — 13 October 2026
**FD-031 Settings > About** is already implemented and working (including centred branding, Release Notes and Open-source Licences; the prior licences crash is fixed). **FD-014 custom Library page and five-step Edit Library workflow** are already implemented and working. Neither is an unscheduled future feature; future owner-requested visual or usability tweaks are normal ongoing maintenance. Historical approved designs remain for reference. Owner confirmation establishes functional status; do not invent build-specific QA evidence.

## 13 October owner decision reconciliation — readiness and closed items
- **FD-012 Smart Collections:** **CLOSED — superseded by existing Supernova Library pages and Home rows**; no further discussion requested. Historical file remains as record only, despite its original Under Discussion directory location.
- **FD-032 Google Drive APK delivery:** current Codex-to-Drive release delivery **implemented and owner-confirmed working** across recent versions. An optional fully unattended OAuth/Actions publisher is a separate historical proposal, not required future work.
- **FD-014 and FD-031:** owner-confirmed implemented and working; cosmetic refinements are routine usage feedback, not future feature approvals.
- **FD-001:** only currently confirmed new feature with complete owner design approval, **ready for implementation when assigned a release**.
- **FD-008 Put.io:** owner-side registration/planning complete; **provisional 0.140** only. Remaining source/linking validation and QA belong to Codex; not an owner design discussion unless a genuine choice arises.
- **FD-028 detailed advisories:** parked; distinct from age-only FD-001. TMDb movie release-date schema includes a `descriptors` array but populated real-title/region/TV coverage remains unverified; investigate as engineering research, not a question for the owner.

## Owner approval — OMDb-enhanced Reception area (13 October 2026)
**Status: APPROVED — AWAITING RELEASE SCHEDULING; NO CODE AUTHORISED.** Enhance the **existing Reception area of the existing Details tab** (do not introduce a new tab or replace the existing page design) with supplementary IMDb audience score and vote count, Rotten Tomatoes score, Metacritic Metascore, and awards/recognition including wins/nominations and major awards when available. Preserve existing TMDb data and established visual language; hide unavailable values cleanly. OMDb is a candidate supplementary source, not a replacement for current providers; its `Rated` field must not override the user-region age certificate. Codex to verify current metadata/ID matching, cache, limits, score freshness, missing values, and OMDb API terms/CC BY-NC licensing before enabling distribution. Owner has approved product scope, not assumed legal permission or implementation. Do not alter active 0.139 work.

## Approved Details tab visual specification — 13 October 2026
- **FD-033: Approved — awaiting scheduling.** [Owner-approved Details tab specification](03%20Approved%20-%20Awaiting%20Scheduling/FD-033-details-tab-approved-design.md). Covers final 16:9 layout below hero, retained official typography, single continuous user-accent focus outline integrating active tab with panel, unboxed inactive tabs, non-interactive/no-scroll Details information, Description/Key Information/Reception hierarchy, 8 cast + 8 crew, technical single-line path and OMDb Reception scope. Owner-approved final illustrative mock-up remains in conversation; GitHub contains the text implementation specification, **not the image asset**. No changes to active 0.139.

## 13 October — Playback HUD discussion reopened
FD-002 is now **Under Discussion**, not approved or scheduled. [Full owner discussion and source-verification requirements](01%20Under%20Discussion/FD-002-playback-hud-redesign.md). Previous parked status in older chronological documents is superseded for this item only. The unfinished 0.139 Codex implementation remains protected.

## Owner scheduling decision — 13 October 2026 (latest)
The owner explicitly **approves four features for the next release after 0.139, expected version 0.140**: FD-001 playback age-classification overlay; FD-033 approved Details-tab redesign; OMDb supplementary IMDb, Rotten Tomatoes and Metacritic ratings (and previously approved awards/recognition) within Details Reception using the free API allowance; and FD-008 Put.io native integration activation. This supersedes earlier 'awaiting scheduling' and 'provisional 0.140' labels for these four. **Do not interrupt, edit or expand the ongoing 0.139 branch.** Codex owns implementation feasibility, API terms/licensing, caching/quotas, source checks and QA. Free allowance choice is not evidence of redistribution/licensing permission. No signed 0.140 build exists yet.

Owner also confirms existing unattended Google Drive release publishing works: do not list as a pending future feature. Broader Movies/TV/metadata audits are considered already completed by the owner; remove them from active discussion backlog, retaining historical records only. Preparing Playback screen remains parked unless reopened for visual design. Detailed content advisories/warnings were the originally explored age-overlay extension; they are **not** included in approved age-only v1 and remain parked.

## Owner correction — Preparing Playback (13 October 2026)
FD-003 Preparing Playback screen redesign is **already implemented in the app, owner-confirmed**. Remove it from active future discussion/parked implementation. Future refinements only if owner reports a desired change. Earlier parked statements are superseded. Content advisories remain a distinct unapproved extension: historical investigation found a possible TMDb movie `descriptors` field but did not verify real populated responses or existing Supernova mappings; final v1 owner approval explicitly excluded warnings. Do not silently expand the approved 0.140 age-only overlay.

## 14 October — Playback HUD / Settings latest decision checkpoint (authoritative)
- **FD-002 Under Discussion; visual foundation approved, implementation NOT scheduled.** See [latest chronological HUD and Settings decision ledger](01%20Under%20Discussion/FD-002-playback-hud-redesign.md). Standard playback HUD: wide floating seek/timing row over subtle bottom gradient; persistent existing HH:MM:SS timestamp bubble whenever HUD visible; inline remaining time and generic `Ends at HH:MM`; no transport buttons; Settings icon + label in subtle rounded unfocused box above/right. Seek bar gets focus when HUD opens; Left/Right immediately seek even if HUD initially hidden (show HUD on initiating key); long hold accelerates timeline traversal, not playback speed. Optional thumbnails **not implemented or approved**.
- **Playback Settings visual foundation APPROVED for later source-backed refinement:** wide dark translucent cinematic panel, four genuine underlined tabs Audio/Subtitles/Video/More, two-column content, inline track/value controls, avoid nested menus, selected tab distinct from actual D-pad focus. Illustrative mock-up settings **not validated** and must not be assumed available. Latest four-panel reference image is conversation-only, not committed as a GitHub image asset.
- **Search Subtitles Online design explicitly DEFERRED** pending Codex audit of existing OpenSubtitles integration/search, authentication, title/episode matching, results, languages, download/application and focus. Three-column illustrative search mock-up **not approved**. Return for owner design review after source evidence; do not implement or change active 0.139.
- **Supersession:** older statements in this README saying FD-002 parked, Preparing Playback unimplemented, or FD-001 limited to age-only are historical and superseded by latest dated owner decisions. FD-003 Preparing Playback and unattended Drive delivery are owner-confirmed implemented. FD-001 0.140 overlay now includes **conditional, genuinely sourced title/region-matched classification descriptors below the age**, if available; otherwise age-only, never inferred. The four explicitly approved next-release 0.140 scopes remain FD-001, FD-033 Details, OMDb supplementary Reception and FD-008 Put.io; **FD-002 is not one of them**.

## 14 October 2026 — FD-007 Library statistics/header APPROVED design (LATEST)
The previously parked FD-007 has an owner-approved **global Movies/TV Shows/third Library page** two-row, single full-width stats panel design and metric definitions. [Read authoritative FD-007 specification](03%20Approved%20-%20Awaiting%20Scheduling/FD-007-library-statistics-header-approved-design.md). Key placement: remove duplicate page heading; stats width aligns exactly to existing library content; **16px-equivalent at 1080p above and below panel**, measured from nav underline and to filters, responsive on 720p/4K. Four existing-style totals on top; five new/extended statistics below (Unwatched, In Progress, Added This Month, Last Updated, static monochrome Library Status). No aggregated drive capacity/usage bars. Old parked FD-007 notes are superseded. Approved **design**, not 0.139 changes or an automatic addition to 0.140. Image-generator spacing labels are unreliable; written measurements prevail.
