package com.archos.mediacenter.video.leanback;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.Gravity;
import android.graphics.Rect;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import androidx.appcompat.widget.AppCompatEditText;
import java.util.ArrayList;

/** Editable/accessibility field without a redundant remote focus stop. */
public final class KeyboardInput extends AppCompatEditText {
    private final Paint caret = new Paint();

    public KeyboardInput(Context c) {
        super(c);
        setShowSoftInputOnFocus(false);
        setSingleLine(true);
        setMaxLines(1);
        setIncludeFontPadding(false);
        setGravity(Gravity.CENTER_VERTICAL);
        setMinimumHeight(0);
        setMinHeight(0);
        caret.setColor(0xffb9dcf4);
    }

    public static int compactHeight(Context c) {
        Paint p = new Paint();
        p.setTextSize(16 * c.getResources().getDisplayMetrics().scaledDensity);
        return Math.round(p.getFontSpacing()) + PreviewDialog.dp(c, 8);
    }

    @Override
    public void addFocusables(ArrayList<View> views, int direction,
        int mode) {
        /* Keyboard keys own D-pad focus; accessibility still exposes an editable field. */
        if (systemSession) {
            super.addFocusables(views, direction, mode);
        }
    }

    // ---- Phone keyboard (owner, 14 Nov 2026; 0.157 notes §36) ------------------------------------------------------
    // The NVIDIA phone app offers its text box (and so paste) only while Android's own keyboard serves a text box.
    // The "Phone" key starts such a session on this box; Back, Done or moving away returns to Supernova's keys.

    private boolean systemSession;
    private Runnable sessionEnded;

    public boolean inSystemSession() {
        return systemSession;
    }

    public void startSystemSession(Runnable ended) {
        sessionEnded = ended;
        systemSession = true;
        setShowSoftInputOnFocus(true);
        setFocusable(true);
        setFocusableInTouchMode(true);
        requestFocus();
        setSelection(length());
        InputMethodManager keyboards = getContext().getSystemService(InputMethodManager.class);
        if (keyboards != null) {
            keyboards.restartInput(this);
            keyboards.showSoftInput(this, InputMethodManager.SHOW_IMPLICIT);
        }
    }

    public void endSystemSession() {
        if (!closeSystemKeyboard()) {
            return;
        }
        Runnable ended = sessionEnded;
        sessionEnded = null;
        if (ended != null) {
            ended.run();
        }
    }

    private boolean closeSystemKeyboard() {
        if (!systemSession) {
            return false;
        }
        systemSession = false;
        InputMethodManager keyboards = getContext().getSystemService(InputMethodManager.class);
        if (keyboards != null) {
            keyboards.hideSoftInputFromWindow(getWindowToken(), 0);
        }
        setShowSoftInputOnFocus(false);
        return true;
    }

    @Override
    public boolean onKeyPreIme(int keyCode, KeyEvent event) {
        if (systemSession && keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
            endSystemSession();
            return true;
        }
        return super.onKeyPreIme(keyCode, event);
    }

    @Override
    public void onEditorAction(int actionCode) {
        if (systemSession && (actionCode == EditorInfo.IME_ACTION_DONE || actionCode == EditorInfo.IME_ACTION_GO
            || actionCode == EditorInfo.IME_ACTION_SEARCH || actionCode == EditorInfo.IME_ACTION_NEXT)) {
            endSystemSession();
            return;
        }
        super.onEditorAction(actionCode);
    }

    @Override
    protected void onFocusChanged(boolean focused, int direction, Rect previous) {
        super.onFocusChanged(focused, direction, previous);
        if (!focused) {
            // Focus already moved to one of Supernova's keys: close Android's keyboard without moving it again.
            sessionEnded = null;
            closeSystemKeyboard();
        }
    }

    float verticalTextOffset() {
        return Math.max(
            0, (getHeight() - getExtendedPaddingTop() - getExtendedPaddingBottom() - getLayout().getHeight()) / 2f);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (hasFocus() || getLayout() == null || getSelectionStart() < 0)
            return;
        int offset = Math.min(length(), getSelectionStart()), line = getLayout().getLineForOffset(offset);
        float x = getCompoundPaddingLeft() + getLayout().getPrimaryHorizontal(offset) - getScrollX();
        float top = getExtendedPaddingTop() + verticalTextOffset() + getLayout().getLineTop(line) - getScrollY();
        canvas.drawRect(x, top, x + Math.max(1, getResources().getDisplayMetrics().density),
            top + getLayout().getLineBottom(line) - getLayout().getLineTop(line), caret);
    }
}
