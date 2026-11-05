# Dead-code map — read-only, conservative candidate map

No source or resource was removed. This is a removal-candidate map, **not a certificate that every candidate can never run**. Every manifest component and every resource XML type mention was treated conservatively as a potential entry point; classic screens were not discarded merely because Supernova normally shows a replacement. No runtime tracing, build or shrinker analysis was run.

## Entry points and UI policy

- AndroidManifest.xml was parsed for all activities, aliases, services, receivers and providers (106 declarations, including entries marked for manifest removal). Exported declarations and those with filters are listed individually below. The final merged manifest may change defaults through dependencies/flavors; “unspecified” does not mean exported=false.
- EntryActivity.onCreate routes using UiChoiceDialog.applicationIsInLeanbackMode, independently of try_new_ui. The phone/tablet MainActivity path therefore cannot be declared dead just because the modern TV UI exists.
- CustomApplication.onCreate calls FoundationUiPolicy.apply. For BuildConfig.FOUNDATION it writes try_new_ui=true even when restored false; non-Foundation variants preserve the old choice. PreviewSettings moves try_new_ui to disabled Legacy controls. There is no ordinary Foundation toggle to turn it off, but that is not a proof that classic classes are unreachable.
- VideoDetailsFragment.onCreateView retains native details when mVideo is null, mLaunchedFromPlayer is true, or try_new_ui is false. It also instantiates the native view before inserting the preview replacement. The legacy superclass, layouts and actions are live dependencies.
- AllMoviesGridActivity.getFragmentInstance still directly constructs AllMoviesGridFragment, and equivalent manifest-listed legacy grid activities remain. Internal components must not be removed solely because they are not exported.
- Root seeds additionally include player/, leanback/settings/ and leanback/Preview* classes. All production source-set Java files and resource XML files were scanned for type-name references. Simple-name collisions and comment/string references were over-approximated to keep possibly live code.
- Class.forName sites reviewed: CustomApplication loads com.amazon.fireos.FireOSInit and android.media.VendorAudioTrackCallback conditionally. getIdentifier sites include VideoUtils language strings, SubtitleDiscovery credentials, VideoPreferencesCommon codepage labels, MiscUtils Android dimensions, PreviewDialog alertTitle, PreviewDiscovery credentials and PreviewLanguageIcon bundled flags. Those resources cannot be removed from a literal-reference count alone.

Graph inventory: 855 production Java files across source sets; 288 conservative root files; 803 files reached. Candidate list: 46 app classes, 5435 source lines. No resources were certified unreachable.

## Removal candidates

A row means no path from the conservative roots was found in this source graph. Any remaining inbound references listed below originate inside disconnected groups. Risks include native/dependency reflection, variant manifests, test compilation and dynamically chosen resources. The entire requested proof of “never runs” remains a follow-up for candidates other than narrowly evidenced unused declarations.

