# Supernova Settings registry — Preview 4.1.4

Source: res/xml/preferences_video.xml; organiser: leanback/settings/PreviewSettings.java. Original listeners and platform capability predicates remain authoritative. Runtime-created commercial-provider settings remain in Streaming. No provider capability is inferred.

| Source key | Original source category | Supernova location | Status / reason | Dependencies / visibility |
| --- | --- | --- | --- | --- |
| `preferences_version` | preferences_about | About | Retained backend preference; grouped by responsibility | Preference; existing runtime/device gating |
| `ui_lang` | preferences_about | About | Retained backend preference; grouped by responsibility | ListPreference; existing runtime/device gating |
| `force_software_decoding` | preferences_category_video | Advanced / Video Compatibility | Retained backend preference; grouped by responsibility | CheckBoxPreference; existing runtime/device gating |
| `dec_choice` | preferences_category_video | Advanced / Video Compatibility | Retained backend preference; grouped by responsibility | ListPreference; existing runtime/device gating |
| `audio_interface_choice` | preferences_category_video | Advanced / Audio Compatibility | Retained backend preference; grouped by responsibility | ListPreference; existing runtime/device gating |
| `audio_decoder_choice` | preferences_category_video | Advanced / Audio Compatibility | Retained backend preference; grouped by responsibility | ListPreference; existing runtime/device gating |
| `enable_cutout_mode_short_edges` | preferences_category_video | Advanced / Video Compatibility | Retained backend preference; grouped by responsibility | CheckBoxPreference; existing runtime/device gating |
| `enable_cutout_both_sidesx` | preferences_category_video | Advanced / Video Compatibility | Retained backend preference; grouped by responsibility | CheckBoxPreference; existing runtime/device gating |
| `force_audio_passthrough_multiple` | preferences_category_video | Audio | Retained backend preference; grouped by responsibility | ListPreference; existing runtime/device gating |
| `force_passthrough` | preferences_category_video | Advanced / Audio Compatibility | Retained backend preference; grouped by responsibility | CheckBoxPreference; existing runtime/device gating |
| `playback_speed` | preferences_category_video | Playback | Retained backend preference; grouped by responsibility | CheckBoxPreference; existing runtime/device gating |
| `audio_speed_audiotrack` | preferences_category_video | Advanced / Audio Compatibility | Retained backend preference; grouped by responsibility | CheckBoxPreference; existing runtime/device gating |
| `enable_dynamic_audio_delay` | preferences_category_video | Advanced / Audio Compatibility | Retained backend preference; grouped by responsibility | CheckBoxPreference; existing runtime/device gating |
| `player_spatialization_enabled` | preferences_category_video | Audio | Retained backend preference; grouped by responsibility | CheckBoxPreference; existing runtime/device gating |
| `disable_downmix` | preferences_category_video | Advanced / Audio Compatibility | Retained backend preference; grouped by responsibility | CheckBoxPreference; existing runtime/device gating |
| `parser_sync_mode` | preferences_category_video | Advanced / Video Compatibility | Retained backend preference; grouped by responsibility | ListPreference; existing runtime/device gating |
| `enable_downmix_androidtv` | preferences_category_video | Advanced / Audio Compatibility | Retained backend preference; grouped by responsibility | CheckBoxPreference; existing runtime/device gating |
| `enable_tv_refreshrate_switch_mode` | preferences_category_video | Video | Retained backend preference; grouped by responsibility | ListPreference; existing runtime/device gating |
| `dolby_vision_mode` | preferences_category_video | Video | Retained backend preference; grouped by responsibility | ListPreference; existing runtime/device gating |
| `prefer_original_audio_track` | preferences_category_video | Audio | Retained backend preference; grouped by responsibility | CheckBoxPreference; existing runtime/device gating |
| `favAudioLang` | preferences_category_video | Audio | Retained backend preference; grouped by responsibility | ListPreference; existing runtime/device gating |
| `activate_tv_switch` | preferences_category_video | Video | Retained backend preference; grouped by responsibility | CheckBoxPreference; existing runtime/device gating |
| `stream_buffer_size` | preferences_category_video | Advanced / Network Compatibility | Retained backend preference; grouped by responsibility | EditTextPreference; existing runtime/device gating |
| `stream_max_iframe_size` | preferences_category_video | Advanced / Video Compatibility | Retained backend preference; grouped by responsibility | EditTextPreference; existing runtime/device gating |
| `allow_3rd_party_player` | preferences_category_video | Playback | Retained backend preference; grouped by responsibility | CheckBoxPreference; existing runtime/device gating |
| `smart_recently_rows` | category_user_interface | Hidden / saved value retained | Legacy/removal review; no underlying function deleted | CheckBoxPreference; existing runtime/device gating |
| `player_projector_mode_key` | category_user_interface | Playback | Retained backend preference; grouped by responsibility | CheckBoxPreference; existing runtime/device gating |
| `hide_watched` | category_user_interface | Library & Metadata | Retained backend preference; grouped by responsibility | CheckBoxPreference; existing runtime/device gating |
| `sort_ignore_articles` | category_user_interface | Library & Metadata | Retained backend preference; grouped by responsibility | CheckBoxPreference; existing runtime/device gating |
| `uimode` | category_user_interface | Hidden / saved value retained | Legacy/removal review; no underlying function deleted | ListPreference; existing runtime/device gating |
| `uimode_leanback` | category_user_interface | Hidden / saved value retained | Legacy/removal review; no underlying function deleted | ListPreference; existing runtime/device gating |
| `ui_zoom` | category_user_interface | Appearance | Retained backend preference; grouped by responsibility | Preference; existing runtime/device gating |
| `display_resume_box` | category_user_interface | Playback | Retained backend preference; grouped by responsibility | CheckBoxPreference; existing runtime/device gating |
| `@string/reset_brightness_on_start_key` | category_user_interface | Playback | Retained backend preference; grouped by responsibility | CheckBoxPreference; existing runtime/device gating |
| `hide_controls_on_pause` | category_user_interface | Playback | Retained backend preference; grouped by responsibility | CheckBoxPreference; existing runtime/device gating |
| `app_theme` | category_user_interface | Hidden / saved value retained | Legacy/removal review; no underlying function deleted | ListPreference; existing runtime/device gating |
| `always_leanback_on_tv_key` | category_leanback_user_interface | Hidden / saved value retained | Legacy/removal review; no underlying function deleted | CheckBoxPreference; existing runtime/device gating |
| `separate_anime_movie_show` | category_leanback_user_interface | Hidden / saved value retained | Legacy/removal review; no underlying function deleted | CheckBoxPreference; existing runtime/device gating |
| `show_last_added_row` | category_leanback_user_interface | Hidden / saved value retained | Legacy/removal review; no underlying function deleted | CheckBoxPreference; existing runtime/device gating |
| `show_last_played_row` | category_leanback_user_interface | Hidden / saved value retained | Legacy/removal review; no underlying function deleted | CheckBoxPreference; existing runtime/device gating |
| `show_watching_up_next_row` | category_leanback_user_interface | Hidden / saved value retained | Legacy/removal review; no underlying function deleted | CheckBoxPreference; existing runtime/device gating |
| `reset_last_played_row` | category_leanback_user_interface | Hidden / saved value retained | Legacy/removal review; no underlying function deleted | Preference; existing runtime/device gating |
| `show_all_movies_row` | category_leanback_user_interface | Hidden / saved value retained | Legacy/removal review; no underlying function deleted | CheckBoxPreference; existing runtime/device gating |
| `preferences_movie_sort_order` | category_leanback_user_interface | Hidden / saved value retained | Legacy/removal review; no underlying function deleted | ListPreference; existing runtime/device gating |
| `try_new_ui` | category_leanback_user_interface | Hidden / saved value retained | Legacy/removal review; no underlying function deleted | SwitchPreferenceCompat; existing runtime/device gating |
| `show_documentaries` | category_leanback_user_interface | Hidden / saved value retained | Legacy/removal review; no underlying function deleted | CheckBoxPreference; existing runtime/device gating |
| `show_all_tv_shows_row` | category_leanback_user_interface | Hidden / saved value retained | Legacy/removal review; no underlying function deleted | CheckBoxPreference; existing runtime/device gating |
| `preferences_tv_show_sort_order` | category_leanback_user_interface | Hidden / saved value retained | Legacy/removal review; no underlying function deleted | ListPreference; existing runtime/device gating |
| `show_all_animes_row` | category_leanback_user_interface | Hidden / saved value retained | Legacy/removal review; no underlying function deleted | CheckBoxPreference; existing runtime/device gating |
| `preferences_animes_sort_order` | category_leanback_user_interface | Hidden / saved value retained | Legacy/removal review; no underlying function deleted | ListPreference; existing runtime/device gating |
| `subtitles_credentials` | Unnamed XML category | Integrations / OpenSubtitles | Retained backend preference; grouped by responsibility | Preference; existing runtime/device gating |
| `subtitles_hide_default` | Unnamed XML category | Hidden / saved value retained | Hidden inverse storage; positive Subtitles by Default proxy | CheckBoxPreference; existing runtime/device gating |
| `favSubLang` | Unnamed XML category | Subtitles | Retained backend preference; grouped by responsibility | ListPreference; existing runtime/device gating |
| `languages_list` | Unnamed XML category | Subtitles | Retained backend preference; grouped by responsibility | MultiSelectListPreference; existing runtime/device gating |
| `codepage` | Unnamed XML category | Advanced / Subtitle Compatibility | Retained backend preference; grouped by responsibility | ListPreference; existing runtime/device gating |
| `trakt_getfull` | trakt_category | Integrations / Trakt | Retained backend preference; grouped by responsibility | Preference; existing runtime/device gating |
| `trakt_signin` | trakt_category | Integrations / Trakt | Retained backend preference; grouped by responsibility | com.archos.mediacenter.video.utils.TraktSigninDialogPreference; existing runtime/device gating |
| `trakt_live_scrobbling` | trakt_category | Integrations / Trakt | Retained backend preference; grouped by responsibility | CheckBoxPreference; existing runtime/device gating |
| `trakt_sync_resume` | trakt_category | Integrations / Trakt | Retained backend preference; grouped by responsibility | CheckBoxPreference; existing runtime/device gating |
| `trakt_force_push` | trakt_category | Integrations / Trakt | Retained backend preference; grouped by responsibility | Preference; existing runtime/device gating |
| `trakt_force_pull` | trakt_category | Integrations / Trakt | Retained backend preference; grouped by responsibility | Preference; existing runtime/device gating |
| `trakt_wipe` | trakt_category | Integrations / Trakt | Retained backend preference; grouped by responsibility | com.archos.mediacenter.video.utils.TraktWipeDialogPreference; existing runtime/device gating |
| `pref_smbj` | netshare_category | Advanced / Network Compatibility | Retained backend preference; grouped by responsibility | CheckBoxPreference; existing runtime/device gating |
| `pref_smbv2` | netshare_category | Advanced / Network Compatibility | Retained backend preference; grouped by responsibility | CheckBoxPreference; existing runtime/device gating |
| `pref_smb_resolv` | netshare_category | Advanced / Network Compatibility | Retained backend preference; grouped by responsibility | CheckBoxPreference; existing runtime/device gating |
| `pref_smb_disable_tcp_discovery` | netshare_category | Advanced / Network Compatibility | Retained backend preference; grouped by responsibility | CheckBoxPreference; existing runtime/device gating |
| `pref_smb_disable_udp_discovery` | netshare_category | Advanced / Network Compatibility | Retained backend preference; grouped by responsibility | CheckBoxPreference; existing runtime/device gating |
| `pref_smb_disable_mdns_discovery` | netshare_category | Advanced / Network Compatibility | Retained backend preference; grouped by responsibility | CheckBoxPreference; existing runtime/device gating |
| `pref_sshj` | netshare_category | Advanced / Network Compatibility | Retained backend preference; grouped by responsibility | CheckBoxPreference; existing runtime/device gating |
| `share_folders` | netshare_category | Hidden / saved value retained | Legacy/removal review; no underlying function deleted | PreferenceScreen; existing runtime/device gating |
| `pref_create_remote_thumbs` | netshare_category | Library & Metadata | Retained backend preference; grouped by responsibility | CheckBoxPreference; existing runtime/device gating |
| `network_bookmarks` | netshare_category | Playback | Retained backend preference; grouped by responsibility | CheckBoxPreference; existing runtime/device gating |
| `pref_network_prefer_vpn` | netshare_category | Network | Retained backend preference; grouped by responsibility | CheckBoxPreference; existing runtime/device gating |
| `@string/preferences_network_mobile_vpn_key` | netshare_category | Network | Retained backend preference; grouped by responsibility | CheckBoxPreference; existing runtime/device gating |
| `enable_auto_scrap_key` | scraper_category | Library & Metadata | Retained backend preference; grouped by responsibility | CheckBoxPreference; existing runtime/device gating |
| `favScraperLang` | scraper_category | Library & Metadata | Retained backend preference; grouped by responsibility | ListPreference; existing runtime/device gating |
| `@string/network_nfo_parse_prefkey` | scraper_category | Library & Metadata | Retained backend preference; grouped by responsibility | CheckBoxPreference; existing runtime/device gating |
| `scrape_from_database_key` | scraper_category | Library & Metadata | Retained backend preference; grouped by responsibility | CheckBoxPreference; existing runtime/device gating |
| `@string/nfo_export_auto_prefkey` | scraper_category | Library & Metadata | Retained backend preference; grouped by responsibility | CheckBoxPreference; existing runtime/device gating |
| `@string/rescrap_all_prefkey` | scraper_category | Library & Metadata | Retained backend preference; grouped by responsibility | Preference; existing runtime/device gating |
| `@string/rescrap_all_movies_prefkey` | scraper_category | Library & Metadata | Retained backend preference; grouped by responsibility | Preference; existing runtime/device gating |
| `@string/rescrap_all_collections_prefkey` | scraper_category | Library & Metadata | Retained backend preference; grouped by responsibility | Preference; existing runtime/device gating |
| `@string/recreate_sort_titles_prefkey` | scraper_category | Library & Metadata | Retained backend preference; grouped by responsibility | Preference; existing runtime/device gating |
| `@string/nfo_export_manual_prefkey` | scraper_category | Library & Metadata | Retained backend preference; grouped by responsibility | Preference; existing runtime/device gating |
| `@string/db_export_manual_prefkey` | scraper_category | Library & Metadata | Retained backend preference; grouped by responsibility | Preference; existing runtime/device gating |
| `@string/media_library_export_prefkey` | scraper_category | Library & Metadata | Retained backend preference; grouped by responsibility | Preference; existing runtime/device gating |
| `@string/media_library_import_prefkey` | scraper_category | Library & Metadata | Retained backend preference; grouped by responsibility | Preference; existing runtime/device gating |
| `enable_adult_scrap_key` | scraper_category | Library & Metadata | Retained backend preference; grouped by responsibility | CheckBoxPreference; existing runtime/device gating |
| `rescan_storage` | Unnamed XML category | Hidden / saved value retained | Scanning relocated to Network & Files; backend retained | Preference; existing runtime/device gating |
| `preference_display_all_files` | Unnamed XML category | Library & Metadata | Retained backend preference; grouped by responsibility | CheckBoxPreference; existing runtime/device gating |
| `preferences_torrent_path` | Unnamed XML category | Hidden / saved value retained | Legacy/removal review; no underlying function deleted | com.archos.mediacenter.video.utils.TorrentPathDialogPreference; existing runtime/device gating |
| `preferences_torrent_blocklist` | Unnamed XML category | Hidden / saved value retained | Legacy/removal review; no underlying function deleted | com.archos.mediacenter.video.utils.TorrentBlocklistDialogPreference; existing runtime/device gating |
| `enable_sponsor` | about_category | About | Retained backend preference; grouped by responsibility | CheckBoxPreference; existing runtime/device gating |
| `preferences_video_os` | about_category | About | Retained backend preference; grouped by responsibility | Preference; existing runtime/device gating |
| `preferences_video_tmdb` | about_category | About | Retained backend preference; grouped by responsibility | Preference; existing runtime/device gating |
| `preferences_video_trakt` | about_category | About | Retained backend preference; grouped by responsibility | Preference; existing runtime/device gating |
| `preferences_video_licences` | about_category | About | Retained backend preference; grouped by responsibility | PreferenceScreen; existing runtime/device gating |
| `preferences_video_advanced_quit` | preferences_category_advanced_video | Advanced | Retained backend preference; grouped by responsibility | Preference; existing runtime/device gating |

