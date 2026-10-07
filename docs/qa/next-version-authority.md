# Supernova — Next Version Authority

Status: **APPROVED implementation scope**
Recorded: 7 October 2026
Branch continuation point: `codex/post-4.1.7-shield-fixes`

> **READ THIS BEFORE IMPLEMENTING THE NEXT VERSION.** This record supersedes the earlier assumption that the post-4.1.7 candidate had completed all reported fixes. Physical NVIDIA Shield QA is authoritative for visual and interaction acceptance. Do not restart, reset, discard, clean or replace the existing candidate implementation. Preserve fixes that physically passed and correct/complete the items below. Do not merge to `main` merely because implementation is complete.

## 1. Candidate under physical QA

The signed post-4.1.7 candidate was built from application/test source:
`61a1ae5a21be90d185d448362c9729eb6c33cc42`

Candidate APK:
`org.courville.nova.markpreview-6040083-6.4.63-mark.4.1.7-preview-universal-release.apk`

APK SHA-256:
`d4364661dfbd760a3a66adf2c9e242c20fdccd2fb501e992894de326246aab8c`

Physical Shield QA found that several development statuses marked FIXED or VERIFIED did not conform on device. Automated validation remains useful, but it is not physical acceptance.

## 2. Physical status of the original 35 QA items

### PASS — protect against regression
4. Continue Watching → Playback → Back restores exact originating card/focus.
5. Clock typography/size.
9. Movies/TV → Details → Back restores exact originating item/focus.
12. Details primary action containers.
13. Details lower navigation/collapse.
17. Find a Match input is visible.
19. Find a Match result posters render.
24. My Providers colour logos.
25. My Providers Done separation.
26. Hidden-HUD D-pad reveals the normal HUD; no Up→More.
27. Primary Info button removed.
28. Exactly four primary HUD controls: Subtitles · Audio · Play/Pause · More.
29. No links from playback HUD panels to full application Settings.
33. HUD Supernova-blue accent/focus styling remains present through tested transitions.
34. No debug/internal preparation text observed during tested playback transitions.

### FAIL — corrective implementation required
1. Home focus enlargement: artwork still zooms within the confinement rather than the complete card/focus treatment scaling as one unit.
2. Horizontal row clipping: artificial clipping remains. This is now an app-wide carousel correction covering Home and applicable Details rows such as Seasons/Episodes and Extras.
3. Home Move/Hide: physical Left-edge behaviour still presents the old context-menu treatment rather than the approved Move/Hide interaction.
6. Header blur/fade: fade extends too deeply and washes out upper content.
7. Movies/TV vertical row positioning: attempted centring produces large snapping/jumping.
8. Movies/TV toolbar alignment: controls still float above the divider instead of sitting on it.
10. Details loading: official logo/title artwork and lower sections such as Extras/More Like This still appear visibly late.
11. Details hero metadata: genre must share the main metadata line rather than sit on a separate line.
16. Find a Match keyboard boundaries/focus remain unreliable.
18. Find a Match typing/navigation boundaries remain inconsistent.
20. Language presentation remains inconsistent; approved replacement is flag-based language presentation.
21. Network & Files three-panel geometry is too high/tall and extends below the visible viewport.
23. Settings nested navigation is in the wrong panel; approved hierarchy is defined below.

### SUPERSEDED / replaced by approved redesign
14. Old compact Details Information layout is superseded by the new Details information design.
15. Old Cast & Crew correction is superseded by the approved 8 Cast + 8 Crew text-only design.

### PARTIAL / approved implementation still required
22. Settings three-panel structure exists but needs approved vertical centring.
30. Subtitles hierarchy is now fully decided; implement the final structure below and fix the subtitle-track frame-jump defect.
31. Audio hierarchy is now fully decided; implement the final structure below.
32. More hierarchy is now fully decided; implement the final structure below.
35. Artwork reliability remains an observation/investigation item. Do not claim a defect solely from historical request/failure counts; fix evidenced missing-artwork, excessive-request, flicker/reload or caching problems.

