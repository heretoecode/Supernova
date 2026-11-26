package com.archos.mediacenter.video.leanback.details;

import android.app.Activity;
import android.view.*;
import android.view.ViewTreeObserver;
import androidx.recyclerview.widget.*;
import com.archos.mediacenter.video.browser.adapters.object.*;
import com.archos.mediacenter.video.diagnostics.Diagnostics;
import com.archos.mediacenter.video.leanback.*;
import com.archos.mediacenter.video.leanback.presenter.PreviewCardPresenter;
import java.text.SimpleDateFormat;
import java.util.*;
import com.archos.mediacenter.video.streaming.*;
import android.net.Uri;
import java.util.Locale;
import org.json.JSONObject;

/** Recycled season rail; long seasons do not inflate or request all artwork at once. */
final class PreviewEpisodeRow extends PreviewFocusRecycler {
    private final List<PreviewEpisodeChoice> episodes;
    private int remembered;
    /** The season card first in the row (DETAILS.md › Episodes tab); null when the row has none. */
    private String seasonName;
    private String seasonLine = "";
    private Uri seasonPoster;
    private Runnable seasonOpen;
    private java.util.function.Consumer<View> seasonMenu;
    private static final int SEASON_CARD = 1;

    /** Puts the season card first: OK opens the season's page, long-press its menu. */
    void setSeasonCard(String name, String line, Uri poster, Runnable open, java.util.function.Consumer<View> menu) {
        seasonName = name;
        seasonLine = line == null ? "" : line;
        seasonPoster = poster;
        seasonOpen = open;
        seasonMenu = menu;
        if (getAdapter() != null)
            getAdapter().notifyDataSetChanged();
    }

    /** 1 while the season card leads the row. */
    private int lead() {
        return seasonName == null ? 0 : 1;
    }

