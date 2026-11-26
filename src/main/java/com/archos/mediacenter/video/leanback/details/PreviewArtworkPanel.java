package com.archos.mediacenter.video.leanback.details;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.archos.mediacenter.video.leanback.PreviewDialog;
import com.archos.mediacenter.video.leanback.PreviewIcon;
import com.archos.mediacenter.video.leanback.SupernovaStyle;
import com.archos.mediascraper.ScraperImage;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * More › Artwork (D3; page sheet DETAILS.md; mock-ups details-background/17, 20): one side panel floating on the
 * right. "Choose for" Hero and rows · Library page; backdrops two to a row, posters three; the current choice has the
 * tick; the hero previews the backdrop you are on; OK keeps it. Back closes (the hero shows the kept picture again).
 */
public final class PreviewArtworkPanel
{
    /** Saves a choice; {@code done} gets whether it was kept. */
    public interface Apply
    {
        void apply(ScraperImage image, boolean poster, Consumer<Boolean> done);
    }

    static final String[] CHOICES = {"Hero and rows", "Library page"};

    private final Context context;
    private final List<ScraperImage> backdrops;
    private final List<ScraperImage> posters;
    private ScraperImage backdropChosen;
    private ScraperImage posterChosen;
    private final Consumer<Uri> preview;
    private final Apply apply;
    private final LinearLayout grid;
    private final TextView hint;
    private final List<TextView> chips = new ArrayList<>();
    private boolean showingPosters;
    private boolean busy;

    private PreviewArtworkPanel(Context context, List<ScraperImage> backdrops, ScraperImage backdropChosen,
                                List<ScraperImage> posters, ScraperImage posterChosen, Consumer<Uri> preview,
                                Apply apply)
    {
        this.context = context;
        this.backdrops = backdrops;
        this.posters = posters;
        this.backdropChosen = backdropChosen;
        this.posterChosen = posterChosen;
        this.preview = preview;
        this.apply = apply;
        grid = new LinearLayout(context);
        grid.setOrientation(LinearLayout.VERTICAL);
        grid.setClipChildren(false);
        grid.setClipToPadding(false);
        hint = new TextView(context);
    }

    public static Dialog show(Context context, List<ScraperImage> backdrops, ScraperImage backdropChosen,
                              List<ScraperImage> posters, ScraperImage posterChosen, Consumer<Uri> preview,
                              Apply apply)
    {
        PreviewArtworkPanel panel = new PreviewArtworkPanel(context, backdrops, backdropChosen, posters,
                                                            posterChosen, preview, apply);
        return panel.open();
    }

    private Dialog open()
    {
        Dialog dialog = PreviewDialog.create(context, "artwork_panel");
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        LinearLayout panel = new LinearLayout(context);
        panel.setOrientation(LinearLayout.VERTICAL);
        int pad = dp(20);
        panel.setPadding(pad, dp(16), pad, dp(16));
        panel.setBackground(SupernovaStyle.glass(context));
        panel.setClipChildren(false);
        // DETAILS.md › Artwork (`tv/09`): no title or footer; "For" fixed at the top, the pictures scroll beneath it.
        LinearLayout choose = new LinearLayout(context);
        choose.setGravity(Gravity.CENTER_VERTICAL);
        choose.addView(text("For", 16, SupernovaStyle.TEXT_SECONDARY));
        for (int index = 0; index < CHOICES.length; index++)
        {
            TextView chip = chip(index);
            LinearLayout.LayoutParams size = new LinearLayout.LayoutParams(-2, dp(30));
            size.leftMargin = dp(10);
            choose.addView(chip, size);
            chips.add(chip);
        }
        LinearLayout.LayoutParams chooseSize = new LinearLayout.LayoutParams(-1, -2);
        chooseSize.topMargin = 0;
        chooseSize.bottomMargin = dp(12);
        panel.addView(choose, chooseSize);
        ScrollView scroll = new ScrollView(context);
        scroll.setVerticalScrollBarEnabled(false);
        scroll.setClipChildren(false);
        scroll.setClipToPadding(false);
        scroll.setPadding(dp(6), dp(6), dp(6), dp(6));
        scroll.addView(grid);
        panel.addView(scroll, new LinearLayout.LayoutParams(-1, 0, 1));
        hint.setTextSize(14);
        hint.setTextColor(SupernovaStyle.TEXT_SECONDARY);
        // The hint is kept for "Keeping this picture…" but not shown as a footer (tv/09).
        dialog.setContentView(panel);
        dialog.setOnDismissListener(gone -> preview.accept(null));
        dialog.show();
        Window window = dialog.getWindow();
        if (window != null)
        {
            window.setBackgroundDrawableResource(android.R.color.transparent);
            int screenWidth = context.getResources().getDisplayMetrics().widthPixels;
            int screenHeight = context.getResources().getDisplayMetrics().heightPixels;
            int width = Math.round(screenWidth * 0.4f);
            int top = Math.round(screenHeight * 56f / 540f);
            WindowManager.LayoutParams at = window.getAttributes();
            at.gravity = Gravity.TOP | Gravity.LEFT;
            at.x = screenWidth - width - Math.round(screenWidth * 28f / 960f);
            at.y = top;
            at.dimAmount = 0f;
            window.setAttributes(at);
            window.setLayout(width, screenHeight - top - dp(16));
        }
        showChoice(false);
        return dialog;
    }

