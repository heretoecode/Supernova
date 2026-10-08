# Network & Files audit — 7 October 2026

## Scope
Current Preview Network & Files workspace, inherited network browsing, storage/source management and future-download implications.

## Current Preview model
Three-panel workspace:
- Left categories: Overview, Local Storage, Network Shares, Cloud Services, Saved Locations.
- Centre: sources/items for selected category.
- Right: description/context/actions.

Current capabilities evidenced in code include:
- Internal/local storage and attached USB/SD/other volumes.
- Indexed network library sources and saved browsing locations.
- SMB and DLNA/UPnP discovery.
- Manual network source path supporting SMB, WebDAV HTTP/HTTPS, SFTP, FTP and FTP over TLS.
- Browse, scan source, add saved location to library and remove source/location.
- Library/network scanning controls.
- put.io account/connection entry; original-quality media is described as using its WebDAV source.
- Google Drive, OneDrive and Dropbox are currently presentation-only “Coming soon” entries.

## Findings
- The three-panel semantic model is strong for TV because category → item → context is visible without modal churn, but current physical QA has shown geometry/focus quality matters as much as architecture.
- Network & Files currently mixes three concepts: storage volumes, library sources and remote services. These should be visually distinguishable.
- Operational scanning belongs naturally here; duplicate policy controls in Settings should be reduced.
- Inherited legacy network screens still contain context-menu/QuickAction patterns. New Preview UX should avoid reintroducing desktop/context-menu interaction.
- Saved Locations and indexed Library Sources are different states and should remain explicit.
- Cloud Services currently risks implying integrations that do not yet exist.
- Downloads fit this domain better than Settings because they are file/source operations.

## Recommended information architecture
Left rail candidates:
Overview · Local Storage · Network Sources · Saved Locations · Cloud Services · **Downloads**

Downloads should manage Active / Completed / Failed transfers. Starting a download may originate from Details/file browsing; management belongs here.

## Future WebDAV/put.io download foundation
- Build transfer capability generically against authenticated WebDAV, initially validate with put.io.
- User chooses local/attached-storage destination.
- Background/resumable transfer where server/range support permits.
- Preflight free space and destination writability.
- Deterministic duplicate-file policy.
- Active progress, speed, remaining bytes/time, cancel; pause/resume only if technically reliable.
- Completed item links back to library/local file; normal Play should prefer valid local copy automatically.
- Deleting local copy must not delete remote source unless explicitly offered as a separate destructive action.
- Interrupted/failed transfers must be recoverable and must never be indexed as complete media.
- Keep credentials out of persisted job descriptions/logs.

## Recommended next design work
Explore three alternatives before implementation: refined three-panel; narrow rail + large content workspace; more visual source cards. Evaluate each by D-pad path length, focus predictability, information density and ability to host Downloads.


## Product-direction update — 7 October 2026

### Cloud placeholders
Remove the Google Drive, OneDrive and Dropbox “Coming soon” placeholders from the future Network & Files design. Unsupported future services should not occupy production navigation. Additional services can be added later when they have real implementations.

### put.io placement
Do not use a generic Cloud Services category solely to contain put.io. put.io should be separated from speculative cloud placeholders. Two acceptable design explorations remain:
1. give **put.io** its own left-navigation entry; or
2. place put.io in another semantically appropriate source/account location once that wider information architecture is designed.

**SUPERSEDED by the later approved design:** put.io now has its own left-navigation entry for the approved future direction. Preserve the put.io/WebDAV distinction: account/service integration is not the same thing as generic WebDAV file transport.

### Library Health — approved future direction
Promote **Library Health** from parked concept to an approved future design direction, but do not add it to the active fixes candidate.

Library Health belongs in **Network & Files** and becomes the management location for actionable library problems. The existing Movies/TV **Unmatched / Unscraped** toolbar feature should eventually be removed from those browsing pages and its user-facing responsibility moved into Library Health.

Initial Library Health problem classes should include:
- unmatched / unidentified media;
- possible incorrect or stale matches where confidence/evidence justifies surfacing them;
- inaccessible/unavailable indexed files or library sources;
- source/authentication problems requiring user action;
- other future actionable library integrity problems.

Missing artwork or temporary metadata/provider failures should not automatically be counted as user-facing health problems when Supernova can reasonably recover by retrying in the background.

### Unmatched media flow
When scanning discovers media that Supernova cannot confidently identify, record it as an actionable Library Health item. Library Health should expose enough filename/path and inferred metadata evidence for safe manual matching/correction. Resolving a match should reconcile title metadata/artwork/provider caches while preserving physical file identity and playback/watch state.

### Home awareness without new focus targets
Home may show a small **non-focusable informational status message** when actionable Library Health items exist, for example:

> 2 media files need attention · Review in Network & Files

This is information only:
- it must never enter the D-pad focus graph;
- it has no OK/click action;
- it must not alter row navigation or focus restoration;
- it disappears automatically when no actionable health items remain.

