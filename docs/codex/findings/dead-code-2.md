# Dead-code second pass — 0.157

Read-only continuation after source merge 9dd9d0c65. No code/resource was removed. Brief 23 requires owner approval
before any removal; the current user instruction separately forbids screen edits. Source bytes are review scope,
never APK savings. Searches include production/test Java, resource XML, manifest and native/preparation patch text.
Reflection sites use platform/vendor class names, not these private method names; no android:onClick or JNI-name
reference to the private candidates was found. Dynamic external consumers prevent public methods/types being
certified solely by a text graph. R8 may already remove unused methods.

| Small batch | Classification | Proof / required next step |
| --- | --- | --- |
| MainFragment.isCursorCountChanged | UNSURE (screen-owned) | Private definition plus commented reference only; no live call, XML/reflection/JNI name. Screen file remains untouched. |
| MediaLibraryBackupService.cleanupExistingData and its private deleteDirectoryContents subtree | SAFE source candidate; removal unapproved | Only definition/log strings for cleanup root, recursive deletion reachable only from that unused root; no reflective/XML/test/native caller. Do not delete restartApplication, which has a live restore caller. Full build/backup tests still required for any approved removal. |
| VideoPreferencesCommon.restartApplication/openStoreListing | UNSURE (screen-owned) | Private definitions; restart has one commented call, no live caller. Screen settings policy/ownership remains with source owner. |
| VideoUtils.deleteAssociatedSubtitles | UNSURE | No current production/test/XML/native caller, but public utility compatibility and original upstream preservation need explicit review. |
| try_new_ui / classic routes | KEEP/UNSURE | 96 lines, 49 files; MainFragment/VideoViewClickedListener and manifest lead to real classic/shared hosts. Follow tv-only-stage5-device-checks.md before proposing route deletion. |
| Engine, playback, protocols, migrations and release-history assets | KEEP | Live JNI/intent/schema/network contracts or historical release consumers. No package-wide removal proposal. |

## Fresh earlier-candidate reference sweep

This rechecks the candidate paths from dead-code-map.md; it is a conservative identifier closure, not a new proof
of all unreachable code. References include comments/strings and simple-name collisions, deliberately retaining
possible consumers. Test-only consumers are explicit. Missing historical files are recorded rather than removed
again. Every extant public type remains UNSURE until entry/reflective/variant/API closure and an approved build.

