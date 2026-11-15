package com.archos.mediacenter.video.player;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockConstruction;
import android.app.Application;
import android.content.ContentResolver;
import android.net.Uri;
import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import com.archos.mediacenter.filecoreextension.upnp2.StreamUriFinder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.util.ReflectionHelpers;
import org.slf4j.LoggerFactory;

@RunWith(RobolectricTestRunner.class)
@Config(application = Application.class, sdk = 30)
public class NextListingLogPrivacyTest
{
    @Test public void serviceSourceLoggingRedactsArgumentsBeforeAnyAppenderSeesThem() throws Exception
    {
        Logger logger = (Logger)LoggerFactory.getLogger(PlayerService.class);
        Level previous = logger.getLevel();
        ListAppender<ILoggingEvent> captured = new ListAppender<>();
        captured.start();
        logger.addAppender(captured);
        logger.setLevel(Level.DEBUG);
        Uri original = Uri.parse("upnp://PRIVATE_USER:PRIVATE_PASSWORD@PRIVATE_HOST/PRIVATE_TITLE"
                                 + "?token=PRIVATE_TOKEN");
        PlayerService service = new PlayerService();
        ReflectionHelpers.setField(service, "mUri", original);
        try (var construction = mockConstruction(StreamUriFinder.class))
        {
            ReflectionHelpers.callInstanceMethod(service, "onDataUriOK");
            assertEquals(original, service.getStreamingUri());
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

    @Test public void originalUriRemainsUntouchedButNoAppenderReceivesTheAddressOrCredentials() throws Exception
    {
        Logger logger = (Logger)LoggerFactory.getLogger(UpdateNextTask.class);
        Level previous = logger.getLevel();
        ListAppender<ILoggingEvent> captured = new ListAppender<>();
        captured.start();
        logger.addAppender(captured);
        logger.setLevel(Level.DEBUG);
        try
        {
            Uri original = Uri.parse("https://PRIVATE_USER:PRIVATE_PASSWORD@PRIVATE_HOST/PRIVATE_TITLE"
                                     + "?token=PRIVATE_TOKEN#PRIVATE_FRAGMENT");
            UpdateNextTask task = new UpdateNextTask(mock(ContentResolver.class), null, original, null, 0, -1);
            task.execute(false, false);
            assertTrue(task.awaitStopped(3000));
            assertEquals(original, ReflectionHelpers.getField(task, "mUri"));
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
