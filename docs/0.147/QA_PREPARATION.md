# 0.147 QA preparation

This checklist supplements all 67 FD-003 cases in TRACEABILITY.json. All physical cases remain NOT RUN. Existing 0.146 device acceptance is still pending and is not inherited as a pass.

## Required Shield run
1. Install only an independently verified, protected-signed QA APK over the retained 0.146 installation. Record package, code, certificate and source receipt. Confirm preferences, accounts, bookmarks, library IDs, artwork, resume/history and protected host identities survive. Do not clear app data.
2. At 720p, 1080p and 4K, traverse the six Network & Files cards in 3x2 order; test first-row Up into global navigation, Down back, edge containment, Back through every nested screen and card focus restoration. Repeat with 150% text and long paths.
3. Open every Settings category and nested subsection, including backup, About, service forms and provider selectors. Verify one shared global header, eight unchanged cards, category/row state persistence, native preference dependencies and nav/content round trips.
4. Use disposable SMB, WebDAV HTTPS/HTTP, SFTP, FTP and FTPS fixtures. Cover remote punctuation, email usernames, anonymous DAV, explicit/default ports, invalid credentials, TLS/host-key failure, timeout, offline paths and stale callbacks. Never disable certificate or host-key verification.
5. Exercise All Files' three controls and all six sort choices, deep breadcrumbs, 50+ rows, list/grid edges, exclusions, staging/continue/discard/apply, saved-location removal and read-only providers. No physical deletion is needed.
6. Use a consented Put.io test account for real code/QR approval, expiry, cancellation, restart/reconnect, all eight account actions, API pagination/search, WebDAV association, review/reassignment and disconnect choices. API access and WebDAV playback remain independent.
7. Unplug/replug a disposable USB fixture; interrupt a network traversal; start overlapping scans. Confirm stable records/history/artwork, truthful status and no mass deletion. Repeat Library Health's five categories and meaningful action focus.
8. Play movies and TV episodes with known configured-region ratings. Verify upper-left white text, thin black outline and slim red accent without a panel; no focus capture or subtitle/HUD interference. Time ten seconds from established output then gentle fade. Cover resume, restart, same-file relaunch, unindexed rapid switching, missing metadata, foreign-only ratings and conflicting regional ratings.

## Evidence boundaries
Software renders are Android/Robolectric output, not Shield captures. Test counts and exact source must be read from the completed run; prior 0.146 counts are historical only. No signed 0.147 APK, live-provider pass or installed upgrade is claimed before its evidence exists.

The inherited 0.146 signing entry point explicitly rejects any branch/version other than its authorized 0.146 candidate. It must not be used for 0.147. Any later 0.147 protected-signing proposal must retain environment branch controls and bind the exact successful unsigned payload; this development task does not change environment protections or expose keys.

## Protected signing preparation
The new version-specific `supernova-0147-qa.yml` retains the same protected environment, certificate pin, exact successful-run/source checks, report-only ancestry rule, no-rebuild signing, payload parity verification and mandatory private-material cleanup. It has not been dispatched. The live deployment policy lists 13 branches including 0.146 and does not authorize `codex/supernova-0.147-implementation-2026-10-29`. Only the owner may change that environment policy. No environment mutation was made.
