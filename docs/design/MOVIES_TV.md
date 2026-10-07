# Movies & TV — Design and Behaviour Authority

Status: **APPROVED/current 4.1.7 baseline; current Shield corrections noted**

## Library composition
- Preserve the liked six-wide poster grid. The current implementation uses a 24-span grid with four spans per poster.
- Shared card focus enlargement remains approximately 1.08×. Artwork, rounded boundary and outward glow behave as one aligned unit; artwork must not escape its rounded boundary.
- Movies has Continue Watching (Movies only) at the top, then the library. TV mirrors the Movies layout.
- Earlier Genre/Year/Collections browsing rows were deliberately removed from the primary Movies layout; filtering belongs in the toolbar.
- Indexed summary may expose total/local/network counts and storage without fabricating unknown sizes.

## Toolbar
Controls sit close to the divider and visually echo Details lower navigation:
**Filters · Sort · Order · Unmatched · List/Grid · Columns (where applicable)**.
White icon/text; no cyan focus text and no large rounded focus box. Focus is the corresponding divider segment turning Supernova blue with restrained optical glow. Unlike Details, no blue segment persists after toolbar focus leaves.
Edges are deterministic: first+LEFT stays; final+RIGHT stays; DOWN enters library/header. Columns+RIGHT never falls into an unrelated header.

## Grid/list navigation
- Terminal grid RIGHT stays; no wrap/jump.
- Top-row UP goes to toolbar.
- Bottom edge stays.
- Horizontal row lock remains until explicit vertical movement.
- Active row positioning should keep the focused row comfortably visible; current Shield QA requires correction here.
- Current Shield QA also requires toolbar/control-bar vertical alignment correction.

## Remembered library view state — APPROVED
Grid/List mode, Filters, Sort and Order are remembered separately for Movies and TV Shows. Preserve these preferences across navigation and background updates unless the user explicitly changes/resets them.

## TV status metadata — APPROVED historical behaviour
Keep grid title to one line with secondary metadata. Normal TV items may show season/episode totals; an active series may show `Up Next · Sx Ey` where this remains compatible with the current card layout.

## Filter / Sort / Order visual lineage
Historical approved direction used compact slate-blue/dark translucent TV-friendly popups with restrained backdrop dim, clear current-value checkmarks and human-readable terminology. Do not regress to giant bright-blue rows or grey stock Android dialogs. Current global visual/focus authority controls the exact accent treatment.

## Exact return focus
Movies/TV → Details → Back restores exact originating media item, scroll position, view mode and visible focus. List mode restores the exact row. Never fall back to the leftmost item or top navigation when the original target is still valid.

## List view
Switching Grid/List, Columns changes and background metadata updates must not cause full flicker/rebuild. Sort is one criterion; Columns controls visibility/order. Codec, bitrate, HDR and other technical values are populated/cached by scan/background work; list focus must not trigger extraction.

## Filters
- Genre contains only genres actually present; multi-select + Done.
- Year multi-select + Done.
- Streaming Service persistent multi-select + Done; ticks retained; real provider icons rendered monochrome without destroying internal logo detail.
- Provider availability is background-populated, not dependent on opening Details.
- Clear Filters works.
- Streaming Service filtering concerns local titles with known fresh availability; it is not a remote-catalogue browser.

## Unmatched
First-class toolbar workflow. Classification and metadata identity are separate. Movies Unmatched = confidently Movie + shared Unknown records. TV Unmatched = confidently TV + the same underlying Unknown records, without duplication. Matching removes the item from Unmatched and returns it to normal library.
Unmatched Details uses honest Not matched/Unavailable/Unknown placeholders and a prominent Match Metadata action.

## Artwork
A historical Shield defect caused some Movies posters to disappear after Scan Library while text/Details art remained. Binding-generation fencing/retry was added, but current physical QA still requires artwork-request/failure investigation. Do not claim all artwork failures are one cause.

