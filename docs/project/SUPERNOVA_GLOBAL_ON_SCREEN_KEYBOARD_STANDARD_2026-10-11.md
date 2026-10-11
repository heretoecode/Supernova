# Supernova — Global On-Screen Keyboard Design and Behaviour Standard

**Date:** 2026-10-11  
**Status:** User-approved design requirements; staged for future Codex handover, not implementation authorisation.

## Scope
Apply this **globally to all Supernova-owned on-screen keyboards**, not just the custom library page naming keyboard. Before modifying, inventory all keyboard implementations and any Android/system-provided keyboard surfaces: shared design rules apply to **app-owned keyboards**; do not assume Supernova can reskin or replace an OS-owned IME. Preserve specialised inputs (password, search, numeric) and their semantics where necessary.

## Single-line entry display
- **Exactly one visible line** of text and **one line of placeholder text**. Do not render multi-line input or two lines of explanatory placeholder copy inside the input box.
- Placeholder text is visually muted and is **not a stored value**. It disappears on first input and reappears when edit buffer becomes empty. For custom library page naming: `Name your library page`.
- Show a **persistent, subtle outlined box** around the input even when not focused; currently it appears only as a focus accent. Use shared Supernova navy background and blue-grey neutral border. Accent colour is reserved for true focus indication; distinguish always-visible border from focus glow.
- Single-line height and padding follow global responsive layout tokens. On overflow, scroll the text/caret horizontally, never wrap or grow the field vertically. Preserve readable contrast.
- Do not add a separate **D-pad focus stop** on the display where it has no useful direct action. Key presses should update a real editing buffer and visible caret while D-pad focus stays on keyboard keys/actions. Ensure external keyboard input and accessibility editing semantics continue to work; verify platform constraints before suppressing input focus.

## Header
- Use a centred, properly capitalised context-specific heading (e.g. `Page Name`), using the shared dialog type hierarchy; do not duplicate placeholder text as a second input line.

## Global six-button control arrangement
User approved compact two-row layout:
- Top row: **Caps | Space | Backspace**
- Bottom row: **Clear | Done | Cancel**
- **Space** must be horizontally centred relative to the keyboard. Maintain predictable directional D-pad focus and initial focus on letter T where that is the current expected behaviour.
- Use **compact, content-appropriate pill/button widths**, bounded by responsive tokens and TV focus target minimums. No oversized equal-width pills unless needed by layout constraints.
- Icons should be comfortably close to their labels, using a consistent small tokenised gap, not positioned at opposite ends of large pills.
- `Done` replaces `Select`: confirms text entry and closes keyboard; it does **not** save the larger page/form.
- `Cancel`: closes keyboard, discards edits made during this keyboard session, restores pre-edit value.
- `Clear`: empties current edit buffer but keeps keyboard open.
- `Caps`, `Space`, `Backspace`: retain expected typing behaviour. If caps toggles, indicate state accessibly.
- Ensure all buttons remain visible and operable at supported logical viewports, including with focus enlargement.

## Engineering and conformance
- One shared keyboard foundation and design tokens for all app-owned keyboard entry points; audit call sites and adapt labels/context while preserving identical structural rules.
- Do not change functioning character-grid D-pad traversal unnecessarily. Preserve intended initial key focus.
- Test new and existing values, empty input, placeholder transitions, caret, long text horizontal scroll, Cancel rollback, Done commit, Clear, external keyboard, accessibility and different Shield output modes.
- Preserve actual app accent colours, typography and focus animations. Reference [Global Responsive Layout Standard](SUPERNOVA_GLOBAL_RESPONSIVE_LAYOUT_STANDARD_2026-10-11.md), [Global Dialog Design Standard](SUPERNOVA_GLOBAL_DIALOG_DESIGN_STANDARD_2026-10-11.md), and [Custom Library Wizard QA](STAGED_CUSTOM_LIBRARY_WIZARD_FOCUS_KEYBOARD_AND_EXIT_2026-10-11.md).

This specification is staged on a documentation branch only; do not start coding or merge into active development until the user authorises the next handover.
