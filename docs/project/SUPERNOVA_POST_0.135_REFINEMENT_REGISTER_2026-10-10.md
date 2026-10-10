# Supernova — Post-0.135 Refinement Register
Date: 2026-10-10
Status: Captured for NEXT refinement release; **not an instruction to implement now**
Source: User's first on-device review of installed 0.135 APK on NVIDIA Shield TV.

## Workflow / authority
- Save each observation promptly so it is available for the next Codex handover.
- Do **not** begin coding, modify the 0.135 branch, merge, rebuild, or launch the next release from this document alone. User will explicitly approve a consolidated handover later.
- Next release is expected to focus on fixes, tweaks and refinements.
- Onboarding problems and detailed custom Library Page review are pending further user feedback; do not infer requirements for them yet.

## 1. Clock typography mismatch — confirmed recurring bug
**Observed:** The top-right clock is visibly a different font and/or size from other top-navigation labels; this has persisted despite earlier reports of fixes. User supplied a photograph of the TV screen showing the mismatch.

**Required:** Clock must use the same font family, weight, visual text size, baseline/alignment as the other navigation labels, while preserving appropriate numeric legibility. Investigate why earlier fixes failed or did not persist, rather than another unverified size-only adjustment. Verify on Shield hardware with screenshots.

## 2. Custom Library Page: remove icon choice — user-approved tweak
**Observed:** Custom Library Page creation offers icons (Library, Movie, TV, Documentary), while other top-navigation pages are text-only.

**Required:** Remove the icon-selection feature from the custom Library Page configuration. Keep text title/name and Next in the existing flow; page appears as text-only in top navigation. Retain the '+' affordance for creating a custom page. Existing saved page names must be preserved; previously saved icon preferences should be safely ignored/migrated without data loss. User will separately review other custom-page functionality later.

## 3. Top-navigation blur abrupt pop-in — confirmed refinement
**Observed:** On TV Shows page, header is crisp at top. When focus enters first library row / page scroll begins, header blur visibly appears abruptly. On leaving and re-entering page, effect resets, then pops in again. User supplied a short video.

**Required:** Preserve the existing approved effect of media moving underneath the blurred header on Home/Movies/TV, but smoothly transition blur/overlay opacity as scrolling/focus changes, without flicker or sudden pop-in on page re-entry. Initial animation guidance: ~200–250 ms ease-in/ease-out, subject to Shield performance and visual QA. Do not reintroduce scroll-dependent blur on Settings, Search or Network & Files, where it was deliberately excluded.

## 4. Navigation focus: accent-colour animated underline — proposed design awaiting final sign-off
**Current:** Rectangular focus box takes the user's accent colour. Left/right navigation remains correctly locked to top navigation (preserve this).

**Proposed:** Replace the focus rectangle with a thin accent-colour underline directly beneath the currently focused label or icon. Underline width tracks focused item's content width, so e.g. Search icon is short and Network & Files is longer. On D-pad left/right transitions, smoothly animate underline position and width (growing/shrinking) without jitter; respect user's selected accent colour. Ensure keyboard/remote focus remains accessible and currently selected page is distinguishable from focused item. **Do not treat as final implementation approval until user confirms final design.**

## Evidence and validation
- Original user photograph of clock and video of header transition were supplied in the planning conversation; these files are **not embedded in this repository document**. Request source media at handover if Codex needs them.
- Test on physical NVIDIA Shield: clock font/size, text-only custom page, smooth header blur in both directions and on re-entry, underline transitions between short and long items, focus navigation retention, and accessibility.
- Do not claim fixes are complete solely from automated test passes; confirm on device.

## Open / not yet specified
- Onboarding observations: user will return with details.
- Additional custom Library Page feedback: user will return with details.
- Additional post-install issues: append as reported.

## 5. Hero carousel: gradient persists on off-centre cards — confirmed bug
**Observed:** The centred hero card correctly has the old gradient removed, but partially visible neighbouring/teaser hero cards still show it. The gradient disappears when that same card becomes centred. User provided screenshot.
**Required:** Apply the approved gradient-free artwork treatment uniformly to every carousel item at every position and during movement. Avoid distinct rendering paths for centred and off-centre cards. **Preserve the now-correct left/right carousel animation.**

