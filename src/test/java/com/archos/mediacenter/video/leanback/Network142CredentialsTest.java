package com.archos.mediacenter.video.leanback;
import android.app.Application;
import com.archos.filecorelibrary.samba.NetworkCredentialsDatabase;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.annotation.Config;
import static org.junit.Assert.*;
@RunWith(org.robolectric.RobolectricTestRunner.class) @Config(application=Application.class,sdk=28)
public class Network142CredentialsTest {
 @Test public void emailUsernamesAreIntactForEveryNonSmbTransport(){for(String scheme:new String[]{"webdav","webdavs","sftp","sshj","ftp","ftps"}){var c=new NetworkCredentialsDatabase.Credential("person@example.test","  fixture  ",scheme+"://host/path","",true);assertEquals(scheme,"person@example.test",c.getUsername());assertEquals("",c.getDomain());assertEquals("  fixture  ",c.getPassword());}}
 @Test public void smbDomainConventionsRemainSupported(){var at=new NetworkCredentialsDatabase.Credential("person@DOMAIN","fixture","smbj://host/share","",true);assertEquals("person",at.getUsername());assertEquals("DOMAIN",at.getDomain());var slash=new NetworkCredentialsDatabase.Credential("DOMAIN\\person","fixture","smb://host/share","",true);assertEquals("person",slash.getUsername());assertEquals("DOMAIN",slash.getDomain());}
 @Test public void hostCaseMatchesWithoutBroadeningPathOrScheme(){NetworkCredentialsDatabase db=new NetworkCredentialsDatabase();var c=new NetworkCredentialsDatabase.Credential("fixture","fixture","webdavs://NAS/Media","",true);db.addCredential(c);assertSame(c,db.getCredential("webdavs://nas/Media/Film"));assertSame(c,db.getCredential("webdavs://nas/Media"));assertNull(db.getCredential("webdav://nas/Media"));assertNull(db.getCredential("webdavs://nas/media"));assertNull(db.getCredential("webdavs://nas/MediaOther"));assertEquals("webdavs://NAS/Media",c.getUriString());}
}