| Class/resource | Why it appears unreachable / evidence | References | Size (lines) | Risk | What must change with it |
|---|---|---|---:|---|---|
| `src/main/java/com/archos/mediacenter/video/widget/VideoWidgetServices.java` | Unregistered nested widget-service wrappers; WidgetProviderVideo chooses separate RemoteViewsService* classes instead | none in production | 66 | low | Review associated tests/fixtures and public API consumers; remove no resources by inference |
| `src/main/java/com/archos/mediacenter/video/info/ActionButton.java` | No production caller or XML/manifest name found | none in production | 108 | medium | Review associated tests/fixtures and public API consumers; remove no resources by inference |
| `src/main/java/com/archos/mediacenter/video/leanback/CompatibleCursorLoaderConverter.java` | Empty top-level class; no production caller or XML/manifest name found | none in production | 22 | low | Review associated tests/fixtures and public API consumers; remove no resources by inference |
| `src/main/java/com/archos/mediacenter/video/leanback/FullTextRow.java` | No root path; referenced only within disconnected source group | `leanback/FullTextRowPresenter.java` | 36 | medium | Review associated tests/fixtures and public API consumers; remove no resources by inference |
| `src/main/java/com/archos/mediacenter/video/leanback/FullTextRowPresenter.java` | No production caller or XML/manifest name found | none in production | 99 | medium | Review associated tests/fixtures and public API consumers; remove no resources by inference |
| `src/main/java/com/archos/mediacenter/video/autoscraper/VideoThumbProcessor.java` | No production caller or XML/manifest name found | none in production | 85 | medium | Review associated tests/fixtures and public API consumers; remove no resources by inference |
| `src/main/java/com/archos/mediacenter/video/autoscraper/AutoScraperCache.java` | No production caller or XML/manifest name found | none in production | 142 | medium | Review associated tests/fixtures and public API consumers; remove no resources by inference |
| `src/main/java/com/archos/mediacenter/video/browser/MetaFile2ItemData.java` | No root path; referenced only within disconnected source group | `browser/filebrowsing/BrowserByUsb.java` | 68 | medium | Review associated tests/fixtures and public API consumers; remove no resources by inference |
| `src/main/java/com/archos/mediacenter/video/collections/UserCollectionStore.java` | No production caller or XML/manifest name found | none in production | 84 | medium | Review associated tests/fixtures and public API consumers; remove no resources by inference |
| `src/main/java/com/archos/mediacenter/video/utils/CustomListPreference.java` | No production caller or XML/manifest name found | none in production | 79 | medium | Review associated tests/fixtures and public API consumers; remove no resources by inference |
| `src/main/java/com/archos/mediacenter/video/utils/BaseInfo.java` | No root path; referenced only within disconnected source group | `utils/EpisodeInfo.java`, `utils/MovieInfo.java` | 83 | medium | Treat cover-roll providers, cover models and BaseInfo descendants as one group; audit external cover framework and resource names |
| `src/main/java/com/archos/mediacenter/video/utils/EpisodeInfo.java` | No root path; referenced only within disconnected source group | `cover/EpisodeCover.java` | 294 | medium | Treat cover-roll providers, cover models and BaseInfo descendants as one group; audit external cover framework and resource names |
| `src/main/java/com/archos/mediacenter/video/utils/VerifiedBackup.java` | No production caller or XML/manifest name found | none in production; tests: `VerifiedBackupTest.java`, `HistoricalBackupFixture.java` | 30 | medium | Keep or migrate the listed test/fixture clients first; production reachability alone does not permit deletion |
| `src/main/java/com/archos/mediacenter/video/utils/MovieInfo.java` | No root path; referenced only within disconnected source group | `cover/MovieCover.java` | 196 | medium | Treat cover-roll providers, cover models and BaseInfo descendants as one group; audit external cover framework and resource names |
| `src/main/java/com/archos/mediacenter/video/utils/SerialExecutor.java` | No production caller or XML/manifest name found | none in production | 55 | low | Review associated tests/fixtures and public API consumers; remove no resources by inference |
| `src/main/java/com/archos/mediacenter/video/cover/AllMoviesProvider.java` | No root path; referenced only within disconnected source group | `cover/CoverRoll3DVideo.java` | 51 | high | Treat cover-roll providers, cover models and BaseInfo descendants as one group; audit external cover framework and resource names |
| `src/main/java/com/archos/mediacenter/video/cover/VideoCover.java` | No root path; referenced only within disconnected source group | `cover/CoverRoll3DVideo.java`, `cover/VideoSingleCursorCoverProvider.java` | 159 | high | Treat cover-roll providers, cover models and BaseInfo descendants as one group; audit external cover framework and resource names |
| `src/main/java/com/archos/mediacenter/video/cover/RecentlyAddedVideosProvider.java` | No root path; referenced only within disconnected source group | `cover/CoverRoll3DVideo.java` | 49 | high | Treat cover-roll providers, cover models and BaseInfo descendants as one group; audit external cover framework and resource names |
| `src/main/java/com/archos/mediacenter/video/cover/VideoSingleCursorCoverProvider.java` | No root path; referenced only within disconnected source group | `cover/AllMoviesProvider.java`, `cover/AllVideosProvider.java`, `cover/RecentlyAddedVideosProvider.java` | 149 | high | Treat cover-roll providers, cover models and BaseInfo descendants as one group; audit external cover framework and resource names |
| `src/main/java/com/archos/mediacenter/video/cover/EpisodeCover.java` | No root path; referenced only within disconnected source group | `cover/VideoSingleCursorCoverProvider.java` | 191 | high | Treat cover-roll providers, cover models and BaseInfo descendants as one group; audit external cover framework and resource names |
| `src/main/java/com/archos/mediacenter/video/cover/AllTVShowsProvider.java` | No root path; referenced only within disconnected source group | `cover/CoverRoll3DVideo.java` | 124 | high | Treat cover-roll providers, cover models and BaseInfo descendants as one group; audit external cover framework and resource names |
| `src/main/java/com/archos/mediacenter/video/cover/CoverRoll3DVideo.java` | No production caller or XML/manifest name found | none in production | 436 | high | Treat cover-roll providers, cover models and BaseInfo descendants as one group; audit external cover framework and resource names |
| `src/main/java/com/archos/mediacenter/video/cover/BaseVideoCover.java` | No root path; referenced only within disconnected source group | `cover/CoverRoll3DVideo.java`, `cover/EpisodeCover.java`, `cover/MovieCover.java`, `cover/VideoCover.java` | 110 | high | Treat cover-roll providers, cover models and BaseInfo descendants as one group; audit external cover framework and resource names |
| `src/main/java/com/archos/mediacenter/video/cover/AllVideosProvider.java` | No root path; referenced only within disconnected source group | `cover/CoverRoll3DVideo.java` | 50 | high | Treat cover-roll providers, cover models and BaseInfo descendants as one group; audit external cover framework and resource names |
| `src/main/java/com/archos/mediacenter/video/cover/MovieCover.java` | No root path; referenced only within disconnected source group | `cover/CoverRoll3DVideo.java`, `cover/VideoSingleCursorCoverProvider.java` | 204 | high | Treat cover-roll providers, cover models and BaseInfo descendants as one group; audit external cover framework and resource names |
| `src/main/java/com/archos/mediacenter/video/leanback/details/PreviewPeopleRail.java` | No production caller or XML/manifest name found | none in production; tests: `PreviewPeopleRailTest.java` | 44 | medium | Keep or migrate the listed test/fixture clients first; production reachability alone does not permit deletion |
| `src/main/java/com/archos/mediacenter/video/leanback/details/ArchosHorizontalGridView.java` | No production caller or XML/manifest name found | none in production | 100 | medium | Review associated tests/fixtures and public API consumers; remove no resources by inference |
| `src/main/java/com/archos/mediacenter/video/leanback/details/ArchosDetailsOverviewRow.java` | No production caller or XML/manifest name found | none in production | 22 | medium | Review associated tests/fixtures and public API consumers; remove no resources by inference |
| `src/main/java/com/archos/mediacenter/video/leanback/animes/AnimesByFragment.java` | No production caller or XML/manifest name found | none in production | 381 | high | Review associated tests/fixtures and public API consumers; remove no resources by inference |
| `src/main/java/com/archos/mediacenter/video/leanback/movies/MoviesByFragment.java` | No production caller or XML/manifest name found | none in production | 410 | high | Review associated tests/fixtures and public API consumers; remove no resources by inference |
| `src/main/java/com/archos/mediacenter/video/leanback/adapter/FtpShortcutMapper.java` | No production caller or XML/manifest name found | none in production | 44 | medium | Review associated tests/fixtures and public API consumers; remove no resources by inference |
| `src/main/java/com/archos/mediacenter/video/leanback/adapter/object/FtpShortcut.java` | No root path; referenced only within disconnected source group | `leanback/adapter/FtpShortcutMapper.java` | 40 | medium | Review associated tests/fixtures and public API consumers; remove no resources by inference |
| `src/main/java/com/archos/mediacenter/video/leanback/adapter/object/FtpBrowse.java` | No production caller or XML/manifest name found | none in production | 41 | medium | Review associated tests/fixtures and public API consumers; remove no resources by inference |
| `src/main/java/com/archos/mediacenter/video/browser/loader/AnimesSelectionLoader.java` | No root path; referenced only within disconnected source group | `leanback/animes/AnimesByFragment.java` | 49 | medium | Review associated tests/fixtures and public API consumers; remove no resources by inference |
| `src/main/java/com/archos/mediacenter/video/browser/loader/UpNextLoader.java` | No production caller or XML/manifest name found | none in production | 83 | medium | Review associated tests/fixtures and public API consumers; remove no resources by inference |
| `src/main/java/com/archos/mediacenter/video/browser/loader/FilmsNCollectionsLoader.java` | No production caller or XML/manifest name found | none in production | 128 | medium | Review associated tests/fixtures and public API consumers; remove no resources by inference |
| `src/main/java/com/archos/mediacenter/video/browser/loader/AllAnimesLoader.java` | No production caller or XML/manifest name found | none in production | 93 | medium | Review associated tests/fixtures and public API consumers; remove no resources by inference |
| `src/main/java/com/archos/mediacenter/video/browser/filebrowsing/BrowserByUsb.java` | No production caller or XML/manifest name found | none in production | 43 | medium | Review associated tests/fixtures and public API consumers; remove no resources by inference |
| `src/main/java/com/archos/mediacenter/video/browser/adapters/CollectionsAdapter.java` | No production caller or XML/manifest name found | none in production | 51 | medium | Review associated tests/fixtures and public API consumers; remove no resources by inference |
| `src/main/java/com/archos/mediacenter/video/browser/adapters/RecyclerViewPresenterAdapterByCursor.java` | No production caller or XML/manifest name found | none in production | 255 | medium | Review associated tests/fixtures and public API consumers; remove no resources by inference |
| `src/main/java/com/archos/mediacenter/video/browser/presenter/HeaderShowPresenter.java` | No production caller or XML/manifest name found | none in production | 64 | medium | Review associated tests/fixtures and public API consumers; remove no resources by inference |
| `src/main/java/com/archos/mediacenter/video/browser/presenter/MoviePresenter.java` | No production caller or XML/manifest name found | none in production | 21 | medium | Review associated tests/fixtures and public API consumers; remove no resources by inference |
| `src/main/java/com/archos/mediacenter/video/browser/filebrowsing/network/FtpBrowser/FtpRootFragment.java` | No production caller or XML/manifest name found | none in production | 180 | high | Review associated tests/fixtures and public API consumers; remove no resources by inference |
| `src/main/java/com/archos/mediacenter/video/browser/filebrowsing/network/FtpBrowser/FtpShortcutAdapter.java` | No root path; referenced only within disconnected source group | `browser/filebrowsing/network/FtpBrowser/FtpRootFragment.java` | 200 | high | Review associated tests/fixtures and public API consumers; remove no resources by inference |
| `src/main/java/com/archos/mediacenter/video/browser/adapters/mappers/SeasonHeaderCursorMapper.java` | No production caller or XML/manifest name found | none in production | 113 | medium | Review associated tests/fixtures and public API consumers; remove no resources by inference |
| `src/main/java/com/archos/mediacenter/video/utils/backup/HiddenItemReplacement.java` | No production caller or XML/manifest name found | none in production; tests: `PortableHiddenItemsTest.java` | 103 | medium | Keep or migrate the listed test/fixture clients first; production reachability alone does not permit deletion |

