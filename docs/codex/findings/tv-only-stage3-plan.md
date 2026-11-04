# TV-only stages 3–6 — dependency order proposal, 2026-11-04

Read-only. No deletion, source/manifest/resource/test edit, merge, rebase or build was performed. Current preparation base is `160b429f014c86833ae89451f88c58e6acd1e913`. Historical evidence below is pinned to fetched `codex/0154-2026-11-03` tip **c43034178**; file:line references in this document refer to that branch unless explicitly labelled current. It was inspected using git show/git grep without switching branches.

The branch report is `docs/codex/reports/2026-11-03-0154-2026-11-03.md`. Stage 1 is `0644adb14`; stage 2 consists of `712dac6f7` **and corrective commit `377d16136`**. The correction preserves historical backup tests and a test-only VerifiedBackup writer; never replay just the first stage-2 deletion. These stages are not ancestors of the present preparation base. Before implementation, transplant/reconcile their intended behavior on the then-current app tip and rerun their checks; do not treat their files as already removed from the current branch. This plan does not authorize merging either branch.

## Exact stage-3 blockers at the stopped branch

Paths are relative to `src/main/java/com/archos/mediacenter/video/` except XML.

| Caller | Live dependency on original stage 3 (S11–S17) | Required grouping |
| --- | --- | --- |
| `browser/BrowserActivity.java:43,183` | Imports and `new BrowserListOfSeasons()` in the cover launch receiver; :181 is explanatory comment, :183 is actual construction. | Remove phone host with its fragments in revised stage 4, after surviving contracts are moved. |
| `browser/MainActivity.java:90–91,490–491` | Imports BrowserListOfSeasons and CursorBrowserByVideo; sets SUBCATEGORY_NAME and instantiates the season browser. | Same stage-4 cohort; first extract the player stereo state discussed below. |
| `browser/BrowserCategoryVideo.java:36–47,70–73,327–363` | Class routing table uses BrowserAllMovies, BrowserMoviesByYear/Genre2; switch instantiates/routes LastAdded, NonScraped, LastPlayed, Playlists, NeverPlayed, AllTvShows, AllVideos. | Remove category/router and shell with library screens; leaving imports in an unreachable method still breaks compilation. |
| `browser/QueryBrowserActivityVideo.java:25,33,55–57` | Typed BrowserByQuery field, cast and constructor. Main manifest :158,184 refers to this searchable Activity; :191 declares its exported host. | Stage 3 preserves external query contract via TV routing, stage 4 retires host+fragment together. |
| `browser/adapters/GroupOfMovieAdapter.java:31,82–84,161` | BrowserMoviesBy column constants (`name`, `list`, `number`) and getMultiposterThumbnailRequest. BrowserMoviesBy constructs adapter (:172), BrowserPlaylists consumes it (:116). | Add adapter to stage-4 cohort; it was outside the map's S11–S27 candidates. If a new retained client is found, extract all four columns including `po_file_list` and thumbnail helper into a neutral grouping helper first. Do not retain an adapter importing a deleted screen. |
| `cover/TvShowCover.java:22,24,210,214` | BrowserListOfSeasons.class.getName plus BrowserActivity.FRAGMENT_NAME/FRAGMENT_ARGS in launch Intent. | Add this remaining cover implementation to stage 4 after dependency/native/reflection search; no other Java construction/reference found on the stopped branch. Do not confuse it with retained Video/Show domain models. |
| `res/layout/browser_item_header_show.xml:88,110` | Inflates ArchosFadeView, which original stage 3 would delete. BrowserWithShowHeader uses this layout. | Remove this phone-only layout with its last owner in stage 4 (not deferred to stage 6), including qualifier/include checks. |
| `browser/Browser.java:840` | Uses widget Utils recycling helper. Widget factories/services/provider/configuration are mutually dependent; deleting Utils ahead of Browser creates another edge. | Stage 3 extracts the bitmap helper or defers Utils with Browser; proposed exact choice below is defer Utils to stage 4. |

The original stage-3 indexed-screen classes also inherit each other (`CursorBrowserByVideo extends BrowserByVideoObjects`; BrowserMoviesBy/BrowserWithShowHeader and leaf subclasses) and exchange extras. Therefore deleting only the leaf list in the old map or moving just MainActivity is not sufficient. Revised stage 4 includes both sides of these inheritance/routing edges.

