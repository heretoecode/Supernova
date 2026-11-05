# 0.135 baseline and audit checkpoint

## Authority and preserved lineage

Read master first, then shared framework, scope checklist, expanded approvals, Library Health, Custom Library Page, HUD, recent product/chat decisions, Backup, Search/Matching, Movies/TV, About, Settings, Network, Home, Details, Playback, global visual rules and Shield QA. Approved images inspected: shared Settings, Search, expanded Details, custom creation and landing. The HUD original image is explicitly absent; implement written authority, never fabricate it. Backup images are illustrative; Android picker is the later authority.

Main at start: eb9dfd1d1df86bd6949a3124e92dc72b463b700f. Clean newly cloned workspace, no preexisting local changes. All remote branches fetched; no open PRs returned by GitHub. Foundation base: 788071ee878d2e7d70e9c78b90b9010edccfd113. Installed release is user-reported stable 0.134. Repository signed-delivery evidence identifies application cf1211ceea12a6e1ae82e10cbfb247d33d4ad01f, protected workflow 4448f67c, APK SHA256 8d159d9ffc70d5ffbe335c0caa280fdf742909911581c85d55865d906ff58e29, package app.supernova.player, versionCode 134, certificate 79ed34c52c3e359756ade0634d7bbb6f8e94e42a059020c1220bd8c7092a9f5e. No attached Shield; installed APK/device state cannot be independently verified here.

Work branch codex/supernova-0.135 starts from Foundation tip, retaining all Foundation identity/assets/native/legal/build safeguards. Latest approved handover and missing visual references copied from main without merging or replacing older application code. Main, Foundation and fixes branches remain untouched. No signing secrets/assets are supplied locally. Existing protected signing workflow currently permits only Foundation branch and pins 0.134 source; it cannot produce 0.135 by rerunning it unchanged.

## Conflict resolutions

Master supersedes planning-only/fixes-only restrictions for this authorised 0.135 task, seven formerly undecided browser/Settings items, parked Library Health, Reset Progress, underline toolbar, sixth wizard exclusion step, general hero-gradient deferral and monochrome My Providers logos. Foundation identity/branding is preserved. Master/HUD inventory is Subtitles, Audio, physically centred Play/Pause, More; this takes precedence over the earlier detailed HUD document's Play/Pause-first order. Foundation white row focus supersedes old blue active-panel glow. Existing About flexible merged layout is preserved. No Smart Collections, Discovery, person pages, speculative ratings, Profiles/AI/Anime/thumbnails or new credentials.

## Browser audit dispositions

The complete original callsite inventory is BROWSER_CALLSITES.md (49 Java files, definitions and callers, plus manifest).

| Entry family | Existing behaviour | Required disposition |
| --- | --- | --- |
| Home empty / MainActivityLeanback / PreviewPages | Empty text and Customise; Network workspace opened separately | Inline compact shared browser; one-time welcome |
| Network & Files / PreviewNetworkWorkspace | Independent 23/45/32 workspace, floating text, Browse launches activity | Shared foundation with inline universal browsing |
| LocalListing/ExtStorageListing | ListingFragment engine + PreviewBrowserSurface | Same universal browser via common adapter, preserve volume roots |
| SMB/SMBJ, FTP/SFTP/SSHJ, WebDAV, UPnP Listing subclasses | Same ListingFragment with protocol credentials/actions | Shared browser route; retain connection/discovery/credential implementation |
| NetworkRootFragment discovery / PreviewSources | Native discovery/connect before ListingActivity | Retain protocol discovery/connect; hand selected URI to shared browser |
| NetworkShortcutDetails / PreviewSourceManagement | Standalone source options workspace | In-browser management; explicit source removal confirmation |
| Saved shortcuts / PreviewFolderActions | Independent add Movies/TV dialog scans immediately | Stage automatic recursive inclusion; save-location remains separate |
| PutioBrowserActivity | Provider file list wrapped by PreviewBrowserSurface | Shared browser presentation; retain provider account/import restrictions |
| Classic phone/tablet BrowserByFolder/Local/USB/Ext/network and MainActivity | Touch/classic compatibility UI, external intent contracts | Preserve phone contracts; TV routes use universal browser, document any remaining exception |
| VideoPicker | External PICK video result contract | Keep external result contract; storage view reuse only where result semantics remain intact |
| FolderPicker (torrent download folder/preferences) | Result-based writable local directory chooser | Keep result/writeability contract; adapter needed before replacement; no library selection actions |
| Android OPEN/CREATE_DOCUMENT, GET_CONTENT, document-tree | Backup/restore, subtitle/artwork/diagnostic output grant | Deliberate exception: retain system picker required for persisted URI grants |
| Indexed BrowserByQuery/Movies/Shows, artwork and match selectors | Media/metadata selection rather than filesystem | Deliberate exception: keep domain-specific selector |

