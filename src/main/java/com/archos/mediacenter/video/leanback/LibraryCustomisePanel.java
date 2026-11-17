package com.archos.mediacenter.video.leanback;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.TextView;
import com.archos.mediacenter.video.leanback.PreviewLibraryLoader.Entry;
import com.archos.mediacenter.video.leanback.PreviewLibraryLoader.Snapshot;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * Customise page (page sheet LIBRARY.md; mock-ups `library-pages/32`, `29`): a side panel floating on the right, the
 * page behind updating live, no Save button, Back closes. What To Show (your own page only) · How To Show It (how the
 * page opens) · Delete page at the foot (own page only), apart by a plain line.
 */
final class LibraryCustomisePanel
{
    /** What the panel changes. Movies and TV Shows have no page of their own ({@link #page()} is null). */
    interface Host
    {
        CustomLibraryPage page();

        Snapshot snapshot();

        /** 0 All · 1 Unwatched · 2 In progress · 3 Watched. */
        int show();

        void setShow(int show);

        boolean list();

        void setList(boolean list);

        /** {@link LibraryPageModel#TITLE}, {@link LibraryPageModel#RELEASE} or {@link LibraryPageModel#ADDED}. */
        String sort();

        void setSort(String key);

        boolean ascending();

        void setAscending(boolean ascending);

        /** The own page's settings changed: save and show it. */
        void pageChanged();

        void deletePage();
    }

    static final String[] SHOW = {"All", "Unwatched", "In progress", "Watched"};
    static final String[] SORTS = {LibraryPageModel.TITLE, LibraryPageModel.RELEASE, LibraryPageModel.ADDED};
    static final String[] RATINGS = {"Any", "6+", "7+", "8+", "9+"};

    private final Context context;
    private final Host host;
    private final FloatingPanel panel;
    private FloatingPanel.ChipRow order;

    private LibraryCustomisePanel(Context context, Host host)
    {
        this.context = context;
        this.host = host;
        panel = new FloatingPanel(context, "Customise page");
    }

    static Dialog show(Context context, Host host)
    {
        LibraryCustomisePanel built = new LibraryCustomisePanel(context, host);
        built.build();
        built.panel.show(null);
        return built.panel.dialog;
    }

    private void build()
    {
        CustomLibraryPage page = host.page();
        if (page != null)
        {
            whatToShow(page);
        }
        panel.heading("How To Show It");
        panel.chips("customise.show", "eye", "Show", SHOW, host.show(), host::setShow);
        panel.chips("customise.view", "grid", "View", new String[] {"Grid", "List"}, host.list() ? 1 : 0,
                    n -> host.setList(n == 1));
        String[] sortLabels = {"Title", page == null || "movies".equals(page.type) ? "Release date"
                                   : "tv".equals(page.type) ? "Air date" : "Release date", "Date added"};
        int sort = indexOf(SORTS, host.sort());
        panel.chips("customise.sort", "sort", "Sort by", sortLabels, Math.max(0, sort), n -> {
            String key = SORTS[n];
            host.setSort(key);
            host.setAscending(LibraryPageModel.startsAscending(key));
            order.setOptions(orderWords(key));
            order.choose(host.ascending() == LibraryPageModel.startsAscending(key) ? 0 : 1, false);
        });
        String key = sort < 0 ? LibraryPageModel.TITLE : host.sort();
        order = panel.chips("customise.order", LibraryPageModel.orderIcon(host.ascending()), "Order",
                            orderWords(key), host.ascending() == LibraryPageModel.startsAscending(key) ? 0 : 1,
                            n -> host.setAscending(n == 0 ? LibraryPageModel.startsAscending(host.sort())
                                                          : !LibraryPageModel.startsAscending(host.sort())));
        if (page != null)
        {
            panel.line();
            panel.action("customise.delete", "delete", "Delete page", host::deletePage);
        }
    }

    /** "Newest" · "Oldest" for dates, "A → Z" · "Z → A" for titles (the first is where the sort starts). */
    static String[] orderWords(String key)
    {
        boolean up = LibraryPageModel.startsAscending(key);
        String first = LibraryPageModel.orderLabel(key, up).replace(" first", "");
        String second = LibraryPageModel.orderLabel(key, !up).replace(" first", "");
        return new String[] {first, second};
    }

    private static int indexOf(String[] values, String value)
    {
        for (int i = 0; i < values.length; i++)
        {
            if (values[i].equals(value))
            {
                return i;
            }
        }
        return -1;
    }

