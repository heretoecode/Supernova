# 10 — Existing shared components and proposed design system

## What already exists

**VERIFIED:** Supernova uses XML and programmatic Android Views, Leanback, RecyclerView and preference adapters; no Compose design system was found in the frozen app. Shared components are real, but many semantic roles are implicit and dimensions/fonts remain passed as literals. The [consumer inventory](evidence/shared-component-consumers.tsv) is a direct textual reference map, not runtime impact proof; XML/JNI/reflection and indirect wrappers require additional review.

| Actual component | Existing role and variants | Consumers / impact / exception |
|---|---|---|
| SharedThreePanel | Panel geometry/decorate/text/heading/divider/action/accentAction/control/focus | Settings, universal browser and Health; changing heading will not change separate text(...,21) or XML category styles |
| PreviewDialog | choose/read/review, sizing/surfaces, window/modal bookkeeping and opener restoration | Many editors/confirmations; native confirmDelete/confirmPageDelete remain distinct destructive flows |
| PreviewKeyboard + KeyboardInput + PreviewTextInput | Shared key grid, Caps/backspace/Space/Clear/Done/Cancel, D-pad nearest key and editable-field behavior | Search/editors/credential wrappers; use context configuration/masking, not a new keyboard implementation |
| PreviewAccent | Color choice and focused/control states | Programmatic utility surfaces and card/focus components; literal whites and old XML themes remain exceptions |
| PreviewFocusRecycler / PreviewFocusRail / ContentFocus | Child navigation, remembered focus and scroll behavior | Library/file grids and rails; do not consolidate incompatible keyboard/menu boundary rules blindly |
| TopNavigation | Shared brand/time/tab/scan composition and first-row Up/Down boundary | Main TV workspace; Details/playback own separate chrome |
| LibraryPageHeader / PreviewLibraryColumns / Toolbar | Library title/stats, density/grid columns/actions | Movies/TV/custom renderer already shares structure |
| PreviewCardPresenter / LandscapeCard / FeaturedCard | Poster/category/continue/hero variants | Shared imagery and local focus zoom; scene-specific shape and action semantics remain |
| TvLayout / viewport helper | Logical density, available width and narrow-width overflow | Preserve text-scale/720p resilience and focus reveal |
| PreviewFrost / Backdrop / UtilityBackground | Bitmap blur/artwork/surface treatment | Do not assume decorative effect is free or unused; allocations need profiling |
| XML preview_preference(_category) | Category/row labels and widget presentation | Different font/19sp medium category than SharedThreePanel 21sp light heading |
| FoundationAboutWorkspace / notice reader | Nested About/technical/release/legal information | Reader paging and scroll focus are specialized semantics, not generic action-list behavior |
| PlayerController / TV menus | HUD transport, seek preview, secondary menus and timestamp | Specialized timing/auto-hide/input semantics should share tokens without inheriting Settings navigation |

Source: [SharedThreePanel.java:62](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/SharedThreePanel.java#L62), [PreviewDialog.java:160](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/PreviewDialog.java#L160), [PreviewKeyboard.java:24](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/PreviewKeyboard.java#L24), [KeyboardInput.java:11](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/KeyboardInput.java#L11), [preview_preference_category.xml:1](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/res/layout/preview_preference_category.xml#L1), [PreviewFrost.java:14](https://github.com/heretoecode/Supernova/blob/b3504c2c4b1725f3070416d14b63e53ae1db3d74/src/main/java/com/archos/mediacenter/video/leanback/PreviewFrost.java#L14).

## Existing token facts

**VERIFIED:** SharedThreePanel text uses white sans-serif-light and line spacing3dp; heading21sp; action18sp/minHeight50dp/padding8dp. Decorated panel padding20dp/radius16dp/stroke1dp/elevation4dp; default gap16dp. Focus surface is translucent with radius10dp and white/accent1dp outline; permanent control has explicit disabled/pressed/focused/rest states and radius constrained10–12dp. XML preference category uses 19sp/sans-serif-medium/#C9E6F6/42dp height/padding14dp. These are local helpers/styles, not a universal global token API. Existing `accentAction` and `control` have different rest/focus roles; “looks similar” does not prove semantic interchangeability.

## PROPOSED structure and semantic roles

Use an additive source-independent catalogue before any code migration:

1. **Token definitions:** typography family/weight/size and scaling rules; surface/text/status/accent colors; spacing/insets/radii/strokes/elevation; focus outline/zoom/glow; animation durations and reduction rules; density/viewport/accessibility. Define intent and consumers, including explicit exceptions.
2. **Semantic component contracts:** panel title, selected-context title, section title, body/help/supporting/status/error text, static fact label/value, navigation row, checkbox/list choice, primary/secondary/destructive action, editable/private field, keyboard key, poster/continue/hero card, toolbar control, progress/incident marker and specialized player seek/transport. Add further roles when actual use/behavior warrants them; there is no arbitrary maximum number.
3. **Templates:** three-panel workspace with configurable widths/scroll/read targets; shared Library grid/list header/toolbar/stats; details metadata facts/action/tab structure; modal/read/review editor; specialized HUD layout. Content callbacks configure these without embedding transport/database work inside styling factories.
4. **Behavior contracts:** focus ownership, initial target, edges, scroll reveal, key consumption, modal parent, Back/cancel/dirty exit, opener fallback, enabled/disabled presentation and accessibility. A component's focus contract is as important as its font.
5. **Impact ledger:** role/token→actual current constructors/XMLs→consumers→explicit overrides→migration status. Changing a token only affects migrated consumers; no claim that an unconverted direct setTextSize magically updates.

Example current-to-target mapping: SharedThreePanel.heading(right,Information) → panel title; separate dynamic Overview TextView → selected-context title; preview_preference_category → Settings section/category label; right description scroll→readable supporting context; scanOverview centre text→noninteractive status summary. Their typography may remain identical by design while semantic identity is distinct. A single future SectionHeader change must not accidentally alter panel titles.

## Feasibility and migration order

**PROPOSED:** use Network Overview as pilot because source provides a small, stable panel with known headings, read target and three action callbacks. First document actual hierarchy and full focus/back behavior with runtime fixture; introduce role wrappers without appearance changes; migrate this panel and compare images/keys; then add other browser command/health variants; then Settings categories/inline choices; then shared Library headers/toolbars/cards; then Details/modals/keyboard exceptions; finally specialized player tokens after native/player lifecycle work stabilizes.

The existing shared Library and keyboard foundations reduce new infrastructure needed. Three-panel templates are feasible, but Settings' model/visibility, network staged edits, read-scroll targets and About nesting are different behavior. Share geometry and row contracts; keep state machines explicit. Do not move transport/scanner or provider-account logic just to reduce View constructor duplication.

## Acceptance and design handover

Every migration needs source-to-role table, known consumer diff, normal/large-text/narrow-viewport render, D-pad sequence, focus fallback after removal/recreation, screenshot comparison and release/minification check where references are dynamic. Existing owner-approved appearance is the baseline; upstream NOVA visuals are excluded. Changes to hierarchy/semantics/keyboard behavior need owner design approval before implementation.

**INCOMPLETE:** a runtime semantic identity and every-element catalogue for all states is not yet available. The static consumer/literal/navigation inventories, current facts and pilot mapping are the concrete design brief. Independent Claude consultation was not performed; these can be supplied later as advisory context after owner instruction.
