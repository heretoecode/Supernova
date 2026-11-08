package com.archos.mediacenter.video.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BooleanSupplier;
import org.junit.Test;

public class LibrarySearchTest
{
    private final LibrarySearch.Person sheridan = new LibrarySearch.Person(1, "Taylor Sheridan", "creator");

    private LibrarySearch.Title title(long id, String name)
    {
        return new LibrarySearch.Title(new LibrarySearch.Identity("tv", id), name, List.of(), List.of(sheridan),
                                       List.of("Western"), 2020);
    }

    private class Sources implements LibrarySearch.Sources
    {
        int calls;

        @Override public List<LibrarySearch.Family> family(LibrarySearch.Title owned, BooleanSupplier cancelled)
        {
            calls++;
            return List.of(new LibrarySearch.Family(title(2, "1923"), 0, 0, true, false, true, true),
                new LibrarySearch.Family(title(3, "Mayor of Kingstown"), 0, 0, false, false, true, false));
        }

        @Override public List<LibrarySearch.Title> work(LibrarySearch.Person person, BooleanSupplier cancelled)
        {
            calls++;
            return List.of(title(3, "Mayor of Kingstown"), title(3, "Mayor of Kingstown"));
        }

        @Override public List<String> services(LibrarySearch.Identity title, BooleanSupplier cancelled)
        {
            calls++;
            return List.of("Paramount+", "Paramount+");
        }
    }

    @Test public void noLibraryMatchNeverCallsRemoteAndCreatorAloneIsNotFamily()
    {
        Sources sources = new Sources();
        List<LibrarySearch.Title> library = List.of(title(1, "Yellowstone"));
        assertEquals(LibrarySearch.Result.EMPTY, LibrarySearch.load("Dexter", library, sources, () -> true,
                                                                  () -> false));
        assertEquals(0, sources.calls);
        var result = LibrarySearch.load("Yellowstone", library, sources, () -> true, () -> false);
        assertEquals(1, result.library().size());
        assertEquals(List.of("1923"), result.families().get(0).items().stream()
            .map(item -> item.title().title()).toList());
        assertTrue(result.people().isEmpty());
        assertEquals(List.of("Paramount+"), result.families().get(0).items().get(0).services());
    }

    @Test public void peopleListEveryLibraryTitleWithRoleAndOtherWorkOnlyOnce()
    {
        var result = LibrarySearch.load("sheridan", List.of(title(1, "Yellowstone"), title(2, "1923")),
                                       new Sources(), () -> true, () -> false);
        assertEquals(2, result.library().size());
        assertEquals("creator", result.library().get(0).reasons().get(0).role());
        assertEquals(1, result.people().size());
        assertEquals(1, result.people().get(0).items().size());
        assertTrue(result.families().isEmpty());
    }

    @Test public void genreAndYearStayLocalAndDisabledNeverChecksAvailability()
    {
        Sources sources = new Sources();
        for (String query : List.of("western", "2020", "Yellowstone"))
        {
            var result = LibrarySearch.load(query, List.of(title(1, "Yellowstone")), sources, () -> false,
                                           () -> false);
            assertEquals(1, result.library().size());
            assertTrue(result.families().isEmpty());
        }
        assertEquals(0, sources.calls);
        assertEquals(LibrarySearch.Result.EMPTY, LibrarySearch.load("Yellowstone", List.of(title(1, "Yellowstone")),
                                                                  sources, () -> true, () -> true));
    }

    @Test public void filmFamilyRequiresExactPositiveCollection()
    {
        var film = new LibrarySearch.Title(new LibrarySearch.Identity("movie", 9), "Dune", List.of(), List.of(),
                                           List.of(), 2021);
        assertTrue(new LibrarySearch.Family(film, 1, 1, false, false, false, false).accepted("movie"));
        assertTrue(!new LibrarySearch.Family(film, 1, 2, true, true, true, true).accepted("movie"));
        assertTrue(!new LibrarySearch.Family(film, 0, 0, true, true, true, true).accepted("movie"));
    }

    @Test public void sessionCachesAndFencesQueuedCallbacksAfterNextKeystroke() throws Exception
    {
        Sources sources = new Sources();
        List<Runnable> callbacks = new java.util.concurrent.CopyOnWriteArrayList<>();
        AtomicInteger delivered = new AtomicInteger();
        try (var session = new LibrarySearchSession(sources, () -> true, callbacks::add))
        {
            List<LibrarySearch.Title> library = List.of(title(1, "Yellowstone"));
            session.submit("Yellowstone", library, "GB:1", result -> delivered.incrementAndGet());
            await(callbacks);
            int calls = sources.calls;
            Runnable old = callbacks.remove(0);
            session.submit("Yellowstone", library, "GB:1", result -> delivered.incrementAndGet());
            await(callbacks);
            old.run();
            assertEquals(0, delivered.get());
            callbacks.remove(0).run();
            assertEquals(1, delivered.get());
            assertEquals(calls, sources.calls);
        }
    }

    private void await(List<Runnable> callbacks) throws Exception
    {
        long deadline = System.nanoTime() + TimeUnit.SECONDS.toNanos(3);
        while (callbacks.isEmpty() && System.nanoTime() < deadline)
            Thread.sleep(5);
        assertTrue(!callbacks.isEmpty());
    }
}
