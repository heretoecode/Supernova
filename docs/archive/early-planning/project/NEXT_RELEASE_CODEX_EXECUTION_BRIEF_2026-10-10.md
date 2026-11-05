# Supernova — Next refinement release: Codex execution brief
Date: 2026-10-10
Status: **USER AUTHORIZED START OF IMPLEMENTATION** for approved post-0.135 scope only. User will review Details and Playback/HUD separately for a **later release**.

## Authority and baseline
- Repository: `heretoecode/Supernova`.
- User's installed reference: **Supernova 0.135 QA** on NVIDIA SHIELD TV.
- Authoritative complete requirements: [post-0.135 register](SUPERNOVA_POST_0.135_REFINEMENT_REGISTER_2026-10-10.md), items **#1–#44**, including later clarifications superseding earlier tentative wording.
- Authoritative shared three-panel layout: [shared framework](../design/shared-three-panel-framework/SHARED_FRAMEWORK_BROWSER_0.135.md).
- **Before modifying anything:** inspect GitHub repository state, default branch, the `codex/supernova-0.135` branch, signed 0.135 provenance/build documentation, current CI, release/signing instructions and all linked project/design docs. Verify exact commit and working baseline. Do not assume that `main` or an old preview branch is the correct implementation base; resolve conflicts using repository evidence and report a real blocker if necessary.
- Create/use an appropriately named **new next-release working branch** from the verified 0.135 code baseline. Do not overwrite historical branches, reset prior work, merge to main, alter app ID, replace signing keys or discard user data.
- Preserve existing design/behaviour unless explicitly changed by the approved register. Treat text requirements as authoritative; visual references may not exist as committed binary assets. If an exact image is essential and unavailable, flag it rather than inventing approval.
- Version number is not yet user-approved: choose a provisional internal working identifier if required, and do not claim final release/version approval.

## In-scope implementation
Implement **all approved next-release changes** in the register, using latest decision per subject. Coverage includes:
1. Home and hero carousel: correct TV series/next-episode prioritisation, watch history and backup controls, one-line official tagline or synopsis fallback, gradient/contrast/action fixes, Continue Watching lifecycle.
2. Header/navigation: consistent clock typography, smooth blur transitions, animated accent underline, stable D-pad focus.
3. Movies, TV Shows and Custom Library Page: shared layout, list controls, focus/scroll behaviour, genre controls, first-use landing, five-step creation/edit workflow, unsaved-change handling.
4. Search and global keyboard: non-three-panel Search layout, unified remote-friendly keyboard and predictable focus.
5. Network & Files: shared browser layout, correct left/centre/right D-pad focus, no header overlap, file controls, navigation and scoped source features. Detailed Overview QA remains deferred; fix approved existing issues without inventing new redesign.
6. Settings: responsive three-panel visual foundation, native inline controls, clear information pane, My Providers ordering, navigation rules, and **Open-source Licences focus-triggered freeze/crash**.
7. First-run onboarding: Build Your Library introduction, flat local drive navigation, folder checkbox selection, staged selections across multiple drives, explicit Save/Finish gating; **no scanning or onboarding dismissal before confirmation**.
8. Enhanced diagnostics (#40–#43): **QA/development logging ON by default**, future public builds detailed logging OFF by default and opt-in; persistent crash breadcrumbs, exception/ANR/unclean-exit evidence, bounded low-overhead long-duration performance/memory trends, suspected D-pad/focus/layout anomaly instrumentation, safe local export/privacy protections. The earlier empty ZIP was due to cleared app data; the second ZIP captured 353 events and a focus-to-unclean-exit sequence but no stack trace. Do not falsely diagnose memory leaks from one sample.
9. Shared three-panel foundation (#39): navigation separators/chevrons, centre row dividers and appropriate checkbox/switch/choice controls, right Information heading/divider and labelled structured data, adaptive widths and merged overview panes.

## Explicit exclusions — later release
- **Item Details page** redesign, feature additions and separate page walkthrough.
- **Playback screen and Playback HUD** redesign, feature additions and separate walkthrough.
- Do **not** implement prior unapproved ideas for either screen, even if they appear in historical documents. Only touch code shared with these screens when strictly necessary for approved cross-app fixes (e.g. logging, global keyboard, common navigation) and protect existing behaviour with regression tests. Surface any unavoidable conflict.
- No unrelated feature expansion, branding/package/signing migration, speculative AI features or main-branch merge.

## Delivery and validation contract
1. Start with a **read-only preflight** mapping each approved requirement to actual implementation, dependency, acceptance criteria and tests; identify conflicts, missing source assets, external dependencies (e.g. put.io OAuth credentials), and items already implemented. Record the map on the working branch. Continue without routine confirmations; ask only for a genuine blocker or a decision that cannot safely be inferred.
2. Implement in manageable commits with explicit requirement IDs and traceability. Do not claim a requirement fixed merely because code was edited.
3. Validate compilation, static checks, unit/integration tests, real navigation/back/focus cases, first-run data-cleared onboarding, library scan gating, persistence, upgrades from installed 0.135, diagnostics enabled by default on QA, Open-source Licences crash path, and performance regression risk. Mark physical SHIELD validation as **pending user QA** unless actually performed on device.
4. Preserve original application identity/signing compatibility for upgrade installations; never invent or expose signing secrets. If signed APK cannot be produced, report exact blocker rather than replacing signing key.
5. Provide an installable QA APK if build/signing infrastructure permits, precise commit SHA, artifact location, concise change log, completed/pending requirements matrix, tests/results, known issues, and any items requiring user verification. Do not merge to main without explicit approval.
6. Update GitHub documentation and progress notes as work proceeds; keep parked Details/HUD work visibly out of this release.

## Workflow authorization
The user has now explicitly requested Codex to **start work on all previously agreed next-version changes** while the user discusses Details and Playback/HUD for a later version. This document is the implementation authorization; the user-facing prompt will point Codex here.
