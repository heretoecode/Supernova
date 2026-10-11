# NEXT CODEX HANDOVER — First-run Home and responsive UI (STAGED, NOT ACTIVE)

**Prepared:** 2026-10-11  
**Status:** READY FOR FUTURE HANDOVER; **DO NOT IMPLEMENT UNTIL USER EXPLICITLY STARTS NEXT VERSION**.

## Read first — authoritative design requirements
1. [Global responsive layout standard](SUPERNOVA_GLOBAL_RESPONSIVE_LAYOUT_STANDARD_2026-10-11.md): shared bounded viewport-relative layout tokens; TV safe area; component contracts for Home, navigation, grids, Details, Network & Files, Settings, dialogs and playback HUD; 720p/1080p/4K conformance.
2. [Global dialog design standard](SUPERNOVA_GLOBAL_DIALOG_DESIGN_STANDARD_2026-10-11.md): user-approved welcome composition, typography, spacing, icon, colours, backdrop, button and focus behaviour, responsive sizing. Numeric ratios are **provisional** until actual Shield screenshot calibration.
3. [First-run Home placeholder behaviour](SUPERNOVA_FIRST_RUN_HOME_PLACEHOLDER_BEHAVIOUR_2026-10-11.md): realistic dark-blue hero and Recently Added placeholders, neutral skeleton lines, shared info dialog, safe no-playback/no-scan behaviour, progressive replacement by real media.

## Approved next-version scope to reconcile at handover
- Replace the compressed first-run Home folder-browser onboarding with the **real Home screen layout populated with realistic placeholders**. Show first-run centred welcome modal with actual Supernova icon, primary **Network & Files**, secondary **Explore Supernova**. First-launch dismissal persists; clearing app data resets it.
- Placeholder focus/animations/layout must match real Home cards. Hero **More Info** and selecting Recently Added placeholders open a shared explanatory modal with Network & Files shortcut; never trigger playback, fake media details or scanning.
- **No scan until user confirms chosen folders** in Network & Files; user verified this was working in installed 0.136 QA. Opening Network & Files must not initiate scanning. Home remains populated with placeholders while genuine media progressively arrives.
- Conduct **global responsive layout audit/correction** against approved existing Supernova visuals. Centralise sizing tokens; preserve visual hierarchy and proportional spacing across logical viewports and font/display scaling. No broad redesign.
- Conduct **separate shared Network & Files D-pad/focus audit and correction**: focus movement, focus return, scrolling, flashing and clipping. User reports related issues in onboarding browser and Network & Files. Responsive fixes **do not count as** fixing focus logic or animations.
- **Do not fabricate a Continue Watching row** when playback history is empty.
- Verify on Shield with actual before/after screenshots, focus traces, scanning invariants and physical QA. Do not mark untested fixes as done.

## Context and handover discipline
- Installed 0.136 QA: user confirms scan no longer auto-starts before folder confirmation, but current onboarding layout is compressed, and Home goes blank after scan initiation until media arrives.
- User approved the welcome concept's **layout and typographic hierarchy** and the **real Supernova app icon**; preserve that reference. Avoid treating concept artwork as exact pixel measurements.
- Next release/version number, priority ordering, scope freeze and implementation branch are **not yet authorised**. At handover, inspect actual current repo/branch, reconcile with existing QA issues and other planned release items, avoid duplicate work and identify conflicts.
- **Do not start coding, build an APK, merge branches, change release automation or disturb Codex's active work based on this document alone.**
- Documentation lives on branch `docs/nova-upstream-deep-audit-2026-10-11`, not necessarily on main or Codex's active branch. **Explicitly pass this link and its three source documents to Codex** when handover is authorised; verify Codex can access the branch.

## Explicitly parked / separate
- Google Drive/GitHub Releases publisher OAuth setup remains paused at Step 3, see [publisher setup handover](PARKED_GOOGLE_DRIVE_RELEASE_PUBLISHER_SETUP_2026-10-11.md).
- Selective NOVA upstream 6.4.64→6.5.5 audit and future implementation are separate and parked.
- Other parked feature ideas remain parked; this next-version brief is not authorisation to activate them.

## Handover checklist
1. Verify active source baseline, installed build and open Codex PRs.
2. Read this index plus all three linked specifications and existing QA/refinement register.
3. Reconcile issues and prepare exact version scope with user approval.
4. Only then authorise Codex implementation on the correct branch.
5. Require design conformance and Shield validation; don't close defects based on code-only claims.
