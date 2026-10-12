# 13 — Reproducible regression and benchmark plan

Everything in this report is **PROPOSED** unless a prior QA receipt is explicitly referenced. No scenario or benchmark below was executed on Shield in this investigation. [Fixed corpus specification](evidence/proposed-media-corpus.json) records18 media families; files/hashes are currently UNKNOWN and must be acquired/generated legally and frozen before comparison. This is a concrete corpus recipe, not fabricated media proof.

## Corpus and fixture rules

Freeze immutable media IDs M01–M18 with SHA256, exact ffprobe/MediaInfo report, encoder/generator/version/command or authorized sample provenance, duration/keyframe/timestamp map, local track indices/dispositions and expected HDR/audio capability. Synthetic clips should show frame number/timecode and synchronized audio ticks; include lossless source markers where objective seek/A/V comparison is intended. Dolby Vision, Atmos, DTS-HD, HDR10+ and DVB fixtures need authentic lawful samples; generic FFmpeg generation does not establish those proprietary features.

Use an unchanged library fixture with movies, episodes, season gap/completion/next season, provider-only items, unmatched/no-artwork metadata, duplicate-title IDs, missing sources, custom memberships and localized/unknown genres. Include pre-1970/tied/missing release dates and 100/1000/10000-row libraries. Use no real passwords/media names in public data. Seed credential-shaped canaries only into synthetic privacy tests and assert their absence from collection, disk, summary, ZIP and log sinks.

## Functional regression matrix

| Area | Fixed action/scenario | Required correctness evidence |
|---|---|---|
| Startup/upgrade | Cold/warm entry, upgrade signed0.137→0.138→approved diagnostics/maintenance, no clear-data | Exact package/certificate/version/source; data intact; permissions and consent preserved; interactive-ready boundary |
| Local playback | All corpus engines/containers, software force and Android fallback where supported | Prepared/play/end, correct metadata, valid picture/audio, known first-output quality, no false EOF |
| Network playback | SMB jcifs/SMBJ, JSch/SSHJ SFTP, FTP/FTPS, DAV HTTP/HTTPS, UPnP, content grants | Byte-integrity check, authentication/trust boundary, bounded resources, recovery and no secret output |
| Seek | Start/middle/end, ±10/30/300s, before/after keyframe, repeated overlapping seeks while playing/paused | Attempt pairing, target/achieved decoded or presented timestamp, latency and tolerance tied to keyframes/backend |
| Pause/resume/speed | Repeated rapid and long pause, 0.5/1/1.5/2× where supported, seek during change | User intent preserved, no cumulative audio drift, no queue starvation or unsupported pass-through speed |
| Track/subtitles | Audio track switch, none/text/bitmap/SDH/forced switch, paused cue replay, delay and encoding | No A/V seek on subtitle-only switch if adopted, cue/clear correctness, acknowledged selected track |
| Recovery/routes | NAS disconnect, timeout, sleep/wake, HDMI/AVR/Bluetooth/noisy focus loss, surface recreate/PiP/exit while preparing | No late recovery after exit, proper requested state/position/track restoration and clean resources |
| Shield output | PCM and each supported AC3/EAC3/DTS/DTS-HD/TrueHD passthrough mode, HDR10/DV/HDR10+, refresh switching | Record Shield model/OS/firmware/display/AVR chain; capability and actual sink output separately verified |
| Library/metadata | Import, full/partial rescrape, encoded names, provider failure/offline, delete/reimport/missing source | Row/identity/membership/history invariants; no lost final refresh or invented local metadata |
| Provider/subtitle cache | Off/default, private mode, intent priorities, season boundaries,429/backoff/expiry | No import searches, proper unknown-vs-negative classification, no unauthorized downloads/account secrets |
| D-pad/UI | Every atlas surface, >8 drives, >50 folders, long/large-text labels,640×360/720p/1080p logical configurations | Initial/edge/Back/cancel/restore targets, modal parent, no hidden/offscreen focus, no clipping or appearance regressions |
| Settings/persistence | Every current visible option + retained legacy values, recreate/restart/back | Handler/persisted state matches selected value; no accidental credential erasure/hidden feature change |
| DB/backup | Historical schema fixtures, dual-v62 fingerprint, current portable backup, interrupted restore/disk full | All-original/all-restored atomic state; custom tables/history preserved; incompatible backups rejected safely |
| Release/shrinker | Release R8/resource shrink,4 ABIs, JNI/reflection/external intent/alternate layout checks | Actual artifact hashes, mapping/usage/seeds; codec/enum/native exports intact; license/source payload intact |
| Privacy/security | Secret canaries, credential URLs/headers/exceptions/nested objects, DAV redirects, SFTP changed key, HTTPS bad cert and listener binding | No forbidden data at any sink; unknown field rejected; protected endpoint trust and expected LAN surface |

