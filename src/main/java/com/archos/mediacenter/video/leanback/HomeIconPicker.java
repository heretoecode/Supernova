package com.archos.mediacenter.video.leanback;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * The icon picker for a Home row (page sheet HOME.md; mock-up `home-v2/07`): a grid of icons with their names, five a
 * row, a tick on the current one, no box on tiles at rest, the white box on the focused tile. Automatic first (the
 * first genre chosen gives its icon). OK chooses and returns; Back closes.
 */
final class HomeIconPicker
{
    static final int COLUMNS = 5;
    static final String AUTOMATIC = "Automatic";

    private HomeIconPicker()
    {
    }

    static Dialog show(Context context, String current, Consumer<String> chosen)
    {
        Dialog dialog = PreviewDialog.create(context, "picker");
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        LinearLayout grid = new LinearLayout(context);
        grid.setOrientation(LinearLayout.VERTICAL);
        grid.setTag("home.icon.picker");
        int pad = SupernovaStyle.dp(context, 12);
        grid.setPadding(pad, pad, pad, pad);
        grid.setBackground(SupernovaStyle.glass(context));
        List<String> names = new ArrayList<>();
        names.add(AUTOMATIC);
        for (String genre : PreviewGenres.NAMES)
        {
            names.add(genre);
        }
        String selected = current == null || current.isEmpty() ? AUTOMATIC : current;
        View focus = null;
        LinearLayout line = null;
        for (int i = 0; i < names.size(); i++)
        {
            if (i % COLUMNS == 0)
            {
                line = new LinearLayout(context);
                grid.addView(line, new LinearLayout.LayoutParams(-2, -2));
            }
            String name = names.get(i);
            View tile = tile(context, name, name.equals(selected), () -> {
                dialog.dismiss();
                chosen.accept(AUTOMATIC.equals(name) ? "" : name);
            });
            line.addView(tile, new LinearLayout.LayoutParams(SupernovaStyle.dp(context, 92),
                                                             SupernovaStyle.dp(context, 70)));
            if (name.equals(selected))
            {
                focus = tile;
            }
        }
        dialog.setContentView(grid);
        dialog.show();
        Window window = dialog.getWindow();
        window.setBackgroundDrawableResource(android.R.color.transparent);
        window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        window.setDimAmount(.35f);
        WindowManager.LayoutParams at = window.getAttributes();
        at.gravity = Gravity.CENTER;
        at.width = WindowManager.LayoutParams.WRAP_CONTENT;
        at.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(at);
        if (focus != null)
        {
            focus.requestFocus();
        }
        return dialog;
    }

    private static View tile(Context context, String name, boolean current, Runnable choose)
    {
        FrameLayout tile = new FrameLayout(context);
        tile.setTag("home.icon:" + name);
        tile.setFocusable(true);
        tile.setFocusableInTouchMode(true);
        tile.setClickable(true);
        tile.setContentDescription(name + (current ? ", chosen" : ""));
        LinearLayout stack = new LinearLayout(context);
        stack.setOrientation(LinearLayout.VERTICAL);
        stack.setGravity(Gravity.CENTER);
        ImageView mark = new ImageView(context);
        Drawable icon = PreviewGenres.known(name) ? new PreviewGenres.Icon(name) : new PreviewIcon("recommendation");
        mark.setImageDrawable(icon);
        stack.addView(mark, new LinearLayout.LayoutParams(SupernovaStyle.dp(context, 20),
                                                          SupernovaStyle.dp(context, 20)));
        TextView label = new TextView(context);
        label.setText(name);
        label.setTextSize(12);
        label.setSingleLine(true);
        label.setGravity(Gravity.CENTER);
        label.setTextColor(Color.WHITE);
        LinearLayout.LayoutParams labelSize = new LinearLayout.LayoutParams(-2, -2);
        labelSize.topMargin = SupernovaStyle.dp(context, 6);
        stack.addView(label, labelSize);
        tile.addView(stack, new FrameLayout.LayoutParams(-1, -1));
        ImageView tick = new ImageView(context);
        tick.setImageDrawable(new PreviewIcon("check"));
        tick.setVisibility(current ? View.VISIBLE : View.GONE);
        int tickSize = SupernovaStyle.dp(context, 12);
        FrameLayout.LayoutParams tickAt = new FrameLayout.LayoutParams(tickSize, tickSize, Gravity.TOP | Gravity.END);
        tickAt.setMargins(0, SupernovaStyle.dp(context, 6), SupernovaStyle.dp(context, 6), 0);
        tile.addView(tick, tickAt);
        tile.setOnFocusChangeListener((v, focused) -> {
            GradientDrawable box = new GradientDrawable();
            box.setCornerRadius(SupernovaStyle.dp(context, 10));
            box.setColor(focused ? SupernovaStyle.FOCUS_FILL : Color.TRANSPARENT);
            tile.setBackground(box);
            int ink = focused ? SupernovaStyle.FOCUS_TEXT : Color.WHITE;
            label.setTextColor(ink);
            android.graphics.PorterDuffColorFilter tint =
                new android.graphics.PorterDuffColorFilter(ink, android.graphics.PorterDuff.Mode.SRC_IN);
            mark.getDrawable().setColorFilter(tint);
            tick.getDrawable().setColorFilter(tint);
        });
        tile.setOnClickListener(v -> choose.run());
        return tile;
    }
}
