package com.archos.mediacenter.video.leanback.settings;

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

    private static void page(int category, Object... sectionsAndKeys) {
        LinkedHashMap<String, List<String>> sections = new LinkedHashMap<>();
        for (int i = 0; i < sectionsAndKeys.length; i += 2)
            sections.put((String)sectionsAndKeys[i], Arrays.asList((String[])sectionsAndKeys[i + 1]));
        PAGES.put(category, sections);
    }

    private static void copy(String key, String title, String description) {
        COPY.put(key, new String[] {title, description});
    }

    static {
        page(0,
             "Playback", new String[] {"playback_speed", "hide_controls_on_pause", "allow_3rd_party_player",
                                       "reset_brightness_on_start", "player_projector_mode_key"},
             "Resume & Progress", new String[] {"display_resume_box", "supernova_save_resume", "network_bookmarks"},
             "Skipping", new String[] {"supernova_segment_intro", "supernova_segment_recap",
                                       "supernova_segment_preview", "supernova_segment_outro",
                                       "supernova_segment_credits"},
             "Video & Display", new String[] {"enable_tv_refreshrate_switch_mode", "dolby_vision_mode",
                                              "activate_tv_switch"});
        page(1,
             "Audio output & tracks", new String[] {"favAudioLang", "prefer_original_audio_track",
                                                    "force_audio_passthrough_multiple",
                                                    "player_spatialization_enabled"},
             "Subtitles", new String[] {"preview_subtitles_by_default", "favSubLang", "languages_list",
                                        "supernova_subtitle_season_prefetch", "supernova_subtitle_discovery",
                                        "subtitles_hide_default", "subtitles_show_full_for_local_audio",
                                        "subtitle_use_margins", "subtitle_fonts_folder_uri",
                                        "subtitle_default_font"});
        page(2,
             "Library", new String[] {"hide_watched", "sort_ignore_articles", "recreate_sort_titles_prefkey"},
             "Library & metadata", new String[] {"enable_auto_scrap_key", "favScraperLang", "network_nfo_parse",
                                                 "nfo_export_auto", "pref_create_remote_thumbs",
                                                 "scrape_from_database_key", "omdb_api_key"},
             "Home", new String[] {"preview_home_editor", "preview_featured_recent", "preview_featured_trending",
                                   "preview_featured_popular"},
             "Watch history", new String[] {"supernova_track_viewing_history", "supernova_clear_history"});
        page(3,
             "Streaming services", new String[] {"streaming_enabled", "streaming_country",
                                                 "streaming_providers_{country}", "streaming_preferred_{country}",
                                                 "streaming_show_all", "(JustWatch)"});
        page(4,
             "Interface & theme", new String[] {"ui_lang"});
        page(5,
             "Service integrations", new String[] {"supernova_integration_trakt",
                                                   "supernova_integration_opensubtitles",
                                                   "supernova_segment_enabled"},
             "Trakt", new String[] {"trakt_signin", "trakt_live_scrobbling", "trakt_sync_resume", "trakt_getfull",
                                    "trakt_force_push", "trakt_force_pull", "trakt_wipe"},
             "OpenSubtitles", new String[] {"subtitles_credentials"});
        page(6,
             "Backup & restore", new String[] {},
             "Diagnostics", new String[] {"export_diagnostic_report", "supernova_diagnostic_logging",
                                          "supernova_diagnostic_level"},
             "Advanced", new String[] {"seek_thumbnails_enabled", "force_software_decoding", "dec_choice", "parser_sync_mode",
                                       "stream_max_iframe_size", "audio_interface_choice", "audio_decoder_choice",
                                       "force_passthrough", "audio_speed_audiotrack", "enable_dynamic_audio_delay",
                                       "disable_downmix", "enable_downmix_androidtv", "supernova_pcm_speed_engine",
                                       "stream_buffer_size", "pref_smbj", "pref_smbv2", "pref_smb_resolv",
                                       "pref_smb_disable_tcp_discovery", "pref_smb_disable_udp_discovery",
                                       "pref_smb_disable_mdns_discovery", "pref_sshj", "codepage",
                                       "preview_diagnostic_Issues Digest"});

        TAB_LABELS.put("Playback", "General");
        TAB_LABELS.put("Resume & Progress", "Resume");
        TAB_LABELS.put("Video & Display", "Picture");
        TAB_LABELS.put("Audio output & tracks", "Audio");
        TAB_LABELS.put("Library & metadata", "Details");
        TAB_LABELS.put("Watch history", "History");
        TAB_LABELS.put("Streaming services", "Where to Watch");
        TAB_LABELS.put("Interface & theme", "Appearance");
        TAB_LABELS.put("Service integrations", "Services");
        TAB_LABELS.put("Backup & restore", "Backup");

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
        copy("seek_thumbnails_enabled", "Seek previews (experimental)",
             "Shows a small picture above the seek bar while you skip through a video.");
        copy("supernova_diagnostic_logging", "Diagnostic logging", "Records everything the app does in full detail.");
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

    /** {title, explanation} for a row, or null to use the setting's own title and summary. */
    static String[] copy(String keyTemplate) {
        return COPY.get(keyTemplate);
    }
}
