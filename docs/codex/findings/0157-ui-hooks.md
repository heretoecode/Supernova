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
Local and Put.io use their distinct owned admission APIs in the continuation section below.
Existing `LibraryScanController.stop()` still owns library worker cancellation.

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
physical size/type check. The continuation below adds missing-source derivation and local/import ↔ network/scanned ownership migration.
Do not infer physical ownership from the public ID; the guarded transaction retains that public identity.

## Personal videos and typed Library Health — items 8–9

The continuation below documents persisted PersonalVideoActions with Undo/HIDDEN backup and typed
LibraryHealthRepository rows/retry. Personal is separate from hidden; unmatched is separate from unreadable.

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
The transient journal is outside the backup settings allow-list. Discovery is described in the continuation below.

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
The continuation below supplies the seek-generation/output handoff contract for item 20.

## Continuation: completed source, role, health, discovery and seek contracts

This section supersedes the earlier partial-scope statements for items 3, 7, 8, 9, 11 and 20. Register the new
0.157 continuation patches in the report before compiling these calls. The physical media database is now **64**:
public library IDs and backing index IDs can differ. Always retain IDs as `long`; resolve the physical locator
rather than inferring local/network ownership from a public ID. The new store is `media-v64.db`. Old APKs continue
to open the retained schema-63 `media.db` rollback snapshot and do not see later 0.157 edits. Their inherited
downgrade helper can delete an unsupported database, so never give them the new store. The current helper uses
SQLite's normal refusing downgrade guard. Verified schema-62/63 raw backups migrate on a private replacement to
schema 64; the reviewed native archive is not changed. Logical backup categories remain the existing categories.

`VideoOpenHelper.getDatabaseFile(context)` is worker-only: its first call may checkpoint/copy/upgrade the old store
under a cross-process migration lease. Failure preserves the old logical data and throws; do not call during a
screen bind. `FileUtils.installDatabaseLocator(BiFunction<Context, String, File>)` is startup-only configuration,
installed by CustomApplication for legacy database-export callers. Its callback executes on the exporting worker.
Raw backup ZIP entries keep the historical logical name `media.db`; live replacement resolves the current file.
No old store is automatically deleted, so allow disk space for both versions during migration.

### Scan one local source and one Put.io scope — item 3

MediaLib `LocalSourceScan.request(context, fileRoot)` is main-thread safe. It returns false for a wrong source kind
or an already owned local-source request. Admission, filesystem inventory and Android MediaStore scanning happen
on workers; a non-registered/unavailable source finishes Failed. The selected URI must exactly match a configured
local library root. Descendants are inventoried recursively, with canonical containment, symlink-cycle protection
and a 100,000-entry bound. A watchdog stops admission after 60 seconds without filesystem/system-scanner progress;
accepted native importing uses the existing importer cancellation/lifecycle. Late
system callbacks after Stop/deadline cannot start importing. Android's system scanner itself is not killed.
The native importer reconciles/imports/extracts file metadata only under that root, with literal SQL wildcard
escaping, keyset paging and the existing cross-process scan lock. It rejects an ID owned by another import scope.
The same lifecycle broadcasts supply checked/total/change counts, aggregate activity and last-ten history.
Use existing `LibraryScanController.stop()`; active import stops through its existing service cancellation and
releases its busy/delete state in finally. Completion means the native inventory/metadata import has returned.

`PutioSyncScheduler.requestSource(context, sourceUri)` is main-thread safe. Only an exact saved API-owned scope for
the signed-in account is selected; parent folders and generic/WebDAV-owned scopes are not substituted. Disabled
or already busy integration admits no new request. Observe existing scheduler status and scan progress, rather
than treating the void return as acknowledgement. The selected scope owns a queued and exactly one terminal
history event even when token/account lookup fails. Existing Put.io client cancellation and aggregate scan activity
cover the whole job. Neither overload changes the saved source selection or scans neighbouring scopes.

### Personal files — item 8