## 6. Hero carousel: remove Play action — approved correction
**Observed:** Both Play and More Info buttons render, but Play cannot be focused; only More Info is reachable.
**Required:** Hero cards are promotional/discovery surfaces, not direct playback entry points. Remove Play button entirely, including layout reservation and stale focus/accessibility handlers. Retain a single **More Info** action that opens Details, where playback decisions are made.

## 7. Hero carousel: More Info control styling — approved refinement
**Observed:** More Info currently has a solid blue fill, unlike the preferred outlined translucent controls on Movies page.
**Required:** Restyle More Info as a rounded, sensibly sized translucent/neutral outlined control with readable text. No solid accent-colour fill at rest. On focus, outline takes user's chosen accent colour. Preserve clear focus indication and remote usability. An icon is optional only if consistent with the approved control system; do not invent one just for this button.

## 8. Shared boxed-action / option control foundation — user-approved design direction
**Reference:** Movies page Filters / Sort controls in user-provided screenshot. Establish a reusable design rule for appropriate interactive boxed controls across Home, Movies, TV Shows, Settings, Network & Files, Search and Details:
- Monochrome icon at left **where a control has an icon**, label to right, consistent typography and gaps.
- Sensibly sized rounded translucent box with neutral visible outline in unfocused state.
- Focused state changes outline to user's selected accent colour; avoid solid accent fill.
- Consistent padding, radii, minimum target size and D-pad focus affordance.
- Apply to boxed actions and option controls, **not** indiscriminately to nav labels, artwork cards or every Settings row. Preserve each screen's approved layout.
- Reconcile this with the existing three-panel shared foundation; avoid parallel divergent styles.
**Evidence:** Two user photographs show hero action controls and Movies-page boxed option controls. Photos remain in planning conversation; not embedded in repository.


## 9. Customize Home — structure, controls and genre workflow (approved for next refinement handover)
**Evidence:** User supplied ~55-second video walkthrough of the Customize Home menu and described the interaction in detail. Media is in the planning conversation, not embedded here.

### Confirmed working behaviour to preserve
- Turning **Dynamic Genre Rule** off **no longer hides the row**; this regression is fixed. Rule enablement and row visibility must remain independent.
- Existing row reorder interaction is satisfactory: preserve.
- New Ascending/Descending direction control is satisfactory: preserve.
- Genre checklist tick indicators are liked: preserve.

### Main Customize Home view
- Add clear hierarchy: heading **Customize Home**, subtle divider beneath heading, and separately styled guidance text (“Select to edit”, “Up/down to reorder”) distinct from focusable options.
- Move **Create New Row** from bottom of list to a prominent location at top, under heading/instructions, so it remains discoverable with long row lists.
- Improve legibility and consistency of **Shown / Hidden** row status (exact visual presentation to be refined with existing shared control design).
- Remove redundant first-level **More** context menu. Selecting a row should expose **Rename**, **Genre Rule & Contents**, and **Delete Row** directly in an organised inline/adjacent action area for that row, rather than forcing an extra popup. Keep D-pad usability and avoid cramming buttons into the title. Rename opens keyboard; Delete requires appropriate safeguard/confirmation. Genre Rule & Contents remains the relevant secondary configuration panel. Precise inline arrangement may be validated with a mockup before coding.

### Genre Rule & Contents
- Consistent title casing for **Dynamic Genre Rule** (avoid “Dynamic genre rule” inconsistency).
- Remove bottom **Done** action. Apply/save changes as users make them; pressing Back simply navigates out without discarding changes. Refresh affected Home row promptly, but avoid unnecessary full-library rescans for each selection.
- Keep existing direction selector and distinct row visibility setting; changing genre rule must not hide row.

### Genre picker
- Keep multi-select checklist with immediate visible tick state.
- Remove bottom **Done** confirmation. Pressing Back exits while retaining selections.
- Move **All Genres** reset to the very top, separated by divider/spacing from actual genre entries. It resets genre selections; it must not be mistaken for a selectable genre.
- Preserve selection state consistently when returning to the picker and Home configuration.

### App-wide on-screen keyboard
- Improve structure/hierarchy of keyboard and action strip throughout the app, not only Home rename.
- Add an accessible **Caps Lock** toggle with clearly visible on/off state, supporting uppercase and lowercase.
- Retain Clear, Space, Backspace, Select and Cancel. Maintain D-pad focus, editing reliability and existing functionality.

