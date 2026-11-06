# Legacy cleanup candidates — 0.157, 6 November 2026

Report first, as required by BRIEF_0157 item 23. **No legacy code/resource/route is removed in this delivery.**
Owner approval of specific batches is still required before removal, then full build/tests and Shield checks.
Source byte counts below are review scope, **not APK savings**; R8 can already discard unreachable code.
No SAFE removal batch has been established. A text-reference search cannot prove reflection, manifest, XML or JNI
closure. The retained engine, playback and protocol code stays close to the reviewed upstream stack.

| Batch | Classification | Files | Source bytes | Evidence / next proof |
| --- | --- | ---: | ---: | --- |
| Classic movie grids | UNSURE | 13 | 56,343 | MainFragment still names classic destinations; nested alphabet launch remains. Check every entry in tv-only-stage5-device-checks.md. |
| Classic anime grids | UNSURE | 12 | 46,936 | Nested alphabet/year/genre/rating routes and manifest registrations remain; no current Shield reachability results. |
| TV-show hosts and grids | KEEP | 31 | 235,635 | TvshowActivity/Fragment also host current content, season selection and action routes. Do not remove the whole package. |
| Collection routes | UNSURE | 12 | 110,826 | VideoViewClickedListener still launches collection details; shared data builders are live. |
| Player engine and controller | KEEP | 93 | 1,472,126 | Live playback/JNI, external playback and protocol contracts; no broad deletion justified. |

## Largest conceptual batch: `try_new_ui` and classic UI routing

There are 96 Java lines mentioning the switch across 49 files in this exact tree.
This is **UNSURE**, not an unused feature: both branches still select live routes and shared hosts. A blanket switch
removal could strand restored preferences, external intents, player information, collection/season actions and Back.
Treat migration of the preference separately from deleting exclusive classic subtrees. Start with one grid pair,
prove all source/XML/manifest/reflection references and replacement entry/Back equivalence, then seek batch approval.
The existing 18-step device checklist remains unexecuted here; absence of a Shield result is not dead-code proof.

Switch reference anchors (first 20, for a reviewer to reproduce with `rg -n try_new_ui src/main/java`):

- `src/main/java/com/archos/mediacenter/video/CustomApplication.java:909`
- `src/main/java/com/archos/mediacenter/video/CustomApplication.java:1506`
- `src/main/java/com/archos/mediacenter/video/CustomApplication.java:1539`
- `src/main/java/com/archos/mediacenter/video/utils/SafeBackup.java:383`
- `src/main/java/com/archos/mediacenter/video/utils/FolderPicker.java:109`
- `src/main/java/com/archos/mediacenter/video/utils/SubtitlesDownloaderActivity2.java:114`
- `src/main/java/com/archos/mediacenter/video/utils/ThemeManager.java:94`
- `src/main/java/com/archos/mediacenter/video/utils/PlayUtils.java:337`
- `src/main/java/com/archos/mediacenter/video/utils/VideoPreferencesCommon.java:400`
- `src/main/java/com/archos/mediacenter/video/utils/VideoPreferencesCommon.java:717`
- `src/main/java/com/archos/mediacenter/video/utils/VideoPreferencesCommon.java:746`
- `src/main/java/com/archos/mediacenter/video/utils/BackupPrivacy.java:65`
- `src/main/java/com/archos/mediacenter/video/utils/BackupPrivacy.java:96`
- `src/main/java/com/archos/mediacenter/video/streaming/StreamingActions.java:227`
- `src/main/java/com/archos/mediacenter/video/streaming/StreamingActions.java:291`
- `src/main/java/com/archos/mediacenter/video/streaming/StreamingActionPresenter.java:64`
- `src/main/java/com/archos/mediacenter/video/leanback/PreviewDialog.java:164`
- `src/main/java/com/archos/mediacenter/video/leanback/MainFragment.java:401`
- `src/main/java/com/archos/mediacenter/video/leanback/MainFragment.java:737`
- `src/main/java/com/archos/mediacenter/video/leanback/ExperimentalBrowseFragment.java:12`

## SAFE / UNSURE / KEEP policy

- **SAFE:** no removal approved or proven in this run. Reports, reference mock-ups and historical release assets are
  not disposable code; release history uses the version folders, so they remain KEEP.
- **UNSURE:** exclusive classic movie/anime/collection candidates above. Inventory sizes include shared helpers;
  they are upper bounds on source review scope, not permission to remove a package.
- **KEEP:** current Details/TV hosts, player/JNI/native wrappers, network transports, credential stores, backup
  migrations, shared library loaders, compatibility intent contracts, historical version folders and licences.

After approval: remove only the named exclusive files, update all entry paths as a separate reviewed change,
compile all affected modules, run full conformance and the relevant Shield journey before taking the next batch.
The current brief forbids workflow/Gradle/resource edits, so any necessary build or screen integration stays with
that review. Do not treat item 22's stage-5 carry-forward as overriding item 23's approval requirement.
