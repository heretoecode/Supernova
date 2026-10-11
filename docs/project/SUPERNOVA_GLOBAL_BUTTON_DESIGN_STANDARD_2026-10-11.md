# Supernova Global Button Design Standard — staged proposal

**Recorded:** 2026-10-11
**Status:** User-directed global design direction; detailed sizing values are proposed and require Shield calibration. Documentation only, no implementation authorisation.

## Scope and intent
Use one shared system for app-owned buttons across Home hero actions, Movies, TV Shows, custom library pages, dialogs, Settings, wizard, keyboard actions and other surfaces. Audit existing button curvature inconsistencies (e.g. More Info versus Movies/TV toolbar). Preserve functional intent and existing approved visual identity.

## User requirements
- Consistent **curvature/corner radius** across standard buttons, using one shared bounded responsive token, with clearly defined variants only when warranted.
- Every **labelled action button** should have a corresponding meaningful icon reflecting the action, visually aligned with its text. Reuse a consistent icon family and avoid arbitrary or misleading icons. Icon-only controls retain accessibility labels.
- Capitalise **every word** in visible button labels (Title Case), e.g. `More Info`, `Save Page`, `Continue Editing`; avoid forced all-uppercase labels. Preserve proper names/acronyms.
- Button backgrounds should be **dark navy and translucent**, dark enough for white text/icons to remain legible over bright artwork and other surfaces. Focused and disabled states need their own accessible contrast and affordances.
- Standardise padding, icon-to-label gap, inter-button gap, alignment and focus expansion clearance throughout app.
- Reuse global responsive design tokens; avoid hardcoded per-screen tweaks.

## Provisional sizing targets (not final)
- Standard corner radius **10–12dp**.
- Standard height **44–48dp** minimum usable TV focus target, subject to accessibility and layout.
- Horizontal padding **16–20dp**; vertical padding **10–12dp** adjusted to actual button height.
- Icon **18–20dp**; icon-label gap **8dp**.
- Inter-button gap **10–12dp**.
- Preserve approved Supernova font family; medium weight for labels.
- Distinguish compact toolbar, normal action, prominent hero/primary, destructive and keyboard variants without inconsistent visual language. Do not force equal width across all buttons.
- Use dark translucent navy surface, test over both very bright and very dark backgrounds; avoid opacity choices that obscure focus states.
- Preserve existing accent focus style, clear focused/unfocused differentiation, and enough space for D-pad focus scale/outline.
- Where necessary use a restrained text shadow for contrast, not as substitute for accessible backgrounds.

## Approved proportional size-family rule (user clarification)
- Use a **shared base button design with proportional size variants**, not independently styled button families.
- **Standard action buttons** (e.g. Home hero More Info) remain comparatively compact.
- **Functional library toolbar buttons** (Filter, Sort, Ascending, List View, etc.) are intentionally larger and more substantial; use the **same functional variant** on built-in Movies, TV Shows and custom library pages.
- As the overall button box increases, scale its **height, horizontal/vertical padding, icon dimensions, icon-to-label gap, text size, corner radius and focus expansion clearance together according to shared ratios**, so the complete component feels like the same design at a different size. Preserve consistent alignment and shape.
- These are **bounded proportional tokens**, not unconstrained multiplication: enforce legibility, fontScale/accessibility, TV safe areas, reasonable icon sizing, minimum focus target and label fit. Allow width to be content-driven; do not demand equal widths or distort text.
- Provisional dp values in this document describe a base size only; the final scale ratios and variant sizes must be calibrated on real Shield screenshots. Avoid treating the example 20% increase as an approved exact scale factor.

## Interaction and QA
- Audit every app-owned button and shared component to avoid missed screens.
- Check icon meaning, label casing, text truncation, padding, spacing, corners, touch/focus targets, D-pad order, disabled and destructive semantics.
- Test with real Shield screenshots, 720p/1080p/4K output modes and font scaling; calibrate provisional tokens against the accepted design.
- Preserve global keyboard six-button arrangement and small control sizing.
- Coordinate with [Global Responsive Layout Standard](SUPERNOVA_GLOBAL_RESPONSIVE_LAYOUT_STANDARD_2026-10-11.md), [Global Dialog Design Standard](SUPERNOVA_GLOBAL_DIALOG_DESIGN_STANDARD_2026-10-11.md), and [Global Keyboard Standard](SUPERNOVA_GLOBAL_ON_SCREEN_KEYBOARD_STANDARD_2026-10-11.md).

**Approval gate:** User has agreed the direction and wants padding/spacing discussed; values above remain suggested, not final pixel-perfect sign-off. Do not implement until the next-version handover is explicitly authorised.
