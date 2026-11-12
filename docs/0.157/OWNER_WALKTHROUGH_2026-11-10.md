# Supernova — Claude Master Handover

Reorganised 10 November 2026 around the NVIDIA Shield → app launch → top-navigation → supporting-screen journey. Existing observations and approval safeguards retained.

## Scope and status

This handover is **discussion-only**. Reported observations are not independently verified; proposals are not approvals. Claude must reconcile existing approved designs and active development before any change.

## 00. Reading guide and approval safeguards

### How to use this brief

- Preserve approved design decisions and the current active development state. Verify against repository documentation, latest designs and the actual installed build before deciding implementation.

- Status labels: OBSERVED = user-reported behaviour (not independently reproduced); PROPOSAL = possible change, not yet approved; INVESTIGATE = root cause or existing decision unknown; PREFERENCE = clearly expressed desired outcome.

- Version context: observations largely from Supernova 0.156; user expects a broader Network & Files redesign for 0.157. Confirm actual version/branch scope before acting.

- Do not interpret a reduction in Library Health counts as proof of the root cause. Do not assume all discussed UI decisions have been approved.

#### Relationship to existing Claude Code discussions — mandatory reconciliation

- **SUPPLEMENTARY INPUT, NOT AN OVERRIDE:** This handover records additional user observations, ideas and questions. It does not supersede prior Claude Code conversations, agreed specifications, approved mock-ups, repository documentation or ongoing development decisions, even where this handover is newer.
- **COMPARE FIRST:** Before proposing any changes, Claude must review the relevant previous Claude discussions, current project documentation, approved designs and implementation status. Where conversation history is inaccessible, state that limitation and request only the specific missing context needed; do not assume a conflict has been resolved.
- **FLAG CONFLICTS AND DUPLICATES:** Identify overlapping requirements, changed assumptions and apparent contradictions. Clearly present them to the user for discussion, without silently favouring either the earlier discussion or this handover.
- **COLLABORATIVE OUTCOME:** Use both sources as inputs to investigation and design. Discuss alternatives, trade-offs and proposed resolutions with the user, and obtain explicit approval for the agreed outcome before implementing anything.
- **NO AUTOMATIC ACTION:** Do not alter code, branches, documentation, settings or approved assets merely because an item appears in this handover.

### User transcription clarifications — 9 November 2026

- Confirmed: all five Skipping-tab settings discussed are IntroDB-related.
- Confirmed: Backup Everything view displays approximately 25–30 empty divider lines, with no settings between them.
- Confirmed: both attached local storage devices are USB devices.
- Confirmed: Movies and TV Shows are the two default library pages; users can create a third, filter-configured library page. The focus issue involves switching away from this custom page.
- Unresolved: original purpose of earlier QR-code discussion; do not assume it was for licences.
- Confirmed: the exact setting name is “Recreate sort titles”. Its purpose and behaviour still require investigation.
- Confirmed transcription correction: “Coplin” means Kotlin. Other phonetic corrections include WebDAV, put.io, Trakt, audio and Cancel; verify remaining technical labels against app source.

### Walkthrough status after 10 November 2026 tests

- COVERED: Empty Library pages show “Nothing on this page yet” when filters match no items; potential visual refinement deferred to Library redesign. Startup, navigation-memory and app-switching walkthroughs have generated observations recorded in their relevant sections. Standby was subsequently tested at 00:59–01:03 on 10 November; page retained but focus restoration failed.
- REMAINING 0.156 CHECKS: Network-interruption recovery only. Save Changes controls and performance walkthrough covered; performance findings remain unresolved, and playback-responsiveness testing is not a newly verified pass.
- DEFERRED: Seven scanning, storage and metadata-maintenance checks until redesigned Files page. Deferred means untested, not passed or failed.

## 01. NVIDIA Shield launcher and external branding

### Nvidia Shield launcher artwork and app-wide dark visual direction (9 November 2026)

- USER DIRECTION / CONTEXT: Supernova is moving away from its earlier space-blue/space-black background treatment towards a more elegant dark visual style across the application. Claude should reconcile this with its latest design discussions and approved decisions; this handover must not silently supersede them.
- OBSERVATION: On the standard Nvidia Shield TV Pro home screen, Supernova appears as an app tile/icon in the Favourite Apps row and Apps page. Its current launcher artwork may no longer complement the evolving in-app design.
- REQUEST FOR DISCUSSION: Review the existing launcher icon/tile artwork alongside the latest proposed app-wide dark visual identity. Explore whether a redesign is warranted, maintaining recognisable Supernova branding and legibility at launcher sizes.
- INVESTIGATE: Identify which Android TV launcher assets and formats Supernova actually supplies and how the Shield displays them (including relevant icon/banner variants and launcher cropping). Compare the current artwork with any previously approved icon, banner and splash branding; present mock-ups/options for discussion rather than replacing assets.
- SCOPE: This concerns the Supernova app artwork visible in Nvidia Shield's existing launcher, not redesigning or replacing the Nvidia Shield home screen itself.
- STATUS: Observation and design discussion only. No artwork change, implementation, GitHub update or replacement of previously approved assets is authorised until the user explicitly agrees after reconciliation with Claude's existing work.

### Splash-screen branding mismatch — 10 November 2026

- OBSERVED: Supernova's startup splash still uses the old space-blue/dark background, despite the move towards the new dark visual identity.
- PREFERENCE: Coordinate splash-screen redesign with the NVIDIA Shield Favourite Apps / Apps launcher icon and banner artwork. Reconcile with existing Claude-approved branding and mock-ups before any asset changes.
- STATUS: Reported observation and design direction; no new artwork or implementation approved by this handover.

## 02. App launch and startup behaviour

- Startup, loading, initial focus, resume from standby and app-switching remain **walkthrough checks**, not new confirmed defects.

### Startup and resume walkthrough — 10 November 2026, approximately 00:53 local time

- USER TEST METHOD: Repeatedly navigated to Movies, TV Shows, custom Documentaries, Search, Settings and Network & Files; returned to the Shield home screen and relaunched Supernova via its launcher icon; also switched to Google Play Store and back using the Shield's app-switching option. The exact app lifecycle state (fresh process start versus resumed existing activity) was not verified.
- OBSERVED: Splash screen appeared on some returns but not others. The app sometimes displayed Home instead of the previously viewed page; at other times the last Library page was restored. This inconsistency is not proof that a fresh launch should always restore the last page; expected startup/resume policy needs confirmation.
- OBSERVED: In one Settings test, Home content loaded while the top navigation still highlighted Settings. In Search tests, Home or the previously visited TV Shows page loaded while the Search tab remained highlighted.
- OBSERVED: Later Search and Settings tests after visiting Network & Files repeatedly returned to Network & Files with no visible focus anywhere, even though the user had left the app from Search, Settings or Settings > Library & Home.
- OBSERVED: Movies, TV Shows and Documentaries generally restored their own page after minimise/relaunch; Movies could lose top-navigation focus. Switching directly to Google Play Store and back preserved Settings and Search correctly in the user's final tests.
- INVESTIGATE: Correlate diagnostic timestamps around 00:53 on 10 November 2026 (local time) with activity/process lifecycle, restored navigation state, page-content selection, top-navigation selected state and remote focus ownership. Compare Shield launcher relaunch with task/app switching; capture the actual sequence before inferring root cause. Diagnostic export has not yet been supplied.
- STATUS: Navigation memory and app-switching walkthroughs covered, but restoration and focus issues remain unresolved; actual device standby behaviour not separately tested.

