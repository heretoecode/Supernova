# Future Development — repository-wide documentation reconciliation (13 October 2026)

## Coverage and limits
Enumerated the **170 Markdown/text documents under `docs/`** on documentation branch `docs/project-decisions-2026-10-12` (including new Future Development documents). Inspected the central roadmap, October 8–13 closure/planning registers, dedicated approved designs, historical parked work, explicit custom Library Page approval, and HUD seek-bar direction. This is a **repository-document inventory plus targeted content audit**, **not** a line-by-line certification of every historical file, every conversation, current source implementation, or all external assets. Historical release notes are evidence of plans, not proof of an installed feature. No code branch changed.

## High-priority status corrections

| Topic | Evidence / authoritative disposition | Future Development treatment |
|---|---|---|
| Age overlay | Owner explicitly approved age-only overlay, thin outline/red line, upper left, ten-second hold, no missing-rating display | FD-001 **approved, release unassigned**; content advisories separately FD-028 parked |
| One custom Library Page | [Explicit approval for 0.135](../project/SUPERNOVA_0.135_CUSTOM_LIBRARY_PAGE_APPROVAL_2026-10-09.md), with two approved GitHub mockups | FD-014 **historically assigned to 0.135**; **remove from unscheduled queue**; verify implementation, do not duplicate |
| Playback HUD four-button layout | [Explicit approved 0.135 design](../design/PLAYBACK_HUD_0.135_APPROVED_2026-10-10.md): four centred pill controls, Audio, Subtitles, More, etc.; remove Info and direct Settings as standalone HUD buttons, preserve functions via menus | Historical **approved design**, **not** proof installed; distinguish from **FD-002 parked broader redesign** |
| Seek bar/end-time refinement | [Dedicated parked discussion](../project/PARKED_PLAYBACK_SEEK_BAR_END_TIME_DESIGN_2026-10-11.md): floating elapsed pill, icon-only Play/Pause far left, right aligned remaining/total/Ends; final implementation not approved | FD-030 **parked refinement**, retain exact direction rather than treating as approved |
| About Settings five subsections | [All five designs approved](../design/ABOUT_SETTINGS.md): App Information, Release Notes, Licences, Credits, Technical Information; legal/binary checks still needed | FD-031 **design approved historically, implementation status unverified**; do not call it undecided or reapprove; reconcile with release scope |
| Branding, app banner and signing | Latest [roadmap](../project-decisions/CURRENT_ROADMAP_AND_DECISIONS.md) says owner reports already implemented | **Reported completed**, not a new future feature; no fresh branding task without evidence |
| 0.139 workstreams | [Authorised implementation handover](../project-decisions/2026-10-13-supernova-0139-codex-implementation-handover.md) supersedes earlier parked proposals (hero, Backup & Restore, Details, etc.) | **Active release scope** tracked outside Future Development; do not double-schedule |
| Put.io | Registered public client ID 9874, linking template and actual Settings activation unverified | **Provisional 0.140 candidate only**, no coding authorisation |
| Shield Home | Separate launcher channels, not already shipped app banner | FD-004 parked |
| Smart Collections | Historical 'NEEDS DECISION' | FD-012 under discussion / not approved |
| Library Health recovery | [Detailed parked register](../project/PARKED_OUTSTANDING_PRODUCT_DISCUSSIONS_2026-10-11.md) records safe offline/disconnected/relink/duplicate cases | FD-015 parked; don't misclassify as an already implemented full recovery UI |
| Google Drive release publishing | [Paused OAuth/publishing handover](../project/PARKED_GOOGLE_DRIVE_RELEASE_PUBLISHER_SETUP_2026-10-11.md) | FD-032 **parked operational workflow**, not product feature |
| NOVA 6.5.5 upstream integration | [Parked upgrade handover](../project/PARKED_NOVA_UPSTREAM_UPGRADE_HANDOVER_2026-10-11.md) and Stage D roadmap | Stage D **maintenance**, not unscheduled UI feature; do not implement until authorised |

## Other preserved approved historic behaviour — do not re-open as if undecided
[9 October closure register](../project/RECENT_DISCUSSIONS_CLOSURE_REGISTER_2026-10-09.md) explicitly identifies approved plans for Continue Watching actions, Up Next five-second countdown, 90% watched threshold, Multiple Versions, Segment Skipping, Search alternate titles, Movies/TV toolbar, Home Smart Rows, diagnostics, and branding. These are **historical approved scope/design decisions**; consult current release handover and source/QA before assigning any remainder. Existing NOVA subtitle styling is **preserve**, not permission to add advanced presets. Cast/Crew compact two-column design was approved, and physical Shield conformance remains a separate QA matter.

## Supersession rules
1. Explicit later owner decisions override earlier historical status tables **for planning status**, not source-code evidence.
2. **Approved design** != **assigned to release** != **implemented** != **installed/tested** != **released**.
3. Owner-reported completion is recorded as reported, never converted into verified completion without APK/QA evidence.
4. Avoid copying entire historical release QA and Stage D maintenance into the future feature queue. Keep references and clear ownership.
5. Images/videos only considered archived when their exact GitHub paths are verified. Preserve original files, do not silently substitute new mock-ups.

## Remaining verification
Check active 0.139 handover/current branch against historic approved About, custom Library Page, playback HUD and other old design contracts; inspect current accepted APK/Shield to distinguish implemented from merely approved. This cannot be proven by historical planning documents alone. The current audit intentionally makes **no claim of complete line-by-line inspection of all 170 files**.
