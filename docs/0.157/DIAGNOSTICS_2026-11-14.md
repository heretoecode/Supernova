# Diagnostics, 14 Nov 2026 (export 23:49 local time)

Read by Claude on 15 Nov 2026 at the start of 0.157 coding (owner rule: diagnostics before coding). The export itself
is not stored in the repository. Covers about a week (important logs from 7 Nov); 132 errors, 65 warnings.

## Findings to fix in 0.157

1. **Seek previews: the cause (P10).** Every preview attempt fails (`thumb_error`, 28 times, all on the Put.io
   WebDAV source, stage `sync_frame_extract`): `NullPointerException: Map.size() on a null object` inside Android's
   `MediaMetadataRetriever.setDataSource`, called from `KeyframeExtractor.extract` → MediaLib's
   `AndroidMediaMetadataRetriever` → `Proxy`/`SmbProxy.setDataSource`. Android's call with a URL needs a headers map
   and gets `null`. Fix: pass an empty map (or the source's real headers), then check local files too. Together with
   the hidden Settings switch, this explains "no previews anywhere".
2. **Put.io scheduled scans keep failing** (`scan_batch_failed`, 66 times): "Source did not complete" from
   `NetworkScanLedger.finish`, trigger `native_scheduler`, Put.io (WebDAVS). With the minute-by-minute schedule found on
   13 Nov, these failures repeat all day. Fix with the scan rules (one at a time, not within 15 minutes, a launch
   limit) and find why the source never reports complete.
3. **A dead WebDAV connection still scanned:** `scan_failed` "Error contacting … (404)" at root resolution for a
   second WebDAV source (the connection the owner stopped using). Files should mark it Not reachable and stop
   scheduled scans of it; Library Care tidies its titles (history kept).
4. **Recent scans store corrupt:** `scan_history_load_failed` (UTFDataFormatException "malformed input"; "Trailing scan
   history data") in `ScanHistoryStore.load`. Make writing atomic (write a temporary file, then rename) and recover from
   a bad file instead of failing.
5. **Scan lock interrupted at start-up:** `scan_failed` "Scan lock wait interrupted" (`ScanProcessLock.acquire`,
   trigger `startup`): two scans racing at launch, again the scan rules.
6. **Artwork:** 149 of 1,035 artwork requests failed; one local poster (`media_id 292`) fails every time with
   FileNotFound. Check missing local artwork files are re-fetched rather than retried forever.
7. **Memory:** process size samples reach about 330 MB in this export (750 MB on 10 Nov); the memory item stays, with
   per-page measurement first.

## Not a fault
- Start-ups every 15 minutes overnight are the background scan scheduler starting the process (no screen).
- `android_process_exit` reason 2 (the system ended the process) appears 7 times; nothing points to a crash.
