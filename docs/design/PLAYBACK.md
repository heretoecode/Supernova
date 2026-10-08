# Playback, Loading Screen & HUD — Design and Behaviour Authority

Status: **APPROVED next-version HUD and submenu authority**

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

## Seeking / timing — protected historical direction
Where supported, retain current time plus predicted end time in the top-right HUD. Historical approved seeking direction included target timestamp/chapter information and favoured a thumbnail preview above the scrub position. **Preview 4.1.7 explicitly deferred playback trick-play thumbnails**, so thumbnail generation/implementation is not current authority or fixes-only scope unless separately promoted later. This does not change the current four-control primary HUD authority.

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


See `docs/project/HISTORY_RECONCILIATION.md` for older HUD concepts that are explicitly superseded by current physical-QA authority.


## Next-version final submenu authority — APPROVED

This section supersedes earlier NEEDS DECISION wording above.

**Primary:** Subtitles · Audio · Play/Pause · More. Exactly four; Play/Pause dead-centre. Rebalance spacing visually if needed without adding a fifth control.

**Subtitles:** Off/available tracks · Download Subtitles · Sync · Appearance. Appearance remains in the HUD so the user can see changes live. Retain Style/colour, Vertical Position, Outline subtitles, Subtitle background and Background opacity. Global defaults/provider/preferred language remain in full Settings.

**Subtitle defect:** changing subtitle tracks must not visibly jump the video forward then snap back. Fix the root cause; no seek/restart/rebind/surface recreation or visible frame disturbance from a track change.

**Audio:** available tracks · Audio Sync · Audio Boost · Night Mode. Persistent defaults remain in Settings.

**More:** flat compact menu: Playback Speed · Play Mode · Format. Remove Report a Problem and unnecessary section headings. Format is picture/aspect presentation (Original, Full Screen, Stretch, 4:3, 16:9, etc.), not technical metadata.

Physical Shield QA has accepted persistent HUD accent/focus colouring and absence of internal/debug preparation text in the tested candidate; protect both against regression.

### Shield QA and playback behaviour decisions — 8 October 2026
- User's physical Shield Play Mode screenshot confirms modes Single, Folder, Repeat single, Repeat folder, Binge watching; beneath is the legacy global `Skip intro/outro` toggle and raw segment timestamps. **Approved direction:** rename the playback toggle **Segment Skipping** and remove raw per-segment timestamps from user-facing Play Mode; keep timestamps in developer diagnostics.
- **Approved direction:** Play Mode's Segment Skipping switch is a temporary current-playback-session override of the global Settings > Integrations > Segment Skipping master. Never persist its value as a global preference. Proposed scope: survives automatic next-episode advancement in the same binge session; resets at a fresh manually initiated playback session. Exact session-lifetime semantics to confirm before implementation.
- **Approved:** Up Next disabled means current episode plays naturally, does not auto-start the next, and returns to the episode Details page when playback ends. **Proposed, pending user confirmation:** Back while Up Next countdown visible dismisses and cancels that episode's automatic transition, rather than hiding panel while countdown continues. Preserve post-credit safeguards.
- Binge Watching and Segment Skipping are independent concepts: former controls next-episode progression; latter controls segment behaviour. No application code changes from this documentation.

### Confirmed five-second Skip pill interaction — 8 October 2026
- **Approved:** For Intro/Recap/Outro/Credits/Preview when policy selects `Show Skip button` (or a safety fallback offers an optional action), show a focusable Skip pill for exactly five seconds. Selecting/pressing it seeks to the verified safe end of the specific segment. If ignored, pill disappears automatically after five seconds and playback continues unchanged. No Cancel/Close/Dismiss button and no user-operated hide action; do not auto-skip on expiry. Avoid focus theft; do not let countdown trigger a seek.
- **Separate behaviour:** Up Next's five-second countdown is an episode-advance prompt and, when safely permitted, expiry **does** start the next episode. Do not confuse Up Next with optional segment Skip pills. Prior speculative Back-to-dismiss requirements for Skip pills are superseded. Up Next Back behaviour was not explicitly resolved by this clarification; do not assume a newly approved cancellation control.
- Temporary session-level Segment Skipping override remains the proposed policy, pending explicit confirmation of exact lifetime across binge episode transitions.

### Confirmed temporary Segment Skipping override lifetime — 8 October 2026
- User **approved**: changing Segment Skipping On/Off from the playback Play Mode menu overrides the global master setting for the **current uninterrupted binge-watching session**, and retains that override through automatic next-episode transitions within the same binge session. It must **not** change the persisted global Settings > Integrations > Segment Skipping preference or per-type choices.
- On ending that playback session and starting a new independent session, discard the override and reapply the saved global setting. Ensure no unintended reset during ordinary intra-session episode transitions, pause/resume or transient player lifecycle changes; distinguish a genuine new playback session from an automatic binge transition.
- This is an approved design decision, not an application code change.
