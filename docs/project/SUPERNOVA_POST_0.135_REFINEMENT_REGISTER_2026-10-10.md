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

## 26. Hero-card text contrast and tagline-versus-synopsis design review (2026-10-10)
**Evidence:** user-reuploaded 19-second Shield video IMG_4840(1).mp4 reviewed in conversation; raw video not committed to repository. Shows near-white American Horror Story hero artwork where white official typography and smaller overlaid information/description become almost unreadable, versus crisp white text on dark hero art. Other carousel examples visible. **Contrast defect confirmed; exact rendering technique and tagline switch remain design proposals pending user confirmation.**
- Maintain official title/logo artwork and established hero image; do **not** solve readability by restoring the previously rejected full-image/side-card gradient or fading side previews.
- Candidate treatment for app-rendered white metadata and descriptive text: restrained dark stroke/outline plus soft shadow, with legibility validation on very bright, mixed and dark imagery. Avoid chunky strokes or exaggerated halos. Where still insufficient, consider only minimal local backing immediately under text, not a broad gradient.
- Official supplied title-logo images may have white letters on pale backgrounds. They are not regular text: ordinary text-stroke logic won't automatically apply. Evaluate separate non-destructive local contrast treatment (e.g. subtle silhouette/drop shadow on transparent logo where technically possible) while preserving original artwork and brand appearance; do not pretend metadata text shadow alone fixes the logo.
- **Under discussion, not yet approved:** prefer official movie/series tagline over full synopsis for concise hero promotion, with a short two-line synopsis fallback when tagline missing; never fabricate tagline or substitute an episode synopsis for whole-show card. Preserve original typography and approved metadata order (year, genres, runtime or represented-season count).
- Shield QA: pale American Horror Story frame, dark Daredevil frame, varied mixed/bright art, adjacent side cards, readable metadata at TV viewing distance, no artwork obscuration, no focus/gradient regressions.

## 27. Final approvals — single-line hero tagline fallback and animated navigation underline (2026-10-10)
**User-approved for next refinement release; supersedes tentative/proposed status in items 4 and 26. No implementation yet.**
- **Hero copy:** prefer actual metadata tagline for the promoted movie/series/episode where appropriate. Render in **exactly one visual line** within a sensible fixed/max text width; do not wrap or grow the hero card. If tagline is absent, show synopsis/description in **exactly one line** with end ellipsis (…) when truncated. Avoid horizontal marquee/scroll and avoid fabricated taglines. More Info opens full description and details. Apply same one-line layout on all hero types, with sensible truncation at TV viewing distance. If no tagline and no description, omit copy without an ugly placeholder.
- **Top navigation:** replace rectangular focus highlight with the previously proposed **animated accent-colour underline** in the next release. Underline tracks focused label/icon width and smoothly moves/resizes across items, without jump/flicker; Codex may tune timing/easing to fit Supernova. Respect accent selection, distinguish selected page versus current remote focus, preserve visible D-pad accessibility and avoid regressions.
- Contrast improvement from item 26 remains an **approved problem to fix**, but exact stroke/shadow/logo treatment should be validated visually on Shield rather than assuming one algorithm fits both raster title logos and app-rendered text.

## 28. Approved watch-history retention, configurable backup selection and deferred contrast QA (2026-10-10)
**User-approved for next refinement handover; not yet implemented. Supersedes unresolved retention/default-backup questions in item 24.**
- Retain compact, identity-based watched history **indefinitely until explicit user deletion** (including records for files removed from the library); no time-based automatic expiry. Disabling Track Watch History pauses new watched-history collection without erasing previous history. Clear Watch History must be a separate explicitly confirmed action, with scope and consequences explained. Avoid accidental deletion during rescans, library rebuilds, source disconnection or backup restoration.
- Backup configuration must offer **independent opt-in/out checkboxes for Watch History and Playback Resume Positions**, both **selected by default**. Preserve the existing configurable-backup approach and any other selectable categories. Choosing to exclude a category from a particular backup must **not** alter the corresponding in-app tracking preference or erase live data. Persist backup preferences where appropriate; clearly describe what will and won't be restored.
- Store stable media identifiers and episode numbers for history, and resume timestamps/position as appropriate; versioned restore should handle unmatched/ambiguous IDs conservatively.
- Hero text/logo readability contrast work remains in next release scope; **do not request user visual validation before implementation**. Validate on Shield only once Codex has produced the updated APK, comparing bright American Horror Story and dark Daredevil examples. Exact contrast technique remains implementation-level design judgment subject to QA.
- Next-episode hero ranking recommendation remains a proposal unless explicitly approved; put.io OAuth still has external configuration dependency.

