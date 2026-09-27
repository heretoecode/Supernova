package com.archos.mediacenter.video.streaming.putio;

import android.net.Uri;
import java.io.*;
import java.util.concurrent.TimeUnit;
import okhttp3.*;
import org.json.*;

/** Provider-documented OOB endpoints. No password flow and no guessed production client. */
final class PutioOAuthClient {
    static final class Code {final String code,link;Code(String code,String link){this.code=code;this.link=link;}}
    private final String clientId,linkTemplate;
    private final OkHttpClient http=new OkHttpClient.Builder().followRedirects(false).followSslRedirects(false).connectTimeout(10,TimeUnit.SECONDS).readTimeout(20,TimeUnit.SECONDS).callTimeout(30,TimeUnit.SECONDS).build();
    PutioOAuthClient(String clientId,String linkTemplate){if(!configured(clientId,linkTemplate))throw new IllegalArgumentException("Registered provider configuration is required");this.clientId=clientId;this.linkTemplate=linkTemplate;}
    static boolean configured(String clientId,String template){
        if(clientId==null||!clientId.matches("[1-9][0-9]{0,17}")||template==null||template.matches(".*[\\x00-\\x20].*")||template.indexOf("{code}")<0||template.indexOf("{code}")!=template.lastIndexOf("{code}"))return false;
        Uri link=Uri.parse(template.replace("{code}","CODE"));String host=link.getHost();
        return "https".equals(link.getScheme())&&("put.io".equals(host)||"app.put.io".equals(host))&&link.getUserInfo()==null&&link.getPort()==-1&&link.getFragment()==null&&!template.toLowerCase(java.util.Locale.ROOT).matches(".*(token|password|secret).*" );
    }
    Code begin()throws IOException{
        HttpUrl url=HttpUrl.parse("https://api.put.io/v2/oauth2/oob/code").newBuilder().addQueryParameter("app_id",clientId).addQueryParameter("client_name","Supernova Android TV").build();
        return parseCode(request(url),linkTemplate);
    }
    String poll(Code code)throws IOException{return parseToken(request(HttpUrl.parse("https://api.put.io/v2/oauth2/oob/code").newBuilder().addPathSegment(code.code).build()));}
    static Code parseCode(JSONObject response,String template)throws IOException{
        try{if(!"OK".equals(response.getString("status"))||!(response.opt("code") instanceof String))throw new JSONException("Invalid envelope");String code=response.getString("code");if(!code.matches("[!-~]{1,128}"))throw new JSONException("Invalid code");return new Code(code,template.replace("{code}",Uri.encode(code)));}
        catch(JSONException error){throw new IOException("Provider linking response unavailable");}
    }
    static String parseToken(JSONObject response)throws IOException{
        try{if(!"OK".equals(response.getString("status"))||!response.has("oauth_token"))throw new JSONException("Invalid envelope");if(response.isNull("oauth_token"))return null;if(!(response.opt("oauth_token") instanceof String))throw new JSONException("Invalid credential");String token=response.getString("oauth_token");if(!token.matches("[!-~]{1,4096}"))throw new JSONException("Invalid credential");return token;}
        catch(JSONException error){throw new IOException("Provider linking response unavailable");}
    }
    private JSONObject request(HttpUrl url)throws IOException{
        try(Response response=http.newCall(new Request.Builder().url(url).build()).execute()){
            if(!response.isSuccessful()||response.body()==null)throw new IOException();
            ByteArrayOutputStream bytes=new ByteArrayOutputStream();byte[] buffer=new byte[4096];int count;InputStream input=response.body().byteStream();
            while((count=input.read(buffer))!=-1){if(Thread.currentThread().isInterrupted()||bytes.size()+count>65536)throw new IOException();bytes.write(buffer,0,count);}
            return new JSONObject(bytes.toString("UTF-8"));
        }catch(Exception failure){throw new IOException("Provider linking is unavailable or expired");}
    }
}
