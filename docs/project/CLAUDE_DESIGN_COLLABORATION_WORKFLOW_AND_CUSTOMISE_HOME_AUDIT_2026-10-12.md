# Claude design collaboration and Customise Home audit — 2026-10-12

**Status:** Workflow agreed; read-only Codex audit launched by user and reportedly in progress. Claude design work remains exploratory. **No implementation authorised.**

## Purpose and authority

This document records the user's October 12 discussions about adding Claude as an *optional specialist visual-design collaborator* to the existing Supernova process. It is a coordination record, **not an instruction to implement UI**, start 0.138, or alter the active 0.137 work.

- Existing process remains **User ↔ ChatGPT ↔ GitHub ↔ Codex ↔ Shield QA**. ChatGPT discusses requirements and QA with the user, maintains GitHub documentation, and supplies a Codex launch prompt only following user approval. Codex implements approved release scope, builds APK, and reports. User tests on NVIDIA Shield TV; ChatGPT reconciles results.
- **GitHub remains authoritative** for approved development requirements, source code, and implementation history.
- **Google Drive is an exchange/archive channel**, particularly for Claude on iOS (where user cannot access a GitHub integration). It does not supersede GitHub or independently approve changes.
- Claude is brought in when user wants fresh design perspectives or is stuck on an interface. Claude may propose UI/interaction changes, but those remain suggestions until explicitly approved.
- For redesigns of existing app areas, ChatGPT first gathers known context, then commissions a **read-only Codex implementation audit** as needed; ChatGPT reviews findings; Claude designs with the user against verified behaviour and technical constraints; Claude exports final approved design materials to Drive; ChatGPT reviews and integrates approved requirements into GitHub for a *separately authorised* release.
- Codex's technical findings should distinguish source-verified facts, intended behaviour, existing bugs, unknowns and conjecture. Design output should distinguish approved details, pending suggestions and placeholders.
- No automatic coding, merge, branch clean-up, or release launch from a design audit.

## Google Drive archive layout (verified folders)

