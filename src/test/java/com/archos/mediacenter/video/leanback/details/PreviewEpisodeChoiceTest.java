package com.archos.mediacenter.video.leanback.details;

import com.archos.mediacenter.video.browser.adapters.object.Episode;
import java.util.*;
import org.json.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(RobolectricTestRunner.class) @Config(application=android.app.Application.class,sdk=28)
public class PreviewEpisodeChoiceTest {
    @Test @SuppressWarnings({"rawtypes","unchecked"}) public void providerMarkKeepsMonochromeShapeAndLocalRebindRestoresPlay()throws Exception{
        android.app.Activity activity=org.robolectric.Robolectric.buildActivity(android.app.Activity.class).setup().get();
        try{com.squareup.picasso.Picasso.get();}catch(IllegalStateException unset){com.squareup.picasso.Picasso.setSingletonInstance(new com.squareup.picasso.Picasso.Builder(activity.getApplicationContext()).build());}
        try{
            com.archos.mediacenter.video.streaming.StreamingRepository.prefs(activity).edit().putBoolean("streaming_enabled",true).putString("streaming_country","IE").putStringSet("streaming_providers_IE",Collections.singleton("8")).apply();
            var exact=com.archos.mediacenter.video.streaming.StreamingRepository.parseAvailability(new JSONObject("{results:{IE:{link:'https://www.themoviedb.org/tv/42/season/1/episode/2/watch',flatrate:[{provider_id:8,provider_name:'Provider'}]}}}"),"IE");
            PreviewEpisodeRow row=new PreviewEpisodeRow(activity,Collections.singletonList(new PreviewEpisodeChoice(1,2,null,new JSONObject("{id:99,name:'Episode',air_date:'2000-01-01'}"))),42,exact);
            androidx.recyclerview.widget.RecyclerView.Adapter adapter=row.getAdapter();
            androidx.recyclerview.widget.RecyclerView.ViewHolder holder=adapter.onCreateViewHolder(row,0);adapter.onBindViewHolder(holder,0);
            PreviewLandscapeCard card=(PreviewLandscapeCard)holder.itemView;
            assertTrue(card.isClickable());assertEquals(Boolean.TRUE,card.availability.getTag());
            assertTrue(card.availability.getColorFilter() instanceof android.graphics.ColorMatrixColorFilter);
            android.graphics.ColorMatrix matrix=new android.graphics.ColorMatrix();((android.graphics.ColorMatrixColorFilter)card.availability.getColorFilter()).getColorMatrix(matrix);
            float[] values=matrix.getArray();for(int channel=0;channel<3;channel++){assertEquals(values[channel],values[channel+5],.0001f);assertEquals(values[channel],values[channel+10],.0001f);}assertEquals(1f,values[18],.0001f);
            assertEquals(205,card.availability.getImageAlpha());
            card.bind("Local episode","",null,true);assertNull(card.availability.getColorFilter());assertEquals(255,card.availability.getImageAlpha());
            adapter.onViewRecycled(holder);
        }finally{activity.finish();}
    }
    @Test @SuppressWarnings({"rawtypes","unchecked"}) public void seasonOfferDoesNotMakeAnUnverifiedEpisodePlayable()throws Exception{
        android.app.Activity activity=org.robolectric.Robolectric.buildActivity(android.app.Activity.class).setup().get();
        try{com.squareup.picasso.Picasso.get();}catch(IllegalStateException unset){com.squareup.picasso.Picasso.setSingletonInstance(new com.squareup.picasso.Picasso.Builder(activity.getApplicationContext()).build());}
        com.archos.mediacenter.video.streaming.StreamingRepository.prefs(activity).edit().putBoolean("streaming_enabled",true).putString("streaming_country","IE").putStringSet("streaming_providers_IE",Collections.singleton("8")).apply();
        com.archos.mediacenter.video.streaming.StreamingRepository.Availability season=com.archos.mediacenter.video.streaming.StreamingRepository.parseAvailability(new JSONObject("{results:{IE:{link:'https://www.themoviedb.org/tv/42/watch',flatrate:[{provider_id:8,provider_name:'Provider'}]}}}"),"IE");
        PreviewEpisodeRow row=new PreviewEpisodeRow(activity,Collections.singletonList(new PreviewEpisodeChoice(1,2,null,new JSONObject("{id:99,name:'Episode',air_date:'2000-01-01'}"))),42,season);
        androidx.recyclerview.widget.RecyclerView.Adapter adapter=row.getAdapter();
        androidx.recyclerview.widget.RecyclerView.ViewHolder holder=adapter.onCreateViewHolder(row,0);adapter.onBindViewHolder(holder,0);
        PreviewLandscapeCard card=(PreviewLandscapeCard)holder.itemView;
        assertEquals("Availability unknown",card.metadata.getText().toString());assertFalse(card.isClickable());assertEquals(Boolean.FALSE,card.availability.getTag());
        adapter.onViewRecycled(holder);activity.finish();
    }
    @Test public void localVersionWinsAndUnknownEpisodesRemainVisible()throws Exception{
        Episode first=mock(Episode.class),duplicate=mock(Episode.class);when(first.getEpisodeNumber()).thenReturn(2);when(duplicate.getEpisodeNumber()).thenReturn(2);
        JSONArray remote=new JSONArray("[{episode_number:3,season_number:1},{episode_number:2,season_number:1},{episode_number:1,season_number:1},{episode_number:3,season_number:1}]");
        List<PreviewEpisodeChoice> row=PreviewEpisodeChoice.reconcile(Collections.singletonMap(1,Arrays.asList(first,duplicate)),Collections.singletonMap(1,remote)).get(1);
        assertEquals(3,row.size());assertEquals(1,row.get(0).number);assertNull(row.get(0).local);assertSame(first,row.get(1).local);assertNotNull(row.get(1).remote);assertEquals(3,row.get(2).number);
    }
    @Test public void invalidRemoteCoordinatesCannotDisplaceLocalSpecials()throws Exception{
        Episode special=mock(Episode.class);when(special.getEpisodeNumber()).thenReturn(1);
        SortedMap<Integer,List<PreviewEpisodeChoice>> rows=PreviewEpisodeChoice.reconcile(Collections.singletonMap(0,Collections.singletonList(special)),Collections.singletonMap(1,new JSONArray("[{episode_number:0},{episode_number:1,season_number:2}]")));
        assertEquals(1,rows.size());assertSame(special,rows.get(0).get(0).local);
    }
    @Test public void unairedOrUnknownEpisodeIsNotOfferedThroughSeasonAvailability(){assertFalse(PreviewEpisodeRow.aired(""));assertFalse(PreviewEpisodeRow.aired("2999-01-01"));assertTrue(PreviewEpisodeRow.aired("2000-01-01"));}
}
