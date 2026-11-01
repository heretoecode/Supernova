# SUPERNOVA — SUBTITLES UI IMPLEMENTATION BLUEPRINT (v2, consolidated)
**Status:** Approved visual design and Codex-ready specification. **NOT an instruction to begin implementation immediately.** Schedule as a separate task only after explicit owner authorisation. Existing active Codex fixes are outside scope.
**Target:** Android TV / NVIDIA Shield, D-pad only; compare at 1920×1080.

## 0. Authoritative images and precedence
- **IMAGE B — approved main Playback Settings > Subtitles screen:** [AA59180F-F8E0-4697-9423-78621273D28B.png](https://github.com/heretoecode/Supernova/blob/main/docs/design/approved/AA59180F-F8E0-4697-9423-78621273D28B.png). **This is authoritative for the main panel.** One outer panel border; no surrounding inner boxes for either section; retain section headings and horizontal row separators.
- **IMAGE A — approved reference for two pop-ups ONLY:** [A31E82C8-CC81-4E6C-AC1D-6AE60370E152.png](https://github.com/heretoecode/Supernova/blob/main/docs/design/approved/A31E82C8-CC81-4E6C-AC1D-6AE60370E152.png). Use the **Subtitle Track** and **Download Subtitles** pop-ups, which appear stacked in this one composite solely for illustration. They must be independent views in the app. **Ignore the superseded main-window design in Image A.**
Both files have been confirmed present in GitHub. User identified and approved their roles. No third image is needed. Image B wins all main-window visual conflicts; Image A wins pop-up details.

## 1. Codex rules and scope
1. **Only** redesign the presentation of the Subtitles tab and its two existing pop-up flows. Preserve Audio/Video/More tab appearance and behaviour, surrounding playback HUD, playback engine, subtitle providers and unrelated app areas.
2. Audit the current implementation, components, styles, settings/preferences and focus behaviour first. Reuse matching theme tokens, focus/row components, dialog logic and vector icons; document what was reused or introduced.
3. Do not creatively reinterpret, simplify or 'improve' the approved design. When image and approximate measurements disagree, image takes precedence; document unavoidable deviations.
4. Preserve existing stored preference keys/values, subtitle provider and playback behaviour, Back-key semantics and navigation. Do not introduce new search/download backends.
5. The measurements and colours below are **estimated from generated mock-ups**, not extracted from source. Target approximately ±4% where feasible; convert proportions into responsive dp/sp using panel-relative scale and TV density, not hard-coded image pixels.
6. No credentials, tokens, subtitle queries, filenames or user file paths in diagnostics without separate authorisation.
7. Do not claim QA success based on compilation alone. Supply actual screenshot evidence and checklist; mark physical Shield testing pending if not performed.

## 2. Geometry, scale and safe area
- Image B reference canvas **1448×1086 px**. Estimated main panel x=191–1258, y=35–980, width **1067 px**, height **945 px**. Target panel scale = actual rendered panel width / 1067. Respect ~5% TV overscan safe area and prevent clipping; adapt height/scrolling to 1920×1080.
- Image A reference canvas width **1188 px**; each pop-up estimated **1115 px** wide. A1 estimated 1115×666; A2 1115×765. Scale each independently from actual panel width / 1115. **Do not stack A1 and A2 in app.**
- Earlier discussions mentioned ~40% TV width but user subsequently approved a **wider** image B panel. Therefore image B proportions and legible controls take precedence over the superseded 40% target. Do not crop rows to force an arbitrary width.
- Keep main title and tab bar accessible while navigating scrollable content; accommodate TV safe areas and screen sizes.

## 3. Approximate visual tokens
Reuse existing matching theme tokens first; otherwise approximate:
| Token | Target |
|---|---|
| Panel background | #050B14, ~96% opacity |
| Outer border | #4F8FCB, 2–3 image px, subtle 12 px blur glow at ~35% |
| Primary / secondary text | #FFFFFF / #C9D6E6 |
| Section heading | #9CC9F0, uppercase, ~2% tracking |
| Selected text | #00A8FF |
| On-toggle track / thumb | #0A6CFF / #FFFFFF |
| Row divider | #2A4A6B at ~70%, 1–2 image px |
| Focus fill | #0B2A4A at ~80% |
| Focus border/glow | #2FB6FF, ~3 image px, soft 16 px glow ~60% |
| Scrollbar | #6E7F90 at ~70%, ~6 px rounded thumb; invisible track |
Backdrop is existing dimmed/blurred playing video, not a required stock photo. No additional HUD controls. Use **existing Supernova font**, never introduce a new font. Estimated reference sizes: title ~40 px B/~44 px A, tabs ~28, section headings ~26, row labels/values ~28, download filename ~32, download metadata ~25. Main outer corner radius ~36 px reference; tab pill ~18, focus row ~22, search ~22. Blue on-toggle reference ~82×44 with ~36 white thumb at right.

## 4. IMAGE B — main Subtitles screen
- Header reference y=35–120: outline gear ~52 px, title **Playback Settings** bold ~40 px.
- Tab bar reference y=135–202, ~67 px high: **Subtitles**, **Audio**, **Video**, **More** left-to-right, each with monochrome icon immediately left of label (CC, speaker, screen/play, sliders). Thin blue-grey outline; selected Subtitles tab dark-blue fill, cyan focus border and glow. Approx tab gaps 16 px; icon ~38 px, label ~28 px, internal gap ~14 px.
- Main content reference y=225–944; inner horizontal padding ~44 px; icons in consistent left column, labels next, controls right-aligned. Rows ~60–62 reference px with thin horizontal separators; increase responsive row height if needed for TV focus/legibility.
- Section heading **SUBTITLE SELECTION** light blue uppercase; **SUBTITLE APPEARANCE** likewise. **No boxes around these groups**.

| Order | Icon | Label | Control |
|---|---|---|---|
| 1 | CC | Subtitles | Direct ON/OFF blue switch |
| 2 | list | Subtitle Track | Country/region flag where known + current track name, e.g. English (SDH), chevron |
| 3 | download | Download Subtitles | Find & Download, chevron |
| 4 | Aa | Text Size | Small \| **Medium** \| Large |
| 5 | palette | Text Colour | Five circular colour swatches |
| 6 | vertical arrows | Vertical Position | Bottom \| **Middle** \| Top |
| 7 | outlined/dashed A | Outline | Direct ON/OFF switch |
| 8 | hatched square | Background | Direct ON/OFF switch |
| 9 | droplet | Background Opacity | 0% \| 25% \| **50%** \| 75% \| 100% |
| 10 | stopwatch | Subtitle Delay | − [signed milliseconds, e.g. 0 ms] + |

- Inline text choices: no pill/button backgrounds; slim vertical separators (~1.5 image px, #8FA4BA, ~24 px tall, ~22 px side spacing). White unselected; blue medium-weight selected.
- Text Colour swatches: **white** (example selected, white outer ring and gap), yellow #FFC93C, cyan #19E6E6, green #4CC554, pink #F06A9B; ~40 px circles, ~26 px gaps. Palette is an **image-based target**; audit existing supported colours and report any missing mapping before altering data model.
- Delay glyphs ~30 px with offset centred. **Agreed design step: ±100 ms per activation.** If existing engine uses another increment or persistence unit, flag the difference and preserve compatibility; do not silently change behaviour or stored units.
- Values shown (ON, English SDH, Medium, Middle, 50%, 0 ms) are **illustrative**, not required defaults. Preserve real preferences.
- One focused element at a time. Image B shows the Subtitles tab focused. For rows, use full-width rounded focus fill + cyan border/glow, unless a specific existing navigation model requires a nested control focus. For swatches, visible ring; for inline options, outline control group.
- Keep a divider below the final row, with bottom padding.

## 5. IMAGE A1 — Subtitle Track pop-up
- Independent floating panel, approximately 1115×666 image px; same navy background, blue outer border and radius.
- Back chevron then **Subtitle Track**, bold ~44 reference px.
- List rows approx 82 image px high, separated by fine lines except focused row. Each: outlined CC icon (~62×44), regional flag (~66×46), language (~34), far-right white tick for selected track.
- Mock-up example rows: English (SDH) selected, English, Spanish (Español), French (Français), German (Deutsch), Italian (Italiano). **Sample only:** real list is from actual player subtitle tracks; do not fabricate choices.
- First row shown focused: dark-blue full-row fill, cyan ~3 px border/glow, white text and selected tick. Thin rounded scrollbar only when list overflows; scroll focused row fully into view.
- Flags are region-specific only where metadata supports that mapping; **do not map language to an arbitrary country**. If no known region, use neutral globe/language icon. Use existing resources or suitable vector drawables, not emoji flags.

## 6. IMAGE A2 — Download Subtitles pop-up
- Independent floating panel approx 1115×765 image px, same title/back/panel styling as A1.
- Search field ~90 image px high, rounded ~22, thin #8FA4BA outline, magnifier, placeholder **Search for subtitles...**. Existing provider/query behaviour only.
- Result rows approx 113 image px high. Each: CC icon, region flag or fallback, filename single-line white semibold (~32), metadata line (~25) such as **English (SDH) · 1.2 MB**, far-right download icon. Long filenames truncate with end ellipsis; do not wrap.
- Sample entries English SDH 1.2 MB, English 1.1 MB, Spanish 1.0 MB, French 1.1 MB are illustrative, **not fixed data**.
- First result shown focused, full-row dark blue fill, cyan outline/glow. Fine separators on non-focused rows; rounded thumb when scrolling required.
- Do not log query strings, subtitle filenames or paths in diagnostics.

## 7. D-pad and behaviour
- B: LEFT/RIGHT across tabs; DOWN into first content row; UP returns to active tab. Within inline choice controls, LEFT/RIGHT operates per existing focus semantics while making every choice operable. OK toggles direct switches.
- B Subtitle Track OK → A1; Download Subtitles OK → A2. Back chevron or remote BACK returns to B with focus restored to invoking row.
- A1 UP/DOWN navigates and scrolls; OK selects track and respects existing stay/close behaviour. Selected tick reflects real track.
- A2 UP/DOWN navigates and scrolls; RIGHT may focus download action if current implementation uses it, otherwise whole row action. OK initiates existing provider download.
- Only one visible focus target; never focus divider/header; no lost focus/traps. BACK closes one level.
- Existing preference keys and values, playback behaviour and subtitle provider must remain compatible. This is a presentation redesign, except where user separately approves a functional discrepancy.

## 8. Assets
Reuse existing icons (gear, CC, audio, video, sliders, list, download, palette, Aa, vertical arrows, outlined A, hatched square, droplet, stopwatch, chevrons, check, search). For missing assets, create matching thin-stroke white Android vector drawables with rounded caps. Use regional flag assets only when track/result metadata identifies a region; otherwise neutral fallback. Never block list rendering on missing artwork.

## 9. Verification and acceptance
Codex deliverables: (1) files changed and existing/new components; (2) source audit and any unresolved compatibility differences; (3) **before/after screenshots** of B, A1 and A2 on Shield or 1920×1080 emulator, plus side-by-side visual comparisons with exact approved image references; (4) tested D-pad focus, scrolling, Back restoration, toggles, track choice, search/download, all inline choices, delay step and persisted settings; (5) deviation list with justification. If screenshots or Shield tests cannot be performed, state **NOT VERIFIED** — do not claim visual parity.

### Acceptance checklist
- [ ] One dark floating outer panel, steel-blue border/soft glow, rounded corners; **no inner section boxes**.
- [ ] B header, four tabs and left-hand monochrome icons match approved main reference.
- [ ] B two light-blue section headings, all ten rows in specified order and fine separators.
- [ ] Inline choices separated by vertical bars, selected text blue, **no pill backgrounds**.
- [ ] Five swatches, selected ring, real settings mapping checked.
- [ ] Direct blue toggles, white thumb on right when ON.
- [ ] A1 header, real track list, CC/region/fallback, selected tick, focus and scrolling.
- [ ] A2 header, search, real results, two-line text, download action, ellipsis and scrolling.
- [ ] Focus visible, no traps, Back returns one level and restores invoking-row focus.
- [ ] Existing preferences, subtitle engine, provider and playback preserved.
- [ ] Screenshot comparisons and deviations documented for all three screens.
- [ ] No credentials, tokens, user paths or search/file metadata added to diagnostics.
- [ ] Audio/Video/More and HUD remain unchanged.
- [ ] Physical Shield QA explicitly marked complete or pending, truthfully.

## Release/implementation gate
This document is **ready to hand to Codex when authorised**, but is not an instruction to modify active development branches or begin this work now. Use a separate authorised work item, preserve unrelated in-flight work and do not create or publish an APK without separate instruction.
