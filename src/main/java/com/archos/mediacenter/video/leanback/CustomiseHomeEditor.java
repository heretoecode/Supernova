package com.archos.mediacenter.video.leanback;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import com.archos.mediacenter.video.leanback.PreviewHomeRows.Row;
import com.archos.mediacenter.video.leanback.PreviewLibraryLoader.Entry;
import com.archos.mediacenter.video.leanback.PreviewLibraryLoader.Snapshot;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/** Two-column Home editor. Presentation owns drafts/previews; the existing model owns durable rows. */
final class CustomiseHomeEditor {
    private final Context context;
    private final PreviewHomeRows model;
    private final Runnable changed;
    private final Dialog dialog;
    private final LinearLayout screen, list, editor;
    private final List<View> bodies = new ArrayList<>(), eyes = new ArrayList<>();
    private final List<View> fields = new ArrayList<>();
    private Row selected, draft, moving;
    private List<Row> moveOriginal;
    private TextView hint;
    private boolean dismissed;
    private Runnable releaseBackdrop=()->{};

    CustomiseHomeEditor(Context context, PreviewHomeRows model, Runnable changed) {
        this.context = context; this.model = model; this.changed = changed;
        dialog = PreviewDialog.create(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        screen = new MenuFoundation.WindowPanel(context);screen.setOrientation(LinearLayout.VERTICAL);screen.setClipChildren(false);screen.setClipToPadding(false); screen.setTag("home.editor");
        screen.setPadding(dp(18), dp(16), dp(18), dp(16));
        screen.setBackground(MenuFoundation.surface(context, false));
        TextView heading = MenuFoundation.text(context, "Customise Home", 23);

        hint = MenuFoundation.text(context, "OK to move a row · Right to edit · Back to return", 12);
        hint.setTextColor(MenuFoundation.SECONDARY); hint.setPadding(0, dp(8), 0, dp(12));
        LinearLayout columns = new LinearLayout(context); columns.setClipChildren(false);
        screen.addView(columns, new LinearLayout.LayoutParams(-1, 0, 1));
        LinearLayout left = column();left.setClipChildren(true);left.addView(heading);left.addView(hint);
        columns.addView(left,new LinearLayout.LayoutParams(0,-1,.37f));
        ScrollView rows = new ScrollView(context); rows.setFillViewport(true); rows.setClipToPadding(true);rows.setClipChildren(true);
        list = column(); list.setPadding(dp(3), dp(3), dp(3), dp(8)); rows.addView(list);
        left.addView(rows,new LinearLayout.LayoutParams(-1,0,1));
        ScrollView viewport = new ScrollView(context); viewport.setFillViewport(true); viewport.setClipToPadding(false);
        editor = column(); editor.setTag("home.editor.context"); editor.setPadding(dp(16), dp(10), dp(16), dp(10));
        editor.setBackground(MenuFoundation.surface(context, true)); viewport.addView(editor);
        LinearLayout.LayoutParams right = new LinearLayout.LayoutParams(0, -1, .63f); right.leftMargin = dp(18);
        columns.addView(viewport, right);
        selected = model.rows.get(0); renderRows(); renderEditor();
        dialog.setContentView(screen);
        dialog.setOnKeyListener((window, key, event) -> {
            if (key != KeyEvent.KEYCODE_BACK) return false;
            if (event.getAction() == KeyEvent.ACTION_UP) return true;
            View focused = dialog.getCurrentFocus();
            if (focused instanceof InlineMenuSelector && ((InlineMenuSelector) focused).cancelPreview()) return true;
            if (moving != null) { finishMove(false); return true; }
            requestExit(); return true;
        });
        dialog.setOnCancelListener(window -> requestExit());
        dialog.setOnDismissListener(window -> { if (!dismissed) { dismissed = true; releaseBackdrop.run(); changed.run(); } });
    }

    void show() {
        if(android.os.Build.VERSION.SDK_INT<31&&context instanceof android.app.Activity)releaseBackdrop=PreviewFrost.show((android.app.Activity)context);
        dialog.setCancelable(false); dialog.show();
        Window window = dialog.getWindow(); window.setBackgroundDrawableResource(android.R.color.transparent);
        window.setDimAmount(.58f);
        window.setLayout(Math.min(dp(900), TvLayout.safeWidth(context)), Math.min(dp(468), TvLayout.safeHeight(context)));
        if (android.os.Build.VERSION.SDK_INT >= 31) {
            window.addFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND);
            WindowManager.LayoutParams blur=window.getAttributes();blur.setBlurBehindRadius(dp(8));window.setAttributes(blur);
        }
        bodies.get(0).requestFocus();
    }

