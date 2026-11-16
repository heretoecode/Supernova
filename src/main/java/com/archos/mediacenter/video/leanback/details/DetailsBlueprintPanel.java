package com.archos.mediacenter.video.leanback.details;

import android.content.Context;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.archos.mediacenter.video.R;
import com.archos.mediacenter.video.leanback.PreviewIcon;
import com.archos.mediacenter.video.leanback.SharedMetadata;
import com.archos.mediacenter.video.leanback.SupernovaStyle;
import com.archos.mediacenter.video.metadata.OmdbSupplement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * The Details tab (approved 14 Nov 2026, mock-up details-background/21, v20): Reception, Key information and
 * Technical information side by side, each under an icon heading with faint lines between its rows; then Cast and
 * Crew as portrait cards. For reading only: nothing here is focusable. The description lives in the hero (two lines)
 * and More › Full description, so this tab never repeats it. Missing values say "Unknown"; rows are never removed.
 */
final class DetailsBlueprintPanel extends LinearLayout {
    /** Soft near-white labels; pure white, bolder values; quieter "Unknown" (RULES, text on the page). */
    static final int LABEL = SupernovaStyle.TEXT_SECONDARY;
    static final int UNKNOWN = 0x99FFFFFF;
    static final int ROW_LINE = 0x14FFFFFF;

    private final Map<String, TextView> keyValues = new LinkedHashMap<>(), technicalValues = new LinkedHashMap<>();
    private final TextView synopsisView;
    private final View crewSource;

    private static final String[][] KEY_FIELDS = {
        {"Released", "Release / first air date", "Air date", "Year"},
        {"Runtime", "Runtime"},
        {"Genres", "Genres"},
        {"Directed by", "Directed by"},
        {"Written by", "Written by"},
        {"Studio", "Studio / Network"},
        {"Country", "Countries"},
        {"Language", "Language"},
        {"Original title", "Original title", "Series original title"},
        {"Budget", "Budget (TMDb)"},
        {"Revenue", "Box office (TMDb)"}
    };
    private static final String[][] TECH_FIELDS = {
        {"Resolution", "Resolution"},
        {"Video", "Video codec", "File formats"},
        {"HDR", "Dynamic range"},
        {"Frame rate", "Frame rate"},
        {"Bitrate", "Video bitrate"},
        {"Audio", "Audio tracks", "Audio"},
        {"Audio languages", "Audio languages"},
        {"Subtitles", "Subtitles"},
        {"Container", "Container"},
        {"Size", "File size", "Library size"},
        {"Added", "Added"}
    };

    DetailsBlueprintPanel(Context c, String synopsis, Map<String, String> facts, Map<String, String> technical,
                          Map<String, String> existingReception, View cast, View crew,
                          OmdbSupplement.Ratings supplementary) {
        this(c, synopsis, facts, technical, existingReception, cast, crew, supplementary, null);
    }

    DetailsBlueprintPanel(Context c, String synopsis, Map<String, String> facts, Map<String, String> technical,
                          Map<String, String> existingReception, View cast, View crew,
                          OmdbSupplement.Ratings supplementary, Runnable reviews) {
        super(c);
        setOrientation(VERTICAL);
        setTag("details.blueprint.v3");
        setFocusable(false);
        setDescendantFocusability(FOCUS_BLOCK_DESCENDANTS);
        setPadding(dp(8), dp(16), dp(8), dp(24));
        crewSource = crew;
        // The full description is not shown on this tab (it is in the hero and More › Full description); the view
        // is kept, hidden, so the page can still read and update it.
        synopsisView = text(synopsisOrUnknown(synopsis), 13, Color.WHITE, false);
        synopsisView.setTag("details.full.synopsis");
        synopsisView.setEllipsize(TextUtils.TruncateAt.END);
        synopsisView.setVisibility(GONE);
        addView(synopsisView);

        LinearLayout columns = new LinearLayout(c);
        columns.setBaselineAligned(false);
        addView(columns, new LayoutParams(-1, -2));
        LinearLayout reception = column(columns, "Reception", "star", "semantic:details.panel.reception");
        reception(reception, existingReception, supplementary);
        LinearLayout key = column(columns, "Key information", "list", "semantic:details.panel.key.information");
        Map<String, String> keyFacts = new LinkedHashMap<>(facts);
        addPeopleFacts(keyFacts);
        bindFacts(key, KEY_FIELDS, keyFacts, keyValues);
        LinearLayout tech =
            column(columns, "Technical information", "cog", "semantic:details.panel.technical.information");
        bindFacts(tech, TECH_FIELDS, technical, technicalValues);

        people("Cast", "people", cast);
        people("Crew", "film", crew);
    }

