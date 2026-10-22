# Supernova 0.141 development candidate

Implementation and exact-source software/binary conformance complete for `4f0937f1`. No signed APK or Drive APK delivery: protected signing access is blocked (HTTP 403). Physical Shield acceptance remains pending.

- Cinematic Playback HUD: far-left transport, slim seek line with focused/active target tooltip, remaining/finish on right and reliable embedded chapter markers when available.
- One right translucent Settings window with exact horizontal **Subtitles | Audio | Display | Playback** tabs. Native track/timing/appearance/output/play-mode handlers retained; subtitle language/preparation defaults embedded. Playback technical/report entries removed while diagnostics remain. Refresh preserves focus even when tracks have identical labels.
- Corrected unavailable Optimized format/PiP fall-through, appearance tap quantization, palette reopening, appearance close persistence and right double-tap capability. Downloaded subtitles use the existing explicit apply route; replacing existing destinations asks first. Neutral global preset sentinels retain their inherited semantics.
- Hero rapid-reversal correction and prompt selection; Library row centring, Statistics value centring and shared toolbar opacity; Edit Library pinned right;22dp Details card/tab gap.
- All notice pagination replaced by continuous searchable disclosure. Existing60 legal component texts/notices retained; current runtime inventory checked separately.
- Central language/region icon mapping across playback and choice renderers; neutral icons for language-only codes, no arbitrary country inference. Shared keyboard gets compact one-line input and dedicated `. , / \\`, retaining bottom-left Caps Lock and all actions.
- Put.io inline account focus replacement/re-entry and pending-auth/error-dialog cleanup and queued completion-after-close corrected. Real account sign-in remains owner/device acceptance, a release-blocking integration gate.

No physical Shield pass, signed upgrade, hardware output, real account/provider/SAF or full storage round trip is implied by automated tests. See [acceptance checklist](SHIELD_QA.md) and [implementation/evidence ledger](IMPLEMENTATION.md). No merge into main is authorised.

Validation: 1,068 application tests/ 226 classes in local and CI runs, zero failures/errors/skips; 95 tooling tests; lint zero errors; 64 native decode comparisons; 24 rebuilt FFmpeg libraries and 88 packaged native hashes verified. All 60 notice texts and 249 region resources remain in the shrunken APK. Complete unsigned artifact download and independent checks passed. See [full report](RELEASE_REPORT.md) for source/checksum, limitations and required owner access.
