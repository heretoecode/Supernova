# 0.158 part B: where it stopped (paused by the owner, 19 Nov 2026)

The owner paused part B: what is in the build running now (0.158.7) is finished and signed; everything else waits
and is picked up again from a discussion session. Full detail: `REPORT_0158_B.md` (per item) and
`JUDGEMENT_CALLS_0158_B.md` (61 decisions made without asking, each with its reason).

## Versions
- **0.158.4** — signed (run 44531272454); installed on the Shield.
- **0.158.5, 0.158.6** — never signed: the render checks found faults that would have stopped Settings opening;
  fixed, and their contents moved into 0.158.7.
- **0.158.7** — building (conformance run 44586168686, source `861ba66`), signed when green. Holds everything below
  under "Done".

## Done (in 0.158.7), not yet seen on the Shield
- **Playback Settings round two** (in 0.158.4): 820 px, every tab fits, stepping rows, Skipping chips, This Show.
- **Panels** at 99%, no outline or glow (in 0.158.4).
- **Settings:** the sheet's wording and choice names; skip rows Off · Show button · Skip · Skip when binge-watching
  (which now skips every part when binge-watching); Home Hero's Because you watched and Change every; Keep
  diagnostics for; When a video was started (Ask · Resume · Start over); Save a diagnostic report in its row (to
  Download › Supernova); action rows show their verb; the OpenSubtitles panel redrawn with a QR code; the Trakt
  sign-in in Supernova's look; NOVA's dud resume-box switch removed.
- **Details:** DT1 (Details tab on one screen), DT3 (cards not cut; Episodes and Extras rows to the screen's edges),
  DT5 (More Like This in rows by service; heading icons), DT6 (artwork side panel for TV shows), R3 (season artwork),
  R4 (season More menu with Delete season), R5 (Play asks which version), the episodes feature (OK plays,
  long-press / Menu opens its menu, deleting asks about earlier episodes).
- **Titles:** logo placement (one area inside 900 × 240) and house style A for names without a logo.
- **Files:** FL1 (nothing under the bars), FL2 (placeholders), FL3 + FL5 (turn a connection off and on; smaller
  buttons), FL4 (form on one screen), FL7 (your own page in Your Library), FL8 (dot only with a badge), FL9, FL10
  (All scans scrolls), FL11 (already in), FL12 (closed fully → Home).
- **Player:** PL8 (Skip button the small-box size).
- **Scanning:** X4 (scans wait their turn instead of timing out), as a new MediaLib patch.
- **Checked in renders this session:** Playback Settings (matches v15), Files connections and form, Library Care,
  Details tab. Not yet seen in a render: episodes menus, titles, R5's menu, More Like This by service.

## Paused (not built), with the reason
| Item | Why it isn't built |
| --- | --- |
| In-row backup flow (What to back up chips, Backups folder, list of backups, progress and Restart now in the row) | Changes where backups are kept (Android's picker → a chosen folder); a mistake loses data, so it wants its own careful pass and a Shield check. Backup still opens its old view. |
| "Also translate other languages" (Subtitles) | A player-engine feature (a second "foreign parts only" subtitle merged on screen) that can only be judged on the Shield. |
| Trakt sign-in as a side panel | Works and now looks like Supernova, but still opens full page; the panel means rebuilding the device-code flow. |
| Reports folder › Change (and Backups folder) | **Owner's decision needed:** on Android 11 Supernova can only write into Download or a folder granted once through Android's own folder picker. Keep Download › Supernova, or Supernova's folder picker plus one Android grant? The one folder picker (RULES §7b) isn't built yet either. |
| Renders of the episodes screens, titles, R5's menu, More Like This by service | Not yet run; due before the owner reviews them. |
| R2 (solid bar on your own library page) | Nothing to code: it now uses the same code as Movies and TV Shows. Owner to check on the Shield. |

## Notes for whoever picks it up
- Session branch: `claude/0158-part-b-continued-d5u9w1`; the working branch is fast-forwarded to it only when a
  build is due (a push there starts conformance and cancels a running one).
- MediaLib patches live in `.github/build/` (ignored by `.gitignore`: add with `git add -f`) and must be listed with
  their SHA-256 in `.github/build/supernova155-dependency-patches.json`.
- Run `preview-renders.yml` on the session branch with the touched tests before a long build: it caught every fault
  this session in about four minutes.
