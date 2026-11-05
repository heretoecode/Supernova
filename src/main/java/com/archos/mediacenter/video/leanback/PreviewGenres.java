package com.archos.mediacenter.video.leanback;

import android.app.Dialog;
import android.content.Context;
import android.graphics.*;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.LinearLayout;
import java.util.*;
import java.util.function.Consumer;

/** Genre rules and their semantic icons are shared by filters and dynamic Home rows. */
public final class PreviewGenres {
    public static final String[] NAMES = {
        "Action", "Adventure", "Animation", "Comedy", "Crime", "Documentary", "Drama",
        "Family", "Fantasy", "History", "Horror", "Kids", "Music", "Mystery", "News", "Reality", "Romance",
        "Science Fiction", "Soap", "Talk", "Thriller", "TV Movie", "War", "Western", "Action & Adventure",
        "Sci-Fi & Fantasy", "War & Politics"
    };

    public static Set<String> parse(String value) {
        Set<String> values = new LinkedHashSet<>();
        if (value != null)
            for (String s : value.split("[|,;/]"))
                if (!s.trim().isEmpty())
                    values.add(s.trim());
        return values;
    }

    public static boolean matches(String genres, Set<String> selected) {
        if (selected.isEmpty())
            return true;
        Set<String> values = parse(genres);
        for (String genre : selected)
            for (String value : values)
                if (value.equalsIgnoreCase(genre))
                    return true;
        return false;
    }

    public static boolean matches(String genres, Set<String> selected, boolean all) {
        if (!all)
            return matches(genres, selected);
        Set<String> available = parse(genres);
        for (String expected : selected)
            if (available.stream().noneMatch(value -> value.equalsIgnoreCase(expected)))
                return false;
        return true;
    }

    public static boolean known(String label) {
        return Arrays.asList(NAMES).contains(label);
    }

    public static void choose(
        Context c, Collection<String> options, Set<String> selected, Consumer<Set<String>> accept) {
        choose(c, options, selected, false, accept);
    }

    public static void choose(
        Context c, Collection<String> options, Set<String> selected, boolean matchAll, Consumer<Set<String>> accept) {
        TreeSet<String> available = new TreeSet<>(options);
        available.addAll(selected);
        List<String> names = new ArrayList<>(available);
        Set<String> result = new LinkedHashSet<>(selected);
        List<String> labels = new ArrayList<>();
        labels.add("All Genres");
        labels.addAll(names);
        Set<Integer> checks = new HashSet<>();
        if (result.isEmpty())
            checks.add(0);
        for (int i = 0; i < names.size(); i++)
            if (result.contains(names.get(i)))
                checks.add(i + 1);
        Dialog[] menu = {
            null
        };
        menu[0] = PreviewDialog.choose(c, "Genres · Match " + (matchAll ? "All" : "Any") + " Selected",
            labels.toArray(new String[0]), -1, checks, false, n -> {
                if (n == 0)
                    result.clear();
                else if (!result.add(names.get(n - 1)))
                    result.remove(names.get(n - 1));
                checks.clear();
                if (result.isEmpty())
                    checks.add(0);
                for (int i = 0; i < names.size(); i++)
                    if (result.contains(names.get(i)))
                        checks.add(i + 1);
                PreviewDialog.updateChecks(menu[0], checks);
                accept.accept(new LinkedHashSet<>(result));
            });
        View label = menu[0].getWindow().getDecorView().findViewWithTag("preview-label:0");
        if (label != null && label.getParent() instanceof View
            && ((View) label.getParent()).getParent() instanceof LinearLayout) {
            LinearLayout rows =
                (LinearLayout) ((View) label.getParent()).getParent();
            View divider = new View(c);
            divider.setBackgroundColor(0x557f8996);
            rows.addView(divider, 1, new LinearLayout.LayoutParams(-1, PreviewDialog.dp(c, 1)));
        }
    }

    public static final class Icon extends Drawable {
        private final String kind;
        private final Paint paint = new Paint(3);

        public Icon(String genre) {
            kind = genre.toLowerCase(Locale.ROOT);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(1.5f);
            paint.setStrokeCap(Paint.Cap.ROUND);
            paint.setStrokeJoin(Paint.Join.ROUND);
            paint.setColor(0xffe1e9ef);
        }

        private void line(Canvas c, float... xy) {
            Path p = new Path();
            p.moveTo(xy[0], xy[1]);
            for (int i = 2; i < xy.length; i += 2)
                p.lineTo(xy[i], xy[i + 1]);
            c.drawPath(p, paint);
        }

