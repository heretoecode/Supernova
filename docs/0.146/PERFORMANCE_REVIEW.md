# Native performance review

The original three-trial measurements included Docker exec, chroot, dynamic loading and QEMU process startup. The x86 short samples appeared39–107% slower, so a second probe timed twenty complete decodes inside one process after one warm-up decode, with three trials per codec and ABI. This measures decoding, demuxer open/close and checksum work; it excludes process startup. It is still synthetic software/QEMU evidence, not hardware playback or Shield acceptance.

| ABI | Sample | Baseline median ms/decode | Candidate median ms/decode | Change |
|---|---|---:|---:|---:|
| arm64-v8a | h264-aac.mp4 | 31.801 | 34.045 | +7.1% |
| arm64-v8a | hevc.mkv | 9.510 | 9.551 | +0.4% |
| arm64-v8a | av1.mkv | 12.042 | 11.529 | -4.3% |
| arm64-v8a | ac3-51.ac3 | 56.370 | 56.221 | -0.3% |
| armeabi-v7a | h264-aac.mp4 | 37.796 | 36.586 | -3.2% |
| armeabi-v7a | hevc.mkv | 11.056 | 10.872 | -1.7% |
| armeabi-v7a | av1.mkv | 12.534 | 11.143 | -11.1% |
| armeabi-v7a | ac3-51.ac3 | 43.164 | 44.618 | +3.4% |
| x86 | h264-aac.mp4 | 36.845 | 36.866 | +0.1% |
| x86 | hevc.mkv | 13.736 | 12.097 | -11.9% |
| x86 | av1.mkv | 17.262 | 15.699 | -9.1% |
| x86 | ac3-51.ac3 | 67.564 | 63.737 | -5.7% |
| x86_64 | h264-aac.mp4 | 3.202 | 2.804 | -12.4% |
| x86_64 | hevc.mkv | 1.498 | 1.019 | -32.0% |
| x86_64 | av1.mkv | 1.163 | 1.106 | -4.9% |
| x86_64 | ac3-51.ac3 | 2.742 | 2.380 | -13.2% |

No performance pass threshold was invented after measurement. Small relative changes over millisecond samples remain timing-sensitive. The initial x86 process-start result does not establish a decoder regression; these longer in-process measurements narrow that concern. There is no CPU/memory/thermal, MediaCodec, network throughput, first-frame UI or physical-device benchmark clearance. The source and all three measured trials remain in NATIVE_WARM_PERFORMANCE.json and qa/warm_performance_probe.c.