    void updateText(String synopsis, Map<String, String> facts, Map<String, String> technical) {
        String value = synopsisOrUnknown(synopsis);
        if (!value.contentEquals(synopsisView.getText()))
            synopsisView.setText(value);
        Map<String, String> keyFacts = new LinkedHashMap<>(facts);
        addPeopleFacts(keyFacts);
        updateValues(keyValues, KEY_FIELDS, keyFacts);
        updateValues(technicalValues, TECH_FIELDS, technical);
    }

    private static String synopsisOrUnknown(String synopsis) {
        return synopsis == null || synopsis.trim().isEmpty() ? "Unknown" : synopsis;
    }

    // ---- columns and rows --------------------------------------------------------------------------

    private LinearLayout column(LinearLayout columns, String title, String icon, String tag) {
        LinearLayout column = new LinearLayout(getContext());
        column.setOrientation(VERTICAL);
        column.setTag(tag);
        column.setFocusable(false);
        LayoutParams size = new LayoutParams(0, -2, 1);
        if (columns.getChildCount() > 0)
            size.leftMargin = dp(38);
        columns.addView(column, size);
        column.addView(heading(title, icon), headingSize(false));
        return column;
    }

    /** An icon heading, closer to its own content than to what is above it (RULES, sections and headings). */
    private TextView heading(String title, String icon) {
        TextView heading = text(title, 15, Color.WHITE, true);
        heading.setSingleLine(true);
        heading.setEllipsize(TextUtils.TruncateAt.END);
        PreviewIcon.apply(heading, icon, 16);
        heading.setCompoundDrawablePadding(dp(10));
        return heading;
    }

    private LayoutParams headingSize(boolean gapAbove) {
        LayoutParams size = new LayoutParams(-1, -2);
        size.topMargin = gapAbove ? dp(32) : 0;
        size.bottomMargin = dp(10);
        return size;
    }

    private void bindFacts(LinearLayout parent, String[][] fields, Map<String, String> values,
                           Map<String, TextView> bound) {
        for (String[] field : fields) {
            if (bound.size() > 0 || parent.getChildCount() > 1)
                line(parent);
            String value = first(values, Arrays.copyOfRange(field, 1, field.length));
            bound.put(field[0], fact(parent, field[0], value));
        }
    }

    private void updateValues(Map<String, TextView> bound, String[][] fields, Map<String, String> values) {
        for (String[] field : fields) {
            TextView view = bound.get(field[0]);
            String value = first(values, Arrays.copyOfRange(field, 1, field.length));
            if (view != null && !value.contentEquals(view.getText())) {
                styleValue(view, value);
                ((View) view.getParent()).setContentDescription(field[0] + ": " + value);
            }
        }
    }

    /** A label/value row: soft label on the left, the value on the right in white and bolder. */
    private TextView fact(LinearLayout parent, String name, String value) {
        LinearLayout row = new LinearLayout(getContext());
        row.setGravity(Gravity.CENTER_VERTICAL);
        row.setMinimumHeight(dp(20));
        row.setContentDescription(name + ": " + value);
        TextView label = text(name, 13, LABEL, false);
        label.setSingleLine(true);
        label.setEllipsize(TextUtils.TruncateAt.END);
        row.addView(label, new LayoutParams(-2, -2));
        TextView data = text(value, 13, Color.WHITE, true);
        data.setSingleLine(true);
        data.setGravity(Gravity.END);
        data.setEllipsize(TextUtils.TruncateAt.END);
        LayoutParams valueSize = new LayoutParams(0, -2, 1);
        valueSize.leftMargin = dp(12);
        row.addView(data, valueSize);
        styleValue(data, value);
        parent.addView(row, new LayoutParams(-1, -2));
        return data;
    }

