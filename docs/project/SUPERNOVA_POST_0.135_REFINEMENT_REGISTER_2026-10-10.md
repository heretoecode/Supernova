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

## 16. Custom Library Page — first-use landing with embedded wizard (approved direction)
**Observed in 0.135:** Clicking '+' in top navigation opens an unnecessary “Create a New Page” context menu containing a single “Library Page” option, then the existing three-panel five-step wizard. The wizard foundation works. User confirms existing Edit control on created custom page reopens wizard, and Delete Page works.

**Required first-use flow:**
- When **no custom Library Page exists**, clicking top-navigation **+** opens a dedicated **first-use landing page directly**, without the intermediary context menu or separate Create button.
- The landing page presents a **brief, clear introduction** describing the purpose of a custom Library Page and personalised filters, with the **existing five-step creation wizard embedded directly below the introduction** and ready to use.
- Keep introduction compact so it does not crowd or shrink the wizard. Preserve the wizard's established three-panel layout, steps, filtering and D-pad functionality.
- **Do not mention Discovery / Coming Soon anywhere on the landing page.** Discovery is parked for future discussion only, not a promised upcoming feature.
- Retain prior approved removal of icon selection; page title is text-only.

**After creation:**
- First-use landing no longer appears; the created custom Library Page is shown normally in the shared library layout.
- Existing **Edit** button beside Filters/Sort etc. reopens the five-step wizard; preserve.
- Existing Delete Page works; preserve. After deletion, return to first-use state so '+' leads to the introduction plus embedded wizard again.
- Preserve the current one-custom-page scope. Do not introduce multiple pages or a new Discovery section.

**Validation:** First use, wizard entry, completion, edit, delete/recreate, Back/focus, no redundant context menu, no Discovery messaging. No application code change authorised by this planning entry.

## 17. Populated Custom Library Page must reuse default library presentation — confirmed regression
**Evidence:** User's ~25-second Shield video of newly created populated Custom Library Page, reuploaded and reviewed in planning chat. Video itself is not embedded in repository. Existing Movies/TV Shows pages are the authoritative design and behaviour reference; no new grid mockup required.

**Core rule:** Custom Library Page is another configured instance of the same library UI as Movies and TV Shows, not a separately styled screen. Only user-defined page title, filtered content and custom-only Edit action should differ. Prefer shared components and styles over parallel duplicated implementations.

**Required fixes:**
- Show page title (e.g. Documentaries) with the **same library summary block and layout** as default pages: item count, total size, storage breakdown (local/network/WebDAV as applicable). Calculate these metrics from the **actual filtered items on this page**, not the full library; support mixed movies/TV and sensible labels.
- Match exact vertical spacing between title, statistics and library control row. Inherit previously approved summary-position refinement.
- Match default-page poster grid spacing, column alignment, card dimensions and clipping/partial visibility conventions.
- Focused and unfocused posters must retain the same rounded corners; remove square-corner focus enlargement, irregular zoom or artwork protrusion. Reuse default-page focus zoom, outline, sizing, z-order and visibility, with user's accent colour as appropriate.
- Match existing D-pad movement and boundary behaviour, plus restoration to the same focused item after returning from Details.
- Inherit shared Filters, Sort, Order, List/Grid and Columns controls and all already recorded library refinements (compact translucent boxes, accent-coloured focus, direct sort-order toggle, genre reset/instant updates, title-case labels). Preserve custom-only **Edit** control in its approved position.
- Preserve working five-step creation wizard, Edit and Delete Page functionality; do not change the previously agreed first-use landing with embedded wizard.
- Verify visual/behaviour parity on Shield against actual Movies and TV Shows screens. Do not consider superficially similar styling sufficient; changes to shared library components should carry across all three.

**Status:** Approved correction for future refinement handover. No code changes authorised by documentation alone.

## 18. Custom Library Page wizard — approved editing overlay, five-step refinements, and deletion dialog (2026-10-10)
**Evidence:** User's ~1-minute walkthrough and photographs of the existing wizard, Live Preview, Review and Delete confirmation, reviewed in planning conversation. The media is not embedded in GitHub.

