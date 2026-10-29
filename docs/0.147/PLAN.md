# 0.147 implementation plan — 2026-10-29

Base: 8e3cf4e1e9ca66d9528598ffdef30c963b194496 (current PR26 head).
Validated application: 938885ac2a84a5efecb1674b3e9f1032a56740ed. Later changes are docs/0.146 only.
Main: 75d9f012fa4f65869eef5d972ad4725df504bd9c; PR26 remains draft/unmerged and stacked on 0.145.
Documentation authority: 58cac445b1dd66ba8ad0a4b6ddd12008fbb3144a.
GitHub releases API returned no releases. Signed 0.146 QA delivery is documented in RELEASE_REPORT and PR26; physical acceptance remains pending.

## Route plan before UI changes
Landing: full width, three columns by two rows, in approved order.
- Local Storage: mounted storage and All Files (existing browser toolbar, breadcrumbs, actions).
- Network Locations: saved/indexed locations, discovery, Put.io account/device OAuth and API browse/search.
- Add Connection: six-protocol picker and existing connection form, with independent save-location/library actions.
- Manage Connections: saved/indexed location rows, source roles and existing contextual credential/bookmark/library actions; Put.io account ownership actions. No new durable connection entity is implied.
- Network Scanning: existing automatic/frequency/on-return/source flags and live status, plus safe manual scans.
- Advanced Options: Library Health (all five categories), All Files, compatibility and trusted-server settings handoffs.

Retain shared browser, transport, credential storage, database, scan safety and provider controllers. No schema or preference migration. No physical delete additions.
Restore the existing TopNavigation around Settings, remove its duplicate brand/clock header, preserve category registry and native preference model.
Audit existing PlaybackAgeOverlay and fix timing/lifecycle deviations with focused regressions.

## Checks and delivery
Validate Java/build, relevant behavior tests, complete suite/lint where environment permits, software renders at 720p/1080p/4K and 150% text. Reconcile every traceability row with concrete routes/tests before claiming parity.
Release history determines next version: current 0.146/code146, so 0.147/code147 is next unused development version. Preserve historical entries.
No main merge, public release, signing protection edits or direct signing. Protected signing and Drive APK delivery follow only successful exact-source gates and appropriate authorization. No device/account credentials are supplied here; physical Shield, NAS, USB and live OAuth remain pending.
