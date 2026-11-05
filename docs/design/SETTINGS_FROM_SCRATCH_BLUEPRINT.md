# Supernova Settings — Ground-Up Design Blueprint

Status: ACTIVE DESIGN WORKING DOCUMENT — not approved for implementation.
Updated: 2026-10-26.
Source of truth for functionality: docs/audits/settings-2026-10-26/{AUDIT,INVENTORY,NAVIGATION,ISSUES,RECOMMENDATIONS,SHIELD_QA}.md, commit 3ed6e7e8a794940ab4a0ff22fb1a1086f3e3987a.

## Non-negotiable approach
Build a NEW Settings interface from scratch, not a visual refurbishment of the inherited NOVA Settings UI. The audit is the functional source of truth: preserve real controls, dependencies, preference keys, values, and integrations. Do not invent settings. Codex chooses safe implementation architecture only after designs are approved. No code changes to release 0.142, PR #20, main or the active release branch as part of design.

## Design sequence
1. Design and explicitly approve the main Settings grid.
2. Design and approve EACH category page individually, beginning with Playback & Video.
3. Map every audited control into the agreed eight categories and subsections, including entry points, advanced controls, dependencies, and navigation.
4. Only then prepare an implementation handover. No implementation approval yet.

## Eight agreed top-level categories — EXACT names and order
Visual order of the APPROVED grid is defined in the reproducibility contract below.
Row 1:
1. Playback & Video — playback behaviour, video presentation and controls.
2. Audio & Subtitles — audio output, languages, subtitle behaviour.
3. Library & Home — library metadata, library display and Home rows.
4. Streaming & Providers — streaming services, availability and provider preferences.
Row 2:
5. Appearance — language, visual preferences and presentation.
6. Connections & Integrations — network connections and service integrations.
7. System & Maintenance — backup, diagnostics, compatibility and maintenance.
8. About Supernova — version, release notes, credits and licences.

These eight labels supersede mistaken mock-up substitutions: NOT 'Library & Content', NOT 'Network & Files', NOT 'Resume & Continue'. Network & Files is a separate dedicated application area, not a replacement top-level Settings category. Resume and Continue are behaviours distributed within the agreed categories, not a top-level category.

## Main grid — APPROVED 2026-10-26
- Full-screen 16:9 Nvidia Shield / Android TV screen, 4 columns x 2 rows, all eight cards visible without scrolling.
- Use the **official, previously approved Supernova rebranding-release background asset**, unchanged, as the Settings background. No new generated nebula, planet, gradient or substitute background. Locate and verify the canonical asset in the repository before implementation; if missing, flag rather than recreate it.
- Supernova brand typography; consistent simple monochrome category icons; title plus concise functional description per card.
- Clear electric-blue focus border and modest glow; unfocused cards understated; TV-readable sizes and contrast.
- Balanced margins and vertical position; no wasted space, extra panels, duplicate top-level navigation or decorative filler.
- D-pad traversal predictable; Enter opens category; Back returns to grid and restores prior focused card.
- Category descriptions must match the approved eight-category intent; final exact strings below are implementation copy. No arbitrary on-screen clock time should be treated as a functional requirement.


## Main grid reproducibility contract — APPROVED
**Priority:** approved requirements and canonical app assets > the approved visual mock-up > numeric candidate tokens. Image generation is a visual reference, not pixel-accurate source or an approved background. Do not silently invent missing fonts, icons, assets, strings or measurements.

### Exact on-screen content and ordering
Display 4 columns in each of 2 rows, left to right:
| Row | Column | Title | Description | Icon intent |
|---|---:|---|---|---|
| 1 | 1 | Playback & Video | Control how content plays, resumes and looks on screen. | Outline play triangle |
| 1 | 2 | Audio & Subtitles | Configure audio, subtitles and related playback options. | Outline speaker/sound |
| 1 | 3 | Library & Home | Manage your libraries, metadata and Home screen displays. | Outline filmstrip |
| 2 | 1 | Appearance | Customise the look and feel of Supernova. | Outline display/monitor |
| 1 | 4 | Streaming & Providers | Set up and manage streaming providers and availability preferences. | Outline cloud/service |
| 2 | 2 | Connections & Integrations | Manage external connections, accounts and integrations. | Outline connected nodes |
| 2 | 3 | System & Maintenance | Diagnostics, backup and other system options. | Outline gear |
| 2 | 4 | About Supernova | Version information, credits and legal details. | Outline information circle |

