# Settings — Design and Behaviour Authority

Status: **APPROVED foundation; category-by-category redesign is not implicitly authorised**

## Shell
Three panels:
**Left Categories → Middle Settings/options → Right contextual explanation/current value**.
Dark/translucent Supernova styling, white type, monochrome icons, compact blue focus boundary/glow.

Permanent non-scrolling left rail with all top-level categories visible and About at bottom:
Playback · Video · Audio · Subtitles · Library & Metadata · Home · Appearance · Streaming · Network · Integrations · Advanced · About.

Entry focuses **Playback in the left rail**, not the first middle setting. Focusing a category never expands it. Indented children do not appear merely on hover. OK/Select or RIGHT explicitly enters category/subcategory. Nested subcategories stay in the middle workspace, never the permanent left rail.
Deterministic routing: rail UP/DOWN one category; RIGHT first appropriate middle; LEFT returns origin; middle UP/DOWN; RIGHT enters meaningful value/control; Back reverses hierarchy; closing a choice panel restores the exact setting.

Preserve existing preference keys, values and semantics unless an explicit requirement changes them. Compatibility controls remain Advanced; normal source scheduling belongs Network & Files. Trakt, OpenSubtitles and the real IntroDB switch belong Integrations.

## Providers
My Providers uses genuine provider catalogue artwork. Selected and alphabetical alternatives are distinct sections. Selection updates tick state in place without flashing/focus movement; regroup when reopened rather than reordering underneath the user.
Current Shield corrections: provider logos should display in colour in My Providers; **Done** must be visually separated appropriately.

## Language
Use a national flag only for a true locale such as en-GB. Generic language uses neutral language iconography. This rule applies consistently to UI language, subtitle reading language and playback subtitle-track selection.

## Current correction
Shield QA requires the intended three-panel structure and nested/indented navigation to be corrected where physical presentation diverges.

## Evidence
`docs/preview-4.1.7/REQUIREMENTS.md` UI-058–060 and UI-009; 4.1.2 audit/handover; 4.1.4 release notes; current Shield QA.


## Superseded historical category structure
An older 13-category foundation (General; Home & Discovery; Playback; Video & Audio; Subtitles; Library; Sources & Storage; Appearance; Trakt; Streaming; Integrations; Advanced; About) is **SUPERSEDED** by the current 12-category rail above. Do not restore it by accident. Historical “Remember library view preferences” semantics remain valid: Grid/List, Filters, Sort and Order are remembered separately for Movies and TV Shows.

See `docs/project/HISTORY_RECONCILIATION.md` for the historical lineage.


## Next-version Shield amendments — APPROVED / SUPERSEDING

This section supersedes conflicting nesting/language wording above.

### Outer geometry
Define usable vertical space from the **bottom edge of top-navigation text** to the **bottom edge of the TV/app viewport**. Vertically centre the complete three-panel group within that area, leaving equal remaining space above and below. Settings needs a smaller downward correction than Network & Files.

### Nested navigation
Nested Integration children belong indented in the **left panel** beneath the parent after explicit entry. Example: **Integrations → OpenSubtitles / IntroDB / Trakt**. Selecting a child populates its settings in the centre panel. Right/Left/Back and exact opener restoration remain deterministic. This explicitly supersedes the earlier rule that nested subcategories must stay in the middle workspace.

### Language
Use the approved **flag representation for languages** consistently rather than the current mixture of flags, globes and two-letter codes. Mapping must be deterministic and consistent across screens; do not silently change the represented language.

Protect physically accepted My Providers colour logos and separated Done action.


### Approved generic-language flag defaults — 7 October 2026

Explicit regional country tags take precedence. For generic language tracks/settings, use one shared mapping across all surfaces: English → United States; Portuguese → Brazil; Chinese → China; Spanish → Spain; Arabic → Saudi Arabia. These defaults were explicitly selected during implementation clarification. They identify the UI representation, not a change to the track language or nationality. Other ambiguous language mappings require an explicit project default before country assignment.

Other generic languages use the standard CLDR likely-region mapping, explicitly approved during implementation clarification. Flag assets are bundled to avoid Shield font fallback; see [LANGUAGE_FLAG_ASSETS.md](LANGUAGE_FLAG_ASSETS.md).
