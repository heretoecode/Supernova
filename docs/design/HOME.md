# Home — Design and Behaviour Authority

Status: **ACTIVE / APPROVED next-version Featured redesign + corrective work**

This document preserves the recoverable Home-screen design authority. It distinguishes the **current implemented/approved 4.1.7 baseline** from a **later PARKED Featured redesign direction** discussed after that baseline. Do not silently combine them.

## 1. Global Home shell — APPROVED/current

Canonical top navigation:
- SUPERNOVA at far left.
- Home · Movies · TV Shows.
- Flexible spacer.
- Network & Files · Search icon · Settings icon · Clock.
- No Streaming or Library top-nav items.
- No separator before the clock and no line under navigation.
- Textual nav: 19sp normal/light; contents remain white.
- Unfocused controls have no box.
- Focus uses a compact rounded Supernova-blue boundary with restrained outward glow; no cyan focus text.
- Home+LEFT is consumed and remains Home. Settings+RIGHT remains Settings. Edge input must not teleport into page content.
- Utility/top fade must not intrude excessively into Network & Files, Search or Settings.

The successful Home structure and physically liked card-enlargement feel are protected.

## 2. Current 4.1.7 Featured/hero — APPROVED/current baseline

The current baseline is the 4.1.7 Featured implementation, not the later exposed-card concept below.

### Content and artwork
- Featured is library/local-content led. It must not become an advertising surface or generic remote recommendation feed.
- Use real source artwork. Never fabricate people or substitute invented imagery.
- Official logo/title artwork must ignore transparent padding when calculating visible bounds and preserve aspect ratio.
- Synopsis width relates to visible logo width: target ~90% of fitted visible logo width, clamped to approximately 25–32% of viewport width.
- Showcase artwork sits on the right, beneath the Network & Files→clock area, to the right of synopsis and above Continue Watching.
- Prefer reposition/crop of real artwork. Darken/blur text-safe areas when required for legibility.
- When only poster-shaped fallback artwork is available for a landscape Home card, preserve the uncropped artwork with letterbox/framing rather than destructive centre-cropping.
- Historical geometry rule retained from the approved Featured work: title maximum two lines; synopsis maximum three lines with ellipsis; content changes must not resize the hero/control geometry.

### Actions and cycling
- Play/Resume is the primary action.
- More Info is the secondary action.
- LEFT/RIGHT while More Info is focused cycles Featured **without moving focus away from More Info**.
- Carousel indicators remain persistent and visually centred for the Featured carousel.
- Indicator transition is a pill/dot morph, approximately 180–220ms ease-out (implemented target 200ms).
- Featured changes update the hero rather than causing all Home rows to rebind/rebuild.
- Do not allow a Featured change to throw focus to Home/top navigation.

### Startup
- Home should become usable promptly from local/indexed data; metadata enrichment continues in background.
- The first-frame/startup gate observes visible card/backdrop artwork readiness and uses a short fade.
- Earlier implementation work deliberately removed a redundant first-backdrop fade after the launch-readiness gate.
- No expensive live per-card blur is required.

### Vertical relationship to rows
- Featured information and Continue Watching must have deliberate breathing room; Continue Watching sits clearly below the hero/carousel area rather than crowding it.
- Preserve the centred carousel indicator between Featured and the following Home content.
- Avoid clipping the next Home row into the Featured region.

## 3. Home rows — APPROVED/current

- Recently Played / Continue Watching remains prominent near the top.
- Continue Watching display is bounded (4.1.7 implementation cap: 30), but display capping must **never destroy playback state** or the in-progress indication elsewhere.
- Recently Added is bounded (4.1.7 implementation cap: 50) so first import cannot create an effectively endless row.
- A later product decision requires Recently Added to show next-to-watch rather than duplicating titles unnecessarily; preserve this as product intent when reconciling current behaviour.
- Home/movie/TV cards preserve the physically liked enlargement amount.
- Artwork + rounded focus boundary + outward glow scale as **one aligned component**. Artwork may not protrude outside the boundary; glow may not clip into square corners.
- Horizontal rows must not clip focused artwork/cards at their bounds.
- Focused row positioning should keep the active content comfortably visible rather than forcing navigation toward the top bar.