    private TextView chip(int index)
    {
        TextView chip = text(CHOICES[index], 16, Color.WHITE);
        chip.setGravity(Gravity.CENTER);
        chip.setPadding(dp(12), 0, dp(12), 0);
        chip.setFocusable(true);
        chip.setFocusableInTouchMode(true);
        chip.setTag("artwork.choice:" + index);
        chip.setOnFocusChangeListener((view, focused) -> {
            if (focused && showingPosters != (index == 1))
            {
                showChoice(index == 1);
            }
            paintChips();
        });
        chip.setOnClickListener(view -> showChoice(index == 1));
        return chip;
    }

    private void paintChips()
    {
        for (int index = 0; index < chips.size(); index++)
        {
            TextView chip = chips.get(index);
            boolean chosen = showingPosters == (index == 1);
            boolean focused = chip.isFocused();
            chip.setTextColor(focused ? SupernovaStyle.FOCUS_TEXT : Color.WHITE);
            chip.setBackground(focused ? box(SupernovaStyle.FOCUS_FILL, 8) : chosen ? box(0x33FFFFFF, 8) : null);
        }
    }

    /** Backdrops (Hero and rows) or posters (Library page). */
    private void showChoice(boolean poster)
    {
        showingPosters = poster;
        paintChips();
        grid.removeAllViews();
        List<ScraperImage> images = poster ? posters : backdrops;
        hint.setText(poster ? "Posters show on the library pages. Your choice is kept."
                            : "The hero shows the picture you are on. Your choice is kept.");
        if (images.isEmpty())
        {
            grid.addView(text(poster ? "No posters found for this title" : "No backdrops found for this title", 15,
                              Color.WHITE));
            return;
        }
        int columns = poster ? 3 : 2;
        LinearLayout row = null;
        for (int index = 0; index < images.size(); index++)
        {
            if (index % columns == 0)
            {
                row = new LinearLayout(context);
                row.setClipChildren(false);
                LinearLayout.LayoutParams rowSize = new LinearLayout.LayoutParams(-1, -2);
                rowSize.bottomMargin = dp(14);
                grid.addView(row, rowSize);
            }
            LinearLayout.LayoutParams cardSize = new LinearLayout.LayoutParams(0, -2, 1);
            if (index % columns > 0)
            {
                cardSize.leftMargin = dp(14);
            }
            row.addView(card(images.get(index), index, poster), cardSize);
        }
        for (int index = images.size() % columns; index > 0 && index < columns; index++)
        {
            LinearLayout.LayoutParams filler = new LinearLayout.LayoutParams(0, 1, 1);
            filler.leftMargin = dp(14);
            row.addView(new View(context), filler);
        }
    }

