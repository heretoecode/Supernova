# 0.148 Shield QA — Settings toggle state visibility (REOPEN)

**Owner evidence:** 12.13-second video `IMG_5076.mp4`, recorded from Nvidia Shield running installed Supernova 0.148, visually reviewed. Frame at ~3 seconds shows **Audio output & tracks** settings, including **Platform spatialization** and **Prefer original audio track**. Video is attached to the conversation, **not verified in GitHub**.

## Confirmed continuing issue
Toggle on/off state remains difficult to distinguish at normal TV viewing distance. Pale thumb and similarly blue-toned tracks, combined with blue focus highlight, obscure state distinction. This is a previously reported defect still present in 0.148; **reopen** rather than mark as fixed. The captured screen includes a setting unavailable for the current device/account configuration; don't conflate unavailable with off.

## Owner-approved requirements
- **On:** visually distinct active track, thumb clearly at right.
- **Off:** neutral grey/inactive track, thumb clearly at left.
- **Unavailable/disabled:** clearly unavailable, distinct from active/on and normal off, without suggesting it is actionable.
- **Focused:** preserve existing blue focus outline without altering or obscuring toggle state.
- Apply to **shared toggle component throughout Settings**, not just the two example controls; preserve actual setting values and behaviour, accessibility, D-pad interaction and focus restoration.
- Ensure distinguishability on physical Nvidia Shield at normal viewing distance, including focused/unfocused and available/unavailable combinations; add component/state visual and interaction regression tests. Avoid relying on colour alone where feasible; verify thumb positions and contrast.

## Acceptance
Before closing, provide implementation/test evidence and request owner physical retest on a new signed QA build. Keep PR29 draft and main unchanged. This report is documentation only; it does not imply code changes or a verified fix.
