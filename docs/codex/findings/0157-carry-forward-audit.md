# 0.157 carry-forward audit — continuation index

This supersedes the initial partial checkpoint at 851ac21ca. Current implementation is 24aa1d4eb plus the DB157 and
DG157 post-hunt fixes; final source merge is 9dd9d0c65. The source owner fixed the old About syntax defect before
this continuation. Whole app/module Java compilation now passes; the old About blocker is resolved, not an open gate.
The same delivery report remains ../reports/2026-11-06-0157.md.

## Manifest boundary inventory


Current source manifest exposes 15 explicit/implicit intent-filter component entries.

| Kind | Component | Permission | Export declaration |
| --- | --- | --- | --- |
| activity | `.EntryActivity` | `none on component` | true |
| activity-alias | `${applicationId}.browser.BrowserActivityVideo` | `none on component` | true |
| activity | `.browser.QueryBrowserActivityVideo` | `none on component` | true |
| activity | `.player.PlayerActivity` | `none on component` | true |
| activity | `.utils.TraktDeviceAuthActivity` | `none on component` | true |
| activity | `.browser.VideoPicker` | `none on component` | true |
| receiver | `com.archos.mediaprovider.video.VideoStoreImportReceiver` | `none on component` | true |
| activity | `.utils.FolderPicker` | `none on component` | true |
| receiver | `com.archos.filecorelibrary.ExtStorageReceiver` | `none on component` | true |
| activity | `com.archos.mediacenter.video.leanback.MainActivityLeanback` | `none on component` | true |
| activity | `com.archos.mediacenter.video.leanback.collections.CollectionActivity` | `none on component` | true |
| activity | `com.archos.mediacenter.video.leanback.tvshow.TvshowActivity` | `none on component` | true |
| activity | `com.archos.mediacenter.video.leanback.details.VideoDetailsActivity` | `none on component` | true |
| activity | `com.archos.mediacenter.video.leanback.search.VideoSearchActivity` | `none on component` | true |
| provider | `com.archos.mediacenter.video.leanback.search.VideoSearchProvider` | `none on component` | true |

A missing component-level permission does not itself prove an exploit: exported media/search/launcher contracts
can be intentional, and caller validation still needs a method-level read. This inventory does not claim that
those handlers reject malicious intents. Do not weaken exported contracts without migration and intent tests.


## Completed records and precise boundaries

- Security/privacy: security-audit.md, source/credential/TLS/backup/intent boundary findings plus safe logger and
  provider-file fixes. Unchanged screen and credential-migration findings remain explicit, not a security certificate.
- Three full player reads: deep-hunt-1-player.md. MORE groups: deep-hunt-2-screens.md (107 full, 47 protected, two
  removed), deep-hunt-3-settings-files.md (21 protected, all excluded), deep-hunt-4-services.md (165 full, four removed).
  Historical partial tables are retained; new receipts state exactly what was read and what changed.
- Scan triggers/cost: scan-cadence-report.md. No observed device cause for the historical 15-minute cycle is invented.
- Frozen upgrade/rollback: 0157-upgrade-safety.md and src/test/resources/upgrade157 source/fixture receipts.
  Host real-SQLite fixtures are not signed in-place APK/KeyStore/power-loss acceptance.
- Version-neutral release recipe: 0157-release-pipeline-draft.md. It is a draft without active workflow/Gradle edits.
- Lint: 0157-lint-inventory.md records the latest remote report's exact source and all categories; current AGP lint
  remains unrun. Local Java compilation and layout tests do not replace lint.
- Dead code: dead-code-2.md and refreshed legacy-cleanup-candidates.md. No removal before owner approval (brief 23),
  and live/shared classic screen routes remain KEEP/UNSURE. Current user instruction also forbids screen edits.
- Performance, APK size and dependency/upstream reports: performance-audit.md, apk-size-audit.md, dependency-audit.md.
  Actual 0.155 APK bytes are attributed; no 0.157 APK or binary saving is fabricated. Dependency declarations plus
  OSV/latest observations are not the final Gradle graph; existing backport receipts must be verified in built bytes.
- Native seek/frame and protocol producer contracts, DP-10 process ownership: 0157-ui-hooks.md and the delivery
  report. Successful Android output submission is distinct from physical display presentation. JNI needs a reviewed
  rebuilt archive; the existing archive reports unavailable. Actual Shield/Binder/protocol/device acceptance remains.

No 0.156 fix, release progress edit, screen/resource edit, active workflow edit or fork push is authored here.
The required source merges include the source owner's work verbatim. Final report records verification evidence.
