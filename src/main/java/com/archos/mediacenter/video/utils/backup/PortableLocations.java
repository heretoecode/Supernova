package com.archos.mediacenter.video.utils.backup;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.*;
import org.json.*;

/** Optional locator text, positively validated before serialization. No account or access grants. */
public final class PortableLocations {
    private static final Set<String> SCHEMES=Set.of("file","smb","ftp","ftps","sftp","nfs","upnp","webdav","webdavs","dav","davs","http","https","content");
    public static String locator(String value)throws JSONException {
        try {
            if(value==null||value.isEmpty()||value.length()>8192||value.indexOf('\\')>=0||
                    value.chars().anyMatch(c->c<32||c==127))throw new IllegalArgumentException();
            StandardCharsets.UTF_8.newEncoder().encode(java.nio.CharBuffer.wrap(value));
            // Raw local paths are supported by the current scanner; URI encoding preserves their segments.
            URI uri=value.startsWith("/")?new URI("file","",value,null):new URI(value);
            String scheme=uri.getScheme()==null?null:uri.getScheme().toLowerCase(Locale.ROOT);
            String authority=uri.getAuthority(),path=uri.getPath();
            if(!SCHEMES.contains(scheme)||uri.isOpaque()||uri.getRawQuery()!=null||uri.getRawFragment()!=null||
                    uri.getUserInfo()!=null||authority!=null&&(authority.contains("@")||authority.indexOf('\\')>=0)||
                    path==null||!path.startsWith("/")||path.indexOf('\\')>=0||path.chars().anyMatch(c->c<32||c==127)||
                    uri.getRawPath().matches("(?i).*%2f.*")||uri.getPort()>65535||uri.getPort()==0)throw new IllegalArgumentException();
            if(scheme.equals("file")) {if(authority!=null&&!authority.isEmpty())throw new IllegalArgumentException();}
            else if(authority==null||authority.isEmpty()||uri.getHost()==null)throw new IllegalArgumentException();
            // Neither queries nor user information are removed: losing them could select different media.
            URI canonical=new URI(scheme,authority==null?"":authority.toLowerCase(Locale.ROOT),path,null,null).normalize();
            if(Arrays.asList(canonical.getPath().split("/")).contains(".."))throw new IllegalArgumentException();
            return canonical.toASCIIString();
        }catch(Exception invalid){throw new JSONException("Unsupported or credential-bearing location; reconnect it separately");}
    }
    public static String reference(String locator)throws JSONException {
        String canonical=locator(locator);try {
            StringBuilder hash=new StringBuilder("location:");for(byte part:MessageDigest.getInstance("SHA-256").digest(canonical.getBytes(StandardCharsets.UTF_8)))hash.append(String.format(Locale.ROOT,"%02x",part&255));return hash.toString();
        }catch(Exception unavailable){throw new JSONException("Location identity unavailable");}
    }
    public static void validateReference(String value)throws JSONException {
        if(value==null||!value.matches("location:[a-f0-9]{64}"))throw new JSONException("Invalid location identity");
    }
    public static JSONObject export(Collection<String> values)throws JSONException {
        Map<String,String> locations=new TreeMap<>();for(String value:values){String canonical=locator(value);locations.put(reference(canonical),canonical);}
        JSONArray rows=new JSONArray();for(var entry:locations.entrySet())rows.put(new JSONObject().put("ref",entry.getKey()).put("uri",entry.getValue()));
        JSONObject result=new JSONObject().put("schema",1).put("rows",rows);validate(result);return result;
    }
    public static Map<String,String> validate(JSONObject payload)throws JSONException {
        if(payload.length()!=2||!Integer.valueOf(1).equals(payload.get("schema")))throw new JSONException("Unsupported location schema");
        JSONArray rows=payload.getJSONArray("rows");if(rows.length()>10000)throw new JSONException("Too many saved locations");Map<String,String> result=new LinkedHashMap<>();
        for(int i=0;i<rows.length();i++) {
            JSONObject row=rows.getJSONObject(i);if(row.length()!=2||!(row.get("ref") instanceof String)||!(row.get("uri") instanceof String))throw new JSONException("Invalid location fields");
            String ref=row.getString("ref"),uri=row.getString("uri");validateReference(ref);
            if(!uri.equals(locator(uri))||!ref.equals(reference(uri))||result.putIfAbsent(ref,uri)!=null)throw new JSONException("Conflicting or invalid location identity");
        }return Collections.unmodifiableMap(result);
    }
    private PortableLocations(){}
}
