# FD-003 — Network & Files comprehensive functionality and redesign-readiness audit

**Requested:** 23 October 2026. **Status:** investigation brief only; no design approved and no implementation authorised.

## Objective
Before any redesign, produce a code-grounded, complete inventory of the existing **Network & Files** area, its screens, panels, flows, components, data model, behaviours, supported protocols, focus/navigation, and dependencies. Reconcile against the **latest delivered and installed Supernova 0.141** source; do not assume main or an earlier release is authoritative. Identify verified defects and UX inconsistencies separately from speculative suggestions. The owner will make subsequent visual/design decisions based on the report.

## Non-negotiable safeguards
- **READ ONLY.** Do not modify application source, resources, manifests, tests, release metadata, signing, CI, or existing branches; do not produce an APK or merge anything.
- Do not start a Network & Files redesign, implement fixes, or begin the separate maintenance programme.
- Work from the actual verified 0.141 source/commit (including draft PR #19 and subsequent release/delivery history); record the exact commit SHA, branch and relevant discrepancies. Preserve 0.141, main, and all existing branches.
- Save the final audit report to GitHub in a dedicated documentation-only branch or existing appropriate docs branch, with a stable path and link. Do not overwrite unrelated docs.
- Inspect existing requirements, approved mock-ups, design tokens and previous Network & Files decisions. Flag missing source assets honestly.

## Required audit outputs
1. **Screen and state atlas:** every entry point, screen, tab, column, panel, list, card, overlay, modal, keyboard and empty/loading/error/connected/disconnected state, with navigation paths and code references. Cover any three-panel arrangement and left navigation structure where present.
2. **Controls/action matrix:** for every visible and conditional control, record label, location, triggering input, focus destination, action, validation, persistence, error feedback, dependency and source file/symbol. Include context menus, long press and Back.
3. **Source and provider capability matrix:** local/USB storage, network discovery and shares (e.g. SMB/NFS where actually supported), WebDAV, cloud providers including Put.io where actually implemented, credentials/auth flows, add/edit/remove/connect/disconnect, browse, scan/import, reconnect, missing sources, credentials storage, permissions and file operations. **Do not infer support merely from UI labels or dependencies.**
4. **Focus/navigation graph:** D-pad routes, initial focus, focus containment, transitions across panels, scroll, overlays, text input, Back and return focus. Include the previously reported Put.io Connect focus escape and whether 0.141 source addresses it; do not claim successful real login without physical account QA.
5. **Data and side effects:** source entities/settings, credential handling (describe without exposing secrets), library mappings, scanner triggers, source removal implications, connectivity/retry/offline states, service/API dependencies and potential regressions.
6. **Visual/layout design audit:** screen hierarchy, current fonts, colour/focus tokens, spacing, opacity, iconography, reusable controls, responsive/TV safe-area constraints, component coupling and areas that can be safely restyled independently.
7. **Issue register:** source-backed bugs, missing or unreachable controls, inconsistent copy, duplicated settings, UX pain points, accessibility and performance concerns. Classify as confirmed source defect, runtime-verified, owner-reported, or unverified hypothesis; include severity, evidence, reproducible steps and dependencies.
8. **Redesign boundaries and options:** what must remain functionally unchanged, opportunities to simplify navigation, reusable components, known constraints and open owner **design preference** decisions. Recommendations are proposals, not approvals.
9. **QA plan:** concrete NVIDIA Shield test cases for each source type and navigation path, with expected results and evidence required. Distinguish automated/source inspection from on-device validation.
10. **Executive summary:** inventory counts where verifiable, architecture overview, high-priority blockers, confidence levels, missing evidence and suggested order of owner-led design review.

## Quality bar
Follow every relevant source reference and screen flow; avoid superficial summaries. Where code permits, run **non-mutating** tests/static analysis or reasoned validation, but do not write generated artifacts into tracked branches. Provide file paths, symbols and line ranges or commits supporting each claim. Explicitly identify anything unverified, blocked by credentials/hardware or inaccessible. No assumptions that all functionality is correctly wired.

## Deliverable
One detailed Markdown report committed to a documentation-only GitHub branch, plus a concise final message with branch, commit, report URL, major findings and outstanding Shield/owner verification. **Stop after the audit. Wait for owner approval before redesign or implementation.**
