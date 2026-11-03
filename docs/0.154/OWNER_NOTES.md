# 0.154 — owner notes from the 0.153 walk-through

Collected while Codex works. **Nothing here is approved for coding until the owner says so.**
Each item: what the owner saw → what they want → status.

## Home

### 1. Focused hero button: icon nearly invisible (bug)
- **Seen (photo, 0.153):** Home hero "More Info" focused: white box, ⓘ icon almost invisible.
- **Cause (likely):** the icon stays white on the white focus box — the label turns dark but the icon does not
  follow the focus state on the hero button. Same family as the HUD Settings label fixed in 0.153.
- **Want:** icon black when focused, white at rest, like every other button.
- **Plan:** reproduce with a focused hero render; fix; extend `SupernovaStyleTest` so icons on every button are
  checked, not only text.
- **Status:** agreed bug — awaiting go-ahead to code.

### 2. Focused button text looks grey, not black (design)
- **Seen:** label on the focused button reads as grey.
- **Cause:** colour is near-black (`#0B0D12`), but buttons use the *Light* font weight; thin strokes look grey on a TV.
- **Decision (owner, 2026-11-03):** button labels in **Medium** weight, globally (every button, focused and at
  rest). Mock-up: `docs/design/proposals/button-focus-polish/01-label-weight.png` (option C).
- **Status:** approved design — awaiting go-ahead to code.

### 3. Grey "bleeding" round the focused button (design/bug)
- **Seen (photo, 0.153):** grey smudge around the focused white button, mostly along the bottom edge, on the
  pale hero image.
- **Cause:** the focus box's soft shadow (`SupernovaStyle.Box`, four stacked offset layers, shifted down). On
  light backgrounds it reads as a dirty halo; the layering also shows steps and is lopsided.
- **Decision (owner, 2026-11-03):** **no shadow** on the focus box, globally. Mock-up:
  `docs/design/proposals/button-focus-polish/02-focus-shadow.png` (option 1); combined result in
  `03-hero-today-vs-proposed.png`.
- **Status:** approved design — awaiting go-ahead to code.

### 4. Faint second line round focused artwork cards (design)
- **Seen (photos, 0.153):** Home row, focused Tulsa King card: a faint line just outside the white outline, all
  the way round.
- **Cause:** card focus (`PreviewFocusGlow`, used by `PreviewCardPresenter` and `CinematicCard`, exempt from the
  0.153 button rules) draws the outline plus four 1dp "glow" rings outside it (16% then 5%); on a TV they read as
  a ghost second line. The wider soft blue haze in the photos is most likely the TV's local-dimming bloom, not
  the app (also seen near unfocused cards).
- **Decision (owner, 2026-11-03):** remove the rings; focused cards keep **one crisp white outline** (plus the
  existing enlargement), matching the global white-outline rule.
- **Status:** approved design — awaiting go-ahead to code.

### 5. Home hero: age rating box and text readability (design)
- **Seen (photos, 0.153):** the boxed age rating ("12A") in the hero metadata row is too heavy and missing on
  some titles (Kingdom, War, Mr. Robot); metadata and tagline are hard to read on bright or busy artwork
  (Kingdom, Tulsa King, Mayday).
- **Decision (owner, 2026-11-03):**
  - **Remove the age rating from the hero metadata row.** Keep it on the Details page and in the playback-start
    age overlay. The row starts with the year.
  - **Readability: soft text shadow + gentle darkening** behind the left third of the hero (option 2):
    shadow under the metadata and tagline; a left-to-right shade (~42% → 0 by ~58% of the width) so the right
    side of the artwork is untouched. Mock-ups: `docs/design/proposals/hero-readability/01–04-*.png`
    (bottom-left in each).
- **Follow-up to check while coding:** why some titles had no rating (likely UK certificate missing in TMDb) —
  matters for the Details page and overlay, which keep it.
- **Status:** approved design — awaiting go-ahead to code.

### 6. Home and Details hero cards unified (design)
- **Seen (photos, 0.153, Black Bag on both):** the Details hero repeats what the Details tab shows (boxed age
  rating, boxed FHD / AAC 5.1 badges, 3-line synopsis); its artwork is dimmed as a whole, so the same film looks
  darker than on Home.
