# FD-003 — Network & Files visual and interaction blueprint (draft v1)
Date: 2026-10-23
Status: OWNER-APPROVED DIRECTION; DETAILED BLUEPRINT DRAFT — NOT IMPLEMENTATION AUTHORISATION
Target: Supernova Android TV, 1920×1080 design coordinate system; scale uniformly to viewport with safe-area adaptation.
Baseline: delivered 0.141 app SHA 4f0937f1fe8826f50d2c0e844b0837e8a2360a46.
Functional authority: docs/audits/FD-003-network-and-files-functionality-audit-2026-10-23.md on docs/network-files-functionality-audit-2026-10-23.
Global visual authority: docs/design/GLOBAL_VISUAL_SYSTEM.md. This document supersedes docs/design/NETWORK_FILES.md only for future Network & Files layout/hierarchy, once implemented and physically approved. Until then existing code/design remains current.

## 1. Owner decisions (binding)
- Replace rigid three-panel layout with a fixed left navigation rail and ONE flexible content workspace.
- Fixed rail remains visible while browsing files/folders and during connection/account workflows.
- Six top-level destinations in order: Overview, Local Storage, Network Shares, Cloud Services, All Files, Library Health.
- Overview is default on initial entry. Returning from child view preserves section, selection and scroll.
- Icons are MONOCHROME, transparent background, no icon tile, disc, coloured square or gradient behind icon. Focus blue belongs to the focused row/border, NOT individual icons.
- Supernova Space Black Blend / existing typography and blue focus language; no new brand/font invention.
- No Quick Actions strip. Overview is information-first, not duplicated links. Four cards: Storage, Network Shares, Cloud Services, Library Status.
- D-pad-only navigation is first-class. Focus never gets trapped or teleports.

## 2. Visual reference manifest (three owner-selected mock-ups in this conversation)
R1: latest revised monochrome Overview image, generated 2026-10-23, image generation ID 40e38f25-296a-4046-a516-bed2e8ea4214. Composition reference for overall dashboard, fixed rail, four vertical cards, no Quick Actions, bare monochrome icons.
R2: WebDAV nine-state storyboard, generation ID 80d72529-0842-471c-8ee9-9b2401bee454. Visual reference for connection list, protocol selection, form, validation, progress, success, failure, return to browsing.
R3: Put.io nine-state storyboard, generation ID 0a2bbc04-4398-4480-ac74-0ccbd749a4c3. Visual reference for cloud account entry, device authorisation, pending/success/failure, connected account and browse.
IMPORTANT: image generation IDs are provenance references, NOT image files. Actual reference PNG bytes must be committed to docs/design/assets/network-files/ and their SHA-256 recorded before pixel-accurate implementation. Do NOT claim that these images are currently repository assets. The mock-up names/URLs/IPs/capacities/statuses are placeholders. R3 depicts Google Drive/Dropbox/OneDrive and manual API token; these are NOT authorised features. R2's 'Test Connection' and R3's simplified authorisation screens are aspirational; validate exact backend flow and don't invent API semantics.

## 3. Coordinate and layout contract
Canvas 1920×1080 logical px; preserve existing global top navigation, not the mock-up's simplified wordmark-only header. The screen content begins below the global nav fade/safe area, with no extra duplicate top bar.
Design grid: 24px base exterior margin, 20px inter-pane gutter, 16px card gutter, 12px row internal spacing, 8px micro spacing. Corner radii: 20px workspace/rail, 16px card, 12px focus row; 1px low-contrast strokes. These are target design tokens to verify against existing shared Android dimensions, not permission to overwrite the global system.
Fixed rail target: 23% of usable content width, minimum 320px and maximum 400px at 1080p; content workspace receives remainder. The left rail never scrolls horizontally. Main workspace scrolls vertically independent of rail; list/detail screens may use a split *inside* workspace when useful but never restore mandatory three-panel chrome.
Left navigation: six rows in specified order, each target 72px high at 1080p; 20px vertical icon/text separation not permitted (horizontal alignment only). Icon target 28–32px, single-stroke monochrome light grey/white. Labels white 26px-equivalent, subject to actual font scale and shared font tokens. Selected/focused row gets compact blue rounded border and restrained blue tint/glow; icons stay monochrome.
Main title 38px-equivalent, secondary description 22px-equivalent; card title 26px-equivalent, item name 22px-equivalent, secondary metadata 18px-equivalent. Use *existing Supernova typeface* and Android sp conversion; do not substitute a new font based on AI-rendered glyphs. All text must remain legible at 10-foot TV distance and honour system scaling. Long names/paths ellipsise or scroll only on deliberate focus; never overlap status/action controls.
Colours: near-black/space-black translucent surfaces, white primary text, desaturated grey secondary, Supernova blue ONLY focus, selected state and progress; semantic connected/unavailable green/red dots allowed (these are status indicators, not icon colours). No coloured icon tiles, including on Put.io or card headers. No live per-card blur.
Card heights depend on real entries; no clipping. Cards vertically stack on Overview. In cards, rows can show name, source/path, status and capacity only when data genuinely available. Empty, loading, error and overflow states are required.
Global navigation and clock positions must follow existing global design authority, NOT the mock-up's simplified header.

