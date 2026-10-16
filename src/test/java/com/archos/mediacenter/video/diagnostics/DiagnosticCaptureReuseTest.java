package com.archos.mediacenter.video.diagnostics;

import android.app.Application;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.*;
import org.robolectric.annotation.Config;
import org.robolectric.util.ReflectionHelpers;
import static org.junit.Assert.*;

@RunWith(RobolectricTestRunner.class) @Config(application=Application.class,sdk=28)
public class DiagnosticCaptureReuseTest {
    @Test public void repeatedAnomaliesShareWindowButManualMarkerGetsFreshCapture(){Application app=RuntimeEnvironment.getApplication();Diagnostics.setEnabled(app,true);try{Diagnostics.event("focus_anomaly_suspected","kind","fixture");Object first=ReflectionHelpers.getStaticField(Diagnostics.class,"captureFlight");assertNotNull(first);Diagnostics.event("focus_anomaly_suspected","kind","fixture");assertSame(first,ReflectionHelpers.getStaticField(Diagnostics.class,"captureFlight"));Diagnostics.event("manual_problem_marker");assertNotSame(first,ReflectionHelpers.getStaticField(Diagnostics.class,"captureFlight"));}finally{Diagnostics.setEnabled(app,false);}assertNull(ReflectionHelpers.getStaticField(Diagnostics.class,"captureFlight"));}
}