## 3. Why Home #1–#3 are reopened

Earlier development reporting used **FIXED** to mean corrective code was present, not physically accepted. That distinction was valid, but the results were too confident:

- #1 changed `PreviewCardPresenter` clipping/scaling, but physical QA shows it solved the wrong layer: the complete artwork + rounded focus boundary + glow still does not enlarge as one aligned component.
- #2 changed Home rail edge geometry, but physical QA shows the required deliberate partial-card carousel behaviour was not achieved.
- #3 was marked **VERIFIED — NO CODE CHANGE REQUIRED** because tests proved Move/Hide actions existed. Physical QA shows the actual Left-edge presentation is still the old context-menu behaviour and therefore does not conform.

For visual/interaction requirements, tests must validate the user-visible requirement rather than a proxy such as existence of an action, a scale property or an inset change.

## 4. Home — approved next-version work

1. Implement the approved **Featured exposed-card redesign**. This is no longer parked for this release. Use the written geometry/behaviour authority in `docs/design/HOME.md`; do not fabricate missing historical mock-up pixels.
2. Fix focused-card enlargement so artwork, rounded boundary and outward glow scale together as one aligned component. No artwork protrusion.
3. Replace accidental horizontal clipping with a deliberate carousel treatment. A partial next/previous card may indicate continuation; focus must remain fully usable.
4. Correct Home Move/Hide. LEFT at the leftmost item exposes the approved Move / Hide treatment, not the old generic context menu; no Delete there.
5. Reduce top-navigation blur/fade depth substantially: a shallow transition immediately below navigation, enough for readability without washing out upper page content.
6. Preserve the physically passed exact Continue Watching return-focus behaviour.

## 5. Shared horizontal carousel rules

Apply a common carousel principle to Home and Details horizontal content where applicable:
- one logical category is one horizontal row;
- never wrap a category onto a second row merely because it contains many items;
- six or fifteen trailers still remain one horizontally scrollable Trailers row;
- deliberately expose part of the next/previous item when continuation exists rather than accidental hard clipping;
- focused cards and their glow must not be clipped;
- card artwork/text bounds remain correct.

## 6. Movies & TV library controls — approved

### Toolbar order
Grid view:
**Grid/List · Filters · Sort · Order · Unmatched (only when present)**

List view:
**Grid/List · Filters · Sort · Order · Columns · Unmatched (only when present)**

Rules:
- Grid/List is the leftmost control.
- Unmatched is always the last/rightmost control and is hidden entirely when there is no unmatched media.
- Columns is shown only where applicable to List view.
- Sort and Order remain separate.
- Order must have an explicit label, e.g. **Order: Newest First**, not merely an icon plus the selected value.
- The complete toolbar moves down so its controls visually sit **on the horizontal divider line**, not above it.
- Preserve remembered Movies/TV state separately.

### Filters — live application
Selections apply immediately. A tick is the confirmation; there is no second **Done** step.

Genre:
- **All Genres** at the top.
- available library genres in the middle.
- remove Done.
- selecting/toggling a genre immediately updates the library behind the menu.
- Back only navigates out; it does not apply/cancel.
- All Genres clears individual genre selections and immediately restores the all-genre view.

Year:
- show only years represented in the current library.
- tick/untick immediately applies.
- keep **Clear Selection**.
- remove Done.

Streaming Services:
- tick/untick immediately applies.
- remove Done.
- retain provider filtering semantics for local titles with known availability.

### Compact active-filter summary
Never concatenate an unbounded list of selected genres/years/providers into the toolbar. Keep the toolbar width stable:
- no active filter: **Filters**
- one active selection: **Filters: Crime** (or equivalent concise value)
- multiple active selections: **Filters (N)**
The submenu ticks remain the authoritative detail of exactly what is selected.

### Vertical navigation
Correct the large snapping/jumping introduced by row-centering. Keep the active row comfortably/approximately centred where possible, but movement must be smooth and deterministic and exact return-focus restoration takes precedence.

