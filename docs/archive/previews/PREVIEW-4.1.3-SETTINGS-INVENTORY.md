# Settings functional inventory

Compared the XML preference definitions, `VideoPreferencesCommon` handlers and
`PreviewSettings.organise/consolidate/auditPresentation` against the delivered
4.1.2 implementation. Existing capability-dependent visibility remains owned by
the native preference controller, not overridden by this pass.

| Capability | Modern access / disposition |
| --- | --- |
| Playback/resume, repeat, pause/HUD, speed | Playback; existing handlers retained |
| Projector positioning | Playback; restored access to `player_projector_mode_key`, which still controls player positioning |
| Decoder, resolution, refresh/HDR options | Video; native device capability gates retained |
| Audio output, passthrough, speed/delay | Audio/Playback; existing handlers and saved values retained |
| Subtitle account, language, encoding and display | Subtitles; existing handlers retained |
| Scan/rescan, scheduling, metadata, hidden media | Library; no new scanner or importer |
| Home row visibility/order and watch-next | Library > Home rows; replaces classic row toggles |
| Movie/TV view and sort | Per-library view/Columns controls; replaces classic global sort preferences |
| Streaming provider selection | Streaming; existing provider catalogue and callbacks retained |
| Network/source/file-browser options | Network; protocol credentials/listings remain native |
| Torrent folder and blocklist | Advanced; restored existing custom preference `onClick` handlers and registered folder picker (not a new torrent implementation) |
| Trakt, backup/restore and integrations | Advanced; original handlers retained |
| Diagnostics | Advanced; new opt-in toggle and report export |
| Version, source/build identity, bundled notes | About |
| Classic theme and classic-only rows | Saved values retained, disabled in modern UI; Accent & Colour/Home rows are their relevant modern replacements |
| Legacy interface-selection/TV-mode overrides | Existing modern-mode restrictions retained; no automatic return to legacy UI |

The earlier explicit disabling of projector and torrent controls was a presentation
restriction, not evidence that their implementations were dead. Their original
preference implementations are reused. No duplicate settings or saved values are deleted.

Physical capability behaviour remains NOT TESTED — USER QA REQUIRED.
