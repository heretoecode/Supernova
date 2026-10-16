# Supernova 0.139 — Shield corrective candidate

16 October 2026. Package **app.supernova.player**, version **0.139**, version code **139**; existing signing identity retained. Corrective signing and delivery are pending. The original15October APK is retained as historical evidence in [its original notes](RELEASE_NOTES_ORIGINAL_SIGNED_QA_2026-10-15.md).

## Corrective changes

- Playback launch: initialize the HUD time formatter before the first clock update. The original signed APK resolved and prepared owned local media, then crashed while rendering duration before the formatter existed. The cold-initialization regression reproduces that exception. Native decoding, source resolution and permissions are preserved.
- Diagnostics: reverse entry from About restores the Advanced preference ancestor and hides About's extra workspace. Diagnostic Logging, restored Diagnostic Level and Export Diagnostic Report are reachable with the D-pad. Normal/Detailed (QA) retain the existing preference values; the default remains detailed, bounded logging.
- Settings: nested sections explicitly take ownership of the visible panes so upward/downward entry cannot leave another category's workspace on screen. The right reading pane can receive focus and return to the selected centre control.
- Backup & Restore: restore the Settings rail / vertical Export Backup and Restore Backup list / selected-option description layout. Deliberate archive operations open a separate category-selection dialog. All26 codecs, privacy defaults, selected replacement, explicit confirmation, cancellation/retry and recreation handling are preserved. Left closes restore review before the next Export selection, including compact-screen geometry.
- Approved Details selected/focus and vertical-overflow behavior, existing0.139 features, application identity and signing identity remain unchanged. No main merge or0.140 work.

## Validation and limits

Three production Settings regressions fail against the unchanged original source for their intended reasons. The repaired Settings/Backup suite passes **33 tests**, including five geometry profiles, D-pad routes, production recreation and restore privacy/confirmation. **81 repository Python guards pass.** The additional cold-HUD regression passes, including first initialization and recreation with known durations; targeted total **34 tests**. Full signing conformance remains pending.

The original signed APK's playback crash was reproduced on software Android TV API28/x86 using owned H.264/AAC media. That verifies a real launch defect; the owner's exact Shield attempt has no current log available here. The named Settings recording, screenshot and10October diagnostic ZIP were not available in this context. The old ZIP is not a current crash log.

**Outstanding:** signed corrective runtime verification, physical Shield retest, hardware decoding/output/HDR/passthrough, the full source/format matrix, physical signed upgrade, and remaining whole-workstream acceptance. Automated checks do not close those gates.
