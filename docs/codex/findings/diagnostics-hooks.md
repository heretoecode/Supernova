# 0.156 diagnostic hooks and remaining coverage

Code checkpoint `207242079`. App line numbers below refer to that checkpoint.
`M:` means the prepared MediaLib stack (locked baseline, existing patches, A scan,
C NFO, D retriever and metadata diagnostics). `F:` means FileCore baseline
`c4b760c55102c72d45f68b0d16999fc1f4f013a7` plus the existing dependency-patch inventory.
Method names are the stable insertion anchors when Claude's parallel edits move lines.
No files in the screen-hook table were edited in Part D.

## Installed engine hooks

- `Diagnostics.java:108`: 32 MiB retention; routine queue 8192, important queue 2048.
  Named writer tasks count rejected submissions by event type. Heartbeats and report
  show these counts; one rejected sink write is not proof the other sink lost the event.
  `clearAll` clears the new counters as well as persisted/queued old evidence.
- `Diagnostics.java:1491`: `error(event, throwable, fields...)` retains actual class,
  message and the complete cause/suppressed chain after shared redaction. Individual
  text fields are capped at 256 KiB with an explicit truncation marker; records at
  1 MiB. ERROR callers that supplied no throwable get a **reporting call-site** trace,
  labeled `trace_origin=reporting_call_site`; this does not invent the missing cause.
- `Diagnostics.java:1182`: only OS crash/native crash/ANR reasons become
  `previous_session_unclean_exit`. Low-memory/user stops have separate events.
  Missing clean marker alone is `previous_session_without_clean_marker`. OS reason,
  description, importance, PSS/RSS, timestamp, PID and process are retained.
- `Diagnostics.java:441,480`: component memory callbacks, created-to-resumed screen
  timing, Choreographer callback deltas >32 ms per screen/minute and pause summary.
  These are UI scheduling measurements, not decoded-video frame counters. Existing
  heap/native heap heartbeat and focus guard instrumentation remain active.
- `Diagnostics.java:1074`: default preference key/new value, with entire secret
  values redacted. No settings screen changes. Redaction covers credential URLs,
  token/API-key pairs, escaped JSON secrets and Basic/Bearer Authorization.
- `PlayerService.java:370`: real buffering start/end duration and thumbnail active/idle
  attribution; `Player.java` forwards backend buffering INFO events. Buffer percentage
  remains a separate progress observation. It is not treated as a stall.
- `PlayerService.java:2661`, `CodecDiscovery.java:483`: decoder type, codec/HDR/DV
  metadata; candidate name/software flag; native candidate index >0 records retry;
  candidate exhaustion records fallback request. Candidate selection is explicitly
  not confirmation of successful initialization or the final native decoder name.
- `PlaybackDiagnosticState.frame`: one render callback after a paired seek callback,
  with request-to-render latency and PROXY quality; stale/overlapping seeks cannot
  claim a measured first frame. Surface/native backends without that callback need
  the native hook below. `audio_output` is selected track/configured passthrough,
  not a claimed hardware sink format. Subtitle kind/format/external flag and Canvas
  render exceptions are recorded. PlayerActivity logs unhandled down-key events.
- `PlayerService.java:2385` and `ChaptersDbPlayback`: lookup hit/miss/latency and
  segment skips used. `StreamingRepository.parseMetadata:236` records endpoint
  category, exact HTTP status/body bytes and first parse reason. Request results for
  TMDB, OMDb and ChaptersDB include status, size and latency; tiny bodies are flagged.
  The new `medialib-supernova156-metadata-diagnostics.patch` also supplies request-scoped
  IntroDB app/org status, parse exceptions, duration and decoded-UTF8 body-byte measurements,
  with an optional app-installed observer. It never reads global LAST_QUERY_RESULT as
  evidence for another request. The observer is disabled with diagnostic logging.
- `utils/backup/BackupDiagnostics`: nested collect/write/verify/copy/apply scopes
  retain the last stage, category, row count, bytes and actual underlying cause.
  Counts/bytes refer to payload bytes during archive work and actual bytes during
  provider copy; `-1` means unavailable for a preference/database write. Existing
  rollback/publication semantics are unchanged. Restore apply is instrumented in
  `PreferenceRestoreTransaction.java:954`; screen-side preview errors need the hook below.