## 29. Hero carousel ranking — next available episodes take priority (approved 2026-10-10)
**User-approved for next refinement release; not yet implemented. Supersedes pending ranking decision in items 24 and 28.**
- Newly available **next-to-watch episodes** whose eligibility is established by reliable retained watch history rank **ahead of ordinary whole-series recommendations** in the Home hero carousel.
- Preserve some carousel variety: don't let a large batch of qualifying next episodes permanently crowd out movies and unstarted series. Codex may implement reasonable deterministic ranking/deduplication and must test multi-show imports and carousel capacity.
- An eligible next episode remains promotable while **unstarted**; once playback starts, remove episode promotion and place it in Continue Watching with resumable position. Completed episodes are not promoted. With watch-history tracking disabled or unavailable, don't label a newly imported episode “next to watch”; it may still qualify as newly added content per prior rules.
- Preserve approved metadata order, official artwork, one-line tagline/synopsis fallback, More Info action and text contrast requirements.

## 30. Settings QA — Open Source focus crash and diagnostic export (2026-10-10)
**Observed by user on installed 0.135 Shield APK. Next-refinement bug and usability requirements; no code changes yet.**
- **High-priority stability regression:** Settings > About > indented **Open Source** (Open Source Licences): merely leaving D-pad focus on this entry for a period causes UI to freeze/hang, eventually exits to Android TV launcher. Investigate on-device reproduction and diagnostic log for main-thread stall/ANR, crash, memory exhaustion, unexpected licence-loading work on focus. Focus should not eagerly run expensive licence parsing/rendering; opening licences should occur on explicit OK, be responsive and cancellable, and never crash. Verify resting focus, moving focus away, opening licences and repeated visits.
- **Diagnostic export observed working:** Settings > Diagnostics > Export Diagnostic Report created a non-zero file with content. Preserve. This alone does **not** verify that reporting was improved in version 0.135; compare implementation/history and report content before claiming improvements.
- **Approved filename convention:** default export name **Supernova Diagnostics - YYYY-MM-DD HH-mm** using actual local export date/time, with the correct extension for the real format (e.g. .zip or .txt). Choose filesystem-safe separators; avoid overwrite/collision on repeated exports (seconds or unique suffix as needed). Ensure the chooser and resulting filename agree.
- If the user shares exported diagnostics, inspect for Open Source focus freeze/ANR/crash evidence and relevant timestamps. No diagnostic report contents have been supplied yet in this walkthrough.

## 31. Timestamped reproduction — Open Source Licences freeze/crash (2026-10-10)
**Observed directly by user on Shield, Supernova 0.135; diagnostic logging enabled.**
- **2026-10-10 17:14 Dublin local time (Europe/Dublin, UTC+01:00):** Settings > About > **Open Source** selected. UI froze immediately/as expected; after a delay the Supernova app fully closed to Android TV launcher. User relaunched Supernova successfully and it resumed normally. This is a reproduced instance of the issue in item 30, not yet a diagnosed root cause.
- User will continue testing with diagnostic logging enabled and will supply a later export. **On receipt, correlate around 17:14 local (16:14 UTC if logs are UTC), through the eventual app termination**, and investigate ANR, fatal exception, OOM or main-thread work on licences. Exact app termination minute not separately recorded.
- User intends to provide timestamps and verbal descriptions for subsequent issues for correlation in that exported report. Do not claim logs were examined before report is actually provided.

## 32. Settings comprehensive Shield walkthrough — responsive panels, native controls, nested navigation (2026-10-10)
**User walkthrough and visual reference:** attached IMG_4843.mp4 (46.57s) visibly covers About, licence-related navigation/crash/relaunch and Release Notes; user mentions a second longer video, but it was not available with this message, so do not claim it was inspected. User's verbal walkthrough covers the rest. **Next release requirements, not authorization to implement now.** Preserve existing app theme and focus conventions.

