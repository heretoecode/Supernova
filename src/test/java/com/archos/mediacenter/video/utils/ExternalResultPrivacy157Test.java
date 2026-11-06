package com.archos.mediacenter.video.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.Mockito.mockConstruction;
import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.net.Uri;
import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import com.archos.mediacenter.utils.videodb.IndexHelper;
import com.archos.mediacenter.utils.videodb.VideoDbInfo;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import org.robolectric.util.ReflectionHelpers;
import org.slf4j.LoggerFactory;

@RunWith(RobolectricTestRunner.class)
@Config(application = Application.class, sdk = 30)
public class ExternalResultPrivacy157Test
{
    @Test public void rawLocationsAndArbitraryResultExtrasNeverReachAnAppender()
    {
        Logger logger = (Logger)LoggerFactory.getLogger(ExternalPlayerResultListener.class);
        Level previous = logger.getLevel();
        ListAppender<ILoggingEvent> captured = new ListAppender<>();
        captured.start();
        logger.addAppender(captured);
        logger.setLevel(Level.DEBUG);
        Uri original = Uri.parse("https://PRIVATE_USER:PRIVATE_PASSWORD@PRIVATE_HOST/PRIVATE_TITLE"
                                 + "?token=PRIVATE_TOKEN#PRIVATE_FRAGMENT");
        try (var helpers = mockConstruction(IndexHelper.class))
        {
            ExternalPlayerResultListener listener = new ExternalPlayerResultListener();
            listener.init(RuntimeEnvironment.getApplication(), original, original, new VideoDbInfo(original));
            listener.onActivityResult(1, Activity.RESULT_OK, new Intent().putExtra("PRIVATE_KEY", "PRIVATE_VALUE")
                    .putExtra("end_by", "PRIVATE_TEXT").putExtra("position", 100));
            assertEquals(original, ReflectionHelpers.getField(listener, "mPlayerUri"));
            assertFalse(captured.list.isEmpty());
            for (ILoggingEvent event : captured.list)
            {
                assertFalse(event.getFormattedMessage().contains("PRIVATE_"));
                if (event.getArgumentArray() != null)
                    for (Object argument : event.getArgumentArray())
                        assertFalse(String.valueOf(argument).contains("PRIVATE_"));
            }
        }
        finally
        {
            logger.detachAppender(captured);
            logger.setLevel(previous);
            captured.stop();
        }
    }
}
