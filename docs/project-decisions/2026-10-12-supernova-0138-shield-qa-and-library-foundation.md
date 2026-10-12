# Supernova 0.138 Shield QA walkthrough — working findings and shared UI contracts
Date: 2026-10-12. Status: owner-reported runtime observations and owner-approved design intentions; not source-verified or implemented. Proposed 0.139 scope pending owner handover. Keep Codex's maintenance investigation isolated and unchanged.

## Terminology and shared foundation
“Third Library” means a user-created Library configured via the five-step Edit Library flow. Movies, TV Shows, and every custom Library must use the same **Library View Foundation** for header/stats layout, controls, grid, focus graph, visual focus, spacing and animations. The custom Library additionally offers Edit Library. One shared change must affect all Library types; validate implementation centralisation rather than assume it.

## Library 0.138 verified by owner
- Custom Library appearance, header quantities/storage, controls, media positioning, zoom and focus animation now visually match Movies/TV Shows.
- Custom Library Filters opens view filters, not Edit Library (previous defect resolved).
- Movies controls Left/Right bounded correctly; no wrap after rightmost control.
- These are observed visual/runtime results, not proof of code inheritance.

## Shared Library focus contract — required
- Selecting a Library tab loads its page **without moving focus** from the selected top navigation tab. No automatic descent.
- Down from top navigation focuses the **leftmost currently available control**, regardless of its identity or last-used focus.
- Up from **any** Library control returns to the selected Library tab.
- Down from **any** Library control focuses the **first (leftmost) media item below the divider in the currently displayed filtered/sorted grid**, not the geometrically aligned item under that control or a fixed title.
- Left/Right moves through controls in their visual order without unintended wrap.
- Returning from controls to tab then Down starts at leftmost control again.
- Focus must remain visible on the active tab until deliberate navigation into page. Back-navigation should never leave an invisible focus state.
- Current third-Library defects: selecting tab jumps focus to Filters; Up from controls cannot reach tab; Back forced to exit and can restore prior page with no visible focus. Movies controls currently descend to different grid columns according to horizontal control position. Investigate/fix shared focus routing and verify all three.

## Shared Library control sizing — owner-approved design
- Keep current icon and label sizes and icon-to-label spacing.
- Reduce oversized visible container height and notably reduce left/right internal padding; width should fit content.
- All labels on ONE line without wrap or unnecessary truncation; long Sort label receives naturally wider button.
- Equal fixed gaps between adjacent controls, left-anchored packed row; when widths shrink, subsequent controls move left (do not retain original button x-positions).
- Consistent border/radius/focus appearance; focus must not shift neighbour positions.
- Apply to all Libraries including Edit Library control; retain usable D-pad interaction target. Previous generated ~56px heights/widths were illustrative, not implementation measurements. No full-screen generated mockup should override approved existing UI.

## Home hero — 0.138 findings and design
- Manual single-tap Left/Right works; holding Left/Right on focused hero also scrolls Recently Added underneath (defect). Focus remains hero.
- Automatic hero carousel transition differs visibly from preferred manual transition; make automatic match manual motion/timing/easing without focus theft.
- Hero has very large library-like selection; owner favours 10 active titles, mixed Movies/TV, sensible rotation without constant changes; policy to finalise.
- Official transparent title logos apparently absent; investigate. Fallback title lower, appropriate size, one line.
- Hero metadata one line: **Rated 15** (etc.) badge at left, year, max 2–3 genres subject to space, TMDB score. **Remove vote count from hero**. Age badge properly centred/padded.
- Tagline below metadata with modest but distinct vertical gap; same font family, size, weight as metadata; slightly reduce current metadata font size; no script font.
- Remove technical pills from hero only, preserve on approved other surfaces.
- Artwork fidelity: no Supernova-applied darkening, grading or bottom gradients across Home hero and media row images; retain original colours. Localised text shadow/contrast treatment okay. Investigate whether current dark bottom is overlay vs intrinsic art.
- Continue Watching absent despite resume state visible in Dune Details; investigate independently.

## Critical playback failure
- Dune: Part Two and Tulsa King attempted ~13:30–13:31 Dublin time; “Preparing playback” then returned to Details, playback never starts. Owner called crash; process crash not verified. Release-blocking candidate, root cause unknown. Await diagnostic ZIP **at end of walkthrough**, do not repeatedly request it or interrupt QA. Preserve user data and update path.

## Release/process boundaries
- 0.138 signed QA installed over 0.137; onboarding deferred until future clean-install test.
- Codex ongoing read-only comprehensive maintenance investigation on isolated docs branch; do not alter it or start 0.139 implementation until owner authorises.
- 0.139 provisional combined fixes and expanded diagnostics, then smoke test, then separate maintenance implementation on stable baseline.
- This file records conversation-derived requirements; not a claim that fixes, APK, diagnostics, tests, source investigation, or screenshots were committed. Confirm implementation with source and Shield QA before marking fixed.


