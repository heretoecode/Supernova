# Recent discussion closure register — 7–9 October 2026

**Purpose:** ensure each recoverable topic from the recent Supernova planning discussions has a clear endpoint, an authoritative GitHub location, and a next-action owner. This is a **documentation reconciliation**, not code implementation, a full verbatim transcript replay, or a claim that every historic image/video is committed. Checked current conversation and available project context, current main documentation and GitHub tree. Direct retrieval of the complete earlier long-chat transcript was unavailable; historical coverage is therefore **best-effort, not certified exhaustive**. This limitation must not be concealed.

**State vocabulary:** APPROVED/CLOSED (user input complete); PRESERVE (keep inherited behaviour); PARKED (no active discussion or implementation); SUPERSEDED (historical only); QA/ENGINEERING (investigate or implement without reopening design); WALKTHROUGH (user input intentionally deferred to one of three remaining reviews); CANDIDATE/NEEDS DECISION (not approved; do not implement by inference).

## Closed decisions and their authorities
| Topic | Discussion endpoint | Authority |
|---|---|---|
| Backup & Restore | **APPROVED/CLOSED** full state, archive format 1.0 onward, no NOVA/pre-1.0 import, embedded instructions, secrets excluded, safe staging/rollback, reconnection | `docs/design/BACKUP_RESTORE.md`, approved mock-ups, `docs/project/TECHNICAL_EXECUTION_READINESS_2026-10-09.md` |
| Diagnostic Logging | **APPROVED/CLOSED** expand existing toggle/export, broad categories, OFF means no optional capture, secret redaction, no new viewer | `docs/project/CURRENT_CHAT_DECISIONS_2026-10-08.md`, technical handover |
| Advanced Diagnostics D-pad route | **APPROVED current fixes-only scope** on 9 Oct: Advanced intro; nested Diagnostics and compatibility; reachable Export Diagnostic Report, centre/right context, focus/Back | `docs/design/SETTINGS.md`, `docs/qa/4.1.7-final-shield-qa.md`, `docs/qa/POST_4_1_7_ADDITIONAL_SHIELD_FINDINGS_2026-10-09.md` |
| Branding/Space Black Blend | **APPROVED/CLOSED visual design**, centred double concentric blue ring, approved banner/icon/splash/wordmark, static splash, loading glow, shared dark background | `docs/project/CURRENT_CHAT_DECISIONS_2026-10-08.md`, `assets/branding/`, technical handover |
| About: five subsections | **APPROVED/CLOSED design**: App Information, Release Notes, Open-source Licences, Credits & Acknowledgements, Technical Information; no in-app updater | `docs/design/ABOUT_SETTINGS.md`, credits/licence audits |
| Foundation Release identity | **APPROVED/CLOSED scope** fresh identity/installation, distinct package/signing, version/branding/attribution, no feature expansion | `docs/project/RELEASE_SEQUENCE_2026-10-09.md`, `docs/project/DECISIONS_AND_ROADMAP.md` |
| Release sequence | **APPROVED/CLOSED plan** fixes-only → Foundation → isolated feature releases; technical prep may run in parallel without changing protected branch | `docs/project/RELEASE_SEQUENCE_2026-10-09.md` |
| Home Smart Rows | **APPROVED future design** Show/Hide independent of dynamic genre membership, sort direction | `docs/design/RECENT_PRODUCT_DECISIONS_2026-10-08.md` |
| Continue Watching actions | **APPROVED latest decision** Rename Dismiss → Remove from Continue Watching Row (preserves progress); Restart Episode/Movie starts at 00:00, including Details > More; earlier Reset Progress is **SUPERSEDED** | `docs/project/CURRENT_CHAT_DECISIONS_2026-10-08.md`, recent product decisions |
| Up Next / Binge Watching | **APPROVED future design** preserve inherited episode progression, five-second countdown, Play Now, no visible Cancel, avoid post-credit damage | `docs/design/RECENT_PRODUCT_DECISIONS_2026-10-08.md` |
| Watched threshold | **APPROVED future design** 90% actual viewing or reliable end; seeking near end alone not watched | recent product decisions |
| Multiple Versions | **APPROVED future design** best compatible quality ranking, persistent manual selection, Versions (N), quality label, consistent entry routes | recent product decisions |
| Segment Skipping | **APPROVED future design** independent controls, safe smart/auto behaviour, five-second pill, session override, no raw timestamps in normal UI | recent product decisions |
| Advanced subtitles | **PRESERVE** NOVA-derived functionality; no new presets/sliders without defect | recent product decisions |
| Search / alternate titles | **APPROVED future design** local-only, articles, punctuation, minor typos, indexed real original/alternate titles | `docs/design/SEARCH_MATCHING.md`, recent product decisions |
| One custom local Library Page | **APPROVED future design**, one slot, five-step wizard, editing/deletion/unsaved safeguards; Discovery remains parked | recent product decisions, `docs/design/PARKED_FUTURE.md` |
| Movies/TV toolbar | **APPROVED future design**, outlined icon+label boxes above divider; custom-page Edit far right; supersedes underline-only idea | `docs/design/MOVIES_TV.md`, recent product decisions |
| Home Featured | Current strong left gradient/transition flash **OBSERVED**, investigate; proposed larger exposed-card carousel **PARKED** | `docs/design/HOME.md` |
| Existing 4.1.7 Shield QA | **APPROVED fixes scope**, 35 base entries plus dated approved Advanced Diagnostics addition | `docs/qa/4.1.7-final-shield-qa.md` |
| Credits/licences | **APPROVED About design**; exact packaged notices/licences **ENGINEERING verification**, not a new UI discussion | `docs/design/ABOUT_SETTINGS.md`, `docs/audits/FOUNDATION_RELEASE_CREDITS_LEDGER_2026-10-08.md` |
| Technical backup, diagnostics, production assets | **ENGINEERING/QA pending**, requirements closed; not user planning questions | `docs/project/TECHNICAL_EXECUTION_READINESS_2026-10-09.md` |
| MobLand S2E4 playback reprepare | **QA evidence pending** user's diagnostic ZIP; no proven cause; remind conversationally before next Codex handover, no scheduled reminder | additional Shield QA findings |