The user navigates to Network & Files normally.

### Network & Files attention state
When actionable health items exist, Network & Files should make this obvious without stealing focus. Candidate treatments include a restrained attention dot/count on the Network & Files navigation label and a visible count/status on the Library Health entry. Opening Network & Files should expose the problem through its normal D-pad hierarchy rather than an interrupting modal.

Principle: **Home informs; Network & Files diagnoses; Library Health resolves.**

### Revised design candidates
Future Network & Files mock-ups should now accommodate:
- Local Storage
- Network Sources
- Saved Locations
- Downloads
- Library Health
- put.io as either its own entry or another deliberately chosen location

**Resolved by the later approved design:** Overview is removed from the approved future left navigation.


## Design approval record — 7 October 2026
The Network & Files design discussion has now reached an approved future direction. The approved design is recorded in docs/design/NETWORK_FILES.md and its visual references in docs/design/VISUAL_REFERENCE_MANIFEST.md.

Key resolution: vertically centred three-panel layout; structured left rail; Local Storage, Attached Storage, Network Sources, Saved Locations, put.io, Downloads and Library Health; no Overview; no Google Drive/OneDrive/Dropbox placeholders; generic WebDAV represented as a network-source protocol rather than a top-level destination; consistent submenu grammar for Network Sources, Downloads, Library Health and Unmatched Media.

This remains future design authority, not permission to broaden the current fixes candidate and not physical Shield acceptance.

## Runtime-interface trace addendum — 8 October 2026
Source inspected: `PreviewNetworkWorkspace.java` and legacy `NetworkRootFragment.java`. This reconstructs the current Preview workspace from its construction code, not physical Shield acceptance.

- **Actual current Preview left rail:** `Overview`, `Local Storage`, `Network Shares`, `Cloud Services`, `Saved Locations`, in that order. The approved future rail (Local Storage, Attached Storage, Network Sources, Saved Locations, put.io, Downloads, Library Health) is **not** implemented by this current workspace constructor.
- **Panels:** runtime constructor builds three columns with relative weights 23% / 45% / 32%, separate scrollable middle and right panels, and top/bottom padding. Focusing a left category rebuilds the middle and context contents; focusing an item rebuilds right-hand actions.
- **Overview:** `Scan Library` and `Network Scanning`. The latter offers Automatic On/Off, Frequency (15/30 minutes, 1/6/24 hours), On open/return, Sources Included, and Scan Now; current period and preferences determine labels and values.
- **Local Storage:** dynamically lists available Box entries of FOLDERS, USB, SDCARD and OTHER types. FOLDERS is displayed as Internal Storage; other entries use actual volume names. Right panel shows path, readability, free/total space, and Browse. Empty state if none.
- **Network Shares:** lists indexed Shortcut sources, plus Add Network Source (SMB, WebDAV HTTP/HTTPS, SFTP, FTP, FTPS) and Discover Devices (SMB computers/NAS, DLNA/UPnP media servers). Each indexed source has Browse, Scan Source and confirmed Remove from Library; removal leaves media files untouched.
- **Saved Locations:** lists saved Shortcut entries, or an empty-state message; each offers Browse, Add to Library and confirmed Remove Saved Location.
- **Cloud Services:** one actionable put.io account/connection entry; Google Drive, OneDrive and Dropbox are inert, dimmed `Coming soon` labels. These are current implementation facts, not approved future design.
- **Runtime dependence:** volume enumeration, indexed/saved source lists, scan period, stored preferences and available put.io account state affect displayed content. The older `NetworkRootFragment` has separate asynchronous indexed-folder, SMB-discovery, UPnP and network-shortcut rows; verify routing before conflating it with the Preview workspace.
- **Missing from current constructor:** top-level Downloads, Library Health, separate Attached Storage and put.io navigation entries. These remain approved future requirements, not implemented or Shield-accepted here.
- **Follow-up validation:** trace all callers/workspace launch conditions, async source list refresh, permissions/storage API differences, authentication state, focus restoration and on-device geometry before declaring exact physical visibility.


## App-wide functional investigation — source checkpoint (8 October 2026)

Verified current `src/main/java/com/archos/mediacenter/video/leanback/PreviewNetworkWorkspace.java` (142 lines). Its current sidebar constructor (line 40) defines **five** sections in order: Overview, Local Storage, Network Shares, Cloud Services, Saved Locations. This is the *implemented* workspace, not the previously approved seven-section future design. Contents depend on indexed/saved sources, available storage, account state, discovery results and scanning preferences. Network scanning frequency is user-selectable among 15/30 minutes, 1/6/24 hours (line 109); Google Drive, OneDrive and Dropbox are separately listed (line 72). Trace each action and all inherited network browsers before asserting end-to-end behaviour or declaring controls obsolete. No physical Shield verification or application changes.
