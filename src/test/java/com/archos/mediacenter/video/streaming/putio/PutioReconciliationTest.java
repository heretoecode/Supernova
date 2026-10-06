package com.archos.mediacenter.video.streaming.putio;

import java.util.*;
import org.junit.Test;
import static org.junit.Assert.*;
import static com.archos.mediacenter.video.streaming.putio.PutioReconciliation.*;

public class PutioReconciliationTest {
    private static Snapshot complete(File... files){Snapshot s=new Snapshot("account:folder","root");s.page("root",Arrays.asList(files),Collections.emptyList());assertTrue(s.finish());return s;}
    @Test public void exactPathAndKnownSizeAssociatesExistingRecord(){Plan p=plan(complete(new File(42,1,"Films/A.mkv",100)),Arrays.asList(new Existing(9,"Films/A.mkv",100,0)));assertEquals(Decision.LINK_EXISTING,p.changes.get(0).decision);assertEquals(9,p.changes.get(0).mediaId);}
    @Test public void stableIdSurvivesRenameAndMove(){Plan p=plan(complete(new File(42,99,"New/B.mkv",100)),Arrays.asList(new Existing(9,"Old/A.mkv",100,42)));assertEquals(Decision.ALREADY_LINKED,p.changes.get(0).decision);assertEquals(9,p.changes.get(0).mediaId);assertTrue(p.missingMediaIds.isEmpty());}
    @Test public void ambiguousFilenameRequiresReview(){Plan p=plan(complete(new File(42,1,"New/A.mkv",100)),Arrays.asList(new Existing(9,"Old/A.mkv",100,0)));assertEquals(Decision.NEEDS_REVIEW,p.changes.get(0).decision);}
    @Test public void duplicatePathsCannotAutoAssociate(){Plan p=plan(complete(new File(42,1,"A.mkv",100)),Arrays.asList(new Existing(9,"A.mkv",100,0),new Existing(10,"A.mkv",100,0)));assertEquals(Decision.NEEDS_REVIEW,p.changes.get(0).decision);}
    @Test public void unknownSizeCannotAutoAssociate(){Plan p=plan(complete(new File(42,1,"A.mkv",0)),Arrays.asList(new Existing(9,"A.mkv",0,0)));assertEquals(Decision.NEEDS_REVIEW,p.changes.get(0).decision);}
    @Test public void allFailureModesPreserveLibrary(){for(Failure failure:Failure.values()){if(failure==Failure.NONE)continue;Snapshot s=complete(new File(42,1,"A.mkv",100));s.fail(failure);Plan p=plan(s,Arrays.asList(new Existing(9,"Missing.mkv",100,55)));assertFalse(p.complete);assertTrue(p.changes.isEmpty());assertTrue(p.missingMediaIds.isEmpty());}}
    @Test public void paginationMustFinishEveryDescendant(){Snapshot s=new Snapshot("account:folder","root");s.page("root",Collections.emptyList(),Arrays.asList("root:page2","child:page1"));assertFalse(s.finish());s.page("root:page2",Collections.emptyList(),Collections.emptyList());assertFalse(s.finish());s.page("child:page1",Arrays.asList(new File(42,2,"Child/A.mkv",100)),Collections.emptyList());assertTrue(s.finish());assertEquals(1,s.files().size());}
    @Test public void repeatedCursorInvalidatesSnapshot(){Snapshot s=new Snapshot("account:folder","root");s.page("root",Collections.emptyList(),Arrays.asList("root"));assertEquals(Failure.INVALID_PAGE,s.failure());assertFalse(s.finish());}
    @Test public void duplicateFileIdInvalidatesSnapshot(){Snapshot s=new Snapshot("account:folder","root");s.page("root",Arrays.asList(new File(42,1,"A.mkv",100),new File(42,1,"B.mkv",100)),Collections.emptyList());assertFalse(s.finish());}
    @Test public void completedAbsenceIsReviewOnly(){Plan p=plan(complete(),Arrays.asList(new Existing(9,"A.mkv",100,42)));assertTrue(p.complete);assertEquals(Arrays.asList(9L),p.missingMediaIds);assertTrue(p.changes.isEmpty());}
    @Test public void changedSizeRequiresReview(){Plan p=plan(complete(new File(42,1,"A.mkv",101)),Arrays.asList(new Existing(9,"A.mkv",100,0)));assertEquals(Decision.NEEDS_REVIEW,p.changes.get(0).decision);}
    @Test public void competingClaimsBothRequireReview(){Plan p=plan(complete(new File(42,1,"A.mkv",100),new File(43,1,"A.mkv",100)),Arrays.asList(new Existing(9,"A.mkv",100,0)));assertEquals(Decision.NEEDS_REVIEW,p.changes.get(0).decision);assertEquals(Decision.NEEDS_REVIEW,p.changes.get(1).decision);}
    @Test(expected=IllegalArgumentException.class) public void pathTraversalIsRejected(){new File(42,1,"../A.mkv",100);}
}
