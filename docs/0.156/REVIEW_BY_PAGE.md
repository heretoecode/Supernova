# 0.156 review — page by page, with what 0.157 changes

For the owner's Shield review of 0.156 (6 Nov 2026). Everything 0.156 changed is listed under the page you will be
on, with a label saying how much of it is worth reviewing, given what is already agreed for 0.157.
Step numbers (#) are from `SHIELD_CHECKLIST.md`; release items (R) are from `RELEASE_LIST.md`.

| Label | Meaning |
| --- | --- |
| **Review** | 0.157 does not change it. Every comment counts. |
| **Works only** | 0.157 changes how it looks. Say if it is broken or behaves wrongly; ignore the styling. |
| **Content only** | Settings: check what is there, the wording and what each setting does. Ignore the panel, layout and colours. |
| **Skip** | 0.157 replaces it. |

Everywhere: a single value or option in blue is **Skip** (0.157 removes blue values). Panel and menu colour and the
focus look are **Works only** (0.157 brings the new panel look and the menu rule).

---

## 1. Home

| What changed in 0.156 | Label | What 0.157 changes |
| --- | --- | --- |
| Hero text easier to read; title logo placement (R27) | **Review** | Hero becomes the Details hero size, with carousel dots |
| Hero keeps its title when you leave for Movies and come back (#7) | **Review** | — |
| Hero changing from one title to the next | **Works only** | Smooth, pre-loaded hero changes |
| Row cards (size, edge, focus ring) | **Works only** | One card size everywhere, faint 28% edge, white ring |
| Row controls: Hide and Move beside the row, moving live (R28) | **Review** | — |
| Customise Home side panel and genre icons (R29) | **Review** (ignore the panel colour) | Panel colour and focus look |
| Customise Home reopens on the row you left (#26) | **Review** | — |
| Top bar charcoal fade that follows the scroll (R30) | **Review** | — |
| Top bar words ("Network & Files", the dots) | **Skip** | Becomes "Files" with the scan ring, no dots |
| Documentaries: no blur or lingering colour in rows and top bar (R18) | **Review** | — |

## 2. Library pages (Movies, TV Shows, Documentaries, your own pages)

| What changed in 0.156 | Label | What 0.157 changes |
| --- | --- | --- |
| Edit Page: no scrolling, year range, separators, Delete Page at the bottom (R32) | **Review** (ignore the panel colour) | Panel colour and focus look |
| Edit Page reopens on the row you left (#26) | **Review** | — |
| Hide watched titles works on these pages | **Review** | — |
| Poster and row cards | **Works only** | Card size, 28% edge, white ring |
| Empty pages say what is going on (R43) | **Review** | — |
| (New in 0.157: library statistics on each page) | — | Nothing to review yet |

## 3. Details page

The whole page is redesigned in 0.157 (hero with description, readable tabs, Seasons as rows, Details as a reading
page, Extras and More Like This as Home-style rows). Report what is broken, not how it looks.

| What changed in 0.156 | Label | What 0.157 changes |
| --- | --- | --- |
| No flat blue card while the artwork loads (R11) | **Works only** | Whole page redesigned |
| Tabs remember the item you were on, and a scrolled tab keeps its place (#8) | **Works only** | Tabs redesigned; the memory stays |
| More › Fix match: side panel with posters, current match marked, keyboard folds away (#9, R40) | **Works only** | More forgiving search (typos, Trakt and IMDb numbers); panel look |
| More menu items | **Works only** | Restart not offered on unstarted films; a unique icon per item |
| Resume / Play wording on the hero | **Works only** | "Resume S1 E1 · 16 min left" |
| Ratings shown without the network, kept for a week (#44, R16) | **Review** | Reception column redesigned, but the saving stays |
| Remembered audio and subtitle choice per series (R42) | **Review** | — |

## 4. Player (the HUD and everything while a video plays)

0.157 barely touches the player (only the seek spinner), so nearly everything here counts.

| What changed in 0.156 | Label | What 0.157 changes |
| --- | --- | --- |
| HUD: large time over a grey chapter name, white seek bar, round glass buttons (#1, R19) | **Review** | — |
| Remote: Up to the buttons, Left/Right between them, OK on the bar plays/pauses, Down to the strip (#1, R20) | **Review** | — |
| Chapters · Episodes strip with a picture per part, current stretch lit (#2, R21) | **Review** | — |
| Seek preview pictures above the time bubble (#3, #32, R26) | **Review** | — |
| Loading spinner when seeking | **Skip** | Moved beside the time, reliable |
| Skip Intro button bottom right, draining, 10 seconds (R23) | **Review** | — |
| Our own first-play tip, once only (#6, R22) | **Review** | — |
| NOVA's blue "Press UP for the TV menu" box has gone (R17) | **Review** | — |
| Playback Settings panel: rows, tabs, what is in it (R10, R25) | **Review** (ignore the panel colour) | Panel colour and focus look |
| Playback Settings remembers your row per tab (#27) | **Review** | — |
| Subtitles: Vertical Position in 5% steps, live sample line (#4, R24) | **Review** | — |
| Download Subtitles inside the panel, no centre pop-up (R25) | **Review** | — |
| Sleep timer and "Still watching?" (#5, R39) | **Review** | — |
| Picture in picture: subtitles off, OK returns full screen, buttons (#33, #34, R15) | **Review** | — |
| Auto-next skips hidden episodes (#43, R12) | **Review** | — |
| Put.io: pause a few minutes, carry on, picture clear and speech in time (#31, R18a) | **Review** | — |

## 5. Search

| What changed in 0.156 | Label | What 0.157 changes |
| --- | --- | --- |
| One panel, QWERTY keyboard with symbols, voice (#10, R31) | **Review** | Same keyboard used on more pages |
| Recent searches, Clear recent, "What you can search" (#10, R31) | **Review** | — |
| Filters, grouped results, typed letters in bold, watched pill (R31) | **Review** | — |
| Panel colour and focus look | **Works only** | Global panel look |

## 6. Network & Files

**Skip the whole page** (owner, 6 Nov 2026). It becomes Files in 0.157 and is reviewed then. This covers
#11–#19 and #35–#39 (R33–R35, R41).
Still mention anything that plainly breaks in normal use: a film from a share will not play, a scan misses new
videos, Put.io stops syncing.

## 7. Settings

**Content only** (owner, 6 Nov 2026): Settings is redesigned as a full page in 0.157, but much of what is on it carries
over, and the current problems must not. Check what each page contains, its wording and what each setting does.
Ignore the panel, the layout and the styling (#30 is **Skip**).

| Page | What changed in 0.156 | Label |
| --- | --- | --- |
| Playback | Plain-English rows; Skipping (intro, recap, preview, outro, credits); Seek previews switch | **Content only** |
| Audio & Subtitles | Three tabs: Audio · Subtitles · Style; Style sample changes size, colour, position, outline, background, opacity; the next film uses them (#20) | **Content only** |
| Audio & Subtitles | During picture in picture, Style says to use the video's Playback Settings (#21) | **Content only** |
| Library & Home | .nfo export asks first, then shows progress (#29); Refresh details moved to Library Health | **Content only** |
| Where to Watch | Renamed; country, your services, show first | **Content only** |
| Appearance | Accent removed | **Content only** |
| Accounts & Services | Renamed; Trakt, OpenSubtitles, IntroDB tidy-up (R37) | **Content only** |
| System › Backup | Everything · Settings only · Custom; only Custom shows the parts; Back up now saves what the choice says (#22) | **Content only** (check a backup really works: #42) |
| System › Diagnostics | Full detail; Clear diagnostics asks first (#28); export includes scans and failures (#45) | **Content only** |
| System › Advanced | Seek previews (experimental), on by default | **Content only** |
| About | Credits for every service and component with QR codes (#23); release notes with 0.156 at the top (#24); licences with search; device; NOVA 6.6.4 (R14) | **Content only** |
| About | Going to another About tab and back keeps your place (#25) | **Content only** |
| Every page | Values and switches in step with their row and read out once (R13) | **Content only** |

## 8. Everywhere (any page)

| What changed in 0.156 | Label | What 0.157 changes |
| --- | --- | --- |
| One shared choice list instead of see-through pop-ups (R38) | **Works only** | Menu rule: unique icons, tick on the right, width to fit |
| Button icons follow focus; Save Changes tick no longer vanishes (R9) | **Review** | — |
| Coming back to a page or tab returns to where you were (R43) | **Review** | Also after Android restarts the app (F1, F6, F8, F10–F13) |
| No crashes when moving focus quickly (R5) | **Review** | — |
| Blue values | **Skip** | Removed |

## 9. Behind the scenes (no particular page)

| Check | Label |
| --- | --- |
| Stop an .nfo export part-way, run it again: stops promptly, no half-written file (#40) | **Review** |
| Scan both sources, send Supernova to the background and reopen: finishes or gives a reason; Stop works (#41) | **Review** |
| Back up the full library and preview it: all titles counted; an earlier backup survives a failed attempt (#42) | **Review** |
| Export diagnostics after the checks: opens and includes scans, reconnections and failures with reasons (#45) | **Review** |
