package com.archos.mediacenter.video.info;

/** Stable metadata handoff keys shared by the player and TV Details host. */
public final class VideoInfoContract {
    private VideoInfoContract() {}
    public static final String SHARED_ELEMENT_NAME = "poster";
    public static final int MAX_VIDEO = 200;
    public static final String EXTRA_NO_ONLINE_UPDATE = "no_online_updates";
    public static final String EXTRA_VIDEO = "VIDEO";
    public static final String EXTRA_VIDEO_ID = "video_id";
    public static final String EXTRA_PLAYLIST_ID = "playlist_id";
    public static final String EXTRA_LAUNCHED_FROM_PLAYER = "launchedFromPlayer";
    public static final String EXTRA_VIDEO_PATHS = "video_paths";
    public static final String EXTRA_VIDEO_PATH = "video_path";
    public static final String EXTRA_CURRENT_POSITION = "current_position";
    public static final String EXTRA_USE_VIDEO_METADATA = "useVideoMetadata";
    public static final String EXTRA_PLAYER_TYPE = "playerType";
    public static final String EXTRA_FORCE_VIDEO_SELECTION = "force_video_selection";
}
