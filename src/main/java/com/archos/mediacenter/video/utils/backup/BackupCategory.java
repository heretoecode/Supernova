package com.archos.mediacenter.video.utils.backup;

import java.util.*;

/** One toggle per source-grounded ownership domain. Media, credentials, Put.io and Collections have none. */
public enum BackupCategory {
    APPEARANCE("Appearance settings","appearance",0,true,"Accent, language and saved display choices.","default preferences / PortableSettingsRegistry"),
    AUDIO("Audio settings","audio",0,true,"Languages, audio output and saved audio preferences.","default preferences / PortableSettingsRegistry"),
    DIAGNOSTICS("Diagnostic preferences","info",0,true,"Logging on/off choice only; no recordings or incidents.","Diagnostics.KEY"),
    INTEGRATIONS("Service settings","integration",0,true,"Trakt, OpenSubtitles and IntroDB policies; sign in separately.","default preferences / PortableSettingsRegistry"),
    LIBRARY("Library settings","library",0,true,"Saved filters, sorting, columns and metadata policies.","default preferences / PortableSettingsRegistry"),
    NETWORK("Network settings","network",0,true,"Transport and scan preferences; no logins, trust keys or sources.","default preferences / PortableSettingsRegistry"),
    PLAYBACK("Playback settings","play",0,true,"Playback, privacy and viewing-recording preferences.","default preferences / PortableSettingsRegistry"),
    STREAMING("Streaming settings","streaming",0,true,"Country and chosen provider IDs; no provider catalogue cache.","StreamingRepository preference intent"),
    SUBTITLES("Subtitle settings","subtitles",0,true,"Languages, styling and preparation choices; no subtitle files.","default preferences / PortableSettingsRegistry"),
    VIDEO("Video settings","video-file",0,true,"Decoder, refresh and format choices; hardware may differ.","default preferences / PortableSettingsRegistry"),
    HOME("Home rows","custom row",1,true,"Ordering, visibility, custom rules and logical membership.","preview_home_rows41 / HomeIntentCodec"),
    CUSTOM_PAGES("Library pages","library",1,true,"Your custom Library page definition and filters.","supernova_custom_library_page"),
    HIDDEN("Hidden titles","eye-off",2,true,"Deliberately hidden titles, using logical media identities.","DbUtils.markAsHiddenByUser / Archos_hiddenByUser"),
    MATCHES("Media matches","edit",2,true,"Saved provider identification; no scraped metadata or images.","VideoStore provider ID relationships"),
    PINNED("Pinned titles","pin",2,true,"Pinned movie/show state and ordering.","DbUtils.markAsPinned / Nova_pinned"),
    PREFERRED("Preferred versions","files",2,true,"Preferred physical versions; unresolved choices remain stored.","preview_version_choice:* / PreviewVariants"),
    REVIEW("Review notes","info",2,true,"User incorrect-match annotations; no automatic health cache.","supernova_library_health_v1 incorrect:*"),
    PLAYLISTS("Saved playlists","list",2,true,"Existing saved playlist definitions and logical membership.","ListDialog / list_table / video_list_table"),
    WATCH_NEXT("Watch Next","list",2,true,"Manually queued logical titles, independent of Home definitions.","preview_home_rows41 watchnext.members"),
    CONTINUE_CHOICES("Continue Watching","history",3,true,"Explicit dismissals and viewing choices; no invented watch counts.","preview_cw_dismiss:* / PreviewHomeRows.dismiss"),
    POSITIONS("Playback positions","history",3,true,"Unfinished playback offsets and duration, independently of history.","ViewingHistory.position/duration / legacy bookmarks"),
    TITLE_PLAYBACK("Title playback","audio",3,false,"Per-title track and delay choices; file variants may differ.","VideoDbInfo playback parameters"),
    HISTORY("Watch history","history",3,true,"What you watched and when. Excluding it loses restored watched state.","ViewingHistory flags/times / legacy watched markers"),
    BOOKMARKS("File bookmarks","bookmark",4,false,"Saved browser favourites. Recreating locations needs explicit opt-in.","shortcuts2_db browser favourites; indexed roots and FTP credentials excluded"),
    SOURCES("Library sources","folder",4,false,"Roots, exclusions and indexed-folder rescan choices. Reconnect access and permissions separately.","SupernovaLibraryPolicy roots/exclusions / ShortcutDbAdapter.VIDEO"),
    LOCATIONS("Filenames and locations","folder",5,false,"May reveal filenames, folders, hosts and shares across included categories.","Global physical-locator policy; never authentication" );

    public final String label,icon,description,source;
    public final int sensitivity;
    public final boolean defaultIncluded;
    BackupCategory(String label,String icon,int sensitivity,boolean included,String description,String source){this.label=label;this.icon=icon;this.sensitivity=sensitivity;this.defaultIncluded=included;this.description=description;this.source=source;}
    public PortableSettingsRegistry.Category settings(){try{return PortableSettingsRegistry.Category.valueOf(name());}catch(IllegalArgumentException logicalData){return null;}}
    public static List<BackupCategory> ordered(){List<BackupCategory> rows=new ArrayList<>(Arrays.asList(values()));rows.sort(Comparator.comparingInt((BackupCategory row)->row.sensitivity).thenComparing(row->row.label,String.CASE_INSENSITIVE_ORDER));return Collections.unmodifiableList(rows);}
    public String preference(){return "supernova_backup_include_"+name().toLowerCase(Locale.ROOT);}
}
