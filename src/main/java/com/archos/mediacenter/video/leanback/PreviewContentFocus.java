package com.archos.mediacenter.video.leanback;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.StateListDrawable;

/** Focus decorates the control boundary; content colours and selection are independent. */
final class PreviewContentFocus extends StateListDrawable {
    PreviewContentFocus(Context context) {
        addState(new int[]{android.R.attr.state_focused}, new PreviewFocusGlow(context));
        addState(new int[]{}, new ColorDrawable(Color.TRANSPARENT));
    }
}
