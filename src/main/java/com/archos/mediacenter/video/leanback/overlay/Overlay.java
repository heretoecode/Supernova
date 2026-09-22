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

package com.archos.mediacenter.video.leanback.overlay;

import android.content.Context;
import androidx.leanback.app.BrowseSupportFragment;
import androidx.leanback.app.DetailsSupportFragment;
import androidx.leanback.app.GuidedStepSupportFragment;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.archos.customizedleanback.app.MyVerticalGridFragment;
import com.archos.mediacenter.video.R;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * ScannerAndScraperProgress must be created while in the Fragment.onViewCreated() AND must be "paused" and "resumed" in onPause() and onResume()
 * Created by vapillon on 26/05/15.
 */
public class Overlay {
    private static final Logger log = LoggerFactory.getLogger(Overlay.class);

    // For now i'm doing some basic polling...
    final static int REPEAT_PERIOD_MS = 1000;

    final Context mContext;
    final private View mOverlayRoot;

    ScannerAndScraperProgress mScanProgress;
    Clock mClock;

    /**
     * Must be created from the Fragment onViewCreated() method
     * @param fragment
     */
    public Overlay(Fragment fragment) {
        if (log.isDebugEnabled()) log.debug("Overlay: creation");
        if (!fragment.isAdded()) {
            throw new IllegalStateException("Overlay must be created once the fragment is added!");
        }

        mContext = fragment.getActivity();
        ViewGroup fragmentView = (ViewGroup)fragment.getView();
        if (fragmentView==null) {
            throw new IllegalStateException("Overlay must be created once the fragment has its view created!");
        }

        int parentViewId = -1;
        if (fragment instanceof BrowseSupportFragment) {
            parentViewId = R.id.browse_frame;
        } else if (fragment instanceof MyVerticalGridFragment) {
            parentViewId = R.id.browse_dummy;
        } else if (fragment instanceof DetailsSupportFragment) {
            parentViewId = R.id.details_fragment_root;
        } else if (fragment instanceof GuidedStepSupportFragment) {
            parentViewId = R.id.guidedstep_background_view_root;
        } else {
            throw new IllegalStateException("Overlay is not compatible with this fragment: "+fragment);
        }

        com.archos.mediacenter.video.leanback.TopNavigation previewNav=findPreviewNavigation(fragmentView);
        ViewGroup parentView = previewNav!=null
            ? previewNav.getStatusContainer()
            : (ViewGroup)fragmentView.findViewById(parentViewId);
        if (parentView==null) {
            throw new IllegalStateException("parentView not found! Maybe IDs in the leanback library have been changed?");
        }

        LayoutInflater.from(mContext).inflate(R.layout.leanback_overlay, parentView);
        mOverlayRoot = parentView.findViewById(R.id.overlay_root);
        mScanProgress = new ScannerAndScraperProgress(mContext, mOverlayRoot);
        mClock = new Clock(mContext, mOverlayRoot);
        if (previewNav!=null) {
            com.archos.mediacenter.video.leanback.TopNavigation nav = previewNav;
            View clock = mOverlayRoot.findViewById(R.id.clock);
            ((ViewGroup)clock.getParent()).removeView(clock);
            clock.setPadding(0, 0, 0, 0);
            ((android.widget.TextView)clock).setGravity(android.view.Gravity.CENTER);
            ((android.widget.TextView)clock).setTypeface(android.graphics.Typeface.create("sans-serif-light",android.graphics.Typeface.NORMAL));
            View fallbackClock=nav.getStatusContainer().findViewWithTag("preview-default-clock");if(fallbackClock!=null)nav.getStatusContainer().removeView(fallbackClock);
            nav.getStatusContainer().addView(clock, new android.widget.FrameLayout.LayoutParams(-1, -1));
            mScanProgress.useFloatingStyle();
            View progress = mOverlayRoot.findViewById(R.id.progress_group);
            ((ViewGroup)progress.getParent()).removeView(progress);
            nav.getScanContainer().addView(progress, new android.widget.FrameLayout.LayoutParams(-2, -2, android.view.Gravity.END | android.view.Gravity.BOTTOM));
        }
    }

    private static com.archos.mediacenter.video.leanback.TopNavigation findPreviewNavigation(View view){
        if(view instanceof com.archos.mediacenter.video.leanback.TopNavigation)return (com.archos.mediacenter.video.leanback.TopNavigation)view;
        if(view instanceof ViewGroup){ViewGroup group=(ViewGroup)view;for(int i=0;i<group.getChildCount();i++){com.archos.mediacenter.video.leanback.TopNavigation found=findPreviewNavigation(group.getChildAt(i));if(found!=null)return found;}}
        return null;
    }

    /**
     * MUST be called in the fragment onResume method
     */
    public void destroy() {
        if (log.isDebugEnabled()) log.debug("destroy");
        mScanProgress.destroy();
        mClock.destroy();
    }

    /**
     * MUST be called in the fragment onDestroyView method
     */
    public void resume() {
        if (log.isDebugEnabled()) log.debug("resume");
        mScanProgress.resume();
        mClock.resume();
    }

    /**
     * MUST be called in the fragment onPause method
     */
    public void pause() {
        if (log.isDebugEnabled()) log.debug("pause");
        mScanProgress.pause();
        mClock.pause();
    }

    /**
     * To be called whenever you want to hide the overlay widgets
     */
    public void hide() {
        if (log.isDebugEnabled()) log.debug("hide");
        mOverlayRoot.setVisibility(View.GONE);
    }

    public void show() {
        if (log.isDebugEnabled()) log.debug("show");
        mOverlayRoot.setVisibility(View.VISIBLE);
    }
}