## 03. Home

### Home and Library walkthrough — Home observations

- OBSERVED: Home hero title persists correctly after navigating to Movies and returning; Home navigation/focus appears correct in this walkthrough.
- BUG / INVESTIGATE: After returning to Home, the first automatic hero transition is noticeably slower than subsequent transitions. Check carousel timer initialisation/reset and distinguish intended dwell from unintended delay.
- DESIGN PROPOSAL (not approved): Reconsider Home hero tagline-first text in favour of the description already chosen for Details hero. Short taglines (3–7 words in observed examples) make Home card text density inconsistent. The Home and Details hero sizes are being aligned; matching description and layout may improve perceived continuity when opening More Info. Explore a fixed line-height/line count and ellipsis (…) for overflow; decide clipping and transitions with Claude.
- BUG / IMPROVEMENT: Continue Watching is slow to refresh on returning from playback: resume progress bar lags behind newly watched position, and a played item previously fourth/fifth/eighth in row takes too long to move to first. Investigate playback history persistence, event notification and Home row refresh separately; expected promptly updated progress and most-recently-watched ordering.
- DEFER: Hide and Move and Customise Home are already scheduled for redesign with Claude; do not re-review current designs or mark new implementations approved.

### Hero card tagline contrast — Heart of the Beast (9 November 2026)

- OBSERVED: Hero-card text readability has improved considerably overall, but the tagline on the *Heart of the Beast* film hero card — “Survival depends on their bond.” — remains readable yet noticeably less prominent against its particular background artwork than text on other hero cards.
- INVESTIGATE: compare text legibility across varied hero artwork, especially the tagline and metadata row. Assess whether the current gradient, shadow, overlay or adaptive contrast treatment can be refined without compromising the improved design.
- STATUS: user-reported refinement opportunity; no redesign or implementation approved. Claude must investigate and discuss options before making any change.

### Hero card backdrop selection and visual balance — additional observations (9 November 2026)

- QUESTION / INVESTIGATE: Determine how Supernova currently chooses the default backdrop for a film or TV series: provider priority/order, language, popularity/votes, cached metadata, first returned image, or another mechanism. Inspect actual implementation and provider responses; do not assume an answer.
- OBSERVED: In *Heart of the Beast*, manually changing to a darker backdrop made the previously reported tagline (“Survival depends on their bond.”) substantially easier to read. This provides a concrete comparison case for Claude.
- OBSERVED: *Mr. Robot* currently displays an intensely red character backdrop; the available artwork picker also offers a darker, closer-framed character image which the user prefers visually.
- OBSERVED: *High Potential* currently uses a bright close-up of the lead actress against sky/palm trees; the user did not see an obviously darker alternative among available backdrops. Artwork selection alone may therefore be insufficient.
- OBSERVED: *Michael* has a visually glaring backdrop with bright lights behind the central character, making the hero card feel overpoweringly bright.
- PROPOSAL FOR DISCUSSION: Investigate whether automatic selection could assess backdrop luminance, contrast behind the actual text area, saturation/colour intensity and composition (including subject placement), while preserving variation and the distinct identity of each title. Avoid making all hero cards look uniform or simply forcing every image darker.
- PROPOSAL FOR DISCUSSION: Compare intelligent backdrop choice with adaptive presentation of the chosen image (e.g. localised gradient, scrim, text shadow or modest brightness treatment); consider combining approaches when no suitable darker artwork exists. Preserve user-selected backdrop overrides and do not automatically replace explicit manual choices without discussing desired behaviour.
- STATUS: observations and design exploration only. Claude must analyse actual current behaviour and discuss trade-offs/mock-ups with the user before any implementation or documentation change.

### Hero backdrop subject placement and left-side UI clearance — High Potential (9 November 2026)

- OBSERVED: The initially selected *High Potential* backdrop centres the lead character. An alternative available backdrop positions the character on the right with relatively open sky/palm-tree scenery on the left.
- CONTEXT: Supernova hero cards put the official logo, metadata, tagline/description and actions on the left. Home has More Info on the left; Details has playback and other action buttons on the left. A centred subject may be obscured by these elements, while the right-positioned alternative keeps the subject visible.
- PROPOSAL FOR DISCUSSION: Investigate artwork selection that assesses subject/salient-region placement and available negative space behind the left-side UI, in addition to brightness, saturation and text contrast. Compare actual crops/aspect ratios on Home and Details, and avoid rigidly requiring all subjects on the right.
- PRESERVE: Artwork variety, visual identity and explicit manual backdrop choices. Do not assume the first artwork is selected without inspecting source code.
- STATUS: observation and proposal only; Claude to investigate and discuss with the user before any change.

### Hero backdrop candidate scoring and hero-versus-row artwork — further observations (9 November 2026)

- PROPOSAL FOR DISCUSSION: Explore scoring/ranking every available backdrop candidate for suitability as a hero card, considering subject/face position, left-side negative space for UI, brightness, saturation, text-area contrast, composition and crop. Select the highest-scoring available option by default, recognising that none may be ideal. Investigate feasibility, performance/caching and metadata/image-analysis approaches; no algorithm approved.
- OBSERVED: On *Practical Magic 2*, the initial backdrop showed two actresses centred back-to-back with dark areas on either side. The left-aligned genre labels (Comedy, Romance, Fantasy) overlapped an actress’s face; Fantasy was less legible. The artwork picker offered alternative images, including one with characters towards the right and a largely dark left side, and another with candles to the right and a largely black left side. User manually chose the right-positioned characters version; Fantasy and the other genre text became clearer.
- OBSERVED: Manually changing *Heart of the Beast* to a darker backdrop also changed its artwork in the Recently Added row. Determine whether this is intended shared-artwork behaviour or simply how the current asset selection is stored.
- INVESTIGATE / DISCUSS: Should scoring and auto-selection apply only to large hero backdrops (Home and Details), or should the same chosen artwork be reused in ordinary library rows? Row cards mainly show small bottom-positioned labels such as Resume, S3 E1 and progress bars, so their visual requirements differ. Compare shared selection against independent hero and row artwork, including consistency, storage, manual overrides and user expectations.
- PRESERVE: Do not overwrite explicit manual artwork choices automatically; do not force every backdrop to look alike. Consider adaptive text/image treatment where no suitable candidate exists. All points remain discussion-only; Claude must investigate and obtain user approval before any change.

