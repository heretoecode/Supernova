package com.archos.mediacenter.video.leanback;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Build;
import android.text.TextUtils;
import android.view.*;
import android.widget.*;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleEventObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.preference.PreferenceManager;
import com.archos.mediacenter.video.diagnostics.Diagnostics;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.function.IntConsumer;

/** Content-sized Nova menus. The caller still owns every real action and selection. */
public final class PreviewDialog {
    private static final Map<Context, List<WeakReference<Dialog>>> WINDOWS =
        new WeakHashMap<>();
    private static final Map<Dialog, String> KINDS = new WeakHashMap<>();

    /** Shared child-dialog lifetime: Back restores the opener in the parent window. */
    public static Dialog create(Context context) {
        return create(context, "dialog");
    }

    public static Dialog create(Context context, String kind) {
        Context owner = owner(context);
        View opener = anchor(context);
        Lifecycle lifetime = owner instanceof LifecycleOwner
                                                    ? ((LifecycleOwner)owner).getLifecycle()
                                                    : null;
        LifecycleEventObserver[] cleanup = {null};
        String restorationToken = Diagnostics.focusEntry(opener, "dialog");
        WeakReference<View> previous = new WeakReference<>(opener);
        WeakReference<View> previousRoot =
            new WeakReference<>(opener == null ? null : opener.getRootView());
        Object semantic = opener == null ? null : opener.getTag();
        int id = opener == null ? View.NO_ID : opener.getId();
        Dialog dialog = new Dialog(context) {
            @Override
            protected void onStart() {
                super.onStart();
                List<WeakReference<Dialog>> windows =
                    WINDOWS.computeIfAbsent(owner, k -> new ArrayList<>());
                windows.removeIf(reference -> reference.get() == null || reference.get() == this);
                windows.add(new WeakReference<>(this));
                modalState(windows);
            }
            @Override
            public void dismiss() {
                if (lifetime != null && cleanup[0] != null) {
                    lifetime.removeObserver(cleanup[0]);
                    cleanup[0] = null;
                }
                boolean showing = isShowing();
                super.dismiss();
                List<WeakReference<Dialog>> windows = WINDOWS.get(owner);
                if (windows != null) {
                    windows.removeIf(reference -> reference.get() == null || reference.get() == this);
                    if (windows.isEmpty())
                        WINDOWS.remove(owner);
                }
                modalState(windows);
                if (!showing)
                    return;
                View root = previousRoot.get(), requested = previous.get(), target = requested;
                boolean fallback = false;
                if (root == null || !root.isAttachedToWindow())
                    return;
                Dialog parent = top(owner);
                if (parent != null && parent.getWindow() != null && parent.getWindow().getDecorView() != root)
                    return;
                if (target == null || !target.isAttachedToWindow() || !target.isShown() || !target.isFocusable()) {
                    fallback = true;
                    target = semantic == null ? null : root.findViewWithTag(semantic);
                    if (target == null && id != View.NO_ID)
                        target = root.findViewById(id);
                    if (target == null || !target.isShown() || !target.isFocusable()) {
                        target = null;
                        for (View candidate : root.getFocusables(View.FOCUS_FORWARD))
                            if (candidate.isShown() && candidate.isEnabled()) {
                                target = candidate;
                                break;
                            }
                    }
                }
                boolean restored = target != null && target.requestFocus();
                Diagnostics.focusRestored(restorationToken, requested, target,
                                                                                   fallback, restored);
            }
        };
        if (lifetime != null) {
            cleanup[0] = (source, event) -> {
                if (event == Lifecycle.Event.ON_DESTROY)
                    dialog.dismiss();
            };
            lifetime.addObserver(cleanup[0]);
        }
        KINDS.put(
            dialog,
            Arrays.asList("dialog", "choice", "reader", "review", "artwork_picker", "trailer").contains(kind)
                ? kind
                : "dialog");
        return dialog;
    }

    private static void modalState(List<WeakReference<Dialog>> windows) {
        List<String> kinds = new ArrayList<>();
        if (windows != null)
            for (WeakReference<Dialog> reference : windows) {
                Dialog dialog = reference.get();
                if (dialog != null)
                    kinds.add(KINDS.getOrDefault(dialog, "dialog"));
            }
        Diagnostics.modalState(kinds.size(),
                                                                        TextUtils.join(",", kinds));
    }

