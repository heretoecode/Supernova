# 0.156 focus memory audit

Source: first merge `aa5c2474d`, 5–6 November 2026. Report only; no screen changes or tests added to `src/`.
Paths below are relative to `src/main/java/com/archos/mediacenter/video/` unless prefixed `MediaLib:`.
The audit inventories all 445 Java files under `leanback/` and `player/`, follows their activity/fragment hosts,
shared panels and preference routes, and includes `foundation/FoundationAboutWorkspace`. There is no sibling
`video/settings/` directory; Settings is `leanback/settings/`.

This is source evidence, not a claim that every transition has passed on a Shield. A retained view, activity
recreation and process death are separate cases. Every return should prefer a stable item identity and child control,
then the nearest surviving item, then the page's first usable control. A deliberately opened *new* page may have an
initial target. The older explicit rules “Down enters Filters” and “Playback Settings opens Subtitles” conflict with
literal last-position restoration on subsequent visits; these are identified below for Claude to reconcile.

## Route and transition coverage

In this table B = Back, D = open Details and return, P = open/close panel or choice, T = switch tab/section,
V = return from player, R = app resume/recreation. A dash means the route has no such operation.

| Page / panel | B, D, P | T | V, R and evidence |
| --- | --- | --- | --- |
| Home | D/P return to the same row/card; live anchors do so | Per-tab row anchors exist; hero index resets | Same live view can restore; custom anchors are not serialised. `PreviewPages:150`, `216`, `415`, `1147`, `1159`; F1/F3 |
| Movies, TV Shows | D returns to the stable title; P uses shared opener restoration | Top-bar Down deliberately replaces the anchor with Filters | Refresh preserves identities; recreation loses anchors. `PreviewPages:373`, `1552`, `1655`; F1/F2 |
| Custom Library | B/P returns to Edit/filter control; draft exit is guarded | Header controls have explicit tags | `CustomLibraryActivity:95`, `170`, `365` saves one tag, but rebuilt grids enter Filters unless tag starts `custom.control.`; F1/F2 |
| Details: Details | P returns to opening action; read-only facts are not focus targets | Reading position resets when leaving a tab | Live rebind preserves focused tag; new view starts Details/primary. `details/PreviewMoviePage:466`, `527`, `606`, `835`, `2254`; F4/F5 |
| Details: Extras | Shared panel restores opener | Re-entry always selects first extra | D/V may keep existing tree, new tree has no per-tab anchor. `details/PreviewMoviePage:559`, `726`; F4/F5 |
| Details: Seasons & Episodes, season page | Row-to-row motion remembers episode; child Details returns through activity | Re-entry chooses next episode rather than last browsed episode | `details/PreviewMoviePage:598`, `743`; `details/PreviewSeasonActivity:28`, `86`, `92`; F4/F5 |
| Details: More Like This | Child Details should return to same related title | Re-entry calls `lower.requestFocus()` | `details/PreviewMoviePage:561`; F4/F5 |
| Search | Results return by selected ID; keyboard remembers its key; modal opener is shared | Filter value persists; focused filter/recent chip does not | `search/PreviewSearch:223`, `461`, `500`, `695`; `search/VideoSearchActivity` delegates saved state; F6. Do not remove existing keyboard/result restoration |
| Network & Files landing | B from a section returns to its selected tile | Six tiles keep `landingSelection` in the live browser | No durable browser state for recreation. `filebrowsing/UniversalFileBrowser:214`, `260`, `439`, `1107`; F7/F8 |
| Local Storage: drives, folders, All files/Videos only | B returns to parent folder/drive; loading has a focus holder; row menu returns via shared dialog | Same-instance row identity is retained | Action-child identity and rebuilt-instance memory are absent. `filebrowsing/LocalStorageView:120`, `271`, `580`, `627`, `1522`; F9/F10 |
| Network Shares, saved connections, SMB/SMBJ/SFTP/SSHJ/FTP/FTPS/WebDAV/UPnP listings | B remembers directory and may restore live centre anchor | `openLandingCard` clears centre anchor; `focusOverview` selects first location outside landing/local | Folder URI is retained in a live map, not child control across recreation. `filebrowsing/UniversalFileBrowser:439`, `1107`, `1235`, `1471`; F7/F8 |
| Put.io account/files within Network Shares | P: account/choice dialogs use shared restoration; file list owns its row state | Re-entry to enclosing browser has F7/F8 | Account implementation also lies in `streaming/putio/`; `PutioConnectionFocusTest` covers its connection form. No independent new loss asserted here |
| Add Connection, Test connection, discovery, unsaved-changes choice | B guards a dirty connection; modal returns to caller | Form fields rebuilt on section re-entry | `filebrowsing/UniversalFileBrowser:439`, `2544`, `2980`; F7/F8. Never persist credentials as a focus key |
| Library Scanning, live scan status, history/schedule choices | P returns to invoking button; live status does not itself take focus | Section re-entry clears anchor | `filebrowsing/UniversalFileBrowser:439`, `scanControls`, `focusOverview`; F7/F8. Scan now is an action, not another page |
| Library Health embedded and full page, issue/action panes, Find it again | Live refresh retains issue tag and right-hand action; removed issues fall back to category | Same-instance category is retained | Full activity creates category zero; no saved category/issue/action. `LibraryHealthActivity:28`, `49`, `121`, `restoreIssue:235`; F11 |
| Settings grid and ordinary sections | B returns to the selected category; P restores opener; same-instance rows remembered by tag | Per-section anchors exist in memory | Recreation restores only current row and then focuses section tab. `settings/SettingsWorkspace:92`, `198`, `375`, `782`, `813`; F12/F13 |
| Playback: General, Resume, Skipping, Picture | Same Settings rule | All four sections use row anchors | F12/F13; `settings/SettingsLayout:51` |
| Audio & Subtitles: Audio, Subtitles, font browser | Same Settings rule; font browser has browser B/P behaviour | Two sections use row anchors | F12/F13; `settings/SettingsLayout:60`; external picker returning to retained screen relies on normal view focus |
| Library & Home: Library, Details, Home, History | Same Settings rule; editor is a separate modal | Four section anchors | F12/F13; `settings/SettingsLayout:69` |
| Where to Watch; Appearance | Same Settings rule; provider/language choice returns to opener | One section each | F12/F13; `settings/SettingsLayout:78`, `82` |
| Accounts & Services: Services, Trakt, OpenSubtitles | Shared modal returns; OpenSubtitles uses special form | Ordinary anchors; special form has its own controls | F12/F13 for workspace; `settings/SettingsWorkspace:432`; no assertion that account values are focus keys |
| System: Backup, Diagnostics, Advanced | Backup review/destination has explicit exit focus; normal sections use anchors | Backup is rebuilt as a special workspace | F12/F13, `settings/BackupSettingsController:588`, `settings/SettingsWorkspace:421`, `settings/SettingsLayout:91` |
| About: App, Credits, Licences/search/details, Version history, Device | Within one About object selection map and detail Back are handled | Settings constructs a fresh About object for every section | `foundation/FoundationAboutWorkspace:63`, `294`, `328`, `383`; `settings/SettingsWorkspace:401`; F14 |
| Playback Settings: Subtitles, Appearance, Audio, Playback; track/colour/delay/speed choices | Close restores player Settings control; nested Back restores expansion anchor | Switching resets `rootFocus`; reopening explicitly chooses Subtitles except shortcuts | `player/PreviewPlaybackMenus:71`, `118`, `251`, `772`; `player/PlaybackSubtitlePanel:606`, `1085`; F15, older initial-tab decision noted |
| Player HUD, seek bar, Chapters/Episodes, PiP return | Panel close calls `restorePreviewSettingsFocus`; player return should keep invoking item | Chapters/episode row has its own navigation | `player/PlayerActivity` owns native surface/HUD lifecycle; decoder/PiP focus cannot be established with source-only Robolectric. No fabricated hardware pass |
| Edit Page: main, name, years, genres/language/studio, Save/Delete, discard confirmation | Nested page Back restores its originating row; shared modal restores page opener | Nested return retains tag, but reopening starts Name | `EditPagePanel:135`, `184`, `658`, `713`; F16 |
| Customise Home: rows, visibility, move, row settings, create/delete, genre list | Back from row settings returns selected row; move cancellation is explicit | Same dialog keeps selected row; next invocation resets to row zero | `CustomiseHomeEditor:99`, `134`, `161`, `349`, `798`; F17 |
| Home Hide/Move controls | Back/finish returns to first card of controlled row; move retains Move | Not an independent tab | `HomeRowMover:162`, `188`, `289`; explicit first-card entry, not a lost off-row anchor |
| Shared choices, confirmation/read panels, inline selectors, Fix match, subtitle results | `PreviewDialog` restores actual opener, then semantic tag/id, then usable fallback | Choices initially focus current selected option; inline cancel preserves parent | `PreviewDialog:75`, `94`, `207`; `settings/SettingsChoiceDialog:132`; `PreviewMatchSearch:97`, `201`; `SubtitleResultsPanel:117`. Opening a fresh result list at result zero is initial entry, not proof of a return bug |