## Further external callers that must be resolved

A branch-wide identifier pass over S11–S27, excluding those target files, found these additional actual dependencies (same-name TV subtitle imports and UI prose were disambiguated):

| Retained or omitted caller | Exact dependency | Resolution before deletion |
| --- | --- | --- |
| `player/PlayerActivity.java:129,770,4243` | MainActivity.mStereoForced initializes VideoEffect and affects plugged-on-TV behavior. MainActivity declares it at :215 and resets/reads `stereo_mode` at :226–231. | Stage 3 moves ownership into player-side state with identical launch/reset semantics. Prefer a launch-scoped player contract, not a permanently sticky new global. Update all reads/writes together and test successive launches. |
| `utils/PlayUtils.java:523` | VideoPreferencesActivity.ALLOW_3RD_PARTY_PLAYER and DEFAULT. | Stage 3 moves stable key `allow_3rd_party_player` and false default into retained VideoPreferencesCommon/neutral playback preference contract; keep temporary aliases until phone consumers are deleted. |
| `utils/VideoPreferencesCommon.java:1010` | Launches phone VideoPreferencesLicencesActivity. This common class is used by TV settings. | Stage 3 routes this action to existing `leanback/settings/VideoSettingsLicencesActivity` (which hosts LicenseDisclosure), preserving navigation and disclosure content. Delete no shared licence assets. |
| `browser/filebrowsing/network/UpnpSmbCommonRootFragment.java:23–47` | Extends NewRootFragment, implements WorkgroupShortcutAndServerAdapter.OnShareOpenListener, imports BrowserBySmb/BrowserByUpnp/BrowserCategory and shares BrowserByNetwork extras. | Add to stage 4 with SmbRootFragment and UpnpRootFragment; both extend it (:52/:43 respectively). No retained TV Java caller found. It is outside `leanback/filebrowsing/`, so it is not the hands-off TV browser. |
| `browser/tools/MultipleSelectionManager.java:33,41,54,60,217,231` | Browser-typed field/constructor and BrowserByNetwork.KEY_NETWORK_BOOKMARKS. Only Browser creates/holds it (:1073,1188). | Add this one phone action-mode class to stage 4. Do not remove the entire browser/tools package: TV operations may use its siblings. |
| `src/noamazon/AndroidManifest.xml:37–81`, `src/manageexternalstorage/AndroidManifest.xml:39–82` | Widget configuration, receiver and five RemoteViewsService declarations; provider metadata points to videowidgetinfo. | Remove these declarations with widget classes in stage 3. Original map's “no widget manifest” conclusion was incomplete because it only saw the main manifest. |

Stage-1 extractions already present **on the old branch** must survive: UiModePolicy replaces UiChoiceDialog policy; VideoInfoContract owns metadata extras; NetworkBrowserPolicy owns the 8000-ms FTP delay; VideosByListLoader owns COLUMN_NUMBER_OF_MOVIES without importing BrowserMoviesBy. Verify these are ported correctly before doing the additional extractions above. `VideoInfoCommonClass`, `VideoPreferencesCommon`, model/mappers/loaders, subtitle enumeration/backends, FolderPicker and TV credential/wizard hosts remain. A type name `SubtitlesWizardActivity` in PlayerActivity or VideoDetailsFragment resolves to `leanback/wizard/`, not the removable `utils/` class.

## Revised four commits after stages 1–2

This is a regrouping proposal for a future implementation run. Each boundary includes required Java, manifest, XML and test changes; only exclusive leftover resources/strings wait for stage 6. A full build is a gate for each proposed commit, not something established by this static plan. No stub classes, disabled tests or empty click handlers should be introduced to make a boundary compile.

### Stage 3/6 — remaining neutral contracts, search routing, widgets

Keep **all phone library and shell classes** for this stage. Perform the stereo, external-player preference and licence routing extractions above. Keep exact keys/defaults and launch data; don't tie common contracts to a phone Activity. Preserve EntryActivity forwarding and TV-only feature declarations from stage 1.

