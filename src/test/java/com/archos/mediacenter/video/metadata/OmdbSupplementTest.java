package com.archos.mediacenter.video.metadata;
import org.junit.*;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.json.*;
import static org.junit.Assert.*;
@RunWith(RobolectricTestRunner.class) @Config(application=android.app.Application.class,sdk=28)
public class OmdbSupplementTest {
 @Test public void productionHasNoCredentialBinding(){assertFalse(OmdbSupplement.PRODUCTION.enabled());assertSame(OmdbSupplement.Ratings.EMPTY,OmdbSupplement.PRODUCTION.load("tt0089218","movie"));}
 @Test public void deniedAccessDoesNotReadKeyOrCallTransport(){OmdbSupplement client=new OmdbSupplement(new OmdbSupplement.Access(){public boolean permitted(){return false;}public boolean acquireRequest(){throw new AssertionError();}public String apiKey(){throw new AssertionError();}},url->{throw new AssertionError();});assertSame(OmdbSupplement.Ratings.EMPTY,client.load("tt0089218","movie"));}
 @Test public void budgetAndIdentifierGuardNetwork(){OmdbSupplement client=new OmdbSupplement(access(false),url->{throw new AssertionError();});assertSame(OmdbSupplement.Ratings.EMPTY,client.load("tt0089218","movie"));assertSame(OmdbSupplement.Ratings.EMPTY,client.load("wrong","movie"));assertSame(OmdbSupplement.Ratings.EMPTY,client.load("tt0089218","season"));}
 @Test public void exactIdTypeAndKnownSourcesOnly()throws Exception{JSONObject data=new JSONObject("{Response:'True',imdbID:'tt0089218',Type:'movie',imdbRating:'7.7',imdbVotes:'300,000',Metascore:'62',Awards:'One nomination.',Rated:'PG',Ratings:[{Source:'Rotten Tomatoes',Value:'77%'},{Source:'Unverified',Value:'100%'}]}");var ratings=OmdbSupplement.parse(data,"tt0089218","movie");assertEquals("7.7/10",ratings.imdb);assertEquals("77%",ratings.rottenTomatoes);assertEquals("62/100",ratings.metacritic);assertEquals("One nomination.",ratings.awards);assertEquals("300,000",ratings.imdbVotes);assertSame(OmdbSupplement.Ratings.EMPTY,OmdbSupplement.parse(data,"tt0000001","movie"));assertSame(OmdbSupplement.Ratings.EMPTY,OmdbSupplement.parse(data,"tt0089218","series"));}
 @Test public void missingMalformedAndOutOfRangeDataRemainMissing()throws Exception{var data=new JSONObject("{Response:'True',imdbID:'tt0089218',Type:'movie',imdbRating:'11',imdbVotes:'N/A',Metascore:'101',Awards:'N/A',Ratings:[{Source:'Rotten Tomatoes',Value:'-1%'}]}");var ratings=OmdbSupplement.parse(data,"tt0089218","movie");for(int i=0;i<4;i++)assertEquals("",ratings.value(i));data.put("Response","False");assertSame(OmdbSupplement.Ratings.EMPTY,OmdbSupplement.parse(data,"tt0089218","movie"));}
 @Test public void preparedRequestIsExactHttpsAndFailureDoesNotLeakKey(){int[] calls={0};OmdbSupplement client=new OmdbSupplement(access(true),url->{calls[0]++;assertEquals("https",url.scheme());assertEquals("www.omdbapi.com",url.host());assertEquals("tt0089218",url.queryParameter("i"));assertEquals("movie",url.queryParameter("type"));assertNull(url.queryParameter("t"));throw new java.io.IOException("fixture unavailable");});assertSame(OmdbSupplement.Ratings.EMPTY,client.load("tt0089218","movie"));assertEquals(1,calls[0]);}
 private OmdbSupplement.Access access(boolean budget){return new OmdbSupplement.Access(){public boolean permitted(){return true;}public boolean acquireRequest(){return budget;}public String apiKey(){return "fixture00";}};}
}