## Measurement contract

Record exact source/APK/certificate/SBOM/native hashes, build mode/compiler/toolchain/shrinker/ABI, dataset hashes, install/upgrade state, device/OS/display/refresh/audio route, thermal/power state, network topology/server/protocol/version/cache state and instrumentation version. Preserve timestamps and measurement quality; do not merge prepared/Java callback/native render/compositor/physical observations.

Recommended metrics: process launch→first interactive composition; navigation intent→ready/focus restored; playback intent→prepared→first-output; seek intent→backend completion→next output and accuracy; buffering duration/recovery count; local/network bytes/read time/throughput; scan/query/artwork duration/cache hits/row rebuilds; Java/native heap/PSS/GC deltas/process CPU/FD/thread/writer queue/drop counts. Existing sampler/flight recorder supplies part of these; report05 defines missing fields. A/V clock differences need sampling freshness and uncertainty; actual physical A/V skew needs appropriate capture/calibration. Memory slope alone is not a leak.

## A/A before A/B

1. Verify identical baseline builds/settings/corpus with at least two repeated A/A sessions before changing behavior. Repeat instrumentation off/on if owner enables that comparison to quantify observer effect; do not upload private traces.
2. Control network using fixed wired topology/server and a documented lab-only impairment profile (normal/throttled/latency/brief disconnect). Freeze cache warm/cold state separately. Do not run measurements while scanning unless the scenario explicitly tests contention.
3. Stabilize temperature/power/refresh output; record pre/post thermals and cooldown criterion. Rotate scenario order and use paired randomized A/B or ABBA runs to limit order/warmup bias. Use the same installation history and retained library state, or rebuild fixture reproducibly.
4. Collect distributions, not one timing. For short actions aim at ≥100 valid independent samples for p95; report n, median,p90,p95,IQR,min/max, failures/censored attempts and bootstrap paired differences. For long soak/network cases20–30 runs may be practical; disclose that tail quantiles have weak precision and report individual failures/range.
5. Predefine improvement thresholds against A/A variability and practical owner impact. No performance pass from a smaller APK alone. Suggested starting gates (subject to measured noise/approval): no correctness/privacy/data regressions; no >5% p95 playback/navigation regression; demonstrate useful optimization beyond noise; diagnostics overhead within agreed budget.
6. Retain failed/interrupted attempts and explicit unknown boundaries. Dropped/rotated events invalidate some pairings; do not replace missing first-frame events with inferred zero latency or report incomplete spans as session lifetime.

## Release and evidence gates

Future release must use clean pinned multi-repo build, release tests/lint/native checks and actual signed artifact conformance before install. Original0.138 receipt reports564 tests and native comparisons; those are not maintenance tests. After install record About/version/startup stability, then corpus/navigation/upgrade/data/security benchmarks and owner acceptance. Exact Shield firmware/output-chain limitation stays in release evidence. No APK publication, CI dispatch or test implementation occurred in this documentation task.
