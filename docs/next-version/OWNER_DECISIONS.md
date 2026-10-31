# Owner decisions after PR #29 continuation

No owner decision blocks the current source-backed implementation. Your latest instructions and strict-matching answer supersede the earlier engineering approval questions; they do not lock a new visual treatment for Edit Library.

| Area | Resolved direction / implemented result |
|---|---|
| Playback HUD | Approved composition retained. Source conventions resolve measurements and remote graph: seek first; Up → Settings, Down → Play/Pause; Settings Down / Play-Pause Up → seek. Ends at hides paused/unknown. Actual timing, focus and layout regressions pass. No renewed HUD approval requested. |
| Playback Settings | Approved Audio / Subtitles / Video / More inline interaction, with actual native capability/range parity. No new settings. |
| ChaptersDB | Your answer: **Keep unverified sets hidden; preserve strict matching**. Bounded acquisition/cache and identity paths are wired. Official API explicitly permits unauthenticated integration. Current v1 data lacks cut/runtime proof, so unverified candidates never become visible markers. No override flow or renewed legal/matching question. |
| Edit Library | Four sections, exact filter/display parity, actual indexed preview and stationary frame are implemented using existing shared .24/.38/.38 proportions and typography. Both naming, Delete in Page Details and pinned in-panel Review Save remain. Retaining them requires no approval. |

## Optional visual revisions requiring a personal choice

These remain unadopted and do not prevent use or testing of the existing implementation:

1. **New editor proportions and styling:** whether to replace the existing .24/.38/.38 shared geometry with the proposed 22/35/43 split and a new type/spacing/card/statistics/poster-grid treatment. Exact candidate styling remains draft under [blueprint D](../design/EDIT_LIBRARY_STRICT_UI_BLUEPRINT_DRAFT_2026-10-30.md); no generated-image measurements are treated as approved. The current source-backed rendition can be reviewed in [passing four-section captures](continuation/software-captures-f5e2924/README.md). Original proposal diagrams remain in design-proposals/ and are labelled accordingly.
2. **Delete location:** whether to move the existing confirmed Delete Page action from Page Details to Review. The current placement and confirmation are preserved; no action is needed to keep them.

No extra decision is required for Both/Mixed naming, Save placement or statistics while preserving the existing source conventions. No filter expansion is proposed.

## Evidence boundaries, not unanswered preferences

The external chapter limitation requires authoritative edition/runtime evidence from the provider or an independently verified per-edition contract. Votes, a last chapter start, title text and episode ordinal alone cannot supply it. Your suppression policy already decides current behaviour. Observed API contracts and permission statement: CHAPTERSDB_CONTINUATION_RESEARCH.json.

Physical Nvidia Shield, original Concept A4 reference comparison and signed installed upgrade remain **NOT RUN**. Software tests/captures do not close hardware reports or establish real playback latency. Device procedures are in SHIELD_ACCEPTANCE.md. Existing signed 0.147 delivery, protected branches and signing inputs remain preserved.

Earlier alternatives and diagrams: [historical packet](design-proposals/HISTORICAL_DECISION_PACKET.md). Current engineering tokens, scope and validation: CONTINUATION.md and STATUS.md.