#### Hero-card metadata consistency across Home and Details — Practical Magic (9 November 2026)

- OBSERVED: The *Practical Magic* Home hero card shows “2026 · Romance, Fantasy, Comedy”, while opening its Details page displays the same genres in a different order: “Comedy, Romance, Fantasy”. The issue is inconsistent presentation, not missing genre data.

- INVESTIGATE: Determine why Home and Details order the same metadata differently (data-source order, sorting, UI formatting, cache or distinct presentation code). Verify actual implementation before identifying a cause.

- PROPOSAL FOR DISCUSSION: Define an agreed metadata-row content and ordering specification for Home hero cards and Details hero cards for film, TV series, season and episode. Specify which fields appear, their order and formatting, and deliberate context-specific exceptions; do not assume all four content types must display identical fields.

- COORDINATE: Review metadata content and ordering together with previously discussed hero-card resizing, layout and consistency across Home and Details. Retain clear differences where relevant to episode/season context.

- STATUS: User observation and discussion request only. Claude must investigate, present findings and discuss options before any implementation; no changes authorised.

### Home-row information placement versus Library-card captions (9 November 2026)

- OBSERVED: Home rows such as Continue Watching place the title, Resume label, season/episode identifier (e.g. S3 E7) and playback progress bar over the artwork. Other Home rows, including Recently Added and Trending on Trakt, typically overlay the title alone.

- OBSERVED: Library pages use poster artwork with a distinct information box beneath it, containing title and year. This differs from the Home-row overlay treatment and may be a visual-consistency and legibility issue.

- PROPOSAL FOR DISCUSSION: Compare moving Home-row titles, Resume status and episode information into a separate caption area beneath the artwork, potentially with a controlled background colour for predictable contrast, against retaining the current overlays.

- INVESTIGATE: Evaluate the impact on card and row height, number of visible rows, focus/zoom behaviour, overall Home layout, and differences between landscape Home artwork and portrait Library posters. Consider a hybrid in which the progress bar stays on the artwork while text moves beneath it.

- COORDINATE: Review this alongside hero-versus-row artwork selection and app-wide card consistency. Removing text overlays may reduce the need for backdrop-specific text contrast treatment on rows, but does not resolve hero-card requirements.

- STATUS: User is asking for evaluation and mock-up discussion, not requesting or approving a design change. Compare with existing Claude decisions and approved designs before proposing any implementation.

### Startup artwork loading and carousel flicker — 10 November 2026

- OBSERVED: On repeated app launches, the Home layout and Continue Watching row structure appear before the row artwork. The visible tops of Continue Watching cards initially show empty artwork boxes, then images populate. Text is outside the visible portion of the row by the intended layout; this is not a text-rendering defect.
- OBSERVED: Hero-carousel artwork visibly loads/flickers during startup. The centred hero is comparatively stable (minor flicker), while the adjacent partially off-screen hero cards exhibit much more noticeable artwork loading and flickering.
- INVESTIGATE: Image loading, prefetching, cache readiness, view recycling and transitions for centre versus neighbouring hero cards and for Continue Watching. Check whether placeholders, preloading or transition timing would improve perceived startup quality without masking errors. No technical cause confirmed.
- STATUS: User-reported performance/visual issue, not an approved implementation solution.

### Rapid navigation and hero carousel asymmetry — 10 November 2026 (0.156)

- TESTED / PERFORMANCE: After launching Supernova from a fully closed state, user held Left/Right across top navigation for approximately 20–30 seconds. No unexpected behaviour or meaningful responsiveness issues noticed.
- TESTED / HERO CAROUSEL: Holding Right and Left to advance hero cards rapidly was generally smooth, with no major performance slowdown. Slow single-step navigation did not visibly show the issue below.
- OBSERVED / VISUAL DEFECT: During sustained Right input, the partly visible **right** neighbouring hero preview appears noticeably thinner than the left preview. During sustained Left input, the partly visible **left** preview appears noticeably thinner than the right. Difference appears directional and transient during rapid navigation, not obvious when stepping slowly.
- INVESTIGATE: Compare neighbouring-card scale, clipping, translation, animation progress and viewport calculations under repeated directional input. Confirm on actual build before attributing a cause.
- DESIGN DEPENDENCY: Claude is already redesigning/resizing the Home hero cards. Evaluate and address this behaviour **within that existing approved/planned redesign**; do not create a competing redesign or assume new hero dimensions. Reconcile latest Claude mock-ups/specifications first.
- WALKTHROUGH STATUS: Performance testing marked **covered at user request**, not bug-free. Earlier app freeze, artwork loading and playback issues remain open for investigation. Library-scrolling issues had already been reported; playback-responsiveness checks were explicitly left for a later date and should not be described as freshly passed. Network interruption recovery is the only remaining active 0.156 walkthrough area. Seven Files checks remain deferred until 0.157.

## 04. Library — Movies, TV Shows and custom pages

#### Shared Library pages, watched state and filtering

- GLOBAL DESIGN REQUIREMENT: Movies, TV Shows and custom pages (e.g. Documentaries) should share reusable layout, components, menu controls, focus rules, sorting and filtering behaviour where applicable; investigate rather than presume implementation is copied. Prior top-nav blur/solid-bar and focus-loss findings remain outstanding. Ensure consistent buttons, statistics and layout while allowing content-specific differences.
- OBSERVED: Mark Watched from Details changes menu to Mark Not Watched, but item remains in the same Library grid after app restart. This proves watched state changed, not that Hide Watched is broken. INVESTIGATE actual Hide Watched setting/filter behaviour and its discoverability; PROPOSAL: clear All / Watched / Unwatched / In Progress filtering and optional subtle watched indicator on mixed grids. Do not assume watched items should disappear unconditionally.
- FILTERS MENU PROPOSAL: Visually separate Clear Filters (an action) from Genre, Year and Streaming Services (filter categories). In genre list, focus outlines sit too close to separator around All Genres/Action; increase spacing.
- OBSERVED WORKING: Year filter lists years represented by Library and filtering works; streaming-service filtering works. PROPOSAL: apply each filter selection immediately, remove Done, move Clear Selection to top with separator before choices, consistently for Year, Streaming Services and comparable menus. Check actual update behaviour before implementation.
- FILTER CHOICES PROPOSAL: Genre, original language and studio/network options in Edit Page should reflect available content in applicable Library/page, not offer selections producing empty results without reason. User examples: CN language, Turkish, and Chrysler Corporation network show no items. Investigate whether availability should be based on entire Library, page scope or current filters; avoid unstable disappearing choices. Consider appropriate genre/network icons and redesign fallback globe.

#### Sorting and Grid/List presentation

