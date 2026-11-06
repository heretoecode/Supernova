# One-off: finish the 0.156 APK build (stand-in for Claude)

Owner's instruction, 6 Nov 2026: Claude has finished all 0.156 coding, including every screen. Use this only if the
owner asks you to stand in because Claude is unavailable. It is separate from your 0.157 work: use a fresh checkout
of the branch below, not your 0.157 working tree.

**Repo and branch:** `heretoecode/Supernova`, branch `claude/supernova-github-access-yfhe89`. For this task only you
may commit to this branch and may edit screen and test files if a fix needs it. Do not touch `main`, do not merge any
0.157 work, and do not open a pull request.

1. Find the latest run of `.github/workflows/supernova-0156-conformance.yml` on that branch (Claude started
   42226746764 on commit 9fae8eccb).
2. **Green:** report the run number, the commit it built and the artifact `supernova-0156-unsigned-candidate`.
   Then do step 5 and stop.
3. **Red:**
   - List the failing tests (`.github/workflows/report-test-failures.yml` with input `run_id`, or the job log).
   - Decide for each whether it is a real bug or a test out of date with an approved design, and fix the cause
     with the smallest change.
   - Never skip, delete or weaken a test just to get green. If a test is out of date, update it and say why in
     the commit message.
   - Rules: minimum API 23 (no `InputStream.readAllBytes`); tests using org.json need Robolectric; one statement
     per line, lines ≤ 120 characters; `python3 tools/test_code_layout.py` must pass.
   - Commit with a clear message and no AI model names, push, and dispatch `supernova-0156-conformance.yml` on the
     branch again (about 35 minutes).
   - Try at most three times. If it is still red, stop and report what fails and the likely cause.
4. **Signing:** do not sign. Only if the owner says "sign 0.156", dispatch `.github/workflows/supernova-0156-qa.yml`
   with `conformance_run_id` set to the green run of the same commit, and report the signed artifact.
5. **Records:** once green, update the Progress section of `docs/0.156/RELEASE_LIST.md` and the 0.156 part of
   `PROJECT_STATUS.md` with the green run number, the commit and whether it is signed. Commit and push.

If you cannot dispatch workflows, push your fix and tell the owner to press Run workflow on GitHub (Actions ›
workflow › Run workflow, branch above).

Never paste keys, passwords or secrets anywhere.