**Approved spatial order:** row 1 = Playback & Video, Audio & Subtitles, Library & Home, Streaming & Providers; row 2 = Appearance, Connections & Integrations, System & Maintenance, About Supernova. This matches the corrected approved visual. Do not change labels or swap cards without approval.

### Implementation requirements
- Main grid is a single landing screen. No permanent sidebar, no third pane, no scrolling at standard TV aspect ratio, no preview widgets.
- Existing canonical Supernova background fills viewport using the app's existing scaling/crop behaviour. Do not embed the generated concept image in the application.
- Reuse existing app wordmark/logo typography, approved font resources, palette and official background. Find their actual repository paths and record them in an asset manifest before coding. Missing assets are a blocker requiring escalation, not permission to substitute.
- Card geometry and typographic tokens above are initial implementation targets; maintain relative composition and readable hierarchy while tuning against Shield captures. Use weighted responsive columns rather than fixed screenshot pixels.
- Selected focus initially on Playback & Video when entering Settings fresh. On return from a category page restore its grid card. Left/right moves one column; up/down moves one row preserving column; no wrapping unless separately approved. OK opens selected category; Back exits Settings through existing app navigation.
- Focus state must have an unmistakable blue outline and restrained surface tint; unfocused cards remain readable. Avoid focus zoom that clips the card.
- Header: existing Supernova brand at left, thin divider, 'Settings'. If the existing app header provides a real clock, reuse it; the image's arbitrary 19:24 is NOT a hardcoded time requirement.
- Use the exact eight labels above. No additional top-level cards, 'Network & Files', 'Resume & Continue', or 'Library & Content'.
- Long descriptions/localisation must wrap without overlapping card borders; use dynamic text sizing only within accessibility constraints. No ellipsis on English approved strings at reference resolution.
- Test card order, D-pad focus, restoration, back, typography, clipping, overscan, actual background and accessibility on Nvidia Shield. Capture screenshots at 1080p and 4K and compare with approved composition.
- The eight category destinations may be stubs only during isolated grid development, but the finished Settings feature is not complete until all category pages and audit-mapped controls are approved and implemented.

### Required asset and implementation evidence before Codex starts
1. Exact official background repository path, resource name, hash and scaling mode.
2. Actual font family names/resource paths for brand, headings and body, with fallback behaviour.
3. Actual colour resource names/values for focus, surfaces and typography.
4. Icon asset paths or a named consistent vector icon set with eight mapped glyphs.
5. A screenshot reference of the approved corrected grid and the revision/date, committed to GitHub if the original can be reliably obtained.
6. Actual Shield screenshots and focus-navigation QA results. Do not claim pixel-perfect fidelity without comparison.
**Unverified today:** exact canonical background/font/icon paths and original image attachment availability in GitHub. This blueprint records the required identification and validation work, rather than pretending those details have been checked.

## Category page design direction — preliminary, NOT YET APPROVED
- A selected grid card opens a dedicated category page; do not repeat all eight top-level categories in a sidebar.
- Left column contains only subsections of that category; right column shows settings.
- Individual settings use short descriptions, proper toggles where applicable, and subtle separators; blue remote focus.
- No permanent third information panel or decorative preview; fewer subsections where naturally appropriate.
- Exact subsection names, controls, defaults and states must be checked against the audit before mock-ups or implementation.


## Design tokens and dimensional specification — APPROVED VISUAL DIRECTION; NUMERIC TARGETS REQUIRE DEVICE VALIDATION
These are measurable implementation targets for design review, NOT measurements verified from a generated image. Codex must validate against Shield screenshots, TV scaling, font assets and actual layout bounds. Use Android **dp** for geometry and **sp** for text, not raw 1920x1080 screenshot pixels. Reference canvas: 1920x1080 at 16:9, layout reference 960x540 dp equivalent at 2x scale; final dimensions adapt to device density, font scale and TV safe area.

