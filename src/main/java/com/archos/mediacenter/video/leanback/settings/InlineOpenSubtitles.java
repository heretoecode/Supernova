package com.archos.mediacenter.video.leanback.settings;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.*;
import com.archos.mediacenter.video.R;
import com.archos.mediacenter.video.leanback.*;
import com.archos.mediacenter.video.utils.*;
import java.io.IOException;
import java.util.concurrent.*;

/** Direct account fields. Password entry uses the shared keyboard and privacy-masked diagnostics. */
final class InlineOpenSubtitles {
    private final Context context;
    private final LinearLayout target;
    private final TextView information;
    private String username, password;
    private boolean reveal;
    private int generation;
    private Future<?> work;
    private static final ExecutorService IO =
        new ThreadPoolExecutor(1, 1, 0, TimeUnit.SECONDS, new ArrayBlockingQueue<>(2), r -> {
            Thread t = new Thread(r, "SupernovaSubtitleLogin");
            t.setDaemon(true);
            return t;
        }, new ThreadPoolExecutor.DiscardOldestPolicy());

    InlineOpenSubtitles(Context c, LinearLayout target, TextView information) {
        context = c;
        this.target = target;
        this.information = information;
        SharedPreferences prefs = c.getSharedPreferences("opensubtitles_credentials", 0);
        username = prefs.getString(OpenSubtitlesCredentialsDialog.OPENSUBTITLES_USERNAME, "");
        password = prefs.getString(OpenSubtitlesCredentialsDialog.OPENSUBTITLES_PASSWORD, "");
        target.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
            @Override
            public void onViewAttachedToWindow(View v) {
            }
            @Override
            public void onViewDetachedFromWindow(View v) {
                generation++;
                if (work != null)
                    work.cancel(true);
            }
        });
        render();
    }

    private void row(String label, String tag, Runnable run) {
        TextView view = SharedThreePanel.accentAction(context, label, run);
        view.setTag(tag);
        target.addView(view);
        SharedThreePanel.divider(target);
    }

    private boolean signingIn;

    /** Whether an account is saved and was accepted. */
    private boolean signedIn() {
        SharedPreferences prefs = context.getSharedPreferences("opensubtitles_credentials", 0);
        return !prefs.getString(OpenSubtitlesCredentialsDialog.OPENSUBTITLES_USERNAME, "").isEmpty()
            && !prefs.getString(OpenSubtitlesCredentialsDialog.OPENSUBTITLES_PASSWORD, "").isEmpty();
    }

    /**
     * The OpenSubtitles panel (page sheet SETTINGS.md; settings-v2/07, 08): signed out, one line, Sign in and a QR code
     * to create a free account; Sign in shows Username, Password (with Show) and Sign in; signed in, the account,
     * Change account and Sign out. Fields use the one shared keyboard.
     */
    void render() {
        target.removeAllViews();
        SharedThreePanel.heading(target, "OpenSubtitles");
        if (signingIn) {
            signInStep();
            return;
        }
        if (signedIn()) {
            information.setText("Signed in as " + username + " · more downloads each day");
            row("Change account", "credential.change", () -> {
                signingIn = true;
                render();
                focus("credential.username");
            });
            row("Sign out", "credential.signout", this::signOut);
            return;
        }
        information.setText("Not signed in · fewer downloads without one");
        row("Sign in", "credential.signin", () -> {
            signingIn = true;
            render();
            focus("credential.username");
        });
        LinearLayout account = new LinearLayout(context);
        account.setGravity(android.view.Gravity.CENTER_VERTICAL);
        int pad = SupernovaStyle.dp(context, 8);
        account.setPadding(pad, pad, pad, pad);
        ImageView code = new ImageView(context);
        code.setImageBitmap(qr("https://www.opensubtitles.com/en/users/sign_up"));
        code.setContentDescription("QR code to create a free OpenSubtitles account");
        int size = SupernovaStyle.dp(context, 84);
        account.addView(code, new LinearLayout.LayoutParams(size, size));
        LinearLayout words = new LinearLayout(context);
        words.setOrientation(LinearLayout.VERTICAL);
        words.setPadding(SupernovaStyle.dp(context, 14), 0, 0, 0);
        TextView title = new TextView(context);
        title.setText("No account yet?");
        title.setTextColor(SupernovaStyle.TEXT);
        title.setTextSize(15);
        words.addView(title);
        TextView line = new TextView(context);
        line.setText("Scan to create a free one on your phone, then come back and sign in");
        line.setTextColor(SupernovaStyle.TEXT_SECONDARY);
        line.setTextSize(12);
        words.addView(line);
        account.addView(words, new LinearLayout.LayoutParams(0, -2, 1));
        target.addView(account);
    }

    /** Sign in to OpenSubtitles: Username, Password with Show, Sign in. */
    private void signInStep() {
        row("Username: " + (username.isEmpty() ? "" : username), "credential.username",
            ()
                -> PreviewTextInput.showValidated(
                    context, "Username", username, 128, value -> value.isEmpty() ? "Enter a username" : null, value -> {
                        username = value;
                        render();
                        focus("credential.password");
                    }));
        row("Password: " + (reveal ? password : password.isEmpty() ? "" : "••••••••"),
            "credential.password", () -> PreviewTextInput.showSecret(context, "Password", password, value -> {
                password = value;
                render();
                focus("credential.save");
            }));
        row(reveal ? "Hide password" : "Show password", "credential.reveal", () -> {
            reveal = !reveal;
            render();
            focus("credential.reveal");
        });
        row("Sign in", "credential.save", this::save);
        information.setText("Free account at opensubtitles.com. Your password stays on this device, never in backups "
                            + "or diagnostics.");
    }

    private void signOut() {
        context.getSharedPreferences("opensubtitles_credentials", 0)
            .edit()
            .remove(OpenSubtitlesCredentialsDialog.OPENSUBTITLES_USERNAME)
            .remove(OpenSubtitlesCredentialsDialog.OPENSUBTITLES_PASSWORD)
            .apply();
        username = "";
        password = "";
        signingIn = false;
        render();
        focus("credential.signin");
    }

    private static android.graphics.Bitmap qr(String link) {
        try {
            com.google.zxing.common.BitMatrix matrix = new com.google.zxing.qrcode.QRCodeWriter().encode(
                link, com.google.zxing.BarcodeFormat.QR_CODE, 256, 256);
            android.graphics.Bitmap image =
                android.graphics.Bitmap.createBitmap(256, 256, android.graphics.Bitmap.Config.ARGB_8888);
            for (int y = 0; y < 256; y++)
                for (int x = 0; x < 256; x++)
                    image.setPixel(x, y, matrix.get(x, y) ? 0xff000000 : 0xffffffff);
            return image;
        } catch (Exception unavailable) {
            return null;
        }
    }

    private void focus(String tag) {
        target.post(() -> {
            View view = target.findViewWithTag(tag);
            if (view != null)
                view.requestFocus();
        });
    }

    private void save() {
        if (username.isEmpty() || password.isEmpty()) {
            information.setText("Enter your username and password before signing in.");
            return;
        }
        int token = ++generation;
        String user = username, secret = password;
        information.setText("Signing in…");
        if (work != null)
            work.cancel(true);
        work = IO.submit(() -> {
            boolean valid = false;
            try {
                valid = OpenSubtitlesApiHelper.login(
                    context.getString(R.string.opensubtitles_api_key), user, secret);
            } catch (IOException failure) {
                /* Never retain exception bodies or credential-bearing URLs. */
            }
            final boolean accepted = valid;
            target.post(() -> {
                if (token != generation || !target.isAttachedToWindow() || !target.isShown())
                    return;
                if (accepted) {
                    context.getSharedPreferences("opensubtitles_credentials", 0)
                        .edit()
                        .putString(OpenSubtitlesCredentialsDialog.OPENSUBTITLES_USERNAME, user)
                        .putString(OpenSubtitlesCredentialsDialog.OPENSUBTITLES_PASSWORD, secret)
                        .apply();
                    OpenSubtitlesApiHelper.persistStatus(context, OpenSubtitlesApiHelper.OS_STATUS_OK);
                    signingIn = false;
                    render();
                    focus("credential.change");
                } else
                    information.setText(
                        "Sign-in failed. Check the account details and connection. Previously saved credentials have been kept.");
            });
        });
    }
}
