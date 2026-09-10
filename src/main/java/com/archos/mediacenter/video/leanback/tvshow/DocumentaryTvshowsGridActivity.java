// Copyright 2026
// Licensed under the Apache License, Version 2.0
package com.archos.mediacenter.video.leanback.tvshow;

import androidx.fragment.app.Fragment;

/** Activity entry point for the TV-friendly documentaries category. */
public class DocumentaryTvshowsGridActivity extends AllTvshowsGridActivity {
    @Override
    public Fragment getFragmentInstance() {
        return new DocumentaryTvshowsGridFragment();
    }
}
