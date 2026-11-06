# 0.157 data and engine hooks

For BRIEF_0157 only. No screen/resource/wording integration is included. These APIs require the four new MediaLib
patch registrations in the delivery report; do not copy the app changes into a build with the old module API.
All paths below are under `com.archos.mediacenter.video` unless a module package is given.

## Files summaries — item 1

`scanning.FilesTabSummary` accepts an inventory worker's devices, local files, current folder rows, saved connection
statuses, library video count, source count, and the **same** `LibraryAttentionCount` used by Library Health.
Publish the completed instance with the page data, then read `shield`, `folder`, `connections`, `care` on main.
Construction is O(inventory), worker-only; reading the records does no I/O. Duplicate physical identities/devices/
connections count once. Non-video entries do not count; excluded videos are neither in-library nor not-added unless
already indexed. Negative/overflowed free-space or connection-video sums remain `-1` (unknown), never zero.
`care.toCheck()` is exactly the supplied attention object's `count`. No network traversal or per-row query occurs.
Input records: `Device(id, freeBytes)`, `FileRow(identity, video, inLibrary, excluded)`,
`Connection(id, reachable, videos)`. IDs must identify physical inventory rows, not translated names.
This is a projection of the caller's real inventory, not a new filesystem scanner. No cancellation is needed for
record reads; discard a superseded worker result when publishing a newer inventory.

## Saved connection reachability — item 2

On the inventory worker, call `scanning.SavedConnectionReachability.create(context, Map<opaqueId, Draft>)` using the
saved connection form's resolved `DraftConnectionTest.Draft` values. It never opens shared playback sessions.
Call returned `ConnectionReachability.refresh(savedIds)` on a worker; `snapshot()` is a memory-only map safe on main.
`Status(reachable, lastSeenUtcMs, checking)` keeps last successful contact across failure/restart; `0` means never
seen. Restart does not pretend that a historical success is a fresh reachability result.
All saved IDs are probed, two at a time, with a 25-second deadline per probe; queued sources follow as a slot opens.
Failure/timeout is reachable=false, not a private exception. The adapter persists only opaque IDs and timestamps.
Put.io is **not** a draft probe: keep using `PutioSyncScheduler.status()` for its row.
`refresh` supersedes old probes; `close()` fences callbacks and closes owned jobs (call on a worker, as transport
close may block). Close caller-owned drafts after closing the repository. The generic constructor takes a worker
executor, isolated `Probe`, saved status map and persistence consumer; use a serial worker for persistence order.

## Single-source scan — item 3 (network portion)

New MediaLib `com.archos.mediaprovider.video.NetworkAutoRefresh.forceRescan(context, sourceUri)` is main-thread safe:
it sends the existing explicit force-scan request with one source selector. The worker admits only an exact
canonical registered network root; it does not replace the saved selection or turn an arbitrary parent into a root.
Progress, stop generation, history and terminal events remain the existing dispatcher/ledger's.
Unknown roots produce no scan. Watch existing progress/request acknowledgement rather than assuming a start.
Local-device-only import and one-Put.io-folder admission are **not supplied**; do not advertise this overload as
supporting those source kinds. Existing `LibraryScanController.stop()` still owns worker cancellation.

## Top-bar scan activity — item 4

`scanning.ScanActivitySignal.LIBRARY.observe(mainHandler::post, running -> ...)` returns an AutoCloseable
`Subscription`. It sends the current state, then changes; main does no polling. Close on view/model disposal.
`running()` is a memory-only getter. The tracker publishes aggregate local/network lifecycle ownership; Put.io
publishes admitted sync ownership through its terminal/failure/finally path, including the gaps between folders.
The ring stays on until the last owner ends. Queued superseded revisions and disposed subscriptions are fenced.
`set(owner, boolean)` is an engine hook, not a screen action: use stable domain keys and clear them on every terminal
path. Do not derive completion from a timer or clear another domain's key. Stop remains active until workers end.
Observers must provide an executor which actually posts to main. Listener exceptions detach that subscription.

