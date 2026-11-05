# FD-003 — Network & Files visual and interaction blueprint (draft v2 — expanded implementation specification)
Date: 2026-10-23
Status: OWNER-APPROVED VISUAL DIRECTION; EXPANDED SPECIFICATION — ASSET/PARITY VALIDATION PENDING; NOT IMPLEMENTATION AUTHORISATION
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
IMPORTANT: Original PNGs are NOW COMMITTED under docs/design/assets/network-files/; see section 20 and the asset README for actual filenames, byte sizes and Git blob hashes. The generation IDs remain provenance references, not file paths. The mock-up names/URLs/IPs/capacities/statuses are placeholders. R3 depicts Google Drive/Dropbox/OneDrive and manual API token; these are NOT authorised features. R2's 'Test Connection' and R3's simplified authorisation screens are aspirational; validate exact backend flow and don't invent API semantics.

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
1. COMPLETED: R1/R2/R3 original PNGs uploaded and verified in docs/design/assets/network-files/; see the asset README for exact paths, sizes and Git blob hashes. Before pixel-perfect implementation, Codex must inspect images and compare against Android captures.
2. Codex should derive actual XML/Compose dimensions/font family and shared token values from existing code; publish a token mapping and screenshot overlay at 1920×1080. Values above are blueprint targets; any necessary deviations require an explicit recorded reason, not guessing.
3. Map every FD-003 control group (92), screen family (28), source-confirmed conditional issue (15), and Shield QA case (67) to preserved/replaced/retired with reason. Do not assume an image storyboard covers them all.
4. Create screen-by-screen screenshots for R1/R2/R3 at key states and compare geometry, monochrome icon treatment, text wrapping and D-pad focus path. No visual 'pass' without side-by-side evidence.
5. Test real SMB/WebDAV (including email-style username), disconnected shares, Put.io device authorisation and association, USB unplug/replug, missing library source, keyboard punctuation, accessibility and focus navigation on Shield.
6. No merge, APK, version assignment, implementation branch changes or claim of physical verification until owner explicitly approves implementation.

## 14. Known unresolved design questions delegated to engineering (do not invent UI)
Actual 0.141 shared browser action inventory and exact sort labels; true device capacity APIs; provider reachability semantics; source display-name persistence; how to fit network scanning and saved locations without loss; Put.io browsing integration and existing device OAuth code/QR behaviour. Codex must investigate and resolve from actual source and report any owner-facing trade-offs before coding.

## 15. Blueprint v2 — implementation geometry, no interpretation allowed

### 15.1 Reference resolution and measurement
Use 1920×1080 as the canonical screenshot coordinate system, with a content-safe inset of 32px on all sides and existing global top navigation retained. Do not stretch or crop R1–R3 to infer exact pixel values: generated images are *concept art*, not measured Android screenshots. For geometry conflicts the explicit numbers in this blueprint win; for font/brand conflicts the delivered app's approved global visual system wins. Record the resolved source resource ID for every token in the implementation mapping.

| Token | Canonical 1080p target | Rule |
|---|---:|---|
| Horizontal content safe inset | 32px | Respect platform overscan/safe inset if larger |
| Top global navigation reservation | Existing global component | Do not replace with mock-up's header |
| Rail-to-content gap | 20px | Constant |
| Rail width | 352px | Fixed at 1080p; clamp 320–400px with responsive scaling |
| Main content width | Available width minus rail and gap | No fixed third column |
| Outer workspace padding | 20px | All four sides |
| Main heading baseline | 48px below workspace top | Existing Supernova typeface |
| Main heading size | 40sp nominal | Source-token override only with recorded mapping |
| Main subtitle size | 21sp nominal | Two lines maximum |
| Rail label | 25sp nominal | One line with ellipsis |
| Rail row height | 76px | Six rows + 8px gap |
| Rail icon | 30px | Monochrome stroke, transparent canvas |
| Rail icon-to-label gap | 20px | Horizontal |
| Card top-level vertical gap | 14px | Consistent |
| Card radius | 18px | No coloured icon backing |
| Card outer horizontal padding | 22px | Both sides |
| Card heading | 27sp nominal | White |
| Card subtitle | 20sp nominal | Muted |
| Data row minimum height | 66px | Expand for two-line paths |
| Row icon | 28px | Monochrome |
| Row status dot | 10px | Only if genuinely measured |
| Row metadata | 18sp nominal | No ellipsis on error meaning |
| Focus outline | 2px nominal | Use existing Supernova blue and glow |
| Scrollbar | Existing system component | No permanent intrusive bar |

