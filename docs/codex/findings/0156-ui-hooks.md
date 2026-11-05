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
