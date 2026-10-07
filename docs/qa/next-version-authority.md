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