## 2026-10-12 Shield QA follow-up — Network & Files and Open Source Licences
- **OWNER-VERIFIED PASS (0.138):** Network & Files Local Storage list D-pad scrolling no longer unexpectedly jumps focus into centre-panel breadcrumb navigation; owner can browse items normally. Do not carry this as an outstanding 0.139 defect unless regression evidence appears.
- **OWNER-VERIFIED PASS (0.138):** Settings > Open Source Licences opens and allows browsing every licence entry without the previously observed crash. Crash root cause remains unknown; do not claim a diagnosed fix.
- **NEW 0.139 REVIEW CANDIDATE — licence viewer content proportionality/compliance:** Owner reports AndroidX runtime entry displays approximately **196 pages** of text, versus Bouncy Castle **1**, jUPnP **5**, Jetty **7**, Kotlin/JetBrains **3**. Owner wants the minimum legally required in-app attribution/notices, with QR links to relevant authoritative project/licence pages; not a huge redundant reader. Investigate the precise contents of the AndroidX entry and generation/source of page count, including whether bundled notices cover multiple components, repeated texts, and licence/NOTICE obligations. Produce per-component inventory of SPDX/licence terms, copyright/NOTICE/attribution, distribution requirements, link target, offline obligations and whether links alone suffice. Preserve all mandatory full text/notices and accessible offline compliance where required; do not assume QR-only is legally sufficient. Propose concise index/summary plus required notices/full text where legally necessary, with QR links as supplementary convenience. Avoid unverified legal conclusions. No implementation until owner approves consolidated 0.139 handover.
- Release notes: owner explicitly deferred discussion; do not infer a change request.


## 2026-10-12 owner refinement — Open Source Licences across all components
- Apply the **most minimalist legally compliant presentation to the entire Open Source Licences section**, not just AndroidX. Audit each component, required notices/full texts, offline distribution obligations, redundant duplication and existing QR link targets. Retain all mandatory attribution/text, with concise navigation and QR links as supplementary conveniences; do not presume links alone discharge obligations.
- **Remove redundant extra entry step:** In three-panel Settings, when the left panel highlights **Open Source Licences**, populate the centre panel immediately with the component list and the right panel with information for the selected/default component. Current observed behaviour: centre panel blank, right panel says **“Press OK to open the locally bundled software licenses and notices.”** and requires OK to load both. Owner wants this interstitial eliminated.
- Preserve predictable D-pad focus and three-panel navigation, scrolling and Back. Determine initial component selection and right-pane content from source/current behaviour before implementation; do not invent.
- **Defer detailed final design until Codex completes the full UI inventory/reference atlas**, then conduct a full walkthrough of this Settings subsection with all entries and focus states.
- This is a provisional **0.139 candidate**, not authorisation to code; maintain current 0.138 owner-confirmed no-crash status.


## 2026-10-12 Shield walkthrough — Details, Extras, return-focus
- Playback Hub remains **BLOCKED** by existing playback launch failure; do not infer its UI passes/fails. Defer Settings and Network & Files detailed reviews until UI Atlas ready.
- **Details hero/details card:** owner sees new card, but its contents are vertically compressed; Resume button should move lower. Allow more width and height, adjust internal spacing while preserving existing approved style. No dimensions approved yet.
- **Details-to-tabs transition:** scrolling down from full Details page to tabs feels insufficiently smooth. Owner may provide video; avoid assuming exact animation defect without footage.
- **Extras tab:** three trailer thumbnails visible, imagery appears cropped. Request screenshots only when actively reviewing; investigate image scale/crop from source and evidence.
- **Extras focus zoom:** gaps between trailer items exist at rest but disappear around focused middle item when zoom applied. Preserve visible breathing room, prevent unintended overlaps. **Owner-approved Home page row spacing, zoom, animation, sizing and D-pad behaviour are the shared foundation contract for equivalent rows across app**, including Extras; document intentional differences. Verify against actual approved Home row rather than blindly copying current defects.
- **HIGH PRIORITY focus restoration:** Shield repro TV Shows > Tulsa King poster > Details > Back: poster focus not restored; Left/Right/Up no response; Down moves focus to Home top navigation. Expected Back restores focus to the exact prior TV show poster with immediately functioning D-pad, without resetting grid scroll. Investigate shared return-focus restoration across Movies, TV Shows and custom Libraries and different Details exits; owner only confirmed TV Shows/Tulsa King repro.
- These are **0.139 candidates**, pending consolidated approval. No application edits authorised by this note.


## 2026-10-12 Shield walkthrough — Seasons & Episodes
- **Joe Pickett (2 seasons):** Details > Seasons & Episodes shows huge full-width focusable Season 1 banner that opens separate Season 1 Details, with episode row below. Owner rejects prominent full-width season header/focus box. Replace visual presentation with subtle Home-style row heading and episodes directly underneath; follow approved Home row layout/spacing/focus zoom/animation contract for episode rows. Verify handling of the existing season-details action rather than accidentally removing access. Apply consistently to every season.
- **Save Me (2018):** owner's library has only 4 Season 2 episodes, yet Season 1 appears labelled **Unavailable**. Desired Seasons & Episodes view shows **only seasons/episodes available in the user's indexed library**, not metadata-only or unavailable season placeholders. Investigate exact source of Season 1 (metadata, stale records, filtering), including handling of partially populated seasons and season numbering; do not assume cause.
- Provisional 0.139 design and filtering defects; no implementation approval. Request images only when needed for precise layout.


