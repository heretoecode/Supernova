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
    @Test @GraphicsMode(GraphicsMode.Mode.NATIVE) @SuppressWarnings({"rawtypes","unchecked"}) public void actualResultClickRetainsDiagnosticEntryTokenUntilWindowReturns()throws Exception{
        var host=Robolectric.buildActivity(TopNavigationTest.Host.class).setup().visible();
        com.archos.mediacenter.video.diagnostics.Diagnostics.setEnabled(host.get(),true);
        try{
            try{com.squareup.picasso.Picasso.get();}catch(IllegalStateException missing){com.squareup.picasso.Picasso.setSingletonInstance(new com.squareup.picasso.Picasso.Builder(host.get().getApplicationContext()).build());}
            PreviewSearch search=new PreviewSearch(host.get(),0,null);TopNavigation shell=new TopNavigation(host.get(),search,n->{},search::atTop);host.get().setContentView(shell);
            var video=new com.archos.mediacenter.video.browser.adapters.object.Movie(1234,"/private-search-file","Private search result",0,"",2024,0,"",null,1000,0,0,0,false,false,false,false,0,0,1920,1080,null,null,null,null,0,1,1000,0);
            Class<?> result=Class.forName(PreviewSearch.class.getName()+"$Result");var constructor=result.getDeclaredConstructor(com.archos.mediacenter.video.browser.adapters.object.Video.class,String.class);constructor.setAccessible(true);
            java.util.List items=org.robolectric.util.ReflectionHelpers.getField(search,"items");items.add(constructor.newInstance(video,""));
            androidx.recyclerview.widget.RecyclerView results=org.robolectric.util.ReflectionHelpers.getField(search,"results");results.getAdapter().notifyDataSetChanged();PreviewPagesTest.layout(shell);
            View opener=results.findViewHolderForAdapterPosition(0).itemView;assertTrue(opener.requestFocus());opener.performClick();assertNotNull(Shadows.shadowOf(host.get()).getNextStartedActivity());
            search.onWindowFocusChanged(false);search.onWindowFocusChanged(true);Shadows.shadowOf(android.os.Looper.getMainLooper()).idle();search.onWindowFocusChanged(true);Shadows.shadowOf(android.os.Looper.getMainLooper()).idle();
            assertSame(opener,search.findFocus());PreviewPagesTest.layout(shell);PreviewPagesTest.capture(shell,"search-populated-return-focused");
            Object flight=org.robolectric.util.ReflectionHelpers.getStaticField(com.archos.mediacenter.video.diagnostics.Diagnostics.class,"FLIGHT");var snapshot=flight.getClass().getDeclaredMethod("snapshot",long.class);snapshot.setAccessible(true);String evidence=(String)snapshot.invoke(flight,android.os.SystemClock.elapsedRealtime());
            String token=null;int entries=0,returns=0;for(String line:evidence.split("\n")){if(line.isEmpty())continue;org.json.JSONObject row=new org.json.JSONObject(line);if("search.details".equals(row.optString("flow"))){entries++;token=row.getString("operation_id");assertEquals("semantic:search.result.video.1234",row.getString("opener"));}}
            assertNotNull(token);for(String line:evidence.split("\n")){if(line.isEmpty())continue;org.json.JSONObject row=new org.json.JSONObject(line);if(token.equals(row.optString("operation_id"))&&"focus_restoration".equals(row.optString("event"))){returns++;assertTrue(row.getBoolean("success"));assertFalse(row.getBoolean("fallback"));assertEquals("semantic:search.result.video.1234",row.getString("restored"));assertFalse(line.contains("Private search"));assertFalse(line.contains("private-search-file"));}}
            assertEquals(1,entries);assertEquals(1,returns);
        }finally{com.archos.mediacenter.video.diagnostics.Diagnostics.setEnabled(host.get(),false);host.pause().stop().destroy();}
    }
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
