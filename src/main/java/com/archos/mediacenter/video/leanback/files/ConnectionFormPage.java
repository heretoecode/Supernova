package com.archos.mediacenter.video.leanback.files;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.preference.PreferenceManager;

import com.archos.filecorelibrary.connections.DraftConnectionTest;
import com.archos.filecorelibrary.samba.NetworkCredentialsDatabase;
import com.archos.mediacenter.video.data.ConnectionForm;
import com.archos.mediacenter.video.leanback.KeyboardInput;
import com.archos.mediacenter.video.leanback.PreviewIcon;
import com.archos.mediacenter.video.leanback.PreviewKeyboard;
import com.archos.mediacenter.video.leanback.SupernovaStyle;
import com.archos.mediacenter.video.leanback.filebrowsing.ConnectionTestWords;
import com.archos.mediacenter.video.leanback.filebrowsing.SavedConnection;

import java.util.Locale;
import java.util.function.BiConsumer;

/**
 * The connection form as a page in the Connections tab (page sheet FILES.md; mock-up files-review-0157-1/04, 2;
 * F32, F34, F39): Type chips SMB · WebDAV · SFTP · FTP, the fields following the type, the one keyboard on the page
 * (RULES §15), then Test connection · Save and browse. Back leaves without saving.
 */
public final class ConnectionFormPage extends LinearLayout
{
    static final String[] TYPES = {"SMB", "WebDAV", "SFTP", "FTP"};
    static final ConnectionForm.Kind[] KINDS = {
        ConnectionForm.Kind.SMB, ConnectionForm.Kind.WEBDAV, ConnectionForm.Kind.SFTP, ConnectionForm.Kind.FTP
    };

    private static final int NAME = 0;
    private static final int ADDRESS = 1;
    private static final int PORT = 2;
    private static final int FOLDER = 3;
    private static final int USER = 4;
    private static final int PASSWORD = 5;
    private static final String[] LABELS = {"Name", "Address", "Port", "Folder", "Username", "Password"};

    private final String[] values = {"", "", "", "", "", ""};
    private final TextView[] shown = new TextView[LABELS.length];
    private final LinearLayout fields;
    private final TextView title;
    private final TextView help;
    private final TextView status;
    private final KeyboardInput buffer;
    private final PreviewKeyboard keyboard;
    private final BiConsumer<Uri, String> saved;
    private int type = 1;
    private boolean encryption = true;
    private boolean remember = true;
    private boolean showPassword;
    private boolean nameTouched;
    private int editing = -1;
    private String before = "";
    private Uri editingUri;
    private DraftConnectionTest.Job test;

    /** {@code saved} gets the new connection's address and name once it is saved (then it is browsed). */
    public ConnectionFormPage(Context context, BiConsumer<Uri, String> saved)
    {
        super(context);
        this.saved = saved;
        setOrientation(HORIZONTAL);
        int side = dp(FilesPage.SIDE_DP);
        setPadding(side, dp(20), side, 0);
        setClipChildren(false);
        LinearLayout left = new LinearLayout(context);
        left.setOrientation(VERTICAL);
        title = text("New connection", 20, Color.WHITE);
        title.setTypeface(android.graphics.Typeface.create("sans-serif-medium", android.graphics.Typeface.NORMAL));
        left.addView(title);
        ScrollView scroll = new ScrollView(context);
        scroll.setVerticalScrollBarEnabled(false);
        scroll.setClipToPadding(false);
        fields = new LinearLayout(context);
        fields.setOrientation(VERTICAL);
        fields.setPadding(0, dp(10), 0, dp(20));
        scroll.addView(fields);
        left.addView(scroll, new LayoutParams(-1, 0, 1));
        addView(left, new LayoutParams(0, -1, 0.48f));
        LinearLayout right = new LinearLayout(context);
        right.setOrientation(VERTICAL);
        right.setPadding(dp(24), dp(34), 0, 0);
        buffer = new KeyboardInput(context);
        buffer.setVisibility(GONE);
        right.addView(buffer);
        keyboard = new PreviewKeyboard(context, buffer, null);
        keyboard.setActions(this::finishEditing, this::cancelEditing);
        right.addView(keyboard, new LayoutParams(-1, -2));
        help = text("", 14, SupernovaStyle.TEXT_SECONDARY);
        help.setSingleLine(false);
        LayoutParams helpSize = new LayoutParams(-1, -2);
        helpSize.topMargin = dp(16);
        right.addView(help, helpSize);
        status = text("", 15, Color.WHITE);
        status.setSingleLine(false);
        LayoutParams statusSize = new LayoutParams(-1, -2);
        statusSize.topMargin = dp(12);
        right.addView(status, statusSize);
        addView(right, new LayoutParams(0, -1, 0.52f));
        buffer.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence text, int start, int count, int after)
            {
            }

