package com.archos.mediacenter.video.leanback;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Enforces the owner's global rule (docs/design/BUTTONS_GLASS_AND_MENUS_APPROVED_2026-11-02.md): every button,
 * menu, panel and row takes its focus styling from {@link SupernovaStyle}. A screen that defines its own focus
 * look fails here, so a screen nobody discussed cannot silently keep an old style.
 *
 * <p>Exceptions are listed with their reason and reported in the build notes for the owner.
 */
public class SupernovaStyleConformanceTest {
    private static final Path JAVA = Paths.get("src/main/java/com/archos/mediacenter/video");
    private static final Path RES = Paths.get("res");

    /** Java files allowed to mention focus states, with the reason. */
    static final Map<String, String> JAVA_EXCEPTIONS =
        Map.of("leanback/SupernovaStyle.java", "the shared style itself", "leanback/PreviewContentFocus.java",
               "delegates to SupernovaStyle.rowFocusOutline", "browser/BrowserCategory.java",
               "original NOVA phone browser; not part of the TV interface");

    /** XML selectors with focus states, with the reason. */
    static final Map<String, String> XML_EXCEPTIONS = Map.of(
        "drawable/skip_button_selector.xml", "XML mirror of the shared button style (values checked below)",
        "color/skip_button_text_selector.xml", "XML mirror of the shared button content colours (values checked below)",
        "drawable/preview_seek_thumb.xml", "seek bar thumb, not a button", "drawable/mark_hud_focus.xml",
        "legacy native player menu cards, only hosted inside the playback panel", "drawable/subtitle_swatch.xml",
        "upstream NOVA 6.6.2 subtitle dialog resource", "drawable/subtitle_row_bg.xml",
        "upstream NOVA 6.6.2 subtitle dialog resource", "drawable/square_button.xml", "original NOVA phone browser",
        "drawable/big_button.xml", "original NOVA phone/network dialogs", "drawable/button_selector_holo_dark.xml",
        "upstream NOVA dialogs");

    private static List<Path> files(Path root, String suffix) throws IOException {
        try (Stream<Path> walk = Files.walk(root)) {
            return walk.filter(p -> p.toString().endsWith(suffix))
                .sorted()
                .collect(Collectors.toList());
        }
    }

    private static String read(Path p) throws IOException {
        return new String(Files.readAllBytes(p), StandardCharsets.UTF_8);
    }

    private static String relative(Path root, Path p) {
        return root.relativize(p).toString().replace('\\', '/');
    }

    @Test
    public void noScreenDefinesItsOwnFocusStyling() throws IOException {
        List<String> offenders = new ArrayList<>();
        for (Path p : files(JAVA, ".java")) {
            String name = relative(JAVA, p);
            if (!JAVA_EXCEPTIONS.containsKey(name) && read(p).contains("state_focused"))
                offenders.add(name);
        }
        assertTrue("These screens define their own focus styling instead of using SupernovaStyle: " + offenders,
                   offenders.isEmpty());
    }

    @Test
    public void noFocusOutlineUsesTheAccentColour() throws IOException {
        Pattern accentStroke = Pattern.compile("setStroke\\([^;]*PreviewAccent");
        List<String> offenders = new ArrayList<>();
        for (Path p : files(JAVA, ".java"))
            if (accentStroke.matcher(read(p)).find())
                offenders.add(relative(JAVA, p));
        assertTrue("Accent-coloured outlines found (focus is white everywhere): " + offenders, offenders.isEmpty());
    }

    @Test
    public void everyXmlFocusSelectorIsAccountedFor() throws IOException {
        List<String> offenders = new ArrayList<>();
        for (Path p : files(RES, ".xml")) {
            String name = relative(RES, p);
            if ((name.startsWith("drawable") || name.startsWith("color")) && read(p).contains("state_focused") &&
                !XML_EXCEPTIONS.containsKey(name))
                offenders.add(name);
        }
        assertTrue("XML focus selectors outside the shared style: " + offenders, offenders.isEmpty());
    }

    @Test
    public void xmlMirrorsMatchTheSharedStyle() throws IOException {
        String skip = read(RES.resolve("drawable/skip_button_selector.xml")).toUpperCase(Locale.ROOT);
        assertTrue(skip.contains(String.format("#%08X", SupernovaStyle.FOCUS_FILL)));
        assertTrue(skip.contains(String.format("#%08X", SupernovaStyle.BUTTON_REST)));
        assertTrue(skip.contains("12DP"));
        String text = read(RES.resolve("color/skip_button_text_selector.xml")).toUpperCase(Locale.ROOT);
        assertTrue(text.contains(String.format("#%08X", SupernovaStyle.FOCUS_TEXT)));
    }

    @Test
    public void exceptionsStillExist() {
        for (String name : JAVA_EXCEPTIONS.keySet())
            assertTrue("Stale exception " + name, Files.exists(JAVA.resolve(name)));
        for (String name : XML_EXCEPTIONS.keySet())
            assertTrue("Stale exception " + name, Files.exists(RES.resolve(name)));
    }
}