### Global Settings structure
- Left nav: keep all category names on one line, especially **Library and Metadata**; reduce oversized item height/padding and excessive inter-item spacing. Aim to fit all standard categories without left-panel scrolling on Shield, not an absolute guarantee for accessibility/expanded sections. On expanding nested items, subtly compress space around other items while preserving readable indent/spacing, stable focus and animations; scroll only when necessary.
- Add distinct section headings/dividers in centre panel, with breathing room between heading and options, across Playback/Video/Audio/etc.
- Adaptive widths: left remains stable, centre expands when right only shows short help text; right narrows accordingly. Centre should not truncate labels such as **Enable Automatic Refresh Rate Switch** or **Library and Metadata**. For suitable overview screens, merge centre+right into a single wide structured panel, no floating text. Preserve top-nav clearance and smooth resizing without focus jumps.
- Centre shows **option label and actual control/value only**, not duplicate descriptive subtext when description already appears on right. Example Enable Projector Mode: description only in right. Right shows **Information** heading/divider and padded description/current value, lower than current top-aligned text. Make wording concise and accessible.

### Replace legacy modal/context menus with Settings panels
- Playback toggle behaviour generally correct. Video **Enable Automatic Refresh Rate Switch** should be a direct on/off toggle in centre, not popup (current popup Disabled vs truncated 'Display mode selection matching video F...'; preserve actual refresh-rate matching functionality).
- **Dolby Vision Mode** is multi-choice (Auto, Disable, Dolby Vision, Force Dolby Vision): expose choices inline below the setting in centre or as selectable options in right, not popup; retain precise underlying semantics.
- Audio **Audio Pass-Through** multi-choice (Disabled, Recommended, additional existing options): expose in panel, not popup.
- Rename **Audio Preferred Language** to **Preferred Audio Language**. Language choices on right panel with clear selected/tick state, not popup. Similarly **Subtitle Reading Language**, **Subtitle Download Language**, **Appearance > User Interface Language**. For single-choice preferences use radio/check selected state rather than mistakenly enabling multiple languages unless existing feature supports multi-select.
- Comprehensive locale/language indicator coverage: inspect available Android locale APIs and language names; flags are not intrinsically equivalent to languages (many languages span countries). Provide accurate flags only when locale has an unambiguous country/region and high-quality assets; otherwise use a neutral language glyph/locale code, never inaccurate guessed flag. Maintain full D-pad navigation and long-list handling.
- Streaming **My Providers** currently opens popup; move provider selection into right panel; preserve coloured provider icons and selected state. **Separate user video pending** for minor visual issue; don't finalise that specific visual fix until reviewed.
- IntroDB Intro/Recap/Outro/Credits multi-choice behaviours (Play Normally, Show Skip [5 seconds], Auto Skip, Smart as applicable) should be panel-based, not popup; **do not misrepresent these multi-choice modes as simple boolean toggles**. Preview currently 'Smart' looked correct; preserve. Centre labels should be wider.
- Advanced > Audio Compatibility > **Choose Audio Decoder**, Subtitle Compatibility > **Subtitle Encoding**: replace context menus with inline/right-panel choices. Preserve other existing settings/values.

### Nested Integrations, Advanced and About
- Clear disclosure chevron/indicator on parents that expand indented items (Integrations, Advanced, About); expand only on click, not hover. When moving from child to another parent and back, ensure child focus **always** restores its matching centre content. Repro: expand Integrations, navigate OpenSubtitles > IntroDB > Trakt, move down to Advanced, move back up to Trakt/IntroDB/OpenSubtitles; centre incorrectly remains stale/blank until returning to Integrations and moving down again.
- **Integrations parent overview:** on left focus Integrations (before expansion), centre lists **Trakt, IntroDB, OpenSubtitles**, each independently enabled by default with toggle; right gives focused integration's benefits and consequences of disabling. Disabled integrations disappear from left nested submenu; preserve credentials/preferences on disable, and restore entry on re-enable. Handle focus if current child becomes disabled.
- **OpenSubtitles child:** no redundant 'OpenSubtitles Credentials' modal chain; show Username, Password, Show Password and save/auth status directly in centre (and/or adaptive right); use Supernova global keyboard design, potentially smaller integrated variant, while preserving secure password handling, remote focus and privacy. Design exact keyboard placement to be validated; don't force Android keyboard popup if avoidable.
- **Advanced parent overview:** currently empty headers/placeholders; user favours a single merged centre/right panel with concise explanation/caution for advanced options. Keep actual nested Video Compatibility, Audio Compatibility, Network Compatibility, Subtitle Compatibility, Diagnostics working and discoverable. No redundant empty placeholder.
- **About parent overview:** preserve existing Supernova branding/version/build data but place inside one merged wide structured panel (currently floats with panels absent). Remove duplicate nested **App Information** because it repeats About overview.
- **Technical Information** and **Credits & Acknowledgements**: content works but floats; restore structured content panels.
- **Release Notes**: restore panels; centre only release version number/date and concise build identifiers, right only the selected version's release notes (no duplicated nested release-note paragraphs in centre). Video confirms awkward floating/duplicated layout. Preserve navigable list and readable notes.
- **Open Source Licences** remains a high-priority reproducible focus/open crash, item 31 at 17:14 Dublin local. Avoid triggering during review until fixed; preserve proper licences access in final release.

