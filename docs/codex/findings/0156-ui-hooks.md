# 0.156 engine hooks for Claude

## Part A

- Backup messages need no change. The same verified 2.0 archive supports 16 MiB per
  uncompressed entry, 32 MiB overall and 36 MiB compressed. No truncation or weakened
  hash/schema/CRC checks. Shield heap measurement with the real 61k library remains required.
- Apply `.github/build/medialib-supernova156-scan-reliability.patch` **after** the
  locked MediaLib operations and the existing `medialib-supernova155-scan.patch`.
  The latter requires the existing FileCore `filecore-supernova155-scan.patch`
  (`ScanIoScope`). These previously unintegrated prerequisites must ship together.
  No protected build manifest/recipe was edited. Apply/check was tested in this order.
- `PreviewLibraryScan.install` installs `ScanMetadataHandoff` to drain a persisted
  metadata request on the next foreground start. `metadata_deferred_until_foreground`
  is a stage on an accepted handoff, not a failed scan. No foreground-service launch
  or new notification is introduced; AutoScrapeService does not promote itself.
- Native lifecycle intents now contain `source_kind`, `failure_stage`,
  `failure_class`, `failure_message`, `failure_trace`. Both scan recorder consumers
  forward them to Diagnostics, which applies its existing text redaction.
- `FocusSearchGuard.findNextFocus(scope, focused, direction)` is the only raw
  FocusFinder caller. Existing menu guard signature remains. All direct callers,
  including PlaybackSubtitlePanel, delegate to it. No focus order was changed.
- Local Storage's existing lookup now uses `LibraryFileLookup`, querying raw local
  paths and matching canonical keys. Claude still owns row contents/artwork.
- Details' cached delivery calls `OmdbSupplement.cached`; its worker calls
  `loadCached(context, kind, tmdbId, imdbIdSupplier)`. Values survive offline and
  locale changes. Refresh attempts (including failures) are reserved for seven days;
  900/day request admission remains active. No additional screen call is needed.

## Validation boundary

The old diagnostic report has no exception evidence for source `4ae5c08c…`; its
actual failure is not proven by source review. The patch fixes known provider-owned
root scheduling, deferred metadata handoff, and cross-process overlap. Capture a new
report after integration to identify any remaining root-resolution/listing/database
failure. Do not describe the historical source-specific cause as confirmed.

## Part B

- Bar/cards/time bubble: `PlayerActivity.previewParts(embeddedChapters, durationMs)`
  returns `PlaybackParts`; use `parts()` and `partAt(positionMs)`. Re-query when the
  existing chapter or IntroDB provider callbacks update. Both file and verified
  ChaptersDB starts participate; the older `previewChapters` API remains for old HUD callers.
- Episodes: `PlayerService.previewSeasonEpisodes()` returns immutable items with
  title, episode number, still URI, media URI/ID, duration, watched and resume fields.
  `previewAdjacentEpisode(1)` is the next playable file (including across seasons).
  These consume the existing in-memory library snapshot; absent snapshot means empty
  until the normal library loader supplies it. Hidden rows are excluded regardless
  of the Library's show-hidden display preference.
- Pictures: `new PartPictures(timeline, episodeStill, frameSource, pictureReady)`;
  display `pictures()` immediately. Each starts with the episode still and requests
  a frame at start + 2 seconds (clamped inside very short parts). Close when switching
  media/closing the strip. Part D's SeekThumbnails supplies `FrameSource`.
- Subtitle position: use `SubtitleManager.getVerticalPosition()` and
  `setVerticalPosition(percent)` (0..90, step 5). The setter saves the new key itself.
  Default is 5. Do not write a new percentage into the old KEY_VPOS on Settings;
  use `SubtitleVerticalPosition.save(prefs, percent)` and restore the manager.
  Migration retains an exact legacy pixel calculation until the first explicit
  adjustment. Settings reset must remove both new keys (they are in STYLE_KEYS).
