# Nvidia Shield TV Pro — complete Settings QA checklist

[Audit](AUDIT.md) · [Inventory](INVENTORY.md) · [Navigation](NAVIGATION.md) · [Issues](ISSUES.md)

**All checks PENDING. No physical Shield tests were performed in this audit.** Do not mark a source/test assertion as a device pass.

## Fixture and evidence record

Record Shield model,Android version,display resolution/logical scale,AV receiver/audio route,remote/keyboard,installed version/code and APK hash. Expected source b347707defdbf2ab3ae83d920ba9fa7af3936220; expected delivered APK hash is in AUDIT.md. Use a disposable library/profile and copies of archives. Preserve initial settings,trust identities,account grants and backup destinations. Test local and read-only/writable SMB,WebDAV and Put.io fixtures; do not clear real history or restore a production library.

For each result record PASS/FAIL/BLOCKED,exact key sequence,initial/current values,expected/actual outcome,screenshot/video reference and sanitised diagnostic marker. Keep credentials and viewing data out of public reports. Capability-removed rows are N/A with the actual gate stated,not automatically PASS. Hidden controls require storage/consumer compatibility checks on a test fixture rather than invented D-pad access.

## Required procedure for every inventory case

T: reach by D-pad with explicit activation; record label/description/current value/default on clean profile. Toggle both ways or enumerate every selector value; verify dependencies,immediate versus next-playback behaviour and cancellation. B: check unusual/invalid input and unsupported state when applicable,first/last row,long text and focused context. P: leave/reenter Settings,force-stop/restart,then reboot; compare stored and effective values. Actions: verify cancellation,safe default,success,error/retry and return focus rather than inventing a persisted action value. Hidden/model rows: verify owning visible proxy and stored compatibility; do not alter production data.

