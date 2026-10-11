# SUPERNOVA — NEXT CODEX IMPLEMENTATION HANDOVER (APPROVED 2026-10-11)

**Repository:** `heretoecode/Supernova`  
**Requirements branch:** `docs/nova-upstream-deep-audit-2026-10-11`  
**Decision:** User explicitly authorised Codex to proceed with the next version and requested a quick turnaround, focusing on the confirmed issues below. This **supersedes earlier “staged / not yet authorised” language only for this enumerated next-version scope**. Other projects, parked work and broader feature requests remain unauthorised.

## 0. Start here: branch, provenance, preservation
1. Inspect GitHub main, open PRs, Codex work branches, actual latest source, existing 0.136 Shield QA baseline, tests and changelogs **before writing code**. In particular, reconcile open refinement PR #10 (`codex/supernova-next-refinement-2026-10-10`, if still open) and any overlapping changes; do not assume the docs branch is an implementation base.
2. Retrieve **all linked requirements directly from this docs branch** (they may not exist on main). Read existing refinement register `docs/SUPERNOVA_POST_0.135_REFINEMENT_REGISTER_2026-10-10.md` from the branch/PR where it exists, if present; it was not available on main at handover preparation. Locate historical design mockups, approved references, existing QA logs and test evidence before changing UI.
3. Create/use a suitable **new implementation branch** from the correct reconciled source baseline; do **not** reset, discard, overwrite, silently merge, or mutate any other active Codex work. No automatic merge to main. Avoid duplicate implementation of items already fixed; verify rather than assume.
4. Preserve existing Supernova identity, package name, signing/install continuity, library data, media sources, watch history, playback behaviour and existing approved design. No NOVA upstream rebase or wholesale UI rewrite.
5. Use source investigation and actual logs to establish root causes; don't mark bugs fixed solely because code compiles. If physical Shield access is unavailable, state that explicitly and provide exact Shield manual acceptance steps rather than claiming physical validation.

## 1. REQUIRED DOCUMENTS — read every one
**Primary approved designs**
1. [First-run Home and placeholders](SUPERNOVA_FIRST_RUN_HOME_PLACEHOLDER_BEHAVIOUR_2026-10-11.md)
2. [Global responsive layout](SUPERNOVA_GLOBAL_RESPONSIVE_LAYOUT_STANDARD_2026-10-11.md)
3. [Global dialog design](SUPERNOVA_GLOBAL_DIALOG_DESIGN_STANDARD_2026-10-11.md)
4. [Global button design, including proportional size families](SUPERNOVA_GLOBAL_BUTTON_DESIGN_STANDARD_2026-10-11.md)
5. [Global app-owned on-screen keyboard](SUPERNOVA_GLOBAL_ON_SCREEN_KEYBOARD_STANDARD_2026-10-11.md)
6. [Custom library wizard focus, keyboard and unsaved-changes exit](STAGED_CUSTOM_LIBRARY_WIZARD_FOCUS_KEYBOARD_AND_EXIT_2026-10-11.md)

**Physical Shield QA and functional defects**
7. [Hero transitions, Continue Watching, custom page parity](STAGED_HERO_CONTINUE_WATCHING_CUSTOM_LIBRARY_QA_2026-10-11.md)
8. [Scanning/indexing, Settings crash, diagnostics, All Files toggle](STAGED_SHIELD_QA_SCANNING_SETTINGS_CONTINUE_WATCHING_2026-10-11.md)

**Operational context only**
9. [Existing Google Drive release publisher setup and limitations](PARKED_GOOGLE_DRIVE_RELEASE_PUBLISHER_SETUP_2026-10-11.md)
10. [Explicitly parked future work — do not implement](PARKED_OUTSTANDING_PRODUCT_DISCUSSIONS_2026-10-11.md)

When requirements conflict, apply the **most recent explicit user clarification** in documents 4, 7 and 8; preserve currently working behaviours. Numerical UI tokens are **provisional calibration guidance**, not immutable pixel measurements. User-approved visuals and the existing working app take precedence over generic redesigns.

