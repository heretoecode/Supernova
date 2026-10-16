package com.archos.mediacenter.video.leanback;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import org.junit.Test;
import static org.junit.Assert.*;

public class HeroRotationPolicyTest {
    private HeroRotationPolicy.Candidate<String> title(String id, boolean tv) { return new HeroRotationPolicy.Candidate<>(id,tv,id); }
    private List<List<HeroRotationPolicy.Candidate<String>>> sources() { List<List<HeroRotationPolicy.Candidate<String>>> result=new ArrayList<>();for(int i=0;i<4;i++)result.add(new ArrayList<>());return result; }
    @Test public void balancedUniquePoolIncludesEveryApprovedSourceWithoutForcedAlternation(){
        List<List<HeroRotationPolicy.Candidate<String>>> sources=sources();
        for(int i=0;i<20;i++)sources.get(0).add(title("recent"+i,i%2==0));
        sources.get(1).add(title("trending-film",false));sources.get(2).add(title("popular-show",true));sources.get(3).add(title("because-film",false));
        sources.get(1).add(title("recent0",true));
        List<HeroRotationPolicy.Selection<String>> selected=HeroRotationPolicy.select(sources,Collections.emptyList(),row->true);
        assertEquals(10,selected.size());assertEquals(5,selected.stream().filter(row->row.candidate.television).count());
        HashSet<String> ids=new HashSet<>();for(HeroRotationPolicy.Selection<String> row:selected)assertTrue(ids.add(row.candidate.identity));
        assertTrue(ids.containsAll(Arrays.asList("trending-film","popular-show","because-film")));
        assertEquals(selected.get(0).candidate.identity,HeroRotationPolicy.select(sources,Collections.emptyList(),row->true).get(0).candidate.identity);
    }
    @Test public void typeShortageFillsFromOtherTypeAndNeverDuplicatesOrAddsIneligibleTitles(){
        List<List<HeroRotationPolicy.Candidate<String>>> sources=sources();for(int i=0;i<8;i++)sources.get(0).add(title("movie"+i,false));
        sources.get(1).add(title("tv0",true));sources.get(2).add(title("movie0",false));sources.get(3).add(title("watched",true));
        List<HeroRotationPolicy.Candidate<String>> fallback=Arrays.asList(title("movie9",false),title("resume",true));
        List<HeroRotationPolicy.Selection<String>> result=HeroRotationPolicy.select(sources,fallback,row->!row.identity.equals("watched")&&!row.identity.equals("resume"));
        assertEquals(10,result.size());assertEquals(1,result.stream().filter(row->row.candidate.television).count());
        assertFalse(result.stream().anyMatch(row->row.candidate.identity.equals("watched")||row.candidate.identity.equals("resume")));
        assertTrue(result.stream().anyMatch(row->row.candidate.identity.equals("movie9")&&row.source==4));
    }
    @Test public void fallbackSuppliesMissingBalanceBeforeQuotaAndSmallLibrariesStaySmall(){
        List<List<HeroRotationPolicy.Candidate<String>>> sources=sources();for(int i=0;i<12;i++)sources.get(0).add(title("movie"+i,false));
        List<HeroRotationPolicy.Candidate<String>> fallback=new ArrayList<>();for(int i=0;i<5;i++)fallback.add(title("tv"+i,true));
        assertEquals(5,HeroRotationPolicy.select(sources,fallback,row->true).stream().filter(row->row.candidate.television).count());
        List<List<HeroRotationPolicy.Candidate<String>>> tiny=sources();tiny.get(0).add(title("one",true));tiny.get(1).add(title("one",true));
        assertEquals(1,HeroRotationPolicy.select(tiny,Collections.emptyList(),row->true).size());
        assertTrue(HeroRotationPolicy.select(sources,Collections.emptyList(),row->false).isEmpty());
    }
    @Test public void thirtySecondIdleEightSecondCadenceAndRemoteResetAreExact(){
        HeroRotationPolicy.Clock clock=new HeroRotationPolicy.Clock();clock.resume(100);
        assertFalse(clock.due(30099,true,true,10));assertTrue(clock.due(30100,true,true,10));
        assertFalse(clock.due(38099,true,true,10));assertTrue(clock.due(38100,true,true,10));
        clock.input(39000);assertFalse(clock.due(46000,true,true,10));assertTrue(clock.due(69000,true,true,10));
    }
    @Test public void lifecycleHiddenHeroReducedMotionAndSingleTitleNeverAutoAdvance(){
        HeroRotationPolicy.Clock clock=new HeroRotationPolicy.Clock();clock.resume(0);clock.suspend();assertFalse(clock.due(30000,true,true,10));
        clock.resume(40000);assertFalse(clock.due(70000,false,true,10));assertFalse(clock.due(70000,true,false,10));assertFalse(clock.due(70000,true,true,1));
        assertTrue(clock.due(70000,true,true,10));clock.resume(80000);assertFalse(clock.due(90000,true,true,10));
    }
}
