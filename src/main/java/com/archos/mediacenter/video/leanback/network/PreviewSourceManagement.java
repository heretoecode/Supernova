package com.archos.mediacenter.video.leanback.network;
import android.app.Activity;
import android.view.View;
import android.net.Uri;
import com.archos.mediacenter.utils.ShortcutDbAdapter;
import com.archos.mediacenter.video.leanback.adapter.object.Shortcut;
import com.archos.mediacenter.video.leanback.filebrowsing.PreviewBrowserSurface;
import java.util.*;
/** Legacy source-detail entry adapts directly into the same browser and staged selection transaction. */
final class PreviewSourceManagement {
 static View create(Activity activity,Shortcut source){
  PreviewBrowserSurface surface=new PreviewBrowserSurface(activity,null,source.getUri(),()->{});
  boolean indexed=ShortcutDbAdapter.VIDEO.isShortcut(activity,source.getUri().toString())>0;
  surface.browser().locations(Collections.emptyList(),indexed?Collections.singletonList(source):Collections.emptyList(),indexed?Collections.emptyList():Collections.singletonList(source),kind->com.archos.mediacenter.video.leanback.PreviewDialog.read(activity,"Connect a source","Open Network & Files to add a connection."));
  surface.browser().open(source.getUri());return surface;
 }
 private PreviewSourceManagement(){}
}