## 7. Details — approved redesign and corrections

### Hero
- Improve real loading latency/root cause for official logo/title and lower sections; do not merely mask delay.
- Genre belongs on the same hero metadata line as year/runtime/rating or season/episode information.
- Streaming/provider action between Play/Resume and More must be D-pad focusable and match neighbouring action height, border, alignment and geometry.

### Information area
Old #14/#15 layouts are superseded.

Tier 1:
**Key Information | Technical Information | Reception**
- borderless;
- aligned columns;
- breathing room below the navigation divider;
- subtle vertical separators;
- no heading icons;
- Reception may contain reliable awards/ratings and one short review excerpt where legally/reliably available.

Tier 2 below a subtle horizontal divider:
**Cast | Crew**
- two equal-width columns;
- one subtle vertical divider;
- Cast left edge aligns with Key Information left edge;
- heading baseline and first-row spacing match;
- no individual row separators.

Cast/Crew:
- exactly up to 8 Cast + 8 Crew when data is available;
- text only;
- no portraits, placeholders or cast/crew artwork requests;
- no displayed counts;
- no More Cast / More Crew;
- no carousel/arrows;
- Cast: person bright/bold white, character softer white/grey;
- Crew: person bright/bold white, job softer white/grey;
- prioritise key creative roles.

## 8. Extras and trailer playback — approved

Extras:
- each category (Trailers, Teasers, Featurettes, Clips, etc.) is exactly one horizontal row;
- never wrap;
- fix title/card bounds so trailer names are not incorrectly clipped/truncated.

Trailer overlay:
- Details page remains underneath, strongly blurred and dimmed including header;
- clean centred 16:9 video, roughly 60% of screen area, aspect preserved;
- no surrounding Supernova container/border;
- no Supernova title, Open YouTube or Close controls;
- no full Supernova playback HUD;
- minimise YouTube chrome only through officially supported behaviour and retain any mandatory provider branding/UI;
- Back closes and restores exact launching trailer-card focus;
- trailer completion closes and restores the same focus;
- OK/Play may provide simple Play/Pause only if technically appropriate;
- quick blur/dim transition around 150–200ms is acceptable.

## 9. Search / Find a Match — approved

### Forgiving title matching
For local Search and Find a Match, normalise where technically appropriate:
- case;
- punctuation/periods;
- apostrophes;
- hyphens;
- whitespace;
- leading articles The/A/An.
Exact/near-exact matches still rank highest.
Examples: `OC` → **The O.C.**; `dark knight` → **The Dark Knight**; `schitts creek` → **Schitt’s Creek**.

### Find a Match keyboard
Hard boundaries stay put with no visual flicker:
- number row + Up stays on the same key;
- Q + Left stays on Q;
- Clear / Space / Backspace + Down stays on the same key;
- P/right edge + Right deliberately enters results when results exist;
- do not transiently move focus into the input and then correct it.

### Find a Match results
Use normal Search as visual authority:
- consistent result-row height;
- consistent artwork slot dimensions/alignment;
- consistent text start position;
- title hierarchy;
- secondary metadata;
- synopsis treatment;
- vertical spacing;
- focus treatment.
Fields may differ because the data source differs.

## 10. Language presentation — approved

Use the approved **flag representation for languages** consistently wherever language is shown. Remove the current inconsistent mix of flags, globes and two-letter codes. Implementation must be deterministic and must not silently change the represented language. Where language-to-flag mapping needs a product mapping because a language spans multiple countries, use the project's approved/default mapping rather than inventing inconsistent per-screen behaviour.

## 11. Settings — approved correction

Keep the three-panel visual model, but correct geometry:
- define usable vertical area from the **bottom edge of the top-navigation text** to the **bottom edge of the app/TV viewport**;
- vertically centre the complete three-panel group within that usable area;
- equal remaining space above and below the panel group;
- Settings requires a smaller downward correction than Network & Files.

