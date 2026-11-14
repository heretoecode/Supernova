# Settings — round 1: every setting, and what happens to it (13 Nov 2026)

Source: the code (`SettingsLayout.java`, the preference files) plus the owner's Settings walk-through (owner notes §20)
and every decision since. Settings becomes a **full page with tabs**, like Files: no tiles, no panel. Look and controls
come from `docs/design/RULES.md` (inline chips up to five, switches, the shared choice list, the folder picker, the
keyboard). **K** keep · **M** move · **J** merge · **R** reword · **X** remove. Nothing here is built yet.

## Proposed tabs (7, down from 8 tiles)

**Playback · Audio · Subtitles · Library · Services · System · About**

- Appearance goes (one setting; Interface language moves to System).
- Accounts & Services is split by where each thing is used (owner §20): IntroDB → Playback › Skipping;
  OpenSubtitles → Subtitles; Trakt joins Where to Watch in **Services**.
- Settings that live on their own page stay there: scan settings on Files › Library Care; Home rows in Customise Home.

---

## 1. Playback

**General**
| Today | Decision |
| --- | --- |
| Enable playback speed | **X** — speed is always in Playback Settings |
| Hide controls when paused | **K** switch |
| Open videos in another player | **K** switch |
| Reset brightness on start | **X** — a phone setting, no meaning on a TV |
| Projector mode | **M** → System › Advanced |
| Seek previews (experimental) | **M** here from Advanced, once seek previews work (P10) |

**Resume**
| Today | Decision |
| --- | --- |
| Display resume box | **R** "When a video was started" — chips: Ask · Resume · Start over |
| Save resume points | **R** "Remember where you stopped" — switch |
| Resume points on network | **J** into the row above (one setting for everything) |

**Skipping** — Intro, Recap, Preview, Outro, Credits: **K**, each as inline chips (Off · Show button · Automatic,
as in the player). **M** here: "Use IntroDB for skip times" (switch, was under Accounts & Services).

**Picture**
| Today | Decision |
| --- | --- |
| Automatic refresh rate | **K** chips (two choices) |
| Dolby Vision mode | **K** chips (three) |
| Automatic 3D TV switch | **X?** — only for 3D TVs (owner to confirm) |

## 2. Audio
| Today | Decision |
| --- | --- |
| Preferred audio language | **K** shared choice list |
| Prefer the original language | **K** switch |
| Surround sound | **K** chips (four); **J** "Force audio passthrough" (Advanced) into it |
| Virtual surround | **K** switch |
| Audio decoder (Advanced) | **M** here as chips (three) — walkthrough asked for inline |

## 3. Subtitles
| Today | Decision |
| --- | --- |
| Show subtitles (when a video starts) | **K** switch |
| Hide subtitles by default | **X** — the same switch the other way round |
| Show subtitles for local-language audio | **R** "Show subtitles when the audio is in your language" — switch |
| Preferred subtitle language | **K** shared choice list |
| Languages to search online | **K** shared choice list (several) |
| Prepare subtitles for the next episodes | **K** switch |
| Find subtitles when none are included | **K** switch |
| OpenSubtitles sign-in + switch | **M** here from Accounts & Services |
| Subtitle style (size, colour, position, outline, background, opacity, sample) | **K** as the player's Subtitle appearance, with the sample line |
| Default subtitle font | **K** choice list |
| Custom fonts folder | **K** — Supernova's folder picker, no Android screen |
| Render subtitles in black bars | **R** "Use the black bars" — switch |
| Subtitles encoding (Advanced) | **M** here; **R** "Text encoding", plain names ("Western European"), choice list |

## 4. Library
| Today | Decision |
| --- | --- |
| Hide watched titles | **X** — replaced by the Watched filter on each page |
| Ignore "The" and "A" when sorting | **K** switch; sort titles rebuild automatically when it changes |
| Recreate sort titles | **X** — nothing visible happened; done automatically instead |
| Find details automatically | **K** switch |
| Details language | **K** shared choice list |
| Use .nfo files | **K** switch |
| Save details as .nfo files | **K** switch |
| Export .nfo files now | **K** action (asks first, shows progress) |
| Make thumbnails for network videos | **K** switch |
| Use already scraped information | **X** — NOVA internal; always on |
| Ratings key (OMDb) | **M** → Services |
| Customise Home… | **K** as a shortcut row (opens Home with Customise Home) |
| Hero picks from: Recently Added / Trending / Popular | **K** three switches, heading "Home Hero" |
| Keep viewing history | **K** switch |
| Clear viewing history | **K** action, with the usual warning (Supernova / Trakt switches) |

