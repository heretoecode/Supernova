package com.archos.mediacenter.video.leanback;

import android.content.Context;
import android.os.SystemClock;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BooleanSupplier;

/**
 * Hide and Move for Home rows, beside the row instead of a pop-up (owner, 4 Nov 2026, 0.155 notes #14).
 *
 * <p>Left on a row's first card reveals {@link HomeRowControls}: the row slides right and Hide is focused. OK on
 * Move lifts the row and dims the others; each Up/Down glides it one place (the row it passes slides the other
 * way) and saves the new order at once; OK or Back drops it. Hide removes the row with a notice; Back within a
 * few seconds brings it back (rows also come back from Customise Home).
 */
final class HomeRowMover implements HomeRowControls.Actions {
    /** The parts of the Home page the mover needs. */
    interface Host {
        RecyclerView list();

        List<PreviewPages.Cell> cells();

        RecyclerView.Adapter<?> adapter();

        /** Rebuilds the page from the saved rows (after Hide or Undo). */
        void rerender();
    }

    static final long UNDO_MS = 6000;
    static final long GLIDE_MS = 200;
    static final float DIMMED = .4f;
    private final Context context;
    private final Host host;
    private HomeRowControls controls;
    private LinearLayout item;
    private RecyclerView rail;
    private String rowId = "";
    private int railPaddingLeft;
    private RecyclerView.ItemAnimator previousAnimator;
    private boolean attachWatcherInstalled;
    private String undoRowId = "";
    private String undoName = "";
    private long undoUntil;
    private final ViewTreeObserver.OnGlobalFocusChangeListener focusWatcher = (oldFocus, newFocus) -> {
        if (controls == null || controls.moving()) {
            return;
        }
        if (newFocus == null || !isInside(newFocus, controls)) {
            dismiss(false);
        }
    };

    private final FirstCardHint hint = new FirstCardHint();
    private View hinted;

    HomeRowMover(Context context, Host host) {
        this.context = context;
        this.host = host;
    }

    /**
     * Shows a faint ‹ in the left margin while a Home row's first card has focus, saying there is more that way
     * (mock-up {@code home-row-controls/00}); {@code active} says whether the page is Home.
     */
    void watchFirstCards(BooleanSupplier active) {
        host.list().getViewTreeObserver().addOnGlobalFocusChangeListener((oldFocus, newFocus) -> {
            View row = active.getAsBoolean() && controls == null ? firstCardRow(newFocus) : null;
            showHint(row);
        });
    }

    /** The Home row item whose first card is {@code focus}, or null. */
    View firstCardRow(View focus) {
        RecyclerView list = host.list();
        if (focus == null || !isInside(focus, list)) {
            return null;
        }
        View row = list.findContainingItemView(focus);
        int position = row == null ? -1 : list.getChildAdapterPosition(row);
        List<PreviewPages.Cell> cells = host.cells();
        if (position < 0 || position >= cells.size() || cells.get(position).homeRowId.isEmpty()) {
            return null;
        }
        RecyclerView rowRail = PreviewPages.railIn(row);
        View card = rowRail == null ? null : rowRail.findContainingItemView(focus);
        return card != null && rowRail.getChildAdapterPosition(card) == 0 ? row : null;
    }

    View hintedRow() {
        return hinted;
    }

    private void showHint(View row) {
        if (row == hinted) {
            return;
        }
        if (hinted != null) {
            hinted.getOverlay().remove(hint);
        }
        hinted = row;
        if (row == null) {
            return;
        }
        RecyclerView rowRail = PreviewPages.railIn(row);
        int margin = rowRail == null ? dp(28) : rowRail.getPaddingLeft();
        int size = dp(14);
        int left = Math.max(0, margin / 2 - size / 2);
        int top = row.getHeight() / 2 - size;
        hint.setBounds(left, top, left + size, top + size * 2);
        row.getOverlay().add(hint);
    }

    boolean showing() {
        return controls != null;
    }

    boolean moving() {
        return controls != null && controls.moving();
    }

    String rowId() {
        return rowId;
    }

    /** True when the given row view holds the controls. */
    boolean owns(View view) {
        return controls != null && view == item;
    }