    private LinearLayout column() {
        LinearLayout layout = new LinearLayout(context); layout.setOrientation(LinearLayout.VERTICAL);
        layout.setClipChildren(false); layout.setClipToPadding(false); return layout;
    }
    private int dp(float value) { return MenuFoundation.dp(context, value); }
    private void committed() { model.save(); changed.run(); }
    private List<Row> visibleRows() { List<Row> rows = new ArrayList<>(model.rows); if (draft != null) rows.add(draft); return rows; }

    private void renderRows() {
        list.removeAllViews(); bodies.clear(); eyes.clear();
        for (Row row : visibleRows()) {
            LinearLayout line = new LinearLayout(context); line.setGravity(Gravity.CENTER_VERTICAL);
            line.setClipChildren(false); line.setClipToPadding(false);
            String name = row.name.isEmpty() ? "New Row" : shortLabel(row);
            TextView body = MenuFoundation.control(context, name, PreviewHomeRows.iconName(row), () -> startOrPlace(row));
            body.setTag(row.id); body.setContentDescription(name + ", OK to reorder");
            Drawable grip = new PreviewIcon("grip"); grip.setBounds(0, 0, dp(17), dp(17));
            body.setCompoundDrawables(body.getCompoundDrawables()[0], null, grip, null);
            body.setOnFocusChangeListener((view, focused) -> { if (focused) select(row); });
            line.addView(body, new LinearLayout.LayoutParams(0, -2, 1)); bodies.add(body);
            TextView eye = MenuFoundation.control(context, "", row.visible ? "eye" : "eye-off", () -> {
                row.visible = !row.visible; if (row != draft) committed();
                View focused = dialog.getCurrentFocus(); Object anchor = focused == null ? null : focused.getTag();
                renderRows(); restore(anchor);
            });
            eye.setTag(row.id + ":visibility"); eye.setContentDescription((row.visible ? "Hide " : "Show ") + name);
            eye.setOnFocusChangeListener((view, focused) -> { if (focused) select(row); });
            LinearLayout.LayoutParams eyeSize = new LinearLayout.LayoutParams(dp(44), -2); eyeSize.leftMargin = dp(10);
            line.addView(eye, eyeSize); eyes.add(eye);
            int index = bodies.size() - 1;
            body.setOnKeyListener((view, key, event) -> rowKey(row, index, false, key, event));
            eye.setOnKeyListener((view, key, event) -> rowKey(row, index, true, key, event));
            LinearLayout.LayoutParams size = new LinearLayout.LayoutParams(-1, -2); size.bottomMargin = dp(8);
            list.addView(line, size);
        }
        TextView create = MenuFoundation.control(context, "Create Row", "plus", () -> {
            if (draft != null) { selected = draft; renderEditor(); firstField(); return; }
            draft = new Row("custom:" + UUID.randomUUID(), ""); draft.dynamic = true;
            selected = draft; renderRows(); renderEditor(); firstField();
        });
        create.setTag("home.editor.create");
        create.setOnKeyListener((view, key, event) -> {
            if (event.getAction() != KeyEvent.ACTION_DOWN) return false;
            if (key == KeyEvent.KEYCODE_DPAD_UP) { if (!bodies.isEmpty()) bodies.get(bodies.size()-1).requestFocus(); return true; }
            if (key == KeyEvent.KEYCODE_DPAD_DOWN || key == KeyEvent.KEYCODE_DPAD_LEFT) return true;
            if (key == KeyEvent.KEYCODE_DPAD_RIGHT) { firstField(); return true; }
            return false;
        });
        list.addView(create);
    }
    private String shortLabel(Row row) {
        if (row.id.equals("trending")) return "Trending on Trakt";
        if (row.id.equals("popular")) return "Popular on Trakt";
        return row.name;
    }
    private void select(Row row) { if (selected != row) { selected = row; renderEditor(); } }
    private boolean rowKey(Row row, int index, boolean eye, int key, KeyEvent event) {
        if (event.getAction() != KeyEvent.ACTION_DOWN) return false;
        if (moving != null) {
            if (key == KeyEvent.KEYCODE_DPAD_UP || key == KeyEvent.KEYCODE_DPAD_DOWN) {
                int current = model.rows.indexOf(moving), next = current + (key == KeyEvent.KEYCODE_DPAD_DOWN ? 1 : -1);
                if (next >= 0 && next < model.rows.size()) {
                    Collections.swap(model.rows, current, next); renderRows(); restore(moving.id);
                    View neighbour = bodies.get(current);
                    if (motion()) { neighbour.setTranslationY(key == KeyEvent.KEYCODE_DPAD_DOWN ? dp(44) : -dp(44)); neighbour.animate().translationY(0).setDuration(150).start(); }
                }
                return true;
            }
            if (key == KeyEvent.KEYCODE_DPAD_LEFT || key == KeyEvent.KEYCODE_DPAD_RIGHT) return true;
        }
        if (key == KeyEvent.KEYCODE_DPAD_LEFT) { if (eye) bodies.get(index).requestFocus(); return true; }
        if (key == KeyEvent.KEYCODE_DPAD_RIGHT) { if (eye) firstField(); else eyes.get(index).requestFocus(); return true; }
        if (key == KeyEvent.KEYCODE_DPAD_UP || key == KeyEvent.KEYCODE_DPAD_DOWN) {
            int next = index + (key == KeyEvent.KEYCODE_DPAD_DOWN ? 1 : -1);
            List<View> targets = eye ? eyes : bodies;
            if (next >= 0 && next < targets.size()) targets.get(next).requestFocus();
            else if (next == targets.size()) list.findViewWithTag("home.editor.create").requestFocus();
            return true;
        }
        return false;
    }
    private boolean motion() { return android.os.Build.VERSION.SDK_INT < 26 || android.animation.ValueAnimator.areAnimatorsEnabled(); }
    private void startOrPlace(Row row) {
        if (row == draft) { firstField(); return; }
        if (moving != null) { finishMove(true); return; }
        moving = row; moveOriginal = new ArrayList<>(model.rows);
        hint.setText("Up/Down to move · OK to place · Back to cancel");
    }
    private void finishMove(boolean commit) {
        String anchor = moving.id;
        if (!commit) { model.rows.clear(); model.rows.addAll(moveOriginal); }
        moving = null; moveOriginal = null;
        if (commit) committed(); renderRows(); restore(anchor);
        hint.setText("OK to move a row · Right to edit · Back to return");
    }
    private void restore(Object anchor) {
        if(anchor==null)return;View view=screen.findViewWithTag(anchor);if(view==null)return;view.requestFocus();
        // A rebuilt row has no bounds yet. Recheck after layout if focus fell back
        // to a container; never take focus from a later deliberate control choice.
        if(!androidx.core.view.ViewCompat.isLaidOut(view))view.addOnLayoutChangeListener(new View.OnLayoutChangeListener(){
            @Override public void onLayoutChange(View target,int left,int top,int right,int bottom,int oldLeft,int oldTop,int oldRight,int oldBottom){
                if(right<=left||bottom<=top)return;target.removeOnLayoutChangeListener(this);
                View focused=dialog.getCurrentFocus();if(target.isAttachedToWindow()&&screen.findViewWithTag(anchor)==target&&(focused==null||focused==target||focused.getTag()==null))target.requestFocus();
            }
        });
    }
    private void firstField() { View name=editor.findViewWithTag("home.editor.name");if(name!=null)name.requestFocus();else if(!fields.isEmpty())fields.get(0).requestFocus(); }