Nested navigation is explicitly revised:
- nested Integration children belong indented in the **left panel** beneath their parent after explicit entry;
- example: Integrations → OpenSubtitles / IntroDB / Trakt;
- selecting a child populates its settings in the centre panel;
- Right/Left/Back and exact opener return must remain coherent.
This supersedes the earlier rule that nested subcategories must stay in the middle workspace.

Protect the physically passed My Providers colour logos and separated Done action.

## 12. Network & Files — approved correction

Preserve the approved three-panel semantics, but apply the same outer geometry principle as Settings:
- usable area begins at bottom edge of top-navigation text and ends at bottom of viewport;
- vertically centre the complete three-panel group within that area;
- all panel bottoms must be visible;
- Network & Files currently needs a substantially larger downward/fit correction than Settings;
- do not regress into an unstructured/classic browser.

## 13. Playback HUD — final approved structure

Primary HUD remains:
**Subtitles · Audio · Play/Pause · More**
- exactly four controls;
- Play/Pause remains dead-centre;
- any D-pad direction reveals the same standard HUD;
- no Info;
- no hidden Up→More;
- no links to full application Settings;
- visually rebalance spacing/alignment without adding a fifth control.

### Subtitles
First level:
- Off / available subtitle tracks;
- Download Subtitles;
- Sync;
- Appearance.

Appearance stays in the playback HUD so changes can be seen against the actual subtitles in real time. Retain:
- Style including style selector/colour choices;
- Vertical Position;
- Outline subtitles;
- Subtitle background;
- Background opacity.

Global defaults/provider/preferred language remain in full Settings.

**Defect:** switching subtitle tracks currently makes video visibly jump forward a frame and snap back. Fix the root cause. Track changes must not seek/restart/rebind/recreate the video surface or visibly disturb playback position/frame.

### Audio
- available audio tracks;
- Audio Sync;
- Audio Boost;
- Night Mode.
These are contextual/session controls; persistent defaults remain in Settings.

### More
Flat compact menu, no unnecessary section headings:
- Playback Speed;
- Play Mode;
- Format.

Remove **Report a Problem**.
Format is picture/aspect presentation (e.g. Original, Full Screen, Stretch, 4:3, 16:9), not technical metadata.

## 14. Regression protections from physical Shield QA

Do not regress:
- exact Continue Watching return focus;
- exact Movies/TV Details return focus;
- clock typography;
- Details permanent action containers;
- lower Details tab/hero navigation;
- Find a Match visible input;
- Find a Match poster artwork;
- My Providers colour logos;
- My Providers separated Done;
- unified D-pad HUD reveal;
- primary Info removal;
- four-control HUD;
- absence of full Settings links from HUD;
- persistent Supernova-blue HUD accent/focus styling;
- absence of debug/internal preparation text.

## 15. Artwork reliability

Continue observing/investigating:
- missing posters/backdrops;
- repeated reload/flicker;
- excessive/duplicate requests;
- failed-load retry behaviour;
- caching/fencing.
Historical raw request/failure counts were affected by deliberate re-scraping and are not proof by themselves.

## 16. Conformance and validation rule

For every item, distinguish:
1. **IMPLEMENTED** — code exists.
2. **AUTOMATED VALIDATED** — relevant tests/build gates pass.
3. **RENDER/INTERACTION REVIEWED** — the actual rendered/interaction requirement has been inspected where tooling permits.
4. **SHIELD ACCEPTED** — only after physical NVIDIA Shield QA confirms it.

Do not label an item physically fixed/accepted merely because a proxy test passes. In particular, Home focus enlargement, carousel clipping and Move/Hide require user-visible conformance evidence.

Do not weaken/remove existing regression tests simply to make a changed implementation pass. Add tests that express the actual requirement.

## 17. Explicitly out of scope

