package com.archos.mediacenter.video.player;

import android.app.Service;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Binder;
import android.os.Parcel;
import android.os.RemoteException;
import android.os.ParcelFileDescriptor;
import android.os.Process;
import com.archos.filecorelibrary.FileUtilsQ;
import com.archos.filecorelibrary.MetaFile2;
import com.archos.filecorelibrary.NetworkInitialization;
import com.archos.filecorelibrary.jcifs.JcifsUtils;
import com.archos.filecorelibrary.samba.NetworkCredentialsDatabase;
import com.archos.filecorelibrary.smbj.SmbjUtils;
import com.archos.filecorelibrary.sshj.SshjUtils;
import com.archos.filecorelibrary.webdav.WebdavUtils;
import com.archos.mediacenter.filecoreextension.upnp2.RawListerFactoryWithUpnp;
import com.archos.mediacenter.filecoreextension.upnp2.MetaFileFactoryWithUpnp;
import com.archos.mediacenter.filecoreextension.upnp2.UpnpServiceManager;
import java.io.DataOutputStream;
import java.util.List;

/** Exactly one operation in a private process: all protocol caches, DNS and UPnP requests belong to that operation. */
public final class NextListingService extends Service
{
    static final int START = 1, STOP = 2;
    private String token;
    static final String DESCRIPTOR = "app.supernova.player.OWNED_NEXT_LISTING";
    private final IBinder binder = new Binder() {
        @Override protected boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException
        {
            if (code == INTERFACE_TRANSACTION)
            {
                if (reply != null)
                    reply.writeString(DESCRIPTOR);
                return true;
            }
            data.enforceInterface(DESCRIPTOR);
            if (Binder.getCallingUid() != Process.myUid())
                return false;
            return receive(code, data.readBundle(NextListingService.class.getClassLoader()));
        }
    };

    @Override public void onCreate()
    {
        super.onCreate();
        if (!NextListingProcess.bootstrap(this))
            throw new IllegalStateException("Listing service must run in its private process");
    }

    @Override public IBinder onBind(Intent intent)
    {
        return binder;
    }

    private synchronized boolean receive(int code, Bundle data)
    {
        if (data == null)
            return false;
        String requested = data.getString("token");
        if (code == STOP && (token == null || token.equals(requested)))
        {
            // Runs on Binder's thread, so a blocked main looper cannot keep obsolete network I/O alive.
            // This process has no playback client or another listing. The caller never receives its PID.
            Process.killProcess(Process.myPid());
            return true;
        }
        if (code != START || token != null || requested == null)
            return true;
        token = requested;
        ParcelFileDescriptor output = data.getParcelable("output");
        if (output == null)
            return true;
        UpnpServiceManager.useOwnedProcessService(NextListingUpnpService.class);
        new Thread(() -> list(data, output), "OwnedNextListing").start();
        return true;
    }

    private void list(Bundle request, ParcelFileDescriptor descriptor)
    {
        try (DataOutputStream output = new DataOutputStream(
                 new ParcelFileDescriptor.AutoCloseOutputStream(descriptor)))
        {
            NetworkInitialization.awaitReady(this);
            if (request.getString("credential_uri") != null)
                NetworkCredentialsDatabase.getInstance().addCredential(new NetworkCredentialsDatabase.Credential(
                    request.getString("user"), request.getString("password"), request.getString("credential_uri"),
                    request.getString("domain"), true));
            JcifsUtils.getInstance(this);
            WebdavUtils.getInstance(this);
            SmbjUtils.getInstance(this);
            SshjUtils.getInstance(this);
            FileUtilsQ.getInstance(this);
            Uri uri = Uri.parse(request.getString("uri"));
            boolean inspect = request.getBoolean("inspect");
            boolean sizes = request.getBoolean("sizes");
            MetaFile2 file = inspect || sizes ? MetaFileFactoryWithUpnp.getMetaFileForUrl(uri) : null;
            if ((inspect || sizes) && file == null || sizes && !inspect && !file.isDirectory())
                throw new java.io.IOException("File or folder unavailable");
            List<MetaFile2> files = inspect ? List.of(file)
                : RawListerFactoryWithUpnp.getRawListerForUrl(uri).getFileList();
            NextListingWire.write(output, files, sizes);
        }
        catch (Exception | LinkageError unavailable)
        {
            // Closing the private pipe is the failure value. No URI, credentials or provider text is logged.
        }
        finally
        {
            request.clear();
        }
    }
}
