# Supernova — initial briefing for Claude: maintenance architecture and diagnostics
Date: 2026-10-12
Status: Independent discussion / consultation only. No implementation authority.

## Your role
Please act as a **constructive independent technical and product-design reference**. Do not automatically agree with this plan, and do not take an adversarial position for its own sake. Assess the reasoning on its merits, explain trade-offs, identify genuinely missing considerations, and offer alternatives where they would improve the outcome. Distinguish **verified facts**, **user-observed behaviour**, **proposed future architecture**, and **questions that require code inspection**. Rank suggestions by value, risk and effort. Ask targeted questions when needed. Do not invent current source structure or UI behaviour.

## Project context
Supernova is an Android TV media player fork of NOVA Video Player, primarily operated with a D-pad on NVIDIA Shield. The owner is designing and iterating the app with ChatGPT and Codex. GitHub: https://github.com/heretoecode/Supernova . The current **0.138 Codex implementation is already in progress** and must not be interrupted, modified, restarted, or expanded. The owner is planning:
1. An intervening **next-version diagnostics expansion** to improve measurement and capture a reliable performance baseline.
2. A later, separate, **dedicated maintenance milestone** (version and timing undecided) to map, consolidate and optimise the entire app.

These are planning documents, **not approval to change code**.

## The underlying problem
Design changes have sometimes been expressed as visual one-off adjustments: make this heading, button, focus state or page resemble another. The owner wants Supernova to be architected around **semantic UI roles and global design rules**: change a shared component or token **once** and every intended consumer changes, without accidental effects on unrelated roles. This includes shared behaviour, not just appearance.

### Concrete example: Network & Files > Overview
The owner reports that the right-hand panel displays, in order: **Information** heading, **Overview** heading, description, then actions **Scan Library**, **Network Scanning**, **Library Health** and further content. 'Information' and 'Overview' currently look identical (font size, positioning and underline). The key question is **what does the existing code say they are?** Are both generic text headings, or a panel header and a section subheader, or something else? The owner wants a future change to a 'PanelHeader' not to unintentionally change a 'SectionHeader'. This screen description is **user-observed, not source-verified**. The centre panel also contains text and an unexpected focusable item that warrants code/runtime tracing.

### Desired shared design foundations
- **Global tokens**: fonts/typography, colour, spacing, surfaces, separators, focus, animations, TV D-pad navigation.
- **Semantic shared components**: panel headers, section headers, descriptions, action buttons, navigation items, status text, keyboard, dialogs and variants, with explicit inheritance/overrides.
- **Library template**: Movies, TV Shows and Custom Libraries should derive common grid/poster sizing, title/stats placement, toolbar, focus and interaction principles from one shared foundation; retain domain-specific functions.
- **Three-panel workspace template**: Settings and Network & Files should share geometry, typography, panel hierarchy, separators, focus and appropriate behaviour, with different contents/actions.
- **One universal Supernova keyboard** across all text-input contexts, with configurable mode (e.g. password masking) rather than separate designs.
- **Change-impact map**: identify every component consumer and explicit exception; visually similar UI may currently be duplicated, so a global edit is only truly global after consolidation/migration.

Names like `PanelHeader`, `SectionHeader`, `ThreePanelWorkspace`, `SupernovaKeyboard` are **conceptual proposals**, not verified existing classes.

## Proposed dedicated maintenance milestone
A. **Freeze a stable reproducible baseline**: source SHA, build settings, APK/installed size, hardware/OS, scenario/test data, startup, navigation, playback, memory and stability measures; compare like-for-like NOVA builds where useful.
B. **Complete read-only source + UI audit**: modules, code, dependencies, resources, native libraries, flags, shrinker configuration, inherited NOVA UI/logic, hidden functionality, settings and their underlying actions. Trace **UI -> code** and **code -> UI**. Never assume a hidden UI path implies dead underlying functionality.
C. **Full Screen-by-Screen UI Atlas**: actual screenshots or reliably rendered previews, exact text/order/hierarchy and locations in each panel, style/component identity, focusable targets, actions, conditional states, errors/loading, scrolling and source references. Differentiate actual runtime capture from code-derived previews and conceptual mockups.
D. **Navigation and functional graph**: every option's destination, follow-on flows, Back/Cancel/focus restoration, state persistence and code dependencies.
E. **Semantic UI classification and design-system consolidation**: record what every element *is* in current code, map to target semantic roles, establish shared components/templates/tokens, and track migration and global change impacts.
F. **Safe legacy cleanup and optimisation**: review unused NOVA layouts, assets, duplicated code, obsolete toggles, unused dependencies and shrinker rules; approve removal candidates individually. Preserve playback, scanning, migrations, backup, licensing, networking and other essential paths. Small reversible commits and regression tests. Do not claim code removal automatically improves runtime performance.
G. **Deliverables**: code/dependency inventory, screen UI Atlas, navigation graph, semantic component catalogue, change-impact matrix, user-reviewed removal/retain decisions, before/after size and performance report, regression evidence, Claude-ready context pack.