| Case | Control | Required route | Required coverage | Result |
| --- | --- | --- | --- | --- |
| QA-S001 | Build (`preferences_version`) | About | T/B/P; values/default in S001; source-only/hidden status must be recorded | PENDING |
| QA-S002 | User interface language (`ui_lang`) | Appearance | T/B/P; values/default in S002; source-only/hidden status must be recorded | PENDING |
| QA-S003 | Force software decoding (`force_software_decoding`) | Advanced / Video Compatibility | T/B/P; values/default in S003; source-only/hidden status must be recorded | PENDING |
| QA-S004 | Choose decoder (`dec_choice`) | Advanced / Video Compatibility | T/B/P; values/default in S004; source-only/hidden status must be recorded | PENDING |
| QA-S005 | Choose audio interface (`audio_interface_choice`) | Advanced / Audio Compatibility | T/B/P; values/default in S005; source-only/hidden status must be recorded | PENDING |
| QA-S006 | Choose audio decoder (`audio_decoder_choice`) | Advanced / Audio Compatibility | T/B/P; values/default in S006; source-only/hidden status must be recorded | PENDING |
| QA-S007 | Fullscreen video with display cutouts (`enable_cutout_mode_short_edges`) | Advanced / Video Compatibility | T/B/P; values/default in S007; source-only/hidden status must be recorded | PENDING |
| QA-S008 | Cut display equally on both sides (`enable_cutout_both_sidesx`) | Advanced / Video Compatibility | T/B/P; values/default in S008; source-only/hidden status must be recorded | PENDING |
| QA-S009 | Audio passthrough (`force_audio_passthrough_multiple`) | Audio | T/B/P; values/default in S009; source-only/hidden status must be recorded | PENDING |
| QA-S010 | Force audio passthrough (`force_passthrough`) | Advanced / Audio Compatibility | T/B/P; values/default in S010; source-only/hidden status must be recorded | PENDING |
| QA-S011 | Enable playback speed (`playback_speed`) | Playback | T/B/P; values/default in S011; source-only/hidden status must be recorded | PENDING |
| QA-S012 | Use AudioTrack for playback speed (`audio_speed_audiotrack`) | Advanced / Audio Compatibility | T/B/P; values/default in S012; source-only/hidden status must be recorded | PENDING |
| QA-S013 | Dynamic audio/video delay estimation (`enable_dynamic_audio_delay`) | Advanced / Audio Compatibility | T/B/P; values/default in S013; source-only/hidden status must be recorded | PENDING |
| QA-S014 | Platform spatialization (`player_spatialization_enabled`) | Audio | T/B/P; values/default in S014; source-only/hidden status must be recorded | PENDING |
| QA-S015 | Disable audio stereo downmix (`disable_downmix`) | Advanced / Audio Compatibility | T/B/P; values/default in S015; source-only/hidden status must be recorded | PENDING |
| QA-S016 | Parser synchronisation mode (`parser_sync_mode`) | Advanced / Video Compatibility | T/B/P; values/default in S016; source-only/hidden status must be recorded | PENDING |
| QA-S017 | Force audio stereo downmix (`enable_downmix_androidtv`) | Advanced / Audio Compatibility | T/B/P; values/default in S017; source-only/hidden status must be recorded | PENDING |
| QA-S018 | Enable automatic refresh rate switch (`enable_tv_refreshrate_switch_mode`) | Video | T/B/P; values/default in S018; source-only/hidden status must be recorded | PENDING |
| QA-S019 | Dolby Vision mode (`dolby_vision_mode`) | Video | T/B/P; values/default in S019; source-only/hidden status must be recorded | PENDING |
| QA-S020 | Prefer original audio track (`prefer_original_audio_track`) | Audio | T/B/P; values/default in S020; source-only/hidden status must be recorded | PENDING |
| QA-S021 | Audio preferred language (`favAudioLang`) | Audio | T/B/P; values/default in S021; source-only/hidden status must be recorded | PENDING |
| QA-S022 | Enable automatic 3D TV switch (`activate_tv_switch`) | Video | T/B/P; values/default in S022; source-only/hidden status must be recorded | PENDING |
| QA-S023 | Stream buffer size (`stream_buffer_size`) | Advanced / Network Compatibility | T/B/P; values/default in S023; source-only/hidden status must be recorded | PENDING |
| QA-S024 | Max i-frame size (`stream_max_iframe_size`) | Advanced / Video Compatibility | T/B/P; values/default in S024; source-only/hidden status must be recorded | PENDING |
| QA-S025 | Allow using another video player (`allow_3rd_party_player`) | Playback | T/B/P; values/default in S025; source-only/hidden status must be recorded | PENDING |
| QA-S026 | Smart recent categories (`smart_recently_rows`) | Hidden / Legacy | T/B/P; values/default in S026; source-only/hidden status must be recorded | PENDING |
| QA-S027 | Enable projector mode (`player_projector_mode_key`) | Playback | T/B/P; values/default in S027; source-only/hidden status must be recorded | PENDING |
| QA-S028 | Hide watched videos (`hide_watched`) | Library & Metadata | T/B/P; values/default in S028; source-only/hidden status must be recorded | PENDING |
| QA-S029 | Ignore initial articles when sorting (`sort_ignore_articles`) | Library & Metadata | T/B/P; values/default in S029; source-only/hidden status must be recorded | PENDING |
| QA-S030 | User interface mode (`uimode`) | Appearance | T/B/P; values/default in S030; source-only/hidden status must be recorded | PENDING |
| QA-S031 | User interface mode (`uimode_leanback`) | Appearance | T/B/P; values/default in S031; source-only/hidden status must be recorded | PENDING |
| QA-S032 | Interface size (`ui_zoom`) | Appearance | T/B/P; values/default in S032; source-only/hidden status must be recorded | PENDING |
| QA-S033 | Display resume box (`display_resume_box`) | Playback | T/B/P; values/default in S033; source-only/hidden status must be recorded | PENDING |
| QA-S034 | Remember video brightness. (`reset_brightness_on_start`) | Playback | T/B/P; values/default in S034; source-only/hidden status must be recorded | PENDING |
| QA-S035 | Hide controls on pause. (`hide_controls_on_pause`) | Playback | T/B/P; values/default in S035; source-only/hidden status must be recorded | PENDING |
| QA-S036 | Color theme (`app_theme`) | Hidden / Legacy | T/B/P; values/default in S036; source-only/hidden status must be recorded | PENDING |
| QA-S037 | Always start in TV user inferface (`always_leanback_on_tv_key`) | Hidden / Legacy | T/B/P; values/default in S037; source-only/hidden status must be recorded | PENDING |
| QA-S038 | Separate animations from movies and TV shows (`separate_anime_movie_show`) | Hidden / Legacy | T/B/P; values/default in S038; source-only/hidden status must be recorded | PENDING |
| QA-S039 | Show last added row (`show_last_added_row`) | Hidden / Legacy | T/B/P; values/default in S039; source-only/hidden status must be recorded | PENDING |
| QA-S040 | Show last played row (`show_last_played_row`) | Hidden / Legacy | T/B/P; values/default in S040; source-only/hidden status must be recorded | PENDING |
| QA-S041 | Show watching/up next row (`show_watching_up_next_row`) | Hidden / Legacy | T/B/P; values/default in S041; source-only/hidden status must be recorded | PENDING |
| QA-S042 | Reset last played row (`reset_last_played_row`) | Hidden / Legacy | T/B/P; values/default in S042; source-only/hidden status must be recorded | PENDING |
| QA-S043 | Show all movie row (`show_all_movies_row`) | Hidden / Legacy | T/B/P; values/default in S043; source-only/hidden status must be recorded | PENDING |
| QA-S044 | All movie row sort order (`preferences_movie_sort_order`) | Hidden / Legacy | T/B/P; values/default in S044; source-only/hidden status must be recorded | PENDING |
| QA-S045 | Try new UI (`try_new_ui`) | Hidden / Legacy | T/B/P; values/default in S045; source-only/hidden status must be recorded | PENDING |
| QA-S046 | Show documentaries (`show_documentaries`) | Hidden / Legacy | T/B/P; values/default in S046; source-only/hidden status must be recorded | PENDING |
| QA-S047 | Show all TV show row (`show_all_tv_shows_row`) | Hidden / Legacy | T/B/P; values/default in S047; source-only/hidden status must be recorded | PENDING |
| QA-S048 | All TV show row sort order (`preferences_tv_show_sort_order`) | Hidden / Legacy | T/B/P; values/default in S048; source-only/hidden status must be recorded | PENDING |
| QA-S049 | Show all animations row (`show_all_animes_row`) | Hidden / Legacy | T/B/P; values/default in S049; source-only/hidden status must be recorded | PENDING |
| QA-S050 | All animations row sort order (`preferences_animes_sort_order`) | Hidden / Legacy | T/B/P; values/default in S050; source-only/hidden status must be recorded | PENDING |
| QA-S051 | OpenSubtitles credentials (`subtitles_credentials`) | Integrations / OpenSubtitles | T/B/P; values/default in S051; source-only/hidden status must be recorded | PENDING |
| QA-S052 | Hide subtitles by default (`subtitles_hide_default`) | Subtitles | T/B/P; values/default in S052; source-only/hidden status must be recorded | PENDING |
| QA-S053 | Subtitles reading language (`favSubLang`) | Subtitles | T/B/P; values/default in S053; source-only/hidden status must be recorded | PENDING |
| QA-S054 | Subtitles download languages (`languages_list`) | Subtitles | T/B/P; values/default in S054; source-only/hidden status must be recorded | PENDING |
| QA-S055 | Subtitles encoding (`codepage`) | Advanced / Subtitle Compatibility | T/B/P; values/default in S055; source-only/hidden status must be recorded | PENDING |
| QA-S056 | Upgrade to ad-free version (`trakt_getfull`) | Integrations / Trakt | T/B/P; values/default in S056; source-only/hidden status must be recorded | PENDING |
| QA-S057 | Sign In (`trakt_signin`) | Integrations / Trakt | T/B/P; values/default in S057; source-only/hidden status must be recorded | PENDING |
| QA-S058 | Live scrobbler (`trakt_live_scrobbling`) | Integrations / Trakt | T/B/P; values/default in S058; source-only/hidden status must be recorded | PENDING |
| QA-S059 | Sync trakt resume position (`trakt_sync_resume`) | Integrations / Trakt | T/B/P; values/default in S059; source-only/hidden status must be recorded | PENDING |
| QA-S060 | Force re-upload to Trakt (`trakt_force_push`) | Integrations / Trakt | T/B/P; values/default in S060; source-only/hidden status must be recorded | PENDING |
| QA-S061 | Force re-import from Trakt (`trakt_force_pull`) | Integrations / Trakt | T/B/P; values/default in S061; source-only/hidden status must be recorded | PENDING |
| QA-S062 | Sign out of Trakt.tv (`trakt_wipe`) | Integrations / Trakt | T/B/P; values/default in S062; source-only/hidden status must be recorded | PENDING |
| QA-S063 | Use alternate SMB implementation (SMB2+) (`pref_smbj`) | Advanced / Network Compatibility | T/B/P; values/default in S063; source-only/hidden status must be recorded | PENDING |
| QA-S064 | Enable SMBv2 support (`pref_smbv2`) | Advanced / Network Compatibility | T/B/P; values/default in S064; source-only/hidden status must be recorded | PENDING |
| QA-S065 | SMB resolver order (`pref_smb_resolv`) | Advanced / Network Compatibility | T/B/P; values/default in S065; source-only/hidden status must be recorded | PENDING |
| QA-S066 | Disable SMB TCP discovery (`pref_smb_disable_tcp_discovery`) | Advanced / Network Compatibility | T/B/P; values/default in S066; source-only/hidden status must be recorded | PENDING |
| QA-S067 | Disable SMB UDP discovery (`pref_smb_disable_udp_discovery`) | Advanced / Network Compatibility | T/B/P; values/default in S067; source-only/hidden status must be recorded | PENDING |
| QA-S068 | Disable SMB mDNS discovery (`pref_smb_disable_mdns_discovery`) | Advanced / Network Compatibility | T/B/P; values/default in S068; source-only/hidden status must be recorded | PENDING |
| QA-S069 | Use newer SFTP implementation (`pref_sshj`) | Advanced / Network Compatibility | T/B/P; values/default in S069; source-only/hidden status must be recorded | PENDING |
| QA-S070 | @string/samba_settings_title [dependency resource] (`share_folders`) | Network | T/B/P; values/default in S070; source-only/hidden status must be recorded | PENDING |
| QA-S071 | Create remote thumbnails (`pref_create_remote_thumbs`) | Library & Metadata | T/B/P; values/default in S071; source-only/hidden status must be recorded | PENDING |
| QA-S072 | Resume points and position bookmarks on network (`network_bookmarks`) | Playback | T/B/P; values/default in S072; source-only/hidden status must be recorded | PENDING |
| QA-S073 | Prefer VPN network (`pref_network_prefer_vpn`) | Network | T/B/P; values/default in S073; source-only/hidden status must be recorded | PENDING |
| QA-S074 | Enable network share on mobile connection (`vpn_mobile`) | Network | T/B/P; values/default in S074; source-only/hidden status must be recorded | PENDING |
| QA-S075 | Automatically get info for new videos (`enable_auto_scrap_key`) | Library & Metadata | T/B/P; values/default in S075; source-only/hidden status must be recorded | PENDING |
| QA-S076 | Video description language (`favScraperLang`) | Library & Metadata | T/B/P; values/default in S076; source-only/hidden status must be recorded | PENDING |
| QA-S077 | Process \'.nfo\' files (`network_nfo_parse`) | Library & Metadata | T/B/P; values/default in S077; source-only/hidden status must be recorded | PENDING |
| QA-S078 | Use already scraped Information (`scrape_from_database_key`) | Library & Metadata | T/B/P; values/default in S078; source-only/hidden status must be recorded | PENDING |
| QA-S079 | Auto-export after search (`nfo_export_auto`) | Library & Metadata | T/B/P; values/default in S079; source-only/hidden status must be recorded | PENDING |
| QA-S080 | Rescrape all (`rescrap_all_prefkey`) | Library & Metadata | T/B/P; values/default in S080; source-only/hidden status must be recorded | PENDING |
| QA-S081 | Rescrape all movies (`rescrap_all_movies_prefkey`) | Library & Metadata | T/B/P; values/default in S081; source-only/hidden status must be recorded | PENDING |
| QA-S082 | Rescrape all collections (`rescrap_all_collections_prefkey`) | Library & Metadata | T/B/P; values/default in S082; source-only/hidden status must be recorded | PENDING |
| QA-S083 | Recreate sort titles (`recreate_sort_titles_prefkey`) | Library & Metadata | T/B/P; values/default in S083; source-only/hidden status must be recorded | PENDING |
| QA-S084 | Export already scraped videos (`export_manual_prefkey`) | Library & Metadata | T/B/P; values/default in S084; source-only/hidden status must be recorded | PENDING |
| QA-S085 | Export media database (`db_export_manual_prefkey`) | Library & Metadata | T/B/P; values/default in S085; source-only/hidden status must be recorded | PENDING |
| QA-S086 | Export media library + credentials + shortcuts (`media_library_export_prefkey`) | Advanced / Backup & Restore | T/B/P; values/default in S086; source-only/hidden status must be recorded | PENDING |
| QA-S087 | Import media library + credentials + shortcuts (`media_library_import_prefkey`) | Advanced / Backup & Restore | T/B/P; values/default in S087; source-only/hidden status must be recorded | PENDING |
| QA-S088 | Adult video scraping (`enable_adult_scrap_key`) | Library & Metadata | T/B/P; values/default in S088; source-only/hidden status must be recorded | PENDING |
| QA-S089 | Full Library Scan (runtime; hidden) (`rescan_storage`) | Library & Metadata / Hidden | T/B/P; values/default in S089; source-only/hidden status must be recorded | PENDING |
| QA-S090 | Display all files in browser (`preference_display_all_files`) | Library & Metadata | T/B/P; values/default in S090; source-only/hidden status must be recorded | PENDING |
| QA-S091 | Enable Nova sponsor link (`enable_sponsor`) | About | T/B/P; values/default in S091; source-only/hidden status must be recorded | PENDING |
| QA-S092 | Subtitles provided by (`preferences_video_os`) | About | T/B/P; values/default in S092; source-only/hidden status must be recorded | PENDING |
| QA-S093 | Movie/TV Show information provided by (`preferences_video_tmdb`) | About | T/B/P; values/default in S093; source-only/hidden status must be recorded | PENDING |
| QA-S094 | Video scrobbler provided by (`preferences_video_trakt`) | About | T/B/P; values/default in S094; source-only/hidden status must be recorded | PENDING |
| QA-S095 | Licenses (`preferences_video_licences`) | About | T/B/P; values/default in S095; source-only/hidden status must be recorded | PENDING |
| QA-S096 | Regular preferences (`preferences_video_advanced_quit`) | Advanced / Legacy | T/B/P; values/default in S096; source-only/hidden status must be recorded | PENDING |
| QA-S097 | Make remaining time negative in player (`make_time_negative`) | Hidden / Legacy | T/B/P; values/default in S097; source-only/hidden status must be recorded | PENDING |
| QA-S098 | Hide trailer row in movies (`hide_trailer_row`) | Hidden / Legacy | T/B/P; values/default in S098; source-only/hidden status must be recorded | PENDING |
| QA-S099 | Show movies/tv shows by rating (`show_by_rating`) | Hidden / Legacy | T/B/P; values/default in S099; source-only/hidden status must be recorded | PENDING |
| QA-S100 | Remember library view preferences (`remember_library_views`) | Library & Metadata | T/B/P; values/default in S100; source-only/hidden status must be recorded | PENDING |
| QA-S101 | Scan network library on return (`auto_rescan_on_app_restart`) | Library & Metadata / Hidden (Network & Files owns scheduling) | T/B/P; values/default in S101; source-only/hidden status must be recorded | PENDING |
| QA-S102 | Prepare Subtitle Searches (`supernova_subtitle_discovery`) | Subtitles | T/B/P; values/default in S102; source-only/hidden status must be recorded | PENDING |
| QA-S103 | Prepare Current Season (`supernova_subtitle_season_prefetch`) | Subtitles | T/B/P; values/default in S103; source-only/hidden status must be recorded | PENDING |
| QA-S104 | Track Watch History (`supernova_track_viewing_history`) | Library & Metadata | T/B/P; values/default in S104; source-only/hidden status must be recorded | PENDING |
| QA-S105 | Remember Playback Position (`supernova_save_resume`) | Library & Metadata | T/B/P; values/default in S105; source-only/hidden status must be recorded | PENDING |
| QA-S106 | Include Watch History in Backups (`supernova_backup_history`) | Advanced / Backup & Restore (model) | T/B/P; values/default in S106; source-only/hidden status must be recorded | PENDING |
| QA-S107 | Include Playback Positions in Backups (`supernova_backup_resume`) | Advanced / Backup & Restore (model) | T/B/P; values/default in S107; source-only/hidden status must be recorded | PENDING |
| QA-S108 | Trakt (`supernova_integration_trakt`) | Integrations | T/B/P; values/default in S108; source-only/hidden status must be recorded | PENDING |
| QA-S109 | OpenSubtitles (`supernova_integration_opensubtitles`) | Integrations | T/B/P; values/default in S109; source-only/hidden status must be recorded | PENDING |
| QA-S110 | IntroDB (`supernova_segment_enabled`) | Integrations | T/B/P; values/default in S110; source-only/hidden status must be recorded | PENDING |
| QA-S111 | Featured: Recently Added (`preview_featured_recent`) | Home | T/B/P; values/default in S111; source-only/hidden status must be recorded | PENDING |
| QA-S112 | Featured: Trakt Trending (`preview_featured_trending`) | Home | T/B/P; values/default in S112; source-only/hidden status must be recorded | PENDING |
| QA-S113 | Featured: Trakt Popular (`preview_featured_popular`) | Home | T/B/P; values/default in S113; source-only/hidden status must be recorded | PENDING |
| QA-S114 | Home rows (`preview_home_editor`) | Home | T/B/P; values/default in S114; source-only/hidden status must be recorded | PENDING |
| QA-S115 | Clear Watch Next (`preview_clear_watch_next`) | Home | T/B/P; values/default in S115; source-only/hidden status must be recorded | PENDING |
| QA-S116 | Accent & Colour (`preview_accent_picker`) | Appearance | T/B/P; values/default in S116; source-only/hidden status must be recorded | PENDING |
| QA-S117 | Clear Viewing History (`supernova_clear_history`) | Library & Metadata | T/B/P; values/default in S117; source-only/hidden status must be recorded | PENDING |
| QA-S118 | SFTP server identities (`preview_sftp_trust`) | Network | T/B/P; values/default in S118; source-only/hidden status must be recorded | PENDING |
| QA-S119 | Issues Digest (`preview_diagnostic_Issues Digest`) | Advanced / Diagnostics | T/B/P; values/default in S119; source-only/hidden status must be recorded | PENDING |
| QA-S120 | Report a Problem (`preview_diagnostic_Report a Problem`) | Advanced / Diagnostics | T/B/P; values/default in S120; source-only/hidden status must be recorded | PENDING |
| QA-S121 | Show Latest Reference (`preview_diagnostic_Show Latest Reference`) | Advanced / Diagnostics | T/B/P; values/default in S121; source-only/hidden status must be recorded | PENDING |
| QA-S122 | Export Diagnostic Report (`export_diagnostic_report`) | Advanced / Diagnostics | T/B/P; values/default in S122; source-only/hidden status must be recorded | PENDING |
| QA-S123 | Diagnostic Logging (`supernova_diagnostic_logging`) | Advanced / Diagnostics | T/B/P; values/default in S123; source-only/hidden status must be recorded | PENDING |
| QA-S124 | Diagnostic Level (`supernova_diagnostic_level`) | Advanced / Diagnostics | T/B/P; values/default in S124; source-only/hidden status must be recorded | PENDING |
| QA-S125 | Subtitles by Default (`preview_subtitles_by_default`) | Subtitles | T/B/P; values/default in S125; source-only/hidden status must be recorded | PENDING |
| QA-S126 | Automatic network scan interval (`preview_scan_interval_control`) | Library & Metadata / Hidden | T/B/P; values/default in S126; source-only/hidden status must be recorded | PENDING |
| QA-S127 | Intro (`supernova_segment_intro`) | Integrations / IntroDB | T/B/P; values/default in S127; source-only/hidden status must be recorded | PENDING |
| QA-S128 | Recap (`supernova_segment_recap`) | Integrations / IntroDB | T/B/P; values/default in S128; source-only/hidden status must be recorded | PENDING |
| QA-S129 | Preview (`supernova_segment_preview`) | Integrations / IntroDB | T/B/P; values/default in S129; source-only/hidden status must be recorded | PENDING |
| QA-S130 | Outro (`supernova_segment_outro`) | Integrations / IntroDB | T/B/P; values/default in S130; source-only/hidden status must be recorded | PENDING |
| QA-S131 | Credits (`supernova_segment_credits`) | Integrations / IntroDB | T/B/P; values/default in S131; source-only/hidden status must be recorded | PENDING |
| QA-S132 | Show streaming options (`streaming_enabled`) | Streaming | T/B/P; values/default in S132; source-only/hidden status must be recorded | PENDING |
| QA-S133 | Country (`streaming_country`) | Streaming | T/B/P; values/default in S133; source-only/hidden status must be recorded | PENDING |
| QA-S134 | Search providers (`streaming_provider_search`) | Streaming | T/B/P; values/default in S134; source-only/hidden status must be recorded | PENDING |
| QA-S135 | Show all providers (`streaming_show_all`) | Streaming | T/B/P; values/default in S135; source-only/hidden status must be recorded | PENDING |
| QA-S136 | My Providers (`streaming_providers_{country}`) | Streaming | T/B/P; values/default in S136; source-only/hidden status must be recorded | PENDING |
| QA-S137 | Preferred provider (`streaming_preferred_{country}`) | Streaming | T/B/P; values/default in S137; source-only/hidden status must be recorded | PENDING |
| QA-S138 | Refresh provider list (`streaming_provider_status`) | Streaming | T/B/P; values/default in S138; source-only/hidden status must be recorded | PENDING |
| QA-S139 | JustWatch (`(JustWatch)`) | Streaming | T/B/P; values/default in S139; source-only/hidden status must be recorded | PENDING |
| QA-S140 | Appearance settings (`supernova_backup_include_appearance`) | Advanced / Backup & Restore / Export categories | T/B/P; values/default in S140; source-only/hidden status must be recorded | PENDING |
| QA-S141 | Audio settings (`supernova_backup_include_audio`) | Advanced / Backup & Restore / Export categories | T/B/P; values/default in S141; source-only/hidden status must be recorded | PENDING |
| QA-S142 | Diagnostic preferences (`supernova_backup_include_diagnostics`) | Advanced / Backup & Restore / Export categories | T/B/P; values/default in S142; source-only/hidden status must be recorded | PENDING |
| QA-S143 | Service settings (`supernova_backup_include_integrations`) | Advanced / Backup & Restore / Export categories | T/B/P; values/default in S143; source-only/hidden status must be recorded | PENDING |
| QA-S144 | Library settings (`supernova_backup_include_library`) | Advanced / Backup & Restore / Export categories | T/B/P; values/default in S144; source-only/hidden status must be recorded | PENDING |
| QA-S145 | Network settings (`supernova_backup_include_network`) | Advanced / Backup & Restore / Export categories | T/B/P; values/default in S145; source-only/hidden status must be recorded | PENDING |
| QA-S146 | Playback settings (`supernova_backup_include_playback`) | Advanced / Backup & Restore / Export categories | T/B/P; values/default in S146; source-only/hidden status must be recorded | PENDING |
| QA-S147 | Streaming settings (`supernova_backup_include_streaming`) | Advanced / Backup & Restore / Export categories | T/B/P; values/default in S147; source-only/hidden status must be recorded | PENDING |
| QA-S148 | Subtitle settings (`supernova_backup_include_subtitles`) | Advanced / Backup & Restore / Export categories | T/B/P; values/default in S148; source-only/hidden status must be recorded | PENDING |
| QA-S149 | Video settings (`supernova_backup_include_video`) | Advanced / Backup & Restore / Export categories | T/B/P; values/default in S149; source-only/hidden status must be recorded | PENDING |
| QA-S150 | Home rows (`supernova_backup_include_home`) | Advanced / Backup & Restore / Export categories | T/B/P; values/default in S150; source-only/hidden status must be recorded | PENDING |
| QA-S151 | Library pages (`supernova_backup_include_custom_pages`) | Advanced / Backup & Restore / Export categories | T/B/P; values/default in S151; source-only/hidden status must be recorded | PENDING |
| QA-S152 | Hidden titles (`supernova_backup_include_hidden`) | Advanced / Backup & Restore / Export categories | T/B/P; values/default in S152; source-only/hidden status must be recorded | PENDING |
| QA-S153 | Media matches (`supernova_backup_include_matches`) | Advanced / Backup & Restore / Export categories | T/B/P; values/default in S153; source-only/hidden status must be recorded | PENDING |
| QA-S154 | Pinned titles (`supernova_backup_include_pinned`) | Advanced / Backup & Restore / Export categories | T/B/P; values/default in S154; source-only/hidden status must be recorded | PENDING |
| QA-S155 | Preferred versions (`supernova_backup_include_preferred`) | Advanced / Backup & Restore / Export categories | T/B/P; values/default in S155; source-only/hidden status must be recorded | PENDING |
| QA-S156 | Review notes (`supernova_backup_include_review`) | Advanced / Backup & Restore / Export categories | T/B/P; values/default in S156; source-only/hidden status must be recorded | PENDING |
| QA-S157 | Saved playlists (`supernova_backup_include_playlists`) | Advanced / Backup & Restore / Export categories | T/B/P; values/default in S157; source-only/hidden status must be recorded | PENDING |
| QA-S158 | Watch Next (`supernova_backup_include_watch_next`) | Advanced / Backup & Restore / Export categories | T/B/P; values/default in S158; source-only/hidden status must be recorded | PENDING |
| QA-S159 | Continue Watching (`supernova_backup_include_continue_choices`) | Advanced / Backup & Restore / Export categories | T/B/P; values/default in S159; source-only/hidden status must be recorded | PENDING |
| QA-S160 | Playback positions (`supernova_backup_include_positions`) | Advanced / Backup & Restore / Export categories | T/B/P; values/default in S160; source-only/hidden status must be recorded | PENDING |
| QA-S161 | Title playback (`supernova_backup_include_title_playback`) | Advanced / Backup & Restore / Export categories | T/B/P; values/default in S161; source-only/hidden status must be recorded | PENDING |
| QA-S162 | Watch history (`supernova_backup_include_history`) | Advanced / Backup & Restore / Export categories | T/B/P; values/default in S162; source-only/hidden status must be recorded | PENDING |
| QA-S163 | File bookmarks (`supernova_backup_include_bookmarks`) | Advanced / Backup & Restore / Export categories | T/B/P; values/default in S163; source-only/hidden status must be recorded | PENDING |
| QA-S164 | Library sources (`supernova_backup_include_sources`) | Advanced / Backup & Restore / Export categories | T/B/P; values/default in S164; source-only/hidden status must be recorded | PENDING |
| QA-S165 | Filenames and locations (`supernova_backup_include_locations`) | Advanced / Backup & Restore / Export categories | T/B/P; values/default in S165; source-only/hidden status must be recorded | PENDING |

