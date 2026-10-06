package com.archos.mediacenter.video.leanback;

import android.app.*;
import android.os.Looper;
import android.view.*;
import android.widget.EditText;
import com.archos.mediascraper.*;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.*;
import org.robolectric.annotation.Config;
import static org.junit.Assert.*;

@RunWith(RobolectricTestRunner.class) @Config(application=Application.class,sdk=28,qualifiers="w960dp-h540dp-land-mdpi")
public class PreviewMatchSearchTest {
    @Test public void appendingResultsPreservesFocusAndSelectionRequiresReviewClick(){
        Activity host=Robolectric.buildActivity(Activity.class).setup().get();
        AtomicReference<BaseTags> chosen=new AtomicReference<>();
        PreviewMatchSearch page=new PreviewMatchSearch(host,"Example",q->{},chosen::set);
        host.setContentView(page);PreviewPagesTest.layout(page);
        assertTrue(page.findViewWithTag("semantic:keyboard:T").hasFocus());
        MovieTags first=movie("Example",2024),second=movie("Example sequel",2025);
        page.setResults(Collections.singletonList(first));PreviewPagesTest.layout(page);
        View result=page.findViewWithTag("semantic:match.result:0");assertTrue("Result has visible width",result.getWidth()>0);assertTrue("Result accepts focus before append",result.requestFocus());
        page.setResults(Arrays.asList(first,second));PreviewPagesTest.layout(page);
        assertSame("Appending keeps the existing row",result,page.findViewWithTag("semantic:match.result:0"));
        assertTrue("Appending preserves result focus",result.hasFocus());assertNull(chosen.get());
        result.performClick();assertSame(first,chosen.get());
        page.setResults(Collections.emptyList());assertTrue(page.findViewWithTag("semantic:keyboard:T").hasFocus());
    }
    @Test public void typingCoalescesQueriesAndDetachedPageDoesNotSearch(){
        Activity host=Robolectric.buildActivity(Activity.class).setup().get();List<String> queries=new ArrayList<>();
        PreviewMatchSearch page=new PreviewMatchSearch(host,"",queries::add,t->{});host.setContentView(page);PreviewPagesTest.layout(page);
        EditText input=page.findViewWithTag("semantic:match.query");input.setText("TT013");input.setText("TT0137523");
        Shadows.shadowOf(Looper.getMainLooper()).idleFor(Duration.ofMillis(349));assertTrue(queries.isEmpty());
        Shadows.shadowOf(Looper.getMainLooper()).idleFor(Duration.ofMillis(1));assertEquals(Collections.singletonList("TT0137523"),queries);
        input.setText("550");host.setContentView(new View(host));Shadows.shadowOf(Looper.getMainLooper()).idleFor(Duration.ofSeconds(1));assertEquals(1,queries.size());
    }
    @Test @org.robolectric.annotation.GraphicsMode(org.robolectric.annotation.GraphicsMode.Mode.NATIVE)
    public void resultDirectionStaysInListAndReturnsToKeyboard()throws Exception{
        Activity host=Robolectric.buildActivity(Activity.class).setup().get();PreviewMatchSearch page=new PreviewMatchSearch(host,"Example",q->{},t->{});host.setContentView(page);
        page.setResults(Arrays.asList(movie("Example",2024),movie("Another",2025)));PreviewPagesTest.layout(page);
        View first=page.findViewWithTag("semantic:match.result:0"),last=page.findViewWithTag("semantic:match.result:1");first.requestFocus();
        first.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN,KeyEvent.KEYCODE_DPAD_DOWN));assertTrue(last.hasFocus());
        last.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN,KeyEvent.KEYCODE_DPAD_DOWN));assertTrue(last.hasFocus());
        last.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN,KeyEvent.KEYCODE_DPAD_LEFT));assertTrue(page.findViewWithTag("semantic:keyboard:T").hasFocus());
        PreviewPagesTest.capture(page,"find-a-match");
    }
    private static MovieTags movie(String title,int year){MovieTags tags=new MovieTags();tags.setTitle(title);tags.setYear(year);return tags;}
}
