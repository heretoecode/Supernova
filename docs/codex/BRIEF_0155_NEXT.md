# Codex brief — 0.155 next run, re-ordered (owner approved 2026-11-04)

This brief **replaces the order** of `docs/codex/BRIEF_0155_MORE.md`. Its rules 1–5 still apply in full; read them
first. Anything in MORE that is not listed below waits until this list is done. Work from the top, as far as you
get; one commit per item (or sub-item where stated); stop cleanly and record where to continue.

## Rules (in addition to MORE rules 1–5)
- Continue on **`codex/0155-code-2026-11-04`** (pull first). Push only to it. No PR, merge, rebase or force-push.
- Read `docs/codex/HANDS_OFF.txt` again before starting; it may have changed. Reading hands-off files is fine;
  editing them is not.
- Allowed new files outside `docs/codex/` are only: new `.github/build/` files whose names contain `supernova155` or
  `supernova156` (remember `git add -f`: `.gitignore` ignores `build`), new `.github/workflows/supernova-0156-*.yml`
  files, and new tests under `src/test/`. Never modify or delete existing files under `.github/`.
- No UI or design work (MORE rule 2). Anything that changes what plays, what is shown or how a screen behaves is a
  finding for the owner and Claude, not a fix.
- Do not re-report U1, N1, N2, L1, P1, U2, L2, DP-01…DP-10.

## The list
1. **Native engine permanent archive** — MORE item 8, exactly as written there (manual-dispatch workflow
   `.github/workflows/supernova-0156-native-archive.yml`, run 40843685431, SHA-256 checked against the existing
   lock/verification files, Release `native-archive-0146`, plus `findings/native-archive-plan.md` saying how the build
   switches to the release and how to verify it). Do not run it. Deadline context: the artifacts expire 27 Jan 2027.
2. **Fix DP-08** (`player/Player.java:701` and `:532`): never log the raw source URI; log a bounded protocol/outcome
   instead. Captured-logger test with user-info, signed query, fragment and private path fixtures; source open still
   attempted. One commit, ID in the message.
3. **Fix DP-10** (`player/UpdateNextTask.java`): cancelling an obsolete lookup must stop its listing (owned call
   cancellation / deadline, cooperative traversal cancellation) without shutting shared playback/network clients.
   Tests: silent and trickling listing (MockWebServer), repeated replace/cancel, worker ends, no late callback.
   Do **not** change which file is chosen (DP-09 is Claude's). One commit, ID in the message.
4. **Security and privacy audit** — MORE item 12 as written (`findings/security-audit.md`), with special attention to
   other log lines like DP-08 (URLs, usernames, tokens, Put.io links) and how WebDAV/SMB/FTP/SFTP passwords and OAuth
   tokens are stored and backed up. Fix only purely internal items (e.g. log redaction), each with a test, one commit
   each. Put.io code is hands-off: report only.
5. **Local scan every 15 minutes — analysis only.** On the owner's Shield the local scan checks 61,050 media rows in
   about 168 seconds and restarts every 15 minutes. Write `findings/local-scan-cadence.md`: every trigger that starts
   it (alarms, WorkManager, observers, receivers, file-system watchers, network/put.io scheduling, MediaLib), why it
   repeats at that interval, whether each run is needed, what it costs (CPU, disk, database, wake locks), and a ranked
   list of fixes described in words with the tests that would prove them. Hands-off files may be read and quoted.
6. **Finish the player hunt** — MORE item 5: full reads of `player/PlayerActivity.java`, `PlayerController.java`,
   `PlayerService.java`; append to `findings/deep-hunt-1-player.md`; one commit.
7. **Upgrade safety tests.** Tests (no production changes) that an upgrade from 0.153/0.154 keeps: saved settings
   and preference keys (including renamed/removed ones from the TV-only stages), the MediaLib database schema and
   migrations, network shortcuts and saved credentials, user collections, hidden items, resume points, and backup
   files made by older versions restoring correctly. Report any failure as a finding, do not fix in the same commit.
8. **One release pipeline (draft).** The per-version scripts (`.github/build/verify-0154-scope.py`,
   `verify-next-qa-0154.py`, `verify-supernova154-native-apk.py`, `record-supernova-0154-conformance.py`,
   `.github/signing/supernova_0154_qa_signing.py`, `verify_supernova_0154_qa_apk.py` and the 0154 workflows) are
   copied each release. Draft version-neutral replacements as new files named `*supernova156*` under `.github/build/`
   and `.github/workflows/supernova-0156-*.yml`, taking the version from one place, with the same checks. Do not
   wire them in or run them. `findings/release-pipeline-plan.md`: what each replaces, how to switch, what to test.
9. **Upstream NOVA survey (report only).** `findings/upstream-nova-survey.md`: commits in upstream aos-Video,
   aos-MediaLib and aos-FileCore since our fork points that fix bugs (crashes, scanning, scraping, network, playback,
   security). Per commit: what it fixes, whether Supernova already has it, conflict risk, take/skip with reason.
10. **0.156 Shield test plan.** `findings/engine-0156-shield-tests.md`: under 25 numbered plain-language steps for the
    owner, naming the kind of test file needed (Dolby Vision profiles 5/7/8/9, HDR10, HDR10+, HLG, SDR, AV1, MPEG-TS
    with resume, multi-video-track, ASS/SRT/PGS subtitles with box background) and exactly what to look for.
11. **Subtitle box padding — plumbing only (draft).** For 0.156: the Java/JNI side of upstream `461685e0faea` (style
    value, native wrapper, saved preference with a default that reproduces today's look), with tests. No Settings
    control, no visible change. If it cannot be done without the 0.156 native libraries, write it as
    `findings/subtitle-padding-plumbing.md` instead.
12. **Then** MORE item 7 (engine 0.156 patches), then MORE items 5 (groups 2–4), 6, 9, 10, 11, 13, 14, 15, 16 in that
    order.

## Before you finish
Report `docs/codex/reports/YYYY-MM-DD-0155-next.md`: per item, commits, what changed, tests added, findings by
severity, what was skipped and why, exact place to continue. Confirm with
`git diff --name-only claude/supernova-github-access-yfhe89...HEAD` that no hands-off or existing `.github/` file
changed in this run. Commit and push.