### Validation
- Test creation, reorder, rename, delete, hide/show, genre-rule enable/disable independence, immediate changes, genre reset, Back behaviour, direction, keyboard case toggle and focus restoration on Shield.
- Preserve established styling and existing functional behaviours.

## Home-page review checkpoint
User stated the Home page review is **complete for now** (2026-10-10). This does not imply any fix has been implemented or verified; the recorded Home refinements are ready for the eventual consolidated next-version Codex handover. User may report further issues later.

## 10. Shared library pages (Movies, TV Shows, Custom Library Page) — review complete for now
**Evidence:** User's ~56-second on-device video and previous Movies screenshot. Video shows Filters, genre/year menus, controls, grid and list view. Source video remains in planning chat, not embedded here.

### Required refinements (apply to all library pages via shared design)
- **Compact outlined controls:** Filters, Sort, Order, List/Grid, Columns etc. currently have oversized boxes. Reduce horizontal/vertical padding and tune widths to content, while retaining accessible D-pad targets, readable text, monochrome icons and the approved translucent outlined style. Do not shrink text merely to reduce box size.
- **Accent-coloured focus:** Currently focused outlines are white; use the user's selected accent colour for focus on these boxed controls, consistent with the global boxed-control foundation. Maintain sufficient contrast.
- **Summary spacing:** On library pages, reposition show/movie count, total size, and local/network storage summary a little lower so the summary block sits more evenly between the page heading and the boxed controls. Preserve all metrics and responsive layout.
- **Genre picker heading:** Standardise the wording/capitalisation for the Genre heading and “Match Any Selected” mode. Prefer consistent title case rather than arbitrary mixed casing; confirm final copy with UI design.
- **All Genres reset:** Move to top of genre list, visibly separated from individual genres. This clears genre selections, not a genre entry.
- **Immediate genre updates:** Toggle ticks and apply/save filters immediately; remove bottom Done action. Back simply exits without discarding current selections. Avoid unnecessary full rescans; ensure UI and results stay in sync.
- **Order toggle:** Replace Ascending/Descending popup with a single direct button. Each OK/Select press toggles Ascending ↔ Descending, with matching label and directional icon updated immediately; preserve sort mode and focus.

### Confirmed working; preserve
- Poster-grid boundary navigation: Down/Right at final item do not move into nonexistent items; Up/Left go to valid adjacent items.
- Back from a title's Details restores focus to that title.
- Filters menu overall layout and Sort mode selection are satisfactory.
- Current List/Grid and Columns behaviour is not targeted for change.
- These are **shared** changes across Movies, TV Shows and custom Library Page, not independent one-off screen implementations.

### Review checkpoint
User considers default Movies/TV library page review complete for now. More custom Library Page-specific feedback may follow separately. No coding is authorised merely by this record.

## 11. Shared control labels — consistent title case
**Observed:** Some library controls inconsistently capitalise their multi-word labels, e.g. “Newest first”, “Oldest first”, “List view”, “Grid view”.
**Required:** Use consistent **Title Case** for these UI control labels: **Newest First**, **Oldest First**, **List View**, **Grid View**. Apply across Movies, TV Shows and custom Library Pages, and audit comparable multi-word action/control labels app-wide for consistency (without blindly title-casing sentences, descriptions, metadata or user-created names). Preserve existing toggling, focus and view behaviour. This is a wording refinement, not a feature change.

## 12. Search page — restore intended non-three-panel design (confirmed 0.135 regression)
**Evidence:** User photo of 0.135 Search shows three oversized blue panels: keyboard left, empty results centre, guidance right. This three-panel foundation was intended for Settings and Network & Files, **not Search**.
**Required:**
- Remove three-panel framing from Search and restore previously approved simpler layout: keyboard on left; right-hand content area has unboxed guidance when there are no search results.
- Guidance is plain text with appropriately subtle **monochrome icons** illustrating search features (movies/TV/episodes/filenames, punctuation/original titles, minor typos, people/studios where supported). No oversized boxed guidance panel.
- When matching search results appear, **replace/hide guidance in that same right-hand area**, rather than showing results in a narrow centre panel; make good use of available width.
- Preserve existing search matching capabilities and D-pad navigation. No search-speed optimisation requested at this time.
- Keep Search excluded from scroll-dependent header blur, consistent with earlier design decisions.
- This is restoration/refinement, not a new three-panel Search redesign.

