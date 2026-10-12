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
