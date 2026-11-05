# Codex brief — engine release integration (owner approved 2026-11-04)

The owner has asked Codex to do the engine release integration; Claude dispatches the builds, checks the results,
signs and hands the APK to the owner. This brief **widens** the earlier rules for this work only. Everything not
widened here still follows `BRIEF_0155_MORE.md` rules 1–5 and `docs/codex/HANDS_OFF.txt`.

## What is widened (engine work only)
- You may change **app code** (Java under `src/main/java/` and tests under `src/test/`) to integrate the engine
  release: apply your three `video-supernova156-*.patch` drafts as real commits, and the JNI/Java callers the new
  engine needs.
- You may port upstream aos-Video `8bbfb666` (subtitles kept on screen when the video overflows vertically) as a
  faithful port. This is the one layout change allowed: it must reproduce upstream behaviour and change nothing else
  that a user sees (no colours, sizes, controls, settings or wording).
- You may add **new** build files: `.github/build/*supernova156*` (remember `git add -f`) and new workflows
  `.github/workflows/supernova-0156-*.yml`. Still never modify or delete an existing `.github/` file; still never edit
  `build.gradle`, `settings.gradle` or `gradle.properties` (Claude does the version bump and wiring that needs them).

## What stays Claude's
Version bump and versionCode, signing, any change to an existing workflow or Gradle file, dispatching workflows,
anything a user sees beyond the `8bbfb666` port, and the hands-off list (Network & Files, library scan, Put.io,
LibraryHealth, SupernovaStyle and the rest).

## The list
1. **Native build workflow (new files).** Based on the existing `supernova-0146-native-stack.yml`,
   `build-supernova146-native-stack.py`, `package-supernova146-native.py` and `native-supernova146.lock.json`, create
   `supernova-0156-native-stack.yml` (manual dispatch only) and the matching `*supernova156*` scripts that build the
   avos tip `5cf3ae9` with your five `avos-supernova156-*.patch` in lock order, reusing the verified third-party
   stack, and upload the arm64 native artifacts with SHA-256 receipts. Turn `native-supernova156.lock.draft.json`
   into `native-supernova156.lock.json` (real hashes only from sources you can verify; mark anything that can only be
   known after the first build as `"pending-first-build"`). Offline tests for the scripts. Do not run it.
2. **Apply the app drafts as code.** Commit `video-supernova156-01`, `-02`, `-03` as real changes (one commit each,
   upstream hash in the message), tests included, in manifest order. Keep the patch files for the record.
3. **Port `8bbfb666`** (subtitle canvas when the video overflows vertically) with a Robolectric test. Say in the
   report whether any horizontal-overflow case still needs work.
4. **Build hand-off note for Claude.** In `HANDOVER.md`, a new top section "Engine release — Claude's steps": exactly
   which workflow to dispatch with which inputs, what artifacts to expect, how the app build picks up the new native
   libraries (what Claude must change in existing Gradle/workflow files — list it, do not change it), the
   `build.gradle` versionName/versionCode for 0.155, and the order: archive job first, native build, app build,
   tests, conformance.
5. **Then** continue `BRIEF_0155_NEXT.md` from item 3 (DP-10) as your last report says.

When Claude reports a build or test failure back to you (through the owner), fix it on this branch with a commit that
names the failure, and update HANDOVER.md.

## Before you finish
Report `docs/codex/reports/YYYY-MM-DD-0155-engine-build.md`; update HANDOVER.md; confirm with
`git diff --name-only claude/supernova-github-access-yfhe89...HEAD` that no existing `.github/` file, Gradle file or
hands-off file changed. Commit and push to `codex/0155-code-2026-11-04` only.