## 2026-10-12 Shield walkthrough — Details focus states and tab order
- **Owner-confirmed working:** Opening Tulsa King Details initially focuses Play; Left/Right constrained to Details-card action buttons; Up from Play focuses TV Shows top navigation without changing Details page; Down from Play reaches first tab (Seasons & Episodes in current order), Up from tabs returns to Play. Preserve these D-pad routes.
- **Misleading tab focus indicator:** Underline persists beneath the currently selected/loaded Details tab (e.g. Seasons & Episodes or More Like This) even after focus has moved Up to Play. Unlike top navigation, where underline does not persist without focus, this makes focus location ambiguous. Owner wants underline to mean **actual tab focus only**; remove underline when focus leaves tab strip, while retaining selected tab content. On return, underline follows focused tab. Investigate any other selected-state styling without confusing it with focus.
- **Owner-approved TV show Details tab order:** **Details → Extras → Seasons & Episodes → More Like This**. Details and Extras describe current title, Seasons & Episodes navigates show contents, recommendations last. Keep existing functionality, left/right traversal and accessible focus. Check movie vs TV tab applicability without inventing missing tabs.
- Candidate for consolidated 0.139, no app code changes authorised.


## 2026-10-12 owner addition — monochrome Details tab icons
- Add a small corresponding **monochrome icon immediately before each Details-page tab label**. Proposed icon semantics: Details = info, Extras = film, Seasons & Episodes = list-video, More Like This = clapperboard. Final glyphs should match existing Supernova icon family and be checked against the actual UI atlas before implementation; these are proposals, not existing app assets.
- Preserve owner-approved order: **Details → Extras → Seasons & Episodes → More Like This** for TV shows; handle movie applicability as discovered.
- Icons should be subtle, single-colour, consistently sized/aligned/spaced, and inherit each tab's normal/focused label colour. Icon+label form **one D-pad focus target**. Focus underline appears only while tab strip has actual focus; selection/content state persists without false focus underline.
- No code change authorised yet; 0.139 candidate.


## 2026-10-12 Shield photo — Chief of War Details card and tab layout
- Owner provided genuine **Shield TV photo** `IMG_4925.jpeg` of Chief of War Details page; photo is reference evidence, NOT a generated mockup. Image shows large backdrop card, title/metadata/synopsis and Play/More buttons clustered high at left, unused lower-left card area, and tab strip below. This local conversation attachment is not yet uploaded to GitHub/Drive; do not claim it is.
- **Card composition:** retain title/metadata/description in upper-left; relocate Play/Resume (where applicable) and other action buttons to **bottom-left** with comfortable inset/padding, rather than immediately beneath text. Slightly enlarge card in both dimensions subject to actual Shield layout constraints. Preserve Play as initial focus.
- **Tab order and reset:** TV show Details uses **Details, Extras, Seasons & Episodes, More Like This**, with previously agreed monochrome icons. Every fresh Details-page opening must **load Details (leftmost tab)**, regardless of last visited tab on this or another item. Down from Play must focus **Details**, first leftmost tab. Owner observed prior-tab persistence/current Details tab being second, resulting in confusing focus/content on re-entry. Preserve selected content while focus is elsewhere; distinguish tab content selection from tab focus.
- **Tab styling:** shorten gap between label/icon and focused underline to match top navigation underline spacing; underline appears only when tab itself is focused, not when focus is on Play. Add subtle **full-width horizontal divider** below tab headers, separate from short focused-tab underline; divider persists as structural separation. Add deliberate vertical breathing room between tab headers/divider and tab page content. Maintain D-pad access and visible focus.
- Exact dimensions/colour/spacing to be calibrated against native UI and real-device photo, not inferred from camera perspective. No code implementation authorised.


## 2026-10-12 owner refinement — one-line tabs, logo investigation, shared metadata
- **All Details tab icon+text labels are strictly ONE LINE**, including **Seasons & Episodes**; no wrapping or truncation and do not silently shrink established text to fit. Use natural widths and deliberate inter-tab spacing. Prior illustrative two-line rendering was incorrect and not a design reference.
- **Title logo inconsistency:** genuine Shield Chief of War photo shows a stylised title artwork/logo on Details, whereas other titles' official title art appears absent on Home hero (and potentially Details). Investigate actual artwork type/source, availability, retrieval/cache/fallback logic and differences between Home hero vs Details and movies vs TV. Do not assume cause or that the Chief of War logo proves all official artwork loads. Intended: official transparent title logo when available, with approved text fallback otherwise, consistent across hero and Details.
- **Shared metadata visual contract:** Home hero and media Details card should use the **same age rating badge, year, limited genres, TMDB score, typography, spacing, separators and format**. Do not display TMDB vote count on Home hero; owner has not separately approved votes on Details, so avoid assuming they belong. Details may additionally show **technical information** (codec/resolution etc.) that must not appear on Home hero. Review season/episode counts separately for fit/semantics; preserve appropriate content-specific details. Prefer shared semantic component with Details-specific extension during later authorised implementation, without prematurely starting broader maintenance architecture.
- This refines provisional 0.139 requirements only; no application coding authorised.


## 2026-10-12 design proposal — shared tagline cards and full synopsis in Details tab
- Owner proposes **Home hero and media Details cards use the same concise tagline rather than full synopsis**; full description/synopsis moves to the **Details tab**, where full readable information belongs. This supports shared logo, age/year/genre/TMDB metadata and typographic foundations while preserving distinct card sizes, technical information and action buttons. Details card Play/Resume/More still bottom-left as previously discussed.
- This is a **proposed design direction awaiting final owner confirmation**, not yet a final mandate. Determine availability of taglines in actual metadata, movie/TV differences, localisation, empty tagline handling, line limits and accessibility. Do not silently replace missing taglines with full descriptions. Keep full synopsis accessible in Details tab from cached/offline metadata when available.
- No app code change authorised.


