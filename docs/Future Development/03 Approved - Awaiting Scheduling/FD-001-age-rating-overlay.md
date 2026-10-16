# FD-001 — Playback age-classification overlay

**Status: OWNER APPROVED DESIGN — AWAITING SCHEDULING.** **Release: UNASSIGNED.** This is *not* permission to implement or add to 0.140 automatically.

## Owner-approved v1
- Show **only an actual age classification** (e.g. 15, 12A, 18, TV-MA); no content warnings.
- Position at upper-left of **actual playing video**, inside TV-safe inset; not on Preparing Playback screen.
- White classification text, **very fine anti-aliased black outline**, slim red vertical accent; **no background, gradient, box or pill**.
- Fade in after playback actually begins, remain fully visible **10 seconds excluding transitions**, gently fade out completely. Exact fade durations can be calibrated during implementation; no new design scope without owner consent.
- Entirely passive, no focus or D-pad interception.
- **If classification is missing, blank, invalid or unavailable, show NOTHING** — not even the red line. Never substitute audience score, guessed country band, or placeholder.
- Use Supernova's already available classification data **if confirmed by source audit**; do not assume `MovieInfo.getRating()` is an age certificate (it is an audience rating). Any mapping, regional ambiguity or metadata-access problem is an implementation prerequisite to resolve before coding.

## Discussion history and assets
- [Canonical investigation and approval timeline](../../project-decisions/2026-10-13-playback-age-rating-content-advisories-investigation.md) (includes initial content warnings, simplification, visual revisions and final approval).
- User supplied Netflix and Disney+ reference videos in conversation; **not committed to GitHub**.
- Chat mock-ups compared text-only, subtle scrim, thicker outline and thinner outline; final approved direction is **fine black outline with no background**. Generated mock-up files have **not been verified as committed to GitHub**; do not claim images archived.

## Scheduling gate
Only after owner chooses a release: source audit of real age classification and playback timing, isolated implementation, Shield QA for bright/dark/busy scenes, missing ratings, movie/TV distinctions and D-pad non-interference. Approval of visual design alone does not assign a release.

## Latest owner clarification — 13 October 2026 (supersedes age-only restriction)
The owner confirms the intended **approved 0.140 playback-start overlay** comprises (1) the actual existing age classification (e.g. 12, 15, 18) prominently, and (2) **optional smaller title-specific classification descriptors underneath** (e.g. violence, strong language, sexual content) **only when trustworthy matching metadata is actually available**. Missing descriptors must not suppress a valid age classification. If the age classification itself is missing/invalid, render no overlay. The owner considers the overlay's appearance, placement, fade-in, ten-second full-opacity dwell, fade-out and passive remote behaviour **already agreed**, with no new design discussion required. The previously documented age-only restriction is superseded for this conditional descriptor scope. The current GitHub record does not establish a source-verified, populated TMDb descriptor feed or exact Supernova provider mapping: Codex must verify lawful source/coverage, regional matching, cache and missing-data behaviour during implementation; never invent warnings from age band, genre or AI inference. Preserve active 0.139; this feature is owner-scheduled for the next release (expected 0.140).
