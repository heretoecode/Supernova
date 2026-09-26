package com.archos.mediacenter.video.leanback;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.view.*;
import android.widget.*;
import com.archos.mediacenter.video.browser.adapters.object.Video;
import java.util.List;
import java.util.function.Consumer;

/** Selection stays open; the current file and keyboard focus are independent states. */
public final class PreviewVersionsDialog {
    public static Dialog show(Activity activity, List<Video> versions, Video current, Consumer<Video> select) {
        View anchor = activity.getCurrentFocus();
        Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        LinearLayout panel = new LinearLayout(activity);
        panel.setOrientation(LinearLayout.VERTICAL);
        int pad = PreviewDialog.dp(activity, 16);
        panel.setPadding(pad, pad, pad, pad);
        panel.setBackground(PreviewDialog.menuSurface(activity));
        TextView title = text(activity, "Versions", 19);
        panel.addView(title);
        ScrollView scroll = new ScrollView(activity);
        LinearLayout rows = new LinearLayout(activity);
        rows.setOrientation(LinearLayout.VERTICAL);
        scroll.addView(rows);
        panel.addView(scroll, new LinearLayout.LayoutParams(-1, 0, 1));
        TextView[] markers = new TextView[versions.size()];
        View[] choices = new View[versions.size()];
        int initial = 0;
        for (int i = 0; i < versions.size(); i++) {
            final int index = i;
            Video video = versions.get(i);
            LinearLayout row = new LinearLayout(activity);
            row.setGravity(Gravity.CENTER_VERTICAL);
            row.setPadding(pad, pad / 2, pad, pad / 2);
            row.setFocusable(true);
            row.setFocusableInTouchMode(true);
            row.setBackground(PreviewDialog.focus(activity));
            row.setTag("version:" + video.getId());
            TextView facts = text(activity, PreviewVariants.details(activity, video), 13);
            row.addView(facts, new LinearLayout.LayoutParams(0, -2, 1));
            TextView marker = text(activity, "✓ Current", 13);
            marker.setGravity(Gravity.RIGHT);
            markers[i] = marker;
            marker.setVisibility(video.getId() == current.getId() ? View.VISIBLE : View.INVISIBLE);
            row.addView(marker, new LinearLayout.LayoutParams(PreviewDialog.dp(activity, 92), -2));
            if (video.getId() == current.getId()) initial = i;
            row.setContentDescription(facts.getText() + (marker.getVisibility() == View.VISIBLE ? ", Current" : ""));
            row.setOnClickListener(v -> {
                select.accept(video);
                for (int n = 0; n < markers.length; n++) {
                    markers[n].setVisibility(n == index ? View.VISIBLE : View.INVISIBLE);
                    choices[n].setContentDescription(PreviewVariants.details(activity, versions.get(n)) + (n == index ? ", Current" : ""));
                }
                row.requestFocus();
            });
            choices[i] = row;
            rows.addView(row, new LinearLayout.LayoutParams(-1, -2));
        }
        dialog.setContentView(panel);
        dialog.setOnDismissListener(d -> { if (anchor != null && anchor.isAttachedToWindow() && anchor.isShown()) anchor.requestFocus(); });
        dialog.show();
        Window window = dialog.getWindow();
        window.setBackgroundDrawableResource(android.R.color.transparent);
        window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        window.setDimAmount(.32f);
        window.setLayout(Math.min(PreviewDialog.dp(activity, 760), activity.getResources().getDisplayMetrics().widthPixels - pad * 4),
                Math.min(PreviewDialog.dp(activity, 80 + versions.size() * 105), activity.getResources().getDisplayMetrics().heightPixels - pad * 4));
        if (choices.length > 0) choices[initial].requestFocus();
        return dialog;
    }

    private static TextView text(Activity activity, String value, int size) {
        TextView text = new TextView(activity);
        text.setText(value);
        text.setTextSize(size);
        text.setTextColor(Color.WHITE);
        return text;
    }

    private PreviewVersionsDialog() { }
}
