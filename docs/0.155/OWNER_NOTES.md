# 0.155 — owner notes from the 0.154 Shield check (4 Nov 2026)

Also queued for 0.155: `docs/0.154/OWNER_NOTES.md` items 13–17 (Network & Files redesign, stuck-scan fix) and
Codex's branch `codex/0155-code-2026-11-04` (DP-01…DP-07, TV-only stages). **Nothing is coded until the owner says so.**

## Confirmed fixed in 0.154 (owner)
- Hero More Info icon dark on focus; no age rating or score on hero cards; hero readability improved; focused
  posters have one crisp outline; Playback Settings has no title and no row lines.

## 1. Hero text readability on some artwork (design)
- **Seen (owner photos, 4 Nov 2026):** Kingdom — year · genre and synopsis on a bright yellow/pink sky, hard to read;
  the synopsis runs across the brightest area. Mayday — readable but weak on a busy crowd. Mr. Robot (flat red) fine.
- **Proposed (Claude):** a soft, vignette-like dark patch behind the text block only (a little stronger than the
  current left fade, artwork elsewhere untouched); year · genre line in Medium weight, pure white, firmer shadow;
  synopsis width limited to the left half of the card.
- **Status:** approved by the owner on 4 Nov 2026 as mocked in `docs/design/proposals/0155-checks/` — awaiting go-ahead to code.

## 2. Hero title logo placement (design)
- **Seen:** Kingdom and Mr. Robot — wide logos sit at the very top of the card with a large gap above the metadata
  row; Mayday sits close to its metadata.
- **Cause (likely):** the logo box has a fixed height and wide logos scale to its width, drawing at the top.
- **Want:** the logo sits just above the metadata whatever its shape (bottom-aligned in its box, consistent gap).
- **Status:** approved by the owner on 4 Nov 2026 as mocked in `docs/design/proposals/0155-checks/` — awaiting go-ahead to code.

## 3. Playback Settings: Audio and Playback tab icons invisible when focused (bug)
- **Seen:** Subtitles tab focused → dark icon; Audio or Playback focused → icon barely visible (white on white).
- **Status:** agreed bug.

## 4. Playback Settings: live subtitle preview (design)
- **Want:** while changing subtitle appearance (size, colour, outline, background, opacity, position), show sample
  text on the video so changes are visible live, whether or not a subtitle is currently on screen.
- **Status:** approved by the owner on 4 Nov 2026 as mocked in `docs/design/proposals/0155-checks/` — awaiting go-ahead to code.

## 5. Download Subtitles: no pop-up in the middle of the screen (design)
- **Seen:** "Download Subtitles · Searching OpenSubtitles… · Cancel" appears as a separate dialog centre-screen,
  then the results appear inside the panel as expected.
- **Want:** the searching state happens inside the panel (same page as the results), Back cancels.
- **Status:** approved by the owner on 4 Nov 2026 as mocked in `docs/design/proposals/0155-checks/` — awaiting go-ahead to code.

## 6. Panel colour check
- Owner confirmed 4 Nov 2026: the panel is the correct charcoal on the TV; the blue tint was the phone camera. Closed.

## 7. Playback Settings: colour swatch selection ring is clipped (bug)
- **Seen (photo):** the ring round the chosen colour (green) looks like a circle with flat, square-ish edges.
- **Cause (Claude, from the code):** `PlaybackSubtitlePanel.Swatch` draws a 15px-radius dot plus a white ring at
  radius 18.5px with a 3px stroke (outer edge ≈ 20px) inside a 38×38px view (half-width 19px), so the view clips the
  ring on four sides.
- **Want:** a complete, clean ring with a small dark gap between the colour and the ring; the swatch view sized to
  fit it. Row focus (white outline) and "chosen colour" (ring) stay visibly different.
- **Status:** agreed bug; fix approved as mocked (`0155-checks/04`) — awaiting go-ahead to code.

## 8. Playback HUD: buttons, time bubble and seek bar (design)
- **Seen (owner photos):** play/pause and Settings are solid black boxes that look wrong on the picture; the blue time
  bubble is small and sits on the seek dot; seek bar colours (white played, light blue remaining) read oddly.
- **Decision (owner, 4 Nov 2026), mock-up `0155-checks/07` (vs `06` today):** HUD buttons in charcoal glass with a
  faint edge (white box on focus unchanged); larger charcoal time bubble (time large, chapter smaller) raised clearly
  above the dot; seek bar white played / light grey buffered / dim grey remaining, slightly thicker, chapter marks as
  gaps, larger dot with a soft halo when focused.
- **Status:** approved — awaiting go-ahead to code.
