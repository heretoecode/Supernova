# Android lint inventory

1401 findings.

| Count | Category | Issue | Severity | What it means |
|---:|---|---|---|---|
| 423 | Performance | `UnusedResources` | Warning | Unused resources |
| 353 | Internationalization:Bidirectional Text | `RtlHardcoded` | Warning | Using left/right instead of start/end attributes |
| 116 | Correctness | `PrivateResource` | Warning | Using private resources |
| 90 | Internationalization | `SetTextI18n` | Warning | TextView Internationalization |
| 63 | Internationalization | `HardcodedText` | Warning | Hardcoded text |
| 37 | Correctness | `SpUsage` | Warning | Using `dp` instead of `sp` for text sizes |
| 25 | Correctness | `PxUsage` | Warning | Using 'px' dimension |
| 24 | Usability | `ViewConstructor` | Warning | Missing View constructors for XML inflation |
| 22 | Internationalization:Bidirectional Text | `RtlSymmetry` | Warning | Padding and margin symmetry |
| 19 | Performance | `StaticFieldLeak` | Warning | Static Field Leaks |
| 15 | Correctness | `UseCompatTextViewDrawableXml` | Warning | Compat compound drawable attributes should be used on `TextView` |
| 14 | Correctness:Messages | `Typos` | Warning | Spelling error |
| 14 | Performance | `UselessParent` | Warning | Unnecessary parent layout |
| 13 | Performance | `NotifyDataSetChanged` | Warning | Invalidating All RecyclerView Data |
| 13 | Performance | `Overdraw` | Warning | Overdraw: Painting regions more than once |
| 11 | Correctness | `FragmentTagUsage` | Warning | Use FragmentContainerView instead of the <fragment> tag |
| 11 | Usability | `AlwaysShowAction` | Warning | Usage of `showAsAction=always` |
| 10 | Correctness | `UnusedAttribute` | Warning | Attribute unused on older versions |
| 10 | Usability | `Autofill` | Warning | Use Autofill |
| 9 | Internationalization | `RelativeOverlap` | Warning | Overlapping items in RelativeLayout |
| 8 | Accessibility | `ContentDescription` | Warning | Image without `contentDescription` |
| 8 | Correctness | `ApplySharedPref` | Warning | Use `apply()` on `SharedPreferences` |
| 8 | Correctness | `InflateParams` | Warning | Layout Inflation without a Parent |
| 8 | Correctness:Messages | `PluralsCandidate` | Warning | Potential Plurals |
| 7 | Performance | `ObsoleteLayoutParam` | Warning | Obsolete layout params |
| 6 | Performance | `MergeRootFrame` | Warning | FrameLayout can be replaced with `<merge>` tag |
| 5 | Performance | `NestedWeights` | Warning | Nested layout weights |
| 5 | Performance | `RedundantNamespace` | Warning | Redundant namespace |
| 4 | Correctness | `DiscouragedApi` | Warning | Using discouraged APIs |
| 4 | Performance | `InefficientWeight` | Warning | Inefficient layout weight |
| 4 | Usability:Icons | `IconDensities` | Warning | Icon densities validation |
| 3 | Correctness | `CutPasteId` | Warning | Likely cut & paste mistakes |
| 3 | Correctness:Messages | `UnusedTranslation` | Warning | Unused Translation |
| 3 | Security | `TrustAllX509TrustManager` | Warning | Insecure TLS/SSL trust manager |
| 2 | Correctness | `InconsistentLayout` | Warning | Inconsistent Layouts |
| 2 | Correctness | `SelectedPhotoAccess` | Warning | Behavior change when requesting photo library access |
| 2 | Correctness | `VectorRaster` | Warning | Vector Image Generation |
| 2 | Performance | `DrawAllocation` | Warning | Memory allocations within drawing code |
| 2 | Performance | `UseCompoundDrawables` | Warning | Node can be replaced by a `TextView` with compound drawables |
| 2 | Usability:Icons | `IconDuplicates` | Warning | Duplicated icons under different names |
| 2 | Usability:Typography | `TypographyEllipsis` | Warning | Ellipsis string can be replaced with ellipsis character |
| 1 | Accessibility | `ClickableViewAccessibility` | Warning | Accessibility in Custom Views |
| 1 | Accessibility | `LabelFor` | Warning | Missing accessibility label |
| 1 | Correctness | `AppLinkWarning` | Warning | App Link warning |
| 1 | Correctness | `PictureInPictureIssue` | Warning | Picture In Picture best practices not followed |
| 1 | Correctness | `RedundantLabel` | Warning | Redundant label on activity |
| 1 | Correctness | `ScrollViewSize` | Warning | ScrollView size validation |
| 1 | Correctness | `UseCompatLoadingForDrawables` | Warning | Should not call `Context.getDrawable` or `Resources.getDrawable` directly |
| 1 | Correctness | `UseSwitchCompatOrMaterialCode` | Warning | Replace usage of `Switch` widget |
| 1 | Correctness | `UseSwitchCompatOrMaterialXml` | Warning | Replace usage of `Switch` widget |
| 1 | Internationalization:Bidirectional Text | `RtlEnabled` | Warning | Using RTL attributes without enabling RTL support |
| 1 | Performance | `ObsoleteSdkInt` | Warning | Obsolete SDK_INT Version Check |
| 1 | Performance | `UnusedNamespace` | Warning | Unused namespace |
| 1 | Performance | `UsableSpace` | Warning | Using getUsableSpace() |
| 1 | Performance | `VectorPath` | Warning | Long vector paths |
| 1 | Security | `ExportedContentProvider` | Warning | Content provider does not require permission |
| 1 | Security | `InsecureBaseConfiguration` | Warning | Insecure Base Configuration |
| 1 | Security | `SetJavaScriptEnabled` | Warning | Using `setJavaScriptEnabled` |
| 1 | Usability:Icons | `IconDuplicatesConfig` | Warning | Identical bitmaps across various configurations |
| 1 | Usability:Icons | `IconLocation` | Warning | Image defined in density-independent drawable folder |