## Current corrections
4.1.7 Final Shield QA: vertical focused-row positioning; control-bar vertical alignment; exact Details return focus; artwork request/failure behaviour.

## Evidence
`docs/preview-4.1.7/REQUIREMENTS.md` UI-012–021; `docs/archive/root-history/NOVA_CODEX_RETURN_HANDOVER_4.1.2.md`; `docs/qa/4.1.7-final-shield-qa.md`.

## Historical approved visual references recovered from Project Library
A legacy `NOVA_DESIGN_REFERENCES` record dated 16 September 2026 was recovered during the preservation pass. It is historical evidence; later written 4.1.7 decisions override conflicts. It records these mock-up identifiers so the visual lineage is not lost:
- List View + Genre/Sort/Order: `bb6ed615-ca35-4578-88c5-5855ad766c4d`
- Broad UI states: `9f0856cb-2d33-49af-999d-734853aea861`
- Primary Details: `db06b3cb-2136-4b1c-a1b9-69b01db3b80c`
- Network compact Option 3: `bb06fd93-a13b-49b3-8525-97eadce38c3b`
- Network no-divider/cyan-nav: `063d1a2d-4380-4db3-8251-6025c32bfb8a`
- Custom red-ribbon concept: `8703e387-1c95-4752-a284-7f988338f811`
- Playback current/end-time HUD: `7435ef99-a0f1-443d-b416-e2c44364e07c`
- Playback tighter-bottom: `4287b280-80d5-4023-b6f1-e4b47b57cf29`
- Playback compact controls: `b483b883-3a0e-4042-8525-9d7cb4778d26`
- Seeking thumbnail: `f36ed05a-13e5-4183-bf3d-e4d898240cdb`

The same historical record explicitly rejected permanent left-sidebar primary navigation, two-line grid titles, empty Continue Watching placeholder panels, huge bright-blue list rows, grey stock Android filter/sort/order dialogs, “NEW” Settings badges, invented analytics settings, an assistant-invented launcher icon, and generated Network/Settings mock-ups that invented unsupported features.

**Asset status:** the identifier/provenance record is recovered; the corresponding original generated image bytes are not currently available as a complete recoverable image set. Identifiers are not substitutes for images. If image bytes are recovered later they should be committed under `docs/design/assets/` with a manifest and status.


See `docs/project/HISTORY_RECONCILIATION.md` for recovered historical decisions and supersession context.


## Next-version toolbar and filter amendments — APPROVED

These rules supersede conflicting older toolbar/filter wording above.

### Toolbar
Grid: **Grid/List · Filters · Sort · Order · Unmatched (only when present)**.

List: **Grid/List · Filters · Sort · Order · Columns · Unmatched (only when present)**.

- Grid/List is leftmost.
- Sort and Order remain separate.
- Order has an explicit label, e.g. **Order: Newest First**; an icon plus value alone is insufficient.
- Columns is List-only where applicable.
- Unmatched is always rightmost and hidden entirely when no unmatched media exists.
- Move the complete control row down so controls visually sit **on the divider line**, not above it.
- Preserve separate remembered Movies and TV state.

### Live filters
A tick is the confirmation. **Remove Done** from Genre, Year and Streaming Service filter selection.

Genre: **All Genres** at top, available genres in the middle; selecting/toggling immediately updates the library. Back only navigates out. All Genres clears individual genre selections immediately.

Year: show only years represented in the current library; tick/untick applies immediately; keep **Clear Selection**; remove Done.

Streaming Service: tick/untick applies immediately; remove Done.

### Compact summary
Never concatenate an unbounded selected-value list into the toolbar. Use **Filters** when none, **Filters: Crime** (or equivalent concise value) for one, and **Filters (N)** for multiple selections. Submenu ticks show the exact selections.

### Vertical movement
Correct the physical Shield snapping/jumping introduced by row-centering. Keep the active row comfortably/approximately centred where possible, but movement must be smooth and deterministic and exact return restoration takes precedence.
