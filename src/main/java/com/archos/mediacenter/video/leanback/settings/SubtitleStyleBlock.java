package com.archos.mediacenter.video.leanback.settings;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.preference.PreferenceManager;
import com.archos.mediacenter.video.leanback.SharedThreePanel;
import com.archos.mediacenter.video.leanback.SupernovaStyle;
import com.archos.mediacenter.video.player.SubtitleManager;
import com.archos.mediacenter.video.player.SubtitleStyleControls;
import java.util.ArrayList;
import java.util.List;

/**
 * Settings › Audio & Subtitles › Style (mock-up settings/08–09): a live sample line and the subtitle style
 * controls the player's Playback Settings has (size, colour, vertical position, outline, background, opacity).
 * Left and Right change a value; OK steps forward. Saved exactly as the player saves them.
 */
final class SubtitleStyleBlock extends LinearLayout
{
    private final SubtitleManager style;
    private final SharedPreferences prefs;
    private final TextView sample;
    private final FrameLayout.LayoutParams sampleSize;
    private final FrameLayout stage;
    private final List<Runnable> bindings = new ArrayList<>();

    SubtitleStyleBlock(Context context)
    {
        super(context);
        setOrientation(VERTICAL);
        prefs = PreferenceManager.getDefaultSharedPreferences(context);
        if (SubtitleStyleControls.playerActive()) {
            style = null;
            sample = null;
            sampleSize = null;
            stage = null;
            TextView note = SharedThreePanel.text(context,
                "A video is open. Change the subtitle style in its Playback Settings, where you can see it.", 14);
            note.setTextColor(SupernovaStyle.TEXT_SECONDARY);
            note.setPadding(dp(11), dp(8), dp(11), dp(12));
            addView(note);
            return;
        }
        style = SubtitleStyleControls.detached(context);
        stage = new FrameLayout(context);
        stage.setBackground(SupernovaStyle.rounded(context, 0xFF0B0D12, 10));
        sample = new TextView(context);
        sample.setText("This is how your subtitles look.");
        sample.setGravity(Gravity.CENTER);
        sample.setTag("settings.subtitle.sample");
        sampleSize = new FrameLayout.LayoutParams(-2, -2, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL);
        stage.addView(sample, sampleSize);
        LayoutParams stageSize = new LayoutParams(-1, dp(120));
        stageSize.bottomMargin = dp(8);
        addView(stage, stageSize);
        stepper("size", "Text size", "Small, Medium or Large.",
            d -> SubtitleStyleControls.step(SubtitleStyleControls.SIZES, get("size"), d),
            () -> SubtitleStyleControls.nameOf(SubtitleStyleControls.SIZES, SubtitleStyleControls.SIZE_NAMES,
                                               get("size"), "Custom"));
        stepper("colour", "Text colour", "White, yellow, cyan, green or pink.",
            d -> SubtitleStyleControls.step(SubtitleStyleControls.COLOURS, get("colour"), d),
            () -> SubtitleStyleControls.nameOf(SubtitleStyleControls.COLOURS, SubtitleStyleControls.COLOUR_NAMES,
                                               get("colour"), "Custom"));
        stepper("position", "Vertical position", "Right raises the subtitles, Left lowers them.",
            d -> get("position") + d * SubtitleStyleControls.POSITION_STEP,
            () -> SubtitleStyleControls.positionName(get("position")));
        stepper("outline", "Outline", "A dark edge round each letter.", d -> 1 - get("outline"),
            () -> get("outline") != 0 ? "On" : "Off");
        stepper("background", "Background", "A dark box behind the words.", d -> 1 - get("background"),
            () -> get("background") != 0 ? "On" : "Off");
        stepper("opacity", "Background opacity", "How solid the box is.",
            d -> SubtitleStyleControls.step(SubtitleStyleControls.OPACITY, get("opacity"), d),
            () -> SubtitleStyleControls.nameOf(SubtitleStyleControls.OPACITY, SubtitleStyleControls.OPACITY_NAMES,
                                               get("opacity"), "Custom"));
        refresh();
    }

    private interface Next
    {
        int value(int direction);
    }

    private interface Name
    {
        String text();
    }

    private int get(String key)
    {
        return SubtitleStyleControls.get(style, key);
    }

    private void change(String key, int value)
    {
        SharedPreferences.Editor editor = prefs.edit();
        SubtitleStyleControls.set(style, editor, key, value);
        editor.apply();
        refresh();
    }

    private void stepper(String key, String name, String detail, Next next, Name current)
    {
        LinearLayout row = new LinearLayout(getContext());
        row.setOrientation(VERTICAL);
        row.setFocusable(true);
        row.setFocusableInTouchMode(true);
        row.setTag("settings.subtitle." + key);
        row.setBackground(SupernovaStyle.rowFocus(getContext()));
        row.setPadding(dp(11), dp(8), dp(11), dp(8));
        row.setMinimumHeight(dp(52));
        SharedThreePanel.rowSeparator(row);
        LinearLayout line = new LinearLayout(getContext());
        line.setGravity(Gravity.CENTER_VERTICAL);
        TextView title = SharedThreePanel.text(getContext(), name, 13);
        title.setTypeface(SupernovaStyle.LABEL);
        line.addView(title, new LayoutParams(0, -2, 1));
        TextView value = SharedThreePanel.text(getContext(), "", 12);
        value.setTextColor(0xFFDFE5F1);
        value.setTag("settings.value");
        line.addView(value);
        row.addView(line);
        TextView about = SharedThreePanel.text(getContext(), detail, 11);
        about.setTextColor(SupernovaStyle.TEXT_SECONDARY);
        row.addView(about);
        bindings.add(() -> {
            String text = current.text();
            value.setText("‹  " + text + "  ›");
            row.setContentDescription(name + ", " + text + ". " + detail);
        });
        row.setOnClickListener(v -> change(key, next.value(1)));
        row.setOnKeyListener((v, code, event) -> {
            if (event.getAction() != KeyEvent.ACTION_DOWN)
                return false;
            if (code == KeyEvent.KEYCODE_DPAD_RIGHT || code == KeyEvent.KEYCODE_DPAD_LEFT) {
                change(key, next.value(code == KeyEvent.KEYCODE_DPAD_RIGHT ? 1 : -1));
                return true;
            }
            return false;
        });
        addView(row, new LayoutParams(-1, -2));
    }

    /** The sample shows size, colour, position, outline and background as they will look. */
    private void refresh()
    {
        for (Runnable binding : bindings)
            binding.run();
        if (sample == null)
            return;
        int size = get("size");
        sample.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12 + size * 0.12f);
        sample.setTextColor(get("colour"));
        if (get("outline") != 0)
            sample.setShadowLayer(3f, 0f, 0f, Color.BLACK);
        else
            sample.setShadowLayer(0f, 0f, 0f, Color.TRANSPARENT);
        if (get("background") != 0) {
            GradientDrawable box = new GradientDrawable();
            box.setColor(Color.argb(Math.max(0, Math.min(255, get("opacity"))), 0, 0, 0));
            box.setCornerRadius(dp(3));
            sample.setBackground(box);
        } else {
            sample.setBackground(null);
        }
        sample.setPadding(dp(6), dp(2), dp(6), dp(2));
        int percent = Math.max(0, Math.min(90, get("position")));
        sampleSize.bottomMargin = Math.round(dp(120) * 0.8f * percent / 100f) + dp(6);
        sample.setLayoutParams(sampleSize);
    }

    private int dp(int value)
    {
        return Math.round(value * getResources().getDisplayMetrics().density);
    }
}
