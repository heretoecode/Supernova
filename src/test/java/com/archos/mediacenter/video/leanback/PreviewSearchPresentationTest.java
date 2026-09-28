package com.archos.mediacenter.video.leanback;

import android.app.Application;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import com.archos.mediacenter.video.leanback.search.PreviewSearch;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.*;
import org.robolectric.annotation.*;
import static org.junit.Assert.*;

@RunWith(RobolectricTestRunner.class) @Config(application=Application.class,sdk=28,qualifiers="w960dp-h540dp-land-mdpi")
public class PreviewSearchPresentationTest {
    @Test @GraphicsMode(GraphicsMode.Mode.NATIVE) public void emptySearchHasNonFocusableQueryAndStartsOnT()throws Exception{
        var host=Robolectric.buildActivity(TopNavigationTest.Host.class).setup().visible();
        try{
            PreviewSearch search=new PreviewSearch(host.get(),0,null);TopNavigation nav=new TopNavigation(host.get(),search,n->{},search::atTop);host.get().setContentView(nav);PreviewPagesTest.layout(nav);search.focusQuery();
            assertTrue(search.findViewWithTag("semantic:keyboard:T").hasFocus());EditText query=findQuery(search);assertNotNull(query);assertFalse(query.isFocusable());assertFalse(query.isCursorVisible());assertEquals("Search Movies and TV Shows",query.getHint().toString());
            assertNull(PreviewPagesTest.findText(search,"Caps"));assertNull(PreviewPagesTest.findText(search,"Shift"));
            PreviewPagesTest.capture(nav,"search-empty-next");
        }finally{host.pause().stop().destroy();}
    }
    @Test public void savedKeyboardFocusSurvivesSurfaceRecreation(){
        var host=Robolectric.buildActivity(TopNavigationTest.Host.class).setup().visible();
        try{
            Bundle state=new Bundle();state.putString("preview_search_key","P");state.putString("preview_search_query","");
            PreviewSearch search=new PreviewSearch(host.get(),0,state);host.get().setContentView(search);PreviewPagesTest.layout(search);search.focusQuery();assertTrue(search.findViewWithTag("semantic:keyboard:P").hasFocus());
            Bundle saved=new Bundle();search.save(saved);assertEquals("P",saved.getString("preview_search_key"));assertEquals("",saved.getString("preview_search_query"));
        }finally{host.pause().stop().destroy();}
    }
    private static EditText findQuery(View view){if(view instanceof EditText)return (EditText)view;if(view instanceof android.view.ViewGroup)for(int i=0;i<((android.view.ViewGroup)view).getChildCount();i++){EditText found=findQuery(((android.view.ViewGroup)view).getChildAt(i));if(found!=null)return found;}return null;}
}