## 13. Single global on-screen keyboard design — app-wide requirement
**Observed:** Inconsistent keyboard design across Search, Customize Home row renaming and other text-entry screens.
**Required:** Implement/use **one shared keyboard component and visual foundation** across all Supernova screens requiring on-screen text entry. Changes to that keyboard should propagate globally; do not create per-screen divergent keyboards.
- Consistent key arrangement, typography, spacing, containers, selected/focused appearance, D-pad focus, editing and focus restoration.
- Retain Clear, Space, Backspace, Select and Cancel where relevant; add previously approved **Caps Lock** control with clearly indicated uppercase/lowercase state.
- Allow screen-specific configuration (prompt, initial text, valid characters, etc.) without altering global keyboard design.
- Verify Search, row rename, metadata search/edit and other existing keyboard entry points on Shield. Avoid breaking platform/system keyboard integrations where required.
- Shared keyboard requirement supersedes any interpretation of item 9 as a Home-only keyboard tweak.

**Status:** User confirmed Search layout concept and app-wide keyboard consistency; saved for next refinement handover, not authorised for immediate coding.

## 14. Global keyboard baseline and action row — clarification
**User decision:** The existing **Search-page keyboard** is the most refined keyboard and should serve as the **visual and interaction baseline** for the global shared keyboard described in item 13. The Home > Customize Home > Rename keyboard currently offers Select and Cancel in addition to Clear, Space and Backspace.

**Required:** Preserve Search keyboard's established key layout and styling as the baseline; standardise a bottom action area supporting **Caps Lock**, **Clear**, **Space**, **Backspace**, **Select**, and **Cancel**. Caps Lock should be visible and consistently available in all keyboard contexts, switching uppercase/lowercase with clear state feedback. Where context calls for it, Select confirms text and Cancel dismisses without applying unconfirmed edits. For live-search contexts, results must continue updating as text is entered without requiring Select to trigger searching. Maintain consistent D-pad navigation and screen-appropriate confirmation/cancellation semantics without visual fragmentation. Validate across Search, Home row rename and other app keyboard entry points.

## 15. APPROVED global keyboard visual design — 2026-10-10
**Decision:** User approved the latest keyboard mockup's **understated key styling** and **compact pill-shaped bottom action controls with monochrome icons LEFT of their text labels**. This is the definitive shared keyboard visual direction for the next refinement release, superseding earlier provisional keyboard illustrations.

### Approved design
- Use the current Search keyboard as the starting point for the global keyboard component, preserving its existing QWERTY/number key layout, input position, and D-pad navigation.
- Individual keys have **very subtle translucent backgrounds**, soft thin neutral outlines and gently rounded corners; avoid heavy rigid boxed/grid appearance.
- Focused key uses the user's chosen accent-colour outline with clear but restrained emphasis (mockup cyan is illustrative only).
- Bottom action controls are **compact horizontal pills**, not square tiles. Each has a monochrome icon **to the left** of its text label, with consistent alignment, subtle translucent background, restrained neutral border, and accent-coloured focus.
- Standard actions: **Caps Lock**, **Clear**, **Space**, **Backspace**, **Select**, **Cancel**. Caps Lock toggles case and visibly indicates state. Keep context-specific Select/Cancel semantics, including live Search updates without mandatory Select.
- **Remove the X/clear icon from inside the Search text field**; it duplicates the **Clear** action below. Clicking/focusing the text field should not erase the query. Do not create two clear mechanisms.
- Apply the same keyboard design globally across Search, Customize Home row rename, metadata editing and other Supernova on-screen keyboard contexts, with only necessary context-specific behaviours.
- The Search page itself remains a **non-three-panel layout**: keyboard left, unboxed search guidance with monochrome icons right until results replace that guidance. The visual mockup is reference for the keyboard, **not approval of any extra panels or unrelated Search changes**.

### Evidence and acceptance
- Latest approved concept was generated and reviewed in the planning conversation; the illustration itself is not embedded in GitHub. The specification above is the authoritative text reference.
- Verify on Shield: readable keys, sensible focus sizes, remote movement, focus restoration, uppercase/lowercase, action row and absence of duplicate Search clear X.
- **Status: DESIGN APPROVED; implementation deferred until explicit next-release Codex handover.**
