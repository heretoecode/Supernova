# Implementation progress

## Development checkpoint — 10 October 2026

Branch: `codex/supernova-next-refinement-2026-10-10`. Signed reference: 0.135 source `cfc2ca2376fb77f79a85a3a5e45333d38b9689e7`; baseline branch documentation tip `42ff29e7`. Historical development branches and main remain untouched.

Work in progress across requirements #1–#43: shared controls and keyboard; Search two-area composition; header underline/clock/transition; hero copy/actions; durable stable-identity viewing history and conservative episode eligibility; custom editor overlay and wizard controls; onboarding completion/scan gates; browser hierarchy, in-panel connections/discovery/Library Health; Settings inline choices and nested visibility; bounded asynchronous licence reader; QA-default diagnostics, persistent exit evidence and ten-second resource sampling.

These are development edits, **not completed fixes**. Fresh compilation, tests, conformance review and physical navigation validation remain pending. The initial local compile reached the Java compiler and exposed a FrameLayout conversion error; corrected for the next run. Pinned dependencies, Android SDK and a complete JDK are provisioned. Proxy CA trust was added to a local Java trust store without disabling TLS verification.

Next: compile all changes, complete backup/history and integration controls, review shared infrastructure exclusions, add meaningful policy/regression tests, and prepare exact-source QA build/signing workflow. Signing material remains solely in the existing protected GitHub environment. The new branch is not yet admitted by its exact-branch policy.

Physical SHIELD QA, upgrade installation, remote focus/visual conformance and soak tests are pending user QA. put.io production OAuth remains externally dependent on the registered client configuration. No final release version or public release is claimed.

## Second implementation checkpoint
- Added shared library header/stats and custom-library rendering, focused floating editor with persistent footer, direct content/language/review controls and a dedicated centred delete confirmation.
- Integrated saved shares, connection fields, device discovery, put.io connection controls and five-category Library Health into the browser panels. Normal left parents expand on OK; first-run drives remain flat and local-only.
- Settings now uses inline native choices and grouped providers, independent integration switches with credential retention, direct OpenSubtitles fields, merged Advanced/About overview and explicit asynchronous licence opening. Backup options remain reachable in Backup & Restore.
- Durable identity history retains completion independently of files, source availability and resume; history/resume backup exclusions are enforced in both database and preference copies. Fixed the real SQLite WAL checkpoint before export; restore fences pending history writes. Explicit existing watched/unwatched actions update identity state.
- Provisional QA identifier **0.136 / versionCode 136**. This is not approval of a final release number. New branch-owned signing scripts preserve the historical workflows, pinned certificate and exact-source binary/native/test gates.
- Validation so far: compilation passed at the previous checkpoint; 49 existing Python guard tests passed. Targeted run executed 28 tests: 25 passed, three remaining UI expectations/focus defects subsequently corrected but not yet revalidated. Initial broad run: 458 executed, 27 failures, three skips and worker exit; not a passing result. JDK 21 is now installed to run supported SDK tests; complete rerun pending.
- New history/hero, keyboard, explicit licence and provider persistence/focus tests added. No physical SHIELD or upgrade acceptance claimed.
- Existing signed 0.135 artifact download again returned Forbidden. Historical CI verification is preserved honestly without inventing a signed APK hash.
