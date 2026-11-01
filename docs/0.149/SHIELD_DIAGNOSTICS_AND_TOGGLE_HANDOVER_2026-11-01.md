# 0.149 — Shield diagnostics and toggle correction handover (1 November 2026)

## Source and handling
Owner provided local archive `Supernova Diagnostics - 2026-11-01 21-02-06-018.zip` and video `IMG_5088.mp4`. **These private diagnostic files have not been uploaded to this repository**; ask owner for a secure Codex-accessible attachment if full raw logs are required. This document is a safe summary, not a claim that Codex has the original ZIP.

Archive build-device: `app.supernova.player` 0.149 (149), source `cb28b1b936891374bc801f0b58b3397926fc3086`, NVIDIA SHIELD Android TV, Android 11, SDK 30. Archive coverage says PARTIAL; dropped=0, write errors=0. README explicitly warns that unclean exits do not prove crashes.

## Extracted diagnostic evidence
Summary event counts: `focus_anomaly_suspected=864`, `scan_failed=166`, `scan_batch_failed=8`, `metadata_network_failed=671`, `metadata_package_failed=672`, `metadata_semantic_failed=12`, `player_error=1`, `uncaught_exception=1`, `playback_attempt=714`, `playback_begin=46`, `playback_end=37`, `ui_rebuild=185`, `artwork_operation_begin=936`, `artwork_ready=872`. Summary reports launches retained 131, clean shutdowns 2, suspected unclean exits 101. These are event counts, **not independently confirmed distinct incidents**. Do not interpret unclean markers as crashes without matching exit evidence. No artwork failures were reported. Prioritise root-cause clustering by session/process/operation, trace exception and player-error context, separate network/service failures from app defects, and distinguish expected focus changes from genuine focus loss.

## Owner-reported toggle regression
The toggle currently looks like a pill with a thumb, but the ON state turns the entire pill white and redundant On/Off text appears beside it. The owner expects a conventional visual switch with an obvious state and no separate On/Off text:
- OFF: dark/muted track, circular thumb at LEFT.
- ON: visibly contrasting enabled track, circular thumb at RIGHT.
- Focus: a separate focus ring/glow; must not masquerade as ON.
- Disabled/unavailable: visually distinct, noninteractive, accessible state.
- Preserve the setting label and actual persisted boolean; remove redundant adjacent `On`/`Off` status text for ordinary binary toggles.
- Apply consistently to Settings and approved Subtitles surfaces without redesigning unrelated controls. TV remote OK must toggle exactly once, state must persist, focus must remain visible.
- Investigate whether white-fill derives from focused-state drawable, checked-state tint, theme selector or a custom view; do not guess.

## Other urgent unresolved owner reports
See `SHIELD_QA_FINDINGS_2026-11-01.md`: custom-library statistics initially invisible until scroll down/up (Movies and TV work: do NOT alter those pages); missing language flags despite packaged resources; Reception must show **ratings only, no reviews**.

## Required Codex work and quality gate
1. Obtain and inspect the **full original diagnostic archive** through a private attachment if available. Investigate every distinct actionable class, identify root cause, severity, evidence and disposition; avoid treating every count as separate defect.
2. Trace and fix confirmed app faults and toggle visuals/behaviour; preserve working Movies/TV and other approved designs. Record exact changed files and tests.
3. Test first-load custom stats without scrolling, actual rendered language flags with real metadata, toggle checked/focused/disabled states with remote input and persistence, and ratings-only Reception. Tests of resources or isolated state logic alone are insufficient.
4. Supply genuine runtime visual evidence where possible. If no emulator/Shield available, say explicitly **implemented, awaiting Shield verification**. Never label user-observed failures fixed solely on JVM tests.
5. Keep PR #30 draft/unmerged. No new APK or production release until owner approval; don't upload raw logs or private paths to public GitHub. Document confirmed issues separately from uncertain signals.