## Diagnostics: expand in an intervening release
The owner says diagnostics are enabled by default. **Current source implementation and exact event inventory have not yet been verified.** The immediate next task is a **read-only current-state audit**: what is collected, when, whether stored/exported, retention, logging overhead, privacy risks, missing instrumentation. Then compare against desired coverage and agree a concrete next-release plan.

**Candidate coverage to assess**, not claims of existing support:
- Crashes, ANRs, error classes, recovery.
- Java/native heap, process memory, GC, low-memory events, sustained growth, potential leak signals; distinguish lightweight production metrics from intensive debug leak analysis.
- Cold/warm start, screen navigation latency, frame timing/jank, UI-thread stalls, focus navigation.
- Playback open/start/resume/seek/buffer/errors; library scan/index/database queries; network connections/retries/timeouts; artwork decode/cache; subtitle lookup/cache/rate limiting.
- CPU, resource pressure, threads, file descriptors, diagnostic system overhead/dropped events.
- Consistent before/after benchmarks (same device, OS, scenarios, build configuration, sample sizes, percentiles where meaningful).

### Non-negotiable diagnostic privacy requirement
**Never record usernames, passwords, credentials, authentication tokens, API keys, cookies, authentication headers, session secrets or equivalent**, including when embedded in a URL, SMB path, exception or stack trace. Sanitize at collection time, and test with seeded secret-bearing inputs. If a field cannot reliably be made secret-free, omit it.

**File paths, filenames, folder names and media titles are not categorically banned.** The owner is the sole current user and permits them **selectively where they add real diagnostic value**, e.g. a specific media file causing repeated playback failures. Do not collect such context just because it is available; prefer safe structured codes/counters for ordinary performance measurements. Paths can themselves embed usernames or credentials, so sanitise carefully. Keep data on-device by default, control retention/export, avoid third-party upload or transmitting private device reports without explicit permission. 'On by default' should not mean costly invasive heap dumps on every device.

## Requested initial feedback
Please offer your own independent judgement on:
1. Is this separation into diagnostics first, maintenance milestone later technically sound? What dependencies or pitfalls have we missed?
2. Are the proposed semantic roles, shared page templates and universal keyboard the right architectural boundaries for Android TV/D-pad UX? What should be shared globally versus configured per page? How should tokens, variants and exceptions be governed?
3. How would you reliably discover the **real semantic identity** of every existing UI element and construct a complete UI Atlas and navigation/focus graph? What can static source analysis establish, and what requires runtime validation? What deliverables are most useful to future designers and Codex?
4. What major classes of inherited NOVA code/asset cleanup, build optimisation, compatibility risk or regression test might be overlooked?
5. Which additional **privacy-safe and low-overhead** diagnostics are genuinely useful for measuring the maintenance milestone's impact? Which proposed metrics are impractical or noisy on Android TV? How should memory leak investigation work without retaining sensitive heap dumps by default?
6. What should the **current-state diagnostic coverage matrix** contain (fields/columns, data provenance, collection/storage/export distinctions, privacy, cost, acceptance tests)?
7. What is missing from the baseline measurement plan, and how would you avoid mistaking normal run-to-run variability for improvement?
8. What would you prioritise, defer, simplify or change, and why?

Please structure the response as **strengths worth preserving; gaps/risks; recommended refinements; practical first steps; and questions requiring evidence or owner decisions**. Be specific, balanced and candid. No code changes, no implementation, no assumption of access to a live APK or source unless independently verified.

## Existing authoritative planning documents
- Maintenance milestone: https://github.com/heretoecode/Supernova/blob/docs/nova-upstream-deep-audit-2026-10-11/docs/project/FUTURE_MAINTENANCE_MILESTONE_CODEBASE_UI_ATLAS_DESIGN_SYSTEM_2026-10-12.md
- Diagnostics: https://github.com/heretoecode/Supernova/blob/docs/nova-upstream-deep-audit-2026-10-11/docs/project/FUTURE_RELEASE_DIAGNOSTICS_PRIVACY_SAFE_PERFORMANCE_BASELINE_2026-10-12.md

The documents above are the owner's working proposals, not source verification or final implementation decisions.
