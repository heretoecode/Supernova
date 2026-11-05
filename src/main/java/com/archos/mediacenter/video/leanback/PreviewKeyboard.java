package com.archos.mediacenter.video.leanback;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Shared TV keyboard (0.156 #31, mock-ups {@code docs/design/proposals/search/01}–{@code 04}). The letters page is a
 * number row, lower-case QWERTY with {@code /} after L and {@code . @} after M, and Shift for capitals; {@code #+=}
 * opens a symbols page with everything a password, address or path needs, and ABC returns. The bottom row is
 * {@code #+=} · Voice · Space · Delete · Clear. Forms keep a Done · Cancel row; Search hides it. Spatial movement is
 * explicit and cannot escape through an empty result pane.
 */
public final class PreviewKeyboard extends LinearLayout {
    public static final String[] ROWS = {"1234567890", "QWERTYUIOP", "ASDFGHJKL/", "ZXCVBNM.@"};
    public static final String[] SYMBOLS = {"!#$%&*()-_", "=+[]{}\\|~`", ":;,?'\"<>^"};
    static final String SHIFT = "Caps Lock";
    static final String SYMBOLS_KEY = "Symbols";
    static final String LETTERS_KEY = "Letters";
    static final String VOICE = "Voice";

    private final EditText input;
    private final Runnable leaveRight;
    private final List<List<TextView>> letters = new ArrayList<>();
    private final List<List<TextView>> symbols = new ArrayList<>();
    private final List<TextView> digits = new ArrayList<>();
    private final List<TextView> bottom = new ArrayList<>();
    private final List<TextView> doneRow = new ArrayList<>();
    private final List<LinearLayout> letterRows = new ArrayList<>();
    private final List<LinearLayout> symbolRows = new ArrayList<>();
    private LinearLayout doneLine;
    private TextView lastKey;
    private TextView shiftKey;
    private TextView pageKey;
    private TextView voiceKey;
    private Runnable leaveDown;
    private Runnable voice;
    private boolean caps;
    private boolean symbolPage;
    private Runnable confirm;
    private Runnable cancel;

    public PreviewKeyboard(Context context, EditText input, Runnable leaveRight) {
        super(context);
        this.input = input;
        this.leaveRight = leaveRight;
        setOrientation(VERTICAL);
        setClipChildren(false);
        setClipToPadding(false);
        LinearLayout numbers = line();
        for (char c : ROWS[0].toCharArray())
            digits.add(character(numbers, String.valueOf(c)));
        for (int r = 1; r < ROWS.length; r++) {
            LinearLayout row = line();
            List<TextView> keys = new ArrayList<>();
            if (r == 3) {
                shiftKey = action(row, SHIFT, "⇧", 1f, this::toggleCaps);
                shiftKey.setContentDescription("Shift");
                keys.add(shiftKey);
            }
            for (char c : ROWS[r].toCharArray())
                keys.add(character(row, String.valueOf(c)));
            letters.add(keys);
            letterRows.add(row);
        }
        for (int r = 0; r < SYMBOLS.length; r++) {
            LinearLayout row = line();
            List<TextView> keys = new ArrayList<>();
            if (r == 2)
                keys.add(action(row, LETTERS_KEY, "ABC", 1f, () -> showSymbols(false)));
            for (char c : SYMBOLS[r].toCharArray())
                keys.add(character(row, String.valueOf(c)));
            symbols.add(keys);
            symbolRows.add(row);
            row.setVisibility(GONE);
        }
        LinearLayout last = line();
        pageKey = action(last, SYMBOLS_KEY, "#+=", 1.6f, () -> showSymbols(!symbolPage));
        voiceKey = action(last, VOICE, "", 1.6f, () -> {
            if (voice != null)
                voice.run();
        });
        voiceKey.setContentDescription("Voice search");
        PreviewIcon.apply(voiceKey, "mic", 14);
        voiceKey.setVisibility(GONE);
        TextView space = action(last, "Space", "Space", 3.3f, () -> replace(input, " "));
        PreviewIcon.apply(space, "Space", 14);
        TextView delete = action(last, "Backspace", "", 1.6f, this::delete);
        PreviewIcon.apply(delete, "Backspace", 14);
        delete.setContentDescription("Delete");
        bottom.add(pageKey);
        bottom.add(voiceKey);
        bottom.add(space);
        bottom.add(delete);
        bottom.add(action(last, "Clear", "Clear", 1.6f, () -> input.setText("")));
        doneLine = line();
        doneRow.add(action(doneLine, "Done", "Done", 1f, () -> {
            if (confirm != null)
                confirm.run();
            else if (leaveRight != null)
                leaveRight.run();
        }));
        doneRow.add(action(doneLine, "Cancel", "Cancel", 1f, () -> {
            if (cancel != null)
                cancel.run();
        }));
        applyCase();
        lastKey = letters.get(0).get(4); // T, as specified for first entry.
    }

    // ---- host API -------------------------------------------------------------------------------------------

    public void setActions(Runnable select, Runnable dismiss) {
        confirm = select;
        cancel = dismiss;
    }

    /** Search keeps the keyboard up while browsing, so it has no Done · Cancel row (owner keyboard rule). */
    public void setDoneRowVisible(boolean visible) {
        doneLine.setVisibility(visible ? VISIBLE : GONE);
    }

    /** Voice (the Shield remote's microphone) shows only when the host can listen. */
    public void setVoice(Runnable listen) {
        voice = listen;
        voiceKey.setVisibility(listen == null ? GONE : VISIBLE);
    }

    public boolean capsLocked() {
        return caps;
    }

    public boolean symbolsShown() {
        return symbolPage;
    }

    public void setLeaveDown(Runnable action) {
        leaveDown = action;
    }

    public void useMenuFoundation() {
        for (List<TextView> row : rows())
            for (TextView key : row)
                SupernovaStyle.applyButton(key, SupernovaStyle.Surface.STANDARD);
    }

    public boolean focusLastKey() {
        if (lastKey.getVisibility() != VISIBLE || !isShownInPage(lastKey))
            lastKey = letters.get(0).get(4);
        return lastKey.requestFocus();
    }

    public boolean atTop() {
        return digits.contains(findFocus());
    }

    public String focusedKey() {
        return String.valueOf(lastKey.getTag()).replace("semantic:keyboard:", "");
    }

    public void restoreKey(String label) {
        for (List<TextView> row : rows())
            for (TextView key : row)
                if (key.getText().toString().equals(label) ||
                    String.valueOf(key.getTag()).equals("semantic:keyboard:" + label)) {
                    lastKey = key;
                    return;
                }
    }

    // ---- pages and case -------------------------------------------------------------------------------------

    private void toggleCaps() {
        caps = !caps;
        applyCase();
    }

    private void applyCase() {
        for (List<TextView> row : letters)
            for (TextView key : row) {
                String value = value(key);
                if (value.length() == 1 && Character.isLetter(value.charAt(0)))
                    key.setText(caps ? value : value.toLowerCase(Locale.ROOT));
            }
        shiftKey.setSelected(caps);
        shiftKey.setContentDescription("Shift " + (caps ? "On" : "Off"));
    }

    private void showSymbols(boolean show) {
        symbolPage = show;
        for (LinearLayout row : letterRows)
            row.setVisibility(show ? GONE : VISIBLE);
        for (LinearLayout row : symbolRows)
            row.setVisibility(show ? VISIBLE : GONE);
        pageKey.setText(show ? "ABC" : "#+=");
        pageKey.setContentDescription(show ? "Letters" : "Symbols");
        pageKey.requestFocus();
    }

    /** The rows on the current page, top to bottom, as the remote moves through them. */
    private List<List<TextView>> rows() {
        List<List<TextView>> rows = new ArrayList<>();
        rows.add(digits);
        rows.addAll(symbolPage ? symbols : letters);
        List<TextView> shownBottom = new ArrayList<>();
        for (TextView key : bottom)
            if (key.getVisibility() == VISIBLE)
                shownBottom.add(key);
        rows.add(shownBottom);
        if (doneLine.getVisibility() == VISIBLE)
            rows.add(doneRow);
        return rows;
    }

    private boolean isShownInPage(TextView key) {
        for (List<TextView> row : rows())
            if (row.contains(key))
                return true;
        return false;
    }

    // ---- keys -----------------------------------------------------------------------------------------------

    private LinearLayout line() {
        LinearLayout row = new LinearLayout(getContext());
        row.setGravity(Gravity.CENTER);
        row.setClipChildren(false);
        row.setClipToPadding(false);
        LayoutParams lp = new LayoutParams(-1, dp(32));
        lp.topMargin = dp(4);
        addView(row, lp);
        return row;
    }

    private TextView character(LinearLayout row, String label) {
        TextView key = key(label, label, 1f, () -> replace(input, typed(label)));
        row.addView(key, key.getLayoutParams());
        return key;
    }

    private TextView action(LinearLayout row, String tag, String label, float weight, Runnable run) {
        TextView key = key(tag, label, weight, run);
        key.setTextSize(12);
        key.setCompoundDrawablePadding(dp(4));
        row.addView(key, key.getLayoutParams());
        return key;
    }

    private String typed(String label) {
        boolean letter = label.length() == 1 && Character.isLetter(label.charAt(0));
        return letter && !caps ? label.toLowerCase(Locale.ROOT) : label;
    }

    private static String value(TextView key) {
        return String.valueOf(key.getTag()).substring("semantic:keyboard:".length());
    }

    private TextView key(String tag, String label, float weight, Runnable run) {
        TextView key = new TextView(getContext());
        key.setText(label);
        key.setTag("semantic:keyboard:" + tag);
        key.setTextColor(Color.WHITE);
        key.setTextSize(15);
        key.setTypeface(Typeface.create("sans-serif", Typeface.NORMAL));
        key.setGravity(Gravity.CENTER);
        key.setFocusable(true);
        key.setFocusableInTouchMode(true);
        key.setId(View.generateViewId());
        key.setPadding(dp(4), 0, dp(4), 0);
        SupernovaStyle.applyButton(key, SupernovaStyle.Surface.PANEL);
        key.setOnClickListener(v -> run.run());
        key.setOnFocusChangeListener((v, focused) -> {
            if (focused)
                lastKey = (TextView)v;
        });
        key.setOnKeyListener((v, code, event) -> move((TextView)v, code, event));
        LayoutParams size = new LayoutParams(0, -1, weight);
        size.leftMargin = dp(2);
        size.rightMargin = dp(2);
        key.setLayoutParams(size);
        return key;
    }

    private boolean move(TextView key, int code, KeyEvent event) {
        if (event.getAction() != KeyEvent.ACTION_DOWN)
            return false;
        List<List<TextView>> rows = rows();
        int r = -1;
        for (int i = 0; i < rows.size(); i++)
            if (rows.get(i).contains(key))
                r = i;
        if (r < 0)
            return false;
        List<TextView> row = rows.get(r);
        int col = row.indexOf(key);
        if (code == KeyEvent.KEYCODE_DPAD_LEFT) {
            if (col > 0)
                row.get(col - 1).requestFocus();
            return true;
        }
        if (code == KeyEvent.KEYCODE_DPAD_RIGHT) {
            if (col + 1 < row.size())
                row.get(col + 1).requestFocus();
            else if (leaveRight != null)
                leaveRight.run();
            return true;
        }
        if (code == KeyEvent.KEYCODE_DPAD_UP || code == KeyEvent.KEYCODE_DPAD_DOWN) {
            int target = r + (code == KeyEvent.KEYCODE_DPAD_UP ? -1 : 1);
            if (target < 0)
                return false; // The host owns entry into global navigation.
            if (target >= rows.size()) {
                if (leaveDown != null)
                    leaveDown.run();
                return true;
            }
            nearest(rows.get(target), key).requestFocus();
            return true;
        }
        return false;
    }

    private TextView nearest(List<TextView> row, View source) {
        float centre = source.getLeft() + source.getWidth() / 2f + ((View)source.getParent()).getLeft();
        TextView result = row.get(0);
        float distance = Float.MAX_VALUE;
        for (TextView candidate : row) {
            float x = candidate.getLeft() + candidate.getWidth() / 2f + ((View)candidate.getParent()).getLeft();
            if (Math.abs(x - centre) < distance) {
                result = candidate;
                distance = Math.abs(x - centre);
            }
        }
        return result;
    }

    private void delete() {
        int start = selection(input);
        int end = Math.max(start, input.getSelectionEnd());
        if (start != end)
            input.getText().delete(start, end);
        else if (start > 0)
            input.getText().delete(input.getText().toString().offsetByCodePoints(start, -1), start);
    }

    private static int selection(EditText input) {
        return input.getSelectionStart() < 0 ? input.length() : input.getSelectionStart();
    }

    private static void replace(EditText input, String value) {
        int start = selection(input);
        int end = Math.max(start, input.getSelectionEnd());
        input.getText().replace(start, end, value);
        input.setSelection(Math.min(input.length(), start + value.length()));
    }

    private int dp(int n) {
        return PreviewDialog.dp(getContext(), n);
    }
}
