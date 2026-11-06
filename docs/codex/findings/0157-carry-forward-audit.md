# 0.157 carry-forward audit checkpoint

This is a bounded static inventory and continuation record for BRIEF_0157 item 22, not a completed security,
performance, dependency or full-player audit. No source was removed. The delivery report gives every carried item
its own disposition; the older findings remain historical evidence, not new acceptance results.

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

## Security / privacy review anchors

- `streaming/putio/PutioTokenStore.java`: AndroidKeyStore-backed encrypted credentials; no token in the new code-link
  snapshot. The new state tests verify a late/expired token is not saved and no credential enters public state.
- `FileCoreLibrary/.../ftp/FtpsTlsPolicy.java`: explicit AUTH TLS on port 21, strict certificate/hostname verification;
  port 990 would select an incompatible implicit-TLS endpoint. The new form mapping retains port 21.
- `StreamingRepository.traktDiscovery`: fixed HTTPS origin and allow-list, no account token, redirects disabled,
  2 MiB body bound. Application API credentials are not a user login and are never included in return values.
- `utils/backup/PortableSettingsRegistry.java`: positive rule ownership; the transient connection-removal journal
  and connection last-seen store are not exported as arbitrary settings. This does not prove all historical backup
  paths are secret-free; credential archive, import, logs and transport stores still need the full audit.
- `player/UpdateNextTask.java`: DP-10 remains open. Existing scan cancellation and new search cancellation do not
  establish ownership/cancellation of this separate next-file listing. Never register its old partial transport
  drafts as a completed fix.

## Cadence and the stuck local state

`VideoStoreImportService` registers MediaStore file/video content observers, reacts to Android scan broadcasts,
initial service startup, volume changes and process foreground. The one-second messages debounce/queue imports;
they are not proof of a 15-minute periodic alarm. `NetworkAutoRefresh` separately uses the configured network
period and foreground admission; Put.io reuses that admission. The source does not establish which Android/media
provider or foreground trigger caused the owner's 15-minute sequence. A full trigger/cost report and a Shield
trace with batch IDs are still needed. No cadence is silently attributed to an unobserved timer.

`VideoStoreImportImpl.doFullImport` and `doIncrementalImport` set INITIAL/REGULAR_IMPORT before storage/reconciliation
work, but their old resets were after the normal path only. A storage exception could strand busy state. The new
finally blocks unconditionally clear import/delete counts and IDLE; controlled storage-failure tests reproduce
both paths and verify the original exception propagates. This fixes a demonstrated source defect, not a proven
reconstruction of the historical photo. Long real work still correctly remains active until the worker finishes.

## Native / seek boundary

`Player.onSeekComplete` is a seek acknowledgement; `onAllSeekComplete` clears busy state. Neither is documented as
an actual presented-frame event for every seek. MEDIA_INFO_VIDEO_RENDERING_START is handled, but cannot be assumed
to fire for every seek/backend. No fake "picture ready" signal or zero native frame counters were added. A reliable
item-20 signal needs a generation-aware native presented-frame/seek contract and backend/device tests.

## Build / size / dependency boundary

The local harness uses actual SDK Java APIs, real AIDL output and published dependency classes. Compile-only R and
BuildConfig bindings are generated outside the checkout; this is not a resource link, Gradle resolution, APK, lint,
R8, ABI or native compatibility proof. Native binaries/receipts are unchanged. No 0.157 APK is available here to
attribute compressed dex/native/resource bytes or estimate install/download savings. Source sizes in the cleanup
report must not be reported as APK savings.

A complete version/advisory audit needs a frozen resolved graph plus native lock/SBOM inventory and checked advisory
sources. Collecting Maven artifacts to compile does not establish newest stable versions or vulnerability status.
Do not relabel the old NOVA 6.6.4 engine archive as a newer reviewed stack.

## Final-merge compile blocker

The required second merge imports an extra closing brace in the source branch's 0.156
`foundation/FoundationAboutWorkspace.java:646`; the call at line 647 is outside its method. The exact source file
was not edited here. Whole-tree compilation of the final merge fails there. The own 0.157 app/module files and
changed tests compile separately, and their 68 regressions pass; see the delivery report for classpath/resource
binding limitations. Do not treat that component pass as a green final application build.
