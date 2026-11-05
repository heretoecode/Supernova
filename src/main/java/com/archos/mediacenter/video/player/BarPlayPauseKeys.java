package com.archos.mediacenter.video.player;

import android.view.KeyEvent;

/** Called only while the progress bar owns focus. A press toggles once; repeats/releases are consumed. */
final class BarPlayPauseKeys
{
    boolean handle(KeyEvent event, Runnable toggle)
    {
        int key = event.getKeyCode();
        if (key != KeyEvent.KEYCODE_DPAD_CENTER && key != KeyEvent.KEYCODE_ENTER
            && key != KeyEvent.KEYCODE_NUMPAD_ENTER)
        {
            return false;
        }
        if (event.getAction() == KeyEvent.ACTION_DOWN && event.getRepeatCount() == 0)
        {
            toggle.run();
        }
        return true;
    }
}
