package com.archos.mediacenter.video.player;

import android.app.Activity;
import android.app.Application;
import android.app.PendingIntent;
import android.app.RemoteAction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Icon;
import android.os.Build;
import android.os.Bundle;
import com.archos.mediacenter.video.R;
import java.util.List;
import java.util.UUID;

/** Android PiP control transport. Activity owns layout and the platform owns the PiP menu. */
final class PipPlaybackActions implements AutoCloseable
{
    static final String RETURN = "supernova_pip_return";
    private final Activity activity;
    private final String action;
    private final BroadcastReceiver receiver;
    private final Application.ActivityLifecycleCallbacks lifecycle;
    private boolean closed;

    PipPlaybackActions(Activity activity, Runnable toggle)
    {
        this.activity = activity;
        action = activity.getPackageName() + ".PIP." + UUID.randomUUID();
        receiver = new BroadcastReceiver() {
            @Override public void onReceive(Context context, Intent intent)
            {
                if (closed || !action.equals(intent.getAction()))
                {
                    return;
                }
                if (intent.getBooleanExtra("fullscreen", false))
                {
                    fullScreen();
                }
                else
                {
                    toggle.run();
                }
            }
        };
        if (Build.VERSION.SDK_INT >= 33)
        {
            activity.registerReceiver(receiver, new IntentFilter(action), Context.RECEIVER_NOT_EXPORTED);
        }
        else
        {
            activity.registerReceiver(receiver, new IntentFilter(action));
        }
        lifecycle = new Application.ActivityLifecycleCallbacks() {
            @Override public void onActivityResumed(Activity opened)
            {
                if (opened != activity && opened.getPackageName().equals(activity.getPackageName()) &&
                    Build.VERSION.SDK_INT >= 24 && activity.isInPictureInPictureMode())
                {
                    fullScreen();
                }
            }
            @Override public void onActivityCreated(Activity opened, Bundle state)
            {
            }
            @Override public void onActivityStarted(Activity opened)
            {
            }
            @Override public void onActivityPaused(Activity opened)
            {
            }
            @Override public void onActivityStopped(Activity opened)
            {
            }
            @Override public void onActivitySaveInstanceState(Activity opened, Bundle state)
            {
            }
            @Override public void onActivityDestroyed(Activity opened)
            {
            }
        };
        activity.getApplication().registerActivityLifecycleCallbacks(lifecycle);
    }

    List<RemoteAction> actions(boolean playing)
    {
        return List.of(
            remote(playing ? android.R.drawable.ic_media_pause : android.R.drawable.ic_media_play,
                   activity.getString(playing ? R.string.floating_player_pause : R.string.floating_player_play), false),
            remote(android.R.drawable.ic_menu_view, activity.getString(R.string.floating_player_restore), true));
    }

    private RemoteAction remote(int drawable, String label, boolean fullscreen)
    {
        Intent intent = new Intent(action).setPackage(activity.getPackageName()).putExtra("fullscreen", fullscreen);
        PendingIntent pending = PendingIntent.getBroadcast(
            activity, fullscreen ? 1 : 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        return new RemoteAction(Icon.createWithResource(activity, drawable), label, label, pending);
    }

    void fullScreen()
    {
        if (!closed)
        {
            Intent intent = new Intent(activity, activity.getClass())
                                .addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_SINGLE_TOP)
                                .putExtra(RETURN, true);
            activity.startActivity(intent);
        }
    }

    @Override public void close()
    {
        if (!closed)
        {
            closed = true;
            activity.unregisterReceiver(receiver);
            activity.getApplication().unregisterActivityLifecycleCallbacks(lifecycle);
        }
    }
}
