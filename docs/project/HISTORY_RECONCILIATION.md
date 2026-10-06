# Supernova — Full History Reconciliation Register

Status: **DURABLE HISTORICAL / PRODUCT AUTHORITY SUPPORT**

Purpose: preserve material recovered from older project records and discussions that is still useful for reconstructing Supernova without ChatGPT history. This file does not override later surface-specific design authority. Use status labels and supersession notes below.

## Authority rules
- Current authority remains `PROJECT_STATUS.md`, the current QA scope, `docs/design/`, and current source.
- Later written decisions override older designs when they conflict.
- Historical mock-ups are authoritative only for aspects explicitly approved.
- Exploratory ideas are not silently promoted to requirements.
- Missing original visual bytes must not be recreated and labelled as originals.

## Recovered historical design decisions

### Library list/filter presentation — HISTORICAL APPROVED, retain where not superseded
- Filter / Sort / Order panels used a slate-blue/dark translucent TV-friendly popup direction.
- Rounded restrained backdrop dim.
- Focus treatment used the then-current accent outline/checkmark.
- Human-readable sort/order terminology; preserve working sorting semantics.
- Explicitly rejected: giant bright-blue list rows and grey stock Android dialogs.
Later global visual authority controls exact current accent/focus treatment.

### Home artwork fallback — APPROVED historical behaviour
When landscape artwork is unavailable and only poster-shaped artwork exists, preserve the artwork without destructive centre-cropping; letterbox/framing is preferred to turning a poster into an arbitrary landscape crop.

### TV grid status — APPROVED historical behaviour
Keep title to one line with secondary metadata. Normal shows may expose season/episode totals; an active series may expose an `Up Next · Sx Ey` state using the Supernova accent where compatible with current layout.

### Library view preferences — APPROVED historical behaviour
“Remember library view preferences” means Grid/List, Filters, Sort and Order are remembered separately for Movies and TV Shows. Preserve preference semantics even if the setting's later location/name changes.

### Scanning safety/system behaviour — APPROVED engineering intent
Before changing mature scanning behaviour, profile it. Desired invariants:
- one scan at a time; overlapping triggers are ignored/coalesced into at most one follow-up;
- temporarily unavailable NAS/share must not be interpreted as mass deletion;
- no temporary duplicates;
- cached artwork remains visible during refresh;
- background updates must not steal D-pad focus or reset scroll;
- interrupted scans recover safely;
- favour cached startup, incremental refresh, reduced unnecessary network probing and safe batched database work.

### Focus/D-pad — APPROVED systemic principle
Treat focus as state/navigation architecture, not scattered `requestFocus` patches. Historical protected behaviours included top-nav activation remaining on the selected item until deliberate Down, Details entry starting at Play/Resume, and Back restoring title/position with normal neighbour relationships. Current QA/design files define the exact current requirements.

### Playback seek / timing — HISTORICAL APPROVED DIRECTION, subject to current HUD authority
- Current time plus predicted end time was approved for the top-right HUD.
- Seeking direction included target timestamp/chapter information and favoured a thumbnail preview above the scrub position.
- No primary app navigation/solid top bar during playback.
Later physical Shield QA supersedes the old primary-control composition: the current primary controls are exactly Subtitles · Audio · Play/Pause · More.

### Launcher/custom identity — HISTORICAL PREVIEW DIRECTION
The real Nova Android TV launcher/banner asset was the source of truth for the old custom Preview identity, with a red CUSTOM corner ribbon/tag to distinguish it from official Nova. An assistant-invented launcher icon was explicitly rejected.
This is historical preview guidance, not authority for the later clean Supernova identity transition, which remains a separate future phase.

## Superseded structures retained for history

### Settings 13-category structure — SUPERSEDED
An older approved foundation used:
General · Home & Discovery · Playback · Video & Audio · Subtitles · Library · Sources & Storage · Appearance · Trakt · Streaming · Integrations · Advanced · About.
It was later replaced by the current 12-category Settings authority in `docs/design/SETTINGS.md`. Do not restore the old category list merely because it appears in historical handovers.

