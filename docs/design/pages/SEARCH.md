# Page sheet: Search

The one place to build Search from (CLAUDE.md "Definition of done"). For 0.158. **Reviewed and approved by the owner
on 25 Nov 2026**; replaces every earlier version.

Mock-ups `docs/design/proposals/search-v6/tv/01`–`11` (Drive › From Claude › 2026-11-25 Search › Final):
`01` before typing (old layout, superseded by `09`), `02` a show and its world, `03` a person, more than fits, Menu on
a library title, `04` streaming still loading, `05` after Clear history, `06` nothing in the library, `07` the Voice
key, `08` progress A (left half), `09` landing with recent searches, `10` landing without history, `11` OK on a genre.
Rules: `docs/design/RULES.md` (§1, §2, §3, §5, §6, §7, §7b, §14, §14c, §15, §16, §18, §21). Older history: 0.157 notes
§18, §22–§23, §26; `search-flat/17`–`21` (12 Nov).

## Whole page
- Speckled background; side margin 52 px on the top bar's edges (RULES §1). Search is a tool: it opens on OK in the
  top bar, which shows Search in its grey box while focus is on the page (RULES §2).
- **Left, always the same:** the search field ("Movies, TV shows, people…") and the one keyboard, top-aligned (field
  level with the first item on the right); under the keyboard, at all times, **What you can search** (light bulb):
  plain tips, no lines, rows about 50 px: Titles · People · Genres · Years, and Your services ("related titles you can
  stream") only when Where to Watch is on. Not focusable.
- **The keyboard:** the one keyboard (RULES §15); bottom row `#+=` · Voice · Space · Delete · Clear; **no Phone key**:
  typing or pasting from the phone app lands in the field as in any other app. Focus starts on **T** and stays on the
  key pressed; results arriving never take it.
- **Right:** before typing, the landing (below); while typing, the results.
- Typing is forgiving: capitals, punctuation and a leading "The" ignored, one-letter typos, original titles, TMDB /
  IMDb / TVDB / Trakt numbers. Typed letters show in bold inside matching results.
- Rows about 96 px (names 32 px, second lines 24 px), 16:9 thumbnails 128 × 72 with the artwork edge, faint fading
  lines between selectable rows only, never under the last; headings with icons, 26 px below and 64 px above (RULES §5).
- More than fits: arrows in the right margin (RULES §16), no scroll bar; a row half off the screen stays.
- **Exactly one focus:** on the right the remote is on an item; the keyboard shows no focus.

## Before typing (`tv/09` with history, `tv/10` without)
- **Recent searches** (clock icon): the last three, rows with a magnifying glass; **Clear history** (plain words with a
  small ✕, no box until focused) at the end of the heading. Not shown when nothing has been searched or when search
  history is off (Settings › Library › Viewing History: Keep search history · Clear search history).
- **Genres in your library** (tag icon): 16:9 artwork cards 214 × 120 (a backdrop from a library title of that genre,
  the artwork edge), the genre and "42 titles" over the foot; four across, most titles first; one row with Recent
  searches, two rows without.
- **People in your library** (person icon): the six people with the most titles in the library, as Details' cast cards
  (2:3, 112 × 168, initials when no photo), name and "6 titles" under.
- Cards are artwork: focus grows them with the 4 px ring (RULES §14), never a box.

## Results
- **The library is the key:** Search looks in the library. Nothing in the library matches → no streaming results.
- **In your library** (books-and-films icon): every matching title as its own row; second line "2019 · TV show · 4
  seasons" / "2025 · Movie · 1 h 52 min"; a person match lists each title with the reason ("Mara Ellison · creator").
- **Progress (owner, 25 Nov, A):** the corner badge on the thumbnail (RULES §14c), scaled to 30 px; a pill in the fixed
  column only where it adds words: "Resume S2 E3" (▶). No "Watched", "Unwatched" or "In library" pills.
- **Streaming groups** (cloud icon), only when a library title matched and Where to Watch is on, one heading starting
  "Stream": "Stream more from the world of Tidelands" (fiction: sequels, prequels, spin-offs, same collection), "Stream
  more related to Our Blue Planet II" (non-fiction), "Stream more from Mara Ellison" (people). Never "similar". Second
  line "2022 · TV show · prequel". Pill: the service's name with the open-in-app icon, the top service in Your
  services when several (outline = information).
- **Still fetching (`tv/04`):** library results show at once; under the streaming heading one row with the small
  loading ring (RULES §21) and "Looking on your services"; the found titles replace it; if none are found the heading
  and the line fade out together (nothing below them moves).
- **Nothing in the library (`tv/06`):** centred on the right, an icon, "Nothing in your library matches "terminus"",
  then two soft lines: "Search looks only in your library." / "Titles you can stream show up under what it finds."
  (Where to Watch off: the first line only.) Focus stays on the keyboard.
- **Voice (`tv/07`):** the Voice key turns the field into "Listening…" with the microphone and a soft ring; the words
  fill the field as they are heard and the results follow; the right keeps what it showed until then.

## Actions and where they lead

| Action | Leads to |
| --- | --- |
| OK on Search in the top bar | Search opens with focus on T |
| Typing | Results update live on the right; focus stays on the key pressed |
| Right from the keyboard's last column | The first item on the right (a recent search, a genre card, or a result) |
| Left from the right side | Back to the keyboard, on the key last used |
| Up / Down on the right | Between rows and sections; arrows show when there is more |
| OK on a recent search | Fills the field and shows its results |
| OK on a genre or person card | Fills the field with its name and shows the results, as if typed (`tv/11`) |
| Clear history | Recent searches cleared at once; the landing shows two rows of genres; the Undo notice (`tv/05`, RULES §7b) |
| OK on a library result | That title's Details; Back returns to Search with the text and focus kept |
| Long-press / Menu on a library result | The library long-press menu beside the row (LIBRARY.md; `tv/03`) |
| OK on a streaming result | The title in its service's app (deep link where possible), no Supernova page |
| Typing or pasting in the phone app | The text lands in the field, no button (RULES §15) |
| Voice key | "Listening…" in the field; the spoken words fill it (`tv/07`) |
| Back | From the right side to the keyboard; from the keyboard to the top bar (Search stays as it was if reopened soon) |

## Never opens from Search
NOVA's old search screen (it must not run hidden behind Search), the old results grid, Android's on-screen keyboard, a
Supernova page for a streaming-only title.

## Later (parked, not for 0.158)
Request titles not in the library through Seerr ("Not in your library" section with Request): REVISIT_LATER ›
Discovery in Search.
