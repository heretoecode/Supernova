# Page sheet: Details

The one place to build Details from (CLAUDE.md "Definition of done"). Latest decisions as of 17 Nov 2026. Sources:
`docs/design/RULES.md` (§14, §14b), `docs/0.157/OWNER_NOTES.md` (§1, §29), mock-ups in
`docs/design/proposals/details-background/` (latest `21`, `16`–`20`; tabs `12`–`14`) and `fix-match-from-details/`.
Where an older mock-up differs, this sheet wins: the tab is called **Episodes** (not "Seasons & Episodes"), the
background is plain charcoal, menu group headings are centred on their line in Title Case, the description is two
lines, and there is no tagline.

## Four kinds
Movie, TV show, season, episode. Single-season shows have no season page.

| Kind | Tabs (master order, hidden when they don't apply) | Opens on |
| --- | --- | --- |
| Movie | Details · Extras · More Like This | Details |
| TV show | Details · Episodes · Extras · More Like This | Episodes |
| Season | Details · Episodes · Extras · More Like This | Episodes |
| Episode | Details · Episodes · Extras · More Like This | Details |

Empty tabs hide and the rest close up; all tabs are drawn at once, never popping in.

## Hero
- Same size and place as Home's (1776×820 at 1080p), thin white edge with inner shade, the tab row peeking below.
  Artwork changes once (no flicker); buttons are not rebuilt while loading.
- **Logo:** the title's official logo artwork; its name in plain text when it has none. Going down into the tabs it
  glides and shrinks to the compact logo top left.
- **Title line** (seasons and episodes): "Season 2" / "Season 2 · Episode 8 · Title", as in the player.
- **Metadata row:** Age rating (outlined badge) · Year · Length · Genres (TMDB order, three at most); missing items
  left out. Length: running time (movie, episode), seasons (show), episodes (season).
- **Description:** two lines; no tagline.
- **Buttons:** fixed place at the bottom left, whatever the text length. First: "Play" or "Resume S1 E5 · 16 min
  left" (focus starts here); a streaming title: the service's name with the open-in-app icon; then More.

## Tabs
- Plain words with icons; focus is the white box; the open tab's underline as RULES §3/§4. Moving along switches the
  tab at once. Up into the tab row lands on the open tab. Down from a tab goes to its first row.
- **Details (one screen, no scrolling; owner, 17 Nov; mock-up `files-review-0157-1/06` B2):** three columns with icon headings: Reception (IMDb, Rotten Tomatoes, Metacritic, TMDB, awards; icons,
  no written names) · Key information · Technical information. Soft labels, white values, faint row lines, no column
  lines; "Unknown" for missing values. Then Cast and Crew: 2:3 portrait cards, rounded, artwork edge, name and role
  underneath, initials when no photo, **seven each, Cast and Crew side by side** under the columns. Everything fits
  on one 1080p screen (smaller text, tighter rows than `21`). **Nothing focusable;** Up returns to the tabs.
- **Episodes:** on a show, one Home-style row per season (season heading lined up with the cards); on a season or
  episode, that season's episodes. Cards 16:9 in row design A (RULES §14c): title and one line over the artwork, the corner badge, no bar.
- **Extras:** Home-row cards, one row per kind, Trailers first; most specific first (episode, season, show).
- **More Like This:** rows "In your library" and "On your services" (only with Where to Watch on). Service titles:
  centred colour logo, title · year; no play icon on library items.

## More menu
Opens beside More, sized to its longest item, an icon per item (no two alike), tick for a current choice, group
headings centred on a thin line in Title Case. Only what makes sense: no Restart on an unstarted title (the Playback
group then disappears).

| Group | Items (episode in progress shown; others the same where they apply) |
| --- | --- |
| Playback | Restart episode / Restart movie (only when started) |
| Library | Mark as watched / Mark as unwatched · Fix match · Remove from library |
| Files & Media | Subtitles · Artwork · Full description · Delete file |
| (foot, set apart by a line) | TMDB page (QR icon) |

## Actions and where they lead

| Action | Leads to |
| --- | --- |
| Play / Resume | Preparing playback, then the player; Back from the player returns here, focus on the same button |
| Streaming button | The title in that service's app (deep link where possible); the top service in Your services when several |
| More | The menu beside the button |
| Restart | Preparing playback from the start |
| Mark as watched / unwatched | Changes at once, with an Undo notice; the button and progress update in place |
| Fix match | Files › Library Care's Fix match page for this title: Match and return (back to this page, refreshed) · Match and stay; Back without choosing returns here unchanged (`fix-match-v2-on-files-from-a-title`) |
| Remove from library | Removed at once with Undo; Back to where the page was opened from |
| Subtitles | Subtitle search and download for this title (to confirm against its own mock-up) |
| Artwork | Side panel floating on the right: Hero and rows · Library page; the hero previews the picture you are on (`17`, `20`) |
| Full description | Pop-up sized to the text: title, title line, then the text; no icon (`19`) |
| Delete file | One warning in Supernova's design: name, text, switches for Supernova history and Trakt history (off), "Keep file" focused first, then Delete |
| OK on a season heading | That season's page (not on single-season shows) |
| OK on an episode card | That episode's page (To discuss on the roadmap: OK plays, long-press menu) |
| TMDB page | Pop-up sized to its content: one line "Scan to open the TMDB page" and the QR code (170 px); the page blurred behind; Back closes (`files-review-0157-1/07`) |
| OK on an Extras card | Plays the video nearly full screen with a small margin, the page blurred behind, no title bar and no player controls; Back closes |
| OK on More Like This (library) | That title's Details page |
| OK on More Like This (service) | The title in that service's app straight away (no Supernova page) |
| Back | From deep in a tab: to the tab row first; then to where Details was opened from, focus restored |

## Never opens from Details
NOVA's old Details screens, the old "Change info of all episodes" side panel, the old Fix match side panel, NOVA's
subtitle dialogs, Android's keyboard.
