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

## Screen data item 2 — recent scans

Code `d59573789`. `LibraryScanController.recentScans()` returns the newest ten finished source scans as an immutable memory-only list (initially empty until asynchronous load). Each ScanProgress includes start UTC time, observed duration, source label, added/removed/updated counts and plain failureReason. -1 means unknown. Native local/network terminal events now carry committed removal counts. Entries include cancelled/not-started requests; duplicate terminal broadcasts do not add duplicates. The atomic bounded file is under noBackupFilesDir, outside existing logical backup categories, so no backup category was expanded.

## Screen data item 3 — hourly automatic scans

Code `ff80eaac2`. No new screen call. `PreviewAutoScanPolicy.initialise` defaults an absent schedule to one hour, preserving explicit Off/custom schedules and the onboarding gate. `NetworkAutoRefresh.autoRescanAtStart(context)` now reports admission for this opening, using the enabled preference AND last successful completion strictly over an hour ago; screens should continue reading the preference itself for the selected On/Off value. The native lifecycle observer and existing application polling both use this gate. `ScanSchedulePolicy.completed(context)` records success; native batch and local import terminal paths call it. Put.io success will join in item 4.

## Screen data item 4 — Put.io service status

Code `9f46af2c1`. `PutioSyncScheduler.status()` returns immutable memory-only PutioServiceStatus: signedIn/signedInAs, storageUsed/storageTotal (-1 unknown), reachable and lastCheckedUtcMs, syncState and plain failureReason. `refresh(context)` asynchronously reads the encrypted token and checks account status without syncing. `retry(context)` checks then syncs the selected API-owned scopes. Existing request calls also update status. `stop()` cancels owned HTTP calls and the worker; LibraryScanController.stop includes it. API source syncs feed the same tile/history adapter. No token/cursor/request/body/credential appears in the DTO. A stale quota remains available with its last-check time after an offline failure; unauthorised clears the account data and requests sign-in. Status is process-local; refresh when opening the service row.

## Screen data item 5 — unsaved connection tests

Code `70a38e719`. Register `filecore-supernova156-screen-connections.patch` after the CURRENT FileCore operations, before its CI fixtures (independent of the MediaLib screen-data patch chain). Call `DraftConnectionTest.start(context, new Draft(type, host, port, folder, user, passwordChars), callbackExecutor, resultConsumer)` from `com.archos.filecorelibrary.connections`. Type: SMB, FTP, FTPS (explicit TLS), SFTP, WEBDAV, WEBDAVS. Port -1 uses the protocol default; SMB folder starts with /share. Result exposes succeeded(), folders/videos (immediate children), field and safe reason, cancelled. Job.close cancels. Keep Draft open while useful and close it to wipe its password copy; Job owns a separate copy and wipes it at completion. The caller’s original char[] remains caller-owned. Callback executor controls delivery; close the job on screen exit. The job result deadline is 25 seconds, transport stages normally 5 seconds; there are two workers and four queued slots. No credential/source/session cache writes, including on success. SFTP checks existing SSH pins read-only; a first-use probe accepts only its ephemeral session key and never persists trust. Saving/confirming trust remains the existing connection flow’s job.

## Screen data item 6 — missing files and recovery

Code `a45276960`. Register `medialib-supernova156-screen-missing.patch` after the schedule patch, before CI fixtures. The LibraryHealth attention worker now publishes `MissingFilesRepository.current()` with immutable missing rows and the SAME attention snapshot. Until a successful inventory, current() is null; failed reads preserve prior data. Rows expose id, library title, sourceName, folder, sinceUtcMs (0 unknown for legacy markers), oldFileName and size. Trigger the existing attention refresh after loading/changing the library; re-read current when data refreshes. Worker-only `findAgain(context, row, cancelledSupplier)` traverses the saved source, requires a unique exact name/size match after the complete traversal, verifies it again, then relinks. False means no unique match or a stale guarded row; exceptions mean search/update did not complete. `remove(context,row)` removes library records only. Both invalidate the library cache. Find errors/ambiguity change nothing. Missing timestamp is first observation, retained across repeated scans; successful rediscovery clears it.

## Screen data item 7 — counted cancellable NFO export

Code `3b8b3daff`. Register `medialib-supernova156-screen-nfo.patch` after the missing-media patch, before CI fixtures. Worker-only `NfoExportJob.preflight(context)` returns immutable Plan.total and knownReadOnly before writing anything. It counts every target video, including those without exportable tags; such videos become failed outcomes. Call `NfoExportJob.start(context, plan)` to get a Job; Job.cancel()/close() stops its own work. One export job is admitted at once. Subscribe with existing `NfoExportProgress.listen(callbackExecutor, consumer)` and close the subscription on exit; snapshots retain done/total/currentTitle/finished and add written/skippedReadOnly/failed/cancelled. Count units are videos (one episode may also need its shared show NFO); companion artwork is best effort. current() is memory-only. A cancelled job may finish with done < total; wait for finished before presenting its result. Job.await is worker-only. Put.io and known read-only protocols skip without credential, metadata or network access. Other sources check parent write capability on the worker. The existing export service uses this same job and cancels its owned job on background/destruction. No screen wording changed.

### Item 7 follow-up — cancellation during output opening

Code `a041eb382`. Apply `medialib-supernova156-screen-nfo-cancel.patch` immediately after the NFO job patch.
No API change. If cancellation wins while getOutputStream is opening, the rejected stream is closed and the empty
NFO target removed. Both this race and cancellation during a blocked write have regressions. All previously
published patches remain unchanged. The report lists the final seven registration operations.

## LAST section 1 — complete About data (6 November 2026)