    private static Context owner(Context context) {
        while (context instanceof ContextWrapper && !(context instanceof Activity)) {
            Context base = ((ContextWrapper)context).getBaseContext();
            if (base == context)
                break;
            context = base;
        }
        return context;
    }

    private static Dialog top(Context context) {
        List<WeakReference<Dialog>> windows = WINDOWS.get(owner(context));
        if (windows != null)
            for (int i = windows.size() - 1; i >= 0; i--) {
                Dialog dialog = windows.get(i).get();
                if (dialog != null && dialog.isShowing())
                    return dialog;
            }
        return null;
    }

    /** Presentation only for retained credential/artwork dialogs; original listeners and inputs remain. */
    public static void styleNative(Dialog dialog) {
        Context c = dialog.getContext();
        if (!PreferenceManager.getDefaultSharedPreferences(c).getBoolean("try_new_ui", false))
            return;
        Window window = dialog.getWindow();
        if (window == null)
            return;
        window.setBackgroundDrawable(dialogSurface(c));
        window.setDimAmount(.45f);
        trackNative(dialog);
        window.setLayout(TvLayout.dialogWidth(c), WindowManager.LayoutParams.WRAP_CONTENT);
        styleNativeChildren(window.getDecorView(), c);
    }

    /** Observe retained native windows without replacing their cancel/dismiss/action listeners. */
    private static void trackNative(Dialog dialog) {
        if (KINDS.containsKey(dialog))
            return;
        Context context = owner(dialog.getContext());
        Dialog parent = top(context);
        View opener = anchor(context);
        WeakReference<View> previous = new WeakReference<>(opener);
        String token = Diagnostics.focusEntry(opener, "native_dialog");
        KINDS.put(dialog, "native_dialog");
        View decor = dialog.getWindow().getDecorView();
        View.OnAttachStateChangeListener observer = new View.OnAttachStateChangeListener() {
            @Override
            public void onViewAttachedToWindow(View view) {
                List<WeakReference<Dialog>> windows =
                    WINDOWS.computeIfAbsent(context, k -> new ArrayList<>());
                windows.removeIf(reference -> reference.get() == null || reference.get() == dialog);
                windows.add(new WeakReference<>(dialog));
                modalState(windows);
            }
            @Override
            public void onViewDetachedFromWindow(View view) {
                List<WeakReference<Dialog>> windows = WINDOWS.get(context);
                if (windows != null) {
                    windows.removeIf(reference -> reference.get() == null || reference.get() == dialog);
                    if (windows.isEmpty())
                        WINDOWS.remove(context);
                }
                modalState(windows);
                View target = previous.get();
                boolean restored = top(context) == parent && target != null && target.isAttachedToWindow() &&
                                   target.isShown() && target.isFocusable() && target.requestFocus();
                Diagnostics.focusRestored(
                    token, target, restored ? target : null, false, restored);
            }
        };
        decor.addOnAttachStateChangeListener(observer);
        if (decor.isAttachedToWindow())
            observer.onViewAttachedToWindow(decor);
    }

    private static void styleNativeChildren(View view, Context c) {
        if (view instanceof TextView) {
            TextView text = (TextView)view;
            if (!(view instanceof EditText)) {
                text.setTextColor(0xffd6e5ef);
                int title = c.getResources().getIdentifier("alertTitle", "id", "android");
                boolean heading =
                    view.getId() == androidx.appcompat.R.id.alertTitle || title != 0 && view.getId() == title;
                text.setTextSize(heading ? 24 : 16);
            }
            if (view instanceof Button) {
                TvButton.style(text, false);
                text.setSingleLine(false);
            }
        }
        if (view instanceof ViewGroup)
            for (int i = 0; i < ((ViewGroup)view).getChildCount(); i++)
                styleNativeChildren(((ViewGroup)view).getChildAt(i), c);
    }

    public static Dialog read(Context c, String title, String body) {
        return read(c, title, body, null);
    }

    public static Dialog read(Context c, String title, String body, Bitmap referenceImage) {
        return readInternal(c, title, body, referenceImage, null, null);
    }

    public static Dialog review(Context c, String title, String body, String confirmation, Runnable accepted) {
        return readInternal(c, title, body, null, confirmation, accepted);
    }

