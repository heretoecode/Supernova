package com.archos.mediacenter.video.player;

import android.app.Activity;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.Checkable;
import androidx.core.text.HtmlCompat;
import androidx.preference.PreferenceManager;
import com.archos.mediacenter.video.R;
import com.archos.mediacenter.video.player.tvmenu.*;
import java.util.*;

/**
 * Presentation adapter for the Playback Settings side panel. Every track, toggle and mode change is
 * the existing native menu action ({@link TVMenuItem#previewClick()}) or an existing PlayerActivity
 * call; this class only reads their state and invokes them. Cards are looked up live on every call,
 * because the player rebuilds its menus when tracks change.
 */
final class SubtitlePlaybackBackend implements PlaybackSubtitlePanel.Backend {
    private final Activity owner;
    private final TVMenuAdapter adapter;
    private int lastTrack = 1;

    SubtitlePlaybackBackend(Activity owner, TVMenuAdapter adapter) {
        this.owner = owner;
        this.adapter = adapter;
    }

    private PlayerActivity player() {
        return owner instanceof PlayerActivity ? (PlayerActivity)owner : null;
    }

    private SubtitleManager manager() {
        return player() == null ? null : player().subtitlePresentationManager();
    }

    private TVCardView card(int title) {
        if (adapter == null)
            return null;
        List<TVCardView> cards = adapter.previewCards();
        if (cards == null)
            return null;
        for (TVCardView c : cards)
            if (owner.getString(title).equals(c.previewTitle()))
                return c;
        return null;
    }

    private List<TVMenuItem> items(int cardTitle) {
        List<TVMenuItem> out = new ArrayList<>();
        TVCardView card = card(cardTitle);
        TVMenu menu = card == null ? null : card.previewMenu();
        if (menu != null)
            for (int i = 0; i < menu.getChildCount(); i++) {
                View child = menu.getChildAt(i);
                if (child instanceof TVMenuItem && child.getVisibility() == View.VISIBLE)
                    out.add((TVMenuItem)child);
            }
        return out;
    }

    /** Radio items of a card: checkable, not a switch. */
    private List<TVMenuItem> radios(int cardTitle) {
        List<TVMenuItem> out = new ArrayList<>();
        for (TVMenuItem item : items(cardTitle))
            if (item.findViewById(R.id.info_text) instanceof Checkable && !item.isSwitchable())
                out.add(item);
        return out;
    }

    private TVMenuItem item(int cardTitle, String text) {
        for (TVMenuItem item : items(cardTitle))
            if (text.equals(item.getText()))
                return item;
        return null;
    }

    private static String plain(TVMenuItem item) {
        return HtmlCompat.fromHtml(item.getText(), 0).toString();
    }

    private List<PlaybackSubtitlePanel.Track> asTracks(List<TVMenuItem> items, boolean subtitles) {
        List<PlaybackSubtitlePanel.Track> out = new ArrayList<>();
        for (int i = 0; i < items.size(); i++) {
            TVMenuItem item = items.get(i);
            final int index = i;
            Object language = item.getTag(R.id.preview_track_language);
            out.add(new PlaybackSubtitlePanel.Track(plain(item), language instanceof String ? (String)language : null,
                                                    item.isChecked(), item.isEnabled(), () -> {
                                                        if (subtitles && index > 0)
                                                            lastTrack = index;
                                                        item.previewClick();
                                                    }));
        }
        return out;
    }

    // ---- Subtitles -------------------------------------------------------------------------

    private List<TVMenuItem> subtitleTracks() {
        return radios(R.string.menu_subtitles);
    }

    @Override
    public List<PlaybackSubtitlePanel.Track> tracks() {
        return asTracks(subtitleTracks(), true);
    }

    @Override
    public int get(String key) {
        SubtitleManager m = manager();
        if (key.equals("enabled")) {
            List<TVMenuItem> items = subtitleTracks();
            for (int i = 1; i < items.size(); i++)
                if (items.get(i).isChecked())
                    return 1;
            return 0;
        }
        if (m == null)
            return 0;
        switch (key) {
        case "size":
            return m.getSize();
        case "colour":
            return m.getColor();
        case "position":
            return m.getVerticalPosition();
        case "outline":
            return m.getOutlineState() ? 1 : 0;
        case "background":
            return m.getBackgroundState() ? 1 : 0;
        case "opacity":
            return m.getBackgroundOpacity();
        case "delay":
            return player().subtitlePresentationDelay();
        default:
            throw new IllegalArgumentException("Unknown subtitle control");
        }
    }