### Follow-ups
- User will provide separate **My Providers** video for icon/visual issue.
- User reports **two Settings videos**, but only IMG_4843.mp4 was accessible in this turn; request missing longer clip to visually validate remaining screens.
- Diagnostic logging on; later export may allow time-correlated investigation.

## 33. Settings walkthrough final sign-off — My Providers and D-pad focus (2026-10-10)
**User-approved requirements for next refinement; no implementation authorized yet.**
- **Streaming > My Providers:** replace legacy context menu with provider selection in the **right-hand Settings panel**. Preserve full-colour provider icons, selectable ticks, and two groups: **Selected Providers** pinned at top and **Other Providers** scrolling below. Keep selected header visible even when empty; use unobtrusive 'No providers selected' placeholder. Remove **Done**; selection changes commit immediately, persist across navigation and app restart.
- On selecting provider in Other Providers, smoothly move it into Selected Providers, with checked state. **Focus transfers to the next provider in Other Providers** (or nearest remaining item if last), allowing continuous selection; ensure focused item stays visible. On deselecting in Selected Providers, move it back into Other Providers at its normal stable/alphabetical position, with sensible focus retention/transfer to avoid disappearing focus. No abrupt list jumps, duplicate entries, lost focus or extra confirmation. Animate efficiently on Shield; respect reduced-motion if applicable.
- **Settings persistence QA:** user checked harmless settings and reports they save/retain correctly. Preserve and regression-test; do not infer every setting tested.
- **D-pad boundary:** from topmost item in left Settings navigation, Up may enter top navigation (expected). At bottom of left, Down stays in left. On centre and right panels, Up/Down remain within that panel even at top/bottom; **Up from first centre setting (e.g. Playback > Enable Projector Mode) must NOT jump to top nav**. User must return Left to left navigation, then Up from top item to reach header. Ensure reliable cross-panel focus memory and no traps.
- User considers **Settings walkthrough complete**. Outstanding separate work: first-run onboarding walkthrough; later diagnostic export and crash-log analysis. Avoid inventing new Settings questions unless a genuine blocker emerges.

## 34. Screenshot-confirmed foundational Settings panel hierarchy (2026-10-10)
**User-approved clarification for next refinement; screenshot IMG_06B06E7B-83D8-4033-8552-84F5C38EE3BE.jpeg, Settings > Playback on Shield. Supersedes inconsistent page-by-page treatments; no coding authorized yet.**
- **Never duplicate descriptive/help text in the centre Settings panel.** Centre contains setting names, their interactive controls and concise values; muted secondary descriptions underneath setting labels are removed **throughout Settings**. Right Information panel is the sole home for the focused setting's explanatory text, plus current value where useful.
- **Responsive widths are foundational:** centre must expand significantly when right contains only explanatory text; keep long setting labels readable, ideally one line, without cramped controls. Right can be narrower but must remain legible. Left navigation remains structurally stable. Screenshot shows narrow centre, wrapping labels and overly wide mostly-empty right.
- **Simplify container nesting:** screenshot shows outer rounded panel, an extra large blue inner content box, and repeated boxed settings; avoid stacked decorative blue rectangles. Use a single clear panel surface, consistent heading/divider, lightweight rows and subtle separators/spacing. Reserve a clear accent/outline focus treatment for the *currently focused* row, without persistent heavy boxes around every item. Preserve Supernova visual identity, rounded corners and accessibility contrast.
- **Right panel layout:** consistent **Information** heading, separator, sensible vertical padding below heading, description then current value/status where applicable. Not crammed at top nor vertically centred; no redundant page title and 'select a setting' placeholders when useful content exists.
- Apply these foundation rules across all relevant Settings categories and nested pages, not just Playback. Test on Shield at normal TV viewing distance, including Projector Mode, Enable Automatic Refresh Rate Switch, long audio/subtitle labels, and D-pad focus.

