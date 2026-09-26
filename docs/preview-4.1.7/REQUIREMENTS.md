# 26 September requirement and verification register

Baseline: `094d8e80938501222b4b16ed715c03937abde68f`.

This verbatim requirement register preserves every substantive paragraph from the six implementation/acceptance/authority documents. Paragraphs may contain multiple acceptance conditions; all must be verified before their status changes. Pending is a planning state, not a completion claim. Final outcomes will use IMPLEMENTED, TESTED, BLOCKED, DEFERRED-BY-SPEC and AWAITING PHYSICAL QA.

### UI-001 — 1. Global visual language and top navigation

Source: `IMPLEMENTATION_SPEC.md`

Canonical top navigation: SUPERNOVA far left; Home · Movies · TV Shows; flexible spacer; Network & Files; Search icon; Settings icon; Clock. No Streaming/Library top-nav items, no Settings/clock separator, no line under nav. All textual nav uses 19sp normal/light styling. Unfocused controls have no box. Focus is a compact rounded Supernova-blue outline/boundary with restrained outward glow; contents stay white; no cyan focus text. Tighten the current oversized focus container without shrinking typography.

Status: **PENDING IMPLEMENTATION REVIEW**

Code mapping: pending detailed tracing. Verification: not yet run for this pass.

### UI-002 — 1. Global visual language and top navigation

Source: `IMPLEMENTATION_SPEC.md`

Adjacent persistent navigation controls should use a travelling focus boundary animation: animate X/width roughly 140–180ms and retarget smoothly during rapid D-pad movement. Cards do not use this mechanism; cards enlarge individually.

Status: **PENDING IMPLEMENTATION REVIEW**

Code mapping: pending detailed tracing. Verification: not yet run for this pass.

### UI-003 — 1. Global visual language and top navigation

Source: `IMPLEMENTATION_SPEC.md`

GLOBAL FOCUS DEFECT: when Home in top nav is focused, LEFT must be consumed and focus must remain Home. Implement centrally. Right-edge Settings + RIGHT remains Settings. Never allow edge presses to teleport into page content.

Status: **IMPLEMENTED**

Code mapping / verification: TopNavigation central edge consumption. Navigation CI tests passed through run 36241111976; physical D-pad QA remains pending.

### UI-004 — 1. Global visual language and top navigation

Source: `IMPLEMENTATION_SPEC.md`

Top nav is not a solid bar. On vertically scrolling pages, content may continue behind it but a progressive blur + darkening gradient is strongest immediately behind the nav and fades seamlessly below. No visible rectangle/separator/permanent bar.

Status: **PENDING IMPLEMENTATION REVIEW**

Code mapping: pending detailed tracing. Verification: not yet run for this pass.

### UI-005 — 2. Home

Source: `IMPLEMENTATION_SPEC.md`

Preserve the successful current Home structure and card enlargement feel. Recently Played/Continue Watching remains prominent. Recently Added must be bounded; first import must not create an effectively endless row. A title falling outside a displayed Continue Watching cap retains its playback state and in-progress indication elsewhere.

Status: **AWAITING PHYSICAL QA**

Code mapping / verification: PreviewPages caps Continue Watching at 30 and Recently Added at 50 without changing stored playback/library records. Source compiled in run 36238345377; physical behaviour not verified.

### UI-006 — 2. Home

Source: `IMPLEMENTATION_SPEC.md`

Featured: normalize visible logo bounds while ignoring transparent artwork padding; preserve aspect ratio. Synopsis width should relate to visible logo width (target around 90%, clamped approximately 25–32vw). Showcase artwork stays on the right, below the Network & Files→clock region, right of synopsis and above Continue Watching. Reposition/crop first; use real source art; darken/blur text-safe areas if needed; never fabricate people. More Info LEFT/RIGHT cycles Featured while focus remains on More Info. Featured indicators use persistent pill/dots with smooth ~180–220ms ease-out morphing.

Status: **PENDING IMPLEMENTATION REVIEW**

Code mapping: pending detailed tracing. Verification: not yet run for this pass.

### UI-007 — 2. Home

Source: `IMPLEMENTATION_SPEC.md`

Home/movie/TV cards: preserve the physically liked enlargement amount. Artwork + rounded boundary + outward glow scale as ONE aligned component. Artwork may never protrude outside the boundary; glow may not clip into square corners.

Status: **PENDING IMPLEMENTATION REVIEW**

Code mapping: pending detailed tracing. Verification: not yet run for this pass.

### UI-008 — 2. Home

Source: `IMPLEMENTATION_SPEC.md`

Customise Home remains a hub with inline row controls. Leftmost Home-row LEFT reveals Move/Hide only; no delete there. Fix clipped delete confirmation focus/button and awkward wrapping. Movies/TV row toggles update locally without whole-page flash/rebuild. Genre selector uses fixed header/footer with middle scrolling viewport and no clipping. Maximum Items supports No Limit, 10,20,30,40,50,75,100 and exact numeric Select; internal 0 may represent No Limit. Full QWERTY keyboard must be used for numeric Select where the existing wrong keyboard appears. Preserve good keyboard behaviour; subtle backdrop/shadow is acceptable.

Status: **PENDING IMPLEMENTATION REVIEW**

Code mapping: pending detailed tracing. Verification: not yet run for this pass.

### UI-009 — 2. Home

Source: `IMPLEMENTATION_SPEC.md`

Add missing language/locale iconography in Settings UI Language, Subtitle Reading Language and playback Select Subtitle Track. Locale flag only for a true locale such as en-GB; generic language uses neutral language iconography.

Status: **PENDING IMPLEMENTATION REVIEW**

Code mapping: pending detailed tracing. Verification: not yet run for this pass.

### UI-010 — 3. Background metadata enrichment

Source: `IMPLEMENTATION_SPEC.md`

Library membership is based on indexed media, not successful metadata. Launch local/Home immediately; enrichment runs persistently in background. Priority: visible/current Home content, current page/title, likely next items, then remaining library. TV expansion understands Episode→Season→Series and can enrich the complete local series; Details Seasons & Episodes can discover non-local/provider episodes too. Fetch/persist complete title packages where applicable: core metadata, logos/artwork, ratings, cast/crew, season/episode data, provider availability, Extras, recommendations, reception and local technical codecs. Deduplicate requests; cached data displays immediately; foreground requests temporarily override queue then background resumes. Persist completeness/staleness.

Status: **PENDING IMPLEMENTATION REVIEW**

Code mapping / verification: PreviewMetadataCache / PreviewEnrichmentQueue and StreamingRepository metadata gateway. Persistent priority/dedup/retry implemented; cache tests passed through run 36240119933. Season-package queue, classification requests, schema-preserving migration and independent network/disk locks added at checkpoint 7; new tests await CI. Complete series presentation and remaining enrichment integration are not complete.

### UI-011 — 3. Background metadata enrichment

Source: `IMPLEMENTATION_SPEC.md`

Provider/filter state must not depend on opening Details. The observed case where a provider appeared only after visiting Details is a defect.

Status: **PENDING IMPLEMENTATION REVIEW**

Code mapping: pending detailed tracing. Verification: not yet run for this pass.

### UI-012 — 4. Movies and TV Shows

Source: `IMPLEMENTATION_SPEC.md`

Preserve the liked six-wide grid and general layout. Fix poster focus geometry globally as described above.

Status: **PENDING IMPLEMENTATION REVIEW**

Code mapping: pending detailed tracing. Verification: not yet run for this pass.

### UI-013 — 4. Movies and TV Shows

Source: `IMPLEMENTATION_SPEC.md`

Toolbar: Filters, Sort, Order, Unmatched, List/Grid and Columns where applicable move close to the divider, visually echoing Details lower navigation. White icon/text, no cyan/glow text and no large rounded focus box. Focus is represented by the associated divider segment turning Supernova blue with restrained outward glow. Unlike Details tabs there is no persistent selected blue segment after focus leaves.

Status: **PENDING IMPLEMENTATION REVIEW**

Code mapping: pending detailed tracing. Verification: not yet run for this pass.

### UI-014 — 4. Movies and TV Shows

Source: `IMPLEMENTATION_SPEC.md`

Toolbar edges are deterministic: LEFT on first stays; RIGHT on final stays; DOWN enters library/header. Columns + RIGHT must not fall to a header.