- GLOBAL SORTING INCONSISTENCY: Movies shows Release Date; TV Shows shows Air Date; Documentaries shows Release / Air Date and Year. Audit and deduplicate all sort options across three pages. PROPOSAL: one consistent Release / Air Date sort label using correct underlying date by media type; assess whether Year is distinct or redundant. Standardise ordering and semantics without erasing meaningful media differences.
- INVESTIGATE sorting correctness and usefulness: Date Added, Title and Release Date seem plausible; Trakt Trending/Popular need checking; Year versus Release Date may overlap; Resolution appeared to sort alphabetically. Runtime, Resolution, HDR, Audio, File Size, Codec, Bit Rate, Container and Path are not visible in Grid but can be shown as List columns. Discuss whether Grid should offer a narrower set of sorts or all sorts; sorting by hidden attributes can still be legitimate. Do not approve removal without testing.
- CLARIFICATION: List columns control what metadata is displayed, not sort order. List view design needs separate future refinement.
- BUG / DESIGN: Newest First / Oldest First toggle works, but up/down arrow icon does not change with state; button width visibly changes between labels despite similar length. Check existing global icon decision and layout sizing.
- LIST COLUMNS PROPOSAL: Remove Done if changes apply instantly; move Reset to Default to top with separator. Remove non-actionable Titles (Always Shown) ticked entry if title cannot be toggled; title should remain visible by default.

#### Custom Library Edit Page observations — reconcile with already redesigned screen

- CONTEXT: Edit Page already has a redesign planned/approved with Claude. Compare findings against that design; do not restart or supersede it. Applies consistently to TV Shows and other Library pages where shared.
- Page Name keyboard: heading Page Name duplicates selected option and crowds/overlaps input; large empty area above. Investigate whether to remove heading or reposition vertically.
- Includes Movies / TV Shows / Both works. Genres and Match currently share same icon despite different functions; follow global unique, identifiable monochrome icon rules.
- GENRES submenu: back-arrow/title header may be unnecessary on TV remote; if retained, use normal focus outline and separator; menu is far wider than content (widest choice Action and Adventure). Selected Documentary uses small blue tick: replace with agreed clean white tick consistently app-wide. Genre choices currently lack icons.
- GLOBAL MENU RULES: Size pop-ups relative to longest item with reasonable padding; avoid huge unused width. Header/back focus is thinner than option focus, and separators are missing; apply consistent remote-navigation design. Consider whether Any / All inline text should use buttons like other inline actions, distinguishing selection controls from action buttons.
- YEAR submenu: overly wide, blue selection tick; From / To arrow controls have blank centre where selected year should appear, despite arrow presses changing Library results — functional defect. Review quick year presets and consistent header/separator/focus. Avoid removing preset options without discussion.
- Watched selector cycles All / Watched / Unwatched / In Progress via arrows; consider four visible inline choices. Minimum Rating cycles Any / 5 / 6 / 7 / 8 / 9; consider inline choices if space allows.
- Original Language submenu: overly wide, thin focus header, no separator, blue tick; globe fallback icon needs design review; CN option shows no content. Studio/Network list long, includes Chrysler Corporation with no results; consider icons and only applicable values.
- EDIT PAGE COUNTS: Current heading displays e.g. 41 title matches and 123 GB. If underlying Library statistics update in real time, investigate whether this duplicates information and should be removed. This is about existing Edit Page UI, NOT a request to review new 0.157 full statistics.
- STATUS: All changes are observations/proposals for Claude to reconcile; no implementation, GitHub changes or approval implied.

### Home, library and context menus

- INVESTIGATE: status of Customise Home redesign; current implementation looks different from old version but does not match latest approved background, colours or design language.

- PROPOSAL: review whether More Info is redundant in library long-press menu when OK already opens Details; if retained, consider “Details”.

- PREFERENCE: library long-press menu should offer a discoverable Fix Match/Change Info action, plus clearly distinct Remove from Library (keep file) and Delete File (destructive) where supported.

- OBSERVED: Add to Row offers Watch Next or creation of a new row using keyboard; verify that creation, insertion and persistence work.

- PROPOSAL: revisit Details “More” name, three-dot icon, positioning and whether it should share a consistent context-menu design with library long-press.

- OBSERVED: TV-series More menu has Mark Watched, Change Info of All Episodes, Add to Row, Remove from Library, Delete and Artwork. Review action applicability, naming and distinctive monochrome icons.

- OBSERVED: Artwork submenu (Posters/Backdrops) overlays More menu rather than replacing it; consider a single active menu layer and Back to return.

- BUG OBSERVED: artwork focus border does not contain enlarged image; first/last rows clip artwork and outline. Apply previously agreed white outline and subtle inner shadow, and unify selected white tick; update background to new design colour.

### Metadata mismatches and Library Health — high-priority investigation

- OBSERVED CASE: TV library displayed “The Roller Coaster” (1982), 1 season/1 episode, though user does not own it. Technical file path appeared to reference Father Ted, and playing the file confirmed Father Ted.

- OBSERVED CASE: Father Ted previously showed 4 seasons/5 episodes (user says count wrong). After manually fixing the mismatched item to Father Ted, The Roller Coaster disappeared and Father Ted episode count rose from 5 to 6.

- OBSERVED CASE: Library Health “Missing artwork or details” count fell from 170 to 169 after correction. This is correlation, not proof that all 169 are incorrect matches or that the app knew the metadata was wrong.

- INVESTIGATE: why an evidently unrelated programme matched a Father Ted filename; inspect parser, naming, metadata provider queries, ID assignment, cache, duplicate handling, folder/series association and re-scan logic using logs and reproducible samples.

- INVESTIGATE: exact meaning of Library Health “Missing artwork or details”: displayed in library, hidden, incomplete metadata or incorrect matches? Improve explanatory text and separate failure categories if needed.

- INVESTIGATE: whether global rescrape/re-identification exists, whether safe automated repair can be offered, and how to avoid destructive or erroneous mass rematches. User prefers automatic correction but paused manual repairs pending investigation.

- OBSERVED: Fix Match side panel pre-fills current title, searches by title/TMDB/IMDb ID, shows keyboard and matches; initial focus unclear, keyboard collapses on Down towards results, and typing may lose visible focus then snap to “1” on Right.

- PROPOSAL: offer quick Fix Match from Details/Library, and potentially a full-screen investigation flow from Files/Library Health. Verify redesigned Files capabilities before choosing a shared flow.

### Top-navigation fade and custom Library-page focus — 9 November 2026, ~21:30–21:33 local time

