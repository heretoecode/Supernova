> Historical handover queue. The owner subsequently authorised Details, full Settings implementation and the next signed test APK. Current delivery and remaining physical acceptance: [0.143](../0.143/README.md). The earlier permission limits below are retained as history.

# Supernova — Codex work queue and handover (2026-10-27)

**Purpose:** A single, separately itemised work queue for Codex, linked to the approved Settings blueprint and the user-reported Details tab issues. **Documentation only**; creating this queue does not start coding, authorise a release, or modify active development branches.

## Priority 1 — Details tab: targeted fixes and QA
Source: user-provided photograph of *Marshals* Details tab and video of *Mortal Kombat II* Details opening. Full acceptance criteria: [DETAILS_TAB_QA_CHANGES_2026-10-27.md](DETAILS_TAB_QA_CHANGES_2026-10-27.md).

1. **Remove language globe:** In Details → Key Information → Language, remove the globe icon before the language value while retaining label/value and alignment.
2. **Cast row spacing:** For the visible 4×2 cast arrangement, increase vertical spacing between the two rows; preserve four-across layout and accessibility/focus.
3. **Crew row spacing:** Apply consistent additional vertical spacing to the two rows of four crew members.
4. **Reception reviews:** Investigate whether review text is already supported and its data sources; ensure Reception offers real review content where available, alongside ratings. Provide honest empty/error states; do not invent review providers or data. Escalate substantive provider/UX changes if necessary.
5. **Slow/flickering metadata and artwork:** Reproduce Details opening from Home; profile time to stable metadata and artwork, investigate disappearing/reappearing fields, delayed background, asynchronous rebinds, cache and stale updates. Preserve known data while loading, avoid visible layout jumps, maintain D-pad focus; verify on Shield across cached/uncached, offline/slow and multiple titles. Do not assume a root cause from the recording alone.
6. **Regression QA:** Check Details, Extras, Seasons & Episodes and More Like This; provide before/after captures and test results.

## Priority 2 — New Settings interface: final technical specification, then implementation only when separately authorised
Authoritative design: [SETTINGS_FROM_SCRATCH_BLUEPRINT.md](SETTINGS_FROM_SCRATCH_BLUEPRINT.md). Full inventory mapping: [SETTINGS_AUDIT_MAPPING.md](SETTINGS_AUDIT_MAPPING.md). Audit sources: `docs/audits/settings-2026-10-26/`.

1. **Verify mapping:** Check all 165 audited definition rows, real keys, defaults, handlers, dependencies, capability gates and current states. Explicitly disposition hidden/legacy rows and Network & Files-owned controls; do not surface hidden options automatically.
2. **Verify assets:** Locate exact existing official Supernova rebranding background, font, icon and palette assets. Reuse unchanged background; do not use generated mock-up background art.
3. **Finalise reusable components:** Approved 4×2 main grid; category pages with compact subsection rail left and controls right; D-pad/focus/back behaviour, typography, separators, dialogs, toggle and selector patterns. Do not invent controls to fill space.
4. **Exceptional flows:** Audit and specify streaming provider selection, backup/restore options and privacy warning, diagnostics, network identity/credential forms, integrations, language choices, and About actions. Preserve actual runtime choices and account/capability gating.
5. **Known defects:** Include My Providers focus regroup and Subtitles by Default preference-listener bypass in implementation and regression plan; verify source issues list.
6. **QA matrix:** Cover navigation, focus restoration, settings persistence, conditional choices, empty/error states, Shield display scales and existing functionality. Clearly distinguish source default from user's saved setting.
7. **Handover gate:** Deliver final verified technical specification, list only genuine user preference decisions, and obtain explicit authorisation before modifying app code or initiating Settings implementation.

## Deferred — GitHub/release process automation (not in current coding scope)
The user has explicitly **parked** discussion of eliminating recurring manual GitHub actions at end of Codex sessions. Do not implement workflow/permission/signing/release automation in this work queue. Revisit only when user requests it.

## Operating instructions
- Inspect repository and active work state before any future implementation. Preserve uncommitted work; no reset, discard, merge to main, new release, signing changes or APK production without appropriate instruction.
- Handle routine engineering decisions independently; ask user only for material preference, scope or approval decisions.
- Record confirmed versus unverified findings and tests; do not report a fix complete without evidence.
- Keep Settings design documentation separate from Details tab bug fixes. Do not assume priority order is permission to start either implementation.

## Completion definitions
- **Handover ready:** this work queue, linked source audit, mapping and blueprint are accessible to Codex on GitHub.
- **Settings specification complete:** source verified and exceptional flows/tests documented, with user approval questions explicitly identified.
- **Code complete:** implementation, automated checks and physical Shield QA finished with evidence; release process remains separately controlled.