## 2026-10-12 owner decision — tagline fallback and Details tab contents
- **Approved card text policy:** Both Home hero and media Details card use the official tagline as their concise one-line text. If unavailable, show **only the first single-line excerpt of the synopsis**, ellipsised when text continues (e.g. `…`). Never show full synopsis on either card; long taglines must also fit/truncate to one line without altering card geometry. Preserve shared title-logo, age/year/genre/TMDB metadata style; Details card may additionally show technical data and its own actions.
- **Full synopsis moves to Details tab** with no arbitrary truncation, using cached/offline metadata where available.
- **Retain Details tab information, redesign presentation later:** full synopsis, up to **8 cast**, up to **8 crew**, **Key Information**, **Reception**, **Technical Information** sections/boxes. Owner wants this content retained; precise visual redesign, section layout and detailed fields pending actual UI atlas/review. Avoid deleting existing data/fields just to simplify design.
- Earlier fallback options/provisional tagline discussion are superseded by this owner decision. No implementation authorised until consolidated 0.139 handover.


## 2026-10-12 owner-approved TV season navigation decision
- **One master Details page per TV show**; do not require separate full Details pages for Season 1, Season 2, etc. For multi-season shows (e.g. Only Fools and Horses), navigate available seasons/episodes within the show's **Seasons & Episodes** tab.
- Replace oversized full-width/focusable season banners with **Home-style row headings** and equivalent Home row focus/zoom/scroll behaviour where applicable. Season headings should not masquerade as giant action cards. Preserve useful season-specific metadata/actions via an intentional compact alternative if source investigation shows they exist.
- **Only library-available seasons and episodes** should appear in library browsing as available playable content; investigate Save Me phantom unavailable Season 1, preserve correct metadata distinctions where relevant. Correct bottom-edge episode clipping, trailer focus spacing and animation.
- Episode selection should retain appropriate details/playback functionality; returning restores the precise season/episode focus and scroll position. Preserve TV show's context.
- This supersedes prior open question about separate season Details pages. **Owner approved the recommendation**; code feasibility and any existing season-specific functions still need a read-only audit before implementation. No 0.139 coding authorised.


## 2026-10-12 Library Statistics concept review — PARKED, NOT APPROVED FOR IMPLEMENTATION
Owner reviewed several progressively more compact **concept mockups** (not Shield screenshots or source-verified layouts) for library statistics, then explicitly requested to **hold off on the change**, document current thinking and move on. **Do not implement or treat the visuals as approved final designs.**

### Current preferred exploration direction
- Keep Statistics **integrated into existing Movies, TV Shows and custom-library headers**, not a new dashboard, side panel or top-level page.
- Reclaim vertical space so the **first poster row starts higher**; retain poster size, focus enlargement and readable TV typography.
- Explore a **single low-height, full-width translucent horizontal strip** with monochrome icons and subtle vertical separators, not large multi-row cards.
- Show each figure and its label **on the same line**, e.g. `1,248 Movies` rather than a stacked `Items` / `1,248`; `1.12 TB total`, `682 GB local`, `438 GB network`. Figures shown in concepts are **illustrative, not user library data**.
- For TV Shows, investigate fitting **unique TV show count, available episode count, total indexed size, local and network sizes** on the same single-line strip without crowding. The latest visual showed Movies and TV Shows as two comparative mockup sections, **not a proposal to display both libraries together on one screen**.
- Custom libraries must adapt to movie-only, TV-only and mixed collections; their exact counts, labels and layout remain undecided.
- Keep the **real existing library controls** in their normal position between the statistics and posters; mockups omitted or invented controls, and the owner explicitly **rejected those illustrated control-button designs**. Do not copy mockup control labels, order or buttons into requirements.
- The final approved design must be assessed against **actual 0.138 Shield UI and Codex UI Atlas**, not reconstructed artwork. Blank poster placeholders are preferred in future concept mockups to avoid irrelevant artwork.
- Data integrity: distinguish unique shows from source membership, avoid double-counting local/network mixed shows; local/network percentages should be based on measured storage size if shown; account for unknown file sizes and incomplete totals. Confirm semantics and source code before implementation.

### Status and next steps
**STATUS: PARKED / NO IMPLEMENTATION AUTHORISED.** Preserve current production UI. Return to statistics only on owner request; compare to genuine Shield layout, settle exact labels and TV/custom variants, controls and D-pad routes, then explicitly approve. Continue other 0.138 QA / 0.139 planning in the meantime.


## 2026-10-12 Customise Home owner decision — remove Libraries filter
- Owner explicitly **rejects the proposed Libraries/source-library selector** in custom Home row configuration. Supernova already tags media by type; the **Movies / TV / Both** content-type choices together with **Genres** are sufficient to scope row content. The intended result is inclusion of matching indexed items across local and network libraries without an extra source-selection burden.
- Proposed custom-row editor fields: direct rename from main editor (for custom rows), inline mutually exclusive **Movies / TV / Both** buttons, Genres selector, compact Sort By and Maximum Items controls (latter two side by side). No Libraries field under Additional Filters or elsewhere in the new custom-row editor.
- Preserve functional sorting, limit, genre filtering and type matching. Before future implementation, audit existing library membership/source filtering, persistence and legacy custom-row configurations; choose safe migration/default semantics so removing the selector does not silently omit media or break old saved rows. Distinguish content-type classification from storage-source selection.
- Owner prefers direct buttons, minimal click-throughs, smooth D-pad row reordering and a clearly bounded editor overlay. Exact UI still under design; **no code changes authorised**.


