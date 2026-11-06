package com.archos.mediacenter.video.leanback.filebrowsing;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.archos.mediacenter.video.leanback.PreviewUtilityBackground;
import com.archos.mediacenter.video.leanback.SupernovaStyle;
import com.archos.mediacenter.video.metadata.UnlibraryLookup;

/**
 * Details for a file that is not in the library (owner decision 4 Nov 2026, 0.155 notes #13): looks the film or show
 * up online from its file name and shows it with Play and Add to library and a "Not in your library yet" line; with no
 * match, "Couldn't identify this file". Nothing is saved or added until Add to library is pressed.
 */
final class UnlibraryDetails
{
    private UnlibraryDetails()
    {
    }

    static Dialog show(Context context, Uri file, String fileName, Runnable play, Runnable add)
    {
        Dialog dialog = new Dialog(context, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        FrameLayout root = new FrameLayout(context);
        root.setBackground(new PreviewUtilityBackground(context));
        ImageView backdrop = new ImageView(context);
        backdrop.setScaleType(ImageView.ScaleType.CENTER_CROP);
        backdrop.setAlpha(.35f);
        root.addView(backdrop, new FrameLayout.LayoutParams(-1, -1));
        LinearLayout page = new LinearLayout(context);
        page.setGravity(Gravity.CENTER_VERTICAL);
        page.setPadding(dp(context, 64), dp(context, 40), dp(context, 64), dp(context, 40));
        root.addView(page, new FrameLayout.LayoutParams(-1, -1));
        ImageView poster = new ImageView(context);
        poster.setScaleType(ImageView.ScaleType.CENTER_CROP);
        poster.setBackgroundColor(0xFF2A2D33);
        page.addView(poster, new LinearLayout.LayoutParams(dp(context, 180), dp(context, 270)));
        LinearLayout words = new LinearLayout(context);
        words.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams wordsSize = new LinearLayout.LayoutParams(0, -2, 1);
        wordsSize.leftMargin = dp(context, 36);
        page.addView(words, wordsSize);
        TextView kind = text(context, "Looking this file up…", 14, SupernovaStyle.TEXT_SECONDARY);
        kind.setTag("unlibrary.kind");
        TextView title = text(context, fileName, 30, Color.WHITE);
        title.setTypeface(SupernovaStyle.LABEL);
        title.setTag("unlibrary.title");
        TextView line = text(context, "", 15, SupernovaStyle.TEXT_SECONDARY);
        line.setTag("unlibrary.line");
        words.addView(kind);
        words.addView(title);
        words.addView(line);
        LinearLayout buttons = new LinearLayout(context);
        LinearLayout.LayoutParams buttonsSize = new LinearLayout.LayoutParams(-2, -2);
        buttonsSize.topMargin = dp(context, 22);
        words.addView(buttons, buttonsSize);
        TextView playButton = button(context, "Play", "play", () -> {
            dialog.dismiss();
            play.run();
        });
        TextView addButton = button(context, "Add to library", "library", () -> {
            dialog.dismiss();
            add.run();
        });
        buttons.addView(playButton);
        LinearLayout.LayoutParams second = new LinearLayout.LayoutParams(-2, -2);
        second.leftMargin = dp(context, 12);
        buttons.addView(addButton, second);
        TextView fileLine = text(context, fileName, 12, SupernovaStyle.TEXT_SECONDARY);
        LinearLayout.LayoutParams fileSize = new LinearLayout.LayoutParams(-2, -2);
        fileSize.topMargin = dp(context, 18);
        words.addView(fileLine, fileSize);
        dialog.setContentView(root, new ViewGroup.LayoutParams(-1, -1));
        UnlibraryLookup.Job job = UnlibraryLookup.start(context, file, root::post, result -> {
            if (!dialog.isShowing())
                return;
            bind(result, kind, title, line, poster, backdrop);
        });
        dialog.setOnDismissListener(d -> job.cancel());
        dialog.show();
        playButton.requestFocus();
        return dialog;
    }

    static void bind(UnlibraryLookup.Result result, TextView kind, TextView title, TextView line, ImageView poster,
                     ImageView backdrop)
    {
        if (result == null || result.confidence == UnlibraryLookup.Confidence.NONE || result.title.isEmpty()) {
            kind.setText("Not in your library");
            line.setText("Couldn't identify this file. You can still play it, or add it and fix the match later.");
            return;
        }
        kind.setText(kindLabel(result));
        title.setText(result.title);
        boolean guess = result.confidence == UnlibraryLookup.Confidence.POSSIBLE;
        String sure = guess ? " · Best guess from the file name" : "";
        line.setText(UnlibraryDetailsWords.detail(result.year, result.season, result.episode) + sure
            + "\nNot in your library yet");
        load(result.poster, poster);
        load(result.backdrop, backdrop);
    }

    private static String kindLabel(UnlibraryLookup.Result result)
    {
        switch (result.kind) {
            case FILM:
                return "Film";
            case EPISODE:
                return "Episode";
            case SHOW:
                return "TV series";
            default:
                return "Video";
        }
    }

    private static void load(Uri image, ImageView into)
    {
        if (image == null)
            return;
        try {
            com.squareup.picasso.Picasso.get().load(image).fit().centerCrop().into(into);
        } catch (RuntimeException unavailable) {
            // No picture loader yet: the plain card stays.
        }
    }

    private static TextView button(Context context, String label, String icon, Runnable run)
    {
        TextView view = new TextView(context);
        view.setText(label);
        view.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
        view.setFocusable(true);
        view.setFocusableInTouchMode(true);
        view.setClickable(true);
        view.setTag("unlibrary.action." + icon);
        com.archos.mediacenter.video.leanback.PreviewIcon.apply(view, icon, 18);
        SupernovaStyle.styleButton(view, SupernovaStyle.Surface.STANDARD, true);
        view.setOnClickListener(v -> run.run());
        return view;
    }

    private static TextView text(Context context, String value, float sp, int colour)
    {
        TextView view = new TextView(context);
        view.setText(value);
        view.setTextSize(TypedValue.COMPLEX_UNIT_SP, sp);
        view.setTextColor(colour);
        view.setPadding(0, dp(context, 3), 0, dp(context, 3));
        return view;
    }

    private static int dp(Context context, int value)
    {
        return Math.round(value * context.getResources().getDisplayMetrics().density);
    }
}
