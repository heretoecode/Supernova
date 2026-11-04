# Settings review (Claude, 4 Nov 2026) — from the owner's photos of all eight sections

Owner brief: review the whole of Settings; move, remove, rename or add settings, sections and whole pages as needed.
Everything here is a proposal for the owner, then mock-ups, then go-ahead. Nothing is coded.

## A. Problems that appear on every page (fix once, everywhere)
1. **Values and switches one row out of step** (item 16): each value/switch is drawn level with the row above.
2. **Everything said twice**: the value appears under the title and again on the right.
3. **Left-hand section list does not match the app's buttons**: pale blue blocks where only the icon changes on focus
   (owner note, Playback & Video). Use the standard charcoal button: charcoal at rest, white with dark text and icon on
   focus, a small white bar beside the open section.
4. **Translucent pop-ups** for every choice (Diagnostic Level, Report a Problem category, IntroDB modes…): replace with
   the shared solid side-panel choice list (item 19.1).
5. **Full-screen Android screens break out of Supernova**: the Custom fonts folder picker and the Export file picker
   open the stock Android Files app in white. Use Supernova's own Local Storage browser where possible (folder picking),
   and keep Android Files only where Android requires it (saving a file), with a Supernova screen before it that says
   what is about to happen.
6. **Engineering wording shown to users**: summaries like "Transport and scan preferences; no logins, trust keys or
   sources", "logical media identities", "no invented watch counts", "Bounded local reports exclude credentials",
   "Legacy: … presentation control retired pending device QA". Rewrite every summary as one plain sentence saying what
   the setting does for the viewer.
7. **Walls of text**: Backup & Restore Details and Restore Instructions are dense legal-style paragraphs. Replace with
   short, scannable sections (what's included · what isn't · what happens on restore), each a few lines.
8. **Language flags**: some languages in the Preferred audio / subtitle language lists have no flag of their own
   (owner note). Give every listed language a flag (or a neutral language badge where no country fits, e.g. Latin,
   Esperanto, Sign languages).

## B. Section by section

### 1. Playback & Video
- Keep: resume behaviour, auto-play next, intro/outro skipping link, video/display options.
- Move the IntroDB skip modes here from Connections (they are playback behaviour); Connections keeps only the
  on/off switch for the service.
- Add (approved suggestions): **Sleep timer default**, **"Still watching?"** (after N auto-played episodes, on/off).
- Rename sub-sections in plain words: *Playback*, *Resume*, *Picture*, *Skipping*.

### 2. Audio & Subtitles
- Keep: preferred audio and subtitle languages (with complete flags), default subtitle behaviour, subtitle style.
- Subtitle **style** moves to a single page with the **live preview** we approved for the player panel (item 4), so
  Settings and the player use the same editor.
- **Custom fonts folder**: open Supernova's folder browser, not Android's; show the folder chosen and a count of fonts
  found; a *Clear* option.
- Remove anything that duplicates the in-player Playback Settings panel without adding a default.

### 3. Library & Home
- **Home rows** is just a link to Customise Home. Keep the entry but make it obvious: "Customise Home…" with a short
  line, opening the approved side panel (item 22). Remove the separate "Clear Watch Next" from here — it lives in
  Customise Home › Watch Next.
- **Rescrape all / Rescrape all movies / Rescrape all collections**: "rescrape" means *fetch the details and artwork
  again*. Proposal:
  - Rename to **Refresh details** and move to **Network & Files › Library Health**, next to the other library
    maintenance tools, as: *Refresh all details*, *Refresh films*, *Refresh TV shows* (each with a confirmation and a
    progress line). Settings keeps no copy.
  - **Rescrape all collections** is NOVA's *film collections* (franchise groupings from TMDb such as "The Matrix
    Collection"), not user collections. Supernova never shows film collections, so remove it.
- Keep: library view memory, hidden-titles management, watched-state options.

### 4. Streaming → rename **Where to Watch**
- Owner: "Streaming" implies Supernova streams. It shows where a title is available on services you choose.
- Rename to **Where to Watch**; first line explains: "Shows which of your services carry a title, on its Details page."
- **Refresh provider list** (to be confirmed in code before the mock-up): re-downloads the list of services available in
  your country from TMDb/JustWatch. Make it automatic (weekly, and when the country changes) and drop the button, or
  keep it only as a small "Updated 3 days ago · Refresh" line.
- Keep: country, chosen services (with their logos), show/hide on Details.