Status: **PENDING IMPLEMENTATION REVIEW**

Code mapping: pending detailed tracing. Verification: not yet run for this pass.

### UI-015 — 4. Movies and TV Shows

Source: `IMPLEMENTATION_SPEC.md`

Grid edges: RIGHT on terminal item stays on terminal item; no wrap/jump. Top-row UP→toolbar. Bottom edge stays. Horizontal row lock remains until explicit vertical movement.

Status: **PENDING IMPLEMENTATION REVIEW**

Code mapping: pending detailed tracing. Verification: not yet run for this pass.

### UI-016 — 4. Movies and TV Shows

Source: `IMPLEMENTATION_SPEC.md`

Return from Details: restore exact originating item, scroll position, view mode and visible focus. In List restore exact row. Do not fall back to leftmost item/top nav.

Status: **PENDING IMPLEMENTATION REVIEW**

Code mapping: pending detailed tracing. Verification: not yet run for this pass.

### UI-017 — 4. Movies and TV Shows

Source: `IMPLEMENTATION_SPEC.md`

List View: fix flicker/rebuild when switching Grid/List, toggling/reordering Columns and background metadata updates. Sort remains one criterion. Columns controls visibility/order. Populate/persist Codec, Bitrate, HDR and other technical values through scan/index/background work; List reads cache rather than triggering focus-driven extraction.

Status: **PENDING IMPLEMENTATION REVIEW**

Code mapping: pending detailed tracing. Verification: not yet run for this pass.

### UI-018 — 4. Movies and TV Shows

Source: `IMPLEMENTATION_SPEC.md`

Filters: Genre only genres actually present; fix phantom genre entries. Genre multi-select + Done. Year becomes multi-select + Done. Streaming Service becomes persistent multi-select + Done with ticks retained on reopen and monochrome provider icons. Provider availability is populated in background. Clear Filters works.

Status: **PENDING IMPLEMENTATION REVIEW**

Code mapping: pending detailed tracing. Verification: not yet run for this pass.

### UI-019 — 4. Movies and TV Shows

Source: `IMPLEMENTATION_SPEC.md`

Movies artwork regression: after Network & Files → Scan Library, some Movies posters disappeared while titles/year and Details artwork remained; cold restart did not restore. Investigate/fix cache/binding invalidation. Latest diagnostics show repeated artwork failures and are evidence, but do not assume causality without code proof. TV did not show the same physical symptom at that time.

Status: **PENDING IMPLEMENTATION REVIEW**

Code mapping: pending detailed tracing. Verification: not yet run for this pass.

### UI-020 — 5. Unmatched media

Source: `IMPLEMENTATION_SPEC.md`

Add first-class Unmatched toolbar workflow to Movies and TV. Classification is separate from metadata identity. Strong TV filename/folder patterns may classify TV; standalone title/year may classify Movie; genuinely uncertain remains Unknown Type. Movies→Unmatched shows confidently Movie unmatched + Unknown; TV→Unmatched shows confidently TV unmatched + the SAME underlying Unknown records. No duplication. Once matched, item disappears from unmatched and enters normal library.

Status: **PENDING IMPLEMENTATION REVIEW**

Code mapping: pending detailed tracing. Verification: not yet run for this pass.

### UI-021 — 5. Unmatched media

Source: `IMPLEMENTATION_SPEC.md`

Unmatched Details uses honest placeholders such as Not matched/Unavailable/Unknown and a prominent Match Metadata action. Inferred TV structure may group provisionally. Matching and More→Edit/Correct use the same matching engine.

Status: **PENDING IMPLEMENTATION REVIEW**

Code mapping: pending detailed tracing. Verification: not yet run for this pass.

### UI-022 — 6. Details Page

Source: `IMPLEMENTATION_SPEC.md`

One continuous vertically scrolling Details page. Initial cinematic Hero; lower nav near bottom with teaser content below. DOWN from actions→lower nav→content/collapse. As user scrolls, title/logo becomes compact/sticky below global nav; hero metadata/actions scroll away. UP reverses smoothly.

Status: **PENDING IMPLEMENTATION REVIEW**

Code mapping: pending detailed tracing. Verification: not yet run for this pass.

### UI-023 — 6. Details Page

Source: `IMPLEMENTATION_SPEC.md`

Movie lower tabs: Details → Extras → More Like This. TV: Seasons & Episodes → Details → Extras → More Like This. Missing tabs disappear/reflow.

Status: **PENDING IMPLEMENTATION REVIEW**

Code mapping: pending detailed tracing. Verification: not yet run for this pass.

### UI-024 — 6. Details Page

Source: `IMPLEMENTATION_SPEC.md`

STRICT divider: selected segment is the EXISTING divider line recoloured blue, exactly same stroke width as white unselected divider; glow is optical outside only. No second underline/thicker bar. Tab text remains white.

Status: **PENDING IMPLEMENTATION REVIEW**

Code mapping: pending detailed tracing. Verification: not yet run for this pass.

### UI-025 — 6. Details Page

Source: `IMPLEMENTATION_SPEC.md`

Hero actions: Play + LEFT stays; Play + RIGHT→More; More + LEFT→Play; More + RIGHT stays; DOWN lower nav. Dynamic Play: unwatched Play, partial movie Resume, TV Resume Sx Ex. Subtle internal progress is allowed behind readable contents; external focus remains separate. Buttons content-sized with constant gap. Streaming-only may have provider primary action and no More when no useful actions.

Status: **PENDING IMPLEMENTATION REVIEW**

Code mapping: pending detailed tracing. Verification: not yet run for this pass.

### UI-026 — 6. Details Page

Source: `IMPLEMENTATION_SPEC.md`

TV Seasons & Episodes: stacked season rows, no dropdown. Four episode cards across. Focus is whole episode unit (artwork + title/runtime), same physical card enlargement ratio, one boundary/glow, no second artwork border. Local uses plain white HUD Play glyph on focus. Streaming-only uses monochrome/translucent provider mark and Watch/deep link. Unavailable remains visible but non-playable. Complete series discovery reconciles local first, then enabled streaming, then unavailable/unknown; no duplicate episode.

Status: **PENDING IMPLEMENTATION REVIEW**

Code mapping / verification: PreviewEpisodeRow / PreviewMoviePage. Local stacked season rows exist. Complete remote discovery/reconciliation and availability presentation remain in progress.

### UI-027 — 6. Details Page

Source: `IMPLEMENTATION_SPEC.md`

More Like This: collapsed compact hero, no repeated heading, landscape thumbnails four across, up to three rows / max 12 but fewer when relevance is weak. Unfocused artwork+title+year. Focus whole unit with same enlargement/boundary/glow. Source mark only on focus: local plain white HUD Play; streaming monochrome/translucent preferred provider. Strong TMDB relevance first, then local reconciliation, then enabled streaming; genre fallback only. Hide tab if none.

Status: **PENDING IMPLEMENTATION REVIEW**

Code mapping / verification: PreviewDetailsData / PreviewMoviePage now order TMDb recommendations before local genre fallback, reconcile local availability, cap 12, hide empty tab and lock row edges. Compact sticky hero and physical visual review remain pending.

### UI-028 — 6. Details Page

Source: `IMPLEMENTATION_SPEC.md`

Extras: collapsed hero; category rows only when content exists; no counts; four cards across; title+duration; whole-unit focus/enlarge; HUD Play glyph; LEFT/RIGHT within row, UP/DOWN categories; strict divider.

Status: **PENDING IMPLEMENTATION REVIEW**

Code mapping: pending detailed tracing. Verification: not yet run for this pass.

### UI-029 — 6. Details Page

Source: `IMPLEMENTATION_SPEC.md`

Details information: Key Information always left. Middle Reception if available. Right = Technical for local movie/episode, Library for local TV overview, Streaming for streaming-only. Empty panels omit/reflow. Key Info includes the agreed movie/TV fields (year/dates/runtime/age/genres/studio/network/distributor/country/budget/box office/collection/filming locations/original title/tagline as applicable). Reception: awards summary, critic/audience monochrome ratings/counts and optional reliable quote. Technical: resolution/codec/HDR/fps, audio format/channels/rate, container/size, subtitles, source, human-friendly path. TV Library: season/episode availability, specials, library size/average, technical counts, storage locations. Streaming: enabled/available providers, region and reliable quality/HDR/audio/subtitles; expiry only if reliable. No "last checked" clutter.