### Typography
- Brand wordmark: use the **existing Supernova wordmark font asset** already used by the app; do not silently substitute a different family. Exact file/family must be identified from the repo before implementation. Candidate displayed size 23sp, medium, tracking +0.20em.
- Page title “Settings”: existing Supernova UI font family (identify exact asset/style in repository before implementation); candidate 28sp semibold.
- Category card title: 22sp semibold, line height 27sp; two lines allowed if localised.
- Category description: 16sp regular, line height 22sp, max 3 lines with graceful wrapping.
- Category page subsection label: 19sp medium; selected subsection 20sp semibold.
- Individual setting title: 19sp medium; explanation: 15sp regular, line height 21sp.
- Secondary/help text: 14sp minimum where needed. Do not compress below comfortable TV viewing size to force content.
- White/off-white primary; muted grey-blue secondary with accessible contrast. No decorative tracking on body copy. Exact typography and colour tokens pending actual font asset verification and visual approval.

### Grid geometry — 960x540 dp reference (calibration targets, not verified measurements)
- TV-safe outer inset: 28dp left/right; 24dp top/bottom minimum; respect Android overscan/safe bounds.
- Header horizontal start: 28dp; header top: 30dp; header baseline and vertical alignment to be checked on device. Brand/title separator: 1dp stroke, 26dp height, 18dp horizontal gaps.
- Grid available width: 904dp (960 - 2x28). Four columns with 10dp horizontal gaps yield **218.5dp cards**. Do not hard-code fractional card widths: distribute available width with weighted columns.
- Grid top candidate: 118dp. Grid bottom candidate: 464dp. Two rows each **168dp** tall with **10dp** row gap. Center grid within safe bounds and adjust after visual inspection.
- Card inner padding: 22dp left/right; 18dp top/bottom. Contents vertically grouped: icon -> 18dp -> title -> 7dp -> description. Content may adjust to fit without clipping.
- Card icon: 38dp bounding box, consistent stroke ~2.5dp; use true vector drawables rather than Unicode glyphs.
- Card corner radius: 9dp. Unfocused border: 1dp neutral low-contrast. Focus border: 2dp electric blue; outer focus glow visually restrained, must not cause neighbouring card overlap or crop.
- Avoid scaling focused card beyond its layout bounds; any optional focus elevation/scale <=1.015 and must be tested for clipping.
- Header and grid must remain legible at 720p and 4K and when Android text scale changes; prefer constraint-based sizing and wrap rather than absolute screen pixels.

### Candidate colours (hex, review against existing Space Black Blend asset)
- Background base #030914 is a FALLBACK DESIGN REFERENCE ONLY, NOT an instruction to replace the official Supernova background asset; card surface #06111F with ~90–94% visual opacity over background; unfocused outline #28394C.
- Primary text #F4F7FC; secondary text #BCC9D9.
- Focus accent #00A8FF; selected card tint #08254F; focus glow uses accent at low opacity.
- Dividers #28394C, 1dp. These values are **candidates**, not confirmed sampled app palette; align with approved Supernova design assets before implementation.

### Category detail page geometry — DIRECTIONAL ONLY
- Same header and safe margins; content top below header, no permanent third panel.
- Left subsection rail target width ~240dp (25% of 960dp reference); gap 18dp; right settings area fills remaining width.
- Left rail item minimum height 52dp, vertical spacing 4–8dp, 14–18dp inset.
- Right settings row: min height 76dp with 18dp vertical padding and 20dp horizontal padding; title and description separated by 4–6dp; separator 1dp inset to text start. Rows with longer descriptions grow naturally.
- Toggle hit target at least 48x48dp, visually ~44x26dp, right aligned with 18dp clearance; distinguish On/Off and focus independent of colour.
- Scroll only settings content when required; preserve focused control on return; rail scroll independently if subsections exceed height.
- These detail dimensions will be revised during **each category's individual approval**.

### Remote focus, motion and accessibility
- D-pad left/right/up/down traversal must follow visual order; avoid focus traps and unexpected rail jumps.
- Focus border remains visible on dark and bright background patches; text contrast target WCAG AA-equivalent where feasible (4.5:1 normal text).
- Motion subtle, ~120–180ms focus transition candidate; avoid layout shifts, flashing, and background animation that distracts.
- Use meaningful accessibility labels for icons and toggle states; do not rely on colour alone.
- Test on Nvidia Shield TV Pro at 1080p and 4K output, with Android font/display scaling, long translations and Back/focus restoration.