    private View card(ScraperImage image, int index, boolean poster)
    {
        LinearLayout holder = new LinearLayout(context);
        holder.setOrientation(LinearLayout.VERTICAL);
        holder.setClipChildren(false);
        FrameLayout card = new FrameLayout(context)
        {
            @Override
            protected void onMeasure(int w, int h)
            {
                int width = MeasureSpec.getSize(w);
                int height = Math.round(width * (poster ? 1.5f : 9f / 16f));
                super.onMeasure(w, MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY));
            }
        };
        card.setFocusable(true);
        card.setFocusableInTouchMode(true);
        String name = (poster ? "Poster " : "Backdrop ") + (index + 1);
        card.setTag("artwork:" + (poster ? "poster:" : "backdrop:") + index);
        card.setContentDescription(name);
        card.setClipToOutline(true);
        GradientDrawable base = new GradientDrawable();
        base.setColor(0xFF2A2E35);
        base.setCornerRadius(dp(8));
        card.setBackground(base);
        ImageView picture = new ImageView(context);
        picture.setScaleType(ImageView.ScaleType.CENTER_CROP);
        card.addView(picture, new FrameLayout.LayoutParams(-1, -1));
        Uri address = address(image);
        if (address != null)
        {
            try
            {
                Picasso.get().load(address).fit().centerCrop().into(picture);
            }
            catch (RuntimeException unavailable)
            {
                // No picture loader: the card keeps its charcoal.
            }
        }
        TextView tick = text("✓", 16, SupernovaStyle.FOCUS_TEXT);
        tick.setGravity(Gravity.CENTER);
        GradientDrawable dot = new GradientDrawable();
        dot.setShape(GradientDrawable.OVAL);
        dot.setColor(Color.WHITE);
        tick.setBackground(dot);
        boolean chosen = same(image, poster ? posterChosen : backdropChosen);
        tick.setVisibility(chosen ? View.VISIBLE : View.INVISIBLE);
        tick.setTag("artwork.tick");
        FrameLayout.LayoutParams tickAt = new FrameLayout.LayoutParams(dp(26), dp(26), Gravity.TOP | Gravity.END);
        tickAt.setMargins(0, dp(8), dp(8), 0);
        card.addView(tick, tickAt);
        if (index == 0)
        {
            // TMDB's first picture is its own pick: the Suggested badge (tv/09).
            TextView suggested = text("Suggested", 12, Color.WHITE);
            suggested.setTag("artwork.suggested");
            suggested.setPadding(dp(8), dp(2), dp(8), dp(2));
            GradientDrawable pill = new GradientDrawable();
            pill.setColor(0xCC16181C);
            pill.setStroke(Math.max(1, dp(1)), 0xB3FFFFFF);
            pill.setCornerRadius(dp(10));
            suggested.setBackground(pill);
            FrameLayout.LayoutParams suggestedAt =
                new FrameLayout.LayoutParams(-2, -2, Gravity.TOP | Gravity.START);
            suggestedAt.setMargins(dp(8), dp(8), 0, 0);
            card.addView(suggested, suggestedAt);
        }
        TextView label = text(name, 14, Color.WHITE);
        card.setOnFocusChangeListener((view, focused) -> {
            view.animate().scaleX(focused ? 1.05f : 1f).scaleY(focused ? 1.05f : 1f).setDuration(120).start();
            view.setForeground(focused ? ring() : null);
            label.setTypeface(android.graphics.Typeface.create(focused ? "sans-serif-medium" : "sans-serif",
                                                               android.graphics.Typeface.NORMAL));
            if (focused && !poster)
            {
                // The hero previews the backdrop you are on.
                preview.accept(address);
            }
        });
        card.setOnClickListener(view -> choose(image, poster));
        holder.addView(card, new LinearLayout.LayoutParams(-1, -2));
        LinearLayout.LayoutParams labelSize = new LinearLayout.LayoutParams(-1, -2);
        labelSize.topMargin = dp(6);
        holder.addView(label, labelSize);
        return holder;
    }

    private void choose(ScraperImage image, boolean poster)
    {
        if (busy)
        {
            return;
        }
        busy = true;
        hint.setText("Keeping this picture…");
        apply.apply(image, poster, kept -> grid.post(() -> {
            busy = false;
            if (Boolean.TRUE.equals(kept))
            {
                if (poster)
                {
                    posterChosen = image;
                }
                else
                {
                    backdropChosen = image;
                }
                String focused = grid.findFocus() == null ? null : String.valueOf(grid.findFocus().getTag());
                showChoice(poster);
                View again = focused == null ? null : grid.findViewWithTag(focused);
                if (again != null)
                {
                    again.requestFocus();
                }
            }
            else
            {
                hint.setText("That picture couldn't be kept. Try again.");
            }
        }));
    }

    static Uri address(ScraperImage image)
    {
        if (image == null)
        {
            return null;
        }
        File file = image.getLargeFileF();
        if (file != null && file.isFile())
        {
            return Uri.fromFile(file);
        }
        return image.getLargeUrl() == null ? null : Uri.parse(image.getLargeUrl());
    }

    private static boolean same(ScraperImage first, ScraperImage second)
    {
        if (first == null || second == null)
        {
            return false;
        }
        String a = first.getLargeUrl();
        String b = second.getLargeUrl();
        return a != null && a.equals(b);
    }

    private GradientDrawable ring()
    {
        GradientDrawable ring = new GradientDrawable();
        ring.setColor(Color.TRANSPARENT);
        ring.setCornerRadius(dp(8));
        ring.setStroke(dp(3), Color.WHITE);
        return ring;
    }

    private GradientDrawable box(int colour, int radius)
    {
        GradientDrawable shape = new GradientDrawable();
        shape.setColor(colour);
        shape.setCornerRadius(dp(radius));
        return shape;
    }

    private TextView text(String value, float size, int colour)
    {
        TextView view = new TextView(context);
        view.setText(value);
        view.setTextSize(size);
        view.setTextColor(colour);
        view.setSingleLine(true);
        return view;
    }

    private int dp(float value)
    {
        return Math.round(value * context.getResources().getDisplayMetrics().density);
    }
}
