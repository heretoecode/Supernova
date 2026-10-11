# Supernova — Global modal dialog design system (approved visual direction)

**Recorded:** 2026-10-11  
**Status:** User-approved visual hierarchy and first-launch welcome composition; numerical design tokens below are **proposed engineering baselines requiring on-device calibration**, not measured pixels from the concept art.  
**Scope:** Reusable TV information, welcome, empty-state and confirmation dialogs. No immediate app implementation authorised by this document; do not modify active Codex branch.

## Design reference and philosophy
The user explicitly approved the **first-launch Welcome to Supernova** dialog shown in the eight-panel first-run storyboard (panel 1). Reproduce its refined, vertically centred, compact but spacious hierarchy, not a generic Android AlertDialog. Use **the real Supernova application icon asset**, not the mock-up's generic star.

Use a shared dialog component and design tokens for consistent structure across the app. Avoid applying a centred brand/icon layout indiscriminately to destructive confirmations or high-density choice lists; variants may omit branding and use different button layout while preserving typography, colour, focus and spacing.

## Responsive scaling policy (approved principle; ratios provisional)

**The design must retain its relative composition at different usable TV viewport sizes.** Fixed dp/sp baselines below are reference values at the design viewport, **not universal fixed dimensions**. Determine usable safe-area dimensions in Android logical dp after system insets/overscan, not raw physical 1080p/4K pixel counts. A 1080p and 4K display with the same logical viewport should look the same proportionally; do not blindly double dimensions at 4K.

- Welcome/information panel: target approximately **35–40% of usable logical viewport width**, bounded by sensible min/max dp for legibility and no overflow; use the available width/height together when the display is unusually narrow or scaled.
- Panel height: **content-driven**, capped at approximately **80% of safe viewport height**; scroll body content only when unavoidable and keep action buttons reachable.
- Horizontal padding: approximately **7–8% of actual panel width**, clamped to comfortable dp minimum/maximum; vertical padding, icon, gaps, radii and button sizes derive from a coherent bounded scale so their relative hierarchy stays recognisable.
- Type sizes: preserve the **brand < title > body** hierarchy using a bounded responsive type scale, respect Android fontScale/accessibility preferences, and never reduce text below legible minimums just to fit a fixed composition. Do not force a single uniform percentage scale that causes tiny text or huge controls.
- Focus targets: enforce Android TV D-pad target minimums and safe spacing regardless of proportional calculations; prevent cropped glow and focus movement outside viewport.
- Dialog stays centred within **safe content bounds**. On unusually small logical viewports, prioritise readability, no clipping, and navigability over exact geometric ratios.
- Centralise sizing in shared responsive design tokens; no screen-specific hardcoded pixel multipliers. Validate screenshots at representative 720p, 1080p and 4K output modes, different display scaling/font settings and overscan. Test both **normalised geometry** (dialog width/viewport width, padding/dialog width, icon/dialog width, action widths) and absolute readability.

**Provisional ratios are calibration targets, not measured pixel-perfect approval.** Compare to the approved welcome design on actual Shield screenshots and adjust globally rather than patching individual screens.

## Baseline design tokens (dp/sp; tune against Shield 1080p and 4K screenshots)
- Width: **420dp reference only** at the baseline design viewport; use the responsive width policy above, with a 90%-of-safe-viewport hard ceiling. Never clip.
- Panel: deep navy opaque background, cool-blue 1dp low-contrast border, **16dp** corner radius, restrained shadow.
- Padding: **32dp** left/right, **28dp** top/bottom; use a vertical content stack.
- Supernova app icon: **56dp square**, centred; preserve actual icon aspect ratio.
- Brand kicker: **14sp medium**, uppercase SUPERNOVA, letter spacing ~0.06em, centred; **12dp** after icon.
- Main title: **24sp semibold**, centred, white; **12dp** after brand kicker.
- Supporting copy: **16sp regular**, line height ~1.4 (about 22–23sp), centred, muted off-white, constrained line length; **20dp** after title.
- Primary CTA: full available width, min height **48dp**, radius ~10dp, strong Supernova blue fill, icon optional; **10dp** to secondary CTA.
- Secondary CTA: full available width, min height **48dp**, dark slate-blue fill or understated outline; no destructive visual prominence.
- Inner rhythm: consistent 8dp spacing grid; approximately **20–24dp** between body and actions; align everything on the dialog's centre axis. Allow text wrapping and TV font scaling.
- Backdrop: subtle background blur where feasible, plus dim layer; never destroy or alter Home screen state. If blur unsupported, use graceful dark dim fallback.
- Focus: only one action receives focus at a time; clear existing Supernova blue focus treatment, no flashing, predictable Up/Down, Back/Escape dismiss only when allowed, focus returns to originating element. On first-run welcome, primary **Network & Files** should be initially focused. Ensure screen reader/accessibility names and contrast.
- Buttons use existing app-wide typography and colour tokens wherever those have been approved; do not introduce a competing font family.

## First-run welcome — approved composition
Top to bottom:
1. Actual Supernova app icon.
2. Small uppercase `SUPERNOVA` brand.
3. `Welcome to Supernova` heading.
4. Short explanatory text: `Add movies and TV shows from local storage, external drives or network shares. Supernova will only begin scanning once you've selected and confirmed your folders.`
5. Primary `Network & Files` action: opens existing full Network & Files interface **without starting any scan**.
6. Secondary `Explore Supernova`: dismisses the dialog and exposes the normal Home layout with dark-blue placeholders.

First launch only; persist dismissal, reset on clearing app data. Home must remain navigable behind/after dismissal. Do not automatically start scanning; only scan after the user explicitly confirms folders. Show actual media progressively as scans complete. Placeholders should preserve normal hero/poster layout and focus animations, but never launch playback or show invented media metadata. Placeholder actions may show a compact shared information dialog with a Network & Files shortcut. No fake Continue Watching row.

## Dialog variants
- **Welcome / empty library:** full icon + brand + heading + body + vertically stacked CTAs.
- **Information:** optional small information glyph, title/body, one or two actions; preserve spacing and typographic hierarchy.
- **Confirmation:** concise title/body, explicit confirm/cancel labels, safety-first focus and accessible action order.
- **Destructive:** separate caution styling, explicit action text and safe default; never inherit automatic primary-focus rule blindly.

## Implementation conformance
Build a reusable dialog foundation with centralised tokens rather than hardcoding per-screen variants. Validate screenshot layout at 1080p and 4K on NVIDIA Shield, D-pad navigation, focus return, accessibility text scaling, long translated copy and modal backdrop. Avoid claiming exact mock-up fidelity until device screenshot comparison is completed. The first-run welcome mock-up is a conceptual reference, not a pixel-measured source.

## Related planned work
The redesigned first-run Home uses dark-blue realistic placeholder hero cards and Recently Added row; replaces the cramped three-column onboarding browser on Home; users enter Network & Files for actual library setup. Existing shared Network & Files focus bugs should be fixed in its shared foundation, not patched solely for onboarding. User confirmed scan does not begin until folder confirmation in installed 0.136 QA. This file captures **design rules**, not approval to implement the whole first-run redesign in the active release.

Related: `docs/project/PARKED_OUTSTANDING_PRODUCT_DISCUSSIONS_2026-10-11.md`.
