package com.archos.mediacenter.video.leanback.settings;

import android.content.Context;
import android.widget.*;
import com.archos.mediacenter.video.leanback.*;
import com.archos.mediacenter.video.utils.*;
import java.util.concurrent.*;

/** Direct account fields. Password entry uses the shared keyboard and privacy-masked diagnostics. */
final class InlineOpenSubtitles {
    private final Context context;private final LinearLayout target;private final TextView information;
    private String username,password;private boolean reveal;private int generation;
    private Future<?> work;
    private static final ExecutorService IO=new ThreadPoolExecutor(1,1,0,TimeUnit.SECONDS,new ArrayBlockingQueue<>(2),r->{Thread t=new Thread(r,"SupernovaSubtitleLogin");t.setDaemon(true);return t;},new ThreadPoolExecutor.DiscardOldestPolicy());
    InlineOpenSubtitles(Context c,LinearLayout target,TextView information){context=c;this.target=target;this.information=information;android.content.SharedPreferences prefs=c.getSharedPreferences("opensubtitles_credentials",0);username=prefs.getString(OpenSubtitlesCredentialsDialog.OPENSUBTITLES_USERNAME,"");password=prefs.getString(OpenSubtitlesCredentialsDialog.OPENSUBTITLES_PASSWORD,"");target.addOnAttachStateChangeListener(new android.view.View.OnAttachStateChangeListener(){public void onViewAttachedToWindow(android.view.View v){}public void onViewDetachedFromWindow(android.view.View v){generation++;if(work!=null)work.cancel(true);}});render();}
    private void row(String label,String tag,Runnable run){TextView view=SharedThreePanel.action(context,label,run);view.setTag(tag);target.addView(view);SharedThreePanel.divider(target);}
    void render(){target.removeAllViews();SharedThreePanel.heading(target,"OpenSubtitles");row("Username: "+username,"credential.username",()->PreviewTextInput.showValidated(context,"Username",username,128,value->value.isEmpty()?"Enter a username":null,value->{username=value;render();focus("credential.username");}));row("Password: "+(reveal?password:password.isEmpty()?"":"••••••"),"credential.password",()->PreviewTextInput.showSecret(context,"Password",password,value->{password=value;render();focus("credential.password");}));row("Show Password: "+(reveal?"On":"Off"),"credential.reveal",()->{reveal=!reveal;render();focus("credential.reveal");});row("Save & Sign In","credential.save",this::save);information.setText("Use your OpenSubtitles account to download available subtitles. Passwords are stored privately on this device and excluded from backups and diagnostics.");}
    private void focus(String tag){target.post(()->{android.view.View view=target.findViewWithTag(tag);if(view!=null)view.requestFocus();});}
    private void save(){if(username.isEmpty()||password.isEmpty()){information.setText("Enter your username and password before signing in.");return;}int token=++generation;String user=username,secret=password;information.setText("Signing in…");if(work!=null)work.cancel(true);work=IO.submit(()->{boolean valid=false;try{valid=OpenSubtitlesApiHelper.login(context.getString(com.archos.mediacenter.video.R.string.opensubtitles_api_key),user,secret);}catch(java.io.IOException failure){/* Never retain exception bodies or credential-bearing URLs. */}final boolean accepted=valid;target.post(()->{if(token!=generation||!target.isAttachedToWindow()||!target.isShown())return;if(accepted){context.getSharedPreferences("opensubtitles_credentials",0).edit().putString(OpenSubtitlesCredentialsDialog.OPENSUBTITLES_USERNAME,user).putString(OpenSubtitlesCredentialsDialog.OPENSUBTITLES_PASSWORD,secret).apply();OpenSubtitlesApiHelper.persistStatus(context,OpenSubtitlesApiHelper.OS_STATUS_OK);information.setText("Signed in to OpenSubtitles.");}else information.setText("Sign-in failed. Check the account details and connection. Previously saved credentials have been kept.");});});}
}
