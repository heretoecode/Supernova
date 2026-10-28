package com.archos.mediacenter.video.leanback;

import android.app.Application;
import com.archos.filecorelibrary.*;
import com.archos.mediaprovider.video.FileVisitor;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import java.util.*;
import java.io.IOException;
import static org.mockito.Mockito.*;

@RunWith(RobolectricTestRunner.class) @Config(application=Application.class,sdk=28)
public class WebdavTraversalCompletenessTest {
    private FileVisitor.Listener listener(){FileVisitor.Listener l=mock(FileVisitor.Listener.class);when(l.onDirectory(any())).thenReturn(true);when(l.onFilesList(any())).thenReturn(true);return l;}
    private MetaFile2 directory(RawLister lister){MetaFile2 file=mock(MetaFile2.class);when(file.isDirectory()).thenReturn(true);when(file.getRawListerInstance()).thenReturn(lister);return file;}
    @Test public void emptySuccessfulListingIsComplete()throws Exception{
        RawLister raw=mock(RawLister.class);when(raw.getFileList()).thenReturn(Collections.emptyList());MetaFile2 root=directory(raw);FileVisitor.Listener l=listener();
        FileVisitor.visit(root,10,l);verify(l,never()).onListingError(any(),any());verify(l).onStop(root);
    }
    @Test public void nullListingAndNetworkFailureAreIncomplete()throws Exception{
        for(boolean timeout:new boolean[]{false,true}){RawLister raw=mock(RawLister.class);if(timeout)when(raw.getFileList()).thenThrow(new IOException("timeout"));else when(raw.getFileList()).thenReturn(null);MetaFile2 root=directory(raw);FileVisitor.Listener l=listener();FileVisitor.visit(root,10,l);verify(l).onListingError(eq(root),any());verify(l).onStop(root);}
    }
    @Test public void depthLimitAndStoppedTraversalCannotAuthorizePruning()throws Exception{
        MetaFile2 root=directory(mock(RawLister.class));FileVisitor.Listener depth=listener();FileVisitor.visit(root,0,depth);verify(depth).onListingError(eq(root),any());
        FileVisitor.Listener stopped=listener();when(stopped.onDirectory(root)).thenReturn(false);FileVisitor.visit(root,10,stopped);verify(stopped).onListingError(eq(root),any());
    }
}
