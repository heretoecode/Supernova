# Playback, Loading Screen & HUD — Design and Behaviour Authority

Status: **current HUD corrections partly APPROVED; some submenu contents NEED DECISION**

## Loading/preparation screen
Dark/neutral cinematic loading surface. Use cached backdrop and official title/logo artwork where available without remote-network flashing. For TV show only Season • Episode and episode title; avoid clutter/badge rows. “Preparing playback…” may be used as the intentional status.
Current Shield defect: stray internal/debug preparation text must never leak into the user-facing loading surface.

## Primary HUD
Current corrective authority from physical 4.1.7 QA:
**Subtitles · Audio · Play/Pause · More** — exactly four primary controls.
Play/Pause is dead-centre. No fifth primary button.
Remove **Info** from the primary HUD.
Remove **Up → More** shortcut.
Do not link from the playback HUD to full application Settings.

Protected surrounding HUD information: title/logo and episode/title context, clock/end-time where supported, seekbar/timestamps and current track context. Keep controls anchored and visually balanced rather than allowing labels to move the centre transport.

First Back dismisses HUD; second Back exits playback. Remote seeking remains available even though old backward/forward transport buttons are not primary HUD controls.

## Subtitles / Audio
Both should be simplified from the current hierarchy. Direction is approved; exact final submenu hierarchy remains **NEEDS DECISION**. Do not invent a new menu during the fixes pass.

## More
Direction is approved but exact final contents remain **NEEDS DECISION**.
Supernova Settings removal is approved. Playback Speed / Play Mode / Format may remain subject to final decision. “Report a Problem” is not final.
Do not duplicate Audio/Subtitles/Info entries in More when already represented appropriately.

## Technical information
Technical information remains available through an appropriate secondary path and should be compact and tied to the actual physical file. Removing Info from the primary HUD does not authorise deleting technical information entirely.

## State restoration
Shield defect: HUD colour/style state can be lost after leaving and returning. Restore the selected visual state consistently.
Playback return to calling surfaces must restore originating focus where valid.

## Evidence
`docs/preview-4.1.7/REQUIREMENTS.md` playback requirements; Preview 4.1.2 handover/audit; Preview 4.1.4 release notes; `docs/qa/4.1.7-final-shield-qa.md`.

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