- **Decision (owner, 2026-11-03):** both heroes look the same —
  - logo; row **`Year · Genres`** (Details adds runtime for films / seasons for series, next to the year:
    `2025 · 1h 34m · Mystery, Thriller`); **tagline** (fallback: two-line synopsis when there is no tagline);
  - **no score on either card** (mock-ups of text / star / five stars / ring in
    `docs/design/proposals/hero-score/` were reviewed and rejected); scores stay in the Details tab's Reception;
  - no age rating and no format badges on either card (both remain in the Details tab; rating also in the
    playback overlay);
  - same artwork treatment on both: approved text shadow + gentle left darkening (item 5), no whole-card dimming;
  - buttons by job: More Info on Home; Play and More on Details.
- **Status:** approved design — awaiting go-ahead to code.

### 7. Menu and panel background: navy glass → charcoal (design)
- **Seen (photo, 0.153):** owner likes the More menu's size and readability; questioned the navy colour.
- **Decision (owner, 2026-11-03):** **charcoal at 97%** (≈ `#16181C` at 97% opacity, faint white edge ≈ 22%)
  replaces the navy glass, **globally** — every pop-up menu, Playback Settings, Edit Page, pickers and dialogs
  (all share `SupernovaStyle.GLASS`). Group headings and their lines turn from light blue to soft grey
  (≈ `#B7BFCC`, line ≈ 22% white). Mock-up: `docs/design/proposals/menu-black/01-menu-background.png`
  (right-hand option). The 72% button black was rejected for menus (text behind shows through).
- **Knock-ons to settle when coding:** buttons/tabs inside panels (`PANEL_REST`, navy today) need a lighter
  charcoal step so they still show on the charcoal panel; Settings tiles and source cards rest on the same
  glass colour today — owner to confirm whether they turn charcoal too.
- **Status:** approved design — awaiting go-ahead to code (tile question open).

### 8. Playback Settings (and all side panels) follow the More-menu structure (design)
- **Seen (photo, 0.153):** panel "very blue"; tabs look like pills; focused row (Text Colour) outline too curvy
  and too tight round the swatches.
- **Cause:** same 24 px (12dp) corner as everywhere, but on shorter boxes (56 px tabs, 60 px rows vs the More
  menu's 74 px rows), so it looks curvier and pinches the content.
- **Decision (owner, 2026-11-03):** approved as mocked in
  `docs/design/proposals/playback-panel-charcoal/01-panel-today-vs-proposed.png` (right):
  - charcoal glass (item 7); **no "Playback Settings" title**;
  - tabs 72 px tall (button proportions; selected = thin white outline, focused = white box);
  - rows 74 px, same as the More menu, with the same breathing room; **no divider lines between rows**;
  - section headings in the More-menu style (sentence case, soft grey, line alongside);
  - applies to all three tabs and to the Edit Page side panel, so every side panel matches the menus.
  - Fits without scrolling (tallest tab ends ≈ 75 px above the screen edge).
- **Status:** approved design — awaiting go-ahead to code.

### 9. Settings and Network & Files landing pages unified (design + 2 bugs)
- **Seen (photos, 0.153):** the two landing pages use different tile styles; Network & Files keeps its page
  title, unequal columns, icons above titles, smaller text. **Bugs:** focused "Add Connection" and focused
  "Playback & Video" tiles show near-invisible text (white on the white focus box).
- **Proposed:** one tile design for both — no page title; tiles fill the page in equal columns (Settings 4×2,
  Network & Files 3×2, same height); icon left of a single-line title, description below (Settings 0.153 sizes);
  white focus box with dark text; **charcoal** tiles at rest (owner leaning charcoal). Network & Files wording:
  Local Storage · Network Shares · Add Connection · Saved Connections · Library Scanning · Advanced (descriptions
  in the mock-up). Mock-ups: `docs/design/proposals/landing-tiles/`.
- **Decision (owner, 2026-11-03):** approved as mocked, charcoal tiles, with the new Network & Files wording.
- **Status:** approved design — awaiting go-ahead to code. The two bugs are fixed by the redesign and covered by
  extending the white-on-white test to tiles.

### 10. Supernova is TV-only (product decision)
- **Decision (owner, 2026-11-03):** Supernova is an Android TV app only. NOVA's phone/tablet interface (routed to
  by `EntryActivity` when not in leanback mode) and the legacy classic-UI paths are therefore removable.