Font-family rule: obtain the exact resource/typeface used by the current Supernova top navigation and approved utility screens, and reuse it; no inferred Avenir/Roboto substitute. Text is in Android sp; layout coordinates are density-independent design targets, adjusted for Android TV density and font scale. Do not implement fixed-height text containers that clip at accessibility font scales.

### 15.2 Workspace placement and content density
At 1920×1080, left rail outer x=32, width=352; workspace x=404 and right edge=1888. Workspace starts at the content origin below the *actual* global nav. Both containers fill available content height; only the workspace content scrolls. Overview cards span available workspace width; section headers and card text align on a common 20px inset. Keep card title and description on left, chevron at right, with no button-shaped background behind header icon. A card's own list rows are vertically contiguous and separated by subtle 1px dividers. When many devices/sources exist, extend card vertically and scroll the workspace; never arbitrarily truncate the underlying dataset.

### 15.3 Colour and icon token rules
No hardcoded new blue: resolve accent/focus colours from the current approved Supernova resources. Surfaces: use Space Black Blend and existing translucent dark card/rail tokens; avoid blur layers per card. White/grey icon strokes only (same colour in normal and focused states); semantic dots can be green, amber or red, but icons themselves remain monochrome. Monochrome applies to icons in **all six sections, forms, modal actions, folder rows, provider lists, error and success states**. Brand logos, including Put.io, are replaced with a monochrome representation or a text label; no bright brand logos unless owner separately approves. No icon-background circles, squares or tiles, including on empty-state illustrations.

## 16. Screen-level component blueprint and interaction IDs

Each identifier is a stable test/implementation reference. Never use the mock-up's fake example values as application data.

### S01 Overview
- N01–N06 rail rows: Overview, Local Storage, Network Shares, Cloud Services, All Files, Library Health.
- O01 page title and O02 subtitle; O10 Storage card, O20 Network Shares card, O30 Cloud Services card, O40 Library Status card.
- O11 device row: icon, name, optional safe path, optional measured capacity bar and exact units, optional measured status. O12 card header action opens Local Storage.
- O21 source row: friendly name, protocol, path when not sensitive, status if checked; O22 card header action opens Network Shares.
- O31 Put.io account row: state; optional actual account storage; O32 card header opens Cloud Services. No other providers without source support.
- O41 last scan; O42 indexed items; O43 missing/unavailable; O44 current scanning state; O45 header opens Library Health.
- Overview card header and each item row are separately focusable **only if they have distinct destinations**. If identical, make just the item rows focusable and header a non-focusable label, to avoid duplicate stops.
- Empty state per card: 'No devices detected', 'No network locations saved', 'Put.io not connected', or 'No scan information yet' only when supported by real state; include a single contextual action where appropriate.
- Data changes while visible update text in place without resetting focus, scroll or selected rail row.

### S02 Local Storage
- L01 page title, L02 device list, L03 device row, L04 browse action, L05 unavailable state, L06 permission-recovery action.
- Clicking a volume opens S05 browser with current volume root; navigating back restores the exact volume row and scroll.
- Never show 'connected' if Android reports only a stale mount. No fake USB capacity, no automatic scan/index on browse.

### S03 Network Shares
- W01 page title; W02 saved/configured location list; W03 Add Network Source; W04 Discover Devices; W05 Network Scanning settings entry; W06 source row.
- W06 context actions: Browse, Add to Library when not already indexed, Scan Source when indexed and available, Remove from Library when indexed, Save Location/Remove Saved Location when bookmarked. Each action has a confirmation/success/error state and distinct effects.
- If a connection is both bookmarked and indexed, display its badges/state separately, not as duplicate list entries. Preserve canonical URI identity.
- W07 empty state includes W03 and optional W04. Discovery only exposes actual SMB and DLNA/UPnP results. FTP/SFTP are entered manually.
- W08–W13 network scan configuration: Automatic On/Off; Frequency 15m/30m/1h/6h/24h; Scan on Open/Return; Included Sources; Last Scan/Result; Scan Now. Network scanning must remain separate from Library scanning.

