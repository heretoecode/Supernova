# Supernova — consolidated next-version Codex handover
**Prepared:** 2026-10-30. **Status:** Owner requests Codex to work autonomously through all agreed next-version work, while respecting unresolved approval gates. **Repository:** heretoecode/Supernova. **Documentation branch:** `docs/network-files-settings-design-next-version-2026-10-29`. **No merge into main or modification of current release/draft PR branches without explicit permission.**

## Read-first authoritative sources
1. `docs/design/PLAYBACK_HUD_LOCKED_BLUEPRINT_2026-10-30.md` — latest owner-approved HUD composition; supersedes older four-pill visuals.
2. `docs/design/NEXT_VERSION_IN_PLAYER_SETTINGS_AND_PLAY_PAUSE.md` — compact Concept A4 in-player Settings and owner-approved inline expansion **across Audio/Subtitles/Video/More**, source parity.
3. `docs/design/NEXT_VERSION_PLAYBACK_PREPARATION_AND_HUD_REFINEMENTS.md` — physical Shield issues, artwork preparation, tooltip/safe area.
4. `docs/design/EDIT_LIBRARY_STRICT_UI_BLUEPRINT_DRAFT_2026-10-30.md` — four-section Edit Library direction, **not yet locked exact geometry**, outstanding owner choices.
5. `docs/design/EDIT_LIBRARY_SOURCE_AUDIT_AND_REDESIGN_PROPOSAL_2026-10-30.md` on `docs/edit-library-audit-and-proposal-2026-10-30`, draft PR #28 — source-backed findings and acceptance matrix.
6. Repo's current `GLOBAL_VISUAL_SYSTEM.md`, actual media/player code, upstream maintenance mapping, conformance/QA logs, existing next-version issue register and latest release notes. Locate exact paths in repo, do not assume missing docs.
7. Historical `PLAYBACK_HUD_0.135_APPROVED_2026-10-10.md` and `PLAYBACK.md` only for legacy behaviour, **not** their superseded visual layout.

## Baseline / branch protection
- Establish actual latest branch/commit, working tree, draft PRs, release source and test status **read-only first**; don't assume historic version labels are latest. Last discussed signed QA was 0.147 from `f4abf7cb880243ba00481f82beee19d20bc72e33` and PR #27, but verify before implementation.
- Preserve main, release branches, 0.147 PR #27, Edit Library audit PR #28, uncommitted work, signing credentials and Drive deliverables. Do not reset/clean/rebase/force-push/merge them. Create a dedicated new implementation branch **only after** checking active work and approvals; don't overwrite someone else's branch.
- Never delete user media, existing backup archives or app data. Build/sign/upload only if authorised and secure signing path exists. Do not claim an APK delivered without verifying.
- Do not implement an unapproved detail just to satisfy the request to 'fully work away'. Instead complete all independent investigation/tests/docs, propose precise alternatives and stop only at genuine owner-choice/permission blockers.

## Workstream A — Playback HUD (visual composition APPROVED)
- Official title logo/typography middle-left and further aligned to **left safe edge**, supported metadata below. Existing logo preferred to generic text; preserve Supernova font and artwork.
- Raised single horizontal row: **focusable Play/Pause → seek bar → total duration → Ends at local wall-clock time → one in-player Settings pill**. Correct safe area; no second control row.
- Current seek timestamp bubble **permanently visible whenever seek bar visible**, follows playhead with notch; real chapter name/number only if known. No duplicated current time beneath bar; no invented thumbnail.
- Initial focus on seek bar. Left/Right seek, physical media keys always work. Play/Pause and Settings separately focusable with distinct focus/playing/selected states. **Exact non-conflicting D-pad traversal is unresolved**: audit actual remote input architecture, write focus graph with alternatives and seek owner sign-off rather than inventing. Measure actual layout tokens in source and provide responsive 720p/1080p/4K acceptance screenshots; owner sign-off where blueprint explicitly requires it.
- Handle pause/seek/speed/unknown-duration/end-time accurately, safe subtitle overlap and performance.

## Workstream B — Compact in-player Settings (interaction APPROVED)
- Retain **Audio / Subtitles / Video / More**, compact dark Space Black Concept A4 panel and approved position/size.
- All four tabs use **Option 2 inline expansion**: multi-choice rows reveal **actual existing choices directly underneath**, select then collapse; binary controls use direct toggles; sliders/adjustments inline. D-pad focus and Back consistent; no pointless secondary pages.
- **Strict existing feature parity.** Inventory each tab's real source controls, availability conditions and exact value ranges before changing UI. AI mock-ups invented examples such as Output, Boost, Downmix, Passthrough, Zoom etc.; **none** are authorisation to add settings. Keep any existing supported capability, no invented options. Preserve live playback, subtitle/audio selection and sync.