## Every navigation-state template

| Case | State | Exercise | Result |
| --- | --- | --- | --- |
| QA-N01 | Entry | Enter,Up/Down/Left/Right/OK/Back;first/last/long/empty as applicable; leave/reenter; Left category list; focus previews, OK/Right enters; top navigation retained | PENDING |
| QA-N02 | Playback | Enter,Up/Down/Left/Right/OK/Back;first/last/long/empty as applicable; leave/reenter; Up/Down rows, Left rail, Right information/value panel | PENDING |
| QA-N03 | Video | Enter,Up/Down/Left/Right/OK/Back;first/last/long/empty as applicable; leave/reenter; Hardware-conditioned rows; selection in right panel | PENDING |
| QA-N04 | Audio | Enter,Up/Down/Left/Right/OK/Back;first/last/long/empty as applicable; leave/reenter; Decoder/output dependencies; disabled rows skipped | PENDING |
| QA-N05 | Subtitles | Enter,Up/Down/Left/Right/OK/Back;first/last/long/empty as applicable; leave/reenter; Positive inverse default proxy, reading/download language selectors | PENDING |
| QA-N06 | Library & Metadata | Enter,Up/Down/Left/Right/OK/Back;first/last/long/empty as applicable; leave/reenter; Long service-action list; confirmations and running jobs | PENDING |
| QA-N07 | Home | Enter,Up/Down/Left/Right/OK/Back;first/last/long/empty as applicable; leave/reenter; Featured switches, editor, queue clear | PENDING |
| QA-N08 | Appearance | Enter,Up/Down/Left/Right/OK/Back;first/last/long/empty as applicable; leave/reenter; Language restart/accent recreation | PENDING |
| QA-N09 | Streaming | Enter,Up/Down/Left/Right/OK/Back;first/last/long/empty as applicable; leave/reenter; Country/query/filter/provider/preferred/refresh/attribution | PENDING |
| QA-N10 | Network | Enter,Up/Down/Left/Right/OK/Back;first/last/long/empty as applicable; leave/reenter; VPN and SFTP trust; transport compatibility under Advanced | PENDING |
| QA-N11 | Integrations landing | Enter,Up/Down/Left/Right/OK/Back;first/last/long/empty as applicable; leave/reenter; Right/OK expands children; explicit switch changes save | PENDING |
| QA-N12 | Advanced introduction | Enter,Up/Down/Left/Right/OK/Back;first/last/long/empty as applicable; leave/reenter; No diagnostics landing list; merged workspace; Right/OK expands only | PENDING |
| QA-N13 | Video Compatibility | Enter,Up/Down/Left/Right/OK/Back;first/last/long/empty as applicable; leave/reenter; Some rows require inherited unlock and device capability | PENDING |
| QA-N14 | Audio Compatibility | Enter,Up/Down/Left/Right/OK/Back;first/last/long/empty as applicable; leave/reenter; Output/decoder constraints | PENDING |
| QA-N15 | Network Compatibility | Enter,Up/Down/Left/Right/OK/Back;first/last/long/empty as applicable; leave/reenter; Backend/discovery/buffering constraints | PENDING |
| QA-N16 | Subtitle Compatibility | Enter,Up/Down/Left/Right/OK/Back;first/last/long/empty as applicable; leave/reenter; Encoding list | PENDING |
| QA-N17 | Diagnostics | Enter,Up/Down/Left/Right/OK/Back;first/last/long/empty as applicable; leave/reenter; Logging/level/digest/report/reference/export | PENDING |
| QA-N18 | Backup landing | Enter,Up/Down/Left/Right/OK/Back;first/last/long/empty as applicable; leave/reenter; Export/Restore,26 direct category toggles,details/instructions | PENDING |
| QA-N19 | Trakt | Enter,Up/Down/Left/Right/OK/Back;first/last/long/empty as applicable; leave/reenter; Sign-in/authenticated/disabled/sync status | PENDING |
| QA-N20 | OpenSubtitles | Enter,Up/Down/Left/Right/OK/Back;first/last/long/empty as applicable; leave/reenter; Draft credentials, reveal, saving/success/failure | PENDING |
| QA-N21 | IntroDB | Enter,Up/Down/Left/Right/OK/Back;first/last/long/empty as applicable; leave/reenter; Five segment policy selectors | PENDING |
| QA-N22 | List choices | Enter,Up/Down/Left/Right/OK/Back;first/last/long/empty as applicable; leave/reenter; Immediate save; rebuild and request selected value focus; Left centre | PENDING |
| QA-N23 | Multi choices | Enter,Up/Down/Left/Right/OK/Back;first/last/long/empty as applicable; leave/reenter; Immediate set persistence; rebuild path differs from modal | PENDING |
| QA-N24 | Provider choices | Enter,Up/Down/Left/Right/OK/Back;first/last/long/empty as applicable; leave/reenter; Selected scroll max140dp; Other flexible scroll; dynamic catalogue | PENDING |
| QA-N25 | Modal preference fallback | Enter,Up/Down/Left/Right/OK/Back;first/last/long/empty as applicable; leave/reenter; Done for multi/provider; in-place checks; not normal inline activation | PENDING |
| QA-N26 | Text keyboard | Enter,Up/Down/Left/Right/OK/Back;first/last/long/empty as applicable; leave/reenter; Remote keyboard + hardware input,Clear/Done/Cancel; validation before acceptance | PENDING |
| QA-N27 | Accent palette | Enter,Up/Down/Left/Right/OK/Back;first/last/long/empty as applicable; leave/reenter; Six preset colours and Custom | PENDING |
| QA-N28 | Custom RGB | Enter,Up/Down/Left/Right/OK/Back;first/last/long/empty as applicable; leave/reenter; Left/Right sliders; Apply recreates Settings | PENDING |
| QA-N29 | Home editor | Enter,Up/Down/Left/Right/OK/Back;first/last/long/empty as applicable; leave/reenter; OK move/place,Right fields,eye visibility,Back cancel move | PENDING |
| QA-N30 | Home genre picker | Enter,Up/Down/Left/Right/OK/Back;first/last/long/empty as applicable; leave/reenter; Any/All appears for multiple genres; restore editor anchor | PENDING |
| QA-N31 | Home confirmation | Enter,Up/Down/Left/Right/OK/Back;first/last/long/empty as applicable; leave/reenter; Safe initial focus; explicit destructive action | PENDING |
| QA-N32 | Clear queue/history | Enter,Up/Down/Left/Right/OK/Back;first/last/long/empty as applicable; leave/reenter; Queue and history actions have distinct retained state | PENDING |
| QA-N33 | SFTP identities | Enter,Up/Down/Left/Right/OK/Back;first/last/long/empty as applicable; leave/reenter; Host choice then Cancel/Reset confirmation | PENDING |
| QA-N34 | Diagnostic disable | Enter,Up/Down/Left/Right/OK/Back;first/last/long/empty as applicable; leave/reenter; Only final noncancel choice disables | PENDING |
| QA-N35 | Diagnostic digest/reference | Enter,Up/Down/Left/Right/OK/Back;first/last/long/empty as applicable; leave/reenter; Local data; return focus; no upload | PENDING |
| QA-N36 | Problem category | Enter,Up/Down/Left/Right/OK/Back;first/last/long/empty as applicable; leave/reenter; Local marker then reference reader; logging-off explanation | PENDING |
| QA-N37 | Diagnostic export | Enter,Up/Down/Left/Right/OK/Back;first/last/long/empty as applicable; leave/reenter; Prepared report/success/failure/cancel and Settings return | PENDING |
| QA-N38 | Backup information | Enter,Up/Down/Left/Right/OK/Back;first/last/long/empty as applicable; leave/reenter; Scroll,Back to landing without replacing state | PENDING |
| QA-N39 | Backup privacy | Enter,Up/Down/Left/Right/OK/Back;first/last/long/empty as applicable; leave/reenter; Safe choice or explicit consent; no restore yet | PENDING |
| QA-N40 | Backup busy | Enter,Up/Down/Left/Right/OK/Back;first/last/long/empty as applicable; leave/reenter; Cancel where supported; held focus; owned modal | PENDING |
| QA-N41 | Backup destination | Enter,Up/Down/Left/Right/OK/Back;first/last/long/empty as applicable; leave/reenter; Success/cancel/unavailable/retry retained staging | PENDING |
| QA-N42 | Backup source | Enter,Up/Down/Left/Right/OK/Back;first/last/long/empty as applicable; leave/reenter; Cancellation changes no settings; chosen archive inspection | PENDING |
| QA-N43 | Backup review | Enter,Up/Down/Left/Right/OK/Back;first/last/long/empty as applicable; leave/reenter; Next preview/Cancel; only supported categories | PENDING |
| QA-N44 | Backup confirmation | Enter,Up/Down/Left/Right/OK/Back;first/last/long/empty as applicable; leave/reenter; Cancel or explicit restore; receiving-device warnings | PENDING |
| QA-N45 | Backup result | Enter,Up/Down/Left/Right/OK/Back;first/last/long/empty as applicable; leave/reenter; Back; Save verified backup again only if permitted | PENDING |
| QA-N46 | About app information | Enter,Up/Down/Left/Right/OK/Back;first/last/long/empty as applicable; leave/reenter; App summary; child links initially collapsed | PENDING |
| QA-N47 | About Release Notes | Enter,Up/Down/Left/Right/OK/Back;first/last/long/empty as applicable; leave/reenter; Current expanded; independent older expansion | PENDING |
| QA-N48 | About Open-source Licences | Enter,Up/Down/Left/Right/OK/Back;first/last/long/empty as applicable; leave/reenter; Pending entry remembered; component selection/details scrolling | PENDING |
| QA-N49 | About Credits | Enter,Up/Down/Left/Right/OK/Back;first/last/long/empty as applicable; leave/reenter; No provider enable actions | PENDING |
| QA-N50 | About Technical | Enter,Up/Down/Left/Right/OK/Back;first/last/long/empty as applicable; leave/reenter; Read-only runtime facts with unavailable fallbacks | PENDING |
| QA-N51 | Classic More options | Enter,Up/Down/Left/Right/OK/Back;first/last/long/empty as applicable; leave/reenter; Remaining time/trailer/rating legacy controls; organised foundation does not expose as ordinary rail child | PENDING |
| QA-N52 | Trakt authentication/sign-out | Enter,Up/Down/Left/Right/OK/Back;first/last/long/empty as applicable; leave/reenter; Account state refresh and network/denial recovery | PENDING |