MediaLib `PersonalVideoActions.set(context, StoredFile(id, exactStoredPath, size), personal)` is worker-only and
returns a `Change` for Undo. `undo(context, change)` returns false for a stale identity/revision/newer choice;
I/O/database errors throw. `isPersonal(context, id)` and `read(database)` are worker-only reads. The role is a native
per-file column, separate from identification, user-hidden state, watched flags and every playback-position column.
Movies/TV data loaders and automatic matching exclude personal files independently of show-hidden preferences.
Both local/network NFO imports and already running automatic match results respect the role. Manual Fix match
rechecks it inside the final database transaction after remote preparation. No metadata is erased by marking.

The existing HIDDEN category exports schema 2 with opaque physical personal-file references; schema-1 imports keep
existing roles. Unresolved role references remain pending and are applied before library loading/matching once the
physical file is indexed. Explicit unmarking supersedes a pending choice; Undo is revision-guarded. No locator,
credential, artwork or media byte is added to that default backup category. `reconcilePending(context)` is a worker
operation used by those engines, not a screen bind. Native role restoration is part of the existing media recovery
transaction; this does not create a new backup category or change its on-screen label.

### Health rows — item 9

`health.LibraryHealthRepository.read(context, kind, cancelledSupplier)` is worker-only and returns immutable rows.
Kind comes from MediaLib `LibraryHealthEvidence.Kind`: COULD_NOT_READ or NOT_MATCHED. Rows expose video ID, title,
reason, candidate count, whether that count is a lower bound, and evidence UTC time (0 means unknown).
COULD_NOT_READ reasons are DAMAGED, TIMED_OUT, UNSUPPORTED or UNKNOWN; an unclassified failure remains UNKNOWN.
NOT_MATCHED reasons are NO_MATCH or POSSIBLE. A limited search never invents an exact total: `more=true` means at
least the returned count. Successful native reads/manual matches clear their own evidence kind. Personal files
are excluded. Retry is `tryAgain(context, row, cancelledSupplier)` on a worker: false means stale/changed/not
retryable; exceptions mean admission failed. It requests the existing native indexing path and retains evidence
until an actual successful read, rather than clearing it on button press.

MediaLib `LibraryHealthEvidence.record`, `readFailure`, `recordMatches` and `clear` are producer APIs, worker-only
except the retriever's existing process-kill watchdog. They persist bounded evidence under noBackupFilesDir;
recording failure cannot turn a successful media operation into a failed one. Never send raw provider text as a
reason. Ordinary unmatched library rows with no stored provider result have count 0 and unknown check time.

### Find everywhere and owned file operations — items 7 and 22 (DP-10)

`MissingFilesRepository.findAgain` keeps its worker/progress/Stop/unique-match contract. Original saved-source
identity may now be absent: the exact stored ID/path/size still guards the old row. Cross-index moves transfer the
physical row between files_import/files_scanned in one SQLite transaction while retaining the public library ID,
identification, watched state, bookmarks, resume position and personal role. A local destination must have a
unique, size-matching MediaStore identity. An occupied destination is refused. Native import upserts preserve
retained rows even with recursive delete triggers enabled; old MediaStore-ID reuse allocates a non-conflicting
public ID within the existing positive local range. The old index no longer owns the relocated file.

`player.FileListingOwner.directory(context, uri, cancelledSupplier)` and `inspect(...)` are worker-only snapshots
used by recovery. They use the same private-process ownership as next-file listing, including setup, DNS, cached
SSH/SMB transports and actual UPnP requests. Stop suppliers must be cheap, thread-safe reads. The engine observes
them and caller interruption; each operation has a 30-second deadline. The complete recovery search retains its
60-second outer deadline and global uniqueness checks. Failed/unavailable roots do not permit a partial relink.
Returned MetaFile2 values are read-only snapshots: URI/name/type/MIME/size; editor/lister calls are unsupported.
The wire is bounded to 100,000 rows, bounded strings and four million name/URI characters; an incomplete pipe
never yields a usable partial list.