## 2. Priority A — urgent reliability and functional defects
**A1 Settings crash / diagnostic access (release blocker):** Settings left rail works through Network, then Down crashes app. This blocks Integrations, About, Advanced and Export Diagnostic Report. Trace logcat/root cause, repair all category navigation/focus and verify all categories and export; never skip/hide them as a workaround.

**A2 Library indexing / external drives:** User selected media folders on two drives; correct Add/Remove from Library labels, but expected media absent after a long-running animated Scanning Local Library pill and Overview indexing state. Investigate persisted selections, permissions, scan jobs, traversal, progress, indexing writes and Home refresh. Differentiate slow vs stalled vs stale indicator; instrument safe bounded progress. Fix demonstrated defects/performance bottlenecks. **Do not scan before explicit folder confirmation** or scan merely on opening browser/Overview. Do not destructively remove offline media.

**A3 Continue Watching live update:** After watching Vigil S2E1, resume progress appeared on Recently Added but Continue Watching was absent; Show/Hidden toggle ineffective. App restart populated Continue Watching, moved Vigil out of Recently Added, and made toggle work. Trace live refresh/invalidation, do not guess membership root cause. Returning from playback must update Home without restart. Preserve progress/history. Remove erroneous Continue Watching row above TV Shows toolbar (and analogous leakage) without removing correct Home row.

**A4 Network & Files All Files toggle:** On external drive, Grid/List and Sort work but All Files does nothing. ON = all files and folders; OFF = supported video files and navigable folders. Immediate filtered refresh, correct state indication and stable D-pad focus; no source-selection changes, scan or file mutation. Preserve Grid/List and Sort.

**A5 Network & Files D-pad reliability:** Repair traps, incorrect transitions/return, scrolling, flashing, clipping and focus enlargement across local drives and network browsing. Separate navigation logic fixes from responsive layout changes.

**A6 Diagnostics:** Highest existing **safe detailed level ON by default from first launch**, including fresh installs/app-data reset; **remove the two-level selector from UI entirely**; keep export. Preserve redaction, privacy salting where applicable, no credentials/tokens, bounded rotation/storage and low overhead.

## 3. Priority B — approved UX/design changes
**B1 First-run:** Real production Home with realistic navy hero and Recently Added placeholders (no fake media, no empty Continue Watching row); welcome dialog with real Supernova icon, Network & Files and Explore Supernova; persist dismissal. Placeholder More Info/card actions show explanatory modal, never playback or scanning. Progressively replace placeholders as media arrives. Preserve existing Home design.

**B2 Global responsive consistency:** Shared logical TV viewport/safe-area scaling, typography, spacing, card expansion clearance and reflow across Home, nav, grids, Movies/TV/Custom pages, Settings, Network & Files, dialogs, keyboard and Playback HUD. This is an audit/correction **not** authorisation to redesign Details or Playback HUD. Validate 720p/1080p/4K logical layouts and focus.

**B3 Global dialogs and buttons:** Shared dark navy modal/dim/blur/focus conventions; shared translucent dark button language, Title Case, meaningful left icons, consistent icon family and proportional size variants. Hero More Info compact; functional library toolbar buttons larger **with proportional icon/text/padding/gaps/radius/height**, not arbitrary independent styling. Calibrate on Shield.

**B4 Global app-owned keyboard:** One-line always-visible outlined input with appropriate title/placeholder, caret/edit buffer; compact 2x3 Caps/Space/Backspace and Clear/Done/Cancel controls, Done commits field (not entire page), Cancel rolls back, correct D-pad focus. Do not replace Android system IME.

**B5 Custom library wizard:** Fix Up from Page Details to top nav, Right from Review to centre panel/Save Page; Back on dirty changes opens Unsaved Changes dialog with Continue Editing (safe), Save Page and Discard Changes. Keep Genres as context menu and right-hand live preview; no unapproved cosmetics.

