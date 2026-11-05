# Seek thumbnails — extraction choice and measurements (0.156)

Code checkpoint: `207242079`; branch `codex/0155-code-2026-11-04`.
This is an experimental engine. Claude owns the scrub bubble, chapter pictures and switch row.

## Extraction route

The selected reader is an **owned AndroidMediaMetadataRetriever**, through the existing
MediaLib FileCore/UPnP proxy. Apply the new
`.github/build/medialib-supernova156-keyframe-retriever.patch` after the Part A scan
and Part C NFO patches. Apply `medialib-supernova156-metadata-diagnostics.patch`
next to install the IntroDB recorder bridge used by Diagnostics. Existing build files and the fork were not changed.

| Route | Evidence | Decision / device check |
| --- | --- | --- |
| Android retriever + existing proxy | Platform `OPTION_CLOSEST_SYNC`; API 27+ scaled sync frame; older platforms scale the returned sync bitmap. The same proxy already routes SMB/SMBJ, FTP/FTPS, SFTP/SSHJ, WebDAV/WebDAVS, content and UPnP; resolved HTTP streams also work through the platform reader. | Selected. Confirm Shield codec/container/range-request support for each source, especially HEVC/DV and large MKV. No Android timings were available here. Unsupported platform extraction emits an error and keeps the still/placeholder. |
| Second libavos retriever | At native commit `5cf3ae9c1f70189d4180a62722150fa76b405a0f`, `avos_media_metadata_retriever.c:314–327` accepts `option` but does not pass it to `getframe`. `thumb_stream.c:226–238` starts a thumbnail stream at a target; no sync-only contract is exposed. | Not selected: passing CLOSEST_SYNC to this JNI method would not establish keyframe-only decoding. Needs a native contract before it can be a fallback. |
| Accurate/full decode seek | Host comparison below decodes forward from a preceding keyframe. | Excluded from production because the brief requires sync-frame extraction. |

The API/source protocol route is available for all of the app's source kinds. Codec
success on every format played by libavos is **not established** by this API check.
There is no silent full-decode fallback. Platform retriever cancellation is best-effort:
stop releases its owned proxy/reader on a separate cancellation executor, with a
20-second cancellation deadline. A vendor call that ignores release can occupy the
thumbnail worker; it never makes playback wait. Confirm this behavior on Shield.

## Measured host comparison

Container FFmpeg `7.1.5-0+deb13u1`; generated 30-second H.264 1280×720/24fps MP4,
fixed 240-frame GOP (10 seconds), 23,967,005 bytes. Seven warm-cache runs per route;
wall time includes process launch and 320×180 JPEG encode. These are **desktop
FFmpeg measurements**, not measurements of Android, libavos, network I/O or Shield.
The JPEG benchmark uses FFmpeg q=5; production uses Android JPEG quality 70.

| Host route | Seven runs, ms | Median | Maximum | JPEG bytes |
| --- | --- | --- | --- | --- |
| Sync-only at nearest keyframe 20 s | 54.26, 49.93, 48.35, 47.22, 47.90, 47.65, 50.06 | 48.35 ms | 54.26 ms | 5,983 |
| Accurate frame at 17.9 s | 121.76, 130.65, 127.58, 134.72, 124.57, 127.36, 127.07 | 127.36 ms | 134.72 ms | 6,107 |

Reproduction (repeat the last two commands seven times, timing externally):

```sh
ffmpeg -f lavfi -i testsrc2=size=1280x720:rate=24 -t 30 -c:v libx264 \
  -preset ultrafast -g 240 -keyint_min 240 -sc_threshold 0 -pix_fmt yuv420p fixture.mp4
ffmpeg -skip_frame nokey -noaccurate_seek -ss 20 -i fixture.mp4 \
  -frames:v 1 -vf scale=320:180 -q:v 5 sync.jpg
ffmpeg -ss 17.9 -i fixture.mp4 -frames:v 1 -vf scale=320:180 -q:v 5 accurate.jpg
```

## Engine behavior and evidence quality

- One low-priority serial I/O worker across playback sessions; a separate kernel
  lease prevents simultaneous thumbnail extraction across app processes. Extraction
  uses a second reader/proxy and never seeks or closes the playing decoder.
- Local/USB/content base interval 10 s; network 45 s. Buffering, seeking, background,
  PiP and the off switch suppress extraction; ordinary background sampling also
  pauses while playback is paused. A priority `frameAt` can run while paused.
- More than 400 ms doubles the interval to at most 120 s. The resulting back-off
  also delays queued priority requests, so opening a long chapter list cannot
  defeat slow-extraction back-off. Duplicate priority targets share one extraction.
- JPEGs are bounded to approximately 320×180, quality 70; at most 32 decoded frames
  are held in memory. Cache is `cache/seekthumbs/<SHA-256 media identity>/`, with
  timestamps and owner digest in `index.json`, atomic partial-file publication,
  restart recovery from owned JPEG filenames and a 500 MiB video-set LRU cap.
- Completion and explicit watched commands remove the set. Bulk commands collect
  owners and scan the cache once; private-mode explicit watched commands still
  clean cache without writing viewing history. Local raw/file URI owners normalize
  identically; query-bearing remote identities remain distinct.
- UI `nearest` is memory-only; a miss coalesces an asynchronous disk lookup.
  `frameAt` prioritizes an exact requested target, and listener delivery is on main.
  Closing fences queued bitmap/listener callbacks. Service owns engine lifetime.
- Android's retriever does not return an actual sync-frame timestamp or byte counter.
  Index positions are **requested sync targets**, not proven frame presentation
  timestamps; telemetry records that quality and uses `read_bytes=-1` when unknown.
  Native dropped-frame deltas and A/V drift are also unavailable in the current
  Java bridge. Buffering comparison uses actual buffering-start callbacks; UI
  buffer percentage is not misrepresented as a playback stall.

## Validation and Shield sequence

Policy/cache/telemetry tests pass on the host. `SeekThumbnailsTest` supplies buffering,
priority/coalescing, interval edges, source classification, local-owner normalization,
stop/cancellation and callback fencing tests for Robolectric. The MediaLib patch
supplies late-proxy/release ownership tests. Those Android tests remain unrun here.

API type checks passed against Android API classes, real MediaLib retriever interfaces
and the real cross-process lease, with small stubs for surrounding app dependencies.
They do not constitute a full Android build or native extraction test.

On Shield: test USB/local, SMB, WebDAV/Put.io, SFTP and UPnP; each with H.264 and
large HEVC/DV media where available. Scrub during buffering, rapidly seek, open many
parts, background/PiP, toggle the switch, stop/reopen, kill/restart the process,
mark watched singly/in bulk, and approach 500 MiB. Export diagnostics and compare
`thumb_session_summary` buffering while extracting/idle, latency/back-off and read
quality. A missing native dropped-frame counter must remain explicitly unavailable.
