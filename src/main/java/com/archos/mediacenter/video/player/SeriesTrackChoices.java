package com.archos.mediacenter.video.player;

import android.content.Context;
import android.content.SharedPreferences;
import com.archos.mediacenter.video.utils.VideoMetadata;
import com.archos.mediacenter.video.utils.VideoUtils;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONObject;

/** Opt-in per-series choices. No file paths, per-file indexes or watched state are stored. */
public final class SeriesTrackChoices
{
    private final SharedPreferences prefs;

    public SeriesTrackChoices(Context context)
    {
        prefs = context.getSharedPreferences("series-track-choices", Context.MODE_PRIVATE);
    }

    private static String key(String series)
    {
        if (series == null || !series.matches("[1-9][0-9]{0,17}"))
        {
            throw new IllegalArgumentException("TMDB series identity required");
        }
        return "tmdb:tv:" + series;
    }

    public boolean enabled(String series)
    {
        return prefs.getBoolean(key(series) + ":enabled", false);
    }

    public void setEnabled(String series, boolean enabled)
    {
        prefs.edit().putBoolean(key(series) + ":enabled", enabled)
                .putBoolean(key(series) + ":audio_enabled", enabled)
                .putBoolean(key(series) + ":subtitle_enabled", enabled).apply();
    }

    public boolean enabled(String series, boolean audio)
    {
        return prefs.getBoolean(key(series) + (audio ? ":audio_enabled" : ":subtitle_enabled"), enabled(series));
    }

    public void setEnabled(String series, boolean audio, boolean enabled)
    {
        prefs.edit().putBoolean(key(series) + (audio ? ":audio_enabled" : ":subtitle_enabled"), enabled).apply();
    }

    public void remember(String series, boolean audio, TrackPreference track)
    {
        if (!enabled(series, audio) || track == null)
            return;
        try
        {
            JSONObject data = new JSONObject()
                                  .put("language", track.language)
                                  .put("codec", track.codec)
                                  .put("channels", track.channels)
                                  .put("forced", track.forced)
                                  .put("none", track.none);
            prefs.edit().putString(key(series) + (audio ? ":audio" : ":subtitle"), data.toString()).apply();
        }
        catch (org.json.JSONException invalid)
        {
            throw new IllegalArgumentException("Invalid track description", invalid);
        }
    }

    public TrackPreference choice(String series, boolean audio)
    {
        if (!enabled(series, audio))
            return null;
        try
        {
            JSONObject data = new JSONObject(prefs.getString(key(series) + (audio ? ":audio" : ":subtitle"), ""));
            return new TrackPreference(data.getString("language"), data.getString("codec"), data.getString("channels"),
                                       data.getBoolean("forced"), data.getBoolean("none"));
        }
        catch (Exception unavailable)
        {
            return null;
        }
    }

    public int matchingTrack(String series, boolean audio, VideoMetadata metadata)
    {
        TrackPreference saved = choice(series, audio);
        if (saved == null)
            return -1;
        int count = audio ? metadata.getAudioTrackNb() : metadata.getSubtitleTrackNb();
        if (saved.none)
            return audio ? -1 : count;
        List<TrackPreference> candidates = new ArrayList<>();
        for (int i = 0; i < count; i++)
        {
            candidates.add(describe(metadata, audio, i));
        }
        return saved.bestMatch(candidates);
    }

    public static TrackPreference describe(VideoMetadata metadata, boolean audio, int index)
    {
        if (audio)
        {
            VideoMetadata.AudioTrack track = metadata.getAudioTrack(index);
            return track == null || !track.supported
                ? null
                : new TrackPreference(track.language, track.format, track.channels,
                                      (track.disposition & VideoUtils.AV_DISPOSITION_FORCED) != 0, false);
        }
        if (index < 0 || index >= metadata.getSubtitleTrackNb())
        {
            return new TrackPreference("", "", "", false, true);
        }
        VideoMetadata.SubtitleTrack track = metadata.getSubtitleTrack(index);
        return track == null ? null
                             : new TrackPreference(track.language, String.valueOf(track.format), "",
                                                   (track.disposition & VideoUtils.AV_DISPOSITION_FORCED) != 0, false);
    }
}
