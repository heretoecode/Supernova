package com.archos.mediacenter.video.leanback;

import android.app.Activity;
import android.app.Application;
import android.view.*;
import android.widget.*;
import androidx.recyclerview.widget.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.*;
import org.robolectric.annotation.Config;
import static org.junit.Assert.*;

@RunWith(RobolectricTestRunner.class)
@Config(application = Application.class, sdk = 28, qualifiers = "w960dp-h540dp-land-mdpi")
public class Preview417NavigationTest {
    private static void layout(View view) {
        view.measure(View.MeasureSpec.makeMeasureSpec(960, View.MeasureSpec.EXACTLY),
                View.MeasureSpec.makeMeasureSpec(540, View.MeasureSpec.EXACTLY));
        view.layout(0, 0, 960, 540);
    }
    @Test public void keyboardStartsAtTAndEmptyResultEdgeKeepsFocus() {
        Activity host = Robolectric.buildActivity(TopNavigationTest.Host.class).setup().visible().get();
        EditText input = new EditText(host);
        input.setFocusable(false);
        PreviewKeyboard keyboard = new PreviewKeyboard(host, input, () -> {});
        host.setContentView(keyboard); layout(keyboard);
        assertTrue("Initial keyboard focus request", keyboard.focusLastKey());
        assertEquals("T", ((TextView) keyboard.findFocus()).getText().toString());
        TextView p = (TextView) PreviewPagesTest.findText(keyboard, "P");
        p.requestFocus(); p.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DPAD_RIGHT));
        assertSame(p, keyboard.findFocus());
        PreviewPagesTest.findText(keyboard, "Clear").performClick();
        PreviewPagesTest.findText(keyboard, "1").performClick();
        assertEquals("1", input.getText().toString());
        keyboard.focusLastKey(); assertSame(p, keyboard.findFocus());
        host.finish();
    }
    @Test public void navigationHasOneBoundaryAndLocksBothEnds() {
        Activity host = Robolectric.buildActivity(TopNavigationTest.Host.class).setup().visible().get();
        FrameLayout content = new FrameLayout(host);
        TopNavigation shell = new TopNavigation(host, content, index -> {}, () -> true);
        host.setContentView(shell); layout(shell);
        LinearLayout bar = (LinearLayout) shell.getChildAt(0);
        LinearLayout rail = (LinearLayout) bar.getChildAt(1);
        assertTrue(rail instanceof PreviewFocusRail);
        View home = rail.getChildAt(0), settings = rail.getChildAt(6), search = rail.getChildAt(5);
        home.requestFocus(); shell.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DPAD_LEFT));
        assertSame(home, shell.findFocus());
        settings.requestFocus(); shell.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DPAD_RIGHT));
        assertSame(settings, shell.findFocus());
        shell.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DPAD_LEFT));
        assertSame(search, shell.findFocus());
        assertNull(home.getBackground()); assertNull(settings.getBackground());
        assertEquals("semantic:topnav.home",home.getTag());assertEquals("semantic:topnav.settings",settings.getTag());
        host.finish();
    }
    @Test public void toolbarColumnsRightCannotEscapeAndDownEntersContent(){
        Activity host=Robolectric.buildActivity(TopNavigationTest.Host.class).setup().visible().get();
        LinearLayout page=new LinearLayout(host);page.setOrientation(LinearLayout.VERTICAL);PreviewToolbar toolbar=new PreviewToolbar(host);page.addView(toolbar);
        for(String label:new String[]{"Filters","Sort","Order","Unmatched","Grid view","Columns"}){TextView control=new TextView(host);control.setText(label);control.setFocusable(true);control.setFocusableInTouchMode(true);toolbar.addView(control,new LinearLayout.LayoutParams(130,50));}
        TextView header=new TextView(host);header.setText("Library header");header.setFocusable(true);header.setFocusableInTouchMode(true);page.addView(header,new LinearLayout.LayoutParams(-1,50));
        host.setContentView(page);layout(page);View first=toolbar.getChildAt(0),last=toolbar.getChildAt(5);
        first.requestFocus();page.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN,KeyEvent.KEYCODE_DPAD_LEFT));assertSame(first,page.findFocus());
        last.requestFocus();page.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN,KeyEvent.KEYCODE_DPAD_RIGHT));assertSame(last,page.findFocus());
        page.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN,KeyEvent.KEYCODE_DPAD_DOWN));assertSame(header,page.findFocus());host.finish();
    }
    @Test public void gridRightDoesNotWrapAndLastRowDownDoesNotEscape() {
        Activity host = Robolectric.buildActivity(TopNavigationTest.Host.class).setup().visible().get();
        PreviewFocusRecycler grid = new PreviewFocusRecycler(host);
        grid.setLayoutManager(new GridLayoutManager(host, 3));
        grid.setAdapter(new RecyclerView.Adapter<RecyclerView.ViewHolder>() {
            public int getItemCount() { return 5; }
            public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int type) {
                TextView card = new TextView(host); card.setFocusable(true); card.setFocusableInTouchMode(true);
                card.setLayoutParams(new RecyclerView.LayoutParams(-1, 100));
                return new RecyclerView.ViewHolder(card) {};
            }
            public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {}
        });
        host.setContentView(grid); layout(grid);
        View terminal = grid.findViewHolderForAdapterPosition(2).itemView;
        terminal.requestFocus(); grid.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DPAD_RIGHT));
        assertSame(terminal, grid.findFocus());
        View last = grid.findViewHolderForAdapterPosition(4).itemView;
        last.requestFocus(); grid.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DPAD_DOWN));
        assertSame(last, grid.findFocus());
        host.finish();
    }
}