    private TextView heading(String title) {
        TextView text = MenuFoundation.text(context, title, 11); text.setTextColor(MenuFoundation.SECONDARY);
        LinearLayout.LayoutParams size = new LinearLayout.LayoutParams(-1, -2); size.topMargin = dp(6); size.bottomMargin = dp(4);
        editor.addView(text, size); return text;
    }
    private <T extends View> T field(T view, String tag) { view.setTag(tag); fields.add(view); return view; }
    private void renderEditor() {
        editor.removeAllViews(); fields.clear(); final Row row = selected;
        TextView title = MenuFoundation.text(context, row.name.isEmpty() ? "New Row" : shortLabel(row), 20);
        MenuFoundation.icon(title, PreviewHomeRows.iconName(row));
        if(!row.custom())editor.addView(title);
        else {
            LinearLayout titleLine=new LinearLayout(context);titleLine.setGravity(Gravity.CENTER_VERTICAL);titleLine.setClipChildren(false);
            titleLine.addView(title,new LinearLayout.LayoutParams(0,-2,1));
            TextView remove=field(MenuFoundation.control(context,"","delete",()->confirm("Delete Row?","Titles remain in your library. Only this Home row is removed.","Cancel","Delete Row",()->{
                if(row==draft)draft=null;else{model.rows.remove(row);committed();}
                selected=model.rows.get(0);renderRows();renderEditor();bodies.get(0).requestFocus();
            })),"home.editor.delete");remove.setContentDescription("Delete Row");
            titleLine.addView(remove,new LinearLayout.LayoutParams(dp(44),-2));editor.addView(titleLine);
        }
        if (!row.custom()) {
            TextView description = MenuFoundation.text(context, builtInDescription(row.id), 14);
            description.setTextColor(MenuFoundation.SECONDARY); description.setPadding(0, dp(14), 0, dp(12)); editor.addView(description);
            TextView automatic = MenuFoundation.text(context, "Display Settings\nAutomatic", 14);
            automatic.setAlpha(.4f); automatic.setPadding(0, dp(12), 0, dp(12)); editor.addView(automatic);
            TextView locked = MenuFoundation.text(context, "Built-in Supernova row\nSettings are managed by Supernova. Reorder or show/hide this row on the left.", 15);
            locked.setBackground(MenuFoundation.surface(context, false)); locked.setPadding(dp(16), dp(14), dp(16), dp(14));
            LinearLayout.LayoutParams size = new LinearLayout.LayoutParams(-1, -2); size.topMargin = dp(28); editor.addView(locked, size);
            editor.setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS); return;
        }
        editor.setDescendantFocusability(ViewGroup.FOCUS_AFTER_DESCENDANTS);
        heading("Row Name / Icon");
        LinearLayout names = new LinearLayout(context); names.setClipChildren(false);
        TextView rename = field(MenuFoundation.control(context, row.name.isEmpty() ? "Enter a name for your row…" : row.name, "rename", () -> {
            PreviewTextInput.showFoundation(context, "Row Name", row.name, 40, value -> {
                row.name = value;
                if (row == draft) { model.rows.add(row); draft = null; }
                committed(); renderRows(); renderEditor(); restore("home.editor.name");
            });
        }), "home.editor.name");
        if (row.name.isEmpty()) rename.setTextColor(MenuFoundation.SECONDARY);
        names.addView(rename, new LinearLayout.LayoutParams(0, -2, 1));
        String[] icons = new String[PreviewGenres.NAMES.length + 1]; icons[0] = "Automatic";
        System.arraycopy(PreviewGenres.NAMES, 0, icons, 1, PreviewGenres.NAMES.length);
        InlineMenuSelector icon = field(new InlineMenuSelector(context, icons, Math.max(0, Arrays.asList(icons).indexOf(row.icon)), PreviewHomeRows.iconName(row), value -> {
            row.icon = value == 0 ? "" : icons[value]; saveRow(row); renderRows(); renderEditor(); restore("home.editor.icon");
        }), "home.editor.icon");
        LinearLayout.LayoutParams iconSize = new LinearLayout.LayoutParams(dp(128), -2); iconSize.leftMargin = dp(10); names.addView(icon, iconSize); editor.addView(names);
        heading("Content Type");
        LinearLayout types = new LinearLayout(context); types.setClipChildren(false);
        String[] labels = {"Movies", "TV Shows", "Both"};
        for (int i = 0; i < labels.length; i++) {
            final int type = i;
            TextView button = field(MenuFoundation.control(context, labels[i], null, () -> {
                row.movies = type != 1; row.tv = type != 0; saveRow(row); renderEditor(); restore("home.editor.type:" + type);
            }), "home.editor.type:" + i);
            boolean active = i == 0 ? row.movies && !row.tv : i == 1 ? row.tv && !row.movies : row.movies && row.tv;
            button.setSelected(active); button.setContentDescription(labels[i] + (active ? ", selected" : ""));
            if (active) button.setTextColor(PreviewAccent.color(context));
            LinearLayout.LayoutParams size = new LinearLayout.LayoutParams(0, -2, 1); if (i > 0) size.leftMargin = dp(8); types.addView(button, size);
        }
        editor.addView(types);
        heading("Genres");
        Set<String> selectedGenres = PreviewGenres.parse(row.genreRule);
        String summary = selectedGenres.isEmpty() ? "All Genres" : android.text.TextUtils.join(", ", selectedGenres) + " · " + selectedGenres.size() + " selected";
        TextView genres = field(MenuFoundation.control(context, summary, selectedGenres.isEmpty() ? "genres" : selectedGenres.iterator().next(), () -> {
            HomeGenrePicker.show(context, row, namesSelected -> {
                row.genreRule = android.text.TextUtils.join("|", namesSelected); saveRow(row);
                // Do not silently re-enable automatic additions when the user froze this row.
                renderRows(); renderEditor(); restore("home.editor.genres");
            });
        }), "home.editor.genres"); HomeGenrePicker.bindSummary(genres,selectedGenres); editor.addView(genres);
        if (selectedGenres.size() > 1) {
            LinearLayout modes = new LinearLayout(context); modes.setClipChildren(false);
            for (int i = 0; i < 2; i++) {
                final boolean all = i == 1;
                TextView mode = field(MenuFoundation.control(context, all ? "Match All" : "Match Any", null, () -> {
                    row.matchAll = all; saveRow(row); renderEditor(); restore("home.editor.match:" + all);
                }), "home.editor.match:" + all);
                mode.setSelected(row.matchAll == all); if (row.matchAll == all) mode.setTextColor(PreviewAccent.color(context));
                LinearLayout.LayoutParams size = new LinearLayout.LayoutParams(0, -2, 1); size.topMargin = dp(6); if (i > 0) size.leftMargin = dp(8); modes.addView(mode, size);
            }
            editor.addView(modes);
        }
        heading("Display Settings");
        LinearLayout display = new LinearLayout(context); display.setClipChildren(false);
        String[] methods = {"Date Added", "Title", "Year"}, keys = {"added", "title", "year"};
        InlineMenuSelector sort = field(new InlineMenuSelector(context, methods, Math.max(0, Arrays.asList(keys).indexOf(row.sort)), "sort", value -> { row.sort = keys[value]; saveRow(row); }), "home.editor.sort");
        display.addView(sort, new LinearLayout.LayoutParams(dp(158), -2));
        TextView direction = field(MenuFoundation.control(context, row.descending ? "Descending" : "Ascending", "order", () -> {
            row.descending = !row.descending; saveRow(row); renderEditor(); restore("home.editor.direction");
        }), "home.editor.direction"); direction.setTextSize(14 * TvLayout.scale(context));
        Drawable arrow = new PreviewIcon(row.descending ? "arrow-down" : "arrow-up"); arrow.setBounds(0, 0, dp(14), dp(14));
        direction.setCompoundDrawables(direction.getCompoundDrawables()[0], null, arrow, null);
        LinearLayout.LayoutParams dirSize = new LinearLayout.LayoutParams(dp(164), -2); dirSize.leftMargin = dp(8); display.addView(direction, dirSize);
        String[] limits = {"5", "10", "15", "20", "25", "30"};
        InlineMenuSelector maximum = field(new InlineMenuSelector(context, limits, Math.max(0, Arrays.asList(limits).indexOf(String.valueOf(row.maximum))), "list", value -> { row.maximum = PreviewHomeRows.MAXIMUM_PRESETS[value]; saveRow(row); }), "home.editor.maximum");
        maximum.setContentDescription("Maximum Items, " + row.maximum + ", OK to edit");
        LinearLayout.LayoutParams limitSize = new LinearLayout.LayoutParams(dp(88), -2); limitSize.leftMargin = dp(8); display.addView(maximum, limitSize);
        android.widget.HorizontalScrollView displayViewport=new android.widget.HorizontalScrollView(context);displayViewport.setClipToPadding(false);displayViewport.setHorizontalScrollBarEnabled(false);
        displayViewport.addView(display,new android.widget.HorizontalScrollView.LayoutParams(-2,-2));editor.addView(displayViewport,new LinearLayout.LayoutParams(-1,-2));
        TextView automatic = field(MenuFoundation.control(context, "Automatically include new matching media", row.dynamic ? "checkbox checked" : "checkbox", () -> {
            if (row == draft) row.dynamic = !row.dynamic;
            else model.setDynamic(row, !row.dynamic, PreviewLibraryLoader.memoryCache());
            if (row != draft) changed.run(); renderEditor(); restore("home.editor.automatic");
        }), "home.editor.automatic");
        automatic.setTextSize(14 * TvLayout.scale(context)); automatic.setContentDescription("Automatically include new matching media, " + (row.dynamic ? "On" : "Off"));
        LinearLayout.LayoutParams autoSize = new LinearLayout.LayoutParams(-1, -2); autoSize.topMargin = dp(6); editor.addView(automatic, autoSize);
        wireFields();
    }
    private void saveRow(Row row) { if (row != draft) committed(); }
    private void wireFields() {
        for (int i = 0; i < fields.size(); i++) {
            final int index = i; View field = fields.get(i);
            field.setOnKeyListener((view, key, event) -> {
                if (event.getAction() != KeyEvent.ACTION_DOWN) return false;
                if (view instanceof InlineMenuSelector && view.isActivated()) return false;
                if (key == KeyEvent.KEYCODE_DPAD_LEFT) {
                    if (index > 0 && sameLine(fields.get(index-1),view)) fields.get(index-1).requestFocus();
                    else { View eye = screen.findViewWithTag(selected.id + ":visibility"); if (eye != null) eye.requestFocus(); }
                    return true;
                }
                if (key == KeyEvent.KEYCODE_DPAD_RIGHT) {
                    if (index + 1 < fields.size() && sameLine(fields.get(index+1),view)) fields.get(index+1).requestFocus(); return true;
                }
                if (key == KeyEvent.KEYCODE_DPAD_UP || key == KeyEvent.KEYCODE_DPAD_DOWN) {
                    int next = index + (key == KeyEvent.KEYCODE_DPAD_DOWN ? 1 : -1);
                    while(next>=0&&next<fields.size()&&sameLine(fields.get(next),view))next+=key==KeyEvent.KEYCODE_DPAD_DOWN?1:-1;
                    if (next >= 0 && next < fields.size()) fields.get(next).requestFocus(); return true;
                }
                return false;
            });
        }
    }
    private boolean sameLine(View a,View b){return a.getParent()==b.getParent()&&a.getParent()!=editor;}
    private String builtInDescription(String id) {
        switch (id) {
            case "continue": return "Continue unfinished movies and the next indexed episode of a series.";
            case "watchnext": return "Titles you explicitly add to Watch Next.";
            case "recent": return "Recently added titles from your indexed library.";
            case "trending": return "Trending on Trakt, matched to titles in your indexed library.";
            case "popular": return "Popular on Trakt, matched to titles in your indexed library.";
            case "watched": return "Recently watched titles from your library.";
            case "similar": return "In-library recommendations based on a previously watched title.";
            default: return "Content selected by Supernova.";
        }
    }
    private void requestExit() {
        if (draft != null) {
            confirm("Discard unfinished row?", "This row has no name. Its unfinished changes will be lost.", "Continue Editing", "Discard Row", () -> { draft = null; dialog.dismiss(); });
        } else dialog.dismiss();
    }
    private void confirm(String title, String message, String safe, String destructive, Runnable accepted) {
        Dialog confirmation = PreviewDialog.create(context); confirmation.requestWindowFeature(Window.FEATURE_NO_TITLE);
        LinearLayout panel = new MenuFoundation.WindowPanel(context);panel.setOrientation(LinearLayout.VERTICAL); panel.setPadding(dp(20), dp(18), dp(20), dp(18)); panel.setBackground(MenuFoundation.surface(context, false));
        panel.addView(MenuFoundation.text(context, title, 22));
        TextView body = MenuFoundation.text(context, message, 15); body.setTextColor(MenuFoundation.SECONDARY); body.setPadding(0, dp(14), 0, dp(16)); panel.addView(body);
        TextView keep = MenuFoundation.control(context, safe, "continue", confirmation::dismiss); keep.setTag("home.confirm.safe"); panel.addView(keep);
        TextView discard = MenuFoundation.control(context, destructive, "discard", () -> { confirmation.dismiss(); accepted.run(); }); discard.setTag("home.confirm.discard");
        LinearLayout.LayoutParams size = new LinearLayout.LayoutParams(-1, -2); size.topMargin = dp(10); panel.addView(discard, size);
        confirmation.setContentView(panel); confirmation.show(); confirmation.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        confirmation.getWindow().setLayout(Math.min(dp(440), TvLayout.safeWidth(context)), -2); confirmation.getWindow().setDimAmount(.4f); keep.requestFocus();
    }
}