## Files with the most findings

| Count | File |
|---:|---|
| 141 | `res/values/strings.xml` |
| 84 | `res/values/supernova146_upstream_additions.xml` |
| 51 | `res/values/dimens.xml` |
| 37 | `src/main/java/com/archos/mediacenter/video/leanback/filebrowsing/UniversalFileBrowser.java` |
| 27 | `res/layout/browser_item_grid.xml` |
| 26 | `res/layout-sw500dp-notouch/player_controller_inside.xml` |
| 25 | `res/layout/leanback_details_description.xml` |
| 24 | `res/layout/player_controller_inside.xml` |
| 22 | `res/layout/player_controller_experimental.xml` |
| 18 | `src/main/java/com/archos/mediacenter/video/player/PlayerActivity.java` |
| 18 | `res/layout-land/player_controller_inside.xml` |
| 18 | `res/layout-sw600dp/player_controller_inside.xml` |
| 18 | `src/main/java/com/archos/mediacenter/video/player/PreviewPlaybackMenus.java` |
| 17 | `res/values/video_info_dimens.xml` |
| 17 | `res/layout/browser_item_list_common.xml` |
| 16 | `res/layout/leanback_details_overview.xml` |
| 16 | `res/values/styles.xml` |
| 16 | `res/layout/browser_item_details_common.xml` |
| 16 | `res/layout-land/video_info2_fragment_actions.xml` |
| 15 | `AndroidManifest.xml` |
| 15 | `res/layout/video_info2_fragment_actions.xml` |
| 14 | `res/layout/leanback_tvshow_details_description.xml` |
| 14 | `res/layout/subtitle_settings_dialog.xml` |
| 12 | `res/layout/leanback_tvshow_more_details_description.xml` |
| 11 | `res/layout/browser_item_details.xml` |
| 11 | `res/layout/browser_item_details_show.xml` |
| 11 | `res/layout/video_info2_scraper.xml` |
| 11 | `res/layout/video_picker_item.xml` |
| 10 | `res/layout/browser_item_list.xml` |
| 10 | `res/layout/browser_item_list_show.xml` |
| 10 | `src/main/java/com/archos/mediacenter/video/leanback/details/ArchosDetailsOverviewRowPresenter.java` |
| 10 | `res/layout/androidtv_subtitles_info_group.xml` |
| 10 | `res/layout/auto_scraper_processed_item.xml` |
| 9 | `src/main/java/com/archos/customizedleanback/widget/MyOrbView.java` |
| 9 | `res/layout/leanback_smb_item.xml` |
| 9 | `res/values-pt-rBR/strings.xml` |
| 9 | `res/layout/video_info2_file.xml` |
| 8 | `res/layout/leanback_season_info_text.xml` |
| 8 | `res/layout/floating_player.xml` |
| 7 | `res/layout/androidtv_fullwidth_row.xml` |