Status: **PENDING IMPLEMENTATION REVIEW**

Code mapping: pending detailed tracing. Verification: not yet run for this pass.

### UI-030 — 6. Details Page

Source: `IMPLEMENTATION_SPEC.md`

Cast/Crew: separate stacked rows, square/rounded portraits (not circles), portrait+name+role one focus unit, whole boundary/glow, complete cards in viewport, subtle edge fade, principal crew. Person/cast discovery is future work; do not implement person pages now.

Status: **PENDING IMPLEMENTATION REVIEW**

Code mapping: pending detailed tracing. Verification: not yet run for this pass.

### UI-031 — 6. Details Page

Source: `IMPLEMENTATION_SPEC.md`

Streaming-only uses the same Details component/quality as local. Advanced provider deep links are IN scope: where provider/platform supports it, open the specific movie/series/episode rather than generic provider hub; implement safe fallback/logging where unsupported.

Status: **PENDING IMPLEMENTATION REVIEW**

Code mapping: pending detailed tracing. Verification: not yet run for this pass.

### UI-032 — 7. More / contextual workflows

Source: `IMPLEMENTATION_SPEC.md`

Keep/refine More as a dark/translucent rounded context stack with monochrome icons, full-row blue focus boundary/glow and white contents. Content-driven; no empty headings/groups. Remove duplicated Resume/ordinary Play/Play Local File, Full Synopsis, Add to List, Streaming Services shortcut, File Information, List Episodes and Remove Info. Metadata correction must not secretly run legacy Remove Info first.

Status: **PENDING IMPLEMENTATION REVIEW**

Code mapping: PreviewMoreActions now filters native Movie/TV IDs independent of translated labels; two tests await CI. PreviewMoviePage hides empty remote More controls and omits empty groups, retaining subtitle/artwork access without a delete action. Duplicate File Information child removed. The metadata correction engine still needs a separate preservation audit; this requirement is not fully signed off.

### UI-033 — 7. More / contextual workflows

Source: `IMPLEMENTATION_SPEC.md`

Navigation stack: Details Hero→More→child→Back restores More child opener focus→Back restores Hero More focus.

Status: **IMPLEMENTED — shared dialog infrastructure; remaining native-child integration pending** / **AWAITING PHYSICAL QA**

Code mapping: PreviewDialog.create tracks weak dialog windows and restores the actual parent-window opener, with semantic replacement/fallback diagnostics. Choose/read/review, Versions and shared keyboard use it. Three stack tests passed in run 36249337871. Retained native dialogs and every More child route still require integration review.

### UI-034 — 7. More / contextual workflows

Source: `IMPLEMENTATION_SPEC.md`

Watched: exactly one dynamic action. Movie/episode immediate. TV series opens scope submenu Entire Series + seasons with state/progress.

Status: **IMPLEMENTED — pending automated integration validation** / **AWAITING PHYSICAL QA**

Code mapping: PreviewMoviePage resolves the current watched action and refreshes open-menu labels. PreviewWatchedScopeDialog/TvshowFragment offer Entire Series and per-season counts, using existing DbUtils writes and Trakt integration. Shared distinct episode counts prevent duplicate versions inflating progress. Five regression tests passed in run 36249337871; real-device refresh and Trakt effects remain unverified.

### UI-035 — 7. More / contextual workflows

Source: `IMPLEMENTATION_SPEC.md`

Add to Row: permanent rows with plain white + / ✓ state, toggle immediately while menu stays open, no toast; Watch Next follows same pattern; Create New Row bottom uses shared keyboard and auto-adds title then returns ticked.

Status: **PENDING IMPLEMENTATION REVIEW**

Code mapping: pending detailed tracing. Verification: not yet run for this pass.

### UI-036 — 7. More / contextual workflows

Source: `IMPLEMENTATION_SPEC.md`

Versions: only when 2+ physical versions. Compact horizontal rows with resolution/HDR/codec/audio/channels/size/source/location. Plain monochrome ✓ Current independent of focus. Selection updates current while menu stays; no toast; Technical updates; resume is title-level.

Status: **IMPLEMENTED — picker presentation; title-history verification incomplete** / **AWAITING PHYSICAL QA**

Code mapping: PreviewVersionsDialog and PreviewVariants.details use cached facts, independent Current state, a persistent window and credential-free location display. VideoDetailsFragment retains the native Details update and carries in-memory resume on switching. Two picker tests await full CI; reload/persistence and different-duration playback still need verification.

### UI-037 — 7. More / contextual workflows

Source: `IMPLEMENTATION_SPEC.md`

Subtitles: Choose local/downloaded with clear source/active state; Download search workflow; Subtitle Settings shortcut opens full Settings→Subtitles; no toast. Sync with HUD selection.

Status: **PENDING IMPLEMENTATION REVIEW**

Code mapping: pending detailed tracing. Verification: not yet run for this pass.

### UI-038 — 7. More / contextual workflows

Source: `IMPLEMENTATION_SPEC.md`

Artwork: Poster/Backdrop grids; one plain white ✓ at top-right safe area, no circle/current label; focus enlargement/boundary/glow; apply immediately, remain open, propagate to surfaces.

Status: **IMPLEMENTED — shared grid and movie/episode route; integration incomplete** / **AWAITING PHYSICAL QA**

Code mapping: PreviewArtworkPicker provides independent selection, single-flight save, failure retention and deterministic grid edges. VideoDetailsFragment reuses native savers with completion callbacks and refreshes the current backdrop. TvshowFragment now routes through the same grid with worker-based native scraper saves and existing TV refresh. Two new grid tests await CI; cross-surface cache propagation and physical behaviour remain unverified.

### UI-039 — 7. More / contextual workflows

Source: `IMPLEMENTATION_SPEC.md`

Metadata: unified Find a Match with subtitle "Search by title, TMDB ID or IMDb ID" and one field accepting title, TMDB numeric ID or IMDb tt... identifier. Already matched may show Current Match IDs + Refresh Metadata; unmatched omits Current Match. Compact results + Match Preview + explicit Use This Match/Use This Episode. TV series correction changes series identity then rebuilds/reconciles episodes; episode correction under known parent with Change Series Match escape hatch. Preserve playback/watched/rows/file associations/versions. TMDB/IMDb UI identifiers are clean monochrome.

Status: **PENDING IMPLEMENTATION REVIEW**

Code mapping: pending detailed tracing. Verification: not yet run for this pass.

### UI-040 — 7. More / contextual workflows

Source: `IMPLEMENTATION_SPEC.md`

Remove from Library is not a broad redesign in this pass. Existing source-context action may remain. Existing Delete remains where applicable; physical WebDAV Delete passed.

Status: **PENDING IMPLEMENTATION REVIEW**

Code mapping: pending detailed tracing. Verification: not yet run for this pass.

### UI-041 — 8. Playback HUD

Source: `IMPLEMENTATION_SPEC.md`

Do not ground-up redesign the physically approved base HUD. Upper left title/logo + TV season/episode/name; upper right current time + Ends. HUD sits low; seekbar close to controls. Seekbar has no blue container: blue played, neutral remaining, scrub dot, floating timestamp, current left/duration right. Exactly five controls: Subtitles | Audio | Play/Pause | Info | More. Unfocused no boxes; focused compact rounded blue boundary/glow; contents white; one-line labels.

Status: **PENDING IMPLEMENTATION REVIEW**

Code mapping: pending detailed tracing. Verification: not yet run for this pass.

### UI-042 — 8. Playback HUD

Source: `IMPLEMENTATION_SPEC.md`

Seeking acceleration: presses 1–3 = 10s, 4–6 = 30s, 7–9 = 1m, 10+ = 2m maximum. Reset after roughly 1–1.5s pause; direction change resets to 10s; hold accelerates; release resumes displayed position. If HUD hidden, LEFT/RIGHT reveals HUD + floating timestamp. Trick-play thumbnail is deferred.

Status: **PENDING IMPLEMENTATION REVIEW**

Code mapping: pending detailed tracing. Verification: not yet run for this pass.

### UI-043 — 8. Playback HUD

Source: `IMPLEMENTATION_SPEC.md`

Subtitle/Audio menus restore exact HUD opener focus on Back. Track changes remain open and tick updates. Add language-specific/generic icons. Replace legacy Get Subtitles Online with designed search/download workflow.

