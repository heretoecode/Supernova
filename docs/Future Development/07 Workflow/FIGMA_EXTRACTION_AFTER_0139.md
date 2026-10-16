# Figma UI extraction — deferred until accepted Supernova 0.139

**Status:** Awaiting owner confirmation that 0.139 implementation is finished and the 0.139 APK has been downloaded, installed and accepted as the baseline. **Do not launch Codex extraction yet.** This is a design-data/export task, not app implementation or a release workstream.

## Owner decision and reminder trigger
When the owner tells ChatGPT that Codex has finished 0.139 and the APK is downloaded and installed, **remind the owner to send the saved Codex prompt below in a new, independent Codex task**. Check whether the owner has accepted the installed build as the baseline; do not assume mere installation equals acceptance. The reminder is triggered by the owner's future message, **not** a background notification or scheduled task. No automatic Codex task is authorised.

## Why wait?
The active 0.139 implementation may change component geometry, controls, styles and behaviour. Extraction from an in-progress commit would create stale Figma tokens. Use the **exact validated source SHA matching the installed/accepted 0.139 APK**; no merge to main is required. Preserve the existing source and historical audit provenance.

## Existing work to reuse first
- UI Atlas commit `7b6413a641139efe4d355727980f12f9729d1bbc`: source maps, UI packages, provenance and gaps.
- Details forensic audit commit `89166c38dfbfcc205701e6aee33b254ad98b7f6d`: geometry, fields, navigation, source ledgers.
- `docs/0.139/INTAKE.md`, `UI_CONFORMANCE.md`, `WORKSTREAMS.md`, `REPRODUCE.md` on the 0.139 branch; earlier Customise Home audit and playback investigations.
- These references establish prior research, **not** proof that complete Figma-ready tokens already exist. Verify underlying documents and source before reuse.

## Scope and essential requirements
Four areas only: **Playback HUD, Details tab, Search page, Edit Library five-step guide**. Produce accurate *current implementation* blueprints, resolved dp/sp/colour/font tokens, source paths and relevant XML/programmatic view code, normal/focused/selected/pressed/disabled states, full D-pad maps, responsive and conditional layouts, screenshots where genuinely available, and full UI option inventories including every nested menu, exact label, value, action, visibility condition and code reference. Details future passive tab and Playback HUD four-tab redesign must remain separate from the current implementation; the latter is under discussion. Never invent missing values or controls. Preserve distinctions between synthetic renders and actual Shield captures.

## Saved Codex prompt — send after 0.139 acceptance

