# Preview 4.1.4 upstream review and ports

Authoritative baseline: Video f6c864064a2d0927258994b478598320290b1596 (Preview 4.1.3).
The application label is 6.4.63, but its pinned dependency graph already includes
6.4.64 FileCore c4b760c55102c72d45f68b0d16999fc1f4f013a7 and AVOS
2cf21c486c7bf244c49b78d2220197a6d75396dd. The source already includes the
6.4.64 rescraping/network-credential refactor. Reapplying those stable changes
would duplicate work. Upstream release manifests were compared directly.

No wholesale 6.4.66 rebase, upstream presentation changes, FFmpeg 9 change,
or replacement dependency graph has been made.

| Upstream change | Application | Why / affected sources | Risk and evidence |
| --- | --- | --- | --- |
| AVOS 9c9f6bb036edab93f54a960464b64ba5141029b1 | Ported against its direct parent, the pinned baseline | Pause/resume/seek clock preservation, pending PCM output, AudioTrack retry. Include/audio_interface.h, Include/stream.h; Source/audio_interface.c, audio_interface_audiotrack_java.c, codec_sfdec2.c, stream.c, stream_audio.c, stream_sync.c, stream_video.c; three upstream explanatory docs | Native clock/audio changes carry device risk. Applied cleanly; rebuilt for armv7, arm64, x86 and x86_64. Shield A/V/passthrough verification is required. |
| AVOS 0f40416fad865927043a13cb5f030a49d498896b | Selected error semantics adapted; broad patch NOT imported | Source/stream_parser_ffmpeg.c: capture av_read_frame result; distinguish EAGAIN/EINTR, abort, real EOF and fatal error; surface pb->error; bounded numeric read-error diagnostic | Physical logs establish false completion but lack the triggering native errno. This addresses a real compatible defect, not proof of the original trigger. No percentage or elapsed-time completion guard. |
| AVOS 1aea10b53228535063080522b602a743d0569943 | Selected drain/error semantics adapted | Drain queued packets before existing VE_FILE_ERROR; prevent next-stream transition swallowing parser_error; clear error after successful seek; stop seek on fatal parse error | Preserve existing stream-end/error callback ordering; long network playback still requires Shield QA. |
| FileCore c9639d9f270be17a5cf81d8ca28327cb4e7104cc | Ported; duplicate a669cd3 not applied again | StreamOverHttp.java: HTTP proxy ranges, unknown lengths, cancellation | Applicable to protocol streams that pass through Nova's HTTP bridge. Compiles against pinned libraries; real SMB/SFTP/WebDAV byte-range and seeking QA required. |
| FileCore c62efcfc8a1231007785e9b7f68d5539e88ad5d6 | Ported | webdav/WebdavFile2.java, WebdavUtils.java, WebdavSecurityAndRangeTest.java: allprop fallback and redirected href deduplication | Preserve known-passing WebDAV scanning; physical server compatibility remains untested. Upstream test source included but do not claim it ran unless listed in final evidence. |

Patches: `.github/build/avos-preview414.patch` and
`.github/build/filecore-preview414.patch`. The workflow checks then applies
both to the pinned repositories before building. Native compile logs explicitly
show `stream_parser_ffmpeg.c` rebuilt for all four architectures.

Reviewed, not wholesale imported: new read-ahead queue limits and 44eff9a queue
cap correction (no queue-cap feature introduced here), broad FD/buffer refactoring,
13e18b8 passthrough seek work and 5c1acce transition-race work. Their surrounding
changes require a separate compatibility evaluation. The 6.4.66 manifest moves
several engines/prebuilts together; that larger migration is outside this pass.

## Observed failure and propagation

Shield playback session 0c4cb834-2174-47fd-bda4-6e927ee99072 used native backend 0,
WebDAV-over-TLS and decoder 4. A completion at position 2298171 ms (~38:18) was
followed by completed=true / failed=false, activity finish error_code=0 and a
shutdown checkpoint of -2. In Nova, -2 is the completed sentinel committed after
the callback; it is not the original read failure.

The pinned FFmpeg parser classified almost every negative av_read_frame result
as EOF. _check_end already has a fatal parser-error path, but that flag was not
set for these reads. Java's existing error handler marks the session failed and
retains its checkpoint; its completion path does not commit completion after a
failure. Correcting the native classification makes that failure path reachable.
A false EOF returned by a server/demuxer without any error indication remains an
open diagnostic case; this patch cannot retrospectively prove or detect every
malformed remote stream.