`UpdateNextTask` now owns cancellation before execute, including cancel(false). A private, non-exported service
process owns exactly one listing; it shares no network caches with playback. Its own UPnP service runs there too.
Private Binder cancellation executes off the main looper and stops only that process. A token rejects stale Stop
messages; Binder death and unbinding precede slot reuse. No external PID-kill API is exposed. Temporary credentials
cross only that private UID-checked Binder and are never saved or logged by the bridge. The process bypasses app
restore recovery, scan scheduling, playback, image warmup and account telemetry; only required transport setup runs.
Filtering/comparison/traversal and both result-post boundaries remain cancellable. Name ordering, repeat wrap,
remote queues, database preference and binge selection are unchanged. Screens still use the existing task API.
`get()` retains its result semantics; cancellation of a Future is not proof that the worker has ended.

### Network discovery — item 11

`connections.NetworkDeviceDiscovery.start(context, callbackExecutor, listener)` is main-thread safe and returns an
owned `Job`. `current()` is memory-only; `close()` fences queued callbacks and detaches discovery. Snapshots expose
LOADING_SAVED / DISCOVERING / FINISHED / FAILED / CLOSED and partial-failure state. A 25-second bound covers saved
connection loading plus discovery. Saved inventory failure never labels unknown devices New. Results are immutable
and carry URI, bounded name, kind and NEW/ADDED. SMB address plus advertised-name aliases recognise saved child
shares without DNS guesses. UPnP uses the existing device identity. Known kinds are SMB_SERVER and MEDIA_SERVER;
other UPnP types remain NETWORK_DEVICE. SMB discovery cannot reliably distinguish a NAS from a computer, so no
hardware kind is invented. `job.saved(alreadyLoadedUris)` refreshes membership after a successful connection change.
The job removes only its UPnP listener; it does not stop the shared service. No screen was changed.

### Seek picture loading — item 20

`Player.seekLoading()` returns `SeekLoadingSignal`. Observe with `observe(mainHandler::post, consumer)` and close
the returned subscription on disposal; `current()` is a synchronous memory read. Snapshot state is IDLE, WAITING,
READY, FAILED or UNAVAILABLE; `loading()` is true only for WAITING. Position is the requested seek position; chapter
index/title come from cached embedded chapter metadata, with index -1/empty title when unknown. No metadata/network
lookup happens while binding the ring. Source replacement, release, playback completion and errors clear ownership.

The new MediaLib/native bridge is `AvosMediaPlayer.seekToTracked(position, token)`, called by Player. Old native
archives safely return unsupported via the absent JNI entry; Player uses the existing seek and publishes
UNAVAILABLE. The Android MediaPlayer fallback also remains UNAVAILABLE, never a fabricated ready result.
The matching native patch carries each token through the queued seek, binds it to the actual decoded frame epoch,
and sends READY only after successful MediaCodec render submission or Android buffer queue. Dropped/stale frames,
queue failures, the one-second preview timeout and seek acknowledgements cannot produce READY. This proves an
image has reached the platform output boundary; it is **not a SurfaceFlinger/photon presentation timestamp**.
A paused seek without output remains WAITING until output/failure/Stop. No timeout clears the ring as if a picture
had appeared. The report lists native source compilation, host regressions and required archive registration;
no old native binary is relabelled as implementing this ABI.

### Native observations and transport operations — item 22

`Player.nativeFrameMetrics()` and `initializedNativeDecoder()` are worker-sampled reads of the current native
backend. MediaLib's corresponding `AvosMediaPlayer.frameMetrics()` / `initializedDecoder()` return null for an
older archive, unsupported/busy lifecycle or unavailable decoder. No caller may translate null into zero drops.
`NativeFrameMonitor.sample(values, decoder)` is the pure policy boundary; it rejects malformed/reset counters and
only calculates covered deltas. PlayerService owns five-second sampling, source/backend fences and cancellation;
an initial or minute sample enters the existing diagnostics session. Screens need no sampling timer.

