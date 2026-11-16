package com.archos.mediacenter.video.leanback.settings;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.preference.ListPreference;
import com.archos.mediacenter.video.leanback.PreviewDialog;
import com.archos.mediacenter.video.leanback.PreviewLanguageIcon;
import com.archos.mediacenter.video.utils.VideoPreferencesCommon;
import java.util.Arrays;
import java.util.Collections;

/**
 * A Settings list choice on the one shared choice list (RULES: tick on the right, search above 12 items, Back
 * closes, charcoal panel). Language lists show each language's flag in place of the row icon.
 */
final class SettingsChoiceDialog {
    private SettingsChoiceDialog() {
    }

    static Dialog show(Context context, ListPreference preference) {
        CharSequence[] entries = preference.getEntries();
        CharSequence[] values = preference.getEntryValues();
        if (entries == null || values == null || entries.length != values.length)
            return null;
        String[] labels = new String[entries.length];
        for (int i = 0; i < entries.length; i++)
            labels[i] = entries[i].toString();
        int selected = preference.findIndexOfValue(preference.getValue());
        Dialog[] dialog = new Dialog[1];
        dialog[0] = PreviewDialog.choose(
            context, String.valueOf(preference.getTitle()), labels, Math.max(0, selected),
            selected < 0 ? Collections.emptySet() : Collections.singleton(selected), false, index -> {
                String value = values[index].toString();
                if (preference.callChangeListener(value)) {
                    preference.setValue(value);
                    dialog[0].dismiss();
                }
            });
        if (dialog[0] == null)
            return null;
        showFlags(dialog[0], preference.getKey(), values);
        return dialog[0];
    }

    /** Swaps in flags for language lists (rows keep the shared list's own index tags). */
    private static void showFlags(Dialog dialog, String key, CharSequence[] values) {
        View root = dialog.getWindow() == null ? null : dialog.getWindow().getDecorView();
        if (root == null)
            return;
        for (int i = 0; i < values.length; i++) {
            View row = root.findViewWithTag(i);
            if (!(row instanceof ViewGroup))
                continue;
            ViewGroup group = (ViewGroup)row;
            if (language(key) && group.getChildCount() > 0 && group.getChildAt(0) instanceof ImageView) {
                String code = PreviewLanguageIcon.providerCode(values[i].toString());
                ((ImageView)group.getChildAt(0)).setImageDrawable(new PreviewLanguageIcon(row.getContext(), code));
            }
        }
    }

    private static boolean language(String key) {
        return Arrays
            .asList("ui_lang", "favSubLang", "favAudioLang", "languages_list",
                    VideoPreferencesCommon.KEY_SCRAPER_FAV_LANG)
            .contains(key);
    }
}
