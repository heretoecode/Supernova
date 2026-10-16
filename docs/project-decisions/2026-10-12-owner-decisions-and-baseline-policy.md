# Supernova — owner decisions and maintenance documentation continuity (12 October 2026)

**Status:** Owner planning decisions; not implementation authorisation. Separate documentation-only branch. Do not merge or modify Codex's ongoing `docs/maintenance-investigation-2026-10` branch, 0.138 QA, 0.137 QA, or main.

## Current confirmed status
- Owner confirms signed Supernova **0.138 APK was delivered and installed successfully in place over 0.137** on NVIDIA Shield TV. Upgrade installation succeeded; preservation of libraries/settings/Home layout and all runtime behaviours remains **unverified** pending walkthrough.
- Owner confirms Codex has begun the comprehensive **read-only maintenance investigation**. Its results are pending. No maintenance implementation is authorised.
- Claude has received the current-state handover and is waiting for investigation findings.

## Agreed release sequence
1. Preserve 0.138 as the initial **source investigation snapshot**, with exact branch/SHA and evidence date recorded by Codex. The installed APK's exact build identity should be verified independently, not assumed from branch name.
2. Owner and ChatGPT perform a **short 0.138 walkthrough**, prioritising glaring issues and preserving existing-install data.
3. Next provisional **0.139** combines **expanded diagnostics (minimum required scope)** and owner-selected fixes **in one release**, not two. No implementation handover until owner approval and investigation review.
4. Owner conducts **basic 0.139 smoke test**, then authorises a separate, controlled maintenance implementation phase if satisfied.
5. Maintenance covers source/architecture, UI Atlas and focus/navigation, semantic design system, cleanup/optimisation, backup/migration safety and selective upstream NOVA fixes, performance, security and feature additions. No indiscriminate upstream UI merge.

## Permanent technical-documentation policy — owner-approved principle
The 0.138 investigation is the **historical baseline**. Keep that snapshot immutable/traceable; **do not overwrite its observations** with 0.139 claims. Future releases should **incrementally update a current master documentation set** based on the actual diff and exact source SHAs, rather than redo a full investigation by default.

For every future release handover:
1. Record prior/new branch, commit SHA, APK identity, date and evidence level.
2. Review changed files and behaviour against the technical documentation impact map.
3. Update affected architecture, UI Atlas, navigation/focus, diagnostic schemas, upstream change ledger, dependencies, issue register and test evidence only as warranted.
4. Label findings `VERIFIED_SOURCE`, `VERIFIED_DEVICE`, `INFERRED`, `PROPOSED`, `UNKNOWN`; distinguish fixes reported by Codex from owner-verified fixes.
5. Retain the 0.138 snapshot and dated change ledger; mark entries current, superseded, resolved or requiring revalidation.
6. If changes are extensive (e.g. maintenance refactor), do broader targeted revalidation and baseline performance tests. Full reinvestigation only for significant architectural changes or evidence gaps.

**Important:** This is the owner's requested future process. Codex's current investigation may or may not yet provide a usable change-impact map. Assess deliverables before deciding any follow-up documentation work. Do not interrupt its ongoing investigation.

## Expanded diagnostics — agreed status
A read-only 0.137 QA source audit and next-release diagnostic expansion plan already exist in Drive. Existing QA diagnostics reportedly sample heap/native memory, CPU, focus, incidents and process exits; no full runtime verification. Gaps include playback/seek/buffer timing, frame jank, repeatable startup and credible leak inference. Codex's current investigation includes diagnostics review, but **does not implement expansion**. 0.139 implementation remains subject to owner-approved scope. Privacy: never collect credentials, tokens, usernames/passwords, cookies or authentication secrets; media paths/titles only with purpose-based necessity and safe sanitisation; no automatic external upload.

## Historical owner design/issue decisions needing source and device reconciliation
- Existing Supernova UI and approved mockups, **not upstream NOVA UI**, govern layout and interaction.
- Movie/TV Details approved iconography, fonts consistent with Supernova top navigation, spacing and information architecture; exact final mockups need retrieval before declaring conformance.
- Home row clipping, focus zoom, row move/hide controls, focus restoration, flashing icons, HUD and context menus were historical owner concerns; **do not automatically classify as unresolved in 0.138**.
- Shared Settings and Network & Files three-panel semantics; reusable keyboard; Library page templates are maintenance architecture goals. Determine what is already implemented before proposing replacement.
- Backup/restore, data migration, existing-install upgrades, and remote/D-pad focus remain regression priorities.
- Historical parked ideas are not automatic approved 0.139 work; verify current release state and ask owner where needed.

## Supporting authoritative owner-discussion documents
- [Current project state and decisions](https://docs.google.com/document/d/1P_BJar8orSdB7SEw87oMruzZVDKOD7rZH8bfXETDYew/edit)
- [Historical decisions, UI intent and parked work](https://docs.google.com/document/d/1-Tfxldeq8Q2sbEjkcP9eOoGmoxedj3-Ig-K6Ry1dSXs/edit)
- [Diagnostics source audit](https://docs.google.com/document/d/1IBurFUBcr6xyd-I2PAFhDjfzWsjdUOTfXZZEs7GdFKc/edit)
- [Diagnostics expansion plan](https://docs.google.com/document/d/1MpoBK58LYZh0ZNaXpmzjZwfb2ZV_A0c2RUKCWz_fDGM/edit)
- [Maintenance design-system planning](https://docs.google.com/document/d/1tojDL4K04yHk85s65tycSG5G0Pg7YDfVZyXI9v8s1lw/edit)

**Evidence boundary:** This file preserves owner decisions and prior audit descriptions. It is not an independent validation of the 0.138 APK, the entire conversation archive, the final approved mockup collection or current source conformance.
