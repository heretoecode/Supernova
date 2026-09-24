package com.archos.mediacenter.video.leanback;

import android.app.Application;
import android.view.*;
import android.widget.*;
import com.archos.mediacenter.video.R;
import org.junit.*;
import org.junit.runner.RunWith;
import org.robolectric.*;
import org.robolectric.annotation.Config;
import static org.junit.Assert.*;

@RunWith(RobolectricTestRunner.class)
@Config(application=Application.class,sdk=28,qualifiers="w960dp-h540dp-land-mdpi")
public class PreviewNextTest {
    @Test public void keyboardHasPermanentNumbersAndDeletesWholeCodePoints() {
        Application app=RuntimeEnvironment.getApplication();
        EditText input=new EditText(app);
        PreviewKeyboard keyboard=new PreviewKeyboard(app,input,()->{});
        assertArrayEquals(new String[]{"1234567890","QWERTYUIOP","ASDFGHJKL","ZXCVBNM"},PreviewKeyboard.ROWS);
        assertEquals(5,keyboard.getChildCount());
        PreviewPagesTest.findText(keyboard,"1").performClick();
        assertEquals("1",input.getText().toString());
        input.setText("A😀");input.setSelection(input.length());
        PreviewPagesTest.findText(keyboard,"Delete").performClick();
        assertEquals("A",input.getText().toString());
        input.setSelection(0,input.length());
        PreviewPagesTest.findText(keyboard,"Q").performClick();
        assertEquals("Q",input.getText().toString());
    }
    @Test public void audioSortStateSurvivesColumnModelRecreation() {
        Application app=RuntimeEnvironment.getApplication();
        PreviewLibraryColumns columns=new PreviewLibraryColumns(app,false);
        assertTrue(columns.available().contains(PreviewLibraryColumns.Column.AUDIO));
        columns.setSort(PreviewLibraryColumns.Column.AUDIO,false);
        PreviewLibraryColumns restored=new PreviewLibraryColumns(app,false);
        assertEquals(PreviewLibraryColumns.Column.AUDIO,restored.sortColumn);
        assertFalse(restored.ascending);
    }
    @Test public void hudHasExactlyFiveVisibleControlGroups() {
        var host=Robolectric.buildActivity(TopNavigationTest.Host.class).setup();
        try {
            View hud=LayoutInflater.from(host.get()).inflate(R.layout.player_controller_experimental,null);
            ViewGroup transport=hud.findViewById(R.id.preview_transport);
            assertEquals(5,transport.getChildCount());
            int[] ids={R.id.preview_subtitles,R.id.preview_audio,R.id.pause,R.id.preview_info,R.id.preview_more};
            for(int i=0;i<ids.length;i++) {
                View button=hud.findViewById(ids[i]);
                assertEquals(View.VISIBLE,button.getVisibility());
                assertSame(transport.getChildAt(i),button.getParent());
            }
            for(int id:new int[]{R.id.preview_previous,R.id.preview_next,R.id.backward,R.id.forward})
                assertEquals(View.GONE,hud.findViewById(id).getVisibility());
        } finally {host.pause().stop().destroy();}
    }
}