    private static void styleValue(TextView view, String value) {
        boolean unknown = "Unknown".equals(value);
        view.setText(value);
        view.setTextColor(unknown ? UNKNOWN : Color.WHITE);
        view.setTypeface(Typeface.create("sans-serif-medium", unknown ? Typeface.NORMAL : Typeface.BOLD));
    }

    /** A faint line between two rows; never under the last (RULES). */
    private void line(LinearLayout parent) {
        View rule = new View(getContext());
        rule.setTag("details.separator");
        rule.setBackgroundColor(ROW_LINE);
        LayoutParams size = new LayoutParams(-1, Math.max(1, dp(.5f)));
        size.topMargin = dp(4);
        size.bottomMargin = dp(4);
        parent.addView(rule, size);
    }

    // ---- reception -----------------------------------------------------------------------------------

    /** Reception row index for TMDb (rows 0-3 are the OMDb sources and awards). */
    static final int TMDB_ROW = 4;
    private String tmdbScore = "", tmdbVotes = "";

    private void reception(LinearLayout reception, Map<String, String> existing, OmdbSupplement.Ratings supplied) {
        boolean suppliedData = false;
        for (int slot = 0; slot < 4; slot++)
            suppliedData |= supplied.hasData(slot);
        tmdbScore = existing.containsKey("TMDB Rating") ? existing.get("TMDB Rating").replace(" / ", "/") : "";
        tmdbVotes = existing.getOrDefault("TMDb votes", "");
        boolean hasReception = !tmdbScore.isEmpty() || existing.containsKey("Indexed Rating") || suppliedData;
        if (tmdbScore.isEmpty() && existing.containsKey("Indexed Rating") && !suppliedData) {
            TextView score = text("Indexed rating " + existing.get("Indexed Rating"), 14, Color.WHITE, true);
            score.setTag("details.reception.primary");
            score.setSingleLine(true);
            reception.addView(score, new LayoutParams(-1, -2));
        }
        if (!hasReception) {
            TextView unavailable = text("Ratings unavailable", 14, UNKNOWN, false);
            unavailable.setTag("details.reception.unavailable");
            reception.addView(unavailable, new LayoutParams(-1, -2));
        }
        if (suppliedData)
            reception.setContentDescription("Reception. Supplementary data via OMDb");
        // Owner decision 2 Nov 2026: every rating with real data, in this order: IMDb, Rotten Tomatoes, Metacritic,
        // TMDb, Awards. Rows without data are left out.
        String[] providers = {"IMDb", "Rotten Tomatoes", "Metacritic", "Awards", "TMDb"};
        int[] order = {0, 1, 2, TMDB_ROW, 3};
        int shown = 0;
        for (int n : order) {
            if (n == TMDB_ROW ? tmdbScore.isEmpty() : !supplied.hasData(n))
                continue;
            if (shown++ > 0)
                line(reception);
            LinearLayout rating = new LinearLayout(getContext());
            rating.setGravity(Gravity.CENTER_VERTICAL);
            rating.setBaselineAligned(false);
            rating.setMinimumHeight(dp(38));
            rating.setTag("details.reception.slot." + n);
            rating.addView(providerMark(n, providers[n]), new LayoutParams(dp(64), dp(30)));
            LayoutParams valueSize = new LayoutParams(0, -2, 1);
            valueSize.leftMargin = dp(12);
            rating.addView(ratingValue(n, supplied), valueSize);
            reception.addView(rating, new LayoutParams(-1, -2));
        }
        if (shown > 0 && shown < 4) {
            // Rows keep their own height; nothing is stretched to fill the column.
            View spacer = new View(getContext());
            spacer.setTag("details.reception.spacer");
            reception.addView(spacer, new LayoutParams(-1, 0));
        }
    }

    /** TMDb mark: its name in TMDb's own teal. */
    private View tmdbMark() {
        TextView mark = text("TMDB", 15, 0xff01b4e4, true);
        mark.setGravity(Gravity.CENTER);
        mark.setSingleLine(true);
        return mark;
    }

