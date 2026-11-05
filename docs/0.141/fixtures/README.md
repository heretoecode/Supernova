# Synthetic chapter QA fixture

`embedded-chapters-12s.mkv` is generated entirely from FFmpeg testsrc2, MPEG-4 video, no audio. It contains two embedded chapters: **Opening at 0ms**, **Second chapter at 6000ms**, end12000ms. It is disposable test media, not an owner recording. SHA-256: `51e3fad5022e49b5ff1b1b3019a5f19acfa6b19abb32670af42c1e06ade50701`.

Reproduction (FFmpeg version/container metadata can change binary bytes):

```sh
ffmpeg -f lavfi -i 'testsrc2=duration=12:size=320x180:rate=24' -i chapters.ffmetadata -map 0:v -map_metadata 1 -map_chapters 1 -c:v mpeg4 -q:v 6 -an embedded-chapters-12s.mkv
ffprobe -v error -show_chapters -show_format -of json embedded-chapters-12s.mkv
```

The actual FFmpeg demux and patched AVOS metadata parcel passed on Android TV API28/x86 using `tools/playback_chapter_metadata_probe.c`. The probe also tests playback-relative origin conversion, absent tables and the256-entry bound. It does not start the full Android player/HUD or establish Shield decoder/output acceptance. On Shield verify that the marker appears at the midpoint and that the seek tooltip changes chapter title at six seconds; no audio tracks should be invented.