Status: **PENDING IMPLEMENTATION REVIEW**

Code mapping: pending detailed tracing. Verification: not yet run for this pass.

### UI-044 — 8. Playback HUD

Source: `IMPLEMENTATION_SPEC.md`

Playback Speed/Audio Delay use same compact adjustment panel dimensions; Subtitle Delay may be taller. Human-readable values: 1.00×, 0 ms, +250 ms, −500 ms, +1.5 s. Back exact opener.

Status: **PENDING IMPLEMENTATION REVIEW**

Code mapping: pending detailed tracing. Verification: not yet run for this pass.

### UI-045 — 8. Playback HUD

Source: `IMPLEMENTATION_SPEC.md`

Preparing Playback: fix Movies generic background and TV generic flash. Use cached real backdrop + gradient + logo/title/minimal info + Preparing playback…; use dark neutral transition while cache resolves; generic only no-art/failure. Do not fetch internet artwork synchronously at Play time.

Status: **PENDING IMPLEMENTATION REVIEW**

Code mapping: pending detailed tracing. Verification: not yet run for this pass.

### UI-046 — 8. Playback HUD

Source: `IMPLEMENTATION_SPEC.md`

Info overlay is technical only: Video, Audio, File, Source. No poster/title/synopsis/path/filename/actions. Remove Resume/Play from Beginning/File & Technical Details from Info. Back restores HUD Info focus.

Status: **AWAITING PHYSICAL QA**

Code mapping / verification: PlayerActivity opens PreviewTechnicalInfo directly; Video/Audio/File/Source only, existing dismiss restores HUD focus. PreviewTechnicalInfoTest passed in run 36238345377. Physical HUD focus restoration remains unverified.

### UI-047 — 9. Search

Source: `IMPLEMENTATION_SPEC.md`

Remove duplicate upper-right Search and subtitle. Keep left heading Search. Field placeholder "Search Movies and TV Shows"; field is non-focusable query display, subtle neutral/translucent boundary, no permanent blue, no X. Search opens keyboard focus immediately on T.

Status: **PENDING IMPLEMENTATION REVIEW**

Code mapping: pending detailed tracing. Verification: not yet run for this pass.

### UI-048 — 9. Search

Source: `IMPLEMENTATION_SPEC.md`

Keyboard rows exactly: 1 2 3 4 5 6 7 8 9 0 / Q W E R T Y U I O P / inset A S D F G H J K L / further inset Z X C V B N M / bottom Clear | Space | Backspace. No Caps/Shift/123. Traditional stagger, subtle dark/translucent backdrop, unfocused subtle key surfaces, focused compact blue outline/glow and white character.

Status: **PENDING IMPLEMENTATION REVIEW**

Code mapping: pending detailed tracing. Verification: not yet run for this pass.

### UI-049 — 9. Search

Source: `IMPLEMENTATION_SPEC.md`

Keyboard primary. Appropriate right-edge key RIGHT→first result when results exist; result LEFT→last keyboard key; UP from number row→top nav; no result edge stays keyboard. Results update only when query changes, not focus. Details return restores query/results/scroll/exact result focus. Preserve current successful live search and series/version routing.

Status: **PENDING IMPLEMENTATION REVIEW**

Code mapping: pending detailed tracing. Verification: not yet run for this pass.

### UI-050 — 10. Network & Files

Source: `IMPLEMENTATION_SPEC.md`

Final left rail: Overview · Local Storage · Network Shares · Cloud Services · Saved Locations. Remove Advanced from Network & Files; advanced app config belongs Settings. Selecting Network & Files enters page with Overview focused. UP/DOWN one rail item; UP Overview→global Network & Files; DOWN final stays; RIGHT→first meaningful middle control; LEFT from centre→originating category. Entering page never auto-activates/expands/enters middle.

Status: **PENDING IMPLEMENTATION REVIEW**

Code mapping: pending detailed tracing. Verification: not yet run for this pass.

### UI-051 — 10. Network & Files

Source: `IMPLEMENTATION_SPEC.md`

Architecture: Left = Where am I? Middle = What can I select? Right = What is it / what can I do with it? Small contextual overlay for finite values. Full-screen transition only for a genuine workspace such as filesystem browser.

Status: **PENDING IMPLEMENTATION REVIEW**

Code mapping: pending detailed tracing. Verification: not yet run for this pass.

### UI-052 — 10. Network & Files

Source: `IMPLEMENTATION_SPEC.md`

Overview keeps Scan Library and Network Scanning separate. Network Scanning right panel: Automatic scanning On/Off; Frequency; Scan when Supernova opens/returns On/Off; Sources Included; Last Scan/Result; Scan Network Sources Now. Remove Configure Network Scanning button. Frequency choices exactly 15m,30m,1h,6h,24h. Use small anchored overlay with tick for finite choices; Sources Included may use larger multi-select. Increase vertical spacing between controls.

Status: **PENDING IMPLEMENTATION REVIEW**

Code mapping: pending detailed tracing. Verification: not yet run for this pass.

### UI-053 — 10. Network & Files

Source: `IMPLEMENTATION_SPEC.md`

Scan Library live state: current phase/source, honest progress only if measurable, items processed/new/updated/elapsed; indeterminate otherwise. Network Scan also exposes live source/location, checked/new/updated, sources completed and elapsed, with unobtrusive persistence while navigating Network & Files. Cancel only if genuinely supported.

Status: **PENDING IMPLEMENTATION REVIEW**

Code mapping: pending detailed tracing. Verification: not yet run for this pass.

### UI-054 — 10. Network & Files

Source: `IMPLEMENTATION_SPEC.md`

PHYSICAL DEFECT: Manual Scan Network Sources Now failed to discover a newly added WebDAV movie/no useful feedback, while close/relaunch scanning found it. Compare manual pipeline with successful startup/resume path and make manual action use the same reliable discovery/reconciliation semantics with start/completion/failure feedback.

Status: **PENDING IMPLEMENTATION REVIEW**

Code mapping: pending detailed tracing. Verification: not yet run for this pass.

### UI-055 — 10. Network & Files

Source: `IMPLEMENTATION_SPEC.md`

Library Sources = indexed network folders/media. Actions Browse/Open, Scan Source, Remove from Library (not delete media). Saved Locations = bookmarks, not necessarily indexed; actions Browse/Open, Add to Library, Remove Saved Location. Shared browser action Add to Saved Locations populates it. Empty state explains saving frequent folders.

Status: **PENDING IMPLEMENTATION REVIEW**

Code mapping: pending detailed tracing. Verification: not yet run for this pass.

### UI-056 — 10. Network & Files

Source: `IMPLEMENTATION_SPEC.md`

Network Shares: flatten nested Library Sources→source→actions. Focusing source immediately updates right panel with friendly path/protocol and actions. No right-panel Back button; remote LEFT returns. Distinguish network connection from library source. Add Network Source establishes connection first, then browse/select folder and add to Movies/TV library. Protocols: SMB, WebDAV HTTPS, WebDAV HTTP, SFTP, FTP, FTP TLS. Do NOT add NFS unless implementation genuinely supports it. Credentials include protocol, server/address, optional port 1–65535, path, username/password, save/show password and SMB domain when needed. WebDAV(s) may allow anonymous empty user. Discovery: SMB computers/NAS and DLNA/UPnP media servers; FTP/SFTP direct, not discovery.

Status: **PENDING IMPLEMENTATION REVIEW**

Code mapping: pending detailed tracing. Verification: not yet run for this pass.

### UI-057 — 10. Network & Files

Source: `IMPLEMENTATION_SPEC.md`

Local Storage: middle storage volumes; right capacity/status/location + Browse. Shared browser is reused for Local Storage, Network Shares, Saved Locations and put.io. Three-panel model location/source→contents→context actions. Actions are capability-driven; never show destructive operations unsupported by/read-only source. Folder actions include Add Folder to Movies Library, Add Folder to TV Shows Library, Add to Saved Locations.

Status: **PENDING IMPLEMENTATION REVIEW**

Code mapping: pending detailed tracing. Verification: not yet run for this pass.

### UI-058 — 11. Settings foundation

Source: `IMPLEMENTATION_SPEC.md`

