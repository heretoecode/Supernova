package com.archos.mediacenter.video.leanback;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.text.*;
import android.text.style.*;
import android.view.View;
import android.widget.*;
import com.archos.mediacenter.video.browser.adapters.object.*;
import com.archos.mediacenter.video.leanback.PreviewLibraryLoader.Entry;
import com.archos.mediacenter.video.leanback.details.PreviewMediaInfo;
import com.archos.mediacenter.video.streaming.StreamingRepository;
import com.archos.mediacenter.video.utils.SubtitleDiscovery;
import com.archos.mediacenter.video.utils.VideoMetadata;
import java.io.Serializable;
import org.json.*;
import java.util.*;

/** Shared, compact metadata. Descriptive provider facts never imply local file capabilities. */
public final class SharedMetadata {
    public static final class Facts implements Serializable {
        public String certificate = "", year = "", genres = "", context = "";
        public double rating;
        public long votes;

        public Facts copy() {
            Facts copy = new Facts();
            copy.certificate = certificate;
            copy.year = year;
            copy.genres = genres;
            copy.context = context;
            copy.rating = rating;
            copy.votes = votes;
            copy.technical.addAll(technical);
            return copy;
        }

        public final List<String> technical = new ArrayList<>();
    }

    /** "2 h 3 min", "2 h", "48 min" (owner, 18 Nov 2026; DT9). */
    public static String runtime(long minutes) {
        if (minutes <= 0)
            return "";
        String hours = minutes >= 60 ? minutes / 60 + " h" : "";
        String rest = minutes % 60 > 0 ? minutes % 60 + " min" : "";
        return hours.isEmpty() ? rest : rest.isEmpty() ? hours : hours + " " + rest;
    }

    /** Whole, readable money (DT9): "$25 million", "$1.2 billion", "$850,000". */
    public static String money(long dollars) {
        if (dollars >= 1_000_000_000L)
            return "$" + shortNumber(dollars / 1e9) + " billion";
        if (dollars >= 1_000_000L)
            return "$" + shortNumber(dollars / 1e6) + " million";
        return "$" + java.text.NumberFormat.getIntegerInstance(java.util.Locale.US).format(dollars);
    }

    private static String shortNumber(double value) {
        double rounded = Math.round(value * 10) / 10.0;
        return rounded == Math.rint(rounded) ? String.valueOf((long) rounded)
                                             : String.format(java.util.Locale.US, "%.1f", rounded);
    }

    /** A bare language code ("ara", "en") as its name ("Arabic", "English"); anything else unchanged (DT9). */
    public static String languageName(String code) {
        String value = code == null ? "" : code.trim();
        if (!value.matches("[A-Za-z]{2,3}"))
            return value;
        String code = value.toLowerCase(java.util.Locale.ROOT);
        if (code.length() == 3)
            for (String two : java.util.Locale.getISOLanguages()) {
                try {
                    if (new java.util.Locale(two).getISO3Language().equals(code)) {
                        code = two;
                        break;
                    }
                } catch (java.util.MissingResourceException unknown) {
                    // No three-letter form for this one.
                }
            }
        String name = new java.util.Locale(code).getDisplayLanguage(java.util.Locale.ENGLISH);
        return name.isEmpty() || name.equalsIgnoreCase(value) ? value : name;
    }

    public static String count(int value, String singular) {
        return value <= 0 ? "" : value + " " + singular + (value == 1 ? "" : "s");
    }

    public static String votes(long count) {
        return count >= 1000000 ? String.format(Locale.UK, "%.1fm votes", count / 1000000d)
            : count >= 1000 ? String.format(Locale.UK, "%.1fk votes", count / 1000d)
                                : count + " votes";
    }

