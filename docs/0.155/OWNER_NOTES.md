# 0.155 — owner notes from the 0.154 Shield check (4 Nov 2026)

> **Release order (owner, 4 Nov 2026):** the engine update ships **first, on its own**, as the next version
> (**0.155**). Everything in these notes, plus the stuck-scan fix, the Network & Files redesign and Codex's
> non-engine work, follows in the version after it (**0.156**). Existing file names keep their working labels:
> Codex's `engine-0156-*`, `*supernova156*` and `supernova-0156-*` files are the engine release; this folder
> (`docs/0.155/`) holds the app/UI release notes.

Also queued for 0.155: `docs/0.154/OWNER_NOTES.md` items 13–17 (Network & Files redesign, stuck-scan fix) and
Codex's branch `codex/0155-code-2026-11-04` (DP-01…DP-07, TV-only stages). **Nothing is coded until the owner says so.**

> **How mock-ups are shown (owner, 4 Nov 2026):** only final mock-ups the owner needs to judge, sent once, as one
> numbered group per question. Claude checks renders without opening them in the chat (no draft or check images).

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

## 9. Details: flat blue card before the artwork arrives (bug)
- **Seen (owner photos, Marshals):** Home hero shows the backdrop; pressing through to Details shows the logo and
  text on a flat navy card for a moment, then the backdrop pops in.
- **Cause (Claude, from the code):**
  - The Details card (`CinematicCard`) has a solid navy background (`0xff07131f`), and
    `PreviewMoviePage.cardArtwork()` loads with `noPlaceholder().noFade()`, so the navy shows until Picasso finishes.
  - The Home hero already has that same backdrop decoded in memory, but at `resize(960×540)`; Details asks for
    `fit().centerCrop()` at the card's size. Picasso keys its memory cache by URI *and* size, so it misses and
    decodes again from disk, or downloads again.
  - Details can also switch image part-way: `bind()` starts the hero's backdrop, then `setTags()` (or `bindRemote`
    with the TMDb `w1280` URL) picks a different URI — local file vs remote, or a different default backdrop —
    which restarts the load and the navy shows again.
- **Proposed fix (not coded):**
  1. Hand the hero's on-screen bitmap (or its exact Picasso key) to Details, and show it on the card the moment the
     page opens, before layout finishes.
  2. Load the full-size image behind it and cross-fade it in when ready, instead of a hard swap.
  3. Only change image when the new one is genuinely different, and keep the current one showing while it loads.
  4. Make the empty-card background charcoal (matching the panels), not navy, for titles with no artwork at all.
  5. Test: a Robolectric test that Details opened with a seeded bitmap shows a drawable straight away, and keeps a
     drawable throughout a later URI change.
- **Status:** approved by the owner on 4 Nov 2026 (no mock-up needed) — awaiting go-ahead to code.

## 10. Auto-next / next episode can play a hidden file (bug, Codex DP-09)
- **Found by Codex** (`docs/codex/findings/deep-hunt-1-player.md`, DP-09, on `codex/0155-code-2026-11-04`):
  `player/UpdateNextTask.java` picks the next file in a folder (auto-next/repeat) and the fallback next episode
  without the user-hidden filter (`Archos_hideFile=0`), so a hidden video can start playing by itself. The playlist
  query in the same file already filters correctly.
- **Decision (owner, 4 Nov 2026):** hidden files are skipped by auto-next, repeat and next episode. Playing a hidden
  file directly, when chosen on purpose, still works.
- **Tests:** a hidden file between two visible ones is skipped; every remaining file hidden means no next file;
  repeat wrap-around skips hidden files; cached and uncached next-episode paths both skip them.
- **Status:** approved — awaiting go-ahead to code (Claude, not Codex: it changes what plays).

## 11. Local Storage: show library status on every row (design)
- **Seen (owner photo, Backup #1 › NVIDIA_SHIELD › Movies):** only the focused row shows Play / Exclude; the other
  rows give no hint whether they are in the library or excluded. Today an excluded row is only dimmed with
  "Excluded" at the end of line 2; included and not-added rows look the same.
- **Mock-ups:** `docs/design/proposals/local-storage/22` (A: a small tag after the title on every row — ● In library,
  ⦸ Excluded with the row dimmed, ○ Not added), `23` (B: only rows that differ from their folder are tagged), `24`
  (A in a mixed folder). The focused row's button matches the state: Exclude / Include / Add to library.
- **Question (owner):** what is the green tick on the right? It is **Watched** (existing in 0.154). Two greens with
  different meanings is confusing; Claude proposes the watched tick in neutral white/grey so green means "in library" only.
- **Decision (owner, 4 Nov 2026):** option **A** (every row tagged). The watched tick becomes a **larger** solid light
  disc with a dark tick (about 44px, was a 26px green outline), so green means "in library" only. Mock-up `25`.
  The folder header gains an "N watched" pill.
- **Owner follow-up (4 Nov 2026):** the tick is not self-explanatory at first glance; use words. Proposed `26`: the
  right-hand end of each row reads **✓ Watched** (white), **40% watched** (blue, with a small bar) or **Unwatched**
  (dim grey). Supersedes the disc in `25`.
- **Status:** `26` awaiting the owner's approval — then awaiting go-ahead to code.

## 13. Local Storage Grid view: artwork, status and long-press menu (design + bug)
- **Seen (owner photos):** Grid shows a generic file icon on every card, never artwork, though the card's own code
  comment says "artwork or icon" (`LocalStorageView.Card.bind` only ever sets `PreviewIcon`). Long press opens a
  translucent Play / Exclude box that sits over the next card with that card's name showing through it.
- **Proposed (`27`, `28`):** each card shows the film's poster when matched, otherwise a frame from the file,
  otherwise the icon; a corner dot for the library state (● in, ⦸ excluded and dimmed, ○ not added); a **Watched**
  badge or a progress bar on the art; a status line under the title ("2015 · In library · Watched"). Long press /
  Menu opens an opaque panel beside the card: title, status line, then Play/Resume, Details, Mark as
  watched/unwatched, Exclude/Include/Add to library. Posters depend on item 12 (library lookup) being fixed.
- **Status:** awaiting the owner's approval — then awaiting go-ahead to code.


## 12. Local Storage: library rows show only file name and size (check)
- **Seen (same photo):** in a folder marked "In your library", rows show the raw file name and only the size
  (e.g. "Aftersun.2022.1080p.WEBRip.x264-RARBG · 2.1 GB"), not the matched title, year, runtime, resolution and audio.
- **Suspicion (Claude, not confirmed):** `LocalStorageView` looks each file up in the library by its URI string; if
  the USB drive's path in the library differs from the browser's (e.g. `file:///storage/XXXX-XXXX/...` vs a
  mount alias), every lookup misses and rows fall back to name + size. To confirm on the next diagnostics export or
  with one photo of a film from this folder in Movies.
- **Status:** owner confirmed 4 Nov 2026 that the rows have no details under the title and wants it fixed — investigate and fix in 0.156 (awaiting go-ahead).
