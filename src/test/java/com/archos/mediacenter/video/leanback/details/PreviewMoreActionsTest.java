package com.archos.mediacenter.video.leanback.details;

import androidx.leanback.widget.Action;
import com.archos.mediacenter.video.leanback.tvshow.TvshowActionAdapter;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import static org.junit.Assert.*;

@RunWith(RobolectricTestRunner.class) @Config(application=android.app.Application.class,sdk=28)
public class PreviewMoreActionsTest {
    @Test public void translatedLabelsCannotRestoreRemovedActions(){
        for(int id:new int[]{VideoActionAdapter.ACTION_RESUME,VideoActionAdapter.ACTION_PLAY,VideoActionAdapter.ACTION_LIST_EPISODES,VideoActionAdapter.ACTION_UNSCRAP,VideoActionAdapter.ACTION_ADD_TO_LIST})
            assertEquals(-1,PreviewMoreActions.group(new Action(id,"Libellé traduit"),false));
        assertEquals(1,PreviewMoreActions.group(new Action(VideoActionAdapter.ACTION_SCRAP,"Modifier"),false));
        assertEquals(3,PreviewMoreActions.group(new Action(VideoActionAdapter.ACTION_DELETE,"Supprimer"),false));
    }
    @Test public void televisionAndMovieActionNamespacesRemainDistinct(){
        assertEquals(-1,PreviewMoreActions.group(new Action(TvshowActionAdapter.ACTION_MORE_DETAILS,"Details"),true));
        assertEquals(1,PreviewMoreActions.group(new Action(TvshowActionAdapter.ACTION_MARK_SHOW_AS_WATCHED,"Vu"),true));
        assertEquals(3,PreviewMoreActions.group(new Action(TvshowActionAdapter.ACTION_DELETE,"Supprimer"),true));
        assertEquals(-1,PreviewMoreActions.group(new Action(VideoActionAdapter.ACTION_PLAY,"Play"),false));
    }
}