This is foundation/navigation only, NOT category-by-category settings redesign. Preserve existing settings and semantics. Three-panel shell: Left Categories → Middle Settings/options → Right contextual explanation/current value. Dark/translucent Supernova styling, white type, monochrome icons, blue focus boundary/glow.

Status: **PENDING IMPLEMENTATION REVIEW**

Code mapping: pending detailed tracing. Verification: not yet run for this pass.

### UI-059 — 11. Settings foundation

Source: `IMPLEMENTATION_SPEC.md`

Fixed non-scrolling left rail with all top-level categories visible and About permanently visible at bottom. Current categories: Playback, Video, Audio, Subtitles, Library & Metadata, Home, Appearance, Streaming, Network, Integrations, Advanced, About.

Status: **PENDING IMPLEMENTATION REVIEW**

Code mapping: pending detailed tracing. Verification: not yet run for this pass.

### UI-060 — 11. Settings foundation

Source: `IMPLEMENTATION_SPEC.md`

Enter Settings focuses Playback in LEFT rail, never first middle setting. Focusing category NEVER expands it. Indented children do not appear on hover/focus. Explicit OK/Select or RIGHT enters category/subcategory; nested subcategories appear in middle workspace, never inserted into permanent left rail. Deterministic focus: rail UP/DOWN one category; RIGHT first appropriate middle; LEFT returns origin; middle UP/DOWN; RIGHT enters value/control when meaningful; Back reverses hierarchy; choice panel Back exact setting. No teleporting.

Status: **PENDING IMPLEMENTATION REVIEW**

Code mapping: pending detailed tracing. Verification: not yet run for this pass.

### UI-061 — 12. Launcher branding / app name

Source: `IMPLEMENTATION_SPEC.md`

Change launcher-facing display name from "Nova Preview" to "Supernova". Keep Preview/build identity inside About/version information. Do NOT change package/application ID or signing identity.

Status: **PENDING IMPLEMENTATION REVIEW**

Code mapping: pending detailed tracing. Verification: not yet run for this pass.

### UI-062 — 12. Launcher branding / app name

Source: `IMPLEMENTATION_SPEC.md`

Use BRANDING/Supernova_Shield_Launcher_Master_APPROVED.png as approved artwork direction and BRANDING/Supernova_Shield_Launcher_320x180_APPROVED.png as the derived Android TV launcher banner target. Preserve high-resolution master. Ensure manifest/resource wiring uses the Android TV banner appropriately. Validate appearance on Nvidia Shield Favourite Apps row at real viewing distance.

Status: **PENDING IMPLEMENTATION REVIEW**

Code mapping: pending detailed tracing. Verification: not yet run for this pass.

### UI-063 — 12. Launcher branding / app name

Source: `IMPLEMENTATION_SPEC.md`

Investigate coexistence: user reports upstream Nova Video Player fails to install both from GitHub APK and Shield app store while Supernova remains installed/unaffected. Do not assume cause. Verify Supernova package identity, manifest authorities/providers and identifiers are isolated from upstream Nova. Acceptance target: Supernova and official/upstream Nova can coexist, launch and upgrade independently if upstream packaging/platform permits. Do not "fix" by changing Supernova's established package/signing identity without evidence.


Status: **PENDING IMPLEMENTATION REVIEW**

Code mapping: pending detailed tracing. Verification: not yet run for this pass.

### PUT-001 — Architecture

Source: `PUTIO_ARCHITECTURE.md`

Hybrid model. put.io supported public API/OAuth handles authentication, account/storage, browse, stable file/folder IDs, discovery/sync, search, file metadata/media info and supported file operations. Existing WebDAV remains the PRIMARY original-quality playback transport initially. Playback stack remains Supernova → AVOS/core → FFmpeg → Android/Nvidia Shield hardware. Do not describe current engine as mpv.

Status: **PENDING IMPLEMENTATION REVIEW**

Code mapping: pending detailed tracing. Verification: not yet run for this pass.

### PUT-002 — Architecture

Source: `PUTIO_ARCHITECTURE.md`

Provider abstraction: Library → Local / Generic Network / Cloud Provider. put.io provider owns API client, stable ID mapping, sync/account/file management. Playback resolver continues to support local/SMB/SFTP/WebDAV and put.io WebDAV original-quality transport. API direct original/HLS/MP4 may be benchmarked later as fallback/future, not primary now. Transfers/download management and playback-position sync are deferred.

Status: **PENDING IMPLEMENTATION REVIEW**

Code mapping: pending detailed tracing. Verification: not yet run for this pass.

### PUT-003 — Authentication

Source: `PUTIO_ARCHITECTURE.md`

Cloud Services → Connect put.io → temporary linking/device code → large TV QR + short code + put.io link → phone sign-in/approve → Supernova polls → connected. Never collect normal put.io username/password in Supernova. QR contains link/code only, never OAuth token/credentials. Manual code fallback. Store OAuth token securely; never export/log/display it. Validate exact production endpoint/deep link during implementation. Supernova requires a registered put.io OAuth app/client.

Status: **BLOCKED**

Code mapping / verification: Production registered Supernova put.io OAuth client configuration is absent. PutioTokenStore implements device-local encrypted persistence; four tests passed in the 213-test checkpoint. Credential lifecycle integration remains pending. No production credentials invented, borrowed or exposed; existing WebDAV access remains untouched.

### PUT-004 — First-time association / zero-duplicate migration

Source: `PUTIO_ARCHITECTURE.md`

1. Authenticate.
2. Choose Movies folder and TV Shows folder using API browser; store stable folder IDs + display path.
3. Save & Continue explicitly states existing library will be preserved and matching put.io files linked rather than imported again.
4. Initial API sync selected folders; high-confidence match existing records using path/name/size and other safe signals; attach put.io IDs to existing record.
5. API-only new items enter normal classification/index/enrichment.
6. Ambiguous matches remain untouched and go to Needs Review: Existing Supernova item vs put.io item; Same File / Keep Separate; Finish Later.
7. Success summary includes matched existing items and duplicates created (target zero).

Status: **PENDING IMPLEMENTATION REVIEW**

Code mapping / verification: PutioReconciliation implements conservative path/name/size matching and review decisions (13 policy tests passed). PutioAssociationStore adds atomic stable mappings, generation fencing and missing-review flags; six database tests await CI. Account linking, association UI, new-item indexing and scanner hand-off remain incomplete.

### PUT-005 — First-time association / zero-duplicate migration

Source: `PUTIO_ARCHITECTURE.md`

Preserve metadata, watched/resume, custom rows, artwork, versions and file associations. Provider fields include provider=put.io, put.io File ID, Parent Folder ID, Supernova Media ID, playback source existing WebDAV URI. Stable file ID survives rename/move.

Status: **PENDING IMPLEMENTATION REVIEW**

Code mapping: pending detailed tracing. Verification: not yet run for this pass.

### PUT-006 — First-time association / zero-duplicate migration

Source: `PUTIO_ARCHITECTURE.md`

Once associated, discovery/index changes for those folders come from put.io API; playback remains WebDAV. Generic network scanner must not independently rediscover the same associated folder.

Status: **PENDING IMPLEMENTATION REVIEW**

Code mapping: pending detailed tracing. Verification: not yet run for this pass.

### PUT-007 — Sync safety

Source: `PUTIO_ARCHITECTURE.md`

Initial complete snapshot; later additions/removals/moves/renames reconcile stable IDs. Correctly paginate large libraries. Partial/interrupted/auth-failed/rate-limited/incomplete listings MUST NEVER trigger mass deletions. Destructive reconciliation only after complete valid sync.

Status: **PENDING IMPLEMENTATION REVIEW**

Code mapping / verification: PutioReconciliation tracks every descendant/continuation page and rejects interrupted/auth/rate-limit/offline/invalid snapshots. Missing stable IDs are review-only. Tests added; API and persistent sync integration remain pending.

### PUT-008 — Sync safety

Source: `PUTIO_ARCHITECTURE.md`

API unavailable → indexed library remains and WebDAV playback may continue. WebDAV unavailable → API browse/sync may continue but playback source reports unavailable; no library deletion. OAuth invalid → Reconnect put.io; do not delete library. Changing selected library folder is source reassignment, not silent deletion; sync new scope and deliberately retire old association while preserving state. Disconnect removes native sync/API credential, never deletes put.io media or silently erases Supernova history. Because WebDAV is independent, explicitly ask whether associated sources revert to generic discovery or remain inactive; do not guess.

