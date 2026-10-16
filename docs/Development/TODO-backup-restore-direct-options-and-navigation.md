# TODO — Backup & Restore: surface existing export options directly

**Owner QA clarification:** 16 October 2026. **Target:** Supernova 0.140 (combined release; standalone 0.139.2 cancelled by owner on 16 October 2026). **Status:** planned, not implemented.

## Observed on physical NVIDIA Shield
- Settings → Advanced → Backup & Restore shows only **Export Backup** and **Restore Backup**.
- Selecting **Export Backup** opens a separate **grid of existing export configuration options**.
- Therefore the options are **present and accessible**; earlier concern that they had disappeared was incorrect. This is a layout/navigation regression, not evidence of lost functionality.
- Playback, Diagnostics access and the Home Continue Watching row were reported working after the recent 0.139 corrective APK; preserve them.

## Owner-approved intended UX
- Keep the established three-panel Settings pattern.
- **Left:** Settings categories, Backup & Restore selected.
- **Centre:** Show Export Backup / Restore Backup **and the relevant existing export configuration options as a vertical list on the main Backup & Restore page**, without requiring Export Backup merely to discover/edit them. Avoid a grid of checkbox cards.
- **Right:** Contextual description for the focused setting or action.
- Export Backup remains an explicit action to actually export; Restore Backup retains appropriate file selection, confirmation and safety checks. No accidental export on merely focusing options.
- Codex should assess the most coherent arrangement and D-pad focus order, keeping existing preferences, persistence, backup contents and restore behaviour intact. Do not duplicate settings or introduce divergent state.
- **General UX principle:** Avoid unnecessary drill-down/intermediate screens when settings can be presented clearly and safely on the existing page; keep steps that are genuinely needed for file choice, safety, confirmation or complex workflows.

## Acceptance checks
- [ ] Options formerly visible in the Export Backup grid are accessible directly from Backup & Restore, in a vertical list.
- [ ] All option labels, selected values, persistence and export semantics match existing behaviour.
- [ ] D-pad Up/Down/Left/Right and Back are predictable; no unreachable options, clipping or focus traps.
- [ ] Right-hand contextual help updates with focus.
- [ ] Export and Restore actions still work with necessary file pickers/confirmations.
- [ ] Regression-check playback, diagnostics and Continue Watching.
- [ ] Use versionName 0.140 and an increasing Android versionCode and update HTML release notes.

This is a QA clarification, not authorisation to start a build or modify the active 0.139 repair branch.