### Retained framework routes

These routes still exist and were included in the inventory; they are not silently treated as deleted. Their normal
B/D/V return is the retained Leanback fragment's selected row/grid position. Modal P uses the dialog/fragment window.
They have no modern top-level T; the activity opens a new category. Framework view restoration alone does not prove
stable identity restoration after loader reorder or process death, so these remain verification cases, not confirmed
losses without a reproducer.

| Route family (all members) | Source / owner of actual selection |
| --- | --- |
| Classic Home/Browse, list views | `MainFragment:1988`, `2003`; `CategoryBrowseFragment`, `ExperimentalBrowseFragment`, `VideosByFragment`, `VideosByListActivity/Fragment`; pending row is retained by adapter object, not modern semantic anchors |
| Movies by all/alpha/genre/rating/year; Non-scraped | `movies/AllMoviesGridFragment`, `MoviesByAlphaFragment`, `MoviesByGenreFragment`, `MoviesByRatingFragment`, `MoviesByYearFragment`; `nonscraped/NonScrapedVideosFragment`; their matching `*Activity` wrappers delegate to those fragments |
| Anime films, anime shows, collections | `animes/AllAnimesGridFragment`, `AnimesByAlphaFragment`, `AnimesByGenreFragment`, `AnimesByRatingFragment`, `AnimesByYearFragment`; `collections/AllAnimeCollectionsGridFragment`, `AllCollectionsGridFragment`, `CollectionFragment`; corresponding activity wrappers; inherited Leanback selection |
| TV, anime/documentary TV, episodes and seasons | `tvshow/AllTvshowsGridFragment`, `AllAnimeShowsGridFragment`, `DocumentaryTvshowsGridFragment`, `AnimeShowsByAlphaFragment`, `TvshowsByAlphaFragment`, `TvshowsByGenreFragment`, `TvshowsByRatingFragment`, `EpisodesByDateFragment`, `SeasonFragment`, `TvshowFragment`, `TvshowMoreDetailsFragment`, corresponding activity wrappers; `SeasonFragment` saves video/season data, not a semantic child focus key |
| Legacy local/network browsers | `filebrowsing/ListingFragment`, `LocalListingFragment`, `ExtStorageListingActivity`; `network/NetworkListingFragment`, `NetworkRootFragment`, `NetworkShortcutDetailsFragment`; FTP/SMB/SMBJ/SSHJ/SFTP/UPnP/WebDAV wrappers; `ListingFragment` owns selected-path restoration, protocols do not add their own focus memory |
| Legacy account / scan / wizard panels | `network/NetworkServerCredentialsDialog`, FTP/SMB credential dialogs; `network/rescan/RescanFragment`, `RescanWhenOpeningApplication`, `ScheduledRescanPeriod`, `ScheduledRescanShares`, `ScheduledRescanTime`; `wizard/SubtitlesWizardFragment`; framework dialog/GuidedStep selection |
| Legacy Search / manual matching | `search/VideoSearchFragment`, `scrapping/SafeSearchSupportFragment`, `ManualScrappingSearchFragment`, `ManualShowScrappingSearchFragment`, `ManualVideoScrappingSearchFragment`, their activity wrappers; SearchSupportFragment owns normal selection |
| Legacy Settings/licences/more | `settings/VideoSettingsFragment`, `VideoSettingsLicencesFragment`, `VideoSettingsMoreLeanbackFragment`, corresponding activity wrappers; Preference/Leanback hierarchy state; `PreviewSettings` keeps centre/left anchors while retained |
| Legacy player dialogs, floating/unsupported player | `player/AudioDelayPickerDialog`, `AudioSpeedPickerDialog`, `BrightnessDialog`, `SubtitleDelayPickerDialog`, `SubtitleSettingsDialog`, `FloatingPlayerActivity`, `SDKNotSupportedDialogActivity`; platform dialog/window focus, no stable page anchor added by these classes |

