package com.archos.mediacenter.video.leanback;

import android.app.*;
import android.graphics.*;
import android.view.*;
import android.widget.*;
import com.archos.mediacenter.video.streaming.StreamingRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.*;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowDialog;
import static org.junit.Assert.*;

@RunWith(RobolectricTestRunner.class)
@Config(application=Application.class,sdk=28,qualifiers="w960dp-h540dp-land-mdpi")
public class PreviewProviderFilterTest {
    @Test public void actualFiltersRetainMultiSelectionAcrossReopenAndRecreation()throws Exception{
        var host=Robolectric.buildActivity(TopNavigationTest.Host.class).setup().visible();
        try{
            try{com.squareup.picasso.Picasso.get();}catch(IllegalStateException missing){com.squareup.picasso.Picasso.setSingletonInstance(new com.squareup.picasso.Picasso.Builder(host.get().getApplicationContext()).build());}
            var prefs=StreamingRepository.prefs(host.get());
            prefs.edit().clear().putString(StreamingRepository.COUNTRY,"IE")
                .putStringSet(StreamingRepository.PROVIDERS+"IE",new java.util.HashSet<>(java.util.Arrays.asList("8","9")))
                // Missing logo data must retain a neutral, monochrome fallback;
                // this local persistence test makes no live artwork requests.
                .putString("streaming_catalogue_IE","[{\"id\":8,\"name\":\"Netflix\",\"logo\":\"\"},{\"id\":9,\"name\":\"Prime Video\",\"logo\":\"\"}]")
                .putString("preview_library_1_genre","Drama").putString("preview_library_1_years","2024|2025")
                .putString("preview_library_2_providers","9").commit();
            PreviewPages pages=page(host.get());pages.setTab(1);
            Dialog menu=providers(pages);
            assertMonochrome(menu,0);assertMonochrome(menu,1);
            row(menu,0).performClick();row(menu,1).performClick();
            assertTrue(menu.isShowing());assertChecked(menu,0,true);assertChecked(menu,1,true);
            assertNull("Live filters have no Done",menu.getWindow().getDecorView().findViewWithTag("preview-label:3"));menu.cancel();assertFalse(menu.isShowing());
            assertEquals(java.util.Set.of("8","9"),PreviewGenres.parse(prefs.getString("preview_library_1_providers","")));
            assertEquals("9",prefs.getString("preview_library_2_providers",""));
            menu=providers(pages);assertChecked(menu,0,true);assertChecked(menu,1,true);menu.dismiss();
            pages=page(host.get());pages.setTab(1);menu=providers(pages);
            assertChecked(menu,0,true);assertChecked(menu,1,true);
            row(menu,2).performClick();assertTrue(menu.isShowing());assertChecked(menu,0,false);assertChecked(menu,1,false);
            assertEquals("",prefs.getString("preview_library_1_providers","not saved"));menu.cancel();
            // Clear Filters resets the other persisted dimensions as well.
            Dialog filters=filters(pages);row(filters,3).performClick();
            assertEquals("",prefs.getString("preview_library_1_genre","not saved"));
            assertEquals("",prefs.getString("preview_library_1_years","not saved"));
            pages.setTab(2);menu=providers(pages);assertChecked(menu,0,false);assertChecked(menu,1,true);menu.dismiss();
        }finally{host.pause().stop().destroy();}
    }
    private static PreviewPages page(Activity host){
        PreviewPages page=new PreviewPages(host,(holder,item)->{});host.setContentView(page);
        page.setSnapshot(new PreviewLibraryLoader.Snapshot());return page;
    }
    private static Dialog filters(PreviewPages pages)throws Exception{
        var filter=PreviewPages.class.getDeclaredMethod("filter");filter.setAccessible(true);filter.invoke(pages);return ShadowDialog.getLatestDialog();
    }
    private static Dialog providers(PreviewPages pages)throws Exception{row(filters(pages),2).performClick();return ShadowDialog.getLatestDialog();}
    private static View row(Dialog menu,int index){return (View)menu.getWindow().getDecorView().findViewWithTag("preview-label:"+index).getParent();}
    private static void assertChecked(Dialog menu,int index,boolean selected){assertEquals(selected?View.VISIBLE:View.INVISIBLE,menu.getWindow().getDecorView().findViewWithTag("preview-check:"+index).getVisibility());}
    private static void assertMonochrome(Dialog menu,int index){
        ImageView icon=(ImageView)((ViewGroup)row(menu,index)).getChildAt(0);
        assertNotNull(icon.getDrawable());assertTrue(icon.getColorFilter() instanceof ColorMatrixColorFilter);
        ColorMatrix matrix=new ColorMatrix();((ColorMatrixColorFilter)icon.getColorFilter()).getColorMatrix(matrix);
        float[] values=matrix.getArray();for(int channel=0;channel<3;channel++){assertEquals(values[channel],values[channel+5],.0001f);assertEquals(values[channel],values[channel+10],.0001f);}
        assertEquals(1f,values[18],.0001f); // Preserve alpha, not a solid white square.
    }
}