            @Override
            public void onTextChanged(CharSequence text, int start, int before, int count)
            {
            }

            @Override
            public void afterTextChanged(Editable text)
            {
                if (editing >= 0)
                {
                    values[editing] = text.toString();
                    if (editing == NAME)
                    {
                        nameTouched = true;
                    }
                    if (editing == ADDRESS && !nameTouched)
                    {
                        values[NAME] = suggestedName(values[ADDRESS]);
                    }
                    refreshValues();
                }
            }
        });
        build();
    }

    /** A new connection, optionally from a device found on the network (address and type filled in). */
    public void startNew(Uri found)
    {
        editingUri = null;
        title.setText("New connection");
        if (found != null)
        {
            int index = SavedConnection.protocolIndex(found.getScheme());
            type = index == 0 ? 0 : index == 3 ? 2 : index >= 4 ? 3 : index >= 1 ? 1 : 0;
            values[ADDRESS] = found.getHost() == null ? "" : found.getHost();
            values[NAME] = suggestedName(values[ADDRESS]);
        }
        build();
    }

    /** The saved connection's form, filled in (F32); the password stays hidden. */
    public void startEdit(Uri uri, String name)
    {
        editingUri = uri;
        title.setText("Edit " + name);
        int index = SavedConnection.protocolIndex(uri.getScheme());
        type = index == 0 ? 0 : index == 3 ? 2 : index >= 4 ? 3 : 1;
        encryption = index == 1 || index == 5 || index == 3;
        values[NAME] = name;
        nameTouched = true;
        values[ADDRESS] = uri.getHost() == null ? "" : uri.getHost();
        values[PORT] = uri.getPort() > 0 ? String.valueOf(uri.getPort()) : "";
        values[FOLDER] = uri.getPath() == null ? "" : uri.getPath();
        try
        {
            NetworkCredentialsDatabase.Credential credential =
                NetworkCredentialsDatabase.getInstance().getCredential(uri.toString());
            values[USER] = credential == null ? "" : credential.getUsername();
            values[PASSWORD] = credential == null ? "" : credential.getPassword();
        }
        catch (RuntimeException unavailable)
        {
            values[USER] = "";
            values[PASSWORD] = "";
        }
        build();
    }

    /** "webdav.put.io" → "Put.io"; an address → itself (F52: the name is suggested from the address). */
    static String suggestedName(String address)
    {
        String host = address == null ? "" : address.trim().toLowerCase(Locale.ROOT);
        if (host.isEmpty())
        {
            return "";
        }
        if (host.matches("[0-9.:]+"))
        {
            return host;
        }
        String[] parts = host.split("\\.");
        if (parts.length >= 2)
        {
            String main = parts[parts.length - 2];
            String ending = parts[parts.length - 1];
            if (main.length() <= 3 && parts.length >= 3)
            {
                main = parts[parts.length - 3];
                ending = parts[parts.length - 2] + "." + ending;
            }
            if (ending.equals("local") || ending.equals("lan") || ending.equals("home"))
            {
                return capital(main);
            }
            return capital(main) + "." + ending;
        }
        return capital(host);
    }

    private static String capital(String word)
    {
        return word.isEmpty() ? word : word.substring(0, 1).toUpperCase(Locale.ROOT) + word.substring(1);
    }

    /** The help line for the chosen type. */
    static String helpLine(int type)
    {
        switch (type)
        {
            case 0:
                return "SMB: shared folders on a Windows PC, Mac or NAS.";
            case 1:
                return "WebDAV: online storage such as Put.io, or a server that shares over the web. "
                    + "Leave Encryption on unless your server only offers plain HTTP.";
            case 2:
                return "SFTP: folders on a computer or server you reach with a login.";
            default:
                return "FTP: folders on an FTP server. Leave Encryption on unless your server only offers plain FTP.";
        }
    }

    /** The encryption switch's words follow the type; SMB and SFTP have none (F39). */
    static String encryptionLabel(int type)
    {
        if (type == 1)
        {
            return "Encryption (HTTPS)";
        }
        return type == 3 ? "Encryption (FTPS)" : null;
    }

    private void build()
    {
        fields.removeAllViews();
        help.setText(helpLine(type));
        LinearLayout chips = new LinearLayout(getContext());
        chips.setGravity(Gravity.CENTER_VERTICAL);
        chips.setPadding(dp(14), 0, dp(14), 0);
        chips.addView(text("Type", 16, SupernovaStyle.TEXT_SECONDARY), new LayoutParams(dp(84), -2));
        for (int index = 0; index < TYPES.length; index++)
        {
            chips.addView(chip(index), chipSize());
        }
        fields.addView(chips, new LayoutParams(-1, dp(46)));
        field(NAME);
        field(ADDRESS);
        String switchLabel = encryptionLabel(type);
        if (switchLabel != null)
        {
            toggle(switchLabel, () -> encryption, () -> {
                encryption = !encryption;
                refreshValues();
            });
        }
        field(PORT);
        field(FOLDER);
        field(USER);
        field(PASSWORD);
        toggle("Remember password", () -> remember, () -> remember = !remember);
        LinearLayout buttons = new LinearLayout(getContext());
        buttons.setGravity(Gravity.CENTER_VERTICAL);
        buttons.addView(button("Test connection", "done", this::testConnection));
        LayoutParams gap = new LayoutParams(-2, -2);
        gap.leftMargin = dp(12);
        buttons.addView(button("Save and browse", "right", this::save), gap);
        LayoutParams buttonsSize = new LayoutParams(-1, -2);
        buttonsSize.topMargin = dp(16);
        fields.addView(buttons, buttonsSize);
        refreshValues();
    }

    private LayoutParams chipSize()
    {
        LayoutParams size = new LayoutParams(-2, dp(30));
        size.rightMargin = dp(4);
        return size;
    }

    private TextView chip(int index)
    {
        TextView chip = text(TYPES[index], 15, Color.WHITE);
        chip.setGravity(Gravity.CENTER);
        chip.setPadding(dp(11), 0, dp(11), 0);
        chip.setFocusable(true);
        chip.setFocusableInTouchMode(true);
        chip.setTag("files.form.type:" + TYPES[index]);
        chip.setContentDescription("Type " + TYPES[index] + (index == type ? ", chosen" : ""));
        Runnable restyle = () -> {
            boolean focused = chip.isFocused();
            boolean chosen = index == type;
            chip.setTextColor(focused || !chosen ? (focused ? SupernovaStyle.FOCUS_TEXT : Color.WHITE) : Color.BLACK);
            chip.setBackground(focused ? box(SupernovaStyle.FOCUS_FILL, 15)
                : chosen ? box(0xFFE8ECF3, 15) : null);
        };
        chip.setOnFocusChangeListener((view, focused) -> restyle.run());
        chip.setOnClickListener(view -> {
            if (type == index)
            {
                return;
            }
            type = index;
            encryption = true;
            build();
            View again = fields.findViewWithTag("files.form.type:" + TYPES[index]);
            if (again != null)
            {
                again.requestFocus();
            }
        });
        restyle.run();
        return chip;
    }

    private void field(int which)
    {
        LinearLayout row = formRow(LABELS[which]);
        TextView value = text("", 17, Color.WHITE);
        value.setSingleLine(true);
        value.setEllipsize(TextUtils.TruncateAt.END);
        row.addView(value, new LayoutParams(0, -2, 1));
        shown[which] = value;
        row.setTag("files.form.field:" + LABELS[which]);
        if (which == PASSWORD)
        {
            TextView eye = text("", 16, Color.WHITE);
            PreviewIcon.apply(eye, "eye", 16);
            row.addView(eye, new LayoutParams(dp(28), -2));
            row.setOnKeyListener((view, key, event) -> {
                if (key == KeyEvent.KEYCODE_DPAD_RIGHT && event.getAction() == KeyEvent.ACTION_DOWN)
                {
                    // Right shows or hides what was typed (the eye; RULES §15).
                    showPassword = !showPassword;
                    PreviewIcon.apply(eye, showPassword ? "eye-off" : "eye", 16);
                    refreshValues();
                    return true;
                }
                return false;
            });
        }
        row.setOnClickListener(view -> startEditing(which));
        styleRow(row);
        line();
        fields.addView(row, new LayoutParams(-1, dp(46)));
    }

    private void toggle(String label, java.util.function.BooleanSupplier on, Runnable flip)
    {
        LinearLayout row = formRow(label);
        row.setTag("files.form.switch:" + label);
        row.addView(new View(getContext()), new LayoutParams(0, 1, 1));
        TextView state = text("", 15, Color.WHITE);
        state.setGravity(Gravity.CENTER);
        row.addView(state, new LayoutParams(dp(56), dp(26)));
        Runnable show = () -> {
            boolean value = on.getAsBoolean();
            boolean focused = row.isFocused();
            int ink = focused ? SupernovaStyle.FOCUS_TEXT : Color.WHITE;
            state.setText(value ? "On" : "Off");
            state.setTextColor(value ? (focused ? Color.WHITE : SupernovaStyle.FOCUS_TEXT) : ink);
            state.setBackground(value ? box(focused ? SupernovaStyle.FOCUS_TEXT : Color.WHITE, 13) : outline(ink));
        };
        row.setOnClickListener(view -> {
            flip.run();
            show.run();
        });
        row.setOnFocusChangeListener((view, focused) -> {
            restyle(row, focused);
            show.run();
        });
        show.run();
        line();
        fields.addView(row, new LayoutParams(-1, dp(46)));
    }

    private LinearLayout formRow(String label)
    {
        LinearLayout row = new LinearLayout(getContext());
        row.setGravity(Gravity.CENTER_VERTICAL);
        row.setPadding(dp(14), 0, dp(14), 0);
        row.setFocusable(true);
        row.setFocusableInTouchMode(true);
        row.setDescendantFocusability(FOCUS_BLOCK_DESCENDANTS);
        row.setContentDescription(label);
        TextView name = text(label, 16, SupernovaStyle.TEXT_SECONDARY);
        row.addView(name, new LayoutParams(dp(104), -2));
        return row;
    }

    private void styleRow(LinearLayout row)
    {
        row.setOnFocusChangeListener((view, focused) -> restyle(row, focused));
        restyle(row, false);
    }

    private void restyle(LinearLayout row, boolean focused)
    {
        row.setBackground(focused ? box(SupernovaStyle.FOCUS_FILL, 10) : null);
        for (int index = 0; index < row.getChildCount(); index++)
        {
            if (row.getChildAt(index) instanceof TextView)
            {
                TextView child = (TextView) row.getChildAt(index);
                boolean label = index == 0;
                child.setTextColor(focused ? SupernovaStyle.FOCUS_TEXT
                    : label ? SupernovaStyle.TEXT_SECONDARY : Color.WHITE);
                SupernovaStyle.inkIcons(child, focused ? SupernovaStyle.FOCUS_TEXT : Color.WHITE);
            }
        }
    }

    private void line()
    {
        if (fields.getChildCount() == 0)
        {
            return;
        }
        View line = new View(getContext());
        line.setBackgroundColor(FilesPage.LINE);
        fields.addView(line, new LayoutParams(-1, Math.max(1, dp(1))));
    }

    private void refreshValues()
    {
        for (int which = 0; which < shown.length; which++)
        {
            if (shown[which] == null)
            {
                continue;
            }
            String value = values[which];
            if (which == PASSWORD && !showPassword)
            {
                value = value.replaceAll(".", "•");
            }
            if (which == PORT && value.isEmpty())
            {
                value = defaultPort() + " · the usual port" + (encryptionLabel(type) != null && encryption
                    ? " with encryption" : "");
            }
            if (which == FOLDER && value.isEmpty())
            {
                value = "/ (the whole share)";
            }
            shown[which].setText(value);
        }
    }

    private int defaultPort()
    {
        return ConnectionForm.transport(KINDS[type], encryption, 0, encryption).port();
    }

    private void startEditing(int which)
    {
        editing = which;
        before = values[which];
        buffer.setText(values[which]);
        buffer.setSelection(buffer.getText().length());
        keyboard.focusLastKey();
    }

    private void finishEditing()
    {
        int done = editing;
        editing = -1;
        View row = done < 0 ? null : fields.findViewWithTag("files.form.field:" + LABELS[done]);
        if (row != null)
        {
            row.requestFocus();
        }
    }

    private void cancelEditing()
    {
        if (editing >= 0)
        {
            values[editing] = before;
            refreshValues();
        }
        finishEditing();
    }

    /** True while the remote is on the keyboard: Back returns to the field instead of leaving the form. */
    public boolean back()
    {
        if (editing >= 0)
        {
            cancelEditing();
            return true;
        }
        if (test != null)
        {
            test.close();
            test = null;
        }
        return false;
    }

    private int protocolIndex()
    {
        switch (type)
        {
            case 0:
                return 0;
            case 1:
                return encryption ? 1 : 2;
            case 2:
                return 3;
            default:
                return encryption ? 5 : 4;
        }
    }

    private void testConnection()
    {
        String address = values[ADDRESS].trim();
        if (address.isEmpty())
        {
            status.setText("Enter the server name or address first.");
            return;
        }
        int port;
        try
        {
            port = values[PORT].isEmpty() ? -1 : Integer.parseInt(values[PORT].trim());
        }
        catch (NumberFormatException wrong)
        {
            status.setText("Enter a port from 1 to 65535.");
            return;
        }
        if (test != null)
        {
            test.close();
        }
        status.setText("Testing " + address + "…");
        String folder = values[FOLDER].startsWith("/") ? values[FOLDER] : "/" + values[FOLDER];
        char[] password = values[PASSWORD].toCharArray();
        try (DraftConnectionTest.Draft draft = new DraftConnectionTest.Draft(
                 SavedConnection.type(protocolIndex()), address, port, folder, values[USER], password))
        {
            test = DraftConnectionTest.start(getContext(), draft, this::post, result -> {
                test = null;
                status.setText(ConnectionTestWords.message(result.cancelled, result.field, result.reason,
                                                           result.folders, result.videos));
            });
        }
        catch (RuntimeException failed)
        {
            status.setText("The test could not start: " + failed.getMessage());
        }
        finally
        {
            java.util.Arrays.fill(password, '\0');
        }
    }

    /** Saves the login and the connection (it joins the library), then browses it (F34). */
    private void save()
    {
        String address = values[ADDRESS].trim();
        if (address.isEmpty() || address.contains("/") || address.contains("@") || address.contains("?"))
        {
            status.setText("Enter a server name or IP address, without a protocol or a login.");
            return;
        }
        int port;
        try
        {
            port = values[PORT].isEmpty() ? -1 : Integer.parseInt(values[PORT].trim());
        }
        catch (NumberFormatException wrong)
        {
            port = 0;
        }
        if (port != -1 && (port < 1 || port > 65535))
        {
            status.setText("Enter a port from 1 to 65535.");
            return;
        }
        android.content.SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        int index = protocolIndex();
        String scheme = new String[] {
            preferences.getBoolean("pref_smbj", true) ? "smbj" : "smb", "webdavs", "webdav",
            preferences.getBoolean("pref_sshj", true) ? "sshj" : "sftp", "ftp", "ftps"
        }[index];
        String authority = address.toLowerCase(Locale.ROOT) + (port < 0 ? "" : ":" + port);
        String folder = values[FOLDER].startsWith("/") ? values[FOLDER] : "/" + values[FOLDER];
        Uri uri = new Uri.Builder().scheme(scheme).encodedAuthority(authority).path(folder).build();
        try
        {
            NetworkCredentialsDatabase database = NetworkCredentialsDatabase.getInstance();
            boolean anonymous = (index == 1 || index == 2) && values[USER].isEmpty() && values[PASSWORD].isEmpty();
            NetworkCredentialsDatabase.Credential credential = new NetworkCredentialsDatabase.Credential(
                anonymous ? "anonymous" : values[USER], values[PASSWORD], uri.toString(), "", !remember);
            if (remember)
            {
                database.saveCredential(credential);
            }
            database.addCredential(credential);
        }
        catch (RuntimeException failed)
        {
            status.setText("The login could not be saved.");
            return;
        }
        Uri replaced = editingUri;
        boolean changed = LibraryChanges.apply(getContext(), selection -> {
            if (replaced != null && !replaced.equals(uri))
            {
                selection.remove(replaced);
            }
            selection.include(uri);
        });
        if (!changed)
        {
            status.setText("The connection could not be saved. Try again in a moment.");
            return;
        }
        String name = values[NAME].trim().isEmpty() ? suggestedName(address) : values[NAME].trim();
        FilesPageData.rename(getContext(), uri, name);
        values[PASSWORD] = "";
        saved.accept(uri, name);
    }

    private TextView button(String label, String icon, Runnable action)
    {
        TextView button = text(label, 16, Color.WHITE);
        button.setPadding(dp(14), dp(8), dp(14), dp(8));
        button.setFocusable(true);
        button.setFocusableInTouchMode(true);
        button.setTag("files.form.button:" + label);
        PreviewIcon.apply(button, icon, 14);
        button.setOnFocusChangeListener((view, focused) -> {
            button.setTextColor(focused ? SupernovaStyle.FOCUS_TEXT : Color.WHITE);
            SupernovaStyle.inkIcons(button, focused ? SupernovaStyle.FOCUS_TEXT : Color.WHITE);
            button.setBackground(focused ? box(SupernovaStyle.FOCUS_FILL, 10) : null);
        });
        button.setOnClickListener(view -> action.run());
        return button;
    }

    private TextView text(String value, float size, int colour)
    {
        TextView view = new TextView(getContext());
        view.setText(value);
        view.setTextSize(size);
        view.setTextColor(colour);
        view.setSingleLine(true);
        return view;
    }

    private GradientDrawable box(int colour, int radius)
    {
        GradientDrawable shape = new GradientDrawable();
        shape.setCornerRadius(dp(radius));
        shape.setColor(colour);
        return shape;
    }

    private GradientDrawable outline(int colour)
    {
        GradientDrawable shape = new GradientDrawable();
        shape.setCornerRadius(dp(13));
        shape.setStroke(Math.max(1, dp(1)), colour);
        return shape;
    }

    private int dp(float value)
    {
        return Math.round(value * getResources().getDisplayMetrics().density);
    }
}