### First creation versus subsequent editing
- **First use, no custom page:** retain approved dedicated landing page with concise introduction and the existing five-step wizard directly below (item 16); no intermediary context menu.
- **Editing an existing page:** change the library control label from ambiguous **Edit** to a clear title such as **Edit Library** (final wording may be harmonised with other controls); add a monochrome icon at left. Open the five-step wizard as a **centred, floating three-panel overlay above the existing custom library page**. The library remains visible but is softly blurred/dimmed, giving the wizard depth through subtle shadow and separation. Underlying page is noninteractive while overlay is open. Preserve legibility, focus and usable panel proportions; avoid an edge-to-edge full-screen takeover. This background treatment is specific to edit mode; do not show the underlying library artwork directly through wizard panels in a distracting way.
- Preserve functional creation/editing, filters, preview, save, delete and navigation. Do not implement a second wizard.

### Wizard foundation / step-specific changes (approved)
- **Left panel:** non-focusable **Library Page** header elevated and separated from the five navigable steps by a subtle divider. Consistent typography, spacing and focus.
- **Page Details:** retain name and remove **Icon** choice everywhere (text-only navigation). Keep the step name **Page Details** for now. Place **Delete Page** only here, at bottom of centre panel in a clearly separated caution/destructive area, not on all five steps. No Delete Page on Content Type, Filters, Display Options or Review.
- **Content Type:** remove Movies/TV Shows/Both popup; expose all three choices directly as selectable controls in centre panel.
- **Filters / Genres:** remove Done; genre changes persist immediately, Back closes without reverting; **All Genres** reset at top separated from real genres; heading uses consistent Title Case such as **Genres · Match Any Selected**.
- **Filters / Original Language:** show established global language flag icons alongside language names, using the app-wide flag system.
- **Filters / Collections:** remove Collections option entirely from this wizard.
- **Display Options:** preserve existing functionality; apply shared section structure and remove redundant Delete Page.
- **Review:** replace unstructured raw lines (e.g. “both · grid”, “title · Ascending”) with labelled, spaced summary rows/sections for page name, content type, layout, sort field and direction; retain Save Page and Previous navigation.
- **Right Live Preview:** keep existing working list and matching-title count for this release; improve heading divider, count hierarchy, spacing and readability. Richer poster preview is not required now.
- **All option pickers:** eliminate redundant Done confirmations where selection can apply immediately; Back should navigate out without discarding changes. **Do not remove Save Page**, which finalises creation/edited-page configuration; distinguish immediate picker selection from committing the overall wizard.

### Approved compact Delete Page confirmation
- A **narrower, centred modal** with monochrome warning icon and heading **Delete This Page?**, subtle divider below heading, and explanatory copy: only this custom page is deleted; library media, watched state and playback progress remain.
- **Cancel** and **Delete** actions centred below copy, each with monochrome icon left of label and the shared outlined/translucent styling. Cancel gets initial D-pad focus. Delete requires explicit confirmation; no destructive default.
- Keep the existing confirmed-working deletion semantics.

### Wizard backdrop
- Use Supernova's established standard background treatment already approved for Settings/Network & Files/etc. for the first-use wizard. For edit mode, the custom library remains behind a **blurred/dimmed overlay**, rather than displaying unfiltered hero artwork through the wizard. Avoid abrupt blur pop-in or excessive visual noise.

### Validation
Test first-use creation, edit overlay size/blur/dim/focus trap and dismissal, direct Content Type selection, genre updates, flags, removed icon/collections, Review formatting, Preview count/list, Save Page, Cancel/Delete safety, and preservation of existing data and D-pad behaviour on Shield.

**Status:** All changes above user-approved as design/requirements; implementation only after explicit Codex handover. Shared foundation refinements are additionally documented in the three-panel foundation specification.