- Appearance focus: `manager.setAppearancePreview(true)` suppresses the real track,
  even when currently None. Request `appearanceSample(localizedText, canvasWidth,
  canvasHeight)` after each style change and place that bitmap on the screen.
  `setAppearancePreview(false)` on leaving the appearance section or closing restores
  the selected track. Delay is excluded. This is a Java sample using current colors,
  family, size, outline, shadow, background and position; compare custom libass font
  substitution on Shield. PiP and appearance suppression compose independently.
- PiP actions and subtitle suppression are wired in PlayerActivity. Existing Play,
  Pause and Restore labels/resources are reused. OK/Enter requests full screen when
  Android delivers that key to the Activity; system interception/Shield menu behavior
  needs device confirmation. Reopening another Supernova Activity brings PlayerActivity
  forward, preserving the original playback intent rather than starting a new media item.
- Sleep: `PlayerService.playbackLimits().setTimer(Timer.MINUTES_30 / MINUTES_60 /
  MINUTES_90 / END_OF_EPISODE / OFF)`. Query `timer()` and `remainingMs()`; -1 means
  episode end. Add/remove a `Consumer<PlaybackLimits.Event>` for card updates.
  STILL_WATCHING opens the card; call `answerStillWatching(true/false)` for its buttons.
  The next episode is held while asking; unanswered means STOP at 60 seconds.
  Keys `still_watching_enabled=true`, `still_watching_episodes=3`; sleep starts Off
  each playback service session and is not restored as a running wall-clock timer.
- Series switch: `PlayerActivity.seriesTrackChoicesEnabled()` /
  `setSeriesTrackChoicesEnabled(boolean)`; default off per series to preserve existing
  behavior. Enabling snapshots the chosen descriptions; successful manual choices are
  remembered. PlayerService matches descriptions when metadata enumerates the next file.
- Bar OK: call `PlayerController.handleBarPlayPause(event)` **only with the bar focused**.
  It toggles on initial down and consumes repeats/releases, leaving arrows untouched.
  No other part of Claude's remote scheme was changed.
- NOVA overlay show code, includes, layout and its translated menu-tip strings are gone.
  Old animation/drawable assets are left to their owner; GitHub cleanup remains parked.

## Part C

- Search's query/matching path now calls `PreviewSearchText.rankFields`; original
  titles stay in the existing metadata index, people/studios form the second tier,
  filenames the final tier. `ranked(query, documents)` exposes immutable ranked data
  for other callers. Matching is at word starts, including surname-only queries.
- Fix match: construct `FixMatchRepository(context)` and run `search(query, scope,
  currentKind, currentTmdbId)` on `StreamingRepository.IO` (scope movie/tv/all).
  Candidates contain ID, title, year, kind, poster URL and currentMatch. Numeric IDs
  use scope; explicit `tmdb:movie:123`, `tmdb:tv:123` and IMDb tt… IDs are accepted.
  On the same worker, `apply(videoId, candidate, season, episode, cancelled)` prepares
  canonical provider details/artwork, asserts file ownership and atomically saves
  native metadata, retaining file watched/bookmark/hidden state. TV requires the
  known season/episode rather than guessing. Refresh the Details data after success;
  the repository invalidates the library cache/notifies the media URI. Use callback
  generation/cancellation checks when closing the panel. No panel was implemented.
- Library tile: `LibraryHealth.attention(context)` returns one immutable count/reason
  snapshot. Or use `needsAttentionCount` and `needsAttentionReason`. Inventory work
  runs on a worker; the first call may return zero until computed. Re-read on the
  existing library/pref refresh; no per-frame database traversal. Deleted/excluded
  media markers do not inflate the actionable count, and media reasons deduplicate.
- Backup: `BackupSelection.categories(Preset.EVERYTHING / SETTINGS_ONLY / CUSTOM,
  customSet)`. Everything includes all supported categories except the three with
  `BackupCategory.isPrivate()`: sources, bookmarks, filenames/locations. They remain
  off by default. `privacyReveal()` supplies each category's existing disclosure text.
  Screens retain selection review and wording ownership.
