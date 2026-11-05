# Next version — prominent Scan Library action in Network & Files

**Priority:** High usability improvement. **Status:** User requested; implementation pending. **Target:** next version after 0.143.

## Observation
User-supplied Shield screenshot of Network & Files > Overview shows no obvious direct Scan Library action. User wants a clearly visible button to initiate a scan immediately.

## Requested UI
- Add a prominent, D-pad-focusable **Scan Library** button in the Overview header/top-right of the main content pane, without hiding it in a submenu.
- Pressing OK should trigger the **existing** library scan pipeline directly; no extra confirmation for ordinary scanning.
- Show Scanning…/progress and a clear completed/failed outcome; handle rapid presses without duplicate concurrent jobs.
- Maintain consistent focus, typography, contrast and layout at 720p/1080p/4K and large text settings.
- Respect current library/source scope and existing scan configuration. Do not imply a successful scan when individual sources failed.

## Related safety and QA
- Coordinate with `docs/design/NEXT_VERSION_WEBDAV_STALE_LIBRARY_FIX.md`: reconcile confirmed missing WebDAV files only after a complete successful listing; never prune records on network outage, timeout, partial scan, auth failure or scan errors.
- Verify button is discoverable and usable with Shield remote, works from Overview, reports meaningful state, and does not freeze navigation.
- Add automated tests and physical Shield QA when available. Do not claim implementation or verification merely because this document exists.

## Release workflow
Include in next-version Codex handover and changelog once implemented. No app code or release branch changed by this documentation commit.
