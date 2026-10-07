# Network & Files — Design and Behaviour Authority

Status: **APPROVED FUTURE DESIGN DIRECTION — reconciled 7 October 2026**

## Core model
Three-panel mental model:
**Left = Where am I? · Middle = What can I select? · Right = What is it / what can I do with it?**
Use small contextual overlays for finite choices. Full-screen transition only for a genuine workspace such as the filesystem browser.

Historical 4.1.7 left rail (SUPERSEDED by the approved 7 October direction below):
**Overview · Local Storage · Network Shares · Cloud Services · Saved Locations**.
Do not implement that historical list as current authority. The approved rail is **Local Storage · Attached Storage · Network Sources · Saved Locations · put.io · Downloads · Library Health**. Focus alone never activates content; D-pad movement and exact focus restoration follow the approved navigation rules below.

## Overview / scanning
Scan Library and Network Scanning are separate.
Network Scanning context includes Automatic On/Off, Frequency, Scan when Supernova opens/returns, Sources Included, Last Scan/Result and Scan Network Sources Now. No Configure Network Scanning button.
Frequency choices exactly 15m, 30m, 1h, 6h, 24h. Finite choices use small anchored tick overlay; Sources Included may use larger multi-select.
Live scan state is honest: phase/source, measurable progress only when measurable, processed/new/updated/elapsed; otherwise indeterminate. No fake Cancel.

Manual network scan must use the same reliable discovery/reconciliation semantics as startup/resume and provide useful start/completion/failure feedback.

## Sources and Saved Locations
Library Sources are indexed network folders/media: Browse/Open, Scan Source, Remove from Library (never delete media).
Saved Locations are bookmarks: Browse/Open, Add to Library, Remove Saved Location. Shared browser “Add to Saved Locations” populates this list.

## Network Shares
Flatten source→actions rather than deep nested menus. Focusing a source updates right context immediately with friendly path/protocol and actions. No right-panel Back button; remote LEFT returns.
Connection is distinct from library source. Add Network Source establishes a connection, then browse/select folder and add to Movies/TV.
Protocols: SMB, WebDAV HTTPS, WebDAV HTTP, SFTP, FTP, FTP TLS. Do not add NFS without genuine support. Never expose internal SMBJ/SSHJ names.
Credentials: protocol, server/address, optional valid port 1–65535, path, username/password, save/show password, SMB domain where needed; WebDAV may allow anonymous empty user.
Discovery: SMB computers/NAS and DLNA/UPnP; FTP/SFTP direct, not discovery.

## Local Storage/shared browser
Middle lists volumes; right shows capacity/status/location + Browse. Shared browser is reused for local, network, saved and put.io contexts. Actions are capability-driven; never show destructive operations unsupported by a read-only source.
Folder actions include Add Folder to Movies Library, Add Folder to TV Shows Library and Add to Saved Locations.

## Scanning safety invariants
Scanning work must preserve mature-library safety: one scan at a time with overlapping triggers coalesced; temporary NAS/share unavailability must not become mass deletion; no temporary duplicates; cached artwork remains visible during refresh; background updates must not steal D-pad focus/reset scroll; interrupted scans recover safely.

## Superseded visual foundation
An older approved Network & Files design used a compact source/location tile landing of roughly four columns by two rows with no permanent sidebar. That design is historical lineage only and is **SUPERSEDED** by the current three-panel model above.

## Current correction
Shield QA says Network & Files still needs the intended three-panel structure/visual correction. Preserve the semantics above while correcting presentation; do not regress into the classic browser.

## Evidence
`docs/preview-4.1.7/REQUIREMENTS.md` UI-050–057; Preview 4.1.4 release notes; 4.1.2 handover/audit; current Shield QA.