    private static Dialog readInternal(Context c, String title, String body, Bitmap referenceImage,
                                       String confirmation, Runnable accepted) {
        Dialog dialog = create(c, accepted == null ? "reader" : "review");
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        LinearLayout panel = new LinearLayout(c);
        panel.setOrientation(android.widget.LinearLayout.VERTICAL);
        panel.setPadding(dp(c, 20), dp(c, 16), dp(c, 20), dp(c, 16));
        panel.setBackground(dialogSurface(c));
        TextView heading = new TextView(c);
        heading.setText(title);
        heading.setTextSize(24);
        heading.setTextColor(Color.WHITE);
        panel.addView(heading);
        ScrollView scroll = new ScrollView(c);
        TextView text = new TextView(c);
        text.setText(body);
        text.setTextColor(0xffd2e1ed);
        text.setTextSize(16);
        text.setPadding(0, dp(c, 14), 0, dp(c, 14));
        View content = text;
        if (referenceImage != null) {
            LinearLayout reference = new LinearLayout(c);
            reference.setGravity(Gravity.CENTER_VERTICAL);
            reference.addView(text, new LinearLayout.LayoutParams(0, -2, 1));
            ImageView image = new ImageView(c);
            image.setImageBitmap(referenceImage);
            image.setScaleType(ImageView.ScaleType.FIT_CENTER);
            image.setContentDescription("QR containing only report reference, category and timestamp");
            LinearLayout.LayoutParams picture = new LinearLayout.LayoutParams(dp(c, 180), dp(c, 180));
            picture.setMargins(dp(c, 16), dp(c, 12), 0, dp(c, 12));
            reference.addView(image, picture);
            content = reference;
        }
        scroll.addView(content);
        scroll.setFocusable(true);
        panel.addView(scroll, new LinearLayout.LayoutParams(-1, 0, 1));
        LinearLayout actions = new LinearLayout(c);
        String[] actionLabels =
            confirmation == null ? new String[] {"Close", "Copy"} : new String[] {"Back", confirmation};
        for (int index = 0; index < actionLabels.length; index++) {
            final int action = index;
            String label = actionLabels[index];
            TextView button = TvButton.create(c, label, () -> {}, false);
            button.setSingleLine(false);
            button.setOnClickListener(v -> {
                if (action == 0)
                    dialog.dismiss();
                else if (accepted != null) {
                    if (dialog.isShowing()) {
                        dialog.dismiss();
                        accepted.run();
                    }
                } else {
                    ClipboardManager clipboard =
                        (ClipboardManager)c.getSystemService(Context.CLIPBOARD_SERVICE);
                    if (clipboard != null)
                        clipboard.setPrimaryClip(ClipData.newPlainText(title, body));
                    Toast.makeText(c, "Copied", Toast.LENGTH_SHORT).show();
                }
            });
            LinearLayout.LayoutParams size = new LinearLayout.LayoutParams(0, -2, 1);
            if (index > 0)
                size.leftMargin = dp(c, 10);
            actions.addView(button, size);
        }
        panel.addView(actions);
        dialog.setContentView(panel);
        dialog.show();
        Window w = dialog.getWindow();
        w.setBackgroundDrawableResource(android.R.color.transparent);
        w.setDimAmount(.35f);
        int width = TvLayout.dialogWidth(c), maximum = Math.min(dp(c, 360), Math.round(TvLayout.safeHeight(c) * .8f));
        int inner = View.MeasureSpec.makeMeasureSpec(width - dp(c, 40), View.MeasureSpec.EXACTLY),
            natural = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        heading.measure(inner, natural);
        content.measure(inner, natural);
        actions.measure(inner, natural);
        int chrome = dp(c, 32) + heading.getMeasuredHeight() + actions.getMeasuredHeight(),
            contentHeight = Math.min(content.getMeasuredHeight(), Math.max(dp(c, 40), maximum - chrome));
        scroll.setLayoutParams(new LinearLayout.LayoutParams(-1, contentHeight));
        w.setLayout(width, chrome + contentHeight);
        if (accepted == null)
            scroll.requestFocus();
        else
            actions.getChildAt(0).requestFocus();
        return dialog;
    }

    public static Dialog choose(Context c, String title, String[] labels, int selected, IntConsumer action) {
        return choose(c, title, labels, selected,
                      selected < 0 ? Collections.emptySet() : Collections.singleton(selected),
                      action);
    }