### Return focus — current fixes-only requirement
Continue Watching → Playback → Back must restore the exact originating card when still valid:
- same card visibly focused;
- immediate LEFT/RIGHT works;
- no extra D-pad press needed to establish focus;
- DOWN must not jump to Home because of an invalid focus target;
- use deterministic return-focus more broadly where the originating target remains valid.

## 4. Customise Home — APPROVED/current

- Customise Home remains a compact hub with inline row controls.
- LEFT at the leftmost Home-row item reveals **Move / Hide only**; no delete action there.
- Preserve reorder, visibility and membership behaviour.
- Row toggles should update locally without whole-page flashing/rebuild.
- Delete confirmation must not clip focus/buttons or wrap awkwardly.
- Genre selector: fixed header/footer with a scrolling middle viewport; no paint/clipping outside it.
- Maximum Items supports: No Limit, 10, 20, 30, 40, 50, 75, 100 and exact numeric Select. Internal 0 may represent No Limit.
- Use the correct full keyboard/input path for exact numeric entry; preserve good keyboard/focus behaviour.
- A subtle backdrop/shadow is acceptable.
- Clearing Watch Next remains separate and confirmed.

## 5. Exposed-card Featured redesign — APPROVED FOR NEXT VERSION

This is not the current 4.1.7 hero. **It is explicitly activated for the next version** as the release's deliberate major Home design change.

The later design discussion moved Featured toward an **exposed-card carousel**, deliberately *not* a conventional horizontal poster row.

Recoverable approved direction:
- One raised, visually dominant **centre Featured card**.
- Centre card approximately **80–88% of usable Featured width**.
- Partial neighbouring cards remain visible at left and right as carousel teasers, approximately **5–8% exposure** each rather than full peer cards.
- Centre card has subtle elevation/shadow/depth; neighbours are visually subordinate.
- Large backdrop/artwork occupies the right side with a dark gradient into the information area.
- Information area contains official logo/title treatment, metadata and a short synopsis/tagline.
- Movie metadata direction: year · runtime · certification · genres.
- TV metadata direction: year · seasons · certification · genres.
- Actions: **Play/Resume** and **More Info**.
- Local/provider identity may be shown when applicable, but the Featured surface remains a user-library experience and must not become an advert/recommendation rail.
- D-pad LEFT/RIGHT changes the featured card.
- Transition direction discussed: approximately **350–450ms translate/depth transition** between cards, maintaining the sense that the centre card becomes dominant while the departing/arriving neighbour changes depth.
- Layout is fixed: changing titles/synopsis must not cause the carousel geometry or action positions to jump.
- Title maximum two lines; synopsis maximum three lines/ellipsis remains a useful protected geometry rule unless a later approved mock-up explicitly supersedes it.

### Important visual-asset note
Historical discussion included generated/approved mock-up work for this direction. The original image bytes have **not yet been recovered into this repository**. Do not manufacture a replacement and call it the approved original. Until the original is recovered, this written geometry/behaviour record is the durable authority for the recoverable agreement.

## 6. Supersession / conflict rules

- Section 2 is the current implemented 4.1.7 baseline.
- Section 5 is **APPROVED next-version implementation authority** and supersedes the parked status.
- The completed post-4.1.7 candidate did not activate Section 5; the next implementation pass must.
- Generated mock-ups frequently had incorrect top navigation. The written canonical navigation in Section 1 overrides any conflicting mock-up navigation.
- Do not infer an external streaming recommendation feature from provider/local identity artwork.
- Do not alter package/signing identity as part of Home work.

## 7. Known physical QA / current corrections

