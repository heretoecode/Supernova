# TODO — Permanent protected Supernova release signing workflow

**Status:** Deferred; do not interrupt current 0.139 playback, Settings, Diagnostics and Backup/Restore repairs. **Owner request:** 16 October 2026. **Scheduling:** After current corrective APK, preferably before the next release-signing cycle; not part of 0.140 feature scope unless separately scheduled.

## Problem
The GitHub Actions environment `supernova-foundation-signing` has deployment branch restrictions. Each new Codex branch has needed manual administrator authorisation in **Repository Settings → Environments → supernova-foundation-signing → Deployment branches and tags**. Codex's attempted environment-policy update failed with HTTP 403, `Resource not accessible by integration`.

## Preferred solution to investigate
A **stable protected release branch or equivalent trusted signing workflow**: development happens and is validated on Codex branches without signing secrets; owner-approved source is promoted through a controlled step to a trusted ref that can access the existing signing environment. The exact implementation is for Codex to audit and recommend, not predetermined.

## Checklist
- [ ] Audit current CI, release signing, GitHub environment branch rules, protections and Drive delivery workflow.
- [ ] Design a secure one-time administrator setup that avoids authorising every new development branch.
- [ ] Keep the **existing Supernova signing key/certificate**, application identity, signing secrets and upgrade compatibility; never log secrets.
- [ ] Prevent arbitrary PRs and development branches from accessing signing secrets.
- [ ] Preserve explicit release approval and **no automatic merge to main**.
- [ ] Verify signed artifact is built from the exact validated source commit with correct version, checksum and HTML release notes.
- [ ] Continue uploading to the **existing Supernova APK folder on Google Drive**, with verification and safe filename handling.
- [ ] Test signing, upgrade signature continuity, error/retry paths and document the repeatable Codex procedure.

## Done when
Future releases can be signed and uploaded through the trusted workflow **without manually adding each Codex development branch to the signing environment**, while keeping certificate and secrets protected.

**Note:** GitHub Issues are disabled in this repository (GitHub API returned HTTP 410), so this is tracked as a repository TODO document rather than an Issue.