## Confirmed loss cases and proposed regressions

The table gives the concrete trigger, desired return, actual behaviour and report test. Source line references above
are the implementation evidence. Tests below assert the requested behaviour and are expected to fail until Claude
adds focus state. They are **not** marked passing and must not be weakened to match the resets.

| ID | Trigger → expected | Actual cause | Test below |
| --- | --- | --- | --- |
| F1 | Recreate Home/Movies/TV/custom grid → same title/control | `PreviewPages:150` stores arrays only in the view; no custom state save | A |
| F2 | Re-enter Movies/TV/custom from top navigation → previous control | `PreviewPages:373` overwrites anchor with first filter; `CustomLibraryActivity:376` invokes it on rebuild | B; reconcile older first-filter rule |
| F3 | Home → another tab → Home → same hero title | `PreviewPages:1159` sets `featuredIndex = 0` | C |
| F4 | Details tab → another tab → return into original content → same extra/episode/related item | `details/PreviewMoviePage:557` chooses next episode/first extra/default lower descendant | D |
| F5 | Leave a scrolled Details tab and return → its previous reading offset | `details/PreviewMoviePage:532` resets shared reading scroll | E |
| F6 | Recreate Search while focus is on a recent chip/filter → that chip | `search/PreviewSearch:500` saves key/result zone only; constructor `:227` returns non-result zones to keyboard | F |
| F7 | Network section → top bar → section content → same field/action/location | `filebrowsing/UniversalFileBrowser:1107` returns first location outside local/landing | G |
| F8 | Recreate Network browser → same section/URI/child | `filebrowsing/UniversalFileBrowser:439` clears anchor; section maps have no saved-state transport | H |
| F9 | Local row action → Details/player/panel return after row reload → same action | `filebrowsing/LocalStorageView:627` saves row identity only; `:606` focuses row | I |
| F10 | Rebuild Local Storage → same row in remembered folder | `filebrowsing/LocalStorageView:120` map is per-view; no saved-state transport | J |
| F11 | Recreate Library Health → same category/issue/action | `LibraryHealthActivity:28`, `121` creates category zero, no saved-state override | K |
| F12 | Recreate Settings with focus in content → same row | `settings/SettingsWorkspace:375` focuses tab even with saved row | L |
| F13 | Visit two Settings sections, recreate, return to first → its previous row | `settings/SettingsWorkspace:813` writes only current row, despite keeping all section names | M |
| F14 | About Licences/History → other About tab → back → selected entry/search state | `settings/SettingsWorkspace:401` creates new `FoundationAboutWorkspace`; its selections map is discarded | N |
| F15 | Switch Playback Settings tabs and re-enter content → previous row per tab | `player/PlaybackSubtitlePanel:572` rebuilds body and resets scroll; `PreviewPlaybackMenus:264` also resets root focus | O |
| F16 | Close/reopen Edit Page for same draft → previously focused edit row | `EditPagePanel:184` always requests Name | P |
| F17 | Close/reopen Customise Home → previously selected row | `CustomiseHomeEditor:99`, `150` always starts at row zero | Q |