### Visual specification governance
- Every approved mock-up should eventually have a repository asset, viewport, revision ID and token delta logged here.
- Do not infer exact dp/sp/hex values from image generation. The values above are deliberate **proposals**, subject to user visual approval and device verification.
- Do not change an approved token or card layout without recording the reason and obtaining approval for visually material changes.


## Approved selector-dialog pattern: preferred audio language — 2026-10-26
**Status:** USER-APPROVED VISUAL DESIGN for the compact modal demonstrated as “Default audio track”; exact preference semantics/options must be reconciled with the Settings audit. The user approved the latest narrow dialog with language/locale icons, not the surrounding unverified Playback controls.

### Placement and naming
- In the final eight-category organisation, preferred/default audio track language belongs under **Audio & Subtitles**, not Playback & Video. The screenshot's Playback placement is illustrative only.
- “Default audio track” is the approved **mock-up title**, subject to matching the real audited preference label and behaviour. Do not introduce a new preference merely to match concept copy.
- Source of truth for actual option values, persistence, default selection and dependency: audited audio-language/track preference and implementation. Example language names and selection in the generated image are illustrative, not verified as the full available options.

### Modal composition
- Opened by OK/Select on the relevant settings row, overlaying the existing category page rather than navigating to a third permanent panel.
- **Compact, vertically oriented modal**: noticeably narrower than the previous broad dialog; roughly **270dp wide** on the 960dp reference canvas (approximately 28% viewport), with adaptive maximum width for longer localised text and a minimum practical TV-readable width. Reference image appears ~455px wide on 1648px screenshot (~28%); image pixel estimates are not exact layout measurements.
- Visually centred in the available settings/content workspace as in approved mock-up, not necessarily the entire physical TV width; avoid covering the left subsection navigation more than necessary.
- Rounded dark translucent panel, subtle 1dp outline, modest shadow, ~10dp corner radius; retain official Supernova background visible behind it. Underlying screen remains visually recognisable but inactive; use restrained dimming as necessary for contrast.
- Modal interior target: 16dp horizontal padding, 14dp top padding, 12dp bottom padding; header 21sp semibold; explanatory line 15sp regular with natural two-line wrapping and ~5dp gap below title.
- Options form a **single-select radio list**; each row min 32–38dp tall depending on TV scaling, with a comfortable focus hit target at least 48dp high even if visible row is more compact. Radio indicator on left, optional locale/neutral icon next, language label. Subtle 1dp separators between rows.
- Selected value shows a filled radio circle; D-pad focused row shows blue outline/tint. These states are independent: moving focus must not commit a change or falsely mark a new selection.
- Cancel action sits at bottom right, separated from list by a subtle divider; Back cancels and restores focus to the originating setting row. Selecting an option saves through the **existing audited preference mechanism**, dismisses the modal and updates the displayed summary. Do not change persistence semantics without audit evidence.
- For many options, scroll the list inside a height-constrained modal while keeping title and footer stationary. No clipping, overlapping rows or text truncation at standard Shield resolutions.
- Exact row count, scrolling behaviour, choice order and default depend on the audited options. Never hardcode the screenshot's example set as the entire real list.

### Flag / locale icon rules — APPROVED
- Show a national flag **only when an option represents a specific country/locale**, e.g. English (UK) -> UK flag, English (US) -> US flag.
- For **generic languages** (e.g. generic French, German, Spanish, Japanese), use a neutral language/globe icon; a language alone is not equivalent to a country.
- For “Original language” use a neutral globe/language symbol. For “Auto (device language)” use a neutral automatic/device symbol. Include these special choices only if the audited preference genuinely supports them.
- Flags are visual supplements; always display the locale name as text, with accessible labels. Do not infer flags from display names without a locale code.
- No country flags for subtitles/audio choices that lack region-specific metadata. Preserve locale and language handling already documented in existing Settings design authority.