## 5. Services (Where to Watch + Trakt)
| Today | Decision |
| --- | --- |
| Show where to watch | **K** switch (off removes every streaming feature) |
| Country | **K** shared choice list with flags; default from the device's region |
| Your services | **K** shared choice list (several), Selected A–Z |
| Show first | **K** choice list from your services |
| Show all services | **?** — owner to say what it should do, or **X** |
| JustWatch | **R** plain credit line, nothing to click |
| Trakt sign-in | **K** |
| Scrobble while watching | **K** switch |
| Sync resume points | **K** switch |
| Sync your library to Trakt | **K** switch |
| Force re-upload / Force re-import | **R** "Send everything to Trakt again" / "Fetch everything from Trakt again" — actions |
| Remove Supernova's data from Trakt | **K** action, with warning |
| OMDb ratings key | **M** here; typed with the keyboard, never shown in full |

## 6. System
**General** — Interface language (**M** from Appearance), shared choice list.

**Backup** — redone from scratch (owner §20): What to back up: Everything · Settings only · Choose; "Choose" lists
about six plain groups (Settings · Library and history · Home rows and pages · Connections · Accounts · Subtitle
fonts), not ~30 items; Back up now; Restore; the help is two short lines, not blue boxes.

**Diagnostics**
| Today | Decision |
| --- | --- |
| Diagnostic logging | **K** switch |
| Save a diagnostic report | **K** action; Supernova's folder picker, remembered folder |
| Clear diagnostics | **K** action, asks first |
| Keep diagnostics for (new) | **K** chips 1 · 2 · 4 weeks (default 2) |
| Issues Digest | **X** (owner §20) |

**Advanced** (grouped; plain words, one-line explanations)
| Today | Decision |
| --- | --- |
| Projector mode | **M** here |
| Force software decoding | **K** switch |
| Choose decoder | **K** chips |
| Parser synchronisation mode | **K** chips |
| Max I-frame size / Stream buffer size | **K** chips |
| Choose audio interface | **K** chips |
| Use AudioTrack for speed + PCM speed engine | **J** "Sound when speed changes": FFmpeg · Sonic |
| Dynamic audio/video delay | **K** switch |
| Disable / Force stereo downmix | **J** "Stereo downmix": Automatic · Always · Never |
| SMB: alternate implementation, SMBv2, resolver order, three discovery switches | **K** under a "Network Shares" heading |
| Newer SFTP implementation | **K** switch, same group |