## Risk and integration scenarios

| Case | Scenario | Required checks | Result |
| --- | --- | --- | --- |
| QA-R01 | Diagnostics regression | Fresh launch focus Playback rail; Advanced hover intro only; OK/Right expands without jumping; Diagnostics explicitly selectable; Up from About enters Diagnostics; visit logging,level,digest,problem,latest,export; picker Cancel/success returns Diagnostics/appropriate opener. | PENDING |
| QA-R02 | Back hierarchy | Test middle→child→parent→global nav→exit using Back and Left separately; expanded Advanced/Integrations/About children; log any top-nav jump before parent restoration. | PENDING |
| QA-R03 | Held and rapid keys | Hold Up/Down,reverse direction during expansion/collapse/adapter updates; first/last directions terminal; no focus teleport or unintended click. | PENDING |
| QA-R04 | Provider stability F01 | Select/deselect first,middle,last,only selected provider; observe exact row/check/order/focus before/after; empty groups; repeated toggles; compare normal inline with fallback modal Done. | PENDING |
| QA-R05 | Streaming async F04 | Focus Refresh before request; slow success,empty response,error,offline cached data; leave region while loading; verify visible status/right text and current region choices; close Settings during load. | PENDING |
| QA-R06 | Subtitles proxy F02 | Begin Hide=true then positive On,and inverse; verify favSubLang availability immediately and after reopen; forced matching subtitles still follow separate policy. | PENDING |
| QA-R07 | Stale context F03 | Toggle native switch without moving focus; compare right Current value to actual stored state; switch Off/On/restart. | PENDING |
| QA-R08 | Advanced legacy V02 | Disposable profile only: rapid >8 Force software decoding activations within timing window; inspect crash/hierarchy; repeat with saved advanced state; preserve actual toggled decoding value. | PENDING |
| QA-R09 | Numeric input V05 | Isolated fixture only: empty,space,non-digits,negative,decimal,0,leading zeros,huge number,512-char value,paste; cancel first; inspect prepared consumer bounds before launching playback with malformed saved data. | PENDING |
| QA-R10 | Integration recovery V04 | Expand with a service disabled; reenable without collapse; try child; collapse/reopen; credentials/policies unchanged; sign-out is distinct from disable. | PENDING |
| QA-R11 | Remote/keyboard | D-pad only password with punctuation and whitespace; hardware Enter/Select,Back/Escape,Tab/Shift-Tab,Backspace,repeat,Caps; empty and maximum length inputs; cancel preserves previous field. | PENDING |
| QA-R12 | Video display | Every available refresh/Dolby Vision mode,SDR/HDR10/DV samples; exit restores display; unsupported mode fallback; verify engine/output rather than checkbox. | PENDING |
| QA-R13 | Audio chain | PCM/passthrough each mode;receiver unplug/replug,Bluetooth,spatialization unavailable/available,TV downmix,FFmpeg/MediaCodec,speed/delay dependencies; verify speech channels/sync and next-playback application. | PENDING |
| QA-R14 | Library metadata | Toggle auto metadata; import local/network; no subtitle provider import searches; NFO parse/export writable/read-only; locale change then rescrape confirmation/cancel; denied storage/service errors. | PENDING |
| QA-R15 | History independence | Track Off/save positions On and converse; restart/resume; clear history on fixture retains positions; inclusion flags independent; Private Mode does not publish tracking/search. | PENDING |
| QA-R16 | Home editor | Each built-in reorder/hide; custom create/name40/empty/duplicate/long Unicode;delete cancel/confirm; all type/genre/match/sort/order/max choices; freeze automatic membership; new matching media; move Back restores old order. | PENDING |
| QA-R17 | Network sharing | Display all files Settings and toolbar same value across restart; backend switch requires reconnect; SMB discovery flags; VPN no VPN; verify schedule stays Network & Files; SFTP reset only disposable server identity. | PENDING |
| QA-R18 | Backup categories | Independently toggle all26; defaults from inventory;history/positions legacy mirrors;locations consent cancel/accept;cancel review preserves export defaults;sync commit failure feedback on controlled fixture. | PENDING |
| QA-R19 | Backup archive validation | Fixture truncated ZIP,corrupt hashes,unknown category,oversized content,traversal,unsupported format and empty selected set; inspect/review before mutation; selected empty category clears only owned data; absent/unselected unchanged. | PENDING |
| QA-R20 | Backup lifecycle | Export low space,picker absent/denied/cancel,readback mismatch,save retry,restore cancellation,process recreation at review/recovery; restore legacy copy only;unresolved media,permissions,accounts and paths reconnect separately. | PENDING |
| QA-R21 | Diagnostics privacy | Disable Cancel/Keep/Delete;level normal/qa;logging-off report;no latest reference;QR failure;digest empty;export low-space/picker unavailable;inspect safe ZIP fixture for secrets without posting raw logs. | PENDING |
| QA-R22 | About async | Rapid component changes,leave before load,Back during load,licence unavailable,very long notice paging,QR scannability,technical unavailable;all legal text remains locally readable offline. | PENDING |
| QA-R23 | Visual scale | At actual Shield scales/1080p/4K and any zoom option:all12 rail categories,expanded children,About bottom,long translated labels,fixed54dp rows,transparent readability,focus glow/border/selection and logo/flag aspect/colour. | PENDING |
| QA-R24 | Restart/recreation | Language and custom accent recreation,background/foreground,Android activity recreation,force stop/reboot;restore correct setting/focus where promised;late callbacks never steal deliberate focus. | PENDING |

Total planned cases: 241 (165 definition cases + 52 state cases + 24 risk scenarios). Auxiliary controls A01–A71 must be exercised within their mapped states; option rows are explicitly enumerated where values are finite. Service/device-dependent blocks must remain BLOCKED with reason until access/evidence exists.