**B6 Custom library page parity:** Use same shared layout/components as built-in Movies and TV Shows, including toolbar icons/order, Edit Library placement, card outer-box focus expansion, grid/list spacing, typography, responsive sizing and navigation; differ only by library config/filters.

**B7 Hero carousel:** Make centre/current and immediate neighbour heroes fully ready (art, metadata, fonts) before transition where feasible, bounded prefetch, smooth animation/rapid D-pad, stable fallback rather than indefinite blocking. More Info background slightly darker for readability; tagline-only subtle contemporary handwritten-feel typeface, legible/licensed. Preserve hero composition.

## 4. Explicitly out of scope
- Cosmetic Genres menu, Filters/Content Type/Display Options and Customize Home pop-up redesign (beyond required responsive/focus/functionality fixes).
- Details-page redesign, Playback HUD redesign, NOVA 6.5.5 upstream integration, Kotlin/Compose rewrite, AI, profiles, discovery, smart collections, broader Library Health/Recovery, and unrelated integration work.
- Google Cloud OAuth/Drive publisher workflow completion or PR #11 merge is **not** a prerequisite for this release. Do not request/store credentials or weaken secret handling.
- Do not silently promote historical refinement items into this version; reconcile overlap and report deferred items.

## 5. Delivery: fast, but verifiable
1. Work in logical checkpoints. Prioritise A1–A4 and crash/indexing regressions first; implement approved B scope after stabilising critical defects. If a blocker prevents completing a subset, report it precisely; do not declare an incomplete release finished.
2. Build a **signed installable QA APK** with existing signing/application identity and a unique, traceable version/build number; do not publish or merge without separate authorisation. Run relevant build/unit/instrumentation checks available; give results and limitations.
3. **Primary APK delivery: the user will attach/connect the Google Drive plugin to the new Codex chat before submitting the prompt. Use that session's connected Google Drive capability to locate the user's existing Supernova APK folder and upload the verified APK there**, provided the connector exposes the required upload/write action and permissions. Do not assume the connection is active until verified; do not create a replacement folder or restart the paused Google Cloud OAuth/GitHub publisher setup. Confirm actual successful upload, correct file and access before reporting Drive delivery; supply the Google Drive file link. If the attached plugin lacks upload capability or permissions, immediately use the fallback rather than blocking the release. Do not claim the existing OAuth/publisher setup is complete; don't block APK production on it.
4. **Fallback: if Google Drive upload is unavailable, fails or cannot be verified, provide a directly downloadable HTTPS link to the exact APK**, preferably a GitHub Actions artifact or GitHub release/asset permitted by existing workflow (or another real downloadable artifact host). Verify link exists, permissions allow user access, and identify expiration/login requirements. Never invent a URL or claim an APK exists before build. If neither route works, report precise obstacle and artifact location for retrieval; keep build accessible.
5. Provide commit SHA, branch, APK filename/version, SHA-256, build/signing provenance, short change log, tests passed/failed, known limitations and Shield manual QA checklist (Settings beyond Network/export, scan two drives, Continue Watching no restart, All Files, wizard, first-run, focus, hero, UI).
6. Do not automatically merge to main, change release signing, delete media, alter credentials, or start the next version. Leave a reviewable PR/branch and clear final handover.

## 6. New Codex chat handover convention
The user starts **each coding session in a fresh Codex chat** to avoid conversation-length problems and will attach the **Google Drive plugin** to that chat before sending the implementation prompt. Treat this master handover and the repository as the full context; do not rely on previous Codex chat state. Inspect the attached connector's actual supported actions and permissions, rather than assuming attachment guarantees write access. Continue using direct APK delivery fallback if upload is unsupported or fails.

## 7. Acceptance standard
Functional fixes are accepted only with demonstrated before/after evidence or explicitly marked **awaiting user's Shield verification**. Do not describe simulated or emulator results as physical Shield tests. Protect the working existing app while shipping a focused, testable QA build quickly.
