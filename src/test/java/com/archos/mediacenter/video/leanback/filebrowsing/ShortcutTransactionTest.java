package com.archos.mediacenter.video.leanback.filebrowsing;
import android.app.Application;
import android.database.Cursor;
import android.net.Uri;
import com.archos.mediacenter.utils.ShortcutDbAdapter;
import java.util.*;
import org.junit.*;
import org.junit.runner.RunWith;
import org.robolectric.*;
import org.robolectric.annotation.Config;
import static org.junit.Assert.*;
@RunWith(RobolectricTestRunner.class) @Config(application=Application.class,sdk=28)
public class ShortcutTransactionTest {
 @Before public void cleanFixture(){ShortcutDbAdapter.VIDEO.close();org.robolectric.util.ReflectionHelpers.setField(ShortcutDbAdapter.VIDEO,"mDbHelper",null);RuntimeEnvironment.getApplication().deleteDatabase("shortcuts_db");}
 @Test public void failedPreferenceCommitRollsBackSourceIdsNamesAndPaths(){
  var c=RuntimeEnvironment.getApplication();var db=ShortcutDbAdapter.VIDEO;String original="SMB://NAS/Movies/";assertTrue(db.addShortcut(c,new ShortcutDbAdapter.Shortcut("Family Movies",original,null)));long id;
  try(Cursor row=db.getAllShortcuts(c,null,null)){assertTrue(row.moveToFirst());id=row.getLong(row.getColumnIndexOrThrow("_id"));}
  assertFalse(db.applySupernovaRoots(c,List.of("smb://nas/New"),List.of("smb://nas/Movies"),()->false));
  try(Cursor row=db.getAllShortcuts(c,null,null)){assertEquals(1,row.getCount());assertTrue(row.moveToFirst());assertEquals(id,row.getLong(row.getColumnIndexOrThrow("_id")));assertEquals(original,row.getString(row.getColumnIndexOrThrow("path")));assertEquals("Family Movies",row.getString(row.getColumnIndexOrThrow("name")));}
 }
 @Test public void canonicalExistingRootIsNotDuplicatedAndMissingRemovalFailsClosed(){
  var c=RuntimeEnvironment.getApplication();var db=ShortcutDbAdapter.VIDEO;assertTrue(db.addShortcut(c,new ShortcutDbAdapter.Shortcut("Movies","SMB://NAS/Movies/",null)));int[] commits={0};
  assertTrue(db.applySupernovaRoots(c,List.of("smb://nas/Movies"),List.of(),()->{commits[0]++;return true;}));
  try(Cursor rows=db.getAllShortcuts(c,null,null)){assertEquals(1,rows.getCount());}
  assertFalse(db.applySupernovaRoots(c,List.of("smb://nas/New"),List.of("smb://nas/Absent"),()->{commits[0]++;return true;}));assertEquals(1,commits[0]);
 }
}
