# Supernova 0.135 — Approved Playback HUD visual specification

Date: 2026-10-10
Status: **USER-APPROVED DESIGN FOR 0.135; PLANNING ONLY — NO CODEX HANDOVER**

## Original image
The user supplied an original Gemini-designed playback HUD reference in the planning conversation on 2026-10-10 (local date). The source image is **not yet committed to the repository**; do not falsely represent a placeholder or other image as the approved reference. Once uploaded, store the exact original image bytes under `docs/design/approved-mockups/playback-hud/` and add its relative link here. This text captures the user's explicit instructions independently of image availability.

## Approved layout and styling
- Seek bar approximately at the reference image's height, across the lower part of the video, with the current seek position/time bubble above the scrubber and total duration on the far right.
- **Four buttons in this exact left-to-right order:** **Play/Pause**, **Subtitles**, **Audio**, **More**. Play/Pause is furthest left; More furthest right.
- The buttons are in a **single horizontally centred row beneath the seek bar**; centre the **entire button group** along the screen's horizontal axis and align button centres on the same vertical axis (consistent Y position).
- Each button is **pill-shaped**, with a **monochrome icon on the left** and the text label on the right.
- The focused button is a bright/white pill with dark icon and text; unfocused buttons use translucent charcoal with light monochrome icon/text. Preserve legibility and TV D-pad focus indication.
- A restrained dark gradient/scrim may sit behind lower HUD controls without overwhelming video.
- Preserve the existing Supernova design language and TV accessibility/focus conventions.
- This is a layout and appearance authority; do not infer unapproved behavioural changes (e.g. seek key semantics) from the image alone.

## Previous checklist ambiguity resolved
The user had deliberately left **“Remove Info and unnecessary Settings links from HUD”** unticked as a reminder to return to HUD design. The subsequent explicit approval of this **exact four-button layout** resolves the top-level control inventory: **Info and direct Settings links are not standalone HUD buttons**. Their underlying functions should remain reachable in appropriate existing menus where needed; do not delete functionality. Mark this resolution as a design-specific supersession of the earlier pending checkbox, not blanket approval of unrelated Settings changes.

## Execution boundary
Document design only. Do **not** start coding, hand off to Codex, merge branches, modify signing, or alter protected release branches without a separate explicit user instruction.

## Related scope
- [User-confirmed 0.135 checklist](../../project/SUPERNOVA_0.135_USER_CHECKLIST_SCOPE_2026-10-09.md)
