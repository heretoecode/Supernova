# Supernova 0.158: release notes, one section per APK

Every signed APK has its own notes here, newest first, even when it is only a fix or a tweak (owner, 19 Nov 2026).
The app shows the same notes in About › What's New. Run links and test counts are in `REPORT_0158_A.md` and
`REPORT_0158_B.md`.

## 0.158.9 (code 1589) — Home, player, Search and library pages
**Home**
- The hero has one button, More: Details · Playback (Play or Resume, Play from the start) · This Title (Mark as
  watched, Exclude from hero).
- Exclude from hero takes the title off the hero at once, with an Undo notice.
- Row headings no longer stop the remote, and there is no Move or Hide on Home: Customise Home does that.

**Player**
- Seek previews never show a grey box: when there is no picture (or Seek previews is off), the time shows on its own.
- Track languages in words ("Chinese", not "chi").
- Audio boost always steps Off · Low · Medium · High.

**Search**
- What you can search stays under the keyboard at all times; the landing shows your last three searches.

**Library pages**
- Six posters a row, 32 px apart.
- Mark as watched happens at once, with no notice; the menu then offers Mark as unwatched.

**Everywhere**
- The top bar: the page you leave loses its grey box as soon as the remote lands on the next page.

## 0.158.8 (code 1588) — not signed (a lint error stopped its build); everything below is in 0.158.9
**Fewer sudden closes**
- Supernova no longer closes by itself while titles are being matched (six times, 20 and 22 Nov) or while it looks
  for network devices.