- `DiagnosticExpandedSummary`: all requested area sections with counts and recent
  measured details, top 20 errors with count/first/last/example, and per-session
  warning/error timelines. These describe retained evidence, not entire missing sessions.

## Screen hooks for Claude (protected files)

Use `Diagnostics.error(name, caughtThrowable, fields...)` wherever a catch is available.
Do not log raw credentials, requests, response bodies or entered password values.
Keep existing key/focus decisions intact; emit unhandled-key events only after an
actual false result. No per-draw logging in shared styling.

| Exact insertion anchor | Event and fields |
| --- | --- |
| `leanback/filebrowsing/UniversalFileBrowser.java:1761` `open(Uri)` | `network_browser_open`: structural screen, source_kind, monotonic start; finish after rows land with duration_ms and row_count. |
| Same file `:1870,1902,2365`, existing catches | `network_browser_failed`: source_kind, stage (`resolve/list/render` as appropriate), operation_id plus actual exception. |
| Same file `:2681` `dispatchKeyEvent`, false return at `:2876` | `ui_unhandled_key`: screen, semantic focused control, key_code, repeat; ACTION_DOWN only. |
| `leanback/PreviewNetworkWorkspace.java:31` `update` | `network_workspace_updated`: volume/source/bookmark counts and elapsed time for this update; no locations or credentials. |
| `streaming/putio/PutioBrowserController.java:122` catch and `:150` `load` | `putio_browser_failed` / `putio_browser_load`: operation_id, listing stage, safe enum reason/status if available, page/row counts and duration; actual caught error. |
| `streaming/putio/PutioBrowserActivity.java:12` create/back callback | Existing global activity sampler covers frame/screen timing. Add unhandled-key logging at the Activity dispatch boundary if overridden; do not change back behavior. |
| `leanback/LibraryHealth.java:153` count worker completion | `library_attention_count`: indexed/metadata/media/source issue counts and duration. **Counts-only ownership remains with this helper; do not add traversal to landing render.** |
| `leanback/LibraryHealthPanel.java:44` `show` | `ui_panel_open`: screen=`library_health`, category index and completed render duration. |
| `leanback/PreviewLibraryScan.java:106` existing intent forwarding | A already forwards source_kind/stage/failure_class/message/trace. Preserve these when merging screen work. Add `scan_batch_failed` only when the batch itself failed, with batch_id/source_kind/stage and actual throwable if present. |
| `leanback/PreviewLocalScanState.java:59`; `PreviewScanProgress.java:9` `accept` | Preserve A's local failure fields; `scan_progress_snapshot` may add checked/added/updated/terminal/failed counts after accepting the batch, without duplicate source completions. |
| `leanback/PreviewNetworkScanning.java:40–67` scan settings callbacks | Default preferences already log key/value centrally. Only add a scan operation/trigger ID when scheduling; no second secret-bearing preference dump. |
| `leanback/settings/BackupOperationModel.java:121` worker catch | `backup_failed`: phase, stage=`collect/verify/copy/apply_preview` mapped from Phase, selected categories, recovery_required and actual throwable. Engine export/import/apply failures are already covered; this also catches legacy/preview paths. |
| Same file `:255` prepare restore, `:289` apply | `backup_stage` for canonical preview category/rows; preserve engine operation IDs. Do not replace existing failure messages or picker ownership rules. |
| `leanback/settings/SettingsWorkspace.java:800` dispatch and `leanback/PreviewPages.java:1257` dispatch | `ui_unhandled_key` after false result; screen, semantic focus, key_code and repeat. Default preference writes are already observed. Any separate preference stores need `configuration_changed` using `DiagnosticRedactor.settingValue(key,value)`. |
| `player/PreviewPlaybackMenus.java:204` dialog key listener; `player/PlaybackSubtitlePanel.java:358` `rowKeys` | Add unhandled-key evidence at the enclosing final dispatch result, avoiding duplicate per-row false results that later get handled by a parent. Existing FocusSearchGuard records actual caught finder exceptions. |
| `leanback/SupernovaStyle.java:237` `applyButtonGroup`, `:260` `styleContainer` | If component rebuild diagnostics are needed, call existing `Diagnostics.uiRebuild` once at the owning panel rebuild, with structural surface/reason/item counts; do not log `Drawable.draw` or add focus behavior here. Global Choreographer sampling already covers slow frames. |

