# 0.156 Part B17a — network pause/read-failure addendum

Code ready **`afb3a755d`**, after D hand-back `edc5a4281`. Branch remains
`codex/0155-code-2026-11-04`. This is the new item in the freshly fetched Claude brief
`c37bd36d2`; it was absent at the initial `b5d3fc95d` read. No merge/rebase/pull of
Claude's code was performed. PortableCustomPage was not touched.

| Requirement | Files / implementation | Test evidence / Shield check | UI call |
| --- | --- | --- | --- |
| Pause >60 s | Player, NetworkRecoveryPolicy: fresh transport and decoder, retained current position, seek before start | Three policy tests pass. Player callback/rapid-pause tests supplied, Robolectric unrun. Confirm 3–4 minute Put.io pause on Shield. | Automatic in existing player pause/start paths. |
| Mid-play read failure | NEW MediaLib network-recovery patch: primary read guard poisons the old generation; owner-fenced app callback and native I/O mappings perform full replacement | Fake TCP server drops an idle promised-length response; old generation rejects reads/opens and new connection succeeds. Parser/API checks pass. Confirm decoder resync and native-only callback coverage on Shield. | None. |
| Last shown / requested seek | Player: pending seek first, recent render clock, live clock, service-owned retained checkpoint; one seek gate before start | Five Player tests supplied for ordering, checkpoints, source identity and stop fencing; unrun. Native timestamp quality remains explicit. | None; existing seek controls retain their target. |
| Paused read-ahead | PlaybackReadGuard shared 64 KiB read allowance, blocking thereafter; direct native sources retire on pause; metadata/sidecars excluded | Budget/late-registration/close tests pass. Adapter/refill tests supplied, unrun. Measure provider/native in-flight buffers and PSS on Shield. | None. |
| stream_reconnect evidence | Diagnostics important retention, DiagnosticExpandedSummary Playback fields, Player event boundaries | Extended summary regression passes; 25 host tests total including all D tests. | Existing export/report. |

Dependencies: register `.github/build/medialib-supernova156-network-recovery.patch`
after A scan, C NFO, D keyframe and D metadata-diagnostics patches. This adds a MediaLib
adapter in the FileCore package using existing package seams; no existing FileCore,
.github/build or Gradle file was edited and no fork was pushed. Native source adoption
also uses the existing release-fenced proxy install method.

Validation: **6 new host tests + 19 D regressions = 25 passed**; 15 changed app/dependency
Java sources/tests parsed. Focused API checks include actual MediaLib interfaces and
FileCore FileEditor/ReadOptions, with surrounding stubs. Full Android compile,
Robolectric/native extraction, lint and Shield tests remain Claude's; no Gradle ran.
Ordered patch reverse/check/forward application and source whitespace checks pass.

Scope checked with the requested three-dot comparison and isolated `edc5a4281..afb3a755d`.
Only owned player/service/diagnostics/tests and one allowed NEW MediaLib patch change.
Historical three-dot differences remain as explained in A/D. No protected screen,
resource, existing build file or cleanup brief changed in this addendum.

Exact behavior, test scope and native-only visibility limits are in
[network-stream-recovery.md](../findings/network-stream-recovery.md).
Claude continues with patch registration, Android tests and the Shield sequence there.
The original A–D work and the late B17a item are now handed back.
