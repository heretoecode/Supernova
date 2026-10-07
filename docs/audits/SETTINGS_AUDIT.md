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