    PreviewEpisodeRow(Activity activity, List<PreviewEpisodeChoice> values, long showId,
        StreamingRepository.Availability availability) {
        super(activity);
        episodes = new ArrayList<>(values);
        setLayoutManager(new LinearLayoutManager(activity, HORIZONTAL, false));
        setItemAnimator(null);
        setClipChildren(false);
        setClipToPadding(false);
        setPadding(PreviewDialog.dp(activity, 5), PreviewDialog.dp(activity, 6), PreviewDialog.dp(activity, 5),
            PreviewDialog.dp(activity, 6));
        setAdapter(new RecyclerView.Adapter<RecyclerView.ViewHolder>() {
            @Override
            public int getItemCount() {
                return episodes.size() + lead();
            }
            @Override
            public int getItemViewType(int position) {
                return position < lead() ? SEASON_CARD : 0;
            }
            public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int type) {
                PreviewLandscapeCard card = new PreviewLandscapeCard(activity);
                RecyclerView.LayoutParams size = new RecyclerView.LayoutParams(cardWidth(), -1);
                size.rightMargin = PreviewDialog.dp(activity, PreviewCardPresenter.Card.ROW_GAP_DP);
                card.setLayoutParams(size);
                return new RecyclerView.ViewHolder(card) {
                };
            }
            public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
                if (position < lead()) {
                    PreviewLandscapeCard season = (PreviewLandscapeCard) holder.itemView;
                    season.bindSeason(seasonPoster, seasonName, seasonLine);
                    season.setTag("season-card");
                    season.setOnClickListener(v -> seasonOpen.run());
                    season.setOnLongClickListener(v -> {
                        if (seasonMenu != null)
                            seasonMenu.accept(v);
                        return seasonMenu != null;
                    });
                    return;
                }
                PreviewEpisodeChoice choice = episodes.get(position - lead());
                Episode ep = choice.local;
                PreviewLandscapeCard card = (PreviewLandscapeCard) holder.itemView;
                card.progress.setVisibility(View.GONE);
                card.setOnClickListener(null);
                card.setOnLongClickListener(null);
                card.setOnKeyListener(null);
                card.setClickable(false);
                if (ep != null) {
                    card.bind(ep.getEpisodeNumber() + " · " + ep.getEpisodeName(),
                        (ep.getDurationMs() > 0 ? ep.getDurationMs() / 60000 + " min" : "")
                            + (PreviewSeriesJourney.completed(ep) ? " · Watched" : ""),
                        ep.getPictureUri() != null ? ep.getPictureUri() : ep.getPreviewBackdrop(), true, ep.getId(),
                        "details.episodes", "episode_still");
                    card.setTag("episode:" + ep.getId());
                    card.progress.setProgress(
                        PreviewCardPresenter.progress(
                            ep.getResumeMs(), ep.getDurationMs()));
                    card.progress.setVisibility(ep.getResumeMs() > 0 ? View.VISIBLE : View.GONE);
                    // Episodes (owner, 18 Nov 2026): OK plays the episode at once; long-press or Menu opens the
                    // same menu as More, beside the card (PreviewEpisodeMenu).
                    card.setOnClickListener(v -> PreviewEpisodeMenu.play(activity, ep));
                    card.setOnLongClickListener(v -> {
                        PreviewEpisodeMenu.show(activity, v, ep, localEpisodes());
                        return true;
                    });
                    card.setOnKeyListener((v, key, event) -> {
                        if (key != KeyEvent.KEYCODE_MENU || event.getAction() != KeyEvent.ACTION_UP)
                            return key == KeyEvent.KEYCODE_MENU;
                        PreviewEpisodeMenu.show(activity, v, ep, localEpisodes());
                        return true;
                    });
                    return;
                }
                JSONObject remote = choice.remote;
                String name = choice.number + " · " + remote.optString("name", "Episode " + choice.number),
                       still = remote.optString("still_path");
                int runtime = remote.optInt("runtime");
                List<StreamingRepository.Offer> offers = availability != null
                        && StreamingRepository.prefs(activity).getBoolean(StreamingRepository.ENABLED, false)
                        && aired(remote.optString("air_date"))
                    ? StreamingRepository.filter(
                          availability, StreamingRepository.selected(activity), StreamingRepository.preferred(activity))
                    : Collections.emptyList();
                boolean exact =
                    StreamingRepository.exactEpisodeAvailability(availability, showId, choice.season, choice.number);
                card.bind(name,
                    (runtime > 0 ? runtime + " min · " : "")
                        + (availability == null || !offers.isEmpty() && !exact ? "Availability unknown"
                                : offers.isEmpty() ? "Unavailable"
                                                                               : offers.get(0).provider.name),
                    still.matches("/[A-Za-z0-9._-]+") ? Uri.parse("https://image.tmdb.org/t/p/w780" + still) : null,
                    exact && !offers.isEmpty(), remote.optLong("id"), "details.episodes", "episode_still");
                card.setTag("remote-episode:" + choice.season + ":" + choice.number);
                if (exact && !offers.isEmpty()) {
                    StreamingRepository.Provider provider = offers.get(0).provider;
                    PreviewProviderIcons.bindProvider(
                        card.availability, provider, remote.optLong("id"), "details.episodes");
                    card.availability.setImageAlpha(205);
                    card.setOnClickListener(v -> {
                        String[] labels = new String[offers.size()];
                        for (int i = 0; i < labels.length; i++)
                            labels[i] = "Watch on " + offers.get(i).provider.name;
                        PreviewDialog.choose(activity, name, labels, 0, index -> StreamingRepository.IO.submit(() -> {
                            String resolved = StreamingRepository.episodeLink(activity.getApplicationContext(), showId,
                                choice.season, choice.number, availability, offers.get(index).provider.id);
                            Diagnostics.event("episode_provider_link",
                                "season", choice.season, "episode", choice.number, "exact_scope", true);
                            activity.runOnUiThread(() -> {
                                if (!activity.isFinishing() && !activity.isDestroyed())
                                    StreamingActions.openWeb(activity,
                                        StreamingRepository.safeWebUrl(resolved) ? resolved : availability.watchUrl);
                            });
                        }));
                    });
                }
            }
            public void onViewRecycled(RecyclerView.ViewHolder holder) {
                ((PreviewLandscapeCard) holder.itemView).release();
            }
        });
    }

    /** The season's episodes that are in the library. */
    List<Episode> localEpisodes() {
        List<Episode> out = new ArrayList<>();
        for (PreviewEpisodeChoice choice : episodes)
            if (choice.local != null)
                out.add(choice.local);
        return out;
    }

    static boolean aired(String date) {
        return date != null && date.matches("[0-9]{4}-[0-9]{2}-[0-9]{2}")
            && date.compareTo(new SimpleDateFormat("yyyy-MM-dd", Locale.ROOT).format(new Date()))
            <= 0;
    }

    boolean focusRemote(String key) {
        for (int i = 0; i < episodes.size(); i++) {
            PreviewEpisodeChoice choice = episodes.get(i);
            if (key.equals("remote-episode:" + choice.season + ":" + choice.number)) {
                remembered = i + lead();
                focusRemembered();
                return true;
            }
        }
        return false;
    }

    /** RULES §14d (one row everywhere): Home's row card, 400 × 225 at 1080p. */
    private int cardWidth() {
        return PreviewDialog.dp(getContext(), PreviewCardPresenter.Card.ROW_WIDTH_DP);
    }

    @Override
    public void requestChildFocus(View child, View focused) {
        super.requestChildFocus(child, focused);
        View item = findContainingItemView(focused);
        if (item != null) {
            int p = getChildAdapterPosition(item);
            if (p >= 0)
                remembered = p;
        }
    }

    boolean focusEpisode(long id) {
        for (int i = 0; i < episodes.size(); i++)
            if (episodes.get(i).local != null && episodes.get(i).local.getId() == id) {
                remembered = i + lead();
                focusRemembered();
                return true;
            }
        return false;
    }

    void focusRemembered() {
        if (episodes.isEmpty())
            return;
        scrollToPosition(remembered);
        // Wait for layout, not an arbitrary time delay: off-screen cards do not exist yet.
        final ViewTreeObserver.OnGlobalLayoutListener listener =
            new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    RecyclerView.ViewHolder holder = findViewHolderForAdapterPosition(remembered);
                    if (holder != null) {
                        getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        holder.itemView.requestFocus();
                    }
                }
            };
        RecyclerView.ViewHolder holder = findViewHolderForAdapterPosition(remembered);
        if (holder != null)
            holder.itemView.requestFocus();
        else {
            getViewTreeObserver().addOnGlobalLayoutListener(listener);
            requestLayout();
        }
    }

    @Override
    public View focusSearch(View focused, int direction) {
        View next = super.focusSearch(focused, direction);
        if (direction == FOCUS_LEFT || direction == FOCUS_RIGHT) {
            View cursor = next;
            while (cursor != null && cursor != this)
                cursor = cursor.getParent() instanceof View ? (View) cursor.getParent() : null;
            if (cursor == null)
                return focused;
        }
        return next;
    }
}