## 35. First-run onboarding QA — local-only shared browser and no preselection scanning (2026-10-10)
**User recorded fresh launch after clearing app data; video IMG_4845.mp4 (45.83s) reviewed via sampled frames. Approved direction for next refinement, not implementation authorization.**
- On first launch with no library folders selected, **do not start library/media scanning, metadata indexing or network scanning**. Only enumerate accessible local storage devices/folders as needed for the chooser. The bottom-right **Scanning Local Library** status pill is inappropriate unless an actual user-authorised scan is running; never fake progress or run an initial scan before selection. Once folders are explicitly selected/confirmed, scan those configured roots only and show accurate progress.
- Empty-library **Build Your Library** onboarding uses the **shared foundational file/folder browser**, but **NOT the entire Network & Files page**. Simplify first-run left navigation to local storage devices/folders only; omit Library Health, Network Scanning, Cloud Services, Discover Devices, Add Network Source, Network Shares and other unrelated management functions. These remain available in normal Network & Files later. Avoid copying unrelated Overview/status screens into onboarding.
- Reuse common file-browser component, consistent visuals, selected-folder affordances and correct D-pad focus. Video demonstrates inherited left/centre focus problems; fix at shared browser layer and regression-test both onboarding and Network & Files. Preserve breadcrumb/navigation and avoid accidental library scan on mere focus.
- Video captures navigation to Network & Files/Overview, local-storage selection and Library Health after clearing data; do not mistake that full-page view for approved first-run setup. Further onboarding walkthrough may supply additional details.

## 36. First-run Build Your Library introductory guidance (2026-10-10)
**User-approved direction for next refinement; no code changes yet.**
- Retain heading **Build Your Library**, but add concise, readable explanatory text immediately above the simplified local-storage chooser. Explain that users can start by selecting local folders containing movies/TV shows, and that broader network/cloud media options are available in **Network & Files**.
- Suggested user-facing copy:
  **Build Your Library**
  “Get started by choosing the folders containing your movies and TV shows from your local storage devices.”
  “Want to add media from your network or cloud services? Visit Network & Files for more options.”
- Use existing Supernova typography, comfortable TV-distance line lengths and clear hierarchy; avoid unnecessary popup, extra wizard step or overexplaining internal shared-browser implementation. Ensure the wording reflects actual available options and the chooser remains prominent.

## 37. Multi-drive configuration transaction and confirmed pre-onboarding scan (2026-10-10)
**User observed on Shield 0.135; next-refinement requirement; no code authorized yet.**
- **Unsaved folder selection across drives:** user configured Backup Drive 2 then attempted to browse Backup Drive 1; current UI warns about unsaved changes upon merely leaving the first drive. Instead, treat the whole folder-selection workflow as **one pending configuration session spanning multiple drives**. Retain staged folder choices while navigating between devices/folders, without warning on drive changes. Provide a clear Save Changes/finish action to commit all staged roots together. On leaving the *configuration workflow* (including switching to unrelated Network & Files functions or leaving the page) with pending changes, show **Save / Discard / Continue Editing**; don't rely only on page-exit warning. Preserve staged selections and focus on Continue Editing, restore original choices on Discard. Avoid partial saves or duplicate scan triggers. Use same principle in onboarding local chooser and normal Network & Files folder management.
- **P0/P1 first-run scan confirmed real, not merely status pill:** after user cleared app data and first-run Build Your Library appeared, Supernova **actually scanned/indexed media before user had selected/confirmed library roots**. Within minutes hero carousel and Home rows filled with media; **onboarding screen vanished**. Must investigate legacy default scan roots/startup indexing/background jobs/source migration and disable unapproved scanning on fresh setup. Device enumeration for folder chooser is permitted; library media scan is not.
- **Onboarding completion state:** explicitly record successful first-run library configuration/intentional completion; **do not infer onboarding completion from media being indexed or Home becoming non-empty**. Preserve onboarding until user completes or intentionally skips setup. No automatic scan or hero/row population based on unapproved default roots; only begin configured-root scan after explicit Save/Finish. Test fresh app-data reset with connected drives, waiting several minutes, switching drives, saving and leaving without saving.