- OBSERVED / EXPECTED: Content should scroll behind the top navigation bar with a subtle fade/blur, and the effect should clear once content is no longer beneath it.
- BUG OBSERVED: Movies and TV Shows retain some blur after returning to the top, potentially delayed. User-created Documentaries additionally shows a solid horizontal bar beneath navigation.
- DESIGN CONSISTENCY REQUIREMENT: Default Movies, default TV Shows and user-created Library pages must share the same layout and interactions: statistics, buttons, dividers, poster grids, focus effects, scrolling and navigation fade. Content and filters may differ. Investigate shared components and custom-page state; do not assume different source code solely from visual differences.
- REPRODUCED BY USER: Leaving Documentaries for Movies, TV Shows or Home makes top-navigation focus/underline disappear. Navigating among Movies, TV Shows and Home without departing Documentaries preserves visible focus; entering Documentaries also preserves focus. Thus the failure is specifically triggered on leaving the user-created Library page.
- CONTRAST CASES: Documentaries → Network & Files places focus visibly on its first tile; Documentaries → Search focuses keyboard; Documentaries → Settings focuses first settings tile. These transitions do not exhibit the same invisible-focus symptom.
- OBSERVED RECOVERY: After Documentaries → Home loses visible focus, pressing Down restores visible focus to Home.
- CLAUDE INVESTIGATION: Inspect focus ownership, navigation focus restoration, custom-page teardown and transition timing, including why only navigation destinations Home/Movies/TV Shows lose the indicator. Reproduce on current build and correlate with diagnostics if supplied.
- SEPARATE OPEN DESIGN QUESTION: In the upcoming renamed/redesigned Files page, decide where focus should land after navigating from the top bar; do not assume today's Network & Files first-tile behaviour is approved for the redesign.
- DIAGNOSTICS: User reports occurrence approximately 21:30–21:33, 9 November 2026 (local time). Diagnostic export was mentioned but has not been supplied in this chat.
- STATUS: Discussion/investigation only; no implementation or GitHub changes authorised.

## 05. Files — sources, scanning and transfers

- Search page redesign has already been discussed with Claude. No new 0.156 walkthrough findings are being added here; preserve the previously agreed design.

### Network & Files: 0.156 observations / 0.157 planning

- OBSERVED: redesigned local-storage file/folder browser already partly present in 0.156. A folder row opens on OK, but also contains an inline Open action alongside Remove and Save; Open appears redundant.

- INVESTIGATE: whether inline Open serves keyboard/remote discoverability or other contexts before removing; review Remove/Save semantics and focus.

- PREFERENCE: any new transfer and metadata-correction flows should align with the broader 0.157 redesign, without assuming the old tile structure persists.

### Files navigation — attention-needed indicator (9 November 2026)

- EXISTING CLAUDE DESIGN CONTEXT (NOT A NEW HANDOVER REQUIREMENT): The Files page redesign and its scan-activity spinner have already been discussed and designed with Claude. Claude already knows the navigation rename and the planned replacement of the bottom-right scan pill; do not reopen or restate those settled design choices as new work.

- NEW PROPOSAL FOR DISCUSSION ONLY: Explore a discreet indicator next to the Files title when scanning, files or folders need user attention. For example, a small count of four outstanding issues could appear near the existing scan indicator; the number and styling are illustrative, not approved.

- INVESTIGATE WITH CLAUDE: Which issues genuinely require action; how counts are aggregated and cleared; where selecting Files leads; simultaneous scan activity and attention alerts; remote focus and accessibility; and compatibility with the already agreed design.

- STATUS: Discuss and reconcile the new attention indicator with Claude's existing Files redesign before approving any implementation. Do not change the agreed page name or scan spinner based on this handover.

### Proposed Media Transfers feature — discovery only

- GOAL: take advantage of >400 GB of user-reported free space across two USB devices connected to Nvidia Shield, reducing paid put.io cloud-storage reliance.

- PROPOSAL: flag film files or TV series folders on put.io for transfer to preconfigured local destinations; recognise media type and select distinct default film/TV folders.

- PROPOSAL: make Media Transfers source-independent: any compatible non-local connection already added through Network & Files (WebDAV, NAS, network shares, put.io, other cloud services) should be considered as a possible source of media files/folders for transfer to storage directly attached to the Supernova device, including configured USB destinations. Distinguish copy from move; do not presume source deletion.

- SCOPE EXCLUSION: Dropbox, Google Drive, Icedrive and similar services were illustrative examples only. Do NOT investigate or propose adding new storage providers as part of this feature. The question is how to offer the same transfer action across any suitable share/source that Supernova already supports, irrespective of whether it is cloud-hosted, WebDAV, NAS or another network connection. Assess transfer capability for existing connection types only where necessary to design the common feature.

- SCOPE CLARIFICATION: this concerns media files (films, TV programmes, episodes) and folders containing them, not a general-purpose downloader for arbitrary documents. Connected sources populate the Supernova library; transferring media locally should preserve library continuity and not require a separate put.io-specific workflow.

- PROPOSAL: queued transfers, progress, pause/resume, scheduled window (example 02:00–07:00), manual “start now” override and manageable transfer status UI.

- PREFERENCE: original cloud media remains playable while transfer is pending or running; maintain library identity and playback progress, avoid duplicate entries.

- INVESTIGATE: Android TV storage permissions, reliable background scheduling, source protocol support, interruption/restart, available space, integrity verification, and handling of source deletion only after successful verified copy.

- PROPOSAL: integrate transfer controls into redesigned Network & Files and suitable Settings tabs; do not assume feature is approved for 0.157.

### File deletion — simplify without losing safety

- OBSERVED: local film deletion: More > Delete > Delete Source File > another confirmation > Android “Allow Supernova to delete this video?” permission. User reports excessive repeated confirmation.

- OBSERVED: put.io file deletion: More > Delete > Delete Source File > second Delete Source File confirmation > success; no Android prompt in observed path.

- PREFERENCE: retain a clear destructive warning but avoid duplicate Supernova confirmations; Android permission may be mandatory and should not be bypassed.

- INVESTIGATE: precise confirmation flow, local/remote differences and logs around 11:37 on 8 November 2026; verify deletion outcomes and handling of failures/cancellation.

### Walkthrough deferral pending redesigned Files page — 10 November 2026

- USER ASSESSMENT: Current Network & Files features appear functional in the user's limited use, with no specific functional failure observed; however, the six-tab landing layout is due to change and the present experience is not sufficiently usable for meaningful full testing. Only Local Storage has received a substantial redesign; other tab interiors retain older blue-panel layouts.
- DEFERRED (NOT PASSED): manual scanning and newly detected media; automatic scanning; background scanning; cancelling scans; unavailable/disconnected storage and reconnection; Library Health presentation; preservation of manually corrected metadata after rescanning.
- PLAN: Resume a full Files-focused walkthrough after the redesigned Files page is available in a future build (currently expected with 0.157). Do not treat the existing functionality as comprehensively verified or reopen already agreed Claude designs merely because testing is deferred.

## 06. Search

## 07. Settings and About

### Settings — playback, audio and subtitles

#### Playback > General / Resume / Skipping / Picture

- OBSERVED: General and Resume toggles are broadly appropriate but alignment varies.

- PROPOSAL: Skipping has five confirmed IntroDB-related settings, each opening a four-choice menu; show choices inline if feasible.

- PROPOSAL: Automatic refresh-rate switching has two modes (Disabled; Display mode selection matching video FPS); Dolby Vision mode has three. Consider inline options.

