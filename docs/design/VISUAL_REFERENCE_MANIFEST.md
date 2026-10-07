# Supernova — Visual Reference Recovery Manifest

Status: **ACTIVE / audited 7 October 2026**

This manifest records what was actually recoverable from Project/Library history after the next-version scope was frozen. It prevents old mock-ups, generated iterations or identifiers from being silently treated as current authority.

## Authority rule

1. `docs/qa/next-version-authority.md` and the current written `docs/design/` specifications are authoritative.
2. An image is normative only for the element explicitly identified here or in the current design file.
3. Later written decisions override conflicting pixels/text in an older mock-up.
4. Do not infer features, navigation, labels, colours or data from accidental/generated mock-up content.
5. A mock-up identifier is provenance only; it is not a substitute for image bytes.

## Recovered current visual material

### Details — latest approved information/Cast/Crew composition

**RECOVERED as actual image bytes in Project/Library history.**

Library item observed during recovery:
- `Dune: Cinematic Details Interface(2).png` — created 6 October 2026 14:36 UTC.

The image matches the latest approved composition now written in `DETAILS.md`: hero above; Key Information / Technical Information / Reception tier; divider; text-only Cast / Crew tier. It is visual authority for **alignment, hierarchy, spacing and overall composition only**.

Important: generated artwork, example film metadata, provider button artwork, ratings/award sources and any accidental typography/content in the image are illustrative. Current written Details authority governs actual fields, legality/reliability and behaviour.

The original Library bytes are recoverable, but this GitHub connector's normal file-content action accepts UTF-8 text only. Do not pretend the binary was committed when it was not. A locally recovered lossless copy was verified during this audit; transfer to `docs/design/assets/` remains a binary-ingestion task.

### Playback HUD menu board

**RECOVERED as actual image bytes in Project/Library history, but SUPERSEDED AS MENU-CONTENT AUTHORITY.**

Library item:
- `Supernova Playback HUD Menus.png` — created 6 October 2026 23:30 UTC.

This board documents the physical/current menu state that was discussed during QA. It is useful provenance for visual footprint and the four-control HUD, but its menu contents are **not** the final requirement. `PLAYBACK.md` and `next-version-authority.md` supersede it:
- Subtitles = tracks/Off · Download · Sync · Appearance.
- Audio = tracks · Audio Sync · Audio Boost · Night Mode.
- More = Playback Speed · Play Mode · Format.
- Report a Problem removed.
- no full Settings links.

As above, original bytes are recoverable from Library history but not falsely represented as committed binary assets.

## Recoverable historical visual provenance

The Library contains `NOVA_DESIGN_REFERENCES*.md` with these retained identifiers:
- List View + Genre/Sort/Order: `bb6ed615-ca35-4578-88c5-5855ad766c4d`
- Broad UI states: `9f0856cb-2d33-49af-999d-734853aea861`
- Primary Details: `db06b3cb-2136-4b1c-a1b9-69b01db3b80c`
- Network compact Option 3: `bb06fd93-a13b-49b3-8525-97eadce38c3b`
- Network no-divider/cyan nav: `063d1a2d-4380-4db3-8251-6025c32bfb8a`
- Custom red-ribbon concept: `8703e387-1c95-4752-a284-7f988338f811`
- Playback current/end-time HUD: `7435ef99-a0f1-443d-b416-e2c44364e07c`
- Playback tighter bottom: `4287b280-80d5-4023-b6f1-e4b47b57cf29`
- Playback compact controls: `b483b883-3a0e-4042-8525-9d7cb4778d26`
- Seeking thumbnail: `f36ed05a-13e5-4183-bf3d-e4d898240cdb`

These are historical provenance, not current binary references.

## Current Library image inventory found during recovery

Project/Library history also contains named generated images for:
- multiple Dune Details iterations;
- Supernova Search iterations;
- Supernova Movies/TV library mock-ups;
- custom-page/Documentaries concepts;
- Home/streaming/hero concepts;
- numerous physical Shield screenshots;
- current Playback HUD menu board.

**Do not bulk-import these.** Many are intermediate, superseded, parked, or physical-QA evidence rather than approved next-version design. Importing by filename/date alone would create false authority.

## Home Featured exposed-card design

**Written authority recovered; exact approved original image not conclusively matched.**

The next-version design remains fully specified in `HOME.md`: dominant raised centre card (~80–88% usable Featured width), ~5–8% neighbouring-card exposure, right-side artwork/gradient, fixed information/action geometry, Play/Resume + More Info, LEFT/RIGHT carousel behaviour and ~350–450ms depth/translate transition.

Several later Home/streaming/hero images exist in Library history, but this recovery did not find evidence strong enough to label one particular binary as the exact approved Featured mock-up. **Do not choose one by guess.** Written authority governs until exact provenance is recovered.

## Movies / TV

The Library contains `Supernova Movies Library UI Blueprint.png` and multiple `Supernova TV Shows Library*.png` images plus older List View identifiers. They are not promoted wholesale because the latest toolbar/filter decisions were made after those images:
- Grid/List first;
- Filters;
- Sort;
- explicit Order;
- Columns in List only;
- Unmatched last and only when present;
- controls sit on the divider;
- live filters; no Done.

Current written `MOVIES_TV.md` is therefore the normative authority.

## Settings / Network & Files

Older visual references exist, but the latest three-panel geometry and Settings nesting decisions post-date/supersede parts of them. Current written `SETTINGS.md` and `NETWORK_FILES.md` are normative. Do not restore the historical Network tile landing or older Settings nesting because an old mock-up depicts it.

## Search / Find a Match

Multiple Search mock-up binaries are present in Library history. Current Search visual lineage can be consulted, but Find a Match must use normal Search as its visual authority and the latest keyboard/matching requirements in `SEARCH_MATCHING.md`. No older image overrides those written corrections.

## Binary recovery status / next safe action

The audit found **two actual image files with sufficiently strong provenance to classify** (latest Details composition; Playback current-menu board). Their bytes are recoverable in Project/Library storage. Other named images are available but cannot safely be labelled approved without stronger provenance.

Until binary transfer into GitHub is available, Codex must:
- use the current written design authority;
- not invent missing mock-ups;
- not substitute an arbitrary generated iteration;
- treat this manifest as the visual-provenance ledger.

When a verified binary is committed later, place it under `docs/design/assets/`, record its SHA-256, date/provenance and exact authoritative elements here, and link it from the relevant design document.
