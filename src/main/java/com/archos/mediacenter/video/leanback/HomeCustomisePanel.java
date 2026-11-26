package com.archos.mediacenter.video.leanback;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.TextView;
import com.archos.mediacenter.video.leanback.PreviewHomeRows.Row;
import com.archos.mediacenter.video.leanback.PreviewLibraryLoader.Entry;
import com.archos.mediacenter.video.leanback.PreviewLibraryLoader.Snapshot;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.UUID;

/**
 * Customise Home (page sheet HOME.md; mock-ups `home-v2/03`, `05`, `06`, `07`): a floating side panel on the right.
 * Your Rows first (each opens its settings with ›), Add a row, then Built-In Rows with a switch each. Changes apply
 * live; Back closes. A row's settings open on top of Customise Home (the panel behind darkened).
 */
final class HomeCustomisePanel
{
    /** Built-in rows in the sheet's order, with their soft second line. */
    static final String[][] BUILT_IN = {
        {"continue", "Continue Watching", "Unfinished movies and next episodes", "history"},
        {"recent", "Recently Added", "Newest in your library", "date added"},
        {"watchnext", "Watch Next", "Titles you saved for later", "bookmark"},
        {"trending", "Trending on Trakt", "In your library", "trending"},
        {"popular", "Popular on Trakt", "In your library", "popular"},
        {"watched", "Recently Watched", "What you finished last", "check"},
        {"similar", "Because You Watched", "Similar titles in your library", "recommendation"},
    };
    static final int[] MAXIMUM = {10, 15, 20, 25, 30};
    /** The row you were on last time, so reopening Customise Home returns there (focus audit F17). */
    static final String LAST_ROW = "supernova_customise_home_last_row";
    static final String[] SORTS = {"title", "year", "added"};

    private final Context context;
    private final PreviewHomeRows model;
    private final Runnable changed;
    private final Snapshot snapshot;
    private FloatingPanel panel;

    private HomeCustomisePanel(Context context, PreviewHomeRows model, Runnable changed)
    {
        this.context = context;
        this.model = model;
        this.changed = changed;
        Snapshot cached = PreviewLibraryLoader.memoryCache();
        snapshot = cached == null ? new Snapshot() : cached;
    }

    static Dialog show(Context context, PreviewHomeRows model, Runnable changed)
    {
        HomeCustomisePanel built = new HomeCustomisePanel(context, model, changed);
        built.build(null);
        return built.panel.dialog;
    }

    /**
     * Customise Home (owner, 21 Nov; `home-v3/tv/07`, `08`): **Shown on Home** in Home's order, **Hidden** A–Z, then
     * **Add a row**. The focused row shows Move · Hide (your own rows also Edit); a hidden row shows Show. Move lifts
     * the row: Up / Down, Home follows live; OK or Back puts it down.
     */
    private void build(String focusTag)
    {
        panel = new FloatingPanel(context, "Customise Home");
        panel.heading("Shown on Home");
        for (Row row : new ArrayList<>(model.rows))
        {
            if (!row.visible || !known(row))
            {
                continue;
            }
            String[] buttons = row.custom() ? new String[] {"Move", "Hide", "Edit"} : new String[] {"Move", "Hide"};
            View[] line = {null};
            line[0] = panel.buttons(tag(row), icon(row), name(row), second(row), buttons, n -> {
                if (n == 0)
                {
                    panel.pickUp(line[0], (v, direction) -> move(row, direction));
                }
                else if (n == 1)
                {
                    row.visible = false;
                    save();
                    rebuild(neighbour(row));
                }
                else
                {
                    rowSettings(row, false);
                }
            });
        }
        List<Row> hidden = new ArrayList<>();
        for (Row row : model.rows)
        {
            if (!row.visible && known(row))
            {
                hidden.add(row);
            }
        }
        hidden.sort((a, b) -> name(a).compareToIgnoreCase(name(b)));
        if (!hidden.isEmpty())
        {
            panel.heading("Hidden");
        }
        for (Row row : hidden)
        {
            panel.buttons(tag(row), icon(row), name(row), second(row), new String[] {"Show"}, n -> {
                row.visible = true;
                save();
                rebuild(tag(row));
            });
        }
        panel.line();
        panel.action("home.customise.add", "plus", "Add a row", this::addRow);
        android.content.SharedPreferences prefs =
            androidx.preference.PreferenceManager.getDefaultSharedPreferences(context);
        String remembered = focusTag != null ? focusTag : prefs.getString(LAST_ROW, null);
        for (int i = 0; i < panel.body.getChildCount(); i++)
        {
            View row = panel.body.getChildAt(i);
            if (row.isFocusable() && row.getTag() instanceof String)
            {
                View.OnFocusChangeListener look = row.getOnFocusChangeListener();
                row.setOnFocusChangeListener((v, has) -> {
                    look.onFocusChange(v, has);
                    if (has)
                    {
                        prefs.edit().putString(LAST_ROW, (String) v.getTag()).apply();
                    }
                });
            }
        }
        panel.show(remembered == null ? null : panel.body.findViewWithTag(remembered));
    }