    @Override
    public Boolean seriesChoices() {
        if (!(owner instanceof PlayerActivity) || ((PlayerActivity)owner).previewEpisode().isEmpty())
            return null;
        return ((PlayerActivity)owner).seriesTrackChoicesEnabled();
    }

    @Override
    public void setSeriesChoices(boolean enabled) {
        if (owner instanceof PlayerActivity)
            ((PlayerActivity)owner).setSeriesTrackChoicesEnabled(enabled);
    }

    @Override
    public String seriesName() {
        return owner instanceof PlayerActivity ? ((PlayerActivity)owner).previewTitle() : "";
    }

    @Override
    public boolean limitsAvailable() {
        return PlayerService.sPlayerService != null;
    }

    private PlaybackLimits limits() {
        PlayerService service = PlayerService.sPlayerService;
        return service == null ? null : service.playbackLimits();
    }

    @Override
    public int sleepChoice() {
        PlaybackLimits limits = limits();
        return limits == null ? 0 : limits.timer().ordinal();
    }

    @Override
    public void setSleepChoice(int choice) {
        PlaybackLimits limits = limits();
        PlaybackLimits.Timer[] all = PlaybackLimits.Timer.values();
        if (limits != null)
            limits.setTimer(all[Math.max(0, Math.min(all.length - 1, choice))]);
    }

    @Override
    public long sleepEndsAt() {
        PlaybackLimits limits = limits();
        long remaining = limits == null ? 0 : limits.remainingMs();
        return remaining > 0 ? System.currentTimeMillis() + remaining : 0;
    }

