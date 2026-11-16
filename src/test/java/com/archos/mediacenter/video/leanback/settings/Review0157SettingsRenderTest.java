package com.archos.mediacenter.video.leanback.settings;

import android.app.Activity;
import android.app.Application;
import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Looper;
import android.view.View;
import androidx.preference.ListPreference;
import java.io.File;
import java.io.FileOutputStream;
import java.time.Duration;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.Shadows;
import org.robolectric.annotation.Config;
import org.robolectric.annotation.GraphicsMode;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

/** Renders a Settings list on the shared choice list (docs/0.157/REVIEW_0157.md #1). */
@RunWith(RobolectricTestRunner.class)
@Config(application = Application.class, sdk = 28, qualifiers = "w960dp-h540dp-land-xhdpi")
public class Review0157SettingsRenderTest {
    @Test
    @GraphicsMode(GraphicsMode.Mode.NATIVE)
    public void preferredAudioLanguageUsesTheSharedChoiceList() throws Exception {
        Activity activity = Robolectric.buildActivity(Activity.class).setup().visible().get();
        ListPreference preference = new ListPreference(activity);
        preference.setPersistent(false);
        preference.setKey("favAudioLang");
        preference.setTitle("Preferred audio language");
        String[] names = {"English", "French", "German", "Spanish", "Italian", "Dutch", "Swedish", "Danish",
                          "Norwegian", "Finnish", "Polish", "Portuguese", "Japanese", "Korean", "Chinese"};
        String[] codes = {"en", "fr", "de", "es", "it", "nl", "sv", "da", "no", "fi", "pl", "pt", "ja", "ko", "zh"};
        preference.setEntries(names);
        preference.setEntryValues(codes);
        preference.setValue("fr");
        Dialog dialog = SettingsChoiceDialog.show(activity, preference);
        assertNotNull(dialog);
        View root = dialog.getWindow().getDecorView();
        Shadows.shadowOf(Looper.getMainLooper()).idleFor(Duration.ofMillis(200));
        assertNull("No Cancel button on the shared list", root.findViewWithTag("settings.choice.cancel"));
        Bitmap bitmap = Bitmap.createBitmap(Math.max(1, root.getWidth()), Math.max(1, root.getHeight()),
                                            Bitmap.Config.ARGB_8888);
        bitmap.eraseColor(0xFF16181C);
        root.draw(new Canvas(bitmap));
        File file = new File("build/reports/preview-ui/0157-settings-audio-language-list.png");
        file.getParentFile().mkdirs();
        try (FileOutputStream out = new FileOutputStream(file)) {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
        }
    }
}