## 19. Custom Library wizard Step 3 — compact fit, focus and persistent navigation
**Evidence:** Three user photos of Filters step show tall vertical gaps, centre-panel scrolling with header disappearing, and Previous/Next/Delete Page below fold.

**Approved intent:** Fit all Step 3 filter controls in the centre panel **when comfortably possible** without reducing readability or eliminating reasonable spacing. First reclaim space by removing Collections and the redundant Delete Page action (already approved in item 18); then tune vertical gaps, grouping and row padding. Do **not** force a no-scroll layout at the expense of text size, remote targets or accessibility. Allow scrolling when genuinely necessary (e.g. smaller viewport, accessibility scaling or longer translated labels).
- Keep wizard **Previous/Next navigation accessible**, ideally outside the independently scrolling filter options region rather than disappearing below the fold.
- Apply shared panel-heading rule to non-focusable **Filters** heading: consistent typography, top inset, divider and separation from first actionable Genres row. Preserve initial D-pad focus on Genres and intuitive scrolling/focus restoration.
- Delete Page must appear **only on Page Details** (item 18), never on Filters.
- Test on Shield at normal scaling and with long text/accessibility settings; no clipped options, no lost focus or offscreen primary actions.
**Status:** User-confirmed refinement; for next Codex handover only.

## 20. Custom Library wizard D-pad focus, Back behaviour, unsaved changes and parent-page restoration (2026-10-10)
**Evidence:** User Shield walkthrough, screenshot of the current “Unsaved page changes” menu and uploaded video. Design/fix approved for the next refinement release; no implementation yet.

- **Left-panel live selection:** moving Up/Down among Page Details, Content Type, Filters, Display Options and Review automatically synchronises the centre panel with the highlighted step, without pressing OK. Apply this as a shared three-panel navigation principle wherever the left column is a navigation/category list (e.g. Settings); do not trigger destructive actions or open files merely by focusing them.
- **Focus transitions:** Right from selected left step enters the first actionable centre control; Right from centre into the Live Preview must show visible focus on the scrollable preview panel or focused item. Up/Down in the preview scrolls it; Left/Back returns to the remembered centre control. Never let focus disappear.
- **Back hierarchy:** Back from centre returns focus to the **same highlighted step on the left**, retaining the matching centre content; it must not silently advance backward to the preceding wizard step. Back must never change the centre content to Filters/Content Type/Page Details while leaving focus highlight stuck on Display Options. Explicit Previous/Next can change steps, synchronising both panels.
- **Top-nav focus flash:** eliminate brief erroneous Home focus/highlight when using Back inside the wizard; retain focus within active overlay.
- **Exit destination (confirmed regression):** when editing an **existing Custom Library Page**, exiting the wizard must return to **that same Custom Library Page**, not TV Shows or another library tab. Preserve existing page identity, content and scroll position; restore focus preferably to the Edit Library control. This applies to Save, Discard and Back/close paths. First-time creation may have different entry/exit routing as already specified.
- **Unsaved-changes modal:** if actual unsaved changes exist on attempted exit, show a compact **centred** dialog rather than a right-panel context menu, with a non-focusable title **Unsaved Page Changes**, separator, and three clearly separated outlined/translucent icon-labelled actions: **Save Page**, **Discard Changes**, **Continue Editing**. Use monochrome icons, accent-colour focus and initial focus on **Continue Editing**. Save persists and returns to originating custom page; Discard restores prior saved configuration and returns; Continue Editing closes dialog and restores prior wizard focus. If no changes exist, exit without prompting. Avoid spurious prompts from mere browsing.
- **Global principle:** on three-panel navigation/category screens, left-column focus previews corresponding centre content without an extra OK; Back reverses panel focus hierarchy predictably, maintaining active category/content synchronisation. Adapt appropriately to each screen's semantics.
- **QA:** test all five steps, focus movement between all three panels, preview scrolling, Back repeatedly, Home flash regression, Save/Discard/Continue Editing, unchanged edits, and exit back to original custom page (never TV Shows), including after filtering or renaming.
**Status:** Recorded for next Codex refinement handover, not implemented.