## Looks obsolete but remains reachable

| Item | Reachable path / reason to keep |
|---|---|
| browser/MainActivity and phone browser categories | EntryActivity → UiChoiceDialog selects tablet/phone route; manifest and alias routes remain. |
| VideoDetailsActivity / VideoDetailsFragment and native details resources | Exported activity; native view created and retained for player-launched/native fallback paths even with try_new_ui=true. |
| Legacy movie, anime and TV grid fragments | Manifest-listed activities instantiate them directly via getFragmentInstance. |
| SubtitlesWizardActivity (phone) and TV wizard | Manifest subtitle action and internal player/details entry points; association/file management still implemented there. |
| SearchProviderVideo / VideoSearchProvider | Manifest providers; TV provider is exported and supports external queries. |
| AdditionalServiceSingleton | PlayerService and PlayerActivity bind to the external optional SuperNOVA service; not dead because that package is absent on one device. |
| SubtitleEngine / native playback hooks | Player constructs it after libavos startup; JNI entry points cannot be removed by Java reference counting. |
| WidgetProviderVideo and RemoteViewsService* | WidgetProviderVideo selects standalone services; XML/runtime widget integrations need variant/merged-manifest review before any removal. They are not certified dead. |
| Diagnostics entry points and PreviewScanReceiver | Manifest components, lifecycle hooks, typed event producers and broadcasts; must survive despite few direct constructors. |
| Subtitle/locale/provider resource families | getIdentifier and external/native resource consumers defeat simple direct R-reference counts. |
| Historical backup/test helpers in the candidate list | VerifiedBackup and HiddenItemReplacement have test/fixture clients; deletion would require moving or updating those clients. |

