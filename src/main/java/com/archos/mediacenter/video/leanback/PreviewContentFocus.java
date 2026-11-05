package com.archos.mediacenter.video.leanback;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.StateListDrawable;

/** Row focus from the shared style (white outline, no fill change). */
final class PreviewContentFocus extends StateListDrawable {
    PreviewContentFocus(Context context) {
        addState(new int[]{android.R.attr.state_focused}, SupernovaStyle.rowFocusOutline(context));
        addState(new int[]{}, new ColorDrawable(Color.TRANSPARENT));
    }
}
