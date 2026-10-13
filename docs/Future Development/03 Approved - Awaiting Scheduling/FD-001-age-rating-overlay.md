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