## Historical approved visual references recovered from Project Library
A legacy `NOVA_DESIGN_REFERENCES` record dated 16 September 2026 was recovered during the preservation pass. It is historical evidence; later written 4.1.7 decisions override conflicts. It records these mock-up identifiers so the visual lineage is not lost:
- List View + Genre/Sort/Order: `bb6ed615-ca35-4578-88c5-5855ad766c4d`
- Broad UI states: `9f0856cb-2d33-49af-999d-734853aea861`
- Primary Details: `db06b3cb-2136-4b1c-a1b9-69b01db3b80c`
- Network compact Option 3: `bb06fd93-a13b-49b3-8525-97eadce38c3b`
- Network no-divider/cyan-nav: `063d1a2d-4380-4db3-8251-6025c32bfb8a`
- Custom red-ribbon concept: `8703e387-1c95-4752-a284-7f988338f811`
- Playback current/end-time HUD: `7435ef99-a0f1-443d-b416-e2c44364e07c`
- Playback tighter-bottom: `4287b280-80d5-4023-b6f1-e4b47b57cf29`
- Playback compact controls: `b483b883-3a0e-4042-8525-9d7cb4778d26`
- Seeking thumbnail: `f36ed05a-13e5-4183-bf3d-e4d898240cdb`

The same historical record explicitly rejected permanent left-sidebar primary navigation, two-line grid titles, empty Continue Watching placeholder panels, huge bright-blue list rows, grey stock Android filter/sort/order dialogs, “NEW” Settings badges, invented analytics settings, an assistant-invented launcher icon, and generated Network/Settings mock-ups that invented unsupported features.

**Asset status:** the identifier/provenance record is recovered; the corresponding original generated image bytes are not currently available as a complete recoverable image set. Identifiers are not substitutes for images. If image bytes are recovered later they should be committed under `docs/design/assets/` with a manifest and status.


See `docs/project/HISTORY_RECONCILIATION.md` for the older Network foundation and supersession context.


## Next-version physical geometry — APPROVED

Preserve all three-panel semantics above. Define usable vertical space from the **bottom edge of top-navigation text** to the **bottom edge of the TV/app viewport** and vertically centre the complete panel group within it, with equal remaining space above and below. Every panel bottom must remain visible. Physical Shield QA shows Network & Files needs a substantially larger downward/fit correction than Settings. Do not solve this by reverting to the classic/unstructured browser.


## Approved Network & Files design direction — 7 October 2026

Status: **APPROVED DESIGN DIRECTION / FUTURE IMPLEMENTATION**. This does not expand the active fixes candidate and is not Shield acceptance.

### Main composition
- No redesign of the global Supernova top navigation is implied by the mock-ups; Network & Files content begins beneath the existing app header.
- Use a **three-panel TV layout**, vertically centred in the usable viewport beneath the existing top navigation. The panels must not hug the top of the screen.
- Left = section/navigation, centre = selected section contents, right = contextual information/actions.
- Keep the panels visually structured with restrained outlines/translucency.
- Left navigation uses an outer panel plus thin horizontal separators between options; do not turn every entry into a heavy standalone box.
- Current focus uses the established Supernova blue/cyan outline/fill treatment.
- Right-panel actions should remain restrained rather than mobile/desktop-style oversized controls.
- Exact D-pad focus restoration is required when returning from child states.

### Approved left navigation
1. Local Storage
2. Attached Storage
3. Network Sources
4. Saved Locations
5. put.io
6. Downloads
7. Library Health

Overview is removed from the approved direction.
USB Storage becomes the broader Attached Storage.
Generic WebDAV is not a top-level left entry; it is a network-source protocol/type.
put.io has its own left entry for now.
Google Drive, OneDrive and Dropbox placeholders are removed.

Downloads and Library Health may show restrained count/status badges when relevant.

### Submenu/state model
The same three-panel grammar is retained instead of inventing separate page structures.

**Local / Attached Storage**
- Centre: drives, folders and files.
- Right: item information plus Browse/Open, Add/Remove from Library, Scan and appropriate storage actions.

