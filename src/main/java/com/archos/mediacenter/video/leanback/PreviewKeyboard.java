package com.archos.mediacenter.video.leanback;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.*;
import android.widget.*;
import java.util.ArrayList;
import java.util.List;

/** Shared TV keyboard. Spatial movement is explicit and cannot escape through an empty result pane. */
public final class PreviewKeyboard extends LinearLayout {
    public static final String[] ROWS = {"1234567890", "QWERTYUIOP", "ASDFGHJKL", "ZXCVBNM"};
    private final List<List<TextView>> keys = new ArrayList<>();
    private TextView lastKey;

    public PreviewKeyboard(Context context, EditText input, Runnable leaveRight) {
        super(context);
        setOrientation(VERTICAL);
        setClipChildren(false);
        setClipToPadding(false);
        for (int rowIndex = 0; rowIndex <= ROWS.length; rowIndex++) {
            final int r = rowIndex;
            LinearLayout row = new LinearLayout(context);
            row.setClipChildren(false);
            row.setClipToPadding(false);
            LayoutParams lp = new LayoutParams(-1, dp(38));
            lp.topMargin = dp(5);
            // Equal key widths, with the familiar stagger on the two shorter letter rows.
            if (r == 2) { lp.leftMargin = dp(12); lp.rightMargin = dp(12); }
            if (r == 3) { lp.leftMargin = dp(36); lp.rightMargin = dp(36); }
            addView(row, lp);
            List<TextView> rowKeys = new ArrayList<>();
            keys.add(rowKeys);
            String[] labels = r == 4 ? new String[]{"Clear", "Space", "Backspace"} : ROWS[r].split("");
            for (String label : labels) {
                if (label.isEmpty()) continue;
                final int col = rowKeys.size();
                TextView key = key(label, () -> {
                    if (r != 4) replace(input, label);
                    else if (col == 0) input.setText("");
                    else if (col == 1) replace(input, " ");
                    else {
                        int start = selection(input), end = Math.max(start, input.getSelectionEnd());
                        if (start != end) input.getText().delete(start, end);
                        else if (start > 0) input.getText().delete(input.getText().toString().offsetByCodePoints(start, -1), start);
                    }
                });
                key.setTag("semantic:keyboard:" + label);
                key.setOnFocusChangeListener((v, focused) -> { if (focused) lastKey = (TextView) v; });
                key.setOnKeyListener((v, code, event) -> {
                    if (event.getAction() != KeyEvent.ACTION_DOWN) return false;
                    if (code == KeyEvent.KEYCODE_DPAD_LEFT) {
                        if (col > 0) keys.get(r).get(col - 1).requestFocus();
                        return true;
                    }
                    if (code == KeyEvent.KEYCODE_DPAD_RIGHT) {
                        if (col + 1 < keys.get(r).size()) keys.get(r).get(col + 1).requestFocus();
                        else if (leaveRight != null) leaveRight.run();
                        return true;
                    }
                    if (code == KeyEvent.KEYCODE_DPAD_UP || code == KeyEvent.KEYCODE_DPAD_DOWN) {
                        int targetRow = r + (code == KeyEvent.KEYCODE_DPAD_UP ? -1 : 1);
                        if (targetRow < 0) return false; // The host owns entry into global navigation.
                        if (targetRow >= keys.size()) return true;
                        nearest(keys.get(targetRow), v).requestFocus();
                        return true;
                    }
                    return false;
                });
                rowKeys.add(key);
                row.addView(key, new LayoutParams(0, -1, 1));
            }
        }
        lastKey = keys.get(1).get(4); // T, as specified for first entry.
    }

    private TextView nearest(List<TextView> row, View source) {
        float centre = source.getLeft() + source.getWidth() / 2f + ((View) source.getParent()).getLeft();
        TextView result = row.get(0);
        float distance = Float.MAX_VALUE;
        for (TextView candidate : row) {
            float x = candidate.getLeft() + candidate.getWidth() / 2f + ((View) candidate.getParent()).getLeft();
            if (Math.abs(x - centre) < distance) { result = candidate; distance = Math.abs(x - centre); }
        }
        return result;
    }

    public boolean focusLastKey() { return lastKey.requestFocus(); }
    public boolean atTop() { return keys.get(0).contains(findFocus()); }
    public String focusedKey() { return lastKey.getText().toString(); }
    public void restoreKey(String label) {
        for (List<TextView> row : keys) for (TextView key : row)
            if (key.getText().toString().equals(label)) { lastKey = key; return; }
    }
    private static int selection(EditText input) { return input.getSelectionStart() < 0 ? input.length() : input.getSelectionStart(); }
    private static void replace(EditText input, String value) {
        int start = selection(input), end = Math.max(start, input.getSelectionEnd());
        input.getText().replace(start, end, value);
        input.setSelection(start + value.length());
    }
    private TextView key(String label, Runnable action) {
        TextView key = new TextView(getContext());
        key.setText(label); key.setTextColor(Color.WHITE); key.setTextSize(14);
        key.setTypeface(Typeface.create("sans-serif-light", Typeface.NORMAL));
        key.setGravity(Gravity.CENTER); key.setFocusable(true); key.setId(View.generateViewId());
        key.setBackground(PreviewDialog.focus(getContext()));
        key.setOnClickListener(v -> action.run());
        return key;
    }
    private int dp(int n) { return PreviewDialog.dp(getContext(), n); }
}
