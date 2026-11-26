package com.archos.mediacenter.video.leanback;

import android.content.Context;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.widget.EditText;
import androidx.appcompat.widget.AppCompatTextView;

/**
 * A key of the one keyboard (RULES §15). No Phone key (owner, 25 Nov 2026): while a key holds the remote's focus it
 * tells Android that the text box is being typed into and hands over the box's own input connection, so the phone
 * app's typing box (and paste) lands in the box with no button to press. Android's on-screen keyboard stays hidden.
 */
final class KeyboardKey extends AppCompatTextView
{
    private final EditText target;

    KeyboardKey(Context context, EditText target)
    {
        super(context);
        this.target = target;
        setShowSoftInputOnFocus(false);
    }

    @Override
    public boolean onCheckIsTextEditor()
    {
        return target != null && target.isEnabled();
    }

    @Override
    public InputConnection onCreateInputConnection(EditorInfo out)
    {
        if (target == null)
            return null;
        InputConnection connection = target.onCreateInputConnection(out);
        // Android's own keyboard never opens over Supernova's: only the connection is offered.
        out.imeOptions |= EditorInfo.IME_FLAG_NO_FULLSCREEN | EditorInfo.IME_FLAG_NO_EXTRACT_UI;
        return connection;
    }
}
