package com.archos.mediacenter.video.scanning;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import org.junit.Test;

public class ConnectionReachabilityTest
{
    @Test public void probesEveryConnectionTwoAtATimeAndKeepsLastSeenAfterFailure()
    {
        List<Consumer<Boolean>> callbacks = new ArrayList<>();
        List<String> opened = new ArrayList<>();
        List<String> closed = new ArrayList<>();
        try (ConnectionReachability repository = new ConnectionReachability(Runnable::run, (id, done) -> {
            opened.add(id);
            callbacks.add(done);
            return () -> closed.add(id);
        }, Map.of("a", new ConnectionReachability.Status(true, 100, false)), ignored -> {}))
        {
            repository.refresh(List.of("a", "b", "c"));
            assertEquals(List.of("a", "b"), opened);
            callbacks.get(0).accept(false);
            assertEquals(List.of("a", "b", "c"), opened);
            assertEquals(100, repository.snapshot().get("a").lastSeenUtcMs());
            callbacks.get(1).accept(true);
            assertTrue(repository.snapshot().get("b").reachable());
            assertTrue(repository.snapshot().get("b").lastSeenUtcMs() > 0);
            assertEquals(List.of("a", "b"), closed);
        }
        assertEquals(List.of("a", "b", "c"), closed);
    }

    @Test public void supersededProbeAndLateCallbackCannotOverwriteANewRefresh()
    {
        List<Consumer<Boolean>> callbacks = new ArrayList<>();
        try (ConnectionReachability repository = new ConnectionReachability(Runnable::run, (id, done) -> {
            callbacks.add(done);
            return () -> {};
        }, Map.of(), ignored -> {}))
        {
            repository.refresh(List.of("a"));
            repository.refresh(List.of("b"));
            callbacks.get(0).accept(true);
            assertFalse(repository.snapshot().containsKey("a"));
            callbacks.get(1).accept(false);
            assertFalse(repository.snapshot().get("b").checking());
        }
    }

    @Test public void deadlineClosesAProbeThatNeverCompletes() throws Exception
    {
        CountDownLatch closed = new CountDownLatch(1);
        CountDownLatch persisted = new CountDownLatch(1);
        try (ConnectionReachability repository = new ConnectionReachability(Runnable::run,
            (id, done) -> closed::countDown, Map.of(), ignored -> persisted.countDown(), 20))
        {
            repository.refresh(List.of("a"));
            assertTrue(closed.await(2, TimeUnit.SECONDS));
            assertTrue(persisted.await(2, TimeUnit.SECONDS));
            assertFalse(repository.snapshot().get("a").reachable());
            assertFalse(repository.snapshot().get("a").checking());
        }
    }
    @Test public void immediateProbeCompletionCannotRecursivelyOverflowTheDispatcher()
    {
        List<String> sources = java.util.stream.IntStream.range(0, 1000).mapToObj(Integer::toString).toList();
        try (ConnectionReachability repository = new ConnectionReachability(Runnable::run, (id, done) -> {
            done.accept(false);
            return () -> {};
        }, Map.of(), ignored -> {}))
        {
            repository.refresh(sources);
            assertEquals(1000, repository.snapshot().size());
            assertTrue(repository.snapshot().values().stream().noneMatch(ConnectionReachability.Status::checking));
        }
    }

}