## 21. Network & Files — Shield walkthrough refinement (2026-10-10)
**Scope:** Approved next-release corrections, not implemented. Evidence: user's multiple Shield videos and spoken walkthrough, including external-drive browser and folder-loading recordings. Media was reviewed in chat; raw video files are not embedded in GitHub. Cross-reference shared three-panel/browser foundation; preserve already working behaviour.

### Critical navigation/layout defects
- **P0 left navigation:** Up/Down must remain in left navigation and move predictably to adjacent visible item, NEVER jump to centre panel on every other press or skip to a different section. Right intentionally enters centre, Left restores exact remembered left item. Correct navigation tree, focus owner and D-pad routing, with regression tests for every entry and nested child, repeated visits, page changes, Back and storage changes. This currently makes walkthrough almost unusable.
- **P0 top bar overlap:** on entry from other pages, Network & Files panels must never slide under/overlap top navigation. Stabilise insets, top alignment and scrolling across repeated navigation/return.
- Left focus automatically previews selected category's centre/right content, without OK, but must never itself transfer focus. Parents are expandable on OK using existing Settings indentation behaviour, indented children under Local Storage and Library Health. Retain single-expanded-section convention where practical.
- Left hierarchy: Overview; Local Storage > Internal Storage, Backup 1, Backup 2 and detected drives; Network Shares; Add Network Source; Discover Devices; Cloud Services; Library Health > Unmatched Media, Incorrect Matches, Missing Metadata or Artwork, Unavailable Files, Source Problems. Device names are examples, dynamically sourced. Keep left entries single-line with short friendly name; put full vendor/device/path details on right. No oversized multiline drive labels.
- Apply shared panel headers/dividers, sensible **comfortable** spacing/padding, accent focus and readable monochrome icons. Avoid needless scrolling but never crush rows.

### Context-sensitive centre/right
- List/Grid, Sort, All Files controls appear **only when centre is an actual file/folder browser**, not Overview, Network Shares, Add Network Source, Discover Devices, Cloud Services or Library Health. Give controls consistent monochrome icons and restrained outlined/translucent styling.
- All Files is a genuine, visible ON/OFF control, not inert text. ON shows all file types, OFF supported video types; folders remain visible. Instant update and persist/synchronise with corresponding Settings preference.
- When browsing files/folders, make centre wider than right (starting test target ~24/46/30 left/centre/right), while non-browser contexts retain appropriate shared foundation widths. Test on Shield, preserve legible details/actions. Breadcrumbs work currently: preserve deep ancestor navigation, focus and scrolling.
- Folder loading: preserve existing Supernova animated indicator, but reserve fixed status/count area and gently transition from loading to count/results without pop/jump or changing breadcrumb/toolbar positions. Preserve item list focus/scroll where appropriate.
- Browser right panel remains contextual metadata and actions, including include/exclude, Add to Library, Save Location. Existing tested inclusion/exclusion saving and subsequent scan are working; protect as regressions. Do not invent duplicate scanning.

### Network/source screens — remove menu-to-menu legacy detours
- Network Shares centre immediately shows saved shares (if any) and Add Network Source action; right shows selected share details/status. No blank centre with file toolbar or Connect > Browse Network Share > Add Source detour.
- Add Network Source centre directly presents currently supported protocols (SMB, WebDAV, FTP, SFTP etc. as verified in app) and their connection controls/forms. Reuse existing connection engine; no redundant legacy full-screen picker/dialog layers.
- Discover Devices shows discovered devices and connection actions within centre/right, without unnecessary intermediary screens.
- Cloud Services is future-extensible but only put.io is currently listed. Direct in-panel account connection/status and **independent Enabled/Disabled versus Connected/Disconnected** controls. Disable retains secure authentication; disconnect logs out/revokes appropriately. **Full put.io OAuth integration is a next-release goal but externally blocked until actual registered client/redirect credentials/configuration and validation are available; never fabricate or claim success.** Record blocker and test once supplied.

