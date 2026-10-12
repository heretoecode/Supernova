# 05 — Network & Files, onboarding, source forms, scanning, Health and cloud

Source baseline: signed 0.138 `b3504c2c4b1725f3070416d14b63e53ae1db3d74`. Evidence below is **VERIFIED static source** unless expressly attributed to native CI evidence or **UNKNOWN**. No device-navigation test was run. Stable IDs describe surfaces; generated SITE/FOCUS IDs identify exact source lines.

## Universal browser and source controls

| ID | Ordered labels / entry / conditions | Effect/persistence and navigation |
|---|---|---|
| NET-001 | `Locations`: `Overview`; `Local Storage  ›` with drives (Internal Storage/name); `Network Shares`, `Add Network Source`, `Discover Devices`, `Cloud Services`, `Network Scanning`, `Library Health  ›` with categories, saved locations | Same UniversalFileBrowser hosted from Network root/Home/protocol/provider/picker. Rail movement selects context; Local/Health expansion mutually exclusive; browsing never automatically includes a source |
| NET-002 | Centre toolbar `List View`/`Grid View`, `Sort`, `All Files: On/Off`; crumbs; `Information` right heading/facts | Default list, Name ascending, all-files preference false unless saved. Sort `Name ↑`, `Name ↓`, `Date ↑`, `Date ↓`, `Size ↑`, `Size ↓`. Separate `supernova_browser_grid/sort` prefs; facts name/type/address/inclusion source |
| NET-003 | Overview right `Scan Library`, `Network Scanning`, `Library Health` | Centre scan status explicitly **nonfocusable**; right description scroll is deliberate focus target if nonempty. Scanning continues away from page. No user NAS scan invoked in atlas work |
| NET-004 | Add Network Source: `Protocol: SMB`; protocol order `SMB`, `WebDAV HTTPS`, `WebDAV HTTP`, `SFTP`, `FTP`, `FTP TLS` | Form order `Server / Address`, `Port`, `Path`, `Username`, `Password`, SMB-only `SMB Domain`, `Save Password: On/Off`, `Show Password: On/Off`; right `Connect`. Default path `/`, save On/reveal Off; SMBJ/SSHJ pref chooses actual scheme |
| NET-005 | Shared input validation | Host `Enter a server name or IP address without a protocol or credentials.`; port decimal input length check, connect numeric range1–65535 `Enter a port from 1 to 65535.`. Empty port default protocol. Password secret keyboard512; successful connect clears visible password, stores separate credential, then browses |
| NET-006 | Network Shares / discovery | `No saved shares`, `Add Network Source`; discovery `Refresh`, `Discovering…`, `<n> devices`, `SMB discovery unavailable. Direct connections remain available.`, `Device discovery unavailable. Direct connections remain available.` SMB + UPnP data, FTP/SFTP direct | Async generation/listener cleanup; real device names/runtime errors UNKNOWN |
| NET-007 | Listing states/actions | `Loading…`, `<n> items`, `No matching files or folders`, error `Retry`; selection-dependent `Add to Library` / `Remove from Library` / `Exclude from Library`, `Restore Inclusion`, folder `Save Location`, saved `Remove Saved Location`, indexed `View Details`, dirty `Save Changes & Scan` | Root/exclusion edits staged in BrowserSelection. Remove/Exclude here preserves physical files. Remove saved confirmation title `Remove saved location? Library records and media files are kept.` → Cancel/Remove; failure `Removal not confirmed` / retry. No source deletion executed |
| NET-008 | Onboarding `Choose Your Media Folders`; `Browse Folder`, `Save & Finish`; directory picker `Choose a Folder`, writable `Choose This Folder` | Onboarding checkbox clicks stage local folders; nonlocal `Choose local folders during initial setup.`. Save writes roots then scans. Writable picker affects download destination, not library. `This folder is not writable. Choose another location.` |
| NET-009 | `Unsaved library changes`: `Discard Changes`, `Continue Editing`, `Save Changes & Scan` | Continue initially focused; Back dismisses and restores opener/centre/rail; save rollback keeps prior roots on failure. `Library changes pending`, `Changes not saved`, exact descriptions in appendix |
| NET-010 | Network Scanning: `Automatic: On/Off`, `Frequency: <n> minutes`, `On Open / Return: On/Off`, `Sources Included`, `Scan Now` | Automatic frequency default remembered hour; frequency options `15 minutes`, `30 minutes`, `1 hour`, `6 hours`, `24 hours`; On Return true. Period scheduling delegated existing scanner/Android jobs. Separate PreviewNetworkScanning modal has its own exact captions in appendix |