### S04 Add Network Source and WebDAV
- C01 protocol picker, six exact rows in this order: SMB, WebDAV HTTPS, WebDAV HTTP, SFTP, FTP, FTP TLS.
- C02 form: protocol (read-only summary + Change), server/address, optional port, path, username, password, Save Password, Show Password; conditional SMB Domain. The display-name field C02a is optional only after verified persistence implementation; never silently save a name in a nonpersistent UI.
- C03 focusable Test Connection; C04 Back; C05 Save, **enabled only after an actual verified connection test if that capability has been implemented**, otherwise use existing truthful Connect/Browse wording and explain the save semantics.
- C06 validation error immediately beneath relevant field (invalid port, missing host, malformed scheme, illegal path). Error text not colour-only.
- C07 progress screen with phase and cancellation only if backend supports cancellation; C08 success with actual host/path and Browse/Save; C09 failure with cause, Edit, Retry, Back.
- C10 text keyboard, C11 password keyboard: focus and punctuation support including email username; preserve text on Back; do not display secret in screenshots/logs.
- WebDAV HTTP must be visibly distinguishable from HTTPS; warn about insecure transport where applicable, but do not invent a TLS validation bypass.
- Connection test must verify an actual remote operation; mere URI parsing or listing launch is not success.

### S05 All Files / shared browser
- F01 source selector/roots (local, saved, network, Put.io only where actually supported); F02 breadcrumb trail; F03 toolbar; F04 file/folder list; F05 selected item contextual actions; F06 empty state; F07 loading; F08 error and Retry; F09 filter chooser; F10 sort chooser; F11 list/grid chooser.
- Toolbar must preserve **all three existing controls** and the sort chooser **all six existing choices**; labels and exact order must be source-verified in implementation inventory, not guessed here.
- Folder OK navigates deeper. Back returns to parent until source root, then section. On folder selection, show Add Folder to Movies Library / Add Folder to TV Shows Library / Add to Saved Locations only when source capabilities allow. File action capability gating is mandatory.
- Maintain breadcrumbs and position after list refresh; error/empty states retain focusable Back or Retry.

### S06 Cloud Services / Put.io
- P01 page title; P02 Put.io service row; P03 enabled toggle; P04 Connect/Reconnect/Account entry; P05 auth code request; P06 QR/code/link reader; P07 waiting/polling; P08 expired/error with Retry; P09 connected account summary; P10 browse; P11 disconnect decision.
- P12–P19 preserve eight current account actions: Library folders / Sync Now; Associate Movies; Associate TV Shows; Change Library Folders; Browse Files; Search Files; Account / Connection; Disconnect.
- P20 association folder picker (Use This Folder, Back to Root, folder rows, Next Page when present); P21 WebDAV source selector; P22 Save & Continue/Cancel; P23 ownership/reassignment decision.
- Authorisation code and link are obtained live; do not use screenshot placeholders. Reconnect should preserve safe inactive mappings; disconnect choices must preserve existing data safety semantics.
- **Do not implement** Google Drive, Dropbox, OneDrive, arbitrary provider picker, manual API token or fictitious Put.io quota values from R3.

### S07 Library Health
- H01 title; H02 existing five health category rows; H03 counts and details; H04 contextual recovery actions; H05 safe confirmation; H06 success/failure; H07 empty state.
- The five actual category names and exact actions must be copied from the 0.141 source audit, not inferred from screenshot. Focus on a category must reach its first actual focusable action; fix CS-02 non-focusable heading barrier.
- Missing/offline media remain retained. No bulk delete action invented.

## 17. Remote focus graph — mandatory tests