Status: **PENDING IMPLEMENTATION REVIEW**

Code mapping: pending detailed tracing. Verification: not yet run for this pass.

### PUT-009 — Sync safety

Source: `PUTIO_ARCHITECTURE.md`

Native destructive put.io file operations, if implemented, use supported API. Do not rely on WebDAV destructive behaviour merely because a physical WebDAV delete happened to pass.

Status: **PENDING IMPLEMENTATION REVIEW**

Code mapping: pending detailed tracing. Verification: not yet run for this pass.

### PUT-010 — Cloud Services visual behaviour

Source: `PUTIO_ARCHITECTURE.md`

Blue remains focus/accent everywhere; no green status theme. Connected/Up to date may use restrained blue/neutral without focus-level glow. Provider logos may retain recognizable brand shapes but are monochrome. put.io is implemented provider. Google Drive, OneDrive, Dropbox may remain monochrome Coming soon reminders only; do not imply implemented.

Status: **PENDING IMPLEMENTATION REVIEW**

Code mapping: pending detailed tracing. Verification: not yet run for this pass.

### PUT-011 — Cloud Services visual behaviour

Source: `PUTIO_ARCHITECTURE.md`

Connected put.io overview: identity/status, Storage Used, Last Sync/Up to date, Movies folder, TV Shows folder, Sync Now, Browse Files, Change Library Folders, Account/Connection, Disconnect. Avoid duplicate Disconnect actions. No transfers/download UI.

Status: **PENDING IMPLEMENTATION REVIEW**

Code mapping: pending detailed tracing. Verification: not yet run for this pass.

### PUT-012 — Required QA

Source: `PUTIO_ARCHITECTURE.md`

Large paginated library; interrupted sync; expired auth; rename; folder move; API outage; WebDAV outage; new file; removed file; ambiguous association; reconnect; no duplicate/lost-state migration. Physical path: Connect → associate Films/TV → sync → new put.io item appears → play over WebDAV → rename/move → sync → same record.

Status: **PENDING IMPLEMENTATION REVIEW**

Code mapping: pending detailed tracing. Verification: not yet run for this pass.

### DIA-001 — Preview 4.1.6 Diagnostic Findings and Next-Version Logging Requirements

Source: `DIAGNOSTICS_4.1.6_FINDINGS.md`

Evidence: QA_EVIDENCE/DIAGNOSTICS/Supernova-Diagnostics-1790390625048.zip. Treat all manual Report-a-Problem markers in this particular export as test/noise: the user explicitly did not intentionally use Report for defects in this report. Automatic diagnostics remain valid evidence.

Status: **PENDING IMPLEMENTATION REVIEW**

Code mapping: pending detailed tracing. Verification: not yet run for this pass.

### DIA-002 — Findings from latest retained report

Source: `DIAGNOSTICS_4.1.6_FINDINGS.md`

- Artwork: 306 artwork requests, 67 artwork_failed, 248 artwork_ready. Failure bursts occurred; observed failure latency approximately 676–845ms with median around 704ms. This is relevant to the physical Movies-poster disappearance after scan but does not by itself prove causality.
- Two PREVIOUS_SESSION_UNCLEAN_EXIT markers occurred around 01:38 UTC. Earlier lifecycle logs include paused→stopped→destroyed, so current clean-marker logic may produce false positives. Investigate; do not label these confirmed crashes.
- Historical earlier-process diagnostic drop counter reached 2,287 events; latest process/export showed zero. Important events need protected retention/backpressure.
- Two library_scan_requested events. One showed local_import toggling without captured network_active in the following window; another showed network_files growth and local/network overlap. Correlate with physical manual-scan defect.
- One metadata_network_failed InterruptedIOException among 49 successful metadata responses: low-priority correlation, not a confirmed defect.
- 65 completed indexed_library_load operation ends were parsed; median roughly 645ms, max roughly 3293ms. Investigate whether UI interactions trigger unnecessary full reloads contributing to List/Columns flicker or lost focus.
- Playback retained 19 newly-started sessions plus one carried-in session; 16/19 reached player_prepared; three exited before prepared. Retained finishes had error_code 0 and were mostly user_back/service_destroyed. No repeated playback-engine crash pattern was established. Native A/V offset/timing was not exposed.
- 916 focus transitions were retained, but generated view IDs are not semantically useful enough to diagnose focus routes.
- No evidence of the historical CursorWindow startup crash was present in this retained report.

Status: **PENDING IMPLEMENTATION REVIEW**

Code mapping: pending detailed tracing. Verification: not yet run for this pass.

### DIA-003 — Required diagnostic/reporting improvements

Source: `DIAGNOSTICS_4.1.6_FINDINGS.md`

Manual Report a Problem creates a short unique reference (example D78C2515-1009), category and exact timestamp. Confirmation shows readable reference and a QR containing ONLY non-sensitive reference/category/time/linking data. Never include credentials, tokens, private paths or raw diagnostic payload. Provide Show Latest Reference. Export manifest indexes manual markers.

Status: **PENDING IMPLEMENTATION REVIEW**

Code mapping: pending detailed tracing. Verification: not yet run for this pass.

### DIA-004 — Required diagnostic/reporting improvements

Source: `DIAGNOSTICS_4.1.6_FINDINGS.md`

Improve multi-day retention. Separate verbose rolling stream from protected important-event/incident stream. Routine focus/heartbeat/success events rotate first; ERROR/FATAL, crashes/exceptions, failed scans, playback failures, significant artwork failures and manual-report windows survive longer. Apply backpressure/prioritisation so routine events cannot cause thousands of important events to be dropped.

Status: **AWAITING PHYSICAL QA**

Code mapping / verification: Diagnostics IMPORTANT executor and daily protected stream separate routine pressure; seven-day age retention with daily size caps. Manual and automatic incident windows now have separate protected daily streams. Archive tests passed through run 36241111976. Long-running Shield pressure/retention validation remains pending.

### DIA-005 — Required diagnostic/reporting improvements

Source: `DIAGNOSTICS_4.1.6_FINDINGS.md`

Add operation correlation IDs across library scans, network scans, metadata enrichment, artwork, playback, provider sync and significant navigation flows. Record trigger/source (Manual, Startup, Resume, Scheduled, Provider Sync) and explicit lifecycle stages.

Status: **PENDING IMPLEMENTATION REVIEW**

Code mapping: pending detailed tracing. Verification: not yet run for this pass.

### DIA-006 — Required diagnostic/reporting improvements

Source: `DIAGNOSTICS_4.1.6_FINDINGS.md`

Artwork failure diagnostics: safe anonymous media ID, surface (Movies Grid/Home Featured/etc.), artwork type, source/cache layer attempted, failure category/reason, elapsed time, fallback attempted/succeeded. No sensitive path/token leakage.

Status: **PENDING IMPLEMENTATION REVIEW**

Code mapping: pending detailed tracing. Verification: not yet run for this pass.

### DIA-007 — Required diagnostic/reporting improvements

Source: `DIAGNOSTICS_4.1.6_FINDINGS.md`

Scan diagnostics: Requested→Queued→Started→source/phase→Index/Reconciliation→Metadata queued→Completed/Failed/Cancelled. Include source counts and honest progress. Make manual scan trace directly comparable to startup/resume scan.

Status: **PENDING IMPLEMENTATION REVIEW**

Code mapping: pending detailed tracing. Verification: not yet run for this pass.

### DIA-008 — Required diagnostic/reporting improvements

Source: `DIAGNOSTICS_4.1.6_FINDINGS.md`

Semantic focus diagnostics: stable IDs such as topnav.home/settings.category.playback, not generated LinearLayout IDs. Log input → previous focus → resulting focus → screen and whether edge input was consumed. Add focus-restoration token on entry to Details/menus/settings children and log requested/restored/fallback result on return.

Status: **PENDING IMPLEMENTATION REVIEW**

Code mapping: pending detailed tracing. Verification: not yet run for this pass.

### DIA-009 — Required diagnostic/reporting improvements

Source: `DIAGNOSTICS_4.1.6_FINDINGS.md`

UI rebuild/flicker telemetry: lightweight records when whole adapter/view-model/library dataset is recreated/rebound, with reason and item count. This must not itself cause performance problems.

