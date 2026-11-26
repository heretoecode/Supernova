package com.archos.mediacenter.video.leanback;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * The next-episode label on a running show's hero (owner, 25 Nov 2026; BRIEF_0158_B › Next episode on the hero;
 * DETAILS.md, HOME.md; mock-ups {@code next-episode/tv/01}, {@code 03}). Placement A: its own line above the logo,
 * 22 px above it; the facts line and buttons never move. A small outlined box (outline = information, RULES §3) with
 * the calendar icon. Shown only when TMDB's next episode airs within seven days; never focusable.
 */
public final class NextEpisodeLabel
{
    static final int WINDOW_DAYS = 7;

    private NextEpisodeLabel()
    {
    }

    /** The label's words, or null when nothing is shown (ended shows, unknown dates, more than a week away). */
    public static String words(JSONObject details, List<LocalDate> recentAirDates, LocalDate today)
    {
        if (details == null || ended(details))
            return null;
        LocalDate next = date(details.optJSONObject("next_episode_to_air"));
        if (next == null)
            return null;
        long days = ChronoUnit.DAYS.between(today, next);
        if (days < 0 || days > WINDOW_DAYS)
            return null;
        if (weekly(recentAirDates, next))
            return "New episode every " + dayName(next.getDayOfWeek());
        if (days == 0)
            return "New episode today";
        if (days == 1)
            return "New episode tomorrow";
        return "New episode " + dayName(next.getDayOfWeek());
    }

