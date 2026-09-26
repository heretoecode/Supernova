package com.archos.mediacenter.video.player;

import android.app.*;
import android.view.View;
import android.widget.Button;
import com.archos.mediacenter.video.R;
import com.archos.mediacenter.video.player.tvmenu.*;
import java.util.Collections;
import org.junit.*;
import org.junit.runner.RunWith;
import org.robolectric.*;
import org.robolectric.annotation.Config;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/** Real menu/dialog and track callbacks; no native playback service or media decoding is started. */
@RunWith(RobolectricTestRunner.class) @Config(application=Application.class,sdk=28,qualifiers="w960dp-h540dp-land-mdpi")
public class PreviewPlaybackMenusTest {
    @After public void close(){PreviewPlaybackMenus.close();}
    @Test public void audioTrackChangeStaysOpenAndBackReturnsToHudInsteadOfMore(){
        Activity host=Robolectric.buildActivity(Activity.class).setup().get();Button opener=new Button(host);opener.setText("Audio");opener.setFocusableInTouchMode(true);host.setContentView(opener);opener.requestFocus();Shadows.shadowOf(host).setCurrentFocus(opener);
        TVMenu menu=new TVMenu(host);TVMenuItem english=menu.createAndAddTVMenuItem("English",true,true),french=menu.createAndAddTVMenuItem("French",true,false);
        english.setOnClickListener(v->{english.setChecked(true);french.setChecked(false);});french.setOnClickListener(v->{english.setChecked(false);french.setChecked(true);});
        TVCardView card=mock(TVCardView.class);when(card.previewTitle()).thenReturn(host.getString(R.string.menu_audio));when(card.previewMenu()).thenReturn(menu);
        TVMenuAdapter adapter=mock(TVMenuAdapter.class);when(adapter.previewCards()).thenReturn(Collections.singletonList(card));
        PreviewPlaybackMenus.show(host,adapter,host.getString(R.string.menu_audio));Dialog dialog=org.robolectric.shadows.ShadowDialog.getLatestDialog();
        // A group heading precedes the two real track choices.
        View label=dialog.getWindow().getDecorView().findViewWithTag("preview-label:2");assertNotNull(label);((View)label.getParent()).performClick();
        assertTrue(dialog.isShowing());assertTrue(french.isChecked());assertFalse(english.isChecked());
        assertEquals(View.VISIBLE,dialog.getWindow().getDecorView().findViewWithTag("preview-check:2").getVisibility());
        assertTrue(PreviewPlaybackMenus.back());assertFalse(dialog.isShowing());assertFalse("Back must not open More",PreviewPlaybackMenus.back());assertTrue(opener.hasFocus());
    }
}
