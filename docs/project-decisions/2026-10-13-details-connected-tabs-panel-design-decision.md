# Supernova — connected tabs and framed panel: owner design decision
Date: 2026-10-13
Status: **Owner-approved design direction and proportional margin baseline; not yet implementation-approved or pixel-perfect design.**
Applies to the Details screen's tab strip and shared content panel (Movies and TV), including Details, Extras, More Like This, and Seasons & Episodes where applicable.

## Core concept
Replace the current separate underline indicating the selected/focused tab with a **single continuous connected tab-and-content-panel outline**. The border of the main content frame runs along its top edge, rises to surround the active tab label (over its top and around its sides), and returns to the main panel's top border. Visually the active tab is physically part of the panel frame, not a detached underlined label. The border then continues down both sides and across the bottom of the content region.

Only the active tab has the raised outlined cap. Other tab labels remain legible but do not have that active outline. D-pad Left/Right moves between tab headings, and the raised outline follows the active tab; the content changes inside the frame. **The panel's outer position and size do not jump when tabs change.** No separate underline beneath the active tab. Avoid flashing, focus duplication, or content shifting. Soft-white/thin monochrome border and slightly squared corners consistent with Menu Foundation are **suggested visual treatments**, not separately owner-locked numeric specifications.

## Owner-approved proportional margins
The frame **must NOT reach the left, right or bottom screen edges**. It should be comfortably inset and balanced on all three sides. Owner explicitly approved **equal margin percentages**:
- Left: **4% of usable screen width**
- Right: **4% of usable screen width**
- Bottom: **4% of usable screen height**
- Top: tab row is positioned **below the existing Details card** with comfortable spacing; top is not subject to the same 4% rule because it depends on the card's location.

At a nominal 1920×1080 screen this corresponds approximately to left/right **76.8 px** and bottom **43.2 px**, before any TV safe-area adjustments. These are **equal percentages, not equal physical pixel distances**. Actual implementation should respect the app's TV safe area, density/scaling and existing content margins, and use consistent layout calculations. Usable viewport definition, pixel rounding, minimum/maximum dp clamps and exact corner radius/border thickness remain to be resolved from source/UI Atlas investigation; do not silently replace the approved 4% baseline with edge-to-edge width or an unrelated fixed margin.

The main content frame spans the width **between** the two side margins and extends downward to the bottom inset, not to the display edge. Frame height is the space available below the card/tab strip to that bottom inset. The earlier assistant suggestion of 90% of remaining vertical space or 5% bottom margin is **superseded** by the owner's 4% three-side baseline.

## Navigation and content interaction
The Details tab is **passive and read-only**. Its information content is non-focusable, non-scrollable, and fits in a single fixed viewport. Focus remains on the Details tab title; Left/Right switches tab titles; Up returns to the Details card/actions; Down does not enter Details content; OK does not activate information. This is the owner-approved **target**, not a claim about current code.

Other tabs (Extras, Seasons & Episodes, More Like This) may contain interactive media; design must preserve selected-tab identification when D-pad focus moves into those tabs' content. The exact selected-versus-focused outline states and transitions for those interactive tabs require a later explicit design/behaviour decision; do not assume the border itself must track a poster/card focus target. Preserve approved tab ordering and existing 0.139 navigation/focus fixes.

## Content preservation and scope
The framed panel is shared tab chrome; this **does not constitute approval of a redesigned Details information layout**. Preserve existing content requirements: full synopsis within Details tab, up to eight cast and eight crew, Key Information, Reception, Technical Information. Owner's genuine Shield photograph of Dune: Part Two illustrates cramped/truncated current UI but is not stored in GitHub. Do not delete fields, hide technical information, reduce text to unreadable sizes, add scrolling or focusable Details elements to satisfy layout constraints without owner discussion.

The comprehensive forensic source/UI audit has been commissioned in `docs/project-decisions/2026-10-13-details-tab-forensic-source-and-ui-audit-brief.md`. Owner intends to **wait for the currently running Codex backup/restore investigation to finish**, then give Codex the Details audit prompt, not start another Codex instance. Audit first, then source-grounded visual design and separate approval; no coding/build/merge authorisation from this document.

## Implementation verification criteria for a later approved task
1. Active tab outline is continuous with content frame, including top border interruption/raised tab shape, sides and bottom.
2. No legacy active underline remains; only one active outlined tab.
3. Frame has 4% usable-width side margins and 4% usable-height bottom margin, with appropriate TV safe-area handling.
4. Main panel size and position remain stable while switching tabs.
5. Details tab content is passive, non-focusable, non-scrollable; D-pad focus contract holds.
6. Interactive tabs maintain legible selected state when their content has focus; exact style to be approved.
7. Movie and TV variants, missing tabs, different resolutions and scaling remain usable without clipped outlines or inaccessible controls.
8. Do not imply the source audit or final UI implementation has already occurred.

Related decisions: `docs/project-decisions/2026-10-13-supernova-0139-consolidated-handover-scope.md`; `docs/project-decisions/CURRENT_ROADMAP_AND_DECISIONS.md`.