## Dependency/native hooks requiring integration

These are explicit coverage gaps, not measured zeroes. FileCore/build/native files
remain outside this Part's edit scope. The D MediaLib patches add retriever
ownership/sync extraction, UPnP cleanup and the available IntroDB Java HTTP observer.

| Exact insertion anchor | Required event / fields |
| --- | --- |
| `F:filecorelibrary/TransportReadObserver.java:25` `Observed.readResult` | Add a periodic boundary at >=60 s with cumulative bytes/calls/read_ns/elapsed_ns; app can derive per-minute deltas. Current observer emits open/first/eof/terminal only, so current `transport_read` throughput is correctly labeled lifetime-at-boundary. |
| `F:.../TransportReadObserver.java:16` factory/observe contract; protocol editor before opening input | Add stream_id plus open latency before returning the input; current observer starts **after** open and cannot measure it. Extend optional sink contract without passing URL/user-info. |
| `F:filecorelibrary/ftp/Session.java:165,245`, retry catches `:172,252` | `transport_retry`: protocol, stream/request ID, attempt=1, stage=connect, elapsed, exception; explicit timeout subtype. Lines are before the separate 155 FTP patch; use the named methods when integrating that prerequisite. |
| `F:filecorelibrary/webdav/WebdavFileEditor.java:78` `call.execute`, and WebdavUtils `:239,300` | `transport_http_response`: stream/request ID, protocol, real status, open latency, bytes when read and real retry count. Current Java read wrapper cannot know status/retries. |
| `F:filecorelibrary/webdav/WebdavRawLister.java:57`, WebdavListingEngine `:165` | `network_listing_failed`: listing stage, protocol, status from SardineException and actual exception, linked to scan/browser operation. |
| `streaming/putio/PutioReadClient.java:260–293` response/catches | `metadata_request_result`: service=putio, endpoint category, status, duration, actual bounded body bytes, safe enum failure reason. This is listed for Claude's coordinated Put.io work; Part D did not edit Put.io files. |
| `M:mediascraper/AutoScrapeService.java:570,654,1683`, worker catches | `scan_metadata_failed`: job/batch ID, source_kind, phase/stage and full exception via a MediaLib observer. A records enqueue/handoff failures; asynchronous scraper-worker causes need their own observer, not a stale caller-thread ScanFailure. |
| `M:medialib/AvosMediaPlayer.java:606` MEDIA_INFO forwarding; IMediaPlayer INFO contract near `:187` | Extend the native-to-Java contract for actual selected decoder name, initialized/fallback result, dropped/late cumulative counters and A/V drift with native timestamp/units. Current VIDEO_TRACK_LAGGING is only a notice, not a count. Feed measured dropped deltas to `SeekThumbnails.droppedFrames`, emit `playback_frame_metrics` per minute with window_ms and availability flags. |
| `M:medialib/AvosMediaPlayer.java:555` buffering callback and the native event producer | If a backend emits only percentage progress, add actual stall-start/end events. Do not infer a stall from percent<100. Forward real INFO buffering events to the installed service hook. |
| Same MEDIA_INFO bridge, actual post-seek presented-frame native callback | `seek_first_frame`: matching seek generation, requested/achieved position, monotonic request-to-present latency, quality=NATIVE/PRESENTATION only when actually available. Existing texture callback metric stays PROXY. |
| Native audio/subtitle bridge feeding Player | `audio_output`: actual sink format/passthrough active state; `subtitle_render_error`: track kind, renderer stage, native error code/message. Current Java hook records selected input track/configured passthrough and actual Canvas exceptions only. |

Shield acceptance: exercise process crash/ANR/low-memory/user-stop separately; export
under a 61k-file scan; inspect per-type rejection counts and recorder memory; check
native/proxy coverage flags rather than interpreting unknown counters as zero.
