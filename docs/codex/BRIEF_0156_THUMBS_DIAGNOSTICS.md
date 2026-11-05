# Codex brief — seek thumbnails (experimental) and a much larger diagnostics recorder (owner approved 2026-11-04)

Rules: `BRIEF_0155_MORE.md` rules 1–5 and `docs/codex/HANDS_OFF.txt` (read it first). Branch
`codex/0155-code-2026-11-04` only. No PR, merge, rebase, force-push or workflow dispatch. **No UI or design work:**
no layouts, colours, wording on screen or Settings rows — Claude does those. Finish `BRIEF_0155_BACKUP.md` first if it
is still open.

Context: `docs/0.155/OWNER_NOTES.md` items 33–39 (chapters/episodes strip, seek thumbnails) and
`docs/0.155/DIAGNOSTICS_2026-11-04.md` (review of the owner's report). The owner is the only user; diagnostics should
record **everything useful, in full detail**. Privacy cut-backs come later, if the app is ever shared.

## A. Seek thumbnails — engine only (experimental; the owner wants to see how it feels and performs)
1. **Engine** `player/SeekThumbnails.java` (+ helpers): for the video playing, a low-priority background job takes one
   small picture (≈320×180 JPEG, quality ~70) every **10 s** for local/USB files and every **45 s** for network files
   (SMB, WebDAV/Put.io, SFTP, UPnP), keyframe-only (nearest sync frame, no full decode run). Choose the extraction
   route that works for every source Supernova plays (MediaMetadataRetriever on the existing local stream proxy, a
   second libavos demuxer, or other) — compare them in `findings/seek-thumbnails.md` with measured numbers from tests
   you can run and say which needs Shield confirmation.
2. **Back-off:** pause while buffering, while the user is seeking, when the app is backgrounded/PiP, and when the
   last extraction took > 400 ms (double the interval, up to 2 min); never run two extractions at once; cancel on
   stop. Playback must never wait on it.
3. **Cache:** app cache dir `seekthumbs/<stable media key>/`, index file with timestamps; delete a video's set when it
   is marked watched; LRU cap **500 MB** total; survive process death.
4. **API for Claude's UI:** `nearest(positionMs) → Bitmap|null` (within ±1 interval), `frameAt(positionMs, callback)`
   for a priority one-off (the Chapters row uses it for each part's start + 2 s), and a listener when new pictures
   land. Preference key `seek_thumbnails_enabled`, default **true** (no Settings row — Claude adds it).
5. **Tests (JUnit/Robolectric):** scheduling per source type, back-off rules, cache index + LRU + watched cleanup,
   `nearest` lookup edges, cancellation.

## B. Diagnostics — measure the thumbnails
Events (all with media key, source kind, file size class, resolution/codec of the video):
`thumb_job_start/stop` (interval, reason), `thumb_frame` (extract ms, bytes read where known, JPEG bytes, position),
`thumb_skip` (reason), `thumb_error` (exception class + message + stage), `thumb_scrub` (hit/miss, distance to
nearest), `thumb_cache` (size, evictions); and per-session summary: frames made, mean/95th extract ms, total bytes
read, cache hit rate, **buffering events and dropped frames while the job ran vs while idle**.

## C. Diagnostics — expand considerably
1. **Fix first:** the recorder crash `Diagnostics.trimStorage` (`diagnostics/Diagnostics.java:1293`) — snapshot each
   file's `lastModified` before sorting. Test with timestamps that change mid-sort.
2. **Always full detail:** remove the Normal/QA level distinction in code (keep reading the old preference
   harmlessly); include exception **messages** and stack traces for every ERROR (existing credential/URL redaction
   still applies — extend it to tokens, `Authorization`, user names in URLs, OMDb/OpenSubtitles keys).
3. **Reasons for failures** everywhere a failure is logged without one: scan (`scan_failed`, `scan_batch_failed`,
   `scan_metadata_failed`, `scan_partial`: source kind, stage, exception), metadata (`metadata_semantic_failed`:
   endpoint category, HTTP status, body size, first parse error), backup/restore (`backup_started`, `backup_stage`,
   `backup_failed`: stage collect/write/verify/copy, category, row count, bytes, exception — see
   `BRIEF_0155_BACKUP.md` findings).
4. **Process exits:** record Android's `ApplicationExitInfo` (reason, description, importance, PSS/RSS) on the next
   start; only call an exit "unclean" when the reason is a crash, ANR or native crash; low-memory and user stops get
   their own names.
5. **Capacity:** raise the recorder budget (8 → 32 MiB) and queue so scans stop dropping events; record drops per event
   type.
6. **Playback:** decoder chosen (name, hardware/software, HDR/Dolby Vision profile), decoder retry/fallback (new in
   0.155), dropped/late frames per minute, A/V sync drift where the engine exposes it, buffering start/end with
   duration and read throughput, seek latency (request → first frame), audio output/passthrough format, subtitle
   track type and render errors, IntroDB/ChaptersDB lookups (hit/miss, latency) and skips used.
7. **Network transport:** per stream: protocol, open latency, read throughput per minute, retries, timeouts, HTTP
   status, bytes read; per metadata request: service, endpoint category, status, latency, body size (flag empty
   bodies like TMDB's 24-byte replies).
8. **UI:** slow frames (Choreographer frame > 32 ms) counted per screen, screen open times, `onTrimMemory` levels,
   heap/native heap every heartbeat, unhandled key presses (screen, focused control, key), and any `FocusFinder`
   exception caught by a guard.
9. **Settings changes:** key and new value (never secrets) so behaviour can be matched to settings.
10. **Report:** add sections to `summary.txt` for each area above, a "top 20 errors by count" table with first/last
    time and example message, and a per-session timeline of warnings/errors.
11. **Hands-off files** (scan, Network & Files, Put.io, Library Health, SupernovaStyle…): do not edit them; list each
    exact insertion point (file:line, event name, fields) in `findings/diagnostics-hooks.md` — Claude adds those.
12. Tests for every new event builder, the redaction rules and the summary sections.

## Before you finish
Update `docs/codex/HANDOVER.md`; write `docs/codex/reports/YYYY-MM-DD-0156-thumbs-diagnostics.md` (per part:
commits, tests, what needs Shield measurement, exact place to continue); confirm with
`git diff --name-only claude/supernova-github-access-yfhe89...HEAD` that no existing `.github/`, Gradle or hands-off
file changed. Commit and push.
