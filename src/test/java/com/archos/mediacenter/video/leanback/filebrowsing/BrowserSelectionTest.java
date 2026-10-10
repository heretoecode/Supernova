package com.archos.mediacenter.video.leanback.filebrowsing;
import android.app.Application;
import android.net.Uri;
import android.content.SharedPreferences;
import java.util.*;
import org.junit.*;
import org.junit.runner.RunWith;
import org.robolectric.*;
import org.robolectric.annotation.Config;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
@RunWith(RobolectricTestRunner.class) @Config(application=Application.class,sdk=28)
public class BrowserSelectionTest {
 @Test public void canonicalLocationsRemoveSecretsAndRespectDirectoryBoundaries(){
  assertEquals("smb://nas/movies",BrowserSelection.canonical(Uri.parse("SMB://user:password@NAS/movies/?token=secret#fragment")));
  assertEquals("file:///movies/kids",BrowserSelection.canonical(Uri.parse("file:///movies/x/%2e%2e/kids")));
  assertEquals("file:///movies/kids",BrowserSelection.canonical(Uri.parse("/movies/kids")));
  assertTrue(BrowserSelection.contains("file:///movies","file:///movies/kids"));assertFalse(BrowserSelection.contains("file:///movies","file:///movies2"));
 }
 @Test public void allChildrenMayBeExcludedWithoutUnmonitoringTheirParent(){
  BrowserSelection selections=new BrowserSelection(Set.of("file:///movies"),Set.of());
  selections.exclude(Uri.parse("file:///movies/one"));selections.exclude(Uri.parse("file:///movies/two.mkv"));
  assertEquals(Set.of("file:///movies"),selections.roots);assertFalse(selections.included(Uri.parse("file:///movies/one/a.mkv")));assertTrue(selections.included(Uri.parse("file:///movies/new.mkv")));
 }
 @Test public void parentAdditionCollapsesNestedRootsAndDiscardRestoresOriginal(){
  BrowserSelection selections=new BrowserSelection(Set.of("smb://nas/media/kids"),Set.of());selections.include(Uri.parse("smb://nas/media"));
  assertEquals(Set.of("smb://nas/media"),selections.roots);assertEquals(List.of("smb://nas/media/kids"),selections.removed());selections.discard();assertFalse(selections.changed());assertEquals(Set.of("smb://nas/media/kids"),selections.roots);
 }
 @Test public void failedCommitKeepsTransactionPendingAndSuccessfulCommitCheckpoints(){
  BrowserSelection selections=new BrowserSelection(Set.of(),Set.of());selections.include(Uri.parse("file:///movies"));
  SharedPreferences failure=mock(SharedPreferences.class);SharedPreferences.Editor editor=mock(SharedPreferences.Editor.class,RETURNS_SELF);when(failure.edit()).thenReturn(editor);when(editor.commit()).thenReturn(false);
  assertFalse(selections.persist(failure));assertTrue(selections.changed());
  SharedPreferences saved=RuntimeEnvironment.getApplication().getSharedPreferences("selections",0);assertTrue(selections.persist(saved));assertFalse(selections.changed());assertTrue(saved.getBoolean("supernova_library_policy_initialized",false));
 }
 @Test public void deepBreadcrumbsRetainEveryAncestorIncludingRoot(){
  String path="smb://nas";for(int i=0;i<50;i++)path+="/folder"+i;
  List<Uri> ancestors=UniversalFileBrowser.ancestors(Uri.parse(path));assertEquals(51,ancestors.size());assertEquals("smb://nas/",ancestors.get(0).toString());assertEquals(path,ancestors.get(50).toString());
 }
 @Test public void inventoryRefreshDoesNotUndoAnUnsavedRootRemoval(){
  BrowserSelection selection=new BrowserSelection(Set.of("smb://nas/Movies"),Set.of());selection.remove(Uri.parse("smb://nas/Movies"));selection.seedExisting(List.of(Uri.parse("smb://nas/Movies")));assertTrue(selection.changed());assertTrue(selection.roots.isEmpty());
 }
}
