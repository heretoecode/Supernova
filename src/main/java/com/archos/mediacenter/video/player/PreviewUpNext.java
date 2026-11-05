package com.archos.mediacenter.video.player;
import android.app.Activity;
import android.graphics.drawable.GradientDrawable;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.view.*;
import android.widget.*;
import com.archos.mediacenter.video.browser.adapters.object.Episode;
import com.archos.mediacenter.video.leanback.*;
import com.archos.mediacenter.utils.introdb.IntroSegments;
import com.archos.mediacenter.video.leanback.SupernovaStyle;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

/** In-player presentation over the existing next-episode engine; never fabricates completion. */
final class PreviewUpNext {
    private final Activity activity;
    private final Handler handler = new Handler(Looper.getMainLooper());
    private TextView skipPill;
    private long skipExpires;
    private View skipOrigin;
    private LinearLayout panel;
    private TextView countdown;
    private long episodeId = -1, deadline;
    private boolean dismissed;
    private long lastTick;
    private View previousFocus;
    private int consumeKeyUp = -1;

    PreviewUpNext(Activity activity) {
        this.activity = activity;
        handler.post(tick);
    }

    static long trigger(long duration, IntroSegments segments) {
        return Math.max(0, duration - 5000);
    }

    private final Runnable tick = new Runnable() {
        @Override
        public void run() {
            PlayerService service = PlayerService.sPlayerService;
            Player player = Player.sPlayer;
            long now = SystemClock.elapsedRealtime();
            segmentPill(service);
            if (service != null && player != null && service.getVideoInfo() != null) {
                long current = service.getVideoInfo().id;
                if (current != episodeId) {
                    remove();
                    episodeId = current;
                    dismissed = false;
                }
                Episode next = service.previewAdjacentEpisode(1);
                long duration = player.getDuration(), position = player.getCurrentPosition();
                if (!dismissed && next != null && service.previewAutoNextEnabled() && duration > 0 &&
                    position >= trigger(duration, service.getIntroSegments())) {
                    if (panel == null)
                        show(next, service);
                    if (!player.isPlaying() && lastTick > 0)
                        deadline += now - lastTick;
                    long seconds = Math.max(0, (deadline - now + 999) / 1000);
                    countdown.setText("UP NEXT  ·  Starting in " + seconds + "s");
                    if (seconds == 0) {
                        dismissed = true;
                        remove();
                        service.previewNavigateEpisode(1);
                    }
                } else if (panel != null) {
                    remove();
                }
            }
            lastTick = now;
            handler.postDelayed(this, 500);
        }
    };

    private void show(Episode next, PlayerService service) {
        deadline = SystemClock.elapsedRealtime() + 5000;
        panel = new LinearLayout(activity);
        panel.setOrientation(LinearLayout.VERTICAL);
        panel.setPadding(dp(16), dp(12), dp(16), dp(12));
        GradientDrawable surface = PreviewDialog.surface(activity, false);
        surface.setColor(0xec08121b);
        surface.setCornerRadius(dp(10));
        panel.setBackground(surface);
        panel.setElevation(dp(8));
        countdown = text("Up Next", 15);
        countdown.setTextColor(PreviewAccent.color(activity));
        panel.addView(countdown);
        if (next.getPictureUri() != null) {
            ImageView still = new ImageView(activity);
            still.setScaleType(ImageView.ScaleType.CENTER_CROP);
            LinearLayout.LayoutParams frame = new LinearLayout.LayoutParams(-1, dp(90));
            frame.topMargin = dp(8);
            panel.addView(still, frame);
            Picasso.get()
                .load(next.getPictureUri())
                .resize(dp(283), dp(90))
                .centerCrop()
                .networkPolicy(NetworkPolicy.OFFLINE)
                .noFade()
                .into(still);
        }
        TextView name =
            text("S" + next.getSeasonNumber() + " E" + next.getEpisodeNumber() + " · " + next.getEpisodeName(), 14);
        name.setMaxLines(2);
        name.setPadding(0, dp(8), 0, dp(8));
        panel.addView(name);
        LinearLayout actions = new LinearLayout(activity);
        for (String label : new String[] {"Play Now"}) {
            TextView button = text(label, 14);
            button.setFocusable(true);
            button.setBackground(PreviewDialog.focus(activity));
            button.setPadding(dp(12), dp(8), dp(12), dp(8));
            button.setOnClickListener(v -> {
                dismissed = true;
                remove();
                if (label.equals("Play Now"))
                    service.previewNavigateEpisode(1);
                else
                    service.previewCancelAutoNext();
            });
            actions.addView(button);
        }
        panel.addView(actions);
        FrameLayout root = activity.findViewById(android.R.id.content);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(dp(315), -2, Gravity.BOTTOM | Gravity.END);
        params.setMargins(dp(26), dp(20), dp(26), dp(24));
        root.addView(panel, params);
    }