Current 4.1.7 Shield QA requires:
- Home artwork focus containment.
- Home horizontal row clipping correction.
- correct Home-row Move/Hide controls.
- exact Continue Watching → Playback → Back focus restoration.
- clock typography/size correction.
- top blur/fade region correction.
- real Featured artwork/crop/glow/readability remains physical-device visual QA.

## 8. Evidence trail

Primary repository evidence:
- `docs/preview-4.1.7/REQUIREMENTS.md` — UI-001 through UI-009 and QA-002/003.
- `docs/archive/root-history/NOVA_CODEX_RETURN_HANDOVER_4.1.2.md` — startup gate, Featured action/indicator, Home editor and preserved behaviours.
- `docs/archive/root-history/NOVA_UI_AUDIT_REPORT_4.1.2.md` — runtime visual audit.
- `docs/preview-4.1.4/RELEASE_NOTES.md` — hero-only Featured update, first-backdrop behaviour and focus.
- `docs/qa/4.1.7-final-shield-qa.md` — current physical fixes-only scope.
- `docs/project/HISTORY_RECONCILIATION.md` — recovered older product/design decisions and supersession notes.

Historical ChatGPT design discussion has now been consolidated here for the later exposed-card direction so that the design intent is not dependent on chat retention.

## 9. Still to recover

- Original approved image/mock-up bytes for the later exposed-card Featured design.
- Any exact pixel measurements that existed only inside an unrecovered image rather than written discussion.
- Final visual comparison between that parked design and the current 4.1.7 hero.

These gaps are explicit. They must not be filled by invention.


## 10. Next-version physical-QA amendments — APPROVED

- Physical Shield QA reopens Home focus enlargement, horizontal row clipping and Move/Hide despite earlier development reports.
- Focus enlargement must scale artwork + rounded boundary + outward glow as one aligned unit; changing only image clipping or a scale property is not sufficient.
- Horizontal Home rows follow the shared carousel rule in `docs/qa/next-version-authority.md`: deliberate partial continuation, no accidental hard clipping, no clipped focused glow.
- LEFT at the leftmost Home-row item must expose the approved Move / Hide interaction, not the old generic context menu.
- Reduce the top fade to a shallow transition immediately below navigation.
- Preserve the physically accepted Continue Watching exact return-focus behaviour.
- The Featured exposed-card redesign in section 5 is now in scope for this next version.


## 11. Approved Home Featured final visual geometry — 7 October 2026

The final Home Featured mock-up approved in the design discussion is the authority for composition/geometry. Its content (Dune: Part Two artwork, copy and media items) was illustrative only.

Because the generated mock-up binary is not currently transferable into this repository, the following written reconstruction is intentionally complete and **is the authoritative substitute for the image**. Codex must not block implementation waiting for the PNG or substitute an older Home mock-up.

### Hard geometry/composition requirements
- The top navigation/header remains completely separate and unobstructed.
- The Featured carousel begins **below** the navigation with a deliberate visible gap. No part of the Featured card may slide behind or underneath the top navigation.
- The active Featured card is substantially larger/taller than the earlier concept. Increase height **downward**, not upward into the header.
- The active card has clearly visible rounded corners on all four sides and reads as a discrete floating card, not an extension of the header/background.
- A narrow portion of the previous and next Featured cards remains visible at the left and right edges. These exposed neighbours are the carousel affordance.
- **No pagination dots/page indicators** appear beneath the Featured carousel.
- The viewport composition shows the large Featured carousel plus **only one normal Home content row beneath it**. The approved reference used Continue Watching as that row. A second row such as Popular Movies must not be visible in the same initial viewport.
- Preserve enough vertical breathing room between Featured and the row beneath it that neither feels clipped or accidentally attached.
- Featured retains the approved information/action composition: title, concise metadata, synopsis, Play/Resume and More Info, with cinematic artwork/gradient treatment.
- LEFT/RIGHT moves through Featured items while preserving the exposed-neighbour carousel language.

