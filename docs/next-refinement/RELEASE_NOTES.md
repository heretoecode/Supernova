# Supernova next refinement — provisional 0.136 QA

Development branch: `codex/supernova-next-refinement-2026-10-10`. Builds retain `app.supernova.player`, use versionCode 136, and require the verified 0.135 certificate. This identifier is provisional, not final release approval. No merge to main is planned.

## Changes
- Home: gradient-free hero previews, one-line real tagline/synopsis, compact More Info, local contrast treatment and prioritised reliable next episodes. Durable identity history survives source removal; unfinished playback remains in Continue Watching. History and resume controls are independent, with explicit history clearing and independent backup checkboxes.
- Navigation and shared library pages: consistent clock, animated accent underline, smooth shade transitions, filtered library summaries, compact outlined controls and immediate genre selection.
- Custom Library Page: text-only first-use landing, five-step embedded creation and floating editing over the saved page, persistent actions, remembered focus, and centred save/discard/delete confirmations.
- Search: keyboard and query area beside guidance/results, live search, structured guidance and one shared six-action remote keyboard with meaningful icons.
- Network & Files: stable category focus, adaptive shared panels, conditional file controls, working All Files switch, staged source selection, inline connection/recovery forms, device discovery, integrated Library Health and put.io enable/account controls.
- Settings: inline native choices, deterministic category/child navigation, grouped provider selection, independent integration controls, merged Advanced/About overviews and bounded asynchronous licence opening only on explicit activation.
- Onboarding: local-only flat drives, folder checkbox selection independent of browsing, selections across drives and explicit save/finish before scan, discovery or enrichment.
- Diagnostics: QA logging enabled by default, persistent crash/exit breadcrumbs, bounded watchdog evidence and resource trends, suspected focus/layout anomalies and private timestamped local export.

## Preserved scope and compatibility
Details and Playback/HUD redesigns and new features are excluded. Their visual source files are unchanged. Unavoidable shared changes comprise the global keyboard/controls and diagnostics, stable history/resume checkpoints, explicit watched commands, native scan/integration policy, and provider lifecycle registration. Existing native media/audio/subtitle and Details regression assertions remain in the suite.

Source identity, signing certificate, package, user preferences and databases are retained. No media-file deletion or user-data reset is part of this release. Actual installation over signed 0.135 and hardware playback remain pending user SHIELD QA.

## Release gates and known limits
See [requirements](requirements.json), [progress](PROGRESS.md), and [QA readiness](QA_READINESS.json) for current evidence; edited code alone is not completion. The clean native build, 514/514 tests, 58/58 Python tests, zero-error lint gate and binary conformance passed in [CI 38090668342](https://github.com/heretoecode/Supernova/actions/runs/38090668342). The four-ABI comparison passed 64 decode cases; the actual unsigned APK contains 88 native libraries, with 24 rebuilt FFmpeg and 138 reviewed runtime artifact hashes matching. See [CI verification](CI_VERIFICATION.json). Signing remains blocked below.

- The protected signing environment must admit this exact new branch. The API rejected the policy update with HTTP 403; the existing key and historical workflows remain intact. No signed APK is claimed yet.
- put.io production OAuth requires actual registered client/redirect configuration and end-to-end validation. Account/enable semantics are implemented; OAuth success is not claimed.
- Physical SHIELD visual/focus/contrast, licence crash reproduction, upgrade/data retention, multi-drive scans, authenticated networks, playback/HDR/audio/subtitles and sustained diagnostics soak are pending. The recorded 0.135 licence process death has no proven root cause; the new reader removes eager focus work and the diagnostic changes improve future evidence.