### Acceptance and verification
1. Compact modal width clearly smaller than previous wide mock-up; remains legible at typical TV viewing distance.
2. Title, explanation, radio choices, neutral/flag icons, selection, focus, separators and Cancel match the approved visual hierarchy.
3. Full D-pad traversal and scroll, selection and Back/Cancel focus restoration work on Shield.
4. Long language names and localisation fit or wrap; no clipping at 720p, 1080p and 4K.
5. Correct audited preference persists across navigation/relaunch; no invented option values or language flags.
6. Reuse official approved Supernova background and actual typography assets; screenshot's artificial space image and arbitrary time are NOT implementation assets.
7. Reuse this selector pattern where semantically suitable for other single-choice settings, but do not apply it blindly to multi-select or complex preferences.

### Reference / provenance
Approved in chat on 2026-10-26: user said “That’s perfect, update the blueprint with all details” referring to the **third, narrower Default audio track dialog mock-up with locale icons**. The image was generated in chat but is not yet a verified GitHub repository asset; attach the original as a design reference only when accessible. This text and audit are authoritative until then.


## Playback & Video — approved Playback subsection visual, 2026-10-27
**Approval scope:** The user requested tightening the left subsection navigation and reducing the empty surrounding panel; the resulting refined screenshot was accepted by proceeding to documentation. This approval applies to the **Playback subsection layout and three audited settings only**. The Video & Display and Resume & Progress pages, and the final mapping of all audited settings, are still pending.

### Structure and hierarchy
- Entry: approved eight-card Settings overview -> **Playback & Video** -> subsection **Playback**.
- Two content columns, **no third panel** and **no duplicate top-level category list**.
- Header: canonical Supernova wordmark, divider, Settings; clock only if provided by real app header. Preserve approved original rebranding background, not the image-generation backdrop.
- Left panel: category identity (play outline icon, **Playback & Video**, short explanatory description); below, exactly three **provisional subsection destinations** in order:
  1. **Playback** — play-circle icon; “Playback speed, external players and projector mode.”
  2. **Video & Display** — monitor icon; “Refresh-rate switching, Dolby Vision and display behaviour.”
  3. **Resume & Progress** — circular-arrow icon; “Resume behaviour and playback progress settings.”
- The **Playback** item has the blue focus/selection treatment in the reference. Distinguish active subsection from keyboard/remote focus in implementation. Subsection descriptions are shown on the left; selected row has a blue outline, subtle glow, dark blue tint and readable white text.
- Right panel: heading **Playback**, description “Control general playback behaviour and how content starts, plays and ends.” Then three settings rows, with setting label above a short explanatory line, and a toggle aligned to the far right.
- **Compact panel revision:** both columns are visually shorter than the previous full-height bordered mock-up. Avoid an oversized empty outlined rectangle beneath the three controls. The reference still shows a small amount of natural empty area below rows; do not add filler controls to eliminate it.
- **Tighter subsection navigation:** use compact, consistent vertical spacing and subdued separators between unselected subsection rows. Preserve readability at TV distance. No extraneous categories such as Performance/Other without audited justification.

### Playback subsection — verified source-backed controls
| Order | UI label | Supporting text in approved mock-up | Control | Audited default | Audit key |
|---|---|---|---|---|---|
| 1 | Enable playback speed | Allow changing the playback speed during playback. | Toggle | ON | `playback_speed` (S011) |
| 2 | Allow using another video player | Open content with an alternative video player when available. | Toggle | OFF | `allow_3rd_party_player` (S025) |
| 3 | Enable projector mode | Optimise playback for projector setups. | Toggle | OFF | `player_projector_mode_key` (S027) |

**Behavioural caveats from audit:** Playback speed is experimental and may cause choppy playback; the external player option invokes Android's compatible player choice and can affect resume/bookmarks; projector mode specifically positions video at the **top of the screen** for projector use. The approved screenshot's abbreviated help copy must not conceal these material consequences: prefer precise help text, or accessible additional detail, during implementation. The screenshot shows **audited default states**, not necessarily the current values on a user's device; always read persisted preferences.