## 38. Onboarding browser space usage — flat local device navigation (2026-10-10)
**User requested final clarification; recommendation documented for next refinement, not authorization to implement.**
- The first-run **Build Your Library** introduction is the landing/overview guidance. **Recommended: omit redundant Overview navigation item** inside the simplified browser entirely; do not show an empty Overview with Grid/Sort/All Files controls and irrelevant right-panel scan/network/library-health information.
- Onboarding left panel should be a non-selectable **Local Storage** section heading followed by **dynamically enumerated drives directly underneath**, e.g. Internal Storage, Backup Drive 1, Backup Drive 2. **No parent expand/collapse or indentation needed in onboarding**; unlike the full Network & Files view. Keep device names single-line, good spacing, compact enough to use available vertical space.
- Focusing/selecting a drive populates centre with its folders and appropriate selection/breadcrumb controls; right panel with relevant drive/folder details or focused-folder guidance. Use expanded centre space when right contains little. Do not show generic Grid/Sort/All Files in any onboarding overview; show browser controls only if useful inside actual folder browser.
- Remove all Network & Files-only functions from onboarding including Library Health, network scanning, cloud, discovery, network shares/add network source, and their overview/status actions. Preserve their availability in full Network & Files. No automatic media scan before folder selection/Save, per items 35 and 37.
- Preserve multi-drive staged folder selections and shared D-pad focus fixes. Avoid copying legacy nested local-storage indentation into onboarding.

## 39. Approved three-panel foundation from Build Your Library mock-up (2026-10-10)
The user approved the shared visual hierarchy, including left chevrons and dividers, centre row dividers and checkbox folder selection, and right Information heading/labelled details/status. **Authoritative specification:** `docs/design/shared-three-panel-framework/SHARED_FRAMEWORK_BROWSER_0.135.md`, section “Approved shared three-panel visual hierarchy — onboarding mock-up”. Flexible widths/merged centre-right layouts remain permitted. No implementation yet.

## 40. Diagnostic ZIP audit — export succeeds but contains no event evidence (2026-10-10)
**Source:** user-uploaded `Supernova-Diagnostics-1791654975678.zip`; inspected all six entries (build-device.json, decoders.txt, README.txt, summary.txt, manifest.json, coverage.txt). **No code changes or Codex handover authorised.**
- Report identifies app **0.135**, source commit `cfc2ca2376fb77f79a85a3a5e45333d38b9689e7`, NVIDIA SHIELD Android TV Android 11; export includes populated build/device details and decoder list.
- `build-device.json`: **`logging_enabled: false` at export**, contrary to user's observation that diagnostic logging was on during testing. Investigate UI toggle persistence, actual logger state, whether crash/relaunch resets logging, and whether exported state is accurate. Do not assume the exact cause.
- `manifest.json`: schema 3, `completeness: PARTIAL`, `first_utc_ms: 0`, `last_utc_ms: 0`, `processes: 0`, `event_counts: {}`, `incidents: []`, `manual_reports: []`, `launches_retained: 0`. `summary.txt` confirms no retained sessions/events. `coverage.txt`: `Observed event types: []`, dropped 0, write errors 0.
- README describes intended structured UTC/monotonic timestamps, process/playback session IDs, bounded event retention and incident queue, but **this export cannot validate performance monitoring, memory leak detection, ANR capture, crash capture or playback-session recording**. Device and decoder export work; event instrumentation/retention are unverified.
- The known **17:14 Dublin local / 16:14 UTC Open Source Licences freeze and app exit** cannot be diagnosed from this ZIP: no event records or crash evidence. Correlate once actual logs exist; ensure crash/ANR breadcrumbs survive process death and appear in subsequent export, with privacy safeguards. Android system logs may be needed for ANRs.
- Priority next-version fix: logging enabled state accurately reflected in UI and export, events persist across crash/relaunch, basic startup/navigation/operations recorded, explicit health/coverage diagnostics make missing event collection obvious. Test on physical Shield with logging enabled, reproduce Open Source crash, relaunch and export, verify timestamped evidence. Distinguish no data from no issues.

