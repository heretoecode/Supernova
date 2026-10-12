# 04 — Upstream change ledger

Recommendations are **PROPOSED**, not approval or portable patches. Source/diff statements are **VERIFIED static**; benefit is expected, never a Supernova benchmark claim. Full SHAs, changed files, hunk symbols and ancestry are in the [219-row inventory](evidence/upstream-commit-inventory.tsv). The [selected ledger JSON](evidence/upstream-selected-ledger.json) provides all fields for implementation planning. Current component snapshots are used; precise v6.5.5 APK inclusion remains UNKNOWN as explained in report 03.

## Prioritized technical ledger

### U01 — AVOS / P0 / ADAPT

[96d5f4fb55fbd5647ecb8e5cfb4a30c18dcf5562](https://github.com/nova-video-player/aos-avos/commit/96d5f4fb55fbd5647ecb8e5cfb4a30c18dcf5562)

- Verified change: stream_buffer_mib_valid checks signed MiB allocation size, ring overlap and doubled CBE capacity; default frame fallback avoids unsafe narrowing.
- Files / Supernova overlap: AVOS stream.c/parser/video; Supernova native overlays share headers and parser.
- Expected benefit: Bound malicious/invalid buffer settings; no CVE or exploit asserted.
- Risk and compatibility: Low-to-medium isolated logic, but header callers must be coherent.
- Required adaptation: Retain zero fallback and Supernova native exports.
- Proposed tests: Boundary sizes, negative/zero/INT_MAX, UBSan, four ABIs and release playback.

### U02 — AVOS / P0 / ADAPT

[0f40416fad865927043a13cb5f030a49d498896b](https://github.com/nova-video-player/aos-avos/commit/0f40416fad865927043a13cb5f030a49d498896b)

- Verified change: Bounded NAL access-unit writes, descriptor duplication/AVIO slices, queue limits and interrupted-input/seek error handling.
- Files / Supernova overlap: Parser, cbe, fd source, stream/video/subtitle; Supernova HDR parser overlap.
- Expected benefit: Avoid corruption, false seek success and unbounded memory.
- Risk and compatibility: High interdependency; introduced packet-limit behavior later corrected by 44eff9a.
- Required adaptation: Carry companion descriptor APIs and retain Supernova metadata.
- Proposed tests: Malformed H264/HEVC, EAGAIN/EINTR, fd slices and ownership, starvation/seek/EOF.

### U03 — AVOS / P0 / ADAPT

[44eff9a07f724f29f3b734804ed804fbb6675c66](https://github.com/nova-video-player/aos-avos/commit/44eff9a07f724f29f3b734804ed804fbb6675c66)

- Verified change: queue_packets reads under mutex; normal full packet queues return FF_PARSE_WAIT before av_read_frame.
- Files / Supernova overlap: stream_parser_ffmpeg.c; same parser carries Supernova HDR changes.
- Expected benefit: TrueHD small-packet read-ahead backpressure without fatal allocation failure.
- Risk and compatibility: Depends on 0f40416 queue structures; isolated cherry-pick into older parser unsafe.
- Required adaptation: Integrate with bounds group, retain packet rather than dropping it.
- Proposed tests: TrueHD PCM/pass-through resume, queue saturation and consumers recovering.

### U04 — AVOS / P0 / ADAPT

[1aea10b53228535063080522b602a743d0569943](https://github.com/nova-video-player/aos-avos/commit/1aea10b53228535063080522b602a743d0569943)

- Verified change: Read-ahead errors distinguished from continuing cached media; follow-up input failure handling.
- Files / Supernova overlap: stream parser/buffer paths; inspect net source with 0f40416.
- Expected benefit: Improve recovery instead of freeze.
- Risk and compatibility: Full applicability not device verified.
- Required adaptation: Reconcile parser error ownership and source cancellation.
- Proposed tests: Disconnected NAS, truncated inputs, reconnect and meaningful error checkpoint.

### U05 — AVOS / P0 / ADAPT

[abad6d2ceb871e9591eab8186257aa39043fe499](https://github.com/nova-video-player/aos-avos/commit/abad6d2ceb871e9591eab8186257aa39043fe499)

- Verified change: Track started threads, audio reader leases/reconfiguration generations, async destruction/open publication.
- Files / Supernova overlap: stream headers, avos_mp, audio/video lifecycle; Supernova sync patch overlap.
- Expected benefit: Prevent races and partial-startup cleanup errors.
- Risk and compatibility: High concurrency and Java/native coupling.
- Required adaptation: Map lock order and release callbacks before integration.
- Proposed tests: Open-stop races, partial thread failure, track switching and repeated surface teardown.

### U06 — AVOS / P0 / ADAPT

[a272ebfa18839ab847b849613bb1c714ede26b5d](https://github.com/nova-video-player/aos-avos/commit/a272ebfa18839ab847b849613bb1c714ede26b5d)

- Verified change: Idle async engine around seek state reset; recycle decoder/frame queues and retag empty containers across flush.
- Files / Supernova overlap: codec_sfdec2, stream_video; native timeline overlap.
- Expected benefit: Avoid stale epochs/frame loss during repeated seeks.
- Risk and compatibility: Depends on surrounding seek generations and sync group.
- Required adaptation: Preserve Supernova intent/seek preview and distinguish achieved position.
- Proposed tests: Forward/back seek while paused and playing; repeated flush failure; PCM/pass-through.

### U07 — AVOS / P1 / ADAPT

[2c8c6161f2cad52dc2d3d1f37d2f03507250ffbe](https://github.com/nova-video-player/aos-avos/commit/2c8c6161f2cad52dc2d3d1f37d2f03507250ffbe)

- Verified change: 64-bit monotonic producer/consumer timestamps replace signed 32-bit deadline paths.
- Files / Supernova overlap: AudioTrack, stream headers/audio/sync/video and atempo.
- Expected benefit: Avoid timing failure after about 24.9 days uptime.
- Risk and compatibility: Wide API/struct change, not a one-line time fix.
- Required adaptation: Update format specifiers and all consumers atomically.
- Proposed tests: Injected boundary clock tests plus long-uptime Shield soak; no waiting 25 days required.

### U08 — AVOS / P1 / ALREADY PRESENT

[9c9f6bb036edab93f54a960464b64ba5141029b1](https://github.com/nova-video-player/aos-avos/commit/9c9f6bb036edab93f54a960464b64ba5141029b1)

- Verified change: Pause/resume lip-sync group has related audio_pause_preserves_output and resume preparation in Supernova avos-preview414.patch.
- Files / Supernova overlap: Native audio/stream/sync files.
- Expected benefit: Existing work should be preserved; exact whole-patch equality NOT found.
- Risk and compatibility: Partial semantic presence only; subsequent fixes still missing.
- Required adaptation: Compare effective final functions; do not duplicate earlier patch.
- Proposed tests: Repeated pause/resume, speed and seek distributions.

### U09 — AVOS / P1 / ADAPT

[13e18b84db1c44c60bbfa586e5b1ce1ccb574592](https://github.com/nova-video-player/aos-avos/commit/13e18b84db1c44c60bbfa586e5b1ce1ccb574592)

- Verified change: IEC carrier validation and passthrough writer pause state; DTS/TrueHD route geometry changes.
- Files / Supernova overlap: audio_spdif and AudioTrack; Supernova pause changes overlap.
- Expected benefit: Seek/start and stalled passthrough writes.
- Risk and compatibility: Receiver/route dependent; surround formats and modes vary.
- Required adaptation: Review with coherent route capabilities and PCM fallback.
- Proposed tests: AC3/EAC3/DTS/DTS-HD/TrueHD mode matrix and interrupted writes.

### U10 — AVOS / P1 / ADAPT

[fbecfc349e2d77da67e1f4decb040be50f27051a](https://github.com/nova-video-player/aos-avos/commit/fbecfc349e2d77da67e1f4decb040be50f27051a)

- Verified change: Commit-aware PCM resume notification, renderer reanchoring and audio clock publication.
- Files / Supernova overlap: codec_sfdec2 and stream resume state.
- Expected benefit: Reduce accumulated pause drift; benefit unmeasured in Supernova.
- Risk and compatibility: Sync group is heavily interdependent with later mode 1/2 changes.
- Required adaptation: Use final snapshot algorithm, retain diagnostic/clock separation.
- Proposed tests: Many rapid pauses, slow/fast speed, sparse timestamps and no-audio video.

### U11 — AVOS / P1 / ADAPT

[da9b4587bae0df3456e010c470734e381bbc5c2e](https://github.com/nova-video-player/aos-avos/commit/da9b4587bae0df3456e010c470734e381bbc5c2e)

- Verified change: Initialize subtitle codec extradata/timebase and clarify decoder/message bitmap ownership.
- Files / Supernova overlap: codec_ffsub, avos_common.
- Expected benefit: Prevent corrupt palettes, initialization failure and ownership errors.
- Risk and compatibility: FFmpeg API and memory-lifetime sensitive.
- Required adaptation: Validate source generation against rebuilt FFmpeg 8 or selected coherent target.
- Proposed tests: PGS/VobSub/mov_text, broken extradata and bitmap replacement/free checks.

### U12 — AVOS / P1 / ADAPT

[4186568a950deff8cbe0229a8e1a3c8c7a19589f](https://github.com/nova-video-player/aos-avos/commit/4186568a950deff8cbe0229a8e1a3c8c7a19589f)

- Verified change: Bounded internal subtitle packet cache/reset_subtitle and replay cues without A/V seek.
- Files / Supernova overlap: stream/parser/subtitle/public events; Supernova track acknowledgements.
- Expected benefit: Switch cues without disturbing audio/video.
- Risk and compatibility: Depends on cache bounds, seek reset and Java paused cue handling.
- Required adaptation: Preserve event enum compatibility and SDH/local language policy.
- Proposed tests: Paused/playing text↔bitmap, none→track, active cue replay and memory cap.

### U13 — AVOS / P1 / ADAPT

[165330045d7fab3d89464242940a385eb80b2042](https://github.com/nova-video-player/aos-avos/commit/165330045d7fab3d89464242940a385eb80b2042)

- Verified change: Private timing prefix only for text; cue timestamps, PGS clears and external subtitle reset.
- Files / Supernova overlap: codec_ffsub, stream_subtitle, subtitle format/parsers.
- Expected benefit: Subtitle timing and seek state correctness.
- Risk and compatibility: Parser/UTF16/source path ownership and rendering cross-effects.
- Required adaptation: Adopt technical portions, retain Supernova chooser appearance.
- Proposed tests: ASS/SRT/WebVTT/PGS/mov_text, colon text, seek and delay.

### U14 — Video / P1 / ADAPT

[12cdaea1e5889bef6823a94774c42b1e7074605b](https://github.com/nova-video-player/aos-Video/commit/12cdaea1e5889bef6823a94774c42b1e7074605b)

- Verified change: SubtitleManager monitor/deadlines preserve remaining cue duration while paused; wait/notify and shutdown outside lock.
- Files / Supernova overlap: Player SubtitleManager with Supernova rendering integration.
- Expected benefit: Avoid paused track switch expiring a cue.
- Risk and compatibility: Frontend file but technical timer change; native replay semantics required.
- Required adaptation: Rewrite nonvisual timer delta only with existing widget/layout unchanged.
- Proposed tests: Paused cue replacement, clear event, repeated resume and shutdown.

### U15 — AVOS / P1 / DEFER

[358d098707a079d0da7b1a71291d4cb8b3491625](https://github.com/nova-video-player/aos-avos/commit/358d098707a079d0da7b1a71291d4cb8b3491625)

- Verified change: Map pixel formats to FFmpeg and use reusable locked swscale conversion contexts.
- Files / Supernova overlap: codec_utils/video conversions/native make; MediaLib and AVP remove libyuv.
- Expected benefit: Potential simplification and conversion correctness, speed UNKNOWN.
- Risk and compatibility: Large coherent ABI/colour pipeline migration.
- Required adaptation: Retain HDR and full/limited range; remove libyuv only after consumer proof.
- Proposed tests: Odd dimensions/strides, software decode, thumbnails, colour bars and HDR/device output.

### U16 — AVOS / P1 / ADAPT

[024469121cb39797c29e437258709da7cd3b5b9c](https://github.com/nova-video-player/aos-avos/commit/024469121cb39797c29e437258709da7cd3b5b9c)

- Verified change: Abort ownership/lifetime for retriever, bounded metadata growth and independent thumbnail timeline.
- Files / Supernova overlap: avos_mr, thumb_stream, metadata buffer.
- Expected benefit: Safer thumbnail cancellation and allocations.
- Risk and compatibility: Depends on Java retriever lifecycle/proxy changes.
- Required adaptation: Preserve local technical metadata fields and cancelled-generation filtering.
- Proposed tests: Rapid folder/details changes, interrupted probe, bad media and no playback timeline interference.

### U17 — AVOS / P1 / ADAPT

[8f4fd16bb65be88addfc787bfbafc6b1163c7d7c](https://github.com/nova-video-player/aos-avos/commit/8f4fd16bb65be88addfc787bfbafc6b1163c7d7c)

- Verified change: Surface reference held until consumers close; JNI AudioTrack route observer lookup.
- Files / Supernova overlap: avos_mp, audio_interface; MediaLib LibAvos route API required.
- Expected benefit: Safer surface recreation and effective route detection.
- Risk and compatibility: Companion Java module and frontend must be integrated together.
- Required adaptation: Preserve Supernova overlays and fallback on older APIs.
- Proposed tests: Refresh switch, HDMI unplug, PiP, background/foreground and repeated destroy.

### U18 — MediaLib / P1 / ADAPT

[933a764b4983946e454e1a66219f46f120ffbc74](https://github.com/nova-video-player/aos-MediaLib/commit/933a764b4983946e454e1a66219f46f120ffbc74)

- Verified change: Lifecycle lock/released guard, proxy transfer and AudioTrack route publication.
- Files / Supernova overlap: AvosMediaPlayer/LibAvos; Supernova track metadata overlay.
- Expected benefit: Avoid replacement proxy/resource races.
- Risk and compatibility: Requires AVOS and Video surface/route partners.
- Required adaptation: Reconcile custom event/metadata enums and error checkpoint lifecycle.
- Proposed tests: Delayed setup after release, route recovery and stale callback rejection.

### U19 — Video / P1 / ADAPT

[4f12d0dddeed67f1ddbc290cf3dc15cc7cb597d8](https://github.com/nova-video-player/aos-Video/commit/4f12d0dddeed67f1ddbc290cf3dc15cc7cb597d8)

- Verified change: Capture source requests, reject obsolete completions, surface stop/reopen, focus intent and renderer shutdown.
- Files / Supernova overlap: Player, PlayerActivity, SurfaceController, effects, CustomApplication.
- Expected benefit: Playback survives surface/audio-route/focus transitions.
- Risk and compatibility: Broad high conflict in custom startup, HUD and durable resume flows.
- Required adaptation: Extract technical lifecycle changes; no upstream presentation import.
- Proposed tests: Exit/reopen, PiP, refresh rate, focus loss, paused recreation and track preservation.

### U20 — Video / P1 / ADAPT

[a5ab41ba1eb62732fab2f1d7cfeb8602e3c4f730](https://github.com/nova-video-player/aos-Video/commit/a5ab41ba1eb62732fab2f1d7cfeb8602e3c4f730)

- Verified change: Compare route capabilities rather than transient IDs; 500ms coalescing.
- Files / Supernova overlap: CustomApplication + Player readiness; custom route code overlap.
- Expected benefit: Avoid unnecessary playback rebuilds.
- Risk and compatibility: Depends on preceding route recovery design.
- Required adaptation: Retain explicit headset exclusivity and capability flags.
- Proposed tests: Identical ID churn versus changed capability, stable debounce and position.

### U21 — Video / P1 / ADAPT

[1a843441afa62f88c691544ec477fcdf2ce58147](https://github.com/nova-video-player/aos-Video/commit/1a843441afa62f88c691544ec477fcdf2ce58147)

- Verified change: beginPlaybackExit before finishing; invalidate pending source/poll/preparation callbacks.
- Files / Supernova overlap: PlayerActivity/Player; custom error/resume and external result flows.
- Expected benefit: No recovery after deliberate exit.
- Risk and compatibility: Do not break floating-player handoff or checkpoint completion.
- Required adaptation: Adapt exit intent only, preserve HUD and result behavior.
- Proposed tests: Exit during prepare, late route callbacks, PiP handoff and saved progress.

### U22 — Video / P2 / DEFER

[d8e24aec2390b4f3b2f9bd741fe64cea02537b58](https://github.com/nova-video-player/aos-Video/commit/d8e24aec2390b4f3b2f9bd741fe64cea02537b58)

- Verified change: Preserve advertised ARC compressed capabilities when TV reports shared speaker route.
- Files / Supernova overlap: CustomApplication route selection.
- Expected benefit: Relevant to TV ARC/eARC, Shield relevance depends on receiver topology.
- Risk and compatibility: Device-specific; must not override Bluetooth/headphone routes.
- Required adaptation: Only adopt after actual target route reproduces need.
- Proposed tests: TV/AVR ARC attach/detach, explicit speaker/headset and capability change.

### U23 — FileCoreLibrary / P1 / ALREADY PRESENT

[c9639d9f270be17a5cf81d8ca28327cb4e7104cc](https://github.com/nova-video-player/aos-FileCoreLibrary/commit/c9639d9f270be17a5cf81d8ca28327cb4e7104cc)

- Verified change: HTTP relay header terminator, suffix/clamped ranges, unknown-length EOF and cancellation admission are present in filecore-preview414.patch.
- Files / Supernova overlap: StreamOverHttp; overlay has MAX_HEADER_BYTES=65536 and MAX_SESSIONS=16.
- Expected benefit: Preserve existing safety rather than duplicate infrastructure.
- Risk and compatibility: Whole-patch ID differs; later transport/read-policy deltas not present by implication.
- Required adaptation: Review final per-function delta and superseding ownership fixes.
- Proposed tests: Fragmented headers, 416/ranges, EOF, seek replacing reads and server close.

### U24 — FileCoreLibrary / P0 / ADAPT

[4cdc574b7b66850d93153752a9a1e5e5fa353782](https://github.com/nova-video-player/aos-FileCoreLibrary/commit/4cdc574b7b66850d93153752a9a1e5e5fa353782)

- Verified change: Shared background crypto/credential load, 30s bounded waits, idempotent publication and retry.
- Files / Supernova overlap: NetworkInitialization, BackgroundInitialization, credentials, jcifs, relay.
- Expected benefit: Avoid early SMB negotiation before readiness.
- Risk and compatibility: Provider publication overlaps CustomApplication BC setup; never await on UI thread.
- Required adaptation: Add application bootstrap plus metadata/player workers in same change.
- Proposed tests: Concurrent waiters, cancellation, retry, timeout, cold start SMB and no-secret diagnostics.

### U25 — Video / P0 / ADAPT

[4a39153aa8adfc1fb7406f2fbbcdcd9ebe2c22d0](https://github.com/nova-video-player/aos-Video/commit/4a39153aa8adfc1fb7406f2fbbcdcd9ebe2c22d0)

- Verified change: Workers wait for SMB readiness before metadata and playback.
- Files / Supernova overlap: Player and details/metadata worker boundaries.
- Expected benefit: Completes 4cdc574 readiness behavior.
- Risk and compatibility: Two upstream readiness commits (4a39153a and dd86fef2); use net change.
- Required adaptation: Gate actual Supernova worker paths, avoid double waits and UI import.
- Proposed tests: Cached and uncached Details, playback cold start and cancelled load.

### U26 — FileCoreLibrary / P1 / ADAPT

[476b444c28eebec2ed10a4f474b224a28922aecb](https://github.com/nova-video-player/aos-FileCoreLibrary/commit/476b444c28eebec2ed10a4f474b224a28922aecb)

- Verified change: Retain @ usernames; only split DOMAIN-backslash-user; credentials DB2→3 repair for non-SMB rows.
- Files / Supernova overlap: NetworkCredentialsDatabase, authentication.
- Expected benefit: Fix email-shaped logins.
- Risk and compatibility: Credential migration is sensitive and isolated from media DB.
- Required adaptation: Preserve save/temporary credentials and avoid logs/exports of usernames.
- Proposed tests: SMB domain and email WebDAV/SFTP, upgrade existing credentials, no cleartext artifacts.

### U27 — FileCoreLibrary / P1 / ADAPT

[32f4ff4e7853cc6cd7c1af1e7f8dff66165acdd4](https://github.com/nova-video-player/aos-FileCoreLibrary/commit/32f4ff4e7853cc6cd7c1af1e7f8dff66165acdd4)

- Verified change: URI child/removal and JcifsFile2 retain canonical names while escaping #/? delimiters.
- Files / Supernova overlap: FileUtils/JcifsFile2; library roots, history and source identity affected.
- Expected benefit: Prevent duplicate indexed paths and wrong filenames.
- Risk and compatibility: Requires MediaLib 8a405189 reconciliation; no blind path rewrite.
- Required adaptation: Test old and newly encoded rows together; protect source matching.
- Proposed tests: Spaces/brackets/%/#/?/Unicode and resume/bookmark/NFO round trips.

### U28 — MediaLib / P1 / ADAPT

[8a40518990684ce181e7b6de0b8a031826176f54](https://github.com/nova-video-player/aos-MediaLib/commit/8a40518990684ce181e7b6de0b8a031826176f54)

- Verified change: Scanner matches raw and legacy encoded paths; XmlDb Pattern.quote for associated XML basename.
- Files / Supernova overlap: NetworkScannerServiceVideo, XmlDb; custom scanner lock/policy overlap.
- Expected benefit: Reconcile existing path identity and NFO matches.
- Risk and compatibility: Logging additions expose names/paths; omit unsafe log strings.
- Required adaptation: Retain current coalescing and exclusions, adapt only query/path logic.
- Proposed tests: Legacy duplicate rows, NFO names with regex metacharacters and watched-state preservation.

### U29 — FileCoreLibrary / P1 / ADAPT

[429b075a86db7cea4bc4f38947f8f006e2e10241](https://github.com/nova-video-player/aos-FileCoreLibrary/commit/429b075a86db7cea4bc4f38947f8f006e2e10241)

- Verified change: OwnedStreams and transfer of content/SFTP cleanup ownership.
- Files / Supernova overlap: ContentStorage/local/SFTP/SSHJ editors and sessions.
- Expected benefit: Reduce connection/stream leaks and double close.
- Risk and compatibility: Combined with later lifecycle synchronization and cancellation protection.
- Required adaptation: Preserve SftpHostTrust in both implementations.
- Proposed tests: Interrupted open/read/close, concurrent sessions and descriptor counters.

### U30 — FileCoreLibrary / P1 / ADAPT

[89862ff8c3ed108db40c66dbd8c4508f95bdb68f](https://github.com/nova-video-player/aos-FileCoreLibrary/commit/89862ff8c3ed108db40c66dbd8c4508f95bdb68f)

- Verified change: Response length/read purpose reaches backend policy; finite ranges conservative, full playback prefetch.
- Files / Supernova overlap: StreamOverHttp/FileEditor policy and MediaLib SmbProxy.
- Expected benefit: Bound metadata without degrading playback.
- Risk and compatibility: Needs ReadMode overloads and backend contracts from preceding commits.
- Required adaptation: Adopt coherent read-policy group; retain existing header/range patch.
- Proposed tests: Small NFO, long tail/range, large movie seek, limited-credit NAS and integrity hash.

### U31 — MediaLib / P1 / ADAPT

[010df8bc5ab7924be66a0466d45cc33917223a4f](https://github.com/nova-video-player/aos-MediaLib/commit/010df8bc5ab7924be66a0466d45cc33917223a4f)

- Verified change: SmbProxy player construction selects PLAYBACK instead of default read mode.
- Files / Supernova overlap: SmbProxy and FileCore overloads.
- Expected benefit: Ensures playback policy actually used.
- Risk and compatibility: Compile/runtime dependency on FileCore ReadMode.
- Required adaptation: Update metadata proxy separately and measure memory/throughput.
- Proposed tests: Local vs SMB proxy startup/seek plus metadata requests not over-prefetched.

### U32 — FileCoreLibrary / P1 / ADAPT

[1aa2089ed760051894875a23467ad2a79dc4dd6a](https://github.com/nova-video-player/aos-FileCoreLibrary/commit/1aa2089ed760051894875a23467ad2a79dc4dd6a)

- Verified change: OPTIONS must advertise DAV before accepting redirect base; target validated without credentials.
- Files / Supernova overlap: WebdavUtils; Supernova cross-origin auth stripping overlay must remain.
- Expected benefit: Avoid login-page redirects mistaken for mounts.
- Risk and compatibility: DAV header can be absent on some interoperable servers.
- Required adaptation: Preserve auth origin protection and bound redirect rules.
- Proposed tests: Same/cross-origin 401, login redirect, actual DAV redirect and no credential forwarding.

### U33 — MediaLib / P1 / ADAPT

[1769015dafa73fb6d0e330d9666c924c0c2c73df](https://github.com/nova-video-player/aos-MediaLib/commit/1769015dafa73fb6d0e330d9666c924c0c2c73df)

- Verified change: Native descriptor slice fast path and buffer-setting validation.
- Files / Supernova overlap: AvosMediaPlayer/retriever/LibAvos/ContentFileDescriptor.
- Expected benefit: Avoid unnecessary relay; source bounds preserved.
- Risk and compatibility: Depends on AVOS fd duplication and FileCore editor lifecycle.
- Required adaptation: Do not map unknown size to valid empty data inadvertently.
- Proposed tests: Nonzero offsets, unknown length, unseekable provider and descriptor ownership.

### U34 — MediaLib / P0 / DEFER

[552d52b835144da8a3102740450c2c50f4377373](https://github.com/nova-video-player/aos-MediaLib/commit/552d52b835144da8a3102740450c2c50f4377373)

- Verified change: m_genre_ids/s_genre_ids schema and locale-independent backfill use upstream DB v62.
- Files / Supernova overlap: VideoOpenHelper/ScraperTables/ScraperStore/GenreUtils; conflicts with custom collections.
- Expected benefit: Stable genre identity across language changes.
- Risk and compatibility: Confirmed schema version collision; raw adoption unsafe.
- Required adaptation: Design successor Supernova schema retaining both; update backup compatibility.
- Proposed tests: Existing v62 custom DB, older upgrade paths, genre backfill unknowns and downgrade restore.

### U35 — Video / P1 / DEFER

[114e979f9afb7b1ad63ddc9e273e0879bedbab5b](https://github.com/nova-video-player/aos-Video/commit/114e979f9afb7b1ad63ddc9e273e0879bedbab5b)

- Verified change: Filter/display genres by IDs rather than names.
- Files / Supernova overlap: Browser/filter/genre widgets; Supernova custom filters remain.
- Expected benefit: Locale stability after schema adaptation.
- Risk and compatibility: Presentation-bearing; must not copy upstream UI.
- Required adaptation: Only data mapping after approved DB migration, preserving labels/focus.
- Proposed tests: Language switch, unknown genre IDs, custom saved filters and restored settings.

### U36 — MediaLib / P1 / ADAPT

[7875a0baca5d51e927e19f4fa3ca6fc7957d64d8](https://github.com/nova-video-player/aos-MediaLib/commit/7875a0baca5d51e927e19f4fa3ca6fc7957d64d8)

- Verified change: Bulk scan/scrape counters and pending state defer category loader refreshes.
- Files / Supernova overlap: LoaderUtils/AutoScrapeService/scanner; Supernova ProviderDiscoveryGate.
- Expected benefit: Reduce repeated UI rebuilds during import.
- Risk and compatibility: Notification completion and custom lock order high overlap.
- Required adaptation: Reconcile final completion and immediate single-item changes.
- Proposed tests: Large scans, one delete/import, cancellation/error and refresh count measurements.

### U37 — Video / P2 / ADAPT

[27961c4015b2ca532856fdcc8ee27ff71014c9fe](https://github.com/nova-video-player/aos-Video/commit/27961c4015b2ca532856fdcc8ee27ff71014c9fe)

- Verified change: Grouped browse-row publication stays stable across deletes.
- Files / Supernova overlap: Inherited grouped loaders/browse rows.
- Expected benefit: Potentially reduce row churn if still reached by current UI.
- Risk and compatibility: Post-6.5.5 development; applicability UNKNOWN.
- Required adaptation: Trace actual custom loader consumers before selection.
- Proposed tests: Deleting selected/nonselected file, focus restoration and empty group.

### U38 — Video / Excluded / REJECT

[7b6a3e17a317a3c22be508654a03b7d03fec7c01](https://github.com/nova-video-player/aos-Video/commit/7b6a3e17a317a3c22be508654a03b7d03fec7c01)

- Verified change: Upstream in-app updater includes its own channel and dialog paths.
- Files / Supernova overlap: Build/configuration/updater/activity/UI.
- Expected benefit: No maintenance benefit within owner-approved scope.
- Risk and compatibility: Identity/signing/channel conflict and explicitly excluded product behavior.
- Required adaptation: Do not import dependencies, permissions or hidden update machinery.
- Proposed tests: Static absence checks and unchanged app identity/release workflow.

### U39 — AVOS / P2 / DEFER

[51e3c510079e50989abf860dbc06ed1607b2cc4c](https://github.com/nova-video-player/aos-avos/commit/51e3c510079e50989abf860dbc06ed1607b2cc4c)

- Verified change: Bounded opt-in PCM observation lease and SurfaceFlinger presentation tooling.
- Files / Supernova overlap: AudioTrack native diagnostics and host/device tools.
- Expected benefit: More precise presentation evidence than Java callback timings.
- Risk and compatibility: Debug-only/tool permissions, overhead and privacy; cannot assert physical A/V offset.
- Required adaptation: Review as controlled QA instrument after safe app event schema.
- Proposed tests: No-leak lease timeout, recorder A/A overhead, sparse evidence and Shield API support.

## Net-change and supersession rules

**VERIFIED:** FileCore raises JSch and SSHJ windows, then reverts those changes (`ce6b063`, `8a66e84`, `d23391c`). jcifs readahead trial `ee92903` is reverted by `57edec1`; multicredit versions progress through later releases to nova12 and the final dependency update. Selecting an early throughput commit ignores the tested final state. No 4× throughput improvement is reported for Supernova.

Native mode 1/2 sync work includes multiple correction/publication/slew changes and test interpretation changes. Use a coherent end state, not the largest number of independent cherry-picks. The old search leads `fe57ba1adf62`, `ae006e5f980d`, `2fd1e0d220ba`, `cf0b003ea825`, `b8d7b800338a`, `7cd2eb4bd703`, `78076e738640` and `3d3226d99468` are not present in these selected base→head commit ranges. That is NOT proof of their behavior being absent: they may predate the pins, be on another branch, or have equivalent changes. Their adoption remains UNRESOLVED until exact objects/ancestry and net effects are reconciled.

## Remaining inventory disposition

Every upstream commit in the compared ranges is listed, including translation/resource, lint/security-manifest, dependency/version, test and documentation changes. These are not omitted because they are inconvenient. Bulk resource modernization, RTL/focus/layout fixes and updater UI are excluded from import under the prior owner boundary. Dependency-only commits need resolved runtime/native comparison rather than Gradle-line substitution. Test/tool/documentation commits can inform future validation without changing product behavior. Nonselected technical commits remain **UNRESOLVED** for individual portability; the inventory is not an assertion of completed backport certification.

**INCOMPLETE:** exhaustive per-commit adaptation certification across all 219 changes and every native builder/prebuilt repository is not established by this source audit. The important source groups above are investigated and prioritized; remaining rows need candidate-level reconciliation on validated 0.139 plus compile/device verification. No upstream integration or bulk merge was performed.
