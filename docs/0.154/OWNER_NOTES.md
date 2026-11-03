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

## Already queued for 0.154 (decided earlier)
- arm64-only APK; design artwork out of the APK; video engines stored permanently (expire 27 Jan 2027).
- NOVA video-engine update (HDR / Dolby Vision) with the 0.154 native work; NOVA subtitle box-padding setting
  (needs a place in the Subtitles tab).
- Codex's combined run (indentation, @Override, torrent leftovers, NOVA 6.6.3 app port, bug hunt, dead-code map).