Native metric coverage bits are 1 accepted output handoff, 2 instrumented late drops, 4 stale epoch drops,
8 scheduled A/V clock phase, 16 early drops. Values are totals plus the last monotonic submission timestamp and
scheduled phase. A physical presentation timestamp and hardware A/V drift are explicitly unavailable. The decoder
name is published only after its real initialisation. Instrumented native paths do not count every codec's internal
drop; an uncovered value is unknown. Known late/early deltas feed the existing thumbnail back-pressure policy.

FileCore `TransportOperationTrace.installFactory(factory)` is startup-only instrumentation configuration;
`begin(protocol)` owns one actual editor-open/read lifetime, `inOperation()` prevents duplicate legacy read hooks.
Sinks execute on the operation's I/O thread, must be fast and must not do provider/network work. They receive an
opaque ID and enum/numeric observations only, never an address, credential or exception message. Sink failure does
not break playback. Closing the wrapped stream closes the trace; caller interruption records Stopped. This API
does not cancel a transport by itself. Public `response(status)` and `retry()` hooks are called only at observed
transport boundaries; no missing status or invisible library retry is fabricated.

The app installs the factory and captures the playback diagnostic session at Requested, before connection setup.
Schema-3 `transport_read` events include Requested/Open/Response/Retry/FirstBytes/Window/Eof and one terminal
Closed/Failed/Timeout/Stopped event, setup latency, real HTTP status, explicit reconnect count and completed-read
bytes/read time. Minute windows are emitted on real read completion, including a final short window; a blocked read
does not emit invented throughput. Library-internal/auth/read retries outside explicit hooks remain unknown.
SMB/SMBJ/SFTP/SSHJ/FTP/FTPS/DAV/DAVS deep editor overloads preserve their original options and return semantics.
Direct legacy TransportReadObserver users retain schema-2 events; nesting never counts the same bytes twice.

MediaLib `UpnpServiceManager.useOwnedProcessService(serviceClass)` is process-start configuration, called only in
the private listing process before singleton creation. Other callers keep the normal shared service. The bridge
service's Binder and wire are internal and are not public screen APIs. Native handle fields are appended to retain
existing layout ordering, but the Java bridge, native headers and JNI must be built and reviewed together.

## Post-hunt internal follow-ups

No new screen call is required for DB157-01…04 or DG157-01…04. Preview snapshots now honour the same personal-role
predicate; history migration cannot replace a newer playback write; chart bodies are byte-bounded; metadata queue
admission failures release the worker. External-player init resets its row/duration and owns each async lookup
reply; callers keep using init/result as before. Unscoped IndexHelper.Listener implementation was removed from
that helper (no production consumer used it). Subtitle search/download keep the same worker-only synchronous APIs,
with one authentication refresh/retry per invocation. Font SAF copies keep the worker-only API and return count/-1,
rejecting unsafe provider display names before I/O. These changes require no screen or on-screen wording edit.

Native integration must apply avos-supernova157-atomic-alignment.patch after seek-and-frame-evidence.patch;
it aligns the new internal 64-bit atomic storage on all ABIs, including i686. The earlier patch bytes and Java
capability/API contracts are unchanged. See 0157-native-alignment.md and the registration receipt.

Final internal logger fixes DG157-10/DB157-07 require no screen call. Subtitle API requests/results and poster
clearing remain unchanged; TRACE uses the same 5s connect/10s read/15s call deadline as normal subtitle requests.

## Part B — 7 November 2026

These APIs are for 0.157 only. See 0157-b-integration.md for the source diagnosis, protected PiP removal map,
company provenance, missing pause archive and Shield acceptance packet. No new patch registration accompanies
Part B; the existing eight registrations and reviewed-native-archive requirement still apply.

### GB certificates (B1)

`metadata.AgeCertificates.Certificate(rating, reasons, seriesFallback)` is an immutable value. `null` is no known
certificate; empty reasons mean no supplied advice. Only U/PG/12/12A/15/18/R18 are accepted. GB is independent of the
service-availability country (whose legacy default is IE). No US/IE or unqualified indexed certificate is substituted.

- `parse(JSONObject, boolean series)` is pure; prefer a worker for a full provider response. It selects GB,
  preferring a film theatrical release, and retains only bounded string descriptors. Release notes are not advice.
