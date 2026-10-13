package com.archos.mediacenter.video.leanback;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import com.archos.mediacenter.video.leanback.PreviewLibraryLoader.Entry;
import com.archos.mediacenter.video.leanback.PreviewLibraryLoader.Snapshot;
import com.archos.mediaprovider.ImportState;
import com.archos.mediaprovider.video.LoaderUtils;
import com.archos.mediaprovider.video.NetworkScannerReceiver;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Consumer;

/** Invoked-only connected genre window. Stable vocabulary/order/focus; counts are indexed identities. */
final class HomeGenrePicker {
    private HomeGenrePicker() { }
    static void show(Context context, PreviewHomeRows.Row row, Consumer<Set<String>> accept) {
        Dialog dialog = PreviewDialog.create(context, "choice"); dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        LinearLayout panel = new MenuFoundation.WindowPanel(context); panel.setOrientation(LinearLayout.VERTICAL);
        panel.setTag("home.genres.panel"); panel.setPadding(dp(context, 16), dp(context, 14), dp(context, 16), dp(context, 18));
        panel.setBackground(MenuFoundation.surface(context, false));
        panel.addView(MenuFoundation.text(context, "Genres", 21));
        TextView explanation = MenuFoundation.text(context, "Counts reflect your currently indexed library and can change. Zero-count genres can be selected before scanning.", 12);
        explanation.setTextColor(MenuFoundation.SECONDARY); explanation.setPadding(0, dp(context, 10), 0, dp(context, 12)); panel.addView(explanation);
        LinearLayout scanning = new LinearLayout(context); scanning.setGravity(Gravity.CENTER_VERTICAL);
        ImageView spinner = new ImageView(context);
        Drawable animation = new com.archos.mediacenter.video.foundation.DoubleRingDrawable(); spinner.setImageDrawable(animation);
        scanning.addView(spinner, new LinearLayout.LayoutParams(dp(context, 20), dp(context, 20)));
        TextView status = MenuFoundation.text(context, "Scanning library…", 12); status.setPadding(dp(context, 8), 0, 0, 0); scanning.addView(status); panel.addView(scanning);
        ScrollView viewport = new ScrollView(context); viewport.setClipToPadding(false);
        LinearLayout options = new LinearLayout(context); options.setOrientation(LinearLayout.VERTICAL);
        options.setClipChildren(false); options.setPadding(dp(context, 3), dp(context, 3), dp(context, 3), dp(context, 3)); viewport.addView(options);
        panel.addView(viewport, new LinearLayout.LayoutParams(-1, 0, 1));
        Set<String> selected = new LinkedHashSet<>(PreviewGenres.parse(row.genreRule));
        TreeSet<String> vocabulary = new TreeSet<>(String.CASE_INSENSITIVE_ORDER); vocabulary.addAll(Arrays.asList(PreviewGenres.NAMES)); vocabulary.addAll(selected);
        Snapshot initial = PreviewLibraryLoader.memoryCache();
        if (initial != null) { for (Entry entry : initial.movies) vocabulary.addAll(PreviewGenres.parse(entry.genres)); for (Entry entry : initial.shows) vocabulary.addAll(PreviewGenres.parse(entry.genres)); }
        List<String> names = new ArrayList<>(); names.add("All Genres"); names.addAll(vocabulary);
        List<LinearLayout> targets = new ArrayList<>(); List<ImageView> checks = new ArrayList<>(); List<RollingCount> counts = new ArrayList<>();
        Runnable updateChecks = () -> {
            for (int i = 0; i < targets.size(); i++) {
                boolean checked = i == 0 ? selected.isEmpty() : contains(selected, names.get(i));
                Drawable glyph = new PreviewIcon(checked ? "checkbox checked" : "checkbox");
                checks.get(i).setImageDrawable(glyph);
                targets.get(i).setSelected(checked);
                targets.get(i).setContentDescription(names.get(i) + (checked ? ", selected" : ", not selected") + ", " + counts.get(i).description());
            }
        };
        for (int i = 0; i < names.size(); i++) {
            final int index = i; final String name = names.get(i);
            LinearLayout target = new LinearLayout(context); target.setGravity(Gravity.CENTER_VERTICAL);
            target.setTag("home.genre:" + name); target.setId(View.generateViewId()); target.setFocusable(true); target.setFocusableInTouchMode(true); target.setClickable(true);
            target.setDescendantFocusability(android.view.ViewGroup.FOCUS_BLOCK_DESCENDANTS);
            target.setPadding(dp(context, 10), dp(context, 8), dp(context, 10), dp(context, 8)); target.setMinimumHeight(dp(context, 44));
            target.setBackground(MenuFoundation.controlBackground(context)); targets.add(target);
            ImageView check = new ImageView(context); checks.add(check); target.addView(check, new LinearLayout.LayoutParams(dp(context, 18), dp(context, 18)));
            TextView label = MenuFoundation.text(context, name, 14); MenuFoundation.icon(label, i == 0 ? "genres" : name);
            LinearLayout.LayoutParams labelSize = new LinearLayout.LayoutParams(0, -2, 1); labelSize.leftMargin = dp(context, 10); target.addView(label, labelSize);
            RollingCount count = new RollingCount(context); counts.add(count); target.addView(count, new LinearLayout.LayoutParams(dp(context, 46), dp(context, 24)));
            target.setOnClickListener(view -> {
                if (index == 0) selected.clear();
                else if (contains(selected, name)) selected.removeIf(value -> value.equalsIgnoreCase(name)); else selected.add(name);
                updateChecks.run(); accept.accept(new LinkedHashSet<>(selected));
            });
            target.setOnKeyListener((view, key, event) -> {
                if (event.getAction() != KeyEvent.ACTION_DOWN) return false;
                if (key == KeyEvent.KEYCODE_DPAD_LEFT || key == KeyEvent.KEYCODE_DPAD_RIGHT) return true;
                if (key == KeyEvent.KEYCODE_DPAD_UP || key == KeyEvent.KEYCODE_DPAD_DOWN) {
                    int next = index + (key == KeyEvent.KEYCODE_DPAD_DOWN ? 1 : -1);
                    if (next >= 0 && next < targets.size()) targets.get(next).requestFocus(); return true;
                }
                return false;
            });
            options.addView(target, new LinearLayout.LayoutParams(-1, -2));
            View divider = new View(context); divider.setBackgroundColor(MenuFoundation.DIVIDER);
            options.addView(divider, new LinearLayout.LayoutParams(-1, Math.max(1, dp(context, 1))));
        }
        Handler handler = new Handler(Looper.getMainLooper());
        Runnable refresh = new Runnable() {
            @Override public void run() {
                if (!dialog.isShowing()) return;
                Snapshot snapshot = PreviewLibraryLoader.memoryCache();
                for (int i = 0; i < names.size(); i++) counts.get(i).setCount(snapshot == null ? -1 : count(snapshot, row, i == 0 ? null : names.get(i)));
                boolean active = NetworkScannerReceiver.isScannerWorking() || ImportState.VIDEO.isInitialImport() || ImportState.VIDEO.isRegularImport() || LoaderUtils.getScrapeInProgress();
                scanning.setVisibility(active ? View.VISIBLE : View.GONE);
                android.graphics.drawable.Animatable rotating = (android.graphics.drawable.Animatable) animation;
                if (active && (android.os.Build.VERSION.SDK_INT < 26 || android.animation.ValueAnimator.areAnimatorsEnabled())) rotating.start(); else rotating.stop();
                updateChecks.run(); handler.postDelayed(this, 1000);
            }
        };
        dialog.setContentView(panel); dialog.setOnDismissListener(window -> { handler.removeCallbacks(refresh); ((android.graphics.drawable.Animatable) animation).stop(); for (RollingCount count : counts) count.cancel(); });
        dialog.show(); dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent); dialog.getWindow().setDimAmount(.3f);
        dialog.getWindow().setLayout(Math.min(dp(context, 380), TvLayout.safeWidth(context)), Math.min(dp(context, 434), TvLayout.safeHeight(context)));
        // Keep the pop-out next to the contextual editor within safe bounds, never a permanent column.
        WindowManager.LayoutParams position = dialog.getWindow().getAttributes(); position.gravity = Gravity.RIGHT | Gravity.CENTER_VERTICAL; position.x = dp(context, 28); dialog.getWindow().setAttributes(position);
        targets.get(0).requestFocus(); refresh.run();
    }
    static void bindSummary(TextView view,Set<String> selected){
        if(selected.isEmpty())return;
        List<String> genres=new ArrayList<>(selected);
        Drawable icons=new Drawable(){
            @Override public void draw(Canvas canvas){
                int size=dp(view.getContext(),19),gap=dp(view.getContext(),5);
                for(int i=0;i<Math.min(3,genres.size());i++){Drawable icon=PreviewGenres.known(genres.get(i))?new PreviewGenres.Icon(genres.get(i)):new PreviewIcon("genres");icon.setBounds(getBounds().left+i*(size+gap),getBounds().top,getBounds().left+i*(size+gap)+size,getBounds().top+size);icon.draw(canvas);}
            }
            @Override public void setAlpha(int value){} @Override public void setColorFilter(android.graphics.ColorFilter filter){} @Override public int getOpacity(){return android.graphics.PixelFormat.TRANSLUCENT;}
        };
        int size=dp(view.getContext(),19),total=Math.min(3,genres.size());icons.setBounds(0,0,total*size+(total-1)*dp(view.getContext(),5),size);
        Paint text=new Paint(Paint.ANTI_ALIAS_FLAG);text.setColor(MenuFoundation.SECONDARY);text.setTextSize(12*TvLayout.scale(view.getContext())*view.getResources().getDisplayMetrics().scaledDensity);
        String label=selected.size()+" selected";
        Drawable count=new Drawable(){
            @Override public void draw(Canvas canvas){canvas.drawText(label,getBounds().left+dp(view.getContext(),8),getBounds().centerY()-(text.ascent()+text.descent())/2,text);}
            @Override public void setAlpha(int value){text.setAlpha(value);}@Override public void setColorFilter(android.graphics.ColorFilter filter){text.setColorFilter(filter);}@Override public int getOpacity(){return android.graphics.PixelFormat.TRANSLUCENT;}
        };
        count.setBounds(0,0,(int)Math.ceil(text.measureText(label))+dp(view.getContext(),8),size);
        view.setText(android.text.TextUtils.join(", ",genres));view.setContentDescription("Genres, "+android.text.TextUtils.join(", ",genres)+", "+label);
        view.setCompoundDrawables(icons,null,count,null);
    }
    private static boolean contains(Set<String> names, String value) { return names.stream().anyMatch(name -> name.equalsIgnoreCase(value)); }
    static int count(Snapshot snapshot, PreviewHomeRows.Row row, String genre) {
        Set<String> identities = new LinkedHashSet<>();
        if (row.movies) for (Entry entry : snapshot.movies) if (genre == null || PreviewGenres.matches(entry.genres, CollectionsHelper.one(genre))) identities.add(entry.key());
        if (row.tv) for (Entry entry : snapshot.shows) if (genre == null || PreviewGenres.matches(entry.genres, CollectionsHelper.one(genre))) identities.add(entry.key());
        return identities.size();
    }
    private static int dp(Context context, float value) { return MenuFoundation.dp(context, value); }
    private static final class CollectionsHelper { static Set<String> one(String name) { return java.util.Collections.singleton(name); } }

    /** Fixed-width tabular digits; updates coalesce and only changed digits roll. No alpha flashing. */
    private static final class RollingCount extends View {
        private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        private String value = "—", before = "—";
        private float progress = 1;
        private android.animation.ValueAnimator animator;
        RollingCount(Context context) {
            super(context); paint.setColor(MenuFoundation.SECONDARY);
            paint.setTextSize(14 * TvLayout.scale(context) * context.getResources().getDisplayMetrics().scaledDensity);
            paint.setTypeface(android.graphics.Typeface.MONOSPACE); setImportantForAccessibility(IMPORTANT_FOR_ACCESSIBILITY_NO);
        }
        String description() { return value.equals("—") ? "not yet indexed" : value + " indexed titles"; }
        void cancel() { if (animator != null) animator.cancel(); animator = null; }
        void setCount(int count) {
            String next = count < 0 ? "—" : String.valueOf(count); if (value.equals(next)) return;
            cancel(); before = value; value = next; progress = 1;
            if (!before.equals("—") && !value.equals("—") && (android.os.Build.VERSION.SDK_INT < 26 || android.animation.ValueAnimator.areAnimatorsEnabled())) {
                animator = android.animation.ValueAnimator.ofFloat(0, 1); animator.setDuration(180);
                animator.addUpdateListener(frame -> { progress = (float) frame.getAnimatedValue(); invalidate(); }); animator.start();
            }
            invalidate();
        }
        @Override protected void onDraw(Canvas canvas) {
            float width = paint.measureText("0"), baseline = (getHeight() - paint.ascent() - paint.descent()) / 2;
            float height = paint.getFontSpacing(); int save = canvas.save(); canvas.clipRect(0, 0, getWidth(), getHeight());
            for (int i = 0; i < value.length(); i++) {
                String digit = value.substring(value.length()-1-i, value.length()-i);
                String old = i < before.length() ? before.substring(before.length()-1-i, before.length()-i) : "";
                float x = getWidth() - width * (i + 1);
                if (progress < 1 && !old.equals(digit)) { canvas.drawText(old, x, baseline - progress * height, paint); canvas.drawText(digit, x, baseline + (1-progress)*height, paint); }
                else canvas.drawText(digit, x, baseline, paint);
            }
            canvas.restoreToCount(save);
        }
    }
}