## 2026-10-12 Customise Home — genre catalogue/matching and inline sort proposal (owner feedback; design in progress)
- **Observed failure:** Owner opened Customise Home before library import/scan completed; metadata-derived genre list lacked Sci-Fi, preventing preconfiguration of a Sci-Fi row. Atlas confirms current CH-004 genre choices derive from indexed metadata; empty cache gives only All Genres. Investigate stable canonical supported genre vocabulary, provider aliases/localisation and user-defined/unrecognised tags. Proposed: show all supported genres regardless of current library content, with subtle availability/count indicators; **keep zero-count genres selectable** so a row can be prepared before scan, rather than disabling them and perpetuating the problem. Counts must distinguish scanning/unknown from confirmed zero.
- Preserve **All Genres** versus **Selected Genres** scope, and add **Match Any (OR)** versus **Match All (AND)** for selected genres. Owner's concrete example: Crime+Drama with Match All must exclude crime-only and drama-only media. Atlas confirms existing CH-004 uses Match Any Selected only; Match All is a new behaviour requiring source investigation/testing. For one selected genre the modes are equivalent; for All Genres, matching mode should be irrelevant/hidden.
- **Dynamic Genre Rule is existing functionality and MUST remain**, with direct inline control in the right editor panel. Atlas CH-003: Dynamic On recalculates membership; Dynamic Off freezes calculated membership; explicit membership handling differs. Do not conflate Dynamic On/Off with All/Selected or Match Any/All; audit implications for filters and saved rows.
- **Sort redesign proposal:** three direct controls Date Added, Title, Year, each with visible selected state and direction arrow. Selecting a different field applies its default direction; pressing currently selected field again toggles Ascending/Descending. No extra direction submenu or ambiguous third 'none' state: always one active sort. Source current defaults: Date Added descending/newest first; Title ascending/A–Z; Year descending/newest first. Retain all three fields and both directions. Owner suggested toggle-to-unselected concept but recognised that a valid sort must always exist; exact toggle semantics to be reviewed before final UI sign-off.
- Continue two-column single Customise Home overlay: left Home rows with direct Move/Show-Hide, right panel automatically updates on selected row, no Edit popup/button, no Libraries source filter. Right panel should present inline Movies/TV/Both, Genre scope/matching, Dynamic Genre Rule, Genres chooser, Sort, Maximum Items (side by side where feasible); direct rename custom rows. Minimise clicks while preserving remote D-pad clarity, selection versus focus and smooth reorder animation.
- **Status:** proposals and owner observations captured; do not treat as final approved layout or authorise code changes. Compare to native Shield and audit metadata/filter semantics before handover.


## 2026-10-12 Customise Home sorting — latest owner correction (supersedes prior per-option direction toggle)
- Owner **rejects double-height or dynamically expanding sort buttons** and the proposed 'click the same sort option again to reverse' interaction.
- Preferred design: **fixed-height, single-line, mutually exclusive sort-method buttons** (existing Date Added, Title, Year; candidate Date Watched, Rating, Random subject to data audit) plus **one separate Ascending/Descending direction control** with an arrow that flips. No verbose 'highest first/lowest first/newest first' descriptions in the button grid. Focus and selected state must be distinct; no resizing/reflow on selection.
- Changing method selects that method; changing direction uses the dedicated button. Keep valid sort always selected and Date Added descending default. For Random, direction is not applicable; disable/hide its direction control without changing grid dimensions.
- This latest owner preference **supersedes** the earlier recorded direct repeated-click sort-direction proposal. Do not implement or treat final until owner explicitly authorises development.


## 2026-10-12 Customise Home sort controls — latest owner decision superseding button grid
- Owner clarified the preferred UI is **exactly two side-by-side inline controls**: (1) a **single sort-method selector** displaying the current value (Date Added, Title, Year, plus proposed Date Watched, Rating, Random subject to feasibility); (2) a **single direction selector** displaying Ascending or Descending. This replaces both the six-button method grid and any extra row of sorting buttons. No separate popup for sort-method choice.
- Shield D-pad: focus Method, OK enters inline editing; Up/Down cycles choices *within the same fixed-size control*; OK commits and returns to normal control focus; Back cancels pending choice and restores prior method. While editing, Up/Down must not move to adjacent editor fields. On direction, OK toggles Ascending/Descending directly. No extra visible controls or height/width reflow when active.
- Exactly one method always selected; retain default Date Added descending. Random has no meaningful direction: disable direction control while preserving stable layout and sensible focus traversal.
- Keep sort UI minimal and visually stable. This **supersedes the immediately previous recorded six fixed-height method buttons plus separate direction control**, and the earlier repeated-click-per-method proposal.
- Design direction only; no implementation authorised.


