# Edition 3.2 — experimental UI implementation

Version: 6.4.63-mark.3.2-preview (6040067). Package and signing identity remain NOVA Preview.

- Compact slate navigation, underlined active section and search icon.
- Landscape Continue watching/recently played cards with episode stills where available, episode numbers and clamped resume progress.
- Illustrated category tiles, including a dedicated documentary landscape; existing library collage artwork is retained.
- Clean portrait cards: captions appear on focus, or remain visible if artwork is missing. Accessible titles remain available throughout.
- Home prioritises Continue watching, TV categories and recently added. Row preferences and native item actions are preserved.
- Playback title inside the slate HUD, compact functional options menu and brighter unfocused controls. More opens the existing player menu.
- Classic UI remains available through Try new UI. No placeholder chapter, statistics, thumbnail or Up Next buttons were added.

Validation: automated card recycling/progress, navigation, classic fallback and native control inflation tests; a native-rendered home layout fixture is included with build diagnostics. Shield playback and remote testing still required.

Remaining functional work: chapter picker/markers, stats, seek previews, temporary subtitles, Up Next and scan counters. Streaming links remain paused. Home uses the actual library rows: Recently added can include episodes as well as films; the mock-up's fictional artwork and rows are not bundled as content.

---

# Edition 3.1 Preview — home-screen repair
Version 6.4.63-mark.3.1-preview (6040066). Updates the separate NOVA Preview app.

- Removes the legacy title/search view structurally so it cannot reappear on row selection.
- Compact top navigation and 40dp row alignment replace the empty header area.
- Home, Movies, TV shows and Network & files each display their corresponding rows.
- Experimental UI always uses slate colours without changing the saved classic theme.
- Poster titles have two lines, brighter text and a slate information panel.
- Classic navigation remains available when Try new UI is switched off.
- This is a home-screen repair; outstanding playback features listed below remain outstanding.

# Mark’s NOVA — Edition 3
Based on NOVA 6.4.63. Test package: 6.4.63-mark.3-preview (6040065).

NOVA Preview installs separately as org.courville.nova.markpreview. It does not update or remove your existing NOVA. The earlier update-signing key was not retained: a parallel build won the cache reservation with a different key. The normal update workflow still rejects this different identity. Preview uses isolated content-provider authorities and its own app data.

## Navigation and appearance
- Optional “Try new UI” in main settings: top navigation with Home, Movies, TV shows, Network & files, Settings and Search.
- Classic sidebar remains the default. Turn the toggle off to restore it.
- Both layouts use the same native NOVA library rows and settings. Top tabs jump to the corresponding rows.
- Documentaries is back inside the TV shows category row, with a main-settings visibility switch.
- Brighter main and secondary text.

## Provider settings
- Streaming launch links are paused for this test edition, following the latest handoff.
- Country-specific choices remain saved. Provider lists use regional display priority.
- Search and “Show all providers” controls; selected providers remain visible when filtered.

## Experimental playback presentation
- “Try new UI” also enables a translucent slate playback bar with the timeline above centred controls.
- Slate focus treatment for the existing audio, subtitle and playback option cards.
- Experimental remote seeks begin at 10 seconds; existing hold acceleration remains.
- Audio channel information accompanies track names when supplied by the file.
- Existing playback engine, intro/recap/credits behaviour and classic controls remain.

## Latest handoff: still outstanding
The complete mock-up HUD, chapter picker/markers, new playback stats panel, scan-result counters,
seek-preview generation, temporary subtitles after rewind and Up Next panel are not included yet.
Preview generation remains absent, so it cannot compete with playback.
These notes distinguish this test candidate from completion of the entire handoff.

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
- Optimised Preview APK with a pinned certificate for the isolated Preview package. The original update certificate check remains intact.

## Limits and testing
- No exact episode-offer data subscription has been arranged; season coverage must not be interpreted as exact episode coverage.
- Shield remote navigation, rendering and playback still require physical-device acceptance testing.
- The signing workflow supports a NOVA_SIGNING_KEY_BASE64 repository secret. Until that is provisioned, the existing Actions cache is required; the build refuses to generate a replacement key.
- Moving to an internal-only provider may affect third-party apps that relied on direct access to NOVA’s private database.
