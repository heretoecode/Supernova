# Supernova 0.152 — owner-approved designs (2 November 2026)

**Status:** implemented and committed on `claude/supernova-github-access-yfhe89`, built on the delivered 0.151 source (`dfa3fafd`). Automated build, tests and signing run in GitHub Actions; the owner approves each signing. **Physical Shield verification pending — nothing here is accepted until the owner confirms it on the Shield.**

## Changes (each traced to an owner report or an approved design)

| # | Owner report / design | Change |
|---|---|---|
| 1 | Playback Settings panel too large; approved side-panel design | One side panel, top-right, a third of the screen wide, height fitted to its rows, no dimming. Three tabs — **Subtitles, Audio, Playback** — and the Settings button always opens on Subtitles. Track rows are steppers (Left/Right); OK opens the full track list **inside the panel**. Background Opacity is a stepper, dimmed while Background is off. Audio Delay, Playback Speed, Format, 3D Mode, Play Mode, Picture in Picture, Audio Boost, Night Mode, **Spatial Audio** and **Skip Intros & Credits** are inline. Download Subtitles results use the same side geometry and no longer show "Size unavailable". ([design](../design/PLAYBACK_SETTINGS_SIDE_PANEL_APPROVED_2026-11-02.md)) |
| 2 | Edit Library "needs some work"; approved Edit Page design | The four-section editor and its Live Preview are replaced by a side panel over the **real page**, which updates as you change settings. The tab name follows the draft; a new page shows "Choose what to show…" until set up. Genres, Original Language and Studio / Network open as lists inside the panel. Save at the top; Delete at the bottom (saved pages only). Back with changes: Keep Editing / Save / Discard. Features and stored format unchanged. ([design](../design/EDIT_PAGE_SIDE_PANEL_APPROVED_2026-11-02.md)) |
| 3 | Missing flags (Welsh, Arabic, Spanish EU/LA, …) | 13 approved flags added (Wales, Scotland, Catalonia, Galicia, Basque Country, Arab League, Esperanto, Sámi, Tatar, Kurdish, Breton, Occitan, Extremadura); OpenSubtitles Spanish (EU) → Spain, Spanish (LA) → Mexico; rare ISO 639-3 codes recognised; UI language names fixed (Kurmanji). |
| 4 | HUD jumps on first show; buttons too circular | Controls start at their final position; play/pause and Settings are rounded rectangles. |
| 5 | Library toolbar not centred | Filters/Sort/View row centred between the statistics and the separator on every library page. |
| 6 | Documentaries: no blur under the top bar | Custom page content blurs and fades under the top bar like Movies/TV. |

## Not changed (deliberately)
- Notifications and Sleep Timer are not in the Playback panel: they do not exist on Android TV / are disabled in source.
- The Download Subtitles search box filters the results already returned by the provider; it is not pre-filled with the title.
- Audio Delay steps by 50 ms; Playback Speed by 0.05× (0.5×–2.0×). A value is remembered across videos only when "Keep setting" was already on.

## Owner Shield checklist
1. Playback → Settings: panel top-right, film visible; opens on **Subtitles**. Left/Right on the tab row switches tabs.
2. Subtitle Track: Left/Right steps tracks; OK shows the list in the panel; Back returns. Same for Audio Track.
3. Background off → Background Opacity dimmed and skipped. Subtitle/Audio Delay − / +.
4. Playback tab: Format, 3D (3D files only), Picture in Picture → Start, Speed, Play Mode, Skip Intros & Credits.
5. Download Subtitles: results in the side panel; no "Size unavailable".
6. Documentaries → Edit Library: panel over the page; change Includes / Watched / Sort and watch the page change behind it; rename and watch the tab; Back → Keep Editing / Save / Discard.
7. "+" (new page): tab shows "New Page •", page says "Choose what to show…", Save Page creates it.
8. Flags: Welsh, Scottish Gaelic, Catalan, Arabic, Spanish (EU) / (LA) tracks and Settings pickers.
9. HUD: no jump on first show; rounded-rectangle buttons. Library toolbar centred on all library pages.
