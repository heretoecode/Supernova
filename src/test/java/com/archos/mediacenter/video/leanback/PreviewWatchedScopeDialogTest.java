package com.archos.mediacenter.video.leanback;

import android.app.*;
import android.view.View;
import com.archos.mediacenter.video.browser.adapters.object.Season;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.*;
import org.robolectric.annotation.Config;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import static org.junit.Assert.*;

@RunWith(RobolectricTestRunner.class) @Config(application=Application.class,sdk=28)
public class PreviewWatchedScopeDialogTest {
    @Test public void seasonSelectionChangesOnlyThatScopeAndBackDoesNothing(){
        Activity activity=Robolectric.buildActivity(Activity.class).setup().get();
        Season one=new Season(8,"Show",null,1,4,4),two=new Season(8,"Show",null,2,6,2);
        AtomicReference<List<Season>> selected=new AtomicReference<>();AtomicReference<Boolean> watched=new AtomicReference<>();
        Dialog back=PreviewWatchedScopeDialog.show(activity,Arrays.asList(one,two),(scope,state)->selected.set(scope));back.dismiss();assertNull(selected.get());
        Dialog dialog=PreviewWatchedScopeDialog.show(activity,Arrays.asList(one,two),(scope,state)->{selected.set(scope);watched.set(state);});
        ((View)dialog.getWindow().getDecorView().findViewWithTag("preview-label:1").getParent()).performClick();
        assertEquals(Collections.singletonList(one),selected.get());assertFalse(watched.get());assertFalse(dialog.isShowing());
    }
    @Test public void entireMixedSeriesMarksEverySeasonWatched(){
        Activity activity=Robolectric.buildActivity(Activity.class).setup().get();List<Season> seasons=Arrays.asList(new Season(8,"Show",null,0,2,0),new Season(8,"Show",null,1,4,4));
        AtomicReference<List<Season>> selected=new AtomicReference<>();AtomicReference<Boolean> watched=new AtomicReference<>();
        Dialog dialog=PreviewWatchedScopeDialog.show(activity,seasons,(scope,state)->{selected.set(scope);watched.set(state);});
        ((View)dialog.getWindow().getDecorView().findViewWithTag("preview-label:0").getParent()).performClick();assertEquals(seasons,selected.get());assertTrue(watched.get());
    }
}