    /** Shows the controls beside {@code rowRail}, which sits in {@code rowItem}; Hide takes focus. */
    void reveal(LinearLayout rowItem, RecyclerView rowRail, String id) {
        if (controls != null) {
            dismiss(false);
        }
        item = rowItem;
        rail = rowRail;
        rowId = id;
        controls = new HomeRowControls(context, this);
        railPaddingLeft = rail.getPaddingLeft();
        int gap = dp(16);
        LinearLayout.LayoutParams size =
            new LinearLayout.LayoutParams(dp(HomeRowControls.widthDp()), ViewGroup.LayoutParams.MATCH_PARENT);
        size.leftMargin = railPaddingLeft;
        size.gravity = Gravity.CENTER_VERTICAL;
        rail.setPadding(gap, rail.getPaddingTop(), rail.getPaddingRight(), rail.getPaddingBottom());
        rail.setLayoutParams(new LinearLayout.LayoutParams(0, rail.getLayoutParams().height, 1f));
        item.addView(controls, 0, size);
        rail.setTranslationX(-(dp(HomeRowControls.widthDp()) + gap));
        rail.animate().translationX(0).setDuration(GLIDE_MS).start();
        controls.setAlpha(0f);
        controls.animate().alpha(1f).setDuration(GLIDE_MS).start();
        showHint(null);
        controls.hide.requestFocus();
        host.list().getViewTreeObserver().addOnGlobalFocusChangeListener(focusWatcher);
    }

