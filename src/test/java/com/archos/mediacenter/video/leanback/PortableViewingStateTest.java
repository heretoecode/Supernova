package com.archos.mediacenter.video.leanback;

import java.util.*;
import android.app.Application;
import org.json.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import static org.junit.Assert.*;

@RunWith(RobolectricTestRunner.class) @Config(application=Application.class,sdk=28)
public class PortableViewingStateTest {
    private final String movie="tmdb:movie:123",episode="tmdb:tv:999:2:4",unmatched="file:"+"a".repeat(64);
    private ViewingHistory.Record record(boolean completed,boolean started,int position,int duration,long played,long completion){return new ViewingHistory.Record(completed,started,position,duration,played,completion);}
    @Test public void payloadsHaveIndependentFieldsAndNoMediaPathsOrNegativeCompletionOffsets()throws Exception{
        Map<String,ViewingHistory.Record> rows=Map.of(movie,record(true,true,-2,5000,100,100),episode,record(false,true,1234,6000,50,0),unmatched,record(false,false,200,1000,0,0));
        JSONObject history=PortableViewingState.export(rows,PortableViewingState.Kind.HISTORY),positions=PortableViewingState.export(rows,PortableViewingState.Kind.POSITIONS);
        assertEquals(2,history.getJSONArray("rows").length());assertEquals(2,positions.getJSONArray("rows").length());assertFalse(history.toString().contains("position"));assertFalse(history.toString().contains("duration"));assertFalse(positions.toString().contains("completed"));assertFalse(positions.toString().contains("played"));assertFalse(positions.toString().contains("started"));assertFalse(positions.toString().contains("-2"));assertTrue(positions.toString().contains(unmatched));
    }
    @Test public void selectedHistoryAndPositionsMatrixPreservesUnselectedColumnsAndSourceMap()throws Exception{
        Map<String,ViewingHistory.Record> old=Map.of(movie,record(true,true,1234,6000,100,100),episode,record(false,true,2000,8000,300,0));
        Map<String,ViewingHistory.Record> incoming=Map.of(movie,record(false,true,888,9000,777,0));
        JSONObject history=PortableViewingState.export(incoming,PortableViewingState.Kind.HISTORY),positions=PortableViewingState.export(incoming,PortableViewingState.Kind.POSITIONS);
        for(boolean selectHistory:new boolean[]{false,true})for(boolean selectPositions:new boolean[]{false,true}){
            if(!selectHistory&&!selectPositions)continue;
            var restored=PortableViewingState.prepare(old,selectHistory?history:null,selectPositions?positions:null);var target=restored.get(movie);var other=restored.get(episode);
            assertEquals(selectHistory?false:true,target.completed);assertTrue(target.started);assertEquals(selectHistory?777:100,target.played);assertEquals(selectHistory?0:100,target.completedAt);
            assertEquals(selectPositions?888:1234,target.position);assertEquals(selectPositions?9000:6000,target.duration);
            assertEquals(selectHistory?false:true,other.started);assertEquals(selectHistory?0:300,other.played);assertEquals(selectPositions?0:2000,other.position);assertEquals(selectPositions?0:8000,other.duration);
            assertEquals(1234,old.get(movie).position);assertTrue(old.get(movie).completed);
        }
    }
    @Test public void presentEmptyHistoryCannotResurrectWatchedMarkersAndPositionsEmptyKeepsHistory()throws Exception{
        Map<String,ViewingHistory.Record> old=Map.of(movie,record(true,true,-2,6000,100,100),episode,record(false,true,2000,8000,300,0));
        JSONObject emptyHistory=PortableViewingState.export(Collections.emptyMap(),PortableViewingState.Kind.HISTORY),emptyPositions=PortableViewingState.export(Collections.emptyMap(),PortableViewingState.Kind.POSITIONS);
        var historyCleared=PortableViewingState.prepare(old,emptyHistory,null);assertFalse(historyCleared.get(movie).completed);assertEquals(0,historyCleared.get(movie).position);assertEquals(2000,historyCleared.get(episode).position);
        var positionsCleared=PortableViewingState.prepare(old,null,emptyPositions);assertTrue(positionsCleared.get(movie).completed);assertEquals(100,positionsCleared.get(movie).completedAt);assertEquals(-2,positionsCleared.get(movie).position);assertEquals(0,positionsCleared.get(episode).position);assertTrue(positionsCleared.get(episode).started);
        var bothCleared=PortableViewingState.prepare(old,emptyHistory,emptyPositions);for(var value:bothCleared.values()){assertFalse(value.completed);assertFalse(value.started);assertEquals(0,value.position);assertEquals(0,value.played);}
    }
    @Test public void absentMediaAndUnmatchedIdentitiesRemainStoredWithoutTitleGuesses()throws Exception{
        Map<String,ViewingHistory.Record> incoming=Map.of(episode,record(true,true,0,0,100,100),unmatched,record(false,true,1234,6000,50,0));
        JSONObject history=PortableViewingState.export(incoming,PortableViewingState.Kind.HISTORY),positions=PortableViewingState.export(incoming,PortableViewingState.Kind.POSITIONS);
        var restored=PortableViewingState.prepare(Collections.emptyMap(),history,positions);assertEquals(incoming.keySet(),restored.keySet());assertEquals(1234,restored.get(unmatched).position);assertEquals(history.toString(),PortableViewingState.export(restored,PortableViewingState.Kind.HISTORY).toString());assertEquals(positions.toString(),PortableViewingState.export(restored,PortableViewingState.Kind.POSITIONS).toString());
    }
    @Test public void injectedPathsSecretsDuplicateIdentitiesAndCoercedNumbersRejectBeforePreparation()throws Exception{
        var original=Map.of(movie,record(false,true,1234,6000,100,0));JSONObject valid=PortableViewingState.export(original,PortableViewingState.Kind.POSITIONS);
        for(Object bad:List.of("smb://user:SECRET@host/FILE.mkv","tmdb:movie:9999999999999999999","tmdb:tv:123:2:2147483648")){
            JSONObject changed=new JSONObject(valid.toString());changed.getJSONArray("rows").getJSONObject(0).put("identity",bad);try{PortableViewingState.prepare(original,null,changed);fail();}catch(JSONException expected){}assertEquals(1234,original.get(movie).position);
        }
        for(Object number:List.of("1",1.5,4294967297L,0,-2)){
            JSONObject changed=new JSONObject(valid.toString());changed.getJSONArray("rows").getJSONObject(0).put("position",number);try{PortableViewingState.validate(changed,PortableViewingState.Kind.POSITIONS);fail();}catch(JSONException expected){}
        }
        JSONObject duplicate=new JSONObject(valid.toString());duplicate.getJSONArray("rows").put(duplicate.getJSONArray("rows").getJSONObject(0));try{PortableViewingState.validate(duplicate,PortableViewingState.Kind.POSITIONS);fail();}catch(JSONException expected){}
        JSONObject secrets=new JSONObject(valid.toString());secrets.getJSONArray("rows").getJSONObject(0).put("credentials","SECRET");try{PortableViewingState.validate(secrets,PortableViewingState.Kind.POSITIONS);fail();}catch(JSONException expected){}
    }
}