## Ready Robolectric tests (report only)

Insert each method into the named **existing** test class, keeping its existing fixtures/imports/cleanup.
A–H need the shared helper block below in `PreviewPagesTest`; D/E/F/G/H use fully qualified class names.
L/M/N go in `settings/SettingsWorkspaceTest`; I/J in `filebrowsing/LocalStorageViewTest`; O in
`player/PreviewPlaybackMenusTest`; P in `EditPagePanelTest`. K and Q are standalone class blocks.
No method below needs a new library beyond the existing JUnit, Mockito and Robolectric dependencies.

### A–H: helpers and methods for PreviewPagesTest

```java
private void auditLayout(View view) {
    for (int pass = 0; pass < 6; pass++) {
        view.measure(View.MeasureSpec.makeMeasureSpec(960, View.MeasureSpec.EXACTLY),
                     View.MeasureSpec.makeMeasureSpec(540, View.MeasureSpec.EXACTLY));
        view.layout(0, 0, 960, 540);
        Shadows.shadowOf(Looper.getMainLooper()).idleFor(Duration.ofMillis(32));
    }
}

@Test
public void auditA_gridAnchorSurvivesRecreation() {
    var host = Robolectric.buildActivity(TopNavigationTest.Host.class).setup().visible();
    try {
        PreviewPages first = new PreviewPages(host.get(), (holder, item) -> {});
        first.setId(9001);
        host.get().setContentView(first);
        first.setTab(1);
        auditLayout(first);
        first.focusFromNavigation();
        auditLayout(first);
        Object[] before = ReflectionHelpers.getField(first, "anchors");
        assertNotNull(before[1]);
        android.util.SparseArray<android.os.Parcelable> state = new android.util.SparseArray<>();
        first.saveHierarchyState(state);
        PreviewPages next = new PreviewPages(host.get(), (holder, item) -> {});
        next.setId(9001);
        host.get().setContentView(next);
        next.restoreHierarchyState(state);
        Object[] after = ReflectionHelpers.getField(next, "anchors");
        assertNotNull("The semantic anchor must cross view recreation", after[1]);
    } finally {
        host.pause().stop().destroy();
    }
}

@Test
public void auditB_topNavigationKeepsLastLibraryControl() {
    var host = Robolectric.buildActivity(TopNavigationTest.Host.class).setup().visible();
    try {
        PreviewPages pages = new PreviewPages(host.get(), (holder, item) -> {});
        host.get().setContentView(pages);
        pages.setTab(1);
        auditLayout(pages);
        View previous = pages.findViewWithTag("control:1");
        assertNotNull(previous);
        assertTrue(previous.requestFocus());
        pages.focusFromNavigation();
        auditLayout(pages);
        assertSame(previous, pages.findFocus());
    } finally {
        host.pause().stop().destroy();
    }
}

@Test
public void auditC_homeHeroIndexSurvivesTabRoundTrip() {
    var host = Robolectric.buildActivity(TopNavigationTest.Host.class).setup().visible();
    try {
        PreviewPages pages = new PreviewPages(host.get(), (holder, item) -> {});
        host.get().setContentView(pages);
        ReflectionHelpers.setField(pages, "featuredIndex", 2);
        pages.setTab(1);
        pages.setTab(0);
        assertEquals(2, (int)ReflectionHelpers.getField(pages, "featuredIndex"));
    } finally {
        host.pause().stop().destroy();
    }
}

@Test
public void auditD_extrasTabReentersLastCard() {
    var host = Robolectric.buildActivity(TopNavigationTest.Host.class).setup().visible();
    try {
        var page = new com.archos.mediacenter.video.leanback.details.PreviewMoviePage(
            host.get(), androidx.leanback.widget.ArrayObjectAdapter::new, action -> {}, () -> {}, uri -> {});
        host.get().setContentView(page);
        LinearLayout extras = ReflectionHelpers.getField(page, "trailers");
        java.util.List<View> cards = ReflectionHelpers.getField(page, "extraCards");
        for (int i = 0; i < 2; i++) {
            Button card = new Button(host.get());
            card.setText("Extra " + i);
            extras.addView(card);
            cards.add(card);
        }
        ReflectionHelpers.callInstanceMethod(page, "showSection",
            ReflectionHelpers.ClassParameter.from(String.class, "Extras"),
            ReflectionHelpers.ClassParameter.from(boolean.class, true));
        auditLayout(page);
        assertTrue(cards.get(1).requestFocus());
        ReflectionHelpers.callInstanceMethod(page, "showSection",
            ReflectionHelpers.ClassParameter.from(String.class, "Details"),
            ReflectionHelpers.ClassParameter.from(boolean.class, false));
        ReflectionHelpers.callInstanceMethod(page, "showSection",
            ReflectionHelpers.ClassParameter.from(String.class, "Extras"),
            ReflectionHelpers.ClassParameter.from(boolean.class, true));
        assertSame(cards.get(1), page.findFocus());
    } finally {
        host.pause().stop().destroy();
    }
}

@Test
public void auditE_detailsTabKeepsItsReadingOffset() {
    var host = Robolectric.buildActivity(TopNavigationTest.Host.class).setup().visible();
    try {
        var page = new com.archos.mediacenter.video.leanback.details.PreviewMoviePage(
            host.get(), androidx.leanback.widget.ArrayObjectAdapter::new, action -> {}, () -> {}, uri -> {});
        host.get().setContentView(page);
        ScrollView reading = ReflectionHelpers.getField(page, "reading");
        reading.getChildAt(0).setMinimumHeight(5000);
        auditLayout(page);
        reading.scrollTo(0, 120);
        assertEquals(120, reading.getScrollY());
        for (String section : new String[] {"Extras", "Details"}) {
            ReflectionHelpers.callInstanceMethod(page, "showSection",
                ReflectionHelpers.ClassParameter.from(String.class, section),
                ReflectionHelpers.ClassParameter.from(boolean.class, false));
        }
        assertEquals(120, reading.getScrollY());
    } finally {
        host.pause().stop().destroy();
    }
}

@Test
public void auditF_recentSearchChipSurvivesRecreation() {
    var host = Robolectric.buildActivity(TopNavigationTest.Host.class).setup().visible();
    try {
        PreferenceManager.getDefaultSharedPreferences(host.get()).edit()
            .putString("preview_search_recent", "Film").commit();
        var first = new com.archos.mediacenter.video.leanback.search.PreviewSearch(host.get(), 0, null);
        host.get().setContentView(first);
        // Populate through the real recent-chip model, independent of background search results.
        LinearLayout recent = ReflectionHelpers.getField(first, "recentChips");
        Button chip = new Button(host.get());
        chip.setTag("search.recent.Film");
        recent.addView(chip);
        recent.setVisibility(View.VISIBLE);
        ((View)recent.getParent()).setVisibility(View.VISIBLE);
        auditLayout(first);
        assertTrue(chip.requestFocus());
        android.os.Bundle state = new android.os.Bundle();
        first.save(state);
        var next = new com.archos.mediacenter.video.leanback.search.PreviewSearch(host.get(), 0, state);
        host.get().setContentView(next);
        auditLayout(next);
        assertNotNull(next.findFocus());
        assertEquals("search.recent.Film", next.findFocus().getTag());
    } finally {
        host.pause().stop().destroy();
    }
}

@Test
public void auditG_networkContentEntryKeepsLastControl() {
    var host = Robolectric.buildActivity(TopNavigationTest.Host.class).setup().visible();
    try {
        var browser = new com.archos.mediacenter.video.leanback.filebrowsing.UniversalFileBrowser(host.get());
        host.get().setContentView(browser);
        browser.networkWorkspace();
        ReflectionHelpers.callInstanceMethod(browser, "openLandingCard",
            ReflectionHelpers.ClassParameter.from(int.class, 4));
        auditLayout(browser);
        java.util.List<View> targets = browser.getFocusables(View.FOCUS_FORWARD);
        assertTrue(targets.size() > 1);
        View previous = targets.get(targets.size() - 1);
        assertTrue(previous.requestFocus());
        browser.focusOverview();
        assertSame(previous, browser.findFocus());
        browser.close();
    } finally {
        host.pause().stop().destroy();
    }
}

@Test
public void auditH_networkSectionSurvivesRecreation() {
    var host = Robolectric.buildActivity(TopNavigationTest.Host.class).setup().visible();
    try {
        var first = new com.archos.mediacenter.video.leanback.filebrowsing.UniversalFileBrowser(host.get());
        first.setId(9002);
        host.get().setContentView(first);
        first.networkWorkspace();
        ReflectionHelpers.callInstanceMethod(first, "openLandingCard",
            ReflectionHelpers.ClassParameter.from(int.class, 4));
        int section = ReflectionHelpers.getField(first, "section");
        android.util.SparseArray<android.os.Parcelable> state = new android.util.SparseArray<>();
        first.saveHierarchyState(state);
        first.close();
        var next = new com.archos.mediacenter.video.leanback.filebrowsing.UniversalFileBrowser(host.get());
        next.setId(9002);
        host.get().setContentView(next);
        next.networkWorkspace();
        next.restoreHierarchyState(state);
        assertEquals(section, (int)ReflectionHelpers.getField(next, "section"));
        next.close();
    } finally {
        host.pause().stop().destroy();
    }
}
```

