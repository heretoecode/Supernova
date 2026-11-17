# Page sheet: Home

The one place to build Home from (CLAUDE.md "Definition of done"). For 0.158. Latest decisions as of 18 Nov 2026.

Sources: `docs/design/RULES.md` (§1, §2, §14, §16, §17, §23), `docs/0.157/OWNER_NOTES.md` §11 (hero change), §19 (rows,
move mode), §33 (Home first pass to "Home done", 13 Nov), §39 (onboarding). Mock-ups in `docs/design/proposals/home-v2/`:
`01-home`, `02-row-heading-and-move`, `03-customise-home`, `05-v3-add-a-row`, `06-v4-add-a-row-focus`,
`07-v4-icon-picker`; the hero as Details (`details-background/21`). Where a mock-up shows the SUPERNOVA wordmark or a top-bar
underline, this sheet wins.

## Whole page
- Plain charcoal `#16181C` (since 0.157.2). **No side margin:** rows run off both edges (owner); the hero and row
  headings start at the top bar's left edge.
- 0.158 top bar (RULES §2 note): Home opens as focus lands on it; first launch after a reboot opens Home.

## Hero
- **Same design as Details** (1776×820 at 1080p): logo artwork (or the name), metadata row (Age rating · Year · Length ·
  Genres), two-line description, no tagline, buttons fixed at the bottom left ("Play" / "Resume S1 E5 · 16 min left",
  then More), artwork edge, the first row peeking below. **Carousel dots** under the hero.
- **Changing hero (§11):** the frame, edge and buttons stay still; the next and previous artwork are loaded first, so
  there's never an empty frame; the picture cross-fades (about 450 ms) with a slight drift; text fades out and back in
  just after; focus stays on the same button; dots glide; presses during a change finish it at once; automatic changes
  pause while focus is on the hero and never happen while a row is focused; follows "remove animations".
- Going into Details from the hero, the card stays exactly where it is (the Home-to-Details glide).

## Rows
- One card size: 16:9, 400×225 (focused 440×248), 24 px gap, a sliver of the next card at the edge.
- **Caption box under each card** (approved 13 Nov): title (bright), one soft line, inset progress bar; nothing printed
  over the artwork; no play icon on library items.
- Row heading with its icon. **Move and Hide** appear beside the heading as plain buttons (no filled circles; Hide a
  minus circle) when focus reaches the heading. Down from a card lands on Move; Down again on the next row's first card;
  Right from Move to Hide.
- Rows a left arrow appears beside: the whole row moves right to make room (never just the first card) (RULES §16).
- **Move mode** (app-wide gesture): the heading lifts with ⌃⌄ and "Row 1 of 6 · OK when done", other rows dimmed; Up /
  Down moves it live; OK or Back puts it down; focus locked to the row meanwhile (never escapes to the hero or top bar).
- Continue Watching updates as soon as playback ends.

## Customise Home (`03`)
Opened from the **Customise Home** button at the bottom of Home, below the last row (as today).
Floating side panel on the right; changes apply live; Back closes.
- **Your Rows** first (each with ›, opens its settings), **Add a row**, then **Built-In Rows** with a switch each:
  Continue Watching, Recently Added, Watch Next, Trending on Trakt, Popular on Trakt, Recently Watched, Because You
  Watched. Order with the move gesture.
- **Add a row / a row's settings** (`05`, `06`), opening on top of Customise Home (panel behind darkened): Row name (one
  keyboard), Icon (opens the icon picker), Includes, Genres (+ Match), Years, Minimum rating, Sort by, Order, Maximum
  items (10 · 15 · 20 · 25 · 30), Add new titles automatically; Delete row at the foot apart by a plain line.
- **Icon picker** (`07`): a grid of icons with names, tick on the current one, no box on tiles at rest. Automatic icon:
  the first genre chosen gives its icon; no genre, a plain list icon.

## Actions and where they lead

| Action | Leads to |
| --- | --- |
| Focus lands on Home in the top bar | Home opens (no OK) |
| Down from the top bar | The hero's first button |
| Left / Right on the hero | Previous / next hero (as above); focus stays on the same button |
| Play / Resume (hero) | Preparing playback, then the player; Back returns to Home, same button |
| More (hero) | The Details More menu beside the button (same items as Details) |
| OK on the hero artwork / Down then Up | Details for that title (card stays in place) |
| OK on a row card | That title's Details; Back returns with focus on the same card |
| OK on a streaming-only card | The title in its service's app (no Supernova page) |
| Long-press / Menu on a card | The library long-press menu beside the card (LIBRARY.md) |
| Move (row heading) | Move mode for that row |
| Hide (row heading) | Row hidden at once with an Undo notice; it can come back in Customise Home (approved 18 Nov) |
| Customise Home (the button at the bottom of Home, below the last row; owner, 18 Nov) | Customise Home panel on the right; Back returns to the button |
| A row in Customise Home (›) / Add a row | Its settings on top of Customise Home |
| Icon | Icon picker; OK chooses and returns |
| Delete row | The delete warning ("Keep row" focused first); Back returns (approved 18 Nov) |
| Back from the rows | First to the hero, then to the top bar |

## Onboarding on Home (0.159; noted here so Home leaves room)
The welcome and its steps sit in Home's space until videos are added or a backup restored (RULES §23); built with
Settings in 0.159.

## Never opens from Home
NOVA's old browse screens, the old Edit row / Home settings dialogs, filled-circle row buttons, the old long-press dialog,
Android's keyboard.
