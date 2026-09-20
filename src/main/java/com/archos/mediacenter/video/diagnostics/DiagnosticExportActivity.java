package com.archos.mediacenter.video.diagnostics;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

/** Android document picker grants access only to the destination selected by the user. */
public final class DiagnosticExportActivity extends Activity {
    @Override protected void onCreate(Bundle state){
        super.onCreate(state);
        if(state!=null)return;
        Intent create=new Intent(Intent.ACTION_CREATE_DOCUMENT).addCategory(Intent.CATEGORY_OPENABLE)
            .setType("application/zip").putExtra(Intent.EXTRA_TITLE,"Supernova-Diagnostics-"+System.currentTimeMillis()+".zip");
        try{startActivityForResult(create,1);}catch(android.content.ActivityNotFoundException unavailable){Toast.makeText(this,"No document picker is installed. Install or enable a file manager with document support.",Toast.LENGTH_LONG).show();finish();}
    }
    @Override protected void onActivityResult(int request,int result,Intent data){
        super.onActivityResult(request,result,data);
        if(request!=1||result!=RESULT_OK||data==null||data.getData()==null){finish();return;}
        android.net.Uri target=data.getData();
        new Thread(()->{
            boolean success=false;
            try(java.io.OutputStream out=getContentResolver().openOutputStream(target,"w")){
                if(out==null)throw new java.io.IOException("No destination stream");
                Diagnostics.export(getApplicationContext(),out);success=true;
            }catch(Exception failure){Diagnostics.error("diagnostic_export_failed",failure);}
            final boolean done=success;runOnUiThread(()->{Toast.makeText(this,done?"Diagnostic report saved":"Report could not be saved; check the selected destination.",Toast.LENGTH_LONG).show();finish();});
        },"DiagnosticExport").start();
    }
}
