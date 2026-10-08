# Supernova — App-wide functional audit: authoritative Codex handover
Date: 8 October 2026
Repository: `heretoecode/Supernova`
Documentation branch: `codex/post-4.1.7-shield-fixes`
Status: **AUDIT REQUIRED — NOT COMPLETE**

## 1. Mission and authority
Conduct a **complete, evidence-based, app-wide functional audit** of the currently implemented Supernova Android TV application, including inherited NOVA functionality. This is **functional discovery and dependency mapping**, not feature implementation, UI redesign, or a code cleanup. The immediate user discussion priority is **Settings** and **Network & Files**; complete both inventories first, then cover the entire application before claiming app-wide completion.

The user's specific question is: *For every option visible on my Shield, what does it do, is it still relevant to modern Supernova, can I safely use it, what hidden/conditional options exist, and should it remain visible?* Build the evidence needed to answer that question precisely, including differences between actual implemented UI and approved future designs.

## 2. Absolute restrictions
- **Read source code and documentation; write audit documentation only.** Do not modify application code, resources, tests, build scripts, workflows, dependencies, signing, package IDs, release metadata, or app configuration.
- Do not delete, disable, hide, move or rename settings or features in code. All such dispositions are **recommendations pending the user's approval**.
- Do not refactor, migrate, reorganise, reset, revert, clean or overwrite existing development work; do not merge into `main`, open an implementation PR, start a new release, or produce an APK.
- Do not assume a function is obsolete because it originated in NOVA or its UI is no longer prominent. Prove call-site and persisted-data dependencies first. Do not confuse "not used by new UI" with "unused by application".
- Do not trigger potentially destructive operations or require the user to test them.
- No physical Nvidia Shield access is expected; do not stall on that. Distinguish **code-established**, **test-supported**, **conditional/unresolved**, and **device-confirmation** claims. Static analysis alone is not proof of runtime correctness.
- Do not reopen approved design decisions or expand fixes-only release scope.
- Save documentation commits to the existing candidate branch only. Keep progress and completed coverage explicit; do not label partial work "complete".

## 3. Establish baseline and sources
Record branch HEAD commit SHA and inspect existing `docs/audits/`, `docs/design/`, `docs/qa/`, `docs/design/references/`, project status and relevant tests. Reconcile implemented code, inherited NOVA behaviour, approved future designs, and user-reported Shield observations as **separate columns**. Do not overwrite earlier audits or treat plans/mockups as proof of implementation.

Trace actual application entrypoints and Preview UI gating, including the `try_new_ui` condition; identify which screens/routes are active, legacy, conditional, unreachable or shared. Cover Java/Kotlin, XML preferences/resources, fragments, activities, adapters, services, DB/provider, persistence, manifest, navigation and background jobs as applicable.

## 4. Priority A — Settings: exhaustive option-level reconstruction
Trace `res/xml/preferences_video.xml`, `res/xml/preferences_more_leanback.xml`, `VideoPreferencesCommon.java`, `VideoSettingsFragment.java`, `VideoSettingsActivity.java`, `PreviewSettings.java`, preference strings, conditional visibility, all dynamically inserted/reparented preferences, inherited categories, nesting and state-dependent enablement.

Reconstruct actual Preview left rail (currently declared: Playback, Video, Audio, Subtitles, Library & Metadata, Home, Appearance, Streaming, Network, Integrations, Advanced, About). For **each category and submenu**, enumerate every row in actual display order with:
- exact user-facing title and summary; source preference key and type (toggle/list/action/category);
- parent path and nesting, default/current-value source and persistence;
- visible/hidden/disabled/conditional logic, including code branch and prerequisite;
- on-click/on-change handler and actual effects; related service, database, library, UI, player or network call sites;
- modern Preview consumption versus inherited/legacy-only usage, including compatibility and old UI paths;
- side effects (DB mutation, library-wide jobs, account/network changes, playback changes), reversibility and safety assessment;
- disposition recommendation: **Keep / Rename / Move / Hide candidate / Further trace**, with evidence and uncertainty;
- source file paths, symbols and line numbers; test coverage; whether device confirmation would help.

Reconcile user-observed **approximately six Playback** rows and **approximately eighteen Library & Metadata** rows; **do not invent counts**. Record computed counts for normal Preview states and how they change under conditions, with methodology. Explicitly investigate:
1. **Hide Watched Videos** (`hide_watched`): `LoaderUtils.mMustHideWatchedVideo`, settings activity finish/UI refresh, actual Preview loader consumers and consequences.
2. **Rescrape All Collections**: `AllCollectionScrapeService.INTENT_RESCRAPE_ALL_COLLECTIONS`, actual collection tables/consumers, job scope, risk and relevance.
3. **Recreate Sort Titles**: `ScraperTables.recreateSortNames`, tables/columns affected, current Movies/TV sort dependencies, cost and risk.
Investigate other rescrape/reset/export/import actions equally rigorously. Trace nested Integrations and Advanced, hidden legacy rows, home-row toggles, conditional account controls and dynamic SFTP identity entries. Do not infer that disabling a preference makes its underlying service removable.

## 5. Priority B — Network & Files: exhaustive interaction inventory
Trace `PreviewNetworkWorkspace.java`, `PreviewNetworkScanning.java`, network fragments, local volume enumeration, saved/indexed shortcuts, SMB/UPnP discovery, SFTP, FTP/FTPS, WebDAV, cloud/put.io, source add/remove/browse/scan, scanning policy, rescan, library effects, credentials, errors, dialogs, focus/Back routing and source-specific conditional UI.