## Explicitly parked, not forgotten
- Profiles, AI/Ask Supernova, Anime, Discovery Page, Library Health, person/cast profile navigation, seek thumbnails, Trakt reviews, broad provider/metadata audits, broad architecture/Kotlin/Compose migration, Home exposed-card Featured redesign, and other speculative ideas: **PARKED** under `docs/design/PARKED_FUTURE.md`, `docs/project/DECISIONS_AND_ROADMAP.md`, recent product decisions and `docs/design/HOME.md`.
- Smart Collections **NEEDS DECISION** remains an unactivated concept distinct from the approved **one custom local Library Page**; no assumption of approved Smart Collections implementation.
- Multi-source ratings (IMDb/Rotten Tomatoes/TMDb via candidate providers) **CANDIDATE/NEEDS DECISION**, not authorised. Evidence and API/legal investigation must precede any future approval. Do not treat as current open user input or a promised feature.
- Existing NOVA-derived subtitle styling **PRESERVE**, not a pending redesign.

## Remaining user-input checkpoints — assigned to the three agreed walkthroughs
1. **Network & Files walkthrough:** broader page structure/navigation review deliberately deferred, not automatically approved.
2. **Settings walkthrough:** broader settings hierarchy and page review deliberately deferred. The narrow Advanced/Diagnostics access fix is **already approved** and is not contingent on reopening the entire Settings review.
3. **Installed-APK walkthrough and targeted QA:** physical review and concrete UI decisions for the current fixes release, notably:
   - Cast & Crew **compact replacement visual now APPROVED** from the user's supplied two-column table mock-up: CAST left / CREW right, no heading counts, equal row spacing. Only physical layout verification remains during APK walkthrough; see `docs/design/DETAILS.md` and QA item 15.
   - Playback HUD **Subtitles and Audio submenu details**, and **More menu exact contents**: QA items 30–32 and `docs/design/PLAYBACK.md` retain **NEEDS DECISION / under review**. Resolve from physical walkthrough if needed before implementation; preserve four approved primary controls.
   - Additional observed playback reprepare and visual/focus bugs may need diagnostic evidence; technical investigation, not preemptive design re-discussion.

## Other historical open lines — dispositions without inventing user approvals
- `docs/project/CURRENT_CHAT_DECISIONS_2026-10-08.md` contains early 'banner undecided', 'background not uploaded', 'ring refinements needed' and 'About proposed' entries: **SUPERSEDED** by later approvals, verified assets and `docs/design/ABOUT_SETTINGS.md`.
- Older Reset Progress and sixth Exclusion Options step: **SUPERSEDED** by Restart and five-step wizard.
- **APPROVED 9 October:** After **Remove from Continue Watching Row**, retain watched status and saved resume position. The card stays removed until the user **actively starts playback of that same item again**, at which point it becomes eligible to reappear in Continue Watching under normal row rules. Codex should cover this with automated tests where feasible; user checks physical Shield behaviour during installed-APK walkthrough. No separate product decision remains.
- 'Can previous diagnostic logs be exported after OFF?' is an **implementation retention/UX edge case**, not settled user preference. Safest specification: OFF stops new optional capture immediately; keep bounded existing logs available for user-initiated export unless a later explicit decision changes retention. Do not imply user approved automatic deletion or indefinite retention.
- Current fixes-release version number and Foundation package ID/versionCode/signing custody are **ENGINEERING/RELEASE GATES**; do not invent values or request visual redesign. App version 1.0 is not the backup archive format 1.0.
- Historic QA item 35 artwork failures is **INVESTIGATE**, not a proven provider defect.

## Evidence completeness and handover standard
The main repo has canonical approved 4K Space Black Blend and double-ring visual reference assets and three approved Backup & Restore mock-ups. Some historic chat-generated mock-up binaries, user's newer photos/videos (including Advanced `IMG_4760.mp4`), and pending diagnostic ZIP **are not committed**; their described observations/decisions are recorded, but this is not equivalent to having all original binary evidence. Do not assert complete visual/video archive coverage.

**Conclusion (updated after user supplied Cast & Crew mock-up):** all recoverable recent topics are classified and assigned an endpoint. Cast & Crew visual design is **approved**. Playback submenu/More exact contents remain scheduled for review against existing decisions in the **installed-APK walkthrough**; they do not form a fourth discussion track. A full message-by-message audit of inaccessible historic chat content cannot be certified. The three user-requested walkthroughs remain the only planned discussion tracks. A full message-by-message audit of inaccessible historic chat content cannot be certified.

## 9 October — user-approved Continue Watching re-entry rule and mock-up handover
User accepted the recommended rule: a removed item re-enters Continue Watching after playback is explicitly restarted; mere app relaunch, library scan or metadata refresh must not immediately undo the removal. Persist dismissal state until active playback, then clear dismissal for that item only. Retain existing resume/watched state. Physical QA remains for installed-APK walkthrough.

**Cast & Crew image storage:** recommended canonical path `docs/design/approved-mockups/details/cast-crew-compact-approved.png` on `main`. Image currently supplied in chat; **not yet committed as binary to GitHub**. Its visual approval is recorded in `docs/design/DETAILS.md` and the fixes-only QA document. File upload/renaming is asset housekeeping, not a new design discussion.