## 2026-10-12 Customise Home further owner refinements — row focus, dynamic rule, counts
- Terminology: single-valued **inline selector / D-pad spinner** (OK enters, Up/Down cycles, OK commits, Back cancels); segmented control for Movies/TV/Both; multi-select genre checkboxes.
- **Row list:** owner observes six-dot drag grip and Up/Down Move button duplicate one another. Latest design proposal removes both and makes **row body itself the Move-mode trigger**. Each list item has **exactly two independent focus targets**: (1) icon+row title/body; (2) Show/Hide eye button. No outer focus border enclosing both or simultaneous nested focus boxes. Up/Down navigates same column across rows, Left/Right crosses between row body and eye; focus versus selected configuration row must be visually distinct.
- OK on row body enters move mode; visibly lifted/emphasised row, contextual Up/Down / OK place / Back cancel hint; adjacent rows smoothly animate to make space, focus tracks moving row, auto-scrolls while moving; Back cancels and restores original order. Move icon/grip is not needed. Final visuals and interaction need owner sign-off.
- **Dynamic Genre Rule** belongs as the **bottommost** setting on custom-row right panel. Proposed clearer label **Automatically include new matching media** with on/off switch. Off must not disable or hide row; owner intends it to stop future matching additions while preserving current membership. Source atlas describes On recalculation / Off frozen calculated membership; audit exact semantics and interactions before implementation.
- **Maximum Items** must be much more compact (inline selector/stepper); preserve No Limit, presets and custom numeric value with existing validation, not just ± one increments. Exact position/layout pending.
- **Genre selector:** checkbox on left, genre name middle, live count/availability at right. Owner likes scanning indicator. Owner asks whether counts refresh while picker remains open; **current behaviour NOT VERIFIED**, as counts and scanning UI are proposed mockup elements. Proposed acceptance: update counts as indexing progresses using batched non-disruptive updates, preserve focus/scroll/checkbox selections, show truthful scanning status, don't confuse unknown/unscanned with confirmed zero, allow selection of zero-count genres, don't reorder list during updates. Verify scanner event/API support and performance before implementation.
- No application changes authorised; docs-only design work.


## 2026-10-12 Customise Home latest owner decisions — grip, limits, animated genre counts
- **Move affordance:** owner prefers a **six-dot grip at the far right of the main row-body focus target**, not to the left of the media icon and not a separate focusable button. Remove separate Up/Down Move button. Keep eye Show/Hide as a **second independent focus target** outside the main row-body focus border. The grip visually communicates OK-on-row enters D-pad Move Mode; preserve smooth neighbouring-row slide and focus/scroll stability.
- **Custom row Maximum Items:** owner rejects 0/unlimited and arbitrary 1–100/custom number entry in the redesigned UI. Preferred six presets **5, 10, 15, 20, 25, 30** (min 5, max 30), presented through **one compact inline D-pad selector**, not six permanently visible buttons. Owner notes some existing Home rows capped at 15; new custom rows may go to 30. Investigate existing stored values (0/unlimited, 1–4, >30, arbitrary) and safe migration/compatibility without silently corrupting old rows; confirm practical performance at 30 and built-in row policies before implementation.
- **Live genre counts during scan:** subtle **odometer/clock-like digit roll** as numbers increase or decrease (e.g. Action 128→129), without blinking/flashing the row or changing label/checkbox position. Fixed-width/tabular numeric column, stable focus and scroll, batched latest-state updates rather than accumulating animations, handle rapid jumps and downward corrections, respect reduced-motion preferences. Live count/status data feasibility not yet verified. Preserve ability to select currently zero-count genres before library indexing finishes.
- Design/QA documentation only. No implementation authorised.


## 2026-10-12 owner clarification — custom-row maximum-items migration is not a blocker
- Owner confirms Supernova is currently **private/testing only**, not a public app with users whose custom-row settings need backwards-compatibility guarantees. Owner is comfortable with existing custom-row limits outside the proposed **5, 10, 15, 20, 25, 30** preset range being adjusted during eventual implementation. Do **not** treat preservation of old 0/unlimited or 100-item custom-row limits as a release blocker.
- Preserve other row configuration and content where practical; implementation should still avoid crashes or invalid persisted state. Exact fallback for legacy limits and default for newly created rows should be confirmed at handover (15 is a candidate based on existing Home rows, not an explicitly owner-approved new-row default).
- No code changes authorised yet.


## 2026-10-12 Customise Home — owner approves compact Display Settings layout and monochrome states
- Owner approves **three compact, fixed-size controls side by side** under Display Settings: **Sort By**, **Direction**, **Maximum Items**. Each must carry an appropriate **monochrome icon anchored at the left**. For Maximum Items, the selected numeric value (default **15**) is **centred in the remaining value area**; no width/height reflow while focused or editing.
- Maximum Items uses a single inline D-pad selector: OK activates, Up/Down cycles **5/10/15/20/25/30**, OK confirms, Back cancels; no 0/unlimited or arbitrary custom value. Default **15 explicitly approved**.
- Owner approves monochrome **eye** (visible) / **eye-off** (hidden) icons with state conveyed by glyph, not colour. The Show/Hide icon is an independent focus target outside the main row-body focus border. The six-dot move affordance is inside the right edge of the main row-body focus target. Focus does not recolour row text.
- Owner also approves direction of genre selector: monochrome selection indicators on left, separate genre icon, names and stable live counts on right; entire genre row one focus target, selections persist independently of focus.
- These are approved **design refinements**, not permission to modify application code. Other Customise Home questions and validation remain open.


