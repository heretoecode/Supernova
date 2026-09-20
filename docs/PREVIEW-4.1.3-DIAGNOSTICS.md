# Supernova diagnostics

Advanced > **Diagnostic Logging** is Off by default. Enable before reproducing a
problem. **Export Diagnostic Report** uses Android's document picker to save a ZIP;
choose a location reachable from your file manager, then upload that ZIP for QA.
A missing document-picker application is reported, not silently worked around.

Logging uses a single reusable API with UTC milliseconds, monotonic elapsed time,
process UUID and correlated playback-session UUID. It records lifecycle/focus
boundaries, player state/error/track/seek/checkpoint events, metadata/provider
requests, scan state, browser failures and backup operations at their owners.
Repeated focus and scan sampling are throttled; there is no per-frame subtitle or
A/V logging. Checkpoint `submitted` means handed to the existing index helper,
not an independently verified database commit.

Storage is private app storage: five event files and two recent-playback files,
each up to 256 KiB. A 256-entry asynchronous queue may drop events under pressure.
Logging failures do not abort the underlying operation. Java uncaught exceptions
are recorded before delegating to the installed crash handler. Native crashes or
OS process kills may have no exception trace; preceding events remain useful.
Disabling stops new events; existing bounded history remains exportable.

The report contains those files, allow-listed build/device identity, decoder names
and supported MIME types, and a README. It deliberately excludes preference dumps,
database/media files, normal logcat, credentials, authentication headers, tokens,
API keys, raw media locations and exception messages. Exceptions retain only types
and stack locations. Session IDs are random diagnostic correlation IDs, not tokens.

Never pass credentials or arbitrary user/network text into the event API. URLs and
paths are stripped as defence in depth, and sensitive field names are rejected.
Only known numeric/enumerated state belongs in instrumentation payloads.

## Investigation boundaries

The original Shield Information → File & Technical Details restart has a recorded
navigation sequence but no supplied stack trace. The Preview path now displays
a read-only metadata snapshot inside the active player rather than launching another
Details activity, serialising live metadata and potentially probing the stream
again when metadata is unavailable. Missing metadata has an explicit non-probing
state. This is a lifecycle correction, **not proof of the original native/Java
exception cause**. Inspect physical diagnostics if the failure recurs.

Long-duration termination, A/V sync and progress after abnormal exit remain
**NOT TESTED — USER QA REQUIRED**. Position/checkpoint and audio-delay events are
not measurements of native audio-versus-video clock drift. Existing progress and
completion guards are retained.
