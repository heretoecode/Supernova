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
