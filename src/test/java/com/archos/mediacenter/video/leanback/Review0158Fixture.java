package com.archos.mediacenter.video.leanback;

import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Looper;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;
import com.archos.mediacenter.video.browser.adapters.object.Episode;
import com.archos.mediacenter.video.browser.adapters.object.Movie;
import com.archos.mediacenter.video.browser.adapters.object.Tvshow;
import com.archos.mediacenter.video.leanback.PreviewLibraryLoader.Entry;
import com.archos.mediacenter.video.leanback.PreviewLibraryLoader.Snapshot;
import java.io.File;
import java.io.FileOutputStream;
import java.time.Duration;
import org.robolectric.Shadows;
import org.robolectric.android.controller.ActivityController;
import org.robolectric.shadows.ShadowDialog;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * 0.158 test data and render helpers (Definition of done 4): long names, films started and watched, shows with one
 * and several seasons, a show half-way through, sizes and resolutions that differ. Names are made up.
 */
final class Review0158Fixture
{
    static final int WIDTH = 1920;
    static final int HEIGHT = 1080;

    private Review0158Fixture()
    {
    }

    static final String[] FILMS = {
        "Conclave", "Anora", "Flow", "Nosferatu",
        "The Extraordinarily Long Title of a Film Nobody Can Remember in Full", "Dune: Part Two",
        "Perfect Days", "The Zone of Interest", "Past Lives", "Aftersun", "Oppenheimer", "Poor Things",
        "Evil Does Not Exist", "Fallen Leaves"
    };

    static final String[] SHOWS = {
        "Slow Horses", "Tidelands of the Northern Coast: The Lighthouse Keepers' Years", "Shogun", "The Bear",
        "Severance", "Fargo", "Mr Bates vs The Post Office", "Ripley", "Baby Reindeer"
    };

    static Snapshot snapshot()
    {
        Snapshot s = new Snapshot();
        String[] genres = {"Drama", "Animation", "Horror", "Science Fiction", "Thriller", "Comedy", "Romance"};
        String[] resolution = {"4K", "1080p", "720p"};
        for (int i = 0; i < FILMS.length; i++)
        {
            int resume = i == 1 ? 42 * 60000 : 0;
            boolean watched = i == 2 || i == 9;
            Movie movie = new Movie(i + 1, "/storage/Backup #2/Movies/" + FILMS[i] + " (2024).mkv", FILMS[i], i + 1,
                                    "A film about something that matters a great deal to the people in it.",
                                    2018 + i % 7, 7, "", null, (95 + i * 7) * 60000, resume, 0, 0, watched, false,
                                    false, false, i + 1,
                                    i + 1, i % 3 == 0 ? 3840 : 1920, i % 3 == 0 ? 2160 : 1080, "Atmos", null, null,
                                    null, 0, 1, 1000, 0);
            // Added over the last few weeks (seconds, as the library stores them).
            Entry entry = new Entry(movie, 1_778_000_000L - i * 260_000L, 0, genres[i % genres.length]);
            entry.bytes = (2L + i * 3L) * 1024L * 1024L * 1024L;
            entry.runtime = (95 + i * 7) * 60000L;
            entry.resolution = resolution[i % 3];
            entry.audio = i % 2 == 0 ? "Dolby Atmos" : "5.1";
            entry.releaseDate = (2018 + i % 7) + "-0" + (1 + i % 9) + "-14";
            entry.started = resume > 0;
            s.movies.add(entry);
            s.recent.add(entry);
            s.technical.add(entry);
            if (resume > 0)
            {
                s.continuingMovies.add(entry);
            }
        }
        for (int i = 0; i < SHOWS.length; i++)
        {
            int seasons = i == 2 ? 1 : 2 + i % 3;
            int episodes = seasons * (i == 2 ? 10 : 8 + i % 3);
            Tvshow show = new Tvshow(100 + i, SHOWS[i], null, seasons, episodes, i == 0 ? 12 : 0,
                                     "/storage/Backup #2/TV Shows/" + SHOWS[i]);
            Entry entry = new Entry(show, 1_777_000_000L - i * 300_000L, 100 + i, genres[(i + 3) % genres.length]);
            entry.seasons = seasons;
            entry.episodes = episodes;
            entry.bytes = episodes * (1L + i % 2) * 1024L * 1024L * 1024L;
            entry.resolution = resolution[i % 3];
            entry.started = i == 0;
            s.shows.add(entry);
            s.recent.add(entry);
        }
        s.episodes.add(episode(100, SHOWS[0], 2, 3, 16 * 60000));
        s.continuingShows.add(s.episodes.get(0));
        return s;
    }

    static Entry episode(long show, String name, int season, int number, int resume)
    {
        Entry e = new Entry(new Episode(show * 1000 + season * 100 + number, show, season, number, "Episode " + number,
                                        0, 0, "", "", name, "/storage/Backup #2/TV Shows/" + name + "/ep.mkv", null,
                                        null, 2700000, resume, 0, 0, false, false, false, false, 1, 100, 1920, 1080,
                                        null, null, null, null, 0, 1, 1000),
                            1, show, "Thriller");
        e.started = true;
        return e;
    }