Unless separately approved later, do **not** implement:
- Profiles;
- Smart Collections/custom pages;
- Library Health centre;
- Discovery/Coming Soon;
- package/application-ID transition;
- new signing identity;
- fresh-install identity migration;
- NFS without genuine support;
- put.io expansion/production OAuth invention;
- broad unrelated dependency or architecture changes.

Do not merge to `main` automatically.

## 18. Next implementation procedure

1. Start from the existing `codex/post-4.1.7-shield-fixes` state; do not restart or discard passed work.
2. Read this document plus the updated `docs/design/` authorities.
3. Inspect the live implementation before changing it.
4. Implement the complete approved scope above.
5. Preserve all Shield-passed behaviours with regression tests.
6. Run full source, UI/render, lint, identity/signing and build validation.
7. Produce a signed installable candidate APK using the existing identity/signing strategy.
8. Record exact source commit, APK filename/hash, signing identity and validation evidence in GitHub.
9. Stop before merge; physical Shield acceptance remains required.


## 19. Visual-reference provenance

Read `docs/design/VISUAL_REFERENCE_MANIFEST.md`. It records the audited recovery status of actual mock-up bytes and historical identifiers. Do not choose an arbitrary generated iteration when exact approval provenance is absent. Current written specifications override conflicting pixels in older images.


## Physical Shield QA addition — seek-bar scrubbing must preview the selected position (7 October 2026)

Evidence: user-supplied physical Shield video `IMG_4710.mp4` (~16.5 seconds), reviewed on 7 October 2026.

Observed behaviour:
- From the primary playback HUD, pressing Up from Play/Pause or another primary HUD control correctly moves focus to the seek bar.
- The timestamp bubble appears correctly.
- LEFT/RIGHT changes the seek-bar cursor / timestamp bubble to an earlier or later position.
- **The video image/playback does not follow the selected seek position while scrubbing.** Playback continues visually from the old/current position even when the seek cursor has moved substantially (example: roughly ten minutes behind).
- This creates a mismatch between the timestamp/seek cursor and the frame the user is seeing, making TV/D-pad seeking difficult to judge.

Required behaviour:
- While the seek bar has focus, LEFT/RIGHT scrubbing must give meaningful visual feedback for the currently selected target position.
- The displayed video frame/preview must track the seek target rather than continuing to show unrelated playback from the pre-scrub position.
- Preserve the existing correct timestamp bubble.
- The implementation should avoid repeatedly committing normal playback seeks in a way that causes unstable playback, excessive decoder churn or visible snapping.
- On confirmation/commit of the seek, playback must continue from the selected target position.
- Back/cancel behaviour must be deterministic and must not accidentally commit an unintended seek.
- Preserve the already accepted four-button HUD, D-pad HUD reveal behaviour, styling and focus behaviour.

Acceptance:
1. Enter the seek bar from any primary HUD button.
2. Move LEFT/RIGHT by both small and large amounts.
3. The visual frame/preview and timestamp correspond meaningfully to the currently selected target.
4. Commit the seek and verify playback resumes from that target without jumping back to the old position.
5. Exercise repeated seeking and boundaries near start/end.
6. Physical Shield validation is required; a focus/unit test alone is not sufficient.

Status: **NEW PHYSICAL-QA DEFECT — REQUIRED FOR NEXT CORRECTIVE BUILD.**


### Physical remote REW/FF timestamp feedback

Additional physical Shield requirement:
- When playback HUD controls are active/visible and the user invokes rewind or fast-forward with the physical remote's dedicated transport controls, the existing rewind/fast-forward action may continue to use its current seek behaviour.
- **The timestamp bubble must become visible while the rewind/fast-forward operation is active**, even if seek-bar focus was not entered first.
- The timestamp shown must track the current seek target so the user can see the position they are moving to.
- Repeated remote REW/FF presses/steps must update the bubble rather than leaving stale time.
- After the operation completes/commits, the bubble should dismiss according to the normal HUD timeout/interaction behaviour.
- Do not require the user to move focus to the seek bar merely to obtain timestamp feedback.
- Preserve the existing working physical-remote rewind/fast-forward behaviour; this is a feedback/presentation correction, not a request to replace the transport mechanism.