    private View providerMark(int row, String name) {
        FrameLayout mark = new FrameLayout(getContext());
        if (row == TMDB_ROW) {
            View tile = tmdbMark();
            tile.setTag("details.reception.logo." + row);
            tile.setContentDescription(name);
            mark.addView(tile, new FrameLayout.LayoutParams(-1, -1, Gravity.CENTER));
            return mark;
        }
        ImageView logo = new ImageView(getContext());
        logo.setTag("details.reception.logo." + row);
        if (row == 0) {
            logo.setImageResource(R.drawable.imdb_logo);
            GradientDrawable tile = new GradientDrawable();
            tile.setColor(0xffeead3d);
            tile.setCornerRadius(dp(2));
            logo.setBackground(tile);
            logo.setPadding(dp(1), dp(1), dp(1), dp(1));
            logo.setColorFilter(new ColorMatrixColorFilter(new ColorMatrix(new float[] {
                0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -1, -1, -1, 1, 0})));
        } else if (row == 1) {
            logo.setImageResource(R.drawable.reception_rotten_tomatoes);
        } else if (row == 2) {
            logo.setImageResource(R.drawable.reception_metacritic);
        } else {
            logo.setImageResource(R.drawable.reception_awards);
        }
        logo.setScaleType(ImageView.ScaleType.FIT_CENTER);
        logo.setContentDescription(name);
        mark.addView(logo, new FrameLayout.LayoutParams(dp(26), dp(26), Gravity.CENTER));
        return mark;
    }

    /** One line: the score in white with its unit smaller, then the vote count small and soft beside it. */
    private View ratingValue(int row, OmdbSupplement.Ratings ratings) {
        LinearLayout box = new LinearLayout(getContext());
        box.setGravity(Gravity.CENTER_VERTICAL);
        String supplied = row == TMDB_ROW ? tmdbScore : ratings.value(row);
        if (row == 3) {
            box.setOrientation(VERTICAL);
            box.setGravity(Gravity.CENTER_VERTICAL | Gravity.START);
            int stop = supplied.indexOf(". ");
            String headline = stop < 0 ? supplied : supplied.substring(0, stop);
            TextView summary = text(headline, 14, Color.WHITE, true);
            summary.setTag("details.reception.awards.summary");
            summary.setMaxLines(2);
            summary.setEllipsize(TextUtils.TruncateAt.END);
            box.addView(summary, new LayoutParams(-1, -2));
            if (stop >= 0) {
                TextView body = text(supplied.substring(stop + 2), 11, LABEL, false);
                body.setTag("details.reception.awards.details");
                box.addView(body);
            }
            return box;
        }
        TextView score = text(supplied.isEmpty() ? "Rating unavailable" : supplied, 15, Color.WHITE, true);
        score.setTag("details.reception.score." + row);
        score.setSingleLine(true);
        int unit = supplied.indexOf('/');
        if (unit < 0 && supplied.endsWith("%"))
            unit = supplied.length() - 1;
        if (unit >= 0) {
            SpannableString styled = new SpannableString(supplied);
            styled.setSpan(new RelativeSizeSpan(.85f), unit, supplied.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            score.setText(styled);
        }
        box.addView(score, new LayoutParams(-2, -2));
        String caption;
        // Counts appear only when genuinely supplied; otherwise the caption names the measure (owner, 2 Nov 2026).
        if (row == 0)
            caption = ratings.imdbVotes.isEmpty() ? "IMDb rating" : votes(ratings.imdbVotes);
        else if (row == 1)
            caption = ratings.tomatoReviews.isEmpty() ? "Tomatometer"
                                                      : "Tomatometer (" + ratings.tomatoReviews + " reviews)";
        else if (row == 2)
            caption = ratings.metacriticReviews.isEmpty() ? "Metascore"
                                                          : "Metascore (" + ratings.metacriticReviews + " reviews)";
        else
            caption = tmdbVotes.isEmpty() ? "TMDb user score" : tmdbVotes + " votes";
        TextView count = text(caption, 11, LABEL, false);
        count.setTag("details.reception.count." + row);
        // Never cut short: on small screens or with large text the caption wraps instead.
        count.setMaxLines(3);
        LayoutParams countSize = new LayoutParams(0, -2, 1);
        countSize.leftMargin = dp(8);
        box.addView(count, countSize);
        return box;
    }

    // ---- cast and crew -------------------------------------------------------------------------------

    private void people(String title, String icon, View content) {
        LinearLayout region = new LinearLayout(getContext());
        region.setOrientation(VERTICAL);
        region.setTag("details.region." + title);
        region.setFocusable(false);
        region.addView(heading(title, icon), headingSize(true));
        if (content != null) {
            if (content.getParent() instanceof ViewGroup)
                ((ViewGroup) content.getParent()).removeView(content);
            content.setVisibility(VISIBLE);
            content.setPadding(0, 0, 0, 0);
            region.addView(content, new LayoutParams(-1, -2));
        }
        addView(region, new LayoutParams(-1, -2));
    }

    /** Directed by and Written by, read from the crew cards ("person:<name>:<role>"). */
    private void addPeopleFacts(Map<String, String> facts) {
        List<String> directors = new ArrayList<>(), writers = new ArrayList<>();
        collectCrew(crewSource, directors, writers);
        if (!directors.isEmpty() && !facts.containsKey("Directed by"))
            facts.put("Directed by", TextUtils.join(", ", directors));
        if (!writers.isEmpty() && !facts.containsKey("Written by"))
            facts.put("Written by", TextUtils.join(", ", writers));
    }

    private static void collectCrew(View view, List<String> directors, List<String> writers) {
        if (view == null)
            return;
        Object tag = view.getTag();
        if (tag instanceof String && ((String) tag).startsWith("person:")) {
            String[] parts = ((String) tag).split(":", 3);
            if (parts.length == 3) {
                String role = parts[2].toLowerCase(Locale.ROOT);
                if (role.equals("director") && !directors.contains(parts[1]))
                    directors.add(parts[1]);
                else if ((role.equals("writer") || role.equals("screenplay") || role.equals("story"))
                         && !writers.contains(parts[1]))
                    writers.add(parts[1]);
            }
        }
        if (view instanceof ViewGroup)
            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++)
                collectCrew(((ViewGroup) view).getChildAt(i), directors, writers);
    }

