package com.archos.mediacenter.video.leanback;

import android.app.Application;
import android.net.Uri;
import java.util.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import static org.junit.Assert.*;

@RunWith(RobolectricTestRunner.class) @Config(application=Application.class,sdk=28)
public class PreviewMediaClassificationTest {
    @Test public void sourceHintClassifiesButDoesNotInventIdentity(){Map<Uri,PreviewMediaClassification.Kind> hints=new HashMap<>();hints.put(Uri.parse("smb://nas/TV"),PreviewMediaClassification.Kind.TV);assertEquals(PreviewMediaClassification.Kind.TV,PreviewMediaClassification.classify("Unknown.mkv",Uri.parse("smb://nas/TV/Unknown.mkv"),hints));}
    @Test public void pathPrefixMustBeARealFolderBoundary(){Map<Uri,PreviewMediaClassification.Kind> hints=new HashMap<>();hints.put(Uri.parse("smb://nas/TV"),PreviewMediaClassification.Kind.TV);assertEquals(PreviewMediaClassification.Kind.UNKNOWN,PreviewMediaClassification.classify("Unknown.mkv",Uri.parse("smb://nas/TVExtra/Unknown.mkv"),hints));}
    @Test public void mostSpecificExplicitFolderWins(){Map<Uri,PreviewMediaClassification.Kind> hints=new HashMap<>();hints.put(Uri.parse("smb://nas/Media"),PreviewMediaClassification.Kind.MOVIE);hints.put(Uri.parse("smb://nas/Media/TV"),PreviewMediaClassification.Kind.TV);assertEquals(PreviewMediaClassification.Kind.TV,PreviewMediaClassification.classify("Unknown.mkv",Uri.parse("smb://nas/Media/TV/Unknown.mkv"),hints));}
    @Test public void unknownRemainsSharedAndEpisodeHintIsConfident(){assertEquals(PreviewMediaClassification.Kind.UNKNOWN,PreviewMediaClassification.classify("Ambiguous.mkv"));assertEquals(PreviewMediaClassification.Kind.TV,PreviewMediaClassification.classify("Series.S02E03.mkv"));}
}
