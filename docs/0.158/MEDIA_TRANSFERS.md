# Media Transfers: everything decided so far (gathered 15 Nov 2026)

Moved whole into **0.158** (owner, 15 Nov 2026): wanted, but not urgent, and the owner wants time to think of more
settings and improvements. Sources: `docs/0.157/OWNER_NOTES.md` §23–24 and §28, mock-up
`docs/design/proposals/network-files-tabs/35-v14-transfers.png`.

## What it is for
Copy films and episodes from network shares (e.g. Put.io, a NAS) onto this device, so they play from local storage;
optionally delete the network copy once the local copy is safe.

## How it works (decided)
1. **Copy.** Start a copy from Files (and, in stage 2, from Details and the library long-press menu).
2. **Where copies go:** two default folders, one for **Movies** and one for **TV Shows**, chosen once with the app's one
   folder picker ("Choose a folder for Movies"; Use this folder · Open; New folder; "Current" pill).
3. **Free-space check** before a copy starts.
4. **Checked when done:** every copy is verified; a finished copy reads "Copied and checked".
5. **Same title:** the copy is recognised as the same film or episode (history, progress and matches carry over; no
   duplicate in the library).
6. **Gives way to playback:** transfers run while you use the app, even during playback, but slow down or pause by
   themselves while you play something from the network, so playback never stutters ("Waiting").
7. **Resilience:** carries on after a restart, a power cut or a dropped connection; part-copied files resume.
8. **Move = delete the network copy** after a verified, correctly matched copy. A switch, **"Remove the network copy",
   off by default**; failed deletes and empty folders handled.
9. **Playback itself is not a download:** playing streams only what it needs and keeps nothing.

## How it looks (decided, Files › Transfers, the fourth tab)
- **Copying now:** progress bar (known progress uses a bar, RULES §21), Pause · Cancel.
- **Waiting:** copies giving way to network playback, or queued.
- **Finished:** "Copied and checked".
- **Where copies go:** Movies and TV Shows rows, each opening the folder picker.
- **Remove the network copy:** switch, off by default.
- Progress and "Done" show in the row itself; no Android notifications.

## Later stages (decided, also 0.158)
- **"Bring new episodes to this device"** per series: one item in Details › More ("Bring new episodes to this device" /
  "Stop bringing new episodes"). Whenever a scan finds a new episode of that series on any connection, it is copied,
  checked, then (if chosen) the network copy deleted. It follows the series, not a folder, so it still works when
  nothing is left on the network. The Transfers page lists the series that have it on, with a way to switch it off.
- **Schedule:** an overnight window plus "Start now" (owner wants it).

## Known risk
- Running while the device sleeps. The owner has run a Plex server on this Shield while it slept, so background work
  is possible; test early.

## Open questions to think about (owner)
- Anything to add to Transfers settings (e.g. how much free space to always keep, speed limit, which connections are
  allowed)?
- Should "Remove the network copy" be per transfer, per series, or one switch for everything?
- What happens when the local folder fills up: stop, or remove the oldest watched copies?
- Should watched copies ever be cleaned up automatically?
- Notifications inside Supernova when something finishes or fails (a badge on Files, a line on Home)?