    /**
     * Details tab › Status (owner, 25 Nov; {@code next-episode/tv/04}): "Returning · S2 E4 on Sun 30 Nov" when the next
     * episode's date is known, else "Returning · 2 seasons"; ended shows say so with their seasons.
     */
    public static String status(JSONObject details, LocalDate today)
    {
        if (details == null)
            return "";
        int seasons = details.optInt("number_of_seasons", 0);
        String count = seasons <= 0 ? "" : seasons + (seasons == 1 ? " season" : " seasons");
        if (ended(details))
            return join(details.optString("status", "Ended").equals("Canceled") ? "Cancelled" : "Ended", count);
        JSONObject next = details.optJSONObject("next_episode_to_air");
        LocalDate date = date(next);
        if (date != null && !date.isBefore(today) && next.optInt("season_number", -1) >= 0 &&
            next.optInt("episode_number", 0) > 0)
            return "Returning · S" + next.optInt("season_number") + " E" + next.optInt("episode_number") + " on " +
                date.getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.UK) + " " + date.getDayOfMonth() + " " +
                date.getMonth().getDisplayName(TextStyle.SHORT, Locale.UK);
        String raw = details.optString("status", "");
        if (raw.isEmpty())
            return count;
        return join(raw.startsWith("Returning") ? "Returning" : raw, count);
    }

    /** "Every Sunday" once the last three episodes aired on the next episode's weekday. */
    static boolean weekly(List<LocalDate> recentAirDates, LocalDate next)
    {
        if (recentAirDates == null || recentAirDates.size() < 3)
            return false;
        List<LocalDate> aired = new ArrayList<>(recentAirDates);
        aired.removeIf(d -> d == null || !d.isBefore(next));
        aired.sort(null);
        if (aired.size() < 3)
            return false;
        for (int i = aired.size() - 3; i < aired.size(); i++)
            if (aired.get(i).getDayOfWeek() != next.getDayOfWeek())
                return false;
        return true;
    }

    /** Air dates of the episodes TMDB lists for the given seasons (the seasons Details already loaded). */
    public static List<LocalDate> airDates(Iterable<JSONArray> seasons)
    {
        List<LocalDate> dates = new ArrayList<>();
        if (seasons == null)
            return dates;
        for (JSONArray episodes : seasons)
            for (int i = 0; episodes != null && i < episodes.length(); i++)
            {
                LocalDate d = date(episodes.optJSONObject(i));
                if (d != null)
                    dates.add(d);
            }
        return dates;
    }

    static boolean ended(JSONObject details)
    {
        String raw = details.optString("status", "");
        return raw.equals("Ended") || raw.equals("Canceled") || raw.equals("Cancelled");
    }

    static LocalDate date(JSONObject episode)
    {
        if (episode == null)
            return null;
        String value = episode.optString("air_date", "");
        try
        {
            return value.length() >= 10 ? LocalDate.parse(value.substring(0, 10)) : null;
        }
        catch (RuntimeException invalid)
        {
            return null;
        }
    }

    private static String dayName(DayOfWeek day)
    {
        return day.getDisplayName(TextStyle.FULL, Locale.UK);
    }

    private static String join(String a, String b)
    {
        return b.isEmpty() ? a : a + " · " + b;
    }

    /** The label view: outlined small box, calendar icon, soft white words; never focusable; hidden until filled. */
    public static TextView create(Context c)
    {
        TextView label = new TextView(c);
        label.setTag("hero.next_episode");
        label.setTextSize(13);
        label.setTextColor(Color.WHITE);
        label.setTypeface(Typeface.create("sans-serif-medium", Typeface.NORMAL));
        label.setSingleLine(true);
        label.setGravity(Gravity.CENTER_VERTICAL);
        label.setFocusable(false);
        label.setFocusableInTouchMode(false);
        float density = c.getResources().getDisplayMetrics().density;
        label.setPadding(Math.round(8 * density), 0, Math.round(10 * density), 0);
        label.setBackground(SupernovaStyle.outline(c));
        PreviewIcon.apply(label, "date", 13);
        label.setCompoundDrawablePadding(Math.round(6 * density));
        label.setShadowLayer(2 * density, 0, density, 0xCC000000);
        label.setVisibility(View.GONE);
        return label;
    }

    /** Height of the label line and its 22 px gap to the logo, taken from the title box so nothing else moves. */
    public static int lineHeight(Context c)
    {
        return Math.round(LABEL_DP * c.getResources().getDisplayMetrics().density) + gap(c);
    }

    static final int LABEL_DP = 15;

    public static int gap(Context c)
    {
        return Math.round(11 * c.getResources().getDisplayMetrics().density);
    }

    private static final java.util.concurrent.ExecutorService IO =
        java.util.concurrent.Executors.newSingleThreadExecutor(r -> {
            Thread thread = new Thread(r, "NextEpisodeLabel");
            thread.setDaemon(true);
            return thread;
        });
    private static final java.util.Map<TextView, Long> BOUND =
        java.util.Collections.synchronizedMap(new java.util.WeakHashMap<>());

    /**
     * Home's hero (the same label as Details, HOME.md): read from the title's kept TMDB details off the main thread,
     * then shown, taking its line from the title box ({@code fullHeight}) so nothing below moves.
     */
    public static void bindCached(TextView label, View titleBox, int fullHeight, long tmdbShowId)
    {
        BOUND.put(label, tmdbShowId);
        apply(label, titleBox, fullHeight, null);
        if (tmdbShowId <= 0)
            return;
        Context app = label.getContext().getApplicationContext();
        IO.execute(() -> {
            String words = null;
            try
            {
                JSONObject details = PreviewMetadataCache.read(app, "tv", tmdbShowId, "");
                JSONObject next = details == null ? null : details.optJSONObject("next_episode_to_air");
                List<JSONArray> seasons = new ArrayList<>();
                if (next != null)
                {
                    JSONObject season =
                        PreviewMetadataCache.read(app, "tv", tmdbShowId, "season/" + next.optInt("season_number", 0));
                    if (season != null && season.optJSONArray("episodes") != null)
                        seasons.add(season.optJSONArray("episodes"));
                }
                words = words(details, airDates(seasons), LocalDate.now());
            }
            catch (RuntimeException unavailable)
            {
                words = null;
            }
            String shown = words;
            label.post(() -> {
                Long bound = BOUND.get(label);
                if (bound != null && bound == tmdbShowId)
                    apply(label, titleBox, fullHeight, shown);
            });
        });
    }

    private static void apply(TextView label, View titleBox, int fullHeight, String words)
    {
        show(label, words);
        int box = fullHeight - (words == null ? 0 : lineHeight(label.getContext()));
        android.view.ViewGroup.LayoutParams size = titleBox.getLayoutParams();
        if (size != null && size.height != box)
        {
            size.height = box;
            titleBox.setLayoutParams(size);
        }
    }

    public static void show(TextView label, String words)
    {
        label.setText(words == null ? "" : words);
        label.setVisibility(words == null ? View.GONE : View.VISIBLE);
    }
}
