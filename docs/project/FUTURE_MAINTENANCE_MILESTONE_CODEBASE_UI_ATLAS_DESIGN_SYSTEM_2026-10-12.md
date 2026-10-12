# Supernova — proposed dedicated maintenance milestone: codebase health, UI atlas and semantic design-system consolidation
Date: 2026-10-12
Status: **Discussion preserved / future milestone proposed. NOT authorised to implement during active 0.138 Codex work.** Release number and timing undecided. User wants a deliberate pause in feature-development cycles to audit a stable *latest* release and then decide safe cleanup.

## Core intent and boundaries
This is **not merely APK compression** and not a visual bug-fix sprint. Freeze a stable release as baseline, inventory the *whole app* as built, map UI and code to their underlying semantic purposes, classify inherited NOVA functionality, rationalise shared components, safely delete genuinely unused code/resources, optimise where measurable, and deliver a clean, maintainable, smaller app. Do not inspect the currently installed 0.137 APK for fresh change requests while Codex is implementing 0.138. Do not change the active coding branch or add this work to the current release.

## Desired outcomes
1. **Whole-codebase audit**: modules, dependency graph, source code, Java/Kotlin, build variants, shrinker rules, obsolete flags, resources/assets, native libraries, legacy NOVA UI and services; inventory reachable and unreachable paths; compare size against like-for-like NOVA release builds, recording compressed APK, installed footprint, DEX, resources, native libraries and dependency contributions. Do not infer unused status from visibility alone. Check reflection, dynamic loading, migrations, background jobs, feature flags, persistence and playback before removal.
2. **Full UI Atlas**: versioned, screen-by-screen inventory of *actual* visible content including left/centre/right panels, exact strings, relative order, separators, typography, focusability, dimensions/bounds, scroll and conditional states; cover Home, Libraries, Details, Search, Settings, Network & Files, onboarding, dialogs, keyboards, playback HUD, editor workflows and error/loading states. Use actual device/emulator screenshots and runtime hierarchy/focus dumps when feasible; distinguish from code-generated Android screenshots/previews and conceptual mockups. User previously referred to 'application images' Codex can produce: **identify the actual feasible screenshot/render mechanism before promising full coverage**.
3. **Complete interaction/navigation graph**: every actionable control, destination, next screens, back/cancel flow, focus restoration, enabled/disabled conditions, persistent setting, callback/service and dependencies. E.g. Network & Files > Network Shares > Add Network Source > subsequent branches, every action traced to actual implementation.
4. **Semantic UI classification**: audit *what each element is*, not just what it looks like. For every label, heading, divider, body text, button, keyboard, navigation item, status element: actual XML/View/Compose class or helper, string ID, style/theme, semantic role (if present), shared consumers, source path, behaviour, visibility, and impact radius of style changes. Distinguish present semantics from proposed semantics; do not falsely claim existing semantic types.
5. **Reusable global design architecture**: propose and, only after approval, migrate toward a hierarchy of global tokens (type, colour, spacing, focus, animation, navigation), shared semantic components, shared page templates, and page-specific content. Central change to a SectionHeader should change all intended SectionHeaders and **not** PanelHeaders. Global changes require a known, documented impact scope and explicit exceptions.
6. **Functional relevance review**: every visible, hidden, conditional, unreachable or removed-from-UI feature traced bidirectionally: UI -> implementation and implementation -> UI. Classify **keep visible / keep internal / expose / consolidate / redesign / deprecate and remove** with rationale and safety impact. Hidden does *not* imply dead. Identify obsolete NOVA Collections options, old 'try new UI' toggle, and other examples *if present*; don't assume their current existence.
7. **Controlled cleanup/optimisation**: remove verified dead code, obsolete layouts, unused images/strings/assets, duplicated widgets, unnecessary dependencies, obsolete flags, inappropriate broad keep rules; retain licensing/attributions and critical playback/scanning/network/backup pathways. Batch small reversible changes with build, UI and device regression tests. Track size, cold/warm start, frame/jank, navigation latency, memory, GC, leak signals, crash/ANR rates and scanning/playback behaviour. Code deletion may improve size and maintainability but is **not proof of runtime speed gains**.