#### Audio & Subtitles

- OBSERVED: Preferred audio language and preferred subtitle language pickers have long lists, left-side radio dots and bottom Cancel; online subtitle search language uses a right-side white tick and Done. Unify picker patterns.

- PROPOSAL: surround sound four-choice selection could be inline; Outline and Background on/off should be normal toggles.

- BUG OBSERVED: Subtitle Style > Vertical Position and Background Opacity can increase but pressing Left jumps focus to Style tab instead of decreasing. Background Opacity was 75% during observation.

- INVESTIGATE: Custom font folder currently launches Android system picker; determine whether the redesigned Files browser is appropriate or whether system picker is required for permissions.

- PREFERENCE: existing small blue tick for playback-card audio/subtitle selection looks out of place; apply consistent white-tick design and previously discussed metadata pills.

### Settings — library, integrations, system

#### Library & Home; Details; Where to Watch

- INVESTIGATE: “Recreate sort titles” references collections no longer in app; its arrow suggests navigation but activation does nothing. Establish actual purpose, then fix, move or remove.

- PROPOSAL: standardise Details language picker. OMDb API key should remain as currently configured for now; review whether Library & Home is the right location for this third-party integration.

- PREFERENCE: Where to Watch country picker should show country flags and consistent selected state.

- OBSERVED: “Your services” groups Selected Providers and Other Providers, but newly selected providers only move groups after closing/reopening. PROPOSAL: animate immediate regrouping without losing focus.

- INVESTIGATE: JustWatch row shows a navigation arrow but activating reports “No application can handle this action”.

#### Accounts & Services consolidation

- PROPOSAL: IntroDB enable/disable is in Accounts & Services while skipping configuration is in Playback. Review co-location of integration switch and options.

- PROPOSAL: OpenSubtitles enable/login may belong alongside subtitle settings. Trakt account, scrobbling, watched syncing and resume-point options might share a renamed section with Where to Watch; no final arrangement approved.

#### System > Backup / Restore

- BUG OBSERVED: “What to include” offers Everything, Settings only, Custom; Right does not change inline selection, Left jumps to Backup tab, OK cycles options. With Everything selected, ~25–30 genuinely empty divider lines appear (confirmed by user; no settings displayed between them); Custom exposes ~25–30 individually separated checkbox rows.

- PROPOSAL: redesign backup inclusion from scratch with meaningful categories, manageable focus and clear preset/custom behaviour; retain useful fine-grained control without an excessively long list.

- OBSERVED: “What’s in a backup” and “How restore works” show large blue full-page information pop-ups. Redesign presentation and readability.

#### System > Diagnostics

- INVESTIGATE: exporting diagnostics opens Android file picker; determine system picker versus in-app Files browser based on storage access rules.

### Default diagnostic export folder — new proposal, 10 November 2026

- OBSERVED: Choosing **Save Diagnostic Report** currently opens the standard Android file/folder picker and requires choosing an export destination on every occasion. The user finds the picker unattractive and the repeated prompt inconvenient.
- USER PROPOSAL (DISCUSSION ONLY): In **Settings → System → Diagnostics**, offer **Default Export Folder**. Select and save a preferred destination using Supernova’s redesigned native file/folder browser. Thereafter, **Save Diagnostic Report** should write directly to that folder without prompting on every export.
- USER PREFERENCE: Retain a simple, remote-friendly workflow and make the destination configurable. This is a proposed feature, not an approved implementation or a request to replace every Android system picker.
- INVESTIGATE WITH CLAUDE: First reconcile any prior discussions/decisions about Android's Storage Access Framework picker versus Supernova’s own browser, and whether the redesigned Files browser can legitimately obtain and retain Android write permission. Consider persistence across app restarts, USB removal, invalid paths, permissions, filename collisions and failed writes; avoid silently losing diagnostic data.
- OPTIONAL DESIGN IDEAS FOR DISCUSSION: Show a brief saved confirmation; allow changing/clearing the folder; use a safe fallback picker or error message if no default exists or the destination becomes unavailable. Do not assume these details are approved.
- STATUS: New feature proposal to discuss with Claude alongside the existing Diagnostics and Files redesigns. No code, settings, or approved designs changed.


### Diagnostic export progress and in-page completion feedback — 10 November 2026

- OBSERVED: After Save Diagnostic Report opens the Android file/folder picker, the user selects a destination and returns to Supernova’s Diagnostics settings. The app remains responsive, but shows no indication that report generation or writing is underway during a noticeable delay. Later a small bottom-centre system-style information pill says “Diagnostic report saved”. The user believes this is Android-provided, but its origin is not verified.
- USER PREFERENCE / PROPOSAL FOR CLAUDE DISCUSSION: Provide immediate, real-time export status **inside Settings → System → Diagnostics**, starting as soon as control returns from the destination picker (or immediately when using the proposed default folder). Show meaningful stages such as preparing/generating and saving/exporting when technically distinguishable, then a clear in-page success state only after the report is actually written. Avoid an unexplained silent interval.
- USER PREFERENCE: Replace the generic bottom-centre completion pill with Supernova-native in-page feedback where the app controls that notification; verify whether any Android system messaging is mandatory before proposing removal. Keep feedback readable and remote-friendly without blocking navigation unnecessarily.
- INVESTIGATE: Existing export pipeline, asynchronous state, timing, callback/completion semantics, error reporting, destination permissions, file integrity and failure/retry handling. Do not fabricate percentage progress if no reliable measurement exists; an activity indicator and descriptive state may be more accurate.
- COORDINATE: Design together with the **Default Diagnostic Export Folder** proposal so manual destination selection and one-click default-folder exports share the same status, completion and error experience. Check earlier Claude design decisions before implementing.
- STATUS: User-reported UX gap and proposed improvement; not an approved design or implementation request. No diagnostic file was supplied for independent analysis.

- PREFERENCE: add explicit Clear Diagnostics action, ideally optionally offered after export; preserve logging on/off.

- PROPOSAL: configurable rolling diagnostic retention (e.g. 1, 2, 3 weeks) to bound file size; determine safe defaults, size caps and deletion semantics.

- OBSERVED: apparent UI lag after exiting System settings at 01:05 on 8 November 2026. Correlate with diagnostic data if available.

#### System > Advanced

- PROPOSAL: Audio decoder three-choice menu inline; clarify whether PCM playback speed engine (FFmpeg/atempo vs Sonic; transcription uncertain) is one segmented control or two buttons.

- INVESTIGATE: Subtitle encoding picker contains “Device language (CP1252)” and other code-page labels. Explain code pages in plain English; review whether flags are meaningful (encodings are not necessarily countries). Standardise selected-state tick and Cancel behaviour.

- PREFERENCE: remove obsolete Issue Digest entry and any other Issue Digest surfaces after verifying diagnostics cover the intended reporting function; diagnostic capture should be automatic where appropriate.

### About Supernova — major overhaul

#### Release notes