Status: **PENDING IMPLEMENTATION REVIEW**

Code mapping: pending detailed tracing. Verification: not yet run for this pass.

### DIA-010 — Required diagnostic/reporting improvements

Source: `DIAGNOSTICS_4.1.6_FINDINGS.md`

On important failure, capture small structured UI-state snapshot (not screenshot): page, selected tab/category, focused semantic control, grid/list mode, active filters/sort, anonymous media ID, open modal/menu. Add severity INFO/WARNING/ERROR/FATAL.

Status: **PENDING IMPLEMENTATION REVIEW**

Code mapping: pending detailed tracing. Verification: not yet run for this pass.

### DIA-011 — Required diagnostic/reporting improvements

Source: `DIAGNOSTICS_4.1.6_FINDINGS.md`

Automatic incident capture: preserve roughly 30–60 seconds before/after significant failures (uncaught exception, ANR-like stall, failed library operation, playback failure, repeated artwork-failure burst). Add burst summarisation while retaining underlying events.

Status: **PENDING IMPLEMENTATION REVIEW**

Code mapping: pending detailed tracing. Verification: not yet run for this pass.

### DIA-012 — Required diagnostic/reporting improvements

Source: `DIAGNOSTICS_4.1.6_FINDINGS.md`

At major failures only, capture safe resource context: app heap/native memory, available memory/storage, thread count and foreground/background state. Avoid continuous heavy profiling.

Status: **PENDING IMPLEMENTATION REVIEW**

Code mapping: pending detailed tracing. Verification: not yet run for this pass.

### DIA-013 — Required diagnostic/reporting improvements

Source: `DIAGNOSTICS_4.1.6_FINDINGS.md`

Network diagnostics: provider/service, operation type, status/error category, duration, retry number and connectivity state; redact full sensitive URLs, authorization headers, passwords, tokens and sensitive query parameters. put.io OAuth secrets must never enter logs/export/QR.

Status: **PENDING IMPLEMENTATION REVIEW**

Code mapping: pending detailed tracing. Verification: not yet run for this pass.

### DIA-014 — Required diagnostic/reporting improvements

Source: `DIAGNOSTICS_4.1.6_FINDINGS.md`

Export should include a machine-readable and human-readable summary: session duration, launches, clean/suspected unclean exits, playback sessions, scans, artwork request/failure counts, dropped-event count, manual reports and automatic incidents, each linked by timestamp/correlation ID to raw evidence.

Status: **PENDING IMPLEMENTATION REVIEW**

Code mapping / verification: DiagnosticArchive indexes manual/incident events, event counts, retained process spans, launches/exit markers, per-process maximum drop counters and timestamp/sequence/operation evidence. Six archive tests passed in run 36241111976. Retained spans are explicitly not complete session lifetimes. Full summary field conformance remains under review.

### DIA-015 — Required diagnostic/reporting improvements

Source: `DIAGNOSTICS_4.1.6_FINDINGS.md`

Improve clean/unclean process detection so normal Android lifecycle destruction does not automatically become a false crash signal.


Status: **AWAITING PHYSICAL QA**

Code mapping / verification: Diagnostics marks an orderly transition to background clean, while retaining unclean-exit observations as suspected rather than proven crashes. Physical lifecycle/kill QA remains pending.

### QA-001 — Build / identity

Source: `QA_AND_ACCEPTANCE.md`

- Recover exact repo/branch/HEAD/status before edits; document final source commit.
- Compile/build succeeds; existing automated suite passes; add targeted tests for changed architecture.
- Signed candidate certificate SHA-256 exactly 89ac087ed6f989c90482d4a999f80511fe6ceee26ef1b9c37a142a9f00d39a5a.
- In-place upgrade from installed 4.1.6 preserves app data/settings/library.
- Launcher label is Supernova; About retains preview/version details.
- Shield Favourite Apps displays approved 16:9 banner legibly.
- Investigate official/upstream Nova install failure and package/authority coexistence without changing Supernova identity casually.

Status: **PENDING IMPLEMENTATION REVIEW**

Code mapping: pending detailed tracing. Verification: not yet run for this pass.

### QA-002 — Startup / global navigation

Source: `QA_AND_ACCEPTANCE.md`

- New UI/current chosen UI starts without immediate close; Home/Movies/TV open; no historical CursorWindow crash.
- Canonical top nav exactly as specified; global blur/darken scroll treatment has no rectangular bar edge.
- Home+LEFT remains Home. Settings+RIGHT remains Settings. No edge teleport.
- Travelling focus boundary is smooth and never leaves two simultaneous focus indications.

Status: **PENDING IMPLEMENTATION REVIEW**

Code mapping: pending detailed tracing. Verification: not yet run for this pass.

### QA-003 — Home

Source: `QA_AND_ACCEPTANCE.md`

- Featured cycling retains More Info focus; indicators animate; synopsis/logo/art geometry follows spec.
- Card enlargement/glow is aligned/no clipping.
- Recently Added is bounded; Continue Watching display cap never destroys playback state.
- Customise Home row controls, delete confirmation, genre selector, Maximum Items and keyboard pass; toggles do not flash/rebuild whole page.

Status: **PENDING IMPLEMENTATION REVIEW**

Code mapping: pending detailed tracing. Verification: not yet run for this pass.

### QA-004 — Movies / TV

Source: `QA_AND_ACCEPTANCE.md`

- Grid terminal RIGHT stays. Top/bottom boundaries deterministic.
- Toolbar divider focus styling correct; Unmatched present; toolbar edges locked.
- Details→Back restores exact item/scroll/view/focus in Grid and List.
- Filter multi-select persistence, genre correctness, provider population and Clear pass.
- Columns changes do not flicker/rebuild; technical columns populate from cache/index.
- Movies artwork survives library/network scan and restart; no poster bleed outside focus boundary.

Status: **PENDING IMPLEMENTATION REVIEW**

Code mapping: pending detailed tracing. Verification: not yet run for this pass.

### QA-005 — Details / More

Source: `QA_AND_ACCEPTANCE.md`

- Continuous Hero→lower-nav scroll/collapse/reverse works.
- Correct Movie/TV tab sets; missing tabs reflow; divider blue segment has identical thickness.
- TV complete series reconciliation no duplicates; local/streaming/unavailable states correct.
- Extras/More Like This card focus and source marks correct.
- Key/Reception/Technical|Library|Streaming panels adapt without empty boxes.
- Cast/Crew rows render/focus correctly; no person pages introduced.
- Streaming-only title parity and specific-title provider deep links/fallback pass.
- More child workflows restore exact focus and preserve playback/library state.

Status: **PENDING IMPLEMENTATION REVIEW**

Code mapping: pending detailed tracing. Verification: not yet run for this pass.

### QA-006 — Playback

Source: `QA_AND_ACCEPTANCE.md`

- Base HUD five controls/spacing remain; subtitle/audio Back restoration exact.
- Progressive seeking increments/reset/direction/hold pass.
- Preparing Playback uses cached real artwork when available; no generic flash; no blocking internet art fetch.
- Info overlay technical only and Back restores Info focus.

Status: **PENDING IMPLEMENTATION REVIEW**

Code mapping: pending detailed tracing. Verification: not yet run for this pass.

### QA-007 — Search

Source: `QA_AND_ACCEPTANCE.md`

- No duplicate heading/subtitle/X; keyboard opens on T; exact rows/buttons; deterministic keyboard↔results routing.
- Results only update on query change; Details return restores query/result/scroll/focus.

Status: **PENDING IMPLEMENTATION REVIEW**

Code mapping: pending detailed tracing. Verification: not yet run for this pass.

### QA-008 — Network & Files

Source: `QA_AND_ACCEPTANCE.md`

- Entry focuses Overview left rail; deterministic three-panel routing; no auto-expanding category.
- Overview scan controls/layout/frequency choices pass.
- Manual network scan finds same new content as startup/resume path and reports live start/progress/completion/failure.
- Library Sources vs Saved Locations semantics pass.
- Network protocol flows support only specified protocols; connection then folder selection; no Add/Browse bounce.
- Shared browser reused and capability-driven.

Status: **PENDING IMPLEMENTATION REVIEW**

Code mapping: pending detailed tracing. Verification: not yet run for this pass.

### QA-009 — put.io

Source: `QA_AND_ACCEPTANCE.md`

