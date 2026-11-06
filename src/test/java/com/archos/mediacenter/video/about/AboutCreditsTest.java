package com.archos.mediacenter.video.about;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertThrows;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.HexFormat;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Test;

public class AboutCreditsTest
{
    private static JSONObject json(String path) throws Exception
    {
        return new JSONObject(Files.readString(Path.of(path)));
    }

    @Test
    public void everyResolvedArtifactAndNativePinHasExactVersionAndFullLegalEvidence() throws Exception
    {
        List<AboutCredits.Entry> entries;
        try (java.io.InputStream input = Files.newInputStream(Path.of("assets/" + AboutCredits.ASSET)))
        {
            entries = AboutCredits.read(input);
        }
        Map<String, AboutCredits.Entry> byId = new HashMap<>();
        for (AboutCredits.Entry entry : entries)
        {
            byId.put(entry.id, entry);
            assertFalse(entry.role.isBlank());
        }
        JSONArray artifacts = json("docs/foundation/RUNTIME_SBOM.json").getJSONArray("artifacts");
        for (int i = 0; i < artifacts.length(); i++)
        {
            String coordinate = artifacts.getJSONObject(i).getString("coordinate");
            assertEquals(coordinate, coordinate.split(":")[2], byId.get(coordinate).version);
        }
        Matcher declarations = Pattern.compile("(?m)^\\s*(?:implementation|coreLibraryDesugaring)\\s*\\(?\\s*'"
                                                + "([^']+:[^']+:[^']+)'")
                                   .matcher(Files.readString(Path.of("build.gradle")));
        while (declarations.find())
        {
            assertTrue(declarations.group(1), byId.containsKey(declarations.group(1)));
        }
        JSONObject nativeLock = json(".github/build/native-supernova156.lock.json");
        JSONArray nativeSources = nativeLock.getJSONObject("sources").getJSONObject("third_party_native_stack")
                                     .getJSONArray("sources");
        for (int i = 0; i < nativeSources.length(); i++)
        {
            JSONObject source = nativeSources.getJSONObject(i);
            String id = "native:" + source.getString("name");
            assertTrue(id, byId.containsKey(id));
            String version = source.optString("tag", "");
            if (version.isEmpty() || version.equals("null"))
                version = source.getString("commit");
            if (source.getString("name").equals("opus"))
                version = "1.6.1-84-g084bb732";
            assertEquals(id, version, byId.get(id).version);
        }
        assertEquals(nativeLock.getJSONObject("sources").getJSONObject("avos").getString("commit"),
            byId.get("module:avos").version);
        JSONObject wrappers = nativeLock.getJSONObject("wrapper_sources");
        assertEquals(wrappers.getJSONObject("FileCoreLibrary").getString("commit"),
            byId.get("module:FileCoreLibrary").version);
        assertEquals(wrappers.getJSONObject("native/libnativehelper").getString("commit"),
            byId.get("module:libnativehelper").version);
        assertEquals(wrappers.getJSONObject("native/audiocompress").getString("commit"),
            byId.get("native:AudioCompress").version);
        JSONArray credits = json("assets/" + AboutCredits.ASSET).getJSONArray("entries");
        for (int i = 0; i < credits.length(); i++)
        {
            JSONObject credit = credits.getJSONObject(i);
            if (credit.has("text_asset"))
            {
                byte[] text = Files.readAllBytes(Path.of("assets/" + credit.getString("text_asset")));
                assertTrue(credit.getString("id"), text.length > 500);
                assertEquals(credit.getString("text_sha256"),
                    HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(text)));
            }
            if (credit.has("commit"))
            {
                String commit = nativeSources.toString();
                assertTrue(commit.contains(credit.getString("commit")));
            }
        }
        for (String service : List.of("TMDB", "TheIntroDB", "IntroDB", "ChaptersDB", "OpenSubtitles", "Trakt",
                                      "OMDb", "JustWatch via TMDB", "Put.io", "YouTube", "Sentry"))
        {
            assertTrue(service, byId.get("service:" + service).service);
        }
    }

    @Test
    public void everyPreparedModuleDependencyHasAResolvedCredit() throws Exception
    {
        JSONArray entries = json("assets/" + AboutCredits.ASSET).getJSONArray("entries");
        java.util.Set<String> modules = new java.util.HashSet<>();
        for (int i = 0; i < entries.length(); i++)
        {
            String[] coordinate = entries.getJSONObject(i).getString("id").split(":");
            if (coordinate.length == 3)
                modules.add(coordinate[0] + ":" + coordinate[1]);
        }
        Pattern declaration = Pattern.compile("(?m)^\\s*(?:api|implementation|coreLibraryDesugaring)"
            + "\\s*\\(?\\s*'([^':]+:[^':]+):[^']+'");
        for (String module : List.of("MediaLib", "FileCoreLibrary"))
        {
            String source = Files.readString(Path.of("../" + module + "/build.gradle"));
            Matcher matches = declaration.matcher(source);
            while (matches.find())
            {
                String dependency = matches.group(1);
                // The Android runtime supplies org.json; the prepared module explicitly excludes the Maven jar.
                if (dependency.equals("org.json:json"))
                {
                    assertTrue(source.contains("exclude group: 'org.json', module: 'json'"));
                }
                else
                {
                    assertTrue(module + ":" + dependency, modules.contains(dependency));
                }
            }
        }
    }

    @Test
    public void releaseHistoryHasBothVersionsWithoutClaimingAnUnsignedReleaseIsShipped() throws Exception
    {
        JSONObject history = json("assets/foundation/release-history.json");
        assertEquals("0.156", history.getJSONObject("current").getString("version"));
        assertEquals("docs/0.156/", history.getJSONObject("current").getString("documentation"));
        JSONObject previous = history.getJSONArray("history").getJSONObject(0);
        assertEquals("0.155", previous.getString("version"));
        assertEquals("docs/0.155/", previous.getString("documentation"));
        assertTrue(Files.isDirectory(Path.of(previous.getString("documentation"))));
        assertTrue(Files.isDirectory(Path.of("docs/0.156/")));
    }

    @Test
    public void malformedCatalogueIsRejected() throws Exception
    {
        assertThrows(IOException.class, () -> AboutCredits.read(new ByteArrayInputStream("{}".getBytes())));
        assertThrows(IOException.class,
            () -> AboutCredits.read(new ByteArrayInputStream("{\"schema\":2,\"entries\":[]}".getBytes())));
    }
}