Acceptance:
1. With focus on each primary playback HUD control, invoke physical-remote REW and FF.
2. Confirm the transport action still works.
3. Confirm the timestamp bubble appears immediately and tracks the seek target.
4. Test repeated presses/steps and both directions.
5. Confirm normal playback resumes at the committed target and HUD/bubble dismissal remains coherent.
6. Validate on physical Shield.


## Pre-Codex readiness note — 7 October 2026

The approved Home Featured visual-reference gap is resolved by normative written reconstruction in `docs/design/HOME.md §11`; the unavailable generated PNG is not an implementation blocker.

Before implementation, Codex must treat the following precedence as authoritative:
1. this next-version authority for consolidated physical-QA scope and regression protections;
2. latest explicit amendments in the corresponding `docs/design/` document;
3. completed audit evidence under `docs/audits/`;
4. historical implementation/checkpoint material only where it does not conflict with later physical QA or explicit design decisions.

No visual/interaction item becomes accepted merely because code exists or an automated proxy passes. Final acceptance requires the applicable render/interaction review and physical Shield validation.

The branch is ready for the consolidated next corrective implementation pass. Do not merge to main, begin the later identity/signing transition, or silently expand scope.

## Corrective implementation candidate checkpoint — 7 October 2026

The consolidated approved implementation is now delivered as a signed **Shield-test candidate**, source `b021c51dcbbe2d5679fb9015c43830713f02aa0b`; source workflow 37568094178 and full signed workflow 37568094185 are SUCCESS. See [exact APK identity/download/evidence](next-corrective-candidate.md), [all-item conformance and physical checks](next-corrective-conformance.md) and [implementation/investigation record](next-corrective-implementation.md). Later delivery-documentation commits are not APK source.

This checkpoint does not revise the physical findings or confer SHIELD ACCEPTED. All corrected visual/interaction requirements and accepted regression protections require user Shield testing on the new APK. #35 remains observationally PARTIAL beyond its evidenced corrections; release numbering remains NEEDS DECISION. Language mapping is resolved in current Settings/asset authority. Main remains unmerged; next action is physical QA, not another release.


## Immediate Home hotfix turnaround — physical Shield QA (7 October 2026)

This is an intentionally narrow, high-priority corrective pass requested before continuing the broader candidate QA. Implement **only the Home items below plus required regression protection**, then build a replacement Shield-test APK. Do not use this hotfix as permission to start unrelated outstanding fixes.

### H1 — Featured card vertical size / viewport composition — FAIL
Observed on physical Shield: the implemented Featured card is still too short.

Required:
- Keep the current safe top position below the top navigation; do not grow upward into the header.
- Increase Featured height downward.
- The exposed previous/next Featured cards must use exactly the same top and bottom boundaries / height as the active centre card. They are currently visibly shorter.
- The initial Home viewport should show the `Continue Watching` heading plus approximately **55–65% (target ~60%)** of its artwork height as a teaser below Featured.
- The full Continue Watching cards do not need to fit in the initial viewport.
- No second ordinary Home row should be visible.
- Preserve comfortable separation between Featured and Continue Watching.

### H2 — Duplicate/background Featured artwork outside card — FAIL
Observed on physical Shield: as Featured items are browsed, the selected item's artwork is rendered both inside the card and as imagery behind/outside the carousel.

Required:
- Featured artwork/backdrop must be contained/clipped to the Featured card.
- Do not render a second copy of the selected Featured artwork as the Home background behind/below the carousel.
- Surrounding Home area should use the normal Home background treatment.
- Preserve the card's rounded corners and cinematic internal gradient/artwork treatment.

### H3 — Horizontal edge clipping — FAIL
Observed on physical Shield: Featured and normal Home rows are cut off at an artificial internal vertical boundary while visible screen space remains.

