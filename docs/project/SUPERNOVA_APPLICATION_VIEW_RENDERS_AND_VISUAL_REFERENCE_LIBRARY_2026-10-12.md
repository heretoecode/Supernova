# Supernova application-view renders and future visual reference library — 2026-10-12

**Status:** Discussion and future documentation proposal only. No implementation, new Codex task, or release change authorised.

## Immediate context: Customise Home read-only audit

Codex completed a read-only Customise Home / Genre Rule implementation audit and supplied a Markdown report and six UI captures in [Google Drive — Codex Reports / Customise Home Menu Redesign](https://drive.google.com/drive/folders/1AJJ4_S5FS0u87Hk8mh6SathtWiGHMLKN). The audit was reported against commit `45521c370563c8c176b0c3d9435fbf08fcd115e0` on the 0.137 development branch, without application changes.

These captures were described in the audit as CI/test-produced application-view renders, rather than live Shield screenshots or AI mock-ups. Such captures can be useful evidence of actual app UI component rendering, but do not automatically demonstrate complete navigation or all states in the running app. The reported test environment did not provide a full Android emulator/Shield runtime.

**User clarification:** The small 700 × 350 Customise Home component capture is **not** itself a problem. The user understands the background/full TV frame is outside the menu component and does not require it in that image. The meaningful omission is **navigation into the submenu**, especially **Customise Home → “Genre: All”**, where the user expects a separate visual reference of the resulting genre-selection interface. Existing six images do not exhaustively capture the workflow and menu states. Do not misconstrue this as a request to enlarge the component render or add a decorative background.

Before asking Codex for more images, review the delivered captures and report against Claude's actual needs when Claude is available; request targeted missing states as appropriate. Do not claim that all states are covered merely because six images were delivered.

## Distinguish types of visual evidence

- **AI mock-up:** conceptual/proposed appearance; not evidence of actual implementation.
- **Application-view render:** real Supernova UI code rendered in an automated Android view/test environment; useful for a component and its programmed states, but may not show the assembled TV app or real device behaviour.
- **Android TV emulator screenshot:** capture of app running on an emulated device; can show integrated layout and navigation if properly configured.
- **NVIDIA Shield screenshot/photo:** capture of app running on the user's actual test device; strongest evidence of user-observed appearance and hardware-specific behaviour, subject to capture limitations.

Every archived image should be labelled by provenance, exact app branch/commit/build, screen/state, dimensions, data source and known limitations. Never pass off a test render as a real-device capture.

## Potential future project: Supernova Visual Reference Library

User sees potential value in Codex creating a **full visual documentation catalogue** of Supernova for design collaboration, regression analysis and QA. This is a *parked proposal*, not an approved or scheduled project.

Possible scope, if later approved:
1. Inventory real screens, dialogs, overlays, submenus, interaction paths and empty/loading/error/focus states from the current code.
2. Capture full-screen app images where possible using a properly configured Android TV emulator/CI workflow, supplemented by genuine application-view renders for components/states that cannot be captured end-to-end.
3. Document remote-control navigation (D-pad directions, OK, Back, focus restoration), visual components, relevant source paths, conditions under which states appear and test limitations.
4. Version snapshots by release/branch/commit and store the visual exchange/archive in appropriately named Google Drive folders, while keeping GitHub authoritative for implementation requirements and source.
5. Optionally use the library to compare before/after designs and detect visual regressions; account for non-deterministic artwork, data, fonts and rendering.
6. Avoid exposing personal media titles/file paths, credentials, network information or other private data; prefer deterministic representative test fixtures.

**Feasibility gate:** First determine whether Codex/CI can reproducibly run the full app on an Android TV emulator, drive navigation and capture screenshots. The prior audit environment reportedly lacked a runnable emulator; do not promise full-app captures until tooling is established.

**Project safeguards:** Read-only investigation/documentation first. Do not interfere with the active 0.137 work, start 0.138, alter app code or launch a new Codex task without the user's express approval. The current Claude Customise Home collaboration and the broader future visual-library concept are distinct efforts.

## Follow-up when resumed

- Ask Claude to identify the missing visual states needed for its current Customise Home design; specifically include **Genre: All** submenu.
- If user approves, prepare a focused read-only capture request for missing states.
- Separately, if user wishes, propose a feasibility/inventory audit for a comprehensive versioned visual reference library.
