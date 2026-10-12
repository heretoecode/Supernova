# Supernova Details Card — upper details-page design direction (2026-10-12)

**Status:** User-approved direction for upper details page; exact dimensions, rendering and focus visuals provisional pending actual Android TV mockups/QA. Documentation only; no implementation authorised.

## Structure

- Replace current full-screen details artwork treatment with a **Details Card**, visually related to existing Home hero cards but substantially larger, set against the previously approved official Supernova page background.
- Shared global details-page system for movie, TV show, season and episode pages, adapting information and available actions to content type. Do not create divergent page-specific designs.
- Card has comfortable margins beneath top navigation, at left/right, and above lower details-page tabs.
- **Lower tabs (Details, Extra, More Like This, as applicable) remain visible in the initial viewport**, clearly suggesting further content below. Do not let card height push the tabs off-screen. Lower section design to be revisited separately; not approved here.
- Provisional reference only: horizontal margins around 4–5% each; navigation gap 24–36dp equivalent; card-to-tabs gap 24–32dp equivalent. No fixed final height or precise dp values approved.

## Proportional hero-to-details relationship

- Official title artwork/logo (user's 'official typography') retains native aspect ratio and the hero card's relative layout proportions. Example: if details design is 20% larger than hero, logo scales approximately 20% rather than arbitrary independent enlargement. Scale internal composition coherently, allowing accessibility/readability adjustments.
- Share title-artwork placement, safe area, cropping prevention, contrast, fallback, metadata and technical badge design **rules**, not necessarily identical dimensions or identical field sets.
- Use the previously approved global metadata/technical standards: age badge, year, genres where applicable, TMDB score/votes, context-specific Season/Episode counts and runtimes, verified technical capabilities, dynamic compaction. Preserve capitalised Season/Seasons/Episode/Episodes.

## Content inside Details Card

- Cinematic backdrop and official title artwork/logo.
- Primary metadata and separate technical-capability badge row when verified.
- Synopsis/description with readable line length and graceful overflow behaviour to be specified.
- Existing Play, Streaming (when applicable), More and other appropriate action buttons, within card.
- Focus **remains on individual buttons**. The card is not focusable/clickable and is not an additional D-pad stop.
- When focus is within card buttons, card itself gains a subtle **visual-only white or user-selected accent-colour outline/highlight** to signal active region. Button's own focus must remain distinct and clear. Card highlight fades when focus moves to lower details. Exact intensity and animation TBD; do not confuse visual region highlighting with real focus.

## Open QA/design decisions

- Validate actual TV viewport dimensions, margins, card height and tabs' persistent visibility, including small screens/scaling, long synopsis, long logos and missing metadata.
- Decide exact internal vertical placement and typography scales, highlight intensity, responsive behaviour, focus traversal from actions to lower tabs.
- Lower details section redesign remains a separate next topic.
- Do not change active branch or start Codex implementation without explicit authorisation.

## Approved refinement — lower tab labels integrated with divider

User clarifies the lower tabs **Details, Extras, More Like This** should sit directly on the existing horizontal divider line, rather than floating above it with a separate gap. **Keep the divider at its current vertical position** in the provisional layout; move the tab labels down to meet/integrate with that line. Use the recovered vertical space to enlarge the Details Card proportionately, preserving comfortable spacing between card and tabs. Keep the tabs visible in the initial viewport. The selected tab may visually connect to/interrupt the line with an understated active underline; precise tab styling remains subject to visual review. This supersedes earlier illustration where labels sat above a detached line. The upper-card direction is otherwise considered settled in principle; actual TV measurements and visual QA remain necessary.

## Proposed next-release tab focus styling — awaiting final choice on separator

User strongly prefers the application's **existing top-navigation underline focus treatment** over the former focus box and requests that the Details/Extras/More Like This tabs use the same **white underline when focused** in the next version. This is a next-release requirement, independent of completing the rest of the lower-half redesign. Mirror actual existing top-navigation semantics and animation rather than assuming a new focus model. Ensure selected tab remains identifiable when focus is elsewhere; distinguish selected vs focused state and validate D-pad navigation.

User asks whether to keep the full-width divider/separator now that tabs have underlines. **Design recommendation: remove the full-width separator entirely** for a cleaner, consistent look; keep tabs at the agreed vertical position, and don't allow removal to push tabs off-screen or change card height unexpectedly. This separator-removal choice is a recommendation **pending user's confirmation**, not an approved requirement. The prior divider-integrated-tabs illustration is superseded only if user approves removal.