| File | Source bytes | Other production refs | Test refs | XML / patch refs | Disposition |
| --- | ---: | ---: | ---: | ---: | --- |
| `src/main/java/com/archos/mediacenter/video/widget/VideoWidgetServices.java` | 0 | — | — | — | already absent |
| `src/main/java/com/archos/mediacenter/video/info/ActionButton.java` | 0 | — | — | — | already absent |
| `src/main/java/com/archos/mediacenter/video/leanback/CompatibleCursorLoaderConverter.java` | 0 | — | — | — | already absent |
| `src/main/java/com/archos/mediacenter/video/leanback/FullTextRow.java` | 0 | — | — | — | already absent |
| `src/main/java/com/archos/mediacenter/video/leanback/FullTextRowPresenter.java` | 0 | — | — | — | already absent |
| `src/main/java/com/archos/mediacenter/video/autoscraper/VideoThumbProcessor.java` | 0 | — | — | — | already absent |
| `src/main/java/com/archos/mediacenter/video/autoscraper/AutoScraperCache.java` | 0 | — | — | — | already absent |
| `src/main/java/com/archos/mediacenter/video/browser/MetaFile2ItemData.java` | 0 | — | — | — | already absent |
| `src/main/java/com/archos/mediacenter/video/collections/UserCollectionStore.java` | 0 | — | — | — | already absent |
| `src/main/java/com/archos/mediacenter/video/utils/CustomListPreference.java` | 0 | — | — | — | already absent |
| `src/main/java/com/archos/mediacenter/video/utils/BaseInfo.java` | 0 | — | — | — | already absent |
| `src/main/java/com/archos/mediacenter/video/utils/EpisodeInfo.java` | 0 | — | — | — | already absent |
| `src/main/java/com/archos/mediacenter/video/utils/VerifiedBackup.java` | 0 | — | — | — | already absent |
| `src/main/java/com/archos/mediacenter/video/utils/MovieInfo.java` | 0 | — | — | — | already absent |
| `src/main/java/com/archos/mediacenter/video/utils/SerialExecutor.java` | 0 | — | — | — | already absent |
| `src/main/java/com/archos/mediacenter/video/cover/AllMoviesProvider.java` | 0 | — | — | — | already absent |
| `src/main/java/com/archos/mediacenter/video/cover/VideoCover.java` | 0 | — | — | — | already absent |
| `src/main/java/com/archos/mediacenter/video/cover/RecentlyAddedVideosProvider.java` | 0 | — | — | — | already absent |
| `src/main/java/com/archos/mediacenter/video/cover/VideoSingleCursorCoverProvider.java` | 0 | — | — | — | already absent |
| `src/main/java/com/archos/mediacenter/video/cover/EpisodeCover.java` | 0 | — | — | — | already absent |
| `src/main/java/com/archos/mediacenter/video/cover/AllTVShowsProvider.java` | 0 | — | — | — | already absent |
| `src/main/java/com/archos/mediacenter/video/cover/CoverRoll3DVideo.java` | 0 | — | — | — | already absent |
| `src/main/java/com/archos/mediacenter/video/cover/BaseVideoCover.java` | 0 | — | — | — | already absent |
| `src/main/java/com/archos/mediacenter/video/cover/AllVideosProvider.java` | 0 | — | — | — | already absent |
| `src/main/java/com/archos/mediacenter/video/cover/MovieCover.java` | 0 | — | — | — | already absent |
| `src/main/java/com/archos/mediacenter/video/leanback/details/PreviewPeopleRail.java` | 0 | — | — | — | already absent |
| `src/main/java/com/archos/mediacenter/video/leanback/details/ArchosHorizontalGridView.java` | 0 | — | — | — | already absent |
| `src/main/java/com/archos/mediacenter/video/leanback/details/ArchosDetailsOverviewRow.java` | 0 | — | — | — | already absent |
| `src/main/java/com/archos/mediacenter/video/leanback/animes/AnimesByFragment.java` | 0 | — | — | — | already absent |
| `src/main/java/com/archos/mediacenter/video/leanback/movies/MoviesByFragment.java` | 0 | — | — | — | already absent |
| `src/main/java/com/archos/mediacenter/video/leanback/adapter/FtpShortcutMapper.java` | 0 | — | — | — | already absent |
| `src/main/java/com/archos/mediacenter/video/leanback/adapter/object/FtpShortcut.java` | 0 | — | — | — | already absent |
| `src/main/java/com/archos/mediacenter/video/leanback/adapter/object/FtpBrowse.java` | 0 | — | — | — | already absent |
| `src/main/java/com/archos/mediacenter/video/browser/loader/AnimesSelectionLoader.java` | 0 | — | — | — | already absent |
| `src/main/java/com/archos/mediacenter/video/browser/loader/UpNextLoader.java` | 0 | — | — | — | already absent |
| `src/main/java/com/archos/mediacenter/video/browser/loader/FilmsNCollectionsLoader.java` | 0 | — | — | — | already absent |
| `src/main/java/com/archos/mediacenter/video/browser/loader/AllAnimesLoader.java` | 0 | — | — | — | already absent |
| `src/main/java/com/archos/mediacenter/video/browser/filebrowsing/BrowserByUsb.java` | 0 | — | — | — | already absent |
| `src/main/java/com/archos/mediacenter/video/browser/adapters/CollectionsAdapter.java` | 0 | — | — | — | already absent |
| `src/main/java/com/archos/mediacenter/video/browser/adapters/RecyclerViewPresenterAdapterByCursor.java` | 0 | — | — | — | already absent |
| `src/main/java/com/archos/mediacenter/video/browser/presenter/HeaderShowPresenter.java` | 0 | — | — | — | already absent |
| `src/main/java/com/archos/mediacenter/video/browser/presenter/MoviePresenter.java` | 0 | — | — | — | already absent |
| `src/main/java/com/archos/mediacenter/video/browser/filebrowsing/network/FtpBrowser/FtpRootFragment.java` | 0 | — | — | — | already absent |
| `src/main/java/com/archos/mediacenter/video/browser/filebrowsing/network/FtpBrowser/FtpShortcutAdapter.java` | 0 | — | — | — | already absent |
| `src/main/java/com/archos/mediacenter/video/browser/adapters/mappers/SeasonHeaderCursorMapper.java` | 0 | — | — | — | already absent |
| `src/main/java/com/archos/mediacenter/video/utils/backup/HiddenItemReplacement.java` | 0 | — | — | — | already absent |