For QueryBrowserActivityVideo, preserve the exported component as a small compatibility forwarding host for this boundary, retaining ACTION_SEARCH/ACTION_VIEW query/data/MIME/extras/URI grants/ClipData to `leanback/search/VideoSearchActivity`. Inspect that host's accepted query semantics and add the adapter there if necessary. Update application/Activity default_searchable metadata and searchable XML together; don't remove the external component while metadata still names it. Delete BrowserByQuery only in stage 4. This stage is routing, not a claim that a class renamed “TV” automatically handles old searches.

Remove as one widget unit: all five `RemoteViewsFactory{AllVideos,Movies,RecentlyAdded,RecentlyPlayed,TVShows}`, `RemoteViewsFactoryBase`, all five corresponding `RemoteViewsService*`, `VideoWidgetConfigure`, `WidgetProviderVideo`, variant widget manifests and exclusively widget provider/configuration/item XML. `VideoWidgetServices` was already removed by old stage 2. **Leave `widget/Utils.java` until stage 4** because Browser uses its helper; dead-but-compilable helper retention is safe. Don't delete `BrowserActivityVideo` entry alias, Android TV recommendations/channels or Watch Next services.

Tests with this stage: retain/port EntryActivityTest intent forwarding test; add query compatibility cases (SEARCH, VIEW, empty query, existing task/back, URI grants); stereo forced launch followed by ordinary launch/reset and rotation; external-player key/default continuity; TV licence action resolves and renders disclosure. Check merged noamazon and manageexternalstorage manifests for absence of widgets and presence of TV launcher/search/deep links, required leanback and non-required touchscreen. These are future tests, not executed here.

### Stage 4/6 — phone shell/library/network/info/settings/scraper dependency cohort

Delete S14–S27 candidates **together**, with S11–S13's deferred widget/Utils, and the additional four caller classes: GroupOfMovieAdapter, TvShowCover, UpnpSmbCommonRootFragment, MultipleSelectionManager. Exact map-derived files are listed in the appendix. Retire phone main/category/browser/activity, indexed leaves and bases, phone folder/network roots, info chooser/search hosts, phone preference/licence hosts, phone subtitle wizard and AutoScraperActivity as one dependency closure. Retain QueryBrowserActivityVideo as the stage-3 compatibility forwarder (rather than deleting the exported component); remove its old BrowserByQuery field/import/construction. It is the explicit exception to the S19 whole-file deletion list.

Remove declarations of deleted components, fragment/custom-view class XML and `browser_item_header_show.xml` **in the same stage**. For each retained layout that embeds a removed class, retain/extract that class or migrate the layout before deleting it; never leave a delayed inflation failure for stage 6. Update source-based conformance tests in this stage if they name deleted files: `SupernovaStyleConformanceTest.java:27` lists BrowserCategory.java; move that assertion to the retained equivalent or remove only the obsolete phone-only entry. Keep NextQaBrowserFocusTest's TV focus assertions (its “Browser” error message alone is not a type dependency).

By stage end, no surviving source should import the phone cohort and no live XML/manifest should instantiate it. Re-scan dependencies too: shared MediaLib classes can refer to app components by string, and Java identifier absence alone isn't proof of runtime isolation. Any new edge into a hands-off file is a retain/defer gate, not authorization to edit it.

Tests with this stage: compile all supported flavors and test sources, merged manifest/AAPT validation, inflation smoke checks of retained hosts; TV launch/search/VIEW aliases, local/WebDAV/FTP/SMB browsing and operations, playback/settings return, details metadata/results and subtitle wizard flows. Keep browser objects/mappers/loaders tests and backup restore suite (including HistoricalBackupFixture and VerifiedBackupTest from stage-2 correction). Check phone-only tests only after separating shared assertions. No library/database cleanup is part of screen deletion.

### Stage 5/6 — only classic TV code with proven replacement, plus neutral TV contracts

Do not wholesale delete S28–S36. Concrete remaining edges on the stopped branch:

- `leanback/MainFragment.java:87–119` imports classic destinations and :1273 onward routes to them; route only functions with a demonstrated Preview replacement, preserving filter/sort/view state, Back and focus. Unmigrated destinations and their fragments stay.
- `leanback/VideoViewClickedListener.java:209–218` launches CollectionActivity with CollectionFragment.EXTRA_COLLECTION and SHARED_ELEMENT_NAME. Retain CollectionActivity/Fragment and layout until an equivalent collection page and transition contract exist. Its :133–146 also uses TvshowMoreDetailsActivity/Fragment backdrop/color APIs.
- `leanback/tvshow/TvshowFragment.java:487–509` and subsequent season branches launch TvshowMoreDetailsActivity and SeasonActivity with their shared extras; retain those pairs until all active actions have replacements. `TvshowMoreDetailsDescriptionPresenter.java:80` depends on the former fragment's dominant color; move/delete that presenter only with its owner or extract the color source.
- `leanback/details/VideoDetailsFragment.java:2953–2954` borrows AllMoviesGridFragment.SORT_PARAM_KEY and SHOW_WATCHED_KEY. Extract exact key strings/defaults into a neutral library preference contract before deleting the grid fragment.
- `utils/VideoPreferencesCommon.java:1813–1838` consumes AllMoviesGridFragment, AllTvshowsGridFragment and AllAnimesGridFragment sortOrderIndexer arrays. Extract the **arrays/indices and semantics**, not only string constants, into a shared sort specification; update common settings and retained screens together before deleting their former owners.

Treat stage 5 as “extract contracts, migrate proven routes, retire closed classic pairs”. Delete Activity/Fragment/presenters/required class XML and sole-purpose tests in one unit per closed route within the stage. Leave the full closure of any retained collection/season/more-details route, including its dependencies, until a later feature migration. Keep VideoDetailsActivity/Fragment and TvshowActivity/Fragment: they host PreviewMoviePage and still have active native/null-media/player-launched fallback behavior. Do not delete native presenters just because the normal page looks modern.

Tests with this stage: sort-index/key migration and restore defaults, same library ordering/filter values, collection open and shared-element Back/focus, season/episode journeys, details launched from player/null-media fallback, search result routing. Device remote focus/Back and visual comparison are required for a replacement-equivalence claim. No supported TV behavior removal is part of this plan.

### Stage 6/6 — exclusive resource/string cleanup and final contract verification

Use S37–S45 as a **candidate** resource list, subtract resources already retired atomically in stages 3–5, and keep everything used by a deferred screen. Search every qualifier, includes, styles, manifest, getIdentifier/dynamic names, dependencies and native use. Remove each string key across translations only when its final consumer is gone. Keep licences, flags and shared dialogs. Manifest cleanup required for valid components happened with their deletion; this final stage audits rather than postpones component safety.

Tests with this stage: all supported resource/flavor builds and merged manifests; retained XML inflation, backup/settings/search/player/library tests; launcher and exported VIEW/SEARCH compatibility; accessibility/remote navigation and screenshots for retained TV pages. Check no unresolved component/resource names and required leanback/touchscreen declarations. A resource named androidtv_* or under res/layout is not automatically removable or retained: ownership decides.

## Hands-off and independent-build gates

Every listed hands-off path remains protected, including leanback/filebrowsing, PreviewMoviePage, style/settings/metadata helpers, PlayerService, SubtitlesWizardCommon, assets/foundation and design/docs trees. The plan neither deletes nor changes these. If a later route migration requires one of them, keep the old route/its closure in that stage and report the deferral. No engine/build.gradle/.github/tools changes belong to this TV cleanup.

Proposed sequence invariant: stage 3 leaves phone source dependencies intact while detaching surviving TV contracts; stage 4 removes the entire phone dependency cohort but preserves its external forwarding contract; stage 5 removes only classic closures after their retained callers are migrated/extracted; stage 6 retires only now-exclusive resources. Before each later implementation commit, re-run branch-wide reference checks, appropriate full build/tests and runtime route checks at that exact boundary. This static report makes the boundaries concrete but does not certify unbuilt code.

## Exact map file grouping for revised stage 4

The following original S14–S27 targets move together (paths relative to the app Java package). QueryBrowserActivityVideo is retained as the forwarding compatibility host, as noted above. Add the four closure files and widget/Utils explicitly listed above; do not remove shared classes by directory.

