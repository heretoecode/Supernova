package com.archos.mediacenter.video.leanback;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import android.app.Activity;
import android.os.Looper;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.Shadows;
import org.robolectric.android.controller.ActivityController;
import org.robolectric.annotation.Config;
import org.robolectric.annotation.LooperMode;

/** 0.155 notes #14: Hide and Move beside a Home row, live moving, Undo on Back. */
@RunWith(RobolectricTestRunner.class)
@Config(application = android.app.Application.class, sdk = 28, qualifiers = "w960dp-h540dp-land-mdpi")
@LooperMode(LooperMode.Mode.PAUSED)
public class HomeRowControlsTest {
    private Activity activity;
    private RecyclerView list;
    private final List<PreviewPages.Cell> cells = new ArrayList<>();
    private int rerenders;
    private HomeRowMover mover;

    @Before
    public void setUp() {
        ActivityController<Activity> controller = Robolectric.buildActivity(Activity.class).setup();
        activity = controller.get();
        androidx.preference.PreferenceManager.getDefaultSharedPreferences(activity).edit().clear().commit();
        List<String> ids = new ArrayList<>();
        for (PreviewHomeRows.Row row : new PreviewHomeRows(activity).rows) {
            ids.add(row.id);
        }
        for (String id : ids.subList(0, 3)) {
            PreviewPages.Cell heading = new PreviewPages.Cell(0, id, null);
            heading.homeRowId = id;
            cells.add(heading);
            PreviewPages.Cell rail = new PreviewPages.Cell(2, id, null);
            rail.homeRowId = id;
            cells.add(rail);
        }
        list = new RecyclerView(activity);
        list.setLayoutManager(new LinearLayoutManager(activity));
        list.setItemAnimator(null);
        list.setAdapter(new Rows());
        FrameLayout root = new FrameLayout(activity);
        root.addView(list, new FrameLayout.LayoutParams(-1, -1));
        activity.setContentView(root);
        Shadows.shadowOf(Looper.getMainLooper()).idle();
        mover = new HomeRowMover(activity, new HomeRowMover.Host() {
            @Override
            public RecyclerView list() {
                return list;
            }

            @Override
            public List<PreviewPages.Cell> cells() {
                return cells;
            }

            @Override
            public RecyclerView.Adapter<?> adapter() {
                return list.getAdapter();
            }

            @Override
            public void rerender() {
                rerenders++;
            }
        });
    }

    @Test
    public void leftOnTheFirstCardShowsHideAndMoveWithHideFocused() {
        LinearLayout row = rowItem(1);
        RecyclerView rail = PreviewPages.railIn(row);
        assertNotNull(rail);
        mover.reveal(row, rail, cells.get(1).homeRowId);
        idle();
        HomeRowControls controls = row.findViewWithTag("home.row.controls");
        assertNotNull(controls);
        assertSame("controls sit before the cards", controls, row.getChildAt(0));
        assertSame("the rail is still found", rail, PreviewPages.railIn(row));
        assertTrue(controls.hide.isFocused());
        assertTrue(HomeRowControls.isWhite(controls.hide));
        assertFalse(HomeRowControls.isWhite(controls.move));
        assertEquals(controls.hide.getChildAt(0).getWidth(), controls.move.getChildAt(0).getWidth());

        press(controls.hide, KeyEvent.KEYCODE_DPAD_RIGHT);
        assertTrue(controls.move.isFocused());
        press(controls.move, KeyEvent.KEYCODE_DPAD_RIGHT);
        assertFalse("Right from Move returns to the cards", mover.showing());
        assertNull(row.findViewWithTag("home.row.controls"));
        assertEquals(ViewGroup.LayoutParams.MATCH_PARENT, rail.getLayoutParams().width);
    }

    @Test
    public void moveCarriesTheRowLiveAndSavesTheOrder() {
        String carried = cells.get(1).homeRowId;
        String second = cells.get(3).homeRowId;
        LinearLayout row = rowItem(1);
        mover.reveal(row, PreviewPages.railIn(row), carried);
        idle();
        HomeRowControls controls = row.findViewWithTag("home.row.controls");
        controls.move.requestFocus();
        controls.move.performClick();
        idle();
        assertTrue(mover.moving());
        assertEquals("Moving", HomeRowControls.labelOf(controls.move));
        assertTrue(HomeRowControls.isWhite(controls.move));
        TextView status = rowItem(0).findViewWithTag("home.row.moving");
        assertNotNull(status);
        assertEquals(HomeRowMover.statusText(1, 3), status.getText().toString());
        assertTrue("other rows are dimmed", mover.dimmed(rowItem(2)));
        assertFalse(mover.dimmed(row));

        press(controls.move, KeyEvent.KEYCODE_DPAD_DOWN);
        idle();
        assertEquals(second, cells.get(1).homeRowId);
        assertEquals(carried, cells.get(3).homeRowId);
        List<String> saved = new ArrayList<>();
        for (PreviewHomeRows.Row r : new PreviewHomeRows(activity).rows) {
            saved.add(r.id);
        }
        assertTrue(saved.indexOf(carried) > saved.indexOf(second));
        assertEquals(HomeRowMover.statusText(2, 3), status.getText().toString());

        press(controls.move, KeyEvent.KEYCODE_BACK);
        idle();
        assertFalse(mover.moving());
        assertTrue("controls stay after dropping", mover.showing());
        assertEquals("Move", HomeRowControls.labelOf(controls.move));
        assertNull(rowItem(0).findViewWithTag("home.row.moving"));
    }

    @Test
    public void hideRemovesTheRowAndBackBringsItBack() {
        String hidden = cells.get(1).homeRowId;
        LinearLayout row = rowItem(1);
        mover.reveal(row, PreviewPages.railIn(row), hidden);
        idle();
        HomeRowControls controls = row.findViewWithTag("home.row.controls");
        controls.hide.performClick();
        assertEquals(1, rerenders);
        assertFalse(visible(hidden));
        assertTrue(mover.undoOnBack());
        assertTrue(visible(hidden));
        assertEquals(2, rerenders);
        assertFalse("only once", mover.undoOnBack());
    }

    private boolean visible(String id) {
        for (PreviewHomeRows.Row r : new PreviewHomeRows(activity).rows) {
            if (r.id.equals(id)) {
                return r.visible;
            }
        }
        return false;
    }

    private LinearLayout rowItem(int position) {
        RecyclerView.ViewHolder holder = list.findViewHolderForAdapterPosition(position);
        return (LinearLayout) holder.itemView;
    }

    private static void press(View view, int code) {
        view.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, code));
        view.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_UP, code));
    }

    private static void idle() {
        Shadows.shadowOf(Looper.getMainLooper()).idle();
    }

    private final class Rows extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int type) {
            LinearLayout item = new LinearLayout(parent.getContext());
            item.setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
            return new RecyclerView.ViewHolder(item) {
            };
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            LinearLayout item = (LinearLayout) holder.itemView;
            item.removeAllViews();
            if (cells.get(position).type == 0) {
                TextView title = new TextView(item.getContext());
                title.setText(cells.get(position).title);
                item.addView(title);
                return;
            }
            RecyclerView rail = new RecyclerView(item.getContext());
            rail.setLayoutManager(new LinearLayoutManager(item.getContext(), RecyclerView.HORIZONTAL, false));
            rail.setPadding(48, 10, 48, 10);
            item.addView(rail, new LinearLayout.LayoutParams(-1, 125));
        }

        @Override
        public int getItemCount() {
            return cells.size();
        }
    }
}