### I/J: methods for LocalStorageViewTest

```java
@Test
public void auditI_localActionSurvivesRowFocusRestore() throws Exception {
    Uri folder = seed(drive, "Film.mkv");
    view.open(folder);
    settle();
    View row = view.findViewWithTag(Uri.fromFile(new File(drive, "Film.mkv")).toString());
    assertNotNull(row);
    assertTrue(row.requestFocus());
    settle();
    View action = row.findViewWithTag("local.row.action.play");
    assertTrue(action.requestFocus());
    ReflectionHelpers.callInstanceMethod(view, "rememberFocus");
    ReflectionHelpers.callInstanceMethod(view, "restoreFocus");
    settle();
    assertTrue(action.hasFocus());
}

@Test
public void auditJ_localRowsSurviveViewRecreation() throws Exception {
    Uri folder = seed(drive, "A.mkv", "B.mkv");
    view.open(folder);
    settle();
    String identity = Uri.fromFile(new File(drive, "B.mkv")).toString();
    assertTrue(view.findViewWithTag(identity).requestFocus());
    ReflectionHelpers.callInstanceMethod(view, "rememberFocus");
    view.setId(9003);
    android.util.SparseArray<android.os.Parcelable> state = new android.util.SparseArray<>();
    view.saveHierarchyState(state);
    view.close();
    view = new LocalStorageView(host.get(), PreferenceManager.getDefaultSharedPreferences(host.get()),
        new BrowserSelection(Collections.emptySet(), Collections.emptySet()), () -> changes++);
    view.setId(9003);
    host.get().setContentView(view);
    view.setVolumes(List.of(new Box(Box.ID.USB, "Backup #2", 0, drive.getPath())));
    seed(drive, "A.mkv", "B.mkv");
    view.open(folder);
    view.restoreHierarchyState(state);
    settle();
    assertEquals(identity, view.findFocus().getTag());
}
```

