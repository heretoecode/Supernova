# Settings audit — 7 October 2026

## Scope
Static architecture/product audit of current Preview settings, legacy preference surfaces and current corrective authority. This is a development foundation, not Shield acceptance.

## Findings
- Preview currently consolidates a large inherited preference tree into modern categories: Playback, Video, Audio, Subtitles, Library & Metadata, Home, Appearance, Streaming, Network, Integrations, Advanced and About. There is still substantial legacy preference ancestry beneath that presentation.
- `PreviewSettings` dynamically creates/re-homes controls. This is useful for migration but makes ownership harder to reason about than a declarative settings model.
- A disabled Legacy bucket deliberately preserves old values. Keep it during stabilisation, but it should not become a permanent dumping ground.
- Network/library scanning appears in settings while operational scan controls also exist in Network & Files. Operational source/scan management belongs primarily in Network & Files; Settings should retain defaults/policy.
- Playback HUD/session controls and persistent defaults must remain explicitly separated. The recent Audio/Subtitles audits already establish this rule.
- Diagnostics are correctly development-oriented but currently add substantial surface area to Advanced.
- Integrations need one consistent hierarchy: parent in left rail, children indented beneath it, child configuration in centre.
- Inert/attribution-only rows should never masquerade as actionable settings.

## Recommendations
1. Define one owner for every setting: global default, library/source policy, session control, integration account, appearance, diagnostic, or legacy.
2. Move operational source actions (scan now, source management, download management) to Network & Files; leave scheduling/default policy in Settings where appropriate.
3. Create a migration/removal register for Legacy rather than adding new entries indefinitely.
4. Replace runtime re-parenting gradually with a declarative Preview settings schema once behaviour is stable.
5. Add per-category D-pad/focus/Back tests and a duplicate-setting test.
6. Keep Advanced/Diagnostics visually separated from normal user preferences.
7. Future Downloads preferences should be limited to defaults (destination, Wi-Fi/network policy if added); active transfers belong in Network & Files.

## Future-development questions
- Which legacy saved values still need migration compatibility after the later fresh-install identity transition?
- Which Network controls are policy versus immediate actions?
- Should About/diagnostics expose build evidence only in preview/development builds?

## Runtime-interface trace addendum — 8 October 2026
Source inspected: `VideoSettingsFragment.java` (onCreatePreferences), `PreviewSettings.java` (organise, consolidate, sidebar). This is source-path evidence, not a physical Shield inventory.

- **Entry gate:** `PreviewSettings.organise(this)` runs only when default SharedPreferences `try_new_ui` is true. The classic preference route is therefore materially different. The modern sidebar is explicitly assembled with twelve category names: Playback, Video, Audio, Subtitles, Library & Metadata, Home, Appearance, Streaming, Network, Integrations, Advanced, About.
- **Runtime migration:** `organise` locates/reuses legacy categories by key, creates missing ones, moves settings by key, creates new controls, then `consolidate` renames/reparents groups. Unexpected inherited categories are drained into Subtitles, Advanced or Sources & Storage based on title and hidden.
- **Explicit visibility:** `consolidate` hides `rescan_storage`, `auto_rescan_on_app_restart`, `preview_scan_interval_control`, `share_folders`, `preferences_torrent_path`, `preferences_torrent_blocklist`, `uimode`, `uimode_leanback`, and the entire Legacy category. `subtitles_hide_default` is hidden and inverted through a nonpersistent visible `Subtitles by Default` switch. The inert `preview_updates` row is removed if present. Attribution rows for OpenSubtitles/TMDb/Trakt are disabled/nonselectable. Legacy Home, sorting, old UI and theme controls are disabled, then hidden by their parent.
- **Dynamic controls:** SFTP server identities only offers a recorded-host choice when host identities exist; otherwise shows an empty-state explanation. New Home editor, Clear Watch Next, Featured source switches, accent picker, diagnostics controls, scan interval and other Preview controls are created in code. Trakt, OpenSubtitles and IntroDB are nested under Integrations; compatibility subcategories are nested under Advanced.
- **Presentation rule:** Sidebar initially captures each direct child preference's `isVisible()` state, then shows only the selected top-level category and its direct non-category preferences. Nested category buttons are generated only when the parent is entered; opening one changes which children are visible. Consequently raw XML alone cannot establish the displayed menu.
- **Potential discrepancy to validate:** Scan controls are constructed under Library then explicitly hidden, with Network & Files owning operational scan controls. Do not count these as visible Settings options. The inherited classic route and other conditional preference declarations need a complete resource/entry-point trace before claiming an exhaustive option-by-option inventory.
- **QA still needed:** runtime state for Preview toggle, installed services, device-specific preference availability, actual nested navigation and persisted legacy values. No Shield execution was performed.

