package com.archos.mediacenter.video.diagnostics;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/** Android document picker grants access only to the destination selected by the user. */
public final class DiagnosticExportActivity extends Activity {
    @Override protected void onCreate(Bundle state){
        super.onCreate(state);
        if(state!=null)return;
        Intent create=new Intent(Intent.ACTION_CREATE_DOCUMENT).addCategory(Intent.CATEGORY_OPENABLE)
            .setType("application/zip").putExtra(Intent.EXTRA_TITLE,"Supernova Diagnostics - "+new SimpleDateFormat("yyyy-MM-dd HH-mm-ss-SSS",Locale.ROOT).format(new Date())+".zip");
        try{startActivityForResult(create,1);}catch(ActivityNotFoundException unavailable){Toast.makeText(this,"No document picker is installed. Install or enable a file manager with document support.",Toast.LENGTH_LONG).show();finish();}
    }
    @Override protected void onActivityResult(int request,int result,Intent data){
        super.onActivityResult(request,result,data);
        if(request!=1||result!=RESULT_OK||data==null||data.getData()==null){finish();return;}
        Uri target=data.getData();
        new Thread(()->{
            boolean success=false;
            try(OutputStream out=getContentResolver().openOutputStream(target,"w")){
                if(out==null)throw new IOException("No destination stream");
                Diagnostics.export(getApplicationContext(),out);success=true;
            }catch(Exception failure){Diagnostics.error("diagnostic_export_failed",failure);}
            final boolean done=success;runOnUiThread(()->{Toast.makeText(this,done?"Diagnostic report saved":"Report could not be saved; check the selected destination.",Toast.LENGTH_LONG).show();finish();});
        },"DiagnosticExport").start();
    }
}