    static void press(ActivityController<?> host, int key, View root)
    {
        host.get().dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, key));
        host.get().dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_UP, key));
        settle(root);
    }

    static void settle(View v)
    {
        for (int i = 0; i < 4; i++)
        {
            v.measure(View.MeasureSpec.makeMeasureSpec(WIDTH, View.MeasureSpec.EXACTLY),
                      View.MeasureSpec.makeMeasureSpec(HEIGHT, View.MeasureSpec.EXACTLY));
            v.layout(0, 0, WIDTH, HEIGHT);
            Shadows.shadowOf(Looper.getMainLooper()).idleFor(Duration.ofMillis(120));
        }
    }

    static View byText(View view, String words)
    {
        if (view instanceof TextView && words.equals(((TextView) view).getText().toString()) && view.isFocusable())
        {
            return view;
        }
        if (view instanceof ViewGroup)
        {
            ViewGroup group = (ViewGroup) view;
            for (int i = 0; i < group.getChildCount(); i++)
            {
                View match = byText(group.getChildAt(i), words);
                if (match != null)
                {
                    return match;
                }
            }
        }
        return null;
    }

    static View byTextStart(View view, String words)
    {
        if (view instanceof TextView && ((TextView) view).getText().toString().startsWith(words)
            && view.isFocusable())
        {
            return view;
        }
        if (view instanceof ViewGroup)
        {
            ViewGroup group = (ViewGroup) view;
            for (int i = 0; i < group.getChildCount(); i++)
            {
                View match = byTextStart(group.getChildAt(i), words);
                if (match != null)
                {
                    return match;
                }
            }
        }
        return null;
    }

    /** Any view (focusable or not) whose text contains the words. */
    static View anyText(View view, String words)
    {
        if (view instanceof TextView && ((TextView) view).getText().toString().contains(words)
            && view.isShown())
        {
            return view;
        }
        if (view instanceof ViewGroup)
        {
            ViewGroup group = (ViewGroup) view;
            for (int i = 0; i < group.getChildCount(); i++)
            {
                View match = anyText(group.getChildAt(i), words);
                if (match != null)
                {
                    return match;
                }
            }
        }
        return null;
    }

    static View byDescription(View view, String start)
    {
        CharSequence description = view.getContentDescription();
        if (description != null && description.toString().startsWith(start) && view.isFocusable())
        {
            return view;
        }
        if (view instanceof ViewGroup)
        {
            ViewGroup group = (ViewGroup) view;
            for (int i = 0; i < group.getChildCount(); i++)
            {
                View match = byDescription(group.getChildAt(i), start);
                if (match != null)
                {
                    return match;
                }
            }
        }
        return null;
    }

    static boolean inside(View ancestor, View view)
    {
        while (view != null)
        {
            if (view == ancestor)
            {
                return true;
            }
            view = view.getParent() instanceof View ? (View) view.getParent() : null;
        }
        return false;
    }

    static void save(View v, String name) throws Exception
    {
        Bitmap bitmap = Bitmap.createBitmap(WIDTH, HEIGHT, Bitmap.Config.ARGB_8888);
        v.draw(new Canvas(bitmap));
        write(bitmap, name);
    }

    /** The page with the latest dialog (menu or panel) drawn where its window sits, as on the screen. */
    static void saveWithDialog(View v, String name) throws Exception
    {
        Bitmap bitmap = Bitmap.createBitmap(WIDTH, HEIGHT, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        v.draw(canvas);
        Dialog menu = ShadowDialog.getLatestDialog();
        assertNotNull("No menu or panel opened", menu);
        assertTrue("The menu or panel is not showing", menu.isShowing());
        View decor = menu.getWindow().getDecorView();
        WindowManager.LayoutParams at = menu.getWindow().getAttributes();
        decor.measure(at.width > 0 ? View.MeasureSpec.makeMeasureSpec(at.width, View.MeasureSpec.EXACTLY)
                          : View.MeasureSpec.makeMeasureSpec(WIDTH, View.MeasureSpec.AT_MOST),
                      at.height > 0 ? View.MeasureSpec.makeMeasureSpec(at.height, View.MeasureSpec.EXACTLY)
                          : View.MeasureSpec.makeMeasureSpec(HEIGHT, View.MeasureSpec.AT_MOST));
        int width = decor.getMeasuredWidth();
        int height = decor.getMeasuredHeight();
        decor.layout(0, 0, width, height);
        int horizontal = at.gravity & android.view.Gravity.HORIZONTAL_GRAVITY_MASK;
        int vertical = at.gravity & android.view.Gravity.VERTICAL_GRAVITY_MASK;
        int x = horizontal == android.view.Gravity.LEFT ? at.x
            : horizontal == android.view.Gravity.RIGHT ? WIDTH - width - at.x : (WIDTH - width) / 2;
        int y = vertical == android.view.Gravity.TOP ? at.y
            : vertical == android.view.Gravity.BOTTOM ? HEIGHT - height - at.y : (HEIGHT - height) / 2;
        canvas.save();
        canvas.translate(x, y);
        decor.draw(canvas);
        canvas.restore();
        write(bitmap, name);
    }

    static void dismissDialog(View root)
    {
        Dialog menu = ShadowDialog.getLatestDialog();
        if (menu != null && menu.isShowing())
        {
            menu.dismiss();
        }
        settle(root);
    }

    private static void write(Bitmap bitmap, String name) throws Exception
    {
        File file = new File("build/reports/preview-ui/" + name + ".png");
        file.getParentFile().mkdirs();
        try (FileOutputStream out = new FileOutputStream(file))
        {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
        }
    }
}
