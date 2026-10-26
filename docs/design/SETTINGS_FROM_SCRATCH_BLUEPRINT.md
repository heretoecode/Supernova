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

## Main grid — current candidate, NOT YET APPROVED
- Full-screen 16:9 Nvidia Shield / Android TV screen, 4 columns x 2 rows, all eight cards visible without scrolling.
- Space Black Blend backdrop: restrained and dark, not a bright nebula or giant planet distracting from navigation.
- Supernova brand typography; consistent simple monochrome category icons; title plus concise functional description per card.
- Clear electric-blue focus border and modest glow; unfocused cards understated; TV-readable sizes and contrast.
- Balanced margins and vertical position; no wasted space, extra panels, duplicate top-level navigation or decorative filler.
- D-pad traversal predictable; Enter opens category; Back returns to grid and restores prior focused card.
- Category descriptions are provisional editorial copy until approved. No arbitrary on-screen clock time should be treated as a functional requirement.

## Category page design direction — preliminary, NOT YET APPROVED
- A selected grid card opens a dedicated category page; do not repeat all eight top-level categories in a sidebar.
- Left column contains only subsections of that category; right column shows settings.
- Individual settings use short descriptions, proper toggles where applicable, and subtle separators; blue remote focus.
- No permanent third information panel or decorative preview; fewer subsections where naturally appropriate.
- Exact subsection names, controls, defaults and states must be checked against the audit before mock-ups or implementation.

## Approval log
- APPROVED IN PRINCIPLE: eight names/order; ground-up approach; full-screen Space Black; no filler; category pages use left subsections + right controls; setting descriptions, toggles and separators.
- UNDER REVIEW: main grid final visual appearance, focus and spacing; latest generated mock-up had three wrong labels and overly prominent nebula/planet. Corrected visual to be presented for approval.
- NOT APPROVED: final main grid; individual category page details; migration/implementation.

## Change control / upkeep
Treat this file as the living design blueprint. After EACH explicit user design decision, update the approval log, layout/specification and any impacted category mapping in a new documentation-only commit. Clearly mark candidate vs approved; never silently promote mock-ups to approved. Keep this file consistent with the audited inventory, and link any approved image/mock-up artefacts when a stable repository asset exists. Avoid touching release/implementation branches while design is in progress.
