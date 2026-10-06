# Network & Files — Design and Behaviour Authority

Status: **APPROVED/current; three-panel physical correction active**

## Core model
Three-panel mental model:
**Left = Where am I? · Middle = What can I select? · Right = What is it / what can I do with it?**
Use small contextual overlays for finite choices. Full-screen transition only for a genuine workspace such as the filesystem browser.

Left rail exactly:
**Overview · Local Storage · Network Shares · Cloud Services · Saved Locations**.
Advanced does not belong here. Entry focuses Overview. Focus alone never activates/expands middle content. UP/DOWN one rail item; Overview+UP returns to global Network & Files; final+DOWN stays; RIGHT enters first meaningful middle control; LEFT from centre returns to originating category.

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
