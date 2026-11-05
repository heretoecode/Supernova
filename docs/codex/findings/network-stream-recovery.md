# B17a network stream recovery — 0.156 addendum

Code ready `afb3a755d`, on `codex/0155-code-2026-11-04`.
This item was added to the Claude brief at `c37bd36d2` during the original A–D work;
D was pushed at `edc5a4281` before continuing this addendum. PortableCustomPage was
not touched. Cleanup remains parked.

## Observed failure and implemented boundary

The owner's second report shows a Put.io/WebDAV connection idling for 223/235 seconds
while paused, then failing on resumed reads. Reconnecting only the byte stream lets
the existing demuxer/audio/video state continue across a discontinuity.

The app now treats recovery as **transport plus decoder replacement**. A network pause
longer than 60 seconds starts a fresh source at the saved position before playback.
A current-backend proxy read failure or known native I/O/timeout error takes the same
path. Failed generations reject further opens/reads. Old decoder release completes on
a worker before a replacement opens, and source-generation checks reject stale callbacks.

Position preference is latest pending user seek, then a recent render-callback playback
clock, then the live playback clock, with a retained service checkpoint if native clock
access has failed. Positions are clamped below duration. Quality is recorded rather
than claiming an unavailable native presentation timestamp. Track/delay/subtitle resume
context and the existing session identity survive; recovery does not mark watched,
advance the playlist or reselect the initial database resume point.

Native `setStartTime` supplies a pre-prepare seek when supported; otherwise the
replacement waits for the actual seek-complete callback and surface readiness before
starting. User pause/play intent survives recovery, including pause/resume while the
seek is pending. Stop/Back/source replacement cancels pending reopens. Recovery times
out at 30 seconds; automatic read failures have at most two recovery admissions in a
30-second window. Intentional pause reopens do not consume that failure budget.

## Read-ahead and source coverage

Apply the new `medialib-supernova156-network-recovery.patch` **after all four patches
listed in the D report**. It adds:

- `PlaybackReadGuard`: one failure/paused-budget state per playback generation.
  Paused reads share a 64 KiB allowance across all ranges, then wait without allocating
  more buffers. Resume/cancel wakes waiters. This is a limit at the Java reader boundary;
  provider/kernel/native buffers already in flight remain bounded by their existing limits.
- `PlaybackReadEvents`: current owner identity and late-registration pause state;
  metadata or obsolete backend failures cannot initiate playback recovery.
- `RecoverablePlaybackStream`: a **new MediaLib source in the FileCore package** using
  FileCore's existing package editor and buffer-size seams. No FileCore source is edited.
  It wraps only the primary media, preserves normal per-backend refill policy and range
  options, and excludes subtitle/poster requests from recovery.
- SmbProxy playback installation registers the guard; stop abandons it before closing
  the proxy. Retriever/metadata readers remain independent. Avos direct proxy adoption
  uses the existing release-fenced install method.

This guards the FileCore playback routes used for SMB/SMBJ, FTP/FTPS, SFTP/SSHJ and
WebDAV/WebDAVS, including the owner's Put.io source. Native-only URL readers with no
Java gate are retired on pause and reopened on resume, including short pauses, to cap
ongoing read-ahead. Their **reported** native I/O/timeout failures also trigger full
replacement. A native engine that silently retries internally without a Java read or
error callback is not observable from this bridge; confirm those native/UPnP/HTTP routes
on Shield and use the native callback contract documented in diagnostics-hooks.md if
additional error forwarding is needed. No direct/native transport success is inferred
from the fake-server test.

## Diagnostics and tests

`stream_reconnect` records operation ID, source kind, idle_ms, cause, checkpoint and
position quality, recovery_ms and result (`started`, `ready`, `ready_paused`,
`resumed_first_frame`, cancellation/failure). Read exceptions and backend numeric
codes/messages are retained through the redactor. Reconnect boundaries use important
retention and appear with measured fields in the Playback report section. Thumbnail
work pauses during replacement, resumes when ready/cancelled, and closes on terminal error.

Six new **host JUnit tests pass**: pause threshold/original timestamp, retry admission,
known native error mapping, a real local fake server dropping an idle response then
accepting a fresh connection, shared paused read allowance/late owner registration,
and intentional-close/late-input ownership. All 19 D host regressions also pass
(**25 tests total**).

Five Player Robolectric tests are supplied for old-release-before-open, seek-before-play
with a rapid pause/resume, presentation/latest-seek checkpoints, stop fencing and proxy
owner rejection. Two MediaLib adapter Robolectric tests cover sidecar isolation and
unchanged jcifs refill policy. These are unrun here. Java 17 parser checks and a focused
API type check against real MediaLib interfaces/FileEditor/ReadOptions passed with
small stubs for surrounding dependencies. No Gradle or full Android build was run.

Shield sequence: reproduce the owner's 3–4 minute pause on Put.io, inspect exactly one
fresh reopen before play, then interrupt an active read and verify replacement from the
checkpoint with synchronized audio/video. Repeat during a seek, while rapidly toggling
pause, in PiP/background, during audio-route changes and while stopping. Check paused
backend read/memory growth and the 30-second failure timeout. Repeat SMB, SFTP, USB/local
(no network recovery), and native HTTP/UPnP. Build registration and device acceptance
remain Claude's.
