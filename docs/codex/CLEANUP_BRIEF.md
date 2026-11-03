# Clean-up brief for Codex — approved by the owner on 2026-11-03

The owner approved every section of `docs/codex/CLEANUP_PROPOSAL.md`. Section 1 (closing the 21 old pull
requests) is already done. Do sections 2–5 below, **exactly as listed**. Nothing here changes app code or
behaviour. If anything does not match what this brief expects (a branch, file or folder is missing, or has
changed since), stop that step, do nothing destructive, and write it in the report.

## Setup
1. Start from the tip of `claude/supernova-github-access-yfhe89`.
2. Create a new branch `codex/cleanup-YYYY-MM-DD` (if it exists, add `-2`, …). File changes are committed and
   pushed only to that branch. Do not open a pull request, merge, rebase or force-push.
3. Do not touch any branch or tag not named in this brief.

## Section 2 — branches (52 → 5, preserving every commit)
**Keep, untouched:** `claude/supernova-github-access-yfhe89`, `main`, `previews/claude`, `v6.4`,
`archive/pre-github-cleanup-2026-09-24`, and your own new `codex/cleanup-…` branch and any `codex/overnight-…`
branch **newer than** `codex/overnight-2026-11-03-4` (it may still be under review).

**2a. Preserve as tags, then delete — 20 branches not contained in our branch.** For each, in this order:
create the tag `archive/<branch name>` on the branch's current tip, push the tag, **check that the pushed tag
points at the same commit as the branch**, and only then delete the branch on GitHub:
```
git fetch origin
for b in <list below>; do
  git tag "archive/$b" "origin/$b"
  git push origin "refs/tags/archive/$b"
  test "$(git ls-remote origin "refs/tags/archive/$b" | cut -f1)" = "$(git rev-parse "origin/$b")" \
    && git push origin --delete "$b" || echo "NOT DELETED: $b"
done
```
List: `codex/preview-4.1.4`, `codex/apk-build-fixes`, `codex/post-4.1.7-shield-fixes`,
`codex/supernova-0.139-implementation-2026-10-13`,
`feature/supernova-app-analysis-and-mockup-6358464526881152498`,
`automation/github-release-drive-publisher-2026-10-11`, `docs/maintenance-investigation-2026-10`,
`docs/nova-upstream-deep-audit-2026-10-11`, `docs/ui-reference-atlas-2026-10-12`,
`docs/backup-restore-investigation-2026-10`, `docs/details-tab-forensic-audit-2026-10-13`,
`docs/playback-hud-functionality-audit-2026-10-16`, `docs/network-files-blueprint-2026-10-23`,
`docs/network-files-functionality-audit-2026-10-23`, `docs/project-decisions-2026-10-12`,
`docs/settings-functionality-audit-2026-10-26`, `docs/handover-investigation-2026-10-27`,
`docs/settings-from-scratch-blueprint-2026-10-26`, `docs/edit-library-audit-and-proposal-2026-10-30`,
`docs/network-files-settings-design-next-version-2026-10-29`.

**2b. Delete — 28 branches already fully contained in our branch.** For each, first confirm
`git merge-base --is-ancestor origin/<branch> origin/claude/supernova-github-access-yfhe89` succeeds; delete
only if it does (`git push origin --delete <branch>`), otherwise leave it and report it.
List: `codex/apk-build-412`, `codex/preview-4.1.6`, `codex/preview-4.1.7`, `codex/foundation-release`,
`codex/supernova-0.135`, `codex/supernova-next-refinement-2026-10-10`, `codex/supernova-0.137-qa-2026-10-11`,
`codex/supernova-0.138-qa-2026-10-12`, `codex/supernova-0.140-implementation-2026-10-16`,
`codex/supernova-0.141-implementation-2026-10-22`, `codex/supernova-0.142-implementation-2026-10-23`,
`codex/details-improvements-2026-10-27`, `codex/supernova-0.143-implementation-2026-10-27`,
`codex/supernova-0.144-implementation-2026-10-28`, `codex/supernova-0.145-maintenance-2026-10-28`,
`codex/supernova-0.146-upstream-completion-2026-10-28`, `codex/supernova-0.147-implementation-2026-10-29`,
`codex/supernova-next-implementation-2026-10-30`, `codex/agreed-fixes-2026-11-01`,
`codex/overnight-2026-11-03`, `codex/overnight-2026-11-03-2`, `codex/overnight-2026-11-03-3`,
`codex/overnight-2026-11-03-4`, `project/authoritative-cleanup`, `project/design-knowledge-preservation`,
`project/full-history-reconciliation`, `project/main-build-alignment`, `project/post-promotion-housekeeping`.

If GitHub refuses a tag push or a branch deletion, stop section 2 there and report the exact error.

## Section 3 — old build workflows (one commit: `Retire workflows of finished versions`)
Delete every file in `.github/workflows/` **except** these 12: `preview-renders.yml`,
`report-artifact-digests.yml`, `report-test-failures.yml`, `supernova-0152-conformance.yml`,
`supernova-0152-qa.yml`, `supernova-0153-conformance.yml`, `supernova-0153-qa.yml`,
`supernova-0146-native-stack.yml`, `supernova-0146-native-security.yml`, `supernova-0146-native-host.yml`,
`foundation-signing.yml`, `nova.yml`. (Expected: 46 deleted.) Do not touch `.github/build`,
`.github/signing` or anything else under `.github/`.

## Section 4 — old screenshots in `docs/` (one commit per folder: `Remove old images from docs/<folder>`)
Delete files ending `.png`, `.jpg`, `.jpeg`, `.webp`, `.gif`, `.mp4`, `.mov` (any case) under
`docs/0.138` … `docs/0.149`, `docs/next-version` and `docs/details-2026-10-27`. **Keep every other file**
(all `.md`, `.json`, `.svg`, `.html`, `.txt`, …), and keep `docs/design`, `docs/foundation` and every other
folder untouched apart from link fixes.
Then, in any `.md`/`.html` file anywhere under `docs/`, replace each link or `<img>` that pointed to a deleted
file with the text `(image removed 2026-11; in git history: <original path>)`. Change nothing else in those
files. One commit: `Point links at removed images to git history`.

## Section 5 — NOVA leftovers (one commit each)
1. Delete `icon_sources/unused/` — `Remove unused icon sources`.
2. Delete `src/noamazon/play/` — `Remove NOVA Play Store listing`. (First confirm no `*.gradle` file mentions
   `play/`; if one does, skip this step and report it.)
3. Delete `ic_launcher-playstore.png`, `ic_launcher-web.png`, `nova-playstore.png` at the repository top
   level — `Remove NOVA store icons`.
4. `git mv doc docs/nova` and `git mv buffer.md docs/nova/buffer-supernova.md`; then in
   `src/test/java/com/archos/mediacenter/video/player/SubtitleTrackSelectionPolicyTest.java` and
   `AudioTrackSelectionPolicyTest.java` (same folder) change the comment text `Video/doc/` to
   `Video/docs/nova/` — nothing else — and in `README.md` update any link to `doc/…` or `buffer.md` to the new
   place. Commit: `Move NOVA technical notes into docs/nova`.

## Report
Add `docs/codex/reports/YYYY-MM-DD-cleanup.md` (new file): per section, what was done; for section 2 the
exact tags created and branches deleted, and anything refused or skipped; the final
`git ls-remote --heads origin` and `git ls-remote --tags origin 'refs/tags/archive/*'` output. Commit and push.