### Visual measurements — calibration targets, not screenshot-derived guarantees
- Reference 16:9 screen. Preserve common outer TV safe margins and header from grid specification.
- Left column candidate width ~27–29% of usable content width, right ~71–73%, with 10–14dp gap. In the refined screenshot the left rail is roughly 445px of 1540px total content (~29%).
- Compact content panel: height determined by left subsection content / right setting rows, not full-screen forced height. Avoid truncation if descriptions or font scale increase.
- Left category header: 20–24dp inset, ~38dp icon, 22sp semibold title, 15–16sp description, 21sp line height.
- Left subsection rows: ~70–82dp target height when a two-line description is present; 16–20dp horizontal inset; 12–16dp vertical separation; icon 26–30dp; subsection label 18–19sp medium; description 14–15sp with 20sp line height.
- Right header: ~20dp inset; heading 24sp semibold; explanation 15–16sp, 21sp line height; 12–16dp separation before first row.
- Right setting rows: ~55–65dp minimum visual height on the 960x540dp reference, expanding as necessary; 14–18dp horizontal padding; title 18–19sp semibold; description 14–15sp regular with ~4dp title-description gap. Thin low-contrast row separators or borders, ~1dp, 8–10dp corner radius. Do not turn every row into a large card.
- Toggle: ~44x26dp visible, minimum 48dp focus/touch target, right aligned with at least 12dp inset; blue for ON and muted grey for OFF. Remote focus state must be discernible from toggle value.
- Colours, actual typefaces, backdrop, blue focus and icon set inherit the grid's canonical tokens/assets. Numeric dimensions are targets for Shield visual QA, not proof of pixel-perfect screenshot measurement.

### Navigation, state and accessibility
- Entering Playback & Video from the grid opens Playback subsection initially, unless restoring an explicitly retained subsection state. Back returns to the same focused grid card.
- D-pad up/down traverses subsection items and setting rows predictably; left/right transitions between subsection rail and settings content; OK on a toggle changes and persists only that setting; focus must not automatically toggle.
- Subsection change updates only the right content, with no unintended category changes or focus loss. Focus remains visible, no zoom clipping, no flashing.
- Scroll content if longer than available screen; compact panel may grow or become scrollable for accessibility text scale.
- TV QA: verify 1080p/4K, font scaling, long text, state persistence, category return, toggle focus, actual approved background and exact audited semantics.

### Reference and outstanding work
- Latest approved-in-context refined mock-up: image generation ID `c9071da6-b5c0-494b-a585-617cc21b83b3`, 2026-10-27, shows the compact left navigation and three real Playback controls. This is a chat image reference **not yet committed as a repository image asset**; do not claim Codex can retrieve it from GitHub. Text specification above is authoritative until an accessible asset is attached.
- Do **not** copy the generated backdrop or mock clock; use the original approved Supernova rebranding asset.
- **Video & Display**: design next, using verified list-valued refresh-rate switch (S018), Dolby Vision mode (S019), 3D switch (S022) and any additional mapped settings from audit. Do not depict refresh-rate mode as a Boolean toggle.
- **Resume & Progress**: inventory must be reviewed for actual controls and ownership; some are under Library & Metadata. Do not invent default playback mode, auto trailers, end-of-content behaviour, or resume thresholds.
- Reconcile all remaining audited playback/video controls before final category approval; a three-row mock-up is not proof of full feature coverage.


## Global application of approved Settings design patterns — 2026-10-27
**Decision:** Reuse the approved design system across **all eight top-level Settings categories**. This supersedes the earlier assumption that every category's basic layout must be redesigned individually. The main grid remains the approved entry point; each category uses a consistent two-column detail page, adapted to its actual audited controls.

