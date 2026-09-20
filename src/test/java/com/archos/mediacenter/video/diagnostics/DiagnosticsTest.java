package com.archos.mediacenter.video.diagnostics;

import android.app.Application;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.*;
import org.robolectric.annotation.Config;
import static org.junit.Assert.*;

@RunWith(RobolectricTestRunner.class) @Config(application=Application.class,sdk=28)
public class DiagnosticsTest {
    @Test public void locationsAndInlineSecretsAreRemoved(){String value=Diagnostics.safe("https://user:password@example/private?token=secret /storage/private.mkv token=abc Bearer xyz");assertFalse(value.contains("example"));assertFalse(value.contains("private"));assertFalse(value.contains("abc"));assertFalse(value.contains("xyz"));}
    @Test public void exceptionMessagesNeverEnterReport(){RuntimeException error=new RuntimeException("password=extremely-private");String trace=Diagnostics.trace(error);assertTrue(trace.contains("RuntimeException"));assertFalse(trace.contains("extremely-private"));}
    @Test public void offByDefaultAndExportWorksWhileOff()throws Exception{Application c=RuntimeEnvironment.getApplication();assertFalse(androidx.preference.PreferenceManager.getDefaultSharedPreferences(c).getBoolean(Diagnostics.KEY,false));Diagnostics.setEnabled(c,false);java.io.ByteArrayOutputStream bytes=new java.io.ByteArrayOutputStream();Diagnostics.export(c,bytes);assertTrue(bytes.size()>0);java.util.Set<String> names=new java.util.HashSet<>();try(java.util.zip.ZipInputStream zip=new java.util.zip.ZipInputStream(new java.io.ByteArrayInputStream(bytes.toByteArray()))){java.util.zip.ZipEntry entry;while((entry=zip.getNextEntry())!=null)names.add(entry.getName());}assertTrue(names.contains("build-device.json"));assertTrue(names.contains("decoders.txt"));assertTrue(names.contains("README.txt"));}
}
