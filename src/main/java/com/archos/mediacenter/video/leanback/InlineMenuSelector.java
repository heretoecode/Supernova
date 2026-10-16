package com.archos.mediacenter.video.leanback;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.KeyEvent;
import androidx.appcompat.widget.AppCompatTextView;
import java.util.function.IntConsumer;

/** Fixed-size OK/cycle/OK selector: Back discards only its current uncommitted preview. */
final class InlineMenuSelector extends AppCompatTextView {
    private final String[] labels;
    private final IntConsumer commit;
    private final Drawable left;
    private int selected, preview;
    private boolean editing;

    InlineMenuSelector(Context context, String[] labels, int initial, String icon, IntConsumer commit) {
        super(context);
        this.labels = labels.clone(); this.selected = Math.max(0, Math.min(initial, labels.length - 1));
        this.preview = this.selected; this.commit = commit;
        setTextSize(14 * TvLayout.scale(context)); setTextColor(MenuFoundation.TEXT);
        setTypeface(android.graphics.Typeface.create("sans-serif-light", android.graphics.Typeface.NORMAL)); setIncludeFontPadding(false);
        setSingleLine(true); setGravity(android.view.Gravity.CENTER_VERTICAL);
        setId(android.view.View.generateViewId()); setFocusable(true); setFocusableInTouchMode(true);
        setClickable(true); setMinimumHeight(MenuFoundation.dp(context, 44));
        setPadding(MenuFoundation.dp(context, 8), MenuFoundation.dp(context, 8),
                MenuFoundation.dp(context, 8), MenuFoundation.dp(context, 8));
        MenuFoundation.icon(this, icon); left = getCompoundDrawables()[0];
        setBackground(MenuFoundation.controlBackground(context)); update();
        setOnClickListener(view -> {
            if (editing) { selected = preview; editing = false; commit.accept(selected); }
            else { preview = selected; editing = true; }
            update();
        });
    }

    private void update() {
        setText(labels[preview]);
        setContentDescription(labels[preview] + (editing ? ", Up or Down to preview, OK to confirm, Back to cancel" : ", OK to edit"));
        Drawable arrows = new PreviewIcon("chevrons");
        int size = MenuFoundation.dp(getContext(), 14); arrows.setBounds(0, 0, size, size);
        setCompoundDrawables(left, null, arrows, null);
        setActivated(editing);
    }

    int contentWidth() {
        float widest=0;for(String label:labels)widest=Math.max(widest,getPaint().measureText(label));
        return (int)Math.ceil(widest)+getCompoundPaddingLeft()+getCompoundPaddingRight()+MenuFoundation.dp(getContext(),2);
    }

    boolean cancelPreview() {
        if (!editing) return false;
        editing = false; preview = selected; update(); return true;
    }

    @Override public boolean dispatchKeyEvent(KeyEvent event) {
        int code = event.getKeyCode();
        if (editing && (code == KeyEvent.KEYCODE_DPAD_UP || code == KeyEvent.KEYCODE_DPAD_DOWN || code == KeyEvent.KEYCODE_BACK)) {
            if (event.getAction() == KeyEvent.ACTION_DOWN) {
                if (code == KeyEvent.KEYCODE_BACK) cancelPreview();
                else { preview = Math.floorMod(preview + (code == KeyEvent.KEYCODE_DPAD_DOWN ? 1 : -1), labels.length); update(); }
            }
            return true;
        }
        return super.dispatchKeyEvent(event);
    }
}