    private void rebuild(String focusTag)
    {
        panel.onClosed(() -> {
        });
        panel.dismiss();
        build(focusTag);
    }

    /** Built-in rows Supernova knows, and every row of your own. */
    private static boolean known(Row row)
    {
        return row.custom() || builtIn(row.id) != null;
    }

    static String tag(Row row)
    {
        return row.custom() ? "home.customise.row:" + row.id : "home.customise.builtin:" + row.id;
    }

    private static String name(Row row)
    {
        String[] builtIn = builtIn(row.id);
        return builtIn != null && !row.custom() ? builtIn[1] : row.name;
    }

    private static String icon(Row row)
    {
        String[] builtIn = builtIn(row.id);
        return builtIn != null && !row.custom() ? builtIn[3] : PreviewHomeRows.iconName(row);
    }

    private static String second(Row row)
    {
        String[] builtIn = builtIn(row.id);
        return builtIn != null && !row.custom() ? builtIn[2] : summary(row);
    }

    /** After hiding a row, focus goes to the next shown row (or the one before, or Add a row). */
    private String neighbour(Row hidden)
    {
        int at = model.rows.indexOf(hidden);
        for (int i = at + 1; i < model.rows.size(); i++)
        {
            if (model.rows.get(i).visible && known(model.rows.get(i)))
            {
                return tag(model.rows.get(i));
            }
        }
        for (int i = at - 1; i >= 0; i--)
        {
            if (model.rows.get(i).visible && known(model.rows.get(i)))
            {
                return tag(model.rows.get(i));
            }
        }
        return "home.customise.add";
    }

    private static String[] builtIn(String id)
    {
        for (String[] row : BUILT_IN)
        {
            if (row[0].equals(id))
            {
                return row;
            }
        }
        return null;
    }

    /** Swaps a shown row with the next shown row above or below it (Home's order, live). */
    private boolean move(Row row, int direction)
    {
        int at = model.rows.indexOf(row);
        for (int i = at + direction; i >= 0 && i < model.rows.size(); i += direction)
        {
            Row other = model.rows.get(i);
            if (!other.visible || !known(other))
            {
                continue;
            }
            model.rows.set(i, row);
            model.rows.set(at, other);
            save();
            return true;
        }
        return false;
    }

    private Row find(String id)
    {
        for (Row row : model.rows)
        {
            if (row.id.equals(id))
            {
                return row;
            }
        }
        return null;
    }

    private void save()
    {
        model.save();
        changed.run();
    }

    /** "Documentary, Nature · newest first" (mock-up `03`). */
    static String summary(Row row)
    {
        Set<String> genres = PreviewGenres.parse(row.genreRule);
        String what = genres.isEmpty() ? (row.movies && row.tv ? "Movies and TV Shows" : row.movies ? "Movies"
                                                                                        : "TV Shows")
                                       : String.join(", ", genres);
        String order = "title".equals(row.sort) ? (row.descending ? "title Z–A" : "title A–Z")
                                                : row.descending ? "newest first" : "oldest first";
        return what + " · " + order;
    }

    private void addRow()
    {
        Row row = new Row("custom:" + UUID.randomUUID(), "New row");
        row.dynamic = true;
        row.maximum = 15;
        rowSettings(row, true);
    }

