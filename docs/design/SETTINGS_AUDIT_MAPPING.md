# Settings Audit-to-New-Design Mapping (working inventory)

Source: `docs/audits/settings-2026-10-26/inventory.json`, source commit `3ed6e7e8`, 0.142 implementation `b347707`. Generated 2026-10-27. **Documentation only; no code changes.**

## Scope and rules
All **165** source audit definition rows appear **exactly once** below. The new category/subsection columns are a **proposed information architecture**, NOT a claim of implementation or final user approval. Source category, key, type, default, values, dependencies, source and consumers remain authoritative in the audit JSON; this file links each row to the new design. `REVIEW / NON-EXPOSED` means hidden/legacy entries that must be dispositioned explicitly, **not** silently surfaced or dropped. 26 backup export categories are nested option families, not 26 new standalone Settings screens. UI interaction is a preliminary classification and must be checked against source handlers before coding.

## Coverage by proposed destination
- About Supernova: **6** rows
- Appearance: **5** rows
- System & Maintenance: **58** rows
- Audio & Subtitles: **10** rows
- Playback & Video: **11** rows
- REVIEW / NON-EXPOSED: **23** rows
- Library & Home: **24** rows
- Connections & Integrations: **20** rows
- Streaming & Providers: **8** rows

## Complete mapping
| ID | Existing category | New category | Proposed subsection | Audited label | Key | Audited control type | UI treatment / caution |
|---|---|---|---|---|---|---|---|
| S001 | About | About Supernova | About & legal | Build | `preferences_version` | Preference | inspect audited control type |
| S002 | Appearance | Appearance | Interface & theme | User interface language | `ui_lang` | ListPreference | single-choice selector |
| S003 | Advanced / Video Compatibility | System & Maintenance | Video compatibility | Force software decoding | `force_software_decoding` | CheckBoxPreference | toggle |
| S004 | Advanced / Video Compatibility | System & Maintenance | Video compatibility | Choose decoder | `dec_choice` | ListPreference | single-choice selector |
| S005 | Advanced / Audio Compatibility | System & Maintenance | Audio compatibility | Choose audio interface | `audio_interface_choice` | ListPreference | single-choice selector |
| S006 | Advanced / Audio Compatibility | System & Maintenance | Audio compatibility | Choose audio decoder | `audio_decoder_choice` | ListPreference | single-choice selector |
| S007 | Advanced / Video Compatibility | System & Maintenance | Video compatibility | Fullscreen video with display cutouts | `enable_cutout_mode_short_edges` | CheckBoxPreference | toggle |
| S008 | Advanced / Video Compatibility | System & Maintenance | Video compatibility | Cut display equally on both sides | `enable_cutout_both_sidesx` | CheckBoxPreference | toggle |
| S009 | Audio | Audio & Subtitles | Audio output & tracks | Audio passthrough | `force_audio_passthrough_multiple` | ListPreference | single-choice selector |
| S010 | Advanced / Audio Compatibility | System & Maintenance | Audio compatibility | Force audio passthrough | `force_passthrough` | CheckBoxPreference | toggle |
| S011 | Playback | Playback & Video | Playback | Enable playback speed | `playback_speed` | CheckBoxPreference | toggle |
| S012 | Advanced / Audio Compatibility | System & Maintenance | Audio compatibility | Use AudioTrack for playback speed | `audio_speed_audiotrack` | CheckBoxPreference | toggle |
| S013 | Advanced / Audio Compatibility | System & Maintenance | Audio compatibility | Dynamic audio/video delay estimation | `enable_dynamic_audio_delay` | CheckBoxPreference | toggle |
| S014 | Audio | Audio & Subtitles | Audio output & tracks | Platform spatialization | `player_spatialization_enabled` | CheckBoxPreference | toggle |
| S015 | Advanced / Audio Compatibility | System & Maintenance | Audio compatibility | Disable audio stereo downmix | `disable_downmix` | CheckBoxPreference | toggle |
| S016 | Advanced / Video Compatibility | System & Maintenance | Video compatibility | Parser synchronisation mode | `parser_sync_mode` | ListPreference | single-choice selector |
| S017 | Advanced / Audio Compatibility | System & Maintenance | Audio compatibility | Force audio stereo downmix | `enable_downmix_androidtv` | CheckBoxPreference | toggle |
| S018 | Video | Playback & Video | Video & Display | Enable automatic refresh rate switch | `enable_tv_refreshrate_switch_mode` | ListPreference | single-choice selector |
| S019 | Video | Playback & Video | Video & Display | Dolby Vision mode | `dolby_vision_mode` | ListPreference | single-choice selector |
| S020 | Audio | Audio & Subtitles | Audio output & tracks | Prefer original audio track | `prefer_original_audio_track` | CheckBoxPreference | toggle |
| S021 | Audio | Audio & Subtitles | Audio output & tracks | Audio preferred language | `favAudioLang` | ListPreference | single-choice selector |
| S022 | Video | Playback & Video | Video & Display | Enable automatic 3D TV switch | `activate_tv_switch` | CheckBoxPreference | toggle |
| S023 | Advanced / Network Compatibility | System & Maintenance | Network compatibility | Stream buffer size | `stream_buffer_size` | EditTextPreference | text/form |
| S024 | Advanced / Video Compatibility | System & Maintenance | Video compatibility | Max i-frame size | `stream_max_iframe_size` | EditTextPreference | text/form |
| S025 | Playback | Playback & Video | Playback | Allow using another video player | `allow_3rd_party_player` | CheckBoxPreference | toggle |
| S026 | Hidden / Legacy | REVIEW / NON-EXPOSED | Hidden & legacy | Smart recent categories | `smart_recently_rows` | CheckBoxPreference | hidden / legacy — no new visible control without review |
| S027 | Playback | Playback & Video | Playback | Enable projector mode | `player_projector_mode_key` | CheckBoxPreference | toggle |
| S028 | Library & Metadata | Library & Home | Library & metadata | Hide watched videos | `hide_watched` | CheckBoxPreference | toggle |
| S029 | Library & Metadata | Library & Home | Library & metadata | Ignore initial articles when sorting | `sort_ignore_articles` | CheckBoxPreference | toggle |
| S030 | Appearance | Appearance | Interface & theme | User interface mode | `uimode` | ListPreference | single-choice selector |
| S031 | Appearance | Appearance | Interface & theme | User interface mode | `uimode_leanback` | ListPreference | single-choice selector |
| S032 | Appearance | Appearance | Interface & theme | Interface size | `ui_zoom` | Preference | inspect audited control type |
| S033 | Playback | Playback & Video | Resume & Progress | Display resume box | `display_resume_box` | CheckBoxPreference | toggle |
| S034 | Playback | Playback & Video | Playback | Remember video brightness. | `reset_brightness_on_start` | CheckBoxPreference | toggle |
| S035 | Playback | Playback & Video | Playback | Hide controls on pause. | `hide_controls_on_pause` | CheckBoxPreference | toggle |
| S036 | Hidden / Legacy | REVIEW / NON-EXPOSED | Hidden & legacy | Color theme | `app_theme` | ListPreference | hidden / legacy — no new visible control without review |
| S037 | Hidden / Legacy | REVIEW / NON-EXPOSED | Hidden & legacy | Always start in TV user inferface | `always_leanback_on_tv_key` | CheckBoxPreference | hidden / legacy — no new visible control without review |
| S038 | Hidden / Legacy | REVIEW / NON-EXPOSED | Hidden & legacy | Separate animations from movies and TV shows | `separate_anime_movie_show` | CheckBoxPreference | hidden / legacy — no new visible control without review |
| S039 | Hidden / Legacy | REVIEW / NON-EXPOSED | Hidden & legacy | Show last added row | `show_last_added_row` | CheckBoxPreference | hidden / legacy — no new visible control without review |
| S040 | Hidden / Legacy | REVIEW / NON-EXPOSED | Hidden & legacy | Show last played row | `show_last_played_row` | CheckBoxPreference | hidden / legacy — no new visible control without review |
| S041 | Hidden / Legacy | REVIEW / NON-EXPOSED | Hidden & legacy | Show watching/up next row | `show_watching_up_next_row` | CheckBoxPreference | hidden / legacy — no new visible control without review |
| S042 | Hidden / Legacy | REVIEW / NON-EXPOSED | Hidden & legacy | Reset last played row | `reset_last_played_row` | Preference | hidden / legacy — no new visible control without review |
| S043 | Hidden / Legacy | REVIEW / NON-EXPOSED | Hidden & legacy | Show all movie row | `show_all_movies_row` | CheckBoxPreference | hidden / legacy — no new visible control without review |
| S044 | Hidden / Legacy | REVIEW / NON-EXPOSED | Hidden & legacy | All movie row sort order | `preferences_movie_sort_order` | ListPreference | hidden / legacy — no new visible control without review |
| S045 | Hidden / Legacy | REVIEW / NON-EXPOSED | Hidden & legacy | Try new UI | `try_new_ui` | SwitchPreferenceCompat | hidden / legacy — no new visible control without review |
| S046 | Hidden / Legacy | REVIEW / NON-EXPOSED | Hidden & legacy | Show documentaries | `show_documentaries` | CheckBoxPreference | hidden / legacy — no new visible control without review |
| S047 | Hidden / Legacy | REVIEW / NON-EXPOSED | Hidden & legacy | Show all TV show row | `show_all_tv_shows_row` | CheckBoxPreference | hidden / legacy — no new visible control without review |
| S048 | Hidden / Legacy | REVIEW / NON-EXPOSED | Hidden & legacy | All TV show row sort order | `preferences_tv_show_sort_order` | ListPreference | hidden / legacy — no new visible control without review |
| S049 | Hidden / Legacy | REVIEW / NON-EXPOSED | Hidden & legacy | Show all animations row | `show_all_animes_row` | CheckBoxPreference | hidden / legacy — no new visible control without review |
| S050 | Hidden / Legacy | REVIEW / NON-EXPOSED | Hidden & legacy | All animations row sort order | `preferences_animes_sort_order` | ListPreference | hidden / legacy — no new visible control without review |
| S051 | Integrations / OpenSubtitles | Connections & Integrations | OpenSubtitles | OpenSubtitles credentials | `subtitles_credentials` | Preference | integration / action — inspect |
| S052 | Subtitles | Audio & Subtitles | Subtitles | Hide subtitles by default | `subtitles_hide_default` | CheckBoxPreference | toggle |
| S053 | Subtitles | Audio & Subtitles | Subtitles | Subtitles reading language | `favSubLang` | ListPreference | single-choice selector |
| S054 | Subtitles | Audio & Subtitles | Subtitles | Subtitles download languages | `languages_list` | MultiSelectListPreference | single-choice selector |
| S055 | Advanced / Subtitle Compatibility | System & Maintenance | Subtitle compatibility | Subtitles encoding | `codepage` | ListPreference | single-choice selector |
| S056 | Integrations / Trakt | Connections & Integrations | Trakt | Upgrade to ad-free version | `trakt_getfull` | Preference | inspect audited control type |
| S057 | Integrations / Trakt | Connections & Integrations | Trakt | Sign In | `trakt_signin` | TraktSigninDialogPreference | integration / action — inspect |
| S058 | Integrations / Trakt | Connections & Integrations | Trakt | Live scrobbler | `trakt_live_scrobbling` | CheckBoxPreference | toggle |
| S059 | Integrations / Trakt | Connections & Integrations | Trakt | Sync trakt resume position | `trakt_sync_resume` | CheckBoxPreference | toggle |
| S060 | Integrations / Trakt | Connections & Integrations | Trakt | Force re-upload to Trakt | `trakt_force_push` | Preference | integration / action — inspect |
| S061 | Integrations / Trakt | Connections & Integrations | Trakt | Force re-import from Trakt | `trakt_force_pull` | Preference | integration / action — inspect |
| S062 | Integrations / Trakt | Connections & Integrations | Trakt | Sign out of Trakt.tv | `trakt_wipe` | TraktWipeDialogPreference | integration / action — inspect |
| S063 | Advanced / Network Compatibility | System & Maintenance | Network compatibility | Use alternate SMB implementation (SMB2+) | `pref_smbj` | CheckBoxPreference | toggle |
| S064 | Advanced / Network Compatibility | System & Maintenance | Network compatibility | Enable SMBv2 support | `pref_smbv2` | CheckBoxPreference | toggle |
| S065 | Advanced / Network Compatibility | System & Maintenance | Network compatibility | SMB resolver order | `pref_smb_resolv` | CheckBoxPreference | toggle |
| S066 | Advanced / Network Compatibility | System & Maintenance | Network compatibility | Disable SMB TCP discovery | `pref_smb_disable_tcp_discovery` | CheckBoxPreference | toggle |
| S067 | Advanced / Network Compatibility | System & Maintenance | Network compatibility | Disable SMB UDP discovery | `pref_smb_disable_udp_discovery` | CheckBoxPreference | toggle |
| S068 | Advanced / Network Compatibility | System & Maintenance | Network compatibility | Disable SMB mDNS discovery | `pref_smb_disable_mdns_discovery` | CheckBoxPreference | toggle |
| S069 | Advanced / Network Compatibility | System & Maintenance | Network compatibility | Use newer SFTP implementation | `pref_sshj` | CheckBoxPreference | toggle |
| S070 | Network | Connections & Integrations | Network connections | Shared folders settings | `share_folders` | PreferenceScreen | inspect audited control type |
| S071 | Library & Metadata | Library & Home | Library & metadata | Create remote thumbnails | `pref_create_remote_thumbs` | CheckBoxPreference | toggle |
| S072 | Playback | Playback & Video | Resume & Progress | Resume points and position bookmarks on network | `network_bookmarks` | CheckBoxPreference | toggle |
| S073 | Network | Connections & Integrations | Network connections | Prefer VPN network | `pref_network_prefer_vpn` | CheckBoxPreference | toggle |
| S074 | Network | Connections & Integrations | Network connections | Enable network share on mobile connection | `vpn_mobile` | CheckBoxPreference | toggle |
| S075 | Library & Metadata | Library & Home | Library & metadata | Automatically get info for new videos | `enable_auto_scrap_key` | CheckBoxPreference | toggle |
| S076 | Library & Metadata | Library & Home | Library & metadata | Video description language | `favScraperLang` | ListPreference | single-choice selector |
| S077 | Library & Metadata | Library & Home | Library & metadata | Process \'.nfo\' files | `network_nfo_parse` | CheckBoxPreference | toggle |
| S078 | Library & Metadata | Library & Home | Library & metadata | Use already scraped Information | `scrape_from_database_key` | CheckBoxPreference | toggle |
| S079 | Library & Metadata | Library & Home | Library & metadata | Auto-export after search | `nfo_export_auto` | CheckBoxPreference | toggle |
| S080 | Library & Metadata | Library & Home | Library & metadata | Rescrape all | `rescrap_all_prefkey` | Preference | action / confirmation — inspect |
| S081 | Library & Metadata | Library & Home | Library & metadata | Rescrape all movies | `rescrap_all_movies_prefkey` | Preference | action / confirmation — inspect |
| S082 | Library & Metadata | Library & Home | Library & metadata | Rescrape all collections | `rescrap_all_collections_prefkey` | Preference | action / confirmation — inspect |
| S083 | Library & Metadata | Library & Home | Library & metadata | Recreate sort titles | `recreate_sort_titles_prefkey` | Preference | inspect audited control type |
| S084 | Library & Metadata | Library & Home | Library & metadata | Export already scraped videos | `export_manual_prefkey` | Preference | action / confirmation — inspect |
| S085 | Library & Metadata | Library & Home | Library & metadata | Export media database | `db_export_manual_prefkey` | Preference | action / confirmation — inspect |
| S086 | Advanced / Backup & Restore | System & Maintenance | Backup & restore | Export media library + credentials + shortcuts | `media_library_export_prefkey` | Preference | integration / action — inspect |
| S087 | Advanced / Backup & Restore | System & Maintenance | Backup & restore | Import media library + credentials + shortcuts | `media_library_import_prefkey` | Preference | integration / action — inspect |
| S088 | Library & Metadata | Library & Home | Library & metadata | Adult video scraping | `enable_adult_scrap_key` | CheckBoxPreference | toggle |
| S089 | Library & Metadata / Hidden | REVIEW / NON-EXPOSED | Library hidden/runtime | Full Library Scan (runtime; hidden) | `rescan_storage` | Preference | hidden / legacy — no new visible control without review |
| S090 | Library & Metadata | Library & Home | Library & metadata | Display all files in browser | `preference_display_all_files` | CheckBoxPreference | toggle |
| S091 | About | About Supernova | About & legal | Enable Nova sponsor link | `enable_sponsor` | CheckBoxPreference | legacy sponsor link — review before exposing |
| S092 | About | About Supernova | About & legal | Subtitles provided by | `preferences_video_os` | Preference | inspect audited control type |
| S093 | About | About Supernova | About & legal | Movie/TV Show information provided by | `preferences_video_tmdb` | Preference | inspect audited control type |
| S094 | About | About Supernova | About & legal | Video scrobbler provided by | `preferences_video_trakt` | Preference | inspect audited control type |
| S095 | About | About Supernova | About & legal | Licenses | `preferences_video_licences` | PreferenceScreen | inspect audited control type |
| S096 | Advanced / Legacy | REVIEW / NON-EXPOSED | Advanced legacy | Regular preferences | `preferences_video_advanced_quit` | Preference | hidden / legacy — no new visible control without review |
| S097 | Hidden / Legacy | REVIEW / NON-EXPOSED | Hidden & legacy | Make remaining time negative in player | `make_time_negative` | CheckBoxPreference | hidden / legacy — no new visible control without review |
| S098 | Hidden / Legacy | REVIEW / NON-EXPOSED | Hidden & legacy | Hide trailer row in movies | `hide_trailer_row` | CheckBoxPreference | hidden / legacy — no new visible control without review |
| S099 | Hidden / Legacy | REVIEW / NON-EXPOSED | Hidden & legacy | Show movies/tv shows by rating | `show_by_rating` | CheckBoxPreference | hidden / legacy — no new visible control without review |
| S100 | Library & Metadata | Library & Home | Library & metadata | Remember library view preferences | `remember_library_views` | Switch | inspect audited control type |
| S101 | Library & Metadata / Hidden (Network & Files owns scheduling) | REVIEW / NON-EXPOSED | Network & Files scheduling | Scan network library on return | `auto_rescan_on_app_restart` | Switch | hidden / legacy — no new visible control without review |
| S102 | Subtitles | Audio & Subtitles | Subtitles | Prepare Subtitle Searches | `supernova_subtitle_discovery` | Switch | action / confirmation — inspect |
| S103 | Subtitles | Audio & Subtitles | Subtitles | Prepare Current Season | `supernova_subtitle_season_prefetch` | Switch | action / confirmation — inspect |
| S104 | Library & Metadata | Library & Home | Watch history | Track Watch History | `supernova_track_viewing_history` | Switch | inspect audited control type |
| S105 | Library & Metadata | Playback & Video | Resume & Progress | Remember Playback Position | `supernova_save_resume` | Switch | inspect audited control type |
| S106 | Advanced / Backup & Restore (model) | System & Maintenance | Backup & restore | Include Watch History in Backups | `supernova_backup_history` | Switch | nested backup model/option — not standalone top-level setting |
| S107 | Advanced / Backup & Restore (model) | System & Maintenance | Backup & restore | Include Playback Positions in Backups | `supernova_backup_resume` | Switch | nested backup model/option — not standalone top-level setting |
| S108 | Integrations | Connections & Integrations | Service integrations | Trakt | `supernova_integration_trakt` | Switch | integration / action — inspect |
| S109 | Integrations | Connections & Integrations | Service integrations | OpenSubtitles | `supernova_integration_opensubtitles` | Switch | integration / action — inspect |
| S110 | Integrations | Connections & Integrations | Service integrations | IntroDB | `supernova_segment_enabled` | Switch | integration / action — inspect |
| S111 | Home | Library & Home | Home | Featured: Recently Added | `preview_featured_recent` | Switch | inspect audited control type |
| S112 | Home | Library & Home | Home | Featured: Trakt Trending | `preview_featured_trending` | Switch | integration / action — inspect |
| S113 | Home | Library & Home | Home | Featured: Trakt Popular | `preview_featured_popular` | Switch | integration / action — inspect |
| S114 | Home | Library & Home | Home | Home rows | `preview_home_editor` | Action | inspect audited control type |
| S115 | Home | Library & Home | Home | Clear Watch Next | `preview_clear_watch_next` | Action | action / confirmation — inspect |
| S116 | Appearance | Appearance | Interface & theme | Accent & Colour | `preview_accent_picker` | Action | inspect audited control type |
| S117 | Library & Metadata | Library & Home | Watch history | Clear Viewing History | `supernova_clear_history` | Action | action / confirmation — inspect |
| S118 | Network | Connections & Integrations | Network connections | SFTP server identities | `preview_sftp_trust` | Action | inspect audited control type |
| S119 | Advanced / Diagnostics | System & Maintenance | Diagnostics | Issues Digest | `preview_diagnostic_Issues Digest` | Action | inspect audited control type |
| S120 | Advanced / Diagnostics | System & Maintenance | Diagnostics | Report a Problem | `preview_diagnostic_Report a Problem` | Action | action / confirmation — inspect |
| S121 | Advanced / Diagnostics | System & Maintenance | Diagnostics | Show Latest Reference | `preview_diagnostic_Show Latest Reference` | Action | action / confirmation — inspect |
| S122 | Advanced / Diagnostics | System & Maintenance | Diagnostics | Export Diagnostic Report | `export_diagnostic_report` | Action | action / confirmation — inspect |
| S123 | Advanced / Diagnostics | System & Maintenance | Diagnostics | Diagnostic Logging | `supernova_diagnostic_logging` | Switch | inspect audited control type |
| S124 | Advanced / Diagnostics | System & Maintenance | Diagnostics | Diagnostic Level | `supernova_diagnostic_level` | List | inspect audited control type |
| S125 | Subtitles | Audio & Subtitles | Subtitles | Subtitles by Default | `preview_subtitles_by_default` | Switch | inspect audited control type |
| S126 | Library & Metadata / Hidden | REVIEW / NON-EXPOSED | Network & Files scheduling | Automatic network scan interval | `preview_scan_interval_control` | List | hidden / legacy — no new visible control without review |
| S127 | Integrations / IntroDB | Connections & Integrations | IntroDB | Intro | `supernova_segment_intro` | List | inspect audited control type |
| S128 | Integrations / IntroDB | Connections & Integrations | IntroDB | Recap | `supernova_segment_recap` | List | inspect audited control type |
| S129 | Integrations / IntroDB | Connections & Integrations | IntroDB | Preview | `supernova_segment_preview` | List | inspect audited control type |
| S130 | Integrations / IntroDB | Connections & Integrations | IntroDB | Outro | `supernova_segment_outro` | List | inspect audited control type |
| S131 | Integrations / IntroDB | Connections & Integrations | IntroDB | Credits | `supernova_segment_credits` | List | inspect audited control type |
| S132 | Streaming | Streaming & Providers | Streaming services | Show streaming options | `streaming_enabled` | Switch | inspect audited control type |
| S133 | Streaming | Streaming & Providers | Streaming services | Country | `streaming_country` | List | inspect audited control type |
| S134 | Streaming | Streaming & Providers | Streaming services | Search providers | `streaming_provider_search` | Text | action / confirmation — inspect |
| S135 | Streaming | Streaming & Providers | Streaming services | Show all providers | `streaming_show_all` | Switch | inspect audited control type |
| S136 | Streaming | Streaming & Providers | Streaming services | My Providers | `streaming_providers_{country}` | MultiSelect | inspect audited control type |
| S137 | Streaming | Streaming & Providers | Streaming services | Preferred provider | `streaming_preferred_{country}` | List | inspect audited control type |
| S138 | Streaming | Streaming & Providers | Streaming services | Refresh provider list | `streaming_provider_status` | Action | action / confirmation — inspect |
| S139 | Streaming | Streaming & Providers | Streaming services | JustWatch | `(JustWatch)` | Action | inspect audited control type |
| S140 | Advanced / Backup & Restore / Export categories | System & Maintenance | Backup export categories | Appearance settings | `supernova_backup_include_appearance` | Checkbox | nested backup model/option — not standalone top-level setting |
| S141 | Advanced / Backup & Restore / Export categories | System & Maintenance | Backup export categories | Audio settings | `supernova_backup_include_audio` | Checkbox | nested backup model/option — not standalone top-level setting |
| S142 | Advanced / Backup & Restore / Export categories | System & Maintenance | Backup export categories | Diagnostic preferences | `supernova_backup_include_diagnostics` | Checkbox | nested backup model/option — not standalone top-level setting |
| S143 | Advanced / Backup & Restore / Export categories | System & Maintenance | Backup export categories | Service settings | `supernova_backup_include_integrations` | Checkbox | nested backup model/option — not standalone top-level setting |
| S144 | Advanced / Backup & Restore / Export categories | System & Maintenance | Backup export categories | Library settings | `supernova_backup_include_library` | Checkbox | nested backup model/option — not standalone top-level setting |
| S145 | Advanced / Backup & Restore / Export categories | System & Maintenance | Backup export categories | Network settings | `supernova_backup_include_network` | Checkbox | nested backup model/option — not standalone top-level setting |
| S146 | Advanced / Backup & Restore / Export categories | System & Maintenance | Backup export categories | Playback settings | `supernova_backup_include_playback` | Checkbox | nested backup model/option — not standalone top-level setting |
| S147 | Advanced / Backup & Restore / Export categories | System & Maintenance | Backup export categories | Streaming settings | `supernova_backup_include_streaming` | Checkbox | nested backup model/option — not standalone top-level setting |
| S148 | Advanced / Backup & Restore / Export categories | System & Maintenance | Backup export categories | Subtitle settings | `supernova_backup_include_subtitles` | Checkbox | nested backup model/option — not standalone top-level setting |
| S149 | Advanced / Backup & Restore / Export categories | System & Maintenance | Backup export categories | Video settings | `supernova_backup_include_video` | Checkbox | nested backup model/option — not standalone top-level setting |
| S150 | Advanced / Backup & Restore / Export categories | System & Maintenance | Backup export categories | Home rows | `supernova_backup_include_home` | Checkbox | nested backup model/option — not standalone top-level setting |
| S151 | Advanced / Backup & Restore / Export categories | System & Maintenance | Backup export categories | Library pages | `supernova_backup_include_custom_pages` | Checkbox | nested backup model/option — not standalone top-level setting |
| S152 | Advanced / Backup & Restore / Export categories | System & Maintenance | Backup export categories | Hidden titles | `supernova_backup_include_hidden` | Checkbox | nested backup model/option — not standalone top-level setting |
| S153 | Advanced / Backup & Restore / Export categories | System & Maintenance | Backup export categories | Media matches | `supernova_backup_include_matches` | Checkbox | nested backup model/option — not standalone top-level setting |
| S154 | Advanced / Backup & Restore / Export categories | System & Maintenance | Backup export categories | Pinned titles | `supernova_backup_include_pinned` | Checkbox | nested backup model/option — not standalone top-level setting |
| S155 | Advanced / Backup & Restore / Export categories | System & Maintenance | Backup export categories | Preferred versions | `supernova_backup_include_preferred` | Checkbox | nested backup model/option — not standalone top-level setting |
| S156 | Advanced / Backup & Restore / Export categories | System & Maintenance | Backup export categories | Review notes | `supernova_backup_include_review` | Checkbox | nested backup model/option — not standalone top-level setting |
| S157 | Advanced / Backup & Restore / Export categories | System & Maintenance | Backup export categories | Saved playlists | `supernova_backup_include_playlists` | Checkbox | nested backup model/option — not standalone top-level setting |
| S158 | Advanced / Backup & Restore / Export categories | System & Maintenance | Backup export categories | Watch Next | `supernova_backup_include_watch_next` | Checkbox | nested backup model/option — not standalone top-level setting |
| S159 | Advanced / Backup & Restore / Export categories | System & Maintenance | Backup export categories | Continue Watching | `supernova_backup_include_continue_choices` | Checkbox | nested backup model/option — not standalone top-level setting |
| S160 | Advanced / Backup & Restore / Export categories | System & Maintenance | Backup export categories | Playback positions | `supernova_backup_include_positions` | Checkbox | nested backup model/option — not standalone top-level setting |
| S161 | Advanced / Backup & Restore / Export categories | System & Maintenance | Backup export categories | Title playback | `supernova_backup_include_title_playback` | Checkbox | nested backup model/option — not standalone top-level setting |
| S162 | Advanced / Backup & Restore / Export categories | System & Maintenance | Backup export categories | Watch history | `supernova_backup_include_history` | Checkbox | nested backup model/option — not standalone top-level setting |
| S163 | Advanced / Backup & Restore / Export categories | System & Maintenance | Backup export categories | File bookmarks | `supernova_backup_include_bookmarks` | Checkbox | nested backup model/option — not standalone top-level setting |
| S164 | Advanced / Backup & Restore / Export categories | System & Maintenance | Backup export categories | Library sources | `supernova_backup_include_sources` | Checkbox | nested backup model/option — not standalone top-level setting |
| S165 | Advanced / Backup & Restore / Export categories | System & Maintenance | Backup export categories | Filenames and locations | `supernova_backup_include_locations` | Checkbox | nested backup model/option — not standalone top-level setting |

## Open functional review / sign-off gates
1. Confirm hidden/legacy, hidden runtime and source scheduling entries: no accidental resurrection of deprecated preferences. Distinguish entry points owned by Network & Files from Settings links.
2. Confirm `S105` playback position and `S104` watch history placement, plus source coupling with Resume & Progress, Home/Continue Watching and Trakt.
3. Confirm `S033`, `S034`, `S035`, `S072` and all Playback & Video controls fit approved subsections without duplication.
4. Verify all single-choice values and runtime options (refresh-rate switch, Dolby Vision, audio language) before UI copy. Refresh-rate is **not** a Boolean toggle.
5. Review category-specific UI: streaming provider multi-select/focus bug, subtitle listener bug, backup export/import privacy semantics, diagnostics gating, network identity forms and sign-in flows.
6. Audit known defects from `ISSUES.md` and ensure migration preserves or explicitly fixes their listener/focus behaviour with tests.
7. Verify exact original Supernova background, fonts, icons and palette asset paths before claiming implementation readiness.
8. Ensure every visible option, nested control family, dependency and action has a test case and no missing audit ID. All source default states must be read from the audit and current user state at runtime, never copied from mock-ups.