**Network Sources**
- Centre: configured sources plus Add Network Source.
- Supported source/protocol presentation may include SMB, WebDAV HTTP(S), SFTP, FTP/FTPS and DLNA/UPnP as appropriate to actual implementation.
- Right: connection state, protocol/address, library status, last scan and Browse / Scan / Edit / Remove actions.
- No legacy floating QuickAction/context-menu interaction as the primary UX.

**Saved Locations**
- Centre: saved browsing shortcuts.
- Right: location details plus Browse, Add to Library and Remove Shortcut where appropriate.

**put.io**
- Centre: account/folder browsing and service state.
- Right: relevant Browse/Play/Add to Library actions; future Download action may originate here.
- Include coherent disconnected/sign-in/error states.
- Keep account/service integration conceptually distinct from generic WebDAV transport.

**Downloads**
- Centre: Active / Completed / Failed states, transfer rows and progress.
- Right: contextual transfer information/actions such as Pause/Resume when reliable, Cancel, Retry, Open/View in Folder and relevant download settings.
- Preserve the generic WebDAV download-engine direction; put.io is an initial supported/tested source rather than a hard-coded transfer engine.

**Library Health**
- Centre: actionable issue categories and counts, initially including Unmatched Media, possible incorrect/stale matches, unavailable files/sources and source/authentication problems.
- Right: explanation and actions for the selected category.
- Healthy/zero-issue state should be intentionally reassuring rather than an empty panel.
- Temporary metadata/artwork failures that Supernova can recover itself should not become user-facing health noise.

**Library Health → Unmatched Media**
- Centre: affected files with filename/path, size/date and useful inferred evidence.
- Right: Find a Match and appropriate classification/exclusion/location actions.
- Manual matching must reconcile catalogue metadata/artwork/provider state while preserving physical file identity and playback/watch state.

### Home awareness
When actionable Library Health items exist, Home may show a small **non-focusable** informational message telling the user that media needs attention and to review Network & Files.
It must never enter the D-pad focus graph or alter Home row navigation.
Network & Files / Library Health may carry restrained attention counts/dots.
Principle: **Home informs; Network & Files diagnoses; Library Health resolves.**

### Navigation rules
- Left moves back toward the section/navigation panel.
- Right progresses toward content/contextual actions where applicable.
- Back reverses one logical level.
- Returning from a child screen restores the exact launching item/focus.
- No modal should steal focus merely because Library Health has outstanding issues.

### Approved visual references
Two 7 October 2026 generated mock-ups were approved in discussion:
- **Main centred Network & Files layout** — image generation id ff dde75e-d0dc-44fe-8623-e6fe54b5eda1 (without the space: ffdde75e-d0dc-44fe-8623-e6fe54b5eda1).
- **Network Sources / Downloads / Library Health / Unmatched Media submenu storyboard** — image generation id 2a938f81-9585-4dca-b5b1-1f4268ed4a63.

These references are visual authority for composition, hierarchy, spacing and submenu grammar; written requirements remain functional authority.


### Repository image files
- Main approved Network & Files mock-up: [network-files-main-approved.png](references/network-files-main-approved.png)
- Approved submenu storyboard: [network-files-submenus-approved.png](references/network-files-submenus-approved.png)

These repository files are the durable visual references available to Codex. The written requirements in this document remain functional authority.

## Delegated functional decisions — 8 October 2026
The user delegates remaining Library Health and Downloads operational UX details to implementation judgement, within the already approved Network & Files design. Library Health should surface actionable persistent problems (including unmatched media), not transient failures recoverable automatically; retain file/watch identity through correction and avoid intrusive focus-stealing alerts. Downloads should present status, progress, destination, completion/failure, retry and cancellation as appropriate, with predictable D-pad/Back behaviour and safe handling of partial transfers; maintain separation between put.io account integration and WebDAV transport. These are future enhancement decisions, not authorisation to expand the active fixes-only candidate. Preserve the approved navigation and mockups.
