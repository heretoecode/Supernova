# Supernova 0.137 Shield QA — Home Continue Watching and Library Health banner
**Recorded:** 2026-10-11
**Status:** Observed on physical NVIDIA Shield; next handover candidate, NOT implementation authorisation
**Evidence:** User-provided IMG_4866.jpeg (in conversation), plus user's verbal reproduction.
**Release:** Signed 0.137 QA, draft PR #12.

## Observations
1. After starting a TV episode on 0.137, Supernova displays a resume status for it, demonstrating that some playback progress is recognised. Yet **no Continue Watching row appears on Home**.
2. The erroneous Continue Watching row previously shown on the TV Shows page **has been removed** as intended.
3. Home displays a full-width, blue, non-focusable/non-clickable banner beneath the hero and above Recently Added: **“Library files need attention · Review Library Health”**. The banner consumes significant space and does not provide a usable action.

## User-approved requested outcome (for future handover)
- **Completely remove the Library Health warning/banner/alert from Home**, including the reserved Home row/space. Do not interpret this as removing Library Health functionality elsewhere.
- Restore correct **Home Continue Watching** for eligible in-progress episodes; verify updates without restart, deduplication versus Recently Added, and Show/Hidden preference.
- Preserve removal of Continue Watching from TV Shows and preserve working hero/Recently Added layouts.
- Investigate whether the Library Health banner interferes with row selection/order/layout. **Hypothesis only**: proximity to the missing row is not proof of causation. Also trace membership/visibility rules, persisted resume data, data-to-UI binding, refresh events and conditional rendering. Don't claim removal alone fixes it without tests.

## Acceptance criteria
- No Library Health banner, alert, placeholder or blank reserved strip on Home.
- In-progress TV episode with eligible resume point appears in Home Continue Watching promptly without restarting.
- Row appears in correct place and responds to Show/Hidden preference; no duplicate entry in Recently Added when membership rules require removal.
- No unintended Continue Watching row on TV Shows, Movies or custom pages.
- Test with actual Shield playback and user library; report evidence, not just automated passing tests.

## Parked reminder before next handover — NOT a scheduled alert
User wants to **revisit first-run onboarding / welcome Home** before the next Codex handover. Current impression is positive: hero cards and Recently Added row look good. Discussion remains open; don't silently treat as approved design or schedule a notification.

## Process
Continue collecting 0.137 QA observations; do not launch Codex or modify active PR #12 solely from this note. Revisit onboarding during the next handover preparation.

## Follow-up clarification — restart regression and investigation sequence
User confirms that on **0.136** the Continue Watching row eventually reappeared after fully closing and restarting the app. On **0.137**, multiple full restarts **do not** restore the row, even though the media has a resume state. User recalls that the wide Library Health banner and disappearance of Continue Watching arrived in the same release; this is their recollection, not independently verified commit history.

**User's explicit priority:** remove the Library Health banner completely from Home **first**, including any layout slot/conditional row, then assess whether Continue Watching returns. Codex may separately inspect the banner's relationship to row composition and the Continue Watching data/visibility pipeline. Do not pre-emptively attribute causality or bundle a speculative unrelated redesign. Compare behaviour with and without the banner, and preserve evidence of any remaining failure. Do not wait for a full Home redesign to remove it.

## Planning decision — next-build Priority 1 scope (2026-10-11)
User explicitly consolidated the previously proposed three Priority 1 items into **one and only one functional change**: **remove the Library Health banner/alert completely from Home, including any reserved layout space**. Do not separately list or authorise a Continue Watching fix/investigation as part of Priority 1 at this stage. User believes banner occupies the Continue Watching area and may be responsible; this is an unverified hypothesis. After banner removal, observe whether Continue Watching returns; only discuss a separate investigation if it remains missing. Preserve Library Health elsewhere. This decision supersedes the earlier broader proposed priority list in this note; no Codex implementation authorised.
