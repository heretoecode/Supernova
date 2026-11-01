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

## Background opacity refinement (approved)
- **Background Opacity** is controlled directly in its existing row, with inline presets **0%, 25%, 50%, 75%, 100%** and a blue selected state; no submenu.
- These are proposed UI presets; implementation must map them safely to the existing 0–255 opacity control and preserve current settings appropriately.
- This remains design-only and is excluded from the active Codex fix batch.

## Inline selector styling (approved)
- Text Size, Vertical Position and Background Opacity options appear as **plain inline text**, not pill/button-shaped controls.
- Separate choices subtly; the selected value is blue, others neutral. Maintain clear individual D-pad focus indication and accessible selection targets even without visible button chrome.
- Do not change their approved option sets or move them to submenus.
- Design only; excluded from active Codex fixes.

## Row spacing refinement (proposed mock-up, awaiting confirmation)
- Increase the vertical height/padding of each individual settings row to reduce the condensed appearance, keeping existing labels, controls, icons and overall structure unchanged.
- Maintain comfortable Shield remote focus targets and visual separation between rows.
- Do not implement until the user approves the updated mock-up.

## Subtitle download action — missing from mock-up (proposed, not yet approved)
- Add **Download Subtitles** immediately beneath Subtitle Track in Subtitle Selection; selecting it should invoke the existing subtitle search/download workflow, not invent a new downloader.
- Verify exact current playback entry point and any other missing in-player subtitle actions before marking the Subtitles category complete.
- The updated mock-up is a proposal only; no implementation authorised and still excluded from current Codex fixes.

## Design fidelity and implementation acceptance (planning; no implementation authorised)
- Once user approves the final mock-up, preserve the exact image in a dedicated approved-reference folder and link its verified repository path here. A chat image alone is not a verified GitHub asset.
- Supply Codex with both the visual reference and this written behaviour specification; do not treat illustrative values in the image as app defaults.
- Acceptance requires matching layout, typography, icons, spacing, colours, selection/focus states, inline controls, and working Shield D-pad/Back navigation.
- Require screenshots from actual implementation compared against the approved reference, documented differences and corrections, plus functional/regression tests. A passing build alone is insufficient.
- Preserve existing functionality and audit current subtitle track/download integration before implementation.
- **Not yet approved for implementation.** Keep separate from the active Codex fix batch.

## Subtitle delay (approved design addition)
- Add **Subtitle Delay** as a directly adjustable inline row in the Subtitles section, with **− / current offset in milliseconds / +** controls.
- Each minus/plus activation adjusts the delay by **100 ms** as observed by the user in Supernova 0.148; no submenu or dialog.
- Display signed offset clearly, including zero; preserve the existing subtitle timing behaviour and remote focus/navigation.
- Still design-only and excluded from the active Codex fix handover.

## Subtitle language flags (proposed visual refinement)
- Show a language flag alongside the CC badge and language name in Subtitle Track and Download Subtitles lists, and alongside the currently selected track where space permits.
- Flags are supplemental, not substitutes for language names or accessibility/SDH labels. Language-to-country flags are not universally one-to-one; use a neutral language/globe fallback when country/region is unknown or ambiguous. Do not invent a country from language alone.
- Preserve list selection and download actions. Design proposal awaiting user approval, not included in active Codex work.

## Subtitle design review — 1 November 2026
- User expressed satisfaction with the simplified layout without inner group boxes, retaining section headings and horizontal row separators. Treat this as the preferred visual direction.
- Subtitle category design is ready for final approval/asset preservation, subject to confirming no remaining 0.148 in-player options are missing. Functional implementation, source parity, remote navigation and Shield QA are **not** complete.
- Do not claim the latest mock-up is uploaded to GitHub until the asset is verified there.
- Continue to keep this redesign separate from active Codex fixes.
