package com.archos.mediacenter.video.leanback;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * An instant action's notice with Undo (RULES §7: "Undo notice instead of are you sure?"): one line at the foot of
 * the screen, the Undo button focused, gone by itself after a few seconds; Back closes it and focus returns to the
 * item you pressed.
 */
final class UndoNotice
{
    static final long SHOWN_MS = 6000;

    private UndoNotice()
    {
    }

    static Dialog show(Context context, String message, Runnable undo)
    {
        Dialog dialog = PreviewDialog.create(context, "notice");
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        LinearLayout line = new LinearLayout(context);
        line.setTag("undo.notice");
        line.setGravity(Gravity.CENTER_VERTICAL);
        int pad = SupernovaStyle.dp(context, 10);
        line.setPadding(pad + pad / 2, pad / 2, pad / 2, pad / 2);
        line.setBackground(SupernovaStyle.glass(context));
        ImageView mark = new ImageView(context);
        mark.setImageDrawable(new PreviewIcon("check"));
        line.addView(mark, new LinearLayout.LayoutParams(SupernovaStyle.dp(context, 16), SupernovaStyle.dp(context, 16)));
        TextView words = new TextView(context);
        words.setText(message);
        words.setTextColor(Color.WHITE);
        words.setTextSize(14);
        words.setSingleLine(true);
        LinearLayout.LayoutParams wordsSize = new LinearLayout.LayoutParams(-2, -2);
        wordsSize.leftMargin = pad;
        wordsSize.rightMargin = pad * 2;
        line.addView(words, wordsSize);
        TextView button = TvButton.create(context, "Undo", () -> {
            dialog.dismiss();
            undo.run();
        }, false);
        PreviewIcon.apply(button, "history", 14);
        button.setTag("undo.notice.undo");
        line.addView(button, new LinearLayout.LayoutParams(-2, -2));
        dialog.setContentView(line);
        dialog.show();
        Window window = dialog.getWindow();
        window.setBackgroundDrawableResource(android.R.color.transparent);
        window.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        WindowManager.LayoutParams at = window.getAttributes();
        at.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
        at.y = SupernovaStyle.dp(context, 28);
        at.width = WindowManager.LayoutParams.WRAP_CONTENT;
        at.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(at);
        button.requestFocus();
        line.postDelayed(() -> {
            if (dialog.isShowing())
            {
                dialog.dismiss();
            }
        }, SHOWN_MS);
        return dialog;
    }
}