    public static CharSequence primary(Facts f) {
        SpannableStringBuilder row = new SpannableStringBuilder();
        // One order everywhere: Age rating · Year · Length · Genres (owner, 12 Nov 2026; notes §29).
        for (String value : new String[] {
            f.certificate, f.year, f.context, f.genres,
                 f.rating > 0 && f.rating <= 10 ? String.format(Locale.UK, "TMDB %.1f/10", f.rating) : "",
                 f.rating > 0 && f.rating <= 10 && f.votes > 0 ? votes(f.votes) : ""
        }) {
            if (value == null || value.trim().isEmpty())
                continue;
            if (row.length() > 0)
                row.append(" · ");
            int start = row.length();
            row.append(value.trim());
            if (value.equals(f.certificate))
                row.setSpan(new AgeBadge(), start, row.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            if (value.endsWith(" votes"))
                row.setSpan(new ForegroundColorSpan(0xffb8c5ce), start, row.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        return row;
    }

    public static void bind(TextView primary, LinearLayout badges, Facts facts, int limit) {
        primary.setText(primary(facts));
        primary.setVisibility(primary.length() == 0 ? View.GONE : View.VISIBLE);
        primary.setTypeface(Typeface.create("sans-serif-light", Typeface.NORMAL));
        primary.setTextColor(Color.WHITE);
        primary.setIncludeFontPadding(false);
        primary.setShadowLayer(2, 0, 1, Color.BLACK);
        badges.removeAllViews();
        Set<String> seen = new LinkedHashSet<>(facts.technical);
        for (String value : seen) {
            if (badges.getChildCount() >= limit)
                break;
            if (value == null || value.trim().isEmpty())
                continue;
            TextView badge = SharedThreePanel.text(primary.getContext(), value, 12);
            badge.setSingleLine(true);
            badge.setIncludeFontPadding(false);
            badge.setPadding(dp(primary.getContext(), 6), dp(primary.getContext(), 3), dp(primary.getContext(), 6),
                dp(primary.getContext(), 3));
            GradientDrawable border = new GradientDrawable();
            border.setColor(0x60071522);
            border.setCornerRadius(dp(primary.getContext(), 4));
            border.setStroke(dp(primary.getContext(), 1), 0xbbb8c5ce);
            badge.setBackground(border);
            LinearLayout.LayoutParams size = new LinearLayout.LayoutParams(-2, -2);
            size.rightMargin = dp(primary.getContext(), 6);
            badges.addView(badge, size);
        }
        badges.setVisibility(badges.getChildCount() == 0 ? View.GONE : View.VISIBLE);
    }

    /**
     * Hero card policy, Home and Details alike (owner, 12 Nov 2026; notes §29, replacing 3 Nov): {@code Age rating ·
     * Year · Length · Genres}, the age rating in a small outlined badge, genres in TMDB's order, three at most;
     * anything missing is left out. No score and no format badges on either card; those stay in the Details tab.
     */
    public static Facts cardFacts(Facts original, boolean home) {
        Facts facts = original.copy();
        facts.votes = 0;
        facts.rating = 0;
        facts.technical.clear();
        Set<String> genres = PreviewGenres.parse(facts.genres);
        List<String> compact = new ArrayList<>(genres);
        facts.genres = android.text.TextUtils.join(", ", compact.subList(0, Math.min(3, compact.size())));
        return facts;
    }

    public static void bindCard(TextView primary, LinearLayout badges, Facts original, boolean home) {
        Facts facts = cardFacts(original, home);
        primary.setTextSize(16 * TvLayout.scale(primary.getContext()));
        primary.setSingleLine(true);
        bind(primary, badges, facts, 5);
        // Readable on bright artwork: a soft shadow under the hero text (approved option 2).
        primary.setShadowLayer(heroShadow(primary.getContext()), 0, dp(primary.getContext(), 1), 0xCC000000);
    }

    public static Facts entry(Context c, Entry entry, boolean home) {
        Facts facts = new Facts();
        if (entry == null)
            return facts;
        facts.year = entry.year() > 0 ? String.valueOf(entry.year()) : "";
        facts.genres = entry.genres.replace('|', ',');
        boolean episode = entry.media instanceof Episode;
        facts.certificate =
            ""; // The legacy index loses territory; only the country-scoped cache can supply an age badge.
        facts.rating = 0; // Indexed/NFO scores do not retain a verified provider.
        String kind = entry.media instanceof Movie ? "movie" : "tv";
        String section = episode ? "season/" + ((Episode) entry.media).getSeasonNumber() : "";
        JSONObject cached = entry.onlineId > 0 ? PreviewMetadataCache.read(c, kind, entry.onlineId, section) : null,
                   data = cached == null ? null : cached.optJSONObject("data");
        if (episode && data != null) {
            JSONArray episodes = data.optJSONArray("episodes");
            data = null;
            if (episodes != null)
                for (int n = 0; n < episodes.length(); n++) {
                    JSONObject candidate = episodes.optJSONObject(n);
                    if (candidate != null
                        && candidate.optInt("episode_number") == ((Episode) entry.media).getEpisodeNumber()) {
                        data = candidate;
                        break;
                    }
                }
        }
        if (data != null) {
            if (data.optDouble("vote_average", 0) > 0)
                facts.rating = data.optDouble("vote_average");
            facts.votes = data.optLong("vote_count");
        }
        if (!episode && entry.onlineId > 0) {
            JSONObject ratings = PreviewMetadataCache.read(
                c, kind, entry.onlineId, kind.equals("tv") ? "content_ratings" : "release_dates");
            String local = certificate(ratings == null ? null : ratings.optJSONObject("data"),
                StreamingRepository.country(c));
            if (!local.isEmpty())
                facts.certificate = local;
        }
        if (!home) {
            if (entry.media instanceof Tvshow)
                facts.context = join(count(entry.seasons, "Season"), count(entry.episodes, "Episode"));
            else if (entry.media instanceof Video)
                facts.context = runtime(((Video) entry.media).getDurationMs() / 60000);
        }
        if (entry.media instanceof Video)
            facts.technical.addAll(technical(c, (Video) entry.media));
        return facts;
    }

    public static List<String> technical(Context c, Video video) {
        List<String> badges = new ArrayList<>();
        if (LibraryHealth.state(c, video) != LibraryHealth.State.AVAILABLE)
            return badges;
        int w = video.getMeasuredWidth(), h = video.getMeasuredHeight();
        if (w > 0 && h > 0)
            badges.add(w >= 3840 || h >= 2160 ? "4K UHD"
                    : w >= 1728 || h >= 1040 ? "FHD"
                    : w >= 1200 || h >= 720 ? "HD"
                                              : "SD");
        String hdr = PreviewVariants.dynamicRange(c, video);
        if (!hdr.isEmpty())
            badges.add(hdr);
        VideoMetadata data = video.getMetadata();
        Set<String> measuredAudio = new LinkedHashSet<>();
        if (data != null)
            for (int n = 0; n < data.getAudioTrackNb(); n++) {
                VideoMetadata.AudioTrack track = data.getAudioTrack(n);
                if (track != null) {
                    String value = PreviewMediaInfo.audioTrack(track.format, track.channels, 0);
                    if (!value.isEmpty())
                        measuredAudio.add(value);
                }
            }
        String audio = measuredAudio.isEmpty() ? PreviewMediaInfo.format(video.getCalculatedBestAudioFormat())
                                               : TextUtils.join(" / ", measuredAudio);
        if (!audio.isEmpty())
            badges.add(audio);
        boolean subtitles = false, sdh = false, captions = false, ad = false;
        if (data != null) {
            for (int n = 0; n < data.getSubtitleTrackNb(); n++) {
                VideoMetadata.SubtitleTrack track = data.getSubtitleTrack(n);
                if (track != null) {
                    subtitles = true;
                    sdh |= (track.disposition & 128) != 0;
                    captions |= (track.disposition & (1 << 16)) != 0;
                }
            }
            for (int n = 0; n < data.getAudioTrackNb(); n++) {
                VideoMetadata.AudioTrack track = data.getAudioTrack(n);
                if (track != null)
                    ad |= (track.disposition & 256) != 0;
            }
        }
        if (subtitles)
            badges.add(subtitleLabel(sdh, captions));
        if (ad)
            badges.add("AD");
        for (String local : SubtitleDiscovery.localBadges(c, video))
            if (!badges.contains(local)
                && !(local.equals("Subtitles")
                    && (badges.contains("SDH") || badges.contains("Captions") || badges.contains("Captions/SDH"))))
                badges.add(local);
        return badges;
    }

    /** FFmpeg's CAPTIONS flag verifies caption text; it does not establish a closed-caption transport codec. */
    public static String subtitleLabel(boolean sdh, boolean captions) {
        return captions ? (sdh ? "Captions/SDH" : "Captions") : sdh ? "SDH" : "Subtitles";
    }

    public static String certificate(JSONObject data, String country) {
        JSONArray regions = data == null ? null : data.optJSONArray("results");
        if (regions == null)
            return "";
        for (int i = 0; i < regions.length(); i++) {
            JSONObject region = regions.optJSONObject(i);
            if (region == null || !country.equals(region.optString("iso_3166_1")))
                continue;
            String rating = region.optString("rating");
            if (!rating.isEmpty())
                return rating;
            JSONArray releases = region.optJSONArray("release_dates");
            String fallback = "";
            if (releases != null)
                for (int n = 0; n < releases.length(); n++) {
                    JSONObject release = releases.optJSONObject(n);
                    if (release == null)
                        continue;
                    String value = release.optString("certification");
                    if (value.isEmpty())
                        continue;
                    if (release.optInt("type") == 3)
                        return value;
                    if (fallback.isEmpty())
                        fallback = value;
                }
            return fallback;
        }
        return "";
    }

    public static String join(String... values) {
        List<String> present = new ArrayList<>();
        for (String value : values)
            if (value != null && !value.isEmpty())
                present.add(value);
        return TextUtils.join(" · ", present);
    }

    /** The approved soft shadow radius for text on hero artwork. */
    public static float heroShadow(Context c) {
        return 6 * c.getResources().getDisplayMetrics().density;
    }

    private static int dp(Context c, int n) {
        return PreviewDialog.dp(c, n);
    }

    private static final class AgeBadge extends ReplacementSpan {
        public int getSize(Paint p, CharSequence text, int start, int end, Paint.FontMetricsInt fm) {
            return Math.round(p.measureText(text, start, end) + p.getTextSize() * .6f);
        }

        public void draw(
            Canvas c, CharSequence text, int start, int end, float x, int top, int y, int bottom, Paint p) {
            Paint outline = new Paint(p);
            outline.setStyle(Paint.Style.STROKE);
            outline.setStrokeWidth(1);
            float inset = p.getTextSize() * .25f, width = getSize(p, text, start, end, null);
            c.drawRoundRect(x, y + p.ascent() - inset, x + width, y + p.descent() + inset, 3, 3, outline);
            c.drawText(text, start, end, x + p.getTextSize() * .3f, y, p);
        }
    }

    private SharedMetadata() {
    }
}
