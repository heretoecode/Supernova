package com.archos.mediacenter.video.leanback;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Typeface;
import android.text.TextUtils;
import android.view.*;
import android.widget.*;
import com.archos.mediacenter.video.utils.OpenSubtitlesSearchResult;
import java.util.*;
import java.util.function.Consumer;

/** Image A2 presentation over already retrieved results; no new provider queries or backend. */
public final class SubtitleResultsPanel {
    public static Dialog show(Context context, List<OpenSubtitlesSearchResult> results,
                              Consumer<OpenSubtitlesSearchResult> download) {
        List<OpenSubtitlesSearchResult> snapshot = new ArrayList<>(results);
        SubtitlePanelStyle p = new SubtitlePanelStyle(context, false, true);
        Dialog dialog = PreviewDialog.create(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        LinearLayout panel = p.panel();
        panel.setTag("subtitles.download");
        p.header(panel, "Download Subtitles", dialog::cancel);
        TextView search = p.text("Search for subtitles...", 25);
        search.setTag("subtitles.download.search");
        search.setFocusable(true);
        search.setFocusableInTouchMode(true);
        search.setGravity(Gravity.CENTER_VERTICAL);
        search.setPadding(p.px(24), p.px(12), p.px(24), p.px(12));
        search.setBackground(p.surface(0x00000000, 0xff5a7bd8, 19));
        search.setTextColor(0xffb9c7e8);
        PreviewIcon.apply(search, "search", 24);
        panel.addView(search, new LinearLayout.LayoutParams(-1, p.px(75)));
        String[] query = {""};
        ScrollView scroll = new ScrollView(context);
        scroll.setSmoothScrollingEnabled(false);
        p.scrollbar(scroll);
        LinearLayout rows = new LinearLayout(context);
        rows.setOrientation(LinearLayout.VERTICAL);
        scroll.addView(rows);
        LinearLayout.LayoutParams body = new LinearLayout.LayoutParams(-1, 0, 1);
        body.topMargin = p.px(18);
        panel.addView(scroll, body);
        boolean[] accepted = {false};
        Runnable render = () -> {
            rows.removeAllViews();
            int count = 0;
            for (int i = 0; i < snapshot.size(); i++) {
                OpenSubtitlesSearchResult item = snapshot.get(i);
                String filename = safe(item.getFileName(), "Subtitle file"), language = language(item);
                if (!query[0].isEmpty() &&
                    !(filename + " " + language).toLowerCase(Locale.ROOT).contains(query[0].toLowerCase(Locale.ROOT)))
                    continue;
                LinearLayout row = p.row("subtitles.result." + i);
                row.setMinimumHeight(p.px(99));
                ImageView flag = new ImageView(context);
                flag.setImageDrawable(
                    new PreviewLanguageIcon(context, PreviewLanguageIcon.providerCode(item.getLanguage())));
                LinearLayout.LayoutParams f = new LinearLayout.LayoutParams(p.px(45), p.px(45));
                f.leftMargin = p.px(4);
                f.rightMargin = p.px(22);
                row.addView(flag, f);
                LinearLayout copy = new LinearLayout(context);
                copy.setOrientation(LinearLayout.VERTICAL);
                TextView title = p.text(filename, 25);
                title.setTypeface(null, Typeface.BOLD);
                title.setSingleLine(true);
                title.setEllipsize(TextUtils.TruncateAt.END);
                copy.addView(title, new LinearLayout.LayoutParams(-1, -2));
                TextView metadata = p.text(language, 21);
                metadata.setTextColor(0xffc9d6e6);
                metadata.setSingleLine(true);
                metadata.setEllipsize(TextUtils.TruncateAt.END);
                metadata.setPadding(0, p.px(6), 0, 0);
                copy.addView(metadata, new LinearLayout.LayoutParams(-1, -2));
                row.addView(copy, new LinearLayout.LayoutParams(0, -2, 1));
                row.addView(p.icon("download", 36));
                row.setContentDescription(filename + ", " + language + ", Download");
                row.setOnClickListener(v -> {
                    if (accepted[0])
                        return;
                    String details = filename + "\n\nLanguage: " + language + "\nSource: OpenSubtitles";
                    if (Boolean.TRUE.equals(item.getMoviehashMatch()))
                        details += "\nMatches this media file's hash";
                    if (item.getRelease() != null && !item.getRelease().trim().isEmpty())
                        details += "\nRelease: " + item.getRelease();
                    PreviewDialog.review(context, "Subtitle Download", details, "Download", () -> {
                        if (accepted[0])
                            return;
                        accepted[0] = true;
                        dialog.dismiss();
                        download.accept(item);
                    });
                });
                rows.addView(row, new LinearLayout.LayoutParams(-1, -2));
                p.rule(rows);
                count++;
            }
            if (count == 0) {
                TextView empty = p.text("No matching subtitles", 28);
                empty.setPadding(0, p.px(22), 0, p.px(22));
                rows.addView(empty);
            }
        };
        search.setContentDescription("Search returned subtitle results");
        search.setOnClickListener(
            v
            -> PreviewTextInput.showValidated(context, "Search for subtitles", query[0], 200, value -> null, value -> {
                query[0] = value.trim();
                search.setText(query[0].isEmpty() ? "Search for subtitles..." : query[0]);
                render.run();
                search.requestFocus();
            }));
        render.run();
        p.show(dialog, panel, p.px(765));
        p.capSideHeight(dialog, panel);
        if (rows.getChildCount() > 0 && rows.getChildAt(0).isFocusable())
            rows.getChildAt(0).requestFocus();
        else
            search.requestFocus();
        return dialog;
    }

    /**
     * The searching (or downloading) state inside the same side panel as the results, instead of a pop-up in the
     * middle of the screen (owner, 4 Nov 2026, 0.155 notes #5, mock-up {@code 0155-checks/05}): a status card with
     * a spinner, grey placeholder rows, and "Back cancels" at the foot. Back or the header arrow calls
     * {@code cancel}.
     */
    public static Dialog working(Context context, String message, String detail, String footer, Runnable cancel) {
        SubtitlePanelStyle p = new SubtitlePanelStyle(context, false, true);
        Dialog dialog = PreviewDialog.create(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        LinearLayout panel = p.panel();
        panel.setTag("subtitles.download.working");
        p.header(panel, "Download Subtitles", dialog::cancel);
        LinearLayout status = new LinearLayout(context);
        status.setTag("subtitles.download.status");
        status.setGravity(Gravity.CENTER_VERTICAL);
        status.setFocusable(true);
        status.setFocusableInTouchMode(true);
        status.setPadding(p.px(22), p.px(18), p.px(22), p.px(18));
        status.setBackground(p.surface(0xff24262b, 0x00000000, 19));
        ProgressBar spinner = new ProgressBar(context);
        spinner.setIndeterminate(true);
        spinner.setIndeterminateTintList(android.content.res.ColorStateList.valueOf(0xffc9d6e6));
        LinearLayout.LayoutParams spinnerSize = new LinearLayout.LayoutParams(p.px(40), p.px(40));
        spinnerSize.rightMargin = p.px(20);
        status.addView(spinner, spinnerSize);
        LinearLayout copy = new LinearLayout(context);
        copy.setOrientation(LinearLayout.VERTICAL);
        TextView title = p.text(message, 25);
        title.setTag("subtitles.download.message");
        title.setTypeface(Typeface.create("sans-serif-medium", Typeface.NORMAL));
        title.setSingleLine(true);
        title.setEllipsize(TextUtils.TruncateAt.END);
        copy.addView(title, new LinearLayout.LayoutParams(-1, -2));
        if (detail != null && !detail.trim().isEmpty()) {
            TextView line = p.text(detail, 21);
            line.setTextColor(0xffc9d6e6);
            line.setSingleLine(true);
            line.setEllipsize(TextUtils.TruncateAt.END);
            line.setPadding(0, p.px(6), 0, 0);
            copy.addView(line, new LinearLayout.LayoutParams(-1, -2));
        }
        status.addView(copy, new LinearLayout.LayoutParams(0, -2, 1));
        panel.addView(status, new LinearLayout.LayoutParams(-1, -2));
        LinearLayout placeholders = new LinearLayout(context);
        placeholders.setOrientation(LinearLayout.VERTICAL);
        placeholders.setPadding(p.px(18), p.px(24), p.px(18), 0);
        for (int i = 0; i < 4; i++) {
            placeholders.addView(bar(context, p, .66f, p.px(16), i == 0 ? 0 : p.px(36)));
            placeholders.addView(bar(context, p, .33f, p.px(12), p.px(10)));
        }
        LinearLayout.LayoutParams body = new LinearLayout.LayoutParams(-1, 0, 1);
        panel.addView(placeholders, body);
        TextView foot = p.text(footer, 20);
        foot.setTag("subtitles.download.footer");
        foot.setTextColor(0xff9aa3b0);
        foot.setPadding(p.px(6), p.px(12), 0, p.px(4));
        panel.addView(foot, new LinearLayout.LayoutParams(-1, -2));
        dialog.setOnCancelListener(d -> cancel.run());
        p.show(dialog, panel, p.px(765));
        p.capSideHeight(dialog, panel);
        status.requestFocus();
        return dialog;
    }

    /** A grey placeholder line, {@code share} of the panel's width. */
    private static View bar(Context context, SubtitlePanelStyle p, float share, int height, int top) {
        View line = new View(context);
        line.setBackground(p.surface(0xff2a2d33, 0x00000000, height / 2));
        LinearLayout.LayoutParams size = new LinearLayout.LayoutParams(Math.round(p.px(700) * share), height);
        size.topMargin = top;
        line.setLayoutParams(size);
        return line;
    }

    private static String safe(String value, String fallback) {
        return value == null || value.trim().isEmpty() ? fallback : value;
    }

    private static String language(OpenSubtitlesSearchResult item) {
        String code = safe(item.getLanguage(), "");
        String name =
            code.isEmpty() ? "Unknown language" : Locale.forLanguageTag(code.replace('_', '-')).getDisplayLanguage();
        if (name.isEmpty() || name.equals(code))
            name = code.isEmpty() ? "Unknown language" : code;
        return name + (Boolean.TRUE.equals(item.getHearingImpaired()) ? " · Hearing impaired" : "");
    }

    private SubtitleResultsPanel() {
    }
}