`about.AboutCredits.load(context)` reads `supernova/credits.json` off the UI thread and returns an immutable list of
`Entry`: `id`, `name`, `version`, `licence`, `url`, `role`, `textAsset`, `licenceSource`, `service`.
`read(InputStream)` supports an already-open stream; it leaves that stream open. Bad schema, incomplete entries and
duplicate identities fail with `IOException`, so do not display a partial catalogue as complete.

The catalogue covers each distinct coordinate of `docs/foundation/RUNTIME_SBOM.json`, pinned local modules, the shipped
native stack, bundled artwork and all eleven service endpoints. Software entries point at full bundled notices in
`foundation/legal/`; read that asset locally for licence detail. The new catalogue does not alter those protected
assets. Service entries link their provider terms/documentation; they are not described as open-source licences or
as fixed server binaries. `url` is the project/service address for the QR. `source`, exact native commits, archive
hashes and licence-text hashes remain in the JSON for provenance; they are not screen copy.

Release history now has current **0.156, in development** and historical **0.155**, with `documentation` paths
`docs/0.156/` and `docs/0.155/`. Existing older entries and documentation directories remain in place. This data edit
also advances the build's existing data-derived version code/name to 156/0.156; it is not evidence of a signed release.

## LAST section 2 — unindexed file Details

Call `metadata.UnlibraryLookup.start(context, fileUri, callbackExecutor, consumer)`; retain the returned `Job` and
call `cancel()` (or `close()`) when leaving/rebinding the file. Supply the main executor for a screen callback.
Cancellation stops the active HTTP call, interrupts the worker and suppresses even an already queued callback.
A surviving job enqueues at most one result. The deadline is 12 seconds; the callback executor owns delivery timing.
The bounded pool admits two active and four waiting jobs; overload, offline, malformed/no results and timeout return
`UnlibraryLookup.NONE`, never an exception or credential-bearing error object.

`Result` has `kind` (`FILM`, `EPISODE`, `SHOW`, `UNKNOWN`), `confidence` (`CONFIDENT`, `POSSIBLE`, `NONE`), `tmdbId`,
`title`, `year`, `season`, `episode`, `poster`, `backdrop`. For episodes the TMDB ID is the **series** ID; season/episode
come from filename parsing. Unknown numbers are -1. Artwork URIs may be null. NONE is UNKNOWN/id 0/empty title and
year. One exact normalised title/year candidate is CONFIDENT; ambiguity/year mismatch is POSSIBLE. Confidence is a
filename-based estimate, not proof of edition or an instruction to save a match.

The existing MediaLib `SearchPreprocessor` parses only the filename. The existing validated Fix match transport is
reused through `StreamingRepository.matchSearch(context, path, parameters, callerOwnedClient)`; the original overload
still uses its original HTTP client. The new overload permits the job to own call cancellation and timeouts. It keeps
the same endpoint/parameter allow-list, response validation, size limit, authentication and diagnostics.
The lookup invokes no indexer, scraper-apply path, match journal or database write. Opening Details must not call Add.

**Add to library for one file:** use the existing MediaLib
`com.archos.mediaprovider.video.VideoStore.requestIndexing(fileUri, context, false)` (or the two-argument overload if
an explicit re-index is wanted). Source: prepared MediaLib `VideoStore.java:78–184`. It routes a local file to the
Android single-file MediaScanner and indexable remote/content URIs to the existing provider/network scanner.
The filename lookup is separate. In Supernova's source-selection browser, first stage `BrowserSelection.include(uri)`
and save via the existing `ShortcutDbAdapter.VIDEO.applySupernovaRoots(...)` transaction, preserving the configured
roots/exclusions. Otherwise the library policy may reject a file outside selected sources. Do not widen the root to
its parent folder just to add a single file. `UniversalFileBrowser.apply` is the existing Save Changes & Scan sequence;
its follow-up `PreviewLibraryScan.requestAfterChanges` is a source refresh, not the one-file indexing API.

## LAST section 3 — persisted backup presets

Use `utils.backup.BackupPresets` with **the same default SharedPreferences** used by `BackupWorkspace`:

- `current(prefs)` migrates once and returns `BackupSelection.Preset`; fresh installs choose EVERYTHING.
- `selection(prefs)` returns an immutable set to pass to the existing backup preparation/export operation.
- `select(prefs, preset)` atomically writes the choice and category flags; CUSTOM retains current choices.
- `setIncluded(prefs, category, included)` atomically selects CUSTOM and writes its complete category set. Keep the
  existing private-data consent flow before enabling a private category.

EVERYTHING contains every category for which `isPrivate()` is false, including TITLE_PLAYBACK. SETTINGS_ONLY is exactly
the ten `PortableSettingsRegistry.Category` domains. Neither preset includes SOURCES, BOOKMARKS or LOCATIONS.
Migration reads all existing per-category flags, with the original history/resume fallbacks and defaults. An existing
selection differing from Everything becomes CUSTOM without dropping private opt-ins or other choices. Direct legacy
category edits are detected on the next read and become CUSTOM. The persisted preset key is
`supernova_backup_preset_v1`; failures to commit throw `IllegalStateException`. The screen owns error handling.
No restore choice, archive format, credential policy or export engine changes. Call this API when wiring the new
three-choice screen; the protected old screen is not edited here.

## LAST section 4 — focus audit

`0156-focus-audit.md` has the route/transition matrix, 17 concrete loss cases and proposed Robolectric regressions.
Those tests live only in the report. Existing opener restoration, Search key/result memory and Local Storage folder
memory are explicitly retained. Claude owns the screen fixes and adding the proposed tests.