    /** A row's settings (`05`, `06`), on top of Customise Home; changes apply live. */
    private void rowSettings(Row row, boolean adding)
    {
        FloatingPanel settings = new FloatingPanel(context, row.name);
        boolean[] added = {!adding};
        Runnable apply = () -> {
            if (!added[0])
            {
                model.rows.add(row);
                added[0] = true;
            }
            save();
        };
        TextView[] name = {null};
        name[0] = settings.opener("home.row.name", "title", "Row name", row.name, () ->
            PreviewTextInput.showLive(context, "Row name", row.name, settings.leftEdge() - settings.dp(12), typed -> {
                String value = typed.trim();
                if (value.isEmpty() || value.length() > 40)
                {
                    return;
                }
                row.name = value;
                name[0].setText(value);
                apply.run();
            }));
        TextView[] icon = {null};
        icon[0] = settings.opener("home.row.icon", PreviewHomeRows.iconName(row), "Icon",
                                  PreviewGenres.known(row.icon) ? row.icon : "Automatic", () -> chooseIcon(row, () -> {
                                      icon[0].setText(PreviewGenres.known(row.icon) ? row.icon : "Automatic");
                                      apply.run();
                                  }));
        int includes = row.movies && row.tv ? 2 : row.tv ? 1 : 0;
        settings.chips("home.row.includes", "tv", "Includes", new String[] {"Movies", "TV Shows", "Both"}, includes,
                       n -> {
                           row.movies = n != 1;
                           row.tv = n != 0;
                           apply.run();
                       });
        TextView[] genres = {null};
        genres[0] = settings.opener("home.row.genres", "genre", "Genres", genreSummary(row), () -> chooseGenres(row,
            () -> {
                genres[0].setText(genreSummary(row));
                apply.run();
            }));
        TextView[] years = {null};
        years[0] = settings.opener("home.row.years", "year", "Years", yearSummary(row), () -> chooseYears(row, () -> {
            years[0].setText(yearSummary(row));
            apply.run();
        }));
        int rating = row.rating >= 9 ? 4 : row.rating >= 8 ? 3 : row.rating >= 7 ? 2 : row.rating >= 6 ? 1 : 0;
        settings.chips("home.row.rating", "star", "Minimum rating", LibraryCustomisePanel.RATINGS, rating, n -> {
            row.rating = n == 0 ? 0 : 5 + n;
            apply.run();
        });
        FloatingPanel.ChipRow[] order = {null};
        TextView[] sortValue = {null};
        // Sort by opens the Sort list (owner, 21 Nov; home-v3/tv/12), a tick on the current one.
        sortValue[0] = settings.opener("home.row.sort", "sort", "Sort by", SORT_WORDS[sortIndex(row)], () -> {
            View anchor = (View) sortValue[0].getParent();
            PreviewDialog.menu(context, anchor, "Sort by", SORT_WORDS, new String[] {"title", "date", "added"},
                               sortIndex(row), n -> {
                                   row.sort = SORTS[n];
                                   row.descending = !"title".equals(row.sort);
                                   sortValue[0].setText(SORT_WORDS[n]);
                                   order[0].setOptions(orderWords(row.sort));
                                   order[0].choose(0, false);
                                   apply.run();
                               });
        });
        order[0] = settings.chips("home.row.order", row.descending ? "descending" : "ascending", "Order",
                                  orderWords(row.sort), "title".equals(row.sort) == row.descending ? 1 : 0, n -> {
                                      boolean first = n == 0;
                                      row.descending = "title".equals(row.sort) ? !first : first;
                                      apply.run();
                                  });
        int maximum = 0;
        for (int i = 0; i < MAXIMUM.length; i++)
        {
            if (MAXIMUM[i] <= Math.max(10, row.maximum))
            {
                maximum = i;
            }
        }
        String[] counts = new String[MAXIMUM.length];
        for (int i = 0; i < MAXIMUM.length; i++)
        {
            counts[i] = String.valueOf(MAXIMUM[i]);
        }
        settings.chips("home.row.maximum", "list", "Maximum items", counts, maximum, n -> {
            row.maximum = MAXIMUM[n];
            apply.run();
        });
        settings.toggle("home.row.automatic", "scan", "Add new titles automatically", null, row.dynamic, on -> {
            model.setDynamic(row, on, snapshot);
            apply.run();
        });
        if (!adding)
        {
            settings.line();
            // Delete this row warns beside itself and uses Undo, no pop-up (owner, 21 Nov; home-v3/tv/12).
            View[] delete = {null};
            delete[0] = settings.buttons("home.row.delete", "delete", "Delete this row",
                                         "Your videos are never deleted.", new String[] {"Keep row", "Delete row"},
                                         n -> {
                                             if (n == 0)
                                             {
                                                 return;
                                             }
                                             int at = model.rows.indexOf(row);
                                             model.rows.remove(row);
                                             save();
                                             settings.onClosed(() -> {
                                             });
                                             settings.dismiss();
                                             rebuild("home.customise.add");
                                             UndoNotice.show(context, row.name + " deleted", () -> {
                                                 model.rows.add(Math.min(at, model.rows.size()), row);
                                                 save();
                                                 rebuild(tag(row));
                                             });
                                         });
        }
        settings.onClosed(() -> {
            if (panel != null && panel.dialog.isShowing())
            {
                panel.dismiss();
                build(added[0] ? "home.customise.row:" + row.id : "home.customise.add");
            }
        });
        settings.show(null);
    }

