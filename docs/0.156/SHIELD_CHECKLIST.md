# 0.156 — what to check on the Shield

One list for the whole version: the screens first (Claude), then the engine (Codex,
`SHIELD_CHECKLIST_ENGINE.md`, copied in unchanged as items 31–45). Each step is one thing to do and what "good" looks
like. Tick what passes; for anything that does not, a photo and the step number is all that is needed.

## Player
1. Start a film: the controls show the large time over a grey chapter name, a white seek bar and round glass buttons;
   Up goes to the buttons, Left and Right move between them, OK on the bar plays or pauses, Down opens the strip.
2. Press Down: the Chapters · Episodes strip shows pictures for each part, with the current stretch of the bar lit.
3. Seek: a picture appears above the time bubble (when Seek previews is on).
4. Open Playback Settings › Subtitles: Vertical Position moves the subtitle up and down live in 5% steps; the sample
   line shows size, colour, outline and background as you change them.
5. Sleep timer and "Still watching?": set a timer and let it run; after three auto-played episodes the question
   appears and playback stops if nobody answers.
6. First play on a fresh install shows our own tip once, never again.

## Home, Details, Search
7. Home: leave for Movies and come back — the hero is on the same title.
8. Details: Extras or Seasons & Episodes, move to an item, switch tab and back — focus returns to that item, and a
   scrolled tab keeps its place.
9. Details › More › Fix match: a panel opens on the right over Details (Details still visible), with the file name at
   the top, posters, year and kind, and the current match marked. Moving into the results folds the keyboard away;
   Up brings it back. OK picks a match and Details refreshes.
10. Search: QWERTY keyboard with symbols, recent searches, "What you can search", filters and voice.

## Network & Files — skip (owner, 6 Nov 2026)
The owner will not review this page in 0.156: it is being completely redesigned as Files in 0.157 and will be
reviewed then. Steps 11–19 and 35–39 are skipped. Anything that plainly breaks in normal use (a film from a share
will not play, a scan misses new videos) is still worth a mention.

11. Local Storage: every row shows In library / Excluded / Not added; one watched pill; grid has artwork.
12. In Local Storage, Details on a file that is not in the library: it looks the film up and shows it with
    "Not in your library yet", Play and Add to library (or "Couldn't identify this file"). Nothing is added until you
    press Add to library; afterwards the file appears in the library.
13. Use a row button (Details, Play), come back: focus is on that same button.
14. Network Shares: the Put.io row says who is signed in, storage used, and "Library up to date · checked … ago";
    switch the network off and Retry Put.io appears.
15. Focus a saved connection: Open · Edit · Test · Remove appear. Edit opens the form filled in (password hidden);
    Test answers "Connected · N folders, M videos found" or names the field to check; Remove asks first.
16. Add Connection opens "choose what you are connecting to": Put.io (Already connected), A computer or NAS (Find on
    my network · Enter address), WebDAV, SFTP, More types.
17. On the Add Connection form, Test connection with a wrong password: "Check the user name and password." Nothing is
    saved.
18. Library Scanning and Library Health: plain words, Scan now shows live progress and can be stopped.
19. Go from any Network & Files section up to the top bar and back down: focus returns to the row you left.

## Settings
20. Audio & Subtitles has three tabs: Audio · Subtitles · Style. Style shows a sample line and changes size, colour,
    vertical position, outline, background and opacity with Left/Right; a film you play next uses them.
21. With a video playing in picture-in-picture, Style says to use the video's Playback Settings instead.
22. System › Backup › What to include: Everything · Settings only · Custom. Only Custom shows the parts list. Back up
    now saves what the choice says.
23. About › Credits: lists every service (TMDB, OpenSubtitles, Trakt, Put.io …) and every open-source component with
    version and licence; OK shows its details and a QR code.
24. About › Release Notes: 0.156 at the top ("In development"), then 0.155 and earlier.
25. About: open Licences, move to an entry, open another About tab and come back — the same entry is selected.
26. Customise Home and Edit Page: close and reopen — focus is on the row you were last on.
27. Playback Settings: move to a row in one tab, switch tab and back, press Down — the same row.
28. Diagnostics: "Clear diagnostics" asks first and then empties the log.
29. .nfo export from Library & metadata asks first, then shows progress.
30. Every Settings page: the single-panel layout, tabs at the top right, plain-English titles and one-line
    explanations.

## Engine (Codex)
31. Play a Put.io film, pause for three minutes, then carry on: the picture stays clear and speech stays in time.
32. Seek forwards and backwards in a large 4K Put.io film: preview pictures appear without interrupting playback.
33. Put a subtitled film in picture in picture: subtitles disappear there and return when you press OK to go full
    screen.
34. Reopen Supernova while picture in picture is showing: the same film returns full screen at the same point.
35. In Library Health, use Find it again for a moved file: the correct file opens and its watched state and saved
    place remain.
36. Test an unsaved SMB connection: the folder and video counts are correct, and a wrong password gives a clear reason.
37. Test an unsaved SFTP connection: the folder and video counts are correct, and a wrong folder gives a clear reason.
38. Test an unsaved FTPS connection: the counts are correct, and an untrusted certificate is refused with a clear
    reason.
39. Export .nfo files to a writable share: the count advances, the files arrive beside the videos, and the final totals
    agree.
40. Stop an export part-way through, then run it again: it stops promptly, leaves no half-written file and can finish
    normally.
41. Scan both library sources, send Supernova to the background and reopen it: the scan finishes or gives a reason;
    Stop works and a fresh scan can start.
42. Back up the full library, then preview that backup: all expected titles are counted and the previous backup
    remains usable if a new attempt fails.
43. Hide the next episode and let the current one end: playback skips the hidden file, including when repeating a
    folder.
44. Open a title's saved ratings without a network connection: the saved values remain; reconnecting within a week
    does not fetch them again.
45. Export diagnostics after these checks: it opens normally and includes the scan results, playback reconnection and
    any failures with reasons.
