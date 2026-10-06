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