    static final long SKIP_SHOWN_MS = 10_000;
    private PlayerService.SegmentPrompt skipPart;
    private long skipShownAt;
    private boolean skipDismissed, skipWithControls, skipWaitForControls;
    private android.animation.ValueAnimator skipDrain;

    /**
     * Owner-approved Skip button (notes #17, mock-up skip-button/05): white, bottom right, selected when it appears
     * so one OK skips; the white drains to light grey over 10 seconds, then it fades; opening the controls brings
     * it back (selected, above them) until the part ends.
     */
    private void segmentPill(PlayerService service) {
        PlayerService.SegmentPrompt live = service == null ? null : service.segmentPrompt();
        long now = SystemClock.elapsedRealtime();
        if (live != null && (skipPart == null || live.target != skipPart.target)) {
            skipPart = live;
            skipShownAt = now;
            skipDismissed = false;
        }
        Player player = Player.sPlayer;
        boolean partRunning = skipPart != null && service != null && player != null &&
            service.segmentSkippingEnabled() && player.getCurrentPosition() < skipPart.target;
        if (!partRunning) {
            skipPart = null;
            removeSkip();
            return;
        }
        long left = SKIP_SHOWN_MS - (now - skipShownAt);
        boolean fresh = left > 0 && !skipDismissed;
        boolean controls = PlayerController.isControlBarShowing();
        if (!controls)
            skipWaitForControls = false;
        if (skipWaitForControls || !fresh && !controls) {
            removeSkip();
            return;
        }
        if (skipPill == null)
            showSkip(service, fresh ? left : 0);
        if (skipWithControls != controls)
            placeSkip(controls);
    }