    private void whatToShow(CustomLibraryPage page)
    {
        panel.heading("What To Show");
        TextView[] name = {null};
        name[0] = panel.opener("customise.name", "title", "Page name", page.name, () -> {
            // The one keyboard in its own panel beside this one (RULES §15); the text appears in the row.
            PreviewTextInput.showLive(context, "Page name", page.name, panel.leftEdge() - panel.dp(12), typed -> {
                String value = typed.trim();
                if (value.isEmpty() || value.length() > 40)
                {
                    return;
                }
                page.name = value;
                name[0].setText(value);
                host.pageChanged();
            });
        });
        String[] types = {"movies", "tv", "both"};
        panel.chips("customise.includes", "tv", "Includes", new String[] {"Movies", "TV Shows", "Both"},
                    Math.max(0, indexOf(types, page.type)), n -> {
                        page.type = types[n];
                        host.pageChanged();
                    });
        TextView[] genres = {null};
        genres[0] = panel.opener("customise.genres", "genre", "Genres", genreSummary(page), () -> chooseGenres(
            page, genres[0]));
        TextView[] years = {null};
        years[0] = panel.opener("customise.years", "year", "Years", yearSummary(page), () -> chooseYears(page,
                                                                                                   years[0]));
        int rating = page.rating >= 9 ? 4 : page.rating >= 8 ? 3 : page.rating >= 7 ? 2 : page.rating >= 6 ? 1 : 0;
        panel.chips("customise.rating", "star", "Minimum rating", RATINGS, rating, n -> {
            page.rating = n == 0 ? 0 : 5 + n;
            host.pageChanged();
        });
        TextView[] language = {null};
        language[0] = panel.opener("customise.language", "language", "Original language",
                                   page.language.isEmpty() ? "Any" : languageName(page.language),
                                   () -> chooseOne(page, true, language[0]));
        TextView[] studio = {null};
        studio[0] = panel.opener("customise.studio", "tv", "Studio / Network",
                                 page.studio.isEmpty() ? "Any" : page.studio, () -> chooseOne(page, false, studio[0]));
    }

    static String genreSummary(CustomLibraryPage page)
    {
        return page.genres.isEmpty() ? "Any" : String.join(", ", page.genres);
    }

    static String yearSummary(CustomLibraryPage page)
    {
        if (page.year <= 0 && page.yearTo <= 0)
        {
            return "Any";
        }
        return (page.year > 0 ? String.valueOf(page.year) : "Any") + " – "
            + (page.yearTo > 0 ? String.valueOf(page.yearTo) : "Any");
    }

    private static String languageName(String code)
    {
        String name = new Locale(code).getDisplayLanguage(Locale.UK);
        return name == null || name.isEmpty() ? code : name;
    }

    /** Genres with their counts; Match: Any · All inside the list, at the top (sheet). */
    private void chooseGenres(CustomLibraryPage page, TextView value)
    {
        Map<String, Integer> counts = new TreeMap<>();
        List<Entry> all = new ArrayList<>(host.snapshot().movies);
        all.addAll(host.snapshot().shows);
        for (Entry entry : all)
        {
            for (String genre : PreviewGenres.parse(entry.genres))
            {
                counts.merge(genre, 1, Integer::sum);
            }
        }
        List<String> labels = new ArrayList<>();
        List<String> icons = new ArrayList<>();
        List<String> names = new ArrayList<>();
        labels.add(page.allGenres ? "Match: All" : "Match: Any");
        icons.add("chevrons");
        names.add(null);
        for (Map.Entry<String, Integer> genre : counts.entrySet())
        {
            labels.add(genre.getKey() + " · " + genre.getValue() + (genre.getValue() == 1 ? " title" : " titles"));
            icons.add(PreviewGenres.known(genre.getKey()) ? null : "genre");
            names.add(genre.getKey());
        }
        Set<Integer> ticked = new HashSet<>();
        for (int i = 1; i < names.size(); i++)
        {
            if (page.genres.contains(names.get(i)))
            {
                ticked.add(i);
            }
        }
        Dialog[] list = {null};
        list[0] = PreviewDialog.menuBelow(context, null, "Genres", labels.toArray(new String[0]),
                                          icons.toArray(new String[0]), ticked, 0, false, true, n -> {
                                              if (n == 0)
                                              {
                                                  page.allGenres = !page.allGenres;
                                                  PreviewDialog.updateLabel(list[0], 0, page.allGenres
                                                      ? "Match: All" : "Match: Any");
                                              }
                                              else if (!page.genres.add(names.get(n)))
                                              {
                                                  page.genres.remove(names.get(n));
                                              }
                                              Set<Integer> now = new HashSet<>();
                                              for (int i = 1; i < names.size(); i++)
                                              {
                                                  if (page.genres.contains(names.get(i)))
                                                  {
                                                      now.add(i);
                                                  }
                                              }
                                              PreviewDialog.updateChecks(list[0], now);
                                              value.setText(genreSummary(page));
                                              host.pageChanged();
                                          });
    }

