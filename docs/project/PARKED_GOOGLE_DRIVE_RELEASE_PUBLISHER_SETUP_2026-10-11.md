# PARKED — Google Cloud OAuth setup and Supernova release publishing continuation

**Recorded:** 2026-10-11  
**Status:** PAUSED AT STEP 3 — USER REQUESTED DOCUMENTATION, NOT FURTHER SETUP  
**Repository:** `heretoecode/Supernova`

## Purpose and approved outcome
The user wants a permanent, approved-release publishing system that:
- Publishes each validated signed QA APK as a GitHub **pre-release** with readable release notes and a **direct standalone APK asset** named exactly `Supernova_<version>.apk`.
- Uploads the same signed APK (not a ZIP, JSON or test intermediary) to **Google Drive / Supernova / APKs** for installation on NVIDIA Shield through **Solid File Explorer**.
- Updates the **existing** dark-themed `Supernova Development.html` in place in the parent Drive folder, retaining historical notes, rather than creating duplicate files.
- Checks APK SHA-256, version, signing continuity, destination, duplicate/version conflicts and upload readback; uses explicit release approval. Do not infer Shield QA acceptance from CI passing.

## Progress already completed
1. Google Drive connected as a **ChatGPT plugin**; manual upload succeeded. **`Supernova_0.136.apk` was verified present** in the APKs folder, Google Drive file ID `1Z_dRagpHpjG1mKb3ar-41-REEuOAO13i`, https://drive.google.com/file/d/1Z_dRagpHpjG1mKb3ar-41-REEuOAO13i/view?usp=drivesdk; size 102,248,688 bytes. This **does not** confer Drive credentials on GitHub Actions.
2. Google Drive parent folder **Supernova**: ID `1c3G--zRc7aAt_SF6KQVniyD8xv6usMmZ`, https://drive.google.com/drive/folders/1c3G--zRc7aAt_SF6KQVniyD8xv6usMmZ. Child **APKs**: ID `1mfHrFgLdJJSJYtTPys1N4Y-_KuE_84mM`, https://drive.google.com/drive/folders/1mfHrFgLdJJSJYtTPys1N4Y-_KuE_84mM. Existing root HTML: file ID `17P05N7Q8c0TQdeZIEjUKYoQ-4xZIz-DW`.
3. Separate implementation branch `automation/github-release-drive-publisher-2026-10-11`, **draft PR #11**: https://github.com/heretoecode/Supernova/pull/11. Contains `.github/workflows/publish-supernova-release.yml`, `scripts/publish_release_drive.py`, `docs/release-publisher/README.md`. **Draft/unmerged/untested**; no automatic publishing live, no release published by this workflow. Does not alter Android app or active Codex branch.
4. User **confirmed** creation of a Google Cloud project named **Supernova Release Publisher** and enabling the **Google Drive API**. The project ID, project number, billing state and OAuth configuration were not shared or independently verified. No credentials have been provided or stored in GitHub.
5. Previous guidance identified **Step 3: Configure Google Auth Platform** as the next user action. The user has **not confirmed** completing Step 3.

## Next user-facing setup — resume here (Step 3)
Open https://console.cloud.google.com/auth/overview with the **Supernova Release Publisher** project selected.
- Start Google Auth Platform / OAuth consent configuration if prompted.
- App name: **Supernova Release Publisher**.
- Support email and contact email: the user's own Google account.
- Audience: **External** for a personal Google account; configure the app's consent settings and add the user's own Google account under **Audience → Test users** while testing.
- Do **not** ask the user to paste client secrets, OAuth tokens, refresh tokens or downloaded credential JSON into ChatGPT or GitHub source.
- **Important:** External OAuth app in **Testing** status with Drive scopes commonly yields refresh tokens expiring after **7 days**. That is unsuitable for unattended long-term GitHub Actions publishing. Before generating credentials, review whether to use a production OAuth app, whether verification is required for selected Drive scopes, and whether a narrowly scoped service account or other credential architecture is preferable. Do not falsely promise permanent refresh tokens or approval-free verification.
- Confirm what the user actually sees in Google Cloud UI before prescribing subsequent clicks; Cloud UI changes over time.

## Remaining implementation and acceptance work
1. Select and verify a durable **least-privilege Google Drive authentication model** that can both create an APK in the existing folder and modify the existing HTML file. The draft PR #11 script currently expects OAuth environment secrets `GOOGLE_OAUTH_CLIENT_ID`, `GOOGLE_OAUTH_CLIENT_SECRET`, `GOOGLE_OAUTH_REFRESH_TOKEN`; these are **not configured**. Google service accounts may need explicit folder sharing and could have ownership/storage limitations on My Drive; assess before switching.
2. Create OAuth credentials if selected; authorise the appropriate Google Drive access via a safe consent flow; securely store credentials as GitHub Actions **environment secrets**, never in source or chat. The existing ChatGPT Google Drive plugin is separate.
3. Configure GitHub Actions environment `supernova-release-publishing`, required reviewers/approval and appropriate workflow permissions. Inspect repository settings; do not assume these are already enabled.
4. Review and test draft PR #11 thoroughly: artifact selection (exactly one APK), release-note file path `release-notes/<version>.md`, signed build provenance, release tag commit, checksums, GitHub Release creation, Drive upload, HTML in-place update, idempotent retry, and error handling. Address issues before merge.
5. After explicit authorisation, merge or otherwise install the workflow in a suitable branch and perform a **controlled end-to-end test** with a validated APK. Check direct GitHub Release download and Google Drive APK/HTML readback, and user retrieval on Shield.
6. Preserve app identity/signing, active Codex work and historical APKs. Do not publish or retag 0.136 as a GitHub Release without an explicit decision. No arbitrary push-triggered publishing.

## Related durable records
- [Mandatory Drive delivery and naming handover](SUPERNOVA_MANDATORY_DRIVE_RELEASE_DELIVERY_HANDOVER_2026-10-11.md).
- [GitHub Releases + Drive publisher implementation plan](SUPERNOVA_GITHUB_RELEASES_AND_DRIVE_PUBLISHING_PLAN_2026-10-11.md).
- [General APK/changelog requirements](SUPERNOVA_APK_DELIVERY_AND_CHANGELOG_REQUIREMENTS_2026-10-11.md).
- [Other parked product discussions](PARKED_OUTSTANDING_PRODUCT_DISCUSSIONS_2026-10-11.md).
- [Draft publisher PR #11](https://github.com/heretoecode/Supernova/pull/11).

**Resume prompt:** "Continue Supernova Google Drive release publisher setup from Step 3 (Google Auth Platform OAuth consent). Read `docs/project/PARKED_GOOGLE_DRIVE_RELEASE_PUBLISHER_SETUP_2026-10-11.md` and draft PR #11 first. Do not modify the active Codex app branch. Guide me through one step at a time."
