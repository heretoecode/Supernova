# 0.148 Shield QA — unwanted Settings focus tick (1 November 2026)

**Evidence:** owner-provided 14-second Shield video `IMG_5075.mp4`, visually inspected. At ~3 seconds, focused left-hand **Backup & Restore** Settings tile displays a leading tick (✓) while blue focus outline is present. The recording is attached in conversation; **not verified uploaded to GitHub**.

## Bug: Settings navigation focus incorrectly shows selection tick
**Observed:** Left-side Settings navigation item gains a tick when focused, implying it is selected/checked. The blue focus outline already indicates D-pad focus. User explicitly requests the tick be removed.

**Approved expected behaviour:** Never render a focus-driven tick/checkmark on the left-hand Settings navigation cards. Preserve each item's original category icon, text, blue focus highlight, selected-page state, D-pad movement, and Back/focus restoration. Do not remove legitimate checkmarks in actual preference controls, checkboxes or multiselect lists; distinguish navigation focus from true selection state.

**Codex investigation and acceptance:** Identify the common Settings rail item component and focus/selection rendering logic. Remove only the spurious focus tick, including when switching Settings categories, returning from detail views, and initial entry. Add tests proving focused rail items do not show ticks and that real setting checkmarks still work. Verify visually on Shield with a new signed QA APK before marking fixed. Keep PR29 draft and do not merge or alter main.
