# 0.157.1 on the Shield: the owner's review (started 16 Nov 2026)

**Plan (owner, 17 Nov):** fix as many bugs as possible in **0.157.2** before 0.158 starts. No new designs in 0.157.2,
with one exception: the background (F27).

The owner installed 0.157.1 (code 1571): "a lot better than it was; not perfect". Reviewed one redesigned area at a
time (Files, then Details, Player, shared pieces). Numbered F1… for Files. "Decided" names where it was already
settled; "To decide" waits for the owner. Photos are taken at an angle: alignment is checked in renders.

## Files

### Working
- Plain charcoal page; the blue background and glass panel are gone.
- Moving along the tabs switches the page straight away.
- Library files show their artwork in the folder grid, with the In library pill.
- Opening a drive from This Device keeps focus where it should be.

### Problems (already decided: fix to match)
- **F1. Up from a tab's content can't reach the top bar** from the tab row; only Back does. RULES §3/§4.
- **F2. Files reopens on the last tab used.** Decided v12 (6 Nov): Files always opens on the first tab.
- **F3. The old panel's frame is still there, invisible:** the right of the tab row (facts and the scan button) is
  cut off where the panel edge was, and focused folder cards are clipped at the same edge. Content must use the full
  page width (RULES §1).
  **Confirmed in the code (17 Nov):** the new Files page is placed as one cell inside the old page's scrolling list
  (`PreviewPages.filesLayout`), and that list keeps its old side padding (28 dp each side) and clips at it. So Files
  sits in an invisible frame: anything reaching past it (the tab row's right end, a grown card) is cut, and the scroll
  bar is drawn at the frame's edge, on the content. The old shared browser broke out of this with negative margins;
  the Files page does not. Fix: Files fills the page itself, not a cell of the old list, **keeping its side margins** (96 px at 1080p, lined up with the top bar; owner, 17 Nov); only the clipping goes. Today's visible margin is the old list's padding plus Files' own, so removing the frame must not narrow it: keep the margin exactly as it looks now (owner, 17 Nov). Only the clipping goes, so a grown card or ring may reach into the margin and the scroll arrows sit there (likely also behind F1 and F9,
  as the old list still handles focus around it).