## User-facing functional relevance review — 8 October 2026
Objective clarified: for each **actually displayed** setting, match exact label/order to code, identify whether modern Preview uses it, document effects and risk of activation, and recommend Keep / Rename / Move / Hide / Further trace. Hidden/legacy options should be recorded, not deleted from code as part of the active fixes-only release. Compare on-device counts (user reports approximately six Playback and eighteen Library & Metadata entries) against code-generated counts; avoid claiming an exact count until nested visibility, XML and runtime conditions are traced.

Three concrete Library & Metadata examples confirmed in `VideoPreferencesCommon.java`:
- `hide_watched`: preference-change handler writes `LoaderUtils.mMustHideWatchedVideo`, sets `ACTIVITY_RESULT_UI_MODE_CHANGED` and finishes the Settings activity. The impact on modern Preview Movies/TV loaders still requires call-site tracing. Do not assume it is inert or safely obsolete.
- `rescrap_all_collections_prefkey`: click starts `AllCollectionScrapeService` with `INTENT_RESCRAPE_ALL_COLLECTIONS` and displays a progress toast. This is a real legacy collections rescrape operation; candidate for UI hiding pending service reachability and collection-data dependency review. Do not activate as a test.
- `recreate_sort_titles_prefkey`: click launches a background thread and invokes `ScraperTables.recreateSortNames(VideoDb.get(context))`. It changes library database sort names; modern sort-field dependencies require tracing before removal/hiding. Do not activate as a test.

No user-facing hide decision or application modification is authorised by these preliminary findings.


## App-wide functional investigation — source checkpoint (8 October 2026)

Verified active Preview source: `src/main/java/com/archos/mediacenter/video/leanback/settings/PreviewSettings.java` (282 lines), `res/xml/preferences_video.xml` (742 lines), and `src/main/java/com/archos/mediacenter/video/utils/VideoPreferencesCommon.java` (1971 lines). Preview sidebar `NAMES` (line 59) declares **12** categories: Playback, Video, Audio, Subtitles, Library & Metadata, Home, Appearance, Streaming, Network, Integrations, Advanced, About. This is the defined sidebar inventory, **not** proof of an exact number of visible rows within any category on the user's Shield. Inherited XML preferences are dynamically moved and consolidated; some are hidden (PreviewSettings lines 84–87) and other legacy home-row controls disabled (line 97). A complete row-by-row count must include dynamic preferences, visibility, category nesting and persisted conditions. User reports approximately 6 Playback and 18 Library & Metadata options, pending reconciliation.

Confirmed handlers in `VideoPreferencesCommon.java` lines 907–955: Hide Watched Videos toggles `LoaderUtils.mMustHideWatchedVideo` and closes Settings with UI-changed result; Rescrape All Collections starts `AllCollectionScrapeService`; Recreate Sort Titles calls `ScraperTables.recreateSortNames` in a worker thread. Do not test these by activating them on the user's library. Trace consumers/dependencies before proposing final dispositions. Source evidence only, no physical QA or application changes.