## Cadence and recent history — items 5–6

`scanning.AutomaticScanChoices.PERIODS` is the immutable millisecond list 0, 3,600,000, 7,200,000, 21,600,000,
86,400,000. `nearest(oldPeriod)` is pure/main safe; non-positive remains Off, enabled values stay enabled, ties
choose the slower positive choice. In particular 12 hours becomes **6 hours**; 15/30 minutes become hourly.
`PreviewAutoScanPolicy.initialise` applies migration and reschedules the existing job, including cancelling Off.
It preserves `auto_rescan_on_app_restart`. Use the existing `NetworkScannerUtil.scheduleNewRescan` on a worker to
save one of the five allowed periods, and `NetworkAutoRefresh.setAutoRescanAtStart` for the separate on-open switch.
Old protected screens still have their old choice lists; the 0.157 Files screen must use PERIODS.
`LibraryScanController.recentScans()` is still the memory-only immutable most recent ten source completions from
0.156's durable store. New `ScanProgress.outcome()` returns RUNNING / FINISHED / STOPPED / FAILED from actual state.
Source label, UTC start, duration, added/removed/updated and safe `failureReason()` already exist. `-1` counts stay
unknown. No new history store, polling or cancellation contract is introduced.

## Search everywhere — item 7

Worker-only `health.MissingFilesRepository.findAgain(context, row, true, cancelled, progressConsumer)` extends the
old overload. `SearchProgress(checked, foldersRemaining)` is delivered on that worker; marshal it to main and fence
it with the screen/job generation. `cancelled` and interruption stop between folders/entries; existing ScanIoScope
owns the 60-second operation deadline and supported transports' cancellation.
It visits the configured connection/local roots, canonicalises local symlinks, checks child containment and a shared
100,000-entry ceiling, and proves uniqueness across the **whole** search before any mutation. A failed/unreachable
listing, cancellation or incomplete traversal cannot commit a partial candidate. False means no unique replacement;
exceptions mean incomplete/unavailable/changed ownership. No result is an instruction to delete the old row.
New MediaLib `MissingMediaActions.relinkAcrossSources(context, expected, oldRoot, newRoot, replacement)` retains the
exact ID/path/size scan-lock/database guards and changes only locations/display names/availability. Watched state,
both bookmark fields, history, hidden state and metadata stay intact. It rejects mismatched source guards/names and
existing destination identities. It is worker-only and can throw IOException; do not bypass the repository's final
physical size/type check. Recovery still needs an identifiable original source for its old-root guard. Crossing local/import and network/
scanned physical index ownership is rejected rather than leaving a row in the wrong source table; that migration
is not implemented. Network-to-network moves retain their existing physical/public identity.

## Personal videos and typed Library Health — items 8–9

No personal-video mutation or typed Damaged/Timed-out/Unsupported health inventory is delivered. Do not implement
these by hiding a title or treating an unmatched title as damaged. Existing missing-files/attention data remains.
See the report for the missing durable scan/backup contracts; there is no new UI call for these two items.

## Forgiving matching — item 10