Required:
- Remove/invalidate the internal inset clipping boundary/mask for horizontal carousels.
- Featured neighbour cards and normal row cards may continue naturally toward and beyond the physical screen edges.
- Cards/focus glow/rounded boundaries are clipped only when they genuinely leave the visible display, not at an inset parent boundary.
- Preserve deliberate partial-card continuation at screen edges.
- Apply this to both Featured and ordinary Home horizontal rows.

### H4 — Featured action simplification — APPROVED DESIGN CHANGE
Remove `Play` / `Resume` from Featured.

Featured has **one action only: More Info**.
- LEFT/RIGHT browses previous/next Featured item.
- OK on More Info opens the normal Details page.
- Play/Resume remains on Details, not on Featured.
- Do not introduce an additional focus layer merely to select between actions.

### H5 — Featured information hierarchy / spacing — FAIL
Official title/logo typography/artwork is acceptable and should be retained.

Improve vertical hierarchy:
1. stable title/logo zone;
2. deliberate larger breathing space;
3. metadata/sub-data lowered beneath that zone;
4. deliberate gap;
5. synopsis;
6. single More Info action lower in the card.

Do not position metadata directly from the raw bottom edge of arbitrary logo artwork. Logos vary greatly in proportions. Use a stable title/logo zone so metadata placement remains visually consistent across titles such as wide, tall and compact logos.

The taller card from H1 provides the space needed; do not solve this by shrinking typography/content.

### H6 — Normal Home thumbnail focus enlargement — SHIELD ACCEPTED / REGRESSION PROTECTED
The previous defect where focused row artwork zoomed outside its thumbnail/card boundary is no longer observed.

Preserve the current corrected behaviour:
- artwork, rounded boundary and focus treatment remain aligned;
- no protruding image outside the card;
- H3 edge-clipping work must not regress this accepted focus behaviour.

### H7 — Move / Hide presentation — PARTIAL IMPLEMENTATION / SHIELD FAIL
Observed: LEFT from the first item now exposes Move/Hide, but they still appear as a floating/context-menu presentation on the left.

Required:
- LEFT from the first media item enters **dedicated Move and Hide controls inline with that row**.
- No floating context-menu panel.
- Controls visually belong to the row and use normal Supernova focus styling.
- RIGHT returns naturally to the first media card.
- UP/DOWN/BACK behaviour is deterministic and preserves sensible row/focus position.
- Preserve H6 focus scaling.

### Hotfix acceptance / process
- Treat H1–H5 and H7 as required corrections; H6 as regression protection.
- Validate across multiple Featured items with different logo proportions and both movie/TV metadata.
- Validate first/middle/last horizontal positions and focused cards at screen edges.
- Validate Move/Hide on multiple Home rows.
- Run relevant automated/build/render/focus checks, but physical Shield QA remains final authority.
- Record exact files changed, tests, source commit, APK filename/hash and signing/application identity in GitHub.
- Produce a signed replacement Shield-test APK using the existing identity/signing strategy.
- **Do not merge to main.**
- Stop after the Home hotfix candidate is built/documented so physical Shield QA can resume.

### Home hotfix implementation/delivery checkpoint — 7 October 2026

The narrow H1–H5/H7 implementation is delivered as a signed replacement **Shield-test candidate**, exact source `f97f62942f6b4f1a90e2e15258db452384d6b39c`. Source run 37614060979 and full signed build 37614061018 are SUCCESS. [Candidate identity/download and H1–H7 conformance](home-hotfix-candidate.md); [implementation/test/render evidence](home-hotfix-implementation.md). Later delivery-documentation commits are not APK source.

This does not overwrite the physical findings or self-assign SHIELD ACCEPTED to the corrections. H6 retains the user's prior SHIELD ACCEPTED status and is regression-protected; all replacement physical QA remains the user's responsibility. Main is unmerged. Stop at the replacement APK; next action is physical Home review, then wider user Shield QA, not unrelated implementation or a later release.