    /** Removes the controls and puts the row back as it was; {@code focusRow} moves focus to its first card. */
    void dismiss(boolean focusRow) {
        if (controls == null) {
            return;
        }
        if (controls.moving()) {
            endMoving();
        }
        host.list().getViewTreeObserver().removeOnGlobalFocusChangeListener(focusWatcher);
        HomeRowControls gone = controls;
        controls = null;
        gone.animate().cancel();
        item.removeView(gone);
        rail.animate().cancel();
        rail.setTranslationX(0);
        rail.setTranslationY(0);
        rail.setPadding(railPaddingLeft, rail.getPaddingTop(), rail.getPaddingRight(), rail.getPaddingBottom());
        rail.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
            rail.getLayoutParams().height));
        if (focusRow) {
            View first = rail.getLayoutManager() == null ? null : rail.getLayoutManager().findViewByPosition(0);
            if (first != null) {
                first.requestFocus();
            } else {
                rail.requestFocus();
            }
        }
        item = null;
        rail = null;
        rowId = "";
    }

    @Override
    public void hide() {
        PreviewHomeRows model = new PreviewHomeRows(context);
        PreviewHomeRows.Row row = find(model, rowId);
        if (row == null) {
            dismiss(true);
            return;
        }
        row.visible = false;
        model.save();
        undoRowId = row.id;
        undoName = row.name;
        undoUntil = SystemClock.elapsedRealtime() + UNDO_MS;
        dismiss(false);
        host.rerender();
        PreviewNotice.show(context, row.name + " hidden · press Back to undo", false, UNDO_MS);
    }

    /** Back shortly after Hide brings the row back; returns true when it did. */
    boolean undoOnBack() {
        if (undoRowId.isEmpty() || SystemClock.elapsedRealtime() > undoUntil) {
            undoRowId = "";
            return false;
        }
        PreviewHomeRows model = new PreviewHomeRows(context);
        PreviewHomeRows.Row row = find(model, undoRowId);
        undoRowId = "";
        if (row == null) {
            return false;
        }
        row.visible = true;
        model.save();
        host.rerender();
        PreviewNotice.show(context, undoName + " is back", false);
        return true;
    }

    @Override
    public void startMove() {
        if (controls == null) {
            return;
        }
        controls.setMoving(true);
        RecyclerView list = host.list();
        previousAnimator = list.getItemAnimator();
        DefaultItemAnimator glide = new DefaultItemAnimator();
        glide.setMoveDuration(GLIDE_MS);
        glide.setSupportsChangeAnimations(false);
        list.setItemAnimator(glide);
        installAttachWatcher();
        rail.animate().translationY(-dp(4)).setDuration(GLIDE_MS).start();
        refreshDimming();
        showStatus();
    }

    @Override
    public void step(int direction) {
        if (!moving()) {
            return;
        }
        List<String> order = displayedRows();
        int here = order.indexOf(rowId);
        int there = here + direction;
        if (here < 0 || there < 0 || there >= order.size()) {
            return;
        }
        String neighbour = order.get(there);
        PreviewHomeRows model = new PreviewHomeRows(context);
        PreviewHomeRows.Row carried = find(model, rowId);
        PreviewHomeRows.Row passed = find(model, neighbour);
        if (carried == null || passed == null) {
            return;
        }
        model.rows.remove(carried);
        int at = model.rows.indexOf(passed);
        model.rows.add(direction < 0 ? at : at + 1, carried);
        model.save();
        String earlier = direction < 0 ? neighbour : rowId;
        String later = direction < 0 ? rowId : neighbour;
        int before = item.getTop();
        swapBlocks(earlier, later);
        keepInPlace(before);
        showStatus();
    }

    @Override
    public void finishMove() {
        if (!moving()) {
            return;
        }
        endMoving();
        controls.move.requestFocus();
    }

    @Override
    public void backToRow() {
        dismiss(true);
    }

    private void endMoving() {
        controls.setMoving(false);
        rail.animate().translationY(0).setDuration(GLIDE_MS).start();
        RecyclerView list = host.list();
        RecyclerView.ItemAnimator restore = previousAnimator;
        list.postDelayed(() -> {
            if (!moving()) {
                list.setItemAnimator(restore);
            }
        }, GLIDE_MS + 50);
        refreshDimming();
        removeStatus();
    }

    /** The Home rows on screen, top to bottom, by id. */
    List<String> displayedRows() {
        List<String> order = new ArrayList<>();
        for (PreviewPages.Cell cell : host.cells()) {
            if (!cell.homeRowId.isEmpty() && !order.contains(cell.homeRowId)) {
                order.add(cell.homeRowId);
            }
        }
        return order;
    }

    /** Moves the later row's cells to just above the earlier row's, so the adapter animates both rows. */
    private void swapBlocks(String earlier, String later) {
        List<PreviewPages.Cell> cells = host.cells();
        int earlierStart = first(cells, earlier);
        int laterStart = first(cells, later);
        if (earlierStart < 0 || laterStart < 0) {
            return;
        }
        int length = 0;
        while (laterStart + length < cells.size() && later.equals(cells.get(laterStart + length).homeRowId)) {
            length++;
        }
        for (int k = 0; k < length; k++) {
            PreviewPages.Cell moved = cells.remove(laterStart + k);
            cells.add(earlierStart + k, moved);
            host.adapter().notifyItemMoved(laterStart + k, earlierStart + k);
        }
    }

    /** Scrolls so the carried row stays at the same height on screen while the others move past it. */
    private void keepInPlace(int before) {
        LinearLayout carried = item;
        RecyclerView list = host.list();
        carried.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int l, int t, int r, int b, int ol, int ot, int or, int ob) {
                carried.removeOnLayoutChangeListener(this);
                int shift = t - before;
                if (shift != 0) {
                    list.post(() -> list.smoothScrollBy(0, shift));
                }
            }
        });
    }

    private void installAttachWatcher() {
        if (attachWatcherInstalled) {
            return;
        }
        attachWatcherInstalled = true;
        host.list().addOnChildAttachStateChangeListener(new RecyclerView.OnChildAttachStateChangeListener() {
            @Override
            public void onChildViewAttachedToWindow(View view) {
                view.setAlpha(dimmed(view) ? DIMMED : 1f);
            }

            @Override
            public void onChildViewDetachedFromWindow(View view) {
                view.setAlpha(1f);
            }
        });
    }

    private void refreshDimming() {
        RecyclerView list = host.list();
        for (int i = 0; i < list.getChildCount(); i++) {
            View child = list.getChildAt(i);
            child.animate().alpha(dimmed(child) ? DIMMED : 1f).setDuration(GLIDE_MS).start();
        }
    }

    /** While moving, everything but the carried row's heading and cards is dimmed. */
    boolean dimmed(View child) {
        if (!moving()) {
            return false;
        }
        int position = host.list().getChildAdapterPosition(child);
        List<PreviewPages.Cell> cells = host.cells();
        if (position < 0 || position >= cells.size()) {
            return child != item;
        }
        return !rowId.equals(cells.get(position).homeRowId);
    }

    private void showStatus() {
        LinearLayout heading = headingView();
        if (heading == null) {
            return;
        }
        TextView status = heading.findViewWithTag("home.row.moving");
        if (status == null) {
            status = new TextView(context);
            status.setTag("home.row.moving");
            status.setTextSize(14);
            status.setTextColor(0xFFC9D3DE);
            status.setSingleLine(true);
            LinearLayout.LayoutParams size = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            size.leftMargin = dp(18);
            size.gravity = Gravity.CENTER_VERTICAL;
            heading.addView(status, size);
        }
        List<String> order = displayedRows();
        status.setText(statusText(order.indexOf(rowId) + 1, order.size()));
    }

    static String statusText(int position, int total) {
        return "Row " + position + " of " + total + " · ↑ ↓ to move · OK or Back when done";
    }

    private void removeStatus() {
        RecyclerView list = host.list();
        for (int i = 0; i < list.getChildCount(); i++) {
            View child = list.getChildAt(i);
            View status = child.findViewWithTag("home.row.moving");
            if (status != null && status.getParent() instanceof ViewGroup) {
                ((ViewGroup) status.getParent()).removeView(status);
            }
        }
    }

    private LinearLayout headingView() {
        int position = first(host.cells(), rowId);
        if (position < 0) {
            return null;
        }
        RecyclerView.ViewHolder holder = host.list().findViewHolderForAdapterPosition(position);
        if (holder == null || holder.itemView == item || !(holder.itemView instanceof LinearLayout)) {
            return null;
        }
        return (LinearLayout) holder.itemView;
    }

    private static int first(List<PreviewPages.Cell> cells, String id) {
        for (int i = 0; i < cells.size(); i++) {
            if (id.equals(cells.get(i).homeRowId)) {
                return i;
            }
        }
        return -1;
    }

    private static PreviewHomeRows.Row find(PreviewHomeRows model, String id) {
        for (PreviewHomeRows.Row row : model.rows) {
            if (row.id.equals(id)) {
                return row;
            }
        }
        return null;
    }

    private static boolean isInside(View view, View container) {
        View current = view;
        while (current != null) {
            if (current == container) {
                return true;
            }
            current = current.getParent() instanceof View ? (View) current.getParent() : null;
        }
        return false;
    }

    private int dp(int value) {
        return PreviewDialog.dp(context, value);
    }

    /** A thin, faint ‹ drawn in the row's overlay, so it never moves the cards or takes focus. */
    static final class FirstCardHint extends android.graphics.drawable.Drawable {
        private final android.graphics.Paint paint = new android.graphics.Paint(android.graphics.Paint.ANTI_ALIAS_FLAG);

        FirstCardHint() {
            paint.setStyle(android.graphics.Paint.Style.STROKE);
            paint.setStrokeCap(android.graphics.Paint.Cap.ROUND);
            paint.setStrokeJoin(android.graphics.Paint.Join.ROUND);
            paint.setColor(0x8CFFFFFF);
        }

        @Override
        public void draw(android.graphics.Canvas canvas) {
            android.graphics.Rect b = getBounds();
            paint.setStrokeWidth(Math.max(2f, b.width() * .16f));
            android.graphics.Path chevron = new android.graphics.Path();
            chevron.moveTo(b.right - b.width() * .25f, b.top + b.height() * .2f);
            chevron.lineTo(b.left + b.width() * .25f, b.exactCenterY());
            chevron.lineTo(b.right - b.width() * .25f, b.bottom - b.height() * .2f);
            canvas.drawPath(chevron, paint);
        }

        @Override
        public void setAlpha(int alpha) {
            paint.setAlpha(alpha);
        }

        @Override
        public void setColorFilter(android.graphics.ColorFilter filter) {
            paint.setColorFilter(filter);
        }

        @Override
        public int getOpacity() {
            return android.graphics.PixelFormat.TRANSLUCENT;
        }
    }
}
