# Supernova 0.158: release notes, one section per APK

Every signed APK has its own notes here, newest first, even when it is only a fix or a tweak (owner, 19 Nov 2026).
The app shows the same notes in About › What's New. Run links and test counts are in `REPORT_0158_A.md` and
`REPORT_0158_B.md`.

## 0.158.11 (code 1591) — signed 27 Nov 2026 (run 45929267213): the Files page to its sheet
**Files**
- Browsing: folders show "Folder · 31 videos · 48.6 GB"; a folder with no videos says "No videos in this folder" with
  Show all files; a folder's menu offers only what applies: Add to library or Scan this folder, and Fix match on a
  folder holding one title.
- The connection form: Type opens a short menu with each kind's line; Encryption always sits under it, greyed with
  its reason for SFTP ("Always on with SFTP") and SMB ("Set by the server"); an icon on every row.
- Connections › Advanced › Network options (moved from Settings): Windows shares, finding devices, SFTP.
  Test answers in the row: "Connected" or "Can't reach it · check it's switched on and on this network".
- Library Care: Couldn't be read offers Try again · It plays fine; Not matched shows a best guess from the file name
  and offers Fix match · It's right, and says when new titles are still being matched; **To fix** lists titles TMDB
  no longer knows; **Kept by you › Excluded from hero** lists them with Include again · Include all again; Missing
  files ends with a short note on what each button does.
- Fix match: This file (name, where and size, what it is matched as now), Best match on the first result, and Match
  and return · Match and stay on their own line under the focused match.
- Scanning: Scan one source opens a menu of every source with where it stands (Waiting, Scanning · 42 %, its last
  scan, Couldn't connect); recent scans and Show all scans open their Scan details; the tab bar adds "· 2 more
  waiting".

**Also**
- Player: Spatial audio keeps a clear switch, with "Not on this device" where it can't work; the Chapters · Episodes
  line fades at both ends.
- Delete warnings: "Also remove it from your Trakt history" now works.
- Search: Genres in your library has the tag icon.
- From NOVA (25 Nov): the skip button gets focus back once the controls fade; two shows with the same title (e.g. a
  2005 original and a 2024 remake) no longer swap episodes.
- Memory: going from Home or a library page to Files or Settings frees the artwork held in memory (D18).

## 0.158.10 (code 1590) — signed 26 Nov 2026 (run 45879328975): Search to its sheet; first pass on Home, Details, player and library pages
**Search**
- Before you type: your last three searches, then **Genres in your library** (cards with a picture and a count) and
  **People in your library** (the six with the most titles). OK on a card searches for it.
- Results: a 16:9 picture with the progress badge; a pill only where it adds words ("Resume S2 E3"); the second line
  starts with the year ("2025 · Movie · 1 h 52 min").
- Long-press or Menu on a result opens the library menu (Resume, Play from the start, Mark as watched, Fix match,
  Remove from library, Delete file).
- While your services are checked, "Looking on your services" under the streaming heading; it fades away if nothing
  turns up. Nothing in the library: a plain message in the middle.
- The Voice key listens in the search box itself ("Listening…"), filling in your words as you speak.

**Home**
- Progress is the corner badge everywhere (a pie, or a tick when watched); no more bars on artwork.
- Row cards: the title and one line over the picture; cards 40 px apart, focus grows 8 %.
- The hero is the full width (1816 px) with its text anchored to the foot, the same layout as Details.
- The current carousel dot fills up until the next title.
- The hero's More starts with Play / Resume and Play from the start, then Details, then This Title.
- Customise Home: Shown on Home and Hidden, with Move · Hide (and Edit on your own rows) on the row you are on; Sort
  by opens its list; Delete this row asks in the row itself and offers Undo.
- A press at the end of a row stays put.

**Details**
- Tabs: the focused tab is the white box; the open one keeps a white underline on a line that fades at both ends.
- More reads "TMDB page QR code"; Remove from library has its own icon; Delete file uses the new delete warning.

**Player**
- Playback Settings about 700 px wide.
- Settings › Playback › Seek preview: Off · Chapter pictures · Video frames (your old choice carries over).

**Library pages and delete warnings**
- Caption box `#141619`, a little shorter, its three lines evenly spaced; the toolbar line thins out at both ends.
- Delete warnings name the file, its size and where it lives; several versions or a show's episodes come with ticks,
  Select all and the total; Clean Up offers clearing viewing history, Trakt history (when connected) and removing an
  empty folder. "This can't be undone." shows only beside a focused Delete.
- Side panels: three or more choices step with Left / Right.

**Fixes**
- Deleting a file that was already gone from its server (M2): it is removed from your library and the message says
  "This file was already gone from <server>, so it's been removed from your library." A server that can't be reached
  is still a failure and keeps the file.
- A renamed connection keeps its new name (M6); every library page, your own included, shows its size in Files ›
  Your Library (M7), which also gains **By Source** and **Biggest Movies**.
- Diagnostics record why (item 9): every failure carries its reason, step and kind of source; an unknown reason says so
  with what was tried and the last thing that worked; the summary lists the top reasons; each event is recorded once
  (D15). Player errors are named in words ("file unreadable", "connection lost").
- New titles still being looked up are not counted as Not matched (M5). A title TMDB no longer has stops being asked
  about every half hour and is recorded as such, never as "Availability service unavailable" (D16).
- Scanning only when there is something to find (D14): coming back within 30 minutes starts nothing; after longer,
  this device is read again only when its last look is over 6 hours old, and servers likewise.
- Scans read every file the library knows even if you leave the app (a cut-short list made every file look new), and
  judge missing files folder by folder: one unreadable folder holds back only its own files (M4).
- A file found gone when it fails to play shows in Files › Library Care › Missing files at once (M3).
- Scan library narrates its steps: "Connecting to Seedbox…", "Looking in Seedbox · 42%", "Done · 3 new · 1 missing", or
  why it couldn't.
- The artwork edge is solid white, 1 px, and the focus ring 4 px (RULES §14).

## 0.158.9 (code 1589) — signed 26 Nov 2026 (run 45774774374): Home, player, Search and library pages, plus everything listed under 0.158.8
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