    // ---- helpers -------------------------------------------------------------------------------------

    private TextView text(String value, float sp, int colour, boolean bold) {
        TextView view = new TextView(getContext());
        view.setText(value);
        view.setTextSize(TypedValue.COMPLEX_UNIT_SP, sp);
        view.setTextColor(colour);
        view.setTypeface(Typeface.create(bold ? "sans-serif-medium" : "sans-serif", Typeface.NORMAL));
        view.setIncludeFontPadding(false);
        view.setFocusable(false);
        return view;
    }

    private static String first(Map<String, String> fields, String... keys) {
        for (String key : keys) {
            String value = fields.get(key);
            if (value != null && !value.trim().isEmpty())
                return value;
        }
        return "Unknown";
    }

    static Map<String, String> facts(LinearLayout panel) {
        Map<String, String> values = new LinkedHashMap<>();
        for (int i = 0; i < panel.getChildCount(); i++) {
            View view = panel.getChildAt(i);
            if (view instanceof LinearLayout) {
                LinearLayout row = (LinearLayout) view;
                if (row.getChildCount() == 2 && row.getChildAt(0) instanceof TextView
                    && row.getChildAt(1) instanceof TextView)
                    values.put(((TextView) row.getChildAt(0)).getText().toString(),
                               ((TextView) row.getChildAt(1)).getText().toString());
            }
        }
        return values;
    }

    private static String votes(String text) {
        try {
            return SharedMetadata.votes(Long.parseLong(text.replace(",", "")))
                .replace(".0k", "k")
                .replace(".0m", "m")
                .replace("k", "K")
                .replace("m", "M");
        } catch (NumberFormatException invalid) {
            return "";
        }
    }

    private int dp(float value) {
        return Math.round(value * getResources().getDisplayMetrics().density);
    }

    /** Plain colours used here. */
    private static final class Color {
        static final int WHITE = 0xFFFFFFFF;
    }
}