    @Override
    public int stillWatchingAfter() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(owner);
        if (!prefs.getBoolean(PlaybackLimits.KEY_STILL_WATCHING, true))
            return 0;
        return prefs.getInt(PlaybackLimits.KEY_EPISODES, PlaybackLimits.DEFAULT_EPISODES);
    }

    @Override
    public void setStillWatchingAfter(int episodes) {
        boolean enabled = episodes > 0;
        SharedPreferences.Editor prefs = PreferenceManager.getDefaultSharedPreferences(owner).edit();
        prefs.putBoolean(PlaybackLimits.KEY_STILL_WATCHING, enabled);
        if (enabled)
            prefs.putInt(PlaybackLimits.KEY_EPISODES, episodes);
        prefs.apply();
        PlaybackLimits limits = limits();
        if (limits != null)
            limits.configureStillWatching(enabled, enabled ? episodes : PlaybackLimits.DEFAULT_EPISODES);
    }

    @Override
    public void appearancePreview(boolean shown) {
        SubtitleManager m = manager();
        if (m != null)
            m.setAppearancePreview(shown);
    }

    @Override
    public android.graphics.Bitmap appearanceSample(String text, int width, int height) {
        SubtitleManager m = manager();
        return m == null ? null : m.appearanceSample(text, width, height);
    }

    @Override
    public boolean available() {
        return manager() != null;
    }

    @Override
    public void set(String key, int value) {
        if (key.equals("enabled")) {
            List<TVMenuItem> items = subtitleTracks();
            if (items.isEmpty())
                return;
            if (value == 0) {
                for (int i = 1; i < items.size(); i++)
                    if (items.get(i).isChecked())
                        lastTrack = i;
                items.get(0).previewClick();
            } else if (items.size() > 1)
                items.get(Math.max(1, Math.min(lastTrack, items.size() - 1))).previewClick();
            return;
        }
        SubtitleManager m = manager();
        if (m == null)
            return;
        SharedPreferences.Editor prefs = PreferenceManager.getDefaultSharedPreferences(owner).edit();
        switch (key) {
        case "size":
            m.setSize(value);
            prefs.putInt(PlayerActivity.KEY_SUBTITLE_SIZE, m.getSize())
                .putInt(SubtitleManager.KEY_FONT_SIZE_PT, m.getFontSizePt());
            break;
        case "colour":
            m.setColor(value);
            prefs.putInt(PlayerActivity.KEY_SUBTITLE_COLOR, value);
            break;
        case "position":
            // The manager saves the new percentage key itself; the old pixel key is left alone (engine hook note).
            m.setVerticalPosition(value);
            break;
        case "outline":
            m.setOutlineState(value != 0);
            prefs.putBoolean(PlayerActivity.KEY_SUBTITLE_OUTLINE, m.getOutlineState())
                .putFloat(SubtitleManager.KEY_OUTLINE_WIDTH, m.getOutlineWidth());
            break;
        case "background":
            m.setBackgroundState(value != 0);
            prefs.putBoolean(PlayerActivity.KEY_SUBTITLE_BACKGROUND, m.getBackgroundState())
                .putInt(SubtitleManager.KEY_BG_MODE, m.getBgMode());
            break;
        case "opacity":
            m.setBackgroundOpacity(value);
            prefs.putInt(PlayerActivity.KEY_SUBTITLE_BG_OPACITY, value);
            break;
        case "delay":
            player().setSubtitleDelay(value - get("delay"));
            return;
        default:
            throw new IllegalArgumentException("Unknown subtitle control");
        }
        prefs.apply();
    }

    @Override
    public void download() {
        TVMenuItem item = item(R.string.menu_subtitles, owner.getString(R.string.get_subtitles_online));
        if (item != null)
            item.previewClick();
    }

    @Override
    public Runnable chooseFile() {
        TVMenuItem item = item(R.string.menu_subtitles, owner.getString(R.string.get_subtitles_on_drive));
        return item == null ? null : item::previewClick;
    }

    // ---- Audio -----------------------------------------------------------------------------

    @Override
    public List<PlaybackSubtitlePanel.Track> audioTracks() {
        return asTracks(radios(R.string.menu_audio), false);
    }

    @Override
    public Integer audioDelay() {
        if (player() == null ||
            item(R.string.menu_audio, owner.getString(R.string.player_pref_audio_delay_title)) == null)
            return null;
        return player().presentationAudioDelay();
    }

    @Override
    public void stepAudioDelay(int direction) {
        if (player() != null)
            player().stepAudioDelay(direction * PlayerActivity.AUDIO_DELAY_STEP_MS);
    }

    private TVMenuItem optionItem(String key) {
        switch (key) {
        case "boost":
            return item(R.string.menu_audio, owner.getString(R.string.pref_audio_filt_title));
        case "night":
            return item(R.string.menu_audio, owner.getString(R.string.pref_audio_filt_night_mode));
        case "spatial":
            return item(R.string.menu_audio, owner.getString(R.string.spatialization_capabilities));
        case "skip":
            for (TVMenuItem item : items(R.string.pref_play_mode_title))
                if (item.isSwitchable())
                    return item;
            return null;
        default:
            return null;
        }
    }

    @Override
    public Boolean option(String key) {
        TVMenuItem item = optionItem(key);
        return item == null ? null : item.isChecked();
    }

    @Override
    public boolean optionEnabled(String key) {
        TVMenuItem item = optionItem(key);
        return item != null && item.isEnabled();
    }

    @Override
    public void toggleOption(String key) {
        TVMenuItem item = optionItem(key);
        if (item != null)
            item.previewClick();
    }

    // ---- Playback --------------------------------------------------------------------------

    @Override
    public List<PlaybackSubtitlePanel.Choice> choices(String key) {
        int title;
        switch (key) {
        case "format":
            title = R.string.pref_format_mode_title;
            break;
        case "s3d":
            title = R.string.pref_s3d_mode_title;
            break;
        case "playmode":
            title = R.string.pref_play_mode_title;
            break;
        default:
            return Collections.emptyList();
        }
        List<PlaybackSubtitlePanel.Choice> out = new ArrayList<>();
        for (TVMenuItem item : radios(title))
            out.add(
                new PlaybackSubtitlePanel.Choice(plain(item), item.isChecked(), item.isEnabled(), item::previewClick));
        return out;
    }

    @Override
    public Float speed() {
        if (player() == null ||
            item(R.string.menu_audio, owner.getString(R.string.player_pref_audio_speed_title)) == null)
            return null;
        return player().presentationAudioSpeed();
    }

    @Override
    public void setSpeed(float speed) {
        if (player() != null)
            player().setPresentationAudioSpeed(speed);
    }

    @Override
    public Runnable pictureInPicture() {
        TVMenuItem item = item(R.string.pref_format_mode_title, owner.getString(R.string.picture_in_picture));
        return item == null || !item.isEnabled() ? null : item::previewClick;
    }
}
