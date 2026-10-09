# GitHub documentation consistency audit — 9 October 2026

**Scope:** read current `main` documentation and repository tree, reconcile user's 9 October decisions. **Documentation-only changes**; no source, build, QA artefact or fixes-only branch changes. This is a **targeted documentation audit**, not a full repository source/asset verification, binary compliance sign-off or complete recovery of all historical chats.

## Confirmed and authoritative
- **About:** `docs/design/ABOUT_SETTINGS.md` now records all five approved future subsections: App Information, Release Notes, Open-source Licences, Credits & Acknowledgements, Technical Information. **Design complete; not implemented.** Actual APK-specific credits/licence validation still outstanding.
- **Branding:** approved canonical `assets/branding/Supernova_Space_Black_Blend_4K_UHD.png` and `assets/branding/Supernova_Double_Ring_Final_Visual_Reference.png` are present in `main` tree. The approved banner/splash composition, symbol, icon design and animation direction are in `docs/project/CURRENT_CHAT_DECISIONS_2026-10-08.md`. **Visual design discussions complete** per user. Production exports, final artwork assembly, app integration and Shield validation are **not established** by the visual reference alone. Do not mislabel design complete as installed.
- **Backup:** `docs/design/BACKUP_RESTORE.md` now explicitly states archive format **1.0 onward** is the only backward-restore support commitment. No pre-1.0/NOVA import. Future versions must continue to restore supported 1.0+ archives. Existing export code was found to include credentials/unfiltered settings; the **feature is not technically compliant yet**, despite product design being complete.
- **Diagnostics:** `docs/project/CURRENT_CHAT_DECISIONS_2026-10-08.md` now records the comprehensive extension of the existing toggle/export across crashes, leaks, UI, playback, indexing, networking and performance. Redact usernames, passwords, credentials, keys and tokens; no new viewer or remote upload approved. Design intent settled; actual logging coverage, instrumentation overhead and export quality remain implementation/QA tasks.
- **Home and Movies/TV:** `docs/design/HOME.md` and `docs/design/MOVIES_TV.md` document user-observed hero gradient/flicker and future boxed-toolbar/focus intent. User photo/video were not committed as binaries.
- **Search/custom page/other future features:** `docs/design/RECENT_PRODUCT_DECISIONS_2026-10-08.md` records approved future designs, parked ideas and implementation boundaries. These are not automatic release commitments.

## Stale text / precedence warnings found
1. `docs/project/CURRENT_CHAT_DECISIONS_2026-10-08.md` is a **chronological log** containing older statements such as “no final banner approved”, “4K background not yet in GitHub”, and early ring concepts. Its **later explicit approvals and uploaded reference records supersede those older lines**. A 9 October clarification was appended; do not treat older paragraphs as current state.
2. `docs/design/PARKED_FUTURE.md` still uses older generic wording that About/versioning and all custom-page concepts are parked. **Superseded for design status:** About is designed; **one custom local Library Page** is approved as future design, while Discovery remains parked. None is authorised for fixes-only implementation.
3. `docs/project/CONVERSATION_RECOVERY_AUDIT_2026-10-08.md` and `docs/design/RECENT_PRODUCT_DECISIONS_2026-10-08.md` preserve historically open questions and superseded actions (e.g. Reset Progress versus Restart). **Read the latest supersession**, not an isolated earlier paragraph.
4. `docs/project/DECISIONS_AND_ROADMAP.md` retains an earlier “signing mechanism needs decision” note; don't interpret it as a request to reopen visual branding. Signing-key custody and final release execution must still be verified independently before any build.
5. `docs/audits/FOUNDATION_RELEASE_CREDITS_LEDGER_2026-10-08.md` is a source-level inventory only. Final APK dependency/native binary validation and legally complete credits are **not passed**, regardless of approved About UI.

## Current user-imposed planning boundary
**Set aside:** wider Settings walkthrough, Network & Files walkthrough, full installed-APK walkthrough. Do not bring these back as next steps unless the user asks.

**Implementation boundary:** `main` documentation may record approved design; **do not modify `codex/post-4.1.7-shield-fixes`**, merge, rename/sign or expand release scope without separate authorisation. “Approved design” ≠ “implemented” ≠ “physically verified”.

## 9 October follow-up — reconciliation completed in place
The older headings in `docs/design/PARKED_FUTURE.md` and `docs/project/DECISIONS_AND_ROADMAP.md` were subsequently corrected in place on `main`; `docs/design/README.md` now links the approved About/Backup authorities. The current discussion-closure audit is `docs/project/RECENT_DISCUSSIONS_CLOSURE_REGISTER_2026-10-09.md`. Historical observations above remain preserved as audit history, **not current warnings that those headings are still stale**. This is not a certification of inaccessible chat messages or missing historical binary mock-ups.

## Audit conclusion
The main design decisions inspected are captured and identifiable. The remaining substantive issues are **execution/validation**, not new design choices: backup secret exclusion and full-state restore, comprehensive diagnostic instrumentation, production branding exports, release-specific licence compliance, and future implementation of approved features. This audit is **targeted rather than exhaustive** and cannot certify that every historical discussion/mock-up exists on GitHub.
