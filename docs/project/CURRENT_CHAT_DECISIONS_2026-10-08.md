# Current chat decisions

Continue Watching long-press: rename Dismiss to Remove from Continue Watching Row, preserving playback position. Add Restart Episode / Restart Movie, launching playback at 00:00. This supersedes the previously proposed Reset Progress long-press action.

Details > More also offers Restart Episode / Restart Movie.

Featured requires genuine high-quality backdrop, accessible matched media, and synopsis fallback episode > series/movie > tagline. Partially watched media uses Resume action.

These are planning decisions, not instructions to modify the fixes-only branch.

## Foundation Release
Approved identity/maintenance-only release: unique package ID, new signing identity, new Supernova TV banner, square icon, splash, app name/About, correct release numbering and upstream attribution. Clean-install and core-function checks; no broad NOVA cleanup or new features. Planning/development may run independently alongside Shield fixes work. Fresh install, empty library and defaults; no migration from Preview required. Aim for coexistence with NOVA and current Supernova Preview. Signing key reportedly created and privately backed up, not independently verified; never place it in repository. Sequential pre-1.0 labels 0.1 through 0.9, then 0.10, 0.11 and so on; 1.0 requires user approval.

TV banner: minimalist charcoal/near-black with white SUPERNOVA typography and ample negative space, no obligatory astronomy/explosion. Android TV banner target 320 x 180, 16:9. Wordmark-only versus small geometric symbol is undecided. No final banner approved.

## Backup and diagnostics clarifications
Backup **format 1.0** (not app version 1.0) is compatibility baseline; future versions restore earlier supported formats starting at 1.0, without obligation to support NOVA/pre-format-1.0 archives. Full ZIP with RESTORE_INSTRUCTIONS.txt, complete supported app data, safe validation/staging/rollback, no authentication secrets in archive, reauthentication after restore, background artwork refresh and cautious USB source rematching.

Existing Diagnostic Logging toggle/export retained. OFF means no optional diagnostic collection or sampling; ON enables all categories including memory/leaks, UI/focus/performance, crashes, playback, indexing and background work. Minimal overhead and redact secrets. OFF stops collection immediately. Whether older logs remain exportable is unresolved; possible removal at app 1.0 is only for later review.

## Observed and unresolved
User verified with Marshall S1E2 that existing Dismiss hides the Continue Watching card while Resume and episode progress remain. Rename action to **Remove from Continue Watching Row** and ensure full menu text is displayed, without truncation. Restart starts immediately; do not clear progress merely by opening the action. Whether a dismissed item returns after new playback remains open. Other optional hero fields need not all be present, but no backdrop or no meaningful description excludes from Featured. Do not substitute episode stills or posters.