### Global visual and interaction contract
- Use the canonical Supernova rebranding background and existing verified app font/icon assets, shared colour tokens, safe margins, header, TV scaling and D-pad focus treatment.
- **Left column:** category identity/title/short explanation, followed by category-specific subsections with consistent monochrome icons, concise explanations, compact vertical spacing and clear selected/focused states. Never repeat the eight top-level categories in this rail.
- **Right column:** active subsection title and short explanation, followed by real audited settings. Use the approved setting-row hierarchy (title, meaningful supporting description, right-aligned control, subtle dividers, consistent padding and row height).
- Toggle only genuinely Boolean preferences; show audited saved state rather than screenshot defaults. Use the approved compact radio-choice pop-up for appropriate single-choice preferences. Preserve separate interaction patterns for multi-select, account sign-in, editors, actions, dangerous operations, forms and nested flows. Do not fabricate choices or silently simplify functionality.
- No decorative third panel, preview, artificial filler rows, large empty framed areas, duplicate navigation or generated background substitutions. Allow natural blank space if a subsection contains few settings; keep panel dimensions content-responsive where feasible.
- Reuse approved typography, spacing, padding, corner radii, contrast, motion and focus specifications, subject to physical Shield calibration. Do not independently restyle each category.
- Navigation: OK opens selected category/subsection/control; Back returns to prior level and restores focus; D-pad movement is predictable, and changes persist via existing preference keys and listeners. Accessibility labels and long translations remain supported.
- Flag icons only for actual locale-specific choices; generic languages use neutral symbols, as specified in the approved language selector.
- **Audit-first mapping:** Every real control/action from the Settings audit must be assigned an intentional home, including dependencies, conditional visibility, advanced controls and any settings reached through other areas. Record exclusions/relocations explicitly; never omit settings because a mock-up has insufficient space.
- **No accidental scope expansion:** Existing functionality is preserved unless the user separately approves a change. The new design does not itself authorise additional capabilities or changing preference semantics.

### Category-by-category adaptation, without separate full visual reinvention
| Top-level category | Mapping/design focus |
|---|---|
| Playback & Video | Playback, Video & Display, and Resume & Progress are current proposed subsections; map all audited relevant controls before finalising. |
| Audio & Subtitles | Audio output/track preferences, subtitle options and approved compact language selector; advanced compatibility may belong under System & Maintenance depending on audit mapping. |
| Library & Home | Metadata, library preferences, watch history and Home display/row customisation; use appropriate existing editors for complex actions. |
| Streaming & Providers | Streaming availability, regions and provider selection; support multi-select provider lists rather than forcing radio pop-ups. |
| Appearance | Actual UI language, accent and other audited appearance controls; fewer subsections are acceptable. |
| Connections & Integrations | Connection preferences, Trakt, OpenSubtitles, IntroDB and other audited integrations; respect sign-in/authentication flows. |
| System & Maintenance | Diagnostics, backup/restore, advanced compatibility and maintenance; retain warnings and confirmations. |
| About Supernova | Version, release notes, licences, credits and technical information; actions/information may be read-only rather than toggles. |

### Approval boundaries and handover requirements
- **Approved for global reuse:** structural two-column design, styling, compact subsection navigation, setting-row presentation, toggles for Boolean controls, single-choice modal pattern, and TV focus behaviour.
- **Not automatically approved:** exact subsection inventories, labels, default states, mapping of every audit entry, category-specific flows, or unreviewed mock-up controls. The last Video & Display mock-up remains a **candidate** until explicit approval; its three audited controls are source-supported, but visual approval is not assumed.
- Codex implementation handover requires: (1) verified asset/font manifest, (2) complete audit-to-new-category mapping with keys and behaviour, (3) interaction-type matrix and any exceptional flows, (4) UI component/token specification, (5) screenshot/focus/functional QA plan. Keep release code untouched during documentation/design.
- Future user design changes should update this one blueprint, and all affected categories should inherit the revision unless explicitly excepted.

## Complete audited settings mapping — 2026-10-27
- Companion document: [SETTINGS_AUDIT_MAPPING.md](SETTINGS_AUDIT_MAPPING.md).
- **165/165** audit definition rows accounted for in a proposed new-category and subsection mapping, including **23 hidden/legacy/runtime review** rows and **26 nested backup export categories**. This is documentation only, not final sign-off or proof of runtime completeness.
- Proposed distribution: Playback & Video 11; Audio & Subtitles 10; Library & Home 24; Appearance 5; Streaming & Providers 8; Connections & Integrations 20; System & Maintenance 58; About Supernova 6; review/non-exposed 23.
- Before implementation, complete the open gates in the mapping document: exact option semantics, hidden/legacy disposition, Network & Files ownership, known listener/focus defects, nested workflows, canonical assets and full Shield QA coverage.
- The mapped categories/subsections are **provisional** until reviewed. The global design system and approved Playback screen remain approved independently of this mapping.