## Workstream C — ChaptersDB (integration APPROVED for next version)
- Owner approved full external chapter integration for movies and individual TV episodes; **validate ChaptersDB's current API, terms/licence, uptime, usage policy and actual data coverage** against official documentation before coding. Previous candidate `https://chaptersdb.com/api` is **not** proof of viable production integration. Document endpoints and tests with evidence; if unavailable/incompatible, raise specific blocker rather than fabricate.
- Match stable movie/series IDs, season and episode, correct edition/cut, runtime and offsets. **Embedded valid chapters take precedence**. Retrieve, parse, normalise, validate, deduplicate and cache external chapters; robust offline, timeout, rate limit, malformed/no-match fallback. Do not send local media paths, credentials or personal information.
- Render valid chapter ticks and actual chapter label in HUD bubble; chapter navigation must not break seeking. Keep existing IntroDB intro/recap/preview/outro/credits skipping **separate**, with no accidental double skip or loss of existing options.
- Automated tests: matching/non-matching editions, TV episodes, wrong duration, zero chapters, invalid offsets, unavailable service, caching, offline playback and Shield HUD display.

## Workstream D — Edit Library (STRUCTURE approved; **specific approval gates remain**)
- Four sections: **1 Page Details & Content Type; 2 Filters; 3 Display Options; 4 Review**. Existing global navigation remains part of app but absent from mock-up crop; no invented global search/Home toggles, description or page icon. No footer Cancel/Previous/Next. Review contains Save; remote Back handles exit and dirty-change confirmation.
- Preserve **exact current filters and persistence semantics**: genres multi-select Any/All, single-year/All, watched status, minimum rating, original language, studio/network, conditional options, existing sort/view/direction and Delete Page semantics. **No** new year ranges, exclude facets, subfolder/special-feature toggles or mock-up-only fields.
- Live Preview shows **actual matching** media posters and source-backed stats, same draft filters/content type/sort, with loading/empty/error, stable focus frame and inner scrolling; no fabricated counts or artwork.
- Fix source-confirmed stale focus, discarded exit destination, background layers, outline bound to scroll child, vertical buttons and relevant UI regressions; investigate stats hidden until scroll and incorrect library-tab highlight. Do not claim unverified physical fixes.
- **Approval gates in draft blueprint still binding:** owner has not signed off exact token measurements, final Pages 3/4 layout, Review Save placement, Delete Page location, 'Both' naming or precise statistics fields. Prepare measured designs, focus graph and clear owner decision packet; implement unblocked approved infrastructure/fixes separately only if safe. Do **not** silently declare draft blueprint locked or move/delete existing functions.

## Workstream E — remaining next-version issues (audit and triage, no invented closure)
Locate actual issue register and source evidence for: custom library statistics hidden until scroll, wrong tab highlight after switching libraries, static Space Black library background, missing age rating in details/playback, keyboard caret alignment and Caps Lock indicator, details hero tint, global Settings focus ghost, toggle ON/OFF clarity, Connections & Integrations grouping, subtitle language flags, slow details loading, reception ratings, Network & Files focus/filter/browser/navigation regressions, Home row clipping/zoom and move/hide controls, and preparation artwork flashes. Determine which are already fixed in baseline and which are still open. Do not redo already delivered 0.147 features or accidentally revert them. Respect any explicitly parked items; list evidence and owners before scheduling changes.

## Mandatory execution sequence
1. Read-only repo and PR baseline audit; reconcile requirements against latest code, tests and docs. Create traceable **requirement-to-source-to-test matrix** with approved / pending / parked / already shipped statuses.
2. Source audit all four Playback Settings tabs and Edit Library controls. Research ChaptersDB production suitability with live evidence; map upstream NOVA chapter capability to avoid duplicating existing functions.
3. Produce measured HUD/Edit Library design tokens and exact D-pad focus diagrams. **Request only genuinely necessary owner approvals** for unresolved UI choices; continue all other safe work in parallel.
4. Plan small isolated commits and regression tests on a **new, dedicated implementation branch** after checking branch/permissions. Keep workstream separation and rollback ability. Never alter main or active draft PR branches.
5. Implement all fully approved, technically viable features/fixes; where a gate is unresolved, document it and avoid guessing. Preserve existing behaviour, settings, data, branding and integrations.
6. Run static/build/unit/instrumented tests available, source diff review and regression checks. Report what is actually verified vs requires **physical Nvidia Shield** QA. Do not assert Shield validation without a real device result.
7. Update GitHub requirements, blueprints, test matrix, change log and release notes with actual implementation status. Prepare signed APK/Drive delivery **only** when authorised and signing access verified; never uninstall the owner's existing installation or assume backups work.
8. Provide a concise final report: exact branch/commit, changed items, tests, blockers, pending owner decisions, QA APK status and links. Keep any unimplemented work explicitly open.

## Approved visual assets
Generated mock-ups were discussed in ChatGPT but **not verified uploaded to GitHub**. Do not pretend they are in the repository or use AI imagery as source truth. Use textual specifications and existing app assets; if an approved image is needed for reference, request upload to the repo's established approved-reference folder and verify exact file path before referencing.

## Owner communication
British English, short plain-language updates. Ask the owner only about genuine personal design choices or security-sensitive/destructive actions; solve engineering details autonomously. No speculative 'done' claims.
