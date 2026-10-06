package com.archos.mediacenter.video.streaming.putio;

import android.net.Uri;
import java.io.*;
import java.util.concurrent.TimeUnit;
import okhttp3.*;
import org.json.*;
import com.archos.mediacenter.video.diagnostics.Diagnostics;

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
        return request(url,"link_begin",response->parseCode(response,linkTemplate));
    }
    String poll(Code code)throws IOException{return request(HttpUrl.parse("https://api.put.io/v2/oauth2/oob/code").newBuilder().addPathSegment(code.code).build(),"link_poll",PutioOAuthClient::parseToken);}
    static Code parseCode(JSONObject response,String template)throws IOException{
        try{if(!"OK".equals(response.getString("status"))||!(response.opt("code") instanceof String))throw new JSONException("Invalid envelope");String code=response.getString("code");if(!code.matches("[!-~]{1,128}"))throw new JSONException("Invalid code");return new Code(code,template.replace("{code}",Uri.encode(code)));}
        catch(JSONException error){throw new IOException("Provider linking response unavailable");}
    }
    static String parseToken(JSONObject response)throws IOException{
        try{if(!"OK".equals(response.getString("status"))||!response.has("oauth_token"))throw new JSONException("Invalid envelope");if(response.isNull("oauth_token"))return null;if(!(response.opt("oauth_token") instanceof String))throw new JSONException("Invalid credential");String token=response.getString("oauth_token");if(!token.matches("[!-~]{1,4096}"))throw new JSONException("Invalid credential");return token;}
        catch(JSONException error){throw new IOException("Provider linking response unavailable");}
    }
    interface Parser<T>{T parse(JSONObject response)throws IOException;}
    private <T> T request(HttpUrl url,String kind,Parser<T> parser)throws IOException{
        String operation=Diagnostics.operation("putio_"+kind),outcome="transport_failed";
        long started=android.os.SystemClock.elapsedRealtime();int status=0;
        try(Response response=http.newCall(new Request.Builder().url(url).build()).execute()){
            status=response.code();
            if(!response.isSuccessful()){outcome="http_error";throw new IOException();}
            outcome="invalid_payload";if(response.body()==null)throw new IOException();
            outcome="transport_failed";
            ByteArrayOutputStream bytes=new ByteArrayOutputStream();byte[] buffer=new byte[4096];int count;InputStream input=response.body().byteStream();
            while((count=input.read(buffer))!=-1){if(Thread.currentThread().isInterrupted())throw new IOException();if(bytes.size()+count>65536){outcome="invalid_payload";throw new IOException();}bytes.write(buffer,0,count);}
            outcome="invalid_payload";T result=parser.parse(new JSONObject(bytes.toString("UTF-8")));outcome=result==null?"pending":"complete";return result;
        }catch(Exception failure){
            if(Thread.currentThread().isInterrupted())outcome="cancelled";
            else if(failure instanceof java.net.SocketTimeoutException)outcome="timeout";
            throw new IOException("Provider linking is unavailable or expired");
        }finally{
            Diagnostics.event("complete".equals(outcome)||"pending".equals(outcome)?"provider_request_complete":"provider_request_failed",
                    "operation_id",operation,"provider","putio","operation_type",kind,"status",status,"outcome",outcome,
                    "duration_ms",android.os.SystemClock.elapsedRealtime()-started,"retry_number",0,"connectivity",Diagnostics.connectivity());
            Diagnostics.finishOperation(operation,"putio_"+kind,started);
        }
    }
}
