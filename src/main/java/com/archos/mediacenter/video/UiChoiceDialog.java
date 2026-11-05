// Copyright 2017 Archos SA
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.archos.mediacenter.video;

import android.app.Dialog;
import androidx.fragment.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

/**
 * Created by vapillon on 11/05/15.
 */
public class UiChoiceDialog extends DialogFragment implements View.OnClickListener {

    static final public String UI_CHOICE_LEANBACK_KEY = UiModePolicy.UI_CHOICE_LEANBACK_KEY;
    static final public String UI_CHOICE_LEANBACK_TABLET_VALUE = UiModePolicy.UI_CHOICE_LEANBACK_TABLET_VALUE;
    static final public String UI_CHOICE_LEANBACK_TV_VALUE = UiModePolicy.UI_CHOICE_LEANBACK_TV_VALUE;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE); // Thank you stack overflow!

        View v = inflater.inflate(R.layout.ui_choice_dialog, container);

        v.findViewById(R.id.choice_tv).setOnClickListener(this);
        v.findViewById(R.id.choice_tablet).setOnClickListener(this);

        return v;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog d = super.onCreateDialog(savedInstanceState);
        d.setTitle(null);
        return d;
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.choice_tv) {
            PreferenceManager.getDefaultSharedPreferences(getActivity())
                    .edit()
                    .putString(UI_CHOICE_LEANBACK_KEY, UI_CHOICE_LEANBACK_TV_VALUE) // string definitions in preference_video.xml and in @array/ui_mode_leanback_entryvalues
                    .apply();

        }
        else if (view.getId() == R.id.choice_tablet) {
            PreferenceManager.getDefaultSharedPreferences(getActivity())
                    .edit()
                    .putString(UI_CHOICE_LEANBACK_KEY, UI_CHOICE_LEANBACK_TABLET_VALUE) // string definitions in preference_video.xml and in @array/ui_mode_leanback_entryvalues
                    .apply();
        }
        UiModePolicy.updateUiModePreferences(getActivity(), true);
        dismiss();

        // Quit the current activity and relaunch the entry acivity
        getActivity().finish();

        Intent i = new Intent(Intent.ACTION_MAIN);
        i.setClass(getActivity(), EntryActivity.class);
        startActivity(i);
    }

    /** Compatibility facade until the phone dialog's removal stage. */
    public static boolean applicationIsInLeanbackMode(Context context) {
        return UiModePolicy.applicationIsInLeanbackMode(context);
    }

    /**
     * Update uimode and uimode_leanback preferences to reflect the current UI mode
     * Should be called after launching MainActivity or MainActivityLeanback
     */
    public static void updateUiModePreferences(Context context, boolean isLeanback) {
        UiModePolicy.updateUiModePreferences(context, isLeanback);
    }

    /**
     * Update uimode preferences using an existing editor to batch with other operations
     * Avoids multiple apply() calls when batching SharedPreferences updates
     */
    public static void updateUiModePreferencesInEditor(Context context, SharedPreferences.Editor editor) {
        UiModePolicy.updateUiModePreferencesInEditor(context, editor);
    }
}
