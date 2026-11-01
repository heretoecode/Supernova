# Supernova 0.149 — physical Shield QA findings (1 November 2026)

**Source:** owner testing the delivered signed 0.149 APK on Nvidia Shield, reported in conversation. These are owner-observed failures, not independently reproduced in this record.

## Confirmed owner reports
1. **Library statistics missing.** Despite fix #1 being reported as implemented and automated regression passing, the owner still cannot see the statistics. **Acceptance:** actual statistics visible in the intended library screen on Shield, with correct focus/scroll behaviour. Investigate runtime rendering, data availability and layout; do not merely update tests.
2. **Language flags missing.** Despite fix #5 retention/binding tests passing and packaging verification, flags are still not visible on the Shield. **Acceptance:** correct supported language flags visibly render on device where expected, with a documented neutral fallback for unknown language/region. Investigate installed resource shrinking, drawable binding and actual on-device rendering.
3. **Reception incorrectly displays reviews.** Owner explicitly rejects reviews: **Reception must display ratings only**, with appropriate unavailable state when rating data is absent. Remove reviews and review navigation/actions from this area; do not infer that previous automated tests reflect the intended product requirement.

## Next development instructions
- Treat these as **open 0.149 Shield QA defects**, not verified fixes.
- Diagnose root causes and validate with real/device-equivalent runtime evidence and screenshots. Extend regression tests to assert the approved user-visible behaviour.
- Preserve other completed 0.149 features and Subtitles design. Do not assume unreported items pass Shield QA.
- Do not merge PR #30 or start an APK/release cycle until owner authorises the next delivery.
- Update this record with investigation, exact commit, tests and any remaining device verification.

**Status:** Reported and recorded; investigation/implementation pending.


## Root-cause investigation required (owner follow-up)

**Important QA coverage gap confirmed from 0.149 documentation:** `docs/0.149/SHIELD_QA.md` instructs statistics checks on **Movies/TV only**, omitting the owner's failing **custom library page**. The user confirms three library pages: Movies, TV and custom; all are intended to share underlying library code. Compare page construction, shared statistics component wiring, data sources, visibility predicates, focus and scroll container and custom-page-specific branches. Prefer reusing the proven Movies/TV implementation over duplicating logic. Add an explicit custom-library runtime/UI acceptance test alongside Movies and TV. Do not mark fixed without a visible on-device screenshot or owner confirmation.

**Flag evidence gap:** packaged `region_gb`/`region_us` resources and unit tests do not establish on-screen display. `PreviewLanguageIcon` reportedly requires explicit regional metadata; trace actual Audio/Subtitle track language values (e.g. generic `en` vs `en-GB`), normalisation, view binding, fallback policy, resource shrink and visibility on a real Shield. Preserve truthful locale semantics: do not assign arbitrary national flags to generic languages; use the approved neutral icon when country is unknown. Test real metadata examples and capture the resulting UI.

**Reception acceptance correction:** `docs/0.149/SHIELD_QA.md` still mentions 'supported reviews even without ratings'; this is superseded by the owner's **ratings-only, no reviews** decision. Update all QA criteria, tests and UI documentation consistently when implementing.

**Diagnosis status:** These are evidenced testing gaps and plausible causes, **not confirmed root causes** until source tracing and runtime verification. Keep all three defects open.