**Scans**
- A scan that is waiting its turn shows as waiting and runs later, instead of being counted as failed.
- No hourly wake-ups while Supernova is closed; a scan that fell due runs when you open it again.
- The import notification no longer lingers at 0/0 (NOVA's fix).

**Settings**
- Up and Down reach every row, the Backup row on System included, and the row you are on is always on screen.

**Everywhere**
- The remote no longer slips onto a hidden grid on Home and the library pages.
- A delete that fails says why in plain words. Files your Put.io connection looks after are deleted on Put.io.
- Where to watch finds TV shows matched long ago, and stops asking about a show it can't find for a day.
- TV show posters whose saved copy went missing are fetched again instead of staying blank.
- Home redraws less, and artwork uses less memory.
- Diagnostics note what was running whenever the screen froze for a second.

**Also new**
- A TV show with a new episode this week says so above its title ("New episode tomorrow", "New episode every
  Sunday"), on Details and on Home; Details › Status reads "Returning · S2 E4 on Sun 29 Nov".
- Settings › Library › Viewing History: Keep search history and Clear search history.
- The keyboard has no Phone key: the phone app should type and paste into the box straight away (to check).
- From NOVA: zero-length credits never skip a whole episode; Trakt stays signed in; notification sounds no longer
  pause the video; one network look-up at start-up.
- Diagnostics reports open with a health summary: memory per page, start-up time, page switches, freezes and the
  old NOVA parts that still ran.

## 0.158.7 (code 1587) — signed 19 Nov 2026
**Settings and Details**
- When a video was started: Ask, Resume or Start over
- The OpenSubtitles panel redrawn; the Trakt sign-in in Supernova's look
- Extras and season rows have icons on their headings

**Files (from 0.158.6, not signed on its own)**
- Lists on Files no longer show under the toolbar, tabs or top bar
- Your Library lists your own library page under By Library Page
- The dot beside Files shows only when Library Care has something to check
- All scans: the remote moves through the scans instead of jumping to Home
- The connection form fits on one screen
- Placeholder cards show again while a folder loads
- A TV show's More › Artwork opens the artwork side panel
- The Skip button is the same size as the player's other buttons
- The Details tab fits on one screen, Cast and Crew included
- After you close Supernova fully, it opens at Home
- With two or more files for a title, Play asks which one
- More Like This in rows: In your library, then one row per service
- Scans wait their turn instead of timing out while another scan runs
- Connections can be turned off and on again; smaller buttons on Files rows
- Save a diagnostic report saves straight to Download › Supernova; action rows show their verb

## 0.158.5 (code 1585) — not signed; everything in it is in 0.158.7
**Settings**
- The skip rows read Off · Show button · Skip · Skip when binge-watching. Skip when binge-watching skips every part
  while you binge and shows the button otherwise.
- Every row's name follows the Settings page sheet; choices have plain names (Automatic, Hardware, Software …).
- Home Hero: Because you watched and Change every (8, 12 or 20 seconds); the last source can't be turned off.
- Diagnostics: Keep diagnostics for.

**Details**
- Episodes: OK on an episode card plays it; long-press (or Menu) opens the episode's menu beside the card.
- Deleting an episode when earlier ones of the season are still there asks about those too: this one only, all of
  them, or Choose… (with Select all). Keep file comes first.
- Seasons show their own artwork; a season's More menu opens beside its button and has Delete season.
- A grown card and its ring are no longer cut off at the sides of a row.

**Titles**
- Official logos sit in one area (inside 900 × 240), trimmed of empty space, their foot just above the details line.
- Titles without a logo are drawn in Supernova's own house style.

## 0.158.4 (code 1584) — signed 19 Nov 2026
**Playback Settings, round two**
- A narrower panel; every row on one line, and every tab fits without scrolling; no line under the tabs.
- Text size, Background opacity, Audio boost, Format, Speed, Stop playing at, Still watching? and Play mode step with
  Left and Right; ‹ › show on the focused row.
- Skipping: Off · Show button · Skip; Remember for this show under This Show.

**Everywhere**
- Pop-ups, menus and side panels at 99%, no outline or glow, over the speckled background.

**Files**
- OK on Check matches keeps focus on Check matches.

## 0.158.3 (code 1583) — signed 19 Nov 2026
**Settings**
- One page with seven tabs: Playback, Audio, Subtitles, Library, Services, System and About; no tiles.
- Each tab shows all its sections, each under its heading; the tabs use the top bar's focus.
- Seek previews is back, in Playback › General.

**Also**
- Fix match in More for TV shows; a smaller gap between seasons on Details.
- Lines between rows show better on the speckled background.
- A page you leave for another opens at its top when you come back.

## 0.158.2 (code 1582) — signed 19 Nov 2026
**Preparing playback and the player**
- One title block on Preparing playback and in the player, top left, with the age rating first on its line.
- The player's clock in the top bar's font; the Chapters row sits lower and closer.
- No chapters of a few seconds; chapter and episode cards have the thin edge.

**Details**
- Tabs use the top bar's focus; More › Full description shows only the text.
- Values read 2 h 3 min, $25 million and Arabic.

**Also**
- The icon picker fits the longest genre names; a found device's name keeps clear of its New pill.

## 0.158.1 (code 1581) — signed 19 Nov 2026
**The new look**
- Every page sits on a fine speckled background instead of plain charcoal.
- Buttons, pills, chips and the focus boxes share one shape: the same height, softer corners.
- Words sit in the middle of their boxes.

**Home**
- The Home hero is the same size and place as the Details hero.
- Carousel dots under the hero; the current one is a longer pill that glides when the hero changes.

**Playback Settings**
- Tabs use the top bar's focus: a white box, and a soft grey box for the open tab.
- Every tab fits; rows are tighter and every row has its name.
- Switches and the chosen chip turn dark on a focused row; pills keep their outline.
- Chips instead of arrows: Format, Still watching?, Background opacity and Audio boost.
- Text size has Extra large; Vertical position has − and +; Subtitle delay sits with the subtitle choices.
- Subtitles stay on top of the picture at every position.
- A track list opens on top, with the panel behind kept whole and darkened.
- Download subtitles: no header, a plain search row, readable names.

**Fixes**
- Leaving network discovery no longer crashes Files.
- Scan this folder on a folder on this device no longer crashes.
- Seek previews of network files work again.

## 0.158 (code 1580) — signed 19 Nov 2026
**Top bar**
- No SUPERNOVA name: every item is a word, Files, Search and Settings included, in one font with the clock.
- Home, Movies, TV Shows and your own page open as you land on them; Files, Search and Settings open on OK.
- The open page keeps a soft grey box while you are elsewhere; no underlines.
- Movies, TV Shows, Files and Search line up with the top bar's edges.

**Movies, TV Shows and your own page**
- Seven posters a row, each with a caption: the title, the show's size or how far you are, and a line that follows
  the sort. Watched titles get a tick; started titles a bar.
- Filters: Watched, Genre (Any or All), Year and your streaming services, each with how many titles.
- Sort in groups (General, Trakt, Files, Series); the Order button's words follow the sort.
- List view with Columns: pick a column up and move it; Reset to default.
- Customise page opens a panel on the right; changes show at once, no Save button.
- Long-press or Menu on a poster: Play or Resume, Mark as watched, Fix match, Remove from library, Delete file.
- Empty pages say so and offer Add your videos; your own page starts with Make this page your own.

**Home**
- Row cards are one size, with their caption under the picture and a bar for what you have started.
- Move and Hide sit beside a row's heading; Hide has Undo.
- Customise Home is a panel on the right: your rows, Add a row and the built-in rows with a switch each.
- The hero: a two-line description, Play (or Resume) and More; a new hero fades in, never an empty frame.

**Search**
- Plain page: the keyboard on the left, Recent searches and tips on the right.
- Clear history has Undo; results sit under In your library with Resume, Watched or Unwatched.
- A film from a series of films offers Stream more from the world of …: the others on your services.

**Files**
- Connections are grouped by server: one row per server and login, one scan button each.
