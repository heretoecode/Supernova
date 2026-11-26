package com.archos.mediacenter.video.leanback;

import android.content.Context;
import android.content.SharedPreferences;
import androidx.preference.PreferenceManager;

/**
 * Files › Connections › Advanced › Network options (FILES.md, `files-v2/tv/15`; moved here from Settings): Windows
 * Shares (SMB): Newer SMB method · SMBv2 support · Name lookup order (Network first / Local first). Finding Devices:
 * Look over TCP · UDP · mDNS (Bonjour). SFTP: Newer SFTP method. Each switch with its line; changes apply at once.
 */
public final class NetworkOptionsPanel
{
    private NetworkOptionsPanel()
    {
    }

    public static void show(Context context, Runnable closed)
    {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        FloatingPanel panel = new FloatingPanel(context, "Network options");
        panel.heading("Windows Shares (SMB)");
        panel.toggle("network.smbj", "network", "Newer SMB method", "Faster with most newer computers and NAS",
                     prefs.getBoolean("pref_smbj", false), on -> prefs.edit().putBoolean("pref_smbj", on).apply());
        panel.toggle("network.smbv2", "settings.nodes", "SMBv2 support", "Turn off only for very old servers",
                     prefs.getBoolean("pref_smbv2", true), on -> prefs.edit().putBoolean("pref_smbv2", on).apply());
        panel.chips("network.resolv", "search", "Name lookup order", new String[] {"Network first", "Local first"},
                    prefs.getBoolean("pref_smb_resolv", false) ? 1 : 0,
                    n -> prefs.edit().putBoolean("pref_smb_resolv", n == 1).apply());
        panel.heading("Finding Devices");
        lookOver(panel, prefs, "TCP", "pref_smb_disable_tcp_discovery", "Asks each computer directly");
        lookOver(panel, prefs, "UDP", "pref_smb_disable_udp_discovery", "Listens for computers announcing themselves");
        lookOver(panel, prefs, "mDNS (Bonjour)", "pref_smb_disable_mdns_discovery", "Finds Macs and most NAS boxes");
        panel.heading("SFTP");
        panel.toggle("network.sshj", "settings.radio", "Newer SFTP method", "Works with more modern servers",
                     prefs.getBoolean("pref_sshj", false), on -> prefs.edit().putBoolean("pref_sshj", on).apply());
        panel.onClosed(closed);
        panel.show(null);
    }

    /** "Look over TCP" is on when its old "disable" switch is off. */
    private static void lookOver(FloatingPanel panel, SharedPreferences prefs, String how, String disableKey,
                                 String line)
    {
        panel.toggle("network.look." + how, "discovery", "Look over " + how, line, !prefs.getBoolean(disableKey, false),
                     on -> prefs.edit().putBoolean(disableKey, !on).apply());
    }
}
