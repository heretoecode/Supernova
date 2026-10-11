# Supernova 0.137 Shield QA — Hero focus, Home rows, custom mixed content

Status: user-reported findings for proposed next build; documentation only, not implementation authorisation. Preserve established 0.137 hero animation improvements.

## Hero focus outline
- Existing interaction: focus lands on hero More Info and Select opens that media item's details. **Preserve exactly**.
- Add an additional visible focus outline around the **whole hero card** when the hero More Info/card area is focused.
- Match the existing app-wide white/user-selected accent colour focus outline style used for media items. This is visual focus feedback, **not a second focusable target**.
- Check clipping, animation transitions, inactive hero cards, and D-pad navigation.

## Home row artwork appearance
User observes apparent fade/washed-out effect on Home row poster artwork. Desired: no unintended fade, washed-out overlay or reduced opacity; retain normal focus indications and crisp image loading. Visual evidence still to be requested and examined before finalising precise reproduction/diagnosis.

## Home row item caps
Limit each of the following Home rows to **15 displayed items**:
- Trending on Trakt
- Popular on Trakt
- Recently Added
- Because You Watched

Retain each row's existing eligibility, sorting and other behaviour; limit should apply to final visible items. User motivated by unwieldy row lengths and performance; do not assert a measured performance improvement.

## Trakt row wording
Replace awkward “Trending on Trakt - in your library” and corresponding Popular variant with more natural:
- “Trending on Trakt and in your library”
- “Popular on Trakt and in your library”
Verify whether text is a row title or description in current implementation; adjust the displayed copy without unintended change to eligibility/meaning.

## Customise Home — custom row with Movies and TV Shows both enabled
User created a rule-based custom Home row, max items = **30** for diagnosis:
- Movies ON, TV Shows ON: only TV Shows visible, zero Movies.
- Movies OFF, TV Shows ON: only TV Shows, unchanged.
- Movies ON, TV Shows OFF: relevant Movies populate.
- Movies ON, TV Shows ON again: TV Shows reappear, Movies disappear.
Expected: combined eligible Movies **and** TV Shows in one row when both toggles enabled, subject to rules, existing sort order and cap. Do not force an arbitrary ratio or alter individual toggle semantics. Investigate result composition, ordering, filtering, limit application and data refresh; distinguish observed symptom from root-cause hypothesis. Validate both-only, movie-only and TV-only combinations, and content availability.

## Evidence and validation protocol
Request user screenshots/photos for hero focus outline and poster fade; short video if fade changes with focus/scroll or if custom row behaviour is easier to show in action. User's original captures stay out of GitHub/Codex handovers unless expressly authorised. Written issue reports should specify steps, actual vs expected behaviour, preservation constraints, and physical Shield validation needs.

## Follow-up: supplied visual evidence and global Home row policy
User supplied two original QA photographs in conversation (not to be shared with Codex or added to GitHub without explicit consent): first shows Father Ted hero with focused More Info but no full-card outline; second shows Watch Next, Trending on Trakt and Popular on Trakt artwork, with user reporting a fade/washed-out look. The photograph alone cannot establish a software opacity effect; audit unintended dimming/overlays while preserving legible text and focus.

**Updated global rule (supersedes earlier four-row cap list):** Supernova-managed, automatically populated Home rows should display a maximum of **15** items, including **Continue Watching**, **Recently Added**, **Trending on Trakt**, **Popular on Trakt**, and **Because You Watched**. This is a displayed-item cap; apply efficiently when feasible without promising unmeasured performance gains. **Watch Next is manually managed by the user: do not impose the 15-item cap.** Custom user-created rows keep their user-configured maximum (e.g. 30), not a forced 15. For future automatic rows, use 15 as the default unless explicitly designed otherwise.

**Copy latitude:** user authorises natural, friendly naming of the existing Home rows, including leaving clear names unchanged. Proposed copy: Continue Watching; Watch Next; Recently Added; Trending on Trakt · In Your Library; Popular on Trakt · In Your Library; Because You Watched. The centred-dot qualifier conveys that Trakt-ranked items are filtered to titles present in the user's library. Confirm row semantics in code before changing labels. The earlier suggested “and in your library” wording is not mandatory.

No original user images/videos to Codex/GitHub. No implementation authorised.
