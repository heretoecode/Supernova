package com.archos.mediacenter.video.leanback;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import androidx.core.graphics.ColorUtils;
import androidx.preference.PreferenceManager;

/** Accent affects controls and subtle utility backgrounds; never tints artwork. */
public final class PreviewAccent {
    public static int color(Context c) {
        return color(c, 0xff62bbf3);
    }

    public static int color(Context c, int defaultColour) {
        return PreferenceManager.getDefaultSharedPreferences(c).getInt(
            "preview_accent41", defaultColour);
    }

    /** A white thumb must remain distinct, including with custom white/black accents. */
    public static int switchTrack(Context c) {
        int accent = color(c);
        double luminance = ColorUtils.calculateLuminance(accent);
        return luminance < .08 || luminance > .65 ? 0xff0a6cff : accent;
    }

    public static int alpha(Context c, int alpha) {
        return (color(c) & 0x00ffffff) | (alpha << 24);
    }

    public static GradientDrawable utility(Context c) {
        int a = color(c);
        return new GradientDrawable(GradientDrawable.Orientation.TL_BR,
            new int[] {
                Color.rgb(13 + Color.red(a) / 24, 17 + Color.green(a) / 24, 22 + Color.blue(a) / 24), 0xff070d14,
                0xff131b24
            });
    }

    public static void choose(Context c, Runnable changed) {
        String[] names = {
            "Slate Blue", "Cyan", "Emerald", "Purple", "Amber", "Red", "Custom…"
        };
        int[] colours = {
            0xff62bbf3, 0xff51d8e8, 0xff58cfaa, 0xffb49af4, 0xffedbd67, 0xffef8282
        };
        PreviewDialog.choose(c, "Accent & Colour", names, -1, n -> {
            if (n < colours.length) {
                save(c, colours[n]);
                changed.run();
            } else
                custom(c, changed);
        });
    }

    private static void save(Context c, int colour) {
        PreferenceManager.getDefaultSharedPreferences(c)
            .edit()
            .putInt("preview_accent41", colour)
            .apply();
    }

    private static void custom(Context c, Runnable changed) {
        Dialog d = new Dialog(c);
        LinearLayout box = new LinearLayout(c);
        box.setOrientation(LinearLayout.VERTICAL);
        box.setPadding(28, 24, 28, 24);
        box.setBackground(utility(c));
        TextView title = new TextView(c);
        title.setText("Custom accent — Left / Right adjusts each colour");
        title.setTextColor(Color.WHITE);
        box.addView(title);
        int initial = color(c);
        int[] rgb = {
            Color.red(initial), Color.green(initial), Color.blue(initial)
        };
        for (int i = 0; i < 3; i++) {
            final int channel = i;
            TextView name = new TextView(c);
            name.setText(new String[] {
                "Red", "Green", "Blue"
            }[i]);
            name.setTextColor(Color.WHITE);
            box.addView(name);
            SeekBar slider = new SeekBar(c);
            slider.setMax(255);
            slider.setProgress(rgb[i]);
            slider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                public void onProgressChanged(SeekBar b, int value, boolean user) {
                    rgb[channel] = value;
                    title.setTextColor(Color.rgb(rgb[0], rgb[1], rgb[2]));
                }
                public void onStartTrackingTouch(SeekBar b) {
                }
                public void onStopTrackingTouch(SeekBar b) {
                }
            });
            box.addView(slider);
        }
        Button apply = new Button(c);
        apply.setText("Apply");
        apply.setOnClickListener(v -> {
            save(c, Color.rgb(rgb[0], rgb[1], rgb[2]));
            d.dismiss();
            changed.run();
        });
        box.addView(apply);
        d.setContentView(box);
        d.show();
        d.getWindow().setLayout(PreviewDialog.dp(c, 500), -2);
    }

    private PreviewAccent() {
    }
}
