# Next release — Edit Library complete audit and redesign (2026-10-30)

**Status:** Owner requests thorough functional/source audit followed by redesign from scratch; design not yet approved and implementation not started. No changes to delivered signed 0.147, draft PR #27, main or signing protections.

## User video evidence
Owner supplied 11.93-second physical Shield clip IMG_5071.mp4, reviewed in chat. It shows opening Edit Library and navigating the three-column editing interface. Observed/owner-reported:
- Multiple visually competing background treatments, including behind Edit Library content and separately behind global top navigation. Current layering feels incoherent.
- Live Preview panel focus appears stronger/visible at top than at bottom; focus boundary and scroll clipping should be investigated.
- Middle-panel actions are stacked vertically despite appearing better suited to a horizontal, consistently spaced action row.
- Dense text and visually inconsistent panel sizing/spacing. Video alone does not establish exact implementation root causes.
Original video is a chat attachment and is NOT claimed uploaded to GitHub.

## Phase A: Mandatory full audit BEFORE designing
- Trace every entry point to Edit Library (including custom pages), actual source layouts, view models/state, data persistence and save/cancel semantics, preview generation and asynchronous work.
- Inventory all editor controls, settings, field types, valid/invalid states, item/library selection, ordering/visibility, page names, filters, artwork, scan/source association and every actual capability (do not assume unsupported ones). Document where each control currently lives and its dependencies.
- Test initial state, dirty edits, Save, Cancel, Back, unsaved-change confirmation, screen return, errors, empty/custom libraries, long text, keyboard, large text, 720p/1080p/4K, D-pad focus and scroll, source disconnect, and data preservation. Distinguish source inspection from physical Shield results.
- Diagnose background layering/top navigation safe area, panel borders/focus clipping, scroll viewport, focus vs selection and stacked action placement; identify root causes rather than cosmetic masking.
- Produce a parity/acceptance matrix of every existing function and a written audit report in GitHub before changing code or proposing final mockups.

## Phase B: Owner reviewable fresh design
- Follow approved Supernova Space Black visual system, global shared top navigation, consistent single background layer and clear safe-area spacing. Do not invent a second header.
- Consider a clean three-region editor (library/section selector, editing workspace, Live Preview) only if audit shows this best fits real functions; layout is a PROPOSAL, not owner-approved final geometry.
- Live Preview should have complete and consistent visible focus treatment on all sides, correct clipping and stable content, with preview updates not stealing D-pad focus.
- Group related controls and use horizontal action rows for Save/Cancel or other paired actions when space allows, instead of unexplained vertical stacking. Keep controls aligned and scalable.
- Present a precise mapping from old controls to new UI, plus mockups and interaction flows for owner approval before implementation. No functionality removed or data format changed without explicit approval.

## Phase C: Implement after approval
Use a new dedicated next-release implementation branch based on verified current release ancestry. Regression tests and real Shield QA required. Preserve user libraries, custom page settings, scan/source links and all saved state. Do not treat mockups or software renders as physical verification.

Related next-release findings: NEXT_RELEASE_0_147_SHIELD_FINDINGS_AND_LIBRARY_BACKGROUND_2026-10-30.md; NEXT_RELEASE_ADDITIONAL_SHIELD_QA_2026-10-30.md.

## Clarification — reuse maintenance mapping first (2026-10-30)
Before any new inventory, Codex MUST search the repository's existing maintenance release mapping, feature registers, requirements and implementation notes for Edit Library/custom-page editing. Reuse and cite existing entries, then perform only a gap analysis for missing functionality and current Shield UI defects. The 0.147 FD-003 traceability is specifically Network & Files, not proof of a whole-app Edit Library map. Produce an audit/findings report and proposed information architecture; DO NOT implement a redesign before owner approval. Do not repeat completed mapping without identifying concrete gaps.

## Owner clarification (2026-10-30)
The custom Library Page feature is ALREADY IMPLEMENTED and works on the owner's Shield. This work is limited to auditing and redesigning Edit Library UI/options, background layers, Live Preview focus border and middle-panel button alignment, with full functional parity. Do not rebuild the custom library feature or describe it as pending implementation. Preserve its working data and behaviour.