    static final String[] SORT_WORDS = {"Title", "Release date", "Date added"};

    private static int sortIndex(Row row)
    {
        return Math.max(0, Arrays.asList(SORTS).indexOf(row.sort));
    }

    static String[] orderWords(String sort)
    {
        return "title".equals(sort) ? new String[] {"A → Z", "Z → A"} : new String[] {"Newest", "Oldest"};
    }

    static String genreSummary(Row row)
    {
        Set<String> genres = PreviewGenres.parse(row.genreRule);
        return genres.isEmpty() ? "Any" : String.join(", ", genres);
    }

    static String yearSummary(Row row)
    {
        if (row.yearFrom <= 0 && row.yearTo <= 0)
        {
            return "Any";
        }
        return (row.yearFrom > 0 ? String.valueOf(row.yearFrom) : "Any") + " – "
            + (row.yearTo > 0 ? String.valueOf(row.yearTo) : "Any");
    }

    /**
     * The icon picker (`07`): every genre icon with its name, a tick on the current one. Automatic: the first genre
     * chosen gives its icon; no genre, a plain list icon.
     */
    private void chooseIcon(Row row, Runnable done)
    {
        HomeIconPicker.show(context, PreviewGenres.known(row.icon) ? row.icon : "", icon -> {
            row.icon = icon;
            done.run();
        });
    }

    private void chooseGenres(Row row, Runnable done)
    {
        Map<String, Integer> counts = new TreeMap<>();
        List<Entry> all = new ArrayList<>(snapshot.movies);
        all.addAll(snapshot.shows);
        for (Entry entry : all)
        {
            for (String genre : PreviewGenres.parse(entry.genres))
            {
                counts.merge(genre, 1, Integer::sum);
            }
        }
        Set<String> chosen = new LinkedHashSet<>(PreviewGenres.parse(row.genreRule));
        for (String genre : chosen)
        {
            counts.putIfAbsent(genre, 0);
        }
        List<String> names = new ArrayList<>(counts.keySet());
        List<String> labels = new ArrayList<>();
        List<String> icons = new ArrayList<>();
        labels.add(row.matchAll ? "Match: All" : "Match: Any");
        icons.add("chevrons");
        for (String genre : names)
        {
            int count = counts.get(genre);
            labels.add(genre + " · " + count + (count == 1 ? " title" : " titles"));
            icons.add(genre);
        }
        Set<Integer> ticked = new HashSet<>();
        for (int i = 0; i < names.size(); i++)
        {
            if (chosen.contains(names.get(i)))
            {
                ticked.add(i + 1);
            }
        }
        Dialog[] list = {null};
        list[0] = PreviewDialog.menuBelow(context, null, "Genres", labels.toArray(new String[0]),
                                          icons.toArray(new String[0]), ticked, 0, false, true, n -> {
                                              if (n == 0)
                                              {
                                                  row.matchAll = !row.matchAll;
                                                  PreviewDialog.updateLabel(list[0], 0, row.matchAll ? "Match: All"
                                                                                                     : "Match: Any");
                                              }
                                              else if (!chosen.add(names.get(n - 1)))
                                              {
                                                  chosen.remove(names.get(n - 1));
                                              }
                                              Set<Integer> now = new HashSet<>();
                                              for (int i = 0; i < names.size(); i++)
                                              {
                                                  if (chosen.contains(names.get(i)))
                                                  {
                                                      now.add(i + 1);
                                                  }
                                              }
                                              PreviewDialog.updateChecks(list[0], now);
                                              row.genreRule = String.join("|", chosen);
                                              done.run();
                                          });
    }

    private void chooseYears(Row row, Runnable done)
    {
        List<Integer> present = LibraryFilters.years(snapshot);
        List<String> labels = new ArrayList<>();
        List<int[]> ranges = new ArrayList<>();
        LibraryFilters.quickYears(present, labels, ranges);
        String[] icons = new String[labels.size()];
        Set<Integer> ticked = new HashSet<>();
        for (int i = 0; i < labels.size(); i++)
        {
            int[] range = ranges.get(i);
            icons[i] = range == null ? null : "year";
            if (range != null && range[0] == row.yearFrom && range[1] == row.yearTo)
            {
                ticked.add(i);
            }
        }
        PreviewDialog.menuBelow(context, null, "Years", labels.toArray(new String[0]), icons, ticked, 1, true, n -> {
            int[] range = ranges.get(n);
            if (range == null)
            {
                return;
            }
            row.yearFrom = range[0];
            row.yearTo = range[1];
            done.run();
        });
    }
}