## 4. Overview screen — R1
Rail selection Overview. Workspace heading Overview and one-line subtitle. Four vertically stacked cards:
A Storage: heading + monochrome disk icon; list actual internal/mounted USB devices, optionally usage/free capacity only if safely measurable. Mounted does not mean source is indexed.
B Network Shares: list configured/canonical network locations, show friendly protocol and reachability only if measured; otherwise 'Status not checked'. Separate connection, saved bookmark and indexed library membership in underlying model.
C Cloud Services: Put.io account state and storage only if available from authenticated API. Do not show unimplemented providers or fabricated quota.
D Library Status: last verified scan, indexed item count if available, unavailable count if available, active scan phase/progress only when measurable. Card links to Library Health but isn't another Library Health menu.
NO Quick Actions bar, fake sample devices, fake dates, fake quota, fabricated scan percentage, or duplicate section menu.
Overview scrolls; focus and position preserved.

## 5. Navigation and D-pad contract
On initial entry focus Overview rail. UP/DOWN rail moves one destination; terminal directions stay stable; RIGHT enters first actionable content; LEFT from workspace returns to selected rail row; OK on rail selects destination. Focus alone must not trigger destructive action or unexpectedly open network resources.
For cards, UP/DOWN move sequentially; RIGHT/OK opens focused row/action; LEFT returns rail when at workspace boundary, otherwise follows contextual focus path. BACK closes modal/keyboard, then child screen, then returns previous category/global view; never discard unsaved form without a clear choice.
Keep fixed rail visible in all normal subflows, including WebDAV form, Put.io authorisation, and All Files browsing. On-screen keyboard/modal may overlay content but underlying rail persists.
Asynchronous scan/listing/Put.io callbacks must not steal focus, reset scroll, or replace unrelated category content.

## 6. Local Storage
Mounted device list with truthful device availability, optional capacity and Browse. Browse uses shared file browser in workspace. Device loss retains safe unavailable state; no implicit library removal. Add Folder to Movies/TV, Save Location and other contextual actions only when supported by the source.

## 7. Network Shares + WebDAV workflow — R2
A. Network Shares list shows real configured network/library/bookmark entries with type and conditional status; Add Network Source is a clear focused action.
B. Protocol picker lists exactly SMB, WebDAV HTTPS, WebDAV HTTP, SFTP, FTP, FTP TLS. No unsupported NFS.
C. WebDAV form contains protocol, server/address, optional port, path, username, password, Save Password, Show Password; SMB Domain only for SMB. Friendly optional display name may be added ONLY if supported or explicitly implemented with persistence. Do not demand username for anonymous WebDAV.
D. Keyboard must permit @ : / . - _ and other required URI/credential punctuation via D-pad, including email-style usernames; secure password masking.
E. Validate syntax locally, then attempt actual remote listing/authentication. 'Test Connection' should only be labelled successful after verified listing/handshake, never merely after form submission.
F. Progress is cancellable only if real cancellation exists. Show honest auth, TLS, timeout, inaccessible path and permission errors with Retry/Edit/Back.
G. Save connection/location only after user confirmation; then offer Browse and optional explicit Add Folder to Movies/TV Library. Saving connection != indexing library. Preserve credentials privacy and secure storage.
H. All Files browsing reuses shared browser. Do not conflate bookmark removal, source removal and media deletion.
NOTE: Audit CS-01/CS-09/L-01 must be resolved or explicitly accounted for before claiming the flow works.

