# Next refinement requirements completion report

Authoritative map: [requirements.json](requirements.json). Code checkpoint: `92900e4647d1ea614d2ef8f0e1dad9fb736e3e05`. All rows follow the latest approvals; historical tentative headings do not override #27/#28/#29/#39/#43/#45.

Current verification: **514/514 tests across 132 classes**, 0 failures/errors/skips; release lint 0 errors / 1494 warnings; Python 58/58; prepared-source conformance PASS. Native exact-source APK conformance still in progress. No hardware results or signed APK claimed.

| ID | Requirement | Implementation / acceptance status |
|---|---|---|
| 1 | Clock typography mismatch | Implemented; Local automation verified; native/device acceptance pending |
| 2 | Custom Library Page: remove icon choice | Implemented; Local automation verified; native/device acceptance pending |
| 3 | Top-navigation blur abrupt pop-in | Implemented; Local automation verified; native/device acceptance pending |
| 4 | Navigation focus: accent-colour animated underline | Implemented; Local automation verified; native/device acceptance pending |
| 5 | Hero carousel: gradient persists on off-centre cards | Implemented; Local automation verified; native/device acceptance pending |
| 6 | Hero carousel: remove Play action | Implemented; Local automation verified; native/device acceptance pending |
| 7 | Hero carousel: More Info control styling | Implemented; Local automation verified; native/device acceptance pending |
| 8 | Shared boxed-action / option control foundation | Implemented; Local automation verified; native/device acceptance pending |
| 9 | Customize Home | Implemented; Local automation verified; native/device acceptance pending |
| 10 | Shared library pages (Movies, TV Shows, Custom Library Page) | Implemented; Local automation verified; native/device acceptance pending |
| 11 | Shared control labels | Implemented; Local automation verified; native/device acceptance pending |
| 12 | Search page | Implemented; Local automation verified; native/device acceptance pending |
| 13 | Single global on-screen keyboard design | Implemented; Local automation verified; native/device acceptance pending |
| 14 | Global keyboard baseline and action row | Implemented; Local automation verified; native/device acceptance pending |
| 15 | APPROVED global keyboard visual design | Implemented; Local automation verified; native/device acceptance pending |
| 16 | Custom Library Page | Implemented; Local automation verified; native/device acceptance pending |
| 17 | Populated Custom Library Page must reuse default library presentation | Implemented; Local automation verified; native/device acceptance pending |
| 18 | Custom Library Page wizard | Implemented; Local automation verified; native/device acceptance pending |
| 19 | Custom Library wizard Step 3 | Implemented; Local automation verified; native/device acceptance pending |
| 20 | Custom Library wizard D-pad focus, Back behaviour, unsaved changes and parent-page restoration | Implemented; Local automation verified; native/device acceptance pending |
| 21 | Network & Files | Implemented browser/source/health controls; put.io production OAuth externally blocked; local automation verified; native/SHIELD pending |
| 22 | Walkthrough coverage and outstanding work | Scope/authority recorded; Details/HUD deferred; automated scope conformance verified; physical regression pending |
| 23 | Home hero carousel | Implemented; Local automation verified; native/device acceptance pending |
| 24 | Persistent watch history, backups, privacy setting and hero fallback | Implemented; Local automation verified; native/device acceptance pending |
| 25 | Clarification | Implemented; Local automation verified; native/device acceptance pending |
| 26 | Hero-card text contrast and tagline-versus-synopsis design review | Implemented; Local automation verified; native/device acceptance pending |
| 27 | Final approvals | Implemented; Local automation verified; native/device acceptance pending |
| 28 | Approved watch-history retention, configurable backup selection and deferred contrast QA | Implemented; Local automation verified; native/device acceptance pending |
| 29 | Hero carousel ranking | Implemented; Local automation verified; native/device acceptance pending |
| 30 | Settings QA | Explicit bounded licence reader and enhanced exit evidence implemented; device freeze/crash cause still unproven; local automation verified; native/device pending |
| 31 | Timestamped reproduction | Explicit bounded licence reader and enhanced exit evidence implemented; device freeze/crash cause still unproven; local automation verified; native/device pending |
| 32 | Settings comprehensive Shield walkthrough | Implemented; Local automation verified; native/device acceptance pending |
| 33 | Settings walkthrough final sign-off | Implemented; Local automation verified; native/device acceptance pending |
| 34 | Screenshot-confirmed foundational Settings panel hierarchy | Implemented; Local automation verified; native/device acceptance pending |
| 35 | First-run onboarding QA | Implemented; Local automation verified; native/device acceptance pending |
| 36 | First-run Build Your Library introductory guidance | Implemented; Local automation verified; native/device acceptance pending |
| 37 | Multi-drive configuration transaction and confirmed pre-onboarding scan | Implemented; Local automation verified; native/device acceptance pending |
| 38 | Onboarding browser space usage | Implemented; Local automation verified; native/device acceptance pending |
| 39 | Approved three-panel foundation from Build Your Library mock-up | Implemented; Local automation verified; native/device acceptance pending |
| 40 | Diagnostic ZIP audit | Implemented; Local automation verified; native/device acceptance pending |
| 41 | Second diagnostic export | Explicit bounded licence reader and enhanced exit evidence implemented; device freeze/crash cause still unproven; local automation verified; native/device pending |
| 42 | Approved direction: developer diagnostics, defaults and focus/performance monitoring | Implemented; Local automation verified; native/device acceptance pending |
| 43 | Next release scope confirmation | Implemented; Local automation verified; native/device acceptance pending |
| 44 | Next-release documentation readiness and deferred walkthroughs | Scope/authority recorded; Details/HUD deferred; automated scope conformance verified; physical regression pending |
| 45 | User authorised Codex implementation start | Scope/authority recorded; Details/HUD deferred; automated scope conformance verified; physical regression pending |

## Release boundaries

Original package/signing pin and user data are preserved in source. Legacy Details focus factory, non-boxed Details toolbar and default title-logo renderer retain their behaviour; new styles are opt-in for refinement screens. No Details visual source or Playback/HUD layout changes. Shared history/resume, keyboard, navigation/clock, diagnostics and native policy/lifecycle changes are documented in [conformance review](CONFORMANCE_REVIEW.md).

## Outstanding dependencies

- Exact new branch admission to protected environment `supernova-foundation-signing`: attempted API update returned HTTP 403. No key replacement or signing bypass.
- Production put.io OAuth: actual registered client/redirect configuration and end-to-end validation unavailable.
- Physical acceptance: see [SHIELD QA checklist](SHIELD_QA.md), including signed 0.135 upgrade/data retention, D-pad/contrast/licence reproduction and sustained diagnostics/native playback.

See [progress](PROGRESS.md), [release notes](RELEASE_NOTES.md) and [QA readiness](QA_READINESS.json). Machine-readable local evidence: [TEST_RESULTS.json](TEST_RESULTS.json). Native evidence will be added after its gate finishes.
