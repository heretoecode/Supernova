package com.archos.mediacenter.video.player;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Handler;
import android.os.Looper;
import androidx.preference.PreferenceManager;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.archos.mediacenter.video.leanback.SupernovaStyle;

/**
 * Supernova's own first-play tip (0.156 #22, mock-up {@code docs/design/proposals/chapters/05}). It replaces NOVA's
 * blue "Press UP" box: a small charcoal card on the first video only, explaining the remote. It goes after eight
 * seconds or at the first press, and that press still does its normal job.
 */
final class PlayerFirstPlayTip
{
    static final String SHOWN_KEY = "supernova_first_play_tip_shown";
    static final long SHOW_MS = 8000;
    static final long PLAYING_BEFORE_TIP_MS = 1000;
    static final String TAG = "player.first_play_tip";

    private final Activity activity;
    private final Handler handler = new Handler(Looper.getMainLooper());
    private View card;
    private long playingSince = -1;
    private boolean finished;

    PlayerFirstPlayTip(Activity activity)
    {
        this.activity = activity;
        finished = alreadyShown(PreferenceManager.getDefaultSharedPreferences(activity));
        if (!finished)
            handler.post(watch);
    }

    static boolean alreadyShown(SharedPreferences prefs)
    {
        return prefs.getBoolean(SHOWN_KEY, false);
    }

    private final Runnable watch = new Runnable()
    {
        @Override
        public void run()
        {
            if (finished)
                return;
            Player player = Player.sPlayer;
            long now = android.os.SystemClock.elapsedRealtime();
            boolean playing = player != null && player.isPlaying();
            if (!playing)
                playingSince = -1;
            else if (playingSince < 0)
                playingSince = now;
            if (playing && now - playingSince >= PLAYING_BEFORE_TIP_MS) {
                show();
                return;
            }
            handler.postDelayed(this, 500);
        }
    };

    /** Shows the card now (also used by tests). Marks the tip as seen so it never appears again. */
    void show()
    {
        if (card != null)
            return;
        finished = true;
        handler.removeCallbacks(watch);
        PreferenceManager.getDefaultSharedPreferences(activity).edit().putBoolean(SHOWN_KEY, true).apply();
        ViewGroup content = activity.findViewById(android.R.id.content);
        if (content == null)
            return;
        card = build();
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(-2, -2, Gravity.CENTER);
        lp.bottomMargin = dp(140);
        content.addView(card, lp);
        card.setAlpha(0f);
        card.animate().alpha(1f).setDuration(200).start();
        handler.postDelayed(this::dismiss, SHOW_MS);
    }

    boolean showing()
    {
        return card != null;
    }

    /** Any press dismisses the tip; the press is not consumed, so it still seeks, pauses or moves focus. */
    boolean handleKey(KeyEvent event)
    {
        if (card != null && event.getAction() == KeyEvent.ACTION_DOWN)
            dismiss();
        return false;
    }

    void dismiss()
    {
        handler.removeCallbacksAndMessages(null);
        View gone = card;
        card = null;
        if (gone == null)
            return;
        gone.animate().cancel();
        if (gone.getParent() instanceof ViewGroup)
            ((ViewGroup)gone.getParent()).removeView(gone);
    }

    void stop()
    {
        finished = true;
        dismiss();
    }

    private View build()
    {
        LinearLayout box = new LinearLayout(activity);
        box.setTag(TAG);
        box.setOrientation(LinearLayout.VERTICAL);
        box.setPadding(dp(18), dp(14), dp(18), dp(14));
        GradientDrawable bg = new GradientDrawable();
        bg.setColor(SupernovaStyle.GLASS);
        bg.setCornerRadius(dp(Math.round(SupernovaStyle.PANEL_RADIUS_DP)));
        box.setBackground(bg);
        box.setElevation(dp(8));
        LinearLayout first = row();
        first.addView(chip("‹ ›"));
        first.addView(label("Seek"));
        TextView dot = label("·");
        dot.setTextColor(SupernovaStyle.TEXT_SECONDARY);
        first.addView(dot);
        first.addView(chip("OK"));
        first.addView(label("Play / pause"));
        box.addView(first);
        LinearLayout second = row();
        second.addView(chip("↑"));
        second.addView(label("Play/pause and Settings buttons"));
        box.addView(second);
        LinearLayout third = row();
        third.addView(chip("↓"));
        third.addView(label("Chapters and Episodes"));
        box.addView(third);
        return box;
    }

    private LinearLayout row()
    {
        LinearLayout row = new LinearLayout(activity);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setGravity(Gravity.CENTER_VERTICAL);
        row.setPadding(0, dp(4), 0, dp(4));
        return row;
    }

    private TextView chip(String keys)
    {
        TextView chip = new TextView(activity);
        chip.setText(keys);
        chip.setTextColor(Color.WHITE);
        chip.setTextSize(11);
        chip.setTypeface(Typeface.create("sans-serif-medium", Typeface.NORMAL));
        chip.setGravity(Gravity.CENTER);
        chip.setMinWidth(dp(28));
        chip.setPadding(dp(6), dp(3), dp(6), dp(3));
        GradientDrawable bg = new GradientDrawable();
        bg.setColor(SupernovaStyle.PANEL_REST);
        bg.setCornerRadius(dp(6));
        chip.setBackground(bg);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-2, -2);
        lp.rightMargin = dp(8);
        chip.setLayoutParams(lp);
        return chip;
    }

    private TextView label(String text)
    {
        TextView label = new TextView(activity);
        label.setText(text);
        label.setTextColor(SupernovaStyle.TEXT);
        label.setTextSize(12);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-2, -2);
        lp.rightMargin = dp(10);
        label.setLayoutParams(lp);
        return label;
    }

    private int dp(int value)
    {
        return Math.round(value * activity.getResources().getDisplayMetrics().density);
    }
}
