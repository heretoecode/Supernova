# 0.155 archive branch and DP-10 continuation — 2026-11-04

Pulled `codex/0155-code-2026-11-04` first (already current at `9fd8bc94c`).
Fetched latest Claude branch at `65457a4e36b30dc0b2bc84e17d427b5b8d708294`.
Read `HANDS_OFF.txt` before the archive brief, then the archive brief and its
MORE/ENGINE_BUILD/NEXT rules. All work stays on the Codex branch; no PR, merge,
rebase, force-push or workflow dispatch. No Gradle/NDK build or device test.

## Per-item result

| Brief item | Commits | Result / validation limit |
|---|---|---|
| ARCHIVE 1 | `ec6507f81`, hardening `8f6599d0e` | Replaces Release publication with one immutable orphan commit on `native-archive-0146`. Keeps Claude's artifact JSON Accept and gh command/reason fixes from `11c60bbb6`, `f4c22771d`. Four original ZIPs, old lock, review and manifest; strict <100,000,000-byte gate. Existing branch must have identical bytes, exact regular-root-file tree and no parents. Fresh fetch validates every file hash, including manifest bytes. Local bare-Git tests only; no GitHub archive branch write. |
| ARCHIVE 2 | `76c402a19`, `8f6599d0e` | Native builder/workflow now offer `branch` (default) and explicit `actions`. Reader fetches the archive afresh, verifies exact tree/manifest, anchors lock/review bytes and all four ZIP hashes to local reviewed inputs, then uses existing tarball/receipt/library verification. Missing/corrupt branch fails without automatic fallback. |
| ARCHIVE 3 | `e3235e233`, `8f6599d0e` | `findings/native-archive-plan.md`: documented owner-only Plan B if job-token push is refused. Exact `gh run download 40843685431`, original-ZIP acquisition, local `--downloads` verification and `--publish` commands. gh run download extracts artifacts, so the instructions deliberately retain original ZIP bytes separately rather than re-zipping. Owner commands were not run. |
| ARCHIVE 4 | `d4735eb89`, `8f6599d0e`, this report/handover commit | Handover now orders branch archive → branch-sourced native build → Claude app wiring → tests → conformance. Engine lock contains measured changed-script and both workflow digests; no invented build hashes. First-build placeholders remain pending and unusable for packaging. |
| ARCHIVE 5 / NEXT 3 | `de40d66c2`, this report/handover commit | Resumed DP-10 with full UpdateNextTask read and prepared dependency transport ownership audit in `findings/dp10-listing-ownership.md`. **Implementation remains pending**, not a completed DP-10 fix. No app source or new cancellation patch is committed in this run. |
| NEXT 4 onward | — | Pending; order retained until DP-10's complete fix/tests land. |

## Archive behavior and checks

The job keeps manual dispatch and the exact limited push trigger Claude added:
only `.github/workflows/supernova-0156-native-archive.yml` on
`claude/supernova-github-access-yfhe89`. A push to the Codex branch does not run it.
The native-stack workflow remains manual only. Job-token authentication uses
`gh auth git-credential` with no token in the URL, tracked files or log.
The archive script uses commit-tree with no parent and a normal non-force push;
it never modifies or repairs an existing archive branch.

Exactly seven files live in the archive: four ABI ZIPs (tarballs/logs inside),
old native lock, existing verification receipt and schema-2 manifest. The manifest
contains SHA-256/size for all six other files; its own bytes are protected by the
Git commit/tree and checked explicitly against the freshly fetched copy. A file
cannot contain its own SHA-256. No separate duplicate tarball assets are stored.

Validation on the final source:

- `python3 .github/build/test-native-archive-supernova156.py`: **16 passed**.
  Real local bare Git remotes exercise create, existing-identical/no push,
  existing-different/no rewrite, post-push corruption, extra tree/history rejection.
  Other cases include oversize/boundary rejection before Git access, bad ZIP/tar/
  receipt/library hashes, unsafe/duplicate archive entries, expired/wrong-run
  metadata, manifest/hash-anchor mismatch, Claude's artifact JSON Accept,
  workflow trigger scope, and the full local four-ABI preparation path.
- `python3 .github/build/test-supernova156-native-stack.py`: **14 passed**.
  Branch reader ordering, no fallback on failed verification, explicit verified
  Actions path, workflow choices/default, lock/recipe digests and existing
  packaging, provenance, malicious-tar, stale-destination and NDK command checks.
- `git diff --check`: passed. No Android/JUnit/Robolectric or actual native build
  was run; the Python tests do not prove the GitHub job token can push the branch.
- FileCore scratch checkout: all eight locked operations plus existing scan patch
  applied cleanly at the pinned source. MediaLib transport sources inspected after
  applying all MediaLib patch operations in manifest order (identity transform
  not run). This is analysis evidence, not production build/integration validation.

## DP-10 status and exact continuation

Continue at **BRIEF_0155_NEXT.md item 3, DP-10**, starting with the public owned
listing handle in a **new**
`.github/build/filecore-supernova155-listing-cancellation.patch`, applied after
existing `filecore-supernova155-scan.patch`. First source points: prepared
FileCore `RawLister.java:38` and `webdav/ScanIoScope.java:29–105`; then app
`UpdateNextTask.java:342–390`, `:400–443`. Read the new ownership audit first.

The audit establishes why the prior scanner scope is insufficient: FTP ownership
starts before the lister receives its client; JSch/SSHJ/SMBJ/JCIFS cache shared
objects; UPnP's MediaLib manager discards the actual Browse handle. A DAV-only
wrapper cannot establish the requested generic contract, and endpoint-wide
close/reset would risk playback. The implementation must close only owned work,
including setup and retries, before claiming termination. No partial public API
or app change was landed under a “fixed DP-10” claim.

Complete the dependency hook(s), task cancellation, filter/sort/traversal
checkpoints and tests in the requested single DP-10 fix commit. A **new MediaLib
companion patch** is needed to expose UPnP request ownership. Tests must use real
silent/trickling DAV listings, repeated replace/cancel (including cancel(false)),
actual worker join/termination, main-queue draining/no late callback, traversal
cancellation, register/close/deadline races, unaffected concurrent playback and
unchanged candidate order/repeat/binge behavior. DP-09 remains Claude's.
After DP-10, NEXT 4 security/privacy, NEXT 5 local scan cadence, NEXT 6 remaining
full player reads, then NEXT 7 onward in order. This run does not add a new bug
ID or re-report the earlier findings. Existing DP-10 remains user-visible/high
confidence; its fix and cross-transport acceptance remain open.

## Scope and delivery

Ran the required comparison:

```sh
git diff --name-only claude/supernova-github-access-yfhe89...HEAD
```

The local Claude ref is a read-only comparison pointer to fetched `65457a4e3`.
The triple-dot comparison includes inherited earlier Codex work (297 paths before
this report). Checked it and the session-only `9fd8bc94c..HEAD` delta against
HANDS_OFF, protected Gradle/tools paths, and the explicitly allowed own
`supernova155`/`supernova156` files. **No forbidden path changed.** The session
modifies only the seven own archive/native recipe/test/workflow files and codex
documentation. No existing unrelated `.github/` file, Gradle file, hands-off path,
UI/resource, or app production source changed in this run.

Final report/handover are committed and pushed only to
`codex/0155-code-2026-11-04`; the final response records the pushed commit SHA.
Claude's next operational step is the live archive job, then branch-sourced native
build and receipt review. Codex's next code step is the DP-10 implementation above.
