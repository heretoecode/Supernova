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
Visual order of the APPROVED grid is defined in the reproducibility contract below; the following numbered list is the eight-category register, not a binding screen placement.
Row 1:
1. Playback & Video — playback behaviour, video presentation and controls.
2. Audio & Subtitles — audio output, languages, subtitle behaviour.
3. Library & Home — library metadata, library display and Home rows.
4. Appearance — language, visual preferences and presentation.
Row 2:
5. Streaming & Providers — streaming services, availability and provider preferences.
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
| 1 | 4 | Appearance | Customise the look and feel of Supernova. | Outline display/monitor |
| 2 | 1 | Streaming & Providers | Set up and manage streaming providers and availability preferences. | Outline cloud/service |
| 2 | 2 | Connections & Integrations | Manage external connections, accounts and integrations. | Outline connected nodes |
| 2 | 3 | System & Maintenance | Diagnostics, backup and other system options. | Outline gear |
| 2 | 4 | About Supernova | Version information, credits and legal details. | Outline information circle |

**Note:** The corrected image places Streaming & Providers in row 1 column 4 and Appearance in row 2 column 1, whereas the earlier written category sequence placed Appearance fourth and Streaming fifth. **The corrected approved IMAGE'S spatial arrangement wins:** row 1 = Playback & Video, Audio & Subtitles, Library & Home, Streaming & Providers; row 2 = Appearance, Connections & Integrations, System & Maintenance, About Supernova. The table above is a content-to-icon list and MUST be read with this spatial override. Do not change labels or swap cards without approval.

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

## Approval log
- APPROVED IN PRINCIPLE: eight names/order; ground-up approach; full-screen Space Black; no filler; category pages use left subsections + right controls; setting descriptions, toggles and separators.
- APPROVED 2026-10-26: main eight-card 4x2 Settings grid visual direction, monochrome icons, descriptions, restrained blue focus, spacing and structure. Mandatory background: the existing official Supernova rebranding-release background asset, NOT the generated planet/nebula concept art. The numeric dp/sp/hex specifications are documented implementation targets requiring asset checks and Shield visual validation; approval of the visual does not assert the numbers were pixel-measured.
- NOT APPROVED: individual category page details; migration/implementation. Main grid IS approved.

## Change control / upkeep
Treat this file as the living design blueprint. After EACH explicit user design decision, update the approval log, layout/specification and any impacted category mapping in a new documentation-only commit. Clearly mark candidate vs approved; never silently promote mock-ups to approved. Keep this file consistent with the audited inventory, and link any approved image/mock-up artefacts when a stable repository asset exists. Avoid touching release/implementation branches while design is in progress.