> **SUPERNOVA — FIGMA DESIGN EXTRACTION & HANDOVER**
>
> Repository: `heretoecode/Supernova`.
>
> **Goal:** Produce a complete, source-verified Figma handover package for (1) Playback HUD, (2) Details tab, (3) Search page, and (4) Edit Library five-step guide. Start from the **exact final, validated Supernova 0.139 source commit matching the owner's installed and accepted APK**; record its SHA and build provenance. If that SHA cannot be established, investigate the release records and report the blocker; never silently use a moving branch head or guess.
>
> **Strictly read-only application investigation.** Do not modify `main`, the 0.139 implementation branch, other working trees, signing, app source, APKs or releases. Work in an isolated documentation/export workspace and branch. No app changes or new APK.
>
> **Reuse existing investigations before extracting anything new.** Locate and actually read the UI Atlas (commit `7b6413a641139efe4d355727980f12f9729d1bbc`), Details forensic audit (commit `89166c38dfbfcc205701e6aee33b254ad98b7f6d`), historical Customise Home/Edit Library audit, playback HUD and search investigations, `docs/0.139/INTAKE.md`, `UI_CONFORMANCE.md`, `WORKSTREAMS.md`, `REPRODUCE.md`, relevant tests and decision records. Compare each finding against the accepted 0.139 SHA. Produce an Existing Research Reuse Report distinguishing reused, changed, missing, outdated and unverified evidence. Only redo work where necessary.
>
> **Trace the complete rendering path** for each of the four areas, not just `res/layout`: XML, Java/Kotlin, custom views, shared components, drawables/selectors, themes/styles, aliases, resource qualifiers, runtime geometry, animations, visibility and focus. Supply original relevant XML/drawables and precise source paths and methods.
>
> **Fully resolve design tokens** to explicit values: colours including alpha, dp margins/padding/size/radii/borders/icons, sp text, font family/weight/line height/letter spacing, elevations/shadows, focus scale, animation timings, and layout rules. Resolve `@dimen`, `@color`, `@style`, `?attr` and inherited values recursively; document every qualifier/theme/runtime variant, formulas, density/font-scale assumptions and evidence source. Mark unresolvable values **UNVERIFIED**, never invent numbers. Describe practical Figma Auto Layout/constraints equivalents without claiming exact Android equivalence.
>
> **Exhaustive user-visible option inventory is mandatory:** For each screen, enumerate **every existing button, icon, setting, field, menu, submenu, selectable value, conditional control and state**, tracing nested menus to final choices. For each record exact displayed label, icon, location, defaults, possible values, action, visibility prerequisites, normal/focused/selected/pressed/disabled appearance, and source reference. Explicitly separate runtime-verified visible, conditionally visible, and source-defined but runtime-unverified options. For the five-step guide verify actual step names, all fields and validation. For Search distinguish system keyboard from app-owned keyboard. For Playback document audio, subtitle, More and playback controls without importing imaginary upstream options. Details is intended passive in future; do not invent focus targets.
>
> **Focus and layout:** Provide component hierarchies, fixed/dynamic bounds, margins, clipping/scrolling, breakpoints/viewport behaviour and directional Up/Down/Left/Right/OK/Back maps with focus restoration. Include original selectors or programmatic equivalents. Do not confuse focused with selected.
>
> **Visual validation:** Reuse legitimate existing captures and tests. If possible, non-destructively render/test actual bounds and focus at documented density, font scale and viewport. Label synthetic fixtures as synthetic; never fabricate Shield screenshots or runtime verification. Document gaps.
>
> **Separate current and future:** Primary package is actual accepted 0.139 UI. Approved Details future design and the **under-discussion** Playback HUD four-tab concept are separate comparison notes, not current tokens. No unapproved redesign.
>
> **Deliverables:** A folder named `Supernova Figma UI Extraction`, containing:
> - `00 - Start Here/`: README, source/version manifest, Existing Research Reuse Report, Figma iPad setup guide.
> - `01 - Playback HUD/`: layout blueprint, Design Tokens.json and .csv, components/states, D-pad map, **complete existing settings/options inventory**, relevant source XML/drawables and verified reference screenshots.
> - `02 - Details Tab/`: same core blueprint/tokens/components/source/captures plus current-vs-approved comparison and field inventory.
> - `03 - Search Page/`: same core deliverables plus complete options/results/keyboard inventory and D-pad map.
> - `04 - Edit Library Five-Step Guide/`: same core deliverables plus actual five-step component map, **all fields/options/values**, validation and D-pad map.
> - `05 - Shared Design System/`: global tokens, typography, colours, focus/interaction rules, reusable components, validation report.
> Avoid empty placeholders; clearly flag unavailable assets. Produce valid JSON/CSV with consistent names, units, variants, exact source provenance and Figma import/conversion instructions; don't claim native import compatibility without checking.
>
> **Delivery:** Find the existing owner's **Supernova folder in Google Drive** and create `Supernova Figma UI Extraction` inside it. Upload the complete folder hierarchy **and a downloadable ZIP**, verify file listings and download integrity, then return actual folder/ZIP links. Save technical records on a separate GitHub documentation/export branch without touching 0.139 or main. If Drive access is blocked, preserve the package, state the exact limitation and supply an accessible verified alternative; never fabricate links or claim delivery.
>
> **QA and completion:** Check all four screens, exhaustive option inventories, reused evidence, resolved resource values and units, source provenance, conditional states, valid JSON/CSV, screenshots' true provenance, ZIP integrity, Drive delivery and untouched app branches. Continue through actionable work without repeated permission requests; only stop for real limitations. Final report: source SHA, reused vs new findings, per-area components/tokens/options counts, verified vs uncertain data, tests, Google Drive links, GitHub branch and any blockers.
>
> **No app implementation, no APK, no release, no branch interference.**

## Notes
- The Figma package should enable faithful recreation of existing 0.139 screens *before* owner redesign.
- Existing audits are not automatically complete or current; resolve missing information against accepted 0.139 source.
- No Codex task has been started by this planning document.
