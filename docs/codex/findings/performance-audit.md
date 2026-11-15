# Performance audit — 0.157

Ranked static findings and host evidence, 6 November 2026. No Shield CPU/heap/I/O/wakelock profile was collected;
there is no claim of a measured improvement in 4K playback. Source bounds and host tests establish mechanisms.

| Priority | Source / mechanism | Impact and suggested action | Status |
| --- | --- | --- | --- |
| 1 | PlayerController.setProgress calls updateClock/updatePreviewScrubber twice; scrubber measures/layouts/builds parts and refreshes strip | Repeated main-thread work each tick; screen owner should consolidate after HUD/device regression | Reported, screen untouched |
| 1 | PlayerService.updateNowPlayingMetadata decodes poster/provider thumbnail synchronously | Main-thread disk/provider/image work can stall input; prepare bounded bitmap on a source-owned worker then post identity-guarded result | Report only; notification artwork behaviour needs integration testing |
| 2 | PlayerService.sampleJourneyTime serialises coverage/preferences on regular progress updates | Repeated allocation/write admission during playback; measure actual latency/write coalescing before changing durable checkpoint frequency | Retain correctness; profile first |
| 2 | First schema-64 locator call checkpoints/copies/upgrades SQLite | Potential long first access, plus two database files on disk | Worker-only API documented; historical upgrade/rollback tests pass |
| 2 | Own provider content-URI source classification originally queried during setVideoURI | Main-thread database work and alias-ID misclassification | Moved to existing source worker; generation-fenced apply; real worker/provider regression |
| 2 | DP-10 DNS/transport setup could outlive cancel | Obsolete listing threads/sockets and cached clients competing with playback | Private process owns listing; 30s including admission, Stop kills that owner; host stalled/trickle socket tests preserve playback connection |
| 3 | Private process per listing + 100k-row/4M-character pipe bound | Startup/IPC overhead unmeasured; huge folders fail closed rather than permit partial uniqueness proof | Record Shield latency/heap; no fake success on truncated listing |
| 3 | Metadata/related/hero warmup and native metrics | Bound fan-out/cache; five-second frame sampling, minute event windows | Existing worker/cancellation fences; actual device decoder cost unmeasured |

Other lifetime review: next lookup cancellation now occurs on mode/source/stop/destroy; stale UPnP/subtitle/IntroDB
results cannot attach to a new source. The controller's retained parts strip and seek subscription need screen-owner
fixes. The process bridge avoids child restore/scan/telemetry startup and never shares playback network caches.
Wake locks and platform service scheduling still require device lifecycle evidence; host JVM termination is not an
Android service/firmware performance test. Suggested run: fixed local/SMB/DAV 4K clips with seeks, next lookup Stop,
mount loss and background/resume; capture Perfetto main-thread spans, heap after repeated source changes, wakelocks,
native coverage bits and transport setup/read windows. Compare identical APK/engine/build settings.

## Further full-read observations

- Diagnostics.event constructs/redacts JSON and touches session preferences under a class lock on its producer;
  export copies up to 32 MiB under the export lock. An important-event writer can wait while holding the class
  lock, so measure producer latency during exports. No lock-cycle deadlock or ANR was demonstrated.
- OmdbRatingsCache holds a static lock over a network fetch, serialising unrelated titles. Respect worker-only
  admission and measure queue delay before narrowing the lock and adding duplicate-request ownership.
- SharedMetadata.entry/PreviewMetadataCache.read can read files on bind; PreviewVariants initialisation enumerates
  codecs. FoundationAboutWorkspace technical rows run codec/StatFs work, and the post-copy subtitle-font list
  refresh invokes native font enumeration from the settings callback. Screen-owner worker integration is needed.
- TrustingOkHttp3Downloader builds a client/trust material for each image load, losing shared connection reuse.
  The trust policy is system plus bundled roots, not unconditional TLS acceptance. Profile then share owned clients.
- SubtitleDiscovery file-key construction stats the file for repeated offers; expired persisted discoveries are
  not globally pruned. PreviewRemoteDetailsActivity overwrites its Future, cancelling only the newest request on
  destroy, although UI callbacks check liveness. These are cost candidates without measured device regressions.
- Subtitle font signatures sum remote mtimes but copied file mtimes are local write times, potentially causing
  repeated copies. Compare real provider timestamps before changing the signature/copy contract.
- MediaLibraryBackupService prepares UUID-named private cache archives without its own retention sweep. OS cache
  reclamation remains possible; establish caller cleanup and bound retained bytes rather than claiming permanent
  storage leakage. None of these report-only observations changes a screen or durable behaviour here.