## 7. About
Release notes (each version's number and date; one change per line; OK opens any version), Credits, Open-source
licences (searchable; QR code to the full text — owner to confirm), App information. Fixes from §20: Up from the first
item reaches the tabs; no "Find in notice" flash; no odd version lists.

---

## Questions for the owner
1. Seven tabs as above (Playback · Audio · Subtitles · Library · Services · System · About)?
2. Remove the Automatic 3D TV switch?
3. "Show all services" — what should it do, or remove it?
4. Licences: full text in the app, or a QR code to it?
5. Keep a "Customise Home" shortcut in Settings, or only on Home?

## Owner's answers (13 Nov 2026)
- Automatic 3D TV switch: **removed**. "Show all providers" toggle: **removed** (the services list shows everything).
- Licences: **QR code** to the full text, not in the app. Customise Home: **only on Home** (its button at the bottom of
  Home already exists); the Settings shortcut goes. Seven tabs: judged on the mock-ups. Mock-ups started.

## Owner on batch 1 (13 Nov 2026) and answers
- Seek previews: marked **Experimental** beside its name; diagnostics to record performance with it on and off.
- Open videos in another player: kept for others who may use it, **moved to System › Advanced**. When on, Play shows
  Android's "Open with" list of your video apps.
- Skip times: two services today — **TheIntroDB** (by TMDB id) and **IntroDB** (by IMDb id + season/episode). Files can
  mark intros and credits as named chapters (common on anime and some rips); the services fill in when they don't. Row
  reworded "Look up skip times online".
- "Full subtitles when the audio is in your language": off = only forced subtitles (signs, foreign-language lines) when
  the audio already matches your subtitle language; on = full subtitles even then.
- Several languages show their flags. Appearance on this page = the **defaults**; Playback Settings' "For This Series"
  saves per show.
- **Smarter subtitle fetching (new for 0.157):** today Supernova only *checks* availability, for the rest of the season.
  Change: fetch the subtitle **while Preparing playback** so it is there when the video starts, and prepare **the next
  two episodes** only (never a whole season), spaced to respect OpenSubtitles' limits. Row "Have subtitles ready".
- Panels drawn (`settings-v2/06`): OpenSubtitles (account, downloads left today, Change account, Sign out), Font
  (built in, then from your folder, each previewed), Text encoding (Automatic, then by language with flags). Custom fonts
  folder uses the shared folder picker (`network-files-tabs/40`).
- **Owner, 13 Nov (batch 1, round 2):** Open videos in another player **stays in Playback › General** (Advanced is not
  a bucket); it opens **Supernova's own "Play With" list** (apps that can play video, "Always use this choice") instead of
  Android's. Skipping keeps **four** choices, mapped from 0.156: Play normally → **Off**; Show skip (5 s) → **Show
  button**; Auto skip → **Skip**; Smart → **Skip when binge-watching** (skips automatically when you've just come from the
  previous episode, otherwise shows the button). Experimental label → RULES. Default Appearance matches the player's
  Subtitle Appearance one for one (size, colour, position, outline, background, opacity); Font, fonts folder, black bars
  and encoding are global only. OpenSubtitles: signed out shows only Sign in; signed in shows Change account (sign in
  as someone else — one account at a time) and Sign out; the sign-in form is username and password fields with the
  keyboard panel beside (`settings-v2/07`).
- **Foreign-language lines (owner's report):** with "Full subtitles…" off and English audio, Supernova picks the forced
  track; if the file has none, foreign lines show nothing. Fix for 0.157: when no forced track exists, fetch an
  OpenSubtitles "foreign parts only" subtitle automatically; if none, fall back to the full track.
- **Skip button (owner, 14 Nov; diagnostics 23:44–23:52):** the button works (23:48:30, OK skipped to 5:30). Faults found
  in the code: (1) it is shown **10 s**, but the setting says "5 seconds"; (2) after rewinding to **before** the intro
  starts it stays on screen (it only checks "before the end of the part"); (3) with the controls open it floats at a
  fixed height that no longer matches the new controls, so it jumps and overlaps; (4) its pressing is not in
  diagnostics. Design (`player/29`, `30`): normal-size button; without controls bottom right, focused so one OK skips,
  a thin bar drains over 10 s; with controls it sits in the button row beside Settings until the part ends. Shown
  only while the position is inside the part. Diagnostics log shown / pressed / dismissed.
- **Subtitles for every language spoken:** a downloaded English subtitle file subtitles whatever its makers translated —
  normally every non-English line, and usually not the English ones. Supernova shows that file in full whatever the
  audio. The "only forced" behaviour applies to subtitle tracks **inside** the video file when the audio is already in
  your language. Proposal: make "Full subtitles when the audio is in your language" **on by default**.
- OpenSubtitles signed out: one-line note; **QR code to create a free account** under Sign in (`settings-v2/08`); Play
  With's doubled line fixed.
- **Approved (owner, 14 Nov 2026):** Skip button lives in the **rightmost slot of the control row** (… Settings ·
  Skip intro); without controls it sits on exactly that spot; with nothing to skip the slot is gone and the seek bar
  takes the room. OpenSubtitles QR code approved.
- **Subtitles for other languages inside a show (owner):** OpenSubtitles marks each subtitle with flags such as
  hearing-impaired (SDH), **foreign parts only**, machine / AI translated and trusted uploader — but **no flag for "translates
  every language spoken"**. Plan for 0.157: when the main English subtitle is on, also look for a **"foreign parts only"**
  English subtitle for the same release and, if found, **show both together** (merged on screen), as with a built-in
  forced track. In Download subtitles, show pills for SDH and Foreign parts only. SDH files more often note other
  languages ("[speaking French]").
- **Subtitles › When To Show (owner, 14 Nov 2026):** Show subtitles; Full subtitles when the audio is in your language
  (on by default); **Also translate other languages — Experimental** (on by default): adds lines in other languages from
  a built-in forced track or an OpenSubtitles "foreign parts only" file, merged on screen. Build it, then judge.

## Owner on batch 2 (14 Nov 2026) and Claude's answers (awaiting confirmation)
- .nfo: "Save details as .nfo files" = from now on, write one for each newly matched title; "Export .nfo files now" =
  once, for everything already there. Reword: **"Write .nfo files for new titles"** (switch) and **"Write .nfo files for
  the whole library"** (action, progress in the row: "412 of 1,000 · Stop", then "Done · 1,000 written").
- **Rule proposal: › only when a row opens a list or page.** Actions show their verb as plain text without › (Clear,
  Send, Fetch, Save, Back up); progress and "Done" appear in the row itself; warnings where data is lost.
- Home Hero: started titles already leave the hero (code: HeroPlaybackEligibility). At least one source must stay on.
  Possible extra sources: Because You Watched, Your rows; "Change every 8 · 12 · 20 s".
- Show first folds into Your services: Selected list is ordered with the move gesture; the top is shown first.
- Trakt "Sync your library" = Trakt's *collection* (what you own), separate from watched history.
- OMDb key: no label needed; if the key is built in later the row simply goes.
- Backup: Everything = all except private data (passwords, file locations); Settings only = every setting, no Home rows,
  pages, history or matches; Choose = a list of about six groups. Back up now / Restore / Save report use a remembered
  folder (changeable in its own row), progress in the row, no Android notification.
- Network shares options (alternate SMB2+, SMBv2, resolver order, TCP / UDP / mDNS discovery, newer SFTP) **move to
  Files › Connections** as an "Advanced" row at the bottom.
- About: build date; "Based on NOVA 6.6.4"; a version opens a pop-up like Full description, one change per line; list the
  last five versions plus "All versions".
- Your services: the hint moves above Selected and reads "Your first choice goes at the top. Press OK on a ticked service to move it." (`settings-v2/22`). Library and Services tabs approved (owner, 14 Nov).
- **Owner on batch 3 (14 Nov 2026):** Backup Choose = the ~25 parts grouped into six (confirmed). Restore: OK on Restore
  lists the backups in the folder → one opens "In This Backup" → Restore this backup → back on System with progress in
  the Restore row; when done the row offers **Restart now** (library and history need a restart to finish). About: the
  app information row goes — its unique facts join the header ("Built … · based on NOVA 6.6.4 · Android TV 11"); free
  space and library counts live on Files. Credits: each service opens a pop-up with what it supplies, any required
  wording (e.g. TMDB's notice) and a QR code to its site; Open-source licences opens a pop-up with the QR code. Network
  options: a line under every option; rows centred. Your services list sized to its content, hint wrapping inside it
  (rule reminder: menu width = longest item + standard padding). Release notes pop-up approved. Mock-ups
  `settings-v2/23`–`26`.
- **Settings design approved (owner, 14 Nov 2026).** Credits pop-ups approved; Open-source licences opens its QR pop-up
  straight over the About page. Seven tabs confirmed by the pictures. **All redesigns are now designed:** Search, Files,
  Details, Player and Playback Settings, the shared choice list, library pages, Home, Settings.
- **Open-source licences list (owner, 14 Nov 2026).** The About row opens a floating, grouped list of the 71 components
  (name and licence type on each row, search row because over 12); OK on one opens its pop-up with a 170 px QR code
  to that licence's full text. One QR code on screen at a time (RULES §20). Mock-up `settings-v2/27`.
