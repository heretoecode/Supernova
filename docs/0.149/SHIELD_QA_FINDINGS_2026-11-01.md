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