## Illustrative structural question that motivated the audit
In Network & Files > Overview the user described right panel text 'Information' then 'Overview' then description then Scan Library, Network Scanning and Library Health actions. Both headings *look* identical (font, alignment, underline). Determine **from actual source** whether they are both generic headings, panel header vs section header, or differently defined widgets styled alike. Record source file/class, string resource, style, semantic role, consumers and downstream change impact. Centre panel has descriptive text and a seemingly unrelated focusable element; inventory exactly what it is, why focusable, and its action, without prejudging it as dead. This example is a user observation, **not verified current-source analysis**.

## Proposed architectural foundations (names illustrative, NOT verified classes)
- **Global tokens:** typography scale, standard Supernova font, colours, translucency, spacing, padding, focus outlines/underlines, animations, D-pad navigation principles, screen-density/accessibility behaviour.
- **Semantic roles:** PanelHeader, SectionHeader, BodyText, SupportingText, StatusText, NavigationItem, PrimaryAction, SecondaryAction, ActionButton, and appropriate specialised variants. Give stable identifiers and document usage. Meaning/role separate from visual treatment.
- **Shared Library page foundation:** Movies, TV Shows, user-created custom Library pages share poster grid, stats/title alignment, toolbars, spacing, focus/zoom and animations; each has contextual content/actions. Avoid separate lookalike implementations.
- **Shared three-panel workspace:** Settings and Network & Files share panel geometry, surfaces, hierarchy, separators, typography and appropriate focus patterns while retaining task-specific navigation and operations.
- **One universal keyboard:** one reusable Supernova keyboard appearance/interaction system across Search, editors, network addresses, credentials etc; per-context configuration (e.g. password masking/character layout) without duplicate designs.
- **Global change impact map:** enumerate all screens consuming a component/token and all intentional overrides; distinguish already migrated from legacy exceptions. Do not imply that changing one style magically changes code not using it.

## Required milestone deliverables
- Exact frozen baseline: source SHA, signed APK/build type, target hardware/Android, measurements, test and privacy conditions.
- App-wide **Screen-by-Screen UI Atlas** with images, text inventory, layout tree, focusability, visible/conditional states and code links.
- **Interaction/navigation graph** including per-control click/back/focus routes and persistent state.
- **Semantic component catalogue**, style/token dictionary, current-vs-target role mapping and global impact matrix.
- Full source/resource/dependency and NOVA legacy inventory; **candidate removal register** with evidence, risk, decision and rollback path.
- Settings and Network & Files **every-option trace**; assess hidden vs visible functionality, obsolete options and whether intentionally internal.
- Side-by-side before/after APK and performance evidence; regression matrix and release sign-off report.
- **Claude-ready design context package**: atlas, component catalogue, navigation maps, behaviour contracts, token specs, approved constraints and precise unanswered design questions. No private Shield photos/videos or diagnostic data sent without explicit permission.

## Milestone execution stages
A. Freeze stable latest release and reproducible builds; record metrics and performance/diagnostics baseline.
B. **Read-only** audit: static source + runtime capture + dependency/asset + UI role and navigation inventory; mark unknowns.
C. Human review and decisions: approve each semantic hierarchy, shared template, retained/removed feature and risky cleanup.
D. Incremental implementation on isolated future maintenance branch, tests and rollback at each step.
E. Device QA, size/performance comparison, atlas refresh, conformance and approval before merge/release.

## Claude collaboration — future, not yet requested to run
Prepare this document plus actual verified repository inventories, screenshots and unresolved questions as a briefing. Ask Claude to critique proposed component taxonomy, shared page templates, style token granularity, visual hierarchy, migration sequencing and risks; require it to distinguish verified source facts from suggestions. Claude input is advisory, not implementation authorisation. User explicitly wants Claude involved *at a later point*; do not imply consultation has occurred.

## Guardrails and unresolved decisions
- No feature expansion or speculative redesign in the maintenance milestone without explicit approval.
- Do not equate hidden code with unused code; protect data migrations, file playback, subtitles, networking, accessibility and backup compatibility.
- Select milestone timing/release number only after current Codex run and suitable stable baseline.
- Determine best actual UI image-generation/capture workflow based on current Android stack.
- Determine final taxonomy, component boundaries, migration sequence and exact removal list after evidence.
- Baseline performance telemetry should be improved **in a separate intervening release**, after privacy and feasibility audit; see companion diagnostics proposal.
