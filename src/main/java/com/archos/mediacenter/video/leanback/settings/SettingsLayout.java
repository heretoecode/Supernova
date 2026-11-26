package com.archos.mediacenter.video.leanback.settings;

import com.archos.mediacenter.video.diagnostics.Diagnostics;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * The 0.156 Settings layout (owner-approved Settings review, mock-ups docs/design/proposals/settings/01–32): which
 * tabs each page has, which settings each tab shows and in what order, and the plain-English title and one-line
 * explanation each row shows.
 *
 * Section ids stay the internal names used elsewhere (state, intents, special pages); {@link #label} gives the tab
 * text. Settings not listed are not shown (removed by the review, moved elsewhere, or waiting for their new page);
 * their saved values are untouched and backups still carry them.
 */
final class SettingsLayout {
    private SettingsLayout() {
    }

    /** Page titles on the landing tiles and in the page header (two renamed by the review). */
    static final String[] LABELS = {"Playback", "Audio & Subtitles", "Library & Home", "Where to Watch",
                                    "Appearance", "Accounts & Services", "System", "About Supernova"};

    static final String[] DESCRIPTIONS = {"How videos play, resume and skip, and how the picture is shown.",
                                          "Languages, surround sound and subtitles.",
                                          "Your library, details, Home rows and viewing history.",
                                          "Shows which of your services carry a title.",
                                          "Language of the menus and the screensaver.",
                                          "Trakt, OpenSubtitles and IntroDB.",
                                          "Backup, diagnostics and advanced options.",
                                          "Version, credits, what's new and licences."};

    private static final Map<Integer, LinkedHashMap<String, List<String>>> PAGES = new LinkedHashMap<>();
    private static final Map<String, String> TAB_LABELS = new LinkedHashMap<>();
    private static final Map<String, String[]> COPY = new LinkedHashMap<>();
    /** Action rows show their verb as plain text (page sheet, RULES §8), not ›. */
    private static final Map<String, String> VERBS = new LinkedHashMap<>();
    /** Declared before the static block below fills it (static fields initialise in order). */
    private static final Map<String, String[]> CHOICES = new LinkedHashMap<>();

    private static void page(int category, Object... sectionsAndKeys) {
        LinkedHashMap<String, List<String>> sections = new LinkedHashMap<>();
        for (int i = 0; i < sectionsAndKeys.length; i += 2)
            sections.put((String)sectionsAndKeys[i], Arrays.asList((String[])sectionsAndKeys[i + 1]));
        PAGES.put(category, sections);
    }

    private static void copy(String key, String title, String description) {
        COPY.put(key, new String[] {title, description});
    }

    /**
     * The seven tabs of the 0.158 Settings page (page sheet docs/design/pages/SETTINGS.md; owner, 14 and 19 Nov
     * 2026): Playback · Audio · Subtitles · Library · Services · System · About. Each tab shows its sections one under
     * the other with a heading each. Category 6 is no longer a tab; About keeps category 7.
     */
    static final int[] TABS = {0, 1, 2, 3, 4, 5, 7};
    static final String[] TAB_NAMES = {"Playback", "Audio", "Subtitles", "Library", "Services", "System", "About"};
    static final String[] TAB_ICONS = {"play", "audio", "sub", "library", "streaming", "settings.gear", "info"};

    static {
        page(0,
             "General", new String[] {"hide_controls_on_pause", "seek_preview_mode", "allow_3rd_party_player"},
             // "display_resume_box" is NOVA's resume button on its old main screen, which Supernova's Home doesn't
             // have, so it is not shown; "When a video was started" is Supernova's own Ask · Resume · Start over.
             "Resume", new String[] {"supernova_resume_choice", "supernova_save_resume"},
             "Skipping", new String[] {"supernova_segment_enabled", "supernova_segment_intro",
                                       "supernova_segment_recap", "supernova_segment_preview",
                                       "supernova_segment_outro", "supernova_segment_credits"},
             "Picture", new String[] {"enable_tv_refreshrate_switch_mode", "dolby_vision_mode"});
        page(1,
             "Language", new String[] {"favAudioLang", "prefer_original_audio_track"},
             "Sound", new String[] {"force_audio_passthrough_multiple", "player_spatialization_enabled",
                                    "audio_decoder_choice"});
        page(2,
             "When To Show", new String[] {"preview_subtitles_by_default", "subtitles_show_full_for_local_audio"},
             "Languages", new String[] {"favSubLang", "languages_list"},
             "Downloading", new String[] {"supernova_subtitle_discovery", "supernova_subtitle_season_prefetch"},
             "OpenSubtitles", new String[] {},
             "Default Appearance", new String[] {"subtitle_default_font", "subtitle_fonts_folder_uri",
                                                 "subtitle_use_margins", "codepage"});
        page(3,
             "Your Library", new String[] {"sort_ignore_articles", "pref_create_remote_thumbs"},
             "Details", new String[] {"enable_auto_scrap_key", "favScraperLang", "network_nfo_parse",
                                      "nfo_export_auto", "export_manual_prefkey"},
             "Home Hero", new String[] {"preview_featured_recent", "preview_featured_trending",
                                        "preview_featured_popular", "preview_featured_because",
                                        "preview_featured_interval"},
             "Viewing History", new String[] {"supernova_track_viewing_history", "supernova_clear_history",
                                              "supernova_keep_search_history", "supernova_clear_search_history"});
        page(4,
             "Where To Watch", new String[] {"streaming_enabled", "streaming_country",
                                             "streaming_providers_{country}"},
             "Trakt", new String[] {"trakt_signin", "trakt_live_scrobbling", "trakt_sync_resume", "trakt_getfull",
                                    "trakt_force_push", "trakt_force_pull", "trakt_wipe"},
             "Ratings", new String[] {"omdb_api_key", "(JustWatch)"});
        page(5,
             "General", new String[] {"ui_lang"},
             "Backup & restore", new String[] {},
             "Diagnostics", new String[] {"supernova_diagnostic_logging", Diagnostics.KEEP_WEEKS,
                                          "export_diagnostic_report", "supernova_diagnostic_clear"},
             "Advanced", new String[] {"player_projector_mode_key", "force_software_decoding", "dec_choice",
                                       "parser_sync_mode", "stream_buffer_size", "stream_max_iframe_size",
                                       "audio_interface_choice", "supernova_pcm_speed_engine",
                                       "enable_downmix_androidtv", "enable_dynamic_audio_delay"});

        TAB_LABELS.put("Backup & restore", "Backup");
        TAB_LABELS.put("Resume & Progress", "Resume");
        TAB_LABELS.put("Video & Display", "Picture");
        TAB_LABELS.put("Audio output & tracks", "Audio");
        TAB_LABELS.put("Library & metadata", "Details");
        TAB_LABELS.put("Watch history", "History");
        TAB_LABELS.put("Streaming services", "Where to Watch");
        TAB_LABELS.put("Interface & theme", "Appearance");
        TAB_LABELS.put("Service integrations", "Services");
        TAB_LABELS.put("Backup & restore", "Backup");
        TAB_LABELS.put("Subtitle style", "Style");

        copy("hide_controls_on_pause", "Hide controls when paused",
             "Shows the paused picture without the controls on top.");
        copy("allow_3rd_party_player", "Open videos in another player",
             "Offers your other video apps when you press Play.");
        copy("supernova_segment_intro", "Intro", "Opening titles and theme tune.");
        copy("supernova_segment_recap", "Recap", "“Previously on…”");
        copy("supernova_segment_preview", "Preview", "Scenes from the next episode.");
        copy("supernova_segment_outro", "Outro", "Closing scene after the story ends.");
        copy("supernova_segment_credits", "Credits", "End credits. Safe skip never cuts a scene after them.");
        copy("favAudioLang", "Preferred audio language", "Chosen first when a video has more than one.");
        copy("prefer_original_audio_track", "Prefer the original language",
             "Plays a film in its original language when that track exists, even if it isn’t your preferred one.");
        copy("force_audio_passthrough_multiple", "Surround sound",
             "How 5.1 and 7.1 sound is sent to your TV or receiver.");
        copy("player_spatialization_enabled", "Virtual surround",
             "Spreads stereo sound for headphones and TV speakers.");
        copy("preview_subtitles_by_default", "Show subtitles", "When a video starts.");
        copy("favSubLang", "Preferred subtitle language", "Chosen first when there is more than one.");
        copy("languages_list", "Languages to search online", "Used by Download Subtitles.");
        copy("supernova_subtitle_season_prefetch", "Prepare subtitles for the next episodes",
             "Looks them up ahead of time while you watch a series.");
        copy("supernova_subtitle_discovery", "Find subtitles when none are included",
             "Searches OpenSubtitles automatically.");
        copy("subtitle_fonts_folder_uri", "Custom fonts folder",
             "Used by styled (ASS/SSA) subtitles that ask for fonts that aren’t built in.");
        copy("hide_watched", "Hide watched titles", "In Movies, TV Shows and your own library pages.");
        copy("sort_ignore_articles", "Ignore “The” and “A” when sorting", "“The Batman” sorts under B.");
        copy("enable_auto_scrap_key", "Find details automatically",
             "Looks up posters, synopsis and cast for new files.");
        copy("favScraperLang", "Details language", "Language used for titles and synopses where available.");
        copy("network_nfo_parse", "Use .nfo files", "Reads details saved next to your files on network shares.");
        copy("nfo_export_auto", "Save details as .nfo files",
             "Writes them next to your files so other apps can use them.");
        copy("pref_create_remote_thumbs", "Make thumbnails for network videos",
             "Takes a frame from videos with no artwork. Uses some network time.");
        copy("preview_home_editor", "Customise Home…", "Add, hide, move and edit rows; manage Watch Next.");
        copy("supernova_track_viewing_history", "Keep viewing history",
             "Used for Continue Watching, Recently Watched and suggestions.");
        copy("supernova_clear_history", "Clear viewing history",
             "Removes what you watched and when. Your library and files are kept.");
        copy("supernova_keep_search_history", "Keep search history",
             "Shows your recent searches in Search. Off: nothing is kept and the list is cleared.");
        copy("supernova_clear_search_history", "Clear search history",
             "Removes your recent searches. Your library is kept.");
        copy("streaming_enabled", "Show where to watch",
             "On a title’s Details page, shows which of your services carry it.");
        copy("streaming_country", "Country", "Services and availability differ by country.");
        copy("streaming_providers_{country}", "Your services", "The ones you subscribe to.");
        copy("streaming_preferred_{country}", "Show first",
             "Listed first when a title is on several of your services.");
        copy("ui_lang", "Interface language", "Language of Supernova’s menus and labels.");
        copy("trakt_live_scrobbling", "Scrobble while watching", "Tells Trakt what you’re watching as you watch it.");
        copy("trakt_sync_resume", "Sync resume points", "Carries on where you stopped on another device.");
        copy("trakt_getfull", "Sync your library to Trakt", "Adds the titles you own to your Trakt collection.");
        copy("trakt_wipe", "Remove Supernova’s data from Trakt",
             "Deletes watched history Supernova sent. Your library is not affected.");
        copy("export_diagnostic_report", "Save a diagnostic report",
             "A ZIP of everything Supernova has recorded, for finding and fixing problems. Never includes passwords.");
        copy("seek_preview_mode", "Seek preview",
             "Off, the chapter's picture, or the nearest frame, above the seek bar while you skip.");
        copy("supernova_diagnostic_logging", "Diagnostic logging", "Records everything the app does in full detail.");
        // 0.158 page sheet wording (SETTINGS.md).
        copy("supernova_segment_enabled", "Look up skip times online",
             "TheIntroDB and IntroDB, when the file doesn’t mark its parts.");
        copy("display_resume_box", "When a video was started", "Ask, resume, or start over.");
        copy("supernova_save_resume", "Remember where you stopped", "On this device and network files.");
        copy("supernova_subtitle_season_prefetch", "Have subtitles ready",
             "Fetched while a video prepares, and for the next two episodes.");
        copy("sort_ignore_articles", "Ignore “The” and “A” when sorting", "Sort titles rebuild by themselves.");
        copy("pref_create_remote_thumbs", "Make thumbnails for network videos",
             "Pictures for network files that have no artwork.");
        copy("supernova_pcm_speed_engine", "Sound when speed changes", "FFmpeg or Sonic.");
        copy("enable_downmix_androidtv", "Stereo downmix", "Turns surround sound into two channels.");
        copy("player_projector_mode_key", "Projector mode", "Flips the picture for rear projection.");
        // 0.158.5: every row's wording checked against the page sheet (SETTINGS.md, owner 19 Nov 2026).
        copy("supernova_resume_choice", "When a video was started", "Ask, resume, or start over.");
        copy("supernova_subtitle_season_prefetch", "Have subtitles ready",
             "Fetched while a video prepares, and for the next two episodes.");
        copy("enable_tv_refreshrate_switch_mode", "Automatic refresh rate",
             "Switches the TV to the video’s frame rate, so motion stays smooth.");
        copy("dolby_vision_mode", "Dolby Vision", "How Dolby Vision videos are sent to your TV.");
        copy("audio_decoder_choice", "Audio decoder", "Which decoder turns the sound into audio.");
        copy("subtitles_show_full_for_local_audio", "Full subtitles when the audio is in your language",
             "Shows every line, not only signs and songs.");
        copy("subtitle_default_font", "Font", "Used for plain subtitles.");
        copy("subtitle_use_margins", "Use the black bars", "Places subtitles in the bars above and below the picture.");
        copy("codepage", "Text encoding", "For older subtitle files whose letters show wrongly.");
        copy("nfo_export_auto", "Write .nfo files for new titles",
             "Saves details next to new files so other apps can use them.");
        copy("export_manual_prefkey", "Write .nfo files for the whole library",
             "Saves details next to every file in your library.");
        copy("preview_featured_recent", "Recently added", "In the Home hero.");
        copy("preview_featured_trending", "Trending on Trakt", "In the Home hero.");
        copy("preview_featured_popular", "Popular on Trakt", "In the Home hero.");
        copy("preview_featured_because", "Because you watched", "In the Home hero.");
        copy("preview_featured_interval", "Change every", "Titles you’ve started leave the hero.");
        copy("trakt_signin", "Trakt", "Sign in to keep your watching in step.");
        copy("trakt_force_push", "Send everything to Trakt again", "Sends your whole watched history once more.");
        copy("trakt_force_pull", "Fetch everything from Trakt again", "Brings your Trakt history in once more.");
        copy("omdb_api_key", "OMDb ratings key", "Shows IMDb, Rotten Tomatoes and Metacritic ratings.");
        copy(Diagnostics.KEEP_WEEKS, "Keep diagnostics for", "Older records are removed by themselves.");
        copy("supernova_diagnostic_clear", "Clear diagnostics", "Removes everything recorded so far.");
        copy("force_software_decoding", "Force software decoding", "For videos the hardware can’t play.");
        copy("dec_choice", "Video decoder", "Which decoder turns the file into pictures.");
        copy("parser_sync_mode", "Parser synchronisation", "How sound and picture are kept in step.");
        copy("stream_buffer_size", "Stream buffer", "How much of a network video is loaded ahead.");
        copy("stream_max_iframe_size", "Max I-frame size", "For network videos with very large frames.");
        copy("audio_interface_choice", "Audio interface", "How sound reaches the device.");
        copy("enable_dynamic_audio_delay", "Automatic audio delay", "Keeps sound in step with the picture.");

        VERBS.put("export_diagnostic_report", "Save");
        VERBS.put("supernova_diagnostic_clear", "Clear");
        VERBS.put("supernova_clear_history", "Clear");
        VERBS.put("supernova_clear_search_history", "Clear");
        VERBS.put("trakt_force_push", "Send");
        VERBS.put("trakt_force_pull", "Fetch");
        VERBS.put("trakt_wipe", "Remove");
        VERBS.put("export_manual_prefkey", "Write");

        // Choice names from the page sheet, value → name, in the sheet's order (values kept, names only).
        choices("supernova_segment_{type}", "NORMAL", "Off", "PROMPT", "Show button", "AUTO", "Skip", "SMART",
                "Skip when binge-watching");
        choices("enable_tv_refreshrate_switch_mode", "0", "Off", "1", "Match the video", "2", "Android matching");
        choices("dolby_vision_mode", "auto", "Automatic", "force", "Always", "off", "Off");
        choices("audio_decoder_choice", "0", "Automatic", "2", "Hardware", "1", "Software");
        choices("dec_choice", "0", "Automatic", "4", "Hardware", "1", "Software", "-1", "Android");
        choices("parser_sync_mode", "0", "Timestamps", "1", "Samples");
        choices("audio_interface_choice", "0", "Automatic", "1", "AudioTrack", "2", "OpenSL");
        choices("supernova_pcm_speed_engine", "2", "FFmpeg", "3", "Sonic");
        choices("force_audio_passthrough_multiple", "0", "Off", "1", "Passthrough", "2", "Via Android", "3",
                "Convert to AC3");
    }

    private static void choices(String key, String... valuesAndNames) {
        CHOICES.put(key, valuesAndNames);
    }

    /**
     * The sheet's names for a list's choices, as {values, names} in the sheet's order, or null. The segment keys
     * (supernova_segment_intro …) share one set.
     */
    static String[][] choices(String key) {
        String[] pairs = CHOICES.get(key != null && key.startsWith("supernova_segment_")
                                     && !key.equals("supernova_segment_enabled") ? "supernova_segment_{type}" : key);
        if (pairs == null)
            return null;
        String[] values = new String[pairs.length / 2];
        String[] names = new String[pairs.length / 2];
        for (int i = 0; i < values.length; i++) {
            values[i] = pairs[i * 2];
            names[i] = pairs[i * 2 + 1];
        }
        return new String[][] {values, names};
    }

    static boolean has(int category) {
        return PAGES.containsKey(category);
    }

    static List<String> sections(int category) {
        LinkedHashMap<String, List<String>> page = PAGES.get(category);
        return page == null ? Collections.emptyList() : new java.util.ArrayList<>(page.keySet());
    }

    /** The keys (templates, e.g. {@code streaming_providers_{country}}) shown in a tab, in order. */
    static List<String> keys(int category, String section) {
        LinkedHashMap<String, List<String>> page = PAGES.get(category);
        List<String> keys = page == null ? null : page.get(section);
        return keys == null ? Collections.emptyList() : keys;
    }

    static String label(String section) {
        String label = TAB_LABELS.get(section);
        return label == null ? section : label;
    }

    /** The verb an action row shows (Save, Clear, Send …), or null. */
    static String verb(String key) {
        return VERBS.get(key);
    }

    /** {title, explanation} for a row, or null to use the setting's own title and summary. */
    static String[] copy(String keyTemplate) {
        return COPY.get(keyTemplate);
    }
}
