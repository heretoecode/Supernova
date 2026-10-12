# Continue Watching next-episode verification and current-season subtitle preparation — 2026-10-12

**Status:** User-requested behaviour and proposed policy; not verified in current code/build. Documentation only; no code implementation authorised.

## Continue Watching: verify existing behaviour

User believes Supernova's Continue Watching row can show the next unwatched episode after the previous episode was completed, even though the next episode has never been started. Example: after finishing S1E1, S1E2 should be eligible to appear. Preserve this **resume-or-continue-series** behaviour if present; investigate if absent. Verify both actual 0.137 source and on-device behaviour before reporting confirmed. A quick default-branch GitHub keyword search did not establish the behaviour; this is **not** proof of absence.

This is distinct from the previously noted 0.137 QA concern about Home Continue Watching being missing in some circumstances; do not silently expand the P1 release scope or assume a separate fix was approved.

## Smart subtitle preparation: current season, not whole series

User proposes that when a person begins watching a TV show/episode, Supernova should prepare subtitles in their chosen language for the **remaining episodes of the current season**, not every season of the series. If watching S1E1, do not search S5 merely because it exists in the library.

Suggested triggers and priorities, subject to current provider/API audit:
- At playback of S1E1: ensure current episode is ready; enqueue other *available library episodes of Season 1* for low-priority background search/download according to settings.
- On Continue Watching next-episode eligibility: prioritise the next episode (e.g. S1E2), even if unstarted.
- Progress through a season: deduplicate searches, respect persistent cache and negative-result TTL, and skip tracks already available in the preferred language.
- At season boundary: prioritise next-season first episode when eligible; only start broader next-season preparation when playback of that season begins.
- Stop/pause low-priority work if viewing intent changes; explicit playback and manual subtitle requests override speculative background queue.
- Avoid an immediate season-wide API burst. Respect actual provider quota, HTTP 429 / Retry-After, rate limiting, request budgets and reimport-proof persistence. A season can contain many episodes, and the user repeatedly reimports a library with 229 shows while testing onboarding.

Distinguish embedded/sidecar subtitle detection from online provider searches and downloads; never assume an unsearched episode has no subtitles or that generic subtitles are CC/SDH. Preferred language and accessibility flags must be handled accurately.

## Verification before implementation

Audit current Continue Watching eligibility, episode completion thresholds, next-episode selection, Home row population, and subtitle provider capabilities/quotas/cache before drafting any implementation. Test completed episode → unstarted next episode, season boundary, missing episodes, multi-season library and reimports.

## Scope safeguards

This is an agreed product direction and explicit verification request, **not** authorisation to change the active development branch or launch a Codex release handover. Continue to remind the user to raise their separate subtitle-related point when returning to the playback HUD.
