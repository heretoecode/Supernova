package com.archos.mediacenter.video.leanback;

import android.content.Context;
import android.net.Uri;
import android.widget.FrameLayout;
import com.archos.mediacenter.video.leanback.adapter.object.Box;
import com.archos.mediacenter.video.leanback.adapter.object.Shortcut;
import com.archos.mediacenter.video.leanback.filebrowsing.UniversalFileBrowser;
import java.util.*;
import java.util.function.Consumer;

/** Compatibility host. Every location now opens inside the universal browser. */
public final class PreviewNetworkWorkspace extends FrameLayout {
    private final UniversalFileBrowser browser;
    public PreviewNetworkWorkspace(Context c,List<Box> volumes,List<Shortcut> sources,List<Shortcut> saved,
            Consumer<Box> browseVolume,Consumer<String> browseNetwork) {
        super(c);browser=new UniversalFileBrowser(c);addView(browser,new LayoutParams(-1,-1));
        update(volumes,sources,saved,browseNetwork);
    }
    public void update(List<Box> volumes,List<Shortcut> sources,List<Shortcut> saved,Consumer<String> network){browser.locations(volumes,sources,saved,network);}
    public UniversalFileBrowser browser(){return browser;}
    public boolean atTop(){return browser.atTop();}
    static String scanStatus(Context c,boolean library){return library?PreviewLibraryScan.libraryStatus(c):PreviewLibraryScan.status(c);}
    public static String protocol(Uri uri){String scheme=uri.getScheme();if(scheme==null)return "Local storage";switch(scheme.toLowerCase(Locale.ROOT)){case "file":return "Local storage";case "webdavs":case "https":case "davs":return "WebDAV · HTTPS";case "webdav":case "http":case "dav":return "WebDAV · HTTP";case "smbj":case "smb":return "SMB";case "sshj":case "sftp":return "SFTP";case "ftps":return "FTP over TLS";case "ftp":return "FTP";default:return scheme.toUpperCase(Locale.ROOT);}}
}