    public static Dialog choose(Context c, String title, String[] labels, int selected, Set<Integer> checked,
                                IntConsumer action) {
        return choose(c, title, labels, selected, checked, true, action);
    }

    public static Dialog choose(Context c, String title, String[] labels, int selected, Set<Integer> checked,
                                boolean dismissOnSelect, IntConsumer action) {
        Dialog d = create(c, "choice");
        d.requestWindowFeature(Window.FEATURE_NO_TITLE);
        LinearLayout panel = new LinearLayout(c);
        panel.setOrientation(android.widget.LinearLayout.VERTICAL);
        int pad = dp(c, 12);
        panel.setPadding(pad, pad, Math.round(pad * 1.2f), pad);
        // Shared choice list (owner-approved 5 Nov 2026, mock-up suggestions/01): a solid side panel, not glass.
        panel.setBackground(SupernovaStyle.solidPanel(c));
        panel.setClipChildren(false);
        panel.setClipToPadding(false);
        // Approved global rule (2 Nov 2026): pop-up menus have no title; the title only names the menu for accessibility.
        panel.setContentDescription(title);
        ScrollView scroll = new ScrollView(c) {
            @Override
            protected void dispatchDraw(Canvas canvas) {
                // The surrounding panel permits outward focus glow, but scrolling rows must
                // remain inside this viewport and never paint over fixed header/footer controls.
                int checkpoint = canvas.save();
                canvas.clipRect(getScrollX(), getScrollY(), getScrollX() + getWidth(), getScrollY() + getHeight());
                super.dispatchDraw(canvas);
                canvas.restoreToCount(checkpoint);
            }
        };
        scroll.setVerticalScrollBarEnabled(false);
        LinearLayout rows = new LinearLayout(c);
        rows.setOrientation(android.widget.LinearLayout.VERTICAL);
        scroll.addView(rows);
        panel.addView(scroll, new LinearLayout.LayoutParams(-1, -2));
        View initial = null;
        int height = 60;
        int footerHeight = 0;
        for (int i = 0; i < labels.length; i++) {
            final int index = i;
            boolean group = labels[i].startsWith("— ");
            boolean enabled = !group && !labels[i].endsWith(" (unavailable)") &&
                              !labels[i].endsWith(" — unavailable") && !labels[i].contains("Coming soon");
            if (group) {
                View heading = SupernovaStyle.groupHeading(c, labels[i].substring(2), 11);
                heading.setTag(i);
                rows.addView(heading, new LinearLayout.LayoutParams(-1, dp(c, 25)));
                height += 25;
                continue;
            }
            LinearLayout row = new LinearLayout(c);
            row.setGravity(Gravity.CENTER_VERTICAL);
            row.setPadding(SupernovaStyle.dp(c, SupernovaStyle.ROW_INSET_DP), 0,
                           SupernovaStyle.dp(c, SupernovaStyle.ROW_INSET_DP), 0);
            row.setBackground(focus(c));
            row.setFocusable(enabled);
            row.setFocusableInTouchMode(enabled);
            row.setEnabled(enabled);
            row.setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);
            row.setAlpha(enabled ? 1f : group ? 1f : .4f);
            Diagnostics.semantic(row, "dialog.choice." + i);
            if (!group) {
                ImageView icon = new ImageView(c);
                String iconLabel = title.equals(c.getString(com.archos.mediacenter.video.R.string.menu_audio)) ? "Audio"
                                   : title.equals(c.getString(com.archos.mediacenter.video.R.string.menu_subtitles))
                                       ? "Subtitles"
                                       : labels[i];
                icon.setImageDrawable(PreviewGenres.known(iconLabel) ? new PreviewGenres.Icon(iconLabel)
                                                                     : new PreviewIcon(iconLabel));
                row.addView(icon, new LinearLayout.LayoutParams(dp(c, 18), dp(c, 18)));
            }
            TextView label = new TextView(c);
            label.setText(group ? labels[i].substring(2) : labels[i]);
            label.setTag("preview-label:" + i);
            label.setTextSize(group ? 11 : 14);
            label.setTextColor(group ? 0xff8aaec5 : Color.WHITE);
            label.setSingleLine(true);
            label.setEllipsize(TextUtils.TruncateAt.END);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0, -2, 1);
            lp.leftMargin = group ? 0 : dp(c, 10);
            row.addView(label, lp);
            if (!group) {
                ImageView check = new ImageView(c);
                check.setTag("preview-check:" + i);
                check.setImageDrawable(new PreviewIcon("check"));
                check.setVisibility(checked.contains(i) ? View.VISIBLE : View.INVISIBLE);
                row.addView(check, new LinearLayout.LayoutParams(dp(c, 18), dp(c, 18)));
            }
            row.setTag(i);
            row.setContentDescription(labels[i] + (checked.contains(i) ? ", selected" : ""));
            row.setOnClickListener(v -> {
                if (dismissOnSelect)
                    d.dismiss();
                action.accept(index);
            });
            int rh = group ? 25 : 37;
            height += rh;
            if (i == labels.length - 1 && labels[i].equals("Done")) {
                View doneDivider = new View(c);
                doneDivider.setBackgroundColor(0x50426a80);
                LinearLayout.LayoutParams dividerSize = new LinearLayout.LayoutParams(-1, dp(c, 1));
                dividerSize.topMargin = dp(c, 10);
                dividerSize.bottomMargin = dp(c, 8);
                panel.addView(doneDivider, dividerSize);
                panel.addView(row, new LinearLayout.LayoutParams(-1, dp(c, rh)));
                footerHeight = dp(c, rh + 19);
                height += 19;
            } else
                rows.addView(row, new LinearLayout.LayoutParams(-1, dp(c, rh)));
            if (enabled && (initial == null || i == selected))
                initial = row;
        }
        d.setContentView(panel);
        d.show();
        Window w = d.getWindow();
        w.setBackgroundDrawableResource(android.R.color.transparent);
        w.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        w.setDimAmount(.32f);
        Paint measure = new Paint();
        measure.setTextSize(14 * c.getResources().getDisplayMetrics().scaledDensity);
        int longest = 0;
        for (String label : labels)
            if (!label.startsWith("— "))
                longest = Math.max(longest, (int)Math.ceil(measure.measureText(label)));
        // Approved global rule: width = longest item (icon + text + tick) + breathing room, between 360px and 40% of the screen.
        int width = SupernovaStyle.menuWidth(c, longest + dp(c, 18 + 10 + 18 + 6), pad + Math.round(pad * 1.2f));
        int chrome = pad * 2;
        int maxHeight = c.getResources().getDisplayMetrics().heightPixels - dp(c, 48);
        int menuHeight = Math.min(dp(c, height - 60) + chrome, maxHeight);
        // Docked at the right below the top bar, sized to its content (owner rule: long lists open the side panel).
        int screenWidth = c.getResources().getDisplayMetrics().widthPixels;
        int screenHeight = c.getResources().getDisplayMetrics().heightPixels;
        int top = Math.round(screenHeight * 56f / 540f);
        menuHeight = Math.min(menuHeight, screenHeight - top - dp(c, 20));
        WindowManager.LayoutParams params = w.getAttributes();
        params.gravity = Gravity.TOP | Gravity.LEFT;
        params.x = Math.max(dp(c, 14), screenWidth - width - Math.round(screenWidth * 28f / 960f));
        params.y = top;
        w.setAttributes(params);
        w.setDimAmount(.35f);
        scroll.setLayoutParams(
            new LinearLayout.LayoutParams(-1, Math.max(dp(c, 30), menuHeight - chrome - footerHeight)));
        w.setLayout(width, menuHeight);
        if (initial != null)
            initial.requestFocus();
        return d;
    }

    private static View anchor(Context c) {
        Dialog parent = top(c);
        if (parent != null && parent.getCurrentFocus() != null)
            return parent.getCurrentFocus();
        Context owner = owner(c);
        return owner instanceof Activity ? ((Activity)owner).getCurrentFocus() : null;
    }

    public static Drawable menuSurface(Context c) {
        return SupernovaStyle.glass(c);
    }

    /** Membership states use a plain white plus/check rather than a second generic row icon. */
    public static void updateMembership(Dialog dialog, Set<Integer> checked, int count) {
        if (dialog == null || dialog.getWindow() == null)
            return;
        View root = dialog.getWindow().getDecorView();
        for (int i = 0; i < count; i++) {
            ImageView mark = root.findViewWithTag("preview-check:" + i);
            if (mark == null)
                continue;
            android.view.ViewGroup row = (android.view.ViewGroup)mark.getParent();
            if (row.getChildCount() > 0 && row.getChildAt(0) instanceof ImageView)
                row.getChildAt(0).setVisibility(View.GONE);
            mark.setImageDrawable(new PreviewIcon(checked.contains(i) ? "check" : "plus"));
            mark.setColorFilter(Color.WHITE);
            mark.setVisibility(View.VISIBLE);
            TextView label = root.findViewWithTag("preview-label:" + i);
            row.setContentDescription(label.getText() + (checked.contains(i) ? ", selected" : ", not selected"));
        }
    }

    public static void updateChecks(Dialog dialog, Set<Integer> checked) {
        if (dialog == null || dialog.getWindow() == null)
            return;
        updateChecks(dialog.getWindow().getDecorView(), checked);
    }

    private static void updateChecks(View view, Set<Integer> checked) {
        Object tag = view.getTag();
        if (tag instanceof String && ((String)tag).startsWith("preview-check:")) {
            int index = Integer.parseInt(((String)tag).substring(14));
            view.setVisibility(checked.contains(index) ? View.VISIBLE : View.INVISIBLE);
            View parent = (View)view.getParent();
            CharSequence description = parent.getContentDescription();
            if (description != null)
                parent.setContentDescription(description.toString().replace(", selected", "") +
                                             (checked.contains(index) ? ", selected" : ""));
        }
        if (view instanceof ViewGroup)
            for (int i = 0; i < ((ViewGroup)view).getChildCount(); i++)
                updateChecks(((ViewGroup)view).getChildAt(i), checked);
    }

    public static void updateLabel(Dialog dialog, int index, String label) {
        if (dialog == null || dialog.getWindow() == null)
            return;
        View view = dialog.getWindow().getDecorView().findViewWithTag("preview-label:" + index);
        if (view instanceof TextView) {
            ((TextView)view).setText(label);
            ((View)view.getParent()).setContentDescription(label);
        }
    }

    public static Dialog confirmPageDelete(Context c, String title, String message, Runnable action) {
        Dialog dialog = message(c, title, message, false, new String[] {"Cancel", "Delete"}, 0, index -> {
            if (index == 1)
                action.run();
        });
        TextView remove = dialog.findViewById(android.R.id.content).findViewWithTag("semantic:dialog.action.1");
        if (remove != null)
            remove.setTextColor(SupernovaStyle.destructiveContent());
        return dialog;
    }

    public static Dialog confirmDelete(Context c, String title, String message, Runnable action) {
        AlertDialog dialog = new AlertDialog.Builder(c)
                                             .setTitle(title)
                                             .setMessage(message)
                                             .setIcon(android.R.drawable.ic_dialog_alert)
                                             .setNegativeButton("Cancel", null)
                                             .setPositiveButton("Delete", (d, w) -> action.run())
                                             .create();
        dialog.setOnShowListener(d -> {
            styleNative(dialog);
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(SupernovaStyle.destructiveContent());
            dialog.getButton(AlertDialog.BUTTON_NEGATIVE).requestFocus();
        });
        dialog.show();
        return dialog;
    }

    /** Content-driven, centred information/welcome/confirmation variant. Choices retain their menu. */
    public static Dialog message(Context c, String title, String body, boolean welcome, String[] labels, int initial,
                                 IntConsumer action) {
        Dialog dialog = create(c, "dialog");
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        int width = TvLayout.dialogWidth(c), padding =
                                                 Math.max(dp(c, 20), Math.min(dp(c, 32), Math.round(width * .075f)));
        LinearLayout panel = new LinearLayout(c);
        panel.setOrientation(LinearLayout.VERTICAL);
        panel.setPadding(padding, dp(c, 24), padding, dp(c, 24));
        panel.setBackground(SupernovaStyle.glass(c));
        ScrollView scroll = new ScrollView(c);
        scroll.setFocusable(true);
        LinearLayout content = new LinearLayout(c);
        content.setOrientation(LinearLayout.VERTICAL);
        scroll.addView(content);
        panel.addView(scroll, new LinearLayout.LayoutParams(-1, -2));
        if (welcome) {
            ImageView icon = new ImageView(c);
            icon.setImageResource(com.archos.mediacenter.video.R.drawable.foundation_icon);
            icon.setContentDescription("Supernova");
            LinearLayout.LayoutParams image = new LinearLayout.LayoutParams(dp(c, 56), dp(c, 56));
            image.gravity = Gravity.CENTER;
            content.addView(icon, image);
            TextView brand = SharedThreePanel.text(c, "SUPERNOVA", 14);
            brand.setLetterSpacing(.06f);
            brand.setGravity(Gravity.CENTER);
            brand.setPadding(0, dp(c, 12), 0, dp(c, 8));
            content.addView(brand);
        }
        TextView heading = SharedThreePanel.text(c, title, 24);
        heading.setTypeface(null, Typeface.BOLD);
        heading.setGravity(Gravity.CENTER);
        content.addView(heading);
        TextView copy = SharedThreePanel.text(c, body, 16);
        copy.setTextColor(0xffc7d8e5);
        copy.setGravity(Gravity.CENTER);
        copy.setPadding(0, dp(c, 16), 0, dp(c, 20));
        copy.setLineSpacing(dp(c, 4), 1);
        content.addView(copy);
        LinearLayout actions = new LinearLayout(c);
        actions.setOrientation(LinearLayout.VERTICAL);
        panel.addView(actions);
        View selected = null;
        for (int i = 0; i < labels.length; i++) {
            final int index = i;
            TextView button = TvButton.create(c, labels[i], () -> {
                dialog.dismiss();
                action.accept(index);
            }, false);
            button.setSingleLine(false);
            button.setMinHeight(dp(c, 48));
            Diagnostics.semantic(button, "dialog.action." + i);
            button.setTag("semantic:dialog.action." + i);
            LinearLayout.LayoutParams size = new LinearLayout.LayoutParams(-1, -2);
            size.topMargin = i == 0 ? 0 : dp(c, 10);
            actions.addView(button, size);
            if (i == initial)
                selected = button;
        }
        dialog.setContentView(panel);
        dialog.show();
        Window window = dialog.getWindow();
        window.setBackgroundDrawableResource(android.R.color.transparent);
        window.setGravity(Gravity.CENTER);
        window.setDimAmount(.45f);
        window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        if (Build.VERSION.SDK_INT >= 31) {
            window.addFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND);
            WindowManager.LayoutParams attributes = window.getAttributes();
            attributes.setBlurBehindRadius(dp(c, 6));
            window.setAttributes(attributes);
        }
        int exact = View.MeasureSpec.makeMeasureSpec(width - 2 * padding, View.MeasureSpec.EXACTLY),
            free = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        actions.measure(exact, free);
        content.measure(exact, free);
        int chrome = 2 * dp(c, 24) + actions.getMeasuredHeight();
        int available = Math.max(dp(c, 32), Math.round(TvLayout.safeHeight(c) * .8f) - chrome);
        int bodyHeight = Math.min(content.getMeasuredHeight(), available);
        scroll.setLayoutParams(new LinearLayout.LayoutParams(-1, bodyHeight));
        window.setLayout(width, chrome + bodyHeight);
        if (selected != null)
            selected.requestFocus();
        return dialog;
    }

    public static Drawable dialogSurface(Context c) {
        return SupernovaStyle.glass(c);
    }

    public static int dp(Context c, int v) {
        return TvLayout.dimension(c, v);
    }

    public static StateListDrawable focus(Context c) {
        return new PreviewContentFocus(c);
    }

    public static StateListDrawable buttonFocus(Context c) {
        return focus(c);
    }

    public static StateListDrawable actionContainerFocus(Context c) {
        // A focusable action container is a button: use SupernovaStyle.applyButton(view, STANDARD) so its content follows focus.
        return SupernovaStyle.button(c, SupernovaStyle.Surface.STANDARD);
    }

    /** The shared glass as a GradientDrawable (approved 2 Nov 2026: one material for every pop-up and panel). */
    public static GradientDrawable surface(Context c, boolean ignored) {
        GradientDrawable g = new GradientDrawable();
        g.setColor(SupernovaStyle.GLASS);
        g.setCornerRadius(SupernovaStyle.dp(c, SupernovaStyle.PANEL_RADIUS_DP));
        g.setStroke(Math.max(1, SupernovaStyle.dp(c, 1)), SupernovaStyle.GLASS_EDGE);
        return g;
    }
}
