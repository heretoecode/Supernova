# Codex brief — everything non-visual (owner approved 2026-11-04)

Work through the list below **from the top, as far as you get**. Each item is independent: one commit per item (or
per sub-item where stated), with a message saying what and why. Stop cleanly when time runs out; the next run
continues from where you stopped (record it in the report).

## Rules (apply to every item)
1. Continue on your existing branch **`codex/0155-code-2026-11-04`** (pull it first). Push only to it. No pull request,
   merge, rebase or force-push.
2. **No UI or design work of any kind.** Do not change layouts, drawables, colours, sizes, spacing, fonts, icons,
   animations, focus behaviour, screen structure, navigation, or any text a user sees (strings shown on screen,
   dialog wording, labels). If a bug fix would change what the user sees or how a screen behaves, do not fix it:
   write it up in the findings for the owner and Claude instead.
3. Read `docs/codex/HANDS_OFF.txt` and never edit anything listed there. Only exception: you may add **new** files
   under `.github/build/` whose names contain `supernova155` or `supernova156`, and new workflow files under
   `.github/workflows/` whose names start `supernova-0156-` (never modify or delete existing files there).
4. You cannot run Gradle; Claude builds and tests your branch. Write Java 17 with complete imports; every code fix
   comes with a JUnit/Robolectric test in the same commit; never weaken, skip or delete an existing test to make
   something pass.
5. Write findings as `docs/codex/findings/*.md` (tables: ID, file:line, what goes wrong, evidence, suggested fix,
   severity, confidence). Report = `docs/codex/reports/YYYY-MM-DD-<suffix>-more.md`.

## The list
1. **Failing test.** Claude built `0efd8e448`: compile OK, lint 0 errors, all tests pass except
   `browser/QueryCompatibilityTest.searchAndViewForwardAllLaunchDataAndLeaveNoCompatibilityHostOnBackStack` at
   `assertNull(origin.getComponent())` — Robolectric's `buildActivity(cls, intent)` sets the component on the intent
   it is given. Pass `new Intent(origin)` to `buildActivity`. Test file only.
2. **Stuck network scan — draft fix (non-UI, MediaLib).** From your `findings/stuck-network-scan.md`, candidate 1
   (and the other no-compensation gaps in its termination table): every counted batch member must end exactly once
   (done / failed / not started), including the receiver's background gate and throttle, start failures, message or
   executor rejection, and teardown of never-started tasks; add a start-acknowledgement deadline and a per-source
   deadline with call cancellation. Deliver as a **new** patch `.github/build/medialib-supernova155-scan.patch`
   (applies with `git apply --check` after the existing MediaLib patches, in order) plus the MediaLib tests your
   analysis described (MockWebServer silent/trickle/disconnect included) inside that patch. Do not change
   `PreviewLibraryScan.java` or any UI; describe any needed UI-side change in the findings instead.
3. **Library Health counting — analysis only.** Write `findings/library-health-counts.md`: exactly how the summary,
   categories and stored markers should be reconciled (no UI wording), including clearing markers when rows are
   deleted, with the tests that would prove it. (LibraryHealth files are hands-off: do not edit them.)
4. **Stage-5 device checklist.** `findings/tv-only-stage5-device-checks.md`: under 20 numbered steps in plain words
   for the owner on the Shield, each proving whether a deferred classic TV screen is still reachable; for each step,
   which files can go if it is "never reached".
5. **Finish the deep bug hunt (read-only):** remaining player files, then groups 2, 3 and 4 (refresh each inventory
   against your branch first; skip hands-off and removed files). Append to `findings/deep-hunt-*.md`; one commit per
   group. Do not re-report U1, N1, N2, L1, P1, U2, L2, DP-01…DP-07.
6. **Fix the non-visual bugs from the hunt.** For every finding of severity crash or user-visible with confidence
   high that is purely logic / threading / leak / data / security (rule 2 decides), fix it with a test, one commit per
   finding, ID in the message. List the ones you did not fix and why.
7. **Engine 0.156 — refreshed native patches (draft).** Per `findings/engine-0156-plan.md`: rework each Supernova avos
   patch against the planned upstream tip as `.github/build/avos-supernova156-*.patch` (each `git apply --check`
   clean, in order, on a clean checkout) and `.github/build/native-supernova156.lock.draft.json` (proposed sources and
   order; draft; no invented hashes). Record any rework you could not finish in the plan.
8. **Engine 0.156 — permanent storage (draft).** A new workflow `.github/workflows/supernova-0156-native-archive.yml`
   (manual dispatch only, never on push) that downloads the pinned native artifacts of run 40843685431, verifies
   their SHA-256 against the existing lock/verification files, and attaches them to a GitHub Release
   `native-archive-0146` so builds no longer depend on artifacts that expire on 27 Jan 2027; plus a short
   `findings/native-archive-plan.md` saying how the build would switch to the release. Do not run it.
9. **Lint warnings (non-visual).** From the latest lint inventory on branch `reports/lint`, fix warnings that cannot
   change what the user sees (e.g. unused code, obsolete SDK checks, Java correctness, threading annotations).
   Leave resource/layout/string warnings alone. Group into small commits by warning type.
10. **Dead code beyond TV-only.** Unused classes, methods, fields, resources-not-referenced-anywhere (code, XML,
    manifest, reflection by name, tests) — prove each with grep evidence in `findings/dead-code-2.md`, then remove in
    small commits. Skip anything you cannot prove unused.
11. **Tests for untested logic.** Find non-UI classes with no tests (backup/restore, metadata/scraper parsing,
    streaming, Put.io API client, Trakt, subtitles download, diagnostics redaction, intent contracts) and add tests
    for their main paths and edge cases. Fix nothing in the same commit; report any bug a test exposes as a finding.
12. **Security and privacy audit (report, plus safe fixes).** Exported components and their permissions, intent
    handling, credential/token storage (OAuth, WebDAV/SMB/FTP passwords), logs that leak URLs, usernames or tokens,
    TLS/trust settings, backup files containing secrets. Write `findings/security-audit.md`; fix only items that are
    purely internal (e.g. redact a log line) with tests.
13. **Performance audit (report only).** Main-thread disk/network/database access, cursor/stream leaks, heavy work in
    `onBindViewHolder`/loaders, wake locks, background scheduling. `findings/performance-audit.md`, ranked.
14. **APK size audit (report only).** What makes up the 34 MB arm64 APK (dex, native, resources, assets), unused
    resources and libraries, and safe reductions with estimated savings. `findings/apk-size-audit.md`.
15. **Dependency audit (report only).** Every Gradle dependency and native component: current version, latest stable,
    known security issues, what an update would touch. `findings/dependency-audit.md`.
16. **Keep going** with item 5 (bug hunt) and item 11 (tests) if everything above is done.

## Before you finish
Write the report: per item, commits, what changed, findings by severity, what was skipped and why, where to continue.
Confirm with `git diff --name-only claude/supernova-github-access-yfhe89...HEAD` that no hands-off file changed
(other than the new files allowed in rule 3). Commit and push.
