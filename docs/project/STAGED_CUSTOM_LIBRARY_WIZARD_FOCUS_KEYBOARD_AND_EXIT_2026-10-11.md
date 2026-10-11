# Staged custom library page wizard QA and UX changes

**Date:** 2026-10-11
**Status:** User-approved focus corrections and keyboard control layout/label; remaining exit confirmation design staged for next-version handover. NOT an implementation authorisation.

## Observed defects
- Custom page wizard launched from Home `+`, with five left-hand steps.
- Initial focus on Step 1 Page Details is correct; pressing **Up** does not enter top navigation, trapping focus in left wizard rail. Down through five steps works; stopping at fifth step on Down is correct.
- Right from Page Details reaches Name, correctly.
- At Step 5 Review, **Right** fails to enter centre panel/actions; user cannot reach Save Page from that state. Up works; Left/Down at boundary correctly have no destination unless a real target exists.
- Combined focus traps make exiting with remote Back the only reliable path; Back currently opens context menu with Save Page, Discard Changes, Continue Editing.
- Keyboard photo (user-provided Shield photo, 2026-10-11) shows oversized pill controls and excessive icon-to-label gaps. Focus initially on T is correct; letter-row left/right focus stays in row and must remain predictable. Text input field is currently focusable despite no useful direct focus action. Heading 'Page name' left aligned.

## Proposed keyboard correction (review with user as needed)
- Centre header **Page Name**, using established modal typography and capitalisation.
- Show muted, non-value placeholder **Name your library page** until user types. Placeholder is not saved as input.
- Avoid an unnecessary D-pad focus stop on the input display; keep editing, caret, insertion/deletion, validation and accessibility working. Test whether accessibility requires an editable semantic field even if not separately D-pad focusable.
- Reduce pill/button dimensions and icon-to-label spacing using global bounded responsive tokens; retain readable text and adequate focus targets.
- Keep alphanumeric key grid and its correct D-pad behaviour, including initial T focus.
- Suggested six-action layout (requires final QA): first row **Caps | Space | Backspace**, second row **Clear | Done | Cancel**. Space visually centred on keyboard. User approved this six-action arrangement. Rename the current keyboard **Select** action to **Done**, since it accepts the typed name and closes the keyboard; it must NOT save the entire library page. **Done** commits the edit and returns focus to Page Name in the wizard; **Cancel** dismisses keyboard edits and restores the field's pre-edit value; **Clear** empties the current edit buffer without closing the keyboard. Space, Backspace and Caps retain their standard functions. Keep action labels unambiguous and sensible directional movement.

## Exit confirmation proposal
- On Back with **unsaved changes**, open a centred **Unsaved Changes** dialog using global dialog design tokens and subtle blurred/dimmed background; **not a context menu**.
- Body: `You've made changes to your custom library page that haven't been saved. What would you like to do?`
- **Continue Editing** (safe initial focus): close dialog and restore previous wizard step/control. Back on dialog does same.
- **Save Page**: run existing validation and save; on success navigate to newly created custom library page and focus it. On validation/error remain in wizard and show actionable message, don't lose data.
- **Discard Changes**: abandon edits and navigate to Home, restoring Home focus.
- On Back **without changes**, exit normally without needless warning.
- **Retain Save Page on Review step** as normal completion action; exit confirmation is fallback for unsaved navigation, not a replacement. Fix focus access to Review Save Page.
- Check handling of top navigation leaving wizard with dirty edits; don't bypass unsaved-changes protection.

## Defer until after global responsive layout pass
- Genres context menu: **keep context menu for now**, because right-side panel must remain a live preview. User wishes to return to its cosmetic UI concerns after seeing global responsive results; **explicit reminder for next QA discussion**, not an implementation item yet. User may provide video later.
- Filters, Content Type and Display Options: user reports mostly working functionality; defer cosmetic revisions until responsive design pass results can be assessed.
- Other keyboard styling not described here should not be broadly redesigned.

## Implementation constraints
- Global responsive standard: [SUPERNOVA_GLOBAL_RESPONSIVE_LAYOUT_STANDARD_2026-10-11.md](SUPERNOVA_GLOBAL_RESPONSIVE_LAYOUT_STANDARD_2026-10-11.md)
- Global modal design: [SUPERNOVA_GLOBAL_DIALOG_DESIGN_STANDARD_2026-10-11.md](SUPERNOVA_GLOBAL_DIALOG_DESIGN_STANDARD_2026-10-11.md)
- Staged handover: [STAGED_NEXT_CODEX_HANDOVER_FIRST_RUN_AND_RESPONSIVE_UI_2026-10-11.md](STAGED_NEXT_CODEX_HANDOVER_FIRST_RUN_AND_RESPONSIVE_UI_2026-10-11.md)
- Preserve approved Supernova styling, app identity, signing, and other ongoing Codex work. Reconcile current branch/version and test on Shield before claiming defects fixed.
