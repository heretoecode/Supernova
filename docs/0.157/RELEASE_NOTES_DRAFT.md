# 0.157 release notes: running draft

Approved style (owner, 14 Nov 2026; notes §43): grouped under centred headings, plain words, a little detail, each
point on one line. Final text goes into `assets/foundation/release-history.json` at signing. Only work that is
coded is listed; it still needs a green build and the owner's Shield test before it counts.

## New Look
- A new Supernova mark: two open white rings on the app icon, the banner and the splash
- One loading ring, the same spinning mark, wherever Supernova is waiting
- Pop-ups, menus and side panels share one charcoal look; a list opened from a panel sits on top of it
- Menu headings are centred on a thin line, in Title Case
- Long lists get a search line above 12 items, with the keyboard beside the list
- Ticked items in long lists glide up under "Selected", A–Z; Back closes, no Done button
- The top bar shows where you are: the focused item in white, the open page underlined
- Every picture has a fine white edge; the one you are on grows and gets a bold white ring
- The keyboard has a Phone key, so you can type or paste from your phone's remote app
- Password boxes have an eye to show what you typed

## Files
- Network & Files is now called Files
- A small ring beside Files while a scan runs, or a white dot when something needs a look
- The "Scanning…" pill in the corner is gone
- All files access is asked for once, so deleting files and choosing folders stay inside Supernova

## Details
- Cast and crew in portrait photos, so nobody loses the top of their head; initials when there is no photo
- Tabs in one order: Details · Episodes · Extras · More Like This
- The line under the title reads age rating · year · length · genres, on Home and Details alike
- Episodes show "Season 2 · Episode 8 · Title" under the logo, as in the player

## Player
- Preparing playback shows the episode as "Season 2 · Episode 8 · Title" and the age rating
- Seek previews now work on Put.io and local files
- Stop playing at: pick a clock time, or the end of the episode or film
- Still watching? can ask after a number of episodes or after a few hours
- Play mode: Stop after this · Play next · Repeat this · Repeat all
- Skip intros and credits: Off · Show button · Automatic
- Chapters are numbered in order after intros and credits are marked
- Picture in picture is gone

## Faster And Steadier
- Supernova reopens on the page you were on, with the same item picked, after Android closes it
- Focus comes back after standby, and holding Down no longer throws you to the top
- No page rebuild when a scan finds nothing new
- Automatic scans rest 15 minutes after the last one and stay quiet while the app is closed
- A local import that has stalled no longer keeps "Scanning" up or the screen awake

## Fixed
- Started titles leave Recently Added
- The skip button shows the right length and goes after rewinding
- Where to Watch uses your device's country until you pick one
- The JustWatch credit in Settings no longer pretends to open something

## Settings
- Keep diagnostics for 1, 2 or 4 weeks
- Diagnostics record each step of deleting a file
- Issues Digest is gone: diagnostics are automatic

## Not in the draft yet (planned for 0.157, not coded)
The new Files page itself (built, switched off until finished), memory savings, the new player controls, subtitles ready ahead of time, the dead-connection fix, the move gesture everywhere.