- `remember(context, kind, id, response)` is worker-only; validates exact response ID, writes the private
  gb-certificates-v1 title store, and throws on invalid identity or failed persistence. StreamingRepository.metadata
  calls it after release_dates/content_ratings acquisition, including cached acquisition. Known advice survives a
  refresh with the same certificate and no descriptors. A successful no-GB response records none; a failed fetch
  does not erase previously stored advice.
- `read(context, kind, id, season, episode)` is worker-only disk access; use (-1,-1) for a film or series. A TV
  episode first uses exact episode advice, then returns its series certificate with seriesFallback=true. Do not
  present that fallback as a verified episode-specific BBFC decision. Locale changes do not change storage keys.
- `verifiedAdvice(context, kind, id, season, episode, certificate)` is a worker-only producer hook for verified
  advice; null clears only that exact scope. It is not a new BBFC lookup service. An absent episode lookup is not
  filled from an invented episode rating. Invalid kind/ID/episode scope throws IllegalArgumentException.
- `refresh(context, kind, id, season, episode)` is synchronous worker-only acquisition using the existing bounded
  metadata gateway, then read. It throws on unavailable uncached data/storage failure. There is no new owned Job
  or cancellation handle: caller owns its worker and suppresses superseded delivery; existing HTTP bounds apply.

Screens should preload/read on a model worker, then publish the immutable value on main. TMDB commonly supplies
no content-advice descriptors; reasons remain empty in that case. No token, path or authentication data is stored.

### Scrub previews and all part pictures (B2–3)

Existing `SeekThumbnails.nearest(positionMs)` remains main-thread safe and immediately returns a cached bitmap or
null. A miss now schedules/coalesces an extraction at the latest target, not just a disk lookup. It cannot block on
source setup/read/decoding. Existing enabled/buffering/seeking/background gates and close fencing are retained.
The memory listener signals a completed picture; use that existing callback to redraw. Put.io uses the playback
resolved URL in the independently owned extractor; callers must not substitute a guessed WebDAV URL.

`PlayerService.partFrameSource()` is a main-thread getter returning a bounded `DeferredPartFrames` bridge even
before the thumbnail engine exists. `PlayerStripSource.frames()` uses it now. `frameAt` queues across startup,
then delegates to the active owned SeekThumbnails. Close/source replacement suppresses old delivery. Standalone
DeferredPartFrames has no I/O; use it on main with a source whose callbacks post to main. At 256 pending requests
it returns a null bitmap for excess admission; after close it suppresses callbacks. `attach` accepts one source,
not a decoder replacement; replace/close the bridge for a new source.

`PartPictures` now admits four requests at a time so timelines longer than the engine's 256-request bound can
still request every part. It preserves the existing callback/fallback/close contract. A 300-part regression proves
all requests can finish after delayed startup. Codec failure still means no bitmap; no fake frame is generated.

`PlaybackParts.numberedChapters()` / `numbered()` are pure/main-safe copies. They number surviving CHAPTER intervals
1…n and replace only blank or generic Chapter N names. Authored names, IntroDB labels, kinds and time boundaries
are retained. The data adapter uses numbered(); no controller/view/resource text was edited. Marker-split ordinary
intervals count separately. The supplied rule is described for the screen owner in 0157-b-integration.md.

### Player episode ownership (B4)

Existing `PlayerService.previewSeasonEpisodes()` / `previewAdjacentEpisode(direction)` remain main-thread data
calls. They schedule a coalesced exact-series query on the service worker and retain its immutable result across
Home cache replacement. A matching Home snapshot can seed the initial response; an authoritative empty result
cannot revive a stale snapshot. Requests are keyed by source generation, physical ID and TMDB series identity.
Refresh/retry is at most every 30 seconds. Source replacement cancels the earlier query; stop/generation/destruction
fences its publish. Destruction cancels the provider signal and shuts down the worker. Result publication is on
main. A first unknown result can still be empty while local inventory loads; it is not an unavailable-series claim.

