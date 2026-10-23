# FD-006 — Edit Library opacity and readability correction

**Status:** SCHEDULED FOR NEXT SUPERNOVA VERSION (owner decision 2026-10-23).
**Target:** Next release after delivered 0.141 (expected 0.142, subject to confirmation).
**Coding authorisation:** Not granted by this roadmap note; no code changes or APK.

## Owner-reported issue
In the Edit Library editor, Home screen artwork, posters and underlying content show through the editing panels. This significantly reduces text legibility, visual separation and focus clarity. Evidence: owner-supplied Shield TV photograph on 2026-10-23 showing Library Page, Page Details and Live Preview over the underlying Home screen.

## Approved correction direction
- Retain the existing editor structure, steps and working controls; do not treat this as approval for a full editor redesign.
- Make the editor's overall backdrop approximately 95–98% opaque Space Black; individual panel surfaces approximately 90–95% opaque dark tones as provisional visual targets, tuned against actual Shield screenshots.
- Heavily dim or blur background artwork only where supported without performance regression; opacity/contrast takes precedence over expensive blur.
- Solid white primary text, muted but readable secondary text, current Supernova blue focus treatment.
- Keep Live Preview clearly distinguishable from the editing controls, without visually competing with background Home content.
- Apply the readability fix consistently across Page Details, Content Type, Filters, Display Options and Review, including dialogs, keyboard and focus states.
- Verify no text, buttons or focus outlines become obscured, and maintain D-pad navigation and editor behaviour.

## Acceptance
Capture before/after 1080p Shield screenshots of all five steps with a visually busy Home screen behind; inspect contrast, selected and unfocused controls, Live Preview and navigation. Record any necessary opacity deviations with reasons. No fake content, no lost controls, no navigation regression.

## Release coordination
Schedule alongside FD-005 Network & Files redesign, but track, implement and test separately. Preserve delivered 0.141 and do not merge to main or start the next release until explicitly authorised.