- S14: `browser/BrowserByIndexedVideos/ArchosFadeView.java`, `browser/BrowserByIndexedVideos/BrowserAllMovies.java`, `browser/BrowserByIndexedVideos/BrowserAllTvShows.java`, `browser/BrowserByIndexedVideos/BrowserAllVideos.java`, `browser/BrowserByIndexedVideos/BrowserByQuery.java`, `browser/BrowserByIndexedVideos/BrowserBySeason.java`.

- S15: `browser/BrowserByIndexedVideos/BrowserByShow.java`, `browser/BrowserByIndexedVideos/BrowserByVideoSelection.java`, `browser/BrowserByIndexedVideos/BrowserLastAdded.java`, `browser/BrowserByIndexedVideos/BrowserLastPlayed.java`, `browser/BrowserByIndexedVideos/BrowserListOfEpisodes.java`, `browser/BrowserByIndexedVideos/BrowserListOfSeasons.java`.

- S16: `browser/BrowserByIndexedVideos/BrowserMoviesBy.java`, `browser/BrowserByIndexedVideos/BrowserMoviesByGenre2.java`, `browser/BrowserByIndexedVideos/BrowserMoviesByYear.java`, `browser/BrowserByIndexedVideos/BrowserNeverPlayed.java`, `browser/BrowserByIndexedVideos/BrowserNonScraped.java`, `browser/BrowserByIndexedVideos/BrowserPlaylists.java`.

- S17: `browser/BrowserByIndexedVideos/BrowserVideosInPlaylist.java`, `browser/BrowserByIndexedVideos/BrowserWithShowHeader.java`, `browser/BrowserByIndexedVideos/CursorBrowserByVideo.java`.

- S18: `browser/Browser.java`, `browser/BrowserActivity.java`, `browser/BrowserByVideoObjects.java`, `browser/BrowserCategory.java`, `browser/BrowserCategoryVideo.java`, `browser/BrowserLayout.java`.

- S19: `browser/MainActivity.java`, `browser/NewVideosActionProvider.java`, `browser/QueryBrowserActivityVideo.java`.

- S20: `browser/filebrowsing/BrowserByExtStorage.java`, `browser/filebrowsing/BrowserByFolder.java`, `browser/filebrowsing/BrowserByLocalFolder.java`, `browser/filebrowsing/BrowserByVideoFolder.java`, `browser/filebrowsing/network/BrowserByNetwork.java`, `browser/filebrowsing/network/FtpBrowser/BrowserBySFTP.java`.

- S21: `browser/filebrowsing/network/FtpBrowser/FTPServerCredentialsDialog.java`, `browser/filebrowsing/network/NewRootFragment.java`, `browser/filebrowsing/network/RootFragmentAdapter.java`, `browser/filebrowsing/network/ShortcutAdapter.java`, `browser/filebrowsing/network/ShortcutRootFragment.java`, `browser/filebrowsing/network/SmbBrowser/BrowserBySmb.java`.

- S22: `browser/filebrowsing/network/SmbBrowser/SMBServerCredentialsDialog.java`, `browser/filebrowsing/network/SmbBrowser/SmbRootFragment.java`, `browser/filebrowsing/network/SmbBrowser/SmbWorkgroupShortcutAndServerAdapter.java`, `browser/filebrowsing/network/UpnpBrowser/BrowserByUpnp.java`, `browser/filebrowsing/network/UpnpBrowser/UpnpRootFragment.java`, `browser/filebrowsing/network/UpnpBrowser/UpnpShortcutAndServerAdapter.java`.

- S23: `browser/filebrowsing/network/WorkgroupShortcutAndServerAdapter.java`.

- S24: `info/VideoInfoActivity.java`, `info/VideoInfoActivityFragment.java`, `info/VideoInfoBackdropChooserFragment.java`, `info/VideoInfoPosterBackdropActivity.java`, `info/VideoInfoPosterChooserFragment.java`, `info/VideoInfoScraperActivity.java`.

- S25: `info/VideoInfoScraperSearchFragment.java`, `info/VideoInfoShowScraperFragment.java`.

- S26: `utils/SubtitlesWizardActivity.java`, `utils/VideoPreferencesActivity.java`, `utils/VideoPreferencesFragment.java`, `utils/VideoPreferencesLicencesActivity.java`, `utils/VideoPreferencesLicencesFragment.java`.

- S27: `autoscraper/AutoScraperActivity.java`.