### K: standalone test

```java
package com.archos.mediacenter.video.leanback;

@org.junit.runner.RunWith(org.robolectric.RobolectricTestRunner.class)
@org.robolectric.annotation.Config(application = android.app.Application.class, sdk = 28)
public class LibraryHealthFocusAuditTest {
    @org.junit.Test
    public void auditK_categorySurvivesActivityRecreation() {
        PreviewAsyncFixtures.offlineTransport();
        var first = org.robolectric.Robolectric.buildActivity(LibraryHealthActivity.class)
            .create().start().visible();
        Object workspace = org.robolectric.util.ReflectionHelpers.getField(first.get(), "workspace");
        org.robolectric.util.ReflectionHelpers.callInstanceMethod(workspace, "choose",
            org.robolectric.util.ReflectionHelpers.ClassParameter.from(int.class, 4));
        android.os.Bundle saved = new android.os.Bundle();
        first.saveInstanceState(saved).stop().destroy();
        var next = org.robolectric.Robolectric.buildActivity(LibraryHealthActivity.class)
            .create(saved).start().visible();
        try {
            Object restored = org.robolectric.util.ReflectionHelpers.getField(next.get(), "workspace");
            org.junit.Assert.assertEquals(4,
                (int)org.robolectric.util.ReflectionHelpers.getField(restored, "category"));
        } finally {
            next.stop().destroy();
        }
    }
}
```