Focus: three panels .31/.42/.27; rail Up/Down stays in rail, Right enters centre; centre Left returns rail anchor, Right enters info/actions; right Left returns centre, description Up/Down scroll until edges then actions. Context actions revealed on focus. Folder/file selection keyed by URI, remembered per-source positions; stale listing callbacks discarded. Dirty exit applies before changing source/navigation. Compact and 50+-folder/8+-drive physical focus outcomes UNKNOWN.

## Library Health

| ID | Exact categories/action order | Semantics |
|---|---|---|
| HEALTH-001 | `Unmatched Media`, `Incorrect Matches`, `Missing Metadata or Artwork` (rail shortened `Missing Metadata`), `Unavailable Files`, `Source Problems` | Healthy/attention overview; import/identification excluded from unmatched/missing warnings. `No issues in this category` empty state |
| HEALTH-002 | Media review `Filename`, `Location`, `Match Status`, `Availability`; actions `View Details`, `Find a Match`, incorrect-only `Match Is Correct` | Correct clears incorrect flag. Unavailable retains records/history. Source problem right `Scan Library` only. All actual source names/runtime availability UNKNOWN |

## put.io / Cloud Services

| ID | Existing conditional choices | Effect / limitations |
|---|---|---|
| CLOUD-001 | put.io `Enabled: On/Off`, `Account / Connection`; disconnected `Connect put.io` | Default enabled true; disable preserves credentials/associations; OAuth needs registered client config. No credentials invented or live sign-in attempted |
| CLOUD-002 | Account ordered `Library folders / Sync Now`, `Associate Movies folder`, `Associate TV Shows folder`, `Change Library Folders`, `Browse Files`, `Search Files`, `Account / Connection`, `Disconnect` | Selected folder association/account data/API pagination determines more options. Account switch `Cancel`, `Keep previous sources inactive`, `Return previous sources to generic discovery`; disconnect `Cancel`, `Keep associated sources inactive`, `Return associated sources to generic discovery` |
| CLOUD-003 | Folder picker `Use this folder · <name>`, `Back to root`, folder names, optional `Next page`; search `Back to account`, `Change search`, results/next page | Existing real remote result universe conditional; no invented results/captures. Preserve/link review `Cancel`, `Save & Continue`; reassignment waits complete source read before retiring old ownership |

All app-owned cloud UI expression sites are in the appendix, including reconnect/resume/review messages. Actual OAuth website/document picker/provider app UI is external and not claimed to be Supernova UI.

## Source owners and exact option appendix

- [UniversalFileBrowser.public UniversalFileBrowser](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/filebrowsing/UniversalFileBrowser.java#L74)
- [UniversalFileBrowser.private void connectionForm](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/filebrowsing/UniversalFileBrowser.java#L235)
- [UniversalFileBrowser.public void requestExit](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/filebrowsing/UniversalFileBrowser.java#L425)
- [PreviewNetworkScanning.public static](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/PreviewNetworkScanning.java#L14)
- [LibraryHealthPanel.public static final String[] NAMES](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/LibraryHealthPanel.java#L15)
- [PutioAccountController.private void disconnect](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/streaming/putio/PutioAccountController.java#L43)
- [BrowserSelection.class](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/filebrowsing/BrowserSelection.java#L8)

[Every matched display/choice expression in this area](../evidence/05-network-health-source-options.md) · [Full display sites](../evidence/display-sites.tsv) · [Focus branches](../evidence/focus-branches.tsv). Supplementary cross-area owners, including inherited utilities, appear in the full catalogue. Literal extraction does not establish visibility.