Preferred main-safe entry: `metadata.FixMatchSearchJob.start(context, query, scope, currentKind, currentId,
mainExecutor, candidatesConsumer)`. It owns a bounded two-worker/four-queued-job pool, a 12-second callback deadline,
and the actual TMDB/Trakt HTTP calls. `close()` cancels calls/work and suppresses even already queued callbacks.
Deadline, offline, rejected work and read failure return an empty list; none of these applies a match.
`FixMatchRepository.search(..., BooleanSupplier cancelled)` is the worker-only synchronous alternative. It now uses
`ForgivingMatchSearch`, as does the old overload. It normalises case/accents/punctuation/article and dotted initials,
tries variants/deletion retries, ranks original as well as translated titles, deduplicates by kind/id and marks the
first ranked candidate with immutable `bestMatch` and `matchScore`. Query fan-out is at most 32 variants (per kind),
40 returned candidates. Synchronous cancellation checks between calls; an in-flight existing transport can take up
to its 20-second bound, so use the Job for prompt physical cancellation and the hard callback deadline.
Identifiers: IMDb `tt…`, TVDB `tvdb:123`, TMDB `123` using scope or `tmdb:movie:123` / `tmdb:tv:123`,
Trakt `trakt:123`, `trakt:movie:slug`, `trakt:tv:slug` (unqualified slug checks both scoped kinds).
Retries are bounded partial-title retries plus one-edit ranking, not a guarantee that TMDB returns every misspelling.
`Transport.trakt(path)` is an optional default-empty extension for injected transports; production resolves Trakt
IDs/slugs without an account token. `ForgivingMatchSearch.Gateway` is the read-only injectable algorithm boundary.
`FixMatchRepository.apply` is unchanged: explicit candidate, known TV season/episode, owned native transaction,
playback-state equality guard. Selecting/searching never applies a result automatically.

## Connections — item 11

`data.ConnectionForm.transport(kind, encryption, previousPort, previousEncryption)` is pure/main safe and returns
`Transport(scheme, port, encrypted)`. Defaults: SMB 445; SFTP 22; WebDAV(S) 80/443; FTP and **explicit** FTPS 21.
Custom ports survive toggles; out-of-range high ports throw. Initialise WebDAV/FTP encryption=true in the form.
New MediaLib `SavedConnectionRemoval.remove(context, shortcutId)` is worker-only and returns `Ticket(undoToken,
expiresUtcMs)` for a durable **10-second** window. It does not touch a server or remove a library/video row.
`pending(context, root)` is a worker-only list/filter query; exclude pending roots from the connection inventory.
Network traversal also excludes them. `undo(context, token)` succeeds only for that pending token before expiry;
false means expired/already undone. Worker exceptions mean the journal/database could not be updated.
`resume(context)` is main safe; the LibraryScanController installation already calls it. It reaps persisted deadlines
on a worker after process restart; database/storage failure retains the journal and retries. Final deletion uses
`ShortcutDbAdapter.deleteShortcut(context, id, expectedUri)` so an ID reused at a different address is protected.
The transient journal is outside the backup settings allow-list. Discovery New/Added/device-kind data is not added.

## Put.io code state — item 12

The current flow already requests an OOB code and links to the configured `https://app.put.io/link?code=…` template.
New `streaming.putio.PutioCodeSignIn.start(context, mainExecutor, listener)` exposes `Snapshot(state, code, link)`
with WAITING / SIGNED_IN / EXPIRED / FAILED; `snapshot()` is memory-only/main safe. No token is ever in this model.
Code request and five-second polling run on the session worker; expiry uses an independent ten-minute clock.
Signed-in is published only after the existing encrypted token store saves successfully. Terminal snapshots discard
the temporary code/link; late/expired/closed responses cannot save a token. Invalid configuration fails on the
worker. `close()` cancels future work and callbacks; an existing OAuth request has its existing 30-second transport
bound. This is a data adapter, not a new credential flow or a change to the protected sign-in screen.

## Files artwork — item 13

Worker-only `data.FilesArtwork.folder(canonicalFolder, inventoryVersion, List<Artwork>)` returns up to four distinct
library picture references under that folder (including descendants), or an empty list. Use the same canonical
path scheme as the inventory; increment version when membership/artwork changes. Cache: 32 folder snapshots.
`still(localFile, playbackBusySupplier, mainExecutor, bitmapConsumer)` is main-safe, returns an AutoCloseable Job,
and returns a cached/small local frame or null. It only admits canonical `/storage/` or `/sdcard/` files on API 29+,
never opens remote URIs, and skips while playbackBusy. The supplier is called on the extraction worker.
Bounds: one extraction worker, four queued jobs, 320×180 target, 8 MiB bitmap LRU, two-second callback deadline.
Close to cancel Android's owned CancellationSignal/work and suppress callbacks. Unsupported/busy/unavailable is
null; retain a plain card. Do not recycle cache-owned bitmaps. Actual codec success and prompt native cancellation
still need Shield tests; a platform decoder can take time to unwind after the callback deadline.

