# Release publisher setup (NOT YET LIVE)

This isolated branch prepares an explicit, manual GitHub QA prerelease and Google Drive publisher. It must not be merged or enabled until credentials, environment approvals, source artifact and changelog validation have been reviewed. No Android application code was modified.

## Required administrator setup
1. Create a GitHub Actions environment named `supernova-release-publishing` with required reviewers; protect the release publishing trigger. The workflow uses `workflow_dispatch`, so a user must manually approve and supply exact signed build evidence.
2. Configure Google OAuth credentials with access to the existing Supernova Google Drive folder. Store `GOOGLE_OAUTH_CLIENT_ID`, `GOOGLE_OAUTH_CLIENT_SECRET`, and `GOOGLE_OAUTH_REFRESH_TOKEN` as **environment secrets**. Prefer the least privilege Drive scope that actually permits updating the existing HTML and adding files to APKs. Do not put secrets in source or chat.
3. Ensure the repo allows Actions to create Releases (`contents:write`); use a narrowly approved publishing environment. Test access without using an actual release first.
4. Commit accurate, user-friendly Markdown release notes at `release-notes/<version>.md` before triggering. This is a new versioned canonical source for GitHub and Drive notes; do not invent shipped changes.
5. Invoke **Publish verified Supernova QA release** manually with `version`, `source_run_id`, `expected_sha256` and `source_commit`. Workflow fetches the successful original run artifact, ensures exactly one APK, verifies checksum, publishes a prerelease and uploads the same APK to Drive.
6. Verify Github Release and Drive APK metadata, test HTML readback and download through Solid File Explorer. If any step fails, do not claim fully delivered. GitHub release may already exist after a partial failure: investigate and recover explicitly, do not overwrite silently.

## Known review points
- A GitHub Actions environment is not configured by this commit; administrators must add required reviewers and secrets.
- The workflow assumes the source run exposes exactly one APK across downloaded artifacts. Adjust artifact selection if other APKs are produced.
- Ensure the checked-out publisher branch contains a `release-notes/<version>.md` file and its source commit is reachable locally. Verify against original signed APK manifest and package/signing identity before production.
- GitHub Actions workflows only run from the default branch for manual dispatch, so a reviewed merge or an equivalent authorised integration is needed.
- Drive upload uses a multipart body held in runner memory; acceptable for current APK size, but can be upgraded to resumable upload for reliability.
- HTML update is non-destructive: existing file ID, history and CSS are preserved. Insertion marker is bootstrapped before closing body if missing.
- No automatic publication on arbitrary pushes; no new app release has been authorised.
