package com.archos.mediacenter.video.data;

import com.archos.mediacenter.video.leanback.search.PreviewSearchText;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BooleanSupplier;

/** Worker-only library-gated discovery. Input is an immutable indexed library snapshot, never a remote search. */
public final class LibrarySearch
{
    public record Identity(String kind, long id)
    {
        public Identity
        {
            if (!("movie".equals(kind) || "tv".equals(kind)) || id <= 0)
                throw new IllegalArgumentException("Exact title identity required");
        }
    }

    public record Person(long id, String name, String role)
    {
    }

    public record Title(Identity identity, String title, List<String> aliases, List<Person> people,
                        List<String> genres, int year)
    {
        public Title
        {
            aliases = List.copyOf(aliases);
            people = List.copyOf(people);
            genres = List.copyOf(genres);
        }
    }

    /** Evidence must describe a shared fictional universe, never recommendation/similarity scores. */
    public record Family(Title title, long ownedCollection, long candidateCollection, boolean explicitSpinoff,
                         boolean sharedName, boolean sharedCreator, boolean sharedCharacters)
    {
        public boolean accepted(String kind)
        {
            if (!title.identity().kind().equals(kind))
                return false;
            if ("movie".equals(kind))
                return ownedCollection > 0 && ownedCollection == candidateCollection;
            return explicitSpinoff || sharedCreator && sharedName && sharedCharacters;
        }
    }

    public record Item(Title title, List<String> services)
    {
        public Item
        {
            services = List.copyOf(new LinkedHashSet<>(services));
        }
    }

    public record Match(Title title, List<Person> reasons)
    {
    }

    public record Group(Identity owner, Person person, List<Item> items)
    {
    }

    public record Result(List<Match> library, List<Group> families, List<Group> people)
    {
        public static final Result EMPTY = new Result(List.of(), List.of(), List.of());
    }

    public interface Sources
    {
        List<Family> family(Title owned, BooleanSupplier cancelled) throws Exception;
        List<Title> work(Person person, BooleanSupplier cancelled) throws Exception;
        List<String> services(Identity title, BooleanSupplier cancelled) throws Exception;
        default void cancel()
        {
        }
    }

    public static Result load(String query, List<Title> library, Sources sources, BooleanSupplier enabled,
                              BooleanSupplier cancelled)
    {
        if (query == null || query.length() > 240 || PreviewSearchText.fold(query).isEmpty())
            return Result.EMPTY;
        List<Match> matches = new ArrayList<>();
        List<Title> titleMatches = new ArrayList<>();
        Map<Long, Person> people = new LinkedHashMap<>();
        Set<Identity> owned = new LinkedHashSet<>();
        for (Title title : library)
        {
            if (stopped(cancelled))
                return Result.EMPTY;
            if (!owned.add(title.identity()))
                continue;
            List<String> names = new ArrayList<>(title.aliases());
            names.add(title.title());
            boolean byTitle = PreviewSearchText.rank(query, names.toArray(new String[0])) > 0;
            List<Person> reasons = new ArrayList<>();
            for (Person person : title.people())
                if (PreviewSearchText.rank(query, person.name()) > 0)
                {
                    reasons.add(person);
                    if (person.id() > 0)
                        people.putIfAbsent(person.id(), person);
                }
            if (byTitle || !reasons.isEmpty() || PreviewSearchText.rank(query,
                title.genres().toArray(new String[0])) > 0 || query.trim().equals(Integer.toString(title.year())))
                matches.add(new Match(title, List.copyOf(reasons)));
            if (byTitle)
                titleMatches.add(title);
        }
        if (matches.isEmpty())
            return Result.EMPTY;
        List<Group> families = new ArrayList<>();
        List<Group> works = new ArrayList<>();
        if (!enabled.getAsBoolean())
            return new Result(List.copyOf(matches), List.of(), List.of());
        for (Title title : titleMatches.subList(0, Math.min(12, titleMatches.size())))
        {
            if (stopped(cancelled) || !enabled.getAsBoolean())
                break;
            try
            {
                List<Title> candidates = new ArrayList<>();
                List<Family> family = sources.family(title, cancelled);
                for (Family member : family.subList(0, Math.min(40, family.size())))
                    if (member.accepted(title.identity().kind()))
                        candidates.add(member.title());
                List<Item> items = available(candidates, owned, sources, enabled, cancelled);
                if (!items.isEmpty())
                    families.add(new Group(title.identity(), null, items));
            }
            catch (Exception unavailable)
            {
                // Independent family failure cannot erase the base library results.
            }
        }
        for (Person person : new ArrayList<>(people.values()).subList(0, Math.min(8, people.size())))
        {
            if (stopped(cancelled) || !enabled.getAsBoolean())
                break;
            try
            {
                List<Item> items = available(sources.work(person, cancelled), owned, sources, enabled, cancelled);
                if (!items.isEmpty())
                    works.add(new Group(null, person, items));
            }
            catch (Exception unavailable)
            {
                // No verified service means no streaming item.
            }
        }
        if (stopped(cancelled))
            return Result.EMPTY;
        return new Result(List.copyOf(matches), enabled.getAsBoolean() ? List.copyOf(families) : List.of(),
                          enabled.getAsBoolean() ? List.copyOf(works) : List.of());
    }

    private static List<Item> available(List<Title> candidates, Set<Identity> owned, Sources sources,
                                        BooleanSupplier enabled, BooleanSupplier cancelled) throws Exception
    {
        Map<Identity, Item> result = new LinkedHashMap<>();
        for (Title title : candidates.subList(0, Math.min(40, candidates.size())))
        {
            if (stopped(cancelled) || !enabled.getAsBoolean())
                return List.of();
            if (owned.contains(title.identity()) || result.containsKey(title.identity()))
                continue;
            List<String> services = sources.services(title.identity(), cancelled);
            if (!services.isEmpty())
                result.put(title.identity(), new Item(title, services));
        }
        return List.copyOf(result.values());
    }

    private static boolean stopped(BooleanSupplier cancelled)
    {
        return cancelled.getAsBoolean() || Thread.currentThread().isInterrupted();
    }

    private LibrarySearch()
    {
    }
}
