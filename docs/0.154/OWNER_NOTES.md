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

### 12. Network & Files › Local Storage and the file browser (design + behaviour)
- **Seen (photos, 0.153):** drive list shows raw paths (`/storage/D4A7-6CC3`), "External drive:" prefixes, an
  "All Files" row, a near-empty right panel, heading inside the focus outline; browsing shows `file://…%20…`
  addresses, nameless path parts, system folders, an unclear "All Files: Off" filter, plain file names, no
  watched state, no Play action; two-panel desktop-style layout; a spinner and an emptied list on every folder
  change (`UniversalFileBrowser.reload()` clears first, never remembers a folder, always shows the spinner).
- **Decision (owner, 2026-11-03), final mock-ups in `docs/design/proposals/local-storage/`:**
  - **One full-width list, no details strip** (16–17). Grid button switches to a poster grid.
  - **Header** (16): small clickable **path chips** (drive icon first; Up from the list focuses them, OK jumps
    to that level, Back still goes up one); the folder name as a large **title** with a folder tile; **status
    pills** — item count, total size, library marker (● In your library / Excluded), watched · in progress; at a
    drive's top level, free space. A divider separates header and list. Grid / Sort / Videos only on the right.
  - **Two-line rows** (14–16): line 1 the **matched title** ("Show · S1 E2" for episodes), or the file name without
    extension until a match is found (owner, 3 Nov 2026, same rule as Network Shares); line 2 (soft grey)
    duration · resolution · audio · size, plus "Watched" or **"Resume at 19:42 (40%)"** in blue. Right edge shows
    only the green ✓ for watched — **no mini progress bar**. Unmatched files show technical facts only.
  - **Inline row buttons** on the focused row (Resume/Play · Details · Exclude/Add · Save). OK on the row plays /
    opens; Right moves onto the buttons (white box); Left/Back returns to the row (white outline).
  - **Menu button / long-press** keeps the same actions as a pop-up (hidden shortcut; Claude's recommendation,
    not objected to).
  - Drive list: friendly names ("Backup #2 · USB drive · Micron"), free-space bars, no raw paths.
  - **"All Files" removed** as a place; filter renamed **"Videos only / All files"**; system folders hidden unless
    "All files".
  - **Loading** (18–21): header switches instantly; no loading indicator if the folder lists within ~¼ s;
    otherwise placeholder rows in the final two-line shape with "Loading…" in the pills and focus on the first
    row (carried to the first real item); file names first, second lines fade in from the library lookup with no
    layout movement; **folders visited this session open instantly from memory** with focus on the item you
    left, then refresh quietly; after a few seconds "Still waiting for <server>… (8 s)" with **Cancel** focused;
    on timeout/failure an amber "Not reachable" pill and the reason, a hint and **Retry** (focused) · Go back in
    the list area.
- **Superseded mock-ups:** 01–13 (two-panel, strip, inline-strip variants) kept for history only.
- **Status:** approved.

### 13. Approved 2026-11-03 for Codex (brief `docs/codex/BRIEF_0154.md`)
- **DP-01…DP-05** (Codex's second bug hunt, verified by Claude) — fix, each with a test. DP-05: on a switch row,
  **Left = Off, Right = On**, OK still flips it.
- **TV-only removal** — approved; done as six stages, one full build each, merged stage by stage after Claude
  verifies them.
- **Subtitle box padding** — approved; it is part of NOVA's engine update, so it comes with the **engine update
  (planned as 0.155)**, together with the MediaLib fix, the three HDR app commits and permanent storage of the
  rebuilt engine. Codex prepares the plan first.

### 14. Network & Files › Network Shares (design, for the version after 0.154)
- **Seen (photos, 0.153):** heading "Network Locations" (tile says Network Shares); Films and TV shown as
  "WebDAV · HTTPS · Library source · Status not checked" (never checked); Put.io shown twice (WebDAV shares and
  a separate account with "Needs review"); duplicate actions (Add Network Source, Discover Devices, Network
  Scanning); Discover lists Sonos speakers and a printer; browsing shows hostnames and raw release names.
- **Decision (owner, 3 Nov 2026), mock-ups `docs/design/proposals/network-shares/01–04`:** Put.io is **one service
  row** (account, storage bar, real reachability and library-sync status; buttons Sync now · Search · Account) with
  **Films** and **TV** as its folders (WebDAV underneath, never named); an **Other shares** section for a future NAS
  or PC; **Add share** top right; no duplicate actions; browsing reuses the new Local Storage page; **matched title
  first** on both pages (file name only until matched); Put.io unreachable is said once, on the service row, with
  Retry, folders dimmed. Discover Devices moves under Add Connection, filtered to video sources; Put.io account
  settings move to Saved Connections.
- **Status:** approved; built after 0.154.

### 15. Network & Files › Add Connection (design, for the version after 0.154)
- **Seen (photos, 0.153):** bare protocol list (SMB, WebDAV HTTPS/HTTP, SFTP, FTP, FTP TLS); one long form for every
  type whose fields look like menu rows; blank Port; Connect as a full-width row; no way to test before saving.
- **Decision (owner, 3 Nov 2026), mock-ups `docs/design/proposals/add-connection/01–04`:** types in plain English
  grouped by where files live (Put.io recommended/"Already connected"; A computer or NAS with Find on my network ·
  Enter address — Discover filtered to video sources; A WebDAV server; An SFTP server; More types = FTP, FTP TLS,
  plain WebDAV); real fields (label + value) with Name, defaults shown, optional Folder, password eye, Remember
  password; **Test connection** and **Save and browse** buttons; help on the right; plain-English success
  ("2 folders, 21 videos found") and failure (wrong field outlined amber, nothing saved). Save and browse opens the
  Local Storage-style page to add folders to the library.
- **Status:** approved; built after 0.154.

## Already queued for 0.154 (decided earlier)
- arm64-only APK; design artwork out of the APK; video engines stored permanently (expire 27 Jan 2027).
- NOVA video-engine update (HDR / Dolby Vision) with the 0.154 native work; NOVA subtitle box-padding setting
  (needs a place in the Subtitles tab).
- Codex's combined run (indentation, @Override, torrent leftovers, NOVA 6.6.3 app port, bug hunt, dead-code map).
