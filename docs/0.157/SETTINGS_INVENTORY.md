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
