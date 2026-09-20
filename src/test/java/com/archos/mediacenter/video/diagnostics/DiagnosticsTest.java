package com.archos.mediacenter.video.diagnostics;

import android.app.Application;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.*;
import org.robolectric.annotation.Config;
import static org.junit.Assert.*;

@RunWith(RobolectricTestRunner.class) @Config(application=Application.class,sdk=28)
public class DiagnosticsTest {
    @Test public void generatedFocusControlsAreDistinctWithoutText()throws Exception{
        Application c=RuntimeEnvironment.getApplication();android.widget.LinearLayout parent=new android.widget.LinearLayout(c);android.widget.TextView a=new android.widget.TextView(c),b=new android.widget.TextView(c);a.setText("private title");b.setText("private query");parent.addView(a);parent.addView(b);
        java.lang.reflect.Method id=Diagnostics.class.getDeclaredMethod("viewId",android.view.View.class);id.setAccessible(true);String first=(String)id.invoke(null,a),second=(String)id.invoke(null,b);assertNotEquals(first,second);assertFalse(first.contains("private"));assertFalse(second.contains("private"));
    }
    @Test public void rotatedFilesStayBoundedAndSessionsRemainCorrelated()throws Exception{
        Application c=RuntimeEnvironment.getApplication();Diagnostics.setEnabled(c,true);
        java.lang.reflect.Method write=Diagnostics.class.getDeclaredMethod("write",String.class,String.class);write.setAccessible(true);
        String payload=new String(new char[4000]).replace('\0','x');
        for(int i=0;i<500;i++)write.invoke(null,"{\"session\":\"fixture-session\",\"event\":\"bounded_test\",\"detail\":\""+payload+"\"}\n","fixture-session");
        java.io.File[] files=new java.io.File(c.getFilesDir(),"supernova-diagnostics").listFiles();assertNotNull(files);assertTrue(files.length<=7);
        for(java.io.File file:files)assertTrue(file.length()<=Diagnostics.LIMIT);
        java.io.ByteArrayOutputStream bytes=new java.io.ByteArrayOutputStream();Diagnostics.export(c,bytes);boolean playback=false;
        try(java.util.zip.ZipInputStream zip=new java.util.zip.ZipInputStream(new java.io.ByteArrayInputStream(bytes.toByteArray()))){java.util.zip.ZipEntry entry;while((entry=zip.getNextEntry())!=null){if(entry.getName().equals("playback.jsonl")){java.io.ByteArrayOutputStream content=new java.io.ByteArrayOutputStream();byte[] buffer=new byte[8192];int count;while((count=zip.read(buffer))!=-1)content.write(buffer,0,count);assertTrue(content.toString("UTF-8").contains("fixture-session"));playback=true;}}}
        assertTrue(playback);Diagnostics.setEnabled(c,false);
    }
    @Test public void locationsAndInlineSecretsAreRemoved(){String value=Diagnostics.safe("https://user:password@example/private?token=secret /storage/private.mkv token=abc Bearer xyz");assertFalse(value.contains("example"));assertFalse(value.contains("private"));assertFalse(value.contains("abc"));assertFalse(value.contains("xyz"));}
    @Test public void exceptionMessagesNeverEnterReport(){RuntimeException error=new RuntimeException("password=extremely-private");String trace=Diagnostics.trace(error);assertTrue(trace.contains("RuntimeException"));assertFalse(trace.contains("extremely-private"));}
    @Test public void offByDefaultAndExportWorksWhileOff()throws Exception{Application c=RuntimeEnvironment.getApplication();assertFalse(androidx.preference.PreferenceManager.getDefaultSharedPreferences(c).getBoolean(Diagnostics.KEY,false));Diagnostics.setEnabled(c,false);java.io.ByteArrayOutputStream bytes=new java.io.ByteArrayOutputStream();Diagnostics.export(c,bytes);assertTrue(bytes.size()>0);java.util.Set<String> names=new java.util.HashSet<>();try(java.util.zip.ZipInputStream zip=new java.util.zip.ZipInputStream(new java.io.ByteArrayInputStream(bytes.toByteArray()))){java.util.zip.ZipEntry entry;while((entry=zip.getNextEntry())!=null)names.add(entry.getName());}assertTrue(names.contains("build-device.json"));assertTrue(names.contains("decoders.txt"));assertTrue(names.contains("README.txt"));}
}