### Network compact 4×2 landing — SUPERSEDED as current architecture
An older approved Network & Files foundation used compact source/location tiles, approximately four columns by two rows, with no permanent sidebar. Later requirements evolved this into the current three-panel Network & Files architecture. Preserve the old design as lineage only.

### Details Trailer-on-hero direction — SUPERSEDED
Older Details direction placed Trailer beside Play/Resume/More and favoured direct playback of the best confident trailer. Later Details authority moves Trailer into Extras. Current `docs/design/DETAILS.md` wins.

### Older playback primary controls — SUPERSEDED
Older HUD concepts included Audio & Subs, Chapters and Info as compact secondary controls. Current physical-QA authority removes Info from the primary HUD and specifies exactly four primary controls. Technical information itself is not deleted; its final secondary path remains governed by current authority.

## Explicit historical rejections
Do not revive without a new explicit decision:
- permanent left-sidebar primary navigation on Home/Movies/TV/Network & Files;
- two-line library grid titles;
- empty Continue Watching placeholder panel (hide the row when empty);
- huge bright-blue List View rows;
- grey stock Android filter/sort/order dialogs;
- “NEW” Settings badges;
- invented analytics settings;
- assistant-invented launcher icon;
- generated Network/Settings mock-up features that did not exist in the product.

## Historical visual identifiers
Recovered identifiers (not image bytes):
- List View + Genre/Sort/Order: `bb6ed615-ca35-4578-88c5-5855ad766c4d`
- Broad UI states: `9f0856cb-2d33-49af-999d-734853aea861`
- Primary Details: `db06b3cb-2136-4b1c-a1b9-69b01db3b80c`
- Network compact Option 3: `bb06fd93-a13b-49b3-8525-97eadce38c3b`
- Network no-divider/cyan-nav: `063d1a2d-4380-4db3-8251-6025c32bfb8a`
- Custom red-ribbon concept: `8703e387-1c95-4752-a284-7f988338f811`
- Playback current/end-time HUD: `7435ef99-a0f1-443d-b416-e2c44364e07c`
- Playback tighter bottom: `4287b280-80d5-4023-b6f1-e4b47b57cf29`
- Playback compact controls: `b483b883-3a0e-4042-8525-9d7cb4778d26`
- Seeking thumbnail: `f36ed05a-13e5-4183-bf3d-e4d898240cdb`

Original bytes are not known to be recoverable as a complete set. Identifiers are provenance only.

## Historical workflow — SUPERSEDED
Older workflow used Google Drive “Nova video player preview”, timestamped handover ZIPs and “latest handover wins”. That was valid at the time but is superseded by GitHub as durable project memory and current repository authority. Old handovers remain evidence, not the current control plane.

## Future-product discussions recovered
The durable future authorities are `docs/design/PARKED_FUTURE.md` and `docs/project/DECISIONS_AND_ROADMAP.md`. Historical discussion also explored architecture/language modernisation and AI-assisted product capabilities. Those conversations did **not** establish enough final product/design detail to authorise implementation. They must remain exploration/NEEDS DECISION unless separately activated and specified.

This includes:
- selective Kotlin/Jetpack Compose modernisation versus Java/XML;
- AI-assisted or natural-language search/discovery concepts;
- richer recommendations/“Ask Supernova”-style assistance;
- use of viewing/history context and the privacy boundaries such features would require.

Do not interpret their presence here as approval to add network AI services, upload viewing history, migrate the UI stack, or broaden the product scope.

## Preservation completion rule
Future durable decisions must be written to GitHub when made. Raw chat transcripts are not project authority. If an original approved mock-up becomes recoverable, preserve its original bytes under `docs/design/assets/` with provenance/status and state exactly which aspects are authoritative.