        @Override
        public void draw(Canvas c) {
            c.save();
            c.translate(getBounds().left, getBounds().top);
            c.scale(getBounds().width() / 24f, getBounds().height() / 24f);
            if (kind.equals("action")) {
                line(c, 4, 21, 19, 3, 21, 3, 21, 6, 6, 21);
                line(c, 4, 13, 11, 20);
            } else if (kind.equals("action & adventure")) {
                // Approved icon set v2 (customise-home/06): a mountain with a flag.
                line(c, 2, 21, 9, 9, 13, 15, 16, 11, 22, 21, 2, 21);
                line(c, 9, 9, 9, 3, 14, 4.5f, 9, 6);
            } else if (kind.equals("adventure")) {
                // v2: a compass, so it no longer copies Action & Adventure.
                c.drawCircle(12, 12, 9, paint);
                line(c, 15.5f, 8.5f, 13.5f, 13.5f, 8.5f, 15.5f, 10.5f, 10.5f, 15.5f, 8.5f);
                c.drawPoint(12, 12, paint);
            } else if (kind.equals("animation")) {
                line(c, 5, 17, 17, 3, 21, 7, 9, 21, 5, 21, 5, 17);
                line(c, 14, 6, 18, 10);
            } else if (kind.equals("comedy") || kind.equals("drama")) {
                line(c, 3, 4, 21, 4, 20, 15, 12, 22, 4, 15, 3, 4);
                c.drawCircle(8, 10, 1, paint);
                c.drawCircle(16, 10, 1, paint);
                c.drawArc(7, kind.equals("comedy") ? 10 : 15, 17, kind.equals("comedy") ? 18 : 21,
                    kind.equals("comedy") ? 0 : 180, 180, false, paint);
            } else if (kind.equals("crime")) {
                line(c, 12, 2, 21, 6, 19, 16, 12, 22, 5, 16, 3, 6, 12, 2);
                c.drawCircle(12, 11, 3, paint);
            } else if (kind.equals("documentary")) {
                c.drawRect(3, 8, 16, 20, paint);
                line(c, 16, 11, 22, 8, 22, 20, 16, 17);
                c.drawCircle(7, 5, 3, paint);
                c.drawCircle(14, 5, 3, paint);
            } else if (kind.equals("kids")) {
                line(c, 12, 2, 20, 8, 12, 17, 4, 8, 12, 2, 12, 17, 16, 20, 12, 23);
                line(c, 4, 8, 20, 8);
            } else if (kind.equals("family")) {
                c.drawCircle(7, 6, 3, paint);
                c.drawCircle(17, 6, 3, paint);
                c.drawCircle(12, 14, 2, paint);
                line(c, 2, 21, 3, 12, 7, 10, 9, 12);
                line(c, 22, 21, 21, 12, 17, 10, 15, 12);
                line(c, 8, 22, 9, 18, 15, 18, 16, 22);
            } else if (kind.equals("fantasy")) {
                line(c, 3, 22, 16, 9);
                line(c, 17, 2, 18, 6, 22, 7, 18, 8, 17, 12, 16, 8, 12, 7, 16, 6, 17, 2);
                c.drawPoint(6, 4, paint);
            } else if (kind.equals("history")) {
                line(c, 5, 2, 19, 2, 19, 6, 8, 18, 8, 22);
                line(c, 5, 22, 19, 22, 19, 18, 8, 6, 8, 2);
            } else if (kind.equals("horror")) {
                c.drawArc(4, 2, 20, 18, 180, 180, false, paint);
                line(c, 4, 10, 4, 22, 8, 19, 12, 22, 16, 19, 20, 22, 20, 10);
                c.drawCircle(9, 10, 1, paint);
                c.drawCircle(15, 10, 1, paint);
            } else if (kind.equals("music")) {
                line(c, 9, 18, 9, 4, 20, 2, 20, 16);
                c.drawOval(3, 16, 9, 21, paint);
                c.drawOval(14, 14, 20, 19, paint);
            } else if (kind.equals("mystery")) {
                c.drawCircle(10, 10, 7, paint);
                line(c, 15, 15, 22, 22);
                line(c, 9, 7, 12, 7, 12, 10, 10, 12);
                c.drawPoint(10, 14, paint);
            } else if (kind.equals("romance")) {
                Path p = new Path();
                p.moveTo(12, 21);
                p.cubicTo(-8, 8, 6, -2, 12, 7);
                p.cubicTo(18, -2, 32, 8, 12, 21);
                c.drawPath(p, paint);
            } else if (kind.equals("sci-fi & fantasy")) {
                c.drawCircle(9, 15, 6, paint);
                line(c, 2, 20, 16, 9);
                line(c, 18, 1, 19, 5, 23, 6, 19, 7, 18, 11, 17, 7, 13, 6, 17, 5, 18, 1);
            } else if (kind.equals("science fiction")) {
                c.drawCircle(12, 12, 7, paint);
                c.save();
                c.rotate(-30, 12, 12);
                c.drawOval(1, 9, 23, 15, paint);
                c.restore();
                c.drawPoint(3, 3, paint);
            } else if (kind.equals("thriller")) {
                // v2: a pulse line (the lightning bolt read as "electric").
                line(c, 2, 13, 7, 13, 9.5f, 7, 13, 19, 15.5f, 11, 17, 13, 22, 13);
            } else if (kind.equals("war & politics")) {
                line(c, 2, 8, 12, 2, 22, 8, 2, 8);
                line(c, 3, 22, 21, 22);
                for (int x = 5; x <= 19; x += 7)
                    line(c, x, 10, x, 19);
            } else if (kind.equals("war")) {
                line(c, 3, 2, 21, 22);
                line(c, 21, 2, 3, 22);
                line(c, 1, 16, 8, 22);
                line(c, 16, 22, 23, 16);
            } else if (kind.equals("western")) {
                line(c, 2, 17, 6, 16, 8, 5, 12, 8, 16, 5, 18, 16, 22, 17);
                c.drawArc(2, 12, 22, 22, 0, 180, false, paint);
            } else if (kind.equals("talk")) {
                c.drawRoundRect(8, 2, 16, 15, 4, 4, paint);
                c.drawArc(5, 7, 19, 19, 0, 180, false, paint);
                line(c, 12, 19, 12, 22, 7, 22, 17, 22);
            } else if (kind.equals("reality")) {
                line(c, 2, 12, 7, 6, 17, 6, 22, 12, 17, 18, 7, 18, 2, 12);
                c.drawCircle(12, 12, 3, paint);
            } else if (kind.equals("soap")) {
                // v2: a heart in a speech bubble (the bubbles read as washing-up).
                line(c, 4, 4, 20, 4, 21, 5, 21, 15, 20, 16, 10, 16, 5, 20, 5, 16, 4, 16, 3, 15, 3, 5, 4, 4);
                heart(c, 12, 13.5f, 4);
            } else if (kind.equals("news")) {
                c.drawRect(2, 3, 22, 21, paint);
                c.drawRect(5, 7, 11, 13, paint);
                line(c, 14, 7, 19, 7);
                line(c, 14, 12, 19, 12);
                line(c, 5, 17, 19, 17);
            } else if (!drawMore(c)) {
                c.drawRoundRect(2, 5, 22, 19, 2, 2, paint);
                line(c, 9, 8, 16, 12, 9, 16, 9, 8);
            }
            c.restore();
        }

