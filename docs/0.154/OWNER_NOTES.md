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

## Already queued for 0.154 (decided earlier)
- arm64-only APK; design artwork out of the APK; video engines stored permanently (expire 27 Jan 2027).
- NOVA video-engine update (HDR / Dolby Vision) with the 0.154 native work; NOVA subtitle box-padding setting
  (needs a place in the Subtitles tab).
- Codex's combined run (indentation, @Override, torrent leftovers, NOVA 6.6.3 app port, bug hunt, dead-code map).