Root: [Supernova](https://drive.google.com/drive/folders/1c3G--zRc7aAt_SF6KQVniyD8xv6usMmZ)

- [APKs](https://drive.google.com/drive/folders/1mfHrFgLdJJSJYtTPys1N4Y-_KuE_84mM) — **existing** Codex delivery path, preserve Shield/Solid Explorer access. Do not unilaterally change APK delivery to version subfolders.
- [Claude Handovers](https://drive.google.com/drive/folders/1rZ1wFsJy53N0VtOisjhUs8AWnYZefaf-) — design projects and transfers.
  - [Customise Home Menu Redesign](https://drive.google.com/drive/folders/179cyN4Ikwh3SzXWbeb-bpSouDDISgRqt)
- [Codex Handovers](https://drive.google.com/drive/folders/1ggs04VihpMfCxYXHqEISXbGM_32x19tC) — optional handover archive; does not replace GitHub.
- [Codex Reports](https://drive.google.com/drive/folders/1HrdpgwwQXXca9QeLswDV-RPWHhi3W_H0)
  - [Customise Home Menu Redesign](https://drive.google.com/drive/folders/1AJJ4_S5FS0u87Hk8mh6SathtWiGHMLKN) — destination for the pending audit.

**Archival policy:** Each distinct design, handover, or release should have a dedicated descriptively named subfolder within the relevant category. Preserve completed handovers as historical snapshots; use named revisions rather than silently overwriting. Where feasible, include date, scope, status, related GitHub branch/SHA, links to corresponding report and APK, and approval state. Version-specific release folders should be created when needed, not speculatively. Folder existence is not evidence that its deliverables exist.

## Current Customise Home design exploration with Claude

User has iteratively reviewed mock-ups of Customise Home and Edit Genre Rule in Claude, with original photos of the top and bottom of the current scrollable menu and a detailed explanation of current functionality. The shared Claude conversation was provided to ChatGPT as a PDF plus continuation screenshots; those **user-supplied images are not authorised for redistribution to GitHub or Codex**. The observations below are a design-history summary, **not final approved implementation specifications**.

- Claude explored a two-pane Customise Home layout (rows on left, selected-row actions on right), eliminating a long monolithic scroll where feasible.
- User requested **simple outline-only focus** and removal of Claude's invented poster-preview strip.
- Genre Rule evolved from a two-pane genre grid to a centred settings panel with a **Genre multi-select dropdown**.
- User confirmed **changes apply immediately**; no Save/Cancel/Done requirement for changes. Preserve actual existing semantics as confirmed by code audit.
- User described existing Dynamic Genre Rule toggle, separate Movies and TV Shows toggles, Genre selection, Sort By, sort direction and Maximum Items.
- Claude initially invented Match Any/All genres and a Movies/TV/Both selector; these were subsequently removed after user clarification.
- User preferred integrating ascending/descending into Sort By (select same choice again to reverse; direction icon on left), and a **spacious vertical** Maximum Items selector.
- User requested app-consistent **monochrome line icons on the left** of text and better spacing/alignment.
- The “37 titles match” count, explanatory subtitles/hints, example genres/data and blur treatment were identified by Claude as **illustrative/suggested**, not verified functionality or automatically mandatory.
- Unfinished states identified in discussion: Rename, Create New Row, delete confirmation, Dynamic Genre Rule OFF, long names, many rows. Further audit may reveal others.
- Mock-ups' 1080p pixel coordinates must not be copied uncritically into Android TV dp/sp; check density, focus and D-pad mapping, responsive layouts and overscan.
- Respect existing Supernova selectable accent colour, shared button system, focus and UI standards. Proposed design behaviour changes require explicit approval; do not silently alter persistence/data model/genre logic.
- Final design package should combine approved PNGs and relevant source/reference, numeric layout/tokens mapped to actual app, focus map for every D-pad direction/OK/Back and restoration, states, preservation boundaries, regression checklist, and clear priority/approval/placeholder labels. Written spec is contract; visual references support conformance checking. Resolve conflicts explicitly.

## Current Codex audit — launched, not yet completed

Claude created the source audit request:
[01_codex_request_customise_home_audit.md](https://drive.google.com/file/d/1G4w2THvbKx_T4rqtNS-PQFbENzg2NMaH/view), under the Customise Home Claude Handovers folder. ChatGPT fetched and reviewed the actual Markdown request.

It asks Codex to inspect, **without editing**:
1. Implementation files, classes, XML/Leanback/Compose/custom views, data model/persistence.
2. Exact built-in row behaviours and create/rename/delete/reorder/show/hide flows; genre-rule options, defaults, immediate application, Back and edge cases.
3. D-pad focus graph, OK/Back, long press, focus restoration.
4. Theme/accent, fonts, focus outlines, icons, shared buttons/dialogs/toggles/dropdowns, blur/performance.
5. 720p/1080p/4K scaling, dp/sp, safe areas, min SDK.
6. Data-model/persistence constraints, strings/localisation, accessibility.
7. Emulator/build and UI-test support, known bugs.
8. Existing screen/state inventory and real screenshots if obtainable.

The user stated **Codex is working on the audit now**. This chat did not directly launch Codex. The prompt explicitly forbids code changes, commits, merges, resets, cleanup, release work or interference with current 0.137 branch/PR. Audit should report inspected branch and exact commit SHA, cite source paths/methods and real values, mark unverified findings UNKNOWN, and save report/screenshots to [Codex Reports / Customise Home Menu Redesign](https://drive.google.com/drive/folders/1AJJ4_S5FS0u87Hk8mh6SathtWiGHMLKN). **No completion report has yet been verified.**

## Claude continuation briefing

ChatGPT prepared a full copyable continuation prompt for the user's existing Claude chat, covering:
- Unchanged ChatGPT/GitHub/Codex development workflow and Claude's optional design-only role.
- Google Drive folder links and immutable handover/revision practice.
- Existing Customise Home exploration and Claude's previously submitted audit request.
- Codex audit *in progress*, not completed.
- Wait for reviewed findings before modifying designs; identify missing functionality, focus/Back, scaling, placeholders, technical conflicts and unfinished states.
- Do not restart the design, generate a premature final spec, implement code or contact Codex directly.
- Final approved Claude package goes to Drive, then ChatGPT reviews and records approved requirements in GitHub.

The user intends to save/paste this briefing to Claude when usage becomes available again. **Do not assert Claude has received it unless the user confirms.**

## Next steps / gates

1. Wait for the user to report Codex audit complete.
2. ChatGPT retrieves the report from Drive and checks completeness, code citations, inspected SHA, limitations and gaps. Follow-up read-only audit only if needed and approved.
3. Prepare a concise, verified Claude-facing summary with links to the original audit and design project folder. User resumes design discussion with Claude.
4. Claude uploads design handover after user approves it; ChatGPT checks it against Supernova standards and reconciles any discrepancies with user.
5. Only after explicit approval: add implementation requirements to GitHub's release handover, then issue normal Codex launch prompt. Preserve existing 0.137 QA and provisional 0.138 scope independently.

**Related standards and QA:** `docs/project/QA_0137_GLOBAL_BUTTON_DESIGN_STANDARD_2026-10-11.md`, `docs/project/GLOBAL_LIBRARY_PAGE_SHARED_DESIGN_RULE_2026-10-12.md`, and other existing 0.137 QA files. This document does not override them.