        private void heart(Canvas c, float cx, float bottom, float size) {
            Path heart = new Path();
            heart.moveTo(cx, bottom);
            heart.cubicTo(cx - size * 1.6f, bottom - size, cx - size, bottom - size * 2.2f, cx, bottom - size * 1.3f);
            heart.cubicTo(cx + size, bottom - size * 2.2f, cx + size * 1.6f, bottom - size, cx, bottom);
            c.drawPath(heart, paint);
        }

        /** Genres added in icon set v2 (approved 4 Nov 2026, customise-home/06). */
        private boolean drawMore(Canvas c) {
            switch (kind) {
            case "tv movie":
                c.drawRoundRect(2, 4, 22, 17, 2, 2, paint);
                line(c, 8, 21, 16, 21);
                line(c, 10, 7.5f, 15, 10.5f, 10, 13.5f, 10, 7.5f);
                return true;
            case "anime":
                line(c, 12, 2, 14.2f, 8.3f, 21, 9, 15.7f, 13, 17.6f, 19.6f, 12, 16, 6.4f, 19.6f, 8.3f, 13, 3, 9,
                     9.8f, 8.3f, 12, 2);
                c.drawPoint(10, 10.5f, paint);
                c.drawPoint(14, 10.5f, paint);
                return true;
            case "biography":
                c.drawRoundRect(3, 4, 21, 20, 2, 2, paint);
                c.drawCircle(8.5f, 10, 2.2f, paint);
                line(c, 5, 16.5f, 6, 14.5f, 11, 14.5f, 12, 16.5f);
                line(c, 14, 9, 18, 9);
                line(c, 14, 12, 18, 12);
                line(c, 14, 15, 17, 15);
                return true;
            case "disaster":
                line(c, 2, 21, 9, 10, 15, 10, 22, 21, 2, 21);
                line(c, 9, 10, 10.5f, 12.5f, 12, 10.5f, 13.5f, 12.5f, 15, 10);
                line(c, 12, 7, 12, 3);
                line(c, 8.5f, 7.5f, 6.5f, 4.5f);
                line(c, 15.5f, 7.5f, 17.5f, 4.5f);
                return true;
            case "game show":
                c.drawRoundRect(4, 13, 20, 21, 2, 2, paint);
                c.drawArc(7, 6, 17, 16, 180, 180, false, paint);
                line(c, 12, 2, 12, 4);
                line(c, 5, 5, 6.5f, 6.5f);
                line(c, 19, 5, 17.5f, 6.5f);
                return true;
            case "holiday":
                c.drawRoundRect(3, 9, 21, 21, 1, 1, paint);
                line(c, 2, 9, 22, 9);
                line(c, 12, 9, 12, 21);
                c.drawOval(7, 4.5f, 12, 8.5f, paint);
                c.drawOval(12, 4.5f, 17, 8.5f, paint);
                return true;
            case "martial arts":
                line(c, 2, 12, 22, 12);
                line(c, 9, 12, 6, 21);
                line(c, 15, 12, 18, 21);
                c.drawArc(9, 8.5f, 15, 15.5f, 180, 180, false, paint);
                return true;
            case "mini-series":
                c.drawRoundRect(2, 8, 16, 19, 1.5f, 1.5f, paint);
                line(c, 5, 5, 18, 5, 18, 16);
                line(c, 8, 2, 21, 2, 21, 13);
                return true;
            case "musical":
                c.drawArc(-3, 3, 6, 21, -90, 180, false, paint);
                c.drawArc(18, 3, 27, 21, 90, 180, false, paint);
                line(c, 3, 3, 21, 3);
                line(c, 12, 8, 12, 15);
                c.drawCircle(10.2f, 15.6f, 1.8f, paint);
                line(c, 12, 8, 15, 9);
                return true;
            case "short":
                c.drawCircle(12, 13, 8, paint);
                line(c, 12, 13, 12, 9);
                line(c, 10, 2, 14, 2);
                line(c, 12, 2, 12, 5);
                line(c, 18.5f, 6.5f, 20, 5);
                return true;
            case "sport":
            case "sports":
                c.drawCircle(12, 12, 9.5f, paint);
                line(c, 12, 7.5f, 16.3f, 10.6f, 14.6f, 15.6f, 9.4f, 15.6f, 7.7f, 10.6f, 12, 7.5f);
                line(c, 12, 2.5f, 12, 7.5f);
                line(c, 21.4f, 10, 16.3f, 10.6f);
                line(c, 17.8f, 19.5f, 14.6f, 15.6f);
                line(c, 6.2f, 19.5f, 9.4f, 15.6f);
                line(c, 2.6f, 10, 7.7f, 10.6f);
                return true;
            case "superhero":
                Path mask = new Path();
                mask.moveTo(2, 9);
                mask.cubicTo(5, 7, 8, 7, 12, 9);
                mask.cubicTo(16, 7, 19, 7, 22, 9);
                mask.cubicTo(21.5f, 14, 19, 16, 16, 16);
                mask.cubicTo(14, 16, 13, 14.5f, 12, 13);
                mask.cubicTo(11, 14.5f, 10, 16, 8, 16);
                mask.cubicTo(5, 16, 2.5f, 14, 2, 9);
                c.drawPath(mask, paint);
                c.drawCircle(7.5f, 11, 1.3f, paint);
                c.drawCircle(16.5f, 11, 1.3f, paint);
                return true;
            default:
                return false;
            }
        }

        @Override
        public void setAlpha(int a) {
            paint.setAlpha(a);
        }

        @Override
        public void setColorFilter(ColorFilter f) {
            paint.setColorFilter(f);
        }

        @Override
        public int getOpacity() {
            return PixelFormat.TRANSLUCENT;
        }
    }
}