`PlayerEpisodeRepository.load(context, tmdbSeries, CancellationSignal)` is worker-only. It queries the local app
video provider with a bound series parameter and the personal-role predicate; filters configured exclusions; maps
actual physical IDs; and rejects more than 10,000 visited rows, interruption, cancelled or unavailable inventory.
CancellationSignal must be non-null. Existing season/hidden/version-choice rules still decide projection. Version
compatibility/health preference state is warmed on that worker before publication. These calls do not list remote
servers or probe the files. Existing metadata without a valid series identity produces no fabricated episode row.

### Separate track fields and per-series policies (B5–6)

`TrackDetails.of(language, format, channels, disposition)` and `audio(AudioTrack)` / `subtitle(SubtitleTrack)` are
pure/main-safe immutable projections. The typed-track factories require non-null metadata. Fields are language
(plain UK-English name, or blank), original, format, channels, sdh, forced and audioDescription. Known codec/channel
codes are expanded, TEXT is blank. Original/SDH/forced/description use native disposition bits, not track-name guesses.
Unknown fields remain empty/original metadata rather than becoming a made-up codec. Track selection/indexes are
unchanged. Screens must consume the separate fields rather than split an already concatenated chooser label.

`SeriesTrackChoices.enabled(series, audio)` / `setEnabled(series, audio, enabled)` select independent policies;
audio=true means audio, false subtitles. Existing remember/choice/matchingTrack now honour that side only. Missing
new flags fall back to the old :enabled bit, preserving every old choice JSON. Turning a side off retains its saved
description for re-enable. Legacy setEnabled(series, enabled) changes both sides; the legacy enabled(series) reads
the legacy bit and cannot describe a split choice. New screens must use the overloads. Invalid TMDB series IDs
throw; an absent/disabled/bad description returns null or matching index -1. These APIs access SharedPreferences:
warm/construct on a worker before main use; cold preference loading can block. Writes use the existing apply path.

### Clock stops and inactivity (B8)

`PlaybackLimits.stopAtEpochMs(choice, wallClockMs)` is a pure/main-safe projection for Off, +30, +60, +90 and end.
Recompute on the screen's clock tick so offered times move. -1 means no finite clock time (the enum distinguishes
Off/end). `selectedStopAtEpochMs(wallClockMs)` projects the fixed elapsed deadline onto the current wall clock;
redrawing choices never restarts the selected timer.

`configureInactivity(durationMs)` enables elapsed-time-without-input questions, zero disables, bounded to 24 hours.
Repeating the same configuration does not reset the deadline. `interaction()` resets it for real user input.
`configureEpisodeCounting(enabled)` permits episode-only, time-only or both, under the existing master switch.
Existing configureStillWatching(enabled, episodes) keeps its episode threshold and master enable semantics.
`inactivityQuestion()` identifies the reason. Inactivity counts elapsed time including pauses unless the caller
explicitly disables/reconfigures it; progress/render callbacks must never be counted as interaction.

The existing 60-second question timeout still stops. Keep watching on an idle question emits IDLE_CONTINUE and
continues the current video; it does not call completion. If an actual automatic episode boundary was held during
the question, approval emits the existing CONTINUE and releases that boundary. Old/superseded question callbacks
cannot stop a newer session. close cancels all timers and clears pending state.

`checkDeadlines()` reconciles elapsed deadlines after device sleep instead of waiting for Handler uptime delays.
PlayerService.playbackLimits() invokes it; call it before a new input after wake. The service's Handler scheduler
and event handling run on main; UI must call the service APIs on main. Class methods themselves are synchronized;
a custom Scheduler/listener must honour the caller's executor contract.

Service reads `still_watching_idle_minutes` (default 0) and `still_watching_count_episodes` (default true), retaining
the existing enabled/episode-count keys. `PlayerService.playbackInteraction()` is the main-thread hook for the
screen owner to call on actual remote input/source launch; it is intentionally not wired by editing PlayerActivity.
No settings row, option wording, focus, card layout or master-switch screen was changed here.
