# Supernova — Post-0.135 Refinement Register
Date: 2026-10-10
Status: Captured for NEXT refinement release; **not an instruction to implement now**
Source: User's first on-device review of installed 0.135 APK on NVIDIA Shield TV.

## Workflow / authority
- Save each observation promptly so it is available for the next Codex handover.
- Do **not** begin coding, modify the 0.135 branch, merge, rebuild, or launch the next release from this document alone. User will explicitly approve a consolidated handover later.
- Next release is expected to focus on fixes, tweaks and refinements.
- Onboarding problems and detailed custom Library Page review are pending further user feedback; do not infer requirements for them yet.

## 1. Clock typography mismatch — confirmed recurring bug
**Observed:** The top-right clock is visibly a different font and/or size from other top-navigation labels; this has persisted despite earlier reports of fixes. User supplied a photograph of the TV screen showing the mismatch.

**Required:** Clock must use the same font family, weight, visual text size, baseline/alignment as the other navigation labels, while preserving appropriate numeric legibility. Investigate why earlier fixes failed or did not persist, rather than another unverified size-only adjustment. Verify on Shield hardware with screenshots.

## 2. Custom Library Page: remove icon choice — user-approved tweak
**Observed:** Custom Library Page creation offers icons (Library, Movie, TV, Documentary), while other top-navigation pages are text-only.

**Required:** Remove the icon-selection feature from the custom Library Page configuration. Keep text title/name and Next in the existing flow; page appears as text-only in top navigation. Retain the '+' affordance for creating a custom page. Existing saved page names must be preserved; previously saved icon preferences should be safely ignored/migrated without data loss. User will separately review other custom-page functionality later.

## 3. Top-navigation blur abrupt pop-in — confirmed refinement
**Observed:** On TV Shows page, header is crisp at top. When focus enters first library row / page scroll begins, header blur visibly appears abruptly. On leaving and re-entering page, effect resets, then pops in again. User supplied a short video.

**Required:** Preserve the existing approved effect of media moving underneath the blurred header on Home/Movies/TV, but smoothly transition blur/overlay opacity as scrolling/focus changes, without flicker or sudden pop-in on page re-entry. Initial animation guidance: ~200–250 ms ease-in/ease-out, subject to Shield performance and visual QA. Do not reintroduce scroll-dependent blur on Settings, Search or Network & Files, where it was deliberately excluded.

## 4. Navigation focus: accent-colour animated underline — proposed design awaiting final sign-off
**Current:** Rectangular focus box takes the user's accent colour. Left/right navigation remains correctly locked to top navigation (preserve this).

**Proposed:** Replace the focus rectangle with a thin accent-colour underline directly beneath the currently focused label or icon. Underline width tracks focused item's content width, so e.g. Search icon is short and Network & Files is longer. On D-pad left/right transitions, smoothly animate underline position and width (growing/shrinking) without jitter; respect user's selected accent colour. Ensure keyboard/remote focus remains accessible and currently selected page is distinguishable from focused item. **Do not treat as final implementation approval until user confirms final design.**

## Evidence and validation
- Original user photograph of clock and video of header transition were supplied in the planning conversation; these files are **not embedded in this repository document**. Request source media at handover if Codex needs them.
- Test on physical NVIDIA Shield: clock font/size, text-only custom page, smooth header blur in both directions and on re-entry, underline transitions between short and long items, focus navigation retention, and accessibility.
- Do not claim fixes are complete solely from automated test passes; confirm on device.

## Open / not yet specified
- Onboarding observations: user will return with details.
- Additional custom Library Page feedback: user will return with details.
- Additional post-install issues: append as reported.