## 2026-10-12 Customise Home — Maximum Items selector alignment refinement
- Owner supersedes earlier centred-value requirement for Maximum Items: place **15 left-aligned immediately after the monochrome list/count icon**, with the **same icon-to-label gap as Sort By (Date Added) and Direction (Descending)**. Keep the selector's fixed dimensions and its position as the third compact control alongside Sort By and Direction.
- Add a small **up/down chevron indicator at the right edge** to communicate inline cycling, not a separate button or popup. Prefer consistent selector affordances across the three controls. Existing D-pad editing, six presets and default 15 remain unchanged.
- Design-only documentation; no app implementation authorised.


## 2026-10-12 Customise Home — Direction one-click toggle (latest owner decision)
- Owner confirms **Maximum Items numeric value remains left-aligned** after its monochrome function icon, like other selector values; ignore the briefly considered return to centring. Right-side up/down chevrons remain for inline cycling.
- Three compact, fixed-height, single-line controls remain side by side: Sort By, Direction, Maximum Items. Avoid text wrapping in TV layout.
- **Direction is NOT an inline D-pad spinner.** It is a **single-press OK toggle** between Ascending and Descending. Left icon represents sorting direction function (e.g. up/down arrows); middle label shows current value; **right icon is one arrow pointing up for Ascending or down for Descending**, changing immediately on OK. No redundant right-side chevrons, no extra edit mode or confirmation. Commit/persist the choice immediately so leaving focus retains it.
- Sort By and Maximum Items continue to use OK→Up/Down cycle→OK commit/Back cancel, with function icon left and chevrons right. No layout shifts or row height changes when toggled/focused.
- Design decisions only; no code implementation authorised.


## 2026-10-12 Customise Home — focus, genre icon summary, neutral built-in copy
- Owner reiterates **exactly one active focus target** at any time: row body OR independent Show/Hide eye button, never both. Row text retains its normal colour under focus; use border only, no cyan/blue recolouring of row label.
- Owner likes descriptive label **Automatically include new matching media** for dynamic genre behaviour; keep it as the bottom setting for custom rows, subject to source semantics verification.
- In the custom-row Genres summary, display **the individual monochrome icons of the actually selected genres**, preceding the selected genre names. E.g. Science Fiction icon + Action icon, then `Science Fiction, Action`, and `2 selected`. For many genres, compact overflow handling can be designed without losing the selected-count text. Do not use a single unrelated generic genre icon when specific selected icons are available.
- Built-in Trending Movies explanatory description should be **content-neutral**, e.g. `Shows currently trending media based on your library activity`, rather than `Shows currently trending movies ...`; verify actual row population semantics before asserting it is library-activity-based.
- The preceding generated mockup incorrectly showed two simultaneous focus outlines and built-in row edit controls; these are NOT approved. Built-in rows show contextual explanation, not editable custom-row fields; genre popup belongs only to a custom row.
- Documentation only; no implementation authorised.


## 2026-10-12 Owner directive — Customise Home must be a specification-driven UI rebuild, not features grafted onto legacy dialogs
- Owner supplied **three genuine Shield photographs** of the current 0.138 Customise Home: oversized translucent legacy row-management dialog, separate tall per-row rule editor, and a third tall genre selection dialog. Current spacing is expansive and dialog layers overlap. These photos are **existing-state evidence**, not the target design.
- Owner explicitly requires the agreed **new visual design to be the implementation foundation**. Codex must NOT retain the old visual dialog layout and merely bolt on new functions. Treat the new Customise Home as a deliberate UI rebuild with reusable controls and layout contracts, while preserving validated application data/business logic where appropriate.
- Before implementation, prepare an authoritative **visual contract** (approved reference mockups, panels, dimensions/relative proportions, density-independent spacing, typography, icons, colours/opacity, focus borders, selected/unselected and hidden states), **D-pad interaction/focus contract** (one active target, row body vs eye button, Move Mode, editor focus, selector confirm/cancel, Back handling, scrolling), and **responsive contract** (reference 1920x1080 TV composition; density-aware scaling, safe margins, text fitting, independently scrolling regions, alternative layout for constrained screens, no overlap or clipping).
- Codex must not infer undocumented appearance or substitute legacy widgets by convenience. If mockups conflict with written contracts, or a detail is unspecified, flag for owner decision rather than guess. Source-derived feasibility findings must be separated from design approval.
- Require screenshot-based implementation comparison and TV D-pad QA at multiple sizes/densities; enforce focus and typography invariants. Existing approved constraints: independent focus row-body/eye, no text recolouring, eye/eye-off monochrome, genre-specific monochrome icons, compact Sort By / Direction / Maximum Items controls, default 15, Direction single-press toggle, dynamic include label last.
- Design/documentation only. No app source changes, merges or builds authorised by this directive.


## 2026-10-12 — Shield Android TV home-screen channels: investigate replacement
- Owner reports Shield Customise Your Home Screen > Supernova currently lists five channels: Recently Added, Recently Played, All Movies, All TV Shows, All Animation Movies. Owner suspects these are legacy NOVA-era offerings, especially All Animation Movies; this is a hypothesis, not source-verified.
- Preferred direction to investigate: publish Supernova Continue Watching, Recently Added, and optionally user-selected Custom Home rows as launcher channels. Consider a per-custom-row 'Show on Shield Home Screen' option independent of visibility within Supernova Home; proposal only, not yet implementation-approved.
- Read-only source audit required: channel registration and IDs, API compatibility, item membership, artwork, deep links/resume, refresh triggers, launcher layout/order limitations, and safe retirement/migration of legacy channels. Do not assume redundancy without evidence.
- Separate parked investigation/design track; no channel removal, app source changes or builds authorised.


