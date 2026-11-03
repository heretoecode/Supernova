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

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.archos.mediacenter.video.foundation.FoundationUiPolicy;
import com.archos.mediacenter.video.leanback.MainActivityLeanback;
import com.archos.mediacenter.video.leanback.PreviewStartupSurface;
import com.archos.mediacenter.video.utils.ThemeManager;
import com.archos.mediacenter.video.utils.VideoPreferencesCommon;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * TV entry point retaining the original launch intent's data and extras.
 * Created by vapillon on 08/06/15.
 */
public class EntryActivity extends AppCompatActivity {

    private static final Logger log = LoggerFactory.getLogger(EntryActivity.class);
    private SharedPreferences.OnSharedPreferenceChangeListener mThemeChangeListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Apply theme before super.onCreate() to avoid visual flash
        FoundationUiPolicy.apply(this);
        // EntryActivity uses launcher theme which is already neutral black
        // The TV activity applies its own theme.
        ((CustomApplication) getApplication()).loadLocale();
        super.onCreate(savedInstanceState);
        
        // Apply window theme after super.onCreate()
        getWindow().setBackgroundDrawable(new PreviewStartupSurface(this));
        if (log.isDebugEnabled()) log.debug("onCreate");
        startActivity(tvIntent(this, getIntent()));

        finish();

        mThemeChangeListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                if (VideoPreferencesCommon.KEY_APP_THEME.equals(key)) {
                    recreate();
                }
            }
        };
        ThemeManager.getInstance(this).registerThemeChangeListener(mThemeChangeListener);
    }

    static Intent tvIntent(Context context, Intent origin) {
        // Copy action, type, URI grants and ClipData as well as data/extras for external launches.
        return new Intent(origin).setClass(context, MainActivityLeanback.class)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
    }

    @Override
    protected void onResume() {
        super.onResume();
        ((CustomApplication) getApplication()).loadLocale();
    }

    @Override
    protected void onDestroy() {
        if (mThemeChangeListener != null) {
            ThemeManager.getInstance(this).unregisterThemeChangeListener(mThemeChangeListener);
        }
        super.onDestroy();
    }

}
