package com.archos.mediacenter.video.player;

import com.archos.mediacenter.video.utils.VideoMetadata;
import java.util.Collections;
import java.util.List;

/** Feeds the Chapters · Episodes strip from the running player, using the engine hooks from Codex's Part B. */
final class PlayerStripSource implements PlayerPartsStrip.Source
{
    private final PlayerActivity activity;

    PlayerStripSource(PlayerActivity activity)
    {
        this.activity = activity;
    }

    @Override
    public PlaybackParts parts()
    {
        Player player = Player.sPlayer;
        if (player == null || player.getDuration() <= 0)
            return null;
        VideoMetadata metadata = player.getVideoMetadata();
        List<PlaybackChapters.Chapter> embedded =
            metadata == null ? Collections.emptyList() : metadata.getChapters();
        return activity.previewParts(embedded, player.getDuration());
    }

    @Override
    public List<PlaybackEpisodes.Item> episodes()
    {
        PlayerService service = PlayerService.sPlayerService;
        return service == null ? Collections.emptyList() : service.previewSeasonEpisodes();
    }

    @Override
    public long positionMs()
    {
        Player player = Player.sPlayer;
        return player == null ? 0 : player.getCurrentPosition();
    }

    @Override
    public long currentEpisodeId()
    {
        PlayerService service = PlayerService.sPlayerService;
        if (service == null || service.getVideoInfo() == null)
            return -1;
        return service.getVideoInfo().id;
    }

    @Override
    public PartPictures.FrameSource frames()
    {
        PlayerService service = PlayerService.sPlayerService;
        return service == null ? null : service.seekThumbnails();
    }

    @Override
    public void seekTo(long positionMs)
    {
        Player player = Player.sPlayer;
        if (player != null)
            player.seekTo((int)Math.min(Integer.MAX_VALUE, positionMs));
    }

    @Override
    public void playEpisode(PlaybackEpisodes.Item item)
    {
        PlayerService service = PlayerService.sPlayerService;
        if (service != null && item != null)
            service.previewPlayEpisode(item.id, item.uri);
    }
}