- QR/device auth contains no secret; token storage/log redaction verified.
- Existing WebDAV Films/TV association creates zero duplicates and preserves watched/resume/metadata/artwork/rows/versions.
- API discovery + WebDAV playback separation verified.
- Pagination, interruption, auth expiry, rate limit/incomplete sync cannot mass-delete.
- Rename/move remains same record; API outage/WebDAV outage/reconnect/folder change/disconnect safe.

Status: **PENDING IMPLEMENTATION REVIEW**

Code mapping: pending detailed tracing. Verification: not yet run for this pass.

### QA-010 — Settings

Source: `QA_AND_ACCEPTANCE.md`

- Entry focuses Playback left rail, not first setting.
- Left rail fixed/non-scrolling with About visible bottom.
- Hover/focus never expands children; explicit select/right enters middle workspace; Back reverses exact hierarchy.
- Existing setting semantics preserved.

Status: **PENDING IMPLEMENTATION REVIEW**

Code mapping: pending detailed tracing. Verification: not yet run for this pass.

### QA-011 — Diagnostics

Source: `QA_AND_ACCEPTANCE.md`

- Manual report ref/category/time + safe QR + Show Latest Reference + export manifest.
- Semantic focus IDs/input transitions and restoration tokens.
- Correlation IDs for scan/playback/metadata/artwork/provider operations.
- Important events survive pressure; routine events can rotate first; dropped counters visible.
- Artwork failure reasons/surface/cache/fallback captured safely.
- Automatic incidents preserve pre/post context and burst summary.
- Export summary generated; secrets/tokens/passwords/private URLs absent.
- Clean-exit detector does not label normal lifecycle destroy as crash.

Status: **PENDING IMPLEMENTATION REVIEW**

Code mapping: pending detailed tracing. Verification: not yet run for this pass.

### SCOPE-001 — Scope / Deferred Guardrail

Source: `SCOPE_AND_DEFERRED.md`

IN SCOPE: global focus/navigation fixes; Home polish/customisation corrections; background metadata enrichment; Movies/TV toolbar/filter/list/grid/focus/artwork corrections; Unmatched workflow; complete approved Details/Seasons/Extras/More Like This/Details panels; refined More child workflows; advanced provider deep links with fallback; playback HUD corrections/seek acceleration/technical Info; Search cleanup/shared keyboard; Network & Files redesign and reliable scanning; shared browser; Saved Locations; native put.io API/OAuth + safe existing-library association while retaining WebDAV playback; Settings foundation/navigation shell only; expanded diagnostics/reporting; Supernova launcher rename/Shield banner; upstream Nova coexistence investigation.

Status: **PENDING IMPLEMENTATION REVIEW**

Code mapping: pending detailed tracing. Verification: not yet run for this pass.

### SCOPE-002 — Scope / Deferred Guardrail

Source: `SCOPE_AND_DEFERRED.md`

NOT A BROAD REDESIGN: Remove from Library. Existing source-context action remains. Existing Delete remains where applicable.

Status: **PENDING IMPLEMENTATION REVIEW**

Code mapping: pending detailed tracing. Verification: not yet run for this pass.

### SCOPE-003 — Scope / Deferred Guardrail

Source: `SCOPE_AND_DEFERRED.md`

DEFERRED: person/cast discovery pages; playback trick-play thumbnails; put.io transfer/download management; put.io playback-position sync; switching primary put.io playback away from proven WebDAV without separate benchmarking; full category-by-category Settings content redesign; unsupported NFS; speculative provider capabilities; any older deferred 4.2 feature not explicitly promoted above.

Status: **DEFERRED-BY-SPEC**

Code mapping / verification: Excluded from implementation. Final scope audit must confirm none of these deferred capabilities entered the candidate.

### SCOPE-004 — Scope / Deferred Guardrail

Source: `SCOPE_AND_DEFERRED.md`

Do not add functionality merely because an old mockup contains it.


Status: **PENDING IMPLEMENTATION REVIEW**

Code mapping: pending detailed tracing. Verification: not yet run for this pass.

### VIS-001 — Visual Authority Rules

Source: `VISUAL_AUTHORITY.md`

Priority order: (1) written requirements in this handover; (2) specifically identified normative reference characteristic; (3) current physically approved Shield behaviour; (4) reference imagery; (5) older/historical material.

Status: **PENDING IMPLEMENTATION REVIEW**

Code mapping: pending detailed tracing. Verification: not yet run for this pass.

### VIS-002 — Visual Authority Rules

Source: `VISUAL_AUTHORITY.md`

Generated mockups frequently contain an incorrect top navigation. IGNORE generated top navigation unless a written requirement explicitly says otherwise. For many mockups the top nav is intentionally outside the design target.

Status: **PENDING IMPLEMENTATION REVIEW**

Code mapping: pending detailed tracing. Verification: not yet run for this pass.

### VIS-003 — Visual Authority Rules

Source: `VISUAL_AUTHORITY.md`

Labels:
- 🔒 NORMATIVE — implement the stated characteristic.
- 📎 REFERENCE — use only the stated characteristic; do not clone unrelated artifacts.
- 🧪 QA EVIDENCE — demonstrates current behaviour/problem, NOT desired design.
- 🚫 SUPERSEDED — do not implement.

Status: **PENDING IMPLEMENTATION REVIEW**

Code mapping: pending detailed tracing. Verification: not yet run for this pass.

### VIS-004 — Visual Authority Rules

Source: `VISUAL_AUTHORITY.md`

Global language: near-black/navy surfaces, brighter white text, monochrome iconography, Supernova blue as focus/accent. No cyan text as focus state. Focus is compact blue boundary/fill where specified + restrained outward glow; only ONE active focus target at a time. Media artwork itself is not recoloured.

Status: **PENDING IMPLEMENTATION REVIEW**

Code mapping: pending detailed tracing. Verification: not yet run for this pass.

### VIS-005 — Visual Authority Rules

Source: `VISUAL_AUTHORITY.md`

Physical Movies/TV card enlargement is the scale authority. Scale artwork+boundary+glow together. Do not zoom artwork inside a fixed box.

Status: **PENDING IMPLEMENTATION REVIEW**

Code mapping: pending detailed tracing. Verification: not yet run for this pass.

### VIS-006 — Visual Authority Rules

Source: `VISUAL_AUTHORITY.md`

Details selected-tab divider: the existing divider segment itself turns blue at identical stroke width. Never draw a thicker secondary underline.

Status: **PENDING IMPLEMENTATION REVIEW**

Code mapping: pending detailed tracing. Verification: not yet run for this pass.

### VIS-007 — Visual Authority Rules

Source: `VISUAL_AUTHORITY.md`

Movies/TV toolbar: divider-segment focus is temporary focus only, not persistent selected-tab state.

Status: **PENDING IMPLEMENTATION REVIEW**

Code mapping: pending detailed tracing. Verification: not yet run for this pass.

### VIS-008 — Visual Authority Rules

Source: `VISUAL_AUTHORITY.md`

The files in VISUAL_REFERENCES/REFERENCE imported from the 24-Sep package remain references only. They are useful for Dune hero geometry, playback HUD composition, current Movies baseline and library header direction, but newer written corrections in this handover win.

Status: **PENDING IMPLEMENTATION REVIEW**

Code mapping: pending detailed tracing. Verification: not yet run for this pass.

### VIS-009 — Visual Authority Rules

Source: `VISUAL_AUTHORITY.md`

VISUAL_REFERENCES/SOURCE_ARCHIVE/Supernova_chat_images_2026-09-24.zip is a preservation archive, NOT a set of instructions. Do not bulk-implement it. Use only images explicitly identified by the written specification or implementation team after matching to the described approved design.

Status: **PENDING IMPLEMENTATION REVIEW**

Code mapping: pending detailed tracing. Verification: not yet run for this pass.

### VIS-010 — Visual Authority Rules

Source: `VISUAL_AUTHORITY.md`

Branding: BRANDING/Supernova_Shield_Launcher_Master_APPROVED.png is approved visual direction. The derived 320x180 file is the Android TV launcher banner target. Preserve the master for future replacement. The wordmark/art may be refined only for legibility during exact asset production, not redesigned.


Status: **PENDING IMPLEMENTATION REVIEW**

Code mapping: pending detailed tracing. Verification: not yet run for this pass.
