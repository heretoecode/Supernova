# 10 — Inherited, external-intent, classic and conditional surfaces

Source baseline: signed 0.138 `b3504c2c4b1725f3070416d14b63e53ae1db3d74`. Evidence below is **VERIFIED static source** unless expressly attributed to native CI evidence or **UNKNOWN**. No device-navigation test was run. Stable IDs describe surfaces; generated SITE/FOCUS IDs identify exact source lines.

The app-wide map does not stop at the seven primary TV tabs. [Manifest screens](../evidence/manifest-screens.tsv) lists all **78** activity/alias entries with stable component-based IDs, labels, exported state/intent filters and source references. [Full inherited display appendix](../evidence/10-inherited-conditional-source-options.md) contains the source controls and resolved English resources; it is not assumed these are all accessible from today's TV Home.

| ID | Discovered families / entry | Reachability and exact-label evidence |
|---|---|---|
| OTHER-001 | Classic browser MainActivity, grid/list/movie/show/category/collection/file/shortcut pages | EntryActivity non-TV route, old settings/external intents. Android layouts/text resources included; source callbacks differ from current Supernova programmatic TV pages. No removal/deprecation by absence from TV navigation |
| OTHER-002 | Legacy Leanback all-movies/by-alpha/by-year/by-rating/by-genre, all-shows/anime/collection grids | Manifest plus activity/fragment classes, native sort choices/title resource references catalogued. Current Foundation primary renderer is PreviewPages; exact reachable menu route per legacy activity remains conditional/UNKNOWN |
| OTHER-003 | Per-protocol local/network SMB/FTP/SFTP/WebDAV/UPnP browsing | Universal browser integration and retained activities; saved schemes/intent extras affect host. FileCore/MediaLib sibling-generated dialogs/settings require external pinned resources, not invented choices |
| OTHER-004 | Manual movie/show scraping, scraper search/result/description/artwork/cover editing | Triggered from Details/Health or classic details. Indexed/remote candidate names, art, source auth errors conditional. Source catalog includes exact resource-linked action/empty/error labels |
| OTHER-005 | Folder/playlist/subtitle/data/document selection utilities | Torrent download-folder picker, subtitle local/online wizard, backup import/export, playlist paths and Android document provider. External Files/OAuth/player activities not Supernova-owned renders; ownership explicitly separate |
| OTHER-006 | Widgets, notifications, TV channels/search provider/deep links | Manifest external intent and XML searchable/widget strings; foreground playback/download/scan notifications and shortcuts. Runtime channel/platform labels/settings external; app-owned resource text inventoried |
| OTHER-007 | Floating player, external player, SDK unsupported, PiP and utility activities | Overlay/device SDK/capability/account conditions; API branches included in source/focus catalogues. No claim of Shield support merely from class declaration |
| OTHER-008 | Retained advanced debug/sponsor/torrent/classic integration preferences | Existing underlying handlers still present; UI visibility/build flags may hide. BitTorrent remains untouched. Whole-app static sites preserve evidence rather than call them unused |

## Explicit gaps

**GAP-I01**: all legacy/external components have a stable entry and exact source/label candidates, but exhaustive reachability from every installed APK intent/menu state is not executed. **GAP-I02**: sibling module resource-generated surfaces and compiled Foundation branding/resource overlays are not independently inflated here. **GAP-I03**: every locale's rendered translations/order/RTL behaviour is not captured; English baseline documented. **GAP-I04**: Android-generated notification/permission/picker/native-dialog text and OEM focus depend on framework/runtime. **GAP-I05**: syntax catalogue can include literal keys/status strings unrelated to visible UI, and can miss text generated indirectly/reflection; area source methods/resources provide review paths.

These gaps prevent an unqualified claim that the full every-option atlas acceptance is complete. Main production TV flows and the two priority visual packages are documented in detail; future captures/reachability reviews use stable IDs and the existing source catalogue without remapping the whole app.

## Source owners and exact option appendix

- [EntryActivity.class](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/EntryActivity.java#L42)
- [MainActivity.class](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/browser/MainActivity.java#L126)
- [MainActivityLeanback.class](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/MainActivityLeanback.java#L58)
- [FolderPicker.class](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/utils/FolderPicker.java#L64)
- [FloatingPlayerActivity.class](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/player/FloatingPlayerActivity.java#L33)

[Every matched display/choice expression in this area](../evidence/10-inherited-conditional-source-options.md) · [Full display sites](../evidence/display-sites.tsv) · [Focus branches](../evidence/focus-branches.tsv). Supplementary cross-area owners, including inherited utilities, appear in the full catalogue. Literal extraction does not establish visibility.