| From | Input | Expected |
|---|---|---|
| Global Network & Files | OK | Open S01 with N01 focused |
| N01–N06 | DOWN/UP | Move one rail row; boundary stays on last/first |
| Rail item | OK | Switch section, keep rail visible, load corresponding Sxx |
| Rail item | RIGHT | Focus first meaningful action in workspace |
| Workspace first item | LEFT | Return to active rail item |
| Workspace card/row | UP/DOWN | Move nearest logical focusable row without skipping |
| Card header with no action | OK | No action; header is not focusable |
| W03 Add Network Source | OK | Open C01, focus first protocol |
| C01 WebDAV HTTPS | OK | Open C02, focus first required input |
| C02 field | OK | Open keyboard; Done returns to same field/next logical field |
| C02 form | BACK | Return to W03, with unsaved-change prompt if dirty |
| C03 Test Connection | OK | C07; success C08 or failure C09 |
| P04 Connect | OK | P05 then P06/P07; pending focus on Cancel only if cancellable |
| P08 auth expired | OK Retry | Request a new code; do not reuse expired code |
| S05 folder | OK | Open folder, preserve breadcrumb history |
| S05 nested folder | BACK | Return parent and restore highlighted folder |
| Any modal | BACK | Close modal, restore invoking control |
| Async data update | No user input | Never change focused control or selected section |
| Unplugged drive | Any | Retain stable focus or move to nearest valid row with announcement |

No focus traps; no invisible focus targets; no focus reset from background network callbacks; focus highlight blue only on actual focused item.

## 18. Form field and error-state matrix

| Field | Required | Validation | Input |
|---|---|---|---|
| Protocol | Yes | One of six supported | D-pad list |
| Host/server | Yes | Nonempty, valid host/IP; protocol-aware | Keyboard |
| Port | No | Integer 1–65535 | Numeric keyboard |
| Path | No | Normalised safely, do not erase valid encoded characters | Keyboard incl / % ? |
| Username | Protocol-dependent | Allow @ and email-style names; don't SMB-split non-SMB | Keyboard |
| Password | Protocol-dependent | Preserve whitespace, mask by default | Secret keyboard |
| SMB Domain | SMB only | Optional domain format | Keyboard |
| Save Password | Optional | Persist only through secure existing mechanism | Toggle |
| Show Password | Optional | Temporary reveal, default off | Toggle |

Errors: bad host, invalid port, bad credentials, TLS trust failure, remote 403/404, timeout, DNS failure, server unreachable, no listing permission, cancelled attempt, stale response after category change, offline saved source. Show user-friendly cause and recovery action; no generic 'Connected' when verification did not occur.

## 19. Source parity, non-regression and QA sign-off

The audit enumerates 92 control groups, 28 state families, 15 source-confirmed conditional defects and 67 Shield QA cases. A complete **parity ledger** must be generated by Codex from the audit, one row per item, with ID, baseline screen, new Sxx destination, status (preserved/moved/changed/retired), test case, source reference, and owner approval if intentionally retired. Counts alone are NOT a substitute for the ledger. This document does not assert that those 92 individual entries have already been mapped.

A screenshot set must include S01 Overview; S02 local populated/empty; S03 network populated/empty; S04 protocol/form/keyboard/progress/success/failure; S05 root/folder/filter/sort/empty/error; S06 Put.io disconnected/code/pending/connected/account/association/error; S07 health populated/empty/action. Capture 1920×1080 with consistent sample fixtures, masking secrets, and provide overlays against actual R1–R3 source PNGs when available. Verify typography, focus bounds, spacing, row truncation, scroll, status truthfulness and monochrome icon treatment.

For acceptance: (1) source parity ledger complete, (2) all actionable FD-003 findings addressed or individually risk-accepted, (3) automated navigation tests, (4) real SMB/WebDAV including email-style usernames, (5) Put.io device code/association, (6) mounted/unmounted USB, (7) background scanning/focus stability, (8) owner Shield screenshots and remote tests. No claims of visual parity or implementation readiness until evidence exists.

## 20. Reference asset delivery status — VERIFIED UPLOADED
All three original PNGs have been uploaded by the owner and verified on this documentation branch. See [asset README](assets/network-files/README.md) for exact files, byte sizes and Git blob hashes.

- R1 Overview: [PNG](assets/network-files/2C2FCA2A-7A91-487A-8837-DD106F93AAD2.png)
- R2 WebDAV: [PNG](assets/network-files/75E72DD0-4878-4FF8-A4EE-8448F228A48E.png)
- R3 Put.io: [PNG](assets/network-files/52D674B6-1502-4657-A583-551556A51FE5.png)

Visual references are now available to Codex in GitHub; source capability audit, token mapping, parity ledger and Shield verification remain outstanding. This documentation update is not implementation authorisation.
