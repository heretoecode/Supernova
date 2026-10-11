# Supernova 0.137 QA candidate

Development/QA only. Not merged or a public release. Based on PR #10; isolated implementation is draft PR #12.

## Functional changes

- Settings no longer requests null On/Off switch text while laying out Integrations. All 12 real categories remain present, with Advanced Diagnostics and export reachable in the targeted regression.
- Native importer uses row-ID paging instead of revisiting excluded rows at offset zero. The regression advances past 2,500 excluded records to the included-drive page and preserves records. Quiet importer status reports lack of recent progress without falsely declaring completion or failure.
- Viewing-history commits invalidate the snapshot and notify Home after persistence. Existing resume/completion rules, Home deduplication and Show/Hidden preferences are retained. Continue Watching is removed from built-in library-page insertion.
- File browser remote traversal uses RecyclerView focus for offscreen items. All Files controls supported-file/hidden-file/hidden-folder filtering, visibly reports On/Off, and retains path and nearby focus during listing refresh. A disappearing focused row no longer lets category focus clear the adapter mid-layout; empty results retain their directory and a reachable control.
- Highest supported bounded diagnostics become the single default; the detail selector is removed. Redaction, retention and export are preserved, including an existing explicit logging opt-out.

## First run and interface

- Normal production Home uses UI-only hero and Recently Added skeletons while no confirmed media exists. No fake media IDs, playback, metadata or Continue Watching placeholders. Welcome uses the real icon, persists explicit dismissal and links to the existing Network & Files flow without starting a scan.
- Shared logical layout dimensions, adaptive poster grids and scrollable navigation/toolbars/panes keep remote actions reachable. Information/confirmation dialogs use bounded width, scrolling content and safe initial focus; child dialogs restore their opener.
- Page Name has a centred heading, hint and caret. The shared keyboard starts at T and has Caps/Space/Backspace plus Clear/Done/Cancel. Done commits only the field; Cancel rolls back; enlarged input can scroll safely.
- Wizard Step 1 Up reaches navigation; Review Right reaches Save. Dirty exit defaults to Continue Editing; Save validates and opens the custom page, Discard returns Home. Custom libraries share the actual toolbar/card family, icons and whole-card focus effect, with Edit at the right.
- Three complete hero cards are prepared around the active position, with bounded additional artwork/logo warming. Typography remains stable during a transition, More Info uses the shared darker control, and only tagline text uses the platform casual font. Cached hero artwork retries a cancelled cold request when reused and retains a ready frame.

## Verification and limitations

See PROGRESS.md and the exact-source Actions evidence for actual completed checks. Settings, importer paging and commit notification have automated reproduction/regression evidence; they are not physical Shield fix-complete claims. Final screenshot fidelity, font availability/readability, hero frame timing, real multi-drive scanning, signed upgrade and device playback require the checklist in SHIELD_QA.md.

Existing put.io service configuration/end-to-end authentication remains separate. Publisher/OAuth setup, Details redesign, Playback HUD redesign and other parked features were not started.
