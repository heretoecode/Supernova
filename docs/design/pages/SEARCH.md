# Page sheet: Search

The one place to build Search from (CLAUDE.md "Definition of done"). For 0.158. Latest decisions as of 18 Nov 2026.

Sources: `docs/design/RULES.md` (§1, §5, §6, §7, §15, §18), `docs/0.157/OWNER_NOTES.md` §18 (7–8 Nov: library first,
streaming rules, Where to Watch), §22–§23 (headings), §26 (final pass, approved 12 Nov). Mock-ups in
`docs/design/proposals/search-flat/`: `17-v5-start`, `18-v5-family`, `19-v5-person`, `20-v5-non-fiction`,
`21-v5-not-in-library`. The keyboard in those pictures is simplified; the app uses the one keyboard (RULES §15).

**Review in progress (25 Nov):** redrawn on today's rules as `search-v6/tv/01`–`08`, waiting for the owner. Until
decided, the 12 Nov pictures and the text below stand.

## Whole page
- Plain charcoal, side margin on the top bar's edges (RULES §1). Search is a tool: it opens on OK in the top bar
  (0.158 top bar).
- **Left:** the search field and the one keyboard, always shown, top-aligned (field level with the first result);
  focus starts on **T**. **Right:** what to show (below). Results arriving never take focus from the keyboard.
- Typing is forgiving: capitals, punctuation and a leading "The" ignored, one-letter typos, original titles, TMDB /
  IMDb / TVDB / Trakt numbers. Typed letters show in bold inside matching results.
- Denser rows (about 96 px, names 32 px, second lines 24 px), thumbnails with the artwork edge, faint lines between
  selectable rows only; headings with icons, about 26 px below and 64 px above (RULES §5).

## Before typing (`17`)
- **Recent searches** (clock icon): rows with a magnifying glass; **Clear history** (plain words with a small ✕, no box
  until focused) at the end of the heading. Also in Settings.
- **What you can search** (light bulb): tips as plain text, no lines; the streaming line ("Your services: related titles
  you can stream, for things already in your library") only when Where to Watch is on.

## Results
- **The library is the key:** Search looks in the library. Nothing in the library matches → no streaming results at all.
- **In your library** (books-and-films icon): every matching title as its own row (a person match lists each title with
  the reason, e.g. "Taylor Sheridan · creator"). Pills in a fixed column: "Resume S5 E3", "Watched", "Unwatched" (no "In
  library").
- **Streaming groups** (cloud icon), only when a library title matched and Where to Watch is on, one line starting
  "Stream": "Stream more from the world of Yellowstone" (fiction: same story — sequels, prequels, spin-offs, same
  collection), "Stream more related to Planet Earth II" (non-fiction), "Stream more from Taylor Sheridan" (people).
  Never "similar" (that stays in More Like This). Pills: the service name with the open-in-app icon; the top service in
  Your services when several.
- Not in the library (`21`): "Nothing in your library matches …", with no streaming results.
- **Exactly one focus:** on results the remote is on a result; the keyboard shows no focus.

## Actions and where they lead

| Action | Leads to |
| --- | --- |
| OK on Search in the top bar | Search opens with focus on T |
| Typing | Results update live on the right; focus stays on the key pressed |
| Right from the keyboard's last column | The first result (or Recent searches before typing) |
| Left from a result | Back to the keyboard, on the key last used |
| OK on a recent search | Fills the field and shows its results |
| Clear history | Recent searches cleared at once, with an Undo notice |
| OK on a library result | That title's Details; Back returns to Search with the text and focus kept |
| OK on a streaming result | The title in its service's app (deep link where possible), no Supernova page |
| Phone key | Android's keyboard on the same field (paste from the phone app); Back or Done returns |
| Voice key | Voice input into the field |
| Back | From results to the keyboard; from the keyboard to the top bar (Search stays as it was if reopened soon) |

## Never opens from Search
NOVA's old search screen (it must not run hidden behind Search), the old results grid, Android's keyboard except through
the Phone key, a Supernova page for a streaming-only title.