## 8. Cloud Services + Put.io workflow — R3
Only Put.io is source-verified as a cloud account integration. Do not show Google Drive, Dropbox, OneDrive, generic token entry or imaginary account types.
A. Cloud Services shows Put.io Enabled state, Connect/Reconnect/Account when applicable; Add Cloud Service may lead directly to Put.io while no other providers exist.
B. Select Put.io, launch EXISTING Put.io device authorisation: request device code, show real QR/code/link from actual service, wait/poll, handle expiry/cancel/transport failure.
C. Do NOT hardcode storyboard example authorisation URL, code or OAuth scopes. Device authorisation is not an in-TV password or API-token form.
D. On success show actual username/status/quota if returned. Existing account actions must survive: Library folders/Sync Now, Associate Movies, Associate TV Shows, Change Library Folders, Browse Files, Search Files, Account/Connection, Disconnect.
E. Association uses existing folder picker and explicit WebDAV-source mapping; avoid suggesting Put.io files become indexed automatically just by connecting. Retain complete-snapshot reconciliation and safe unlink/disconnect choices.
F. Browse files in shared browser/workspace if technically possible while preserving Put.io capability limits and no unsupported destructive operations. If existing browser cannot support this directly, Codex must document the architectural adaptation before implementation.
G. All loading/expiry/error/retry/cancel states, focus restoration and no credential exposure are required.

## 9. All Files
Use one workspace file browser with breadcrumb, search/filter, list/grid toggle, sort choices and appropriate contextual actions. Preserve existing three browser toolbar controls and six sort choices documented by FD-003, with exact labels verified from source. Support local, network, saved and Put.io paths only to extent supported by provider. Distinguish hidden/video-only/all-files filters; loading/empty/error/retry states. Avoid destructive action on read-only provider.

## 10. Library Health
Keep five existing health categories, real counts and safe source-specific recovery actions. Preserve missing/offline media without mass deletion. Address FD-003 CS-02 focus-entry barrier. Keep health in permanent rail; Overview only summarises health, not duplicate action menu.

## 11. Additional functional coverage that must not disappear
- Saved Locations/bookmarks: available inside appropriate destination or All Files, with Add/Remove Saved Location separate from Add/Remove Library Source.
- Network discovery: SMB and DLNA/UPnP where supported, not fake discovery of FTP/SFTP.
- Network Scanning configuration: Automatic, Frequency (15m/30m/1h/6h/24h), scan on open/return, included sources, last result, manual scan. Distinct from Scan Library. Surface via Overview/Network Shares settings without seventh permanent rail item.
- Exclusions and source membership: explicit, safe, and understandable.
- Scan safety: coalescing, missing-source retention, no duplicate entries, no focus theft, no fake cancel/progress.
- Credential editing and recovery; permission-safe and accessible input.

## 12. Screen/state implementation matrix (minimum)
Overview: loading, populated, empty, partial data, error, scanning.
Local Storage: none, one, multiple, removed/unavailable, browse, permission denied.
Network Shares: empty, populated, discover, protocol picker, WebDAV/SMB/SFTP/FTP forms, keyboard, validation, connecting, success, auth failure, TLS failure, timeout, disconnected, edit, remove.
Cloud Put.io: disabled, disconnected, requesting code, code/QR, pending, expired, failed, connected, reconnect, account actions, associations, disconnect decision.
All Files: root, nested folder, breadcrumb, filter, sort, empty, loading, retry, unsupported action.
Library Health: five categories, empty, populated, actions, background scan changes.

## 13. Acceptance and evidence gate
1. First commit actual R1/R2/R3 PNG files into docs/design/assets/network-files/ with filenames and SHA-256. Until this happens, visual references are conversation-only and exact pixel matching is NOT verifiable from GitHub.
2. Codex should derive actual XML/Compose dimensions/font family and shared token values from existing code; publish a token mapping and screenshot overlay at 1920×1080. Values above are blueprint targets; any necessary deviations require an explicit recorded reason, not guessing.
3. Map every FD-003 control group (92), screen family (28), source-confirmed conditional issue (15), and Shield QA case (67) to preserved/replaced/retired with reason. Do not assume an image storyboard covers them all.
4. Create screen-by-screen screenshots for R1/R2/R3 at key states and compare geometry, monochrome icon treatment, text wrapping and D-pad focus path. No visual 'pass' without side-by-side evidence.
5. Test real SMB/WebDAV (including email-style username), disconnected shares, Put.io device authorisation and association, USB unplug/replug, missing library source, keyboard punctuation, accessibility and focus navigation on Shield.
6. No merge, APK, version assignment, implementation branch changes or claim of physical verification until owner explicitly approves implementation.

## 14. Known unresolved design questions delegated to engineering (do not invent UI)
Actual 0.141 shared browser action inventory and exact sort labels; true device capacity APIs; provider reachability semantics; source display-name persistence; how to fit network scanning and saved locations without loss; Put.io browsing integration and existing device OAuth code/QR behaviour. Codex must investigate and resolve from actual source and report any owner-facing trade-offs before coding.