## 41. Second diagnostic export — confirmed Open-source Licences focus then unclean exit (2026-10-10)
**Evidence:** user-uploaded `Supernova-Diagnostics-1791655429469.zip`, 0.135, SHIELD Android 11; diagnostic logging explicitly **true**. Previous empty export explained by user clearing app data for onboarding QA, which also erased logs; do not treat prior disabled logging as a persistence defect without independent reproduction. **Documentation only; no coding or Codex handover.**
- ZIP contains `events.jsonl` (353 parsed event records), `important-20261010.jsonl`, manifest, summary, coverage, device and decoder details. Two process IDs; one `PREVIOUS_SESSION_UNCLEAN_EXIT`, zero captured `incidents` and no exception/ANR stack. `completeness: PARTIAL`; dropped events 0, write errors 0.
- At **19:03:05.826 Dublin / 18:03:05.826 UTC**, focus moves to About > App Information; **19:03:06.325** Release Notes; **19:03:06.829** focus moves to **Open-source Licences**, followed by focus_navigation at **19:03:06.916**. Last event in that process at **19:03:07.721** (indexed library load completed); next startup **19:03:35.582** records `PREVIOUS_SESSION_UNCLEAN_EXIT` for preceding process. This strongly correlates focus on Open-source Licences with abnormal termination but **does not prove exact code-level cause**. No captured click required; user says focus itself causes crash.
- Diagnostics implementation positively demonstrated: focus/focus_navigation, lifecycle, app sessions, startup, library scan progress, artwork timings, metadata HTTP outcomes, operation durations, UI rebuilds, one heap/queue heartbeat and cross-process unclean-exit detection. Single heartbeat reports Java heap used 26,375,720 bytes, native heap 229,123,776 bytes, heap max 536,870,912 bytes — **one sample cannot establish leak or memory safety**. No proof of sustained memory leak analysis, ANR stack capture or full native transport instrumentation.
- Notable scan load: two concurrent retained incremental local-import scan_indexing operations, one reaching 290,001 checked rows and another 66,521 checked rows; technical rows checked are **not distinct media items**. Investigate possible scan contention/performance only with broader evidence. 14 artwork requests with 0 recorded failures. No playback sessions, so playback telemetry not validated.
- Priority bug: investigate Open-source Licences focus-triggered freeze/crash using Settings focus/render/preview handlers and Android logcat/ANR traces around 18:03:06–18:03:35 UTC. Improve automatic capture of Java fatal exceptions and ANR breadcrumbs/stacks where platform permits; ensure preserved after process restart, avoid exposing private paths/tokens. Validate with physical SHIELD. Do not assert root cause from breadcrumbs alone.

## 42. Approved direction: developer diagnostics, defaults and focus/performance monitoring (2026-10-10)
**User request:** strengthen crash traces, long-duration memory/performance monitoring and focus/UI fault detection; **diagnostics ON by default during development/QA, OFF by default for eventual public releases**. Documentation only; no implementation/handover.
- Build-flavour-aware defaults: development/QA auto-enable diagnostic collection from first launch, including after app-data reset. Public release defaults detailed diagnostics off with explicit user opt-in; always provide manual override and export, no automatic upload. Avoid sensitive paths, filenames, account tokens and credentials. Prefer 14-day bounded rolling retention as a proposal subject to size/overhead validation; crash evidence survives process death/relaunch.
- Capture uncaught Java/Kotlin exception stacks, thread/foreground-screen/focus context and bounded navigation breadcrumbs; distinguish Java exceptions, native crashes, ANRs, watchdog-detected main-thread stalls and low-memory/process kills. Persist recent context safely and correlate next-launch ApplicationExitInfo when supported; watchdog is heuristic and should not claim definitive ANR without OS evidence. Collect stack traces where allowed.
- Lightweight sampling proposal: memory/CPU/main-thread responsiveness about every 10 seconds, aggregate summaries about every 60 seconds, and event-triggered samples around screens, scans, playback and stalls. Track Java/native/PSS where available, GC and sustained growth after repeated operations; compare builds/sessions; **do not label rising memory as a leak without controlled evidence**. Bound overhead, retention and privacy.
- Focus/UI instrumentation: D-pad event to expected focus transition latency; detect missing focus, repeated unhandled navigation, surprising panel jumps, off-screen/occluded focused views, focus restoration after back/playback, geometry clipping and selected layout overlap when observable. Flag **suspected** focus issues with screen, item IDs, bounds, action and breadcrumbs, avoiding false positives at list boundaries. Pure visual blur/colour/font defects still require screenshots/video.
- Validate on real SHIELD, including Open-source Licences focus crash, Settings panels, Details return focus and onboarding. Existing 0.135 diagnostics already capture focus/lifecycle and one heap snapshot, but **do not yet demonstrate automated anomaly detection or sustained memory-leak analysis**.