## Backend findings and necessary changes

- ListingFragment already uses ListingEngineFactoryWithUpnp, async file/database merge, errors, timeouts, credentials and per-protocol behaviour. Existing surface has 21/54/25 widths, ellipsised breadcrumb, redundant Open/Source Options, and no persisted visible List/Grid/All Files controls. Replace composition, preserve protocols and metadata merge.
- Source addition currently classifies via popup and starts NetworkScanner immediately. Staging must persist roots/exclusions before invoking existing scheduler once. Local MediaStore reconciliation also needs exclusions; UI-only filtering is insufficient.
- NetworkScannerServiceVideo's traversal skips deletion on partial errors, but successful reconciliation deletes absent rows. VideoStoreImportImpl.removeMissingPrimaryRows deletes absent primary-storage rows. These conflict with 0.135 retention. Availability must be separate from removal; preserve stable IDs and metadata. Existing local remapping is already conservative and should remain.
- Existing network scheduler coalesces traversals and manual command requests promptly; do not replace it. Existing local import trace, scan phases and counters are available, but presentation uses changing text/raw seconds. Reuse those counters for fixed overview.
- PreviewSettings direct diagnostics under Advanced are bypassed by compatibility-only child navigation. Add peer Diagnostics and retain handlers/keys; share panel geometry. FoundationAboutWorkspace already provides local legal text/QR and verified release history; preserve it.
- PreviewHomeRows dynamic membership is calculated at render time and not frozen into members on OFF; manual chooser omits dynamic rows and lacks reversible direction. Implement freeze/union/manual/sort without discarding stored members.
- PreviewSearchText currently only Unicode folding and substring matching; it does not support punctuation/article/typos. Search reads title/episode/path only; do not advertise broader indexed fields without adding actual supported data.
- PreviewUpNext currently uses 15 seconds, a Cancel button and can advance from estimated credits; change to five seconds with safe progression gates, test against inherited auto-next race.
- PlayerService already accounts real playback samples; verify its threshold/natural-end policy rather than assuming seek-position correctness.
- MediaLibraryBackupService exports credentials_db. SettingsBackup serialises every key. MigrationBackup iterates almost all named prefs. SafeBackup accepts credential DB/pre-format archives. These are confirmed secret-export/import risks. Apply explicit format gate, database sanitisation, filtered preferences, archive guide and preserve verification/journal/rollback.
- Metadata source feasibility: current TMDb, streaming provider and IntroDB integrations are inherited; full alternate-title/HDR/device capability support must be verified. No unsupported providers/keys assumed.

## Baseline validation

36 existing Python guard tests PASS (python3 -m unittest discover -s tools -p test_*.py, 4.552s). Gradle baseline attempts: first failed direct wrapper download because Java did not inherit HTTP proxy; proxy-configured retry downloaded Gradle and configured 0.134 correctly, then failed because environment had JRE without javac. SDK 37.0, build-tools 36.0 and platform-tools installed; JDK installation is underway. These are environment failures, not a successful application build. Local development Java tests exclude native ndkBuild only because native workspace is not installed; final full release assembly must include native build and conformance, without exclusions.