## Programmatic controls

| Key/class | Baseline location | New location | Status / reason / dependency |
| --- | --- | --- | --- |
| `preview_subtitles_by_default` | Inverse subtitles_hide_default | Subtitles | Nonpersistent positive proxy; writes existing key |
| `PlayerService.KEY_INTRODB_ENABLED` (`introdb_enabled`) | Playback integration | Integrations / IntroDB | Existing provider switch; existing segment rules retained |
| `remember_library_views` | Library | Library & Metadata | Existing independent Movies/TV view state |
| `preview_featured_recent`, `preview_featured_trending`, `preview_featured_popular` | Home & Discovery | Home | Actual local/Trakt-matched Featured sources |
| `PreviewHomeRows` | Home | Home / Home Rows | Existing row organiser and Watch Next membership |
| `PreviewAccent` | Appearance | Appearance | Existing accent choices, no theme/density system added |
| `Diagnostics.KEY` | Advanced | Advanced / diagnostics controls | Opt-in; bounded redacted output |
| `NetworkAutoRefresh`, `auto_rescan_on_app_restart`, `PreviewNetworkScanning` | Sources/scanning | Network & Files / Network Scanning | One real scheduler and per-source flags; no second scan engine |

Provider controls: the entire existing Trakt category is moved together, including sign-in, live scrobbling, resume sync, force push/pull and wipe actions. OpenSubtitles configuration follows its existing account action. IntroDB naming was verified in PlayerService; no invented subtitle/skip provider added.

About keeps the build/version and upstream attribution. Technical compatibility sections retain their existing preference implementations; nested section appearance and all device-specific visibility require Shield QA.
