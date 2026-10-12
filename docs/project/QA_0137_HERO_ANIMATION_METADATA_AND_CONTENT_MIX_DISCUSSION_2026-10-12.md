# 0.137 Shield QA — hero animation, metadata and content mix (discussion, 2026-10-12)

**Status:** User observations and design exploration. **Not an approved implementation handover**. Preserve current 0.137 branch and existing 0.138 scope. Do not implement until user signs off.

## Hero carousel animation (previous user video)
- User supplied a 5.6-second Shield video of D-pad traversal of Home hero cards. User describes motion as already smooth but capable of polish.
- Treat as **visual enhancement candidate**, not proven frame-drop/animation defect. Investigate actual animator/interpolator/duration, card movement, artwork loading, focus and rapid repeated D-pad behaviour before specifying a change.
- Candidate improvements: natural ease-out/settle, responsive navigation under repeated input, coordinated artwork/metadata changes, no unwanted scale/bounce/glow or image flashes.
- Preserve current hero layout, More Info focus/action and existing behaviours.
- Large Review Library Health Home button visible in video: **already separately documented for removal from Home including reserved space**, not a new hero animation issue.
- User-supplied video is **not authorised for upload to GitHub or Codex**; no raw media attached here.

## New screenshots / metadata observations
User supplied six photographs of actual hero cards (Daredevil: Born Again, High Potential, Joe Pickett, American Horror Story, Slow Horses, The O.C.). Screens show prominent artwork/title logo, a small line currently containing year + genres + season count, tagline, and More Info. On bright backdrops the smaller light metadata and tagline can be difficult to read.

User identifies the line as the **hero metadata row**. User wants it standardised across titles and **noticeably larger**, suggesting potentially double current apparent size (final sp/dp size to be verified in code and on Shield). Consistent typography, spacing and readable contrast should be evaluated against the shared design system. Preserve title artwork/logo and overall hero concept.

### Proposed content order — not yet final
User initially mentioned year → genres → seasons/runtime, then expressly revised this:
1. Year.
2. Genres.
3. **Age classification** (use verified, territory-appropriate source; distinguish from rating score).
4. **Review score** (show provider/source and consistent scale, only if actually available and trustworthy).
5. Optionally a short line of **media capability indicators**, such as HD / 4K UHD, 5.1, subtitles/CC, audio description (AD), Dolby Atmos, if reliably known for **the actual locally playable file**. Avoid inventing capabilities or inferring from external streaming services or source artwork.

**Do not include season count or runtime on the hero** in the user's latest preference; those can be found on details pages. Do not carry the earlier season/runtime arrangement into implementation.

Reference comparisons given by user: Disney+ shows HD, 5.1, CC, AD; Netflix shows 4K, 5.1, AD and subtitle icon; Prime Video shows AD, subtitle, UHD and Dolby Atmos, plus an unidentified lightning-symbol badge. The unidentified icon should **not** be assigned an invented meaning or included by default.

Design candidates: compact first metadata row with year/genres/age/review; optional distinct restrained second row of media badges if reliable metadata exists; subdued consistent left-side text scrim/gradient for contrast; avoid heavy boxes, excessive clutter or unverified data. Example scores/classifications/badges are **placeholders only**, not verified title facts. Consider local age-rating scheme and missing data, variable genre length, very bright artwork, logos, and both movies and TV.

## Potential hero content-selection issue — investigate
User notices **all hero cards currently encountered are TV shows**, despite movies in library. This could be related to addition order or population/selection logic, but is **not established**. Read-only Codex investigation should establish:
- Hero eligibility rules for Movies and TV Shows; ordering, limits, caching, refresh triggers, persistent hero lists.
- Whether initial library import order (TV added before Movies) affects selection or whether new movies are excluded until rebuild/refresh.
- Any filter, grouping, duplicate suppression, source preference, fallback or timing issue.
- Whether observed six TV examples are representative of the whole hero dataset (cannot conclude from six screenshots).
- Verify with tests/logs where possible; report UNKNOWN rather than infer.
Do not force a 50/50 movie/show split unless explicitly approved. Goal is correct inclusion and selection according to intended rules.

## Follow-up / approval gates
- Ask user to decide precise metadata priority, whether ratings and technical badges should be shown, and preferred badge aesthetics after reviewing options.
- Investigate metadata availability and reliability (age rating, review score, stream/file codecs, audio channels, subtitles, AD) before promising display.
- Consider separate read-only Codex hero audit when authorised; **do not conflate with the currently running Customise Home audit**.
- If approved, integrate into future consolidated QA/release handover through GitHub. No automatic implementation.