- **F4. Scroll bar sits on the content** (the old panel's edge), covering the right of the rows. See F15.
- **F5. No List view, and grid is the default.** Approved: list is the default (`network-files-tabs/02`) with a
  Grid / List button that shows the other view (`03`, `32`). QUESTIONS #14.
- **F6. Sort is a toggle** (Name ↔ Newest). Approved: a Sort menu with its choices and mirrored order icons
  (`network-files-tabs/11`; v10).
- **F7. No actions on a file or folder in the grid:** long-press does nothing and release plays; nothing adds a
  folder. Approved: the focused list row shows its buttons (Play · Details · Mark as watched · Exclude, `02`) and the
  Menu button opens the same actions (`28`). See F16.
- **F8. Folder cards show a plain blue-to-black gradient.** Approved: a mosaic of the artwork inside, or a plain card
  when there is none (v4, `32`).
- **F9. Focus jumps to Home in the top bar** every time a folder opens or Back goes up a level, while browsing
  inside a drive (not on This Device's list). The page loads, the Files underline stays, but the white box lands on
  Home; Down brings it back. RULES §3: focus never jumps.
- **F10. Two crashes, at about 23:09 and 23:14 local time,** both pressing Back while focus had jumped to Home (F9).
  Needs the diagnostics report from the Shield.
- **F11. Back after playing a file returns to This Device's list,** not the folder it was played from. Changing tab
  and back also loses the folder.
- **F12. Re-choosing the open tab doesn't reset it:** after F9, choosing This Device still showed "This folder is
  empty"; Back then walked the breadcrumb.
- **F13. Grey text on the white focus box** (the "This device" row). REVIEW_0157 #20 was marked fixed; it isn't on
  the device.
- **F14. The internal storage row has no storage bar,** while the USB drives do (two lines against three). Every
  storage row gets the bar (consistency).

### To decide (Claude's recommendation in brackets)
- **F15. Scroll bar:** owner (16 Nov): no scroll bar (a PC thing; nothing to click with a remote), but keep a sign
  that there is more below. Soft fade **declined** (17 Nov): the half-cut row already shows it. Owner's idea: a sign in
  the empty right-hand margin. Options drawn (`files-review-0157-1/02`): A one down arrow at the foot of the list;
  B the arrow with how many are left; C up and down arrows (up faint at the top). **Owner (17 Nov): C, for now** (revisit once built); no scroll bar. No arrows when the page fits.
- **F16. Long-press (approved 17 Nov, `files-review-0157-1/01`):** a folder's menu is Open · Add to library · Scan this
  folder · Exclude; a file's stays Play · Details · Mark as watched · Exclude · Fix match.
  Was: should a long-press of OK open the row's actions menu, as the Menu button does, everywhere
  (Files grid and list, libraries)? [Yes, one gesture app-wide. Release without holding still plays.]
- **F17. Up always goes to the open tab** (owner, 16 Nov: agreed): from anywhere in a tab's content, Up into the tab
  row lands on the open tab, never the tab above the focus (today Connections or Library Care + Up opens Your
  Library). Rule for every tab row, Details included. To add to RULES §4.
- **F18. Tab-row facts removed:** the summary on the right ("1,934 Videos · 3 To Check · …") repeats the Library Care
  badge, Library Health and Your Library, and won't fit as tabs grow. **Owner (16 Nov): remove; always keep a scan button
  in the tab row, locked right** (until the room is needed for tabs); the badge stays. **17 Nov:** "12 min ago" reads as
  information, not a button: it says **"Scan library"** (every local and network source). While scanning it reads
  "Scanning · 42%" (OK stops); the last scan time lives in Library Care › Recent scans. **Approved 17 Nov.** Replaces RULES §7b "Tab row facts" and v3/v6/v7. The latest design (v19, `network-files-tabs/44`,
  13 Nov) already shows only the scan button on the right ("⟳ 12 min ago": OK scans now, Stop while scanning; "Scanning
  Put.io · 42%" during a scan; §28 of the 0.157 notes). [Remove the facts on every tab; keep that scan button alone.]
- **F19. "This device" row renamed "Internal storage"** (the tab is already This Device). [Yes.]

- **F20. File captions (approved 17 Nov):** title alone on line one (long titles end in "…"); line two "year · size"
  (e.g. "2024 · 14.2 GB"); size only when the year is unknown. In a folder view the size matters more than the year.
  Replaces `32` (title and year). Folders: count and In library / Excluded / Not added pill on every folder (approved).
- **F21. Your Library › Picture: show every quality** (owner idea, 16 Nov): 4K, 1080p, 720p, SD and HDR, not only
  "% 4K · % HDR". (The 0% figures are right: 1 of 420 watched, two titles in 4K.)
- **F22. Folder actions not drawn:** the row menu (`28`: Play · Details · Mark as watched · Exclude · Fix match) is for
  files. A folder's menu (e.g. Open · Add to library · Exclude · Scan this folder) needs a mock-up.

- **F23. Folders have no count and no pill:** a folder shows neither "86 videos" nor In library / Excluded / Not
  added. Approved (`32`, `02`): every folder has its count and its pill, like files.
- **F24. Folder artwork:** no mosaic yet (see F8). Files not in the library have no TMDB artwork, so v4 approved a
  picture taken from the video file itself (a frame from a few minutes in, like a thumbnail), or a plain card when one
  can't be made. Owner to test what shows today. For network files this is Settings › Library and Home › "Make
  thumbnails for network videos" (kept as a switch in the Settings redesign): off means network files get the plain
  card, because taking a picture means reading part of each file over the network. Plain card drawn and **approved 17 Nov**
  (`files-review-0157-1/03`): charcoal-grey card, the film (or folder) icon, name and size, the usual pill.
- **F25. Sort:** approved menu (`11`): Name A–Z / Z–A, Newest / Oldest first, Largest / Smallest first. Owner asks for
  sort choices plus an ascending / descending button. Library pages already split them (Sort · Order). Drawn (`files-review-0157-1/02` D):
  Sort: Name · Date modified · Size, and an Order button beside it whose words follow the sort (A → Z / Z → A, Newest /
  Oldest first, Largest / Smallest first). **Approved 17 Nov.** Folders always first.
- **F26. List view "not yet":** the fix pass for REVIEW_0157 #25 built the grid and stopped (QUESTIONS #14); it was
  left unfinished, not dropped. List is the default.
- **F27. Background differs between pages:** Settings, Search and Files share one; Home and the library pages look
  different (no longer Space Black, but not the same either). **Owner (17 Nov): in 0.157.2, Home and the three library
  pages get the same plain charcoal background as Files, so Settings, Search, Files, Home and every library page match
  exactly.** Only the background changes; their redesigns stay in 0.158.
- **F28. Screen went blank for a moment at about 23:39 local time,** then came back (no reload). For the diagnostics.

### Connections tab (17 Nov, from about 00:13)
- **F29. The Reachable pill jumps left on the focused row** to make room for the buttons. Approved (`04`): the pills
  sit in one fixed column on every row, focused or not (RULES §7).
- **F30. Row buttons wrong:** Open's icon is invisible; Edit and Remove icons are white on the light buttons; Edit has
  a list icon (approved: pencil); Test is missing. Approved (`04`): Open · Edit · Test · Remove, icons inverting with
  the text (RULES §12). Focused-row text and pill are grey, not black (see F13).
- **F31. Remove empties the page** (about 00:24): only the top bar is left; Up goes to Documentaries; choosing Files
  again brings it back. Approved (`24`): the row goes at once with an Undo notice.
- **F32. Edit does the same** (page vanishes). Approved (`08`): the connection's form as a page in the tab.
- **F33. Open shows an old NOVA screen, then the old three-panel browser.** Approved (`16`): the connection opens in
  Files like a drive (breadcrumb, list / grid).
- **F34. Add a connection opens NOVA's "Browse network share" page, then the old "Add a network source" pop-up** with
  Android's keyboard and old Show password · Save password · Cancel · OK. Approved (v8): focusing Add a connection
  shows the types inline on its row (Computer or NAS · WebDAV · SFTP · FTP); each opens its form page with the one
  keyboard (RULES §15: Phone key, eye on password), Test connection and Save and browse (`08`, drawn before the
  keyboard rule: the app uses the one keyboard).
- **F35. Found on your network: every device opens the same NOVA page.** Approved (`25`): the focused device shows
  Add, which opens the form with its address filled in; already added ones say Added.
- **F36. Scroll arrows everywhere (owner, 17 Nov):** option C (F15) is the rule app-wide; no scroll bars anywhere.
- **F37. Your Library:** looks right (read-only, nothing focusable) apart from the scroll bar (F36).
- **F38. Connection rows lack the account name and second line:** approved (`04`) "Put.io · <account>" with
  "WebDAV · 1,204 videos" underneath; the build shows the title only.
- **F39. Add a connection, changed (owner, 17 Nov):** no inline type buttons and no extra button to name: the row
  itself opens the form (› on the right, RULES §8). The form starts with a **Type** row (Computer or NAS · WebDAV ·
  SFTP · FTP, inline chips since there are four), and the fields below follow the type. **Owner (17 Nov): "Computer or
  NAS" renamed "SMB"** (bare, like the others; the help line says "SMB: shared folders on a Windows PC, Mac or NAS" when it is chosen). No "(S)" on the chips (SFTP is not encrypted FTP);
  the switch names it instead: WebDAV "Encryption (HTTPS)", FTP "Encryption (FTPS)", on by default. Replaces v8's inline types
  (`06`). Mock-up `files-review-0157-1/04` (2). Awaiting the owner.
- **F40. Found on your network opens to browse (owner, 17 Nov):** NOVA browses a found device straight away; Supernova
  should too. OK (Open) browses it in Files like a drive, asking for a username and password only if the device needs
  them; **Add** saves it as a connection (form with address and type filled in); once saved it moves up into Your
  connections and leaves Found on your network (owner, 17 Nov; no "Added" rows). The › is right because OK opens it.
  Replaces `25` (Add only; its › on unfocused "New" rows was inconsistent). Mock-up `files-review-0157-1/04` (1).
- **F41. Photos 00:25–00:27:** Open on a connection shows NOVA's "Library Sources" cards, then the old three-panel
  browser (F33); Add a connection shows "Saved Locations › Browse a network share", then the old pop-up and Android's
  keyboard (F34).

- **F42. One side margin for every page (owner, 17 Nov; for 0.158):** Home, library pages and Files each have a
  different margin today (Files the widest). Settle one value with the 0.158 redesigns; in 0.157.2 only the frame goes.

### Library Care tab (17 Nov, about 01:15)
- Working: the badge (3) matches Missing files (3 files); Check matches runs and its pill updates ("Last: 17 Nov");
  Scan one source starts a scan without odd pages.
- **F43. Rows not on their centre line:** pills sit across the row lines and the › arrows are half a row too high
  (a stray › above the first row). RULES §7.
- **F44. Check matches opens a page even when nothing changed** ("Everything looks fine: every film still finds its
  own match"); focus also jumped to Home (F9) and the text says "film" (RULES §6: Movies). Owner wants the result
  inline. [Run it in the row: progress, then the pill says "No changes · 17 Nov"; only when there are changes does
  the row say "4 to review" and OK open the review page.]
- **F45. Missing files, Couldn't be read and Not matched open the old three-panel screen.** Approved: one page each in
  the tab (`12`, `13`, `14`) with Find it again · Remove / Try again · Exclude / Fix match · Not a movie.
- **F46. Rows with nothing to show still open** ("None"). RULES §10: never offer what does nothing. [A "None" row has
  no › and OK does nothing.]
- **F47. Scan buttons' icons invisible,** focused or not (see F30).
- **F48. Scan one source shows a "This device" button while not focused;** the other buttons appear only on focus.
  RULES §7: buttons show on the focused row only.
- **F49. Two focuses:** moving Up from Scan one source to Check matches leaves the white box on Scan one source.
  RULES §3: exactly one focus.
- **F50. Recent scans can't be reached** (each row has a "Done" pill; focus never gets there). Approved: the last three
  as plain rows, then Show all scans (focusable) for the last ten.
- **F51. Pressing several scan buttons:** nothing visible happens after the first (queued or ignored?). Approved scan
  rules: one at a time. [Each pressed source shows "Waiting" in the row until its turn.]
- **F52. One connection per server (owner idea, 17 Nov):** the owner added Films and TV (one WebDAV server) and Movies
  and TV Shows (one SFTP server) as four connections, so Scan one source shows four buttons. Proposed: group by server.
  [A connection is the server and its login; the folders chosen are part of it. Connections shows one row per server
  (second line lists its folders), Scan one source one button per server, named by the connection's name, not its
  type, so two WebDAV servers stay apart.] Awaiting the owner; likely 0.158.

### Confirmed
- The breadcrumb ("Backup 1 › … › Movies") is a signpost, not focusable; Back goes up one level (v6, 6 Nov).
