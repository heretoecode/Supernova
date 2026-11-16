# Media Transfers: everything decided so far (gathered 15 Nov 2026; roadmap: Undecided › Ready, 16 Nov)

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

## How copies run (owner, 16 Nov 2026; mock-ups `docs/design/proposals/transfers-settings/` v3, to approve)
A section on the Transfers tab, above Where copies go:
- **Keep free on this device: a percentage of the drive the copies go to** (5 · 10 · 15 · 20%), shown with the real
  size of that percentage on that drive (e.g. "10% · 100 GB"). Second line: "Small overruns are fine; bigger ones wait
  for your OK". **An overrun of up to 2% of the amount kept free goes ahead; anything bigger pauses the copy (never
  cancels it) and puts it in Needs your OK** at the top of the Transfers tab (Copy anyway · Choose another folder ·
  Cancel on its focused row). **No pop-up** (owner, 16 Nov: nothing forced): where you pressed Copy it quietly reads
  "Waiting for your OK · Transfers". A truly full disk stops with an error.
- **While you watch** (from the network or this device): Pause · Slow down · Carry on (default Slow down).
- **Scans** always get priority (no setting).
- **Full speed from ‹02:00›** and **until ‹07:00›**, inline (Left/Right in half hours; the first "from" step is Off).
  Between the times copies run flat out; otherwise gently, leaving room on the network.
- **Full speed now:** a button on the copying row (beside Pause and Cancel), not a setting.
- **Copies at once:** 1 · 2 · 3 (default 1).
- **Where copies go:** two inline rows, Movies and TV Shows, each showing its folder; OK opens the one folder picker.
- **Remove the network copy:** one switch for everything (owner, 16 Nov), off by default; Bring new episodes follows it.
- "Only copy from these connections": dropped.
- **The Files dot (owner, 16 Nov):** Files always opens on This Device; nothing is forced. Each tab needing a look carries
  its count badge (Transfers, Library Care); the question waits on that tab, at the top.

## Open questions to think about (owner)
- Anything to add to Transfers settings (e.g. how much free space to always keep, speed limit, which connections are
  allowed)?
- Should "Remove the network copy" be per transfer, per series, or one switch for everything?
- **Decided (owner, 16 Nov):** when the local folder fills up, **stop** (never remove anything), show an error and ask
  the user to choose another location.
- **Decided (owner, 16 Nov):** watched copies are **never cleaned up automatically**.
- **Decided (owner, 16 Nov):** a transfer problem shows as the **dot beside Files** in the top bar (the existing
  attention dot).
