# Playback settings redesign — structure decision (1 November 2026)

**Status:** Approved structure; detailed layout and contents still under discussion. **Do not implement yet.** Separate from the active 1 November Codex fix handover.

- Retain one Settings button on the playback HUD.
- Activating it opens one on-screen settings window containing all available playback settings.
- Display the categories in this approved order: **Subtitles → Audio → Video → More**; settings are organised within the single window.
- Support Nvidia Shield remote navigation.
- Preserve all existing setting functionality; this work concerns presentation and organisation.
- Earlier proposal for a standalone Subtitles window opened directly from the HUD is **parked**, not approved for implementation.
- Do not alter the ongoing Codex fixes or include this redesign in that work.

**Next design task:** establish accurate complete inventory and grouping within the single settings window before producing further implementation-ready mock-ups.

## Category presentation (approved)
- Use monochrome category icons, positioned immediately **to the left of** their text labels (horizontal alignment), not above the labels.
- Applies to Subtitles, Audio, Video and More, in that order.

- **Category navigation layout (approved):** one continuous horizontal left-to-right row, in the order Subtitles → Audio → Video → More. Do not arrange categories in a 2×2 grid. Keep each monochrome icon to the left of its label.

## Subtitle section refinements (approved 1 November)
- Subtitle Selection: first row **Subtitles** with a direct On/Off toggle; second row **Subtitle Track** with a selection control.
- Subtitle Appearance: **Outline** and **Background** must each use a direct On/Off toggle, not navigate to a submenu.
- Keep the remaining appearance controls individually visible; retain the four horizontal category tabs and existing overall styling.
- Mock-up values are illustrative, not verified current playback values. Redesign remains parked for implementation until separately approved.

## Text size refinement (approved)
- Subtitle Appearance > **Text Size** displays three inline, directly selectable options on the same row: **Small | Medium | Large**.
- Highlight the currently selected option. Do not open another menu or dialog to change text size.
- Existing underlying fine-grained subtitle sizing functionality must be preserved or explicitly mapped during implementation; the three preset values are a design choice, not verified existing engine values.
- Still design-only, excluded from the active Codex fix batch.

## Subtitle text colour picker (approved)
- Selecting **Text Colour** opens a compact pop-up with a **single horizontal row of preset colour swatches**, not a full settings page or complex colour wheel.
- Selecting a swatch applies that colour directly; no extra confirmation screen. Shield remote Back dismisses the picker and returns to the same Playback Settings window.
- Use existing dark visual styling and blue focus/selection treatment.
- Exact preset palette is **not yet approved**; example colours shown during discussion are illustrative. Preserve existing subtitle colour capability in implementation.
- Design-only; explicitly excluded from ongoing Codex fix batch.