### Library Health integrated in Network & Files
- On highlighting parent **Library Health**, show a meaningful **Health Overview** in centre (source availability, issue counts and clean/healthy empty state) and contextual details/actions on right; do not automatically select first child.
- Expandable indented children: Unmatched Media, Incorrect Matches, Missing Metadata or Artwork, Unavailable Files, Source Problems. Focusing child immediately updates centre to matching issue list, right to issue details/remediation. The centre issue list need not be a file browser, so no file toolbar.
- Reuse actual existing Library Health logic, eliminate Review Issues/Review Sources intermediate screen. Preserve offline media records, metadata, watched and resume progress and distinguish offline source vs missing file.
- Overview (top-level Network & Files): preserve confirmed working scan-in-progress reporting; improve hierarchy, status alignment and focus, remove file toolbar. Detailed Overview QA is deferred until critical focus fixes allow reliable walkthrough.

### Acceptance / regression
Shield video-based D-pad test matrix: all left items and nested children; no escape; no header overlap; entering/leaving from every top-level page; conditional toolbar; visible All Files state and filtering; breadcrumbs/deep folders; loading transition; shares/add source/discovery; put.io gated authentication; Library Health overview and categories; saving staged folder changes and scan; no loss of offline library records. Preserve current working carousel and other unrelated pages.

**Status:** Documented and ready for next Codex handover; no code implementation authorised by this documentation.

## 22. Walkthrough coverage and outstanding work (2026-10-10)
Reviewed/refinement requirements recorded in this register for: **Home**, **Movies and TV Shows shared library pages**, **Custom Library Page first-use landing and five-step create/edit wizard**, **Search and global keyboard**, **top navigation**, and **Network & Files / universal browser / Library Health / network sources**. Shared design rules are in the three-panel foundation spec. **Settings page detailed walkthrough is next and NOT yet complete.** **Initial onboarding / empty-library first-launch** had prior design requirements in the 0.135 master/shared browser specification but the user has deferred testing of the actual 0.135 onboarding experience; do not mark its post-install QA as completed. The user intends to test it after clearing app data. **Before clearing data, warn that this can remove local app settings, library index, artwork/cache, watched state and progress, saved source configuration and authentication; back up/export supported data first, and avoid clearing data until ready.** Other deferred discussions (e.g. APK size investigation) remain outside this walkthrough scope.

## 23. Home hero carousel — TV series versus newly arrived episode eligibility (2026-10-10)
**Status: user-confirmed behaviour for next refinement handover; design requirements, not implemented.** Preserve existing approved hero artwork, centred/side-card gradient removal, More Info-only outline button (remove Play), and metadata refinements.

- An unstarted TV show may receive a **whole-series** hero card, not an arbitrary episode (observed incorrect The O.C. S01E09 promotion despite no watch history).
- Once the user begins a show, remove the generic **whole-series** hero promotion because ongoing viewing belongs in Continue Watching. In-progress episode (e.g. Daredevil S01E03 half watched) stays in Continue Watching, not hero.
- When the user completes all currently available sequential episodes (e.g. S01E01–E03), and a **newly imported** next sequential episode (S01E04) becomes available, promote that **specific new episode** in the hero carousel. This should work even when an unaired/not-yet-in-library episode previously prevented further progress.
- Eligibility requires reliable series/season/episode identity, playback-completed state and newly-added timestamp; don't promote arbitrary older episodes, out-of-order imports, duplicate versions or already watched episodes. Handle season boundaries, gaps and metadata uncertainty conservatively. Avoid duplicating the same actionable item in hero and Continue Watching. Define expiry/dismissal/consumption of the promotion in implementation plan, not invented as an approved decision.
- Consistent hero metadata order: **release year · genres · context-sensitive third field**: movie runtime, individual episode runtime, or series season count. Series counts reflect distinct seasons with local episodes, **not complete-season claims**; detailed episode inventory on Details page. For individual episodes use episode year if reliably known, series year fallback when unavailable. Maintain official artwork/typography.
- Hero card remains promotional, **More Info** only; no direct Play action.
- QA scenarios: first-run unwatched show with multiple episodes; started series with in-progress next episode; completing last available episode then importing immediate successor; imported historical/out-of-order episode; missing metadata; season boundary; Continue Watching coexistence; app relaunch; no arbitrary episode such as The O.C. S01E09.