- OBSERVED: version list is full-width and contains verbose development/QA descriptions and timestamps. User wants version number plus date (day–month–year; time optional) in list; detail on selection.

- BUG OBSERVED: OK/click does not consistently open historical versions, whereas Right may open them; Left may jump to tab. Fix directional and select behaviour.

- OBSERVED: 0.156 notes contain only two broad, reportedly inaccurate lines; 0.155 and older versions can have fuller entries, but multiple unrelated changes are combined in single bullets (example 0.138).

- PREFERENCE: meaningful but concise release notes for every listed version, grouped into distinct changes rather than one line spanning unrelated features; validate from actual changelog/release records.

- PROPOSAL: redesign release-note list/detail layout rather than a full-width list followed by a separate full-page view.

#### Open-source licences, credits, technical information

- BUG OBSERVED: “Find in notice” briefly appears on switching to licences tab; within licence detail it is an oversized control opening keyboard and highlighting matching terms. Review its purpose and presentation.

- INVESTIGATE: AndroidX/Kotlin/JetBrains and Sentry component entries show lengthy or multiple version strings; verify accuracy and formatting, including the two Sentry versions reported.

- PROPOSAL: revisit licence display and potential QR links (original QR-code discussion context unconfirmed); first check legal attribution and licence-text obligations. A QR code must not automatically replace legally required notices.

- BUG OBSERVED: on licence and credits lists, Up sometimes does nothing while Left returns to tab and Right opens item. Fix directional focus semantics.

- PREFERENCE: revisit credits/acknowledgements and technical information designs as part of overall About redesign; app information is comparatively acceptable.

- INVESTIGATE: prior discussion about QR-code size/placement — recover approved context before adopting.

## 08. Details

- Playback HUD and playback settings redesign have already been discussed with Claude. This section is a reference point only; do not reopen settled designs.

### Context-aware Details page

- PROPOSAL: preserve one coherent visual design but adapt metadata, actions and More menu to four contexts: film, TV series, TV season and TV episode.

- INVESTIGATE: for series/season/episode, ensure Mark Watched, playback, artwork, Fix Match, Remove from Library and Delete have explicit scopes and appropriate confirmation.

### Details-page tab-title styling reference — Starlight (9 November 2026)

- REFERENCE: User supplied screenshot of https://starlight.astro.build/getting-started/ showing the npm / pnpm / Yarn horizontal tabs, with a thin line running under the full tab group and a short, bright underline immediately beneath the selected tab label. Screenshot highlights the tab strip, not the command content.
- CONTEXT: Supernova has tab navigation in several places. The Details page was recently redesigned in discussions with Claude but the redesign has not yet been implemented, according to the user. The user wants to revisit the appearance of Details-page tab titles specifically, without assuming the existing approved design is obsolete.
- INVESTIGATE: Review the latest agreed Claude Details-page mock-ups/specification and actual current tab implementation. Compare their tab labels, alignment, spacing, dividers, active-state indicator and transitions against the supplied Starlight reference; identify what, if anything, would improve readability and visual consistency.
- ANDROID TV CONSIDERATION: Distinguish the selected/active tab from the currently D-pad-focused tab; propose visible, accessible states for both, and test left/right navigation, focus restoration and legibility at TV viewing distance. Do not assume web hover/focus behaviour transfers directly to TV.
- PROPOSAL FOR DISCUSSION ONLY: Consider a full-width subtle baseline plus a short high-contrast underline under the active tab label, with appropriate label colour and a distinct focus treatment. Present options or mock-ups and compare against the previously approved design before any change.
- STATUS: Visual inspiration and request for investigation/discussion only. Reconcile with earlier Claude decisions; no redesign or implementation authorised.

## 09. Playback and HUD

## 10. Cross-app navigation, visual consistency and performance

### Cross-app design and navigation requirements

#### Settings information architecture and clarity

- PREFERENCE: Audit every existing setting, tag by function, review placement, wording and description, and regroup logically into the redesigned tab-based Settings page. Do not mechanically retain eight old tiles or constrain the new design to five tabs; tab titles take precedence over optional top-right information.

- PREFERENCE: Use understandable titles and descriptions in Settings and Network & Files; rename or relocate only where genuinely helpful. The single-setting Appearance tile (interface language) is a clear consolidation candidate.

- OBSERVED: setting title, description and toggle often sit at inconsistent vertical positions within focus areas. Audit alignment across all tabs and settings.

- PREFERENCE: show short sets of choices inline instead of opening additional menus, where remote focus and legibility permit. Long language/encoding lists can remain menus.

#### Selection, menus, focus and visual consistency

- PREFERENCE: standardise selected-state treatment across audio/subtitle tracks, language/country pickers, service providers, artwork and other lists. User prefers a larger white tick on the right rather than small blue ticks or left-side radio circles. Final component design should be reviewed.

- PREFERENCE: flags for applicable language/country lists; provider branding consistent with approved coloured provider logos on Details screens, subject to the final visual system.

- PROPOSAL: remove bottom Cancel/Done actions from long pickers where remote Back safely dismisses without losing intended selections; validate accessibility and any uncommitted changes first.

- OBSERVED: directional focus jumps, disappears, opens items unexpectedly or returns to tabs. Treat remote-control navigation as an app-wide acceptance/testing concern, including hold-to-scroll, focus restoration, keyboard and edge clipping.

### Remote-control navigation — reproducible observations

- OBSERVED: holding Down on TV library causes apparent jumps to top during long scroll; individual presses generally work. Holding Up from bottom also behaves unpredictably, sometimes placing focus on top navigation Home while TV Shows content remains visible.

- OBSERVED: switching library tabs can leave focus indicator invisible; e.g. navigate Home, Movies, TV Shows, user-created third library page (configured using filters), then back to TV Shows.

- PROPOSAL: define Back behaviour from middle of library (scroll to top and focus TV Shows navigation tab, Filters or first library item). Decide deliberately and test all tabs.

- INVESTIGATE: diagnostic correlation around 02:15 on 8 November 2026; distinguish focus state, list recycling, asynchronous content loads and animation issues rather than assuming a cause.

### Cross-page focus restoration — repeatable user walkthrough, 10 November 2026

- OBSERVED: Content and selected tab can disagree after return to Supernova: Settings or Search remains highlighted while Home or TV Shows is displayed. After using Network & Files, the app may reopen on that page regardless of the last selected Search/Settings screen, with no visible focus. On Network & Files, pressing Down can restore focus to Home while Files content remains visible.
- CONTRAST: Returning from Movies, TV Shows and Documentaries generally preserves page content; direct Shield app switching preserved Search and Settings in the final tests. This suggests multiple return paths to compare, not a confirmed lifecycle cause.
- INVESTIGATE: Single source of truth for active page and selected navigation item, restoration ordering, focus hand-off, task resumption and recreation; compare with earlier Documentaries-to-other-page focus loss. Diagnostics window approximately 00:53 local time, 10 November 2026. User has not yet provided the diagnostic report.
- STATUS: Observed and repeatedly tested by user; not independently reproduced or fixed.