- **Plan:** extend Codex's dead-code map (`docs/codex/findings/dead-code-map.md`) with the TV-only assumption,
  then remove in safe stages (each proven by the full build + tests), with the owner approving each stage.
  The manifest must keep declaring TV-only (leanback required, touchscreen not required).
- **Status:** decided — removal plan to be drafted.

### 11. Five confirmed bugs from Codex's bug hunt (fix in 0.154, each with a test)
Verified by Claude against the code on 2026-11-03 (`docs/codex/findings/`); approved for fixing by the owner.
- **U1** `utils/SubtitlesWizardCommon.java:~295` — cache folder built as a `file:///` URI string, then used as a
  plain path, so the cached copy of a renamed subtitle is never renamed.
- **N1** `leanback/network/ftp/FtpServerCredentialsDialog.java:~172` — an unparseable port shows "invalid port"
  but the connection proceeds on the default port; it must stop instead.
- **N2** same dialog `~210` — with debug logging on, usernames and server/folder paths are logged (no passwords);
  redact.
- **L1** `leanback/search/VideoSearchProvider.java:85` — exported search provider dereferences a null first
  selection argument; add the guard.
- **P1** `player/PlayerService.java:~1733` — a pending skip prompt stays actionable ≤5 s after skipping is
  switched off; recheck the master setting.
- U2 and L2 are in phone-only screens and disappear with the TV-only removal (item 10).
- **Status:** approved — awaiting go-ahead to code.

### 12. Network & Files › Local Storage and the file browser (design)
- **Seen (photos, 0.153):** drive list shows raw paths (`/storage/D4A7-6CC3`), "External drive:" prefixes, an
  "All Files" row, a near-empty right panel, heading inside the focus outline; browsing shows `file://…%20…`
  addresses, nameless path parts, system folders, an unclear "All Files: Off" filter, plain file names, no
  watched state, no Play action; two-panel desktop-style layout.
- **Decision (owner, 2026-11-03):**
  - **Layout C:** one full-width list + a **details strip** at the bottom for the focused item; **OK** does the
    obvious thing (open folder / play video); the **menu button / long-press opens the approved pop-up menu**
    (Play, Play from beginning, Details, Exclude/Add to Library, Save Location). **Grid** button switches to a
    poster grid (layout B). Mock-ups: `docs/design/proposals/local-storage/04–06-*.png` (C = 06).
  - Drive list: friendly names ("Backup #2 · USB drive · Micron"), free-space bars, no raw paths, no "All Files".
  - **"All Files" removed** as a place (its contents live under Local Storage and Network Shares).
  - Filter renamed **"Showing: Videos only / All files"**; system folders hidden unless "All files".
  - Video rows: name without extension, duration · resolution, watched ✓ / progress bar; strip shows what it
    is matched to, poster, details and library status.
- **Proposed, awaiting approval:** clickable **path bar** (Up from the toolbar; OK jumps to that level; Back
  still goes up one) and **library markers** — "Library folder" tag on folders you added, excluded items dimmed
  with an "Excluded" tag, inherited items untagged, the strip states the reason ("Excluded by you · inside the
  TV library folder"). Mock-ups: `07–09-*.png`. List rows use the approved white-outline row focus.
- **Status:** layout approved; path bar and markers awaiting approval.

## Already queued for 0.154 (decided earlier)
- arm64-only APK; design artwork out of the APK; video engines stored permanently (expire 27 Jan 2027).
- NOVA video-engine update (HDR / Dolby Vision) with the 0.154 native work; NOVA subtitle box-padding setting
  (needs a place in the Subtitles tab).
- Codex's combined run (indentation, @Override, torrent leftovers, NOVA 6.6.3 app port, bug hunt, dead-code map).
