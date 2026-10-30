package com.archos.mediacenter.video.player;

import android.app.Application;
import org.json.*;
import org.junit.*;
import org.junit.runner.RunWith;
import org.robolectric.*;
import org.robolectric.annotation.Config;
import org.robolectric.util.ReflectionHelpers;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import okhttp3.*;
import static org.junit.Assert.*;

@RunWith(RobolectricTestRunner.class) @Config(application=Application.class,sdk=28)
public class ChaptersDbClientTest {
 private JSONObject fixture(String file)throws Exception{try(InputStream in=getClass().getResourceAsStream("/chaptersdb/"+file)){assertNotNull(in);return new JSONObject(new String(in.readAllBytes(),StandardCharsets.UTF_8));}}
 @Before public void resetRate(){ReflectionHelpers.setStaticField(ChaptersDbClient.HttpSource.class,"nextRequestAt",0L);ReflectionHelpers.setStaticField(ChaptersDbClient.HttpSource.class,"backoffUntil",0L);}
 @Test public void realMovieSetSortsWithoutInventingEditionOrRuntimeProof()throws Exception{
  JSONObject movie=fixture("movie.json");List<String> routes=new ArrayList<>();ChaptersDbClient client=new ChaptersDbClient(route->{routes.add(route);return movie;});ChaptersDbClient.Result result=client.movie("tt6628394",141000*60L);
  assertEquals(Collections.singletonList("show/tt6628394"),routes);assertEquals(ChaptersDbClient.Status.UNVERIFIED_ALIGNMENT,result.status);assertEquals(1,result.candidates.size());assertEquals(0,result.candidates.get(0).chapters.get(0).startMs);assertEquals(11,result.candidates.get(0).chapters.size());assertEquals(8145428,result.candidates.get(0).chapters.get(10).startMs);assertTrue("Unverified sets remain hidden by owner decision",result.verifiedChapters().isEmpty());assertEquals("",result.candidates.get(0).note);
 }
 @Test public void wrongMovieIdentityAndMissingIdsNeverYieldMarkers()throws Exception{AtomicInteger calls=new AtomicInteger();JSONObject movie=fixture("movie.json");ChaptersDbClient client=new ChaptersDbClient(route->{calls.incrementAndGet();return movie;});assertEquals(ChaptersDbClient.Status.MISSING_ID,client.movie("file:///private/Film.mkv",9000000).status);assertEquals(0,calls.get());assertEquals(ChaptersDbClient.Status.IDENTITY_MISMATCH,client.movie("tt1375666",9000000).status);assertEquals(ChaptersDbClient.Status.MISSING_ID,client.episode("tt0112178","",1,1,6000000).status);assertEquals(1,calls.get());}
 @Test public void airedAndDvdOrdinalsParentAndEpisodeIdentityAreAllRequired()throws Exception{
  JSONObject episode=fixture("episode.json"),chapters=fixture("episode-chapters.json");JSONObject show=new JSONObject("{id:'d40e78af-b1d0-4ab5-9f73-4d5e1e32f0f1',type:'tv',imdbId:'tt0112178'}");
  ChaptersDbClient client=new ChaptersDbClient(route->route.startsWith("show/")?show:route.startsWith("episode/")?episode:chapters);
  assertEquals("Observed empty TVDB episode identity is not guessed from S01E01",ChaptersDbClient.Status.IDENTITY_MISMATCH,client.episode("tt0112178","349232",1,1,6000000).status);
  // Explicit synthetic identity extension exercises the valid join, not a claim of live coverage.
  episode.put("tvdbId","349232");assertEquals(ChaptersDbClient.Status.IDENTITY_MISMATCH,client.episode("tt0112178","349232",1,2,6000000).status);
  assertEquals(ChaptersDbClient.Status.UNVERIFIED_ALIGNMENT,client.episode("tt0112178","349232",1,1,6000000).status);
  chapters.put("episodeId","another-episode");assertEquals(ChaptersDbClient.Status.IDENTITY_MISMATCH,client.episode("tt0112178","349232",1,1,6000000).status);
 }
 @Test public void sortingDedupAndBoundsRejectConflictingMalformedAndWrongCutStarts()throws Exception{
  JSONObject set=new JSONObject("{id:'5e2438cd-3170-4078-afa4-a120ec5f5d0f',entries:[{time:'00:00:02.5',name:'Second'},{time:'00:00:00.000',name:'First'},{time:'00:00:02.5',name:'Second'}]}");
  ChaptersDbClient.Result result=ChaptersDbClient.sets(new JSONArray().put(set),10000);assertEquals(2,result.candidates.get(0).chapters.size());assertEquals(2500,result.candidates.get(0).chapters.get(1).startMs);
  set.getJSONArray("entries").put(new JSONObject("{time:'00:00:02.500',name:'Conflict'}"));assertTrue(ChaptersDbClient.sets(new JSONArray().put(set),10000).candidates.isEmpty());
  assertTrue(ChaptersDbClient.sets(fixture("episode-chapters.json").getJSONArray("chapters"),45*60*1000).candidates.isEmpty());
  for(String value:new String[]{"-00:00:01","00:60:00","NaN","1:02:03.000","00:00:01.1234"})assertEquals(-1,ChaptersDbClient.timestamp(value));
 }
 private Response response(Interceptor.Chain chain,int status,String body,String type){return new Response.Builder().request(chain.request()).protocol(Protocol.HTTP_1_1).code(status).message("Fixture").header("Retry-After","120").body(ResponseBody.create(body,MediaType.get(type))).build();}
 @Test public void transportUsesOnlyPublishedHttpsRoutesAndPrivateCacheSurvivesOffline()throws Exception{
  AtomicInteger calls=new AtomicInteger();OkHttpClient http=new OkHttpClient.Builder().addInterceptor(chain->{int n=calls.incrementAndGet();assertEquals("https://chaptersdb.com/api/v1/show/tt6628394",chain.request().url().toString());assertEquals("GET",chain.request().method());assertNull(chain.request().header("Authorization"));if(n>1)throw new IOException("Offline fixture");return response(chain,200,"{id:'known'}","application/json");}).build();
  ChaptersDbClient.HttpSource source=new ChaptersDbClient.HttpSource(RuntimeEnvironment.getApplication(),http);assertEquals("known",source.get("show/tt6628394").getString("id"));assertEquals("known",source.get("show/tt6628394").getString("id"));assertEquals(1,calls.get());
  File cache=new File(RuntimeEnvironment.getApplication().getCacheDir(),"chaptersdb-v1");File file=Objects.requireNonNull(cache.listFiles())[0];JSONObject envelope;try(InputStream in=new FileInputStream(file)){envelope=new JSONObject(ChaptersDbClient.HttpSource.read(in));}envelope.put("at",System.currentTimeMillis()-25L*60*60*1000);try(FileOutputStream out=new FileOutputStream(file)){out.write(envelope.toString().getBytes(StandardCharsets.UTF_8));}
  ReflectionHelpers.setStaticField(ChaptersDbClient.HttpSource.class,"nextRequestAt",0L);assertEquals("known",source.get("show/tt6628394").getString("id"));assertEquals(2,calls.get());
 }
 @Test public void responseLimitRedirectHtmlAndRateBackoffDoNotBecomeData()throws Exception{
  try{ChaptersDbClient.HttpSource.read(new ByteArrayInputStream(new byte[1024*1024+1]));fail();}catch(IOException expected){}
  AtomicInteger calls=new AtomicInteger();OkHttpClient http=new OkHttpClient.Builder().addInterceptor(chain->{calls.incrementAndGet();return response(chain,429,"{}","application/json");}).build();ChaptersDbClient.HttpSource source=new ChaptersDbClient.HttpSource(RuntimeEnvironment.getApplication(),http);
  for(int i=0;i<2;i++)try{source.get("show/tt6628394");fail();}catch(IOException expected){}assertEquals(1,calls.get());
  try{source.get("https://other.example/private");fail();}catch(IOException expected){}assertEquals(1,calls.get());
 }
}