    /** Quick choices, then From and To from the years that hold titles (sheet: only ranges that hold titles). */
    private void chooseYears(CustomLibraryPage page, TextView value)
    {
        List<Integer> present = LibraryFilters.years(host.snapshot());
        List<String> labels = new ArrayList<>();
        List<int[]> ranges = new ArrayList<>();
        LibraryFilters.quickYears(present, labels, ranges);
        labels.add("— Your Own Range");
        ranges.add(null);
        labels.add("From · " + (page.year > 0 ? page.year : "Any"));
        ranges.add(new int[] {-1, 0});
        labels.add("To · " + (page.yearTo > 0 ? page.yearTo : "Any"));
        ranges.add(new int[] {0, -1});
        String[] icons = new String[labels.size()];
        Set<Integer> ticked = new HashSet<>();
        for (int i = 0; i < ranges.size(); i++)
        {
            int[] range = ranges.get(i);
            icons[i] = labels.get(i).startsWith("— ") ? null : i < labels.size() - 2 ? "year" : "edit";
            if (range != null && range[0] >= 0 && range[1] >= 0 && range[0] == page.year && range[1] == page.yearTo)
            {
                ticked.add(i);
            }
        }
        PreviewDialog.menuBelow(context, null, "Years", labels.toArray(new String[0]), icons, ticked, 0, true, n -> {
            int[] range = ranges.get(n);
            if (range[0] < 0 || range[1] < 0)
            {
                boolean from = range[0] < 0;
                LibraryFilters.chooseYear(context, present, from ? "From" : "To", from ? page.year : page.yearTo,
                                          year -> {
                                              if (from)
                                              {
                                                  page.year = year;
                                              }
                                              else
                                              {
                                                  page.yearTo = year;
                                              }
                                              if (page.year > 0 && page.yearTo > 0 && page.yearTo < page.year)
                                              {
                                                  page.yearTo = page.year;
                                              }
                                              value.setText(yearSummary(page));
                                              host.pageChanged();
                                          });
                return;
            }
            page.year = range[0];
            page.yearTo = range[1];
            value.setText(yearSummary(page));
            host.pageChanged();
        });
    }

    /** Original language or Studio / Network: one choice from what the library holds, Any first. */
    private void chooseOne(CustomLibraryPage page, boolean languages, TextView value)
    {
        Map<String, Integer> counts = new TreeMap<>();
        List<Entry> all = new ArrayList<>(host.snapshot().movies);
        all.addAll(host.snapshot().shows);
        for (Entry entry : all)
        {
            if (languages)
            {
                if (entry.language != null && !entry.language.isEmpty())
                {
                    counts.merge(entry.language, 1, Integer::sum);
                }
            }
            else
            {
                for (String studio : PreviewGenres.parse(entry.studio))
                {
                    counts.merge(studio, 1, Integer::sum);
                }
            }
        }
        List<String> labels = new ArrayList<>();
        List<String> keys = new ArrayList<>();
        labels.add("Any");
        keys.add("");
        for (Map.Entry<String, Integer> item : counts.entrySet())
        {
            String shown = languages ? languageName(item.getKey()) : item.getKey();
            labels.add(shown + " · " + item.getValue() + (item.getValue() == 1 ? " title" : " titles"));
            keys.add(item.getKey());
        }
        String current = languages ? page.language : page.studio;
        int at = Math.max(0, keys.indexOf(current));
        String[] icons = new String[labels.size()];
        java.util.Arrays.fill(icons, languages ? "language" : "tv");
        icons[0] = "close";
        PreviewDialog.menuBelow(context, null, languages ? "Original language" : "Studio / Network",
                                labels.toArray(new String[0]), icons, Collections.singleton(at), at, true, true, n -> {
                                    if (languages)
                                    {
                                        page.language = keys.get(n);
                                    }
                                    else
                                    {
                                        page.studio = keys.get(n);
                                    }
                                    value.setText(n == 0 ? "Any" : labels.get(n).replaceFirst(" · .*$", ""));
                                    host.pageChanged();
                                });
    }

    /** For tests: the panel's root view. */
    View root()
    {
        return panel.body;
    }
}