Start from **implemented** sidebar (currently Overview, Local Storage, Network Shares, Cloud Services, Saved Locations), **not** the approved future seven-section design (Local Storage, Attached Storage, Network Sources, Saved Locations, put.io, Downloads, Library Health). For every action and submenu: exact label/order, prerequisites, dynamic population, handler, service/DB changes, data deletion semantics, network/account consequences, error/empty states, legacy fallbacks, safety, code references, and Keep/Move/Hide/Further trace recommendation. Identify features that are only designed, stubbed, not reachable, or implemented elsewhere. Document any dependencies on existing NOVA browser/scanner classes and whether Settings duplicates operational controls.

## 6. Entire application coverage — no omissions
After completing the priority inventories, map **all reachable functionality** and meaningful conditional/legacy routes, at least:
- Startup, navigation, Preview/legacy UI gating, top navigation, focus/Back, page entry and return;
- Home (Featured readiness/rotation/promotion, Continue Watching, Recently Added, rows, Watch Next, move/hide, loading/fallback);
- Movies and TV Shows (grid/list, filtering, sorting, columns, library queries, unmatched handling, per-view persistence);
- Search and matching/rematching, keyboard, catalogue/provider search, fallback, errors;
- Details (actions, metadata, cast/crew, Extras/trailers, More Like This, provider availability, artwork, rematch);
- Playback engine and HUD (play/resume, subtitle/audio selection, timing/appearance, seek, formats, decoder, repeat, history, exit);
- Settings and Network & Files (priority sections above);
- Library indexing, scanner/scraper, local/network storage, DB/provider, watch state, import/export, background jobs;
- Streaming/provider integrations (TMDb, Trakt, OpenSubtitles, JustWatch if implemented, IntroDB/SkipDB, put.io, etc.), login/auth, failure modes and entitlement-dependent features;
- Downloads, Library Health, smart/custom collections and other proposed/parked features: distinguish implemented, partial, legacy, approved future, parked, and absent;
- Diagnostics, About, permissions, onboarding, configuration, upgrade/persistence compatibility, and other reachable activities/services.

For each functional workflow record entrypoint, UI trigger, implementation chain, persisted data, shared dependencies, side effects, feature-state classification, code evidence, tests and unresolved risks. Capture non-user-facing legacy NOVA components relevant to later cleanup. Inspect manifest registrations and references before classifying anything as dead.

## 7. Legacy code cleanup **register only**
Build a cross-referenced candidate register (not deletion instructions) covering obsolete UI options, duplicate handlers, legacy NOVA paths, unreachable features, stale resources, old settings and potentially unused services. For each candidate record evidence of reachability/nonreachability, reflection/manifest/intent/string-key/DB dependencies, persisted compatibility concerns, removal risk, confidence and prerequisites for eventual removal. Categorise **safe to hide in UI after approval** separately from **potentially removable from code later**. No code removal until full audit is complete, user authorises separate cleanup, and regression/dependency validation is planned.

## 8. Documentation deliverables
Create/update GitHub Markdown documentation under `docs/audits/`:
- `APP_WIDE_FUNCTIONAL_AUDIT_STATUS.md`: baseline SHA, methodology, explicit coverage matrix by subsystem, checkpoints, remaining work, known gaps and final status;
- `SETTINGS_FUNCTIONAL_INVENTORY.md`: complete category/submenu/row-level table with source evidence, counts and safety/relevance findings;
- `NETWORK_FILES_FUNCTIONAL_INVENTORY.md`: complete category/action/state inventory;
- `APP_WIDE_FUNCTIONAL_INVENTORY.md`: full workflow/function coverage, dependencies and findings for remaining subsystems;
- `LEGACY_NOVA_CLEANUP_CANDIDATES.md`: evidence-based future-only register;
- `FUNCTIONAL_AUDIT_FINDINGS.md`: prioritised actionable summary, recommended hides/keeps/moves, uncertainty and questions for user; **not** an implementation plan.
Update `docs/audits/README.md` with links and status. Existing audit docs remain valid historical context; cross-reference instead of replacing them. Split large files if necessary, preserving exhaustive coverage and navigability. Every assertion about behaviour should link to path/symbol/line or supporting test, not merely a design document.

## 9. Completion gates
Do not report Settings complete until every XML and dynamically generated preference has been mapped to its Preview-visible category/submenu, including hidden/conditional states and handlers, and observed-count discrepancies explained or explicitly isolated.
Do not report Network & Files complete until every reachable sidebar action, source type, dialog, dynamic/empty state and inherited route has been mapped to code and risk.
Do not report app-wide complete until every major route, user-facing control and meaningful underlying service has been inventoried, cross-dependencies traced, the cleanup register populated, and all documentation committed. Run read-only/static checks or existing tests **only if** safe and not modifying tracked application files; disclose anything untested. Audit conclusions may be "unresolved" with concrete evidence of the limit; no fabricated certainty.
Before final completion, perform **three review passes**: (1) inventory completeness vs source tree/entrypoints/preferences, (2) handler/dependency and side-effect evidence, (3) findings consistency vs approved/parked decisions and no unintended code changes. Record coverage counts, unresolved items, validation method and commit SHAs. Explicitly check `git diff`/status so documentation-only modifications are proven. Never equate an interim checkpoint with completion.

## 10. Reporting to user
Work through the full investigation without routine confirmation questions. If an actual blocker prevents progress, report it precisely with what is needed. At completion, provide a concise final report with links to the GitHub inventories, actual Settings and Network & Files counts and findings, significant safety/legacy candidates, overall coverage, uncertainties, and confirmation that **no application code was changed**. The user will review design dispositions in the planning chat afterward. No unilateral UI or code changes.