### L/M/N: methods for SettingsWorkspaceTest

```java
@Test
public void auditL_currentSettingsRowGetsFocusAfterRecreation() {
    workspace.open(0, "Playback");
    layout();
    String tag = "settings.preference.playback_speed";
    assertTrue(tagged(tag).requestFocus());
    Bundle saved = new Bundle();
    workspace.save(saved);
    BackupSettingsController backup = ReflectionHelpers.getField(workspace, "backup");
    SettingsWorkspace next = new SettingsWorkspace(fragment, backup, saved);
    host.get().setContentView(next);
    root = next;
    layout();
    assertNotNull(next.findFocus());
    assertEquals(tag, next.findFocus().getTag());
}

@Test
public void auditM_allVisitedSectionAnchorsSurviveRecreation() {
    workspace.open(0, "Playback");
    layout();
    assertTrue(tagged("settings.preference.playback_speed").requestFocus());
    workspace.open(0, "Skipping");
    layout();
    Bundle saved = new Bundle();
    workspace.save(saved);
    BackupSettingsController backup = ReflectionHelpers.getField(workspace, "backup");
    SettingsWorkspace next = new SettingsWorkspace(fragment, backup, saved);
    java.util.Map<String, String> anchors = ReflectionHelpers.getField(next, "anchors");
    assertEquals("settings.preference.playback_speed", anchors.get("0:Playback"));
    next.dispose();
}

@Test
public void auditN_aboutSelectionSurvivesLeavingItsSection() {
    workspace.open(7, "Release Notes");
    layout();
    com.archos.mediacenter.video.foundation.FoundationAboutWorkspace first =
        ReflectionHelpers.getField(workspace, "about");
    java.util.Map<String, Integer> selections = ReflectionHelpers.getField(first, "selections");
    selections.put("Release Notes", 3);
    ReflectionHelpers.callInstanceMethod(workspace, "select",
        ReflectionHelpers.ClassParameter.from(String.class, "Technical Information"));
    ReflectionHelpers.callInstanceMethod(workspace, "select",
        ReflectionHelpers.ClassParameter.from(String.class, "Release Notes"));
    com.archos.mediacenter.video.foundation.FoundationAboutWorkspace next =
        ReflectionHelpers.getField(workspace, "about");
    java.util.Map<String, Integer> restored = ReflectionHelpers.getField(next, "selections");
    assertEquals(Integer.valueOf(3), restored.get("Release Notes"));
}
```

### O: method for PreviewPlaybackMenusTest

