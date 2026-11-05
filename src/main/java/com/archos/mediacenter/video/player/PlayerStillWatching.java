package com.archos.mediacenter.video.player;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.preference.PreferenceManager;
import com.archos.mediacenter.video.browser.adapters.object.Episode;
import com.archos.mediacenter.video.leanback.PreviewIcon;
import com.archos.mediacenter.video.leanback.SupernovaStyle;
import com.archos.mediacenter.video.leanback.TvButton;

/**
 * "Still watching?" (0.156 #39, mock-up {@code docs/design/proposals/suggestions/03}): after the set number of
 * auto-played episodes the engine holds the next one and asks. Keep watching is focused; Stop or Back stops; with
 * no answer the engine stops after 60 seconds and the card goes.
 */
final class PlayerStillWatching
{
    static final String TAG = "player.still_watching";

    /** The engine's question, as the card sees it; the player supplies it, tests a fixture. */
    interface Question
    {
        boolean pending();

        void answer(boolean keepWatching);

        String title();

        String next();

        int episodes();
    }

    private final Activity activity;
    private final Question question;
    private final Handler handler = new Handler(Looper.getMainLooper());
    private View card;
    private TextView keep;
    private TextView stop;
    private boolean stopped;

    PlayerStillWatching(Activity activity, Question question)
    {
        this.activity = activity;
        this.question = question;
        handler.post(watch);
    }

    private final Runnable watch = new Runnable()
    {
        @Override
        public void run()
        {
            if (stopped)
                return;
            check();
            handler.postDelayed(this, 500);
        }
    };

    /** Shows or removes the card to match the engine (also called by tests). */
    void check()
    {
        boolean pending = question.pending();
        if (pending && card == null)
            show();
        else if (!pending && card != null)
            remove();
    }

    boolean showing()
    {
        return card != null;
    }

    /** While the card is up it owns the remote: Left/Right between the buttons, OK presses, Back stops. */
    boolean handleKey(KeyEvent event)
    {
        if (card == null)
            return false;
        int key = event.getKeyCode();
        if (event.getAction() != KeyEvent.ACTION_DOWN)
            return true;
        if (key == KeyEvent.KEYCODE_DPAD_LEFT)
            keep.requestFocus();
        else if (key == KeyEvent.KEYCODE_DPAD_RIGHT)
            stop.requestFocus();
        else if (key == KeyEvent.KEYCODE_DPAD_CENTER || key == KeyEvent.KEYCODE_ENTER)
            answer(!stop.hasFocus());
        else if (key == KeyEvent.KEYCODE_BACK)
            answer(false);
        return true;
    }

    void stop()
    {
        stopped = true;
        handler.removeCallbacksAndMessages(null);
        remove();
    }

    private void answer(boolean keepWatching)
    {
        remove();
        question.answer(keepWatching);
    }

    private void show()
    {
        ViewGroup content = activity.findViewById(android.R.id.content);
        if (content == null)
            return;
        LinearLayout box = new LinearLayout(activity);
        box.setTag(TAG);
        box.setOrientation(LinearLayout.VERTICAL);
        box.setGravity(Gravity.CENTER_HORIZONTAL);
        box.setPadding(dp(32), dp(22), dp(32), dp(20));
        GradientDrawable bg = new GradientDrawable();
        bg.setColor(SupernovaStyle.GLASS);
        bg.setCornerRadius(dp(16));
        bg.setStroke(Math.max(1, dp(1)), SupernovaStyle.GLASS_EDGE);
        box.setBackground(bg);
        box.setElevation(dp(12));
        String title = question.title();
        box.addView(text(title.isEmpty() ? "Still watching?" : "Still watching " + title + "?", 22, Typeface.BOLD,
                         SupernovaStyle.TEXT));
        String next = question.next();
        String line = "You've watched " + question.episodes() + " episodes in a row." +
                      (next.isEmpty() ? "" : " Next: " + next);
        TextView detail = text(line, 13, Typeface.NORMAL, SupernovaStyle.TEXT_SECONDARY);
        detail.setPadding(0, dp(6), 0, dp(14));
        box.addView(detail);
        LinearLayout buttons = new LinearLayout(activity);
        buttons.setGravity(Gravity.CENTER);
        keep = TvButton.create(activity, "Keep watching", () -> answer(true), false);
        PreviewIcon.apply(keep, "play", dp(16));
        stop = TvButton.create(activity, "Stop", () -> answer(false), false);
        buttons.addView(keep);
        LinearLayout.LayoutParams gap = new LinearLayout.LayoutParams(-2, -2);
        gap.leftMargin = dp(12);
        buttons.addView(stop, gap);
        box.addView(buttons);
        TextView note = text("If nothing is pressed, playback stops in 60 seconds", 11, Typeface.NORMAL,
                             SupernovaStyle.TEXT_SECONDARY);
        note.setPadding(0, dp(14), 0, 0);
        box.addView(note);
        card = box;
        content.addView(card, new FrameLayout.LayoutParams(-2, -2, Gravity.CENTER));
        keep.requestFocus();
    }

    private void remove()
    {
        View gone = card;
        card = null;
        if (gone != null && gone.getParent() instanceof ViewGroup)
            ((ViewGroup)gone.getParent()).removeView(gone);
    }

    private TextView text(String value, int size, int style, int color)
    {
        TextView text = new TextView(activity);
        text.setText(value);
        text.setTextSize(size);
        text.setTypeface(Typeface.create("sans-serif", style));
        text.setTextColor(color);
        text.setGravity(Gravity.CENTER);
        text.setShadowLayer(0, 0, 0, Color.TRANSPARENT);
        return text;
    }

    private int dp(int value)
    {
        return Math.round(value * activity.getResources().getDisplayMetrics().density);
    }

    /** The running player's question. */
    static Question fromPlayer(PlayerActivity activity)
    {
        return new Question()
        {
            @Override
            public boolean pending()
            {
                PlayerService service = PlayerService.sPlayerService;
                return service != null && service.playbackLimits().questionPending();
            }

            @Override
            public void answer(boolean keepWatching)
            {
                PlayerService service = PlayerService.sPlayerService;
                if (service != null)
                    service.playbackLimits().answerStillWatching(keepWatching);
            }

            @Override
            public String title()
            {
                return activity.previewTitle();
            }

            @Override
            public String next()
            {
                PlayerService service = PlayerService.sPlayerService;
                Episode next = service == null ? null : service.previewAdjacentEpisode(1);
                if (next == null)
                    return "";
                return "Season " + next.getSeasonNumber() + " · Episode " + next.getEpisodeNumber();
            }

            @Override
            public int episodes()
            {
                return PreferenceManager.getDefaultSharedPreferences(activity)
                    .getInt(PlaybackLimits.KEY_EPISODES, PlaybackLimits.DEFAULT_EPISODES);
            }
        };
    }
}
