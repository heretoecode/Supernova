package com.archos.mediacenter.video.utils.backup;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.DocumentsContract;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.io.OutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.function.BooleanSupplier;

/** Prepare before opening a picker; success requires a closed destination and exact readable copy. */
public final class VerifiedBackupPublication
{
    public interface Destination
    {
        OutputStream openWrite() throws IOException;
        InputStream openRead() throws IOException;
        /** Delete only a document known to have been created for this export. Never a pre-existing document. */
        boolean removeIncomplete() throws IOException;
    }
    public enum Cleanup
    {
        NOT_NEEDED,
        REMOVED,
        LEFT_UNVERIFIED
    }
    private static final class Cancelled extends InterruptedIOException
    {
        Cancelled()
        {
            super("Backup publication cancelled");
        }
    }
    public static final class Failure extends IOException
    {
        public final Cleanup cleanup;
        public final boolean cancelled;
        public final long bytesWritten;
        private Failure(IOException cause, Cleanup cleanup, long bytesWritten)
        {
            super("Backup destination was not verified; the private verified archive is retained", cause);
            this.cleanup = cleanup;
            this.bytesWritten = bytesWritten;
            this.cancelled = cause instanceof Cancelled;
        }
    }
    public static final class Prepared
    {
        private final File archive;
        private final byte[] digest;
        public final long bytes;
        private Prepared(File archive, byte[] digest)
        {
            this.archive = archive;
            this.digest = digest;
            bytes = archive.length();
        }
        /** Remains available for retry after provider failure. The caller owns bounded private retention. */
        public File privateArchive()
        {
            return archive;
        }
    }
    private static void cancelled(BooleanSupplier cancel) throws InterruptedIOException
    {
        if (cancel.getAsBoolean() || Thread.currentThread().isInterrupted())
            throw new Cancelled();
    }
    private static MessageDigest digest() throws IOException
    {
        try
        {
            return MessageDigest.getInstance("SHA-256");
        }
        catch (NoSuchAlgorithmException e)
        {
            throw new IOException("Backup digest unavailable", e);
        }
    }
    private static byte[] hash(InputStream input, long expected, BooleanSupplier cancel) throws IOException
    {
        MessageDigest result = digest();
        byte[] buffer = new byte[65536];
        long size = 0;
        int n;
        while ((n = input.read(buffer)) != -1)
        {
            cancelled(cancel);
            size += n;
            if (size > expected)
                throw new IOException("Backup copy has unexpected trailing data");
            result.update(buffer, 0, n);
        }
        if (size != expected)
            throw new IOException("Backup copy is incomplete");
        return result.digest();
    }
    public static Prepared prepare(File privateArchive, BooleanSupplier cancel) throws IOException
    {
        return BackupDiagnostics.run("export", "verify", () -> prepareInternal(privateArchive, cancel));
    }

    private static Prepared prepareInternal(File privateArchive, BooleanSupplier cancel) throws IOException
    {
        BackupDiagnostics.stage("verify", null, 0, privateArchive.length());
        cancelled(cancel);
        if (!privateArchive.isFile() || privateArchive.length() <= 0 ||
            privateArchive.length() > LogicalBackupArchive.MAX_COMPRESSED)
            throw new IOException("Private archive exceeds supported limits");
        LogicalBackupArchive.readCategories(privateArchive, cancel);
        try (InputStream input = new FileInputStream(privateArchive))
        {
            byte[] hash = hash(input, privateArchive.length(), cancel);
            cancelled(cancel);
            return new Prepared(privateArchive, hash);
        }
    }
    /** No atomic-rename promise: a provider may expose an incomplete document while this is running. */
    public static long publish(Prepared prepared, Destination destination, BooleanSupplier cancel) throws Failure
    {
        try
        {
            return BackupDiagnostics.run("export", "verify", () -> publishInternal(prepared, destination, cancel));
        }
        catch (Failure failure)
        {
            throw failure;
        }
        catch (IOException impossible)
        {
            throw new Failure(impossible, Cleanup.NOT_NEEDED, 0);
        }
    }

    private static long publishInternal(Prepared prepared, Destination destination, BooleanSupplier cancel)
        throws Failure
    {
        boolean opened = false;
        long written = 0;
        try
        {
            cancelled(cancel);
            if (prepared.archive.length() != prepared.bytes)
                throw new IOException("Private archive changed after preparation");
            try (InputStream input = new FileInputStream(prepared.archive))
            {
                if (!MessageDigest.isEqual(prepared.digest, hash(input, prepared.bytes, cancel)))
                    throw new IOException("Private archive changed after preparation");
            }
            cancelled(cancel);
            BackupDiagnostics.stage("copy", null, 0, 0);
            opened = true;
            try (OutputStream output = destination.openWrite();
                 InputStream input = new FileInputStream(prepared.archive))
            {
                if (output == null)
                    throw new IOException("Document provider did not open the destination");
                byte[] buffer = new byte[65536];
                int n;
                while ((n = input.read(buffer)) != -1)
                {
                    cancelled(cancel);
                    if (written + n > prepared.bytes)
                        throw new IOException("Private archive changed during publication");
                    output.write(buffer, 0, n);
                    written += n;
                    BackupDiagnostics.stage("copy", null, 0, written);
                }
                if (written != prepared.bytes)
                    throw new IOException("Private archive changed during publication");
                cancelled(cancel);
                output.flush();
            }
            cancelled(cancel);
            BackupDiagnostics.stage("verify", null, 0, written);
            try (InputStream input = destination.openRead())
            {
                if (input == null || !MessageDigest.isEqual(prepared.digest, hash(input, prepared.bytes, cancel)))
                    throw new IOException("Document provider readback verification failed");
            }
            cancelled(cancel);
            return written;
        }
        catch (IOException failure)
        {
            Cleanup cleanup = Cleanup.NOT_NEEDED;
            if (opened)
            {
                try
                {
                    cleanup = destination.removeIncomplete() ? Cleanup.REMOVED : Cleanup.LEFT_UNVERIFIED;
                }
                catch (IOException | RuntimeException ignored)
                {
                    cleanup = Cleanup.LEFT_UNVERIFIED;
                }
            }
            throw new Failure(failure, cleanup, written);
        }
        catch (RuntimeException failure)
        {
            Cleanup cleanup = Cleanup.NOT_NEEDED;
            if (opened)
            {
                try
                {
                    cleanup = destination.removeIncomplete() ? Cleanup.REMOVED : Cleanup.LEFT_UNVERIFIED;
                }
                catch (IOException | RuntimeException ignored)
                {
                    cleanup = Cleanup.LEFT_UNVERIFIED;
                }
            }
            throw new Failure(new IOException("Document provider operation failed", failure), cleanup, written);
        }
    }
    /** Picker integration must establish ownership separately; choosing an existing file is not creation proof. */
    public static Destination saf(ContentResolver resolver, Uri document, boolean createdForThisExport)
    {
        return new Destination() {
            @Override public OutputStream openWrite() throws IOException
            {
                return resolver.openOutputStream(document, "wt");
            }
            @Override public InputStream openRead() throws IOException
            {
                return resolver.openInputStream(document);
            }
            @Override public boolean removeIncomplete() throws IOException
            {
                if (!createdForThisExport)
                    return false;
                return DocumentsContract.deleteDocument(resolver, document);
            }
        };
    }
    private VerifiedBackupPublication()
    {
    }
}