### Acceptance
Physical Shield review must compare the rendered Home screen against this geometry, not merely verify that a Featured carousel exists. Specifically reject:
- card intrusion beneath navigation;
- a card that has reverted to the earlier shorter size;
- pagination dots;
- no exposed neighbour cards;
- two or more ordinary Home rows visible in the initial viewport;
- clipped rounded corners/glow;
- enlargement that affects only artwork rather than the complete card.

This section supersedes any older Home Featured dimensions/composition that conflict with it.


## 12. Physical Shield Home hotfix amendments — 7 October 2026

These amendments supersede conflicting earlier Home details for the immediate replacement candidate.

- Featured becomes taller by extending downward while retaining the safe gap below top navigation.
- Previous/next exposed Featured cards have the **same height/top/bottom geometry** as the active card.
- Initial viewport intentionally shows only the Continue Watching heading plus roughly 55–65% (target ~60%) of that row's artwork; full cards are not required to fit.
- Selected Featured artwork is contained inside the card; do not duplicate it as background imagery behind the carousel.
- Horizontal Featured and normal Home rows must not be clipped at an artificial inset boundary. Continuation is clipped only at the physical screen edge.
- Featured has one action only: **More Info**. Remove Play/Resume from Featured; playback action remains on Details.
- Retain official title/logo treatment, but use a stable logo/title zone. Lower metadata with more breathing room, then synopsis, then More Info. Do not let variable logo aspect ratios determine the metadata baseline.
- Normal Home row focus enlargement is physically accepted and regression-protected.
- Move/Hide must be dedicated **inline row controls**, not a floating/context menu.

See the H1–H7 acceptance requirements in `docs/qa/next-version-authority.md`.

## Approved — Featured card readiness gate (8 October 2026)
Physical Shield evidence: a newly indexed *Mr. Robot* episode initially entered Home Featured with its raw filename, absent descriptive metadata and unready artwork; after asynchronous enrichment it displayed the correct title logo, genres, synopsis and backdrop. **Do not render or rotate a newly indexed Featured candidate into the visible carousel until its presentation is complete.** Continue showing already-ready cards while metadata/artwork resolve. Readiness requires a resolved catalogue title identity/display title, the required synopsis/genre text where available, and completed logo/backdrop presentation using fetched artwork or established approved fallbacks when truly unavailable. Do not display raw filenames, provisional `Unknown` labels, blank hero areas or partial cards in Featured. Distinguish loading/in-flight from confirmed missing data; do not indefinitely block on absent optional artwork or failed requests. Assemble and swap in a coherent card atomically once ready, with no layout jump or focus disruption. This is a Home Featured-specific rule; fixed factual slots elsewhere may still display `Unknown` until populated. The existing focus/navigation and title-zone geometry remain unchanged. Record as future approved corrective requirement, subject to existing fixes-only release authority and physical Shield QA.

## Approved — Featured automatic rotation and priority promotion for newly ready media (8 October 2026)
User clarification following the Mr. Robot physical Shield evidence: the Featured carousel should advance automatically on a defined cadence; **first audit the current code to establish whether autoplay exists and its real interval**, rather than assuming the illustrative 10 seconds. Newly added media must be fully resolved and its Featured presentation assembled **off-screen**, preferably in the immediate next/right carousel slot, without replacing or mutating the currently centred card. When that next/right card becomes ready, trigger an **immediate animated advance to centre**, even if the normal autoplay interval has not elapsed (e.g. at 5 seconds of an illustrative 10-second cadence), then reset the cadence from the completed transition. Keep adjacent left/right card previews and established focus/visual design intact. While a user is actively navigating/interacting with Featured, defer automatic advancement to avoid stealing focus or surprising movement; resume safely after interaction. Debounce/coalesce multiple newly ready titles to avoid repeated rapid transitions; do not move an unready/partially loaded card into view, and do not allow stale async responses to displace a newer selection. This extends the approved Featured readiness gate above; it does not modify the active fixes-only implementation until explicitly scheduled. Verify carousel autoplay behaviour, transition timing, priority insertion, focus and Back restoration on physical Shield.