```java
@Test
public void auditO_playbackTabsRememberTheirContentRow() {
    PlaybackSubtitlePanel.Backend backend = mock(PlaybackSubtitlePanel.Backend.class);
    when(backend.tracks()).thenReturn(Collections.emptyList());
    when(backend.audioTracks()).thenReturn(Collections.singletonList(
        new PlaybackSubtitlePanel.Track("English", "en", true, true, () -> {})));
    when(backend.audioDelay()).thenReturn(0);
    when(backend.choices(anyString())).thenReturn(Collections.emptyList());
    PlaybackSubtitlePanel panel = new PlaybackSubtitlePanel(host, backend, PlaybackSubtitlePanel.TAB_AUDIO);
    host.setContentView(panel);
    panel.measure(View.MeasureSpec.makeMeasureSpec(960, View.MeasureSpec.EXACTLY),
                  View.MeasureSpec.makeMeasureSpec(540, View.MeasureSpec.EXACTLY));
    panel.layout(0, 0, 960, 540);
    View delay = panel.findViewWithTag("audio.row.delay");
    assertNotNull(delay);
    assertTrue(delay.requestFocus());
    panel.showTab(PlaybackSubtitlePanel.TAB_PLAYBACK);
    panel.showTab(PlaybackSubtitlePanel.TAB_AUDIO);
    assertNotNull(panel.findFocus());
    assertEquals("audio.row.delay", panel.findFocus().getTag());
    panel.close();
}
```

### P: method for EditPagePanelTest

```java
@Test
public void auditP_editPageReopensAtLastRow() {
    var activity = Robolectric.buildActivity(TopNavigationTest.Host.class).setup().visible();
    try {
        EditPagePanel.Host model = org.mockito.Mockito.mock(EditPagePanel.Host.class);
        CustomLibraryPage draft = new CustomLibraryPage();
        org.mockito.Mockito.when(model.draft()).thenReturn(draft);
        org.mockito.Mockito.when(model.snapshot()).thenReturn(new PreviewLibraryLoader.Snapshot());
        EditPagePanel first = EditPagePanel.show(activity.get(), model);
        Shadows.shadowOf(Looper.getMainLooper()).idle();
        View row = first.findViewWithTag("edit.row.language");
        assertNotNull(row);
        assertTrue(row.requestFocus());
        first.dismiss();
        EditPagePanel next = EditPagePanel.show(activity.get(), model);
        Shadows.shadowOf(Looper.getMainLooper()).idle();
        assertNotNull(next.findFocus());
        assertEquals("edit.row.language", next.findFocus().getTag());
        next.dismiss();
    } finally {
        activity.pause().stop().destroy();
    }
}
```

### Q: standalone test

```java
package com.archos.mediacenter.video.leanback;

@org.junit.runner.RunWith(org.robolectric.RobolectricTestRunner.class)
@org.robolectric.annotation.Config(application = android.app.Application.class, sdk = 28)
public class CustomiseHomeFocusAuditTest {
    @org.junit.Test
    public void auditQ_customiseHomeReopensAtLastRow() {
        PreviewAsyncFixtures.offlineTransport();
        var host = org.robolectric.Robolectric.buildActivity(TopNavigationTest.Host.class).setup().visible();
        try {
            CustomiseHomeEditor first = new CustomiseHomeEditor(host.get(), new PreviewHomeRows(host.get()), () -> {});
            first.show();
            android.app.Dialog dialog = org.robolectric.util.ReflectionHelpers.getField(first, "dialog");
            java.util.List<android.view.View> rows =
                org.robolectric.util.ReflectionHelpers.getField(first, "bodies");
            org.junit.Assert.assertTrue(rows.size() > 1);
            org.junit.Assert.assertTrue(rows.get(1).requestFocus());
            Object identity = rows.get(1).getTag();
            dialog.dismiss();
            CustomiseHomeEditor next = new CustomiseHomeEditor(host.get(), new PreviewHomeRows(host.get()), () -> {});
            next.show();
            android.app.Dialog restored = org.robolectric.util.ReflectionHelpers.getField(next, "dialog");
            org.junit.Assert.assertNotNull(restored.getCurrentFocus());
            org.junit.Assert.assertEquals(identity, restored.getCurrentFocus().getTag());
            restored.dismiss();
        } finally {
            host.pause().stop().destroy();
        }
    }
}
```

For D, extend the same assertion to Seasons & Episodes and More Like This with the existing episode/related fixtures;
for G, run the same content-entry assertion for Network Shares and Add Connection. These are instances of the same
reset, not evidence of additional independent defects. Keep the source item's stable ID when lists reorder. Do not
persist filenames, credentials or provider tokens in diagnostic focus tags.