## More Like This — item 14

Worker-only `data.RelatedTitlesRepository.load(context, kind, id, Set<kind:id> library, cancelled)` fetches TMDB
recommendations, TMDB similar and Trakt related using the existing metadata/cache/country/provider settings.
`RelatedTitles.load` and its `Sources` interface are the injectable merge boundary. Each source contributes at most
40 rows. Kind+TMDB ID deduplication excludes the original title; `Result.inLibrary` wins over `onServices`.
Unavailable providers are independent; an offer must pass the configured provider/type filters to appear remote.
Cancellation returns empty groups between requests; existing network calls retain their transport bounds. This is
worker-only and may perform per-title availability I/O; prefetch/cache it, never call from a main-thread tab bind.
New `StreamingRepository.traktDiscovery(context, allowListedPath, client)` uses the fixed HTTPS origin, application
API key only, no account token, redirects disabled, 2 MiB bound. It accepts title ID lookup, slug and related paths;
no arbitrary URL, user endpoint or personal Trakt data is permitted. It throws for bad/unavailable payloads.
TMDB `metadata(..., "similar")` and numeric TVDB `matchSearch(find/...)` are the only TMDB allow-list extensions.

## Details, hero and library statistics — items 15–18

`data.DetailsFacts.tabs(series)` synchronously returns the complete stable tab enum list, including Extras/Related
when content is still unavailable. Draw titles once; fill their content asynchronously. No content result removes a
tab. No network/main-thread work or cancellation exists for this pure call.
`DetailsFacts.read(kind, expectedId, details, alternatives, episodeRuntimesMs, country)` is worker data preparation.
It rejects wrong IDs/kind envelopes, limits aliases to the requested country, deduplicates the main title, derives
series runtime from the median known episode duration (then online episode runtime/last episode fallback), omits
financial fields for series, and takes companies only from that verified title's production_companies envelope.
Missing numeric values are -1, empty collections remain explicit. This does not establish why the owner's Marshals
payload said WingNut Films; that source-specific diagnosis is still open.
`data.HeroArtworkPrefetch.bitmaps(mainExecutor, changed)` owns a two-worker bounded warmup and 40 MiB/three-pair LRU.
Call `warm(List<Assets(backdrop, logo)>)` for current/neighbour heroes (at most three). `ready(assets)` is memory-only;
null means keep the current hero, **not** clear its image. A pair becomes ready only when both requested images are
loaded. `changed` is posted/fenced on main. `close()` cancels tasks, fences callbacks and releases cache references;
no screen/view/animation is retained. The generic Loader/size/budget constructor supports deterministic testing.
`data.LibraryPageFacts.from(existing LibraryStatistics, lastVerifiedUpdateUtcMs, attention, scanProgress)` is pure
and main safe after the existing page-scoped statistics are prepared on their worker. It returns the eight facts
with local/network bytes, -1 for unknown totals, and Status/Reason enums. Feed a verified update timestamp, never
"now" just because a screen opened. Active worker, health items, stopped/failed scan, never scanned and up to date
are distinct. The caller still owns page selection and formatting; there is no provider query per statistic.

## Stuck import and seek — items 19–20

No new call for item 19: the MediaLib import-state patch fixes both full/incremental finally cleanup. Existing
lifecycle events turn off scan ownership; exceptions still propagate and remain failure evidence.
No reliable presented-picture/seek-generation hook is supplied for item 20. Do not substitute seek acknowledgement
or a timeout for actual frame readiness; the required native event is still open.
