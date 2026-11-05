# Preview 4.1.3 — physical Nvidia Shield QA

All checks below are **NOT TESTED — USER QA REQUIRED** until performed on your Shield.
Emulator and Robolectric evidence in the return package is not physical-device evidence.

## First checks

1. Install over Preview 4.1.2; keep the existing library and settings.
2. Open Home, Movies, TV Shows, Details, Search, Settings and Network & Files.
3. Check neutral unfocused navigation, accent text/icon with a soft focused glow,
   no underline, and visible Settings-cog feedback. Check cards brighten on focus,
   list rows glow without an unfocused box, and keyboard letters remain readable.
4. Featured has only More Info. Left/Right changes the item directionally without
   requiring another action; More Info opens its Details page. Watch the first
   official-logo frame and the header while scrolling. Check Customise Home size.
5. Check both Movie and TV Details against the approved poster-free design: title
   logo or clean text fallback, narrow synopsis, grouped metadata, one action row,
   compact unboxed cast and See All. Test real provider handoffs and More Options.
6. More Info should be centred, wide/short, with the Details page dimmed/frosted
   behind it. No X; Back closes it and returns focus to More Info. See All exposes
   the complete available cast/crew. Unsupported awards remain absent.
7. Compare Movies/TV total/local/network counts and sizes with indexed media.
   Specifically check the WebDAV Parasite file and a WebDAV episode. Mixed-source
   TV series may occur in both source subtotals; total unique shows is deduplicated.
8. Inspect Network & Files and its modern browser for the reported background seam.
9. Search Shōgun and unaccented Shogun: parent-series matching must remain available,
   with Top Result as a label and View Show as an action. Check Columns size.
10. Settings: verify contextual three-panel behaviour and restored projector,
    torrent download folder and torrent blocklist controls where applicable.

## Playback and diagnostics

1. In Settings → Advanced, enable Diagnostic Logging before reproducing a problem.
   Logging is OFF by default. Export Diagnostic Report uses Android's document
   picker; select a retrievable destination. See the diagnostics guide for limits.
2. Play a TV episode. Open Information/Synopsis → File and technical details.
   It should remain inside the player, show available snapshot metadata and not
   restart Supernova. Repeat with missing/limited metadata and a movie.
3. Check the HUD's outlined Play/Pause, retained D-pad graph, direct Audio/Subtitles,
   current track labels, remote seeking, compact nested menus and subtitle Settings
   at the bottom. First Back dismisses the HUD; second Back exits playback.
4. Check exact top-right wording: Episode Ends HH:MM / Movie Ends HH:MM.
5. Check real official title/logo perceived size and transparent visible bounds.
6. During normal viewing, note any unexpected return to Details, A/V drift or lost
   progress. Record approximate time and media source type, then export promptly.

## Still open — do not treat as fixed

- Random playback termination / return to Details.
- A/V synchronisation problems over long playback.
- Progress persistence following abnormal playback exit.
- Exact exception behind the supplied Shield TV technical-information crash:
  the video contains no Java/native trace. The unsafe cross-activity/probing route
  has been replaced, but the original exception mechanism is not proven.

No Smart Rows, automatic subtitle triggers/acquisition, Profiles or deferred 4.2
features are included. Existing safeguards remain in place.