    private void showSkip(PlayerService service, long left) {
        removeSkip();
        PlayerService.SegmentPrompt part = skipPart;
        skipPill = new TextView(activity);
        skipPill.setTag("playback.segment.skip");
        skipPill.setText("Skip " + PlayerService.introLabels(activity).get(part.type));
        skipPill.setTextColor(SupernovaStyle.FOCUS_TEXT);
        skipPill.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, dp(22));
        skipPill.setTypeface(SupernovaStyle.LABEL);
        skipPill.setIncludeFontPadding(false);
        skipPill.setGravity(Gravity.CENTER);
        skipPill.setSingleLine(true);
        skipPill.setPadding(dp(20), 0, dp(26), 0);
        PreviewIcon icon = new PreviewIcon("next");
        icon.setColorFilter(new android.graphics.PorterDuffColorFilter(SupernovaStyle.FOCUS_TEXT,
                                                                       android.graphics.PorterDuff.Mode.SRC_IN));
        icon.setBounds(0, 0, dp(23), dp(23));
        skipPill.setCompoundDrawables(icon, null, null, null);
        skipPill.setCompoundDrawablePadding(dp(10));
        SkipDrain drain = new SkipDrain(dp(17));
        drain.fraction = left > 0 ? (float)left / SKIP_SHOWN_MS : 1f;
        skipPill.setBackground(drain);
        skipPill.setElevation(dp(5));
        skipPill.setFocusable(true);
        skipPill.setFocusableInTouchMode(true);
        skipPill.setOnClickListener(v -> skip(part));
        if (left > 0) {
            skipDrain = android.animation.ValueAnimator.ofFloat(drain.fraction, 0f);
            skipDrain.setDuration(left);
            skipDrain.setInterpolator(new android.view.animation.LinearInterpolator());
            skipDrain.addUpdateListener(a -> {
                drain.fraction = (float)a.getAnimatedValue();
                drain.invalidateSelf();
            });
            skipDrain.start();
        }
        FrameLayout root = activity.findViewById(android.R.id.content);
        skipWithControls = !PlayerController.isControlBarShowing();
        root.addView(skipPill, new FrameLayout.LayoutParams(-2, dp(60), Gravity.END | Gravity.BOTTOM));
        placeSkip(PlayerController.isControlBarShowing());
        skipOrigin = activity.getCurrentFocus();
        skipPill.requestFocus();
    }

    private void placeSkip(boolean controls) {
        skipWithControls = controls;
        if (skipPill == null)
            return;
        FrameLayout.LayoutParams frame = (FrameLayout.LayoutParams)skipPill.getLayoutParams();
        frame.setMargins(dp(24), dp(20), dp(48), controls ? dp(135) : dp(55));
        skipPill.setLayoutParams(frame);
    }

    private void skip(PlayerService.SegmentPrompt part) {
        Player player = Player.sPlayer;
        if (part != null && player != null && PlayerService.isSafeAutoSkipTarget(part.target, player.getDuration()))
            player.seekTo((int)part.target);
        skipPart = null;
        removeSkip();
    }

    /** White that drains away to light grey as the time runs out (owner's choice, option B). */
    static final class SkipDrain extends android.graphics.drawable.Drawable {
        private final android.graphics.Paint paint = new android.graphics.Paint(android.graphics.Paint.ANTI_ALIAS_FLAG);
        private final float radius;
        float fraction = 1f;

        SkipDrain(float radius) {
            this.radius = radius;
        }

        @Override
        public void draw(android.graphics.Canvas canvas) {
            android.graphics.Rect b = getBounds();
            android.graphics.RectF box = new android.graphics.RectF(b);
            paint.setColor(0xFFAEB4BF);
            canvas.drawRoundRect(box, radius, radius, paint);
            int saved = canvas.save();
            canvas.clipRect(b.left, b.top, b.left + b.width() * Math.max(0f, Math.min(1f, fraction)), b.bottom);
            paint.setColor(SupernovaStyle.FOCUS_FILL);
            canvas.drawRoundRect(box, radius, radius, paint);
            canvas.restoreToCount(saved);
        }

        @Override
        public void setAlpha(int alpha) {
            paint.setAlpha(alpha);
        }

        @Override
        public void setColorFilter(android.graphics.ColorFilter filter) {
            paint.setColorFilter(filter);
        }

        @Override
        public int getOpacity() {
            return android.graphics.PixelFormat.TRANSLUCENT;
        }
    }

    private void removeSkip() {
        if (skipDrain != null) {
            skipDrain.cancel();
            skipDrain = null;
        }
        if (skipPill != null) {
            boolean restore = skipPill.hasFocus();
            if (skipPill.getParent() instanceof ViewGroup)
                ((ViewGroup)skipPill.getParent()).removeView(skipPill);
            skipPill = null;
            if (restore && skipOrigin != null)
                skipOrigin.requestFocus();
        }
        skipOrigin = null;
    }

    boolean cancelFocused() {
        if (panel == null || !panel.hasFocus())
            return false;
        dismissed = true;
        remove();
        if (PlayerService.sPlayerService != null)
            PlayerService.sPlayerService.previewCancelAutoNext();
        return true;
    }

    boolean handleKey(KeyEvent event, Runnable showHud) {
        int key = event.getKeyCode();
        if (event.getAction() == KeyEvent.ACTION_UP && key == consumeKeyUp) {
            consumeKeyUp = -1;
            return true;
        }
        if (event.getAction() != KeyEvent.ACTION_DOWN)
            return false;
        if (skipPill != null) {
            if (skipPill.hasFocus()) {
                if (key == KeyEvent.KEYCODE_DPAD_CENTER || key == KeyEvent.KEYCODE_ENTER) {
                    if (event.getRepeatCount() == 0)
                        skipPill.performClick();
                    consumeKeyUp = key;
                    return true;
                }
                if (key == KeyEvent.KEYCODE_DPAD_DOWN) {
                    // Down goes to the controls (opening them if hidden); the button stays.
                    if (skipOrigin != null && skipOrigin.isShown())
                        skipOrigin.requestFocus();
                    else
                        showHud.run();
                    consumeKeyUp = key;
                    return true;
                }
                if (key == KeyEvent.KEYCODE_BACK || key == KeyEvent.KEYCODE_DPAD_UP ||
                    key == KeyEvent.KEYCODE_DPAD_LEFT || key == KeyEvent.KEYCODE_DPAD_RIGHT) {
                    // Dismiss: the part plays; it comes back only with the controls.
                    skipDismissed = true;
                    skipWaitForControls = PlayerController.isControlBarShowing();
                    removeSkip();
                    consumeKeyUp = key;
                    return true;
                }
            } else if (key == KeyEvent.KEYCODE_DPAD_DOWN && activity.getCurrentFocus() != null &&
                       activity.getCurrentFocus().getId() == com.archos.mediacenter.video.R.id.preview_more) {
                skipOrigin = activity.getCurrentFocus();
                skipPill.requestFocus();
                consumeKeyUp = key;
                return true;
            }
        }
        if (panel == null)
            return false;
        LinearLayout actions = (LinearLayout)panel.getChildAt(panel.getChildCount() - 1);
        if (!panel.hasFocus()) {
            if (key != KeyEvent.KEYCODE_DPAD_UP)
                return false;
            previousFocus = activity.getCurrentFocus();
            actions.getChildAt(0).requestFocus();
            consumeKeyUp = key;
            return true;
        }
        if (key == KeyEvent.KEYCODE_DPAD_LEFT || key == KeyEvent.KEYCODE_DPAD_RIGHT) {
            int current = actions.indexOfChild(actions.findFocus());
            actions
                .getChildAt(Math.max(
                    0, Math.min(actions.getChildCount() - 1, current + (key == KeyEvent.KEYCODE_DPAD_LEFT ? -1 : 1))))
                .requestFocus();
        } else if (key == KeyEvent.KEYCODE_DPAD_CENTER || key == KeyEvent.KEYCODE_ENTER) {
            if (event.getRepeatCount() == 0 && actions.findFocus() != null)
                actions.findFocus().performClick();
        } else if (key == KeyEvent.KEYCODE_DPAD_DOWN) {
            if (previousFocus != null)
                previousFocus.requestFocus();
            showHud.run();
        } else if (key == KeyEvent.KEYCODE_BACK) {
            cancelFocused();
        } else if (key != KeyEvent.KEYCODE_DPAD_UP)
            return false;
        consumeKeyUp = key;
        return true;
    }

    private TextView text(String value, int size) {
        TextView v = new TextView(activity);
        v.setText(value);
        v.setTextSize(size);
        v.setTextColor(0xffe7eff5);
        return v;
    }

    private int dp(int value) {
        return PreviewDialog.dp(activity, value);
    }

    private void remove() {
        boolean restore = panel != null && panel.hasFocus();
        if (panel != null && panel.getParent() instanceof ViewGroup)
            ((ViewGroup)panel.getParent()).removeView(panel);
        panel = null;
        if (restore && previousFocus != null && previousFocus.isAttachedToWindow())
            previousFocus.requestFocus();
        previousFocus = null;
    }

    void stop() {
        handler.removeCallbacksAndMessages(null);
        remove();
        removeSkip();
    }
}
