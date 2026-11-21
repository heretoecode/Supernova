# Page sheet: Details

The one place to build Details from (CLAUDE.md "Definition of done"). **Rewritten 21 Nov 2026** from the owner's
review (rounds 1–6, all approved); replaces every earlier version. Mock-ups `docs/design/proposals/details-v3/tv/`
(`01`–`28`; Drive › From Claude › 2026-11-21 Details). Rules: `docs/design/RULES.md` (§3, §4, §8, §10, §11, §14,
§14c, §14d, §16, behind the top bar, long lists, no lone word). Where an older mock-up or doc differs, this sheet wins.

## Four kinds
Movie, TV show, season, episode. Single-season shows have no season page.

| Kind | Tabs (hidden when empty; all drawn at once, never popping in) | Opens on |
| --- | --- | --- |
| Movie | Details · Extras · More Like This | Details |
| TV show | Details · Episodes · Extras · More Like This | Episodes |
| Season | Details · Episodes · Extras · More Like This | Episodes |
| Episode | Details · Episodes · Extras · More Like This | Details |

## Whole page
- One page: the hero, then the tabs below it. Speckled background. **Side margin 52 px** (the top bar's edges) for the
  hero, tabs, headings, the Details tab and the start of rows. Rows run off both screen edges, never cut at an
  invisible line (also on the left once scrolled).
- **On a tab** the page scrolls so **the hero's foot (about 56 px) peeks above the tabs**; no title or logo is
  repeated; no scroll arrows on the page.
- **Behind the top bar** (`tv/16`–`18`): scrolled content runs on behind the bar blurred and dimmed, fading out at the
  bar's foot; never a solid band or hard line; the bar's words on top. While focus is below the hero, its buttons
  are at rest (never white).

## Hero (the one shared hero, RULES §14d; `tv/01`, `tv/25`)
- 1816 × 820 at x 52, y 112; 1 px white edge with inner shade; dark shade from the left; text block bottom-left,
  anchored up from the buttons.
- **Logo** (official artwork, placed per RULES §19; the name in house style when none).
- **Title line:** episodes only, "Season 1 · Episode 3 · The Long Tide". **Seasons have no title line:** the facts
  line starts with "Season 1" in medium weight.
- **Facts line:** age badge (no dot after it) · year (show: "2024–2025") · length (movie/episode running time;
  season "10 episodes"; show "2 seasons") · up to four genres.
- **Description:** two lines.
- **Buttons** fixed at the bottom left: Play / "Resume S1 E3 · 31 min left" (or the streaming button) · More.
  Focus starts on the first. The tabs sit at the same height as Home's first row heading.

## Tabs
Plain words with icons; **the player's tab behaviour** (RULES "One tab behaviour everywhere"): focus = the white
box, sized to the word, clear of the line; inside the tab the open tab keeps a white underline, never a grey box,
never overlapping the line. Line under the tab row = the
library toolbar line (white 35%, about 2 px, fading at both ends). Moving along switches the tab at once; Up lands
on the open tab; Down goes to the tab's first row.

## Details tab (one screen, nothing focusable; `tv/02` movie, `13` show, `14` season, `15` episode)
- Three columns with icon headings: **Reception** (IMDb, Rotten Tomatoes, Metacritic, TMDB, awards; rows about 76 px,
  28 px values) · **Key information** · **Technical information**; soft labels, white values, quiet "Unknown",
  faint fading row lines; both right-hand columns the same number of rows.
- Then **Cast** and **Crew** side by side, seven each, 2:3 portraits 112 × 168 with the artwork edge, name and role
  under (show: "Main cast"; season: "Season cast"; episode: guests included).
- **Long lists** (studios, countries, languages, subtitles, mixed formats): at most two, then "and N more"; mixed
  technical values across files: the most common, then "· n files differ" (or "1080p · 2 in 2160p"). Never a full line.

| Row | Movie | Episode | Season | Show |
| --- | --- | --- | --- | --- |
| Date | Released | Aired | Aired (first – last) | First aired |
| Second | Runtime | Episode "Season 2 · Episode 3"; Runtime | Season "2 of 2"; Episodes "8 · 8 in your library"; Episode length "Average 50 min"; Total running time | Status "Returning · 2 seasons"; Episodes "18 · 16 in your library"; Episode length; Total running time |
| People | Directed by, Written by | Directed by, Written by, Guest stars | "Ruth Kane and 2 others" | Created by |
| Other | Genres, Studio, Country, Language, Original title, Budget | Genres, Network, Country, Language | Genres, Network, Language | Genres, Network, Country, Language |
| Technical | Resolution, Video, HDR, Frame rate, Audio, Audio languages, Subtitles, Size, **File**, **Location** | the same | the same with "Average 2.9 GB · total 23.2 GB", **Files** "8 episodes", Location = the season folder | the same, Location = the show folder |

No Container (the file name shows it), no Revenue, no Bitrate (list-view column only). Reception on a season or
episode shows its own scores where they exist, otherwise "Unknown".

## Episodes tab (`tv/06`, `tv/26`)
- One shared row per season, heading "Season 1" (not focusable).
- **Season card first in each row (A, `tv/26`):** same size as an episode card; the season's own 2:3 poster whole on
  the left; "Season 1", "10 episodes · 2 watched" (or "not started" / "all watched"), "Open season ›". OK opens the
  season page; long-press opens the season's More menu (artwork, Delete season). Not on single-season shows, not on
  a season page's own Episodes tab.
- Episode cards: row design A, corner badge. **OK plays the episode.** **Long-press** (`tv/17`), beside the card on
  whichever side has room: Playback (Resume · n min left / Play, Play from the start) · Library (Mark as watched ⇄
  Mark as unwatched, Mark earlier episodes as watched) · Files & Media (Subtitles, Delete episode file).

## Extras tab (`tv/18`, `tv/19`)
Shared rows, one per kind, Trailers first; most specific first (episode, season, show). YouTube's red mark the size
of the streaming logos, centred on each card. OK plays it **as large as possible**, page blurred behind, no title,
**no YouTube controls**, a thin progress line at the foot; Back closes.

## More Like This tab (`tv/04`, `tv/20`)
"In Your Library" first (OK opens its Details; **long-press**: Playback (Resume / Play, Play from the start) ·
Library (Add to Watch Next, Mark as watched)). Then one row per service, headed with the service's logo and
**"On Netflix"** ("On Prime Video" …); the logo centred on each card; OK opens the title in that app.

## More menu (`tv/05`)
Beside the button, sized to its longest item, an icon each (no two alike), centred Title Case headings. Only what
makes sense (no Restart on an unstarted title).
Playback: Restart movie / episode · Library: Mark as watched ⇄ unwatched, Fix match (pencil icon), Remove from
library (crossed-off list) · Files & Media: Subtitles, Artwork, Full description, Delete file · foot, after a line:
**TMDB page QR code**.

## Actions and where they lead

| Action | Leads to |
| --- | --- |
| Play / Resume | Preparing playback, then the player; Back returns here, focus on the same button |
| Streaming button | The title in that service's app; the top service in Your services when several |
| Restart | Preparing playback from the start |
| Mark as watched / unwatched | Changes at once, no notice; the menu row flips |
| Fix match | Files › Library Care's Fix match page (Match and return · Match and stay); redesign at the Files review |
| Remove from library | Removed at once; back to the page you came from, the poster gone; Undo notice (`tv/07`) |
| Subtitles | Side panel about 560 px (`tv/08`): Built In · Subtitle Files · Online; OK ticks |
| Find subtitles online | On top of Subtitles (`tv/08b`): Language ‹ English › (stepper), Results (release name, downloads; pills Best match · SDH · Forced); OK downloads and ticks |
| Artwork | Side panel (`tv/09`), no title or footer: For (Hero and rows · Library page) fixed at the top; pictures scroll beneath it with arrows in the margin; **Suggested** badge; tick on the current; the hero previews the focused picture |
| Full description | The text alone, page blurred; Back closes (`tv/10`) |
| Delete file | Warning (`tv/21`): "Delete <title>?"; the file name on its own line, its facts under it; Clean Up (history, Trakt when connected, empty folder on); Keep file focused |
| Delete episode file | The same warning; with earlier episodes in the library (`tv/28`): **Also delete ‹ None · Watched only (n) · All earlier (n) ›** naming the range ("E1 – E10 except E7"), starts on Watched only when any is watched; total; "Delete n files" |
| TMDB page QR code | The code alone, page blurred; Back closes (`tv/12`) |
| Season card | That season's page (`tv/25`) |
| OK on an episode | Plays it |
| OK on an Extras card | The Extras player |
| OK on More Like This (library / service) | Its Details page / the service's app |
| Long-press (episode, library title, season card) | Its menu beside the card |
| Back | From a tab's rows to the tab row, then to where Details was opened from, focus restored |

## Never opens from Details
NOVA's old Details screens, the old "Change info of all episodes" panel, the old Fix match side panel, NOVA's
subtitle dialogs, Android's keyboard, YouTube's own player controls.

## Bugs to fix on 0.158.7 (owner's photos, 21 Nov)
Hero clipped left and right by an invisible frame (DT3); rows cut off at both ends (More Like This); a solid band
under the top bar instead of the blur; the tab focus box too big and overlapping the line, and a grey box (also
overlapping) on the open tab instead of the underline. See BRIEF_0158_B › Details review, round 3.
