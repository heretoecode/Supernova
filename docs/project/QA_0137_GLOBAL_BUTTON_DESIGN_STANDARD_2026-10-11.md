# Supernova 0.138 planning — global button design standard

Status: agreed product/design requirement, pending Codex implementation and audit; no code changes authorised by this document.

## Immediate visual changes
- Home **Customize Home** button must receive the same translucent black/near-black background treatment as hero **More Info**; replace saturated blue styling.
- Preserve button actions, focus behaviour, typography and appropriate per-context size/placement.
- Do not change unrelated artwork, poster cards, switches, tabs, sliders or playback controls merely because they are interactive.

## App-wide standard and future-proofing
User requires a genuine shared button design system: future changes to button appearance should be made once in common styles/tokens/components and propagate to all applicable buttons throughout Supernova, rather than requiring separate requests per page. Audit existing implementations and migrate applicable buttons away from duplicated hardcoded styling, with explicit documented exceptions only when function/context warrants. Establish consistent background/opacity, corners, font/icon styling, padding, focus (white or user-selected accent), transitions, pressed/disabled states and contrast. Allow context-appropriate dimensions and specialised control types.

## Verification and acceptance
- Inspect Home (More Info and Customize Home), Movie/TV details, Network & Files, Settings, dialogs and other button-bearing screens.
- Confirm applicable buttons share tokens/components and that one global visual change propagates to them.
- Verify D-pad focus, activation, legibility, accessibility and disabled/pressed states.
- Record any exceptions with rationale; do not assert current code already follows global styling until audited.
- This is a design-system task, not a request for a blanket colour replacement across all UI elements.

Original user photos/videos are not to be shared with Codex/GitHub without approval.
