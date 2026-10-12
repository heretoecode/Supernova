# Actual application-view evidence

These PNGs are captured from the application's real Android views in Robolectric with synthetic fixtures at the verified test-source commit. They are not conceptual designs, photographs of Shield output or claims of hardware acceptance. The [provenance receipt](RENDER_PROVENANCE.json) records each digest. Framework rasterisation, font smoothing, artwork and animation differ from device output; [Shield QA](../SHIELD_QA.md) remains required.

| Capture | Evidence |
|---|---|
| [Five sections](0138-details-five-sections.png) / [150% text](0138-details-five-sections-150-text.png) | Cast/Crew capped at eight and all available lower sections visible together, read-only. Streaming-only fixture shows availability instead of local technical claims. |
| [Local full technical fields, 150% text](0138-details-local-tracks-150-text.png) | Real metadata-constructor fixture with codec/channels, HDR, bitrate/frame rate and subtitles; entire Technical Information panel retained in viewport. |
| [Small Details viewport](0138-details-small-viewport.png) | 640×360 logical viewport keeps real Play/More actions and lower tabs visible. |
| [Enlarged navigation](0138-navigation-150-text.png) | Single-line brand/clock and scrollable focused navigation at enlarged text. |
| [Bottom Technical Information](0138-settings-technical-bottom.png) | Independently revealed nested About item within padded viewport. |
| [Actual AndroidX notice reader](0138-licences-androidx-reader.png) | Async catalogue/first bundled notice uses lossless bounded pages and tested reader/catalogue navigation. |
| [HUD](0138-playback-hud.png) | Real transport configuration, compact bordered timestamp, approved secondary/main row grouping; example duration/end labels supplied by test. Separate controller tests exercise actual timing/context updates. |
| [Search](search-empty-next.png) | Compact centred field, icon Caps/Backspace, wider Space, five distinct aligned explanations. |
| [Home](foundation-home-initial.png) / [poster focus](foundation-home-focus-position-5.png) | Preserved Home geometry and whole artwork focus unit using synthetic artwork; no original faded-poster diagnosis claimed. |
| [Retained wizard](next-qa-wizard-review-save.png) | Existing walkthrough navigation/review/save preserved; no broader redesign. |
