package com.archos.mediacenter.video.leanback;

import android.app.Application;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.archos.mediacenter.video.browser.adapters.object.Movie;
import com.archos.mediacenter.video.leanback.PreviewLibraryLoader.Entry;
import com.archos.mediacenter.video.leanback.PreviewLibraryLoader.Snapshot;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import static org.junit.Assert.*;

@RunWith(org.robolectric.RobolectricTestRunner.class)
@Config(application=Application.class,sdk=28,qualifiers="w960dp-h540dp-land-mdpi")
public class PreviewLibraryReturnTest {
    @Test public void gridReturnRetainsExactItemAndScrolledPositionAfterRefresh(){checkReturn(false);}
    @Test public void listReturnRetainsExactRowAndViewModeAfterRefresh(){checkReturn(true);}

    private void checkReturn(boolean table){
        try{com.squareup.picasso.Picasso.get();}catch(IllegalStateException missing){com.squareup.picasso.Picasso.setSingletonInstance(new com.squareup.picasso.Picasso.Builder(RuntimeEnvironment.getApplication()).build());}
        androidx.preference.PreferenceManager.getDefaultSharedPreferences(RuntimeEnvironment.getApplication()).edit().putBoolean("preview_library_1_list",table).apply();
        var host=Robolectric.buildActivity(TopNavigationTest.Host.class).setup().visible();
        try{
            long[] opened={-1};PreviewPages pages=new PreviewPages(host.get(),(holder,item)->opened[0]=((Movie)item).getId());
            FrameLayout container=new FrameLayout(host.get());container.addView(pages,new FrameLayout.LayoutParams(-1,-1));
            TextView child=new TextView(host.get());child.setFocusableInTouchMode(true);child.setText("Details stand-in");child.setVisibility(View.GONE);container.addView(child);
            host.get().setContentView(container);pages.setDiscovery(new PreviewDiscovery());pages.setSnapshot(snapshot(false));pages.setTab(1);PreviewPagesTest.layout(container);
            RecyclerView list=(RecyclerView)pages.getChildAt(0);GridLayoutManager layout=(GridLayoutManager)list.getLayoutManager();
            // 60 records sorted newest first, plus the summary and toolbar cells.
            int position=28;layout.scrollToPositionWithOffset(position,120);PreviewPagesTest.layout(container);
            View item=list.findViewHolderForAdapterPosition(position).itemView;assertEquals("v34",item.getTag());assertTrue(item.requestFocus());PreviewPagesTest.layout(container);
            int top=item.getTop();int first=layout.findFirstVisibleItemPosition();assertTrue(first>2);assertEquals(table?24:4,layout.getSpanSizeLookup().getSpanSize(position));
            item.performClick();assertEquals(34,opened[0]);
            child.setVisibility(View.VISIBLE);assertTrue(child.requestFocus());assertFalse(pages.hasFocus());
            pages.setSnapshot(snapshot(true));PreviewPagesTest.layout(container);assertSame(child,container.findFocus());
            child.setVisibility(View.GONE);pages.requestFocus();PreviewPagesTest.layout(container);
            assertNotNull(pages.findFocus());assertEquals("v34",pages.findFocus().getTag());
            View restored=list.findContainingItemView(pages.findFocus());assertEquals(top,restored.getTop());assertEquals(first,layout.findFirstVisibleItemPosition());
            assertEquals(table?24:4,layout.getSpanSizeLookup().getSpanSize(list.getChildAdapterPosition(restored)));
        }finally{host.pause().stop().destroy();}
    }
    private Snapshot snapshot(boolean updated){
        Snapshot result=new Snapshot();
        for(int id=1;id<=60;id++){
            Movie movie=new Movie(id,"/fixture/"+id,"Film "+id,id,updated?"Refreshed synopsis":"Synopsis",2024,7,"",null,100000,0,0,0,false,false,false,false,id,id,1920,1080,null,null,null,null,0,1,1000,0);
            result.movies.add(new Entry(movie,id,0,"Drama"));
        }
        return result;
    }
}
