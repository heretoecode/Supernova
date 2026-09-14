package com.archos.mediacenter.video.leanback;
import android.app.Application;
import android.os.Bundle;
import android.view.*;
import android.widget.*;
import androidx.fragment.app.FragmentActivity;
import androidx.leanback.app.BrowseSupportFragment;
import androidx.leanback.widget.*;
import com.archos.mediacenter.video.R;
import org.junit.*;
import org.junit.runner.RunWith;
import org.robolectric.*;
import org.robolectric.annotation.Config;
import static org.junit.Assert.*;
@RunWith(RobolectricTestRunner.class)
@Config(application=Application.class,sdk=28)
public class TopNavigationTest {
    public static class Host extends FragmentActivity {
        @Override public void onCreate(Bundle state) {
            setTheme(R.style.MyLeanbackTheme); super.onCreate(state);
            FrameLayout frame=new FrameLayout(this);frame.setId(android.R.id.content);setContentView(frame);
        }
    }
    public static class Browse extends BrowseSupportFragment {
        @Override public View onCreateView(LayoutInflater i,ViewGroup p,Bundle b) {
            return new TopNavigation(requireContext(),super.onCreateView(i,p,b),tab->{},()->true);
        }
        @Override public void onViewCreated(View v,Bundle state) {
            super.onViewCreated(v,state);setHeadersState(HEADERS_DISABLED);showTitle(false);
            setAdapter(new ArrayObjectAdapter(new ListRowPresenter()));
        }
    }
    @Test public void topShellWorksWithNativeBrowseAndCanFocusNavigation() {
        org.robolectric.android.controller.ActivityController<Host> host=Robolectric.buildActivity(Host.class).setup();
        try {
            Browse browse=new Browse();
            host.get().getSupportFragmentManager().beginTransaction().add(android.R.id.content,browse).commitNow();
            assertTrue(browse.getView() instanceof TopNavigation);
            assertEquals(BrowseSupportFragment.HEADERS_DISABLED,browse.getHeadersState());
            ((TopNavigation)browse.getView()).focusNavigation();
            assertTrue(browse.getView().hasFocus());
        } finally { host.pause().stop().destroy(); }
    }
    @Test public void experimentalPlaybackLayoutKeepsNativeControlTypes() {
        org.robolectric.android.controller.ActivityController<Host> host=Robolectric.buildActivity(Host.class).setup();
        try {
            View hud=LayoutInflater.from(host.get()).inflate(R.layout.player_controller_experimental,null);
            assertTrue(hud.findViewById(R.id.seek_progress) instanceof android.widget.SeekBar);
            assertTrue(hud.findViewById(R.id.pause) instanceof ImageButton);
            assertNotNull(hud.findViewById(R.id.my_recycler_view));
            assertNotNull(hud.findViewById(R.id.time_current));
        } finally {host.pause().stop().destroy();}
    }

}
