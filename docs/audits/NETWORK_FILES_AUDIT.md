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