### 5. Appearance
- **Accent & Colour** — what it changes today (from the code): the focus glow/rail/underline colours, the hero's
  indicator dots, the keyboard's highlight, card and Details accents, the player's progress/loading accents and Up Next,
  and a faint utility-page gradient. It does **not** change the white focus box, charcoal panels or text, which the
  0.153–0.154 style made fixed — so today it tints a handful of small details, inconsistently.
  - **Recommendation: remove it** and fix one accent (the current blue used for progress and selection). The app's look
    is now a deliberate single design; a picker that changes a few stray details invites mismatches and doubles the
    testing. Alternative if the owner wants personalisation: keep it but make it change exactly one defined set
    (progress bars, selected toggles, watched-pill fill) and show a live preview.
- **User interface language**: keep.
- Add: **Screensaver** (approved suggestion) — on/off and what it shows (library backdrops).

### 6. Connections
- **Service integrations**: one row per service — Trakt, OpenSubtitles, IntroDB — each showing *Connected as … /
  Not connected* and an on/off switch, opening its own page (sign in / sign out, options). Fix the misaligned switches.
- **Network connections** page duplicates Network & Files › Network Shares (and the approved Saved Connections merge).
  Remove it from Settings; leave a single row "Network shares are managed in Network & Files" that jumps there.
- **IntroDB** page keeps only the service switch; the skip modes move to Playback & Video (see 1).
- **Trakt sign-in** screen: keep the code + QR approach; restyle in the panel design.

### 7. System
- **Compatibility pages** (Audio, Network, Subtitle, Video compatibility): device-specific expert options. Group into
  one **Advanced** page with a clear "Only change these if something isn't working" line.
- **Backup & Restore**:
  - **Bug:** "Backup could not be prepared. Existing backups and application data were retained." on the owner's
    Shield — creating a backup fails. Needs investigating (diagnostics report) before 0.156.
  - The category list (20+ checkboxes with technical summaries) becomes three choices: **Everything** (default),
    **Settings only**, **Custom…** (the full list, renamed in plain words, grouped: Settings · Home & library pages ·
    Viewing history & progress · Matches & preferences · Sources & locations).
  - "Filenames and locations" stays off by default with one plain warning line.
  - Replace "See full details" and "Restore instructions" walls of text with a short **What's in a backup** page.
- **Diagnostics**:
  - Keep: Report a Problem (with category), Export Diagnostic Report, Diagnostic Logging on/off.
  - **Issues Digest** shows raw process IDs and timestamps — developer output. Move it behind Advanced, or turn it
    into a plain summary ("12 scan requests, 0 artwork failures in this session").
  - **Show Latest Reference** duplicates the screen Report a Problem already shows; fold it into Report a Problem
    ("Last report: 06f6050a · Playback · 16:48").
  - **Diagnostic Level** (Normal / Detailed (QA)): QA is for testing; keep it under Advanced.
  - Remove the redundant first row "Diagnostics — Local logging, problem references and report export" (it repeats the
    page title).

### 8. About Supernova
- **App information** and **Technical Information › Application & Build** repeat the same facts. Merge: App information
  shows version, "Based on NOVA 6.6.x" and build date; Technical Information keeps device, decoders, storage.
- **Release Notes**: entries read like internal QA notes ("Shield QA candidate — … physical verification pending").
  Show only released versions, written for viewers ("Charcoal menus; clearer hero text; new Local Storage").
- **Open-source Licences**: correct and complete; the list is long and the notice screen has a stray blank "Find in
  notice" bar. Add a search, group into *App libraries* and *Playback engine*, and tidy the notice header.
- **Credits & Acknowledgements**: good. Keep.
- **Technical Information › Diagnostics** ("No new diagnostic viewer or upload is added") is an engineering note —
  remove it; link to System › Diagnostics instead.
- Technical Information › Library & Storage: "Cached count is not an authoritative full-library scan" → plain wording.

## C. Proposed new layout (landing tiles)
| Playback | Audio & Subtitles | Library & Home | Where to Watch |
|---|---|---|---|
| **Appearance** | **Accounts & Services** (was Connections) | **System** | **About Supernova** |

Same eight tiles, two renamed. Inside each, the same page pattern: left section list in the standard button style,
right a list of rows with one value or switch each, aligned, said once, with one plain line of explanation.

## D. Bugs found during the review (for 0.156)
1. Backup cannot be prepared on the owner's Shield.
2. Values/switches one row out of step (every page).
3. Duplicate first row on Diagnostics; "Find in notice" blank bar on licence pages.
4. Missing language flags.
