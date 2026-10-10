package com.archos.mediacenter.video.leanback;

import org.robolectric.util.ReflectionHelpers;
import java.util.concurrent.*;

/** Finish/cancel fixture work before Robolectric deletes its native runtime and database directory. */
public final class PreviewAsyncFixtures {
 private static okhttp3.OkHttpClient fixtureTransport;
 private static java.util.List<okhttp3.Interceptor> previousInterceptors;
 public static void offlineTransport() {
  fixtureTransport=ReflectionHelpers.getStaticField(com.archos.mediacenter.video.streaming.StreamingRepository.class,"HTTP");
  previousInterceptors=fixtureTransport.interceptors();
  ReflectionHelpers.setField(fixtureTransport,"interceptors",java.util.List.<okhttp3.Interceptor>of(chain->{throw new java.io.IOException("Offline UI fixture");}));
 }
 public static void drain() throws Exception {
  for(Thread thread:Thread.getAllStackTraces().keySet())if(thread.getName().equals("SupernovaSourceSummary")){
   thread.join(15000);if(thread.isAlive())throw new AssertionError("Source fixture did not finish before sandbox teardown");
  }
  ScheduledExecutorService technical=ReflectionHelpers.getStaticField(PreviewMetadata.class,"queue");
  technical.submit(()->{ScheduledFuture<?> retry=ReflectionHelpers.getStaticField(PreviewMetadata.class,"retry");if(retry!=null)retry.cancel(false);java.util.Map<?,?> jobs=ReflectionHelpers.getStaticField(PreviewMetadata.class,"jobs");jobs.clear();}).get(15,TimeUnit.SECONDS);
  ExecutorService extraction=ReflectionHelpers.getStaticField(PreviewMetadata.class,"worker");extraction.submit(()->{}).get(15,TimeUnit.SECONDS);
  technical.submit(()->{ScheduledFuture<?> retry=ReflectionHelpers.getStaticField(PreviewMetadata.class,"retry");if(retry!=null)retry.cancel(false);}).get(15,TimeUnit.SECONDS);
  ScheduledExecutorService enrichment=ReflectionHelpers.getStaticField(PreviewEnrichmentQueue.class,"WORK");
  long deadline=System.nanoTime()+TimeUnit.SECONDS.toNanos(30);
  while(enrichment.submit(()->{ScheduledFuture<?> retry=ReflectionHelpers.getStaticField(PreviewEnrichmentQueue.class,"retry");if(retry!=null)retry.cancel(false);return (Boolean)ReflectionHelpers.getStaticField(PreviewEnrichmentQueue.class,"draining");}).get(15,TimeUnit.SECONDS))
   {if(System.nanoTime()>deadline)throw new AssertionError("Enrichment fixture did not finish before sandbox teardown");Thread.sleep(10);}
  enrichment.submit(()->{android.database.sqlite.SQLiteOpenHelper store=ReflectionHelpers.getStaticField(PreviewEnrichmentQueue.class,"store");if(store!=null)store.close();ReflectionHelpers.setStaticField(PreviewEnrichmentQueue.class,"store",null);}).get(15,TimeUnit.SECONDS);
  ViewingHistory.awaitWrites();
  if(fixtureTransport!=null)ReflectionHelpers.setField(fixtureTransport,"interceptors",previousInterceptors);
 }
 private PreviewAsyncFixtures(){}
}