- Diagnostics always records full detail; LEVEL is still read but harmless.
  `Diagnostics.clearAll(context)` removes recorder-owned files and pending old
  event generations without changing the logging switch. It throws IOException if
  deletion fails; call on a worker. Old queued evidence cannot repopulate a cleared log.
- Seek switch key: `SeekThumbnailPreferences.KEY` = `seek_thumbnails_enabled`, true
  by default. Part D reads it for scheduled and explicit frame requests.
- Provider list: `StreamingRepository.providers(context, country)` is persisted for
  a week; `providersLastUpdated(context, country)` is the last successful refresh.
  `installProviderRefresh(context)` subscribes to country changes and checks due
  refreshes daily. It is automatically installed by providers/selectedCatalogue;
  Claude may also call it from the existing service initialization for earlier startup.
  Country changes force refresh without erasing the last-success date or stale list.
- NFO: apply `medialib-supernova156-nfo-progress.patch` after Part A's scan patch.
  `NfoExportProgress.listen(mainExecutor, snapshotConsumer)` returns AutoCloseable;
  close on screen exit. Snapshots have jobId/done/total/currentTitle/finished.
  `current()` supports immediate restoration. Done counts export attempts whose writer
  task finished (or rows that could not be exported), not a guarantee every file was
  writable. Cancellation can finish with done < total. No new export screen/wording.

## Part D — asynchronous previews and expanded diagnostics

Code `207242079`. `PlayerService.seekThumbnails()` returns the service-owned engine
once prepared; it can be null. Keep the episode still/placeholder in that case.
For the time bubble call `nearest(positionMs)`; it never reads disk on the UI thread.
Subscribe with `addListener(positionMs -> refreshCurrentBubble())`, and close that
subscription when its UI is removed. For parts pass this engine to the existing
`PartPictures(parts, episodeStill, frameSource, ready)`; it requests start+2 seconds.
Close `PartPictures` on page/source replacement. The service, not the screen, closes
`SeekThumbnails`. All picture callbacks arrive on main and are fenced after close.

The `seek_thumbnails_enabled` preference defaults true and the live listener cancels
work when switched off. Slow extraction back-off also delays priority picture jobs.
Lifecycle, seek, stop, playback completion and explicit/bulk watched cleanup are
wired; no screen, row, new resource or settings wording changed in Part D.

Diagnostics stay always full while enabled. `clearAll(context)` is a worker call.
See `findings/diagnostics-hooks.md` for exact protected screen/native insertion points
and coverage limits, and `findings/seek-thumbnails.md` for extraction choice, measured
host timings, request-target timestamp quality and the Shield test sequence.

## Late B17a — network stream recovery

Code `afb3a755d`; no new UI call or control change. Existing player pause/start,
proxy read errors and native-reported I/O failures invoke the recovery engine.
Apply `medialib-supernova156-network-recovery.patch` after the four A/C/D patches.
Recovery preserves session/track/seek intent and emits stream_reconnect boundaries;
see `findings/network-stream-recovery.md` for gate behavior and native visibility limits.

## Screen data item 1 — stop and progress

Code `6dac8747d`. Register `medialib-supernova156-screen-scan.patch` after the current MediaLib operations,
before `next-medialib-test-fixtures.patch`. `PreviewLibraryScan.install(context)` installs the process observer.
`PreviewLibraryScan.progress()` / `LibraryScanController.progress()` returns immutable `ScanProgress` with
sourceLabel, checked, total (-1 while unknown), running, stopping, failure and failureReason(). It performs no
I/O. `PreviewLibraryScan.stop()` cancels queued work and the active network/local/metadata workers off main;
running remains true until active workers unwind and send their terminal events. Requests during stopping are
held out. A subsequent request starts with a fresh cancellation generation. Put.io API sync wiring follows in item 4.

Friendly names are copied from shortcut/storage metadata on a worker. A source-list model can optionally call
`LibraryScanController.sourceName(indexedRootUri, friendlyName)`; progress immediately uses the cached name.
No raw exception message or credential is included in failureReason. Stop does not interrupt a database applyBatch;
the existing atomic provider transaction finishes or rolls back before the worker releases its ownership.
