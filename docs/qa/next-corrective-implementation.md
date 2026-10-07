# Next corrective implementation — work in progress

Starting live branch: `codex/post-4.1.7-shield-fixes`, `563c3374d6227ef290fb61c4899d9d93adb94eea`, inspected 7 October 2026. Local checkout advanced by fast-forward only; no existing work discarded. Main remains unchanged/unmerged. Accepted 4.1.7 Final and physically reviewed previous candidate keep their frozen identities.

Authority: next-version-authority.md, latest surface amendments, completed audits, then compatible historical evidence. Older FIXED labels, parked Featured wording, middle-panel Integration nesting and undecided playback-menu wording are superseded by the latest consolidated authority.

## Implementation checkpoints

Initial checkpoint is **unvalidated implementation work**, not a candidate or acceptance claim. Changes cover modal keyboard containment without input focus detours; live library Genre/Year/Provider filters and compact summary; toolbar ordering and explicit Order; bounded animated centring; shallow header fade; fixed exposed-neighbour Featured with two actions/no pagination; complete card unit boundary/artwork scaling; two-tier Details with capped text-only people; single-row Extras; embedded trailer overlay and exact opener return; left-panel Settings nesting; balanced utility-panel fit; shared bundled national flags; forgiving title comparison/ranking; playback flat menus; coalesced paused frame seeking with explicit commit/cancel and remote timestamp feedback; parallel independent Details sections.

Native subtitle investigation found an explicit backward `stream_seek_time` in pinned AVOS `Source/stream_subtitle.c:stream_set_subtitle_stream`. The corrective patch fences subtitle switch/decoder/queue state, removes playback pause/reseek, and discards buffered packets belonging to the old subtitle stream. It is applied after the retained 4.1.4 patch in both build workflows. Native compile/runtime validation is still required; do not infer frame conformance from source alone.

## Decisions resolved during implementation

Language defaults were explicitly selected: English→US, Portuguese→Brazil, Chinese→China, Spanish→Spain, Arabic→Saudi Arabia; other generic languages use standard likely-region data. Explicit region tags take precedence. Bundled country PNGs avoid font/glyph variation. Provenance: docs/design/LANGUAGE_FLAG_ASSETS.md. Version fields remain unchanged because numbering is still NEEDS DECISION; this does not block a candidate using the established lineage.

## Pending gates

Complete all scope corrections and per-requirement tests; automated source/full suites; actual rendered geometry/focus review; lint/debug/native playback/release/signature/upgrade validation; conformance register; exact source/APK/hash evidence. No next candidate has yet been produced. Only subsequent user Shield QA can confer SHIELD ACCEPTED. Existing accepted return focus, clock, action containers, lower navigation, input/posters, provider logos/Done, HUD reveal/control count/style and no debug text remain regression protections.

### Source validation checkpoint

Source workflow 37560414870 compiled the first checkpoint and ran the targeted tests; three Details tests retained superseded expectations (ten cast people, old Library Information tag, and old Streaming Availability heading). Updated them to assert the new eight-person cap and Technical Information/Region while retaining empty reception, unknown file-size, lower-navigation and return-focus coverage. This is not a green validation claim. Further corrections add inline Left-edge Move/Hide, separators, matching Search row geometry, menu ordering, valid-empty-only logo negative caching, unchanged-section refresh fencing, and bounded 400ms Featured motion.

### Consolidated corrective validation pass

Added actual Featured geometry/render assertions (86% active width, raised rounded card, 5–8% exposed neighbours), inline Move/Hide Back/opener coverage, and native playback frame/target/commit/cancel evidence using a changing synthetic clip. Fixed the provider action arrow handler that skipped the otherwise focusable action. Audio Boost/Night Mode HUD changes now use session-only native setters; Audio Sync does not expose/save a persistent default. Settings/Network geometry uses measured navigation text ink bottom, with balanced outer padding and visible panel bottoms. Metadata completion publishes independently in completion order, including local recommendations before slow provider reconciliation. These changes still require the complete automated/native/render gates; physical Shield remains pending.

### Details tier navigation and carousel conformance

The new text-only Cast/Crew tier sits below the information tier. UP from its first row deliberately enters the aligned information column, then the selected Details tab; only another UP expands the hero. The old single-tier test expected to skip the new information tier, so it now verifies the whole approved two-tier path while retaining the collapsed-hero assertions. Home rail sizing exposes a deliberately partial continuation and reserves vertical glow space; More Like This is also one horizontal category. Added an exact Network text-edge-to-viewport margin assertion and ISO 639-2 bibliographic language-alias coverage.

### Release resource and provider refresh corrections

Explicitly retain dynamically resolved bundled flag drawables through release resource shrinking. Unchanged provider identity/artwork now preserves its existing view/focus and only refreshes the action callback; all three hero actions use the same explicit 38dp height. Nested Settings regression asserts left-panel ancestry. Embedded trailer document explicitly fills its 16:9 WebView, avoiding HTML percentage-height ambiguity. The complete conformance register is `next-corrective-conformance.md`; it separates implementation from pending automation/render/Shield acceptance.

### Validation follow-up

Workflow 37561857959 caught a Featured render-fixture type mismatch; corrected the fixture to decorate actual Featured image views rather than pass them to a navigation-only helper. Added rendered-pixel enlargement/boundary evidence and persistent-vs-session Audio preference assertions. The logo negative-cache namespace is versioned to avoid inheriting older failed-image timestamps; positive bitmap caches are retained. Extended standard language data to ISO 639-3 and bibliographic aliases without a large generated Java initializer. These are corrective follow-ups, not validation acceptance claims.

### Full-suite and render review follow-up

Full source run 37562196171 completed the targeted gate, then exposed four full-suite failures. Updated superseded Done/horizontal-person assertions to assert live persistence and vertical text-only people; retained stable person diagnostics. Match result replacement now restores the remembered keyboard key before removing the focused results, avoiding platform focus fallback changing that key. Move/Hide Back/Right is centrally contained even before the first overlay layout, with scheduled initial control focus. Actual Home/Featured renders exposed a square left corner from an unclipped gradient layer; explicit rounded child drawing plus pixel-corner assertions now cover all layers. Reviewed Network panel fit/centred margins, library toolbar order/divider geometry, Search/Match slot hierarchy, focused-card enlargement and Details columns. Review does not grant Shield acceptance. Episode card bounds, series genre refresh, Settings panel renders and trailer callback wiring were added as further regressions. Dedicated FF/RW native-gate screenshots use immediate capture for transient feedback, preserving paused-HUD visibility and scheduling bubble timeout refresh.

### Green source suite and native subtitle recovery gate

Source run 37562856028 passed targeted tests, the complete Video suite, stability/library checks and WebDAV checks at d8919de023764bd1d7f2276dd1e517c600c151f6. Signed build 37562856029 then found AppCompatCustomView lint on Featured's custom artwork subclass; corrected it to AppCompatImageView without suppressing lint. Native inspection found that simply removing the subtitle-change reseek would discard unselected-track cues already demuxed. The bounded native patch now retains all known internal subtitle tracks in a four-MiB/4096-packet cache, expires completed cues (one minute for unknown-duration bitmap events), restores only selected subtitle packets, and clears history on actual seek/close. Audio/video queues, playback position and transport remain untouched by subtitle selection. Cache saturation can limit historical bitmap decoder context and requires physical format coverage; no unlimited cache is introduced. The native gate uses two real subtitle streams with long cues preceding selection, verifies the paused frame/position and successful native selection, then resumes normally and requires the French caption. This is distinct from callback-only verification and is not Shield acceptance. All gates for this follow-up remain pending.