## Approval log
- APPROVED IN PRINCIPLE: eight names/order; ground-up approach; full-screen Space Black; no filler; category pages use left subsections + right controls; setting descriptions, toggles and separators.
- APPROVED 2026-10-26: main eight-card 4x2 Settings grid visual direction, monochrome icons, descriptions, restrained blue focus, spacing and structure. Mandatory background: the existing official Supernova rebranding-release background asset, NOT the generated planet/nebula concept art. The numeric dp/sp/hex specifications are documented implementation targets requiring asset checks and Shield visual validation; approval of the visual does not assert the numbers were pixel-measured.
- APPROVED 2026-10-26: compact single-choice audio-language dialog, radio selection, blue focus, Cancel and locale-aware flags/neutral icons as specified above.
- APPROVED VISUAL 2026-10-27: compact Playback & Video -> Playback subsection, three audited toggle rows and tighter subsection rail; precise scope and QA constraints recorded above.
- MAPPING IN PROGRESS 2026-10-27: all 165 audit definitions classified in companion mapping document; final placement, special flows, and implementation remain unapproved.
- NOT APPROVED: Video & Display, Resume & Progress, other full category pages, migration/implementation. Main grid IS approved.

## Change control / upkeep
Treat this file as the living design blueprint. After EACH explicit user design decision, update the approval log, layout/specification and any impacted category mapping in a new documentation-only commit. Clearly mark candidate vs approved; never silently promote mock-ups to approved. Keep this file consistent with the audited inventory, and link any approved image/mock-up artefacts when a stable repository asset exists. Avoid touching release/implementation branches while design is in progress.

## Codex handover readiness and scope — 2026-10-27
**User direction:** The user has finished supplying design preferences for now. Codex should independently resolve routine engineering choices, request user input only for genuine personal-preference decisions or changes to agreed functionality, and avoid unnecessary repeated design reviews. This is **authorisation to prepare/verify the handover, not authorisation to edit application code or start a release**.

### Authoritative handover inputs
1. This master blueprint (approved 4×2 main grid, two-column category layout, common visual/focus patterns, approved compact choice dialog and Playback subsection).
2. [165-entry audit mapping](SETTINGS_AUDIT_MAPPING.md), **provisional** placement, all source IDs covered, including nested backup options and hidden/legacy disposition queue.
3. Original audit files in `docs/audits/settings-2026-10-26/`, especially `inventory.json`, `INVENTORY.md`, `AUDIT.md`, `ISSUES.md` where available. The source audit, not an image mock-up, is authoritative for keys, options, defaults, gates, and handlers.
4. Existing approved Supernova rebranding background, app typography and branding assets (Codex must locate exact paths and verify; generated mock-up backgrounds are not assets).

### Codex's next documentation-only deliverables
- Validate all 165 mapping rows against source; produce a final disposition of hidden/legacy entries, nested controls, Network & Files-owned scheduling and dependencies. Do not turn hidden rows into visible controls without review.
- Confirm actual option lists, runtime defaults, persistence and preference listeners, including multi-choice vs radio vs toggle, account state and gating.
- Specify reusable UI components and exceptional screens for streaming providers, backup/restore privacy and selection, diagnostics, network credentials, integrations and About.
- Explicitly include known My Providers focus and Subtitles by Default listener issues in the regression matrix; check the audit issue list for others.
- Verify exact approved background/font/icon assets and annotate any design tokens that remain estimates pending Shield measurement.
- Produce an actionable implementation sequence and Shield QA matrix covering D-pad focus, Back restoration, scrolling, saved settings, conditional states, accessibility, failures, existing features and regression tests.
- Return only substantive unresolved preference/design choices to the user, with options and impact. Do not imply full final sign-off until these checks pass.

### Safety and completion boundaries
- **Current status:** blueprint and draft mapping are on GitHub and ready for Codex's *verification/specification* pass. Functional specification and implementation are **not yet completed**.
- **Do not** alter active release branches, merge into main, reset work, change signing/app identity, produce an APK, or implement the Settings UI as part of this documentation handover. Obtain explicit go-ahead for implementation.
- Record verified results and any decisions in documentation commits on this design branch, keeping this blueprint as the index. Preserve the exact existing functionality unless a change is separately approved.
