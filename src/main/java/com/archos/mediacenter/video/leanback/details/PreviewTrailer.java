package com.archos.mediacenter.video.leanback.details;

import android.app.*;
import android.content.*;
import android.net.Uri;
import android.text.TextUtils;
import android.webkit.*;
import android.view.*;
import android.widget.*;
import com.archos.mediacenter.video.leanback.PreviewDialog;
import com.archos.mediacenter.video.leanback.PreviewIcon;
import com.archos.mediascraper.ScraperTrailer;

/** Official embedded player, with an explicit fallback on devices without a WebView provider. */
public final class PreviewTrailer {
    public static void show(Activity activity, ScraperTrailer trailer) {
        if (!"YouTube".equals(trailer.mSite) || trailer.mVideoKey == null
            || !trailer.mVideoKey.matches("[A-Za-z0-9_-]{11}")) {
            Toast.makeText(activity, "This trailer format is not supported", Toast.LENGTH_LONG).show();
            return;
        }
        Uri fallback = Uri.parse("https://www.youtube.com/watch?v=" + trailer.mVideoKey);
        WebView web;
        try {
            web = new WebView(activity);
        } catch (RuntimeException unavailable) {
            PreviewDialog.choose(activity,
                "In-app trailers are unavailable on this device.", new String[] {
                    "Open YouTube", "Cancel"
                }, 0, n -> {
                    if (n == 0)
                        external(activity, fallback);
                });
            return;
        }
        web.getSettings().setMediaPlaybackRequiresUserGesture(false);
        web.getSettings().setJavaScriptEnabled(true);
        web.getSettings().setDomStorageEnabled(true);
        web.getSettings().setAllowFileAccess(false);
        web.getSettings().setAllowContentAccess(false);
        web.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_NEVER_ALLOW);
        web.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView v, WebResourceRequest r) {
                String host = r.getUrl().getHost();
                return host == null || !(host.equals("www.youtube.com") || host.equals("www.youtube-nocookie.com"));
            }
        });
        // Trailers (D19, owner 17 Nov 2026): the video nearly full screen with a small margin round it, the page
        // blurred behind, no title bar, no buttons and no YouTube controls; the highest quality asked for; Back
        // closes.
        Dialog dialog = PreviewDialog.create(activity, "trailer");
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        FrameLayout layout = new FrameLayout(activity);
        layout.setBackgroundColor(0xff000000);
        web.setBackgroundColor(0xff000000);
        web.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        web.setWebChromeClient(new WebChromeClient());
        web.setFocusable(false);
        web.setFocusableInTouchMode(false);
        layout.addView(web, new FrameLayout.LayoutParams(-1, -1));
        // DETAILS.md › Extras: a thin progress line at the foot, fed by the embedded player's own time.
        View progress = new View(activity);
        progress.setTag("trailer.progress");
        progress.setBackgroundColor(0xE6FFFFFF);
        progress.setPivotX(0);
        progress.setScaleX(0);
        layout.addView(progress, new FrameLayout.LayoutParams(-1, Math.max(2, dp(activity, 2)), Gravity.BOTTOM));
        web.addJavascriptInterface(new Object() {
            @JavascriptInterface
            public void progress(float fraction) {
                progress.post(() -> progress.setScaleX(Math.max(0f, Math.min(1f, fraction))));
            }
        }, "SupernovaTrailer");
        dialog.setContentView(layout);
        dialog.setOnDismissListener(d -> {
            web.stopLoading();
            web.loadUrl("about:blank");
            web.onPause();
            layout.removeView(web);
            web.destroy();
        });
        dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);
        dialog.show();
        Window window = dialog.getWindow();
        window.setBackgroundDrawableResource(android.R.color.transparent);
        window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        window.setDimAmount(.55f);
        if (android.os.Build.VERSION.SDK_INT >= 31) {
            window.addFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND);
            window.getAttributes().setBlurBehindRadius(dp(activity, 24));
            window.setAttributes(window.getAttributes());
        }
        int[] size = size(activity.getResources().getDisplayMetrics().widthPixels,
                          activity.getResources().getDisplayMetrics().heightPixels, dp(activity, 24));
        window.setLayout(size[0], size[1]);
        web.loadDataWithBaseURL("https://" + activity.getPackageName() + "/",
            "<html style='height:100%'><body style='margin:0;background:#000;height:100%;overflow:hidden'>"
                + "<iframe id='trailer' width='100%' height='100%' src='https://www.youtube.com/embed/"
                + trailer.mVideoKey
                + "?playsinline=1&autoplay=1&controls=0&disablekb=1&fs=0&rel=0&modestbranding=1&iv_load_policy=3"
                + "&vq=hd1080&enablejsapi=1' frameborder='0' allow='autoplay; encrypted-media' ></iframe>"
                + PROGRESS_SCRIPT + "</body></html>",
            "text/html", "UTF-8", null);
    }

    /** Reads the embedded player's time twice a second and hands the share played to the progress line. */
    static final String PROGRESS_SCRIPT = "<script src='https://www.youtube.com/iframe_api'></script><script>"
        + "var player;function onYouTubeIframeAPIReady(){player=new YT.Player('trailer');"
        + "setInterval(function(){try{var d=player.getDuration();if(d>0)"
        + "SupernovaTrailer.progress(player.getCurrentTime()/d);}catch(e){}},500);}</script>";

    /** The largest 16:9 window that leaves {@code margin} round it on the screen. Pure, for tests. */
    static int[] size(int screenWidth, int screenHeight, int margin) {
        int width = Math.max(1, screenWidth - 2 * margin);
        int height = Math.round(width * 9f / 16f);
        if (height > screenHeight - 2 * margin) {
            height = Math.max(1, screenHeight - 2 * margin);
            width = Math.round(height * 16f / 9f);
        }
        return new int[] {width, height};
    }

    private static int dp(Activity a, int n) {
        return Math.round(n * a.getResources().getDisplayMetrics().density);
    }

    private static void external(Activity a, Uri uri) {
        try {
            a.startActivity(new Intent(Intent.ACTION_VIEW, uri));
        } catch (ActivityNotFoundException e) {
            Toast.makeText(a, "No app available to open this trailer", Toast.LENGTH_LONG).show();
        }
    }
}
