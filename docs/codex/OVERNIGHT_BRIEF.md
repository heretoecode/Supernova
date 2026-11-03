# Combined brief for Codex — everything queued, in one run

Six parts, **in order (A → F)**. Every change is its own commit, so partial progress is kept if you run out of
time; always do "Before you finish". The owner reviews everything before it is merged.

| Part | What | Changes code? | Proof |
|---|---|---|---|
| A | Fix 34 misleading indentations (lint errors) | layout only | `check_format_only.py` |
| B | Re-apply 94 `@Override` | labels only | `check_imports_only.py --allow-added-override` |
| C | Remove leftover BitTorrent code | yes (owner-approved) | owner's full build + tests |
| D | Port NOVA 6.6.3 app changes | yes (owner-approved) | owner's full build + tests |
| E | Bug hunt — every line, 4 areas | **no** (report only) | — |
| F | Dead-code map — what can never run | **no** (report only) | — |

## Setup
1. Start from the tip of branch `claude/supernova-github-access-yfhe89`.
2. Create a new branch `codex/overnight-YYYY-MM-DD` (today's date; if it exists, add `-2`, `-3`, …). Push only
   to that branch. No pull request, merge, rebase or force-push.
3. Never edit `.github/`, `tools/`, build files (`*.gradle`, `gradle.properties`) or `assets/`. Edit `res/` only
   where Part C names a file. In `docs/` only add your report and the findings files.
4. No Gradle, builds or tests. If something needs a judgement call, skip it and write the question in the
   report.

## Part A — fix 34 misleading indentations (layout only)
Android lint reports 34 `SuspiciousIndentation` errors in
`src/main/java/com/archos/mediacenter/video/leanback/settings/PreviewSettings.java`: a brace-less
`for`/`if`/`while` whose single-statement body sits at the **same** indentation as its owner, e.g.
```java
for (String key : new String[] { … })
move(root, audio, key);          // body: must be indented one level deeper
```
Indent each such body one level (4 spaces) deeper. Fix the same pattern anywhere else in `src/`. **Never add
braces.** Commit per file `Fix misleading indentation in <FileName>.java (layout only)`, then
`python3 tools/check_format_only.py --base claude/supernova-github-access-yfhe89 --files <path>` must say `ok`.

## Part B — re-apply the 94 `@Override` labels
`docs/codex/reports/2026-11-03-overnight-2026-11-03-5.md` ("Files restored after the single checker attempt")
lists nine files whose additions were rejected only by a checker bug, now fixed. Same rules as before (only
where certain; `@Override` above any other annotations). Commit per file `Add missing @Override in
<FileName>.java`, then `python3 tools/check_imports_only.py --base claude/supernova-github-access-yfhe89
--files <path> --allow-added-override` must say `ok`.

## Part C — remove leftover BitTorrent code (owner-approved)
Supernova has no BitTorrent feature; the torrent engine is already excluded from the APK. Remove what is left:
- `browser/filebrowsing/BrowserByFolder.java`: drop `application/x-bittorrent` from the file filter and the
  mime check (lines ~103 and ~697) so `.torrent` files are no longer listed as videos.
- `leanback/details/VideoDetailsFragment.java` (~line 1883): remove the torrent special case so the file-info
  task runs for every file (the `.torrent` branch can no longer occur).
- `leanback/settings/PreviewSettings.java` (~line 96): remove the `"torrent"` title mapping.
- `res/layout/player.xml` and `res/layout/floating_player.xml`: remove the `torrent_status` view **only if**
  nothing in `src/` references `R.id.torrent_status` (if something does, remove that reference too and say so).
- `res/raw/logback_full.xml`: remove the `TorrentObserverService` logger line.
- `res/xml/preferences_licences.xml`: remove the `libtorrent` licence entry (it is not shipped).
- Any torrent-only strings in `res/values*/strings.xml` that nothing references any more.
**Keep:** `PlayUtils.unsupportedTorrent` and its "BitTorrent playback is no longer supported" message (a
deliberate guard if someone opens a `.torrent` file), and the torrent keys in `utils/BackupPrivacy.java` (old
backups may still contain them). One commit per file, message `Remove leftover BitTorrent code from <file>`.

## Part D — port NOVA 6.6.3 app changes (owner-approved)
Our last upstream sync was NOVA 6.6.2. Clone `https://github.com/nova-video-player/aos-Video` and port, **one
upstream commit at a time, oldest first**, the commits from `842ac7592ea9188b60a34acd08a450b88d0c4545`
(exclusive) up to and including `c4f39331` ("6.6.3"). Supernova's code is heavily modified, so adapt each
change to where the same logic now lives; never overwrite Supernova code wholesale.
- **Port:** the cold-start crash fix (`17542651`, wait for libavos before creating the subtitle engine); the
  intro/outro skip changes (`fb1a20fd`, `915347c7`, `f4ebc6f9` and related) wired into Supernova's existing
  "Skip Intros & Credits" setting, keeping Supernova's current default; subtitle outline/shadow defaults
  (`73327baa`); subtitle association when rename fails (`e1642b6b`).
- **Skip and list in the report** (they need native-engine or design work the owner does by day): the subtitle
  box-padding setting (`73df6e4d`, `b777104f` — needs native `avos` changes and a place in Supernova's own
  Subtitles tab); the subtitle-settings pill fix (`65fc9b28` — NOVA's settings UI, which Supernova replaced);
  translation syncs (`crowdin sync`); NOVA documentation-only commits; NOVA's version bump.
- Do **not** port anything after `c4f39331` (HDR/decoder work pairs with native-engine changes done by day).
- Commit each port as `Port NOVA <short sha>: <upstream title>`; in the commit body, say what was adapted.

## Part E — read-only bug hunt: every line of `src/main/java`
Read every `.java` file under `src/main/java/com/archos/mediacenter/video/`, in four areas, and write one
findings file per area under `docs/codex/findings/`:
1. `area1-playback-details.md` — `player/`, `leanback/details/`;
2. `area2-settings-foundation-utils.md` — `leanback/settings/`, `foundation/`, `utils/`;
3. `area3-network-files-streaming.md` — `leanback/network/`, `leanback/filebrowsing/`, `browser/filebrowsing/`,
   `streaming/`;
4. `area4-library-home-rest.md` — everything else (rest of `leanback/`, `browser/`, `collections/`, `tvshow/`,
   `autoscraper/`, `metadata/`, `info/`, `cover/`, `picasso/`, `widget/`, `ui/`, `diagnostics/`, `debug/`, the
   top-level classes).
Also read the lint findings for each file: `git fetch origin reports/lint`, then
`git show origin/reports/lint:lint-findings.json` (filter by `file`).
Look for real defects, not style: **crashes** (null/empty assumed present, index out of range, bad casts,
unguarded I/O or parsing, use after destroy); **leaks** (listeners, receivers, handlers, cursors, streams,
bitmaps, threads never released; static references to Activity/View/Context); **threading** (UI off the main
thread, disk/network/database on the main thread, unprotected shared state); **logic** (inverted conditions,
off-by-one, copy-paste mistakes, ms/s or dp/px mix-ups, state not reset between videos, settings read but never
applied); **security/data** (secrets in logs, certificates accepted blindly, data lost on error).
Table, one row per distinct problem:

| ID | File:line | Kind | Severity (crash / user-visible / minor) | Confidence (high / medium / low) | What goes wrong, for whom, when | Evidence (lines and why) | Suggested fix (described, not applied) |

Read the code as it is after Parts C and D. Change no code. End each file with the list of files read. Commit
per area: `Bug hunt <area>: read-only findings`.

## Part F — read-only dead-code map
Supernova replaced many NOVA screens (the phone/tablet browser, the old leanback details page, old grids and
dialogs) but their code is still in the app. Map what can **never run** in Supernova. Start from the entry
points: every `<activity>`, `<service>`, `<receiver>` and `<provider>` in `AndroidManifest.xml` (note which are
exported or have intent filters — other apps can start those), `EntryActivity`, `CustomApplication`, and the
Supernova screens (`leanback/Preview*`, `leanback/settings/`, `player/`). Follow references (including
`Class.forName`, reflection, XML `android:name`, `getIdentifier`) and settings that switch between old and new
UI (e.g. `try_new_ui` — find what it does and whether Supernova can ever turn it off).
Write `docs/codex/findings/dead-code-map.md`:
- a table of removal candidates: class/resource, why it is unreachable (the evidence), what references it,
  size (lines), risk (low/medium/high) and anything that would have to change with it;
- a separate list of things that **look** dead but are reachable (and how), so they are not removed by mistake;
- a suggested removal order (safest first).
Change no code. Commit `Dead-code map (read-only)`.

## Before you finish
1. Proof for Parts A–B: run `python3 tools/check_imports_only.py --base claude/supernova-github-access-yfhe89
   --allow-added-override --files <every file changed in Parts A and B>` — every file `ok`.
2. Add a **new** report `docs/codex/reports/YYYY-MM-DD-<branch suffix>.md`: per part what was done; Part C files
   and what was removed; Part D each upstream commit ported, adapted or skipped (with reason); Part E findings
   count per area by severity and confidence; Part F number of candidates and total lines; the exact checker
   output; anything odd. Commit and push.
