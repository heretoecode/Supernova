package com.archos.mediacenter.video.leanback;

import android.app.*;
import android.view.*;
import android.widget.*;
import com.archos.mediacenter.video.streaming.PreviewProviderIcons;
import org.junit.*;
import org.junit.runner.RunWith;
import org.robolectric.*;
import org.robolectric.annotation.Config;
import static org.junit.Assert.*;

@RunWith(RobolectricTestRunner.class) @Config(application=Application.class,sdk=28,qualifiers="w960dp-h540dp-land-mdpi")
public class PreviewProviderCorrectionsTest {
    @Before public void initialiseLoader(){try{com.squareup.picasso.Picasso.get();}catch(IllegalStateException absent){com.squareup.picasso.Picasso.setSingletonInstance(new com.squareup.picasso.Picasso.Builder(RuntimeEnvironment.getApplication()).build());}}
    @Test public void myProvidersClearsMonochromeWhileOtherSurfacesKeepTheirTreatment(){
        ImageView image=new ImageView(RuntimeEnvironment.getApplication());
        PreviewProviderIcons.bind(image,null,0,"details.providers");assertNotNull(image.getColorFilter());
        PreviewProviderIcons.bind(image,null,0,"settings.providers");assertNull(image.getColorFilter());assertNotNull(image.getDrawable());
    }
    @Test public void doneIsAFixedDistinctActionBelowTheProviderViewport(){
        Activity host=Robolectric.buildActivity(Activity.class).setup().get();
        Dialog dialog=PreviewDialog.choose(host,"My Providers",new String[]{"Provider One","Provider Two","Done"},0,i->{});
        PreviewDialog.separateDone(dialog,2);View root=dialog.getWindow().getDecorView();
        LinearLayout done=root.findViewWithTag(2);assertNotNull(done.getBackground());assertEquals(View.GONE,done.getChildAt(0).getVisibility());
        assertTrue(((LinearLayout.LayoutParams)done.getLayoutParams()).topMargin>0);
        View ancestor=done;while(ancestor.getParent() instanceof View){ancestor=(View)ancestor.getParent();assertFalse("Done stays outside scrolling provider rows",ancestor instanceof ScrollView);}
        assertEquals(View.GONE,root.findViewWithTag("preview-check:2").getVisibility());dialog.dismiss();host.finish();
    }
}