## Suggested removal order

1. Review the empty CompatibleCursorLoaderConverter declaration first; remove only after confirming no external API consumers.
2. Review self-contained unregistered wrappers (VideoWidgetServices) and unused SerialExecutor, preserving live standalone widget services.
3. Review unused adapters/mappers and small single-purpose helpers, together with every inbound client in their disconnected groups.
4. Migrate test-only legacy helpers into test sources where appropriate before removing production copies; keep historical backup fixtures intact.
5. Audit the entire cover-roll / BaseInfo model group and disconnected old browse fragments against variant XML, dependency callbacks and reflection before considering removal.
6. Only then evaluate resource families with the owner’s merged-manifest/build analysis. No resource or manifest deletion is authorized by this map.

## Manifest entry-point inventory

The exported column records only the explicit source attribute; enabled/filters/permissions and manifest-merger overrides still matter. Entries with tools:node=remove (the Sentry providers) are not asserted installed components.

| Kind | Name | Exported | Enabled | Intent filters | Alias target | Permission |
|---|---|---|---|---:|---|---|
| activity | `.foundation.FoundationAboutActivity` | false | true | 0 | — | — |
| activity | `.EntryActivity` | true | true | 0 | — | — |
| activity | `.browser.MainActivity` | unspecified | true | 0 | — | — |
| activity-alias | `${applicationId}.browser.BrowserActivityVideo` | true | true | 1 | .EntryActivity | — |
| activity-alias | `.browser.MainActivityStereo` | unspecified | true | 0 | .browser.MainActivity | — |
| activity | `.browser.QueryBrowserActivityVideo` | true | true | 1 | — | — |
| provider | `com.archos.mediacenter.video.browser.SearchProviderVideo` | false | true | 0 | — | — |
| provider | `androidx.core.content.FileProvider` | false | true | 0 | — | — |
| activity | `.player.PlayerActivity` | true | true | 6 | — | — |
| activity | `.autoscraper.AutoScraperActivity` | true | true | 1 | — | — |
| activity | `.utils.SubtitlesWizardActivity` | true | true | 1 | — | — |
| activity | `.utils.credentialsmanager.CredentialsManagerPreferenceActivity` | unspecified | true | 0 | — | — |
| activity | `.utils.VideoPreferencesActivity` | true | true | 1 | — | — |
| activity | `.utils.TraktDeviceAuthActivity` | true | true | 2 | — | — |
| activity | `.utils.VideoPreferencesLicencesActivity` | true | true | 1 | — | — |
| activity | `.browser.VideoPicker` | true | true | 1 | — | — |
| activity | `.utils.SubtitlesDownloaderActivity2` | unspecified | true | 0 | — | — |
| activity | `.player.SDKNotSupportedDialogActivity` | unspecified | true | 0 | — | — |
| activity | `com.archos.filecorelibrary.samba.SharedPasswordRequest` | unspecified | true | 0 | — | — |
| activity | `com.archos.mediacenter.utils.HelpOverlayActivity` | unspecified | true | 0 | — | — |
| service | `com.archos.mediacenter.filecoreextension.upnp2.NovaUpnpService` | unspecified | true | 0 | — | — |
| service | `.player.FloatingPlayerService` | false | true | 0 | — | — |
| service | `com.archos.mediacenter.video.player.PlayerService` | false | true | 0 | — | — |
| service | `.player.ExternalPlayerService` | false | true | 0 | — | — |
| service | `com.archos.mediascraper.AutoScrapeService` | unspecified | true | 0 | — | — |
| service | `com.archos.mediascraper.AllCollectionScrapeService` | unspecified | true | 0 | — | — |
| service | `com.archos.mediacenter.video.browser.FileManagerService` | unspecified | true | 0 | — | — |
| service | `com.archos.mediacenter.video.utils.MediaLibraryBackupService` | false | true | 0 | — | — |
| receiver | `com.archos.mediacenter.LibAvosReceiver` | false | false | 5 | — | — |
| receiver | `com.archos.mediaprovider.video.NetworkAutoRefresh` | false | true | 3 | — | — |
| service | `com.archos.mediaprovider.video.NetworkRefreshJob` | unspecified | true | 0 | — | android.permission.BIND_JOB_SERVICE |
| provider | `com.archos.mediaprovider.video.VideoProvider` | false | true | 0 | — | — |
| receiver | `com.archos.mediaprovider.video.VideoStoreImportReceiver` | true | true | 5 | — | — |
| service | `com.archos.mediaprovider.MediaThumbnailService` | unspecified | true | 0 | — | — |
| service | `com.archos.mediaprovider.MediaRetrieverService` | false | true | 0 | — | — |
| service | `com.archos.mediaprovider.video.VideoStoreImportService` | unspecified | true | 0 | — | — |
| service | `com.archos.mediaprovider.video.NetworkScannerServiceVideo` | unspecified | true | 0 | — | — |
| service | `com.archos.mediacenter.utils.trakt.TraktService` | unspecified | true | 0 | — | — |
| receiver | `com.archos.mediascraper.receiver.ScraperReceiver` | false | true | 2 | — | — |
| activity | `com.archos.filecorelibrary.samba.SambaPreferenceActivity` | unspecified | true | 0 | — | — |
| activity | `.info.VideoInfoActivity` | unspecified | true | 0 | — | — |
| activity | `.info.VideoInfoScraperActivity` | unspecified | true | 0 | — | — |
| activity | `.info.VideoInfoPosterBackdropActivity` | unspecified | true | 0 | — | — |
| activity | `.utils.FolderPicker` | true | true | 0 | — | — |
| service | `com.archos.mediaprovider.video.RemoteStateService` | false | true | 0 | — | — |
| receiver | `com.archos.mediacenter.video.utils.PreviewScanReceiver` | false | true | 2 | — | — |
| receiver | `com.archos.filecorelibrary.ExtStorageReceiver` | true | true | 1 | — | — |
| service | `com.archos.mediascraper.NfoExportService` | false | true | 0 | — | — |
| service | `.browser.UpdateRecommendationsService` | unspecified | true | 0 | — | — |
| activity | `com.archos.mediacenter.video.leanback.MainActivityLeanback` | true | true | 1 | — | — |
| activity | `com.archos.mediacenter.video.leanback.movies.AllMoviesGridActivity` | unspecified | true | 0 | — | — |
| activity | `com.archos.mediacenter.video.leanback.animes.AllAnimesGridActivity` | unspecified | true | 0 | — | — |
| activity | `com.archos.mediacenter.video.leanback.tvshow.AllAnimeShowsGridActivity` | unspecified | true | 0 | — | — |
| activity | `com.archos.mediacenter.video.leanback.tvshow.AllTvshowsGridActivity` | unspecified | true | 0 | — | — |
| activity | `com.archos.mediacenter.video.leanback.tvshow.DocumentaryTvshowsGridActivity` | unspecified | true | 0 | — | — |
| activity | `com.archos.mediacenter.video.leanback.collections.AllCollectionsGridActivity` | unspecified | true | 0 | — | — |
| activity | `com.archos.mediacenter.video.leanback.collections.AllAnimeCollectionsGridActivity` | unspecified | true | 0 | — | — |
| activity | `com.archos.mediacenter.video.leanback.tvshow.AnimeShowsByAlphaActivity` | unspecified | true | 0 | — | — |
| activity | `com.archos.mediacenter.video.leanback.collections.CollectionActivity` | true | true | 0 | — | — |
| activity | `com.archos.mediacenter.video.leanback.tvshow.TvshowActivity` | true | true | 0 | — | — |
| activity | `com.archos.mediacenter.video.leanback.tvshow.TvshowMoreDetailsActivity` | unspecified | true | 0 | — | — |
| activity | `com.archos.mediacenter.video.leanback.details.PreviewSeasonActivity` | false | true | 0 | — | — |
| activity | `com.archos.mediacenter.video.leanback.tvshow.SeasonActivity` | unspecified | true | 0 | — | — |
| activity | `com.archos.mediacenter.video.leanback.movies.MoviesByAlphaActivity` | unspecified | true | 0 | — | — |
| activity | `com.archos.mediacenter.video.leanback.movies.MoviesByGenreActivity` | unspecified | true | 0 | — | — |
| activity | `com.archos.mediacenter.video.leanback.movies.MoviesByRatingActivity` | unspecified | true | 0 | — | — |
| activity | `com.archos.mediacenter.video.leanback.movies.MoviesByYearActivity` | unspecified | true | 0 | — | — |
| activity | `com.archos.mediacenter.video.leanback.animes.AnimesByAlphaActivity` | unspecified | true | 0 | — | — |
| activity | `com.archos.mediacenter.video.leanback.animes.AnimesByGenreActivity` | unspecified | true | 0 | — | — |
| activity | `com.archos.mediacenter.video.leanback.animes.AnimesByRatingActivity` | unspecified | true | 0 | — | — |
| activity | `com.archos.mediacenter.video.leanback.animes.AnimesByYearActivity` | unspecified | true | 0 | — | — |
| activity | `com.archos.mediacenter.video.leanback.tvshow.TvshowsByAlphaActivity` | unspecified | true | 0 | — | — |
| activity | `com.archos.mediacenter.video.leanback.tvshow.TvshowsByGenreActivity` | unspecified | true | 0 | — | — |
| activity | `com.archos.mediacenter.video.leanback.tvshow.TvshowsByRatingActivity` | unspecified | true | 0 | — | — |
| activity | `com.archos.mediacenter.video.leanback.tvshow.EpisodesByDateActivity` | unspecified | true | 0 | — | — |
| activity | `com.archos.mediacenter.video.leanback.VideosByListActivity` | unspecified | true | 0 | — | — |
| activity | `com.archos.mediacenter.video.leanback.details.VideoDetailsActivity` | true | true | 0 | — | — |
| activity | `com.archos.mediacenter.video.diagnostics.DiagnosticExportActivity` | false | true | 0 | — | — |
| activity | `com.archos.mediacenter.video.leanback.details.VideoDetailsOverlayActivity` | unspecified | true | 0 | — | — |
| activity | `com.archos.mediacenter.video.leanback.scrapping.ManualVideoScrappingActivity` | unspecified | true | 0 | — | — |
| activity | `com.archos.mediacenter.video.leanback.scrapping.ManualShowScrappingActivity` | unspecified | true | 0 | — | — |
| activity | `com.archos.mediacenter.video.leanback.filebrowsing.LocalListingActivity` | unspecified | true | 0 | — | — |
| activity | `com.archos.mediacenter.video.leanback.filebrowsing.ExtStorageListingActivity` | unspecified | true | 0 | — | — |
| activity | `com.archos.mediacenter.video.leanback.network.NetworkRootActivity` | unspecified | true | 0 | — | — |
| activity | `com.archos.mediacenter.video.leanback.network.NetworkShortcutDetailsActivity` | unspecified | true | 0 | — | — |
| activity | `com.archos.mediacenter.video.leanback.network.ftp.FtpListingActivity` | unspecified | true | 0 | — | — |
| activity | `com.archos.mediacenter.video.leanback.network.smb.SmbListingActivity` | unspecified | true | 0 | — | — |
| activity | `com.archos.mediacenter.video.leanback.network.upnp.UpnpListingActivity` | unspecified | true | 0 | — | — |
| activity | `com.archos.mediacenter.video.leanback.network.webdav.WebdavListingActivity` | unspecified | true | 0 | — | — |
| activity | `com.archos.mediacenter.video.leanback.network.smbj.SmbjListingActivity` | unspecified | true | 0 | — | — |
| activity | `com.archos.mediacenter.video.leanback.network.sshj.SshjListingActivity` | unspecified | true | 0 | — | — |
| activity | `com.archos.mediacenter.video.leanback.nonscraped.NonScrapedVideosActivity` | unspecified | true | 0 | — | — |
| activity | `com.archos.mediacenter.video.leanback.search.VideoSearchActivity` | true | true | 1 | — | — |
| provider | `com.archos.mediacenter.video.leanback.search.VideoSearchProvider` | true | true | 0 | — | — |
| activity | `com.archos.mediacenter.video.utils.WebViewActivity` | false | true | 0 | — | — |
| activity | `com.archos.mediacenter.video.leanback.network.rescan.RescanActivity` | unspecified | true | 0 | — | — |
| activity | `com.archos.mediacenter.video.leanback.wizard.SubtitlesWizardActivity` | unspecified | true | 0 | — | — |
| activity | `com.archos.mediacenter.video.leanback.LibraryHealthActivity` | false | true | 0 | — | — |
| activity | `com.archos.mediacenter.video.leanback.CustomLibraryActivity` | false | true | 0 | — | — |
| activity | `com.archos.mediacenter.video.leanback.settings.VideoSettingsActivity` | unspecified | true | 0 | — | — |
| activity | `com.archos.mediacenter.video.leanback.settings.VideoSettingsLicencesActivity` | unspecified | true | 0 | — | — |
| activity | `com.archos.mediacenter.video.leanback.settings.VideoSettingsMoreLeanbackActivity` | unspecified | true | 0 | — | — |
| provider | `io.sentry.android.core.SentryInitProvider` | unspecified | true | 0 | — | — |
| provider | `io.sentry.android.core.SentryPerformanceProvider` | unspecified | true | 0 | — | — |
| activity | `com.archos.mediacenter.video.streaming.PreviewRemoteDetailsActivity` | false | true | 0 | — | — |
| activity | `com.archos.mediacenter.video.streaming.putio.PutioBrowserActivity` | false | true | 0 | — | — |
