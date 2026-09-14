# Mark’s NOVA — Edition 3
Based on NOVA 6.4.63. Version 6.4.63-mark.3 (6040065).

## Navigation and appearance
- Optional “Try new UI” in main settings: top navigation with Home, Movies, TV shows, Network & files, Settings and Search.
- Classic sidebar remains the default. Turn the toggle off to restore it.
- Both layouts use the same native NOVA library rows and settings. Top tabs jump to the corresponding rows.
- Documentaries is back inside the TV shows category row, with a main-settings visibility switch.
- Brighter main and secondary text.

## Streaming
- Provider logos with accessible names and a readable fallback if artwork cannot load.
- Compact alternatives button; local playback retains its ordinary visible label.
- JustWatch/TMDb attribution is separate from playback controls.
- Episode details query availability for that episode’s season. Exact episode coverage is not verified; this is explicitly labelled.
- Show details retain explicitly labelled show-level coverage.
- Availability appears before title-link enrichment. Links resolve when selected.
- Strict provider-name matching avoids mixing TMDb and JustWatch numeric identifiers.
- Repeated launch clicks are guarded. Refresh in streaming settings invalidates availability.
- No rent/buy offers. Country-specific saved provider choices remain.

## Identification and responsiveness
- TV-show identification accepts TMDb numeric IDs, TMDb TV URLs, IMDb IDs and IMDb title URLs.
- The existing confirmation screen is retained before applying a match.
- Metadata matches without posters/air dates are no longer categorically hidden.
- Subtitle discovery and file operations in the TV wizard run off the UI thread.
- Library searches cancel when leaving the screen; empty searches clear old results.
- Poster images may retry after a temporary failure.
- Corrected legacy artwork-cache folder construction.

## Security and backups
- Internal media database provider is no longer exported to other apps.
- Old diagnostic broadcast receiver is disabled.
- OpenSubtitles tokens and request bodies are no longer included in diagnostic logs.
- Backup export includes typed settings and uses dated names.
- System document picker for export/import, with a fallback when a TV has no document picker installed.
- Full archive validation, path/size/duplicate checks and database integrity/version checks before replacement.
- Prepared files replace live data with rollback on an operation failure. A dated recovery archive is kept.
- Optimised release APK, signed with the existing personal build identity. CI refuses a different certificate.

## Limits and testing
- No exact episode-offer data subscription has been arranged; season coverage must not be interpreted as exact episode coverage.
- Shield remote navigation, rendering and playback still require physical-device acceptance testing.
- The signing workflow supports a NOVA_SIGNING_KEY_BASE64 repository secret. Until that is provisioned, the existing Actions cache is required; the build refuses to generate a replacement key.
- Moving to an internal-only provider may affect third-party apps that relied on direct access to NOVA’s private database.