## 24. Persistent watch history, backups, privacy setting and hero fallback (2026-10-10)
**Status: requirements and proposed fallback for next refinement; Settings placement/UX to review in upcoming walkthrough. Not yet implemented or tested.**
- Preserve **watched history independent of media-file presence and library inventory**. Example: user completed Daredevil S01E01–E03, deleted files, later imports S01E04: next-episode promotion must still work. Store stable TMDB/TVDB or equivalent series/episode identifiers with season/episode numbers, completed flag and optional last-watched timestamp. Do not rely solely on filename, path or local library row IDs. Preserve separate playback progress (position, duration, last played), even if source goes offline/removed; use safe metadata matching and migrations.
- Backups/restores must include compact identity-based watch history, progress and preferences, **not media files, artwork or secrets**. Restore matches by stable identity across fresh installs, moved files, different drives. Include versioned schema, collision/ambiguous-ID handling and user-visible restore results. Privacy: backups may reveal viewing habits; treat accordingly.
- Provide **Track Watch History** on/off setting with explicit explanation of effects on personalisation, next-episode hero promotion and history retention. Turning tracking OFF should stop adding new watched-history events, **not automatically erase existing records**. Separate Clear Watch History confirmation/action. Proposed: resume-position tracking remains independently configurable; confirm exact setting placement and interaction in Settings walkthrough.
- With history enabled and episodes 1–3 known complete, newly imported immediate next episode 4 may get episode-specific hero promotion; if episode 3 is in progress, Continue Watching takes precedence.
- With history disabled/unknown, new episode 4 can still be promoted as **recently added**, but **never claim it is next-to-watch**. When several episodes from a show arrive together, prefer one series-level promotion to avoid flooding carousel; where episode IDs are uncertain prefer series card. Re-enabling history records forward from then, no retrospective inference.
- Open implementation decisions: distinction between watched-history tracking and resume tracking; how long to retain history; whether backup history inclusion is default or optional; exact recently-added hero expiry/priority; whether explicit user-marked watched status is tracked while automatic tracking is off. Verify behaviour in actual Shield testing and prevent silent data loss.

## 25. Clarification — independent resume tracking and hero-to-Continue-Watching lifecycle (2026-10-10)
**Confirmed by user for next refinement; no code implementation yet.**
- **Remember Playback Position** must be independent of **Track Watch History**, enabled by default. Disabling long-term watched-history tracking must not disable resume positions. Back up both saved progress and relevant preferences; precise Settings UX to review later.
- If a user has completed episodes 1–3 (including when those media files were deleted), the newly available next episode 4 qualifies for **episode-specific hero promotion while it remains unstarted**. Do not move it to Continue Watching just because it exists.
- When playback of episode 4 actually begins and there is resumable progress, remove its hero promotion and surface it in **Continue Watching**. If it is completed, mark watched when history tracking is enabled; later next episode 5 may qualify when available.
- Eligibility to be promoted is not a guarantee of permanent on-screen hero placement: carousel capacity and ranking still apply, but unstarted next-episode promotion should not expire solely due to the user not playing it; expiry/priority details remain to be designed.
- If watched history is disabled, do not infer the user watched episodes 1–3. A newly imported episode may be eligible as **newly added**, without a “next episode” claim. Keep playback position functionality available independently.
- Test transitions after playback start, pause/exit, completed playback, deletion/reimport, fresh launch and backup/restore. Prevent the same in-progress item being simultaneously promoted in hero and Continue Watching.