## 2026-10-12 — Owner directive: Customise Home as full UI replacement and Supernova design-system reference
- Owner strongly approves the latest concept's dark charcoal/navy background colour, rounded corners, padded raised option rows, nested surfaces, and layered depth. The goal is clear structural hierarchy: background > overlay window > grouped section/card > individual controls > single focused element. Depth and separation are essential, not merely decorative.
- **Customise Home must be a ground-up presentation/interaction UI replacement, NOT a cosmetic reskin, slight rearrangement, reuse of legacy blue dialogs, or old blur treatment.** The approved final mockup and behavioural specification are the visual/interaction contract. Remove or retire the legacy Customise Home UI implementation where appropriate, while auditing and preserving correct underlying filtering, data, persistence and functionality; do not recklessly delete business logic.
- Use the approved Customise Home implementation as the first reference for a reusable, consistent Supernova design system (surface colours, depth, layering, padding, radii, borders, typography, focus and connected pop-outs) to inform future Network, Files, Settings, context menus and other dialogs. No blanket redesign of these other screens is authorised yet.
- Mockup conventions: always use the faded cinematic poster-library background; omit top navigation and clock; secondary pop-outs visibly connect to their invoking setting with subtle parent indicator, not a second active focus. Precisely one active D-pad focus; left row body and eye are independent targets with a clear gap.
- The new Shield Home publishing toggle sits above bottommost 'Automatically include new matching media'. Genre picker shows a scan-in-progress banner only when scanning; otherwise explanatory counts text. Genre Matching Match Any/All is retained. Final owner approval of corrected mockup and technical source audit required before coding.


## 2026-10-12 — Named reusable visual contract: Supernova Menu Foundation Design
- Owner names the approved Customise Home mockup's visual language **Supernova Menu Foundation Design**, shorthand **Menu Foundation**. It is a reusable, implementation-ready **visual specification** for other app menus/windows; it must NOT import Customise Home-specific functionality, data, labels, row editing, or workflows into those destinations.
- When owner later says 'apply the Menu Foundation' to Network, Files, Settings, context menus or another UI area, this means faithfully transfer the approved visual system while adapting layout and retaining that area's own functionality.
- Foundation deliverable must specify measurable tokens and component contracts: exact colours, opacity and backdrop treatment; nested surface/elevation/depth hierarchy; borders/shadows/radii; spacing and padding; typography; monochrome icons; one-target-at-a-time D-pad focus; secondary pop-out parent connector versus active focus; transitions; scalable TV layout and screenshot acceptance criteria. Reference approved mockup assets and states; distinguish fixed design rules from adaptive layouts.
- Current mockup remains a concept with known focus-state errors; do not elevate incorrect rendered focus or unverified values into the foundation. Final owner approval and source/feasibility validation before implementation.


## 2026-10-12 — Global Menu Foundation reuse and monochrome icon semantics
- Owner expects that, after approval and component implementation, an instruction such as 'replace all menus in Supernova with the Menu Foundation Design' can drive a planned app-wide visual migration, retaining each menu's own functionality and allowing screen-specific layout adjustments and QA.
- Establish global rule: each applicable actionable menu option/control should have a recognisable, semantically meaningful **monochrome icon adjacent to its text**, consistent in alignment, dimensions, visual weight and mapping across the app. Distinct actions should be identifiable; identical actions should reuse iconography. Avoid gratuitous icons for body copy or headings, and record intentional exceptions.
- Consolidate pre-existing agreed foundation-like rules (D-pad single focus, typography, monochrome iconography, nested surfaces, padding/radii, connected pop-outs) into one canonical versioned visual specification, shared component catalogue, and screen-by-screen adoption/acceptance matrix. Audit current app for legacy and divergent menus before any broad replacement; phased migration with regression tests rather than uncontrolled blanket deletion.
- Foundation visuals must be specific enough for faithful recreation and validation, not mere inspiration. No app-wide changes authorised at this planning stage.


## 2026-10-12 — Proposed global Language & Region Icon Standard
- Owner requests consistent, recognisable language icons across Settings language lists, playback HUD, subtitle/audio selection and other language-bearing UI. National/regional flags may remain full colour as an explicit exception to the otherwise monochrome Menu Foundation iconography; do not create monochrome flags.
- Standards research: Android Locale/BCP 47 and Unicode CLDR distinguish language, script and region; there is no universal official one-flag-per-language mapping. **Do not infer a country from language alone**. Proposed mapping: explicit valid region -> corresponding full-colour flag; language with no region -> neutral monochrome language icon; unknown/unparseable -> neutral icon and readable label. Consistent icons and labels across all app surfaces via a shared language/region resolver.
- Audit existing subtitle/audio metadata formats, ISO 639 variants, BCP 47, regional codes, flag assets, and actual Shield rendering; confirm ambiguous languages and region fallbacks with owner before final sign-off. Avoid platform-dependent emoji assumptions without testing. This is a design proposal and audit task, not implementation authorisation.