## 11. Documentation and development workflow

### Supernova documentation platform — proposed GitHub / Zensical / GitHub Pages / GitHub Actions workflow (9 November 2026)

- USER PROPOSAL FOR CLAUDE DISCUSSION: Consider using the same documentation approach being planned for MediaHarbour: GitHub as the authoritative storage for Markdown documentation, Zensical for building a navigable documentation website, GitHub Pages for hosting the published site, and GitHub Actions for automated publishing (subject to Claude’s review).

- RECONCILE FIRST: Review the latest Claude discussions, current GitHub documentation structure, existing Google Drive research hub and interim wiki drafts. Do not assume Zensical, GitHub Pages or GitHub Actions are already configured, approved or suitable for this repository.

- DISCUSS: Information architecture, documentation ownership, workflow from Claude/Codex to GitHub, approval/status tracking, drafts versus authoritative content, navigation/search, automatic publication, versioning, handling research and handovers, and maintaining approved mock-ups without disturbing development branches.

- CURRENT DRIVE CONTEXT: The consolidated Supernova Google Drive folder holds APKs, Claude/Codex handovers, historical NOVA material, a research hub and interim wiki drafts. These interim drafts must be reconciled with Claude before promotion to authoritative GitHub documents.

- STATUS: Exploration and discussion only. No site deployment, GitHub restructuring or workflow change authorised.

### Standby, performance and put.io playback — 10 November 2026

- OBSERVED / STANDBY: At 00:59, Movies page with *The Bodyguard* focused; Shield placed into standby. At 01:03 after waking, Movies page remained but focus was invisible; Up, Left and Right had no apparent effect; Down restored focus to Home in the top navigation while Movies content stayed visible. Investigate focus restoration and navigation/content mismatch, not simply page persistence.
- OBSERVED / SAVE CONTROLS: User has not seen additional persistence or save-confirmation problems beyond previously documented issues. No exhaustive re-test claimed.
- OBSERVED / FREEZE: At approximately 01:07, a noticeable freeze occurred in Supernova. Unknown whether originating in the app, Shield or another component. User subsequently fully closed and relaunched Supernova.
- OBSERVED / CONTINUE WATCHING: *Dexter: Resurrection*, season 2 episode 2, played for approximately 5–10 minutes, showed a resume progress bar yet remained in Recently Added rather than being promoted to Continue Watching. Verify actual Continue Watching eligibility, progress persistence, refresh and ordering, and whether simultaneous presence in Recently Added is intentional. User expects started content to become available in Continue Watching. Do not presume the claimed season/episode metadata is independently validated.
- OBSERVED / PUT.IO PAUSE: Same title on put.io, sought to about 17:18 and playing at 17:25, paused at approximately 01:09. At approximately 01:11, first attempt to resume returned to Details instead; user could not confirm which remote button was pressed, so input error is possible. Restarted playback, sought to about 18:30 and paused again. On a later confirmed Play press (approximately 01:12), a brief visual glitch occurred, but playback resumed and audio remained in sync during subsequent viewing. A prior audio desynchronisation after a long pause was reported historically, NOT reproduced in this test.
- INVESTIGATE: Playback lifecycle and pause duration, put.io buffering/stream reinitialisation, decoder/timestamp handling, AV sync, player-to-Details navigation and remote input events. Correlate actual diagnostic records; do not claim cause from observations alone. Diagnostic export not yet provided.

### Diagnostic timestamp index — user-reported local times (local)

| Date | Approximate time | What Claude should inspect |
| --- | --- | --- |
| 8 Nov 2026 | 01:05 | UI lag when leaving System Settings |
| 8 Nov 2026 | 02:15 | Library focus jumps |
| 8 Nov 2026 | 11:37 | Local/put.io file-deletion confirmations and Android permission prompt |
| 9 Nov 2026 | 21:30–21:33 | Leaving custom Documentaries page; lost top-navigation focus |
| 10 Nov 2026 | Around 00:53 | Repeated launcher returns, app switching, incorrect page restoration and missing/mismatched focus |
| 10 Nov 2026 | 00:59–01:03 | Shield standby/wake; Movies retained, *The Bodyguard* focus lost; Down focuses Home |
| 10 Nov 2026 | Around 01:07 | Noticeable freeze; subsequent force-close and fresh launch; Continue Watching observation |
| 10 Nov 2026 | 01:09–01:12 | put.io pause, unexpected return to Details, second resume with visual glitch but synchronised audio |
| 10 Nov 2026 | Time not supplied, after the 01:09–01:12 tests | Rapid Home hero navigation: right preview thinner during sustained Right, left preview thinner during sustained Left; no major slowdown. Correlate only if log timing can be established |

- All times are user-reported local time (GMT/UTC on these November dates), approximate unless stated; verify diagnostic log timezone and clock alignment. Events may not all be retained in available logs. Correlate only after user supplies diagnostic export. This index is for investigation, not evidence that logs already prove a defect.

### Evidence, questions and next steps for Claude

- Request existing design specs, approved mock-ups, current repository state and relevant changelogs first; compare each proposed change against approved decisions and implemented behaviour.

- Collect diagnostics for 8 Nov 2026 01:05 (Settings exit lag), 02:15 (library focus jumps) and 11:37 (deletion prompts), if logs actually cover those periods. Times are user-reported local times; verify log time zone.

- Prioritise investigation of metadata misidentification, global focus reliability, destructive deletion flow, broken Settings focus and backup separators. Do not infer root causes from transcript alone.

- Prepare a traceable issue register with severity, affected screen, reproducibility, likely cause (only when evidenced), proposed solution, design dependency and approval status.

- Present open design decisions for user approval: Settings grouping, picker/tick design, Back behaviour, Fix Match workflow, media-transfer scope and any licence-page simplification.

- STRICT APPROVAL GATE: ALL user contributions to this evolving brief are discussion-only. Claude must investigate, analyse, present findings/options and discuss with the user before ANY implementation, design alteration, GitHub documentation edit or repository/branch change. Do not interpret OBSERVED, PREFERENCE, or PROPOSAL labels as implementation approval.

### Transcript provenance

Transcript 1 — Settings page walkthrough (all eight legacy tiles, About, backup, diagnostics).

Transcript 2 — Home customisation, library context menus, artwork, metadata mismatch and focus.

Transcript 3 — Local and put.io deletion confirmations.

Transcript 4 — Four Details-page content contexts.

Transcript 5 — Cloud/network-to-local media transfers.

Transcript 6 — Local file-browser Open redundancy and disappearing focus.

Editorial note: speech-to-text errors were normalised where meaning was clear (e.g. “council” → Cancel, “tract” → Trakt, “WebDev” → WebDAV). Technical terms whose intended wording is uncertain are marked for verification. This document is a structured synthesis, not a verbatim transcript.
