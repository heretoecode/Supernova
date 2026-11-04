package com.archos.mediacenter.video.browser;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import com.archos.mediacenter.video.leanback.search.VideoSearchActivity;

/** Compatibility component for existing search shortcuts and integrations. */
public class QueryBrowserActivityVideo extends Activity {
    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);
        startActivity(tvIntent(this, getIntent()));
        finish();
    }

    public static Intent tvIntent(Context context, Intent origin) {
        // Copy query, action, data/type, all extras, URI grants, ClipData and task flags.
        return new Intent(origin).setClass(context, VideoSearchActivity.class);
    }
}
