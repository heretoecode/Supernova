# 0.157.2: judgement calls made overnight (for the owner to read)

Each line: what was unclear, what was chosen, and why. Any of these can be changed later.

1. **Diagnostics report:** not in `docs/INBOX.md` and no "Inbox:" issue, so the crashes at about 23:09 / 23:14 and
   the blank screen at about 23:39 (16 Nov) were worked on from the review's description (Back while focus had jumped
   to the top bar) without the report. The read of what the owner did not see waits for the report.
2. **Branches:** the session's own branch is `claude/confident-goldberg-7502kz`; every push also goes to the working
   branch `claude/supernova-github-access-yfhe89`, because the signing workflow only accepts conformance runs there.
3. **Version history:** 0.157.2 replaces 0.157.1 as "current" in `release-history.json` (point releases are not
   listed separately in the history, as for 0.157.1).
4. **Files: Grid / List, Sort, Order and the filter are remembered** between visits (like the library pages'
   "remember views"), so a change sticks. Default: List, Name A → Z, Videos only.
5. **"Videos only / All files":** the button names what is shown now. Videos only shows folders and videos; All
   files adds the rest, dimmed and not focusable (they can't be played: RULES §10). Default Videos only (cleaner).
6. **List rows have no inline buttons**: the sheet's Browsing section gives files and folders a long-press (or
   Menu) menu beside them; inline buttons (old `02`) would duplicate it. OK opens or plays.
7. **File menu items only when they do something:** Details, Mark as watched and Fix match appear only for a title
   in the library (RULES §10). "Mark as unwatched" replaces "Mark as watched" for a watched title.
8. **Folder counts:** a folder on this device counts its videos where they are (any depth, up to 5,000 files);
   a folder on a connection shows how many of its videos are in the library (counting over the network would read
   every folder). "Folder" alone when there are none.
9. **Exclude and Add to library** use Supernova's own library choices (the same ones the old browser saved), not
   NOVA's older "blacklist"; Add to library adds the folder straight away (no Movies / TV Shows question; the
   library sorts titles itself).
10. **Plain card for files not in the library:** the plain card (approved `03`) is used; a still taken from the
    video is not built yet (see the report's "not built").
11. **Connection names** are kept on the device (as a short hash, no address stored as a key); new connections get
    the name suggested from the address (webdav.put.io → "Put.io"), changeable with Edit.
12. **Save and browse** saves the login (if Remember password is on), adds the connection to the library (so it is
    scanned, as every existing connection is) and opens it.
13. **Remove** takes the connection out of the library at once; the notice row "Put.io removed" with **Undo** stays
    at the top of Your connections until you leave the tab.
14. **Test** shows "Connected" or "Not reachable: no answer within 15 seconds" in the row's second line.
15. **Found on your network › Open** browses the device; if it needs a login the folder says it can't be opened
    and **Add** (form with the address filled in) is the way in. A login prompt on Open is not built.
16. **Check matches** pill reads "Not run yet", "Checking 120…", "No changes · 17 Nov" or "4 to review"; OK while
    checking stops it.
17. **Scan one source "Waiting"**: sources pressed while a scan runs wait in order; the row's second line names
    them ("Put.io is waiting for its turn").
18. **Couldn't be read › Try again** asks the next scan to look at that folder again; Exclude leaves the file out.
19. **Not matched › Not a movie** hides the video from Not matched (and the badge) on this device.
20. **Back from the top bar** while Files is inside a folder goes up a level in Files rather than leaving the app.
21. **Details More menu:** Subtitles and Artwork still open the existing Supernova choice list (download / choose
    subtitles, posters / backdrops); the approved Artwork side panel (D3) is not built yet. "Delete file" uses the
    shared delete warning (the history switches are not built). TMDB page uses the "external" icon (there is no QR
    icon yet).
22. **Restart** appears only when the title has been started (sheet); the Playback group then disappears.
23. **Service titles in More Like This** open the service's app at its start; opening the exact title needs the
    title link, which is fetched only for the hero's button (engine work, 0.158).
24. **Netflix and Disney+ (D9):** the title link now goes to the app with Netflix's TV hand-off; when the app
    refuses the link it opens at its start, never Android's "No application can handle this action". Not tested
    on a Shield (no device here).
25. **Playback Settings width (P4):** one width for every tab, 40% of the screen (the recommendation). At that width
    the Playback tab's longest label shortens ("Skip In…").
26. **Background (F27):** Home and the library pages lose the full-page artwork behind them and are plain charcoal,
    as asked; the hero card keeps its own picture.
27. **Details Runtime for a show:** TMDB's show runtime when it has one, else the middle episode runtime across the
    seasons' episodes ("Episodes · about 47 min").
